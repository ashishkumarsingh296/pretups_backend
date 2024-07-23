package com.web.pretups.channel.reports.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.validator.ValidatorException;
//import org.apache.struts.action.ActionForward;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLCommonController;
import com.btsl.common.CommonValidator;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.reports.businesslogic.ChannelUserOperatorUserRolesDAO;
import com.btsl.pretups.channel.reports.businesslogic.ChannelUserReportDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelUserOperatorUserRolesVO;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.pretups.common.DownloadCSVReports;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.reports.web.UsersReportModel;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;

/**
 * @author mohit.miglani
 *
 */
@Service("ChannelUserOperatorUserRolesService")
public class ChannelUserOperatorUserRolesServiceImpl implements
		ChannelUserOperatorUserRolesService {
	public static final Log _log = LogFactory
			.getLog(ChannelUserOperatorUserRolesServiceImpl.class.getName());
	public static final String LIST_ALL="list.all";
	private static final String FAIL_KEY = "fail";
	@Override
	public void loadO2cUserRoles(UsersReportModel usersReportModel,
			HttpServletRequest request, HttpServletResponse response,
			UserVO userVO) {
		BTSLCommonController btslCommonController = new BTSLCommonController();
		
		if (_log.isDebugEnabled()) {
			_log.debug("loadO2cUserRoles", "Entered");
		}
		

		Connection con = null;
		MComConnectionI mcomCon = null;

		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			final DomainDAO domainDAO = new DomainDAO();

			ListValueVO listValueVO = null;
			final ArrayList<?> loggedInUserDomainList = new ArrayList<>();
			ArrayList<?> domainList = new ArrayList<>();
			domainList = domainDAO.loadDomainDetails(con);
			ArrayList<?> g = getdomlist(domainList);

			final CategoryWebDAO categoryWebDAO = new CategoryWebDAO();

			if (TypesI.SUPER_CHANNEL_ADMIN.equals(userVO.getCategoryCode()))
				usersReportModel.setZoneList(new GeographicalDomainDAO()
						.loadUserGeographyList(con, userVO.getUserID(),
								userVO.getNetworkID()));
			else

				usersReportModel.setZoneList(userVO.getGeographicalAreaList());
			usersReportModel.setUserType(userVO.getUserType());
			

			usersReportModel.setDomainList(domainList);

			BTSLCommonController ob = new BTSLCommonController();
			ob.commonUserList(usersReportModel, loggedInUserDomainList, userVO);

			ArrayList<String> dom = new ArrayList<>();
			for (int i = 0, j = g.size(); i < j; i++) {
				listValueVO = (ListValueVO) g.get(i);
				dom.add(listValueVO.getValue());
			}
			StringBuilder sb = new StringBuilder();
			
			for (String s : dom) {
				if (!("OPT").equals(s)) {
				sb.append("'" + s + "'");
				
					sb.append(",");
				}
			}

			usersReportModel.setDomainListString(sb.toString());

			usersReportModel.setDomainList(g);
			btslCommonController.commonUserList(usersReportModel,
					loggedInUserDomainList, userVO);

			if (!("OPERATOR".equals(userVO.getUserType()))) {
				final int loginSeqNo = userVO.getCategoryVO()
						.getSequenceNumber();
				usersReportModel.setParentCategoryList(categoryWebDAO
						.loadCategoryReporSeqtList(con, loginSeqNo));
			} else {
				usersReportModel.setParentCategoryList(categoryWebDAO
						.loadCategoryVOList(con));
			}
			usersReportModel.setLoginUserID(userVO.getUserID());
			usersReportModel.setLoggedInUserCategoryCode(userVO.getCategoryVO()
					.getCategoryCode());
			checkstaff(usersReportModel, userVO);
			usersReportModel.setUserStatusList(LookupsCache.loadLookupDropDown(
					PretupsI.USER_STATUS_TYPE, true));
			usersReportModel.setLoginUserID(userVO.getUserID());
			listValueVO = null;

			btslCommonController.commonGeographicDetails(usersReportModel,
					listValueVO);
			usersReportModel.setZoneListSize(usersReportModel.getZoneList()
					.size());

		} catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.debug("loadO2cUserRoles", "" + e);
			}
		} finally {
			if (mcomCon != null) {
				mcomCon.close("ChannelUserOperatorUserRolesAction#loadO2cUserRoles");
				mcomCon = null;
			}
			
		}
		

	}
	private void checkstaff(UsersReportModel usersReportModel, UserVO userVO) {
		if (userVO.isStaffUser()) {
			usersReportModel.setLoggedInUserName(userVO.getParentName());
		} else {
			usersReportModel.setLoggedInUserName(userVO.getUserName());
		}
	}
	private ArrayList<?> getdomlist(ArrayList<?> domainList) {
		ListValueVO listValueVO;
		ArrayList<Object> g = new ArrayList<>();
		@SuppressWarnings("unchecked")
		ArrayList<DomainVO> b = (ArrayList<DomainVO>) domainList;
		for (int i = 0; i < b.size(); i++) {
			DomainVO arr = b.get(i);
			String c = arr.getDomainCode();
			String d = arr.getDomainName();
			listValueVO = new ListValueVO(d, c);
			g.add(listValueVO);
		}
		return g;
	}
@Override
	public boolean displayChannelUserRolesReport(
			UsersReportModel usersReportModel, HttpServletRequest request,
			HttpServletResponse response, UserVO userVO, ChannelUserVO userVO1,
			BindingResult bindingResult) {

		final String methodName = "displayChannelUserRolesReport";
		
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered");
		}
		//ActionForward forward = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		
		
		

		CommonValidator commonValidator = new CommonValidator(
				"configfiles/c2sreports/validator-ExternalUserReports.xml",
				usersReportModel, "ExternalUserReportsValidation");
		Map<String, String> errorMessages;
		try {
			errorMessages = commonValidator.validateModel();
			PretupsRestUtil pru = new PretupsRestUtil();
			pru.processFieldError(errorMessages, bindingResult);
			request.getSession().setAttribute("usersReportModel",
					usersReportModel);
		} catch (ValidatorException | IOException | SAXException e) {

			_log.errorTrace(methodName, e);
		}

		if (bindingResult.hasFieldErrors()) {

			return false;
		}

		if (request.getParameter("initiatesummary") != null) {
			try {

				

				usersReportModel.setNetworkCode(userVO.getNetworkID());
				usersReportModel.setNetworkName(userVO.getNetworkName());
				usersReportModel.setReportHeaderName(userVO
						.getReportHeaderName());

				commonDomainDetails(usersReportModel);

				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				final ChannelUserReportDAO channelUserDAO = new ChannelUserReportDAO();
Model model = null;
				conditionfordisplay(usersReportModel, userVO, con,
					 channelUserDAO,model);

				ChannelUserOperatorUserRolesDAO channelUserOperatorUserRolesDAO = new ChannelUserOperatorUserRolesDAO();
				

				checkusertype(usersReportModel, userVO, con,
						channelUserOperatorUserRolesDAO);

			} catch (Exception e) {
				_log.errorTrace(methodName, e);

			} finally {
				if (mcomCon != null) {
					mcomCon.close("ChannelUserOperatorUserRolesAction#displayChannelUserRolesReport");
					mcomCon = null;
				}
				/*if (_log.isDebugEnabled()) {
					_log.debug(methodName, "Exiting:forward=" + forward);
				}*/
			}
		}
		return true;
	}
private void checkusertype(UsersReportModel usersReportModel, UserVO userVO,
		Connection con,
		ChannelUserOperatorUserRolesDAO channelUserOperatorUserRolesDAO)
		throws SQLException {
	String rptCode;
	if (userVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE)) {
		rptCode = "C2SSWCHUS01";
		usersReportModel.setrptCode(rptCode);
		List<ChannelUserOperatorUserRolesVO> externalUserReportList = channelUserOperatorUserRolesDAO
				.loadExternalUserRolesReport(con, usersReportModel);
		usersReportModel
				.setExternalUserReportList(externalUserReportList);
	}
	if (TypesI.CHANNEL_USER_TYPE.equalsIgnoreCase(usersReportModel
			.getUserType())) {
		rptCode = "C2SSWCHUS02";
		usersReportModel.setrptCode(rptCode);
		List<ChannelUserOperatorUserRolesVO> externalUserReportList = channelUserOperatorUserRolesDAO
				.loadExternalUserRolesChannelReport(con,
						usersReportModel);
		usersReportModel
				.setExternalUserReportList(externalUserReportList);
	}
}
private void conditionfordisplay(UsersReportModel usersReportModel,
		UserVO userVO, Connection con, 
		 final ChannelUserReportDAO channelUserDAO,Model model )
		throws BTSLBaseException {
	ArrayList<?> userList ;
	ListValueVO listValueVO;
	String user = usersReportModel.getUserName();
    extractusername(usersReportModel, user);
	if (usersReportModel.getParentCategoryCode().equals(TypesI.ALL)) {

		userList = channelUserDAO
				.loadUserListBasisOfZoneDomainCategory(con,
						PretupsI.ALL,
						usersReportModel.getDomainCode(),
						usersReportModel.getZoneCode(),
						usersReportModel.getUserName() + "%",
						userVO.getUserID());

	} else {
		getParentCategoryCode(usersReportModel );
		String[] arr ;
		if (!("OPERATOR".equals(userVO.getUserType()))) {
			arr = usersReportModel.getParentCategoryCode().split(
					"\\|");
		} else {
			arr = usersReportModel.getParentCategoryCode().split(
					":");
		}
		usersReportModel.setParentCategoryCode(arr[1]);
		userList = channelUserDAO.loadUserListBasisOfZoneDomainCategory(con, arr[1], usersReportModel.getDomainCode(), usersReportModel.getZoneCode(),
                usersReportModel.getUserName() + "%", userVO.getUserID());
	}

	if (usersReportModel.getUserStatus().equals(TypesI.ALL)) {

		usersReportModel.setUserStatusName(PretupsRestUtil
				.getMessageString(LIST_ALL));
		usersReportModel.setStatus(PretupsRestUtil
				.getMessageString(LIST_ALL));
	} else {
		listValueVO = BTSLUtil.getOptionDesc(
				usersReportModel.getUserStatus(),
				usersReportModel.getUserStatusList());
		usersReportModel.setUserStatusName(listValueVO.getLabel());
	}
	if ("ALL".equals(usersReportModel.getUserName())) {
		usersReportModel.setUserID(PretupsI.ALL);

	} else if (userList == null || userList.isEmpty()) {
		
        model.addAttribute(
                FAIL_KEY,
                PretupsRestUtil.getMessageString("c2s.reports.searchuserstatuswiseextusers.error.usernotexist"));

		
		
	
	} else if (userList.size() == 1) {
		final ChannelUserTransferVO channelUserTransferVO = (ChannelUserTransferVO) userList
				.get(0);
		usersReportModel.setUserName(channelUserTransferVO
				.getUserName());

	} else if (userList.size() > 1&&!BTSLUtil.isNullString(usersReportModel.getUserID())) {
		/*
		 * This is the case when userList size greater than 1 if
		 * user click the submit button(selectcategoryForEdit.jsp)
		 * after performing search through searchUser and select one
		 * form the shown list at that time we set the userid on the
		 * form(becs two user have the same name but different id)
		 * so here we check the userId is null or not it is not null
		 * iterate the list and open the screen in edit mode
		 * corresponding to the userid
		 */
		
		
			channelTransferLoop( userList, usersReportModel);
		

		

	}
}
private void extractusername(UsersReportModel usersReportModel, String user) {
	try{
    if(!("ALL").equals(user))
    {
    String[] parts = user.split("\\(");
	String userName = parts[0]; 
	usersReportModel.setUserName(userName);
	String a = parts[1];
	String[] w1=a.split("\\)");
	usersReportModel.setUserID(w1[0]);
    }
    }
    catch(Exception e)
    {
    	_log.error("methodName", "Name selected did not had ID "
				+ e);
		_log.errorTrace("methodName", e);
    }
}

	/**
	 * @param usersReportModel
	 */
	public void commonDomainDetails(UsersReportModel usersReportModel) {

		if (usersReportModel.getDomainCode().equals(PretupsI.ALL)) {
			usersReportModel.setDomainName(PretupsRestUtil
					.getMessageString("list.all"));
			
		} else {

			int i;
			int j;
			for (i = 0, j = usersReportModel.getDomainList().size(); i < j; i++) {
				if (usersReportModel.getDomainList().get(i).getClass().equals(DomainVO.class))

				{
					DomainVO domainVO = (DomainVO) usersReportModel
							.getDomainList().get(i);
					domname(usersReportModel, domainVO);

				} else if (usersReportModel.getDomainList().get(i).getClass().equals(ListValueVO.class)) {

					ListValueVO listVO = (ListValueVO) usersReportModel
							.getDomainList().get(i);
					domn(usersReportModel, listVO);
				} else {
					usersReportModel.setDomainName(usersReportModel
							.getDomainName());

				}
			}

		}

	}
	private void domn(UsersReportModel usersReportModel, ListValueVO listVO) {
		if (listVO.getValue().equals(
				usersReportModel.getDomainCode())) {
			usersReportModel.setDomainName(listVO.getLabel());

		}
	}

	private void domname(UsersReportModel usersReportModel, DomainVO domainVO) {
		if (domainVO.getDomainCode().equals(
				usersReportModel.getDomainCode())) {
			usersReportModel
					.setDomainName(domainVO.getDomainName());

		}
	}

	@Override
	public List<?> loadUserList(UserVO userVO, String parentCategoryCode,
			String domainList, String zoneList, String userName) {

		final String methodName = "loadUserList";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED);
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelUserReportDAO channelUserDAO = null;
		ArrayList<?> userList = new ArrayList<>();
		try {

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			channelUserDAO = new ChannelUserReportDAO();

			 String[] arr = parentCategoryCode.split("\\|");
			 String[] arr1 = parentCategoryCode.split("\\:");

			if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
				if (parentCategoryCode.equals(PretupsI.ALL)) {
					userList = channelUserDAO
							.loadUserListBasisOfZoneDomainCategoryHierarchy(
									con, PretupsI.ALL, domainList, zoneList,
									userName, userVO.getUserID());
				} else {
					userList = channelUserDAO
							.loadUserListBasisOfZoneDomainCategoryHierarchy(
									con, arr[1], domainList, zoneList,
									userName, userVO.getUserID());
				}
			} else {
				if (parentCategoryCode.equals(PretupsI.ALL)) {
					userList = channelUserDAO
							.loadUserListBasisOfZoneDomainCategory(con,
									PretupsI.ALL, domainList, zoneList,
									userName, userVO.getUserID());
				} else {
					userList = channelUserDAO
							.loadUserListBasisOfZoneDomainCategory(con, arr1[1],
									domainList, zoneList, userName,
									userVO.getUserID());
				}
			}

		} catch (Exception e) {
			_log.error(methodName, "Exceptin:e=" + e);
			_log.errorTrace(methodName, e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("UserClosingBalanceServiceImpl#loadUserList");
				mcomCon = null;
			}

		}

		return userList;
	}

	@Override
	public String downloadFileforSumm(UsersReportModel usersReportModel)
			throws InterruptedException, BTSLBaseException, SQLException {

		DownloadCSVReports downloadCSVReports = new DownloadCSVReports();
		Connection con;
		MComConnectionI mcomCon;
		mcomCon = new MComConnection();
		con = mcomCon.getConnection();
		String rptCode = usersReportModel.getrptCode();
		String filePath;

		filePath = downloadCSVReports.prepareData(usersReportModel, rptCode,
				con);
		return filePath;

	}
	
	
	/**
	 * @param usersReportModel
	 * @param userVO
	 * @param channelUserDAO
	 * @param userList
	 * @param con
	 * @throws BTSLBaseException
	 */
	public void getParentCategoryCode(UsersReportModel usersReportModel ) throws BTSLBaseException
	{
		
		
		
		ListValueVO listValueVO;
		listValueVO = BTSLUtil.getOptionDesc(
				usersReportModel.getParentCategoryCode(),
				usersReportModel.getParentCategoryList());
		usersReportModel.setCategoryName(listValueVO.getLabel());

		
		
		
		 
	}
	
	
	/**
	 * @param userList
	 * @param usersReportModel
	 */
	public void channelTransferLoop(List<?> userList,UsersReportModel usersReportModel)
	{
		
	
	for (int i = 0, j = userList.size(); i < j; i++) {
		final ChannelUserTransferVO channelUserTransferVO = (ChannelUserTransferVO) userList
				.get(i);
		if (usersReportModel.getUserID().equals(
				channelUserTransferVO.getUserID())) {
			usersReportModel
					.setUserName(channelUserTransferVO
							.getUserName());
			

			break;
		}
	}
	
	}
}


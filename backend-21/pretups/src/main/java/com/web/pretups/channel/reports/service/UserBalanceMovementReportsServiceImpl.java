package com.web.pretups.channel.reports.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

////import org.apache.struts.action.ActionForward;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLCommonController;
import com.btsl.common.BTSLMessages;
import com.btsl.common.CommonValidator;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.reports.businesslogic.ChannelUserReportDAO;
import com.btsl.pretups.channel.reports.businesslogic.UserDailyBalanceMovementRptDAO;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.pretups.common.DownloadCSVReports;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.reports.web.UsersReportModel;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

/**
 * 
 * @author rahul.arya
 *
 */
@Service("UserBalanceMovementReportsService")
public class UserBalanceMovementReportsServiceImpl implements
		UserBalanceMovementReportsService {

	public static final Log log = LogFactory
			.getLog(UserBalanceMovementReportsServiceImpl.class.getName());
	private static final String FAIL_KEY = "fail";

	private static final String PANEL_NO = "PanelNo";
    
	private static final String INETReport_MSISDN="iNETReportMSISDN";
	
	private static final String PRETUPS_ALL="pretups.list.all";
	@Override
	public Boolean loadUserBalMovementSummaryReportPage(
			HttpServletRequest request, UserVO userVO,
			UsersReportModel usersReportModel, Model model) throws IOException {
		final String methodName = "UserBalanceMovementReportsServiceImpl#loadUserBalMovementSummaryReportPage";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}

		final ArrayList<Object> loggedInUserDomainList = new ArrayList<>();
		Connection con = null;
		MComConnectionI mcomCon = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			BTSLCommonController btslCommonController = new BTSLCommonController();
			ListValueVO listValueVO = null;
			btslCommonController.loadingSummaryDetails(usersReportModel,
					request, con, loggedInUserDomainList, listValueVO);
			final ServicesTypeDAO servicesTypeDAO = new ServicesTypeDAO();
			usersReportModel
					.setServiceTypeList(servicesTypeDAO
							.loadServicesListForReconciliation(con,
									PretupsI.C2S_MODULE));
			if (usersReportModel.getServiceTypeList().size() == 1) {
				listValueVO = (ListValueVO) usersReportModel
						.getServiceTypeList().get(0);
				usersReportModel.setServiceType(listValueVO.getValue());
				usersReportModel.setServiceTypeName(listValueVO.getLabel());
			}
		} catch (BTSLBaseException | SQLException e) {
			log.errorTrace(methodName, e);
			model.addAttribute(FAIL_KEY,
					PretupsRestUtil.getMessageString(e.getMessage()));
			return false;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("UserBalanceMovementReportsServiceImpl#loadUserBalMovementSummaryReportPage");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}
		return true;
	}

	/**
	 * 
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<ChannelUserTransferVO> loadFromUserList(
			UsersReportModel usersReportModel, UserVO userVO, String zoneCode,
			String domainCode, String fromTransferCategorycode,
			String userName, HttpServletRequest request)
			throws BTSLBaseException {

		final String methodName = "loadUserList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		//ActionForward forward = null;
		ChannelUserReportDAO channelUserDAO = null;

		ArrayList<ChannelUserTransferVO> userList = new ArrayList();
		try {

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			channelUserDAO = new ChannelUserReportDAO();

			String[] arr = fromTransferCategorycode.split("\\|");

			if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
				if (fromTransferCategorycode.equals(PretupsI.ALL)) {
					userList = channelUserDAO
							.loadUserListBasisOfZoneDomainCategoryHierarchy(
									con, PretupsI.ALL, domainCode, zoneCode,
									userName, usersReportModel.getLoginUserID());
				} else {
					userList = channelUserDAO
							.loadUserListBasisOfZoneDomainCategoryHierarchy(
									con, arr[1], domainCode, zoneCode,
									userName, usersReportModel.getLoginUserID());
				}
			} else {
				if (fromTransferCategorycode.equals(PretupsI.ALL)) {
					userList = channelUserDAO
							.loadUserListBasisOfZoneDomainCategory(con,
									PretupsI.ALL, domainCode, zoneCode,
									userName, usersReportModel.getLoginUserID());
				} else {
					userList = channelUserDAO
							.loadUserListBasisOfZoneDomainCategory(con, arr[1],
									domainCode, zoneCode, userName,
									usersReportModel.getLoginUserID());
				}
			}
			usersReportModel.setUserList(userList);
			usersReportModel.setUserListSize(userList);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("BTSLDispatchAction#commonlistUsers");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				//log.debug(methodName, "Exiting:forward=" + forward);
			}
		}
		return userList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.btsl.pretups.channel.reports.service.UserBalanceMovementReportsService
	 * #displayUserBalanceReport(jakarta.servlet.http.HttpServletRequest,
	 * jakarta.servlet.http.HttpServletResponse,
	 * com.btsl.pretups.channel.reports.web.UsersReportModel,
	 * com.btsl.user.businesslogic.UserVO, org.springframework.ui.Model,
	 * org.springframework.validation.BindingResult)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean displayUserBalanceReport(HttpServletRequest request,
			HttpServletResponse response, UsersReportModel usersReportModel,
			UserVO userVO, Model model, BindingResult bindingResult) {
		final String methodName = "displayUserBalanceReport";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
		}
		Boolean bool = true;
		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList userList = null;
		String tempCatCode = "";
		ChannelUserWebDAO channelUserWebDAO = null;
		String rptCode = null;
		BTSLCommonController btslCommonController = new BTSLCommonController();
		try {

			if (request.getParameter("submitButtonForMsisdn") != null
					|| request.getParameter(INETReport_MSISDN) != null) {
				CommonValidator commonValidator = new CommonValidator(
						"configfiles/c2sreports/validator-UserBalanceMovSum.xml",
						usersReportModel, "UserBalMovMSISDN");
				Map<String, String> errorMessages = commonValidator
						.validateModel();
				PretupsRestUtil pru = new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult);
				model.addAttribute(PANEL_NO, "Panel-One");
				request.getSession().setAttribute(PANEL_NO, "Panel-One");
			}

			if (request.getParameter("submitButtonForUserName") != null
					|| request.getParameter("iNETReportUserName") != null) {
				CommonValidator commonValidator = new CommonValidator(
						"configfiles/c2sreports/validator-UserBalanceMovSum.xml",
						usersReportModel, "UserBalMovUserName");
				Map<String, String> errorMessages = commonValidator
						.validateModel();
				PretupsRestUtil pru = new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult);
				model.addAttribute(PANEL_NO, "Panel-Two");
				request.getSession().setAttribute(PANEL_NO, "Panel-Two");
			}
			if (bindingResult.hasFieldErrors()) {
				request.getSession().setAttribute(
						"userDailyBalanceMovementReport", usersReportModel);
				return false;
			}
			Date fromd = BTSLUtil.getDateFromDateString(usersReportModel
					.getFromDate());

			Date tod = BTSLUtil.getDateFromDateString(usersReportModel
					.getToDate());

			int diff = BTSLUtil.getDifferenceInUtilDates(fromd, tod);
			if (diff > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CRYSTAL_REPORT_MAX_DATEDIFF))).intValue()) {
				model.addAttribute(
						FAIL_KEY,
						PretupsRestUtil
								.getMessageString(
										"btsl.date.error.datecompare",
										new String[] { String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CRYSTAL_REPORT_MAX_DATEDIFF))).intValue()) }));
				request.getSession().setAttribute(
						"userDailyBalanceMovementReport", usersReportModel);
				return false;
			}
			String user = usersReportModel.getUserName();
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
	            	log.error(methodName, "Name selected did not had ID "
							+ e);
					log.errorTrace(methodName, e);
	            }
			channelUserWebDAO = new ChannelUserWebDAO();
			String loadUserBalance = "loadUserBalance";
			usersReportModel.setNetworkCode(userVO.getNetworkID());
			usersReportModel.setNetworkName(userVO.getNetworkName());
			usersReportModel.setReportHeaderName(userVO.getReportHeaderName());
			ListValueVO listValueVO = null;
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			String[] ar = null;
			if (BTSLUtil.isNullString(usersReportModel.getMsisdn())
					&& BTSLUtil.isNullString(usersReportModel.getZoneCode())
					&& (BTSLUtil.isNullString(usersReportModel.getDomainCode())
							&& BTSLUtil.isNullString(usersReportModel
									.getParentCategoryCode()) && BTSLUtil
								.isNullString(usersReportModel.getUserName()))) {
				bool = true;
				throw new BTSLBaseException(
						this,
						methodName,
						"c2s.reports.web.userreportform.error.msg.required.value",
						0, ar, loadUserBalance);
			} else if (BTSLUtil.isNullString(usersReportModel.getMsisdn())
					&& BTSLUtil.isNullString(usersReportModel.getDomainCode())) {
				throw new BTSLBaseException(
						this,
						methodName,
						"c2s.reports.web.userreportform.error.msg.required.values",
						0, ar, loadUserBalance);
			} else if (BTSLUtil.isNullString(usersReportModel.getMsisdn())) {
				if (BTSLUtil.isNullString(usersReportModel.getZoneCode())) {
					throw new BTSLBaseException(
							this,
							methodName,
							"c2s.reports.web.userreportform.error.msg.required.zone",
							0, ar, loadUserBalance);
				}
				if (BTSLUtil.isNullString(usersReportModel.getDomainCode())) {
					throw new BTSLBaseException(
							this,
							methodName,
							"c2s.reports.web.userreportform.error.msg.required.domain",
							0, ar, loadUserBalance);
				}
				if (BTSLUtil.isNullString(usersReportModel
						.getParentCategoryCode())) {
					throw new BTSLBaseException(
							this,
							methodName,
							"c2s.reports.web.userreportform.error.msg.required.category",
							0, ar, loadUserBalance);
				}
				if (BTSLUtil.isNullString(usersReportModel.getUserName())) {
					throw new BTSLBaseException(
							this,
							methodName,
							"c2s.reports.web.userreportform.error.msg.required.user",
							0, ar, loadUserBalance);
				}
			}
			if (!BTSLUtil.isNullString(usersReportModel.getMsisdn())) {
				// check for valid msisdn
				if (!BTSLUtil.isValidMSISDN(usersReportModel.getMsisdn())) {
					ar = new String[1];
					ar[0] = usersReportModel.getMsisdn();
					throw new BTSLBaseException(this, methodName,
							"btsl.msisdn.error.length", 0, ar, loadUserBalance);
				}
			}
			if (!BTSLUtil.isNullString(usersReportModel.getMsisdn()))// MSISDN
																		// based
			// report
			{
				// check if the msisdn belongs to login user network
				final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache
						.getObject(PretupsBL.getMSISDNPrefix(PretupsBL
								.getFilteredMSISDN(usersReportModel.getMsisdn())));
				if (prefixVO == null
						|| !prefixVO.getNetworkCode().equals(
								usersReportModel.getNetworkCode())) {
					final String []arr1 = { usersReportModel.getMsisdn(),
							usersReportModel.getNetworkName() };
					log.error(methodName, "Error: MSISDN Number"
							+ usersReportModel.getMsisdn() + " not belongs to "
							+ usersReportModel.getNetworkName() + "network");
					throw new BTSLBaseException(this, methodName,
							"c2s.reports.error.msisdnnotinsamenetwork", 0,
							arr1, loadUserBalance);
				}

				final String status = "'" + PretupsI.USER_STATUS_NEW + "','"
						+ PretupsI.USER_STATUS_CANCELED + "','"
						+ PretupsI.USER_STATUS_DELETED + "'";
				final String statusUsed = PretupsI.STATUS_NOTIN;
				final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
				final String filteredMSISDN = PretupsBL
						.getFilteredMSISDN(usersReportModel.getMsisdn());
				ChannelUserVO channelUserVO = null;
				// load user details on the basis of msisdn
				if (PretupsI.OPERATOR_TYPE_OPT.equals(userVO.getDomainID())) {
					channelUserVO = channelUserDAO.loadUsersDetails(con,
							filteredMSISDN, null, statusUsed, status);
				} else {
					channelUserVO = channelUserDAO.loadUsersDetails(con,
							filteredMSISDN, userVO.getUserID(), statusUsed,
							status);
				}
				if (!(PretupsBL.getFilteredMSISDN(usersReportModel.getMsisdn())
						.equals(userVO.getMsisdn()))
						&& (channelUserVO == null || PretupsI.STAFF_USER_TYPE
								.equals(channelUserVO.getUserType()))) {
					ar = new String[1];
					ar[0] = usersReportModel.getMsisdn();
					throw new BTSLBaseException(this, methodName,
							"c2s.reports.error.nouserbymsisdn", 0, ar,
							loadUserBalance);
				}
				// check msisdn belongs to hierarchy of login user if hierarchy
				// is allowed to login user
				ArrayList channelUserList = new ArrayList();
				if ("Y".equals(userVO.getCategoryVO().getHierarchyAllowed())) {
					channelUserList = channelUserWebDAO
							.loadChannelUserHierarchy(con,
									userVO.getUserCode(), false);
				} else {
					channelUserList = channelUserWebDAO
							.loadChannelUserHierarchy(con,
									(channelUserDAO.loadChannelUser(con,
											userVO.getParentID()))
											.getUserCode(), false);
				}
				if (channelUserList != null && !channelUserList.isEmpty()) {
					if (!(channelUserVO.getCategoryVO()
							.getAgentDomainCodeforCategory()).equals(userVO
							.getCategoryVO().getDomainCodeforCategory())) {
						final String []arr1 = { usersReportModel.getMsisdn() };
						log.error(methodName, "Error: MSISDN Number"
								+ usersReportModel.getMsisdn()
								+ " does not belong to channel domain");
						throw new BTSLBaseException(this, methodName,
								"c2s.reports.error.usernotindomain", 0, arr1,
								loadUserBalance);
					}
				}
				btslCommonController.validateMsisdn(filteredMSISDN, userVO,
						usersReportModel, channelUserVO, con);

			}// 5.1.3 new features end
			else {
				tempCatCode = usersReportModel.getParentCategoryCode();
				if (usersReportModel.getZoneCode().equals(TypesI.ALL)) {
					usersReportModel.setZoneName(PretupsRestUtil
							.getMessageString(PRETUPS_ALL));
				} else {
					listValueVO = BTSLUtil.getOptionDesc(
							usersReportModel.getZoneCode(),
							usersReportModel.getZoneList());
					usersReportModel.setZoneName(listValueVO.getLabel());
				}
				listValueVO = BTSLUtil.getOptionDesc(
						usersReportModel.getDomainCode(),
						usersReportModel.getDomainList());
				usersReportModel.setDomainName(listValueVO.getLabel());
				final ChannelUserReportDAO channelUserReportDAO = new ChannelUserReportDAO();
				if (usersReportModel.getParentCategoryCode().equals(TypesI.ALL)) {
					usersReportModel.setParentCategoryCode(TypesI.ALL);
					usersReportModel.setCategoryName(PretupsRestUtil
							.getMessageString(PRETUPS_ALL));
					userList = channelUserReportDAO
							.loadUserListBasisOfZoneDomainCategory(con,
									PretupsI.ALL,
									usersReportModel.getDomainCode(),
									usersReportModel.getZoneCode(),
									usersReportModel.getUserName(),
									userVO.getUserID());
				} else {
					listValueVO = BTSLUtil.getOptionDesc(
							usersReportModel.getParentCategoryCode(),
							usersReportModel.getParentCategoryList());
					usersReportModel.setCategoryName(listValueVO.getLabel());

					final String[] arr = usersReportModel
							.getParentCategoryCode().split("\\|");
					usersReportModel.setParentCategoryCode(arr[1]);
					userList = channelUserReportDAO
							.loadUserListBasisOfZoneDomainCategory(con, arr[1],
									usersReportModel.getDomainCode(),
									usersReportModel.getZoneCode(),
									usersReportModel.getUserName(),
									userVO.getUserID());
				}
				if (usersReportModel.getUserName().equalsIgnoreCase(
						PretupsRestUtil.getMessageString(PRETUPS_ALL))) {
					usersReportModel.setUserID(PretupsI.ALL);

				} else if (userList == null || userList.isEmpty()) {
					usersReportModel.setParentCategoryCode(tempCatCode);
					model.addAttribute(
							FAIL_KEY,
							PretupsRestUtil
									.getMessageString("pretups.user.selectcategoryforedit.error.usernotexist"));
					bool = false;

				} else if (userList.size() == 1) {
					final ChannelUserTransferVO channelUserTransferVO = (ChannelUserTransferVO) userList
							.get(0);
					usersReportModel.setUserName(channelUserTransferVO
							.getUserName());
					usersReportModel.setUserID(channelUserTransferVO
							.getUserID());
				} else if (userList.size() > 1) {
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
					boolean flag = true;
					if (!BTSLUtil.isNullString(usersReportModel.getUserID())) {
						for (int i = 0, j = userList.size(); i < j; i++) {
							final ChannelUserTransferVO channelUserTransferVO = (ChannelUserTransferVO) userList
									.get(i);
							if (usersReportModel.getUserID().equals(
									channelUserTransferVO.getUserID())
									&& usersReportModel.getUserName()
											.equalsIgnoreCase(
													channelUserTransferVO
															.getUserName())) {
								usersReportModel
										.setUserName(channelUserTransferVO
												.getUserName());
								flag = false;
								
								break;
							}
						}
					}
					if (flag) {
						usersReportModel.setParentCategoryCode(tempCatCode);
						final BTSLMessages btslMessage = new BTSLMessages(
								"user.selectcategoryforedit.error.usermorethanone",
								loadUserBalance);
					}
				}
			}
			usersReportModel.setDateType("");
			final java.util.Date frDate = BTSLUtil
					.getDateFromDateString(usersReportModel.getFromDate());
			final java.util.Date tDate = BTSLUtil
					.getDateFromDateString(usersReportModel.getToDate());
			final ChannelUserVO _channerUser = (ChannelUserVO) userVO;
			if (!(BTSLUtil.checkDateFromMisDates(tDate, tDate, _channerUser,
					ProcessI.C2SMIS))) {
				final String []arr = {
						BTSLUtil.getDateStringFromDate(_channerUser
								.getC2sMisFromDate()),
						BTSLUtil.getDateStringFromDate(_channerUser
								.getC2sMisToDate()) };
				throw new BTSLBaseException(
						"report.date.range.error.msg", arr, loadUserBalance);
				
			}
			final String fromdate = BTSLUtil.sqlDateToDateYYYYString(BTSLUtil
					.getSQLDateFromUtilDate(frDate));
			final String todate = BTSLUtil.sqlDateToDateYYYYString(BTSLUtil
					.getSQLDateFromUtilDate(tDate));

			if(request.getParameter("iNETReportUserName")!= null || request.getParameter(INETReport_MSISDN)!= null)
			{
				usersReportModel
				  .setRptfromDate(BTSLUtil.reportDateFormat(fromdate));
				usersReportModel.setRpttoDate(BTSLUtil.reportDateFormat(todate));
			}
			else
			{
				usersReportModel.setRptfromDate(usersReportModel.getFromDate());
				 
				usersReportModel.setRpttoDate(usersReportModel.getToDate());
			}
			 
			
			if (!BTSLUtil.isNullString(usersReportModel.getServiceType())
					&& usersReportModel.getServiceType().equals(TypesI.ALL)) {
				usersReportModel.setServiceTypeName(PretupsRestUtil
						.getMessageString(PRETUPS_ALL));
			} else if (!BTSLUtil
					.isNullString(usersReportModel.getServiceType())) {
				listValueVO = BTSLUtil.getOptionDesc(
						usersReportModel.getServiceType(),
						usersReportModel.getServiceTypeList());
				usersReportModel.setServiceTypeName(listValueVO.getLabel());
			}
			if (!BTSLUtil.isNullString(usersReportModel.getTransferStatus())) {
				listValueVO = BTSLUtil.getOptionDesc(
						usersReportModel.getTransferStatus(),
						usersReportModel.getTransferStatusList());
				usersReportModel.setTransferStatusName(listValueVO.getLabel());
			}
			if(request.getParameter("iNETReportUserName")== null && request.getParameter(INETReport_MSISDN)== null)
			{
				UserDailyBalanceMovementRptDAO userDailyBalanceMovementRptDAO = new UserDailyBalanceMovementRptDAO();
			
			if (usersReportModel.getUserType().equals("CHANNEL")) {
				rptCode = "USERBALMOV02";
				final ArrayList transferList = userDailyBalanceMovementRptDAO
						.dailyBalanceMovementChnlUserRpt(con, usersReportModel);

				usersReportModel.setTransferList(transferList);
				usersReportModel.setrptCode(rptCode);
				usersReportModel.setTransferListSize(transferList.size());
			}
			if (usersReportModel.getUserType().equals("OPERATOR"))

			{
				rptCode = "USERBALMOV01";
				final ArrayList transferList = userDailyBalanceMovementRptDAO
						.dailyBalanceMovementOptRpt(con, usersReportModel);
				usersReportModel.setTransferList(transferList);
				usersReportModel.setrptCode(rptCode);
				usersReportModel.setTransferListSize(transferList.size());
			}
			}

		} catch (BTSLBaseException e) {
			log.errorTrace(methodName, e);
			model.addAttribute(
					FAIL_KEY,
					PretupsRestUtil.getMessageString(e.getMessage(),
							e.getArgs()));
			request.getSession().setAttribute("userDailyBalanceMovementReport",
					usersReportModel);
			return false;

		} catch (Exception e) {
			log.errorTrace(methodName, e);
			model.addAttribute(FAIL_KEY,
					PretupsRestUtil.getMessageString(e.getMessage()));
			request.getSession().setAttribute("userDailyBalanceMovementReport",
					usersReportModel);
			return false;

		} finally {
			if (mcomCon != null) {
				mcomCon.close("UserBalanceMovementReportsServiceImpl#displayUserBalanceReport");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.error(methodName, "Exiting");
			}
		}
		request.getSession().setAttribute("userDailyBalanceMovementReport",
				usersReportModel);
		return bool;

	}
	
	@Override
	public String downloadFileforSumm(UsersReportModel usersReportModel)
			throws InterruptedException, BTSLBaseException, SQLException {

		DownloadCSVReports downloadCSVReports = new DownloadCSVReports();
		Connection con ;
		MComConnectionI mcomCon ;
		mcomCon = new MComConnection();
		con = mcomCon.getConnection();
		String rptCode = usersReportModel.getrptCode();
		String filePath ;

		filePath = downloadCSVReports.prepareData(usersReportModel, rptCode,
				con);
		return filePath;

	}
}

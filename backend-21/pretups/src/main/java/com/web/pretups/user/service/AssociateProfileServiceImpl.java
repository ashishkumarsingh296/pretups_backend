package com.web.pretups.user.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonValidator;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileDAO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryGradeDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.logging.ChannelUserLog;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LocaleMasterDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.master.businesslogic.SubLookUpDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.TargetBasedCommissionMessages;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.roles.businesslogic.UserRolesWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.pretups.user.web.AssociateProfileController;
import com.web.user.businesslogic.UserWebDAO;
import com.web.user.web.UserModel;

@Service("associateProfileService")
public class AssociateProfileServiceImpl implements AssociateProfileService{
	public static final Log log = LogFactory.getLog(AssociateProfileController.class
			.getName());
	private static final String CLASS_NAME = "AssociateProfileServiceImpl";
	private static final String FAIL_KEY = "fail";
	private static final String CHANNEL = "CHANNEL";
	private static final String ASSOCIATE = "associate";
	private static final String FORM_NUMBER = "formNumber";
	private static final String USER_SAME_LEVEL_ERROR = "Error: User are at the same level";
	private static final String USER_NOT_EXIST_ERROR = "Error: User not exist";
	private static final String USER_NOT_IN_SAME_DOMAIN_ERROR = "Error: User not in the same domain";
	private static final String VALIDATOR_ASSOCIATE_USER = "configfiles/user/validator-associateChannelUser.xml";
	private static final String VALIDATOR_ASSOCIATE_USER_SUBMIT = "configfiles/user/validator-associateChannelUserSubmit.xml";
	private static final String CONTROL_COUNT = "control_count = ";
	private static final String TARGET_COUNT = ", target_count = ";
	private static final String RETVAL_4_ASSOCIATION = ", retval4Association = ";
	private static final String RETVAL_4_DEASSOCIATION = ", retval4deAssociation = ";
	private static final String RETVAL_TARGET_DEASSOCIATION = ", retvalTargetControl = ";
	private static final String CONTROL_CNT = "control_count";
	private static final String TGT_CNT = "target_count";
	private static final String ERROR_GENERAL = "error.general.processing";

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public UserModel loadAssociateProfile(UserVO userVO) {

		final String methodName = "loadAssociateProfile";
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
		}

		UserModel userModel = new UserModel();
		userModel.setRequestType(ASSOCIATE); 
		userModel.setNetworkCode(userVO.getNetworkID());
		userModel.setNetworkName(userVO.getNetworkName());
		userModel.setLoginUserID(userVO.getOwnerName()+"("+userVO.getOwnerID()+")");
		Connection con = null;
		MComConnectionI mcomCon = null;
		CategoryWebDAO categoryWebDAO =  new CategoryWebDAO();
		final CategoryDAO categoryDAO = new CategoryDAO();
		final ArrayList list = new ArrayList();
		try{
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			if (PretupsI.OPERATOR_TYPE_OPT.equals(userVO.getDomainID())) {
				userModel.setDomainCode(userVO.getDomainID());
				userModel.setDomainList(BTSLUtil.displayDomainList(userVO.getDomainList()));   
				if(!userModel.getDomainList().isEmpty()){
					userModel.setDomainListTSize(userVO.getDomainList().size());
				}
				if (userModel.getDomainList() != null && !userModel.getDomainList().isEmpty()) {
					userModel.setDomainShowFlag(true);
				} else {
					userModel.setDomainShowFlag(false);
				}
				userModel.setOrigCategoryList(categoryDAO.loadOtherCategorList(con, PretupsI.OPERATOR_TYPE_OPT));  
				if (userModel.getOrigCategoryList() != null && !BTSLUtil.isNullString(userModel.getDomainCode())) {
					CategoryVO categoryVO = null;
					for (int i = 0, j = userModel.getOrigCategoryList().size(); i < j; i++) {
						categoryVO = (CategoryVO) userModel.getOrigCategoryList().get(i);
						if (categoryVO.getDomainCodeforCategory().equals(userModel.getDomainCode())) {
							list.add(categoryVO);
						}
					}
				}
				userModel.setCategoryList(list);
			} else {
				userModel.setDomainCode(userVO.getDomainID());
				userModel.setDomainCodeDesc(userVO.getDomainName());
				userModel.setDomainShowFlag(true);
				final ArrayList categoryList = categoryWebDAO.loadCategorListByDomainCode(con, userVO.getDomainID());
				userModel.setOrigCategoryList(categoryList);
				if (categoryList != null) {
					CategoryVO categoryVO = null;
					for (int i = 0, j = categoryList.size(); i < j; i++) {
						categoryVO = (CategoryVO) categoryList.get(i);

						if (ASSOCIATE.equals(userModel.getRequestType()) && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PROFILEASSOCIATE_AGENT_PREFERENCES)).booleanValue() || "associateOther".equals(userModel.getRequestType()) && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PROFILEASSOCIATE_AGENT_PREFERENCES)).booleanValue()) {
							if ((categoryVO.getSequenceNumber() == userVO.getCategoryVO().getSequenceNumber() + 1) && PretupsI.AGENTCATEGORY.equals(categoryVO
									.getCategoryType())) {
								list.add(categoryVO);
							}

						}

						else if (categoryVO.getSequenceNumber() > userVO.getCategoryVO().getSequenceNumber()) {
							list.add(categoryVO);
						}
					}
					userModel.setCategoryList(list);
					if (ASSOCIATE.equals(userModel.getRequestType()) && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PROFILEASSOCIATE_AGENT_PREFERENCES)).booleanValue() || "associateOther".equals(userModel.getRequestType()) && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PROFILEASSOCIATE_AGENT_PREFERENCES)).booleanValue()) {
						if (list.isEmpty()) {
							throw new BTSLBaseException(this, methodName, "user.loaddomainlist.error.noagentcategoryfound");
						}
					}

				}
			}
			final ArrayList geoList = userVO.getGeographicalAreaList();
			if (geoList != null && geoList.size() > 1) {
				userModel.setAssociatedGeographicalList(geoList);
				userModel.setGeoDomainSize(geoList.size());
				final UserGeographiesVO vo = (UserGeographiesVO) geoList.get(0);
				userModel.setParentDomainTypeDesc(vo.getGraphDomainTypeName());
			} else if (geoList != null && geoList.size() == 1) {
				userModel.setGeoDomainSize(geoList.size());
				userModel.setAssociatedGeographicalList(null);
				final UserGeographiesVO vo = (UserGeographiesVO) geoList.get(0);
				userModel.setParentDomainCode(vo.getGraphDomainCode());
				userModel.setParentDomainDesc(vo.getGraphDomainName());
				userModel.setParentDomainTypeDesc(vo.getGraphDomainTypeName());
			} else {
				userModel.setAssociatedGeographicalList(null);
			}
		}catch(Exception e){

			log.errorTrace(methodName, e);
		}
		finally{
			if(mcomCon != null)
			{
				mcomCon.close(CLASS_NAME+methodName);
				mcomCon=null;
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME+methodName, PretupsI.EXITED);
		}	

		return userModel;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserVO> loadUserList(UserVO userVO, String categorycode, String ownerId,
			String userName, String domainCode, String prntDomainCode, HttpServletRequest request, String index) {
		final String methodName = "loadUserList";
		StringBuilder statusSbf = null;
		StringBuilder statusUsedSbf = null;
		String status = null;
		String statusUsed = null;
		ArrayList<UserVO> userList = new ArrayList<>();
		UserWebDAO userwebDAO = new UserWebDAO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		try {
			mcomCon = new MComConnection();
			try{
				con = mcomCon.getConnection();
			}
			catch(SQLException e){
				log.error(methodName,  "SQLException"+ e.getMessage());
				if (log.isDebugEnabled()) {
					log.debug(CLASS_NAME + methodName, e);
				}
			}
			statusSbf = new StringBuilder();
			statusUsedSbf = new StringBuilder();

			if ("1".equalsIgnoreCase(index) && !CHANNEL.equalsIgnoreCase(userVO.getUserType())) {

				statusSbf.append(PretupsBL.userStatusIn() + ",'" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'");
				statusUsedSbf.append(PretupsI.STATUS_IN);
		
				status = statusSbf.toString();
				statusUsed = statusUsedSbf.toString();

				userList = userwebDAO.loadOwnerUserList(con, prntDomainCode, "%" + userName + "%", domainCode, statusUsed,
						status);
			} else {

				statusSbf.append(PretupsBL.userStatusIn());
				statusUsedSbf.append(PretupsI.STATUS_IN);
				status = statusSbf.toString();
				statusUsed = statusUsedSbf.toString();

				if (PretupsI.OPERATOR_TYPE_OPT.equals(userVO.getDomainID())) {
					userList = userwebDAO.loadUsersListByNameAndOwnerId(con, categorycode, "%" + userName + "%", ownerId, null, statusUsed, status, CHANNEL);
				} else {
					String userID = userVO.getUserID();


					userList = userwebDAO.loadUsersListByNameAndOwnerId(con, categorycode, "%" + userName + "%", userID, userID, statusUsed, status, CHANNEL);
				}

			}
		} catch (BTSLBaseException e) {

			log.error(methodName, PretupsI.BTSLEXCEPTION + e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME + methodName, e);
			}
		}finally{
			if(mcomCon != null)
			{
				mcomCon.close(CLASS_NAME+"#"+methodName);
				mcomCon=null;
			}
		}

		return userList;
	}

	@Override
	public void getCategoryList(UserModel userModel){
		final String methodName = "getCategoryList";
		final ArrayList<CategoryVO> list = new ArrayList<>();
		Connection con = null;
		MComConnectionI mcomCon = null;
		final CategoryDAO categoryDAO = new CategoryDAO();
		try{
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			userModel.setOrigCategoryList(categoryDAO.loadOtherCategorList(con, PretupsI.OPERATOR_TYPE_OPT));
			if (userModel.getOrigCategoryList() != null && !BTSLUtil.isNullString(userModel.getDomainCode())) {
				CategoryVO categoryVO = null;
				for (int i = 0, j = userModel.getOrigCategoryList().size(); i < j; i++) {
					categoryVO = (CategoryVO) userModel.getOrigCategoryList().get(i);

					if (categoryVO.getDomainCodeforCategory().equals(userModel.getDomainCode())) {
						list.add(categoryVO);
					}
				}
			}
			userModel.setCategoryList(list);
		}catch(Exception e){

			log.error(methodName, PretupsI.EXCEPTION + e);
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME + methodName, e);
			}
		}
		finally{
			if(mcomCon != null)
			{
				mcomCon.close(CLASS_NAME+"#"+methodName);
				mcomCon=null;
			}
		}
	}


	@Override
	public List<UserVO> loadOwnerList(UserVO userVO, String prntDomaincode, String ownerName, String domainCode, HttpServletRequest request){
		Connection con = null;
		MComConnectionI mcomCon = null;
		final UserWebDAO userwebDAO = new UserWebDAO();
		final String methodName = "loadOwnerList";
		ArrayList userList = null;
		try{
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();

			if (!BTSLUtil.isNullString(ownerName)) {

				String status = null;
				String statusUsed = null;

				StringBuilder statusSbf = new StringBuilder();
				StringBuilder statusUsedSbf  = new StringBuilder();

				statusSbf.append(PretupsBL.userStatusIn());
				statusUsedSbf.append(PretupsI.STATUS_IN);


				status = statusSbf.toString();
				statusUsed = statusUsedSbf.toString();
				userList = userwebDAO.loadOwnerUserList(con, prntDomaincode, "%" + ownerName + "%", domainCode, statusUsed,
						status);

			}

		}catch(Exception e){

			log.error(methodName, PretupsI.EXCEPTION + e);
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME + methodName, e);
			}
		}
		finally{
			if(mcomCon != null)
			{
				mcomCon.close(CLASS_NAME+"#"+methodName);
				mcomCon=null;
			}
		}
		return userList;
	}

	@Override
	public boolean getAssociationDetails(ChannelUserVO channelUserSessionVO, UserModel userModel, Model model, BindingResult bindingResult, HttpServletRequest request){
		final String methodName = "#getAssociationDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		UserWebDAO userwebDAO = null;

		ArrayList<UserPhoneVO> userPhoneList = null;
		try {
			userwebDAO = new UserWebDAO();


			userModel.setSearchList(null);
			String status = "";
			String statusUsed = "";

			if(request.getParameter("submitMsisdn")!=null){
				CommonValidator commonValidator=new CommonValidator(VALIDATOR_ASSOCIATE_USER, userModel, "UserModelMsisdn");
				Map<String, String> errorMessages = commonValidator.validateModel();
				PretupsRestUtil pru=new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult);
				request.getSession().setAttribute(FORM_NUMBER, "Panel-One");
			}
			if(request.getParameter("submitLoginId")!=null){
				CommonValidator commonValidator=new CommonValidator(VALIDATOR_ASSOCIATE_USER, userModel, "UserModelLoginId");
				Map<String, String> errorMessages = commonValidator.validateModel();
				PretupsRestUtil pru=new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult); 
				request.getSession().setAttribute(FORM_NUMBER, "Panel-Two");
			}
			if(request.getParameter("submitUser")!=null){
				CommonValidator commonValidator=new CommonValidator(VALIDATOR_ASSOCIATE_USER, userModel, "UserModelUserName");
				Map<String, String> errorMessages = commonValidator.validateModel();
				PretupsRestUtil pru=new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult); 
				request.getSession().setAttribute(FORM_NUMBER, "Panel-Three");
			}
			if(bindingResult.hasFieldErrors()){

				return false;
			}
			status = PretupsBL.userStatusNotIn() + ",'" + PretupsI.USER_STATUS_DELETE_REQUEST + "'";
			statusUsed = PretupsI.STATUS_NOTIN;

			String[] arr = null;
			if (BTSLUtil.isNullString(userModel.getSearchMsisdn()) && BTSLUtil.isNullString(userModel.getDomainCode()) && BTSLUtil.isNullString(userModel
					.getChannelCategoryCode()) && BTSLUtil.isNullString(userModel.getSearchLoginId())) {
				model.addAttribute(FAIL_KEY, PretupsRestUtil
						.getMessageString("pretups.user.selectchanneluserforview.error.search.required"));
				return false;
			} else if (BTSLUtil.isNullString(userModel.getSearchMsisdn()) && BTSLUtil.isNullString(userModel.getSearchLoginId()) ) { 
				if (BTSLUtil.isNullString(userModel.getDomainCode())) {
					model.addAttribute(FAIL_KEY, PretupsRestUtil
							.getMessageString("pretups.user.selectchanneluserforview.error.domaincode.required"));
					return false;
				}
				if (BTSLUtil.isNullString(userModel.getChannelCategoryCode())) {
					model.addAttribute(FAIL_KEY, PretupsRestUtil
							.getMessageString("pretups.user.selectchanneluserforview.error.channelcategorycode.required"));
					return false;
				}

			}
			if (!BTSLUtil.isNullString(userModel.getSearchMsisdn()) && !BTSLUtil.isValidMSISDN(userModel.getSearchMsisdn())) {

				
					arr = new String[1];
					arr[0] = userModel.getSearchMsisdn();
					model.addAttribute(FAIL_KEY, PretupsRestUtil
							.getMessageString("pretups.associateProfile.msisdn.error.length", arr));
					return false;
				
			}
	
			if (!BTSLUtil.isNullString(userModel.getSearchMsisdn())){

				userModel.setSearchCriteria("M");
				final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(userModel
						.getSearchMsisdn())));
				if (prefixVO == null || !prefixVO.getNetworkCode().equals(channelUserSessionVO.getNetworkID())) {
					final String[] arr1 = { userModel.getSearchMsisdn(), channelUserSessionVO.getNetworkName() };
					log.error(methodName, "Error: MSISDN Number" + userModel.getSearchMsisdn() + " not belongs to " + channelUserSessionVO.getNetworkName() + "network");
					model.addAttribute(FAIL_KEY, PretupsRestUtil
							.getMessageString("pretups.user.associateProfile.error.msisdnnotinsamenetwork",arr1));
					return false;
				}

				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				final UserDAO userDAO = new UserDAO();


				final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
				final String filteredMSISDN = PretupsBL.getFilteredMSISDN(userModel.getSearchMsisdn());
				ChannelUserVO channelUserVO = null;

				if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO.getDomainID())) {
					channelUserVO = channelUserDAO.loadUsersDetails(con, filteredMSISDN, null, statusUsed, status);
				} else {
					String userID = channelUserSessionVO.getUserID();
					channelUserVO = channelUserDAO.loadUsersDetails(con, filteredMSISDN, userID, statusUsed, status);
				}

				if (channelUserVO != null) {

					boolean rsaRequired = false;
					rsaRequired = BTSLUtil.isRsaRequired(channelUserVO);
					userModel.setRsaRequired(rsaRequired);
					if (channelUserSessionVO.getCategoryVO().getCategoryCode().equals(channelUserVO.getCategoryVO().getCategoryCode()) && !PretupsI.USER_TYPE_STAFF
							.equals(channelUserVO.getUserType())) {

						final String[] arr2 = { userModel.getSearchMsisdn() };
						log.error(methodName, USER_SAME_LEVEL_ERROR);
						model.addAttribute(FAIL_KEY, PretupsRestUtil
								.getMessageString("pretups.user.associateProfile.error.usermsisdnatsamelevel", arr2));
						return false;
					}

					if (userModel.getSelectDomainList() != null) {
						final boolean isDomainFlag = this.isExistDomain(userModel.getSelectDomainList(), channelUserVO);
						final ListValueVO listValueVO = BTSLUtil.getOptionDesc(channelUserVO.getCategoryVO().getDomainCodeforCategory(), userModel.getSelectDomainList());
						userModel.setDomainCodeDesc(listValueVO.getLabel());
						if (!isDomainFlag) {

							final String[] arr2 = { userModel.getSearchMsisdn() };
							log.error(methodName, USER_NOT_IN_SAME_DOMAIN_ERROR);
							model.addAttribute(FAIL_KEY, PretupsRestUtil
									.getMessageString("pretups.user.associateProfile.error.usermsisdnnotinsamedomain", arr2));
							return false;

						}
					}
					final boolean isGeoDomainFlag = userwebDAO.isUserInSameGRPHDomain(con, channelUserVO.getUserID(), channelUserVO.getCategoryVO().getGrphDomainType(),
							channelUserSessionVO.getUserID(), channelUserSessionVO.getCategoryVO().getGrphDomainType());
					if (isGeoDomainFlag) {
						userModel.setCategoryVO(channelUserVO.getCategoryVO());
						userModel.setCategoryCode(userModel.getCategoryVO().getCategoryCode());
						userModel.setChannelCategoryCode(userModel.getCategoryVO().getCategoryCode());
						userModel.setCategoryCodeDesc(userModel.getCategoryVO().getCategoryName());
						userModel.setChannelCategoryDesc(userModel.getCategoryVO().getCategoryName());

						userModel.setParentDomainDesc(channelUserVO.getGeographicalDesc());
						userModel.setEmail(channelUserVO.getEmail());
						userPhoneList = userDAO.loadUserPhoneList(con,
								channelUserVO.getUserID());
						for(int i =0; i < userPhoneList.size(); i++){
							userPhoneList.get(i).setShowSmsPin("");
							userPhoneList.get(i).setConfirmSmsPin("");
						}
						userModel.setMsisdnList(userPhoneList);
						userModel.setUserName(channelUserVO.getUserName());
						setDetailsOnModel(channelUserVO, userModel, request, channelUserSessionVO);
						return true;
					} else if (!isGeoDomainFlag) {
						final String[] arr2 = { userModel.getSearchMsisdn() };
						log.error(methodName, USER_NOT_IN_SAME_DOMAIN_ERROR);

						model.addAttribute(FAIL_KEY, PretupsRestUtil
								.getMessageString("pretups.user.associateProfile.error.usermsisdnnotinsamegeodomain",arr2));
						return false;
					}

				} else {

					final String[] arr2 = { userModel.getSearchMsisdn() };
					log.error(methodName, USER_NOT_EXIST_ERROR);

					model.addAttribute(FAIL_KEY, PretupsRestUtil
							.getMessageString("pretups.user.associateProfile.error.usermsisdnnotexist", arr2));
					return false;
				}

			}

			else if (!BTSLUtil.isNullString(userModel.getSearchLoginId())){
				userModel.setSearchCriteria("L");
				mcomCon = new MComConnection();
				con=mcomCon.getConnection();
				final UserDAO userDAO = new UserDAO();


				final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
				ChannelUserVO channelUserVO = null;

				if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO.getDomainID())) {
					channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(con, userModel.getSearchLoginId(), null, statusUsed, status);
				} else {
					String userID = channelUserSessionVO.getUserID();


					channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(con, userModel.getSearchLoginId(), userID, statusUsed, status);
				}

				if (channelUserVO != null) {

					boolean rsaRequired = false;
					rsaRequired = BTSLUtil.isRsaRequired(channelUserVO);
					userModel.setRsaRequired(rsaRequired);

					if (channelUserSessionVO.getCategoryVO().getCategoryCode().equals(channelUserVO.getCategoryVO().getCategoryCode()) && !PretupsI.USER_TYPE_STAFF
							.equals(channelUserVO.getUserType())) {
						final String[] arr2 = { userModel.getSearchLoginId() };
						log.error(methodName, USER_SAME_LEVEL_ERROR);

						model.addAttribute(FAIL_KEY, PretupsRestUtil
								.getMessageString("pretups.user.associateProfile.error.userloginidatsamelevel", arr2));
						return false;
					}

					if (userModel.getSelectDomainList() != null) {
						final boolean isDomainFlag = this.isExistDomain(userModel.getSelectDomainList(), channelUserVO);
						final ListValueVO listValueVO = BTSLUtil.getOptionDesc(channelUserVO.getCategoryVO().getDomainCodeforCategory(), userModel.getSelectDomainList());
						userModel.setDomainCodeDesc(listValueVO.getLabel());
						if (!isDomainFlag) {
							final String[] arr2 = { userModel.getSearchLoginId() };
							log.error(methodName, USER_NOT_IN_SAME_DOMAIN_ERROR);

							model.addAttribute(FAIL_KEY, PretupsRestUtil
									.getMessageString("pretups.user.associateProfile.error.userloginidnotinsamedomain", arr2));
							return false;
						}
					}


					final boolean isGeoDomainFlag = userwebDAO.isUserInSameGRPHDomain(con, channelUserVO.getUserID(), channelUserVO.getCategoryVO().getGrphDomainType(),
							channelUserSessionVO.getUserID(), channelUserSessionVO.getCategoryVO().getGrphDomainType());

					if (isGeoDomainFlag) {
						userModel.setCategoryVO(channelUserVO.getCategoryVO());
						userModel.setCategoryCode(userModel.getCategoryVO().getCategoryCode());
						userModel.setChannelCategoryCode(userModel.getCategoryVO().getCategoryCode());
						userModel.setCategoryCodeDesc(userModel.getCategoryVO().getCategoryName());
						userModel.setChannelCategoryDesc(userModel.getCategoryVO().getCategoryName());
						userModel.setParentDomainDesc(channelUserVO.getGeographicalDesc());
						userModel.setEmail(channelUserVO.getEmail());
						userPhoneList = userDAO.loadUserPhoneList(con,
								channelUserVO.getUserID());
						for(int i =0; i < userPhoneList.size(); i++){
							userPhoneList.get(i).setShowSmsPin("");
							userPhoneList.get(i).setConfirmSmsPin("");
						}
						userModel.setMsisdnList(userPhoneList);
						userModel.setUserName(channelUserVO.getUserName());
						setDetailsOnModel(channelUserVO, userModel, request, channelUserSessionVO);
						return true;
					} else if (!isGeoDomainFlag) {

						final String[] arr2 = { userModel.getSearchLoginId() };
						log.error(methodName, USER_NOT_IN_SAME_DOMAIN_ERROR);

						model.addAttribute(FAIL_KEY, PretupsRestUtil
								.getMessageString("pretups.user.associateProfile.error.userloginidnotinsamegeodomain", arr2));
						return false;
					}

				} else {
					final String[] arr2 = { userModel.getSearchLoginId() };
					log.error(methodName, USER_NOT_EXIST_ERROR);
					model.addAttribute(FAIL_KEY, PretupsRestUtil
							.getMessageString("pretups.user.associateProfile.error.userloginidnotexist", arr2)); 
					return false;
				}
			}
			else if(!BTSLUtil.isNullString(userModel.getUserId())){
				
				String userName=userModel.getUserId();
				String[] userIDParts = null;
				String userID = null;
				String[] parts=userName.split("\\(");
				String userId = null;
				userName = parts[0];
				if(parts.length != 2){
					final String[] arr2 = { userModel.getUserId() };
					log.error(methodName, USER_NOT_EXIST_ERROR);
					model.addAttribute("fail", PretupsRestUtil
							.getMessageString("pretups.user.associateProfile.error.usernamenotexist", arr2));
					return false;
				}
				if(!BTSLUtil.isNullString(parts[1])){
					userIDParts = parts[1].split("\\)");
					userID = userIDParts[0];
				}
				userModel.setUserName(userName);
				if(request.getSession().getAttribute("ownerID") != null){
				userModel.setOwnerID(request.getSession().getAttribute("ownerID").toString());
				}
				userModel.setSearchCriteria("D");
				mcomCon = new MComConnection();
				con=mcomCon.getConnection();
				final UserDAO userDAO = new UserDAO();


				
				UserVO channelUserVO = null;
				
				
				List<UserVO> userList;
	        	String index = request.getSession().getAttribute("index").toString();
	        	
				
				if ("1".equalsIgnoreCase(index) && !CHANNEL.equalsIgnoreCase(channelUserSessionVO.getUserType())){
					status = PretupsBL.userStatusIn() + ",'" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'";
					statusUsed = PretupsI.STATUS_IN;
	        	if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO.getDomainID())) {
	        		
                    userList = userwebDAO.loadUsersList(con, channelUserSessionVO.getNetworkID(), null, null, userID, null, null,
                                    statusUsed, status);
                } else {
                    String sessionUserID = channelUserSessionVO.getUserID();
                    userList = userwebDAO.loadUsersList(con, channelUserSessionVO.getNetworkID(), null, null, userModel.getOwnerID(), null, sessionUserID,
                                    statusUsed, status);
                }
				}
				else{
					status = PretupsBL.userStatusIn();
					statusUsed = PretupsI.STATUS_IN;
	        	if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO.getDomainID())) {
                    userList = userwebDAO.loadUsersList(con, channelUserSessionVO.getNetworkID(), userModel.getCategoryCode(), "%" + userName + "%", null, userModel.getOwnerID(), null, statusUsed, status);
                } else {
                    String sessionuserId = channelUserSessionVO.getUserID();
                    userList = userwebDAO.loadUsersList(con, channelUserSessionVO.getNetworkID(), userModel.getCategoryCode(), "%" + userName + "%", null, userModel.getOwnerID(), sessionuserId, statusUsed, status);
                }
				}
				if(userList.size() == 1){

					channelUserVO  = userList.get(0); 
					userId = channelUserVO.getUserID();
					boolean rsaRequired = false;
					rsaRequired = BTSLUtil.isRsaRequired(channelUserVO);
					userModel.setRsaRequired(rsaRequired);
				}
				else if(userList.size()>1){

                    boolean isExist = false;

                    if (!BTSLUtil.isNullString(userID)) {
                        for (int i = 0, k = userList.size(); i < k; i++) {
                        	channelUserVO =  userList.get(i);
                            if (channelUserVO.getUserID().equals(userID) && userModel.getUserName().compareTo(channelUserVO.getUserName()) == 0) {
                                userModel.setUserId(channelUserVO.getUserID());
                                userModel.setUserName(channelUserVO.getUserName());
                                isExist = true;
                                break;
                            }
                        }

                    } else {
                    	ChannelUserVO listValueNextVO = null;
                        for (int i = 0, k = userList.size(); i < k; i++) {
                        	channelUserVO =  userList.get(i);
                            if (userModel.getUserName().compareTo(channelUserVO.getUserName()) == 0) {
                                if (((i + 1) < k)) {
                                    listValueNextVO = (ChannelUserVO) userList.get(i + 1);
                                    if (userModel.getUserName().compareTo(listValueNextVO.getUserName()) == 0) {
                                        isExist = false;
                                        break;
                                    }
                                    userModel.setUserId(channelUserVO.getUserID());
                                    userModel.setUserName(channelUserVO.getUserName());
                                    
                                    isExist = true;
                                    break;
                                }
                                userModel.setUserId(channelUserVO.getUserID());
                                userModel.setUserName(channelUserVO.getUserName());
                             
                                isExist = true;
                                break;
                            }
                        }
                    }
                    if (!isExist) {
                       model.addAttribute("fail",
    							PretupsRestUtil.getMessageString("pretups.channeltransfer.chnltochnlsearchuser.usermorethanoneexist.msg"));
    					return false;
                   
                    }
                
				}
				
				else {
					final String[] arr2 = { userModel.getUserId() };
					log.error(methodName, USER_NOT_EXIST_ERROR);
					model.addAttribute(FAIL_KEY, PretupsRestUtil
							.getMessageString("pretups.user.associateProfile.error.usernamenotexist", arr2));
					return false;
				}
				
				if (channelUserSessionVO.getCategoryVO().getCategoryCode().equals(channelUserVO.getCategoryCode()) && !PretupsI.USER_TYPE_STAFF
						.equals(channelUserVO.getUserType())) {
					final String[] arr2 = { userModel.getUserId() };
					log.error(methodName, USER_SAME_LEVEL_ERROR);

					model.addAttribute(FAIL_KEY, PretupsRestUtil
							.getMessageString("pretups.user.associateProfile.error.usernameatsamelevel", arr2));
					userModel.setUserId(userId);
					return false;
				}


				
					userModel.setEmail(channelUserVO.getEmail());
					userPhoneList = userDAO.loadUserPhoneList(con,
							channelUserVO.getUserID());
					for(int i =0; i < userPhoneList.size(); i++){
						userPhoneList.get(i).setShowSmsPin("");
						userPhoneList.get(i).setConfirmSmsPin("");
					}
					userModel.setMsisdnList(userPhoneList);
					channelUserVO.setNetworkID(userModel.getNetworkCode());
					
					setDetailsOnModel(channelUserVO, userModel, request, channelUserSessionVO);
					return true;

			}else{
				model.addAttribute(FAIL_KEY, PretupsRestUtil
						.getMessageString("pretups.user.choose.at.least.one.criteria")); 
				return false;

			}

		}catch(Exception e){

			try {
				if (con != null) {
					con.rollback();
				}
			} catch (Exception se) {
				log.errorTrace(methodName, se);
			}
			log.error(methodName, PretupsI.EXCEPTION + e);
			log.errorTrace(methodName, e);
		}finally {
			if (mcomCon != null) {
				mcomCon.close(CLASS_NAME+"#"+methodName);
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED+methodName);
			}
		}
		return true;        
	}

	/**
	 * 
	 * @param p_domainList
	 * @param p_channelUserVO
	 * @return
	 * @throws BTSLBaseException 
	 * @throws Exception
	 */
	private boolean isExistDomain(ArrayList domainList, UserVO channelUserVO) throws BTSLBaseException {
		final String methodName = "isExistDomain";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered p_domainList.size()=" + domainList.size() + ", p_channelUserVO=" + channelUserVO);
		}
		if (domainList == null || domainList.isEmpty()) {
			return true;
		}
		boolean isDomainExist = false;
		try {
			ListValueVO listValueVO;
			for (int i = 0, j = domainList.size(); i < j; i++) {
				listValueVO = (ListValueVO) domainList.get(i);
				if (listValueVO.getValue().equals(channelUserVO.getCategoryVO().getDomainCodeforCategory())) {
					isDomainExist = true;
					break;
				}
			}
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			throw new BTSLBaseException(e);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting isDomainExist=" + isDomainExist);
		}
		return isDomainExist;
	}
	

	 private void setDetailsOnModel(UserVO userVO, UserModel userModel, HttpServletRequest request, ChannelUserVO channelUserSessionVO){
	       
		 	final String methodName = "setDetailsOnModel";
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, PretupsI.ENTERED);
	        }
	        Connection con = null;
			MComConnectionI mcomCon = null;
			UserWebDAO userwebDAO = new UserWebDAO();
			UserDAO userDAO = new UserDAO();			
			try {
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				if(userVO.getCreatedOn() != null){
				userModel.setCreatedOn(BTSLUtil.getDateStringFromDate(userVO.getCreatedOn()));
				}
				final CategoryDAO catDAO = new CategoryDAO();
				final ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();


				userModel.setBatchID(userVO.getBatchID());
				userModel.setCreationType(userVO.getCreationType());
				if (!BTSLUtil.isNullString(userVO.getCreationType())) {
					userModel.setCreationTypeDesc(((LookupsVO) LookupsCache.getObject(PretupsI.USR_CREATION_TYPE, userVO.getCreationType())).getLookupName());
				}
				userModel.setUserId(userVO.getUserID());
				userModel.setChannelUserName(userVO.getUserName());
				userModel.setWebLoginID(userVO.getLoginID());
				userModel.setOldWebLoginID(userVO.getLoginID());
				userModel.setWebPassword(userVO.getPassword());
				userModel.setPasswordModifiedOn(userVO.getPasswordModifiedOn());
				
				String passValue="";
				if (!BTSLUtil.isNullString(userVO.getPassword())) {

					if ("SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
						passValue = "********";
					} else {
						passValue = BTSLUtil.getDefaultPasswordText(BTSLUtil.decryptText(userVO.getPassword()));
					}
				}
				userModel.setShowPassword(passValue);
				userModel.setConfirmPassword(passValue);
				userModel.setParentID(userVO.getParentID());
				userModel.setOwnerID(userVO.getOwnerID());
				userModel.setParentName(userVO.getParentName());
				userModel.setOwnerName(userVO.getOwnerName());
				userModel.setAllowedIPs(userVO.getAllowedIps());
				if (userVO.getAllowedDays() != null && userVO.getAllowedDays().trim().length() > 0) {
					userModel.setAllowedDays(userVO.getAllowedDays().split(","));
				}

				userModel.setAllowedFormTime(userVO.getFromTime());
				userModel.setAllowedToTime(userVO.getToTime());
				userModel.setEmpCode(userVO.getEmpCode());
				userModel.setStatus(userVO.getStatus());
				userModel.setStatusDesc(userVO.getStatusDesc());
				userModel.setPreviousStatus(userVO.getPreviousStatus());
				userModel.setEmail(userVO.getEmail());

				userModel.setCompany(userVO.getCompany());
				userModel.setFax(userVO.getFax());
				userModel.setUserLanguage(userVO.getLanguage());
				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_FNAME_LNAME_ALLOWED))).booleanValue()) {
					userModel.setFirstName(userVO.getFirstName());
					userModel.setLastName(userVO.getLastName());
				}

				userModel.setUserLanguageList(LocaleMasterDAO.loadLocaleMasterData());

				userModel.setContactNo(userVO.getContactNo());
				userModel.setDesignation(userVO.getDesignation());

				userModel.setMsisdn(userVO.getMsisdn());
				userModel.setUserType(userVO.getUserType());

				userModel.setAddress1(userVO.getAddress1());
				userModel.setAddress2(userVO.getAddress2());
				userModel.setCity(userVO.getCity());
				userModel.setState(userVO.getState());
				userModel.setCountry(userVO.getCountry());
				userModel.setRsaAuthentication(userVO.getRsaFlag());
				userModel.setSsn(userVO.getSsn());
				userModel.setUserNamePrefixCode(userVO.getUserNamePrefix());
				userModel.setExternalCode(userVO.getExternalCode());
				userModel.setShortName(userVO.getShortName());
				if (userVO.getAppointmentDate() != null) {
					userModel.setAppointmentDate(BTSLUtil.getDateStringFromDate(userVO.getAppointmentDate()));
				}
				userModel.setLastModified(userVO.getLastModified());
				userModel.setLevel1ApprovedBy(userVO.getLevel1ApprovedBy());
				userModel.setLevel1ApprovedOn(userVO.getLevel1ApprovedOn());
				userModel.setLevel2ApprovedBy(userVO.getLevel2ApprovedBy());
				userModel.setLevel2ApprovedOn(userVO.getLevel2ApprovedOn());
				userModel.setUserCode(userVO.getUserCode());
				
				final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
				final ChannelUserVO channelUserVO = channelUserDAO.loadChannelUser(con, userVO.getUserID());
				userModel.setContactPerson(channelUserVO.getContactPerson());
				userModel.setUserGradeId(channelUserVO.getUserGrade());
				userModel.setTrannferProfileId(channelUserVO.getTransferProfileID());
				if (channelUserVO.getCommissionProfileSetID() != null) {
					userModel.setCommissionProfileSetId(channelUserVO.getCommissionProfileSetID().split(":")[0]);
				}

				
				if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
					userModel.setLmsProfileId(channelUserVO.getLmsProfile());
					if (userModel.getLmsProfileId() != null) {
						userModel.setControlGroup(channelUserVO.getControlGroup());
					} else {
						userModel.setControlGroup(PretupsI.NO);
					}
				}
				userModel.setInsuspend(channelUserVO.getInSuspend());
				userModel.setOutsuspend(channelUserVO.getOutSuspened());
				userModel.setCategoryCode(channelUserVO.getCategoryCode());

				userModel.setCategoryCode(channelUserVO.getCategoryCode());

				userModel.setAuthTypeAllowed(channelUserVO.getAuthTypeAllowed());
				if ("N".equals(userModel.getIsCategoryCodeNeeded())) {
					userModel.setCategoryVO(catDAO.loadCategoryDetailsByCategoryCode(con, userModel.getCategoryCode()));
					userModel.setOutletCode(userModel.getCategoryVO().getOutletsAllowed());
					userModel.setChannelCategoryDesc(userModel.getCategoryVO().getCategoryName());
				} else {
					userModel.setOutletCode(channelUserVO.getOutletCode());
				}

				userModel.setOutletCode(channelUserVO.getOutletCode());
				userModel.setSubOutletCode(channelUserVO.getSubOutletCode());


				userModel.setLongitude(channelUserVO.getLongitude());
				userModel.setLatitude(channelUserVO.getLatitude());
				userModel.setDocumentType(channelUserVO.getDocumentType());
				userModel.setDocumentNo(channelUserVO.getDocumentNo());
				userModel.setPaymentType(channelUserVO.getPaymentType());
				userModel.setMpayProfileID(channelUserVO.getMpayProfileID());
				userModel.setMcommerceServiceAllow(channelUserVO.getMcommerceServiceAllow());
				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW))).booleanValue()) {
					userModel.setTrannferRuleTypeId(channelUserVO.getTrannferRuleTypeId());
				}
				if (!BTSLUtil.isNullString(channelUserVO.getUserGrade()) && !BTSLUtil.isNullString(channelUserVO.getMpayProfileID())) {
					userModel.setMpayProfileIDWithGrad(channelUserVO.getUserGrade() + ":" + channelUserVO.getMpayProfileID());
				}

				userModel.setOtherEmail(channelUserVO.getAlertEmail());
				userModel.setLowBalAlertAllow(channelUserVO.getLowBalAlertAllow());
				userModel.setLowBalAlertToSelf("N");
				userModel.setLowBalAlertToParent("N");
				userModel.setLowBalAlertToOther("N");
				
				if (userModel.getCategoryVO() != null && (TypesI.YES.equals(userModel.getCategoryVO().getLowBalAlertAllow()) && TypesI.YES.equals(channelUserVO.getLowBalAlertAllow()))) {
					final String alerttype = channelUserVO.getAlertType();
					if (alerttype != null) {
						final String[] alertTypeArr = alerttype.split(";");

						for (int k = 0; k < alertTypeArr.length; k++) {
							alertTypeArr[k] = alertTypeArr[k].toUpperCase().trim();

							if (alertTypeArr[k].equals(PretupsI.ALERT_TYPE_SELF)) {
								userModel.setLowBalAlertToSelf("Y");
							}
							if (alertTypeArr[k].equals(PretupsI.ALERT_TYPE_OTHER)) {
								userModel.setLowBalAlertToOther("Y");
							}
							if (alertTypeArr[k].equals(PretupsI.ALERT_TYPE_PARENT)) {
								userModel.setLowBalAlertToParent("Y");
							}
						}
					}
				}
				

				if (!BTSLUtil.isNullString(channelUserVO.getSubOutletCode()) && !BTSLUtil.isNullString(channelUserVO.getOutletCode())) {
					userModel.setSubOutletCode(channelUserVO.getSubOutletCode() + ":" + channelUserVO.getOutletCode());
				}

				userModel.setLmsProfileList(channelUserWebDAO.getLmsProfileList(con, userVO.getNetworkID()));


	           if(userModel.getCategoryVO() != null){
	            if (TypesI.YES.equals(userModel.getCategoryVO().getOutletsAllowed())) {
	                
	                if (userModel.getOutletList() == null) {
	                	userModel.setOutletList(LookupsCache.loadLookupDropDown(PretupsI.OUTLET_TYPE, true));
	                }

	                if (userModel.getSubOutletList() == null) {
	                    final SubLookUpDAO sublookupDAO = new SubLookUpDAO();
	                    userModel.setSubOutletList(sublookupDAO.loadSublookupByLookupType(con, PretupsI.OUTLET_TYPE));
	                }

	                ListValueVO vo = null;
	                
	                if (userModel.getOutletList() != null) {
	                    vo = BTSLUtil.getOptionDesc(userModel.getOutletCode(), userModel.getOutletList());
	                    userModel.setOutletCodeDesc(vo.getLabel());
	                }
	                if (userModel.getSubOutletList() != null) {
	                    vo = BTSLUtil.getOptionDesc(userModel.getSubOutletCode(), userModel.getSubOutletList());
	                    userModel.setSubOutletCodeDesc(vo.getLabel());
	                }

	            }
	           }
	            userModel.setCommissionProfileList(userwebDAO.loadCommisionProfileListByCategoryIDandGeography(con, userModel.getCategoryCode(), userVO.getNetworkID(), userModel
	                            .getGeographicalCode()));


	            final CategoryGradeDAO categoryGradeDAO = new CategoryGradeDAO();
	            
	            userModel.setUserGradeList(categoryGradeDAO.loadGradeList(con, userModel.getCategoryCode()));

	            
	            final TransferProfileDAO profileDAO = new TransferProfileDAO();
	            userModel.setTrannferProfileList(profileDAO.loadTransferProfileByCategoryID(con, userVO.getNetworkID(), userModel.getCategoryCode(), PretupsI.PARENT_PROFILE_ID_USER));

	           
	            userModel.setUserNamePrefixList(LookupsCache.loadLookupDropDown(PretupsI.USER_NAME_PREFIX_TYPE, true));
	            
	            final boolean isTrfRuleTypeAllow = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW, userVO.getNetworkID(), userModel
	                            .getCategoryCode())).booleanValue();
	            if (isTrfRuleTypeAllow) {
	            	userModel.setTrannferRuleTypeList(LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_RULE_AT_USER_LEVEL, true));
	            }
	            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
	            	userModel.setLmsProfileList(channelUserWebDAO.getLmsProfileList(con, channelUserSessionVO.getNetworkID()));
	            }

	           

	            
	            this.loadDetails(request, userModel, con, userDAO, userwebDAO, userVO, channelUserSessionVO);
	           
	            final ArrayList phoneList1 = userModel.getMsisdnList();
	            if (phoneList1 != null && !phoneList1.isEmpty()) {
	                final UserPhoneVO phoneVO = (UserPhoneVO) phoneList1.get(0);
	                userVO.setLanguage(phoneVO.getPhoneLanguage() + "_" + phoneVO.getCountry());
	                userModel.setPrimaryNumber(phoneVO.getPrimaryNumber());
	                userModel.setShowSmsPin(BTSLUtil.decryptText(phoneVO.getSmsPin()));
	                userModel.setConfirmSmsPin(BTSLUtil.decryptText(phoneVO.getSmsPin()));
	            }

	            userModel.setUserLanguage(userVO.getLanguage());
	            
				this.setDropDownValue(userModel);
			} catch (BTSLBaseException | SQLException | ParseException e) {
				log.error(methodName, PretupsI.EXCEPTION + e);
				if (log.isDebugEnabled()) {
					log.debug(CLASS_NAME + methodName, e);
				}
			} catch (Exception e) {
				log.error(methodName, PretupsI.EXCEPTION + e);
				if (log.isDebugEnabled()) {
					log.debug(CLASS_NAME + methodName, e);
				}
			}finally{
				if(mcomCon != null)
				{
					mcomCon.close(CLASS_NAME+methodName);
					mcomCon=null;
				}
			}
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, PretupsI.EXITED);
	        }   
	    }
	
	 
	 private void setDropDownValue(UserModel userModel) {
		 if (userModel.getCommissionProfileList() != null && !userModel.getCommissionProfileList().isEmpty()) {
			 final CommissionProfileSetVO vo = BTSLUtil.getOptionDescForCommProfile(userModel.getCommissionProfileSetId(), userModel.getCommissionProfileList());

			 userModel.setCommissionProfileSetIdDesc(vo.getCommProfileSetName());
		 }

		 GradeVO gradeVO;
		 if (userModel.getUserGradeList() != null) {
			 for (int i = 0, j = userModel.getUserGradeList().size(); i < j; i++) {
				 gradeVO = (GradeVO) userModel.getUserGradeList().get(i);
				 if (gradeVO.getGradeCode().equals(userModel.getUserGradeId())) {
					 userModel.setUserGradeIdDesc(gradeVO.getGradeName());
					 break;
				 }
			 }
		 }
		 if (userModel.getLmsProfileList() != null && !userModel.getLmsProfileList().isEmpty()) {
			 final ListValueVO listValueVO = BTSLUtil.getOptionDesc(userModel.getLmsProfileId(), userModel.getLmsProfileList());
			 userModel.setLmsProfileListIdDesc(listValueVO.getLabel());
		 }
		 if (userModel.getTrannferProfileList() != null && !userModel.getTrannferProfileList().isEmpty()) {
			 final ListValueVO vo = BTSLUtil.getOptionDesc(userModel.getTrannferProfileId(), userModel.getTrannferProfileList());
			 userModel.setTrannferProfileIdDesc(vo.getLabel());
		 }
		
		 final boolean isTrfRuleTypeAllow = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW, userModel.getNetworkCode(), userModel
				 .getCategoryCode())).booleanValue();
		 if (isTrfRuleTypeAllow && userModel.getTrannferRuleTypeList() != null && !userModel.getTrannferRuleTypeList().isEmpty()) {
			 final ListValueVO vo = BTSLUtil.getOptionDesc(userModel.getTrannferRuleTypeId(), userModel.getTrannferRuleTypeList());
			 userModel.setTrannferRuleTypeIdDesc(vo.getLabel());
		 }
		 if (userModel.getUserNamePrefixList() != null && !userModel.getUserNamePrefixList().isEmpty()) {
			 final ListValueVO vo = BTSLUtil.getOptionDesc(userModel.getUserNamePrefixCode(), userModel.getUserNamePrefixList());
			 userModel.setUserNamePrefixDesc(vo.getLabel());
		 }
		 
		 if (userModel.getUserLanguageList() != null && !userModel.getUserLanguageList().isEmpty()) {
			 final ListValueVO vo1 = BTSLUtil.getOptionDesc(userModel.getUserLanguage(), userModel.getUserLanguageList());
			 userModel.setUserLanguageDesc(vo1.getLabel());
		 }
		
		 if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD))).booleanValue() && userModel.getMpayProfileList() != null && !userModel.getMpayProfileList().isEmpty()) {

			 if (!BTSLUtil.isNullString(userModel.getMpayProfileIDWithGrad()) && userModel.getMpayProfileIDWithGrad().contains(":")) {
				 userModel.setMpayProfileID((userModel.getMpayProfileIDWithGrad()).split(":")[1]);
			 } else {
				 userModel.setMpayProfileID("");
			 }

			 if (!BTSLUtil.isNullString(userModel.getMpayProfileID()) && !BTSLUtil.isNullString(userModel.getUserGradeId())) {
				 userModel.setMpayProfileIDWithGrad(userModel.getUserGradeId() + ":" + userModel.getMpayProfileID());
			 }

			 userModel.setMpayProfileDesc((BTSLUtil.getOptionDesc(userModel.getMpayProfileIDWithGrad(), userModel.getMpayProfileList())).getLabel());

		 }
	 }
	
	 @Override
	 public boolean processProfileAssociation(UserModel userModel, UserVO sessionUserVO, Model model, BindingResult bindingResult){

		 final String methodName = "processProfileAssociation";
		 if (log.isDebugEnabled()) {
			 log.debug(methodName, PretupsI.ENTERED);
		 }
	
		 
		 Connection con = null;
		 MComConnectionI mcomCon = null;
		 ChannelUserWebDAO channelUserWebDAO = null;

		 try {

			 CommonValidator commonValidator=new CommonValidator(VALIDATOR_ASSOCIATE_USER_SUBMIT, userModel, "UserModelSubmit");
			 Map<String, String> errorMessages = commonValidator.validateModel();
			 PretupsRestUtil pru=new PretupsRestUtil();
			 pru.processFieldError(errorMessages, bindingResult); 

			 if(bindingResult.hasFieldErrors()){

				 return false;
			 }
			 channelUserWebDAO = new ChannelUserWebDAO();

			 final UserWebDAO userwebDAO = new UserWebDAO();
			 final ChannelUserVO channelUserVO = new ChannelUserVO();
			 mcomCon = new MComConnection();
			 con = mcomCon.getConnection();
			 final Date currentDate = new Date();

			 channelUserVO.setNetworkID(sessionUserVO.getNetworkID());
			 channelUserVO.setUserID(userModel.getUserId());
			 channelUserVO.setUserName(userModel.getChannelUserName());
			 channelUserVO.setLastModified(userModel.getLastModified());
			 channelUserVO.setModifiedBy(sessionUserVO.getActiveUserID());
			 channelUserVO.setModifiedOn(currentDate);
			 if (!BTSLUtil.isNullString(userModel.getRsaAuthentication())) {
				 channelUserVO.setSsn(userModel.getSsn());
				 channelUserVO.setRsaFlag(userModel.getRsaAuthentication());
				 final int updateCounter = userwebDAO.updateUserForRsaAuthentication(con, channelUserVO);
				 if (updateCounter <= 0) {
					 con.rollback();
					 log.error(methodName, "Error: while Updating User For Associate Profile");
					 model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString(ERROR_GENERAL)); 
					 return false;
				 }
			 }

			 String tempUserId = channelUserVO.getUserID();
			 String[] parts = tempUserId.split("\\(");
			 if(parts.length == 2){
				 channelUserVO.setUserID(parts[1].split("\\)")[0]);
			 }
			 final int updateCount = userwebDAO.updateUserForAssociate(con, channelUserVO);
			 if(parts.length == 2){
				 channelUserVO.setUserID(tempUserId);
			 }
			 if (updateCount <= 0) {
				 con.rollback();
				 log.error(methodName, "Error: while Updating User For Associate Profile");
				 model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString(ERROR_GENERAL)); 
				 return false;
			 }

			 final ChannelUserDAO channelUserDAO = new ChannelUserDAO();

			 channelUserVO.setUserGrade(userModel.getUserGradeId());
			 channelUserVO.setTransferProfileID(userModel.getTrannferProfileId());
			 channelUserVO.setCommissionProfileSetID(userModel.getCommissionProfileSetId().split(" ")[0]);
			 if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
				 channelUserVO.setLmsProfile(userModel.getLmsProfileId());
				 channelUserVO.setControlGroup(userModel.getControlGroup());
			 }

			 if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW))).booleanValue()) {
				 channelUserVO.setTrannferRuleTypeId(userModel.getTrannferRuleTypeId());
			 }
			 final String paymentNumber = "";


			 channelUserVO.setMcommerceServiceAllow(PretupsI.RESET_CHECKBOX);
			 channelUserVO.setMsisdn(userModel.getMsisdn());
			 channelUserVO.setMpayProfileID("");


			 if (log.isDebugEnabled()) {
				 log.debug(methodName, "userModel.getLmsProfileId() = " + userModel.getLmsProfileId() + ", channelUserVO.getLmsProfile() = " + channelUserVO
						 .getLmsProfile());
			 }
			 double targetCount = 0d;
			 double controlCount = 0d;
			 int retval4Association = 0;
			 int retval4deAssociation = 0;
			 int retvalTargetControl = 0;
			 Map<String, Double> countOfUsersInTargetControlGroup = null;
			 double targetCountOfassprofile = 0d;
			 double controlCountOfassprofile = 0d;
			 int retval4AssociationOfassprofile = 0;
			 int retval4deAssociationOfassprofile = 0;
			 int retvalTargetControlOfassprofile = 0;
			 Map<String, Double> countOfUsersInTargetControlGroupOfassprofile = null;
			 ChannelUserVO channelUserLMSVO = null;
			 if (!BTSLUtil.isNullString(userModel.getLmsProfileId()) && !BTSLUtil.isNullString(channelUserVO.getLmsProfile())) {
				 countOfUsersInTargetControlGroup = channelUserDAO.countOfUsersInTargetControlGroup(con, channelUserVO.getLmsProfile());
				 if (countOfUsersInTargetControlGroup != null) {
					 controlCount = countOfUsersInTargetControlGroup.get(CONTROL_CNT);
					 targetCount = countOfUsersInTargetControlGroup.get(TGT_CNT);
					 retval4Association = Double.compare(targetCount, 0d);
					 retval4deAssociation = Double.compare(controlCount, 0d);
					 retvalTargetControl = Double.compare(targetCount, controlCount);
					 if (log.isDebugEnabled()) {
						 log.debug(methodName,
								 CONTROL_COUNT + controlCount + TARGET_COUNT + targetCount + RETVAL_4_ASSOCIATION + retval4Association + RETVAL_4_DEASSOCIATION + retval4deAssociation + RETVAL_TARGET_DEASSOCIATION + retvalTargetControl);
					 }
				 }
				 channelUserLMSVO = channelUserDAO.loadChannelUser(con, channelUserVO.getUserID());
				 if (log.isDebugEnabled()) {
					 log.debug(methodName, "Already associated with lms profile :: channelUserLMSVO.getControlGroup() = " + channelUserLMSVO.getControlGroup());
				 }
				 if (!BTSLUtil.isNullString(channelUserLMSVO.getLmsProfile()) && !BTSLUtil.isNullString(channelUserLMSVO.getControlGroup()) && PretupsI.NO
						 .equalsIgnoreCase(channelUserLMSVO.getControlGroup())) {
					 countOfUsersInTargetControlGroupOfassprofile = channelUserDAO.countOfUsersInTargetControlGroup(con, channelUserLMSVO.getLmsProfile());
					 if (countOfUsersInTargetControlGroupOfassprofile != null) {
						 controlCountOfassprofile = countOfUsersInTargetControlGroupOfassprofile.get(CONTROL_CNT);
						 targetCountOfassprofile = countOfUsersInTargetControlGroupOfassprofile.get(TGT_CNT);
						 retval4AssociationOfassprofile = Double.compare(targetCountOfassprofile, 1d);
						 retval4deAssociationOfassprofile = Double.compare(controlCountOfassprofile, 1d);
						 retvalTargetControlOfassprofile = Double.compare(targetCountOfassprofile, controlCountOfassprofile);
						 if (log.isDebugEnabled()) {
							 log.debug(methodName,
									 "Already associated with lms profile" + channelUserLMSVO.getLmsProfile() + " :: control_count = " + controlCountOfassprofile + TARGET_COUNT + targetCountOfassprofile + RETVAL_4_ASSOCIATION + retval4AssociationOfassprofile + RETVAL_4_DEASSOCIATION + retval4deAssociationOfassprofile + RETVAL_TARGET_DEASSOCIATION + retvalTargetControlOfassprofile);
						 }
						 if (retvalTargetControlOfassprofile < 0 || (retval4deAssociationOfassprofile == 0 && retval4AssociationOfassprofile == 0)) {
							 if (log.isDebugEnabled()) {
								 log.debug(methodName,
										 "Already associated with lms profile :: User de-association is not allowded from target group as one user still exists into the control group of already associated profile");
							 }
							 final String[] arr = { channelUserVO.getUserName(), paymentNumber };
							 final String key = "pretups.user.associatechanneluser.alreadyassociated.oneuserstillexistsintocontrolgroup";
							 model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString(key, arr)); 
							 return false;
						 } else if (retvalTargetControlOfassprofile > 0 || (retval4AssociationOfassprofile > 1 && retval4deAssociationOfassprofile < 0)) {
							 if (log.isDebugEnabled()) {
								 log.debug(methodName,
										 "Already associated with lms profile :: User Association is allowded into the target group as no such user into the control group of already associate profile");
							 }
						 }
					 }
				 }

				 if (BTSLUtil.isNullString(channelUserVO.getControlGroup())) {
					 if (log.isDebugEnabled()) {
						 log.debug(methodName, "The value of control group is missing");
					 }
					 final String[] arr = { channelUserVO.getUserName(), paymentNumber };
					 final String key = "pretups.user.associatechanneluser.updatecontrollednotfound";
					 model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString(key, arr)); 
					 return false;
				 } else if (PretupsI.YES.equalsIgnoreCase(channelUserVO.getControlGroup())) {
					 if (retval4Association >= 1) {
						 if (channelUserDAO.isProfileActive(channelUserVO.getMsisdn(), channelUserVO.getLmsProfile())) {
							 if (log.isDebugEnabled()) {
								 log.debug(methodName, "User assocition is not allowded into control group profile as profile is active");
							 }
							 final String[] arr = { channelUserVO.getUserName(), paymentNumber };
							 final String key = "pretups.user.associatechanneluser.updatecontrolledactive";
							 model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString(key, arr)); 
							 return false;
						 }
					 } else {
						 if (log.isDebugEnabled()) {
							 log.debug(methodName, "User association is not allowded into control group as  no user belong to target group of this profile");
						 }
						 final String[] arr = { channelUserVO.getUserName(), paymentNumber };
						 final String key = "pretups.user.associatechanneluser.controlgroup.oneuserrequiredtotargetgroup";
						 model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString(key, arr)); 
						 return false;
					 }
				 } else if (PretupsI.NO.equalsIgnoreCase(channelUserVO.getControlGroup())) {
					 channelUserLMSVO = channelUserDAO.loadChannelUser(con, channelUserVO.getUserID());
					 if (!BTSLUtil.isNullString(channelUserLMSVO.getLmsProfileId()) && !BTSLUtil.isNullString(channelUserLMSVO.getControlGroup()) && PretupsI.YES
							 .equalsIgnoreCase(channelUserLMSVO.getControlGroup())) {
						 countOfUsersInTargetControlGroup = channelUserDAO.countOfUsersInTargetControlGroup(con, channelUserLMSVO.getLmsProfileId());
						 if (countOfUsersInTargetControlGroup != null) {
							 controlCount = countOfUsersInTargetControlGroup.get(CONTROL_CNT);
							 targetCount = countOfUsersInTargetControlGroup.get(TGT_CNT);
							 retval4Association = Double.compare(targetCount, 0d);
							 retval4deAssociation = Double.compare(controlCount, 0d);
							 retvalTargetControl = Double.compare(targetCount, controlCount);
							 if (log.isDebugEnabled()) {
								 log.debug(methodName,
										 CONTROL_COUNT + controlCount + TARGET_COUNT + targetCount + RETVAL_4_ASSOCIATION + retval4Association + RETVAL_4_DEASSOCIATION + retval4deAssociation + RETVAL_TARGET_DEASSOCIATION + retvalTargetControl);
							 }
							 if (retvalTargetControl == 0 || retval4Association >= 0) {
								 if (log.isDebugEnabled()) {
									 log.debug(methodName,
											 "User Association is allowded into the target group as no such user into the control group of this profile");
								 }
							 } else if (retval4deAssociation <= 1) {
								 if (log.isDebugEnabled()) {
									 log.debug(methodName,
											 "User de-association is not allowded from target group as one user still exists into the control group of this profile");
								 }
								 final String[] arr = { channelUserVO.getUserName(), paymentNumber };
								 final String key = "pretups.user.associatechanneluser.targetgroup.oneuserstillexistsintocontrolgroup";
								 model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString(key, arr)); 
								 return false;
							 }
						 }
					 }
				 }
			 } else {
				 if (log.isDebugEnabled()) {
					 log.debug(methodName, "If profile was already associated with lms profile into the control group : channelUserVO.getUserID() = " + channelUserVO
							 .getUserID());
				 }
				 channelUserLMSVO = channelUserDAO.loadChannelUser(con, channelUserVO.getUserID());
				 if (log.isDebugEnabled()) {
					 log.debug(methodName, " channelUserLMSVO.getControlGroup() = " + channelUserLMSVO.getControlGroup());
				 }
				 if (!BTSLUtil.isNullString(channelUserLMSVO.getLmsProfile()) && !BTSLUtil.isNullString(channelUserLMSVO.getControlGroup())) {
					 countOfUsersInTargetControlGroup = channelUserDAO.countOfUsersInTargetControlGroup(con, channelUserLMSVO.getLmsProfile());
					 if (countOfUsersInTargetControlGroup != null) {
						 controlCount = countOfUsersInTargetControlGroup.get(CONTROL_CNT);
						 targetCount = countOfUsersInTargetControlGroup.get(TGT_CNT);
						 retval4Association = Double.compare(targetCount, 1d);
						 retval4deAssociation = Double.compare(controlCount, 1d);
						 retvalTargetControl = Double.compare(targetCount, controlCount);
						 if (log.isDebugEnabled()) {
							 log.debug(methodName,
									 CONTROL_COUNT + controlCount + TARGET_COUNT + targetCount + RETVAL_4_ASSOCIATION + retval4Association + RETVAL_4_DEASSOCIATION + retval4deAssociation + RETVAL_TARGET_DEASSOCIATION + retvalTargetControl);
						 }
						 if (retvalTargetControl > 0 || retval4Association > 0) {
							 if (log.isDebugEnabled()) {
								 log.debug(methodName, "User Association is allowded into the target group as no such user into the control group of this profile");
							 }
						 } else if (retval4deAssociation <= 1) {
							 if (log.isDebugEnabled()) {
								 log.debug(methodName,
										 "User de-association is not allowded from target group as one user still exists into the control group of this profile");
							 }
							 final String[] arr = { channelUserVO.getUserName(), paymentNumber };
							 final String key = "pretups.user.associatechanneluser.targetgroup.oneuserstillexistsintocontrolgroup";
							 model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString(key, arr)); 
							 return false;
						 }
					 }
				 }
			 }
			 String tempUsrId = channelUserVO.getUserID();
			 String[] partsS = tempUsrId.split("\\(");
			 if(partsS.length == 2){
				 channelUserVO.setUserID(partsS[1].split("\\)")[0]);
			 }
			 final int updateChannelCount = channelUserWebDAO.updateChannelUserForAssociate(con, channelUserVO);
			 if(partsS.length == 2){
				 channelUserVO.setUserID(tempUsrId);
			 }
			 
			 if (updateChannelCount <= 0) {
				 con.rollback();
				 log.error(methodName, "Error: while Updating Channel User For Associate Profile");
				 model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString(ERROR_GENERAL)); 
				 return false;
			 }


			 con.commit();
			 if (log.isDebugEnabled()) {
				 log.debug(methodName, "Enter ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD))).booleanValue()=" + ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD))).booleanValue() + " paymentNumber=" + paymentNumber);
			 }
			 final String[] arr = { channelUserVO.getUserName(), paymentNumber };
			 String key = "pretups.user.associatechanneluser.updatesuccessmessage";
			 if (!BTSLUtil.isNullString(paymentNumber)) {
				 key = "pretups.user.associatechanneluser.updatesuccessmessage.tango";
			 }


			 channelUserVO.setStatus(userModel.getStatus());
			 ChannelUserLog.log("ASSPROCHNLUSR", channelUserVO, sessionUserVO, true, null);


			 if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.REALTIME_OTF_MSGS))).booleanValue() &&((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelUserVO.getNetworkID()) || (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,channelUserVO.getNetworkID()))){
				 TargetBasedCommissionMessages tbcm =new TargetBasedCommissionMessages();
				 tbcm.loadCommissionProfileDetailsForOTFMessages(con,channelUserVO);
			 }

			 model.addAttribute("success", PretupsRestUtil.getMessageString(key, arr)); 
			 return true;  

		 } catch (Exception e) {
			 log.errorTrace(methodName, e);

		 } finally {
			 if (mcomCon != null) {
				 mcomCon.close(CLASS_NAME+"#"+methodName);
				 mcomCon = null;
			 }
			 if (log.isDebugEnabled()) {
				 log.debug(methodName, PretupsI.EXITED+methodName);
			 }
		 }

		 return true;

	 }
	 
	 
	 
	 private void loadDetails(HttpServletRequest request, UserModel userModel, Connection pCon, UserDAO pUserDAO, UserWebDAO userwebDAO, UserVO pUserVO, ChannelUserVO channelUserSessionVO) throws Exception {
	        final String methodName = "loadDetails";
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Entered");
	        }
      
	       
	        final ArrayList phoneList = pUserDAO.loadUserPhoneList(pCon, pUserVO.getUserID());
	        if (phoneList != null && !phoneList.isEmpty()) {
	        	userModel.setMsisdnList(phoneList);
	        	userModel.setOldMsisdnList(phoneList);
	        }

	        final GeographicalDomainDAO geographyDAO = new GeographicalDomainDAO();
	        
	        final ArrayList geographyList = geographyDAO.loadUserGeographyList(pCon, pUserVO.getUserID(), pUserVO.getNetworkID());
	        userModel.setGeographicalList(geographyList);

	        if(userModel.getCategoryVO() != null && !geographyList.isEmpty()){
	        
	            
	            UserGeographiesVO geographyVO;
	            if (TypesI.YES.equals(userModel.getCategoryVO().getMultipleGrphDomains())) {
	                final String[] arr = new String[geographyList.size()];
	                for (int i = 0, j = geographyList.size(); i < j; i++) {
	                    geographyVO = (UserGeographiesVO) geographyList.get(i);
	                    arr[i] = geographyVO.getGraphDomainCode();
	                    userModel.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());
	                }
	                userModel.setGeographicalCodeArray(arr);
	                userModel.setCommissionProfileList(userwebDAO.loadCommisionProfileListByCategoryIDandGeography(pCon, userModel.getCategoryCode(), channelUserSessionVO
	                                .getNetworkID(), userModel.getGeographicalCode()));

	            } else {
	                if (geographyList.size() == 1) {
	                    geographyVO = (UserGeographiesVO) geographyList.get(0);
	                    userModel.setGeographicalCode(geographyVO.getGraphDomainCode());
	                    userModel.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());
	                }
	                userModel.setCommissionProfileList(userwebDAO.loadCommisionProfileListByCategoryIDandGeography(pCon, userModel.getCategoryCode(), channelUserSessionVO
	                                .getNetworkID(), userModel.getGeographicalCode()));

	            }
	        }
	        
	        final UserRolesWebDAO rolesWebDAO = new UserRolesWebDAO();
	        final ArrayList rolesList = rolesWebDAO.loadUserRolesList(pCon, pUserVO.getUserID());

	        if (rolesList != null && !rolesList.isEmpty()) {
	            final String[] arr = new String[rolesList.size()];
	            rolesList.toArray(arr);
	            userModel.setRoleFlag(arr);
	        }
	       
	        userModel.setRolesMap(rolesWebDAO.loadRolesList(pCon, userModel.getCategoryCode()));
	        if (userModel.getRolesMap() != null && userModel.getRolesMap().size() > 0) {
	           
	            this.populateSelectedRoles(userModel);
	        } else {
	           
	            if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_USER_ROLE_TYPE_DISPLAY)).equalsIgnoreCase(PretupsI.SYSTEM)) {
	            	userModel.setRoleType("N");
	            } else if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_USER_ROLE_TYPE_DISPLAY)).equalsIgnoreCase(PretupsI.GROUP)) {
	            	userModel.setRoleType("Y");

	            } else {
	            	userModel.setRoleType("N");
	            }
	        }

	        
	        final ServicesTypeDAO servicesDAO = new ServicesTypeDAO();
	        final ArrayList serviceList = servicesDAO.loadUserServicesList(pCon, pUserVO.getUserID());
	        if (serviceList != null && !serviceList.isEmpty()) {
	            final String[] arr = new String[serviceList.size()];
	            for (int i = 0, j = serviceList.size(); i < j; i++) {
	                final ListValueVO listVO = (ListValueVO) serviceList.get(i);
	                arr[i] = listVO.getValue();
	            }
	            userModel.setServicesTypes(arr);
	        }
	       
	        userModel.setServicesList(servicesDAO.loadServicesList(pCon, pUserVO.getNetworkID(), PretupsI.C2S_MODULE, userModel.getCategoryCode(), false));

	       
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, PretupsI.EXITED);
	        }
	    }
	 
	 private void populateSelectedRoles(UserModel userModel) throws Exception {
	        final String methodName = "populateSelectedRoles";
	        if (log.isDebugEnabled()) {
	            log.debug(methodName,PretupsI.ENTERED);
	        }
	        final HashMap mp = userModel.getRolesMap();
	        final HashMap newSelectedMap = new HashMap();
	        final Iterator it = mp.entrySet().iterator();
	        String key = null;
	        ArrayList list = null;
	        ArrayList listNew = null;
	        UserRolesVO roleVO = null;
	        Map.Entry pairs = null;
	        boolean foundFlag = false;

	        while (it.hasNext()) {
	            pairs = (Map.Entry) it.next();
	            key = (String) pairs.getKey();
	            list = new ArrayList((ArrayList) pairs.getValue());
	            listNew = new ArrayList();
	            foundFlag = false;
	            if (list != null) {
	                for (int i = 0, j = list.size(); i < j; i++) {
	                    roleVO = (UserRolesVO) list.get(i);
	                    if (userModel.getRoleFlag() != null && userModel.getRoleFlag().length > 0) {
	                        for (int k = 0; k < userModel.getRoleFlag().length; k++) {
	                            if (roleVO.getRoleCode().equals(userModel.getRoleFlag()[k])) {
	                                listNew.add(roleVO);
	                                foundFlag = true;
	                                
	                                userModel.setRoleType(roleVO.getGroupRole());
	                            }
	                        }
	                    }
	                }
	            }
	            if (foundFlag) {
	                newSelectedMap.put(key, listNew);
	            }
	        }
	        if (newSelectedMap.size() > 0) {
	        	userModel.setRolesMapSelected(newSelectedMap);
	        } else {
	            
	            if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_USER_ROLE_TYPE_DISPLAY)).equalsIgnoreCase(PretupsI.SYSTEM)) {
	            	userModel.setRoleType("N");
	            } else if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_USER_ROLE_TYPE_DISPLAY)).equalsIgnoreCase(PretupsI.GROUP)) {
	            	userModel.setRoleType("Y");

	            } else {
	            	userModel.setRoleType("N");
	            }
	            userModel.setRolesMapSelected(null);
	        }

	        if (log.isDebugEnabled()) {
	            log.debug(methodName, PretupsI.EXITED);
	        }
	    }
}

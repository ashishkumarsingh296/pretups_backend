package com.client.pretups.user.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//import org.apache.struts.action.ActionForward;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonValidator;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestClient;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryGradeDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.master.businesslogic.LocaleMasterDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.SubLookUpDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.businesslogic.UserWebDAO;
import com.web.user.web.UserModel;

@Service("userProfileThresholdService")
public class UserProfileThresholdServiceImpl implements
		UserProfileThresholdService {

	public static final Log _log = LogFactory
			.getLog(UserProfileThresholdServiceImpl.class.getName());
	private static final String PANEL_NAME = "formNumber";
	private static final String FAIL_KEY = "fail";
	private static final String EXIT = "Exiting";
	private static final String ENTER = "Entered";
	private static final String COMMON_XML = "configfiles/user/validator-userProfileThreshold.xml";
	@Autowired
	private PretupsRestClient pretupsRestClient;

	/**
	 * * Load Domain for Channel User
	 * * @return List The list of lookup filtered from DB
	 * * @throws IOException, Exception
	 * */

	@Override
	@SuppressWarnings("unchecked")
	public List<ListValueVO> loadDomain() throws BTSLBaseException {
		final String methodName = "loadDomains";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, ENTER);
		}

		DomainDAO domainDAO = new DomainDAO();
		Connection con = OracleUtil.getConnection();
		List<ListValueVO> list = domainDAO.loadDomainList(con, "dwada");

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, EXIT);
		}
		return list;
	}

	@Override
	public List<CategoryVO> loadCategory(String domain, String networkId) {

		if (_log.isDebugEnabled()) {
			_log.debug("UserProfileThresholdServiceImpl#loadCategory",
					PretupsI.ENTERED);
		}

		UserModel userModel = new UserModel();
		userModel.setDomainCode(domain);
		userModel.setDomainCodeDesc(networkId);
		userModel.setDomainShowFlag(true);
		Connection connection = null;
		CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
		ArrayList<CategoryVO> categoryList = null;
		try {
			connection = OracleUtil.getConnection();
			categoryList = categoryWebDAO.loadCategorListByDomainCode(
					connection, domain);

		} catch (Exception e) {

			_log.errorTrace("loadCategory", e);
		} finally {
			OracleUtil.closeQuietly(connection);
		}
		if (_log.isDebugEnabled()) {
			_log.debug("UserBalanceServiceImpl#loadCategory", PretupsI.EXITED);
		}

		return categoryList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserVO> loadUserList(ChannelUserVO channelUserVO,
			HttpServletRequest request, Model model, UserModel userModel,
			String ownerId, String geodomainCode, String sequenceNo) {

		String status = null;
		String statusUsed = null;

		StringBuilder statusSbf = null;
		StringBuilder statusUsedSbf = null;

		statusSbf = new StringBuilder();
		statusUsedSbf = new StringBuilder();
		ArrayList<UserVO> userList = new ArrayList<>();
		final UserModel theForm = userModel;
		Connection con = null;

		try {
			final UserWebDAO userwebDAO = new UserWebDAO();

			con = OracleUtil.getConnection();

			int sequence = Integer.parseInt(sequenceNo);

			if (sequence == 1) {

				this.loadOwnerUserStatsInfo(theForm, statusSbf, statusUsedSbf);
				status = statusSbf.toString();
				statusUsed = statusUsedSbf.toString();

				userList = userwebDAO.loadOwnerUserList(con, geodomainCode, "%"
						+ userModel.getUserName() + "%",
						userModel.getDomainCode(), statusUsed, status);

			}

			else {
				if (ownerId != null) {
					statusSbf = new StringBuilder();
					statusUsedSbf = new StringBuilder();
					this.loadChannelUserStatsInfo(statusSbf, statusUsedSbf);
					status = statusSbf.toString();
					statusUsed = statusUsedSbf.toString();

					if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserVO
							.getDomainID())) {

						userList = userwebDAO.loadUsersListByNameAndOwnerId(
								con, userModel.getCategoryCode(), "%"
										+ userModel.getUserName() + "%",
								ownerId, null, statusUsed, status, "CHANNEL");
					}

					else {

						String userID = channelUserVO.getUserID();
						userList = userwebDAO.loadUsersListByNameAndOwnerId(
								con, userModel.getCategoryCode(), "%"
										+ userModel.getUserName() + "%",
								ownerId, userID, statusUsed, status, "CHANNEL");

					}
				}

				else {
					final CategoryVO categoryVO = (CategoryVO) theForm
							.getSearchList().get(0);
					final String[] str = { categoryVO.getCategoryName() };
					model.addAttribute(
							FAIL_KEY,
							PretupsRestUtil
									.getMessageString(
											"user.selectparentuser.error.previoussearch",
											str));
				}
			}
		}

		catch (BTSLBaseException e) {

			_log.errorTrace("Exception", e);

		} finally {
			OracleUtil.closeQuietly(con);
		}

		return userList;
	}

	@Override
	public List<UserVO> loadParentUserList(HttpServletRequest request,
			UserModel userModel, ChannelUserVO channelUserVO,
			String categorycode, String ownName, String geodomaincode,
			String domainCode) {

		final String methodName = "loadParentUser";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, ENTER);
		}

		Connection con = null;
		ArrayList userList = new ArrayList();

		try {
			final UserWebDAO userwebDAO = new UserWebDAO();
			final UserModel theForm = userModel;
			con = OracleUtil.getConnection();
			final UserDAO userDAO = new UserDAO();
			if (!BTSLUtil.isNullString(ownName)) {

				String status = null;
				String statusUsed = null;

				StringBuilder statusSbf = null;
				StringBuilder statusUsedSbf = null;

				statusSbf = new StringBuilder();
				statusUsedSbf = new StringBuilder();

				this.loadOwnerUserStatsInfo(theForm, statusSbf, statusUsedSbf);
				status = statusSbf.toString();
				statusUsed = statusUsedSbf.toString();

				userList = userwebDAO.loadOwnerUserList(con, geodomaincode, "%"
						+ ownName + "%", domainCode, statusUsed, status);

			}

		} catch (Exception e) {
			_log.errorTrace("Exception", e);

		} finally {
			OracleUtil.closeQuietly(con);
		}

		return userList;

	}

	private void loadChannelUserStatsInfo(StringBuilder pstatus,
			StringBuilder pstatusUsed) {

		pstatus.append(PretupsBL.userStatusNotIn());
		pstatusUsed.append(PretupsI.STATUS_NOTIN);

	}

	@Override
	public boolean loadUserProfile(Model model, UserModel userModel,
			ChannelUserVO channelUserSessionVO, HttpServletRequest request,
			HttpServletResponse response, BindingResult bindingResult) {
		final String methodName = "loadUserProfile";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, ENTER);
		}
		//ActionForward forward = null;
		Connection con = null;
		OperatorUtilI operatorUtili = null;
		ChannelUserWebDAO channelUserWebDAO = null;
		final UserModel theForm = userModel;
		try {

			final String utilClass = (String) PreferenceCache
					.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
			operatorUtili = (OperatorUtilI) Class.forName(utilClass)
					.newInstance();
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			EventHandler.handle(
					EventIDI.SYSTEM_ERROR,
					EventComponentI.SYSTEM,
					EventStatusI.RAISED,
					EventLevelI.FATAL,
					"BatchUserUpdateAction[processUploadedFile]",
					"",
					"",
					"",
					"Exception while loading the class at the call:"
							+ e.getMessage());
		}
		try {

			if (request.getParameter("submitUsrDetails") != null) {
				CommonValidator commonValidator = new CommonValidator(
						COMMON_XML, theForm, "UserProfileThresholdMsisdn");
				Map<String, String> errorMessages = commonValidator
						.validateModel();
				PretupsRestUtil pru = new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult);
				model.addAttribute(PANEL_NAME, "Panel-Three");
				request.getSession().setAttribute(PANEL_NAME, "Panel-Three");

				if (_log.isDebugEnabled()) {
					_log.debug(methodName, "Validation : for User details ");
				}

			}

			final UserWebDAO userwebDAO = new UserWebDAO();
			channelUserWebDAO = new ChannelUserWebDAO();

			UserVO parentUserVO = null;// load the data after first search
			// perform
			UserVO searchUserVO = null;// load the data after second search

			if (theForm.getOrigCategoryList() != null) {
				CategoryVO vo = null;

				for (int i = 0, j = theForm.getOrigCategoryList().size(); i < j; i++) {
					vo = (CategoryVO) theForm.getOrigCategoryList().get(i);
					if (vo.getCategoryCode().equalsIgnoreCase(
							theForm.getCategoryCode())) {
						theForm.setCategoryVO(vo);
						break;
					}
				}
			}

			theForm.setUserCodeFlag(((Boolean) PreferenceCache
					.getSystemPreferenceValue(PretupsI.USER_CODE_REQUIRED))
					.booleanValue());
			theForm.setUserLanguageList(LocaleMasterDAO.loadLocaleMasterData());// Added

			con = OracleUtil.getConnection();
			final UserDAO userDAO = new UserDAO();

			if (BTSLUtil.isNullString(theForm.getUserLanguage())) {
				theForm.setUserLanguage((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE))
						+ "_" + (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
			}

			theForm.setUserNamePrefixList(LookupsCache.loadLookupDropDown(
					PretupsI.USER_NAME_PREFIX_TYPE, true));

			String status = null;
			String statusUsed = null;
			StringBuilder statusSbf = new StringBuilder();
			StringBuilder statusUsedSbf = new StringBuilder();
			this.loadOwnerUserStatsInfo(theForm, statusSbf, statusUsedSbf);
			status = statusSbf.toString();
			statusUsed = statusUsedSbf.toString();

			if (((Integer) PreferenceCache.getControlPreference(
					PreferenceI.USER_APPROVAL_LEVEL,
					channelUserSessionVO.getNetworkID(),
					theForm.getCategoryCode())).intValue() == 0
					|| !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.APPROVER_CAN_EDIT))).booleanValue()) {

				theForm.setCommissionProfileList(userwebDAO
						.loadCommisionProfileListByCategoryIDandGeography(con,
								theForm.getCategoryCode(),
								channelUserSessionVO.getNetworkID(), null));

				final CategoryGradeDAO categoryGradeDAO = new CategoryGradeDAO();

				theForm.setUserGradeList(categoryGradeDAO.loadGradeList(con,
						theForm.getCategoryCode()));

				final TransferProfileDAO profileDAO = new TransferProfileDAO();
				theForm.setTrannferProfileList(profileDAO
						.loadTransferProfileByCategoryID(con,
								channelUserSessionVO.getNetworkID(),
								theForm.getCategoryCode(),
								PretupsI.PARENT_PROFILE_ID_USER));

				final boolean isTrfRuleTypeAllow = ((Boolean) PreferenceCache
						.getControlPreference(
								PreferenceI.TRF_RULE_USER_LEVEL_ALLOW,
								channelUserSessionVO.getNetworkID(),
								theForm.getCategoryCode())).booleanValue();
				if (isTrfRuleTypeAllow) {
					theForm.setTrannferRuleTypeList(LookupsCache
							.loadLookupDropDown(
									PretupsI.TRANSFER_RULE_AT_USER_LEVEL, true));
				}

				if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
					theForm.setLmsProfileList(channelUserWebDAO
							.getLmsProfileList(con,
									channelUserSessionVO.getNetworkID()));
				}
			}

			if (TypesI.YES.equals(theForm.getCategoryVO().getOutletsAllowed())) {
				// load the outlet dropdown
				theForm.setOutletList(LookupsCache.loadLookupDropDown(
						PretupsI.OUTLET_TYPE, true));

				final SubLookUpDAO sublookupDAO = new SubLookUpDAO();
				theForm.setSubOutletList(sublookupDAO
						.loadSublookupByLookupType(con, PretupsI.OUTLET_TYPE));
			}

			if (searchUserVO == null) {

				ArrayList userVOList = null;
				/*
				 * pass sessionUserID if Channel User loggedIn else
				 * null(if Channel Admin)
				 * 
				 * In case of Channel user we are apply Connect By Prior
				 */
				if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO
						.getDomainID())) {
					userVOList = userwebDAO.loadUsersList(con,
							channelUserSessionVO.getNetworkID(), null, null,
							theForm.getUserName().split(":")[1], null, null,
							statusUsed, status);
				} else {
					String userID = channelUserSessionVO.getUserID();

					if (PretupsI.CATEGORY_TYPE_AGENT
							.equals(channelUserSessionVO.getCategoryVO()
									.getCategoryType())) {
						userID = channelUserSessionVO.getParentID();
					}
					userVOList = userwebDAO.loadUsersList(con,
							channelUserSessionVO.getNetworkID(), null, null,
							parentUserVO.getUserID(), null, userID, statusUsed,
							status);

				}

				if (userVOList.isEmpty()) {
					final String[] arr2 = { theForm.getUserName() };
					model.addAttribute(FAIL_KEY, PretupsRestUtil
							.getMessageString("user.name.is.not.valid", arr2));
					return false;

				}
				searchUserVO = (UserVO) userVOList.get(0);
			}

			loadUserCounters(theForm, request, response, searchUserVO, con,
					channelUserSessionVO);

		} catch (Exception e) {
			_log.error(methodName,  "Exception"+ e.getMessage());
        	_log.errorTrace(methodName, e);
        	
			final String[] arr2 = { theForm.getUserName() };
			model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString(
					"user.name.is.not.valid", arr2));
			return false;
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, EXIT);
			}
		}

		return true;
	}

	/**
	 * @param theForm
	 * @param statusSbf
	 * @param statusUsedSbf
	 */
	private void loadOwnerUserStatsInfo(UserModel theForm,
			StringBuilder pstatus, StringBuilder pstatusUsed) {

		final String methodName = "loadOwnerUserStatsInfo";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered theForm.getRequestType()="
					+ theForm.getRequestType() + ", p_status=" + pstatus
					+ ",p_statusUsed=" + pstatusUsed);
		}
		pstatus.append(PretupsBL.userStatusNotIn());
		pstatusUsed.append(PretupsI.STATUS_NOTIN);

	}

	public void populateSelectedRoles(UserModel form) throws Exception {
		final String methodName = "populateSelectedRoles";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, ENTER);
		}
		final UserModel theForm = form;
		final HashMap mp = theForm.getRolesMap();
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
				int listSize = list.size();
				for (int i = 0, j = listSize; i < j; i++) {
					roleVO = (UserRolesVO) list.get(i);
					if (theForm.getRoleFlag() != null
							&& theForm.getRoleFlag().length > 0) {
						int roleLength = theForm.getRoleFlag().length;
						for (int k = 0; k < roleLength; k++) {
							if (roleVO.getRoleCode().equals(
									theForm.getRoleFlag()[k])) {
								listNew.add(roleVO);
								foundFlag = true;

								theForm.setRoleType(roleVO.getGroupRole());
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
			theForm.setRolesMapSelected(newSelectedMap);
		} else {
			// by default set Role Type = N(means System Role radio button will
			// be checked in edit mode if no role assigned yet)
			if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_USER_ROLE_TYPE_DISPLAY))
					.equalsIgnoreCase(PretupsI.SYSTEM)) {
				theForm.setRoleType("N");
			} else if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_USER_ROLE_TYPE_DISPLAY))
					.equalsIgnoreCase(PretupsI.GROUP)) {
				theForm.setRoleType("Y");

			} else {
				theForm.setRoleType("N");
			}
			theForm.setRolesMapSelected(null);
		}

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, EXIT);
		}
	}

	public void loadUserCounters(UserModel form, HttpServletRequest request,
			HttpServletResponse response, UserVO puserVO, Connection pcon,
			ChannelUserVO channelUserSessionVO) {
		final String methodName = "loadUserCounters";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, ENTER);
		}
		final UserModel theForm = form;
		final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
		final TransferProfileDAO transferProfileDAO = new TransferProfileDAO();

		try {
			// the method below is used to load the balance of user
			final ArrayList userBalanceList = channelUserDAO.loadUserBalances(
					pcon, puserVO.getNetworkID(), puserVO.getNetworkID(),
					puserVO.getUserID());
			int userBalanceListSize = userBalanceList.size();
			// the method below is used to load the current counters of the user
			UserTransferCountsVO userTransferCountsVO = userTransferCountsDAO
					.loadTransferCounts(pcon, puserVO.getUserID(), false);
			if (userTransferCountsVO == null) {
				userTransferCountsVO = new UserTransferCountsVO();
			}
			final Date pCurrentDate = new Date(System.currentTimeMillis());
			ChannelTransferBL.checkResetCountersAfterPeriodChange(
					userTransferCountsVO, pCurrentDate);
			theForm.setUserTransferCountsVO(userTransferCountsVO);

			// load the profile details of the user
			final ChannelUserVO channelUserVO = channelUserDAO.loadChannelUser(
					pcon, puserVO.getUserID());
			if (BTSLUtil.isNullString(channelUserVO.getTransferProfileID())) {
				throw new BTSLBaseException(
						this,
						methodName,
						"user.selectchanneluserforviewcounters.msg.noprofileassociated",
						"selectChannelUserForViewCounters");
			}
			// load the profile counters of the user
			final TransferProfileVO transferProfileVO = transferProfileDAO
					.loadTransferProfile(pcon,
							channelUserVO.getTransferProfileID(),
							puserVO.getNetworkID(), true);
			int profileProductListSize = transferProfileVO.getProfileProductList().size();
			if (transferProfileVO != null) {

				transferProfileVO
						.setStatus((BTSLUtil.getOptionDesc(transferProfileVO
								.getStatus(), LookupsCache.loadLookupDropDown(
								PretupsI.STATUS_TYPE, true)).getLabel()));

				theForm.setTransferProfileVO(transferProfileVO);
				
				// map the balance with product
				if (userBalanceList != null && !(userBalanceList.isEmpty())) {
					for (int index1 = 0; index1 < profileProductListSize; index1++) {
						
						for (int index = 0; index < userBalanceListSize; index++) {
							if (((UserBalancesVO) userBalanceList.get(index))
									.getProductCode()
									.equals(((TransferProfileProductVO) transferProfileVO
											.getProfileProductList()
											.get(index1)).getProductCode())) {

								((TransferProfileProductVO) transferProfileVO
										.getProfileProductList().get(index1))
										.setCurrentBalance(PretupsBL
												.getDisplayAmount(((UserBalancesVO) userBalanceList
														.get(index))
														.getBalance()));
								break;
							} else {
								((TransferProfileProductVO) transferProfileVO
										.getProfileProductList().get(index1))
										.setCurrentBalance("0");
							}
						}
					}
				} else {
					for (int index1 = 0; index1 < profileProductListSize; index1++) {
						((TransferProfileProductVO) transferProfileVO
								.getProfileProductList().get(index1))
								.setCurrentBalance("0");
					}
				}

			} else {
				throw new BTSLBaseException(this, methodName,
						"batchfoc.processuploadedfile.error.trfprfsuspended",
						"selectChannelUserForViewCounters");
			}
			// SubscriberOutCountFlag keep tracks of either subscriber out count
			// is allowed or not
			final boolean subscriberOutcount = ((Boolean) PreferenceCache
					.getSystemPreferenceValue(PretupsI.SUBSCRIBER_TRANSFER_OUTCOUNT))
					.booleanValue();
			theForm.setSubscriberOutCountFlag(subscriberOutcount);

			theForm.setCategoryVO(puserVO.getCategoryVO());
			theForm.setCategoryCode(puserVO.getCategoryVO().getCategoryCode());

			theForm.setCategoryCodeDesc(puserVO.getCategoryVO()
					.getCategoryName());
			theForm.setChannelCategoryDesc(puserVO.getCategoryVO()
					.getCategoryName());
			theForm.setUserId(puserVO.getUserID());
			theForm.setUserName(puserVO.getUserName());
			theForm.setNetworkCode(puserVO.getNetworkID());
			theForm.setNetworkName(channelUserSessionVO.getNetworkName());
			theForm.setWebLoginID(puserVO.getLoginID());
			theForm.setMsisdn(puserVO.getMsisdn());
			theForm.setUserType(puserVO.getUserType());
			theForm.setUserCode(puserVO.getUserCode());
			// unctrlTransferFlag is used to keep track of either the
			// uncontrolled transfer is allowed or not
			if (PretupsI.YES.equals(puserVO.getCategoryVO()
					.getUnctrlTransferAllowed())) {
				theForm.setUnctrlTransferFlag(true);
			} else {
				theForm.setUnctrlTransferFlag(false);
			}

		} catch (Exception e) {
			_log.errorTrace(methodName, e);

		}

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, EXIT);
		}

	}

	@Override
	public boolean loadUserProfileByMobileNo(Model model, UserModel userModel,
			ChannelUserVO channelUserSessionVO, BindingResult bindingResult,
			HttpServletRequest request, HttpServletResponse response) {

		final String methodName = "#loadUserProfileByMobileNo";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, ENTER);
		}
		final UserModel theForm = userModel;
		Connection con = null;

		try {

			if (request.getParameter("submitMsisdn") != null) {
				CommonValidator commonValidator = new CommonValidator(
						COMMON_XML, theForm, "UserProfileThresholdMsisdn");
				Map<String, String> errorMessages = commonValidator
						.validateModel();
				PretupsRestUtil pru = new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult);
				model.addAttribute(PANEL_NAME, "Panel-One");
				request.getSession().setAttribute(PANEL_NAME, "Panel-One");

				if (_log.isDebugEnabled()) {
					_log.debug(methodName, "Validation : for mobile Number ");
				}

			}

			if (request.getParameter("submitLoginId") != null) {
				CommonValidator commonValidator = new CommonValidator(
						COMMON_XML, theForm, "UserProfileThresholdLoginId");
				Map<String, String> errorMessages = commonValidator
						.validateModel();
				PretupsRestUtil pru = new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult);
				model.addAttribute(PANEL_NAME, "Panel-Two");
				request.getSession().setAttribute(PANEL_NAME, "Panel-Two");
				if (_log.isDebugEnabled()) {
					_log.debug(methodName, "Validation : for Login ID ");
				}
			}

			if (bindingResult.hasFieldErrors()) {

				final String[] arr1 = { theForm.getSearchMsisdn(),
						channelUserSessionVO.getNetworkName() };

				model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString(
						"user.assignphone.error.msisdnnotinsamenetwork", arr1));

				return false;
			}

			theForm.setAgentBalanceList(null);
			theForm.setUserBalanceList(null);
			theForm.setViewType(null);
			final UserWebDAO userwebDAO = new UserWebDAO();
			String[] categoryID = null;

			if (theForm.getChannelCategoryCode() != null) {
				categoryID = theForm.getChannelCategoryCode().split(":");
				theForm.setCategoryCode(categoryID[0]);
				// load the dropdowns descriptions

				if (theForm.getSelectDomainList() != null
						&& theForm.getSelectDomainList().size() > 0) {
					final ListValueVO listVO = BTSLUtil.getOptionDesc(
							theForm.getDomainCode(),
							theForm.getSelectDomainList());
					theForm.setDomainCodeDesc(listVO.getLabel());
				}

				if (theForm.getOrigCategoryList() != null) {
					CategoryVO categoryVO = null;
					// parentID is the combination of categoryCode, Domain
					// Code and sequenceNo
					String[] parentID = null;
					if (theForm.getParentCategoryCode() != null) {
						parentID = theForm.getParentCategoryCode().split(":");
					}
					for (int i = 0, j = theForm.getOrigCategoryList().size(); i < j; i++) {
						categoryVO = (CategoryVO) theForm.getOrigCategoryList()
								.get(i);

						if (categoryVO.getCategoryCode().equalsIgnoreCase(
								categoryID[0])) {
							theForm.setChannelCategoryDesc(categoryVO
									.getCategoryName());
						}

						if (categoryVO.getCategoryCode().equalsIgnoreCase(
								parentID[0])) {
							theForm.setParentCategoryDesc(categoryVO
									.getCategoryName());
						}
					}
				}
				// set the domain dropdown value
				if (theForm.getAssociatedGeographicalList() != null
						&& theForm.getAssociatedGeographicalList().size() > 0) {
					UserGeographiesVO geographyVO = null;
					for (int i = 0, j = theForm.getAssociatedGeographicalList()
							.size(); i < j; i++) {
						geographyVO = (UserGeographiesVO) theForm
								.getAssociatedGeographicalList().get(i);
						if (geographyVO.getGraphDomainCode().equals(
								theForm.getParentDomainCode())) {
							theForm.setParentDomainDesc(geographyVO
									.getGraphDomainName());
							theForm.setParentDomainTypeDesc(geographyVO
									.getGraphDomainTypeName());
							break;
						}
					}
				}
			}

			String status = null;
			String statusUsed = null;
			String forwardJsp = null;
			status = PretupsBL.userStatusNotIn();
			statusUsed = PretupsI.STATUS_NOTIN;
			final HashMap map = new HashMap();
			String[] arr = null;

			if (!BTSLUtil.isNullString(theForm.getSearchMsisdn()))// load user
			// deatils by
			// MSISDN
			{
				/*
				 * Search Criteria = M (means user search through Mobile Number)
				 */
				theForm.setSearchCriteria("M");
				// check for msisdn belongs to same network or not
				final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache
						.getObject(PretupsBL.getMSISDNPrefix(PretupsBL
								.getFilteredMSISDN(theForm.getSearchMsisdn())));
				if (prefixVO == null
						|| !prefixVO.getNetworkCode().equals(
								channelUserSessionVO.getNetworkID())) {
					final String[] arr1 = { theForm.getSearchMsisdn(),
							channelUserSessionVO.getNetworkName() };
					_log.error(
							methodName,
							"Error: MSISDN Number" + theForm.getSearchMsisdn()
									+ " not belongs to "
									+ channelUserSessionVO.getNetworkName()
									+ "network");
					/*
					 * forwardJsp value set on the Top
					 */

					model.addAttribute(
							FAIL_KEY,
							PretupsRestUtil
									.getMessageString(
											"user.assignphone.error.msisdnnotinsamenetwork",
											arr1));

					return false;
				}

				con = OracleUtil.getConnection();

				// load the user info on the basis of msisdn number
				final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
				final String filteredMSISDN = PretupsBL
						.getFilteredMSISDN(theForm.getSearchMsisdn());
				ChannelUserVO channelUserVO = null;
				/*
				 * If operator user pass userId = null
				 * but in case of channel user pass userId = session user Id
				 * 
				 * In case of channel user we need to perform a Connect By Prior
				 * becs load only the child user
				 */
				if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO
						.getDomainID())) {
					channelUserVO = channelUserDAO.loadUsersDetails(con,
							filteredMSISDN, null, statusUsed, status);
				} else {
					String userID = channelUserSessionVO.getUserID();
					// if user's category is Agent then it can see the details
					// of it's parent's chlid.
					if (PretupsI.CATEGORY_TYPE_AGENT
							.equals(channelUserSessionVO.getCategoryVO()
									.getCategoryType())) {
						userID = channelUserSessionVO.getParentID();
					}
					channelUserVO = channelUserDAO.loadUsersDetails(con,
							filteredMSISDN, userID, statusUsed, status);
				}

				if (channelUserVO == null) {
					model.addAttribute(
							FAIL_KEY,
							PretupsRestUtil
									.getMessageString("channeltransfer.chnltochnlsearchuser.usernotfound.msg"));
					return false;

				} else {
					if ("Others".equalsIgnoreCase(theForm.getViewType())
							&& channelUserSessionVO
									.getCategoryVO()
									.getCategoryCode()
									.equals(channelUserVO.getCategoryVO()
											.getCategoryCode())) {
						// check to see if the users are at same level or not
						// if they are at the same level then their category
						// code will be same

						// check the user in the same domain or not
						final String[] arr2 = { theForm.getSearchMsisdn() };
						_log.error(methodName,
								"Error: User are at the same level");
						/*
						 * forwardJsp value set on the Top
						 */

						model.addAttribute(
								FAIL_KEY,
								PretupsRestUtil
										.getMessageString(
												"user.selectchanneluserforview.error.usermsisdnatsamelevel",
												arr2));
						return false;

					}

					// check for searched user is exist in the same domain or
					// not
					if (theForm.getSelectDomainList() != null) {
						final boolean isDomainFlag = this.isExistDomain(
								theForm.getSelectDomainList(), channelUserVO);
						if (!isDomainFlag) {
							// check the user in the same domain or not
							final String arr2[] = { theForm.getSearchMsisdn() };

							model.addAttribute(
									FAIL_KEY,
									PretupsRestUtil
											.getMessageString("user.selectchanneluserforview.error.usermsisdnnotinsamedomain"));
							return false;
						}
					}

					loadUserCounters(theForm, request, response, channelUserVO,
							con, channelUserSessionVO);

				}

			} else if (!BTSLUtil.isNullString(theForm.getSearchLoginId()))// load
			// user
			// deatils
			// by
			// LoginId
			{
				/*
				 * Search Criteria = L (means user search through Login Id)
				 */
				theForm.setSearchCriteria("L");
				con = OracleUtil.getConnection();
				// load the user info on the basis of LoginId number
				final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
				ChannelUserVO channelUserVO = null;

				/*
				 * If operator user pass userId = null
				 * but in case of channel user pass userId = session user Id
				 * 
				 * In case of channel user we need to perform a Connect By Prior
				 * becs load only the child user
				 */
				if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO
						.getDomainID())) {
					channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(
							con, theForm.getSearchLoginId(), null, statusUsed,
							status);
				} else {
					if ("Self".equals(theForm.getViewType())) {
						channelUserVO = channelUserDAO
								.loadUsersDetailsByLoginId(con,
										theForm.getSearchLoginId(), null,
										statusUsed, status);
					} else {
						String userID = channelUserSessionVO.getUserID();
						if (PretupsI.CATEGORY_TYPE_AGENT
								.equals(channelUserSessionVO.getCategoryVO()
										.getCategoryType())) {
							userID = channelUserSessionVO.getParentID();
						}
						channelUserVO = channelUserDAO
								.loadUsersDetailsByLoginId(con,
										theForm.getSearchLoginId(), userID,
										statusUsed, status);

					}
				}

				if (channelUserVO != null) {
					if (PretupsI.STAFF_USER_TYPE.equals(channelUserVO
							.getUserType())) {
						// throw exception no user exist with this Login Id
						final String[] arr2 = { theForm.getSearchLoginId() };
						_log.error(methodName, "Error: User not exist");

						model.addAttribute(
								FAIL_KEY,
								PretupsRestUtil
										.getMessageString(
												"user.selectchanneluserforview.error.userloginidnotexist",
												arr2));
						return false;
					}
					if ("Others".equalsIgnoreCase(theForm.getViewType())
							&& channelUserSessionVO
									.getCategoryVO()
									.getCategoryCode()
									.equals(channelUserVO.getCategoryVO()
											.getCategoryCode())) {

						// check to see if the users are at same level or not
						// if they are at the same level then their category
						// code will be same

						// check the user in the same domain or not
						final String[] arr2 = { theForm.getSearchLoginId() };
						_log.error(methodName,
								"Error: User are at the same level");

						model.addAttribute(
								FAIL_KEY,
								PretupsRestUtil
										.getMessageString(
												"user.selectchanneluserforview.error.userloginidatsamelevel",
												arr2));
						return false;

					}
					// check for searched user is exist in the same domain or
					// not
					if (theForm.getSelectDomainList() != null) {
						final boolean isDomainFlag = this.isExistDomain(
								theForm.getSelectDomainList(), channelUserVO);
						if (!isDomainFlag) {
							// check the user in the same domain or not
							final String[] arr2 = { theForm.getSearchLoginId() };
							_log.debug(methodName,
									"Error: User not in the same domain");

							model.addAttribute(
									FAIL_KEY,
									PretupsRestUtil
											.getMessageString(
													"user.selectchanneluserforview.error.userloginidnotinsamedomain",
													arr2));
							return false;
						}
					}

					final boolean isDomainFlag = userwebDAO
							.isUserInSameGRPHDomain(con, channelUserVO
									.getUserID(), channelUserVO.getCategoryVO()
									.getGrphDomainType(), channelUserSessionVO
									.getUserID(), channelUserSessionVO
									.getCategoryVO().getGrphDomainType());

					if (isDomainFlag) {
						loadUserCounters(theForm, request, response,
								channelUserVO, con, channelUserSessionVO);

					} else {

						final String[] arr2 = { theForm.getSearchLoginId() };
						_log.debug(methodName,
								"Error: User not in the same domain");

						model.addAttribute(
								FAIL_KEY,
								PretupsRestUtil
										.getMessageString(
												"user.selectchanneluserforview.error.userloginidnotinsamegeodomain",
												arr2));
						return false;

					}
				} else {
					final String[] arr2 = { theForm.getSearchLoginId() };
					model.addAttribute(
							FAIL_KEY,
							PretupsRestUtil
									.getMessageString(
											"user.selectchanneluserforview.error.userloginidnotexist",
											arr2));
					return false;
				}

			} else {
				theForm.setSearchCriteria("D");
			}

		} catch (Exception e) {

			_log.debug(
					methodName,
					"Exception:e ="
							+ PretupsRestUtil.getMessageString(
									e.getMessage(),
									new String[] {
											theForm.getSearchMsisdn(),
											channelUserSessionVO
													.getNetworkName() }));

			_log.errorTrace(methodName, e);
			model.addAttribute(FAIL_KEY,
					PretupsRestUtil.getMessageString(e.getMessage()));
			return false;

		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, EXIT);
			}
		}

		return true;
	}

	private boolean isExistDomain(ArrayList pdomainList,
			ChannelUserVO pchannelUserVO) {
		final String methodName = "isExistDomain";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,
					"Entered p_domainList.size()=" + pdomainList.size()
							+ ", p_channelUserVO=" + pchannelUserVO);
		}
		if (pdomainList == null || pdomainList.isEmpty()) {
			return true;
		}
		boolean isDomainExist = false;
		try {
			ListValueVO listValueVO = null;
			for (int i = 0, j = pdomainList.size(); i < j; i++) {
				listValueVO = (ListValueVO) pdomainList.get(i);
				if (listValueVO.getValue().equals(
						pchannelUserVO.getCategoryVO()
								.getDomainCodeforCategory())) {
					isDomainExist = true;
					break;
				}
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting isDomainExist=" + isDomainExist);
		}
		return isDomainExist;
	}
}

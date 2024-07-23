package com.web.pretups.channel.user.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.CommonValidator;
import com.btsl.common.EmailSendToUser;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.network.service.ChangeNetworkServiceImpl;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserEventRemarksVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.user.service.UserBalanceServiceImpl;
import com.web.user.businesslogic.UserWebDAO;
import com.web.user.web.UserModel;

/**
 * @author ayush.abhijeet
 *
 */
@Service("changeSelfPinService")
public class ChangePinServiceImpl implements ChangePinService {

	public static final Log log = LogFactory
			.getLog(ChangeNetworkServiceImpl.class.getName());

	@Autowired
	private static final String DATA_LIST = "dataList";
	private static final String EMAIL = "email";
	private static final String MODULE = "module";
	private static final String FORM_NUMBER = "formNumber";
	private static final String CHANGE_PIN = "changePin";
	private static final String USER_SAME_LEVEL_ERROR = "Error: User are at the same level";
	private static final String USER_NOT_EXIST_ERROR = "Error: User not exist";
	private static final String USER_NOT_IN_SAME_DOMAIN_ERROR = "Error: User not in the same domain";
	private static final String VALIDATOR_CHANGE_PIN = "configfiles/user/validator-changePin.xml";
	private static final String OLD_PIN_VALIDATOR_MSG = "pretups.user.changepin.error.staffvalidatesmspinandoldpin";
	private static final String UPDATE_SUCCESS_MSG = "pretups.user.changepin.msg.updatesuccess";
	private UserBalanceServiceImpl userBalanceService;

	/**
	  * @param channelUserVO
	  * @param model
	  * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<UserPhoneVO> loadSelfPin(ChannelUserVO channelUserVO, Model model) {

		final String METHOD_NAME = "loadSelfPin";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, PretupsI.ENTERED);
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		UserDAO userDAO = null;
		UserPhoneVO phoneVO = null;
		ArrayList<UserPhoneVO> userPhoneList = null;
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			userDAO = new UserDAO();
			userPhoneList = userDAO.loadUserPhoneList(con,
					channelUserVO.getActiveUserID());



			final Iterator<UserPhoneVO> itr = userPhoneList.iterator();
			while (itr.hasNext()) {
				phoneVO = itr.next();
				phoneVO.setShowSmsPin("");
				phoneVO.setConfirmSmsPin("");
				if (PretupsI.NOT_AVAILABLE.equals(phoneVO.getMsisdn())) {
					phoneVO.setMsisdn(PretupsI.NOT_AVAILABLE_DESC);
				}
			}

			channelUserVO.setMsisdnList(userPhoneList);

			return userPhoneList;
		} catch (Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("ChangePinServiceImpl#loadSelfPin");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting");
			}
		}

		return userPhoneList;
	}

	/**
	 * @param userModel
	 * @param sessionUserVO
	 * @param model
	 * @param bindingResult
	 * @param phoneListData
	 */
	@Override
	public List<String> processData(UserModel userModel, ChannelUserVO sessionUserVO,
			Model model, BindingResult bindingResult, ArrayList<UserPhoneVO> phoneListData, HttpServletRequest request) {


		final String METHOD_NAME = "processData";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		UserDAO userDAO = null;
		UserPhoneVO phoneVO = null;
		ArrayList<UserPhoneVO> phoneList = null;
		int updateCount = 0;
		boolean flag = false;
		String smsPin = "";
		boolean pinExistance = false;
		boolean pinExistanceAll = false;
		final Date currentDate = new Date();
		UserEventRemarksVO userRemarksVO = null;
		ArrayList<UserEventRemarksVO> changePinRemarkList = null;
		List<String> errorList = new ArrayList<String>();

		try {

			for(int i =0; i < phoneListData.size(); i++){
				phoneListData.get(i).setMultiBox(userModel.getMsisdnList().get(i).getMultiBox());
				phoneListData.get(i).setOldSmsPin(userModel.getMsisdnList().get(i).getOldSmsPin());
				phoneListData.get(i).setShowSmsPin(userModel.getMsisdnList().get(i).getShowSmsPin());
				phoneListData.get(i).setConfirmSmsPin(userModel.getMsisdnList().get(i).getConfirmSmsPin());
			}	
			userModel.setMsisdnList(phoneListData);	

			final UserWebDAO userwebDAO = new UserWebDAO();
			phoneList = new ArrayList<>(userModel.getMsisdnList());

			Iterator<UserPhoneVO> phoneListIterator = phoneList.iterator();

			while (phoneListIterator.hasNext()) {
				phoneVO = phoneListIterator.next();
				if (!BTSLUtil.isNullString(phoneVO.getMultiBox())
						&& "Y".equals(phoneVO.getMultiBox())) {
					phoneVO.setModifiedBy(sessionUserVO.getActiveUserID());
					phoneVO.setModifiedOn(currentDate);
					phoneVO.setPinModifiedOn(currentDate);
					phoneVO.setPinModifyFlag(true);
					if ("Y".equalsIgnoreCase(phoneVO.getPrimaryNumber())) {
						flag = true;
						smsPin = phoneVO.getConfirmSmsPin();
					}
				} else {
					phoneListIterator.remove();
				}
			}

			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			userDAO = new UserDAO();
			// pin existance check
			final String modifificationType = PretupsI.USER_PIN_MANAGEMENT;
			final ArrayList<String> msidnlist = new ArrayList<>();

			// check for pin existance in password_history table if pin exist
			// add all existance pin to an array list and show error one by one
			for (int i = 0, j = phoneList.size(); i < j; i++) {
				phoneVO = phoneList.get(i);
				pinExistance = false;
				pinExistance = userDAO.checkPasswordHistory(con,
						modifificationType, phoneVO.getUserId(),
						phoneVO.getMsisdn(),
						BTSLUtil.encryptText(phoneVO.getShowSmsPin()));
				if (pinExistance) {
					msidnlist.add(phoneVO.getMsisdn());
					pinExistanceAll = true;
				}
			}
			if (pinExistanceAll && msidnlist != null && !msidnlist.isEmpty()) {
				
					final String[] error_msidn = msidnlist
							.toArray(new String[msidnlist.size()]);
					errorList.add(PretupsRestUtil.getMessageString(
							"pretups.user.changepin.error.oldsmspinhistory",
							error_msidn));
					return errorList;
				
			}
			
			final Iterator<UserPhoneVO> itr = phoneList.iterator();
			while (itr.hasNext()) {
				phoneVO = itr.next();
				if (!BTSLUtil.isNullString(phoneVO.getMultiBox())
						&& "Y".equals(phoneVO.getMultiBox())) {
					phoneVO.setOldSmsPin(phoneVO.getOldSmsPin());
					phoneVO.setConfirmSmsPin(phoneVO.getConfirmSmsPin());
					phoneVO.setShowSmsPin(phoneVO.getConfirmSmsPin());
				}
			}

			if(phoneList.isEmpty()){
				errorList.add(PretupsRestUtil.getMessageString("pretups.select.at.least.one.msisdn"));
				return errorList;
			}else{
				String[] msisdn = new String[1];
				String oldSmsPin = null;
				String showSmsPin = null;
				String confirmSmsPin = null;
				String smsPinValue = null;
				int  phoneLists= phoneList.size();
				for(int i = 0; i <phoneLists; i++){

					msisdn[0] = phoneList.get(i).getMsisdn();
					oldSmsPin = phoneList.get(i).getOldSmsPin();
					showSmsPin = phoneList.get(i).getShowSmsPin();
					confirmSmsPin = phoneList.get(i).getConfirmSmsPin();
					smsPinValue = phoneList.get(i).getSmsPin();
					if (PretupsI.NOT_AVAILABLE.equals(msisdn[0]) || PretupsI.NOT_AVAILABLE_DESC.equals(msisdn[0])) {
						if (BTSLUtil.isNullString(oldSmsPin)) {
							errorList.add(PretupsRestUtil.getMessageString("pretups.old.sms.pin.required", msisdn));
						}

						if ("SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
							if (!smsPinValue.equals(oldSmsPin)) {
								errorList.add(PretupsRestUtil.getMessageString(OLD_PIN_VALIDATOR_MSG, msisdn));
								return errorList;
							}
						} else {
							if (!BTSLUtil.encryptText(oldSmsPin).equalsIgnoreCase(smsPinValue)) {
								errorList.add(PretupsRestUtil.getMessageString(OLD_PIN_VALIDATOR_MSG, msisdn));
								return errorList;
							}
						}
						if (BTSLUtil.isNullString(confirmSmsPin.trim())) {
							errorList.add(PretupsRestUtil.getMessageString("pretups.confirm.sms.pin.required", msisdn));
							return errorList;
						}
						if (!BTSLUtil.isNullString(showSmsPin) && !BTSLUtil.isNumeric(showSmsPin)) {
							errorList.add(PretupsRestUtil.getMessageString("pretups.only.numeric.value.allowed.new.pin", msisdn));
							return errorList;
						}
						if (!BTSLUtil.isNullString(confirmSmsPin) && !BTSLUtil.isNumeric(confirmSmsPin)) {
							errorList.add(PretupsRestUtil.getMessageString("pretups.only.numeric.value.allowed.confirm.pin", msisdn));
							return errorList;
						}

						if (BTSLUtil.encryptText(oldSmsPin).equalsIgnoreCase(smsPinValue) && oldSmsPin.equalsIgnoreCase(showSmsPin)) {
							errorList.add(PretupsRestUtil.getMessageString("pretups.same.old.and.new.pin", msisdn));
							return errorList;
						}
						if ((!BTSLUtil.isNullString(showSmsPin) && !BTSLUtil.isNullString(confirmSmsPin)) && (!showSmsPin.equalsIgnoreCase(confirmSmsPin))) {
							errorList.add(PretupsRestUtil.getMessageString("pretups.different.new.and.confirm.pin", msisdn));
							return errorList;
						}
					} else {
						if (BTSLUtil.isNullString(oldSmsPin)) {
							errorList.add(PretupsRestUtil.getMessageString("pretups.old.sms.pin.required", msisdn));
							return errorList;
						} else {
							if ("SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
								if (!smsPinValue.equals(BTSLUtil.encryptText(oldSmsPin))) {
									errorList.add(PretupsRestUtil.getMessageString(OLD_PIN_VALIDATOR_MSG, msisdn));
									return errorList;
								}
							} else {
								if (!BTSLUtil.encryptText(oldSmsPin).equalsIgnoreCase(smsPinValue)) {
									errorList.add(PretupsRestUtil.getMessageString(OLD_PIN_VALIDATOR_MSG,msisdn));
									return errorList;
								}
							}
						}

						if (BTSLUtil.isNullString(confirmSmsPin.trim())) {
							errorList.add(PretupsRestUtil.getMessageString("pretups.confirm.sms.pin.required", msisdn));
							return errorList;
						}
						if (!BTSLUtil.isNullString(showSmsPin) && !BTSLUtil.isNumeric(showSmsPin)) {
							errorList.add(PretupsRestUtil.getMessageString("pretups.only.numeric.value.allowed.new.pin", msisdn));
							return errorList;
						}
						if (!BTSLUtil.isNullString(confirmSmsPin) && !BTSLUtil.isNumeric(confirmSmsPin)) {
							errorList.add(PretupsRestUtil.getMessageString("pretups.only.numeric.value.allowed.confirm.pin", msisdn));
							return errorList;
						}

						if (BTSLUtil.encryptText(oldSmsPin).equalsIgnoreCase(smsPinValue) && oldSmsPin.equalsIgnoreCase(showSmsPin)) {
							errorList.add(PretupsRestUtil.getMessageString("pretups.same.old.and.new.pin", msisdn));
							return errorList;
						}

						if ((!BTSLUtil.isNullString(showSmsPin) && !BTSLUtil.isNullString(confirmSmsPin)) && (!showSmsPin.equalsIgnoreCase(confirmSmsPin))) {
							errorList.add(PretupsRestUtil.getMessageString("pretups.different.new.and.confirm.pin", msisdn));
							return errorList;
						}
					}
					if (BTSLUtil.isNullString(showSmsPin)) {
						if (PretupsI.NOT_AVAILABLE.equals(msisdn[0]) || PretupsI.NOT_AVAILABLE_DESC.equals(msisdn[0])) {
							errorList.add(PretupsRestUtil.getMessageString("pretups.new.sms.pin.required"));
							return errorList;
						} else {
							errorList.add(PretupsRestUtil.getMessageString("pretups.new.sms.pin.required", msisdn));
							return errorList;
						}
					} else {
						OperatorUtil operatorUtili = new OperatorUtil();
						String webloginID = userModel.getWebLoginID();
						if (operatorUtili.isPinUserId(showSmsPin, webloginID)) {
							errorList.add(PretupsRestUtil.getMessageString("pretups.user.changepin.error.pinsameasloginid", msisdn));
							return errorList;
						}

						if (showSmsPin.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SMS_PIN_LENGTH))).intValue() || showSmsPin.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH))).intValue()) {
							final String[] args = { String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SMS_PIN_LENGTH))).intValue()), String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH))).intValue()), msisdn[0] };
							errorList.add(PretupsRestUtil.getMessageString("pretups.operatorutil.validatepin.error.pinlength", args));
							return errorList;
						}
						final int result = BTSLUtil.isSMSPinValid(showSmsPin);
						if (result == -1) {
							errorList.add(PretupsRestUtil.getMessageString("pretups.operatorutil.validatepin.error.pinsamedigits", msisdn));
							return errorList;
						} else if (result == 1) {
							errorList.add(PretupsRestUtil.getMessageString("pretups.operatorutil.validatepin.error.pinconsecutivedigits", msisdn));
							return errorList;
						}

					}
				}
			}
			if(!errorList.isEmpty()){
				return errorList;
			}
			else{
				updateCount = userwebDAO.updateSmsPin(con, phoneList);

				if (updateCount > 0) {
					int changePinCount = 0;
					changePinRemarkList = new ArrayList<>();
					userRemarksVO = new UserEventRemarksVO();
					userRemarksVO.setCreatedBy(sessionUserVO.getCreatedBy());
					userRemarksVO.setCreatedOn(new Date());
					userRemarksVO.setEventType(PretupsI.CHANGE_PIN);
					userRemarksVO.setMsisdn(phoneVO.getMsisdn());
					userRemarksVO.setRemarks(userModel.getEventRemarks());
					userRemarksVO.setUserID(phoneVO.getUserId());
					userRemarksVO.setUserType(userModel.getUserType());
					userRemarksVO.setModule(PretupsI.C2S_MODULE);
					changePinRemarkList.add(userRemarksVO);
					changePinCount = userwebDAO.insertEventRemark(con,
							changePinRemarkList);
					if (changePinCount <= 0) {
						con.rollback();
						log.error("saveDeleteSuspend",
								"Error: while inserting into userEventRemarks Table");
						throw new BTSLBaseException(this, "save",
								"pretups.error.general.processing");
					}

				}
				if (con != null && updateCount == phoneList.size()) {
					
						mcomCon.finalCommit();
						final ChannelUserVO channelUserVO = new ChannelUserVO();
						channelUserVO.setMsisdnList(phoneList);

						PushMessage pushMessage = null;
						BTSLMessages sendbtslMessage = null;
						if ("changeSmsPin".equals(userModel.getRequestType())) {
							final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));

							String subject = null;
							EmailSendToUser emailSendToUser = null;
							final ChannelUserVO tmpChnlUserVO = new ChannelUserVO();
							BeanUtils.copyProperties(tmpChnlUserVO, channelUserVO);
							for (int i = 0, j = phoneList.size(); i < j; i++) {
								phoneVO = phoneList.get(i);
								String msisdn = null;
								if (!PretupsI.NOT_AVAILABLE.equals(phoneVO.getMsisdn()) && !PretupsI.NOT_AVAILABLE_DESC.equals(phoneVO.getMsisdn())) {
									msisdn = phoneVO.getMsisdn();
									sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PIN_MODIFY, new String[] { phoneVO.getShowSmsPin() });
								} else {

									msisdn = sessionUserVO.getMsisdn();
									sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PIN_MODIFY_STAFF, new String[] { phoneVO.getShowSmsPin(), userModel
											.getSearchLoginId() });
								}


								pushMessage = new PushMessage(msisdn, sendbtslMessage, "", "", locale, sessionUserVO.getNetworkID(), null);
								pushMessage.push();

								if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(userModel.getEmail())) {
									tmpChnlUserVO.setEmail(userModel.getEmail());
									tmpChnlUserVO.setUserType("CHANNEL");
									tmpChnlUserVO.setStatus(userModel.getStatus());
									tmpChnlUserVO.setUserID(userModel.getUserId());
									tmpChnlUserVO.setModifiedOn(phoneVO.getModifiedOn());
									tmpChnlUserVO.setMsisdn(phoneVO.getMsisdn());
									subject = PretupsRestUtil.getMessageString(UPDATE_SUCCESS_MSG,
											new String[] { phoneVO.getMsisdn() });
									emailSendToUser = new EmailSendToUser(subject, sendbtslMessage, locale, sessionUserVO.getNetworkID(), "Email will be delivered shortly",
											tmpChnlUserVO, sessionUserVO);
									emailSendToUser.sendMail();
								}
							}
							for(int i =0; i < phoneList.size(); i++){
								phoneList.get(i).setSmsPin(BTSLUtil.encryptText(phoneList.get(i).getShowSmsPin()));
							}
							request.getSession().setAttribute(DATA_LIST, phoneList);
							model.addAttribute(
									"success",
									PretupsRestUtil
									.getMessageString(UPDATE_SUCCESS_MSG));

						}
						if ("changeSelfPin".equals(userModel.getRequestType())) {
							if (flag) {

								if (sessionUserVO.isStaffUser()) {
									sessionUserVO.setActiveUserPin(BTSLUtil
											.encryptText(smsPin));
								} else {
									sessionUserVO.setSmsPin(BTSLUtil
											.encryptText(smsPin));
								}
							}
							model.addAttribute(
									"success",
									PretupsRestUtil
									.getMessageString(UPDATE_SUCCESS_MSG));
						
					}
				}
			}
		} catch (Exception e) {
			try {
				if (con != null) {
					mcomCon.finalRollback();
				}
			} catch (Exception se) {
				log.errorTrace(METHOD_NAME, se);
			}
			if (mcomCon != null) {
				mcomCon.close("ChangePinServiceImpl#updateChangePin");
				mcomCon = null;
			}
			log.error("updateChangePin", "Exception:e= " + e);
			log.errorTrace(METHOD_NAME, e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("ChangePinServiceImpl#updateChangePin");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting: "+METHOD_NAME);
			}
		}
		return errorList;

	}

	/**
	 * @param model
	 * @param channelUserSessionVO
	 * @param userModel
	 * @param bindingResult
	 * @param request
	 * @return
	 */
	@Override
	public boolean changePin(Model model, ChannelUserVO channelUserSessionVO, UserModel userModel, BindingResult bindingResult, HttpServletRequest request){
		final String methodName = "#changePin";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered");
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
				CommonValidator commonValidator=new CommonValidator(VALIDATOR_CHANGE_PIN, userModel, "UserModelMsisdn");
				Map<String, String> errorMessages = commonValidator.validateModel();
				PretupsRestUtil pru=new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult);
				request.getSession().setAttribute(FORM_NUMBER, "Panel-One");
			}
			if(request.getParameter("submitLoginId")!=null){
				CommonValidator commonValidator=new CommonValidator(VALIDATOR_CHANGE_PIN, userModel, "UserModelLoginId");
				Map<String, String> errorMessages = commonValidator.validateModel();
				PretupsRestUtil pru=new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult); 
				request.getSession().setAttribute(FORM_NUMBER, "Panel-Two");
			}
			if(request.getParameter("submitUser")!=null){
				CommonValidator commonValidator=new CommonValidator(VALIDATOR_CHANGE_PIN, userModel, "UserModelUserName");
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
				model.addAttribute("fail", PretupsRestUtil
						.getMessageString("pretups.user.selectchanneluserforview.error.search.required"));
				return false;
			} else if (BTSLUtil.isNullString(userModel.getSearchMsisdn()) && BTSLUtil.isNullString(userModel.getSearchLoginId()) ) { 
				if (BTSLUtil.isNullString(userModel.getDomainCodeDesc())) {
					model.addAttribute("fail", PretupsRestUtil
							.getMessageString("pretups.user.selectchanneluserforview.error.domaincode.required"));
					return false;
				}
				if (BTSLUtil.isNullString(userModel.getChannelCategoryCode())) {
					model.addAttribute("fail", PretupsRestUtil
							.getMessageString("pretups.user.selectchanneluserforview.error.channelcategorycode.required"));
					return false;
				}

			}
			if (!BTSLUtil.isNullString(userModel.getSearchMsisdn()) && !BTSLUtil.isValidMSISDN(userModel.getSearchMsisdn())) {

				
					arr = new String[1];
					arr[0] = userModel.getSearchMsisdn();
					model.addAttribute("fail", PretupsRestUtil
							.getMessageString("pretups.changepin.msisdn.error.length", arr));
					return false;
				
			}
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_EVENT_REMARKS))).booleanValue() && BTSLUtil.isNullString(userModel.getEventRemarks())) {
				
					model.addAttribute("fail", PretupsRestUtil
							.getMessageString("pretups.user.changepin.error.remarkrequired", arr));
					return false;
				
			}


			if (!BTSLUtil.isNullString(userModel.getSearchMsisdn())){

				userModel.setSearchCriteria("M");
				final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(userModel
						.getSearchMsisdn())));
				if (prefixVO == null || !prefixVO.getNetworkCode().equals(channelUserSessionVO.getNetworkID())) {
					final String[] arr1 = { userModel.getSearchMsisdn(), channelUserSessionVO.getNetworkName() };
					log.error(methodName, "Error: MSISDN Number" + userModel.getSearchMsisdn() + " not belongs to " + channelUserSessionVO.getNetworkName() + "network");
					model.addAttribute("fail", PretupsRestUtil
							.getMessageString("pretups.user.changepin.error.msisdnnotinsamenetwork",arr1));
					return false;
				}

				mcomCon = new MComConnection();
				con=mcomCon.getConnection();
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
						model.addAttribute("fail", PretupsRestUtil
								.getMessageString("pretups.user.changepin.error.usermsisdnatsamelevel", arr2));
						return false;
					}

					if (userModel.getSelectDomainList() != null) {
						final boolean isDomainFlag = this.isExistDomain(userModel.getSelectDomainList(), channelUserVO);
						final ListValueVO listValueVO = BTSLUtil.getOptionDesc(channelUserVO.getCategoryVO().getDomainCodeforCategory(), userModel.getSelectDomainList());
						userModel.setDomainCodeDesc(listValueVO.getLabel());
						if (!isDomainFlag) {

							final String[] arr2 = { userModel.getSearchMsisdn() };
							log.error(methodName, USER_NOT_IN_SAME_DOMAIN_ERROR);
							model.addAttribute("fail", PretupsRestUtil
									.getMessageString("pretups.user.changepin.error.usermsisdnnotinsamedomain", arr2));
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
						int usersPhoneLists=userPhoneList.size();
						for(int i =0; i < usersPhoneLists; i++){
							userPhoneList.get(i).setShowSmsPin("");
							userPhoneList.get(i).setConfirmSmsPin("");
						}
						userModel.setMsisdnList(userPhoneList);
						request.getSession().setAttribute(DATA_LIST, userPhoneList);
						request.getSession().setAttribute(MODULE, CHANGE_PIN);
						request.getSession().setAttribute(EMAIL, userModel.getEmail());
						return true;
					} else if (!isGeoDomainFlag) {
						final String[] arr2 = { userModel.getSearchMsisdn() };
						log.error(methodName, USER_NOT_IN_SAME_DOMAIN_ERROR);

						model.addAttribute("fail", PretupsRestUtil
								.getMessageString("pretups.user.changepin.error.usermsisdnnotinsamegeodomain",arr2));
						return false;
					}





				} else {

					final String[] arr2 = { userModel.getSearchMsisdn() };
					log.error(methodName, USER_NOT_EXIST_ERROR);

					model.addAttribute("fail", PretupsRestUtil
							.getMessageString("pretups.user.changepin.error.usermsisdnnotexist", arr2));
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

						model.addAttribute("fail", PretupsRestUtil
								.getMessageString("pretups.user.changepin.error.userloginidatsamelevel", arr2));
						return false;
					}

					if (userModel.getSelectDomainList() != null) {
						final boolean isDomainFlag = this.isExistDomain(userModel.getSelectDomainList(), channelUserVO);
						final ListValueVO listValueVO = BTSLUtil.getOptionDesc(channelUserVO.getCategoryVO().getDomainCodeforCategory(), userModel.getSelectDomainList());
						userModel.setDomainCodeDesc(listValueVO.getLabel());
						if (!isDomainFlag) {
							final String[] arr2 = { userModel.getSearchLoginId() };
							log.error(methodName, USER_NOT_IN_SAME_DOMAIN_ERROR);

							model.addAttribute("fail", PretupsRestUtil
									.getMessageString("pretups.user.changepin.error.userloginidnotinsamedomain", arr2));
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
						int userPhoneLists=userPhoneList.size();
						for(int i =0; i < userPhoneLists; i++){
							userPhoneList.get(i).setShowSmsPin("");
							userPhoneList.get(i).setConfirmSmsPin("");
						}
						userModel.setMsisdnList(userPhoneList);
						request.getSession().setAttribute(MODULE, CHANGE_PIN);
						request.getSession().setAttribute(EMAIL, userModel.getEmail());
						return true;
					} else if (!isGeoDomainFlag) {

						final String[] arr2 = { userModel.getSearchLoginId() };
						log.error(methodName, USER_NOT_IN_SAME_DOMAIN_ERROR);

						model.addAttribute("fail", PretupsRestUtil
								.getMessageString("pretups.user.changepin.error.userloginidnotinsamegeodomain", arr2));
						return false;
					}

				} else {
					final String[] arr2 = { userModel.getSearchLoginId() };
					log.error(methodName, USER_NOT_EXIST_ERROR);
					model.addAttribute("fail", PretupsRestUtil
							.getMessageString("pretups.user.changepin.error.userloginidnotexist", arr2)); 
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
							.getMessageString("pretups.user.changepin.error.usernamenotexist", arr2));
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
				
				

	        	String index = request.getSession().getAttribute("index").toString();
	        	String prntDomainCode = request.getSession().getAttribute("prntDomainCode").toString();
				userBalanceService = new UserBalanceServiceImpl();
				
				List<UserVO> userList = userBalanceService.loadUserList(channelUserSessionVO, userModel.getChannelCategoryCode(), userModel.getOwnerID(), userName,  userModel.getDomainCodeDesc(), prntDomainCode, request, index);
			   
				if(userList.size() == 1){

					channelUserVO  = userList.get(0); 
					userId = channelUserVO.getUserID();
					boolean rsaRequired = false;
					rsaRequired = BTSLUtil.isRsaRequired(channelUserVO);
					userModel.setRsaRequired(rsaRequired);
					userModel.setUserId(userId);
					
					

				}else if(userList.size()>1){

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
					model.addAttribute("fail", PretupsRestUtil
							.getMessageString("pretups.user.changepin.error.usernamenotexist", arr2));
					return false;
				}

				if (channelUserSessionVO.getCategoryVO().getCategoryCode().equals(channelUserVO.getCategoryCode()) && !PretupsI.USER_TYPE_STAFF
						.equals(channelUserVO.getUserType())) {
					final String[] arr2 = { userModel.getUserId() };
					log.error(methodName, USER_SAME_LEVEL_ERROR);

					model.addAttribute("fail", PretupsRestUtil
							.getMessageString("pretups.user.changepin.error.usernameatsamelevel", arr2));
					userModel.setUserId(userId);
					return false;
				}


				
					userModel.setEmail(channelUserVO.getEmail());
					userPhoneList = userDAO.loadUserPhoneList(con,
							userModel.getUserId());
					for(int i =0; i < userPhoneList.size(); i++){
						userPhoneList.get(i).setShowSmsPin("");
						userPhoneList.get(i).setConfirmSmsPin("");
					}
					userModel.setMsisdnList(userPhoneList);
					request.getSession().setAttribute(MODULE, CHANGE_PIN);
					request.getSession().setAttribute(EMAIL, userModel.getEmail());
					return true;

			}else{
				model.addAttribute("fail", PretupsRestUtil
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
			log.error(methodName, "Exception:e= " + e);
			log.errorTrace(methodName, e);
		}finally {
			if (mcomCon != null) {
				mcomCon.close("ChangePinServiceImpl#changePin");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting: "+methodName);
			}
		}
		return true;        
	}

	/**
	 * 
	 * @param p_domainList
	 * @param p_channelUserVO
	 * @return
	 * @throws Exception
	 */
	private boolean isExistDomain(ArrayList p_domainList, UserVO p_channelUserVO) {
		final String methodName = "isExistDomain";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered p_domainList.size()=" + p_domainList.size() + ", p_channelUserVO=" + p_channelUserVO);
		}
		if (p_domainList == null || p_domainList.isEmpty()) {
			return true;
		}
		boolean isDomainExist = false;
		try {
			ListValueVO listValueVO;
			for (int i = 0, j = p_domainList.size(); i < j; i++) {
				listValueVO = (ListValueVO) p_domainList.get(i);
				if (listValueVO.getValue().equals(p_channelUserVO.getCategoryVO().getDomainCodeforCategory())) {
					isDomainExist = true;
					break;
				}
			}
		} catch (Exception e) {
			log.errorTrace(methodName, e);
			
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting isDomainExist=" + isDomainExist);
		}
		return isDomainExist;
	}
	
}

package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.routing.subscribermgmt.businesslogic.NumberPortDAO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.ExtUserDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

/**
 * @description : This controller class will be used to process the modify
 *              request for user through external system via operator receiver.
 * @author : diwakar
 */

public class UserModifyController implements ServiceKeywordControllerI {
    private Log _log = LogFactory.getLog(UserModifyController.class.getName());
    private ChannelUserVO _channelUserVO = null;
    private UserDAO _userDAO = null;
    private ChannelUserDAO _channelUserDao = null;
    private ChannelUserWebDAO _channelUserWebDao = null;
    private ExtUserDAO _extUserDao = null;
    private ChannelUserVO modifiesChannelUserVO = null;
    private ChannelUserVO _senderVO = null;

    /**
     * Method Process
     * Process Method , Processes external channel user registration request
     * 
     * @param p_requestVO
     */
	public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("UserModifyController process", "Entered p_requestVO=" + p_requestVO);
        }
        Connection con = null;MComConnectionI mcomCon = null;
        final HashMap requestMap = p_requestVO.getRequestMap();
        _channelUserDao = new ChannelUserDAO();
        _channelUserWebDao = new ChannelUserWebDAO();
        _channelUserVO = new ChannelUserVO();
        _userDAO = new UserDAO();
        _extUserDao = new ExtUserDAO();
        OperatorUtilI operatorUtili = null;
        final String msg[] = new String[1];
        Locale locale = null;
        ArrayList oldPhoneList = null;
        String senderPin = "";
        try {
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserModifyController[process]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }

        try {
        	mcomCon = new MComConnection();con=mcomCon.getConnection();

            _senderVO = (ChannelUserVO) p_requestVO.getSenderVO();

            // Validation for Channel ADMIN.if BCU is the category code i.e.
            // User is Channel administrator OPERATOR_CATEGORY and External Code
            // exists,
            // MDISDN and SMS pin is valid or not.
            String userMsisdn = (String) requestMap.get("USERMSISDN");

            userMsisdn = PretupsBL.getFilteredMSISDN(userMsisdn);
            if (BTSLUtil.isNullString(userMsisdn)) {
                throw new BTSLBaseException("UserModifyController", "process", PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_BLANK);
            }

            UserVO requestUserVO = null;
            locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));

            // Load user details for modification.
            requestUserVO = new UserDAO().loadUsersDetails(con, userMsisdn);

            // If no user details found for the user, throw an exception.
            if (requestUserVO == null) {
                throw new BTSLBaseException("UserModifyController", "process", PretupsErrorCodesI.ERROR_USER_NOT_EXIST);
            }

            // Get category code
            String catCode = requestUserVO.getCategoryCode();

            // get User Id
            String userId = requestUserVO.getUserID();
            userId = userId.trim();
            catCode = catCode.trim();

            // Rest validation
            // Check If Channel ADMIN
            if (catCode.equals(PretupsI.OPERATOR_CATEGORY)) {
                // Login id validation
                final String loginId = (String) requestMap.get("LOGINID");
                final String password = (String) requestMap.get("PASSWORD");
                if (!BTSLUtil.isNullString(loginId)) {
                    final HashMap errorMessageMap = operatorUtili.validatePassword(loginId, password);
                    if (null != errorMessageMap && errorMessageMap.size() > 0) {
                        throw new BTSLBaseException("UserModifyController", "process", PretupsErrorCodesI.ERROR_ERP_CHNL_USER_INVALID_PASSWORD);
                    }
                }
            }
            // Requester Validation Ends
            // Load details of channel user to be modified
            locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            String userExtCode = (String) requestMap.get("EXTERNALCODE");
            String modifiedUserMsisdn = (String) requestMap.get("USERMSISDN");
            String modifiedUserLoginId = (String) requestMap.get("WEBLOGINID");
            modifiedUserMsisdn = PretupsBL.getFilteredMSISDN(modifiedUserMsisdn);
            userExtCode = userExtCode.trim();
            modifiedUserLoginId = modifiedUserLoginId.trim();
            // Load Channel User on basis of Primary MSISDN only:
            modifiesChannelUserVO = _extUserDao.loadChannelUserDetailsByMsisdnLoginIdExt(con, modifiedUserMsisdn, null, null, userExtCode, locale);
            if (!(modifiesChannelUserVO == null)) {
                oldPhoneList = _userDAO.loadUserPhoneList(con, modifiesChannelUserVO.getUserID());
                _channelUserVO = modifiesChannelUserVO;
            } else {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_RECEIVER_DETAILS_INVALID); // Message
                // Changes
                // on
                // 11-MAR-2014
            }

            if(!BTSLUtil.isNullString(userExtCode)&&!modifiesChannelUserVO.getExternalCode().equals(userExtCode)){
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERROR_ERP_CHNL_USER_INVALID_EXTCODE);
            }
            final String newUserExtCode = (String) requestMap.get("NEWEXTERNALCODE");
            if (!BTSLUtil.isNullString(newUserExtCode)) {
                _channelUserVO.setExternalCode(newUserExtCode);// Set New
            }
            // External Code
            // Check given channel User MSISDN if it is not already existing ,
            // in request
            final String originalMsisdn = BTSLUtil.NullToString((String) requestMap.get("USERMSISDN"));
            final String modifiedMsisdn = BTSLUtil.NullToString((String) requestMap.get("PRIMARYMSISDN"));
            boolean isExists = false;
            isExists = _channelUserDao.isPhoneExists(con, modifiedMsisdn);

            // If primary MSISDN is not again given and any existing MSISDN
            // given in modification request throw error
            if (!originalMsisdn.equals(modifiedMsisdn)) {
                if (!isExists) {
                    _channelUserVO.setMsisdn(BTSLUtil.NullToString((String) requestMap.get("USERMSISDN")));
                } else {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_ALREADY_EXIST);
                }
            }

            // User Name set
            boolean blank = true;
            final String modifiedUsername = (String) requestMap.get("USERNAME");
            blank = BTSLUtil.isNullString(modifiedUsername);
            if (!blank) {
                _channelUserVO.setUserName(modifiedUsername.toString().trim());
                String []name=modifiedUsername.trim().split(" ", 2);
                _channelUserVO.setFirstName(name[0]);
                if(name.length>1){
                	_channelUserVO.setLastName(name[1]);
                } 
            } else {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.XML_ERROR_USER_NAME_BLANK);// Blank
                // User
                // Name
                // Exception
            }

            // short name set
            final String modifiedUserShortname = (String) requestMap.get("SHORTNAME");
            blank = BTSLUtil.isNullString(modifiedUserShortname);
            if (!blank) {
                _channelUserVO.setShortName(modifiedUserShortname.toString().trim());
            }

            // User prefix check
            String userPrifix = (String) requestMap.get("USERNAMEPREFIX");
            userPrifix = userPrifix.toUpperCase();
            
            
            List<ListValueVO> userPrefix = new ArrayList<>();
            boolean prefix = false;
            if (!userPrifix.equals(modifiesChannelUserVO.getUserNamePrefix())) {
                userPrefix= LookupsCache.loadLookupDropDown(PretupsI.USER_NAME_PREFIX_TYPE, true);
                for(ListValueVO value : userPrefix){
                    if(value.getLabel().equalsIgnoreCase(userPrifix)){
                _channelUserVO.setUserNamePrefix(userPrifix);
                prefix=true;
                break;
                    }
                }
                if(!prefix){
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERROR_INVALID_USERNAMEPRFX);
                }
            }

            // set External Code
            final String modifiedEmpCode = (String) requestMap.get("NEWEXTERNALCODE");
            blank = BTSLUtil.isNullString(modifiedEmpCode);
            
            if (!blank) {
                final boolean isExtCodeExist = _channelUserWebDao.isExternalCodeExist(con, modifiedEmpCode, null);
                if (isExtCodeExist) {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTERNAL_CODE_ALREARY_EXIST);
                }
            }

            // Set Contact Person , contact number , ssn , address1, address2,
            // city , state, country ,email id
            final String modifiedContactPerson = (String) requestMap.get("CONTACTPERSON");
            blank = BTSLUtil.isNullString(modifiedContactPerson);
            if (!blank) {
                _channelUserVO.setContactPerson(modifiedContactPerson.toString().trim());
            }

            final String modifiedContactNumber = (String) requestMap.get("CONTACTNUMBER");
            blank = BTSLUtil.isNullString(modifiedContactNumber);
            if (!blank) {
                _channelUserVO.setContactNo(modifiedContactNumber.toString().trim());
            }

            final String modifiedSSN = (String) requestMap.get("SSN");
            blank = BTSLUtil.isNullString(modifiedSSN);
            if (!blank) {
                _channelUserVO.setSsn(modifiedSSN.toString().trim());
            }

            // Address1
            final String modifedAddress1 = (String) requestMap.get("ADDRESS1");
            blank = BTSLUtil.isNullString(modifedAddress1);
            if (!blank) {
                _channelUserVO.setAddress1(modifedAddress1.toString().trim());
            }

            // Address2
            final String modifedAddress2 = (String) requestMap.get("ADDRESS2");
            blank = BTSLUtil.isNullString(modifedAddress2);
            if (!blank) {
                _channelUserVO.setAddress2(modifedAddress2.toString().trim());
            }

            final String modifedCity = (String) requestMap.get("CITY");
            blank = BTSLUtil.isNullString(modifedCity);
            if (!blank) {
                _channelUserVO.setCity(modifedCity.toString().trim());
            }

            // State
            final String modifedState = (String) requestMap.get("STATE");
            blank = BTSLUtil.isNullString(modifedState);
            if (!blank) {
                _channelUserVO.setState(modifedState.toString().trim());
            }

            // Country
            final String modifedCountry = (String) requestMap.get("COUNTRY");
            blank = BTSLUtil.isNullString(modifedCountry);
            if (!blank) {
                _channelUserVO.setCountry(modifedCountry.toString().trim());
            }

            // EmailId
            final String modifedEmail = (String) requestMap.get("EMAILID");
            blank = BTSLUtil.isNullString(modifedEmail);
            boolean validEmail = false;
            if (!blank) {
                validEmail = BTSLUtil.validateEmailID(modifedEmail);
                if (validEmail) {
                    _channelUserVO.setEmail(modifedEmail.toString().trim());
                }
            }

            // WebLoginId
            final String modifedLoginId = (String) requestMap.get("WEBLOGINID");
            final String existingLoginId = (String) modifiesChannelUserVO.getLoginID();
            if (!BTSLUtil.isNullString(modifedLoginId) && !modifedLoginId.equals(existingLoginId)) {
                if (new UserDAO().isUserLoginExist(con, modifedLoginId, null)) {
                    throw new BTSLBaseException("UserModifyController", "process", PretupsErrorCodesI.ERROR_ERP_CHNL_USER_LOGINID_ALREADY_EXIST);
                    // 11-MAR-2014
                } else {
                    _channelUserVO.setLoginID(modifedLoginId.toString().trim());
                    // Ended Here
                }
            } else {
                _channelUserVO.setLoginID(existingLoginId.trim());
            }

            // Web Password
            String modifedPassword = (String) requestMap.get("WEBPASSWORD");

            // 11-MAR-2104 for setFlagForSMS
            boolean isWebLoginIdChanged = false;
            boolean isWebPasswordChanged = false;
            boolean isWebLoginIdPassswordChanged = false;
            boolean isPinChanged = false;
            Map<String, String[]> errorMessageMap = new HashMap<>();
            if(!BTSLUtil.isNullString(modifedPassword)&&!BTSLUtil.isNullString(_channelUserVO.getLoginID())){
                errorMessageMap = operatorUtili.validatePassword(_channelUserVO.getLoginID(), modifedPassword);
                if (null != errorMessageMap && errorMessageMap.size() > 0) {
                    throw new BTSLBaseException("UserAddController", "process", PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_INVALID);
                }
            }
            
            if (!BTSLUtil.isNullString(modifiedUserLoginId) && !modifiedUserLoginId.equalsIgnoreCase(existingLoginId) && !BTSLUtil.isNullString(modifedPassword) && !BTSLUtil
                .encryptText(modifedPassword).equalsIgnoreCase(modifiesChannelUserVO.getPassword())) {
                isWebLoginIdPassswordChanged = true;
            } else if (!BTSLUtil.isNullString(modifiedUserLoginId) && !modifiedUserLoginId.equalsIgnoreCase(existingLoginId)) {
                isWebLoginIdChanged = true;
            } else if (BTSLUtil.isNullString(modifiedUserLoginId) && !"".equalsIgnoreCase(existingLoginId)) {
                isWebLoginIdChanged = true;
            } else if (!BTSLUtil.isNullString(modifedPassword) && !BTSLUtil.encryptText(modifedPassword).equalsIgnoreCase(modifiesChannelUserVO.getPassword())) {
                isWebPasswordChanged = true;
                // Ended Here
            }

            // If in modify request, web password is not mentioned, then set the
            // previous one.
            if (BTSLUtil.isNullString(modifedPassword)) {
                modifedPassword = requestUserVO.getPassword();
                _channelUserVO.setPassword(modifedPassword);
            } else {
	            _channelUserVO.setPassword(BTSLUtil.encryptText(modifedPassword));
            }
            // get PIN of send if not comming then generate random pin as
            // discussed with Divyakant Sir
            senderPin = (String) requestMap.get("PIN");
            if (BTSLUtil.isNullString(senderPin)) {
                if (oldPhoneList != null && !oldPhoneList.isEmpty()) {
                    final UserPhoneVO oldPhoneVO = (UserPhoneVO) oldPhoneList.get(0);
                    // 11-MAR-2104 for setFlagForSMS
                    if (!BTSLUtil.isNullString(senderPin) && !oldPhoneVO.getSmsPin().toString().equalsIgnoreCase(BTSLUtil.encryptText(senderPin))) {
                        isPinChanged = true;
                        // Ended Here
                        // senderPin =
                        // BTSLUtil.decryptText(oldPhoneVO.getSmsPin());
                    }
                } else {
                    senderPin = operatorUtili.generateRandomPin();
                }

            }

            // Set some use full parameter
            final Date currentDate = new Date();
            // prepare user phone list
            final ArrayList userPhoneList = prepareUserPhoneVOList(con, requestMap, modifiesChannelUserVO, currentDate, oldPhoneList, senderPin);
            _channelUserVO.setModifiedBy(_senderVO.getUserID());
            _channelUserVO.setModifiedOn(currentDate);
            if (_channelUserVO.getMsisdn() != null) {
                _channelUserVO.setUserCode(_channelUserVO.getMsisdn());
            }

            final String userProfileId = _channelUserVO.getUserProfileID();
            _channelUserVO.setUserProfileID(userProfileId);
            _channelUserVO.setLowBalAlertAllow(_channelUserVO.getLowBalAlertAllow());
            // Employee Code
            final String empCode = (String) requestMap.get("EMPCODE");
            blank = BTSLUtil.isNullString(empCode);
            if (!blank) {
                _channelUserVO.setEmpCode(empCode.toString().trim());
            }
            // insert in to user table
            final int userCount = new UserDAO().updateUser(con, _channelUserVO);
            if (userCount <= 0) {
                con.rollback();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "UserModifyController[process]", "", "", "",
                    "Exception:Update count <=0 ");
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERP_USER_REGISTRATION_FAILED);
            }
            if (BTSLUtil.isNullString(_channelUserVO.getInSuspend())) {
                _channelUserVO.setInSuspend("N");
            }
            if (BTSLUtil.isNullString(_channelUserVO.getOutSuspened())) {
                _channelUserVO.setOutSuspened("N");
            }
            // insert data into channel users table
            final int userChannelCount = _channelUserDao.updateChannelUserInfo(con, _channelUserVO);
            if (userChannelCount <= 0) {
                con.rollback();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "UserModifyController[process]", "", "", "",
                    "Exception:Update count <=0 ");
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERP_USER_REGISTRATION_FAILED);
            }
            if (userPhoneList != null && !userPhoneList.isEmpty()) {
                final int phoneCount = _userDAO.updateInsertDeleteUserPhoneList(con, userPhoneList);
                if (phoneCount <= 0) {
                    try {
                        con.rollback();
                    } catch (SQLException e) {
                        _log.errorTrace(METHOD_NAME, e);
                    }
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "UserModifyController[process]", "", "", "",
                        "Exception:Update count <=0 ");
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERP_USER_REGISTRATION_FAILED);
                }
            }
            con.commit();
            requestMap.put("CHNUSERVO", _channelUserVO);
            p_requestVO.setRequestMap(requestMap);

            if (PretupsI.USER_STATUS_ACTIVE.equals(_channelUserVO.getStatus())) {
                // send a message to the user about there activation
                /*
                 * if(locale==null)
                 * locale = p_requestVO.getLocale();
                 */// code commented for sonar issue Dodgy - Redundant nullcheck
                   // of value known to be non-null
                BTSLMessages btslPushMessage = null;

                // 11-MAR-2014
                String[] arrArray = null;
                String[] arrArray1=null;
                String messageCode = null;
                if (isWebLoginIdPassswordChanged && isPinChanged) {
                    arrArray = new String[] { _channelUserVO.getLoginID(), modifedPassword, senderPin };
                    arrArray1 = new String[] { _channelUserVO.getLoginID(), "*****", "*****"};
                    messageCode = PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PWD_AND_PIN_MODIFY;
                } else if (isWebLoginIdPassswordChanged) {
                    arrArray = new String[] { _channelUserVO.getLoginID(), modifedPassword };
                    arrArray1 = new String[] { _channelUserVO.getLoginID(), "*****"};
                    messageCode = PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PWD_MODIFY;
                } else if (isWebLoginIdChanged && isPinChanged) {
                    arrArray = new String[] { _channelUserVO.getLoginID(), senderPin };
                    arrArray1 = new String[] { _channelUserVO.getLoginID(), "*****"};
                    messageCode = PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PIN_MODIFY;
                } else if (isWebPasswordChanged && isPinChanged) {
                    arrArray = new String[] { modifedPassword, senderPin };
                    arrArray1 = new String[] {"*****", "*****"};
                    messageCode = PretupsErrorCodesI.CHNL_USER_PWD_AND_PIN_MODIFY;
                } else if (isWebLoginIdChanged) {
                    arrArray = new String[] { _channelUserVO.getLoginID() };
                    arrArray1 = new String[] {_channelUserVO.getLoginID() };
                    messageCode = PretupsErrorCodesI.CHNL_USER_LOGIN_MODIFY;
                } else if (isWebPasswordChanged) {
                    arrArray = new String[] { modifedPassword };
                    arrArray1 = new String[] {"*****"};
                    messageCode = PretupsErrorCodesI.CHNL_USER_PWD_MODIFY;
                } else if (isPinChanged) {
                    arrArray = new String[] { senderPin };
                    arrArray1 = new String[] {"*****"};
                    messageCode = PretupsErrorCodesI.CHNL_USER_PIN_MODIFY;
                } else {
                    messageCode = PretupsErrorCodesI.USER_MODIFY_SUCCESS;
                }
                // Ended Here
                if (!BTSLUtil.isNullString(messageCode) && arrArray != null && arrArray.length > 0 && arrArray1 != null && arrArray1.length > 0) {
                    btslPushMessage = new BTSLMessages(messageCode, arrArray);
                    //p_requestVO.setMessageArguments(arrArray);
                    p_requestVO.setMessageArguments(arrArray1);
                    p_requestVO.setMessageCode(messageCode);
                    new PushMessage(_channelUserVO.getMsisdn(), btslPushMessage, null, null, locale, p_requestVO.getExternalNetworkCode()).push();
                } else if (!BTSLUtil.isNullString(messageCode)) {
                    btslPushMessage = new BTSLMessages(messageCode);
                    p_requestVO.setMessageCode(messageCode);
                    new PushMessage(_channelUserVO.getMsisdn(), btslPushMessage, null, null, locale, p_requestVO.getExternalNetworkCode()).push();
                }

            }
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);

            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
                return;
            }
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            _log.error("process", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserModifyController[process]", "", "", "",
                "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
            _channelUserDao = null;
            _channelUserWebDao = null;
            _userDAO = null;
            _channelUserVO = null;


			if (mcomCon != null) {
				mcomCon.close("UserModifyController#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }
    }

    /**
     * @description : This method to prepare the userphoneVO to update user
     *              information
     * @author :diwakar
     * @return : ArrayList
     */
    private ArrayList prepareUserPhoneVOList(Connection con, HashMap requestMap, ChannelUserVO _channelUserVO, Date currentDate, ArrayList oldPhoneList, String senderPin) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("prepareUserPhoneVOList", "Entered oldPhoneList.size()=" + oldPhoneList.size() + ",newMsisdnList.size()=" + ((ArrayList) requestMap.get("MSISDNLIST"))
                .size());
        }

        final ArrayList newMsisdnList = (ArrayList) requestMap.get("MSISDNLIST");
        NetworkPrefixVO networkPrefixVO = null;
        String msisdn = null;
        String stkProfile = null;
        final String oldUserPhoneID = null;
        final ArrayList phoneList = new ArrayList();
        final ArrayList stkProfileList = _userDAO.loadPhoneProfileList(con, _channelUserVO.getCategoryCode());
        if (stkProfileList != null) {
            final ListValueVO listValueVO = (ListValueVO) stkProfileList.get(0);
            stkProfile = listValueVO.getValue();
        }
        if (newMsisdnList != null && !newMsisdnList.isEmpty()) {
            UserPhoneVO phoneVO = null;
            UserPhoneVO oldPhoneVO = null;
            for (int i = 0, j = newMsisdnList.size(); i < j; i++) {
                phoneVO = new UserPhoneVO();
                msisdn = (String) newMsisdnList.get(i);
                phoneVO.setMsisdn(msisdn);
                phoneVO.setPinModifyFlag(true);
                phoneVO.setPhoneProfile(stkProfile);
                if (!BTSLUtil.isNullString(phoneVO.getMsisdn())) {

                    if (oldPhoneList != null && !oldPhoneList.isEmpty()) {
                        if (i < oldPhoneList.size()) {
                            oldPhoneVO = (UserPhoneVO) oldPhoneList.get(oldPhoneList.size() - (i + 1));
                        }
                    }
                    phoneVO.setMsisdn(PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn()));
                    if (oldPhoneVO != null) {
                        phoneVO.setUserPhonesId(oldPhoneVO.getUserPhonesId());
                        phoneVO.setPinModifyFlag(false);
                        phoneVO.setOperationType("U");
                        phoneVO.setPinModifiedOn(oldPhoneVO.getPinModifiedOn());
                        phoneVO.setPinRequired(oldPhoneVO.getPinRequired());
                        phoneVO.setSmsPin(oldPhoneVO.getSmsPin());
                        phoneVO.setIdGenerate(false);
                    } else if (BTSLUtil.isNullString(oldUserPhoneID)) {
                        phoneVO.setUserPhonesId(String.valueOf(IDGenerator.getNextID("PHONE_ID", TypesI.ALL)));
                        phoneVO.setOperationType("I");
                        phoneVO.setPinModifyFlag(true);
                        phoneVO.setPinModifiedOn(currentDate);
                        phoneVO.setSmsPin(BTSLUtil.encryptText(senderPin));
                        phoneVO.setIdGenerate(true);
                        phoneVO.setPinRequired(PretupsI.YES);
                    }

                    phoneVO.setUserId(_channelUserVO.getUserID());
                    // set the default values
                    phoneVO.setCreatedBy(_senderVO.getUserID());
                    phoneVO.setModifiedBy(_senderVO.getUserID());
                    phoneVO.setCreatedOn(currentDate);
                    phoneVO.setModifiedOn(currentDate);
                    phoneVO.setCountry((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                    phoneVO.setPhoneLanguage((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)));
                    phoneVO.setPinModifiedOn(currentDate);
                    final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL
                        .getMSISDNPrefix(PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn())));
                    phoneVO.setPrefixID(prefixVO.getPrefixID());
                    if (msisdn.equals((String) requestMap.get("PRIMARYMSISDN"))) {
                        _channelUserVO.setMsisdn(msisdn);
                        phoneVO.setPrimaryNumber(PretupsI.YES);
                    } else {
                        phoneVO.setPrimaryNumber(PretupsI.NO);
                    }
                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED))).booleanValue()) {
                        networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(msisdn));
                        boolean numberAllowed = false;
                        if (networkPrefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
                            numberAllowed = new NumberPortDAO().isExists(con, msisdn, "", PretupsI.PORTED_IN);
                            if (!numberAllowed) {
                                throw new BTSLBaseException("UserModifyController", "prepareUserPhoneVOList", PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_PORTED_IN);
                            }
                        } else {
                            numberAllowed = new NumberPortDAO().isExists(con, msisdn, "", PretupsI.PORTED_OUT);
                            if (numberAllowed) {
                                throw new BTSLBaseException("UserModifyController", "prepareUserPhoneVOList", PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_PORTED_OUT);
                            }
                        }
                    }

                    if (_userDAO.isMSISDNExist(con, PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn()), phoneVO.getUserId())) {
                        final String[] arr = { phoneVO.getMsisdn() };
                        throw new BTSLBaseException("UserModifyController", "prepareUserPhoneVOList", PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_ALREADY_EXIST, arr);
                    }
                    phoneList.add(phoneVO);
                } else {
                    if (oldPhoneList != null && !oldPhoneList.isEmpty()) {
                        if (i < oldPhoneList.size()) {
                            oldPhoneVO = (UserPhoneVO) oldPhoneList.get(oldPhoneList.size() - (i + 1));
                            phoneVO = new UserPhoneVO();
                            phoneVO.setUserPhonesId(oldPhoneVO.getUserPhonesId());
                        }
                    }
                    if (!BTSLUtil.isNullString(phoneVO.getUserPhonesId())) {
                        if (!phoneVO.isIdGenerate()) {
                            phoneVO.setOperationType("D");
                        }
                        phoneList.add(phoneVO);

                    }
                }
                oldPhoneVO = null;
                phoneVO = null;
            }

        }
        return phoneList;
    }
}

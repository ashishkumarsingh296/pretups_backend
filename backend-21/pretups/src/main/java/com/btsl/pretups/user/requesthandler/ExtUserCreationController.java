package com.btsl.pretups.user.requesthandler;

/**
 * @(#)ExtUserCreationController.java
 *                                    Copyright(c) 2010, Comviva Technologies
 *                                    Ltd.
 *                                    All Rights Reserved
 * 
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Author Date History
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Ashish Kumar Todia 24Sept10 Creation.
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    This class is used for Ussd User Creation.
 * 
 */
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
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
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.routing.subscribermgmt.businesslogic.NumberPortDAO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UssdUserBL;
import com.btsl.pretups.user.businesslogic.UssdUserDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.XMLTagValueValidation;

public class ExtUserCreationController implements ServiceKeywordControllerI {

    private static Log _log = LogFactory.getLog(ExtUserCreationController.class.getName());

    private ChannelUserVO _channelUserVO; // sender user Vo
    private ChannelUserVO _newChannelUserVO; // new user to be created vo.
    private Date _currentDate = new Date();

    private RequestVO _requestVO = null;
    private CategoryVO _categoryVO = null;
    private boolean isRequestValid = false;
    private ChannelUserVO _parentUserVO;
    private static OperatorUtilI _operatorUtil = null;
    private static UserDAO _userdao = new UserDAO();
    private static UssdUserDAO _ussddao = new UssdUserDAO();
    private static NumberPortDAO _numberportdao = new NumberPortDAO();
    private String _RecAlternetGatewaySMS;

    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[initialize]", "", "", "",
                            "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        Connection con = null;
        MComConnectionI mcomCon = null;
        int count = 0;
        if (_log.isDebugEnabled()) {
            _log.debug("process", p_requestVO.getRequestIDStr(), "Entered for Request ID=" + p_requestVO.getRequestID() + " MSISDN=" + p_requestVO.getFilteredMSISDN());
        }
        try {
            _requestVO = p_requestVO;
            _channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();

            _RecAlternetGatewaySMS = BTSLUtil.NullToString(Constants.getProperty("C2S_REC_MSG_REQD_BY_ALT_GW"));
            // getting the request message string
            // KEYWORD USERMSISDN USERNAME LOGINID CATEGORYCODE PARENTMSISDN
            // <OPTIONAL> EMPCODE SSN EXTCODE </OPTIONAL> PIN
            final String[] str = p_requestVO.getRequestMessageArray();

            if (str.length < 7 && str.length > 10) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT);
            }
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            // to check the pin of the sender.

            // KEYWORD USERMSISDN USERNAME LOGINID CATEGORYCODE PARENTMSISDN
            // <OPTIONAL> EMPCODE SSN EXTCODE </OPTIONAL> PIN
            final String _senderPin = str[str.length - 1];

            _operatorUtil.validatePIN(con, _channelUserVO, _senderPin);

            _parentUserVO = UssdUserBL.loadUserDetail(con, str[5]); // parent
            // msisdn
            if (_parentUserVO == null) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.PARENT_NOT_FOUND);
            }
            _newChannelUserVO = new ChannelUserVO();
            populateVOFromRequest(_requestVO);
            // Validation of UserMsisdn
            XMLTagValueValidation.validateMsisdn(_newChannelUserVO.getMsisdn(), _parentUserVO.getNetworkID(), XMLTagValueValidation.isTagManadatory());
            // Ended here
            final ChannelUserVO _checkUserExistVO = UssdUserBL.loadUserDetail(con, _newChannelUserVO.getMsisdn());
            if (!(_checkUserExistVO == null)) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.USER_ALREADY_EXIST);
            }
            // Check for sender and parent are in same hirarchy
            final boolean inSenderHierachy = UssdUserBL.checkParentInSenderHierarchy(con, _channelUserVO, _parentUserVO);
            if (!inSenderHierachy) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.USER_NOT_IN_HIERARCHY);
            }
            _newChannelUserVO = new ChannelUserVO();
            populateVOFromRequest(_requestVO);

            final boolean loginidExist = _userdao.isUserLoginExist(con, _newChannelUserVO.getLoginID(), null);
            if (loginidExist) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.LOGINID_ALREADY_EXIST);
            }

            if (!(BTSLUtil.isNullString(_newChannelUserVO.getExternalCode()))) {
                final boolean isExtCodeExist = _ussddao.isExternalCodeExist(con, _newChannelUserVO.getExternalCode());
                if (isExtCodeExist) {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTERNAL_CODE_ALREARY_EXIST);
                }
            }

            final boolean isPortedOut = _numberportdao.isExists(con, _newChannelUserVO.getMsisdn(), "", PretupsI.PORTED_OUT);
            if (isPortedOut) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.PORTED_MSISDN);
            }

            // validate the input categoryCode.
            final boolean isValidCategory = UssdUserBL
                            .validateCategoryCode(con, _newChannelUserVO.getCategoryCode(), _parentUserVO.getCategoryVO().getDomainCodeforCategory());
            if (!isValidCategory) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CATEGORY_NOT_EXIST);
            }
            // Validate whether it is possible to create user.(from transfer
            // rules).
            final boolean isUserCreationValid = UssdUserBL.validateUserCreationByCategoryCode(con, _parentUserVO.getCategoryCode(), _newChannelUserVO.getCategoryCode(),
                            _parentUserVO.getNetworkID());
            if (!isUserCreationValid) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.USSD_USER_CREATION_NOT_ALLOWED);
            }
            final String filteredMSISDN = _operatorUtil.getSystemFilteredMSISDN(_newChannelUserVO.getMsisdn());
            if (!BTSLUtil.isValidMSISDN(filteredMSISDN)) {
                final String args[] = { filteredMSISDN };
                throw new BTSLBaseException("ExtUserCreationController", "process", PretupsErrorCodesI.ERROR_INVALID_MSISDN, args);

            }
            _newChannelUserVO.setNetworkID(_parentUserVO.getNetworkID());
            count = updateDataBase(con, _newChannelUserVO, _requestVO);
            // User added SucessFully..
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            }
            _log.errorTrace(METHOD_NAME, be);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            p_requestVO.setSuccessTxn(false);
        } finally {
            try {
                if (count == 1) {
                    if (con != null) {
                       mcomCon.finalCommit();
                    } else if (con != null) {
                       mcomCon.finalRollback();
                    }
                }
                if(mcomCon != null)
                {
                	mcomCon.close("ExtUserCreationController#process");
                	mcomCon=null;
                	}
            } catch (SQLException sqlexp) {
                _log.errorTrace(METHOD_NAME, sqlexp);
                _log.error("process", " SqlException : " + sqlexp.getMessage());
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error("process", " Exception : " + e.getMessage());
            }

            // Pushing the SMS to the users.
            final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            String requestGW = p_requestVO.getRequestGatewayCode();
            if (!BTSLUtil.isNullString(_RecAlternetGatewaySMS) && (_RecAlternetGatewaySMS.split(":")).length >= 2) {
                if (requestGW.equalsIgnoreCase(_RecAlternetGatewaySMS.split(":")[0])) {
                    requestGW = (_RecAlternetGatewaySMS.split(":")[1]).trim();
                    if (_log.isDebugEnabled()) {
                        _log.debug("process: Reciver Message push through alternate GW", requestGW, "Requested GW was:" + p_requestVO.getRequestGatewayCode());
                    }
                }
            }
            if (count > 0) {
                final String networkCode = _newChannelUserVO.getNetworkCode();
                // New User Message.
                final String[] arr = new String[1];
                arr[0] = _newChannelUserVO.getParentMsisdn();
                final BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.EXT_USER_CREATION_USER_MSG, arr);
                final PushMessage pushMessage = new PushMessage(_newChannelUserVO.getMsisdn(), btslMessage, null, requestGW, locale, networkCode);
                pushMessage.push();
                // TO Sender.
                final String[] arr1 = new String[2];
                arr1[0] = _newChannelUserVO.getMsisdn();
                arr1[1] = _parentUserVO.getMsisdn();
                final BTSLMessages btslMessage1 = new BTSLMessages(PretupsErrorCodesI.EXT_USER_CREATION_SENDER_MSG, arr1);
                final PushMessage pushMessage1 = new PushMessage(_channelUserVO.getMsisdn(), btslMessage1, null, requestGW, locale, networkCode);
                pushMessage1.push();

                if (!(_channelUserVO.getMsisdn().equalsIgnoreCase(_parentUserVO.getMsisdn()))) {
                    final String[] arr2 = new String[1];
                    arr[0] = _newChannelUserVO.getMsisdn();
                    final BTSLMessages btslMessage2 = new BTSLMessages(PretupsErrorCodesI.EXT_USER_CREATION_PARENT_MSG, arr2);
                    final PushMessage pushMessage2 = new PushMessage(_parentUserVO.getMsisdn(), btslMessage2, null, requestGW, locale, networkCode);
                    pushMessage2.push();
                }

                // PushMessage pushMessage3=new
                // PushMessage(_channelUserVO.getMsisdn(),btslMessage1,null,p_requestVO.getRequestGatewayCode(),locale,networkCode);
                // pushMessage3.push();
            } else {
                final BTSLMessages btslMessage = new BTSLMessages(p_requestVO.getMessageCode(), p_requestVO.getMessageArguments());
                final PushMessage pushMessage = new PushMessage(_channelUserVO.getMsisdn(), btslMessage, null, p_requestVO.getRequestGatewayCode(), locale, _channelUserVO
                                .getNetworkCode());
                pushMessage.push();
            }
            if (_log.isDebugEnabled()) {
                _log.debug("process", "Exiting");
            }
        }
    }

    /**
     * This method handles all the sequences of the user addition.
     * 
     * @param p_channelUserVO
     */
    private int updateDataBase(Connection p_con, ChannelUserVO p_channelUserVO, RequestVO p_requestVO) {
        final String METHOD_NAME = "updateDataBase";
        if (_log.isDebugEnabled()) {
            _log.debug("updateDataBase", "", "Entered for MSISDN=" + p_channelUserVO.getMsisdn());
        }
        int count = 0;
        HashMap<String, String> defaultValueMap = null;
        // grades/commission/transferprofile/geography/roles
        String defaultGrade = null;
        String defaultComm = null;
        String defaultTransfPrf = null;
        String defaultGeo = null;
        String defaultRoleCode = null;
        try {
            defaultValueMap = UssdUserBL.setDefaultProfiles(p_con, p_channelUserVO); // to
            // get
            // roles
            // remaining.
            defaultGrade = defaultValueMap.get("defaultGrade");
            if (BTSLUtil.isNullString(defaultGrade)) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.DEFAULT_GRADE_NOT_FOUND);
            }
            defaultComm = defaultValueMap.get("defaultCommission");
            if (BTSLUtil.isNullString(defaultComm)) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.DEFAULT_COMM_PRF_NOT_FOUND);
            }
            defaultTransfPrf = defaultValueMap.get("defaultTrasfPrf");
            if (BTSLUtil.isNullString(defaultTransfPrf)) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.DEFAULT_TRF_PRF_NOT_FOUND);
            }
            defaultGeo = defaultValueMap.get("defaultGeo");
            if (BTSLUtil.isNullString(defaultGeo)) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.DEFAULT_GEO_NOT_FOUND);
            }
            defaultRoleCode = defaultValueMap.get("defaultRoleCode");
            if (BTSLUtil.isNullString(defaultRoleCode)) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.DEFAULT_ROLES_NOT_FOUND);
            }
            // setting default values to the usersVo.
            p_channelUserVO.setUserGrade(defaultGrade);
            p_channelUserVO.setCommissionProfileSetID(defaultComm);
            p_channelUserVO.setTransferProfileID(defaultTransfPrf);
            p_channelUserVO.setGeographicalCode(defaultGeo);
            p_channelUserVO.setGroupRoleCode(defaultRoleCode);

            count = UssdUserBL.addUserDetails(p_con, p_channelUserVO);
            if (count == 0) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.USSD_USER_CREATION_ERROR);
            }
            count = UssdUserBL.addChannelUserDetaile(p_con, p_channelUserVO);
            if (count == 0) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.USSD_USER_CREATION_ERROR);
            }
            count = UssdUserBL.addUserPhoneDetails(p_con, p_channelUserVO);
            if (count == 0) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.USSD_USER_CREATION_ERROR);
            }
            count = UssdUserBL.addUserServices(p_con, p_channelUserVO, _requestVO.getModule());
            if (count == 0) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.USSD_USER_CREATION_ERROR);
            }
            count = UssdUserBL.addUserGeograpicalDomain(p_con, p_channelUserVO);
            if (count == 0) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.USSD_USER_CREATION_ERROR);
            }
            // add roles only when group roles will be there only..
            if (!BTSLUtil.isNullString(p_channelUserVO.getGroupRoleCode())) {
                count = UssdUserBL.addUserRolesDetails(p_con, p_channelUserVO);
                if (count == 0) {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.USSD_USER_CREATION_ERROR);
                }
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            p_requestVO.setSuccessTxn(false);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("updateDataBase", "Exiting count: " + count);
        }
        return count;
    }

    // check for category without web access.
    // check for the optinal parts of the request.
    private void populateVOFromRequest(RequestVO p_requestVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("populateVOFromRequest", "Entered :" + p_requestVO);
        }
        // // keyword usermsisnd username loginId categoryCode parentmsisdn pin
        final String[] str = p_requestVO.getRequestMessageArray();
        final HashMap<String, String> requestMap = p_requestVO.getRequestMap();
        String empCode = null;
        String ssn = null;
        String extCode = null;

        final String msisdn = str[1].toString();
        final String username = str[2].toString();
        final String loginId = str[3].toString();
        final String categoryCode = str[4].toString();
        final String parentMsisdn = str[5].toString();

        _newChannelUserVO.setMsisdn(msisdn);
        _newChannelUserVO.setUserName(username);
        _newChannelUserVO.setLoginID(loginId);
        _newChannelUserVO.setCategoryCode(categoryCode);
        _newChannelUserVO.setParentMsisdn(parentMsisdn);

        if (p_requestVO.isPlainMessage()) {

            if (!BTSLUtil.isNullString(p_requestVO.getEmployeeCode())) {
                empCode = p_requestVO.getEmployeeCode();
                _newChannelUserVO.setEmpCode(empCode);
            }
            if (!BTSLUtil.isNullString(p_requestVO.getSsn())) {
                ssn = requestMap.get("SSN");
                _newChannelUserVO.setSsn(ssn);
            }
            if (!BTSLUtil.isNullString(p_requestVO.getExternalReferenceNum())) {
                extCode = p_requestVO.getExternalReferenceNum();
                _newChannelUserVO.setExternalCode(extCode);
            }

        } else {
            empCode = requestMap.get("EMPCODE");
            _newChannelUserVO.setEmpCode(empCode);
            ssn = requestMap.get("SSN");
            _newChannelUserVO.setSsn(ssn);
            extCode = requestMap.get("EXTCODE");
            _newChannelUserVO.setExternalCode(extCode);
        }

        _newChannelUserVO.setNetworkCode(_parentUserVO.getNetworkCode());
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PWD_GENERATE_ALLOW))).booleanValue()) {
            _newChannelUserVO.setPassword(BTSLUtil.encryptText(_operatorUtil.generateRandomPassword()));
            _newChannelUserVO.setSmsPin(BTSLUtil.encryptText(_operatorUtil.generateRandomPin()));
        } else {
            _newChannelUserVO.setPassword(BTSLUtil.encryptText(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_PASSWORD))));
            _newChannelUserVO.setSmsPin(BTSLUtil.encryptText(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN))));
        }
        _newChannelUserVO.setParentID(_parentUserVO.getUserID());
        _newChannelUserVO.setOwnerID(_parentUserVO.getOwnerID());
        if (((Integer) PreferenceCache.getSystemPreferenceValue(PretupsI.USR_APPROVAL_LEVEL)).intValue() > 0) {
            _newChannelUserVO.setStatus(PretupsI.USER_STATUS_NEW);// N New
            _newChannelUserVO.setPreviousStatus(PretupsI.USER_STATUS_NEW);// N
            // New
        } else {
			if (LookupsCache.getLookupCodeList(PretupsI.ALLOWED_USER_STATUS).contains(PretupsI.USER_STATUS_PREACTIVE)) {
                		_newChannelUserVO.setStatus(PretupsI.USER_STATUS_PREACTIVE);// Y Active
						_newChannelUserVO.setPreviousStatus(PretupsI.USER_STATUS_PREACTIVE);// Y
                		// Active
                	} else {
                		_newChannelUserVO.setStatus(PretupsI.USER_STATUS_ACTIVE);// Y Active
						_newChannelUserVO.setPreviousStatus(PretupsI.USER_STATUS_ACTIVE);// Y
                	}
          }
        _newChannelUserVO.setUserType(PretupsI.CHANNEL_USER_TYPE);
        _newChannelUserVO.setCreatedBy(p_requestVO.getActiverUserId());
        _newChannelUserVO.setCreatedOn(_currentDate);
        _newChannelUserVO.setModifiedBy(p_requestVO.getActiverUserId());
        _newChannelUserVO.setModifiedOn(_currentDate);
        _newChannelUserVO.setUserNamePrefix("CMPY");
        _newChannelUserVO.setUserCode(str[1]);

        if (p_requestVO.getSourceType().equalsIgnoreCase(PretupsI.REQUEST_SOURCE_STK)) {
            _newChannelUserVO.setCreationType(PretupsI.STK_USER_CREATION_TYPE);
        } else if (p_requestVO.getSourceType().equalsIgnoreCase(PretupsI.REQUEST_SOURCE_TYPE_USSD)) {
            _newChannelUserVO.setCreationType(PretupsI.USSD_USER_CREATION_TYPE);
        } else if (p_requestVO.getSourceType().equalsIgnoreCase(PretupsI.REQUEST_SOURCE_TYPE_EXTGW)) {
            _newChannelUserVO.setCreationType(PretupsI.EXT_USER_CREATION_TYPE);
        }

        if (_log.isDebugEnabled()) {
            _log.debug("populateVOFromRequest", "Exiting ");
        }
    }

}

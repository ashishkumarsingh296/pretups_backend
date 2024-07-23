/*
 * @#ExtUsrController.java
 * * This class is the controller class of external user creation module.
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Ankur Dhawan August, 2011 Initial creation*
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2010 Comviva Ltd.
 */
package com.btsl.pretups.requesthandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Pattern;

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
import com.btsl.ota.services.businesslogic.ServiceTypeVO;
import com.btsl.ota.services.businesslogic.UserServicesCache;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.UserGeographiesDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.roles.businesslogic.UserRolesDAO;
import com.btsl.pretups.routing.subscribermgmt.businesslogic.NumberPortDAO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserDefaultCache;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.txn.pretups.master.businesslogic.GeographicalDomainTxnDAO;
import com.txn.pretups.user.businesslogic.ChannelUserTxnDAO;
import com.txn.user.businesslogic.UserTxnDAO;

public class ExtUsrController implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(ExtUsrController.class.getName());

    private RequestVO _requestVO = null;
    private HashMap<String, Object> _requestMap = null;

    private static OperatorUtilI _operatorUtil = null;

    private ChannelUserVO _newChannelUserVO = new ChannelUserVO();

    static {
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("ExtUsrController[process]", "Entered");
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        CategoryDAO categoryDAO = new CategoryDAO();
        ChannelUserVO channelUserVO = null;
        GeographicalDomainTxnDAO geographicalDomainTxnDAO = new GeographicalDomainTxnDAO();
        ArrayList<ChannelUserVO> usersList = new ArrayList<ChannelUserVO>();
        Iterator<ChannelUserVO> userListIterator = null;
        ChannelUserTxnDAO channelUserTxnDAO = null;
        UserTxnDAO usertxnDAO = new UserTxnDAO();
        CategoryVO _categoryVO = null;
        ChannelUserVO _ownerVO = null;
        ChannelUserVO _parentVO = null;
        ChannelUserVO _creatorVO = null;
        NetworkPrefixVO prefixVO = null;
        UserPhoneVO phoneVO = new UserPhoneVO();
        Date currentDate = new Date();
        Locale locale = null;
        Boolean rsaRequired = false;
        ArrayList<ServiceTypeVO> serviceList = null;
        ArrayList<UserPhoneVO> phoneList = new ArrayList<UserPhoneVO>();
        int count = 0;
        Pattern p = Pattern.compile(",");

        _requestVO = p_requestVO;
        _requestMap = p_requestVO.getRequestMap();

        String networkCode = (String) _requestMap.get("EXTNWCODE");
        String ownerMsisdn = (String) _requestMap.get("OWNERMSISDN");
        String parentMsisdn = (String) _requestMap.get("PARENTMSISDN");
        String categoryCode = (String) _requestMap.get("CATCODE");
        String optLoginId = (String) p_requestVO.getRequestMap().get("OPTLOGINID");
        String msisdn = (String) _requestMap.get("MSISDN");
        String userName = (String) _requestMap.get("USERNAME");
        String loginId = (String) _requestMap.get("LOGINID");
        String empCode = (String) _requestMap.get("EMPCODE");
        String extCode = (String) _requestMap.get("EXTCODE");
        String rsa = (String) _requestMap.get("RSA");
        String ssn = (String) _requestMap.get("SSN");
        String geoCode = (String) _requestMap.get("GEOGRAPHY");
        String ruleType = (String) _requestMap.get("RULETYPE");
        if (_log.isDebugEnabled()) {
            _log.debug("ExtUsrController[process]", "networkCode:" + networkCode + ",ownerMsisdn:" + ownerMsisdn + ",parentMsisdn:" + parentMsisdn + ",categoryCode:" + categoryCode + ",optLoginId:" + optLoginId + ",msisdn:" + msisdn + ",userName:" + userName + ",loginId:" + loginId + ",empCode:" + empCode + ",extCode:" + extCode + ",rsa:" + rsa + ",ssn:" + ssn + ",geoCode:" + geoCode + ",ruleType:" + ruleType);
        }
        HashMap<String, String> userMap = null;
        ArrayList<CategoryVO> catList = null;
        try {
            channelUserTxnDAO = new ChannelUserTxnDAO();
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            // String status =
            // "'"+PretupsI.USER_STATUS_ACTIVE+"','"+PretupsI.USER_STATUS_SUSPEND+"', '"+PretupsI.USER_STATUS_SUSPEND_REQUEST+"'";
            StringBuilder status = new StringBuilder("'" + PretupsI.USER_STATUS_ACTIVE + "','" + PretupsI.USER_STATUS_SUSPEND + "','" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'");
            String statusUsed = PretupsI.STATUS_IN;
            // Loads the details of creator, parent and owner
            usersList = channelUserTxnDAO.loadUsersListForExtApi(con, optLoginId, parentMsisdn, ownerMsisdn, statusUsed, status.toString());
            userListIterator = usersList.iterator();
            while (userListIterator.hasNext()) {
                channelUserVO = (ChannelUserVO) userListIterator.next();
                if (channelUserVO.getLoginID().equals(optLoginId)) {
                    _creatorVO = channelUserVO;
                } else if (channelUserVO.getMsisdn().equals(parentMsisdn)) {
                    _parentVO = channelUserVO;
                    if (parentMsisdn.equals(ownerMsisdn)) {
                        _ownerVO = channelUserVO;
                    }
                } else if (channelUserVO.getMsisdn().equals(ownerMsisdn)) {
                    _ownerVO = channelUserVO;
                }
                channelUserVO = null;
            }

            if (_log.isDebugEnabled()) {
                _log.debug("ExtUsrController[process]", "CreatorVO:" + _creatorVO + ",OwnerVO:" + _ownerVO + "ParentVO:" + _parentVO);
            }
            if (_creatorVO == null) {
                String errArgs[] = { optLoginId };
                throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.ERROR_USER_NOT_EXIST, 0, errArgs, null);
            }
            if (!networkCode.equalsIgnoreCase(_creatorVO.getNetworkID())) {
                throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.EXT_XML_ERROR_NO_SUCH_USER);
            }
            if (!_creatorVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE)) {
                String[] errArgs = { optLoginId };
                throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.EXT_USERADD_OPTLOGIN_NOT_OPERATOR, 0, errArgs, null);
            }
            // Category code validation
            catList = categoryDAO.loadCategoryDetailsUsingCategoryCode(con, categoryCode);
            if (_log.isDebugEnabled()) {
                _log.debug("ExtUsrController[process]", "Category List Size:" + catList.size());
            }
            if (catList == null || catList.isEmpty()) {
                throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.EXT_USRADD_INVALID_CATEGORY);
            } else {
                _categoryVO = (CategoryVO) catList.get(0);
            }

            // Validate if loginID or msisdn or external code is already alloted
            channelUserVO = usertxnDAO.verifyUniqueDetails(con, loginId, msisdn, extCode);
            if (channelUserVO != null) {
                if (_log.isDebugEnabled()) {
                    _log.debug("ExtUsrController[process]", "Channel user MSISDN:" + channelUserVO.getMsisdn() + ",LoginID:" + channelUserVO.getLoginID() + ",External Code:" + channelUserVO.getExternalCode());
                }
                if (!BTSLUtil.isNullString(channelUserVO.getMsisdn())) {
                    if (channelUserVO.getMsisdn().equals(msisdn)) {
                        throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.EXT_USRADD_MSISDN_EXISTS);
                    }
                }
                if (!BTSLUtil.isNullString(channelUserVO.getLoginID())) {
                    if (channelUserVO.getLoginID().equals(loginId)) {
                        throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.EXT_USRADD_LOGIN_EXISTS);
                    }
                }
                if (!BTSLUtil.isNullString(channelUserVO.getExternalCode())) {
                    if (channelUserVO.getExternalCode().equals(extCode)) {
                        throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.EXT_USRADD_EXTCODE_EXISTS);
                    }
                }
            }

            // Validation for Login ID
            if ((_categoryVO.getWebInterfaceAllowed()).equals(PretupsI.YES) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED))).booleanValue()) {
                if (!BTSLUtil.isNullString(loginId)) {
                    HashMap messageMap = _operatorUtil.validateLoginId(loginId);
                    if (!messageMap.isEmpty()) {
                        throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.EXT_USRADD_INVALID_LOGINID);
                    }
                } else {
                    throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.EXT_USRADD_ERROR_MISSING_MANDATORY_FIELDS);
                }
                _newChannelUserVO.setLoginID(loginId);
            } else if (!BTSLUtil.isNullString(loginId) && !(_categoryVO.getWebInterfaceAllowed()).equals(PretupsI.YES)) {
                throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.EXT_USRADD_LOGIN_NOT_ALLOWED);
            }
            // Password generation
            if (!BTSLUtil.isNullString(loginId)) {
                String randomPwd = _operatorUtil.generateRandomPassword();
                String password = BTSLUtil.encryptText(randomPwd);
                _newChannelUserVO.setPassword(password);
                _newChannelUserVO.setPasswordModifyFlag(true);
            }

            if (_log.isDebugEnabled()) {
                _log.debug("ExtUsrController[process]", "Category WEB allow:" + _categoryVO.getWebInterfaceAllowed() + ",LoginId:" + _newChannelUserVO.getLoginID());
            }

            // Validation for User Name
            if (p.matcher(userName).find() || (userName.length() > 80)) {
                throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.EXT_USRADD_USERNAME_INCORRECT);
            }

            _newChannelUserVO.setExternalCode(extCode);
            _newChannelUserVO.setUserName(userName);
            _newChannelUserVO.setNetworkID(networkCode);
            _newChannelUserVO.setNetworkCode(networkCode);
            _newChannelUserVO.setCategoryCode(categoryCode);
            _newChannelUserVO.setGeographicalCode(geoCode);
            _newChannelUserVO.setUserID(generateUserId(networkCode, _categoryVO.getUserIdPrefix()));

            _log.info("ExtUsrController[process]", "UserId:" + _newChannelUserVO.getUserID() + "Category Sequence No:" + _categoryVO.getSequenceNumber());

            // for owner and parent details
            if (_categoryVO.getSequenceNumber() != 1) {
                if (BTSLUtil.isNullString(ownerMsisdn)) {
                    throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.EXT_USRADD_OWNER_MISSING);
                }
                if (BTSLUtil.isNullString(parentMsisdn)) {
                    throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.EXT_USRADD_PARENT_MISSING);
                }
                if (_ownerVO == null) {
                    String[] arr1 = { ownerMsisdn };
                    _log.error("ExtUsrController[process] ", "Error: No owner user found with the MSISDN Number" + ownerMsisdn);
                    throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.EXT_USRADD_OWNER_NOT_EXIST, 0, arr1, null);
                }
                if (_parentVO == null) {
                    String[] arr1 = { parentMsisdn };
                    _log.error("ExtUsrController[process] ", "Error: No parent user found with the MSISDN Number" + parentMsisdn);
                    throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.EXT_USRADD_PARENT_NOT_EXIST, 0, arr1, null);
                }
                channelUserTxnDAO.validateParentAndOwner(con, _ownerVO, _parentVO, categoryCode, geoCode);
                _log.info("ExtUsrController[process] ", "Parent and Owner are valid");
                _newChannelUserVO.setParentID(_parentVO.getUserID());
                _newChannelUserVO.setOwnerID(_ownerVO.getUserID());
            } else {
                if (!BTSLUtil.isNullString(ownerMsisdn) || !BTSLUtil.isNullString(parentMsisdn)) {
                    throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.EXT_USRADD_TOP_LEVEL_USER);
                }

                if (!geographicalDomainTxnDAO.validateGeography(con, geoCode, categoryCode)) {
                    throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.EXT_USRADD_INVALID_GEOGRAPHY);
                }
                _newChannelUserVO.setParentID(PretupsI.ROOT_PARENT_ID);
                _newChannelUserVO.setOwnerID(_newChannelUserVO.getUserID());
            }

            _newChannelUserVO.setUserType(PretupsI.CHANNEL_USER_TYPE);
            _newChannelUserVO.setStatus(PretupsI.USER_STATUS_ACTIVE);
            _newChannelUserVO.setPreviousStatus(PretupsI.USER_STATUS_ACTIVE);// Y
                                                                             // Active

            if (_log.isDebugEnabled()) {
                _log.debug("ExtUsrController[process]", "New Channel User Details 1:" + _newChannelUserVO.toString());
            }
            // Validation for MSISDN
            String filterMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
            prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(filterMsisdn));
            if (prefixVO == null || !prefixVO.getNetworkCode().equals(_newChannelUserVO.getNetworkCode())) {
                String[] arr1 = { msisdn, networkCode };
                _log.error("ExtUsrController[process] ", "Error: MSISDN Number" + msisdn + " does not belong to " + networkCode + "network");
                throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.EXT_USRADD_NOT_IN_SAME_NETWORK, 0, arr1, null);
            }
            if (msisdn.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue() || msisdn.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue()) {
                throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.EXT_USRADD_INVALID_MSISDN);
            }

            phoneVO.setMsisdn(filterMsisdn);
            phoneVO.setUserPhonesId(String.valueOf(IDGenerator.getNextID("PHONE_ID", TypesI.ALL)));
            phoneVO.setUserId(_newChannelUserVO.getUserID());
            // set the default values
            phoneVO.setCreatedBy(_creatorVO.getUserID());
            phoneVO.setModifiedBy(_creatorVO.getUserID());
            phoneVO.setCreatedOn(currentDate);
            phoneVO.setModifiedOn(currentDate);
            phoneVO.setPinModifiedOn(currentDate);
            phoneVO.setCountry((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            phoneVO.setPhoneLanguage((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)));
            phoneVO.setPrefixID(prefixVO.getPrefixID());
            phoneVO.setPhoneProfile(categoryCode);
            phoneVO.setPrimaryNumber(PretupsI.YES);
            phoneVO.setPinRequired(PretupsI.YES);

            String randomPin = _operatorUtil.generateRandomPin();
            String excryptedPin = BTSLUtil.encryptText(randomPin);
            phoneVO.setShowSmsPin(randomPin);
            phoneVO.setConfirmSmsPin(excryptedPin);
            phoneVO.setSmsPin(excryptedPin);

            phoneList.add(phoneVO);
            if (_log.isDebugEnabled()) {
                _log.debug("ExtUsrController[process]", "PhoneVO List:" + phoneList.size());
            }
            _newChannelUserVO.setMsisdnList(phoneList);
            _newChannelUserVO.setMsisdn(phoneVO.getMsisdn());
            _newChannelUserVO.setPrimaryMsisdnPin(phoneVO.getSmsPin());
            locale = new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());

            /*
             * Code Added for MNP
             * Preference to check whether MNP is allowed in system or not.
             * If yes then check whether Number has not been ported out, If yes
             * then throw error, else continue
             */
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED))).booleanValue()) {
                boolean numberAllowed = false;
                if (prefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
                    numberAllowed = new NumberPortDAO().isExists(con, filterMsisdn, "", PretupsI.PORTED_IN);
                    if (!numberAllowed) {
                        throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.EXT_USRADD_NOT_IN_SAME_NETWORK, 0, new String[] { filterMsisdn, prefixVO.getNetworkName() }, null);
                    }
                } else {
                    numberAllowed = new NumberPortDAO().isExists(con, filterMsisdn, "", PretupsI.PORTED_OUT);
                    if (numberAllowed) {
                        throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.EXT_USRADD_NOT_IN_SAME_NETWORK, 0, new String[] { filterMsisdn, prefixVO.getNetworkName() }, null);
                    }
                }
            }
            // MNP Code End
            phoneVO.setPinModifyFlag(true);
            // MSISDN validation and entries end

            _newChannelUserVO.setCreatedBy(_creatorVO.getUserID());
            _newChannelUserVO.setCreatedOn(currentDate);
            _newChannelUserVO.setModifiedBy(_creatorVO.getUserID());
            _newChannelUserVO.setModifiedOn(currentDate);

            // Put default grade,trf profile,comm profile and trf rule for the
            // category
            HashMap<String, Object> profileMap = new UserDefaultCache().getCategoryDefaultConfig(categoryCode);
            if (_log.isDebugEnabled() && profileMap != null) {
                _log.debug("ExtUsrController[process]", profileMap.size());
            }
            if (profileMap == null || profileMap.isEmpty()) {
                String[] errArray = { categoryCode };
                throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.EXT_USRADD_PROFILE_ENTRIES_MISSING, 0, errArray, null);
            } else {
                String key = categoryCode + PretupsI.USR_CACHE_GRDCODE_SUFFIX;
                _newChannelUserVO.setUserGrade(profileMap.get(key).toString());
                key = categoryCode + PretupsI.USR_CACHE_TRFPRF_SUFFIX;
                _newChannelUserVO.setTransferProfileID(profileMap.get(key).toString());
                key = categoryCode + PretupsI.USR_CACHE_COMPRF_SUFFIX;
                _newChannelUserVO.setCommissionProfileSetID(profileMap.get(key).toString());
                key = categoryCode + PretupsI.USR_CACHE_ROLECODE_SUFFIX;
                _newChannelUserVO.setGroupRoleCode(profileMap.get(key).toString());
            }
            if (_log.isDebugEnabled()) {
                _log.debug("ExtUsrController[process]", _newChannelUserVO.getUserGrade() + "," + _newChannelUserVO.getTransferProfileID() + "," + _newChannelUserVO.getCommissionProfileSetID() + "," + _newChannelUserVO.getGroupRoleCode());
            }
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW))).booleanValue() == true) {
                ArrayList tfrRuleTypeList = LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_RULE_AT_USER_LEVEL, true);
                boolean isValidRuleType = false;
                Iterator iter = tfrRuleTypeList.iterator();
                while (iter.hasNext()) {
                    ListValueVO listVO = (ListValueVO) iter.next();
                    if (listVO.getValue().equals(ruleType)) {
                        isValidRuleType = true;
                    }
                }
                if (isValidRuleType) {
                    _newChannelUserVO.setTrannferRuleTypeId(ruleType);
                } else {
                    throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.EXT_USRADD_INVALID_RULETYPE);
                }
            }
            // validations for rsa and ssn
            if (!rsa.equals(PretupsI.YES) && !rsa.equals(PretupsI.NO) && !"".equals(rsa)) {
                throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.EXT_USRADD_INVALID_RSA);
            }

            rsaRequired = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.RSA_AUTHENTICATION_REQUIRED, _newChannelUserVO.getNetworkCode(), categoryCode)).booleanValue();

            if (rsaRequired) {
                if (rsa.equals(PretupsI.NO) && !BTSLUtil.isNullString(ssn)) {
                    throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.EXT_USRADD_SSN_NOT_ALLOWED);
                }
                if (rsa.equals(PretupsI.YES) && BTSLUtil.isNullString(ssn)) {
                    throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.EXT_USRADD_SSN_REQUIRD);
                }
            } else {
                if (!rsa.equals(PretupsI.NO)) {
                    String[] errArray = { categoryCode };
                    throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.EXT_USRADD_RSA_NOT_ALLOWED, 0, errArray, null);
                }
                // throw new BTSLBaseException(this,
                // "ExtUsrController[process]",
                // PretupsErrorCodesI.EXT_USRADD_RSA_NOT_ALLOWED);
                if (!BTSLUtil.isNullString(ssn)) {
                    throw new BTSLBaseException(this, "ExtUsrController[process]", PretupsErrorCodesI.EXT_USRADD_SSN_NOT_ALLOWED);
                }
            }
            _newChannelUserVO.setRsaFlag(rsa);
            _newChannelUserVO.setSsn(ssn);
            // end of rsa and ssn validation

            _newChannelUserVO.setInSuspend(PretupsI.NO);
            _newChannelUserVO.setOutSuspened(PretupsI.NO);
            _newChannelUserVO.setUserNamePrefix(PretupsI.UNAME_PREFIX_DEFAULT);
            _newChannelUserVO.setOutletCode(PretupsI.OUTLET_TYPE_DEFAULT);
            _newChannelUserVO.setSubOutletCode(PretupsI.SUB_OUTLET_DEFAULT);
            _newChannelUserVO.setUserProfileID(_newChannelUserVO.getUserID());
            _newChannelUserVO.setMcommerceServiceAllow(PretupsI.NO);
            _newChannelUserVO.setMpayProfileID("");
            _newChannelUserVO.setLowBalAlertAllow(PretupsI.NO);
            _newChannelUserVO.setActivatedOn(currentDate);
            _newChannelUserVO.setCreationType(PretupsI.EXTERNAL_USR_CREATION_TYPE);
            _newChannelUserVO.setInvalidPasswordCount(0);
            _newChannelUserVO.setUserCode(_newChannelUserVO.getMsisdn());
            _newChannelUserVO.setEmpCode(empCode);
            _newChannelUserVO.setCategoryVO(_categoryVO);
            // to load the default services for a category.
            serviceList = (ArrayList<ServiceTypeVO>) UserServicesCache.getObject(categoryCode + "_" + networkCode + "_" + networkCode);
            if (_log.isDebugEnabled() && serviceList != null) {
                _log.debug("process", "_serviceList size= " + serviceList.size());
            } else {
                _log.debug("process", "services list " + serviceList);
            }
            _newChannelUserVO.setServiceList(serviceList);

            // Adding the values in the DB
            count = updateDataBase(con);
            if (_log.isDebugEnabled()) {
                _log.debug("process", "Count= " + count);
            }
            // If values added successfully ie count>0
            if (count > 0) {
                userMap = new HashMap<String, String>();
                userMap.put("GROUPROLE", _newChannelUserVO.getGroupRoleCode());
                userMap.put("COMMPROFILE", _newChannelUserVO.getCommissionProfileSetID());
                userMap.put("TRFPROFILE", _newChannelUserVO.getTransferProfileID());
                userMap.put("GRADE", _newChannelUserVO.getUserGrade());
                _requestMap.put(PretupsI.USER_MAP, userMap);
                _requestVO.setSuccessTxn(true);
                _requestVO.setTxnAuthStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                _requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
                _requestVO.setRequestMap(_requestMap);
            }
        } catch (BTSLBaseException be) {
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _requestVO.setSuccessTxn(false);
            if (be.isKey()) {
                _requestVO.setMessageCode(be.getMessageKey());
                _requestVO.setMessageArguments(be.getArgs());
            } else {
                _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.errorTrace(METHOD_NAME, be);
        } catch (Exception e) {
            _requestVO.setSuccessTxn(false);
            _log.error("process", "Exception " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExtUsrController[process]", "", "", "", "Exception:" + e.getMessage());
            _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            _log.errorTrace(METHOD_NAME, e);
        } finally {
            try {
                if (count == 1) {
                    if (con != null) {
                        mcomCon.finalCommit();
                    }
                } else {
                    if (con != null) {
                        mcomCon.finalRollback();
                    }
                }
				if (mcomCon != null) {
					mcomCon.close("ExtUsrController#process");
					mcomCon = null;
				}
            } catch (SQLException sqlexp) {
                _log.errorTrace(METHOD_NAME, sqlexp);
                _log.error("process", " SqlException : " + sqlexp.getMessage());
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error("process", " Exception : " + e.getMessage());
            }

            // Pushing the SMS to the users.
            if (count > 0) {
                // New User Message.
                if ((_categoryVO.getWebInterfaceAllowed()).equals(PretupsI.YES) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED))).booleanValue() && !BTSLUtil.isNullString(loginId)) {
                    // String
                    // []arr={_newChannelUserVO.getLoginID(),_newChannelUserVO.getMsisdn(),"",BTSLUtil.decryptText(_newChannelUserVO.getPassword()),BTSLUtil.decryptText(_newChannelUserVO.getPrimaryMsisdnPin())};
                    String[] arr = new String[5];
                    arr[0] = _newChannelUserVO.getLoginID();
                    arr[1] = _newChannelUserVO.getMsisdn();
                    arr[2] = "";
                    arr[3] = BTSLUtil.decryptText(_newChannelUserVO.getPassword());
                    arr[4] = BTSLUtil.decryptText(_newChannelUserVO.getPrimaryMsisdnPin());
                    BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.USER_WEB_SMSPIN_ACTIVATE, arr);
                    PushMessage pushMessage = new PushMessage(_newChannelUserVO.getMsisdn(), btslMessage, null, null, locale, networkCode);
                    pushMessage.push();
                } else {
                    String[] arr = new String[3];
                    arr[0] = _newChannelUserVO.getMsisdn();
                    arr[1] = "";
                    arr[2] = BTSLUtil.decryptText(_newChannelUserVO.getPrimaryMsisdnPin());
                    BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.USER_SMSPIN_ACTIVATE, arr);
                    PushMessage pushMessage = new PushMessage(_newChannelUserVO.getMsisdn(), btslMessage, null, null, locale, networkCode);
                    pushMessage.push();
                }

                if (_parentVO != null && (("1").equalsIgnoreCase(Constants.getProperty("DMS_PARENT_MSG_ALLOW")))) {
                    String[] arr = new String[1];
                    arr[0] = _newChannelUserVO.getMsisdn();
                    BTSLMessages btslMessage2 = new BTSLMessages(PretupsErrorCodesI.EXT_USRADD_PARENT_MSG, arr);
                    PushMessage pushMessage2 = new PushMessage(_parentVO.getMsisdn(), btslMessage2, null, null, locale, networkCode);
                    pushMessage2.push();
                }
                if (_creatorVO != null && _creatorVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE) && (("1").equalsIgnoreCase(Constants.getProperty("DMS_OWNER_MSG_ALLOW")))) {
                    BTSLMessages btslMessage = new BTSLMessages(p_requestVO.getMessageCode());
                    if (locale == null) {
                        locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                    }
                    PushMessage pushMessage = new PushMessage(_creatorVO.getMsisdn(), btslMessage, null, null, locale, _creatorVO.getNetworkCode());
                    pushMessage.push();
                }
            } else {
                BTSLMessages btslMessage = new BTSLMessages(p_requestVO.getMessageCode());
                if (locale == null) {
                    locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                }
                if (_creatorVO != null && _creatorVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE)) {
                    PushMessage pushMessage = new PushMessage(_creatorVO.getMsisdn(), btslMessage, null, null, locale, _creatorVO.getNetworkCode());
                    pushMessage.push();
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug("process", "Exiting!!!");
            }
        }
    }

    /**
     * Method to generate the userId while inserting new record
     * 
     * @param p_networkCode
     *            String
     * @param p_prefix
     *            String
     * @return String
     */
    private String generateUserId(String p_networkCode, String p_prefix) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("generateUserId", "Entered p_networkCode=" + p_networkCode + " p_prefix=" + p_prefix);
        }

        int length = Integer.parseInt(Constants.getProperty("USER_PADDING_LENGTH"));
        String id = BTSLUtil.padZeroesToLeft((IDGenerator.getNextID(TypesI.USERID, TypesI.ALL, p_networkCode)) + "", length);

        id = p_networkCode + p_prefix + id;
        if (_log.isDebugEnabled()) {
            _log.debug("generateUserId", "Exiting id=" + id);
        }
        return id;
    }

    /**
     * Method to update the database
     * 
     * @param con
     *            Connection
     * @return int
     */
    private int updateDataBase(Connection con) throws BTSLBaseException {
        final String METHOD_NAME = "updateDataBase";
        if (_log.isDebugEnabled()) {
            _log.debug("updateDataBase", "Entered");
        }

        ArrayList serviceList = null;
        String[] serivceArray = null;
        ArrayList phoneList = null;
        ArrayList geoList = new ArrayList();
        ServicesTypeDAO serviceDAO = new ServicesTypeDAO();
        ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        UserGeographiesDAO userGeographiesDAO = new UserGeographiesDAO();
        UserRolesDAO userRolesDAO = new UserRolesDAO();
        UserGeographiesVO userGeoVO = new UserGeographiesVO();
        UserDAO userDAO = new UserDAO();
        UserTxnDAO usertxnDAO = new UserTxnDAO();
        int count;

        try {
            // Adding in users table
            count = usertxnDAO.addUserForExtApi(con, _newChannelUserVO);
            if (count == 0) {
                throw new BTSLBaseException(this, "updateDataBase", PretupsErrorCodesI.EXT_USRADD_ERROR_WHILE_INSERTION);
            }
            count = 0;

            // Adding in channel_users table
            count = channelUserDAO.addChannelUser(con, _newChannelUserVO);
            if (count == 0) {
                throw new BTSLBaseException(this, "updateDataBase", PretupsErrorCodesI.EXT_USRADD_ERROR_WHILE_INSERTION);
            }
            count = 0;

            // Adding in user_phones
            phoneList = _newChannelUserVO.getMsisdnList();
            count = userDAO.addUserPhoneList(con, phoneList);
            if (count == 0) {
                throw new BTSLBaseException(this, "updateDataBase", PretupsErrorCodesI.EXT_USRADD_ERROR_WHILE_INSERTION);
            }
            count = 0;

            // Adding service list for the user
            serviceList = _newChannelUserVO.getServiceList();
            if (_newChannelUserVO.getCategoryVO().getServiceAllowed().equals(PretupsI.YES)) {
                if (serviceList != null && serviceList.size() > 0) {
                    serivceArray = new String[serviceList.size()];
                    for (int i = 0, j = serviceList.size(); i < j; i++) {
                        ServiceTypeVO serviceTypeVO = (ServiceTypeVO) serviceList.get(i);
                        serivceArray[i] = serviceTypeVO.getServiceType();
                    }
                }
                if (_log.isDebugEnabled()) {
                    _log.debug("updateDataBase", "Service Array:" + serivceArray);
                }
                if (serivceArray != null && serivceArray.length > 0) {
                    count = serviceDAO.addUserServicesList(con, _newChannelUserVO.getUserID(), serivceArray, PretupsI.YES);
                    if (count == 0) {
                        throw new BTSLBaseException(this, "updateDataBase", PretupsErrorCodesI.EXT_USRADD_ERROR_WHILE_INSERTION);
                    }
                    count = 0;
                }
            }
            // Adding in user_geographies
            userGeoVO.setUserId(_newChannelUserVO.getUserID());
            userGeoVO.setGraphDomainCode(_newChannelUserVO.getGeographicalCode());
            geoList.add(userGeoVO);
            count = userGeographiesDAO.addUserGeographyList(con, geoList);
            if (count == 0) {
                throw new BTSLBaseException(this, "updateDataBase", PretupsErrorCodesI.EXT_USRADD_ERROR_WHILE_INSERTION);
            }
            count = 0;

            // Adding roles for the user. *[only when group role will be given]
            if (!BTSLUtil.isNullString(_newChannelUserVO.getGroupRoleCode())) {
                String[] defaultroleList = null;
                defaultroleList = new String[1];
                defaultroleList[0] = _newChannelUserVO.getGroupRoleCode();
                count = userRolesDAO.addUserRolesList(con, _newChannelUserVO.getUserID(), defaultroleList);
                if (count == 0) {
                    throw new BTSLBaseException(this, "updateDataBase", PretupsErrorCodesI.EXT_USRADD_ERROR_WHILE_INSERTION);
                }
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            BTSLMessages bmSqlError = new BTSLMessages("error.general.sql.processing");
            BTSLMessages bmGeneralError = new BTSLMessages("error.general.processing");
            if ((be.getMessage().equals(bmSqlError.getMessageKey())) || (be.getMessage().equals(bmGeneralError.getMessageKey()))) {
                throw new BTSLBaseException(this, "updateDataBase", PretupsErrorCodesI.EXT_USRADD_EXCEPTION);
            } else {
                throw be;
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "updateDataBase", PretupsErrorCodesI.EXT_USRADD_EXCEPTION);
        }
        return count;
    }

}

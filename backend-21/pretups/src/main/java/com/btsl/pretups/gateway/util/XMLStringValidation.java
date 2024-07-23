package com.btsl.pretups.gateway.util;

/**
 * @description : This class will be used to validate the XML request buffer
 *              based on standard API for
 *              User Add/Modify/Delete/Suspend/Resume/Role- Add/Delete
 *              /MNP/ICCID-MSISDN Mapping.
 * @author diwakar
 * @date : 23-JAN-2014
 */
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.RegularExpression;
import com.btsl.util.XMLTagValueValidation;
import com.btsl.util.XmlTagValueConstant;

public class XMLStringValidation {
    public static final Log _log = LogFactory.getLog(XMLStringValidation.class.getName());
    // Added by Diwakar on 03-MAR-2014
    private static String[] _errorMsg = new String[3];
    public static final boolean _tagManadatory = true;
    public static final boolean _tagOptinal = false;

    public static OperatorUtilI _operatorUtil = null;
    private static String irisDateFormat = null;
    private static Integer maxMsisdnLength = 10;
    private static Integer minMsisdnLength = 5;
    private static Integer maxSmsPinLength = 4;
    private static Integer minSmsPinLength = 4;
    private static Integer maxLoginPwdLength = 10;
    private static Integer minLoginPwdLength = 5;
    private static Integer vomsDamgPinLnthAllow = 4;
    private static Integer vomsPinMaxLength = 10;
    private static Integer vomsSerialNoMaxLength = 10;
    private static String externalDateFormat = null;
    private static Boolean isExternalCodeMandatoryForUser = false;
    private static Boolean isVoucherProfileOptional = false;
    private static StringBuilder sb =  new StringBuilder(1024);
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static block", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLTagValueValidation[initialize]", "", "", "",
                "Exception while loading the class at the call:"+e.getMessage());
        }
        irisDateFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.IRIS_DATE_FORMAT);
        maxMsisdnLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE);
        minMsisdnLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH_CODE);
        minSmsPinLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SMS_PIN_LENGTH);
        maxSmsPinLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH);
        minLoginPwdLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH);
        maxLoginPwdLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH);
        vomsDamgPinLnthAllow = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_DAMG_PIN_LNTH_ALLOW);
        vomsPinMaxLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MAX_LENGTH);
        vomsSerialNoMaxLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MAX_LENGTH);
        externalDateFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_DATE_FORMAT);
        isExternalCodeMandatoryForUser = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_CODE_MANDATORY_FORUSER);
        isVoucherProfileOptional = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOUCHER_PROFLE_IS_OPTIONAL);
    }

    /**
   	 * ensures no instantiation
   	 */
    private XMLStringValidation(){
    	
    }
    
    public static boolean isTagManadatory() {
        return _tagManadatory;
    }

    public static boolean isTagOptinal() {
        return _tagOptinal;
    }

    // Ended HerE

    /**
     * @description: This method will validate the user change password request
     *               coming through external system.
     * @author :diwakar
     * @date : 23-JAN-2014
     * @throws BTSLBaseException
     */
    public static void validateExtChannelUserChangePasswordRequest(RequestVO p_requestVO, String type, String reqDate, String externalNetworkCode, String categoryCode, String employeeCode, String requestLoginId, String password, String extRefNum, String userLoginId, String userMsisdn, String newPassword, String confirmNewPassword) throws BTSLBaseException {
    	final String METHOD_NAME = "validateExtChannelUserChangePasswordRequest";
		sb.setLength(0);
		LogFactory.printLog(METHOD_NAME, sb.append("Entered p_requestVO ID=").append(p_requestVO.getRequestIDStr()), _log);
    	sb.setLength(0);
        LogFactory.printLog(METHOD_NAME, sb.append("type =  ").append(type ).append( "  | reqDate = ").append(reqDate ).append( "  |  externalNetworkCode =").append(externalNetworkCode ).append( "  | employeeCode=  ").append(employeeCode ).append( "  | userMsisdn = ").append(userMsisdn ).append( "  | requestLoginId = ").append(requestLoginId ).append( "  | extRefNum = ").append(extRefNum ).append( "  | newPassword =  ").append(newPassword ).append( "  |  confirmNewPassword =").append(confirmNewPassword).toString(), _log);
        final String[] errorMsg = new String[2];
        try {
            // Creation Date Validation : For user Creation/Registration Date
            // must be specified.
            if (BTSLUtil.isNullString(reqDate)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_DATE_BLANK);
            } else {
                try {
                    BTSLUtil.getDateFromDateString(reqDate, externalDateFormat);
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT);
                }
            }

            // Mandatory: Sender External Network Code validation
            if (BTSLUtil.isNullString(externalNetworkCode)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_BLANK);
            } else if (externalNetworkCode.length() > 2) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID);
            } else {
                alphanumericWithSpecialChar(externalNetworkCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EXTNWCODE, XMLStringValidation
                    .isTagManadatory());
            }

            // Optional: Sender CATCODE not null and length check
            if (!BTSLUtil.isNullString(categoryCode) && categoryCode.length() > 10) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,
                    PretupsErrorCodesI.EXTSYS_REQ_USERCATCODE_BLANK_OR_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(categoryCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_CATCODE, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional: EmployeeCode validation
            if (!BTSLUtil.isNullString(employeeCode) && employeeCode.length() > 12) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EMPCODE_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(employeeCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EMPCODE, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional: Sender LoginID validation
            if (!BTSLUtil.isNullString(requestLoginId) && requestLoginId.length() > 20) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(requestLoginId, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_LOGIN_ID, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional: Sender Password validation
            if (!BTSLUtil.isNullString(password) && password.length() > (int)maxLoginPwdLength) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(password, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_PASSWORD, XMLStringValidation.isTagOptinal());
            }

            // Optional: Sender external reference number validation
            if (!BTSLUtil.isNullString(extRefNum) && extRefNum.length() > 15) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTREFNUM_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(extRefNum, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EXTREFNUM, XMLStringValidation
                    .isTagOptinal());
            }

            if (BTSLUtil.isNullString(employeeCode) && (BTSLUtil.isNullString(password) && BTSLUtil.isNullString(requestLoginId))) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_SENDER_DETAILS_INVALID);
            }

            //

            // Optional: Existing MSISDN.
            if (!BTSLUtil.isNullString(userMsisdn)) {
                errorMsg[0] = PretupsErrorCodesI.EXTSYS_REQ_MSISDN_INVALID_FORMAT;
                validateMSISDN(userMsisdn, externalNetworkCode, PretupsErrorCodesI.EXTSYS_REQ_MSISDN_INVALID_FORMAT, errorMsg);
                numeric(userMsisdn, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, XmlTagValueConstant.TAG_USERMSISDN, XMLStringValidation.isTagOptinal());

            }

            // Mandatory: LOGIN ID length check.
            if (!BTSLUtil.isNullString(userLoginId) && userLoginId.length() > 20) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(userLoginId, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_LOGIN_ID, XMLStringValidation
                    .isTagOptinal());
            }

            // Mandatory: NEWPASSWD
            if (!BTSLUtil.isNullString(newPassword) && newPassword.length() > (int)maxLoginPwdLength) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_LENGTH_EXCEEDS);
            } else {
                alphanumericSpecialCharAtleast(newPassword, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL_ATLEAST, XmlTagValueConstant.TAG_NEWPASSWD, XMLStringValidation
                    .isTagManadatory());
            }

            // Mandatory: CONFIRMPASSWD
            if (!BTSLUtil.isNullString(confirmNewPassword) && confirmNewPassword.length() > (int)maxLoginPwdLength) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_LENGTH_EXCEEDS);
            } else {
                alphanumericSpecialCharAtleast(confirmNewPassword, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL_ATLEAST, XmlTagValueConstant.TAG_CONFIRMPASSWD,
                    XMLStringValidation.isTagManadatory());
            }

            if (!newPassword.equalsIgnoreCase(confirmNewPassword)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.CHANGE_PASSWORD_NEW_CONFIRM_NOTSAME);
            }
            if ((BTSLUtil.isNullString(userLoginId) && BTSLUtil.isNullString(userMsisdn))) {
                throw new BTSLBaseException("XMLStringValidation",  METHOD_NAME, PretupsErrorCodesI.CHANGE_PASSWORD_LOGIID_MSISDN_BLANK);
            }

        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());
            throw be;
        } finally {
            sb.setLength(0);
            LogFactory.printLog(METHOD_NAME, sb.append("Exiting p_requestVO ID=").append(p_requestVO.getRequestIDStr()).toString(), _log);
        }
    }

    /**
     * @description: This method will validate the user's add request coming
     *               through external system.
     * @author :diwakar
     * @date : 23-JAN-2014
     * @throws BTSLBaseException
     */
    public static void validateExtChannelUserAddRequest(RequestVO p_requestVO, String type, String reqDate, String externalNetworkCode, String employeeCode, String msisdn, String requestLoginId, String password, String extRefNum, String parentMsisdn, String userCategory, String userName, String userNamePrefix, String externalCode, String msisdn1, String shortName, String userNamePrefix2, String subscriberCode, String contactPerson, String contactNumber, String ssn, String address1, String address2, String city, String state, String country, String emailId, String webloginId, String webPassword, String pin, String msisdn2, String msisdn3) throws BTSLBaseException {
    	final String METHOD_NAME = "validateExtChannelUserAddRequest";
        sb.setLength(0);
    	LogFactory.printLog(METHOD_NAME, sb.append("Entered p_requestVO ID=").append(p_requestVO.getRequestIDStr()), _log);
		sb.setLength(0);
		LogFactory.printLog(METHOD_NAME, sb.append("type =  ").append(type ).append( "  | reqDate = ").append(reqDate ).append( "  |  externalNetworkCode =").append(externalNetworkCode ).append( "  | employeeCode=  ").append(employeeCode ).append( "  | msisdn = ").append(msisdn ).append( "  | requestLoginId = ").append(requestLoginId ).append( "  | extRefNum = ").append(extRefNum ).append( "  | parentMsisdn =  ").append(parentMsisdn ).append( "  |  userCategory =").append(userCategory ).append( "  | userName =  ").append(userName ).append( "  | userNamePrefix = ").append(userNamePrefix ).append( "  | externalCode=   ").append(externalCode ).append( "  | msisdn1 ").append(msisdn1).toString(), _log);
        final String[] errorMsg = new String[2];
        try {
            // Creation Date Validation : For user Creation/Registration Date
            // must be specified.
            if (BTSLUtil.isNullString(reqDate)) {
                throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_DATE_BLANK);
            } else {
                try {
                    BTSLUtil.getDateFromDateString(reqDate, externalDateFormat);
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT);
                }
            }

            // Mandatory: Sender External Network Code validation
            if (BTSLUtil.isNullString(externalNetworkCode)) {
                throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_BLANK);
            } else if (externalNetworkCode.length() > 2) {
                throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID);
            } else {
                alphanumericWithSpecialChar(externalNetworkCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EXTNWCODE, XMLStringValidation
                    .isTagManadatory());
            }

            // Optional: EmployeeCode validation
            if (!BTSLUtil.isNullString(employeeCode) && employeeCode.length() > 12) {
                throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_EMPCODE_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(employeeCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EMPCODE, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional: Sender LoginID validation
            if (!BTSLUtil.isNullString(requestLoginId) && requestLoginId.length() > 20) {
                throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_LENGTH_EXCEEDS);
            }

            // Optional: Sender Password validation
            if (!BTSLUtil.isNullString(password) && password.length() > (int)maxLoginPwdLength) {
                throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(password, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_PASSWORD, XMLStringValidation.isTagOptinal());
            }

            // Optional: Sender MSISDN validation
            if (!BTSLUtil.isNullString(msisdn)) {
                validateMSISDN(msisdn, externalNetworkCode, PretupsErrorCodesI.EXTSYS_REQ_MSISDN_INVALID_FORMAT, errorMsg);
            }
            numeric(msisdn, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, XmlTagValueConstant.TAG_MSISDN, XMLStringValidation.isTagOptinal());

            // Optional: Sender Pin validation
            if (!BTSLUtil.isNullString(pin) && pin.length() > (int)maxSmsPinLength) {
                throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_PIN_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(pin, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_PIN, XMLStringValidation.isTagOptinal());
            }

            // Optional: Sender external reference number validation
            if (!BTSLUtil.isNullString(extRefNum) && extRefNum.length() > 15) {
                throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_EXTREFNUM_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(extRefNum, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EXTREFNUM, XMLStringValidation
                    .isTagOptinal());
            }

            if (BTSLUtil.isNullString(employeeCode) && BTSLUtil.isNullString(msisdn) && (BTSLUtil.isNullString(password) || BTSLUtil.isNullString(requestLoginId))) {
                throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_SENDER_EMPLOYEE_DETAILS_INVALID);
            }

            //

            // Optional: Parent MSISDN.
            if (!BTSLUtil.isNullString(parentMsisdn)) {
                validateMSISDN(parentMsisdn, externalNetworkCode, PretupsErrorCodesI.EXTSYS_REQ_PARENTMSISDN_INVALID_FORMAT, errorMsg);
                numeric(parentMsisdn, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, XmlTagValueConstant.TAG_PARENTMSISDN, XMLStringValidation.isTagOptinal());
            }

            // Mandatory: PRIMARY MSISDN((MSISDN1)) of new user can't be blank
            // in the user creation request.
            if (BTSLUtil.isNullString(msisdn1)) {
                throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_PRIMARY_MSISDN_BLANK);
            }

            // Mandatory: New MSISDN and EXTERNAL NETWORK CODE not null and
            // length check
            validateMSISDN(msisdn1, externalNetworkCode, PretupsErrorCodesI.EXTSYS_REQ_MSISDN_INVALID_FORMAT, errorMsg);
            numeric(msisdn1, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, XmlTagValueConstant.TAG_MSISDN1, XMLStringValidation.isTagManadatory());

            // Added validation for 2'nd & 3'rd MSISDN on 21-02-2014
            if (!BTSLUtil.isNullString(msisdn2)) {
                validateMSISDN(msisdn2, externalNetworkCode, PretupsErrorCodesI.EXTSYS_REQ_MSISDN_INVALID_FORMAT, errorMsg);
                numeric(msisdn2, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, XmlTagValueConstant.TAG_MSISDN2, XMLStringValidation.isTagOptinal());
            }
            if (!BTSLUtil.isNullString(msisdn3)) {
                validateMSISDN(msisdn3, externalNetworkCode, PretupsErrorCodesI.EXTSYS_REQ_MSISDN_INVALID_FORMAT, errorMsg);
                numeric(msisdn3, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, XmlTagValueConstant.TAG_MSISDN3, XMLStringValidation.isTagOptinal());
            }
            // Ended Here

            // Mandatory: Old EXTERNALCODE CODE not null and length check
            if (BTSLUtil.isNullString(userCategory) || userCategory.length() > 10) {
                throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_USERCATCODE_BLANK_OR_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(userCategory, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_USERCATCODE, XMLStringValidation
                    .isTagManadatory());
            }

            // Mandatory: New NEWEXTERNALCODE not null and length check
            if (BTSLUtil.isNullString(userName) || userName.length() > 80) {
                throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_USERNAME_BLANK_OR_LENGTH_INVALID);
            } else {
                alphanumericWithSpecialChar(userName, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_USERNAME, XMLStringValidation
                    .isTagManadatory());
            }

            // Optional: New User SHORT NAME length check.
            if (!BTSLUtil.isNullString(shortName) && shortName.length() > 15) {
                throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_SHORTNAME_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(shortName, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_SHORTNAME, XMLStringValidation
                    .isTagOptinal());
            }

            // Mandatory: New USER NAME PREFIX not null and length check
            if (BTSLUtil.isNullString(userNamePrefix) || userNamePrefix.length() > 10) {
                throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_USERNAMEPREFIX_BLANK_OR_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(userNamePrefix, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_USERNAMEPREFIX, XMLStringValidation
                    .isTagManadatory());
            }

            // Optional:New user's SUBSCRIBER CODE field's length check.
            if (!BTSLUtil.isNullString(subscriberCode) && subscriberCode.length() > 12) {
                throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_SUBSCRIBERCODE_BLANK_OR_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(subscriberCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_USERNAME, XMLStringValidation
                    .isTagOptinal());
            }

            // Mandatory: New user EXTERNAL CODE not null and length check
            if (BTSLUtil.isNullString(externalCode) || externalCode.length() > 20) {
                throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_EXTERNALCODE_BLANK_OR_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(externalCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EXTCODE, XMLStringValidation
                    .isTagManadatory());
            }

            // Optional: CONTACT PERSON field's length check.
            if (!BTSLUtil.isNullString(contactPerson) && contactPerson.length() > 80) {
                throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_CONTACTPERSON_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(contactPerson, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_CONTACTPERSON, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional: CONTACT NUMBER field's length and numeric check.
            if (!BTSLUtil.isNullString(contactNumber)) {
                if (contactNumber.length() > 50) {
                    throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_CONTACTNUMBER_LENGTH_EXCEEDS);
                } else if (!BTSLUtil.isValidNumber(contactNumber)) {
                    errorMsg[0] = contactNumber.trim();
                    throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_CONTACTNUMBER_NON_NUMERIC, errorMsg);
                }
            }

            // Optional: SSN field's length check.
            if (!BTSLUtil.isNullString(ssn) && ssn.length() > 15) {
                throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_SSN_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(ssn, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_SSN, XMLStringValidation.isTagOptinal());
            }

            // Optional: ADDRESS1 field's length check.
            if (!BTSLUtil.isNullString(address1) && address1.length() > 50) {
                throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_ADDRESS1_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(address1, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_ADDRESS1, XMLStringValidation.isTagOptinal());
            }

            // Optional: ADDRESS2 field's length check.
            if (!BTSLUtil.isNullString(address2) && address2.length() > 50) {
                throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_ADDRESS2_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(address2, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_ADDRESS2, XMLStringValidation.isTagOptinal());
            }

            // Optional: CITY field's length check.
            if (!BTSLUtil.isNullString(city) && city.length() > 30) {
                throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_CITY_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(city, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_CITY, XMLStringValidation.isTagOptinal());
            }

            // Optional: STATE field's length check.
            if (!BTSLUtil.isNullString(state) && state.length() > 30) {
                throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_STATE_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(state, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_STATE, XMLStringValidation.isTagOptinal());
            }

            // Optional: COUNTRY field's length check.
            if (!BTSLUtil.isNullString(country) && country.length() > 20) {
                throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_COUNTRY_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(country, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_CITY, XMLStringValidation.isTagOptinal());
            }

            // Optional: EMAIL ID field's length & format check.
            if (!BTSLUtil.isNullString(emailId)) {
                if (emailId.length() > 60) {
                    throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_EMAILID_LENGTH_EXCEEDS);
                } else if (!BTSLUtil.validateEmailID(emailId)) {
                    errorMsg[0] = emailId.trim();
                    throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_EMAILID_INVALID_FORMAT, errorMsg);
                }
            }

            // Optional: New user WEB LOGIN ID not null and length check.
            if (!BTSLUtil.isNullString(webloginId) && webloginId.length() > 20) {
                throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(webloginId, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_WEBLOGINID, XMLStringValidation
                    .isTagOptinal());
            }

            // webPassword validation
            if (!BTSLUtil.isNullString(webPassword) && webPassword.length() > (int)maxLoginPwdLength) {
                throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_LENGTH_EXCEEDS);
            }

        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());
            throw be;
        } finally {
            	sb.setLength(0);
                LogFactory.printLog(METHOD_NAME, sb.append("Exiting p_requestVO ID=").append(p_requestVO.getRequestIDStr()), _log);
        }
    }

    /**
     * @description : This method will validate the user modification request
     *              coming through external system.
     * @author : diwakar
     * @date : 23-JAN-2014
     * @throws BTSLBaseException
     */
    public static void validateExtChannelUserModifyRequest(RequestVO p_requestVO, String type, String reqDate, String externalNetworkCode, String employeeCode, String msisdn, String requestLoginId, String password, String extRefNum, String existingUserMsisdn, String externalCode, String newExtCode, String userName, String userNamePrefix, String msisdn1, String shortName, String subscriberCode, String contactPerson, String contactNumber, String ssn, String address1, String address2, String city, String state, String country, String emailId, String webloginId, String webPassword, String pin, String msisdn2, String msisdn3) throws BTSLBaseException {
    	final String METHOD_NAME = "validateExtChannelUserModifyRequest";
		sb.setLength(0);
		LogFactory.printLog(METHOD_NAME, sb.append("Entered p_requestVO ID=").append(p_requestVO.getRequestIDStr()), _log);
        final String[] errorMsg = new String[2];
        final HashMap requestHashMap = p_requestVO.getRequestMap();
        try {
            // Modification Date Validation : For user Creation/Registration
            // Date must be specified.
            if (BTSLUtil.isNullString(reqDate)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_DATE_BLANK);
            } else {
                try {
                    BTSLUtil.getDateFromDateString(reqDate, externalDateFormat);
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT);
                }
            }
            // Mandatory: Sender External Network Code validation
            if (BTSLUtil.isNullString(externalNetworkCode)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_BLANK);
            } else if (externalNetworkCode.length() > 2) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID);
            } else {
                alphanumericWithSpecialChar(externalNetworkCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EXTNWCODE, XMLStringValidation
                    .isTagManadatory());
            }

            // Optional: EmployeeCode validation
            if (!BTSLUtil.isNullString(employeeCode) && employeeCode.length() > 12) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EMPCODE_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(employeeCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EMPCODE, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional: Sender LoginID validation
            if (!BTSLUtil.isNullString(requestLoginId) && requestLoginId.length() > 20) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(requestLoginId, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_LOGIN_ID, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional: Sender Password validation
            if (!BTSLUtil.isNullString(password) && password.length() > (int)maxLoginPwdLength) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(password, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_PASSWORD, XMLStringValidation.isTagOptinal());
            }

            // Optional: Sender MSISDN validation
            if (!BTSLUtil.isNullString(msisdn)) {
                validateMSISDN(msisdn, externalNetworkCode, PretupsErrorCodesI.EXTSYS_REQ_MSISDN_INVALID_FORMAT, errorMsg);
            }
            numeric(msisdn, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, XmlTagValueConstant.TAG_MSISDN, XMLStringValidation.isTagOptinal());

            // Optional: Sender Pin validation
            if (!BTSLUtil.isNullString(pin) && pin.length() > (int)maxSmsPinLength) {
                throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_PIN_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(pin, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_PIN, XMLStringValidation.isTagOptinal());
            }

            // Optional: Sender external reference number validation
            if (!BTSLUtil.isNullString(extRefNum) && extRefNum.length() > 15) {
                throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest", PretupsErrorCodesI.EXTSYS_REQ_EXTREFNUM_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(extRefNum, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EXTREFNUM, XMLStringValidation
                    .isTagOptinal());
            }

            if (BTSLUtil.isNullString(employeeCode) && BTSLUtil.isNullString(msisdn) && (BTSLUtil.isNullString(password) || BTSLUtil.isNullString(requestLoginId))) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_SENDER_DETAILS_INVALID);
            }

            //

            // Mandatory: USERMSISDN that exists into the system.
            if (BTSLUtil.isNullString(existingUserMsisdn)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_USER_MSISDN_BLANK);
            }
            validateMSISDN(existingUserMsisdn, externalNetworkCode, PretupsErrorCodesI.EXTSYS_REQ_MSISDN_INVALID_FORMAT, errorMsg);
            numeric(existingUserMsisdn, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC, XmlTagValueConstant.TAG_USERMSISDN, XMLStringValidation.isTagOptinal());

            // Mandatory: New USERCAT CODE not null and length check
            if (!BTSLUtil.isNullString(externalCode)) {
                if (externalCode.length() > 20) {
                    throw new BTSLBaseException("XMLStringValidation", "validateExtChannelUserAddRequest",
                            PretupsErrorCodesI.EXTSYS_REQ_EXTERNALCODE_BLANK_OR_LENGTH_EXCEEDS);
                } else {
                    alphanumericWithSpecialChar(externalCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL,
                            XmlTagValueConstant.TAG_EXTCODE, XMLStringValidation.isTagManadatory());
                }
            }

            // Mandatory: New USERCAT CODE not null and length check
                alphanumericWithSpecialChar(newExtCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_NEWEXTERNALCODE, XMLStringValidation
                    .isTagManadatory());
            // Mandatory: New USER NAME not null and length check
            if (BTSLUtil.isNullString(userName) || userName.length() > 80) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_USERNAME_BLANK_OR_LENGTH_INVALID);
            } else {
                alphanumericWithSpecialChar(userName, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_USERNAME, XMLStringValidation
                    .isTagManadatory());
            }

            // Optional: New User SHORT NAME length check.
            if (!BTSLUtil.isNullString(shortName) && shortName.length() > 15) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_SHORTNAME_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(shortName, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_SHORTNAME, XMLStringValidation
                    .isTagOptinal());
            }

            // Mandatory: New USER NAME PREFIX not null and length check
            if (BTSLUtil.isNullString(userNamePrefix) || userNamePrefix.length() > 10) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_USERNAMEPREFIX_BLANK_OR_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(userNamePrefix, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_USERNAMEPREFIX, XMLStringValidation
                    .isTagManadatory());
            }

            // Optional:New user's SUBSCRIBER CODE field's length check.
            if (!BTSLUtil.isNullString(subscriberCode) && subscriberCode.length() > 12) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_SUBSCRIBERCODE_BLANK_OR_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(subscriberCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_SUBSCRIBERCODE, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional: CONTACT PERSON field's length check.
            if (!BTSLUtil.isNullString(contactPerson) && contactPerson.length() > 80) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_CONTACTPERSON_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(contactPerson, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_CONTACTPERSON, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional: CONTACT NUMBER field's length and numeric check.
            if (!BTSLUtil.isNullString(contactNumber)) {
                if (contactNumber.length() > 50) {
                    throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_CONTACTNUMBER_LENGTH_EXCEEDS);
                } else if (!BTSLUtil.isValidNumber(contactNumber)) {
                    errorMsg[0] = contactNumber.trim();
                    throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_CONTACTNUMBER_NON_NUMERIC,
                        errorMsg);
                }
            }

            // Optional: SSN field's length check.
            if (!BTSLUtil.isNullString(ssn) && ssn.length() > 15) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_SSN_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(ssn, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_SSN, XMLStringValidation.isTagOptinal());
            }

            // Optional: ADDRESS1 field's length check.
            if (!BTSLUtil.isNullString(address1) && address1.length() > 50) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_ADDRESS1_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(address1, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_ADDRESS1, XMLStringValidation.isTagOptinal());
            }

            // Optional: ADDRESS2 field's length check.
            if (!BTSLUtil.isNullString(address2) && address2.length() > 50) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_ADDRESS2_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(address2, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_ADDRESS2, XMLStringValidation.isTagOptinal());
            }

            // Optional: CITY field's length check.
            if (!BTSLUtil.isNullString(city) && city.length() > 30) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_CITY_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(city, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_CITY, XMLStringValidation.isTagOptinal());
            }

            // Optional: STATE field's length check.
            if (!BTSLUtil.isNullString(state) && state.length() > 30) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_STATE_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(state, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_STATE, XMLStringValidation.isTagOptinal());
            }

            // Optional: COUNTRY field's length check.
            if (!BTSLUtil.isNullString(country) && country.length() > 20) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_COUNTRY_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(country, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_COUNTRY, XMLStringValidation.isTagOptinal());
            }

            // Optional: EMAIL ID field's length & format check.
            if (!BTSLUtil.isNullString(emailId)) {
                if (emailId.length() > 60) {
                    throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EMAILID_LENGTH_EXCEEDS);
                } else if (!BTSLUtil.validateEmailID(emailId)) {
                    errorMsg[0] = emailId.trim();
                    throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EMAILID_INVALID_FORMAT, errorMsg);
                }
            }

            // Mandatory: New user WEB LOGIN ID not null and length check.
            /*if (BTSLUtil.isNullString(webloginId)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_BLANK);
            } else */
            	if (webloginId.length() > 20) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(webloginId, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_WEBLOGINID, XMLStringValidation
                    .isTagOptinal());
            }

            // webPassword validation
            if (!BTSLUtil.isNullString(webPassword) && webPassword.length() > (int)maxLoginPwdLength) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(webPassword, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_WEBPASSWORD, XMLStringValidation
                    .isTagOptinal());
            }

            // Mandatory: PRIMARY MSISDN((MSISDN1)) of new user can't be blank
            // in the user creation request.
            if (BTSLUtil.isNullString(msisdn1)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_NEW_PRIMARY_MSISDN_BLANK);
            }

            // Mandatory: New MSISDN and EXTERNAL NETWORK CODE not null and
            // length check
            validateMSISDN(msisdn1, externalNetworkCode, PretupsErrorCodesI.EXTSYS_REQ_MSISDN_INVALID_FORMAT, errorMsg);
            numeric(msisdn1, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, XmlTagValueConstant.TAG_MSISDN1, XMLStringValidation.isTagManadatory());

            // Added validation for 2'nd & 3'rd MSISDN on 21-02-2014
            if (!BTSLUtil.isNullString(msisdn2)) {
                validateMSISDN(msisdn2, externalNetworkCode, PretupsErrorCodesI.EXTSYS_REQ_MSISDN_INVALID_FORMAT, errorMsg);
                numeric(msisdn2, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, XmlTagValueConstant.TAG_MSISDN2, XMLStringValidation.isTagOptinal());
            }
            if (!BTSLUtil.isNullString(msisdn3)) {
                validateMSISDN(msisdn3, externalNetworkCode, PretupsErrorCodesI.EXTSYS_REQ_MSISDN_INVALID_FORMAT, errorMsg);
                numeric(msisdn3, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, XmlTagValueConstant.TAG_MSISDN3, XMLStringValidation.isTagOptinal());
            }
            // Ended Here

        } catch (BTSLBaseException be) {
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());
            throw be;
        } finally {
            sb.setLength(0);
            LogFactory.printLog(METHOD_NAME, sb.append("Exiting p_requestVO ID=").append(p_requestVO.getRequestIDStr()), _log);
            
        }
    }

    /**
     * @description : This method will validate the user delete request coming
     *              through external system.
     * @author : diwakar
     * @date : 23-JAN-2014
     * @throws BTSLBaseException
     */
    public static void validateExtChannelUserDeleteRequest(RequestVO p_requestVO, String type, String reqDate, String externalNetworkCode, String categoryCode, String employeeCode, String msisdn, String operatorLoginId, String operatorPassword, String extRefNum, String userLoginId, String externalCode, String remark) throws BTSLBaseException {
    	final String METHOD_NAME = "validateExtChannelUserDeleteRequest";
    	sb.setLength(0);
        LogFactory.printLog(METHOD_NAME, sb.append("Entered p_requestVO ID=").append(p_requestVO.getRequestIDStr()), _log);
        final String[] errorMsg = new String[2];
        final HashMap requestHashMap = p_requestVO.getRequestMap();
        try {
            // Modification Date Validation : For user Creation/Registration
            // Date must be specified.
            if (BTSLUtil.isNullString(reqDate)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_DATE_BLANK);
            } else {
                try {
                    BTSLUtil.getDateFromDateString(reqDate, externalDateFormat);
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT);
                }
            }

            // Optional: EXTCODE not null and length check
            if (!BTSLUtil.isNullString(externalCode) && externalCode.length() > 12) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTERNALCODE_BLANK_OR_LENGTH_EXCEEDS);
            } else if (!BTSLUtil.isNullString(externalCode)) {
                // else case also on
                // 11-MAR-2014
                alphanumericWithSpecialChar(externalCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EXTCODE, XMLStringValidation
                    .isTagManadatory());
            }

            // 26-FEB-2014
            if (BTSLUtil.isNullString(externalNetworkCode)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_BLANK);
            } else if (externalNetworkCode.length() > 2) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID);
            } else {
                alphanumericWithSpecialChar(externalNetworkCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EXTNWCODE, XMLStringValidation
                    .isTagManadatory());
            }

            // Ended Here

            // Optional: CATCODE not null and length check
            if (!BTSLUtil.isNullString(categoryCode) && categoryCode.length() > 10) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_USERCATCODE_BLANK_OR_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(categoryCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_CATCODE, XMLStringValidation
                    .isTagOptinal());
            }

            // Mandatory: EMPCODE not null and length check
            if (!BTSLUtil.isNullString(extRefNum) && extRefNum.length() > 15) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTREFNUM_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(extRefNum, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EXTREFNUM, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional: EMPCODE length check
            if (!BTSLUtil.isNullString(employeeCode) && employeeCode.length() > 12) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EMPCODE_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(employeeCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EMPCODE, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional: Sender LoginID validation
            if (!BTSLUtil.isNullString(operatorLoginId) && operatorLoginId.length() > 20) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(operatorLoginId, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_LOGIN_ID, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional: Sender Password validation
            if (!BTSLUtil.isNullString(operatorPassword) && operatorPassword.length() > (int)maxLoginPwdLength) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(operatorPassword, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_PASSWORD, XMLStringValidation
                    .isTagOptinal());
            }

            if (BTSLUtil.isNullString(employeeCode) && BTSLUtil.isNullString(externalCode) && (BTSLUtil.isNullString(operatorPassword) || BTSLUtil.isNullString(operatorLoginId))) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_SENDER_DETAILS_INVALID);
            }

            // Mandatory: External Network Code validation
            if (BTSLUtil.isNullString(externalNetworkCode)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_BLANK);
            } else if (externalNetworkCode.length() > 2) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID);
            } else {
                alphanumericWithSpecialChar(externalNetworkCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EXTNWCODE, XMLStringValidation
                    .isTagManadatory());
            }

            // Ended Here
            if (BTSLUtil.isNullString(msisdn) && BTSLUtil.isNullString(userLoginId)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_RECEIVER_DETAILS_INVALID);
            }

            // Mandatory: MSISDN of user that exists into the system.
            if (!BTSLUtil.isNullString(msisdn)) {
                validateMSISDN(msisdn, externalNetworkCode, PretupsErrorCodesI.EXTSYS_REQ_MSISDN_INVALID_FORMAT, errorMsg);
            }
            numeric(msisdn, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, XmlTagValueConstant.TAG_MSISDN, XMLStringValidation.isTagOptinal());

            // Optional: New user WEB LOGIN ID not null and length check.
            if (!BTSLUtil.isNullString(userLoginId) && userLoginId.length() > 20) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_LENGTH_EXCEEDS);
            }

        } catch (BTSLBaseException be) {
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());
            throw be;
        } finally {
        	sb.setLength(0);
            LogFactory.printLog(METHOD_NAME, sb.append("Exiting p_requestVO ID=").append(p_requestVO.getRequestIDStr()), _log);            
        }
    }

    /**
     * @description : This method will validate the user suspend/resume request
     *              coming through external system.
     * @author : diwakar
     * @date : 23-JAN-2014
     * @throws BTSLBaseException
     */
    public static void validateExtChannelUserSuspendResumeRequest(RequestVO p_requestVO, String type, String reqDate, String externalNetworkCode, String categoryCode, String employeeCode, String msisdn, String operatorLoginId, String operatorPassword, String extRefNum, String msisdn2, String userLoginId, String externalCode, String pin, String userMsisdn) throws BTSLBaseException {
    	final String METHOD_NAME = "validateExtChannelUserSuspendResumeRequest";
        sb.setLength(0);
    	LogFactory.printLog(METHOD_NAME, sb.append("Entered p_requestVO ID=").append(p_requestVO.getRequestIDStr()), _log);
        final String[] errorMsg = new String[2];
        final HashMap requestHashMap = p_requestVO.getRequestMap();
        try {
            // Creation Date Validation : For user Creation/Registration Date
            // must be specified.
            if (BTSLUtil.isNullString(reqDate)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_DATE_BLANK);
            } else {
                try {
                    BTSLUtil.getDateFromDateString(reqDate, externalDateFormat);
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT);
                }
            }

            // Mandatory: Sender External Network Code validation
            if (BTSLUtil.isNullString(externalNetworkCode)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_BLANK);
            } else if (externalNetworkCode.length() > 2) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID);
            } else {
                alphanumericWithSpecialChar(externalNetworkCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EXTNWCODE, XMLStringValidation
                    .isTagManadatory());
            }

            // Optional : EMPCODE length Check
            if (!BTSLUtil.isNullString(employeeCode) && employeeCode.length() > 12) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EMPCODE_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(employeeCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EMPCODE, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional: Sender LoginID validation
            if (!BTSLUtil.isNullString(operatorLoginId) && operatorLoginId.length() > 20) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(operatorLoginId, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_LOGIN_ID, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional: Sender Password validation
            if (!BTSLUtil.isNullString(operatorPassword) && operatorPassword.length() > (int)maxLoginPwdLength) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(operatorPassword, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_PASSWORD, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional: Sender MSISDN validation.
            if (!BTSLUtil.isNullString(msisdn)) {
                validateMSISDN(msisdn, externalNetworkCode, PretupsErrorCodesI.EXTSYS_REQ_MSISDN_INVALID_FORMAT, errorMsg);
                numeric(msisdn, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, XmlTagValueConstant.TAG_MSISDN, XMLStringValidation.isTagManadatory());

            }

            // Optional: PIN not null and length check
            if (!BTSLUtil.isNullString(pin) && pin.length() > (int)maxSmsPinLength) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_PIN_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(pin, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_PIN, XMLStringValidation.isTagOptinal());
            }

            // Mandatory: New EXTERNAL REFERENCE NUMBER not null and length
            // check
            if (!BTSLUtil.isNullString(extRefNum) && extRefNum.length() > 15) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTREFNUM_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(extRefNum, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EXTREFNUM, XMLStringValidation
                    .isTagOptinal());
            }

            if (BTSLUtil.isNullString(employeeCode) && BTSLUtil.isNullString(msisdn) && (BTSLUtil.isNullString(operatorPassword) || BTSLUtil.isNullString(operatorLoginId))) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_SENDER_DETAILS_INVALID);
            }

            // Optional: PRIMARY MSISDN((MSISDN1)) of new user can't be blank in
            // the user creation request.
            if (!BTSLUtil.isNullString(userMsisdn)) {
                validateMSISDN(userMsisdn, externalNetworkCode, PretupsErrorCodesI.EXTSYS_REQ_MSISDN_INVALID_FORMAT, errorMsg);
            }
            numeric(userMsisdn, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, XmlTagValueConstant.TAG_USERMSISDN, XMLStringValidation.isTagOptinal());

            // Optional: User's WEB LOGIN ID not null and length check.
            final String newUserLoginId = (String) requestHashMap.get("USERLOGINID");
            if (!BTSLUtil.isNullString(newUserLoginId) && newUserLoginId.length() > 20) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(newUserLoginId, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_USERLOGINID, XMLStringValidation
                    .isTagOptinal());
            }

            if (BTSLUtil.isNullString(userMsisdn) && BTSLUtil.isNullString(newUserLoginId)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,
                    PretupsErrorCodesI.USER_SUSPEND_RESUME_MSISDN_LOGINID_BLANK);
            }

            // Mandatory: User's EXTERNAL CODE not null and length check
            final String newUserExtCode = (String) requestHashMap.get("EXTERNALCODE");
			if(isExternalCodeMandatoryForUser){
				if (BTSLUtil.isNullString(newUserExtCode) || newUserExtCode.length()>12) 
					throw new BTSLBaseException("XMLStringValidation",METHOD_NAME,PretupsErrorCodesI.EXTSYS_REQ_EXTERNALCODE_BLANK_OR_LENGTH_EXCEEDS);
				else
					alphanumericWithSpecialChar(newUserExtCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL,XmlTagValueConstant.TAG_EXTERNALCODE,XMLStringValidation.isTagOptinal());
			}

            // Check Action Id is not null and can be only S/R
            final String actionId = (String) requestHashMap.get("ACTION");
            if (BTSLUtil.isNullString(actionId)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_ACTION_BLANK);
            } else if (actionId.length() > 1) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_ACTION_LENGTH_EXCEEDS);
            } else if (!(PretupsI.USER_STATUS_SUSPEND.equals(actionId) || PretupsI.USER_STATUS_ACTIVE_R.equals(actionId))) {
                throw new BTSLBaseException("XMLStringValidation", "parseChannelUserDeleteSuspendResumeRequestAPI", PretupsErrorCodesI.EXTSYS_REQ_ACTION_INVALID_VALUE);
            }

        } catch (BTSLBaseException be) {
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());
            throw be;
        } finally {
        	sb.setLength(0);
            LogFactory.printLog(METHOD_NAME, sb.append("Exiting p_requestVO ID=").append(p_requestVO.getRequestIDStr()), _log);
        }
    }

    /**
     * @description : This method will validate the user role for add /delete
     *              request coming through external system.
     * @author :diwakar
     * @date : 23-JAN-2014
     * @throws BTSLBaseException
     */
    public static void validateExtChannelUserRoleAddOrDeleteRequest(RequestVO p_requestVO, String type, String reqDate, String externalNetworkCode, String categoryCode, String employeeCode, String msisdn, String operatorLoginId, String operatorPassword, String extRefNum, String msisdn2, String userLoginId, String externalCode, String action, String roleCode, String userMsisdn) throws BTSLBaseException {
    	final String METHOD_NAME = "validateExtChannelUserRoleAddOrDeleteRequest";
		sb.setLength(0);
		LogFactory.printLog(METHOD_NAME, sb.append("Entered p_requestVO ID=").append(p_requestVO.getRequestIDStr()), _log);
        final String[] errorMsg = new String[2];
        final HashMap requestHashMap = p_requestVO.getRequestMap();
        try {
            // Creation Date Validation : For user Creation/Registration Date
            // must be specified.
            if (BTSLUtil.isNullString(reqDate)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_DATE_BLANK);
            } else {
                try {
                    BTSLUtil.getDateFromDateString(reqDate, externalDateFormat);
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT);
                }
            }

            // Mandatory: Sender External Network Code validation
            if (BTSLUtil.isNullString(externalNetworkCode)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_BLANK);
            } else if (externalNetworkCode.length() > 2) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID);
            } else {
                alphanumericWithSpecialChar(externalNetworkCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EXTNWCODE, XMLStringValidation
                    .isTagManadatory());
            }

            // Mandatory: CATCODE not null and length check
            if (!BTSLUtil.isNullString(categoryCode) && categoryCode.length() > 10) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_USERCATCODE_BLANK_OR_LENGTH_EXCEEDS);
            } else {
                alphanumeric(categoryCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC, XmlTagValueConstant.TAG_CATCODE, XMLStringValidation.isTagOptinal());
            }

            // Optional: EmployeeCode validation
            if (!BTSLUtil.isNullString(employeeCode) && employeeCode.length() > 12) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EMPCODE_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(employeeCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EMPCODE, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional: Sender LoginID validation
            if (!BTSLUtil.isNullString(operatorLoginId) && operatorLoginId.length() > 20) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(operatorLoginId, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_LOGIN_ID, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional: Sender Password validation
            if (!BTSLUtil.isNullString(operatorPassword) && operatorPassword.length() > (int)maxLoginPwdLength) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(operatorPassword, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_PASSWORD, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional: New EXTERNAL REFERENCE NUMBER not null and length check
            if (!BTSLUtil.isNullString(extRefNum) && extRefNum.length() > 15) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTREFNUM_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(extRefNum, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EXTREFNUM, XMLStringValidation
                    .isTagOptinal());
            }

            if (BTSLUtil.isNullString(employeeCode) && (BTSLUtil.isNullString(operatorPassword) || BTSLUtil.isNullString(operatorLoginId))) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_SENDER_DETAILS_INVALID);
            }

            // Optional: User's WEB LOGIN ID not null and length check.
            if (!BTSLUtil.isNullString(userLoginId) && userLoginId.length() > 20) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(userLoginId, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_LOGIN_ID, XMLStringValidation
                    .isTagOptinal());
            }

            // Mandatory: User's EXTERNAL CODE not null and length check
            if (BTSLUtil.isNullString(roleCode) || roleCode.length() > 12) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,
                    PretupsErrorCodesI.EXTSYS_REQ_ROLECODE_BLANK_OR_LENGTH_EXCEEDS);
            } else {
                alphabetic(roleCode, PretupsErrorCodesI.EXTSYS_NOT_ALFABETIC, XmlTagValueConstant.TAG_ROLECODE, XMLStringValidation.isTagManadatory());
            }

            // Mandatory: USERMSISDN can't be blank in the user creation
            // request.
            if (BTSLUtil.isNullString(userMsisdn)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_NEW_PRIMARY_MSISDN_BLANK);
            }

            // Mandatory: New MSISDN and EXTERNAL NETWORK CODE not null and
            // length check
            validateMSISDN(userMsisdn, externalNetworkCode, PretupsErrorCodesI.EXTSYS_REQ_MSISDN_INVALID_FORMAT, errorMsg);
            numeric(userMsisdn, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, XmlTagValueConstant.TAG_USERMSISDN, XMLStringValidation.isTagManadatory());

            // Mandatory: Action is not null and can be only A/D .
            if (BTSLUtil.isNullString(action)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_ACTION_BLANK);
            } else if (action.length() > 1) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_ACTION_LENGTH_EXCEEDS);
            } else if (!(PretupsI.USER_ROLE_ADD.equals(action) || PretupsI.USER_ROLE_DELETE.equals(action))) {
                throw new BTSLBaseException("XMLStringValidation", "parseChannelUserDeleteSuspendResumeRequestAPI", PretupsErrorCodesI.EXTSYS_REQ_ROLE_ACTION_INVALID_VALUE); // 11-MAR-2013
                // as
                // error
                // code
                // separated
                // for
                // role
                // add
            }

        } catch (BTSLBaseException be) {
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());
            throw be;
        } finally {
        	sb.setLength(0);
            LogFactory.printLog(METHOD_NAME, sb.append("Exiting p_requestVO ID=").append(p_requestVO.getRequestIDStr()), _log);
        }
    }

    /**
     * @description : This method will validate the user MSISDN & ICCID mapping
     *              for request coming through external system.
     * @author : diwakar
     * @date : 23-JAN-2014
     * @throws BTSLBaseException
     */
    public static void validateExtChannelUserMSISDNAssociationWithICCIDRequest(RequestVO p_requestVO, String type, String reqDate, String externalNetworkCode, String categoryCode, String employeeCode, String msisdn, String operatorLoginId, String operatorPassword, String extRefNum, String iccid, String externalCode, String iccidConfirm, String userMsisdn) throws BTSLBaseException {
    	final String METHOD_NAME = "validateExtChannelUserMSISDNAssociationWithICCIDRequest";
        sb.setLength(0);
    	LogFactory.printLog(METHOD_NAME, sb.append("Entered p_requestVO ID=").append(p_requestVO.getRequestIDStr()), _log);
        final String[] errorMsg = new String[2];
        final HashMap requestHashMap = p_requestVO.getRequestMap();
        try {
            // Creation Date Validation : For user Creation/Registration Date
            // must be specified.
            if (BTSLUtil.isNullString(reqDate)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_DATE_BLANK);
            } else {
                try {
                    BTSLUtil.getDateFromDateString(reqDate, externalDateFormat);
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,
                        PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT);
                }
            }

            // Mandatory: Sender External Network Code validation
            if (BTSLUtil.isNullString(externalNetworkCode)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_BLANK);
            } else if (externalNetworkCode.length() > 2) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID);
            } else {
                alphanumericWithSpecialChar(externalNetworkCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EXTNWCODE, XMLStringValidation
                    .isTagManadatory());
            }

            // Optional: Old EXTERNALCODE CODE not null and length check
            if (!BTSLUtil.isNullString(categoryCode) && categoryCode.length() > 10) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_USERCATCODE_BLANK_OR_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(categoryCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_CATCODE, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional: EmployeeCode validation
            if (!BTSLUtil.isNullString(employeeCode) && employeeCode.length() > 12) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,
                    PretupsErrorCodesI.EXTSYS_REQ_EMPCODE_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(employeeCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EMPCODE, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional: Sender LoginID validation
            if (!BTSLUtil.isNullString(operatorLoginId) && operatorLoginId.length() > 20) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,
                    PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(operatorLoginId, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_LOGIN_ID, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional: Sender Password validation
            if (!BTSLUtil.isNullString(operatorPassword) && operatorPassword.length() > (int)maxLoginPwdLength) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,
                    PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(operatorPassword, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_PASSWORD, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional: New EXTERNAL REFERENCE NUMBER not null and length check
            if (!BTSLUtil.isNullString(extRefNum) && extRefNum.length() > 15) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,
                    PretupsErrorCodesI.EXTSYS_REQ_EXTREFNUM_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(extRefNum, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EXTREFNUM, XMLStringValidation
                    .isTagOptinal());
            }

            if (BTSLUtil.isNullString(employeeCode) && (BTSLUtil.isNullString(operatorPassword) || BTSLUtil.isNullString(operatorLoginId))) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,
                    PretupsErrorCodesI.EXTSYS_REQ_SENDER_DETAILS_INVALID);
            }

            // Mandatory: User's WEB LOGIN ID not null and length check.
            if (BTSLUtil.isNullString(iccid)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.XML_ERROR_ICCID_IS_NULL);
            }

            // Mandatory: User's WEB LOGIN ID not null and length check.
            if (BTSLUtil.isNullString(iccidConfirm)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,
                    PretupsErrorCodesI.XML_ERROR_CONFIRM_ICCID_IS_NULL);
            }

            // Mandatory: User's WEB LOGIN ID not null and length check.
            if (BTSLUtil.isNullString(userMsisdn)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.XML_ERROR_MSISDN_IS_NULL);
            }

            // Mandatory: User's WEB LOGIN ID not null and length check.
            if (!BTSLUtil.isNullString(iccid) && iccid.length() > 20) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,
                    PretupsErrorCodesI.EXTSYS_ICCID_MAP_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(iccid, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_ICCID, XMLStringValidation.isTagManadatory());
            }

            // Mandatory: User's EXTERNAL CODE not null and length check
            if (!BTSLUtil.isNullString(iccidConfirm) && iccidConfirm.length() > 20) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,
                    PretupsErrorCodesI.EXTSYS_CONFIRM_ICCID_MAP_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(iccidConfirm, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_ICCIDCONFIRM, XMLStringValidation
                    .isTagManadatory());
            }

            if (!BTSLUtil.isNullString(iccid) && !BTSLUtil.isNullString(iccidConfirm) && !iccid.equalsIgnoreCase(iccidConfirm)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,
                    PretupsErrorCodesI.EXTSYS_ICCID_CONFIRM_ICCID_MAP_NOT_SAME);
            }

            // Mandatory: USERMSISDN can't be blank in the user creation
            // request.
            if (!BTSLUtil.isNullString(userMsisdn)) {
                validateMSISDN(userMsisdn, externalNetworkCode, PretupsErrorCodesI.EXTSYS_REQ_MSISDN_INVALID_FORMAT, errorMsg);
            }
            numeric(userMsisdn, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC, XmlTagValueConstant.TAG_MSISDN, XMLStringValidation.isTagManadatory());

            if (BTSLUtil.isNullString(iccid) && BTSLUtil.isNullString(userMsisdn)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,
                    PretupsErrorCodesI.XML_ERROR_ICCID_MSISDN_REQUIRED);
            }

        } catch (BTSLBaseException be) {
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());
            throw be;
        } finally {
			sb.setLength(0);
			LogFactory.printLog(METHOD_NAME, sb.append("Exiting p_requestVO ID=").append(p_requestVO.getRequestIDStr()), _log);
        }
    }

    /**
     * @description : This method will be used to validate the MSISDN
     * @author : diwakar
     * @date : 23-JAN-2014
     * @throws BTSLBaseException
     */
    private static void validateMSISDN(String msisdn, String externalNetworkCode, String errorCode, String[] errorMsg) throws BTSLBaseException {
        if (!BTSLUtil.isNullString(msisdn)) {
            if (!BTSLUtil.isNullString(msisdn)) {
                // Throw exception if MSISDN is not in valid format.
                if (!BTSLUtil.isValidMSISDN(msisdn)) {
                    errorMsg[0] = msisdn;
                    throw new BTSLBaseException("XMLStringValidation", "validateMSISDN", errorCode, errorMsg);
                }

                // Get the filtered MSISDN
                msisdn = PretupsBL.getFilteredMSISDN(msisdn);
                final String msisdnPrefix = PretupsBL.getMSISDNPrefix(msisdn);
                final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                if (networkPrefixVO == null || !networkPrefixVO.getNetworkCode().equals(externalNetworkCode)) {
                    errorMsg[0] = msisdn;
                    errorMsg[1] = externalNetworkCode;
                    throw new BTSLBaseException("XMLStringValidation", "validateMSISDN", PretupsErrorCodesI.INVALID_USERMSISDN_AS_PER_NETWORK_CODE, errorMsg); // 21-02-2014
                }

            }

        } else {
            throw new BTSLBaseException("XMLStringValidation", "validateMSISDN", PretupsErrorCodesI.EXTSYS_REQ_USR_MSISDNS_LIST_INVALID);
        }

    }

    /**
     * @description : This method will be used to validate the tags that are
     *              coming into the request for various XML buffer.
     * @author : diwakar
     * @date : 23-JAN-2014
     * @throws BTSLBaseException
     */
    public static void validateTags(String requestStr, String[] validateTag) throws BTSLBaseException {
    	final String METHOD_NAME = "validateTags";
    	sb.setLength(0);
        for (int indexTag = 0; indexTag < validateTag.length; indexTag++) {
            if ((requestStr.indexOf(validateTag[indexTag]) == -1) || (requestStr.indexOf(validateTag[indexTag + 1]) == -1)) {
                LogFactory.printLog(METHOD_NAME, sb.append("validateTags : missing indexTag = ").append(indexTag), _log);
                final String[] missingTag = { validateTag[indexTag], validateTag[indexTag + 1] };
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_INVALID_FORMAT, missingTag);
            }
            indexTag++;
        }

    }

    /***
     * @author diwakar
     * @date : 01-MAR-2014
     */
    public static void validateChannelExtCreditTransferRequest(RequestVO p_requestVO, String type, String date, String extNwCode, String msisdn, String pin, String loginId, String password, String extCode, String extRefNumber, String msisdn2, String amount, String language1, String language2, String selector) throws BTSLBaseException {
		final String METHOD_NAME = "validateChannelExtCreditTransferRequest";
		sb.setLength(0);
		LogFactory.printLog(METHOD_NAME, sb.append("validateChannelExtCreditTransferRequest : Enter"), _log);
        try {
            XMLTagValueValidation.validateType(type, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateDate(date, XMLTagValueValidation.isTagOptinal());
            XMLTagValueValidation.validateExtNetworkCode(extNwCode, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateSenderDetails(msisdn, pin, loginId, password, extCode, extNwCode, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateExtRefNum(extRefNumber, XMLTagValueValidation.isTagOptinal());
            //XMLTagValueValidation.validateMsisdn2(msisdn2, extNwCode, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateAmount(amount, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateLanguage1(language1, XMLTagValueValidation.isTagOptinal());
            XMLTagValueValidation.validateLanguage2(language2, XMLTagValueValidation.isTagOptinal());
            XMLTagValueValidation.validateSelector(selector, XMLTagValueValidation.isTagManadatory());
        } catch (BTSLBaseException be) {
        	sb.setLength(0);
            LogFactory.printError(METHOD_NAME, sb.append("validateChannelExtCreditTransferRequest : getMessageKey = ").append(be.getMessageKey() ).append( " | getArgs = ").append(be.getArgs()), _log);
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());
            throw be;
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            _log.errorTrace(METHOD_NAME, e);
        } finally {
	        sb.setLength(0);
			LogFactory.printLog(METHOD_NAME, sb.append("validateChannelExtCreditTransferRequest : Exit"), _log);
        }
    }

    /***
     * @author diwakar
     * @date : 01-MAR-2014
     */
    public static void validateChannelExtTransferBillPayment(RequestVO p_requestVO, String type, String date, String extNwCode, String msisdn, String pin, String loginId, String password, String extCode, String extRefNumber, String msisdn2, String amount, String language1, String language2, String selector) throws BTSLBaseException {
    	final String METHOD_NAME = "validateChannelExtTransferBillPayment";
    	sb.setLength(0);
        LogFactory.printLog(METHOD_NAME, sb.append("validateChannelExtTransferBillPayment : Enter"), _log);
        try {
            XMLTagValueValidation.validateType(type, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateDate(date, XMLTagValueValidation.isTagOptinal());
            XMLTagValueValidation.validateExtNetworkCode(extNwCode, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateSenderDetails(msisdn, pin, loginId, password, extCode, extNwCode, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateExtRefNum(extRefNumber, XMLTagValueValidation.isTagOptinal());
            //XMLTagValueValidation.validateMsisdn2(msisdn2, extNwCode, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateAmount(amount, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateLanguage1(language1, XMLTagValueValidation.isTagOptinal());
            XMLTagValueValidation.validateLanguage2(language2, XMLTagValueValidation.isTagOptinal());
            XMLTagValueValidation.validateSelector(selector, XMLTagValueValidation.isTagManadatory());
        } catch (BTSLBaseException be) {
			sb.setLength(0);
			LogFactory.printError(METHOD_NAME, sb.append("validateChannelExtTransferBillPayment : getMessageKey = ").append(be.getMessageKey() ).append( " | getArgs = ").append(be.getArgs()), _log);
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());
            throw be;
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            _log.errorTrace(METHOD_NAME, e);
        } finally {
	        sb.setLength(0);
			LogFactory.printLog(METHOD_NAME, sb.append("validateChannelExtTransferBillPayment : Exit"), _log);
        }
    }

    /***
     * @author diwakar
     * @date : 01-MAR-2014
     */
    public static void validateExtEVDRequest(RequestVO p_requestVO, String type, String date, String extNwCode, String msisdn, String pin, String loginId, String password, String extCode, String extRefNumber, String msisdn2, String amount, String language1, String language2, String selector) throws BTSLBaseException {
    	final String METHOD_NAME = "validateExtEVDRequest";
        sb.setLength(0);
		LogFactory.printLog(METHOD_NAME, sb.append("validateExtEVDRequest : Enter"), _log);
        try {
            XMLTagValueValidation.validateType(type, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateDate(date, XMLTagValueValidation.isTagOptinal());
            XMLTagValueValidation.validateExtNetworkCode(extNwCode, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateSenderDetails(msisdn, pin, loginId, password, extCode, extNwCode, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateExtRefNum(extRefNumber, XMLTagValueValidation.isTagOptinal());
            //XMLTagValueValidation.validateMsisdn2(msisdn2, extNwCode, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateAmount(amount, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateLanguage1(language1, XMLTagValueValidation.isTagOptinal());
            XMLTagValueValidation.validateLanguage2(language2, XMLTagValueValidation.isTagOptinal());
            XMLTagValueValidation.validateSelector(selector, XMLTagValueValidation.isTagManadatory());
        } catch (BTSLBaseException be) {
        	sb.setLength(0);
            LogFactory.printError(METHOD_NAME, sb.append("validateExtEVDRequest : getMessageKey = ").append(be.getMessageKey() ).append( " | getArgs = ").append(be.getArgs()), _log);
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());
            throw be;
        } catch (RuntimeException e) {
            _log.errorTrace(METHOD_NAME, e);
        } finally {
	        sb.setLength(0);
			LogFactory.printLog(METHOD_NAME, sb.append("validateExtEVDRequest : Exit"), _log);
        }
    }

    /***
     * @author diwakar
     * @date : 01-MAR-2014
     */
    public static void validateChannelExtRechargeStatusRequest(RequestVO p_requestVO, String type, String date, String extNwCode, String msisdn, String pin, String loginId, String password, String extCode, String extRefNumber, String txnID, String language1) throws BTSLBaseException {
    	final String METHOD_NAME = "validateChannelExtRechargeStatusRequest";
        sb.setLength(0);
		LogFactory.printLog(METHOD_NAME, sb.append("validateChannelExtRechargeStatusRequest : Enter"), _log);
        try {
            XMLTagValueValidation.validateType(type, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateDate(date, XMLTagValueValidation.isTagOptinal());
            XMLTagValueValidation.validateExtNetworkCode(extNwCode, XMLTagValueValidation.isTagManadatory());
			if(BTSLUtil.isStringIn(p_requestVO.getType(),Constants.getProperty("EXTGW_SENDER_OPTIONAL_PIN_ACTIONS")))
				XMLTagValueValidation.validateSenderDetails(msisdn,pin,loginId,password,extCode,extNwCode,XMLTagValueValidation.isTagOptinal());
			else
            XMLTagValueValidation.validateSenderDetails(msisdn, pin, loginId, password, extCode, extNwCode, XMLTagValueValidation.isTagManadatory());
			if(BTSLUtil.isNullString(extRefNumber) && BTSLUtil.isNullString(txnID))
                throw new BTSLBaseException("XMLTagValueValidation","blank",PretupsErrorCodesI.EXTSYS_BLANK,"External Reference number & TxnId both can not be blank");
			XMLTagValueValidation.validateExtRefNum(extRefNumber,XMLTagValueValidation.isTagOptinal());
			XMLTagValueValidation.validateTxnId(txnID,XMLTagValueValidation.isTagOptinal());
            XMLTagValueValidation.validateLanguage1(language1, XMLTagValueValidation.isTagOptinal());
        } catch (BTSLBaseException be) {
        	sb.setLength(0);
            LogFactory.printError(METHOD_NAME, sb.append("validateChannelExtRechargeStatusRequest : getMessageKey = ").append(be.getMessageKey() ).append( " | getArgs = ").append(be.getArgs()), _log);
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());
            throw be;
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            _log.errorTrace(METHOD_NAME, e);
        } finally {
	        sb.setLength(0);
			LogFactory.printLog(METHOD_NAME, sb.append("validateChannelExtRechargeStatusRequest : Exit"), _log);
        }
    }

    /***
     * @author diwakar
     * @date : 01-MAR-2014
     */
    public static void validateExtChannelUserBalanceRequest(RequestVO p_requestVO, String type, String date, String extNwCode, String msisdn, String pin, String loginId, String password, String extCode, String extRefNumber,String userType) throws BTSLBaseException {
    	final String METHOD_NAME = "validateExtChannelUserBalanceRequest";
        sb.setLength(0);
		LogFactory.printLog(METHOD_NAME, sb.append("validateExtChannelUserBalanceRequest : Enter"), _log);
        try {
            XMLTagValueValidation.validateType(type, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateDate(date, XMLTagValueValidation.isTagOptinal());
            XMLTagValueValidation.validateExtNetworkCode(extNwCode, XMLTagValueValidation.isTagManadatory());
			if(BTSLUtil.isStringIn(p_requestVO.getType(),Constants.getProperty("EXTGW_SENDER_OPTIONAL_PIN_ACTIONS")))
				XMLTagValueValidation.validateSenderDetails(msisdn,pin,loginId,password,extCode,extNwCode,XMLTagValueValidation.isTagOptinal(),p_requestVO);
			
			else if(BTSLUtil.isStringIn(p_requestVO.getServiceKeyword(),Constants.getProperty("REST_SENDER_OPTIONAL_PIN_ACTIONS"))){
				XMLTagValueValidation.validateSenderDetails(msisdn,pin,loginId,password,extCode,extNwCode,XMLTagValueValidation.isTagOptinal(),p_requestVO);
			} 

			else{
                if (!BTSLUtil.isNullString(userType)) {
                    if (userType.equals(PretupsI.OPERATOR_TYPE_OPT))
                        XMLTagValueValidation.validateSenderDetails(msisdn, pin, loginId, password, extCode, extNwCode, XMLTagValueValidation.isTagOptinal(), p_requestVO);
                    else
                        XMLTagValueValidation.validateSenderDetails(msisdn, pin, loginId, password, extCode, extNwCode, XMLTagValueValidation.isTagManadatory(), p_requestVO);
                } else {
                    XMLTagValueValidation.validateSenderDetails(msisdn, pin, loginId, password, extCode, extNwCode, XMLTagValueValidation.isTagManadatory(), p_requestVO);
                }

            }
            XMLTagValueValidation.validateExtRefNum(extRefNumber, XMLTagValueValidation.isTagOptinal());
        } catch (BTSLBaseException be) {
        	sb.setLength(0);
            LogFactory.printError(METHOD_NAME, sb.append("validateExtChannelUserBalanceRequest : getMessageKey = ").append(be.getMessageKey() ).append( " | getArgs = ").append(be.getArgs()), _log);
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());
            throw be;
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            _log.errorTrace(METHOD_NAME, e);
        } finally {
	        sb.setLength(0);
			LogFactory.printLog(METHOD_NAME, sb.append("validateExtChannelUserBalanceRequest : Exit"), _log);
        }
    }

    /***
     * @author diwakar
     * @date : 01-MAR-2014
     */
    public static void validateCommonO2CTransferAPIParsing(RequestVO p_requestVO, String extNwCode, String msisdn, String pin, String extCode, String extNumber, String extDate, String productCode, String productQty, String trfCategory, String refNumber, String paymentType, String paymentNumber, String paymentDate, String remarks) throws BTSLBaseException {
    	final String METHOD_NAME = "validateCommonO2CTransferAPIParsing";
        sb.setLength(0);
		LogFactory.printLog(METHOD_NAME, sb.append("validateCommonO2CTransferAPIParsing : Enter"), _log);
        try {
            XMLTagValueValidation.validateExtNetworkCode(extNwCode, XMLTagValueValidation.isTagManadatory());
            // XMLTagValueValidation.validateSenderDetails(msisdn,pin,"","",extCode,extNwCode,XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateByPassPinSenderDetails(msisdn, pin, "", "", extCode, extNwCode, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateExtRefNum(extNumber, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateTxnDate(extDate, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateProductCode(productCode, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateQuantity(productQty,XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateTrfCategoryCode(trfCategory, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateRefNum(refNumber, XMLTagValueValidation.isTagOptinal());
            XMLTagValueValidation.validatePaymentType(paymentType, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validatePaymentNumber(paymentNumber, XMLTagValueValidation.isTagOptinal());
            XMLTagValueValidation.validatePaymentDate(paymentDate, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateRemark(remarks, XMLTagValueValidation.isTagOptinal());
            XMLTagValueValidation.validatePin(pin, XMLTagValueValidation.isTagOptinal());

        } catch (BTSLBaseException be) {
			sb.setLength(0);
			LogFactory.printError(METHOD_NAME, sb.append("validateCommonO2CTransferAPIParsing : getMessageKey = ").append(be.getMessageKey() ).append( " | getArgs = ").append(be.getArgs()), _log);
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());

            throw be;
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            _log.errorTrace(METHOD_NAME, e);
        } finally {
        	sb.setLength(0);
        	LogFactory.printLog(METHOD_NAME, sb.append("validateCommonO2CTransferAPIParsing : Exit"), _log);
        }
    }

    /***
     * @author diwakar
     * @date : 01-MAR-2014
     */
    public static void validateCommonO2CReturnAPIParsing(RequestVO p_requestVO, String extNwCode, String msisdn, String pin, String extCode, String extNumber, String extDate, String productCode, String productQty, String remarks) throws BTSLBaseException {
    	final String METHOD_NAME = "validateCommonO2CReturnAPIParsing";
        sb.setLength(0);
    	LogFactory.printLog(METHOD_NAME, sb.append("validateCommonO2CReturnAPIParsing : Enter"), _log);
        try {
            XMLTagValueValidation.validateExtNetworkCode(extNwCode, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateSenderDetails(msisdn, pin, "", "", extCode, extNwCode, XMLTagValueValidation.isTagOptinal());
            XMLTagValueValidation.validateExtRefNum(extNumber, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateTxnDate(extDate, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateProductCode(productCode, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateQuantity(productQty, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateRemark(remarks, XMLTagValueValidation.isTagOptinal());
            XMLTagValueValidation.validatePin(pin, XMLTagValueValidation.isTagOptinal());
        } catch (BTSLBaseException be) {
			sb.setLength(0);
			LogFactory.printError(METHOD_NAME, sb.append("validateCommonO2CReturnAPIParsing : getMessageKey = ").append(be.getMessageKey() ).append( " | getArgs = ").append(be.getArgs()), _log);
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());
            throw be;
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            _log.errorTrace(METHOD_NAME, e);
        } finally {
        	sb.setLength(0);
        	LogFactory.printLog(METHOD_NAME, sb.append("validateCommonO2CReturnAPIParsing : Exit"), _log);
        }
    }

    /***
     * @author diwakar
     * @date : 01-MAR-2014
     */
    public static void validateExtC2CTransferRequest(RequestVO p_requestVO, String extNwCode, String msisdn1, String pin, String loginId, String password, String extCode, String extRefNumber, String msisdn2, String extCode2, String loginId2, String productCode, String productQty, String language1) throws BTSLBaseException {
    	final String METHOD_NAME = "validateExtC2CTransferRequest";
    	sb.setLength(0);
    	LogFactory.printLog(METHOD_NAME, sb.append("validateExtC2CTransferRequest : Enter msisdn2=").append(msisdn2).append(",loginId2=").append(loginId2).append(",extCode2=").append(extCode2), _log);	
        try {
            XMLTagValueValidation.validateExtNetworkCode(extNwCode, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateSenderDetails(msisdn1, pin, loginId, password, extCode, extNwCode, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateExtRefNum(extRefNumber, XMLTagValueValidation.isTagOptinal());
			if(BTSLUtil.isNullString(msisdn2) && BTSLUtil.isNullString(loginId2) && BTSLUtil.isNullString(extCode2)){ 
				throw new BTSLBaseException("XMLStringValidation",METHOD_NAME,PretupsErrorCodesI.ERROR_INVALID_REC_USERCODE);
			}
            XMLTagValueValidation.validateMsisdn2(msisdn2, extNwCode, XMLTagValueValidation.isTagOptinal());
            XMLTagValueValidation.validateLoginId2(loginId2, XMLTagValueValidation.isTagOptinal());
            XMLTagValueValidation.validateExtCode2(extCode2, XMLTagValueValidation.isTagOptinal());
            XMLTagValueValidation.validateProductCode(productCode, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateQuantity(productQty, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateLanguage1(language1, XMLTagValueValidation.isTagOptinal());

        } catch (BTSLBaseException be) {
			sb.setLength(0);
			LogFactory.printError(METHOD_NAME, sb.append("validateExtC2CTransferRequest : getMessageKey = ").append(be.getMessageKey() ).append( " | getArgs = ").append(be.getArgs()), _log);
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());
            throw be;
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            _log.errorTrace(METHOD_NAME, e);
        } finally {
			sb.setLength(0);
			LogFactory.printLog(METHOD_NAME, sb.append("validateExtC2CTransferRequest : Exit"), _log);
        }
    }

    /***
     * @author diwakar
     * @throws BTSLBaseException
     * @date : 03-MAR-2014
     */
    public static void validateMNPUploadRequest(RequestVO p_requestVO, String type, String date, String extNwCode, String categoryCode, String loginId, String password, String extRefNumber, String msisdn, String subsType, String portType, String empCode) throws BTSLBaseException {
    	final String METHOD_NAME = "validateMNPUploadRequest";
    	sb.setLength(0);
        LogFactory.printLog(METHOD_NAME, sb.append("validateMNPUploadRequest : Enter"), _log);
        try {
            XMLTagValueValidation.validateExtNetworkCode(extNwCode, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateSenderDetails("", "", loginId, password, "", empCode, extNwCode, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateExtRefNum(extRefNumber, XMLTagValueValidation.isTagOptinal());
            XMLTagValueValidation.validateMsisdn(msisdn, extNwCode, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateSubType(subsType, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validatePortype(portType, XMLTagValueValidation.isTagManadatory());

        } catch (BTSLBaseException be) {
			sb.setLength(0);
			LogFactory.printError(METHOD_NAME, sb.append("validateMNPUploadRequest : getMessageKey = ").append(be.getMessageKey() ).append( " | getArgs = ").append(be.getArgs()), _log);
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());
            throw be;
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            _log.errorTrace(METHOD_NAME, e);
        } finally {
        	sb.setLength(0);
        	LogFactory.printLog(METHOD_NAME, sb.append("validateMNPUploadRequest : Exit"), _log);
        }

    }

    /***
     * @author diwakar
     * @date : 03-MAR-2014
     */
    static void alphanumeric(String p_str, String p_errorKey, String tagName, boolean p_isManOrOpt) throws BTSLBaseException {
        if (p_isManOrOpt || !BTSLUtil.isNullString(p_str)) {
            if (!p_str.matches(RegularExpression.ALPHA_NUMERIC)) {
                _errorMsg[0] = tagName;
                throw new BTSLBaseException("XMLTagValueValidation", "alphanumeric", p_errorKey, _errorMsg);
            }
        }
    }

    /***
     * @author diwakar
     * @date : 03-MAR-2014
     */
    static void alphabetic(String p_str, String p_errorKey, String tagName, boolean p_isManOrOpt) throws BTSLBaseException {
        if (p_isManOrOpt || !BTSLUtil.isNullString(p_str)) {
            if (!p_str.matches(RegularExpression.ALPHABET)) {
                _errorMsg[0] = tagName;
                throw new BTSLBaseException("XMLTagValueValidation", "alphabetic", p_errorKey, _errorMsg);
            }
        }
    }

    /***
     * @author diwakar
     * @date : 03-MAR-2014
     */
    static void numeric(String p_str, String p_errorKey, String tagName, boolean p_isManOrOpt) throws BTSLBaseException {
        if (p_isManOrOpt || !BTSLUtil.isNullString(p_str)) {
            if (!p_str.matches(RegularExpression.NUMERIC)) {
                _errorMsg[0] = tagName;
                throw new BTSLBaseException("XMLTagValueValidation", "numeric", p_errorKey, _errorMsg);
            }
        }
    }

    /***
     * @author diwakar
     * @date : 11-MAR-2014
     */
    static void alphanumericWithSpecialChar(String p_str, String p_errorKey, String tagName, boolean p_isManOrOpt) throws BTSLBaseException {
        if (p_isManOrOpt || !BTSLUtil.isNullString(p_str)) {
            if (!p_str.matches(RegularExpression.ALPHA_NUMERIC_SPECIAL_CHAR)) {
                _errorMsg[0] = tagName;
                throw new BTSLBaseException("XMLTagValueValidation", "alphanumericWithSpecialChar", p_errorKey, _errorMsg);
            }
        }
    }

    /***
     * @author diwakar
     * @date : 11-MAR-2014
     */
    static void alphanumericSpecialCharAtleast(String p_str, String p_errorKey, String tagName, boolean p_isManOrOpt) throws BTSLBaseException {

        final String METHOD_NAME = "alphanumericSpecialCharAtleast";
        if (p_isManOrOpt || !BTSLUtil.isNullString(p_str)) {
            if (!p_str.matches(RegularExpression.ALPHA_NUMERIC_SPECIAL_CHAR_ATLEAST)) {

                // 2n'd level check on 31-MAR-2014
                OperatorUtilI operatorUtili = null;
                try {
                    final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
                    operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
                if (operatorUtili != null) {
                    final HashMap errorMessageMap = operatorUtili.validatePassword("", p_str);
                    if (null != errorMessageMap && errorMessageMap.size() > 0) {
                        _errorMsg[0] = tagName;
                        throw new BTSLBaseException("XMLTagValueValidation", "alphanumericSpecialCharAtleast", p_errorKey, _errorMsg);
                    }
                } else {
                    _errorMsg[0] = tagName;
                    throw new BTSLBaseException("XMLTagValueValidation", "alphanumericSpecialCharAtleast", p_errorKey, _errorMsg);
                }
            }
        }
    }

    public static void validateOperatorUserAddRequest(RequestVO p_requestVO, String type, String reqDate, String externalNetworkCode, String requestLoginId, String password, String extRefNum, String userCategory, String userName, String userNamePrefix, String externalCode, String mobileNumber, String division, String department, String shortName, String userNamePrefix2, String subscriberCode, String contactPerson, String contactNumber, String ssn, String address1, String address2, String city, String state, String country, String emailId, String webloginId, String webPassword) throws BTSLBaseException {
        final String METHOD_NAME = "validateOperatorUserAddRequest";
    	sb.setLength(0);
        LogFactory.printLog(METHOD_NAME, sb.append("Entered p_requestVO ID=").append(p_requestVO.getRequestIDStr()), _log);
		sb.setLength(0);
		LogFactory.printLog(METHOD_NAME, sb.append("type =  ").append(type ).append( "  | reqDate = ").append(reqDate ).append( "  |  externalNetworkCode =").append(externalNetworkCode ).append( "  | requestLoginId = ").append(requestLoginId ).append( "  | extRefNum = ").append(extRefNum ).append( "  |  userCategory =").append(userCategory ).append( "  |  Division =").append(division ).append( "  |  Department =").append(department ).append( "  | userName =  ").append(userName ).append( "  | userNamePrefix = ").append(userNamePrefix ).append( "  | externalCode=   ").append(externalCode ).append( "  | mobileNumber ").append(mobileNumber), _log);
        final String[] errorMsg = new String[2];
        try {
            // Creation Date Validation : For user Creation/Registration Date
            // must be specified.
            if (BTSLUtil.isNullString(reqDate)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_DATE_BLANK);
            } else {
                try {
                    BTSLUtil.getDateFromDateString(reqDate, externalDateFormat);
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT);
                }
            }

            // Mandatory: Sender External Network Code validation
            if (BTSLUtil.isNullString(externalNetworkCode)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_BLANK);
            } else if (externalNetworkCode.length() > 2) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID);
            } else {
                alphanumericWithSpecialChar(externalNetworkCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EXTNWCODE, XMLStringValidation
                    .isTagManadatory());
            }

            // Optional: Sender LoginID validation
            if (!BTSLUtil.isNullString(requestLoginId) && requestLoginId.length() > 20) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_LENGTH_EXCEEDS);
            }

            // Optional: Sender Password validation
            if (!BTSLUtil.isNullString(password) && password.length() > (int)maxLoginPwdLength) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(password, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_PASSWORD, XMLStringValidation.isTagOptinal());
            }

            // Optional: Sender external reference number validation
            if (!BTSLUtil.isNullString(extRefNum) && extRefNum.length() > 15) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTREFNUM_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(extRefNum, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EXTREFNUM, XMLStringValidation
                    .isTagOptinal());
            }

            if ((BTSLUtil.isNullString(password) || BTSLUtil.isNullString(requestLoginId))) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_SENDER_DETAILS_INVALID);
            }

            // Mandatory: Old EXTERNALCODE CODE not null and length check
            if (BTSLUtil.isNullString(userCategory) || userCategory.length() > 10) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_USERCATCODE_BLANK_OR_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(userCategory, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_USERCATCODE, XMLStringValidation
                    .isTagManadatory());
            }

            // Mandatory: New NEWEXTERNALCODE not null and length check
            if (BTSLUtil.isNullString(userName) || userName.length() > 80) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_USERNAME_BLANK_OR_LENGTH_INVALID);
            } else {
                alphanumericWithSpecialChar(userName, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_USERNAME, XMLStringValidation
                    .isTagManadatory());
            }

            // Optional: New User SHORT NAME length check.
            if (!BTSLUtil.isNullString(shortName) && shortName.length() > 15) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_SHORTNAME_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(shortName, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_SHORTNAME, XMLStringValidation
                    .isTagOptinal());
            }

            // Mandatory: New USER NAME PREFIX not null and length check
            if (BTSLUtil.isNullString(userNamePrefix) || userNamePrefix.length() > 10) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_USERNAMEPREFIX_BLANK_OR_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(userNamePrefix, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_USERNAMEPREFIX, XMLStringValidation
                    .isTagManadatory());
            }

            // Optional:New user's SUBSCRIBER CODE field's length check.
            if (!BTSLUtil.isNullString(subscriberCode) && subscriberCode.length() > 12) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_SUBSCRIBERCODE_BLANK_OR_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(subscriberCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_USERNAME, XMLStringValidation
                    .isTagOptinal());
            }

            // Mandatory: New user EXTERNAL CODE not null and length check
            if (BTSLUtil.isNullString(externalCode) || externalCode.length() > 20) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTERNALCODE_BLANK_OR_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(externalCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EXTCODE, XMLStringValidation
                    .isTagManadatory());
            }

            // Optional: CONTACT PERSON field's length check.
            if (!BTSLUtil.isNullString(contactPerson) && contactPerson.length() > 80) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_CONTACTPERSON_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(contactPerson, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_CONTACTPERSON, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional: CONTACT NUMBER field's length and numeric check.
            if (!BTSLUtil.isNullString(contactNumber)) {
                if (contactNumber.length() > 50) {
                    throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_CONTACTNUMBER_LENGTH_EXCEEDS);
                } else if (!BTSLUtil.isValidNumber(contactNumber)) {
                    errorMsg[0] = contactNumber.trim();
                    throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_CONTACTNUMBER_NON_NUMERIC, errorMsg);
                }
            }

            // Optional: MobileNumber field's length check.
            if (!BTSLUtil.isNullString(mobileNumber) && mobileNumber.length() > 15) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_MOBILENUMBER_LENGTH_EXCEEDS);
            } else {
                if (!BTSLUtil.isValidNumber(mobileNumber)) {
                    errorMsg[0] = mobileNumber.trim();
                    throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_MOBILENUMBER_NON_NUMERIC, errorMsg);
                }
            }

            // Optional: Division field's length check.
            if (!BTSLUtil.isNullString(division) && division.length() > 10) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_DIVISION_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(division, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_DIVISION, XMLStringValidation.isTagOptinal());
            }

            // Optional: Department field's length check.
            if (!BTSLUtil.isNullString(department) && department.length() > 10) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_DEPARTMENT_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(department, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_DEPARTMENT, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional: SSN field's length check.
            if (!BTSLUtil.isNullString(ssn) && ssn.length() > 15) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_SSN_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(ssn, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_SSN, XMLStringValidation.isTagOptinal());
            }

            // Optional: ADDRESS1 field's length check.
            if (!BTSLUtil.isNullString(address1) && address1.length() > 50) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_ADDRESS1_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(address1, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_ADDRESS1, XMLStringValidation.isTagOptinal());
            }

            // Optional: ADDRESS2 field's length check.
            if (!BTSLUtil.isNullString(address2) && address2.length() > 50) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_ADDRESS2_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(address2, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_ADDRESS2, XMLStringValidation.isTagOptinal());
            }

            // Optional: CITY field's length check.
            if (!BTSLUtil.isNullString(city) && city.length() > 30) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_CITY_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(city, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_CITY, XMLStringValidation.isTagOptinal());
            }

            // Optional: STATE field's length check.
            if (!BTSLUtil.isNullString(state) && state.length() > 30) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_STATE_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(state, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_STATE, XMLStringValidation.isTagOptinal());
            }

            // Optional: COUNTRY field's length check.
            if (!BTSLUtil.isNullString(country) && country.length() > 20) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_COUNTRY_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(country, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_CITY, XMLStringValidation.isTagOptinal());
            }

            // Optional: EMAIL ID field's length & format check.
            if (!BTSLUtil.isNullString(emailId)) {
                if (emailId.length() > 60) {
                    throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EMAILID_LENGTH_EXCEEDS);
                } else if (!BTSLUtil.validateEmailID(emailId)) {
                    errorMsg[0] = emailId.trim();
                    throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EMAILID_INVALID_FORMAT, errorMsg);
                }
            }

            // Optional: New user WEB LOGIN ID not null and length check.
            if (!BTSLUtil.isNullString(webloginId) && webloginId.length() > 20) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(webloginId, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_WEBLOGINID, XMLStringValidation
                    .isTagOptinal());
            }

            // webPassword validation
            if (!BTSLUtil.isNullString(webPassword) && webPassword.length() > (int)maxLoginPwdLength) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_LENGTH_EXCEEDS);
            }

        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());
            throw be;
        } finally {
            sb.setLength(0);
            LogFactory.printLog(METHOD_NAME, sb.append("Exiting p_requestVO ID=").append(p_requestVO.getRequestIDStr()), _log);
        }
    }

    public static void validateOperatorUserModRequest(RequestVO p_requestVO, String type, String reqDate, String externalNetworkCode, String requestLoginId, String password, String extRefNum, String userCategory, String userName, String userNamePrefix, String externalCode, String mobileNumber, String division, String department, String shortName, String userNamePrefix2, String subscriberCode, String contactPerson, String contactNumber, String ssn, String address1, String address2, String city, String state, String country, String emailId, String webloginId, String webPassword, String currentLoginId) throws BTSLBaseException {
        final String METHOD_NAME = "validateOperatorUserModRequest";
        sb.setLength(0);
    	LogFactory.printLog(METHOD_NAME, sb.append("Entered p_requestVO ID=").append(p_requestVO.getRequestIDStr()), _log);
    	sb.setLength(0);
    	LogFactory.printLog(METHOD_NAME, sb.append("type =  ").append(type ).append( "  | reqDate = ").append(reqDate ).append( "  |  externalNetworkCode =").append(externalNetworkCode ).append( "  | currentLoginId = ").append(currentLoginId ).append( "  | requestLoginId = ").append(requestLoginId ).append( "  | extRefNum = ").append(extRefNum ).append( "  |  userCategory =").append(userCategory ).append( "  |  Division =").append(division ).append( "  |  Department =").append(department ).append( "  | userName =  ").append(userName ).append( "  | userNamePrefix = ").append(userNamePrefix ).append( "  | externalCode=   ").append(externalCode ).append( "  | mobileNumber ").append(mobileNumber), _log);
        final String[] errorMsg = new String[2];
        try {
            // Creation Date Validation : For user Creation/Registration Date
            // must be specified.
            if (BTSLUtil.isNullString(reqDate)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_DATE_BLANK);
            } else {
                try {
                    BTSLUtil.getDateFromDateString(reqDate, externalDateFormat);
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT);
                }
            }

            // Mandatory: Sender External Network Code validation
            if (BTSLUtil.isNullString(externalNetworkCode)) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_BLANK);
            } else if (externalNetworkCode.length() > 2) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID);
            } else {
                alphanumericWithSpecialChar(externalNetworkCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EXTNWCODE, XMLStringValidation
                    .isTagManadatory());
            }

            // Optional: Sender LoginID validation
            if (!BTSLUtil.isNullString(requestLoginId) && requestLoginId.length() > 20) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_LENGTH_EXCEEDS);
            }

            // Optional: Sender Password validation
            if (!BTSLUtil.isNullString(password) && password.length() > (int)maxLoginPwdLength) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(password, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_PASSWORD, XMLStringValidation.isTagOptinal());
            }

            // Optional: Sender external reference number validation
            if (!BTSLUtil.isNullString(extRefNum) && extRefNum.length() > 15) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTREFNUM_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(extRefNum, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EXTREFNUM, XMLStringValidation
                    .isTagOptinal());
            }

            if ((BTSLUtil.isNullString(password) || BTSLUtil.isNullString(requestLoginId))) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_SENDER_DETAILS_INVALID);
            }

            // Mandatory: Old EXTERNALCODE CODE not null and length check
            if (BTSLUtil.isNullString(currentLoginId) || currentLoginId.length() > 10) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(currentLoginId, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_USERCATCODE, XMLStringValidation
                    .isTagManadatory());
            }

            // Mandatory: New NEWEXTERNALCODE not null and length check
            if (!BTSLUtil.isNullString(userName) && userName.length() > 80) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_USERNAME_BLANK_OR_LENGTH_INVALID);
            } else {
                alphanumericWithSpecialChar(userName, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_USERNAME, XMLStringValidation.isTagOptinal());
            }

            // Optional: New User SHORT NAME length check.
            if (!BTSLUtil.isNullString(shortName) && shortName.length() > 15) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_SHORTNAME_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(shortName, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_SHORTNAME, XMLStringValidation
                    .isTagOptinal());
            }

            // Mandatory: New USER NAME PREFIX not null and length check
            if (!BTSLUtil.isNullString(userNamePrefix) && userNamePrefix.length() > 10) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_USERNAMEPREFIX_BLANK_OR_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(userNamePrefix, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_USERNAMEPREFIX, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional:New user's SUBSCRIBER CODE field's length check.
            if (!BTSLUtil.isNullString(subscriberCode) && subscriberCode.length() > 12) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_SUBSCRIBERCODE_BLANK_OR_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(subscriberCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_USERNAME, XMLStringValidation
                    .isTagOptinal());
            }

            // Mandatory: New user EXTERNAL CODE not null and length check
            if (!BTSLUtil.isNullString(externalCode) && externalCode.length() > 20) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EXTERNALCODE_BLANK_OR_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(externalCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_EXTCODE, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional: CONTACT PERSON field's length check.
            if (!BTSLUtil.isNullString(contactPerson) && contactPerson.length() > 80) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_CONTACTPERSON_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(contactPerson, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_CONTACTPERSON, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional: CONTACT NUMBER field's length and numeric check.
            if (!BTSLUtil.isNullString(contactNumber)) {
                if (contactNumber.length() > 50) {
                    throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_CONTACTNUMBER_LENGTH_EXCEEDS);
                } else if (!BTSLUtil.isValidNumber(contactNumber)) {
                    errorMsg[0] = contactNumber.trim();
                    throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_CONTACTNUMBER_NON_NUMERIC, errorMsg);
                }
            }

            // Optional: MobileNumber field's length check.
            if (!BTSLUtil.isNullString(mobileNumber) && mobileNumber.length() > 15) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_MOBILENUMBER_LENGTH_EXCEEDS);
            } else {
                if (!BTSLUtil.isValidNumber(mobileNumber)) {
                    errorMsg[0] = mobileNumber.trim();
                    throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_MOBILENUMBER_NON_NUMERIC, errorMsg);
                }
            }

            // Optional: Division field's length check.
            if (!BTSLUtil.isNullString(division) && division.length() > 10) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_DIVISION_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(division, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_DIVISION, XMLStringValidation.isTagOptinal());
            }

            // Optional: Department field's length check.
            if (!BTSLUtil.isNullString(department) && department.length() > 10) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_DEPARTMENT_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(department, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_DEPARTMENT, XMLStringValidation
                    .isTagOptinal());
            }

            // Optional: SSN field's length check.
            if (!BTSLUtil.isNullString(ssn) && ssn.length() > 15) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_SSN_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(ssn, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_SSN, XMLStringValidation.isTagOptinal());
            }

            // Optional: ADDRESS1 field's length check.
            if (!BTSLUtil.isNullString(address1) && address1.length() > 50) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_ADDRESS1_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(address1, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_ADDRESS1, XMLStringValidation.isTagOptinal());
            }

            // Optional: ADDRESS2 field's length check.
            if (!BTSLUtil.isNullString(address2) && address2.length() > 50) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_ADDRESS2_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(address2, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_ADDRESS2, XMLStringValidation.isTagOptinal());
            }

            // Optional: CITY field's length check.
            if (!BTSLUtil.isNullString(city) && city.length() > 30) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_CITY_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(city, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_CITY, XMLStringValidation.isTagOptinal());
            }

            // Optional: STATE field's length check.
            if (!BTSLUtil.isNullString(state) && state.length() > 30) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_STATE_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(state, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_STATE, XMLStringValidation.isTagOptinal());
            }

            // Optional: COUNTRY field's length check.
            if (!BTSLUtil.isNullString(country) && country.length() > 20) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_COUNTRY_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(country, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_CITY, XMLStringValidation.isTagOptinal());
            }

            // Optional: EMAIL ID field's length & format check.
            if (!BTSLUtil.isNullString(emailId)) {
                if (emailId.length() > 60) {
                    throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EMAILID_LENGTH_EXCEEDS);
                } else if (!BTSLUtil.validateEmailID(emailId)) {
                    errorMsg[0] = emailId.trim();
                    throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_EMAILID_INVALID_FORMAT, errorMsg);
                }
            }

            // Optional: New user WEB LOGIN ID not null and length check.
            if (!BTSLUtil.isNullString(webloginId) && webloginId.length() > 20) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_LENGTH_EXCEEDS);
            } else {
                alphanumericWithSpecialChar(webloginId, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, XmlTagValueConstant.TAG_WEBLOGINID, XMLStringValidation
                    .isTagOptinal());
            }

            // webPassword validation
            if (!BTSLUtil.isNullString(webPassword) && webPassword.length() > (int)maxLoginPwdLength) {
                throw new BTSLBaseException("XMLStringValidation", METHOD_NAME, PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_LENGTH_EXCEEDS);
            }

        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());
            throw be;
        } finally {
            sb.setLength(0);
			LogFactory.printLog(METHOD_NAME, sb.append("Exiting p_requestVO ID=").append(p_requestVO.getRequestIDStr()), _log);
        }
    }
 public static void validateChannelExtRecharge(RequestVO p_requestVO, String type, String date, String extNwCode,String msisdn, String pin, String loginId, String password,String extCode,String extRefNumber, String msisdn2, String amount,String language1, String language2, String selector , String bonus, String info1, String info2, String info3, String info4, String info5, String info6, String info7, String info8, String info9, String info10, String switchid, String cellid) throws BTSLBaseException {
		 	final String METHOD_NAME="validateChannelExtRecahrge";
			sb.setLength(0);
			LogFactory.printLog(METHOD_NAME, sb.append("validateChannelExtRecahrge : Enter"), _log);	
			try {
				XMLTagValueValidation.validateType(type,XMLTagValueValidation.isTagManadatory());
				XMLTagValueValidation.validateIRISDate(date,XMLTagValueValidation.isTagManadatory());
				XMLTagValueValidation.validateExtNetworkCode(extNwCode,XMLTagValueValidation.isTagManadatory());
				XMLTagValueValidation.validateSenderDetails(msisdn,pin,loginId,password,extCode,extNwCode,XMLTagValueValidation.isTagManadatory());
				XMLTagValueValidation.validateExtRefNum(extRefNumber,XMLTagValueValidation.isTagManadatory());
				XMLTagValueValidation.validateMsisdn2(msisdn2,extNwCode,XMLTagValueValidation.isTagManadatory());
				XMLTagValueValidation.validateIRISAmount(amount, XMLTagValueValidation.isTagManadatory());
				XMLTagValueValidation.validateLanguage1(language1, XMLTagValueValidation.isTagOptinal());
				XMLTagValueValidation.validateLanguage2(language2, XMLTagValueValidation.isTagOptinal());
				if(type.equalsIgnoreCase(PretupsI.PROMOVAS_EXTGW_TYPE))
					XMLTagValueValidation.validateSelectorForPVAS(selector, XMLTagValueValidation.isTagManadatory());
				else
					XMLTagValueValidation.validateSelector(selector, XMLTagValueValidation.isTagManadatory());
				XMLTagValueValidation.validateIRISBonus(bonus, XMLTagValueValidation.isTagOptinal());
				XMLTagValueValidation.validateExternalData(info1,info2,info3,info4,info5,info6,info7,info8,info9,info10, XMLTagValueValidation.isTagOptinal());
				XMLTagValueValidation.validateSwitchIDAndCellID(switchid,cellid, XMLTagValueValidation.isTagOptinal());
			
			}
			catch(BTSLBaseException be)
			{
				sb.setLength(0);
				LogFactory.printError(METHOD_NAME, sb.append("validateChannelExtRecahrge : getMessageKey = ").append(be.getMessageKey()).append(" | getArgs = ").append(be.getArgs()), _log);	
				p_requestVO.setMessageCode(be.getMessageKey());
				p_requestVO.setMessageArguments(be.getArgs());
				throw be;
			}
			catch (RuntimeException e) {
				_log.errorTrace(METHOD_NAME,e);
			} finally {
				sb.setLength(0);
				LogFactory.printLog(METHOD_NAME, sb.append("validateChannelExtRecahrge : Exit"), _log);
			}
		}
	 public static void validateChannelExtRechargeReversal(RequestVO p_requestVO, String type, String date, String extNwCode,String msisdn, String pin, String loginId, String password,String extCode,String extRefNumber, String msisdn2,String language1, String language2) throws BTSLBaseException {
			final String METHOD_NAME="validateChannelExtRecahrge";
			sb.setLength(0);
			LogFactory.printLog(METHOD_NAME, sb.append(" : Enter"), _log);	
			try {
				XMLTagValueValidation.validateType(type,XMLTagValueValidation.isTagManadatory());
				XMLTagValueValidation.validateIRISDate(date,XMLTagValueValidation.isTagManadatory());
				XMLTagValueValidation.validateExtNetworkCode(extNwCode,XMLTagValueValidation.isTagManadatory());
				XMLTagValueValidation.validateSenderDetails(msisdn,pin,loginId,password,extCode,extNwCode,XMLTagValueValidation.isTagManadatory());
				XMLTagValueValidation.validateExtRefNum(extRefNumber,XMLTagValueValidation.isTagManadatory());
				XMLTagValueValidation.validateMsisdn2(msisdn2,extNwCode,XMLTagValueValidation.isTagManadatory());
				XMLTagValueValidation.validateLanguage1(language1, XMLTagValueValidation.isTagOptinal());
				XMLTagValueValidation.validateLanguage2(language2, XMLTagValueValidation.isTagOptinal());
			}
			catch(BTSLBaseException be)
			{
				sb.setLength(0);
				LogFactory.printError(METHOD_NAME, sb.append("validateChannelExtRechargeReversal : getMessageKey = ").append(be.getMessageKey()).append(" | getArgs = ").append(be.getArgs()), _log);	
				p_requestVO.setMessageCode(be.getMessageKey());
				p_requestVO.setMessageArguments(be.getArgs());
				throw be;
			}
			catch (RuntimeException e) {
				_log.errorTrace(METHOD_NAME,e);
			} finally {
		        sb.setLength(0);
		        LogFactory.printLog(METHOD_NAME, sb.append("validateChannelExtRechargeReversal : Exit"), _log);
			}
		}

	/**
	 * XMLStringValidation.java
	 * @param p_requestVO
	 * @param extNwCode
	 * @param extRefNumber
	 * @param currencyRecord
	 * @param date
	 * @throws BTSLBaseException
	 * void
	 * akanksha.gupta
	 * 08-Aug-2016 1:11:28 pm
	 */
	public static void validateCurrencyConversionRequest(RequestVO p_requestVO, String extNwCode, String extRefNumber,
			String currencyRecord, String date) throws BTSLBaseException {
		// TODO Auto-generated method stub
       
        final String METHOD_NAME = "validateCurrencyConversionRequest";
        final String CLASS_NAME = "XMLStringValidation";
        sb.setLength(0);
        LogFactory.printLog(METHOD_NAME, sb.append(": Enter"), _log);
        try {
            XMLTagValueValidation.validateExtNetworkCode(extNwCode, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateExtRefNum(extRefNumber, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateTxnDate(date, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateCurrencyRecord(currencyRecord, XMLTagValueValidation.isTagManadatory());
      
        } catch (BTSLBaseException be) {
            sb.setLength(0);
        	LogFactory.printLog(METHOD_NAME, sb.append(": getMessageKey = ").append(be.getMessageKey() ).append( " | getArgs = ").append(be.getArgs()), _log);
            p_requestVO.setMessageCode(be.getMessageKey());
            p_requestVO.setMessageArguments(be.getArgs());

            throw be;
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            _log.errorTrace(METHOD_NAME, e);
        } finally {
			sb.setLength(0);
			LogFactory.printLog(METHOD_NAME, sb.append( ": Exit"), _log);
        }
	}
	//added for channel user transfer
	/**
	 * @description : This method will validate the user Transfer request coming through external system.
	 * @author : Naveen
	 * @date : 18-Nov-2015
	 * @throws BTSLBaseException  
	 */
	public static void validateExtChannelUserTransferRequest(RequestVO p_requestVO,String date,String extNetworkCode,String empCode,String loginId,String passwd,String extCode,String extRefNo,String networkCode,String fromUserMsisdn,String fromUserOriginId,String fromUserExtCode,String toParentMsisdn,String toParentOriginId,String toParentExtCode,String toUserGeoCode,String toUserCatCode,String fromUserLoginId,String toParentLoginId) throws BTSLBaseException 
		{
		final String METHOD_NAME="validateExtChannelUserTransferRequest";
		sb.setLength(0);
		LogFactory.printLog(METHOD_NAME, sb.append("Entered p_requestVO ID=").append(p_requestVO.getRequestIDStr()), _log);
		String[] errorMsg=new String[2];
		HashMap requestHashMap = p_requestVO.getRequestMap();
		try
		{
			// Modification Date Validation : For user Creation/Registration Date must be specified.
			if(BTSLUtil.isNullString(date))
				throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,PretupsErrorCodesI.EXTSYS_REQ_DATE_BLANK);
			else
			{
				try
				{
					BTSLUtil.getDateFromDateString(date,externalDateFormat);
				}
				catch (Exception e)
				{
					_log.errorTrace(METHOD_NAME, e);
					throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT);
				}
			}
			//Mandatory: Sender External Network Code validation 
			if(BTSLUtil.isNullString(extNetworkCode))
				throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_BLANK);
			else if(extNetworkCode.length() > 2)
				throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID);
			else
				alphanumericWithSpecialChar(extNetworkCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL,XmlTagValueConstant.TAG_EXTNWCODE,XMLStringValidation.isTagManadatory());
		
			//Mandatory: Sender  Network Code validation 
			if(BTSLUtil.isNullString(networkCode))
				throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_BLANK);
			else if(networkCode.length() > 2)
				throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID);
			else
				alphanumericWithSpecialChar(networkCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL,XmlTagValueConstant.TAG_EXTNWCODE,XMLStringValidation.isTagManadatory());
			
			XMLTagValueValidation.validateSenderDtl(loginId,passwd,empCode,XMLTagValueValidation.isTagManadatory());
			//Optional: EmployeeCode validation
			if(!BTSLUtil.isNullString(empCode) && empCode.length() > 12)
				throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,PretupsErrorCodesI.EXTSYS_REQ_EMPCODE_LENGTH_EXCEEDS);
			else
				alphanumericWithSpecialChar(empCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL,XmlTagValueConstant.TAG_EMPCODE,XMLStringValidation.isTagOptinal());
			
			// Optional: Sender LoginID validation
			if(!BTSLUtil.isNullString(loginId) && loginId.length() > 20)
				throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_LENGTH_EXCEEDS);
			else
				alphanumericWithSpecialChar(loginId, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL,XmlTagValueConstant.TAG_LOGIN_ID,XMLStringValidation.isTagOptinal());
			
			// Optional: Sender Password validation
			if(!BTSLUtil.isNullString(passwd) && passwd.length()>(int)maxLoginPwdLength)
				throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_LENGTH_EXCEEDS);
			else
				alphanumericWithSpecialChar(passwd, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL,XmlTagValueConstant.TAG_PASSWORD,XMLStringValidation.isTagOptinal());
			
			// Optional: External  Code validation
			if (!BTSLUtil.isNullString(extCode) && extCode.length()>20)
				throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,PretupsErrorCodesI.EXTSYS_REQ_EXTERNALCODE_BLANK_OR_LENGTH_EXCEEDS);
			else
				alphanumericWithSpecialChar(extCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL,XmlTagValueConstant.TAG_EXTCODE,XMLStringValidation.isTagOptinal());
			
			// Optional: Sender External RefNo Code validation
			/*if (!BTSLUtil.isNullString(extRefNo) && extRefNo.length()>15)
				throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,PretupsErrorCodesI.EXTSYS_REQ_EXTREFNUM_LENGTH_EXCEEDS);
			else
				alphanumericWithSpecialChar(extRefNo, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL,XmlTagValueConstant.TAG_EXTREFNUM,XMLStringValidation.isTagOptinal());*/
			XMLTagValueValidation.validateExtRefNum(extRefNo,XMLTagValueValidation.isTagOptinal());
			
			//Mandatory: FROM USER MSISDN((MSISDN1)) of can't be blank in the user tranfer request.
			/*if(BTSLUtil.isNullString(fromUserMsisdn))
				throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,PretupsErrorCodesI.EXTSYS_REQ_FROM_USER_MSISDN_BLANK);
	*/
			if(!BTSLUtil.isNullString(fromUserMsisdn)){
				validateMSISDN(fromUserMsisdn,networkCode,PretupsErrorCodesI.EXTSYS_REQ_MSISDN_INVALID_FORMAT,errorMsg);
				numeric(fromUserMsisdn, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC,XmlTagValueConstant.TAG_MSISDN,XMLStringValidation.isTagManadatory());
			}
			// fromuserloginid id added
			if(!BTSLUtil.isNullString(fromUserLoginId) && fromUserLoginId.length() > 20)
				throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_LENGTH_EXCEEDS);
			else
				alphanumericWithSpecialChar(fromUserLoginId, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL,XmlTagValueConstant.TAG_LOGIN_ID,XMLStringValidation.isTagManadatory());
			
			
			/*if(SystemPreferences.ORIGIN_ID_ALLOW)  // added to check Origin ID allowed
				{
				  if (BTSLUtil.isNullString(fromUserOriginId) || fromUserOriginId.length()>15) 
						throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,PretupsErrorCodesI.EXTSYS_REQ_ORIGINID_BLANK_OR_LENGTH_EXCEEDS);
				}*/
			
			if (!BTSLUtil.isNullString(fromUserExtCode) && fromUserExtCode.length()>20)
				throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,PretupsErrorCodesI.EXTSYS_REQ_EXTERNALCODE_BLANK_OR_LENGTH_EXCEEDS);
			else
				alphanumericWithSpecialChar(fromUserExtCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL,XmlTagValueConstant.TAG_EXTCODE,XMLStringValidation.isTagOptinal());
			
			//Mandatory: FROM USER MSISDN((MSISDN1)) of can't be blank in the user tranfer request.
			/*if(BTSLUtil.isNullString(toParentMsisdn))
				throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,PretupsErrorCodesI.EXTSYS_REQ_TO_PARENT_MSISDN_BLANK);
					*/
			if(!BTSLUtil.isNullString(toParentMsisdn)){
				validateMSISDN(toParentMsisdn,networkCode,PretupsErrorCodesI.EXTSYS_REQ_MSISDN_INVALID_FORMAT,errorMsg);
				numeric(toParentMsisdn, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC,XmlTagValueConstant.TAG_MSISDN,XMLStringValidation.isTagManadatory());
			}
			
			// fromuserloginid id added
			if(!BTSLUtil.isNullString(toParentLoginId) && toParentLoginId.length() > 20)
				throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,PretupsErrorCodesI.EXTSYS_REQ_WEBLOGINID_LENGTH_EXCEEDS);
			else
				alphanumericWithSpecialChar(toParentLoginId, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL,XmlTagValueConstant.TAG_LOGIN_ID,XMLStringValidation.isTagManadatory());
			
			
			
			/*if(SystemPreferences.ORIGIN_ID_ALLOW)  // added to check Origin ID allowed
				{
				  if (BTSLUtil.isNullString(toParentOriginId) || fromUserOriginId.length()>15) 
						throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,PretupsErrorCodesI.EXTSYS_REQ_ORIGINID_BLANK_OR_LENGTH_EXCEEDS);
				}
*/			// Optional: to parent External  Code validation
			if (!BTSLUtil.isNullString(toParentExtCode) && toParentExtCode.length()>20)
				throw new BTSLBaseException("XMLStringValidation", METHOD_NAME,PretupsErrorCodesI.EXTSYS_REQ_EXTERNALCODE_BLANK_OR_LENGTH_EXCEEDS);
			else
				alphanumericWithSpecialChar(toParentExtCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL,XmlTagValueConstant.TAG_EXTCODE,XMLStringValidation.isTagOptinal());
			
			//Mandatory: to User Geography Code can't be blank for user tranfer request.
			if (!BTSLUtil.isNullString(toUserGeoCode) && toUserGeoCode.length()>10)
				throw new BTSLBaseException("XMLStringValidation",METHOD_NAME,PretupsErrorCodesI.EXTSYS_REQ_USERCATCODE_BLANK_OR_LENGTH_EXCEEDS);
			else
				alphanumericWithSpecialChar(toUserGeoCode,PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL,XmlTagValueConstant.TAG_GEOGRAPHYCODE,XMLStringValidation.isTagManadatory());
			
			//Mandatory: to User Category Code can't be blank for user tranfer request.
			if (!BTSLUtil.isNullString(toUserCatCode) && toUserCatCode.length()>10)
				throw new BTSLBaseException("XMLStringValidation",METHOD_NAME,PretupsErrorCodesI.EXTSYS_REQ_USERCATCODE_BLANK_OR_LENGTH_EXCEEDS);
			else
				alphanumericWithSpecialChar(toUserCatCode,PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL,XmlTagValueConstant.TAG_CATCODE,XMLStringValidation.isTagManadatory());
			
		}
		catch(BTSLBaseException be)
		{
			p_requestVO.setMessageCode(be.getMessageKey());
			p_requestVO.setMessageArguments(be.getArgs());
			throw be;
		} finally {
			sb.setLength(0);
			_log.debug(METHOD_NAME,sb.append("Exiting p_requestVO ID=").append(p_requestVO.getRequestIDStr()), _log);
		}
	}
	
		//DATA
		public static void validateChannelExtCP2PDataTransferRequest(RequestVO p_requestVO, String type, String date, String extNwCode,String msisdn, String pin, String loginId, String password,String extCode,String extRefNumber, String msisdn2, String amount,String language1, String language2, String selector) throws BTSLBaseException {
			final String methodName= "validateChannelExtCP2PDataTransferRequest";	
			sb.setLength(0);
			LogFactory.printLog(methodName, sb.append(methodName).append( ": Enter"), _log);
			try {
				XMLTagValueValidation.validateType(type,XMLTagValueValidation.isTagManadatory());
				XMLTagValueValidation.validateDate(date,XMLTagValueValidation.isTagOptinal());
				XMLTagValueValidation.validateExtNetworkCode(extNwCode,XMLTagValueValidation.isTagManadatory());
				XMLTagValueValidation.validateSenderDetails(msisdn,pin,loginId,password,extCode,extNwCode,XMLTagValueValidation.isTagManadatory());
				XMLTagValueValidation.validateExtRefNum(extRefNumber,XMLTagValueValidation.isTagOptinal());
				XMLTagValueValidation.validateMsisdn2(msisdn2,extNwCode,XMLTagValueValidation.isTagManadatory());
				XMLTagValueValidation.validateAmount(amount, XMLTagValueValidation.isTagManadatory());
				XMLTagValueValidation.validateLanguage1(language1, XMLTagValueValidation.isTagOptinal());
				XMLTagValueValidation.validateLanguage2(language2, XMLTagValueValidation.isTagOptinal());
				XMLTagValueValidation.validateSelector(selector, XMLTagValueValidation.isTagManadatory());
			}
			catch(BTSLBaseException be)
			{
				sb.setLength(0);
				LogFactory.printError(methodName, sb.append("validateChannelExtCP2PDataTransferRequest : getMessageKey = ").append(be.getMessageKey()).append(" | getArgs = ").append(be.getArgs()), _log);	
				p_requestVO.setMessageCode(be.getMessageKey());
				p_requestVO.setMessageArguments(be.getArgs());
				throw be;
			}
			catch (RuntimeException e) {
				_log.errorTrace(methodName, e);
			} finally {
				sb.setLength(0);
				LogFactory.printLog(methodName, sb.append("validateChannelExtCP2PDataTransferRequest : Exit"), _log);
			}
		}
	  
		/**
	     * Method validateSystemReceiverRequest
	     * validateSystemReceiverRequest Method , validate the request from System Receiver
	     * 
	     * @param requestVO
	     * @param type
	     * @param date
	     * @param extNwCode
	     * @param extRefNumber
	     */
	    public static void validateSystemReceiverRequest(RequestVO requestVO, String type, String date, String extNwCode, String extRefNumber) throws BTSLBaseException {
        final String methodName = "validateSystemReceiverRequest";
		sb.setLength(0);
		LogFactory.printLog(methodName, sb.append("Entered requestVO=").append(requestVO ).append( " type=").append(type).append(" date=").append(date).append(" extNwCode=").append(extNwCode).append(" extRefNumber=").append(extRefNumber).toString(), _log);
        try {
            XMLTagValueValidation.validateType(type, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateDate(date, XMLTagValueValidation.isTagOptinal());
            XMLTagValueValidation.validateExtNetworkCode(extNwCode, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateExtRefNum(extRefNumber, XMLTagValueValidation.isTagOptinal());
        } catch (BTSLBaseException be) {
        	sb.setLength(0);
            LogFactory.printError(methodName, sb.append("validateSystemReceiverRequest : getMessageKey = ").append(be.getMessageKey() ).append( " | getArgs = ").append(be.getArgs()).toString(), _log);
            requestVO.setMessageCode(be.getMessageKey());
            requestVO.setMessageArguments(be.getArgs());
            throw be;
        } catch (RuntimeException e) {
            _log.errorTrace(methodName, e);
        } finally {
	        sb.setLength(0);
	        LogFactory.printLog(methodName, "validateSystemReceiverRequest : Exit", _log);
        }
	}

	    /**
	     * Method validateOptReceiverRequest
	     * validateOptReceiverRequest Method , validate the request from Operator Receiver
	     * 
	     * @param requestVO
	     * @param type
	     * @param date
	     * @param extNwCode
	     * @param extRefNumber
	     * @param loginId
	     * @param password
	     * @param msisdn
	     * @param pin
	     * @param extCode
	     */
	    public static void validateOptReceiverRequest(RequestVO requestVO, String type, String date, String extNwCode, 
	    		String extRefNumber, String loginId, String password, String msisdn, String pin, String extCode) throws BTSLBaseException {
        final String methodName = "validateOptReceiverRequest";
        sb.setLength(0);
		LogFactory.printLog(methodName, sb.append("Entered requestVO=").append(requestVO ).append( " type=").append(type).append(" date=").append(date).append(" extNwCode=").append(extNwCode).append(" extRefNumber=").append(
        extRefNumber).append(" loginId=").append(loginId).append(" password=").append(password).append(" msisdn=").append(msisdn).append(" pin=").append(pin).append(" extCode=").append(extCode).toString(), _log);
        try {
            XMLTagValueValidation.validateType(type, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateDate(date, XMLTagValueValidation.isTagOptinal());
            XMLTagValueValidation.validateExtNetworkCode(extNwCode, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validateExtRefNum(extRefNumber, XMLTagValueValidation.isTagOptinal());
            if(BTSLUtil.isStringIn(requestVO.getType(),Constants.getProperty("EXTGW_SENDER_OPTIONAL_PIN_ACTIONS")))
				XMLTagValueValidation.validateSenderDetails(msisdn,pin,loginId,password,extCode,extNwCode,XMLTagValueValidation.isTagOptinal());
			else
            	XMLTagValueValidation.validateSenderDetails(msisdn, pin, loginId, password, extCode, extNwCode, XMLTagValueValidation.isTagManadatory());
            
        } catch (BTSLBaseException be) {
            sb.setLength(0);
            LogFactory.printError(methodName, sb.append("validateOptReceiverRequest : getMessageKey = ").append(be.getMessageKey() ).append( " | getArgs = ").append(be.getArgs()).toString(), _log);
            requestVO.setMessageCode(be.getMessageKey());
            requestVO.setMessageArguments(be.getArgs());
            throw be;
        } catch (RuntimeException e) {
            _log.errorTrace(methodName, e);
        } finally {
        	sb.setLength(0);
        	LogFactory.printLog(methodName, "validateOptReceiverRequest : Exit", _log);
        }
	}
	    
	    public static void validateVoucherValidateRequest(RequestVO p_requestVO, String type, String date, String extNwCode, String voucherPin, String voucherSerialNo,String extRefNumber, String language1) throws BTSLBaseException {
	    	final String METHOD_NAME = "validateVoucherValidateRequest";
	        sb.setLength(0);
	        LogFactory.printLog(METHOD_NAME, sb.append("validateVoucherValidateRequest : Enter"), _log);
	        try {
	            XMLTagValueValidation.validateType(type, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateVoucherDate(date, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateExtNetworkCode(extNwCode, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateExtRefNum(extRefNumber, XMLTagValueValidation.isTagOptinal());
	            XMLTagValueValidation.validateVoucherPinSerial(voucherPin, XMLTagValueValidation.isTagManadatory(), voucherSerialNo);
	            XMLTagValueValidation.validateLanguage1(language1, XMLTagValueValidation.isTagManadatory());	            
	        } catch (BTSLBaseException be) {
	            sb.setLength(0);
	            LogFactory.printError(METHOD_NAME, sb.append("validateVoucherValidateRequest : getMessageKey = ").append(be.getMessageKey() ).append( " | getArgs = ").append(be.getArgs()), _log);
	            p_requestVO.setSuccessTxn(false);
	            p_requestVO.setMessageCode(be.getMessageKey());
	            p_requestVO.setMessageArguments(be.getArgs());
	            throw be;
	        } catch (RuntimeException e) {
	            // TODO Auto-generated catch block
	            _log.errorTrace(METHOD_NAME, e);
	        } finally {
	            sb.setLength(0);
	            LogFactory.printLog(METHOD_NAME, sb.append("validateVoucherValidateRequest : Exit"), _log);
	        }
	    }
	    public static void validateVoucherReserveRequest(RequestVO p_requestVO, String type, String date, String extNwCode, String voucherPin, String voucherSerialNo,String extRefNumber, String language1, String msisdn) throws BTSLBaseException {
	    	final String METHOD_NAME = "validateVoucherReserveRequest";
	        sb.setLength(0);
	        LogFactory.printLog(METHOD_NAME, sb.append("validateVoucherReserveRequest : Enter"), _log);
	        try {
	            XMLTagValueValidation.validateType(type, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateVoucherDate(date, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateExtNetworkCode(extNwCode, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateExtRefNum(extRefNumber, XMLTagValueValidation.isTagOptinal());
	            XMLTagValueValidation.validateVoucherPinSerial(voucherPin, XMLTagValueValidation.isTagManadatory(), voucherSerialNo);
	            XMLTagValueValidation.validateMsisdn(msisdn, extNwCode, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateLanguage1(language1, XMLTagValueValidation.isTagManadatory());	            
	        } catch (BTSLBaseException be) {
	            sb.setLength(0);
	            LogFactory.printError(METHOD_NAME, sb.append("validateVoucherReserveRequest : getMessageKey = ").append(be.getMessageKey() ).append( " | getArgs = ").append(be.getArgs()), _log);
	            p_requestVO.setSuccessTxn(false);
	            p_requestVO.setMessageCode(be.getMessageKey());
	            p_requestVO.setMessageArguments(be.getArgs());
	            throw be;
	        } catch (RuntimeException e) {
	            // TODO Auto-generated catch block
	            _log.errorTrace(METHOD_NAME, e);
	        } finally {
	            sb.setLength(0);
	            LogFactory.printLog(METHOD_NAME, sb.append("validateVoucherReserveRequest : Exit"), _log);
	        }
	    }
	    public static void validateDirectConsumptionRequest(RequestVO p_requestVO, String type, String date, String extNwCode, String voucherPin, String voucherSerialNo,String extRefNumber, String language1, String msisdn) throws BTSLBaseException {
	    	final String METHOD_NAME = "validateDirectConsumptionRequest";
	        sb.setLength(0);
	        LogFactory.printLog(METHOD_NAME, sb.append("validateDirectConsumptionRequest : Enter"), _log);
	        try {
	            XMLTagValueValidation.validateType(type, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateVoucherDate(date, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateExtNetworkCode(extNwCode, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateExtRefNum(extRefNumber, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateVoucherPinSerial(voucherPin, XMLTagValueValidation.isTagManadatory(), voucherSerialNo);
	            XMLTagValueValidation.validateMsisdn(msisdn, extNwCode, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateLanguage1(language1, XMLTagValueValidation.isTagManadatory());	            
	        } catch (BTSLBaseException be) {
	            sb.setLength(0);
	            LogFactory.printError(METHOD_NAME, sb.append("validateDirectConsumptionRequest : getMessageKey = ").append(be.getMessageKey() ).append( " | getArgs = ").append(be.getArgs()), _log);
	            p_requestVO.setSuccessTxn(false);
	            p_requestVO.setMessageCode(be.getMessageKey());
	            p_requestVO.setMessageArguments(be.getArgs());
	            throw be;
	        } catch (RuntimeException e) {
	            // TODO Auto-generated catch block
	            _log.errorTrace(METHOD_NAME, e);
	        } finally {
	            sb.setLength(0);
	            LogFactory.printLog(METHOD_NAME, sb.append("validateDirectConsumptionRequest : Exit"), _log);
	        }
	    }
	    
	    public static void validateDirectRollbackRequest(RequestVO p_requestVO, String type, String date, String extNwCode, String voucherPin, String voucherSerialNo,String extRefNumber, String language1, String msisdn, String stateChangeReason) throws BTSLBaseException {
	    	final String METHOD_NAME = "validateDirectRollbackRequest";
	        sb.setLength(0);LogFactory.printLog(METHOD_NAME, sb.append("validateDirectRollbackRequest : Enter"), _log);
	        try {
	            XMLTagValueValidation.validateType(type, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateVoucherDate(date, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateExtNetworkCode(extNwCode, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateExtRefNum(extRefNumber, XMLTagValueValidation.isTagOptinal());
	            XMLTagValueValidation.validateVoucherPinSerial(voucherPin, XMLTagValueValidation.isTagManadatory(), voucherSerialNo);
	            XMLTagValueValidation.validateMsisdn(msisdn, extNwCode, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateLanguage1(language1, XMLTagValueValidation.isTagManadatory());	 
	            XMLTagValueValidation.validateStateChangeReason(stateChangeReason, XMLTagValueValidation.isTagManadatory());
	        } catch (BTSLBaseException be) {
	            sb.setLength(0);
	            LogFactory.printError(METHOD_NAME, sb.append("validateDirectRollbackRequest : getMessageKey = ").append(be.getMessageKey() ).append( " | getArgs = ").append(be.getArgs()), _log);
	            p_requestVO.setSuccessTxn(false);
	            p_requestVO.setMessageCode(be.getMessageKey());
	            p_requestVO.setMessageArguments(be.getArgs());
	            throw be;
	        } catch (RuntimeException e) {
	            // TODO Auto-generated catch block
	            _log.errorTrace(METHOD_NAME, e);
	        } finally {
	            sb.setLength(0);
	            LogFactory.printLog(METHOD_NAME, sb.append("validateDirectRollbackRequest : Exit"), _log);
	        }
	    }

		public static void validateExtDigitalVouchersAvailabilityRequest(RequestVO p_requestVO, String type, String extNwCode, String msisdn, String pin, String loginId,
				String password, String extCode, String voucherType, String voucherSegment, String denomination, String voucherProfile, String language1) throws BTSLBaseException 
		{
			final String METHOD_NAME = "validateExtDigitalVouchersAvailabilityRequest";
	        sb.setLength(0);
	        LogFactory.printLog(METHOD_NAME, sb.append("validateExtDigitalVouchersAvailabilityRequest : Enter"), _log);
	        try {
	            XMLTagValueValidation.validateType(type, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateExtNetworkCode(extNwCode, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateSenderDetails(msisdn, pin, loginId, password, extCode, extNwCode, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateLanguage1(language1, XMLTagValueValidation.isTagOptinal());
	            XMLTagValueValidation.validateVoucherType1(voucherType, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateVoucherSegment(voucherSegment, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateAmount(denomination, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateVoucherProfile(voucherProfile, XMLTagValueValidation.isTagOptinal());
	        } catch (BTSLBaseException be) {
	            sb.setLength(0);
	            LogFactory.printError(METHOD_NAME, sb.append(METHOD_NAME ).append( ": getMessageKey = ").append(be.getMessageKey() ).append( " | getArgs = ").append(be.getArgs()), _log);
	            p_requestVO.setMessageCode(be.getMessageKey());
	            p_requestVO.setMessageArguments(be.getArgs());
	            throw be;
	        } catch (RuntimeException e) {
	            _log.errorTrace(METHOD_NAME, e);
	        } finally {
		        sb.setLength(0);
		        LogFactory.printLog(METHOD_NAME, ": Exit", _log);
	        }
		}
		
		/**
		 * Validate DVD request
		 * @param p_requestVO
		 * @param type
		 * @param date
		 * @param extNwCode
		 * @param msisdn
		 * @param pin
		 * @param loginId
		 * @param password
		 * @param extCode
		 * @param extRefNumber
		 * @param msisdn2
		 * @param amount
		 * @param language1
		 * @param language2
		 * @param selector
		 * @param voucherType
		 * @param voucherSegment
		 * @param voucherProfile
		 * @param quantity
		 * @throws BTSLBaseException
		 */
		public static void validateExtDVDRequest(RequestVO p_requestVO, String type, String date, String extNwCode, String msisdn, 
				String pin, String loginId, String password, String extCode, String extRefNumber, String msisdn2, String amount, 
				String language1, String language2, String selector, String voucherType, String voucherSegment, String voucherProfile, 
				String quantity) throws BTSLBaseException {
			final String methodName = "validateExtDVDRequest";
	        sb.setLength(0);
	        LogFactory.printLog(methodName, sb.append("validateExtDVDRequest : Enter"), _log);
	        try {
	            XMLTagValueValidation.validateType(type, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateDate(date, XMLTagValueValidation.isTagOptinal());
	            XMLTagValueValidation.validateExtNetworkCode(extNwCode, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateSenderDetails(msisdn, pin, loginId, password, extCode, extNwCode, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateExtRefNum(extRefNumber, XMLTagValueValidation.isTagOptinal());
	            XMLTagValueValidation.validateAmount(amount, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateLanguage1(language1, XMLTagValueValidation.isTagOptinal());
	            XMLTagValueValidation.validateLanguage2(language2, XMLTagValueValidation.isTagOptinal());
	            XMLTagValueValidation.validateSelector(selector, XMLTagValueValidation.isTagOptinal());
	            XMLTagValueValidation.validateVoucherType(voucherType, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateVoucherSegment(voucherSegment, XMLTagValueValidation.isTagManadatory());
	            if(!isVoucherProfileOptional)
	            	XMLTagValueValidation.validateVoucherProfile(voucherProfile, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateQuantity(quantity, XMLTagValueValidation.isTagManadatory());
	            
	        } catch (BTSLBaseException be) {
	            sb.setLength(0);
	            LogFactory.printError(methodName, sb.append("validateExtDVDRequest : getMessageKey = ").append(be.getMessageKey() ).append( " | getArgs = ").append(be.getArgs()), _log);
	            p_requestVO.setMessageCode(be.getMessageKey());
	            p_requestVO.setMessageArguments(be.getArgs());
	            throw be;
	        } catch (RuntimeException e) {
	            _log.errorTrace(methodName, e);
	        } finally {
		        sb.setLength(0);
		        LogFactory.printLog(methodName, sb.append("validateExtDVDRequest : Exit"), _log);
	        }
	    }

public static void validateVoucherO2CTransferParsing(RequestVO p_requestVO, String extNwCode, String msisdn, String pin, String extCode, String extNumber, String extDate, String productCode, String trfCategory, String paymentType, String paymentNumber, String paymentDate, String remarks,String fromserial,String toserial) throws BTSLBaseException {
			final String METHOD_NAME = "validateCommonO2CTransferAPIParsing";
		    sb.setLength(0);
		    LogFactory.printLog(METHOD_NAME, sb.append("validateCommonO2CTransferAPIParsing : Enter"), _log);
	        try {
	            XMLTagValueValidation.validateExtNetworkCode(extNwCode, XMLTagValueValidation.isTagManadatory());
	            // XMLTagValueValidation.validateSenderDetails(msisdn,pin,"","",extCode,extNwCode,XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateByPassPinSenderDetails(msisdn, pin, "", "", extCode, extNwCode, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateExtRefNum(extNumber, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateTxnDate(extDate, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateProductCode(productCode, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateTrfCategoryCode(trfCategory, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validatePaymentType(paymentType, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validatePaymentNumber(paymentNumber, XMLTagValueValidation.isTagOptinal());
	            XMLTagValueValidation.validatePaymentDate(paymentDate, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateRemark(remarks, XMLTagValueValidation.isTagOptinal());
	            XMLTagValueValidation.validateVoucherSerialNo(fromserial, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validateVoucherSerialNo(toserial, XMLTagValueValidation.isTagManadatory());
	            XMLTagValueValidation.validatePin(pin, XMLTagValueValidation.isTagOptinal());

	        } catch (BTSLBaseException be) {
	            sb.setLength(0);
	            LogFactory.printError(METHOD_NAME, sb.append("validateCommonO2CTransferAPIParsing : getMessageKey = ").append(be.getMessageKey() ).append( " | getArgs = ").append(be.getArgs()), _log);
	            p_requestVO.setMessageCode(be.getMessageKey());
	            p_requestVO.setMessageArguments(be.getArgs());

	            throw be;
	        } catch (RuntimeException e) {
	            // TODO Auto-generated catch block
	            _log.errorTrace(METHOD_NAME, e);
	        } finally {
	            sb.setLength(0);
	            LogFactory.printLog(METHOD_NAME, sb.append("validateCommonO2CTransferAPIParsing : Exit"), _log);
	        }
	    }

public static void validateExtC2CVomsTransferRequest(RequestVO p_requestVO, String extNwCode, String msisdn1, String pin, String loginId, String password, String extCode, String extRefNumber, String msisdn2, String voucherType, String voucherSegment,String language1,String voucherDetails) throws BTSLBaseException {
		final String methodName = "validateExtC2CVomsTransferRequest";
	    sb.setLength(0);
		LogFactory.printLog(methodName, sb.append("validateExtC2CVomsTransferRequest : Entered"), _log);	
    try {
        XMLTagValueValidation.validateExtNetworkCode(extNwCode, XMLTagValueValidation.isTagManadatory());
        XMLTagValueValidation.validateSenderDetails(msisdn1, pin, loginId, password, extCode, extNwCode, XMLTagValueValidation.isTagManadatory());
        XMLTagValueValidation.validateExtRefNum(extRefNumber, XMLTagValueValidation.isTagOptinal());
        XMLTagValueValidation.validateMsisdn2(msisdn2, extNwCode, XMLTagValueValidation.isTagOptinal());
        XMLTagValueValidation.validateLanguage1(language1, XMLTagValueValidation.isTagOptinal());
        XMLTagValueValidation.validateVoucherType(voucherType, XMLTagValueValidation.isTagManadatory());
        XMLTagValueValidation.validateVoucherSegment(voucherSegment, XMLTagValueValidation.isTagManadatory());
        String[] voucherDetailsArr = voucherDetails.split(",");
        
        for (String voucherDetailsArrObj : voucherDetailsArr) {
               if (voucherDetailsArrObj != null && voucherDetailsArrObj.trim().length() > 0) {
            	   int index = voucherDetailsArrObj.indexOf(":");
            	   int lastIndex = voucherDetailsArrObj.lastIndexOf(":");
            	   if(index != lastIndex){
                    String denomination = voucherDetailsArrObj.substring(0, index);
                    XMLTagValueValidation.validateDenomination(denomination, XMLTagValueValidation.isTagManadatory());
                    final String fromSerialNo = voucherDetailsArrObj.substring(index+1, lastIndex);
                    XMLTagValueValidation.validateSerialNo(fromSerialNo, XMLTagValueValidation.isTagManadatory());
                    final String toSerialNo = voucherDetailsArrObj.substring(lastIndex+1, voucherDetailsArrObj.length());
                    XMLTagValueValidation.validateSerialNo(toSerialNo, XMLTagValueValidation.isTagManadatory());
            	   }
            	   else{
            		   String denomination = voucherDetailsArrObj.substring(0, index);
                       XMLTagValueValidation.validateDenomination(denomination, XMLTagValueValidation.isTagManadatory());
                       final String quantity = voucherDetailsArrObj.substring(index+1, voucherDetailsArrObj.length());
                       XMLTagValueValidation.validateQuantity(quantity, XMLTagValueValidation.isTagManadatory());
                       
            	   }
               }
        }

    } catch (BTSLBaseException be) {
		sb.setLength(0);
		LogFactory.printError(methodName, sb.append("validateExtC2CVomsTransferRequest : getMessageKey = ").append(be.getMessageKey() ).append( " | getArgs = ").append(be.getArgs()), _log);
        p_requestVO.setMessageCode(be.getMessageKey());
        p_requestVO.setMessageArguments(be.getArgs());
        throw be;
    } catch (RuntimeException e) {
        // TODO Auto-generated catch block
        _log.errorTrace(methodName, e);
    } finally {
	    sb.setLength(0);
	    LogFactory.printLog(methodName, sb.append("validateExtC2CVomsTransferRequest : Exit"), _log);
    }
}

/**
 * @param p_requestVO
 * @param extNwCode
 * @param msisdn
 * @param pin
 * @param loginId
 * @param password
 * @param extCode
 * @param fromDate
 * @param toDate
 * @param serviceType
 * @param language1
 * @throws BTSLBaseException
 */
public static void validateExtAllTxnCountRequest(RequestVO p_requestVO, String extNwCode, String msisdn, String pin, String loginId, String password, String extCode, String fromDate, String toDate, String serviceType,String language1) throws BTSLBaseException {
	final String methodName = "validateExtAllTxnCountRequest";
    sb.setLength(0);
    LogFactory.printLog(methodName, sb.append("validateExtAllTxnCountRequest : Entered"), _log);	
    try {
        XMLTagValueValidation.validateExtNetworkCode(extNwCode, XMLTagValueValidation.isTagManadatory());
        XMLTagValueValidation.validateSenderDetails(msisdn, pin, loginId, password, extCode, extNwCode, XMLTagValueValidation.isTagManadatory());
        XMLTagValueValidation.validateLanguage1(language1, XMLTagValueValidation.isTagOptinal());
        XMLTagValueValidation.validateServiceType(serviceType, XMLTagValueValidation.isTagManadatory());
        XMLTagValueValidation.validateFromToDate(fromDate, XMLTagValueValidation.isTagManadatory(), "fromDate");
        XMLTagValueValidation.validateFromToDate(toDate, XMLTagValueValidation.isTagManadatory(), "toDate" );
        
    } catch (BTSLBaseException be) {
		sb.setLength(0);
		LogFactory.printError(methodName, sb.append("validateExtAllTxnCountRequest : getMessageKey = ").append(be.getMessageKey() ).append( " | getArgs = ").append(be.getArgs()), _log);
        p_requestVO.setMessageCode(be.getMessageKey());
        p_requestVO.setMessageArguments(be.getArgs());
        throw be;
    } catch (RuntimeException e) {
        // TODO Auto-generated catch block
        _log.errorTrace(methodName, e);
    } finally {
	    sb.setLength(0);
	    LogFactory.printLog(methodName, sb.append("validateExtAllTxnCountRequest : Exit"), _log);
    }
}    

/**
 * @param p_requestVO
 * @param extNwCode
 * @param msisdn
 * @param pin
 * @param loginId
 * @param password
 * @param extCode
 * @param fromDate
 * @param toDate
 * @param serviceType
 * @param language1
 * @throws BTSLBaseException
 */
public static void validateExtTotalTxnDetailRequest(RequestVO p_requestVO, String extNwCode, String msisdn, String pin, String loginId, String password, String extCode, String fromDate, String toDate,String status, String msisdn2) throws BTSLBaseException {
	final String methodName = "validateExtAllTxnCountRequest";
    sb.setLength(0);
    LogFactory.printLog(methodName, sb.append("validateExtAllTxnCountRequest : Entered"), _log);	
    try {
        XMLTagValueValidation.validateExtNetworkCode(extNwCode, XMLTagValueValidation.isTagManadatory());
        XMLTagValueValidation.validateSenderDetails(msisdn, pin, loginId, password, extCode, extNwCode, XMLTagValueValidation.isTagManadatory());
        XMLTagValueValidation.validateMsisdn2(msisdn2, XMLTagValueValidation.isTagManadatory());
        XMLTagValueValidation.validateStatus(status, XMLTagValueValidation.isTagManadatory());
        XMLTagValueValidation.validateFromToDate(fromDate, XMLTagValueValidation.isTagManadatory(), "fromDate");
        XMLTagValueValidation.validateFromToDate(toDate, XMLTagValueValidation.isTagManadatory(), "toDate" );
       
    } catch (BTSLBaseException be) {
        sb.setLength(0);
        LogFactory.printError(methodName, sb.append("validateExtAllTxnCountRequest : getMessageKey = ").append(be.getMessageKey() ).append( " | getArgs = ").append(be.getArgs()), _log);
        p_requestVO.setMessageCode(be.getMessageKey());
        p_requestVO.setMessageArguments(be.getArgs());
        throw be;
    } catch (RuntimeException e) {
        _log.errorTrace(methodName, e);
    } finally {
	    sb.setLength(0);
	    LogFactory.printLog(methodName, sb.append("validateExtTotalTxnDetailRequest : Exit"), _log);
    }
}    

public static void validateCommissionCalculatorRequest(RequestVO p_requestVO, String extNwCode, String msisdn, String pin, String loginId, String password, String extCode, String fromDate, String toDate, String msisdn2) throws BTSLBaseException {
	final String methodName = "validateExtAllTxnCountRequest";
    sb.setLength(0);
    LogFactory.printLog(methodName, sb.append("validateExtAllTxnCountRequest : Entered"), _log);	
    try {
        XMLTagValueValidation.validateExtNetworkCode(extNwCode, XMLTagValueValidation.isTagManadatory());
        XMLTagValueValidation.validateSenderDetails(msisdn, pin, loginId, password, extCode, extNwCode, XMLTagValueValidation.isTagManadatory());
        XMLTagValueValidation.validateMsisdn2(msisdn2, XMLTagValueValidation.isTagManadatory());
        XMLTagValueValidation.validateFromToDate(fromDate, XMLTagValueValidation.isTagManadatory(), "fromDate");
        XMLTagValueValidation.validateFromToDate(toDate, XMLTagValueValidation.isTagManadatory(), "toDate" );
       
    } catch (BTSLBaseException be) {
		sb.setLength(0);
		LogFactory.printError(methodName, sb.append("validateExtAllTxnCountRequest : getMessageKey = ").append(be.getMessageKey() ).append( " | getArgs = ").append(be.getArgs()), _log);
        p_requestVO.setMessageCode(be.getMessageKey());
        p_requestVO.setMessageArguments(be.getArgs());
        throw be;
    } catch (RuntimeException e) {
        // TODO Auto-generated catch block
        _log.errorTrace(methodName, e);
    } finally{
    	sb.setLength(0);
        LogFactory.printLog(methodName, sb.append("validateExtTotalTxnDetailRequest : Exit"), _log);    	
    }    
}    

}


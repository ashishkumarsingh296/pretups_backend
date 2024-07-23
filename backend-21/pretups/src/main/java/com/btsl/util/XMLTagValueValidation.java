package com.btsl.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.btsl.common.BTSLBaseException;
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
import com.btsl.pretups.master.businesslogic.LocaleMasterDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.vomsproduct.businesslogic.VoucherTypeVO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

/**
 * @description : This class will be used to validate the tag value which is
 *              coming from 3'rd part in the form of XML request.
 * @author : diwakar
 * @date : 28-FEB-2014
 * 
 */
public class XMLTagValueValidation {

    private static Log _log = LogFactory.getFactory().getInstance(XMLTagValueValidation.class.getName());
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
    
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static block", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "XMLTagValueValidation[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
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
    }

    public static boolean isTagManadatory() {
        return _tagManadatory;
    }

    public static boolean isTagOptinal() {
        return _tagOptinal;
    }

    /**
     * @descption : This method will be used to validate the MSISDN
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_msisdn
     * @param p_externalNetworkCode
     * @param p_errorCode
     * @param p_errorMsg
     * @throws BTSLBaseException
     */
    public static void validateMsisdn(String p_msisdn, String p_externalNetworkCode, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_MSISDN;
        // Common Validation
        blank(p_msisdn, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        p_msisdn = PretupsBL.getFilteredMSISDN(p_msisdn);
        numeric(p_msisdn, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, tagName, p_isManOrOpt);
        minMaxlength(p_msisdn, (int)minMsisdnLength, (int)maxMsisdnLength, PretupsErrorCodesI.EXTSYS_LENGTH_RANGE_INVALID, tagName, p_isManOrOpt);

        // Any Special type validation if any
        p_msisdn = PretupsBL.getFilteredMSISDN(p_msisdn);
        NetworkPrefixVO  networkPrefixVO=null;
        if(p_externalNetworkCode==null)
        {
        networkPrefixVO = PretupsBL.getNetworkDetails(p_msisdn, PretupsI.USER_TYPE_SENDER);
        p_externalNetworkCode= networkPrefixVO.getNetworkCode();
        }
        final String msisdnPrefix = PretupsBL.getMSISDNPrefix(p_msisdn);
        networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
        if (networkPrefixVO == null || !networkPrefixVO.getNetworkCode().equals(p_externalNetworkCode)) {
            _errorMsg[0] = tagName;
            _errorMsg[1] = p_externalNetworkCode;
            throw new BTSLBaseException("XMLTagValueValidation", "validateMsisdn", PretupsErrorCodesI.EXTSYS_NETWORK_CODE_INVALID, _errorMsg);
        }

    }

   
    /**
     * @descption : This method will be used to validate the MSISDN
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_msisdn
     * @param p_externalNetworkCode
     * @param p_errorCode
     * @param p_errorMsg
     * @throws BTSLBaseException
     */
    public static void validateMsisdn2(String p_msisdn, String p_externalNetworkCode, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_MSISDN2;
        // Common Validation
        blank(p_msisdn, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
		if(!BTSLUtil.isNullString(p_msisdn)){
	        p_msisdn = PretupsBL.getFilteredMSISDN(p_msisdn);
	        numeric(p_msisdn, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, tagName, p_isManOrOpt);
	        minMaxlength(p_msisdn, (int)minMsisdnLength, (int)maxMsisdnLength, PretupsErrorCodesI.EXTSYS_LENGTH_RANGE_INVALID, tagName, p_isManOrOpt);

	        // Any Special type validation if any
	        p_msisdn = PretupsBL.getFilteredMSISDN(p_msisdn);
	        final String msisdnPrefix = PretupsBL.getMSISDNPrefix(p_msisdn);
	        final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
	        if (networkPrefixVO == null || !networkPrefixVO.getNetworkCode().equals(p_externalNetworkCode)) {
	            _errorMsg[0] = XmlTagValueConstant.TAG_MSISDN;
	            _errorMsg[1] = p_externalNetworkCode;
	            throw new BTSLBaseException("XMLTagValueValidation", "validateMsisdn", PretupsErrorCodesI.EXTSYS_NETWORK_CODE_INVALID, _errorMsg);
	        }
		}
    }
    
    public static void validateMsisdn2(String p_msisdn, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_MSISDN2;
        // Common Validation
        blank(p_msisdn, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
		if(!BTSLUtil.isNullString(p_msisdn)){
	        p_msisdn = PretupsBL.getFilteredMSISDN(p_msisdn);
	        numeric(p_msisdn, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, tagName, p_isManOrOpt);
	        minMaxlength(p_msisdn, (int)minMsisdnLength, (int)maxMsisdnLength, PretupsErrorCodesI.EXTSYS_LENGTH_RANGE_INVALID, tagName, p_isManOrOpt);
		}
    }
    /**
     * @descption : This method will be used to validate the User MSISDN
     * @date : 22-SEP-2017
     * @param msisdn
     * @param externalNetworkCode
     * @param isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateFromUserMsisdn(String msisdn, String externalNetworkCode, boolean isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_FROM_USER_MSISDN;
        // Common Validation
        blank(msisdn, PretupsErrorCodesI.EXTSYS_BLANK, tagName, isManOrOpt);
        if(!BTSLUtil.isNullString(msisdn)){
            msisdn = PretupsBL.getFilteredMSISDN(msisdn);
            numeric(msisdn, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, tagName, isManOrOpt);
            minMaxlength(msisdn, (int)minMsisdnLength, (int)maxMsisdnLength,
            		PretupsErrorCodesI.EXTSYS_LENGTH_RANGE_INVALID, tagName, isManOrOpt);

	        // Any Special type validation if any
            msisdn = PretupsBL.getFilteredMSISDN(msisdn);
            final String msisdnPrefix = PretupsBL.getMSISDNPrefix(msisdn);
            final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
            if (networkPrefixVO == null || !networkPrefixVO.getNetworkCode().equals(externalNetworkCode)) {
                _errorMsg[0] = tagName;
                _errorMsg[1] = externalNetworkCode;
                throw new BTSLBaseException("XMLTagValueValidation", "validateFromUserMsisdn", PretupsErrorCodesI.EXTSYS_NETWORK_CODE_INVALID, _errorMsg);
	        }
		}
    }
    
    /**
     * @descption : This method will be used to validate the Parent MSISDN
     * @date : 22-SEP-2017
     * @param msisdn
     * @param externalNetworkCode
     * @param isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateparentMsisdn(String msisdn, String externalNetworkCode, boolean isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_TO_PARENT_MSISDN;
        // Common Validation
        blank(msisdn, PretupsErrorCodesI.EXTSYS_BLANK, tagName, isManOrOpt);
        if(!BTSLUtil.isNullString(msisdn)){
            msisdn = PretupsBL.getFilteredMSISDN(msisdn);
            numeric(msisdn, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, tagName, isManOrOpt);
            minMaxlength(msisdn, (int)minMsisdnLength, (int)maxMsisdnLength, 
            		PretupsErrorCodesI.EXTSYS_LENGTH_RANGE_INVALID, tagName, isManOrOpt);

            // Any Special type validation if any
            msisdn = PretupsBL.getFilteredMSISDN(msisdn);
            final String msisdnPrefix = PretupsBL.getMSISDNPrefix(msisdn);
            final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
            if (networkPrefixVO == null || !networkPrefixVO.getNetworkCode().equals(externalNetworkCode)) {
                _errorMsg[0] = tagName;
                _errorMsg[1] = externalNetworkCode;
                throw new BTSLBaseException("XMLTagValueValidation", "validateparentMsisdn", PretupsErrorCodesI.EXTSYS_NETWORK_CODE_INVALID, _errorMsg);
	        }
		}
    }

    /**
     * @descption : This method will be used to validate the date
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_msisdn
     * @param p_externalNetworkCode
     * @param p_errorCode
     * @param p_errorMsg
     * @throws BTSLBaseException
     */
    public static void validateDate(String p_date, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_DATE;
        // Common Validation
        blank(p_date, PretupsErrorCodesI.EXTSYS_REQ_DATE_BLANK, tagName, p_isManOrOpt);
        // dateFormat(p_date,PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT,tagName,p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_date
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateTxnDate(String p_date, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_EXTTXNDATE;
        // Common Validation
        blank(p_date, PretupsErrorCodesI.ERROR_EXT_DATE_BLANK, tagName, p_isManOrOpt);
        dateFormat(p_date, PretupsErrorCodesI.ERROR_EXT_DATE_NOT_PROPER, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_date
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validatePaymentDate(String p_date, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_PAYMENTDATE;
        // Common Validation
        blank(p_date, PretupsErrorCodesI.ERROR_PAYMENT_INSTRUMENT_DATE_BLANK, tagName, p_isManOrOpt);
        dateFormat(p_date, PretupsErrorCodesI.ERROR_PAYMENT_INSTRUMENT_DATE_NOT_PROPER, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_extNwCode
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateExtNetworkCode(String p_extNwCode, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_EXTNWCODE;
        // Common Validation
        blank(p_extNwCode, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_BLANK, tagName, p_isManOrOpt);
        alphanumericWithSpecialChar(p_extNwCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
        length(p_extNwCode, XmlTagValueConstant.NETWORK_CODE_LENGTH, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_pin
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validatePin(String p_pin, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_PIN;
        // Common Validation
        blank(p_pin, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        p_pin = _operatorUtil.decryptPINPassword(p_pin);
        if(!"SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
        	numeric(p_pin, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, tagName, p_isManOrOpt);
			minMaxlength(p_pin, (int)minSmsPinLength, (int)maxSmsPinLength, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);
        }else {
        	alphanumericWithSpecialChar(p_pin, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
        }
        

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_loginId
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateLoginId(String p_loginId, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_LOGIN_ID;
        // Common Validation
        blank(p_loginId, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        // alphanumeric(p_loginId,
        // PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC,tagName,p_isManOrOpt); //
        // 11-MAR-2014 as special char allowded as discussed with Divyakant Sir
        alphanumericWithSpecialChar(p_loginId, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt); // 11-MAR-2014
        // as
        // special
        // char
        // allowded
        // as
        // discussed
        // with
        // Divyakant
        // Sir
        length(p_loginId, XmlTagValueConstant.LOGIN_ID_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_loginId
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateLoginId2(String p_loginId, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_LOGIN_ID2;
        // Common Validation
        blank(p_loginId, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
		if(!BTSLUtil.isNullString(p_loginId)){
        // alphanumeric(p_loginId,
        // PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC,tagName,p_isManOrOpt); //
        // 11-MAR-2014 as special char allowded as discussed with Divyakant Sir
        alphanumericWithSpecialChar(p_loginId, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt); // 11-MAR-2014
        // as
        // special
        // char
        // allowded
        // as
        // discussed
        // with
        // Divyakant
        // Sir
        length(p_loginId, XmlTagValueConstant.LOGIN_ID_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);
		}
    }
    
    /**
     * @descption : This method will be used to validate the User Login ID
     * @date : 22-SEP-2017
     * @param loginId
     * @param isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateuserLoginId(String loginId, boolean isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_USERLOGINID;
        blank(loginId, PretupsErrorCodesI.EXTSYS_BLANK, tagName, isManOrOpt);
        if(!BTSLUtil.isNullString(loginId)){
            alphanumericWithSpecialChar(loginId, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, isManOrOpt); // 11-MAR-2014
            length(loginId, XmlTagValueConstant.LOGIN_ID_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, isManOrOpt);
		}
    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_password
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validatePassword(String p_password, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_PASSWORD;
        // Common Validation
        blank(p_password, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
         alphanumericWithSpecialChar(p_password,
         PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL,tagName,p_isManOrOpt);
         alphanumericSpecialCharAtleast(p_password,
         PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL,tagName,p_isManOrOpt);
         minMaxlength(p_password, (int)minLoginPwdLength, (int)maxLoginPwdLength,PretupsErrorCodesI.EXTSYS_LENGTH_INVALID,tagName,p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_extCode
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateExtCode(String p_extCode, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_EXTCODE;
        // Common Validation
        blank(p_extCode, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphanumericWithSpecialChar(p_extCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
        length(p_extCode, XmlTagValueConstant.EXT_CODE_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);
        valid(p_extCode, PretupsErrorCodesI.EXTSYS_EXTCODE_WRONG, tagName, p_isManOrOpt);
    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_extCode
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateExtCode2(String p_extCode, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_EXTCODE2;
        // Common Validation
        blank(p_extCode, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
		if(BTSLUtil.isNullString(p_extCode)){
        	alphanumericWithSpecialChar(p_extCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
	        length(p_extCode, XmlTagValueConstant.EXT_CODE_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);
		}
    }
    
    public static void validateParentExtCode(String p_extCode, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_PARENTEXTCODE;
        // Common Validation
        blank(p_extCode, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        if(BTSLUtil.isNullString(p_extCode)){
            alphanumericWithSpecialChar(p_extCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
            length(p_extCode, XmlTagValueConstant.EXT_CODE_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);
        }
    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_extRefNum
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateExtRefNum(String p_extRefNum, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_EXTREFNUM;
        // Common Validation
        blank(p_extRefNum, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphanumericWithSpecialChar(p_extRefNum, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
        length(p_extRefNum, XmlTagValueConstant.EXTREFNUM_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author Vipan
     * @date : 01-MAR-2014
     * @param p_extRefNum
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateVoucherCode(String p_voucherCode, boolean p_isManOrOpt,String serialNo) throws BTSLBaseException {
    	final String tagName = XmlTagValueConstant.TAG_VOUCHERCODE;
    	// Common Validation
    	blank(p_voucherCode, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
    	numeric(p_voucherCode, PretupsErrorCodesI.ERROR_VOMS_INVALID_PIN_LENGTH, tagName, p_isManOrOpt);
    	if(!BTSLUtil.isNullString(serialNo))
    	{
    		if((int)vomsDamgPinLnthAllow>p_voucherCode.length())
    		{
    			_errorMsg[0] = tagName;
    			throw new BTSLBaseException("XMLTagValueValidation", "length",  PretupsErrorCodesI.ERROR_VOMS_INVALID_PIN_LENGTH, _errorMsg);
    		}
    	}else{
    		length(p_voucherCode, (int)vomsPinMaxLength, PretupsErrorCodesI.ERROR_VOMS_INVALID_PIN_LENGTH, tagName, p_isManOrOpt);
    	}
    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_extRefNum
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateSerialNo(String p_serilNo, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_SERIALNUMBER;
        // Common Validation
        blank(p_serilNo, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        numeric(p_serilNo, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, tagName, p_isManOrOpt);
        length(p_serilNo, (int)vomsSerialNoMaxLength, PretupsErrorCodesI.ERROR_VOMS_INVALID_REQUEST_FORMAT, tagName, p_isManOrOpt);

    }
    
    public static void validateStatus(String p_serilNo, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_STATUS;
        // Common Validation
        blank(p_serilNo, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphabetic(p_serilNo, PretupsErrorCodesI.ERROR_VOMS_INVALID_REQUEST_FORMAT, tagName, p_isManOrOpt);
        length(p_serilNo, XmlTagValueConstant.STATUS_LENGTH, PretupsErrorCodesI.ERROR_VOMS_INVALID_REQUEST_FORMAT, tagName, p_isManOrOpt);

    }
    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_amount
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateAmount(String p_amount, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_AMOUNT;
        // Common Validation
        blank(p_amount, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        if(!p_amount.equalsIgnoreCase(PretupsI.VAS_BLANK_SLCTR_AMNT))
    	{
        numericOrDecimal(p_amount, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC_OR_DECIMAL, tagName, p_isManOrOpt);
    	}
        length(p_amount, XmlTagValueConstant.AMOUNT_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_language
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateLanguage1(String p_language, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_LANGUAGE1;
        // Common Validation
        blank(p_language, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        numeric(p_language, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, tagName, p_isManOrOpt);
        if(!BTSLUtil.isNullString(p_language))
        	isValidLanguageCode(p_language, PretupsErrorCodesI.INVALID_LANGUAGE_CODE, tagName);
        length(p_language, XmlTagValueConstant.LANGUAGE_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_language
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateLanguage2(String p_language, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_LANGUAGE2;
        // Common Validation
        blank(p_language, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        numeric(p_language, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, tagName, p_isManOrOpt);
        if(!BTSLUtil.isNullString(p_language))
        	isValidLanguageCode(p_language, PretupsErrorCodesI.INVALID_LANGUAGE_CODE, tagName);
        length(p_language, XmlTagValueConstant.LANGUAGE_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_language
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateSelector(String p_language, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_SELECTOR;
        // Common Validation
        blank(p_language, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        numeric(p_language, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, tagName, p_isManOrOpt);
        length(p_language, XmlTagValueConstant.LANGUAGE_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_txnId
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateTxnId(String p_txnId, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_TXNID;
        // Common Validation
        blank(p_txnId, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphanumericWithDot(p_txnId, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
        length(p_txnId, XmlTagValueConstant.TXNID_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_message
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public void validateMessage(String p_message, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_MESSAGE;
        // Common Validation
        blank(p_message, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphanumericWithSpecialChar(p_message, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
        length(p_message, XmlTagValueConstant.MESSAGE_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_type
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateType(String p_type, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_TYPE;
        // Common Validation
        blank(p_type, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphanumericWithSpecialChar(p_type, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
        length(p_type, XmlTagValueConstant.TYPE_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_productCode
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateProductCode(String p_productCode, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_PRODUCTCODE;
        // Common Validation
        blank(p_productCode, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        numeric(p_productCode, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, tagName, p_isManOrOpt);
        length(p_productCode, XmlTagValueConstant.PRODUCTCODE_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_quantity
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateQuantity(String p_quantity, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_QTY;
        // Common Validation
        blank(p_quantity, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        numericOrDecimal(p_quantity, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, tagName, p_isManOrOpt);
        length(p_quantity, XmlTagValueConstant.QTY_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_remark
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateRemark(String p_remark, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_REMARK;
        // Common Validation
        blank(p_remark, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphanumericWithSpecialChar(p_remark, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
        length(p_remark, XmlTagValueConstant.REMARK_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_categoryCode
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateCategoryCode(String p_categoryCode, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_CATCODE;
        // Common Validation
        blank(p_categoryCode, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphanumericWithSpecialChar(p_categoryCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
        length(p_categoryCode, XmlTagValueConstant.CATCODE_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_userName
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateUserName(String p_userName, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_USERNAME;
        // Common Validation
        blank(p_userName, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphanumericWithSpecialChar(p_userName, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
        length(p_userName, XmlTagValueConstant.USERNAME_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_shortName
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateShortName(String p_shortName, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_SHORTNAME;
        // Common Validation
        blank(p_shortName, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphanumericWithSpecialChar(p_shortName, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
        length(p_shortName, XmlTagValueConstant.SHORTNAME_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_userNamePrefix
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateUserNamePrefix(String p_userNamePrefix, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_USERNAMEPREFIX;
        // Common Validation
        blank(p_userNamePrefix, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphanumericWithSpecialChar(p_userNamePrefix, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
        length(p_userNamePrefix, XmlTagValueConstant.USERNAMEPREFIX_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_subscriberCode
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateSubscriberCode(String p_subscriberCode, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_SUBSCRIBERCODE;
        // Common Validation
        blank(p_subscriberCode, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphanumericWithSpecialChar(p_subscriberCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
        length(p_subscriberCode, XmlTagValueConstant.SUBSCRIBERCODE_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_contactPerson
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateContactPerson(String p_contactPerson, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_CONTACTPERSON;
        // Common Validation
        blank(p_contactPerson, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphanumericWithSpecialChar(p_contactPerson, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
        length(p_contactPerson, XmlTagValueConstant.CONTACTPERSON_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_ssn
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void  validateSsn(String p_ssn, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_SSN;
        // Common Validation
        blank(p_ssn, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphanumericWithSpecialChar(p_ssn, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
        length(p_ssn, XmlTagValueConstant.SSN_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_address
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateAddress(String p_address, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_ADDRESS1;
        // Common Validation
        blank(p_address, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphanumericWithSpecialChar(p_address, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
        length(p_address, XmlTagValueConstant.ADDRESS_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_city
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateCity(String p_city, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_CITY;
        // Common Validation
        blank(p_city, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphanumericWithSpecialChar(p_city, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
        length(p_city, XmlTagValueConstant.CITY_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_state
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateState(String p_state, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_STATE;
        // Common Validation
        blank(p_state, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphanumericWithSpecialChar(p_state, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
        length(p_state, XmlTagValueConstant.STATE_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_countryName
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateCountryName(String p_countryName, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_COUNTRY;
        // Common Validation
        blank(p_countryName, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphanumericWithSpecialChar(p_countryName, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
        length(p_countryName, XmlTagValueConstant.COUNTRY_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_emailId
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateEmailId(String p_emailId, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_EMAILID;
        // Common Validation
        blank(p_emailId, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphanumericWithSpecialChar(p_emailId, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
        length(p_emailId, XmlTagValueConstant.EMAILID_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);
        validEmailID(p_emailId,PretupsErrorCodesI.INVALID_EMAIL_MAPP,tagName,p_isManOrOpt);
        

    }

    public static void validEmailID(String p_email, String error, String tagName,boolean p_isManOrOpt) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("validateEmailID", "Entered p_email=" + p_email);
        }
        boolean matchFound = false;
        final String METHOD_NAME = "validateEmailID";
            final Pattern p = Pattern.compile("^[a-zA-Z0-9_\\.-]+@([a-zA-Z0-9]+\\.)+[a-zA-Z]{2,6}$");
            final Matcher m = p.matcher(p_email);
            matchFound = m.matches();
            if(!matchFound){
            _errorMsg[0] = tagName;
            throw new BTSLBaseException("XMLTagValueValidation", "validEmailID", error, _errorMsg);
            }
       
    }
    /**
     * @author diwakar
     * @date : 11-MAR-2014
     * @param p_extCode
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateEmpCode(String p_empCode, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_EMPCODE;
        // Common Validation
        blank(p_empCode, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphanumericWithSpecialChar(p_empCode, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
        length(p_empCode, XmlTagValueConstant.EMPCODE_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_msisdn
     * @param p_pin
     * @param p_loginId
     * @param p_password
     * @param p_extCode
     * @param p_extNwCode
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateSenderDetails(String p_msisdn, String p_pin, String p_loginId, String p_password, String p_extCode, String p_extNwCode, boolean p_isManOrOpt) throws BTSLBaseException {
        // Common Validation
        if (BTSLUtil.isNullString(p_msisdn) && BTSLUtil.isNullString(p_loginId) && BTSLUtil.isNullString(p_extCode)) {
            throw new BTSLBaseException("XMLTagValueValidation", "validateSenderDetails", PretupsErrorCodesI.EXTSYS_REQ_SENDER_DETAILS_INVALID);
        }

        if (!BTSLUtil.isNullString(p_msisdn)) {
            XMLTagValueValidation.validateMsisdn(p_msisdn, p_extNwCode, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validatePin(p_pin, p_isManOrOpt);
        }

        if (!BTSLUtil.isNullString(p_loginId)) {
            XMLTagValueValidation.validateLoginId(p_loginId, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validatePassword(p_password, XMLTagValueValidation.isTagManadatory());
        }
        if (!BTSLUtil.isNullString(p_extCode)) {
            XMLTagValueValidation.validateExtCode(p_extCode, XMLTagValueValidation.isTagManadatory());
        }

    }
    public static void validateSenderDetails(String p_msisdn, String p_pin, String p_loginId, String p_password, String p_extCode, String p_extNwCode, boolean p_isManOrOpt, RequestVO requestVO) throws BTSLBaseException {
        // Common Validation
        if (BTSLUtil.isNullString(p_msisdn) && BTSLUtil.isNullString(p_loginId) && BTSLUtil.isNullString(p_extCode)) {
            throw new BTSLBaseException("XMLTagValueValidation", "validateSenderDetails", PretupsErrorCodesI.EXTSYS_REQ_SENDER_DETAILS_INVALID);
        }

        if (!BTSLUtil.isNullString(p_msisdn)) {
            XMLTagValueValidation.validateMsisdn(p_msisdn, p_extNwCode, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validatePin(p_pin, p_isManOrOpt);
        }

        if (!BTSLUtil.isNullString(p_loginId)) {
            XMLTagValueValidation.validateLoginId(p_loginId, XMLTagValueValidation.isTagManadatory());
            if(!BTSLUtil.isStringIn(requestVO.getServiceKeyword(),Constants.getProperty("REST_SENDER_OPTIONAL_PWD_ACTIONS"))){
            XMLTagValueValidation.validatePassword(p_password, XMLTagValueValidation.isTagManadatory());
            }
        }
        if (!BTSLUtil.isNullString(p_extCode)) {
            XMLTagValueValidation.validateExtCode(p_extCode, XMLTagValueValidation.isTagManadatory());
        }

    }
    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_extRefNum
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateRefNum(String p_extRefNum, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_REFNUMBER;
        // Common Validation
        blank(p_extRefNum, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphanumericWithSpecialChar(p_extRefNum, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
        length(p_extRefNum, XmlTagValueConstant.EXTREFNUM_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_extRefNum
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateServiceType(String p_extRefNum, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_SERVICETYPE;
        // Common Validation
        blank(p_extRefNum, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphanumericWithSpecialChar(p_extRefNum, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT, tagName, p_isManOrOpt);
        length(p_extRefNum, XmlTagValueConstant.PRODUCTCODE_LENGTH, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT, tagName, p_isManOrOpt);

    }
    public static void validateSID(String p_sid, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_SID;
        // Common Validation
        blank(p_sid, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        numeric(p_sid, PretupsErrorCodesI.INVALID_SID_REG_MSG_FORMAT, tagName, p_isManOrOpt);
        length(p_sid, (int)minMsisdnLength, PretupsErrorCodesI.INVALID_SID_REG_MSG_FORMAT, tagName, p_isManOrOpt);

    }
    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_trfCategoryCode
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateTrfCategoryCode(String p_trfCategoryCode, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_TRFCATEGORY;
        // Common Validation
        blank(p_trfCategoryCode, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphabetic(p_trfCategoryCode, PretupsErrorCodesI.EXTSYS_NOT_ALFABETIC, tagName, p_isManOrOpt);
        length(p_trfCategoryCode, XmlTagValueConstant.TRFCATEGORY_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_paymentType
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validatePaymentType(String p_paymentType, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_PAYMENTTYPE;
        // Common Validation
        blank(p_paymentType, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphabetic(p_paymentType, PretupsErrorCodesI.EXTSYS_NOT_ALFABETIC, tagName, p_isManOrOpt);
        length(p_paymentType, XmlTagValueConstant.PAYMENTTYPE_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 01-MAR-2014
     * @param p_paymentNumber
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validatePaymentNumber(String p_paymentNumber, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_PAYMENTINSTNUMBER;
        // Common Validation
        blank(p_paymentNumber, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphanumericWithSpecialChar(p_paymentNumber, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
        length(p_paymentNumber, XmlTagValueConstant.PAYMENTINSTNUMBER_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 03-MAR-2014
     * @param p_subType
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateSubType(String p_subType, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_SUBSTYPE;
        // Common Validation
        blank(p_subType, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphanumericWithSpecialChar(p_subType, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
        length(p_subType, XmlTagValueConstant.SUBSTYPE_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 03-MAR-2014
     * @param p_portType
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validatePortype(String p_portType, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_PORTTYPE;
        // Common Validation
        blank(p_portType, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphanumericWithSpecialChar(p_portType, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
        length(p_portType, XmlTagValueConstant.PORTTYPE_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

    }

    /**
     * @author diwakar
     * @date : 11-MAR-2014
     * @param p_msisdn
     * @param p_pin
     * @param p_loginId
     * @param p_password
     * @param p_extCode
     * @param p_empCode
     * @param p_extNwCode
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateSenderDetails(String p_msisdn, String p_pin, String p_loginId, String p_password, String p_extCode, String p_empCode, String p_extNwCode, boolean p_isManOrOpt) throws BTSLBaseException {
        // Common Validation
        if (BTSLUtil.isNullString(p_msisdn) && BTSLUtil.isNullString(p_loginId) && BTSLUtil.isNullString(p_extCode) && BTSLUtil.isNullString(p_empCode)) {
            throw new BTSLBaseException("XMLTagValueValidation", "validateSenderDetails", PretupsErrorCodesI.EXTSYS_REQ_SENDER_DETAILS_INVALID);
        }

        if (!BTSLUtil.isNullString(p_msisdn)) {
            XMLTagValueValidation.validateMsisdn(p_msisdn, p_extNwCode, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validatePin(p_pin, XMLTagValueValidation.isTagManadatory());
        }

        if (!BTSLUtil.isNullString(p_loginId)) {
            XMLTagValueValidation.validateLoginId(p_loginId, XMLTagValueValidation.isTagManadatory());
            XMLTagValueValidation.validatePassword(p_password, XMLTagValueValidation.isTagManadatory());
        }
        if (!BTSLUtil.isNullString(p_extCode)) {
            XMLTagValueValidation.validateExtCode(p_extCode, XMLTagValueValidation.isTagManadatory());
        }

        if (!BTSLUtil.isNullString(p_empCode)) {
            XMLTagValueValidation.validateEmpCode(p_empCode, XMLTagValueValidation.isTagManadatory());
        }

    }

    /**
     * @author diwakar
     * @date : 26-SEP-2014
     * @param p_msisdn
     * @param p_pin
     * @param p_loginId
     * @param p_password
     * @param p_extCode
     * @param p_extNwCode
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateByPassPinSenderDetails(String p_msisdn, String p_pin, String p_loginId, String p_password, String p_extCode, String p_extNwCode, boolean p_isManOrOpt) throws BTSLBaseException {
        // Common Validation
        if (BTSLUtil.isNullString(p_msisdn) && BTSLUtil.isNullString(p_loginId) && BTSLUtil.isNullString(p_extCode)) {
            throw new BTSLBaseException("XMLTagValueValidation", "validateByPassPinSenderDetails", PretupsErrorCodesI.EXTSYS_REQ_SENDER_DETAILS_INVALID);
        }

        if (!BTSLUtil.isNullString(p_msisdn)) {
            XMLTagValueValidation.validateMsisdn(p_msisdn, p_extNwCode, XMLTagValueValidation.isTagManadatory());
            // Commented as discussed with Gopal
            // XMLTagValueValidation.validatePin(
            // p_pin,XMLTagValueValidation.isTagOptinal());

        }

        if (!BTSLUtil.isNullString(p_loginId)) {
            XMLTagValueValidation.validateLoginId(p_loginId, XMLTagValueValidation.isTagManadatory());
            // Commented as discussed with Gopal
            // XMLTagValueValidation.validatePassword( p_password,
            // XMLTagValueValidation.isTagManadatory());
        }
        if (!BTSLUtil.isNullString(p_extCode)) {
            XMLTagValueValidation.validateExtCode(p_extCode, XMLTagValueValidation.isTagManadatory());
        }

    }

    /**
     * @description : This method will be used to validate whether value is
     *              blank or not.
     * @param p_str
     * @param p_errorKey
     * @throws BTSLBaseException
     */
    static void blank(String p_str, String p_errorKey, String tagName, boolean p_isManOrOpt) throws BTSLBaseException {
        if (p_isManOrOpt) {
            if (BTSLUtil.isNullString(p_str)) {
                _errorMsg[0] = tagName;
                throw new BTSLBaseException("XMLTagValueValidation", "blank", p_errorKey, _errorMsg);
            }
        }
    }

    /**
     * @description : This method will be used to validate whether value is
     *              numeric or not.
     * @param p_str
     * @param p_errorKey
     * @throws BTSLBaseException
     */
    static void numeric(String p_str, String p_errorKey, String tagName, boolean p_isManOrOpt) throws BTSLBaseException {
        if (p_isManOrOpt || !BTSLUtil.isNullString(p_str)) {
            if (!p_str.matches(RegularExpression.NUMERIC)) {
                _errorMsg[0] = tagName;
                throw new BTSLBaseException("XMLTagValueValidation", "numeric", p_errorKey, _errorMsg);
            }
        }
    }

    /**
     * @description : This method will be used to validate whether length of
     *              value lies between min & max length specified by users.
     * @param p_str
     * @param p_minLength
     * @param p_maxLength
     * @param p_errorKey
     * @throws BTSLBaseException
     */
    static void minMaxlength(String p_str, int p_minLength, int p_maxLength, String p_errorKey, String tagName, boolean p_isManOrOpt) throws BTSLBaseException {
        if (p_isManOrOpt || !BTSLUtil.isNullString(p_str)) {
            final int length = p_str.length();
            if (length > p_maxLength || length < p_minLength) {
                _errorMsg[0] = tagName;
                _errorMsg[1] = String.valueOf(p_minLength);
                _errorMsg[2] = String.valueOf(p_maxLength);
                throw new BTSLBaseException("XMLTagValueValidation", "minMaxlength", p_errorKey, _errorMsg);
            }
        }
    }

    /**
     * @description : This method will be used to validate dateformat as per
     *              configured into the system.
     * @param p_date
     * @param p_errorKey
     * @throws BTSLBaseException
     * @throws ParseException
     */
    static void dateFormat(String p_date, String p_errorKey, String tagName, boolean p_isManOrOpt) throws BTSLBaseException {
        final String METHOD_NAME = "dateFormat";
        try {
            if (p_isManOrOpt || !BTSLUtil.isNullString(p_date)) {
                //BTSLUtil.getDateFromDateString(p_date, externalDateFormat);
            	BTSLDateUtil.getSystemLocaleDateInFormat(p_date, PretupsI.DATE_FORMAT);
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _errorMsg[0] = tagName;
            throw new BTSLBaseException("XMLTagValueValidation", "dateFormat", p_errorKey, _errorMsg);
        }

    }

    /**
     * 
     * @param p_extNwCode
     * @param p_errorKey
     * @throws BTSLBaseException
     */
    static void length(String p_str, int p_maxLength, String p_errorKey, String tagName, boolean p_isManOrOpt) throws BTSLBaseException {
        if (p_isManOrOpt || !BTSLUtil.isNullString(p_str)) {
            final int length = p_str.length();
            if (length <= 0 || length > p_maxLength) {
                _errorMsg[0] = p_str;
                throw new BTSLBaseException("XMLTagValueValidation", "length", p_errorKey, _errorMsg);
            }
        }
    }

    static void alphanumeric(String p_str, String p_errorKey, String tagName, boolean p_isManOrOpt) throws BTSLBaseException {
        if (p_isManOrOpt || !BTSLUtil.isNullString(p_str)) {
            if (!p_str.matches(RegularExpression.ALPHA_NUMERIC)) {
                _errorMsg[0] = tagName;
                throw new BTSLBaseException("XMLTagValueValidation", "alphanumeric", p_errorKey, _errorMsg);
            }
        }
    }

    static void alphabetic(String p_str, String p_errorKey, String tagName, boolean p_isManOrOpt) throws BTSLBaseException {
        if (p_isManOrOpt || !BTSLUtil.isNullString(p_str)) {
            if (!p_str.matches(RegularExpression.ALPHABET)) {
                _errorMsg[0] = tagName;
                throw new BTSLBaseException("XMLTagValueValidation", "alphabetic", p_errorKey, _errorMsg);
            }
        }
    }

    static void alphanumericWithSpecialChar(String p_str, String p_errorKey, String tagName, boolean p_isManOrOpt) throws BTSLBaseException {
        if (p_isManOrOpt || !BTSLUtil.isNullString(p_str)) {
            if (!p_str.matches(RegularExpression.ALPHA_NUMERIC_SPECIAL_CHAR)) {
                _errorMsg[0] = tagName;
                throw new BTSLBaseException("XMLTagValueValidation", "alphanumericWithSpecialChar", p_errorKey, _errorMsg);
            }
        }
    }

    static void alphanumericWithDot(String p_str, String p_errorKey, String tagName, boolean p_isManOrOpt) throws BTSLBaseException {
        if (p_isManOrOpt || !BTSLUtil.isNullString(p_str)) {
            if (!p_str.matches(RegularExpression.ALPHA_NUMERIC_DOT_CHAR)) {
                _errorMsg[0] = tagName;
                throw new BTSLBaseException("XMLTagValueValidation", "alphanumericWithDot", p_errorKey, _errorMsg);
            }
        }
    }

    static void alphanumericSpecialCharAtleast(String p_str, String p_errorKey, String tagName, boolean p_isManOrOpt) throws BTSLBaseException {
        if (p_isManOrOpt || !BTSLUtil.isNullString(p_str)) {
            if (!p_str.matches(RegularExpression.ALPHA_NUMERIC_SPECIAL_CHAR_ATLEAST)) {
                // 2n'd level check on 31-MAR-2014
                final String METHOD_NAME = "alphanumericSpecialCharAtleast";
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

    /**
     * @description : This method will be used to validate whether value is
     *              decimal,numeric or not.
     * @param p_str
     * @param p_errorKey
     * @throws BTSLBaseException
     */
    static void numericOrDecimal(String p_str, String p_errorKey, String tagName, boolean p_isManOrOpt) throws BTSLBaseException {
        if (p_isManOrOpt || !BTSLUtil.isNullString(p_str)) {
            if (!p_str.matches(RegularExpression.NUMERIC_OR_DECIMAL)) {
                _errorMsg[0] = tagName;
                throw new BTSLBaseException("XMLTagValueValidation", "numericaOrDecimal", p_errorKey, _errorMsg);
            }
        }
    }

    /**
     * @author birendra.mishra
     *         This method to validate BALANCETYPE tag and it's content.
     * @param p_balanceType
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateBalanceType(String p_balanceType, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_BALANCETYPE;
        // Common Validation
        blank(p_balanceType, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphabetic(p_balanceType, PretupsErrorCodesI.EXTSYS_NOT_ALFABETIC, tagName, p_isManOrOpt);
        length(p_balanceType, XmlTagValueConstant.BALANCETYPE_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);
    }

	public static void validateIRISDate(String p_date,boolean p_isManOrOpt) throws BTSLBaseException
	{
		String tagName = XmlTagValueConstant.TAG_DATE;
		blank(p_date,PretupsErrorCodesI.EXTSYS_REQ_DATE_BLANK,tagName,p_isManOrOpt);
		dateFormatIris(p_date,PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT,tagName,p_isManOrOpt);
	}	
	static void dateFormatIris(String p_date, String p_errorKey,String tagName,boolean p_isManOrOpt) throws BTSLBaseException  {
	    final String METHOD_NAME = "dateFormat";
		try
		{	if(p_isManOrOpt || !BTSLUtil.isNullString(p_date))
				BTSLUtil.getDateTimeFromDateTimeString(p_date, irisDateFormat);
		}
		catch (Exception e)
		{
			_log.errorTrace(METHOD_NAME,e);
			_errorMsg[0] = tagName;
			throw new BTSLBaseException("XMLTagValueValidation","dateFormat",p_errorKey,_errorMsg);
		}
	}
	public static  void validateIRISAmount(String p_amount,boolean p_isManOrOpt) throws BTSLBaseException
	{
		String tagName = XmlTagValueConstant.TAG_AMOUNT;
		blank(p_amount,PretupsErrorCodesI.EXTSYS_BLANK,tagName,p_isManOrOpt);
		numericOrUptoTwoDecimalPlace(p_amount, PretupsErrorCodesI.ERROR_INVALID_AMOUNT,tagName,p_isManOrOpt);
		length(p_amount,XmlTagValueConstant.AMOUNT_LENGTH,PretupsErrorCodesI.EXTSYS_LENGTH_INVALID,tagName,p_isManOrOpt);
	}
	static void numericOrUptoTwoDecimalPlace(String p_str, String p_errorKey,String tagName,boolean p_isManOrOpt) throws BTSLBaseException {
		if(p_isManOrOpt || !BTSLUtil.isNullString(p_str)){
			if (!p_str.matches(RegularExpression.NUMERIC_OR_UPTO_TWO_DECIMAL)){
				_errorMsg[0] = tagName;
				throw new BTSLBaseException("XMLTagValueValidation","numericOrUptoTwoDecimalPlace",p_errorKey,_errorMsg);
			}
		}
	}
	public static  void validateSelectorForPVAS(String p_language,boolean p_isManOrOpt) throws BTSLBaseException
	{
		String tagName = XmlTagValueConstant.TAG_SELECTOR;
		blank(p_language,PretupsErrorCodesI.EXTSYS_BLANK,tagName,p_isManOrOpt);
		alphanumeric(p_language, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC,tagName,p_isManOrOpt);
		length(p_language,XmlTagValueConstant.SELECTOR_LENGTH_IRIS,PretupsErrorCodesI.EXTSYS_LENGTH_INVALID,tagName,p_isManOrOpt);
	}
	public static  void validateIRISBonus(String p_amount,boolean p_isManOrOpt) throws BTSLBaseException
	{
		String tagName = XmlTagValueConstant.TAG_BONUS;
		blank(p_amount,PretupsErrorCodesI.EXTSYS_BLANK,tagName,p_isManOrOpt);
		numericOrUptoTwoDecimalPlace(p_amount, PretupsErrorCodesI.ERROR_INVALID_BONUSAMOUNT,tagName,p_isManOrOpt);
		length(p_amount,XmlTagValueConstant.AMOUNT_LENGTH,PretupsErrorCodesI.EXTSYS_LENGTH_INVALID,tagName,p_isManOrOpt);
	}
    
    /**
     * XMLTagValueValidation.java
     * @param p_remark
     * @param p_isManOrOpt
     * @throws BTSLBaseException
     * void
     * akanksha.gupta
     * 09-Aug-2016 5:59:02 pm
     */
    public static void validateCurrencyRecord(String p_remark, boolean p_isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_REMARK;
        // Common Validation
        blank(p_remark, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
        alphanumericWithSpecialChar(p_remark, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
        

    }
    // added for channel user transfer
    public static  void validateSenderDtl(String p_loginId,String p_password,String empcode,boolean p_isManOrOpt) throws BTSLBaseException
	{
		//Common Validation
		if((BTSLUtil.isNullString(p_loginId)||BTSLUtil.isNullString(p_password)) && BTSLUtil.isNullString(empcode)){ 
			throw new BTSLBaseException("XMLTagValueValidation","validateSenderDetails",PretupsErrorCodesI.USER_REQ_SENDER_DETAILS_INVALID);
		}
				
		if(!BTSLUtil.isNullString( p_loginId)){
			XMLTagValueValidation.validateLoginId( p_loginId,XMLTagValueValidation.isTagManadatory());
			XMLTagValueValidation.validatePassword( p_password, XMLTagValueValidation.isTagManadatory());
		}
		if(!BTSLUtil.isNullString( empcode)){
			XMLTagValueValidation.validateEmpCode( empcode, XMLTagValueValidation.isTagManadatory());
		}	
		
	}
    /**
     *
     * @date : 28-AUG-2017
     * @param action
     * @param isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateSuspendResumeAction(String action, boolean isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_ACTION;
        // Common Validation
        blank(action, PretupsErrorCodesI.EXTSYS_BLANK, tagName, isManOrOpt);
        length(action, XmlTagValueConstant.ACTION_LENGTH, PretupsErrorCodesI.EXTSYS_REQ_ACTION_LENGTH_EXCEEDS, tagName, isManOrOpt);
        validateAction(action, PretupsErrorCodesI.EXTSYS_REQ_ACTION_INVALID_VALUE, tagName);        
        
    }
    
    /**
    *
    * @date : 28-AUG-2017
    * @param action
    * @param errorKey
    * @param tagName
    * @throws BTSLBaseException
    */
    public static void validateAction(String action, String errorKey, String tagName) throws BTSLBaseException {
        if(!(action.equalsIgnoreCase(PretupsI.USER_STATUS_SUSPEND) || action.equalsIgnoreCase(PretupsI.USER_STATUS_ACTIVE_R)
    			|| action.equalsIgnoreCase(PretupsI.USER_STATUS_DELETED))){
            _errorMsg[0] = tagName;
            throw new BTSLBaseException("XMLTagValueValidation", "validataAction", errorKey, _errorMsg);
        }    	
    }
    /**
     * Method validateGeographyType
     * validateGeographyType Method , validates the geography type in the request
     * 
     * @param geographyType
     * @param isManOrOpt
     */
    public static void validateGeographyType(String geographyType, boolean isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_GEOGRAPHYTYPE;
        blank(geographyType, PretupsErrorCodesI.EXTSYS_BLANK, tagName, isManOrOpt);
    }
    
    /**
     * Method validateGeographyCode
     * validateGeographyCode Method , validates the geography code in the request
     * 
     * @param geographyCode
     * @param isManOrOpt
     */
    public static void validateGeographyCode(String geographyCode, boolean isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_GEOGRAPHYCODE;
        blank(geographyCode, PretupsErrorCodesI.EXTSYS_BLANK, tagName, isManOrOpt);
    }
    
    /**
     * Method validateParentGeographyCode
     * validateParentGeographyCode Method , validates the parent geography code in the request
     * 
     * @param parentGeographyCode
     * @param isManOrOpt
     */
    public static void validateParentGeographyCode(String parentGeographyCode, boolean isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_PARENTGEOGRAPHYCODE;
        blank(parentGeographyCode, PretupsErrorCodesI.EXTSYS_BLANK, tagName, isManOrOpt);
    }
    
    /**
     * Method validateGeographyName
     * validateGeographyName Method , validates the geography name in the request
     * 
     * @param geographyName
     * @param isManOrOpt
     */
    public static void validateGeographyName(String geographyName, boolean isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_GEOGRAPHYNAME;
        blank(geographyName, PretupsErrorCodesI.EXTSYS_BLANK, tagName, isManOrOpt);
    }
    
    /**
     * Method validateGeographyShortName
     * validateGeographyShortName Method , validates the geography short name in the request
     * 
     * @param geographyShortName
     * @param isManOrOpt
     */
    public static void validateGeographyShortName(String geographyShortName, boolean isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_GEOGRAPHYSHORTNAME;
        blank(geographyShortName, PretupsErrorCodesI.EXTSYS_BLANK, tagName, isManOrOpt);
    }
    
    /**
     * Method validateGeographyDescription
     * validateGeographyDescription Method , validates the geography description in the request
     * 
     * @param geographyDescription
     * @param isManOrOpt
     */
    public static void validateGeographyDescription(String geographyDescription, boolean isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_GEOGRAPHYDESCRIPTION;
        blank(geographyDescription, PretupsErrorCodesI.EXTSYS_BLANK, tagName, isManOrOpt);
    }
    
    /**
     * Method validateGeographyDefaultFlag
     * validateGeographyDefaultFlag Method , validates the geography isdefault flag in the request
     * 
     * @param isGeographyDefault
     * @param isManOrOpt
     */
    public static void validateGeographyDefaultFlag(String isGeographyDefault, boolean isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_GEOGRAPHYDEFAULTFLAG;
        blank(isGeographyDefault, PretupsErrorCodesI.EXTSYS_BLANK, tagName, isManOrOpt);
    }
    
    /**
     * Method validateGeographyAction
     * validateGeographyAction Method , validates the geography action in the request
     * 
     * @param geographyAction
     * @param isManOrOpt
     */
    public static void validateGeographyAction(String geographyAction, boolean isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_GEOGRAPHYACTION;
        blank(geographyAction, PretupsErrorCodesI.EXTSYS_BLANK, tagName, isManOrOpt);
    }
    
    /**
     * Method validateDesignation
     * validateDesignation Method , validates the designation in the request
     * 
     * @param designation
     * @param isManOrOpt
     */
    public static void validateDesignation(String designation, boolean isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_DESIGNATION;
        blank(designation, PretupsErrorCodesI.EXTSYS_BLANK, tagName, isManOrOpt);
    }
    
    /**
     * Method validateInSuspend
     * validateInSuspend Method , validates the inSuspend in the request
     * 
     * @param inSuspend
     * @param isManOrOpt
     */
    public static void validateInSuspend(String inSuspend, boolean isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_INSUSPEND;
        blank(inSuspend, PretupsErrorCodesI.EXTSYS_BLANK, tagName, isManOrOpt);
    }
    
    /**
     * Method validateOutSuspend
     * validateOutSuspend Method , validates the outSuspend in the request
     * 
     * @param outSuspend
     * @param isManOrOpt
     */
    public static void validateOutSuspend(String outSuspend, boolean isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_OUTSUSPEND;
        blank(outSuspend, PretupsErrorCodesI.EXTSYS_BLANK, tagName, isManOrOpt);
    }
    
    /**
     * Method validateCompany
     * validateCompany Method , validates the company in the request
     * 
     * @param company
     * @param isManOrOpt
     */
    public static void validateCompany(String company, boolean isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_COMPANY;
        blank(company, PretupsErrorCodesI.EXTSYS_BLANK, tagName, isManOrOpt);
    }
    
    /**
     * Method validateFax
     * validateFax Method , validates the fax in the request
     * 
     * @param fax
     * @param isManOrOpt
     */
    public static void validateFax(String fax, boolean isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_FAX;
        blank(fax, PretupsErrorCodesI.EXTSYS_BLANK, tagName, isManOrOpt);
    }
    
    /**
     * @descption : This method will be used to validate the User MSISDN
     * @date : 22-SEP-2017
     * @param msisdn
     * @param externalNetworkCode
     * @param isManOrOpt
     * @throws BTSLBaseException
     */
    public static void validateUserMsisdn(String msisdn, String externalNetworkCode, boolean isManOrOpt) throws BTSLBaseException {
        final String tagName = XmlTagValueConstant.TAG_USERMSISDN;
        // Common Validation
        blank(msisdn, PretupsErrorCodesI.EXTSYS_BLANK, tagName, isManOrOpt);
        msisdn = PretupsBL.getFilteredMSISDN(msisdn);
        numeric(msisdn, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, tagName, isManOrOpt);
        minMaxlength(msisdn, (int)minMsisdnLength, (int)maxMsisdnLength, 
        		PretupsErrorCodesI.EXTSYS_LENGTH_RANGE_INVALID, tagName, isManOrOpt);

        // Any Special type validation if any
        msisdn = PretupsBL.getFilteredMSISDN(msisdn);
        if(!BTSLUtil.isNullString(msisdn)){
            final String msisdnPrefix = PretupsBL.getMSISDNPrefix(msisdn);
            final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
            if (networkPrefixVO == null || !networkPrefixVO.getNetworkCode().equals(externalNetworkCode)) {
                _errorMsg[0] = tagName;
                _errorMsg[1] = externalNetworkCode;
                throw new BTSLBaseException("XMLTagValueValidation", "validateUserMsisdn", PretupsErrorCodesI.EXTSYS_NETWORK_CODE_INVALID, _errorMsg);
            }
        }
    }
    
    /**
     * Method validateMobileNumber
     * validateMobileNumber Method , validates the mobilenumber in the request
     * 
     * @param mobileNumber
     * @param isManOrOpt
     */
    public static void validateMobileNumber(String mobileNumber, boolean isManOrOpt) throws BTSLBaseException {        
        if (BTSLUtil.isNullString(mobileNumber) || mobileNumber.length() > 15) {
            throw new BTSLBaseException("XMLTagValueValidation", "validateMobileNumber", PretupsErrorCodesI.EXTSYS_REQ_MOBILENUMBER_LENGTH_EXCEEDS);
        } else {
            if (!BTSLUtil.isValidNumber(mobileNumber)) {
                final String[] errorMsg = new String[2];
                errorMsg[0] = mobileNumber.trim();
                throw new BTSLBaseException("XMLTagValueValidation", "validateMobileNumber", PretupsErrorCodesI.EXTSYS_REQ_MOBILENUMBER_NON_NUMERIC, errorMsg);
            }
        }
    }
    
	/**
	 * @author ankit
	 * @date : 29-AUG-2016
	 * @param p_language
	 * @param p_isManOrOpt
	 * @throws BTSLBaseException
	 */
	public static  void validateExternalData(String info1,String info2,String info3,String info4,String info5,String info6,String info7,String info8,String info9,String info10,boolean p_isManOrOpt) throws BTSLBaseException
	{
		String tagName = XmlTagValueConstant.TAG_EXTERNALDATA1;
		length(info1,XmlTagValueConstant.EXTERNALDATA_LENGTH,PretupsErrorCodesI.EXTSYS_LENGTH_INVALID,tagName,p_isManOrOpt);
		
		tagName = XmlTagValueConstant.TAG_EXTERNALDATA2;
		length(info2,XmlTagValueConstant.EXTERNALDATA_LENGTH,PretupsErrorCodesI.EXTSYS_LENGTH_INVALID,tagName,p_isManOrOpt);
		
		tagName = XmlTagValueConstant.TAG_EXTERNALDATA3;
		length(info3,XmlTagValueConstant.EXTERNALDATA_LENGTH,PretupsErrorCodesI.EXTSYS_LENGTH_INVALID,tagName,p_isManOrOpt);
		
		tagName = XmlTagValueConstant.TAG_EXTERNALDATA4;
		length(info4,XmlTagValueConstant.EXTERNALDATA_LENGTH,PretupsErrorCodesI.EXTSYS_LENGTH_INVALID,tagName,p_isManOrOpt);
		
		tagName = XmlTagValueConstant.TAG_EXTERNALDATA5;
		length(info5,XmlTagValueConstant.EXTERNALDATA_LENGTH,PretupsErrorCodesI.EXTSYS_LENGTH_INVALID,tagName,p_isManOrOpt);
		
		tagName = XmlTagValueConstant.TAG_EXTERNALDATA6;
		length(info6,XmlTagValueConstant.EXTERNALDATA_LENGTH,PretupsErrorCodesI.EXTSYS_LENGTH_INVALID,tagName,p_isManOrOpt);
		
		tagName = XmlTagValueConstant.TAG_EXTERNALDATA7;
		length(info7,XmlTagValueConstant.EXTERNALDATA_LENGTH,PretupsErrorCodesI.EXTSYS_LENGTH_INVALID,tagName,p_isManOrOpt);
		
		tagName = XmlTagValueConstant.TAG_EXTERNALDATA8;
		length(info8,XmlTagValueConstant.EXTERNALDATA_LENGTH,PretupsErrorCodesI.EXTSYS_LENGTH_INVALID,tagName,p_isManOrOpt);
		
		tagName = XmlTagValueConstant.TAG_EXTERNALDATA9;
		length(info9,XmlTagValueConstant.EXTERNALDATA_LENGTH,PretupsErrorCodesI.EXTSYS_LENGTH_INVALID,tagName,p_isManOrOpt);
		
		tagName = XmlTagValueConstant.TAG_EXTERNALDATA10;
		length(info10,XmlTagValueConstant.EXTERNALDATA_LENGTH,PretupsErrorCodesI.EXTSYS_LENGTH_INVALID,tagName,p_isManOrOpt);
	}
	public static  void validateSwitchIDAndCellID(String switchid,String cellid,boolean p_isManOrOpt) throws BTSLBaseException
	{
		String tagName = XmlTagValueConstant.TAG_SWITCHID;
		length(switchid,XmlTagValueConstant.LOCATION_LENGTH,PretupsErrorCodesI.EXTSYS_LENGTH_INVALID,tagName,p_isManOrOpt);
		
		tagName = XmlTagValueConstant.TAG_CELLID;
		length(cellid,XmlTagValueConstant.LOCATION_LENGTH,PretupsErrorCodesI.EXTSYS_LENGTH_INVALID,tagName,p_isManOrOpt);
	}
	
	
	static void valid(String p_extCode, String p_errorKey, String tagName, boolean p_isManOrOpt) throws BTSLBaseException {
        //DAO for checking if ext code is wrong
		MComConnectionI mcomCon = new MComConnection();
		Connection con = null;
		boolean isExtCodeExist = false;
		try {
			con = mcomCon.getConnection();
			ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
			isExtCodeExist = channelUserWebDAO.isExternalCodeExist(con,p_extCode,"");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (mcomCon != null) {
				mcomCon.close("XMLTagValueValidation#valid");
				mcomCon = null;
			}
		}
		if(!isExtCodeExist)
			throw new BTSLBaseException("XMLTagValueValidation", "extcode", p_errorKey, new String[] {tagName});
    }
	
	
	public static void validateVoucherType(String p_voucherType, boolean p_isManOrOpt) throws BTSLBaseException {
    	final String tagName = XmlTagValueConstant.TAG_VOUCHERTYPE;
    	// Common Validation
    	blank(p_voucherType, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
    }
	
	public static void validateExpiryDate(String p_date,boolean p_isManOrOpt) throws BTSLBaseException, ParseException {
        final String tagName = XmlTagValueConstant.TAG_DATE;
        Date currDate = new Date();
        // Common Validation
        blank(p_date, PretupsErrorCodesI.EXTSYS_REQ_DATE_BLANK, tagName, p_isManOrOpt);
        dateFormat(p_date,PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT,tagName,p_isManOrOpt);
       dateComparison(p_date,PretupsErrorCodesI.EXTSYS_REQ_DATE_BEFORE,tagName,p_isManOrOpt);
    }

	 public static void dateComparison(String p_date, String p_errorKey, String tagName, boolean p_isManOrOpt) throws BTSLBaseException {
		 final String METHOD_NAME = "dateComparison";
	        try {
	        	 if (p_isManOrOpt || !BTSLUtil.isNullString(p_date)) {
	        		 Date date = new Date();
		            	if(!BTSLUtil.getDateFromDateString(p_date).after(BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(date)))){
		    	            _errorMsg[0] = tagName;
		    	            throw new BTSLBaseException("XMLTagValueValidation", "dateComparison", p_errorKey, _errorMsg);
		            			}
		            }
			} catch (Exception e) {
				_log.errorTrace(METHOD_NAME, e);
	            _errorMsg[0] = tagName;
	            throw new BTSLBaseException("XMLTagValueValidation", "dateComparison", p_errorKey, _errorMsg);
			}
		 }
	 public static void currentDateCheck(String p_date,String currentDate, String p_errorKey, String tagName, boolean p_isManOrOpt) throws BTSLBaseException {
	        final String METHOD_NAME = "currentDateCheck";
	        try {
	            if (p_isManOrOpt || !BTSLUtil.isNullString(p_date)) {
	            	if(!BTSLUtil.getDateFromDateString(p_date).after(BTSLUtil.getDateFromDateString(currentDate))){
	    	            _errorMsg[0] = tagName;
	    	            throw new BTSLBaseException("XMLTagValueValidation", "currentDateCheck", p_errorKey, _errorMsg);
	            			}
	            }
	        } catch (Exception e) {
	            _log.errorTrace(METHOD_NAME, e);
	            _errorMsg[0] = tagName;
	            throw new BTSLBaseException("XMLTagValueValidation", "currentDateCheck", p_errorKey, _errorMsg);
	        }
	 }
	 public static void validateVoucherDate(String p_date, boolean p_isManOrOpt) throws BTSLBaseException {
	        final String tagName = XmlTagValueConstant.TAG_EXTTXNDATE;
	        // Common Validation
	        blank(p_date, PretupsErrorCodesI.ERROR_EXT_DATE_BLANK, tagName, p_isManOrOpt);
	        datetimestampFormat(p_date, PretupsErrorCodesI.ERROR_EXT_DATE_NOT_PROPER, tagName, p_isManOrOpt);

	    }
	 /**
	     * @description : This method will be used to validate timestampformat as per
	     *              configured into the system.
	     * @param p_date
	     * @param p_errorKey
	     * @throws BTSLBaseException
	     * @throws ParseException
	     */
	    static void datetimestampFormat(String p_date, String p_errorKey, String tagName, boolean p_isManOrOpt) throws BTSLBaseException {
	        final String METHOD_NAME = "datetimestampFormat";
	        try {
	            if (p_isManOrOpt || !BTSLUtil.isNullString(p_date)) {
	                //BTSLUtil.getDateFromDateString(p_date, externalDateFormat);
	            	//BTSLDateUtil.getSystemLocaleDate(p_date, PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
	            	BTSLUtil.getDateStringFromDate(BTSLUtil.getDateFromDateString(p_date, PretupsI.TIMESTAMP_DDMMYYYYHHMMSS));
	            }
	        } catch (Exception e) {
	            _log.errorTrace(METHOD_NAME, e);
	            _errorMsg[0] = tagName;
	            throw new BTSLBaseException("XMLTagValueValidation", "dateFormat", p_errorKey, _errorMsg);
	        }

	    }
	    
	    /**
	     * @param p_isManOrOpt
	     * @throws BTSLBaseException
	     */
	    public static void validateVoucherPinSerial(String p_voucherCode, boolean p_isManOrOpt,String serialNo) throws BTSLBaseException {
	    	final String tagName = XmlTagValueConstant.TAG_VOUCHERPIN;
	    	// Common Validation
	    	  if (BTSLUtil.isNullString(p_voucherCode) && BTSLUtil.isNullString(serialNo)) {
	                _errorMsg[0] = "Both_PIN_and_SNO";
	                throw new BTSLBaseException("XMLTagValueValidation", "blank", PretupsErrorCodesI.EXTSYS_BLANK, _errorMsg);
	           }
	    	  if (!BTSLUtil.isNullString(p_voucherCode)){
	    			  numeric(p_voucherCode, PretupsErrorCodesI.ERROR_VOMS_INVALID_PIN_LENGTH, tagName, p_isManOrOpt);
	    	          length(p_voucherCode, (int)vomsPinMaxLength, PretupsErrorCodesI.ERROR_VOMS_INVALID_PIN_LENGTH, tagName, p_isManOrOpt);
	    	  }
	    	  if (!BTSLUtil.isNullString(serialNo)){
	    		  numeric(serialNo, PretupsErrorCodesI.ERROR_VOMS_INVALID_REQUEST_FORMAT, tagName, p_isManOrOpt);
		          length(serialNo, (int)vomsSerialNoMaxLength, PretupsErrorCodesI.ERROR_VOMS_INVALID_REQUEST_FORMAT, tagName, p_isManOrOpt);
	    	  }
	    	
	    }
	    
	    public static void validateSubsId(String p_msisdn, String p_externalNetworkCode, boolean p_isManOrOpt) throws BTSLBaseException {
	        final String tagName = XmlTagValueConstant.TAG_SUBID;
	        // Common Validation
	        blank(p_msisdn, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
	        p_msisdn = PretupsBL.getFilteredMSISDN(p_msisdn);
	        numeric(p_msisdn, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, tagName, p_isManOrOpt);
	        minMaxlength(p_msisdn, (int)minMsisdnLength, (int)maxMsisdnLength, PretupsErrorCodesI.EXTSYS_LENGTH_RANGE_INVALID, tagName, p_isManOrOpt);

	        // Any Special type validation if any
	        p_msisdn = PretupsBL.getFilteredMSISDN(p_msisdn);
	        final String msisdnPrefix = PretupsBL.getMSISDNPrefix(p_msisdn);
	        final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
	        if (networkPrefixVO == null || !networkPrefixVO.getNetworkCode().equals(p_externalNetworkCode)) {
	            _errorMsg[0] = tagName;
	            _errorMsg[1] = p_externalNetworkCode;
	            throw new BTSLBaseException("XMLTagValueValidation", "validateMsisdn", PretupsErrorCodesI.EXTSYS_NETWORK_CODE_INVALID, _errorMsg);
	        }

	    }
	    
	    public static void validateStateChangeReason(String p_stateChangeReason, boolean p_isManOrOpt) throws BTSLBaseException {
	        final String tagName = XmlTagValueConstant.TAG_STATE_CHANGE_REASON;
	        // Common Validation
	        blank(p_stateChangeReason, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_BLANK, tagName, p_isManOrOpt);
	        length(p_stateChangeReason, XmlTagValueConstant.STATE_CHANGE_REASON_LENGTH, PretupsErrorCodesI.EXTSYS_REQ_STATE_CHANGE_REASON_INVALID, tagName, p_isManOrOpt);

	    }


	    
	    public static void validateExpiryChangeReason(String p_remark, boolean p_isManOrOpt) throws BTSLBaseException {
	        final String tagName = XmlTagValueConstant.TAG_EXPIRY_CHANGE_REASON;
	        // Common Validation
	        blank(p_remark, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
	        alphanumericWithSpecialChar(p_remark, PretupsErrorCodesI.EXTSYS_NOT_ALFA_NUMERIC_SPECIAL, tagName, p_isManOrOpt);
	        length(p_remark, XmlTagValueConstant.REMARK_LENGTH, PretupsErrorCodesI.EXTSYS_LENGTH_INVALID, tagName, p_isManOrOpt);

	    }

		public static void validateVoucherSegment(String voucherSegment, boolean tagOptinal)  throws BTSLBaseException{
			final String tagName = XmlTagValueConstant.TAG_VOUCHERSEGMENT;
	    	// Common Validation
	    	blank(voucherSegment, PretupsErrorCodesI.EXTSYS_BLANK, tagName, tagOptinal);
	    	if(!PretupsBL.validateVoucherSegment(voucherSegment))
	    	{
	    		throw new BTSLBaseException("XMLTagValueValidation", "validateVoucherSegment", PretupsErrorCodesI.EXTSYS_VOUCHER_SEGMENT_INVALID, voucherSegment);
	    	}
		}

		public static void validateDenomination(String denomination, boolean tagOptinal)  throws BTSLBaseException{
			 final String tagName = XmlTagValueConstant.TAG_AMOUNT;
		        // Common Validation
		     blank(denomination, PretupsErrorCodesI.EXTSYS_BLANK, tagName, tagOptinal);
	    }
		
		public static void validateVoucherProfile(String voucherProfile, boolean tagOptinal)  throws BTSLBaseException{
			final String tagName = XmlTagValueConstant.TAG_VOUCHERPROFILE;
	        // Common Validation
			blank(voucherProfile, PretupsErrorCodesI.EXTSYS_BLANK, tagName, tagOptinal);
	    }
		
		public static void validateVoucherType1(String p_voucherType, boolean p_isManOrOpt) throws BTSLBaseException {
	    	final String tagName = XmlTagValueConstant.TAG_VOUCHERTYPE;
	    	// Common Validation
	    	blank(p_voucherType, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
	    	ArrayList<VoucherTypeVO> voucherTypeList = null;
	    	VomsProductDAO vomsProductDAO = new VomsProductDAO();
	    	String voucherDesc = null;
	    	Connection con = null;
	        MComConnectionI mcomCon = null;
	        try {
	        	mcomCon = new MComConnection();
	        	con = mcomCon.getConnection();
	        	voucherTypeList = vomsProductDAO.loadDigitalVoucherDetails(con);
	        	if(BTSLUtil.isNullString(p_voucherType) || !BTSLUtil.isAlphaNumeric(p_voucherType) ){
	        		throw new BTSLBaseException("XMLTagValueValidation",PretupsErrorCodesI.VOUCHER_TYPE_INVALID, p_voucherType, new String[] {XmlTagValueConstant.TAG_VOUCHERTYPE});
	        	}

	        	voucherDesc = BTSLUtil.getVoucherTypeDesc(voucherTypeList, p_voucherType);
	        	if (BTSLUtil.isNullString(voucherDesc)){
	        		throw new BTSLBaseException("XMLTagValueValidation", "not found", PretupsErrorCodesI.VOUCHER_TYPE_DOESNOT_EXIST, new String[] {tagName});
	        	}
	        }
	        catch (BTSLBaseException be)
	        {
	        	throw be;
	        }
	        catch (SQLException e) {
				_log.error("validateVoucherTypeDVD","SQLException : ", e.getMessage());
			}
	        finally
	        {
	        	if (con != null) {
	        		if (mcomCon != null)
	        			mcomCon.close("XMLTagValueValidation#validateVoucherTypeDVD");
	        		mcomCon = null;
	        		con = null;
	        	}
	        	if (_log.isDebugEnabled()) {
		            _log.debug("validateVoucherTypeDVD", " Exited ");
		        }
	        }	
	    }
		public static void validateVoucherSerialNo(String serialNo, boolean p_isManOrOpt) throws BTSLBaseException {
	        final String tagName = XmlTagValueConstant.TAG_SERIALNUMBER;
	        // Common Validation
	        blank(serialNo, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
	        numeric(serialNo, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC, tagName, p_isManOrOpt);
	    }
		
		/**
		 * Validate language code using DAO call in database
		 * @param languageCode
		 * @param p_errorKey
		 * @param tagName
		 * @throws BTSLBaseException
		 */
		static void isValidLanguageCode(String languageCode, String p_errorKey, String tagName) throws BTSLBaseException {
			MComConnectionI mcomCon = new MComConnection();
			Connection con = null;
			boolean isValidLanguageCode = false;
			try {
				con = mcomCon.getConnection();
				LocaleMasterDAO localeMasterDAO = new LocaleMasterDAO();
				isValidLanguageCode = localeMasterDAO.validateLanguageCode(con,languageCode);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (mcomCon != null) {
					mcomCon.close("XMLTagValueValidation#valid");
					mcomCon = null;
				}
			}
			if(!isValidLanguageCode)
				throw new BTSLBaseException("XMLTagValueValidation", "extcode", p_errorKey, new String[] {tagName});
	    }
		
		
		/**
		 * @param p_date
		 * @param p_isManOrOpt
		 * @param tagName
		 * @throws BTSLBaseException
		 */
		public static void validateFromToDate(String p_date, boolean p_isManOrOpt,String tagName) throws BTSLBaseException {
	        // Common Validation
	        blank(p_date, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
	        dateFormat(p_date,PretupsErrorCodesI.EXTSYS_DATE_INVALID_FORMAT,tagName,p_isManOrOpt);

	    }
		
		/**
		 * @param flag
		 * @param p_isManOrOpt
		 * @param tagName
		 * @throws BTSLBaseException
		 */
		public static void validateTopProductFlag(String flag, boolean p_isManOrOpt,String tagName) throws BTSLBaseException {
	        // Common Validation
	        blank(flag, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
	        if(!(flag.equals("Y")||flag.equals("N"))){
	        	_errorMsg[0] = tagName;
                throw new BTSLBaseException("XMLTagValueValidation", "blank",PretupsErrorCodesI.C2S_ERROR_TOP_PROD, _errorMsg);
	        }
		}


        /**
         * @param p_amount
         * @param p_isManOrOpt
         * @param tagName
         * @throws BTSLBaseException
         */
        public static void validateNoOfProd(String p_amount, boolean p_isManOrOpt,String tagName) throws BTSLBaseException {
	        // Common Validation
	        blank(p_amount, PretupsErrorCodesI.EXTSYS_BLANK, tagName, p_isManOrOpt);
	        numericOrDecimal(p_amount, PretupsErrorCodesI.EXTSYS_NOT_NUMERIC_OR_DECIMAL, tagName, p_isManOrOpt);
	    }

}

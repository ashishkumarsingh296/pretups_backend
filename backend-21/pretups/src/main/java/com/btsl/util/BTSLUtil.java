package com.btsl.util;

import java.io.BufferedReader;

/**
 * @(#)BTSLUtil
 *              Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 *              All Rights Reserved
 *              This class is an utility Class for Pretups System.
 *              ----------------------------------------------------------------
 *              ---------------------------------
 *              Author Date History
 *              ----------------------------------------------------------------
 *              ---------------------------------
 *              Abhijit Chauhan June 10,2005 Initial Creation
 *              Ankit Zindal Aug 17,2006 Modified for change ID=ENCODESPCL
 *              Change #1 for file TelesoftPreTUPsv5.0-TestlabTest record
 *              sheet.xls, the bug no.-880 on 13/10/06 by Amit Singh
 *              Ankit Zindal Nov 20,2006 ChangeID=LOCALEMASTER
 *              Ankit Zindal Dec 19, 2006 Change ID=ACCOUNTID
 *              ----------------------------------------------------------------
 *              --------------------------------
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.Collator;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.restapi.c2sservices.service.ReadGenericFileUtil;
import jakarta.activation.MimetypesFileTypeMap;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.spring.custom.action.Globals;
//import org.apache.struts.upload.FormFile;
import org.json.JSONArray;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.CertificateLoader;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.jigsaw.tcp.dto.TransferProfileWrapperDTO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.SearchCriteria.BooleanOperator;
import com.btsl.util.SearchCriteria.Operator;
import com.btsl.util.SearchCriteria.ValueType;
import com.btsl.voms.vomscategory.businesslogic.VomsCategoryVO;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductVO;
import com.btsl.voms.vomsproduct.businesslogic.VoucherTypeVO;
import com.google.gson.Gson;
import com.ibm.icu.util.Calendar;

import nl.captcha.Captcha;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.*;

public class BTSLUtil { 

    private static final Log LOG = LogFactory.getLog(BTSLUtil.class.getName());
    public static final String classname = "BTSLUtil";
    public static final int PERIOD_DAY = 1;
    public static final int PERIOD_WEEK = 2;
    public static final int PERIOD_MONTH = 3;
    public static final int PERIOD_YEAR = 4;
    /**
     * boolean specifying by default whether or not it is okay for a String to
     * be empty
     */
    public static final boolean defaultEmptyOK = true;
    public static final String whitespace = " \t\n\r";
    private static final String FORMAT="'9999999999999999999999D99'";
    /**
     * Default Constructor
     * 
     */
    public BTSLUtil() {
        super();
    }

    /**
     * Is Null String
     * 
     * @param str
     * @return
     */
    public static boolean isNullString(String str) {
        if (str == null || str.trim().length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get Financial Year
     * 
     * @return String
     */
    public static String getFinancialYear() {
        final Calendar getRight = BTSLDateUtil.getInstance();
        final int month = getRight.get(Calendar.MONTH);
        final int year = getRight.get(Calendar.YEAR);
        int financialYearStartIndex = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.FINANCIAL_YEAR_START_INDEX))).intValue();
        final int finanYearStart = financialYearStartIndex;
        if (finanYearStart != 0 && (month >= 0) && (month < finanYearStart)) {
            return String.valueOf(year - 1);
        } else {
            return String.valueOf(year);
        }
    }

    /**
     * Get java.util.Date from java.sql.Timestamp
     * 
     * @param date
     * @return
     */
    /**
     * @param timestamp
     * @return
     * @throws Exception
     */
    public static java.util.Date getUtilDateFromTimestamp(Timestamp timestamp) throws Exception {
        if (timestamp == null) {
            return null;
        }
        return new java.util.Date(timestamp.getTime());
    }

    /**
     * Get java.sql.Timestamp from java.util.Date
     * 
     * @param date
     * @return
     */
    public static Timestamp getTimestampFromUtilDate(java.util.Date date) {
        if (date == null) {
            return null;
        }
        return new java.sql.Timestamp(date.getTime());
    }

    /**
     * @param p_str
     * @param p_str2
     * @return
     */
    public static boolean compareLocaleString(String p_str, String p_str2) {

        boolean flag = false;
        final Collator usCollator = Collator.getInstance();
        usCollator.setStrength(Collator.IDENTICAL);

        if (usCollator.compare(p_str, p_str2) == 0) {
            flag = true;
        }
        return flag;
    }

    /**
     * Format the message against the format defined in config file
     * 
     * @param p_action
     * @param p_key
     * @param p_message
     * @return
     *         String
     *         NetworkCache
     */

    public static String formatMessage(String p_action, String p_key, String p_message) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("formatMessage", "Entered Action " + p_action + " p_key" + p_key + " message " + p_message);
        }

        final StringBuffer messageFormat = new StringBuffer(Constants.getProperty("cachemessageformat"));
        messageFormat.replace(messageFormat.indexOf("{"), messageFormat.indexOf("}") + 1, p_action);
        messageFormat.replace(messageFormat.indexOf("{"), messageFormat.indexOf("}") + 1, p_key);
        messageFormat.replace(messageFormat.indexOf("{"), messageFormat.indexOf("}") + 1, p_message);

        if (LOG.isDebugEnabled()) {
            LOG.debug("formatMessage", "Exited " + messageFormat);
        }
        return messageFormat.toString();
    }

    /**
     * Parse the ArrayList that consist of ListValueVO, returns the ListvalueVO
     * object where ListvalueVO.value == code(coming as a request parameter)
     * 
     * @param p_List
     *            (ArrayList)
     * @param p_code
     *            (String)
     * @return ListValueVO
     */
    /**
     * @param p_code
     * @param p_list
     * @return
     */
    public static ListValueVO getOptionDesc(String p_code, List p_list) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("getOptionDesc", "Entered: p_code=" + p_code + " p_list=" + p_list);
        }
        ListValueVO vo = null;
        boolean flag = false;
        if (p_list != null && !p_list.isEmpty()) {
            for (int i = 0, j = p_list.size(); i < j; i++) {
                vo = (ListValueVO) p_list.get(i);
                if (vo.getValue().equalsIgnoreCase(p_code)) {
                    flag = true;
                    break;
                }
            }
        }
        if (!flag) {
            vo = new ListValueVO();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("getOptionDesc", "Exited: vo=" + vo);
        }
        return vo;
    }

    /**
     * Null To String
     * 
     * @param p_str
     * @return
     */
    public static String NullToString(String p_str) {
        if (p_str == null) {
            return "";
        } else {
            return p_str;
        }
    }

    /**
     * Decrypts the passed text string using an encryption key
     * 
     * @param p_text
     * @return
     */
    public static String decryptText(String p_text) {
        final String METHOD_NAME = "decryptText";
        String pinPasswordEnDeCryptionType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE);
        try {
            if ("SHA".equals(pinPasswordEnDeCryptionType)) {
                return p_text;
            } else if ("AES".equals(pinPasswordEnDeCryptionType)) {
                return new AESEncryptionUtil().DecryptAES(p_text);
            } else if ("DES".equals(pinPasswordEnDeCryptionType)) {
                return new CryptoUtil().decrypt(p_text, Constants.KEY);
            } else {
                return null;
            }
        } catch (Exception e) {
            LOG.error("decryptText", "Exception e=" + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            return null;
        }
    }// end method

    /**
     * Encrypts the passed text string using an encryption key
     * 
     * @param password
     * @return String
     */
    
    /**
     * @param p_text
     * @return
     */
    public static String encryptText(String p_text) {
        final String METHOD_NAME = "encryptText";
        String pinPasswordEnDeCryptionType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE);
        try {
            if ("SHA".equals(pinPasswordEnDeCryptionType)) {
                return OneWayHashingAlgoUtil.getInstance().encrypt(p_text);
            } else if ("AES".equals(pinPasswordEnDeCryptionType)) {
                return new AESEncryptionUtil().EncryptAES(p_text);
            } else if ("DES".equals(pinPasswordEnDeCryptionType)) {
                return new CryptoUtil().encrypt(p_text, Constants.KEY);
            } else {
                return null;
            }
        } catch (Exception e) {
            LOG.error("encryptText", "Exception e=" + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            return null;
        }
    }

    /**
     * Converts Util date to Sql Date
     * 
     * @param utilDate
     * @return
     */
    public static java.sql.Date getSQLDateFromUtilDate(java.util.Date utilDate) {
        final String METHOD_NAME = "getSQLDateFromUtilDate";
        java.sql.Date sqlDate = null;
     
		 if (LOG.isDebugEnabled()) {
			 LOG.debug(METHOD_NAME, "Entered  utilDate=" +utilDate);
		 }
		 if (utilDate != null) {
			final java.util.Date utilDateNew=(Date)utilDate.clone();
			try {
                utilDateNew.setHours(0);
                utilDateNew.setMinutes(0);
                utilDateNew.setSeconds(0);
				sqlDate = new java.sql.Date(utilDateNew.getTime());
//				utilDate=utilDateNew;
			} catch (Exception e) {
				LOG.debug(METHOD_NAME,"Entered Catch Block utilDate: "+utilDate);
				sqlDate = new java.sql.Date(utilDateNew.getTime());
//				utilDate=utilDateNew;
			}
	      }
	        
		 if(LOG.isDebugEnabled()) {
			 LOG.debug(METHOD_NAME,"Exiting message Final Unchanged utilDate: "+utilDate);
		 }
	     return sqlDate;
	   }// end of UtilDateToSqlDate

    /**
     * Converts sql Date to Util Date format
     * 
     * @param sqlDate
     * @return util.date
     */
    public static java.util.Date getUtilDateFromSQLDate(java.sql.Date sqlDate) {
        java.util.Date tempUtilDate = null;
        if (sqlDate != null) {
            tempUtilDate = new java.util.Date(sqlDate.getTime());
        }
        return tempUtilDate;
    }// end of sqlDateToUtilDate

    /**
     * converts java.sql.date to a string in indian format
     * 
     * @param Date
     *            to be converted
     * @return String in Indian Format
     */
    
    /**
     * @param string
     * @param inString
     * @return
     */
    public static boolean isStringIn(String string, String inString) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("isStringIn", "Entered string=" + string + "  inString=" + inString);
        }
        final String METHOD_NAME = "isStringIn";
        try {
            ArrayList usageStringList = null;
            if (inString != null) {
                usageStringList = new ArrayList();
                final StringTokenizer strToken = new StringTokenizer(inString, ",");
                while (strToken.hasMoreTokens()) {
                    usageStringList.add(strToken.nextElement());
                }
            } else {
                return false;
            }
            for (int i = 0; i < usageStringList.size(); i++) {
                if (((String) usageStringList.get(i)).equals(string)) {
                    return true;
                }
            }
        } catch (Exception e) {
            LOG.error("isStringIn", "Exception e:" + e);
            LOG.errorTrace(METHOD_NAME, e);
        }
        return false;
    }

    /**
     * Checks whether coma seprated value present in a string
     * 
     * @param commaStr
     * @param string
     * @return
     */
    public static boolean isStringContain(String commaStr, String string) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("isStringContain", "Entered commaStr=" + commaStr + "  string=" + string);
        }
        final String METHOD_NAME = "isStringContain";
        try {
            ArrayList usageStringList = null;
            if (commaStr != null) {
                usageStringList = new ArrayList();
                final StringTokenizer strToken = new StringTokenizer(commaStr, ",");
                while (strToken.hasMoreTokens()) {
                    usageStringList.add(strToken.nextElement());
                }
            } else {
                return false;
            }
            String tempStr;
            for (int i = 0; i < usageStringList.size(); i++) {
                tempStr = (String) usageStringList.get(i);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("isStringContain", "comparing allowed=" + tempStr + "  with=" + string);
                }
                if (string.indexOf(tempStr.trim()) != -1) {
                    return true;
                }
            }
        } catch (Exception e) {
            LOG.error("isStringContain", "Exception e=" + e);
            LOG.errorTrace(METHOD_NAME, e);
        }
        return false;
    }

    /**
     * converts a string that contains a date to a Date.
     * The entry string should not be null else a Nullpointer will be thrown
     * 
     * @param allowedDays
     *            String
     * @param fromTime
     *            String
     * @param toTime
     *            String
     * @return boolean
     * @throws ParseException
     *             if date is invalid or wrong format
     */
    public static boolean isDayTimeValid(String allowedDays, String fromTime, String toTime) throws ParseException {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("isDayTimeValid", "Entered allowedDays=" + allowedDays + "   fromTime=" + fromTime + "   toTime=" + toTime);
            }
            if(!BTSLUtil.isNullString(Constants.getProperty("IS_DATE_FIELD_DISABLED")) && 
            		PretupsI.NO.equals(Constants.getProperty("IS_DATE_FIELD_DISABLED").toUpperCase())) {
                 return true;
            }
            final Calendar cal = BTSLDateUtil.getInstance();

            String todayDay = Integer.toString(cal.get(Calendar.DAY_OF_WEEK));
            
            // 1 BugNo 880 Code fix start
            if (isNullString(fromTime) && isNullString(toTime)) {
                if (isNullString(allowedDays)) {
                    return true;
                }
                if (allowedDays.indexOf(todayDay) == -1) {
                    return false;
                } else {
                    return true;
                }
            }

            if (isNullString(fromTime) || isNullString(toTime)) {
                return true;
            }

            // 1 BugNo 880 Code fix end
            final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMM);
            sdf.setLenient(false);
            final java.util.Date currentDate = new java.util.Date();
            final String dateString = getDateStringFromDate(currentDate);
            if (!isNullString(fromTime)) {
                if (fromTime.indexOf(":") == -1) {
                    fromTime = fromTime + ":00";
                }
            }
            if (!isNullString(toTime)) {
                if (toTime.indexOf(":") == -1) {
                    toTime = toTime + ":00";
                }
            }

            java.util.Date fromdate = sdf.parse(dateString + " " + fromTime);
            java.util.Date todate = sdf.parse(dateString + " " + toTime);
            if (todate.before(fromdate) && currentDate.after(fromdate) && currentDate.after(todate)) {
                final Calendar tempcal = BTSLDateUtil.getInstance();
                tempcal.setTime(todate);
                tempcal.add(Calendar.DATE, 1);
                todate = tempcal.getTime();
            }
            if (todate.before(fromdate) && currentDate.before(fromdate) && currentDate.before(todate)) {
                final Calendar tempcal = BTSLDateUtil.getInstance();
                tempcal.setTime(fromdate);
                tempcal.add(Calendar.DATE, -1);
                todayDay = String.valueOf(Integer.parseInt(todayDay) - 1);
                fromdate = tempcal.getTime();
            }
            if (!isNullString(allowedDays) && allowedDays.indexOf(todayDay) == -1) {
                return false;
            }
            if (currentDate.after(fromdate) && currentDate.before(todate)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            LOG.error("isDayTimeValid", "Exception " + e);
            throw e;
        }
    }

    /**
     * Checks whether current hours is allowed in the hours allowed
     * 
     * @param currentHour
     * @param fromTimeString
     * @param toTimeString
     * @param delimiter
     * @param message
     * @return boolean
     */
    public static boolean isHourBetweenStrings(int currentHour, String fromTimeString, String toTimeString, String delimiter, StringBuffer message) {
        final String METHOD_NAME = "isHourBetweenStrings";
        try {
            ArrayList fromTimeStringList = null;
            ArrayList toTimeStringList = null;
            if (fromTimeString != null) {
                fromTimeStringList = new ArrayList();
                final StringTokenizer strToken = new StringTokenizer(fromTimeString, delimiter);
                while (strToken.hasMoreTokens()) {
                    fromTimeStringList.add(((String) strToken.nextElement()).trim());
                }
            } else {
                return false;
            }
            if (toTimeString != null) {
                toTimeStringList = new ArrayList();
                final StringTokenizer strToken = new StringTokenizer(toTimeString, delimiter);
                while (strToken.hasMoreTokens()) {
                    toTimeStringList.add(((String) strToken.nextElement()).trim());
                }
            } else {
                return false;
            }
            int size = 0;
            if (toTimeStringList.size() < fromTimeStringList.size()) {
                size = toTimeStringList.size();
            } else {
                size = fromTimeStringList.size();
            }
            int fromHour;
            int toHour;
            for (int i = 0; i < size; i++) {
                fromHour = Integer.parseInt((String) fromTimeStringList.get(i));
                toHour = Integer.parseInt((String) toTimeStringList.get(i));
                if (i != 0) {
                    message.append(" or ");
                }
                message.append(fromHour + "-" + toHour);
                if (fromHour > toHour) {
                    if ((currentHour <= fromHour && currentHour < toHour)||currentHour >= fromHour && currentHour > toHour) {
                        return true;
                    }

                } else {
                    if (currentHour >= fromHour && currentHour < toHour) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            LOG.error("isHourBetweenStrings", " Exception e=" + e);
            LOG.errorTrace(METHOD_NAME, e);
        }
        return false;
    }

    /**
     * 
     * @param str
     * @return
     */
    public static boolean isValidAmount(String str) {
        boolean found = false;
        final String dot = new String(".");
        String tempDot = "";
        str = str.trim();
        final int strLength = str.length();

        for (int i = 0; i < strLength; i++) {
            tempDot = str.substring(i, i + 1);
            if (dot.equals(tempDot)) {
                if (found) {
                    return false;
                } else {
                    found = true;
                }

            } else if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates MSISDN for numeric and MIN and MAX length
     * 
     * @param mobileno
     * @return
     */
    
    /**
     * @param p_msisdn
     * @return
     */
    public static boolean isValidMSISDN(String p_msisdn) {
        final int strLength = p_msisdn.length();
        int minMsisdnLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue();
        int maxMsisdnLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue();
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                "isValidMobileNo",
                "strLength= " + strLength + " minMsisdnLength=" + minMsisdnLength + "maxMsisdnLength=" + maxMsisdnLength + "*************");
        }
        //if MSISDN having only zeros
        if(p_msisdn.matches("^[0]+$")){
        	 return false;
        }
        
        if (strLength < minMsisdnLength || strLength > maxMsisdnLength) {
            return false;
        }
        for (int i = 0; i < strLength; i++) {
            if (!((Character.isDigit(p_msisdn.charAt(i))) || (p_msisdn.charAt(i) == '+'))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Pads the number to zeroes at the starting of a number
     * 
     * @param p_strValue
     * @param p_strLength
     * @return String
     */
    public static String padZeroesToLeft(String p_strValue, int p_strLength) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("padZeroesToLeft()", "Entered with p_strValue= " + p_strValue + " p_strLength:" + p_strLength);
        }
        final int cntr = p_strLength - p_strValue.length();
        if (cntr > 0) {
            for (int i = 0; i < cntr; i++) {
                p_strValue = "0" + p_strValue;
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("padZeroesToLeft()", "Exiting with p_strValue= " + p_strValue);
        }
        return p_strValue;
    }

    /**
     * To check String have numeric value or not. The numeric value should not
     * be decimal value
     * 
     * @param str
     * @return boolean if numeric returns true else false;
     */
    public static boolean isNumeric(String str) {

        boolean flag = true;
        if (str != null) {
            final char arr[] = str.toCharArray();

            for (int i = 0; i < arr.length; i++) {

                if (!(arr[i] >= '0' && arr[i] <= '9')) {
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }

    public static boolean isNegativeNumeric(String str) {

        boolean flag = false;
        boolean isNegative = false;
        if (str != null) {
            final char arr[] = str.toCharArray();

            for (int i = 0; i < arr.length; i++) {
            	if(arr[0] == '-') {
            		isNegative = true;
            		break;
            	}
            }
            if(isNegative && isNumeric(str.substring(1))) {
            	flag = true;
            }
        }
        return flag;
    }
    
    /**
     * To check whether the value is proper decimal value
     * 
     * @param str
     * @return
     */
    public static boolean isDecimalValue(String str) {

        boolean flag = false;
        final String METHOD_NAME = "isDecimalValue";
        try {
            Double.parseDouble(str);
            flag = true;
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            flag = false;
        }
        return flag;
    }

    
    public static boolean isValidDecimal(String str) {

        boolean flag = false;
        final String METHOD_NAME = "isDecimalValue";
        try {
            double d=Double.parseDouble(str);
            double d1 = d%1;
            if(floatEqualityCheck(d1, 0d, "!="))
            	flag = true;
            else
            	flag = false;
            
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            
        }
        return flag;
      
        
    }

    /**
     * This method converts a string in the format "a=1&b=2&c=3" into a HashMap
     * in the format "{a=1, b=2, c=3}"
     * 
     * @param str
     *            The string to be converted to hash
     * @param startStr
     *            string after which tokenization is required i.e. "?"
     * @param token1
     *            The tokenizer : "&"
     * @param token2
     *            The tokenizer : "="
     * @return Returns HashMap representation of the String passed
     */
    public static HashMap getStringToHash(String str, String startStr, String token1, String token2) {

        str = str.substring(str.indexOf(startStr) + startStr.length());
        return getStringToHash(str, token1, token2);
    }

    /**
     * This method converts a string in the format "a=1&b=2&c=3" into a HashMap
     * in the format "{a=1, b=2, c=3}"
     * 
     * @param str
     *            The string to be converted to hash
     * @param token1
     *            The tokenizer : "&"
     * @param token2
     *            The tokenizer : "="
     * @return Returns HashMap representation of the String passed
     */
    public static HashMap getStringToHash(String str, String token1, String token2) {
        final HashMap ht = new HashMap();
        StringTokenizer stToken1 = null;
        StringTokenizer stToken2 = null;
        String newString = "";
        final ArrayList list = new ArrayList();
        stToken1 = new StringTokenizer(str, token1);
        while (stToken1.hasMoreTokens()) {
            newString = stToken1.nextToken();
            if (newString.indexOf(token2) < 0) {
                continue;
            }
            stToken2 = new StringTokenizer(newString, token2);
            if (stToken2.countTokens() < 2) {
                continue;
            }
            while (stToken2.hasMoreTokens()) {
                list.add(stToken2.nextToken());
            }
        }
        int listSize = list.size();
        for (int i = 0; i < listSize; i++) {
            if (i % 2 == 1) {
                ht.put(list.get(i - 1), list.get(i));
            } // End of if Statement
        } // End of for loop

        return ht;
    }

    /**
     * This method converts a string in the format "a=1&b=2&c=3" into a HashMap
     * in the format "{a=1, b=2, c=3}"
     * 
     * @param str
     *            The string to be converted to hash
     * @param token1
     *            The tokenizer : "&"
     * @param token2
     *            The tokenizer : "="
     * @return Returns void
     */
    /**
     * @param map
     * @param str
     * @param token1
     * @param token2
     */
    public static void populateStringToHash(HashMap map, String str, String token1, String token2) {
        StringTokenizer stToken1 = null;
        StringTokenizer stToken2 = null;
        String newString = "";
        final ArrayList list = new ArrayList();
        stToken1 = new StringTokenizer(str, token1);
        while (stToken1.hasMoreTokens()) {
            newString = stToken1.nextToken();
            stToken2 = new StringTokenizer(newString, token2);
            while (stToken2.hasMoreTokens()) {
                list.add(stToken2.nextToken());
            }
        }
        for (int i = 0; i < list.size(); i++) {
            if (i % 2 == 1) {
                map.put(list.get(i - 1), list.get(i));
            } // End of if Statement
        } // End of for loop
    }

    /**
     * Convert Hash to String
     * 
     * @param map
     * @return
     */
    public static String getStringFromHash(HashMap map) {
		String  methodName="getStringFromHash";
        final ArrayList list = new ArrayList();
        String msg = "";
        final Set keySet = map.keySet();
        final Iterator it = keySet.iterator();
        String key;
        String value;
        StringBuilder sb = new StringBuilder(1024);
        while (it.hasNext()) {
            key = (String) it.next();
            sb.append(key).append("=");
            value = (String) map.get(key);
            if (value != null) {
                try {
					sb.append(URLEncoder.encode(value,(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CHARSET_ENCODING)))).append("&");
				} catch (UnsupportedEncodingException e) {
				LOG.errorTrace(methodName, e);
				}
            } else {
                sb.append("&");
            }
        }
        msg = sb.toString();
        return msg;
    }

    /**
     * To get the difference between two util dates in no of days
     * 
     * @param date1
     * @param date2
     * @return int
     */
    public static int getDifferenceInUtilDates(java.util.Date date1, java.util.Date date2) {
        final long dt1 = date1.getTime();
        final long dt2 = date2.getTime();
        final int nodays = Long.valueOf(((dt2 - dt1) / (1000 * 60 * 60 * 24))).intValue();
        return nodays;
    }

    /**
     * Returns the last specified no of last digits in a year
     * 
     * @param p_noOfDigits
     *            If passed p_noOfDigits=2 then year returned will be last 2
     *            digits i.e. 05 in case of 2005
     * @return
     */
    public static String getFinancialYearLastDigits(int p_noOfDigits) {
        String yearStr = getFinancialYear();
        if (p_noOfDigits <= 4) {
            yearStr = yearStr.substring(yearStr.length() - p_noOfDigits, yearStr.length());
        }
        return yearStr;
    }

    /**
     * Get Date From Date String
     * 
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static Date getDateFromDateString(String dateStr) throws ParseException {
    	String systemDateFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT);
        String format = systemDateFormat;
        try {
            dateStr = URLDecoder.decode(dateStr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //do nothing
        }
        if (isNullString(format)) {
            format = "dd/MM/yy";
        }
        if (format.length() != dateStr.length()) {
            throw new ParseException(format, 0);
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false); // this is required else it will convert
        return sdf.parse(dateStr);
    }

    
    
    public static Time getTimeFromTimeString(String dateStr) throws ParseException {
        String format = "HH:mm";
        if (format.length() != dateStr.length()) {
            throw new ParseException(format, 0);
        }
        DateFormat formatter = new SimpleDateFormat(format);
       Time timeValue = new Time(formatter.parse(dateStr).getTime());
        
        return timeValue;
    }
    public static boolean TimeRangeValidation(String additionalCommissionTimeSlab,String otfTimeSlab, String comType)throws ParseException{
    	 final String methodName="TimeRangeValidation";
    	try{
    	 if(!BTSLUtil.isNullString(additionalCommissionTimeSlab)){
         	String[] timeRange=additionalCommissionTimeSlab.split(",");
         
         	if(!BTSLUtil.isNullString(otfTimeSlab)){
          		String[] otfTimeRange=otfTimeSlab.split(",");
         		boolean valid =  false;
         		int k=0;
         		int r=0;
         		for(int t=0;t<otfTimeRange.length;t++){
         		String[] otftime=otfTimeRange[t].split("-");
         		
         		for(int m=0;m<timeRange.length;m++){
         			String[] time=timeRange[m].split("-");
         			if(comType.equals(PretupsI.COMM_TYPE_ADNLCOMM)){
         				if((getTimeFromTimeString(otftime[k]).after(getTimeFromTimeString(time[r]))||getTimeFromTimeString(otftime[k]).equals(getTimeFromTimeString(time[r]))) && (getTimeFromTimeString(otftime[k+1]).before(getTimeFromTimeString(time[r+1]))||getTimeFromTimeString(otftime[k+1]).equals(getTimeFromTimeString(time[r+1])))){
         					valid = true;
         					break;
         				}else{
         					valid = false;
         				}
         			}
         			else if(comType.equals(PretupsI.COMM_TYPE_BASECOMM)){
         				if(getTimeFromTimeString(otftime[k]).after(getTimeFromTimeString(time[r]))||getTimeFromTimeString(otftime[k]).equals(getTimeFromTimeString(time[r]))|| getTimeFromTimeString(otftime[k+1]).equals(getTimeFromTimeString(time[r]))||getTimeFromTimeString(otftime[k+1]).after(getTimeFromTimeString(time[r]))){
         					valid = true;
         					break;
         				}else{
         					valid=false;
         				}
         			}
         			
         		}
         		
         		if(!valid){
         			return true;
         		}
         		
         		}
         		if(!valid){
         			return true;
         		}else{
         			return false;
         		}
         	}
         		
         }
    	 return false;
    	}
    	catch(Exception e){
    		 LOG.errorTrace(methodName, e);
    		 return false;
    	}
    }
    /**
     * Get Date String From Date
     * 
     * @param date
     * @return
     * @throws ParseException
     */
    public static String getDateStringFromDate(Date date) throws ParseException {
    	String systemDateFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT);
    	String format = systemDateFormat;
        if (isNullString(format)) {
            format = PretupsI.DATE_FORMAT;
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false); // this is required else it will convert
        return sdf.format(date);
    }

    /**
     * Get DateTime String From Date
     * 
     * @param date
     * @return
     * @throws ParseException
     */
    public static String getDateTimeStringFromDate(Date date) throws ParseException {
    	String systemDateTimeFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATETIME_FORMAT);
        String format = systemDateTimeFormat;
        if (isNullString(format)) {
            format = PretupsI.TIMESTAMP_DATESPACEHHMM;
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false); // this is required else it will convert
        return sdf.format(date);
    }

    /**
     * Get Date From Date String
     * 
     * @param dateStr
     * @param format
     * @return
     * @throws ParseException
     */
    public static Date getDateFromDateString(String dateStr, String format) throws ParseException {

        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false); // this is required else it will convert
        return sdf.parse(dateStr);
    }

    /**
     * Get date-String in the format provided in "format" from Date
     * If format set null then default format will be dd/MM/yy HH:mm:ss
     * 
     * @param date
     * @param format
     * @return String Date-String
     * @throws ParseException
     * @author zafar.abbas
     */
    public static String getDateTimeStringFromDate(Date date, String format) throws ParseException {
        if (isNullString(format)) {
            format = PretupsI.TIMESTAMP_DATESPACEHHMMSS;
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false); // this is required else it will convert
        return sdf.format(date);
    }

    /**
     * @param args
     */
    public static void main(String args[]) {
        final Date currentdate = new Date();
        final String METHOD_NAME = "main";

        try {
        	
        	ChannelUserVO channelUserVO1 = null;
        	if(BTSLUtil.isNullObject(channelUserVO1)){
        		LogFactory.printLog(METHOD_NAME, "NULL CHECK "+channelUserVO1, LOG);
        	} 
        	
        	ChannelUserVO channelUserVO2 = new ChannelUserVO();
        	if(!BTSLUtil.isNullObject(channelUserVO2)){
        		LogFactory.printLog(METHOD_NAME, "NULL CHECK "+channelUserVO2, LOG);
        	}
        	
        	ChannelUserVO channelUserVO3 = new ChannelUserVO();
        	ChannelUserVO channelUserVO4 = channelUserVO3; 
        	channelUserVO4.setAccessType("a");
        	if(channelUserVO4.equals(channelUserVO3)){
        		LogFactory.printLog(METHOD_NAME, "Equal Check CHECK "+channelUserVO3, LOG);
        	}else{
        		LogFactory.printLog(METHOD_NAME, "NO NULL POINTER ", LOG);
        	}
        	/*Properties properties = new Properties();
            File file = new File("C:/pretups/jee-neon2/workspace5/pretups/src/main/resources/configfiles/INFiles/mobileAppResponse.props");
            properties.load(new FileInputStream(file));
        	
        	String message = "TYPE=C2SCPN&MSISDN=<MSISDN>&OLDPIN=<old pin>&NEWPIN=<new pin>&CONFIRMPIN=<confirm pin>&IMEI=<IMEI>&LANGUAGE1=<lang code>";
            int index = message.indexOf("=");
            int index1 = message.indexOf("&");
            System.out.println("Shishupal Singh"+index);
            String methodName = message.substring(index+1, index1);
            System.out.println("Shishupal Singh"+methodName);
            String body = message.substring(index+1);
            System.out.println("Shishupal Singh"+body);
            
            System.out.println("Shishupal Singh"+properties.getProperty(methodName));*/
            
           
            //timeRangeValidation("adasd", new Date());
           /* final Date d = new Date();
            final Calendar cal = BTSLDateUtil.getInstance();
    		java.util.Date dt = cal.getTime(); // Current Date
            dt = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(BTSLUtil.addDaysInUtilDate(dt, -3), PretupsI.DATE_FORMAT));
    		//dt = BTSLUtil.addDaysInUtilDate(dt, -3);
            System.out.println("shishupal - "+ dt);
            System.out.println( new CryptoUtil().decrypt("293e04353e2423f4", Constants.KEY));
            System.out.println( new CryptoUtil().encrypt("pretups74_dev", Constants.KEY));*/
           /* if (LOG.isDebugEnabled()) {
                LOG.debug("main", "Decryption Using DES Algo:-" + new CryptoUtil().decrypt("79fdd8ae822fd4500b98afb3049bc012", Constants.KEY));
            } */
        } catch (Exception e) {
        	LOG.error(METHOD_NAME,"Exception "+e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
        }
    }

    /**
     * @param p_date
     * @return
     */
    public static Date getFirstDateOfMonth(Date p_date) {

        final Calendar calendar = BTSLDateUtil.getInstance();

        calendar.set(p_date.getYear(), p_date.getMonth(), 1);

        return calendar.getTime();

    }

    /**
     * Get Message from Messages.props on the basis of locale
     * 
     * @param locale
     * @param key
     * @param args
     * @return
     */
    public static String getMessage(Locale locale, String key, String[] p_args) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getMessage", "Entered");
        }
        HttpServletRequest request = null;
        String language = null;
        String country = null;
        if (RequestContextHolder.getRequestAttributes() != null) {

            request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

            if(request != null) {
                language = request.getHeader("language");
                country = request.getHeader("country");
            }
        }

        if (language != null && country != null) {
            locale = new Locale(language, country);
        }

        String message = null;
        final String METHOD_NAME = "getMessage";
        String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		String p_argstemp[] = null;
        try {
            if (locale == null) {
                LOG.error("getMessage",
                    "Locale not defined considering default locale " + defaultLanguage + " " + defaultCountry + "    key: " + key);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BTSLUtil[getMessage]", "", "", " ",
                    "Locale not defined considering default locale " + defaultLanguage + " " + defaultCountry + "    key: " + key);
                locale = new Locale(defaultLanguage, defaultCountry);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("getMessage", "entered locale " + locale.getDisplayName() + " key: " + key + " args: " + p_args);
            }
            MessagesCache messagesCache = MessagesCaches.get(locale);
            if (messagesCache == null) {
                LOG.error("getMessage", "Messages cache not available for locale: " + locale.getDisplayName() + "    key: " + key);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[getMessage]", "", "", " ",
                    "Messages cache not available for locale " + locale.getDisplayName() + "    key: " + key);
                locale = new Locale(defaultLanguage, defaultCountry);
                messagesCache = MessagesCaches.get(locale);
                if (messagesCache == null) {
                    return null;
                }
            }
            message = messagesCache.getProperty(key);
            // ChangeID=LOCALEMASTER
            // populate localemasterVO for the requested locale
            final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);

            if (("ru".equals(locale.getLanguage()) && message != null) || ("ar".equals(locale.getLanguage()) && message != null) || ("fa".equals(locale.getLanguage()) && message != null)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("getMessage", "encoding msg for russian/arabic locale: " + locale.getDisplayName() + " key: " + key + " message: " + message);
                }
                final int indexOf = message.indexOf("mclass^");
                String tempMessage = null;
                String messageWithID = null;
                String messageID = null;
                String messageToEncode = null;
                if (indexOf != -1) {
                    tempMessage = message.substring(0, message.indexOf(":", indexOf) + 1);
                    messageWithID = message.substring(message.indexOf(":", indexOf) + 1);
                } else {
                    messageWithID = message;
                }
                if (messageWithID.lastIndexOf("[") != -1) {
                    if ("ar".equals(locale.getLanguage()) || "fa".equals(locale.getLanguage())) {
                        messageToEncode = messageWithID.substring(0, messageWithID.lastIndexOf("["));
                    } else {
                        messageToEncode = messageWithID.substring(messageWithID.lastIndexOf("]") + 1);
                    }
                    LOG.debug("getMessage", "Message: " + messageToEncode);
                    messageID = messageWithID.substring(messageWithID.lastIndexOf("[") + 1, messageWithID.lastIndexOf("]"));
                    LOG.debug("getMessage", "MessageID: " + messageID);
                } else {
                    messageToEncode = messageWithID;
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("getMessage", "messageToEncode: " + messageToEncode + " messageWithID: " + messageWithID);
                }
                // ChangeID=LOCALEMASTER
                // pass the encoding scheme from the locale master VO
                final String text = encodeSpecial(messageToEncode, false, localeVO.getEncoding());

                LOG.debug("getMessage", "message after encoding: " + text);
                String str = null;
                if (messageID != null) {
                    // ChangeID=LOCALEMASTER
                    // pass the encoding scheme from the locale master VO
                    final String unicodeMessageID = encodeSpecial(messageID, true, localeVO.getEncoding()) + "%00%3A";
                    str = unicodeMessageID + text;
                } else {
                    str = text;
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("getMessage", "message after encode but before replacing args str: " + str);
                }
                if (!isNullString(tempMessage)) {
                    str = tempMessage + str;
                }
                if (p_args != null && p_args.length > 0) {
                    final int argslen = p_args.length;
					p_argstemp = new String[argslen];
                    p_argstemp = p_args.clone();
                    for (int index = 0; index < argslen; index++) {
                        // Change done by Ankit Zindal
                        // Date 17/08/06 Change ID=ENCODESPCL
                        // Reason of change is that, in some cases array size is
                        // more but elements are null in it
                        // So it gives null pointer exception when we are going
                        // to encode a null element.
                        // Now encode special method is called only when element
                        // of array is not null.

                        // ChangeID=LOCALEMASTER
                        // pass the encoding scheme from the locale master VO
                        if (p_args[index] != null) {
                            p_argstemp[index] = encodeSpecial(p_args[index], true, localeVO.getEncoding());
                        }
                    }
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("getMessage", "message  after encoding and before formatting: " + message);
                }
                if (str != null) {
                    message = str;
                }
			} else {
            	if (p_args != null && p_args.length > 0) {
            		final int argslen = p_args.length;
	            	p_argstemp = new String[argslen];
	                p_argstemp = p_args.clone();                
            	}
            }
            
			/*
             * The change below is done by ankit Z on date 4/8/06 for following
             * problem
             * 1) In case when message is not find in message.props then message
             * formatter should not be called
             * 2) If message is not defined for key which are combination of two
             * parts i.e. have under score in it then no event is not raised
             * This alarm is not raised because we will try other key as its
             * alternative
             * 3) If key not contains under score then alarm will be raised.
             */
            if (!BTSLUtil.isNullString(message)) {
                message = MessageFormat.format(escape(message), p_argstemp);
            } else if (!BTSLUtil.isNullString(key) && key.indexOf("_") == -1) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[getMessage]", "", "", "",
                    " Exception: Message not defined for key" + key);
            }
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[getMessage]", "", "", "", " Exception:" + e
                .getMessage());
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("getMessage", "Exiting message: " + message);
            }
        }
        return message;
    }

    /**
     * Get Message from Messages.props on the basis of locale
     * 
     * @param locale
     * @param keyArgumentList
     * @return
     */
    public static String getMessage(Locale locale, ArrayList keyArgumentList) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getMessage", "Entered locale: " + locale + " keyArgumentList size: " + keyArgumentList.size());
        }
        String str = "";
        final String METHOD_NAME = "getMessage";
        String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        try {
            MessagesCache messagesCache = MessagesCaches.get(locale);
            if (messagesCache == null) {
                LOG.error("getMessage", "Messages cache not available for locale " + locale);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[getMessage]", "", "", " ",
                    "Messages cache not available for locale " + locale);
                locale = new Locale(defaultLanguage, defaultCountry);
                messagesCache = MessagesCaches.get(locale);
                if (messagesCache == null) {
                    return null;
                }
            }
            KeyArgumentVO keyArgumentVO = null;
            for (int i = 0; i < keyArgumentList.size(); i++) {
                keyArgumentVO = (KeyArgumentVO) keyArgumentList.get(i);
                if (i != 0) {
                    str += ", ";
                }
                str += MessageFormat.format(messagesCache.getProperty(keyArgumentVO.getKey()), keyArgumentVO.getArguments());

                if (LOG.isDebugEnabled()) {
                    LOG.debug("getMessage", "key: " + keyArgumentVO.getKey() + " args: " + keyArgumentVO.getArguments() + " str: " + str);
                }
            }
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[getMessage]", "", "", "", " Exception:" + e
                .getMessage());
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("getMessage", "Exiting str: " + str);
            }
        }
        return str;
    }

    /**
     * Is Valid Time
     * 
     * @param thetime
     *            String
     * @return boolean
     */
    public static boolean isValidTime(String thetime) {
        if (thetime == null) {
            return false;
        } else {
            if (thetime.indexOf(":") == -1) {
                thetime = thetime + ":00";
            }

            if (thetime.length() < 5) {
                return false;
            }
        }
        for (int i = 0; i < 5; i++) {
            if (i == 2) {
                if (thetime.charAt(i) != ':') {
                    return false;
                }
            } else {
                if (!Character.isDigit(thetime.charAt(i))) {
                    return false;
                }
            }
        }
        final int hr = Integer.parseInt(thetime.substring(0, 2));
        if (hr >= 24) {
            return false;
        }
        final int min = Integer.parseInt(thetime.substring(3, 5));
        if (min > 59) {
            return false;
        }
        return true;
    }

    /**
     * This method adds the no of days in the passed date
     * 
     * @param p_date
     * @param p_no_ofDays
     * @return Date
     */
    public static Date addDaysInUtilDate(java.util.Date p_date, int p_no_ofDays) {
        final Calendar cal = BTSLDateUtil.getInstance();
        cal.setTime(p_date);
        cal.add(Calendar.DATE, p_no_ofDays);
        return cal.getTime();
    }

    /**
     * converts java.sql.date to a string in indian format
     * 
     * @param Date
     *            to be converted
     * @return String in Indian Format (dd/MM/yyyy)
     */
    /**
     * @param d
     * @return
     * @throws ParseException
     */
    public static String sqlDateToDateYYYYString(java.sql.Date d) throws ParseException {

        final SimpleDateFormat sdf = new SimpleDateFormat("dd'/'MM'/'yyyy");
        sdf.setLenient(false); // this is required else it will convert
        final String dateString = sdf.format(d);
        return dateString;
    }

    /**
     * Report Date Format
     * 
     * @param strFormat
     *            String
     * @return String
     */
    public static String reportDateFormat(String strFormat) {
        String methodName = "reportDateFormat";
        if (LOG.isDebugEnabled()) {
	    LOG.debug("formatMessage", methodName + " and date passed is : " + strFormat);
		}
		String tempStr = "";
		String localeDate = PretupsI.EMPTY;
		String calenderType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CALENDAR_TYPE);
		if(!PretupsI.GREGORIAN.equalsIgnoreCase(calenderType)) {
			localeDate = BTSLDateUtil.getSystemLocaleDate(strFormat, "dd/MM/yyyy");			
		} else {
			localeDate = strFormat; 
		}
		if (!isNullString(localeDate)) {
		    strFormat = localeDate.trim();
		    final String dd = localeDate.substring(0, 2);
		    final String mm = localeDate.substring(3, 5);
		    final String yyyy = localeDate.substring(6);
		    tempStr = "Date(" + yyyy + "," + mm + "," + dd + ")";
		}// end of null str
		return tempStr;
    }
    
    /**
     * This method will the return the date in String in the 
     * @param rptDate
     * @return
     */
    public static String getDateFromRptDate(String rptDate) {
		String tempDate = PretupsI.EMPTY;
		String dateFormatCalJava = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA);
		if(!BTSLUtil.isNullString(rptDate)) {
			tempDate = rptDate.substring(rptDate.indexOf(PretupsI.ROUND_PARAN_START) + 1, rptDate.indexOf(PretupsI.ROUND_PARAN_END)).replace(",", "/");
			try {
				tempDate = BTSLUtil.getDateStringInFormat(tempDate, dateFormatCalJava);
			} catch (ParseException e) {
				LOG.error("getDateFromRptDate", " Exception " + e.getMessage());
			}
		}
		return tempDate;
    }
    
    public static String getDateFromRptDateStr(String rptDate) {
		String tempDate = PretupsI.EMPTY;
		String dateFormatCalJava = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA);
		if(!BTSLUtil.isNullString(rptDate)) {
			tempDate = rptDate.substring(rptDate.indexOf(PretupsI.ROUND_PARAN_START) + 1, rptDate.indexOf(PretupsI.ROUND_PARAN_END)).replace(",", "/");
			try {
				tempDate = BTSLUtil.getDateStringInFormat(tempDate, dateFormatCalJava);
			} catch (ParseException e) {
				LOG.error("getDateFromRptDate", " Exception " + e.getMessage());
			}
		}
		return "TO_DATE(\"" + tempDate +"\",\"" +  dateFormatCalJava + "\")";
    }
    
    public static String getDateFromRptDateTimeStr(String rptDate) {
		String tempDate = PretupsI.EMPTY;
		String dateFormatCalJava = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA);
		String dateTimeFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_TIME_FORMAT);
		if(!BTSLUtil.isNullString(rptDate)) {
			tempDate = rptDate.substring(rptDate.indexOf(PretupsI.ROUND_PARAN_START) + 1, rptDate.indexOf(PretupsI.ROUND_PARAN_END)).replace(",", "/");
			try {
				tempDate = BTSLUtil.getDateStringInFormat(tempDate, dateFormatCalJava);
			} catch (ParseException e) {
				LOG.error("getDateFromRptDate", " Exception " + e.getMessage());
			}
		}
		return "TO_DATE(\"" + tempDate +"\",\"" +  dateTimeFormat + "\")";
    }
 // end of reportDateFormat

    /**
     * calculate New Icc Id actual or swapped
     * Creation date: (18/08/04)
     * 
     * @return java.lang.String
     * @param iccid
     *            String
     * @param p_networkCode
     *            String
     * 
     */
    
    /**
     * @param iccId
     * @param p_networkCode
     * @return
     * @throws BTSLBaseException 
     */
    public static String calcIccId(String iccId, String p_networkCode) throws BTSLBaseException {
        // ICCID calculation is required in case STK is registered with ICCID
        // numbers, otherwise it is not required.
    	boolean stkRegIccid = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.STK_REG_ICCID);
        if (!stkRegIccid) {
            return iccId;
        }

        String result = null;
        final String oldIccId = iccId.trim();
        final int len = oldIccId.length();
        char pos1, pos2;
        String newStr = "";
       
        final String icicidCheckStr = (String) (PreferenceCache.getNetworkPrefrencesValue(PreferenceI.ICCID_CHECKSTRING, p_networkCode));
        String checkStr = null;
        if (!BTSLUtil.isNullString(icicidCheckStr)) {
        	checkStr = oldIccId.substring(0, icicidCheckStr.length());
		}
        	if (BTSLUtil.isNullString(icicidCheckStr) || icicidCheckStr.equals(checkStr)) {
        		if (len == 19) {
        			newStr = oldIccId.substring(0, 18);
        			pos1 = oldIccId.charAt(18);
        			newStr = newStr + "" + "F" + pos1;
        		} else if (len == 18) {
        			newStr = oldIccId.substring(0, 18);
        			newStr = newStr + "FF";
        		} else if (len == 20) {
        			newStr = oldIccId;
        		}
        		result = newStr;
        	} else {
        		char first, second;
        		for (int i = 0; i < 18; i = i + 2) {
        			first = oldIccId.charAt(i);
        			second = oldIccId.charAt(i + 1);
        			newStr = newStr + "" + second + "" + first;
        		}
        		if (len == 19) {
        			pos1 = oldIccId.charAt(18);
        			newStr = newStr + "" + "F" + pos1;
        		} else if (len == 18) {
        			newStr = newStr + "FF";
        		} else {
        			pos1 = oldIccId.charAt(18);
        			pos2 = oldIccId.charAt(19);
        			newStr = newStr + "" + pos2 + "" + pos1;
        		}
        		result = newStr;
        	}// end of else
        if (LOG.isDebugEnabled()) {
            LOG.debug("calcIccId", "ICC ID returned:" + result);
        }
        return result;
    }

    /**
     * method uploadFileToServer
     * This method is to upload the file to the server. This method will work
     * only in the STRUTS FRAMEWORK
     * 
     * @param p_formFile
     *            FormFile -- contains information of the file to be uploaded
     * @param p_dirPath
     *            String -- directory path of the server where to upload the
     *            file
     * @param p_contentType
     *            String -- contetnt type of the file to be checked
     * @param forward
     *            String -- where to forward on the error
     * @return boolean
     * @throws BTSLBaseException
     */
    
    /**
     * @param p_formFile
     * @param p_dirPath
     * @param p_contentType
     * @param forward
     * @param p_fileSize
     * @return
     * @throws BTSLBaseException
     */
   /* public static boolean uploadFileToServer(FormFile p_formFile, String p_dirPath, String p_contentType, String forward, long p_fileSize) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("uploadFileToServer",
                "Entered :p_formFile=" + p_formFile + ", p_dirPath=" + p_dirPath + ", p_contentType=" + p_contentType + ", forward=" + forward + ", p_fileSize=" + p_fileSize);
        }
        FileOutputStream outputStream = null;
        boolean returnValue = false;
        final String METHOD_NAME = "uploadFileToServer";
        // modified by Manisha(18/01/08) use singal try catch
        try {
            final File fileDir = new File(p_dirPath);
            if (!fileDir.isDirectory()) {
                fileDir.mkdirs();
            }
            if (!fileDir.exists()) {
                LOG.debug("uploadFileToServer", "Directory does not exist: " + fileDir + " ");
                throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "uploadfile.error.dirnotcreated", forward);
            }

            final File fileName = new File(p_dirPath, p_formFile.getFileName());

            // if file already exist then show the error message.
            if (p_formFile != null) {
                if (p_formFile.getFileSize() <= 0) {
                    throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "uploadfile.error.filesizezero", forward);
                } else if (p_formFile.getFileSize() > p_fileSize) {
                    throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "uploadfile.error.largefilesize", 0, new String[] { String.valueOf(p_fileSize) }, forward);
                }

                boolean contentTypeExist = false;
                if (p_contentType.contains(",")) {
                    final String temp[] = p_contentType.split(",");
                    for (int i = 0, j = temp.length; i < j; i++) {
                        if (p_formFile.getContentType().equalsIgnoreCase(temp[i].trim())) {
                            contentTypeExist = true;
                            break;
                        }
                    }
                } else if (p_formFile.getContentType().equalsIgnoreCase(p_contentType)) {
                    contentTypeExist = true;
                }

                if (contentTypeExist) {
                    if (fileName.exists()) {
                        throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "uploadfile.error.fileexists", forward);
                    }
                    outputStream = new FileOutputStream(fileName);
                    outputStream.write(p_formFile.getFileData());
                    returnValue = true;
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("uploadFileToServer", "File Uploaded Successfully");
                    }
                }
                // if file is not a text file show error message
                else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            "uploadFileToServer",
                            "Invalid content type: " + p_formFile.getContentType() + " required is p_contentType: " + p_contentType + " p_formFile.getFileName(): " + p_formFile
                                .getFileName());
                    }
                    throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "uploadfile.error.notrequiredcontent", forward);
                }
            }
            // if there is no such file then show the error message
            else {
                throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "uploadfile.error.nofile", forward);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            LOG.error("uploadFileToServer", "Exception " + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[uploadFileToServer]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "error.general.processing", forward);
        } finally {
        	try{
                if (outputStream!= null){
                	outputStream.close();
                }
              }
              catch (IOException e){
            	  LOG.error("An error occurred closing outputStream.", e);
              }
            if (LOG.isDebugEnabled()) {
                LOG.debug("uploadFileToServer", "Exit :returnValue=" + returnValue);
            }

        }
        return returnValue;
    }*/
  /*  public static boolean uploadFileToServer(FormFile p_formFile,String uploadFileName, String p_dirPath, String p_contentType, String forward, long p_fileSize) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("uploadFileToServer",
                "Entered :p_formFile=" + p_formFile + ", p_dirPath=" + p_dirPath + ", p_contentType=" + p_contentType + ", forward=" + forward + ", p_fileSize=" + p_fileSize);
        }
        FileOutputStream outputStream = null;
        boolean returnValue = false;
        final String METHOD_NAME = "uploadFileToServer";
        // modified by Manisha(18/01/08) use singal try catch
        try {
            final File fileDir = new File(p_dirPath);
            if (!fileDir.isDirectory()) {
                fileDir.mkdirs();
            }
            if (!fileDir.exists()) {
                LOG.debug("uploadFileToServer", "Directory does not exist: " + fileDir + " ");
                throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "uploadfile.error.dirnotcreated", forward);
            }

            final File fileName = new File(p_dirPath, uploadFileName);

            // if file already exist then show the error message.
            if (p_formFile != null) {
                if (p_formFile.getFileSize() <= 0) {
                    throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "uploadfile.error.filesizezero", forward);
                } else if (p_formFile.getFileSize() > p_fileSize) {
                    throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "uploadfile.error.largefilesize", 0, new String[] { String.valueOf(p_fileSize) }, forward);
                }

                boolean contentTypeExist = false;
                if (p_contentType.contains(",")) {
                    final String temp[] = p_contentType.split(",");
                    for (int i = 0, j = temp.length; i < j; i++) {
                        if (p_formFile.getContentType().equalsIgnoreCase(temp[i].trim())) {
                            contentTypeExist = true;
                            break;
                        }
                    }
                } else if (p_formFile.getContentType().equalsIgnoreCase(p_contentType)) {
                    contentTypeExist = true;
                }

                if (contentTypeExist) {
                    if (fileName.exists()) {
                        throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "uploadfile.error.fileexists", forward);
                    }
                    outputStream = new FileOutputStream(fileName);
                    outputStream.write(p_formFile.getFileData());
                    returnValue = true;
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("uploadFileToServer", "File Uploaded Successfully");
                    }
                }
                // if file is not a text file show error message
                else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            "uploadFileToServer",
                            "Invalid content type: " + p_formFile.getContentType() + " required is p_contentType: " + p_contentType + " p_formFile.getFileName(): " + p_formFile
                                .getFileName());
                    }
                    throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "uploadfile.error.notrequiredcontent", forward);
                }
            }
            // if there is no such file then show the error message
            else {
                throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "uploadfile.error.nofile", forward);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            LOG.error("uploadFileToServer", "Exception " + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[uploadFileToServer]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "error.general.processing", forward);
        } finally {
        	try{
                if (outputStream!= null){
                	outputStream.close();
                }
              }
              catch (IOException e){
            	  LOG.error("An error occurred closing outputStream.", e);
              }
            if (LOG.isDebugEnabled()) {
                LOG.debug("uploadFileToServer", "Exit :returnValue=" + returnValue);
            }

        }
        return returnValue;
    }*/
    /**
     * @param date
     * @return
     */
    public static int getHour(Date date) {
        final Calendar calendar = BTSLDateUtil.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * @param date
     * @return
     */
    public static int getMinute(Date date) {
        final Calendar calendar = BTSLDateUtil.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * This method is used to validate an IP address
     * 
     * @param ipAddress
     * @return
     */
    public static boolean isValidateIpAddress(String ipAddress) {
    	boolean ipv6Enabled = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IPV6_ENABLED);
    	if(ipv6Enabled){
    		return isIpAddress(ipAddress);
    	}else{
    	final StringTokenizer st = new StringTokenizer(ipAddress, ".");
        int count = 0;
        if (st.countTokens() != 4) {
            return false;
        }
        int counter = 0;
        for (int i = 0; i < ipAddress.length(); i++) {
            if (ipAddress.charAt(i) == '.') {
                counter++;
            }
        }
        if (counter != 3) {
            return false;
        }
        while (st.hasMoreTokens()) {
            try {
                final int part = Integer.parseInt(st.nextToken());
                if (count == 0 && part == 0) {
                    return false;
                }
                if (part > 255 || part < 0) {
                    return false;
                }
            } catch (NumberFormatException nfex) {
                return false;
            }
            count++;
        }
        return true;
    	}
    }

    /**
     * This method is used to validate Port number
     * 
     * @param port
     * @author akanksha
     * @return boolean
     */
    public static boolean isValidatePortNumber(String port) {

        try {
            final int p_port = Integer.parseInt(port);
            if (p_port > 65535 || p_port < 1) {
                return false;
            }
        } catch (NumberFormatException nfex) {
            return false;
        }

        return true;
    }

    /**
     * Method that will round a double number and return a String
     * 
     * @param number
     * @param afterDecimal
     * @return
     */
    public static String roundToStr(double number, int afterDecimal) {
        String result = null;
        try {
            String formatStr = "##########0.00";
            for (int i = 2; i < afterDecimal; i++) {
                formatStr += "#";
            }
            final DecimalFormat decFormat = new DecimalFormat(formatStr);
            result = decFormat.format(number);
        } catch (Exception ex) {
            LOG.errorTrace("roundToStr", ex);
        }
        return result;
    }

    /**
     * Method is to validate the string of allowed versions used in the
     * servicekeyword module
     * 
     * @param _allowedVersion
     * @return boolean
     */
    public static boolean isValidVersionString(String _allowedVersion) {
        if (!BTSLUtil.isNullString(_allowedVersion)) {
            if (_allowedVersion.contains("..") || _allowedVersion.contains(",.") || _allowedVersion.contains(".,") || _allowedVersion.contains(",,")) {
                return false;
            } else {
                final String[] versionArr = _allowedVersion.split(",");
                boolean versionEntered = false;
                for (int i = 0, j = versionArr.length; i < j; i++) {
                    versionEntered = true;
                    if ((versionArr[i].indexOf('.') <= 0) || versionArr[i].charAt(versionArr[i].length() - 1) == '.' || versionArr[i].indexOf('.') != versionArr[i].lastIndexOf('.')) {
                        return false;
                    }
                }
                if (!versionEntered) {
                    return false;
                } else {
                    return true;
                }
            }
        } else {
            return true;
        }
    }

    /**
     * returns a default password text
     * 
     * @param String
     *            str
     * @return String (*****)
     */
    /**
     * @param str
     * @return
     */
    public static String getDefaultPasswordText(String str) {
        String value = "";
        if (!isNullString(str)) {
            for (int i = 0, j = str.length(); i < j; i++) {
                value = value + "*";
            }
        }
        return value;
    }

    /**
     * returns a default password text
     * 
     * @param String
     *            str
     * @return String (*****)
     */
    public static String getDefaultPasswordNumeric(String str) {
        String value = "";
        if (!isNullString(str)) {
            for (int i = 0, j = str.length(); i < j; i++) {
                value = value + "0";
            }
        }
        return value;
    }

    /**
     * @param hour
     * @param minute
     * @return
     */
    public static String getTimeinHHMM(int hour, int minute) {
        String time = "";

        if (hour > 0 && hour > 9) {
            time = String.valueOf(hour);
        } else if (hour > 0 && hour < 10) {
            time = "0" + String.valueOf(hour);
        } else {
            time = "00";
        }

        if (minute > 0 && minute > 9) {
            time = time + ":" + String.valueOf(minute);
        } else if (minute > 0 && minute < 10) {
            time = time + ":0" + String.valueOf(minute);
        } else {
            time = time + ":00";
        }

        if (time.length() == 0) {
            time = "00:00";
        }
        return time;
    }

    /**
     * Method validateSMSPinConsecutive.
     * This validation method is to validate the sms pin should not be
     * consecutive number.
     * 
     * @param p_smsPin
     *            java.lang.String
     * @return boolean
     */
    public static boolean validateSMSPinConsecutive(String p_smsPin) {
        boolean validate = true;
      
        if (p_smsPin == null || p_smsPin.length() == 0) {
            return true;
        }
        int ctr = 0, j = 0;
        char pos1 = 0, pos = 0;
        // iterates thru the p_smsPin and validates that the number is neither
        // in 123456 or 121212
        for (int i = 0; i < p_smsPin.length(); i++) {
            pos = p_smsPin.charAt(i);

            if (i < p_smsPin.length() - 1) {
                pos1 = p_smsPin.charAt(i + 1);
            }

            j = pos1;
            if (j == pos + 1 || j == pos - 1) {
                ctr++;
            }
        }

        if (ctr == (p_smsPin.length() - 1)) {
            validate = false;
        } else {
            validate = true;
        }
        return validate;
    }

    /**
     * Method validateSMSPinSameDigit.
     * This validation method is to validate the sms pin should not the same
     * digit.
     * 
     * @param p_smsPin
     *            java.lang.String
     * @return boolean
     */
    public static boolean validateSMSPinSameDigit(String p_smsPin) {
        boolean validate = true;
       
        if (p_smsPin == null || p_smsPin.length() == 0) {
            return true;
        }

        // check the value with the default numeric value
        String defaultPin = BTSLUtil.getDefaultPasswordNumeric(p_smsPin);
        if (defaultPin.equals(p_smsPin)) {
            return true;
        }

        // check the value with the default text value
        defaultPin = BTSLUtil.getDefaultPasswordText(p_smsPin);
        if (defaultPin.equals(p_smsPin)) {
            return true;
        }

        int count = 0;
        char pos1 = 0, pos = 0;
        // iterates thru the p_smsPin and validates that the number is neither
        // in 444444 or 11111
        for (int i = 0; i <  p_smsPin.length(); i++) {
            pos = p_smsPin.charAt(i);

            if (i < p_smsPin.length() - 1) {
                pos1 = p_smsPin.charAt(i + 1);
            }

            if (pos == pos1) {
                count++;
            }
        }
        if (count == p_smsPin.length()) {
            validate = false;
        } else {
            validate = true;
        }
        return validate;
    }

    /**
     * by ved
     * Method getNoOfDaysInMonth.
     * This method return no of days in a month.
     * 
     * @param date
     *            java.lang.String
     * @param format
     *            java.lang.String
     * @return int
     */
    public static int getNoOfDaysInMonth(String date, String format) throws ParseException {
        int days;
        Date temdate;
        temdate = BTSLUtil.getDateFromDateString(date, format);
        String tempDate = BTSLDateUtil.getSystemLocaleDate(temdate,PretupsI.DATE_FORMAT_DDMMYYYY);
        String dateArr[] = null;
        final Calendar calender = BTSLDateUtil.getInstance();
        if(!BTSLUtil.isNullString(tempDate)) {
        	dateArr =	tempDate.split(PretupsI.FORWARD_SLASH);
        }
        if(dateArr.length > 2) {
        	calender.set(Integer.parseInt(dateArr[2]), Integer.parseInt(dateArr[1])-1, Integer.parseInt(dateArr[0]));
        } else {
        	final int day = temdate.getDate();
            final int month = temdate.getMonth();
            final int year = temdate.getYear() + 1900;
            calender.set(year, month, day);
        }
        days = calender.getActualMaximum(Calendar.DAY_OF_MONTH);
        return days;
    }

    /**
     * @return
     * @throws ParseException
     */
    public static String currentDateTimeFormatString() throws ParseException {
        final java.util.Date mydate = new java.util.Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        final String dateString = sdf.format(mydate);
        return dateString;
    }

    /**
     * Is the PIN valid
     * if(result == -1) sms pin same digit
     * if(result ==1) sms pin consecutive
     * 
     * @param p_smsPin
     * @return
     */
    public static int isSMSPinValid(String p_smsPin) {
        int count = 0;
        int ctr = 0;
        int j;
        char pos1 = 0;
        char pos ;
        int result = 0;
        // iterates thru the p_smsId and validates that the number is neither in
        // 444444 or 123456 format 121212
        for (int i = 0; i < p_smsPin.length(); i++) {
            pos = p_smsPin.charAt(i);

            if (i < p_smsPin.length() - 1) {
                pos1 = p_smsPin.charAt(i + 1);
            }

            j = pos1;
            if (pos == pos1) {
                count++;
            } else if (j == pos + 1 || j == pos - 1) {
                ctr++;
            }
        }

        if (count == p_smsPin.length()) {
            return result = -1;
        } else if (ctr == (p_smsPin.length() - 1)) {
            return result = 1;
        } else {
            return result;
        }
    }

    /**
     * Method to validate the SMS PIn sent in the request
     * 
     * @param p_pin
     * @throws BTSLBaseException
     */
    public static void validatePIN(String p_pin) throws BTSLBaseException {
    	int minSmsPinLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SMS_PIN_LENGTH))).intValue();
    	int maxSmsPinLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH))).intValue();
        if (isNullString(p_pin)) {
            throw new BTSLBaseException("BTSLUtil", "validatePIN", PretupsErrorCodesI.PIN_INVALID);
        } else if (!isNumeric(p_pin)) {
            throw new BTSLBaseException("BTSLUtil", "validatePIN", PretupsErrorCodesI.NEWPIN_NOTNUMERIC);
        } else if (p_pin.length() < minSmsPinLength || p_pin.length() > maxSmsPinLength) {
            final String msg[] = { String.valueOf(minSmsPinLength), String.valueOf(maxSmsPinLength) };
            throw new BTSLBaseException("BTSLUtil", "validatePIN", PretupsErrorCodesI.PIN_LENGTHINVALID, 0, msg, null);
        } else {
            final int result = isSMSPinValid(p_pin);
            if (result == -1) {
                throw new BTSLBaseException("BTSLUtil", "validatePIN", PretupsErrorCodesI.PIN_SAMEDIGIT);
            } else if (result == 1) {
                throw new BTSLBaseException("BTSLUtil", "validatePIN", PretupsErrorCodesI.PIN_CONSECUTIVE);
            }
        }
    }

    /*
     * 
     * @author mohit.goel
     * 
     * Method retuen the Locale object
     */
    /**
     * @param request
     * @return
     */
    public static Locale getBTSLLocale(HttpServletRequest request) {
        final Locale locale = (Locale) request.getSession().getAttribute(Globals.LOCALE_KEY);
        if (locale == null) {
            return request.getLocale();
        } else {
            return locale;
        }
    }

    /*
     * 
     * @author ved.sharma
     * dif_date Date
     * howmanyday int (+,-)both -back, +forward
     * Method retuen the difference date
     */
    /**
     * @param dif_date
     * @param howmanyday
     * @return
     */
    public static Date getDifferenceDate(Date dif_date, int howmanyday) {
    	final String methodName = "getDifferenceDate";
    	Date d = null;
		final Calendar worldTour = BTSLDateUtil.getCalendar(dif_date.getYear() + 1900, dif_date.getMonth(), dif_date.getDate());
        worldTour.add(Calendar.DATE, howmanyday);
        LogFactory.printLog(methodName, "worldTour.getTime() : "+worldTour.getTime(), LOG);
        d = worldTour.getTime();
        return d;
    }

    /**
     * split
     * 
     * @param p_inStr
     * @param p_saperator
     * @return
     */
    public static String[] split(String p_inStr, String p_separator) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("split", " Entered p_inStr=" + BTSLUtil.maskParam(p_inStr) + " p_separator=" + p_separator);
        }
        final ArrayList list = new ArrayList();
        String[] arr = null;
        if (p_inStr != null) {
            if (!BTSLUtil.isNullString(p_separator)) {
                int saparetorIndex = 0;
                int startIndex = 0;
                saparetorIndex = p_inStr.indexOf(p_separator, startIndex);
                if (saparetorIndex == -1) {
                    list.add(p_inStr.trim());
                }
                while (saparetorIndex != -1) {
                    list.add((p_inStr.substring(startIndex, saparetorIndex)).trim());
                    startIndex = saparetorIndex + 1;
                    saparetorIndex = p_inStr.indexOf(p_separator, startIndex);
                    if (saparetorIndex == -1 && startIndex <= p_inStr.length() && !BTSLUtil.isNullString(p_inStr.substring(startIndex))) {
                        list.add((p_inStr.substring(startIndex)).trim());
                    }
                }
                arr = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    arr[i] = (String) list.get(i);
                }
            } else {
                arr = p_inStr.split(p_separator);
                for (int i = 0, j = 0; i < arr.length; i++) {
                    if (!BTSLUtil.isNullString(arr[i])) {
                        list.add(j, arr[i]);
                        j++;
                    }
                }
                arr = null;
                arr = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    arr[i] = (String) list.get(i);
                }
            }
        }
        return arr;
    }

    /**
     * This method checks whether period has been changed between two dates
     * 
     * @param p_oldDate
     * @param p_newDate
     * @param p_param
     *            : 1 for Day Change, 2 For week Change, 3 for month Change , 4
     *            for Year Change
     * @return
     */
    public static boolean isPeriodChangeBetweenDates(java.util.Date p_oldDate, java.util.Date p_newDate, int p_param) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("isPeriodChangeBetweenDates", "Entered with p_oldDate=" + p_oldDate + " p_newDate=" + p_newDate + " p_param=" + p_param);
        }
        boolean isCounterChange = false;

        if (p_oldDate != null) {
            final Calendar cal = BTSLDateUtil.getInstance();
            cal.setTime(p_newDate);
            final int presentDay = cal.get(Calendar.DAY_OF_MONTH);
            final int presentWeek = cal.get(Calendar.WEEK_OF_MONTH);
            final int presentMonth = cal.get(Calendar.MONTH);
            final int presentYear = cal.get(Calendar.YEAR);
            cal.setTime(p_oldDate);
            final int lastWeek = cal.get(Calendar.WEEK_OF_MONTH);
            final int lastTrxDay = cal.get(Calendar.DAY_OF_MONTH);
            final int lastTrxMonth = cal.get(Calendar.MONTH);
            final int lastTrxYear = cal.get(Calendar.YEAR);
            // Day Change
            if (p_param == PERIOD_DAY) {
                if (presentDay != lastTrxDay || presentMonth != lastTrxMonth || presentYear != lastTrxYear) {
                    isCounterChange = true;
                } 
            }
            // Week Change
            else if (p_param == PERIOD_WEEK) {
                if (presentWeek != lastWeek || presentMonth != lastTrxMonth || presentYear != lastTrxYear) {
                    isCounterChange = true;
                } 
            }
            // Month Change
            else if (p_param == PERIOD_MONTH) {
                if (presentMonth != lastTrxMonth || presentYear != lastTrxYear) {
                    isCounterChange = true;
                } 
            }
            // Year Change
            else if (p_param == PERIOD_YEAR) {
                if (presentYear != lastTrxYear) {
                    isCounterChange = true;
                }
            }
        } else {
            isCounterChange = true;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("isPeriodChangeBetweenDates", "Exiting with isCounterChange=" + isCounterChange);
        }
        return isCounterChange;
    }

    /**
     * Get Message from Messages.props on the basis of locale and key that is
     * appented to key
     * If message not found for key_appendKey then message corresponding to key
     * is send
     * 
     * @param locale
     * @param key
     * @param args
     * @param appendKey
     * @return
     */
    public static String getMessage(Locale locale, String key, String[] args, String appendKey) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getMessage", "Entered locale " + locale + "    key: " + key + " args: " + args + " appendKey: " + appendKey);
        }
        String message = null;
        final String METHOD_NAME = "getMessage";
        try {
            message = getMessage(locale, key + "_" + appendKey, args);
            if (isNullString(message)) {
                message = getMessage(locale, key, args);
            }
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[getMessage]", "", "", "", " Exception:" + e
                .getMessage());
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("getMessage", "Exiting message : " + message);
            }
        }
        return message;
    }

    /**
     * Escape any single quote characters that are included in the specified
     * message string.
     * 
     * @param string
     *            The string to be escaped
     */
    /**
     * @param string
     * @return
     */
    public static String escape(String string) {
        if ((string == null) || (string.indexOf('\'') < 0)) {
            return string;
        }

        final int n = string.length();
        final StringBuffer sb = new StringBuffer(n);

        for (int i = 0; i < n; i++) {
            final char ch = string.charAt(i);

            if (ch == '\'') {
                sb.append('\'');
            }

            sb.append(ch);
        }

        return sb.toString();

    }

    /**
     * This method swap the values of the Iccid.
     * 
     * @param iccId
     *            String
     * @param p_networkCode
     *            String
     * @return String
     * 
     */
    public static String getByteSwappedKey(String iccId, String p_networkCode) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getByteSwappedKey", " Entered iccId : " + iccId);
        }
        // ICCID calculation is required in case STK is registered with ICCID
        // numbers, otherwise it is not required.
        boolean stkRegIccid = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.STK_REG_ICCID);
        if (!stkRegIccid) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("getByteSwappedKey", "Exit final result : " + iccId);
            }
            return iccId;
        }
        String result = null;
        if (BTSLUtil.isNullString(iccId)) {
            result = "";
        } else {
            final String oldIccId = iccId.trim();
            final int len = oldIccId.length();
            char pos1, pos2;
            String newStr = "";
            char first, second;
            final String icicidCheckStr = (String) (PreferenceCache.getNetworkPrefrencesValue(PreferenceI.ICCID_CHECKSTRING, p_networkCode));
            String checkStr = null;
            String newIcicidCheckStr = "";
          
            if (!BTSLUtil.isNullString(icicidCheckStr)) {
                checkStr = oldIccId.substring(0, icicidCheckStr.length());
                for (int i = 0; i <  icicidCheckStr.length(); i = i + 2) {
                    first = icicidCheckStr.charAt(i);
                    second = icicidCheckStr.charAt(i + 1);
                    newIcicidCheckStr = newIcicidCheckStr + "" + second + "" + first;
                }
            }
            if (BTSLUtil.isNullString(newIcicidCheckStr) || newIcicidCheckStr.equals(checkStr)) {
                result = iccId;
            } else {
                for (int i = 0; i < 18; i = i + 2) {
                    first = oldIccId.charAt(i);
                    second = oldIccId.charAt(i + 1);
                    newStr = newStr + "" + second + "" + first;
                }
                if (len == 20) {
                    pos1 = oldIccId.charAt(18);
                    pos2 = oldIccId.charAt(19);
                   if ("F".equalsIgnoreCase(Character.toString(pos1))) {
                        newStr = newStr + "" + pos2;
                    } else {
                        newStr = newStr + "" + pos2 + "" + pos1;
                    }
                }
                result = newStr;
            }// end of else
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("getByteSwappedKey", "Exit final result : " + result);
        }
        return result;
    }

    /**
     * This method will check the array for null.
     * If all the entries in array is null then return true otherwise return
     * false
     * 
     * @param p_arr
     * @return
     */
    public static boolean isNullArray(String[] p_arr) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("isNullArray", "Entered p_arr: " + p_arr);
        }
        boolean isNull = true;
        if (p_arr != null) {
            for (int i = 0, j = p_arr.length; i < j; i++) {
                if (!isNullString(p_arr[i])) {
                    isNull = false;
                    break;
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("isNullArray", "Exited isNull: " + isNull);
        }
        return isNull;
    }


	public static String encode(byte[] data) {
           //uncomment below two lines for jdk8 or lower
      //     sun.misc.BASE64Encoder encode = new sun.misc.BASE64Encoder(); //return
      //     return encode.encode(data);
           
           //uncomment below two lines for openjdk11 or above
           java.util.Base64.Encoder enc = java.util.Base64.getEncoder();
        return enc.encodeToString(data);
    }

	public static byte[] decodeBuffer(String data) throws IOException {
		// uncomment below two lines for jdk8 or lower
//		final sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
//		return decoder.decodeBuffer(data);

		// uncomment below two lines for openjdk11 or above
		 java.util.Base64.Decoder dec = java.util.Base64.getDecoder();
		 return dec.decode(data);
	}

    /**
     * encodeSpecial
     * This methos is changed by ankit zindal on date 25/06/07 for taking
     * encoding scheme from sysetm preferences
     * The methos is also optimized by commenting extra code.
     * 
     * @param p_messageToEncode
     * @param p_encodeAlphanumeric
     * @param p_encodingToUse
     *            //ChangeID=LOCALEMASTER
     * @return
     */
    public static String encodeSpecial(String p_messageToEncode, boolean p_encodeAlphanumeric, String p_encodingToUse) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("encodeSpecial",
                "Entered p_messageToEncode: " + p_messageToEncode + " p_encodeAlphanumeric:" + p_encodeAlphanumeric + "encodingToUse:" + p_encodingToUse);
        }
        final String strNumber = p_messageToEncode;
        String strFinalValue = "";
        String strUnicodeValue = null;
        final String METHOD_NAME = "encodeSpecial";

        // ChangeID=LOCALEMASTER
        // Use the encoding obtained from the locale master table
        String encodingToUse = p_encodingToUse;
        // If Not defined then use the default encoding
        if (BTSLUtil.isNullString(encodingToUse)) {
            encodingToUse = "UTF16";
        }
        for (int intCount = 0, j = p_messageToEncode.length(); intCount < j; intCount++) {
            // if argument is numeric then encoding is diff and if other
            // language then diff.
            if (!(strNumber.charAt(intCount) >= '0' && strNumber.charAt(intCount) <= '9')) {
                if ((strNumber.charAt(intCount) >= 'A' && strNumber.charAt(intCount) <= 'Z') || (strNumber.charAt(intCount) >= 'a' && strNumber.charAt(intCount) <= 'z')) {
                    strUnicodeValue = Integer.toString(strNumber.charAt(intCount), 16);
                    strFinalValue = strFinalValue + "%00%" + strUnicodeValue;
                } else {
                    try {
                        strUnicodeValue = URLEncoder.encode(String.valueOf(strNumber.charAt(intCount)), encodingToUse);
                    } catch (Exception e) {
                        LOG.errorTrace(METHOD_NAME, e);
                    }
                    strUnicodeValue = strUnicodeValue.replaceAll("%FE%FF", "");
                    strUnicodeValue = strUnicodeValue.replaceAll("%00%7B", "{");
                    strUnicodeValue = strUnicodeValue.replaceAll("%00%7D", "}");
                    strUnicodeValue = strUnicodeValue.replaceAll("%FE%FF%00%7E", "~");
                    strUnicodeValue = strUnicodeValue.replaceAll("\\+", "%00%20");

                    strUnicodeValue = strUnicodeValue.replaceAll("\\.", "%00%2E");
                    strUnicodeValue = strUnicodeValue.replaceAll("\\-", "%00%2D");
                    strFinalValue = strFinalValue + strUnicodeValue;
                }

            } else if (!p_encodeAlphanumeric && intCount > 0 && ((strNumber.charAt(intCount) >= '0' && strNumber.charAt(intCount) <= '9') && ((strNumber.charAt(intCount - 1) >= 'A' && strNumber
                .charAt(intCount - 1) <= 'Z') || (strNumber.charAt(intCount - 1) >= 'a' && strNumber.charAt(intCount - 1) <= 'z')))) {
                strUnicodeValue = Integer.toString(strNumber.charAt(intCount), 16);
                strFinalValue = strFinalValue + "%00%" + strUnicodeValue;
            } else {
                if (p_encodeAlphanumeric) {
                    strUnicodeValue = Integer.toString(strNumber.charAt(intCount), 16);
                    strFinalValue = strFinalValue + "%00%" + strUnicodeValue;
                    
                } else {
                    strFinalValue = strFinalValue + String.valueOf(strNumber.charAt(intCount));
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("encodeSpecial", "Exiting strFinalValue: " + strFinalValue);
        }

        return strFinalValue;
    }

    /**
     * Get DateTime String From Date
     * 
     * @param date
     * @return
     * @throws ParseException
     */
    public static String getFileNameStringFromDate(Date date) throws ParseException {
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        final SimpleDateFormat sdf1 = new SimpleDateFormat("HHmmss");
        String datepart = sdf.format(date);
        String timePart = sdf1.format(date);
        sdf.setLenient(false); // this is required else it will convert
        sdf1.setLenient(false); // this is required else it will convert
        return BTSLDateUtil.getSystemLocaleDate(datepart, "dd/MM/yy")
                                .replace(PretupsI.FORWARD_SLASH, PretupsI.EMPTY) + "_" + timePart;
    }

    /**
     * Validates identification number for numeric and MIN and MAX length
     * Change ID=ACCOUNTID
     * 
     * @param mobileno
     * @return
     */
    /**
     * @param p_identificationNumber
     * @return
     */
    public static boolean isValidIdentificationNumber(String p_identificationNumber) {
        final int strLength = p_identificationNumber.length();
        if (LOG.isDebugEnabled()) {
            LOG.debug("isValidIdentificationNumber", "strLength= " + strLength);
        }
        int minMsisdnLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue();
        int maxMsisdnLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue();
        int minIdentificationNumberLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_IDENTIFICATION_NUMBER_LENGTH))).intValue();
        int maxIdentificationNumberLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_IDENTIFICATION_NUMBER_LENGTH))).intValue();
        String identificationNumberValType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.IDENTIFICATION_NUMBER_VAL_TYPE);
        boolean alphaIdNumAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALPHA_ID_NUM_ALLOWED);
        
        final String validationType = identificationNumberValType;
        int allowedMinLength = 0;
        int allowedMaxLength = 0;
        final boolean alphaNumericIDAllowed = alphaIdNumAllowed;
        if (PretupsI.MSISDN_VALIDATION.equals(validationType)) {
            allowedMinLength = minMsisdnLength;
            allowedMaxLength = maxMsisdnLength;
        } else if (PretupsI.OTHER_VALIDATION.equals(validationType)) {
            allowedMinLength = minIdentificationNumberLength;
            allowedMaxLength = maxIdentificationNumberLength;
        } else if (PretupsI.BOTH_VALIDATION.equals(validationType)) {
            allowedMinLength = minMsisdnLength < minIdentificationNumberLength ? minMsisdnLength : minIdentificationNumberLength;
            allowedMaxLength = maxMsisdnLength > maxIdentificationNumberLength ? maxMsisdnLength : maxIdentificationNumberLength;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("isValidIdentificationNumber", "allowedMinLength= " + allowedMinLength + " allowedMaxLength=" + allowedMaxLength);
        }
        if (strLength < allowedMinLength || strLength > allowedMaxLength) {
            return false;
        }
        if (PretupsI.MSISDN_VALIDATION.equals(validationType) || !alphaNumericIDAllowed) {
            for (int i = 0; i < strLength; i++) {
                if (!((Character.isDigit(p_identificationNumber.charAt(i))) || (p_identificationNumber.charAt(i) == '+'))) {
                    return false;
                }
            }
        } else {
            for (int i = 0; i < strLength; i++) {
                if (Character.isSpaceChar(p_identificationNumber.charAt(i)) || !((Character.isLetterOrDigit(p_identificationNumber.charAt(i))) || (p_identificationNumber
                    .charAt(i) == '+'))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @param p_string
     * @return
     */
    public static boolean isLetterOrDigit(String p_string) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("isLetterOrDigit", "p_string= " + p_string);
        }
        if (p_string != null) {
            final int strLength = p_string.length();
            for (int i = 0; i < strLength; i++) {
                if (Character.isSpaceChar(p_string.charAt(i)) || !(Character.isLetterOrDigit(p_string.charAt(i)))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Method: getDateFromVOMSDateString
     * this method is used in classes PretupsValidator,
     * 
     * Get Date From VOMS Date String
     * 
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static Date getDateFromVOMSDateString(String dateStr) throws ParseException {
    	String vomsDateFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_DATE_FORMAT);
        String format = vomsDateFormat;
        if (isNullString(format)) {
            format = PretupsI.DATE_FORMAT;
        }
        if (format.length() != dateStr.length()) {
            throw new ParseException(format, 0);
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false); // this is required else it will convert
        return sdf.parse(dateStr);
    }

    /**
     * Method: getVomsDateStringFromDate
     * this method is used in class VomsProductAction in method displayDate ,
     * Get VOMS Date String From Date
     * 
     * @param date
     * @return
     * @throws ParseException
     */
    public static String getVomsDateStringFromDate(Date date) throws ParseException {
    	String vomsDateFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_DATE_FORMAT);
        String format = vomsDateFormat;
        if (isNullString(format)) {
            format = PretupsI.DATE_FORMAT;
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false); // this is required else it will convert
        return sdf.format(date);
    }

    /**
     * Get Document Year
     * Creation date: (11/01/02 8:52:43 AM)
     * 
     * @return int
     */
    public static int getDocumentYear() {
        final Calendar getRight = BTSLDateUtil.getInstance();
        final int month = getRight.get(Calendar.MONTH);
        final int year = getRight.get(Calendar.YEAR);
        if ((month >= 0) && (month <= 2)) {
            return year - 1;
        } else {
            return year;
        }
    }

    /**
     * Date : Apr 16, 2007
     * Discription :This method remove the dublicate strings array and return
     * unique sorted string array
     * Method : removeDuplicatesString
     * 
     * @param duplicates
     * @return String[]
     * @author ved.sharma
     */
    public static String[] removeDuplicatesString(String[] duplicates) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("removeDuplicatesString", "Entered duplicates=" + duplicates);
        }
        String[] unique = null;
        final String METHOD_NAME = "removeDuplicatesString";
        try {
            Arrays.sort(duplicates);
            int k = 0;
            duplicates[k] = duplicates[k].trim();
            for (int i = 0; i < duplicates.length; i++) {
                if (i > 0 && (duplicates[i].trim()).equals(duplicates[i - 1].trim())) {
                    continue;
                }
                duplicates[k++] = duplicates[i].trim();
            }
            unique = new String[k];
            System.arraycopy(duplicates, 0, unique, 0, k);
        } catch (Exception e) {
            LOG.error("removeDuplicatesString", "Exception " + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("removeDuplicatesString", "Exit unique=" + unique);
        }
        return unique;

    }

    /**
     * Date : July 05, 2007
     * Discription :This method text area in a jsp page
     * Method : validateTextArea
     * 
     * @param textAreaString
     * @param textAreaMaxAllowedSize
     * @param messageKey
     * @param forwardName
     * @return BTSLMessages
     * @author ashish kumar srivastav
     */
    /**
     * @param textAreaName
     * @param textAreaString
     * @param textAreaMaxAllowedSize
     * @param messageKey
     * @param forwardName
     * @return
     */
    public static BTSLMessages validateTextArea(String textAreaName, String textAreaString, int textAreaMaxAllowedSize, String messageKey, String forwardName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("validateTextArea", "Entered");
        }
        final String arg[] = new String[2];
        arg[0] = textAreaName;
        arg[1] = Integer.toString(textAreaMaxAllowedSize);
        final BTSLMessages btslMessage = new BTSLMessages(messageKey, arg, forwardName);
        return btslMessage;
    }

    /**
     * To check String have at least one character value or not.
     * 
     * @param str
     * @return boolean if contains character returns true else false;
     */
    public static boolean containsChar(String str) {

        boolean flag = false;
        if (str != null) {
            final char arr[] = str.toCharArray();

            for (int i = 0; i < arr.length; i++) {
                if ((arr[i] >= 'A' && arr[i] <= 'Z') || (arr[i] >= 'a' && arr[i] <= 'z')) {
                    return flag = true;
                }
            }
        }
        return flag;
    }

    /**
     * @param p_email
     * @return
     */
    public static boolean validateEmailID(String p_email) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("validateEmailID", "Entered p_email=" + p_email);
        }
        boolean matchFound = false;
        final String METHOD_NAME = "validateEmailID";
        try {
            // Set the email pattern string  ".+@.+\\.[a-z]+"
        	final Pattern p = Pattern.compile("^[a-zA-Z0-9_\\.-]+@([a-zA-Z0-9]+\\.)+[a-zA-Z]{2,6}$");
            // Match the given string with the pattern
            final Matcher m = p.matcher(p_email);
            // check whether match is found
            matchFound = m.matches();
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
            LOG.error("validateEmailID", "Exception " + e.getMessage());
            return matchFound = false;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("validateEmailID", "Exit matchFound=" + matchFound);
        }
        return matchFound;
    }

    /**
     * Date : Nov 20, 2007
     * Discription :
     * Method : displayDomainList
     * 
     * @param p_domainList
     * @return ArrayList
     * @author ved.sharma
     */
    public static ArrayList displayDomainList(List p_domainList) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("displayDomainList", "Entered ");
        }
        final ArrayList list = new ArrayList();
        if (p_domainList != null) {
            ListValueVO listValueVO = null;
            for (int i = 0, j = p_domainList.size(); i < j; i++) {
                listValueVO = (ListValueVO) p_domainList.get(i);
                if (PretupsI.YES.equalsIgnoreCase(listValueVO.getStatusType())) {
                    list.add(listValueVO);
                }
            }

        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("displayDomainList", "Exit ");
        }
        return list;
    }

    /**
     * @author santanu.mohanty
     *         This method is used for decrypt password over the network
     * @param pass
     *            String
     * @return String
     */
    /**
     * @param p_password
     * @return
     */
    public static String decryptPasswordOverRequest(String p_password) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("decryptPasswordOverRequest", "Entered " + p_password);
        }
        p_password = p_password.trim();
        final StringBuilder sbu = new StringBuilder();
        final StringBuilder finalPassword = new StringBuilder();

        if (!isNullString(p_password)) {

            for (int counter = p_password.length(); counter > 0; counter--) {
                sbu.append(p_password.substring(counter - 1, counter));
            }
            final String[] words = sbu.toString().split(" ");
            int dpass = 0;
            // char temp;

            for (int j = 1; j <= words.length; j++) {
                dpass = Integer.parseInt(words[j - 1]) / j;
                // temp = (char) dpass;
                finalPassword.append(String.valueOf(Character.toChars(dpass)));
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("decryptPasswordOverRequest", "Exit ");
        }
        return finalPassword.toString();
    }

    /**
     * returns a default password text
     * 
     * @param String
     *            str
     * @return String (*****)
     */
    /**
     * @param str
     * @return
     */
    public static String getDefaultPasswordTextOverHttp(String str) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getDefaultPasswordTextOverHttp", "Entered str" + str);
        }
        String value = "";
        if (!isNullString(str)) {
            for (int i = 0, j = str.length(); i < j; i++) {
                value = value + "*";
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("getDefaultPasswordTextOverHttp", "Exit value" + value);
        }
        return value;
    }

    /**
     * isTimeExpired method gives the expiry status
     * 
     * @author santanu.mohanty
     * @param creationDate
     * @param expPeriod
     * @return boolean
     */
    public static boolean isTimeExpired(Date creationDate, long expPeriod) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("isTimeExpired", "Entered expPeriod" + expPeriod);
        }
        boolean expiryStatus = false;
        final Calendar calendar1 = BTSLDateUtil.getInstance();
        final Calendar calendar2 = BTSLDateUtil.getInstance();
        calendar1.setTimeInMillis(creationDate.getTime());
        calendar2.setTimeInMillis(System.currentTimeMillis());
        final long milliseconds1 = calendar1.getTimeInMillis();
        final long milliseconds2 = calendar2.getTimeInMillis();
        final long diff = milliseconds2 - milliseconds1;
        final long diffMinutes = diff / (60 * 1000);

        if (diffMinutes >= expPeriod) {
            expiryStatus = true;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("isTimeExpired", "Exit expiryStatus" + expiryStatus);
        }
        return expiryStatus;
    }

    /**
     * Date : Feb 26, 2008
     * Discription :
     * Method : ipPort
     * 
     * @param p_basePath
     * @param p_ipPort
     * @param p_ipPortStr
     * @return String[]
     */
    /**
     * @param p_basePath
     * @param p_ipPort
     * @param p_ipPortStr
     * @param p_context
     * @return
     */
    public static String[] scheduleRptIPs(String p_basePath, String p_ipPort, String p_ipPortStr, String p_context) {

        final String METHOD_NAME = "scheduleRptIPs";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered basePath=" + p_basePath + ", p_ipPort=" + p_ipPort + ", p_ipPortStr=" + p_ipPortStr + " p_context=" + p_context);
        }
        InputStream ins = null;
        HttpURLConnection con = null;
        String scheduleRptIPsBasePath[] = null;
        String scheduleRptIPsArr[] = null;
        String temp = null;

        try {
            scheduleRptIPsArr = p_ipPortStr.split(",");
            scheduleRptIPsBasePath = new String[2];
            if (BTSLUtil.isNullString(p_context)) {
                p_context = "crystal";
            }
            for (int i = 0; i < scheduleRptIPsArr.length; i++) {
                try {
                    temp = p_basePath;
                    temp = temp.replace(p_ipPort, scheduleRptIPsArr[i]);
                    temp += p_context;

                    final URL myurl = new URL(temp);
                    con = (HttpURLConnection) myurl.openConnection();
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.setUseCaches(false);
                    con.setRequestProperty("Content-Type", "text/xml");
                    con.setAllowUserInteraction(true);
                    con.setRequestMethod("POST");
                    ins = con.getInputStream();
                    Thread.sleep(200);
                    scheduleRptIPsBasePath[0] = scheduleRptIPsArr[i];
                    scheduleRptIPsBasePath[1] = temp;
                    break;
                } catch (ConnectException ex1) {

                    LOG.errorTrace(METHOD_NAME, ex1);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[scheduleRptIPs]", "", "", " ",
                        "ConnectException, This server is down =" + temp);

                } catch (Exception ex) {

                    LOG.errorTrace(METHOD_NAME, ex);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[scheduleRptIPs]", "", "", " ",
                        "Exception, This server is down =" + temp);
                } finally {
                    if (ins != null) {
                        ins = null;
                    }
                    if (con != null) {
                        con = null;
                    }
                }
            }
        } catch (Exception ex) {
            LOG.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[scheduleRptIPs]", "", "", " ",
                "Exception1, This server is down =" + temp);
        } finally {
            try {
                if (ins != null) {
                    ins.close();
                }
            } catch (IOException ioe) {
                LOG.errorTrace(METHOD_NAME, ioe);
            }
            if (con != null) {
                con = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting scheduleRptIPsBasePath[0]=" + scheduleRptIPsBasePath[0] + " scheduleRptIPsBasePath[1]=" + scheduleRptIPsBasePath[1]);
            }
        }
        return scheduleRptIPsBasePath;
    }

    /**
     * Date : Feb 29, 2008
     * This method can be used in the validate method of form bean to remove the
     * validation error message(error message coming from the xml validation
     * part)
     * based on the key.
     * 
     * @param p_error
     * @param p_key
     * @param p_property
     * @return ActionErrors
     */
  /*  public static ActionErrors removeErrMsg(ActionErrors p_error, String p_key, String p_property) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("removeErrMsg", "Entered p_error=" + p_error + ", p_key=" + p_key + ", p_property=" + p_property);
        }

        final ActionErrors errors = new ActionErrors();
        final Iterator itr = p_error.get();
        ActionMessage msg = null;
        String key = null;
        while (itr.hasNext()) {
            msg = (ActionMessage) itr.next();
            key = msg.getKey();
            if (!key.equals(p_key)) {
                errors.add(p_property, msg);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("removeErrMsg", "Exiting errors= " + errors);
        }
        return errors;
    }*/

    /**
     * @param p_fileType
     * @return
     */
    public static String getFileContentType(String p_fileType) {
        String contentType = null;
        if (PretupsI.FILE_CONTENT_TYPE_CSV.equals(p_fileType)) {
            contentType = "application/octet-stream,application/csv,text/csv,application/vnd.ms-excel";
        } else if (PretupsI.FILE_CONTENT_TYPE_XLS.equals(p_fileType)) {
            contentType = "application/vnd.ms-excel,application/xls";
        } else if (PretupsI.FILE_CONTENT_TYPE_PLAIN_TEXT.equals(p_fileType)) {
            contentType = "text/plain";
        } else if (PretupsI.FILE_CONTENT_TYPE_XLSX.equals(p_fileType)) {  
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/xlsx";
        } else if (PretupsI.FILE_CONTENT_TYPE_PDF.equals(p_fileType)) {  
            contentType = "application/pdf";
        } 
        return contentType;
    }

    /**
     * This method is used to get the absolute path of class file.
     * 
     * @param cls
     * @return
     */
    public static String getFilePath(Class cls) {
        if (cls == null) {
            return null;
        }
        final String METHOD_NAME = "getFilePath";
        final String name = cls.getName().replace('.', '/');
        final URL loc = cls.getResource("/" + name + ".class");
        File f = new File(loc.getFile());
        // Class file is inside a jar file.
        if (f.getPath().startsWith("file:")) {
            final String s = f.getPath();
            final int index = s.indexOf('!');
            // It confirm it is a jar file
            if (index != -1) {
                f = new File(s.substring(5).replace('!', File.separatorChar));
                return f.getPath();
            }
        }
        try {
            f = f.getCanonicalFile();
        } catch (IOException ioe) {
            LOG.errorTrace(METHOD_NAME, ioe);
            return null;
        }
        return f.getPath();
    }

    /**
     * This method is used to create a string of particular format from another
     * String.
     * 
     * @param p_month
     *            ,p_date,p_year
     * @return String
     */
    /**
     * @param p_month
     * @param p_date
     * @param p_year
     * @return
     */
    public static String getDateFromString(String p_month, String p_date, String p_year) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getDateFromString", "Entered p_month=" + p_month + ", p_date=" + p_date + ", p_year=" + p_year);
        }
        String formatedDate = null;
        String month = "";
        String systemDateFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT);
        final String year = p_year.substring(2);
        if ("jan".equalsIgnoreCase(p_month)) {
            month = "01";
        } else if ("feb".equalsIgnoreCase(p_month)) {
            month = "02";
        } else if ("mar".equalsIgnoreCase(p_month)) {
            month = "03";
        } else if ("apr".equalsIgnoreCase(p_month)) {
            month = "04";
        } else if ("may".equalsIgnoreCase(p_month)) {
            month = "05";
        } else if ("jun".equalsIgnoreCase(p_month)) {
            month = "06";
        } else if ("jul".equalsIgnoreCase(p_month)) {
            month = "07";
        } else if ("aug".equalsIgnoreCase(p_month)) {
            month = "08";
        } else if ("sep".equalsIgnoreCase(p_month)) {
            month = "09";
        } else if ("oct".equalsIgnoreCase(p_month)) {
            month = "10";
        } else if ("nov".equalsIgnoreCase(p_month)) {
            month = "11";
        } else if ("dec".equalsIgnoreCase(p_month)) {
            month = "12";
        }

        if ((PretupsI.DATE_FORMAT).equalsIgnoreCase(systemDateFormat)) {
            formatedDate = p_date + "/" + month + "/" + year;
        } else if (("MM/dd/yy").equalsIgnoreCase(systemDateFormat)) {
            formatedDate = month + "/" + p_date + "/" + year;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("getDateFromString", "Exiting forward= " + formatedDate);
        }
        return formatedDate;
    }

    /**
     * Get Date From Date String
     * 
     * @param dateStr
     * @param format
     * @return
     * @throws ParseException
     */
    public static Date getDateFromString(String dateStr, String format) throws ParseException {

        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false); // this is required else it will convert
        return sdf.parse(dateStr);
    }

    /**
     * validate the pattern of the category/item code entered by user
     * 
     * @param regEx
     * @param errors
     * @return
     */
    public static String validatePattern(String regEx) {
        String error = NullToString("");
        final Pattern p = Pattern.compile("[a-zA-Z\\d]+?");
        // Match the given string with the pattern
        final Matcher m = p.matcher(regEx);
        // check whether match is found
        boolean matchFound = false;
        matchFound = m.matches();
        if (!matchFound) {
            error = "vas.error.pattern";
        }
        return error;
    }

    /**
     * @param value
     * @return flag
     * @author shishupal.singh
     */
    public static boolean isValidDatePattern(String value) {
        boolean flag = false;
        if (value != null && value.length() == 0) {
            flag = true;
        } else {
            try {
                getDateFromDateString(value);
                flag = true;
            } catch (ParseException e) {
                flag = false;
            }
        }
        return flag;
    }

    /**
     * @param regEx
     * @return
     */
    public static boolean isAlphaNumeric(String regEx) {
    	String alphaNumericSpclRegex = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALPHANUM_SPCL_REGEX);
    	String regExp = alphaNumericSpclRegex;
    	if(BTSLUtil.isNullString(regExp)) {
    	    		regExp = "[a-zA-Z\\d]+?";
    	}
final Pattern p = Pattern.compile(regExp);
    	
    //	final Pattern p = Pattern.compile("[a-zA-Z\\d]+?");
        
        // Match the given string with the pattern
        final Matcher m = p.matcher(regEx);
        // check whether match is found
        return m.matches();
    }

    /**
     * To check String have at least one character value or not.
     * 
     * @param str
     * @return boolean if contains character returns true else false;
     */
    public static boolean containsCapChar(String str) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("containsCapChar", "Entered str=" + maskParam(str));
        }
        int a = 0;
        if (str != null) {
            final char arr[] = str.toCharArray();
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] >= 'A' && arr[i] <= 'Z') {
                    ++a;
                }
                if (a >= 1) {
                    break;
                }
            }
        }
        if (a == 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Generates random PIN
     * 
     * @param chars
     * @param passLength
     * @return String
     * @throws BTSLBaseException
     */
    public static String generateRandomPIN(String chars, int passLength) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("generateRandomPIN ", "Entered str " + passLength);
        }
        if (passLength > chars.length()) {
            throw new BTSLBaseException("Password or generation is imposible, Minimum password length must be lessthen 10.");
        }
        final Random m_generator = new Random(System.currentTimeMillis());
        final char[] availableChars = chars.toCharArray();
        int availableCharsLeft = availableChars.length;
        final StringBuffer temp = new StringBuffer(passLength);
        int pos = 0;
        for (int i = 0; i < passLength; i++) {
            pos = Double.valueOf((availableCharsLeft * m_generator.nextDouble())).intValue();
            temp.append(availableChars[pos]);
            availableChars[pos] = availableChars[availableCharsLeft - 1];
            --availableCharsLeft;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("generateRandomPIN ", "Exit value " + String.valueOf(temp));
        }
        return String.valueOf(temp);
    }

    /*
     * add minute on days
     */
    /**
     * @param p_date
     * @param p_no_ofMinute
     * @return
     */
    public static Date addMinuteOnDaysInUtilMinute(java.util.Date p_date, int p_no_ofMinute) {
        final Calendar cal = BTSLDateUtil.getInstance();
        cal.setTime(p_date);
        cal.add(Calendar.MINUTE, p_no_ofMinute);
        return cal.getTime();
    }

    /**
     * @param p_date
     * @param p_no_ofMinute
     * @return
     */
    public static Date addMilliSecondOnDays(java.util.Date p_date, int p_no_ofMinute) {
        final Calendar cal = BTSLDateUtil.getInstance();
        cal.setTime(p_date);
        cal.add(Calendar.MILLISECOND, p_no_ofMinute);
        return cal.getTime();
    }

    /**
     * Converts Util date to Sql Date
     * 
     * @param utilDate
     * @return
     */
    public static java.sql.Timestamp getSQLDateTimeFromUtilDate(java.util.Date utilDate) {
        java.sql.Timestamp sqlDateTime = null;
        if (utilDate != null) {
            sqlDateTime = new java.sql.Timestamp(utilDate.getTime());
        }
        return sqlDateTime;
    }// end of UtilDateToSqlDateTime Stamp

    /*
     * Ashish T
     * Change done for implementing hashing
     * String p_text is hash value
     * field is the value to be converted in hash and then compared with p_text
     */

    /**
     * @param p_hashValue
     * @param p_text
     * @return
     */
    public static String compareHash2String(String p_hashValue, String p_text) {
        final String METHOD_NAME = "compareHash2String";
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("compareHash2String ", "Entered with p_hashValue:" + p_hashValue + "p_text" + BTSLUtil.maskParam(p_text));
            }

            if (BTSLUtil.isNullString(p_text)) {
                return p_hashValue;
            } else {
                if (p_hashValue.equals(BTSLUtil.encryptText(p_text))) {
                    return "true";
                } else {
                    return "false";
                }
            }
        } catch (Exception e) {
            LOG.error("compareHash2String", "Exception e=" + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            return null;
        }
    }// end method

    /**
     * Encrypts the passed text string using an encryption key for 3DES & AES
     * 
     * @param password
     * @return String
     */
    /**
     * @param p_text
     * @return
     */
    public static String encrypt3DesAesText(String p_text) {
        final String METHOD_NAME = "encrypt3DesAesText";
        try {
            if ("AES".equals(Constants.getProperty("ENDECRYPTION_TYPE"))) {
                final AESEncryptionUtil bex = new AESEncryptionUtil();
                return bex.EncryptAES(p_text);
            } else {
                return new CryptoUtil().encrypt(p_text, Constants.KEY);
            }

        } catch (Exception e) {
            LOG.error("encrypt3DesAesText", "Exception e=" + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            return null;
        }
    }

    /**
     * Decrypts the passed text string using an encryption key for 3DES & AES
     * 
     * @param p_text
     * @return
     */
    public static String decrypt3DesAesText(String p_text) {
    	final String METHOD_NAME = "decrypt3DesAesText";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, " Entered  p_text = " + p_text);
        }
        try {
            if ("AES".equals(Constants.getProperty("ENDECRYPTION_TYPE"))) {
                final AESEncryptionUtil bex = new AESEncryptionUtil();
                return bex.DecryptAES(p_text);
            } else {
                return new CryptoUtil().decrypt(p_text, Constants.KEY);
            }

        } catch (Exception e) {
            LOG.error(METHOD_NAME, "Exception e=" + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            return null;
        }finally{
        	if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, " Exit" );
            }
        }
    }// end method

    /**
     * To implement https when creating a url connection.
     * 
     * @param URL
     * @return
     */
    public static HttpURLConnection getConnection(URL url) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, KeyManagementException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getConnection", " Entered  URL = " + url);
        }
        final String METHOD_NAME = "getConnection";
        final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                for (int i = 0; i < certs.length; i++) {

                }
            }
        } };

        try {
            final SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            LOG.errorTrace(METHOD_NAME, e);
        }

        // Create all-trusting host name verifier
        final HostnameVerifier allHostsValid = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        if (!CertificateLoader.isTomcatServer()) {
            return (HttpURLConnection) url.openConnection();
        } else {
            boolean bProxySet = false;

            String proxyPassword = null;
            String proxyEnabled;
            proxyEnabled = System.getProperty("https.proxySet");
            if (null != proxyEnabled && ("true").equalsIgnoreCase(proxyEnabled)) {

                proxyPassword = System.getProperty("https.proxyPassword");
                bProxySet = true;
            }
            try {
                final HostnameVerifier hv = new HostnameVerifier() {

                    @Override
                    public boolean verify(String urlHostName, SSLSession session) {
                        return true;
                    }
                };
                HttpsURLConnection.setDefaultHostnameVerifier(hv);
            } catch (Exception e) {
                LOG.error("getConnection", " Error in Host name Verification  = " + e.getMessage());
                LOG.errorTrace(METHOD_NAME, e);
            }
            final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            if (CertificateLoader.getSslSocketFactory() == null) {
                CertificateLoader.loadCertificateOnStartUp();
            }
            connection.setSSLSocketFactory(CertificateLoader.getSslSocketFactory());
            // set Authenticator
            if (bProxySet) {
                Authenticator.setDefault(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("getPasswordAuthentication", " PASSWORD AUNTHENTICATION");
                        }
                        final String user = System.getProperty("https.proxyUser");
                        final String password = System.getProperty("https.proxyPassword");
                        return new PasswordAuthentication(user, password.toCharArray());
                    }
                });
            }
            return connection;
        }
    }

    /**
     * Get Date String From Date
     * 
     * @param date
     * @return
     * @throws ParseException
     */
    public static String getDateStringFromDate(Date date, String format) throws ParseException {
        if (isNullString(format)) {
            format = PretupsI.DATE_FORMAT;
        }
        if(date == null) {
        	return PretupsI.EMPTY;
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false); // this is required else it will convert
        return sdf.format(date);
    }

    /**
     * Encrypts the passed text string using an encryption key
     * 
     * @param password
     * @return String
     */
    
    /**
     * @param p_text
     * @return
     */
    public static String encryptAES(String p_text) {
        final String METHOD_NAME = "encryptAES";
        try {
            return new AESEncryptionUtil().EncryptAES(p_text);
        } catch (Exception e) {
            LOG.error("encryptAES", "Exception e=" + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            return null;
        }
    }

    /**
     * Decrypts the passed text string using an encryption key
     * 
     * @param p_text
     * @return
     */
    public static String decryptAES(String p_text) {
        final String METHOD_NAME = "decryptAES";
        try {
            return new AESEncryptionUtil().DecryptAES(p_text);

        } catch (Exception e) {
            LOG.error("decryptAES", "Exception e=" + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            return null;
        }
    }// end method

    /**
     * Generate WeekNumbers
     * 
     * @param string
     * @return stringbuffer
     * @throws BTSLBaseException
     * @author arvinder.singh
     * 
     */
    
    /**
     * @param num
     * @return
     * @throws BTSLBaseException
     */
    public static StringBuffer numberToWeekdays(String num) throws BTSLBaseException {

        final String[] temp = num.split(",");
        final StringBuffer fullDaysString = new StringBuffer();

        String arr[] = new String[temp.length];
        arr = temp;

        for (int i = 0; i < arr.length; i++) {
            String value = arr[i];
            if (value.contains("0")) {
                value = "Sun";
            }
            if (value.contains("1")) {
                value = "Mon";
            }
            if (value.contains("2")) {
                value = "Tue";
            }
            if (value.contains("3")) {
                value = "Wed";
            }
            if (value.contains("4")) {
                value = "Thu";
            }
            if (value.contains("5")) {
                value = "Fri";
            }
            if (value.contains("6")) {
                value = "Sat";
            }

            if (i != 0) {
                fullDaysString.append(",");
            }
            fullDaysString.append(value);
        }
        return fullDaysString;
    }

    /**
     * Method to validate the to and from report dates from Mis dates.
     * 
     * @param p_fromDate
     * @param p_toDate
     * @param p_channelUserVO
     * @param p_type
     * @return
     * @throws BTSLBaseException
     */
    public static boolean checkDateFromMisDates(Date p_fromDate, Date p_toDate, ChannelUserVO p_channelUserVO, String p_type) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("checkDateFromMisDates", " Entered  p_fromDate = " + p_fromDate + " p_toDate= " + p_toDate + "p_type= " + p_type);
        }
        boolean flag = false;
        Date _misFromDate = null;
        Date _misToDate = null;
        if (ProcessI.C2SMIS.equalsIgnoreCase(p_type)) {
            _misFromDate = p_channelUserVO.getC2sMisFromDate();
            _misToDate = p_channelUserVO.getC2sMisToDate();
        } else if (ProcessI.P2PMIS.equalsIgnoreCase(p_type)) {
            _misFromDate = p_channelUserVO.getP2pMisFromDate();
            _misToDate = p_channelUserVO.getP2pMisToDate();
        }
        // From date should be more than the MIS_FROM DATE and To date should be
        // less than MIS_TO DATE.
        if ((BTSLUtil.getDifferenceInUtilDates(_misFromDate, p_fromDate) >= 0) && (BTSLUtil.getDifferenceInUtilDates(p_toDate, _misToDate) >= 0)) {
            flag = true;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("checkDateFromMisDates", " Exiting flag :: " + flag);
        }
        return flag;
    }

    /**
     * Generate WeekDaysNames
     * 
     * @param string
     * @return stringbuffer
     * @throws BTSLBaseException
     * @author arvinder.singh
     * 
     */

    /**
     * @param words
     * @return
     * @throws BTSLBaseException
     */
    public static StringBuffer weekDaysToNumber(String words) throws BTSLBaseException {
        final String[] temp = words.split(",");
        final StringBuffer fullWeeknames = new StringBuffer();
        String arr[] = new String[temp.length];
        arr = temp;

        for (int i = 0; i < arr.length; i++) {
            String value = arr[i];
            if (value.contains("Sun")) {
                value = "0";
            }
            if (value.contains("Mon")) {
                value = "1";
            }
            if (value.contains("Tue")) {
                value = "2";
            }
            if (value.contains("Wed")) {
                value = "3";
            }
            if (value.contains("Thu")) {
                value = "4";
            }
            if (value.contains("Fri")) {
                value = "5";
            }
            if (value.contains("Sat")) {
                value = "6";
            }

            if (i != 0) {
                fullWeeknames.append(",");
            }
            fullWeeknames.append(value);
        }
        return fullWeeknames;
    }

    /**
     * @param prefixIds
     * @return
     * @throws BTSLBaseException 
     */
    public static StringBuffer getPrefixSeries(String prefixIds) throws BTSLBaseException {
        final StringTokenizer tokenizer = new StringTokenizer(prefixIds, ",");
        NetworkPrefixVO networkPrefixVO = null;// added by arvinder
        String allowPrefixIds = "";
        final StringBuffer validPrefixBuff = new StringBuffer();
        while (tokenizer.hasMoreTokens()) {
            allowPrefixIds = tokenizer.nextToken().trim();
            networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(allowPrefixIds);
            if (networkPrefixVO == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("add", "Allowed PrefixID doesnt match " + allowPrefixIds);
                }
                continue;
            }
            validPrefixBuff.append(networkPrefixVO.getPrefixID());
            validPrefixBuff.append(",");

        }

        return validPrefixBuff;

    }

    /**
     * Method moveFileToArchive.
     * This method is used to make Archive file on the server. Here the
     * p_dirPath is picked form Constant.props
     * It is the directory path where the file is archived.
     * 
     * @author babu.kunwar
     * @param p_dirPath
     * @param p_fileName
     *            String
     * @param p_file
     *            String
     * @return boolean
     */
    public static boolean moveFileToArchive(String p_dirPath, String p_absoluteFilePath, String p_fileName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("moveFileToArchive", " Entered:: p_absoluteFilePath::=" + p_absoluteFilePath + " p_fileName::=" + p_fileName + " p_dirPath::=" + p_dirPath);
        }

        final String FileName = p_fileName.substring(0, p_fileName.length() - 4);
        final File fileRead = new File(p_absoluteFilePath);
        File fileArchive = new File(String.valueOf(p_dirPath));
        if (!fileArchive.isDirectory()) {
            fileArchive.mkdirs();
        }
        fileArchive = new File(String.valueOf((p_dirPath) + FileName + "_" + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime()) + ".txt"); // to
        // make
        // the
        // new
        // file
        // name
        final boolean flag = fileRead.renameTo(fileArchive);
        if (LOG.isDebugEnabled()) {
            LOG.debug("moveFileToArchive", " Exiting File Moved=" + flag);
        }
        return flag;
    }

    /**
     * This method will convert operator specific msisdn to system specific
     * msisdn.
     * 
     * @param p_msisdn
     * @return
     * @throws BTSLBaseException
     * @author mukesh.singh
     */
    public static String addCountryCodeToMSISDN(String p_msisdn) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("addCountryCodeToMSISDN", "Entered p_msisdn:" + p_msisdn);
        }
        String msisdn = null;
        boolean countryCodeFound = false;
        String prefix = null;
        final String METHOD_NAME = "addCountryCodeToMSISDN";
        int minMsisdnLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue();
        String msisdnPrefixList = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST_CODE);
        String countryCode = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.COUNTRY_CODE);
        try {
            if (p_msisdn.length() <= minMsisdnLength) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("addCountryCodeToMSISDN", "msisdnPrefixList:" + msisdnPrefixList);
                }
                final StringTokenizer strTok = new StringTokenizer(msisdnPrefixList, ",");
                while (strTok.hasMoreTokens()) {
                    prefix = strTok.nextToken();
                    // check if country code value matches msisdn prefix list
                    if (prefix.equals(countryCode)) {
                        countryCodeFound = true;
                        break;
                    } else {
                        continue;
                    }
                }
                if (countryCodeFound) {
                    // append the country code
                    msisdn = countryCode + p_msisdn;
                } else {
                    // if country code not found in db then msisdn remains the
                    // same
                    msisdn = p_msisdn;
                }
            } else {
                msisdn = p_msisdn;
            }
        } catch (Exception e) {
            LOG.error("addCountryCodeToMSISDN", "Exception while getting the mobile no from passed no=" + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[addCountryCodeToMSISDN]", "", p_msisdn, "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("addCountryCodeToMSISDN", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("addCountryCodeToMSISDN", "Exiting Filtered msisdn=" + msisdn);
            }
        }
        return msisdn;
    }

    /**
     * Method isRsaRequired
     * 
     * @param userVO
     *            UserVO
     * @return boolean
     * @throws Exception
     */
    public static boolean isRsaRequired(UserVO userVO) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("isRsaRequired", "Entered userVO=" + userVO);
        }
        boolean rsaRequired = false;
        try {
            rsaRequired = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.RSA_AUTHENTICATION_REQUIRED, userVO.getNetworkID(), userVO.getCategoryCode()))
                .booleanValue();
            if (!(PretupsI.OPERATOR_TYPE_OPT.equalsIgnoreCase(userVO.getDomainID())) && rsaRequired) {
                final String webLoginAllowedOrNot = userVO.getCategoryVO().getWebInterfaceAllowed();
                if ("Y".equalsIgnoreCase(webLoginAllowedOrNot)) {
                    rsaRequired = true;
                } else {
                    rsaRequired = false;
                }
            }
        } catch (Exception e) {
            LOG.debug("isRsaRequired", "Exception: " + e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("isRsaRequired", "Exiting rsaRequired=" + rsaRequired);
        }
        return rsaRequired;
    }

    /**
     * @param p_date
     * @return
     * @throws ParseException
     */
    public static String currentDateTimeFormatString(Date p_date) throws ParseException {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        final String dateString = sdf.format(p_date);
        return dateString;
    }
    
    /**
     * @param p_date
     * @return
     * @throws ParseException
     */
    public static String currentTimeFormatString(Date p_date) throws ParseException {
        final SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
        final String dateString = sdf.format(p_date);
        return dateString;
    }

    /**
     * This method converts a string in the format "a=1&b=2&c=3" into a HashMap
     * in the format "{a=1, b=2, c=3}"
     * 
     * @param str
     *            The string to be converted to hash
     * @param token1
     *            The tokenizer : "&"
     * @param token2
     *            The tokenizer : "="
     * @return Returns HashMap representation of the String passed
     */
    public static HashMap getStringToHashNew(String str, String token1, String token2) {
    	String methodName ="getStringToHashNew";
        final HashMap ht = new HashMap();
        StringTokenizer stToken1 = null;
        StringTokenizer stToken2 = null;
        String newString = "";
        final ArrayList list = new ArrayList();
        stToken1 = new StringTokenizer(str, token1);
        final String charsetEncoding = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CHARSET_ENCODING));
        while (stToken1.hasMoreTokens()) {
            newString = stToken1.nextToken();
            if (newString.indexOf(token2) < 0) {
                continue;
            }
            stToken2 = new StringTokenizer(newString, token2);
            if (stToken2.countTokens() < 2) {
                continue;
            }
            while (stToken2.hasMoreTokens()) {
                list.add(stToken2.nextToken());
            }
        }
        
        for (int i = 0; i < list.size(); i++) {
            if (i % 2 == 1) {
                try {
					ht.put(list.get(i - 1), URLDecoder.decode((String) list.get(i), charsetEncoding));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					LOG.errorTrace(methodName, e);
				}
            } // End of if Statement
        } // End of for loop

        return ht;
    }

    /**
     * @author diwakar
     *         Validates MSISDN for numeric and MIN and MAX length
     * @param mobileno
     * @return
     */
    /**
     * @param p_reqNumber
     * @return
     */
    public static boolean isValidNumber(String p_reqNumber) {
        final int strLength = p_reqNumber.length();
        if (LOG.isDebugEnabled()) {
            LOG.debug("isValidNumber", "p_reqNumber length=" + strLength);
        }

        for (int i = 0; i < strLength; i++) {
            if (!(Character.isDigit(p_reqNumber.charAt(i)))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates MSISDN for numeric and MIN and MAX length
     * 
     * @param mobileno
     * @author diwakar
     * @date : 18-02-2014
     * @return
     */
    /**
     * @param p_msisdn
     * @return
     */
    public static boolean isValidMSISDNLength(String p_msisdn) {
        final int strLength = p_msisdn.length();
        int minMsisdnLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue();
        int maxMsisdnLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue();
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                "isValidMobileNo",
                "strLength= " + strLength + " minMsisdnLength=" + minMsisdnLength + "maxMsisdnLength=" + maxMsisdnLength + "*************");
        }

        if (strLength < minMsisdnLength || strLength > maxMsisdnLength) {
            return false;
        }
        return true;
    }

    /**
     * Validates MSISDN for numeric and MIN and MAX length
     * 
     * @param mobileno
     * @author diwakar
     * @date : 18-02-2014
     * @return
     */
    
    /**
     * @param p_msisdn
     * @return
     */
    public static boolean isValidMSISDNDigit(String p_msisdn) {
        final int strLength = p_msisdn.length();
        int minMsisdnLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue();
        int maxMsisdnLength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue();
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                "isValidMobileNo",
                "strLength= " + strLength + " minMsisdnLength=" + minMsisdnLength + "maxMsisdnLength=" + maxMsisdnLength + "*************");
        }

        for (int i = 0; i < strLength; i++) {
            if (!((Character.isDigit(p_msisdn.charAt(i))) || (p_msisdn.charAt(i) == '+'))) {
                return false;
            }
        }
        return true;
    }

    /*
     * Validate the string contains the special character ror not
     * 
     * @param p_string
     * 
     * @author diwakar
     * 
     * @date : 21-05-2014
     * 
     * @return
     */
    public static boolean isContainsSpecialCharacters(String p_string) {
        final String METHOD_NAME = "isContainsSpecialCharacters";

        if (LOG.isDebugEnabled()) {
            LOG.debug("isContainsSpecialCharacters", "p_string= " + p_string);
        }
        boolean result = false;
        String specialCharacters = "%=&";
        try {
            specialCharacters = Constants.getProperty("SPECIAL_CHAR_MANNAGE_MESSAGE");
            if (specialCharacters == null) {
                specialCharacters = "%=&";
            }
        } catch (RuntimeException e) {
            LOG.errorTrace(METHOD_NAME, e);
            specialCharacters = "%=&";
        }
        final char[] charArray = specialCharacters.toCharArray();
        int counter = charArray.length;
        while (counter > 0) {
            --counter;
            if (p_string != null && p_string.contains(String.valueOf(charArray[counter]))) {
                result = true;
                break;
            }
        }
        return result;
    }

    /***
     * @author gaurav.pandey
     * @param p_text
     * @param key
     * @return
     */

    public static String encryptAESNew(String p_text, String key) {
        final String METHOD_NAME = "encryptAESNew";
        try {
            return new AESEncryptionUtil().EncryptAESNew(p_text, key);
        } catch (Exception e) {
            LOG.error("encryptAESNew", "Exception e=" + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            return null;
        }
    }

    /****
     * @author gaurav.pandey
     * @param p_text
     * @param key
     * @return
     */

    public static String decryptAESNew(String p_text, String key) {
        final String METHOD_NAME = "decryptAESNew";
        try {
            return new AESEncryptionUtil().DecryptAESNew(p_text, key);

        } catch (Exception e) {
            LOG.error("decryptAESNew", "Exception e=" + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            return null;
        }
    }

    /***
     * @author gaurav.pandey
     * @return
     */

    public static String genrateAESKey() {
        final String METHOD_NAME = "genrateAESKey";
        try {
            return new AESEncryptionUtil().genrateAESKey();

        } catch (Exception e) {
            LOG.error("genrateAESKey", "Exception e=" + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            return null;
        }

    }

    /**
     * Validates Email- for Struts 2
     * 
     * @param String
     * @author Vibhu Trehan
     * @date : 04-01-2014
     * @return
     */
    /**
     * @param s
     * @return
     */
    public static boolean isEmail(String s) {
        if (isEmpty(s)) {
            return defaultEmptyOK;
        }

        // is s whitespace?
        if (isWhitespace(s)) {
            return false;
        }

        // there must be >= 1 character before @, so we
        // start looking at character position 1
        // (i.e. second character)
        int i = 1;
        final int sLength = s.length();

        // look for @
        while ((i < sLength) && (s.charAt(i) != '@')) {
            i++;
        }

        // there must be at least one character after the .
        if ((i >= sLength - 1) || (s.charAt(i) != '@')) {
            return false;
        } else {
            return true;
        }
    }

    /** Check whether string s is empty. */
    public static boolean isEmpty(String s) {
        return (s == null) || (s.trim().length() == 0);
    }

    public static boolean isWhitespace(String s) {
        // Is s empty?
        if (isEmpty(s)) {
            return true;
        }

        // Search through string's characters one by one
        // until we find a non-whitespace character.
        // When we do, return false; if we don't, return true.
        int sLength = s.length();
        for (int i = 0; i < sLength; i++) {
            // Check that current character isn't whitespace.
            final char c = s.charAt(i);

            if (whitespace.indexOf(c) == -1) {
                return false;
            }
        }
        // All characters are whitespace.
        return true;
    }

    /**
     * Utility method to validate the Captcha from the request
     * against the value in session. Added for Captcha Authentication
     * 
     * @param request
     *            HttpServletRequest
     * @return boolean
     */
    public static boolean validateCaptcha(HttpServletRequest request) {
        boolean isSuccessful = false;
        final HttpSession session = request.getSession();
        // retrieve captcha value from imageCaptchaServlet class
        final Captcha captcha = (Captcha) session.getAttribute(Captcha.NAME);
        final String captchaVerificationCode = request.getParameter("captchaVerificationCode");
        if (captcha != null) {
            // if captcha from image matches user enterd captcha
            if (captcha.isCorrect(captchaVerificationCode)) {
                isSuccessful = true;
            }
        }
        if(!BTSLUtil.isNullObject(session)) {
        	session.setAttribute(Captcha.NAME, null);        	
        }
        return isSuccessful;
    }

    /**
     * This method will validate the file content for in-valid characters(<,>) *
     * 
     * @param line
     * @return boolean
     */
    public static boolean isFileContentValid(String line) {
        boolean isValidFileContent = true;
        final String pattern = Constants.getProperty("FILE_CONTENT_WHITE_LIST");
        final Pattern r = Pattern.compile(pattern);
        final Matcher m = r.matcher(line);
        if (!m.find()) {
            isValidFileContent = false;
        }
        return isValidFileContent;
    }

    /**
     * Validates the name of the file being uploaded
     * 
     * @param fileName
     * @author akanksha.
     * @date : 14-08-2014
     * @return boolean
     */
    public static boolean isValideFileName(String fileName) {
        boolean isValidFileContent = true;
        final String pattern = Constants.getProperty("FILE_NAME_WHITE_LIST");
        // to match a text against a regular expression pattern more than one
        // time,
        // need to create a Pattern instance using the Pattern.compile() method.
        final Pattern r = Pattern.compile(pattern);
        // to find matches of the pattern in texts
        final Matcher m = r.matcher(fileName);
        // if any character does not match then set variable's value false
        if (!m.find()) {
            isValidFileContent = false;
        }
        return isValidFileContent;
    }

    /**
     * Validates the name of the file being uploaded
     * 
     * @param String
     *            and Date
     * @date : 14-08-2014
     * @return boolean
     */
    /**
     * @param value
     * @param date
     * @return
     */
    public static boolean timeRangeValidation(String value, Date date) {
        boolean validate = false;
        final String METHOD_NAME = "timeRangeValidation";
        if (value == null || value.length() == 0) {
            return true;
        }

        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            sdf.setLenient(false);
            final String[] dateString = sdf.format(date).split(":");
            final int dm = Integer.parseInt(dateString[0]) * 60 + Integer.parseInt(dateString[1]);
            final String[] commaSepatated = value.split(","); // String []
            if (commaSepatated.length > 0) {
                for (int i = 0; i < commaSepatated.length; i++) {
                    final String[] hyphenSeparated = commaSepatated[i].split("-");
                    if (hyphenSeparated.length == 2) {
                        final String[] current1 = hyphenSeparated[0].split(":");
                        final String[] current2 = hyphenSeparated[1].split(":");
                        if (Integer.parseInt(current1[0]) * 60 + Integer.parseInt(current1[1]) <= dm && dm < Integer.parseInt(current2[0]) * 60 + Integer
                            .parseInt(current2[1])) {
                            validate = true;
                            break;
                        }
                    }
                }
            } else {
                validate = false;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("validate=", validate);
            }
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("validate", "validation failed");
            }
            LOG.errorTrace(METHOD_NAME, e);
        }
        return validate;
    }

    public static CommissionProfileSetVO getOptionDescForCommProfile(String p_code, ArrayList p_list) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getOptionDescForCommProfile", "Entered");
        }
        CommissionProfileSetVO vo = null;

        boolean flag = false;
        if (p_list != null && !p_list.isEmpty()) {
            for (int i = 0, j = p_list.size(); i < j; i++) {
                vo = (CommissionProfileSetVO) p_list.get(i);
                if (vo.getCommProfileSetId().equalsIgnoreCase(p_code)) {
                    flag = true;
                    break;
                }
            }
        }
        if (!flag) {
            vo = new CommissionProfileSetVO();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("getOptionDescForCommProfile", "Exited: vo=" + vo);
        }
        return vo;
    }

    /**
     * count char '?' String
     * 
     * @param str
     * @author yogesh.pandey
     * @return
     */
    public static int countChar(String str) {
        int count = 0;
        int strLength = str.length();
        for (int i = 0; i < strLength; i++) {
            if (str.charAt(i) == '?') {
                count++;
            }
        }
        return count;
    }

    /**
     * Validates the name of the table of the database
     * 
     * @param voucher_type
     * @author akanksha.
     * @date : 30-04-2015
     * @return boolean
     */
    public static boolean validateTableName(String voucher_type) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("validateTableName", "Entered: voucher type= " + voucher_type);
        }
        boolean matchFound = false;
        final Pattern p = Pattern.compile("[a-zA-Z]*");
        // Match the given string with the pattern
        final Matcher m = p.matcher(voucher_type);
        // check whether match is found
        matchFound = m.find();
        if (LOG.isDebugEnabled()) {
            LOG.debug("validateTableName", "Exited: Match found value= " + matchFound);
        }
        return matchFound;
    }

    /**
     * Get Display Amount
     * 
     * @param p_amount
     * @return
     * @throws BTSLBaseException
     */
    public static double getDisplayAmount(double p_amount) {
    	int amountMultFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
        final int multiplicationFactor = amountMultFactor;
        double amount = 0d;
        try {
            amount = p_amount / multiplicationFactor;
        } catch (Exception e) {
            LOG.errorTrace("getDisplayAmount", e);
        }
        return amount;
    }
    public static boolean isNumericInteger(String str){

		boolean flag = false;
		try
		{
			Integer.parseInt(str);
			flag=true;
		}
		catch (Exception e)
		{
			LOG.errorTrace("isNumericInteger", e);
			flag=false;
		}
		return flag;
	}
    
    /**
     * This method is used to validate the csrf from action class.
     * @param METHOD_NAME
     * @param flag
     * @param forString (forward String)
     * @param className
     * @throws BTSLBaseException
     */
    public static void csrfValidation(String METHOD_NAME, boolean flag,String forString,String className) throws BTSLBaseException {
		if (!flag) {
		    if (LOG.isDebugEnabled()) {
		        LOG.debug("CSRF", "ATTACK!");
		    }
		    throw new BTSLBaseException(className, METHOD_NAME, "error.general.processing", forString);
		}
	}
    
    
    /**
     * This method is used to validate names in pretups system, e.g while creating user its first name, last name etc.
     * @param name
     * @return
     */
    public static boolean isValidName(String name) {
        boolean isValidName = true;
        final String pattern = Constants.getProperty("NAME_WHITE_LIST");
        // to match a text against a regular expression pattern more than one
        // time,
        // need to create a Pattern instance using the Pattern.compile() method.
        final Pattern r = Pattern.compile(pattern);
        // to find matches of the pattern in texts
        final Matcher m = r.matcher(name);
        // if any character does not match then set variable's value false
        if (!m.find()) {
        	isValidName = false;
        }
        return isValidName;
    }
    
    
    /**
     * This method is used to validate other input field in pretups system, e.g while creating user its designation, etc.
     * @param inputField
     * @return
     */
    public static boolean isValidInputField(String inputField) {
        boolean isValidInput = true;
        final String pattern = Constants.getProperty("INPUT_WHITE_LIST");
        // to match a text against a regular expression pattern more than one
        // time,
        // need to create a Pattern instance using the Pattern.compile() method.
        final Pattern r = Pattern.compile(pattern);
        // to find matches of the pattern in texts
        final Matcher m = r.matcher(inputField);
        // if any character does not match then set variable's value false
        if (!m.find()) {
        	isValidInput = false;
        }
        return isValidInput;
    }
	
	public static Date getDateTimeFromDateTimeString(String dateStr,String format)  throws ParseException {
		if(format.length()!=dateStr.length()) {
			throw new ParseException(format, 0);
		}
		SimpleDateFormat sdf = new SimpleDateFormat (format);
		sdf.setLenient(false); // this is required else it will convert
		return sdf.parse(dateStr);
	}
	
	/**
	 * @param dateToValidate
	 * @return true if date is before today
	 */
	public static boolean isDateBeforeToday(Date dateToValidate){
		long DAY_IN_MS = (long)1000 * 60 * 60 * 24;
		Date dayBeforeToday = new Date(new Date().getTime() - (1 * DAY_IN_MS));
		 return dateToValidate.before(dayBeforeToday);
	}
	/**
	 * @param dateToValidate
	 * @param reqDate
	 * @return true if date is before than required date
	 */
	public static boolean isDateBeforeReqDate(Date dateToValidate, Date reqDate){
		 return dateToValidate.before(reqDate);
	}
	
	//Added by Amit Raheja
			/**
			 * method uploadEncryptedFileToServer
			 * This method is to upload the file to the server. This method will work only in the STRUTS FRAMEWORK
			 * 
			 * @param p_formFile FormFile -- contains information of the file to be uploaded
			 * @param p_dirPath  String -- directory path of the server where to upload the file
			 * @param p_contentType String -- contetnt type of the file to be checked
			 * @param forward String  -- where to forward on the error
			 * @return boolean
			 * @throws BTSLBaseException 
			 */	
		/*	public static boolean uploadEncryptedFileToServer(FormFile p_formFile,String p_dirPath,String p_contentType,String forward,long p_fileSize) throws BTSLBaseException
			{
				final String metod_Name = "uploadEncryptedFileToServer";
				if(LOG.isDebugEnabled())
					LOG.debug(metod_Name,"Entered :p_formFile="+p_formFile+"p_formFile.getContentType()="+p_formFile.getContentType()+", p_dirPath="+p_dirPath+", p_contentType="+p_contentType+", forward="+forward+", p_fileSize="+p_fileSize);
				FileOutputStream outputStream=null;
				boolean returnValue=false;
				File fileDir=null;
				File fileName=null;
				try{
				fileDir = new File(p_dirPath);
				if(!fileDir.isDirectory())
					fileDir.mkdirs();
				if(!fileDir.exists())
				{
				    LOG.debug(metod_Name,"Directory does not exist: "+fileDir+" ");
				    throw new BTSLBaseException("BTSLUtil",metod_Name,"uploadfile.error.dirnotcreated",forward);
				}
				
				fileName = new File(p_dirPath,p_formFile.getFileName()+".encrypted");
				
			    // if file already exist then show the error message.
				if(p_formFile!= null)
				{
					if(p_formFile.getFileSize()<=0)
						throw new BTSLBaseException("BTSLUtil",metod_Name,"uploadfile.error.filesizezero",forward);
					else if(p_formFile.getFileSize()>p_fileSize)
						throw new BTSLBaseException("BTSLUtil",metod_Name,"uploadfile.error.largefilesize",0,new String[]{String.valueOf(p_fileSize)},forward);
					LOG.debug("p_formFile.getContentType()",p_formFile.getContentType());
					
					boolean contentTypeExist=false;
					if(p_contentType.contains(","))
					{
					    String temp[]=p_contentType.split(",");
					    for(int i=0,j=temp.length;i<j;i++)
					    {
					        if(p_formFile.getContentType().equalsIgnoreCase(temp[i].trim()))
					        {
					            contentTypeExist=true;
					            break;
					        }
					    }
					}
					else if(p_formFile.getContentType().equalsIgnoreCase(p_contentType))
					    contentTypeExist=true;
					    
					if(contentTypeExist)
					{
						if(fileName.exists())
							throw new BTSLBaseException("BTSLUtil",metod_Name,"uploadfile.error.fileexists",forward);
						outputStream=new FileOutputStream(fileName);
						// encryting the file and then uploading on the server
						outputStream.write(VoucherFileD3DES.encryptBytes(p_formFile.getFileData()));
						
						returnValue=true;
						if (LOG.isDebugEnabled())
							LOG.debug(metod_Name, "File Uploaded Successfully");
					}
					// 	if file is not a text file show error message
					else
					{
						if(LOG.isDebugEnabled())
							LOG.debug(metod_Name,"Invalid content type: "+p_formFile.getContentType()+" required is p_contentType: "+p_contentType+" p_formFile.getFileName(): "+p_formFile.getFileName());
						throw new BTSLBaseException("BTSLUtil","uploadFileToServer","uploadfile.error.notrequiredcontent",forward);
					}
				}
				// if there is no such file then show the error message
				else
					throw new BTSLBaseException("BTSLUtil",metod_Name,"uploadfile.error.nofile",forward);
			}
			catch (BTSLBaseException be)
			{
				throw be;
			} 
			catch (Exception e)
			{
				
				LOG.error(metod_Name,  "Exception"+ e.getMessage());
	    		LOG.errorTrace(metod_Name, e);
				e.getMessage();
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"BTSLUtil[uploadFileToServer]","","","","Exception:"+e.getMessage());
				throw new BTSLBaseException("BTSLUtil","uploadFileToServer","error.general.processing",forward);
			}
			finally
			{
				try{
			        if (outputStream!= null){
			        	outputStream.close();
			        }
			      }
			      catch (IOException e){
			    	  LOG.error("An error occurred closing outputStream.", e);
			      }
				if(LOG.isDebugEnabled()) 
					LOG.debug(metod_Name,"Exit :returnValue="+returnValue);
				fileName=null;
				fileDir=null;
			}
			return returnValue;
			}*/

			/**
			 * @param ptempTbl
			 * @return
			 */
			public static boolean validateTableNameCommon(String ptempTbl) {
		        if (LOG.isDebugEnabled()) {
		            LOG.debug("validateTableNameCommon", "Entered: table name= " + ptempTbl);
		        }
		        boolean matchFound;
		        final Pattern p = Pattern.compile("[a-zA-Z\\d_]*");
		        // Match the given string with the pattern
		        final Matcher m = p.matcher(ptempTbl);
		        // check whether match is found
		        matchFound = m.find();
		        if (LOG.isDebugEnabled()) {
		            LOG.debug("validateTableNameCommon", "Exited: Match found value= " + matchFound);
		        }
		        return matchFound;
		    }		
			
			
			/**
			 * @param name
			 * @param start
			 * @param end
			 * @return
			 */
			public static int getUniqueInteger(String name, int start, int end){
				final String methodName="getUniqueInteger";
				if(LOG.isDebugEnabled())
					LOG.debug(methodName,"Entered :start="+start+"end"+end);
				
		           int diff = end - start;
		         String plaintext = name;
		         int val=0;
		         MessageDigest m;
		         try {
		             m = MessageDigest.getInstance("SHA-512");
		             m.reset();
		             m.update(plaintext.getBytes());
		             byte[] digest = m.digest();
		             Checksum checksum = new CRC32();
		            checksum.update(digest,0,digest.length);
		            long lngChecksum = checksum.getValue();
		            val=Long.valueOf((lngChecksum%diff)).intValue()+(end-diff);
		            if (LOG.isDebugEnabled())
						LOG.debug(methodName, val);
		    
		         } catch (NoSuchAlgorithmException e) {
		        	 e.getMessage();
		        	 LOG.error(methodName, "Exception:e=" + e);
		         }
		         if(LOG.isDebugEnabled())
						LOG.debug(methodName,"Exiting :");
		         return val;
		     }

			
			
			
	//Addition ends
				 /**
				* this method is used in case of insert on partition tables in postgres.
		     * @param int
		     * @author shashi.singh
		     * @return
		     */
		    public static int getInsertCount(int insertCount ) {
		    	String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
	        	if (QueryConstants.DB_POSTGRESQL.equals(dbConnected) && 0==insertCount){
	        		insertCount=1;
	        	} 
		        return insertCount;
		    }
			
			/**
		     * This method is used to validate allowed Days
		     * 
		     * @param allowedDays
		     * @return
		     */
		public static boolean isValidateAllowedDays(String allowedDays) {
        boolean flag = true;
        final StringTokenizer st = new StringTokenizer(allowedDays, ",");
        if (st.countTokens() > 7) {
            flag = false;
        }
        if(!isNullString(allowedDays) && st.countTokens()==0){
            flag = false;
        }
        while (st.hasMoreTokens()) {
            try {
                final int part = Integer.parseInt(st.nextToken());
                if (part > 7 || part < 1) {
                    flag = false;
                }
            } catch (NumberFormatException nfex) {
                flag = false;
		        }
		        }
        return flag;
		    }

		    /**
		     * This method is used to validate allowed Time
		     * 
		     * @param allowedTime
		     * @return
		     */
		public static boolean isValidateAllowedTime(String allowedTime) {
        boolean flag = true;
        String[] time = allowedTime.split(":");

        if (time.length != 2) {
            flag = false;
        }else{
            try {
                Integer.parseInt(time[0]);
                Integer.parseInt(time[1]);
                if (time[0].length() != 2) {
                    flag = false;
                } else if (time[1].length() != 2) {
                    flag = false;
                } else if (Integer.parseInt(time[0]) > 23 || Integer.parseInt(time[0]) < 0) {
                    flag = false;
                } else if (Integer.parseInt(time[1]) > 59 || Integer.parseInt(time[1]) < 0) {
                    flag = false;
                }
             } catch (NumberFormatException ne) {
                LOG.errorTrace("isValidateAllowedTime", ne);
                flag = false;
        }
                }
        return flag;
            }
			   
		/**
	     * To check String have at least one character value or not.
	     * 
	     * @param str
	     * @return boolean if contains character returns true else false;
	     */
	    public static boolean containsSmallChar(String str) {
	        if (LOG.isDebugEnabled()) {
	            LOG.debug("containsSmallChar", "Entered str=" + str);
	        }
	        int a = 0;
	        if (str != null) {
	            final char arr[] = str.toCharArray();
	            for (int i = 0; i < arr.length; i++) {
	                if (arr[i] >= 'a' && arr[i] <= 'z') {
	                    ++a;
	                }
	                if (a >= 1) {
	                    break;
	                }
	            }
	        }
	        if (a == 1) {
	            return true;
	        } else {
	            return false;
	        }
	    }
	    
	    /**
	     * This method converts a string in the format "a=1&b=2&c=3" into a HashMap
	     * in the format "{a=1, b=2, c=3}"
	     * 
	     * @param str
	     *            The string to be converted to hash
	     * @param token1
	     *            The tokenizer : "&"
	     * @param token2
	     *            The tokenizer : "="
	     * @return Returns HashMap representation of the String passed
	     */
	    public static HashMap getString2Hash(String str, String token1, String token2) {
        final HashMap ht = new HashMap();
        StringTokenizer stToken1 = null;
        StringTokenizer stToken2 = null;
        String newString = "";
        stToken1 = new StringTokenizer(str, token1);
        while (stToken1.hasMoreTokens()) {
            newString = stToken1.nextToken();
            if (newString.indexOf(token2) < 0) {
                continue;
            }
            stToken2 = new StringTokenizer(newString, token2);
            if (stToken2.countTokens() == 2) {
                ht.put(stToken2.nextToken(), stToken2.nextToken());
            }
            if (stToken2.countTokens() == 1) {
                ht.put(stToken2.nextToken(),"");
           }
            
        }
        return ht;
	    }
	    
	    public static void validateConnection() {
	    	try{	
	    		int i = OracleUtil.getAvailableConnection();
	    		if(i < Integer.parseInt(Constants.getProperty("minpoolsize"))/7){
	    			EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DBUtil[validateConnection]", "", "", "", "Application is running out of Database connections - ");
	    			throw new SQLException();
	    		}
	    	}
	    	catch (SQLException e){
	    		LOG.error("An error occurred in validating Connection ------ ", e);
	    	}
	    }
	    
	    public static String getTwoDigit(int p_no) {
	        String digit = "";
	        if (p_no <= 9) {
	            digit = "0" + p_no;
	        } else {
	            digit = p_no + "";
	        }
	        return digit;
	    }
	    public static String getDisplayFormat(String text)
	    {
	    	int amountMultFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
            double number = Double.parseDouble(text);
            number=number/amountMultFactor;
            DecimalFormat format = new DecimalFormat("0.00");
            String formatted = format.format(number);
            return formatted;
	    }

	    public static StringBuilder appendZero(StringBuilder selectQueryBuff, String field, String returnName) {
			StringBuilder fieldStr = new StringBuilder(field);
			StringBuilder returnNameStr = new StringBuilder(returnName);
			int amountMultFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
			selectQueryBuff.append(" CASE WHEN to_number(trim(To_char(SUM(").append(fieldStr).append(")/").
			append(amountMultFactor).append(",").append(FORMAT).
			append(")),").append(FORMAT).append(") < '1' THEN '0' || trim(To_char(SUM(").append(fieldStr).append(")/").
			append(amountMultFactor).append(",").append(FORMAT).
			append(")) ELSE To_char(SUM(").append(fieldStr).append(")/").
			append(amountMultFactor).append(",").append(FORMAT).
			append(") END ").append(returnNameStr);
			return selectQueryBuff;
		}
	    
	    /**
	     * 
	     * @param date
	     * @return
	     */
	    public static String getDateWithDayOfMonth(String date) {
	    	String dateStr = PretupsI.EMPTY;
	    	final String methodName = "getDateWithDayOfMonth";
	    	if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, PretupsI.ENTERED);
	        }
	    	String dateFormatCalJava = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA);
	    	if(dateFormatCalJava.equalsIgnoreCase(PretupsI.DATE_FORMAT_DDMMYY)) {
	    		dateStr = "01/" + date;    		
	    	} else if(dateFormatCalJava.equalsIgnoreCase(PretupsI.DATE_FORMAT_YYYYMMDD)) {
	    		dateStr = date + "/01";
	    	}
	    	if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, PretupsI.EXITED);
	        }
	    	return dateStr;
	    }
	    /**
	     * 
	     * @param date
	     * @return
	     */
	    public static String getDateWithDayOfMonth(int dateOfMonth, String monthYear) {
	    	String dateStr = PretupsI.EMPTY;
	    	final String methodName = "getDateWithDayOfMonth";
	    	if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, PretupsI.ENTERED);
	        }
	    	String dateFormatCalJava = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA);
	    	if(dateFormatCalJava.equalsIgnoreCase(PretupsI.DATE_FORMAT_DDMMYY)) {
	    		dateStr = BTSLDateUtil.getGregorianDateInString(dateOfMonth + PretupsI.FORWARD_SLASH + monthYear);    		
	    	} else if(dateFormatCalJava.equalsIgnoreCase(PretupsI.DATE_FORMAT_YYYYMMDD)) {
	    		dateStr = BTSLDateUtil.getGregorianDateInString(monthYear + PretupsI.FORWARD_SLASH + dateOfMonth); 
	    	}
	    	if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, PretupsI.EXITED);
	        }
	    	return dateStr;
	    }
	    
	    public static StringBuilder formatQueryForInCase(StringBuilder selectQueryBuff, String parameter) {
	    	final String methodName = "formatQueryForInCase";
	    	if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, PretupsI.ENTERED);
	        }
			if(!BTSLUtil.isNullString(parameter)) {
				String[] arr = parameter.split("\\,");
				for (int i = 0; i < arr.length; i++) {
					selectQueryBuff.append(" ?");
					if (i != arr.length - 1) {
						selectQueryBuff.append(",");
					}
				}			
			}
			if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, PretupsI.EXITED);
	        }
			return selectQueryBuff;
		}
		
		public static int addParameterForInCase(PreparedStatement preparedStatement, String parameter, int index) throws SQLException {
			final String methodName = "addParameterForInCase";
	    	if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, PretupsI.ENTERED);
	        }
			if(!BTSLUtil.isNullString(parameter)) {
				String[] arr3 = parameter.split("\\,");
				for (int x = 0; x < arr3.length; x++) {
					arr3[x] =arr3[x].replace("'", "");
					preparedStatement.setString(index++, arr3[x]);
	            }				
			}
			if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, PretupsI.EXITED);
	        }
			return index;
		}
		/**
		 * This function will return date in the passed format
		 * Date passed should be Gregorian date - For other dates it'll Date will consider only Gregorian dates
		 * @param date
		 * @param dateInFormat
		 * @return
		 * @throws ParseException
		 */
		public static String getDateStringInFormat(String date, String dateInFormat) throws ParseException {
			final String methodName = "getDateStringInFormat";
			String finalDate = PretupsI.EMPTY;
			if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, PretupsI.ENTERED + " date passed : " + date + " convert into format : " + dateInFormat);
	        }
			String dateFormatCalJava = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA);
			if(!BTSLUtil.isNullString(date)) {
				String formatToUse;
				Date newDate = getDateFromDateString(date, BTSLDateUtil.getDateFormat(date));
				if(BTSLUtil.isNullString(dateInFormat)) {
					formatToUse = dateFormatCalJava;
				} else {
					formatToUse = dateInFormat;
				}
				finalDate = BTSLUtil.getDateStringFromDate(newDate, formatToUse);
//				finalDate = BTSLDateUtil.getFinalDate(finalDate);
		    	if (LOG.isDebugEnabled()) {
		            LOG.debug(methodName, PretupsI.EXITED + " date returned : " + finalDate);
		        }
			}
			return finalDate;
		}
		
		/**
	     * This method will return the date object from the passed transaction Id
	     * @param transId
	     */
	    public static Date getDateFromTransactionId(String transId) throws ParseException {
	    	final String methodName = "getDateFromTransactionId";
	    	if (LOG.isDebugEnabled()) {
	    		LOG.debug(methodName, PretupsI.ENTERED + " passed transaction id is = " + transId);
	        }
	    	Date date = new Date();
	    	String dateString = PretupsI.EMPTY;
	    	String dateStr = PretupsI.EMPTY;
	    	int moreChar = 0;
	    	String calenderType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CALENDAR_TYPE);
	    	if(!Character.isDigit(transId.charAt(1))) {
	    		moreChar = 1;
	        }
	    	if(PretupsI.GREGORIAN.equalsIgnoreCase(calenderType)) {
	    		dateStr = !BTSLUtil.isNullString(transId) ? transId.substring(5 + moreChar, 7 + moreChar) + PretupsI.FORWARD_SLASH
		    			+ transId.substring(3 + moreChar, 5 + moreChar) + PretupsI.FORWARD_SLASH + transId.substring(1 + moreChar, 3 + moreChar) : PretupsI.EMPTY;
	    		date = BTSLUtil.getDateFromDateString(dateStr, PretupsI.DATE_FORMAT_DDMMYY);
	    	} else if(PretupsI.PERSIAN.equalsIgnoreCase(calenderType)) {
	    		dateStr = !BTSLUtil.isNullString(transId) ? transId.substring(1 + moreChar, 3 + moreChar) + PretupsI.FORWARD_SLASH
		    			+ transId.substring(3 + moreChar, 5 + moreChar) + PretupsI.FORWARD_SLASH + transId.substring(5 + moreChar, 7 + moreChar) : PretupsI.EMPTY;
				String appendInYear = Integer.parseInt(transId.substring(1 + moreChar, 3 + moreChar)) >= 97 ? "13" : "14";//implemented for 100 years. Current year is 1397 in persian
				dateString = BTSLDateUtil.getGregorianDateInString(appendInYear+dateStr);
				date = BTSLUtil.getDateFromDateString(dateString, BTSLDateUtil.getDateFormat(dateString));
	    	}
	    	if (LOG.isDebugEnabled()) {
	    		LOG.debug(methodName, PretupsI.EXITED + PretupsI.LOGGER_DATE_RETURNED + dateString);
	        }
	        return date;
	    }
	    /**
	     * This function will check whether the passed String can be converted to Integer or not
	     * @param s
	     * @return
	     */
	    public static boolean isStringInt(String s){
	    	final String methodName = "isStringInt";
	        Scanner in = null;
			boolean isStringInt = false;
			try {
				in = new Scanner(s);
				isStringInt = in.hasNextInt();
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			} finally {
				try {
					if(in !=null) {
						in.close();
					}
				} catch (Exception e) {
					LOG.errorTrace(methodName, e);
				}
			}
	        return isStringInt;
	    }
	    
	    /**
	     * This function will return the table name to be used for query.
	     * This is for POC purpose
	     * @param table
	     * @return
	     */
	    public static String getTableName(String table) {
	    	if (LOG.isDebugEnabled()) {
	            LOG.debug("getTableName", "Entered table name " + table);
	        }
	    	String isNewTableToUse = getTrimmedValue(Constants.getProperty("IS_NEW_TABLE_TO_USE_PG10"));
	        String tableName;
	        if(PretupsI.YES.equalsIgnoreCase(isNewTableToUse)) {
	        	String appendName = getTrimmedValue(Constants.getProperty("NEW_TABLE_PG10"));
	        	tableName = table + appendName;
	        } else {
	        	tableName = table;
	        }
	        if (LOG.isDebugEnabled()) {
	            LOG.debug("getTableName", "Exiting table name " + tableName);
	        }
	        return tableName;
	    }
	    
	    /**
	     * This function will return the trimmed value of the passed parameter
	     * @param parameter
	     * @return
	     */
	    public static String getTrimmedValue(String parameter) {
			return BTSLUtil.isNullString(parameter) ? PretupsI.EMPTY : parameter.trim();
		}
	    
	    /** 
		 * This function will convert Active Profile(Applicable date) - Ex - PROD15(1397/02/02) to PROD15(22/04/18) - to gregorian date
		 * To the passed calendar type
		 * If we need to change to Persian Calendar type make user the passed actProfAppFrom is in Gregorian
		 * and vice-versa as well
		 * @param actProfAppFrom
		 * @param toCalendarType
		 * @return
		 */
		public static String getActiveProfileApplicableFrom(String actProfAppFrom, String toCalendarType) {
			String finalStr;
			if(!BTSLUtil.isNullString(actProfAppFrom) && actProfAppFrom.contains(PretupsI.ROUND_PARAN_START) && actProfAppFrom.contains(")")) {
				String tempDate;
				int paranStart;
				int paranEnd;
				paranStart = actProfAppFrom.indexOf('(');
				paranEnd = actProfAppFrom.indexOf(')');
		        String subsTring = actProfAppFrom.substring(paranStart + 1, paranEnd);
		        if(!PretupsI.GREGORIAN.equalsIgnoreCase(toCalendarType)) {
		        	tempDate = BTSLDateUtil.getSystemLocaleDate(subsTring);		        	
		        } else {
		        	tempDate = BTSLDateUtil.getGregorianDateInString(subsTring);
		        }
		        finalStr = actProfAppFrom.substring(0, paranStart + 1) + tempDate + actProfAppFrom.substring(paranEnd);
		    } else {
		    	finalStr = actProfAppFrom;
		    }
			return finalStr;
		}
		
		/**
		 * This function will return ddMMyy
		 * @param date
		 * @return
		 */
		public static String getDateStrForName(Date date) {
			return BTSLDateUtil.getSystemLocaleDate(date, PretupsI.DATE_FORMAT_DDMMYY_WOSEPARATOR);
		}
		
		public static ArrayList getInstrumentListForUser(ArrayList instTypeList, String[] paymentTypes, boolean channelUserLoginedFlag) {
            ArrayList instTypeListFinal = new ArrayList<>();
            if(instTypeList == null || instTypeList.size() < 1) {
                  return null ;
            }
            if(channelUserLoginedFlag) {
            	if(!BTSLUtil.isNullArray(paymentTypes)) {
            		for(int i=0; i<paymentTypes.length; i++) {
  	                  for(int j=0; j<instTypeList.size(); j++) {
  	                        if(((ListValueVO)instTypeList.get(j)).getValue().equals(paymentTypes[i])) {
  	                              instTypeListFinal.add(instTypeList.get(j));
  	                        }
  	                  }
            		}           		
            	}
            } else {
                for(int j=0; j<instTypeList.size(); j++) {
                      if(!PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equals(((ListValueVO)instTypeList.get(j)).getValue())) {
                            instTypeListFinal.add(instTypeList.get(j));
                      }
                }
            }
            return instTypeListFinal;
      }

	/**
     * change one date format to another date format
     * @param date
     * @param givenformat
     * @param resultformat
     * @return string
     */
public static String formatDateFromOnetoAnother(String date,String givenformat,String resultformat){
	           final String methodName="formatDateFromOnetoAnother";
	             String result="";
	           SimpleDateFormat sdf;
	           SimpleDateFormat sdf1;
	           try{
	               sdf = new SimpleDateFormat(givenformat);
	               sdf1 = new SimpleDateFormat(resultformat);
	               result=sdf1.format(sdf.parse(date));
	           }catch(Exception e){
	               LOG.trace(methodName, e);
	                     LOG.error(methodName, "Exception : "+e.getMessage());
	               return "";
	           }finally{
	               sdf=null;
	               sdf1=null;
	           }
	           return result;
	       }

	public static Locale getSystemLocaleForEmail() {
		String emailDefaultLocale = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_DEFAULT_LOCALE);
		String localeProp = emailDefaultLocale;
		if (!BTSLUtil.isNullString(localeProp)) {
			String[] localePropArr = localeProp.split("_");
			if (localePropArr.length > 1) {
				return new Locale(localePropArr[0], localePropArr[1]);
			}
		}
		return getSystemLocale();
	}
	
	public static Locale getSystemLocale() {
		String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		return new Locale(defaultLanguage, defaultCountry);
	}
	
	/**
	 * Get Message from Messages.props on the basis of locale
	 * @param locale
	 * @param key
	 * @param args
	 * @return
	 */
	public static String getMessage(Locale locale,String key)
	{
		final String METHOD_NAME = "getMessage";
		if(LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME,"Entered");
		}
        HttpServletRequest request = null;
        String language = null;
        String country = null;
        if (RequestContextHolder.getRequestAttributes() != null) {

            request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

            if(request != null) {
                language = request.getHeader("language");
                country = request.getHeader("country");
            }
        }

        if (language != null && country != null) {
            locale = new Locale(language, country);
        }
		String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		String message=null;
		try
		{
			if(locale==null)
			{
				LOG.error(METHOD_NAME,"Locale not defined considering default locale "+defaultLanguage+" "+defaultCountry+"    key: "+key);
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"BTSLUtil[getMessage]","",""," ","Locale not defined considering default locale "+defaultLanguage+" "+defaultCountry+"    key: "+key);
				locale=new Locale(defaultLanguage,defaultCountry);
			}
			if(LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME,"entered locale "+locale.getDisplayName()+" key: "+key);
			}
			MessagesCache messagesCache=MessagesCaches.get(locale);
			if(messagesCache==null)
			{
				LOG.error(METHOD_NAME,"Messages cache not available for locale: "+locale.getDisplayName()+"    key: "+key);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"BTSLUtil[getMessage]","",""," ","Messages cache not available for locale "+locale.getDisplayName()+"    key: "+key);
				locale=new Locale(defaultLanguage,defaultCountry);
				messagesCache=MessagesCaches.get(locale);
				if(messagesCache==null) {
					return null;
				}
			}
			message=messagesCache.getProperty(key);
		}
		catch(Exception e){
			LOG.errorTrace(METHOD_NAME,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"BTSLUtil[getMessage]","","",""," Exception:"+e.getMessage());
		}
		finally
		{
			if(LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME,"Exiting message: "+message);
			}
		}
		return message;
	}
	
	// IPv4 & IPv6 validation 
		private static Pattern VALID_IPV4_PATTERN = null;
		private static Pattern VALID_IPV6_PATTERN = null;
		private static final String ipv4Pattern = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
		private static final String ipv6Pattern = "^(?:(?:(?:(?:(?:(?:(?:[0-9a-fA-F]{1,4})):){6})(?:(?:(?:(?:(?:[0-9a-fA-F]{1,4})):(?:(?:[0-9a-fA-F]{1,4})))|(?:(?:(?:(?:(?:25[0-5]|(?:[1-9]|1[0-9]|2[0-4])?[0-9]))"
				+ "\\"
				+ ".){3}(?:(?:25[0-5]|(?:[1-9]|1[0-9]|2[0-4])?[0-9])))))))|(?:(?:::(?:(?:(?:[0-9a-fA-F]{1,4})):){5})(?:(?:(?:(?:(?:[0-9a-fA-F]{1,4})):(?:(?:[0-9a-fA-F]{1,4})))|(?:(?:(?:(?:(?:25[0-5]|(?:[1-9]|1[0-9]|2[0-4])?[0-9]))"
				+ "\\"
				+ ".){3}(?:(?:25[0-5]|(?:[1-9]|1[0-9]|2[0-4])?[0-9])))))))|(?:(?:(?:(?:(?:[0-9a-fA-F]{1,4})))?::(?:(?:(?:[0-9a-fA-F]{1,4})):){4})(?:(?:(?:(?:(?:[0-9a-fA-F]{1,4})):(?:(?:[0-9a-fA-F]{1,4})))|(?:(?:(?:(?:(?:25[0-5]|(?:[1-9]|1[0-9]|2[0-4])?[0-9]))"
				+ "\\"
				+ ".){3}(?:(?:25[0-5]|(?:[1-9]|1[0-9]|2[0-4])?[0-9])))))))|(?:(?:(?:(?:(?:(?:[0-9a-fA-F]{1,4})):){0,1}(?:(?:[0-9a-fA-F]{1,4})))?::(?:(?:(?:[0-9a-fA-F]{1,4})):){3})(?:(?:(?:(?:(?:[0-9a-fA-F]{1,4})):(?:(?:[0-9a-fA-F]{1,4})))|(?:(?:(?:(?:(?:25[0-5]|(?:[1-9]|1[0-9]|2[0-4])?[0-9]))"
				+ "\\"
				+ ".){3}(?:(?:25[0-5]|(?:[1-9]|1[0-9]|2[0-4])?[0-9])))))))|(?:(?:(?:(?:(?:(?:[0-9a-fA-F]{1,4})):){0,2}(?:(?:[0-9a-fA-F]{1,4})))?::(?:(?:(?:[0-9a-fA-F]{1,4})):){2})(?:(?:(?:(?:(?:[0-9a-fA-F]{1,4})):(?:(?:[0-9a-fA-F]{1,4})))|(?:(?:(?:(?:(?:25[0-5]|(?:[1-9]|1[0-9]|2[0-4])?[0-9]))"
				+ "\\"
				+ ".){3}(?:(?:25[0-5]|(?:[1-9]|1[0-9]|2[0-4])?[0-9])))))))|(?:(?:(?:(?:(?:(?:[0-9a-fA-F]{1,4})):){0,3}(?:(?:[0-9a-fA-F]{1,4})))?::(?:(?:[0-9a-fA-F]{1,4})):)(?:(?:(?:(?:(?:[0-9a-fA-F]{1,4})):(?:(?:[0-9a-fA-F]{1,4})))|(?:(?:(?:(?:(?:25[0-5]|(?:[1-9]|1[0-9]|2[0-4])?[0-9]))"
				+ "\\"
				+ ".){3}(?:(?:25[0-5]|(?:[1-9]|1[0-9]|2[0-4])?[0-9])))))))|(?:(?:(?:(?:(?:(?:[0-9a-fA-F]{1,4})):){0,4}(?:(?:[0-9a-fA-F]{1,4})))?::)(?:(?:(?:(?:(?:[0-9a-fA-F]{1,4})):(?:(?:[0-9a-fA-F]{1,4})))|(?:(?:(?:(?:(?:25[0-5]|(?:[1-9]|1[0-9]|2[0-4])?[0-9]))"
				+ "\\"
				+ ".){3}(?:(?:25[0-5]|(?:[1-9]|1[0-9]|2[0-4])?[0-9])))))))|(?:(?:(?:(?:(?:(?:[0-9a-fA-F]{1,4})):){0,5}(?:(?:[0-9a-fA-F]{1,4})))?::)(?:(?:[0-9a-fA-F]{1,4})))|(?:(?:(?:(?:(?:(?:[0-9a-fA-F]{1,4})):){0,6}(?:(?:[0-9a-fA-F]{1,4})))?::))))$";

		static {
			try {
				VALID_IPV4_PATTERN = Pattern.compile(ipv4Pattern, Pattern.CASE_INSENSITIVE);
				VALID_IPV6_PATTERN = Pattern.compile(ipv6Pattern, Pattern.CASE_INSENSITIVE);
			} catch (PatternSyntaxException e) {
				 LOG.trace("BTSLUtil", e);
                 LOG.error("BTSLUtil", "Exception : "+e.getMessage());
			}
		}
		
		public static boolean isIpAddress(String ipAddress) {
			Matcher m1 = BTSLUtil.VALID_IPV4_PATTERN.matcher(ipAddress);
			if (m1.matches() && validateIpAddresswith0(ipAddress,"\\.")) {
				return true;
			}
			if(checkCharacterfromInitialandLast(ipAddress))
			{
				ipAddress = ipAddress.replace("[","");
				ipAddress = ipAddress.replace("]","");
			}
			else{
				return false;
			}
			Matcher m2 = BTSLUtil.VALID_IPV6_PATTERN.matcher(ipAddress);
			if (m2.matches()&& validateIpAddresswith0(ipAddress,":")) {
				return true;
			}
			return false;
		}
		
		public static boolean validateIpAddresswith0(String ipAddress,String ipCheck) {
			String[] s = ipAddress.split(ipCheck);
			if (!(s[0] == null || s[0].trim().length() == 0)) {
				Long x;
				try {
					x = Long.parseLong(s[0]);
				} catch (NumberFormatException e) {
					return true;
				}
				if (x == 0) {
					return false;
				} else {
					return true;
				}
			}
			return false;
		}
		
		public static boolean checkCharacterfromInitialandLast(String ipAddress)
		{
			int leString = ipAddress.length();
			if(!(ipAddress.charAt(0)=='[' && ipAddress.charAt(leString - 1)==']'))
			{
				return false;
			}
			return true;
		}
	
		 public static boolean isNullObject(Object obj) {
		        if (obj == null) {
		            return true;
		        } else {
		            return false;
		        }
		 }
		
		 //Compares float values for inequality
		 public static boolean floatEqualityCheck(Double num1, Double num2, String ineqality) {
			 int amountMultFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
			 long mult_factor = amountMultFactor;
			 long firstNumber = new Double((num1*mult_factor)).longValue();
			 long secondNumber = new Double((num2*mult_factor)).longValue();
			 if(ineqality.equals("=="))
				 return firstNumber == secondNumber;
			 
			 else if(ineqality.equals("!="))
				 return firstNumber != secondNumber;
			 
			 else if(ineqality.equals(">="))
				 return firstNumber >= secondNumber;
			 
			 else if(ineqality.equals("<="))
				 return firstNumber <= secondNumber;
			 
			 else if(ineqality.equals(">"))
				 return firstNumber > secondNumber;
			 
			 else if(ineqality.equals("<"))
				 return firstNumber < secondNumber;

			 return false;
		 }
		 
	    public static int getApprovalLevel(String categoryCode) {
	    	
	    	if(PretupsI.SUPER_ADMIN.equals(categoryCode)) {
	    		int vomsMaxApprovalLevel = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_MAX_APPROVAL_LEVEL))).intValue();
		    	return vomsMaxApprovalLevel;
	    	} else if(PretupsI.SUB_SUPER_ADMIN.equals(categoryCode)) {
	    		int vomsMaxApprovalLevelSubSuperAdmin = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_MAX_APPROVAL_LEVEL_SUB_SUPER_ADMIN))).intValue();
		    	return vomsMaxApprovalLevelSubSuperAdmin;
	    	} else if(PretupsI.SUPER_NETWORK_ADMIN.equals(categoryCode)) {
	    		int vomsMaxApprovalLevelSuperNwAdmin = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_MAX_APPROVAL_LEVEL_SUPER_NW_ADMIN))).intValue();
		    	return vomsMaxApprovalLevelSuperNwAdmin;
	    	} else if(PretupsI.NETWORK_ADMIN.equals(categoryCode)) {
	    		int vomsMaxApprovalLevelNwAdmin = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_MAX_APPROVAL_LEVEL_NW_ADMIN))).intValue();
	    		return vomsMaxApprovalLevelNwAdmin;
	    	}
	    	return 0;
	    }
	    
	    public static ArrayList getSegmentList(String voucherType, ArrayList segmentList) {
	    	if (LOG.isDebugEnabled()) {
	            LOG.debug("getSegmentList", "voucherType " + voucherType);
	        }
	    	/*ArrayList segmentListToUser = new ArrayList();
	    	if(!BTSLUtil.isNullString(voucherType) && segmentList != null && !segmentList.isEmpty()) {
	    		segmentListToUser = new ArrayList(segmentList);
	            if(segmentListToUser != null && !segmentListToUser.isEmpty() && VOMSI.VOUCHER_TYPE_TEST_ELECTRONIC.equals(voucherType) || 
	            		VOMSI.VOUCHER_TYPE_ELECTRONIC.equals(voucherType)) {
	            	for(int i=0; i < segmentListToUser.size(); i++) {
	            		if(VOMSI.VOUCHER_SEGMENT_NATIONAL.equals(((ListValueVO)segmentListToUser.get(i)).getValue())) {
	            			segmentListToUser.remove(i);
	            		}
	            	}
	            }	    		
	    	}*/
            return segmentList;
	    }
	    
	    public static String getSegmentDesc(String segment) {
	    	if (LOG.isDebugEnabled()) {
	            LOG.debug("getSegmentDesc", "Entered - segment " + segment);
	        }
	    	ArrayList segmentList = LookupsCache.loadLookupDropDown(VOMSI.VOUCHER_SEGMENT, true);
            ListValueVO segmentVo = BTSLUtil.getOptionDesc(segment, segmentList);
            String segmentDesc = "";
            if(!BTSLUtil.isNullObject(segmentVo)) {
            	segmentDesc = segmentVo.getLabel();                	
            }
            if (LOG.isDebugEnabled()) {
	            LOG.debug("getSegmentDesc", "Exited - segmentDesc " + segmentDesc);
	        }
            return segmentDesc;
	    }
	    
	    public static String getPrefixCodeUsingNwCode(String networkCode) {
	    	final String methodName="getPrefixCodeUsingNwCode";
	    	if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, "Entered - networkCode " + networkCode);
	        }
	    	String nwCodeNwPrefixMapping = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.NW_CODE_NW_PREFIX_MAPPING);
	    	String nwCodePrefixMappingStr = nwCodeNwPrefixMapping;
	    	if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, "nwCodePrefixMappingStr = " + networkCode);
	        }
	    	Map<String, String> nwCodePrefixMapping = new HashMap<String, String>();
	    	if(!BTSLUtil.isNullString(nwCodePrefixMappingStr)) {
	    		String[] nwCodePrefixMappingArr = nwCodePrefixMappingStr.split(",");
	    		for (int i=0;i<nwCodePrefixMappingArr.length;i++) {
	    		    String nwCodePrefixMap = nwCodePrefixMappingArr[i];
	    		    String[] keyValue = nwCodePrefixMap.split("=");
	    		    nwCodePrefixMapping.put(keyValue[0], keyValue[1]);
	    		}
	    	}
	    	String prefixCode = nwCodePrefixMapping.get(networkCode);
	    	if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, "Entered - prefixCode " + prefixCode);
	        }
	    	return prefixCode;
	    }
	    
	    public static boolean isSegmentVisible(String categoryCode, String categoryCodeInForm) {
	    	final String methodName="isSegmentVisible";
	    	if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, "Entered - categoryCode " + categoryCode);
	        }
	    	if((PretupsI.SUPER_ADMIN.equals(categoryCode) || PretupsI.SUB_SUPER_ADMIN.equals(categoryCode)) && 
	    			(PretupsI.SUPER_ADMIN.equals(categoryCodeInForm) || PretupsI.SUB_SUPER_ADMIN.equals(categoryCodeInForm) || 
	    			PretupsI.SUPER_NETWORK_ADMIN.equals(categoryCodeInForm) || PretupsI.NETWORK_ADMIN.equals(categoryCodeInForm))) {
	    		return true;
	    	}
	    	return false;
	    }
	    
	    public static boolean isNullOrEmptyList(ArrayList list) {
	        if (list == null || list.size() == 0) {
	            return true;
	        } else {
	            return false;
	        }
	    }
	    
	    public static String getVoucherTypeDesc(ArrayList<VoucherTypeVO> voucherList,String voucherType) {
	    	if (LOG.isDebugEnabled()) {
	            LOG.debug("getVoucherTypeDesc", "Entered - voucher type " + voucherType);
	        }
            String voucherTypeDesc = "";
            if(!BTSLUtil.isNullString(voucherType)){
            for(VoucherTypeVO voucher : voucherList){
            	 if(voucherType.equals(voucher.getVoucherType())){
            		 voucherTypeDesc=voucher.getVoucherName();
            		 break;
            		 }
            }
            }
            if (LOG.isDebugEnabled()) {
	            LOG.debug("getVoucherTypeDesc", "Exited - voucherTypeDesc " + voucherTypeDesc);
	        }
            return voucherTypeDesc;
	    }
	    public static String getVoucherTypeCode(ArrayList<VomsCategoryVO> voucherList,String voucherType) {
	    	if (LOG.isDebugEnabled()) {
	            LOG.debug("getVoucherTypeCode", "Entered - voucher type " + voucherType);
	        }
            String voucherTypeDesc = "";
            if(!BTSLUtil.isNullString(voucherType)){
            for(VomsCategoryVO voucher : voucherList){
            	 if(voucherType.equals(voucher.getVoucherType())){
            		 voucherTypeDesc=voucher.getType();
            		 break;
            		 }
            }
            }
            if (LOG.isDebugEnabled()) {
	            LOG.debug("getVoucherTypeCode", "Exited - voucherTypeDesc " + voucherTypeDesc);
	        }
            return voucherTypeDesc;
	    }
	    public static String getVoucherProductName(ArrayList<VomsProductVO> voucherProductList,String voucherProductId) {
	    	if (LOG.isDebugEnabled()) {
	            LOG.debug("getVoucherTypeDesc", "Entered - voucher type " + voucherProductId);
	        }
            String voucherTypeDesc = "";
            if(!BTSLUtil.isNullString(voucherProductId)){
            for(VomsProductVO productId : voucherProductList){
            	 if(voucherProductId.equals(productId.getProductID())){
            		 voucherTypeDesc=productId.getProductName();
            				 break;
            		 }
            }
            }
            if (LOG.isDebugEnabled()) {
	            LOG.debug("getVoucherTypeDesc", "Exited - voucherTypeDesc " + voucherTypeDesc);
	        }
            return voucherTypeDesc;
	    }

	    public static String getVoucherType(ArrayList<VoucherTypeVO> voucherList,String voucherTypeDesc) {
	    	if (LOG.isDebugEnabled()) {
	            LOG.debug("getVoucherTypeDesc", "Entered - voucher type " + voucherTypeDesc);
	        }
            String voucherType = "";
            if(!BTSLUtil.isNullString(voucherTypeDesc)){
            for(VoucherTypeVO voucher : voucherList){
            	 if(voucherTypeDesc.equals(voucher.getVoucherName())){
            		 voucherType=voucher.getVoucherType();
            		 break;
            		 }
            }
            }
            if (LOG.isDebugEnabled()) {
	            LOG.debug("getVoucherType", "Exited - voucherType " + voucherType);
	        }
            return voucherType;
	    }
	    
	    public static String getVoucherProductId(ArrayList<VomsProductVO> voucherProductList,String voucherProductName) {
	    	if (LOG.isDebugEnabled()) {
	            LOG.debug("getVoucherProductId", "Entered - voucher type " + voucherProductName);
	        }
            String voucherProductId = "";
            if(!BTSLUtil.isNullString(voucherProductName)){
            for(VomsProductVO productId : voucherProductList){
            	 if(voucherProductName.equals(productId.getProductName())){
            		 voucherProductId=productId.getProductID();
            				 break;
            		 }
            }
            }
            if (LOG.isDebugEnabled()) {
	            LOG.debug("getVoucherProductId", "Exited - voucherTypeDesc " + voucherProductId);
	        }
            return voucherProductId;
	    }
	    
	    
	    public static String getSegment(String segmentDesc) {
	    	if (LOG.isDebugEnabled()) {
	            LOG.debug("getSegment", "Entered - segmentDesc " + segmentDesc);
	        }
	    	ArrayList segmentList = LookupsCache.loadLookupDropDown(VOMSI.VOUCHER_SEGMENT, true);
            ListValueVO segmentVo = BTSLUtil.getOptionKey(segmentDesc, segmentList);
            String segment = "";
            if(!BTSLUtil.isNullObject(segmentVo)) {
            	segment = segmentVo.getValue();                	
            }
            if (LOG.isDebugEnabled()) {
	            LOG.debug("getSegment", "Exited - segment " + segment);
	        }
            return segment;
	    }
	     
	    public static ListValueVO getOptionKey(String p_code, List p_list) {
	        if (LOG.isDebugEnabled()) {
	            LOG.debug("getOptionDesc", "Entered: p_code=" + p_code + " p_list=" + p_list);
	        }
	        ListValueVO vo = null;
	        boolean flag = false;
	        if (p_list != null && !p_list.isEmpty()) {
	            for (int i = 0, j = p_list.size(); i < j; i++) {
	                vo = (ListValueVO) p_list.get(i);
	                if (vo.getLabel().equalsIgnoreCase(p_code)) {
	                    flag = true;
	                    break;
	                }
	            }
	        }
	        if (!flag) {
	            vo = new ListValueVO();
	        }
	        if (LOG.isDebugEnabled()) {
	            LOG.debug("getOptionDesc", "Exited: vo=" + vo);
	        }
	        return vo;
	    }
	    
	    
	    public static String getValidityPeriodType(ArrayList<ListValueVO> validityList,String validityTypeName) {
	    	if (LOG.isDebugEnabled()) {
	            LOG.debug("getVoucherTypeDesc", "Entered - validity Type Name " + validityTypeName);
	        }
            String validityType = "";
            if(!BTSLUtil.isNullString(validityTypeName)){
            for(ListValueVO lookupsVO : validityList){
            	 if(validityTypeName.equals(lookupsVO.getLabel())){
            		 validityType=lookupsVO.getValue();
            		 break;
            		 }
            }
            }
            if (LOG.isDebugEnabled()) {
	            LOG.debug("getVoucherType", "Exited - validityType " + validityType);
	        }
            return validityType;
	    }
	    
	    public static boolean isVoucherService(String service) {
	    	final String methodName = "isVoucherService";
	    	if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, "Entered - service " + service);
	        }
	    	String vmsServices = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VMS_SERVICES);
            boolean isPresent = false;
            if(!BTSLUtil.isNullString(vmsServices)){
            	isPresent = Arrays.asList(vmsServices.split(",")).contains(service);
            }
            if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, "Exited -  " + isPresent);
	        }
            return isPresent;
	    }
	    
	    
	    public static void closeOpenStream(PrintWriter out, File newFile) throws IOException{
	    	out.close();
	    	out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
	    }
	    
	    public static void pstmtForInQuery(String [] args, StringBuilder sb) {
	    	final String methodName = "pstmtForInQuery";
	    	if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, "Entered - service " + sb);
	        }
	    	sb.append("(");
            for(int i=0;i<args.length-1;i++)
            {
            	sb.append("?,");
            }
            sb.append("?)");
            if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, "Exiting -  " + sb);
	        }
	    }
	    
	    public static void pstmtForInQuery(String [] args, StringBuffer sb) {
	    	final String methodName = "pstmtForInQuery";
	    	if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, "Entered - service " + sb);
	        }
	    	sb.append("(");
            for(int i=0;i<args.length-1;i++)
            {
            	sb.append("?,");
            }
            sb.append("?)");
            if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, "Exiting -  " + sb);
	        }
	    }
	    
	    

    /**
     * Utility Method for grep and cut functions - internaly utilizes shell grep and cut commands
     * @param searchPattern
     * @param filePath
     * @param filePathNew
     * @param userId
     * @param cutColumn
     * @param startColumn
     * @return  Integer value. zero means sucess
     * @param prepareNewFile
     */
	
	public static int grepAndPrepareFile(String searchPattern, String filePath, String filePathNew, String userId,
			boolean cutColumn, int startColumn, boolean prepareNewFile) {

		final String methodName = "grepAndPrepareFile";
		String[] cmd = new String[3];

		BufferedReader reader = null;
		int exitVal = 0;
		try {

			StringBuffer loggerValue = new StringBuffer("");

			if (LOG.isDebugEnabled()) {
				
				loggerValue.append(methodName+" Entered: searchPattern=");
				loggerValue.append(searchPattern);

				loggerValue.append("searchPattern=");
				loggerValue.append(searchPattern);

				loggerValue.append("filePath=");
				loggerValue.append(filePath);

				loggerValue.append("filePathNew=");
				loggerValue.append(filePathNew);

				loggerValue.append("userId=");
				loggerValue.append(userId);

				loggerValue.append("cutColumn=");
				loggerValue.append(cutColumn);

				loggerValue.append("startColumn=");
				loggerValue.append(startColumn);

				loggerValue.append("prepareNewFile=");
				loggerValue.append(prepareNewFile);

				
				LOG.debug(methodName, loggerValue);
			}
			
			cmd[0] = "sh";
			cmd[1] = "-c";

			if (cutColumn == false) {
				if (prepareNewFile == true) {
					cmd[2] = "grep \"" + searchPattern + "\" " + filePath + "   > " + filePathNew;
				} else {
					cmd[2] = "grep \"" + searchPattern + "\" " + filePath + "   >> " + filePathNew;
				}

			} else {
				if (prepareNewFile == true) {
					cmd[2] = "grep \"" + searchPattern + "\" " + filePath + " | cut -d \",\" -f " + startColumn
							+ "-  > " + filePathNew;
				} else {
					cmd[2] = "grep \"" + searchPattern + "\" " + filePath + " | cut -d \",\" -f " + startColumn
							+ "-  >> " + filePathNew;
				}

			}

			Process process = Runtime.getRuntime().exec(cmd);
			StringBuilder output = new StringBuilder();

			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}

			exitVal = process.waitFor();
			if (exitVal == 0) {
				LOG.debug(methodName, "Success " + output);
			} else {
				LOG.debug(methodName, "Execution failed " + exitVal);
			}

			try {
				reader.close();
			} catch (Exception e) {
				LOG.error(methodName, "Not able to close BufferedReader " + e);
			}

		} catch (Exception e) {
			LOG.errorTrace(methodName, e);
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
				LOG.error(methodName, "Not able to close BufferedReader " + e);
			}

		}

		return exitVal;
	}
	/**
	 * To validate the payment type
	 * @param paymenttype
	 * @return
	 */
	public static boolean isPaymentTypeValid(String paymentType) {
		String methodName = "isPaymentTypeValid";
		if (LOG.isDebugEnabled())
			LOG.debug("BTSLUtil", "Entered: " + methodName);
		ArrayList<ListValueVO> paymentInstList = LookupsCache.loadLookupDropDown(PretupsI.C2C_PAYMENT_INSTRUMENT_TYPE, true);
		for(ListValueVO s : paymentInstList){
			if(s.getValue().equalsIgnoreCase(paymentType)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Method to get the date one month before the given date, if not 
	 * available then return the next available date
	 * @param currDate
	 * @return Date
	 */
	public static Date getDateOneMonthBeforeFromDate(Date currDate){
		String methodName = "getDateOneMonthBeforeFromDate";
		if (LOG.isDebugEnabled())
			LOG.debug("BTSLUtil", "Entered: " + methodName);
		Date beforeDate = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(currDate);
		int month = cal.get(Calendar.MONTH);
		month++;
		int year = cal.get(Calendar.YEAR);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int leapYear = 0;
		if(year % 4 == 0 )
			leapYear=1;
		if((month == 3 || month == 5 ||month == 7 || month == 10 || month == 12 )&& day == 31){
			day = 1;
			Calendar date = Calendar.getInstance();
			date.set(Calendar.YEAR, year);
		    date.set(Calendar.MONTH, month-1);
		    date.set(Calendar.DAY_OF_MONTH, day);
		    beforeDate = date.getTime();
		}
		else if((month == 3 && day ==29 && leapYear ==0)||(month == 3 && day ==30)){
			day = 1;
			Calendar date = Calendar.getInstance();
			date.set(Calendar.YEAR, year);
		    date.set(Calendar.MONTH, month-1);
		    date.set(Calendar.DAY_OF_MONTH, day);
		    beforeDate = date.getTime();
		}
		else
		{
			cal.setTime(currDate);
			cal.add(Calendar.MONTH, -1);
			beforeDate = cal.getTime();
		}
		
		return beforeDate;
		
		
	}
	/**
	 * Method to get the date one month before the given date, if not 
	 * available then return the next available date
	 * @param currDate
	 * @return Date
	 */
	public static Date getDateOneMonthBeforeToDate(Date currDate){
		String methodName = "getDateOneMonthBeforeToDate";
		if (LOG.isDebugEnabled())
			LOG.debug("BTSLUtil", "Entered: " + methodName);
		Calendar cal = Calendar.getInstance();
		cal = Calendar.getInstance();
		cal.setTime(currDate);
		cal.add(Calendar.MONTH, -1);
		Date previousTo = cal.getTime();
		return previousTo;
		
	}
	/**
	 * Method to get difference in dates in minutes
	 * @param date1
	 * @param  date2
	 */
	 public static long getDifferenceInUtilDatesinSeconds(java.util.Date date1, java.util.Date date2) {
	        final long dt1 = date1.getTime();
	        final long dt2 = date2.getTime();
	        
	        final long diff = dt2-dt1;
	        long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diff);
	        return diffInSec;
	    }
	 
	 
	 public static boolean validateFileFormat(String fileName) {
		 
		 String[] allowedFormatsArr = {".jpeg",".jpg",".pdf",".png"};
		 
		 List<String> allowedFormats = Arrays.asList(allowedFormatsArr);
		 
		 if(allowedFormats.contains(fileName.substring(fileName.lastIndexOf(".")).toLowerCase())) {
			 return true;
		 }
		 
		 return false;
	 }

	 /**
		 * Validate the null check for Object super class.
		 *
		 * @param str - input
		 * @return boolean
		 */
		public static boolean isNullorEmpty(Object str) {
			return (null == str || "".equals(str) || "null".equals(str) || "undefined".equals(str));
		}
		

	    /**
	     * @param regEx
	     * @return
	     */
	    public static boolean isAlphaNumericWithUnderscore(String regEx) {
	        
	    	String regExp = "[a-zA-Z\\d_]+?";
	        final Pattern p = Pattern.compile(regExp);
	    	
	       //	final Pattern p = Pattern.compile("[a-zA-Z\\d]+?");
	        
	        // Match the given string with the pattern
	        final Matcher m = p.matcher(regEx);
	        // check whether match is found
	        return m.matches();
	    }	
	    
	    
	    public static String checkNullandReturnEmtpyString(String inputData) {
	    	
	    	 if(inputData==null) {
	    		 return "";
	    	 }else {
	    		 return inputData;
	    	 }
	    	
	    }
	    
		public static void updateMapViaMicroServiceResultSet(Map linkedHashMap,
				HashMap<String, HashMap<String, String>> resultSetFinal) {

			
			for (Object key : linkedHashMap.keySet()) {
				ChannelUserVO channelVOObj = (ChannelUserVO) linkedHashMap.get(key);

				if (resultSetFinal.get(channelVOObj.getTransferProfileID()) != null) {
					channelVOObj.setTransferProfileName(
							resultSetFinal.get(channelVOObj.getTransferProfileID()).get("profileName"));// TCP

					channelVOObj.setTransferProfileStatus(
							resultSetFinal.get(channelVOObj.getTransferProfileID()).get("status"));// TCP

					linkedHashMap.put(key, channelVOObj);
				} else {
					linkedHashMap.remove(key);
				}
			}
		}

		
		
		public static ArrayList<ChannelUserVO> updateMapViaMicroServiceResultSet(ArrayList list,
				HashMap<String, HashMap<String, String>> resultSetFinal) {

			ArrayList<ChannelUserVO> list2 = new ArrayList();
			
			Iterator itr = list.iterator();
			
			while (itr.hasNext()) {
				
				ChannelUserVO channelVOObj = (ChannelUserVO) itr.next();

				if (resultSetFinal.get(channelVOObj.getTransferProfileID()) != null) {
					channelVOObj.setTransferProfileName(
							resultSetFinal.get(channelVOObj.getTransferProfileID()).get("profileName"));// TCP

					channelVOObj.setTransferProfileStatus(
							resultSetFinal.get(channelVOObj.getTransferProfileID()).get("status"));// TCP

					list2.add(channelVOObj);
				} 
			}
			
			return list2;
		}
		

		public static HashMap<String, HashMap<String, String>> fetchMicroServiceTCPDataByKey(Set<String> columns, SearchCriteria searchCriteria) {

			java.util.List<HashMap<String, String>> resultSet = BTSLUtil.invokeService("TRANSFER_PROFILE", columns,
					searchCriteria);
			HashMap<String, HashMap<String, String>> resultSetFinal = new HashMap<String, HashMap<String, String>>();

			if (resultSet != null && resultSet.size() > 0) {

				Iterator<HashMap<String, String>> itrResultSet = resultSet.iterator();

				while (itrResultSet.hasNext()) {
					HashMap<String, String> resultMapObj = itrResultSet.next();
					resultSetFinal.put(resultMapObj.get("profileId"), resultMapObj);

				}

			} else {
				return null;

			}

			return resultSetFinal;

		}
		
		public static HashMap<String, HashMap<String, String>> fetchMicroServiceDataByKey(String keyColumn,
				String tableName, Set<String> columns, SearchCriteria searchCriteria) {

			java.util.List<HashMap<String, String>> resultSet = BTSLUtil.invokeService(tableName, columns,
					searchCriteria);
			HashMap<String, HashMap<String, String>> resultSetFinal = new HashMap<String, HashMap<String, String>>();

			if (resultSet != null && resultSet.size() > 0) {

				Iterator<HashMap<String, String>> itrResultSet = resultSet.iterator();

				while (itrResultSet.hasNext()) {
					HashMap<String, String> resultMapObj = itrResultSet.next();
					resultSetFinal.put(resultMapObj.get(keyColumn), resultMapObj);

				}

			} else {
				return null;

			}

			return resultSetFinal;

		}
		    
		    
		    public static java.util.List<HashMap<String, String>>  invokeService(String tableName, Set<String> columnsStr, SearchCriteria searchCriteriaMain) {

		    	java.util.List<HashMap<String, String>> resultSet = null;
		    	
		        try {

		          resultSet =  new ArrayList<HashMap<String, String>>();
		          
		          DefaultHttpClient httpClient = new DefaultHttpClient();
		          HttpPost postRequest = new HttpPost(
		              "http://172.30.24.113:9999/common/v1/fetch/records");

		          Iterator<String> itrCol = columnsStr.iterator();
		          String columns = "";
		        		  
		          while(itrCol.hasNext()) {
		        	  columns = columns + itrCol.next()+"," ;
		          }
		         
		          if(columns != null && columns.endsWith(",")) {
		        	  columns = columns.substring(0, columns.lastIndexOf(","));
		          }
		 
		          
		          String SearchCriteriaStr = "";
		          
		          SearchCriteria searchCriteria = searchCriteriaMain;
		          
		          while(true) {
		        	  Operator operator =  searchCriteria.getOperator();
		        	
					switch (operator) {
					case EQUALS:
						ValueType valueType = searchCriteria.getValueType();
						switch (valueType) {
						case STRING:
							SearchCriteriaStr = SearchCriteriaStr + searchCriteria.getColumn()+"='"+searchCriteria.getValue()+"'" ;
							break;
							
						case NUMBER:
							SearchCriteriaStr = SearchCriteriaStr + searchCriteria.getColumn()+"="+searchCriteria.getValue() ;
							break;
						}
						
						
						break;
					case NOT_EQUALS:
						ValueType valueType2 = searchCriteria.getValueType();
						switch (valueType2) {
						case STRING:
							SearchCriteriaStr = SearchCriteriaStr + searchCriteria.getColumn()+"<>'"+searchCriteria.getValue()+"'" ;
							break;
							
						case NUMBER:
							SearchCriteriaStr = SearchCriteriaStr + searchCriteria.getColumn()+"<>"+searchCriteria.getValue() ;
							break;
						}
						
						break;
					case GREATER_THAN:
						SearchCriteriaStr = SearchCriteriaStr + searchCriteria.getColumn()+">"+searchCriteria.getValue() ;
						break;
					case LESS_THAN:
						SearchCriteriaStr = SearchCriteriaStr + searchCriteria.getColumn()+"<"+searchCriteria.getValue() ;
						break;
					case IN:
						Set<String> values = searchCriteria.getValues() ;
						ValueType valueType3 = searchCriteria.getValueType();
						if(values != null && values.size() ==1 && values.iterator().next().equalsIgnoreCase("ALL")) {
							
						}
						else if(values != null && values.size() > 0) {
						Iterator<String> itr = values.iterator();
						String inValues="";
						while(itr.hasNext()) {
							
							switch (valueType3) {
							case STRING:
								inValues = inValues + "'"+itr.next() +"',";
								break;
								
							case NUMBER:
								inValues = inValues + itr.next() +",";
								break;
							}
							
						}
						
						if(inValues != null && inValues.endsWith(",")) {
							inValues = inValues.substring(0, inValues.lastIndexOf(","));
						}
						SearchCriteriaStr = SearchCriteriaStr + searchCriteria.getColumn()+" IN("+inValues+")" ;
						}else {
							SearchCriteriaStr = SearchCriteriaStr  ;//No Search, everything
							return null;
						}
						break;

					}  
		        	  if(searchCriteriaMain.getSearchCriteriaList() == null || searchCriteriaMain.getSearchCriteriaList().size() == 0) {
		        		  SearchCriteriaStr = "("+SearchCriteriaStr+") ";
		        		  break;
		        	  }else {
		        		  searchCriteria = searchCriteriaMain.getSearchCriteria();
		        		  BooleanOperator booleanOperator = searchCriteria.getBooleanOperator() ;
		        		  switch (booleanOperator) {
							case AND:
								SearchCriteriaStr = " "+SearchCriteriaStr+"  AND ";
								break;
								
							case OR:
								SearchCriteriaStr = " "+SearchCriteriaStr+" OR ";
								break;
							}
		        		  
		        		  
		        		  
		        	  }
		          }
		          
		          
		          
		          StringEntity input = new StringEntity("{ \"columns\": \""+columns+"\", \"resumeServiceRequestId\": \"037abfef-767e-4543-beff-9bf7519cb1ea\", \"searchCriteris\": \""+SearchCriteriaStr+"\", \"tableName\": \""+tableName+"\", \"mfsTenantId\": \"mfsPrimaryTenant\", \"language\": \"en\", \"bearerCode\": \"USSD\" }");
		          
		          input.setContentType("application/json");
		          postRequest.setEntity(input);

		          HttpResponse response = httpClient.execute(postRequest);

		        
		          BufferedReader br = new BufferedReader(
		                          new InputStreamReader((response.getEntity().getContent())));

		          String output;
		          System.out.println("Output from Server .... \n");
		          while ((output = br.readLine()) != null) {
		              //System.out.println(output);
		              if(output !=null && output.contains("records")) {
		            	//"records": "[{'profileId':'6','profileName':'Distribution channel','shortName':'DIST'}]",
		            	 /// String responseStr =  "{"+output.replaceAll("\"", "").replaceAll("\'", "\"")+"}";
		            	  
		            	  String   responseStr = output.substring(output.indexOf("records")+10);
		            	   responseStr = responseStr.substring(0, responseStr.indexOf("\""));
		            	   
		            	   responseStr = "{  \"records\":"+responseStr+"}";
		            	   responseStr = responseStr.replaceAll("'", "\"");
		            	   		
		            	  JSONObject jObject = new JSONObject(responseStr);
		                  Iterator<String> keys = jObject.keys();
		                //  System.out.println("jObject "+jObject);
		                  
		                  
		                  
		                  
		                  while( keys.hasNext() ){
		                      String key = (String)keys.next();
		                      
		                      
		                      JSONArray jsonArray = jObject.getJSONArray(key);
		                      for (int i = 0; i < jsonArray.length(); i++) {
		                          JSONObject jSubOject = jsonArray.getJSONObject(i);
		                          Iterator<String> keys2 = jSubOject.keys();
		                          HashMap<String, String> hmap = new HashMap<String, String>();
								while (keys2.hasNext()) {
									String keyName = (String) keys2.next();
									String value = jSubOject.getString(keyName);
									//System.out.println(keyName + " " + value);
									hmap.put(keyName, value);

								}
		                         
		                          resultSet.add(hmap);
		                      
		                      
		                      }
		                   
		                  }

		                  //System.out.println("map : "+map);
		            	  
		            	 
		            	  //"records": "[{'profileName':'deepaVCN','profileId':'777'}]
		              }
		          }

		          httpClient.getConnectionManager().shutdown();

		          
		          
		          
		        }  catch (Exception e) {

		          e.printStackTrace();

		        }

		        return resultSet;
		      }
		    

		    
	public static java.util.List<HashMap<String, String>> invokeJigsawTcpService(TransferProfileWrapperDTO transferProfileWrapper) {

		java.util.List<HashMap<String, String>> resultSet = null;

		try {

			resultSet = new ArrayList<HashMap<String, String>>();

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost("http://172.30.24.113:9999/common/v1/fetch/parent/records");

			
			Gson gson = new Gson(); 
			StringEntity input = new StringEntity(gson.toJson(transferProfileWrapper));

			input.setContentType("application/json");
			postRequest.setEntity(input);

			HttpResponse response = httpClient.execute(postRequest);

			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				if (output != null && output.contains("records")) {
					String responseStr = output.substring(output.indexOf("records") + 10);
					responseStr = responseStr.substring(0, responseStr.indexOf("\""));

					responseStr = "{  \"records\":" + responseStr + "}";
					responseStr = responseStr.replaceAll("'", "\"");

					JSONObject jObject = new JSONObject(responseStr);
					Iterator<String> keys = jObject.keys();
					while (keys.hasNext()) {
						String key = (String) keys.next();

						JSONArray jsonArray = jObject.getJSONArray(key);
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jSubOject = jsonArray.getJSONObject(i);
							Iterator<String> keys2 = jSubOject.keys();
							HashMap<String, String> hmap = new HashMap<String, String>();
							while (keys2.hasNext()) {
								String keyName = (String) keys2.next();
								String value = jSubOject.getString(keyName);
								// System.out.println(keyName + " " + value);
								hmap.put(keyName, value);

							}

							resultSet.add(hmap);

						}

					}
				}
			}

			httpClient.getConnectionManager().shutdown();

		} catch (Exception e) {

			e.printStackTrace();

		}

		return resultSet;
	}

	 /**
	  * Writing Forging value into the Log
	  * @param requestParameter String that will be forged
	  * @return Forged value will be return for logging only
	  */
	 public static String logForgingReqParam(String requestParameter){
		 final String methodName = "logForgingReqParam";
		 final String targetN = "\n";
		 final String targetR = "\r";
		 final String targetT = "\t";
		 final String replacement = "_";
		 String cleanRequestParameter = null;
		 try {
			cleanRequestParameter = ESAPI.encoder().encodeForHTML( requestParameter.replace(targetN, replacement).replace(targetR, replacement).replace(targetT, replacement));
		 } catch (Exception e) {
			LOG.errorTrace(methodName, e);
		 }
		 return cleanRequestParameter;
	}
	 	
	 /**
	  * To validate of request parameter for trusted data
	  * @param requestParameter String that will be forged
	  * @return  true or false based on input value
	  */
	 	public static boolean validateHttpReqParam(String requestParameter){
	 		final String methodName = "validateHttpReqParam";
			Pattern commonValidationPattern = Pattern.compile(SecurityConstants.getProperty("VALIDPATTERN"));
			boolean matchFound = false;
			try {
				Matcher m = commonValidationPattern.matcher(requestParameter);
				matchFound = m.matches();
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			return matchFound;	
		 }
	 	
	 	
	 	/**
	 	 * To convert double value to long
	 	 * 
	 	 * @param number
	 	 * @return
	 	 */
	 	public static long parseDoubleToLong(double number) {
	 		return Double.valueOf(number).longValue();
	 	}
	 	
	 	/**
	 	 * To convert float value to long
	 	 * 
	 	 * @param number
	 	 * @return
	 	 */
	 	public static long parseFloatToLong(float number) {
	 		return Float.valueOf(number).longValue();
	 	}
	 	
	 	
	 	/**
	 	 * To convert double value to int
	 	 * 
	 	 * @param number
	 	 * @return
	 	 */
	 	public static int parseDoubleToInt(double number) {
	 		return Double.valueOf(number).intValue();
	 	}
	 	
	 	/**
	 	 * To convert numeric-string to long
	 	 * 
	 	 * @param number
	 	 * @return
	 	 */
	 	public static long parseStringToLong(String number) {
	 		return Double.valueOf(number).longValue();
	 	}
	 	
	 	
	 	/**
	 	 * To convert int to short
	 	 * 
	 	 * @param number
	 	 * @return
	 	 */
	 	public static short parseIntToShort(int number) {
	 		return Short.valueOf(String.valueOf(number));
	 	}
	 	
	 	/**
	 	 * To convert long to int
	 	 * 
	 	 * @param number
	 	 * @return
	 	 */
	 	public static int parseLongToInt(long number) {
	 		return Integer.parseInt(String.valueOf(number));
	 	}
	 	
	 	/**
	 	 * To convert int to double
	 	 * 
	 	 * @param number
	 	 * @return
	 	 */
	 	public static double parseIntToDouble(int number) {
	 		return Double.parseDouble(String.valueOf(number));
	 	}
	 	
	 	/**
	 	 * To convert long to double
	 	 * 
	 	 * @param number
	 	 * @return
	 	 */
	 	public static double parseLongToDouble(long number) {
	 		return Double.parseDouble(String.valueOf(number));
	 	}
	 	
	 	/**
	 	 * To convert int to byte
	 	 * 
	 	 * @param number
	 	 * @return
	 	 */
	 	public static byte parseIntToByte(int number) {
	 		return Byte.parseByte(String.valueOf(number));
	 	}
	 	
	 	/**
	 	 * To append ",webApiCall:true" in provided String
	 	 * Deletes last character(expecting '}')
	 	 * @param data
	 	 * @return
	 	 */
	 	public static String appendWebApiCall(String data) {
	 		if(data.length() > 1) {
	 			StringBuilder sb = new StringBuilder();
		 		sb.append(data);
				sb.deleteCharAt(data.length()-1);
				sb.append(",\"webApiCall\":\"true\"}");
				return sb.toString();
	 		}else {
	 			return data;
	 		}
	 	}
	 	
		/**
		 * This method will upload .txt file to server
		 */

		/**
		 * @author harshita.bajaj
		 * @param p_formFile
		 * @param p_dirPath
		 * @param p_contentType
		 * @param forward
		 * @param p_fileSize
		 * @param data1
		 * @param content
		 * @return
		 * @throws BTSLBaseException
		 */
		public static boolean uploadFileToServerWithHashMap(HashMap<String, String> p_formFile, String p_dirPath,
				String p_contentType, String forward, long p_fileSize, byte[] data1, String fileType)
				throws BTSLBaseException {
			final String METHOD_NAME = "uploadFileToServerWithHashMap";
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
			}
			FileOutputStream outputStream = null;
			boolean returnValue = false;

			try {
				final File fileDir = new File(p_dirPath);
				if (!fileDir.isDirectory()) {
					fileDir.mkdirs();
				}
				if (!fileDir.exists()) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_UPLOAD_ERROR, 0, null);
				}

				final File fileName = new File(p_dirPath, p_formFile.get(PretupsI.FILE_NAME));

				// if file already exist then show the error message.
				if (p_formFile != null) {
					boolean contentTypeExist = false;
					if (p_contentType.contains(",")) {
						final String temp[] = p_contentType.split(",");
						for (int i = 0, j = temp.length; i < j; i++) {
							if (p_contentType.equalsIgnoreCase(temp[i].trim())) {
								contentTypeExist = true;
								break;
							}
						}
					}
					if (fileType.equalsIgnoreCase(p_contentType)) {
						contentTypeExist = true;
					}

					if (contentTypeExist) {
						if (fileName.exists()) {
							throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_EXISTS, 0,
									null);
						}
						outputStream = new FileOutputStream(fileName);
						outputStream.write(data1);
						returnValue = true;
						if (LOG.isDebugEnabled()) {
							LOG.debug(METHOD_NAME, "File Uploaded Successfully");
						}
					}
					// if file is not a text file show error message
					else {
						throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_UPLOAD_ERROR, 0,
								null);
					}
				}
			} catch (BTSLBaseException be) {
				throw be;
			} catch (Exception e) {
				LOG.error(METHOD_NAME, "Exception " + e.getMessage());
				LOG.errorTrace(METHOD_NAME, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
						EventLevelI.FATAL, "BTSLUtil[uploadFileToServer]", "", "", "", "Exception:" + e.getMessage());
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_UPLOAD_ERROR, 0, null);
			} finally {
				try {
					if (outputStream != null) {
						outputStream.close();
					}
				} catch (IOException e) {
					LOG.error("An error occurred closing outputStream.", e);
				}
				if (LOG.isDebugEnabled()) {
					LOG.debug("uploadFileToServer", "Exit :returnValue=" + returnValue);
				}

			}
			return returnValue;
		}
	 	
		
		public static boolean uploadCsvFileToServerWithHashMapForAssosiateAlert(HashMap<String, String> p_formFile,
				String p_dirPath, String p_contentType, String forward, byte[] data1, long p_fileSize)
				throws BTSLBaseException {
			
			if (LOG.isDebugEnabled()) {
	            LOG.debug("uploadCsvFileToServerWithHashMapForAssosiateAlert",
	                "Entered :p_formFile=" + p_formFile + ", p_dirPath=" + p_dirPath + ", p_contentType=" + p_contentType + ", forward=" + forward + ", p_fileSize=" + p_fileSize);
	        }
	        FileOutputStream outputStream = null;
	        boolean returnValue = false;
	        final String METHOD_NAME = "uploadCsvFileToServerWithHashMapForAssosiateAlert";
	        // modified by Manisha(18/01/08) use singal try catch
	        try {
	            final File fileDir = new File(p_dirPath);
	            if (!fileDir.isDirectory()) {
	                fileDir.mkdirs();
	            }
	            if (!fileDir.exists()) {
	                LOG.debug("uploadFileToServer", "Directory does not exist: " + fileDir + " ");
	                throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "uploadfile.error.dirnotcreated", forward);
	            }

	            final File fileName = new File(p_dirPath, (p_formFile.get(PretupsI.FILE_NAME)+".csv"));
	            if (p_formFile != null) {
//	                if (p_formFile.getFileSize() <= 0) {
//	                    throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "uploadfile.error.filesizezero", forward);
//	                } else if (p_formFile.getFileSize() > p_fileSize) {
//	                    throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "uploadfile.error.largefilesize", 0, new String[] { String.valueOf(p_fileSize) }, forward);
//	                }

	                boolean contentTypeExist = false;
	                if (p_contentType.contains(",")) {
	                    final String temp[] = p_contentType.split(",");
	                    for (int i = 0, j = temp.length; i < j; i++) {
	                        if (p_formFile.get(PretupsI.FILE_TYPE).equalsIgnoreCase(temp[i].trim())) {
	                    	//commented by anand for testing purpose
	                    	//if (("text/csv").equalsIgnoreCase(temp[i].trim())) {
	                            contentTypeExist = true;
	                            break;
	                        }
	                    }
	                } else if (p_formFile.get(PretupsI.FILE_TYPE).equalsIgnoreCase(p_contentType)) {
	                    contentTypeExist = true;
	                }

	                if (contentTypeExist) {
	                    if (fileName.exists()) {
	                        throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "uploadfile.error.fileexists", forward);
	                    }
	                    outputStream = new FileOutputStream(fileName);
	                    //outputStream.write(p_formFile.getFileData());
	                    outputStream.write(data1);
	                    returnValue = true;
	                    if (LOG.isDebugEnabled()) {
	                        LOG.debug("uploadFileToServer", "File Uploaded Successfully");
	                    }
	                }
	                // if file is not a text file show error message
	                else {
	                    if (LOG.isDebugEnabled()) {
	                        LOG.debug(
	                            "uploadFileToServer",
	                            "Invalid content type: " + p_formFile.get(PretupsI.FILE_TYPE) + " required is p_contentType: " + p_contentType + " p_formFile.getFileName(): " + p_formFile
	                                .get(PretupsI.FILE_NAME));
	                    }
	                    throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "uploadfile.error.notrequiredcontent", forward);
	                }
	            }
	            // if there is no such file then show the error message
	            else {
	                throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "uploadfile.error.nofile", forward);
	            }
	            
	            
	        }
	        
	        catch (BTSLBaseException be) {
	            throw be;
	        } catch (Exception e) {
	            LOG.error("uploadFileToServer", "Exception " + e.getMessage());
	            LOG.errorTrace(METHOD_NAME, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[uploadFileToServer]", "", "", "",
	                "Exception:" + e.getMessage());
	            throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "error.general.processing", forward);
	        } finally {
	        	try{
	                if (outputStream!= null){
	                	outputStream.close();
	                }
	              }
	              catch (IOException e){
	            	  LOG.error("An error occurred closing outputStream.", e);
	              }
	            if (LOG.isDebugEnabled()) {
	                LOG.debug("uploadFileToServer", "Exit :returnValue=" + returnValue);
	            }

	        }
	        
			
			return returnValue;
		}
		public static boolean uploadCsvFileToServerWithHashMapForXLS(HashMap<String, String> p_formFile,
				String p_dirPath, String p_contentType, String forward, byte[] data1, long p_fileSize)
				throws BTSLBaseException {
			
			if (LOG.isDebugEnabled()) {
	            LOG.debug("uploadCsvFileToServerWithHashMapForXLS",
	                "Entered :p_formFile=" + p_formFile + ", p_dirPath=" + p_dirPath + ", p_contentType=" + p_contentType + ", forward=" + forward + ", p_fileSize=" + p_fileSize);
	        }
	        FileOutputStream outputStream = null;
	        boolean returnValue = false;
	        final String METHOD_NAME = "uploadCsvFileToServerWithHashMapForXLS";
	        try {
	            final File fileDir = new File(p_dirPath);
	            if (!fileDir.isDirectory()) {
	                fileDir.mkdirs();
	            }
	            if (!fileDir.exists()) {
	                LOG.debug("uploadFileToServer", "Directory does not exist: " + fileDir + " ");
	                throw new BTSLBaseException(classname, METHOD_NAME, "directory not created", 0, null);
	            }

	            final File fileName = new File(p_dirPath, (p_formFile.get(PretupsI.FILE_NAME)+".xls"));
	            if (p_formFile != null) {

	                boolean contentTypeExist = false;
	                if (p_contentType.contains(",")) {
	                    final String temp[] = p_contentType.split(",");
	                    for (int i = 0, j = temp.length; i < j; i++) {
	                        if (p_contentType.equalsIgnoreCase(temp[i].trim())) {
	                            contentTypeExist = true;
	                            break;
	                        }
	                    }
	                } else if (p_formFile.get(PretupsI.FILE_TYPE).equalsIgnoreCase(p_contentType)) {
	                    contentTypeExist = true;
	                }

	                if (contentTypeExist) {
	                    if (fileName.exists()) {
	                    	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_EXISTS, 0,
									null);
	                    }
	                    outputStream = new FileOutputStream(fileName);
	                    outputStream.write(data1);
	                    returnValue = true;
	                    if (LOG.isDebugEnabled()) {
	                        LOG.debug("uploadFileToServer", "File Uploaded Successfully");
	                    }
	                }
	                // if file is not a text file show error message
	                else {
	                    if (LOG.isDebugEnabled()) {
	                        LOG.debug(
	                            "uploadFileToServer",
	                            "Invalid content type: " + p_formFile.get(PretupsI.FILE_TYPE) + " required is p_contentType: " + p_contentType + " p_formFile.getFileName(): " + p_formFile
	                                .get(PretupsI.FILE_NAME));
	                    }
	                    throw new BTSLBaseException(classname, METHOD_NAME,"content not found", 0, null);
	                }
	            }
	            // if there is no such file then show the error message
	            else {
	            	throw new BTSLBaseException(classname, METHOD_NAME,"no file found", 0, null);
	            }
	            
	            
	        }
	        
	        catch (BTSLBaseException be) {
	            throw be;
	        } catch (Exception e) {
	            LOG.error("uploadFileToServer", "Exception " + e.getMessage());
	            LOG.errorTrace(METHOD_NAME, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[uploadFileToServer]", "", "", "",
	                "Exception:" + e.getMessage());
	            throw new BTSLBaseException(classname, METHOD_NAME, "file processing error", 0, null);
	        } finally {
	        	try{
	                if (outputStream!= null){
	                	outputStream.close();
	                }
	              }
	              catch (IOException e){
	            	  LOG.error("An error occurred closing outputStream.", e);
	              }
	            if (LOG.isDebugEnabled()) {
	                LOG.debug("uploadFileToServer", "Exit :returnValue=" + returnValue);
	            }

	        }
	        
			
			return returnValue;
		}
		
		public static boolean isContains_UnderScore(String p_string) {
	        final String METHOD_NAME = "isContains_UnderScore";
		        if (LOG.isDebugEnabled()) {
		            LOG.debug("isContains_UnderScore", "p_string= " + p_string);
		        }
	            String specialCharacters = "_";
	            return (p_string != null && p_string.contains(specialCharacters));
	        
	    }
	
		/**
	     * This method adds the no of days in the passed date
	     * 
	     * @param p_date
	     * @param p_no_ofDays
	     * @return Date
	     */
	    public static Date addHoursInUtilDate(java.util.Date p_date, int p_no_ofHours) {
	        final Calendar cal = BTSLDateUtil.getInstance();
	        cal.setTime(p_date);
	        cal.add(Calendar.HOUR, p_no_ofHours);
	        return cal.getTime();
	    }
	 	
		
	    /**
	     * 
	     * @param headers
	     * @param requestJson
	     */
	    public static void modifyHeaders(HttpHeaders headers, String requestJson) {
	    	
	    	/*String KEY = Constants.getProperty("SECRET_KEY_INTERNAL_API_CALL");
	    	
	    	if(requestJson != null) { 
	    	final HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, KEY);
	    	String encryptedPayload = hmacUtils.hmacHex(requestJson);
	        headers.set("Signature", encryptedPayload);
	    	}else {
	    		headers.set("Signature", Constants.getProperty("SECRET_SIGNATURE_INTERNAL_API_CALL"));
	    	}*/
	        
	        headers.set("Nonce", Constants.getProperty("SECRET_NONCE_INTERNAL_API_CALL"));
	        
	    }
		
		/**
		  * Encrypts the passed text string using an encryption key for 3DES 
		  * @param p_text
		  * @return String
		  */
		 public	static String encrypt3DESText(String p_text)
		 {
			 final String methodName="encrypt3DESText";
			 try
			 {
					 return new CryptoUtil().encrypt(p_text,Constants.KEY);

			 }
			 catch(Exception e)
			 {
				 LOG.error(methodName,"Exception e="+e.getMessage());
				 LOG.errorTrace(methodName, e);
				 return null;
			 }
		 }
	    
	 /**
     * To get the difference between two util dates in no of days based on date only
     * 
     * @param date1
     * @param date2
     * @return int
     * @throws ParseException 
     */
    @SuppressWarnings("deprecation")
	public static int getDifferenceInUtilDates2(java.util.Date date1, java.util.Date date2) throws ParseException {
    	final String METHOD_NAME = "getDifferenceInUtilDates2";
    	long dt1 = 0 ;
        long dt2 = 0 ;
        java.sql.Date sqlDate1 = null;
        java.sql.Date sqlDate2 = null;
		
        if (LOG.isDebugEnabled()) {
         LOG.debug(METHOD_NAME, "date1=" + date1+", date2="+date2);
     }
    	if (date1 != null) {
			java.util.Date utilDateNew1=(Date)date1.clone();
			try {						
				if (LOG.isDebugEnabled()) {
                    LOG.debug(METHOD_NAME, "utilDateNew1=" + utilDateNew1);
                }
				sqlDate1 = new java.sql.Date(utilDateNew1.getTime());
				if (LOG.isDebugEnabled()) {
                    LOG.debug(METHOD_NAME, "sqlDate1=" + sqlDate1);
                }
				dt1 = sqlDate1.getTime();
				
			} catch (Exception e) {
				 LOG.errorTrace(METHOD_NAME, e);
				sqlDate1 = new java.sql.Date(utilDateNew1.getTime());
				dt1 = sqlDate1.getTime();
				if (LOG.isDebugEnabled()) {
	               LOG.debug(METHOD_NAME, "Exception utilDateNew1=" + utilDateNew1);
	            }
				if (LOG.isDebugEnabled()) {
                    LOG.debug(METHOD_NAME, "Exception sqlDate1=" + sqlDate1);
                }
			}
	      }
    	if (date2 != null) {
			java.util.Date utilDateNew2=(Date)date2.clone();
			try {
				if (LOG.isDebugEnabled()) {
                    LOG.debug(METHOD_NAME, "utilDateNew2=" + utilDateNew2);
                }
				sqlDate2 = getSQLDateFromUtilDate(utilDateNew2);						
				if (LOG.isDebugEnabled()) {
                    LOG.debug(METHOD_NAME, "sqlDate2=" + sqlDate2);
                }
				dt2 = sqlDate2.getTime();						
			} catch (Exception e) {
				 LOG.errorTrace(METHOD_NAME, e);
				sqlDate2 = new java.sql.Date(utilDateNew2.getTime());
				dt2 = sqlDate2.getTime();
				if (LOG.isDebugEnabled()) {
	              LOG.debug(METHOD_NAME, "Exception utilDateNew2=" + utilDateNew2);
	            }
				if (LOG.isDebugEnabled()) {
                    LOG.debug(METHOD_NAME, "Exception sqlDate2=" + sqlDate2);
                }
			}
	      }
    	if (LOG.isDebugEnabled()) {
         LOG.debug(METHOD_NAME, "dt2=" + dt2+", dt1=" + dt1+", noOfDays="+(int) ((dt2 - dt1) / new Long((1000 * 60 * 60 * 24))));
    	}
        //int noOfDays = (int) ((dt2 - dt1) / new Long((1000 * 60 * 60 * 24)));
        Date sdf1 = null;
    	Date sdf2 = null;
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(true); 
        sdf1 =  sdf.parse(sdf.format(sqlDate1));
        sdf2 =  sdf.parse(sdf.format(sqlDate2));
    	if (LOG.isDebugEnabled()) {
         LOG.debug(METHOD_NAME, "Before timeset  sdf1=" + sdf1+", sdf2=" + sdf2);
     }
    	sdf1.setHours(0);
    	sdf1.setMinutes(0);
    	sdf1.setSeconds(0);
    	dt1 = sdf1.getTime();
    	
    	sdf2.setHours(0);
    	sdf2.setMinutes(0);
    	sdf2.setSeconds(0);
    	dt2 = sdf2.getTime();
    	if (LOG.isDebugEnabled()) {
         LOG.debug(METHOD_NAME, "After timeset  sdf1=" + sdf1+", sdf2=" + sdf2);
     }
    	if (LOG.isDebugEnabled()) {
         LOG.debug(METHOD_NAME, "dt2=" + dt2+", dt1=" + dt1+", noOfDays="+(int) ((dt2 - dt1) / new Long((1000 * 60 * 60 * 24))));
     }
    	int noOfDays = (int) ((dt2 - dt1) / new Long((1000 * 60 * 60 * 24)));
    	
        if (LOG.isDebugEnabled()) {
         LOG.debug(METHOD_NAME, "noOfDays=" + noOfDays);
     }
        return noOfDays;
    }

    public static String maskParam(String param) {
    	return maskParam(param, PretupsI.MASK_TYPE_ENC);
    }
    public static String maskParam(String param,String maskType) {
    	
    	String maskOrMac =  Constants.getProperty("MASK_PARAM_LOGIC");
    	if(maskType != null) {
    		maskOrMac =  maskType;
    	}
    	
    	
    	if(maskOrMac != null && param != null) {
    		
    		if(maskOrMac.equalsIgnoreCase(PretupsI.MASK_TYPE_MASK)) {
    			return "*********";
    		}else if(maskOrMac.equalsIgnoreCase(PretupsI.MASK_TYPE_HMAC)) {
    			String KEY = Constants.getProperty("SECRET_KEY_INTERNAL_PARAM");
    	    	
    	    	final HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, KEY);
    	    	return hmacUtils.hmacHex(param);
    			
    		}else if(maskOrMac.equalsIgnoreCase(PretupsI.MASK_TYPE_ENC)) {
    			return AESEncryptionUtil.aesEncryptor(param, Constants.A_KEY);
    			
    		}else {
    			return param;
    		}
    	}else {
    	
    		return param;
    	}
    	
    	
    }
    
   public static boolean validateIsalphanumeric(String data) {
	   String regex = "^[a-zA-Z0-9]+$";
	   boolean result=false;
	   if(data!=null)  result = data.matches(regex);
	   return result;
	   
   }
   
   
   public static boolean isNumericDouble(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    try {
	        double d = Double.parseDouble(strNum);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}

    public static boolean uploadFileToServer(HashMap<String, String> p_formFile, String p_dirPath, String p_contentType, long p_fileSize) throws BTSLBaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("uploadFileToServer",
                    "Entered :p_formFile=" + p_formFile + ", p_dirPath=" + p_dirPath + ", p_contentType=" + p_contentType + ", p_fileSize=" + p_fileSize);
        }
        FileOutputStream outputStream = null;
        boolean returnValue = false;
        final String METHOD_NAME = "uploadFileToServer";
        try {
            final File fileDir = new File(p_dirPath);
            if (!fileDir.isDirectory()) {
                fileDir.mkdirs();
            }
            if (!fileDir.exists()) {
                LOG.debug("uploadFileToServer", "Directory does not exist: " + fileDir + " ");
                throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "uploadfile.error.dirnotcreated");
            }
            final File fileName = new File(p_dirPath, (p_formFile.get(PretupsI.FILE_NAME)+".xls"));

            // if file already exist then show the error message.
            if (p_formFile != null) {
                ReadGenericFileUtil fileUtil = new ReadGenericFileUtil();

                final byte[] data =fileUtil.decodeFile(p_formFile.get(PretupsI.FILE_ATTACHMENT));
                if (data.length <= 0) {
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.THE_FILE_DOES_NOT_EXISTS_OR_THE_FILE_HAS_NO_DATA_PLEASE_USE_PROPER_FILE_WITH_VALID_DATA);
                } else if(data.length > p_fileSize){
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_SIZE_LARGE,
                            PretupsI.RESPONSE_FAIL,null);
                }

                boolean contentTypeExist = false;
                if (p_contentType.contains(",")) {
                    final String temp[] = p_contentType.split(",");
                    for (int i = 0, j = temp.length; i < j; i++) {
                        if (getContentType(p_formFile.get(PretupsI.FILE_ATTACHMENT)).equalsIgnoreCase(temp[i].trim())) {
                            contentTypeExist = true;
                            break;
                        }
                    }
                } else if (getContentType(p_formFile.get(PretupsI.FILE_ATTACHMENT)).equalsIgnoreCase(p_contentType)) {
                    contentTypeExist = true;
                }

                if (contentTypeExist) {
                    if (fileName.exists()) {
                        throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_ALREADY_EXISTS);
                    }
                    outputStream = new FileOutputStream(fileName);
                    outputStream.write(data);
                    returnValue = true;
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("uploadFileToServer", "File Uploaded Successfully");
                    }
                }
                // if file is not a text file show error message
                else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                                METHOD_NAME,
                                "Invalid content type: " + p_formFile.get(PretupsI.FILE_TYPE) + " required is p_contentType: " + p_contentType + " p_formFile.getFileName(): " + p_formFile
                                        .get(PretupsI.FILE_NAME));
                    }
                    throw new BTSLBaseException(classname, METHOD_NAME,"content not found", 0, null);
                }
            }
            // if there is no such file then show the error message
            else {
                throw new BTSLBaseException(classname, METHOD_NAME,"no file found", 0, null);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            LOG.error("uploadFileToServer", "Exception " + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[uploadFileToServer]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "error.general.processing");
        } finally {
            try{
                if (outputStream!= null){
                    outputStream.close();
                }
            }
            catch (IOException e){
                LOG.error("An error occurred closing outputStream.", e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("uploadFileToServer", "Exit :returnValue=" + returnValue);
            }

        }
        return returnValue;
    }

    private static String getContentType(String base64EncodedString) {
        // filetype magic number(hex)
        // "xlsx" "504B030414000600",
        // "xls" "D0CF11E0A1B11AE1"
        byte[] data = Base64.getDecoder().decode(base64EncodedString);
        final byte[] xlsxPattern = new byte[] { 0x50, 0x4B, 0x03, 0x04, 0x14, 0x00, 0x06, 0x00 };
        final byte[] xlsPattern = new byte[] { (byte) 0xD0, (byte) 0xCF, 0x11, (byte) 0xE0, (byte) 0xA1, (byte) 0xB1,
                0x1A, (byte) 0xE1 };

        final String contentTypeXlsx = "application/xlsx";
        final String contentTypeXls = "application/xls";
        final String contentTypeUnknown = "application/octet-stream";

        Map<String, byte[]> dict = new HashMap<String, byte[]>();
        dict.put(contentTypeXlsx, xlsxPattern);
        dict.put(contentTypeXls, xlsPattern);

        for (Map.Entry<String, byte[]> entry : dict.entrySet()) {
            String mime = entry.getKey();
            byte[] pattern = entry.getValue();
            if (pattern.length <= data.length) {
                int idx = 0;
                for (idx = 0; idx < pattern.length; ++idx) {
                    if (pattern[idx] != data[idx])
                        break;
                }
                boolean isMatch = Integer.compare(idx, pattern.length) == 0;
                if (isMatch)
                    return mime;

            }
        }
        return contentTypeUnknown;
    }

    public static boolean uploadFileToServer(File formFile,byte []data, String dirPath, String contentType,  long size) throws BTSLBaseException {
        final String METHOD_NAME = "uploadFileToServer";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME,
                    "Entered :formFile=" + formFile + ", dirPath=" + dirPath + ", contentType=" + contentType  + ", size=" + size);
        }
        FileOutputStream outputStream = null;
        boolean returnValue = false;
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        try {
            final File fileDir = new File(dirPath);
            if (!fileDir.isDirectory()) {
                fileDir.mkdirs();
            }
            if (!fileDir.exists()) {
                LOG.debug(METHOD_NAME, "Directory does not exist: " + fileDir + " ");
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DIR_NOT_CREATED, "");
            }

            final File fileName = new File(dirPath, formFile.getName());
            Path path = Paths.get( formFile.getName());
            long fileSize = Files.size(path);
            File file = new File(fileDir+ PretupsI.FORWARD_SLASH + formFile.getName());
            String    mimeType = mimeTypesMap.getContentType(file);
            // if file already exist then show the error message.
            if (formFile != null) {
                if (fileSize <= 0) {
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_SIZE_EMPTY);
                } else if (fileSize > size) {
                    throw new BTSLBaseException(classname, METHOD_NAME,PretupsErrorCodesI.FILE_SIZE_LARGE, 0, new String[] { String.valueOf(size) }, "");
                }

                boolean contentTypeExist = false;
                if (contentType.contains(PretupsI.COMMA)) {
                    final String temp[] = contentType.split(PretupsI.COMMA);
                    for (int i = 0, j = temp.length; i < j; i++) {
                        if (mimeType.equalsIgnoreCase(temp[i].trim())) {
                            contentTypeExist = true;
                            break;
                        }
                    }
                } else if (mimeType.equalsIgnoreCase(contentType)) {
                    contentTypeExist = true;
                }

                if (contentTypeExist) {
                    if (fileName.exists()) {
                        throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_ALREADY_EXISTS);
                    }
                    outputStream = new FileOutputStream(fileName);
                    outputStream.write(data);
                    returnValue = true;
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(METHOD_NAME, "File Uploaded Successfully");
                    }
                }
                // if file is not a text file show error message
                else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                                METHOD_NAME,
                                "Invalid content type: " + mimeType + " required is contentType: " + contentType + " formFile.getFileName(): " + formFile
                                        .getName());
                    }
                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.UPLOAD_NOT_TXT_FILE, "");
                }
            }
            // if there is no such file then show the error message
            else {
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_FILE_EXIST);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            LOG.error(METHOD_NAME, "Exception " + e.getMessage());
            LOG.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[uploadFileToServer]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
        } finally {
            try{
                if (outputStream!= null){
                    outputStream.close();
                }
            }
            catch (IOException e){
                LOG.error("An error occurred closing outputStream.", e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exit :returnValue=" + returnValue);
            }
        }
        return returnValue;
    }

}

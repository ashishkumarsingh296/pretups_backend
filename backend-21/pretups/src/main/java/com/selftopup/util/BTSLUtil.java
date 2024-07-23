package com.selftopup.util;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.sql.Timestamp;
import java.text.Collator;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import jakarta.servlet.http.HttpServletRequest;

import org.spring.custom.action.Globals;
import org.spring.custom.action.action.ActionErrors;
import org.spring.custom.action.action.ActionMessage;
import org.apache.struts.upload.FormFile;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.BTSLMessages;
import com.selftopup.common.CertificateLoader;
import com.selftopup.common.ListValueVO;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.master.businesslogic.LocaleMasterCache;
import com.selftopup.pretups.master.businesslogic.LocaleMasterVO;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixCache;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixVO;
import com.selftopup.pretups.preference.businesslogic.PreferenceCache;
import com.selftopup.pretups.preference.businesslogic.PreferenceI;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.processes.businesslogic.ProcessI;
import com.selftopup.pretups.user.businesslogic.ChannelUserVO;

public class BTSLUtil {

    private static Log _log = LogFactory.getLog(BTSLUtil.class.getName());
    public final static int PERIOD_DAY = 1;
    public final static int PERIOD_WEEK = 2;
    public final static int PERIOD_MONTH = 3;
    public final static int PERIOD_YEAR = 4;
    /**
     * boolean specifying by default whether or not it is okay for a String to
     * be empty
     */
    public static final boolean defaultEmptyOK = true;
    public static final String whitespace = " \t\n\r";

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
        if (str == null || str.trim().length() == 0)
            return true;
        else
            return false;
    }

    /**
     * Get Financial Year
     * 
     * @return String
     */
    public static String getFinancialYear() {
        java.util.Calendar getRight = GregorianCalendar.getInstance();
        int month = getRight.get(Calendar.MONTH);
        int year = getRight.get(Calendar.YEAR);
        int finanYearStart = SystemPreferences.FINANCIAL_YEAR_START_INDEX;
        if (finanYearStart != 0 && (month >= 0) && (month < finanYearStart))
            return String.valueOf(year - 1);
        else
            return String.valueOf(year);
    }

    /**
     * Get java.util.Date from java.sql.Timestamp
     * 
     * @param date
     * @return
     */
    public static java.util.Date getUtilDateFromTimestamp(Timestamp timestamp) throws Exception {
        if (timestamp == null)
            return null;
        return new java.util.Date(timestamp.getTime());
    }

    /**
     * Get java.sql.Timestamp from java.util.Date
     * 
     * @param date
     * @return
     */
    public static Timestamp getTimestampFromUtilDate(java.util.Date date) {
        if (date == null)
            return null;
        return new java.sql.Timestamp(date.getTime());
    }

    public static boolean compareLocaleString(String p_str, String p_str2) {

        boolean flag = false;
        Collator usCollator = Collator.getInstance();
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

        if (_log.isDebugEnabled()) {
            _log.debug("formatMessage", "Entered Action " + p_action + " p_key" + p_key + " message " + p_message);
        }

        StringBuffer messageFormat = new StringBuffer(Constants.getProperty("cachemessageformat"));
        messageFormat.replace(messageFormat.indexOf("{"), messageFormat.indexOf("}") + 1, p_action);
        messageFormat.replace(messageFormat.indexOf("{"), messageFormat.indexOf("}") + 1, p_key);
        messageFormat.replace(messageFormat.indexOf("{"), messageFormat.indexOf("}") + 1, p_message);

        if (_log.isDebugEnabled()) {
            _log.debug("formatMessage", "Exited " + messageFormat);
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
    public static ListValueVO getOptionDesc(String p_code, ArrayList p_list) {

        if (_log.isDebugEnabled())
            _log.debug("getOptionDesc", "Entered: p_code=" + p_code + " p_list=" + p_list);
        ListValueVO vo = null;
        boolean flag = false;
        if (p_list != null && p_list.size() > 0) {
            for (int i = 0, j = p_list.size(); i < j; i++) {
                vo = (ListValueVO) p_list.get(i);
                if (vo.getValue().equalsIgnoreCase(p_code)) {
                    flag = true;
                    break;
                }
            }
        }
        if (!flag)
            vo = new ListValueVO();
        if (_log.isDebugEnabled())
            _log.debug("getOptionDesc", "Exited: vo=" + vo);
        return vo;
    }

    /**
     * Null To String
     * 
     * @param p_str
     * @return
     */
    public static String NullToString(String p_str) {
        if (p_str == null)
            return "";
        else
            return p_str;
    }

    /**
     * Decrypts the passed text string using an encryption key
     * 
     * @param p_text
     * @return
     */
    public static String decryptText(String p_text) {
        try {
            if ("SHA".equals(SystemPreferences.PINPAS_EN_DE_CRYPTION_TYPE))
                return p_text;
            else if ("AES".equals(SystemPreferences.PINPAS_EN_DE_CRYPTION_TYPE))
                return new AESEncryptionUtil().DecryptAES(p_text);
            else if ("DES".equals(SystemPreferences.PINPAS_EN_DE_CRYPTION_TYPE))
                return new CryptoUtil().decrypt(p_text, Constants.KEY);
            else
                return null;
        } catch (Exception e) {
            _log.error("decryptText", "Exception e=" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }// end method

    /**
     * Encrypts the passed text string using an encryption key
     * 
     * @param password
     * @return String
     */
    public static String encryptText(String p_text) {
        try {
            if ("SHA".equals(SystemPreferences.PINPAS_EN_DE_CRYPTION_TYPE))
                return OneWayHashingAlgoUtil.getInstance().encrypt(p_text);
            else if ("AES".equals(SystemPreferences.PINPAS_EN_DE_CRYPTION_TYPE))
                return new AESEncryptionUtil().EncryptAES(p_text);
            else if ("DES".equals(SystemPreferences.PINPAS_EN_DE_CRYPTION_TYPE))
                return new CryptoUtil().encrypt(p_text, Constants.KEY);
            else
                return null;
        } catch (Exception e) {
            _log.error("encryptText", "Exception e=" + e.getMessage());
            e.printStackTrace();
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
        java.sql.Date sqlDate = null;
        if (utilDate != null)
            sqlDate = new java.sql.Date(utilDate.getTime());
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
        if (sqlDate != null)
            tempUtilDate = new java.util.Date(sqlDate.getTime());
        return tempUtilDate;
    }// end of sqlDateToUtilDate

    /**
     * converts java.sql.date to a string in indian format
     * 
     * @param Date
     *            to be converted
     * @return String in Indian Format
     */
    public static boolean isStringIn(String string, String inString) {
        if (_log.isDebugEnabled())
            _log.debug("isStringIn", "Entered string=" + string + "  inString=" + inString);
        try {
            ArrayList usageStringList = null;
            if (inString != null) {
                usageStringList = new ArrayList();
                StringTokenizer strToken = new StringTokenizer(inString, ",");
                while (strToken.hasMoreTokens()) {
                    usageStringList.add(strToken.nextElement());
                }
            } else
                return false;
            for (int i = 0; i < usageStringList.size(); i++) {
                if (((String) usageStringList.get(i)).equals(string)) {
                    return true;
                }
            }
        } catch (Exception e) {
            _log.error("isStringIn", "Exception e:" + e);
            e.printStackTrace();
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
        if (_log.isDebugEnabled())
            _log.debug("isStringContain", "Entered commaStr=" + commaStr + "  string=" + string);
        try {
            ArrayList usageStringList = null;
            if (commaStr != null) {
                usageStringList = new ArrayList();
                StringTokenizer strToken = new StringTokenizer(commaStr, ",");
                while (strToken.hasMoreTokens()) {
                    usageStringList.add(strToken.nextElement());
                }
            } else
                return false;
            String tempStr;
            for (int i = 0; i < usageStringList.size(); i++) {
                tempStr = (String) usageStringList.get(i);
                if (_log.isDebugEnabled())
                    _log.debug("isStringContain", "comparing allowed=" + tempStr + "  with=" + string);
                if (string.indexOf(tempStr.trim()) != -1) {
                    return true;
                }
            }
        } catch (Exception e) {
            _log.error("isStringContain", "Exception e=" + e);
            e.printStackTrace();
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
            if (_log.isDebugEnabled())
                _log.debug("isDayTimeValid", "Entered allowedDays=" + allowedDays + "   fromTime=" + fromTime + "   toTime=" + toTime);
            Calendar cal = GregorianCalendar.getInstance();
            // System.out.println("isDayTimeValid .....1");

            String todayDay = "" + cal.get(Calendar.DAY_OF_WEEK);

            // 1 BugNo 880 Code fix start
            if (isNullString(fromTime) && isNullString(toTime)) {
                if (isNullString(allowedDays))
                    return true;
                if (allowedDays.indexOf(todayDay) == -1)
                    return false;
                else
                    return true;
            }

            if (isNullString(fromTime) || isNullString(toTime))
                return true;

            // 1 BugNo 880 Code fix end

            // System.out.println("isDayTimeValid .....3");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
            sdf.setLenient(false);
            java.util.Date currentDate = new java.util.Date();
            String dateString = getDateStringFromDate(currentDate);
            // System.out.println("isDayTimeValid .....4 dateString="+dateString);
            // System.out.println("isDayTimeValid .....4 dateString full="+dateString+" "+fromTime);
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

            // System.out.println("currentDate="+currentDate);
            // System.out.println("fromdate="+fromdate);
            // System.out.println("todate="+todate);
            if (todate.before(fromdate) && currentDate.after(fromdate) && currentDate.after(todate)) {
                Calendar tempcal = GregorianCalendar.getInstance();
                tempcal.setTime(todate);
                tempcal.add(Calendar.DATE, 1);
                todate = tempcal.getTime();
            }
            if (todate.before(fromdate) && currentDate.before(fromdate) && currentDate.before(todate)) {
                Calendar tempcal = GregorianCalendar.getInstance();
                tempcal.setTime(fromdate);
                tempcal.add(Calendar.DATE, -1);
                todayDay = String.valueOf(Integer.parseInt(todayDay) - 1);
                fromdate = tempcal.getTime();
            }
            if (!isNullString(allowedDays) && allowedDays.indexOf(todayDay) == -1)
                return false;
            // System.out.println("currentDate="+currentDate);
            // System.out.println("fromdate="+fromdate);
            // System.out.println("todate="+todate);
            if (currentDate.after(fromdate) && currentDate.before(todate)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            _log.error("isDayTimeValid", "Exception " + e);
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
        try {
            ArrayList fromTimeStringList = null;
            ArrayList toTimeStringList = null;
            if (fromTimeString != null) {
                fromTimeStringList = new ArrayList();
                StringTokenizer strToken = new StringTokenizer(fromTimeString, delimiter);
                while (strToken.hasMoreTokens()) {
                    fromTimeStringList.add(((String) strToken.nextElement()).trim());
                }
            } else
                return false;
            if (toTimeString != null) {
                toTimeStringList = new ArrayList();
                StringTokenizer strToken = new StringTokenizer(toTimeString, delimiter);
                while (strToken.hasMoreTokens()) {
                    toTimeStringList.add(((String) strToken.nextElement()).trim());
                }
            } else
                return false;
            int size = 0;
            if (toTimeStringList.size() < fromTimeStringList.size())
                size = toTimeStringList.size();
            else
                size = fromTimeStringList.size();
            int fromHour = 0;
            int toHour = 0;
            for (int i = 0; i < size; i++) {
                fromHour = Integer.parseInt((String) fromTimeStringList.get(i));
                toHour = Integer.parseInt((String) toTimeStringList.get(i));
                if (i != 0)
                    message.append(" or ");
                message.append(fromHour + "-" + toHour);
                if (fromHour > toHour) {
                    if (currentHour <= fromHour && currentHour < toHour)
                        return true;
                    else if (currentHour >= fromHour && currentHour > toHour)
                        return true;

                } else {
                    if (currentHour >= fromHour && currentHour < toHour)
                        return true;
                }
            }
            return false;
        } catch (Exception e) {
            _log.error("isHourBetweenStrings", " Exception e=" + e);
            e.printStackTrace();
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
        String dot = new String(".");
        String tempDot = "";
        str = str.trim();
        int strLength = str.length();

        for (int i = 0; i < strLength; i++) {
            tempDot = str.substring(i, i + 1);
            if (dot.equals(tempDot)) {
                if (found)
                    return false;
                else
                    found = true;

            } else if (!Character.isDigit(str.charAt(i)))
                return false;
        }
        return true;
    }

    /**
     * Validates MSISDN for numeric and MIN and MAX length
     * 
     * @param mobileno
     * @return
     */
    public static boolean isValidMSISDN(String p_msisdn) {
        int strLength = p_msisdn.length();
        if (_log.isDebugEnabled())
            _log.debug("isValidMobileNo", "strLength= " + strLength + " SystemPreferences.MIN_MSISDN_LENGTH=" + SystemPreferences.MIN_MSISDN_LENGTH + "SystemPreferences.MAX_MSISDN_LENGTH=" + SystemPreferences.MAX_MSISDN_LENGTH + "*************");

        if ((strLength < SystemPreferences.MIN_MSISDN_LENGTH || strLength > SystemPreferences.MAX_MSISDN_LENGTH))
            return false;
        for (int i = 0; i < strLength; i++) {
            if (!((Character.isDigit(p_msisdn.charAt(i))) || (p_msisdn.charAt(i) == '+')))
                return false;
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
        if (_log.isDebugEnabled())
            _log.debug("padZeroesToLeft()", "Entered with p_strValue= " + p_strValue + " p_strLength:" + p_strLength);
        int cntr = p_strLength - p_strValue.length();
        if (cntr > 0) {
            for (int i = 0; i < cntr; i++) {
                p_strValue = "0" + p_strValue;
            }
        }
        if (_log.isDebugEnabled())
            _log.debug("padZeroesToLeft()", "Exiting with p_strValue= " + p_strValue);
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
            char arr[] = str.toCharArray();

            for (int i = 0; i < arr.length; i++) {

                if (!(arr[i] >= '0' && arr[i] <= '9')) {
                    flag = false;
                    break;
                }
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
        try {
            Double.parseDouble(str);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
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
        HashMap ht = new HashMap();
        StringTokenizer stToken1 = null;
        StringTokenizer stToken2 = null;
        String newString = "";
        ArrayList list = new ArrayList();
        stToken1 = new StringTokenizer(str, token1);
        while (stToken1.hasMoreTokens()) {
            newString = stToken1.nextToken();
            if (newString.indexOf(token2) < 0)
                continue;
            stToken2 = new StringTokenizer(newString, token2);
            if (stToken2.countTokens() < 2)
                continue;
            while (stToken2.hasMoreTokens()) {
                list.add((String) stToken2.nextToken());
            }
        }
        for (int i = 0; i < list.size(); i++) {
            if (i % 2 == 1) {
                ht.put((String) list.get(i - 1), (String) list.get(i));
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
    public static void populateStringToHash(HashMap map, String str, String token1, String token2) {
        StringTokenizer stToken1 = null;
        StringTokenizer stToken2 = null;
        String newString = "";
        ArrayList list = new ArrayList();
        stToken1 = new StringTokenizer(str, token1);
        while (stToken1.hasMoreTokens()) {
            newString = stToken1.nextToken();
            stToken2 = new StringTokenizer(newString, token2);
            while (stToken2.hasMoreTokens()) {
                list.add((String) stToken2.nextToken());
            }
        }
        for (int i = 0; i < list.size(); i++) {
            if (i % 2 == 1) {
                map.put((String) list.get(i - 1), (String) list.get(i));
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
        ArrayList list = new ArrayList();
        String msg = "";
        Set keySet = map.keySet();
        Iterator it = keySet.iterator();
        String key = null;
        String value = null;
        while (it.hasNext()) {
            key = (String) it.next();
            msg += key + "=";
            value = (String) map.get(key);
            if (value != null)
                msg += URLEncoder.encode(value) + "&";
            else
                msg += "&";
        }
        // msg = msg.substring(0,msg.lastIndexOf("&"));
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
        long dt1 = date1.getTime();
        long dt2 = date2.getTime();
        int nodays = (int) ((dt2 - dt1) / (1000 * 60 * 60 * 24));
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
        if (p_noOfDigits <= 4)
            yearStr = yearStr.substring(yearStr.length() - p_noOfDigits, yearStr.length());
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
        String format = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT);
        if (isNullString(format)) {
            format = "dd/MM/yy";
        }
        if (format.length() != dateStr.length())
            throw new ParseException(format, 0);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false); // this is required else it will convert
        return sdf.parse(dateStr);
    }

    /**
     * Get Date String From Date
     * 
     * @param date
     * @return
     * @throws ParseException
     */
    public static String getDateStringFromDate(Date date) throws ParseException {
        String format = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT);
        if (isNullString(format)) {
            format = "dd/MM/yy";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
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
        String format = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATETIME_FORMAT);
        if (isNullString(format)) {
            format = "dd/MM/yy HH:mm";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
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

        SimpleDateFormat sdf = new SimpleDateFormat(format);
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
            format = "dd/MM/yy HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false); // this is required else it will convert
        return sdf.format(date);
    }

    public static void main(String args[]) {
        Date currentdate = new Date();

        try {
            Date d = addMilliSecondOnDays(currentdate, (1000 * 60 * 60 * 24) * 2);
            d = addMilliSecondOnDays(currentdate, -(1000 * 60 * 60 * 24) * 2);
            Calendar c = Calendar.getInstance();
            int monthMaxDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
            System.out.println(monthMaxDays);
            System.out.println(validateEmailID("12@12.cw"));
            // System.out.println("Encrypt Using AES Algo:-"+encryptText("1324"));
            // System.out.println("Decrypt Using AES Algo:-"+decryptText("dd6fd61bfbe1ad90"));
            // System.out.println("Decrypt Using AES Algo:-"+decryptText("2huKYMs/D5zIJfpzwP4K1kPidxM+MFOWVIBqPYsCQVXFkDfNnSgCyMTuTm/+iIkBppOCNeiiiUJJLvmdXNtekw=="));
            // System.out.println("Encryption Using DES Algo:-"+new
            // CryptoUtil().encrypt("pretups552_dev",Constants.KEY));
            System.out.println("isAlphaNumeric:-" + isAlphaNumericIncludingSpace("Vikas&Singh"));
            System.out.println("Decryption Using DES Algo:-" + new CryptoUtil().decrypt("927e833f12f6e5a8", Constants.KEY));
            System.out.println("Decryption Using DES Algo:-" + new CryptoUtil().decrypt("6cae7ac13edb3a83", Constants.KEY));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Date getFirstDateOfMonth(Date p_date) {

        Calendar calendar = Calendar.getInstance();

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
        if (_log.isDebugEnabled())
            _log.debug("getMessage", "Entered");
        String message = null;
        try {
            if (locale == null) {
                _log.error("getMessage", "Locale not defined considering default locale " + (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE) + " " + (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY) + "    key: " + key);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BTSLUtil[getMessage]", "", "", " ", "Locale not defined considering default locale " + (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE) + " " + (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY) + "    key: " + key);
                locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
            }
            if (_log.isDebugEnabled())
                _log.debug("getMessage", "entered locale " + locale.getDisplayName() + " key: " + key + " args: " + p_args);
            MessagesCache messagesCache = MessagesCaches.get(locale);
            if (messagesCache == null) {
                _log.error("getMessage", "Messages cache not available for locale: " + locale.getDisplayName() + "    key: " + key);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[getMessage]", "", "", " ", "Messages cache not available for locale " + locale.getDisplayName() + "    key: " + key);
                locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
                messagesCache = MessagesCaches.get(locale);
                if (messagesCache == null)
                    return null;
            }
            message = messagesCache.getProperty(key);
            // ChangeID=LOCALEMASTER
            // populate localemasterVO for the requested locale
            LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);

            if (("ru".equals(locale.getLanguage()) && message != null) || ("ar".equals(locale.getLanguage()) && message != null)) {
                if (_log.isDebugEnabled())
                    _log.debug("getMessage", "encoding msg for russian/arabic locale: " + locale.getDisplayName() + " key: " + key + " message: " + message);
                int indexOf = message.indexOf("mclass^");
                String tempMessage = null;
                String messageWithID = null;
                String messageID = null;
                String messageToEncode = null;
                if (indexOf != -1) {
                    tempMessage = message.substring(0, message.indexOf(":", indexOf) + 1);
                    messageWithID = message.substring(message.indexOf(":", indexOf) + 1);
                    // _log.error("getMessage","Before encoding tempMessage="+tempMessage+" messageWithID="+messageWithID);
                } else
                    messageWithID = message;
                if (messageWithID.lastIndexOf("[") != -1) {
                    if ("ar".equals(locale.getLanguage()) && message != null) {
                        messageToEncode = messageWithID.substring(0, messageWithID.lastIndexOf("["));
                    } else {
                        messageToEncode = messageWithID.substring(messageWithID.lastIndexOf("]") + 1);
                    }
                    _log.debug("getMessage", "Message: " + messageToEncode);
                    messageID = messageWithID.substring(messageWithID.lastIndexOf("[") + 1, messageWithID.lastIndexOf("]"));
                    // _log.error("getMessage","*************messageID="+messageID);
                    _log.debug("getMessage", "MessageID: " + messageID);
                } else
                    messageToEncode = messageWithID;
                if (_log.isDebugEnabled())
                    _log.debug("getMessage", "messageToEncode: " + messageToEncode + " messageWithID: " + messageWithID);
                // ChangeID=LOCALEMASTER
                // pass the encoding scheme from the locale master VO
                String text = encodeSpecial(messageToEncode, false, localeVO.getEncoding());

                _log.debug("getMessage", "message after encoding: " + text);
                String str = null;
                if (messageID != null) {
                    // ChangeID=LOCALEMASTER
                    // pass the encoding scheme from the locale master VO
                    String unicodeMessageID = encodeSpecial(messageID, true, localeVO.getEncoding()) + "%00%3A";
                    str = unicodeMessageID + text;
                } else
                    str = text;
                if (_log.isDebugEnabled())
                    _log.debug("getMessage", "message after encode but before replacing args str: " + str);
                if (!isNullString(tempMessage))
                    str = tempMessage + str;
                if (p_args != null && p_args.length > 0) {
                    int argslen = p_args.length;
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
                        if (p_args[index] != null)
                            p_args[index] = encodeSpecial(p_args[index], true, localeVO.getEncoding());
                    }
                }
                if (_log.isDebugEnabled())
                    _log.debug("getMessage", "message  after encoding and before formatting: " + message);
                if (str != null)
                    message = str;
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
            if (!BTSLUtil.isNullString(message))
                message = MessageFormat.format(escape(message), p_args);
            else if (!BTSLUtil.isNullString(key) && key.indexOf("_") == -1) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[getMessage]", "", "", "", " Exception: Message not defined for key" + key);
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[getMessage]", "", "", "", " Exception:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getMessage", "Exiting message: " + message);
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
        if (_log.isDebugEnabled())
            _log.debug("getMessage", "Entered locale: " + locale + " keyArgumentList size: " + keyArgumentList.size());
        String str = "";
        try {
            MessagesCache messagesCache = MessagesCaches.get(locale);
            if (messagesCache == null) {
                _log.error("getMessage", "Messages cache not available for locale " + locale);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[getMessage]", "", "", " ", "Messages cache not available for locale " + locale);
                locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
                messagesCache = MessagesCaches.get(locale);
                if (messagesCache == null)
                    return null;
            }
            KeyArgumentVO keyArgumentVO = null;
            for (int i = 0; i < keyArgumentList.size(); i++) {
                keyArgumentVO = (KeyArgumentVO) keyArgumentList.get(i);
                if (i != 0)
                    str += ", ";
                str += MessageFormat.format(messagesCache.getProperty(keyArgumentVO.getKey()), keyArgumentVO.getArguments());

                if (_log.isDebugEnabled())
                    _log.debug("getMessage", "key: " + keyArgumentVO.getKey() + " args: " + keyArgumentVO.getArguments() + " str: " + str);
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[getMessage]", "", "", "", " Exception:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getMessage", "Exiting str: " + str);
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
        if (thetime == null)
            return false;
        else {
            if (thetime.indexOf(":") == -1) {
                thetime = thetime + ":00";
            }

            if (thetime.length() < 5) {
                return false;
            }
        }
        // int size = thetime.length();
        for (int i = 0; i < 5; i++) {
            if ((i == 2)) {
                if (thetime.charAt(i) != ':')
                    return false;
            } else {
                if (!Character.isDigit(thetime.charAt(i)))
                    return false;
            }
        }
        int hr = Integer.parseInt(thetime.substring(0, 2));
        if (hr >= 24)
            return false;
        int min = Integer.parseInt(thetime.substring(3, 5));
        if (min > 59)
            return false;
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
        Calendar cal = Calendar.getInstance();
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
    public static String sqlDateToDateYYYYString(java.sql.Date d) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd'/'MM'/'yyyy");
        sdf.setLenient(false); // this is required else it will convert
        String dateString = sdf.format(d);
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
        String tempStr = "";
        if (!isNullString(strFormat)) {
            strFormat = strFormat.trim();
            String dd = strFormat.substring(0, 2);
            String mm = strFormat.substring(3, 5);
            String yyyy = strFormat.substring(6);
            // System.out.println(dd);
            // System.out.println(mm);
            // System.out.println(yyyy);
            tempStr = "Date(" + yyyy + "," + mm + "," + dd + ")";
            // System.out.println(tempStr);
        }// end of null str
        return tempStr;
    } // end of reportDateFormat

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
    public static String calcIccId(String iccId, String p_networkCode) {
        // ICCID calculation is required in case STK is registered with ICCID
        // numbers, otherwise it is not required.
        if (!SystemPreferences.STK_REG_ICCID)
            return iccId;

        String result = null;
        String oldIccId = iccId.trim();
        int len = oldIccId.length();
        char pos1, pos2;
        String newStr = "";
        String icicidCheckStr = (String) (PreferenceCache.getNetworkPrefrencesValue(PreferenceI.ICCID_CHECKSTRING, p_networkCode));
        String checkStr = null;
        if (!BTSLUtil.isNullString(icicidCheckStr))
            checkStr = oldIccId.substring(0, icicidCheckStr.length());
        if (BTSLUtil.isNullString(icicidCheckStr) || checkStr.equals(icicidCheckStr)) {
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
            // result=iccId;
            result = newStr;
        } else {
            // String str=oldIccId.substring(0,18);
            char first, second;
            for (int i = 0; i < 18; i = i + 2) {
                first = oldIccId.charAt(i);
                second = oldIccId.charAt(i + 1);
                newStr = newStr + "" + second + "" + first;
            }
            if (len == 19) {
                pos1 = oldIccId.charAt(18);
                // newStr=newStr+""+pos1;
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
        if (_log.isDebugEnabled())
            _log.debug("calcIccId", "ICC ID returned:" + result);
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
    public static boolean uploadFileToServer(FormFile p_formFile, String p_dirPath, String p_contentType, String forward, long p_fileSize) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("uploadFileToServer", "Entered :p_formFile=" + p_formFile + ", p_dirPath=" + p_dirPath + ", p_contentType=" + p_contentType + ", forward=" + forward + ", p_fileSize=" + p_fileSize);
        FileOutputStream outputStream = null;
        boolean returnValue = false;
        // modified by Manisha(18/01/08) use singal try catch
        try {
            File fileDir = new File(p_dirPath);
            if (!fileDir.isDirectory())
                fileDir.mkdirs();
            if (!fileDir.exists()) {
                _log.debug("uploadFileToServer", "Directory does not exist: " + fileDir + " ");
                throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "uploadfile.error.dirnotcreated", forward);
            }

            File fileName = new File(p_dirPath, p_formFile.getFileName());

            // if file already exist then show the error message.
            if (p_formFile != null) {
                if (p_formFile.getFileSize() <= 0)
                    throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "uploadfile.error.filesizezero", forward);
                else if (p_formFile.getFileSize() > p_fileSize)
                    throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "uploadfile.error.largefilesize", 0, new String[] { String.valueOf(p_fileSize) }, forward);

                boolean contentTypeExist = false;
                if (p_contentType.contains(",")) {
                    String temp[] = p_contentType.split(",");
                    for (int i = 0, j = temp.length; i < j; i++) {
                        if (p_formFile.getContentType().equalsIgnoreCase(temp[i].trim())) {
                            contentTypeExist = true;
                            break;
                        }
                    }
                } else if (p_formFile.getContentType().equalsIgnoreCase(p_contentType))
                    contentTypeExist = true;

                if (contentTypeExist) {
                    if (fileName.exists())
                        throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "uploadfile.error.fileexists", forward);
                    outputStream = new FileOutputStream(fileName);
                    outputStream.write(p_formFile.getFileData());
                    // outputStream.close();
                    returnValue = true;
                    if (_log.isDebugEnabled())
                        _log.debug("uploadFileToServer", "File Uploaded Successfully");
                }
                // if file is not a text file show error message
                else {
                    if (_log.isDebugEnabled())
                        _log.debug("uploadFileToServer", "Invalid content type: " + p_formFile.getContentType() + " required is p_contentType: " + p_contentType + " p_formFile.getFileName(): " + p_formFile.getFileName());
                    throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "uploadfile.error.notrequiredcontent", forward);
                }
            }
            // if there is no such file then show the error message
            else
                throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "uploadfile.error.nofile", forward);
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            _log.error("uploadFileToServer", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[uploadFileToServer]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", "error.general.processing", forward);
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            if (_log.isDebugEnabled())
                _log.debug("uploadFileToServer", "Exit :returnValue=" + returnValue);

        }
        return returnValue;
    }

    public static int getHour(Date date) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinute(Date date) {
        Calendar calendar = GregorianCalendar.getInstance();
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
        StringTokenizer st = new StringTokenizer(ipAddress, ".");
        int count = 0;
        if (st.countTokens() != 4)
            return false;
        while (st.hasMoreTokens()) {
            try {
                int part = Integer.parseInt(st.nextToken());
                if (count == 0 && part == 0)
                    return false;
                if (part > 255 || part < 0)
                    return false;
            } catch (NumberFormatException nfex) {
                return false;
            }
            count++;
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
            for (int i = 2; i < afterDecimal; i++)
                formatStr += "#";
            DecimalFormat decFormat = new DecimalFormat(formatStr);
            result = decFormat.format(number);
        } catch (Exception ex) {
            System.out.println("BTSLUtil roundToStr Exception ex=" + ex.getMessage());
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
                String[] versionArr = _allowedVersion.split(",");
                boolean versionEntered = false;
                for (int i = 0, j = versionArr.length; i < j; i++) {
                    versionEntered = true;
                    if (!(versionArr[i].indexOf(".") > 0)) {
                        return false;
                    } else if (versionArr[i].charAt(versionArr[i].length() - 1) == '.') {
                        return false;
                    } else if (versionArr[i].indexOf(".") != versionArr[i].lastIndexOf(".")) {
                        return false;
                    }
                }
                if (!versionEntered)
                    return false;
                else
                    return true;
            }
        } else
            return true;
    }

    /**
     * returns a default password text
     * 
     * @param String
     *            str
     * @return String (*****)
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

        if (time.length() == 0)
            time = "00:00";
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
        if (p_smsPin == null || p_smsPin.length() == 0)
            return true;
        // int count = 0, ctr = 0, j = 0;
        int ctr = 0, j = 0;
        char pos1 = 0, pos = 0;
        // iterates thru the p_smsPin and validates that the number is neither
        // in 123456 or 121212
        for (int i = 0; i < p_smsPin.length(); i++) {
            pos = p_smsPin.charAt(i);

            if (i < p_smsPin.length() - 1)
                pos1 = p_smsPin.charAt(i + 1);

            j = pos1;
            if (j == pos + 1 || j == pos - 1)
                ctr++;
        }

        if (ctr == (p_smsPin.length() - 1))
            validate = false;
        else
            validate = true;
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
        if (p_smsPin == null || p_smsPin.length() == 0)
            return true;

        // check the value with the default numeric value
        String defaultPin = BTSLUtil.getDefaultPasswordNumeric(p_smsPin);
        if (defaultPin.equals(p_smsPin))
            return true;

        // check the value with the default text value
        defaultPin = BTSLUtil.getDefaultPasswordText(p_smsPin);
        if (defaultPin.equals(p_smsPin))
            return true;

        // int count = 0, ctr = 0, j = 0;
        int count = 0, j = 0;
        char pos1 = 0, pos = 0;
        // iterates thru the p_smsPin and validates that the number is neither
        // in 444444 or 11111
        for (int i = 0; i < p_smsPin.length(); i++) {
            pos = p_smsPin.charAt(i);

            if (i < p_smsPin.length() - 1)
                pos1 = p_smsPin.charAt(i + 1);

            j = pos1;
            if (pos == pos1)
                count++;
            // else if(j == pos+1 || j == pos-1)
            // ctr++;
        }
        if (count == p_smsPin.length())
            validate = false;
        else
            validate = true;
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
    public static int getNoOfDaysInMonth(String date, String format) throws ParseException, Exception {
        int days = 0;
        Date temdate = null;
        temdate = BTSLUtil.getDateFromDateString(date, format);
        int day = temdate.getDate();
        int month = temdate.getMonth();
        int year = temdate.getYear() + 1900;
        Calendar calender = Calendar.getInstance();
        calender.set(year, month, day);
        days = calender.getActualMaximum(Calendar.DAY_OF_MONTH);
        return days;
    }

    public static String currentDateTimeFormatString() throws ParseException {
        java.util.Date mydate = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = sdf.format(mydate);
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
        int count = 0, ctr = 0, j = 0;
        char pos1 = 0, pos = 0;
        int result = 0;
        // iterates thru the p_smsId and validates that the number is neither in
        // 444444 or 123456 format 121212
        for (int i = 0; i < p_smsPin.length(); i++) {
            pos = p_smsPin.charAt(i);

            if (i < p_smsPin.length() - 1)
                pos1 = p_smsPin.charAt(i + 1);

            j = pos1;
            if (pos == pos1)
                count++;
            else if (j == pos + 1 || j == pos - 1)
                ctr++;
        }

        if (count == p_smsPin.length()) {
            return result = -1;
        } else if (ctr == (p_smsPin.length() - 1)) {
            return result = 1;
        } else
            return result;
    }

    /**
     * Method to validate the SMS PIn sent in the request
     * 
     * @param p_pin
     * @throws BTSLBaseException
     */
    public static void validatePIN(String p_pin) throws BTSLBaseException {
        if (isNullString(p_pin))
            throw new BTSLBaseException("BTSLUtil", "validatePIN", SelfTopUpErrorCodesI.PIN_INVALID);
        else if (!isNumeric(p_pin))
            throw new BTSLBaseException("BTSLUtil", "validatePIN", SelfTopUpErrorCodesI.NEWPIN_NOTNUMERIC);
        else if (p_pin.length() < SystemPreferences.MIN_SMS_PIN_LENGTH || p_pin.length() > SystemPreferences.MAX_SMS_PIN_LENGTH) {
            String msg[] = { String.valueOf(SystemPreferences.MIN_SMS_PIN_LENGTH), String.valueOf(SystemPreferences.MAX_SMS_PIN_LENGTH) };
            throw new BTSLBaseException("BTSLUtil", "validatePIN", SelfTopUpErrorCodesI.PIN_LENGTHINVALID, 0, msg, null);
        } else {
            int result = isSMSPinValid(p_pin);
            if (result == -1)
                throw new BTSLBaseException("BTSLUtil", "validatePIN", SelfTopUpErrorCodesI.PIN_SAMEDIGIT);
            else if (result == 1)
                throw new BTSLBaseException("BTSLUtil", "validatePIN", SelfTopUpErrorCodesI.PIN_CONSECUTIVE);
        }
    }

    /*
     * 
     * @author mohit.goel
     * 
     * Method retuen the Locale object
     */
    public static Locale getBTSLLocale(HttpServletRequest request) {
        Locale locale = (Locale) request.getSession().getAttribute(Globals.LOCALE_KEY);
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
    public static Date getDifferenceDate(Date dif_date, int howmanyday) {
        GregorianCalendar worldTour = new GregorianCalendar(dif_date.getYear() + 1900, dif_date.getMonth(), dif_date.getDate());
        worldTour.add(GregorianCalendar.DATE, howmanyday);
        Date d = worldTour.getTime();
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
        if (_log.isDebugEnabled())
            _log.debug("split", " Entered p_inStr=" + p_inStr + " p_separator=" + p_separator);
        ArrayList list = new ArrayList();
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
                    if (saparetorIndex == -1 && startIndex <= p_inStr.length() && !BTSLUtil.isNullString(p_inStr.substring(startIndex)))
                        list.add((p_inStr.substring(startIndex)).trim());
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
        if (_log.isDebugEnabled())
            _log.debug("isPeriodChangeBetweenDates", "Entered with p_oldDate=" + p_oldDate + " p_newDate=" + p_newDate + " p_param=" + p_param);
        boolean isCounterChange = false;

        if (p_oldDate != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(p_newDate);
            int presentDay = cal.get(Calendar.DAY_OF_MONTH);
            int presentWeek = cal.get(Calendar.WEEK_OF_MONTH);
            int presentMonth = cal.get(Calendar.MONTH);
            int presentYear = cal.get(Calendar.YEAR);
            cal.setTime(p_oldDate);
            int lastWeek = cal.get(Calendar.WEEK_OF_MONTH);
            int lastTrxDay = cal.get(Calendar.DAY_OF_MONTH);
            int lastTrxMonth = cal.get(Calendar.MONTH);
            int lastTrxYear = cal.get(Calendar.YEAR);
            // Day Change
            if (p_param == PERIOD_DAY) {
                if (presentDay != lastTrxDay)
                    isCounterChange = true;
                else if (presentMonth != lastTrxMonth)
                    isCounterChange = true;
                else if (presentYear != lastTrxYear)
                    isCounterChange = true;
            }
            // Week Change
            else if (p_param == PERIOD_WEEK) {
                if (presentWeek != lastWeek)
                    isCounterChange = true;
                else if (presentMonth != lastTrxMonth)
                    isCounterChange = true;
                else if (presentYear != lastTrxYear)
                    isCounterChange = true;
            }
            // Month Change
            else if (p_param == PERIOD_MONTH) {
                if (presentMonth != lastTrxMonth)
                    isCounterChange = true;
                else if (presentYear != lastTrxYear)
                    isCounterChange = true;
            }
            // Year Change
            else if (p_param == PERIOD_YEAR) {
                if (presentYear != lastTrxYear)
                    isCounterChange = true;
            }
        } else
            isCounterChange = true;

        if (_log.isDebugEnabled())
            _log.debug("isPeriodChangeBetweenDates", "Exiting with isCounterChange=" + isCounterChange);
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
        if (_log.isDebugEnabled())
            _log.debug("getMessage", "Entered locale " + locale + "    key: " + key + " args: " + args + " appendKey: " + appendKey);
        String message = null;
        try {
            message = getMessage(locale, key + "_" + appendKey, args);
            if (isNullString(message)) {
                message = getMessage(locale, key, args);
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[getMessage]", "", "", "", " Exception:" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getMessage", "Exiting message : " + message);
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
    public static String escape(String string) {
        if ((string == null) || (string.indexOf('\'') < 0)) {
            return string;
        }

        int n = string.length();
        StringBuffer sb = new StringBuffer(n);

        for (int i = 0; i < n; i++) {
            char ch = string.charAt(i);

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
        if (_log.isDebugEnabled())
            _log.debug("getByteSwappedKey", " Entered iccId : " + iccId);
        // ICCID calculation is required in case STK is registered with ICCID
        // numbers, otherwise it is not required.
        if (!SystemPreferences.STK_REG_ICCID) {
            if (_log.isDebugEnabled())
                _log.debug("getByteSwappedKey", "Exit final result : " + iccId);
            return iccId;
        }
        String result = null;
        if (BTSLUtil.isNullString(iccId))
            result = "";
        else if ("null".equalsIgnoreCase(iccId))
            result = "";
        else {
            String oldIccId = iccId.trim();
            int len = oldIccId.length();
            char pos1, pos2;
            String newStr = "";
            char first, second;
            String icicidCheckStr = (String) (PreferenceCache.getNetworkPrefrencesValue(PreferenceI.ICCID_CHECKSTRING, p_networkCode));
            String checkStr = null;
            String newIcicidCheckStr = "";
            if (!BTSLUtil.isNullString(icicidCheckStr)) {
                checkStr = oldIccId.substring(0, icicidCheckStr.length());
                for (int i = 0; i < icicidCheckStr.length(); i = i + 2) {
                    first = icicidCheckStr.charAt(i);
                    second = icicidCheckStr.charAt(i + 1);
                    newIcicidCheckStr = newIcicidCheckStr + "" + second + "" + first;
                }
            }
            if (BTSLUtil.isNullString(newIcicidCheckStr) || checkStr.equals(newIcicidCheckStr)) {
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
                    if (("" + pos1).equalsIgnoreCase("F") && ("" + pos2).equalsIgnoreCase("F"))
                        newStr = newStr;
                    else if (("" + pos1).equalsIgnoreCase("F"))
                        newStr = newStr + "" + pos2;
                    else
                        newStr = newStr + "" + pos2 + "" + pos1;
                }
                result = newStr;
            }// end of else
        }
        if (_log.isDebugEnabled())
            _log.debug("getByteSwappedKey", "Exit final result : " + result);
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
        if (_log.isDebugEnabled())
            _log.debug("isNullArray", "Entered p_arr: " + p_arr);
        boolean isNull = true;
        if (p_arr != null) {
            for (int i = 0, j = p_arr.length; i < j; i++) {
                if (!isNullString(p_arr[i])) {
                    isNull = false;
                    break;
                }
            }
        }
        if (_log.isDebugEnabled())
            _log.debug("isNullArray", "Exited isNull: " + isNull);
        return isNull;
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
        if (_log.isDebugEnabled())
            _log.debug("encodeSpecial", "Entered p_messageToEncode: " + p_messageToEncode + " p_encodeAlphanumeric:" + p_encodeAlphanumeric + "encodingToUse:" + p_encodingToUse);
        String strNumber = p_messageToEncode;
        String strFinalValue = "";
        String strUnicodeValue = null;

        // ChangeID=LOCALEMASTER
        // Use the encoding obtained from the locale master table
        String encodingToUse = p_encodingToUse;
        // If Not defined then use the default encoding
        if (BTSLUtil.isNullString(encodingToUse))
            encodingToUse = "UTF16";
        for (int intCount = 0, j = p_messageToEncode.length(); intCount < j; intCount++) {
            // System.out.println("intCount (strNumber.charAt(intCount)):::::"+intCount+"++"+((int)strNumber.charAt(intCount)));
            // if argument is numeric then encoding is diff and if other
            // language then diff.
            if (!(strNumber.charAt(intCount) >= '0' && strNumber.charAt(intCount) <= '9')) {
                if ((strNumber.charAt(intCount) >= 'A' && strNumber.charAt(intCount) <= 'Z') || (strNumber.charAt(intCount) >= 'a' && strNumber.charAt(intCount) <= 'z')) {
                    strUnicodeValue = Integer.toString((int) strNumber.charAt(intCount), 16);
                    strFinalValue = strFinalValue + "%00%" + strUnicodeValue;
                } else {
                    // if(_log.isDebugEnabled())
                    // _log.debug("encodeSpecial","Encoding char other then alphanumeric strFinalValue="+strFinalValue);
                    try {
                        strUnicodeValue = URLEncoder.encode(String.valueOf(strNumber.charAt(intCount)), encodingToUse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    strUnicodeValue = strUnicodeValue.replaceAll("%FE%FF", "");
                    strUnicodeValue = strUnicodeValue.replaceAll("%00%7B", "{");
                    strUnicodeValue = strUnicodeValue.replaceAll("%00%7D", "}");
                    strUnicodeValue = strUnicodeValue.replaceAll("%FE%FF%00%7E", "~");
                    strUnicodeValue = strUnicodeValue.replaceAll("\\+", "%00%20");

                    strUnicodeValue = strUnicodeValue.replaceAll("\\.", "%00%2E");
                    strUnicodeValue = strUnicodeValue.replaceAll("\\-", "%00%2D");
                    // i=strFinalValue.lastIndexOf("%00");
                    // if(i!=-1)
                    // strFinalValue=strFinalValue.substring(0,strFinalValue.lastIndexOf("%00"));
                    // strFinalValue=strFinalValue+strUnicodeValue+"%00";
                    strFinalValue = strFinalValue + strUnicodeValue;
                }

            } else if (!p_encodeAlphanumeric && intCount > 0 && ((strNumber.charAt(intCount) >= '0' && strNumber.charAt(intCount) <= '9') && ((strNumber.charAt(intCount - 1) >= 'A' && strNumber.charAt(intCount - 1) <= 'Z') || (strNumber.charAt(intCount - 1) >= 'a' && strNumber.charAt(intCount - 1) <= 'z')))) {
                strUnicodeValue = Integer.toString((int) strNumber.charAt(intCount), 16);
                strFinalValue = strFinalValue + "%00%" + strUnicodeValue;
            } else {
                if (p_encodeAlphanumeric) {
                    // if(_log.isDebugEnabled())
                    // _log.debug("encodeSpecial","Encoding char that are alphanumeric strFinalValue="+strFinalValue);
                    strUnicodeValue = Integer.toString((int) strNumber.charAt(intCount), 16);
                    strFinalValue = strFinalValue + "%00%" + strUnicodeValue;
                    /*
                     * if (!(intCount==(p_messageToEncode.length()-1)))
                     * {
                     * if(intCount+1<j && ((strNumber.charAt(intCount+1)>='0' &&
                     * strNumber
                     * .charAt(intCount+1)<='9')||(strNumber.charAt(intCount
                     * +1)>='A' &&
                     * strNumber.charAt(intCount+1)<='Z')||(strNumber
                     * .charAt(intCount+1)>='a' &&
                     * strNumber.charAt(intCount+1)<='z')))
                     * strFinalValue=strFinalValue+"%00";
                     * }
                     */
                } else {
                    // if(_log.isDebugEnabled())
                    // _log.debug("encodeSpecial","Not encoding char that are alphanumeric strFinalValue="+strFinalValue);
                    // i=strFinalValue.lastIndexOf("%00");
                    // if(i!=-1)
                    // strFinalValue=strFinalValue.substring(0,strFinalValue.lastIndexOf("%00"));
                    // strFinalValue=strFinalValue+String.valueOf(strNumber.charAt(intCount))+"%00";
                    strFinalValue = strFinalValue + String.valueOf(strNumber.charAt(intCount));
                }
            }
        }
        if (_log.isDebugEnabled())
            _log.debug("encodeSpecial", "Exiting strFinalValue: " + strFinalValue);

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
        String format = "ddMMyy_HHmmss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false); // this is required else it will convert
        return sdf.format(date);
    }

    /**
     * Validates identification number for numeric and MIN and MAX length
     * Change ID=ACCOUNTID
     * 
     * @param mobileno
     * @return
     */
    public static boolean isValidIdentificationNumber(String p_identificationNumber) {
        int strLength = p_identificationNumber.length();
        if (_log.isDebugEnabled())
            _log.debug("isValidIdentificationNumber", "strLength= " + strLength);
        String validationType = SystemPreferences.IDENTIFICATION_NUMBER_VAL_TYPE;
        int allowedMinLength = 0;
        int allowedMaxLength = 0;
        boolean alphaNumericIDAllowed = SystemPreferences.ALPHA_ID_NUM_ALLOWED;
        if (PretupsI.MSISDN_VALIDATION.equals(validationType)) {
            allowedMinLength = SystemPreferences.MIN_MSISDN_LENGTH;
            allowedMaxLength = SystemPreferences.MAX_MSISDN_LENGTH;
        } else if (PretupsI.OTHER_VALIDATION.equals(validationType)) {
            allowedMinLength = SystemPreferences.MIN_IDENTIFICATION_NUMBER_LENGTH;
            allowedMaxLength = SystemPreferences.MAX_IDENTIFICATION_NUMBER_LENGTH;
        } else if (PretupsI.BOTH_VALIDATION.equals(validationType)) {
            allowedMinLength = SystemPreferences.MIN_MSISDN_LENGTH < SystemPreferences.MIN_IDENTIFICATION_NUMBER_LENGTH ? SystemPreferences.MIN_MSISDN_LENGTH : SystemPreferences.MIN_IDENTIFICATION_NUMBER_LENGTH;
            allowedMaxLength = SystemPreferences.MAX_MSISDN_LENGTH > SystemPreferences.MAX_IDENTIFICATION_NUMBER_LENGTH ? SystemPreferences.MAX_MSISDN_LENGTH : SystemPreferences.MAX_IDENTIFICATION_NUMBER_LENGTH;
        }
        if (_log.isDebugEnabled())
            _log.debug("isValidIdentificationNumber", "allowedMinLength= " + allowedMinLength + " allowedMaxLength=" + allowedMaxLength);
        if ((strLength < allowedMinLength || strLength > allowedMaxLength))
            return false;
        if (PretupsI.MSISDN_VALIDATION.equals(validationType) || !alphaNumericIDAllowed) {
            for (int i = 0; i < strLength; i++) {
                if (!((Character.isDigit(p_identificationNumber.charAt(i))) || (p_identificationNumber.charAt(i) == '+')))
                    return false;
            }
        } else {
            for (int i = 0; i < strLength; i++) {
                if (Character.isSpaceChar(p_identificationNumber.charAt(i)) || !((Character.isLetterOrDigit(p_identificationNumber.charAt(i))) || (p_identificationNumber.charAt(i) == '+')))
                    return false;
            }
        }
        return true;
    }

    public static boolean isLetterOrDigit(String p_string) {
        if (_log.isDebugEnabled())
            _log.debug("isLetterOrDigit", "p_string= " + p_string);
        if (p_string != null) {
            int strLength = p_string.length();
            for (int i = 0; i < strLength; i++) {
                if (Character.isSpaceChar(p_string.charAt(i)) || !(Character.isLetterOrDigit(p_string.charAt(i))))
                    return false;
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
        String format = SystemPreferences.VOMS_DATE_FORMAT;
        if (isNullString(format)) {
            format = "dd/MM/yy";
        }
        if (format.length() != dateStr.length())
            throw new ParseException(format, 0);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
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
        String format = SystemPreferences.VOMS_DATE_FORMAT;
        if (isNullString(format)) {
            format = "dd/MM/yy";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
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
        java.util.Calendar getRight = Calendar.getInstance();
        int month = getRight.get(Calendar.MONTH);
        // System.out.println("month in getDocYear of sdnetutil="+month);
        int year = getRight.get(Calendar.YEAR);
        // System.out.println("year in getDocYear of sdnetutil="+year);
        if ((month >= 0) && (month <= 2))
            return year - 1;
        else
            return year;
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
        if (_log.isDebugEnabled())
            _log.debug("removeDuplicatesString", "Entered duplicates=" + duplicates);
        String[] unique = null;
        try {
            Arrays.sort(duplicates);
            int k = 0;
            duplicates[k] = duplicates[k].trim();
            for (int i = 0; i < duplicates.length; i++) {
                if (i > 0 && (duplicates[i].trim()).equals((duplicates[i - 1].trim())))
                    continue;
                duplicates[k++] = duplicates[i].trim();
            }
            unique = new String[k];
            System.arraycopy(duplicates, 0, unique, 0, k);
        } catch (Exception e) {
            _log.error("removeDuplicatesString", "Exception " + e.getMessage());
            e.printStackTrace();
        }
        if (_log.isDebugEnabled())
            _log.debug("removeDuplicatesString", "Exit unique=" + unique);
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
    public static BTSLMessages validateTextArea(String textAreaName, String textAreaString, int textAreaMaxAllowedSize, String messageKey, String forwardName) {
        if (_log.isDebugEnabled())
            _log.debug("validateTextArea", "Entered");
        String arg[] = new String[2];
        arg[0] = textAreaName;
        arg[1] = "" + textAreaMaxAllowedSize;
        BTSLMessages btslMessage = new BTSLMessages(messageKey, arg, forwardName);
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
            char arr[] = str.toCharArray();

            for (int i = 0; i < arr.length; i++) {
                if ((arr[i] >= 'A' && arr[i] <= 'Z') || (arr[i] >= 'a' && arr[i] <= 'z'))
                    return flag = true;
            }
        }
        return flag;
    }

    /**
     * @param p_email
     * @return
     */
    public static boolean validateEmailID(String p_email) {
        if (_log.isDebugEnabled())
            _log.debug("validateEmailID", "Entered p_email=" + p_email);
        boolean matchFound = false;
        try {
            // Set the email pattern string
            // Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
            Pattern p = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
            // Match the given string with the pattern
            Matcher m = p.matcher(p_email);
            // check whether match is found
            matchFound = m.matches();
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("validateEmailID", "Exception " + e.getMessage());
            return matchFound = false;
        }
        if (_log.isDebugEnabled())
            _log.debug("validateEmailID", "Exit matchFound=" + matchFound);
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
    public static ArrayList displayDomainList(ArrayList p_domainList) {
        if (_log.isDebugEnabled())
            _log.debug("displayDomainList", "Entered ");
        ArrayList list = new ArrayList();
        if (p_domainList != null) {
            ListValueVO listValueVO = null;
            for (int i = 0, j = p_domainList.size(); i < j; i++) {
                listValueVO = (ListValueVO) p_domainList.get(i);
                if (PretupsI.YES.equalsIgnoreCase(listValueVO.getStatusType()))
                    list.add(listValueVO);
            }

        }
        if (_log.isDebugEnabled())
            _log.debug("displayDomainList", "Exit ");
        return list;
    }

    /**
     * @author santanu.mohanty
     *         This method is used for decrypt password over the network
     * @param pass
     *            String
     * @return String
     */
    public static String decryptPasswordOverRequest(String p_password) {
        if (_log.isDebugEnabled())
            _log.debug("decryptPasswordOverRequest", "Entered " + p_password);
        p_password = p_password.trim();
        StringBuilder sbu = new StringBuilder();
        StringBuilder finalPassword = new StringBuilder();

        if (!isNullString(p_password)) {

            for (int counter = p_password.length(); counter > 0; counter--) {
                sbu.append(p_password.substring(counter - 1, counter));
            }
            String[] words = sbu.toString().split(" ");
            int dpass = 0;
            char temp;

            for (int j = 1; j <= words.length; j++) {
                dpass = Integer.parseInt(words[j - 1]) / j;
                temp = (char) dpass;
                finalPassword.append(temp);
            }
        }
        if (_log.isDebugEnabled())
            _log.debug("decryptPasswordOverRequest", "Exit ");
        return finalPassword.toString();
    }

    /**
     * returns a default password text
     * 
     * @param String
     *            str
     * @return String (*****)
     */
    public static String getDefaultPasswordTextOverHttp(String str) {
        if (_log.isDebugEnabled())
            _log.debug("getDefaultPasswordTextOverHttp", "Entered str" + str);
        String value = "";
        if (!isNullString(str)) {
            // str=decryptPasswordOverRequest(str);
            for (int i = 0, j = str.length(); i < j; i++) {
                value = value + "*";
            }
        }
        if (_log.isDebugEnabled())
            _log.debug("getDefaultPasswordTextOverHttp", "Exit value" + value);
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
        if (_log.isDebugEnabled())
            _log.debug("isTimeExpired", "Entered expPeriod" + expPeriod);
        boolean expiryStatus = false;
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTimeInMillis(creationDate.getTime());
        calendar2.setTimeInMillis(System.currentTimeMillis());
        long milliseconds1 = calendar1.getTimeInMillis();
        long milliseconds2 = calendar2.getTimeInMillis();
        long diff = milliseconds2 - milliseconds1;
        // long diffSeconds = diff / 1000;
        long diffMinutes = diff / (60 * 1000);
        // long diffHours = diff / (60 * 60 * 1000);
        // long diffDays = diff / (24 * 60 * 60 * 1000);

        if (diffMinutes >= expPeriod)
            expiryStatus = true;
        if (_log.isDebugEnabled())
            _log.debug("isTimeExpired", "Exit expiryStatus" + expiryStatus);
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
    public static String[] scheduleRptIPs(String p_basePath, String p_ipPort, String p_ipPortStr, String p_context) {

        if (_log.isDebugEnabled())
            _log.debug("scheduleRptIPs", "Entered basePath=" + p_basePath + ", p_ipPort=" + p_ipPort + ", p_ipPortStr=" + p_ipPortStr + " p_context=" + p_context);
        InputStream ins = null;
        HttpURLConnection con = null;
        String scheduleRptIPsBasePath[] = null;
        String scheduleRptIPsArr[] = null;
        String temp = null;

        try {
            scheduleRptIPsArr = p_ipPortStr.split(",");
            scheduleRptIPsBasePath = new String[2];
            if (BTSLUtil.isNullString(p_context))
                p_context = "crystal";
            for (int i = 0; i < scheduleRptIPsArr.length; i++) {
                try {
                    temp = p_basePath;
                    temp = temp.replace(p_ipPort, scheduleRptIPsArr[i]);
                    temp += p_context;

                    URL myurl = new URL(temp);
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

                    ex1.printStackTrace();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[scheduleRptIPs]", "", "", " ", "ConnectException, This server is down =" + temp);

                } catch (Exception ex) {

                    ex.printStackTrace();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[scheduleRptIPs]", "", "", " ", "Exception, This server is down =" + temp);
                } finally {
                    if (ins != null)
                        ins = null;
                    if (con != null)
                        con = null;
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception" + ex.getMessage());
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[scheduleRptIPs]", "", "", " ", "Exception1, This server is down =" + temp);
        } finally {
            try {
                if (ins != null)
                    ins.close();
            } catch (IOException ioe) {
            }
            if (con != null)
                con = null;
            if (_log.isDebugEnabled())
                _log.debug("scheduleRptIPs", "Exiting scheduleRptIPsBasePath[0]=" + scheduleRptIPsBasePath[0] + " scheduleRptIPsBasePath[1]=" + scheduleRptIPsBasePath[1]);
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
    public static ActionErrors removeErrMsg(ActionErrors p_error, String p_key, String p_property) {
        if (_log.isDebugEnabled())
            _log.debug("removeErrMsg", "Entered p_error=" + p_error + ", p_key=" + p_key + ", p_property=" + p_property);

        ActionErrors errors = new ActionErrors();
        Iterator itr = p_error.get();
        ActionMessage msg = null;
        String key = null;
        while (itr.hasNext()) {
            msg = (ActionMessage) itr.next();
            key = msg.getKey();
            if (!key.equals(p_key))
                errors.add(p_property, msg);
        }
        if (_log.isDebugEnabled())
            _log.debug("removeErrMsg", "Exiting errors= " + errors);
        return errors;
    }

    /**
     * @param p_fileType
     * @return
     */
    public static String getFileContentType(String p_fileType) {
        String contentType = null;
        if (PretupsI.FILE_CONTENT_TYPE_CSV.equals(p_fileType))
            // DB2
            // CSV,XLSChangescontentType="application/octet-stream,application/vnd.ms-excel";
            contentType = "application/octet-stream,application/csv,application/vnd.ms-excel";
        else if (PretupsI.FILE_CONTENT_TYPE_XLS.equals(p_fileType))
            // contentType="application/vnd.ms-excel
            contentType = "application/vnd.ms-excel,application/xls";// db220120105
        else if (PretupsI.FILE_CONTENT_TYPE_PLAIN_TEXT.equals(p_fileType))
            contentType = "text/plain";
        return contentType;
    }

    /**
     * This method is used to get the absolute path of class file.
     * 
     * @param cls
     * @return
     */
    public static String getFilePath(Class cls) {
        if (cls == null)
            return null;
        String name = cls.getName().replace('.', '/');
        URL loc = cls.getResource("/" + name + ".class");
        File f = new File(loc.getFile());
        // Class file is inside a jar file.
        if (f.getPath().startsWith("file:")) {
            String s = f.getPath();
            int index = s.indexOf('!');
            // It confirm it is a jar file
            if (index != -1) {
                f = new File(s.substring(5).replace('!', File.separatorChar));
                return f.getPath();
            }
        }
        try {
            f = f.getCanonicalFile();
        } catch (IOException ioe) {
            ioe.printStackTrace();
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
    public static String getDateFromString(String p_month, String p_date, String p_year) {
        if (_log.isDebugEnabled())
            _log.debug("getDateFromString", "Entered p_month=" + p_month + ", p_date=" + p_date + ", p_year=" + p_year);
        String formatedDate = null;
        String month = "";
        String year = p_year.substring(2);
        if (p_month.equalsIgnoreCase("jan"))
            month = "01";
        else if (p_month.equalsIgnoreCase("feb"))
            month = "02";
        else if (p_month.equalsIgnoreCase("mar"))
            month = "03";
        else if (p_month.equalsIgnoreCase("apr"))
            month = "04";
        else if (p_month.equalsIgnoreCase("may"))
            month = "05";
        else if (p_month.equalsIgnoreCase("jun"))
            month = "06";
        else if (p_month.equalsIgnoreCase("jul"))
            month = "07";
        else if (p_month.equalsIgnoreCase("aug"))
            month = "08";
        else if (p_month.equalsIgnoreCase("sep"))
            month = "09";
        else if (p_month.equalsIgnoreCase("oct"))
            month = "10";
        else if (p_month.equalsIgnoreCase("nov"))
            month = "11";
        else if (p_month.equalsIgnoreCase("dec"))
            month = "12";

        if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)).equalsIgnoreCase("dd/MM/yy"))
            formatedDate = p_date + "/" + month + "/" + year;
        else if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)).equalsIgnoreCase("MM/dd/yy"))
            formatedDate = month + "/" + p_date + "/" + year;
        if (_log.isDebugEnabled())
            _log.debug("getDateFromString", "Exiting forward= " + formatedDate);
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

        SimpleDateFormat sdf = new SimpleDateFormat(format);
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
        Pattern p = Pattern.compile("[a-zA-Z\\d]+?");
        // Match the given string with the pattern
        Matcher m = p.matcher(regEx);
        // check whether match is found
        boolean matchFound = false;
        matchFound = m.matches();
        if (!matchFound)
            error = "vas.error.pattern";
        return error;
    }

    /**
     * @param value
     * @return flag
     * @author shishupal.singh
     */
    public static boolean isValidDatePattern(String value) {
        boolean flag = false;
        if (value != null && value.length() == 0)
            flag = true;
        else
            try {
                getDateFromDateString(value);
                flag = true;
            } catch (ParseException e) {
                flag = false;
            }
        return flag;
    }

    public static boolean isAlphaNumeric(String regEx) {
        Pattern p = Pattern.compile("[a-zA-Z\\d]+?");
        // Match the given string with the pattern
        Matcher m = p.matcher(regEx);
        // check whether match is found
        return m.matches();
    }

    public static boolean isAlphaNumericIncludingSpace(String regEx) {
        Pattern p = Pattern.compile("[a-zA-Z\\d ]+?");
        // Match the given string with the pattern
        Matcher m = p.matcher(regEx);
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
        if (_log.isDebugEnabled())
            _log.debug("containsCapChar", "Entered str=" + str);
        int a = 0;
        if (str != null) {
            char arr[] = str.toCharArray();
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] >= 'A' && arr[i] <= 'Z')
                    ++a;
                if (a >= 1)
                    break;
            }
        }
        if (a == 1)
            return true;
        else
            return false;
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
        if (_log.isDebugEnabled())
            _log.debug("generateRandomPIN ", "Entered str " + passLength);
        if (passLength > chars.length()) {
            throw new BTSLBaseException("Password or generation is imposible, Minimum password length must be lessthen 10.");
        }
        Random m_generator = new Random(System.currentTimeMillis());
        char[] availableChars = chars.toCharArray();
        int availableCharsLeft = availableChars.length;
        StringBuffer temp = new StringBuffer(passLength);
        int pos = 0;
        for (int i = 0; i < passLength; i++) {
            pos = (int) (availableCharsLeft * m_generator.nextDouble());
            temp.append(availableChars[pos]);
            availableChars[pos] = availableChars[availableCharsLeft - 1];
            --availableCharsLeft;
        }
        if (_log.isDebugEnabled())
            _log.debug("generateRandomPIN ", "Exit value " + String.valueOf(temp));
        return String.valueOf(temp);
    }

    /*
     * add minute on days
     */
    public static Date addMinuteOnDaysInUtilMinute(java.util.Date p_date, int p_no_ofMinute) {
        Calendar cal = Calendar.getInstance();
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
        Calendar cal = Calendar.getInstance();
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
        if (utilDate != null)
            sqlDateTime = new java.sql.Timestamp(utilDate.getTime());
        return sqlDateTime;
    }// end of UtilDateToSqlDateTime Stamp

    /*
     * Ashish T
     * Change done for implementing hashing
     * String p_text is hash value
     * field is the value to be converted in hash and then compared with p_text
     */

    public static String compareHash2String(String p_hashValue, String p_text) {
        try {
            if (_log.isDebugEnabled())
                _log.debug("compareHash2String ", "Entered with p_hashValue:" + p_hashValue + "p_text" + p_text);

            if (BTSLUtil.isNullString(p_text))
                return p_hashValue;
            else {
                if (p_hashValue.equals(BTSLUtil.encryptText(p_text)))
                    return "true";
                else
                    return "false";
            }
        } catch (Exception e) {
            _log.error("compareHash2String", "Exception e=" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }// end method

    /**
     * Encrypts the passed text string using an encryption key for 3DES & AES
     * 
     * @param password
     * @return String
     */
    public static String encrypt3DesAesText(String p_text) {
        try {
            if ("AES".equals(Constants.getProperty("ENDECRYPTION_TYPE"))) {
                AESEncryptionUtil bex = new AESEncryptionUtil();
                return bex.EncryptAES(p_text);
            } else
                return new CryptoUtil().encrypt(p_text, Constants.KEY);

        } catch (Exception e) {
            _log.error("encrypt3DesAesText", "Exception e=" + e.getMessage());
            e.printStackTrace();
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
        try {
            if ("AES".equals(Constants.getProperty("ENDECRYPTION_TYPE"))) {
                AESEncryptionUtil bex = new AESEncryptionUtil();
                return bex.DecryptAES(p_text);
            } else
                return new CryptoUtil().decrypt(p_text, Constants.KEY);

        } catch (Exception e) {
            _log.error("decrypt3DesAesText", "Exception e=" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }// end method

    /**
     * To implement https when creating a url connection.
     * 
     * @param URL
     * @return
     */
    public static HttpURLConnection getConnection(URL url) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, KeyManagementException {
        if (_log.isDebugEnabled())
            _log.debug("getConnection", " Entered  URL = " + url);
        if (!CertificateLoader.isTomcatServer())
            return (HttpURLConnection) url.openConnection();
        else {
            boolean bProxySet = false;

            String proxyPassword = null;
            String proxyEnabled;
            proxyEnabled = System.getProperty("https.proxySet");
            if (null != proxyEnabled && ("true").equalsIgnoreCase(proxyEnabled)) {

                proxyPassword = System.getProperty("https.proxyPassword");
                bProxySet = true;
            }
            try {
                HostnameVerifier hv = new HostnameVerifier() {

                    public boolean verify(String urlHostName, SSLSession session) {
                        return true;
                    }
                };
                HttpsURLConnection.setDefaultHostnameVerifier(hv);
            } catch (Exception e) {
                _log.error("getConnection", " Error in Host name Verification  = " + e.getMessage());
                e.printStackTrace();
            }
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            if (CertificateLoader.getSslSocketFactory() == null)
                CertificateLoader.loadCertificateOnStartUp();
            connection.setSSLSocketFactory(CertificateLoader.getSslSocketFactory());
            // set Authenticator
            if (bProxySet) {
                Authenticator.setDefault(new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        if (_log.isDebugEnabled())
                            _log.debug("getPasswordAuthentication", " PASSWORD AUNTHENTICATION");
                        String user = System.getProperty("https.proxyUser");
                        String password = System.getProperty("https.proxyPassword");
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
            format = "dd/MM/yy";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false); // this is required else it will convert
        return sdf.format(date);
    }

    /**
     * Encrypts the passed text string using an encryption key
     * 
     * @param password
     * @return String
     */
    public static String encryptAES(String p_text) {
        try {
            return new AESEncryptionUtil().EncryptAES(p_text);
        } catch (Exception e) {
            _log.error("encryptAES", "Exception e=" + e.getMessage());
            e.printStackTrace();
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
        try {
            return new AESEncryptionUtil().DecryptAES(p_text);

        } catch (Exception e) {
            _log.error("decryptAES", "Exception e=" + e.getMessage());
            e.printStackTrace();
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
    public static StringBuffer numberToWeekdays(String num) throws BTSLBaseException {

        String[] temp = num.split(",");
        StringBuffer fullDaysString = new StringBuffer();

        String arr[] = new String[temp.length];
        arr = temp;

        for (int i = 0; i < arr.length; i++) {
            String value = arr[i];
            if (value.contains("0"))
                value = "Sun";
            if (value.contains("1"))
                value = "Mon";
            if (value.contains("2"))
                value = "Tue";
            if (value.contains("3"))
                value = "Wed";
            if (value.contains("4"))
                value = "Thu";
            if (value.contains("5"))
                value = "Fri";
            if (value.contains("6"))
                value = "Sat";

            if (i != 0)
                fullDaysString.append(",");
            fullDaysString.append(value);
        }
        return fullDaysString;
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

    public static StringBuffer weekDaysToNumber(String words) throws BTSLBaseException {
        String[] temp = words.split(",");
        StringBuffer fullWeeknames = new StringBuffer();
        String arr[] = new String[temp.length];
        arr = temp;

        for (int i = 0; i < arr.length; i++) {
            String value = arr[i];
            if (value.contains("Sun"))
                value = "0";
            if (value.contains("Mon"))
                value = "1";
            if (value.contains("Tue"))
                value = "2";
            if (value.contains("Wed"))
                value = "3";
            if (value.contains("Thu"))
                value = "4";
            if (value.contains("Fri"))
                value = "5";
            if (value.contains("Sat"))
                value = "6";

            if (i != 0)
                fullWeeknames.append(",");
            fullWeeknames.append(value);
        }
        return fullWeeknames;
    }

    public static StringBuffer getPrefixSeries(String prefixIds) {
        StringTokenizer tokenizer = new StringTokenizer(prefixIds, ",");
        NetworkPrefixVO networkPrefixVO = null;// added by arvinder
        String allowPrefixIds = "";
        StringBuffer validPrefixBuff = new StringBuffer();
        while (tokenizer.hasMoreTokens()) {
            allowPrefixIds = tokenizer.nextToken().trim();
            networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(allowPrefixIds);
            if (networkPrefixVO == null) {
                if (_log.isDebugEnabled())
                    _log.debug("add", "Allowed PrefixID doesnt match " + allowPrefixIds);
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
        if (_log.isDebugEnabled())
            _log.debug("moveFileToArchive", " Entered:: p_absoluteFilePath::=" + p_absoluteFilePath + " p_fileName::=" + p_fileName + " p_dirPath::=" + p_dirPath);

        String FileName = p_fileName.substring(0, p_fileName.length() - 4);
        File fileRead = new File(p_absoluteFilePath);
        File fileArchive = new File(String.valueOf(p_dirPath));
        if (!fileArchive.isDirectory())
            fileArchive.mkdirs();
        fileArchive = new File(String.valueOf((p_dirPath) + FileName + "_" + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime()) + ".txt"); // to
                                                                                                                                                 // make
                                                                                                                                                 // the
                                                                                                                                                 // new
                                                                                                                                                 // file
                                                                                                                                                 // name
        boolean flag = fileRead.renameTo(fileArchive);
        if (_log.isDebugEnabled())
            _log.debug("moveFileToArchive", " Exiting File Moved=" + flag);
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
        if (_log.isDebugEnabled())
            _log.debug("addCountryCodeToMSISDN", "Entered p_msisdn:" + p_msisdn);
        String msisdn = null;
        // boolean prefixFound=false;
        boolean countryCodeFound = false;
        String prefix = null;
        try {
            if (p_msisdn.length() <= SystemPreferences.MIN_MSISDN_LENGTH) {
                if (_log.isDebugEnabled())
                    _log.debug("addCountryCodeToMSISDN", "(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST):" + (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST));
                StringTokenizer strTok = new StringTokenizer((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_PREFIX_LIST), ",");
                while (strTok.hasMoreTokens()) {
                    prefix = strTok.nextToken();
                    // check if country code value matches msisdn prefix list
                    if (prefix.equals(SystemPreferences.COUNTRY_CODE)) {
                        countryCodeFound = true;
                        break;
                    } else
                        continue;
                }
                if (countryCodeFound)
                    // append the country code
                    msisdn = SystemPreferences.COUNTRY_CODE + p_msisdn;
                else
                    // if country code not found in db then msisdn remains the
                    // same
                    msisdn = p_msisdn;
            } else
                msisdn = p_msisdn;
        } catch (Exception e) {
            _log.error("addCountryCodeToMSISDN", "Exception while getting the mobile no from passed no=" + e.getMessage());
            e.printStackTrace();
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"BTSLUtil[getSaddCountryCodeToMSISDN",p_msisdn,"","Exception:"+e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[addCountryCodeToMSISDN]", "", p_msisdn, "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("addCountryCodeToMSISDN", SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("addCountryCodeToMSISDN", "Exiting Filtered msisdn=" + msisdn);
        }
        return msisdn;
    }

    public static String currentDateTimeFormatString(Date p_date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String dateString = sdf.format(p_date);
        return dateString;
    }

    public static String currentTimeFormatString(Date p_date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
        String dateString = sdf.format(p_date);
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
        HashMap ht = new HashMap();
        StringTokenizer stToken1 = null;
        StringTokenizer stToken2 = null;
        String newString = "";
        ArrayList list = new ArrayList();
        stToken1 = new StringTokenizer(str, token1);
        while (stToken1.hasMoreTokens()) {
            newString = stToken1.nextToken();
            if (newString.indexOf(token2) < 0)
                continue;
            stToken2 = new StringTokenizer(newString, token2);
            if (stToken2.countTokens() < 2)
                continue;
            while (stToken2.hasMoreTokens()) {
                list.add((String) stToken2.nextToken());
            }
        }
        for (int i = 0; i < list.size(); i++) {
            if (i % 2 == 1) {
                ht.put((String) list.get(i - 1), URLDecoder.decode((String) list.get(i)));
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
    public static boolean isValidNumber(String p_reqNumber) {
        int strLength = p_reqNumber.length();
        if (_log.isDebugEnabled())
            _log.debug("isValidNumber", "p_reqNumber length=" + strLength);

        for (int i = 0; i < strLength; i++) {
            if (!((Character.isDigit(p_reqNumber.charAt(i)))))
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
    public static boolean isValidMSISDNLength(String p_msisdn) {
        int strLength = p_msisdn.length();
        if (_log.isDebugEnabled())
            _log.debug("isValidMobileNo", "strLength= " + strLength + " SystemPreferences.MIN_MSISDN_LENGTH=" + SystemPreferences.MIN_MSISDN_LENGTH + "SystemPreferences.MAX_MSISDN_LENGTH=" + SystemPreferences.MAX_MSISDN_LENGTH + "*************");

        if ((strLength < SystemPreferences.MIN_MSISDN_LENGTH || strLength > SystemPreferences.MAX_MSISDN_LENGTH))
            return false;
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
    public static boolean isValidMSISDNDigit(String p_msisdn) {
        int strLength = p_msisdn.length();
        if (_log.isDebugEnabled())
            _log.debug("isValidMobileNo", "strLength= " + strLength + " SystemPreferences.MIN_MSISDN_LENGTH=" + SystemPreferences.MIN_MSISDN_LENGTH + "SystemPreferences.MAX_MSISDN_LENGTH=" + SystemPreferences.MAX_MSISDN_LENGTH + "*************");

        for (int i = 0; i < strLength; i++) {
            if (!((Character.isDigit(p_msisdn.charAt(i))) || (p_msisdn.charAt(i) == '+')))
                return false;
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

        if (_log.isDebugEnabled())
            _log.debug("isContainsSpecialCharacters", "p_string= " + p_string);
        boolean result = false;
        String specialCharacters = "%=&";
        try {
            specialCharacters = Constants.getProperty("SPECIAL_CHAR_MANNAGE_MESSAGE");
            if (specialCharacters == null)
                specialCharacters = "%=&";
        } catch (RuntimeException e) {
            specialCharacters = "%=&";
        }
        char[] charArray = specialCharacters.toCharArray();
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
        try {
            return new AESEncryptionUtil().EncryptAESNew(p_text, key);
        } catch (Exception e) {
            _log.error("encryptAESNew", "Exception e=" + e.getMessage());
            e.printStackTrace();
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
        try {
            return new AESEncryptionUtil().DecryptAESNew(p_text, key);

        } catch (Exception e) {
            _log.error("decryptAESNew", "Exception e=" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /***
     * @author gaurav.pandey
     * @return
     */

    public static String genrateAESKey() {
        try {
            return new AESEncryptionUtil().genrateAESKey();

        } catch (Exception e) {
            _log.error("genrateAESKey", "Exception e=" + e.getMessage());
            e.printStackTrace();
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
        int sLength = s.length();

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
        return ((s == null) || (s.trim().length() == 0));
    }

    public static boolean isWhitespace(String s) {
        // Is s empty?
        if (isEmpty(s)) {
            return true;
        }

        // Search through string's characters one by one
        // until we find a non-whitespace character.
        // When we do, return false; if we don't, return true.
        for (int i = 0; i < s.length(); i++) {
            // Check that current character isn't whitespace.
            char c = s.charAt(i);

            if (whitespace.indexOf(c) == -1) {
                return false;
            }
        }
        // All characters are whitespace.
        return true;
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
        if (_log.isDebugEnabled())
            _log.debug("checkDateFromMisDates", " Entered  p_fromDate = " + p_fromDate + " p_toDate= " + p_toDate + "p_type= " + p_type);
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
        if ((BTSLUtil.getDifferenceInUtilDates(_misFromDate, p_fromDate) >= 0) && (BTSLUtil.getDifferenceInUtilDates(p_toDate, _misToDate) >= 0))
            flag = true;
        if (_log.isDebugEnabled())
            _log.debug("checkDateFromMisDates", " Exiting flag :: " + flag);
        return flag;
    }

    /**
     * Method to return the date for monthly schedule Auto Top Up
     * 
     * @param _day
     * @return cheduleDate
     * @throws BTSLBaseException
     */
    public static Date getMonthlyScheduleDate(String p_day) throws BTSLBaseException {
        Date scheduleDate = null;
        int requestedDay = 0;
        int currentDayOfMonth = 0;
        int p_no_ofDays = 0;
        int monthMaxDays = 0;
        final String METHOD_NAME = "getMonthlyScheduleDate";
        try {
            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
            monthMaxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            if (BTSLUtil.isNumeric(p_day)) {
                requestedDay = Integer.parseInt(p_day);
            } else {
                _log.error("process", "DAY is not numeric");
                throw new BTSLBaseException("BTSLUtil", "getMonthlyScheduleDate", SelfTopUpErrorCodesI.AUTO_TOPUP_NONNUMERIC_DAY);
            }
            if (!(Integer.parseInt(p_day) >= 1 && Integer.parseInt(p_day) <= monthMaxDays)) {
                _log.error("process", "DAY MUST BE BETWEEN 1 AND " + monthMaxDays);
                String arr[] = new String[] { ((Integer) monthMaxDays).toString() };
                throw new BTSLBaseException("BTSLUtil", "getMonthlyScheduleDate", SelfTopUpErrorCodesI.AUTO_TOPUP_MONTH_DAY_ERROR, arr);
            }
            currentDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
            if (currentDayOfMonth == requestedDay) {
                cal.add(Calendar.DATE, monthMaxDays);
                scheduleDate = cal.getTime();
            } else if ((requestedDay - currentDayOfMonth) >= 2) {
                p_no_ofDays = (requestedDay - currentDayOfMonth);
                cal.add(Calendar.DATE, p_no_ofDays);
                scheduleDate = cal.getTime();
            } else {
                p_no_ofDays = (monthMaxDays - currentDayOfMonth + requestedDay);
                if (p_no_ofDays <= 2)
                    cal.add(Calendar.DATE, p_no_ofDays + monthMaxDays);
                else
                    cal.add(Calendar.DATE, p_no_ofDays);
                scheduleDate = cal.getTime();
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("getMonthlyScheduleDate", " Exiting: scheduleDate " + scheduleDate);
            }
        }
        return scheduleDate;
    }

    /**
     * Method to return the date for weekly schedule Auto Top Up
     * 
     * @param _day
     * @return cheduleDate
     * @throws BTSLBaseException
     */
    public static Date getWeeklyScheduleDate(String p_day) throws BTSLBaseException {
        Date scheduleDate = null;
        int requestedDay = 0;
        int currentDayOfWeek = 0;
        int p_no_ofDays = 0;
        final String METHOD_NAME = "getWeeklyScheduleDate";
        try {
            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
            if (BTSLUtil.isNumeric(p_day)) {
                requestedDay = Integer.parseInt(p_day);
            } else {
                _log.error("process", "DAY is not numeric");
                throw new BTSLBaseException("BTSLUtil", "getWeeklyScheduleDate", SelfTopUpErrorCodesI.AUTO_TOPUP_NONNUMERIC_DAY);
            }
            if (!(requestedDay >= 1 && requestedDay <= 7)) {
                _log.error("process", "DAY MUST BE BETWEEN 1 AND 7");
                throw new BTSLBaseException("BTSLUtil", "getWeeklyScheduleDate", SelfTopUpErrorCodesI.AUTO_TOPUP_WEEK_DAY_ERROR);
            }
            currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            if (currentDayOfWeek == requestedDay) {
                cal.add(Calendar.DATE, PretupsI.NO_OF_DAYS_IN_WEEK);
                scheduleDate = cal.getTime();
            }
            // else if ( requestedDay>=(currentDayOfWeek+2)%NO_OF_DAYS_IN_WEEK){
            else if ((requestedDay - currentDayOfWeek) >= 2) {
                p_no_ofDays = (requestedDay + PretupsI.NO_OF_DAYS_IN_WEEK - currentDayOfWeek) % PretupsI.NO_OF_DAYS_IN_WEEK;
                cal.add(Calendar.DATE, p_no_ofDays);
                scheduleDate = cal.getTime();
            } else {
                p_no_ofDays = (requestedDay + PretupsI.NO_OF_DAYS_IN_WEEK - currentDayOfWeek) % PretupsI.NO_OF_DAYS_IN_WEEK;
                cal.add(Calendar.DATE, p_no_ofDays);
                scheduleDate = cal.getTime();
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("getWeeklyScheduleDate", " Exiting: scheduleDate:" + scheduleDate);
            }
        }
        return scheduleDate;

    }

}
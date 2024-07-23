package com.inter.claroca.iat;

/*
 * Created on Jul 6, 2009
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;

public class IATUtil {

    private static Log _log = LogFactory.getLog(InterfaceUtil.class.getName());

    /**
     * 
     * @param p_validAmount
     * @param p_multiplicationFactor
     * @return
     * @throws BTSLBaseException
     */
    public static long getSystemAmount(double p_validAmount, int p_multiplicationFactor) throws BTSLBaseException {
        if (_log.isDebugEnabled())
            _log.debug("getSystemAmount", "Entered p_validAmount=" + p_validAmount + " MultiplicationFactor=" + p_multiplicationFactor);
        long amount = 0;
        amount = (long) (p_validAmount * (double) p_multiplicationFactor);
        if (_log.isDebugEnabled())
            _log.debug("getSystemAmount", "Exiting amount:" + amount);
        return amount;
    }

    /**
     * 
     * @param p_amount
     * @param p_multiplicationFactor
     * @return
     */
    public static String getDisplayAmount(long p_amount, int p_multiplicationFactor) {
        if (_log.isDebugEnabled())
            _log.debug("getDisplayAmount", "Entered p_amount:" + p_amount + " MultiplicationFactor " + p_multiplicationFactor);
        double amount = (double) p_amount / (double) p_multiplicationFactor;
        String amountStr = new DecimalFormat("##############.###").format(amount);
        if (_log.isDebugEnabled())
            _log.debug("getDisplayAmount", "Exiting display amount:" + amountStr);
        return amountStr;
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
     * Get Date From Date String
     * 
     * @param dateStr
     * @param format
     * @return
     * @throws ParseException
     */
    public static Date getDateTimeFromDateString(String dateStr) throws ParseException {

        String format = PretupsI.TIMESTAMP_DATESPACEHHMM;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false); // this is required else it will convert
        return sdf.parse(dateStr);
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
     * Used to convert the amount by multipling the amount with multiple
     * factor(double)
     * 
     * @param p_validAmount
     * @param p_multiplicationFactor
     * @return
     * @throws BTSLBaseException
     */
    public static double getINAmountFromSystemAmountToIN(double p_validAmount, double p_multiplicationFactor) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getINAmountFromSystemAmountToIN", "Entered p_validAmount=" + p_validAmount + "p_multiplicationFactor=" + p_multiplicationFactor);
        double amount = 0;
        try {
            amount = p_validAmount / p_multiplicationFactor;
            // roundAmount=Math.round(amount);
        } catch (Exception e) {
            throw e;
        }
        if (_log.isDebugEnabled())
            _log.debug("getINAmountFromSystemAmountToIN", "Exiting amount:" + amount);
        return amount;
    }

    public static double getSystemAmountFromINAmount(double p_amountStr, double p_multiplicationFactor) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getSystemAmountFromINAmount", "Entered p_amountStr:" + p_amountStr + " MultiplicationFactor " + p_multiplicationFactor);
        long amountLong = 0;
        try {
            double amountDouble = 0;
            // double amountDouble = Double.parseDouble(p_amountStr);
            amountDouble = p_amountStr * p_multiplicationFactor;
            amountLong = Math.round(amountDouble);
        } catch (Exception e) {
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getSystemAmountFromINAmount", "Exiting  amountLong:" + amountLong);
        }
        return amountLong;
    }

    /**
     * Method to return mapped Error Codes against transaction response error
     * code and cancel response error code.
     * 
     * @param p_map
     * @param inErrorCode
     * @param mapping
     * @throws BTSLBaseException
     * @return String
     */
    public static String getErrorCodeFromMapping(HashMap p_map, String inErrorCode, String mapping) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getErrorCodeFromMapping", "Entered. inErrorCode=" + inErrorCode + ",mapping=" + mapping);
        String errorCode = null;
        String[] mappingArr;
        String[] inErrorCodeArr;
        boolean errorCodeFound = false;
        try {
            // SYSTEM_STATUS_MAPPING=SUCCESS,ALREADY_DONE,NOT_REACHED,FAIL:350|AMBIGUOUS,NA:250
            String errorCodeMappingStr = (String) p_map.get(mapping);
            mappingArr = errorCodeMappingStr.split("\\|");
            for (int in = 0, len = mappingArr.length; in < len; in++) {
                inErrorCodeArr = mappingArr[in].split(":");
                Object[] tempArr = inErrorCodeArr[0].split(",");
                if (Arrays.asList(tempArr).contains(inErrorCode)) {
                    errorCodeFound = true;
                    errorCode = inErrorCodeArr[1];
                    break;
                }
            }// end of for loop
            if (!errorCodeFound) {
                // Alarm should be raised?
                errorCode = InterfaceErrorCodesI.AMBIGOUS;
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "InterfaceUtil[getErrorCodeFromMapping]", (String) p_map.get("TRANSACTION_ID"), (String) p_map.get("MSISDN"), (String) p_map.get("NETWORK_CODE"), "Mapping not found for IN Error code '" + inErrorCode + "' in " + mapping + "parameter of IN File");
            }
        }// end of try
        catch (Exception e) {
            e.printStackTrace();
            _log.error("getErrorCodeFromMapping", "Exception e=" + e);
            errorCode = InterfaceErrorCodesI.AMBIGOUS;
            // Alarm should be raised?
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.INTERFACES, EventStatusI.RAISED, EventLevelI.INFO, "InterfaceUtil[getErrorCodeFromMapping]", (String) p_map.get("TRANSACTION_ID"), (String) p_map.get("MSISDN"), (String) p_map.get("NETWORK_CODE"), "Error occured during parsing  of key " + mapping + " defined in IN file");
            // throw new
            // BTSLBaseException(this,InterfaceErrorCodesI.INTERFACE_HANDLER_EXCEPTION,"Error occured during parsing  of key SYSTEM_STATUS_MAPPING defined in IN file");
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("getErrorCodeFromMapping", "Exited errorCode =" + errorCode);
        }// end of finally setLanguageFromMapping
        return errorCode;
    } // End of equestIN method

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
     * This method will return Date & Time in required Format
     * 
     * @param String
     *            format
     * @return String
     * @throws Exception
     */
    public static String getDateTimeFormat(String format) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getDateTimeFormat", "Enetered: format= " + format);
        String dateString = null;
        Date date = null;
        SimpleDateFormat sdf = null;
        try {
            date = new Date();
            sdf = new SimpleDateFormat(format);
            sdf.setLenient(false);
            dateString = sdf.format(date);
        }// end of try block
        catch (Exception e) {
            _log.error("getDateTimeFormat", "Exception e=" + e);
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("getDateTimeFormat", "Exited dateString= " + dateString);
        }// end of finally
        return dateString;
    }// end of getDateTimeFormat

    /**
     * This method will calculate & return number of date difference between two
     * dates
     * 
     * @param Date
     *            date1, Date date2
     * @return integer
     */
    public static int getDifferenceInUtilDates(Date date1, Date date2) {
        long dt1 = date1.getTime();
        long dt2 = date2.getTime();
        int noOfDaysDiff = (int) ((dt1 - dt2) / (1000 * 60 * 60 * 24));
        return noOfDaysDiff;
    }// end of getDifferenceInUtilDates

    public static boolean isNullString(String str) {
        if (str == null || str.trim().length() == 0 || str.equals("null"))
            return true;
        else
            return false;
    }

    public static String getCalenderFromString(String p_format) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getCalenderFromString", " p_format =" + p_format);
        String transDateTime = null;
        try {
            SimpleDateFormat callDate = new SimpleDateFormat(p_format);
            Date date = Calendar.getInstance().getTime();

            transDateTime = callDate.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getCalenderFromString", "Exception e = " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getCalenderFromString", " transDateTime = " + transDateTime);
        }
        return transDateTime;
    }

    public static Calendar getDateFromDateString(Date p_date) throws ParseException {
        DateFormat formatter;
        String format = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT));
        if (isNullString(format)) {
            format = PretupsI.DATE_FORMAT;
        }
        formatter = new SimpleDateFormat(format);
        formatter.setLenient(false); // this is required else it will convert
        Calendar cal = formatter.getCalendar();
        cal.setTime(p_date);

        return cal;
    }

    public static void main(String[] args) {
        try {
            String str_date = "11-June-07";
            DateFormat formatter;
            Date date;
            formatter = new SimpleDateFormat("dd-MMM-yy");
            date = (Date) formatter.parse(str_date);
            Calendar cal = Calendar.getInstance();
            // cal.setTime(date);
            System.out.println("Today is " + cal);
        } catch (ParseException e) {
            System.out.println("Exception :" + e);
        }

    }

    /**
     * Get Display Amount
     * 
     * @param p_amount
     * @return
     * @throws BTSLBaseException
     */
    /*
     * moved to BTSLUtil.java
     * public static double getDisplayAmount(double p_amount)
     * {
     * int multiplicationFactor=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
     * double amount=0d;
     * try
     * {
     * amount=p_amount/(double)multiplicationFactor;
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * }
     * return amount;
     * }
     */
    /**
     * Method convertStringToDate USed to Convert String to date of format
     * yyyy-MM-dd HH:mm:ss
     * 
     * @param p_dateString
     * @return Date
     * @throws ParseException
     * @author babu.kunwar
     */
    public static Date convertStringToDate(String p_dateString) throws ParseException {
        String format = "";
        if (isNullString(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        if (format.length() != p_dateString.length())
            throw new ParseException(format, 0);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false); // this is required else it will convert
        return sdf.parse(p_dateString);

    }
}

package com.selftopup.pretups.inter.module;

/**
 * @(#)InterfaceUtil.java
 *                        Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 *                        All Rights Reserved
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Abhijit Chauhan June 22,2005 Initial Creation
 *                        ------------------------------------------------------
 *                        ------------------------------------------
 */
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.inter.cache.FileCache;
import com.selftopup.util.OracleUtil;

public class InterfaceUtil {
    private static Log _log = LogFactory.getLog(InterfaceUtil.class.getName());
    private static int _txnCounter = 0;

    public static String getEricssionCurrentDateTime() {
        // String s;
        Calendar calender = Calendar.getInstance();
        String str = "";
        String s = "";
        int t = 0;
        t = calender.get(Calendar.YEAR);
        s = s + t;

        t = calender.get(Calendar.MONTH);
        t++;
        if (t / 10 == 0) {
            s += "0" + t;
        } else {
            s += t;
        }

        t = calender.get(Calendar.DATE);
        if (t / 10 == 0) {
            s += "0" + t;
        } else {
            s += t;
        }

        s += "T";

        t = calender.get(Calendar.HOUR_OF_DAY);
        if (t / 10 == 0) {
            s += "0" + t;
        } else {
            s += t;
        }

        t = calender.get(Calendar.MINUTE);
        if (t / 10 == 0) {
            s += "0" + t;
        } else {
            s += t;
        }

        t = calender.get(Calendar.SECOND);
        if (t / 10 == 0) {
            s += "0" + t;
        } else {
            s += t;
        }
        return s;
    }

    public static String getPrintMap(HashMap p_map) {
        return (" TRANSACTION_ID:" + (String) p_map.get("TRANSACTION_ID") + " MSISDN:" + (String) p_map.get("MSISDN") + " NETWORK_CODE:" + (String) p_map.get("NETWORK_CODE") + " INTERFACE_ID:" + (String) p_map.get("INTERFACE_ID") + " INTERFACE_HANDLER:" + (String) p_map.get("INTERFACE_HANDLER"));
    }

    /**
     * Get IN TransactionID
     * 
     * @return
     * @throws BTSLBaseException
     */
    public static String getINTransactionID() {
        if (_log.isDebugEnabled())
            _log.debug("getINTransactionID", "Entered");
        java.util.Date mydate = new java.util.Date();
        // Change on 17/05/06 for making the TXN ID as unique in Interface
        // Transaction Table (CR00021)
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSSSS");
        String dateString = sdf.format(mydate);
        if (_log.isDebugEnabled())
            _log.debug("getINTransactionID", "Exited  id: " + dateString);
        return dateString;
    }

    public static String NullToString(String p_str) {
        if (p_str == null)
            return "";
        else
            return p_str;
    }

    public static String trimString(String p_str) {
        if (p_str != null)
            return p_str.trim();
        else
            return p_str;
    }

    public static boolean isNullString(String str) {
        if (str == null || str.trim().length() == 0 || str.equals("null"))
            return true;
        else
            return false;
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
        /*
         * while(stToken1.hasMoreTokens())
         * {
         * newString = stToken1.nextToken();
         * if(newString.indexOf(token2)<0)
         * continue;
         * stToken2 = new StringTokenizer(newString, token2);
         * while(stToken2.hasMoreTokens())
         * {
         * list.add((String)stToken2.nextToken());
         * }
         * }
         */

        while (stToken1.hasMoreTokens()) {
            newString = stToken1.nextToken();
            if (newString.indexOf(token2) < 0)
                continue;
            stToken2 = new StringTokenizer(newString, token2);
            boolean addAnother = false;
            if (stToken2.countTokens() < 2)
                addAnother = true;
            while (stToken2.hasMoreTokens()) {
                list.add((String) stToken2.nextToken());
                if (addAnother) {
                    list.add("");
                    addAnother = false;
                }
            }
        }
        for (int i = 0; i < list.size(); i++) {
            if (i % 2 == 1) {
                // map.put((String)list.get(i-1),(String)list.get(i));
                map.put(((String) list.get(i - 1)).trim(), ((String) list.get(i)).trim());
            } // End of if Statement
        } // End of for loop
    }

    public static String getCurrentDateString(String p_format) {
        Date date = new Date();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(p_format);
            sdf.setLenient(false); // this is required else it will convert
            return sdf.format(date);
        } catch (Exception e) {
            SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
            sdf.setLenient(false); // this is required else it will convert
            return sdf.format(date);
        }
    }

    public static String getInterfaceDateFromDateString(String dateStr, String p_format) {
        String format = p_format;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setLenient(false); // this is required else it will convert
            Date d1 = sdf.parse(dateStr);
            sdf = new SimpleDateFormat("ddMMyyyy");
            sdf.setLenient(false); // this is required else it will convert
            return sdf.format(d1);
        } catch (Exception e) {
            return null;
        }
    }

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

    public static void insertInDatabase(String p_txnType, HashMap p_requestMap) {
        if (_log.isDebugEnabled())
            _log.debug("insertInDatabase", "Entered p_txnType:" + p_txnType);
        Connection con = null;
        try {
            InterfaceModuleDAO interfaceModuleDAO = new InterfaceModuleDAO();
            con = OracleUtil.getConnection();
            interfaceModuleDAO.addInterfaceModuleDetails(con, getVOFromMap(p_txnType, p_requestMap, "U"));
            con.commit();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (Exception ex) {
            }
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception ex) {
                }
            }
        }
        if (_log.isDebugEnabled())
            _log.debug("insertInDatabase", "Exiting");
    }

    public static void updateInDatabase(String p_txnType, HashMap p_requestMap, String p_txnStatus) {
        Connection con = null;
        try {
            InterfaceModuleDAO interfaceModuleDAO = new InterfaceModuleDAO();
            con = OracleUtil.getConnection();
            interfaceModuleDAO.updateInterfaceModuleDetails(con, (String) p_requestMap.get("IN_TXN_ID"), getVOFromMap(p_txnType, p_requestMap, "C"));
            con.commit();
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (Exception ex) {
            }
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    public static InterfaceModuleVO getVOFromMap(String txnType, HashMap _map, String p_status) {
        if (_log.isDebugEnabled())
            _log.debug("getVOFromMap", "Entered txnType: " + txnType + " _map: " + _map + " p_status=" + p_status);
        InterfaceModuleVO interfaceModuleVO = new InterfaceModuleVO();
        String txnID = (String) _map.get("IN_TXN_ID");
        if (txnID == null) {
            txnID = InterfaceUtil.getINTransactionID();
            _map.put("IN_TXN_ID", txnID);
        }
        interfaceModuleVO.setTxnID(txnID);
        interfaceModuleVO.setReferenceID((String) _map.get("TRANSACTION_ID"));
        interfaceModuleVO.setMsisdn((String) _map.get("MSISDN"));
        try {
            interfaceModuleVO.setRequestValue(Long.parseLong((String) _map.get("INTERFACE_AMOUNT")));
        } catch (Exception e) {
        }
        try {
            interfaceModuleVO.setPreviousBalance(Long.parseLong((String) _map.get("INTERFACE_PREV_BALANCE")));
        } catch (Exception e) {
        }
        try {
            interfaceModuleVO.setPostBalance(Long.parseLong((String) _map.get("INTERFACE_POST_BALANCE")));
        } catch (Exception e) {
        }
        try {
            interfaceModuleVO.setValidity(Integer.parseInt((String) _map.get("VALIDITY_DAYS")));
        } catch (Exception e) {
        }
        interfaceModuleVO.setInterfaceType((String) _map.get("INTERFACE_TYPE"));
        interfaceModuleVO.setInterfaceID((String) _map.get("INTERFACE_ID"));
        interfaceModuleVO.setInterfaceResonseCode((String) _map.get("INTERFACE_STATUS"));
        interfaceModuleVO.setCardGroup((String) _map.get("CARD_GROUP"));
        interfaceModuleVO.setServiceClass((String) _map.get("SERVICE_CLASS"));
        interfaceModuleVO.setUrlID((String) _map.get("URL_ID"));

        try {
            interfaceModuleVO.setBonusValue(Long.parseLong((String) _map.get("BONUS_AMOUNT")));
        } catch (Exception e) {
        }
        try {
            interfaceModuleVO.setBonusValidity(Integer.parseInt((String) _map.get("BONUS_VALIDITY_DAYS")));
        } catch (Exception e) {
        }

        try {
            interfaceModuleVO.setMsisdnPreviousExpiry(getDateFromDateString((String) _map.get("OLD_EXPIRY_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
        }
        try {
            interfaceModuleVO.setMsisdnNewExpiry(getDateFromDateString((String) _map.get("NEW_EXPIRY_DATE"), "ddMMyyyy"));
        } catch (Exception e) {
        }
        interfaceModuleVO.setTxnStatus((String) _map.get("TRANSACTION_STATUS"));
        interfaceModuleVO.setTxnResponseReceived(p_status);
        interfaceModuleVO.setTxnType(txnType);
        interfaceModuleVO.setTxnDateTime(new Date());
        if (_map.get("IN_START_TIME") != null)
            interfaceModuleVO.setTxnStartTime(((Long.valueOf((String) _map.get("IN_START_TIME"))).longValue()));
        if (_map.get("IN_END_TIME") != null)
            interfaceModuleVO.setTxnEndTime(((Long.valueOf((String) _map.get("IN_END_TIME"))).longValue()));
        // added for logging details present in interface_transaction table
        // (Manisha 04/02/08)
        try {
            interfaceModuleVO.setBonusSMS(Double.parseDouble((String) _map.get("BONUS1")));
        } catch (Exception e) {
        }
        try {
            interfaceModuleVO.setBonusMMS(Double.parseDouble((String) _map.get("BONUS2")));
        } catch (Exception e) {
        }
        interfaceModuleVO.setUserType((String) _map.get("USER_TYPE"));
        interfaceModuleVO.setServiceType((String) _map.get("REQ_SERVICE"));
        // added by vikask for cardgroup updation
        try {
            interfaceModuleVO.setBonusSMSValidity(Long.parseLong((String) _map.get("BONUS1_VAL")));
        } catch (Exception e) {
        }
        try {
            interfaceModuleVO.setBonusMMSValidity(Long.parseLong((String) _map.get("BONUS2_VAL")));
        } catch (Exception e) {
        }
        try {
            interfaceModuleVO.setCreditbonusValidity(Long.parseLong((String) _map.get("CREDIT_BONUS_VAL")));
        } catch (Exception e) {
        }
        // added by amit
        interfaceModuleVO.setBoth((String) _map.get("COMBINED_RECHARGE"));
        interfaceModuleVO.setOnLine((String) _map.get("IMPLICIT_RECHARGE"));

        return interfaceModuleVO;
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
     * Get Filter MSISDN
     * 
     * @param p_interfaceID
     * @param p_msisdn
     * @return
     */
    public static String getFilterMSISDN(String p_interfaceID, String p_msisdn) {
        String filterMSISDN = null;
        /*
         * String
         * removeStr=FileCache.getValue(p_interfaceID,"MSISDN_REMOVE_PREFIX");
         * String addStr=FileCache.getValue(p_interfaceID,"MSISDN_ADD_PREFIX");
         * if(removeStr!=null&&filterMSISDN.indexOf(removeStr)==0)
         * filterMSISDN=filterMSISDN.replaceFirst(removeStr,"");
         * if(addStr!=null)
         * filterMSISDN=addStr+filterMSISDN;
         */
        boolean prefixFound = false;
        String prefix = FileCache.getValue(p_interfaceID, "MSISDN_REMOVE_PREFIX");
        String addStr = FileCache.getValue(p_interfaceID, "MSISDN_ADD_PREFIX");
        if (p_msisdn.startsWith(prefix, 0))
            prefixFound = true;
        if (prefixFound)
            filterMSISDN = p_msisdn.substring(prefix.length());
        else
            filterMSISDN = p_msisdn;
        if (addStr != null)
            filterMSISDN = addStr + filterMSISDN;
        return filterMSISDN;
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
     * This method is used to formate the transaction date time for siemens
     * interface
     * 
     * @param p_format
     * @return
     * @throws Exception
     */
    public static String getSiemensTransDateTime(String p_format) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getSiemensTransDateTime", " p_format =" + p_format);
        String transDateTime = null;
        try {
            SimpleDateFormat callDate = new SimpleDateFormat(p_format);
            Date date = Calendar.getInstance().getTime();
            transDateTime = callDate.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("getSiemensTransDateTime", "Exception e = " + e.getMessage());
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getSiemensTransDateTime", " transDateTime = " + transDateTime);
        }
        return transDateTime;
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

    public static String getSystemAmountFromINAmount(String p_amountStr, double p_multiplicationFactor) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getSystemAmountFromINAmount", "Entered p_amountStr:" + p_amountStr + " MultiplicationFactor " + p_multiplicationFactor);
        long amountLong = 0;
        try {
            double amountDouble = Double.parseDouble(p_amountStr);
            amountDouble = amountDouble * p_multiplicationFactor;
            amountLong = Math.round(amountDouble);
        } catch (Exception e) {
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getSystemAmountFromINAmount", "Exiting  amountLong:" + amountLong);
        }
        return String.valueOf(amountLong);
    }

    public static String getTrunncatedSystemAmountFromINAmount(String p_amountStr, double p_multiplicationFactor, int truncateAfterDec) throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getSystemAmountFromINAmount", "Entered p_amountStr:" + p_amountStr + " MultiplicationFactor " + p_multiplicationFactor + "truncateAfterDec=" + truncateAfterDec);
        long amountLong = 0;
        String truncatedAmoutntStr = null;
        try {
            double amountDouble = Double.parseDouble(p_amountStr);
            amountDouble = amountDouble * p_multiplicationFactor;
            // amountLong = Math.round(amountDouble);
            truncatedAmoutntStr = roundToStr(amountDouble, truncateAfterDec);
        } catch (Exception e) {
            throw e;
        } finally {
            if (_log.isDebugEnabled())
                _log.debug("getSystemAmountFromINAmount", "Exiting  amountLong:" + amountLong);
        }
        return truncatedAmoutntStr;
    }

    /**
     * Get IN TransactionID
     * 
     * @return String
     * @throws BTSLBaseException
     */
    public static String getTimeWhenAmbiguousCaseOccured() {
        if (_log.isDebugEnabled())
            _log.debug("getTimeWhenAmbigousCaseOccured", "Entered");
        java.util.Date mydate = new java.util.Date();
        // Change on 17/05/06 for making the TXN ID as unique in Interface
        // Transaction Table (CR00021)
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSS");
        String dateString = sdf.format(mydate);
        if (_log.isDebugEnabled())
            _log.debug("getTimeWhenAmbigousCaseOccured", "Exited  id: " + dateString);
        return dateString;
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
            if (_log.isDebugEnabled())
                _log.debug("", "BTSLUtil roundToStr Exception ex=" + ex.getMessage());
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

    /*
     * public static void main(String[] ar)
     * {
     * System.out.println(isStringIn("1","0,2,10"));
     * // System.out.println(" value ="+(double)(4.5/2.3));
     * //System.out.println(" value ="+Math.round((double)(4.5*2.3)));
     * }
     */

    public static int getCharCount(String str, char c) {
        int count = 0;
        String subString = str;
        while (subString.indexOf(c) != -1) {
            subString = subString.substring(subString.indexOf(c) + 1);
            count++;

        }
        return count;
    }

    public static String printByteData(byte[] p_message) {
        StringBuffer messageStr = null;
        if (p_message != null) {
            messageStr = new StringBuffer();
            for (int i = 0; i < p_message.length; i++) {
                messageStr.append((new StringBuilder()).append(p_message[i]).append("").toString());
            }
        }
        return messageStr.toString();
    }

    public synchronized static String getINTxnID() throws Exception {
        if (_log.isDebugEnabled())
            _log.debug("getINTxnID", "Entered ");
        // This method will be used when we have transID based on database
        // sequence.
        String inTransactionID = "";
        int inTxnLength = 21;
        int length = 0;
        int tmpLength = 0;
        try {
            if (_txnCounter >= 998)
                _txnCounter = 0;
            inTransactionID = getINTransactionID() + "." + _txnCounter++;
            if (_log.isDebugEnabled())
                _log.debug("getINTxnID", "counter :" + inTransactionID);
            length = inTransactionID.length();
            tmpLength = inTxnLength - length;
            if (length < inTxnLength) {
                for (int i = 0; i < tmpLength; i++)
                    inTransactionID = inTransactionID + 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled())
                _log.debug("getINTxnID", "Exit inTransactionID:" + inTransactionID);
        }// end of finally
        return inTransactionID;
    }// end of getINReconTxnID

}

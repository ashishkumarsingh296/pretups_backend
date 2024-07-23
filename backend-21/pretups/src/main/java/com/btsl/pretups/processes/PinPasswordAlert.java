package com.btsl.pretups.processes;

/*
 * PinPasswordAlert.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Ankit Singhal 23/11/2005 Initial Creation
 * Ankit Singhal 07/03/2007 Modification
 * Ved Prakash 16/03/07 Modification
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Main class for sending alerts to subscribers whose PIN or password
 * will expire after certain days.
 */

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.ProcessesLog;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.PinPasswordAlertVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

public class PinPasswordAlert {
    private static Log _logger = LogFactory.getLog(PinPasswordAlert.class.getName());

    /**
     * to ensure no class instantiation 
     */
    private PinPasswordAlert(){
    	
    }
    public static void main(String[] args) {
        int alertDays = 0;
        final String METHOD_NAME = "main";
        try {
            final File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
                _logger.info(METHOD_NAME, " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = new File(args[1]);
            if (!logconfigFile.exists()) {
                _logger.info(METHOD_NAME, " Logconfig File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        }// end try
        catch (Exception ex) {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, " Error:" + ex.getMessage());
            }

            _logger.errorTrace(METHOD_NAME, ex);
            ConfigServlet.destroyProcessCache();
            return;
        }

        alertDays = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PIN_PASSWORD_ALERT_DAYS))).intValue();
        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();
            if ("P2PPIN".equalsIgnoreCase(args[2]) || "C2SPIN".equalsIgnoreCase(args[2]) || "CHNLPWD".equalsIgnoreCase(args[2]) || "OPTPWD".equalsIgnoreCase(args[2])) {
                if ("P2PPIN".equalsIgnoreCase(args[2])) {
                    p2pPinAlert(con, alertDays);
                }
                if ("C2SPIN".equalsIgnoreCase(args[2])) {
                    c2sPinAlert(con, alertDays);
                }
                if ("CHNLPWD".equalsIgnoreCase(args[2])) {
                    channelPasswordAlert(con, alertDays);
                }
                if ("OPTPWD".equalsIgnoreCase(args[2])) {
                    operatorPasswordAlert(con, alertDays);
                }
            } else {
                _logger.info(METHOD_NAME, "Available choices are: P2PPIN, C2SPIN, CHNLPWD and OPTPWD");
            }
        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, " Error:" + e.getMessage());
            }

            _logger.errorTrace(METHOD_NAME, e);
        }// end catch
        finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                _logger.errorTrace(METHOD_NAME, e);

            }
            if (_logger.isDebugEnabled()) {
                _logger.info(METHOD_NAME, " Exiting Main Method ..................");
            }
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            ConfigServlet.destroyProcessCache();
        }// end finally
    }// end main

    /**
     * This method sends alerts to p2p users to change their PIN.
     * 
     * @param p_con
     *            Connection
     * @param p_alertDays
     *            int
     * @return void
     */
    private static void p2pPinAlert(Connection p_con, int p_alertDays) throws SQLException {
        final String METHOD_NAME = "p2pPinAlert";
        if (_logger.isDebugEnabled()) {
            _logger.info("p2pPinAlert", " Alert days:" + p_alertDays);
        }
        final Date currentDate = new Date();
        Locale locale = null;
        final int p2pChangePinDays = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DAYS_AFTER_CHANGE_PIN))).intValue();
        final String defaultLanguage = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE));
        final String defaultCountry = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
        String alertSleepTime = null;
        long lAlertSleepTime = 0;
        ArrayList arrayList = null;
        PinPasswordAlertVO alertVO = null;
        try {
            alertSleepTime = Constants.getProperty("ALERT_SLEEP_TIME");
            lAlertSleepTime = Integer.parseInt(alertSleepTime);
        } catch (Exception e) {
            lAlertSleepTime = 50;// in miliseconds
            _logger.errorTrace(METHOD_NAME, e);
        }
            
        PinPasswordAlertQry pinPwdQry= (PinPasswordAlertQry)ObjectProducer.getObject(QueryConstants.PIN_PASSWORD_ALERT_QRY, QueryConstants.QUERY_PRODUCER);
        
        final String query = pinPwdQry.p2pPinAlert(p_alertDays,p2pChangePinDays);
        
        if (_logger.isDebugEnabled()) {
            _logger.info("p2pPinAlert", " Query:" + query);
        }
        PreparedStatement pstmt = null;
        ResultSet rst = null;
        try {
            pstmt = p_con.prepareStatement(query);
            int i = 1;
            pstmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(currentDate));
           // pstmt.setInt(i++, p_alertDays);
          //  pstmt.setInt(i++, p2pChangePinDays);
           // pstmt.setInt(i++, p2pChangePinDays);
            pstmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(currentDate));
            rst = pstmt.executeQuery();
            if (rst.next()) {
                arrayList = new ArrayList();
                do {
                    alertVO = new PinPasswordAlertVO();
                    alertVO.setMsisdn(rst.getString("msisdn"));
                    alertVO.setLastModifiedOn(rst.getDate("pin_modified_on"));
                    try {
                        locale = new Locale(rst.getString("language"), rst.getString("country"));
                    } catch (Exception e) {
                        locale = new Locale(defaultLanguage, defaultCountry);
                        _logger.errorTrace(METHOD_NAME, e);
                    }
                    alertVO.setLocale(locale);
                    arrayList.add(alertVO);
                } while (rst.next());
            }
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e2) {
                    _logger.errorTrace(METHOD_NAME, e2);
                }
            }
            if (arrayList != null && arrayList.size() > 0) {
                updateAlertTable(p_con, "P2P", arrayList, "PIN", lAlertSleepTime, p2pChangePinDays);
            }
        }// end try
        catch (SQLException e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("p2pPinAlert", "Error =" + e.getMessage());
            }

            _logger.errorTrace(METHOD_NAME, e);
            throw e;
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e2) {
                    _logger.errorTrace(METHOD_NAME, e2);
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e3) {
                    _logger.errorTrace(METHOD_NAME, e3);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.info("p2pPinAlert", " Exiting :");
            }
        }
    }// end p2pPinAlert

    /**
     * This method sends alerts to c2s users to change their PIN.
     * 
     * @param p_con
     *            Connection
     * @param p_alertDays
     *            int
     * @return void
     * @throws SQLException 
     */
    private static void c2sPinAlert(Connection p_con, int p_alertDays) throws SQLException  {

        final String METHOD_NAME = "c2sPinAlert";
        if (_logger.isDebugEnabled()) {
            _logger.info("c2sPinAlert", " Alert days:" + p_alertDays);
        }
        final Date currentDate = new Date();
        Locale locale = null;
        int c2sChangePinDays = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DAYS_AFTER_CHANGE_PIN))).intValue();
        final String defaultLanguage = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE));
        final String defaultCountry = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
        String alertSleepTime = null;
        long lAlertSleepTime = 0;
        ArrayList arrayList = null;
        PinPasswordAlertVO alertVO = null;
        Date nextPinChangeDate = null;
        Date tempDate = null;
        try {
            alertSleepTime = Constants.getProperty("ALERT_SLEEP_TIME");
            lAlertSleepTime = Integer.parseInt(alertSleepTime);
        } catch (Exception e) {
            lAlertSleepTime = 50;// in miliseconds
            _logger.errorTrace(METHOD_NAME, e);
        }
        // query need to be changed
               // queryBuf.append(" AND ? + ? > trunc(nvl(UP.pin_modified_on,UP.created_on)) + ? AND trunc(nvl(UP.pin_modified_on,UP.created_on)) + ? > ?");
        PinPasswordAlertQry pinPwdQry= (PinPasswordAlertQry)ObjectProducer.getObject(QueryConstants.PIN_PASSWORD_ALERT_QRY, QueryConstants.QUERY_PRODUCER);

        final String query = pinPwdQry.c2sPinAlert();
        if (_logger.isDebugEnabled()) {
            _logger.info("c2sPinAlert", " Query:" + query);
        }
        PreparedStatement pstmt = null;
        ResultSet rst = null;
        try {
            pstmt = p_con.prepareStatement(query);
            /*
             * int i=1;
             * pstmt.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(currentDate));
             * pstmt.setInt(i++,p_alertDays);
             * pstmt.setInt(i++,c2sChangePinDays);
             * pstmt.setInt(i++,c2sChangePinDays);
             * pstmt.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(currentDate));
             */
            rst = pstmt.executeQuery();
            if (rst.next()) {
                arrayList = new ArrayList();
                do {
                    c2sChangePinDays = ((Integer) PreferenceCache.getControlPreference(PreferenceI.C2S_DAYS_AFTER_CHANGE_PIN, rst.getString("network_code"), rst
                        .getString("category_code"))).intValue();
                    p_alertDays = ((Integer) PreferenceCache.getControlPreference(PreferenceI.PIN_PASSWORD_ALERT_DAYS, rst.getString("network_code"), rst
                        .getString("category_code"))).intValue();
                    nextPinChangeDate = BTSLUtil.addDaysInUtilDate(rst.getDate("pin_modified_on"), c2sChangePinDays);
                    tempDate = BTSLUtil.addDaysInUtilDate(currentDate, p_alertDays);
                    if (tempDate.compareTo(nextPinChangeDate) >= 0 && nextPinChangeDate.after(currentDate)) {
                        alertVO = new PinPasswordAlertVO();
                        alertVO.setMsisdn(rst.getString("msisdn"));
                        alertVO.setLastModifiedOn(rst.getDate("pin_modified_on"));
                        try {
                            locale = new Locale(rst.getString("language"), rst.getString("country"));
                        } catch (Exception e) {
                            locale = new Locale(defaultLanguage, defaultCountry);
                            _logger.errorTrace(METHOD_NAME, e);
                        }
                        alertVO.setLocale(locale);
                        arrayList.add(alertVO);
                    }
                }// end while
                while (rst.next());
            }
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e2) {
                    _logger.errorTrace(METHOD_NAME, e2);

                }
            }
            if (arrayList != null && arrayList.size() > 0) {
                updateAlertTable(p_con, "C2S", arrayList, "PIN", lAlertSleepTime, c2sChangePinDays);
            }
        }// end try
        catch (SQLException e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("", "c2sPinAlert " + "  Error=" + e.getMessage());
            }

            _logger.errorTrace(METHOD_NAME, e);
            throw e;
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e2) {
                    _logger.errorTrace(METHOD_NAME, e2);
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e3) {
                    _logger.errorTrace(METHOD_NAME, e3);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.info("c2sPinAlert", " Exiting:");
            }
        }
    }// end c2sPinAlert

    /**
     * this method sends alerts to channel users to change their password.
     * 
     * @param p_con
     *            Connection
     * @param p_alertDays
     *            int
     * @return void
     */
    private static void channelPasswordAlert(Connection p_con, int p_alertDays) throws SQLException {
        final String METHOD_NAME = "channelPasswordAlert";
        if (_logger.isDebugEnabled()) {
            _logger.info("channelPasswordAlert", " Alert days:" + p_alertDays);
        }
        final Date currentDate = new Date();
        Locale locale = null;
        int channelChangePasswordDays = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DAYS_AFTER_CHANGE_PASSWORD))).intValue();
        final String defaultLanguage = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE));
        final String defaultCountry = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
        String alertSleepTime = null;
        long lAlertSleepTime = 0;
        ArrayList arrayList = null;
        PinPasswordAlertVO alertVO = null;
        Date nextPwdChangeDate = null;
        Date tempDate = null;
        try {
            alertSleepTime = Constants.getProperty("ALERT_SLEEP_TIME");
            lAlertSleepTime = Integer.parseInt(alertSleepTime);
        } catch (Exception e) {
            lAlertSleepTime = 50;// in miliseconds
            _logger.errorTrace(METHOD_NAME, e);
        }
               // queryBuf.append(" AND ? + ? > trunc(nvl(U.pswd_modified_on,U.created_on)) + ? AND trunc(nvl(U.pswd_modified_on,U.created_on)) + ? > ?");
       
        PinPasswordAlertQry pinPwdQry= (PinPasswordAlertQry)ObjectProducer.getObject(QueryConstants.PIN_PASSWORD_ALERT_QRY, QueryConstants.QUERY_PRODUCER);

        final String query = pinPwdQry.channelPasswordAlert();
        if (_logger.isDebugEnabled()) {
            _logger.info("channelPasswordAlert", " Query:" + query);
        }

        PreparedStatement pstmt = null;
        ResultSet rst = null;
        try {
            pstmt = p_con.prepareStatement(query);
            /*
             * int i=1;
             * pstmt.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(currentDate));
             * pstmt.setInt(i++,p_alertDays);
             * pstmt.setInt(i++,channelChangePasswordDays);
             * pstmt.setInt(i++,channelChangePasswordDays);
             * pstmt.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(currentDate));
             */
            rst = pstmt.executeQuery();
            if (rst.next()) {
                arrayList = new ArrayList();
                do {
                    channelChangePasswordDays = ((Integer) PreferenceCache.getControlPreference(PreferenceI.DAYS_AFTER_CHANGE_PASSWORD, rst.getString("network_code"), rst
                        .getString("category_code"))).intValue();
                    p_alertDays = ((Integer) PreferenceCache.getControlPreference(PreferenceI.PIN_PASSWORD_ALERT_DAYS, rst.getString("network_code"), rst
                        .getString("category_code"))).intValue();
                    nextPwdChangeDate = BTSLUtil.addDaysInUtilDate(rst.getDate("pswd_modified_on"), channelChangePasswordDays);
                    tempDate = BTSLUtil.addDaysInUtilDate(currentDate, p_alertDays);
                    if (tempDate.compareTo(nextPwdChangeDate) >= 0 && nextPwdChangeDate.after(currentDate)) {
                        alertVO = new PinPasswordAlertVO();
                        alertVO.setMsisdn(rst.getString("msisdn"));
                        alertVO.setLastModifiedOn(rst.getDate("pswd_modified_on"));
                        try {
                            locale = new Locale(rst.getString("language"), rst.getString("country"));
                        } catch (Exception e) {
                            locale = new Locale(defaultLanguage, defaultCountry);
                            _logger.errorTrace(METHOD_NAME, e);
                        }
                        alertVO.setLocale(locale);
                        arrayList.add(alertVO);
                    }
                }// end while
                while (rst.next());
            }
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e2) {
                    _logger.errorTrace(METHOD_NAME, e2);

                }
            }
            if (arrayList != null && arrayList.size() > 0) {
                updateAlertTable(p_con, "CHANNEL", arrayList, "PASSWORD", lAlertSleepTime, channelChangePasswordDays);
            }
        }// end try
        catch (SQLException e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("channelPasswordAlert", " Error=" + e.getMessage());
            }

            _logger.errorTrace(METHOD_NAME, e);
            throw e;
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e2) {
                    _logger.errorTrace(METHOD_NAME, e2);

                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e3) {
                    _logger.errorTrace(METHOD_NAME, e3);

                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.info("channelPasswordAlert", " Exiting:");
            }
        }
    }// end channelPasswordAlert

    /**
     * This method sends alerts to operators to change their password.
     * 
     * @param p_con
     *            Connection
     * @param p_alertDays
     *            int
     * @return void
     * @throws SQLException 
     */

    private static void operatorPasswordAlert(Connection p_con, int p_alertDays) throws SQLException  {
        final String METHOD_NAME = "operatorPasswordAlert";
        if (_logger.isDebugEnabled()) {
            _logger.info("operatorPasswordAlert", " Alert days:" + p_alertDays);
        }
        final Date currentDate = new Date();
        Locale locale = null;
        int operatorChangePasswordDays = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DAYS_AFTER_CHANGE_PASSWORD))).intValue();
        final String defaultLanguage = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE));
        final String defaultCountry = (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
        String alertSleepTime = null;
        long lAlertSleepTime = 0;
        ArrayList arrayList = null;
        PinPasswordAlertVO alertVO = null;
        Date nextPwdChangeDate = null;
        Date tempDate = null;
        try {
            alertSleepTime = Constants.getProperty("ALERT_SLEEP_TIME");
            lAlertSleepTime = Integer.parseInt(alertSleepTime);
        } catch (Exception e) {
            lAlertSleepTime = 50;// in miliseconds
            _logger.errorTrace(METHOD_NAME, e);
        }
                // queryBuf.append(" AND ? + ? > trunc(nvl(U.pswd_modified_on,U.created_on)) + ? AND trunc(nvl(U.pswd_modified_on,U.created_on)) + ? > ?");

        PinPasswordAlertQry pinPwdQry= (PinPasswordAlertQry)ObjectProducer.getObject(QueryConstants.PIN_PASSWORD_ALERT_QRY, QueryConstants.QUERY_PRODUCER);

        final String query = pinPwdQry.operatorPasswordAlert();
        if (_logger.isDebugEnabled()) {
            _logger.info("operatorPasswordAlert", " Query:" + query);
        }

        PreparedStatement pstmt = null;
        ResultSet rst = null;
        try {
            pstmt = p_con.prepareStatement(query);
            /*
             * int i=1;
             * pstmt.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(currentDate));
             * pstmt.setInt(i++,p_alertDays);
             * pstmt.setInt(i++,operatorChangePasswordDays);
             * pstmt.setInt(i++,operatorChangePasswordDays);
             * pstmt.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(currentDate));
             */
            rst = pstmt.executeQuery();
            if (rst.next()) {
                arrayList = new ArrayList();
                do {
                    operatorChangePasswordDays = ((Integer) PreferenceCache.getControlPreference(PreferenceI.DAYS_AFTER_CHANGE_PASSWORD, rst.getString("network_code"), rst
                        .getString("category_code"))).intValue();
                    p_alertDays = ((Integer) PreferenceCache.getControlPreference(PreferenceI.PIN_PASSWORD_ALERT_DAYS, rst.getString("network_code"), rst
                        .getString("category_code"))).intValue();
                    nextPwdChangeDate = BTSLUtil.addDaysInUtilDate(rst.getDate("pswd_modified_on"), operatorChangePasswordDays);
                    tempDate = BTSLUtil.addDaysInUtilDate(currentDate, p_alertDays);
                    if (tempDate.compareTo(nextPwdChangeDate) >= 0 && nextPwdChangeDate.after(currentDate)) {
                        alertVO = new PinPasswordAlertVO();
                        alertVO.setMsisdn(rst.getString("msisdn"));
                        alertVO.setLastModifiedOn(rst.getDate("pswd_modified_on"));
                        locale = new Locale(defaultLanguage, defaultCountry);
                        alertVO.setLocale(locale);
                        arrayList.add(alertVO);
                    }
                }// end while
                while (rst.next());
            }
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e2) {
                    _logger.errorTrace(METHOD_NAME, e2);
                }
            }
            if (arrayList != null && arrayList.size() > 0) {
                updateAlertTable(p_con, "OPERATOR", arrayList, "PASSWORD", lAlertSleepTime, operatorChangePasswordDays);
            }
        }// end try
        catch (SQLException e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("operatorPasswordAlert", "Error=" + e.getMessage());
            }

            _logger.errorTrace(METHOD_NAME, e);
            throw e;
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e2) {
                    _logger.errorTrace(METHOD_NAME, e2);

                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e3) {
                    _logger.errorTrace(METHOD_NAME, e3);

                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.info("operatorPasswordAlert", "Exiting:");
            }
        }
    }// end operatorPasswordAlert

    /**
     * This method enters information that for which mobile no,
     * which module and which type, which date alert is send on which date.
     * 
     * @param p_con
     *            Connection
     * @param p_moduleCode
     *            String
     * @param p_arrayList
     *            Arraylist
     * @param p_type
     *            String
     * @param p_sleepTime
     *            Long
     * @return void
     */

    private static void updateAlertTable(Connection p_con, String p_moduleCode, ArrayList p_arrayList, String p_type, long p_sleepTime, int p_changePinDays) {
        final String METHOD_NAME = "updateAlertTable";
        if (_logger.isDebugEnabled()) {
            _logger.info("updateAlertTable", " Module code:" + p_moduleCode + " p_arrayListSize:" + p_arrayList.size() + " type:" + p_type);
        }
        PreparedStatement insertAlertDatePstmt = null;
        int updateCount = 0;
        final Date tempDate = new Date();
        PinPasswordAlertVO alertVO = null;
        String msisdn = null;
        Locale locale = null;
        String arr[] = null;
        // int p2pChangePinDays=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DAYS_AFTER_CHANGE_PIN))).intValue();
        String message = null;
        final Date onlyDate = new Date(tempDate.getYear(), tempDate.getMonth(), tempDate.getDate());
        try {
            final StringBuffer updateStrBuff = new StringBuffer("INSERT INTO pin_password_alert VALUES ");
            updateStrBuff.append("(?,?,?,?,?)");
            insertAlertDatePstmt = p_con.prepareStatement(updateStrBuff.toString());

            String messageCode = PretupsErrorCodesI.PIN_ALERT_MSG;
            if ("PASSWORD".equals(p_type)) {
                messageCode = PretupsErrorCodesI.PSWD_ALERT_MSG;
            }

            for (int j = 0, listSize = p_arrayList.size(); j < listSize; j++) {
                alertVO = (PinPasswordAlertVO) p_arrayList.get(j);
                msisdn = alertVO.getMsisdn();
                locale = alertVO.getLocale();
                arr = new String[1];
                arr[0] = (BTSLUtil.getDateStringFromDate(BTSLUtil.addDaysInUtilDate(alertVO.getLastModifiedOn(), p_changePinDays)).toString());
                message = BTSLUtil.getMessage(locale, messageCode, arr);
                final PushMessage pushMessage = new PushMessage(msisdn, message, null, null, locale);
                pushMessage.push();
                final StringBuffer otherInfo = new StringBuffer();
                otherInfo.append(("[ALERT DATE = " + onlyDate + "] "));
                ProcessesLog.log(p_moduleCode + "-" + p_type + "-ALERT", msisdn, message, otherInfo);

                int i = 1;
                insertAlertDatePstmt.setString(i++, msisdn);
                insertAlertDatePstmt.setString(i++, p_moduleCode);
                insertAlertDatePstmt.setString(i++, p_type);
                insertAlertDatePstmt.setTimestamp(i++, BTSLUtil.getTimestampFromUtilDate(tempDate));
                insertAlertDatePstmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(onlyDate));
                updateCount = updateCount + insertAlertDatePstmt.executeUpdate();
                p_con.commit();
                insertAlertDatePstmt.clearParameters();
                try {
                    Thread.sleep(p_sleepTime);
                } catch (Exception exSleep) {
                    _logger.errorTrace(METHOD_NAME, exSleep);

                }
            }
        } catch (SQLException e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("updateAlertTable", " Exception while updating the pin_password_alert table =" + e.getMessage());
            }
            updateCount = 0;

            _logger.errorTrace(METHOD_NAME, e);
        } catch (ParseException e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("updateAlertTable", " Exception while updating the pin_password_alert table =" + e.getMessage());
            }
            updateCount = 0;

            _logger.errorTrace(METHOD_NAME, e);
        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("updateAlertTable", " Exception while updating the pin_password_alert table =" + e.getMessage());
            }
            updateCount = 0;

            _logger.errorTrace(METHOD_NAME, e);
        } finally {
            if (insertAlertDatePstmt != null) {
                try {
                    insertAlertDatePstmt.close();
                } catch (SQLException e3) {
                    _logger.errorTrace(METHOD_NAME, e3);

                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.info("updateAlertTable", " Exiting : ");
            }
        }
    }// end updateAlertTable
}// end class
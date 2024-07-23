package com.btsl.pretups.processes;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.ProcessesLog;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.OracleUtil;

/**
 * CorporateLowBalAlert.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Ankit Singhal 24/03/2006 Initial Creation
 * Ankit Singhal 10/04/2006 modiied
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Class for sending alerts to the channel users who does not have sufficient
 * balance for their scheduled recharges.
 */

public class CorporateLowBalAlert {
    private static Log _logger = LogFactory.getLog(CorporateLowBalAlert.class.getName());

    /**
     * ensures no instantiation
     */
    private CorporateLowBalAlert(){
    	
    }
    
    public static void main(String[] args) {
        final String METHOD_NAME = "main";
        try {
            final File constantsFile = Constants.validateFilePath(args[0]);// path of
            // constants.props
            // file
            if (!constantsFile.exists()) {
                System.out.println("CorporateLowBalAlert::" + "main:" + " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = Constants.validateFilePath(args[1]);// path of
            // logconfig.props
            // file
            if (!logconfigFile.exists()) {
                System.out.println("CorporateLowBalAlert::" + "main:" + " Logconfig File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        }// end try
        catch (Exception ex) {
            System.out.println("CorporateLowBalAlert::" + "main:" + " Error in Loading Configuration files ...........................: " + ex);
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CorporateLowBalAlert[main]", "", "", "",
                "Exception:" + ex.getMessage());
            ConfigServlet.destroyProcessCache();
            return;
        }// end catch

        Connection con = null;
        try {
            // Getting database connection
            con = OracleUtil.getSingleConnection();
            // method call balanceAlert to acquire information that to what
            // number alert should be send
            balanceAlert(con);
        }// end try
        catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.error("main", " " + e.getMessage());
            }
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CorporateLowBalAlert[main]", "", "", "",
                "Exception:" + e.getMessage());
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
                _logger.info("main", "Exiting");
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
     * This method finds the list of numbers to which alert is to be sent.
     * 
     * @param p_con
     *            Connection
     * @return boolean
     */
    public static boolean balanceAlert(Connection p_con) {
        final String METHOD_NAME = "balanceAlert";
        if (_logger.isDebugEnabled()) {
            _logger.info("balanceAlert", "Entered");
        }
        final HashMap userProductHashmap = new HashMap();
        String previousUserId = null;
        String previousMsisdn = null;
        PreparedStatement balancePstmt = null;
        ResultSet balanceRst = null;
        PreparedStatement amountPstmt = null;
        ResultSet amountRst = null;

        int tempVariable = 0;
        final long lAlertSleepTime = 50;
        final String arr[] = new String[3];

        String userId = null;
        String shortName = null;
        long amount = 0;
        String msisdn = null;
        long balance = 0;

        ArrayList arrList = new ArrayList();
        KeyArgumentVO keyArgumentVO = null;
        final Date currentDate = new Date();
        Date batchTillDate = new Date();
        Locale locale = null;

        try {
        	 String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
            batchTillDate = BTSLUtil.addDaysInUtilDate(currentDate, Integer.parseInt(Constants.getProperty("DATE_TILL_BATCH_TO_CONSIDER")));
            // query to get the available balance of a user product wise
            final StringBuffer queryBufBalance = new StringBuffer("SELECT UB.user_id user_id,P.short_name,(SUM(UB.balance) - TPP.min_residual_balance) balance ");
            queryBufBalance.append("FROM user_balances UB,users U,categories C,channel_users CU,transfer_profile_products TPP,products P ");
            queryBufBalance.append("WHERE U.user_id=UB.user_id AND U.user_id=CU.user_id AND P.product_code=UB.product_code ");
            queryBufBalance.append("AND U.category_code=C.category_code AND C.restricted_msisdns!='N' ");
            queryBufBalance.append("AND CU.transfer_profile_id=TPP.profile_id AND TPP.product_code=UB.product_code ");
            queryBufBalance.append("group BY UB.user_id,TPP.min_residual_balance,P.short_name ORDER BY UB.user_id,P.short_name");
            final String queryBalance = queryBufBalance.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug("balanceAlert", "Query:" + queryBalance);
            }
            balancePstmt = p_con.prepareStatement(queryBalance);
            balanceRst = balancePstmt.executeQuery();

            // query to get the scheduled amount of a user product wise
            StringBuilder queryBufAmount=new StringBuilder();
            if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){

           	 queryBufAmount = queryBufAmount.append("SELECT temp.user_id,temp.short_name,sum(temp.amount) amount, U.msisdn,U.country,UP.phone_language FROM ");
                queryBufAmount.append("(SELECT DISTINCT user_id,short_name,msisdn,amount FROM ");
                queryBufAmount.append("(SELECT CU.user_id user_id,getProduct(p.product_type,scd.amount::integer) short_name,SCD.amount,SCD.msisdn ");
                queryBufAmount.append("FROM scheduled_batch_detail SCD,scheduled_batch_master SCM, ");
                queryBufAmount.append("channel_users CU, users U,categories C,PRODUCTS p,PRODUCT_SERVICE_TYPE_MAPPING pstm ");
                queryBufAmount.append("WHERE SCD.batch_id=SCM.batch_id AND U.user_id=CU.USER_ID ");
                queryBufAmount.append("AND U.category_code=C.category_code AND C.RESTRICTED_MSISDNS!='N' ");
                queryBufAmount.append("AND CU.user_id=SCM.initiated_by AND scm.service_type=pstm.service_type ");
                queryBufAmount.append("AND SCD.status='S' AND PSTM.product_type=P.product_type AND PSTM.product_code=P.product_code AND SCM.scheduled_date<=?) AS Q) temp, users U,user_phones UP ");
                queryBufAmount.append("WHERE temp.user_id=U.user_id AND U.user_id=UP.user_id AND UP.primary_number='Y' ");
                queryBufAmount.append("GROUP BY temp.user_id,temp.short_name ,U.msisdn,U.country,UP.phone_language ORDER BY temp.user_id,temp.short_name ");
           
            	
            }
            else{
            	 queryBufAmount = queryBufAmount.append("SELECT temp.user_id,temp.short_name,sum(temp.amount) amount, U.msisdn,U.country,UP.phone_language FROM ");
                 queryBufAmount.append("(SELECT DISTINCT user_id,short_name,msisdn,amount FROM ");
                 queryBufAmount.append("(SELECT CU.user_id user_id,getProduct(p.product_type,scd.amount) short_name,SCD.amount,SCD.msisdn ");
                 queryBufAmount.append("FROM scheduled_batch_detail SCD,scheduled_batch_master SCM, ");
                 queryBufAmount.append("channel_users CU, users U,categories C,PRODUCTS p,PRODUCT_SERVICE_TYPE_MAPPING pstm ");
                 queryBufAmount.append("WHERE SCD.batch_id=SCM.batch_id AND U.user_id=CU.USER_ID ");
                 queryBufAmount.append("AND U.category_code=C.category_code AND C.RESTRICTED_MSISDNS!='N' ");
                 queryBufAmount.append("AND CU.user_id=SCM.initiated_by AND scm.service_type=pstm.service_type ");
                 queryBufAmount.append("AND SCD.status='S' AND PSTM.product_type=P.product_type AND PSTM.product_code=P.product_code AND SCM.scheduled_date<=?)) temp, users U,user_phones UP ");
                 queryBufAmount.append("WHERE temp.user_id=U.user_id AND U.user_id=UP.user_id AND UP.primary_number='Y' ");
                 queryBufAmount.append("GROUP BY temp.user_id,temp.short_name ,U.msisdn,U.country,UP.phone_language ORDER BY temp.user_id,temp.short_name ");
            }
           
            final String queryAmount = queryBufAmount.toString();

            if (_logger.isDebugEnabled()) {
                _logger.debug("balanceAlert", "Query:" + queryAmount);
            }
            amountPstmt = p_con.prepareStatement(queryAmount);
            amountPstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(batchTillDate));
            amountRst = amountPstmt.executeQuery();

            while (balanceRst.next()) {
                userProductHashmap.put(balanceRst.getString("user_id") + ":" + balanceRst.getString("short_name"), PretupsBL.getDisplayAmount(balanceRst.getLong("balance"))
                    .toString());
            }

            try {
                if (amountRst.next()) {
                    do {
                        userId = amountRst.getString("user_id");
                        shortName = amountRst.getString("short_name");
                        amount = (Long.valueOf(PretupsBL.getDisplayAmount(amountRst.getLong("amount")))).longValue();
                        msisdn = amountRst.getString("msisdn");

                        // set the user balance as zero if no data found or
                        // negative balance found as we are getting
                        // balance-residual balance in balance field
                        if ((String) userProductHashmap.get(userId + ":" + shortName) != null) {
                            balance = (Long.valueOf((String) userProductHashmap.get(userId + ":" + shortName))).longValue();
                            if (balance < 0) {
                                balance = 0;
                            }
                        } else {
                            balance = 0;
                        }

                        if (tempVariable != 0) {
                            if (!previousUserId.equalsIgnoreCase(userId)) {
                                if (arrList.size() >= 1) {
                                    try {
                                        locale = new Locale(amountRst.getString("phone_language"), amountRst.getString("country"));
                                    } catch (Exception e) {
                                        locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                                        _logger.errorTrace(METHOD_NAME, e);
                                    }
                                    final String array = BTSLUtil.getMessage(locale, arrList);
                                    arr[0] = array;
                                    final String senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.CORP_LOW_BALANCE_ALERT_MSG, arr);
                                    final PushMessage pushMessage = new PushMessage(previousMsisdn, senderMessage, null, null, locale);
                                    pushMessage.push();
                                    final StringBuffer otherInfo = new StringBuffer("[PRODUCT(S) WITH THEIR BALANCE(S) AND AMOUNT(S)= " + array + "] ");
                                    ProcessesLog.log("CORPORATE-LOW-BALANCE-ALERT", previousMsisdn, senderMessage, otherInfo);

                                    arrList = new ArrayList();
                                    try {
                                        Thread.sleep(lAlertSleepTime);
                                    } catch (Exception exSleep) {
                                        _logger.errorTrace(METHOD_NAME, exSleep);
                                    }
                                }
                            }
                            if (balance - amount < 0) {
                                keyArgumentVO = new KeyArgumentVO();
                                keyArgumentVO.setKey(PretupsErrorCodesI.CORP_LOW_BALANCE_ALERT_MSG_SUBKEY);
                                arr[0] = shortName;
                                arr[1] = String.valueOf(balance);
                                arr[2] = String.valueOf(amount);
                                keyArgumentVO.setArguments(arr);
                                arrList.add(keyArgumentVO);
                                previousUserId = userId;
                                previousMsisdn = msisdn;
                            }
                        } else {
                            if (balance - amount < 0) {
                                keyArgumentVO = new KeyArgumentVO();
                                keyArgumentVO.setKey(PretupsErrorCodesI.CORP_LOW_BALANCE_ALERT_MSG_SUBKEY);
                                arr[0] = shortName;
                                arr[1] = String.valueOf(balance);
                                arr[2] = String.valueOf(amount);
                                keyArgumentVO.setArguments(arr);
                                tempVariable = 1;
                                arrList.add(keyArgumentVO);
                                previousUserId = userId;
                                previousMsisdn = msisdn;
                            }
                        }
                    } while (amountRst.next());
                    if (arrList.size() >= 1) {
                        try {
                            locale = new Locale(amountRst.getString("phone_language"), amountRst.getString("country"));
                        } catch (Exception e) {
                            locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                            _logger.errorTrace(METHOD_NAME, e);
                        }
                        final String array = BTSLUtil.getMessage(locale, arrList);
                        arr[0] = array;
                        final String senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.CORP_LOW_BALANCE_ALERT_MSG, arr);
                        final PushMessage pushMessage = new PushMessage(previousMsisdn, senderMessage, null, null, locale);
                        pushMessage.push();
                        final StringBuffer otherInfo = new StringBuffer("[PRODUCT(S) WITH THEIR BALANCE(S) AND AMOUNT(S)= " + array + "] ");
                        ProcessesLog.log("CORPORATE-LOW-BALANCE-ALERT", previousMsisdn, senderMessage, otherInfo);
                    }
                }
            } catch (SQLException e) {
                if (_logger.isDebugEnabled()) {
                    _logger.error("balanceAlert", "Error:" + e.getMessage());
                }
                _logger.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CorporateLowBalAlert[balanceAlert]", "", "", "",
                    "Exception:" + e.getMessage());
                // throw e;
            } catch (Exception e) {
                if (_logger.isDebugEnabled()) {
                    _logger.error("balanceAlert", "Error:" + e.getMessage());
                }
                _logger.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CorporateLowBalAlert[balanceAlert]", "", "", "",
                    "Exception:" + e.getMessage());
                // throw e;
            }
            try {
                p_con.commit();
            } catch (SQLException e) {
                _logger.errorTrace(METHOD_NAME, e);
                p_con.rollback();
            }
            return true;
        }// end try
        catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.error("balanceAlert", "Error:" + e.getMessage());
            }
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CorporateLowBalAlert[balanceAlert]", "", "", "",
                "Exception:" + e.getMessage());
            return false;
        }// end catch
        finally {
            if (balanceRst != null) {
                try {
                    balanceRst.close();
                } catch (SQLException e2) {
                    _logger.errorTrace(METHOD_NAME, e2);
                }
            }
            if (balancePstmt != null) {
                try {
                    balancePstmt.close();
                } catch (SQLException e3) {
                    _logger.errorTrace(METHOD_NAME, e3);
                }
            }
            if (amountRst != null) {
                try {
                    amountRst.close();
                } catch (SQLException e2) {
                    _logger.errorTrace(METHOD_NAME, e2);
                }
            }
            if (amountPstmt != null) {
                try {
                    amountPstmt.close();
                } catch (SQLException e3) {
                    _logger.errorTrace(METHOD_NAME, e3);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.info("balanceAlert", " Exiting");
            }
        }// end finally
    }
}// end class
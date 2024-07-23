package com.btsl.pretups.processes;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.OracleUtil;

/**
 * @(#)AmbiguousCaseAlarm.java
 *                             Copyright(c) 2006, Bharti Telesoft Ltd.
 *                             All Rights Reserved
 * 
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Ankit Singhal 11/07/2006 Initial Creation
 */
public class AmbiguousCaseAlarm {
    // This class is used to send alarms to the admin mobile numbers if there
    // are ambiguous cases
    // in the transaction tables for a specific number of days before the crrent
    // date.
    private static final Log LOGGER = LogFactory.getLog(AmbiguousCaseAlarm.class.getName());

    /**
     * ensures no instantiation
     */
    private AmbiguousCaseAlarm(){
    	
    }
    
    public static void main(String arg[]) {
        final String METHOD_NAME = "main";
        try {
            if (arg.length != 2) {
                System.out.println("Usage : AmbiguousCaseAlarm [Constants file] [ProcessLogConfig file]");
                return;
            }
            final File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
                System.out.println("AmbiguousCaseAlarm" + " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                System.out.println("AmbiguousCaseAlarm" + " ProcessLogconfig File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        }// end of try
        catch (Exception e) {
            LOGGER.error(METHOD_NAME, "Error in Loading Configuration files ...........................: " + e);
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {
            // This method is called to make all the processing for sending
            // alarms.
            // This method can be called from anywhere.
            process();
        } catch (BTSLBaseException be) {
            LOGGER.error("main", "BTSLBaseException : " + be.getMessage());
            LOGGER.errorTrace(METHOD_NAME, be);
            return;
        } finally {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("main", "Exiting..... ");
            }
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                LOGGER.errorTrace(METHOD_NAME, e);
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    /*
     * This method loads the enabled modules and calls the methods accordingly.
     */
    private static void process() throws BTSLBaseException {
        final String METHOD_NAME = "process";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("process", "Entered..... ");
        }
        PreparedStatement selectModulePstmt = null;
        ResultSet selectModuleRst = null;
        Connection con = null;
        String query = null;
        try {
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("process", " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AmbiguousCaseAlarm[process]", "",
                    "", "", "DATABASE Connection is NULL");
                return;
            }
            query = new String("SELECT lookup_code FROM LOOKUPS WHERE status=? AND lookup_type=?");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info("process", " Query:" + query);
            }

            selectModulePstmt = con.prepareStatement(query);
            selectModulePstmt.setString(1, PretupsI.YES);
            selectModulePstmt.setString(2, PretupsI.MODULE_TYPE);
            selectModuleRst = selectModulePstmt.executeQuery();

            // c2s and or p2p processes are called on the basis of availiability
            // of modules
            // if both are enabled, both are called else process for enabled
            // module is called.
            while (selectModuleRst.next()) {
                final String module = selectModuleRst.getString("lookup_code");
                if (PretupsI.C2S_MODULE.equalsIgnoreCase(module)) {
                    c2sAlarm(con);
                } else if (PretupsI.P2P_MODULE.equalsIgnoreCase(module)) {
                    p2pAlarm(con);
                }
            }
        } catch (BTSLBaseException be) {
            LOGGER.error("main", "BTSLBaseException : " + be.getMessage());
            LOGGER.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            LOGGER.error("main", "BTSLBaseException : " + e.getMessage());
            LOGGER.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("AmbiguousCaseAlarm", "process", PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (selectModuleRst != null) {
                try {
                    selectModuleRst.close();
                } catch (Exception ex) {
                    LOGGER.errorTrace(METHOD_NAME, ex);
                }
            }
            if (selectModulePstmt != null) {
                try {
                    selectModulePstmt.close();
                } catch (Exception ex) {
                    LOGGER.errorTrace(METHOD_NAME, ex);
                }
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("process", "Exception in closing connection ");
                }
                LOGGER.errorTrace(METHOD_NAME, ex);
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info("process", "Exiting..... ");
            }
        }
    }

    /*
     * This method sends alarm if ambiguous cases are found in subscriber
     * transactions.
     */
    private static void p2pAlarm(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "p2pAlarm";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("p2pAlarm", "Entered..... ");
        }
        PreparedStatement selectPstmt = null;
        ResultSet selectRst = null;
        KeyArgumentVO keyArgumentVO = null;
        final ArrayList arrList = new ArrayList();
        int p2pCount = 0;
        try {
            final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            final StringBuffer queryBuff = new StringBuffer("SELECT transfer_date ,COUNT(transfer_date) COUNT FROM SUBSCRIBER_TRANSFERS");
            queryBuff.append(" WHERE transfer_status=? AND transfer_date>=? AND transfer_date<=? GROUP BY transfer_date");
            final String query = queryBuff.toString();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info("p2pAlarm", " Query:" + query);
            }

            Date fromTransferDate = new Date();
            final Date toTransferDate = new Date();
            fromTransferDate = BTSLUtil.addDaysInUtilDate(fromTransferDate, -2);
            // subtracting 2 from transfer date as transaction before a fix
            // number of days are to be considered
            selectPstmt = p_con.prepareStatement(query);
            selectPstmt.setString(1, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            selectPstmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(fromTransferDate));
            selectPstmt.setDate(3, BTSLUtil.getSQLDateFromUtilDate(toTransferDate));
            selectRst = selectPstmt.executeQuery();
            if (selectRst.next()) {
                do {
                    keyArgumentVO = new KeyArgumentVO();
                    keyArgumentVO.setKey(PretupsErrorCodesI.P2P_AMBIGUOUS_CASE_ALERT_MSG_SUBKEY);
                    final String arr[] = { (BTSLUtil.getSQLDateFromUtilDate(selectRst.getDate("transfer_date"))).toString(), selectRst.getString("count") };
                    p2pCount += selectRst.getInt("count");
                    keyArgumentVO.setArguments(arr);
                    arrList.add(keyArgumentVO);
                } while (selectRst.next());
            }
            // message alert will be sent only if ambiguous cases are found
            // otherwise alarm will be generated
            if (arrList.size() > 0) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "AmbiguousCaseAlarm[p2pAlarm]", "", "", "",
                    "(" + p2pCount + ") P2P ambiguous transaction found between date:" + BTSLUtil.getSQLDateFromUtilDate(fromTransferDate) + " and " + BTSLUtil
                        .getSQLDateFromUtilDate(toTransferDate));
                final String array = BTSLUtil.getMessage(locale, arrList);
                final String arr[] = new String[1];
                arr[0] = array;
                final String senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.P2P_AMBIGUOUS_CASE_ALERT_MSG, arr);
                final String msisdnString = new String(Constants.getProperty("adminmobile"));
                final String[] msisdn = msisdnString.split(",");

                for (int i = 0; i < msisdn.length; i++) {
                    final PushMessage pushMessage = new PushMessage(msisdn[i], senderMessage, null, null, locale);
                    pushMessage.push();
                }
            } else {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "AmbiguousCaseAlarm[p2pAlarm]", "", "", "",
                    "" + "No P2P ambiguous transaction found till date:" + BTSLUtil.getSQLDateFromUtilDate(toTransferDate));
            }
        } catch (Exception e) {
            LOGGER.error("p2pAlarm", "Exception : " + e.getMessage());
            LOGGER.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AmbiguousCaseAlarm[p2pAlarm]", "", "", "",
                "Exception while ruuning AmbiguousCaseAlarm, getting :" + e.getMessage());
            throw new BTSLBaseException("AmbiguousCaseAlarm", "p2pAlarm", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (selectRst != null) {
                try {
                    selectRst.close();
                } catch (Exception ex) {
                    LOGGER.errorTrace(METHOD_NAME, ex);
                }
            }
            if (selectPstmt != null) {
                try {
                    selectPstmt.close();
                } catch (Exception ex) {
                    LOGGER.errorTrace(METHOD_NAME, ex);
                }
            }
        }
    }

    /*
     * This method sends alarm if ambiguous cases are found in channel
     * transactions.
     */
    private static void c2sAlarm(Connection p_con) throws BTSLBaseException {
    	//local_index_implemented
        final String METHOD_NAME = "c2sAlarm";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("c2sAlarm", "Entered... ");
        }
        PreparedStatement selectPstmt = null;
        ResultSet selectRst = null;
        KeyArgumentVO keyArgumentVO = null;
        final ArrayList arrList = new ArrayList();
        int c2sCount = 0;
        try {
            final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            final StringBuffer queryBuff = new StringBuffer("SELECT transfer_date ,COUNT(transfer_date) COUNT FROM C2S_TRANSFERS");
            queryBuff.append(" WHERE transfer_date>=? AND transfer_date<=? AND transfer_status=? GROUP BY transfer_date");
            final String query = queryBuff.toString();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info("process", " Query:" + query);
            }

            Date fromTansferDate = new Date();
            final Date toTansferDate = new Date();
            fromTansferDate = BTSLUtil.addDaysInUtilDate(fromTansferDate, -2);
            // subtracting 2 from transfer date as transaction before a fix
            // number of days are to be considered
            selectPstmt = p_con.prepareStatement(query);
            selectPstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(fromTansferDate));
            selectPstmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(toTansferDate));
            selectPstmt.setString(3, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            selectRst = selectPstmt.executeQuery();
            if (selectRst.next()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("c2sAlarm", "under if");
                }
                do {
                    keyArgumentVO = new KeyArgumentVO();
                    keyArgumentVO.setKey(PretupsErrorCodesI.C2S_AMBIGUOUS_CASE_ALERT_MSG_SUBKEY);
                    final String arr[] = { (BTSLUtil.getSQLDateFromUtilDate(selectRst.getDate("transfer_date"))).toString(), selectRst.getString("count") };
                    c2sCount += selectRst.getInt("count");
                    keyArgumentVO.setArguments(arr);
                    arrList.add(keyArgumentVO);
                } while (selectRst.next());
            }
            // message alert will be sent only if ambiguous cases are found
            // otherwise alarm will be generated
            if (arrList.size() > 0) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("c2sAlarm", "Ambigous Case found, No. of cases=" + arrList.size());
                }
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "AmbiguousCaseAlarm[c2sAlarm]", "", "", "",
                    "(" + c2sCount + ") C2S ambiguous transaction found between date:" + BTSLUtil.getSQLDateFromUtilDate(fromTansferDate) + " and " + BTSLUtil
                        .getSQLDateFromUtilDate(toTansferDate));
                final String array = BTSLUtil.getMessage(locale, arrList);
                final String arr[] = new String[1];
                arr[0] = array;
                final String senderMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.C2S_AMBIGUOUS_CASE_ALERT_MSG, arr);
                final String msisdnString = new String(Constants.getProperty("adminmobile"));
                final String[] msisdn = msisdnString.split(",");

                for (int i = 0; i < msisdn.length; i++) {
                    final PushMessage pushMessage = new PushMessage(msisdn[i], senderMessage, null, null, locale);
                    pushMessage.push();
                }
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("c2sAlarm", "under else");
                }
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "AmbiguousCaseAlarm[c2sAlarm]", "", "", "",
                    "" + "No C2S ambiguous transaction found till date:" + BTSLUtil.getSQLDateFromUtilDate(toTansferDate));
            }
        } catch (Exception e) {
            LOGGER.error("c2sAlarm", "Exception : " + e.getMessage());
            LOGGER.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AmbiguousCaseAlarm[c2sAlarm]", "", "", "",
                "Exception while running AmbiguousCaseAlarm, getting :" + e.getMessage());
            throw new BTSLBaseException("AmbiguousCaseAlarm", "c2sAlarm", PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
            if (selectRst != null) {
                try {
                    selectRst.close();
                } catch (Exception ex) {
                    LOGGER.errorTrace(METHOD_NAME, ex);
                }
            }
            if (selectPstmt != null) {
                try {
                    selectPstmt.close();
                } catch (Exception ex) {
                    LOGGER.errorTrace(METHOD_NAME, ex);
                }
            }
        }
    }
}

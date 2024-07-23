package com.btsl.pretups.processes;

/**
 * @(#)LMSReconcilliationProcess.java
 * 
 * 
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Author Date History
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Pradyumn Mishra 06/12/2013 Initial
 *                                    Creation
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 */

import java.io.File;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
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
import com.btsl.pretups.loyalty.transaction.LoyaltyDAO;
import com.btsl.pretups.loyalty.transaction.LoyaltyVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.ibm.icu.util.Calendar;

public class LMSReconcilliationProcess {
    private static ProcessStatusVO _processStatusVO;
    private static ProcessBL _processBL = null;
    private static HashMap _csvMap = new HashMap();
    private static Properties _csvproperties = new Properties();

    // C:\\jakarta-tomcat-5.5.6\\jakarta-tomcat-5.5.7\\webapps\\nigeria\\WEB-INF\\classes\\configfiles\\Constants.props
    // C:\\jakarta-tomcat-5.5.6\\jakarta-tomcat-5.5.7\\webapps\\nigeria\\WEB-INF\\classes\\configfiles\\LogConfig.props
    // C:\\jakarta-tomcat-5.5.6\\jakarta-tomcat-5.5.7\\webapps\\nigeria\\WEB-INF\\classes\\configfiles\\csvConfigFile.props
    private static Log _logger = LogFactory.getLog(LMSExpiryPointsReturn.class.getName());

    /**
     * ensures no instantiation
     */
    private LMSReconcilliationProcess(){
    	
    }
    public static void main(String arg[]) {
        final String METHOD_NAME = "main";
        try {

            if (arg.length != 2) {
                System.out.println("Usage : LMSReconcilliationProcess [Constants file] [LogConfig file] ");
                return;
            }

            final File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
                System.out.println("LMSReconcilliationProcess" + " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                System.out.println("LMSReconcilliationProcess" + " Logconfig File Not Found .............");
                return;
            }

            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());

        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("main", " Error in Loading Files ...........................: " + e.getMessage());
            }
            _logger.errorTrace(METHOD_NAME, e);
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            process();
        } catch (BTSLBaseException be) {
            _logger.error("main", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("main", "Exiting..... ");
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    private static void process() throws BTSLBaseException {
        Date processedUpto = null;
        Date currentDateTime = new Date();
        Connection con = null;
        String processId = null;
        boolean statusOk = false;
        final String METHOD_NAME = "process";
        try {
            String format = null;

            _logger.debug("process", "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            final Calendar cal = BTSLDateUtil.getInstance();
            currentDateTime = cal.getTime(); // Current Date
            loadConstantParameters();

            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("process", " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LMSExpiryPointsReturn[process]", "",
                    "", "", "DATABASE Connection is NULL");
                return;
            }
            processId = "LMSRecon";
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(con, processId);
            statusOk = _processStatusVO.isStatusOkBool();
            if (statusOk) {
                con.commit();
                processedUpto = _processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    LoyaltyVO loyaltyVO = null;
                    LoyaltyDAO loyaltyDAO = null;
                    loyaltyDAO = new LoyaltyDAO();
                    loyaltyVO = new LoyaltyVO();
                    format = PretupsI.DATE_FORMAT;
                    final String adminMobile;
                    String message;
                    Locale locale = null;
                    try {
                        loyaltyDAO.getLoyalityPointsUsedStatus(con, loyaltyVO);

                        locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                        final String msisdnString = new String(Constants.getProperty("adminmobile"));
                        final String[] msisdn = msisdnString.split(",");

                        // /
                        final String arr[] = { String.valueOf(loyaltyVO.getLoyalityPointsInitiated()), String.valueOf(loyaltyVO.getLoyalityPointsStock()), String
                            .valueOf(loyaltyVO.getLoyalityPointsExpired()), String.valueOf(loyaltyVO.getLoyalityPointsRedempted()), String.valueOf(loyaltyVO
                            .getLoyalityPointsUsers()) };
                        message = BTSLUtil.getMessage(locale, PretupsErrorCodesI.LOYALTY_RECON_MESSAGE, arr);
                        if (!(loyaltyVO.getLoyalityPointsInitiated() == (loyaltyVO.getLoyalityPointsStock() + loyaltyVO.getLoyalityPointsExpired() + loyaltyVO
                            .getLoyalityPointsRedempted() + loyaltyVO.getLoyalityPointsUsers()))) {
                            for (int i = 0; i < msisdn.length; i++) {
                                final PushMessage pushMessage = new PushMessage(msisdn[i], message, null, null, locale);
                                pushMessage.push();
                            }
                        } else {
                            message = "Reconciliation Process Executed successfully ";
                            for (int i = 0; i < msisdn.length; i++) {
                                final PushMessage pushMessage = new PushMessage(msisdn[i], message, null, null, locale);
                                pushMessage.push();
                            }
                            _processStatusVO.setExecutedUpto(currentDateTime);
                        }
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("Stock initiated=", "" + loyaltyVO.getLoyalityPointsInitiated());
                        }
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("Current loyality stock + Expired Loyality points + Redemeed points + Points Rewarded to users=", "" + loyaltyVO
                                .getLoyalityPointsStock() + "+" + loyaltyVO.getLoyalityPointsExpired() + "+" + loyaltyVO.getLoyalityPointsRedempted() + "+" + loyaltyVO
                                .getLoyalityPointsUsers());
                        }
                    } catch (Exception e) {
                        _logger.error("process", "loyaltyVO=" + loyaltyVO.toString() + "  Exception : " + e.getMessage());
                        _logger.errorTrace(METHOD_NAME, e);
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LMSReconcilliationProcess[process]", "",
                            "", "", "csvfilevo=" + loyaltyVO.toString() + " Exception =" + e.getMessage());
                    }

                    _processStatusVO.setExecutedOn(currentDateTime);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LMSReconcilliationProcess[process]", "", "",
                        "", " LMSReconcilliationProcess process has been executed successfully.");
                } else {
                    throw new BTSLBaseException("LMSReconcilliationProcess", "process", PretupsErrorCodesI.DWH_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
                }
            }
        } catch (BTSLBaseException be) {
            _logger.error("process", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _logger.error("process", "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LMSReconcilliationProcess[process]", "", "", "",
                " Exception =" + e.getMessage());
            throw new BTSLBaseException("LMSReconcilliationProcess", "process", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            // if the status was marked as under process by this method call,
            // only then it is marked as complete on termination
            if (statusOk) {
                try {
                    if (markProcessStatusAsComplete(con, processId) == 1) {
                        try {
                            con.commit();
                        } catch (Exception e) {
                            _logger.errorTrace(METHOD_NAME, e);
                        }
                    } else {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            _logger.errorTrace(METHOD_NAME, e);
                        }
                    }
                } catch (Exception e) {
                    _logger.errorTrace(METHOD_NAME, e);
                }
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (Exception ex) {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("process", "Exception closing connection ");
                    }
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            _logger.debug("process", "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            if (_logger.isDebugEnabled()) {
                _logger.debug("process", "Exiting..... ");
            }
        }
    }

    private static void loadConstantParameters() throws BTSLBaseException {
        final String METHOD_NAME = "loadConstantParameters";
        if (_logger.isDebugEnabled()) {
            _logger.debug("loadParameters", " Entered: ");
        }
        try {

            // _logger.debug("loadConstantParameters"," Required information successfuly loaded from csvConfigFile.properties...............: ");
        }

        catch (Exception e) {
            _logger.error("loadConstantParameters", "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            final BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LMSReconcilliationProcess[loadConstantParameters]",
                "", "", "", "Message:" + btslMessage);
            throw new BTSLBaseException("LMSReconcilliationProcess", "loadConstantParameters", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }

    }

    private static int markProcessStatusAsComplete(Connection p_con, String p_processId) throws BTSLBaseException {
        final String METHOD_NAME = "markProcessStatusAsComplete";
        if (_logger.isDebugEnabled()) {
            _logger.debug("markProcessStatusAsComplete", " Entered:  p_processId:" + p_processId);
        }
        int updateCount = 0;
        final Date currentDate = new Date();
        final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        _processStatusVO.setProcessID(p_processId);
        _processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        _processStatusVO.setStartDate(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetail(p_con, _processStatusVO);
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("markProcessStatusAsComplete", "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "LMSReconcilliationProcess[markProcessStatusAsComplete]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("LMSReconcilliationProcess", "markProcessStatusAsComplete", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;

    }

}

package com.btsl.pretups.processes;

/**
 * @(#)SOSSettlement
 *                   Copyright(c) 2009, Bharti Telesoft Ltd.
 *                   All Rights Reserved
 * 
 *                   ----------------------------------------------------------
 *                   ---------------------------------------
 *                   Author Date History
 *                   ----------------------------------------------------------
 *                   ---------------------------------------
 *                   Shamit Jain 8/12/2009 Initial Creation
 * 
 *                   This File is used to collect the infor ation of the user
 *                   daily balance and stor it in the CSV file.
 */

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.logging.SOSSettlementRequestLog;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.network.businesslogic.MSISDNPrefixInterfaceMappingCache;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.payment.businesslogic.ServicePaymentMappingCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.routing.master.businesslogic.InterfaceRoutingControlCache;
import com.btsl.pretups.routing.master.businesslogic.ServiceInterfaceRoutingCache;
import com.btsl.pretups.routing.master.businesslogic.SubscriberRoutingControlCache;
import com.btsl.pretups.sos.businesslogic.SOSVO;
import com.btsl.pretups.sos.requesthandler.SOSSettlementController;
import com.btsl.pretups.vastrix.businesslogic.ServiceSelectorInterfaceMappingCache;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.txn.pretups.sos.businesslogic.SOSTxnDAO;

public class SOSSettlement {
    private static final Log _log = LogFactory.getLog(SOSSettlement.class.getName());
    private static ProcessStatusVO _processStatusVO;
    private static ProcessBL _processBL = null;
    private static final Log _logger = LogFactory.getLog(SOSSettlement.class.getName());
    private static SOSVO _sosvoProcessdeatil = null;


    /**
     * to ensure no class instantiation 
     */
    private SOSSettlement(){
    	
    }
    public static void main(String arg[]) {
        final String METHOD_NAME = "main";
        try {
            _log.debug(METHOD_NAME, arg.length);
            if (arg.length != 2) {
                _log.debug(METHOD_NAME, "Usage : SOSSettlement [Constants file] [LogConfig file]");
                return;
            }
            _log.debug(METHOD_NAME, arg[0]);
            final File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
                _log.info(METHOD_NAME, "SOSSettlement" + " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                _log.info(METHOD_NAME, "SOSSettlement" + " Logconfig File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            _log.info(METHOD_NAME, "SOSSettlement After loading NetworkInterfaceModuleCache before loading ServicePaymentMappingCache ");
            ServicePaymentMappingCache.loadServicePaymentMappingOnStartUp();
            _log.info(METHOD_NAME, "SOSSettlement After loading MSISDNPrefixInterfaceMappingCache before NetworkInterfaceModuleCache ");
            NetworkInterfaceModuleCache.loadNetworkInterfaceModuleAtStartup();
            _log.info(METHOD_NAME, "SOSSettlement After Loading Registeration Contol Cache ");
            ServiceInterfaceRoutingCache.refreshServiceInterfaceRouting();
            _log.info(METHOD_NAME, "SOSSettlement After loading Preferences before loading Network prefixes ");
            NetworkPrefixCache.loadNetworkPrefixesAtStartup();
            _log.info(METHOD_NAME, "SOSSettlement After loading Transaction Load Before Loading Subscriber Routing Control Cache");
            SubscriberRoutingControlCache.refreshSubscriberRoutingControl();
            _log.info(METHOD_NAME, "SOSSettlement After loading SimProfileCache before MSISDNPrefixInterfaceMappingCache ");
            MSISDNPrefixInterfaceMappingCache.loadPrefixInterfaceMappingAtStartup();
            _log.info(METHOD_NAME, "SOSSettlement After loading RequestInterfaceCache ");
            FileCache.loadAtStartUp();
            _log.info(METHOD_NAME, "SOSSettlement After loading NodeServlet ");

            _log.info(METHOD_NAME, "SOSSettlement After Loading Subscriber Routing Control Cache Before Registeration Contol Cache ");
            InterfaceRoutingControlCache.refreshInterfaceRoutingControl();
            _log.info(METHOD_NAME, "SOSSettlement After Loading Subscriber Routing Control Cache Before Registeration Contol Cache ");
            ServiceSelectorMappingCache.loadServiceSelectorMappingCacheOnStartUp();
            _log.info(METHOD_NAME, "SOSSettlement After Loading Service Selector mapping cache ");
            ServiceSelectorInterfaceMappingCache.loadServSelInterfMappingOnStartup();
            
            
        }// end of try
        catch (Exception e) {
            _logger.error(METHOD_NAME, "Error in Loading Configuration files ...........................: " + e);
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {
            process();
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... ");
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * Description :-
     * Author :-
     * Method :-process
     * 
     * @throws BTSLBaseException
     *             Return :-void
     *             Sep 24, 2010 12:14:41 PM
     */
    private static void process() throws BTSLBaseException {
        final String METHOD_NAME = "process";
        Date processedUpto = null;
        Date executeTilllDate = null;
        Date dateCount = null;
        Date currentDate = new Date();
        Connection con = null;
        String processId = null;
        boolean statusOk = false;
        ProcessStatusDAO processStatusDAO = null;
        int beforeInterval = 0;
        Date dateForSOS_SettlementData = null; // this date compare in the
        // recharge date of the SOS table

        try {
            _logger.debug(METHOD_NAME, "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlement[process]", "", "", "",
                    "DATABASE Connection is NULL");
                return;
            }
            // getting process id
            processId = ProcessI.SOS_SETTLEMENT_PROCESSID;
            // method call to check status of the process
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(con, processId);
            statusOk = _processStatusVO.isStatusOkBool();
            beforeInterval = BTSLUtil.parseLongToInt( _processStatusVO.getBeforeInterval() / (60 * 24));
            SOSTxnDAO _sosTxnDAO = new SOSTxnDAO();

            if (statusOk) {
                con.commit();
                // method call to find maximum date till which process has been
                // executed
                processedUpto = _processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    // adding 1 in processed upto dtae as we have to start from
                    // the next day till which process has been executed
                    processedUpto = BTSLUtil.addDaysInUtilDate(processedUpto, 1);
                    executeTilllDate = BTSLUtil.addDaysInUtilDate(currentDate, 1);
                    ArrayList sosSettList = null;
                    SOSSettlementRequestLog.log("SOSSettlement", "process", "Process Start Date :" + currentDate);
                    SOSSettlementRequestLog.log("SOSSettlement", "process", "Process run from Date :" + BTSLUtil.getSQLDateFromUtilDate(processedUpto) + " to=" + BTSLUtil
                        .addDaysInUtilDate(currentDate, -beforeInterval));
                    final long sleepTime = Long.parseLong(Constants.getProperty("SLEEP_TIME_SOS_SETTLEMENT"));

                    sosSettList = null;

                    // avoid privious fail data in every day iteration
                    // if we are running process date wise privious fail data
                    // will pick in next date, to avoid this
                    // pick data in one go from current date.

                    dateCount = BTSLUtil.addDaysInUtilDate(currentDate, -beforeInterval);

                    dateForSOS_SettlementData = BTSLUtil.addDaysInUtilDate(dateCount, -((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_SETTLE_DAYS))).intValue());
                    if (_logger.isDebugEnabled()) {
                        _logger.debug(METHOD_NAME,
                            "	SOS SETTLEMENT DAYS" + ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_SETTLE_DAYS))).intValue() + " PROCEES RUNNNING FOR DATE" + dateCount + " SOS Data Fetch DATE");
                    }
                    SOSSettlementRequestLog
                        .log("SOSSettlement", "process",
                            "*****************************************************************************************************************************************************************");
                    SOSSettlementRequestLog
                        .log(
                            "SOSSettlement",
                            "process",
                            "Process Running for date :" + dateCount + " Load SOS Settlement data for date=" + dateForSOS_SettlementData + " as the SOS_SETTLE_DAYS is " + ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_SETTLE_DAYS))).intValue());
                    sosSettList = _sosTxnDAO.loadSOSSettlementList(con, dateForSOS_SettlementData);

                    _sosvoProcessdeatil = new SOSVO();
                    SOSVO sosvo = null;
                    int i = 1;
                    // method call to update maximum date till which process has
                    // been executed
                    _processStatusVO.setExecutedUpto(dateCount);
                    _processStatusVO.setExecutedOn(currentDate);
                    processStatusDAO = new ProcessStatusDAO();
                    final int maxDoneDateUpdateCount = processStatusDAO.updateProcessDetail(con, _processStatusVO);
                    // if the process is successful, transaction is commit, else
                    // rollback
                    if (maxDoneDateUpdateCount > 0) {
                        con.commit();
                    } else {
                        con.rollback();
                        throw new BTSLBaseException("SOSSettlement", "process", "ERROR TO UPDATE PROCESS_STATUS table");
                    }

                    try {
                        if (sosSettList != null && !(sosSettList.isEmpty())) {
                            if (_logger.isDebugEnabled()) {
                                _logger.debug(METHOD_NAME, "TOTAL RECOD FETCH " + sosSettList.size());
                            }
                            _sosvoProcessdeatil.setTotalRecords(sosSettList.size());

                            for (final Object sosvoObject : sosSettList) {
                                sosvo = (SOSVO) sosvoObject;
                                sosvo.setRecordCount(i);
                                processSettlement(sosvo, con);
                                Thread.sleep(sleepTime);
                                i++;
                            }
                        } else {
                            _sosvoProcessdeatil.setTotalRecords(0);
                        }
                    } finally {
                        SOSSettlementRequestLog
                            .log(
                                "SOSSettlement",
                                "process",
                                "Process Running for date :" + dateCount + " Load SOS Settlement data for date=" + dateForSOS_SettlementData + " TOTAL RECORDS=" + _sosvoProcessdeatil
                                    .getTotalRecords() + " SUCCESS RECORDS=" + _sosvoProcessdeatil.getTotalSucces() + " FAIL RECORDS=" + _sosvoProcessdeatil.getTotalFail());
                    }

                    for (dateCount = BTSLUtil.getSQLDateFromUtilDate(processedUpto); dateCount.before(BTSLUtil.addDaysInUtilDate(executeTilllDate, -beforeInterval)); dateCount = BTSLUtil
                        .addDaysInUtilDate(dateCount, 1)) {
                        // these transaction will for auto reconcillation
                        try {
                            sosSettList = null;
                            SOSSettlementRequestLog
                                .log(
                                    "SOSSettlement",
                                    "process",
                                    "Process Running for date :" + dateCount + " Load SOS RECON Settlement data for date=" + dateCount + " and recharge date" + dateForSOS_SettlementData + " as the SOS_SETTLE_DAYS is " + ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_SETTLE_DAYS))).intValue());
                            sosSettList = _sosTxnDAO.loadSOSReconSettlementList(con, dateCount);
                            _sosvoProcessdeatil = new SOSVO();
                            if (sosSettList != null && !(sosSettList.isEmpty())) {
                                if (_logger.isDebugEnabled()) {
                                    _logger.debug(METHOD_NAME, "TOTAL RECOD FETCH " + sosSettList.size());
                                }
                                _sosvoProcessdeatil.setTotalRecords(sosSettList.size());
                                i = 1;
                                for (final Object sosvoObject : sosSettList) {
                                    sosvo = (SOSVO) sosvoObject;
                                    sosvo.setRecordCount(i);
                                    processSettlement((SOSVO) sosvo, con);
                                    Thread.sleep(sleepTime);
                                    i++;
                                }
                            } else {
                                _sosvoProcessdeatil.setTotalRecords(0);
                            }
                        } finally {
                            SOSSettlementRequestLog
                                .log(
                                    "SOSSettlement",
                                    "process",
                                    "Process Running for date :" + dateCount + " Load SOS RECON Settlement data for date=" + dateCount + " and recharge date=" + dateForSOS_SettlementData + " TOTAL RECON RECORDS=" + _sosvoProcessdeatil
                                        .getTotalRecords() + " SUCCESS RECON RECORDS=" + _sosvoProcessdeatil.getTotalSucces() + " FAIL RECON RECORDS=" + _sosvoProcessdeatil
                                        .getTotalFail());
                        }

                    } // end loop
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "SOSSettlement[process]", "", "", "",
                        " SOSSettlement process has been executed successfully.");
                } else {
                    throw new BTSLBaseException("SOSSettlement", "process", PretupsErrorCodesI.SOS_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
                }
            }
        }// end of try
        catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "SOSSettlement[process]", "", "", "",
                " SOSSettlement process could not be executed successfully.");
            throw new BTSLBaseException("SOSSettlement", METHOD_NAME, PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
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
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            _logger.debug(METHOD_NAME, "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... ");
            }
            SOSSettlementRequestLog.log("SOSSettlement", "process", "Exiting.......");
        }
    }

    /**
     * This method used to process the settlement in the system.
     * 
     * @param p_sosvo
     * @param p_con
     * @throws BTSLBaseException
     */
    private static void processSettlement(SOSVO p_sosvo, Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "processSettlement";
        if (_logger.isDebugEnabled()) {
            _logger.debug("processSettlement", "Entering: p_sosvo" + p_sosvo);
        }
        final long startTime = System.currentTimeMillis();
        final SOSSettlementController sos = new SOSSettlementController();
        SOSTxnDAO _sosTxnDAO = new SOSTxnDAO();
        try {
        	
            sos.process(p_sosvo);
            if (p_sosvo.getTransactionStatus().equals(PretupsErrorCodesI.TXN_STATUS_SUCCESS)) {
                _sosvoProcessdeatil.setTotalSucces(_sosvoProcessdeatil.getTotalSucces() + 1);
            } else {
                _sosvoProcessdeatil.setTotalFail(_sosvoProcessdeatil.getTotalFail() + 1);
                _sosTxnDAO.insertBadLendMeBalanceRechargesDuringSettlement(p_con, p_sosvo);
                // Insert those transactions which are failed in a different
                // table which holds bad recharges.
            }
        } catch (Exception e) {
            _sosvoProcessdeatil.setTotalFail(_sosvoProcessdeatil.getTotalFail() + 1);
            _logger.errorTrace(METHOD_NAME, e);
        } finally {
            final long endTime = System.currentTimeMillis();
            SOSSettlementRequestLog
                .log("SOSSettlement", "processSettlement",
                    "[START TIME=" + startTime + " END TIME=" + endTime + " TOTAL TIME TAKEN=" + (endTime - startTime) + " TOTAL RECORDS=" + _sosvoProcessdeatil
                        .getTotalRecords() + " CURRENT RECORD=" + p_sosvo.getRecordCount() + "] After Process Req::" + p_sosvo);
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug("processSettlement", "Exiting: p_sosvo" + p_sosvo);
        }
    }

    /**
     * @param p_con
     *            Connection
     * @param p_processId
     *            String
     * @throws BTSLBaseException
     * @return int
     */
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSSettlement[markProcessStatusAsComplete]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("SOSSettlement", "markProcessStatusAsComplete", PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }
}

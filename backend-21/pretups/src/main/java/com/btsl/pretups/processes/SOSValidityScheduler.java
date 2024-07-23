package com.btsl.pretups.processes;

/**
 * @(#)SOSValidityScheduler
 *                          Copyright(c) 2012, Comviva Technologies Ltd.
 *                          All Rights Reserved
 * 
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Author Date History
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Ankuj Arora 3/23/2012 Initial Creation
 * 
 *                          This process is used to find out the users whose
 *                          validity assigned for the LMB service has expired
 *                          and
 *                          they must not be allowed to recharge in the system
 *                          as long as these MSISDNs arent uploaded in the
 *                          system
 *                          again.
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
import com.btsl.pretups.logging.SOSSettlementRequestLog;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.sos.businesslogic.SOSVO;
import com.btsl.pretups.sos.requesthandler.SOSSettlementController;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.txn.pretups.sos.businesslogic.SOSTxnDAO;

public class SOSValidityScheduler {
    private static ProcessStatusVO _processStatusVO;
    private static ProcessBL _processBL = null;
    private static final Log _logger = LogFactory.getLog(SOSValidityScheduler.class.getName());
    private static SOSVO _sosvoProcessdeatil = null;

    private static SOSTxnDAO _sosTxnDAO = new SOSTxnDAO();

    /**
     * to ensure no class instantiation 
     */
    private SOSValidityScheduler(){
    	
    }
    public static void main(String arg[]) {
        final Date date = null;
        final String METHOD_NAME = "main";
        try {
            if (arg.length != 2) {
                System.out.println("Usage : SOSValidityScheduler [Constants file] [LogConfig file]");
                return;
            }
            final File constantsFile = Constants.validateFilePath(arg[0]);
            final File logConfigFile = new File(arg[1]);
            if (!constantsFile.exists()) {
                System.out.println("Constants file does not exist..........");
                return;
            } else if (!logConfigFile.exists()) {
                System.out.println("Logger config file does not exist..........");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logConfigFile.toString());
        }

        catch (Exception e) {
            _logger.error(METHOD_NAME, "Error in Loading Configuration files ...........................: " + e);
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
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
        Date dateForSOS_ValidityCheck = null; // this date compare in the
        // recharge date of the SOS table
        _logger.debug("process", "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
        try {
            currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("process", " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSValidityScheduler[process]", "",
                    "", "", "DATABASE Connection is NULL");
                return;
            }
            // getting process id
            processId = ProcessI.SOSVALIDITY;
            // method call to check status of the process
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(con, processId);
            statusOk = _processStatusVO.isStatusOkBool();
            beforeInterval = BTSLUtil.parseLongToInt( _processStatusVO.getBeforeInterval() / (60 * 24));
            processedUpto = _processStatusVO.getExecutedUpto();
            if (processedUpto != null) {

                // adding 1 in processed upto dtae as we have to start from the
                // next day till which process has been executed
                processedUpto = BTSLUtil.addDaysInUtilDate(processedUpto, 1);
                executeTilllDate = BTSLUtil.addDaysInUtilDate(currentDate, 1);
                ArrayList sosValChkList = null;
                SOSSettlementRequestLog.log("SOSValidityScheduler", "process", "Process Start Date :" + currentDate);
                SOSSettlementRequestLog.log("SOSValidityScheduler", "process", "Process run from Date :" + BTSLUtil.getSQLDateFromUtilDate(processedUpto) + " to=" + BTSLUtil
                    .addDaysInUtilDate(currentDate, -beforeInterval));
                final long sleepTime = Long.parseLong(Constants.getProperty("SLEEP_TIME_SOS_SETTLEMENT"));

                // for(dateCount=BTSLUtil.getSQLDateFromUtilDate(processedUpto);dateCount.before(BTSLUtil.addDaysInUtilDate(executeTilllDate,-beforeInterval));dateCount=BTSLUtil.addDaysInUtilDate(dateCount,1))
                // {

                // avoid privious fail data in every day iteration
                // if we are running process date wise privious fail data will
                // pick in next date, to avoid this
                // pick data in one go from current date.

                dateCount = BTSLUtil.addDaysInUtilDate(currentDate, -beforeInterval);

                dateForSOS_ValidityCheck = BTSLUtil.addDaysInUtilDate(dateCount, -((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_MIN_VALIDITY_DAYS))).intValue());
                if (_logger.isDebugEnabled()) {
                    _logger.debug("process",
                        "	SOS_MIN_VALIDITY_DAYS" + ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_MIN_VALIDITY_DAYS))).intValue() + " PROCEES RUNNNING FOR DATE" + dateCount + " SOS Data Fetch DATE");
                }
                SOSSettlementRequestLog
                    .log("SOSValidityScheduler", "process",
                        "*****************************************************************************************************************************************************************");
                SOSSettlementRequestLog
                    .log(
                        "SOSValidityScheduler",
                        "process",
                        "Process Running for date :" + dateCount + " Load SOS Settlement data for date=" + dateForSOS_ValidityCheck + " as the SOS_SETTLE_DAYS is " + ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_SETTLE_DAYS))).intValue());

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
                    throw new BTSLBaseException("SOSValidityScheduler", "process", "ERROR TO UPDATE PROCESS_STATUS table");
                }
                for (dateCount = BTSLUtil.getSQLDateFromUtilDate(processedUpto); dateCount.before(BTSLUtil.addDaysInUtilDate(executeTilllDate, -beforeInterval)); dateCount = BTSLUtil
                    .addDaysInUtilDate(dateCount, 1)) {
                    try {

                        sosValChkList = null;
                        SOSSettlementRequestLog
                            .log(
                                "SOSValidityScheduler",
                                "process",
                                "Process Running for date :" + dateCount + " Load SOS Validity Check data for date=" + dateCount + " as the SOS_MIN_VALIDITY_DAYS is " + ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SOS_MIN_VALIDITY_DAYS))).intValue());
                        sosValChkList = _sosTxnDAO.loadSOSValidityChkList(con, dateCount);
                        if (sosValChkList != null && !(sosValChkList.isEmpty())) {
                            if (_logger.isDebugEnabled()) {
                                _logger.debug("process", "TOTAL RECOD FETCH " + sosValChkList.size());
                            }
                            _sosvoProcessdeatil.setTotalRecords(sosValChkList.size());
                            for (final Object sosvoObject : sosValChkList) {
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
                        SOSSettlementRequestLog.log("SOSValidityScheduler", "process",
                            "Process Running for date :" + dateCount + " Load SOS Validity Check data for date=" + dateCount + " TOTAL RECON RECORDS=" + _sosvoProcessdeatil
                                .getTotalRecords());
                    }
                } // end loop
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "SOSValidityScheduler[process]", "", "", "",
                    " SOSValidityScheduler process has been executed successfully.");
            } else {
                throw new BTSLBaseException("SOSValidityScheduler", "process", PretupsErrorCodesI.SOS_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
            }
        } catch (BTSLBaseException be) {
            _logger.error("process", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _logger.error("process", "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "SOSValidityScheduler[process]", "", "", "",
                " SOSValidityScheduler process could not be executed successfully.");
            throw new BTSLBaseException("SOSValidityScheduler", "process", PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
        }

        // if the status was marked as under process by this method call, only
        // then it is marked as complete on termination
        finally {
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
            _logger.debug("process", "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            if (_logger.isDebugEnabled()) {
                _logger.debug("process", "Exiting..... ");
            }
            SOSSettlementRequestLog.log("SOSValidityScheduler", "process", "Exiting.......");
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
        try {
            _sosTxnDAO.updateValidityStatus(p_con, p_sosvo);
            // Insert those transactions which are failed in a different table
            // which holds bad recharges.
        } catch (Exception e) {
            _sosvoProcessdeatil.setTotalFail(_sosvoProcessdeatil.getTotalFail() + 1);
            _logger.errorTrace(METHOD_NAME, e);
        } finally {
            final long endTime = System.currentTimeMillis();
            SOSSettlementRequestLog
                .log("SOSValidityScheduler", "processSettlement",
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
        } catch (BTSLBaseException be) {
            _logger.error("process", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("markProcessStatusAsComplete", "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SOSValidityScheduler[markProcessStatusAsComplete]",
                "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("SOSValidityScheduler", "markProcessStatusAsComplete", PretupsErrorCodesI.SOS_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;

    }
}

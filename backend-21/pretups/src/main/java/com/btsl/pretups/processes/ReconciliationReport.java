package com.btsl.pretups.processes;

/**
 * @(#)ReconciliationReport.java
 *                               Copyright(c) 2006, Bharti Telesoft Ltd.
 *                               All Rights Reserved
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Author Date History
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Ved Prakash Sharma 22/08/2006 Initial Creation
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.btsl.pretups.inter.cache.FileCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.IntervalTimeVO;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

/**
 * @author ved.sharma
 *         This class user for generate P2P And C2S Reconcilation report
 *         Interface wise.
 */
public class ReconciliationReport {
    private static Log _log = LogFactory.getLog(ReconciliationReport.class.getName());
    private static String _sqlDateFormat = null;
    private static String _sqlDateTimeFormat = null;
    private static String _dateTimeFormat = null;
    private static SimpleDateFormat _simpleDateFormat = null;
    private static Locale _locale = null;
    private static String _fileLabelP2P = null;
    private static String _fileLabelC2S = null;
    private static String _queryC2S = null;
    private static String _queryP2PInterfaceWise = null;
    private static String _queryP2PServiceWise = null;
    private static boolean _isHeaderShow = false;
    private static boolean _isFooterShow = false;

    /**
     * Method main
     * 
     * @param arg
     *            String[]
     *            arg[0] = Constants.props (With path)
     *            arg[1] = ProcessLogConfig.props (With path)
     *            arg[2] = QueryFile.props (With path)
     *            arg[3] = Module(BOTH/C2S/P2P)
     *            arg[4] = Process interval nemuric value (24 hours for a day)
     *            arg[5] = Locale(0 or 1)
     * @author Ved Prakash
     */
    public static void main(String[] arg) {
        final String METHOD_NAME = "main";
        try {
            if (arg.length != 6)// check the argument length
            {
                _log.info(METHOD_NAME,
                    "ReconciliationReport :: Not sufficient arguments, please pass Conatnsts.props ProcessLogconfig.props QueryFile.props moduleCode ProcessInterval Locale");
                return;
            }
            final File constantsFile = new File(arg[0]);
            if (!constantsFile.exists())// check file (Constants.props) exist or
            // not
            {
                _log.debug(METHOD_NAME, "ReconciliationReport" + " Constants File Not Found at the path : " + arg[0]);
                return;
            }
            final File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists())// check file (ProcessLogConfig.props)
            // exist or not
            {
                _log.debug(METHOD_NAME, "ReconciliationReport" + " ProcessLogConfig File Not Found at the path : " + arg[1]);
                return;
            }
            final File queryFile = new File(arg[2]);
            if (!queryFile.exists())// check file (ProcessLogConfig.props) exist
            // or not
            {
                _log.debug(METHOD_NAME, "ReconciliationReport" + " QueryFile File Not Found at the path : " + arg[2]);
                return;
            }
            if (!(arg[3].equalsIgnoreCase("BOTH") || arg[3].equalsIgnoreCase(PretupsI.C2S_MODULE) || arg[3].equalsIgnoreCase(PretupsI.P2P_MODULE)))// check
            // the
            // module
            // value
            // ("BOTH"/"P2P"/"C2S")
            {
                _log.debug(METHOD_NAME, "ReconciliationReport :: Invalid Module code " + arg[3] + " It should be BOTH or P2P or C2S");
                return;
            }
            if (!BTSLUtil.isNullString(arg[4]))// Process Interval
            {
                if (!BTSLUtil.isNumeric(arg[4]))// check the Process Interval is
                // numeric
                {
                    _log.debug(METHOD_NAME, "ReconciliationReport :: Invalid Process Interval " + arg[4] + " It should be between 1 - 24");
                    return;
                } else if (Integer.parseInt(arg[4]) < 1) {
                    _log.debug(METHOD_NAME, "ReconciliationReport :: Invalid Process Interval " + arg[4] + " It should be between 1 - 24");
                    return;
                }
            } else {
                _log.info(METHOD_NAME, "ReconciliationReport" + " Process Interval missing ");
                return;
            }
            if (BTSLUtil.isNullString(arg[5]))// Locale check
            {
                _log.info(METHOD_NAME, "ReconciliationReport :: Locale is missing ");
                return;
            }
            if (!BTSLUtil.isNumeric(arg[5]))// check the Process Interval is
            // numeric
            {
                _log.debug(METHOD_NAME, "ReconciliationReport :: Invalid Locale " + arg[5] + " It should be 0 or 1");
                return;
            }
            if (Integer.parseInt(arg[5]) > 1) {
                _log.debug(METHOD_NAME, "ReconciliationReport :: Invalid Locale " + arg[5] + " It should be 0 or 1");
                return;
            }
            // use to load the Constants.props and ProcessLogConfig.props files
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            Constants.load(arg[2]);
            FileCache.loadAtStartUp();
        }// end of try
        catch (Exception e) {
            _log.error("main", "Main: Error in loading the Cache information.." + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ReconciliationReport[main]", "", "", "",
                "  Error in loading the Cache information");
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {
            final String c2sQryKey = "query.process.reconcilationreport.c2s.interfacewise";
            final String p2pIntQryKey = "query.process.reconcilationreport.p2p.interfacewise";
            final String p2pSerQryKey = "query.process.reconcilationreport.p2p.servicewise";
            _queryC2S = Constants.getProperty(c2sQryKey);
            if (BTSLUtil.isNullString(_queryC2S)) {
                _log.error("main", "Error in loading the Query information..key :" + c2sQryKey);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ReconciliationReport[main]", "", "", "",
                    "  Error in loading the QueryFile key " + c2sQryKey);
                ConfigServlet.destroyProcessCache();
                return;
            }
            _queryP2PInterfaceWise = Constants.getProperty(p2pIntQryKey);
            if (BTSLUtil.isNullString(_queryP2PInterfaceWise)) {
                _log.error("main", "Error in loading the Query information..key :" + p2pIntQryKey);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ReconciliationReport[main]", "", "", "",
                    "  Error in loading the QueryFile key " + p2pIntQryKey);
                ConfigServlet.destroyProcessCache();
                return;
            }
            _queryP2PServiceWise = Constants.getProperty(p2pSerQryKey);
            if (BTSLUtil.isNullString(_queryP2PServiceWise)) {
                _log.error("main", "Error in loading the Query information..key :" + p2pSerQryKey);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ReconciliationReport[main]", "", "", "",
                    "  Error in loading the QueryFile key " + p2pSerQryKey);
                ConfigServlet.destroyProcessCache();
                return;
            }

        } catch (Exception e) {
            _log.error("main", "Main: Error in loading the Query information.." + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ReconciliationReport[main]", "", "", "",
                "  Error in loading the QueryFile");
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {
            _locale = LocaleMasterCache.getLocaleFromCodeDetails(arg[5]);
            if (_locale == null) {
                _locale = LocaleMasterCache.getLocaleFromCodeDetails("0");
                if (_log.isDebugEnabled()) {
                    _log.debug("main", "Error : Invalid Locale " + arg[5] + " It should be 0 or 1, thus using default locale code 0");
                }
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "ReconciliationReport[main]", "", "", "",
                    "  Message:  Invalid Locale " + arg[5] + " It should be 0 (EN) or 1 (OTH) ");
            }
        } catch (Exception e) {
            _log.error("main", " Invalid locale : " + arg[5] + " Exception:" + e.getMessage());
            _locale = new Locale("en", "US");
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "ReconciliationReport[main]", "", "", "",
                "  Message:  Not able to get the locale");
        }
        try {
            _fileLabelC2S = BTSLUtil.getMessage(_locale, "C2S_RECON_RPT_REPORT_LABEL", null);
            _fileLabelP2P = BTSLUtil.getMessage(_locale, "P2P_RECON_RPT_REPORT_LABEL", null);

            _sqlDateFormat = Constants.getProperty("report.onlydateformat");
            _sqlDateTimeFormat = Constants.getProperty("report.datetimeformat");
            _dateTimeFormat = Constants.getProperty("report.systemdatetime.format");

            if (BTSLUtil.isNullString(_sqlDateFormat)) {
                _log.debug("main", "Main: Error  Missing entry from Constants.props [report.onlydateformat]");
                _sqlDateFormat = PretupsI.DATE_FORMAT;
            }
            if (BTSLUtil.isNullString(_sqlDateTimeFormat)) {
                _log.debug("main", "Main: Error  Missing entry from Constants.props [report.datetimeformat]");
                _sqlDateTimeFormat = "dd/MM/yy HH24:MI:SS";
            }
            if (BTSLUtil.isNullString(_dateTimeFormat)) {
                _log.debug("main", "Main: Error  Missing entry from Constants.props [report.systemdatetime.format]");
                _dateTimeFormat = PretupsI.TIMESTAMP_DATESPACEHHMMSS;
            }

            _simpleDateFormat = new SimpleDateFormat(_dateTimeFormat);
            _simpleDateFormat.setLenient(false);

            if (PretupsI.YES.equalsIgnoreCase(Constants.getProperty("RECON_RPT_HEADER_SHOW"))) {
                _isHeaderShow = true;
            }

            if (PretupsI.YES.equalsIgnoreCase(Constants.getProperty("RECON_RPT_FOOTER_SHOW"))) {
                _isFooterShow = true;
            }

        } catch (Exception ee) {
            _log
                .error(
                    "main",
                    "Missing/Wrong entry from Constants.props [report.onlydateformat] or [report.datetimeformat] or [report.systemdatetime.format] or [RECON_RPT_HEADER_SHOW] or [RECON_RPT_FOOTER_SHOW] " + ee
                        .getMessage());
            _log.errorTrace(METHOD_NAME, ee);
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            if (_log.isDebugEnabled()) {
                _log.debug("main", " _sqlDateFormat : " + _sqlDateFormat + " _sqlDateTimeFormat : " + _sqlDateTimeFormat);
            }
            final ReconciliationReport recon = new ReconciliationReport();
            recon.process(Integer.parseInt(arg[4]), arg[3]);
        } catch (BTSLBaseException be) {
            _log.error("main", "BTSLBaseException :" + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            // event handle
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ReconciliationReport[main]", "", "", "",
                "BTSLBaseException:" + be.getMessage());
        } catch (Exception e) {
            _log.error("main", "Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            // event handle
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ReconciliationReport[main]", "", "", "",
                "Exception:" + e.getMessage());
        } finally {
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * Method process
     * This method start the process. first check the module code the start the
     * process according to module code.
     * Maximum three process and Minimum one process is running.
     * In C2S module one process Execute (C2S interface wise)
     * In P2P module two process Execute (1. P2P interface wise, 2. P2P service
     * type wise)
     * In BOTH module three process Execute (1. C2S interface wise, 2. P2P
     * interface wise, 3. P2P service type wise)
     * 
     * @param p_processInterval
     *            int
     * @param p_module
     *            String
     * @throws BTSLBaseException
     * @author Ved Prakash
     */
    private void process(int p_processInterval, String p_module) throws BTSLBaseException {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " Entered:  p_processInterval : " + p_processInterval + " p_module: " + p_module);
        }
        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_log.isDebugEnabled()) {
                    _log.debug(METHOD_NAME, " DATABASE Connection is NULL ");
                }
                throw new BTSLBaseException(this, METHOD_NAME, "Not able to get the connection");
            }
            final String transferStatus = PretupsErrorCodesI.TXN_STATUS_FAIL;
            if (p_module.equalsIgnoreCase(PretupsI.P2P_MODULE))// For only P2P
            // Module
            {
                processP2P(con, p_processInterval, transferStatus);
            }// end of if(p_module.equalsIgnoreCase(PretupsI.P2P_MODULE))// For
             // only P2P Module

            else if (p_module.equalsIgnoreCase(PretupsI.C2S_MODULE)) // For only
            // C2S
            // Module
            {
                this.processC2S(con, p_processInterval, transferStatus);
            }// End of else if(p_module.equalsIgnoreCase(PretupsI.C2S_MODULE))
             // // For only C2S Module

            else// For Both (P2P & C2S) Module
            {
                this.processC2S(con, p_processInterval, transferStatus);
                this.processP2P(con, p_processInterval, transferStatus);
            }// End OF else// For Both (P2P & C2S) Module
        } catch (BTSLBaseException be) {
            _log.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            throw be;
        } catch (Exception be) {
            _log.error(METHOD_NAME, "Exception : " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            throw new BTSLBaseException(this, METHOD_NAME, "Exception=" + be.getMessage());
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting..... ");
            }
        }
    }

    /**
     * Method processP2P
     * In P2P module two process Execute (1. P2P interface wise, 2. P2P service
     * type wise)
     * 
     * @param p_con
     *            Connection
     * @param p_processInterval
     *            int
     * @param p_transferStatus
     *            String
     * @throws BTSLBaseException
     * @author Ved Prakash
     */
    private void processP2P(Connection p_con, int p_processInterval, String p_transferStatus) {
        final String METHOD_NAME = "processP2P";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " Entered: p_processInterval : " + p_processInterval + " p_transferStatus: " + p_transferStatus);
        }
        try {
            ProcessStatusVO processVO = null;
            ProcessStatusDAO processDAO = null;
            final ProcessBL processBL = new ProcessBL();
            try // try block for P2P Service type
            {
                processVO = processBL.checkProcessUnderProcess(p_con, PretupsI.P2P_RECON_SERVICE_WISE_PROCESS_ID);
                if (processVO.isStatusOkBool()) {
                    p_con.commit();
                    final ArrayList intervalList = this.generateInterval(processVO, p_processInterval);
                    this.fetchP2PData(p_con, intervalList, processVO, p_transferStatus, true);// P2P
                    // Service
                    // type
                } else {
                    throw new BTSLBaseException("ReconciliationReport P2P Transfer (Service type wise)", "process", "Process is already running..");
                }
            } catch (BTSLBaseException e1) {
                _log.error(METHOD_NAME, "BTSLBaseException :  P2P Transfer (Service type wise) : " + e1.getMessage());
                _log.errorTrace(METHOD_NAME, e1);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ReconciliationReport[processP2P] P2P Reconcilation report(Service type wise)", "", "", "", "Exception for Process ID=" + e1.getMessage());
            } catch (Exception e2) {
                _log.error(METHOD_NAME, "Exception : P2P Transfer (Service type wise) : " + e2.getMessage());
                _log.errorTrace(METHOD_NAME, e2);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ReconciliationReport[processP2P] P2P Reconcilation report(Service type wise)", "", "", "", "Exception for Process ID=" + e2.getMessage());
            } finally {
                processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                try {
                    processDAO = new ProcessStatusDAO();
                    if (processDAO.updateProcessDetail(p_con, processVO) > 0) {
                        p_con.commit();
                    } else {
                        p_con.rollback();
                    }
                } catch (Exception e) {
                    _log.error(METHOD_NAME, " Exception in update process detail" + e.getMessage());
                    _log.errorTrace(METHOD_NAME, e);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "ReconciliationReport[processP2P] P2P Reconcilation report(Service type wise)", "", "", "",
                        "Exception in update process detail for Process ID=" + PretupsI.P2P_RECON_SERVICE_WISE_PROCESS_ID + " :" + e.getMessage());
                   }
            }

            try // try block for P2P Interface wise
            {
                processVO = null;
                processVO = processBL.checkProcessUnderProcess(p_con, PretupsI.P2P_RECON_INTERFACE_WISE_PROCESS_ID);
                if (processVO.isStatusOkBool()) {
                    p_con.commit();
                    final ArrayList intervalList = this.generateInterval(processVO, p_processInterval);
                    this.fetchP2PData(p_con, intervalList, processVO, p_transferStatus, false);// P2P
                    // Interface
                    // wise
                } else {
                    throw new BTSLBaseException("ReconciliationReport P2P Transfer (Interface wise)", "process", "Process is already running..");
                }

            } catch (BTSLBaseException e1) {
                _log.error(METHOD_NAME, "BTSLBaseException : P2P Transfer (Interface wise) : " + e1.getMessage());
                _log.errorTrace(METHOD_NAME, e1);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ReconciliationReport[processP2P] P2P Reconcilation report(Interface wise)", "", "", "", "BTSLBaseException:" + e1.getMessage());
            } catch (Exception e2) {
                _log.error(METHOD_NAME, "Exception : P2P Transfer (Interface wise) : " + e2.getMessage());
                _log.errorTrace(METHOD_NAME, e2);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ReconciliationReport[processP2P] P2P Reconcilation report(Interface wise)", "", "", "", "Exception:" + e2.getMessage());
            } finally {
                processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                try {
                    processDAO = new ProcessStatusDAO();
                    if (processDAO.updateProcessDetail(p_con, processVO) > 0) {
                        p_con.commit();
                    } else {
                        p_con.rollback();
                    }
                } catch (Exception e) {
                    _log.error(METHOD_NAME, " Exception in update process detail" + e.getMessage());
                    _log.errorTrace(METHOD_NAME, e);
                    ;
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "ReconciliationReport[processP2P] P2P Reconcilation report(Service type wise)", "", "", "",
                        "Exception in update process detail for Process ID=" + PretupsI.P2P_RECON_INTERFACE_WISE_PROCESS_ID + " :" + e.getMessage());
                }
            }
        } catch (Exception e) {
            _log.error(METHOD_NAME, " Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Exiting..... ");
        }
    }

    /**
     * Method process
     * In C2S module one process Execute (C2S interface wise)
     * 
     * @param p_con
     *            Connection
     * @param p_processInterval
     *            int
     * @param p_transferStatus
     *            String
     * @throws BTSLBaseException
     * @author Ved Prakash
     */
    private void processC2S(Connection p_con, int p_processInterval, String p_transferStatus) {
        final String METHOD_NAME = "processC2S";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " Entered: p_con : " + p_con + "  p_processInterval : " + p_processInterval + " p_transferStatus: " + p_transferStatus);
        }
        try {
            ProcessStatusVO processVO = null;
            ProcessStatusDAO processDAO = null;
            final ProcessBL processBL = new ProcessBL();

            try // try block for C2S Interface wise
            {
                processVO = null;
                processVO = processBL.checkProcessUnderProcess(p_con, PretupsI.C2S_RECON_INTERFACE_WISE_PROCESS_ID);
                if (processVO.isStatusOkBool()) {
                    p_con.commit();
                    final ArrayList intervalList = this.generateInterval(processVO, p_processInterval);
                    this.fetchC2SData(p_con, intervalList, processVO, p_transferStatus);// C2S
                    // Interface
                    // wise
                } else {
                    throw new BTSLBaseException("ReconciliationReport C2S Interface wise", "process", "Process is already running..");
                }
            } catch (BTSLBaseException e1) {
                _log.error(METHOD_NAME, "BTSLBaseException : C2S Transfer (Interface wise) : " + e1.getMessage());
                _log.errorTrace(METHOD_NAME, e1);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ReconciliationReport[processC2S] C2S Reconcilation report(Interface wise)", "", "", "", "BTSLBaseException:" + e1.getMessage());
            } catch (Exception e2) {
                _log.error(METHOD_NAME, "Exception : C2S Transfer (Interface wise) : " + e2.getMessage());
                _log.errorTrace(METHOD_NAME, e2);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ReconciliationReport[processC2S] C2S Reconcilation report(Interface wise)", "", "", "", "Exception:" + e2.getMessage());
            } finally {
                processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                try {
                    processDAO = new ProcessStatusDAO();
                    if (processDAO.updateProcessDetail(p_con, processVO) > 0) {
                        p_con.commit();
                    } else {
                        p_con.rollback();
                    }
                } catch (Exception e) {
                    _log.error(METHOD_NAME, " Exception in update process detail" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "ReconciliationReport[processC2S] C2S Transfer (Interface wise)", "", "", "",
                        "Exception in update process detail for Process ID=" + PretupsI.C2S_RECON_INTERFACE_WISE_PROCESS_ID + " :" + e.getMessage());
                    _log.errorTrace(METHOD_NAME, e);
                }
            }
        } catch (Exception e) {
            _log.error(METHOD_NAME, " Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
        }
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Exiting..... ");
        }
    }

    /**
     * method fetchC2SData
     * 
     * @deprecated This method will fetch all the required C2S transactions data
     *             from database
     * @param p_con
     *            Connection
     * @param p_intervalList
     *            ArrayList
     * @param p_processVO
     *            ProcessStatusVO
     * @param p_trasferStatus
     *            String
     * @throws BTSLBaseException
     * @author Ved Prakash
     */
    @Deprecated
    private void fetchC2SData(Connection p_con, ArrayList p_intervalList, ProcessStatusVO p_processVO, String p_trasferStatus) throws BTSLBaseException {
        final String METHOD_NAME = "fetchC2SData";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME,
                " Entered:p_intervalList.size()=" + p_intervalList.size() + "p_processVO =" + p_processVO.toString() + " p_trasferStatus : " + p_trasferStatus);
        }
        PreparedStatement pstmtC2SSelect = null;
        ResultSet rsC2S = null;
        Timestamp startTimestamp = null;
        try {
            String filePath = null;
            String fileName = null;
            try {
                filePath = Constants.getProperty("CREATE_RECONCILATION_REPORT") + PretupsI.C2S_MODULE + File.separator;
                fileName = Constants.getProperty("C2S_RECONCILATION_REPORT_FILE_NAME");
                final File fileDir = new File(filePath);
                if (!fileDir.isDirectory()) {
                    fileDir.mkdirs();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error(METHOD_NAME, "Exception" + e.getMessage());
                throw new BTSLBaseException("ReconciliationReport", METHOD_NAME, "Directory not create or file name  not found please check Constants.props");
            }

            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "C2S select query:" + _queryC2S);
            }
            pstmtC2SSelect = p_con.prepareStatement(_queryC2S, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            int i = 0;
            String fromDate = null;
            String toDate = null;
            IntervalTimeVO intervalTimeVO = null;
            final ProcessStatusDAO processDAO = new ProcessStatusDAO();
            for (int j = 0, k = p_intervalList.size(); j < k; j++) {
                startTimestamp = null;
                intervalTimeVO = (IntervalTimeVO) p_intervalList.get(j);
                startTimestamp = intervalTimeVO.getStartTime();
                i = 1;
                pstmtC2SSelect.clearParameters();
                pstmtC2SSelect.setString(i, _sqlDateFormat);
                i++;
                pstmtC2SSelect.setString(i, _sqlDateTimeFormat);
                i++;
                pstmtC2SSelect.setString(i, p_trasferStatus);
                i++;
                pstmtC2SSelect.setString(i, PretupsI.KEY_VALUE_C2C_STATUS);
                i++;
                pstmtC2SSelect.setTimestamp(i, intervalTimeVO.getStartTime());
                i++;
                pstmtC2SSelect.setTimestamp(i, intervalTimeVO.getEndTime());
                i++;
                rsC2S = pstmtC2SSelect.executeQuery();
                fromDate = _simpleDateFormat.format(BTSLUtil.getUtilDateFromTimestamp(intervalTimeVO.getStartTime()));
                toDate = _simpleDateFormat.format(BTSLUtil.getUtilDateFromTimestamp(intervalTimeVO.getEndTime()));
                // method call to write data in the files
                if (rsC2S != null) {
                    writeC2SDataInFile(rsC2S, filePath, fileName, fromDate, toDate);
                }
                p_processVO.setProcessStatus(ProcessI.STATUS_UNDERPROCESS);
                p_processVO.setExecutedUpto(intervalTimeVO.getEndTime());
                p_processVO.setExecutedOn(new Date());
                if (processDAO.updateProcessDetail(p_con, p_processVO) > 0) {
                    p_con.commit();
                } else {
                    p_processVO.setExecutedUpto(startTimestamp);
                    p_con.rollback();
                    throw new BTSLBaseException("ReconciliationReport", METHOD_NAME, "Process Status is not commited");
                }
            }
        } catch (BTSLBaseException be) {
            if (startTimestamp != null) {
                p_processVO.setExecutedUpto(startTimestamp);
            }
            _log.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (SQLException sqe) {
            if (startTimestamp != null) {
                p_processVO.setExecutedUpto(startTimestamp);
            }
            _log.error(METHOD_NAME, "SQLException " + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ReconciliationReport[fetchC2SData]", "", "", "",
                "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("ReconciliationReport", METHOD_NAME, "SQLException :" + sqe.getMessage());
        }// end of catch
        catch (Exception ex) {
            if (startTimestamp != null) {
                p_processVO.setExecutedUpto(startTimestamp);
            }
            _log.error(METHOD_NAME, "Exception : " + ex.getMessage());
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ReconciliationReport[fetchC2SData]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException("ReconciliationReport", METHOD_NAME, "Exception : " + ex.getMessage());
        }// end of catch
        finally {
            try {
                if (rsC2S != null) {
                	rsC2S.close();
                }
            } catch (Exception ex) {
                if (_log.isDebugEnabled()) {
                	_log.debug(METHOD_NAME, "Exception closing Callable statement ");
                }
                _log.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (pstmtC2SSelect != null) {
                	pstmtC2SSelect.close();
                }
            } catch (Exception ex) {
                if (_log.isDebugEnabled()) {
                	_log.debug(METHOD_NAME, "Exception closing Callable statement ");
                }
                _log.errorTrace(METHOD_NAME, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting ");
            }
        }// end of finally
    }

    /**
     * method fetchP2PData
     * This method will fetch all the required P2P transactions data from
     * database
     * 
     * @param p_con
     *            Connection
     * @param p_intervalList
     *            ArrayList
     * @param p_processVO
     *            ProcessStatusVO
     * @param p_trasferStatus
     *            String
     * @param p_orderByServiceType
     *            boolean
     * @throws BTSLBaseException
     * @author Ved Prakash
     */
    private void fetchP2PData(Connection p_con, ArrayList p_intervalList, ProcessStatusVO p_processVO, String p_trasferStatus, boolean p_orderByServiceType) throws BTSLBaseException {
        final String METHOD_NAME = "fetchP2PData";
        if (_log.isDebugEnabled()) {
            _log
                .debug(
                    METHOD_NAME,
                    " Entered: p_intervalList.size()=" + p_intervalList.size() + "p_processVO =" + p_processVO.toString() + " p_trasferStatus : " + p_trasferStatus + " p_orderByServiceType : " + p_orderByServiceType);
        }
        String qryStr = null;
        if (p_orderByServiceType) {
            qryStr = _queryP2PServiceWise;
        } else {
            qryStr = _queryP2PInterfaceWise;
        }

        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "P2P select query:" + qryStr);
        }

        PreparedStatement pstmtP2PSelect = null;
        ResultSet rsP2P = null;
        Timestamp startTimestamp = null;
        try {
            String filePath = null;
            String fileName = null;
            try {
                filePath = Constants.getProperty("CREATE_RECONCILATION_REPORT") + PretupsI.P2P_MODULE + File.separator;
                fileName = Constants.getProperty("P2P_RECONCILATION_REPORT_FILE_NAME");
                final File fileDir = new File(filePath);
                if (!fileDir.isDirectory()) {
                    fileDir.mkdirs();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                _log.error(METHOD_NAME, "Exception" + e.getMessage());
                throw new BTSLBaseException("ReconciliationReport", METHOD_NAME, "Directory not create or file name not found please check Constants.props");
            }
            pstmtP2PSelect = p_con.prepareStatement(qryStr.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            int i = 0;
            String fromDate = null;
            String toDate = null;
            IntervalTimeVO intervalTimeVO = null;
            final ProcessStatusDAO processDAO = new ProcessStatusDAO();
            for (int j = 0, k = p_intervalList.size(); j < k; j++) {
                startTimestamp = null;
                intervalTimeVO = (IntervalTimeVO) p_intervalList.get(j);
                startTimestamp = intervalTimeVO.getStartTime();
                i = 1;
                pstmtP2PSelect.clearParameters();
                pstmtP2PSelect.setString(i, _sqlDateFormat);
                i++;
                pstmtP2PSelect.setString(i, _sqlDateTimeFormat);
                i++;
                pstmtP2PSelect.setString(i, p_trasferStatus);
                i++;
                pstmtP2PSelect.setString(i, PretupsI.KEY_VALUE_P2P_STATUS);
                i++;
                pstmtP2PSelect.setTimestamp(i, intervalTimeVO.getStartTime());
                i++;
                pstmtP2PSelect.setTimestamp(i, intervalTimeVO.getEndTime());
                i++;
                rsP2P = pstmtP2PSelect.executeQuery();

                fromDate = _simpleDateFormat.format(BTSLUtil.getUtilDateFromTimestamp(intervalTimeVO.getStartTime()));
                toDate = _simpleDateFormat.format(BTSLUtil.getUtilDateFromTimestamp(intervalTimeVO.getEndTime()));
                // method call to write data in the files
                if (rsP2P != null) {
                    writeP2PDataInFile(rsP2P, filePath, fileName, fromDate, toDate);
                }
                p_processVO.setProcessStatus(ProcessI.STATUS_UNDERPROCESS);
                p_processVO.setExecutedUpto(intervalTimeVO.getEndTime());
                p_processVO.setExecutedOn(new Date());
                if (processDAO.updateProcessDetail(p_con, p_processVO) > 0) {
                    p_con.commit();
                } else {
                    p_processVO.setExecutedUpto(startTimestamp);
                    p_con.rollback();
                    throw new BTSLBaseException("ReconciliationReport", METHOD_NAME, "Process Status is not commited");
                }
            }
        } catch (BTSLBaseException be) {
            if (startTimestamp != null) {
                p_processVO.setExecutedUpto(startTimestamp);
            }
            _log.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (SQLException sqe) {
            if (startTimestamp != null) {
                p_processVO.setExecutedUpto(startTimestamp);
            }
            _log.error(METHOD_NAME, "SQLException " + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ReconciliationReport[fetchP2PData]", "", "", "",
                "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("ReconciliationReport", METHOD_NAME, "SQLException :" + sqe.getMessage());
        }// end of catch
        catch (Exception ex) {
            if (startTimestamp != null) {
                p_processVO.setExecutedUpto(startTimestamp);
            }
            _log.error(METHOD_NAME, "Exception : " + ex.getMessage());
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ReconciliationReport[fetchP2PData]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException("ReconciliationReport", METHOD_NAME, "Exception : " + ex.getMessage());
        }// end of catch
        finally {
            
                try {
                	if (rsP2P != null) {
                    rsP2P.close();
                	}
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
            
            if (pstmtP2PSelect != null) {
                try {
                    pstmtP2PSelect.close();
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting ");
            }
        }// end of finally
    }

    /**
     * Method writeC2SDataInFile
     * This method write C2S data in Csv files
     * 
     * @param p_rs
     *            ResultSet
     * @param p_filePath
     *            String
     * @param p_fileName
     *            String
     * @param p_fromDate
     *            String
     * @param p_toDate
     *            String
     * @param p_tempHsMap
     *            TODO
     * @throws BTSLBaseException
     * @author Ved Prakash
     */
    private void writeC2SDataInFile(ResultSet p_rs, String p_filePath, String p_fileName, String p_fromDate, String p_toDate) throws BTSLBaseException {
        final String METHOD_NAME = "writeC2SDataInFile";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME,
                " Entered:  p_rs=" + p_rs + " p_filePath : " + p_filePath + " p_fileName : " + p_fileName + " p_fromDate : " + p_fromDate + " p_toDate : " + p_toDate);
        }
        PrintWriter out = null;
        File newFile = null;
        String oldExternalID = null;
        String newExternalID = null;
        String fileData = null;
        String fileHeader = null;
        String fileFooter = null;
        int recordsWrittenInFile = 0;
        String finalFileName = null;
        float multFactor = 1;
        String interfaceID = null;
        boolean interFaceChangeFlag = true;

        try {
            String tmpReplace[] = null;
            String tempFinalStr = "";
            while (p_rs.next()) {
                fileData = p_rs.getString(1);
                newExternalID = p_rs.getString(2);
                interfaceID = p_rs.getString(3);
                tempFinalStr = "";
                if (!BTSLUtil.isNullString(FileCache.getValue(interfaceID, "MULTIPLICATION_FACTOR"))) {
                    multFactor = Float.parseFloat(FileCache.getValue(interfaceID, "MULTIPLICATION_FACTOR"));
                } else {
                    multFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
                }
                tmpReplace = fileData.split(",");
                for (int i = 0, j = tmpReplace.length; i < j; i++) {
                    if (((tmpReplace[i].trim()).toUpperCase()).startsWith("RBV")) {
                        tmpReplace[i] = (Integer.parseInt((tmpReplace[i].substring(tmpReplace[i].indexOf("=") + 1)).trim()) / multFactor) + "";
                    } else if (((tmpReplace[i].trim()).toUpperCase()).startsWith("RV")) {
                        tmpReplace[i] = (Integer.parseInt((tmpReplace[i].substring(tmpReplace[i].indexOf("=") + 1)).trim()) / multFactor) + "";
                    } else if (((tmpReplace[i].trim()).toUpperCase()).startsWith("TV")) {
                        tmpReplace[i] = (Integer.parseInt((tmpReplace[i].substring(tmpReplace[i].indexOf("=") + 1)).trim()) / multFactor) + "";
                    }
                    tempFinalStr = tempFinalStr + tmpReplace[i] + ",";
                }
                if (!BTSLUtil.isNullString(tempFinalStr)) {
                    fileData = tempFinalStr.substring(0, tempFinalStr.length() - 1);
                }

                if (recordsWrittenInFile != 0 && !oldExternalID.equals(newExternalID)) {
                    interFaceChangeFlag = true;
                    if (_isFooterShow) {
                        fileFooter = constructFileFooter(recordsWrittenInFile);
                        out.write(fileFooter);
                    }
                    out.flush();
                    if (out != null) {
                        out.close();
                    }
                }
                if (interFaceChangeFlag) {
                    interFaceChangeFlag = false;
                    oldExternalID = newExternalID;
                    recordsWrittenInFile = 0;
                    finalFileName = p_filePath + p_fileName + "_" + newExternalID + "_" + BTSLUtil.getFileNameStringFromDate(BTSLUtil.getDateFromDateString(p_fromDate,
                        _dateTimeFormat)) + ".csv";
                    newFile = new File(finalFileName);
                    out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
                    if (_log.isDebugEnabled()) {
                        _log.debug(METHOD_NAME, "  fileName=" + finalFileName);
                    }
                    if (_isHeaderShow) {
                        fileHeader = constructFileHeader(p_fromDate, p_toDate, _fileLabelC2S);
                        out.write(fileHeader + "\n");
                    }
                }
                out.write(fileData + "\n");
                recordsWrittenInFile++;
            }// end of while(p_rs.next())
             // if number of records are not zero then footer is appended as
             // file is deleted
            if (recordsWrittenInFile > 0) {
                if (_isFooterShow) {
                    fileFooter = constructFileFooter(recordsWrittenInFile);
                    out.write(fileFooter);
                }
            } else {
                if (out != null) {
                    out.close();
                }
                if (newFile != null) {
                	boolean isDeleted = newFile.delete();
                    if(isDeleted){
                     _log.debug(METHOD_NAME, "File deleted successfully");
                    }
                }
            }
        } catch (BTSLBaseException e) {
            _log.debug(METHOD_NAME, "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            if (newFile != null) {
            	boolean isDeleted = newFile.delete();
                if(isDeleted){
                 _log.debug(METHOD_NAME, "File deleted successfully");
                }
                newFile = null;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ReconciliationReport[writeC2SDataInFile]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("ReconciliationReport", METHOD_NAME, "Exception: " + e.getMessage());
        } catch (SQLException e) {
            _log.debug(METHOD_NAME, "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            if (newFile != null) {
            	boolean isDeleted = newFile.delete();
                if(isDeleted){
                 _log.debug(METHOD_NAME, "File deleted successfully");
                }
                newFile = null;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ReconciliationReport[writeC2SDataInFile]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("ReconciliationReport", METHOD_NAME, "Exception: " + e.getMessage());
        } catch (ParseException e) {
            _log.debug(METHOD_NAME, "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            if (newFile != null) {
            	boolean isDeleted = newFile.delete();
                if(isDeleted){
                 _log.debug(METHOD_NAME, "File deleted successfully");
                }
                newFile = null;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ReconciliationReport[writeC2SDataInFile]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("ReconciliationReport", METHOD_NAME, "Exception: " + e.getMessage());
        } catch (Exception e) {
            _log.debug(METHOD_NAME, "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            if (newFile != null) {
            	boolean isDeleted = newFile.delete();
                if(isDeleted){
                 _log.debug(METHOD_NAME, "File deleted successfully");
                }
                newFile = null;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ReconciliationReport[writeC2SDataInFile]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("ReconciliationReport", METHOD_NAME, "Exception: " + e.getMessage());
        } finally {
            if (out != null) {
                out.close();
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting ");
            }
        }
    }

    /**
     * Method writeP2PDataInFile
     * This method write P2P data in Csv files
     * 
     * @param p_rs
     *            ResultSet
     * @param p_filePath
     *            String
     * @param p_fileName
     *            String
     * @param p_fromDate
     *            String
     * @param p_toDate
     *            String
     * @throws BTSLBaseException
     * @author Ved Prakash
     */
    private void writeP2PDataInFile(ResultSet p_rs, String p_filePath, String p_fileName, String p_fromDate, String p_toDate) throws BTSLBaseException {
        final String METHOD_NAME = "writeP2PDataInFile";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME,
                " Entered:  p_rs=" + p_rs + " p_filePath : " + p_filePath + " p_fileName : " + p_fileName + " p_fromDate : " + p_fromDate + " p_toDate : " + p_toDate);
        }
        PrintWriter out = null;
        File newFile = null;
        String oldInterfaceID = null;
        String newInterfaceID = null;
        String fileData = null;
        String fileHeader = null;
        String fileFooter = null;
        int recordsWrittenInFile = 0;
        String finalFileName = null;
        boolean interFaceChangeFlag = true;
        float multFactor = 1;
        String interfaceID = null;
        try {
            String tmpReplace[] = null;
            String tempFinalStr = "";
            while (p_rs.next()) {
                fileData = p_rs.getString(1);
                newInterfaceID = p_rs.getString(2);
                interfaceID = p_rs.getString(3);
                tempFinalStr = "";
                if (!BTSLUtil.isNullString(FileCache.getValue(interfaceID, "MULTIPLICATION_FACTOR"))) {
                    multFactor = Float.parseFloat(FileCache.getValue(interfaceID, "MULTIPLICATION_FACTOR"));
                } else {
                    multFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
                }
                tmpReplace = fileData.split(",");
                for (int i = 0, j = tmpReplace.length; i < j; i++) {
                    if (((tmpReplace[i].trim()).toUpperCase()).startsWith("RBV")) {
                        tmpReplace[i] = (Integer.parseInt((tmpReplace[i].substring(tmpReplace[i].indexOf("=") + 1)).trim()) / multFactor) + "";
                    } else if (((tmpReplace[i].trim()).toUpperCase()).startsWith("RV")) {
                        tmpReplace[i] = (Integer.parseInt((tmpReplace[i].substring(tmpReplace[i].indexOf("=") + 1)).trim()) / multFactor) + "";
                    } else if (((tmpReplace[i].trim()).toUpperCase()).startsWith("TV")) {
                        tmpReplace[i] = (Integer.parseInt((tmpReplace[i].substring(tmpReplace[i].indexOf("=") + 1)).trim()) / multFactor) + "";
                    }
                    tempFinalStr = tempFinalStr + tmpReplace[i] + ",";
                }
                if (!BTSLUtil.isNullString(tempFinalStr)) {
                    fileData = tempFinalStr.substring(0, tempFinalStr.length() - 1);
                }
                if (recordsWrittenInFile != 0 && !oldInterfaceID.equals(newInterfaceID)) {
                    interFaceChangeFlag = true;
                    if (_isFooterShow) {
                        fileFooter = constructFileFooter(recordsWrittenInFile);
                        out.write(fileFooter);
                    }
                    out.flush();
                    if (out != null) {
                        out.close();
                    }
                }
                if (interFaceChangeFlag) {
                    interFaceChangeFlag = false;
                    oldInterfaceID = newInterfaceID;
                    recordsWrittenInFile = 0;
                    finalFileName = p_filePath + p_fileName + "_" + newInterfaceID + "_" + BTSLUtil.getFileNameStringFromDate(BTSLUtil.getDateFromDateString(p_fromDate,
                        _dateTimeFormat)) + ".csv";
                    newFile = new File(finalFileName);
                    out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
                    if (_log.isDebugEnabled()) {
                        _log.debug(METHOD_NAME, "  fileName=" + finalFileName);
                    }
                    if (_isHeaderShow) {
                        fileHeader = constructFileHeader(p_fromDate, p_toDate, _fileLabelP2P);
                        out.write(fileHeader + "\n");
                    }
                }
                out.write(fileData + "\n");
                recordsWrittenInFile++;
            }// end of while(p_rs.next())
             // if number of records are not zero then footer is appended as
             // file is deleted
            if (recordsWrittenInFile > 0) {
                if (_isFooterShow) {
                    fileFooter = constructFileFooter(recordsWrittenInFile);
                    out.write(fileFooter);
                }
            } else {
                if (out != null) {
                    out.close();
                }
                if (newFile != null) {
                	boolean isDeleted = newFile.delete();
                    if(isDeleted){
                     _log.debug(METHOD_NAME, "File deleted successfully");
                    }
                }
            }
        } catch (BTSLBaseException e) {
            _log.debug(METHOD_NAME, "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            if (newFile != null) {
            	boolean isDeleted = newFile.delete();
                if(isDeleted){
                 _log.debug(METHOD_NAME, "File deleted successfully");
                }
                newFile = null;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ReconciliationReport[writeP2PDataInFile]", "", "", "",
                "BTSLBaseException:" + e.getMessage());
            throw new BTSLBaseException("ReconciliationReport", METHOD_NAME, "Exception: " + e.getMessage());
        } catch (SQLException e) {
            _log.debug(METHOD_NAME, "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            if (newFile != null) {
            	boolean isDeleted = newFile.delete();
                if(isDeleted){
                 _log.debug(METHOD_NAME, "File deleted successfully");
                }
                newFile = null;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ReconciliationReport[writeP2PDataInFile]", "", "", "",
                "SQLException:" + e.getMessage());
            throw new BTSLBaseException("ReconciliationReport", METHOD_NAME, "Exception: " + e.getMessage());
        } catch (IOException e) {
            _log.debug(METHOD_NAME, "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            if (newFile != null) {
            	boolean isDeleted = newFile.delete();
                if(isDeleted){
                 _log.debug(METHOD_NAME, "File deleted successfully");
                }
                newFile = null;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ReconciliationReport[writeP2PDataInFile]", "", "", "",
                "IOException:" + e.getMessage());
            throw new BTSLBaseException("ReconciliationReport", METHOD_NAME, "Exception: " + e.getMessage());
        } catch (Exception e) {
            _log.debug(METHOD_NAME, "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            if (newFile != null) {
            	boolean isDeleted = newFile.delete();
                if(isDeleted){
                 _log.debug(METHOD_NAME, "File deleted successfully");
                }
                newFile = null;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ReconciliationReport[writeP2PDataInFile]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("ReconciliationReport", METHOD_NAME, "Exception: " + e.getMessage());
        } finally {
            if (out != null) {
                out.close();
            }
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting ");
            }
        }
    }

    /**
     * This method is used to constuct file header
     * 
     * @param p_fromDate
     *            String
     * @param p_toDate
     *            String
     * @param p_fileLabel
     *            String
     * @return String fileHeaderBuf
     * @throws BTSLBaseException
     * @author Ved Prakash
     */
    private String constructFileHeader(String p_fromDate, String p_toDate, String p_fileLabel) throws BTSLBaseException {
        final String METHOD_NAME = "constructFileHeader";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " Entered: p_fromDate :" + p_fromDate + " p_toDate :" + p_toDate + " p_fileLabel :" + p_fileLabel);
        }
        StringBuffer fileHeaderBuf = null;
        try {
            fileHeaderBuf = new StringBuffer("From Date=" + p_fromDate);
            fileHeaderBuf.append("\n" + "To Date=" + p_toDate);
            fileHeaderBuf.append("\n" + p_fileLabel);
            fileHeaderBuf.append("\n" + "[STARTDATA]");
        } catch (Exception e) {
            _log.error(METHOD_NAME, "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ReconciliationReport", METHOD_NAME, "Exception: " + e.getMessage());
        }
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Exiting: fileHeaderBuf.toString()=" + fileHeaderBuf.toString());
        }
        return fileHeaderBuf.toString();
    }

    /**
     * This method is used to constuct file footer
     * 
     * @param p_noOfRecords
     *            long
     * @return String fileFooterBuf
     * @throws BTSLBaseException
     * @author Ved Prakash
     */
    private String constructFileFooter(long p_noOfRecords) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("constructFileFooter", " Entered: ");
        }
        StringBuffer fileFooterBuf = null;
        try {
            fileFooterBuf = new StringBuffer("[ENDDATA]" + "\n");
            fileFooterBuf.append("Number of records=" + p_noOfRecords + "\n");
        } catch (Exception e) {
            final String METHOD_NAME = "constructFileHeader";
            _log.error(METHOD_NAME, "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ReconciliationReport", "constructFileFooter", " Exception: " + e.getMessage());
        }
        if (_log.isDebugEnabled()) {
            _log.debug("constructFileFooter", "Exiting: fileHeaderBuf.toString()=" + fileFooterBuf.toString());
        }
        return fileFooterBuf.toString();
    }

    /**
     * Method generateInterval
     * Used to generate the interval according to the Executed upto and current
     * date
     * The formula used is -> process
     * duration=(currentTime-ExecutedUpto-beforeInterval)
     * 
     * @param p_processStatusVO
     *            ProcessStatusVO
     * @param p_intervalHour
     *            int
     * @return intervalTimeVOList ArrayList
     * @throws Exception
     * @author Ved Prakash
     */
    public ArrayList generateInterval(ProcessStatusVO p_processStatusVO, int p_intervalHour) throws Exception {
        final String METHOD_NAME = "generateInterval";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered p_processStatusVO=" + p_processStatusVO.toString() + " p_intervalHour=" + p_intervalHour);
        }
        final ArrayList intervalTimeVOList = new ArrayList();
        IntervalTimeVO intervalTimeVO = null;
        try {
            Date currDate = new Date();
            final int beforeInterval = BTSLUtil.parseLongToInt( p_processStatusVO.getBeforeInterval() / 60);
            final long diffInMillis = currDate.getTime() - p_processStatusVO.getExecutedUpto().getTime() - beforeInterval;
            final long processDuration = diffInMillis / (1000 * 60 * 60);
            long parts = processDuration / p_intervalHour;
            if (processDuration % p_intervalHour > 0) {
                parts = parts + 1;
            }
            Date endDate = null;
            Timestamp startTime = null;
            Timestamp endTime = null;
            currDate = new Date(currDate.getYear(), currDate.getMonth(), currDate.getDate(), (currDate.getHours() - beforeInterval), currDate.getMinutes(), currDate
                .getSeconds());
            for (long i = 0; i <= parts; i++) {
                intervalTimeVO = new IntervalTimeVO();
                intervalTimeVO.setStartTime(BTSLUtil.getTimestampFromUtilDate(BTSLUtil.getSQLDateFromUtilDate(p_processStatusVO.getExecutedUpto())));
                startTime = intervalTimeVO.getStartTime();
                endDate = new Date(startTime.getYear(), startTime.getMonth(), startTime.getDate(), startTime.getHours() + p_intervalHour, startTime.getMinutes(), startTime
                    .getSeconds());
                endTime = BTSLUtil.getTimestampFromUtilDate(endDate);
                intervalTimeVO.setEndTime(endTime);
                p_processStatusVO.setExecutedUpto(endDate);
                if (endDate.after(currDate)) {
                    p_processStatusVO.setExecutedUpto(currDate);
                    intervalTimeVO.setEndTime(BTSLUtil.getTimestampFromUtilDate(currDate));
                    i = parts;
                }
                if (_log.isDebugEnabled()) {
                    _log.debug(METHOD_NAME, "starttime.getDate()=" + startTime.getDate() + " endDate.getDate()=" + endDate.getDate() + " Start Time=" + intervalTimeVO
                        .getStartTime() + " End Time=" + intervalTimeVO.getEndTime());
                }
                intervalTimeVOList.add(intervalTimeVO);
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME, "Exception : " + e.getMessage());
            throw new BTSLBaseException("ReconciliationReport", METHOD_NAME, "Exception :" + e.getMessage());
        }
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Exiting intervalTimeVOList=" + intervalTimeVOList.size());
        }
        return intervalTimeVOList;
    }
}

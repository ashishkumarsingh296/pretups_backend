package com.btsl.pretups.processes;

/**
 * @(#)DailyTechnicalReport.java
 *                               Copyright(c) 2007, Bharti Telesoft Ltd. All
 *                               Rights Reserved
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Author Date History
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Ankit Singhal 03/04/2007 Initial Creation
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               This class is used for generating a excel
 *                               report for technical report.
 */
import java.io.File;
import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.processes.businesslogic.DailyReportAnalysisDAO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

public class DailyTechnicalReport {
    private static Log _logger = LogFactory.getLog(DailyTechnicalReport.class.getName());
    private static Locale _locale = null;
    private static String finalFileName = null;
    private static Date _reportDate = null;

    /**
     * @param arg
     *            arg[0]=Conatnsts.props
     *            arg[1]=ProcessLogconfig.props
     *            arg[2]=Locale(0 or 1)
     *            arg[3]=Network (ALL or MO or DL...)
     *            arg[4]=report Date
     **/
    public static void main(String[] arg) {
        final String METHOD_NAME = "main";
        try {
            if (arg.length > 5 || arg.length < 4)// check the argument length
            {
                _logger.info(METHOD_NAME, "DailyTechnicalReport :: Not sufficient arguments, please pass Conatnsts.props ProcessLogconfig.props Locale Network ReportDate");
                return;
            }
            final File constantsFile = new File(arg[0]);
            if (!constantsFile.exists())// check file (Constants.props) exist or
            // not
            {
                _logger.debug(METHOD_NAME, "DailyTechnicalReport" + " Constants File Not Found at the path : " + arg[0]);
                return;
            }
            final File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists())// check file (ProcessLogConfig.props)
            // exist or not
            {
                _logger.debug(METHOD_NAME, "DailyTechnicalReport" + " ProcessLogConfig File Not Found at the path : " + arg[1]);
                return;
            }
            if (BTSLUtil.isNullString(arg[2]))// Locale check
            {
                _logger.info(METHOD_NAME, "DailyTechnicalReport :: Locale is missing ");
                return;
            }
            if (!BTSLUtil.isNumeric(arg[2]))// check the Process Interval is
            // numeric
            {
                _logger.debug(METHOD_NAME, "DailyTechnicalReport :: Invalid Locale " + arg[2] + " It should be 0 or 1");
                return;
            }
            if (Integer.parseInt(arg[2]) > 1 && Integer.parseInt(arg[2]) < 0) {
                _logger.debug(METHOD_NAME, "DailyTechnicalReport :: Invalid Locale " + arg[2] + " It should be 0 or 1");
                return;
            }
            if (BTSLUtil.isNullString(arg[3]))// Network code check
            {
                _logger.info(METHOD_NAME, "DailyTechnicalReport :: Network code is missing ");
                return;
            }
            if (arg.length == 5 && !BTSLUtil.isNullString(arg[4])) {
                try {
                    _reportDate = BTSLUtil.getDateFromDateString(arg[4], PretupsI.DATE_FORMAT);
                } catch (ParseException e1) {
                    _logger.info(METHOD_NAME, "DailyTechnicalReport :: Report date format should be dd/MM/yy");
                    _logger.errorTrace(METHOD_NAME, e1);
                    return;
                }
            } else {
                _reportDate = new Date();
            }
            _reportDate = BTSLUtil.addDaysInUtilDate(_reportDate, -1);
            // use to load the Constants.props and ProcessLogConfig.props files
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            LookupsCache.loadLookAtStartup();
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Main: Error in loading the Cache information.." + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DailyTechnicalReport[main]", "", "", "",
                "  Error in loading the Cache information");
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {
            _locale = LocaleMasterCache.getLocaleFromCodeDetails(arg[2]);
            if (_locale == null) {
                _locale = LocaleMasterCache.getLocaleFromCodeDetails("0");
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, "Error : Invalid Locale " + arg[2] + " It should be 0 or 1, thus using default locale code 0");
                }
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "DailyTechnicalReport[main]", "", "", "",
                    "  Message:  Invalid Locale " + arg[2] + " It should be 0 (EN) or 1 (OTH) ");
            }
        } catch (Exception e) {
            _logger.error(METHOD_NAME, " Invalid locale : " + arg[2] + " Exception:" + e.getMessage());
            _locale = new Locale("en", "US");
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "DailyTechnicalReport[main]", "", "", "",
                "  Message:  Not able to get the locale");
        }
        try {
            final DailyTechnicalReport dailyTechnicalReport = new DailyTechnicalReport();
            dailyTechnicalReport.process(arg[3]);
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException :" + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyTechnicalReport[main]", "", "", "",
                "BTSLBaseException:" + be.getMessage());
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception :" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyTechnicalReport[main]", "", "", "",
                "Exception:" + e.getMessage());
        } finally {
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * method process
     * This method start the process for technical report.
     * This report load the DAO value put in HashMap and pass it XLS write.
     * 
     * @param p_networkCode
     * @throws BTSLBaseException
     */
    private void process(String p_networkCode) throws BTSLBaseException {
        final String METHOD_NAME = "process";
        if (_logger.isDebugEnabled()) {
            _logger.debug("process", " Entered:  p_networkCode=" + p_networkCode);
        }
        Connection con = null;
        String filePath = null;
        try {
            filePath = Constants.getProperty("DAILY_TECHNICAL_REPORT_FILE_PATH");
            final File fileDir = new File(filePath);
            if (!fileDir.isDirectory()) {
                fileDir.mkdirs();
            }
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("process", "Exception " + e.getMessage());
            throw new BTSLBaseException(this, "process", "Unable to create directory =" + filePath, "error");
        }
        try {
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("process", " DATABASE Connection is NULL ");
                }
                throw new BTSLBaseException(this, "process", "Not able to get the connection");
            }
            final HashMap writeINxlsHM = new HashMap();
            final DailyReportAnalysisDAO dailyReportAnalysisDAO = new DailyReportAnalysisDAO();

            ArrayList loadC2SRechargeList = null;
            ArrayList loadP2PRechargeList = null;

            // load c2s recharge for all network
            loadC2SRechargeList = dailyReportAnalysisDAO.loadC2SServiceInterfaceRecharge(con, _reportDate, p_networkCode);
            if (loadC2SRechargeList != null && !loadC2SRechargeList.isEmpty()) {
                Collections.sort(loadC2SRechargeList);
            }
            writeINxlsHM.put("C2S_RECHARGE", loadC2SRechargeList);

            // load p2p recharge for all network
            loadP2PRechargeList = dailyReportAnalysisDAO.loadP2PServiceInterfaceRecharge(con, _reportDate, p_networkCode);
            if (loadP2PRechargeList != null && !loadP2PRechargeList.isEmpty()) {
                Collections.sort(loadP2PRechargeList);
            }
            writeINxlsHM.put("P2P_RECHARGE", loadP2PRechargeList);

            writeINxlsHM.put("REPORT_DATE", _reportDate);
            final String fileName = Constants.getProperty("DAILY_TECHNICAL_REPORT_FILE_PRIFIX") + BTSLUtil.getFileNameStringFromDate(new Date()) + ".xls";
            finalFileName = filePath + fileName;
            // Pass hashMap to write in XLS file
            final DailyReportWriteInXL dailyReportWriteInXL = new DailyReportWriteInXL();
            dailyReportWriteInXL.writeTechnicalExcel(writeINxlsHM, _locale, finalFileName);
        } catch (BTSLBaseException be) {
            _logger.error("process", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception be) {
            _logger.error("process", "Exception : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw new BTSLBaseException(this, "process", "Exception=" + be.getMessage());
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("process", "Exiting..... ");
            }
        }
    }
}
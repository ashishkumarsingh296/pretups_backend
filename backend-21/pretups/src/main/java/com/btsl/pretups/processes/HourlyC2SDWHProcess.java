package com.btsl.pretups.processes;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.filetransfer.FtpUtils_jftp;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
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

/**
 * @(#)HourlyC2SDWHProcess .java
 *                         Copyright(c) 2005, Bharti Telesoft Ltd.
 *                         All Rights Reserved
 *                         This class will call the MIS package to populate the
 *                         MIS data tables
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Ankit Singhal 27/11/2005 Initial creation
 *                         Ankit SInghal 30/01/2007 Modification
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 */

public class HourlyC2SDWHProcess {
    private static String message = "";
    private static ProcessBL _processBL = null;
    private static ProcessStatusVO _processStatusVO;
    private static Log _logger = LogFactory.getLog(HourlyC2SDWHProcess.class.getName());

    /**
     * ensures no instantiation
     */
    private HourlyC2SDWHProcess(){
    	
    }
    
    public static void main(String[] args) {
        Connection con = null;
        CallableStatement cstmt = null;
        Date nextDwhExecutionDateTime = null;
        String reportTo = null;
        final String prevDateStr = null;
        Date processedUpto = null;
        String processId = null;
        boolean statusOk = false;
        int beforeInterval = 0;
        final String METHOD_NAME = "main";
        try {
            if (args.length != 2) {
                _logger.info(METHOD_NAME, "Usage : HourlyC2SDWHProcess [Constants file] [LogConfig file]");
                return;
            }
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
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            ConfigServlet.destroyProcessCache();
            return;
        }

        try {
            // Make Connection
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("HourlyC2SDWHProcess[main]", "Not able to get Connection for HourlyC2SDWHProcess: ");
                }
                throw new SQLException();
            }
            processId = "HOURLYC2SDWH";
            // method call to check status of the process
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(con, processId);
            statusOk = _processStatusVO.isStatusOkBool();
            //beforeInterval = (int) _processStatusVO.getBeforeInterval() / (60 * 24);
            beforeInterval = BTSLUtil.parseLongToInt(_processStatusVO.getBeforeInterval()) / (60 * 24);
            
            if (statusOk) {
                con.commit();
                // method call to find maximum date till which process has been
                // executed
                processedUpto = _processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    final Calendar cal = BTSLDateUtil.getInstance();
                    cal.add(Calendar.HOUR, -beforeInterval);
                    nextDwhExecutionDateTime = cal.getTime();
                } else {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("HourlyC2SDWHProcess[main]", " Date till which process has been executed is not found.");
                    }
                    return;
                }
            }

            // c2s dwh process will be exceuted from the start till to date -1
            if (_logger.isDebugEnabled()) {
                _logger.debug("HourlyC2SDWHProcess[main]",
                    "From date=" + prevDateStr + " To Date=" + reportTo + " processedUpto.compareTo(nextDwhExecutionDateTime)=" + processedUpto
                        .compareTo(nextDwhExecutionDateTime));
            }

            // If process is already ran for the last day then do not run again
            if (processedUpto != null && processedUpto.compareTo(nextDwhExecutionDateTime) > 0) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "HourlyC2SDWHProcess[main]", "", "", "",
                    "Hourly C2S DWH already run for the date=" + String.valueOf(nextDwhExecutionDateTime));
                return;
            }
            try {
                final Date currentDate = new Date();
                /*final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH");
                sdf.setLenient(false); // this is required else it will convert
                reportTo = sdf.format(currentDate); // Current Date*/
            	reportTo = BTSLDateUtil.getSystemLocaleDate(currentDate, "dd/MM/yy HH");
            } catch (Exception e) {
                reportTo = "";
                _logger.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException("Not able to convert date to String");
            }
            if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                cstmt = con.prepareCall("{call  " + Constants.getProperty("currentschema") + ".C2S_DWH_PKG.C2S_DWH_PROC_DTRANGE(?,?,?,?,?)}");
            } else {
                cstmt = con.prepareCall("{call C2S_DWH_PKG.C2S_DWH_PROC_DTRANGE(?,?,?,?,?)}");

            }
            cstmt.registerOutParameter(1, Types.VARCHAR); // Message
            cstmt.registerOutParameter(2, Types.VARCHAR); // Message for log
            cstmt.registerOutParameter(3, Types.VARCHAR); // Sql Exception
            cstmt.registerOutParameter(4, Types.VARCHAR); // generated file
            // names
            cstmt.setString(5, reportTo);

            if (_logger.isDebugEnabled()) {
                _logger.debug("HourlyC2SDWHProcess[main]", "Before Exceuting Procedure reportTo=" + reportTo);
            }
            cstmt.executeUpdate();
            if (_logger.isDebugEnabled()) {
                _logger.debug("HourlyC2SDWHProcess[main]", "After Exceuting Procedure");
            }
            if (_logger.isDebugEnabled()) {
                _logger
                    .debug(
                        "HourlyC2SDWHProcess[main]",
                        "Parameters Returned : Status=" + cstmt.getString(1) + " , Message=" + cstmt.getString(2) + " ,Exception if any=" + cstmt.getString(3) + "generated file names=" + cstmt
                            .getString(4));
            }

            if (cstmt.getString(1) == null || !cstmt.getString(1).equalsIgnoreCase("SUCCESS")) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HourlyC2SDWHProcess[main]", "", "", "", cstmt
                    .getString(2) + " Exception if any:" + cstmt.getString(3) + "generated file names=" + cstmt.getString(4));
            } else {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "HourlyC2SDWHProcess[main]", "", "", "", cstmt
                    .getString(2) + " Exception if any:" + cstmt.getString(3) + "generated file names=" + cstmt.getString(4));
            }

            // download files from db server to monitor server using ftp
            try {
                // new
                // FtpUtils_jftp().doUpload("","","","PRE_PAID_CARD_20061204_63521.DAT",
                // "C:/", "/s/");
                String strFileNames = cstmt.getString(4);
                final String hostip = Constants.getProperty("HOURLYC2SDWH_HOSTIP");
                if (BTSLUtil.isNullString(hostip)) {
                    _logger.error("loadConstantParameters", " Could not find HOURLYC2SDWH_HOSTIP in the Constants file.");
                } else {
                    _logger.debug("HourlyC2SDWHProcess[main]", " HOURLYC2SDWH_HOSTIP = " + hostip);
                }
                ;
                final String username = Constants.getProperty("HOURLYC2SDWH_USERNAME");
                if (BTSLUtil.isNullString(username)) {
                    _logger.error("loadConstantParameters", " Could not find HOURLYC2SDWH_USERNAME in the Constants file.");
                } else {
                    _logger.debug("HourlyC2SDWHProcess[main]", " HOURLYC2SDWH_USERNAME = " + username);
                }
                ;
                ;
                final String password = Constants.getProperty("HOURLYC2SDWH_PASSWORD");
                if (BTSLUtil.isNullString(password)) {
                    _logger.error("loadConstantParameters", " Could not find HOURLYC2SDWH_PASSWORD in the Constants file.");
                } else {
                    _logger.debug("HourlyC2SDWHProcess[main]", " HOURLYC2SDWH_PASSWORD = " + password);
                }
                ;
                ;
                final String serverdir = Constants.getProperty("HOURLYC2SDWH_SERVERDIR");
                if (BTSLUtil.isNullString(serverdir)) {
                    _logger.error("loadConstantParameters", " Could not find HOURLYC2SDWH_SERVERDIR in the Constants file.");
                } else {
                    _logger.debug("HourlyC2SDWHProcess[main]", " HOURLYC2SDWH_SERVERDIR = " + serverdir);
                }
                ;
                ;
                final String localdir = Constants.getProperty("HOURLYC2SDWH_LOCALDIR");
                if (BTSLUtil.isNullString(localdir)) {
                    _logger.error("loadConstantParameters", " Could not find HOURLYC2SDWH_LOCALDIR in the Constants file.");
                } else {
                    _logger.debug("HourlyC2SDWHProcess[main]", " HOURLYC2SDWH_LOCALDIR = " + localdir);
                }
                final String destHostIp = Constants.getProperty("HOURLYC2SDWH_DESTHOSTIP");
                if (BTSLUtil.isNullString(destHostIp)) {
                    _logger.error("loadConstantParameters", " Could not find HOURLYC2SDWH_DESTHOSTIP in the Constants file.");
                } else {
                    _logger.debug("HourlyC2SDWHProcess[main]", " HOURLYC2SDWH_DESTHOSTIP = " + destHostIp);
                }
                ;
                final String destusername = Constants.getProperty("HOURLYC2SDWH_DESTUSERNAME");
                if (BTSLUtil.isNullString(destusername)) {
                    _logger.error("loadConstantParameters", " Could not find HOURLYC2SDWH_DESTUSERNAME in the Constants file.");
                } else {
                    _logger.debug("HourlyC2SDWHProcess[main]", " HOURLYC2SDWH_DESTUSERNAME = " + destusername);
                }
                ;
                ;
                final String destpassword = Constants.getProperty("HOURLYC2SDWH_DESTPASSWORD");
                if (BTSLUtil.isNullString(destpassword)) {
                    _logger.error("loadConstantParameters", " Could not find HOURLYC2SDWH_DESTPASSWORD in the Constants file.");
                } else {
                    _logger.debug("HourlyC2SDWHProcess[main]", " HOURLYC2SDWH_DESTPASSWORD = " + destpassword);
                }
                ;
                ;
                final String destserverdir = Constants.getProperty("HOURLYC2SDWH_DESTSERVERDIR");
                if (BTSLUtil.isNullString(destserverdir)) {
                    _logger.error("loadConstantParameters", " Could not find HOURLYC2SDWH_DESTSERVERDIR in the Constants file.");
                } else {
                    _logger.debug("HourlyC2SDWHProcess[main]", " HOURLYC2SDWH_DESTSERVERDIR = " + destserverdir);
                }

                strFileNames = strFileNames.substring(0, strFileNames.length() - 1);
                final FtpUtils_jftp ftp = new FtpUtils_jftp();
                final StringTokenizer tokenizer = new StringTokenizer(strFileNames, ",");
                String str = null;
                while (tokenizer.hasMoreTokens()) {
                    str = tokenizer.nextToken().trim();
                    try {
                        ftp.doDownload(hostip, username, password, str, localdir, serverdir);
                    } catch (Exception e) {
                        _logger.errorTrace(METHOD_NAME, e);
                    }
                    try {
                        ftp.doUpload(destHostIp, destusername, destpassword, str, localdir, destserverdir);
                    } catch (Exception e) {
                        _logger.errorTrace(METHOD_NAME, e);
                    }
                }

            } catch (Exception exception) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "HourlyC2SDWHProcess[main]", "", "", "",
                    "Hourly C2S DWH file download not completed properly " + exception.getMessage());
                _logger.errorTrace(METHOD_NAME, exception);
            }

            // send the message as SMS
            final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            message = BTSLUtil.NullToString(cstmt.getString(2)) + BTSLUtil.NullToString(cstmt.getString(3));
            final String msisdnString = new String(Constants.getProperty("adminmobile"));
            final String[] msisdn = msisdnString.split(",");

            for (int i = 0; i < msisdn.length; i++) {
                final PushMessage pushMessage = new PushMessage(msisdn[i], message, null, null, locale);
                pushMessage.push();
            }
            try {
                Thread.sleep(5);
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
        } catch (Exception e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception sqlex) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("HourlyC2SDWHProcess", "HourlyC2SDWHProcess::Exception while roll back" + sqlex);
                }
                _logger.errorTrace(METHOD_NAME, sqlex);
            }
            message = e.getMessage();
            // send the message as SMS
            _logger.errorTrace(METHOD_NAME, e);
        } finally {
            try {
                if (statusOk) {
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
                }
                if (cstmt != null) {
                    cstmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("HourlyC2SDWHProcess", "Exception while closing statement in HourlyC2SDWHProcess method ");
                }
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * @param p_con
     *            Connection
     * @param p_processId
     *            String
     * @return int
     */
    private static int markProcessStatusAsComplete(Connection p_con, String p_processId) {
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
            updateCount = processStatusDAO.updateProcessDetailForMis(p_con, _processStatusVO);
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            if (_logger.isDebugEnabled()) {
                _logger.debug("markProcessStatusAsComplete", "Exception= " + e.getMessage());
            }
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;

    }
}

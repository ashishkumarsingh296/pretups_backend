package com.btsl.pretups.processes;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
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
 * @(#)VoucherMisDataProcessing .java
 *                              Copyright(c) 2005, Bharti Telesoft Ltd.
 *                              All Rights Reserved
 *                              This class will call the MIS package to populate
 *                              the MIS data tables
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Amit Raheja 19/06/2012 Initial creation
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 */

public class VoucherMisDataProcessing {
    private static String message = "";
    private static ProcessBL _processBL = null;
    private static ProcessStatusVO _processStatusVO;
    private static Log _logger = LogFactory.getLog(C2sMisDataProcessingNew.class.getName());
    private static final String CLASS_NAME = "VoucherMisDataProcessing";

    /**
     * to ensure no class instantiation 
     */
    private VoucherMisDataProcessing(){
    	
    }
    public static void main(String[] args) {
        Connection con = null;
        final String METHOD_NAME = "main";
        try {
            if (args.length != 2) {
                _logger.info(METHOD_NAME, "Usage : VoucherMisDataProcessing [Constants file] [LogConfig file]");
                return;
            }
            final File constantsFile = Constants.validateFilePath(args[0]);
            if (!constantsFile.exists()) {
                _logger.info(METHOD_NAME, " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = Constants.validateFilePath(args[1]);
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
                    _logger.debug("VoucherMisDataProcessing[main]", "Not able to get Connection for VoucherMisDataProcessing: ");
                }
                throw new SQLException();
            }
            process(con);

        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            ConfigServlet.destroyProcessCache();
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("VoucherMisDataProcessing[main]", "Exiting");
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    private static void process(Connection con) {
        final String METHOD_NAME = "process";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entered:");
        }
        CallableStatement cstmt = null;
        Date currentDate = null;
        String reportTo = null;
        String prevDateStr = null;
        Date processedUpto = null;
        String processId = null;
        boolean statusOk = false;
        int beforeInterval = 0;
        try {

            processId = ProcessI.VMSMIS;
            // method call to check status of the process
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(con, processId);
            statusOk = _processStatusVO.isStatusOkBool();
            beforeInterval = BTSLUtil.parseLongToInt( _processStatusVO.getBeforeInterval() / (60 * 24));
            if (statusOk) {
                con.commit();
                // method call to find maximum date till which process has been
                // executed
                processedUpto = _processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    // adding 1 in processed upto date as we have to start from
                    // the next day till which process has been executed
                    processedUpto = BTSLUtil.addDaysInUtilDate(processedUpto, 1);
                    final Calendar cal = BTSLDateUtil.getInstance();
                    currentDate = cal.getTime(); // Current Date
                    currentDate = BTSLUtil.addDaysInUtilDate(currentDate, -beforeInterval);
                } else {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("VoucherMisDataProcessing[process]", " Date till which process has been executed is not found.");
                    }
                    return;
                }
            }
            try {
                final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT);
                sdf.setLenient(false); // this is required else it will convert
                reportTo = sdf.format(currentDate); // Current Date
                prevDateStr = sdf.format(processedUpto);// Last MIS Done Date +1
            } catch (Exception e) {
                reportTo = "";
                prevDateStr = "";
                _logger.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,"Not able to convert date to String");
            }

            // Mis process will be exceuted from the start till to date -1
            if (_logger.isDebugEnabled()) {
                _logger.debug("VoucherMisDataProcessing[process]",
                    "From date=" + prevDateStr + " To Date=" + reportTo + " processedUpto.compareTo(currentDate)=" + processedUpto.compareTo(currentDate));
            }

            // If process is already ran for the last day then do not run again
            if (processedUpto != null && processedUpto.compareTo(currentDate) > 0) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherMisDataProcessing[process]", "", "", "",
                    "Daily C2S MIS already run for the date=" + String.valueOf(currentDate));
                return;
            }

            cstmt = con.prepareCall("{call VMS_Mis_Data_Pkg.sp_get_mis_data_dtrange(?,?,?,?,?)}");
            cstmt.registerOutParameter(3, Types.VARCHAR); // Message
            cstmt.registerOutParameter(4, Types.VARCHAR); // Message for log
            cstmt.registerOutParameter(5, Types.VARCHAR); // Sql Exception
            cstmt.setString(1, prevDateStr);
            cstmt.setString(2, reportTo);

            if (_logger.isDebugEnabled()) {
                _logger.debug("VoucherMisDataProcessing[process]", "Before Exceuting Procedure");
            }
            cstmt.executeUpdate();
            if (_logger.isDebugEnabled()) {
                _logger.debug("VoucherMisDataProcessing[process]", "After Exceuting Procedure");
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("VoucherMisDataProcessing[process]",
                    "Parameters Returned : Status=" + cstmt.getString(3) + " , Message=" + cstmt.getString(4) + " ,Exception if any=" + cstmt.getString(5));
            }

            if (cstmt.getString(3) == null || !"SUCCESS".equalsIgnoreCase(cstmt.getString(3))) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherMisDataProcessing[process]", "", "", "",
                    cstmt.getString(4) + " Exception if any:" + cstmt.getString(5));
            } else {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherMisDataProcessing[process]", "", "", "",
                    cstmt.getString(4) + " Exception if any:" + cstmt.getString(5));
            }

            // send the message as SMS
            final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            message = BTSLUtil.NullToString(cstmt.getString(4)) + BTSLUtil.NullToString(cstmt.getString(5));
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
            _logger.errorTrace(METHOD_NAME, e);
            try {
                con.rollback();
            } catch (Exception sqlex) {
                _logger.errorTrace(METHOD_NAME, sqlex);
            }
            message = e.getMessage();
            // send the message as SMS
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
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
                _logger.info(METHOD_NAME, "Exception while closing statement in VoucherMisDataProcessing[process] method ");
            }
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
        }

    }

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

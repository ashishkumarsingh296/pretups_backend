package com.btsl.pretups.processes;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.QueryConstants;
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
 * @(#)P2pMisDataProcessing .java
 *                          Copyright(c) 2005, Bharti Telesoft Ltd.
 *                          All Rights Reserved
 *                          This class will call the MIS package to populate the
 *                          MIS data tables at the end of night
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Author Date History
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Rachna Gupta 06/07/2005 Initital Creation
 *                          Ankit Singhal 27/11/2005 Modification
 *                          Ankit SInghal 30/01/2007 Modification
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 */

public class P2pMisDataProcessing {
    private static String message = new String();
    private static ProcessBL _processBL = null;
    private static ProcessStatusVO _processStatusVO;
    private static final Log _logger = LogFactory.getLog(P2pMisDataProcessing.class.getName());

    /**
     * to ensure no class instantiation 
     */
    private P2pMisDataProcessing(){
    	
    }
    public static void main(String[] args) {
        Connection con = null;
        CallableStatement cstmt = null;
        Date currentDate = null;
        String reportTo = null;
        String prevDateStr = null;
        Date processedUpto = null;
        String processId = null;
        boolean statusOk = false;
        int beforeInterval = 0;
        final String METHOD_NAME = "main";
        ResultSet rs = null;
        try {
            if (args.length != 2) {
                _logger.info(METHOD_NAME, "Usage : P2pMisDataProcessing [Constants file] [LogConfig file]");
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
                    _logger.debug("P2pMisDataProcessing[main]", " Not able to get Connection in MisDataProcessing: ");
                }
                throw new SQLException();
            }
            processId = ProcessI.P2PMIS;
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
                    _logger.info(METHOD_NAME, "C2sMisDataProcessing:: Date till which process has been executed is not found.");
                    return;
                }
            }
            try {
                /*final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT);
                sdf.setLenient(false); // this is required else it will convert
                reportTo = sdf.format(currentDate); // Current Date
                prevDateStr = sdf.format(processedUpto);// Last MIS Done Date +1*/ 
            	reportTo = BTSLDateUtil.getSystemLocaleDate(currentDate, PretupsI.DATE_FORMAT);
            	prevDateStr = BTSLDateUtil.getSystemLocaleDate(processedUpto, PretupsI.DATE_FORMAT);
            } catch (Exception e) {
                reportTo = "";
                prevDateStr = "";
                _logger.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException("Not able to convert date to String");
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("P2pMisDataProcessing[main]", "From date=" + prevDateStr + " To Date=" + reportTo + " processedUpto.compareTo(currentDate)=" + processedUpto
                    .compareTo(currentDate));
            }

            // If process is already ran for the last day then do not run again
            if (processedUpto != null && processedUpto.compareTo(currentDate) > 0) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "P2pMisDataProcessing[main]", "", "", "",
                    "P2P MIS already run for the date=" + String.valueOf(currentDate));
                return;
            }

            String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);

            if (_logger.isDebugEnabled()) {
                _logger.debug("P2pMisDataProcessing[main]", "Before Exceuting Procedure");
            }
            
            if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                cstmt = con.prepareCall("{call " + Constants.getProperty("currentschema") + ".Pkg_Mis_Summary_Reports.SP_GET_MIS_DATA_DTRANGE(?,?,?,?,?)}");
            }else if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
            	  cstmt = con.prepareCall("{call mis_summary_report_main_sp_get_mis_data_dtrange(?,?)}");
            }
            else {
                cstmt = con.prepareCall("{call Pkg_Mis_Summary_Reports.SP_GET_MIS_DATA_DTRANGE(?,?,?,?,?)}");
            }
            cstmt.setString(1, prevDateStr);
            cstmt.setString(2, reportTo);
            if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
            	 rs=cstmt.executeQuery();
               	if(rs.next()){
                	String status=rs.getString("aov_message");
                	String messageforlog=rs.getString("aov_messageforlog");
                	String sqlerrmsgforlog=rs.getString("aov_sqlerrmsgforlog");
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("P2pMisDataProcessing[main]",
                            "Parameters Returned : Status=" + status + " , Message=" + messageforlog + " ,Exception if any=" + sqlerrmsgforlog);
                    }

                	if("SUCCESS".equalsIgnoreCase(status))
                		con.commit();
                	if (status == null || !("SUCCESS".equalsIgnoreCase(status))) {
                         EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2pMisDataProcessing[main]",
                             "", "", "", messageforlog + " Exception if any:" + sqlerrmsgforlog);
                    } else {
                         EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "P2pMisDataProcessing[main]", "",
                             "", "", messageforlog + " Exception if any:" + sqlerrmsgforlog);
                    }
                     message = BTSLUtil.NullToString(messageforlog) + BTSLUtil.NullToString(sqlerrmsgforlog);
                	}
            }else{
            cstmt.registerOutParameter(3, Types.VARCHAR); // Message
            cstmt.registerOutParameter(4, Types.VARCHAR); // Message for log
            cstmt.registerOutParameter(5, Types.VARCHAR); // Sql Exception
            cstmt.executeUpdate();

            if (_logger.isDebugEnabled()) {
                _logger.debug("P2pMisDataProcessing[main]",
                    "Parameters Returned : Status=" + cstmt.getString(3) + " , Message=" + cstmt.getString(4) + " ,Exception if any=" + cstmt.getString(5));
            }

            if (cstmt.getString(3) == null || !cstmt.getString(3).equalsIgnoreCase("SUCCESS")) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2pMisDataProcessing[main]", "", "", "", cstmt
                    .getString(4) + " Exception if any:" + cstmt.getString(5));
            } else {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "P2pMisDataProcessing[main]", "", "", "", cstmt
                    .getString(4) + " Exception if any:" + cstmt.getString(5));
            }
            message = BTSLUtil.NullToString(cstmt.getString(4)) + BTSLUtil.NullToString(cstmt.getString(5));
            
            }
            
            if (_logger.isDebugEnabled()) {
                _logger.debug("P2pMisDataProcessing[main]", "After Exceuting Procedure");
            }
            
            // send the message as SMS
            final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
           
            final String msisdnString = Constants.getProperty("adminmobile");
            final String[] msisdn = msisdnString.split(",");

            for (int i = 0; i < msisdn.length; i++) {
                final PushMessage pushMessage = new PushMessage(msisdn[i], message, null, null, locale);
                pushMessage.push();
            }
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            try {
                if (con != null) {
                    con.rollback();
                }
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
                            if (con != null) {
                                con.commit();
                            }
                        } catch (Exception e) {
                            _logger.errorTrace(METHOD_NAME, e);
                        }
                    } else {
                        try {
                            if (con != null) {
                                con.rollback();
                            }
                        } catch (Exception e) {
                            _logger.errorTrace(METHOD_NAME, e);
                        }
                    }
                }
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (Exception ex) {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug(METHOD_NAME, "Exception closing Callable statement ");
                    }
                    _logger.errorTrace(METHOD_NAME, ex);
                }
                try {
                    if (cstmt != null) {
                        cstmt.close();
                    }
                } catch (Exception ex) {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug(METHOD_NAME, "Exception closing Callable statement ");
                    }
                    _logger.errorTrace(METHOD_NAME, ex);
                }
                
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
                _logger.info(METHOD_NAME, "Exception while closing statement in P2pMisDataProcessing method ");
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

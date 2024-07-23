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
 * @(#)LMBMisDataProcessing .java
 *                          Copyright(c) 2010, Comviva Technologies Ltd.
 *                          All Rights Reserved
 *                          This class will call the LMB MIS package to populate
 *                          the LMB MIS data tables at the end of night
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Author Date History
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 *                          Nand Kishor 08/10/2010 Initital Creation
 *                          ----------------------------------------------------
 *                          ---------------------------------------------
 */

public class LMBMisDataProcessing {
    public static String message = new String();
  
    private static ProcessBL _processBL = null;
    private static ProcessStatusVO _processStatusVO;
    private static final Log _logger = LogFactory.getLog(LMBMisDataProcessing.class.getName());

    /**
     * ensures no instantiation
     */
    private LMBMisDataProcessing(){
    	
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
                _logger.info(METHOD_NAME, "Usage : LMBMisDataProcessing [Constants file] [LogConfig file]");
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
                    _logger.debug("LMBMisDataProcessing[main]", " Not able to get Connection in LMBMisDataProcessing: ");
                }
                throw new SQLException();
            }
            processId = ProcessI.SOSMIS;
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
                    _logger.info(METHOD_NAME, "LMBMisDataProcessing:: Date till which process has been executed is not found.");
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
                _logger.debug("LMBMisDataProcessing[main]", "From date=" + prevDateStr + " To Date=" + reportTo + " processedUpto.compareTo(currentDate)=" + processedUpto
                    .compareTo(currentDate));
            }

            // If process is already ran for the last day then do not run again
            if (processedUpto != null && processedUpto.compareTo(currentDate) > 0) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LMBMisDataProcessing[main]", "", "", "",
                    "LMB MIS already run for the date=" + String.valueOf(currentDate));
                return;
            }
            // Pkg_Lmb_Mis_Summary_Reports.SP_GET_LMBMIS_DATA_DTRANGE
            String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
            String status=null;
            String otherMessage= null;
            String message1=null;
            if (_logger.isDebugEnabled())
                _logger.debug("LMBMisDataProcessing[main]", "Before Exceuting Procedure");
            if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                cstmt = con.prepareCall("{call lmb_mis_summary_report_sp_get_lmbmis_data_dtrange(?,?)}");
                cstmt.setString(1, prevDateStr);
                cstmt.setString(2, reportTo);
                rs = cstmt.executeQuery();
               if(rs.next()){
                 status =rs.getString("aov_message");
                 message1 = rs.getString("aov_messageforlog");
                 otherMessage = rs.getString("aov_sqlerrmsgforlog");
                 }
               message=message1+otherMessage;
            }
            else{
            cstmt = con.prepareCall("{call Pkg_Lmb_Mis_Summary_Reports.SP_GET_LMBMIS_DATA_DTRANGE(?,?,?,?,?)}");
            cstmt.setString(1, prevDateStr);
            cstmt.setString(2, reportTo);
            cstmt.registerOutParameter(3, Types.VARCHAR); // Message
            cstmt.registerOutParameter(4, Types.VARCHAR); // Message for log
            cstmt.registerOutParameter(5, Types.VARCHAR); // Sql Exception
            cstmt.executeUpdate();
            message = BTSLUtil.NullToString(cstmt.getString(4)) + BTSLUtil.NullToString(cstmt.getString(5));
            }
            
            
            if (_logger.isDebugEnabled()) 
                _logger.debug("LMBMisDataProcessing[main]", "After Exceuting Procedure");
            
            if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
            	 if (_logger.isDebugEnabled()) 
                     _logger.debug("LMBMisDataProcessing[main]","Parameters Returned : Status=" + status + " , Message=" +message1 + " ,Exception if any=" + otherMessage);

            	  if (status == null || !PretupsI.SUCCESS.equalsIgnoreCase(status)) {
            		  if (con != null) 
                          con.rollback();
                      EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LMBMisDataProcessing[main]", "", "", "", message1 + " Exception if any:" +otherMessage);
                      
            	  } else{
            		  if (con != null)
                          con.commit();
                      EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LMBMisDataProcessing[main]", "", "", "", message1 + " Exception if any:" +otherMessage);
            	  }
            }
            else{
            if (_logger.isDebugEnabled()) 
                _logger.debug("LMBMisDataProcessing[main]","Parameters Returned : Status=" + cstmt.getString(3) + " , Message=" + cstmt.getString(4) + " ,Exception if any=" + cstmt.getString(5));

            if (cstmt.getString(3) == null || !PretupsI.SUCCESS.equalsIgnoreCase( cstmt.getString(3))) 
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LMBMisDataProcessing[main]", "", "", "", cstmt
                    .getString(4) + " Exception if any:" + cstmt.getString(5));
            else
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LMBMisDataProcessing[main]", "", "", "", cstmt
                    .getString(4) + " Exception if any:" + cstmt.getString(5));
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
                if (cstmt != null) {
                    cstmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  _logger.error("An error occurred closing statement.", e);
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
     * Marks the MIS Process Status as Complete
     * 
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

package com.btsl.pretups.processes;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
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
 * @(#)C2sMisDataProcessingNew .java
 *                             Copyright(c) 2005, Bharti Telesoft Ltd.
 *                             All Rights Reserved
 *                             This class will call the MIS package to populate
 *                             the MIS data tables
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Shishupal Singh 29/07/2011 Initial creation
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 */

public class C2sMisDataProcessingNew {
    public static String message = "";
    private static ProcessBL _processBL = null;
    private static ProcessStatusVO _processStatusVO;
    private static final Log LOGGER = LogFactory.getLog(C2sMisDataProcessingNew.class.getName());

    /**
     * ensures no instantiation
     */
    private C2sMisDataProcessingNew(){
    	
    }
    
    public static void main(String[] args) {
        Connection con = null;
        final String METHOD_NAME = "main";
        try {
            if (args.length != 2) {
                System.out.println("Usage : C2sMisDataProcessingNew [Constants file] [LogConfig file]");
                return;
            }
            final File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
                System.out.println(" Constants file not found on provided location.");
                return;
            }
            final File logconfigFile = new File(args[1]);
            if (!logconfigFile.exists()) {
                System.out.println(" Logconfig file not found on provided location.");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        } catch (Exception e) {
            System.out.println("Exception thrown in C2sMisDataProcessingNew: Not able to load files" + e);
            ConfigServlet.destroyProcessCache();
            return;
        }

        try {
            // Make Connection
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("C2sMisDataProcessingNew[main]", "Not able to get Connection for C2sMisDataProcessingNew: ");
                }
                throw new BTSLBaseException("Not able to get Connection for C2sMisDataProcessingNew: "); 
            }
            try{dailyC2sMisExecution(con);}catch(Exception e){ LOGGER.errorTrace(METHOD_NAME, e);}
            monthlyC2sMisExecution(con);
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("C2sMisDataProcessingNew[main]", "Exception thrown in C2sMisDataProcessingNew: Not able to load files" + e);
            }
            LOGGER.errorTrace(METHOD_NAME, e);
            ConfigServlet.destroyProcessCache();
        } finally {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("C2sMisDataProcessingNew[main]", "Exiting");
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                LOGGER.errorTrace(METHOD_NAME, e);
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    private static void dailyC2sMisExecution(Connection con) {
        final String METHOD_NAME = "dailyC2sMisExecution";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(METHOD_NAME, " Entered:");
        }
        CallableStatement cstmt = null;
        Date currentDate = null;
        String reportTo = null;
        String prevDateStr = null;
        Date processedUpto = null;
        String processId = null;
        boolean statusOk = false;
        int beforeInterval = 0;
        ResultSet rs = null;
        try {

            processId = ProcessI.C2SMIS;
            // method call to check status of the process
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(con, processId);
            statusOk = _processStatusVO.isStatusOkBool();
            beforeInterval = BTSLUtil.parseLongToInt( _processStatusVO.getBeforeInterval() / (60 * 24) );
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
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("C2sMisDataProcessingNew["+METHOD_NAME+"]", " Date till which process has been executed is not found.");
                    }
                    return;
                }
            }
            try {
//                final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT);
//                sdf.setLenient(false); // this is required else it will convert
                /*reportTo = BTSLDateUtil.getSystemLocaleDate(sdf.format(currentDate)); // Current Date
                prevDateStr = BTSLDateUtil.getSystemLocaleDate(sdf.format(processedUpto));// Last MIS Done Date +1
*/                
                reportTo = BTSLDateUtil.getSystemLocaleDate(currentDate, PretupsI.DATE_FORMAT); // Current Date
                prevDateStr = BTSLDateUtil.getSystemLocaleDate(processedUpto, PretupsI.DATE_FORMAT);// Last MIS Done Date +1
            } catch (Exception e) {
                reportTo = "";
                prevDateStr = "";
                LOGGER.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException("Not able to convert date to String");
            }

            // Mis process will be exceuted from the start till to date -1
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("C2sMisDataProcessingNew["+METHOD_NAME+"]",
                    "From date=" + prevDateStr + " To Date=" + reportTo + " processedUpto.compareTo(currentDate)=" + processedUpto.compareTo(currentDate));
            }

            // If process is already ran for the last day then do not run again
            if (processedUpto != null && processedUpto.compareTo(currentDate) > 0) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2sMisDataProcessingNew["+METHOD_NAME+"]", "",
                    "", "", "Daily C2S MIS already run for the date=" + String.valueOf(currentDate));
                return;
            }
            String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
            if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
            	cstmt = con.prepareCall("{call mis_sp_get_mis_data_dtrange(?,?)}");
            	cstmt.setString(1, prevDateStr);
                cstmt.setString(2, reportTo);
                
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("C2sMisDataProcessingNew["+METHOD_NAME+"]", "Before Exceuting Procedure");
                }
                 rs=cstmt.executeQuery();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("C2sMisDataProcessingNew["+METHOD_NAME+"]", "After Exceuting Procedure");
                }
                if(rs.next()){
                	String status=rs.getString("aov_message");
                	String messageforlog=rs.getString("aov_messageforlog");
                	String sqlerrmsgforlog=rs.getString("aov_sqlerrmsgforlog");
                if("SUCCESS".equalsIgnoreCase(status))
                	con.commit();
                
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("C2sMisDataProcessingNew["+METHOD_NAME+"]",
                        "Parameters Returned : Status=" + status + " , Message=" + messageforlog + " ,Exception if any=" + sqlerrmsgforlog);
                }
                if (status == null || !("SUCCESS".equalsIgnoreCase(status))) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2sMisDataProcessingNew["+METHOD_NAME+"]",
                        "", "", "", messageforlog + " Exception if any:" + sqlerrmsgforlog);
                } else {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2sMisDataProcessingNew["+METHOD_NAME+"]", "",
                        "", "", messageforlog + " Exception if any:" + sqlerrmsgforlog);
                }
                message = BTSLUtil.NullToString(messageforlog) + BTSLUtil.NullToString(sqlerrmsgforlog);
                }
               
            }
            else{
            cstmt = con.prepareCall("{call MIS_DATA_PKG_NEW.sp_get_mis_data_dtrange(?,?,?,?,?)}");
            cstmt.registerOutParameter(3, Types.VARCHAR); // Message
            cstmt.registerOutParameter(4, Types.VARCHAR); // Message for log
            cstmt.registerOutParameter(5, Types.VARCHAR); // Sql Exception
            cstmt.setString(1, prevDateStr);
            cstmt.setString(2, reportTo);
            
           

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("C2sMisDataProcessingNew["+METHOD_NAME+"]", "Before Exceuting Procedure");
            }
            cstmt.executeUpdate();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("C2sMisDataProcessingNew["+METHOD_NAME+"]", "After Exceuting Procedure");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("C2sMisDataProcessingNew["+METHOD_NAME+"]",
                    "Parameters Returned : Status=" + cstmt.getString(3) + " , Message=" + cstmt.getString(4) + " ,Exception if any=" + cstmt.getString(5));
            }

            if (cstmt.getString(3) == null || !("SUCCESS".equalsIgnoreCase(cstmt.getString(3)))) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2sMisDataProcessingNew["+METHOD_NAME+"]",
                    "", "", "", cstmt.getString(4) + " Exception if any:" + cstmt.getString(5));
            } else {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2sMisDataProcessingNew["+METHOD_NAME+"]", "",
                    "", "", cstmt.getString(4) + " Exception if any:" + cstmt.getString(5));
            }
            message = BTSLUtil.NullToString(cstmt.getString(4)) + BTSLUtil.NullToString(cstmt.getString(5));
            
            }
            // send the message as SMS
            final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
           
            final String msisdnString = new String(Constants.getProperty("adminmobile"));
            final String[] msisdn = msisdnString.split(",");

            for (int i = 0; i < msisdn.length; i++) {
                final PushMessage pushMessage = new PushMessage(msisdn[i], message, null, null, locale);
                pushMessage.push();
            }
            try {
                Thread.sleep(5);
            } catch (Exception e) {
                LOGGER.errorTrace(METHOD_NAME, e);
            }
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (Exception sqlex) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("C2sMisDataProcessingNew", "C2sMisDataProcessingNew["+METHOD_NAME+"]::Exception while roll back" + sqlex);
                }
                LOGGER.errorTrace(METHOD_NAME, sqlex);
            }
            message = e.getMessage();
            // send the message as SMS
            LOGGER.errorTrace(METHOD_NAME, e);
        } finally {
            try {
                if (statusOk) {
                    if (markProcessStatusAsComplete(con, processId) == 1) {
                        try {
                            con.commit();
                        } catch (Exception e) {
                            LOGGER.errorTrace(METHOD_NAME, e);
                        }
                    } else {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            LOGGER.errorTrace(METHOD_NAME, e);
                        }
                    }
                }
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (Exception e) {
                    LOGGER.errorTrace(METHOD_NAME, e);
                }
                
                if (cstmt != null) {
                    cstmt.close();
                }
            } catch (Exception ex) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("C2sMisDataProcessingNew", "Exception while closing statement in C2sMisDataProcessingNew["+METHOD_NAME+"] method ");
                }
                LOGGER.errorTrace(METHOD_NAME, ex);
            }
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                LOGGER.errorTrace(METHOD_NAME, e);
            }
        }

    }

    private static void monthlyC2sMisExecution(Connection con) {
        final String METHOD_NAME = "monthlyC2sMisExecution";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(METHOD_NAME, " Entered:");
        }
        CallableStatement cstmt = null;
        Date currentDate = null;
        String reportTo = null;
        String prevDateStr = null;
        Date processedUpto = null;
        String processId = null;
        boolean statusOk = false;
        int beforeInterval = 0;
        ResultSet rs = null;
        try {
        	String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
            processId = "C2SMISMON";
            // method call to check status of the process
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(con, processId);
            statusOk = _processStatusVO.isStatusOkBool();
            beforeInterval = BTSLUtil.parseLongToInt( _processStatusVO.getBeforeInterval() / (60 * 24) );
            if (statusOk) {
                con.commit();
                // method call to find maximum date till which process has been
                // executed
                processedUpto = _processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    // adding 1 in processed upto date as we have to start from
                    // the next day till which process has been executed
                    processedUpto = BTSLUtil.addDaysInUtilDate(processedUpto, 1);
                    final Calendar cal = Calendar.getInstance();
                    currentDate = cal.getTime(); // Current Date
                    currentDate = BTSLUtil.addDaysInUtilDate(currentDate, -beforeInterval);
                } else {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("C2sMisDataProcessingNew["+METHOD_NAME+"]", " Date till which process has been executed is not found.");
                    }
                    return;
                }
            }
            try {
//                final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT);
//                sdf.setLenient(false); // this is required else it will convert
                /*reportTo = BTSLDateUtil.getSystemLocaleDate(sdf.format(currentDate)); // Current Date
                prevDateStr = BTSLDateUtil.getSystemLocaleDate(sdf.format(processedUpto));// Last MIS Done Date +1
*/                
                reportTo = BTSLDateUtil.getSystemLocaleDate(currentDate, PretupsI.DATE_FORMAT); // Current Date
                prevDateStr = BTSLDateUtil.getSystemLocaleDate(processedUpto, PretupsI.DATE_FORMAT);// Last MIS Done Date +1
                
            } catch (Exception e) {
                reportTo = "";
                prevDateStr = "";
                LOGGER.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException("C2sMisDataProcessingNew", METHOD_NAME, "Not able to convert date to String");
            }

            // Mis process will be exceuted from the start till to date -1
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("C2sMisDataProcessingNew["+METHOD_NAME+"]",
                    "From date=" + prevDateStr + " To Date=" + reportTo + " processedUpto.compareTo(currentDate)=" + processedUpto.compareTo(currentDate));
            }

            // If process is already ran for the last day then do not run again
            if (processedUpto != null && processedUpto.compareTo(currentDate) > 0) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2sMisDataProcessingNew["+METHOD_NAME+"]",
                    "", "", "", "Monthly C2S MIS already run for the date=" + String.valueOf(currentDate));
                return;
            }
            if(PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                cstmt = con.prepareCall("{call " + Constants.getProperty("currentschema") + ".MIS_DATA_PKG_NEW.sp_get_mis_mon_data_dtrange(?,?,?,?,?)}");
            }
            else if(QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
              	 cstmt = con.prepareCall("{call mis_sp_get_mis_mon_data_dtrange(?,?)}");
              	 
            }else
            {
                  
                  cstmt = con.prepareCall("{call MIS_DATA_PKG_NEW.sp_get_mis_mon_data_dtrange(?,?,?,?,?)}");
              }
            final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            if(QueryConstants.DB_POSTGRESQL.equals(dbConnected))
            {
            	cstmt.setString(1, prevDateStr);
                cstmt.setString(2, reportTo);
                
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("C2sMisDataProcessingNew["+METHOD_NAME+"]", "Before Exceuting Procedure");
                }
                 rs=cstmt.executeQuery();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("C2sMisDataProcessingNew["+METHOD_NAME+"]", "After Exceuting Procedure");
                }
                if(rs.next()){
                	String status=rs.getString("aov_message");
                	String messageforlog=rs.getString("aov_messageforlog");
                	String sqlerrmsgforlog=rs.getString("aov_sqlerrmsgforlog");
                if("SUCCESS".equalsIgnoreCase(status))
                    	con.commit();   
                            
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("C2sMisDataProcessingNew["+METHOD_NAME+"]", "After Exceuting Procedure");
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("C2sMisDataProcessingNew["+METHOD_NAME+"]",
                        "Parameters Returned : Status=" + status + " , Message=" + messageforlog + " ,Exception if any=" + sqlerrmsgforlog);
                }

                if (status == null || !("SUCCESS".equalsIgnoreCase(status))) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2sMisDataProcessingNew["+METHOD_NAME+"]",
                        "", "", "", messageforlog + " Exception if any:" + sqlerrmsgforlog);
                } else {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2sMisDataProcessingNew["+METHOD_NAME+"]",
                        "", "", "", messageforlog + " Exception if any:" + sqlerrmsgforlog);
                }

                // send the message as SMS
                
                message = BTSLUtil.NullToString(messageforlog) + BTSLUtil.NullToString(sqlerrmsgforlog);
                }
            
            }
            else{
            cstmt.registerOutParameter(3, Types.VARCHAR); // Message
            cstmt.registerOutParameter(4, Types.VARCHAR); // Message for log
            cstmt.registerOutParameter(5, Types.VARCHAR); // Sql Exception
            cstmt.setString(1, prevDateStr);
            cstmt.setString(2, reportTo);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("C2sMisDataProcessingNew["+METHOD_NAME+"]", "Before Exceuting Procedure");
            }
            cstmt.executeUpdate();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("C2sMisDataProcessingNew["+METHOD_NAME+"]", "After Exceuting Procedure");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("C2sMisDataProcessingNew["+METHOD_NAME+"]",
                    "Parameters Returned : Status=" + cstmt.getString(3) + " , Message=" + cstmt.getString(4) + " ,Exception if any=" + cstmt.getString(5));
            }

            if (cstmt.getString(3) == null || !("SUCCESS".equalsIgnoreCase(cstmt.getString(3)))) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2sMisDataProcessingNew["+METHOD_NAME+"]",
                    "", "", "", cstmt.getString(4) + " Exception if any:" + cstmt.getString(5));
            } else {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "C2sMisDataProcessingNew["+METHOD_NAME+"]",
                    "", "", "", cstmt.getString(4) + " Exception if any:" + cstmt.getString(5));
            }

            // send the message as SMS
            
            message = BTSLUtil.NullToString(cstmt.getString(4)) + BTSLUtil.NullToString(cstmt.getString(5));
            }
            final String msisdnString = new String(Constants.getProperty("adminmobile"));
            final String[] msisdn = msisdnString.split(",");

            for (int i = 0; i < msisdn.length; i++) {
                final PushMessage pushMessage = new PushMessage(msisdn[i], message, null, null, locale);
                pushMessage.push();
            }
            try {
                Thread.sleep(5);
            } catch (Exception e) {
                LOGGER.errorTrace(METHOD_NAME, e);
            }
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (Exception sqlex) {

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("C2sMisDataProcessingNew", "C2sMisDataProcessingNew["+METHOD_NAME+"]::Exception while roll back" + sqlex);
                }
                LOGGER.errorTrace(METHOD_NAME, sqlex);
            }
            message = e.getMessage();
            // send the message as SMS
            LOGGER.errorTrace(METHOD_NAME, e);
        } finally {
            try {
                if (statusOk) {
                    if (markProcessStatusAsComplete(con, processId) == 1) {
                        try {
                            con.commit();
                        } catch (Exception e) {
                            LOGGER.errorTrace(METHOD_NAME, e);
                        }
                    } else {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            LOGGER.errorTrace(METHOD_NAME, e);
                        }
                    }
                }
            } catch (Exception ex) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("C2sMisDataProcessingNew", "Exception while closing statement in C2sMisDataProcessingNew["+METHOD_NAME+"] method ");
                }
                LOGGER.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOGGER.errorTrace(METHOD_NAME, e);
            }
            try {
                if (cstmt != null) {
                	cstmt.close();
                }
            } catch (Exception e) {
                LOGGER.errorTrace(METHOD_NAME, e);
            }
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                LOGGER.errorTrace(METHOD_NAME, e);
            }
        }

    }

    private static int markProcessStatusAsComplete(Connection p_con, String p_processId) {
        final String METHOD_NAME = "markProcessStatusAsComplete";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("markProcessStatusAsComplete", " Entered:  p_processId:" + p_processId);
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
            LOGGER.errorTrace(METHOD_NAME, e);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("markProcessStatusAsComplete", "Exception= " + e.getMessage());
            }
        } finally {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;

    }
}

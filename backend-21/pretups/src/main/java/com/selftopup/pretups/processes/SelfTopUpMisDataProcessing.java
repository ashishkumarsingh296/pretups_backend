package com.selftopup.pretups.processes;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.gateway.businesslogic.PushMessage;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.processes.businesslogic.ProcessBL;
import com.selftopup.pretups.processes.businesslogic.ProcessI;
import com.selftopup.pretups.processes.businesslogic.ProcessStatusDAO;
import com.selftopup.pretups.processes.businesslogic.ProcessStatusVO;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.ConfigServlet;
import com.selftopup.util.Constants;
import com.selftopup.util.OracleUtil;

/**
 * @(#)SelfTopUpMisDataProcessing .java
 * 
 *                                This class will call the MIS package to
 *                                populate the MIS data tables at the end of
 *                                night
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                Author Date History
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 *                                gaurav pandey 06/07/2005 Initital Creation
 * 
 *                                ----------------------------------------------
 *                                ----------------------------------------------
 *                                -----
 */

public class SelfTopUpMisDataProcessing {
    public static String message = new String();
    private static ProcessBL processBL = null;
    private static ProcessStatusVO processStatusVO;
    private static Log logger = LogFactory.getLog(SelfTopUpMisDataProcessing.class.getName());

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

        try {
            if (args.length != 2) {
                System.out.println("Usage : SelfTopUpMisDataProcessing [Constants file] [LogConfig file]");
                return;
            }
            File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
                System.out.println(" Constants File Not Found .............");
                return;
            }
            File logconfigFile = new File(args[1]);
            if (!logconfigFile.exists()) {
                System.out.println(" Logconfig File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        } catch (Exception e) {
            System.out.println("Exception thrown in SelfTopUpMisDataProcessing: Not able to load files" + e);
            ConfigServlet.destroyProcessCache();
            return;
        }

        try {
            // Make Connection
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (logger.isDebugEnabled())
                    logger.debug("SelfTopUpMisDataProcessing[main]", " Not able to get Connection in MisDataProcessing: ");
                throw new Exception();
            }
            processId = "SLFTPMIS";
            // method call to check status of the process
            processBL = new ProcessBL();
            processStatusVO = processBL.checkProcessUnderProcess(con, processId);
            statusOk = processStatusVO.isStatusOkBool();
            beforeInterval = (int) processStatusVO.getBeforeInterval() / (60 * 24);
            if (statusOk) {
                con.commit();
                // method call to find maximum date till which process has been
                // executed
                processedUpto = processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    // adding 1 in processed upto date as we have to start from
                    // the next day till which process has been executed
                    processedUpto = BTSLUtil.addDaysInUtilDate(processedUpto, 1);
                    Calendar cal = Calendar.getInstance();
                    currentDate = cal.getTime(); // Current Date
                    currentDate = BTSLUtil.addDaysInUtilDate(currentDate, -beforeInterval);
                } else {
                    System.out.println("SelfTopUpMisDataProcessing:: Date till which process has been executed is not found.");
                    return;
                }
            }
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT);
                sdf.setLenient(false); // this is required else it will convert
                reportTo = sdf.format(currentDate); // Current Date
                prevDateStr = sdf.format(processedUpto);// Last MIS Done Date +1
            } catch (Exception e) {
                reportTo = "";
                prevDateStr = "";
                throw new Exception("Not able to convert date to String");
            }
            if (logger.isDebugEnabled())
                logger.debug("SelfTopUpMisDataProcessing[main]", "From date=" + prevDateStr + " To Date=" + reportTo + " processedUpto.compareTo(currentDate)=" + processedUpto.compareTo(currentDate));

            // If process is already ran for the last day then do not run again
            if (processedUpto.compareTo(currentDate) > 0) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "SelfTopUpMisDataProcessing[main]", "", "", "", "SelfTopUp MIS already run for the date=" + String.valueOf(currentDate));
                return;
            }

            if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                cstmt = con.prepareCall("{call " + Constants.getProperty("currentschema") + ".pkg_selftopupmis.SP_GET_MIS_DATA_DTRANGE(?,?,?,?,?)}");
            } else {
                cstmt = con.prepareCall("{call pkg_selftopupmis.SP_GET_MIS_DATA_DTRANGE(?,?,?,?,?)}");
            }
            cstmt.setString(1, prevDateStr);
            cstmt.setString(2, reportTo);
            cstmt.registerOutParameter(3, Types.VARCHAR); // Message
            cstmt.registerOutParameter(4, Types.VARCHAR); // Message for log
            cstmt.registerOutParameter(5, Types.VARCHAR); // Sql Exception

            if (logger.isDebugEnabled())
                logger.debug("SelfTopUpMisDataProcessing[main]", "Before Exceuting Procedure");
            cstmt.executeUpdate();
            if (logger.isDebugEnabled())
                logger.debug("SelfTopUpMisDataProcessing[main]", "After Exceuting Procedure");
            if (logger.isDebugEnabled())
                logger.debug("SelfTopUpMisDataProcessing[main]", "Parameters Returned : Status=" + cstmt.getString(3) + " , Message=" + cstmt.getString(4) + " ,Exception if any=" + cstmt.getString(5));

            if (cstmt.getString(3) == null || !cstmt.getString(3).equalsIgnoreCase("SUCCESS"))
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelfTopUpMisDataProcessing[main]", "", "", "", cstmt.getString(4) + " Exception if any:" + cstmt.getString(5));
            else
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "SelfTopUpMisDataProcessing[main]", "", "", "", cstmt.getString(4) + " Exception if any:" + cstmt.getString(5));

            // send the message as SMS
            Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
            message = BTSLUtil.NullToString(cstmt.getString(4)) + BTSLUtil.NullToString(cstmt.getString(5));
            String msisdnString = new String(Constants.getProperty("adminmobile"));
            String[] msisdn = msisdnString.split(",");

            for (int i = 0; i < msisdn.length; i++) {
                PushMessage pushMessage = new PushMessage(msisdn[i], message, null, null, locale);
                pushMessage.push();
            }
        } catch (Exception e) {
            System.out.println("Exception in method " + e);
            try {
                con.rollback();
            } catch (Exception sqlex) {
                System.out.println("SelfTopUpMisDataProcessing::Exception while roll back" + sqlex);
            }
            message = e.getMessage();
            // send the message as SMS
        } finally {
            try {
                if (statusOk) {
                    if (markProcessStatusAsComplete(con, processId) == 1)
                        try {
                            con.commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    else
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
                if (cstmt != null)
                    cstmt.close();
                if (con != null)
                    con.close();
            } catch (Exception ex) {
                System.out.println("Exception while closing statement in SelfTopUpMisDataProcessing method ");
            }
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
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
        if (logger.isDebugEnabled())
            logger.debug("markProcessStatusAsComplete", " Entered:  p_processId:" + p_processId);
        int updateCount = 0;
        Date currentDate = new Date();
        ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        processStatusVO.setProcessID(p_processId);
        processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        processStatusVO.setStartDate(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetailForMis(p_con, processStatusVO);
        } catch (Exception e) {
            e.printStackTrace();
            if (logger.isDebugEnabled())
                logger.debug("markProcessStatusAsComplete", "Exception= " + e.getMessage());
        } finally {
            if (logger.isDebugEnabled())
                logger.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
        } // end of finally
        return updateCount;

    }
}

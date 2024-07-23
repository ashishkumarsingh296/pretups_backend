package com.client.pretups.processes.clientprocesses.vodaidea;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

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
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

public class VoucherSummaryLoader {
    private static ProcessStatusVO processStatusVO;
    private static ProcessBL processBL = null;
    protected static HashMap<String, Long> fileNameMap = null;
    protected static TreeMap<String, Object> fileRecordMap = null;
    
    
    static final String FILENAME = "  fileName=";
    static final String CALL = "{call ";
    static final String FREE = "Free: ";
    static final String CLASSNAME = "VoucherSummaryLoader";
    private static Log logger = LogFactory.getLog(VoucherSummaryLoader.class.getName());

    public static void main(String arg[]) {
        final String methodName = "main";
        Date date = null;
        try {
            if (arg.length < 2) {
                System.out.println("Usage : VoucherSummaryLoader [Constants file] [LogConfig file]");
                return;
            }
            final File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
                System.out.println(CLASSNAME + " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                System.out.println(CLASSNAME + " Logconfig File Not Found .............");
                return;
            }

            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        }// end of try
        catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("main", " Error in Loading Files ...........................: " + e.getMessage());
            }
            logger.errorTrace(methodName, e);
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {
            process(date);
        } catch (BTSLBaseException be) {
            logger.error("main", PretupsI.BTSLEXCEPTION + be.getMessage());
            logger.errorTrace(methodName, be);
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug("main", "Exiting..... ");
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    private static void process(Date pDate) throws BTSLBaseException {
        Date processedUpto = null;
        Date currentDate = new Date();
        Connection con = null;
        String processId = null;
        boolean statusOk = false;
        CallableStatement cstmt = null;
        final String methodName = "process";
        try {
            logger.debug(methodName, "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + FREE + Runtime.getRuntime().freeMemory() / 1049576);
            currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);
            // getting all the required parameters from Constants.props
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug(methodName, " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "",
                    "", "DATABASE Connection is NULL");
                return;
            }
            // getting process id
            processId = "VMSSUM";
            // method call to check status of the process
            processBL = new ProcessBL();
            processStatusVO = processBL.checkProcessUnderProcess(con, processId);
            statusOk = processStatusVO.isStatusOkBool();
            if (pDate == null && statusOk) {
                con.commit();
                // method call to find maximum date till which process has been
                // executed
                processedUpto = processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    // current date or not
                    if (processedUpto.compareTo(currentDate) == 0) {
                        throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_PROCESS_ALREADY_EXECUTED_TILL_TODAY);
                    }
                    processedUpto = BTSLUtil.addDaysInUtilDate(processedUpto, 1);
                        
                    if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                        cstmt = con.prepareCall(CALL + Constants.getProperty("currentschema") + ".SP_VOMS_VOUCHER_DAILY_SUMMARY(?)}");
                    } else {
                        cstmt = con.prepareCall("{call SP_VOMS_VOUCHER_DAILY_SUMMARY(?)}");
                    }
                        
                    cstmt.registerOutParameter(1, Types.VARCHAR); // Status
                    if (logger.isDebugEnabled()) {
                        logger.debug(methodName, "Before Exceuting Procedure");
                    }
                    cstmt.executeUpdate();
                    if (logger.isDebugEnabled()) {
                        logger.debug(methodName, "After Exceuting Procedure");
                    }
                    
                    final String isSuccess = cstmt.getString(1);

                    if (!"SUCCESS".equals(isSuccess)) {
                        throw new BTSLBaseException(CLASSNAME, methodName, "Procedure Execution Fail");
                    }
                    
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, "", "", "",
                        " VoucherSummaryLoader process has been executed successfully.");
                }
                else {
                    throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
                }
            }
            
        }// end of try
        catch (BTSLBaseException be) {
            logger.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
            logger.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            logger.error(methodName, PretupsI.EXCEPTION + e.getMessage());
            logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, "", "", "",
                " VoucherSummaryLoader process could not be executed successfully.");
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (statusOk) {
                try {
                    if (markProcessStatusAsComplete(con, processId) == 1) {
                        try {
                            con.commit();
                        } catch (Exception e) {
                            logger.errorTrace(methodName, e);
                        }
                    } else {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            logger.errorTrace(methodName, e);
                        }
                    }
                } catch (Exception e) {
                    logger.errorTrace(methodName, e);
                }
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (Exception ex) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(methodName, "Exception closing connection ");
                    }
                    logger.errorTrace(methodName, ex);
                }
            }
            try {
                if (cstmt != null) {
                    cstmt.close();
                }
            } catch (Exception e) {
                logger.errorTrace(methodName, e);
            }
            logger.debug(methodName, "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + FREE + Runtime.getRuntime().freeMemory() / 1049576);
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, "Exiting..... ");
            }
        }
    }


    private static int markProcessStatusAsComplete(Connection pCon, String pProcessId) throws BTSLBaseException {
        final String methodName = "markProcessStatusAsComplete";
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, " Entered:  pProcessId:" + pProcessId);
        }
        int updateCount = 0;
        final Date currentDate = new Date();
        final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        processStatusVO.setProcessID(pProcessId);
        processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        processStatusVO.setStartDate(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetail(pCon, processStatusVO);
        } catch (Exception e) {
            logger.errorTrace(methodName, e);
            logger.error(methodName, "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherSummaryLoader[markProcessStatusAsComplete]", "", "",
                "", PretupsI.EXCEPTION + e.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, "Exiting: updateCount=" + updateCount);
            }            
        } // end of finally
        return updateCount;

    }

 


}

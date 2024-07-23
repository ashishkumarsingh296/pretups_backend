package com.btsl.voms.voucher.businesslogic;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
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
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.OracleUtil;
import com.btsl.voms.vomslogging.VomsBatchInfoLog;

public class VoucherAvailabilityReport {
	private static Log _logger = LogFactory.getLog(VoucherAvailabilityReport.class.getName());
    private static long starttime = System.currentTimeMillis();
    
    public static void main(String args[]) {
        final String METHOD_NAME = "main";
        try {
            if (args.length < 2 || args.length > 3) {
                System.out.println("Usage : VoucherAvailabilityReport [Constants file] [LogConfig file] [Y/N]");
                return;
            }
            // load constants.props
            File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
                System.out.println("VoucherAvailabilityReport" + " Constants File Not Found .............");
                _logger.error("VoucherAvailabilityReport[main]", "Constants file not found on location: " + constantsFile.toString());
                return;
            }
            // load log config file
            File logFile = new File(args[1]);
            if (!logFile.exists()) {
                System.out.println("VoucherAvailabilityReport" + " Logconfig File Not Found .............");
                _logger.error("VoucherAvailabilityReport[main]", "Logconfig File not found on location: " + logFile.toString());
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logFile.toString());
        }// end of try block
        catch (Exception e) {
           System.err.println("Error in Loading Configuration files ...........................: " + e);
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch block
        try {
            process();
        }// end of try block
        catch (BTSLBaseException be) {
            _logger.errorTrace(METHOD_NAME, be);
            _logger.error("main", "BTSLBaseException : " + be.getMessage());
            return;
        }// end of catch block
        catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("main", " " + e.getMessage());
            }
            _logger.errorTrace(METHOD_NAME, e);
            return;
        }// end of catch block
        finally {
            VomsBatchInfoLog.log("Total time taken:" + (System.currentTimeMillis() - starttime));
            if (_logger.isDebugEnabled()) {
                _logger.info("main", "Exiting");
            }
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            ConfigServlet.destroyProcessCache();
        }// end of finally
    }

    public static void process() throws BTSLBaseException {
        if (_logger.isDebugEnabled()) {
            _logger.info("process ", "Entered ");
        }
        final String METHOD_NAME = "process";
        String processId = null;
        ProcessBL processBL = null;
        Connection con = null;
        int beforeInterval = 0;
        ProcessStatusVO processStatusVO = null;
        Date currentDate = null;
        Date processedUpto = null;
        int updateCount = 0; // check process details are updated or not
        Date startdate, endDate = null;
        try {
            processId = ProcessI.VOMS_GEN;
            con = OracleUtil.getSingleConnection();
            processBL = new ProcessBL();
            processStatusVO = processBL.checkProcessUnderProcess(con, processId);
            beforeInterval = BTSLUtil.parseLongToInt( processStatusVO.getBeforeInterval() / (60 * 24));
            if (processStatusVO.isStatusOkBool()) {
                // method call to find maximum date till which process has been
                // executed
                processedUpto = processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    currentDate = BTSLUtil.getTimestampFromUtilDate(new Date());
                    
                    startdate = processedUpto;
               
                    con.commit();
                    processedUpto = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(currentDate));
                    processedUpto = currentDate;
                    // call process for uploading transfer details
                    boolean isDataProcessed = true;//generateVouchers(con, startdate, processedUpto,VOMSI.MANUAL);
                    if (isDataProcessed) {
                        processStatusVO.setExecutedUpto(BTSLUtil.addDaysInUtilDate(processedUpto, -beforeInterval));
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherGenerator[process]", "", "", "", " VoucherGenerator process has been executed successfully.");
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("process", " successfully");
                        }
                    }
                } else {
                    throw new BTSLBaseException("VoucherAvailabilityReport", "process", PretupsErrorCodesI.ERROR_VOMS_GEN);
                }
            } else {
                throw new BTSLBaseException("VoucherAvailabilityReport", "process", PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
            }
        } catch (BTSLBaseException be) {
            _logger.error("process", "BTSLBaseException : " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _logger.error("process", "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherGenerator[process]", "", "", "", " VoucherGenerator process could not be executed successfully.");
            throw new BTSLBaseException("VoucherGenerator", "process", PretupsErrorCodesI.ERROR_VOMS_GEN,e);
        } finally {
            try {
                if (processStatusVO.isStatusOkBool()) {
                    processStatusVO.setStartDate(currentDate);
                    processStatusVO.setExecutedOn(currentDate);
                    //
                    processStatusVO.setExecutedUpto(currentDate);
                    processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                    updateCount = (new ProcessStatusDAO()).updateProcessDetail(con, processStatusVO);
                    if (updateCount > 0) {
                        con.commit();
                    }
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
                if (_logger.isDebugEnabled()) {
                    _logger.debug("process", "Exception in closing connection ");
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e1) {
                    _logger.errorTrace(METHOD_NAME, e1);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("process", "Exiting..... ");
            }
        }
    }
}

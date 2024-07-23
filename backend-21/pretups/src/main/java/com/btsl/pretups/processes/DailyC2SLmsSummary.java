package com.btsl.pretups.processes;

/**
 * @(#)DailyC2SLmsSummary
 *                        Copyright(c) 2015, Mahindra Comviva Technologies Ltd.
 *                        All Rights Reserved
 * 
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Diwakar 08/11/2015 Initial Creation
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 */

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.QueryConstants;
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
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.ibm.icu.util.Calendar;

public class DailyC2SLmsSummary {
    private static ProcessStatusVO _processStatusVO;
    private static ProcessStatusVO _processStatusMISVO =null;
    private static ProcessStatusVO _processStatusVOTargetCredit;
    private static ProcessBL _processBL = null;

    private static Log _logger = LogFactory.getLog(DailyC2SLmsSummary.class.getName());

    /**
     * ensures no instantiation
     */
    private DailyC2SLmsSummary(){
    	
    }
    public static void main(String arg[]) {
        final String METHOD_NAME = "main";
        Date date = null;
        try {
            // ID LMS003 third argument is to generate DWH even there is
            // Ambiguous or Under Process Txn.
            if (arg.length < 2) {
                System.out.println("Usage : DailyC2SLmsSummary [Constants file] [LogConfig file]");
                return;
            }
            final File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
                System.out.println("DailyC2SLmsSummary" + " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                System.out.println("DailyC2SLmsSummary" + " Logconfig File Not Found .............");
                return;
            }
            final int noOfArgs = arg.length;
            System.out.println("DailyC2SLmsSummary" + " Number of Args= " + noOfArgs + " , argument parameters = " + Arrays.asList(arg));
            // if third argument is not set then try to set it with Default
            // value true;
            try {
               if (noOfArgs == 3) {
                    final String inputDate = arg[2];
                    if (!BTSLUtil.isNullString(inputDate)) {
                        date = BTSLUtil.getDateFromDateString(inputDate, PretupsI.DATE_FORMAT_DDMMYYYY);
                        final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
                        if (!date.before(BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(new Date())))) {
                            System.out.println("DailyC2SLmsSummary" + " process will execute only for current or previous date ");
                            return;

                        }
                        System.out.println("DailyC2SLmsSummary" + " process is going to execute only till.............date:" + date);

                    }

                }

            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }

            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());

        }// end of try
        catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, " Error in Loading Files ...........................: " + e.getMessage());
            }
            _logger.errorTrace(METHOD_NAME, e);
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {
            process(date);
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... ");
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    private static void process(Date p_date) throws BTSLBaseException {
        Date processedUpto = null;
        Date processedUptoTargetCredit = null;
        Date dateCount = null;
        Date currentDate = new Date();
        Connection con = null;
        String processId = null;
        String processIdTargetCredit = null;
        boolean statusOk = false;
        boolean statusOkTargetCredit = false;
        ProcessStatusDAO processStatusDAO = null;
        int beforeInterval = 0;
        CallableStatement cstmt = null;
	    Date processedUptoMIS=null;
        int maxDoneDateUpdateCount = 0;
        String lmsC2sSummaryProcName = null;
        String dbtype = null;
        String dtype = null;
        final String METHOD_NAME = "process";
        String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
        String isSuccess = null;
        String returnMess = null;
        String returnError = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        try {
            if (_logger.isDebugEnabled()) {
                _logger
                    .debug(METHOD_NAME, "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            }
            currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);

            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyC2SLmsSummary[process]", "",
                    "", "", "DATABASE Connection is NULL");
                return;
            }
            // getting process id
            processId = ProcessI.LMS_C2S_SUMMARY;
            // getting dependent process id
            processIdTargetCredit = ProcessI.LMS_TARGET_CREDIT;
            // method call to check status of the process
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(con, processId);
       	    _processStatusMISVO=_processBL.checkProcessUnderProcess(con,ProcessI.C2SMIS);
            processStatusDAO = new ProcessStatusDAO();
            _processStatusVOTargetCredit = processStatusDAO.loadProcessDetail(con, processIdTargetCredit);
            statusOk = _processStatusVO.isStatusOkBool();
            statusOkTargetCredit = _processStatusVOTargetCredit.isStatusOkBool();
            beforeInterval = BTSLUtil.parseLongToInt( _processStatusVO.getBeforeInterval() / (60 * 24) );
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "p_date= " + p_date + ",statusOk= " + statusOk + ", statusOkTargetCredit=" + statusOkTargetCredit);
            }
            // method call to find maximum date till which process has been
            // executed
            processedUpto = _processStatusVO.getExecutedUpto();
            processedUptoTargetCredit = _processStatusVOTargetCredit.getExecutedUpto();
   	        processedUptoMIS=_processStatusMISVO.getExecutedUpto();
	   	    if(processedUptoMIS!=null) {
			con.rollback();
			//Process should not execute until the MIS has not executed successfully for previous day
			Calendar cal4CurrentDate = BTSLDateUtil.getInstance();
			Calendar cal14MisExecutedUpTo = BTSLDateUtil.getInstance();
			cal4CurrentDate.add(Calendar.DAY_OF_MONTH, -1);
			Date currentDate1=cal4CurrentDate.getTime(); //Current Date
			cal14MisExecutedUpTo.setTime(processedUptoMIS);
			Calendar cal24CurrentDate = BTSLDateUtil.getCalendar(cal4CurrentDate);
		    Calendar cal34MisExecutedUpTo = BTSLDateUtil.getCalendar(cal14MisExecutedUpTo);
		    if (_logger.isDebugEnabled()) {
		    	_logger.debug(METHOD_NAME,"(currentDate - 1) = "+currentDate1 +" processedUptoMIS = "+processedUptoMIS+",cal34MisExecutedUpTo.compareTo(cal24CurrentDate)="+cal34MisExecutedUpTo.compareTo(cal24CurrentDate));
		    }
			if(cal34MisExecutedUpTo.compareTo(cal24CurrentDate) <0)
			{
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "The MIS has not been executed for the previous day.");
				}
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"DailyC2SLmsSummary[process]","","","","The MIS has not been executed for the previous day.");
				throw new BTSLBaseException(METHOD_NAME,METHOD_NAME,PretupsErrorCodesI.LMS_MIS_DEPENDENCY);
			}
            if (p_date == null && (statusOk && !statusOkTargetCredit)) {
                con.commit();
                if (processedUpto != null && processedUptoTargetCredit != null) {
                    // ID LMS002 to check whether process has been executed till
                    // current date or not
                    if (processedUpto.compareTo(currentDate) == 0) {
                        throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.LMS_C2S_SUMMARY_PROCESS_ALREADY_EXECUTED_TILL_TODAY);
                    }
                    // adding 1 in processed upto date as we have to start from
                    // the next day till which process has been executed
                    processedUpto = BTSLUtil.addDaysInUtilDate(processedUpto, 1);
                    if (_logger.isDebugEnabled()) {
                        _logger.debug(METHOD_NAME, "dateCount= " + BTSLUtil.getSQLDateFromUtilDate(processedUpto) + ", dateCount.before=" + BTSLUtil.getSQLDateFromUtilDate(
                            processedUpto).before(BTSLUtil.addDaysInUtilDate(currentDate, -beforeInterval)));
                    }
                    // loop to be started for each date
                    // the loop starts from the date till which process has been
                    // executed and executes one day before current date
                    for (dateCount = BTSLUtil.getSQLDateFromUtilDate(processedUpto); dateCount.before(BTSLUtil.addDaysInUtilDate(processedUptoTargetCredit, 0)); dateCount = BTSLUtil
                        .addDaysInUtilDate(dateCount, 1)) {
                        if (_logger.isDebugEnabled()) {
                            _logger
                                .debug(
                                    METHOD_NAME,
                                    "Process is being executed for date =" + dateCount + ", processedUpto= " + processedUpto + ", processedUptoTargetCredit=" + processedUptoTargetCredit);
                        }
                        if (BTSLUtil.addDaysInUtilDate(processedUpto, 0).before(processedUptoTargetCredit) || BTSLUtil.addDaysInUtilDate(processedUpto, 0).compareTo(
                            processedUptoTargetCredit) == 1) {
                            lmsC2sSummaryProcName = "insert_dly_no_c2s_lms_smry";
                            if (!(BTSLUtil.isNullString(lmsC2sSummaryProcName) || lmsC2sSummaryProcName.isEmpty())) {
                                if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                                    dbtype = "{call " + Constants.getProperty("currentschema") + "." + lmsC2sSummaryProcName + "(?,?,?,?)}";
                                    cstmt = con.prepareCall(dbtype);
                                }else if(QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                                	 dtype = "{call " + lmsC2sSummaryProcName + "(?)}";
                                     cstmt = con.prepareCall(dtype);
                                }
                                else {
                                    dtype = "{call " + lmsC2sSummaryProcName + "(?,?,?,?)}";
                                    cstmt = con.prepareCall(dtype);
                                    
                                }
                            }
                            
                            if (_logger.isDebugEnabled()) {
                                _logger.debug(METHOD_NAME, "Before Exceuting Procedure , lmsC2sSummaryProcName= " + lmsC2sSummaryProcName);
                            }
                            
                            cstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(dateCount)); // Date for which the procedure is being executed
                            if(!QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                            cstmt.registerOutParameter(2, Types.VARCHAR); // Status
                            cstmt.registerOutParameter(3, Types.VARCHAR); // return message
                            cstmt.registerOutParameter(4, Types.VARCHAR); // SQL exception
                            cstmt.executeUpdate();
                             isSuccess = cstmt.getString(2);
                             returnMess = cstmt.getString(3);
                             returnError = cstmt.getString(4);
                            }
                            else{
                            	   rs = cstmt.executeQuery();
                                   if(rs.next()){
                                   	isSuccess =rs.getString("rtn_message");
                                   	returnMess=rs.getString("rtn_messageforlog");
                                   	returnError=rs.getString("rtn_sqlerrmsgforlog");
                                    }
                            }
                            if (_logger.isDebugEnabled()) {
                                _logger.debug(METHOD_NAME, "After Exceuting Procedure , lmsC2sSummaryProcName= " + lmsC2sSummaryProcName);
                            }
                          
                            if (_logger.isDebugEnabled()) {
                                _logger
                                    .debug(
                                        METHOD_NAME,
                                        "isSuccess= " + isSuccess + " , returnMess= " + returnMess + " , returnError= " + returnError + " , lmsC2sSummaryProcName= " + lmsC2sSummaryProcName);
                            }

                            if (!"SUCCESS".equalsIgnoreCase(isSuccess)) {
                                processStatusDAO = null;
                                con.rollback();
                                throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, "Procedure Execution Fail");
                            } else {

                                lmsC2sSummaryProcName = "update_accpnt_dly_c2s_lms_smry";
                                if (!(BTSLUtil.isNullString(lmsC2sSummaryProcName) || lmsC2sSummaryProcName.isEmpty())) {
                                    if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                                        dbtype = "{call " + Constants.getProperty("currentschema") + "." + lmsC2sSummaryProcName + "(?,?,?,?)}";
                                        cstmt = con.prepareCall(dbtype);
                                    }else if(QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                                    	 dtype = "{call " + lmsC2sSummaryProcName + "(?)}";
                                         cstmt = con.prepareCall(dtype);
                                    } 
                                    else {
                                        dtype = "{call " + lmsC2sSummaryProcName + "(?,?,?,?)}";
                                        cstmt = con.prepareCall(dtype);
                                    }
                                }
                                
                                if (_logger.isDebugEnabled()) {
                                    _logger.debug(METHOD_NAME, "Before Exceuting Procedure , lmsC2sSummaryProcName= " + lmsC2sSummaryProcName);
                                }
                                
                                cstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(dateCount));// Date for which the procedure is being executed
                                if(!QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                                cstmt.registerOutParameter(2, Types.VARCHAR); // Status
                                cstmt.registerOutParameter(3, Types.VARCHAR); // return message
                                cstmt.registerOutParameter(4, Types.VARCHAR); // SQL exception
                                cstmt.executeUpdate();
                                isSuccess = cstmt.getString(2);
                                returnMess = cstmt.getString(3);
                                returnError = cstmt.getString(4);
                                }else
                                {
                                	    rs1 = cstmt.executeQuery();
                                       if(rs.next()){
                                    	   isSuccess =rs1.getString("rtn_message");
                                    	   returnMess = rs1.getString("rtn_messageforlog");
                                    	   returnError = rs1.getString("rtn_sqlerrmsgforlog");
                                         }
                                }
                                
                                if (_logger.isDebugEnabled()) {
                                    _logger.debug(METHOD_NAME, "After Exceuting Procedure , lmsC2sSummaryProcName= " + lmsC2sSummaryProcName);
                                }
                               
                                if (_logger.isDebugEnabled()) {
                                    _logger
                                        .debug(
                                            METHOD_NAME,
                                            "isSuccess= " + isSuccess + " , returnMess= " + returnMess + " , returnError= " + returnError + " , lmsC2sSummaryProcName= " + lmsC2sSummaryProcName);
                                }

                                if (!"SUCCESS".equalsIgnoreCase(isSuccess)) {
                                    processStatusDAO = null;
                                    con.rollback();
                                    throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, "Procedure Execution Fail");
                                }
                            }
                            // method call to update maximum date till which
                            // process has been executed
                            _processStatusVO.setExecutedUpto(dateCount);
                            _processStatusVO.setExecutedOn(currentDate);
                            processStatusDAO = new ProcessStatusDAO();
                            maxDoneDateUpdateCount = processStatusDAO.updateProcessDetail(con, _processStatusVO);

                            // if the process is successful, transaction is
                            // commit, else roll back
                            if (maxDoneDateUpdateCount > 0) {
                                con.commit();
                                processStatusDAO = null;
                            } else {
                                processStatusDAO = null;
                                con.rollback();
                                throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.LMS_C2S_SUMMARY_COULD_NOT_UPDATE_MAX_DONE_DATE);
                            }
                            Thread.sleep(100);
                        } else {
                            if (_logger.isDebugEnabled()) {
                                _logger.debug(METHOD_NAME, "processedUpto= " + processedUpto + ", processedUptoTargetCredit=" + processedUptoTargetCredit);
                            }
                            if (_logger.isDebugEnabled()) {
                                _logger.debug(METHOD_NAME, "Process will not be executed untill target credit process will not till it's date");
                            }
                        }
                    }// end loop
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DailyC2SLmsSummary[process]", "", "", "",
                        " DailyC2SLmsSummary process has been executed successfully.");
                }
                // ID LMS002 to avoid the null pointer exception thrown, in case
                // processesUpto is null
                else {
                    throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.LMS_C2S_SUMMARY_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
                }
            }// end of if (statusOk)
            else {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, "Exceuting Procedure for specific date = " + p_date + ", processedUptoTargetCredit=" + processedUptoTargetCredit);
                }
                // Execution will be for only specific date
                if (p_date != null && p_date.before(processedUptoTargetCredit)) {
                    lmsC2sSummaryProcName = "insert_dly_no_c2s_lms_smry";
                    if (!(BTSLUtil.isNullString(lmsC2sSummaryProcName) || lmsC2sSummaryProcName.isEmpty())) {
                        if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                            dbtype = "{call " + Constants.getProperty("currentschema") + "." + lmsC2sSummaryProcName + "(?,?,?,?)}";
                            cstmt = con.prepareCall(dbtype);
                        }else if(QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                        	 dtype = "{call " + lmsC2sSummaryProcName + "(?)}";
                             cstmt = con.prepareCall(dtype);
                        } 
                        else {
                            dtype = "{call " + lmsC2sSummaryProcName + "(?,?,?,?)}";
                            cstmt = con.prepareCall(dtype);
                        }
                    }
                    if (_logger.isDebugEnabled()) {
                        _logger.debug(METHOD_NAME, "Before Exceuting Procedure , lmsC2sSummaryProcName= " + lmsC2sSummaryProcName);
                    }
                    cstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_date));
                    if(!QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                    cstmt.registerOutParameter(2, Types.VARCHAR); // Status
                    cstmt.registerOutParameter(3, Types.VARCHAR); // return message
                    cstmt.registerOutParameter(4, Types.VARCHAR); // SQL exception
                    cstmt.executeUpdate();
                    isSuccess = cstmt.getString(2);
                    returnMess = cstmt.getString(3);
                    returnError = cstmt.getString(4);
                    }
                    else{
                 	    rs2 = cstmt.executeQuery();
                        if(rs2.next()){
                        	isSuccess =rs2.getString("rtn_message");
                        	returnMess=rs2.getString("rtn_messageforlog");
                        	returnError=rs2.getString("rtn_sqlerrmsgforlog");
                         }
                    }
                    if (_logger.isDebugEnabled()) {
                        _logger.debug(METHOD_NAME, "After Exceuting Procedure , lmsC2sSummaryProcName= " + lmsC2sSummaryProcName);
                    }
                    
                    if (_logger.isDebugEnabled()) {
                        _logger.debug(METHOD_NAME, "isSuccess= " + isSuccess + " , returnMess= " + returnMess + " , returnError= " + returnError);
                    }
                    if (!"SUCCESS".equalsIgnoreCase(isSuccess)) {
                        processStatusDAO = null;
                        con.rollback();
                        throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, "Procedure Execution Fail");
                    } else {

                        lmsC2sSummaryProcName = "update_accpnt_dly_c2s_lms_smry";
                        if (!(BTSLUtil.isNullString(lmsC2sSummaryProcName) || lmsC2sSummaryProcName.isEmpty())) {
                            if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                                dbtype = "{call " + Constants.getProperty("currentschema") + "." + lmsC2sSummaryProcName + "(?,?,?,?)}";
                                cstmt = con.prepareCall(dbtype);
                            } else if(QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                            	 dtype = "{call " + lmsC2sSummaryProcName + "(?)}";
                                 cstmt = con.prepareCall(dtype);
                            } 
                            else {
                                dtype = "{call " + lmsC2sSummaryProcName + "(?,?,?,?)}";
                                cstmt = con.prepareCall(dtype);
                            }
                        }
                        
                        if (_logger.isDebugEnabled()) {
                            _logger.debug(METHOD_NAME, "Before Exceuting Procedure , lmsC2sSummaryProcName= " + lmsC2sSummaryProcName);
                        }
                        cstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_date)); // Date  for which the procedure is being executed

                        if(!QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                        cstmt.registerOutParameter(2, Types.VARCHAR); // Status
                        cstmt.registerOutParameter(3, Types.VARCHAR); // return message
                        cstmt.registerOutParameter(4, Types.VARCHAR); // SQL exception
                        cstmt.executeUpdate();
                        isSuccess = cstmt.getString(2);
                        returnMess = cstmt.getString(3);
                        returnError = cstmt.getString(4);
                        }
                        else
                        {
                        	    rs3 = cstmt.executeQuery();
                               if(rs.next()){
                            	   isSuccess =rs3.getString("rtn_message");
                            	   returnMess = rs3.getString("rtn_messageforlog");
                            	   returnError = rs3.getString("rtn_sqlerrmsgforlog");
                                 }
                        }
                        if (_logger.isDebugEnabled()) {
                            _logger.debug(METHOD_NAME, "After Exceuting Procedure , lmsC2sSummaryProcName= " + lmsC2sSummaryProcName);
                        }
                      
                        if (_logger.isDebugEnabled()) {
                            _logger
                                .debug(
                                    METHOD_NAME,
                                    "isSuccess= " + isSuccess + " , returnMess= " + returnMess + " , returnError= " + returnError + " , lmsC2sSummaryProcName= " + lmsC2sSummaryProcName);
                        }
                        if (!"SUCCESS".equalsIgnoreCase(isSuccess)) {
                            processStatusDAO = null;
                            con.rollback();
                            throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, "Procedure Execution Fail");
                        }

                    }
                    if (!"SUCCESS".equalsIgnoreCase(isSuccess)) {
                        processStatusDAO = null;
                        con.rollback();
                        throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, "Procedure Execution Fail");
                    }
                    // method call to update maximum date till which process has
                    // been executed
                    _processStatusVO.setExecutedUpto(p_date);
                    _processStatusVO.setExecutedOn(BTSLUtil.addDaysInUtilDate(p_date, 1));
                    processStatusDAO = new ProcessStatusDAO();
                    maxDoneDateUpdateCount = processStatusDAO.updateProcessDetail(con, _processStatusVO);

                    // if the process is successful, transaction is commit, else
                    // rollback
                    if (maxDoneDateUpdateCount > 0) {
                        con.commit();
                        processStatusDAO = null;
                    } else {
                        processStatusDAO = null;
                        con.rollback();
                        throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.LMS_C2S_SUMMARY_COULD_NOT_UPDATE_MAX_DONE_DATE);
                    }
                    // DWH002 sleep has been added after processing records of
                    // one day
                    Thread.sleep(100);

                }
            }
		} else	{
			throw new BTSLBaseException(METHOD_NAME,"process",PretupsErrorCodesI.LMS_MIS_DEPENDENCY);
		}
		}//end of try
        catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DailyC2SLmsSummary[process]", "", "", "",
                " DailyC2SLmsSummary process could not be executed successfully.");
            throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.LMS_C2S_SUMMARY_ERROR_EXCEPTION);
        } finally {
            // if the status was marked as under process by this method call,
            // only then it is marked as complete on termination
            if (statusOk) {
                try {
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
                } catch (Exception e) {
                    _logger.errorTrace(METHOD_NAME, e);
                }
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (Exception ex) {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug(METHOD_NAME, "Exception closing connection ");
                    }
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rs2 != null) {
                    rs2.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (rs3 != null) {
                    rs3.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (cstmt != null) {
                    cstmt.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            _logger.debug(METHOD_NAME, "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... ");
            }
        }
    }

    /**
     * @param p_con
     *            Connection
     * @param p_processId
     *            String
     * @throws BTSLBaseException
     * @return int
     */
    private static int markProcessStatusAsComplete(Connection p_con, String p_processId) throws BTSLBaseException {
        final String METHOD_NAME = "markProcessStatusAsComplete";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entered:  p_processId:" + p_processId);
        }
        int updateCount = 0;
        final Date currentDate = new Date();
        final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        _processStatusVO.setProcessID(p_processId);
        _processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        _processStatusVO.setStartDate(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetail(p_con, _processStatusVO);
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error(METHOD_NAME, "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyC2SLmsSummary[markProcessStatusAsComplete]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(METHOD_NAME, METHOD_NAME, PretupsErrorCodesI.LMS_C2S_SUMMARY_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;

    }

}

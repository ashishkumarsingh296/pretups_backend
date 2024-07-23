package com.btsl.pretups.iat.processes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
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

public class IATDWHFileCreation {
    private static String _iatDWHFileLabelForTransaction = null;
    private static String _iatDWHFileNameForTransaction = null;
    private static String _iatFinalMasterDirectoryPath = null;
    private static String _iatMasterDirectoryPathAndName = null;
    private static String _iatChildDirectory = null;
    private static String _iatFileEXT = null;
    private static long _iatMaxFileLength = 0;
    private static ArrayList _iatFileNameLst = new ArrayList();
    private static ProcessStatusVO _processStatusVO;
    private static ProcessBL _processBL = null;
    // Option for generating DWH if there is Ambiguous or Under Process Txn.
    private static boolean _iatAmbiUnderTxnFound = true;

    /*
     * This process will be used for generating Dataware house files for IAT
     * transactions.
     * It will generate the files in specified location ,operator can download
     * these file for processing.
     * It will load the transactions details from database and write it in to
     * excel sheet.
     */

    private static Log _log = LogFactory.getLog(IATDWHFileCreation.class.getName());

    /**
   	 * ensures no instantiation
   	 */
    private IATDWHFileCreation(){
    	
    }
    public static void main(String arg[]) {
        final String methodName = "main";
        try {
            // ID DWH003 third argument is to generate DWH even there is
            // Ambiguous or Under Process Txn.
            if (arg.length != 3) {
                if (arg.length != 2) {
                    System.out.println("Usage : IATDWHFileCreation [Constants file] [LogConfig file]");
                    return;
                }
            }
            File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
                System.out.println("IATDWHFileCreation" + " Constants File Not Found .............");
                return;
            }
            File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                System.out.println("IATDWHFileCreation" + " Logconfig File Not Found .............");
                return;
            }
            // if third argument is not set then try to set it with Default
            // value true;
            try {
                if (PretupsI.YES.equalsIgnoreCase(arg[2])) {
                    _iatAmbiUnderTxnFound = false;
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                _iatAmbiUnderTxnFound = true;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        }// end of try
        catch (Exception e) {
            _log.error(methodName, " Error in Loading Files ...........................: " + e.getMessage());
            _log.errorTrace(methodName, e);
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {
            process();
        } catch (BTSLBaseException be) {
            if (_log.isErrorEnabled()) {
                _log.error(methodName, "BTSLBaseException : " + be.getMessage());
            }
            _log.errorTrace(methodName, be);
        } finally {
            if (_log.isErrorEnabled()) {
                _log.error(methodName, "Exiting..... ");
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * process
     * This method will responsible for generation DWH files.
     * 
     * @return void
     * @throws BTSLBaseException
     */
    private static void process() throws BTSLBaseException {
        Date processedUpto = null;
        Date dateCount = null;
        Date currentDate = new Date();
        Connection con = null;
        String processId = null;
        boolean statusOk = false;
        ProcessStatusDAO processStatusDAO = null;
        int beforeInterval = 0;

        int maxDoneDateUpdateCount = 0;

       CallableStatement cstmt = null;
        String iatDwhProcName = null;
        ResultSet rs = null;
        PreparedStatement pstmnt = null;
        
        final String methodName = "process";
        String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
        long iatTransCount=0 ;
        String isSuccess=null ;
        
        try {
            if (_log.isDebugEnabled()) {
            	StringBuilder sb = new StringBuilder("");
            	sb.append("Memory at startup: Total:");
            	sb.append(Runtime.getRuntime().totalMemory() / 1049576);
            	sb.append(" Free:");
            	sb.append(Runtime.getRuntime().freeMemory() / 1049576);
                _log.debug(methodName, sb.toString());
            }
            currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);
            // getting all the required parameters from Constants.props
            loadConstantParameters();

            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATDWHFileCreation[process]", "", "", "", "DATABASE Connection is NULL");
                return;
            }
            // getting process id
            processId = ProcessI.IAT_DWH_PROCESSID;
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
                    // ID DWH002 to check whether process has been executed till
                    // current date or not
                    processedUpto = BTSLUtil.addDaysInUtilDate(processedUpto, 1);
                    if (BTSLUtil.getDifferenceInUtilDates(processedUpto, currentDate) == 0) {
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "IATDWHFileCreation[process]", "", "", "", " IATDWHFileCreation process has been already executed.");
                        throw new BTSLBaseException("IATDWHFileCreation", methodName, PretupsErrorCodesI.IAT_DWH_PROCESS_ALREADY_EXECUTED_TILL_TODAY);
                    }
                    // adding 1 in processed upto dtae as we have to start from
                    // the next day till which process has been executed

                    // loop to be started for each date
                    // the loop starts from the date till which process has been
                    // executed and executes one day before current date
                    boolean ambiUnderTxnFound = false;
                    for (dateCount = BTSLUtil.getSQLDateFromUtilDate(processedUpto); dateCount.before(BTSLUtil.addDaysInUtilDate(currentDate, -beforeInterval)); dateCount = BTSLUtil.addDaysInUtilDate(dateCount, 1)) {

                        iatDwhProcName = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.IATDWH_OPT_SPECIFIC_PROC_NAME));
                        StringBuilder query=new StringBuilder();
                        if (!(BTSLUtil.isNullString(iatDwhProcName) || "".equals(iatDwhProcName))) {
                            if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                                query = query.append("{call " ).append( Constants.getProperty("currentschema")).append( ".").append(iatDwhProcName).append("(?,?,?)}");
                                cstmt = con.prepareCall(query.toString());
                            } else if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                            	query=query.append("{call ").append(iatDwhProcName).append("(?)}");
                            	cstmt = con.prepareCall(query.toString());
                            } 
                            else {
                            	query=query.append("{call ").append(iatDwhProcName).append("(?,?,?)}");
                                cstmt = con.prepareCall(query.toString());
                            }
                        } else {
                            if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                            	query=query.append("{call").append(Constants.getProperty("currentschema")).append(".Iatdwhtempprc(?,?,?)}");
                                cstmt = con.prepareCall(query.toString());
                            }else if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                            	query=query.append("{call  iatDwhProcName(?)}");
                            	cstmt = con.prepareCall(query.toString());
                            }  else {
                                cstmt = con.prepareCall("{call Iatdwhtempprc(?,?,?)}");
                            }
                        }
                        
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "Before Exceuting Procedure");
                        }

                        cstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(dateCount));
                       
                        if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                             rs = cstmt.executeQuery();
                            if(rs.next()){
                            	iatTransCount =rs.getLong("p_iattranscnt");
                            	isSuccess=rs.getString("p_message");
                             }
                        }
                        else{
                         cstmt.registerOutParameter(2, Types.NUMERIC); // Count
                         cstmt.registerOutParameter(3, Types.VARCHAR); // Status
                         cstmt.executeUpdate();
                         iatTransCount = cstmt.getLong(2);
                         isSuccess = cstmt.getString(3);
                        }
                        
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "After Exceuting Procedure");
                        }

                        if (!"SUCCESS".equalsIgnoreCase(isSuccess)) {
                        	 if (QueryConstants.DB_POSTGRESQL.equals(dbConnected))
                        	 {
                        		 if (con != null) 
                                     con.rollback();
                        	 }
                            throw new BTSLBaseException("IATDWHFileCreation", methodName, "Procedure Execution Fail");
                        }

                        if (_iatAmbiUnderTxnFound) {
                            ambiUnderTxnFound = checkUnderprocessTransaction(con, dateCount);
                        }
                        if (!ambiUnderTxnFound) {
                            // method call to create master directory and child
                            // directory if does not exist
                            _iatChildDirectory = createDirectory(_iatMasterDirectoryPathAndName, processId, dateCount);
                            // method call to fetch transaction data and write
                            // it in files
                            int fileNumber = 1;
                            for (long i = 0, j = _iatMaxFileLength; i < iatTransCount; i += _iatMaxFileLength) {
                                fetchIATTransactionData(con, dateCount, _iatChildDirectory, _iatDWHFileNameForTransaction, _iatDWHFileLabelForTransaction, _iatFileEXT, i, j, fileNumber);
                                fileNumber++;
                                if ((j + _iatMaxFileLength) < iatTransCount) {
                                    j += _iatMaxFileLength;
                                } else if (j != iatTransCount) {
                                    j = iatTransCount;
                                }
                            }

                            // method call to update maximum date till which
                            // process has been executed
                            _processStatusVO.setExecutedUpto(dateCount);
                            _processStatusVO.setExecutedOn(currentDate);
                            processStatusDAO = new ProcessStatusDAO();
                            maxDoneDateUpdateCount = processStatusDAO.updateProcessDetail(con, _processStatusVO);

                            // if the process is successful, transaction is
                            // commit, else rollback
                            if (maxDoneDateUpdateCount > 0) {
                                moveFilesToFinalDirectory(_iatMasterDirectoryPathAndName, _iatFinalMasterDirectoryPath, processId, dateCount);
                                con.commit();
                            } else {
                                deleteAllFiles();
                                con.rollback();
                                throw new BTSLBaseException("IATDWHFileCreation", methodName, PretupsErrorCodesI.DWH_COULD_NOT_UPDATE_MAX_DONE_DATE);
                            }
                            // DWH002 sleep has been added after processing
                            // records of one day
                            Thread.sleep(500);
                        }// end if
                    }// end loop
                    
                    if (QueryConstants.DB_POSTGRESQL.equals(dbConnected) && con != null )
                        con.commit();
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "IATDWHFileCreation[process]", "", "", "", " IATDWHFileCreation process has been executed successfully.");
                }
                // ID DWH002 to avoid the null pointer exception thrown, in case
                // processesUpto is null
                else {
                    throw new BTSLBaseException("IATDWHFileCreation", methodName, PretupsErrorCodesI.DWH_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
                }
            }
        }// end of try
        catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException : " + be.getMessage());
            _log.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            if (_iatFileNameLst != null && !_iatFileNameLst.isEmpty()) {
                deleteAllFiles();
            }
            _log.error(methodName, "Exception : " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "IATDWHFileCreation[process]", "", "", "", " IATDWHFileCreation process could not be executed successfully.");
            throw new BTSLBaseException("IATDWHFileCreation", methodName, PretupsErrorCodesI.IAT_DWH_ERROR_EXCEPTION);
        } finally {
            // if the status was marked as under process by this method call,
            // only then it is marked as complete on termination
            if (statusOk) {
                try {
                    if (markProcessStatusAsComplete(con, processId) == 1) {
                        try {
                            con.commit();
                        } catch (Exception e) {
                            _log.errorTrace(methodName, e);
                        }
                    } else {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            _log.errorTrace(methodName, e);
                        }
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);

                }
            }
            try {
                if (cstmt != null) {
                    cstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Exception closing connection ");
                }
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
            	StringBuilder sb = new StringBuilder("");
            	sb.append("Memory at end: Total:");
            	sb.append(Runtime.getRuntime().totalMemory() / 1049576);
            	sb.append(" Free:");
            	sb.append(Runtime.getRuntime().freeMemory() / 1049576);
                _log.debug(methodName, sb.toString());
                _log.debug(methodName, "Exiting..... ");
            }
        }
    }

    /**
     * loadConstantParameters
     * This method will load the Constant parameter from Constant.props File
     * 
     * @return void
     * @throws BTSLBaseException
     */
    private static void loadConstantParameters() throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadParameters", " Entered: ");
        }
        final String methodName = "loadConstantParameters";
        try {
            _iatDWHFileLabelForTransaction = Constants.getProperty("IAT_DWH_TRANSACTION_FILE_LABEL");
            if (BTSLUtil.isNullString(_iatDWHFileLabelForTransaction)) {
                _log.error(methodName, " Could not find file label for transaction data in the Constants file.");
            } else {
                _log.debug("main", " _iatDWHFileLabelForTransaction=" + _iatDWHFileLabelForTransaction);
            }

            _iatDWHFileNameForTransaction = Constants.getProperty("IAT_DWH_TRANSACTION_FILE_NAME");
            if (BTSLUtil.isNullString(_iatDWHFileNameForTransaction)) {
                _log.error(methodName, " Could not find file name for transaction data in the Constants file.");
            } else {
                _log.debug(methodName, " _iatDWHFileNameForTransaction=" + _iatDWHFileNameForTransaction);
            }

            _iatMasterDirectoryPathAndName = Constants.getProperty("IAT_DWH_MASTER_DIRECTORY");
            if (BTSLUtil.isNullString(_iatMasterDirectoryPathAndName)) {
                _log.error(methodName, " Could not find directory path in the Constants file.");
            } else {
                _log.debug(methodName, " _iatMasterDirectoryPathAndName=" + _iatMasterDirectoryPathAndName);
            }
            _iatFinalMasterDirectoryPath = Constants.getProperty("IAT_DWH_FINAL_DIRECTORY");
            if (BTSLUtil.isNullString(_iatFinalMasterDirectoryPath)) {
                _log.error(methodName, " Could not find final directory path in the Constants file.");
            } else {
                _log.debug(methodName, " finalMasterDirectoryPath=" + _iatFinalMasterDirectoryPath);
            }

            // checking that none of the required parameters should be null
            if (BTSLUtil.isNullString(_iatDWHFileLabelForTransaction) || BTSLUtil.isNullString(_iatDWHFileNameForTransaction) || BTSLUtil.isNullString(_iatMasterDirectoryPathAndName) || BTSLUtil.isNullString(_iatFinalMasterDirectoryPath)) {
                throw new BTSLBaseException("IATDWHFileCreation", methodName, PretupsErrorCodesI.DWH_COULD_NOT_FIND_DATA_IN_CONSTANTS_FILE);
            }
            try {
                _iatFileEXT = Constants.getProperty("IAT_DWH_FILE_EXT");
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                _iatFileEXT = ".csv";
            }
            _log.debug(methodName, " _iatFileEXT=" + _iatFileEXT);
            try {
                _iatMaxFileLength = Long.parseLong(Constants.getProperty("IAT_DWH_MAX_FILE_LENGTH"));
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                _iatMaxFileLength = 1000;
            }
            _log.debug(methodName, " _iatMaxFileLength=" + _iatMaxFileLength);
            _log.debug(methodName, " Required information successfuly loaded from Constants.props...............: ");
        } catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException : " + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATDWHFileCreation[loadConstantParameters]", "", "", "", "Message:" + be.getMessage());
            _log.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            _log.error(methodName, "Exception : " + e.getMessage());
            _log.errorTrace(methodName, e);
            BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.IAT_DWH_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATDWHFileCreation[loadConstantParameters]", "", "", "", "Message:" + btslMessage);
            throw new BTSLBaseException("IATDWHFileCreation", methodName, PretupsErrorCodesI.IAT_DWH_ERROR_EXCEPTION);
        }

    }

    /**
     * This method will check the existance of under process and/or ambiguous
     * transaction for the given date
     * for the date for which method is called
     * 
     * @param p_con
     *            Connection
     * @param p_beingProcessedDate
     *            Date
     * @return boolean
     * @throws BTSLBaseException
     */
    private static boolean checkUnderprocessTransaction(Connection p_con, Date p_beingProcessedDate) throws BTSLBaseException {
    	//local_index_implemented
        final String methodName = "checkUnderprocessTransaction";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered: p_beingProcessedDate=" + p_beingProcessedDate);
        }
        PreparedStatement selectPstmt = null;
        ResultSet selectRst = null;
        boolean transactionFound = false;
        StringBuilder selectQuery = new StringBuilder();
        try {
            // to make get the status from the file not be hardcoded
            selectQuery = selectQuery.append("SELECT 1 FROM c2s_transfers WHERE transfer_date=? AND transfer_status IN('").append(PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS).append("','").append(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS).append("') AND EXT_CREDIT_INTFCE_TYPE='").append(PretupsI.IAT_TRANSACTION_TYPE ).append("'");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "select query:" + selectQuery);
            }
            selectPstmt = p_con.prepareStatement(selectQuery.toString());
            selectPstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate));
            selectRst = selectPstmt.executeQuery();
            if (selectRst.next()) {
                transactionFound = true;
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATDWHFileCreation[checkUnderprocessTransaction]", "", "", "", "Message: IATDWHFileCreation process cannot continue as underprocess and/or ambiguous transactions are found.");
                throw new BTSLBaseException("IATDWHFileCreation", methodName, PretupsErrorCodesI.DWH_AMB_OR_UP_TXN_FOUND);
            }
        } catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException : " + be.getMessage());
            _log.errorTrace(methodName, be);
            throw be;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException " + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATDWHFileCreation[checkUnderprocessTransaction]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("IATDWHFileCreation", methodName, PretupsErrorCodesI.IAT_DWH_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex.getMessage());
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATDWHFileCreation[checkUnderprocessTransaction]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("IATDWHFileCreation", methodName, PretupsErrorCodesI.IAT_DWH_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (selectRst != null) {
                try {
                    selectRst.close();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
            if (selectPstmt != null) {
                try {
                    selectPstmt.close();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting transactionFound=" + transactionFound);
            }
        }// end of finally
        return transactionFound;
    }

    /**
     * This method will create master and child directory at the path defined in
     * Constants.props, if it does not exist
     * 
     * @param p_directoryPathAndName
     *            String
     * @param p_processId
     *            String
     * @param p_beingProcessedDate
     *            Date
     * @throws BTSLBaseException
     * @return String
     */
    private static String createDirectory(String p_directoryPathAndName, String p_processId, Date p_beingProcessedDate) throws BTSLBaseException {
        final String methodName = "createDirectory";
        if (_log.isDebugEnabled()) {
        	StringBuilder sb = new StringBuilder("");
        	sb.append(" Entered: p_directoryPathAndName=");
        	sb.append(p_directoryPathAndName);
        	sb.append(" p_processId=");
        	sb.append(p_processId);
        	sb.append(" p_beingProcessedDate=");
        	sb.append(p_beingProcessedDate);
            _log.debug(methodName, sb.toString());
        }
        String dirName = null;
        try {
            boolean success = false;
            File parentDir = new File(p_directoryPathAndName);
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            p_beingProcessedDate = BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate);
            // child directory name includes a file name and being processed
            // date, month and year
//            dirName = p_directoryPathAndName + File.separator + p_processId + "_" + p_beingProcessedDate.toString().substring(8, 10) + p_beingProcessedDate.toString().substring(5, 7) + p_beingProcessedDate.toString().substring(2, 4);
            StringBuilder sb = new StringBuilder("");
            sb.append(p_directoryPathAndName);
            sb.append(File.separator);
            sb.append(p_processId);
            sb.append("_");
            sb.append(BTSLUtil.getDateStrForName(p_beingProcessedDate));
            dirName = sb.toString();
            File newDir = new File(dirName);
            if (!newDir.exists()) {
                success = newDir.mkdirs();
            } else {
                success = true;
            }
            if (!success) {
                throw new BTSLBaseException("IATDWHFileCreation", methodName, PretupsErrorCodesI.COULD_NOT_CREATE_DIR);
            }
        } catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException : " + be.getMessage());
            _log.errorTrace(methodName, be);
            throw be;
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex.getMessage());
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATDWHFileCreation[createDirectory]", "", "", "", "SQLException:" + ex.getMessage());
            throw new BTSLBaseException("IATDWHFileCreation", methodName, PretupsErrorCodesI.IAT_DWH_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting dirName=" + dirName);
            }
        }
        return dirName;
    }

    /**
     * This method will fetch all the required transactions data from database
     * 
     * @param p_con
     *            Connection
     * @param p_beingProcessedDate
     *            Date
     * @param p_dirPath
     *            String
     * @param p_fileName
     *            String
     * @param p_fileLabel
     *            String
     * @param p_fileEXT
     *            String
     * @param p_startRowNum
     *            long
     * @param p_endRowNum
     *            long
     * @param p_fileNumber
     *            int
     * 
     * @return void
     * @throws SQLException
     *             ,Exception
     */
    private static void fetchIATTransactionData(Connection p_con, Date p_beingProcessedDate, String p_dirPath, String p_fileName, String p_fileLabel, String p_fileEXT, long p_startRowNum, long p_endRowNum, int p_fileNumber) throws BTSLBaseException {
        final String methodName = "fetchIATTransactionData";
        if (_log.isDebugEnabled()) {
        	StringBuilder sb = new StringBuilder("");
        	sb.append(" Entered: p_beingProcessedDate=");
        	sb.append(p_beingProcessedDate);
        	sb.append(" p_dirPath=");
        	sb.append(p_dirPath);
        	sb.append(" p_fileName=");
        	sb.append(p_fileName);
        	sb.append(" p_fileLabel=");
        	sb.append(p_fileLabel);
        	sb.append(" p_fileEXT=");
        	sb.append(p_fileEXT);
        	sb.append(" p_startRowNum=");
        	sb.append(p_startRowNum);
        	sb.append(" p_endRowNum=");
        	sb.append(p_endRowNum);
        	sb.append(" p_fileNumber=");
        	sb.append(p_fileNumber);
        	_log.debug(methodName,sb.toString());
        }

        StringBuffer iatQueryBuf = new StringBuffer();
        iatQueryBuf.append(" SELECT DATA,TRANSFER_STATUS,BONUS_DETAILS FROM TEMP_IAT_DWH_IATTRANS");
        iatQueryBuf.append(" WHERE SRNO > ? and SRNO <= ? ");
        String iatSelectQuery = iatQueryBuf.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "iat select query:" + iatSelectQuery);
        }
        PreparedStatement c2sSelectPstmt = null;
        ResultSet c2sSelectRst = null;
        try {
            c2sSelectPstmt = p_con.prepareStatement(iatSelectQuery);
            c2sSelectPstmt.setLong(1, p_startRowNum);
            c2sSelectPstmt.setLong(2, p_endRowNum);
            c2sSelectRst = c2sSelectPstmt.executeQuery();
            StringBuilder sb = new StringBuilder("");
            sb.append("Memory after loading channel transaction data: Total:");
            sb.append(Runtime.getRuntime().totalMemory() / 1049576);
            sb.append(" Free:");
            sb.append(Runtime.getRuntime().freeMemory() / 1049576);
            sb.append(" for date:");
            sb.append(p_beingProcessedDate);
            _log.debug(methodName, sb.toString());

            // method call to write data in the files
            writeDataInFile(p_dirPath, p_fileName, p_fileLabel, p_beingProcessedDate, p_fileEXT, p_fileNumber, c2sSelectRst);
            StringBuilder sb1 = new StringBuilder("");
            sb1.append("Memory after writing transaction files: Total:");
            sb1.append(Runtime.getRuntime().totalMemory() / 1049576);
            sb1.append(" Free:");
            sb1.append(Runtime.getRuntime().freeMemory() / 1049576);
            sb1.append(" for date:");
            sb1.append(p_beingProcessedDate);
            _log.debug(methodName, sb.toString());
             
        } catch (BTSLBaseException be) {
            _log.error(methodName, "BTSLBaseException : " + be.getMessage());
            _log.errorTrace(methodName, be);
            throw be;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException " + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[fetchIATTransactionData]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("DWHFileCreation", methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex.getMessage());
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[fetchIATTransactionData]", "", "", "", "SQLException:" + ex.getMessage());
            throw new BTSLBaseException("DWHFileCreation", methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (c2sSelectRst != null) {
                try {
                    c2sSelectRst.close();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }
            if (c2sSelectPstmt != null) {
                try {
                    c2sSelectPstmt.close();
                } catch (Exception ex) {
                    _log.errorTrace(methodName, ex);
                }
            }

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting ");
            }
        }// end of finally
    }

    /**
     * @param p_dirPath
     *            String
     * @param p_fileName
     *            String
     * @param p_fileLabel
     *            String
     * @param p_beingProcessedDate
     *            Date
     * @param p_fileEXT
     *            String
     * @param p_maxFileLength
     *            long
     * @param rst1
     *            ResultSet
     * @param rst2
     *            ResultSet
     * @return void
     * @throws Exception
     */
    private static void writeDataInFile(String p_dirPath, String p_fileName, String p_fileLabel, Date p_beingProcessedDate, String p_fileEXT, int p_fileNumber, ResultSet rst1) throws BTSLBaseException {
        final String methodName = "writeDataInFile";
        if (_log.isDebugEnabled()) {
        	StringBuilder sb = new StringBuilder("");
        	sb.append(" Entered:  p_dirPath=");
        	sb.append(p_dirPath);
        	sb.append(" p_fileName=");
        	sb.append(p_fileName);
        	sb.append(" p_fileLabel=");
        	sb.append(p_fileLabel);
        	sb.append(" p_beingProcessedDate=");
        	sb.append(p_beingProcessedDate);
        	sb.append(" p_fileEXT=");
        	sb.append(p_fileEXT);
        	sb.append(" p_fileNumber=");
        	sb.append(p_fileNumber);
            _log.debug(methodName,sb.toString());
        }

        long recordsWrittenInFile = 0;
        PrintWriter out = null;
        int fileNumber = 1;
        String fileName = null;
        File newFile = null;
        String fileData = null;
        String fileHeader = null;
        String fileFooter = null;

        try {
            // generating file name
            fileNumber = p_fileNumber;
            // if the length of file number is 1, two zeros are added as prefix
            if (Integer.toString(fileNumber).length() == 1) {
                fileName = p_dirPath + File.separator + p_fileName + "00" + fileNumber + p_fileEXT;
            } else if (Integer.toString(fileNumber).length() == 2) {
                fileName = p_dirPath + File.separator + p_fileName + "0" + fileNumber + p_fileEXT;
            } else if (Integer.toString(fileNumber).length() == 3) {
                fileName = p_dirPath + File.separator + p_fileName + fileNumber + p_fileEXT;
            }

            _log.debug(methodName, "  fileName=" + fileName);

            newFile = new File(fileName);
            _iatFileNameLst.add(fileName);
            //
            out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));

            // ID DWH002 to make addition of header and footer optional on the
            // basis of entry in Constants.props
            if ("Y".equalsIgnoreCase(Constants.getProperty("ADD_HEADER_FOOTER"))) {
                fileHeader = constructFileHeader(p_beingProcessedDate, fileNumber, p_fileLabel);
                out.write(fileHeader);
            }
            // traverse first resultset
            while (rst1 != null && rst1.next()) {
                fileData = rst1.getString("DATA");
                out.write(fileData + "\n");
                out.flush();
                recordsWrittenInFile++;
            }

            // if number of records are not zero then footer is appended as file
            // is deleted
            if (recordsWrittenInFile > 0) {
                // ID DWH002 to make addition of header and footer optional on
                // the basis of entry in Constants.props
                if ("Y".equalsIgnoreCase(Constants.getProperty("ADD_HEADER_FOOTER"))) {
                    fileFooter = constructFileFooter(recordsWrittenInFile);
                    out.write(fileFooter);
                }
            } else {
                if (out != null) {
                    out.close();
                }
                boolean isDeleted = newFile.delete();
                if(isDeleted){
                 _log.debug(methodName, "File deleted successfully");
                }
                _iatFileNameLst.remove(_iatFileNameLst.size() - 1);
            }
            if (out != null) {
                out.close();
            }
        } catch (Exception e) {
            deleteAllFiles();
            _log.debug(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATDWHFileCreation[writeDataInFile]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("IATDWHFileCreation", methodName, PretupsErrorCodesI.IAT_DWH_ERROR_EXCEPTION);
        } finally {
            if (out != null) {
                out.close();
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting ");
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
        final String methodName = "markProcessStatusAsComplete";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered:  p_processId:" + p_processId);
        }
        int updateCount = 0;
        Date currentDate = new Date();
        ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        _processStatusVO.setProcessID(p_processId);
        _processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        _processStatusVO.setStartDate(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetail(p_con, _processStatusVO);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATDWHFileCreation[markProcessStatusAsComplete]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("IATDWHFileCreation", methodName, PretupsErrorCodesI.IAT_DWH_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;

    }

    /**
     * This method will delete all the files if some error is encountered
     * after file creation and files need to be deleted.
     * 
     * @throws BTSLBaseException
     * @return void
     */
    private static void deleteAllFiles() throws BTSLBaseException {
        final String methodName = "deleteAllFiles";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Entered: ");
        }
        int size = 0;
        if (_iatFileNameLst != null) {
            size = _iatFileNameLst.size();
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " : Number of files to be deleted " + size);
        }
        String fileName = null;
        File newFile = null;
        for (int i = 0; i < size; i++) {
            try {
                fileName = (String) _iatFileNameLst.get(i);
                newFile = new File(fileName);
                boolean isDeleted = newFile.delete();
                if(isDeleted){
                 _log.debug(methodName, "File deleted successfully");
                }
                if (_log.isDebugEnabled()) {
                    _log.debug("", fileName + " file deleted");
                }
            } catch (Exception e) {
                _log.error(methodName, "Exception " + e.getMessage());
                _log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATDWHFileCreation[deleteAllFiles]", "", "", "", "Exception:" + e.getMessage());
                throw new BTSLBaseException("IATDWHFileCreation", methodName, PretupsErrorCodesI.IAT_DWH_ERROR_EXCEPTION);
            }
        }// end of for loop
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATDWHFileCreation[deleteAllFiles]", "", "", "", " Message: IATDWHFileCreation process has found some error, so deleting all the files.");
        if (_iatFileNameLst != null && _iatFileNameLst.isEmpty()) {
            _iatFileNameLst.clear();
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " : Exiting.............................");
        }
    }

    /**
     * This method is used to constuct file header
     * 
     * @param p_beingProcessedDate
     *            Date
     * @param p_fileNumber
     *            long
     * @param p_fileLabel
     *            String
     * @return String
     */
    private static String constructFileHeader(Date p_beingProcessedDate, long p_fileNumber, String p_fileLabel) {
        SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
        StringBuffer fileHeaderBuf = new StringBuffer("");
        fileHeaderBuf.append("\n" + " Present Date=" + BTSLDateUtil.getSystemLocaleDate(sdf.format(new Date())));
        fileHeaderBuf.append("\n" + " For Date=" + BTSLDateUtil.getSystemLocaleDate(sdf.format(p_beingProcessedDate)));
        fileHeaderBuf.append("\n" + " File Number=" + p_fileNumber);
        fileHeaderBuf.append("\n" + p_fileLabel);
        fileHeaderBuf.append("\n" + "[STARTDATA]" + "\n");
        return fileHeaderBuf.toString();
    }

    /**
     * This method is used to constuct file footer
     * 
     * @param p_noOfRecords
     *            long
     * @return String
     */
    private static String constructFileFooter(long p_noOfRecords) {
        StringBuffer fileHeaderBuf = null;
        fileHeaderBuf = new StringBuffer("");
        fileHeaderBuf.append("[ENDDATA]" + "\n");
        fileHeaderBuf.append(" Number of records=" + p_noOfRecords);
        return fileHeaderBuf.toString();
    }

    /**
     * This method will copy all the created files to another location.
     * the process will generate files in a particular directroy. if the process
     * thats has to read files strarts before copletion of the file generation,
     * errors will occur. so a different directory is created and files are
     * moved to that final directory.
     * 
     * @param p_oldDirectoryPath
     *            String
     * @param p_finalDirectoryPath
     *            String
     * @param p_processId
     *            String
     * @param p_beingProcessedDate
     *            Date
     * @throws BTSLBaseException
     * @return String
     */
    private static void moveFilesToFinalDirectory(String p_oldDirectoryPath, String p_finalDirectoryPath, String p_processId, Date p_beingProcessedDate) throws BTSLBaseException {
        final String methodName = "moveFilesToFinalDirectory";
        if (_log.isDebugEnabled()) {
        	StringBuilder sb = new StringBuilder("");
        	sb.append(" Entered: p_oldDirectoryPath=");
        	sb.append(p_oldDirectoryPath);
        	sb.append(" p_finalDirectoryPath=");
        	sb.append(p_finalDirectoryPath);
        	sb.append(" p_processId=");
        	sb.append(p_processId);
        	sb.append(" p_beingProcessedDate=");
        	sb.append(p_beingProcessedDate);
            _log.debug(methodName, sb.toString());
        }

        String oldFileName = null;
        String newFileName = null;
        File oldFile = null;
        File newFile = null;
        File parentDir = new File(p_finalDirectoryPath);
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        p_beingProcessedDate = BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate);
        // child directory name includes a file name and being processed date,
        // month and year
        StringBuilder sb = new StringBuilder("");
        sb.append(p_oldDirectoryPath);
        sb.append(File.separator);
        sb.append(p_processId);
        sb.append("_");
        sb.append(p_beingProcessedDate.toString().substring(8, 10));
        sb.append(p_beingProcessedDate.toString().substring(5, 7));
        sb.append(p_beingProcessedDate.toString().substring(2, 4));
        String oldDirName = sb.toString();
        StringBuilder sb1 = new StringBuilder("");
        sb1.append(p_finalDirectoryPath);
        sb1.append(File.separator);
        sb1.append(p_processId);
        sb1.append("_");
        sb1.append(p_beingProcessedDate.toString().substring(8, 10));
        sb1.append(p_beingProcessedDate.toString().substring(5, 7));
        sb1.append(p_beingProcessedDate.toString().substring(2, 4));
        String newDirName = sb1.toString();
        File oldDir = new File(oldDirName);
        File newDir = new File(newDirName);
        if (!newDir.exists()) {
            newDir.mkdirs();
        }

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " newDirName=" + newDirName);
        }

        int size = _iatFileNameLst.size();
        try {
            for (int i = 0; i < size; i++) {
                oldFileName = (String) _iatFileNameLst.get(i);
                oldFile = new File(oldFileName);
                newFileName = oldFileName.replace(p_oldDirectoryPath, p_finalDirectoryPath);
                newFile = new File(newFileName);
                if(oldFile.renameTo(newFile))
                {
                	_log.debug(methodName, "File renamed successfully");
                }
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, " File " + oldFileName + " is moved to " + newFileName);
                }
            }// end of for loop
            _iatFileNameLst.clear();
            if (oldDir.exists()) {
            	boolean isDeleted = oldDir.delete();
                if(isDeleted){
                	_log.debug(methodName, "Directory deleted successfully");
                }
            }
            _log.debug(methodName, " File " + oldFileName + " is moved to " + newFileName);
        } catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATDWHFileCreation[moveFilesToFinalDirectory]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("IATDWHFileCreation", "deleteAllFiles", PretupsErrorCodesI.IAT_DWH_ERROR_EXCEPTION);
        } finally {
            if (oldFile != null) {
                oldFile = null;
            }
            if (newFile != null) {
                newFile = null;
            }
            if (parentDir != null) {
                parentDir = null;
            }
            if (newDir != null) {
                newDir = null;
            }
            if (oldDir != null) {
                oldDir = null;
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting.. ");
            }
        } // end of finally

    }
}

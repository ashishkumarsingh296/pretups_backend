package com.btsl.pretups.processes;

/**
 * @(#)LMBDWHFileCreation
 *                        Copyright(c) 2006, Bharti Telesoft Ltd.
 *                        All Rights Reserved
 * 
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Amit Singh 01/06/2006 Initial Creation
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
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

public class LMBDWHFileCreation {
    private static String _dwhFileLabelForTransaction = null;// use to store
    // the labels
    // for the SOS
    // transactions
    private static String _dwhFileNameForTransaction = null;// use to store the
    // file name for the
    // SOS transactions
    private static String _masterDirectoryPathAndName = null;// use to store
    // the master
    // directory
    // path and name
    // in which the
    // master and
    // transaction
    // data files
    // will be
    // stored
    private static String _finalMasterDirectoryPath = null;// use to store the
    // final master
    // directory path in
    // which the master
    // and transaction
    // data files will
    // be moved after
    // all files
    // creation
    private static String _childDirectory = null;// use to store the master
    // directory path and name
    // in which the master and
    // transaction data files
    // will be stored
    private static String _fileEXT = null;// use to store the extension of the
    // files, which are going to create
    // by the process
    private static long _maxFileLength = 0;// use to store the maximum no. of
    // records a file can contain
    private static ArrayList _fileNameLst = new ArrayList();// use to store the
    // all names of the
    // files
    private static ProcessStatusVO _processStatusVO;
    private static ProcessBL _processBL = null;
    private static Log _logger = LogFactory.getLog(LMBDWHFileCreation.class.getName());

    /**
     * ensures no instantiation
     */
    private LMBDWHFileCreation(){
    	
    } 	
    public static void main(String arg[]) {
        try {
            if (arg.length != 2) {
                System.out.println("Usage : LMBDWHFileCreation [Constants file] [LogConfig file]");
                return;
            }
            final File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
                System.out.println("LMBDWHFileCreation" + " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                System.out.println("LMBDWHFileCreation" + " Logconfig File Not Found .............");
                return;
            }
            // use to load the constant.props and processLogConfig files
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        }// end of try
        catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("main", " Error in Loading Files ...........................: " + e.getMessage());
            }
            _logger.errorTrace("main", e);
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {
            process();
        } catch (BTSLBaseException be) {
            _logger.error("main", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace("main", be);
        } finally {
            ConfigServlet.destroyProcessCache();
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug("main", "Exiting..... ");
        }
    }

    /**
     * This method is the main method of this process, which is responsible for
     * the SOS DWH files creation.
     * 
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

        final String METHOD_NAME = "process";
        try {
            _logger.debug(METHOD_NAME, "Memory at statup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);
            // getting all the required parameters from Constants.props
            loadConstantParameters();

            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LMBDWHFileCreation[process]", "",
                    "", "", "DATABASE Connection is NULL");
                return;
            }
            // getting process id
            processId = ProcessI.SOSDWH_PROCESSID;
            // method call to check status of the process
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(con, processId);
            statusOk = _processStatusVO.isStatusOkBool();
            beforeInterval =  BTSLUtil.parseLongToInt(  _processStatusVO.getBeforeInterval() / (60 * 24));
            if (statusOk) {
                con.commit();
                // method call to find maximum date till which process has been
                // executed
                processedUpto = _processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    // ID SOSDWH001 to check whether process has been executed
                    // till current date or not
                    if (processedUpto.compareTo(currentDate) == 0) {
                        throw new BTSLBaseException("LMBDWHFileCreation", METHOD_NAME, PretupsErrorCodesI.SOSDWH_PROCESS_ALREADY_EXECUTED_TILL_TODAY);
                    }
                    // adding 1 in processed upto date as we have to start from
                    // the next day till which process has been executed
                    processedUpto = BTSLUtil.addDaysInUtilDate(processedUpto, 1);
                    // loop to be started for each date
                    // the loop starts from the date till which process has been
                    // executed plus one day and executes one day before current
                    // date
                    for (dateCount = BTSLUtil.getSQLDateFromUtilDate(processedUpto); dateCount.before(BTSLUtil.addDaysInUtilDate(currentDate, -beforeInterval)); dateCount = BTSLUtil
                        .addDaysInUtilDate(dateCount, 1)) {
                        if (!checkUnderprocessTransaction(con, dateCount)) {
                            // method call to create master directory and child
                            // directory if does not exist
                            _childDirectory = createDirectory(_masterDirectoryPathAndName, processId, dateCount);
                            // method call to fetch the SOS transactions data
                            // and write it in files
                            fetchSOSTransactionData(con, dateCount, _childDirectory, _dwhFileNameForTransaction, _dwhFileLabelForTransaction, _fileEXT, _maxFileLength);
                            // method call to update maximum date till which
                            // process has been executed
                            _processStatusVO.setExecutedUpto(dateCount);
                            _processStatusVO.setExecutedOn(currentDate);
                            processStatusDAO = new ProcessStatusDAO();
                            maxDoneDateUpdateCount = processStatusDAO.updateProcessDetail(con, _processStatusVO);

                            // if the process is successful, transaction is
                            // commit, else rollback
                            if (maxDoneDateUpdateCount > 0) {
                                // use to move all the created files into the
                                // final directory
                                moveFilesToFinalDirectory(_masterDirectoryPathAndName, _finalMasterDirectoryPath, processId, dateCount);
                                con.commit();
                            } else {
                                if (_fileNameLst.size() > 0) {
                                    deleteAllFiles();
                                }
                                con.rollback();
                                throw new BTSLBaseException("LMBDWHFileCreation", METHOD_NAME, PretupsErrorCodesI.SOSDWH_COULD_NOT_UPDATE_MAX_DONE_DATE);
                            }
                            // SOSDWH001 sleep has been added after processing
                            // records of one day
                            Thread.sleep(500);
                        }// end if
                    }// end loop
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LMBDWHFileCreation[process]", "", "", "",
                        " LMBDWHFileCreation process has been executed successfully.");
                }// end of if (processedUpto!=null)
                 // ID DWH002 to avoid the null pointer exception thrown, in
                 // case
                 // processesUpto is null
                else {
                    throw new BTSLBaseException("LMBDWHFileCreation", METHOD_NAME, PretupsErrorCodesI.SOSDWH_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
                }
            }// end of if (statusOk)
        }// end of try
        catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            try {
                if (_fileNameLst.size() > 0) {
                    deleteAllFiles();
                }
            } catch (Exception e1) {
                _logger.errorTrace(METHOD_NAME, e1);
            }
            _logger.error(METHOD_NAME, "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LMBDWHFileCreation[process]", "", "", "",
                " LMBDWHFileCreation process could not be executed successfully.");
            throw new BTSLBaseException("LMBDWHFileCreation", METHOD_NAME, PretupsErrorCodesI.SOSDWH_ERROR_EXCEPTION);
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
                        try {
                            if (_fileNameLst.size() > 0) {
                                deleteAllFiles();
                            }
                        } catch (Exception e1) {
                            _logger.errorTrace(METHOD_NAME, e1);
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
            _logger.debug(METHOD_NAME, "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... ");
            }
        }
    }

    /**
     * This method is used to load all the required parameters defined in the
     * constant.props file which are used to creating the DWH files
     * 
     * @throws BTSLBaseException
     */
    private static void loadConstantParameters() throws BTSLBaseException {
        final String METHOD_NAME = "loadConstantParameters";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entered: ");
        }
        try {
            _dwhFileLabelForTransaction = Constants.getProperty("LMB_DWH_TRANSACTION_FILE_LABEL");
            if (BTSLUtil.isNullString(_dwhFileLabelForTransaction)) {
                _logger.error(METHOD_NAME, " Could not find file label for transaction data in the Constants file.");
            } else {
                _logger.debug(METHOD_NAME, " _dwhFileLabelForTransaction=" + _dwhFileLabelForTransaction);
            }

            _dwhFileNameForTransaction = Constants.getProperty("LMB_DWH_TRANSACTION_FILE_NAME");
            if (BTSLUtil.isNullString(_dwhFileNameForTransaction)) {
                _logger.error(METHOD_NAME, " Could not find file name for transaction data in the Constants file.");
            } else {
                _logger.debug(METHOD_NAME, " _dwhFileNameForTransaction=" + _dwhFileNameForTransaction);
            }

            _masterDirectoryPathAndName = Constants.getProperty("LMB_DWH_MASTER_DIRECTORY");
            if (BTSLUtil.isNullString(_masterDirectoryPathAndName)) {
                _logger.error(METHOD_NAME, " Could not find directory path in the Constants file.");
            } else {
                _logger.debug(METHOD_NAME, " _masterDirectoryPathAndName=" + _masterDirectoryPathAndName);
            }
            _finalMasterDirectoryPath = Constants.getProperty("LMB_DWH_FINAL_DIRECTORY");

            if (BTSLUtil.isNullString(_finalMasterDirectoryPath)) {
                _logger.error(METHOD_NAME, " Could not find final directory path in the Constants file.");
            } else {
                _logger.debug(METHOD_NAME, " finalMasterDirectoryPath=" + _finalMasterDirectoryPath);
            }

            // checking that none of the required parameters should be null
            if (BTSLUtil.isNullString(_dwhFileLabelForTransaction) || BTSLUtil.isNullString(_dwhFileNameForTransaction) || BTSLUtil.isNullString(_masterDirectoryPathAndName) || BTSLUtil
                .isNullString(_finalMasterDirectoryPath)) {
                throw new BTSLBaseException("LMBDWHFileCreation", METHOD_NAME, PretupsErrorCodesI.SOSDWH_COULD_NOT_FIND_DATA_IN_CONSTANTS_FILE);
            }
            try {
                _fileEXT = Constants.getProperty("LMB_DWH_FILE_EXT");
            } catch (Exception e) {
                _fileEXT = ".csv";
                _logger.errorTrace(METHOD_NAME, e);
            }
            _logger.debug(METHOD_NAME, " _fileEXT=" + _fileEXT);
            try {
                _maxFileLength = Long.parseLong(Constants.getProperty("LMB_DWH_MAX_FILE_LENGTH"));
            } catch (Exception e) {
                _maxFileLength = 1000;
                _logger.errorTrace(METHOD_NAME, e);
            }
            _logger.debug(METHOD_NAME, " _maxFileLength=" + _maxFileLength);
            _logger.debug(METHOD_NAME, " Required information successfuly loaded from Constants.props...............: ");
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LMBDWHFileCreation[loadConstantParameters]", "", "",
                "", "Message:" + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            final BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.SOSDWH_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LMBDWHFileCreation[loadConstantParameters]", "", "",
                "", "Message:" + btslMessage);
            throw new BTSLBaseException("LMBDWHFileCreation", METHOD_NAME, PretupsErrorCodesI.SOSDWH_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, " Exiting.. ");
            }
        }
    }

    /**
     * This method will check the existance of under process and/or ambiguous
     * transaction for the given date for the date for which method is called
     * 
     * @param p_con
     *            Connection
     * @param p_beingProcessedDate
     *            Date
     * @return boolean
     * @throws BTSLBaseException
     */
    private static boolean checkUnderprocessTransaction(Connection p_con, Date p_beingProcessedDate) throws BTSLBaseException {
        final String METHOD_NAME = "checkUnderprocessTransaction";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entered: p_beingProcessedDate=" + p_beingProcessedDate);
        }
        PreparedStatement selectPstmt = null;
        ResultSet selectRst = null;
        boolean transactionFound = false;
        String selectQuery = null;
        try {
            selectQuery = new String("SELECT 1 FROM SOS_TRANSACTION_DETAILS WHERE RECHARGE_DATE=? AND SOS_RECHARGE_STATUS IN('205','250') ");
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "select query:" + selectQuery);
            }
            selectPstmt = p_con.prepareStatement(selectQuery);
            selectPstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate));
            selectRst = selectPstmt.executeQuery();
            if (selectRst.next()) {
                transactionFound = true;
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LMBDWHFileCreation[checkUnderprocessTransaction]",
                    "", "", "", "Message:LMBDWHFileCreation process cannot continue as underprocess and/or ambiguous transactions are found.");
                throw new BTSLBaseException("LMBDWHFileCreation", METHOD_NAME, PretupsErrorCodesI.SOSDWH_AMB_OR_UP_TXN_FOUND);
            }
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (SQLException sqe) {
            _logger.error(METHOD_NAME, "SQLException " + sqe.getMessage());
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LMBDWHFileCreation[checkUnderprocessTransaction]", "",
                "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("LMBDWHFileCreation", METHOD_NAME, PretupsErrorCodesI.SOSDWH_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _logger.error(METHOD_NAME, "Exception : " + ex.getMessage());
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LMBDWHFileCreation[checkUnderprocessTransaction]", "",
                "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("LMBDWHFileCreation", METHOD_NAME, PretupsErrorCodesI.SOSDWH_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (selectRst != null) {
                try {
                    selectRst.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (selectPstmt != null) {
                try {
                    selectPstmt.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting transactionFound=" + transactionFound);
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
     * @throws Exception
     * @return String
     */
    private static String createDirectory(String p_directoryPathAndName, String p_processId, Date p_beingProcessedDate) throws BTSLBaseException {
        final String METHOD_NAME = "createDirectory";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME,
                " Entered: p_directoryPathAndName=" + p_directoryPathAndName + " p_processId=" + p_processId + " p_beingProcessedDate=" + p_beingProcessedDate);
        }

        File parentDir = null;
        File newDir = null;
        String dirName = null;
        try {
            parentDir = new File(p_directoryPathAndName);
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            p_beingProcessedDate = BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate);
            // child directory name includes a file name and being processed
            // date, month and year
            //dirName = p_directoryPathAndName + File.separator + p_processId + "_" + p_beingProcessedDate.toString().substring(8, 10) + p_beingProcessedDate.toString()
               // .substring(5, 7) + p_beingProcessedDate.toString().substring(2, 4);
            dirName = p_directoryPathAndName + File.separator + p_processId + "_" + BTSLUtil.getDateStrForName(p_beingProcessedDate);
            newDir = new File(dirName);
            if (!newDir.exists()) {
                newDir.mkdirs();
            }
        } catch (Exception e) {
            _logger.debug(METHOD_NAME, "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            if (parentDir != null) {
                parentDir = null;
            }
            if (newDir != null) {
                newDir = null;
            }
            throw new BTSLBaseException("LMBDWHFileCreation", METHOD_NAME, PretupsErrorCodesI.SOSDWH_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting dirName=" + dirName);
            }
        } // end of finally
        return dirName;
    }

    /**
     * This method will fetch all the required SOS transactions data from
     * database
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
     * @param p_maxFileLength
     *            long
     * @return void
     * @throws SQLException
     *             ,Exception
     */
    private static void fetchSOSTransactionData(Connection p_con, Date p_beingProcessedDate, String p_dirPath, String p_fileName, String p_fileLabel, String p_fileEXT, long p_maxFileLength) throws BTSLBaseException {
        final String METHOD_NAME = "fetchSOSTransactionData";
        if (_logger.isDebugEnabled()) {
            _logger
                .debug(
                    METHOD_NAME,
                    " Entered: p_beingProcessedDate=" + p_beingProcessedDate + " p_dirPath=" + p_dirPath + " p_fileName=" + p_fileName + " p_fileLabel=" + p_fileLabel + " p_fileEXT=" + p_fileEXT + " p_maxFileLength=" + p_maxFileLength);
        }

        final StringBuffer SOSQueryBuf = new StringBuffer();

        SOSQueryBuf
            .append(" SELECT STR.TRANSACTION_ID||',' ||STR.SUBSCRIBER_MSISDN||','||to_char(STR.RECHARGE_DATE,'dd-fmmon-yyyy')||','||to_char(STR.RECHARGE_DATE_TIME,'dd-fmmon-yyyy fmhh24:mi:ss AM')||','");
        SOSQueryBuf.append("||STR.SOS_RECHARGE_AMOUNT||','|| STR.SOS_CREDIT_AMOUNT||','|| STR.SOS_DEBIT_AMOUNT||','||SOS_RECHARGE_STATUS||','");
        SOSQueryBuf.append("||STR.ERROR_STATUS||','||STR.INTERFACE_RESPONSE_CODE||','||STR.NETWORK_CODE||','||STR.PRODUCT_CODE||','");
        SOSQueryBuf.append("||STR.REQUEST_GATEWAY_TYPE||','||STR.REQUEST_GATEWAY_CODE||','||STR.SERVICE_TYPE||','||STR.ACCOUNT_STATUS||','");
        SOSQueryBuf.append("||STR.SERVICE_CLASS_CODE||','||STR.CARD_GROUP_CODE||',' ||STR.TAX1_VALUE||','||STR.TAX2_VALUE||','");
        SOSQueryBuf
            .append("||STR.PROCESS_FEE_VALUE||','||STR.VALIDITY||','||STR.BONUS_VALUE||','||STR.GRACE_PERIOD||','|| STR.BONUS_VALIDITY ||',' ||STR.VALPERIOD_TYPE||','||STR.PREVIOUS_BALANCE||','||STR.POST_BALANCE||',' ");
        SOSQueryBuf
            .append("||STR.SETTLEMENT_STATUS||','||to_char(STR.SETTLEMENT_DATE,'dd-fmmon-yyyy')||','||STR.SETTLEMENT_FLAG||','||STR.SETTLEMENT_RECON_FLAG||','||to_char(STR.SETTLEMENT_RECON_DATE,'dd-fmmon-yyyy')||','||STR.RECONCILIATION_FLAG||','||to_char(STR.RECONCILIATION_DATE,'dd-fmmon-yyyy')||','");
        SOSQueryBuf
            .append("||STR.LMB_AMTAT_IN||','||STR.LMB_DEBIT_UPDATE_STATUS||','||STR.LMB_CREDIT_UPDATE_STATUS||','||STR.SETTLEMENT_ERROR_CODE||','||STR.SETTLEMENT_PREVIOUS_BALANCE||','||STR.CELL_ID||','||STR.SWITCH_ID||','");
        SOSQueryBuf.append("||REPLACE(KV.value,',',' ')||','");
        SOSQueryBuf.append(" FROM SOS_TRANSACTION_DETAILS STR, key_values KV ");
        SOSQueryBuf.append(" WHERE STR.RECHARGE_DATE = ? AND KV.KEY(+) = STR.SOS_RECHARGE_STATUS AND KV.TYPE(+) = ?");

        final String SOSSelectQuery = SOSQueryBuf.toString();
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "SOS select query:" + SOSSelectQuery);
        }
        PreparedStatement SOSSelectPstmt = null;
        ResultSet SOSSelectRst = null;

        try {
            SOSSelectPstmt = p_con.prepareStatement(SOSSelectQuery);
            SOSSelectPstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate));
            SOSSelectPstmt.setString(2, PretupsI.KEY_VALUE_P2P_STATUS);
            SOSSelectRst = SOSSelectPstmt.executeQuery();
            _logger.debug(METHOD_NAME, "Memory after loading transaction data: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime()
                .freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);

            // method call to write data in the files
            writeDataInFile(p_dirPath, p_fileName, p_fileLabel, p_beingProcessedDate, p_fileEXT, p_maxFileLength, SOSSelectRst);
            _logger.debug(METHOD_NAME, "Memory after writing transaction files: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime()
                .freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (SQLException sqe) {
            _logger.error(METHOD_NAME, "SQLException " + sqe.getMessage());
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LMBDWHFileCreation[fetchSOSTransactionData]", "", "",
                "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("LMBDWHFileCreation", METHOD_NAME, PretupsErrorCodesI.SOSDWH_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _logger.error(METHOD_NAME, "Exception : " + ex.getMessage());
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LMBDWHFileCreation[fetchSOSTransactionData]", "", "",
                "", "SQLException:" + ex.getMessage());
            throw new BTSLBaseException("LMBDWHFileCreation", METHOD_NAME, PretupsErrorCodesI.SOSDWH_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (SOSSelectRst != null) {
                try {
                    SOSSelectRst.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (SOSSelectPstmt != null) {
                try {
                    SOSSelectPstmt.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }

            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting ");
            }
        }// end of finally
    }

    /**
     * This method is used to write the fetched data into the file(s)
     * 
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
     * @param p_rst
     *            ResultSet
     * @return void
     * @throws BTSLBaseException
     */
    private static void writeDataInFile(String p_dirPath, String p_fileName, String p_fileLabel, Date p_beingProcessedDate, String p_fileEXT, long p_maxFileLength, ResultSet p_rst) throws BTSLBaseException {
        final String METHOD_NAME = "writeDataInFile";
        if (_logger.isDebugEnabled()) {
            _logger
                .debug(
                    METHOD_NAME,
                    " Entered:  p_dirPath=" + p_dirPath + " p_fileName=" + p_fileName + " p_fileLabel=" + p_fileLabel + " p_beingProcessedDate=" + p_beingProcessedDate + " p_fileEXT=" + p_fileEXT + " p_maxFileLength=" + p_maxFileLength + "p_rst=" + p_rst);
        }
        long recordsWrittenInFile = 0;
        PrintWriter out = null;
        int fileNumber = 0;
        String fileName = null;
        File newFile = null;
        String fileData = null;
        String fileHeader = null;
        String fileFooter = null;

        try {
            // generating file name
            fileNumber = 1;
            // if the length of file number is 1, two zeros are added as prefix
            if (Integer.toString(fileNumber).length() == 1) {
                fileName = p_dirPath + File.separator + p_fileName + "00" + fileNumber + p_fileEXT;
            } else if (Integer.toString(fileNumber).length() == 2) {
                fileName = p_dirPath + File.separator + p_fileName + "0" + fileNumber + p_fileEXT;
            } else if (Integer.toString(fileNumber).length() == 3) {
                fileName = p_dirPath + File.separator + p_fileName + fileNumber + p_fileEXT;
            }

            _logger.debug(METHOD_NAME, "  fileName=" + fileName);

            newFile = new File(fileName);
            _fileNameLst.add(fileName);
            out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
            // ID DWH002 to make addition of header and footer optional on the
            // basis of entry in Constants.props
            if ("Y".equalsIgnoreCase(Constants.getProperty("LMB_DWH_ADD_HEADER_FOOTER"))) {
                fileHeader = constructFileHeader(p_beingProcessedDate, fileNumber, p_fileLabel);
                out.write(fileHeader);
            }
            // traverse first resultset
            while (p_rst.next()) {
                fileData = p_rst.getString(1);
                out.write(fileData + "\n");
                recordsWrittenInFile++;
                if (recordsWrittenInFile >= p_maxFileLength) {
                    // ID DWH002 to make addition of header and footer optional
                    // on the basis of entry in Constants.props
                    if ("Y".equalsIgnoreCase(Constants.getProperty("LMB_DWH_ADD_HEADER_FOOTER"))) {
                        fileFooter = constructFileFooter(recordsWrittenInFile);
                        out.write(fileFooter);
                    }
                    recordsWrittenInFile = 0;
                    fileNumber = fileNumber + 1;
                    //out.close();

                    // if the length of file number is 1, two zeros are added as
                    // prefix
                    if (Integer.toString(fileNumber).length() == 1) {
                        fileName = p_dirPath + File.separator + p_fileName + "00" + fileNumber + p_fileEXT;
                    } else if (Integer.toString(fileNumber).length() == 2) {
                        fileName = p_dirPath + File.separator + p_fileName + "0" + fileNumber + p_fileEXT;
                    } else if (Integer.toString(fileNumber).length() == 3) {
                        fileName = p_dirPath + File.separator + p_fileName + fileNumber + p_fileEXT;
                    }

                    _logger.debug(METHOD_NAME, "  fileName=" + fileName);
                    newFile = new File(fileName);
                    //out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
                    BTSLUtil.closeOpenStream(out, newFile);
                    _fileNameLst.add(fileName);
                    // ID DWH002 to make addition of header and footer optional
                    // on the basis of entry in Constants.props
                    if ("Y".equalsIgnoreCase(Constants.getProperty("LMB_DWH_ADD_HEADER_FOOTER"))) {
                        fileHeader = constructFileHeader(p_beingProcessedDate, fileNumber, p_fileLabel);
                        out.write(fileHeader);
                    }
                }// end of if(recordsWrittenInFile>=p_maxFileLength)
            }// end of while(p_rst.next())
             // if number of records are not zero then footer is appended as
             // file
             // is deleted
            if (recordsWrittenInFile > 0) {
                // ID DWH002 to make addition of header and footer optional on
                // the basis of entry in Constants.props
                if ("Y".equalsIgnoreCase(Constants.getProperty("LMB_DWH_ADD_HEADER_FOOTER"))) {
                    fileFooter = constructFileFooter(recordsWrittenInFile);
                    out.write(fileFooter);
                }
            } else {
                if (out != null) {
                    out.close();
                }
                boolean isDeleted = newFile.delete();
                if(isDeleted){
                	_logger.debug(METHOD_NAME, "File deleted successfully");
                }
                _fileNameLst.remove(_fileNameLst.size() - 1);
            }
            if (out != null) {
                out.close();
            }
        } catch (BTSLBaseException e) {
            deleteAllFiles();
            _logger.debug(METHOD_NAME, "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            if (newFile != null) {
                newFile = null;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LMBDWHFileCreation[writeDataInFile]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("LMBDWHFileCreation", METHOD_NAME, PretupsErrorCodesI.SOSDWH_ERROR_EXCEPTION);
        } catch (SQLException e) {
            deleteAllFiles();
            _logger.debug(METHOD_NAME, "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            if (newFile != null) {
                newFile = null;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LMBDWHFileCreation[writeDataInFile]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("LMBDWHFileCreation", METHOD_NAME, PretupsErrorCodesI.SOSDWH_ERROR_EXCEPTION);
        } catch (Exception e) {
            deleteAllFiles();
            _logger.debug(METHOD_NAME, "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            if (newFile != null) {
                newFile = null;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LMBDWHFileCreation[writeDataInFile]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("LMBDWHFileCreation", METHOD_NAME, PretupsErrorCodesI.SOSDWH_ERROR_EXCEPTION);
        } finally {
        	try{
        		if (out != null) {
                    out.close();
                }
        	}catch(Exception e){
        		 _logger.errorTrace(METHOD_NAME, e);
        	}
            
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting ");
            }
        }
    }

    /**
     * This method is used to change the status as Complete(C) in the
     * process_status table
     * 
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LMBDWHFileCreation[markProcessStatusAsComplete]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("LMBDWHFileCreation", METHOD_NAME, PretupsErrorCodesI.SOSDWH_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }

    /**
     * This method will delete all the files if some error is encountered after
     * file creation and files need to be deleted.
     * 
     * @throws BTSLBaseException
     * @return void
     */
    private static void deleteAllFiles() throws BTSLBaseException {
        final String METHOD_NAME = "deleteAllFiles";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entered: ");
        }
        int size = 0;
        if (_fileNameLst != null) {
            size = _fileNameLst.size();
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " : Number of files to be deleted " + size);
        }
        String fileName = null;
        File newFile = null;
        for (int i = 0; i < size; i++) {
            try {
                fileName = (String) _fileNameLst.get(i);
                newFile = new File(fileName);
                boolean isDeleted = newFile.delete();
                if(isDeleted){
                	_logger.debug(METHOD_NAME, "File deleted successfully");
                }
                if (_logger.isDebugEnabled()) {
                    _logger.debug("", fileName + " file deleted");
                }
            } catch (Exception e) {
                _logger.error(METHOD_NAME, "Exception " + e.getMessage());
                _logger.errorTrace(METHOD_NAME, e);
                if (newFile != null) {
                    newFile = null;
                }
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LMBDWHFileCreation[deleteAllFiles]", "", "", "",
                    "Exception:" + e.getMessage());
                throw new BTSLBaseException("LMBDWHFileCreation", METHOD_NAME, PretupsErrorCodesI.SOSDWH_ERROR_EXCEPTION);
            }
        }// end of for loop
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LMBDWHFileCreation[deleteAllFiles]", "", "", "",
            " Message: LMBDWHFileCreation process has found some error, so deleting all the files.");
        if (_fileNameLst != null && _fileNameLst.isEmpty()) {
            _fileNameLst.clear();
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " : Exiting.............................");
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
    private static String constructFileHeader(Date p_beingProcessedDate, long p_fileNumber, String p_fileLabel) throws BTSLBaseException {
        final String METHOD_NAME = "constructFileHeader";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entered: ");
        }
        StringBuffer fileHeaderBuf = null;
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
            fileHeaderBuf = new StringBuffer("");
            fileHeaderBuf.append("\n" + " Present Date=" + BTSLDateUtil.getSystemLocaleDate(sdf.format(new Date())));
            fileHeaderBuf.append("\n" + " For Date=" + BTSLDateUtil.getSystemLocaleDate(sdf.format(p_beingProcessedDate)));
            fileHeaderBuf.append("\n" + " File Number=" + p_fileNumber);
            fileHeaderBuf.append("\n" + p_fileLabel);
            fileHeaderBuf.append("\n" + "[STARTDATA]" + "\n");
        } catch (Exception e) {
            _logger.debug(METHOD_NAME, "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("LMBDWHFileCreation", METHOD_NAME, PretupsErrorCodesI.SOSDWH_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting: fileHeaderBuf.toString()=" + fileHeaderBuf.toString());
            }
        } // end of finally
        return fileHeaderBuf.toString();
    }

    /**
     * This method is used to constuct file footer
     * 
     * @param p_noOfRecords
     *            long
     * @return String
     */
    private static String constructFileFooter(long p_noOfRecords) throws BTSLBaseException {
        final String METHOD_NAME = "constructFileFooter";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entered: ");
        }
        StringBuffer fileFooterBuf = null;
        try {
            fileFooterBuf = new StringBuffer("");
            fileFooterBuf.append("[ENDDATA]" + "\n");
            fileFooterBuf.append(" Number of records=" + p_noOfRecords);
        } catch (Exception e) {
            _logger.debug("constructFileHeader", "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("LMBDWHFileCreation", METHOD_NAME, PretupsErrorCodesI.SOSDWH_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting: fileHeaderBuf.toString()=" + fileFooterBuf.toString());
            }
        } // end of finally
        return fileFooterBuf.toString();
    }

    /**
     * This method will copy all the created files to another location. the
     * process will generate files in a particular directroy. if the process
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
     * @return void
     */
    private static void moveFilesToFinalDirectory(String p_oldDirectoryPath, String p_finalDirectoryPath, String p_processId, Date p_beingProcessedDate) throws BTSLBaseException {
        final String METHOD_NAME = "moveFilesToFinalDirectory";
        if (_logger.isDebugEnabled()) {
            _logger
                .debug(
                    METHOD_NAME,
                    " Entered: p_oldDirectoryPath=" + p_oldDirectoryPath + " p_finalDirectoryPath=" + p_finalDirectoryPath + " p_processId=" + p_processId + " p_beingProcessedDate=" + p_beingProcessedDate);
        }

        String oldFileName = null;
        String newFileName = null;
        File oldFile = null;
        File newFile = null;
        File parentDir = new File(p_finalDirectoryPath);
        if (!parentDir.exists()) {
            parentDir.mkdir();
        }
        p_beingProcessedDate = BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate);
        // child directory name includes a file name and being processed date,
        // month and year
       // final String oldDirName = p_oldDirectoryPath + File.separator + p_processId + "_" + p_beingProcessedDate.toString().substring(8, 10) + p_beingProcessedDate.toString()
            //.substring(5, 7) + p_beingProcessedDate.toString().substring(2, 4);
        final String oldDirName = p_oldDirectoryPath + File.separator + p_processId + "_" + BTSLUtil.getDateStrForName(p_beingProcessedDate);
        //final String newDirName = p_finalDirectoryPath + File.separator + p_processId + "_" + p_beingProcessedDate.toString().substring(8, 10) + p_beingProcessedDate
           // .toString().substring(5, 7) + p_beingProcessedDate.toString().substring(2, 4);
       
        final String newDirName = p_finalDirectoryPath + File.separator + p_processId + "_" + BTSLUtil.getDateStrForName(p_beingProcessedDate);
        
        File oldDir = new File(oldDirName);
        File newDir = new File(newDirName);
        if (!newDir.exists()) {
            newDir.mkdir();
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " dirName=" + newDirName);
        }

        final int size = _fileNameLst.size();
        try {
            for (int i = 0; i < size; i++) {
                oldFileName = (String) _fileNameLst.get(i);
                oldFile = new File(oldFileName);
                newFileName = oldFileName.replace(p_oldDirectoryPath, p_finalDirectoryPath);
                newFile = new File(newFileName);
                if(oldFile.renameTo(newFile))
                {
                	_logger.debug(METHOD_NAME, "File renamed successfully");
                }
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, " File " + oldFileName + " is moved to " + newFileName);
                }
            }// end of for loop
            _fileNameLst.clear();
            if (oldDir.exists()) {
            	boolean isDeleted = oldDir.delete();
                if(isDeleted){
                	_logger.debug(METHOD_NAME, "Directory deleted successfully");
                }
            }
            _logger.debug(METHOD_NAME, " File " + oldFileName + " is moved to " + newFileName);
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LMBDWHFileCreation[moveFilesToFinalDirectory]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("LMBDWHFileCreation", METHOD_NAME, PretupsErrorCodesI.SOSDWH_ERROR_EXCEPTION);
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
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting.. ");
            }
        } // end of finally
    }
}

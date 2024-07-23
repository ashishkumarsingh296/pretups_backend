package com.btsl.pretups.processes.clientprocesses;

/**
 * @(#)VFEDWHFileCreation
 *                        Copyright(c) 2006, Bharti Telesoft Ltd.
 *                        All Rights Reserved
 * 
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Ankit Singhal 8/05/2006 Initial Creation
 *                        Sandeep Goel Aug 04, 2006 Modification ID DWH001
 *                        Ankit Singhal Sep 09, 2006 Modification ID DWH002
 *                        Sanjeev Nov 29,2007 Modification ID DWH003
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

public class VFEDWHFileCreation {
    private static String _dwhFileLabelForTransaction = null;
    private static String _dwhFileLabelForMaster = null;
    private static String _dwhFileNameForTransaction = null;
    private static String _dwhFileNameForMaster = null;
    private static String _masterDirectoryPathAndName = null;
    private static String _finalMasterDirectoryPath = null;
    private static String _childDirectory = null;
    private static String _fileEXT = null;
    private static long _maxFileLength = 0;
    private static ArrayList _fileNameLst = new ArrayList();
    private static ProcessStatusVO _processStatusVO;
    private static ProcessBL _processBL = null;
    // Option for generating DWH if there is Ambiguous or Under Process Txn.
    private static boolean _ambiUnderTxnFound = true;
    private static String bonusBundleDefaultValues = null;

    private static Log _log = LogFactory.getLog(VFEDWHFileCreation.class.getName());

    /**
     * to ensure no class instantiation 
     */
    private VFEDWHFileCreation(){
    	
    }
    public static void main(String arg[]) {
        final String METHOD_NAME = "main";
        try {
            // ID DWH003 third argument is to generate DWH even there is
            // Ambiguous or Under Process Txn.
            if (arg.length != 3) {
                if (arg.length != 2) {
                    System.out.println("Usage : VFEDWHFileCreation [Constants file] [LogConfig file]");
                    return;
                }
            }
            final File constantsFile = Constants.validateFilePath(arg[0]);
            if (!constantsFile.exists()) {
                System.out.println("VFEDWHFileCreation" + " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = Constants.validateFilePath(arg[1]);
            if (!logconfigFile.exists()) {
                System.out.println("VFEDWHFileCreation" + " Logconfig File Not Found .............");
                return;
            }

            // if third argument is not set then try to set it with Default
            // value true;
            try {
                if (PretupsI.YES.equalsIgnoreCase(arg[2])) {
                    _ambiUnderTxnFound = false;
                }
            } catch (Exception e) {
                _ambiUnderTxnFound = true;
                _log.errorTrace(METHOD_NAME, e);
            }

            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            bonusBundleDefaultValues = Constants.getProperty("BONUS_BUNDLE_DEFAULT_VAL");
            if (BTSLUtil.isNullString(bonusBundleDefaultValues)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VFEDWHFileCreation[main]", "", "", "",
                    "BONUS_BUNDLE_DEFAULT_VAL is not defined in Constants.props");
                System.out.println("VFEDWHFileCreation" + " BONUS_BUNDLE_DEFAULT_VAL is not defined in Constants.props.............");
                return;
            }

        }// end of try
        catch (Exception e) {
            if (_log.isDebugEnabled()) {
                _log.debug("main", " Error in Loading Files ...........................: " + e.getMessage());
            }
            _log.errorTrace(METHOD_NAME, e);
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {
            process();
        } catch (BTSLBaseException be) {
            _log.error("main", "BTSLBaseException : " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("main", "Exiting..... ");
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    private static void process() throws BTSLBaseException {
        final String METHOD_NAME = "process";
        Date processedUpto = null;
        Date dateCount = null;
        Date currentDate = new Date();
        Connection con = null;
        String processId = null;
        boolean statusOk = false;
        ProcessStatusDAO processStatusDAO = null;
        int beforeInterval = 0;

        int maxDoneDateUpdateCount = 0;

        try {
            _log.debug("process", "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);
            // getting all the required parameters from Constants.props
            loadConstantParameters();

            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_log.isDebugEnabled()) {
                    _log.debug("process", " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VFEDWHFileCreation[process]", "",
                    "", "", "DATABASE Connection is NULL");
                return;
            }
            // getting process id
            processId = ProcessI.DWH_PROCESSID;
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
                    // ID DWH002 to check whether process has been executed till
                    // current date or not
                    if (processedUpto.compareTo(currentDate) == 0) {
                        throw new BTSLBaseException("VFEDWHFileCreation", "process", PretupsErrorCodesI.DWH_PROCESS_ALREADY_EXECUTED_TILL_TODAY);
                    }
                    // adding 1 in processed upto dtae as we have to start from
                    // the next day till which process has been executed
                    processedUpto = BTSLUtil.addDaysInUtilDate(processedUpto, 1);
                    // loop to be started for each date
                    // the loop starts from the date till which process has been
                    // executed and executes one day before current date
                    for (dateCount = BTSLUtil.getSQLDateFromUtilDate(processedUpto); dateCount.before(BTSLUtil.addDaysInUtilDate(currentDate, -beforeInterval)); dateCount = BTSLUtil
                        .addDaysInUtilDate(dateCount, 1)) {
                        if (_ambiUnderTxnFound) {
                            _ambiUnderTxnFound = checkUnderprocessTransaction(con, dateCount);
                        }
                        if (!_ambiUnderTxnFound) {
                            // method call to create master directory and child
                            // directory if does not exist
                            _childDirectory = createDirectory(_masterDirectoryPathAndName, processId, dateCount);
                            // method call to fetch transaction data and write
                            // it in files
                            fetchChannelTransactionData(con, dateCount, _childDirectory, _dwhFileNameForTransaction, _dwhFileLabelForTransaction, _fileEXT, _maxFileLength);
                            // method call to fetch master data and write it in
                            // files
                            fetchMasterData(con, dateCount, _childDirectory, _dwhFileNameForMaster, _dwhFileLabelForMaster, _fileEXT, _maxFileLength);
                            // method call to update maximum date till which
                            // process has been executed
                            _processStatusVO.setExecutedUpto(dateCount);
                            _processStatusVO.setExecutedOn(currentDate);
                            processStatusDAO = new ProcessStatusDAO();
                            maxDoneDateUpdateCount = processStatusDAO.updateProcessDetail(con, _processStatusVO);

                            // if the process is successful, transaction is
                            // commit, else rollback
                            if (maxDoneDateUpdateCount > 0) {
                                moveFilesToFinalDirectory(_masterDirectoryPathAndName, _finalMasterDirectoryPath, processId, dateCount);
                                con.commit();
                            } else {
                                deleteAllFiles();
                                con.rollback();
                                throw new BTSLBaseException("VFEDWHFileCreation", "process", PretupsErrorCodesI.DWH_COULD_NOT_UPDATE_MAX_DONE_DATE);
                            }
                            // DWH002 sleep has been added after processing
                            // records of one day
                            Thread.sleep(500);
                        }// end if
                    }// end loop
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VFEDWHFileCreation[process]", "", "", "",
                        " VFEDWHFileCreation process has been executed successfully.");
                }
                // ID DWH002 to avoid the null pointer exception thrown, in case
                // processesUpto is null
                else {
                    throw new BTSLBaseException("VFEDWHFileCreation", "process", PretupsErrorCodesI.DWH_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
                }
            }
        }// end of try
        catch (BTSLBaseException be) {
            _log.error("process", "BTSLBaseException : " + be.getMessage());
            if (_fileNameLst.size() > 0) {
                deleteAllFiles();
            }
            try {
                con.rollback();
            } catch (SQLException se) {
                _log.errorTrace(METHOD_NAME, se);
            }

            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            if (_fileNameLst.size() > 0) {
                deleteAllFiles();
            }
            _log.error("process", "Exception : " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VFEDWHFileCreation[process]", "", "", "",
                " VFEDWHFileCreation process could not be executed successfully.");
            throw new BTSLBaseException("VFEDWHFileCreation", "process", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            // if the status was marked as under process by this method call,
            // only then it is marked as complete on termination
            if (statusOk) {
                try {
                    if (markProcessStatusAsComplete(con, processId) == 1) {
                        try {
                            con.commit();
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                    } else {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                        }
                    }
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (Exception ex) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("process", "Exception closing connection ");
                    }
                    _log.errorTrace(METHOD_NAME, ex);
                }
            }
            _log.debug("process", "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            if (_log.isDebugEnabled()) {
                _log.debug("process", "Exiting..... ");
            }
        }
    }

    private static void loadConstantParameters() throws BTSLBaseException {
        final String METHOD_NAME = "loadConstantParameters";
        if (_log.isDebugEnabled()) {
            _log.debug("loadParameters", " Entered: ");
        }
        try {
            _dwhFileLabelForTransaction = Constants.getProperty("DWH_TRANSACTION_FILE_LABEL");
            if (BTSLUtil.isNullString(_dwhFileLabelForTransaction)) {
                _log.error("loadConstantParameters", " Could not find file label for transaction data in the Constants file.");
            } else {
                _log.debug("main", " _dwhFileLabelForTransaction=" + _dwhFileLabelForTransaction);
            }
            _dwhFileLabelForMaster = Constants.getProperty("DWH_MASTER_FILE_LABEL");
            if (BTSLUtil.isNullString(_dwhFileLabelForMaster)) {
                _log.error("loadConstantParameters", " Could not find file label for master data in the Constants file.");
            } else {
                _log.debug("loadConstantParameters", " _dwhFileLabelForMaster=" + _dwhFileLabelForMaster);
            }
            _dwhFileNameForTransaction = Constants.getProperty("DWH_TRANSACTION_FILE_NAME");
            if (BTSLUtil.isNullString(_dwhFileNameForTransaction)) {
                _log.error("loadConstantParameters", " Could not find file name for transaction data in the Constants file.");
            } else {
                _log.debug("loadConstantParameters", " _dwhFileNameForTransaction=" + _dwhFileNameForTransaction);
            }
            _dwhFileNameForMaster = Constants.getProperty("DWH_MASTER_FILE_NAME");
            if (BTSLUtil.isNullString(_dwhFileNameForMaster)) {
                _log.error("loadConstantParameters", " Could not find file name for master data in the Constants file.");
            } else {
                _log.debug("loadConstantParameters", " _dwhFileNameForMaster=" + _dwhFileNameForMaster);
            }

            _masterDirectoryPathAndName = Constants.getProperty("DWH_MASTER_DIRECTORY");
            if (BTSLUtil.isNullString(_masterDirectoryPathAndName)) {
                _log.error("loadConstantParameters", " Could not find directory path in the Constants file.");
            } else {
                _log.debug("loadConstantParameters", " _masterDirectoryPathAndName=" + _masterDirectoryPathAndName);
            }
            _finalMasterDirectoryPath = Constants.getProperty("DWH_FINAL_DIRECTORY");
            if (BTSLUtil.isNullString(_finalMasterDirectoryPath)) {
                _log.error("loadConstantParameters", " Could not find final directory path in the Constants file.");
            } else {
                _log.debug("loadConstantParameters", " finalMasterDirectoryPath=" + _finalMasterDirectoryPath);
            }

            // checking that none of the required parameters should be null
            if (BTSLUtil.isNullString(_dwhFileLabelForTransaction) || BTSLUtil.isNullString(_dwhFileLabelForMaster) || BTSLUtil.isNullString(_dwhFileNameForTransaction) || BTSLUtil
                .isNullString(_dwhFileNameForMaster) || BTSLUtil.isNullString(_masterDirectoryPathAndName) || BTSLUtil.isNullString(_finalMasterDirectoryPath)) {
                throw new BTSLBaseException("VFEDWHFileCreation", "loadConstantParameters", PretupsErrorCodesI.DWH_COULD_NOT_FIND_DATA_IN_CONSTANTS_FILE);
            }
            try {
                _fileEXT = Constants.getProperty("DWH_FILE_EXT");
            } catch (Exception e) {
                _fileEXT = ".csv";
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.debug("loadConstantParameters", " _fileEXT=" + _fileEXT);
            try {
                _maxFileLength = Long.parseLong(Constants.getProperty("DWH_MAX_FILE_LENGTH"));
            } catch (Exception e) {
                _maxFileLength = 1000;
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.debug("loadConstantParameters", " _maxFileLength=" + _maxFileLength);
            _log.debug("loadConstantParameters", " Required information successfuly loaded from Constants.props...............: ");
        } catch (BTSLBaseException be) {
            _log.error("loadConstantParameters", "BTSLBaseException : " + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VFEDWHFileCreation[loadConstantParameters]", "", "",
                "", "Message:" + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _log.error("loadConstantParameters", "Exception : " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            final BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VFEDWHFileCreation[loadConstantParameters]", "", "",
                "", "Message:" + btslMessage);
            throw new BTSLBaseException("VFEDWHFileCreation", "loadConstantParameters", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
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
        final String METHOD_NAME = "checkUnderprocessTransaction";
        if (_log.isDebugEnabled()) {
            _log.debug("checkUnderprocessTransaction", " Entered: p_beingProcessedDate=" + p_beingProcessedDate);
        }
        PreparedStatement selectPstmt = null;
        ResultSet selectRst = null;
        boolean transactionFound = false;
        StringBuilder selectQuery;
        String selectQry =null;
        try {
        	selectQuery=new StringBuilder();
            // by sandeep goel ID DWH001 to make get the status from the file
            // not be hardcoded
        	selectQuery.append("SELECT 1 FROM c2s_transfers WHERE transfer_date=? AND ");
        	selectQuery.append(" transfer_status IN(?,?)");
        	selectQry=selectQuery.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("checkUnderprocessTransaction", "select query:" + selectQry);
            }
            selectPstmt = p_con.prepareStatement(selectQry);
            selectPstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate));
            selectPstmt.setString(2, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            selectPstmt.setString(3,PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            selectRst = selectPstmt.executeQuery();
            if (selectRst.next()) {
                transactionFound = true;
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VFEDWHFileCreation[checkUnderprocessTransaction]",
                    "", "", "", "Message: VFEDWHFileCreation process cannot continue as underprocess and/or ambiguous transactions are found.");
                throw new BTSLBaseException("VFEDWHFileCreation", "checkUnderprocessTransaction", PretupsErrorCodesI.DWH_AMB_OR_UP_TXN_FOUND);
            }
        } catch (BTSLBaseException be) {
            _log.error("checkUnderprocessTransaction", "BTSLBaseException : " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (SQLException sqe) {
            _log.error("checkUnderprocessTransaction", "SQLException " + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VFEDWHFileCreation[checkUnderprocessTransaction]", "",
                "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("VFEDWHFileCreation", "checkUnderprocessTransaction", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _log.error("checkUnderprocessTransaction", "Exception : " + ex.getMessage());
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VFEDWHFileCreation[checkUnderprocessTransaction]", "",
                "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("VFEDWHFileCreation", "checkUnderprocessTransaction", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (selectRst != null) {
                try {
                    selectRst.close();
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
            }
            if (selectPstmt != null) {
                try {
                    selectPstmt.close();
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug("checkUnderprocessTransaction", "Exiting transactionFound=" + transactionFound);
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
        final String METHOD_NAME = "createDirectory";
        if (_log.isDebugEnabled()) {
            _log.debug("createDirectory",
                " Entered: p_directoryPathAndName=" + p_directoryPathAndName + " p_processId=" + p_processId + " p_beingProcessedDate=" + p_beingProcessedDate);
        }
        String dirName = null;
        try {
            boolean success = false;
            final File parentDir = new File(p_directoryPathAndName);
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            p_beingProcessedDate = BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate);
            dirName = p_directoryPathAndName;
            final File newDir = new File(dirName);
            if (!newDir.exists()) {
                success = newDir.mkdirs();
            } else {
                success = true;
            }
            if (!success) {
                throw new BTSLBaseException("VFEDWHFileCreation", "createDirectory", PretupsErrorCodesI.COULD_NOT_CREATE_DIR);
            }
        } catch (BTSLBaseException be) {
            _log.error("createDirectory", "BTSLBaseException : " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception ex) {
            _log.error("createDirectory", "Exception : " + ex.getMessage());
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VFEDWHFileCreation[createDirectory]", "", "", "",
                "SQLException:" + ex.getMessage());
            throw new BTSLBaseException("VFEDWHFileCreation", "createDirectory", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug("createDirectory", "Exiting dirName=" + dirName);
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
     * @param p_maxFileLength
     *            long
     * @return void
     * @throws SQLException
     *             ,Exception
     */
    private static void fetchChannelTransactionData(Connection p_con, Date p_beingProcessedDate, String p_dirPath, String p_fileName, String p_fileLabel, String p_fileEXT, long p_maxFileLength) throws BTSLBaseException {
    	//local_index_implemented
        final String METHOD_NAME = "fetchChannelTransactionData";
        if (_log.isDebugEnabled()) {
            _log
                .debug(
                    "fetchChannelTransactionData",
                    " Entered: p_beingProcessedDate=" + p_beingProcessedDate + " p_dirPath=" + p_dirPath + " p_fileName=" + p_fileName + " p_fileLabel=" + p_fileLabel + " p_fileEXT=" + p_fileEXT + " p_maxFileLength=" + p_maxFileLength);
        }

        final StringBuffer c2sQueryBuf = new StringBuffer();
        c2sQueryBuf
            .append(" SELECT CT.transfer_id||','||request_gateway_type||','||to_char(CT.transfer_date,'dd/mm/yyyy')||','||to_char(CT.transfer_date_time,'dd/mm/yyyy hh12:mi:ss PM')||','||CT.network_code||','||CT.service_type||','||','||");
        // by sandeep goel ID DWH001 to make Sale into SALE since in the C2C
        // txns it is in the upper case
        c2sQueryBuf.append(" 'SALE'||','||'C2S'||','||CT.sender_id||','||','||CT.sender_msisdn||','||CT.receiver_msisdn||','||");
        c2sQueryBuf.append(" CT.sender_category||','||','||CT.sender_transfer_value||','||CT.receiver_transfer_value||','||");
        c2sQueryBuf.append(" CT.transfer_value||','||CT.quantity||','||','||','|| CT.receiver_access_fee||','||");
        c2sQueryBuf.append(" CT.receiver_tax1_value||','||CT.receiver_tax2_value||','||0||','||','||CT.differential_applicable||','||");
        c2sQueryBuf.append(" CT.differential_given||','||','||','||','||CT.product_code||','||CT.credit_back_status||','||CT.transfer_status");
        c2sQueryBuf
            .append(" ||','||CT.receiver_bonus_value||','||CT.receiver_validity||','||CT.receiver_bonus_validity||','||CTIR.service_class_code||','||CTIR.interface_id||','||CT.card_group_code");
        // Added By Narendra VFE^ CR
        c2sQueryBuf
            .append(" ||','||REPLACE(KV.value,',',' ')||','||CT.serial_number||','||CTIS.PREVIOUS_BALANCE||','||CTIS.POST_BALANCE||','||CTIR.PREVIOUS_BALANCE||','||CTIR.POST_BALANCE||','||','||CT.active_user_id||','||REPLACE(CT.reference_id,',',' ')||','||CT.ext_credit_intfce_type||','||CT.info1||','||CT.info2||','||CT.info3||','||CT.info4||','||CT.info5||','||CT.info6||','||CT.info7||','||CT.info8||','||CT.info9||','||CT.info10");

        c2sQueryBuf
            .append(",CT.transfer_status,CT.bonus_details  FROM c2s_transfers CT, c2s_transfer_items CTIS, c2s_transfer_items CTIR,key_values KV WHERE CT.transfer_date=? AND CTIR.transfer_date=? AND CT.transfer_id=CTIS.transfer_id AND CTIS.sno=1 AND CT.transfer_id=CTIR.transfer_id AND CTIR.sno=2");
        c2sQueryBuf.append(" AND KV.key(+)=CT.error_code AND KV.type(+)='C2S_ERR_CD' ORDER BY CT.transfer_date_time");
        final String c2sSelectQuery = c2sQueryBuf.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("fetchChannelTransactionData", "c2s select query:" + c2sSelectQuery);
        }
        PreparedStatement c2sSelectPstmt = null;
        ResultSet c2sSelectRst = null;

        final StringBuffer channelQueryBuf = new StringBuffer();
        channelQueryBuf
            .append(" SELECT CT.transfer_id||','||source||','||to_char(CT.transfer_date,'dd/mm/yyyy')||','||to_char(CTI.transfer_date,'dd/mm/yyyy hh12:mi:ss PM')||','||CT.network_code");
        channelQueryBuf.append(" ||','||CT.transfer_type||','||CT.transfer_sub_type||','||CT.transfer_category");
        // by sandeep goel ID DWH001 to get the sender msisdn and receiver
        // msisdn now both are available
        // in the channel transfer table so there is no need of the users table
        // also
        channelQueryBuf.append(" ||','||CT.type||','||CT.from_user_id||','||CT.to_user_id||','||CT.msisdn||','||CT.to_msisdn");
        channelQueryBuf.append(" ||','||CT.sender_category_code||','||CT.receiver_category_code");
        channelQueryBuf.append(" ||','||CTI.required_quantity||','||CTI.required_quantity||','||CTI.required_quantity");
        channelQueryBuf.append(" ||','||CTI.mrp||','||CTI.payable_amount||','||CTI.net_payable_amount||','||0");
        channelQueryBuf.append(" ||','||CTI.tax1_value||','||CTI.tax2_value||','||CTI.tax3_value||','||CTI.commission_value");
        channelQueryBuf
            .append(" ||','||','||','||CT.ext_txn_no||','||to_char(CT.ext_txn_date,'dd/mm/yyyy')||','||','||CTI.product_code||','||','|| DECODE(CT.status ,'CLOSE','" + PretupsErrorCodesI.TXN_STATUS_SUCCESS + "','" + PretupsErrorCodesI.TXN_STATUS_CANCEL + "') ||','||','||','||','||','||','||','||','");
        channelQueryBuf
            .append(" ||','||CTI.SENDER_PREVIOUS_STOCK||','||CTI.SENDER_POST_STOCK||','||CTI.RECEIVER_PREVIOUS_STOCK||','||CTI.RECEIVER_POST_STOCK||','||CT.TXN_WALLET||','||CT.active_user_id||','");
        channelQueryBuf.append(" FROM channel_transfers CT,channel_transfers_items CTI ");
        channelQueryBuf.append(" WHERE CT.transfer_id=CTI.transfer_id");
        channelQueryBuf.append(" AND CT.status IN('CLOSE','CNCL') AND CT.transfer_date=? ");
        channelQueryBuf.append(" ORDER BY CT.modified_on,CT.type");
        final String channelSelectQuery = channelQueryBuf.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("fetchChannelTransactionData", "channel select query:" + channelSelectQuery);
        }
        PreparedStatement channelSelectPstmt = null;
        ResultSet channelSelectRst = null;
        try {
            channelSelectPstmt = p_con.prepareStatement(channelSelectQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            channelSelectPstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate));
            channelSelectRst = channelSelectPstmt.executeQuery();
            _log
                .debug(
                    "fetchChannelTransactionData",
                    "Memory after loading channel transaction data: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);

            c2sSelectPstmt = p_con.prepareStatement(c2sSelectQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            c2sSelectPstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate));
            c2sSelectPstmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate));
            c2sSelectRst = c2sSelectPstmt.executeQuery();
            _log
                .debug("fetchChannelTransactionData", "Memory after loading C2S transaction data: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime
                    .getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);

            // method call to write data in the files
            writeDataInFile(p_dirPath, p_fileName, p_fileLabel, p_beingProcessedDate, p_fileEXT, p_maxFileLength, channelSelectRst, c2sSelectRst, "1");
            _log.debug("fetchChannelTransactionData", "Memory after writing transaction files: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime
                .getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
        } catch (BTSLBaseException be) {
            _log.error("fetchChannelTransactionData", "BTSLBaseException : " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (SQLException sqe) {
            _log.error("fetchChannelTransactionData", "SQLException " + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VFEDWHFileCreation[fetchChannelTransactionData]", "",
                "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("VFEDWHFileCreation", "fetchChannelTransactionData", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _log.error("fetchChannelTransactionData", "Exception : " + ex.getMessage());
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VFEDWHFileCreation[fetchChannelTransactionData]", "",
                "", "", "SQLException:" + ex.getMessage());
            throw new BTSLBaseException("VFEDWHFileCreation", "fetchChannelTransactionData", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (channelSelectRst != null) {
                try {
                    channelSelectRst.close();
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
            }
            if (channelSelectPstmt != null) {
                try {
                    channelSelectPstmt.close();
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
            }
            if (c2sSelectRst != null) {
                try {
                    c2sSelectRst.close();
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
            }
            if (c2sSelectPstmt != null) {
                try {
                    c2sSelectPstmt.close();
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
            }

            if (_log.isDebugEnabled()) {
                _log.debug("fetchChannelTransactionData", "Exiting ");
            }
        }// end of finally
    }

    /**
     * This method will fetch all the required data from USERS table
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
     * @throws sqlException
     *             ,Exception
     */
    private static void fetchMasterData(Connection p_con, Date p_beingProcessedDate, String p_dirPath, String p_fileName, String p_fileLabel, String p_fileEXT, long p_maxFileLength) throws BTSLBaseException {
        final String METHOD_NAME = "fetchMasterData";
        if (_log.isDebugEnabled()) {
            _log
                .debug(
                    "fetchMasterData",
                    " Entered:  p_beingProcessedDate=" + p_beingProcessedDate + " p_dirPath=" + p_dirPath + " p_fileName=" + p_fileName + " p_fileLabel=" + p_fileLabel + " p_fileEXT=" + p_fileEXT + " p_maxFileLength=" + p_maxFileLength);
        }
        final StringBuffer queryBuf = new StringBuffer();
        queryBuf.append(" SELECT U.user_id||','||parent_id||','||owner_id||','||user_type||','||external_code||','||msisdn");
        queryBuf.append(" ||','||REPLACE(L.lookup_name,',',' ')||','||REPLACE(login_id,',',' ')||','||U.category_code||','||CAT.category_name||','||");
        queryBuf
            .append(" UG.grph_domain_code||','||REPLACE(GD.grph_domain_name,',',' ')||','||REPLACE(user_name,',',' ')||','||REPLACE(city,',',' ')||','||REPLACE(state,',',' ')||','||REPLACE(country,',',' ')||','");
        queryBuf.append(" FROM users U, categories CAT,user_geographies UG,geographical_domains GD,lookups L, lookup_types LT");
        queryBuf.append(" WHERE U.user_id=UG.user_id AND U.category_code=CAT.category_code AND U.status<>'C'");
        queryBuf.append(" AND UG.grph_domain_code=GD.grph_domain_code AND L.lookup_code=U.status");
        queryBuf.append(" AND LT.lookup_type=? AND LT.lookup_type=L.lookup_type AND trunc(U.created_on)<=?");
        queryBuf.append(" AND user_type='CHANNEL'");
        final String selectQuery = queryBuf.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("fetchMasterData", "select query:" + selectQuery);
        }
        PreparedStatement selectPstmt = null;
        ResultSet selectRst = null;
        try {
            selectPstmt = p_con.prepareStatement(selectQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            selectPstmt.setString(1, PretupsI.USER_STATUS_TYPE);
            selectPstmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate));
            selectRst = selectPstmt.executeQuery();
            _log.debug("fetchMasterData", "Memory after loading master data: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime()
                .freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
            // method call to write data in files
            writeDataInFile(p_dirPath, p_fileName, p_fileLabel, p_beingProcessedDate, p_fileEXT, p_maxFileLength, selectRst, null, "2");
            _log.debug("fetchMasterData", "Memory after writing master files: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime()
                .freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
        } catch (BTSLBaseException be) {
            _log.error("fetchMasterData", "BTSLBaseException : " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (SQLException sqe) {
            _log.error("fetchMasterData", "SQLException " + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VFEDWHFileCreation[fetchMasterData]", "", "", "",
                "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("VFEDWHFileCreation", "fetchMasterData", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _log.error("fetchMasterData", "Exception : " + ex.getMessage());
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VFEDWHFileCreation[fetchMasterData]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException("VFEDWHFileCreation", "fetchMasterData", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (selectRst != null) {
                try {
                    selectRst.close();
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
            }
            if (selectPstmt != null) {
                try {
                    selectPstmt.close();
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
            }
            if (_log.isDebugEnabled()) {
                _log.debug("fetchMasterData", "Exiting ");
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
    private static void writeDataInFile(String p_dirPath, String p_fileName, String p_fileLabel, Date p_beingProcessedDate, String p_fileEXT, long p_maxFileLength, ResultSet rst1, ResultSet rst2, String call) throws BTSLBaseException {
        final String METHOD_NAME = "writeDataInFile";
        if (_log.isDebugEnabled()) {
            _log
                .debug(
                    "writeDataInFile",
                    " Entered:  p_dirPath=" + p_dirPath + " p_fileName=" + p_fileName + " p_fileLabel=" + p_fileLabel + " p_beingProcessedDate=" + p_beingProcessedDate + " p_fileEXT=" + p_fileEXT + " p_maxFileLength=" + p_maxFileLength);
        }
        long recordsWrittenInFile = 0;
        PrintWriter out = null;
        int fileNumber = 0;
        String fileName = null;
        File newFile = null;
        String fileData = null;
        String fileHeader = null;

        String txnStatus = null;
        String bonusDetails = null;

        try {

            // generating file name
            fileNumber = 1;

            // change fileNmae to YYYYMMDD_fileName001.csv
            final Date date = BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate);
            p_fileName = date.toString().substring(0, 4) + date.toString().substring(5, 7) + date.toString().substring(8, 10) + "_" + p_fileName;
            // if the length of file number is 1, two zeros are added as prefix
            if (Integer.toString(fileNumber).length() == 1) {
                fileName = p_dirPath + File.separator + p_fileName + "00" + fileNumber + p_fileEXT;
            } else if (Integer.toString(fileNumber).length() == 2) {
                fileName = p_dirPath + File.separator + p_fileName + "0" + fileNumber + p_fileEXT;
            } else if (Integer.toString(fileNumber).length() == 3) {
                fileName = p_dirPath + File.separator + p_fileName + fileNumber + p_fileEXT;
            }

            _log.debug("writeDataInFile", "  fileName=" + fileName);

            newFile = new File(fileName);
            _fileNameLst.add(fileName);
            //
            out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));

            // ID DWH002 to make addition of header and footer optional on the
            // basis of entry in Constants.props
            if ("Y".equalsIgnoreCase(Constants.getProperty("ADD_HEADER_FOOTER"))) {
                fileHeader = constructFileHeader(p_beingProcessedDate, fileNumber, p_fileLabel);
                out.write(fileHeader);
            }
            // traverse first resultset
            while (rst1.next()) {
                fileData = rst1.getString(1);
 
                out.write(fileData + "\n");
                recordsWrittenInFile++;
                if (recordsWrittenInFile >= p_maxFileLength) {

                    recordsWrittenInFile = 0;
                    fileNumber = fileNumber + 1;
                    out.close();

                    // if the length of file number is 1, two zeros are added as
                    // prefix
                    if (Integer.toString(fileNumber).length() == 1) {
                        fileName = p_dirPath + File.separator + p_fileName + "00" + fileNumber + p_fileEXT;
                    } else if (Integer.toString(fileNumber).length() == 2) {
                        fileName = p_dirPath + File.separator + p_fileName + "0" + fileNumber + p_fileEXT;
                    } else if (Integer.toString(fileNumber).length() == 3) {
                        fileName = p_dirPath + File.separator + p_fileName + fileNumber + p_fileEXT;
                    }

                    _log.debug("writeDataInFile", "  fileName=" + fileName);
                    newFile = new File(fileName);
                    _fileNameLst.add(fileName);
                    out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
                    // ID DWH002 to make addition of header and footer optional
                    // on the basis of entry in Constants.props
                    if ("Y".equalsIgnoreCase(Constants.getProperty("ADD_HEADER_FOOTER"))) {
                        fileHeader = constructFileHeader(p_beingProcessedDate, fileNumber, p_fileLabel);
                        out.write(fileHeader);
                    }
                }
            }

            // in case of master data, there is only one resultset
            // while in case of transactions data, two resultsets are to be
            // traversed.
            while (rst2 != null && rst2.next()) {
                fileData = rst2.getString(1);
                txnStatus = rst2.getString(2);
                bonusDetails = rst2.getString(3);

                out.write(fileData + "\n");
                recordsWrittenInFile++;
                if (recordsWrittenInFile >= p_maxFileLength) {

                    recordsWrittenInFile = 0;
                    fileNumber = fileNumber + 1;
                    out.close();

                    // if the length of file number is 1, two zeros are added as
                    // prefix
                    if (Integer.toString(fileNumber).length() == 1) {
                        fileName = p_dirPath + File.separator + p_fileName + "00" + fileNumber + p_fileEXT;
                    } else if (Integer.toString(fileNumber).length() == 2) {
                        fileName = p_dirPath + File.separator + p_fileName + "0" + fileNumber + p_fileEXT;
                    } else if (Integer.toString(fileNumber).length() == 3) {
                        fileName = p_dirPath + File.separator + p_fileName + fileNumber + p_fileEXT;
                    }

                    _log.debug("writeDataInFile", "  fileName=" + fileName);
                    newFile = new File(fileName);
                    _fileNameLst.add(fileName);
                    out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
                    // ID DWH002 to make addition of header and footer optional
                    // on the basis of entry in Constants.props
                    if ("Y".equalsIgnoreCase(Constants.getProperty("ADD_HEADER_FOOTER"))) {
                        fileHeader = constructFileHeader(p_beingProcessedDate, fileNumber, p_fileLabel);
                        out.write(fileHeader);
                    }
                }
            }
            // if number of records are not zero then footer is appended as file
            // is deleted
            if (recordsWrittenInFile > 0) {

            } else {
                if (out != null) {
                    out.close();
                }
                boolean isDeleted = newFile.delete();
                if(isDeleted){
                 _log.debug(METHOD_NAME, "File deleted successfully");
                }
                _fileNameLst.remove(_fileNameLst.size() - 1);
            }
            if (out != null) {
                out.close();
            }
        } catch (Exception e) {
            deleteAllFiles();
            _log.debug("writeDataInFile", "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VFEDWHFileCreation[writeDataInFile]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("VFEDWHFileCreation", "writeDataInFile", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (out != null) {
                out.close();
            }
            if (_log.isDebugEnabled()) {
                _log.debug("writeDataInFile", "Exiting ");
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
        if (_log.isDebugEnabled()) {
            _log.debug("markProcessStatusAsComplete", " Entered:  p_processId:" + p_processId);
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
            _log.errorTrace(METHOD_NAME, e);
            _log.error("markProcessStatusAsComplete", "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VFEDWHFileCreation[markProcessStatusAsComplete]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("VFEDWHFileCreation", "markProcessStatusAsComplete", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
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
        final String METHOD_NAME = "deleteAllFiles";
        if (_log.isDebugEnabled()) {
            _log.debug("deleteAllFiles", " Entered: ");
        }
        int size = 0;
        if (_fileNameLst != null) {
            size = _fileNameLst.size();
        }
        if (_log.isDebugEnabled()) {
            _log.debug("deleteAllFiles", " : Number of files to be deleted " + size);
        }
        String fileName = null;
        File newFile = null;
        for (int i = 0; i < size; i++) {
            try {
                fileName = (String) _fileNameLst.get(i);
                newFile = new File(fileName);
                boolean isDeleted = newFile.delete();
                if(isDeleted){
                 _log.debug(METHOD_NAME, "File deleted successfully");
                }
                if (_log.isDebugEnabled()) {
                    _log.debug("", fileName + " file deleted");
                }
            } catch (Exception e) {
                _log.error("deleteAllFiles", "Exception " + e.getMessage());
                _log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VFEDWHFileCreation[deleteAllFiles]", "", "", "",
                    "Exception:" + e.getMessage());
                throw new BTSLBaseException("VFEDWHFileCreation", "deleteAllFiles", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
            }
        }// end of for loop
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VFEDWHFileCreation[deleteAllFiles]", "", "", "",
            " Message: VFEDWHFileCreation process has found some error, so deleting all the files.");
        if (_fileNameLst != null && _fileNameLst.isEmpty()) {
            _fileNameLst.clear();
        }
        if (_log.isDebugEnabled()) {
            _log.debug("deleteAllFiles", " : Exiting.............................");
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

        final StringBuffer fileHeaderBuf = new StringBuffer("");
        fileHeaderBuf.append(p_fileLabel + "\n");
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
        final String METHOD_NAME = "moveFilesToFinalDirectory";
        if (_log.isDebugEnabled()) {
            _log
                .debug(
                    "moveFilesToFinalDirectory",
                    " Entered: p_oldDirectoryPath=" + p_oldDirectoryPath + " p_finalDirectoryPath=" + p_finalDirectoryPath + " p_processId=" + p_processId + " p_beingProcessedDate=" + p_beingProcessedDate);
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
        
        final String oldDirName = p_oldDirectoryPath;
        final String newDirName = p_finalDirectoryPath;
        File oldDir = new File(oldDirName);
        File newDir = new File(newDirName);
        if (!newDir.exists()) {
            newDir.mkdirs();
        }

        if (_log.isDebugEnabled()) {
            _log.debug("moveFilesToFinalDirectory", " newDirName=" + newDirName);
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
                	_log.debug(METHOD_NAME, "File renamed successfully");
                }
                if (_log.isDebugEnabled()) {
                    _log.debug("moveFilesToFinalDirectory", " File " + oldFileName + " is moved to " + newFileName);
                }
            }// end of for loop
            _fileNameLst.clear();
            if (oldDir.exists()) {
            	boolean isDeleted = oldDir.delete();
                if(isDeleted){
                 _log.debug(METHOD_NAME, "Directory deleted successfully");
                }
            }
            _log.debug("moveFilesToFinalDirectory", " File " + oldFileName + " is moved to " + newFileName);
        } catch (Exception e) {
            _log.error("moveFilesToFinalDirectory", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VFEDWHFileCreation[moveFilesToFinalDirectory]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("VFEDWHFileCreation", "deleteAllFiles", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
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
                _log.debug("moveFilesToFinalDirectory", "Exiting.. ");
            }
        } // end of finally

    }
}

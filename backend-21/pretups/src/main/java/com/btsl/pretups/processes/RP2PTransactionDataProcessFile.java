package com.btsl.pretups.processes;

/**
 * @(#)RP2PTransactionDataProcessFile
 *                                    Copyright(c) 2006, Comviva Technologies
 *                                    LTD.
 *                                    All Rights Reserved
 * 
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Author Date History
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
 *                                    Babu Kunwar 14-Apr-2011 Initial Creation
 *                                    ------------------------------------------
 *                                    ------------------------------------------
 *                                    -------------
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
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

public class RP2PTransactionDataProcessFile {
    private static ArrayList<String> _fileNameList = new ArrayList<String>();
    private static String _transactionFileLabel = null;
    private static String _transactionFileName = null;
    private static String _transactionDataDirectory = null;
    private static String _finalDirectoryPath = null;
    private static ProcessBL _checkProcessStatus = null;
    private static ProcessStatusVO _processStatusVO = null;
    private static String _childDirectory = null;
    private static String _fileExtension = null;
    private static long _maxFileLength = 0;

    private static final Log _logger = LogFactory.getLog(RP2PTransactionDataProcessFile.class.getName());

    /**
     * to ensure no class instantiation 
     */
    private RP2PTransactionDataProcessFile(){
    	
    }
    public static void main(String[] args) {
        final String METHOD_NAME = "main";
        try {
            if (args.length != 2) {
                System.out.println("Please provide path for [Constant Props] and [LogConfig]");
                return;
            }
            final File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
                System.out.println("RP2PTransactionDataProcessFile" + " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = new File(args[1]);
            if (!logconfigFile.exists()) {
                System.out.println("RP2PTransactionDataProcessFile" + " Logconfig File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("Error in Main() Method", " Error in Loading Files: " + e.getMessage());
            }
            _logger.errorTrace(METHOD_NAME, e);
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            process();
        } catch (BTSLBaseException be) {
            _logger.error("Error in Main() Method", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("main", "Exiting..... ");
            }
            ConfigServlet.destroyProcessCache();
        }

    }

    private static void process() throws BTSLBaseException {
        final String METHOD_NAME = "process";
        Date currentDate = new Date();
        Date _processedTillDate = null;
        Date countDate = null;
        Connection con = null;
        String _processID = null;
        boolean _isStatusOk = false;
        int beforeInterval = 0;
        int maxDoneDateUpdateCount = 0;
        ProcessStatusDAO processStatusDAO = null;
        try {
            _logger.debug("Inside process()",
                "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " " + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);
            /*
             * Loading all the parameters from Constant.props File
             * through loadConstantParameters() method.
             */
            loadConstantParameters();
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("process", " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "RP2PTransactionDataProcessFile[process]", "", "", "", "DATABASE Connection is NULL");
                return;
            }
            // getting process id
            _processID = ProcessI.TRANSACTION_ID;
            _checkProcessStatus = new ProcessBL();
            _processStatusVO = _checkProcessStatus.checkProcessUnderProcess(con, _processID);
            _isStatusOk = _processStatusVO.isStatusOkBool();
            beforeInterval = BTSLUtil.parseLongToInt( _processStatusVO.getBeforeInterval() / (60 * 24));
            if (_isStatusOk) {
                con.commit();
                // Checking the Maximum Date Upto Which Process Has Been
                // Executes
                _processedTillDate = _processStatusVO.getExecutedUpto();
                if (_processedTillDate != null) {
                    // Checking Wheter the process is Executed Till Current Date
                    // or not
                    if (_processedTillDate.compareTo(currentDate) == 0) {
                        throw new BTSLBaseException("RP2PTransactionDataProcessFile", "process", PretupsErrorCodesI.RP2P_PROCESS_ALREADY_EXECUTED_TILL_TODAY);
                    }
                    // Adding 1 to Current Date as process has to run from Next
                    // Date
                    _processedTillDate = BTSLUtil.addDaysInUtilDate(_processedTillDate, 1);
                    // Running the process in Loop
                    for (countDate = BTSLUtil.getSQLDateFromUtilDate(_processedTillDate); countDate.before(BTSLUtil.addDaysInUtilDate(currentDate, -beforeInterval)); countDate = BTSLUtil
                        .addDaysInUtilDate(countDate, 1)) {
                        if (countDate != null) {
                            _childDirectory = createDirectory(_transactionDataDirectory, _processID);
                            getChannelTransactionData(con, countDate, _childDirectory, _transactionFileName, _transactionFileLabel, _fileExtension, _maxFileLength);
                            _processStatusVO.setExecutedUpto(countDate);
                            _processStatusVO.setExecutedOn(currentDate);
                            processStatusDAO = new ProcessStatusDAO();
                            maxDoneDateUpdateCount = processStatusDAO.updateProcessDetail(con, _processStatusVO);
                            if (maxDoneDateUpdateCount > 0) {
                                moveFilesToFinalDirectory(_transactionDataDirectory, _finalDirectoryPath, _processID);
                                con.commit();
                            } else {
                                deleteAllFiles();
                                con.rollback();
                                throw new BTSLBaseException("RP2PTransactionDataProcessFile", "process", PretupsErrorCodesI.RP2P_COULD_NOT_UPDATE_MAX_DONE_DATE);
                            }
                        }
                    }
                }
            }
        } catch (BTSLBaseException be) {
            _logger.error("process", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            if (_fileNameList.size() > 0) {
                deleteAllFiles();
            }
            _logger.error("process", "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "RP2PTransactionDataProcessFile[process]", "", "", "",
                " RP2PTransactionDataProcessFile process could not be executed successfully.");
            throw new BTSLBaseException("RP2PTransactionDataProcessFile", "process", PretupsErrorCodesI.RP2P_ERROR_EXCEPTION);
        } finally {
            if (_isStatusOk) {
                try {
                    if (markProcessStatusAsComplete(con, _processID) == 1) {
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
                        _logger.debug("process", "Exception closing connection ");
                    }
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
        }
        _logger.debug("process", "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
        if (_logger.isDebugEnabled()) {
            _logger.debug("process", "Exiting..... ");
        }
    }

    private static void loadConstantParameters() throws BTSLBaseException {
        final String METHOD_NAME = "loadConstantParameters";
        if (_logger.isDebugEnabled()) {
            _logger.debug("loadConstantParameters", " Entered: ");
        }
        try {
            _transactionFileLabel = Constants.getProperty("TRANSACTION_FILE_LABEL");
            if (BTSLUtil.isNullString(_transactionFileLabel)) {
                _logger.debug("Inside loadConstantParameters", "TRANSACTION_FILE_LABEL Value not Retrieved");
            } else {
                _logger.debug("Inside loadConstantParameters", "TRANSACTION_FILE_LABEL Value" + " " + _transactionFileLabel);
            }

            _transactionFileName = Constants.getProperty("CHANNEL_TRANSACTION_FILE_NAME");
            if (BTSLUtil.isNullString(_transactionFileName)) {
                _logger.debug("Inside loadConstantParameters", "CHANNEL_TRANSACTION_FILE_NAME Value not Retrieved");
            } else {
                _logger.debug("Inside loadConstantParameters", "CHANNEL_TRANSACTION_FILE_NAME Value" + " " + _transactionFileName);
            }

            _transactionDataDirectory = Constants.getProperty("TRANSACTION_DATA_DIRECTORY");
            if (BTSLUtil.isNullString(_transactionDataDirectory)) {
                _logger.debug("Inside loadConstantParameters", "TRANSACTION_DATA_DIRECTORY Path Does Not Exsist");
            } else {
                _logger.debug("Inside loadConstantParameters", "TRANSACTION_DATA_DIRECTORY Value" + " " + _transactionDataDirectory);
            }

            _finalDirectoryPath = Constants.getProperty("FINAL_TRANSACTION_DATA_DIRECTORY");
            if (BTSLUtil.isNullString(_finalDirectoryPath)) {
                _logger.debug("Inside loadConstantParameters", "FINAL_TRANSACTION_DATA_DIRECTORY Path Does Not Exsist");
            } else {
                _logger.debug("Inside loadConstantParameters", "FINAL_TRANSACTION_DATA_DIRECTORY Value" + " " + _finalDirectoryPath);
            }

            if (BTSLUtil.isNullString(_transactionFileLabel) || BTSLUtil.isNullString(_transactionFileName) || BTSLUtil.isNullString(_transactionDataDirectory) || BTSLUtil
                .isNullString(_finalDirectoryPath)) {
                throw new BTSLBaseException("RP2PTransactionDataProcessFile", "loadConstantParameters", PretupsErrorCodesI.RP2P_COULD_NOT_FIND_DATA_IN_CONSTANTS_FILE);
            }
            try {
                _fileExtension = Constants.getProperty("RP2P_FILE_EXTENSION");
                _logger.debug("Inside loadConstantParameters", "File Extension Value" + " " + _fileExtension);
            } catch (Exception fe) {
                _logger.debug("Inside loadConstantParameters", "Error in getting File Extension" + " " + _fileExtension);
                _logger.errorTrace(METHOD_NAME, fe);
            }
            try {
                _maxFileLength = Long.parseLong(Constants.getProperty("RP2P_MAX_FILE_LENGTH"));
                _logger.debug("Inside loadConstantParameters", "File Length Value" + " " + _maxFileLength);
            } catch (Exception fe) {
                _logger.debug("Inside loadConstantParameters", "Error in getting File Length" + " " + _maxFileLength);
                _logger.errorTrace(METHOD_NAME, fe);
            }
            _logger.debug("loadConstantParameters", "******************************************************************************");
            _logger.debug("loadConstantParameters", "      " + "Required information successfuly loaded from Constants.props" + "      ");
            _logger.debug("loadConstantParameters", "******************************************************************************");
        } catch (BTSLBaseException be) {
            _logger.error("Inside loadConstantParameters", "BTSLBaseException : " + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "RP2PTransactionDataProcessFile[loadConstantParameters]", "", "", "", "Message:" + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _logger.error("Inside loadConstantParameters", "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            final BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.RP2P_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "RP2PTransactionDataProcessFile[loadConstantParameters]", "", "", "", "Message:" + btslMessage);
            throw new BTSLBaseException("Inside loadConstantParameters", "loadConstantParameters", PretupsErrorCodesI.RP2P_ERROR_EXCEPTION);
        }

    }

    private static void deleteAllFiles() throws BTSLBaseException {
        final String METHOD_NAME = "deleteAllFiles";
        if (_logger.isDebugEnabled()) {
            _logger.debug("deleteAllFiles", " Entered: ");
        }
        int size = 0;
        if (_fileNameList != null) {
            size = _fileNameList.size();
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug("deleteAllFiles", " : Number of files to be deleted " + size);
        }
        String fileName = null;
        File newFile = null;
        for (int i = 0; i < size; i++) {
            try {
                fileName = (String) _fileNameList.get(i);
                newFile = new File(fileName);
                boolean isDeleted = newFile.delete();
                if(isDeleted){
                 _logger.debug(METHOD_NAME, "File deleted successfully");
                }
                if (_logger.isDebugEnabled()) {
                    _logger.debug("", fileName + " file deleted");
                }
            } catch (Exception e) {
                _logger.error("deleteAllFiles", "Exception " + e.getMessage());
                _logger.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RP2PTransactionDataProcessFile[deleteAllFiles]",
                    "", "", "", "Exception:" + e.getMessage());
                throw new BTSLBaseException("RP2PTransactionDataProcessFile", "deleteAllFiles", PretupsErrorCodesI.RP2P_ERROR_EXCEPTION);
            }
        }// end of for loop
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RP2PTransactionDataProcessFile[deleteAllFiles]", "", "",
            "", " Message: RP2PTransactionDataProcessFile process has found some error, so deleting all the files.");
        if (_fileNameList != null && _fileNameList.isEmpty()) {
            _fileNameList.clear();
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug("deleteAllFiles", " : Exiting.............................");
        }

    }

    private static String createDirectory(String p_directoryPathAndName, String p_processId) throws BTSLBaseException {
        final String METHOD_NAME = "createDirectory";
        if (_logger.isDebugEnabled()) {
            _logger.debug("createDirectory", " Entered: p_directoryPathAndName=" + p_directoryPathAndName + " p_processId=" + p_processId);
        }
        String directoryName = null;
        try {

            boolean success = false;
            final File parentDir = new File(p_directoryPathAndName);
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            // child directory name includes a file name and being processed
            // date, month and year
            directoryName = p_directoryPathAndName + File.separator + p_processId;
            final File newDir = new File(directoryName);
            if (!newDir.exists()) {
                success = newDir.mkdirs();
            } else {
                success = true;
            }
            if (!success) {
                throw new BTSLBaseException("RP2PTransactionDataProcessFile", "createDirectory", PretupsErrorCodesI.COULD_NOT_CREATE_DIR);
            }
        } catch (BTSLBaseException be) {
            _logger.error("createDirectory", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception ex) {
            _logger.error("createDirectory", "Exception : " + ex.getMessage());
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RP2PTransactionDataProcessFile[createDirectory]", "",
                "", "", "SQLException:" + ex.getMessage());
            throw new BTSLBaseException("RP2PTransactionDataProcessFile", "createDirectory", PretupsErrorCodesI.RP2P_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("createDirectory", "Exiting dirName=" + directoryName);
            }
        }
        return directoryName;
    }

    private static void getChannelTransactionData(Connection p_con, Date p_beingProcessedDate, String p_dirPath, String p_fileName, String p_fileLabel, String p_fileEXT, long p_maxFileLength) throws BTSLBaseException {
        final String METHOD_NAME = "getChannelTransactionData";
        if (_logger.isDebugEnabled()) {
            _logger
                .debug(
                    "getChannelTransactionData",
                    " Entered: p_beingProcessedDate=" + p_beingProcessedDate + " p_dirPath=" + p_dirPath + " p_fileName=" + p_fileName + " p_fileLabel=" + p_fileLabel + " p_fileEXT=" + p_fileEXT + " p_maxFileLength=" + p_maxFileLength);
        }

        final StringBuffer channelDataBuffer = new StringBuffer();

        channelDataBuffer.append(" SELECT user_id,user_id||','||TRANS_DATE||','||CATEGORY_CODE");
        channelDataBuffer.append(" ||','||SENDER_DOMAIN_CODE||','||ROAM_C2S_TRANSFER_OUT_AMOUNT");
        channelDataBuffer.append(" ||','||OPENING_BALANCE||','||CLOSING_BALANCE");
        channelDataBuffer.append(" ||','||C2S_TRANSFER_OUT_COUNT||','||C2S_TRANSFER_OUT_AMOUNT");
        channelDataBuffer.append(" ||','||O2C_TRANSFER_IN_COUNT||','||O2C_TRANSFER_IN_AMOUNT");
        channelDataBuffer.append(" ||','||O2C_RETURN_OUT_COUNT||','||O2C_RETURN_OUT_AMOUNT");
        channelDataBuffer.append(" ||','||O2C_WITHDRAW_OUT_COUNT||','||O2C_WITHDRAW_OUT_AMOUNT");
        channelDataBuffer.append(" ||','||C2C_TRANSFER_IN_COUNT||','||C2C_TRANSFER_IN_AMOUNT");
        channelDataBuffer.append(" ||','||C2C_TRANSFER_OUT_COUNT||','||C2C_TRANSFER_OUT_AMOUNT");
        channelDataBuffer.append(" ||','||C2C_RETURN_IN_COUNT||','||C2C_RETURN_IN_AMOUNT");
        channelDataBuffer.append(" ||','||C2C_RETURN_OUT_COUNT||','||C2C_RETURN_OUT_AMOUNT");
        channelDataBuffer.append(" ||','||C2C_WITHDRAW_IN_COUNT||','||C2C_WITHDRAW_IN_AMOUNT");
        channelDataBuffer.append(" ||','||C2C_WITHDRAW_OUT_COUNT||','||C2C_WITHDRAW_OUT_AMOUNT");
        channelDataBuffer.append(" ||','||DIFFERENTIAL||','||ADJUSTMENT_IN");
        channelDataBuffer.append(" ||','||ADJUSTMENT_OUT||','||CREATED_ON||','||GRPH_DOMAIN_CODE");
        channelDataBuffer.append(" FROM DAILY_CHNL_TRANS_MAIN DCTM, PROCESS_STATUS PS");
        channelDataBuffer.append(" WHERE TRANS_DATE =? AND PS.PROCESS_ID=? order by user_id");
        final String channelDataSelectQuery = channelDataBuffer.toString();
        if (_logger.isDebugEnabled()) {
            _logger.debug("getChannelTransactionData", "Channel Data SELECT query:" + channelDataSelectQuery);
        }
        ResultSet channelDataResultSet = null;
        PreparedStatement channelDataPreparedStmt = null;
        try {
            channelDataPreparedStmt = p_con.prepareStatement(channelDataSelectQuery);
            channelDataPreparedStmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate));
            channelDataPreparedStmt.setString(2, ProcessI.TRANSACTION_ID);
            channelDataResultSet = channelDataPreparedStmt.executeQuery();
            _logger.debug("getChannelTransactionData", "Memory after loading master data: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime
                .getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
            if (channelDataResultSet.next()) {
                writeDataInFile(p_dirPath, p_fileName, p_fileLabel, p_beingProcessedDate, p_fileEXT, p_maxFileLength, channelDataResultSet);
            } else {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "RP2PTransactionDataProcessFile[getChannelTransactionData]", "", "", "", "No Data Found In DataBase For this Date");
            }
            _logger.debug("getChannelTransactionData", "Memory after writing master files: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime
                .getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
        } catch (BTSLBaseException be) {
            _logger.error("getChannelTransactionData", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (SQLException sqe) {
            _logger.error("getChannelTransactionData", "SQLException " + sqe.getMessage());
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "RP2PTransactionDataProcessFile[getChannelTransactionData]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("RP2PTransactionDataProcessFile", "getChannelTransactionData", PretupsErrorCodesI.RP2P_ERROR_EXCEPTION);
        } catch (Exception ex) {
            _logger.error("getChannelTransactionData", "Exception : " + ex.getMessage());
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "RP2PTransactionDataProcessFile[getChannelTransactionData]", "", "", "", "SQLException:" + ex.getMessage());
            throw new BTSLBaseException("RP2PTransactionDataProcessFile", "getChannelTransactionData", PretupsErrorCodesI.RP2P_ERROR_EXCEPTION);
        }finally{
        	try {
                if (channelDataResultSet != null) {
                	channelDataResultSet.close();
                }
            } catch (Exception ex) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, "Exception closing Callable statement ");
                }
                _logger.errorTrace(METHOD_NAME, ex);
            }
        	try {
                if (channelDataPreparedStmt != null) {
                	channelDataPreparedStmt.close();
                }
            } catch (Exception ex) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, "Exception closing Callable statement ");
                }
                _logger.errorTrace(METHOD_NAME, ex);
            }
        }
    }

    private static void writeDataInFile(String p_dirPath, String p_fileName, String p_fileLabel, Date p_beingProcessedDate, String p_fileEXT, long p_maxFileLength, ResultSet firstResultSet) throws BTSLBaseException {
        final String METHOD_NAME = "writeDataInFile";
        if (_logger.isDebugEnabled()) {
            _logger
                .debug(
                    "writeDataInFile",
                    " Entered:  p_dirPath=" + p_dirPath + " p_fileName=" + p_fileName + " p_fileLabel=" + p_fileLabel + " p_beingProcessedDate=" + p_beingProcessedDate + " p_fileEXT=" + p_fileEXT + " p_maxFileLength=" + p_maxFileLength);
        }
        long totalRecordsWritten = 0;
        PrintWriter out = null;
        int fileNumber = 0;
        String _transactionFileName = null;
        File RP2PnewFile = null;
        String fileData = null;
        String _transactionFileHeader = null;

        try {
            fileNumber = 1;
            final Date date = BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate);
            p_fileName = date.toString().substring(0, 4) + date.toString().substring(5, 7) + date.toString().substring(8, 10) + "_" + p_fileName;
            // if the length of file number is 1, two zeros are added as prefix
            if (Integer.toString(fileNumber).length() == 1) {
                _transactionFileName = p_dirPath + File.separator + p_fileName + "00" + fileNumber + p_fileEXT;
            } else if (Integer.toString(fileNumber).length() == 2) {
                _transactionFileName = p_dirPath + File.separator + p_fileName + "0" + fileNumber + p_fileEXT;
            } else if (Integer.toString(fileNumber).length() == 3) {
                _transactionFileName = p_dirPath + File.separator + p_fileName + fileNumber + p_fileEXT;
            }

            _logger.debug("writeDataInFile", "  fileName=" + _transactionFileName);
            RP2PnewFile = new File(_transactionFileName);
            _fileNameList.add(_transactionFileName);
            out = new PrintWriter(new BufferedWriter(new FileWriter(RP2PnewFile)));
            if ("Y".equalsIgnoreCase(Constants.getProperty("ADD_HEADER_FOOTER_IN_FILE"))) {
                _transactionFileHeader = constructFileHeader(p_fileLabel);
                out.write(_transactionFileHeader);
            }
            while (firstResultSet != null && firstResultSet.next()) {
                fileData = firstResultSet.getString(2);
                if (!BTSLUtil.isNullString(fileData)) {
                    out.write("\n" + fileData);
                }
                totalRecordsWritten++;
                if (totalRecordsWritten >= p_maxFileLength) {
                    totalRecordsWritten = 0;
                    fileNumber = fileNumber + 1;
                    out.close();
                    // Length of file number is 1, two zeros are added as prefix
                    if (Integer.toString(fileNumber).length() == 1) {
                        _transactionFileName = p_dirPath + File.separator + p_fileName + "00" + fileNumber + p_fileEXT;
                    } else if (Integer.toString(fileNumber).length() == 2) {
                        _transactionFileName = p_dirPath + File.separator + p_fileName + "0" + fileNumber + p_fileEXT;
                    } else if (Integer.toString(fileNumber).length() == 3) {
                        _transactionFileName = p_dirPath + File.separator + p_fileName + fileNumber + p_fileEXT;
                    }

                    _logger.debug("writeDataInFile", "  fileName=" + _transactionFileName);
                    RP2PnewFile = new File(_transactionFileName);
                    BTSLUtil.closeOpenStream(out, RP2PnewFile);
                    _fileNameList.add(_transactionFileName);
                    //out = new PrintWriter(new BufferedWriter(new FileWriter(RP2PnewFile)));
                }
            }
            if (totalRecordsWritten < 0) {
                boolean isDeleted = RP2PnewFile.delete();
                if(isDeleted){
                 _logger.debug(METHOD_NAME, "File deleted successfully");
                }
                _fileNameList.remove(_fileNameList.size() - 1);
            }
            
        } catch (Exception e) {
            deleteAllFiles();
            _logger.debug("writeDataInFile", "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RP2PTransactionDataProcessFile[writeDataInFile]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("RP2PTransactionDataProcessFile", "writeDataInFile", PretupsErrorCodesI.RP2P_ERROR_EXCEPTION);
        } finally {
        	try{
        		if (out != null) {
                    out.close();
                }
        	}catch(Exception e){
        		_logger.errorTrace(METHOD_NAME, e);
        	}
            if (_logger.isDebugEnabled()) {
                _logger.debug("writeDataInFile", "Exiting ");
            }
        }
    }

    private static String constructFileHeader(String p_fileLabel) {

        final StringBuffer fileHeaderBuf = new StringBuffer("");
        fileHeaderBuf.append("\n" + p_fileLabel);
        return fileHeaderBuf.toString();
    }

    private static int markProcessStatusAsComplete(Connection p_con, String p_processId) throws BTSLBaseException {
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
            updateCount = processStatusDAO.updateProcessDetail(p_con, _processStatusVO);
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("markProcessStatusAsComplete", "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "RP2PTransactionDataProcessFile[markProcessStatusAsComplete]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("RP2PTransactionDataProcessFile", "markProcessStatusAsComplete", PretupsErrorCodesI.RP2P_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
            }
        }
        return updateCount;
    }

    private static void moveFilesToFinalDirectory(String p_oldDirectoryPath, String p_finalDirectoryPath, String p_processId) throws BTSLBaseException {

        final String METHOD_NAME = "moveFilesToFinalDirectory";
        if (_logger.isDebugEnabled()) {
            _logger.debug("moveFilesToFinalDirectory",
                " Entered: p_oldDirectoryPath=" + p_oldDirectoryPath + " p_finalDirectoryPath=" + p_finalDirectoryPath + " p_processId=" + p_processId);
        }

        String oldFileName = null;
        String newFileName = null;
        File oldFile = null;
        File newFile = null;
        File parentDir = new File(p_finalDirectoryPath);
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        // child directory name includes a file name and being processed date,
        // month and year
        final String oldDirName = p_oldDirectoryPath + File.separator + p_processId;
        final String newDirName = p_finalDirectoryPath + File.separator + p_processId;
        File oldDir = new File(oldDirName);
        File newDir = new File(newDirName);
        if (!newDir.exists()) {
            newDir.mkdirs();
        }

        if (_logger.isDebugEnabled()) {
            _logger.debug("moveFilesToFinalDirectory", " newDirName=" + newDirName);
        }

        final int size = _fileNameList.size();
        try {
            for (int i = 0; i < size; i++) {
                oldFileName = (String) _fileNameList.get(i);
                oldFile = new File(oldFileName);
                newFileName = oldFileName.replace(p_oldDirectoryPath, p_finalDirectoryPath);
                newFile = new File(newFileName);
                if(oldFile.renameTo(newFile))
                {
                	_logger.debug(METHOD_NAME, "File renamed successfully");
                }
                if (_logger.isDebugEnabled()) {
                    _logger.debug("moveFilesToFinalDirectory", " File " + oldFileName + " is moved to " + newFileName);
                }
            }
            _fileNameList.clear();
            if (oldDir.exists()) {
            	boolean isDeleted = oldDir.delete();
                if(isDeleted){
                 _logger.debug(METHOD_NAME, "Directory deleted successfully");
                }
            }
            _logger.debug("moveFilesToFinalDirectory", " File " + oldFileName + " is moved to " + newFileName);
        } catch (Exception e) {
            _logger.error("moveFilesToFinalDirectory", "Exception " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "RP2PTransactionDataProcessFile[moveFilesToFinalDirectory]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("RP2PTransactionDataProcessFile", "deleteAllFiles", PretupsErrorCodesI.RP2P_ERROR_EXCEPTION);
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
                _logger.debug("moveFilesToFinalDirectory", "Exiting.. ");
            }
        }
    }
}

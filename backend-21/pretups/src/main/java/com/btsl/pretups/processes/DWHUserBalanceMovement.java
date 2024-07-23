package com.btsl.pretups.processes;

/**
 * @(#)DWHUserBalanceMovement
 *                            Copyright(c) 2009, Bharti Telesoft Ltd.
 *                            All Rights Reserved
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Ankit Singhal 05/09/2009 Initial Creation
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

public class DWHUserBalanceMovement {
    private static String _dwhFileLabelForUserBalMove = null;
    private static String _dwhFileNameForUserBalMove = null;
    private static String _masterDirectoryPathAndName = null;
    private static String _finalMasterDirectoryPath = null;
    private static String _childDirectory = null;
    private static String _fileEXT = null;
    private static long _maxFileLength = 0;
    private static ArrayList _fileNameLst = new ArrayList();
    private static ProcessStatusVO _processStatusVO;
    private static ProcessBL _processBL = null;

    private static Log _logger = LogFactory.getLog(DWHUserBalanceMovement.class.getName());


    /**
     * ensures no instantiation
     */
    private DWHUserBalanceMovement(){
    	
    }
    
    public static void main(String arg[]) {
        final String METHOD_NAME = "main";
        try {
            if (arg.length != 2) {
                System.out.println("Usage : DWHUserBalanceMovement [Constants file] [LogConfig file]");
                return;
            }
            final File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
                System.out.println("DWHUserBalanceMovement" + " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                System.out.println("DWHUserBalanceMovement" + " Logconfig File Not Found .............");
                return;
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
            process();
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
            _logger.debug(METHOD_NAME, "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);
            // getting all the required parameters from Constants.props
            loadConstantParameters();

            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHUserBalanceMovement[process]",
                    "", "", "", "DATABASE Connection is NULL");
                return;
            }
            // getting process id
            processId = ProcessI.DWH_USER_BAL_MOV_PROCESSID;
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
                    if (processedUpto.compareTo(currentDate) == 0) {
                        throw new BTSLBaseException("DWHUserBalanceMovement", METHOD_NAME, PretupsErrorCodesI.USRBALMOVT_PROCESS_ALREADY_EXECUTED_TILL_TODAY);
                    }
                    // adding 1 in processed upto dtae as we have to start from
                    // the next day till which process has been executed
                    processedUpto = BTSLUtil.addDaysInUtilDate(processedUpto, 1);
                    // loop to be started for each date
                    // the loop starts from the date till which process has been
                    // executed and executes one day before current date
                    for (dateCount = BTSLUtil.getSQLDateFromUtilDate(processedUpto); dateCount.before(BTSLUtil.addDaysInUtilDate(currentDate, -beforeInterval)); dateCount = BTSLUtil
                        .addDaysInUtilDate(dateCount, 1)) {
                        // method call to create master directory and child
                        // directory if does not exist
                        // Using the DWH process id for writing the files in
                        // same directory as of DWH.
                        _childDirectory = createDirectory(_masterDirectoryPathAndName, ProcessI.DWH_PROCESSID, dateCount);
                        // method call to fetch data and write it in files
                        fetchData(con, dateCount, _childDirectory, _dwhFileNameForUserBalMove, _dwhFileLabelForUserBalMove, _fileEXT, _maxFileLength);
                        // method call to update maximum date till which process
                        // has been executed
                        _processStatusVO.setExecutedUpto(dateCount);
                        _processStatusVO.setExecutedOn(currentDate);
                        processStatusDAO = new ProcessStatusDAO();
                        maxDoneDateUpdateCount = processStatusDAO.updateProcessDetail(con, _processStatusVO);

                        // if the process is successful, transaction is commit,
                        // else rollback
                        if (maxDoneDateUpdateCount > 0) {
                            moveFilesToFinalDirectory(_masterDirectoryPathAndName, _finalMasterDirectoryPath, ProcessI.DWH_PROCESSID, dateCount);
                            con.commit();
                        } else {
                            deleteAllFiles();
                            con.rollback();
                            throw new BTSLBaseException("DWHUserBalanceMovement", METHOD_NAME, PretupsErrorCodesI.USRBALMOVT_COULD_NOT_UPDATE_MAX_DONE_DATE);
                        }
                        // DWH002 sleep has been added after processing records
                        // of one day
                        Thread.sleep(500);
                    }// end loop
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DWHUserBalanceMovement[process]", "", "", "",
                        " DWHUserBalanceMovement process has been executed successfully.");
                }
                // ID DWH002 to avoid the null pointer exception thrown, in case
                // processesUpto is null
                else {
                    throw new BTSLBaseException("DWHUserBalanceMovement", METHOD_NAME, PretupsErrorCodesI.USRBALMOVT_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
                }
            }
        }// end of try
        catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            if (_fileNameLst.size() > 0) {
                deleteAllFiles();
            }
            _logger.error(METHOD_NAME, "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DWHUserBalanceMovement[process]", "", "", "",
                " DWHUserBalanceMovement process could not be executed successfully.");
            throw new BTSLBaseException("DWHUserBalanceMovement", METHOD_NAME, PretupsErrorCodesI.USRBALMOVT_ERROR_EXCEPTION);
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
            _logger.debug(METHOD_NAME, "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... ");
            }
        }
    }

    private static void loadConstantParameters() throws BTSLBaseException {
        if (_logger.isDebugEnabled()) {
            _logger.debug("loadParameters", " Entered: ");
        }
        final String METHOD_NAME = "loadConstantParameters";
        try {
            _dwhFileLabelForUserBalMove = Constants.getProperty("DWH_USR_BAL_MOV_FILE_LABEL");
            if (BTSLUtil.isNullString(_dwhFileLabelForUserBalMove)) {
                _logger.error(METHOD_NAME, " Could not find file label for master data in the Constants file.");
            } else {
                _logger.debug(METHOD_NAME, " _dwhFileLabelForUserBalMove=" + _dwhFileLabelForUserBalMove);
            }
            _dwhFileNameForUserBalMove = Constants.getProperty("DWH_USR_BAL_MOV_FILE_NAME");
            if (BTSLUtil.isNullString(_dwhFileNameForUserBalMove)) {
                _logger.error(METHOD_NAME, " Could not find file name for data in the Constants file.");
            } else {
                _logger.debug(METHOD_NAME, " _dwhFileNameForUserBalMove=" + _dwhFileNameForUserBalMove);
            }

            _masterDirectoryPathAndName = Constants.getProperty("DWH_MASTER_DIRECTORY");
            if (BTSLUtil.isNullString(_masterDirectoryPathAndName)) {
                _logger.error(METHOD_NAME, " Could not find directory path in the Constants file.");
            } else {
                _logger.debug(METHOD_NAME, " _masterDirectoryPathAndName=" + _masterDirectoryPathAndName);
            }
            _finalMasterDirectoryPath = Constants.getProperty("DWH_FINAL_DIRECTORY");
            if (BTSLUtil.isNullString(_finalMasterDirectoryPath)) {
                _logger.error(METHOD_NAME, " Could not find final directory path in the Constants file.");
            } else {
                _logger.debug(METHOD_NAME, " finalMasterDirectoryPath=" + _finalMasterDirectoryPath);
            }

            // checking that none of the required parameters should be null
            if (BTSLUtil.isNullString(_dwhFileLabelForUserBalMove) || BTSLUtil.isNullString(_dwhFileNameForUserBalMove) || BTSLUtil.isNullString(_masterDirectoryPathAndName) || BTSLUtil
                .isNullString(_finalMasterDirectoryPath)) {
                throw new BTSLBaseException("DWHUserBalanceMovement", METHOD_NAME, PretupsErrorCodesI.USRBALMOVT_COULD_NOT_FIND_DATA_IN_CONSTANTS_FILE);
            }
            try {
                _fileEXT = Constants.getProperty("DWH_FILE_EXT");
            } catch (Exception e) {
                _fileEXT = ".csv";
                _logger.errorTrace(METHOD_NAME, e);
            }
            _logger.debug(METHOD_NAME, " _fileEXT=" + _fileEXT);
            try {
                _maxFileLength = Long.parseLong(Constants.getProperty("DWH_MAX_FILE_LENGTH"));
            } catch (Exception e) {
                _maxFileLength = 1000;
                _logger.errorTrace(METHOD_NAME, e);
            }
            _logger.debug(METHOD_NAME, " _maxFileLength=" + _maxFileLength);
            _logger.debug(METHOD_NAME, " Required information successfuly loaded from Constants.props...............: ");
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHUserBalanceMovement[loadConstantParameters]", "",
                "", "", "Message:" + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            final BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.USRBALMOVT_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHUserBalanceMovement[loadConstantParameters]", "",
                "", "", "Message:" + btslMessage);
            throw new BTSLBaseException("DWHUserBalanceMovement", METHOD_NAME, PretupsErrorCodesI.USRBALMOVT_ERROR_EXCEPTION);
        }
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
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME,
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
            // child directory name includes a file name and being processed
            // date, month and year
            //dirName = p_directoryPathAndName + File.separator + p_processId + "_" + p_beingProcessedDate.toString().substring(8, 10) + p_beingProcessedDate.toString()
                //.substring(5, 7) + p_beingProcessedDate.toString().substring(2, 4);
            dirName = p_directoryPathAndName + File.separator + p_processId + "_" + BTSLUtil.getDateStrForName(p_beingProcessedDate);
            
            final File newDir = new File(dirName);
            if (!newDir.exists()) {
                success = newDir.mkdirs();
            } else {
                success = true;
            }
            if (!success) {
                throw new BTSLBaseException("DWHUserBalanceMovement", METHOD_NAME, PretupsErrorCodesI.COULD_NOT_CREATE_DIR);
            }
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception ex) {
            _logger.error(METHOD_NAME, "Exception : " + ex.getMessage());
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHUserBalanceMovement[createDirectory]", "", "", "",
                "SQLException:" + ex.getMessage());
            throw new BTSLBaseException("DWHUserBalanceMovement", METHOD_NAME, PretupsErrorCodesI.USRBALMOVT_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting dirName=" + dirName);
            }
        }
        return dirName;
    }

    /**
     * This method will fetch all the required data from table
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
    private static void fetchData(Connection p_con, Date p_beingProcessedDate, String p_dirPath, String p_fileName, String p_fileLabel, String p_fileEXT, long p_maxFileLength) throws BTSLBaseException {
        final String METHOD_NAME = "fetchData";
        if (_logger.isDebugEnabled()) {
            _logger
                .debug(
                    METHOD_NAME,
                    " Entered:  p_beingProcessedDate=" + p_beingProcessedDate + " p_dirPath=" + p_dirPath + " p_fileName=" + p_fileName + " p_fileLabel=" + p_fileLabel + " p_fileEXT=" + p_fileEXT + " p_maxFileLength=" + p_maxFileLength);
        }
        final StringBuffer queryBuf = new StringBuffer();

        queryBuf.append(" SELECT to_char(CT.trans_date,'dd/mm/yyyy')||';'||U.user_id||';'||U.user_name||';'||U.msisdn||';'|| ");
        queryBuf.append(" CT.opening_balance||';'||CT.o2c_transfer_in_amount||';'||");
        queryBuf.append(" (CT.o2c_return_out_amount+CT.o2c_withdraw_out_amount)||';'||");
        queryBuf.append(" (CT.c2c_transfer_out_amount+CT.c2c_withdraw_out_amount+CT.c2c_return_out_amount)||';'||");
        queryBuf.append(" (CT.c2c_withdraw_in_amount+CT.c2c_return_in_amount+CT.c2c_transfer_in_amount)||';'||");
        queryBuf.append(" CT.c2s_transfer_out_amount||';'||CT.closing_balance||';'|| ");
        queryBuf.append(" case when (CT.closing_balance=CT.opening_balance+CT.o2c_transfer_in_amount-CT.o2c_return_out_amount-CT.o2c_withdraw_out_amount-");
        queryBuf
            .append(" CT.c2c_transfer_out_amount-CT.c2c_withdraw_out_amount-CT.c2c_return_out_amount+CT.c2c_withdraw_in_amount+CT.c2c_return_in_amount+CT.c2c_transfer_in_amount-CT.c2s_transfer_out_amount) then 'N' else 'Y' end");
        queryBuf.append(" FROM daily_chnl_trans_main CT, users U ");
        queryBuf.append(" WHERE  CT.trans_date = ? AND CT.user_id = U.user_id ");

        final String selectQuery = queryBuf.toString();
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "select query:" + selectQuery);
        }
        PreparedStatement selectPstmt = null;
        ResultSet selectRst = null;
        try {
            selectPstmt = p_con.prepareStatement(selectQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            selectPstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate));
            selectRst = selectPstmt.executeQuery();
            _logger
                .debug(
                    METHOD_NAME,
                    "Memory after loading data: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
            // method call to write data in files
            writeDataInFile(p_dirPath, p_fileName, p_fileLabel, p_beingProcessedDate, p_fileEXT, p_maxFileLength, selectRst);
            _logger
                .debug(
                    METHOD_NAME,
                    "Memory after writing files: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (SQLException sqe) {
            _logger.error(METHOD_NAME, "SQLException " + sqe.getMessage());
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHUserBalanceMovement[fetchData]", "", "", "",
                "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("DWHUserBalanceMovement", METHOD_NAME, PretupsErrorCodesI.USRBALMOVT_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _logger.error(METHOD_NAME, "Exception : " + ex.getMessage());
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHUserBalanceMovement[fetchData]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException("DWHUserBalanceMovement", METHOD_NAME, PretupsErrorCodesI.USRBALMOVT_ERROR_EXCEPTION);
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
                _logger.debug(METHOD_NAME, "Exiting ");
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
     * @param rst
     *            ResultSet
     * @return void
     * @throws Exception
     */
    private static void writeDataInFile(String p_dirPath, String p_fileName, String p_fileLabel, Date p_beingProcessedDate, String p_fileEXT, long p_maxFileLength, ResultSet rst) throws BTSLBaseException {
        final String METHOD_NAME = "writeDataInFile";
        if (_logger.isDebugEnabled()) {
            _logger
                .debug(
                    METHOD_NAME,
                    " Entered:  p_dirPath=" + p_dirPath + " p_fileName=" + p_fileName + " p_fileLabel=" + p_fileLabel + " p_beingProcessedDate=" + p_beingProcessedDate + " p_fileEXT=" + p_fileEXT + " p_maxFileLength=" + p_maxFileLength);
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
            //
            out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));

            if ("Y".equalsIgnoreCase(Constants.getProperty("ADD_HEADER_FOOTER"))) {
                fileHeader = constructFileHeader(p_beingProcessedDate, fileNumber, p_fileLabel);
                out.write(fileHeader);
            }
            // traverse first resultset
            while (rst.next()) {
                fileData = rst.getString(1);
                out.write(fileData + "\n");
                recordsWrittenInFile++;
                if (recordsWrittenInFile >= p_maxFileLength) {
                    if ("Y".equalsIgnoreCase(Constants.getProperty("ADD_HEADER_FOOTER"))) {
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
                    if ("Y".equalsIgnoreCase(Constants.getProperty("ADD_HEADER_FOOTER"))) {
                        fileHeader = constructFileHeader(p_beingProcessedDate, fileNumber, p_fileLabel);
                        out.write(fileHeader);
                    }
                }
            }

            // if number of records are not zero then footer is appended as file
            // is deleted
            if (recordsWrittenInFile > 0) {
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
                	_logger.debug(METHOD_NAME, "File deleted successfully");
                }
                _fileNameLst.remove(_fileNameLst.size() - 1);
            }
        } catch (Exception e) {
            deleteAllFiles();
            _logger.debug(METHOD_NAME, "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHUserBalanceMovement[writeDataInFile]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("DWHUserBalanceMovement", METHOD_NAME, PretupsErrorCodesI.USRBALMOVT_ERROR_EXCEPTION);
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHUserBalanceMovement[markProcessStatusAsComplete]",
                "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("DWHUserBalanceMovement", METHOD_NAME, PretupsErrorCodesI.USRBALMOVT_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting: updateCount=" + updateCount);
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHUserBalanceMovement[deleteAllFiles]", "", "",
                    "", "Exception:" + e.getMessage());
                throw new BTSLBaseException("DWHUserBalanceMovement", METHOD_NAME, PretupsErrorCodesI.USRBALMOVT_ERROR_EXCEPTION);
            }
        }// end of for loop
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHUserBalanceMovement[deleteAllFiles]", "", "", "",
            " Message: DWHUserBalanceMovement process has found some error, so deleting all the files.");
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
    private static String constructFileHeader(Date p_beingProcessedDate, long p_fileNumber, String p_fileLabel) {
        final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
        final StringBuffer fileHeaderBuf = new StringBuffer("");
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
            parentDir.mkdirs();
        }
        p_beingProcessedDate = BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate);
        // child directory name includes a file name and being processed date,
        // month and year
        final String oldDirName = p_oldDirectoryPath + File.separator + p_processId + "_" + p_beingProcessedDate.toString().substring(8, 10) + p_beingProcessedDate.toString()
            .substring(5, 7) + p_beingProcessedDate.toString().substring(2, 4);
        final String newDirName = p_finalDirectoryPath + File.separator + p_processId + "_" + p_beingProcessedDate.toString().substring(8, 10) + p_beingProcessedDate
            .toString().substring(5, 7) + p_beingProcessedDate.toString().substring(2, 4);
        File oldDir = new File(oldDirName);
        File newDir = new File(newDirName);
        if (!newDir.exists()) {
            newDir.mkdirs();
        }

        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " newDirName=" + newDirName);
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHUserBalanceMovement[moveFilesToFinalDirectory]",
                "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("DWHUserBalanceMovement", "deleteAllFiles", PretupsErrorCodesI.USRBALMOVT_ERROR_EXCEPTION);
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

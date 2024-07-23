package com.selftopup.pretups.processes;

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

import com.selftopup.common.BTSLBaseException;
import com.selftopup.common.BTSLMessages;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.processes.businesslogic.ProcessBL;
import com.selftopup.pretups.processes.businesslogic.ProcessI;
import com.selftopup.pretups.processes.businesslogic.ProcessStatusDAO;
import com.selftopup.pretups.processes.businesslogic.ProcessStatusVO;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.ConfigServlet;
import com.selftopup.util.Constants;
import com.selftopup.util.OracleUtil;

public class SelfTopUpCDRCreation {
    private static String pgcdrFileLabelForTxns = null;
    private static String pgcdrFileNameForTxns = null;
    private static String pgcdrDirectoryPathAndName = null;
    private static String finalPgcdrDirectoryPath = null;
    private static String childDirectory = null;
    private static String fileEXT = null;
    private static long maxFileLength = 0;
    private static ArrayList fileNameLst = new ArrayList();
    private static ProcessStatusVO processStatusVO;
    private static ProcessBL processBL = null;
    private static Log logger = LogFactory.getLog(SelfTopUpCDRCreation.class.getName());

    public static void main(String arg[]) {
        try {
            if (arg.length != 2) {
                System.out.println("Usage : SelfTopUpCDRCreation [Constants file] [LogConfig file]");
                return;
            }
            File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
                System.out.println("SelfTopUpCDRCreation" + " Constants File Not Found .............");
                return;
            }
            File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                System.out.println("SelfTopUpCDRCreation" + " Logconfig File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        } catch (Exception e) {
            if (logger.isDebugEnabled())
                logger.debug("main", " Error in Loading Files ...........................: " + e.getMessage());
            e.printStackTrace();
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            process();
        } catch (BTSLBaseException be) {
            logger.error("main", "BTSLBaseException : " + be.getMessage());
            be.printStackTrace();
        } finally {
            ConfigServlet.destroyProcessCache();
        }
        if (logger.isDebugEnabled())
            logger.debug("main", "Exiting..... ");
    }

    /**
     * This method is the main method of this process,
     * which is responsible for the SelfTopUp CDR files creation.
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

        try {
            logger.debug("process", "Memory at statup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);
            loadConstantParameters();

            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (logger.isDebugEnabled())
                    logger.debug("process", " DATABASE Connection is NULL ");
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelfTopUpCDRCreation[process]", "", "", "", "DATABASE Connection is NULL");
                return;
            }
            processId = "SLFTPCDR";
            processBL = new ProcessBL();
            processStatusVO = processBL.checkProcessUnderProcess(con, processId);
            statusOk = processStatusVO.isStatusOkBool();
            beforeInterval = (int) processStatusVO.getBeforeInterval() / (60 * 24);
            if (statusOk) {
                con.commit();
                processedUpto = processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    if (processedUpto.compareTo(currentDate) == 0)
                        throw new BTSLBaseException("SelfTopUpCDRCreation", "process", SelfTopUpErrorCodesI.P2PDWH_PROCESS_ALREADY_EXECUTED_TILL_TODAY);
                    processedUpto = BTSLUtil.addDaysInUtilDate(processedUpto, 1);
                    for (dateCount = BTSLUtil.getSQLDateFromUtilDate(processedUpto); dateCount.before(BTSLUtil.addDaysInUtilDate(currentDate, -beforeInterval)); dateCount = BTSLUtil.addDaysInUtilDate(dateCount, 1)) {
                        if (!checkUnderprocessTransaction(con, dateCount)) {
                            childDirectory = createDirectory(pgcdrDirectoryPathAndName, processId, dateCount);
                            fetchPGReconciledData(con, dateCount, childDirectory, pgcdrFileNameForTxns, pgcdrFileLabelForTxns, fileEXT, maxFileLength);
                            processStatusVO.setExecutedUpto(dateCount);
                            processStatusVO.setExecutedOn(currentDate);
                            processStatusDAO = new ProcessStatusDAO();
                            maxDoneDateUpdateCount = processStatusDAO.updateProcessDetail(con, processStatusVO);

                            if (maxDoneDateUpdateCount > 0) {
                                moveFilesToFinalDirectory(pgcdrDirectoryPathAndName, finalPgcdrDirectoryPath, processId, dateCount);
                                con.commit();
                            } else {
                                if (fileNameLst.size() > 0)
                                    deleteAllFiles();
                                con.rollback();
                                throw new BTSLBaseException("SelfTopUpCDRCreation", "process", SelfTopUpErrorCodesI.P2PDWH_COULD_NOT_UPDATE_MAX_DONE_DATE);
                            }
                            Thread.sleep(500);
                        }
                    }
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "SelfTopUpCDRCreation[process]", "", "", "", " SelfTopUpCDRCreation process has been executed successfully.");
                } else
                    throw new BTSLBaseException("SelfTopUpCDRCreation", "process", SelfTopUpErrorCodesI.P2PDWH_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
            }
        } catch (BTSLBaseException be) {
            logger.error("process", "BTSLBaseException : " + be.getMessage());
            be.printStackTrace();
            throw be;
        } catch (Exception e) {
            try {
                if (fileNameLst.size() > 0)
                    deleteAllFiles();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            logger.error("process", "Exception : " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "SelfTopUpCDRCreation[process]", "", "", "", " SelfTopUpCDRCreation process could not be executed successfully.");
            throw new BTSLBaseException("SelfTopUpCDRCreation", "process", SelfTopUpErrorCodesI.P2PDWH_ERROR_EXCEPTION);
        } finally {
            if (statusOk) {
                try {
                    if (markProcessStatusAsComplete(con, processId) == 1)
                        try {
                            con.commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    else {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            if (fileNameLst.size() > 0)
                                deleteAllFiles();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (con != null)
                        con.close();
                } catch (Exception ex) {
                    if (logger.isDebugEnabled())
                        logger.debug("process", "Exception closing connection ");
                }
            }
            logger.debug("process", "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            if (logger.isDebugEnabled())
                logger.debug("process", "Exiting..... ");
        }
    }

    /**
     * This method is used to load all the required parameters defined in the
     * constant.props file which are used to creating the DWH files
     * 
     * @throws BTSLBaseException
     */
    private static void loadConstantParameters() throws BTSLBaseException {
        if (logger.isDebugEnabled())
            logger.debug("loadConstantParameters", " Entered: ");
        try {
            pgcdrFileLabelForTxns = Constants.getProperty("STU_TRANSACTION_FILE_LABEL");
            if (BTSLUtil.isNullString(pgcdrFileLabelForTxns))
                logger.error("loadConstantParameters", " Could not find file label for transaction data in the Constants file.");
            else
                logger.debug("loadConstantParameters", " _pgcdrFileLabelForTxns=" + pgcdrFileLabelForTxns);

            pgcdrFileNameForTxns = Constants.getProperty("STU_TRANSACTION_FILE_NAME");
            if (BTSLUtil.isNullString(pgcdrFileNameForTxns))
                logger.error("loadConstantParameters", " Could not find file name for transaction data in the Constants file.");
            else
                logger.debug("loadConstantParameters", " _pgcdrFileNameForTxns=" + pgcdrFileNameForTxns);

            pgcdrDirectoryPathAndName = Constants.getProperty("STU_DIRECTORY");
            if (BTSLUtil.isNullString(pgcdrDirectoryPathAndName))
                logger.error("loadConstantParameters", " Could not find directory path in the Constants file.");
            else
                logger.debug("loadConstantParameters", " _pgcdrDirectoryPathAndName=" + pgcdrDirectoryPathAndName);
            finalPgcdrDirectoryPath = Constants.getProperty("STU_FINAL_DIRECTORY");

            if (BTSLUtil.isNullString(finalPgcdrDirectoryPath))
                logger.error("loadConstantParameters", " Could not find final directory path in the Constants file.");
            else
                logger.debug("loadConstantParameters", " _finalPgcdrDirectoryPath=" + finalPgcdrDirectoryPath);

            if (BTSLUtil.isNullString(pgcdrFileLabelForTxns) || BTSLUtil.isNullString(pgcdrFileNameForTxns) || BTSLUtil.isNullString(pgcdrDirectoryPathAndName) || BTSLUtil.isNullString(finalPgcdrDirectoryPath))
                throw new BTSLBaseException("SelfTopUpCDRCreation", "loadConstantParameters", SelfTopUpErrorCodesI.P2PDWH_COULD_NOT_FIND_DATA_IN_CONSTANTS_FILE);
            try {
                fileEXT = Constants.getProperty("STU_FILE_EXT");
            } catch (Exception e) {
                fileEXT = ".csv";
            }
            logger.debug("loadConstantParameters", " _fileEXT=" + fileEXT);
            try {
                maxFileLength = Long.parseLong(Constants.getProperty("STU_MAX_FILE_LENGTH"));
            } catch (Exception e) {
                maxFileLength = 1000;
            }
            logger.debug("loadConstantParameters", " _maxFileLength=" + maxFileLength);
            logger.debug("loadConstantParameters", " Required information successfuly loaded from Constants.props...............: ");
        } catch (BTSLBaseException be) {
            logger.error("loadConstantParameters", "BTSLBaseException : " + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelfTopUpCDRCreation[loadConstantParameters]", "", "", "", "Message:" + be.getMessage());
            be.printStackTrace();
            throw be;
        } catch (Exception e) {
            logger.error("loadConstantParameters", "Exception : " + e.getMessage());
            e.printStackTrace();
            BTSLMessages btslMessage = new BTSLMessages(SelfTopUpErrorCodesI.P2PDWH_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelfTopUpCDRCreation[loadConstantParameters]", "", "", "", "Message:" + btslMessage);
            throw new BTSLBaseException("SelfTopUpCDRCreation", "loadConstantParameters", SelfTopUpErrorCodesI.P2PDWH_ERROR_EXCEPTION);
        } finally {
            if (logger.isDebugEnabled())
                logger.debug("loadConstantParameters", " Exiting.. ");
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
        if (logger.isDebugEnabled())
            logger.debug("checkUnderprocessTransaction", " Entered: p_beingProcessedDate=" + p_beingProcessedDate);
        PreparedStatement selectPstmt = null;
        ResultSet selectRst = null;
        boolean transactionFound = false;
        try {
            String selectQuery = new String("SELECT 1 FROM subscriber_transfers WHERE transfer_date=? AND transfer_status IN('205','250') ");
            if (logger.isDebugEnabled())
                logger.debug("checkUnderprocessTransaction", "select query:" + selectQuery);
            selectPstmt = p_con.prepareStatement(selectQuery);
            selectPstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate));
            selectRst = selectPstmt.executeQuery();
            if (selectRst.next()) {
                transactionFound = true;
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelfTopUpCDRCreation[checkUnderprocessTransaction]", "", "", "", "Message:SelfTopUpCDRCreation process cannot continue as underprocess and/or ambiguous transactions are found.");
                throw new BTSLBaseException("SelfTopUpCDRCreation", "checkUnderprocessTransaction", SelfTopUpErrorCodesI.DWH_AMB_OR_UP_TXN_FOUND);
            }
        } catch (BTSLBaseException be) {
            logger.error("checkUnderprocessTransaction", "BTSLBaseException : " + be.getMessage());
            be.printStackTrace();
            throw be;
        } catch (SQLException sqe) {
            logger.error("checkUnderprocessTransaction", "SQLException " + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelfTopUpCDRCreation[checkUnderprocessTransaction]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("SelfTopUpCDRCreation", "checkUnderprocessTransaction", SelfTopUpErrorCodesI.P2PDWH_ERROR_EXCEPTION);
        } catch (Exception ex) {
            logger.error("checkUnderprocessTransaction", "Exception : " + ex.getMessage());
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelfTopUpCDRCreation[checkUnderprocessTransaction]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("SelfTopUpCDRCreation", "checkUnderprocessTransaction", SelfTopUpErrorCodesI.P2PDWH_ERROR_EXCEPTION);
        } finally {
            if (selectRst != null)
                try {
                    selectRst.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            if (selectPstmt != null)
                try {
                    selectPstmt.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            if (logger.isDebugEnabled())
                logger.debug("checkUnderprocessTransaction", "Exiting transactionFound=" + transactionFound);
        }
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
        if (logger.isDebugEnabled())
            logger.debug("createDirectory", " Entered: p_directoryPathAndName=" + p_directoryPathAndName + " p_processId=" + p_processId + " p_beingProcessedDate=" + p_beingProcessedDate);

        File parentDir = null;
        File newDir = null;
        String dirName = null;
        try {
            parentDir = new File(p_directoryPathAndName);
            if (!parentDir.exists())
                parentDir.mkdirs();
            p_beingProcessedDate = BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate);
            dirName = p_directoryPathAndName + File.separator + p_processId + "_" + p_beingProcessedDate.toString().substring(8, 10) + p_beingProcessedDate.toString().substring(5, 7) + p_beingProcessedDate.toString().substring(2, 4);
            newDir = new File(dirName);
            if (!newDir.exists())
                newDir.mkdirs();
        } catch (Exception e) {
            logger.debug("createDirectory", "Exception: " + e.getMessage());
            e.printStackTrace();
            if (parentDir != null)
                parentDir = null;
            if (newDir != null)
                newDir = null;
            throw new BTSLBaseException("SelfTopUpCDRCreation", "createDirectory", SelfTopUpErrorCodesI.P2PDWH_ERROR_EXCEPTION);
        } finally {
            if (logger.isDebugEnabled())
                logger.debug("createDirectory", "Exiting dirName=" + dirName);
        }
        return dirName;
    }

    /**
     * This method will fetch all the required SelfTopUp transactions data from
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
    private static void fetchPGReconciledData(Connection p_con, Date p_beingProcessedDate, String p_dirPath, String p_fileName, String p_fileLabel, String p_fileEXT, long p_maxFileLength) throws BTSLBaseException {
        if (logger.isDebugEnabled())
            logger.debug("fetchPGReconciledData", " Entered: p_beingProcessedDate=" + p_beingProcessedDate + " p_dirPath=" + p_dirPath + " p_fileName=" + p_fileName + " p_fileLabel=" + p_fileLabel + " p_fileEXT=" + p_fileEXT + " p_maxFileLength=" + p_maxFileLength);

        StringBuffer stuQueryBuf = new StringBuffer();
        stuQueryBuf.append(" select 'CITYBANK,'||ti.transfer_id||','||ti.reference_id||',NA,'||ti.transfer_date||','||ti.transfer_value||','|| ");
        stuQueryBuf.append(" ti.transfer_value from subscriber_transfers st, transfer_items ti, transfer_items ti2 where st.transfer_id=ti.transfer_id ");
        stuQueryBuf.append(" and st.transfer_id=ti2.transfer_id and ti.user_type=? and ti2.user_type=? and ti2.transfer_type=? ");
        stuQueryBuf.append(" and ti.transfer_type=? and st.reconciliation_date between ? and ?");
        String stuSelectQuery = stuQueryBuf.toString();
        if (logger.isDebugEnabled())
            logger.debug("fetchPGReconciledData", "SelfTopUp select query:" + stuSelectQuery);
        PreparedStatement stuSelectPstmt = null;
        ResultSet stuSelectRst = null;

        try {
            stuSelectPstmt = p_con.prepareStatement(stuSelectQuery);
            stuSelectPstmt.setString(1, "SENDER");
            stuSelectPstmt.setString(2, "SENDER");
            stuSelectPstmt.setString(3, "RECON");
            stuSelectPstmt.setString(4, "TXN");
            stuSelectPstmt.setDate(5, BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate));
            stuSelectPstmt.setDate(6, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(p_beingProcessedDate, 1)));
            stuSelectRst = stuSelectPstmt.executeQuery();
            logger.debug("fetchPGReconciledData", "Memory after loading transaction data: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate + "abc " + BTSLUtil.addDaysInUtilDate(p_beingProcessedDate, 1));

            writeDataInFile(p_dirPath, p_fileName, p_fileLabel, p_beingProcessedDate, p_fileEXT, p_maxFileLength, stuSelectRst);
            logger.debug("fetchPGReconciledData", "Memory after writing transaction files: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
        } catch (BTSLBaseException be) {
            logger.error("fetchPGReconciledData", "BTSLBaseException : " + be.getMessage());
            be.printStackTrace();
            throw be;
        } catch (SQLException sqe) {
            logger.error("fetchPGReconciledData", "SQLException " + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelfTopUpCDRCreation[fetchPGReconciledData]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("SelfTopUpCDRCreation", "fetchPGReconciledData", SelfTopUpErrorCodesI.P2PDWH_ERROR_EXCEPTION);
        } catch (Exception ex) {
            logger.error("fetchPGReconciledData", "Exception : " + ex.getMessage());
            ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelfTopUpCDRCreation[fetchPGReconciledData]", "", "", "", "SQLException:" + ex.getMessage());
            throw new BTSLBaseException("SelfTopUpCDRCreation", "fetchPGReconciledData", SelfTopUpErrorCodesI.P2PDWH_ERROR_EXCEPTION);
        } finally {
            if (stuSelectRst != null)
                try {
                    stuSelectRst.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            if (stuSelectPstmt != null)
                try {
                    stuSelectPstmt.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            if (logger.isDebugEnabled())
                logger.debug("fetchPGReconciledData", "Exiting ");
        }
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
     * @param rst
     *            ResultSet
     * @return void
     * @throws BTSLBaseException
     */
    private static void writeDataInFile(String p_dirPath, String p_fileName, String p_fileLabel, Date p_beingProcessedDate, String p_fileEXT, long p_maxFileLength, ResultSet rst) throws BTSLBaseException {
        if (logger.isDebugEnabled())
            logger.debug("writeDataInFile", " Entered:  p_dirPath=" + p_dirPath + " p_fileName=" + p_fileName + " p_fileLabel=" + p_fileLabel + " p_beingProcessedDate=" + p_beingProcessedDate + " p_fileEXT=" + p_fileEXT + " p_maxFileLength=" + p_maxFileLength + "p_rst=" + rst);
        long recordsWrittenInFile = 0;
        PrintWriter out = null;
        int fileNumber = 0;
        String fileName = null;
        File newFile = null;
        String fileData = null;
        String fileHeader = null;
        String fileFooter = null;

        try {
            SimpleDateFormat sdf = null;
            try {
                sdf = new SimpleDateFormat("ddMMyyyy");
            } catch (Exception e) {
                sdf = new SimpleDateFormat("ddMMyyyy");
            }
            fileNumber = 1;
            if (Integer.toString(fileNumber).length() == 1)
                fileName = p_dirPath + File.separator + p_fileName + "00" + fileNumber + "." + sdf.format(p_beingProcessedDate) + p_fileEXT;
            else if (Integer.toString(fileNumber).length() == 2)
                fileName = p_dirPath + File.separator + p_fileName + "0" + fileNumber + "." + sdf.format(p_beingProcessedDate) + p_fileEXT;
            else if (Integer.toString(fileNumber).length() == 3)
                fileName = p_dirPath + File.separator + p_fileName + fileNumber + "." + sdf.format(p_beingProcessedDate) + p_fileEXT;

            logger.debug("writeDataInFile", "  fileName=" + fileName);

            newFile = new File(fileName);
            fileNameLst.add(fileName);
            out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
            if ("Y".equalsIgnoreCase(Constants.getProperty("ADD_HEADER_FOOTER"))) {
                fileHeader = constructFileHeader(p_beingProcessedDate, fileNumber, p_fileLabel);
                out.write(fileHeader);
            }
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
                    out.close();

                    if (Integer.toString(fileNumber).length() == 1)
                        fileName = p_dirPath + File.separator + p_fileName + "00" + fileNumber + "." + sdf.format(p_beingProcessedDate) + p_fileEXT;
                    else if (Integer.toString(fileNumber).length() == 2)
                        fileName = p_dirPath + File.separator + p_fileName + "0" + fileNumber + "." + sdf.format(p_beingProcessedDate) + p_fileEXT;
                    else if (Integer.toString(fileNumber).length() == 3)
                        fileName = p_dirPath + File.separator + p_fileName + fileNumber + "." + sdf.format(p_beingProcessedDate) + p_fileEXT;

                    logger.debug("writeDataInFile", "  fileName=" + fileName);
                    newFile = new File(fileName);
                    fileNameLst.add(fileName);
                    out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
                    if ("Y".equalsIgnoreCase(Constants.getProperty("ADD_HEADER_FOOTER"))) {
                        fileHeader = constructFileHeader(p_beingProcessedDate, fileNumber, p_fileLabel);
                        out.write(fileHeader);
                    }
                }
            }

            if (recordsWrittenInFile > 0) {
                if ("Y".equalsIgnoreCase(Constants.getProperty("ADD_HEADER_FOOTER"))) {
                    fileFooter = constructFileFooter(recordsWrittenInFile);
                    out.write(fileFooter);
                }
            } else {
                if (out != null)
                    out.close();
                newFile.delete();
                fileNameLst.remove(fileNameLst.size() - 1);
            }
            if (out != null)
                out.close();
        } catch (Exception e) {
            deleteAllFiles();
            logger.debug("writeDataInFile", "Exception: " + e.getMessage());
            e.printStackTrace();
            if (newFile != null)
                newFile = null;
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelfTopUpCDRCreation[writeDataInFile]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("SelfTopUpCDRCreation", "writeDataInFile", SelfTopUpErrorCodesI.P2PDWH_ERROR_EXCEPTION);
        } finally {
            if (out != null)
                out.close();
            if (logger.isDebugEnabled())
                logger.debug("writeDataInFile", "Exiting ");
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
        if (logger.isDebugEnabled())
            logger.debug("markProcessStatusAsComplete", " Entered:  p_processId:" + p_processId);
        int updateCount = 0;
        Date currentDate = new Date();
        ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        processStatusVO.setProcessID(p_processId);
        processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        processStatusVO.setStartDate(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetail(p_con, processStatusVO);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("markProcessStatusAsComplete", "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelfTopUpCDRCreation[markProcessStatusAsComplete]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("SelfTopUpCDRCreation", "markProcessStatusAsComplete", SelfTopUpErrorCodesI.P2PDWH_ERROR_EXCEPTION);
        } finally {
            if (logger.isDebugEnabled())
                logger.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
        }
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
        if (logger.isDebugEnabled())
            logger.debug("deleteAllFiles", " Entered: ");
        int size = 0;
        if (fileNameLst != null)
            size = fileNameLst.size();
        if (logger.isDebugEnabled())
            logger.debug("deleteAllFiles", " : Number of files to be deleted " + size);
        String fileName = null;
        File newFile = null;
        for (int i = 0; i < size; i++) {
            try {
                fileName = (String) fileNameLst.get(i);
                newFile = new File(fileName);
                if (newFile != null) {
                    newFile.delete();
                    if (logger.isDebugEnabled())
                        logger.debug("", fileName + " file deleted");
                } else {
                    if (logger.isDebugEnabled())
                        logger.debug("", fileName + " is null");
                }
            } catch (Exception e) {
                logger.error("deleteAllFiles", "Exception " + e.getMessage());
                e.printStackTrace();
                if (newFile != null)
                    newFile = null;
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelfTopUpCDRCreation[deleteAllFiles]", "", "", "", "Exception:" + e.getMessage());
                throw new BTSLBaseException("SelfTopUpCDRCreation", "deleteAllFiles", SelfTopUpErrorCodesI.P2PDWH_ERROR_EXCEPTION);
            }
        }// end of for loop
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelfTopUpCDRCreation[deleteAllFiles]", "", "", "", " Message: SelfTopUpCDRCreation process has found some error, so deleting all the files.");
        if (fileNameLst.isEmpty())
            fileNameLst.clear();
        if (logger.isDebugEnabled())
            logger.debug("deleteAllFiles", " : Exiting.............................");
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
        if (logger.isDebugEnabled())
            logger.debug("constructFileHeader", " Entered: ");
        StringBuffer fileHeaderBuf = null;
        try {
            fileHeaderBuf = new StringBuffer(p_fileLabel);
            fileHeaderBuf.append("\n");
        } catch (Exception e) {
            logger.debug("constructFileHeader", "Exception: " + e.getMessage());
            e.printStackTrace();
            throw new BTSLBaseException("SelfTopUpCDRCreation", "constructFileHeader", SelfTopUpErrorCodesI.P2PDWH_ERROR_EXCEPTION);
        } finally {
            if (logger.isDebugEnabled())
                logger.debug("constructFileHeader", "Exiting: fileHeaderBuf.toString()=" + fileHeaderBuf.toString());
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
        if (logger.isDebugEnabled())
            logger.debug("constructFileFooter", " Entered: ");
        StringBuffer fileFooterBuf = null;
        try {
            fileFooterBuf = new StringBuffer("");
            fileFooterBuf.append(" Records count=" + p_noOfRecords + "\n");
        } catch (Exception e) {
            logger.debug("constructFileHeader", "Exception: " + e.getMessage());
            e.printStackTrace();
            throw new BTSLBaseException("SelfTopUpCDRCreation", "constructFileFooter", SelfTopUpErrorCodesI.P2PDWH_ERROR_EXCEPTION);
        } finally {
            if (logger.isDebugEnabled())
                logger.debug("constructFileFooter", "Exiting: fileHeaderBuf.toString()=" + fileFooterBuf.toString());
        }
        return fileFooterBuf.toString();
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
     * @return void
     */
    private static void moveFilesToFinalDirectory(String p_oldDirectoryPath, String p_finalDirectoryPath, String p_processId, Date p_beingProcessedDate) throws BTSLBaseException {
        if (logger.isDebugEnabled())
            logger.debug("moveFilesToFinalDirectory", " Entered: p_oldDirectoryPath=" + p_oldDirectoryPath + " p_finalDirectoryPath=" + p_finalDirectoryPath + " p_processId=" + p_processId + " p_beingProcessedDate=" + p_beingProcessedDate);

        String oldFileName = null;
        String newFileName = null;
        File oldFile = null;
        File newFile = null;
        File parentDir = new File(p_finalDirectoryPath);
        if (!parentDir.exists())
            parentDir.mkdir();
        p_beingProcessedDate = BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate);
        // child directory name includes a file name and being processed date,
        // month and year
        String oldDirName = p_oldDirectoryPath + File.separator + p_processId + "_" + p_beingProcessedDate.toString().substring(8, 10) + p_beingProcessedDate.toString().substring(5, 7) + p_beingProcessedDate.toString().substring(2, 4);
        String newDirName = p_finalDirectoryPath + File.separator + p_processId + "_" + p_beingProcessedDate.toString().substring(8, 10) + p_beingProcessedDate.toString().substring(5, 7) + p_beingProcessedDate.toString().substring(2, 4);
        File oldDir = new File(oldDirName);
        File newDir = new File(newDirName);
        if (!newDir.exists())
            newDir.mkdir();
        if (logger.isDebugEnabled())
            logger.debug("moveFilesToFinalDirectory", " dirName=" + newDirName);

        int size = fileNameLst.size();
        try {
            for (int i = 0; i < size; i++) {
                oldFileName = (String) fileNameLst.get(i);
                oldFile = new File(oldFileName);
                newFileName = oldFileName.replace(p_oldDirectoryPath, p_finalDirectoryPath);
                newFile = new File(newFileName);
                if (oldFile != null) {
                    oldFile.renameTo(newFile);
                    if (logger.isDebugEnabled())
                        logger.debug("moveFilesToFinalDirectory", " File " + oldFileName + " is moved to " + newFileName);
                } else {
                    if (logger.isDebugEnabled())
                        logger.debug("moveFilesToFinalDirectory", " File" + oldFileName + " is null");
                }
            }
            fileNameLst.clear();
            if (oldDir.exists())
                oldDir.delete();
            logger.debug("moveFilesToFinalDirectory", " File " + oldFileName + " is moved to " + newFileName);
        } catch (Exception e) {
            logger.error("moveFilesToFinalDirectory", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SelfTopUpCDRCreation[moveFilesToFinalDirectory]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("SelfTopUpCDRCreation", "moveFilesToFinalDirectory", SelfTopUpErrorCodesI.P2PDWH_ERROR_EXCEPTION);
        } finally {
            if (oldFile != null)
                oldFile = null;
            if (newFile != null)
                newFile = null;
            if (parentDir != null)
                parentDir = null;
            if (newDir != null)
                newDir = null;
            if (oldDir != null)
                oldDir = null;
            if (logger.isDebugEnabled())
                logger.debug("moveFilesToFinalDirectory", "Exiting.. ");
        }
    }
}

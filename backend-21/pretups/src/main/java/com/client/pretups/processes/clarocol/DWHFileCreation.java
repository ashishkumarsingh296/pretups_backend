package com.client.pretups.processes.clarocol;

/**
 * @(#)DWHFileCreation
 *                     Copyright(c) 2006, Bharti Telesoft Ltd.
 *                     All Rights Reserved
 * 
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Author Date History
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Ankit Singhal 8/05/2006 Initial Creation
 *                     Sandeep Goel Aug 04, 2006 Modification ID DWH001
 *                     Ankit Singhal Sep 09, 2006 Modification ID DWH002
 *                     Sanjeev Nov 29,2007 Modification ID DWH003
 *                     Vikas Jauhari Feb 10, 2011 Modification ID DWH004
 *                     Sanjay Kumar Bind1 Sep 30, 2016 Modification ID DWH005
 * 
 *                     Change Description: In this process, process flow is
 *                     changed, we have used procedure named as
 *                     'Rp2pdwhtempprc' to move slected data into temporary
 *                     table then we have fetched required DWH
 *                     data from temp tables to generate .csv files.
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

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
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.DateSorting;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

public class DWHFileCreation {
    private static String dwhFileLabelForTransaction = null;
    private static String dwhFileSeparator= null;
    private static String dwhFileLabelForMaster = null;
    private static String dwhFileNameForTransaction = null;
    private static String dwhFileNameForMaster = null;
    private static String masterDirectoryPathAndName = null;
    private static String finalMasterDirectoryPath = null;
    private static String childDirectory = null;
    private static String fileEXT = null;
    
    // Added By Sanjay on 30-Sep-2016
    private static String ctlFileEXT = null;
    private static long maxFileLength = 0;
    private static ArrayList fileNameLst = new ArrayList();
    private static ProcessStatusVO processStatusVO;
    private static ProcessBL processBL = null;
    // Option for generating DWH if there is Ambiguous or Under Process Txn.
    private static boolean ambiUnderTxnFound = false;
    private static boolean ambiUnderCheckRequired = true;
    private static String bonusBundleDefaultValues = null;
    // added by Akanksha
    private static String dwhFileLabelForMasterChannelUser = null;
    private static String dwhFileNameForMasterChannelUser = null;
    // 04-MAR-2014 for OCI client
    protected static HashMap<String, Long> fileNameMap = null;
    protected static TreeMap<String, Object> fileRecordMap = null;
    
    
    // Added By Sanjay on 30-Sep-2016
    static final String ADDHEADERFOOTER = "ADD_HEADER_FOOTER";
    static final String PFILEEXT = "pFileExt";
    static final String PCTLFILEEXT = "pCtlFileExt";
    static final String PFILELABEL = " pFileLabel=";
    static final String PFILENUMBER = " pFileNumber=";
    static final String PFILENAME = " pFileName=";
    static final String PDIRPATH = " pDirPath=";
    static final String FORDATE = " for date:";
    static final String FILENAME = "  fileName=";
    static final String CALL = "{call ";
    static final String FREE = "Free: ";
    static final String FILELABELMSG = " Could not find file label for master data in the Constants file.";
    static final String WRITEDATAINFILE = "writeDataInFile";
    static final String CLASSNAME = "DWHFileCreation";
    
    
    // Ended Here
    
    private static Log logger = LogFactory.getLog(DWHFileCreation.class.getName());

    public static void main(String arg[]) {
        final String methodName = "main";
        Date date = null;
        try {
            // ID DWH003 third argument is to generate DWH even there is
            // Ambiguous or Under Process Txn.
            if (arg.length < 2) {
                System.out.println("Usage : DWHFileCreation [Constants file] [LogConfig file]");
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

            // if third argument is not set then try to set it with Default
            try {
                if (arg.length == 4) {
                    final String inputDate = arg[3];
                    if (!BTSLUtil.isNullString(inputDate)) {
                        date = BTSLUtil.getDateFromDateString(inputDate, PretupsI.DATE_FORMAT_DDMMYYYY);
                        if (!date.before(BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(new Date())))) {
                            System.out.println(CLASSNAME + " process will execute only for current or previous date ");
                            return;

                        }
                        System.out.println(CLASSNAME + " DWHFileCreation process is going to execute only till.............date:" + date);

                        System.out.println(CLASSNAME + " DWHFileCreation process is going to execute only for.............date:" + date);
                    }

                }
                if (arg.length >= 3) {
                    if (PretupsI.YES.equalsIgnoreCase(arg[2])) {
                        ambiUnderCheckRequired = true;
                    } else {
                        ambiUnderCheckRequired = false;
                    }
                }
            } catch (Exception e) {
                ambiUnderCheckRequired = true;
                logger.errorTrace(methodName, e);
            }

            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            bonusBundleDefaultValues = Constants.getProperty("BONUS_BUNDLE_DEFAULT_VAL");
            if (BTSLUtil.isNullString(bonusBundleDefaultValues)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[main]", "", "", "",
                    "BONUS_BUNDLE_DEFAULT_VAL is not defined in Constants.props");
            }

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
        Date dateCount = null;
        Date currentDate = new Date();
        Connection con = null;
        String processId = null;
        boolean statusOk = false;
        ProcessStatusDAO processStatusDAO = null;
        int beforeInterval = 0;
        CallableStatement cstmt = null;

        int maxDoneDateUpdateCount = 0;
        String dwhProcName = null;
        final String methodName = "process";
        try {
            // 04-MAR-2014 for OCI client
            fileNameMap = new HashMap<>();
            fileRecordMap = new TreeMap<>(new DateSorting());
            // Ended Here

            logger.debug(methodName, "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + FREE + Runtime.getRuntime().freeMemory() / 1049576);
            currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);
            // getting all the required parameters from Constants.props
            loadConstantParameters();

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
            processId = ProcessI.DWH_PROCESSID;
            // method call to check status of the process
            processBL = new ProcessBL();
            processStatusVO = processBL.checkProcessUnderProcess(con, processId);
            statusOk = processStatusVO.isStatusOkBool();
            beforeInterval = (int) processStatusVO.getBeforeInterval() / (60 * 24);
            if (pDate == null && statusOk) {
                con.commit();
                // method call to find maximum date till which process has been
                // executed
                processedUpto = processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    // ID DWH002 to check whether process has been executed till
                    // current date or not
                    if (processedUpto.compareTo(currentDate) == 0) {
                        throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_PROCESS_ALREADY_EXECUTED_TILL_TODAY);
                    }
                    // adding 1 in processed upto dtae as we have to start from
                    // the next day till which process has been executed
                    processedUpto = BTSLUtil.addDaysInUtilDate(processedUpto, 1);
                    // loop to be started for each date
                    // the loop starts from the date till which process has been
                    // executed and executes one day before current date
                    for (dateCount = BTSLUtil.getSQLDateFromUtilDate(processedUpto); dateCount.before(BTSLUtil.addDaysInUtilDate(currentDate, -beforeInterval)); dateCount =  BTSLUtil.getSQLDateFromUtilDate(BTSLUtil
                        .addDaysInUtilDate(dateCount, 1))) {                    	
                        dwhProcName = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.RP2PDWH_OPT_SPECIFIC_PROC_NAME));
                        if (!(BTSLUtil.isNullString(dwhProcName) || "".equals(dwhProcName))) {
                            if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                                final String prepareCall = CALL + Constants.getProperty("currentschema") + "." + dwhProcName + "(?,?,?,?,?)}";
                                cstmt = con.prepareCall(prepareCall);
                            } else {
                                final String prepareCall2 = CALL + dwhProcName + "(?,?,?,?,?)}";
                                cstmt = con.prepareCall(prepareCall2);
                            }
                        } else {
                            if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                                cstmt = con.prepareCall(CALL + Constants.getProperty("currentschema") + ".Rp2pdwhtempprc(?,?,?,?,?)}");
                            } else {
                                cstmt = con.prepareCall("{call Rp2pdwhtempprc(?,?,?,?,?)}");
                            }
                        }
                        cstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(dateCount));
                        cstmt.registerOutParameter(2, Types.NUMERIC); // Count
                        // of
                        // Master
                        cstmt.registerOutParameter(3, Types.NUMERIC); // Count
                        // for
                        // Channel
                        // Transaction
                        cstmt.registerOutParameter(4, Types.NUMERIC); // Count
                        // for C2S
                        // Transaction
                        cstmt.registerOutParameter(5, Types.VARCHAR); // Status
                        if (logger.isDebugEnabled()) {
                            logger.debug(methodName, "Before Exceuting Procedure");
                        }
                        cstmt.executeUpdate();
                        if (logger.isDebugEnabled()) {
                            logger.debug(methodName, "After Exceuting Procedure");
                        }
                        final long masterCount = cstmt.getLong(2);
                        final long chTransCount = cstmt.getLong(3);
                        final long c2sTransCount = cstmt.getLong(4);
                        final String isSuccess = cstmt.getString(5);

                        if (!"SUCCESS".equals(isSuccess)) {
                            throw new BTSLBaseException(CLASSNAME, methodName, "Procedure Execution Fail");
                        }
                        if (ambiUnderCheckRequired) {
                            ambiUnderTxnFound = checkUnderprocessTransaction(con, dateCount);
                        }
                        if (!ambiUnderTxnFound) {
                            // method call to create master directory and child
                            // directory if does not exist
                            childDirectory = createDirectory(masterDirectoryPathAndName, processId, dateCount);
                            // method call to fetch Channel transaction data and
                            // write it in files
                            int fileNumber = 1;
                            for (long i = 0, j = maxFileLength; i < chTransCount; i += maxFileLength) {
                                fetchChannelTransactionData(con, dateCount, childDirectory, dwhFileNameForTransaction, dwhFileLabelForTransaction, fileEXT, i, j,
                                    fileNumber);
                                fileNumber++;
                                if ((j + maxFileLength) < chTransCount) {
                                    j += maxFileLength;
                                } else if (j != chTransCount) {
                                    j = chTransCount;
                                }
                            }
                            // method call to fetch C2S transaction data and
                            // write it in files
                            for (long i = 0, j = maxFileLength; i < c2sTransCount; i += maxFileLength) {
                                fetchC2STransactionData(con, dateCount, childDirectory, dwhFileNameForTransaction, dwhFileLabelForTransaction, fileEXT, i, j, fileNumber);
                                fileNumber++;
                                if ((j + maxFileLength) < c2sTransCount) {
                                    j += maxFileLength;
                                } else if (j != c2sTransCount) {
                                    j = c2sTransCount;
                                }
                            }
                            // method call to fetch master data and write it in
                            // files
                            int fileNumber3 = 1;
                            for (long n = 0, m = maxFileLength; n < masterCount; n += maxFileLength) {

                                fetchMasterData(con, dateCount, childDirectory, dwhFileNameForMaster, dwhFileLabelForMaster, fileEXT, n, m, fileNumber3);
                                fileNumber3++;
                                if ((m + maxFileLength) < masterCount) {
                                    m += maxFileLength;
                                } else if (m != masterCount) {
                                    m = masterCount;
                                }

                            }
                            if (Constants.getProperty("CHANNEL_USER_MASTER_ALLOWED").equals(PretupsI.YES)) {
                                int fileNumber4 = 1;
                                for (long n = 0, m = maxFileLength; n < masterCount; n += maxFileLength) {
                                    fetchMasterDataForChannelUser(con, dateCount, childDirectory, dwhFileNameForMasterChannelUser, dwhFileLabelForMasterChannelUser,
                                        fileEXT, n, m, fileNumber4);
                                    fileNumber4++;
                                    if ((m + maxFileLength) < masterCount) {
                                        m += maxFileLength;
                                    } else if (m != masterCount) {
                                        m = masterCount;
                                    }
                                }
                            }
                            // method call to update maximum date till which
                            // process has been executed
                            processStatusVO.setExecutedUpto(dateCount);
                            processStatusVO.setExecutedOn(currentDate);
                            processStatusDAO = new ProcessStatusDAO();
                            maxDoneDateUpdateCount = processStatusDAO.updateProcessDetail(con, processStatusVO);

                            // if the process is successful, transaction is
                            // commit, else rollback
                            if (maxDoneDateUpdateCount > 0) {
                                moveFilesToFinalDirectory(masterDirectoryPathAndName, finalMasterDirectoryPath, processId, dateCount);
                                con.commit();
                            } else {
                                deleteAllFiles();
                                con.rollback();
                                throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_COULD_NOT_UPDATE_MAX_DONE_DATE);
                            }
                            // DWH002 sleep has been added after processing
                            // records of one day
                            Thread.sleep(500);
                        }// end if
                    }// end loop
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, "", "", "",
                        " DWHFileCreation process has been executed successfully.");
                }
                // ID DWH002 to avoid the null pointer exception thrown, in case
                // processesUpto is null
                else {
                    throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
                }
            }
            else {
                // in order not to update the status of tabel for this processID
                con.rollback();
                if (ambiUnderCheckRequired) {
                    ambiUnderTxnFound = checkUnderprocessTransaction(con, dateCount);
                }
                if (!ambiUnderTxnFound) {
                    // method call to create master directory and child
                    // directory if does not exist
                    childDirectory = createDirectory(masterDirectoryPathAndName, processId, pDate);
                    // cstmt =
                    dwhProcName = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.RP2PDWH_OPT_SPECIFIC_PROC_NAME));
                    if (!(BTSLUtil.isNullString(dwhProcName) || "".equals(dwhProcName))) {
                        if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                            final String prepCall = CALL + Constants.getProperty("currentschema") + "." + dwhProcName + "(?,?,?,?,?)}";
                            cstmt = con.prepareCall(prepCall);
                        } else {
                            final String prepCall2 = CALL + dwhProcName + "(?,?,?,?,?)}";
                            cstmt = con.prepareCall(prepCall2);
                        }
                    } else {
                        if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                            cstmt = con.prepareCall(CALL + Constants.getProperty("currentschema") + ".Rp2pdwhtempprc(?,?,?,?,?)}");
                        } else {
                            cstmt = con.prepareCall("{call Rp2pdwhtempprc(?,?,?,?,?)}");
                        }
                    }
                    cstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(pDate));
                    cstmt.registerOutParameter(2, Types.NUMERIC); // Count of
                    // Master
                    cstmt.registerOutParameter(3, Types.NUMERIC); // Count for
                    // Channel
                    // Transaction
                    cstmt.registerOutParameter(4, Types.NUMERIC); // Count for
                    // C2S
                    // Transaction
                    cstmt.registerOutParameter(5, Types.VARCHAR); // Status

                    if (logger.isDebugEnabled()) {
                        logger.debug(methodName, "Before Exceuting Procedure");
                    }
                    cstmt.executeUpdate();
                    if (logger.isDebugEnabled()) {
                        logger.debug(methodName, "After Exceuting Procedure");
                    }

                    final long masterCount = cstmt.getLong(2);
                    final long chTransCount = cstmt.getLong(3);
                    final long c2sTransCount = cstmt.getLong(4);
                    final String isSuccess = cstmt.getString(5);

                    if (!"SUCCESS".equals(isSuccess)) {
                        throw new BTSLBaseException(CLASSNAME, methodName, "Procedure Execution Fail");
                    }
                    // method call to fetch Channel transaction data and write
                    // it in files
                    int fileNumber = 1;
                    for (long i = 0, j = maxFileLength; i < chTransCount; i += maxFileLength) {
                        fetchChannelTransactionData(con, pDate, childDirectory, dwhFileNameForTransaction, dwhFileLabelForTransaction, fileEXT, i, j, fileNumber);
                        fileNumber++;
                        if ((j + maxFileLength) < chTransCount) {
                            j += maxFileLength;
                        } else if (j != chTransCount) {
                            j = chTransCount;
                        }
                    }
                    // method call to fetch C2S transaction data and write it in
                    // files
                    for (long i = 0, j = maxFileLength; i < c2sTransCount; i += maxFileLength) {
                        fetchC2STransactionData(con, pDate, childDirectory, dwhFileNameForTransaction, dwhFileLabelForTransaction, fileEXT, i, j, fileNumber);
                        fileNumber++;
                        if ((j + maxFileLength) < c2sTransCount) {
                            j += maxFileLength;
                        } else if (j != c2sTransCount) {
                            j = c2sTransCount;
                        }
                    }

                    // method call to fetch master data and write it in files
                    int fileNumber3 = 1;
                    for (long n = 0, m = maxFileLength; n < masterCount; n += maxFileLength) {

                        fetchMasterData(con, pDate, childDirectory, dwhFileNameForMaster, dwhFileLabelForMaster, fileEXT, n, m, fileNumber3);
                        fileNumber3++;
                        if ((m + maxFileLength) < masterCount) {
                            m += maxFileLength;
                        } else if (m != masterCount) {
                            m = masterCount;
                        }
                    }
                    // if the process is successful, transaction is commit, else
                    // rollback
                    moveFilesToFinalDirectory(masterDirectoryPathAndName, finalMasterDirectoryPath, processId, pDate);
                    con.commit();
                }
            }
        }// end of try
        catch (BTSLBaseException be) {
            logger.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
            logger.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            if (!fileNameLst.isEmpty()) {
                deleteAllFiles();
            }
            logger.error(methodName, PretupsI.EXCEPTION + e.getMessage());
            logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, "", "", "",
                " DWHFileCreation process could not be executed successfully.");
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            // 04-MAR-2014 for OCI client

            // if the status was marked as under process by this method call,
            // only then it is marked as complete on termination
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

    private static void loadConstantParameters() throws BTSLBaseException {
        final String methodName = "loadConstantParameters";
        if (logger.isDebugEnabled()) {
            logger.debug("loadParameters", " Entered: ");
        }
        try {
            dwhFileLabelForTransaction = Constants.getProperty("DWH_TRANSACTION_FILE_LABEL");
            if (BTSLUtil.isNullString(dwhFileLabelForTransaction)) {
                logger.error(methodName, " Could not find file label for transaction data in the Constants file.");
            } else {
                logger.debug("main", " dwhFileLabelForTransaction=" + dwhFileLabelForTransaction);
            }
            dwhFileSeparator = Constants.getProperty("DWH_FILE_SEPARATOR");
            if (BTSLUtil.isNullString(dwhFileLabelForTransaction)) {
                logger.error(methodName, " Could not find file dwhFileSeparator in the Constants file.");
            } else {
                logger.debug("main", " dwhFileSeparator=" + dwhFileSeparator);
            }
            dwhFileLabelForMaster = Constants.getProperty("DWH_MASTER_FILE_LABEL");
            if (BTSLUtil.isNullString(dwhFileLabelForMaster)) {
                logger.error(methodName, FILELABELMSG);
            } else {
                logger.debug(methodName, " dwhFileLabelForMaster=" + dwhFileLabelForMaster);
            }
            dwhFileNameForTransaction = Constants.getProperty("DWH_TRANSACTION_FILE_NAME");
            if (BTSLUtil.isNullString(dwhFileNameForTransaction)) {
                logger.error(methodName, " Could not find file name for transaction data in the Constants file.");
            } else {
                logger.debug(methodName, " dwhFileNameForTransaction=" + dwhFileNameForTransaction);
            }
            dwhFileNameForMaster = Constants.getProperty("DWH_MASTER_FILE_NAME");
            if (BTSLUtil.isNullString(dwhFileNameForMaster)) {
                logger.error(methodName, " Could not find file name for master data in the Constants file.");
            } else {
                logger.debug(methodName, " dwhFileNameForMaster=" + dwhFileNameForMaster);
            }

            masterDirectoryPathAndName = Constants.getProperty("DWH_MASTER_DIRECTORY");
            if (BTSLUtil.isNullString(masterDirectoryPathAndName)) {
                logger.error(methodName, " Could not find directory path in the Constants file.");
            } else {
                logger.debug(methodName, " masterDirectoryPathAndName=" + masterDirectoryPathAndName);
            }
            finalMasterDirectoryPath = Constants.getProperty("DWH_FINAL_DIRECTORY");
            if (BTSLUtil.isNullString(finalMasterDirectoryPath)) {
                logger.error(methodName, " Could not find final directory path in the Constants file.");
            } else {
                logger.debug(methodName, " finalMasterDirectoryPath=" + finalMasterDirectoryPath);
            }
            if (Constants.getProperty("CHANNEL_USER_MASTER_ALLOWED").equals(PretupsI.YES)) {
                dwhFileLabelForMasterChannelUser = Constants.getProperty("DWH_MASTER_CHANNELUSER_FILE_LABEL");
                if (BTSLUtil.isNullString(dwhFileLabelForMasterChannelUser)) {
                    logger.error(methodName, FILELABELMSG);
                } else {
                    logger.debug(methodName, " dwhFileLabelForMaster=" + dwhFileLabelForMasterChannelUser);
                    // checking that none of the required parameters should be
                    // null
                }

                dwhFileNameForMasterChannelUser = Constants.getProperty("DWH_MASTER_CHANNELUSER_FILE_NAME");
                if (BTSLUtil.isNullString(dwhFileNameForMasterChannelUser)) {
                    logger.error(methodName, FILELABELMSG);
                } else {
                    logger.debug(methodName, " dwhFileLabelForMaster=" + dwhFileNameForMasterChannelUser);
                }
                if (BTSLUtil.isNullString(dwhFileLabelForMasterChannelUser) || BTSLUtil.isNullString(dwhFileNameForMasterChannelUser)) {
                    throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_COULD_NOT_FIND_DATA_IN_CONSTANTS_FILE);
                }

            }
            if (BTSLUtil.isNullString(dwhFileLabelForTransaction) || BTSLUtil.isNullString(dwhFileLabelForMaster) || BTSLUtil.isNullString(dwhFileNameForTransaction) || BTSLUtil
                .isNullString(dwhFileNameForMaster) || BTSLUtil.isNullString(masterDirectoryPathAndName) || BTSLUtil.isNullString(finalMasterDirectoryPath)) {
                throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_COULD_NOT_FIND_DATA_IN_CONSTANTS_FILE);
            }
            try {
                fileEXT = Constants.getProperty("DWH_FILE_EXT");
                ctlFileEXT = Constants.getProperty("DWH_CONTROL_FILE_EXT");
            } catch (Exception e) {
                fileEXT = ".csv";
                ctlFileEXT = ".ctl";
                logger.errorTrace(methodName, e);
            }
            logger.debug(methodName, " fileEXT=" + fileEXT);
            logger.debug(methodName, " ctrlFileEXT=" + ctlFileEXT);
            try {
                maxFileLength = Long.parseLong(Constants.getProperty("DWH_MAX_FILE_LENGTH"));
            } catch (Exception e) {
                maxFileLength = 1000;
                logger.errorTrace(methodName, e);
            }
            logger.debug(methodName, " maxFileLength=" + maxFileLength);
            logger.debug(methodName, " Required information successfuly loaded from Constants.props...............: ");
        } catch (BTSLBaseException be) {
            logger.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[loadConstantParameters]", "", "", "",
                "Message:" + be.getMessage());
            logger.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            logger.error(methodName, PretupsI.EXCEPTION + e.getMessage());
            logger.errorTrace(methodName, e);
            final BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[loadConstantParameters]", "", "", "",
                "Message:" + btslMessage);
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }

    }

    /**
     * This method will check the existance of under process and/or ambiguous
     * transaction for the given date
     * for the date for which method is called
     * 
     * @param pCon
     *            Connection
     * @param pBeingProcessedDate
     *            Date
     * @return boolean
     * @throws BTSLBaseException
     */
    private static boolean checkUnderprocessTransaction(Connection pCon, Date pBeingProcessedDate) throws BTSLBaseException {
        final String methodName = "checkUnderprocessTransaction";
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, " Entered : pBeingProcessedDate=" + pBeingProcessedDate);
        }
        PreparedStatement selectPstmt = null;
        ResultSet selectRst = null;
        boolean transactionFound = false;
        String selectQuery = null;
        try {
            // by sandeep goel ID DWH001 to make get the status from the file
            // not be hardcoded
            selectQuery = new String(
                "SELECT 1 FROM c2s_transfers WHERE transfer_date=? AND transfer_status IN('" + PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS + "','" + PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS + "') ");
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, "select query:" + selectQuery);
            }
            selectPstmt = pCon.prepareStatement(selectQuery);
            selectPstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(pBeingProcessedDate));
            selectRst = selectPstmt.executeQuery();
            if (selectRst.next()) {
                transactionFound = true;
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName,
                    "", "", "", "Message: DWHFileCreation process cannot continue as underprocess and/or ambiguous transactions are found.");
                throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_AMB_OR_UP_TXN_FOUND);
            }
        } catch (BTSLBaseException be) {
            logger.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
            logger.errorTrace(methodName, be);
            throw be;
        } catch (SQLException sqe) {
            logger.error(methodName, PretupsI.SQLEXCEPTION + sqe.getMessage());
            logger.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "",
                "", "", PretupsI.SQLEXCEPTION + sqe.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            logger.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
            logger.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "",
                "", "", PretupsI.EXCEPTION + ex.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (selectRst != null) {
                try {
                    selectRst.close();
                } catch (Exception ex) {
                    logger.errorTrace(methodName, ex);
                }
            }
            if (selectPstmt != null) {
                try {
                    selectPstmt.close();
                } catch (Exception ex) {
                    logger.errorTrace(methodName, ex);
                }
            }
            
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, "Exiting transactionFound=" + transactionFound);
            }
        }// end of finally
        return transactionFound;
    }

    /**
     * This method will create master and child directory at the path defined in
     * Constants.props, if it does not exist
     * 
     * @param pDirectoryPathAndName
     *            String
     * @param pProcessId
     *            String
     * @param pBeingProcessedDate
     *            Date
     * @throws BTSLBaseException
     * @return String
     */
    private static String createDirectory(String pDirectoryPathAndName, String pProcessId, Date pBeingProcessedDate) throws BTSLBaseException {
        final String methodName = "createDirectory";
        if (logger.isDebugEnabled()) {
            logger.debug(methodName,
                " Entered: pDirectoryPathAndName=" + pDirectoryPathAndName + " pProcessId=" + pProcessId + " pBeingProcessedDate = " + pBeingProcessedDate);
        }
        String dirName = null;
        try {
            boolean success = false;
            final File parentDir = new File(pDirectoryPathAndName);
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            pBeingProcessedDate = BTSLUtil.getSQLDateFromUtilDate(pBeingProcessedDate);
            // child directory name includes a file name and being processed
            // date, month and year
            dirName = pDirectoryPathAndName + File.separator + pProcessId + "_" + pBeingProcessedDate.toString().substring(8, 10) + pBeingProcessedDate.toString()
                .substring(5, 7) + pBeingProcessedDate.toString().substring(0, 4);
            final File newDir = new File(dirName);
            if (!newDir.exists()) {
                success = newDir.mkdirs();
            } else {
                success = true;
            }
            if (!success) {
                throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.COULD_NOT_CREATE_DIR);
            }
        } catch (BTSLBaseException be) {
            logger.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
            logger.errorTrace(methodName, be);
            throw be;
        } catch (Exception ex) {
            logger.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
            logger.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[createDirectory]", "", "", "",
                PretupsI.SQLEXCEPTION + ex.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, "Exiting dirName=" + dirName);
            }
        }
        return dirName;
    }

    /**
     * This method will fetch all the required Channel transactions data from
     * database
     * 
     * @param pCon
     *            Connection
     * @param pBeingProcessedDate
     *            Date
     * @param pDirPath
     *            String
     * @param pFileName
     *            String
     * @param pFileLabel
     *            String
     * @param pFileEXT
     *            String
     * @param pStartRowNum
     *            long
     * @param pEndRowNum
     *            long
     * @param pFileNumber
     *            int
     * @return void
     * @throws SQLException
     *             ,Exception
     */
    private static void fetchChannelTransactionData(Connection pCon, Date pBeingProcessedDate, String pDirPath, String pFileName, String pFileLabel, String pFileEXT, long pStartRowNum, long pEndRowNum, int pFileNumber) throws BTSLBaseException {
    	final String methodName = "fetchChannelTransactionData";
    	if (logger.isDebugEnabled()) {
            logger
                .debug(
                		methodName,
                    " Entered: pBeingProcessedDate=" + pBeingProcessedDate + PDIRPATH + pDirPath + PFILENAME + pFileName + PFILELABEL + pFileLabel + PFILEEXT + pFileEXT + " pStartRowNum=" + pStartRowNum + " pEndRowNum=" + pEndRowNum + PFILENUMBER + pFileNumber);
        }

        final StringBuilder channelQueryBuf = new StringBuilder();
        channelQueryBuf.append(" SELECT DATA FROM TEMP_RP2P_DWH_CHTRANS");
        channelQueryBuf.append(" WHERE SRNO > ? and SRNO <= ? ");
        final String channelSelectQuery = channelQueryBuf.toString();
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, "channel select query:" + channelSelectQuery);
        }
        PreparedStatement channelSelectPstmt = null;
        ResultSet channelSelectRst = null;

        try {
            channelSelectPstmt = pCon.prepareStatement(channelSelectQuery);
            channelSelectPstmt.setLong(1, pStartRowNum);
            channelSelectPstmt.setLong(2, pEndRowNum);
            channelSelectRst = channelSelectPstmt.executeQuery();
            logger.debug(methodName,
                        "Memory after loading channel transaction data: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + FREE + Runtime.getRuntime().freeMemory() / 1049576 + FORDATE + pBeingProcessedDate);

            // method call to write data in the files
            writeDataInTransactionFile(pDirPath, pFileName, pFileLabel, pBeingProcessedDate, pFileEXT, channelSelectRst, null, pFileNumber, "1");
            logger.debug(methodName, "Memory after writing transaction files: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + FREE + Runtime
            	                .getRuntime().freeMemory() / 1049576 + FORDATE + pBeingProcessedDate);
        } catch (BTSLBaseException be) {
            logger.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
            logger.errorTrace(methodName, be);
            throw be;
        } catch (SQLException sqe) {
            logger.error(methodName, PretupsI.SQLEXCEPTION + sqe.getMessage());
            logger.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "",
                "", PretupsI.SQLEXCEPTION + sqe.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            logger.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
            logger.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "",
                "", PretupsI.SQLEXCEPTION + ex.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (channelSelectRst != null) {
                try {
                    channelSelectRst.close();
                } catch (Exception ex) {
                    logger.errorTrace(methodName, ex);
                }
            }
            if (channelSelectPstmt != null) {
                try {
                    channelSelectPstmt.close();
                } catch (Exception ex) {
                    logger.errorTrace(methodName, ex);
                }
            }
            
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, PretupsI.EXITED);
            }
        }// end of finally
    }

    /**
     * This method will fetch all the required data from USERS table
     * 
     * @param pCon
     *            Connection
     * @param pBeingProcessedDate
     *            Date
     * @param pDirPath
     *            String
     * @param pFileName
     *            String
     * @param pFileLabel
     *            String
     * @param pFileEXT
     *            String
     * @param pStartRowNum
     *            long
     * @param pEndRowNum
     *            long
     * @param pFileNumber
     *            int
     * @return void
     * @throws SQLException
     *             ,Exception
     */
    private static void fetchMasterData(Connection pCon, Date pBeingProcessedDate, String pDirPath, String pFileName, String pFileLabel, String pFileEXT, long pStartRowNum, long pEndRowNum, int pFileNumber) throws BTSLBaseException {
        final String methodName = "fetchMasterData";
        
        if (logger.isDebugEnabled()) {
            logger
                .debug(
                    methodName,
                    " Entered:  pBeingProcessedDate=" + pBeingProcessedDate + PDIRPATH + pDirPath + PFILENAME + pFileName + PFILELABEL + pFileLabel + PFILEEXT + pFileEXT + ", pStartRowNum:" + pStartRowNum + ", pEndRowNum" + pEndRowNum + ", pFileNumber" + pFileNumber);
        }

        final StringBuilder queryBuf = new StringBuilder();
        queryBuf.append(" SELECT DATA FROM TEMP_RP2P_DWH_MASTER ");
        queryBuf.append(" WHERE SRNO > ? and SRNO <= ?");
        final String selectQuery = queryBuf.toString();
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, "select query:" + selectQuery);
        }
        PreparedStatement selectPstmt = null;
        ResultSet selectRst = null;

        try {
            selectPstmt = pCon.prepareStatement(selectQuery);
            selectPstmt.clearParameters();
            selectPstmt.setLong(1, pStartRowNum);
            selectPstmt.setLong(2, pEndRowNum);
            selectRst = selectPstmt.executeQuery();
            logger.debug(methodName, "Memory after loading master data: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + FREE + Runtime.getRuntime()
                            .freeMemory() / 1049576 + FORDATE + pBeingProcessedDate);
            // method call to write data in files
            writeDataInFile(pDirPath, pFileName, pFileLabel, pBeingProcessedDate, pFileEXT, selectRst, null, pFileNumber, "2");
            logger.debug(methodName, "Memory after writing master files: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + FREE + Runtime.getRuntime()
            	                .freeMemory() / 1049576 + FORDATE + pBeingProcessedDate);
        } catch (BTSLBaseException be) {
            logger.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
            logger.errorTrace(methodName, be);
            throw be;
        } catch (SQLException sqe) {
            logger.error(methodName, PretupsI.SQLEXCEPTION + sqe.getMessage());
            logger.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[fetchMasterData]", "", "", "",
                PretupsI.SQLEXCEPTION + sqe.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            logger.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
            logger.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[fetchMasterData]", "", "", "",
                PretupsI.EXCEPTION + ex.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (selectRst != null) {
                try {
                    selectRst.close();
                } catch (Exception ex) {
                    logger.errorTrace(methodName, ex);
                }
            }
            if (selectPstmt != null) {
                try {
                    selectPstmt.close();
                } catch (Exception ex) {
                    logger.errorTrace(methodName, ex);
                }
            }
            
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, PretupsI.EXITED);
            }
        }// end of finally
    }

    /**
     * This method will fetch all the required data from USERS table
     * 
     * @param pCon
     *            Connection
     * @param pBeingProcessedDate
     *            Date
     * @param pDirPath
     *            String
     * @param pFileName
     *            String
     * @param pFileLabel
     *            String
     * @param pFileEXT
     *            String
     * @param pStartRowNum
     *            long
     * @param pEndRowNum
     *            long
     * @param pFileNumber
     *            int
     * @return void
     * @throws SQLException
     *             ,Exception
     */
    private static void fetchMasterDataForChannelUser(Connection pCon, Date pBeingProcessedDate, String pDirPath, String pFileName, String pFileLabel, String pFileEXT, long pStartRowNum, long pEndRowNum, int pFileNumber) throws BTSLBaseException {
        final String methodName = "fetchMasterDataForChannelUser";
        
        if (logger.isDebugEnabled()) {
            logger
                .debug(
                    methodName,
                    " Entered:  pBeingProcessedDate=" + pBeingProcessedDate + PDIRPATH + pDirPath + PFILENAME + pFileName + PFILELABEL + pFileLabel + PFILEEXT + pFileEXT + ", pStartRowNum:" + pStartRowNum + ", pEndRowNum" + pEndRowNum + ", pFileNumber" + pFileNumber);
        }

        final StringBuilder queryBuf = new StringBuilder();
        queryBuf.append(" SELECT DATA FROM TEMP_RP2P_DWH_MASTER ");
        queryBuf.append(" WHERE SRNO > ? and SRNO <= ?");
        final String selectQuery = queryBuf.toString();
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, "select query:" + selectQuery);
        }
        PreparedStatement selectPstmt = null;
        ResultSet selectRst = null;

        try {
            selectPstmt = pCon.prepareStatement(selectQuery);
            selectPstmt.clearParameters();
            selectPstmt.setLong(1, pStartRowNum);
            selectPstmt.setLong(2, pEndRowNum);
            selectRst = selectPstmt.executeQuery();
            logger.debug(methodName, "Memory after loading master data: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + FREE + Runtime
                            .getRuntime().freeMemory() / 1049576 + FORDATE + pBeingProcessedDate);
            // method call to write data in files
            writeDataInFileChannelUser(pDirPath, pFileName, pFileLabel, pBeingProcessedDate, pFileEXT, selectRst, null, pFileNumber, "2");
            logger.debug(methodName, "Memory after writing master files: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + FREE + Runtime
                            .getRuntime().freeMemory() / 1049576 + FORDATE + pBeingProcessedDate);
        } catch (BTSLBaseException be) {
            logger.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
            logger.errorTrace(methodName, be);
            throw be;
        } catch (SQLException sqe) {
            logger.error(methodName, PretupsI.SQLEXCEPTION + sqe.getMessage());
            logger.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[fetchMasterDataForChannelUser]", "",
                "", "", PretupsI.SQLEXCEPTION + sqe.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            logger.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
            logger.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[fetchMasterDataForChannelUser]", "",
                "", "", PretupsI.EXCEPTION + ex.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (selectRst != null) {
                try {
                    selectRst.close();
                } catch (Exception ex) {
                    logger.errorTrace(methodName, ex);
                }
            }
            if (selectPstmt != null) {
                try {
                    selectPstmt.close();
                } catch (Exception ex) {
                    logger.errorTrace(methodName, ex);
                }
            }
            
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, PretupsI.EXITED);
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
    private static void writeDataInFile(String pdirPath, String pfileName, String pfileLabel, Date pbeingProcessedDate, String pfileEXT, ResultSet rst1, ResultSet rst2, int pfileNumber, String call) throws BTSLBaseException {
        if (logger.isDebugEnabled()) {
            logger
                .debug(
                    WRITEDATAINFILE,
                    " Entered:  pdirPath=" + pdirPath + " pfileName=" + pfileName + " pfileLabel=" + pfileLabel + " pbeingProcessedDate=" + pbeingProcessedDate + " pfileEXT=" + pfileEXT + " pfileNumber=" + pfileNumber);
        }
        final String methodName = WRITEDATAINFILE;
        long recordsWrittenInFile = 0;
        PrintWriter out = null;
        int fileNumber = 1;
        String fileName = null;
        File newFile = null;
        String fileData = null;
        String fileHeader = null;
        String fileFooter = null;
        String txnStatus = null;
        String bonusDetails = null;

        try {

            // Generating file name
        	// Added By Sanjay on 30-Sep-2016
            fileNumber = pfileNumber;
            // if the length of file number is 1, four zeros are added as prefix
            if (Integer.toString(fileNumber).length() == 1) {
                fileName = pdirPath + File.separator + pfileName + "_" + "0000" + fileNumber + pfileEXT;
            } else if (Integer.toString(fileNumber).length() == 2) {
                fileName = pdirPath + File.separator + pfileName + "_" + "000" + fileNumber + pfileEXT;
            } else if (Integer.toString(fileNumber).length() == 3) {
            	fileName = pdirPath + File.separator + pfileName + "_" + "00" + fileNumber + pfileEXT;
            }
            else if (Integer.toString(fileNumber).length() == 4) {
            	fileName = pdirPath + File.separator + pfileName + "_" + "0" + fileNumber + pfileEXT;
            }
            else if (Integer.toString(fileNumber).length() == 5) {
            	fileName = pdirPath + File.separator + pfileName + "_" + fileNumber + pfileEXT;
            }

            logger.debug(WRITEDATAINFILE, FILENAME + fileName);

            newFile = new File(fileName);
            fileNameLst.add(fileName);
            //
            out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));

            // ID DWH002 to make addition of header and footer optional on the
            // basis of entry in Constants.props
            if ("Y".equalsIgnoreCase(Constants.getProperty("ADD_HEADER_FOOTER"))) {
                fileHeader = constructFileHeader(pbeingProcessedDate, fileNumber, pfileLabel);
                out.write(fileHeader);
            }
            // traverse first resultset

            while (rst1 != null && rst1.next()) {
                fileData = rst1.getString("DATA");
                if ("2".equals(call)) {
                    out.write(fileData + "\n");
                } else {
                    out.write(fileData + bonusBundleDefaultValues + "\n");
                }
                out.flush();
                recordsWrittenInFile++;
            }

            // in case of master data, there is only one resultset
            // while in case of transactions data, two resultsets are to be
            // traversed.
            while (rst2 != null && rst2.next()) {
                fileData = rst2.getString(1);
                txnStatus = rst2.getString(2);
                bonusDetails = rst2.getString(3);
                if (PretupsErrorCodesI.TXN_STATUS_SUCCESS.equals(txnStatus)) {
                    if (!BTSLUtil.isNullString(bonusDetails)) {
                        out.write(fileData + bonusDetails + "\n");
                    } else {
                        out.write(fileData + bonusBundleDefaultValues + "\n");
                    }
                } else {
                    out.write(fileData + bonusBundleDefaultValues + "\n");
                }
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

                // 04-MAR-2014 for OCI client
                if (fileName.contains(masterDirectoryPathAndName)) {
                    fileName = fileName.replace(masterDirectoryPathAndName, finalMasterDirectoryPath);
                }
            } else {
                if (out != null) {
                    out.close();
                }
                newFile.delete();
                fileNameLst.remove(fileNameLst.size() - 1);
            }
            if (out != null) {
                out.close();
            }
        } catch (SQLException e) {
            deleteAllFiles();
            logger.debug(WRITEDATAINFILE, "SQLException: " + e.getMessage());
            logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[writeDataInFile]", "", "", "",
            		PretupsI.EXCEPTION + e.getMessage());
            throw new BTSLBaseException("DWHFileCreation", WRITEDATAINFILE, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } catch (IOException e) {
            deleteAllFiles();
            logger.debug(WRITEDATAINFILE, "IOException: " + e.getMessage());
            logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[writeDataInFile]", "", "", "",
            		PretupsI.EXCEPTION + e.getMessage());
            throw new BTSLBaseException("DWHFileCreation", WRITEDATAINFILE, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } catch (Exception e) {
            deleteAllFiles();
            logger.debug(WRITEDATAINFILE, "Exception: " + e.getMessage());
            logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[writeDataInFile]", "", "", "",
            		PretupsI.EXCEPTION + e.getMessage());
            throw new BTSLBaseException("DWHFileCreation", WRITEDATAINFILE, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (out != null) {
                out.close();
            }
            if (logger.isDebugEnabled()) {
                logger.debug(WRITEDATAINFILE, "Exiting ");
            }
        }
    }


    /**
     * @param pDirPath
     *            String
     * @param pFileName
     *            String
     * @param pFileLabel
     *            String
     * @param pBeingProcessedDate
     *            Date
     * @param pFileEXT
     *            String
     * @param p_maxFileLength
     *            long
     * @param rst1
     *            ResultSet
     * @param rst2
     *            ResultSet
     * @return void
     * @throws Exception
     * 
     * */
    private static void writeDataInFileChannelUser(String pDirPath, String pFileName, String pFileLabel, Date pBeingProcessedDate, String pFileEXT, ResultSet rst1, ResultSet rst2, int pFileNumber, String call) throws BTSLBaseException {
        final String methodName = "writeDataInFileChannelUser";
        if (logger.isDebugEnabled()) {
            logger
                .debug(
                    methodName,
                    " Entered:  pDirPath=" + pDirPath + PFILENAME + pFileName + PFILELABEL + pFileLabel + " pBeingProcessedDate=" + pBeingProcessedDate + PFILEEXT + pFileEXT + PFILENUMBER + pFileNumber);
        }

        long recordsWrittenInFile = 0;
        PrintWriter out = null;
        int fileNumber = 1;
        String fileName = null;
        File newFile = null;
        String fileData = null;
        String fileHeader = null;
        String fileFooter = null;
        String txnStatus = null;
        String bonusDetails = null;
        String fileDataArray[] = null;
        try {
            try {
                pBeingProcessedDate = BTSLUtil.getSQLDateFromUtilDate(pBeingProcessedDate);

            } catch (Exception e) {
                logger.errorTrace(methodName, e);
            }
            // Generating file name
            fileNumber = pFileNumber;
            // if the length of file number is 1, four zeros are added as prefix
            if (Integer.toString(fileNumber).length() == 1) {
                fileName = pDirPath + File.separator + pFileName + "_" + pBeingProcessedDate.toString().substring(0, 4) + "_" + pBeingProcessedDate.toString().substring(
                    5, 7) + "_" + pBeingProcessedDate.toString().substring(8, 10) + pFileEXT;
            }

            logger.debug(methodName, FILENAME + fileName);

            newFile = new File(fileName);
            fileNameLst.add(fileName);
            //
            out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));

            // ID DWH002 to make addition of header and footer optional on the
            // basis of entry in Constants.props
            if ("Y".equalsIgnoreCase(Constants.getProperty(ADDHEADERFOOTER))) {
                fileHeader = constructFileHeader(pBeingProcessedDate, fileNumber, pFileLabel);
                out.write(fileHeader);
            }
            // traverse first resultset

            while (rst1 != null && rst1.next()) {
                fileData = rst1.getString("DATA");
                
                fileDataArray = fileData.replace(dwhFileSeparator, ",").split(",");
                fileData = fileDataArray[5] + "," + fileDataArray[7] + "," + fileDataArray[4] + "," + fileDataArray[6] + "," + fileDataArray[9] + "," + fileDataArray[11]+ "," + fileDataArray[10] + "," + fileDataArray[12];
               
                
                if ("2".equals(call)) {
                    out.write(fileData + "\n");
                } else {
                    out.write(fileData + bonusBundleDefaultValues + "\n");
                }
                out.flush();
                recordsWrittenInFile++;
            }

            // in case of master data, there is only one resultset
            // while in case of transactions data, two resultsets are to be
            // traversed.
            while (rst2 != null && rst2.next()) {
                fileData = rst2.getString(1);
                txnStatus = rst2.getString(2);
                bonusDetails = rst2.getString(3);
                if (PretupsErrorCodesI.TXN_STATUS_SUCCESS.equals(txnStatus)) {
                    if (!BTSLUtil.isNullString(bonusDetails)) {
                        out.write(fileData + bonusDetails + "\n");
                    } else {
                        out.write(fileData + bonusBundleDefaultValues + "\n");
                    }
                } else {
                    out.write(fileData + bonusBundleDefaultValues + "\n");
                }
                out.flush();
                recordsWrittenInFile++;
            }
            // if number of records are not zero then footer is appended as file
            // is deleted
            if (recordsWrittenInFile > 0) {
                // ID DWH002 to make addition of header and footer optional on
                // the basis of entry in Constants.props
                if ("Y".equalsIgnoreCase(Constants.getProperty(ADDHEADERFOOTER))) {
                    fileFooter = constructFileFooter(recordsWrittenInFile);
                    out.write(fileFooter);
                }
            } else {
                if (out != null) {
                    out.close();
                }
                newFile.delete();
                fileNameLst.remove(fileNameLst.size() - 1);
            }
            if (out != null) {
                out.close();
            }
        } catch (Exception e) {
            deleteAllFiles();
            logger.debug(methodName, PretupsI.EXCEPTION + e.getMessage());
            logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[writeDataInFileChannelUser]", "", "",
                "", PretupsI.EXCEPTION + e.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (out != null) {
                out.close();
            }
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, PretupsI.EXITED);
            }
        }
    }

    /**
     * @param pCon
     *            Connection
     * @param pProcessId
     *            String
     * @throws BTSLBaseException
     * @return int
     */
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[markProcessStatusAsComplete]", "", "",
                "", PretupsI.EXCEPTION + e.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, "Exiting: updateCount=" + updateCount);
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
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, " Entered: ");
        }
        int size = 0;
        if (fileNameLst != null) {
            size = fileNameLst.size();
        }
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, " : Number of files to be deleted " + size);
        }
        String fileName;
        File newFile;
        for (int i = 0; i < size; i++) {
            try {
                fileName = (String) fileNameLst.get(i);
                newFile = new File(fileName);
                newFile.delete();
                if (logger.isDebugEnabled()) {
                    logger.debug("", fileName + " file deleted");
                }
            } catch (Exception e) {
                logger.error(methodName, "Exception " + e.getMessage());
                logger.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[deleteAllFiles]", "", "", "",
                    PretupsI.EXCEPTION + e.getMessage());
                throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
            }
        }// end of for loop
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[deleteAllFiles]", "", "", "",
            " Message: DWHFileCreation process has found some error, so deleting all the files.");
        if (fileNameLst != null && fileNameLst.isEmpty()) {
            fileNameLst.clear();
        }
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, " : Exiting.............................");
        }
    }

    /**
     * This method is used to constuct file header
     * 
     * @param pBeingProcessedDate
     *            Date
     * @param pFileNumber
     *            long
     * @param pFileLabel
     *            String
     * @return String
     */
    private static String constructFileHeader(Date pBeingProcessedDate, long pFileNumber, String pFileLabel) {
        final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
        final StringBuilder fileHeaderBuf = new StringBuilder("");
        fileHeaderBuf.append("Present Date=" + sdf.format(new Date()));
        fileHeaderBuf.append("\n" + "For Date=" + sdf.format(pBeingProcessedDate));
        fileHeaderBuf.append("\n" + "File Number=" + pFileNumber);
        fileHeaderBuf.append("\n" + pFileLabel);
        fileHeaderBuf.append("\n" + "[STARTDATA]" + "\n");
        return fileHeaderBuf.toString();
    }

    /**
     * This method is used to constuct file footer
     * 
     * @param pNoOfRecords
     *            long
     * @return String
     */
    private static String constructFileFooter(long pNoOfRecords) {
    	StringBuilder fileHeaderBuf;
        fileHeaderBuf = new StringBuilder("");
        fileHeaderBuf.append("[ENDDATA]" + "\n");
        fileHeaderBuf.append("Number of records=" + pNoOfRecords);
        return fileHeaderBuf.toString();
    }

    /**
     * This method will copy all the created files to another location.
     * the process will generate files in a particular directroy. if the process
     * thats has to read files strarts before copletion of the file generation,
     * errors will occur. so a different directory is created and files are
     * moved to that final directory.
     * 
     * @param pOldDirectoryPath
     *            String
     * @param pFinalDirectoryPath
     *            String
     * @param pProcessId
     *            String
     * @param pBeingProcessedDate
     *            Date
     * @throws BTSLBaseException
     * @return String
     */
    private static void moveFilesToFinalDirectory(String pOldDirectoryPath, String pFinalDirectoryPath, String pProcessId, Date pBeingProcessedDate) throws BTSLBaseException {
        final String methodName = "moveFilesToFinalDirectory";
        if (logger.isDebugEnabled()) {
            logger
                .debug(
                    methodName,
                    " Entered: pOldDirectoryPath=" + pOldDirectoryPath + " pFinalDirectoryPath=" + pFinalDirectoryPath + " pProcessId=" + pProcessId + " pBeingProcessedDate=" + pBeingProcessedDate);
        }

        String oldFileName = null;
        String newFileName = null;
        File oldFile = null;
        File newFile = null;
        File parentDir = new File(pFinalDirectoryPath);
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        pBeingProcessedDate = BTSLUtil.getSQLDateFromUtilDate(pBeingProcessedDate);
        // child directory name includes a file name and being processed date,
        // month and year
        final String oldDirName = pOldDirectoryPath + File.separator + pProcessId + "_" + pBeingProcessedDate.toString().substring(8, 10) + pBeingProcessedDate.toString()
            .substring(5, 7) + pBeingProcessedDate.toString().substring(0, 4);
        final String newDirName = pFinalDirectoryPath + File.separator + pProcessId + "_" + pBeingProcessedDate.toString().substring(8, 10) + pBeingProcessedDate
            .toString().substring(5, 7) + pBeingProcessedDate.toString().substring(0, 4);
        File oldDir = new File(oldDirName);
        File newDir = new File(newDirName);
        if (!newDir.exists()) {
            newDir.mkdirs();
        }

        if (logger.isDebugEnabled()) {
            logger.debug(methodName, " newDirName=" + newDirName);
        }

        final int size = fileNameLst.size();
        try {
            for (int i = 0; i < size; i++) {
                oldFileName = (String) fileNameLst.get(i);
                oldFile = new File(oldFileName);
                newFileName = oldFileName.replace(pOldDirectoryPath, pFinalDirectoryPath);
                newFile = new File(newFileName);
                oldFile.renameTo(newFile);
                if (logger.isDebugEnabled()) {
                    logger.debug(methodName, " File " + oldFileName + " is moved to " + newFileName);
                }
            }// end of for loop
            fileNameLst.clear();
            if (oldDir.exists()) {
                oldDir.delete();
            }
            logger.debug(methodName, " File " + oldFileName + " is moved to " + newFileName);
        } catch (Exception e) {
            logger.error(methodName, "Exception " + e.getMessage());
            logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[moveFilesToFinalDirectory]", "", "",
                "", PretupsI.EXCEPTION + e.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
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
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, "Exiting.. ");
            }
        } // end of finally
    }

    /**
     * This method will fetch all the required C2S transactions data from
     * database
     * 
     * @param pCon
     *            Connection
     * @param pBeingProcessedDate
     *            Date
     * @param pDirPath
     *            String
     * @param pFileName
     *            String
     * @param pFileLabel
     *            String
     * @param pFileEXT
     *            String
     * @param pStartRowNum
     *            long
     * @param pEndRowNum
     *            long
     * @param pFileNumber
     *            int
     * @return void
     * @throws SQLException
     *             ,Exception
     */
    private static void fetchC2STransactionData(Connection pCon, Date pBeingProcessedDate, String pDirPath, String pFileName, String pFileLabel, String pFileEXT, long pStartRowNum, long pEndRowNum, int pFileNumber) throws BTSLBaseException {
        final String methodName = "fetchC2STransactionData";
        if (logger.isDebugEnabled()) {
            logger
                .debug(
                    methodName,
                    " Entered: pBeingProcessedDate=" + pBeingProcessedDate + PDIRPATH + pDirPath + PFILENAME + pFileName + PFILELABEL + pFileLabel + PFILEEXT + pFileEXT + " pStartRowNum=" + pStartRowNum + " pEndRowNum=" + pEndRowNum + PFILENUMBER + pFileNumber);
        }

        final StringBuilder channelQueryBuf = new StringBuilder();
        channelQueryBuf.append(" SELECT DATA,TRANSFER_STATUS,BONUS_DETAILS FROM TEMP_RP2P_DWH_C2STRANS");
        channelQueryBuf.append(" WHERE SRNO > ? and SRNO <= ? ");
        final String channelSelectQuery = channelQueryBuf.toString();
        if (logger.isDebugEnabled()) {
            logger.debug(methodName, "channel select query:" + channelSelectQuery);
        }
        PreparedStatement channelSelectPstmt = null;
        ResultSet channelSelectRst = null;

        try {
            channelSelectPstmt = pCon.prepareStatement(channelSelectQuery);
            channelSelectPstmt.setLong(1, pStartRowNum);
            channelSelectPstmt.setLong(2, pEndRowNum);
            channelSelectRst = channelSelectPstmt.executeQuery();
            logger
            .debug(methodName, "Memory after loading channel transaction data: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + FREE + Runtime
                             .getRuntime().freeMemory() / 1049576 + FORDATE + pBeingProcessedDate);

            // method call to write data in the files
            writeDataInTransactionFile(pDirPath, pFileName, pFileLabel, pBeingProcessedDate, pFileEXT, null, channelSelectRst, pFileNumber, "1");
            logger.debug(methodName, "Memory after writing transaction files: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + FREE + Runtime
            	                .getRuntime().freeMemory() / 1049576 + FORDATE + pBeingProcessedDate);
        } catch (BTSLBaseException be) {
            logger.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
            logger.errorTrace(methodName, be);
            throw be;
        } catch (SQLException sqe) {
            logger.error(methodName, PretupsI.SQLEXCEPTION + sqe.getMessage());
            logger.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[fetchC2STransactionData]", "", "", "",
                PretupsI.SQLEXCEPTION + sqe.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            logger.error(methodName, PretupsI.EXCEPTION + ex.getMessage());
            logger.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "",
                "", PretupsI.SQLEXCEPTION + ex.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (channelSelectRst != null) {
                try {
                    channelSelectRst.close();
                } catch (Exception ex) {
                    logger.errorTrace(methodName, ex);
                }
            }
            if (channelSelectPstmt != null) {
                try {
                    channelSelectPstmt.close();
                } catch (Exception ex) {
                    logger.errorTrace(methodName, ex);
                }
            }

            if (logger.isDebugEnabled()) {
                logger.debug(methodName, PretupsI.EXITED);
            }
        }// end of finally
    }

    /**
     * @author diwakar
     * @date : 04-MAR-2014
     * @param pBeingProcessedDate
     * @param pRecordsWrittenInFile
     * @param pFileName
     * @throws BTSLBaseException
     */
    private static void generateDataFileSummary(Date pBeingProcessedDate, long pRecordsWrittenInFile, String pFileName) throws BTSLBaseException {
        if (logger.isDebugEnabled()) {
            logger
                .debug(
                    CLASSNAME,
                    " Entered: generateDataFileSummary pBeingProcessedDate=" + pBeingProcessedDate + ", pRecordsWrittenInFile=" + pRecordsWrittenInFile + ", pFileName=" + pFileName);
        }
        final String methodName = "generateDataFileSummary";
        try {
            final String processDateStr = BTSLUtil.getDateStringFromDate(pBeingProcessedDate);
            if (fileRecordMap.size() == 0) {
                fileNameMap.put(pFileName, pRecordsWrittenInFile);
                fileRecordMap.put(processDateStr, fileNameMap);
            } else {
                if (fileRecordMap.containsKey(processDateStr)) {
                    fileNameMap = (HashMap<String, Long>) fileRecordMap.get(processDateStr);
                    fileNameMap.put(pFileName, pRecordsWrittenInFile);
                    // Added By Diwakar on 07-MAR-2014
                    fileRecordMap.put(processDateStr, fileNameMap);
                    // Ended Here
                } else {
                    fileNameMap = new HashMap<>();
                    fileNameMap.put(pFileName, pRecordsWrittenInFile);
                    fileRecordMap.put(processDateStr, fileNameMap);
                }
            }

        } catch (Exception e) {
            logger.errorTrace(methodName, e);
            logger.debug(CLASSNAME, " generateDataFileSummary() While recoding file list Exception: " + e.getMessage());
        } finally {
            // 09-SEP-2014 for OCI client
            String isSummaryFileReq = "N";
            isSummaryFileReq = Constants.getProperty("DWH_SUMMARY_FILE_REQUIRED");
            if (BTSLUtil.isNullString(isSummaryFileReq)) {
                isSummaryFileReq = "N";
            }
            if ("Y".equalsIgnoreCase(isSummaryFileReq)) {
                try {
                    writeFileSummary(finalMasterDirectoryPath, ctlFileEXT, pBeingProcessedDate);
                } catch (Exception e) {
                    logger.errorTrace(methodName, e);
                }
                // Ended Here
            }

            if (logger.isDebugEnabled()) {
                logger.debug(CLASSNAME, "Exiting generateDataFileSummary() ");
            }
        }
    }

    /**
     * @author diwakar
     * @date : 04-MAR-2014
     * @param pDirPath
     * @param pFileEXT
     * @param processedDate
     * @throws BTSLBaseException
     */
    private static void writeFileSummary(String pDirPath, String pFileEXT, Date pBeingProcessedDate) throws BTSLBaseException {
        final String methodName = "writeFileSummary";
        if (logger.isDebugEnabled()) {
            logger.debug(CLASSNAME,
                " Entered: writeFileSummary() pDirPath=" + pDirPath + ", pFileEXT=" + pFileEXT + ", pBeingProcessedDate=" + pBeingProcessedDate);
        }
        PrintWriter out = null;
        File newFile = null;
        try {
            String fileName = null;
            String fileData = null;
            String fileHeader = null;
            String processDate = null;
            // Changed on 05-MAR-2014
            // Commented below line
            // fileName=pDirPath+File.separator+"fileRecordSumm_"+BTSLUtil.getDateTimeStringFromDate(new
            fileName = pDirPath + File.separator + "Control_" + BTSLUtil.getDateTimeStringFromDate(pBeingProcessedDate, "yyyyMMdd") + pFileEXT;
            // Ended Here
            logger.debug(CLASSNAME, " writeFileSummary() fileName=" + fileName);
            boolean isFileAlreadyExists = false;
            newFile = new File(fileName);
            
            File parentDir = new File(pDirPath);
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            if (!newFile.exists()) {
                newFile.createNewFile();
                isFileAlreadyExists = false;
            } else {
                isFileAlreadyExists = true;
            }
            // Added by Diwakar on 07-MAR-2014
            String transSeperator = null;
            try {
                transSeperator = Constants.getProperty("DWH_TRANSCATION_STAT_SEPERATOR");
            } catch (RuntimeException e) {
                transSeperator = ", ";
                logger.errorTrace(methodName, e);
            }
            if (newFile.exists()) {
                if (!isFileAlreadyExists) {
                    out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
                } else {
                    out = new PrintWriter(new BufferedWriter(new FileWriter(newFile, true /*
                                                                                           * append
                                                                                           * =
                                                                                           * true
                                                                                           */)));
                }
                if (!isFileAlreadyExists) {
                    fileHeader = "File_Name" + transSeperator + "Total_Records";
                    out.write(fileHeader + "\n");
                }
                HashMap<String, Long> fileRecord = null;
                fileRecordMap.comparator();
                final Set<String> keyList = fileRecordMap.keySet();
                final Iterator<String> itrProcessDate = keyList.iterator();
                Iterator<String> itrFile = null;
                String file = null;
                int i = 0;
                while (itrProcessDate.hasNext()) {
                    i = 0;
                    file = null;
                    fileData = null;
                    itrFile = null;
                    processDate = null;
                    processDate = itrProcessDate.next();
                    fileRecord = (HashMap) fileRecordMap.get(processDate);
                    itrFile = (fileRecord.keySet()).iterator();
                    // fileData=processDate+","+new
                    while (itrFile.hasNext()) {
                        file = itrFile.next();
                        fileData =  file + transSeperator + fileRecord.get(file).toString();
                        out.append(fileData + "\n");
                        i++;
                    }
                }
                out.flush();
                logger.error(CLASSNAME, " writeFileSummary() fileName=" + fileName + " has been created successfully.");
            } else {
                logger.error(CLASSNAME, " writeFileSummary() fileName=" + fileName + " does not exists on system.");
            }
        } catch (IOException e) {
            logger.debug(CLASSNAME, "Exception writeFileSummary(): " + e.getMessage());
            logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[writeFileSummary]", "", "", "",
                PretupsI.EXCEPTION + e.getMessage());
            throw new BTSLBaseException(CLASSNAME, "writeFileSummary()", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } catch (Exception e) {
            logger.debug(CLASSNAME, "Exception writeFileSummary(): " + e.getMessage());
            logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[writeFileSummary]", "", "", "",
                PretupsI.EXCEPTION + e.getMessage());
            throw new BTSLBaseException(CLASSNAME, "writeFileSummary()", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (out != null) {
                out.close();
            }
            if (fileRecordMap != null) {
                fileRecordMap.clear();
            }
            if (fileNameMap != null) {
                fileNameMap.clear();
            }
            if (logger.isDebugEnabled()) {
                logger.debug(CLASSNAME, "Exiting writeFileSummary() ");
            }
        }
    }
    
    
    /**
     * @param pDirPath
     *            String
     * @param pFileName
     *            String
     * @param pFileLabel
     *            String
     * @param pBeingProcessedDate
     *            Date
     * @param pFileEXT
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
    
    // Added By Sanjay on 30-Sep-2016
    private static void writeDataInTransactionFile(String pDirPath, String pFileName, String pFileLabel, Date pBeingProcessedDate, String pFileEXT, ResultSet rst1, ResultSet rst2, int pFileNumber, String call) throws BTSLBaseException {
    	if (logger.isDebugEnabled()) {
            logger
                .debug(
                    "writeDataInTransactionFile",
                    " Entered:  pDirPath=" + pDirPath + PFILENAME + pFileName + PFILELABEL + pFileLabel + " pBeingProcessedDate=" + pBeingProcessedDate + PFILEEXT + pFileEXT + PFILENUMBER + pFileNumber);
        }
        final String methodName = WRITEDATAINFILE;
        long recordsWrittenInFile = 0;
        PrintWriter out = null;
        int fileNumber = 1;
        String fileName = null;
        File newFile = null;
        String fileData = null;
        String fileHeader = null;
        String fileFooter = null;
        String txnStatus = null;
        String bonusDetails = null;

        try {
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        	Date currentDate = new Date();
        	String generationDateTime = sdf.format(currentDate);
            // Generating file name
            fileNumber = pFileNumber;
            // if the length of file number is 1, four zeros are added as prefix
            if (Integer.toString(fileNumber).length() == 1) {
                fileName = pDirPath + File.separator + pFileName +"_"+  pBeingProcessedDate.toString().substring(0, 4) + pBeingProcessedDate.toString()
                        .substring(5, 7) + pBeingProcessedDate.toString().substring(8, 10)+ "_"+ generationDateTime + "_"+ "0000" + fileNumber + pFileEXT;
            } else if (Integer.toString(fileNumber).length() == 2) {
                fileName = pDirPath + File.separator + pFileName +"_"+  pBeingProcessedDate.toString().substring(0, 4) + pBeingProcessedDate.toString()
                .substring(5, 7) + pBeingProcessedDate.toString().substring(8, 10)+ "_"+ generationDateTime + "_"+ "000" + fileNumber + pFileEXT;
            } else if (Integer.toString(fileNumber).length() == 3) {
                fileName = pDirPath + File.separator + pFileName +"_"+  pBeingProcessedDate.toString().substring(0, 4) + pBeingProcessedDate.toString()
                .substring(5, 7) + pBeingProcessedDate.toString().substring(8, 10)+ "_"+ generationDateTime + "_"+ "00" + fileNumber + pFileEXT;
            }
            else if (Integer.toString(fileNumber).length() == 4) {
                fileName = pDirPath + File.separator + pFileName +"_"+  pBeingProcessedDate.toString().substring(0, 4) + pBeingProcessedDate.toString()
                .substring(5, 7) + pBeingProcessedDate.toString().substring(8, 10)+ "_"+ generationDateTime + "_"+ "0" + fileNumber + pFileEXT;
            }
            else if (Integer.toString(fileNumber).length() == 5) {
                fileName = pDirPath + File.separator + pFileName +"_"+  pBeingProcessedDate.toString().substring(0, 4) + pBeingProcessedDate.toString()
                .substring(5, 7) + pBeingProcessedDate.toString().substring(8, 10)+ "_"+ generationDateTime + "_" + fileNumber + pFileEXT;
           }
            
            logger.debug(methodName, FILENAME + fileName);
           
            newFile = new File(fileName);
            fileNameLst.add(fileName);
            //
            out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));

            // ID DWH002 to make addition of header and footer optional on the
            // basis of entry in Constants.props
            if ("Y".equalsIgnoreCase(Constants.getProperty(ADDHEADERFOOTER))) {
                fileHeader = constructFileHeader(pBeingProcessedDate, fileNumber, pFileLabel);
                out.write(fileHeader);
            }
            // traverse first resultset

            while (rst1 != null && rst1.next()) {
                fileData = rst1.getString("DATA");
                if ("2".equals(call)) {
                    out.write(fileData + "\n");
                } else {
                    out.write(fileData + bonusBundleDefaultValues + "\n");
                }
                out.flush();
                recordsWrittenInFile++;
            }

            // in case of master data, there is only one resultset
            // while in case of transactions data, two resultsets are to be
            // traversed.
            while (rst2 != null && rst2.next()) {
                fileData = rst2.getString(1);
                txnStatus = rst2.getString(2);
                bonusDetails = rst2.getString(3);
                if (PretupsErrorCodesI.TXN_STATUS_SUCCESS.equals(txnStatus)) {
                    if (!BTSLUtil.isNullString(bonusDetails)) {
                        out.write(fileData + bonusDetails + "\n");
                    } else {
                        out.write(fileData + bonusBundleDefaultValues + "\n");
                    }
                } else {
                    out.write(fileData + bonusBundleDefaultValues + "\n");
                }
                out.flush();
                recordsWrittenInFile++;
            }
            // if number of records are not zero then footer is appended as file
            // is deleted
            if (recordsWrittenInFile > 0) {
                // ID DWH002 to make addition of header and footer optional on
                // the basis of entry in Constants.props
                if ("Y".equalsIgnoreCase(Constants.getProperty(ADDHEADERFOOTER))) {
                    fileFooter = constructFileFooter(recordsWrittenInFile);
                    out.write(fileFooter);
                }

                // 04-MAR-2014 for OCI client
                if (fileName.contains(masterDirectoryPathAndName)) {
                    fileName = fileName.replace(masterDirectoryPathAndName, finalMasterDirectoryPath);
                }
                try {
                	String temp = File.separator + ProcessI.DWH_PROCESSID + "_" + pBeingProcessedDate.toString().substring(8, 10) + pBeingProcessedDate.toString()
                            .substring(5, 7) + pBeingProcessedDate.toString().substring(0, 4)+File.separator;
                    generateDataFileSummary(pBeingProcessedDate, recordsWrittenInFile, fileName.substring(finalMasterDirectoryPath.length()+temp.length()));
                } catch (Exception e) {
                    logger.errorTrace(methodName, e);
                }
                // Ended Here
            } else {
                if (out != null) {
                    out.close();
                }
                newFile.delete();
                fileNameLst.remove(fileNameLst.size() - 1);
            }
            if (out != null) {
                out.close();
            }
        } catch (SQLException e) {
            deleteAllFiles();
            logger.debug(methodName, "SQLException: " + e.getMessage());
            logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "",
                PretupsI.EXCEPTION + e.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } catch (IOException e) {
            deleteAllFiles();
            logger.debug(methodName, "IOException: " + e.getMessage());
            logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "",
                PretupsI.EXCEPTION + e.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } catch (Exception e) {
            deleteAllFiles();
            logger.debug(methodName, PretupsI.EXCEPTION + e.getMessage());
            logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "",
                PretupsI.EXCEPTION + e.getMessage());
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (out != null) {
                out.close();
            }
            if (logger.isDebugEnabled()) {
                logger.debug(methodName, PretupsI.EXITED);
            }
        }
    }
    
}

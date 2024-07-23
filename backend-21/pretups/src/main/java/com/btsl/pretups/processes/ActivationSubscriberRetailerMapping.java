package com.btsl.pretups.processes;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.TypesI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.ProfileSetVersionVO;
import com.btsl.pretups.channel.profile.businesslogic.RetSubsMappingVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.logging.AssociateMsisdnFileProcessLog;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.ibm.icu.util.Calendar;

/*
 * ActivationSubscriberRetailerMapping.java
 * Name Date History
 * ------------------------------------------------------------------------
 * nand.sahu 24/02/2009 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright(c) 2008, Bharti Telesoft Ltd.
 * Main class for access subscriber and retailer mapping for Activation Bonus
 */
public class ActivationSubscriberRetailerMapping {
    private static final Log _logger = LogFactory.getLog(ActivationSubscriberRetailerMapping.class.getName());
    private HashMap<String, RetSubsMappingVO> _activationMappingMap = null;
    private HashMap<String, RetSubsMappingVO> _activationMappingErrorList = null;
    private HashMap<String, String> _subscriberTypeMap = new HashMap<String, String>();
    private HashMap<String, String> _networkCodeMap = new HashMap<String, String>();
    private HashMap<String, String> _userIdMap = null;// Instantiated at the
    // time of loading data in
    // memery.
    private HashMap<String, String> _setIdMapByUserId = new HashMap<String, String>();
    private HashMap<String, String> _setIdMapByCategoryCode = new HashMap<String, String>();
    private HashMap<String, ProfileSetVersionVO> _profileListBySetId = new HashMap<String, ProfileSetVersionVO>();
    private static File _currentFileObj = null;  
    private String _errorMessage = "";
    private static ProcessStatusVO _processStatusVO = null;
    private static int _totalRecords = 0;
    private static int _totalSuccessRecords = 0;
    private static int _countErrorAtDataAccess = 0;
    private static String _sourceType = "";
    private ArrayList<RetSubsMappingVO> _duplicateMappingErrorList = null;
    private static long currentMlilSec = System.currentTimeMillis();
    private static java.util.Date _currentDate = new java.util.Date(currentMlilSec);
    private java.sql.Timestamp _timeStamp = new java.sql.Timestamp(currentMlilSec);
    private PreparedStatement _isMappingPstmt = null;
    private PreparedStatement _isSameNetworkPstmt = null;
    private PreparedStatement _insertMappingPstmt = null;

    private final String CLASS_NAME = "ActivationSubscriberRetailerMapping";
    /**
     * @author nand.sahu
     * @param args
     */
    public static void main(String[] args) {

        Connection con = null;
        final ActivationSubscriberRetailerMapping activationSubscriberRetailerMapping = new ActivationSubscriberRetailerMapping();
        final String METHOD_NAME = "main";
        try {
            if (args.length != 2) {
                AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "PROCESS FAILED Arguments for [Constants file] [LogConfig file] not provided",
                    "Date = " + _currentDate);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ActivationSubscriberRetailerMapping[main]", "", "",
                    "", "Arguments for [Constants file] [LogConfig file] not provided");
                return;
            }
            final File constantsFile = Constants.validateFilePath(args[0]);
            if (!constantsFile.exists()) {
                AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "PROCESS FAILED Constants File Not Found", "Date = " + _currentDate);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ActivationSubscriberRetailerMapping[main]", "", "",
                    "", "Constants File Not Found");
                return;
            }
            final File logconfigFile = Constants.validateFilePath(args[1]);
            if (!logconfigFile.exists()) {
                AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "PROCESS FAILED Logconfig File Not Found", "Date = " + _currentDate);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ActivationSubscriberRetailerMapping[main]", "", "",
                    "", "Logconfig File Not Found");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());

            AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "PROCESS STARTED", "Current Time = " + _currentDate);
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "PROCESS FAILED Database connection not found", "Date = " + _currentDate);
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                    "ActivationSubscriberRetailerMapping[main]", "", "", "", "DATABASE Connection is NULL");
                return;
            }
        }// end try
        catch (Exception ex) {
            AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "PROCESS FAILED Error in Loading Configuration files", "Date = " + _currentDate);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ActivationSubscriberRetailerMapping[main]", "", "",
                "", "Exception at loading Constant file or ProcessLogConfig, or DB connection");
            _logger.errorTrace(METHOD_NAME, ex);
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            // Calling Process method for Process Subscriber Retailer Mapping
            // Batch process
            activationSubscriberRetailerMapping.process(con);
        } catch (Exception ex) {
            AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A",
                "Complete process has not been executed successfully. It is Intrupted due to Exception  " + ex.getMessage(), "");
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ActivationSubscriberRetailerMapping[main]", "", "",
                "", "Complete process has not been executed successfully. It is Intrupted due to Exception. Message = " + ex.getMessage());
            _logger.errorTrace(METHOD_NAME, ex);
            return;
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                if (_logger.isDebugEnabled()) {
                    _logger.error(METHOD_NAME, "Exception at closing Prepared Statement/Connection ");
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }

            if (_logger.isDebugEnabled()) {
                _logger.info(METHOD_NAME, " Exiting");
            }
            AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "PROCESS ENDED", "Current Time = " + new java.util.Date());
            ConfigServlet.destroyProcessCache();
            activationSubscriberRetailerMapping.freeMemory();
            // Free Class Object before exiting.
        }
    }

    /**
     * Perform process for Subscrier Retailer Mapping Association
     * 
     * @author nand.sahu
     * @return void
     * @throws BTSLBaseException
     */
    private void process(Connection p_con) throws BTSLBaseException {
        /************************************************************************************************************************
         * 1. This method called from main() *
         * 2. Whole process using single Database connection. *
         * 3. Before start process. update status of this process into DB to
         * avoid concurrent access. *
         * 4. Loading Retailer details, Subercriber type and Retailer Profile
         * details in HashMap once to avaoid more DB hits. *
         * 5. Process using the paging concept. *
         * 6. If there are N files for process then It will complete process in
         * N iteration. *
         * 7. Process will fail for current iteration, if Error percentage >
         * Defeined Error Percentage *
         * 8. After completion of every iteration DB commit will be perform. *
         * 9. If a File level validation failed, Process will faile for that
         * file only. *
         * 10.Uploaded file extension will not consider as case sensitive. *
         * 11.Blank line in file will be treated as one error for that file. *
         * 12.If duplicate records for Subscriber exist in file. All duplicate
         * records will be failed. *
         * 13.If few records are not valid in given file then whole process will
         * not fail. *
         * 14.Single line logs file created for view error records as well as
         * success. *
         * 15.To avoid creating multiple object of Prepared Statements. It is
         * using some Global Prepared Statements. *
         * 16.Before ending batch process Process status will be updated in
         * PROCESS_STATUS table. *
         * 17.Summay of Subscriber Retailer Activation Mapping is inserting in
         * table SUBS_ACTIVATION_SUMMARY *
         ************************************************************************************************************************/
        boolean statusOk = false;
        final String METHOD_NAME = "process";
        try {
            final ProcessBL processBL = new ProcessBL();
            _processStatusVO = processBL.checkProcessUnderProcess(p_con, ProcessI.ACTIVATION_MAPPING);// need
            // to
            // Add
            // in
            // ProcessI.ACTIVATION_MAPPING
            statusOk = _processStatusVO.isStatusOkBool();
            if (!statusOk) {
                AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "PROCESS FAILED Activation Mapping Process is in UnderProcess state, ",
                    "Date = " + _currentDate);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ActivationSubscriberRetailerMapping[process]", "",
                    "", "", "Activation Mapping Process is in UnderProcess state");
                return;
            } else {
                try {
                    p_con.commit();
                } catch (Exception ex) {
                    AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "PROCESS FAILED EXCEPTION! at commit Process Status", "Date = " + _currentDate);
                    EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "ActivationSubscriberRetailerMapping[process]", "", "", "", "EXCEPTION! at commit Process Status");
                    _logger.errorTrace(METHOD_NAME, ex);
                    return;
                }
            }
            //
            // loading Map of Prefix like series as KEY and series_type as VALUE
            loadSubscriberTypeList(p_con);
            // loading UserId and CategoryId for all non deleted Users in the
            // system.ie Key(user_id|categoryCode) value(CSA)
            loadUserIdList(p_con);
            // Loading Profile SetId by UserId
            loadAssociatedSetIdByUserId(p_con);
            // Loading Profile SetId by CategoryCode
            loadAssociatedSetIdByCategoryCode(p_con);
            // Loading Profile details in HashMap.
            loadRetailerProfileDetailes(p_con);

            // Terminating the process if Subscriber or Retailer details unable
            // to load in memory
            if (!isSubscriberRetailerDetailsLoaded()) {
                AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "PROCESS FAILED at loading either Subscriber details or Retailer detals",
                    "Date = " + new java.util.Date());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ActivationSubscriberRetailerMapping[process]", "",
                    "", "", "FAILED at loading either Subscriber details OR Retailer detals");
                return;
            }

            // Creating global prepare prepare statement.
            final boolean pstmtFlag = createGlobalPreparedStatements(p_con);

            // Process terminating if even single Prepared Statement failed
            // during creation
            if (!pstmtFlag) {
                AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "PROCESS FAILED Unable to Create Global Prepard Statements ",
                    "Date = " + new java.util.Date());
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ActivationSubscriberRetailerMapping[process]", "",
                    "", "", "PROCESS FAILED Unable to Create Global Prepard Statements");
                return;
            }

            _sourceType = Constants.getProperty("SOURCE_TYPE_FOR_ACTIVATION_PROCESS");
            if (BTSLUtil.isNullString(_sourceType)) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.UNABLE_TO_GET_SOURCE_TYPE_FOR_BATCHPROCESS);
            }

            boolean flagForWholeProcess = true, flagForSingleFile = false;
            if ("FILE".equalsIgnoreCase(_sourceType.trim())) {
                final String filePath = Constants.getProperty("ACTIVATION_MAPPING_FILE_PATH");
                final String backupFilePath = Constants.getProperty("ACTIVATION_BACKUP_MAPPING_FILE_PATH");

                if (BTSLUtil.isNullString(filePath) || BTSLUtil.isNullString(backupFilePath)) {
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.UNABLE_TO_GET_FILE_PATH_FOR_BATCHPROCESS);
                }

                final File dir = new File(filePath);
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, "File Path = " + filePath + " Backup File Path = " + filePath + " dir.getPath() = " + dir.getPath());
                }

                String txtFiles[] = {};
                int len = 0;
                if (dir.isDirectory()) {
                    // Accessing files endwith .txt(lowerCase)
                    FilenameFilter onlyExtFiles = new OnlyExt("txt");
                    final String txtFilesLowerCase[] = dir.list(onlyExtFiles);

                    // Accessing files endwith .TXT(UpperCase)
                    onlyExtFiles = new OnlyExt("TXT");
                    final String txtFilesUpperCase[] = dir.list(onlyExtFiles);
                    txtFiles = new String[txtFilesLowerCase.length + txtFilesUpperCase.length];
                    for (int i = 0; i < txtFilesLowerCase.length; i++) {
                        txtFiles[len] = txtFilesLowerCase[i];
                        len++;
                    }
                    for (int i = 0; i < txtFilesUpperCase.length; i++) {
                        txtFiles[len] = txtFilesUpperCase[i];
                        len++;
                    }

                } else {
                    AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "PROCESS FAILED Activation Mapping file path is not valid, ",
                        "File Path = " + filePath + ", Date = " + _currentDate);
                    return;
                }

                for (int i = 0; i < txtFiles.length; i++) {
                    refreshObjectForNextIteration();
                    flagForSingleFile = false; // Setting false for every time
                    // at new file
                    _currentFileObj = new File(filePath + File.separator + txtFiles[i]);
                    boolean isMovedInBackup = false;
                    AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "Iteration " + (i + 1) + " Started",
                        "File Name = " + _currentFileObj.getName() + ", Date = " + new java.util.Date());
                    final File buckupFileObj = new File(backupFilePath + File.separator + _currentFileObj.getName());
                    if (_currentFileObj.length() == 0) {
                        AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "None Records Exist in file", "");
                        isMovedInBackup = _currentFileObj.renameTo(buckupFileObj);
                        continue;
                    }
                    if (!isValideFileName(_currentFileObj)) {
                        AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "File Name is not valid", "");
                        isMovedInBackup = _currentFileObj.renameTo(buckupFileObj);
                        continue;
                    }
                    try {
                        // Calling method to access Active Subscriber Retailer
                        // mapping list
                        accessActivationMappingList();

                        // Start Batch process, It add Profile details
                        // informatin in List read from File or CRM, Then insert
                        // data into Mapping details table.
                        startBatchProcess();

                        if (getTotalRecords() == 0) {
                            AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "None Records Exist in file", "");
                            isMovedInBackup = _currentFileObj.renameTo(buckupFileObj);
                            continue;
                        }
                        // Check percentage of Errors. Process failed if Error
                        // percent > Defined Error percent.
                        if (checkErrorPercentage()) {
                            flagForSingleFile = true;// Process done
                            // successfully for Single
                            // file
                        } else {
                            try {
                                p_con.rollback();
                                writeActivationMappingLogs(false);// FALSE case
                                // will not
                                // allow to
                                // write
                                // success
                                // logs
                                AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A",
                                    "Process FAILED for this Iteration. Error Percentage > Defined Error Percentage", "");
                                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                                    "ActivationSubscriberRetailerMapping[process]", "", "", "",
                                    "Process FAILED for this Iteration. Error Percentage > Defined Error Percentage FILE = " + _currentFileObj.getName());
                            } catch (Exception ex) {
                                _logger.error(METHOD_NAME, "Exception at commit or rollback : " + ex.getMessage());
                                _logger.errorTrace(METHOD_NAME, ex);
                            }
                        }
                    } catch (BTSLBaseException bts) {
                        AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "process Intrupted due to Exception " + bts.getMessage(),
                            "[File Name = " + _currentFileObj.getName() + "] ");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                            "ActivationSubscriberRetailerMapping[process]", "", "", "",
                            "process Intrupted due to Exception " + bts.getMessage() + ", FILE = " + _currentFileObj.getName());
                        _logger.errorTrace(METHOD_NAME, bts);
                    }

                    // Moving file into backup folder and Performing commit in
                    // DB if Process done successfully for current file
                    if (flagForSingleFile) {
                        isMovedInBackup = _currentFileObj.renameTo(buckupFileObj);
                        try {
                            if (isMovedInBackup) {
                                p_con.commit();
                                AssociateMsisdnFileProcessLog
                                    .activationMappingLog(
                                        "N.A",
                                        "N.A",
                                        "N.A",
                                        "Iteration " + (i + 1) + " Ended Successfylly",
                                        "[File Name = " + _currentFileObj.getName() + ", Total Records in File = " + getTotalRecords() + ", Total Success Records = " + _totalSuccessRecords + ", Current time = " + new java.util.Date() + "] ");
                                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                                    "ActivationSubscriberRetailerMapping[process]", "", "", "", "Process Execution done Successfully for FILE = " + _currentFileObj.getName());
                                // Writing Success and failure logs in bulk
                                writeActivationMappingLogs(isMovedInBackup);// FALSE
                                // case
                                // will
                                // not
                                // allow
                                // to
                                // write
                                // success
                                // logs
                            } else {
                                p_con.rollback();
                                AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "Process FAILED for this Iteration", "[File Name = " + _currentFileObj
                                    .getName() + ", Current time = " + new java.util.Date() + "] ");
                                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                                    "ActivationSubscriberRetailerMapping[process]", "", "", "", "Process Failed DB Rollback done for FILE = " + _currentFileObj.getName());
                            }
                        } catch (Exception ex) {
                            AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "Process FAILED for this Iteration at DB commit/rollback",
                                "[File Name = " + _currentFileObj.getName() + ", Current time = " + new java.util.Date() + "] ");
                            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                                "ActivationSubscriberRetailerMapping[process]", "", "", "", "Failed at save/rollback data in DB for FILE = " + _currentFileObj.getName());
                            _logger.errorTrace(METHOD_NAME, ex);
                        }
                    } else {
                        // If Single process(For one File) has not executed
                        // successfully, it means process has not completed
                        // successfully
                        // This flag helps to update status in 'PROCESS_STATUS
                        // table.
                        flagForWholeProcess = false;
                    }
                    // Free memory now no use of that data.
                    _activationMappingMap = null;
                    _activationMappingErrorList = null;
                    _duplicateMappingErrorList = null;
                }// End of for loop
            } else if ("CRM".equalsIgnoreCase(_sourceType)) {
                final int initialRecordsForProcess = recordsCountForActivationProcess();
                final int crmActPagingSize = Integer.parseInt(Constants.getProperty("CRM_ACTIVATION_MAPPING_PAGING_SIZE"));
                final int loopForCRM = BTSLUtil.parseDoubleToInt( Math.ceil(initialRecordsForProcess / crmActPagingSize) );
                for (int i = 0; i < loopForCRM; i++) {
                    // Calling method to access Active Subscriber Retailer
                    // mapping list
                    accessActivationMappingList();
                    // Start Batch process, It add Profile details informatin in
                    // List read from File or CRM, Then insert data into Mapping
                    // details table.
                    startBatchProcess();

                }// End of loop
            }
            if (!flagForWholeProcess) {
                AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A",
                    "Complete process has not been executed successfully It is Intrupted due to Technical Problem", "");
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ActivationSubscriberRetailerMapping[process]", "",
                    "", "", "Complete process has not been executed successfully It is Intrupted due to Technical Problem");
            }
        } catch (BTSLBaseException be) {
            _logger.errorTrace(METHOD_NAME, be);
            throw new BTSLBaseException(CLASS_NAME, "accessActivationMappingList",
                "Complete process has not been executed successfully. It is Intrupted due to Exception  " + be.getMessage());
        } catch (Exception ex) {
            _logger.errorTrace(METHOD_NAME, ex);
            throw new BTSLBaseException(CLASS_NAME, "accessActivationMappingList",
                "Complete process has not been executed successfully. It is Intrupted due to Exception  " + ex.getMessage());
        } finally {
            if (statusOk) {
                try {
                    if (insertActivationMappingSummary(p_con)) {
                        p_con.commit();
                        AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "Subscriber Activation Mapping Summary inserted successfully", "");
                    } else {
                        p_con.rollback();
                        AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "None Rocords inserted in Subscriber Activaion Summary table", "");
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                            "ActivationSubscriberRetailerMapping[process]", "", "", "", "None Rocords inserted in Subscriber Activaion Summary table");
                    }
                } catch (Exception ex) {
                    AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A",
                        "Activation Mapping Summary insertion FAILED due to Exception at commit/rollback " + ex.getMessage(), "");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ActivationSubscriberRetailerMapping[process]",
                        "", "", "", "Activation Mapping Summary insertion FAILED due to Exception at commit/rollback " + ex.getMessage());
                    _logger.errorTrace(METHOD_NAME, ex);
                }
                // Code for updating process Status
                try {
                    final int updatecount = markProcessStatusAsComplete(p_con, ProcessI.ACTIVATION_MAPPING);
                    if (updatecount > 0) {
                        p_con.commit();
                    }
                } catch (BTSLBaseException bts) {
                    AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "markProcessStatusAsComplete FAILED due to Exception " + bts.getMessage(), "");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ActivationSubscriberRetailerMapping[process]",
                        "", "", "", "markProcessStatusAsComplete FAILED due to Exception " + bts.getMessage());
                    _logger.errorTrace(METHOD_NAME, bts);
                } catch (Exception ex) {
                    AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "markProcessStatusAsComplete FAILED due to Exception " + ex.getMessage(), "");
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ActivationSubscriberRetailerMapping[process]",
                        "", "", "", "markProcessStatusAsComplete FAILED due to Exception " + ex.getMessage());
                    _logger.errorTrace(METHOD_NAME, ex);
                }
                // Closing Global all Prepared Statements
                closeGlobalPreparedStatements();
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... ");
            }
        }
    }// End of process method

    /**
     * Reads data form File or CRM. File are available in server at predefined
     * path.
     * 
     * @author nand.sahu
     * @return void
     * @throws BTSLBaseException
     */
    private void accessActivationMappingList() throws BTSLBaseException {

        final ActivationMappingReader activationMappingReader = new ActivationMappingReader();
        final String METHOD_NAME = "accessActivationMappingList";
        try {
            if ("FILE".equalsIgnoreCase(_sourceType)) {
                activationMappingReader.getActivationMappingList(_currentFileObj, _activationMappingMap, _activationMappingErrorList, _duplicateMappingErrorList);
            } else if ("CRM".equalsIgnoreCase(_sourceType)) {
                activationMappingReader.getActivationMappingList(_activationMappingMap, _activationMappingErrorList, _duplicateMappingErrorList);
            }
        } catch (BTSLBaseException exp) {
            _logger.errorTrace(METHOD_NAME, exp);
            throw new BTSLBaseException(CLASS_NAME, "accessActivationMappingList", PretupsErrorCodesI.UNABLE_TO_ACCESS_ACTIVATION_MAPPING_RECORDS);
        }
    }

    /**
     * This method access one by one subcriber retailer mapping from HashMap and
     * insert into DB.
     * Insert into errorMap if insertion failed.
     * If total records are N and failed records are M (here N>M)then N-M
     * records will be inserted successfully in DB.
     * 
     * @author nand.sahu
     * @return void
     */
    private void startBatchProcess() {
        final String METHOD_NAME = "startBatchProcess()";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered ");
        }

        int count = 0;
        if (_activationMappingMap != null && _activationMappingMap.size() > 0) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("startBatchProcess() ", " Total Records for Process = " + _activationMappingMap.size());
            }
            final Set<String> subscriberKeys = _activationMappingMap.keySet();
            final Iterator<String> subscriberKeysItr = subscriberKeys.iterator();

            while (subscriberKeysItr.hasNext()) {

                final String subscriberMsisdn = subscriberKeysItr.next();
                if (_logger.isDebugEnabled()) {
                    _logger.debug("startBatchProcess() ", " < PROCESSING RECORD NUMBER >  " + ++count + ", SubscriberMsisdn =  " + subscriberMsisdn);
                }

                try {
                    final RetSubsMappingVO retSubsMappingVO = _activationMappingMap.get(subscriberMsisdn);
                    final boolean validationFlag = validateMappingDetails(retSubsMappingVO);
                    if (!validationFlag) {
                        _logger.error("startBatchProcess", "Validation for Subscriber Retailer mapping details FAILED");
                        continue;
                    }

                    // Adding Profile details in _activationMappingMap.
                    final boolean isDetailOk = makeMappingDetailsMap(retSubsMappingVO);
                    if (!isDetailOk) {
                        _logger.error("startBatchProcess", "Making Mapping detailes for Subscriber Retailer FAILED");
                        continue;
                    }

                    // Inserting Subscriber Retailer mapping into Database
                    final boolean isMappingInserted = insertSubscriberRetailerMappingDetails(getPreparedStatementForInsertMapping(), retSubsMappingVO);

                    // Adding in Error list if insertion failed due to any
                    // reason
                    if (!isMappingInserted) {
                        _errorMessage = "<FAILED> Subscriber Retailer Mapping has not been inserted in DB for this record";
                        addErrorInMap(_activationMappingMap.get(subscriberMsisdn));
                    }

                } catch (Exception exp) {
                    // Adding in Error list if insertion failed due to any
                    // reason
                    _errorMessage = "<FAILED> Subscriber Retailer Mapping has not been inserted in DB for this record";
                    addErrorInMap(_activationMappingMap.get(subscriberMsisdn));
                    _logger.errorTrace(METHOD_NAME, exp);
                }
            }// End of while block
        }// End of if block

    }// End of startBatchProcess()

    /**
     * Reads number of records to be process form CRM.
     * 
     * @author nand.sahu
     * @return int totalRecordsForProcess
     */
    private int recordsCountForActivationProcess() {
        if (_logger.isDebugEnabled()) {
            _logger.debug("recordsCountForActivationProcess()", "Entered");
        }

        // ActivationMappingReader activationMappingReader = new

        final int totalRecordsForProcess = 0;
 
        return totalRecordsForProcess;
    }// End of recordsCountForActivationProcess()

    /**
     * Takes input as current file object and validate about file name. Also
     * validate Registeration date given with file.
     * 
     * @author nand.sahu
     * @param File
     *            p_currentFileObj
     * @return boolean
     */
    private boolean isValideFileName(File p_currentFileObj) {
        boolean fileNameFlag = true;
        final String METHOD_NAME = "isValideFileName";
        try {
            if (_logger.isDebugEnabled()) {
                _logger.debug("isValideFileName()", "Entered currentFile = " + p_currentFileObj.getName());
            }

            final String fileName = p_currentFileObj.getName();
            final String definedFileName = Constants.getProperty("ACTIVATION_MAPPING_INITIAL_FILENAME");
            final int fileNameLength = Integer.parseInt(Constants.getProperty("ACTIVATION_MAPPING_FILENAME_LENGTH"));
            if (_logger.isDebugEnabled()) {
                _logger.debug("isValideFileName", "file name = " + fileName + " fileNameLength = " + fileNameLength + " islength = " + (fileName.length() > fileNameLength));
            }

            if (fileName.length() > fileNameLength) {
                _logger.error("isValideFileName", "File Name is larger than defined : ");
                fileNameFlag = false;
            } else if (!(fileName.substring(0, fileName.indexOf("_")).equals(definedFileName))) {
                _logger.error("isValideFileName", "Intial File Name is not correct. It shoult be like" + definedFileName);
                fileNameFlag = false;
            } else if (Integer.parseInt(fileName.substring(fileName.lastIndexOf("_") + 1, fileName.lastIndexOf("."))) > 9999) {
                _logger.error("isValideFileName", "Number of file can not greater than 9999");
                fileNameFlag = false;
            }
            final String ddmmyy = fileName.substring(fileName.indexOf("_") + 1, fileName.lastIndexOf("_"));
            final Calendar cal = BTSLDateUtil.getInstance();
            // Params are as (YYMMDD)
            cal.set(Integer.parseInt(ddmmyy.substring(5)), Integer.parseInt(ddmmyy.substring(3, 4)), Integer.parseInt(ddmmyy.substring(0, 2)));
            return fileNameFlag;
        } catch (Exception ex) {
            _logger.error("isValideFileName", "File Name is not valid " + ex.getMessage());
            _logger.errorTrace(METHOD_NAME, ex);
            fileNameFlag = false;
            return fileNameFlag;
        } finally {

            if (_logger.isDebugEnabled()) {
                _logger.debug("isValideFileName()", "Exited fileNameFlag = " + fileNameFlag);
            }
        }

    }// End of isValideFileName()

    /**
     * Perform Subscriber and Retailer MSISDN details validation,
     * 
     * @author nand.sahu
     * @param retailerMsisdn
     * @param subscriberMsisdn
     * @return boolean
     */
    private boolean validateMappingDetails(RetSubsMappingVO p_retSubsMappingVO) {
        /**
         * This method perform fallowing validations
         * 1. Numeric check, Maximum and Minimum length for subscriber and
         * retailer msisdn
         * 2. Given Subscriber Series and type with e-recharge system
         * 3. Retailer and Subscriner MSISDN must belong from same Network.
         * 4. Retailer must exist in e-recharge system.
         * 5. Subscriber should not mapped with another Retailer.
         * 6. return FALSE means validation failed
         **/
        final String METHOD_NAME = "validateMappingDetails";
        String categoryCode = null;

        // Validate Max and Min Length for MSISDN
        try {
            Long.parseLong(p_retSubsMappingVO.getRetailerMsisdn());
        } catch (Exception e) {
            _logger.error("validateMappingDetails", "Retailer MSISDN is not numeric ");
            _errorMessage = "<FAILED> Retailer MSISDN is non numeric";
            addErrorInMap(_activationMappingMap.get(p_retSubsMappingVO.getSubscriberMsisdn()));
            _logger.errorTrace(METHOD_NAME, e);
            return false;
        }
        try {
            Long.parseLong(p_retSubsMappingVO.getSubscriberMsisdn());
        } catch (Exception e) {
            _logger.error("validateMappingDetails", "Subscriber MSISDN is not numeric ");
            _errorMessage = "<FAILED> Subscriber MSISDN is non numeric";
            addErrorInMap(_activationMappingMap.get(p_retSubsMappingVO.getSubscriberMsisdn()));
            _logger.errorTrace(METHOD_NAME, e);
            return false;
        }

        // Validate Max and Min Length for MSISDN
        if (p_retSubsMappingVO.getRetailerMsisdn().length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue() || p_retSubsMappingVO.getRetailerMsisdn().length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue()) {
            _logger.error("validateMappingDetails", "Retailer MSISDN length is not correct ");
            _errorMessage = "<FAILED> Retailer MSISDN length is invalid";
            addErrorInMap(_activationMappingMap.get(p_retSubsMappingVO.getSubscriberMsisdn()));
            return false;
        } else if (p_retSubsMappingVO.getSubscriberMsisdn().length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue() || p_retSubsMappingVO.getSubscriberMsisdn().length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue()) {
            _logger.error("validateMappingDetails", "Subscriber MSISDN length is not correct");
            _errorMessage = "<FAILED> Subscriber MSISDN length is invalid";
            addErrorInMap(_activationMappingMap.get(p_retSubsMappingVO.getSubscriberMsisdn()));
            return false;
        }

        final String retailerMSISDNPrefix = PretupsBL.getMSISDNPrefix(p_retSubsMappingVO.getRetailerMsisdn());
        final String subscriberMSISDNPrefix = PretupsBL.getMSISDNPrefix(p_retSubsMappingVO.getSubscriberMsisdn());

        // Subscriber Series and Subscriber type must match with e-recharge
        // system.
        if (!(_subscriberTypeMap.containsKey(subscriberMSISDNPrefix + "_" + p_retSubsMappingVO.getSubscriberType()))) {
            _errorMessage = "<FAILED> Subscriber is not valid in the System";
            addErrorInMap(_activationMappingMap.get(p_retSubsMappingVO.getSubscriberMsisdn()));
            return false;
        }

        // Retailer and Subscriner MSISDN must belong from same location.
        final boolean seriesFlag = isSeriesExistInSameNetwork(getPreparedStatementForNetworkCheck(), retailerMSISDNPrefix, subscriberMSISDNPrefix);
        if (!seriesFlag) {
            _logger.error("validateMappingDetails", "Retailer and Subscriber does not belongs from same network ");
            _errorMessage = "<FAILED> Retailer and Subscriber does not belongs from same network";
            addErrorInMap(_activationMappingMap.get(p_retSubsMappingVO.getSubscriberMsisdn()));
            return false;
        }
        // Generate Error if given retailer does not exist in the system.
        try {
            p_retSubsMappingVO.setNetworkCode(_networkCodeMap.get(subscriberMSISDNPrefix));
            final String userInfo = _userIdMap.get(p_retSubsMappingVO.getRetailerMsisdn());
            if (userInfo != null) {
                p_retSubsMappingVO.setRetailerId(userInfo.substring(0, userInfo.indexOf("|")));
                categoryCode = userInfo.substring(userInfo.indexOf("|") + 1);
            } else {
                _errorMessage = "<FAILED> Retailer details not availeble";
                addErrorInMap(_activationMappingMap.get(p_retSubsMappingVO.getSubscriberMsisdn()));
                return false;
            }
        } catch (Exception ee) {
            _errorMessage = "<FAILED> Retailer details not availeble";
            addErrorInMap(_activationMappingMap.get(p_retSubsMappingVO.getSubscriberMsisdn()));
            _logger.errorTrace(METHOD_NAME, ee);
            return false;
        }

        // First Search Profile details on User basis then search in
        // memory(HashMap) else hitting the database.
        if (_setIdMapByUserId.containsKey(p_retSubsMappingVO.getRetailerId())) {
            p_retSubsMappingVO.setSetID(_setIdMapByUserId.get(p_retSubsMappingVO.getRetailerId()));
        } else if (_setIdMapByCategoryCode.containsKey(categoryCode + "|" + p_retSubsMappingVO.getNetworkCode())) {
            p_retSubsMappingVO.setSetID(_setIdMapByCategoryCode.get(categoryCode + "|" + p_retSubsMappingVO.getNetworkCode()));
        }

        // Validate Max and Min Length for MSISDN
        if (isSubscriberMappingExist(getPreparedStatementForMapping(), p_retSubsMappingVO.getSubscriberMsisdn())) {
            _logger.error("validateMappingDetails", "Subscriber mapping ALREADY EXIST ");
            _errorMessage = "<FAILED> Subscriber Mapping already exist";
            addErrorInMap(_activationMappingMap.get(p_retSubsMappingVO.getSubscriberMsisdn()));
            return false;
        }

        return true;
    }// End of validateMappingDetails()

    /**
     * Returns FALSE if Subscriber activation mapping already exist with some
     * another Retailer in DB.
     * 
     * @author nand.sahu
     * @param PreparedStatement
     *            p_pstmtSelect
     * @param String
     *            p_subscriberMsisdn
     * @return boolean
     */
    private boolean isSubscriberMappingExist(PreparedStatement p_pstmtSelect, String p_subscriberMsisdn) {
        final String METHOD_NAME = "isSubscriberMappingExist";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered p_subscriberMsisdn = " + p_subscriberMsisdn);
        }
        ResultSet rs = null;
        try {
            p_pstmtSelect.clearParameters();
            p_pstmtSelect.setString(1, p_subscriberMsisdn);
            p_pstmtSelect.setString(2, PretupsI.NO);
            rs = p_pstmtSelect.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException sqle) {
            _logger.errorTrace(METHOD_NAME, sqle);
            return true;
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            return true;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
        }// end of finally
        return false;
    }// End of isSubscriberMappingExist()

    /**
     * Retrun FALSE if Reailer and Subcriber does not belongs from same Network.
     * 
     * @author nand.sahu
     * @param PreparedStatement
     *            p_pstmtSelect
     * @param String
     *            p_retailerSeries
     * @param String
     *            p_subscriberSeries
     * @return boolean
     */
    private boolean isSeriesExistInSameNetwork(PreparedStatement p_pstmtSelect, String p_retailerSeries, String p_subscriberSeries) {

   
        ResultSet rs = null;
        int selectCount = 0;
        String networkCode = "";
        final String METHOD_NAME = "isSeriesExistInSameNetwork";
        try {
            p_pstmtSelect.clearParameters();
            p_pstmtSelect.setString(1, p_retailerSeries);
            p_pstmtSelect.setString(2, p_subscriberSeries);
            p_pstmtSelect.setString(3, PretupsI.YES);
            p_pstmtSelect.setString(4, PretupsI.YES);
            rs = p_pstmtSelect.executeQuery();
            while (rs.next()) {
                networkCode = rs.getString("networkcode");
                selectCount++;
            }
            if (selectCount == 1) {
                if (!_networkCodeMap.containsKey(p_subscriberSeries)) {
                    _networkCodeMap.put(p_subscriberSeries, networkCode);
                }
                return true;
            } else {
                return false;
            }
        } catch (SQLException sqle) {
            _logger.errorTrace(METHOD_NAME, sqle);
            return false;
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
        }// end of finally
    }// End of isSeriesExistInSameNetwork()

    /**
     * Adding some other details like Profile details and subscriber type in
     * HashMap for Subscriber Retailer Mapping association.
     * 
     * @author nand.sahu
     * @param RetSubsMappingVO
     *            p_retSubsMappingVO
     * @return boolean
     */
    private boolean makeMappingDetailsMap(RetSubsMappingVO p_retSubsMappingVO) {


        ProfileSetVersionVO profileSetVersionVO = null;
        final String METHOD_NAME = "makeMappingDetailsMap";
        try {
            if (!BTSLUtil.isNullString(p_retSubsMappingVO.getSetID()) && _profileListBySetId.containsKey(p_retSubsMappingVO.getSetID())) {
                profileSetVersionVO = _profileListBySetId.get(p_retSubsMappingVO.getSetID());
            }

            if (profileSetVersionVO != null) {
                p_retSubsMappingVO.setSetID(profileSetVersionVO.getSetId());
                p_retSubsMappingVO.setVersion(profileSetVersionVO.getVersion());
                p_retSubsMappingVO.setBonusDureation(BTSLUtil.parseLongToInt(profileSetVersionVO.getBonusDuration()) );
            }

            p_retSubsMappingVO.setActivationBonusGiven(PretupsI.NO);// Initially
            // set as N
            // after
            // discussion
            // with
            // Ankit.
            p_retSubsMappingVO.setStatus(PretupsI.YES);// By default status of
            
            p_retSubsMappingVO.setCreatedBy(TypesI.SYSTEM_USER);
            p_retSubsMappingVO.setCreatedOn(_currentDate);
            p_retSubsMappingVO.setModifiedBy(TypesI.SYSTEM_USER);
            p_retSubsMappingVO.setModifiedOn(_currentDate);
            p_retSubsMappingVO.setApprovedBy(TypesI.SYSTEM_USER);
            p_retSubsMappingVO.setApprovedOn(_currentDate);

            // Taking the decision on the basis of calculation for expiry date
            // done successfully or not
            if (!calculateActivationExpiryDate(p_retSubsMappingVO)) {
                _errorMessage = "<FAILED> Error at calculation of Activation Expiry Date";
                addErrorInMap(_activationMappingMap.get(p_retSubsMappingVO.getSubscriberMsisdn()));
                return false;
            }
        } catch (Exception e) {
            // Copying Mapping records form success Map into Error Map
            _errorMessage = "<FAILED> Exception occured at making Mapping details";
            addErrorInMap(_activationMappingMap.get(p_retSubsMappingVO.getSubscriberMsisdn()));
            _logger.errorTrace(METHOD_NAME, e);
            return false;
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting ");
            }
        }// end of finally
        return true;
    }// End of makeMappingDetailsMap()

    /**
     * Load Profile setId in HashMap by UserId
     * 
     * @author nand.sahu
     * @param Connection
     *            p_con
     * @return String setId
     * @throws BTSLBaseException
     */
    private void loadAssociatedSetIdByUserId(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "loadAssociatedSetIdByUserId";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered:");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final String sqlSelect = "SELECT user_id, set_id FROM user_oth_profiles WHERE PROFILE_TYPE =?";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.PROFILE_TYPE_ACTIVATION);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                _setIdMapByUserId.put(rs.getString("user_id"), rs.getString("set_id"));
            }

        } catch (SQLException sqe)

        {
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ActivationSubscriberRetailerMapping[loadAssociatedSetIdByUserId]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.FAILED_AT_LOADING_PROFILE_SETID_BY_USERID);
        } catch (Exception ex) {
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                "ActivationSubscriberRetailerMapping[loadAssociatedSetIdByUserId]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.FAILED_AT_LOADING_PROFILE_SETID_BY_USERID);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting: loadAssociatedSetIdByUserId size=" + _setIdMapByUserId.size());
            }
        }
    }// End of loadAssociatedSetIdByUserId()

    /**
     * Load Profile setId in HashMap by Category Code.
     * 
     * @author nand.sahu
     * @param Connection
     *            p_con
     * @return String setId
     * @throws BTSLBaseException
     */
    private void loadAssociatedSetIdByCategoryCode(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "loadAssociatedSetIdByCategoryCode";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered:");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final String sqlSelect = "SELECT srv_class_or_category_code,network_code,set_id  FROM PROFILE_MAPPING WHERE  PROFILE_TYPE =? AND IS_DEFAULT=? ";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.PROFILE_TYPE_ACTIVATION);
            pstmt.setString(2, PretupsI.YES);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                _setIdMapByCategoryCode.put(rs.getString("srv_class_or_category_code") + "|" + rs.getString("network_code"), rs.getString("set_id"));
            }

        } catch (SQLException sqe)

        {
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ActivationSubscriberRetailerMapping[loadAssociatedSetIdByCategoryCode]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.FAILED_AT_LOADING_PROFILE_SETID_BY_CATEGORYCODE);
        } catch (Exception ex) {
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                "ActivationSubscriberRetailerMapping[loadAssociatedSetIdByCategoryCode]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.FAILED_AT_LOADING_PROFILE_SETID_BY_CATEGORYCODE);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting: loadAssociatedSetIdByCategoryCode size=" + _setIdMapByCategoryCode.size());
            }
        }
    }// End of loadAssociatedSetIdByCategoryCode()

    /**
     * Inserting Subscriber Retailer Mapping Details in DB.
     * 
     * @author nand.sahu
     * @param PreparedStatement
     *            pstmt
     * @param RetSubsMappingVO
     *            p_retSubsMappingVO
     * @return boolean
     */
    private boolean insertSubscriberRetailerMappingDetails(PreparedStatement p_pstmt, RetSubsMappingVO p_retSubsMappingVO) {
        final String METHOD_NAME = "insertSubscriberRetailerMappingDetails";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered RetailerId = " + p_retSubsMappingVO.getRetailerId());
        }
        int index = 0;
        try {
            if (p_retSubsMappingVO == null) {
                return false;
            } else {
                p_pstmt.clearParameters();
                p_pstmt.setString(++index, p_retSubsMappingVO.getRetailerId());
                p_pstmt.setString(++index, p_retSubsMappingVO.getSubscriberMsisdn());
                p_pstmt.setString(++index, p_retSubsMappingVO.getSubscriberType());
                // If set is null then it means Activation profile is not
                // defined.
                if (BTSLUtil.isNullString(p_retSubsMappingVO.getSetID())) {
                    p_pstmt.setString(++index, null);
                    p_pstmt.setString(++index, null);
                } else {
                    p_pstmt.setString(++index, p_retSubsMappingVO.getSetID());
                    p_pstmt.setString(++index, p_retSubsMappingVO.getVersion());
                }

                if (p_retSubsMappingVO.getExpiryDate() != null) {
                    p_pstmt.setDate(++index, BTSLUtil.getSQLDateFromUtilDate(p_retSubsMappingVO.getExpiryDate()));
                } else {
                    p_pstmt.setDate(++index, null);
                }

                p_pstmt.setDate(++index, BTSLUtil.getSQLDateFromUtilDate(p_retSubsMappingVO.getRegisteredOn()));
                p_pstmt.setString(++index, p_retSubsMappingVO.getActivationBonusGiven());
                p_pstmt.setString(++index, p_retSubsMappingVO.getStatus());
                p_pstmt.setTimestamp(++index, _timeStamp);
                p_pstmt.setString(++index, p_retSubsMappingVO.getCreatedBy());
                p_pstmt.setTimestamp(++index, _timeStamp);
                p_pstmt.setString(++index, p_retSubsMappingVO.getModifiedBy());
                p_pstmt.setString(++index, p_retSubsMappingVO.getNetworkCode());
                p_pstmt.setDate(++index, BTSLUtil.getSQLDateFromUtilDate(p_retSubsMappingVO.getApprovedOn()));
                p_pstmt.setString(++index, p_retSubsMappingVO.getApprovedBy());

                final int count = p_pstmt.executeUpdate();
                if (count > 0) {
                    _totalSuccessRecords = _totalSuccessRecords + 1;
                    if (_logger.isDebugEnabled()) {
                        _logger.debug(METHOD_NAME, "Inserted mapping details in DB for SUBSCRIBER = " + p_retSubsMappingVO.getSubscriberMsisdn());
                    }
                    return true;
                }
            }

        } catch (SQLException sqle) {
            _logger.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ActivationSubscriberRetailerMapping[insertSubscriberRetailerMappingDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                "ActivationSubscriberRetailerMapping[insertSubscriberRetailerMappingDetails]", "", "", "", "Exception:" + e.getMessage());
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("Exiting", METHOD_NAME);
            }
        }// end of finally
        return false;
    }// End of insertSubscriberRetailerMappingDetails()

    /**
     * Load all series and series type in memory
     * 
     * @author nand.sahu
     * @param Connection
     *            p_con
     * @return void
     * @throws BTSLBaseException
     */
    private void loadSubscriberTypeList(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "loadSubscriberTypeList";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered:");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final String sqlSelect = "SELECT series, series_type FROM network_prefixes WHERE status <> ?";

        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.NO);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                final String seriesWithType = rs.getString("series_type").toUpperCase();
                _subscriberTypeMap.put(rs.getString("series") + "_" + seriesWithType, seriesWithType);
            }

        } catch (SQLException sqe) {
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ActivationSubscriberRetailerMapping[loadSubscriberTypeList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.UNABLE_TO_LOAD_SERIES_DETAILS);
        } catch (Exception ex) {
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                "ActivationSubscriberRetailerMapping[loadSubscriberTypeList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.UNABLE_TO_LOAD_SERIES_DETAILS);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting: loadSubscriberTypeList size=" + _subscriberTypeMap.size());
            }
        }
    }// End of loadSubscriberTypeList()

    /**
     * load Channel user details and store in HashMap. Key(subscriber MSISDN)
     * Value(User_id|Category_Id)
     * 
     * @author nand.sahu
     * @param Connection
     *            p_con
     * @return void
     * @throws BTSLBaseException
     */
    private void loadUserIdList(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "loadUserIdList";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered: ");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        _userIdMap = new HashMap<String, String>();
        final String sqlSelect = "SELECT U.user_id, U.category_code, UP.msisdn FROM user_phones UP, users U WHERE U.status <> ? AND U.user_id = UP.user_id";

        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, "N");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                _userIdMap.put(rs.getString("msisdn"), rs.getString("user_id") + "|" + rs.getString("category_code"));
            }

        } catch (SQLException sqe) {
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationSubscriberRetailerMapping[loadUserIdList]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.UNABLE_TO_LOAD_USERS_DETAILS);
        } catch (Exception ex) {
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ActivationSubscriberRetailerMapping[loadUserIdList]",
                "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.UNABLE_TO_LOAD_USERS_DETAILS);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting: _userIdMap size=" + _userIdMap.size());
            }
        }
    }// End of loadUserIdList()

    /**
     * load all current Retailer Profile details .
     * 
     * @author nand.sahu
     * @param Connection
     *            p_con
     * @throws BTSLBaseException
     * @return void
     */
    private void loadRetailerProfileDetailes(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "loadRetailerProfileDetailes";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered");
        }

        ProfileSetVersionVO profileSetVersionVO = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            final StringBuilder profileBuff = new StringBuilder("SELECT PSV.set_id, PSV.one_time_bonus, PSV.VERSION, PSV.bonus_duration ");
            profileBuff.append("FROM profile_set_version PSV WHERE applicable_from = ");
            profileBuff.append("(SELECT MAX(applicable_from)FROM profile_set_version WHERE  applicable_from<=? AND status = ? AND set_id=PSV.set_id)");
            final String profileDetails = profileBuff.toString();
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "QUERY sqlSelect=" + profileDetails);
            }
            try {
                pstmt = p_con.prepareStatement(profileDetails);
                pstmt.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
                pstmt.setString(2, PretupsI.YES);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    profileSetVersionVO = new ProfileSetVersionVO();
                    final String setId = rs.getString("set_id");
                    profileSetVersionVO.setSetId(setId);
                    profileSetVersionVO.setOneTimeBonus(rs.getLong("one_time_bonus"));
                    profileSetVersionVO.setVersion(rs.getString("version"));
                    profileSetVersionVO.setBonusDuration(rs.getLong("bonus_duration"));

                    _profileListBySetId.put(setId, profileSetVersionVO);
                }

            } catch (SQLException sqe) {
                _logger.errorTrace(METHOD_NAME, sqe);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ActivationSubscriberRetailerMapping[loadRetailerProfileDetailes]", "", "", "", "SQL Exception:" + sqe.getMessage());
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.UNABLE_TO_LOAD_PROFILE_DETAILS);
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                    "ActivationSubscriberRetailerMapping[loadRetailerProfileDetailes]", "", "", "", "Exception:" + ex.getMessage());
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.UNABLE_TO_LOAD_PROFILE_DETAILS);
            }
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting: _profileListBySetId size=" + _profileListBySetId.size());
            }
        }
    }// End of loadRetailerProfileDetailes()

    /**
     * Calculate Expiry date on the basis of Registeration on and Bonus Duration
     * 
     * @author nand.sahu
     * @param RetSubsMappingVO
     *            p_retSubsMappingVO
     * @return boolean
     */
    private boolean calculateActivationExpiryDate(RetSubsMappingVO p_retSubsMappingVO) {
        final String METHOD_NAME = "calculateActivationExpiryDate";
        if (_logger.isDebugEnabled()) {
            _logger.debug("calculateActivationExpiryDate()", "Entered with bonus duration=" + p_retSubsMappingVO.getBonusDureation());
        }
        try {
            if (p_retSubsMappingVO.getBonusDureation() > 0) {
                p_retSubsMappingVO.setExpiryDate(BTSLUtil.addDaysInUtilDate(p_retSubsMappingVO.getRegisteredOn(), p_retSubsMappingVO.getBonusDureation()));
                
            }

        } catch (Exception ex) {
            _logger.error("calculateActivationExpiryDate()",
                "Exception Registeration_date = " + p_retSubsMappingVO.getRegisteredOn() + " Bonus Duration = " + p_retSubsMappingVO.getBonusDureation());
            _logger.errorTrace(METHOD_NAME, ex);
            return false;
        }
        return true;
    }// End of calculateActivationExpiryDate()

    /**
     * Adding Error from successMap into ErrorMap
     * 
     * @author nand.sahu
     * @param RetSubsMappingVO
     *            p_retSubsMappingVO
     * @return boolean
     */
    public boolean addErrorInMap(RetSubsMappingVO p_retSubsMappingVO) {
        final String METHOD_NAME = "addErrorInMap";
        if (_logger.isDebugEnabled()) {
            _logger.debug("addErrorInMap()", "Entered:: Adding Error in ErrorMap");
        }
        try {
            p_retSubsMappingVO.setMessage(_errorMessage);
            _activationMappingErrorList.put(p_retSubsMappingVO.getSubscriberMsisdn(), p_retSubsMappingVO);
        } catch (Exception ex) {
            _logger.error("addErrorInMap", "Exception at adding error in ErrorMap " + ex.getMessage());
            AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "Unable to add error in HashMap", "Subscriber_MSISDN = " + BTSLUtil
                .NullToString(p_retSubsMappingVO.getSubscriberMsisdn()) + ", _errorMessage = " + _errorMessage);
            _logger.errorTrace(METHOD_NAME, ex);
            return false;
        }
        return true;
    }// End of addErrorInMap()

    /**
     * Remove error records from success Map and write success and fail logs in
     * file for further reference.
     * 
     * @author nand.sahu
     * @return boolean p_isWriteSuccessLog
     * @return void
     */
    private void writeActivationMappingLogs(boolean p_isWriteSuccessLog) {
        final String METHOD_NAME = "writeActivationMappingLogs";
        try {
            if (_logger.isDebugEnabled()) {
                _logger.debug("writeActivationMappingLogs()", "Entered");
            }
            if (_activationMappingErrorList != null && _activationMappingErrorList.size() > 0) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("writeActivationMappingLogs()", "Writing Error logs");
                }
                final Set<String> subscriberKeys = _activationMappingErrorList.keySet();
                final Iterator<String> subscriberKeysItr = subscriberKeys.iterator();
                while (subscriberKeysItr.hasNext()) {
                    final String subscriberMsisdn = subscriberKeysItr.next();
                    try {
                        _activationMappingMap.remove(subscriberMsisdn);
                    } catch (Exception ex) {
                        _logger.error("writeActivationMappingLogs", "Exception at Removing FAILED records from Success MAP");
                        _logger.errorTrace(METHOD_NAME, ex);
                    }
                    final RetSubsMappingVO retSubsMappingVO = _activationMappingErrorList.get(subscriberMsisdn);
                    AssociateMsisdnFileProcessLog.activationMappingLog(BTSLUtil.NullToString(retSubsMappingVO.getRetailerId()), BTSLUtil.NullToString(retSubsMappingVO
                        .getRetailerMsisdn()), BTSLUtil.NullToString(retSubsMappingVO.getSubscriberMsisdn()), BTSLUtil.NullToString(retSubsMappingVO.getMessage()), "");
                }
            }// End of _activationMappingErrorList if block

            if (_duplicateMappingErrorList != null && _duplicateMappingErrorList.size() > 0) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("writeActivationMappingLogs()", "Writing Duplicate Error logs");
                }
                for (int i = 0; i < _duplicateMappingErrorList.size(); i++) {
                    final RetSubsMappingVO retSubsMappingVO = _duplicateMappingErrorList.get(i);
                    AssociateMsisdnFileProcessLog.activationMappingLog(BTSLUtil.NullToString(retSubsMappingVO.getRetailerId()), BTSLUtil.NullToString(retSubsMappingVO
                        .getRetailerMsisdn()), BTSLUtil.NullToString(retSubsMappingVO.getSubscriberMsisdn()), BTSLUtil.NullToString(retSubsMappingVO.getMessage()), "");
                }
            }// End of _duplicateMappingErrorList if block

            if (_activationMappingMap != null && _activationMappingMap.size() > 0 && p_isWriteSuccessLog) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("writeActivationMappingLogs()", "Writing Success logs");
                }
                final Set<String> subscriberKeys = _activationMappingMap.keySet();
                final Iterator<String> subscriberKeysItr = subscriberKeys.iterator();
                while (subscriberKeysItr.hasNext()) {
                    final String subscriberMsisdn = subscriberKeysItr.next();
                    final RetSubsMappingVO retSubsMappingVO = _activationMappingMap.get(subscriberMsisdn);
                    AssociateMsisdnFileProcessLog.activationMappingLog(BTSLUtil.NullToString(retSubsMappingVO.getRetailerId()), BTSLUtil.NullToString(retSubsMappingVO
                        .getRetailerMsisdn()), BTSLUtil.NullToString(retSubsMappingVO.getSubscriberMsisdn()), "SUCCESS", "");
                }
            }// End of _activationMappingMap if block
        } catch (Exception ex) {
            AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "Unable to write complete logs due to EXCPTION!", "");
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                "ActivationSubscriberRetailerMapping[writeActivationMappingLogs]", "", "", "", "Unable to write complete logs due to EXCPTION!");
            _logger.errorTrace(METHOD_NAME, ex);
        }
    }

    /**
     * This method update the process status as Complete.
     * 
     * @param Connection
     *            p_con
     * @param String
     *            p_processId
     * @throws BTSLBaseException
     * @return int updateCount
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
        _processStatusVO.setExecutedUpto(currentDate);
        _processStatusVO.setExecutedOn(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetail(p_con, _processStatusVO);
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ActivationBonusRedemption[markProcessStatusAsComplete]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("ActivationBonusRedemption", METHOD_NAME, PretupsErrorCodesI.FALED_AT_PROCESS_STATUS_COMPLETE);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }// End of markProcessStatusAsComplete()

    /**
     * Insert summay for Subscriber Retaielr Activatin Mapping Summary.
     * 
     * @param Connection
     *            p_con
     * @param String
     *            p_processId
     * @throws BTSLBaseException
     * @return int updateCount
     */
    private boolean insertActivationMappingSummary(Connection p_con) {
        final String METHOD_NAME = "insertActivationMappingSummary";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered: ");
        }

        PreparedStatement pstmt = null, p_selectPstmt = null, p_updatePstmt = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs = null;
        RetSubsMappingVO retSubsMappingVO = null;
        int insertedCount = 0, updateCount = 0;
        final java.util.ArrayList<RetSubsMappingVO> activatedSubscriberList = new java.util.ArrayList<RetSubsMappingVO>();
        final String sqlSelect = "SELECT  user_id ,COUNT(1) number_of_activated_subscriber,registered_on FROM act_bonus_subs_mapping WHERE to_char(created_on, 'dd/mm/yy hh24:mi:ss') = ? GROUP BY user_id, registered_on ";

        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Summary select QUERY " + sqlSelect);
        }

        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, BTSLUtil.getDateTimeStringFromDate(_currentDate, PretupsI.TIMESTAMP_DATESPACEHHMMSS));
            rs = pstmt.executeQuery();
            while (rs.next()) {
                retSubsMappingVO = new RetSubsMappingVO();
                retSubsMappingVO.setRetailerId(rs.getString("user_id"));
                retSubsMappingVO.setNoOfActivatedSubs(rs.getLong("number_of_activated_subscriber"));
                retSubsMappingVO.setRegisteredOn(BTSLUtil.getUtilDateFromSQLDate((rs.getDate("registered_on"))));
                activatedSubscriberList.add(retSubsMappingVO);
            }
            if (activatedSubscriberList.size() > 0) {
                final String sqlUpdateSelect = "SELECT  activated_users FROM subs_activation_summary WHERE activation_date = ? AND user_id = ? ";
                if (_logger.isDebugEnabled()) {
                    _logger.debug("insertActivationMappingSummary()", "sqlUpdateSelect = " + sqlUpdateSelect);
                }

                final String updateSummaryQuery = "UPDATE subs_activation_summary SET activated_users = ? WHERE activation_date =? AND user_id = ? ";
                if (_logger.isDebugEnabled()) {
                    _logger.debug("insertActivationMappingSummary()", "updateSummaryQuery = " + updateSummaryQuery);
                }

                p_selectPstmt = p_con.prepareStatement(sqlUpdateSelect);
                p_updatePstmt = p_con.prepareStatement(updateSummaryQuery);

                final String insertSummaryQuery = "INSERT INTO subs_activation_summary (user_id, activated_users, activation_date) VALUES (?,?,?)";
                if (_logger.isDebugEnabled()) {
                    _logger.debug("insertActivationMappingSummary()", "insertSummaryQuery = " + insertSummaryQuery);
                }

                pstmt1 = p_con.prepareStatement(insertSummaryQuery);
                int listSizes = activatedSubscriberList.size();
                for (int i = 0; i <listSizes ; i++) {
                    final RetSubsMappingVO activatedMappingVO = activatedSubscriberList.get(i);
                    updateCount = updateSummaryIfAlreadyExist(p_selectPstmt, p_updatePstmt, activatedMappingVO);
                    if (updateCount == 0) {
                        pstmt1.setString(1, activatedMappingVO.getRetailerId());
                        pstmt1.setLong(2, activatedMappingVO.getNoOfActivatedSubs());
                        pstmt1.setDate(3, BTSLUtil.getSQLDateFromUtilDate(activatedMappingVO.getRegisteredOn()));
                        insertedCount = pstmt1.executeUpdate();
                        pstmt1.clearParameters();
                    }
                }
            }// End of if block for check inserting summary
            if (insertedCount > 0 || updateCount > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException sqe) {
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ActivationSubscriberRetailerMapping[insertActivationMappingSummary]", "", "", "", "SQL Exception:" + sqe.getMessage());
            return false;
        } catch (Exception ex) {
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                "ActivationSubscriberRetailerMapping[insertActivationMappingSummary]", "", "", "", "Exception:" + ex.getMessage());
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt1 != null) {
                    pstmt1.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (p_selectPstmt != null) {
                    p_selectPstmt.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (p_updatePstmt != null) {
                    p_updatePstmt.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting: Number of inserted rows in SUMMAY table=" + activatedSubscriberList.size());
            }
        }

    }// End of insertActivationMappingSummary()

    /**
     * Update Number of records if Retailer exist already in summary table for
     * given date.
     * 
     * @author nand.sahu
     * @param p_selectPstmt
     * @param p_updatePstmt
     * @param p_activatedMappingVO
     * @return updateCount
     * @throws BTSLBaseException
     */
    private int updateSummaryIfAlreadyExist(PreparedStatement p_selectPstmt, PreparedStatement p_updatePstmt, RetSubsMappingVO p_activatedMappingVO) {
        final String METHOD_NAME = "updateSummaryIfAlreadyExist";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered: ");
        }
        ResultSet rs = null;
        int updateCount = 0;
        try {
            long noOfTxn = 0;
            if (p_selectPstmt != null) {
                p_selectPstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_activatedMappingVO.getRegisteredOn()));
                p_selectPstmt.setString(2, p_activatedMappingVO.getRetailerId());
                rs = p_selectPstmt.executeQuery();
            }
            if (rs != null && rs.next()) {
                noOfTxn = rs.getLong("activated_users");

                p_updatePstmt.setLong(1, (p_activatedMappingVO.getNoOfActivatedSubs() + noOfTxn));
                p_updatePstmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(p_activatedMappingVO.getRegisteredOn()));
                p_updatePstmt.setString(3, p_activatedMappingVO.getRetailerId());
                updateCount = p_updatePstmt.executeUpdate();
                p_updatePstmt.clearParameters();
            }
            if (p_selectPstmt != null) {
                p_selectPstmt.clearParameters();
            }
            return updateCount;
        } catch (SQLException sqe) {
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "ActivationSubscriberRetailerMapping[updateSummaryIfAlreadyExist]", "", "", "", "SQL Exception:" + sqe.getMessage());
            return -1;
        } catch (Exception ex) {
            _logger.errorTrace(METHOD_NAME, ex);
            ;
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                "ActivationSubscriberRetailerMapping[updateSummaryIfAlreadyExist]", "", "", "", "Exception:" + ex.getMessage());
            return -1;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting: updateCount" + updateCount);
            }
        }

    }// End of updateSummaryIfAlreadyExist()

    /**
     * Checks Error percentage. Return false if Error Percentage > Defined Error
     * Percentage
     * 
     * @author nand.sahu
     * @return boolean
     */
    private boolean checkErrorPercentage() {
        final String METHOD_NAME = "checkErrorPercentage";
        if (_logger.isDebugEnabled()) {
            _logger.debug("checkErrorPercentage()", "Entered");
        }
        int definedErrorPercentage = 0;
        try {
            definedErrorPercentage = Integer.parseInt(Constants.getProperty("MAX_ERROR_PERCENTAGE_ALLOWED_FOR_ACTIVATION_PROCESS"));
            if (_activationMappingMap != null && _activationMappingErrorList != null) {
                if (_logger.isDebugEnabled()) {
                    _logger
                        .debug(
                            "checkErrorPercentage()",
                            "total Records = " + ActivationSubscriberRetailerMapping.getTotalRecords() + ", ErrorMap Size = " + _activationMappingErrorList.size() + ", getCountErrorAtDataAccess() = " + ActivationSubscriberRetailerMapping
                                .getCountErrorAtDataAccess());
                }
                final double errorPercent = ((_activationMappingErrorList.size() + ActivationSubscriberRetailerMapping.getCountErrorAtDataAccess()) * 100 / (double) ActivationSubscriberRetailerMapping
                    .getTotalRecords());
                AssociateMsisdnFileProcessLog.activationMappingLog("N.A", "N.A", "N.A", "N.A",
                    "Error Percent = " + errorPercent + ", Defined Error Percent = " + definedErrorPercentage + "");
                if (errorPercent > definedErrorPercentage) {
                    _logger.error("checkErrorPercentage", "Process errors are greater than defined errors. So process getting FAIL for this iteration");
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } catch (Exception e) {
            _logger.error("checkErrorPercentage", "MAX_ERROR_PERCENTAGE_ALLOWED_FOR_ACTIVATION_PROCESS is not defined in configuration file");
            _logger.errorTrace(METHOD_NAME, e);
            return false;
        }
    }

    /**
     * Returns true if Subscriber and Retailer information loaded in memory.
     * 
     * @author nand.sahu
     * @return boolean
     */
    private boolean isSubscriberRetailerDetailsLoaded() {
        if (_logger.isDebugEnabled()) {
            _logger.debug("isSubscriberRetailerDetailsLoaded()", "Entered");
        }
        if (_subscriberTypeMap == null || _userIdMap == null || _subscriberTypeMap.size() == 0 || _userIdMap.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Refresh object used for next iteration.
     * 
     * @author nand.sahu
     *         return void
     */
    private void refreshObjectForNextIteration() {
        if (_logger.isDebugEnabled()) {
            _logger.debug("refreshObjectForNextIteration()", "Entered");
        }

        _activationMappingErrorList = new HashMap<String, RetSubsMappingVO>();
        _activationMappingMap = new HashMap<String, RetSubsMappingVO>();
        _duplicateMappingErrorList = new ArrayList<RetSubsMappingVO>();
        _currentFileObj = null;
        _totalSuccessRecords = 0;
        // Initializing total records and Error records at file reading to zero
        setCountErrorAtDataAccess(0); // Assigning Errors 0 for next file.
        setTotalRecords(0);

    }

    /**
     * Free memery
     * 
     * @author nand.sahu
     * @return void
     */
    private void freeMemory() {
        _activationMappingMap = null;
        _activationMappingErrorList = null;
        _subscriberTypeMap = null;
        _networkCodeMap = null;
        _userIdMap = null;
        _setIdMapByUserId = null;
        _setIdMapByCategoryCode = null;
        _profileListBySetId = null;
        _currentFileObj = null;
        _errorMessage = null;
        _processStatusVO = null;
        _totalRecords = 0;
        _countErrorAtDataAccess = 0;
        _sourceType = "";
        _duplicateMappingErrorList = null;
        _currentDate = null;
        _timeStamp = null;
    }

    /**
     * @author nand.sahu
     * @return int _countErrorAtDataAccess
     */
    public static int getCountErrorAtDataAccess() {
        return _countErrorAtDataAccess;
    }

    /**
     * @author nand.sahu
     * @param int errorAtDataAccess
     * @return void
     */
    public static void setCountErrorAtDataAccess(int errorAtDataAccess) {
        _countErrorAtDataAccess = errorAtDataAccess;
    }

    /**
     * @author nand.sahu
     * @return int _totalRecords
     */
    public static int getTotalRecords() {
        return _totalRecords;
    }

    /**
     * @author nand.sahu
     * @param int records
     * @return void
     */
    public static void setTotalRecords(int records) {
        _totalRecords = records;
    }

    /**
     * Returns PreparedStatement to check that Subscriber Retailer Mapping Exist
     * already.
     * 
     * @author nand.sahu
     * @return PreparedStatement_isMappingPstmt
     */
    private PreparedStatement getPreparedStatementForMapping() {
        if (_logger.isDebugEnabled()) {
            _logger.debug("getPreparedStatementForMapping()", "Entered");
        }
        return _isMappingPstmt;
    }

    /**
     * Returns PreparedStatement to check that Subscriber and Retailer Exist in
     * same Network.
     * 
     * @author nand.sahu
     * @return PreparedStatement _isSameNetworkPstmt
     */
    private PreparedStatement getPreparedStatementForNetworkCheck() {
        if (_logger.isDebugEnabled()) {
            _logger.debug("getPreparedStatementForNetworkCheck()", "Entered");
        }
        return _isSameNetworkPstmt;
    }

    /**
     * Returns PreparedStatement to insert Subscriber Retailer Mapping details
     * in DB.
     * 
     * @author nand.sahu
     * @return PreparedStatement _insertMappingPstmt
     */
    private PreparedStatement getPreparedStatementForInsertMapping() {
        if (_logger.isDebugEnabled()) {
            _logger.debug("getPreparedStatementForInsertMapping()", "Entered");
        }
        return _insertMappingPstmt;
    }// End of getPrepareStatementForInsertMapping()

    /**
     * Create Global Prepared Statements
     * 
     * @author nand.sahu
     * @param con
     * @return
     */
    private boolean createGlobalPreparedStatements(Connection con) {
        final String METHOD_NAME = "createGlobalPreparedStatements";
        final String checkMappingQuery = new String(" SELECT 1 FROM  act_bonus_subs_mapping WHERE subscriber_msisdn = ? AND status <> ? ");
        if (_logger.isDebugEnabled()) {
            _logger.debug("createGlobalPreparedStatements()", " SQLQUERY 1:  for check is Subscriber already Mapped with other Retailer/Same or Not  = " + checkMappingQuery);
        }

        final String checkNetworkQuery = new String(
            " SELECT distinct(n.network_code) networkcode FROM  network_prefixes np, networks n WHERE np.series IN(?,?)  AND np.status = ? AND np.network_code = n.network_code  AND n.status = ?");
        if (_logger.isDebugEnabled()) {
            _logger.debug("createGlobalPreparedStatements()", "SQLQUERY 2: For check subscriber and Retailer Network  code  " + checkNetworkQuery);
        }

        final StringBuilder insertBuff = new StringBuilder("INSERT INTO act_bonus_subs_mapping (user_id, subscriber_msisdn, ");
        insertBuff.append("subscriber_type, set_id, version, expiry_date, registered_on, activation_bonus_given, status, ");
        insertBuff.append(" created_on, created_by, modified_on, modified_by, network_code,APPROVED_ON, APPROVED_BY) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        final String insertQuery = insertBuff.toString();
        if (_logger.isDebugEnabled()) {
            _logger.debug("createGlobalPreparedStatements()", "SQLQUERY 3: Inserting subscriber retialer mapping " + insertQuery);
        }
        try {
            _isMappingPstmt = con.prepareStatement(checkMappingQuery);
            _isSameNetworkPstmt = con.prepareStatement(checkNetworkQuery);
            _insertMappingPstmt = con.prepareStatement(insertQuery);
            return true;
        } catch (Exception ex) {
            _insertMappingPstmt = null;
            _logger.error("createGlobalPreparedStatements", "Global Prepared Statement not created");
            _logger.errorTrace(METHOD_NAME, ex);
            return false;
        }

    }// End of createGlobalPreparedStatements()

    /**
     * Closing all the Prepared Statements created globally.
     * 
     * @author nand.sahu
     * @return
     */
    private boolean closeGlobalPreparedStatements() {
    	boolean br=true;
        final String METHOD_NAME = "closeGlobalPreparedStatements";
        if (_logger.isDebugEnabled()) {
            _logger.debug("closeGlobalPreparedStatements()", "Entered");
        }
      
        try {
            try {
                if (_isMappingPstmt != null) {
                    _isMappingPstmt.close();
                }
            } catch (Exception e) {
            	br=false;
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (_isSameNetworkPstmt != null) {
                    _isSameNetworkPstmt.close();
                }
            } catch (Exception e) {
            	br=false;
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (_insertMappingPstmt != null) {
                    _insertMappingPstmt.close();
                }
            } catch (Exception e) {
            	br=false;
                _logger.errorTrace(METHOD_NAME, e);
            }
            return br;
        } catch (Exception ex) {
        	br=false;
            _logger.error("closeGlobalPreparedStatements", "Unable to Close global Prepare Statement");
            _logger.errorTrace(METHOD_NAME, ex);
            return br;
        }
    }// End of closeGlobalPreparedStatements()

}// End of class


package com.btsl.pretups.processes;

/**
 * @(#)P2PDWHFileCreation
 *                        Copyright(c) 2006, Bharti Telesoft Ltd.
 *                        All Rights Reserved
 * 
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Amit Singh 01/06/2006 Initial Creation
 *                        Ankit Singhal 06/09/2006 Modification ID P2PDWH001
 *                        Vikas Jauhari 10/02/2011 Modification ID P2PDWH002
 * 
 *                        Change Description: In this process, process flow is
 *                        changed, we have used procedure named as
 *                        'P2pdwhtempprc' to move slected data into temporary
 *                        table then we have fetched required DWH
 *                        data from temp tables to generate .csv files.
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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

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
import com.btsl.pretups.processes.businesslogic.DateSorting;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

public class P2PDWHFileCreation {
    private static String _dwhFileLabelForTransaction = null;// use to store the
    // labels for the
    // P2P transactions
    private static String _dwhFileLabelForMaster = null;// use to store the
    // labels for the P2P
    // master data
    private static String _dwhFileNameForTransaction = null;// use to store the
    // file name for the
    // P2P transactions
    private static String _dwhFileNameForMaster = null;// use to store the file
    // name for the P2P
    // master data
    private static String _masterDirectoryPathAndName = null;// use to store the
    // master directory
    // path and name in
    // which the master
    // and transaction
    // data files will
    // be stored
    private static String _finalMasterDirectoryPath = null;// use to store the
    // final master
    // directory path in
    // which the master
    // and transaction
    // data files will be
    // moved after all
    // files creation
    private static String _childDirectory = null;// use to store the master
    // directory path and name in
    // which the master and
    // transaction data files will
    // be stored
    private static String _fileEXT = null;// use to store the extension of the
    // files, which are going to create by
    // the process
    private static long _maxFileLength = 0;// use to store the maximum no. of
    // records a file can contain
    private static ArrayList _fileNameLst = new ArrayList();// use to store the
    // all names of the
    // files
    private static ProcessStatusVO _processStatusVO;
    private static ProcessBL _processBL = null;
    private static Log _logger = LogFactory.getLog(P2PDWHFileCreation.class.getName());
    // 06-MAR-2014 for OCI client
    private static Hashtable<String, Long> _fileNameMap = null;
    private static TreeMap<String, Object> _fileRecordMap = null;

    // Ended Here

    /**
     * to ensure no class instantiation 
     */
    private P2PDWHFileCreation(){
    	
    }
    public static void main(String arg[]) {
        final String METHOD_NAME = "main";
        Date date = null;
        try {

            if (arg.length == 3) {
                final String inputDate = arg[2];

                if (!BTSLUtil.isNullString(inputDate)) {
                    date = BTSLUtil.getDateFromDateString(inputDate, PretupsI.DATE_FORMAT_DDMMYYYY);
                }
            }

            final File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
                _logger.debug("main", "P2PDWHFileCreation" + " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                _logger.debug("main", "P2PDWHFileCreation" + " Logconfig File Not Found .............");
                return;
            }
            // use to load the constant.props and processLogConfig files
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        }// end of try
        catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("main", " Error in Loading Files ...........................: " + e.getMessage());
            }
            _logger.errorTrace(METHOD_NAME, e);
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch

        try {
            process(date);
        } catch (BTSLBaseException be) {
            _logger.error("main", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
        } finally {
            ConfigServlet.destroyProcessCache();
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug("main", "Exiting..... ");
        }

    }

    /**
     * This method is the main method of this process,
     * which is responsible for the P2P DWH files creation.
     * 
     * @throws BTSLBaseException
     */
    private static void process(Date p_date) throws BTSLBaseException {
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
        String p2pDwhProcName = null;

        final String methodName = "process";
        String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
        long masterCount = 0;
        long transCount = 0;
        String isSuccess = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        try {
            // 06-MAR-2014 for OCI client
            _fileNameMap = new Hashtable<String, Long>();
            _fileRecordMap = new TreeMap<String, Object>(new DateSorting());
            // Ended Here

            _logger.debug(methodName, "Memory at statup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);
            // getting all the required parameters from Constants.props
            loadConstantParameters();

            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(methodName, " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PDWHFileCreation[process]", "",
                    "", "", "DATABASE Connection is NULL");
                return;
            }
            // getting process id
            processId = ProcessI.P2PDWH_PROCESSID;
            // method call to check status of the process
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(con, processId);
            statusOk = _processStatusVO.isStatusOkBool();
            beforeInterval = BTSLUtil.parseLongToInt( _processStatusVO.getBeforeInterval() / (60 * 24));

            if (p_date == null && statusOk) {
                con.commit();
                // method call to find maximum date till which process has been
                // executed
                processedUpto = _processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    // ID P2PDWH001 to check whether process has been executed
                    // till current date or not
                    if (processedUpto.compareTo(currentDate) == 0) {
                        throw new BTSLBaseException("P2PDWHFileCreation", methodName, PretupsErrorCodesI.P2PDWH_PROCESS_ALREADY_EXECUTED_TILL_TODAY);
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

                        p2pDwhProcName = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2PDWH_OPT_SPECIFIC_PROC_NAME));
                        StringBuilder query=new StringBuilder();
                        if (!(BTSLUtil.isNullString(p2pDwhProcName) || "".equals(p2pDwhProcName))) {
                            if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                            	query = query.append("{call ").append(Constants.getProperty("currentschema")).append( "." ).append( p2pDwhProcName).append("(?,?,?,?)}");
                                cstmt = con.prepareCall(query.toString());
                            }else if(QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                            	query = query.append("{call ").append( p2pDwhProcName ).append("(?)}");
                            	cstmt =con.prepareCall(query.toString());
                            	
                            } 
                            else {
                            	query = query.append("{call ").append( p2pDwhProcName).append( "(?,?,?,?)}");
                                cstmt = con.prepareCall(query.toString());
                            }
                        } else {
                            if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                            	query=query.append("{call ").append(Constants.getProperty("currentschema")).append(".P2pdwhtempprc(?,?,?,?)}");
                                cstmt = con.prepareCall(query.toString());
                            }else if(QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                            	cstmt =con.prepareCall("{call P2pdwhtempprc(?)}");
                            } 
                            else {
                                cstmt = con.prepareCall("{call P2pdwhtempprc(?,?,?,?)}");
                            }
                        }
                        cstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(dateCount));
                        
                        if(!QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                        cstmt.registerOutParameter(2, Types.NUMERIC); // Count of Master
                        cstmt.registerOutParameter(3, Types.NUMERIC); // Count for Transaction
                        cstmt.registerOutParameter(4, Types.VARCHAR); // Status
                        }
                        
                        if (_logger.isDebugEnabled()) {
                            _logger.debug(methodName, "Before Exceuting Procedure");
                        }
                        
                        if(QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                             rs1 = cstmt.executeQuery();
                            if(rs1.next()){
                            	masterCount =rs1.getLong("p_mastercnt");
                            	transCount = rs1.getLong("p_transCnt");
                            	isSuccess=rs1.getString("p_message");
                             }
                        }
                        else{
                        	cstmt.executeUpdate();
                        	masterCount = cstmt.getLong(2);
                            transCount = cstmt.getLong(3);
                            isSuccess = cstmt.getString(4);
                        }
                        
                        if (_logger.isDebugEnabled()) {
                            _logger.debug(methodName, "After Exceuting Procedure");
                        }

                        if (!"SUCCESS".equals(isSuccess)) {
                        	 if (QueryConstants.DB_POSTGRESQL.equals(dbConnected) && con != null)
                                     con.rollback();
                            throw new BTSLBaseException("P2PDWHFileCreation", methodName, "Procedure Execution Fail");
                        }

                        if (!checkUnderprocessTransaction(con, dateCount)) {
                            // method call to create master directory and child
                            // directory if does not exist
                            _childDirectory = createDirectory(_masterDirectoryPathAndName, processId, dateCount);

                            int fileNumber = 1;
                            for (long i = 0, j = _maxFileLength; i < transCount; i += _maxFileLength) {

                                fetchP2PTransactionData(con, dateCount, _childDirectory, _dwhFileNameForTransaction, _dwhFileLabelForTransaction, _fileEXT, i, j, fileNumber);
                                fileNumber++;
                                if ((j + _maxFileLength) < transCount) {
                                    j += _maxFileLength;
                                } else if (j != transCount) {
                                    j = transCount;
                                }
                            }

                            int fileNumber2 = 1;
                            for (long n = 0, m = _maxFileLength; n < masterCount; n += _maxFileLength) {

                                fetchMasterData(con, dateCount, _childDirectory, _dwhFileNameForMaster, _dwhFileLabelForMaster, _fileEXT, n, m, fileNumber2);
                                fileNumber2++;
                                if ((m + _maxFileLength) < masterCount) {
                                    m += _maxFileLength;
                                } else if (m != masterCount) {
                                    m = masterCount;
                                }
                            }

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
                                throw new BTSLBaseException("P2PDWHFileCreation", methodName, PretupsErrorCodesI.P2PDWH_COULD_NOT_UPDATE_MAX_DONE_DATE);
                            }
                            // P2PDWH001 sleep has been added after processing
                            // records of one day
                            Thread.sleep(500);
                        }// end if
                    }// end loop

                    if (QueryConstants.DB_POSTGRESQL.equals(dbConnected) && con != null )
                     con.commit();
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "P2PDWHFileCreation[process]", "", "", "",
                        " P2PDWHFileCreation process has been executed successfully.");

                }// end of if (processedUpto!=null)
                 // ID DWH002 to avoid the null pointer exception thrown, in
                 // case processesUpto is null
                else {
                    throw new BTSLBaseException("P2PDWHFileCreation", methodName, PretupsErrorCodesI.P2PDWH_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
                }
            }// end of if (statusOk)
            else {
                // in order not to update the status of tabel for this processID
                con.rollback();
                if (!checkUnderprocessTransaction(con, p_date)) {
                    // method call to create master directory and child
                    // directory if does not exist
                    _childDirectory = createDirectory(_masterDirectoryPathAndName, processId, p_date);

                    p2pDwhProcName = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2PDWH_OPT_SPECIFIC_PROC_NAME));
                    StringBuilder query=new StringBuilder();
                    if (!(BTSLUtil.isNullString(p2pDwhProcName) || "".equals(p2pDwhProcName))) {
                        if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                        	query = query.append("{call ").append(Constants.getProperty("currentschema")).append( ".").append(p2pDwhProcName).append( "(?,?,?,?)}");
                            cstmt = con.prepareCall(query.toString());
                        } else if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                        	query=query.append("{call " ).append( p2pDwhProcName ).append( "(?)}");
                        	cstmt = con.prepareCall(query.toString());
                        }else {
                        	query = query.append("{call ").append( p2pDwhProcName ).append( "(?,?,?,?)}");
                            cstmt = con.prepareCall(query.toString());
                        }
                    } else {
                        if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                        	query=query.append("{call ").append( Constants.getProperty("currentschema") ).append( ".P2pdwhtempprc(?,?,?,?)}");
                            cstmt = con.prepareCall(query.toString());
                        }else if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                        	query=query.append("{call P2pdwhtempprc(?)}");
                        	cstmt = con.prepareCall(query.toString());
                        } else {
                            cstmt = con.prepareCall("{call P2pdwhtempprc(?,?,?,?)}");
                        }
                    }

                    cstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_date));
                    
                    if (!QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                    cstmt.registerOutParameter(2, Types.NUMERIC); // Count of Master
                    cstmt.registerOutParameter(3, Types.NUMERIC); // Count for Transaction
                    cstmt.registerOutParameter(4, Types.VARCHAR); // Status
                    }
                    if (_logger.isDebugEnabled()) {
                        _logger.debug(methodName, "Before Exceuting Procedure");
                    }
                    
                    if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){

                         rs = cstmt.executeQuery();
                        if(rs.next()){
                        	masterCount =rs.getLong("p_mastercnt");
                        	transCount = rs.getLong("p_transCnt");
                        	isSuccess=rs.getString("p_message");
                         }
                    
                    	
                    }
                    else{
                    cstmt.executeUpdate();
                    masterCount = cstmt.getLong(2);
                    transCount = cstmt.getLong(3);
                    isSuccess = cstmt.getString(4);
                    }
                    
                    if (_logger.isDebugEnabled()) {
                        _logger.debug(methodName, "After Exceuting Procedure");
                    }

                    if (!"SUCCESS".equals(isSuccess)) {
                    	if(con!=null)
                    		con.rollback();
                        throw new BTSLBaseException("P2PDWHFileCreation", "process ", "Single Date Procedure Execution Fail");
                    }

                    int fileNumber = 1;
                    for (long i = 0, j = _maxFileLength; i < transCount; i += _maxFileLength) {

                        fetchP2PTransactionData(con, p_date, _childDirectory, _dwhFileNameForTransaction, _dwhFileLabelForTransaction, _fileEXT, i, j, fileNumber);
                        fileNumber++;
                        if ((j + _maxFileLength) < transCount) {
                            j += _maxFileLength;
                        } else if (j != transCount) {
                            j = transCount;
                        }
                    }

                    int fileNumber2 = 1;
                    for (long i = 0, j = _maxFileLength; i < masterCount; i += _maxFileLength) {

                        fetchMasterData(con, p_date, _childDirectory, _dwhFileNameForMaster, _dwhFileLabelForMaster, _fileEXT, i, j, fileNumber2);
                        fileNumber2++;
                        if ((j + _maxFileLength) < masterCount) {
                            j += _maxFileLength;
                        } else if (j != masterCount) {
                            j = masterCount;
                        }
                    }

                    moveFilesToFinalDirectory(_masterDirectoryPathAndName, _finalMasterDirectoryPath, processId, p_date);

                }// end if

            }

        }// end of try
        catch (BTSLBaseException be) {
            _logger.error(methodName, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            try {
                if (_fileNameLst.size() > 0) {
                    deleteAllFiles();
                }
            } catch (Exception e1) {
                _logger.errorTrace(methodName, e1);
            }
            _logger.error(methodName, "Exception : " + e.getMessage());
            _logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "P2PDWHFileCreation[process]", "", "", "",
                " P2PDWHFileCreation process could not be executed successfully.");
            throw new BTSLBaseException("P2PDWHFileCreation", methodName, PretupsErrorCodesI.P2PDWH_ERROR_EXCEPTION);
        } finally {
            // 06-MAR-2014 for OCI client
            // try{writeFileSummary(_finalMasterDirectoryPath,_fileEXT);}catch(Exception
            // e){}
            // Ended Here

            // if the status was marked as under process by this method call,
            // only then it is marked as complete on termination
            if (statusOk) {
                try {
                    // not update process status for indvidual date execution of
                    // DWH
                    if (p_date == null && markProcessStatusAsComplete(con, processId) == 1) {
                        try {
                            con.commit();
                        } catch (Exception e) {
                            _logger.errorTrace(methodName, e);
                        }
                    } else {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            _logger.errorTrace(methodName, e);
                        }
                        try {
                            if (p_date == null && _fileNameLst.size() > 0) {
                                deleteAllFiles();
                            }
                        } catch (Exception e1) {
                            _logger.errorTrace(methodName, e1);
                        }
                    }
                } catch (Exception e) {
                    _logger.errorTrace(methodName, e);
                }

                try {
                    if (cstmt != null) {
                        cstmt.close();
                    }
                } catch (Exception ex) {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug(methodName, "Exception closing Callable statement ");
                    }
                    _logger.errorTrace(methodName, ex);
                }
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (Exception ex) {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug(methodName, "Exception closing connection ");
                    }
                    _logger.errorTrace(methodName, ex);
                }
            }

            try {
                if (cstmt != null) {
                    cstmt.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(methodName, "Exception closing Callable statement ");
                }
                _logger.errorTrace(methodName, ex);
            }
            try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(methodName, ex);
            }
            _logger.debug(methodName, "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, "Exiting..... ");
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
            _logger.debug("loadConstantParameters", " Entered: ");
        }
        try {
            _dwhFileLabelForTransaction = Constants.getProperty("P2PDWH_TRANSACTION_FILE_LABEL");
            if (BTSLUtil.isNullString(_dwhFileLabelForTransaction)) {
                _logger.error("loadConstantParameters", " Could not find file label for transaction data in the Constants file.");
            } else {
                _logger.debug("loadConstantParameters", " _dwhFileLabelForTransaction=" + _dwhFileLabelForTransaction);
            }

            _dwhFileLabelForMaster = Constants.getProperty("P2PDWH_MASTER_FILE_LABEL");
            if (BTSLUtil.isNullString(_dwhFileLabelForMaster)) {
                _logger.error("loadConstantParameters", " Could not find file label for master data in the Constants file.");
            } else {
                _logger.debug("loadConstantParameters", " _dwhFileLabelForMaster=" + _dwhFileLabelForMaster);
            }

            _dwhFileNameForTransaction = Constants.getProperty("P2PDWH_TRANSACTION_FILE_NAME");
            if (BTSLUtil.isNullString(_dwhFileNameForTransaction)) {
                _logger.error("loadConstantParameters", " Could not find file name for transaction data in the Constants file.");
            } else {
                _logger.debug("loadConstantParameters", " _dwhFileNameForTransaction=" + _dwhFileNameForTransaction);
            }
            _dwhFileNameForMaster = Constants.getProperty("P2PDWH_MASTER_FILE_NAME");
            if (BTSLUtil.isNullString(_dwhFileNameForMaster)) {
                _logger.error("loadConstantParameters", " Could not find file name for master data in the Constants file.");
            } else {
                _logger.debug("loadConstantParameters", " _dwhFileNameForMaster=" + _dwhFileNameForMaster);
            }

            _masterDirectoryPathAndName = Constants.getProperty("P2PDWH_MASTER_DIRECTORY");
            if (BTSLUtil.isNullString(_masterDirectoryPathAndName)) {
                _logger.error("loadConstantParameters", " Could not find directory path in the Constants file.");
            } else {
                _logger.debug("loadConstantParameters", " _masterDirectoryPathAndName=" + _masterDirectoryPathAndName);
            }
            _finalMasterDirectoryPath = Constants.getProperty("P2PDWH_FINAL_DIRECTORY");

            if (BTSLUtil.isNullString(_finalMasterDirectoryPath)) {
                _logger.error("loadConstantParameters", " Could not find final directory path in the Constants file.");
            } else {
                _logger.debug("loadConstantParameters", " finalMasterDirectoryPath=" + _finalMasterDirectoryPath);
            }

            // checking that none of the required parameters should be null
            if (BTSLUtil.isNullString(_dwhFileLabelForTransaction) || BTSLUtil.isNullString(_dwhFileLabelForMaster) || BTSLUtil.isNullString(_dwhFileNameForTransaction) || BTSLUtil
                .isNullString(_dwhFileNameForMaster) || BTSLUtil.isNullString(_masterDirectoryPathAndName) || BTSLUtil.isNullString(_finalMasterDirectoryPath)) {
                throw new BTSLBaseException("P2PDWHFileCreation", "loadConstantParameters", PretupsErrorCodesI.P2PDWH_COULD_NOT_FIND_DATA_IN_CONSTANTS_FILE);
            }
            try {
                _fileEXT = Constants.getProperty("P2PDWH_FILE_EXT");
            } catch (Exception e) {
                _fileEXT = ".csv";
                _logger.errorTrace(METHOD_NAME, e);
            }
            _logger.debug("loadConstantParameters", " _fileEXT=" + _fileEXT);
            try {
                _maxFileLength = Long.parseLong(Constants.getProperty("P2PDWH_MAX_FILE_LENGTH"));
            } catch (Exception e) {
                _maxFileLength = 1000;
                _logger.errorTrace(METHOD_NAME, e);
            }
            _logger.debug("loadConstantParameters", " _maxFileLength=" + _maxFileLength);
            _logger.debug("loadConstantParameters", " Required information successfuly loaded from Constants.props...............: ");
        } catch (BTSLBaseException be) {
            _logger.error("loadConstantParameters", "BTSLBaseException : " + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PDWHFileCreation[loadConstantParameters]", "", "",
                "", "Message:" + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _logger.error("loadConstantParameters", "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            final BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.P2PDWH_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PDWHFileCreation[loadConstantParameters]", "", "",
                "", "Message:" + btslMessage);
            throw new BTSLBaseException("P2PDWHFileCreation", "loadConstantParameters", PretupsErrorCodesI.P2PDWH_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("loadConstantParameters", " Exiting.. ");
            }
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
        final String METHOD_NAME = "checkUnderprocessTransaction";
        if (_logger.isDebugEnabled()) {
            _logger.debug("checkUnderprocessTransaction", " Entered: p_beingProcessedDate=" + p_beingProcessedDate);
        }
        PreparedStatement selectPstmt = null;
        ResultSet selectRst = null;
        boolean transactionFound = false;
        String selectQuery = null;
        try {
            selectQuery = new String("SELECT 1 FROM subscriber_transfers WHERE transfer_date=? AND transfer_status IN('205','250') ");
            if (_logger.isDebugEnabled()) {
                _logger.debug("checkUnderprocessTransaction", "select query:" + selectQuery);
            }
            selectPstmt = p_con.prepareStatement(selectQuery);
            selectPstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate));
            selectRst = selectPstmt.executeQuery();
            if (selectRst.next()) {
                transactionFound = true;
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PDWHFileCreation[checkUnderprocessTransaction]",
                    "", "", "", "Message:P2PDWHFileCreation process cannot continue as underprocess and/or ambiguous transactions are found.");
                throw new BTSLBaseException("P2PDWHFileCreation", "checkUnderprocessTransaction", PretupsErrorCodesI.DWH_AMB_OR_UP_TXN_FOUND);
            }
        } catch (BTSLBaseException be) {
            _logger.error("checkUnderprocessTransaction", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (SQLException sqe) {
            _logger.error("checkUnderprocessTransaction", "SQLException " + sqe.getMessage());
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PDWHFileCreation[checkUnderprocessTransaction]", "",
                "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("P2PDWHFileCreation", "checkUnderprocessTransaction", PretupsErrorCodesI.P2PDWH_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _logger.error("checkUnderprocessTransaction", "Exception : " + ex.getMessage());
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PDWHFileCreation[checkUnderprocessTransaction]", "",
                "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("P2PDWHFileCreation", "checkUnderprocessTransaction", PretupsErrorCodesI.P2PDWH_ERROR_EXCEPTION);
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
                _logger.debug("checkUnderprocessTransaction", "Exiting transactionFound=" + transactionFound);
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
            _logger.debug("createDirectory",
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
           // dirName = p_directoryPathAndName + File.separator + p_processId + "_" + p_beingProcessedDate.toString().substring(8, 10) + p_beingProcessedDate.toString()
                //.substring(5, 7) + p_beingProcessedDate.toString().substring(2, 4);
            dirName = p_directoryPathAndName + File.separator + p_processId + "_" + BTSLUtil.getDateStrForName(p_beingProcessedDate);
            newDir = new File(dirName);
            if (!newDir.exists()) {
                newDir.mkdirs();
            }
        } catch (Exception e) {
            _logger.debug("createDirectory", "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            if (parentDir != null) {
                parentDir = null;
            }
            if (newDir != null) {
                newDir = null;
            }
            throw new BTSLBaseException("P2PDWHFileCreation", "createDirectory", PretupsErrorCodesI.P2PDWH_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("createDirectory", "Exiting dirName=" + dirName);
            }
        } // end of finally
        return dirName;
    }

    /**
     * This method will fetch all the required P2P transactions data from
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
    private static void fetchP2PTransactionData(Connection p_con, Date p_beingProcessedDate, String p_dirPath, String p_fileName, String p_fileLabel, String p_fileEXT, long startRowNum, long endRowNum, int p_fileNumber) throws BTSLBaseException {
        final String METHOD_NAME = "fetchP2PTransactionData";
        if (_logger.isDebugEnabled()) {
            _logger
                .debug(
                    "fetchP2PTransactionData",
                    " Entered: p_beingProcessedDate=" + p_beingProcessedDate + " p_dirPath=" + p_dirPath + " p_fileName=" + p_fileName + " p_fileLabel=" + p_fileLabel + " p_fileEXT=" + p_fileEXT + " startRowNum=" + startRowNum + " endRowNum=" + endRowNum + " p_fileNumber=" + p_fileNumber);
        }

        final StringBuffer P2PQueryBuf = new StringBuffer();

        P2PQueryBuf.append(" SELECT DATA FROM TEMP_P2P_DWH_TRANS ");
        P2PQueryBuf.append(" WHERE SRNO >= ? and SRNO <= ? ");
        final String P2PSelectQuery = P2PQueryBuf.toString();
        if (_logger.isDebugEnabled()) {
            _logger.debug("fetchP2PTransactionData", "P2P select query:" + P2PSelectQuery);
        }
        PreparedStatement P2PSelectPstmt = null;
        ResultSet P2PSelectRst = null;

        try {
            P2PSelectPstmt = p_con.prepareStatement(P2PSelectQuery);
            P2PSelectPstmt.setLong(1, startRowNum);
            P2PSelectPstmt.setLong(2, endRowNum);
            P2PSelectRst = P2PSelectPstmt.executeQuery();
            _logger.debug("fetchP2PTransactionData", "Memory after loading transaction data: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime
                .getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);

            // method call to write data in the files
            writeDataInFile(p_dirPath, p_fileName, p_fileLabel, p_beingProcessedDate, p_fileEXT, P2PSelectRst, p_fileNumber);
            _logger.debug("fetchP2PTransactionData", "Memory after writing transaction files: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime
                .getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
        } catch (BTSLBaseException be) {
            _logger.error("fetchP2PTransactionData", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (SQLException sqe) {
            _logger.error("fetchP2PTransactionData", "SQLException " + sqe.getMessage());
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PSampleDWHFileCreation[fetchP2PTransactionData]",
                "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("P2PSampleDWHFileCreation", "fetchP2PTransactionData", PretupsErrorCodesI.P2PDWH_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _logger.error("fetchP2PTransactionData", "Exception : " + ex.getMessage());
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PSampleDWHFileCreation[fetchP2PTransactionData]",
                "", "", "", "SQLException:" + ex.getMessage());
            throw new BTSLBaseException("P2PSampleDWHFileCreation", "fetchP2PTransactionData", PretupsErrorCodesI.P2PDWH_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (P2PSelectRst != null) {
                try {
                    P2PSelectRst.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (P2PSelectPstmt != null) {
                try {
                    P2PSelectPstmt.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }

            if (_logger.isDebugEnabled()) {
                _logger.debug("fetchP2PTransactionData", "Exiting ");
            }

        }// end of finally
    }

    /**
     * This method will fetch all the required data from P2P_SUBSCRIBERS table
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
    private static void fetchMasterData(Connection p_con, Date p_beingProcessedDate, String p_dirPath, String p_fileName, String p_fileLabel, String p_fileEXT, long startRowNum, long endRowNum, int p_fileNumber) throws BTSLBaseException {
        final String METHOD_NAME = "fetchMasterData";
        if (_logger.isDebugEnabled()) {
            _logger
                .debug(
                    "fetchMasterData",
                    " Entered:  p_beingProcessedDate=" + p_beingProcessedDate + " p_dirPath=" + p_dirPath + " p_fileName=" + p_fileName + " p_fileLabel=" + p_fileLabel + " p_fileEXT=" + p_fileEXT + " startRowNum=" + startRowNum + " endRowNum=" + endRowNum + " p_fileNumber=" + p_fileNumber);
        }

        final StringBuffer queryBuf = new StringBuffer();
        queryBuf.append(" SELECT DATA FROM TEMP_P2P_DWH_MASTER ");
        queryBuf.append(" WHERE SRNO >= ? and SRNO <= ?");
        final String selectQuery = queryBuf.toString();
        if (_logger.isDebugEnabled()) {
            _logger.debug("fetchMasterData", "select query:" + selectQuery);
        }
        PreparedStatement selectPstmt = null;
        ResultSet selectRst = null;
        try {
            selectPstmt = p_con.prepareStatement(selectQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            selectPstmt.setLong(1, startRowNum);
            selectPstmt.setLong(2, endRowNum);

            selectRst = selectPstmt.executeQuery();
            _logger.debug("fetchMasterData", "Memory after loading master data: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime()
                .freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);

            // method call to write data in files
            writeDataInFile(p_dirPath, p_fileName, p_fileLabel, p_beingProcessedDate, p_fileEXT, selectRst, p_fileNumber);

            _logger.debug("fetchMasterData", "Memory after writing master files: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime()
                .freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
        } catch (BTSLBaseException be) {
            _logger.error("fetchMasterData", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (SQLException sqe) {
            _logger.error("fetchMasterData", "SQLException " + sqe.getMessage());
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PDWHFileCreation[fetchMasterData]", "", "", "",
                "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("P2PDWHFileCreation", "fetchMasterData", PretupsErrorCodesI.P2PDWH_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _logger.error("fetchMasterData", "Exception : " + ex.getMessage());
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PDWHFileCreation[fetchMasterData]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException("P2PDWHFileCreation", "fetchMasterData", PretupsErrorCodesI.P2PDWH_ERROR_EXCEPTION);
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
                _logger.debug("fetchMasterData", "Exiting ");
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
    private static void writeDataInFile(String p_dirPath, String p_fileName, String p_fileLabel, Date p_beingProcessedDate, String p_fileEXT, ResultSet p_rst, int fileNumber) throws BTSLBaseException {

        final String METHOD_NAME = "writeDataInFile";
        if (_logger.isDebugEnabled()) {
            _logger
                .debug(
                    "writeDataInFile",
                    " Entered:  p_dirPath=" + p_dirPath + " p_fileName=" + p_fileName + " p_fileLabel=" + p_fileLabel + " p_beingProcessedDate=" + p_beingProcessedDate + " p_fileEXT=" + p_fileEXT + " p_rst=" + p_rst + " fileNumber=" + fileNumber);
        }

        long recordsWrittenInFile = 0;
        PrintWriter out = null;

        String fileName = null;
        File newFile = null;
        String fileData = null;
        String fileHeader = null;
        String fileFooter = null;

        try {
            // generating file name

            // if the length of file number is 1, two zeros are added as prefix
            if (Integer.toString(fileNumber).length() == 1) {
                fileName = p_dirPath + File.separator + p_fileName + "00" + fileNumber + p_fileEXT;
            } else if (Integer.toString(fileNumber).length() == 2) {
                fileName = p_dirPath + File.separator + p_fileName + "0" + fileNumber + p_fileEXT;
            } else if (Integer.toString(fileNumber).length() == 3) {
                fileName = p_dirPath + File.separator + p_fileName + fileNumber + p_fileEXT;
            }

            _logger.debug("writeDataInFile", "  fileName=" + fileName);

            newFile = new File(fileName);
            _fileNameLst.add(fileName);
            out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
            // ID DWH002 to make addition of header and footer optional on the
            // basis of entry in Constants.props
            if ("Y".equalsIgnoreCase(Constants.getProperty("ADD_HEADER_FOOTER"))) {
                fileHeader = constructFileHeader(p_beingProcessedDate, fileNumber, p_fileLabel);
                out.write(fileHeader);
            }
            // traverse first resultset
            while (p_rst.next()) {
                fileData = p_rst.getString(1);
                out.write(fileData + "\n");
                out.flush();
                // added by aknsksha for tigo guatemal CR
                recordsWrittenInFile++;

            }// end of while(p_rst.next())

            if ("Y".equalsIgnoreCase(Constants.getProperty("ADD_HEADER_FOOTER"))) {
                fileFooter = constructFileFooter(recordsWrittenInFile);
                out.write(fileFooter);
            }
            // 06-MAR-2014 for OCI client
            if (fileName.contains(_masterDirectoryPathAndName)) {
                fileName = fileName.replace(_masterDirectoryPathAndName, _finalMasterDirectoryPath);
            }
            try {
                generateDataFileSummary(p_beingProcessedDate, recordsWrittenInFile, fileName);
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            // Ended Here
            if (out != null) {
                out.close();
            }
        } catch (BTSLBaseException e) {
            deleteAllFiles();
            _logger.debug("writeDataInFile", "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            if (newFile != null) {
                newFile = null;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PSampleDWHFileCreation[writeDataInFile]", "", "",
                "", "BTSLBaseException:" + e.getMessage());
            throw new BTSLBaseException("P2PSampleDWHFileCreation", "writeDataInFile", PretupsErrorCodesI.P2PDWH_ERROR_EXCEPTION);
        } catch (IOException e) {
            deleteAllFiles();
            _logger.debug("writeDataInFile", "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            if (newFile != null) {
                newFile = null;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PSampleDWHFileCreation[writeDataInFile]", "", "",
                "", "IOException:" + e.getMessage());
            throw new BTSLBaseException("P2PSampleDWHFileCreation", "writeDataInFile", PretupsErrorCodesI.P2PDWH_ERROR_EXCEPTION);
        } catch (SQLException e) {
            deleteAllFiles();
            _logger.debug("writeDataInFile", "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            if (newFile != null) {
                newFile = null;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PSampleDWHFileCreation[writeDataInFile]", "", "",
                "", "SQLException:" + e.getMessage());
            throw new BTSLBaseException("P2PSampleDWHFileCreation", "writeDataInFile", PretupsErrorCodesI.P2PDWH_ERROR_EXCEPTION);
        } catch (Exception e) {
            deleteAllFiles();
            _logger.debug("writeDataInFile", "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            if (newFile != null) {
                newFile = null;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PSampleDWHFileCreation[writeDataInFile]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("P2PSampleDWHFileCreation", "writeDataInFile", PretupsErrorCodesI.P2PDWH_ERROR_EXCEPTION);
        } finally {
            if (out != null) {
                out.close();
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("writeDataInFile", "Exiting ");
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PDWHFileCreation[markProcessStatusAsComplete]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("P2PDWHFileCreation", "markProcessStatusAsComplete", PretupsErrorCodesI.P2PDWH_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
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
            _logger.debug("deleteAllFiles", " Entered: ");
        }

        int size = 0;
        if (_fileNameLst != null) {
            size = _fileNameLst.size();
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug("deleteAllFiles", " : Number of files to be deleted " + size);
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
                _logger.error("deleteAllFiles", "Exception " + e.getMessage());
                _logger.errorTrace(METHOD_NAME, e);
                if (newFile != null) {
                    newFile = null;
                }
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PDWHFileCreation[deleteAllFiles]", "", "", "",
                    "Exception:" + e.getMessage());
                throw new BTSLBaseException("P2PDWHFileCreation", "deleteAllFiles", PretupsErrorCodesI.P2PDWH_ERROR_EXCEPTION);
            }
        }// end of for loop
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PDWHFileCreation[deleteAllFiles]", "", "", "",
            " Message: P2PDWHFileCreation process has found some error, so deleting all the files.");
        if (_fileNameLst != null && _fileNameLst.isEmpty()) {
            _fileNameLst.clear();
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug("deleteAllFiles", " : Exiting.............................");
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
            _logger.debug("constructFileHeader", " Entered: ");
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
            _logger.debug("constructFileHeader", "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("P2PDWHFileCreation", "constructFileHeader", PretupsErrorCodesI.P2PDWH_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("constructFileHeader", "Exiting: fileHeaderBuf.toString()=" + fileHeaderBuf.toString());
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
            _logger.debug("constructFileFooter", " Entered: ");
        }
        StringBuffer fileFooterBuf = null;
        try {
            fileFooterBuf = new StringBuffer("");
            fileFooterBuf.append("[ENDDATA]" + "\n");
            fileFooterBuf.append(" Number of records=" + p_noOfRecords);
        } catch (Exception e) {
            _logger.debug("constructFileHeader", "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("P2PDWHFileCreation", "constructFileFooter", PretupsErrorCodesI.P2PDWH_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("constructFileFooter", "Exiting: fileHeaderBuf.toString()=" + fileFooterBuf.toString());
            }
        } // end of finally
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
        final String METHOD_NAME = "moveFilesToFinalDirectory";
        if (_logger.isDebugEnabled()) {
            _logger
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
            parentDir.mkdir();
        }
        p_beingProcessedDate = BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate);
        // child directory name includes a file name and being processed date,
        // month and year
       // final String oldDirName = p_oldDirectoryPath + File.separator + p_processId + "_" + p_beingProcessedDate.toString().substring(8, 10) + p_beingProcessedDate.toString()
            //.substring(5, 7) + p_beingProcessedDate.toString().substring(2, 4);
        final String oldDirName = p_finalDirectoryPath + File.separator + p_processId + "_" + BTSLUtil.getDateStrForName(p_beingProcessedDate);
       // final String newDirName = p_finalDirectoryPath + File.separator + p_processId + "_" + p_beingProcessedDate.toString().substring(8, 10) + p_beingProcessedDate
           // .toString().substring(5, 7) + p_beingProcessedDate.toString().substring(2, 4);
       
        final String newDirName = p_finalDirectoryPath + File.separator + p_processId + "_" + BTSLUtil.getDateStrForName(p_beingProcessedDate);
        
        File oldDir = new File(oldDirName);
        File newDir = new File(newDirName);
        if (!newDir.exists()) {
            newDir.mkdir();
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug("moveFilesToFinalDirectory", " dirName=" + newDirName);
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
                    _logger.debug("moveFilesToFinalDirectory", " File " + oldFileName + " is moved to " + newFileName);
                }
            }// end of for loop
            _fileNameLst.clear();
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PDWHFileCreation[moveFilesToFinalDirectory]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("P2PDWHFileCreation", "moveFilesToFinalDirectory", PretupsErrorCodesI.P2PDWH_ERROR_EXCEPTION);
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

        } // end of finally
    }

    /**
     * @author diwakar
     * @date : 06-MAR-2014
     * @param p_dirPath
     * @param p_fileEXT
     * @throws BTSLBaseException
     */
    private static void writeFileSummary(String p_dirPath, String p_fileEXT, Date p_beingProcessedDate) throws BTSLBaseException {
        final String METHOD_NAME = "writeFileSummary";
        if (_logger.isDebugEnabled()) {
            _logger.debug("P2PDWHFileCreation",
                " Entered: writeFileSummary() p_dirPath=" + p_dirPath + ", p_fileEXT=" + p_fileEXT + ", p_beingProcessedDate=" + p_beingProcessedDate);
        }
        PrintWriter out = null;
        File newFile = null;
        try {
            String fileName = null;
            String fileData = null;
            String fileHeader = null;
            String processDate = null;

            //fileName = p_dirPath + File.separator + ProcessI.P2PDWH_PROCESSID + "Trans_Stat_" + BTSLUtil.getDateTimeStringFromDate(p_beingProcessedDate, "ddMMyy") + p_fileEXT;
            fileName = p_dirPath + File.separator + ProcessI.P2PDWH_PROCESSID + "Trans_Stat_" + BTSLUtil.getDateStrForName(p_beingProcessedDate) + p_fileEXT;
            
            // Ended Here
            _logger.debug("P2PDWHFileCreation", " writeFileSummary() fileName=" + fileName);

            newFile = new File(fileName);
            boolean isFileAlreadyExists = false;
            newFile = new File(fileName);
            if (!newFile.exists()) {
                newFile.createNewFile();
                isFileAlreadyExists = false;
            } else {
                isFileAlreadyExists = true;
            }
            // Added by Diwakar on 07-MAR-2014
            String transSeperator = null;
            try {
                transSeperator = Constants.getProperty("P2PDWH_TRANSCATION_STAT_SEPERATOR");
            } catch (RuntimeException e) {
                transSeperator = ";";
                _logger.errorTrace(METHOD_NAME, e);
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
                    fileHeader = "Date" + transSeperator + "Files_Number" + transSeperator + "File_Name" + transSeperator + "Total_Records";
                    out.write(fileHeader + "\n");
                }
                Hashtable<String, Long> fileRecord = null;
                _fileRecordMap.comparator();
                final Set<String> keyList = _fileRecordMap.keySet();
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
                    fileRecord = (Hashtable) _fileRecordMap.get(processDate);
                    itrFile = (fileRecord.keySet()).iterator();

                    fileData = BTSLDateUtil.getSystemLocaleDate(processDate) + transSeperator + new Integer(fileRecord.size()).toString() + transSeperator;
                    while (itrFile.hasNext()) {
                        file = itrFile.next().toString();
                        fileData = fileData + file + transSeperator + fileRecord.get(file).toString();
                        out.append(fileData + "\n");
                        i++;
                    }
                }
                out.flush();
            } else {
                _logger.error("P2PDWHFileCreation", " writeFileSummary() fileName=" + fileName + "does not exists on system.");
            }
        } catch (IOException e) {
            _logger.debug("P2PDWHFileCreation", "Exception writeFileSummary(): " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PDWHFileCreation[writeFileSummary]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("P2PDWHFileCreation", "writeFileSummary()", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } catch (Exception e) {
            _logger.debug("P2PDWHFileCreation", "Exception writeFileSummary(): " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "P2PDWHFileCreation[writeFileSummary]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("P2PDWHFileCreation", "writeFileSummary()", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (out != null) {
                out.close();
            }
            if (_fileRecordMap != null) {
                _fileRecordMap.clear();
            }
            if (_fileNameMap != null) {
                _fileNameMap.clear();
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("P2PDWHFileCreation", "Exiting writeFileSummary() ");
            }
        }
    }

    /**
     * @author diwakar
     * @date : 04-MAR-2014
     * @param p_beingProcessedDate
     * @param p_recordsWrittenInFile
     * @param p_fileName
     * @throws BTSLBaseException
     */
    private static void generateDataFileSummary(Date p_beingProcessedDate, long p_recordsWrittenInFile, String p_fileName) throws BTSLBaseException {

        final String METHOD_NAME = "generateDataFileSummary";
        if (_logger.isDebugEnabled()) {
            _logger
                .debug(
                    "P2PDWHFileCreation",
                    " Entered: generateDataFileSummary p_beingProcessedDate=" + p_beingProcessedDate + ", p_recordsWrittenInFile=" + p_recordsWrittenInFile + ", p_fileName=" + p_fileName);
        }
        try {
            final String processDateStr = BTSLUtil.getDateStringFromDate(p_beingProcessedDate);
            if (_fileRecordMap.isEmpty()) {
                _fileNameMap.put(p_fileName, p_recordsWrittenInFile);
                _fileRecordMap.put(processDateStr, _fileNameMap);
            } else {
                if (_fileRecordMap.containsKey(processDateStr)) {
                    _fileNameMap = (Hashtable<String, Long>) _fileRecordMap.get(processDateStr);
                    _fileNameMap.put(p_fileName, p_recordsWrittenInFile);
                    // Added By Diwakar on 07-MAR-2014
                    _fileRecordMap.put(processDateStr, _fileNameMap);
                    // Ended Here
                } else {
                    _fileNameMap = new Hashtable<String, Long>();
                    _fileNameMap.put(p_fileName, p_recordsWrittenInFile);
                    _fileRecordMap.put(processDateStr, _fileNameMap);
                }
            }

        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.debug("P2PDWHFileCreation", " generateDataFileSummary() While recoding file list Exception: " + e.getMessage());
        } finally {
            // 09-SEP-2014 for OCI client
            String isSummaryFileReq = "N";
            isSummaryFileReq = Constants.getProperty("P2PDWH_SUMMARY_FILE_REQUIRED");
            if (BTSLUtil.isNullString(isSummaryFileReq)) {
                isSummaryFileReq = "N";
            }
            if ("Y".equalsIgnoreCase(isSummaryFileReq)) {
                try {
                    writeFileSummary(_finalMasterDirectoryPath, _fileEXT, p_beingProcessedDate);
                } catch (Exception e) {
                    _logger.errorTrace(METHOD_NAME, e);
                }
                // Ended Here
            }

            if (_logger.isDebugEnabled()) {
                _logger.debug("P2PDWHFileCreation", "Exiting generateDataFileSummary() ");
            }
        }
    }

}

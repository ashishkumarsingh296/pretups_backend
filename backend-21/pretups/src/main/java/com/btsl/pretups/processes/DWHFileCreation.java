package com.btsl.pretups.processes;

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

public class DWHFileCreation {
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
    private static boolean _ambiUnderCheckRequired = true;
    private static String bonusBundleDefaultValues = null;
    // added by akanksha
    private static String _dwhFileLabelForMasterChannelUser = null;
    private static String _dwhFileNameForMasterChannelUser = null;
    // 04-MAR-2014 for OCI client
    private static Hashtable<String, Long> _fileNameMap = null;
    private static TreeMap<String, Object> _fileRecordMap = null;
    // Ended Here

    private static Log _logger = LogFactory.getLog(DWHFileCreation.class.getName());

    /**
     * ensures no instantiation
     */
    private DWHFileCreation(){
    	
    }
    public static void main(String arg[]) {
        final String METHOD_NAME = "main";
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
                System.out.println("DWHFileCreation" + " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                System.out.println("DWHFileCreation" + " Logconfig File Not Found .............");
                return;
            }

            // if third argument is not set then try to set it with Default
            // value true;
            try {
                if (arg.length == 4) {
                    final String inputDate = arg[3];
                    if (!BTSLUtil.isNullString(inputDate)) {
                        date = BTSLUtil.getDateFromDateString(inputDate, PretupsI.DATE_FORMAT_DDMMYYYY);
                        final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
                        if (!date.before(BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(new Date())))) {
                            System.out.println("DWHFileCreation" + " process will execute only for current or previous date ");
                            return;
                           
                        }
                        System.out.println("DWHFileCreation" + " DWHFileCreation process is going to execute only till.............date:" + date);

                        System.out.println("DWHFileCreation" + " DWHFileCreation process is going to execute only for.............date:" + date);
                    }

                }
                if (arg.length >= 3) {
                    if (PretupsI.YES.equalsIgnoreCase(arg[2])) {
                    	   _ambiUnderCheckRequired = true;
                    } else {
                        _ambiUnderCheckRequired = false;
                    }
                }
            } catch (Exception e) {
                _ambiUnderCheckRequired = true;
                _logger.errorTrace(METHOD_NAME, e);
            }

            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            bonusBundleDefaultValues = Constants.getProperty("BONUS_BUNDLE_DEFAULT_VAL");
            if (BTSLUtil.isNullString(bonusBundleDefaultValues)) {
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[main]", "", "", "",
                    "BONUS_BUNDLE_DEFAULT_VAL is not defined in Constants.props");
            }

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
            if (_logger.isDebugEnabled()) {
                _logger.debug("main", "Exiting..... ");
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    private static void process(Date p_date) throws BTSLBaseException {
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
        ResultSet rs = null;
         ResultSet rs1 = null;
        long masterCount=0 ;
        long chTransCount=0 ;
        long c2sTransCount=0 ;
        String isSuccess=null ;
        String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
        try {
            // 04-MAR-2014 for OCI client
            _fileNameMap = new Hashtable<String, Long>();
            _fileRecordMap = new TreeMap<String, Object>(new DateSorting());
            // Ended Here

            _logger.debug(methodName, "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);
            // getting all the required parameters from Constants.props
            loadConstantParameters();

            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(methodName, " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[process]", "", "",
                    "", "DATABASE Connection is NULL");
                return;
            }
            // getting process id
            processId = ProcessI.DWH_PROCESSID;
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
                    // ID DWH002 to check whether process has been executed till
                    // current date or not
                    if (processedUpto.compareTo(currentDate) == 0) {
                        throw new BTSLBaseException("DWHFileCreation", methodName, PretupsErrorCodesI.DWH_PROCESS_ALREADY_EXECUTED_TILL_TODAY);
                    }
                    // adding 1 in processed upto dtae as we have to start from
                    // the next day till which process has been executed
                    processedUpto = BTSLUtil.addDaysInUtilDate(processedUpto, 1);
                    // loop to be started for each date
                    // the loop starts from the date till which process has been
                    // executed and executes one day before current date
                    //for postgres db check
                  
                    for (dateCount = BTSLUtil.getSQLDateFromUtilDate(processedUpto); dateCount.before(BTSLUtil.addDaysInUtilDate(currentDate, -beforeInterval)); dateCount = BTSLUtil
                        .addDaysInUtilDate(dateCount, 1)) {
                        dwhProcName = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.RP2PDWH_OPT_SPECIFIC_PROC_NAME));
                        StringBuilder query = new StringBuilder();
                        if (!(BTSLUtil.isNullString(dwhProcName) || "".equals(dwhProcName))) {
                            if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                                 query = query.append("{call ").append(Constants.getProperty("currentschema")).append(".").append(dwhProcName).append("(?,?,?,?,?)}");
                                cstmt = con.prepareCall(query.toString());
                            }else if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                            	query=query.append("{call ").append(dwhProcName).append("(?)}");
                            	cstmt = con.prepareCall(query.toString());
                            } 
                            else {
                            	query = query.append("{call ").append(dwhProcName).append("(?,?,?,?,?)}");
                                cstmt = con.prepareCall(query.toString());
                            }
                        } else {
                            if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                            	query= query.append("{call ").append(Constants.getProperty("currentschema")).append(".Rp2pdwhtempprc(?,?,?,?,?)}");
                                cstmt = con.prepareCall(query.toString());
                            }
                            else if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                            	query=query.append("{call Rp2pdwhtempprc(?)}");
                            	cstmt = con.prepareCall(query.toString());
                            }
                            else {
                                cstmt = con.prepareCall("{call Rp2pdwhtempprc(?,?,?,?,?)}");
                            }
                        }
                        
                        
                        cstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(dateCount));
                        
                        if(!QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                        cstmt.registerOutParameter(2, Types.NUMERIC); // Count of  Master
                        cstmt.registerOutParameter(3, Types.NUMERIC); // Count for Channel Transaction
                        cstmt.registerOutParameter(4, Types.NUMERIC); // Count for C2S Transaction
                        cstmt.registerOutParameter(5, Types.VARCHAR); // Status
                        }
                        
                        if (_logger.isDebugEnabled()) {
                            _logger.debug(methodName, "Before Exceuting Procedure");
                        }
                        
                        if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                         rs = cstmt.executeQuery();
                        if(rs.next()){
                        	masterCount =rs.getLong("p_mastercnt");
                        	chTransCount = rs.getLong("p_chtranscnt");
                        	c2sTransCount=rs.getLong("p_c2stranscnt");
                        	isSuccess=rs.getString("p_message");
                         }
                        }
                        else{
                         cstmt.executeUpdate();
                         masterCount = cstmt.getLong(2);
                         chTransCount = cstmt.getLong(3);
                         c2sTransCount = cstmt.getLong(4);
                         isSuccess = cstmt.getString(5);
                        }
                        
                        if (_logger.isDebugEnabled()) {
                            _logger.debug(methodName, "After Exceuting Procedure");
                        }
                      
                        if (!"SUCCESS".equals(isSuccess)) {
                        	 if (QueryConstants.DB_POSTGRESQL.equals(dbConnected) && con != null )
                                  con.rollback();
                            throw new BTSLBaseException("DWHFileCreation", methodName, "Procedure Execution Fail");
                        }

                        if (_ambiUnderCheckRequired) {
                            _ambiUnderTxnFound = checkUnderprocessTransaction(con, dateCount);

                        }else{
                        	_ambiUnderTxnFound=false;
                        }
                        if (!_ambiUnderTxnFound) {
                            // method call to create master directory and child
                            // directory if does not exist
                            _childDirectory = createDirectory(_masterDirectoryPathAndName, processId, dateCount);
                            // method call to fetch Channel transaction data and
                            // write it in files
                            int fileNumber = 1;
                            for (long i = 0, j = _maxFileLength; i < chTransCount; i += _maxFileLength) {
                                fetchChannelTransactionData(con, dateCount, _childDirectory, _dwhFileNameForTransaction, _dwhFileLabelForTransaction, _fileEXT, i, j,
                                    fileNumber);
                                fileNumber++;
                                if ((j + _maxFileLength) < chTransCount) {
                                    j += _maxFileLength;
                                } else if (j != chTransCount) {
                                    j = chTransCount;
                                }
                            }
                            // method call to fetch C2S transaction data and
                            // write it in files
                            for (long i = 0, j = _maxFileLength; i < c2sTransCount; i += _maxFileLength) {
                                fetchC2STransactionData(con, dateCount, _childDirectory, _dwhFileNameForTransaction, _dwhFileLabelForTransaction, _fileEXT, i, j, fileNumber);
                                fileNumber++;
                                if ((j + _maxFileLength) < c2sTransCount) {
                                    j += _maxFileLength;
                                } else if (j != c2sTransCount) {
                                    j = c2sTransCount;
                                }
                            }
                            // method call to fetch master data and write it in
                            // files
                            int fileNumber3 = 1;
                            for (long n = 0, m = _maxFileLength; n < masterCount; n += _maxFileLength) {

                                fetchMasterData(con, dateCount, _childDirectory, _dwhFileNameForMaster, _dwhFileLabelForMaster, _fileEXT, n, m, fileNumber3);
                                fileNumber3++;
                                if ((m + _maxFileLength) < masterCount) {
                                    m += _maxFileLength;
                                } else if (m != masterCount) {
                                    m = masterCount;
                                }

                            }
                            if (Constants.getProperty("CHANNEL_USER_MASTER_ALLOWED").equals(PretupsI.YES)) {
                                int fileNumber4 = 1;
                                for (long n = 0, m = _maxFileLength; n < masterCount; n += _maxFileLength) {
                                    fetchMasterDataForChannelUser(con, dateCount, _childDirectory, _dwhFileNameForMasterChannelUser, _dwhFileLabelForMasterChannelUser,
                                        _fileEXT, n, m, fileNumber4);
                                    fileNumber4++;
                                    if ((m + _maxFileLength) < masterCount) {
                                        m += _maxFileLength;
                                    } else if (m != masterCount) {
                                        m = masterCount;
                                    }
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
                                moveFilesToFinalDirectory(_masterDirectoryPathAndName, _finalMasterDirectoryPath, processId, dateCount);
                                con.commit();
                            } else {
                                deleteAllFiles();
                                con.rollback();
                                throw new BTSLBaseException("DWHFileCreation", methodName, PretupsErrorCodesI.DWH_COULD_NOT_UPDATE_MAX_DONE_DATE);
                            }
                            // DWH002 sleep has been added after processing
                            // records of one day
                            Thread.sleep(500);
                        }// end if
                    }// end loop
                   
                    if (QueryConstants.DB_POSTGRESQL.equals(dbConnected) && con != null )
                    con.commit();
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DWHFileCreation[process]", "", "", ""," DWHFileCreation process has been executed successfully.");
                   
                }
                // ID DWH002 to avoid the null pointer exception thrown, in case
                // processesUpto is null
                else {
                    throw new BTSLBaseException("DWHFileCreation", methodName, PretupsErrorCodesI.DWH_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
                }
            }// end of if (statusOk)
            else {
                // in order not to update the status of tabel for this processID
                con.rollback();
                if (_ambiUnderCheckRequired) {
                    _ambiUnderTxnFound = checkUnderprocessTransaction(con, p_date);
                }
                if (!_ambiUnderTxnFound) {
                    // method call to create master directory and child
                    // directory if does not exist
                    _childDirectory = createDirectory(_masterDirectoryPathAndName, processId, p_date);
                    dwhProcName = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.RP2PDWH_OPT_SPECIFIC_PROC_NAME));
                    StringBuilder prepCall= new StringBuilder();
                    if (!(BTSLUtil.isNullString(dwhProcName) || "".equals(dwhProcName))) {
                        if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                             prepCall = prepCall.append("{call ").append(Constants.getProperty("currentschema")).append(".").append(dwhProcName).append("(?,?,?,?,?)}");
                            cstmt = con.prepareCall(prepCall.toString());
                        } else if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                        	prepCall=prepCall.append("{call ").append(dwhProcName).append("(?)}");
                        	cstmt = con.prepareCall(prepCall.toString());
                        }
                        else {
                            prepCall = prepCall.append("{call ").append( dwhProcName).append( "(?,?,?,?,?)}");
                            cstmt = con.prepareCall(prepCall.toString());
                        }
                    } else {
                        if (PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {
                            cstmt = con.prepareCall("{call " + Constants.getProperty("currentschema") + ".Rp2pdwhtempprc(?,?,?,?,?)}");
                        }else if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                        	cstmt = con.prepareCall("{call Rp2pdwhtempprc(?)}");
                        } 
                        else {
                            cstmt = con.prepareCall("{call Rp2pdwhtempprc(?,?,?,?,?)}");
                        }
                    }
                    cstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_date));
                    
                    if(!QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                    cstmt.registerOutParameter(2, Types.NUMERIC); // Count of Master
                    cstmt.registerOutParameter(3, Types.NUMERIC); // Count for Channel Transaction
                    cstmt.registerOutParameter(4, Types.NUMERIC); // Count for C2STransaction
                    cstmt.registerOutParameter(5, Types.VARCHAR); // Status
                    }
                    
                    if (_logger.isDebugEnabled()) {
                        _logger.debug(methodName, "Before Exceuting Procedure");
                    }
                    
                    if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                         rs1 = cstmt.executeQuery();
                        if(rs1.next()){
                        	masterCount =rs1.getLong("p_mastercnt");
                        	chTransCount = rs1.getLong("p_chtranscnt");
                        	c2sTransCount=rs1.getLong("p_c2stranscnt");
                        	isSuccess=rs1.getString("p_message");
                         }
                     }
                    else{
                    cstmt.executeUpdate();
                    masterCount = cstmt.getLong(2);
                    chTransCount = cstmt.getLong(3);
                    c2sTransCount = cstmt.getLong(4);
                    isSuccess = cstmt.getString(5);
                    }
                    
                    if (_logger.isDebugEnabled()) {
                        _logger.debug(methodName, "After Exceuting Procedure");
                    }

                    


                    if (!"SUCCESS".equals(isSuccess)) {
                    	 if (QueryConstants.DB_POSTGRESQL.equals(dbConnected) && con!=null)
                             con.rollback();
                        throw new BTSLBaseException("DWHFileCreation", methodName, "Procedure Execution Fail");
                    }
                    // method call to fetch Channel transaction data and write
                    // it in files
                    int fileNumber = 1;
                    for (long i = 0, j = _maxFileLength; i < chTransCount; i += _maxFileLength) {
                        fetchChannelTransactionData(con, p_date, _childDirectory, _dwhFileNameForTransaction, _dwhFileLabelForTransaction, _fileEXT, i, j, fileNumber);
                        fileNumber++;
                        if ((j + _maxFileLength) < chTransCount) {
                            j += _maxFileLength;
                        } else if (j != chTransCount) {
                            j = chTransCount;
                        }
                    }
                    // method call to fetch C2S transaction data and write it in
                    // files
                    for (long i = 0, j = _maxFileLength; i < c2sTransCount; i += _maxFileLength) {
                        fetchC2STransactionData(con, p_date, _childDirectory, _dwhFileNameForTransaction, _dwhFileLabelForTransaction, _fileEXT, i, j, fileNumber);
                        fileNumber++;
                        if ((j + _maxFileLength) < c2sTransCount) {
                            j += _maxFileLength;
                        } else if (j != c2sTransCount) {
                            j = c2sTransCount;
                        }
                    }

                    // method call to fetch master data and write it in files
                    int fileNumber3 = 1;
                    for (long n = 0, m = _maxFileLength; n < masterCount; n += _maxFileLength) {

                        fetchMasterData(con, p_date, _childDirectory, _dwhFileNameForMaster, _dwhFileLabelForMaster, _fileEXT, n, m, fileNumber3);
                        fileNumber3++;
                        if ((m + _maxFileLength) < masterCount) {
                            m += _maxFileLength;
                        } else if (m != masterCount) {
                            m = masterCount;
                        }
                    }
                    // if the process is successful, transaction is commit, else
                    // rollback
                    moveFilesToFinalDirectory(_masterDirectoryPathAndName, _finalMasterDirectoryPath, processId, p_date);
                    con.commit();
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DWHFileCreation[process]", "", "", "",
                            " DWHFileCreation process has been executed successfully.");
                }
            }
        }// end of try
        catch (BTSLBaseException be) {
            _logger.error(methodName, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            if (_fileNameLst.size() > 0) {
                deleteAllFiles();
            }
            _logger.error(methodName, "Exception : " + e.getMessage());
            _logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DWHFileCreation[process]", "", "", "",
                " DWHFileCreation process could not be executed successfully.");
            throw new BTSLBaseException("DWHFileCreation", methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            // 04-MAR-2014 for OCI client
            // try{writeFileSummary(_finalMasterDirectoryPath,_fileEXT);}catch(Exception
            // e){}
            // Ended Here

            // if the status was marked as under process by this method call,
            // only then it is marked as complete on termination
            if (statusOk) {
                try {
                    if (markProcessStatusAsComplete(con, processId) == 1) {
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
                    }
                } catch (Exception e) {
                    _logger.errorTrace(methodName, e);
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
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
            }
            try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
            }
            try {
                if (cstmt != null) {
                    cstmt.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
            }
            _logger.debug(methodName, "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, "Exiting..... ");
            }
        }
    }

    private static void loadConstantParameters() throws BTSLBaseException {
        final String METHOD_NAME = "loadConstantParameters";
        if (_logger.isDebugEnabled()) {
            _logger.debug("loadParameters", " Entered: ");
        }
        try {
            _dwhFileLabelForTransaction = Constants.getProperty("DWH_TRANSACTION_FILE_LABEL");
            if (BTSLUtil.isNullString(_dwhFileLabelForTransaction)) {
                _logger.error("loadConstantParameters", " Could not find file label for transaction data in the Constants file.");
            } else {
                _logger.debug("main", " _dwhFileLabelForTransaction=" + _dwhFileLabelForTransaction);
            }
            _dwhFileLabelForMaster = Constants.getProperty("DWH_MASTER_FILE_LABEL");
            if (BTSLUtil.isNullString(_dwhFileLabelForMaster)) {
                _logger.error("loadConstantParameters", " Could not find file label for master data in the Constants file.");
            } else {
                _logger.debug("loadConstantParameters", " _dwhFileLabelForMaster=" + _dwhFileLabelForMaster);
            }
            _dwhFileNameForTransaction = Constants.getProperty("DWH_TRANSACTION_FILE_NAME");
            if (BTSLUtil.isNullString(_dwhFileNameForTransaction)) {
                _logger.error("loadConstantParameters", " Could not find file name for transaction data in the Constants file.");
            } else {
                _logger.debug("loadConstantParameters", " _dwhFileNameForTransaction=" + _dwhFileNameForTransaction);
            }
            _dwhFileNameForMaster = Constants.getProperty("DWH_MASTER_FILE_NAME");
            if (BTSLUtil.isNullString(_dwhFileNameForMaster)) {
                _logger.error("loadConstantParameters", " Could not find file name for master data in the Constants file.");
            } else {
                _logger.debug("loadConstantParameters", " _dwhFileNameForMaster=" + _dwhFileNameForMaster);
            }

            _masterDirectoryPathAndName = Constants.getProperty("DWH_MASTER_DIRECTORY");
            if (BTSLUtil.isNullString(_masterDirectoryPathAndName)) {
                _logger.error("loadConstantParameters", " Could not find directory path in the Constants file.");
            } else {
                _logger.debug("loadConstantParameters", " _masterDirectoryPathAndName=" + _masterDirectoryPathAndName);
            }
            _finalMasterDirectoryPath = Constants.getProperty("DWH_FINAL_DIRECTORY");
            if (BTSLUtil.isNullString(_finalMasterDirectoryPath)) {
                _logger.error("loadConstantParameters", " Could not find final directory path in the Constants file.");
            } else {
                _logger.debug("loadConstantParameters", " finalMasterDirectoryPath=" + _finalMasterDirectoryPath);
            }
            if (Constants.getProperty("CHANNEL_USER_MASTER_ALLOWED").equals(PretupsI.YES)) {
                _dwhFileLabelForMasterChannelUser = Constants.getProperty("DWH_MASTER_CHANNELUSER_FILE_LABEL");
                if (BTSLUtil.isNullString(_dwhFileLabelForMasterChannelUser)) {
                    _logger.error("loadConstantParameters", " Could not find file label for master data in the Constants file.");
                } else {
                    _logger.debug("loadConstantParameters", " _dwhFileLabelForMaster=" + _dwhFileLabelForMasterChannelUser);
                    // checking that none of the required parameters should be
                    // null
                }

                _dwhFileNameForMasterChannelUser = Constants.getProperty("DWH_MASTER_CHANNELUSER_FILE_NAME");
                if (BTSLUtil.isNullString(_dwhFileNameForMasterChannelUser)) {
                    _logger.error("loadConstantParameters", " Could not find file label for master data in the Constants file.");
                } else {
                    _logger.debug("loadConstantParameters", " _dwhFileLabelForMaster=" + _dwhFileNameForMasterChannelUser);
                }
                if (BTSLUtil.isNullString(_dwhFileLabelForMasterChannelUser) || BTSLUtil.isNullString(_dwhFileNameForMasterChannelUser)) {
                    throw new BTSLBaseException("DWHFileCreation", "loadConstantParameters", PretupsErrorCodesI.DWH_COULD_NOT_FIND_DATA_IN_CONSTANTS_FILE);
                }

            }
            if (BTSLUtil.isNullString(_dwhFileLabelForTransaction) || BTSLUtil.isNullString(_dwhFileLabelForMaster) || BTSLUtil.isNullString(_dwhFileNameForTransaction) || BTSLUtil
                .isNullString(_dwhFileNameForMaster) || BTSLUtil.isNullString(_masterDirectoryPathAndName) || BTSLUtil.isNullString(_finalMasterDirectoryPath)) {
                throw new BTSLBaseException("DWHFileCreation", "loadConstantParameters", PretupsErrorCodesI.DWH_COULD_NOT_FIND_DATA_IN_CONSTANTS_FILE);
            }
            try {
                _fileEXT = Constants.getProperty("DWH_FILE_EXT");
            } catch (Exception e) {
                _fileEXT = ".csv";
                _logger.errorTrace(METHOD_NAME, e);
            }
            _logger.debug("loadConstantParameters", " _fileEXT=" + _fileEXT);
            try {
                _maxFileLength = Long.parseLong(Constants.getProperty("DWH_MAX_FILE_LENGTH"));
            } catch (Exception e) {
                _maxFileLength = 1000;
                _logger.errorTrace(METHOD_NAME, e);
            }
            _logger.debug("loadConstantParameters", " _maxFileLength=" + _maxFileLength);
            _logger.debug("loadConstantParameters", " Required information successfuly loaded from Constants.props...............: ");
        } catch (BTSLBaseException be) {
            _logger.error("loadConstantParameters", "BTSLBaseException : " + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[loadConstantParameters]", "", "", "",
                "Message:" + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _logger.error("loadConstantParameters", "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            final BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[loadConstantParameters]", "", "", "",
                "Message:" + btslMessage);
            throw new BTSLBaseException("DWHFileCreation", "loadConstantParameters", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
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
        //local_index_implemented
        if (_logger.isDebugEnabled()) {
            _logger.debug("checkUnderprocessTransaction", " Entered: p_beingProcessedDate=" + p_beingProcessedDate);
        }
        PreparedStatement selectPstmt = null;
        ResultSet selectRst = null;
        boolean transactionFound = false;
        StringBuilder selectQuery = new StringBuilder();
        try {
            // by sandeep goel ID DWH001 to make get the status from the file
            // not be hardcoded
            selectQuery = selectQuery.append("SELECT 1 FROM c2s_transfers WHERE transfer_date=? AND transfer_status IN('").append( PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS).append("','").append(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS).append("') ");
            if (_logger.isDebugEnabled()) {
                _logger.debug("checkUnderprocessTransaction", "select query:" + selectQuery);
            }
            selectPstmt = p_con.prepareStatement(selectQuery.toString());
            selectPstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate));
            selectRst = selectPstmt.executeQuery();
            if (selectRst.next()) {
                transactionFound = true;
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[checkUnderprocessTransaction]",
                    "", "", "", "Message: DWHFileCreation process cannot continue as underprocess and/or ambiguous transactions are found.");
                throw new BTSLBaseException("DWHFileCreation", "checkUnderprocessTransaction", PretupsErrorCodesI.DWH_AMB_OR_UP_TXN_FOUND);
            }
        } catch (BTSLBaseException be) {
            _logger.error("checkUnderprocessTransaction", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (SQLException sqe) {
            _logger.error("checkUnderprocessTransaction", "SQLException " + sqe.getMessage());
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[checkUnderprocessTransaction]", "",
                "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("DWHFileCreation", "checkUnderprocessTransaction", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _logger.error("checkUnderprocessTransaction", "Exception : " + ex.getMessage());
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[checkUnderprocessTransaction]", "",
                "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("DWHFileCreation", "checkUnderprocessTransaction", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
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
     * @throws BTSLBaseException
     * @return String
     */
    private static String createDirectory(String p_directoryPathAndName, String p_processId, Date p_beingProcessedDate) throws BTSLBaseException {
        final String METHOD_NAME = "createDirectory";
        if (_logger.isDebugEnabled()) {
            _logger.debug("createDirectory",
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
                throw new BTSLBaseException("DWHFileCreation", "createDirectory", PretupsErrorCodesI.COULD_NOT_CREATE_DIR);
            }
        } catch (BTSLBaseException be) {
            _logger.error("createDirectory", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception ex) {
            _logger.error("createDirectory", "Exception : " + ex.getMessage());
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[createDirectory]", "", "", "",
                "SQLException:" + ex.getMessage());
            throw new BTSLBaseException("DWHFileCreation", "createDirectory", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("createDirectory", "Exiting dirName=" + dirName);
            }
        }
        return dirName;
    }

    /**
     * This method will fetch all the required Channel transactions data from
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
     * @param p_startRowNum
     *            long
     * @param p_endRowNum
     *            long
     * @param p_fileNumber
     *            int
     * @return void
     * @throws SQLException
     *             ,Exception
     */
    private static void fetchChannelTransactionData(Connection p_con, Date p_beingProcessedDate, String p_dirPath, String p_fileName, String p_fileLabel, String p_fileEXT, long p_startRowNum, long p_endRowNum, int p_fileNumber) throws BTSLBaseException {
        final String METHOD_NAME = "fetchChannelTransactionData";
        if (_logger.isDebugEnabled()) {
            _logger
                .debug(
                    "fetchChannelTransactionData",
                    " Entered: p_beingProcessedDate=" + p_beingProcessedDate + " p_dirPath=" + p_dirPath + " p_fileName=" + p_fileName + " p_fileLabel=" + p_fileLabel + " p_fileEXT=" + p_fileEXT + " p_startRowNum=" + p_startRowNum + " p_endRowNum=" + p_endRowNum + " p_fileNumber=" + p_fileNumber);
        }

        final StringBuffer channelQueryBuf = new StringBuffer();
        channelQueryBuf.append(" SELECT DATA FROM TEMP_RP2P_DWH_CHTRANS");
        channelQueryBuf.append(" WHERE SRNO > ? and SRNO <= ? ");
        final String channelSelectQuery = channelQueryBuf.toString();
        if (_logger.isDebugEnabled()) {
            _logger.debug("fetchChannelTransactionData", "channel select query:" + channelSelectQuery);
        }
        PreparedStatement channelSelectPstmt = null;
        ResultSet channelSelectRst = null;

        try {
            channelSelectPstmt = p_con.prepareStatement(channelSelectQuery);
            channelSelectPstmt.setLong(1, p_startRowNum);
            channelSelectPstmt.setLong(2, p_endRowNum);
            channelSelectRst = channelSelectPstmt.executeQuery();
            _logger
                .debug(
                    "fetchChannelTransactionData",
                    "Memory after loading channel transaction data: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);

            // method call to write data in the files
            writeDataInFile(p_dirPath, p_fileName, p_fileLabel, p_beingProcessedDate, p_fileEXT, channelSelectRst, null, p_fileNumber, "1");
            _logger.debug("fetchChannelTransactionData", "Memory after writing transaction files: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime
                .getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
        } catch (BTSLBaseException be) {
            _logger.error("fetchChannelTransactionData", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (SQLException sqe) {
            _logger.error("fetchChannelTransactionData", "SQLException " + sqe.getMessage());
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[fetchChannelTransactionData]", "", "",
                "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("DWHFileCreation", "fetchChannelTransactionData", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _logger.error("fetchChannelTransactionData", "Exception : " + ex.getMessage());
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[fetchChannelTransactionData]", "", "",
                "", "SQLException:" + ex.getMessage());
            throw new BTSLBaseException("DWHFileCreation", "fetchChannelTransactionData", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (channelSelectRst != null) {
                try {
                    channelSelectRst.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (channelSelectPstmt != null) {
                try {
                    channelSelectPstmt.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }

            if (_logger.isDebugEnabled()) {
                _logger.debug("fetchChannelTransactionData", "Exiting ");
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
     * @param p_startRowNum
     *            long
     * @param p_endRowNum
     *            long
     * @param p_fileNumber
     *            int
     * @return void
     * @throws SQLException
     *             ,Exception
     */
    private static void fetchMasterData(Connection p_con, Date p_beingProcessedDate, String p_dirPath, String p_fileName, String p_fileLabel, String p_fileEXT, long p_startRowNum, long p_endRowNum, int p_fileNumber) throws BTSLBaseException {
        final String METHOD_NAME = "fetchMasterData";
        if (_logger.isDebugEnabled()) {
            _logger
                .debug(
                    "fetchMasterData",
                    " Entered:  p_beingProcessedDate=" + p_beingProcessedDate + " p_dirPath=" + p_dirPath + " p_fileName=" + p_fileName + " p_fileLabel=" + p_fileLabel + " p_fileEXT=" + p_fileEXT + ", p_startRowNum:" + p_startRowNum + ", p_endRowNum" + p_endRowNum + ", p_fileNumber" + p_fileNumber);
        }

        final StringBuffer queryBuf = new StringBuffer();
        queryBuf.append(" SELECT DATA FROM TEMP_RP2P_DWH_MASTER ");
        queryBuf.append(" WHERE SRNO > ? and SRNO <= ?");
        final String selectQuery = queryBuf.toString();
        if (_logger.isDebugEnabled()) {
            _logger.debug("fetchMasterData", "select query:" + selectQuery);
        }
        PreparedStatement selectPstmt = null;
        ResultSet selectRst = null;

        try {
            selectPstmt = p_con.prepareStatement(selectQuery);
            selectPstmt.clearParameters();
            selectPstmt.setLong(1, p_startRowNum);
            selectPstmt.setLong(2, p_endRowNum);
            selectRst = selectPstmt.executeQuery();
            _logger.debug("fetchMasterData", "Memory after loading master data: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime()
                .freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
            // method call to write data in files
            writeDataInFile(p_dirPath, p_fileName, p_fileLabel, p_beingProcessedDate, p_fileEXT, selectRst, null, p_fileNumber, "2");
            _logger.debug("fetchMasterData", "Memory after writing master files: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime()
                .freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
        } catch (BTSLBaseException be) {
            _logger.error("fetchMasterData", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (SQLException sqe) {
            _logger.error("fetchMasterData", "SQLException " + sqe.getMessage());
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[fetchMasterData]", "", "", "",
                "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("DWHFileCreation", "fetchMasterData", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _logger.error("fetchMasterData", "Exception : " + ex.getMessage());
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[fetchMasterData]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException("DWHFileCreation", "fetchMasterData", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
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
     * @param p_startRowNum
     *            long
     * @param p_endRowNum
     *            long
     * @param p_fileNumber
     *            int
     * @return void
     * @throws SQLException
     *             ,Exception
     */
    private static void fetchMasterDataForChannelUser(Connection p_con, Date p_beingProcessedDate, String p_dirPath, String p_fileName, String p_fileLabel, String p_fileEXT, long p_startRowNum, long p_endRowNum, int p_fileNumber) throws BTSLBaseException {
        final String METHOD_NAME = "fetchMasterDataForChannelUser";
        if (_logger.isDebugEnabled()) {
            _logger
                .debug(
                    "fetchMasterDataForChannelUser",
                    " Entered:  p_beingProcessedDate=" + p_beingProcessedDate + " p_dirPath=" + p_dirPath + " p_fileName=" + p_fileName + " p_fileLabel=" + p_fileLabel + " p_fileEXT=" + p_fileEXT + ", p_startRowNum:" + p_startRowNum + ", p_endRowNum" + p_endRowNum + ", p_fileNumber" + p_fileNumber);
        }

        final StringBuffer queryBuf = new StringBuffer();
        queryBuf.append(" SELECT DATA FROM TEMP_RP2P_DWH_MASTER ");
        queryBuf.append(" WHERE SRNO > ? and SRNO <= ?");
        final String selectQuery = queryBuf.toString();
        if (_logger.isDebugEnabled()) {
            _logger.debug("fetchMasterDataForChannelUser", "select query:" + selectQuery);
        }
        PreparedStatement selectPstmt = null;
        ResultSet selectRst = null;

        try {
            selectPstmt = p_con.prepareStatement(selectQuery);
            selectPstmt.clearParameters();
            selectPstmt.setLong(1, p_startRowNum);
            selectPstmt.setLong(2, p_endRowNum);
            selectRst = selectPstmt.executeQuery();
            _logger.debug("fetchMasterDataForChannelUser", "Memory after loading master data: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime
                .getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
            // method call to write data in files
            writeDataInFileChannelUser(p_dirPath, p_fileName, p_fileLabel, p_beingProcessedDate, p_fileEXT, selectRst, null, p_fileNumber, "2");
            _logger.debug("fetchMasterDataForChannelUser", "Memory after writing master files: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime
                .getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
        } catch (BTSLBaseException be) {
            _logger.error("fetchMasterDataForChannelUser", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (SQLException sqe) {
            _logger.error("fetchMasterDataForChannelUser", "SQLException " + sqe.getMessage());
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[fetchMasterDataForChannelUser]", "",
                "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("DWHFileCreation", "fetchMasterDataForChannelUser", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _logger.error("fetchMasterData", "Exception : " + ex.getMessage());
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[fetchMasterDataForChannelUser]", "",
                "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("DWHFileCreation", "fetchMasterDataForChannelUser", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
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
                _logger.debug("fetchMasterDataForChannelUser", "Exiting ");
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
    private static void writeDataInFile(String p_dirPath, String p_fileName, String p_fileLabel, Date p_beingProcessedDate, String p_fileEXT, ResultSet rst1, ResultSet rst2, int p_fileNumber, String call) throws BTSLBaseException {
        if (_logger.isDebugEnabled()) {
            _logger
                .debug(
                    "writeDataInFile",
                    " Entered:  p_dirPath=" + p_dirPath + " p_fileName=" + p_fileName + " p_fileLabel=" + p_fileLabel + " p_beingProcessedDate=" + p_beingProcessedDate + " p_fileEXT=" + p_fileEXT + " p_fileNumber=" + p_fileNumber);
        }
        final String METHOD_NAME = "writeDataInFile";
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

            _logger.debug("writeDataInFile", "  fileName=" + fileName);

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
                if (fileName.contains(_masterDirectoryPathAndName)) {
                    fileName = fileName.replace(_masterDirectoryPathAndName, _finalMasterDirectoryPath);
                }
                try {
                    generateDataFileSummary(p_beingProcessedDate, recordsWrittenInFile, fileName);
                } catch (Exception e) {
                    _logger.errorTrace(METHOD_NAME, e);
                }
                // Ended Here
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
        } catch (SQLException e) {
            deleteAllFiles();
            _logger.debug("writeDataInFile", "SQLException: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[writeDataInFile]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("DWHFileCreation", "writeDataInFile", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } catch (IOException e) {
            deleteAllFiles();
            _logger.debug("writeDataInFile", "IOException: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[writeDataInFile]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("DWHFileCreation", "writeDataInFile", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } catch (Exception e) {
            deleteAllFiles();
            _logger.debug("writeDataInFile", "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[writeDataInFile]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("DWHFileCreation", "writeDataInFile", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
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
     * 
     * */
    private static void writeDataInFileChannelUser(String p_dirPath, String p_fileName, String p_fileLabel, Date p_beingProcessedDate, String p_fileEXT, ResultSet rst1, ResultSet rst2, int p_fileNumber, String call) throws BTSLBaseException {
        final String METHOD_NAME = "writeDataInFileChannelUser";
        if (_logger.isDebugEnabled()) {
            _logger
                .debug(
                    "writeDataInFileChannelUser",
                    " Entered:  p_dirPath=" + p_dirPath + " p_fileName=" + p_fileName + " p_fileLabel=" + p_fileLabel + " p_beingProcessedDate=" + p_beingProcessedDate + " p_fileEXT=" + p_fileEXT + " p_fileNumber=" + p_fileNumber);
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
                p_beingProcessedDate = BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate);

            } catch (Exception e) {
                p_beingProcessedDate = p_beingProcessedDate;
                _logger.errorTrace(METHOD_NAME, e);
            }
            // generating file name
            fileNumber = p_fileNumber;
            // if the length of file number is 1, two zeros are added as prefix
            if (Integer.toString(fileNumber).length() == 1) {
               // fileName = p_dirPath + File.separator + p_fileName + "_" + p_beingProcessedDate.toString().substring(0, 4) + "_" + p_beingProcessedDate.toString().substring(
                   // 5, 7) + "_" + p_beingProcessedDate.toString().substring(8, 10) + p_fileEXT;
            	fileName = p_dirPath + File.separator + p_fileName + "_"+ BTSLUtil.getDateStrForName(p_beingProcessedDate) + p_fileEXT;
            }

            _logger.debug("writeDataInFile", "  fileName=" + fileName);

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

            while (rst1 != null && rst1.next()) {
                fileData = rst1.getString("DATA");
                fileDataArray = fileData.split(",");
                fileData = fileDataArray[5] + "," + fileDataArray[7] + "," + fileDataArray[4] + "," + fileDataArray[6] + "," + fileDataArray[9] + "," + fileDataArray[10] + "," + fileDataArray[12];
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
        } catch (Exception e) {
            deleteAllFiles();
            _logger.debug("writeDataInFileChannelUser", "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[writeDataInFileChannelUser]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("DWHFileCreation", "writeDataInFileChannelUser", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (out != null) {
                out.close();
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("writeDataInFileChannelUser", "Exiting ");
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[markProcessStatusAsComplete]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("DWHFileCreation", "markProcessStatusAsComplete", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
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
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[deleteAllFiles]", "", "", "",
                    "Exception:" + e.getMessage());
                throw new BTSLBaseException("DWHFileCreation", "deleteAllFiles", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
            }
        }// end of for loop
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[deleteAllFiles]", "", "", "",
            " Message: DWHFileCreation process has found some error, so deleting all the files.");
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
    private static String constructFileHeader(Date p_beingProcessedDate, long p_fileNumber, String p_fileLabel) {
        final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYY);
        final StringBuffer fileHeaderBuf = new StringBuffer("");
        fileHeaderBuf.append("Present Date=" + BTSLDateUtil.getSystemLocaleDate(sdf.format(new Date())));
        fileHeaderBuf.append("\n" + "For Date=" + BTSLDateUtil.getSystemLocaleDate(sdf.format(p_beingProcessedDate)));
        fileHeaderBuf.append("\n" + "File Number=" + p_fileNumber);
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
        fileHeaderBuf.append("Number of records=" + p_noOfRecords);
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
        // child directory name includes a file name and being processed date,
        // month and year
         final String oldDirName =p_oldDirectoryPath + File.separator + p_processId + "_"+ BTSLUtil.getDateStrForName(p_beingProcessedDate);
          
         final String newDirName = p_finalDirectoryPath + File.separator + p_processId + "_"+ BTSLUtil.getDateStrForName(p_beingProcessedDate);
        
        File oldDir = new File(oldDirName);
        File newDir = new File(newDirName);
        if (!newDir.exists()) {
            newDir.mkdirs();
        }

        if (_logger.isDebugEnabled()) {
            _logger.debug("moveFilesToFinalDirectory", " newDirName=" + newDirName);
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[moveFilesToFinalDirectory]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("DWHFileCreation", "deleteAllFiles", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
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
     * This method will fetch all the required C2S transactions data from
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
     * @param p_startRowNum
     *            long
     * @param p_endRowNum
     *            long
     * @param p_fileNumber
     *            int
     * @return void
     * @throws SQLException
     *             ,Exception
     */
    private static void fetchC2STransactionData(Connection p_con, Date p_beingProcessedDate, String p_dirPath, String p_fileName, String p_fileLabel, String p_fileEXT, long p_startRowNum, long p_endRowNum, int p_fileNumber) throws BTSLBaseException {
        final String METHOD_NAME = "fetchC2STransactionData";
        if (_logger.isDebugEnabled()) {
            _logger
                .debug(
                    "fetchC2STransactionData",
                    " Entered: p_beingProcessedDate=" + p_beingProcessedDate + " p_dirPath=" + p_dirPath + " p_fileName=" + p_fileName + " p_fileLabel=" + p_fileLabel + " p_fileEXT=" + p_fileEXT + " p_startRowNum=" + p_startRowNum + " p_endRowNum=" + p_endRowNum + " p_fileNumber=" + p_fileNumber);
        }

        final StringBuffer channelQueryBuf = new StringBuffer();
        channelQueryBuf.append(" SELECT DATA,TRANSFER_STATUS,BONUS_DETAILS FROM TEMP_RP2P_DWH_C2STRANS");
        channelQueryBuf.append(" WHERE SRNO > ? and SRNO <= ? ");
        final String channelSelectQuery = channelQueryBuf.toString();
        if (_logger.isDebugEnabled()) {
            _logger.debug("fetchC2STransactionData", "channel select query:" + channelSelectQuery);
        }
        PreparedStatement channelSelectPstmt = null;
        ResultSet channelSelectRst = null;

        try {
            channelSelectPstmt = p_con.prepareStatement(channelSelectQuery);
            channelSelectPstmt.setLong(1, p_startRowNum);
            channelSelectPstmt.setLong(2, p_endRowNum);
            channelSelectRst = channelSelectPstmt.executeQuery();
            _logger
                .debug("fetchC2STransactionData", "Memory after loading channel transaction data: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime
                    .getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);

            // method call to write data in the files
            writeDataInFile(p_dirPath, p_fileName, p_fileLabel, p_beingProcessedDate, p_fileEXT, null, channelSelectRst, p_fileNumber, "1");
            _logger.debug("fetchC2STransactionData", "Memory after writing transaction files: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime
                .getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
        } catch (BTSLBaseException be) {
            _logger.error("fetchC2STransactionData", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (SQLException sqe) {
            _logger.error("fetchC2STransactionData", "SQLException " + sqe.getMessage());
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[fetchC2STransactionData]", "", "", "",
                "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("DWHFileCreation", "fetchC2STransactionData", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _logger.error("fetchChannelTransactionData", "Exception : " + ex.getMessage());
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[fetchChannelTransactionData]", "", "",
                "", "SQLException:" + ex.getMessage());
            throw new BTSLBaseException("DWHFileCreation", "fetchChannelTransactionData", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (channelSelectRst != null) {
                try {
                    channelSelectRst.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            if (channelSelectPstmt != null) {
                try {
                    channelSelectPstmt.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }

            if (_logger.isDebugEnabled()) {
                _logger.debug("fetchC2STransactionData", "Exiting ");
            }
        }// end of finally
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
        if (_logger.isDebugEnabled()) {
            _logger
                .debug(
                    "DWHFileCreation",
                    " Entered: generateDataFileSummary p_beingProcessedDate=" + p_beingProcessedDate + ", p_recordsWrittenInFile=" + p_recordsWrittenInFile + ", p_fileName=" + p_fileName);
        }
        final String METHOD_NAME = "generateDataFileSummary";
        try {
            final String processDateStr = BTSLUtil.getDateStringFromDate(p_beingProcessedDate);
            if (_fileRecordMap.size() == 0) {
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
            _logger.debug("DWHFileCreation", " generateDataFileSummary() While recoding file list Exception: " + e.getMessage());
        } finally {
            // 09-SEP-2014 for OCI client
            String isSummaryFileReq = "N";
            isSummaryFileReq = Constants.getProperty("DWH_SUMMARY_FILE_REQUIRED");
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
                _logger.debug("DWHFileCreation", "Exiting generateDataFileSummary() ");
            }
        }
    }

    /**
     * @author diwakar
     * @date : 04-MAR-2014
     * @param p_dirPath
     * @param p_fileEXT
     * @param processedDate
     * @throws BTSLBaseException
     */
    private static void writeFileSummary(String p_dirPath, String p_fileEXT, Date p_beingProcessedDate) throws BTSLBaseException {
        final String METHOD_NAME = "writeFileSummary";
        if (_logger.isDebugEnabled()) {
            _logger.debug("DWHFileCreation",
                " Entered: writeFileSummary() p_dirPath=" + p_dirPath + ", p_fileEXT=" + p_fileEXT + ", p_beingProcessedDate=" + p_beingProcessedDate);
        }
        PrintWriter out = null;
        File newFile = null;
        try {
            String fileName = null;
            String fileData = null;
            String fileHeader = null;
            String processDate = null;
            fileName = p_dirPath + File.separator + ProcessI.DWH_PROCESSID + "Trans_Stat_" + BTSLUtil.getDateStrForName(p_beingProcessedDate) + p_fileEXT;
            // Ended Here
            _logger.debug("DWHFileCreation", " writeFileSummary() fileName=" + fileName);
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
                transSeperator = Constants.getProperty("DWH_TRANSCATION_STAT_SEPERATOR");
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
                _logger.error("DWHFileCreation", " writeFileSummary() fileName=" + fileName + " has been created successfully.");
            } else {
                _logger.error("DWHFileCreation", " writeFileSummary() fileName=" + fileName + " does not exists on system.");
            }
        } catch (IOException e) {
            _logger.debug("DWHFileCreation", "Exception writeFileSummary(): " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[writeFileSummary]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("DWHFileCreation", "writeFileSummary()", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } catch (Exception e) {
            _logger.debug("DWHFileCreation", "Exception writeFileSummary(): " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[writeFileSummary]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("DWHFileCreation", "writeFileSummary()", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
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
                _logger.debug("DWHFileCreation", "Exiting writeFileSummary() ");
            }
        }
    }
}

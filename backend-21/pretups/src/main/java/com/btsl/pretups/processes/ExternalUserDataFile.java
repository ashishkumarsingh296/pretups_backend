package com.btsl.pretups.processes;

/**
 * @(#)ExternalUserDataFile.java
 *                               Copyright(c) 2009, Bharti Telesoft Ltd.
 *                               All Rights Reserved
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Author Date History
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Vinay Kumar Singh March 20, 2009 Initial
 *                               Creation
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               This process will generate the External User
 *                               CRM Data for each network defined in csv
 *                               format.
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.processes.businesslogic.IntervalTimeVO;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.ibm.icu.util.Calendar;

public class ExternalUserDataFile {

    private static Log _logger = LogFactory.getLog(ExternalUserDataFile.class.getName());
    private static String _extUsrDataFileLabel = null;
    private static String _userQueryFile = null;
    private static boolean _isHeaderShow = false;
    private static boolean _isFooterShow = false;
    private static String _sqlDateTimeFormat = null;
    private static String _dateTimeFormat = null;
    private static SimpleDateFormat _simpleDateFormat = null;
    private static String _networkCode = null;
    private String _filePath = null;
    private String _fileName = null;

    /**
     * Method main
     * 
     * @param args
     *            String[]
     *            args[0] = Constants.props (With path)
     *            args[1] = ProcessLogConfig.props (With path)
     *            args[2] = QueryFile.props (With path)
     *            args[3] = Process interval nemuric value (24 hours for a day)
     * @author Vinay Singh
     */
    public static void main(String[] args) {
        final String METHOD_NAME = "main";
        try {
            if (args.length > 4 || args.length < 3)// check the argument length
            {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("ExternalUserDataFile ::", " Not sufficient arguments, please pass Conatnsts.props ProcessLogconfig.props QueryFile.props ProcessInterval");
                }
                return;
            }
            final File constantsFile = new File(args[0]);
            if (!constantsFile.exists())// check file (Constants.props) exist or
            // not
            {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("ExternalUserDataFile", " Constants File Not Found at the path : " + args[0]);
                }
                return;
            }
            final File logconfigFile = new File(args[1]);
            if (!logconfigFile.exists())// check file (ProcessLogConfig.props)
            // exist or not
            {
                _logger.debug("ExternalUserDataFile", " ProcessLogConfig File Not Found at the path : " + args[1]);
                return;
            }
            final File queryFile = new File(args[2]);
            if (!queryFile.exists())// check file (QueryFile.props) exist or not
            {
                _logger.debug("ExternalUserDataFile", " QueryFile File Not Found at the path : " + args[2]);
                return;
            }
            if (!BTSLUtil.isNullString(args[3]))// Process Interval
            {
                if (!BTSLUtil.isNumeric(args[3]))// check the Process Interval
                // is numeric
                {
                    _logger.debug("ExternalUserDataFile ::", " Invalid Process Interval " + args[3] + " It should be between 1 - 24");
                    return;
                } else if (Integer.parseInt(args[3]) < 1) {
                    _logger.debug("ExternalUserDataFile ::", " Invalid Process Interval " + args[3] + " It should be between 1 - 24");
                    return;
                }
            } else {
                _logger.debug("ExternalUserDataFile", " Process Interval missing ");
                return;
            }
            // use to load the Constants.props and ProcessLogConfig.props files
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            // load query file
            Constants.load(args[2]);
        }// end of try
        catch (Exception e) {
            _logger.error("main", " Error in loading the Cache information.." + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ExternalUserDataFile[main]", "", "", "",
                "  Error in loading the Cache information");
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {
            // Load the query.
            final String crmUserQry = "EXTERNAL_USRDATA_QUERY";
            _userQueryFile = Constants.getProperty(crmUserQry);
            if (BTSLUtil.isNullString(_userQueryFile)) {
                _logger.error("main", " Error in loading the New User Query information, key :" + crmUserQry);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ExternalUserDataFile[main]", "", "", "",
                    "  Error in loading the QueryFile key " + crmUserQry);
                ConfigServlet.destroyProcessCache();
                return;
            }
        } catch (Exception e) {
            _logger.error("main", " Main: Error in loading the Query information.." + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ExternalUserDataFile[main]", "", "", "",
                "  Error in loading the QueryFile");
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {
            if (PretupsI.YES.equalsIgnoreCase(BTSLUtil.NullToString(Constants.getProperty("SHOW_HEADER_EXTERNAL_USRDATA_FILE")))) {
                _isHeaderShow = true;
            }
            if (PretupsI.YES.equalsIgnoreCase(BTSLUtil.NullToString(Constants.getProperty("SHOW_FOOTER_EXTERNAL_USRDATA_FILE")))) {
                _isFooterShow = true;
            }
        } catch (Exception ee) {
            _logger.error("main:", " Missing/Wrong entry from Constants.props for [SHOW_HEADER_EXTERNAL_USRDATA_FILE] or [SHOW_FOOTER_EXTERNAL_USRDATA_FILE] " + ee
                .getMessage());
            _logger.errorTrace(METHOD_NAME, ee);
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            final ExternalUserDataFile extUsrDataFile = new ExternalUserDataFile();
            extUsrDataFile.generateExtUsrDataFile(Integer.parseInt(args[3]));
        } catch (BTSLBaseException be) {
            _logger.error("main", " BTSLBaseException: " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            // event handler
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExternalUserDataFile[main]", "", "", "",
                "BTSLBaseException:" + be.getMessage());
        } catch (Exception e) {
            _logger.error("main", " Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            // event handler
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExternalUserDataFile[main]", "", "", "",
                "Exception:" + e.getMessage());
        } finally {
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * Method generateExtUsrDataFile
     * This method will load all the networks from the Constants.props for which
     * the process has to execute.
     * 
     * @param p_processInterval
     *            int
     * @throws BTSLBaseException
     * @author Vinay Singh
     */
    private void generateExtUsrDataFile(int p_processInterval) throws BTSLBaseException {
        final String METHOD_NAME = "generateExtUsrDataFile";
        if (_logger.isDebugEnabled()) {
            _logger.debug("generateExtUsrDataFile", " Entered:  p_processInterval : " + p_processInterval);
        }
        int index = 0;
        int noOfNetworks = 0;
        try {
            final String networks = Constants.getProperty("EXTERNAL_USRDATA_FILE_NETWORKS");
            _logger.debug("generateExtUsrDataFile", " Defined networks are: " + networks);
            if (!BTSLUtil.isNullString(networks)) {
                index = networks.indexOf(",");
                if (index != -1) {
                    final String[] definedNetworks = networks.split(",");
                    noOfNetworks = definedNetworks.length;
                    for (int i = 0; i < noOfNetworks; i++) {
                        _networkCode = definedNetworks[i];
                        this.process(p_processInterval);
                    }
                } else {
                    _networkCode = networks;
                    this.process(p_processInterval);
                }
            } else {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("generateExtUsrDataFile: ", "Networks are not defined in the Constant.props.");
                }
                throw new BTSLBaseException(this, "generateExtUsrDataFile: ", "Not able to generate file because networks are not defined in the Constant.props.");
            }
        } catch (BTSLBaseException be) {
            _logger.error("generateExtUsrDataFile", " BTSLBaseException : " + be.getMessage());
            throw be;
        } catch (Exception be) {
            _logger.error("generateExtUsrDataFile", " Exception : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw new BTSLBaseException(this, "generateExtUsrDataFile", " Exception=" + be.getMessage());
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("generateExtUsrDataFile", " Exiting..... ");
            }
        }
    }

    /**
     * Method process
     * This method is for creating database connection and processing the
     * process interval.
     * 
     * @param p_processInterval
     *            int
     * @throws BTSLBaseException
     * @author Vinay Singh
     */
    private void process(int p_processInterval) throws BTSLBaseException {
        final String METHOD_NAME = "process";
        if (_logger.isDebugEnabled()) {
            _logger.debug("process", " Entered:  p_processInterval : " + p_processInterval);
        }
        Connection con = null;
        ProcessBL processBL = null;
        ProcessStatusVO processVO = null;
        ProcessStatusDAO processDAO = null;
        ArrayList intervalList = null;
        try {
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("process: ", "DATABASE Connection is NULL ");
                }
                throw new BTSLBaseException(this, "process: ", "Not able to get the connection");
            }
            try {
                processBL = new ProcessBL();
                final String processID = ProcessI.EXTERAL_USRDATA_FILE_PROCESS_ID;
                processVO = processBL.checkProcessUnderProcessNetworkWise(con, processID, _networkCode);
                if (processVO.isStatusOkBool()) {
                    con.commit();
                    intervalList = this.generateInterval(processVO, p_processInterval);
                    this.fetchExtUsrDataFile(con, intervalList, processVO);
                } else {
                    throw new BTSLBaseException("ExternalUserDataFile: ", "process::", " Process is already running.");
                }
            } catch (BTSLBaseException e1) {
                _logger.error("process", " BTSLBaseException : ExternalUserDataFile: " + e1.getMessage());
                _logger.errorTrace(METHOD_NAME, e1);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ExternalUserDataFile[process] ExternalUserDataFile ", "", "", "", "BTSLBaseException:" + e1.getMessage());
            } catch (Exception e2) {
                _logger.error("process", " Exception : ExternalUserDataFile: " + e2.getMessage());
                _logger.errorTrace(METHOD_NAME, e2);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "ExternalUserDataFile[process] ExternalUserDataFile ", "", "", "", "Exception:" + e2.getMessage());
            } finally {
                try {
                    if (processVO.isStatusOkBool()) {
                        processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                        processDAO = new ProcessStatusDAO();
                        if (processDAO.updateProcessDetailNetworkWise(con, processVO) > 0) {
                            con.commit();
                        } else {
                            con.rollback();
                        }
                    }
                } catch (Exception e) {
                    _logger.error("process", " Exception in update process detail" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "ExternalUserDataFile[process] ExternalUserDataFile ", "", "", "",
                        "Exception in update process detail for Process ID=" + PretupsI.EXTERAL_FILE_PROCESS_ID + " :" + e.getMessage());
                    _logger.errorTrace(METHOD_NAME, e);
                }
            }
        } catch (Exception be) {
            _logger.error("process", " Exception : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw new BTSLBaseException(this, "process", " Exception=" + be.getMessage());
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("process", " Exiting..... ");
            }
        }
    }

    /**
     * Method fetchExtUsrDataFile
     * This method is for fetching the data from the database.
     * 
     * @param p_con
     *            Connection
     * @param p_intervalList
     *            ArrayList
     * @param p_processVO
     *            ProcessStatusVO
     * @throws BTSLBaseException
     * @author Vinay Singh
     */
    private void fetchExtUsrDataFile(Connection p_con, ArrayList p_intervalList, ProcessStatusVO p_processVO) throws BTSLBaseException {
        final String METHOD_NAME = "fetchExtUsrDataFile";
        if (_logger.isDebugEnabled()) {
            _logger.debug("fetchExtUsrDataFile:", " Entered, Interval list size=" + p_intervalList.size() + " Process VO=" + p_processVO.toString());
        }
        int i = 0;
        String toDate = null;
        String fromDate = null;
        ResultSet rs = null;
        Timestamp startTimestamp = null;
        Timestamp endTimestamp = null;
        IntervalTimeVO intervalTimeVO = null;
        ProcessStatusDAO processDAO = null;
        PreparedStatement pstmtSelect = null;
        try {
            // getting all the required parameters from Constants.props
            this.loadConstantParameters();
            final File newUserDileDir = new File(_filePath);
            // Chech whether the directory exist or not for the generation of
            // new users data, if not then make that directory.
            if (!newUserDileDir.isDirectory()) {
                newUserDileDir.mkdirs();
            }
            // Query
            if (_logger.isDebugEnabled()) {
                _logger.debug("fetchExtUsrDataFile:", " Select query=" + _userQueryFile);
            }

            pstmtSelect = p_con.prepareStatement(_userQueryFile, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            processDAO = new ProcessStatusDAO();
            for (int j = 0, k = p_intervalList.size(); j < k; j++) {
                startTimestamp = null;
                intervalTimeVO = (IntervalTimeVO) p_intervalList.get(j);
                startTimestamp = intervalTimeVO.getStartTime();
                endTimestamp = intervalTimeVO.getEndTime();
                // Set the parameters of prepared statement for new users
                i = 1;
                pstmtSelect.clearParameters();
                pstmtSelect.setString(i++, _sqlDateTimeFormat);
                pstmtSelect.setString(i++, p_processVO.getNetworkCode());
                pstmtSelect.setTimestamp(i++, startTimestamp);
                pstmtSelect.setTimestamp(i++, endTimestamp);
                pstmtSelect.setTimestamp(i++, startTimestamp);
                pstmtSelect.setTimestamp(i++, endTimestamp);
                pstmtSelect.setTimestamp(i++, startTimestamp);
                pstmtSelect.setTimestamp(i++, endTimestamp);
                pstmtSelect.setTimestamp(i++, startTimestamp);
                pstmtSelect.setTimestamp(i++, endTimestamp);
                pstmtSelect.setString(i++, _sqlDateTimeFormat);
                pstmtSelect.setString(i++, p_processVO.getNetworkCode());
                pstmtSelect.setTimestamp(i++, startTimestamp);
                pstmtSelect.setTimestamp(i++, endTimestamp);
                // execute the statement
                rs = pstmtSelect.executeQuery();
                // Get the from date and to date to generate the file
                fromDate = _simpleDateFormat.format(BTSLUtil.getUtilDateFromTimestamp(intervalTimeVO.getStartTime()));
                toDate = _simpleDateFormat.format(BTSLUtil.getUtilDateFromTimestamp(endTimestamp));
                // method call to write data in the files
                if (rs.next()) {
                    writeExtUserDataInFile(rs, _networkCode, _fileName, fromDate, toDate, _filePath, endTimestamp);
                }
                // Close the result set for new users
                // Set the process status as under process.
                p_processVO.setProcessStatus(ProcessI.STATUS_UNDERPROCESS);
                p_processVO.setExecutedUpto(intervalTimeVO.getEndTime());
                p_processVO.setExecutedOn(new Date());
                if (processDAO.updateProcessDetailNetworkWise(p_con, p_processVO) > 0) {
                    p_con.commit(); // Commiting the changes
                } else {
                    p_processVO.setExecutedUpto(startTimestamp);
                    p_con.rollback();
                    throw new BTSLBaseException("ExternalUserDataFile ", "fetchExtUsrDataFile ", "Process Status is not commited");
                }
            }
        } catch (BTSLBaseException be) {
            if (startTimestamp != null) {
                p_processVO.setExecutedUpto(startTimestamp);
            }
            _logger.error("fetchExtUsrDataFile ", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (SQLException sqe) {
            if (startTimestamp != null) {
                p_processVO.setExecutedUpto(startTimestamp);
            }
            _logger.error("fetchExtUsrDataFile ", "SQLException " + sqe.getMessage());
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExternalUserDataFile[fetchExtUsrDataFile]", "", "",
                "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("ExternalUserDataFile ", "fetchExtUsrDataFile", " SQLException :" + sqe.getMessage());
        } catch (Exception ex) {
            if (startTimestamp != null) {
                p_processVO.setExecutedUpto(startTimestamp);
            }
            _logger.error("fetchExtUsrDataFile ", "Exception : " + ex.getMessage());
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExternalUserDataFile[fetchExtUsrDataFile]", "", "",
                "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("ExternalUserDataFile: ", "fetchExtUsrDataFile ", "Exception : " + ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                } // Closing the result set
            }
            if (pstmtSelect != null) {
                try {
                    pstmtSelect.close();
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                } // Closing the prepared statement
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("fetchExtUsrDataFile ", "Exiting ");
            }
        }
    }

    /**
     * Method writeExtUserDataInFile
     * This method is for writing data in CSV file.
     * 
     * @param p_rs
     *            ResultSet
     * @param p_fileName
     *            String
     * @param p_fromDate
     *            String
     * @param p_toDate
     *            String
     * @param p_filePath
     *            String
     * @param p_endTime
     *            TODO
     * @throws BTSLBaseException
     * @author Vinay Singh
     */
    private void writeExtUserDataInFile(ResultSet p_rs, String p_networkCode, String p_fileName, String p_fromDate, String p_toDate, String p_filePath, Timestamp p_endTime) throws BTSLBaseException {
        final String METHOD_NAME = "writeExtUserDataInFile";
        if (_logger.isDebugEnabled()) {
            _logger
                .debug(
                    "writeExtUserDataInFile",
                    " Entered:  p_rs=" + p_rs + " p_networkCode : " + p_networkCode + " p_fileName : " + p_fileName + " p_fromDate : " + p_fromDate + " p_toDate : " + p_toDate + " File Path=" + p_filePath + " End Time=" + p_endTime);
        }
        PrintWriter out = null;
        File newFile = null;
        String fileData = null;
        String fileHeader = null;
        String fileFooter = null;
        int recordsWrittenInFile = 0;
        String finalFileName = null;
        final Calendar cal = BTSLDateUtil.getInstance();
        try {
            cal.setTime(p_endTime);
            finalFileName = p_filePath + p_networkCode + "-" + p_fileName + "-" + getFileNameStringFromDate(BTSLUtil.getDateFromDateString(p_fromDate, _dateTimeFormat)) + "-" + cal
                .get(Calendar.HOUR_OF_DAY) + "-" + cal.get(Calendar.MINUTE) + ".csv";
            newFile = new File(finalFileName);
            out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
            if (_isHeaderShow) // For showing file header
            {
                fileHeader = constructFileHeader(p_fromDate, p_toDate, _extUsrDataFileLabel);
                out.write(fileHeader + "\n");
            }
            while (p_rs.next()) { // Iterating the result set for writing data.
                fileData = p_rs.getString(1);
                out.write(fileData + "\n");
                recordsWrittenInFile++;
            }
            // end of while(p_rs.next())
            _logger.debug("writeExtUserDataInFile", " No. of records written in the file=" + recordsWrittenInFile);
            // if number of records are not zero then footer is appended
            // otherwise file will be deleted
            if (recordsWrittenInFile > 0) {
                if (_isFooterShow) {
                    fileFooter = constructFileFooter(recordsWrittenInFile);
                    out.write(fileFooter);
                }
            }
            // Deleting the file if there are no records.
            else {
                if (out != null) {
                    out.close();
                }
                if (newFile != null) {
                	boolean isDeleted = newFile.delete();
                    if(isDeleted){
                    	_logger.debug(METHOD_NAME, "File deleted successfully");
                    }
                }
            }
        } catch (BTSLBaseException e) {
            _logger.debug("writeExtUserDataInFile", " Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            if (newFile != null) {
            	boolean isDeleted = newFile.delete();
                if(isDeleted){
                	_logger.debug(METHOD_NAME, "File deleted successfully");
                }
                newFile = null;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExternalUserDataFile[writeExtUserDataInFile]", "", "",
                "", " Exception:" + e.getMessage());
            throw new BTSLBaseException("ExternalUserDataFile ", "writeExtUserDataInFile", " Exception: " + e.getMessage());
        } catch (IOException e) {
            _logger.debug("writeExtUserDataInFile", " Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            if (newFile != null) {
            	boolean isDeleted = newFile.delete();
                if(isDeleted){
                	_logger.debug(METHOD_NAME, "File deleted successfully");
                }
                newFile = null;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExternalUserDataFile[writeExtUserDataInFile]", "", "",
                "", " Exception:" + e.getMessage());
            throw new BTSLBaseException("ExternalUserDataFile ", "writeExtUserDataInFile", " Exception: " + e.getMessage());
        } catch (ParseException e) {
            _logger.debug("writeExtUserDataInFile", " Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);

            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExternalUserDataFile[writeExtUserDataInFile]", "", "",
                "", " Exception:" + e.getMessage());
            throw new BTSLBaseException("ExternalUserDataFile ", "writeExtUserDataInFile", " Exception: " + e.getMessage());
        } catch (SQLException e) {
            _logger.debug("writeExtUserDataInFile", " Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            if (newFile != null) {
            	boolean isDeleted = newFile.delete();
                if(isDeleted){
                	_logger.debug(METHOD_NAME, "File deleted successfully");
                }
                newFile = null;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExternalUserDataFile[writeExtUserDataInFile]", "", "",
                "", " Exception:" + e.getMessage());
            throw new BTSLBaseException("ExternalUserDataFile ", "writeExtUserDataInFile", " Exception: " + e.getMessage());
        } catch (Exception e) {
            _logger.debug("writeExtUserDataInFile", " Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            if (newFile != null) {
            	boolean isDeleted = newFile.delete();
                if(isDeleted){
                	_logger.debug(METHOD_NAME, "File deleted successfully");
                }
                newFile = null;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExternalUserDataFile[writeExtUserDataInFile]", "", "",
                "", " Exception:" + e.getMessage());
            throw new BTSLBaseException("ExternalUserDataFile ", "writeExtUserDataInFile", " Exception: " + e.getMessage());
        } finally {
            if (out != null) {
                out.close(); // Closing the PrintWriter
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("writeExtUserDataInFile ", "Exiting ");
            }
        }
    }

    /**
     * This method is used to constuct file header
     * 
     * @param p_fromDate
     *            String
     * @param p_toDate
     *            String
     * @param p_fileLabel
     *            String
     * @return String fileHeaderBuf
     * @throws BTSLBaseException
     * @author Vinay Singh
     */
    private String constructFileHeader(String p_fromDate, String p_toDate, String p_fileLabel) throws BTSLBaseException {
        final String METHOD_NAME = "constructFileHeader";
        if (_logger.isDebugEnabled()) {
            _logger.debug("constructFileHeader[ExternalUserDataFile]", " Entered: p_fromDate :" + p_fromDate + " p_toDate :" + p_toDate + " p_fileLabel :" + p_fileLabel);
        }
        StringBuffer fileHeaderBuf = null;
        try {
            fileHeaderBuf = new StringBuffer("From Date=" + p_fromDate);
            fileHeaderBuf.append("\n" + "To Date=" + p_toDate);
            fileHeaderBuf.append("\n" + "[STARTDATA]");
            fileHeaderBuf.append("\n" + p_fileLabel);
        } catch (Exception e) {
            _logger.error("constructFileHeader[ExternalUserDataFile] ", "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ExternalUserDataFile ", "constructFileHeader", " Exception: " + e.getMessage());
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug("constructFileHeader[ExternalUserDataFile] ", "Exiting: File header=" + fileHeaderBuf.toString());
        }
        return fileHeaderBuf.toString();
    }

    /**
     * This method is used to constuct file footer
     * 
     * @param p_noOfRecords
     *            long
     * @return String fileFooterBuf
     * @throws BTSLBaseException
     * @author Vinay Singh
     */
    private String constructFileFooter(long p_noOfRecords) throws BTSLBaseException {
        final String METHOD_NAME = "constructFileFooter";
        if (_logger.isDebugEnabled()) {
            _logger.debug("constructFileFooter[ExternalUserDataFile] ", " Entered: ");
        }
        StringBuffer fileFooterBuf = null;
        try {
            fileFooterBuf = new StringBuffer("[ENDDATA]" + "\n");
            fileFooterBuf.append("Number of records=" + p_noOfRecords + "\n");
        } catch (Exception e) {
            _logger.error("constructFileHeader[ExternalUserDataFile] ", "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException("ExternalUserDataFile ", "constructFileFooter", " Exception: " + e.getMessage());
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug("constructFileFooter[ExternalUserDataFile] ", "Exiting: File footer=" + fileFooterBuf.toString());
        }
        return fileFooterBuf.toString();
    }

    /**
     * Method generateInterval
     * Used to generate the interval according to the Executed upto and current
     * date
     * The formula used is -> process
     * duration=(currentTime-ExecutedUpto-beforeInterval)
     * 
     * @param p_processStatusVO
     *            ProcessStatusVO
     * @param p_intervalHour
     *            int
     * @return intervalTimeVOList ArrayList
     * @throws Exception
     * @author Vinay Singh
     */
    private ArrayList generateInterval(ProcessStatusVO p_processStatusVO, int p_intervalHour) throws Exception {
        final String METHOD_NAME = "generateInterval";
        if (_logger.isDebugEnabled()) {
            _logger.debug("generateInterval", "Entered p_processStatusVO=" + p_processStatusVO.toString() + " p_intervalHour=" + p_intervalHour);
        }
        Date currDate = null;
        Date endDate = null;
        Calendar cal = null;
        Timestamp startTime = null;
        Timestamp endTime = null;
        int startDateOfInterval;
        int endDateOfInterval;
        ArrayList intervalTimeVOList = null;
        IntervalTimeVO intervalTimeVO = null;
        try {
            currDate = new Date();
            intervalTimeVOList = new ArrayList();
            final int beforeInterval = BTSLUtil.parseLongToInt( p_processStatusVO.getBeforeInterval() / 60);
            final long diffInMillis = currDate.getTime() - p_processStatusVO.getExecutedUpto().getTime() - beforeInterval;
            final long processDuration = diffInMillis / (1000 * 60 * 60);
            long parts = processDuration / p_intervalHour;
            if (processDuration % p_intervalHour > 0) {
                parts = parts + 1;
            }

            cal = Calendar.getInstance();
            cal.add(Calendar.HOUR_OF_DAY, -beforeInterval);
            currDate = cal.getTime();
            // Iterate for the intervals.
            for (long i = 0; i <= parts; i++) {
                intervalTimeVO = new IntervalTimeVO();
                intervalTimeVO.setStartTime(BTSLUtil.getTimestampFromUtilDate(BTSLUtil.getSQLDateFromUtilDate(p_processStatusVO.getExecutedUpto())));
                startTime = intervalTimeVO.getStartTime();
                cal.setTime(startTime);
                // Start date of the interval
                startDateOfInterval = cal.get(Calendar.DAY_OF_MONTH);
                // Add the time interval in to hour
                cal.add(Calendar.HOUR_OF_DAY, +p_intervalHour);
                endDate = cal.getTime();
                // End date of the interval
                endDateOfInterval = cal.get(Calendar.DAY_OF_MONTH);
                // get the time from the date
                endTime = BTSLUtil.getTimestampFromUtilDate(endDate);
                intervalTimeVO.setEndTime(endTime);
                p_processStatusVO.setExecutedUpto(endDate);

                if (endDate.after(currDate)) {
                    p_processStatusVO.setExecutedUpto(currDate);
                    intervalTimeVO.setEndTime(BTSLUtil.getTimestampFromUtilDate(currDate));
                    i = parts;
                }
                if (_logger.isDebugEnabled()) {
                    _logger
                        .debug(
                            "generateInterval",
                            "Start date of interval=" + startDateOfInterval + " End date of interval=" + endDateOfInterval + " Start Time=" + intervalTimeVO.getStartTime() + " End Time=" + intervalTimeVO
                                .getEndTime());
                }
                intervalTimeVOList.add(intervalTimeVO);
            }
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("generateInterval", "Exception : " + e.getMessage());
            throw new BTSLBaseException("ExternalFile", "generateInterval", "Exception :" + e.getMessage());
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug("generateInterval", "Exiting intervalTimeVOList=" + intervalTimeVOList.size());
        }
        return intervalTimeVOList;
    }

    /**
     * This method will load all the parameters required for the process from
     * the Constants.props
     * 
     * @return void
     * @throws BTSLBaseException
     * @author Vinay Singh
     */
    private void loadConstantParameters() throws BTSLBaseException {
        final String METHOD_NAME = "loadConstantParameters";
        if (_logger.isDebugEnabled()) {
            _logger.debug("loadConstantParameters", " Entered: ");
        }
        try {
            _filePath = Constants.getProperty("EXTERNAL_USRDATA_FILE_PATH");
            _fileName = Constants.getProperty("EXTERNAL_USRDATA_FILE_NAME");
            if (BTSLUtil.isNullString(_filePath) || BTSLUtil.isNullString(_fileName)) {
                _logger
                    .debug("loadConstantParameters: ",
                        "Error  Directory[EXTERNAL_NEW_USRDATA_FILE_PATH] not defined or file name[EXTERNAL_NEW_USRDATA_FILE_NAME]  not found in the Constants.props, please check.");
            }
            // Get the labels from Constant.props
            _extUsrDataFileLabel = Constants.getProperty("EXTERNAL_USRDATA_FILE_LABEL");
            if (BTSLUtil.isNullString(_extUsrDataFileLabel)) {
                _logger.debug("loadConstantParameters: ", "Error  Missing entry from Constants.props [EXTERNAL_USRDATA_FILE_LABEL]");
            }
            // Get the SQL date time format from Constant.props
            _sqlDateTimeFormat = Constants.getProperty("EXTERNAL_USRDATA_FILE_SQLDATETIME_FORMAT");
            if (BTSLUtil.isNullString(_sqlDateTimeFormat)) {
                _logger.debug("loadConstantParameters: ", "Error  Missing entry from Constants.props [EXTERNAL_USRDATA_FILE_SQLDATETIME_FORMAT]");
                _sqlDateTimeFormat = "dd/MM/yy HH24:MI:SS";
            }
            // Get the date-time format from Constant.props
            _dateTimeFormat = Constants.getProperty("EXTERNAL_USRDATA_FILE_DATETIME_FORMAT");
            if (BTSLUtil.isNullString(_dateTimeFormat)) {
                _logger.debug("loadConstantParameters: ", "Error  Missing entry from Constants.props [EXTERNAL_USRDATA_FILE_DATETIME_FORMAT]");
                _dateTimeFormat = PretupsI.TIMESTAMP_DATESPACEHHMMSS;
            }
            _simpleDateFormat = new SimpleDateFormat(_dateTimeFormat);
            _simpleDateFormat.setLenient(false);
        } catch (Exception e) {
            _logger.error("loadConstantParameters", "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExternalUserDataFile[loadConstantParameters]", "", "",
                "", "Message:" + e.getMessage());
            throw new BTSLBaseException("ExternalUserDataFile", " loadConstantParameters", e.getMessage());
        }
    }

    /**
     * Get Date String From Date
     * 
     * @param date
     * @return
     * @throws ParseException
     */
    private String getFileNameStringFromDate(Date date) throws ParseException {
        final String format = "ddMMyy";
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false); // this is required else it will convert
        return sdf.format(date);
    }
}

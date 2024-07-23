package com.btsl.pretups.processes;

/**
 * @(#)ExternalFile.java
 *                       Copyright(c) 2007, Bharti Telesoft Ltd.
 *                       All Rights Reserved
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Author Date History
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 *                       Vinay Kumar Singh Jul 24, 2007 Initial Creation
 *                       ------------------------------------------------------
 *                       -------------------------------------------
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.processes.businesslogic.IntervalTimeVO;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

public class ExternalFile {

    private static Log _logger = LogFactory.getLog(ExternalFile.class.getName());
    private static String _fileLabelExtFile = null;
    private static Locale _locale = null;
    private static String _queryExternalFile = null;
    private static boolean _isHeaderShow = false;
    private static boolean _isFooterShow = false;
    private static String _sqlDateFormat = null;
    private static String _sqlDateTimeFormat = null;
    private static String _dateTimeFormat = null;
    private static SimpleDateFormat _simpleDateFormat = null;

    /**
     * Method main
     * 
     * @param args
     *            String[] args[0] = Constants.props (With path) args[1] =
     *            ProcessLogConfig.props (With path) args[2] = QueryFile.props
     *            (With path) args[3] = Process interval nemuric value (24 hours
     *            for a day) args[4] = Locale(0 or 1)
     * @author Vinay Kumar Singh
     */

    public static void main(String[] args) {
        try {
            if (args.length != 5)// check the argument length
            {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("ExternalFile ::",
                        " Not sufficient arguments, please pass Conatnsts.props ProcessLogconfig.props QueryFile.props moduleCode ProcessInterval Locale");
                }
                return;
            }
            final File constantsFile = new File(args[0]);
            if (!constantsFile.exists())// check file (Constants.props) exist or
            // not
            {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("ExternalFile", " Constants File Not Found at the path : " + args[0]);
                }
                return;
            }
            final File logconfigFile = new File(args[1]);
            if (!logconfigFile.exists())// check file (ProcessLogConfig.props)
            // exist or not
            {
                _logger.debug("ExternalFile", " ProcessLogConfig File Not Found at the path : " + args[1]);
                return;
            }
            final File queryFile = new File(args[2]);
            if (!queryFile.exists())// check file (ProcessLogConfig.props) exist
            // or not
            {
                _logger.debug("ExternalFile", " QueryFile File Not Found at the path : " + args[2]);
                return;
            }
            if (!BTSLUtil.isNullString(args[3]))// Process Interval
            {
                if (!BTSLUtil.isNumeric(args[3]))// check the Process
                // Interval is numeric
                {
                    _logger.debug("ExternalFile ::", " Invalid Process Interval " + args[3] + " It should be between 1 - 24");
                    return;
                } else if (Integer.parseInt(args[3]) < 1) {
                    _logger.debug("ExternalFile ::", " Invalid Process Interval " + args[3] + " It should be between 1 - 24");
                    return;
                }
            } else {
                _logger.debug("ExternalFile", " Process Interval missing ");
                return;
            }
            if (BTSLUtil.isNullString(args[4]))// Locale check
            {
                _logger.debug("ExternalFile ::", " Locale is missing ");
                return;
            }
            if (!BTSLUtil.isNumeric(args[4]))// check the Process Interval is
            // numeric
            {
                _logger.debug("ExternalFile ::", " Invalid Locale " + args[4] + " It should be 0 or 1");
                return;
            }
            if (Integer.parseInt(args[4]) > 1) {
                _logger.debug("ExternalFile ::", " Invalid Locale " + args[4] + " It should be 0 or 1");
                return;
            }
            // use to load the Constants.props and ProcessLogConfig.props files
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            // load query file
            Constants.load(args[2]);
        }// end of try
        catch (Exception e) {
            _logger.error("main", "Main: Error in loading the Cache information.." + e.getMessage());
            _logger.errorTrace("main", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ExternalFile[main]", "", "", "",
                "  Error in loading the Cache information");
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {
            final String imaQryKey = "EXTERNAL_FILE_QUERY";
            _queryExternalFile = Constants.getProperty(imaQryKey);
            if (BTSLUtil.isNullString(_queryExternalFile)) {
                _logger.error("main", "Error in loading the Query information..key :" + imaQryKey);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ExternalFile[main]", "", "", "",
                    "  Error in loading the QueryFile key " + imaQryKey);
                ConfigServlet.destroyProcessCache();
                return;
            }

        } catch (Exception e) {
            _logger.error("main", "Main: Error in loading the Query information.." + e.getMessage());
            _logger.errorTrace("main", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ExternalFile[main]", "", "", "",
                "  Error in loading the QueryFile");
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {
            _locale = LocaleMasterCache.getLocaleFromCodeDetails(args[4]);
            if (_locale == null) {
                _locale = LocaleMasterCache.getLocaleFromCodeDetails("0");
                if (_logger.isDebugEnabled()) {
                    _logger.debug("main", "Error : Invalid Locale " + args[4] + " It should be 0 or 1, thus using default locale code 0");
                }
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "ExternalFile[main]", "", "", "",
                    "  Message:  Invalid Locale " + args[4] + " It should be 0 (EN) or 1 (OTH) ");
            }
        } catch (Exception e) {
            _logger.error("main", " Invalid locale : " + args[4] + " Exception:" + e.getMessage());
            _locale = new Locale("en", "US");
            _logger.errorTrace("main", e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "ExternalFile[main]", "", "", "",
                "  Message:  Not able to get the locale");
        }
        try {
            _fileLabelExtFile = BTSLUtil.getMessage(_locale, "EXTERNAL_FILE_HEADER", null);
            if (PretupsI.YES.equalsIgnoreCase(BTSLUtil.NullToString(Constants.getProperty("SHOW_HEADER_EXTERNAL_FILE")))) {
                _isHeaderShow = true;
            }
            if (PretupsI.YES.equalsIgnoreCase(BTSLUtil.NullToString(Constants.getProperty("SHOW_FOOTER_EXTERNAL_FILE")))) {
                _isFooterShow = true;
            }

        } catch (Exception ee) {
            _logger
                .error(
                    "main",
                    "Missing/Wrong entry from Constants.props [externalfile.onlydateformat] or [externalfile.datetimeformat] or [externalfile.systemdatetime.format] or [RECON_RPT_HEADER_SHOW] or [RECON_RPT_FOOTER_SHOW] " + ee
                        .getMessage());
            _logger.errorTrace("main", ee);
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            final ExternalFile extfl = new ExternalFile();
            extfl.process(Integer.parseInt(args[3]));
        } catch (BTSLBaseException be) {
            _logger.error("main", "BTSLBaseException :" + be.getMessage());
            _logger.errorTrace("main", be);
            // event handler
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExternalFile[main]", "", "", "",
                "BTSLBaseException:" + be.getMessage());
        } catch (Exception e) {
            _logger.error("main", "Exception :" + e.getMessage());
            _logger.errorTrace("main", e);
            // event handler
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExternalFile[main]", "", "", "", "Exception:" + e
                .getMessage());
        } finally {
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * Method process This methos is for creating database connection and
     * processing the process interval.
     * 
     * @param p_processInterval
     *            int
     * @throws BTSLBaseException
     * @author Vinay
     */
    private void process(int p_processInterval) throws BTSLBaseException {
        final String methodName = "process";
        if (_logger.isDebugEnabled()) {
            _logger.debug(methodName, " Entered:  p_processInterval : " + p_processInterval);
        }
        Connection con = null;
        try {
            con = OracleUtil.getSingleConnection();
            ProcessStatusVO processVO = null;
            ProcessStatusDAO processDAO = null;
            final ProcessBL processBL = new ProcessBL();

            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(methodName, " DATABASE Connection is NULL ");
                }
                throw new BTSLBaseException(this, methodName, "Not able to get the connection");
            }
            try {
                processVO = null;
                processVO = processBL.checkProcessUnderProcess(con, PretupsI.EXTERAL_FILE_PROCESS_ID);
                if (processVO.isStatusOkBool()) {
                    con.commit();
                    final ArrayList intervalList = this.generateInterval(processVO, p_processInterval);
                    this.fetchExtFile(con, intervalList, processVO);
                } else {
                    throw new BTSLBaseException("ExternalFile ", methodName, "Process is already running..");
                }
            } catch (BTSLBaseException e1) {
                _logger.error(methodName, "BTSLBaseException : ExternalFile (IMA File) : " + e1.getMessage());
                _logger.errorTrace(methodName, e1);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExternalFile[process] ExternalFile (IMA File)",
                    "", "", "", "BTSLBaseException:" + e1.getMessage());
            } catch (Exception e2) {
                _logger.error(methodName, "Exception : ExternalFile (IMA File) : " + e2.getMessage());
                _logger.errorTrace(methodName, e2);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExternalFile[process] ExternalFile (IMA File)",
                    "", "", "", "Exception:" + e2.getMessage());
            } finally {
                processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                try {
                    processDAO = new ProcessStatusDAO();
                    if (processDAO.updateProcessDetail(con, processVO) > 0) {
                        con.commit();
                    } else {
                        con.rollback();
                    }
                } catch (Exception e) {
                    _logger.error(methodName, " Exception in update process detail" + e.getMessage());
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "ExternalFile[process] ExternalFile (IMA File)", "", "", "",
                        "Exception in update process detail for Process ID=" + PretupsI.EXTERAL_FILE_PROCESS_ID + " :" + e.getMessage());
                    _logger.errorTrace(methodName, e);
                }
            }
        } catch (BTSLBaseException be) {
            _logger.error(methodName, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(methodName, be);
            throw be;
        } catch (Exception be) {
            _logger.error(methodName, "Exception : " + be.getMessage());
            _logger.errorTrace(methodName, be);
            throw new BTSLBaseException(this, methodName, "Exception=" + be.getMessage());
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, "Exiting..... ");
            }
        }
    }

    /**
     * method fetchExtFile This method is for fetching the data from the
     * database.
     * 
     * @param p_con
     *            Connection
     * @param p_intervalList
     *            ArrayList
     * @param p_processVO
     *            ProcessStatusVO
     * @throws BTSLBaseException
     * @author Vinay
     */
    private void fetchExtFile(Connection p_con, ArrayList p_intervalList, ProcessStatusVO p_processVO) throws BTSLBaseException {

        final String methodName = "fetchExtFile";
        if (_logger.isDebugEnabled()) {
            _logger.debug(methodName, " Entered:p_intervalList.size()=" + p_intervalList.size() + "p_processVO =" + p_processVO.toString());
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        PreparedStatement pstmtSelectAmbig = null;
        ResultSet rsAmbig = null;
        Timestamp startTimestamp = null;
        try {
            String filePath = null;
            String fileName = null;
            try {
                filePath = Constants.getProperty("EXTERNAL_FILE_PATH");
                fileName = Constants.getProperty("EXTERNAL_FILE_PREFIX");
                final File fileDir = new File(filePath);
                if (!fileDir.isDirectory()) {
                    fileDir.mkdirs();
                }
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
                _logger.error(methodName, "Exception" + e.getMessage());
                throw new BTSLBaseException("ExternalFile", methodName, "Directory not created or file name  not found please check Constants.props");
            }

            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, "ExternalFile select query:" + _queryExternalFile);
            }
            pstmtSelect = p_con.prepareStatement(_queryExternalFile, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            _sqlDateFormat = Constants.getProperty("EXTERNAL_FILE_DATE_FORMAT");
            if (BTSLUtil.isNullString(_sqlDateFormat)) {
                _logger.debug("ExternalFile", "fetchExtFile: Error  Missing entry from Constants.props [EXTERNAL_FILE_DATE_FORMAT]");
                _sqlDateFormat = PretupsI.DATE_FORMAT;
            }
            _sqlDateTimeFormat = Constants.getProperty("EXTERNAL_FILE_SQLDATETIME_FORMAT");
            if (BTSLUtil.isNullString(_sqlDateTimeFormat)) {
                _logger.debug("ExternalFile", "fetchExtFile: Error  Missing entry from Constants.props [EXTERNAL_FILE_SQLDATETIME_FORMAT]");
                _sqlDateTimeFormat = "dd/MM/yy HH24:MI:SS";
            }
            _dateTimeFormat = Constants.getProperty("EXTERNAL_FILE_DATETIME_FORMAT");
            if (BTSLUtil.isNullString(_dateTimeFormat)) {
                _logger.debug("ExternalFile", "fetchExtFile: Error  Missing entry from Constants.props [EXTERNAL_FILE_DATETIME_FORMAT]");
                _dateTimeFormat = PretupsI.TIMESTAMP_DATESPACEHHMMSS;
            }
            _simpleDateFormat = new SimpleDateFormat(_dateTimeFormat);
            _simpleDateFormat.setLenient(false);
            // For finding Ambigous or Underprocess status
            //local_index_missing
            final String ambigousCase = "SELECT 1 FROM c2s_transfers WHERE transfer_status in ('205','250') AND transfer_date_time>? AND transfer_date_time<=?";
            pstmtSelectAmbig = p_con.prepareStatement(ambigousCase);
            int i = 0;
            String fromDate = null;
            String toDate = null;
            IntervalTimeVO intervalTimeVO = null;
            final ProcessStatusDAO processDAO = new ProcessStatusDAO();
            for (int j = 0, k = p_intervalList.size(); j < k; j++) {
                startTimestamp = null;
                intervalTimeVO = (IntervalTimeVO) p_intervalList.get(j);
                startTimestamp = intervalTimeVO.getStartTime();
                i = 1;
                pstmtSelectAmbig.clearParameters();
                pstmtSelectAmbig.setTimestamp(1, intervalTimeVO.getStartTime());
                pstmtSelectAmbig.setTimestamp(2, intervalTimeVO.getEndTime());
                rsAmbig = pstmtSelectAmbig.executeQuery();
                // Checking for the Ambigous(250) and Underprocess(205) status.
                if (rsAmbig.next()) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "ExternalFile[fetchExtFile] ExternalFile(IMA File)", "", "", "", "Ambigous date between" + intervalTimeVO.getStartTime() + " and " + intervalTimeVO
                            .getEndTime());
                    throw new BTSLBaseException("ExternalFile", methodName, "Ambigous date between" + intervalTimeVO.getStartTime() + " and " + intervalTimeVO.getEndTime());
                } else {
                    pstmtSelect.clearParameters();
                    pstmtSelect.setString(i++, _sqlDateFormat);
                    pstmtSelect.setString(i++, _sqlDateTimeFormat);
                    pstmtSelect.setTimestamp(i++, intervalTimeVO.getStartTime());
                    pstmtSelect.setTimestamp(i++, intervalTimeVO.getEndTime());
                    rs = pstmtSelect.executeQuery();
                    fromDate = _simpleDateFormat.format(BTSLUtil.getUtilDateFromTimestamp(intervalTimeVO.getStartTime()));
                    toDate = _simpleDateFormat.format(BTSLUtil.getUtilDateFromTimestamp(intervalTimeVO.getEndTime()));
                    // method call to write data in the files
                    if (rs != null) {
                        writeExtFileDataInFile(rs, filePath, fileName, fromDate, toDate);
                    }
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    } catch (Exception ex) {
                        _logger.errorTrace(methodName, ex);
                    }
                    rs = null;
                    p_processVO.setProcessStatus(ProcessI.STATUS_UNDERPROCESS);
                    p_processVO.setExecutedUpto(intervalTimeVO.getEndTime());
                    p_processVO.setExecutedOn(new Date());
                    if (processDAO.updateProcessDetail(p_con, p_processVO) > 0) {
                        p_con.commit(); // Commiting the changes
                    } else {
                        p_processVO.setExecutedUpto(startTimestamp);
                        p_con.rollback();
                        throw new BTSLBaseException("ExternalFile", methodName, "Process Status is not commited");
                    }
                }
            }
        } catch (BTSLBaseException be) {
            if (startTimestamp != null) {
                p_processVO.setExecutedUpto(startTimestamp);
            }
            _logger.error(methodName, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(methodName, be);
            throw be;
        } catch (SQLException sqe) {
            if (startTimestamp != null) {
                p_processVO.setExecutedUpto(startTimestamp);
            }
            _logger.error(methodName, "SQLException " + sqe.getMessage());
            _logger.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExternalFile[fetchExtFile]", "", "", "",
                "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("ExternalFile", methodName, "SQLException :" + sqe.getMessage());
        } catch (Exception ex) {
            if (startTimestamp != null) {
                p_processVO.setExecutedUpto(startTimestamp);
            }
            _logger.error(methodName, "Exception : " + ex.getMessage());
            _logger.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExternalFile[fetchExtFile]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException("ExternalFile", methodName, "Exception : " + ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception ex) {
                    _logger.errorTrace(methodName, ex);
                } // Closing the result set
            }
            if (pstmtSelect != null) {
                try {
                    pstmtSelect.close();
                } catch (Exception ex) {
                    _logger.errorTrace(methodName, ex);
                } // Closing the prepared statement
            }
            if (rsAmbig != null) {
                try {
                    rsAmbig.close();
                } catch (Exception ex) {
                    _logger.errorTrace(methodName, ex);
                } // Closing the ambigous result set
            }
            if (pstmtSelectAmbig != null) {
                try {
                    pstmtSelectAmbig.close();
                } catch (Exception ex) {
                    _logger.errorTrace(methodName, ex);
                }
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, "Exiting ");
            }
        }
    }

    /**
     * Method writeExtFileDataInFile This method is for writing data in CSV
     * file.
     * 
     * @param p_rs
     *            ResultSet
     * @param p_filePath
     *            String
     * @param p_fileName
     *            String
     * @param p_fromDate
     *            String
     * @param p_toDate
     *            String
     * @throws BTSLBaseException
     * @author Vinay
     */
    private void writeExtFileDataInFile(ResultSet p_rs, String p_filePath, String p_fileName, String p_fromDate, String p_toDate) throws BTSLBaseException {
        final String methodName = "writeExtFileDataInFile";
        if (_logger.isDebugEnabled()) {
            _logger.debug(methodName,
                " Entered:  p_rs=" + p_rs + " p_filePath : " + p_filePath + " p_fileName : " + p_fileName + " p_fromDate : " + p_fromDate + " p_toDate : " + p_toDate);
        }
        PrintWriter out = null;
        File newFile = null;
        String fileData = null;
        String fileHeader = null;
        String fileFooter = null;
        int recordsWrittenInFile = 0;
        String finalFileName = null;
        try {
            finalFileName = p_filePath + p_fileName + BTSLUtil.getFileNameStringFromDate(BTSLUtil.getDateFromDateString(p_fromDate, _dateTimeFormat)) + ".csv";
            newFile = new File(finalFileName);
            out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
            if (_isHeaderShow) // For showing file header
            {
                fileHeader = constructFileHeader(p_fromDate, p_toDate, _fileLabelExtFile);
                out.write(fileHeader + "\n");
            }
            while (p_rs.next()) { // Iterating the result set for writing
                // data.
                fileData = p_rs.getString(1);
                out.write(fileData + "\n");
                recordsWrittenInFile++;
            } // end of while(p_rs.next())
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
                    	_logger.debug(methodName, "File deleted successfully");
                    }
                }
            }
        } catch (Exception e) {
            _logger.debug(methodName, "Exception: " + e.getMessage());
            _logger.errorTrace(methodName, e);
            if (newFile != null) {
            	boolean isDeleted = newFile.delete();
                if(isDeleted){
                	_logger.debug(methodName, "File deleted successfully");
                }
                newFile = null;
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ExternalFile[writeExtFileDataInFile]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("ExternalFile", methodName, "Exception: " + e.getMessage());
        } finally {
            if (out != null) {
                out.close(); // Closing the PrintWriter
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, "Exiting ");
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
     * @author Vinay
     */
    private String constructFileHeader(String p_fromDate, String p_toDate, String p_fileLabel) throws BTSLBaseException {
        final String methodName = "constructFileHeader";
        if (_logger.isDebugEnabled()) {
            _logger.debug(methodName, " Entered: p_fromDate :" + p_fromDate + " p_toDate :" + p_toDate + " p_fileLabel :" + p_fileLabel);
        }
        StringBuffer fileHeaderBuf = null;
        try {
            fileHeaderBuf = new StringBuffer("From Date=" + p_fromDate);
            fileHeaderBuf.append("\n" + "To Date=" + p_toDate);
            fileHeaderBuf.append("\n" + p_fileLabel);
            fileHeaderBuf.append("\n" + "[STARTDATA]");
        } catch (Exception e) {
            _logger.error(methodName, "Exception: " + e.getMessage());
            _logger.errorTrace(methodName, e);
            throw new BTSLBaseException("ExternalFile", methodName, "Exception: " + e.getMessage());
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug(methodName, "Exiting: fileHeaderBuf.toString()=" + fileHeaderBuf.toString());
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
     * @author Vinay
     */
    private String constructFileFooter(long p_noOfRecords) throws BTSLBaseException {
        final String methodName = "constructFileFooter";
        if (_logger.isDebugEnabled()) {
            _logger.debug(methodName, " Entered: ");
        }
        StringBuffer fileFooterBuf = null;
        try {
            fileFooterBuf = new StringBuffer("[ENDDATA]" + "\n");
            fileFooterBuf.append("Number of records=" + p_noOfRecords + "\n");
        } catch (Exception e) {
            _logger.error("constructFileHeader", "Exception: " + e.getMessage());
            _logger.errorTrace(methodName, e);
            throw new BTSLBaseException("ExternalFile", methodName, " Exception: " + e.getMessage());
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug(methodName, "Exiting: fileHeaderBuf.toString()=" + fileFooterBuf.toString());
        }
        return fileFooterBuf.toString();
    }

    /**
     * Method generateInterval Used to generate the interval according to the
     * Executed upto and current date The formula used is -> process
     * duration=(currentTime-ExecutedUpto-beforeInterval)
     * 
     * @param p_processStatusVO
     *            ProcessStatusVO
     * @param p_intervalHour
     *            int
     * @return intervalTimeVOList ArrayList
     * @throws Exception
     * @author Vinay
     */
    public ArrayList generateInterval(ProcessStatusVO p_processStatusVO, int p_intervalHour) throws Exception {
        final String methodName = "generateInterval";
        if (_logger.isDebugEnabled()) {
            _logger.debug(methodName, "Entered p_processStatusVO=" + p_processStatusVO.toString() + " p_intervalHour=" + p_intervalHour);
        }
        final ArrayList intervalTimeVOList = new ArrayList();
        IntervalTimeVO intervalTimeVO = null;
        try {
            Date currDate = new Date();
            final int beforeInterval = BTSLUtil.parseLongToInt( p_processStatusVO.getBeforeInterval() / 60);
            final long diffInMillis = currDate.getTime() - p_processStatusVO.getExecutedUpto().getTime() - beforeInterval;
            final long processDuration = diffInMillis / (1000 * 60 * 60);
            long parts = processDuration / p_intervalHour;
            if (processDuration % p_intervalHour > 0) {
                parts = parts + 1;
            }
            Date endDate = null;
            Timestamp startTime = null;
            Timestamp endTime = null;
            currDate = new Date(currDate.getYear(), currDate.getMonth(), currDate.getDate(), (currDate.getHours() - beforeInterval), currDate.getMinutes(), currDate
                .getSeconds());
            for (long i = 0; i <= parts; i++) {
                intervalTimeVO = new IntervalTimeVO();
                intervalTimeVO.setStartTime(BTSLUtil.getTimestampFromUtilDate(BTSLUtil.getSQLDateFromUtilDate(p_processStatusVO.getExecutedUpto())));
                startTime = intervalTimeVO.getStartTime();
                endDate = new Date(startTime.getYear(), startTime.getMonth(), startTime.getDate(), startTime.getHours() + p_intervalHour, startTime.getMinutes(), startTime
                    .getSeconds());
                endTime = BTSLUtil.getTimestampFromUtilDate(endDate);
                intervalTimeVO.setEndTime(endTime);
                p_processStatusVO.setExecutedUpto(endDate);
                if (endDate.after(currDate)) {
                    p_processStatusVO.setExecutedUpto(currDate);
                    intervalTimeVO.setEndTime(BTSLUtil.getTimestampFromUtilDate(currDate));
                    i = parts;
                }
                if (_logger.isDebugEnabled()) {
                    _logger.debug(methodName, "starttime.getDate()=" + startTime.getDate() + " endDate.getDate()=" + endDate.getDate() + " Start Time=" + intervalTimeVO
                        .getStartTime() + " End Time=" + intervalTimeVO.getEndTime());
                }
                intervalTimeVOList.add(intervalTimeVO);
            }
        } catch (Exception e) {
            _logger.errorTrace(methodName, e);
            _logger.error(methodName, "Exception : " + e.getMessage());
            throw new BTSLBaseException("ExternalFile", methodName, "Exception :" + e.getMessage());
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug(methodName, "Exiting intervalTimeVOList=" + intervalTimeVOList.size());
        }
        return intervalTimeVOList;
    }
}

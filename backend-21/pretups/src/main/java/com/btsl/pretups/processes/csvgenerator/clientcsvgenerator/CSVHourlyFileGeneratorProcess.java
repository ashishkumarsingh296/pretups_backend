/**
 * @(#)CSVHourlyFileGeneratorProcess
 *                                   Copyright(c) 2014, Bharti Telesoft Ltd.
 *                                   All Rights Reserved
 * 
 *                                   ------------------------------------------
 *                                   ---------------
 *                                   Author Date History
 *                                   ------------------------------------------
 *                                   ---------------
 *                                   Yogesh Kumar Pandey 29/12/2014 Initial
 *                                   Creation
 *                                   ------------------------------------------
 *                                   ---------------
 */

package com.btsl.pretups.processes.csvgenerator.clientcsvgenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
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
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.processes.businesslogic.CsvHourlyProcessDAO;
import com.btsl.pretups.processes.businesslogic.DateSorting;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.processes.csvgenerator.CSVFileVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.ibm.icu.util.Calendar;

public class CSVHourlyFileGeneratorProcess {

    private CSVHourlyFileGeneratorProcess() {

    }

    private static List<String> _fileNameLst = new ArrayList<String>();
    private static ProcessStatusVO _processStatusVO;
    private static ProcessBL _processBL = null;
    private static Map<String, CSVFileVO> _csvMap = new HashMap<String, CSVFileVO>();
    private static Properties _csvproperties = new Properties();
    private static final Log LOG = LogFactory.getLog(CSVHourlyFileGeneratorProcess.class.getName());
    private static String _finalMasterDirectoryPath = null;
    private static String _fileEXT = ".csv";
    private static Map<String, Long> _fileNameMap = null;
    private static SortedMap<String, Object> _fileRecordMap = null;
    private static String processId = null;

    /**
     * main method()
     * 
     * @param arg
     */
    public static void main(String arg[]) {
        final String methodName = "main";
        FileInputStream fileInputStream = null;
        try {
            if (arg.length < 3) {
                LOG.info(methodName, "Usage : CSVHourlyFileGeneratorProcess[Constants file] [LogConfig file] [csvConfigFileHourly file]");
                return;
            }
            File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
                LOG.info(methodName, " Constants.props File Not Found .............");
            }
            File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                LOG.info(methodName, " Logconfig.props File Not Found .............");
            }

            File csvConfigFileHourly = new File(arg[2]);
            if (!csvConfigFileHourly.exists()) {
                LOG.info(methodName, " csvConfigFileHourly.props File Not Found .............");
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            try{
            	fileInputStream = new FileInputStream(csvConfigFileHourly);
	            _csvproperties.load(fileInputStream);
	            hourlyProcess();
            }catch (Exception e) {
            	 LOG.errorTrace(methodName, e);
			}finally {
				if (fileInputStream !=null) {
					fileInputStream.close();
				}				
			}
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            ConfigServlet.destroyProcessCache();
            return;
        } finally {
            ConfigServlet.destroyProcessCache();
        }
        if (LOG.isDebugEnabled())
            LOG.debug(methodName, "Exiting..... ");
    }

    private static void hourlyProcess() throws BTSLBaseException {
        Date processedUpto = null;
        Date currentDateTime = null;
        Connection con = null;
        boolean statusOk = false;
        final String methodName = "hourlyProcess";
        try {
            _fileNameMap = new Hashtable<String, Long>();
            _fileRecordMap = new TreeMap<String, Object>(new DateSorting());
            LOG.debug(methodName, "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            Calendar cal = BTSLDateUtil.getInstance();
            currentDateTime = cal.getTime();
            loadHourlyConstantParameters();
            con = OracleUtil.getSingleConnection();
            processId = "CSVGENERATOR";
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(con, processId);
            statusOk = hourlyProcessCheckStatus(currentDateTime, con, methodName);
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            if (!_fileNameLst.isEmpty())
                deleteAllCsvFiles();
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "CSVHourlyFileGeneratorProcess[" + methodName + "]", "", "", "", " Exception =" + e.getMessage());
            throw new BTSLBaseException("CSVHourlyFileGeneratorProcess", methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            releaseProcessResources(con, statusOk, methodName);
        }
        if (LOG.isDebugEnabled())
            LOG.debug(methodName, "Exiting..... ");
    }

    /**
     * @param currentDateTime
     * @param con
     * @param methodName
     * @return
     * @throws SQLException
     * @throws BTSLBaseException
     * @throws InterruptedException
     */
    private static boolean hourlyProcessCheckStatus(Date currentDateTime, Connection con, final String methodName) throws SQLException, BTSLBaseException, InterruptedException {
        Date processedUpto;
        boolean statusOk;
        statusOk = _processStatusVO.isStatusOkBool();
        if (statusOk) {
            con.commit();
            processedUpto = _processStatusVO.getExecutedUpto();
            if (processedUpto != null) {
                CSVFileVO csvfilevo = null;
                Iterator<String> itr = _csvMap.keySet().iterator();

                while (itr.hasNext()) {
                    csvfilevo = _csvMap.get(itr.next());

                    fetchHourlyProcessQuery(con, processedUpto, csvfilevo.getDirName(), csvfilevo.getPrefixName(), csvfilevo.getHeaderName(), csvfilevo.getExtName(), csvfilevo.getQueryName(), csvfilevo.getProcessId(), Long.parseLong(_csvproperties.getProperty("MAX_ROWS")));

                    Thread.sleep(500);
                }

                _processStatusVO.setExecutedUpto(currentDateTime);
                _processStatusVO.setExecutedOn(currentDateTime);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "CSVHourlyFileGeneratorProcess[" + methodName + "]", "", "", "", " CSVHourlyFileGeneratorProcess process has been executed successfully.");
            } else
                throw new BTSLBaseException("CSVHourlyFileGeneratorProcess", methodName, PretupsErrorCodesI.DWH_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
        }
        return statusOk;
    }

    /**
     * This method will close all connection resources
     * 
     * @param statusOk
     *            boolean
     * @param con
     *            Connection
     * @param methodName
     *            String
     * @return void
     * @throws Exception
     */
    private static void releaseProcessResources(Connection con, boolean statusOk, final String methodName) {
        Iterator<String> itr = _csvMap.keySet().iterator();
        while (itr.hasNext()) {
            CSVFileVO csvfilevo = _csvMap.get(itr.next());
            _finalMasterDirectoryPath = csvfilevo.getDirName();
            if (!BTSLUtil.isNullString(csvfilevo.getExtName()))
                _fileEXT = csvfilevo.getExtName();
            if (!BTSLUtil.isNullString(csvfilevo.getProcessId()))
                processId = csvfilevo.getProcessId();
            String isSummaryFileReq = "N";
            isSummaryFileReq = _csvproperties.getProperty("SUMMARY_FILE_REQUIRED");
            if (BTSLUtil.isNullString(isSummaryFileReq)) {
                isSummaryFileReq = "N";
            }
            if ("Y".equalsIgnoreCase(isSummaryFileReq))
                try {
                    writeProcessFileSummary(_finalMasterDirectoryPath, _fileEXT, processId);
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                }
        }
        processId = "CSVGENERATOR";
        checkProcessReleaseStatus(con, statusOk, methodName);
        LOG.debug(methodName, "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
    }

    /**
     * @param con
     * @param statusOk
     * @param methodName
     */
    private static void checkProcessReleaseStatus(Connection cp_con, boolean statusOk, final String methodName) {
        if (statusOk) {
            try {
                checkStatus(cp_con, methodName);
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            try {
                if (cp_con != null)
                    cp_con.close();
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
        }
    }

    /**
     * This method will check process status data based on processId from
     * database
     * 
     * @param con
     *            Connection
     * @param methodName
     *            String
     * @return void
     * @throws Exception
     */
    private static void checkStatus(Connection con, final String methodName) throws BTSLBaseException {
        if (markHourlyProcessStatusAsComplete(con, processId) == 1) {
            try {
                con.commit();
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
        } else {
            try {
                con.rollback();
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
        }
    }

    /**
     * This method will load constant parameters from property file
     * 
     * @return void
     * @throws Exception
     */
    private static void loadHourlyConstantParameters() throws BTSLBaseException {
        final String methodName = "loadHourlyConstantParameters";
        if (LOG.isDebugEnabled())
            LOG.debug(methodName, " Entered: ");
        try {
            initialization(_csvproperties.getProperty("PROCESS_ID"));
            LOG.debug(methodName, " Required information successfuly loaded from csvConfigFileHourly.properties...............: ");
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CSVHourlyFileGeneratorProcess[" + methodName + "]", "", "", "", "Message:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CSVHourlyFileGeneratorProcess[" + methodName + "]", "", "", "", "Message:" + btslMessage);
            throw new BTSLBaseException("CSVHourlyFileGeneratorProcess", methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }
    }

    private static void fetchHourlyProcessQuery(Connection p_con, Date p_beingProcessedDate, String p_dirPath, String p_fileName, String p_fileLabel, String p_fileEXT, String p_sqlQuery, String processId, long p_maxFileLength) throws BTSLBaseException {
        final String methodName = "fetchHourlyProcessQuery";
        CsvHourlyProcessDAO csvHourlyProcessDao = new CsvHourlyProcessDAO();
        if (LOG.isDebugEnabled())
            LOG.debug(methodName, " Entered: p_beingProcessedDate=" + p_beingProcessedDate + " p_dirPath=" + p_dirPath + " p_fileName=" + p_fileName + " p_fileLabel=" + p_fileLabel + " p_fileEXT=" + p_fileEXT + " p_maxFileLength=" + p_maxFileLength);
        String processStatusVO = "";
        Date fromDate = null;
        Date afterAddingBeforeMins = null;
        long t;
        int beforeintinterval;
        final long ONE_MINUTE_IN_MILLIS = 60000;
        String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
        Formatter fmt = new Formatter();
        Calendar cal = Calendar.getInstance();
        fmt.format("Time using 24-hour clock: %tT%n", cal);
        Date startDate = cal.getTime();
        String executedUpto = "";
        String beforeInterval = "";
        processStatusVO = csvHourlyProcessDao.fetchHourlyProcessStatus(processId);
        String[] temp;
        String delimiter = "#";
        temp = processStatusVO.split(delimiter);
        int tempLength = temp.length;
        for (int i = 0; i < tempLength; i++) {
            executedUpto = temp[0];
            beforeInterval = temp[1];
        }
        beforeintinterval = Integer.parseInt(beforeInterval);
        try {
            fromDate = BTSLUtil.getDateFromDateString(executedUpto, DATE_FORMAT_NOW);
            t = fromDate.getTime();

            afterAddingBeforeMins = new Date(t + (beforeintinterval * ONE_MINUTE_IN_MILLIS));
        } catch (ParseException e) {
            LOG.errorTrace(methodName, e);
        }

        String c2sFetchQuery = p_sqlQuery;
        if (LOG.isDebugEnabled())
            LOG.debug(methodName, "sql fetch query:" + c2sFetchQuery);
        PreparedStatement c2sInsertPstmt = null;
        ResultSet rst = null;
        Map<String, String> processMap = null;
        while (afterAddingBeforeMins.before(startDate)) {
            try {
                c2sInsertPstmt = p_con.prepareStatement(c2sFetchQuery);
                if (LOG.isDebugEnabled()) {
                    LOG.debug(methodName, "Select qrySelect:" + c2sFetchQuery);
                }
                if (BTSLUtil.countChar(c2sFetchQuery) == 2) {
                    c2sInsertPstmt.clearParameters();
                    c2sInsertPstmt.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(fromDate));
                    c2sInsertPstmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(afterAddingBeforeMins));
                }
                rst = c2sInsertPstmt.executeQuery();
                p_con.commit();
                processMap = new HashMap<String, String>();
                while (rst.next()) {
                    processMap.put(rst.getString(1), rst.getString(2));
                }
                LOG.debug(methodName, "Memory after loading sql query data: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
                if (!processMap.isEmpty()) {
                    writeHourlyDataInFile(p_dirPath, p_fileName, p_fileLabel, p_beingProcessedDate, p_fileEXT, p_maxFileLength, processMap, afterAddingBeforeMins);
                } else {
                    LOG.info(methodName, " no result found in map");
                }
                csvHourlyProcessDao.updateHourlyProcessStatus(fromDate, afterAddingBeforeMins, processId);
                LOG.debug(methodName, "Memory after writing data files: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
            } catch (BTSLBaseException be) {
                LOG.errorTrace(methodName, be);
                throw be;
            } catch (SQLException sqe) {
                LOG.errorTrace(methodName, sqe);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CSVHourlyFileGeneratorProcess[" + methodName + "]", "", "", "", "SQLException:" + sqe.getMessage());
                throw new BTSLBaseException("CSVHourlyFileGeneratorProcess", methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CSVHourlyFileGeneratorProcess[" + methodName + "]", "", "", "", "SQLException:" + ex.getMessage());
                throw new BTSLBaseException("CSVHourlyFileGeneratorProcess", methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
            } finally {
                if (c2sInsertPstmt != null)
                    try {
                        c2sInsertPstmt.close();
                    } catch (Exception ex) {
                        LOG.errorTrace(methodName, ex);
                    }
            }
            if (LOG.isDebugEnabled())
                LOG.debug(methodName, "Exiting ");
            fromDate = afterAddingBeforeMins;
            t = fromDate.getTime();
            afterAddingBeforeMins = new Date(t + (beforeintinterval * ONE_MINUTE_IN_MILLIS));
        }
    }

    /**
     * This method will write on csv file from hashmap fetch from database
     * 
     * @param p_dirPath
     *            String
     * @param p_fileName
     *            String
     * @param p_fileLabel
     *            String
     * @param p_beingProcessedDate
     *            String
     * @param p_fileEXT
     *            String
     * @param p_maxFileLength
     *            Long
     * @param processMap
     *            Map
     * @param executedUpto
     *            Date
     * @return void
     * @throws Exception
     */
    private static void writeHourlyDataInFile(String p_dirPath, String p_fileName, String p_fileLabel, Date p_beingProcessedDate, String p_fileEXT, long p_maxFileLength, Map<String, String> processMap, Date executedUpto) throws BTSLBaseException {
        final String methodName = "writeHourlyDataInFile";
        if (LOG.isDebugEnabled())
            LOG.debug(methodName, " Entered:  p_dirPath=" + p_dirPath + " p_fileName=" + p_fileName + " p_fileLabel=" + p_fileLabel + " p_beingProcessedDate=" + p_beingProcessedDate + " p_fileEXT=" + p_fileEXT + " p_maxFileLength=" + p_maxFileLength);
        long recordsWrittenInFile = 0;
        PrintWriter out = null;
        int fileNumber = 0;
        String fileName = "";
        File newFile = null;
        String fileHeader = null;
        String fileFooter = null;
        Formatter fmt = new Formatter();
        Calendar cal = Calendar.getInstance();
        fmt.format("Time using 24-hour clock: %tT%n", cal);
        try {
            SimpleDateFormat sdf = null;
            sdf = new SimpleDateFormat(_csvproperties.getProperty("FILE_SUBSTR_DATE_FORMAT"));
            fileNumber = 1;
            if (Integer.toString(fileNumber).length() == 1)
                fileName = p_dirPath + File.separator + p_fileName + sdf.format(executedUpto) + "00" + fileNumber + p_fileEXT;
            else if (Integer.toString(fileNumber).length() == 2)
                fileName = p_dirPath + File.separator + p_fileName + sdf.format(executedUpto) + "0" + fileNumber + p_fileEXT;
            else if (Integer.toString(fileNumber).length() == 3)
                fileName = p_dirPath + File.separator + p_fileName + sdf.format(executedUpto) + fileNumber + p_fileEXT;
            else {
                fileName = p_dirPath + File.separator + p_fileName + sdf.format(executedUpto) + "0000" + fileNumber + p_fileEXT;
            }
            LOG.debug(methodName, "  fileName=" + fileName);

            newFile = new File(fileName);
            _fileNameLst.add(fileName);

            out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));

            fileHeader = constructHourlyFileHeader(fileNumber, p_fileLabel);
            out.write(fileHeader);

            int count = 0;

            Set<String> setOfKeys = processMap.keySet();
            Iterator<String> iterator = setOfKeys.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();

                out.write(processMap.get(key) + "\n");

                recordsWrittenInFile++;
                count++;

            }
            generateHourlyDataFileSummary(p_beingProcessedDate, recordsWrittenInFile, fileName);
            if (recordsWrittenInFile >= p_maxFileLength) {
                fileFooter = constructHourlyFileFooter(recordsWrittenInFile);
                out.write(fileFooter);

                recordsWrittenInFile = 0;
                fileNumber = fileNumber + 1;
                out.close();

                if (Integer.toString(fileNumber).length() == 1)
                    fileName = p_dirPath + File.separator + p_fileName + sdf.format(executedUpto) + "00" + fileNumber + p_fileEXT;
                else if (Integer.toString(fileNumber).length() == 2)
                    fileName = p_dirPath + File.separator + p_fileName + sdf.format(executedUpto) + "0" + fileNumber + p_fileEXT;
                else if (Integer.toString(fileNumber).length() == 3)
                    fileName = p_dirPath + File.separator + p_fileName + sdf.format(executedUpto) + fileNumber + p_fileEXT;

                LOG.debug(methodName, "  fileName=" + fileName);
                newFile = new File(fileName);
                _fileNameLst.add(fileName);
                out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));

                fileHeader = constructHourlyFileHeader(fileNumber, p_fileLabel);
                out.write(fileHeader);
            }
            if (recordsWrittenInFile > 0) {
                fileFooter = constructHourlyFileFooter(recordsWrittenInFile);
                out.write(fileFooter);
            } else {
                if (out != null)
                    out.close();
            }
            if (out != null)
                out.close();
        } catch (Exception e) {
            deleteAllCsvFiles();
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CSVHourlyFileGeneratorProcess[" + methodName + "]", "", "", "", "Exception:" + e.getMessage());
            return;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
        }
        if (LOG.isDebugEnabled())
            LOG.debug(methodName, "Exiting ");
    }

    private static int markHourlyProcessStatusAsComplete(Connection p_con, String p_processId) throws BTSLBaseException {
        final String methodName = "markHourlyProcessStatusAsComplete";
        if (LOG.isDebugEnabled())
            LOG.debug(methodName, " Entered:  p_processId:" + p_processId);
        int updateCount = 0;
        Date currentDate = new Date();
        ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        _processStatusVO.setProcessID(p_processId);
        _processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        _processStatusVO.setStartDate(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetail(p_con, _processStatusVO);
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CSVHourlyFileGeneratorProcess[" + methodName + "]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("CSVHourlyFileGeneratorProcess", methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (LOG.isDebugEnabled())
                LOG.debug(methodName, "Exiting: updateCount=" + updateCount);
        }
        return updateCount;

    }

    private static void deleteAllCsvFiles() throws BTSLBaseException {
        final String methodName = "deleteAllCsvFiles";
        if (LOG.isDebugEnabled())
            LOG.debug(methodName, " Entered: ");
        int size = 0;
        if (!_fileNameLst.isEmpty())
            size = _fileNameLst.size();
        else {
            LOG.info(methodName, " fileNameLst is null");
        }
        if (LOG.isDebugEnabled())
            LOG.debug(methodName, " : Number of files to be deleted " + size);
        for (int i = 0; i < size; i++) {
            deleteAllCsv(methodName, i);
        }
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CSVHourlyFileGeneratorProcess[" + methodName + "]", "", "", "", " Message: CSVHourlyFileGeneratorProcess process has found some error, so deleting all the files.");
        try {
            if (!_fileNameLst.isEmpty()) {
                _fileNameLst.clear();
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        if (LOG.isDebugEnabled())
            LOG.debug(methodName, " : Exiting.............................");
    }

    /**
     * @param methodName
     * @param i
     * @throws BTSLBaseException
     */
    private static void deleteAllCsv(final String methodName, int i) throws BTSLBaseException {
        String fileName;
        File newFile;
        try {
            fileName = _fileNameLst.get(i);
            newFile = new File(fileName);
            newFile.delete();
            if (LOG.isDebugEnabled())
                LOG.debug("", fileName + " file deleted");
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CSVHourlyFileGeneratorProcess[" + methodName + "]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("CSVHourlyFileGeneratorProcess", methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }
    }

    private static String constructHourlyFileHeader(long p_fileNumber, String p_fileLabel) {
        final String methodName = "constructHourlyFileHeader";
        SimpleDateFormat sdf = null;
        try {
            sdf = new SimpleDateFormat(_csvproperties.getProperty("DATE_TIME_FORMAT"));
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        StringBuilder fileHeaderBuf = new StringBuilder("");
        fileHeaderBuf.append("\n" + " Present Date and Time=" + BTSLDateUtil.getLocaleTimeStamp(sdf.format(new Date())));
        fileHeaderBuf.append("\n" + " File Number=" + p_fileNumber);
        fileHeaderBuf.append("\n" + p_fileLabel);
        fileHeaderBuf.append("\n" + "[STARTDATA]" + "\n");
        return fileHeaderBuf.toString();
    }

    private static String constructHourlyFileFooter(long p_noOfRecords) {
        StringBuilder fileHeaderBuf = null;
        fileHeaderBuf = new StringBuilder("");
        fileHeaderBuf.append("[ENDDATA]" + "\n");
        fileHeaderBuf.append(" Number of records=" + p_noOfRecords);
        return fileHeaderBuf.toString();
    }

    public static void initialization(String p_processIDs) throws BTSLBaseException {
        final String methodName = "initialization";
        String processId = null;
        String[] inStrArray = null;
        try {
            CSVFileVO csvFileVO = null;
            inStrArray = p_processIDs.split(",");
            if (BTSLUtil.isNullArray(inStrArray))
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS3_NO_INTERFACEIDS);
            for (int i = 0, size = inStrArray.length; i < size; i++) {
                processId = inStrArray[i].trim();
                csvFileVO = new CSVFileVO();
                csvFileVO.setProcessId(processId);
                csvFileVO.setQueryName(_csvproperties.getProperty(processId + "_QRY"));
                csvFileVO.setDirName(_csvproperties.getProperty(processId + "_DIR"));
                csvFileVO.setExtName(_csvproperties.getProperty(processId + "_EXT"));
                csvFileVO.setHeaderName(_csvproperties.getProperty(processId + "_HEADER"));
                csvFileVO.setPrefixName(_csvproperties.getProperty(processId + "_PREFIX_NAME"));
                if (LOG.isDebugEnabled())
                    LOG.debug("CSVHourlyFileGeneratorProcess[" + methodName + "]", "csvFileVO::" + csvFileVO);
                _csvMap.put(processId, csvFileVO);
            }
        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CSVHourlyFileGeneratorProcess[" + methodName + "]", "String of p_processIDs ids=" + p_processIDs, "", "", "While initializing the processIDs for the CSV file generator process =" + processId + " get Exception=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS3_NODE_INITIALIZATION);
        }
        if (LOG.isDebugEnabled())
            LOG.debug(methodName, "Exited _csvMap::" + _csvMap);
    }

    @SuppressWarnings("unchecked")
    private static void generateHourlyDataFileSummary(Date p_beingProcessedDate, long p_recordsWrittenInFile, String p_fileName) throws BTSLBaseException {
        final String methodName = "generateHourlyDataFileSummary";
        if (LOG.isDebugEnabled())
            LOG.debug("CSVHourlyFileGeneratorProcess", " Entered: " + methodName + " p_beingProcessedDate=" + p_beingProcessedDate + ", p_recordsWrittenInFile=" + p_recordsWrittenInFile + ", p_fileName=" + p_fileName);
        try {
            String processDateStr = BTSLUtil.getDateStringFromDate(p_beingProcessedDate);
            if (_fileRecordMap.isEmpty()) {
                _fileNameMap.put(p_fileName, p_recordsWrittenInFile);
                _fileRecordMap.put(processDateStr, _fileNameMap);
            } else {
                if (_fileRecordMap.containsKey(processDateStr)) {
                    _fileNameMap = (Hashtable<String, Long>) _fileRecordMap.get(processDateStr);
                    _fileNameMap.put(p_fileName, p_recordsWrittenInFile);
                    _fileRecordMap.put(processDateStr, _fileNameMap);
                } else {
                    _fileNameMap = new Hashtable<String, Long>();
                    _fileNameMap.put(p_fileName, p_recordsWrittenInFile);
                    _fileRecordMap.put(processDateStr, _fileNameMap);
                }
            }

        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        } finally {
            if (LOG.isDebugEnabled())
                LOG.debug("CSVHourlyFileGeneratorProcess", "Exiting  " + methodName);
        }
    }

    @SuppressWarnings("unchecked")
    private static void writeProcessFileSummary(String p_dirPath, String p_fileEXT, String p_processId) throws BTSLBaseException {
        final String methodName = "writeProcessFileSummary";
        if (LOG.isDebugEnabled())
            LOG.debug("CSVHourlyFileGeneratorProcess", " Entered: " + methodName + " p_dirPath=" + p_dirPath + ", p_fileEXT=" + p_fileEXT);
        PrintWriter out = null;
        File newFile = null;
        try {
            String writeProcessFileName = null;
            String fileData = null;
            String fileHeader = null;
            String processDate = null;
            writeProcessFileName = p_dirPath + File.separator + p_processId + "Trans_Stat_" + BTSLUtil.getDateTimeStringFromDate(new Date(), "ddMMyy") + p_fileEXT;
            LOG.debug("CSVHourlyFileGeneratorProcess", methodName + " fileName=" + writeProcessFileName);
            newFile = new File(writeProcessFileName);
            boolean isFileAlreadyExists = false;
            newFile = new File(writeProcessFileName);
            if (!newFile.exists()) {
                newFile.createNewFile();
                isFileAlreadyExists = false;
            } else {
                isFileAlreadyExists = true;
            }
            String transSeperator = null;
            transSeperator = writeCsvLineSeparator(methodName);
            if (newFile.exists()) {
                if (!isFileAlreadyExists) {
                    out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
                } else {
                    out = new PrintWriter(new BufferedWriter(new FileWriter(newFile, true)));
                }
                fileHeader = "Date" + transSeperator + "Files_Number" + transSeperator + "File_Name" + transSeperator + "Total_Records";
                out.write(fileHeader + "\n");
                Map<String, Long> fileRecord = null;
                _fileRecordMap.comparator();
                Set<String> keyList = _fileRecordMap.keySet();
                Iterator<String> itrProcessDate = keyList.iterator();
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
                    fileRecord = (Hashtable<String, Long>) _fileRecordMap.get(processDate);
                    itrFile = (fileRecord.keySet()).iterator();
                    fileData = processDate + transSeperator + new Integer(fileRecord.size()).toString() + transSeperator;
                    while (itrFile.hasNext()) {
                        file = itrFile.next().toString();
                        fileData = fileData + file + transSeperator + fileRecord.get(file).toString();
                        out.append(fileData + "\n");
                        i++;
                    }
                }
                out.flush();
            } else {
                LOG.info(methodName, "  fileName=" + writeProcessFileName + "does not exists on system.");
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CSVHourlyFileGeneratorProcess[" + methodName + "]", "", "", "", "Exception:" + e.getMessage());
            return;
        } finally {
            closeFileResources(methodName, out);
        }
        if (LOG.isDebugEnabled())
            LOG.debug("CSVHourlyFileGeneratorProcess", "Exiting " + methodName);
    }

    /**
     * @param methodName
     * @return
     */
    private static String writeCsvLineSeparator(final String methodName) {
        String transSeperator;
        try {
            transSeperator = Constants.getProperty("DAILY_USER_BALANCE_TRANSCATION_STAT_SEPERATOR");
        } catch (RuntimeException e) {
            transSeperator = ";";
            LOG.errorTrace(methodName, e);
        }
        return transSeperator;
    }

    /**
     * @param methodName
     * @param out
     */
    private static void closeFileResources(final String methodName, PrintWriter out) {
        try {
            if (out != null) {
                out.close();
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        try {
            if (_fileRecordMap != null) {
                _fileRecordMap.clear();
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
        try {
            if (_fileNameMap != null) {
                _fileNameMap.clear();
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        }
    }
}

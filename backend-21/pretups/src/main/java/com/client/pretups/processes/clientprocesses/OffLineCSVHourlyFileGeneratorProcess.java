package com.client.pretups.processes.clientprocesses;


/**
 * @(#
 OffLineCSVHourlyFileGeneratorProcess
 *                             Copyright(c) 2006, Bharti Telesoft Ltd.
 *                             All Rights Reserved
 *
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                                      Vishal Kumar    27/09/2016 Initial Creation
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 */

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
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
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
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.processes.businesslogic.CsvMinutelyProcessDAO;
import com.btsl.pretups.processes.businesslogic.DateSorting;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.processes.csvgenerator.CSVFileVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

public class OffLineCSVHourlyFileGeneratorProcess {

        private OffLineCSVHourlyFileGeneratorProcess()
        {

        }

    private static ArrayList<String> fileNameLst = new ArrayList<String>();
    private static ProcessStatusVO processStatusVoOffLine;
    private static ProcessBL processBL = null;
    private static HashMap csvMap = new HashMap();
    private static Properties csvproperties = new Properties();


    private static Log logger = LogFactory.getLog(OffLineCSVHourlyFileGeneratorProcess.class.getName());

    private static String finalMasterDirectoryPath = null;
    private static String fileEXT = ".csv";
    protected static HashMap<String, Long> fileNameMap = null;
    protected static TreeMap<String, Object> fileRecordMap = null;
    protected static String processId = null;
    protected static String tempprocessId = null;
    static int beforeInterval;
    static Date finalDateTobeUpdatedInProcess;
    // Ended Here
    public static void main(String arg[]) {
        final String methodName = "main";
        try {
            if (arg.length != 3) {

                    System.out.println("Usage : OffLineCSVHourlyFileGeneratorProcess [Constants file] [LogConfig file] [csvConfigFile file] [Param for running the file]");
                    return;
               }
            File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
                System.out.println(methodName + " Constants File Not Found .............");
                return;
            }
            File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                System.out.println(methodName + " Logconfig File Not Found .............");
                return;
            }

            File csvConfigFile = new File(arg[2]);
            if (!csvConfigFile.exists()) {
                System.out.println(methodName + " csvConfigFile.props File Not Found .............");
                return;
            }




            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            csvproperties.load(new FileInputStream(csvConfigFile));

           //processId=csvproperties.getProperty("PROCESS_ID").split(",")[0];
           processId=csvproperties.getProperty("PROCESS_ID");

            System.out.println("OffLineCSVHourlyFileGeneratorProcess=" +processId);


        } catch (Exception e) {

                logger.error(methodName, " Error in Loading Files ...........................: " + e.getMessage());
            logger.errorTrace(methodName, e);
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            process();
        } catch (BTSLBaseException be) {
            logger.error("main", "BTSLBaseException: " + be.getMessage());
            logger.errorTrace(methodName, be);
        } finally {
            if (logger.isDebugEnabled())
                logger.debug("main", "Exiting..... ");
            ConfigServlet.destroyProcessCache();
        }
    }

    private static void process() throws BTSLBaseException {
        final String methodName = "process";
        Date processedUpto = null;
        Date currentDateTime = new Date();
        Connection con = null;

        boolean statusOk = false;
        Iterator itr = null;
        CSVFileVO csvfilevo = null;

        try {

            fileNameMap = new HashMap<String, Long>();
            fileRecordMap = new TreeMap<String, Object>(new DateSorting());
            if (logger.isDebugEnabled())
                logger.debug(methodName, "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free :" + Runtime.getRuntime().freeMemory() / 1049576);
            Calendar cal = Calendar.getInstance();
            currentDateTime = cal.getTime(); // Current Date
            loadConstantParameters();
                itr = csvMap.keySet().iterator();
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (logger.isDebugEnabled())
                    logger.debug(methodName, " DATABASE Connection is NULL ");
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OffLineCSVHourlyFileGeneratorProcess [process]", "", "", "", "DATABASE Connection is NULL");
                return;
            }

//          Iterator itr = csvMap.keySet().iterator();
//              CSVFileVO csvfilevo = null;
                while (itr.hasNext()) {
                try{
                        csvfilevo = (CSVFileVO) csvMap.get((String) itr.next());
            processBL = new ProcessBL();
            processStatusVoOffLine = processBL.checkProcessUnderProcess(con, csvfilevo.getProcessId());
            beforeInterval = (int) processStatusVoOffLine.getBeforeInterval() / (60 * 24);

            System.out.println("beforeInterval VVVVVVVVVV AAAA :"+beforeInterval);
            statusOk = processStatusVoOffLine.isStatusOkBool();
            if (statusOk) {
                con.commit();
                processedUpto = processStatusVoOffLine.getExecutedUpto();
                if (processedUpto != null) {
//                    CSVFileVO csvfilevo = null;
                    // method call to create master directory and child
                    // directory if does not exist
                    // String
                    // _childDirectory=createDirectory(_masterDirectoryPathAndName,processId,dateCount);
                    // method call to fetch transaction data and write it in
                    // files
  //                  Iterator itr = csvMap.keySet().iterator();

                    try {
    //                    while (itr.hasNext()) {
      //                      csvfilevo = (CSVFileVO) csvMap.get((String) itr.next());
                             finalDateTobeUpdatedInProcess=fetchQuery( processedUpto, csvfilevo.getDirName(), csvfilevo.getPrefixName(), csvfilevo.getHeaderName(), csvfilevo.getExtName(), csvfilevo.getQueryName(), Long.parseLong(csvproperties.getProperty("MAX_ROWS")),csvfilevo.getTempTable(),csvfilevo.getProcessId());
                            Thread.sleep(500);
        //                }
                    } catch (Exception e) {
                        logger.errorTrace(methodName, e);
                        logger.error(methodName, "csvfilevo=" + csvfilevo.toString() + "  Exception : " + e.getMessage());
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "OffLineCSVHourlyFileGeneratorProcess [process]", "", "", "", "csvfilevo=" + csvfilevo.toString() + " Exception =" + e.getMessage());
                    }
                    processStatusVoOffLine.setExecutedUpto(finalDateTobeUpdatedInProcess);
                    processStatusVoOffLine.setExecutedOn(currentDateTime);

                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "OffLineCSVHourlyFileGeneratorProcess[process]", "", "", "", " OffLineCSVHourlyFileGeneratorProcess process has been executed successfully.");
                } else
                    throw new BTSLBaseException(methodName, methodName, PretupsErrorCodesI.DWH_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);

                }
        } catch (BTSLBaseException be) {
            logger.error(methodName, "BTSLBaseException : " + be.getMessage());
            logger.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            logger.error(methodName, "Exception : " + e.getMessage());
            logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "OffLineCSVHourlyFileGeneratorProcess[process]", "", "", "", " Exception =" + e.getMessage());
            throw new BTSLBaseException(methodName, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            // 07-MAR-2014 for OCI client
  //          Iterator<String> itr = csvMap.keySet().iterator();
    //        while (itr.hasNext()) {
       //         CSVFileVO  csvfilevo = (CSVFileVO) csvMap.get((String) itr.next());
                finalMasterDirectoryPath = csvfilevo.getDirName();
                if (!BTSLUtil.isNullString(csvfilevo.getExtName()))
                    fileEXT = csvfilevo.getExtName();
                if (!BTSLUtil.isNullString(csvfilevo.getProcessId()))
                    processId = csvfilevo.getProcessId();
                String isSummaryFileReq = "N";
                isSummaryFileReq = csvproperties.getProperty("SUMMARY_FILE_REQUIRED");
                if (BTSLUtil.isNullString(isSummaryFileReq)) {
                    isSummaryFileReq = "N";
                }
                if ("Y".equalsIgnoreCase(isSummaryFileReq))
                    try {
                        writeFileSummary(finalMasterDirectoryPath, fileEXT, processId);
                    } catch (Exception e) {
                        logger.errorTrace(methodName, e);
                    }
         //   }


            // if the status was marked as under process by this method call,
            // only then it is marked as complete on termination
            if (statusOk) {
                try {
                    if (markProcessStatusAsComplete(con, processId) == 1)
                        try {
                            con.commit();
                        } catch (Exception e) {
                            logger.errorTrace(methodName, e);
                        }
                    else
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            logger.errorTrace(methodName, e);
                        }
                } catch (Exception e) {
                    logger.errorTrace(methodName, e);
                }
            }
            if (logger.isDebugEnabled())
            logger.debug(methodName, "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free: " + Runtime.getRuntime().freeMemory() / 1049576);
            if (logger.isDebugEnabled())
                logger.debug(methodName, "Exiting..... ");
        }
           }
                }
                catch(Exception e){
                        logger.errorTrace(methodName, e);
                }
                finally{
                        try {
                                if (con != null)
                                        con.close();
                        } catch (Exception ex) {
                                logger.errorTrace(methodName, ex);
                                if (logger.isDebugEnabled())
                                        logger.debug(methodName, "Exception closing connection ");
                        }
                }
    }

    private static void loadConstantParameters() throws BTSLBaseException {
        final String methodName = " loadConstantParameters";
        if (logger.isDebugEnabled())
            logger.debug("loadParameters", " Entered: ");
        try {
                                initialize(processId);
            logger.debug(methodName, " Required information successfuly loaded from csvConfigFile.properties...............: ");
        } catch (BTSLBaseException be) {
            logger.error(methodName, "BTSLBaseException : " + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OffLineCSVHourlyFileGeneratorProcess[loadConstantParameters]", "", "", "", "Message:" + be.getMessage());
            logger.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            logger.error(methodName, "Exception : " + e.getMessage());
            logger.errorTrace(methodName, e);
            BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OffLineCSVHourlyFileGeneratorProcess[loadConstantParameters]", "", "", "", "Message:" + btslMessage);
            throw new BTSLBaseException(methodName, "loadConstantParameters", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }

    }



    private static Date fetchQuery( Date p_beingProcessedDate, String p_dirPath, String p_fileName, String p_fileLabel, String p_fileEXT, String p_sqlQuery,  long p_maxFileLength,String p_fileSuffix,String p_processID) throws BTSLBaseException {
        final String METHOD_NAME = "fetchQuery";
        if (logger.isDebugEnabled())
            logger.debug(METHOD_NAME, " Entered: p_beingProcessedDate=" + p_beingProcessedDate +
                        " p_dirPath=" + p_dirPath + " pfileName=" + p_fileName + " pfileLabel=" + p_fileLabel +
                        " pfileEXT=" + p_fileEXT + " pmaxFileLength=" + p_maxFileLength+ "p_processID="+p_processID);

        Connection p_conDR=null;
        try{
                p_conDR=OracleUtil.getExternalDBConnection();
                }
        catch(Exception e)
        {
                logger.errorTrace(METHOD_NAME, e);
        }


            CsvMinutelyProcessDAO csvMinutelyProcessDao = new CsvMinutelyProcessDAO();
            if (logger.isDebugEnabled())
                logger.debug(METHOD_NAME, " Entered: p_beingProcessedDate="
                        + p_beingProcessedDate + " p_dirPath=" + p_dirPath
                        + " p_fileName=" + p_fileName + " p_fileLabel="
                        + p_fileLabel + " p_fileEXT=" + p_fileEXT
                        + " p_maxFileLength=" + p_maxFileLength);
            String processStatusVO = "";
            Date fromDate = null;
            //Date tempDate = null;
            Date afterAddingBeforeMins = null;
            String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
            Formatter fmt = new Formatter();
            Calendar cal = Calendar.getInstance();
            fmt.format("Time using 24-hour clock: %tT%n", cal);
            //For D-1 concept

            //cal.add(Calendar.DATE,-1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            //END
            Date startDate = cal.getTime();
            String executedUpto = "";

            processStatusVO = csvMinutelyProcessDao
                    .fetchMinutelyProcessStatus(p_processID);
                System.out.println("AA###"+processStatusVO);
            String[] temp;
            String delimiter = "#";
            temp = processStatusVO.split(delimiter);
             executedUpto = temp[0];
            System.out.println("AA"+executedUpto);
            try {
                        fromDate = BTSLUtil.getDateFromDateString(executedUpto,   DATE_FORMAT_NOW);

                        cal.setTime(fromDate);
                        cal.add(Calendar.HOUR, beforeInterval);
                        afterAddingBeforeMins =cal.getTime();

            } catch (ParseException e) {
                logger.errorTrace(METHOD_NAME, e);
            }

            String c2sFetchQuery = p_sqlQuery;
            if (logger.isDebugEnabled())
                logger.debug(METHOD_NAME, "sql fetch query:" + c2sFetchQuery);
            PreparedStatement c2sInsertPstmt = null;
            ResultSet rst = null;
	    Date tempDate;
            Map<String, String> processMap = null;
            if (logger.isDebugEnabled()) {
                logger.debug("the Report is going to be generated for the time Param 1 ",fromDate);
                logger.debug("the Report is going to be generated for the time Param 2",afterAddingBeforeMins);
            }

                         if (logger.isDebugEnabled())
                        logger.debug("the Report is for the process id mentioned in the Csv Confugration file  ",p_processID);

                        while (afterAddingBeforeMins.before(startDate) || afterAddingBeforeMins.equals(startDate)) {
                                 if (logger.isDebugEnabled()) {
                                        logger.debug("fromDate =>  ",fromDate);
                                        logger.debug("afterAddingBeforeMins => ",afterAddingBeforeMins);
                                 }
                try {
                    c2sInsertPstmt = p_conDR.prepareStatement(c2sFetchQuery);
                    if (logger.isDebugEnabled()) {
                        logger.debug(METHOD_NAME, "Select qrySelect:" + c2sFetchQuery);
                    }
                    if (BTSLUtil.countChar(c2sFetchQuery) == 2) {
                        c2sInsertPstmt.clearParameters();
                        c2sInsertPstmt.setTimestamp(1, BTSLUtil
                                .getTimestampFromUtilDate(fromDate));
                        c2sInsertPstmt.setTimestamp(2, BTSLUtil
                                .getTimestampFromUtilDate(afterAddingBeforeMins));
                    }

                    rst = c2sInsertPstmt.executeQuery();

                    p_conDR.commit();
                    processMap = new HashMap<String, String>();
                    while (rst.next()) {

                        processMap.put(rst.getString(1), rst.getString(2));
                    }
                    if (logger.isDebugEnabled())
                    logger.debug(METHOD_NAME,
                                    "Memory after loading sql query data: Total:"
                                            + Runtime.getRuntime().totalMemory()
                                            / 1049576 + " Free :"
                                            + Runtime.getRuntime().freeMemory()
                                            / 1049576 + " fordate:"
                                            + p_beingProcessedDate);
                    if (!processMap.isEmpty()) {
                        writeMinutelyDataInFile(p_dirPath, p_fileName, p_fileLabel,
                                p_beingProcessedDate, p_fileEXT, p_maxFileLength,
                                processMap, afterAddingBeforeMins,p_fileSuffix);
                    } else {
                        logger.info(METHOD_NAME, " no result found in map");
                    }
                    if (logger.isDebugEnabled())
                    logger.debug("the Process is going to update for the date ",afterAddingBeforeMins);
			tempDate = new Date(afterAddingBeforeMins.getTime());
                    csvMinutelyProcessDao.updateMinutelyProcessStatus(fromDate,
                            afterAddingBeforeMins, processId);
			afterAddingBeforeMins = tempDate;
                                    processStatusVoOffLine.setExecutedUpto(afterAddingBeforeMins);
                                    processStatusVoOffLine.setExecutedOn(fromDate);
                                    logger.debug(METHOD_NAME, "Memory after writing data files: Total: "
                            + Runtime.getRuntime().totalMemory() / 1049576
                            + " Free: " + Runtime.getRuntime().freeMemory()
                            / 1049576 + " for date :" + p_beingProcessedDate);
                } catch (BTSLBaseException be) {
                        logger.errorTrace(METHOD_NAME, be);
                    throw be;
                } catch (SQLException sqe) {
                        logger.errorTrace(METHOD_NAME, sqe);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR,
                            EventComponentI.SYSTEM, EventStatusI.RAISED,
                            EventLevelI.FATAL, "CSVMinutelyFileGeneratorProcess ["
                                    + METHOD_NAME + "]", "", "", "", "SQLException :"
                                    + sqe.getMessage());
                    throw new BTSLBaseException(" CSVMinutelyFileGeneratorProcess",
                                METHOD_NAME, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
                } catch (Exception ex) {
                        logger.errorTrace(METHOD_NAME, ex);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR,
                            EventComponentI.SYSTEM, EventStatusI.RAISED,
                            EventLevelI.FATAL, "CSVMinutelyFileGeneratorProcess[ "
                                    + METHOD_NAME + "]", "", "", "", "SQLException :"
                                    + ex.getMessage());
                    throw new BTSLBaseException("CSVMinutelyFileGeneratorProcess ",
                                METHOD_NAME, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
                } finally {

                        try {
                        if (c2sInsertPstmt != null)
                                    c2sInsertPstmt.close();

                        }
                        catch (Exception ex) {
                                logger.errorTrace(METHOD_NAME, ex);}

                      }

                fromDate = afterAddingBeforeMins;
                cal.add(Calendar.HOUR, beforeInterval);
                afterAddingBeforeMins =cal.getTime();

               }

                        cal.add(Calendar.HOUR, -beforeInterval);
                        afterAddingBeforeMins =cal.getTime();
                        if (logger.isDebugEnabled())
                            logger.debug(METHOD_NAME, "Exiting" +afterAddingBeforeMins);
                        return afterAddingBeforeMins;


       }


    private static void writeMinutelyDataInFile(String p_dirPath,
            String p_fileName, String p_fileLabel, Date p_beingProcessedDate,
            String p_fileEXT, long p_maxFileLength,
            Map<String, String> processMap, Date executedUpto,String p_fileSuffix)
            throws BTSLBaseException {
        final String methodName = "writeMinutelyDataInFile";
        if (logger.isDebugEnabled())
                logger.debug(methodName, " Entered:  p_dirPath=" + p_dirPath
                    + " p_fileName=" + p_fileName + " p_fileLabel="
                    + p_fileLabel + " p_beingProcessedDate="
                    + p_beingProcessedDate + " p_fileEXT=" + p_fileEXT
                    + " p_maxFileLength=" + p_maxFileLength + " executedUpto=" + executedUpto);
        long recordsWrittenInFile = 0;
        PrintWriter out = null;
        int fileNumber = 0;
        String fileName = "";
        File newFile = null;
        String fileHeader = null;

        PreparedStatement selectStmt = null;
        Formatter fmt = new Formatter();
        Calendar cal = Calendar.getInstance();
        fmt.format("Time using 24-hour clock: %tT%n", cal);
        try {
            SimpleDateFormat sdf = null;
            sdf = new SimpleDateFormat(csvproperties
                    .getProperty("FILE_SUBSTR_DATE_FORMAT"));
            fileNumber = 1;
            int numberOfrecords=processMap.size();
            System.out.println(sdf.format(executedUpto) + "VVVVVVVVVV");
            String completeDate = sdf.format(executedUpto);
            String date = completeDate.split("_")[0];
            String time = completeDate.split("_")[1];
            String hour = time.substring(0,2);

            if(hour.equals("00"))
                {
                        Date tempDate = new Date();
                        tempDate.setTime(executedUpto.getTime());
                        Calendar cal1 = Calendar.getInstance();
                        cal1.setTime(tempDate);
                        cal1.add(Calendar.DATE, -1);
                        tempDate.setTime(cal1.getTimeInMillis());
                        completeDate = sdf.format(tempDate);
                        date = completeDate.split("_")[0];
                        time = completeDate.split("_")[1];
                        hour = time.substring(0,2);
                        System.out.println("##"+cal1.getTime());
                                System.out.println("###"+date+hour+time);
                }

            System.out.println("###"+hour);
            if(Integer.parseInt(hour)<10)
                hour =  "000"+Integer.parseInt(hour);
            else
                hour =  "00"+Integer.parseInt(hour);

            if (Integer.toString(fileNumber).length() == 1||Integer.toString(fileNumber).length() == 2||Integer.toString(fileNumber).length() == 3)
                fileName = p_dirPath + File.separator + p_fileName
                        +date+p_fileSuffix
                        +hour
                        + p_fileEXT;
            else {
                fileName = p_dirPath + File.separator + p_fileName
                                +date+p_fileSuffix
                                +hour
                        + p_fileEXT;
            }
            if (logger.isDebugEnabled())
            logger.debug(methodName, "  fileName=" + fileName);

            newFile = new File(fileName);
            fileNameLst.add(fileName);

            out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));

            //fileHeader = constructFileHeader(fileNumber, p_fileLabel);
            //out.write(fileHeader);



            Set<String> setOfKeys = processMap.keySet();
            Iterator<String> iterator = setOfKeys.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();

                out.write(processMap.get(key) + "\n");

                recordsWrittenInFile++;


            }

            if (recordsWrittenInFile > 0) {
                //fileFooter = constructHourlyFileFooter(recordsWrittenInFile);
                //out.write(fileFooter);
            } else {
                if (out != null)
                    out.close();
            }
            if (out != null)
                out.close();
        } catch (Exception e) {
                 logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
                    EventStatusI.RAISED, EventLevelI.FATAL,
                    "CSVMinutelyFileGeneratorProcess[" + methodName + "]", "",
                    "", "", "Exception :" + e.getMessage());
            return;
        } finally {
            if (selectStmt != null)
                try {
                    selectStmt.close();
                } catch (Exception e) {
                        logger.errorTrace(methodName, e);
                }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                logger.errorTrace(methodName, e);
            }
        }
        if (logger.isDebugEnabled())
                logger.debug(methodName, "Exiting ");
    }


    private static String constructFileHeader(long p_fileNumber, String p_fileLabel) {
        final String METHOD_NAME = "constructFileHeader";
        SimpleDateFormat sdf = null;
        try {
            sdf = new SimpleDateFormat(csvproperties.getProperty("DATE_TIME_FORMAT"));
        } catch (Exception e) {
            sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);
            logger.errorTrace(METHOD_NAME, e);
        }
        StringBuffer fileHeaderBuf = new StringBuffer("");
        //fileHeaderBuf.append("\n" + " Present Date and Time=" + sdf.format(new Date()));
        // fileHeaderBuf.append("\n"+" For Date="+sdf.format(p_beingProcessedDate));
        //fileHeaderBuf.append("\n" + " File Number=" + p_fileNumber);
        fileHeaderBuf.append("\n" + p_fileLabel+ "\n");
        //fileHeaderBuf.append("\n" + "[STARTDATA]" + "\n");
        return fileHeaderBuf.toString();
    }

    private static int markProcessStatusAsComplete(Connection pcon, String pprocessId) throws BTSLBaseException {
        final String methodName = "markProcessStatusAsComplete";
        if (logger.isDebugEnabled())
            logger.debug(methodName, " Entered:  p_processId:" + pprocessId);
        int updateCount = 0;
        Date currentDate = new Date();
        ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        processStatusVoOffLine.setProcessID(pprocessId);
        processStatusVoOffLine.setProcessStatus(ProcessI.STATUS_COMPLETE);
        processStatusVoOffLine.setStartDate(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetail(pcon, processStatusVoOffLine);
        } catch (Exception e) {
            logger.errorTrace(methodName, e);
            logger.error(methodName, "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OffLineCSVHourlyFileGeneratorProcess[markProcessStatusAsComplete]", "", "", "", " Exception:" + e.getMessage());
            throw new BTSLBaseException(methodName, "markProcessStatusAsComplete", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (logger.isDebugEnabled())
                logger.debug(methodName, "Exiting: updateCount=" + updateCount);
        } // end of finally
        return updateCount;

    }

    public static void initialize(String p_processIDs) throws BTSLBaseException {
        final String methodName = " initialize";
        if (logger.isDebugEnabled())
            logger.debug(methodName, "Entered p_processIDs::" + p_processIDs);
        String processId = null;
        String[] inStrArray = null;
        try {
            CSVFileVO csvFileVO ;
            inStrArray = p_processIDs.split(",");
            if (BTSLUtil.isNullArray(inStrArray))
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS3_NO_INTERFACEIDS);

                for(int pCount = 0;pCount<inStrArray.length;pCount++)
                {
                //processId=p_processIDs;
                processId=inStrArray[pCount];
                csvFileVO = new CSVFileVO();
                csvFileVO.setProcessId(processId);
                csvFileVO.setQueryName(csvproperties.getProperty(processId + "_QRY"));
                csvFileVO.setDirName(csvproperties.getProperty(processId + "_DIR"));
                csvFileVO.setExtName(csvproperties.getProperty(processId + "_EXT"));
                csvFileVO.setHeaderName(csvproperties.getProperty(processId + "_HEADER"));
                csvFileVO.setPrefixName(csvproperties.getProperty(processId + "_PREFIX_NAME"));
                csvFileVO.setTempTable(csvproperties.getProperty(processId + "_TEMP_TBL"));
                if (logger.isDebugEnabled())
                    logger.debug("OffLineCSVHourlyFileGeneratorProcess[initialize]", "csvFileVO::" + csvFileVO);
                csvMap.put(processId, csvFileVO);
                }

        } catch (BTSLBaseException be) {
            logger.errorTrace(methodName, be);
            logger.error("initialize", "BTSLBaseException be:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            logger.errorTrace(methodName, e);
            logger.error(methodName, "Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OffLineCSVHourlyFileGeneratorProcess[initialize]", "String of p_processIDs ids=" + p_processIDs, "", "", "While initializing the processIDs for the CSV file generator process =" + processId + " get Exception=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS3_NODE_INITIALIZATION);
        } finally {
            if (logger.isDebugEnabled())
                logger.debug("initialize", "Exited _csvMap::" + csvMap);
        }
    }
    private static void writeFileSummary(String p_dirPath, String p_fileEXT, String p_processId) throws BTSLBaseException {
        final String METHOD_NAME = "writeFileSummary";
        if (logger.isDebugEnabled())
            logger.debug(METHOD_NAME, " Entered: writeFileSummary() p_dirPath=" + p_dirPath + ", p_fileEXT=" + p_fileEXT);
        PrintWriter out = null;
        File newFile = null;
        try {
            String fileName = null;
            String fileData = null;
            String fileHeader = null;
            String processDate = null;
            // Changed on 05-MAR-2014
            fileName = p_dirPath + File.separator + p_processId + "Trans_Stat_" + BTSLUtil.getDateTimeStringFromDate(new Date(), "ddMMyy") + p_fileEXT;
            // Ended Here
            logger.debug(METHOD_NAME, " writeFileSummary() fileName=" + fileName);

            newFile = new File(fileName);
            boolean isFileAlreadyExists = false;
            newFile = new File(fileName);
            if (!newFile.exists()) {
                newFile.createNewFile();
                isFileAlreadyExists = false;
            } else {
                isFileAlreadyExists = true;
            }
            // Added by Diwakar on 07-MAR-2014 for OCI client
            String transSeperator = null;
            try {
                transSeperator = Constants.getProperty("DAILY_USER_BALANCE_TRANSCATION_STAT_SEPERATOR");
            } catch (RuntimeException e) {
                logger.errorTrace(METHOD_NAME, e);
                transSeperator = ";";
            }
            if (newFile.exists()) {
                // _fileNameLst.add(fileName);
                if (!isFileAlreadyExists) {
                    out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
                } else {
                    out = new PrintWriter(new BufferedWriter(new FileWriter(newFile, true /*
                                                                                           * append
                                                                                           * =
                                                                                           * true
                                                                                           */)));
                }
                // fileHeader="Date,Files_Number,File_Name,Total_Records";
                fileHeader = "Date" + transSeperator + "Files_Number" + transSeperator + "File_Name" + transSeperator + "Total_Records";
                out.write(fileHeader + "\n");
                HashMap<String, Long> fileRecord = null;
                fileRecordMap.comparator();
                Set<String> keyList = fileRecordMap.keySet();
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
                    fileRecord = (HashMap) fileRecordMap.get(processDate);
                    itrFile = (fileRecord.keySet()).iterator();
                    // fileData=processDate+","+new
                    // Integer(fileRecord.size()).toString()+",";
                    fileData = processDate + transSeperator + new Integer(fileRecord.size()).toString() + transSeperator;
                    while (itrFile.hasNext()) {
                        file = itrFile.next();
                        fileData = fileData + file + transSeperator + fileRecord.get(file).toString();
                        out.append(fileData + "\n");
                        i++;
                    }
                }
                out.flush();
            } else {
                logger.error(METHOD_NAME, " writeFileSummary() fileName=" + fileName + "does not exists on system.");
            }
        } catch (Exception e) {
            logger.debug(METHOD_NAME, "Exception writeFileSummary(): " + e.getMessage());
            logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OffLineCSVHourlyFileGeneratorProcess[writeFileSummary]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(METHOD_NAME, "writeFileSummary()", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (out != null)
                out.close();
            if (fileRecordMap != null)
                fileRecordMap.clear();
            if (fileNameMap != null)
                fileNameMap.clear();
            if (logger.isDebugEnabled())
                logger.debug(METHOD_NAME, "Exiting writeFileSummary() ");
        }
      }
}


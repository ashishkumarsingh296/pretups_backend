package com.btsl.pretups.processes.clientprocesses;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

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
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.processes.csvgenerator.CSVFileVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.OracleUtil;
import com.ibm.icu.util.Calendar;

/**
 * @(#)HourlyC2SDWHProcess .java
 *                         This class will be used to generated CSV file for
 *                         populating the RP2P Hourly transaction based on
 *                         configured interval at database end.
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Created on Created by History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Sep 27, 2014 Mahindra Comviva Initial creation
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Copyright(c) 2014 Comviva Technologies Ltd.
 */

public class HourlyC2SDWHProcessOCI {
    private static String message = "";
    private static ProcessBL _processBL = null;
    private static ProcessStatusVO _processStatusVO;
    private static Log _logger = LogFactory.getLog(HourlyC2SDWHProcessOCI.class.getName());
    private static ArrayList<String> _fileNameLst = new ArrayList<String>();
    private static Properties _csvproperties = new Properties();
    private static HashMap<String, CSVFileVO> _csvMap = new HashMap<String, CSVFileVO>();

    /**
     * to ensure no class instantiation 
     */
    private HourlyC2SDWHProcessOCI(){
    	
    }
    public static void main(String[] args) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Date nextDwhExecutionDateTime = null;
        String reportTo = null;
        final String prevDateStr = null;
        Date processedUpto = null;
        String processId = null;
        boolean statusOk = false;
        int beforeInterval = 0;
        final String methodName = "main";
        try {
            if (args.length != 3) {
                _logger.info(methodName, "Usage : HourlyC2SDWHProcess [Constants file] [LogConfig file] [csvConfigFile4HourlyC2SDWH.props]");
                return;
            }
            final File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
                _logger.info(methodName, " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = new File(args[1]);
            if (!logconfigFile.exists()) {
                _logger.info(methodName, " Logconfig File Not Found .............");
                return;
            }

            final File csvConfigFile = new File(args[2]);
            if (!csvConfigFile.exists()) {
                _logger.info(methodName, "HourlyC2SDWHProcessOCI : " + " csvConfigFile4HourlyC2SDWH.props File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            try(FileInputStream fileInputStream = new FileInputStream(csvConfigFile))
            {
            _csvproperties.load(fileInputStream);
            }
        } catch (Exception e) {
            _logger.errorTrace(methodName, e);
            ConfigServlet.destroyProcessCache();
            return;
        }
        processId = "HOURLYC2SDWH2";
    
        try {
        	// getting all the required parameters from
            // csvConfigFile4HourlyC2SDWH.props
            loadConstantParameters(processId);

            // Make Connection
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("HourlyC2SDWHProcess[main]", "Not able to get Connection for HourlyC2SDWHProcess: ");
                }
                throw new SQLException();
            }

            // method call to check status of the process
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(con, processId);
            statusOk = _processStatusVO.isStatusOkBool();
            beforeInterval = BTSLUtil.parseLongToInt( _processStatusVO.getBeforeInterval() / (60 * 24) );
            if (statusOk) {
                con.commit();
                // method call to find maximum date till which process has been
                // executed
                processedUpto = _processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    final Calendar cal = BTSLDateUtil.getInstance();
                    cal.add(Calendar.HOUR, -beforeInterval);
                    nextDwhExecutionDateTime = cal.getTime();
                } else {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("HourlyC2SDWHProcess[main]", " Date till which process has been executed is not found.");
                    }
                    return;
                }
            }

            // c2s dwh process will be exceuted from the start till to date -1
            if (_logger.isDebugEnabled()) {
                _logger.debug("HourlyC2SDWHProcess[main]",
                    "From date=" + prevDateStr + " To Date=" + reportTo + " processedUpto.compareTo(nextDwhExecutionDateTime)=" + processedUpto
                        .compareTo(nextDwhExecutionDateTime));
            }

            // If process is already ran for the last day then do not run again
            if (processedUpto != null && processedUpto.compareTo(nextDwhExecutionDateTime) > 0) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "HourlyC2SDWHProcess[main]", "", "", "",
                    "Hourly C2S DWH already run for the date=" + String.valueOf(nextDwhExecutionDateTime));
                return;
            }
            try {
                final Date currentDate = new Date();
                final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);
                sdf.setLenient(false); // this is required else it will convert
                reportTo = sdf.format(currentDate); // Current Date
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
                reportTo = "";
                throw new BTSLBaseException("Not able to convert date to String");
            }

            final StringBuffer qrySelect4MIS = new StringBuffer(" SELECT executed_upto,BEFORE_INTERVAL ");
            qrySelect4MIS.append("	FROM PROCESS_STATUS ");
            qrySelect4MIS.append("	WHERE PROCESS_ID='" + processId + "' ");
            if (_logger.isDebugEnabled()) {
                _logger.debug("qrySelect4MIS", "Select qrySelect:" + qrySelect4MIS);
            }
            // Fetching of MIS DATA
            pstmt = con.prepareStatement(qrySelect4MIS.toString());
            rs = pstmt.executeQuery();
            // Date ld_from_date = new Date();
            Timestamp ld_from_date = null;
            long ld_before_interval = 0;
            while (rs.next()) {
                ld_from_date = rs.getTimestamp("executed_upto");
                ld_before_interval = rs.getLong("BEFORE_INTERVAL");
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("", "qrySelect4MIS : reportTo = " + reportTo + " | ld_from_date = " + ld_from_date + " | ld_before_interval = " + ld_before_interval);
            }
            int intervalInInt = 0;
            intervalInInt = Integer.parseInt(String.valueOf(ld_before_interval));
            ArrayList<String> noOfFilesToBeGenerated = findoutNoOfFilesToBeGenerated(ld_from_date, intervalInInt, reportTo);
            if (_logger.isDebugEnabled()) {
                _logger.debug("HourlyC2SDWHProcess[main]", " noOfFilesToBeGenerated = " + noOfFilesToBeGenerated);
            }
            final ArrayList<String> outputFromSelectQueries = new ArrayList<String>(1000);
            if (noOfFilesToBeGenerated != null && !noOfFilesToBeGenerated.isEmpty()) {
                final Iterator<String> iterator = noOfFilesToBeGenerated.iterator();
                while (iterator.hasNext()) {
                    final String startEndDate = iterator.next();
                    fetchTxnDetailsOnInterval4O2C(con, outputFromSelectQueries, startEndDate);
                    fetchTxnDetailsOnInterval4C2C(con, outputFromSelectQueries, startEndDate);
                    fetchTxnDetailsOnInterval4C2S(con, outputFromSelectQueries, startEndDate);

                    // method call to write data in the files
                    // if(outputFromSelectQueries!=null &&
                    // outputFromSelectQueries.size()>0)
                    {
                        CSVFileVO csvfilevo = null;
                        final Iterator<String> itr = _csvMap.keySet().iterator();
                        while (itr.hasNext()) {
                            csvfilevo = _csvMap.get(itr.next());
                        }

                        String[] arrStartEndDate = startEndDate.split(";");

                        // Write date into the generated file and name of file
                        // will be ended with completed hour
                        writeDataInFile(outputFromSelectQueries, csvfilevo.getDirName(), csvfilevo.getPrefixName(), csvfilevo.getHeaderName(), csvfilevo.getExtName(), Long
                            .parseLong(_csvproperties.getProperty("MAX_ROWS")), arrStartEndDate[0], arrStartEndDate[1], intervalInInt);
                        outputFromSelectQueries.clear();
                        final String[] minutesAndSecond = getMinutesAndSecond();
                        // StringBuffer StartEndDateComplete = new
                        // StringBuffer(arrStartEndDate[1]).append(minutesAndSecond[0]).append(minutesAndSecond[1]).append(minutesAndSecond[2]).append(minutesAndSecond[3]);
                        StringBuffer StartEndDateComplete = new StringBuffer(arrStartEndDate[1]);
                        String arrStartEndDateComplete = StartEndDateComplete.toString();
                        // Date date =
                        // BTSLUtil.getDateFromString(arrStartEndDateComplete,"dd-MM-yyyy HH:mm:ss");
                        final Date date = BTSLUtil.getDateFromString(arrStartEndDateComplete, "dd-MM-yyyy HH");
                        // date.setHours(date.getHours()+1);
                        _processStatusVO.setExecutedUpto(date);
                        _processStatusVO.setExecutedOn(date);
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("fetchQuery", "Process has been executed successfully for startEnd Date = " + startEndDate + " ExecutedUpTo = " + date);
                        }
                        arrStartEndDateComplete = null;
                        arrStartEndDate = null;
                        StartEndDateComplete = null;
                    }
                    /*
                     * else
                     * {
                     * if (_logger.isDebugEnabled()) _logger.debug("fetchQuery",
                     * "Process has been executed successfully with no record found for startEnd Date = "
                     * +startEndDate+" .");
                     * }
                     */

                }
                noOfFilesToBeGenerated = null;
                _logger.info("fetchQuery", "Process has been executed successfully.");
            }
            _logger.debug("fetchQuery", "Memory after writing data files: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime()
                .freeMemory() / 1049576 + " for date:");

        } catch (Exception e) {
            _logger.errorTrace(methodName, e);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception sqlex) {
                _logger.errorTrace(methodName, sqlex);
            }
            message = e.getMessage();
            // send the message as SMS
        } finally {
            try {
                if (statusOk) {
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
                }
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(methodName, ex);
                _logger.info(methodName, "Exception while closing statement in HourlyC2SDWHProcess method ");
            }
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    private static ArrayList<String> findoutNoOfFilesToBeGenerated(Date executedUpTo, int interval, String reportTo) {
        final String METHOD_NAME = "findoutNoOfFilesToBeGenerated";
        final ArrayList<String> findoutNoOfFilesToBeGenerated = new ArrayList<String>(1000);
        try {
            int noOfDateCounter = 0;
            try {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("findoutNoOfFilesToBeGenerated", "strExecutedUpTo = " + executedUpTo + " | strExecutToBe = " + reportTo);
                }

                // Create Calendar instance
                final Calendar calendar1 = BTSLDateUtil.getInstance();
                final Calendar calendar2 = BTSLDateUtil.getInstance();

                // Set the values for the calendar fields YEAR, MONTH, and
                // DAY_OF_MONTH.
                calendar1.setTime(BTSLUtil.getDateFromString(BTSLUtil.getDateStringFromDate(executedUpTo, PretupsI.TIMESTAMP_DATESPACEHHMMSS), PretupsI.TIMESTAMP_DATESPACEHHMMSS));
                calendar2.setTime(BTSLUtil.getDateFromString(reportTo, PretupsI.TIMESTAMP_DATESPACEHHMMSS));

                long miliSecondForDate1 = calendar1.getTimeInMillis();
                long miliSecondForDate2 = calendar2.getTimeInMillis();

                // Calculate the difference in millisecond between two dates
                long diffInMilis = miliSecondForDate2 - miliSecondForDate1;
                long diffInHour = diffInMilis / (60 * 60 * 1000);
                final int intervalInHour = interval / 60;
                if (_logger.isDebugEnabled()) {
                    _logger.debug("findoutNoOfFilesToBeGenerated", "Difference in [" + intervalInHour + "] Hours : " + diffInHour);
                }
                String startDateHour = null;
                String endDateHour ;
                int difference ;
                StringBuffer sbfinalStartDateEndDate = null;
                do {
                    startDateHour = BTSLUtil.getDateTimeStringFromDate(calendar1.getTime(), "dd-MM-yyyy HH");
                    miliSecondForDate1 = calendar1.getTimeInMillis();

                    calendar1.add(Calendar.HOUR, intervalInHour);
                    endDateHour = BTSLUtil.getDateTimeStringFromDate(calendar1.getTime(), "dd-MM-yyyy HH");

                    difference = calendar2.compareTo(calendar1);
                    miliSecondForDate2 = calendar2.getTimeInMillis();

                    diffInMilis = miliSecondForDate2 - miliSecondForDate1;
                    diffInHour = diffInMilis / (60 * 60 * 1000);
                    // calendar1.add(Calendar.HOUR,1);
                    // if(_logger.isDebugEnabled())
                    // _logger.debug("findoutNoOfFilesToBeGenerated","difference = "+difference+"diffInHour = "+diffInHour+" startDateHour = "+
                    // startDateHour+" : endDateHour = " +
                    // endDateHour+" : diffInMilis = "+diffInMilis);
                    if (difference > 0 && diffInHour >= intervalInHour) {
                        if (_logger.isDebugEnabled()) {
                            _logger.debug("findoutNoOfFilesToBeGenerated",
                                "difference = " + difference + "diffInHour = " + diffInHour + " startDateHour = " + startDateHour + " : endDateHour = " + endDateHour);
                        }
                        sbfinalStartDateEndDate = new StringBuffer(startDateHour).append(";").append(endDateHour);
                        findoutNoOfFilesToBeGenerated.add(sbfinalStartDateEndDate.toString());
                        sbfinalStartDateEndDate = null;
                        noOfDateCounter++;
                    }
                } while (diffInHour >= intervalInHour && difference > 0);
                if (_logger.isDebugEnabled()) {
                    _logger.debug("findoutNoOfFilesToBeGenerated", "noOfDateCounter = " + noOfDateCounter);
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
        }
        return findoutNoOfFilesToBeGenerated;
    }

    private static void fetchTxnDetailsOnInterval4O2C(Connection con, ArrayList<String> outputFromSelectQueries, String startEndDate) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("fetchTxnDetailsOnInterval4O2C", "Entered ");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer qrySelect4O2C = null;
        final String METHOD_NAME = "fetchTxnDetailsOnInterval4O2C";
        try {
            qrySelect4O2C = new StringBuffer(
                " SELECT (CT.TRANSFER_ID||';'||DECODE(CT.STATUS ,'CLOSE','200','240')||';'||CT.TRANSFER_SUB_TYPE||';'||CT.TYPE||';'||CT.FROM_USER_ID||';'||CT.MSISDN ");
            qrySelect4O2C.append(" ||';'||'OPT'||';'||CT.SENDER_CATEGORY_CODE||';'||CT.TO_USER_ID||';'||CT.TO_MSISDN||';'||RU.USER_NAME ");
            qrySelect4O2C.append(" ||';'||CT.RECEIVER_CATEGORY_CODE||';'||TO_CHAR(CT.CREATED_ON,'DD/MM/YYYY HH24:MI:SS')||';'||CTI.REQUIRED_QUANTITY ");
            qrySelect4O2C.append(" ||';'||CTI.SENDER_PREVIOUS_STOCK||';'||CTI.SENDER_POST_STOCK||';'||';'||';'||';'||'NA') DATA ");
            qrySelect4O2C.append(" FROM CHANNEL_TRANSFERS CT,CHANNEL_TRANSFERS_ITEMS CTI, USERS RU ");
            qrySelect4O2C.append(" WHERE CT.TRANSFER_ID=CTI.TRANSFER_ID(+) ");
            qrySelect4O2C.append(" AND CT.STATUS IN('CLOSE','CNCL') AND TRUNC(CT.CLOSE_DATE)=trunc(TO_DATE(?,'DD-MM-YYYY HH24'))");
            qrySelect4O2C.append(" AND CT.FROM_USER_ID='OPT' AND CT.TO_USER_ID=RU.USER_ID ");
            qrySelect4O2C.append(" AND CT.CLOSE_DATE >= TO_DATE(?,'DD-MM-YYYY HH24') AND CT.CLOSE_DATE < TO_DATE(?,'DD-MM-YYYY HH24') ");
            qrySelect4O2C.append(" ORDER BY CT.MODIFIED_ON,CT.TYPE ");

            if (_logger.isDebugEnabled()) {
                _logger.debug("qrySelect4O2C", "Select qrySelect4O2C:" + qrySelect4O2C);
            }

            // Fetching of O2C data
            pstmt = con.prepareStatement(qrySelect4O2C.toString());
            final String[] startDateEndDateWithHour = startEndDate.split(";");
            pstmt.setString(1, startDateEndDateWithHour[0]);
            pstmt.setString(2, startDateEndDateWithHour[0]);
            pstmt.setString(3, startDateEndDateWithHour[1]);
            if (_logger.isDebugEnabled()) {
                _logger.debug("qrySelect4O2C", "startDate= " + startDateEndDateWithHour[0] + " | endDate= " + startDateEndDateWithHour[1]);
            }
            // pstmt.setString(3, p_date+p_before_interval/1440);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String data = rs.getString("DATA");
                outputFromSelectQueries.add(data);
                if (_logger.isDebugEnabled()) {
                    _logger.debug("qrySelect4O2C", "startDate= " + startDateEndDateWithHour[0] + " | endDate= " + startDateEndDateWithHour[1] + " | data:" + data);
                }
                data = null;
            }
            pstmt.clearParameters();

        } catch (SQLException sql) {
            _logger.error("fetchTxnDetailsOnInterval4O2C", "SQLException:=" + sql.getMessage());
            _logger.errorTrace(METHOD_NAME, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "[fetchTxnDetailsOnInterval4O2C]", "", "", "",
                "SQLException:" + sql.getMessage());
        } catch (Exception e) {
            _logger.error("fetchTxnDetailsOnInterval4O2C", "Exception:=" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "[fetchTxnDetailsOnInterval4O2C]", "", "", "",
                "Exception:" + e.getMessage());
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("fetchTxnDetailsOnInterval4C2C", "Exit outputFromSelectQueries.size() = " + outputFromSelectQueries.size());
            }
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e1) {
                _logger.errorTrace(METHOD_NAME, e1);
            }
        }
    }

    private static void fetchTxnDetailsOnInterval4C2C(Connection con, ArrayList<String> outputFromSelectQueries, String startEndDate) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("fetchTxnDetailsOnInterval4C2C", "Entered ");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer qrySelect4C2C = null;
        final String METHOD_NAME = "fetchTxnDetailsOnInterval4C2C";
        try {
            qrySelect4C2C = new StringBuffer(
                " SELECT (CT.TRANSFER_ID||';'||DECODE(CT.STATUS ,'CLOSE','200','240')||';'||CT.TRANSFER_SUB_TYPE||';'||CT.TYPE||';'||CT.FROM_USER_ID||';'||CT.MSISDN ");
            qrySelect4C2C.append(" ||';'||SU.USER_NAME||';'||CT.SENDER_CATEGORY_CODE||';'||CT.TO_USER_ID||';'||CT.TO_MSISDN||';'||RU.USER_NAME ");
            qrySelect4C2C.append(" ||';'||CT.RECEIVER_CATEGORY_CODE||';'||TO_CHAR(CT.CREATED_ON,'DD/MM/YYYY HH24:MI:SS')||';'||CTI.REQUIRED_QUANTITY ");
            qrySelect4C2C.append(" ||';'||CTI.SENDER_PREVIOUS_STOCK||';'||CTI.SENDER_POST_STOCK||';'||';'||';'||';'||'NA') DATA ");
            qrySelect4C2C.append(" FROM CHANNEL_TRANSFERS CT,CHANNEL_TRANSFERS_ITEMS CTI, USERS SU, USERS RU ");
            qrySelect4C2C.append(" WHERE CT.TRANSFER_ID=CTI.TRANSFER_ID(+) ");
            qrySelect4C2C.append(" AND CT.STATUS IN('CLOSE','CNCL') AND TRUNC(CT.CLOSE_DATE)=trunc( TO_DATE(?,'DD-MM-YYYY HH24')) ");
            qrySelect4C2C.append(" AND SU.USER_ID=CT.FROM_USER_ID AND RU.USER_ID=CT.TO_USER_ID ");
            qrySelect4C2C.append(" AND CT.CLOSE_DATE >= TO_DATE(?,'DD-MM-YYYY HH24') AND CT.CLOSE_DATE < TO_DATE(?,'DD-MM-YYYY HH24') ");
            qrySelect4C2C.append(" ORDER BY CT.MODIFIED_ON,CT.TYPE ");

            if (_logger.isDebugEnabled()) {
                _logger.debug("qrySelect4C2C", "Select qrySelect4C2C:" + qrySelect4C2C);
            }
            // Fetching of C2C data
            pstmt = con.prepareStatement(qrySelect4C2C.toString());

            final String[] startDateEndDateWithHour = startEndDate.split(";");
            pstmt.setString(1, startDateEndDateWithHour[0]);
            pstmt.setString(2, startDateEndDateWithHour[0]);
            pstmt.setString(3, startDateEndDateWithHour[1]);
            // pstmt.setString(3, p_date+p_before_interval/1440);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String data = rs.getString("DATA");
                outputFromSelectQueries.add(data);
                if (_logger.isDebugEnabled()) {
                    _logger.debug("qrySelect4C2C", "startDate= " + startDateEndDateWithHour[0] + " | endDate= " + startDateEndDateWithHour[1] + " | data:" + data);
                }
                data = null;
            }
            pstmt.clearParameters();

        } catch (SQLException sql) {
            _logger.error("fetchTxnDetailsOnInterval4C2C", "SQLException:=" + sql.getMessage());
            _logger.errorTrace(METHOD_NAME, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "[fetchTxnDetailsOnInterval4C2C]", "", "", "",
                "SQLException:" + sql.getMessage());

        } catch (Exception e) {
            _logger.error("fetchTxnDetailsOnInterval4C2C", "Exception:=" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "[fetchTxnDetailsOnInterval4C2C]", "", "", "",
                "Exception:" + e.getMessage());

        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("fetchTxnDetailsOnInterval4C2C", "Exit outputFromSelectQueries.size() = " + outputFromSelectQueries.size());
            }
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e1) {
                _logger.errorTrace(METHOD_NAME, e1);
            }
        }
    }

    private static void fetchTxnDetailsOnInterval4C2S(Connection con, ArrayList<String> outputFromSelectQueries, String startEndDate) {
    	//local_index_implemented
        if (_logger.isDebugEnabled()) {
            _logger.debug("fetchTxnDetailsOnInterval4C2S", "Entered ");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer qrySelect4C2S = null;
        final String METHOD_NAME = "fetchTxnDetailsOnInterval4C2S";
        try {

            qrySelect4C2S = new StringBuffer(" SELECT (CT.TRANSFER_ID||';'||CT.TRANSFER_STATUS||';'||CT.SERVICE_TYPE||';'||'C2S'||';'||CT.SENDER_ID||';'||CT.SENDER_MSISDN ");
            qrySelect4C2S.append(" ||';'||U.USER_NAME||';'||CT.SENDER_CATEGORY||';'||'NA'||';'||CT.RECEIVER_MSISDN||';'||'NA' ");
            qrySelect4C2S.append(" ||';'||'NA'||';'||TO_CHAR(CT.TRANSFER_DATE_TIME,'DD/MM/YYYY HH24:MI:SS')||';'||CT.TRANSFER_VALUE ");
            qrySelect4C2S
                .append(" ||';'||CT.SENDER_PREVIOUS_BALANCE||';'||CT.SENDER_POST_BALANCE||';'||CT.ERROR_CODE||';'||KV.VALUE||';'||(TO_NUMBER(CT.END_TIME)-TO_NUMBER(CT.START_TIME)) ");
            qrySelect4C2S.append(" ||';'||CT.bonus_details) DATA ");
            qrySelect4C2S.append(" FROM C2S_TRANSFERS CT, USERS U,  KEY_VALUES KV ");
            qrySelect4C2S.append(" WHERE CT.TRANSFER_DATE=trunc(TO_DATE(?,'DD-MM-YYYY HH24'))  ");
            qrySelect4C2S.append(" AND CT.TRANSFER_DATE_TIME >= TO_DATE(?,'DD-MM-YYYY HH24')  ");
            qrySelect4C2S.append(" and CT.TRANSFER_DATE_TIME < TO_DATE(?,'DD-MM-YYYY HH24') ");
            qrySelect4C2S.append(" AND CT.SENDER_ID=U.USER_ID  ");
            qrySelect4C2S.append(" AND KV.KEY(+)=CT.ERROR_CODE  ");
            qrySelect4C2S.append(" AND KV.TYPE(+)='C2S_ERR_CD' ORDER BY CT.TRANSFER_DATE_TIME ");

            if (_logger.isDebugEnabled()) {
                _logger.debug("fetchTxnDetailsOnInterval4C2S", "Select qrySelect4C2S:" + qrySelect4C2S);
            }

            // Fetching of C2S data
            pstmt = con.prepareStatement(qrySelect4C2S.toString());
            String[] startDateEndDateWithHour = startEndDate.split(";");
            pstmt.setString(1, startDateEndDateWithHour[0]);
            pstmt.setString(2, startDateEndDateWithHour[0]);
            pstmt.setString(3, startDateEndDateWithHour[1]);
            // pstmt.setString(3, p_date+p_before_interval/1440);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String data = rs.getString("DATA");
                outputFromSelectQueries.add(data);
                if (_logger.isDebugEnabled()) {
                    _logger.debug("fetchTxnDetailsOnInterval4C2S",
                        "startDate= " + startDateEndDateWithHour[0] + " | endDate= " + startDateEndDateWithHour[1] + " | data:" + data);
                }
                data = null;
            }
            pstmt.clearParameters();
            rs.close();
            pstmt.close();

            // Fetch data from Old table(C2S_TRANSFERS_OLD)
            qrySelect4C2S = null;
            qrySelect4C2S = new StringBuffer(" SELECT (CT.TRANSFER_ID||';'||CT.TRANSFER_STATUS||';'||CT.SERVICE_TYPE||';'||'C2S'||';'||CT.SENDER_ID||';'||CT.SENDER_MSISDN ");
            qrySelect4C2S.append("||';'||U.USER_NAME||';'||CT.SENDER_CATEGORY||';'||'NA'||';'||CT.RECEIVER_MSISDN||';'||'NA' ");
            qrySelect4C2S.append("||';'||'NA'||';'||TO_CHAR(CT.TRANSFER_DATE_TIME,'DD/MM/YYYY HH24:MI:SS')||';'||CT.TRANSFER_VALUE ");
            qrySelect4C2S
                .append("||';'||CTIS.PREVIOUS_BALANCE||';'||CTIS.POST_BALANCE||';'||CT.ERROR_CODE||';'||KV.VALUE||';'||(TO_NUMBER(CT.END_TIME)-TO_NUMBER(CT.START_TIME)) ");
            qrySelect4C2S.append("||';'||CT.bonus_details) DATA  ");
            qrySelect4C2S.append(" FROM C2S_TRANSFERS_OLD CT, C2S_TRANSFER_ITEMS CTIS, C2S_TRANSFER_ITEMS CTIR, USERS U,  KEY_VALUES KV  ");
            qrySelect4C2S.append(" WHERE ");
            qrySelect4C2S.append(" CT.TRANSFER_DATE=trunc(TO_DATE(?,'DD-MM-YYYY HH24'))  ");
            qrySelect4C2S.append(" AND CT.TRANSFER_DATE_TIME >= TO_DATE(?,'DD-MM-YYYY HH24')  ");
            qrySelect4C2S.append(" and CT.TRANSFER_DATE_TIME <= TO_DATE(?,'DD-MM-YYYY HH24') ");
            qrySelect4C2S.append(" AND CT.SENDER_ID=U.USER_ID  ");
            qrySelect4C2S.append(" AND CT.TRANSFER_ID=CTIS.TRANSFER_ID(+) AND CTIS.SNO(+)=1 ");
            qrySelect4C2S.append(" AND CT.TRANSFER_ID=CTIR.TRANSFER_ID(+) AND CTIR.SNO(+)=2  ");
            qrySelect4C2S.append(" AND KV.KEY(+)=CT.ERROR_CODE  ");
            qrySelect4C2S.append(" AND KV.TYPE(+)='C2S_ERR_CD'  ");
            qrySelect4C2S.append(" ORDER BY CT.TRANSFER_DATE_TIME ");
            if (_logger.isDebugEnabled()) {
                _logger.debug("fetchTxnDetailsOnInterval4C2S_OLD", "Select qrySelect4C2S_OLD:" + qrySelect4C2S);
            }

            // Fetching of C2S data
            pstmt = con.prepareStatement(qrySelect4C2S.toString());
            startDateEndDateWithHour = startEndDate.split(";");
            int index = 1;
            pstmt.setString(index++, startDateEndDateWithHour[0]);
            pstmt.setString(index++, startDateEndDateWithHour[0]);
            pstmt.setString(index++, startDateEndDateWithHour[1]);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String data = rs.getString("DATA");
                outputFromSelectQueries.add(data);
                if (_logger.isDebugEnabled()) {
                    _logger.debug("fetchTxnDetailsOnInterval4C2S_OLD",
                        "startDate= " + startDateEndDateWithHour[0] + " | endDate= " + startDateEndDateWithHour[1] + " | data:" + data);
                }
                data = null;
            }
            pstmt.clearParameters();

        } catch (SQLException sql) {
            _logger.error("fetchTxnDetailsOnInterval4C2S", "SQLException:=" + sql.getMessage());
            _logger.errorTrace(METHOD_NAME, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "[fetchTxnDetailsOnInterval4C2S]", "", "", "",
                "SQLException:" + sql.getMessage());

        } catch (Exception e) {
            _logger.error("fetchTxnDetailsOnInterval4C2S", "Exception:=" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "[fetchTxnDetailsOnInterval4C2S]", "", "", "",
                "Exception:" + e.getMessage());

        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("fetchTxnDetailsOnInterval4C2S", "Exit outputFromSelectQueries.size() = " + outputFromSelectQueries.size());
            }
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e1) {
                _logger.errorTrace(METHOD_NAME, e1);
            }
        }
    }

    private static void writeDataInFile(ArrayList<String> outputFromSelectQueries, String p_dirPath, String p_fileName, String p_fileLabel, String p_fileEXT, long p_maxFileLength, String startDate, String endDate, int intervalInInt) throws BTSLBaseException {
        final String METHOD_NAME = "writeDataInFile";
        if (_logger.isDebugEnabled()) {
            _logger
                .debug(
                    "writeDataInFile",
                    " Entered: outputFromSelectQueries = " + outputFromSelectQueries + " p_dirPath=" + p_dirPath + " p_fileName=" + p_fileName + " p_fileLabel=" + p_fileLabel + " p_fileEXT=" + p_fileEXT + " p_maxFileLength=" + p_maxFileLength + " startDate = " + startDate);
        }
        long recordsWrittenInFile = 0;
        PrintWriter out = null;
        int fileNumber = 0;
        String fileName = null;
        File newFile = null;
        String fileData = null;
        String fileHeader = null;
        String fileFooter = null;
        final PreparedStatement selectStmt = null;
        try {
            
            SimpleDateFormat sdf;
			try {
                sdf = new SimpleDateFormat(_csvproperties.getProperty("FILE_SUBSTR_DATE_FORMAT"));
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
                sdf = new SimpleDateFormat("_ddMMyy_hh_");
            }

            final String[] startNumber = startDate.split(" ");
            final int startHour = Integer.parseInt(startNumber[1]);

            String startHourStr = "";
            if (startHour < 10) {
                startHourStr = "0" + startHour;
            } else {
                startHourStr = "" + startHour;
            }

            final String[] lastNumber = endDate.split(" ");
            final int endHour = Integer.parseInt(lastNumber[1]);

            String endHourStr = "";
            if (endHour < 10) {
                endHourStr = "0" + endHour;
            } else {
                endHourStr = "" + endHour;
            }

            /*
             * if(endHour<10){
             * //end hour is zero
             * if(endHour <1){
             * endHour = startHour+ (intervalInInt/60);
             * if(endHour<10)
             * endHourStr="0"+endHour;
             * else
             * endHourStr=""+endHour;
             * 
             * } else {
             * endHourStr="0"+endHour;
             * }
             * }else {
             * endHourStr=""+endHour;
             * }
             */

            final String fileAppend = startHourStr + "_" + endHourStr;

            // generating file name
            fileNumber = 1;
            // if the length of file number is 1, two zeros are added as prefix
            if (Integer.toString(fileNumber).length() == 1) {
                fileName = p_dirPath + File.separator + p_fileName + BTSLUtil.getDateStringFromDate(BTSLUtil.getDateFromDateString(startDate, "dd-MM-yyyy HH"), "ddMMyy_") + fileAppend + p_fileEXT;
            } else if (Integer.toString(fileNumber).length() == 2) {
                fileName = p_dirPath + File.separator + p_fileName + BTSLUtil.getDateStringFromDate(BTSLUtil.getDateFromDateString(startDate, "dd-MM-yyyy HH"), "ddMMyy_") + fileAppend + p_fileEXT;
            } else if (Integer.toString(fileNumber).length() == 3) {
                fileName = p_dirPath + File.separator + p_fileName + BTSLUtil.getDateStringFromDate(BTSLUtil.getDateFromDateString(startDate, "dd-MM-yyyy HH"), "ddMMyy_") + fileAppend + p_fileEXT;
            }
            _logger.debug("writeDataInFile", " ======================================================= ");
            if (_logger.isDebugEnabled()) {
                _logger.debug("writeDataInFile", "  fileName=" + fileName);
            }

            final File directory = new File(p_dirPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            newFile = new File(fileName);
            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            _fileNameLst.add(fileName);

            out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));

            fileHeader = constructFileHeader(fileNumber, p_fileLabel);
            out.write(fileHeader);

            int count = 0;
            final Iterator<String> iterator = outputFromSelectQueries.iterator();
            while (iterator.hasNext()) {
                fileData = iterator.next();
                out.write(fileData + "\n");
                recordsWrittenInFile++;
                count++;
                if (recordsWrittenInFile >= p_maxFileLength) {
                    fileFooter = constructFileFooter(recordsWrittenInFile);
                    out.write(fileFooter);

                    recordsWrittenInFile = 0;
                    fileNumber = fileNumber + 1;
                    out.close();

                    // if the length of file number is 1, two zeros are added as
                    // prefix
                    if (Integer.toString(fileNumber).length() == 1) {
                        fileName = p_dirPath + File.separator + p_fileName + BTSLUtil.getDateStringFromDate(BTSLUtil.getDateFromDateString(startDate, "dd-MM-yyyy HH"),
                            "ddMMyy_") + fileAppend + "_0" + fileNumber + p_fileEXT;
                    } else if (Integer.toString(fileNumber).length() == 2) {
                        fileName = p_dirPath + File.separator + p_fileName + BTSLUtil.getDateStringFromDate(BTSLUtil.getDateFromDateString(startDate, "dd-MM-yyyy HH"),
                            "ddMMyy_") + fileAppend + "_0" + fileNumber + p_fileEXT;
                    } else if (Integer.toString(fileNumber).length() == 3) {
                        fileName = p_dirPath + File.separator + p_fileName + BTSLUtil.getDateStringFromDate(BTSLUtil.getDateFromDateString(startDate, "dd-MM-yyyy HH"),
                            "ddMMyy_") + fileAppend + "_0" + fileNumber + p_fileEXT;
                    }

                    _logger.debug("writeDataInFile", "  fileName=" + fileName);
                    newFile = new File(fileName);
                    _fileNameLst.add(fileName);
                    out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));

                    fileHeader = constructFileHeader(fileNumber, p_fileLabel);
                    out.write(fileHeader);
                }
            }

            // if number of records are not zero then footer is appended as file
            // is deleted
            if (recordsWrittenInFile > 0) {
                fileFooter = constructFileFooter(recordsWrittenInFile);
                out.write(fileFooter);
            } else {
                if (out != null) {
                    out.close();
                    // newFile.delete();
                    // _fileNameLst.remove(_fileNameLst.size()-1);
                }
            }
            if (out != null) {
                out.close();
            }

            _logger.debug("writeDataInFile", " ======================================================= ");
        } catch (ParseException e) {
            // deleteAllFiles();
            _logger.debug("writeDataInFile", "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HourlyC2SDWHProcessOCI[writeDataInFile]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("HourlyC2SDWHProcessOCI", "writeDataInFile", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } catch (IOException e) {
            // deleteAllFiles();
            _logger.debug("writeDataInFile", "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HourlyC2SDWHProcessOCI[writeDataInFile]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("HourlyC2SDWHProcessOCI", "writeDataInFile", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } catch (Exception e) {
            // deleteAllFiles();
            _logger.debug("writeDataInFile", "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HourlyC2SDWHProcessOCI[writeDataInFile]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("HourlyC2SDWHProcessOCI", "writeDataInFile", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (selectStmt != null) {
                try {
                    selectStmt.close();
                } catch (Exception e) {
                    _logger.errorTrace(METHOD_NAME, e);
                }
            }
            if (out != null) {
                out.close();
            }

            if (_logger.isDebugEnabled()) {
                _logger.debug("writeDataInFile", "Exiting ");
            }
        }
    }

    private static String constructFileHeader(long p_fileNumber, String p_fileLabel) {
        /*
         * SimpleDateFormat sdf = null;
         * try
         * {
         * sdf = new
         * SimpleDateFormat(_csvproperties.getProperty("DATE_TIME_FORMAT"));
         * }
         * catch(Exception e)
         * {
         * sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);
         * }
         */

        final StringBuffer fileHeaderBuf = new StringBuffer("");
        // fileHeaderBuf.append("Present Date and Time="+sdf.format(new
        // Date()));
        // fileHeaderBuf.append("\n"+"File Number="+p_fileNumber);
        // fileHeaderBuf.append("\n"+p_fileLabel);
        fileHeaderBuf.append(p_fileLabel + "\n");
        // fileHeaderBuf.append("\n"+"[STARTDATA]"+"\n");

        return fileHeaderBuf.toString();
    }

    private static String constructFileFooter(long p_noOfRecords) {
        StringBuffer fileHeaderBuf ;
        fileHeaderBuf = new StringBuffer("");
        // fileHeaderBuf.append("[ENDDATA]"+"\n");
        // fileHeaderBuf.append("Number of records="+p_noOfRecords);

        return fileHeaderBuf.toString();
    }

    public static void initialize(String p_processIDs) throws BTSLBaseException {
        if (_logger.isDebugEnabled()) {
            _logger.debug("initialize", "Entered p_processIDs::" + p_processIDs);
        }
        String processId = null;
        String[] inStrArray = null;
        final String METHOD_NAME = "initialize";
        try {
            CSVFileVO csvFileVO ;
            inStrArray = p_processIDs.split(",");
            if (BTSLUtil.isNullArray(inStrArray)) {
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS3_NO_INTERFACEIDS);
            }
            for (int i = 0, size = inStrArray.length; i < size; i++) {
                processId = inStrArray[i].trim();
                csvFileVO = new CSVFileVO();
                csvFileVO.setProcessId(processId);
                csvFileVO.setDirName(_csvproperties.getProperty("HOURLY_C2S_DWH_PROCESS_DIR"));
                csvFileVO.setExtName(_csvproperties.getProperty("HOURLY_C2S_DWH_PROCESS_EXT"));
                csvFileVO.setHeaderName(_csvproperties.getProperty("HOURLY_C2S_DWH_PROCESS_HEADER"));
                csvFileVO.setPrefixName(_csvproperties.getProperty("HOURLY_C2S_DWH_PROCESS_PREFIX_NAME"));
                if (_logger.isDebugEnabled()) {
                    _logger.debug("HourlyC2SDWHProcessOCI[initialize]", "csvFileVO::" + csvFileVO);
                }
                _csvMap.put(processId, csvFileVO);
            }
        } catch (BTSLBaseException be) {
            _logger.error("initialize", "BTSLBaseException be:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("initialize", "Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HourlyC2SDWHProcessOCI[initialize]",
                "String of p_processIDs ids=" + p_processIDs, "", "",
                "While initializing the processIDs for the CSV file generator process =" + processId + " get Exception=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS3_NODE_INITIALIZATION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("initialize", "Exited _csvMap::" + _csvMap);
            }
        }
    }

    private static void deleteAllFiles() throws BTSLBaseException {
        if (_logger.isDebugEnabled()) {
            _logger.debug("deleteAllFiles", " Entered: ");
        }

        int size = 0;
        final String METHOD_NAME = "deleteAllFiles";
        if (_fileNameLst != null) {
            size = _fileNameLst.size();
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug("deleteAllFiles", " : Number of files to be deleted " + size);
        }
        String fileName ;
        File newFile ;
        for (int i = 0; i < size; i++) {
            try {
                fileName = _fileNameLst.get(i);
                newFile = new File(fileName);
                newFile.delete();
                if (_logger.isDebugEnabled()) {
                    _logger.debug("", fileName + " file deleted");
                }
            } catch (Exception e) {
                _logger.error("deleteAllFiles", "Exception " + e.getMessage());
                _logger.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HourlyC2SDWHProcessOCI[deleteAllFiles]", "", "",
                    "", "Exception:" + e.getMessage());
                throw new BTSLBaseException("HourlyC2SDWHProcessOCI", "deleteAllFiles", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
            }
        }// end of for loop
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HourlyC2SDWHProcessOCI[deleteAllFiles]", "", "", "",
            " Message: HourlyC2SDWHProcessOCI process has found some error, so deleting all the files.");
        if (_fileNameLst.isEmpty()) {
            _fileNameLst.clear();
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug("deleteAllFiles", " : Exiting.............................");
        }
    }

    private static void loadConstantParameters(String processId) throws BTSLBaseException {
        if (_logger.isDebugEnabled()) {
            _logger.debug("loadParameters", " Entered: ");
        }
        final String METHOD_NAME = "loadConstantParameters";
        try {
            initialize(processId);
            _logger.debug("loadConstantParameters", " Required information successfuly loaded from csvConfigFile.properties...............: ");
        } catch (BTSLBaseException be) {
            _logger.error("loadConstantParameters", "BTSLBaseException : " + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HourlyC2SDWHProcessOCI[loadConstantParameters]", "",
                "", "", "Message:" + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _logger.error("loadConstantParameters", "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            final BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "HourlyC2SDWHProcessOCI[loadConstantParameters]", "",
                "", "", "Message:" + btslMessage);
            throw new BTSLBaseException("HourlyC2SDWHProcessOCI", "loadConstantParameters", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }

    }

    private static String[] getMinutesAndSecond() {
        String[] minuteAndSecond ;
        Calendar calendar1 = BTSLDateUtil.getInstance();
        final int minute = calendar1.get(Calendar.MINUTE);
        final int second = calendar1.get(Calendar.SECOND);
        String minuteStr ;
        String secondStr = null;
        if (minute < 10) {
            minuteStr = "0" + minute;
        } else {
            minuteStr = "" + minute;
        }
        if (second < 10) {
            secondStr = "0" + second;
        } else {
            secondStr = "" + second;
        }

        minuteAndSecond = new String[] { ":", minuteStr, ":", secondStr };
       
        return minuteAndSecond;
    }

    /**
     * @param p_con
     *            Connection
     * @param p_processId
     *            String
     * @return int
     */
    private static int markProcessStatusAsComplete(Connection p_con, String p_processId) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("markProcessStatusAsComplete", " Entered:  p_processId:" + p_processId);
        }
        final String METHOD_NAME = "markProcessStatusAsComplete";
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
            if (_logger.isDebugEnabled()) {
                _logger.debug("markProcessStatusAsComplete", "Exception= " + e.getMessage());
            }
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;

    }
}

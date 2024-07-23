package com.btsl.pretups.processes.clientprocesses;

/**
 * @(#)MonthlyReport4Pos.java
 *                            Copyright(c) 2014, Comviva technologies Ltd.
 *                            All Rights Reserved
 * 
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Diwakar Jan 08 2014 Initial Creation
 *                            This class will be used to prepare the monthly
 *                            wise POS categerization report based on configured
 *                            profiles and it will be configured at cron end
 *                            that should be scheduled to execute on monthly
 *                            wise.
 * 
 */

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.EMailSender;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.processes.businesslogic.MonthlyReport4PosDAO;
import com.btsl.pretups.processes.businesslogic.MonthlyReport4PosVO;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.BTSLUtil4MonthlyPOSReport;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.btsl.xl.ExcelStyle;
import com.ibm.icu.util.Calendar;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class MonthlyReport4Pos {

    private static Log _logger = LogFactory.getLog(MonthlyReport4Pos.class.getName());
    private static Locale _locale = null;
    private static String _finalFileName = null;
    private static Date _reportDate = null;
    private static Date _fromDate = null;

    private int _rowCount = 0;
    private String _networkCode = null;
    private MonthlyReport4PosDAO monthlyReport4PosDAO = null;
    private static ProcessBL _processBL = null;
    private static ProcessStatusVO _processStatusVO;

    public MonthlyReport4Pos() {
        monthlyReport4PosDAO = new MonthlyReport4PosDAO();
    }

    /**
     * @param args
     *            arg[0]=Conatnsts.props
     *            arg[1]=ProcessLogconfig.props
     *            arg[2]=Locale(0 or 1)
     */
    public static void main(String[] arg) {
        final String METHOD_NAME = "main";
        try {
            if (arg.length > 3 || arg.length < 3)// check the argument length
            {
                _logger.info(METHOD_NAME, "MonthlyReport4Pos :: Not sufficient arguments, please pass Conatnsts.props ProcessLogconfig.props Locale Network ReportDate");
                return;
            }
            final File constantsFile = new File(arg[0]);
            if (!constantsFile.exists())// check file (Constants.props) exist or
            // not
            {
                _logger.debug(METHOD_NAME, "MonthlyReport4Pos" + " Constants File Not Found at the path : " + arg[0]);
                return;
            }
            final File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists())// check file (ProcessLogConfig.props)
            // exist or not
            {
                _logger.debug(METHOD_NAME, "MonthlyReport4Pos" + " ProcessLogConfig File Not Found at the path : " + arg[1]);
                return;
            }
            if (BTSLUtil.isNullString(arg[2]))// Locale check
            {
                _logger.info(METHOD_NAME, "MonthlyReport4Pos :: Locale is missing ");
                return;
            }
            if (!BTSLUtil.isNumeric(arg[2]))// check the Process Interval is
            // numeric
            {
                _logger.debug(METHOD_NAME, "MonthlyReport4Pos :: Invalid Locale " + arg[2] + " It should be 0 or 1");
                return;
            }
            if (Integer.parseInt(arg[2]) > 1 && Integer.parseInt(arg[2]) < 0) {
                _logger.debug(METHOD_NAME, "MonthlyReport4Pos :: Invalid Locale " + arg[2] + " It should be 0 or 1");
                return;
            }
            // _reportDate=new Date();
            // _reportDate=BTSLUtil.addDaysInUtilDate(_reportDate,-1);
            // GregorianCalendar gc = new
            // GregorianCalendar(_reportDate.getYear()+1900,_reportDate.getMonth(),1);
            // _fromDate = gc.getTime();

            // use to load the Constants.props and ProcessLogConfig.props files
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            LookupsCache.loadLookAtStartup();
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Main: Error in loading the Cache information.." + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "MonthlyReport4Pos[main]", "", "", "",
                "  Error in loading the Cache information");
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {
            _locale = LocaleMasterCache.getLocaleFromCodeDetails(arg[2]);
            if (_locale == null) {
                _locale = LocaleMasterCache.getLocaleFromCodeDetails("0");
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, "Error : Invalid Locale " + arg[2] + " It should be 0 or 1, thus using default locale code 0");
                }
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "MonthlyReport4Pos[main]", "", "", "",
                    "  Message:  Invalid Locale " + arg[2] + " It should be 0 (EN) or 1 (OTH) ");
            }
        } catch (Exception e) {
            _logger.error(METHOD_NAME, " Invalid locale : " + arg[5] + " Exception:" + e.getMessage());
            _locale = new Locale("en", "US");
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "MonthlyReport4Pos[main]", "", "", "",
                "  Message:  Not able to get the locale");
        }
        String processId = null;
        boolean statusOk = false;
        Connection conProcessStatus = null;
        try {
            processId = ProcessI.POS_MIS;
            // Make Connection
            conProcessStatus = OracleUtil.getSingleConnection();
            if (conProcessStatus == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("MonthlyReport4Pos[main]", "Not able to get Connection for MonthlyReport4Pos: ");
                }
                throw new SQLException();
            }
            // method call to check status of the process
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(conProcessStatus, processId);
            statusOk = _processStatusVO.isStatusOkBool();
            if (statusOk) {
                conProcessStatus.commit();
            }

            // Calculate Previous Month Date
            calculatePreviousMonthDate();

            final String filePath = Constants.getProperty("MONTHLY_POS_REPORT_FILE_PATH");
            if (!BTSLUtil.isNullString(filePath)) {
                final File fileDir = new File(filePath);
                if (!fileDir.isDirectory()) {
                    fileDir.mkdirs();
                }

                final String monthlyPosReportPrefix = Constants.getProperty("MONTHLY_POS_REPORT_FILE_PRIFIX");
                final String monthlyPosReportDateFormat = Constants.getProperty("MONTHLY_POS_REPORT_FILE_DATE_FORMAT");

                if (!BTSLUtil.isNullString(monthlyPosReportPrefix) && !BTSLUtil.isNullString(monthlyPosReportDateFormat)) {
                    final String fileName = monthlyPosReportPrefix + BTSLUtil4MonthlyPOSReport.getFileNameStringFromDate(new Date(), monthlyPosReportDateFormat) + ".xls";
                    _finalFileName = filePath + fileName;

                    final MonthlyReport4Pos monthlyReport4Pos = new MonthlyReport4Pos();
                    monthlyReport4Pos.writeExcel(_finalFileName, fileName);
                    _logger.info("MonthlyReport4Pos", "main : Monthly Report based on POS categerization is being generated on the path = " + _finalFileName);
                } else if (BTSLUtil.isNullString(monthlyPosReportPrefix)) {
                    _logger.error("MonthlyReport4Pos", "main : Please provide the value for key <MONTHLY_POS_REPORT_FILE_PRIFIX> into the constants.props. ");
                } else if (BTSLUtil.isNullString(monthlyPosReportDateFormat)) {
                    _logger.error("MonthlyReport4Pos", "main : Please provide the value for key <MONTHLY_POS_REPORT_FILE_DATE_FORMAT> into the constants.props. ");
                }
            } else {
                _logger.error("MonthlyReport4Pos", "main : Please provide the value for key <MONTHLY_POS_REPORT_FILE_PATH> into the constants.props. ");
            }
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException :" + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            // event handle
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MonthlyReport4Pos[main]", "", "", "",
                "BTSLBaseException:" + be.getMessage());
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception :" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            // event handle
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MonthlyReport4Pos[main]", "", "", "", "Exception:" + e
                .getMessage());
        } finally {
            try {
                if (statusOk) {
                    if (markProcessStatusAsComplete(conProcessStatus, processId) == 1) {
                        try {
                            conProcessStatus.commit();
                        } catch (Exception e) {
                            _logger.errorTrace(METHOD_NAME, e);
                        }
                    } else {
                        try {
                            conProcessStatus.rollback();
                        } catch (Exception e) {
                            _logger.errorTrace(METHOD_NAME, e);
                        }
                    }
                }

            } catch (Exception ex) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("MonthlyReport4Pos", "Exception while closing statement in MonthlyReport4Pos[MonthlyReport4Pos] method ");
                }
                _logger.errorTrace(METHOD_NAME, ex);
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * @descriptions : This method will be used to calculate the previous month
     *               duration from the current time.
     * @author : diwakar
     */
    private static void calculatePreviousMonthDate() {
        final String METHOD_NAME = "calculatePreviousMonthDate";
        try {
            final Calendar cal = BTSLDateUtil.getInstance();
            cal.add(Calendar.MONTH, -1);
            cal.set(Calendar.DATE, 1);
            _fromDate = cal.getTime();
            if (_logger.isDebugEnabled()) {
                _logger.debug("calculatePreviousMonthDate", " firstDateOfPreviousMonth: " + _fromDate);
            }

            cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
            _reportDate = cal.getTime();
            if (_logger.isDebugEnabled()) {
                _logger.debug("calculatePreviousMonthDate", "Info : firstDateOfPreviousMonth =  " + _fromDate + "  | lastDateOfPreviousMonth = " + _reportDate);
            }
        } catch (RuntimeException e) {
            _logger.error("calculatePreviousMonthDate", "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw e;
        }

    }

    /**
     * @descriptions : This method will be used to write the data for O2C,C2C &
     *               C2S transaction into Excel sheets.
     * @param p_fileName
     * @param p_file
     * @throws BTSLBaseException
     */
    public void writeExcel(String p_fileName, String p_file) throws BTSLBaseException {
        final String METHOD_NAME = "writeExcel";
        if (_logger.isDebugEnabled()) {
            _logger.debug("writeExcel", " p_fileName: " + p_fileName + " p_file=" + p_file);
        }

        WritableWorkbook workbook = null;
        WritableSheet worksheet1 = null;
        File fileName = null;
        Connection conDatafatch = null;
        try {
            conDatafatch = OracleUtil.getReportDBSingleConnection();
            if (conDatafatch == null) {
                _logger.error("writeExcel", " DATABASE Connection is NULL for Reporting - ReportDBSingleConnection");
                throw new BTSLBaseException(this, "sendSMS", "Not able to get the connection");

            }

            final ArrayList<MonthlyReport4PosVO> posList4O2c = monthlyReport4PosDAO.fetchPosDetailsBasedOnUserProfile(conDatafatch, _fromDate, _reportDate,
                PretupsI.O2C_MODULE);
            if (_logger.isDebugEnabled()) {
                _logger.debug("writeExcel", "posList4O2c.size()=" + posList4O2c);
            }

            // validate the messages configured into properties files
            final String[] messageKeys = { "monthlyReport4Pos.o2c.sheet.name", "monthlyReport4Pos.o2c.report.name", "monthlyReport4Pos.o2c.header.name", "monthlyReport4Pos.o2c.date.label", "monthlyReport4Pos.o2c.generatedon.label", "monthlyReport4Pos.o2c.eventmonth.label", "monthlyReport4Pos.o2c.region.label", "monthlyReport4Pos.o2c.area.label", "monthlyReport4Pos.o2c.retailerName.label", "monthlyReport4Pos.o2c.posmsisdn.label", "monthlyReport4Pos.o2c.activedays.label", "monthlyReport4Pos.o2c.amount.label", "monthlyReport4Pos.o2c.count.label", "monthlyReport4Pos.o2c.dailyavgtxnamount.label", "monthlyReport4Pos.o2c.dailyavgtxnacount.label", "monthlyReport4Pos.o2c.classtype.label", "monthlyReport4Pos.c2c.sheet.name", "monthlyReport4Pos.c2c.report.name", "monthlyReport4Pos.c2c.header.name", "monthlyReport4Pos.c2c.date.label", "monthlyReport4Pos.c2c.generatedon.label", "monthlyReport4Pos.c2c.eventmonth.label", "monthlyReport4Pos.c2c.region.label", "monthlyReport4Pos.c2c.area.label", "monthlyReport4Pos.c2c.retailerName.label", "monthlyReport4Pos.c2c.posmsisdn.label", "monthlyReport4Pos.c2c.activedays.label", "monthlyReport4Pos.c2c.amount.label", "monthlyReport4Pos.c2c.count.label", "monthlyReport4Pos.c2c.dailyavgtxnamount.label", "monthlyReport4Pos.c2c.dailyavgtxnacount.label", "monthlyReport4Pos.c2c.classtype.label", "monthlyReport4Pos.c2s.sheet.name", "monthlyReport4Pos.c2s.report.name", "monthlyReport4Pos.c2s.header.name", "monthlyReport4Pos.c2s.date.label", "monthlyReport4Pos.c2s.generatedon.label", "monthlyReport4Pos.c2s.eventmonth.label", "monthlyReport4Pos.c2s.region.label", "monthlyReport4Pos.c2s.area.label", "monthlyReport4Pos.c2s.retailerName.label", "monthlyReport4Pos.c2s.posmsisdn.label", "monthlyReport4Pos.c2s.activedays.label", "monthlyReport4Pos.c2s.amount.label", "monthlyReport4Pos.c2s.count.label", "monthlyReport4Pos.c2s.dailyavgtxnamount.label", "monthlyReport4Pos.c2s.dailyavgtxnacount.label", "monthlyReport4Pos.c2s.classtype.label" };

            if (isManadatoryMessageKeyConfigured(messageKeys)) {

                // Create a xls file
                fileName = new File(p_fileName);

                // create a workbook with name
                workbook = Workbook.createWorkbook(fileName);
                String repHeader = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.o2c.sheet.name", null);
                worksheet1 = workbook.createSheet(repHeader, 0);
                repHeader = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.o2c.report.name", null);
                // write into O2S sheet
                _rowCount = writeExcelHeader(worksheet1, _reportDate, repHeader, PretupsI.O2C_MODULE);
                prepareAndWriteDetailsIntoExcel(posList4O2c, worksheet1);

                final ArrayList<MonthlyReport4PosVO> posList4C2c = monthlyReport4PosDAO.fetchPosDetailsBasedOnUserProfile(conDatafatch, _fromDate, _reportDate,
                    PretupsI.C2C_MODULE);
                // ArrayList<MonthlyReport4PosVO> posList4C2c = posList4O2c;
                repHeader = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.c2c.sheet.name", null);
                worksheet1 = workbook.createSheet(repHeader, 1);
                repHeader = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.c2c.report.name", null);
                // write into C2S sheet
                _rowCount = writeExcelHeader(worksheet1, _reportDate, repHeader, PretupsI.C2C_MODULE);
                prepareAndWriteDetailsIntoExcel(posList4C2c, worksheet1);

                final ArrayList<MonthlyReport4PosVO> posList4C2s = monthlyReport4PosDAO.fetchPosDetailsBasedOnUserProfile(conDatafatch, _fromDate, _reportDate,
                    PretupsI.C2S_MODULE);
                // ArrayList<MonthlyReport4PosVO> posList4C2s = posList4O2c;
                repHeader = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.c2s.sheet.name", null);
                worksheet1 = workbook.createSheet(repHeader, 2);
                repHeader = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.c2s.report.name", null);
                // write into C2S sheet
                _rowCount = writeExcelHeader(worksheet1, _reportDate, repHeader, PretupsI.C2S_MODULE);
                prepareAndWriteDetailsIntoExcel(posList4C2s, worksheet1);

                workbook.write();
                if (workbook != null) {
                    workbook.close();
                }

                if (PretupsI.YES.equalsIgnoreCase(Constants.getProperty("MONTHLY_POS_MAIL_SEND"))) {
                    String to = Constants.getProperty("MONTHLY_POS_REPORT_SUMMARY_MAIL_" + _networkCode);
                    if (BTSLUtil.isNullString(to)) {
                        to = Constants.getProperty("MONTHLY_POS_REPORT_SUMMARY_MAIL_DEFAULT");
                    }
                    final String from = Constants.getProperty("MONTHLY_POS_REPORT_SUMMARY_MAIL_FROM");
                    final String subject = Constants.getProperty("MONTHLY_POS_REPORT_SUMMARY_MAIL_SUBJECT");
                    final String bcc = Constants.getProperty("MONTHLY_POS_REPORT_SUMMARY_MAIL_BCC");
                    final String cc = Constants.getProperty("MONTHLY_POS_REPORT_SUMMARY_MAIL_CC");
                    final String msg = Constants.getProperty("MONTHLY_POS_REPORT_SUMMARY_MAIL_MESSAGE");

                    // Send mail
                    EMailSender.sendMail(to, from, bcc, cc, subject, msg, true, _finalFileName, p_file);
                }
            } else {
                _logger.error("writeExcel", "Please configure the values into the Message.properties .");
            }
        } catch (BTSLBaseException be) {
            if (fileName != null) {
                fileName.delete();
            }
            _logger.errorTrace(METHOD_NAME, be);
            _logger.error("writeExcel", " BTSLBaseException e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("writeExcel", " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, "writeExcel", "Exception=" + e.getMessage());
        } finally {
            try {
                if (conDatafatch != null) {
                    conDatafatch.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            worksheet1 = null;
            workbook = null;
            if (_logger.isDebugEnabled()) {
                _logger.debug("writeExcel", " Exiting");
            }
        }
    }

    /**
     * @descriptions : This method will be used to prepare & write the data for
     *               O2C,C2C & C2S transaction into specific Excel sheet.
     * @author : diwakar
     * @param : ArrayList<MonthlyReport4PosVO> - monthly data for each
     *        transaction (O2C,C2C & C2S)
     * @param : WritableSheet - writable excel sheet
     */
    private void prepareAndWriteDetailsIntoExcel(ArrayList<MonthlyReport4PosVO> posList, WritableSheet p_worksheet) {
        final String METHOD_NAME = "prepareAndWriteDetailsIntoExcel";
        try {
            if (posList != null) {
                MonthlyReport4PosVO monthlyReport4PosVO = null;
                int col = 0;
                Label label;
                Number number;
                int posListSizes=posList.size();
                for (int i = 0; i < posListSizes; i++) {
                    try {
                        monthlyReport4PosVO = (MonthlyReport4PosVO) posList.get(i);
                        label = new Label(col++, _rowCount, monthlyReport4PosVO.get_eventMonth(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(label);
                        label = new Label(col++, _rowCount, monthlyReport4PosVO.get_resion(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(label);
                        label = new Label(col++, _rowCount, monthlyReport4PosVO.get_area(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(label);
                        label = new Label(col++, _rowCount, monthlyReport4PosVO.get_retailerName(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(label);
                        number = new Number(col++, _rowCount, monthlyReport4PosVO.get_posMsisdn(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(number);
                        number = new Number(col++, _rowCount, monthlyReport4PosVO.get_activeDays(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(number);
                        number = new Number(col++, _rowCount, monthlyReport4PosVO.get_amount(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(number);
                        number = new Number(col++, _rowCount, monthlyReport4PosVO.get_count(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(number);
                        number = new Number(col++, _rowCount, monthlyReport4PosVO.get_dailyAvgTxnAmount(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(number);
                        number = new Number(col++, _rowCount, monthlyReport4PosVO.get_dailyAvgTxnCount(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(number);
                        label = new Label(col++, _rowCount, monthlyReport4PosVO.get_classType(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(label);
                    } catch (RuntimeException e) {
                        // TODO Auto-generated catch block
                        _logger.errorTrace(METHOD_NAME, e);
                    }
                    _rowCount++;
                    col = 0;
                }
                _rowCount = 0;
            }
        } catch (RowsExceededException e) {
            // TODO Auto-generated catch block
            _logger.errorTrace(METHOD_NAME, e);
        } catch (WriteException e) {
            // TODO Auto-generated catch block
            _logger.errorTrace(METHOD_NAME, e);
        } catch (BTSLBaseException e) {
            // TODO Auto-generated catch block
            _logger.errorTrace(METHOD_NAME, e);
        }

    }

    /**
     * @descriptions : This method will be used to write the template format of
     *               excel sheet to each transaction (O2C,C2C & C2S).
     * @author : diwakar
     * @param : WritableSheet - writable excel sheet
     * @param : Date - todays date & time that will be written into excel sheet
     * @param : String - header content
     * @param : String - type of transaction report
     * @return int - no of rows written into sheet
     */

    private int writeExcelHeader(WritableSheet p_worksheet1, Date p_reportDate, String p_Header, String p_reportType) throws BTSLBaseException {
        final String METHOD_NAME = "writeExcelHeader";
        if (_logger.isDebugEnabled()) {
            _logger.debug("writeExcelHeader", " p_reportDate:" + p_reportDate + " p_Header=" + p_Header);
        }

        int col = 0;
        try {
            p_worksheet1.setColumnView(0, 27);
            p_worksheet1.setColumnView(1, 27);
            p_worksheet1.setColumnView(2, 27);
            p_worksheet1.setColumnView(3, 27);
            p_worksheet1.setColumnView(4, 27);
            p_worksheet1.setColumnView(5, 16);
            p_worksheet1.setColumnView(6, 16);
            p_worksheet1.setColumnView(7, 16);
            p_worksheet1.setColumnView(8, 30);
            p_worksheet1.setColumnView(9, 30);
            p_worksheet1.setColumnView(10, 16);
            p_worksheet1.setColumnView(11, 16);

            final Date currentDate = new Date();
            String keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.o2c.header.name", null);
            Label label = new Label(col, _rowCount, keyName, ExcelStyle.getTopHeadingFont());
            p_worksheet1.mergeCells(col, _rowCount, col + 10, _rowCount);
            p_worksheet1.addCell(label);

            col = 0;
            _rowCount++;
            label = new Label(col, _rowCount, p_Header, ExcelStyle.getSecondTopHeadingFont2());
            p_worksheet1.mergeCells(col, _rowCount, col + 10, _rowCount);
            p_worksheet1.addCell(label);

            col = 0;
            _rowCount++;
            keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.o2c.date.label", null) + BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(p_reportDate));
            label = new Label(col, _rowCount, keyName, ExcelStyle.getSecondTopHeadingFont());
            p_worksheet1.mergeCells(col, _rowCount, ++col + 6, _rowCount);
            p_worksheet1.addCell(label);

            col = col + 6;
            label = new Label(++col, _rowCount, "", ExcelStyle.getSecondTopHeadingFont());
            p_worksheet1.addCell(label);

            keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.o2c.generatedon.label", null) + BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(currentDate));
            label = new Label(++col, _rowCount, keyName, ExcelStyle.getSecondTopHeadingFont());
            p_worksheet1.mergeCells(col, _rowCount, ++col, _rowCount);
            p_worksheet1.addCell(label);

            col = 0;
            _rowCount++;

            label = new Label(col, _rowCount, "", ExcelStyle.getSecondTopHeadingFont());
            p_worksheet1.mergeCells(col, _rowCount, col + 10, _rowCount);
            p_worksheet1.addCell(label);

            col = 0;
            _rowCount++;
            _rowCount++;
            if (PretupsI.O2C_MODULE.equals(p_reportType)) {
                writeLabel4O2C(p_worksheet1);
            } else if (PretupsI.C2C_MODULE.equals(p_reportType)) {
                writeLabel4C2C(p_worksheet1);
            } else if (PretupsI.C2S_MODULE.equals(p_reportType)) {
                writeLabel4C2S(p_worksheet1);
            }

            _rowCount++;
        } catch (BTSLBaseException be) {
            _logger.errorTrace(METHOD_NAME, be);
            _logger.error("writeExcelHeader", " BTSLBaseException e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("writeExcelHeader", " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, "writeExcelHeader", "Exception=" + e.getMessage());
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug("writeExcelHeader", " Exiting _rowCount:" + _rowCount);
        }
        return _rowCount;
    }

    /**
     * @descriptions : This method will be used to write the label into template
     *               format of excel sheet to O2C transaction.
     * @author : diwakar
     * @param : WritableSheet - writable excel sheet
     */
    private void writeLabel4O2C(WritableSheet p_worksheet1) throws BTSLBaseException, RowsExceededException, WriteException {
        int col = 0;
        String keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.o2c.eventmonth.label", null);
        Label label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.o2c.region.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.o2c.area.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.o2c.retailerName.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.o2c.posmsisdn.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.o2c.activedays.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.o2c.amount.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.o2c.count.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.o2c.dailyavgtxnamount.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.o2c.dailyavgtxnacount.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.o2c.classtype.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);
    }

    /**
     * @descriptions : This method will be used to write the label into template
     *               format of excel sheet to C2C transaction.
     * @author : diwakar
     * @param : WritableSheet - writable excel sheet
     */
    private void writeLabel4C2C(WritableSheet p_worksheet1) throws BTSLBaseException, RowsExceededException, WriteException {
        int col = 0;
        String keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.c2c.eventmonth.label", null);
        Label label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.c2c.region.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.c2c.area.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.c2c.retailerName.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.c2c.posmsisdn.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.c2c.activedays.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.c2c.amount.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.c2c.count.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.c2c.dailyavgtxnamount.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.c2c.dailyavgtxnacount.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.c2c.classtype.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);
    }

    /**
     * @descriptions : This method will be used to write the label into template
     *               format of excel sheet to C2S transaction.
     * @author : diwakar
     * @param : WritableSheet - writable excel sheet
     */
    private void writeLabel4C2S(WritableSheet p_worksheet1) throws BTSLBaseException, RowsExceededException, WriteException {
        int col = 0;
        String keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.c2s.eventmonth.label", null);
        Label label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.c2s.region.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.c2s.area.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.c2s.retailerName.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.c2s.posmsisdn.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.c2s.activedays.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.c2s.amount.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.c2s.count.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.c2s.dailyavgtxnamount.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.c2s.dailyavgtxnacount.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);

        keyName = BTSLUtil.getMessage(_locale, "monthlyReport4Pos.c2s.classtype.label", null);
        label = new Label(col++, _rowCount, keyName, ExcelStyle.getHeadingFont());
        p_worksheet1.addCell(label);
    }

    /**
     * @descriptions : This method will be used to update the process.
     * @author : diwakar
     * @param : Connection - connection with database
     * @param : String - The process Id that need to update
     */
    private static int markProcessStatusAsComplete(Connection p_con, String p_processId) {
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
            updateCount = processStatusDAO.updateProcessDetailForMis(p_con, _processStatusVO);
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

    /**
     * @descriptions : This method will be used to validate the message key from
     *               the properties file.
     * @author : diwakar
     */
    private boolean isManadatoryMessageKeyConfigured(String[] messageKeys) {
        final String METHOD_NAME = "isManadatoryMessageKeyConfigured";
        boolean isReturn = true;
        final ArrayList<String> arrayList = new ArrayList<String>();
        for (int index = 0; index < messageKeys.length; index++) {
            final String key = messageKeys[index];
            try {
                if (BTSLUtil.isNullString(BTSLUtil.getMessage(_locale, key, null))) {
                    _logger.error("isManadatoryMessageKeyConfigured", "Please provide the value for key <" + key + "> into the Message.properties.");
                    isReturn = false;
                    return isReturn;
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
                _logger.error("isManadatoryMessageKeyConfigured", "Please provide the value for key <" + key + "> into the Message.properties.");
                isReturn = false;
                return isReturn;
            }
        }
        return isReturn;
    }
}

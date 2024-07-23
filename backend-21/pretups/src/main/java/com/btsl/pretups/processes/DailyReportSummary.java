package com.btsl.pretups.processes;

import java.io.File;
import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.EMailSender;
import com.btsl.common.ListValueVO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.DailyReportAnalysisDAO;
import com.btsl.pretups.processes.businesslogic.DailyReportVO;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
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

/**
 * @(#)DailyReportSummary.java
 *                             Copyright(c) 2006, Bharti Telesoft Ltd.
 *                             All Rights Reserved
 * 
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Gurjeet Bedi Oct 9, 2006 Initial Creation
 * 
 */

public class DailyReportSummary {

    private static final Log _logger = LogFactory.getLog(DailyReportSummary.class.getName());
    private static Locale _locale = null;
    private static String _finalFileName = null;
    private static Date _reportDate = null;
    private static Date _fromDate = null;

    private int _rowCount = 0;
    private String _networkCode = null;
    private ArrayList _c2sProductList = null;
    private ArrayList _c2sServicesList = null;
    private ArrayList _p2pServicesList = null;
    private ArrayList _p2pProductList = null;
    private boolean _c2sDataRequired = false;
    private boolean _p2pDataRequired = false;
    private boolean _c2sProductsAvailable = false;
    private DailyReportAnalysisDAO dailyReportAnalysisDAO = null;

    public DailyReportSummary() {
        dailyReportAnalysisDAO = new DailyReportAnalysisDAO();
    }

    /**
     * @param args
     *            arg[0]=Conatnsts.props
     *            arg[1]=ProcessLogconfig.props
     *            arg[2]=Locale(0 or 1)
     *            arg[3]=Network (ALL or MO or DL...)
     *            arg[4]=report Date
     */
    public static void main(String[] arg) {
        final String METHOD_NAME = "main";
        try {
            if (arg.length > 5 || arg.length < 4)// check the argument length
            {
                _logger.info(METHOD_NAME, "DailyReportSummary :: Not sufficient arguments, please pass Conatnsts.props ProcessLogconfig.props Locale Network ReportDate");
                return;
            }
            final File constantsFile = Constants.validateFilePath(arg[0]);
            if (!constantsFile.exists())// check file (Constants.props) exist or
            // not
            {
                _logger.debug(METHOD_NAME, "DailyReportSummary" + " Constants File Not Found at the provided path.");
                return;
            }
            final File logconfigFile = Constants.validateFilePath(arg[1]);
            if (!logconfigFile.exists())// check file (ProcessLogConfig.props)
            // exist or not
            {
                _logger.debug(METHOD_NAME, "DailyReportSummary" + " ProcessLogConfig File Not Found at the provided path.");
                return;
            }
            if (BTSLUtil.isNullString(arg[2]))// Locale check
            {
                _logger.info(METHOD_NAME, "DailyReportSummary :: Locale is missing ");
                return;
            }
            if (!BTSLUtil.isNumeric(arg[2]))// check the Process Interval is
            // numeric
            {
                _logger.debug(METHOD_NAME, "DailyReportSummary :: Invalid Locale " + arg[2] + " It should be 0 or 1");
                return;
            }
            if (Integer.parseInt(arg[2]) > 1 && Integer.parseInt(arg[2]) < 0) {
                _logger.debug(METHOD_NAME, "DailyReportSummary :: Invalid Locale " + arg[2] + " It should be 0 or 1");
                return;
            }
            if (BTSLUtil.isNullString(arg[3]))// Network code check
            {
                _logger.info(METHOD_NAME, "DailyReportSummary :: Network code is missing ");
                return;
            }
            if (arg.length == 5 && !BTSLUtil.isNullString(arg[4])) {
                try {
                    _reportDate = BTSLUtil.getDateFromDateString(arg[4], PretupsI.DATE_FORMAT);
                } catch (ParseException e1) {
                    _logger.info(METHOD_NAME, "DailyReportAnalysis :: Report date format should be dd/MM/yy");
                    _logger.errorTrace(METHOD_NAME, e1);
                    return;
                }
            } else {
                _reportDate = new Date();
            }
            _reportDate = BTSLUtil.addDaysInUtilDate(_reportDate, -1);
            final Calendar g = BTSLDateUtil.getCalendar(_reportDate.getYear() + 1900, _reportDate.getMonth(), 1);
            _fromDate = g.getTime();
            // use to load the Constants.props and ProcessLogConfig.props files
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            LookupsCache.loadLookAtStartup();
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Main: Error in loading the Cache information.." + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DailyReportSummary[main]", "", "", "",
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
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "DailyReportSummary[main]", "", "", "",
                    "  Message:  Invalid Locale " + arg[2] + " It should be 0 (EN) or 1 (OTH) ");
            }
        } catch (Exception e) {
            _logger.error(METHOD_NAME, " Invalid locale : " + arg[5] + " Exception:" + e.getMessage());
            _locale = new Locale("en", "US");
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "DailyReportSummary[main]", "", "", "",
                "  Message:  Not able to get the locale");
        }
        try {
            final String filePath = Constants.getProperty("DAILY_REPORT_FILE_PATH");
            final File fileDir = new File(filePath);
            if (!fileDir.isDirectory()) {
                fileDir.mkdirs();
            }
            final String fileName = Constants.getProperty("DAILY_REPORT_SUMMARY_PREFIX") + BTSLUtil.getFileNameStringFromDate(new Date()) + ".xls";
            _finalFileName = filePath + fileName;

            final DailyReportSummary dailyReportSummary = new DailyReportSummary();
            dailyReportSummary._networkCode = arg[3];
            dailyReportSummary.writeExcel(_finalFileName, fileName);
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException :" + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            // event handle
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportSummary[main]", "", "", "",
                "BTSLBaseException:" + be.getMessage());
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception :" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            // event handle
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportSummary[main]", "", "", "",
                "Exception:" + e.getMessage());
        } finally {
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * Main Method to write Excel and all
     * 
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
        Connection con = null;
        String c2sMisDoneDate = null;
        String p2pMisDoneDate = null;
        try {
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                _logger.error("writeExcel", " DATABASE Connection is NULL ");
                throw new BTSLBaseException(this, "writeExcel", "Not able to get the connection");
            }

            final ArrayList networkList = dailyReportAnalysisDAO.loadNetworkList(con);
            String passedNetworkName = null;
            if (!PretupsI.ALL.equalsIgnoreCase(_networkCode)) {
                NetworkVO networkVO = null;
                boolean isNetworkFound = false;
                for (int i = 0, j = networkList.size(); i < j; i++)// for
                // multile
                // network
                {
                    networkVO = (NetworkVO) networkList.get(i);
                    if (networkVO.getNetworkCode().equals(_networkCode)) {
                        isNetworkFound = true;
                        passedNetworkName = networkVO.getNetworkName();
                        break;
                    }
                }
                if (!isNetworkFound) {
                    _logger.error("writeExcel", " Not a valid Network Code=" + _networkCode);
                    throw new BTSLBaseException(this, "writeExcel", "Not a valid Network Code=" + _networkCode);
                }
            }
            _c2sProductList = dailyReportAnalysisDAO.loadProductListByModuleCode(con, PretupsI.C2S_MODULE);
            _p2pProductList = dailyReportAnalysisDAO.loadProductListByModuleCode(con, PretupsI.P2P_MODULE);
            _c2sServicesList = dailyReportAnalysisDAO.loadServiceTypeList(con, PretupsI.YES, PretupsI.C2S_MODULE);
            _p2pServicesList = dailyReportAnalysisDAO.loadServiceTypeList(con, PretupsI.YES, PretupsI.P2P_MODULE);

            fileName = new File(p_fileName);
            workbook = Workbook.createWorkbook(fileName);
            String repHeader = BTSLUtil.getMessage(_locale, "dailyreport.summary.sheet.name", null);
            worksheet1 = workbook.createSheet(repHeader, 0);

            repHeader = BTSLUtil.getMessage(_locale, "dailyreport.summary.report.name", null);
            final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();

            ProcessStatusVO processStatusVO = processStatusDAO.loadProcessDetail(con, ProcessI.C2SMIS);
            if (processStatusVO != null) {
                c2sMisDoneDate = BTSLUtil.getDateStringFromDate(processStatusVO.getExecutedUpto());
            } else {
                c2sMisDoneDate = "N.A";
            }
            processStatusVO = null;
            processStatusVO = processStatusDAO.loadProcessDetail(con, ProcessI.P2PMIS);
            if (processStatusVO != null) {
                p2pMisDoneDate = BTSLUtil.getDateStringFromDate(processStatusVO.getExecutedUpto());
            } else {
                p2pMisDoneDate = "N.A";
            }

            // write into C2S sheet
            _rowCount = writeExcelHeader(worksheet1, _reportDate, repHeader, c2sMisDoneDate, p2pMisDoneDate);
            if (PretupsI.ALL.equalsIgnoreCase(_networkCode)) {
                NetworkVO networkVO = null;
                for (int i = 0, j = networkList.size(); i < j; i++)// for
                // multile
                // network
                {
                    networkVO = (NetworkVO) networkList.get(i);
                    _rowCount = writeExcelForNetwork(con, worksheet1, _rowCount, networkVO.getNetworkCode(), networkVO.getNetworkName(), _reportDate);
                    _rowCount++;
                }
            } else {
                _rowCount = writeExcelForNetwork(con, worksheet1, _rowCount, _networkCode.toUpperCase(), passedNetworkName, _reportDate);
            }

            workbook.write();
            if (workbook != null) {
                workbook.close();
            }

            if (PretupsI.YES.equalsIgnoreCase(Constants.getProperty("DAILY_REPORT_MAIL_SEND"))) {
                String to = Constants.getProperty("DAILY_REPORT_SUMMARY_MAIL_" + _networkCode);
                if (BTSLUtil.isNullString(to)) {
                    to = Constants.getProperty("DAILY_REPORT_SUMMARY_MAIL_DEFAULT");
                }
                final String from = Constants.getProperty("DAILY_REPORT_SUMMARY_MAIL_FROM");
                final String subject = Constants.getProperty("DAILY_REPORT_SUMMARY_MAIL_SUBJECT");
                final String bcc = Constants.getProperty("DAILY_REPORT_SUMMARY_MAIL_BCC");
                final String cc = Constants.getProperty("DAILY_REPORT_SUMMARY_MAIL_CC");
                final String msg = Constants.getProperty("DAILY_REPORT_SUMMARY_MAIL_MESSAGE");
                // Send mail
                EMailSender.sendMail(to, from, bcc, cc, subject, msg, true, _finalFileName, p_file);
                // send the message as SMS
                PushMessage pushMessage = null;
                final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                final BTSLMessages message = new BTSLMessages(PretupsErrorCodesI.PROCESS_ADMIN_MESSAGE);
                final String msisdnString = Constants.getProperty("adminmobile");
                final String[] msisdn = msisdnString.split(",");
                for (int i = 0; i < msisdn.length; i++) {
                    pushMessage = new PushMessage(msisdn[i], message, "", "", locale,"");
                    pushMessage.push();
                }
                
                
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
                if (con != null) {
                    con.close();
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
     * Method to write Information for a particular Network
     * 
     * @param p_con
     * @param p_worksheet1
     * @param p_rowCount
     * @param p_networkCode
     * @param p_networkName
     * @param p_toDate
     * @return
     * @throws BTSLBaseException
     */
    private int writeExcelForNetwork(Connection p_con, WritableSheet p_worksheet1, int p_rowCount, String p_networkCode, String p_networkName, Date p_toDate) throws BTSLBaseException {
        final String METHOD_NAME = "writeExcelForNetwork";
        if (_logger.isDebugEnabled()) {
            _logger.debug("writeExcelForNetwork", " p_rowCount:" + p_rowCount + " p_networkName=" + p_networkName);
        }
        int col = 0;
        try {
            String keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.network.label", null);
            Label label = new Label(col, p_rowCount, keyName, ExcelStyle.getHeadingFont());
            p_worksheet1.mergeCells(col, p_rowCount, col++, p_rowCount);
            p_worksheet1.addCell(label);

            final ArrayList moduleList = LookupsCache.loadLookupDropDown(PretupsI.MODULE_TYPE, true);
            int   moduleListSizes=moduleList.size();
            for (int m = 0, n =moduleListSizes; m < n; m++) {
                final ListValueVO listValueVO = (ListValueVO) moduleList.get(m);
                if (PretupsI.C2S_MODULE.equals(listValueVO.getValue())) {
                    _c2sDataRequired = true;
                    if (_c2sProductList != null && !_c2sProductList.isEmpty()) {
                        keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.products.label", null);
                        label = new Label(col, p_rowCount, keyName, ExcelStyle.getHeadingFont());
                        p_worksheet1.mergeCells(col, p_rowCount, _c2sProductList.size() * 4, p_rowCount);
                        p_worksheet1.addCell(label);

                        col = 0;
                        p_rowCount++;

                        label = new Label(col, p_rowCount, p_networkName, ExcelStyle.getHeadingFont());
                        p_worksheet1.mergeCells(col, p_rowCount, col++, p_rowCount);
                        p_worksheet1.addCell(label);
                        int c2sProductListSizes=_c2sProductList.size();
                        for (int k = 0; k < c2sProductListSizes; k++) {
                            final String[] product = { BTSLUtil.getMessage(_locale, ((DailyReportVO) _c2sProductList.get(k)).getProductCode(), null) };
                            if (BTSLUtil.isNullArray(product)) {
                                product[0] = ((DailyReportVO) _c2sProductList.get(k)).getProductCode();
                            }
                            keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.fortheday", product);
                            label = new Label(col, p_rowCount, keyName, ExcelStyle.getHeadingFont());
                            p_worksheet1.mergeCells(col, p_rowCount, ++col, p_rowCount);
                            p_worksheet1.addCell(label);

                            keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.forthemonth", product);
                            label = new Label(++col, p_rowCount, keyName, ExcelStyle.getHeadingFont());
                            p_worksheet1.mergeCells(col, p_rowCount, ++col, p_rowCount);
                            p_worksheet1.addCell(label);
                            col++;
                        }
                        col = 0;
                        p_rowCount++;

                        keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.channelservices.label", null);
                        label = new Label(col++, p_rowCount, keyName, ExcelStyle.getHeadingFont());
                        p_worksheet1.addCell(label);

                        do {
                            keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.count.label", null);
                            label = new Label(col++, p_rowCount, keyName, ExcelStyle.getHeadingFont());
                            p_worksheet1.addCell(label);

                            keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.value.label", null);
                            label = new Label(col++, p_rowCount, keyName, ExcelStyle.getHeadingFont());
                            p_worksheet1.addCell(label);

                        } while (col < _c2sProductList.size() * 4);

                        col = 0;
                        p_rowCount++;

                        do {
                            label = new Label(col++, p_rowCount, "", ExcelStyle.getDataStyle());
                            p_worksheet1.addCell(label);
                        } while (col <= _c2sProductList.size() * 4);

                        col = 0;
                        p_rowCount++;

                        p_rowCount = writeNetworkServicesC2S(p_con, p_worksheet1, p_networkCode, p_rowCount, p_toDate);

                    } else {
                        keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.products.label", null);
                        label = new Label(col, p_rowCount, keyName, ExcelStyle.getHeadingFont());
                        p_worksheet1.mergeCells(col, p_rowCount, col + 4, p_rowCount);
                        p_worksheet1.addCell(label);

                        col = 0;
                        p_rowCount++;

                        label = new Label(col, p_rowCount, p_networkName, ExcelStyle.getHeadingFont());
                        p_worksheet1.mergeCells(col, p_rowCount, ++col, p_rowCount);
                        p_worksheet1.addCell(label);

                        label = new Label(col, p_rowCount, "dailyreport.summary.products.noproducts", ExcelStyle.getHeadingFont());
                        p_worksheet1.mergeCells(col, p_rowCount, col + 4, p_rowCount);
                        p_worksheet1.addCell(label);

                        col = 0;
                        p_rowCount++;
                        keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.channelservices.label", null);
                        label = new Label(col++, p_rowCount, keyName, ExcelStyle.getHeadingFont());
                        p_worksheet1.addCell(label);

                        do {
                            label = new Label(col++, p_rowCount, "", ExcelStyle.getHeadingFont());
                            p_worksheet1.addCell(label);

                        } while (col <= 4);

                        col = 0;
                        p_rowCount++;

                        do {
                            label = new Label(col++, _rowCount, "", ExcelStyle.getDataStyle());
                            p_worksheet1.addCell(label);
                        } while (col <= 4);

                        col = 0;
                        p_rowCount++;
                    }
                } else {

                    _p2pDataRequired = true;
                    if (_p2pProductList != null && !_p2pProductList.isEmpty()) {
                        col = 0;
                        p_rowCount++;

                        label = new Label(col++, p_rowCount, p_networkName, ExcelStyle.getHeadingFont());
                        p_worksheet1.addCell(label);

                        for (int k = 0; k < _p2pProductList.size(); k++) {
                            final String[] product = { BTSLUtil.getMessage(_locale, ((DailyReportVO) _p2pProductList.get(k)).getProductCode(), null) };
                            if (BTSLUtil.isNullArray(product)) {
                                product[0] = ((DailyReportVO) _p2pProductList.get(k)).getProductCode();
                            }
                            keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.forthedayp2p", product);
                            label = new Label(col, p_rowCount, keyName, ExcelStyle.getHeadingFont());
                            p_worksheet1.mergeCells(col, p_rowCount, ++col, p_rowCount);
                            p_worksheet1.addCell(label);

                            keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.forthemonthp2p", product);
                            label = new Label(++col, p_rowCount, keyName, ExcelStyle.getHeadingFont());
                            p_worksheet1.mergeCells(col, p_rowCount, ++col, p_rowCount);
                            p_worksheet1.addCell(label);
                            col++;
                        }
                        col = 0;
                        p_rowCount++;

                        keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.p2pservices.label", null);
                        label = new Label(col, p_rowCount, keyName, ExcelStyle.getHeadingFont());
                        p_worksheet1.mergeCells(col, p_rowCount, col++, p_rowCount);
                        p_worksheet1.addCell(label);

                        do {
                            keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.p2pcount.label", null);
                            label = new Label(col++, p_rowCount, keyName, ExcelStyle.getHeadingFont());
                            p_worksheet1.addCell(label);

                            keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.p2pvalue.label", null);
                            label = new Label(col++, p_rowCount, keyName, ExcelStyle.getHeadingFont());
                            p_worksheet1.addCell(label);

                        } while (col < _p2pProductList.size() * 4);

                        col = 0;
                        p_rowCount++;

                        p_rowCount = writeNetworkServicesP2P(p_con, p_worksheet1, p_networkCode, p_rowCount, p_toDate);

                    } else {
                        p_rowCount++;
                        col = 0;

                        label = new Label(col++, p_rowCount, p_networkName, ExcelStyle.getHeadingFont());
                        p_worksheet1.addCell(label);

                        do {
                            label = new Label(col++, _rowCount, "", ExcelStyle.getDataStyle());
                            p_worksheet1.addCell(label);
                        } while (col <= 4);

                        col = 0;
                        p_rowCount++;

                        keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.p2pservices.label", null);
                        label = new Label(col, p_rowCount, keyName, ExcelStyle.getHeadingFont());
                        p_worksheet1.mergeCells(col, p_rowCount, col++, p_rowCount);
                        p_worksheet1.addCell(label);

                        label = new Label(col, p_rowCount, "dailyreport.summary.p2pproducts.noproducts", ExcelStyle.getHeadingFont());
                        p_worksheet1.mergeCells(col, p_rowCount, col + 4, p_rowCount);
                        p_worksheet1.addCell(label);

                        col = 0;
                        p_rowCount++;

                        do {
                            label = new Label(col++, p_rowCount, "", ExcelStyle.getHeadingFont());
                            p_worksheet1.addCell(label);

                        } while (col <= 4);

                        col = 0;
                        p_rowCount++;

                        do {
                            label = new Label(col++, _rowCount, "", ExcelStyle.getDataStyle());
                            p_worksheet1.addCell(label);
                        } while (col <= 4);

                        col = 0;
                        p_rowCount++;
                    }
                }
            }
            p_rowCount++;
            p_rowCount = writeSummaryInformation(p_con, p_worksheet1, p_networkCode, p_rowCount, p_toDate);

        } catch (BTSLBaseException be) {
            _logger.errorTrace(METHOD_NAME, be);
            _logger.error("writeExcelForNetwork", " BTSLBaseException e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("writeExcelForNetwork", " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, "writeExcelForNetwork", "Exception=" + e.getMessage());
        }
        return p_rowCount;
    }

    /**
     * Writes Summary info on stock and Active users etc
     * 
     * @param p_con
     * @param p_worksheet1
     * @param p_networkCode
     * @param p_rowCount
     * @param p_toDate
     * @return
     * @throws BTSLBaseException
     */
    private int writeSummaryInformation(Connection p_con, WritableSheet p_worksheet1, String p_networkCode, int p_rowCount, Date p_toDate) throws BTSLBaseException {
        final String METHOD_NAME = "writeSummaryInformation";
        try {
            int col = 0;
            String keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.activeusers.label", null);
            Label label = new Label(col++, p_rowCount, keyName, ExcelStyle.getHeadingFont());
            p_worksheet1.addCell(label);

            keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.forthedayusers.label", null);
            label = new Label(col++, p_rowCount, keyName, ExcelStyle.getHeadingFont());
            p_worksheet1.addCell(label);

            keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.forthemonthusers.label", null);
            label = new Label(col++, p_rowCount, keyName, ExcelStyle.getHeadingFont());
            p_worksheet1.addCell(label);

            p_rowCount++;
            col = 0;

            DailyReportVO countsVO = null;
            Number number = null;
            if (_c2sDataRequired) {
                keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.channelusers.label", null);
                label = new Label(col++, p_rowCount, keyName, ExcelStyle.getDataStyle());
                p_worksheet1.addCell(label);

                countsVO = dailyReportAnalysisDAO.loadChannelActivUserCounts(p_con, p_networkCode, p_toDate, p_toDate);
                number = new Number(col++, p_rowCount, countsVO.getProdCount(), ExcelStyle.getDataStyle());
                p_worksheet1.addCell(number);

                countsVO = dailyReportAnalysisDAO.loadChannelActivUserCounts(p_con, p_networkCode, _fromDate, p_toDate);
                number = new Number(col++, p_rowCount, countsVO.getProdCount(), ExcelStyle.getDataStyle());
                p_worksheet1.addCell(number);

                p_rowCount++;
                col = 0;
            }
            if (_p2pDataRequired) {
                col = 0;
                keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.p2pusers.label", null);
                label = new Label(col++, p_rowCount, keyName, ExcelStyle.getDataStyle());
                // p_worksheet1.mergeCells(col,p_rowCount,++col,p_rowCount);
                p_worksheet1.addCell(label);

                countsVO = dailyReportAnalysisDAO.loadP2PActivUserCounts(p_con, p_networkCode, p_toDate, p_toDate);
                number = new Number(col++, p_rowCount, countsVO.getProdCount(), ExcelStyle.getDataStyle());
                p_worksheet1.addCell(number);

                countsVO = dailyReportAnalysisDAO.loadP2PActivUserCounts(p_con, p_networkCode, _fromDate, p_toDate);
                number = new Number(col++, p_rowCount, countsVO.getProdCount(), ExcelStyle.getDataStyle());
                p_worksheet1.addCell(number);

                p_rowCount++;
                col = 0;
            }

            if (_c2sDataRequired && _c2sProductsAvailable) {
                p_rowCount++;

                label = new Label(col++, p_rowCount, "", ExcelStyle.getDataStyle());
                p_worksheet1.addCell(label);

                keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.summproducts.label", null);
                label = new Label(col, p_rowCount, keyName, ExcelStyle.getHeadingFont());
                p_worksheet1.mergeCells(col, p_rowCount, _c2sProductList.size(), p_rowCount);
                p_worksheet1.addCell(label);
                p_rowCount++;
                col = 0;
                keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.summstock.label", null);
                label = new Label(col++, p_rowCount, keyName, ExcelStyle.getHeadingFont());
                p_worksheet1.addCell(label);

                p_rowCount = writeStockInformation(p_con, p_worksheet1, p_networkCode, p_rowCount, 1);
                p_rowCount = writeStockInformation(p_con, p_worksheet1, p_networkCode, p_rowCount, 2);

                p_rowCount++;
            }
        } catch (BTSLBaseException be) {
            _logger.errorTrace(METHOD_NAME, be);
            _logger.error("writeSummaryInformation", " BTSLBaseException e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("writeSummaryInformation", " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, "writeSummaryInformation", "Exception=" + e.getMessage());
        }
        return p_rowCount;
    }

    /**
     * Method to write Stock Informations of Network And User Balance
     * 
     * @param p_con
     * @param p_worksheet1
     * @param p_networkCode
     * @param p_rowCount
     * @param p_source
     * @return
     * @throws BTSLBaseException
     */
    private int writeStockInformation(Connection p_con, WritableSheet p_worksheet1, String p_networkCode, int p_rowCount, int p_source) throws BTSLBaseException {
        final String METHOD_NAME = "writeStockInformation";
        try {
            DailyReportVO productDailyReportVO = null;
            Label label = null;
            int col = 0;
            String keyName = null;
            if (p_source == 1) {
                col = 1;
                for (int l = 0; l < _c2sProductList.size(); l++) {
                    productDailyReportVO = (DailyReportVO) _c2sProductList.get(l);
                    final String[] product = { BTSLUtil.getMessage(_locale, productDailyReportVO.getProductCode(), null) };
                    if (BTSLUtil.isNullArray(product)) {
                        product[0] = productDailyReportVO.getProductCode();
                    }
                    keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.productCode", product);
                    label = new Label(col++, p_rowCount, keyName, ExcelStyle.getHeadingFont());
                    p_worksheet1.addCell(label);
                }
            }
            col = 0;
            p_rowCount++;
            switch (p_source) {
                case 1:
                    {
                        keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.networkStock.total", null);
                        label = new Label(col++, p_rowCount, keyName, ExcelStyle.getDataStyle());
                        p_worksheet1.addCell(label);
                        break;
                    }
                case 2:
                    {
                        keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.userbalance.total", null);
                        label = new Label(col++, p_rowCount, keyName, ExcelStyle.getDataStyle());
                        p_worksheet1.addCell(label);
                        break;
                    }
            }
            DailyReportVO countsVO = null;
            Number number = null;
            int c2sProductListSizes=_c2sProductList.size();
            for (int l = 0; l < c2sProductListSizes; l++) {
                productDailyReportVO = (DailyReportVO) _c2sProductList.get(l);
                if (p_source == 1) {
                    countsVO = dailyReportAnalysisDAO.loadNetworkStockCount(p_con, p_networkCode, productDailyReportVO.getProductCode());
                } else {
                    countsVO = dailyReportAnalysisDAO.loadTotalUserBalance(p_con, p_networkCode, productDailyReportVO.getProductCode());
                }
                number = new Number(col++, p_rowCount, Double.parseDouble(PretupsBL.getDisplayAmount(countsVO.getProdAmount())), ExcelStyle.getDataStyle());
                p_worksheet1.addCell(number);
            }
        } catch (BTSLBaseException be) {
            _logger.errorTrace(METHOD_NAME, be);
            _logger.error("writeStockInformation", " BTSLBaseException e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("writeStockInformation", " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, "writeStockInformation", "Exception=" + e.getMessage());
        }
        return p_rowCount;
    }

    /**
     * Method to write P2P Services Data for Network
     * 
     * @param p_con
     * @param p_worksheet1
     * @param p_networkCode
     * @param p_rowCount
     * @param p_toDate
     * @return
     * @throws BTSLBaseException
     */
    private int writeNetworkServicesP2P(Connection p_con, WritableSheet p_worksheet1, String p_networkCode, int p_rowCount, Date p_toDate) throws BTSLBaseException {
        final String METHOD_NAME = "writeNetworkServicesP2P";
        int col = 0;
        try {
            final long[] totalCount = new long[_p2pProductList.size() * 4];
            ListValueVO p2pServiceDailyReportVO = null;
            DailyReportVO productDailyReportVO = null;
            DailyReportVO countsVO = null;
            Number number = null;
            Label label = null;
            int arrSize = 0;
            String keyName = null;
            if (_p2pServicesList != null) {
                for (int i = 0; i < _p2pServicesList.size(); i++) {
                    p2pServiceDailyReportVO = (ListValueVO) _p2pServicesList.get(i);
                    col = 0;
                    label = new Label(col++, p_rowCount, p2pServiceDailyReportVO.getLabel(), ExcelStyle.getDataStyle());
                    p_worksheet1.addCell(label);

                    for (int l = 0; l < _p2pProductList.size(); l++) {
                        productDailyReportVO = (DailyReportVO) _p2pProductList.get(l);
                        countsVO = null;
                        countsVO = dailyReportAnalysisDAO.loadCountsForP2PServices(p_con, p_networkCode, productDailyReportVO.getProductCode(), p2pServiceDailyReportVO
                            .getValue(), p_toDate, p_toDate);
                        number = new Number(col++, p_rowCount, countsVO.getProdCount(), ExcelStyle.getDataStyle());
                        p_worksheet1.addCell(number);
                        totalCount[arrSize] = totalCount[arrSize] + countsVO.getProdCount();

                        arrSize = arrSize + 1;
                        number = new Number(col++, p_rowCount, Double.parseDouble(PretupsBL.getDisplayAmount(countsVO.getProdAmount())), ExcelStyle.getDataStyle());
                        p_worksheet1.addCell(number);
                        totalCount[arrSize] = totalCount[arrSize] + countsVO.getProdAmount();

                        arrSize = arrSize + 1;

                        countsVO = null;
                        countsVO = dailyReportAnalysisDAO.loadCountsForP2PServices(p_con, p_networkCode, productDailyReportVO.getProductCode(), p2pServiceDailyReportVO
                            .getValue(), _fromDate, p_toDate);
                        number = new Number(col++, p_rowCount, countsVO.getProdCount(), ExcelStyle.getDataStyle());
                        p_worksheet1.addCell(number);
                        totalCount[arrSize] = totalCount[arrSize] + countsVO.getProdCount();

                        arrSize = arrSize + 1;

                        number = new Number(col++, p_rowCount, Double.parseDouble(PretupsBL.getDisplayAmount(countsVO.getProdAmount())), ExcelStyle.getDataStyle());
                        p_worksheet1.addCell(number);
                        totalCount[arrSize] = totalCount[arrSize] + countsVO.getProdAmount();

                    }
                    arrSize = 0;
                    p_rowCount++;
                }
            }

            col = 0;

            keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.p2ptotal.label", null);
            label = new Label(col++, p_rowCount, keyName, ExcelStyle.getTotalSummaryFont());
            p_worksheet1.addCell(label);
            int totalCountsLength=totalCount.length;
            for (int i = 0; i < totalCountsLength; i++) {
                if (i % 2 == 0) {
                    number = new Number(col++, p_rowCount, totalCount[i], ExcelStyle.getTotalSummaryFont());
                    p_worksheet1.addCell(number);
                } else {
                    number = new Number(col++, p_rowCount, Double.parseDouble(PretupsBL.getDisplayAmount(totalCount[i])), ExcelStyle.getTotalSummaryFont());
                    p_worksheet1.addCell(number);
                }
            }
            col = 0;
            p_rowCount++;

        } catch (BTSLBaseException be) {
            _logger.errorTrace(METHOD_NAME, be);
            _logger.error("writeNetworkServicesP2P", " BTSLBaseException e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("writeNetworkServicesP2P", " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, "writeNetworkServicesP2P", "Exception=" + e.getMessage());
        }
        return p_rowCount;
    }

    /**
     * Method to write Channel service for Network
     * 
     * @param p_con
     * @param p_worksheet1
     * @param p_networkCode
     * @param p_rowCount
     * @param p_toDate
     * @return
     * @throws BTSLBaseException
     */
    private int writeNetworkServicesC2S(Connection p_con, WritableSheet p_worksheet1, String p_networkCode, int p_rowCount, Date p_toDate) throws BTSLBaseException {
        final String METHOD_NAME = "writeNetworkServicesC2S";
        int col = 0;
        try {
            final long[] totalCount = new long[_c2sProductList.size() * 4];
            ListValueVO c2sServiceDailyReportVO = null;
            DailyReportVO productDailyReportVO = null;
            DailyReportVO countsVO = null;
            Number number = null;
            Label label = null;
            int arrSize = 0;
            String keyName = null;
            String transferCode = null;
            String transferSubType = null;
            if (_c2sServicesList != null) {
                _c2sProductsAvailable = true;
                int c2sServicesListSizes=_c2sServicesList.size();
                for (int i = 0; i <c2sServicesListSizes ; i++) {
                    c2sServiceDailyReportVO = (ListValueVO) _c2sServicesList.get(i);
                    col = 0;
                    label = new Label(col, p_rowCount, c2sServiceDailyReportVO.getLabel(), ExcelStyle.getDataStyle());
                    p_worksheet1.mergeCells(col, p_rowCount, col++, p_rowCount);
                    p_worksheet1.addCell(label);
                    int c2sProductListsSizes=_c2sProductList.size();
                    for (int l = 0; l < c2sProductListsSizes; l++) {
                        productDailyReportVO = (DailyReportVO) _c2sProductList.get(l);
                        countsVO = null;
                        countsVO = dailyReportAnalysisDAO.loadC2SServiceCounts(p_con, p_networkCode, productDailyReportVO.getProductCode(),
                            c2sServiceDailyReportVO.getValue(), p_toDate, p_toDate);
                        number = new Number(col++, p_rowCount, countsVO.getProdCount(), ExcelStyle.getDataStyle());
                        p_worksheet1.addCell(number);
                        totalCount[arrSize] = totalCount[arrSize] + countsVO.getProdCount();

                        arrSize = arrSize + 1;
                        number = new Number(col++, p_rowCount, Double.parseDouble(PretupsBL.getDisplayAmount(countsVO.getProdAmount())), ExcelStyle.getDataStyle());
                        p_worksheet1.addCell(number);
                        totalCount[arrSize] = totalCount[arrSize] + countsVO.getProdAmount();

                        arrSize = arrSize + 1;

                        countsVO = null;
                        countsVO = dailyReportAnalysisDAO.loadC2SServiceCounts(p_con, p_networkCode, productDailyReportVO.getProductCode(),
                            c2sServiceDailyReportVO.getValue(), _fromDate, p_toDate);
                        number = new Number(col++, p_rowCount, countsVO.getProdCount(), ExcelStyle.getDataStyle());
                        p_worksheet1.addCell(number);
                        totalCount[arrSize] = totalCount[arrSize] + countsVO.getProdCount();

                        arrSize = arrSize + 1;

                        number = new Number(col++, p_rowCount, Double.parseDouble(PretupsBL.getDisplayAmount(countsVO.getProdAmount())), ExcelStyle.getDataStyle());
                        p_worksheet1.addCell(number);
                        totalCount[arrSize] = totalCount[arrSize] + countsVO.getProdAmount();

                        arrSize = arrSize + 1;
                    }
                    arrSize = 0;
                    p_rowCount++;
                }
            }
            String tempStr = PretupsI.CHANNEL_TYPE_O2C + "," + PretupsI.CHANNEL_TYPE_C2C;
            final StringTokenizer stk = new StringTokenizer(tempStr, ",");
            tempStr = PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER + "," + PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW + "," + PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN;
            StringTokenizer transferType = null;
            arrSize = 0;
            while (stk.hasMoreElements()) {
                transferCode = stk.nextToken();
                transferType = new StringTokenizer(tempStr, ",");
                while (transferType.hasMoreElements()) {
                    transferSubType = transferType.nextToken();
                    col = 0;
                    keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.service." + transferCode + "_" + transferSubType, null);
                    label = new Label(col++, p_rowCount, keyName, ExcelStyle.getDataStyle());
                    p_worksheet1.addCell(label);

                    for (int l = 0; l < _c2sProductList.size(); l++) {
                        productDailyReportVO = (DailyReportVO) _c2sProductList.get(l);

                        countsVO = null;
                        countsVO = dailyReportAnalysisDAO.loadChannelServiceCounts(p_con, p_networkCode, productDailyReportVO.getProductCode(), transferCode, transferSubType,
                            p_toDate, p_toDate);
                        number = new Number(col++, p_rowCount, countsVO.getProdCount(), ExcelStyle.getDataStyle());
                        p_worksheet1.addCell(number);
                        totalCount[arrSize] = totalCount[arrSize] + countsVO.getProdCount();

                        arrSize = arrSize + 1;
                        number = new Number(col++, p_rowCount, Double.parseDouble(PretupsBL.getDisplayAmount(countsVO.getProdAmount())), ExcelStyle.getDataStyle());
                        p_worksheet1.addCell(number);
                        totalCount[arrSize] = totalCount[arrSize] + countsVO.getProdAmount();

                        arrSize = arrSize + 1;

                        countsVO = null;
                        countsVO = dailyReportAnalysisDAO.loadChannelServiceCounts(p_con, p_networkCode, productDailyReportVO.getProductCode(), transferCode, transferSubType,
                            _fromDate, p_toDate);
                        number = new Number(col++, p_rowCount, countsVO.getProdCount(), ExcelStyle.getDataStyle());
                        p_worksheet1.addCell(number);
                        totalCount[arrSize] = totalCount[arrSize] + countsVO.getProdCount();

                        arrSize = arrSize + 1;
                        number = new Number(col++, p_rowCount, Double.parseDouble(PretupsBL.getDisplayAmount(countsVO.getProdAmount())), ExcelStyle.getDataStyle());
                        p_worksheet1.addCell(number);
                        totalCount[arrSize] = totalCount[arrSize] + countsVO.getProdAmount();

                        arrSize = arrSize + 1;
                    }
                    arrSize = 0;
                    p_rowCount++;
                }
            }

            col = 0;
            keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.service.networkstock", null);
            label = new Label(col++, p_rowCount, keyName, ExcelStyle.getDataStyle());
            p_worksheet1.addCell(label);
            arrSize = 0;

            for (int l = 0; l < _c2sProductList.size(); l++) {
                productDailyReportVO = (DailyReportVO) _c2sProductList.get(l);

                countsVO = null;
                countsVO = dailyReportAnalysisDAO.loadCountsForNtwrkTransfer(p_con, p_networkCode, productDailyReportVO.getProductCode(),
                    PretupsI.NETWORK_STOCK_TRANSACTION_CREATION, p_toDate, p_toDate);
                number = new Number(col++, p_rowCount, countsVO.getProdCount(), ExcelStyle.getDataStyle());
                p_worksheet1.addCell(number);
                totalCount[arrSize] = totalCount[arrSize] + countsVO.getProdCount();

                arrSize = arrSize + 1;
                number = new Number(col++, p_rowCount, Double.parseDouble(PretupsBL.getDisplayAmount(countsVO.getProdAmount())), ExcelStyle.getDataStyle());
                p_worksheet1.addCell(number);
                totalCount[arrSize] = totalCount[arrSize] + countsVO.getProdAmount();

                arrSize = arrSize + 1;

                countsVO = null;
                countsVO = dailyReportAnalysisDAO.loadCountsForNtwrkTransfer(p_con, p_networkCode, productDailyReportVO.getProductCode(),
                    PretupsI.NETWORK_STOCK_TRANSACTION_CREATION, _fromDate, p_toDate);
                number = new Number(col++, p_rowCount, countsVO.getProdCount(), ExcelStyle.getDataStyle());
                p_worksheet1.addCell(number);
                totalCount[arrSize] = totalCount[arrSize] + countsVO.getProdCount();

                arrSize = arrSize + 1;
                number = new Number(col++, p_rowCount, Double.parseDouble(PretupsBL.getDisplayAmount(countsVO.getProdAmount())), ExcelStyle.getDataStyle());
                p_worksheet1.addCell(number);
                totalCount[arrSize] = totalCount[arrSize] + countsVO.getProdAmount();

                arrSize = arrSize + 1;
            }
            col = 0;
            p_rowCount++;
            keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.total.label", null);
            label = new Label(col++, p_rowCount, keyName, ExcelStyle.getTotalSummaryFont());
            p_worksheet1.addCell(label);
             int totalCountsLength=totalCount.length;
            for (int i = 0; i < totalCountsLength; i++) {
                if (i % 2 == 0) {
                    number = new Number(col++, p_rowCount, totalCount[i], ExcelStyle.getTotalSummaryFont());
                    p_worksheet1.addCell(number);
                } else {
                    number = new Number(col++, p_rowCount, Double.parseDouble(PretupsBL.getDisplayAmount(totalCount[i])), ExcelStyle.getTotalSummaryFont());
                    p_worksheet1.addCell(number);
                }
            }
            col = 0;
            p_rowCount++;

            do {
                label = new Label(col++, p_rowCount, "", ExcelStyle.getDataStyle());
                p_worksheet1.addCell(label);
            } while (col <= _c2sProductList.size() * 4);

            col = 0;
        } catch (BTSLBaseException be) {
            _logger.errorTrace(METHOD_NAME, be);
            _logger.error("writeNetworkServicesC2S", " BTSLBaseException e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("writeNetworkServicesC2S", " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, "writeNetworkServicesC2S", "Exception=" + e.getMessage());
        }
        return p_rowCount;
    }

    /**
     * Method to write the Excel Header info
     * 
     * @param p_worksheet1
     * @param p_reportDate
     * @param p_Header
     * @param p_c2sMisDate
     * @param p_p2pMisDate
     * @return
     * @throws BTSLBaseException
     */
    private int writeExcelHeader(WritableSheet p_worksheet1, Date p_reportDate, String p_Header, String p_c2sMisDate, String p_p2pMisDate) throws BTSLBaseException {
        final String METHOD_NAME = "writeExcelHeader";
        if (_logger.isDebugEnabled()) {
            _logger.debug("writeExcelHeader", " p_reportDate:" + p_reportDate + " p_Header=" + p_Header);
        }

        int col = 0;
        try {
            p_worksheet1.setColumnView(0, 27);
            p_worksheet1.setColumnView(1, 27);
            for (int i = 2; i < 30; i++) {
                p_worksheet1.setColumnView(i, 16);
            }

            final Date currentDate = new Date();
            String keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.header.name", null);
            Label label = new Label(col, _rowCount, keyName, ExcelStyle.getTopHeadingFont());
            p_worksheet1.mergeCells(col, _rowCount, col + 4, _rowCount);
            p_worksheet1.addCell(label);

            col = 0;
            _rowCount++;
            label = new Label(col, _rowCount, p_Header, ExcelStyle.getSecondTopHeadingFont2());
            p_worksheet1.mergeCells(col, _rowCount, col + 4, _rowCount);
            p_worksheet1.addCell(label);

            col = 0;
            _rowCount++;
            keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.date.label", null) + BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(p_reportDate));
            label = new Label(col, _rowCount, keyName, ExcelStyle.getSecondTopHeadingFont());
            p_worksheet1.mergeCells(col, _rowCount, ++col, _rowCount);
            p_worksheet1.addCell(label);

            label = new Label(++col, _rowCount, "", ExcelStyle.getSecondTopHeadingFont());
            p_worksheet1.addCell(label);

            keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.generatedon.label", null) + BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(currentDate));
            label = new Label(++col, _rowCount, keyName, ExcelStyle.getSecondTopHeadingFont());
            p_worksheet1.mergeCells(col, _rowCount, ++col, _rowCount);
            p_worksheet1.addCell(label);

            col = 0;
            _rowCount++;

            label = new Label(col, _rowCount, "", ExcelStyle.getSecondTopHeadingFont());
            p_worksheet1.mergeCells(col, _rowCount, col + 4, _rowCount);
            p_worksheet1.addCell(label);

            col = 0;
            _rowCount++;

            final ArrayList moduleList = LookupsCache.loadLookupDropDown(PretupsI.MODULE_TYPE, true);
            for (int m = 0, n = moduleList.size(); m < n; m++) {
                final ListValueVO listValueVO = (ListValueVO) moduleList.get(m);
                if (PretupsI.C2S_MODULE.equals(listValueVO.getValue())) {
                    keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.c2s.misdate", null) + BTSLDateUtil.getSystemLocaleDate(p_c2sMisDate);
                    label = new Label(col, _rowCount, keyName, ExcelStyle.getThirdTopHeadingFont());
                    p_worksheet1.mergeCells(col, _rowCount, ++col, _rowCount);
                    p_worksheet1.addCell(label);

                    label = new Label(++col, _rowCount, "", ExcelStyle.getThirdTopHeadingFont());
                    p_worksheet1.addCell(label);
                    col++;

                } else if (PretupsI.P2P_MODULE.equals(listValueVO.getValue())) {
                    keyName = BTSLUtil.getMessage(_locale, "dailyreport.summary.p2p.misdate", null) + BTSLDateUtil.getSystemLocaleDate(p_p2pMisDate);
                    label = new Label(col, _rowCount, keyName, ExcelStyle.getThirdTopHeadingFont());
                    p_worksheet1.mergeCells(col, _rowCount, ++col, _rowCount);
                    p_worksheet1.addCell(label);
                    col++;
                    if (col < 4) {
                        label = new Label(col, _rowCount, "", ExcelStyle.getThirdTopHeadingFont());
                        p_worksheet1.mergeCells(col, _rowCount, 4, _rowCount);
                        p_worksheet1.addCell(label);
                    }

                }
            }
            col = 0;
            _rowCount++;
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

}

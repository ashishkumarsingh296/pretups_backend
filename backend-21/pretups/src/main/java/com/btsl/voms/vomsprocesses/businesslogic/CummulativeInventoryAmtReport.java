package com.btsl.voms.vomsprocesses.businesslogic;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.EMailSender;
import com.btsl.db.util.ObjectProducer;
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
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductVO;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherSummaryVO;
import com.ibm.icu.util.Calendar;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Border;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * @(#)CummulativeInventoryAmtReport.java
 *                                        Copyright(c) 2003, Bharti Telesoft
 *                                        Ltd.
 *                                        All Rights Reserved
 *                                        This method writes the Cummulative
 *                                        inventory report.
 *                                        --------------------------------------
 *                                        --------------------------------------
 *                                        ---------------------
 *                                        Author Date History
 *                                        --------------------------------------
 *                                        --------------------------------------
 *                                        ---------------------
 *                                        Gurjeet Singh 20/07/2006 Initial
 *                                        Creation
 *                                        Amit Singh 20/07/2006 Initial Creation
 *                                        --------------------------------------
 *                                        --------------------------------------
 *                                        ---------------------
 */

public class CummulativeInventoryAmtReport {

    private static Log _logger = LogFactory.getLog(CummulativeInventoryAmtReport.class.getName());

    private File _file = null;

    // create a new workbook
    private static WritableWorkbook _workbook = null;

    // create a new sheet
    private static WritableSheet _worksheetInv = null;
    private static WritableSheet _worksheetAct = null;

    private static String _reportFor = null;
    private static String _reportTo = null;
    private static Date _utilDate = null;
    private static java.util.Date _currentDate = null;
    private static String _mainBodyPart = "";
    private static String _bodyPart = "";
    private static String _sheet1BodyPart = "";
    private static String _sheet1BodyContPart = "";
    private static Locale _locale = null;

    // this is used for the arguments for the
    // messages of messages.properties
    private static String arg[] = {};

    private ProcessStatusDAO _processStatusDAO = null;
    private static ProcessStatusVO processStatusVO = null;
    private static boolean processStatusOK = false;

    public CummulativeInventoryAmtReport() {
        super();
        _processStatusDAO = new ProcessStatusDAO();
        _currentDate = new Date();
    }

    public static void main(String[] args) {
        Date startDate = null;

        java.sql.Connection con = null;
        String filePath = null;
        String fileName = null;
        String actualFilePath = null;
        ProcessBL processBL = new ProcessBL();

        final String methodName = "main";
        try {
            if (args.length != 3) {
                _logger.error(methodName, " CummulativeInventoryAmtReport : Proper no. of arguments are not defined properly ");
                return;
            }

            File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
                _logger.debug(methodName, "Constants file not found on location:: " + constantsFile.toString());
                _logger.error(methodName, " Constants file not found on location:: " + constantsFile.toString());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, methodName, "", "", "", " The Constants file doesn't exists at the path specified. ");
                throw new BTSLBaseException("CummulativeInventoryAmtReport ", methodName, PretupsErrorCodesI.VOMS_INVRPT_ERROR_EXCEPTION_CONST_FILE_MISSING);
            }

            File logconfigFile = new File(args[1]);
            if (!logconfigFile.exists()) {
                _logger.debug(methodName, "Logconfig file not found on location:: " + logconfigFile.toString());
                _logger.error(methodName, " ProcessLogConfig file not found on location:: " + logconfigFile.toString());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "", " The ProcessLogConfig file doesn't exists  at the path specified. ");
                throw new BTSLBaseException("CummulativeInventoryAmtReport ", methodName, PretupsErrorCodesI.VOMS_INVRPT_ERROR_EXCEPTION_LOG_FILE_MISSING);
            }

            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, " before loading constants.props & ProcessLogConfig.props ");
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());

            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, " After loading constants.props & ProcessLogConfig.props ");
            }

        }// end of try
        catch (Exception exception) {
            _logger.errorTrace(methodName, exception);
            _logger.error(methodName, " Error while loading property files");
            return;
        }// end of catch

        try

        {
            filePath = Constants.getProperty("daily_inventory_report_path");
            fileName = Constants.getProperty("daily_inventory_report_name");

            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(methodName, " Connection null");
                }
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CummulativeInventoryAmtReport[process]", "", "", "", "  Message: Not able to get database connection");
                throw new BTSLBaseException("CummulativeInventoryAmtReport ", methodName, PretupsErrorCodesI.VOMS_INVRPT_ERROR_EXCEPTION_CON);
            }


            processStatusVO = processBL.checkProcessUnderProcess(con, ProcessI.VOMS_INVENTORY_REPORT_PROCCESSID);

            if (!(processStatusVO != null && processStatusVO.isStatusOkBool())) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(methodName, " The process entry is not found in the process_status table and ProcessID: " + ProcessI.VOMS_INVENTORY_REPORT_PROCCESSID);
                }
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "CummulativeInventoryAmtReport[process]", ProcessI.VOMS_INVENTORY_REPORT_PROCCESSID, "", "", " Entry not found in process_status table");
                throw new BTSLBaseException("CummulativeInventoryAmtReport", methodName, PretupsErrorCodesI.VOMS_INVRPT_ERROR_EXCEPTION);

            }
            processStatusOK = processStatusVO.isStatusOkBool();
            // Commiting the status of process status as 'U-Under Process'.

            // This is the main method of this process
            // for creating the inventory report
            SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT);
            if (sdf.format(processStatusVO.getExecutedUpto()).equals(sdf.format(BTSLUtil.addDaysInUtilDate(new Date(), -1)))) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("process", " The process has already been executed for today ");
                }
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "CummulativeInventoryAmtReport[process]", "", "", "", "  Message:  The process has already been executed for today ");
                throw new BTSLBaseException("CummulativeInventoryAmtReport ", "process", PretupsErrorCodesI.VOMS_INVRPT_ERROR_EXCEPTION_ALREADY_EXECUTED);
            }

            try {
                con.commit();
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
            }

            for (startDate = BTSLUtil.addDaysInUtilDate(processStatusVO.getExecutedUpto(), +1); startDate.before(BTSLUtil.addDaysInUtilDate(new Date(), -1)); startDate = BTSLUtil.addDaysInUtilDate(startDate, 1)) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(methodName, " Calling the process() method from main()..");
                }

                actualFilePath = filePath + fileName + "_" + BTSLUtil.getSQLDateFromUtilDate(startDate) + ".xls";

                if (_logger.isDebugEnabled()) {
                    _logger.debug(methodName, " actualFilePath: " + actualFilePath);
                }

                // args[2] is used to pass the language code defined as a
                // arguments in the script
                new CummulativeInventoryAmtReport().process(con, BTSLUtil.getSQLDateFromUtilDate(startDate), args[2], filePath, fileName, actualFilePath);
            }
        } catch (BTSLBaseException be) {
            _logger.error(methodName, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(methodName, be);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
            }
            return;
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                _logger.error(methodName, "Exception closing connection ");
                _logger.errorTrace(methodName, ex);
            }

            try {
                if (_workbook != null) {
                    _workbook.close();
                }
            } catch (WriteException e1) {
                // TODO Auto-generated catch block
                _logger.errorTrace(methodName, e1);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                _logger.errorTrace(methodName, e1);
            }
            ConfigServlet.destroyProcessCache();
        }

        if (_logger.isDebugEnabled()) {
            _logger.debug(methodName, "Exiting..... ");
        }
    }

    /**
     * This method is the main method of this process,
     * which is responcible for the Inventory report creation.
     * 
     * @throws BTSLBaseException
     */
    private void process(Connection p_con, java.sql.Date p_reqDate, String p_languageCode, String p_filePath, String p_fileName, String p_actualFilePath) throws BTSLBaseException {
        final String METHOD_NAME = "process";
        if (_logger.isDebugEnabled()) {
            _logger.debug("process", " Entered in method with:: p_con=" + p_con + " p_reqDate" + p_reqDate + "p_languageCode: " + p_languageCode + "p_filePath: " + p_filePath + "p_fileName: " + p_fileName + "p_actualFilePath: " + p_actualFilePath);
        }

        java.sql.Date requiredDate = p_reqDate;
        Calendar cal = BTSLDateUtil.getInstance();
        cal.add(Calendar.DATE, (-1) * 1);
        _utilDate = cal.getTime();

        try {
            _reportFor = BTSLUtil.getVomsDateStringFromDate(_utilDate);
            _reportTo = BTSLUtil.getVomsDateStringFromDate(_currentDate);
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _reportFor = "";
            _reportTo = "";
        }
        try {

            String fileNameForEmail = null;
            String subjectForMail = null;
            String fromEmail = null;
            String toEmailAddresses = null;

            try {
                _locale = LocaleMasterCache.getLocaleFromCodeDetails(p_languageCode);
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
                if (_logger.isDebugEnabled()) {
                    _logger.debug("process", " Not able to get the locale");
                }

                _locale = new Locale("en", "US");

                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "CummulativeInventoryAmtReport[process]", "", "", "", "  Message:  Not able to get the locale");
                throw new BTSLBaseException("CummulativeInventoryAmtReport ", "process", PretupsErrorCodesI.VOMS_INVRPT_ERROR_EXCEPTION_LOCALE);
            }
            if (_locale == null) {
                _locale = new Locale("en", "US");
            }

            try {
                subjectForMail = Constants.getProperty("daily_inventory_report_subject");
                fileNameForEmail = Constants.getProperty("daily_inventory_report_name_mail");
                fromEmail = Constants.getProperty("from_email_address");
                toEmailAddresses = Constants.getProperty("toEmailAddresses");
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
                if (_logger.isDebugEnabled()) {
                    _logger.debug("process", " Not able to get Constants values");
                }
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "CummulativeInventoryAmtReport[process]", "", "", "", "  Message: Not able to get Constants values");
                throw new BTSLBaseException("CummulativeInventoryAmtReport ", "process", PretupsErrorCodesI.VOMS_INVRPT_ERROR_EXCEPTION_CONSTANTS);
            }
            try {
                File fileDir = new File(p_filePath);
                if (!fileDir.isDirectory()) {
                    fileDir.mkdirs();

                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
                if (_logger.isDebugEnabled()) {
                    _logger.debug("process", " Not able to create a directory");
                }
                throw new BTSLBaseException("CummulativeInventoryAmtReport ", "process", PretupsErrorCodesI.VOMS_INVRPT_ERROR_EXCEPTION_CONSTANTS);
            }

            String tableBodyEmail = null;
            String secTableBodyEmail = null;
            String thirdTableBodyEmail = null;
            String argMail[] = { _reportFor };

            _bodyPart = "";
            tableBodyEmail = null;
            secTableBodyEmail = null;
            _mainBodyPart = "<html><body bgcolor=\"#FFFFFF\" text=\"#000000\"><p align=\"left\"><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"3\"><b><font size=\"2\">";
            _mainBodyPart = _mainBodyPart + BTSLUtil.getMessage(_locale, "dailyreport.inventory.xlrpt.bodymessagestart", argMail) + "</font></b></font></p>";
            tableBodyEmail = "<table width=\"95%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#CCCCCC\" align=\"center\">";
            tableBodyEmail = tableBodyEmail + "<tr><td bgcolor=\"#CCCCCC\">";
            tableBodyEmail = tableBodyEmail + "<table width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"3\">";
            tableBodyEmail = tableBodyEmail + "<tr bgcolor=\"#FFFFFF\">";
            tableBodyEmail = tableBodyEmail + "<td bgcolor=\"#0F1177\" width=\"30%\"><font color=\"#FFFFFF\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>" + BTSLUtil.getMessage(_locale, "dailyreport.inventory.email.tableactivitylabel", arg) + "</b></font></td>";
            tableBodyEmail = tableBodyEmail + "<td bgcolor=\"#0F1177\" width=\"29%\"><font color=\"#FFFFFF\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>" + BTSLUtil.getMessage(_locale, "dailyreport.inventory.email.tableproductlabel", arg) + "</b></font></td>";
            tableBodyEmail = tableBodyEmail + "<td bgcolor=\"#0F1177\" width=\"17%\"><font color=\"#FFFFFF\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>" + BTSLUtil.getMessage(_locale, "dailyreport.inventory.email.tablecountlabel", arg) + "</b></font></td></tr>";

            secTableBodyEmail = "<table width=\"95%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#CCCCCC\" align=\"center\">";
            secTableBodyEmail = secTableBodyEmail + "<tr><td bgcolor=\"#CCCCCC\">";
            secTableBodyEmail = secTableBodyEmail + "<table width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"3\">";
            secTableBodyEmail = secTableBodyEmail + "<tr bgcolor=\"#FFFFFF\">";
            secTableBodyEmail = secTableBodyEmail + "<td bgcolor=\"#0F1177\" width=\"30%\"><font color=\"#FFFFFF\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>" + BTSLUtil.getMessage(_locale, "dailyreport.inventory.email.sectableactivitylabel", arg) + "</b></font></td>";
            secTableBodyEmail = secTableBodyEmail + "<td bgcolor=\"#0F1177\" width=\"29%\"><font color=\"#FFFFFF\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>" + BTSLUtil.getMessage(_locale, "dailyreport.inventory.email.secforlabel", arg) + "</b></font></td>";
            secTableBodyEmail = secTableBodyEmail + "<td bgcolor=\"#0F1177\" width=\"24%\"><font color=\"#FFFFFF\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>" + BTSLUtil.getMessage(_locale, "dailyreport.inventory.email.sectablebtaffectlabel", arg) + "</b></font></td>";
            secTableBodyEmail = secTableBodyEmail + "<td bgcolor=\"#0F1177\" width=\"17%\"><font color=\"#FFFFFF\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>" + BTSLUtil.getMessage(_locale, "dailyreport.inventory.email.sectablecountlabel", arg) + "</b></font></td></tr>";

            thirdTableBodyEmail = "<table width=\"95%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#CCCCCC\" align=\"center\">";
            thirdTableBodyEmail = thirdTableBodyEmail + "<tr><td bgcolor=\"#CCCCCC\">";
            thirdTableBodyEmail = thirdTableBodyEmail + "<table width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"3\">";
            thirdTableBodyEmail = thirdTableBodyEmail + "<tr bgcolor=\"#FFFFFF\">";
            thirdTableBodyEmail = thirdTableBodyEmail + "<td bgcolor=\"#0F1177\" width=\"30%\"><font color=\"#FFFFFF\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>" + BTSLUtil.getMessage(_locale, "dailyreport.inventory.email.thirdtableactivitylabel", arg) + "</b></font></td>";
            thirdTableBodyEmail = thirdTableBodyEmail + "<td bgcolor=\"#0F1177\" width=\"20%\"><font color=\"#FFFFFF\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>" + BTSLUtil.getMessage(_locale, "dailyreport.inventory.email.thirdprodlabel", arg) + "</b></font></td>";
            thirdTableBodyEmail = thirdTableBodyEmail + "<td bgcolor=\"#0F1177\" width=\"20%\"><font color=\"#FFFFFF\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>" + BTSLUtil.getMessage(_locale, "dailyreport.inventory.email.thirdtablefromlabel", arg) + "</b></font></td>";
            thirdTableBodyEmail = thirdTableBodyEmail + "<td bgcolor=\"#0F1177\" width=\"20%\"><font color=\"#FFFFFF\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>" + BTSLUtil.getMessage(_locale, "dailyreport.inventory.email.thirdtabletolabel", arg) + "</b></font></td>";
            thirdTableBodyEmail = thirdTableBodyEmail + "<td bgcolor=\"#0F1177\" width=\"10%\"><font color=\"#FFFFFF\" face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"2\"><b>" + BTSLUtil.getMessage(_locale, "dailyreport.inventory.email.thirdtablecountlabel", arg) + "</b></font></td></tr>";

            // create a new workbook

            _file = new File(p_actualFilePath);
            _workbook = Workbook.createWorkbook(_file);

            // create a new sheet
            _worksheetInv = _workbook.createSheet(BTSLUtil.getMessage(_locale, "dailyreport.inventory.xlrpt.sheet0", arg), 0);

            writeInventoryInfo(p_con, requiredDate);

            // SECOND SHEET INFO
            _worksheetAct = _workbook.createSheet(BTSLUtil.getMessage(_locale, "dailyreport.inventory.xlrpt.sheet1", arg), 1);
            writeBatchVoucherInfo(p_con, requiredDate);

            _workbook.write();

            try {
                _workbook.close();
                _workbook = null;
            } catch (WriteException e1) {
                // TODO Auto-generated catch block
                _logger.errorTrace(METHOD_NAME, e1);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                _logger.errorTrace(METHOD_NAME, e1);
            }

            if (_logger.isDebugEnabled()) {
                _logger.debug("process", " After making xls file");
            }

            if (_logger.isDebugEnabled()) {
                _logger.debug("process", " After closing output stream");
            }

            if (_logger.isDebugEnabled()) {
                _logger.debug("process", " Before sending E Mail");
            }

            if (!BTSLUtil.isNullString(_bodyPart)) {
                _mainBodyPart = _mainBodyPart + "<p align=\"left\"><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"4\"><b><font size=\"2\">";
                _mainBodyPart = _mainBodyPart + BTSLUtil.getMessage(_locale, "dailyreport.inventory.email.exceptionalcaseslabel", arg) + "</font></b></font></p>" + tableBodyEmail;
                _mainBodyPart = _mainBodyPart + _bodyPart;
                _mainBodyPart = _mainBodyPart + "</table></td></tr></table>";
                if (!BTSLUtil.isNullString(_sheet1BodyPart)) {
                    _mainBodyPart = _mainBodyPart + "<p>&nbsp;</p>" + secTableBodyEmail;
                    _mainBodyPart = _mainBodyPart + _sheet1BodyPart;
                    _mainBodyPart = _mainBodyPart + "</table></td></tr></table>";
                }
                if (!BTSLUtil.isNullString(_sheet1BodyContPart)) {
                    _mainBodyPart = _mainBodyPart + "<p>&nbsp;</p>" + thirdTableBodyEmail;
                    _mainBodyPart = _mainBodyPart + _sheet1BodyContPart;
                    _mainBodyPart = _mainBodyPart + "</table></td></tr></table>";
                }
            } else {
                if (!BTSLUtil.isNullString(_sheet1BodyPart)) {
                    _mainBodyPart = _mainBodyPart + "<p align=\"left\"><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"4\"><b><font size=\"2\">";
                    _mainBodyPart = _mainBodyPart + BTSLUtil.getMessage(_locale, "dailyreport.inventory.email.exceptionalcaseslabel", arg) + "</font></b></font></p>" + secTableBodyEmail;
                    _mainBodyPart = _mainBodyPart + _sheet1BodyPart;
                    _mainBodyPart = _mainBodyPart + "</table></td></tr></table>";
                    if (!BTSLUtil.isNullString(_sheet1BodyContPart)) {
                        _mainBodyPart = _mainBodyPart + "<p>&nbsp;</p>" + thirdTableBodyEmail;
                        _mainBodyPart = _mainBodyPart + _sheet1BodyContPart;
                        _mainBodyPart = _mainBodyPart + "</table></td></tr></table>";
                    }
                } else if (!BTSLUtil.isNullString(_sheet1BodyContPart)) {
                    _mainBodyPart = _mainBodyPart + "<p align=\"left\"><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"4\"><b><font size=\"2\">";
                    _mainBodyPart = _mainBodyPart + BTSLUtil.getMessage(_locale, "dailyreport.inventory.email.exceptionalcaseslabel", arg) + "</font></b></font></p>" + secTableBodyEmail;
                    _mainBodyPart = _mainBodyPart + _sheet1BodyPart;
                    _mainBodyPart = _mainBodyPart + "</table></td></tr></table>";

                }
            }
            _mainBodyPart = _mainBodyPart + "</body></html>";

            // For sending E-Mail
            try {
                EMailSender.sendMail(toEmailAddresses, fromEmail, "", "", subjectForMail, _mainBodyPart, true, p_actualFilePath, fileNameForEmail);
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
                if (_logger.isDebugEnabled()) {
                    _logger.debug("process", " Exception while sending the mail=" + e);
                }
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "CummulativeInventoryAmtReport[process]", "", "", "", " Exception while sending the mail");
                throw new BTSLBaseException("CummulativeInventoryAmtReport ", "process", PretupsErrorCodesI.VOMS_INVRPT_ERROR_EXCEPTION_EMAIL_SENDING);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("process", "  Inventory report creation process executed successfully");
            }
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "CummulativeInventoryAmtReport[process]", "", "", "", " Inventory report creation process executed successfully");
        }// end of try
        catch (BTSLBaseException be) {
            _logger.error("process", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            if (p_con != null) {
                try {
                    p_con.rollback();
                } catch (Exception e1) {
                    _logger.errorTrace(METHOD_NAME, e1);
                }
            }
            throw be;
        } catch (Exception e) {
            _logger.error("process", "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            if (p_con != null) {
                try {
                    p_con.rollback();
                } catch (Exception e1) {
                    _logger.errorTrace(METHOD_NAME, e1);
                }
            }
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "CummulativeInventoryAmtReport[process]", "", "", "", " Inventory report creation process could not be executed successfully");
            throw new BTSLBaseException("CummulativeInventoryAmtReport", "process", PretupsErrorCodesI.VOMS_INVRPT_ERROR_EXCEPTION);
        } finally {
            try {
                // Setting the process status as 'C-Complete' if the
                // processStatusOK is true
                if (processStatusOK) {
                    Date date = new Date();

                    processStatusVO.setExecutedOn(date);
                    processStatusVO.setExecutedUpto(BTSLUtil.getUtilDateFromSQLDate(requiredDate));
                    processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                    int successU = _processStatusDAO.updateProcessDetail(p_con, processStatusVO);

                    // Commiting the process status as 'C-Complete'
                    if (successU > 0) {
                        p_con.commit();
                    } else {
                        // throw new
                        // BTSLBaseException(this,"process",PretupsErrorCodesI.PROCESS_ERROR_UPDATE_STATUS);
                    }
                }// end of IF-Checks the proccess status
            }// end of try-block
            catch (BTSLBaseException be) {
                _logger.errorTrace(METHOD_NAME, be);
                _logger.error("process", "BTSLBaseException be= " + be);
                if (p_con != null) {
                    try {
                        p_con.rollback();
                    } catch (Exception e1) {
                        _logger.errorTrace(METHOD_NAME, e1);
                    }
                }
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "process", "processStatusVO.getProcessID()" + ProcessI.VOMS_INVENTORY_REPORT_PROCCESSID, "", "", "BTSLBaseException:" + be.getMessage());
            
            }// end of catch-BTSLBaseException
            catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
                _logger.error("process", "Exception e= " + e);
                if (p_con != null) {
                    try {
                        p_con.rollback();
                    } catch (Exception e1) {
                        _logger.errorTrace(METHOD_NAME, e1);
                    }
                }
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "process", "processStatusVO.getProcessID()" + ProcessI.VOMS_INVENTORY_REPORT_PROCCESSID, "", "", "BaseException:" + e.getMessage());
            }// end of catch-Exception

            if (_logger.isDebugEnabled()) {
                _logger.debug("process", "Exiting..... ");
            }
        }
    }

    /**
     * This method gets the counts from voucher summary table.
     * 
     * @param p_con
     *            Connection
     * @param p_createdDate
     *            java.sql.Date
     * @param p_noOfDays
     *            int
     * @return ArrayList
     */
    public static ArrayList getVoucherSummInfoLoc(Connection p_con, java.sql.Date p_createdDate, int p_noOfdays) throws BTSLBaseException {
        final String METHOD_NAME = "getVoucherSummInfoLoc";
        if (_logger.isDebugEnabled()) {
            _logger.debug("getVoucherSummInfoLoc", "Entered with p_createdDate=" + p_createdDate + "p_noOfdays=" + p_noOfdays);
        }
        PreparedStatement psmt = null;
        ResultSet rs = null;
        VomsVoucherSummaryVO vomsVoucherSummaryVO = null;
        java.util.ArrayList summaryList = null;
        CummulativeInventoryAmtReportQry cummulativeInventoryAmtReportQry = (CummulativeInventoryAmtReportQry)
        		ObjectProducer.getObject(QueryConstants.COMMULATIVE_INVENTORY_AMT_REPORT_QRY, QueryConstants.QUERY_PRODUCER);
      final String  strBuff = cummulativeInventoryAmtReportQry.getVoucherSummInfoLocQry(p_noOfdays);

        try {
            if (_logger.isDebugEnabled()) {
                _logger.debug("getVoucherSummInfoLoc", "Query :: " + strBuff.toString());
            }

            psmt = p_con.prepareStatement(strBuff.toString());

            psmt.setDate(1, p_createdDate);
            rs = psmt.executeQuery();

            if (rs != null) {
                summaryList = new java.util.ArrayList();
            }
            psmt.clearParameters();
            while (rs.next()) {
                vomsVoucherSummaryVO = new VomsVoucherSummaryVO();
                vomsVoucherSummaryVO.setProductID(rs.getString("PRODUCTID"));
                vomsVoucherSummaryVO.setProductName(rs.getString("PRODUCTNAME"));
                vomsVoucherSummaryVO.setMrpStr(PretupsBL.getDisplayAmount(rs.getLong("MRP")));
                vomsVoucherSummaryVO.setMinReqQuantity(rs.getLong("MIN_REQ_QUANTITY"));
                vomsVoucherSummaryVO.setMaxReqQuantity(rs.getLong("MAX_REQ_QUANTITY"));
                vomsVoucherSummaryVO.setTotalGenerated(rs.getLong("total_generated"));
                vomsVoucherSummaryVO.setTotalEnabled(rs.getLong("total_enabled"));
                vomsVoucherSummaryVO.setTotalRecharged(rs.getLong("total_recharged"));
                vomsVoucherSummaryVO.setTotalOnHold(rs.getLong("total_hold"));
                vomsVoucherSummaryVO.setTotalStolenDmg(rs.getLong("total_st_da_before"));
                vomsVoucherSummaryVO.setTotalStolenDmgAfterEn(rs.getLong("total_st_da_after"));
                vomsVoucherSummaryVO.setTotalReconciled(rs.getInt("total_reconciled"));

                summaryList.add(vomsVoucherSummaryVO);
            }
        } catch (SQLException sqe) {
            _logger.errorTrace(METHOD_NAME, sqe);
            if (_logger.isDebugEnabled()) {
                _logger.debug("getVoucherSummInfoLoc", " :: SQLException : " + sqe.getMessage());
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CummulativeInventoryAmtReport[getVoucherSummInfoLoc]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException("CummulativeInventoryAmtReport", "getVoucherSummInfoLoc", PretupsErrorCodesI.VOMS_INVRPT_ERROR_EXCEPTION_VOUCHERINFO);
        } catch (Exception ex) {
            _logger.errorTrace(METHOD_NAME, ex);
            if (_logger.isDebugEnabled()) {
                _logger.debug("getVoucherSummInfoLoc", " :: Exception : " + ex.getMessage());
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CummulativeInventoryAmtReport[getVoucherSummInfoLoc]", "", "", "", "SQL Exception:" + ex.getMessage());
            throw new BTSLBaseException("CummulativeInventoryAmtReport", "getVoucherSummInfoLoc", PretupsErrorCodesI.VOMS_INVRPT_ERROR_EXCEPTION_VOUCHERINFO);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("getVoucherSummInfoLoc", " :: Exiting : summaryList size = " + summaryList.size());
            }
        }
        return summaryList;
    }

    /**
     * This method gets the counts from voucher archive table.
     * 
     * @param p_con
     *            Connection
     * @param p_createdDate
     *            java.sql.Date
     * @return summaryList ArrayList
     */
    public static ArrayList getVoucherArchiveInfoLoc(Connection p_con, java.sql.Date p_createdDate) throws BTSLBaseException {
        final String METHOD_NAME = "getVoucherArchiveInfoLoc";
        if (_logger.isDebugEnabled()) {
            _logger.debug("getVoucherArchiveInfoLoc", " :: Entered with p_createdDate=" + p_createdDate);
        }
        PreparedStatement psmt = null;
        ResultSet rs = null;
        VomsVoucherSummaryVO vomsVoucherSummaryVO = null;
        java.util.ArrayList summaryList = null;
        CummulativeInventoryAmtReportQry cummulativeInventoryAmtReportQry = (CummulativeInventoryAmtReportQry)
        		ObjectProducer.getObject(QueryConstants.COMMULATIVE_INVENTORY_AMT_REPORT_QRY, QueryConstants.QUERY_PRODUCER);
        final String strBuff = cummulativeInventoryAmtReportQry.getVoucherArchiveInfoLocQry();

        try {
            if (_logger.isDebugEnabled()) {
                _logger.debug("getVoucherArchiveInfoLoc", " :: Query :: " + strBuff.toString());
            }

            psmt = p_con.prepareStatement(strBuff.toString());
            psmt.setDate(1, p_createdDate);
            rs = psmt.executeQuery();
            summaryList = new java.util.ArrayList();
            psmt.clearParameters();
            while (rs.next()) {
                vomsVoucherSummaryVO = new VomsVoucherSummaryVO();
                vomsVoucherSummaryVO.setProductName(rs.getString("PRODUCTNAME"));
                vomsVoucherSummaryVO.setMrpStr(PretupsBL.getDisplayAmount(rs.getLong("MRP")));
                vomsVoucherSummaryVO.setMinReqQuantity(rs.getLong("MIN_REQ_QUANTITY"));
                vomsVoucherSummaryVO.setMaxReqQuantity(rs.getLong("MAX_REQ_QUANTITY"));
                vomsVoucherSummaryVO.setTotalGenerated(rs.getLong("TOTAL_GENERATED"));
                vomsVoucherSummaryVO.setTotalEnabled(rs.getLong("TOTAL_ENABLED"));
                vomsVoucherSummaryVO.setTotalRecharged(rs.getLong("TOTAL_RECHARGED"));
                vomsVoucherSummaryVO.setTotalOnHold(rs.getLong("TOTAL_HOLD"));
                vomsVoucherSummaryVO.setTotalStolenDmg(rs.getLong("TOTAL_ST_DA_BEFORE"));
                vomsVoucherSummaryVO.setTotalStolenDmgAfterEn(rs.getLong("TOTAL_ST_DA_AFTER"));
                vomsVoucherSummaryVO.setTotalReconciled(rs.getInt("TOTAL_RECONCILED"));

                summaryList.add(vomsVoucherSummaryVO);
            }

        } catch (SQLException sqe) {
            _logger.errorTrace(METHOD_NAME, sqe);
            if (_logger.isDebugEnabled()) {
                _logger.debug("getVoucherArchiveInfoLoc", " :: SQLException : " + sqe.getMessage());
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CummulativeInventoryAmtReport[getVoucherArchiveInfoLoc]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException("CummulativeInventoryAmtReport", "getVoucherArchiveInfoLoc", PretupsErrorCodesI.VOMS_INVRPT_ERROR_EXCEPTION_VOARCHINFO);
        } catch (Exception ex) {
            _logger.errorTrace(METHOD_NAME, ex);
            if (_logger.isDebugEnabled()) {
                _logger.debug("getVoucherArchiveInfoLoc", " :: Exception : " + ex.getMessage());
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CummulativeInventoryAmtReport[getVoucherArchiveInfoLoc]", "", "", "", "SQL Exception:" + ex.getMessage());
            throw new BTSLBaseException("CummulativeInventoryAmtReport", "getVoucherArchiveInfoLoc", PretupsErrorCodesI.VOMS_INVRPT_ERROR_EXCEPTION_VOARCHINFO);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("getVoucherArchiveInfoLoc", " :: Exiting : summaryList size = " + summaryList.size());
            }
        }
        return summaryList;
    }

    /**
     * This method gets the counts from voucher audit table.
     * 
     * @param p_con
     *            Connection
     * @param p_productId
     *            String
     * @param p_previousStat
     *            String
     * @param p_newStat
     *            String
     * @param p_createdDate
     *            java.sql.Date
     * @return int
     */
    private static int getVoucherCount(Connection p_con, String p_productId, String p_previousStat, String p_newStat, java.sql.Date p_createdDate) throws BTSLBaseException {
        final String METHOD_NAME = "getVoucherCount";
        if (_logger.isDebugEnabled()) {
            _logger.debug("getVoucherCount", " :: Entered with p_productId=" + p_productId + " p_previousStat=" + p_previousStat + "p_createdDate=" + p_createdDate);
        }

        PreparedStatement psmt = null;
        ResultSet rs = null;
        int noOfVouchers = 0;

      
        CummulativeInventoryAmtReportQry cummulativeInventoryAmtReportQry = (CummulativeInventoryAmtReportQry)
        		ObjectProducer.getObject(QueryConstants.COMMULATIVE_INVENTORY_AMT_REPORT_QRY, QueryConstants.QUERY_PRODUCER);
        
        final String strBuff = cummulativeInventoryAmtReportQry.getVoucherCountQry();
        try {
            if (_logger.isDebugEnabled()) {
                _logger.debug("getVoucherCount", " :: Query :: " + strBuff.toString());
            }
            psmt = p_con.prepareStatement(strBuff.toString());
            psmt.setString(1, p_productId);
            psmt.setString(2, p_newStat);
            psmt.setString(3, p_previousStat);
            psmt.setDate(4, p_createdDate);

            rs = psmt.executeQuery();
            while (rs.next()) {
                noOfVouchers = rs.getInt("VOUCHCOUNT");
            }
        } catch (SQLException sqe) {
            _logger.errorTrace(METHOD_NAME, sqe);
            if (_logger.isDebugEnabled()) {
                _logger.debug("getVoucherCount", " :: SQLException : " + sqe.getMessage());
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CummulativeInventoryAmtReport[getVoucherCount]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException("CummulativeInventoryAmtReport", "getVoucherCount", PretupsErrorCodesI.VOMS_INVRPT_ERROR_EXCEPTION_VOARCOUNT);
        } catch (Exception ex) {
            _logger.errorTrace(METHOD_NAME, ex);
            if (_logger.isDebugEnabled()) {
                _logger.debug("getVoucherCount", " :: Exception : " + ex.getMessage());
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CummulativeInventoryAmtReport[getVoucherCount]", "", "", "", "SQL Exception:" + ex.getMessage());
            throw new BTSLBaseException("CummulativeInventoryAmtReport", "getVoucherCount", PretupsErrorCodesI.VOMS_INVRPT_ERROR_EXCEPTION_VOARCOUNT);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("getVoucherCount", " :: Exiting : noOfVouchers size = " + noOfVouchers);
            }
        }
        return noOfVouchers;
    }

    /**
     * This method writes the inventory info. on the Excel file.
     * 
     * @param p_con
     *            Connection
     * @param p_date
     *            java.sql.Date
     * @param p_reportHeading
     *            String
     */
    private static void writeInventoryInfo(Connection p_con, java.sql.Date p_date) throws Exception {

        final String METHOD_NAME = "writeInventoryInfo";
        if (_logger.isDebugEnabled()) {
            _logger.debug("writeInventoryInfo", " :: Entered with p_date=" + p_date);
        }

        ArrayList voucherSummList = null;
        VomsVoucherSummaryVO currentSummVO = null;
        VomsVoucherSummaryVO archiveSummVO = null;
        ArrayList voucharchive = null;
        ArrayList dailySummList = null;
        VomsVoucherSummaryVO dailySummVO = null;

        long todaysTotalGeCount = 0;
        long todaysTotalEnCount = 0;
        long todaysTotalOholdCount = 0;
        long todaysTotalRecCount = 0;
        long todaysTotalStDaCount = 0;
        long todaysTotalStDaEnCount = 0;
        long todaysTotalReconcileCount = 0;

        long totalGeneratedCount = 0;
        long totalEnableCount = 0;
        long totalRechargeCount = 0;
        long totalOnHoldCount = 0;
        long totalStolenCount = 0;
        long totalDamageCount = 0;
        long totalReconcileCount = 0;

        long availGeneratedCount = 0;
        long availEnableCount = 0;
        long availRechargeCount = 0;
        long availOnHoldCount = 0;
        long availStolenCount = 0;
        long availDamageCount = 0;
        long availReconcileCount = 0;

        double availGeneratedMrp = 0;
        double availEnableMrp = 0;
        double availRechargeMrp = 0;
        double availOnHoldMrp = 0;
        double availStolenMrp = 0;
        double availDamageMrp = 0;
        double availReconcileMrp = 0;

        double productMrp = 0;

        int noOfdays = 0;
        int k = 6;

        try {
            WritableFont style1font = new WritableFont(WritableFont.COURIER, 13, WritableFont.BOLD, true);
            WritableCellFormat style1 = new WritableCellFormat(style1font);
            style1.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            style1.setAlignment(Alignment.CENTRE);
            style1.setBackground(Colour.BRIGHT_GREEN);
            WritableFont style2font = new WritableFont(WritableFont.COURIER, 12, WritableFont.BOLD, true);
            WritableCellFormat style2 = new WritableCellFormat(style2font);
            style2.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            style2.setAlignment(Alignment.CENTRE);
            style2.setBackground(Colour.GOLD);
            WritableFont style3font = new WritableFont(WritableFont.COURIER, 11, WritableFont.BOLD, true);
            WritableCellFormat style3 = new WritableCellFormat(style3font);
            style3.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            style3.setAlignment(Alignment.CENTRE);
            style3.setBackground(Colour.GOLD);

            WritableCellFormat style4 = new WritableCellFormat(style3font);
            style4.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            style4.setAlignment(Alignment.CENTRE);
            style4.setBackground(Colour.GRAY_25);

            Label label = new Label(0, 0, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.mainheading", arg), style1);
            _worksheetInv.addCell(label);
            label = new Label(0, 1, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.inventReport", arg), style2);
            _worksheetInv.addCell(label);
            label = new Label(0, 2, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.date", arg), style3);
            _worksheetInv.addCell(label);
            label = new Label(1, 2, BTSLUtil.getDateStringFromDate(p_date), style3);
            _worksheetInv.addCell(label);
            label = new Label(3, 2, "", style3);
            _worksheetInv.addCell(label);

            _worksheetInv.mergeCells(0, 0, 22, 0);
            _worksheetInv.mergeCells(0, 1, 22, 1);
            _worksheetInv.mergeCells(1, 2, 2, 2);
            _worksheetInv.mergeCells(3, 2, 22, 2);

            label = new Label(0, 5, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.productname", arg), style3);
            _worksheetInv.mergeCells(0, 5, 1, 5);
            _worksheetInv.addCell(label);
            label = new Label(2, 5, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.genlabel", arg), style3);
            _worksheetInv.mergeCells(2, 5, 4, 5);
            _worksheetInv.addCell(label);
            label = new Label(5, 5, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.enablabel", arg), style3);
            _worksheetInv.mergeCells(5, 5, 7, 5);
            _worksheetInv.addCell(label);
            label = new Label(8, 5, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.onholdlabel", arg), style3);
            _worksheetInv.mergeCells(8, 5, 10, 5);
            _worksheetInv.addCell(label);
            label = new Label(11, 5, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.rchlabel", arg), style3);
            _worksheetInv.mergeCells(11, 5, 13, 5);
            _worksheetInv.addCell(label);
            label = new Label(14, 5, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.stdalabel", arg), style3);
            _worksheetInv.mergeCells(14, 5, 16, 5);
            _worksheetInv.addCell(label);
            label = new Label(17, 5, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.stdaafterlabel", arg), style3);
            _worksheetInv.mergeCells(17, 5, 19, 5);
            _worksheetInv.addCell(label);
            label = new Label(20, 5, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.reconcilelabel", arg), style3);
            _worksheetInv.mergeCells(20, 5, 22, 5);
            _worksheetInv.addCell(label);

            voucherSummList = getVoucherSummInfoLoc(p_con, p_date, noOfdays);
            voucharchive = getVoucherArchiveInfoLoc(p_con, p_date);
            dailySummList = getVoucherSummaryInfoLoc(p_con, p_date);

            for (int j = 0; j < voucherSummList.size(); j++) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("writeInventoryInfo", " :: voucherSummList.size==" + voucherSummList.size());
                }
                if (_logger.isDebugEnabled()) {
                    _logger.debug("writeInventoryInfo", " :: voucharchive.size==" + voucharchive.size());
                }
                if (_logger.isDebugEnabled()) {
                    _logger.debug("writeInventoryInfo", " :: dailySummList.size==" + dailySummList.size());
                }

                currentSummVO = (VomsVoucherSummaryVO) voucherSummList.get(j);
                archiveSummVO = (VomsVoucherSummaryVO) voucharchive.get(j);
                dailySummVO = (VomsVoucherSummaryVO) dailySummList.get(j);

                if (k == 6) {
                    label = new Label(0, 6, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.prodnamelabel", arg), style4);
                    _worksheetInv.addCell(label);
                    label = new Label(1, 6, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.prodnamemrplabel", arg), style4);
                    _worksheetInv.addCell(label);
                    label = new Label(2, 6, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.todaystotallabel", arg), style4);
                    _worksheetInv.addCell(label);
                    label = new Label(3, 6, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.availballabel", arg), style4);
                    _worksheetInv.addCell(label);
                    label = new Label(4, 6, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.availballmrplabel", arg), style4);
                    _worksheetInv.addCell(label);
                    label = new Label(5, 6, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.todaystotallabel", arg), style4);
                    _worksheetInv.addCell(label);
                    label = new Label(6, 6, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.availballabel", arg), style4);
                    _worksheetInv.addCell(label);
                    label = new Label(7, 6, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.availballmrplabel", arg), style4);
                    _worksheetInv.addCell(label);
                    label = new Label(8, 6, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.todaystotallabel", arg), style4);
                    _worksheetInv.addCell(label);
                    label = new Label(9, 6, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.availballabel", arg), style4);
                    _worksheetInv.addCell(label);
                    label = new Label(10, 6, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.availballmrplabel", arg), style4);
                    _worksheetInv.addCell(label);
                    label = new Label(11, 6, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.todaystotallabel", arg), style4);
                    _worksheetInv.addCell(label);
                    label = new Label(12, 6, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.totalballabel", arg), style4);
                    _worksheetInv.addCell(label);
                    label = new Label(13, 6, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.availballmrplabel", arg), style4);
                    _worksheetInv.addCell(label);
                    label = new Label(14, 6, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.todaystotallabel", arg), style4);
                    _worksheetInv.addCell(label);
                    label = new Label(15, 6, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.totalballabel", arg), style4);
                    _worksheetInv.addCell(label);
                    label = new Label(16, 6, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.availballmrplabel", arg), style4);
                    _worksheetInv.addCell(label);
                    label = new Label(17, 6, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.todaystotallabel", arg), style4);
                    _worksheetInv.addCell(label);
                    label = new Label(18, 6, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.totalballabel", arg), style4);
                    _worksheetInv.addCell(label);
                    label = new Label(19, 6, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.availballmrplabel", arg), style4);
                    _worksheetInv.addCell(label);
                    label = new Label(20, 6, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.todaystotallabel", arg), style4);
                    _worksheetInv.addCell(label);
                    label = new Label(21, 6, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.totalballabel", arg), style4);
                    _worksheetInv.addCell(label);
                    label = new Label(22, 6, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.availballmrplabel", arg), style4);
                    _worksheetInv.addCell(label);
                }
                k = k + 1;

                label = new Label(0, k, currentSummVO.getProductName());
                _worksheetInv.addCell(label);
                label = new Label(1, k, currentSummVO.getMrpStr());
                _worksheetInv.addCell(label);

                try {
                    productMrp = Double.parseDouble(currentSummVO.getMrpStr());
                } catch (Exception e) {
                    _logger.errorTrace(METHOD_NAME, e);
                    productMrp = 1;
                }
                label = new Label(2, k, String.valueOf(formatLongValue(dailySummVO.getTotalGenerated())));
                _worksheetInv.addCell(label);

                availGeneratedCount = currentSummVO.getTotalGenerated() - archiveSummVO.getTotalGenerated();
                // WHETHER TO SHOW AFTER SUBTRACTION ARCHIVE OR NOT
                label = new Label(3, k, formatLongValue(availGeneratedCount));
                _worksheetInv.addCell(label);

                label = new Label(4, k, formatDoubleValue(availGeneratedCount * productMrp, 2));
                _worksheetInv.addCell(label);

                todaysTotalGeCount = todaysTotalGeCount + dailySummVO.getTotalGenerated();
                availGeneratedMrp = availGeneratedMrp + (availGeneratedCount * productMrp);
                totalGeneratedCount = totalGeneratedCount + availGeneratedCount;

                label = new Label(5, k, formatLongValue(dailySummVO.getTotalEnabled()));
                _worksheetInv.addCell(label);

                availEnableCount = currentSummVO.getTotalEnabled() - archiveSummVO.getTotalEnabled();
                if (availEnableCount > currentSummVO.getMaxReqQuantity()) {
                    _bodyPart = _bodyPart + "<tr bgcolor=\"#FFFFFF\"><td width=\"33%\"><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"1\">";
                    _bodyPart = _bodyPart + BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.emailenavaillabel", arg) + "</font></td><td width=\"38%\"><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"1\">" + currentSummVO.getProductName() + "</font></td><td width=\"29%\"><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"1\">" + availEnableCount + "</font></td></tr>";
                    _bodyPart = _bodyPart + "AAAA" + "</font></td><td width=\"38%\"><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"1\">" + currentSummVO.getProductName() + "</font></td><td width=\"29%\"><font face=\"Verdana, Arial, Helvetica, sans-serif\" size=\"1\">" + availEnableCount + "</font></td></tr>";
                    _bodyPart = _bodyPart + "\r\n" + BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.emailenarchlabel", arg) + currentSummVO.getProductName() + "=" + archiveSummVO.getTotalEnabled();
                }

                label = new Label(6, k, formatLongValue(availEnableCount));
                _worksheetInv.addCell(label);

                if (availEnableCount < currentSummVO.getMinReqQuantity()) {
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "CummulativeInventoryRpt", "", "", "", " Message: Total Available EN Count is below the limit(" + archiveSummVO.getMinReqQuantity() + " for " + archiveSummVO.getProductName());
                }
                label = new Label(7, k, formatDoubleValue(availEnableCount * productMrp, 2));
                _worksheetInv.addCell(label);

                totalEnableCount = totalEnableCount + availEnableCount;
                availEnableMrp = availEnableMrp + (availEnableCount * productMrp);
                todaysTotalEnCount = todaysTotalEnCount + dailySummVO.getTotalEnabled();

                label = new Label(8, k, formatLongValue(dailySummVO.getTotalOnHold()));
                _worksheetInv.addCell(label);

                availOnHoldCount = currentSummVO.getTotalOnHold() - archiveSummVO.getTotalOnHold();

                label = new Label(9, k, formatLongValue(availOnHoldCount));
                _worksheetInv.addCell(label);

                label = new Label(10, k, formatDoubleValue((availOnHoldCount * productMrp), 2));
                _worksheetInv.addCell(label);

                totalOnHoldCount = totalOnHoldCount + availOnHoldCount;
                availOnHoldMrp = availOnHoldMrp + (availOnHoldCount * productMrp);
                todaysTotalOholdCount = todaysTotalOholdCount + dailySummVO.getTotalOnHold();

                label = new Label(11, k, formatLongValue(dailySummVO.getTotalRecharged()));
                _worksheetInv.addCell(label);

                availRechargeCount = currentSummVO.getTotalRecharged() - archiveSummVO.getTotalRecharged();
                label = new Label(12, k, formatLongValue(availRechargeCount));
                _worksheetInv.addCell(label);

                label = new Label(13, k, formatDoubleValue(availRechargeCount * productMrp, 2));
                _worksheetInv.addCell(label);

                totalRechargeCount = totalRechargeCount + availRechargeCount;
                availRechargeMrp = availRechargeMrp + (availRechargeCount * productMrp);
                todaysTotalRecCount = todaysTotalRecCount + dailySummVO.getTotalRecharged();

                label = new Label(14, k, formatLongValue(dailySummVO.getTotalStolenDmg()));
                _worksheetInv.addCell(label);

                availStolenCount = currentSummVO.getTotalStolenDmg() - archiveSummVO.getTotalStolenDmg();
                label = new Label(15, k, formatLongValue(availStolenCount));
                _worksheetInv.addCell(label);

                label = new Label(16, k, formatDoubleValue(availStolenCount * productMrp, 2));
                _worksheetInv.addCell(label);

                totalStolenCount = totalStolenCount + availStolenCount;
                availStolenMrp = availStolenMrp + (availStolenCount * productMrp);
                todaysTotalStDaCount = todaysTotalStDaCount + dailySummVO.getTotalStolenDmg();

                label = new Label(17, k, formatLongValue(dailySummVO.getTotalStolenDmgAfterEn()));
                _worksheetInv.addCell(label);

                availDamageCount = currentSummVO.getTotalStolenDmgAfterEn() - archiveSummVO.getTotalStolenDmgAfterEn();

                label = new Label(18, k, formatLongValue(availDamageCount));
                _worksheetInv.addCell(label);

                label = new Label(19, k, formatDoubleValue(availDamageCount * productMrp, 2));
                _worksheetInv.addCell(label);

                totalDamageCount = totalDamageCount + availDamageCount;
                availDamageMrp = availDamageMrp + (availDamageCount * productMrp);
                todaysTotalStDaEnCount = todaysTotalStDaEnCount + dailySummVO.getTotalStolenDmgAfterEn();

                label = new Label(20, k, formatLongValue(dailySummVO.getTotalReconciled()));
                _worksheetInv.addCell(label);

                availReconcileCount = currentSummVO.getTotalReconciled() - archiveSummVO.getTotalReconciled();
                label = new Label(21, k, formatLongValue(availReconcileCount));
                _worksheetInv.addCell(label);

                label = new Label(22, k, formatDoubleValue(availReconcileCount * productMrp, 2));
                _worksheetInv.addCell(label);

                totalReconcileCount = totalReconcileCount + availReconcileCount;
                availReconcileMrp = availReconcileMrp + (availReconcileCount * productMrp);
                todaysTotalReconcileCount = todaysTotalReconcileCount + dailySummVO.getTotalReconciled();
            }
            k = k + 1;
            label = new Label(0, k, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.totallabel", arg), style4);
            _worksheetInv.mergeCells(0, k, 1, k);
            _worksheetInv.addCell(label);
            label = new Label(2, k, formatLongValue(todaysTotalGeCount), style4);
            _worksheetInv.addCell(label);
            label = new Label(3, k, formatLongValue(totalGeneratedCount), style4);
            _worksheetInv.addCell(label);
            label = new Label(4, k, formatDoubleValue(availGeneratedMrp, 2), style4);
            _worksheetInv.addCell(label);
            label = new Label(5, k, formatLongValue(todaysTotalEnCount), style4);
            _worksheetInv.addCell(label);
            label = new Label(6, k, formatLongValue(totalEnableCount), style4);
            _worksheetInv.addCell(label);
            label = new Label(7, k, formatDoubleValue(availEnableMrp, 2), style4);
            _worksheetInv.addCell(label);
            label = new Label(8, k, formatLongValue(todaysTotalOholdCount), style4);
            _worksheetInv.addCell(label);
            label = new Label(9, k, formatLongValue(totalOnHoldCount), style4);
            _worksheetInv.addCell(label);
            label = new Label(10, k, formatDoubleValue(availOnHoldMrp, 2), style4);
            _worksheetInv.addCell(label);
            label = new Label(11, k, formatLongValue(todaysTotalRecCount), style4);
            _worksheetInv.addCell(label);
            label = new Label(12, k, formatLongValue(totalRechargeCount), style4);
            _worksheetInv.addCell(label);
            label = new Label(13, k, formatDoubleValue(availRechargeMrp, 2), style4);
            _worksheetInv.addCell(label);
            label = new Label(14, k, formatLongValue(todaysTotalStDaCount), style4);
            _worksheetInv.addCell(label);
            label = new Label(15, k, formatLongValue(totalStolenCount), style4);
            _worksheetInv.addCell(label);
            label = new Label(16, k, formatDoubleValue(availStolenMrp, 2), style4);
            _worksheetInv.addCell(label);
            label = new Label(17, k, formatLongValue(todaysTotalStDaEnCount), style4);
            _worksheetInv.addCell(label);
            label = new Label(18, k, formatLongValue(totalDamageCount), style4);
            _worksheetInv.addCell(label);
            label = new Label(19, k, formatDoubleValue(availDamageMrp, 2), style4);
            _worksheetInv.addCell(label);
            label = new Label(20, k, formatLongValue(todaysTotalReconcileCount), style4);
            _worksheetInv.addCell(label);
            label = new Label(21, k, formatLongValue(totalReconcileCount), style4);
            _worksheetInv.addCell(label);
            label = new Label(22, k, formatDoubleValue(availReconcileMrp, 2), style4);
            _worksheetInv.addCell(label);

            k = k + 2;
            String time = "";
            try {
                time = BTSLUtil.getTimeinHHMM(_currentDate.getHours(), _currentDate.getMinutes());
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
                time = "";
            }
            label = new Label(0, k, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet0.generatedon", arg) + BTSLDateUtil.getSystemLocaleDate(_reportTo) + " " + time, style3);
            _worksheetInv.addCell(label);
            _worksheetInv.mergeCells(0, k, 3, k);
        } catch (RowsExceededException e) {
            _logger.errorTrace(METHOD_NAME, e);
            if (_logger.isDebugEnabled()) {
                _logger.debug("writeInventoryInfo", " :: SQLException : " + e.getMessage());
            }
            throw e;
        } catch (WriteException e) {
            _logger.errorTrace(METHOD_NAME, e);
            if (_logger.isDebugEnabled()) {
                _logger.debug("writeInventoryInfo", " :: SQLException : " + e.getMessage());
            }
            throw e;
        }
    }

    /**
     * This method gets the counts from voucher summary for the last day.
     * 
     * @param p_con
     * @param p_createdDate
     * @return ArrayList
     */
    private static ArrayList getVoucherSummaryInfoLoc(Connection p_con, java.sql.Date p_createdDate) throws BTSLBaseException {
        final String METHOD_NAME = "getVoucherSummaryInfoLoc";
        if (_logger.isDebugEnabled()) {
            _logger.debug("getVoucherSummaryInfoLoc", " :: Entered with p_createdDate=" + p_createdDate);
        }

        PreparedStatement psmt = null;
        ResultSet rs = null;
        VomsVoucherSummaryVO vomsVoucherSummaryVO = null;
        java.util.ArrayList summaryList = null;


        CummulativeInventoryAmtReportQry cummulativeInventoryAmtReportQry = (CummulativeInventoryAmtReportQry)
        		ObjectProducer.getObject(QueryConstants.COMMULATIVE_INVENTORY_AMT_REPORT_QRY, QueryConstants.QUERY_PRODUCER);

       final String strBuff =  cummulativeInventoryAmtReportQry.getVoucherSummaryInfoLocQry();
        
        try {
            if (_logger.isDebugEnabled()) {
                _logger.debug("getVoucherSummaryInfoLoc", " :: Query :: " + strBuff.toString());
            }
            psmt = p_con.prepareStatement(strBuff.toString());
            psmt.setDate(1, p_createdDate);
            rs = psmt.executeQuery();
            if (rs != null) {
                summaryList = new java.util.ArrayList();
            }
            psmt.clearParameters();
            while (rs.next()) {
                vomsVoucherSummaryVO = new VomsVoucherSummaryVO();
                vomsVoucherSummaryVO.setProductID(rs.getString("productid"));
                vomsVoucherSummaryVO.setProductName(rs.getString("PRODUCTNAME"));
                vomsVoucherSummaryVO.setMrpStr(PretupsBL.getDisplayAmount(rs.getLong("MRP")));
                vomsVoucherSummaryVO.setMinReqQuantity(rs.getLong("MIN_REQ_QUANTITY"));
                vomsVoucherSummaryVO.setMaxReqQuantity(rs.getLong("MAX_REQ_QUANTITY"));
                vomsVoucherSummaryVO.setTotalGenerated(rs.getLong("total_generated"));
                vomsVoucherSummaryVO.setTotalEnabled(rs.getLong("total_enabled"));
                vomsVoucherSummaryVO.setTotalRecharged(rs.getLong("total_recharged"));
                vomsVoucherSummaryVO.setTotalOnHold(rs.getLong("total_hold"));
                vomsVoucherSummaryVO.setTotalStolenDmg(rs.getLong("total_st_da_before"));
                vomsVoucherSummaryVO.setTotalStolenDmgAfterEn(rs.getLong("total_st_da_after"));
                vomsVoucherSummaryVO.setTotalReconciled(rs.getInt("total_reconciled"));

                summaryList.add(vomsVoucherSummaryVO);
            }
        } catch (SQLException sqe) {
            _logger.errorTrace(METHOD_NAME, sqe);
            if (_logger.isDebugEnabled()) {
                _logger.debug("getVoucherSummaryInfoLoc", " :: SQLException : " + sqe.getMessage());
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CummulativeInventoryAmtReport[getVoucherSummaryInfoLoc]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException("CummulativeInventoryAmtReport", "getVoucherSummaryInfoLoc", PretupsErrorCodesI.VOMS_INVRPT_ERROR_EXCEPTION_VOSUMMINFO);
        } catch (Exception ex) {
            _logger.errorTrace(METHOD_NAME, ex);
            if (_logger.isDebugEnabled()) {
                _logger.debug("getVoucherSummaryInfoLoc", " :: Exception : " + ex.getMessage());
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CummulativeInventoryAmtReport[getVoucherSummaryInfoLoc]", "", "", "", "SQL Exception:" + ex.getMessage());
            throw new BTSLBaseException("CummulativeInventoryAmtReport", "getVoucherSummaryInfoLoc", PretupsErrorCodesI.VOMS_INVRPT_ERROR_EXCEPTION_VOSUMMINFO);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("getVoucherSummaryInfoLoc", " :: Exiting : summaryList size = " + summaryList.size());
            }
        }
        return summaryList;
    }

    /**
     * This method gets the batch List for a particular location.
     * 
     * @param p_con
     * @param p_createdDate
     * @return ArrayList
     */
    private static ArrayList getBatchListForLoc(Connection p_con, java.sql.Date p_createdDate) throws BTSLBaseException {
        final String METHOD_NAME = "getBatchListForLoc";
        if (_logger.isDebugEnabled()) {
            _logger.debug("getBatchListForLoc", " :: Entered with p_createdDate=" + p_createdDate);
        }
        PreparedStatement psmt = null;
        ResultSet rs = null;
        VomsBatchVO vomsBatchVO = null;
        java.util.ArrayList batchList = null;
        batchList = new java.util.ArrayList();

     
        CummulativeInventoryAmtReportQry cummulativeInventoryAmtReportQry = (CummulativeInventoryAmtReportQry)
        		ObjectProducer.getObject(QueryConstants.COMMULATIVE_INVENTORY_AMT_REPORT_QRY, QueryConstants.QUERY_PRODUCER);
        final String strBuff = cummulativeInventoryAmtReportQry.getBatchListForLocQry();
        
        try {
            if (_logger.isDebugEnabled()) {
                _logger.debug("getBatchListForLoc", " :: Query :: " + strBuff.toString());
            }
            psmt = p_con.prepareStatement(strBuff.toString());
            psmt.setDate(1, p_createdDate);
            rs = psmt.executeQuery();
            if (rs != null) {
                batchList = new java.util.ArrayList();
            }
            psmt.clearParameters();
            while (rs.next()) {
                vomsBatchVO = new VomsBatchVO();
                vomsBatchVO.setProductID(rs.getString("PRODUCTID"));
                vomsBatchVO.setProductName(rs.getString("PRODUCTNAME"));
                vomsBatchVO.setBatchType(rs.getString("BATCHTYPE"));
                vomsBatchVO.setNoOfVoucher(rs.getLong("TOTALVOUCHER"));
                vomsBatchVO.setBatchNo("" + rs.getInt("NOOFBATCH"));
                vomsBatchVO.setFailCount(rs.getLong("FAILCOUNT"));
                vomsBatchVO.setSuccessCount(rs.getLong("SUCCCOUNT"));
                vomsBatchVO.setDownloadCount(rs.getInt("COUNT"));
                vomsBatchVO.setExecuteCount(rs.getInt("EXCOUNT"));
                vomsBatchVO.setFailureCount(rs.getInt("FACOUNT"));
                vomsBatchVO.setScheduleCount(rs.getInt("SCHCOUNT"));
                vomsBatchVO.setMrp(PretupsBL.getDisplayAmount(rs.getLong("MRP")));
                batchList.add(vomsBatchVO);

            }
        } catch (SQLException sqe) {
            _logger.errorTrace(METHOD_NAME, sqe);
            if (_logger.isDebugEnabled()) {
                _logger.debug("getBatchListForLoc", " :: SQLException : " + sqe.getMessage());
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CummulativeInventoryAmtReport[getBatchListForLoc]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException("CummulativeInventoryAmtReport", "getBatchListForLoc", PretupsErrorCodesI.VOMS_INVRPT_ERROR_EXCEPTION_VOBATCHLIST);
        } catch (Exception ex) {
            _logger.errorTrace(METHOD_NAME, ex);
            if (_logger.isDebugEnabled()) {
                _logger.debug("getBatchListForLoc", " :: Exception : " + ex.getMessage());
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CummulativeInventoryAmtReport[getBatchListForLoc]", "", "", "", "SQL Exception:" + ex.getMessage());
            throw new BTSLBaseException("CummulativeInventoryAmtReport", "getBatchListForLoc", PretupsErrorCodesI.VOMS_INVRPT_ERROR_EXCEPTION_VOBATCHLIST);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("getBatchListForLoc", " :: Exiting : batchList size = " + batchList.size());
            }
        }
        return batchList;
    }

    /**
     * This method writes the voucher and batch info. on the excel file.
     * 
     * @param p_con
     * @param p_date
     *            java.sql.Date
     * @param p_reportHeading
     *            String
     * @return void
     */
    private static void writeBatchVoucherInfo(Connection p_con, java.sql.Date p_date) throws Exception {
        final String METHOD_NAME = "writeBatchVoucherInfo";
        VomsBatchVO vomsBatchVO = null;
        ArrayList batchList = null;
        int k = 0;
        int enableCount = 0;
        int rechargeCount = 0;
        int OnHoldCount = 0;
        int reconcileCount = 0;
        int stolenCount = 0;
        int damageCount = 0;
        String voucherNetworkCode = null;
        UserVO userVO = null;
        try {
            WritableFont style1font = new WritableFont(WritableFont.COURIER, 13, WritableFont.BOLD, true);
            WritableCellFormat style1 = new WritableCellFormat(style1font);
            style1.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            style1.setAlignment(Alignment.CENTRE);
            style1.setBackground(Colour.BRIGHT_GREEN);
            WritableFont style2font = new WritableFont(WritableFont.COURIER, 12, WritableFont.BOLD, true);
            WritableCellFormat style2 = new WritableCellFormat(style2font);
            style2.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            style2.setAlignment(Alignment.CENTRE);
            style2.setBackground(Colour.GOLD);
            WritableFont style3font = new WritableFont(WritableFont.COURIER, 11, WritableFont.BOLD, true);
            WritableCellFormat style3 = new WritableCellFormat(style3font);
            style3.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            style3.setAlignment(Alignment.CENTRE);
            style3.setBackground(Colour.GOLD);

            WritableCellFormat style4 = new WritableCellFormat(style3font);
            style4.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            style4.setAlignment(Alignment.CENTRE);
            style4.setBackground(Colour.GRAY_25);

            Label label = new Label(0, 0, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.mainheading", arg), style1);
            _worksheetAct.addCell(label);
            label = new Label(0, 1, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.actReport", arg), style2);
            _worksheetAct.addCell(label);
            label = new Label(0, 2, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.date", arg), style3);
            _worksheetAct.addCell(label);
            label = new Label(1, 2, BTSLUtil.getDateStringFromDate(p_date), style3);
            _worksheetAct.addCell(label);
            label = new Label(3, 2, "", style3);
            _worksheetAct.addCell(label);

            _worksheetAct.mergeCells(0, 0, 8, 0);
            _worksheetAct.mergeCells(0, 1, 8, 1);
            _worksheetAct.mergeCells(1, 2, 2, 2);
            _worksheetAct.mergeCells(3, 2, 8, 2);

            label = new Label(0, 5, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.creatbatchlabel", arg));
            _worksheetAct.addCell(label);
            _worksheetAct.mergeCells(0, 5, 4, 5);

            label = new Label(0, 6, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.productslabel", arg), style3);
            _worksheetAct.mergeCells(0, 6, 1, 6);
            _worksheetAct.addCell(label);
            label = new Label(2, 6, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.batcheslabel", arg), style3);
            _worksheetAct.mergeCells(2, 6, 5, 6);
            _worksheetAct.addCell(label);
            label = new Label(6, 6, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.voucherslabel", arg), style3);
            _worksheetAct.mergeCells(6, 6, 8, 6);
            _worksheetAct.addCell(label);

            k = 7;
            
            ArrayList productList = null;
            VomsProductVO productVO = null;

            String batchType = null;
            String batchTypeName = null;
            //userVO = this.getUserFormSession(request);
            voucherNetworkCode =  userVO.getNetworkID();
            try {
                productList = new VomsProductDAO().loadProductDetailsList(p_con, "'" + VOMSI.ALL + "'", true, null, null);
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
                productList = new ArrayList();
            }
            batchList = getBatchListForLoc(p_con, p_date);
            if (batchList != null && batchList.size() > 0) {
                for (int j = 0; j < batchList.size(); j++) {
                    vomsBatchVO = (VomsBatchVO) batchList.get(j);
                    batchType = null;
                    batchTypeName = null;

                    if (k == 7) {
                        label = new Label(0, k, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.prodnamelabel", arg), style4);
                        _worksheetAct.addCell(label);
                        label = new Label(1, k, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.prodmrplabel", arg), style4);
                        _worksheetAct.addCell(label);
                        label = new Label(2, k, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.batchtypelabel", arg), style4);
                        _worksheetAct.addCell(label);
                        label = new Label(3, k, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.batchtotallabel", arg), style4);
                        _worksheetAct.addCell(label);
                        label = new Label(4, k, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.batchsucclabel", arg), style4);
                        _worksheetAct.addCell(label);
                        label = new Label(5, k, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.batchfaillabel", arg), style4);
                        _worksheetAct.addCell(label);
                        label = new Label(6, k, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.vouchertotlabel", arg), style4);
                        _worksheetAct.addCell(label);
                        label = new Label(7, k, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.vouchersucclabel", arg), style4);
                        _worksheetAct.addCell(label);
                        label = new Label(8, k, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.voucherfaillabel", arg), style4);
                        _worksheetAct.addCell(label);
                    }

                    k = k + 1;
                    label = new Label(0, k, vomsBatchVO.getProductName());
                    _worksheetAct.addCell(label);
                    label = new Label(1, k, vomsBatchVO.getMrp());
                    _worksheetAct.addCell(label);

                    batchType = vomsBatchVO.getBatchType();
                    if (batchType.equalsIgnoreCase(VOMSI.BATCH_GENERATED)) {
                        batchTypeName = BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.vgenerated", arg);
                    } else if (batchType.equalsIgnoreCase(VOMSI.BATCH_ENABLED)) {
                        batchTypeName = BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.venabled", arg);
                    } else if (batchType.equalsIgnoreCase(VOMSI.BATCH_ONHOLD)) {
                        batchTypeName = BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.vonhold", arg);
                    } else if (batchType.equalsIgnoreCase(VOMSI.BATCH_STOLEN)) {
                        batchTypeName = BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.vstolen", arg);
                    } else if (batchType.equalsIgnoreCase(VOMSI.BATCH_DAMAGED)) {
                        batchTypeName = BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.vdamaged", arg);
                    } else if (batchType.equalsIgnoreCase(VOMSI.BATCHCONSUMESTAT)) {
                        batchTypeName = BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.vconsumed", arg);
                    } else {
                        batchTypeName = "N.A.";
                    }

                    label = new Label(2, k, batchTypeName);
                    _worksheetAct.addCell(label);
                    label = new Label(3, k, formatLongStrValue(vomsBatchVO.getBatchNo()));
                    _worksheetAct.addCell(label);
                    label = new Label(4, k, formatLongValue(vomsBatchVO.getExecuteCount()));
                    _worksheetAct.addCell(label);
                    label = new Label(5, k, formatLongValue(vomsBatchVO.getFailureCount()));
                    _worksheetAct.addCell(label);
                    label = new Label(6, k, formatLongValue(vomsBatchVO.getNoOfVoucher()));
                    _worksheetAct.addCell(label);
                    label = new Label(7, k, formatLongValue(vomsBatchVO.getSuccessCount()));
                    _worksheetAct.addCell(label);
                    label = new Label(8, k, formatLongValue(vomsBatchVO.getFailCount()));
                    _worksheetAct.addCell(label);
                }
            } else {
                k = k + 1;
                label = new Label(8, k, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.nobatcheslabel", arg));
                _worksheetAct.addCell(label);
                _worksheetAct.mergeCells(0, k, 8, k);
            }

            k = k + 2;
            label = new Label(0, k, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.vostatuschglabel", arg));
            _worksheetAct.addCell(label);
            _worksheetAct.mergeCells(0, k, 2, k);

            k = k + 1;
            label = new Label(0, k, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.prodnamelabel", arg), style4);
            _worksheetAct.addCell(label);
            label = new Label(1, k, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.fromstatlabel", arg), style4);
            _worksheetAct.addCell(label);
            label = new Label(2, k, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.tostatlabel", arg), style4);
            _worksheetAct.addCell(label);
            label = new Label(3, k, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.noodvouchlabel", arg), style4);
            _worksheetAct.addCell(label);

            for (int i = 0; i < productList.size(); i++) {
                productVO = (VomsProductVO) productList.get(i);

                enableCount = getVoucherCount(p_con, productVO.getProductID(), VOMSI.VOUCHER_NEW, VOMSI.VOUCHER_ENABLE, p_date);

                k = k + 1;
                label = new Label(0, k, productVO.getProductName());
                _worksheetAct.addCell(label);
                label = new Label(1, k, BTSLUtil.getMessage(_locale, "voms.project.status.vgenerated", arg));
                _worksheetAct.addCell(label);
                label = new Label(2, k, BTSLUtil.getMessage(_locale, "voms.project.status.venabled", arg));
                _worksheetAct.addCell(label);
                label = new Label(3, k, formatLongValue(enableCount));
                _worksheetAct.addCell(label);

                OnHoldCount = getVoucherCount(p_con, productVO.getProductID(), VOMSI.VOUCHER_ENABLE, VOMSI.VOUCHER_ON_HOLD, p_date);
                k = k + 1;
                label = new Label(1, k, BTSLUtil.getMessage(_locale, "voms.project.status.venabled", arg));
                _worksheetAct.addCell(label);
                label = new Label(2, k, BTSLUtil.getMessage(_locale, "voms.project.status.vonhold", arg));
                _worksheetAct.addCell(label);
                label = new Label(3, k, formatLongValue(OnHoldCount));
                _worksheetAct.addCell(label);

                enableCount = 0;
                enableCount = getVoucherCount(p_con, productVO.getProductID(), VOMSI.VOUCHER_ON_HOLD, VOMSI.VOUCHER_ENABLE, p_date);
                k = k + 1;
                label = new Label(1, k, BTSLUtil.getMessage(_locale, "voms.project.status.vonhold", arg));
                _worksheetAct.addCell(label);
                label = new Label(2, k, BTSLUtil.getMessage(_locale, "voms.project.status.venabled", arg));
                _worksheetAct.addCell(label);
                label = new Label(3, k, formatLongValue(enableCount));
                _worksheetAct.addCell(label);

                stolenCount = getVoucherCount(p_con, productVO.getProductID(), VOMSI.VOUCHER_ENABLE, VOMSI.VOUCHER_STOLEN, p_date);
                damageCount = getVoucherCount(p_con, productVO.getProductID(), VOMSI.VOUCHER_ENABLE, VOMSI.VOUCHER_DAMAGED, p_date);
                int upDamageCount = getVoucherCount(p_con, productVO.getProductID(), VOMSI.VOUCHER_UNPROCESS, VOMSI.VOUCHER_DAMAGED, p_date);
                int upStolenCount = getVoucherCount(p_con, productVO.getProductID(), VOMSI.VOUCHER_UNPROCESS, VOMSI.VOUCHER_STOLEN, p_date);
                k = k + 1;
                label = new Label(1, k, BTSLUtil.getMessage(_locale, "voms.project.status.venabled", arg));
                _worksheetAct.addCell(label);
                label = new Label(2, k, BTSLUtil.getMessage(_locale, "voms.project.status.vstoldama", arg));
                _worksheetAct.addCell(label);
                label = new Label(3, k, formatLongValue(stolenCount + damageCount + upDamageCount + upStolenCount));
                _worksheetAct.addCell(label);

                stolenCount = 0;
                stolenCount = getVoucherCount(p_con, productVO.getProductID(), VOMSI.VOUCHER_ON_HOLD, VOMSI.VOUCHER_STOLEN, p_date);
                damageCount = 0;
                damageCount = getVoucherCount(p_con, productVO.getProductID(), VOMSI.VOUCHER_ON_HOLD, VOMSI.VOUCHER_DAMAGED, p_date);
                k = k + 1;
                label = new Label(1, k, BTSLUtil.getMessage(_locale, "voms.project.status.vonhold", arg));
                _worksheetAct.addCell(label);
                label = new Label(2, k, BTSLUtil.getMessage(_locale, "voms.project.status.vstoldama", arg));
                _worksheetAct.addCell(label);
                label = new Label(3, k, formatLongValue(stolenCount + damageCount));
                _worksheetAct.addCell(label);

                stolenCount = 0;
                stolenCount = getVoucherCount(p_con, productVO.getProductID(), VOMSI.VOUCHER_NEW, VOMSI.VOUCHER_STOLEN, p_date);
                damageCount = 0;
                damageCount = getVoucherCount(p_con, productVO.getProductID(), VOMSI.VOUCHER_NEW, VOMSI.VOUCHER_DAMAGED, p_date);
                k = k + 1;
                label = new Label(1, k, BTSLUtil.getMessage(_locale, "voms.project.status.vgenerated", arg));
                _worksheetAct.addCell(label);
                label = new Label(2, k, BTSLUtil.getMessage(_locale, "voms.project.status.vstoldama", arg));
                _worksheetAct.addCell(label);
                label = new Label(3, k, formatLongValue(stolenCount + damageCount));
                _worksheetAct.addCell(label);

                reconcileCount = getVoucherCount(p_con, productVO.getProductID(), VOMSI.VOUCHER_UNPROCESS, VOMSI.VOUCHER_RECONCILE, p_date);
                k = k + 1;
                label = new Label(1, k, BTSLUtil.getMessage(_locale, "voms.project.status.venabled", arg));
                _worksheetAct.addCell(label);
                label = new Label(2, k, BTSLUtil.getMessage(_locale, "voms.project.status.vreconcile", arg));
                _worksheetAct.addCell(label);
                label = new Label(3, k, formatLongValue(reconcileCount));
                _worksheetAct.addCell(label);

                rechargeCount = getVoucherCount(p_con, productVO.getProductID(), VOMSI.VOUCHER_UNPROCESS, VOMSI.VOUCHER_USED, p_date);
                k = k + 1;
                label = new Label(1, k, BTSLUtil.getMessage(_locale, "voms.project.status.venabled", arg));
                _worksheetAct.addCell(label);
                label = new Label(2, k, BTSLUtil.getMessage(_locale, "voms.project.status.vconsumed", arg));
                _worksheetAct.addCell(label);
                label = new Label(3, k, formatLongValue(rechargeCount));
                _worksheetAct.addCell(label);

                enableCount = 0;
                enableCount = getVoucherCount(p_con, productVO.getProductID(), VOMSI.VOUCHER_RECONCILE, VOMSI.VOUCHER_ENABLE, BTSLUtil.getSQLDateFromUtilDate(new Date()));
                k = k + 1;
                label = new Label(1, k, BTSLUtil.getMessage(_locale, "voms.project.status.vreconcile", arg));
                _worksheetAct.addCell(label);
                label = new Label(2, k, BTSLUtil.getMessage(_locale, "voms.project.status.venabled", arg));
                _worksheetAct.addCell(label);
                label = new Label(3, k, formatLongValue(enableCount));
                _worksheetAct.addCell(label);

                rechargeCount = 0;
                rechargeCount = getVoucherCount(p_con, productVO.getProductID(), VOMSI.VOUCHER_RECONCILE, VOMSI.VOUCHER_USED, BTSLUtil.getSQLDateFromUtilDate(new Date()));
                k = k + 1;
                label = new Label(1, k, BTSLUtil.getMessage(_locale, "voms.project.status.vreconcile", arg));
                _worksheetAct.addCell(label);
                label = new Label(2, k, BTSLUtil.getMessage(_locale, "voms.project.status.vconsumed", arg));
                _worksheetAct.addCell(label);
                label = new Label(3, k, formatLongValue(rechargeCount));
                _worksheetAct.addCell(label);

                damageCount = 0;
                damageCount = getVoucherCount(p_con, productVO.getProductID(), VOMSI.VOUCHER_RECONCILE, VOMSI.VOUCHER_DAMAGED, BTSLUtil.getSQLDateFromUtilDate(new Date()));
                k = k + 1;
                label = new Label(1, k, BTSLUtil.getMessage(_locale, "voms.project.status.vreconcile", arg));
                _worksheetAct.addCell(label);
                label = new Label(2, k, BTSLUtil.getMessage(_locale, "voms.project.status.vstoldama", arg));
                _worksheetAct.addCell(label);
                label = new Label(3, k, formatLongValue(damageCount));
                _worksheetAct.addCell(label);

                k = k + 1;
            }
            String time = "";
            try {
                time = BTSLUtil.getTimeinHHMM(_currentDate.getHours(), _currentDate.getMinutes());
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
                time = "";
            }
            label = new Label(0, k, BTSLUtil.getMessage(_locale, "dailyreport.inventory.sheet1.generatedon", arg) + BTSLDateUtil.getSystemLocaleDate(_reportTo) + " " + time, style3);
            _worksheetAct.addCell(label);
            _worksheetAct.mergeCells(0, k, 2, k);
        } catch (RowsExceededException e) {
            _logger.errorTrace(METHOD_NAME, e);
            if (_logger.isDebugEnabled()) {
                _logger.debug("writeInventoryInfo", " :: SQLException : " + e.getMessage());
            }
            throw e;
        } catch (WriteException e) {
            _logger.errorTrace(METHOD_NAME, e);
            if (_logger.isDebugEnabled()) {
                _logger.debug("writeInventoryInfo", " :: SQLException : " + e.getMessage());
            }
            throw e;
        }
    }

    /**
     * Formats the values according to Indian Standard
     * 
     * @param number
     * @param afterDecimal
     * @return String
     *         Created On: Nov 9, 2004 3:22:16 PM
     * @author: gurjeet.bedi
     */
    private static String formatDoubleValue(double number, int afterDecimal) {
        final String METHOD_NAME = "formatDoubleValue";
        String result = null;
        double currencyNumber = number;
        try {

            String formatStr = "";
            if (afterDecimal > 0) {
                formatStr = "##,##,##,##,##0.";
            } else {
                formatStr = "##,##,##,##,##0";
            }
            for (int i = 0; i < afterDecimal; i++) {
                formatStr += "#";
            }
            DecimalFormat decFormat = new DecimalFormat(formatStr);
            result = decFormat.format(currencyNumber);
        } catch (Exception ex) {
            _logger.errorTrace(METHOD_NAME, ex);
            _logger.errorTrace("formatDoubleValue", ex);
            result = "" + currencyNumber;
        }
        return result;
    }

    /**
     * Formats the long value in Indian Style
     * 
     * @param number
     * @return String
     *         Created On: Nov 9, 2004 3:24:00 PM
     * @author: gurjeet.bedi
     */
    private static String formatLongValue(long number) {
        final String METHOD_NAME = "formatLongValue";
        String result = null;
        long currencyNumber = number;
        try {

            String formatStr = "##,##,##,##,##0";
            DecimalFormat decFormat = new DecimalFormat(formatStr);
            result = decFormat.format(currencyNumber);
        } catch (Exception ex) {
            _logger.errorTrace(METHOD_NAME, ex);
            _logger.errorTrace("formatLongValue", ex);
            result = "" + currencyNumber;
        }
        return result;
    }

    /**
     * Formats the Long String value
     * 
     * @param number
     * @return String
     *         Created On: Nov 9, 2004 3:27:28 PM
     * @author: gurjeet.bedi
     */
    private static String formatLongStrValue(String number) throws BTSLBaseException {
        final String METHOD_NAME = "formatLongStrValue";
        String result = null;
        String currencyNumber = number;
        try {

            String formatStr = "##,##,##,##,##0";
            DecimalFormat decFormat = new DecimalFormat(formatStr);
            result = decFormat.format(currencyNumber);
        } catch (Exception ex) {
            _logger.errorTrace(METHOD_NAME, ex);
            if (_logger.isDebugEnabled()) {
                _logger.debug("getVoucherSummaryInfoLoc", " :: Exception : " + ex.getMessage());
            }
            result = currencyNumber;
        }
        return result;
    }
}

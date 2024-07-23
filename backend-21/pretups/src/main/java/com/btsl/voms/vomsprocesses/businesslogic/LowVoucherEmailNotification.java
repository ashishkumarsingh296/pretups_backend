package com.btsl.voms.vomsprocesses.businesslogic;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;
import com.btsl.xl.ExcelStyle;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class LowVoucherEmailNotification {

    private static Log _logger = LogFactory.getLog(LowVoucherEmailNotification.class.getName());
    private static ProcessStatusVO _processStatusVO;
    private int col = 0;
    private int row = 3;
    private static Locale _locale = new Locale("en", "US");
    private static String finalFileName = null;
    private ArrayList arrList = new ArrayList();

    public static void main(String arg[]) {
        final String METHOD_NAME = "main";
        try {
            if (arg.length != 2) {
                System.out.println("Usage : LowVoucherEmailNotification [Constants file] [LogConfig file]");
                return;
            }
            File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
                System.out.println("LowVoucherEmailNotification: Constants File Not Found .............");
                return;
            }
            File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                System.out.println("LowVoucherAlert: Logconfig File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());

        }// end of try
        catch (Exception e) {
            System.err.println("Error in Loading Configuration files ...........................: " + e);
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {

            new LowVoucherEmailNotification().process();
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

    private void process() throws BTSLBaseException {
        final String methodName = "process";
        _logger.debug(methodName, " Entered: ");
        Connection con = null;
        String processId = null;
        boolean statusOk = false;
        int maxDoneDateUpdateCount = 0;
        ProcessBL processBL = null;
        ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        ResultSet resultSet = null;
        PreparedStatement psmt = null;
        Date currentDate = null;

        String filePath = null;
        try {
            filePath = Constants.getProperty("VOUCHER_ALERT_MAIL_NOTIFICATION_REPORT_FILE_PATH");
            File fileDir = new File(filePath);
            if (!fileDir.isDirectory()) {
                fileDir.mkdirs();
            }
        } catch (Exception e) {
            _logger.errorTrace(methodName, e);
            _logger.error(methodName, "Exception " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "Unable to create directory =" + filePath, "error");
        }

        try {
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(methodName, " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LowVoucherAlert[process]", "", "", "", "DATABASE Connection is NULL");
                return;
            }
            processId = ProcessI.VOUCHER_ALERT;
            // method call to check status of the process
            processBL = new ProcessBL();
            _processStatusVO = processBL.checkProcessUnderProcess(con, processId);
            statusOk = _processStatusVO.isStatusOkBool();
            // the use of process_status table is only for running one instance,
            // executed_upto has no relevance here.
            if (statusOk) {
                // con.commit();
                StringBuffer strBuff = new StringBuffer("SELECT product_name,COUNT(serial_no) available_vouchers ,min_req_quantity,MRP");
                strBuff.append(" FROM voms_vouchers VV, voms_products VP");
                strBuff.append(" WHERE VV.product_id=VP.product_id AND VV.current_status=? AND expiry_date>?");
                strBuff.append(" GROUP BY VP.product_id,product_name,min_req_quantity,MRP");
                strBuff.append(" HAVING min_req_quantity>COUNT(serial_no)");

                currentDate = new Date();
                if (_logger.isDebugEnabled()) {
                    _logger.debug(methodName, "Query :: " + strBuff.toString());
                }

                psmt = con.prepareStatement(strBuff.toString());
                psmt.setString(1, VOMSI.VOUCHER_ENABLE);
                psmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(currentDate));
                resultSet = psmt.executeQuery();
                VomsVoucherVO vv = null;
                while (resultSet.next()) {
                    vv = new VomsVoucherVO();
                    vv.setProductName(resultSet.getString("product_name"));
                    vv.setMRP((resultSet.getInt("MRP")) / ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue());
                    vv.setCount(resultSet.getInt("available_vouchers"));
                    vv.setMinReqQuantity(resultSet.getLong("min_req_quantity"));
                    arrList.add(vv);
                }

                String fileName = Constants.getProperty("VOUCHER_ALERT_MAIL_NOTIFICATION_REPORT_FILE_PRIFIX") + BTSLUtil.getFileNameStringFromDate(new Date()) + ".xls";
                finalFileName = filePath + fileName;
                writeExcel(_locale, finalFileName);

                if (PretupsI.YES.equalsIgnoreCase(Constants.getProperty("VOUCHER_ALERT_MAIL_NOTIFICATION_REPORT_MAIL_SEND"))) {
                    // start to send mail sending process
                    // String
                    // to=Constants.getProperty("DAILY_REPORT_MAIL_"+p_networkCode);
                    // if(BTSLUtil.isNullString(to))
                    String to = Constants.getProperty("VOUCHER_ALERT_MAIL_NOTIFICATION_REPORT_MAIL_DEFAULT");
                    String from = Constants.getProperty("VOUCHER_ALERT_MAIL_NOTIFICATION_REPORT_MAIL_FROM");
                    String subject = Constants.getProperty("VOUCHER_ALERT_MAIL_NOTIFICATION_REPORT_MAIL_SUBJECT");
                    String bcc = Constants.getProperty("VOUCHER_ALERT_MAIL_NOTIFICATION_REPORT_MAIL_BCC");
                    String cc = Constants.getProperty("VOUCHER_ALERT_MAIL_NOTIFICATION_REPORT_MAIL_CC");
                    String msg = Constants.getProperty("VOUCHER_ALERT_MAIL_NOTIFICATION_REPORT_MAIL_MESSAGE");
                    // Send mail
                    EMailSender.sendMail(to, from, bcc, cc, subject, msg, true, finalFileName, fileName);
                }
                _processStatusVO.setExecutedUpto(currentDate);
                _processStatusVO.setExecutedOn(currentDate);
                maxDoneDateUpdateCount = processStatusDAO.updateProcessDetail(con, _processStatusVO);
                // if the process is successful, transaction is commit, else
                // rollback
                if (maxDoneDateUpdateCount > 0) {
                    con.commit();
                } else {
                    con.rollback();
                    throw new BTSLBaseException("LowVoucherEmailNotification", methodName, PretupsErrorCodesI.VOMS_ALERT_COULD_NOT_UPDATE_MAX_DONE_DATE);
                }
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LowVoucherAlert[process]", "", "", "", " LowVoucherAlert process has been executed successfully.");
            } else {
                throw new BTSLBaseException("LowVoucherEmailNotification", methodName, PretupsErrorCodesI.VOMS_ALERT_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
            }
        } catch (BTSLBaseException be) {
            _logger.error(methodName, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            _logger.error(methodName, "Exception : " + e.getMessage());
            _logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LowVoucherAlert[process]", "", "", "", " LowVoucherAlert process could not be executed successfully.");
            throw new BTSLBaseException("LowVoucherEmailNotification", methodName, PretupsErrorCodesI.VOMS_ALERT_ERROR_EXCEPTION);
        } finally {
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
                    _logger.errorTrace(methodName, ex);
                    if (_logger.isDebugEnabled()) {
                        _logger.debug(methodName, "Exception closing connection ");
                    }
                }

            }
            try {
                if (resultSet != null) {
                	resultSet.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
            }

            try {
                if (psmt != null) {
                    psmt.close();
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
     * This method marks the status of process as complete after successful
     * completion.
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
        Date currentDate = new Date();
        ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        _processStatusVO.setProcessID(p_processId);
        _processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        _processStatusVO.setStartDate(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetail(p_con, _processStatusVO);
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("markProcessStatusAsComplete", "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LowVoucherAlert[markProcessStatusAsComplete]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("LowVoucherAlert", "markProcessStatusAsComplete", PretupsErrorCodesI.VOMS_ALERT_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }

    /**
     * method writeExcelHeader
     * This method write sheet header.
     * 
     * @param p_worksheet1
     * @param p_reportDate
     * @param p_Header
     * @return
     * @throws BTSLBaseException
     */
    private int writeExcelHeader(WritableSheet p_worksheet1, Date p_reportDate, String p_Header) throws BTSLBaseException {
        final String METHOD_NAME = "writeExcelHeader";
        if (_logger.isDebugEnabled()) {
            _logger.debug("writeExcelHeader", " p_reportDate:" + p_reportDate + " p_Header=" + p_Header);
        }
        int rowCount = 0;
        col = 0;
        try {
            p_worksheet1.setColumnView(0, 27);
            p_worksheet1.setColumnView(1, 27);
            for (int i = 2; i < 30; i++) {
                p_worksheet1.setColumnView(i, 16);
            }

            Date currentDate = new Date();
            /*
             * String keyName =
             * BTSLUtil.getMessage(_locale,"dailyreport.header",null);
             * Label label = new Label(col,rowCount,keyName,
             * ExcelStyle.getTopHeadingFont());
             * p_worksheet1.mergeCells(col,rowCount,col+5,rowCount);
             * p_worksheet1.addCell(label);
             */
            col = 0;
            rowCount++;
            Label label = new Label(col, rowCount, "", ExcelStyle.getSecondTopHeadingFont());
            // p_worksheet1.mergeCells(col,rowCount,++col,rowCount);
            p_worksheet1.addCell(label);

            label = new Label(++col, rowCount, p_Header, ExcelStyle.getSecondTopHeadingFont2());
            p_worksheet1.mergeCells(col, rowCount, (++col + 1), rowCount);
            p_worksheet1.addCell(label);

            label = new Label(++col, rowCount, "", ExcelStyle.getSecondTopHeadingFont());
            p_worksheet1.mergeCells(col, rowCount, (++col + 1), rowCount);
            p_worksheet1.addCell(label);

            col = 0;
            rowCount++;
            String keyName = BTSLUtil.getMessage(_locale, "dailyreport.date", null) + BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(p_reportDate));
            label = new Label(col, rowCount, keyName, ExcelStyle.getSecondTopHeadingFont());
            p_worksheet1.mergeCells(col, rowCount, ++col, rowCount);
            p_worksheet1.addCell(label);

            label = new Label(++col, rowCount, "", ExcelStyle.getSecondTopHeadingFont());
            p_worksheet1.mergeCells(col, rowCount, ++col, rowCount);
            p_worksheet1.addCell(label);

            keyName = BTSLUtil.getMessage(_locale, "dailyreport.generatedon", null) + BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(currentDate));
            label = new Label(++col, rowCount, keyName, ExcelStyle.getSecondTopHeadingFont());
            p_worksheet1.mergeCells(col, rowCount, ++col, rowCount);
            p_worksheet1.addCell(label);

            col = 0;
            rowCount++;

            keyName = BTSLUtil.getMessage(_locale, "lowalert.voucher.sheet.header1.name", null);
            label = new Label(col, rowCount, keyName, ExcelStyle.getSecondTopHeadingFont());
            p_worksheet1.mergeCells(col, rowCount, (++col + 4), rowCount);
            p_worksheet1.addCell(label);

            label = new Label(++col, rowCount, "", ExcelStyle.getSecondTopHeadingFont());
            p_worksheet1.mergeCells(col, rowCount, ++col, rowCount);
            p_worksheet1.addCell(label);

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
            _logger.debug("writeExcelHeader", " Exiting row:" + row);
        }
        return rowCount;

    }

    public void writeExcel(Locale p_locale, String p_fileName) throws BTSLBaseException {
        final String METHOD_NAME = "writeExcel";
        if (_logger.isDebugEnabled()) {
            _logger.debug("writeExcel", "  p_locale: " + p_locale + " p_fileName: " + p_fileName);
        }
        WritableWorkbook workbook = null;
        WritableSheet worksheet1 = null;

        // locale=p_locale;
        File fileName = null;
        String repHeader = null;
        int index = 0;
        Date reportDate = new Date();
        try {

            reportDate = BTSLUtil.addDaysInUtilDate(reportDate, -1);
            fileName = new File(p_fileName);
            try {
                workbook = Workbook.createWorkbook(fileName);
            } catch (Exception e) {
                e.getMessage();
                _logger.errorTrace(METHOD_NAME, e);
            }
            repHeader = BTSLUtil.getMessage(_locale, "lowalert.voucher.sheet.name", null);
            worksheet1 = workbook.createSheet(repHeader, index++);
            repHeader = BTSLUtil.getMessage(_locale, "lowalert.voucher.sheet.header.name", null);
            // write into C2S sheet
            row = writeExcelHeader(worksheet1, reportDate, repHeader);
            row = writeExcelVoucherStatus(worksheet1, row);
            workbook.write();
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            worksheet1 = workbook.createSheet("HEADER", index++);
        } finally {
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
     * method writeExcelVoucherStatus
     * This method write to start data on first sheet for Voucher Status.
     * 
     * @param p_worksheet
     * @param p_row
     * @return
     * @throws BTSLBaseException
     */
    private int writeExcelVoucherStatus(WritableSheet p_worksheet, int p_row) throws BTSLBaseException {
        final String METHOD_NAME = "writeExcelVoucherStatus";
        if (_logger.isDebugEnabled()) {
            _logger.debug("writeExcelVoucherStatus", "  p_row: " + p_row);
        }
        try {
            VomsVoucherVO vomsVoucherVO = null;
            vomsVoucherVO = null;
            String keyName = null;
            Label label = null;
            Number number = null;
            col = 0;
            p_row++;
            keyName = BTSLUtil.getMessage(_locale, "lowalert.voucher.sheet.sno.name", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.addCell(label);

            keyName = BTSLUtil.getMessage(_locale, "lowalert.voucher.sheet.voucherprofile.name", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.addCell(label);

            keyName = BTSLUtil.getMessage(_locale, "lowalert.voucher.sheet.denomination.name", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.addCell(label);

            keyName = BTSLUtil.getMessage(_locale, "lowalert.voucher.sheet.numbervouvheravailable.name", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(_locale, "lowalert.voucher.sheet.minimumvoucherreq.name", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.addCell(label);

            if (arrList != null && arrList.size() > 0) {
                for (int i = 0, j = arrList.size(); i < j; i++) {
                    vomsVoucherVO = (VomsVoucherVO) arrList.get(i);
                    col = 0;
                    p_row++;
                    col = 0;
                    p_row++;
                    label = new Label(col++, p_row, String.valueOf(i + 1), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                    if (col == 0) {
                        col++;
                    }
                    label = new Label(col++, p_row, vomsVoucherVO.getProductName(), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                    number = new Number(col++, p_row, vomsVoucherVO.getMRP(), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(number);

                    number = new Number(col++, p_row, vomsVoucherVO.getCount(), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(number);

                    number = new Number(col++, p_row, vomsVoucherVO.getMinReqQuantity(), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(number);
                }
            }
        } catch (BTSLBaseException be) {
            _logger.errorTrace(METHOD_NAME, be);
            _logger.error("writeExcelVoucherStatus", " BTSLBaseException e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("writeExcelVoucherStatus", " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, "writeExcelVoucherStatus", "Exception=" + e.getMessage());
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug("writeExcelVoucherStatus", " Exiting row:" + row);
        }
        return p_row;
    }
}

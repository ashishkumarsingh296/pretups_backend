/*
 * @(#)KPIReportWriteInXLS.java
 * Copyright(c) 2009, Comviva Technologies Ltd.
 * All Rights Reserved
 * Description :-
 * --------------------------------------------------------------------
 * Author Date History
 * --------------------------------------------------------------------
 * ved.sharma Nov 25, 2009 Initial creation
 * --------------------------------------------------------------------
 */
package com.btsl.pretups.processes;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class KPIReportWriteInXLS {

    private final Log _log = LogFactory.getLog(this.getClass().getName());

    private int sheetCount = 0;
    private static final int MAX_ROWS = 65000;

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-getDateHeadingFont
     * 
     * @throws BTSLBaseException
     *             Return :-WritableCellFormat
     *             Nov 26, 2009 10:35:03 AM
     */
    private WritableCellFormat getDateHeadingFont() throws BTSLBaseException {
        final String METHOD_NAME = "getDateHeadingFont";
        WritableCellFormat headingHeader = null;
        try {
            final WritableFont timesfont3 = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
            timesfont3.setColour(Colour.WHITE);
            headingHeader = new WritableCellFormat(timesfont3);
            headingHeader.setBackground(Colour.ORANGE);
            headingHeader.setAlignment(Alignment.LEFT);
            headingHeader.setBorder(Border.ALL, BorderLineStyle.THIN);
            headingHeader.setVerticalAlignment(VerticalAlignment.BOTTOM);
            // headingHeader.setWrap(true);
        } catch (Exception e) {
            _log.error("getDateHeadingFont", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "getDateHeadingFont", "Exception=" + e.getMessage());
        }
        return headingHeader;
    }

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-getDateHeadingFont1
     * 
     * @throws BTSLBaseException
     *             Return :-WritableCellFormat
     *             Nov 26, 2009 12:38:55 PM
     */
    private WritableCellFormat getDateHeadingFont1() throws BTSLBaseException {
        final String METHOD_NAME = "getDateHeadingFont1";
        WritableCellFormat headingHeader = null;
        try {
            final WritableFont timesfont3 = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
            timesfont3.setColour(Colour.BLACK);
            headingHeader = new WritableCellFormat(timesfont3);
            headingHeader.setBackground(Colour.GOLD);
            headingHeader.setAlignment(Alignment.LEFT);
            headingHeader.setBorder(Border.ALL, BorderLineStyle.THIN);
            headingHeader.setVerticalAlignment(VerticalAlignment.BOTTOM);
            // headingHeader.setWrap(true);
        } catch (Exception e) {
            _log.error("getDateHeadingFont", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "getDateHeadingFont", "Exception=" + e.getMessage());
        }
        return headingHeader;
    }

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-getTopHeadingFont
     * 
     * @throws BTSLBaseException
     *             Return :-WritableCellFormat
     *             Nov 26, 2009 10:34:59 AM
     */
    private WritableCellFormat getTopHeadingFont() throws BTSLBaseException {
        final String METHOD_NAME = "getTopHeadingFont";
        WritableCellFormat headingHeader = null;

        try {
            final WritableFont timesfont3 = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false);
            timesfont3.setColour(Colour.WHITE);
            headingHeader = new WritableCellFormat(timesfont3);
            headingHeader.setBackground(Colour.DARK_BLUE);
            headingHeader.setAlignment(Alignment.CENTRE);
            headingHeader.setBorder(Border.ALL, BorderLineStyle.THIN);
            headingHeader.setVerticalAlignment(VerticalAlignment.BOTTOM);
            // headingHeader.setWrap(true);
        } catch (Exception e) {
            _log.error("getTopHeadingFont", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "getTopHeadingFont", "Exception=" + e.getMessage());
        }
        return headingHeader;
    }

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-getDataFont
     * 
     * @throws BTSLBaseException
     *             Return :-WritableCellFormat
     *             Nov 26, 2009 1:00:40 PM
     */
    private WritableCellFormat getNumberDataFont() throws BTSLBaseException {
        final String METHOD_NAME = "getNumberDataFont";
        WritableCellFormat headingHeader = null;

        try {
            final WritableFont timesfont3 = new WritableFont(WritableFont.ARIAL, 11, WritableFont.NO_BOLD, false);
            timesfont3.setColour(Colour.BLACK);
            headingHeader = new WritableCellFormat(timesfont3);
            // headingHeader.setBackground(Colour.DARK_BLUE);
            headingHeader.setAlignment(Alignment.RIGHT);
            headingHeader.setBorder(Border.ALL, BorderLineStyle.THIN);
            // headingHeader.setVerticalAlignment(VerticalAlignment.BOTTOM);
            // headingHeader.setWrap(true);
        } catch (Exception e) {
            _log.error("getNumberDataFont", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "getNumberDataFont", "Exception=" + e.getMessage());
        }
        return headingHeader;
    }

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-getStringDataFont
     * 
     * @throws BTSLBaseException
     *             Return :-WritableCellFormat
     *             Nov 27, 2009 9:25:47 AM
     */
    private WritableCellFormat getStringDataFont() throws BTSLBaseException {
        final String METHOD_NAME = "getStringDataFont";
        WritableCellFormat headingHeader = null;

        try {
            final WritableFont timesfont3 = new WritableFont(WritableFont.ARIAL, 11, WritableFont.NO_BOLD, false);
            timesfont3.setColour(Colour.BLACK);
            headingHeader = new WritableCellFormat(timesfont3);
            // headingHeader.setBackground(Colour.DARK_BLUE);
            headingHeader.setAlignment(Alignment.LEFT);
            headingHeader.setBorder(Border.ALL, BorderLineStyle.THIN);
            // headingHeader.setVerticalAlignment(VerticalAlignment.BOTTOM);
            // headingHeader.setWrap(true);
        } catch (Exception e) {
            _log.error("getStringDataFont", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "getStringDataFont", "Exception=" + e.getMessage());
        }
        return headingHeader;
    }

    public void writeExcel(HashMap p_hashMap, String p_fileName) throws BTSLBaseException {
        final String METHOD_NAME = "writeExcel";
        if (_log.isDebugEnabled()) {
            _log.debug("writeExcel", " p_hashMap:" + p_hashMap);
        }

        WritableSheet worksheet = null;
        WritableWorkbook workbook = null;
        File file = null;
        int col = 0;
        int row = 0;
        try {
            final Date fromdate = (Date) p_hashMap.get("FROM_DATE");
            final Date todate = (Date) p_hashMap.get("TO_DATE");
            final String frequency = (String) p_hashMap.get("FREQUENCY");

            file = new File(p_fileName);
            workbook = Workbook.createWorkbook(file);
            worksheet = workbook.createSheet(frequency, sheetCount);

            String keyName = Constants.getProperty("REPORT_HEADER");
            Label label = new Label(col, row, keyName, getTopHeadingFont());
            worksheet.mergeCells(col, row, col + 4, row);
            worksheet.addCell(label);

            col = 0;
            row++;
            row++;
            keyName = Constants.getProperty("REPORT_HEADER_FROM_DATE");
            label = new Label(col, row, keyName, getDateHeadingFont1());
            worksheet.mergeCells(col, row, col = col + 2, row);
            worksheet.addCell(label);
            label = new Label(++col, row, BTSLUtil.getDateStringFromDate(fromdate), getStringDataFont());
            worksheet.addCell(label);
            col = 0;
            row++;

            keyName = Constants.getProperty("REPORT_HEADER_TO_DATE");
            label = new Label(col, row, keyName, getDateHeadingFont1());
            worksheet.mergeCells(col, row, col = col + 2, row);
            worksheet.addCell(label);
            label = new Label(++col, row, BTSLUtil.getDateStringFromDate(todate), getStringDataFont());
            worksheet.addCell(label);
            col = 0;
            row += 2;

            final ArrayList indexList = new KPIProcess().getKPIIndex();
            int allKPI = 0;

            if (indexList == null || indexList.isEmpty())// All kpi
            {
                row = write(workbook, worksheet, row, p_hashMap, -1);
                row++;
            } else {
                for (int n = 0, m = indexList.size(); n < m; n++) {
                    allKPI = Integer.parseInt((String) indexList.get(n));
                    row = write(workbook, worksheet, row, p_hashMap, allKPI);
                    row++;
                }
            }
            workbook.write();

        } catch (BTSLBaseException e) {
            throw e;
        } catch (Exception e) {
            _log.error("writeExcel", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeExcel", "Exception=" + e.getMessage());
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("writeExcel", " Exiting");
            }
        }
    }

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-write
     * 
     * @param workbook
     * @param worksheet
     * @param row
     * @param p_hashMap
     *            Return :-void
     *            Dec 1, 2009 4:34:04 PM
     */
    private int write(WritableWorkbook workbook, WritableSheet worksheet, int row, HashMap p_hashMap, int p_kpiID) throws BTSLBaseException {
        final String METHOD_NAME = "write";
        int col = 0;
        String keyName = null;
        Label label = null;
        Number number = null;
        try {
            final String frequency = (String) p_hashMap.get("FREQUENCY");
            /*
             * ## This is the KPI Secquence number
             * ## 1) Number of retailers -ok
             * ## 2) Number of new retailers in last month -ok
             * ## 3) Number of end customers -ok
             * ## 4) Number of new customers in last month -ok
             * ## 5) Average balance per retailer in last month -ok
             * 
             * ## 6) Average C2C - Transfer amount in last month -ok
             * ## Average C2C - Return amount in last month -ok
             * ## Average C2C - withdraw amount in last month -ok
             * ## Total number of C2C - Transfer in last month -ok
             * ## Total number of C2C - Return in last month -ok
             * ## Total number of C2C - Withdraw in last month -ok
             * 
             * ## 7) Total number of O2C - Transfer in last month -ok
             * ## Total number of O2C - Withdraw in last month -ok
             * ## Total number of O2C - Return in last month -ok
             * ## 8) Average number of C2S per retailer in last month -ok
             * ## 9) Average number of C2C per retailer in last month -ok
             * ## 10) Concentration of distribution to measure the concentration
             * of air time distribution per country -ok
             * ## #Distribution 60% KPI : number of active RP2P users which
             * contributes to 60% on the RP2P amount per month
             * ## #Distribution 80% KPI : number of active RP2P users which
             * contributes to 80% on the RP2P amount per month
             * ## 11) If more than 1 last month the put the value as comma
             * seperated, value should be positive integer -ok
             * ## #Number of active retailers in last 3 months
             * ## #Number of active retailers in last 6 months
             * ## 12) Average commission per retailer (O2C et C2C) in last month
             * -ok
             * ## 13) Average bonus per C2S transaction in last month -ok
             * ## 14) Average bonus per P2P transaction in last month -ok
             * ## 15) % of active end-customers in last month -(Under
             * contruction)
             * ## 16) Average number of P2P per customer in last month -(Under
             * contruction)
             * ## 17) The total air-time concretely transferred to end-users C2S
             * (with generated bonus). -(Under contruction)
             * ## 18) The sum of the revenues collected on the Head of Channels
             * O2C (PAYABLE AMOUNT = RP2P REVENUE for the Orange
             * affiliate)-(Under contruction)
             * ## 19) The commissions generated within all the domains through
             * O2C and C2C (b)
             */
            switch (p_kpiID) {
                case -1:
                    {
                        // for All, it will first case of switch;
                    }
                case 1:
                    {
                        keyName = Constants.getProperty("NO_CHNL_USERS");
                        label = new Label(col, row, keyName, getDateHeadingFont());
                        worksheet.mergeCells(col, row, col = col + 2, row);
                        worksheet.addCell(label);
                        keyName = (String) p_hashMap.get("NO_CHNL_USERS");

                        if (BTSLUtil.isNullString(keyName)) {
                            number = new Number(++col, row, 0, getNumberDataFont());
                        } else {
                            number = new Number(++col, row, Double.parseDouble(keyName), getNumberDataFont());
                        }
                        worksheet.addCell(number);

                        col = 0;
                        row++;
                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 2:
                    {
                        keyName = Constants.getProperty("NO_OF_NEW_CHNL_USERS_DURATION");
                        label = new Label(col, row, keyName, getDateHeadingFont());
                        worksheet.mergeCells(col, row, col = col + 2, row);
                        worksheet.addCell(label);
                        keyName = (String) p_hashMap.get("NO_OF_NEW_CHNL_USERS_DURATION");
                        if (BTSLUtil.isNullString(keyName)) {
                            number = new Number(++col, row, 0, getNumberDataFont());
                        } else {
                            number = new Number(++col, row, Double.parseDouble(keyName), getNumberDataFont());
                        }
                        worksheet.addCell(number);
                        col = 0;
                        row++;
                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 3:
                    {
                        keyName = Constants.getProperty("NO_END_CUSTOMERS");
                        label = new Label(col, row, keyName, getDateHeadingFont());
                        worksheet.mergeCells(col, row, col = col + 2, row);
                        worksheet.addCell(label);
                        keyName = (String) p_hashMap.get("NO_END_CUSTOMERS");
                        if (BTSLUtil.isNullString(keyName)) {
                            number = new Number(++col, row, 0, getNumberDataFont());
                        } else {
                            number = new Number(++col, row, Double.parseDouble(keyName), getNumberDataFont());
                        }
                        worksheet.addCell(number);
                        col = 0;
                        row++;
                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 4:
                    {
                        keyName = Constants.getProperty("NO_OF_NEW_CUSTOMERS_DURATION");
                        label = new Label(col, row, keyName, getDateHeadingFont());
                        worksheet.mergeCells(col, row, col = col + 2, row);
                        worksheet.addCell(label);
                        keyName = (String) p_hashMap.get("NO_OF_NEW_CUSTOMERS_DURATION");
                        if (BTSLUtil.isNullString(keyName)) {
                            number = new Number(++col, row, 0, getNumberDataFont());
                        } else {
                            number = new Number(++col, row, Double.parseDouble(keyName), getNumberDataFont());
                        }
                        worksheet.addCell(number);
                        col = 0;
                        row++;
                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 11:
                    {
                        final String activeUsers = NullToString(Constants.getProperty("NO_OF_ACTIVE_CHNL_USER_LAST_DURATION")).trim();
                        final String str[] = activeUsers.split(",");
                        for (int i = 0, j = str.length; i < j; i++) {
                            final HashMap map = (HashMap) p_hashMap.get("ACTIVE_USERS-" + str[i].trim());
                            if (map != null && map.size() > 0) {
                                keyName = Constants.getProperty("NO_OF_ACTIVE_CHNL_USER") + " " + Constants.getProperty("LAST") + " " + str[i].trim() + " " + Constants
                                    .getProperty(frequency);
                                keyName = keyName + " (" + BTSLUtil.getDateStringFromDate((Date) map.get("FROM_DATE")) + " to " + BTSLUtil.getDateStringFromDate((Date) map
                                    .get("TO_DATE")) + ")";
                                label = new Label(col, row, keyName, getDateHeadingFont());
                                worksheet.mergeCells(col, row, col = col + 2, row);
                                worksheet.addCell(label);
                                keyName = (String) map.get("ACTIVE_USERS");
                                if (BTSLUtil.isNullString(keyName)) {
                                    number = new Number(++col, row, 0, getNumberDataFont());
                                } else {
                                    number = new Number(++col, row, Double.parseDouble(keyName), getNumberDataFont());
                                }
                                worksheet.addCell(number);
                                col = 0;
                                row++;
                            }
                        }
                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 14:
                    {
                        keyName = Constants.getProperty("P2P_AVG_BONUS");
                        label = new Label(col, row, keyName, getDateHeadingFont());
                        worksheet.mergeCells(col, row, col = col + 2, row);
                        worksheet.addCell(label);
                        keyName = (String) p_hashMap.get("P2P_AVG_BONUS");
                        if (BTSLUtil.isNullString(keyName)) {
                            number = new Number(++col, row, 0, getNumberDataFont());
                        } else {
                            number = new Number(++col, row, Double.parseDouble(keyName), getNumberDataFont());
                        }
                        worksheet.addCell(number);
                        col = 0;
                        row++;
                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 5:
                    {
                        writeAvgBalPerChnlUser(workbook, p_hashMap);
                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 7:
                    {
                        col = 0;
                        row++;
                        row = writeO2CTransation(row, worksheet, p_hashMap);
                        col = 0;
                        row++;
                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 6:
                    {
                        col = 0;
                        row++;
                        row = writeC2CTransation(row, worksheet, p_hashMap);
                        col = 0;
                        row++;
                        if (p_kpiID != -1) {
                            break;
                        }
                    }

                case 8:
                    {
                        writeAvgNoC2SPerChnlUser(workbook, p_hashMap);
                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 9:
                    {
                        writeAvgNoC2CPerChnlUser(workbook, p_hashMap);
                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 10:
                    {
                        String pctCont = NullToString(Constants.getProperty("DISTRIBUTION_PCT")).trim();
                        final String[] str = pctCont.split(",");
                        for (int i = 0, j = str.length; i < j; i++) {
                            pctCont = str[i].trim();
                            writePctContributionC2STxn(workbook, p_hashMap, pctCont);
                        }
                        if (p_kpiID != -1) {
                            break;
                        }
                    }

                case 12:
                    {
                        writeAvgCommissionPerChnlUser(workbook, p_hashMap);
                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 13:
                    {
                        writeAvgBonusPerChnlUser(workbook, p_hashMap);
                        if (p_kpiID != -1) {
                            break;
                        }
                    }

                case 15:
                    {
                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 16:
                    {
                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 17:
                    {

                        writeC2STransationDateWise(workbook, p_hashMap);

                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 18:
                    {
                        col = 0;
                        row++;
                        row = writeRevenuesDomainWise(row, worksheet, p_hashMap);
                        col = 0;
                        row++;
                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 19:
                    {
                        col = 0;
                        row++;
                        row = writeCommissionDomainWise(row, worksheet, p_hashMap);
                        col = 0;
                        row++;
                        if (p_kpiID != -1) {
                            break;
                        }
                    }
                case 20:
                    {
                        if (p_kpiID != -1) {
                            break;
                        }
                    }

            }

        } catch (BTSLBaseException e) {
            throw e;
        } catch (Exception e) {
            _log.error("write", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "write", "Exception=" + e.getMessage());
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("write", " Exiting");
            }
        }
        return row;

    }

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-writeC2CTransation
     * 
     * @param p_row
     * @param worksheet
     * @param p_hashMap
     * @throws BTSLBaseException
     *             Return :-int
     *             Nov 26, 2009 11:35:13 AM
     */
    private int writeC2CTransation(int p_row, WritableSheet worksheet, HashMap p_hashMap) throws BTSLBaseException {
        final String METHOD_NAME = "writeC2CTransation";
        String trfAvg[] = null;
        String wdAvg[] = null;
        String retAvg[] = null;
        String trfTotal[] = null;
        String wdTotal[] = null;
        String retTotal[] = null;
        String trfStr = null;
        String wdStr = null;
        String retStr = null;
        int col = 0;
        final int row = p_row;
        try {
            String keyName = Constants.getProperty("C2C_TEANSACTION");
            Label label = new Label(col, p_row, keyName, getTopHeadingFont());
            worksheet.mergeCells(col, p_row, col + 3, row);
            worksheet.addCell(label);
            col = 0;
            p_row++;

            label = new Label(col++, p_row, "", getDateHeadingFont());
            worksheet.addCell(label);
            label = new Label(col++, p_row, "", getDateHeadingFont());
            worksheet.addCell(label);
            keyName = Constants.getProperty("AVERAGE");
            label = new Label(col++, p_row, keyName, getDateHeadingFont());
            worksheet.addCell(label);
            keyName = Constants.getProperty("TOTAL");
            label = new Label(col++, p_row, keyName, getDateHeadingFont());
            worksheet.addCell(label);
            col = 0;
            p_row++;

            trfStr = ((String) p_hashMap.get("AVG_C2C_TRANSFER_BAL"));
            wdStr = ((String) p_hashMap.get("AVG_C2C_WITHDRAW_BAL"));
            retStr = ((String) p_hashMap.get("AVG_C2C_RETURN_BAL"));

            if (!BTSLUtil.isNullString(trfStr)) {
                trfAvg = trfStr.split(":");
            }
            if (!BTSLUtil.isNullString(wdStr)) {
                wdAvg = wdStr.split(":");
            }
            if (!BTSLUtil.isNullString(retStr)) {
                retAvg = retStr.split(":");
            }
            trfStr = null;
            wdStr = null;
            retStr = null;

            trfStr = ((String) p_hashMap.get("TOTAL_C2C_TRANSFER_BAL"));
            wdStr = ((String) p_hashMap.get("TOTAL_C2C_WITHDRAW_BAL"));
            retStr = ((String) p_hashMap.get("TOTAL_C2C_RETURN_BAL"));

            if (!BTSLUtil.isNullString(trfStr)) {
                trfTotal = trfStr.split(":");
            }
            if (!BTSLUtil.isNullString(wdStr)) {
                wdTotal = wdStr.split(":");
            }
            if (!BTSLUtil.isNullString(retStr)) {
                retTotal = retStr.split(":");
            }

            int rowMrg = p_row + 2;
            keyName = Constants.getProperty("IN_AMOUNT");
            label = new Label(col, p_row, keyName, getDateHeadingFont1());
            worksheet.mergeCells(col, p_row, col, rowMrg);
            worksheet.addCell(label);

            keyName = Constants.getProperty("OUT_AMOUNT");
            label = new Label(col, ++rowMrg, keyName, getDateHeadingFont());
            worksheet.mergeCells(col, rowMrg, col, rowMrg + 2);
            worksheet.addCell(label);

            keyName = Constants.getProperty("TRANSFER");
            label = new Label(++col, p_row, keyName, getDateHeadingFont1());
            worksheet.addCell(label);
            label = new Label(col, p_row + 3, keyName, getDateHeadingFont());
            worksheet.addCell(label);
            Number number = null;
            if (BTSLUtil.isNullArray(trfAvg)) {
                number = new Number(++col, p_row, 0, getNumberDataFont());
                worksheet.addCell(number);
                number = new Number(col, p_row + 3, 0, getNumberDataFont());
                worksheet.addCell(number);
            } else {
                number = new Number(++col, p_row, Double.parseDouble(trfAvg[0]), getNumberDataFont());
                worksheet.addCell(number);
                number = new Number(col, p_row + 3, Double.parseDouble(trfAvg[1]), getNumberDataFont());
                worksheet.addCell(number);
            }
            if (BTSLUtil.isNullArray(trfTotal)) {
                number = new Number(++col, p_row, 0, getNumberDataFont());
                worksheet.addCell(number);
                number = new Number(col, p_row + 3, 0, getNumberDataFont());
                worksheet.addCell(number);
            } else {
                number = new Number(++col, p_row, Double.parseDouble(trfTotal[0]), getNumberDataFont());
                worksheet.addCell(number);
                number = new Number(col, p_row + 3, Double.parseDouble(trfTotal[1]), getNumberDataFont());
                worksheet.addCell(number);
            }
            col = 0;
            p_row++;
            keyName = Constants.getProperty("WITHDRAW");
            label = new Label(++col, p_row, keyName, getDateHeadingFont1());
            worksheet.addCell(label);
            label = new Label(col, p_row + 3, keyName, getDateHeadingFont());
            worksheet.addCell(label);
            if (BTSLUtil.isNullArray(wdAvg)) {
                number = new Number(++col, p_row, 0, getNumberDataFont());
                worksheet.addCell(number);
                number = new Number(col, p_row + 3, 0, getNumberDataFont());
                worksheet.addCell(number);
            } else {
                number = new Number(++col, p_row, Double.parseDouble(wdAvg[0]), getNumberDataFont());
                worksheet.addCell(number);
                number = new Number(col, p_row + 3, Double.parseDouble(wdAvg[1]), getNumberDataFont());
                worksheet.addCell(number);
            }
            if (BTSLUtil.isNullArray(wdTotal)) {
                number = new Number(++col, p_row, 0, getNumberDataFont());
                worksheet.addCell(number);
                number = new Number(col, p_row + 3, 0, getNumberDataFont());
                worksheet.addCell(number);
            } else {
                number = new Number(++col, p_row, Double.parseDouble(wdTotal[0]), getNumberDataFont());
                worksheet.addCell(number);
                number = new Number(col, p_row + 3, Double.parseDouble(wdTotal[1]), getNumberDataFont());
                worksheet.addCell(number);
            }
            col = 0;
            p_row++;
            keyName = Constants.getProperty("RETURN");
            label = new Label(++col, p_row, keyName, getDateHeadingFont1());
            worksheet.addCell(label);
            label = new Label(col, p_row + 3, keyName, getDateHeadingFont());
            worksheet.addCell(label);
            if (BTSLUtil.isNullArray(retAvg)) {
                number = new Number(++col, p_row, 0, getNumberDataFont());
                worksheet.addCell(number);
                number = new Number(col, p_row + 3, 0, getNumberDataFont());
                worksheet.addCell(number);
            } else {
                number = new Number(++col, p_row, Double.parseDouble(retAvg[0]), getNumberDataFont());
                worksheet.addCell(number);
                number = new Number(col, p_row + 3, Double.parseDouble(retAvg[1]), getNumberDataFont());
                worksheet.addCell(number);
            }
            if (BTSLUtil.isNullArray(retTotal)) {
                number = new Number(++col, p_row, 0, getNumberDataFont());
                worksheet.addCell(number);
                number = new Number(col, p_row + 3, 0, getNumberDataFont());
                worksheet.addCell(number);

            } else {
                number = new Number(++col, p_row, Double.parseDouble(retTotal[0]), getNumberDataFont());
                worksheet.addCell(number);
                number = new Number(col, p_row + 3, Double.parseDouble(retTotal[1]), getNumberDataFont());
                worksheet.addCell(number);
            }
            p_row = p_row + 3;
        } catch (BTSLBaseException e) {
            _log.error("writeC2CTransation", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeC2CTransation", "Exception=" + e.getMessage());
        } catch (RowsExceededException e) {
            _log.error("writeC2CTransation", " RowsExceededException :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeC2CTransation", "Exception=" + e.getMessage());
        } catch (WriteException e) {
            _log.error("writeC2CTransation", " WriteException :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeC2CTransation", "Exception=" + e.getMessage());
        } catch (Exception e) {
            _log.error("writeC2CTransation", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeC2CTransation", "Exception=" + e.getMessage());
        }
        return p_row;
    }

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-writeO2CTransation
     * 
     * @param p_row
     * @param worksheet
     * @param p_hashMap
     * @throws BTSLBaseException
     *             Return :-int
     *             Nov 26, 2009 3:23:05 PM
     */
    private int writeO2CTransation(int p_row, WritableSheet worksheet, HashMap p_hashMap) throws BTSLBaseException {
        final String METHOD_NAME = "writeO2CTransation";
        String trfTotal[] = null;
        String trfStr = null;
        int col = 0;
        final int row = p_row;
        try {
            String keyName = Constants.getProperty("TOTAL") + " " + Constants.getProperty("O2C_TEANSACTION");
            Label label = new Label(col, p_row, keyName, getTopHeadingFont());
            worksheet.mergeCells(col, p_row, col + 1, row);
            worksheet.addCell(label);
            col = 0;
            p_row++;

            trfStr = ((String) p_hashMap.get("TOTAL_O2C_BAL"));
            if (!BTSLUtil.isNullString(trfStr)) {
                trfTotal = trfStr.split(":");
            }

            keyName = Constants.getProperty("TRANSFER") + " " + Constants.getProperty("IN_AMOUNT");
            label = new Label(col, p_row, keyName, getDateHeadingFont());
            worksheet.addCell(label);
            p_row++;
            keyName = Constants.getProperty("WITHDRAW") + " " + Constants.getProperty("OUT_AMOUNT");
            label = new Label(col, p_row, keyName, getDateHeadingFont());
            worksheet.addCell(label);
            p_row++;
            keyName = Constants.getProperty("RETURN") + " " + Constants.getProperty("OUT_AMOUNT");
            label = new Label(col, p_row, keyName, getDateHeadingFont());
            worksheet.addCell(label);
            Number number = null;
            if (BTSLUtil.isNullArray(trfTotal)) {
                p_row -= 2;
                number = new Number(++col, p_row++, 0, getNumberDataFont());
                worksheet.addCell(number);
                number = new Number(col, p_row++, 0, getNumberDataFont());
                worksheet.addCell(number);
                number = new Number(col, p_row++, 0, getNumberDataFont());
                worksheet.addCell(number);
            } else {
                p_row -= 2;
                number = new Number(++col, p_row++, Double.parseDouble(trfTotal[0]), getNumberDataFont());
                worksheet.addCell(number);
                number = new Number(col, p_row++, Double.parseDouble(trfTotal[1]), getNumberDataFont());
                worksheet.addCell(number);
                number = new Number(col, p_row++, Double.parseDouble(trfTotal[2]), getNumberDataFont());
                worksheet.addCell(number);
            }
        } catch (BTSLBaseException e) {
            _log.error("writeC2CTransation", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeC2CTransation", "Exception=" + e.getMessage());
        } catch (RowsExceededException e) {
            _log.error("writeC2CTransation", " RowsExceededException :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeC2CTransation", "Exception=" + e.getMessage());
        } catch (WriteException e) {
            _log.error("writeC2CTransation", " WriteException :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeC2CTransation", "Exception=" + e.getMessage());
        } catch (Exception e) {
            _log.error("writeC2CTransation", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeC2CTransation", "Exception=" + e.getMessage());
        }
        return p_row;
    }

    // AVG_BAL_PER_CHNLUSR
    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-writeAvgBalPerChnlUser
     * 
     * @param p_workbook
     * @param p_hashMap
     * @throws BTSLBaseException
     *             Return :-void
     *             Nov 26, 2009 4:37:22 PM
     */
    private void writeAvgBalPerChnlUser(WritableWorkbook p_workbook, HashMap p_hashMap) throws BTSLBaseException {
        final String METHOD_NAME = "writeAvgBalPerChnlUser";
        WritableSheet worksheet = null;
        int lrow = 0, lcol = 0;
        try {
            final ArrayList list = (ArrayList) p_hashMap.get("AVG_CHNL_USERS_BAL");
            final String frequency = (String) p_hashMap.get("FREQUENCY");
            String keyName = Constants.getProperty("SHEET_NAME_AVG_BAL_PER_CHNLUSR");
            final String sheetName = keyName;
            int index = 1;

            boolean isAddIndex = false;
            if (list != null && !list.isEmpty()) {
                if (list.size() > MAX_ROWS) {
                    isAddIndex = true;
                }
            }

            sheetCount++;
            if (isAddIndex) {
                worksheet = p_workbook.createSheet(sheetName + "-" + index++, sheetCount);
            } else {
                worksheet = p_workbook.createSheet(sheetName, sheetCount);
            }
            final Date fromdate = (Date) p_hashMap.get("FROM_DATE");
            final Date todate = (Date) p_hashMap.get("TO_DATE");
            keyName = Constants.getProperty("AVG_BAL_PER_CHNLUSR");
            keyName = keyName + " (" + BTSLUtil.getDateStringFromDate(fromdate) + " to " + BTSLUtil.getDateStringFromDate(todate) + ")";
            Label label = new Label(lcol, lrow, keyName, getTopHeadingFont());
            Number number = null;
            worksheet.mergeCells(lcol, lrow, lcol + 4, lrow);
            worksheet.addCell(label);
            lcol = 0;
            lrow++;
            lrow++;
            if (list == null || list.isEmpty()) {
                keyName = Constants.getProperty("DATA_NOT_AVILABLE");
                label = new Label(lcol, lrow, keyName, getDateHeadingFont());
                worksheet.mergeCells(lcol, lrow, lcol + 4, lrow);
                worksheet.addCell(label);
            } else {
                boolean header = false;
                String str[] = null;
                for (int i = 0, j = list.size(); i < j; i++) {
                    if (!header) {
                        keyName = Constants.getProperty("SERIAL_NO");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        keyName = Constants.getProperty("USER_NAME");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        keyName = Constants.getProperty("USER_LOGIN_ID");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        keyName = Constants.getProperty("USER_MSISDN");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        keyName = Constants.getProperty("AVERAGE_BAL");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        header = true;
                        lcol = 0;
                        lrow++;
                    }
                    str = ((String) list.get(i)).split(":");
                    number = new Number(lcol++, lrow, i + 1, getNumberDataFont());
                    worksheet.addCell(number);
                    label = new Label(lcol++, lrow, NullToString(str[0]), getStringDataFont());
                    worksheet.addCell(label);
                    label = new Label(lcol++, lrow, NullToString(str[1]), getStringDataFont());
                    worksheet.addCell(label);
                    label = new Label(lcol++, lrow, NullToString(str[2]), getStringDataFont());
                    worksheet.addCell(label);
                    number = new Number(lcol++, lrow, Double.parseDouble(str[3]), getNumberDataFont());
                    worksheet.addCell(number);
                    lcol = 0;
                    lrow++;
                    if (lrow > MAX_ROWS) {
                        header = false;
                        sheetCount++;
                        worksheet = p_workbook.createSheet(sheetName + "-" + index++, sheetCount);
                        lcol = 0;
                        lrow = 0;
                    }

                }

            }
        } catch (BTSLBaseException e) {
            _log.error("writeAvgBalPerChnlUser", " BTSLBaseException :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeAvgBalPerChnlUser", "Exception=" + e.getMessage());
        } catch (RowsExceededException e) {
            _log.error("writeAvgBalPerChnlUser", " RowsExceededException :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeAvgBalPerChnlUser", "Exception=" + e.getMessage());
        } catch (WriteException e) {
            _log.error("writeAvgBalPerChnlUser", " WriteException :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeAvgBalPerChnlUser", "Exception=" + e.getMessage());
        } catch (Exception e) {
            _log.error("writeAvgBalPerChnlUser", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeAvgBalPerChnlUser", "Exception=" + e.getMessage());
        }

    }

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-writeAvgNoC2CPerChnlUser
     * 
     * @param p_workbook
     * @param p_hashMap
     * @throws BTSLBaseException
     *             Return :-void
     *             Nov 27, 2009 10:07:13 AM
     */
    private void writeAvgNoC2CPerChnlUser(WritableWorkbook p_workbook, HashMap p_hashMap) throws BTSLBaseException {
        final String METHOD_NAME = "writeAvgNoC2CPerChnlUser";
        WritableSheet worksheet = null;
        int lrow = 0, lcol = 0;
        try {
            final ArrayList list = (ArrayList) p_hashMap.get("AVG_NO_C2C_PER_CHNL_USERS");

            String keyName = Constants.getProperty("SHEET_NAME_AVG_NO_C2C_PER_CHNLUSR");
            final String sheetName = keyName;
            int index = 1;

            boolean isAddIndex = false;
            if (list != null && !list.isEmpty()) {
                if (list.size() > MAX_ROWS) {
                    isAddIndex = true;
                }
            }

            sheetCount++;
            if (isAddIndex) {
                worksheet = p_workbook.createSheet(sheetName + "-" + index++, sheetCount);
            } else {
                worksheet = p_workbook.createSheet(sheetName, sheetCount);
            }
            final Date fromdate = (Date) p_hashMap.get("FROM_DATE");
            final Date todate = (Date) p_hashMap.get("TO_DATE");
            keyName = Constants.getProperty("AVG_NO_C2C_PER_CHNL_USERS");
            keyName = keyName + " (" + BTSLUtil.getDateStringFromDate(fromdate) + " to " + BTSLUtil.getDateStringFromDate(todate) + ")";
            Label label = new Label(lcol, lrow, keyName, getTopHeadingFont());
            Number number = null;
            worksheet.mergeCells(lcol, lrow, lcol + 4, lrow);
            worksheet.addCell(label);
            lcol = 0;
            lrow++;
            lrow++;
            if (list == null || list.isEmpty()) {
                keyName = Constants.getProperty("DATA_NOT_AVILABLE");
                label = new Label(lcol, lrow, keyName, getDateHeadingFont());
                worksheet.mergeCells(lcol, lrow, lcol + 4, lrow);
                worksheet.addCell(label);
            } else {
                boolean header = false;
                String str[] = null;
                for (int i = 0, j = list.size(); i < j; i++) {
                    if (!header) {
                        keyName = Constants.getProperty("SERIAL_NO");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        keyName = Constants.getProperty("USER_NAME");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        keyName = Constants.getProperty("USER_LOGIN_ID");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        keyName = Constants.getProperty("USER_MSISDN");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        keyName = Constants.getProperty("AVERAGE_C2C");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        header = true;
                        lcol = 0;
                        lrow++;
                    }

                    str = ((String) list.get(i)).split(":");
                    number = new Number(lcol++, lrow, i + 1, getNumberDataFont());
                    worksheet.addCell(number);
                    label = new Label(lcol++, lrow, NullToString(str[0]), getStringDataFont());
                    worksheet.addCell(label);
                    label = new Label(lcol++, lrow, NullToString(str[1]), getStringDataFont());
                    worksheet.addCell(label);
                    label = new Label(lcol++, lrow, NullToString(str[2]), getStringDataFont());
                    worksheet.addCell(label);
                    number = new Number(lcol++, lrow, Double.parseDouble(str[3]), getNumberDataFont());
                    worksheet.addCell(number);
                    lcol = 0;
                    lrow++;
                    if (lrow > MAX_ROWS) {
                        header = false;
                        sheetCount++;
                        worksheet = p_workbook.createSheet(sheetName + "-" + index++, sheetCount);
                        lcol = 0;
                        lrow = 0;
                    }
                }
            }
        } catch (BTSLBaseException e) {
            _log.error("writeAvgNoC2CPerChnlUser", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeAvgNoC2CPerChnlUser", "Exception=" + e.getMessage());
        } catch (RowsExceededException e) {
            _log.error("writeAvgNoC2CPerChnlUser", " RowsExceededException :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeAvgNoC2CPerChnlUser", "Exception=" + e.getMessage());
        } catch (WriteException e) {
            _log.error("writeAvgNoC2CPerChnlUser", " WriteException :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeAvgNoC2CPerChnlUser", "Exception=" + e.getMessage());
        } catch (ParseException e) {
            _log.error("writeAvgNoC2CPerChnlUser", " ParseException :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeAvgNoC2CPerChnlUser", "Exception=" + e.getMessage());
        } catch (Exception e) {
            _log.error("writeAvgNoC2CPerChnlUser", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeAvgNoC2CPerChnlUser", "Exception=" + e.getMessage());
        }
    }

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-writeAvgNoC2SPerChnlUser
     * 
     * @param p_workbook
     * @param p_hashMap
     * @throws BTSLBaseException
     *             Return :-void
     *             Nov 27, 2009 3:16:45 PM
     */
    private void writeAvgNoC2SPerChnlUser(WritableWorkbook p_workbook, HashMap p_hashMap) throws BTSLBaseException {
        final String METHOD_NAME = "writeAvgNoC2SPerChnlUser";
        WritableSheet worksheet = null;
        int lrow = 0, lcol = 0;
        try {
            final ArrayList list = (ArrayList) p_hashMap.get("AVG_NO_C2S_PER_CHNL_USERS");

            String keyName = Constants.getProperty("SHEET_NAME_AVG_NO_C2S_PER_CHNL_USERS");
            final String sheetName = keyName;
            int index = 1;
            boolean noDataFound = false;
            boolean isAddIndex = false;
            if (list != null && !list.isEmpty()) {
                if (list.size() > MAX_ROWS) {
                    isAddIndex = true;
                }
            } else {
                noDataFound = true;
            }
            sheetCount++;
            if (isAddIndex) {
                worksheet = p_workbook.createSheet(sheetName + "-" + index++, sheetCount);
            } else {
                worksheet = p_workbook.createSheet(sheetName, sheetCount);
            }
            final Date fromdate = (Date) p_hashMap.get("FROM_DATE");
            final Date todate = (Date) p_hashMap.get("TO_DATE");
            keyName = Constants.getProperty("AVG_NO_C2S_PER_CHNL_USERS");
            keyName = keyName + " (" + BTSLUtil.getDateStringFromDate(fromdate) + " to " + BTSLUtil.getDateStringFromDate(todate) + ")";
            Label label = new Label(lcol, lrow, keyName, getTopHeadingFont());
            Number number = null;
            worksheet.mergeCells(lcol, lrow, lcol + 4, lrow);
            worksheet.addCell(label);
            lcol = 0;
            lrow++;
            lrow++;
            if (list == null || list.isEmpty()) {
                keyName = Constants.getProperty("DATA_NOT_AVILABLE");
                label = new Label(lcol, lrow, keyName, getDateHeadingFont());
                worksheet.mergeCells(lcol, lrow, lcol + 4, lrow);
                worksheet.addCell(label);
            } else {
                boolean header = false;
                String str[] = null;
                for (int i = 0, j = list.size(); i < j; i++) {
                    if (!header) {
                        keyName = Constants.getProperty("SERIAL_NO");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        keyName = Constants.getProperty("USER_NAME");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        keyName = Constants.getProperty("USER_LOGIN_ID");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        keyName = Constants.getProperty("USER_MSISDN");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        keyName = Constants.getProperty("AVERAGE_C2S");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        header = true;
                        lcol = 0;
                        lrow++;
                    }

                    str = ((String) list.get(i)).split(":");
                    number = new Number(lcol++, lrow, i + 1, getNumberDataFont());
                    worksheet.addCell(number);
                    label = new Label(lcol++, lrow, NullToString(str[0]), getStringDataFont());
                    worksheet.addCell(label);
                    label = new Label(lcol++, lrow, NullToString(str[1]), getStringDataFont());
                    worksheet.addCell(label);
                    label = new Label(lcol++, lrow, NullToString(str[2]), getStringDataFont());
                    worksheet.addCell(label);
                    number = new Number(lcol++, lrow, Double.parseDouble(str[3]), getNumberDataFont());
                    worksheet.addCell(number);
                    lcol = 0;
                    lrow++;
                    if (lrow > MAX_ROWS) {
                        header = false;
                        sheetCount++;
                        worksheet = p_workbook.createSheet(sheetName + "-" + index++, sheetCount);
                        lcol = 0;
                        lrow = 0;
                    }

                }

            }
        } catch (BTSLBaseException e) {
            _log.error("writeAvgNoC2SPerChnlUser", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeAvgNoC2SPerChnlUser", "Exception=" + e.getMessage());
        } catch (RowsExceededException e) {
            _log.error("writeAvgNoC2SPerChnlUser", " RowsExceededException :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeAvgNoC2SPerChnlUser", "Exception=" + e.getMessage());
        } catch (WriteException e) {
            _log.error("writeAvgNoC2SPerChnlUser", " WriteException :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeAvgNoC2SPerChnlUser", "Exception=" + e.getMessage());
        } catch (ParseException e) {
            _log.error("writeAvgNoC2SPerChnlUser", " ParseException :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeAvgNoC2SPerChnlUser", "Exception=" + e.getMessage());
        } catch (Exception e) {
            _log.error("writeAvgNoC2SPerChnlUser", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeAvgNoC2SPerChnlUser", "Exception=" + e.getMessage());
        }

    }

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-writePctContributionC2STxn
     * 
     * @param p_workbook
     * @param p_hashMap
     * @param p_pct
     * @throws BTSLBaseException
     *             Return :-void
     *             Nov 27, 2009 3:16:34 PM
     */
    private void writePctContributionC2STxn(WritableWorkbook p_workbook, HashMap p_hashMap, String p_pct) throws BTSLBaseException {
        final String METHOD_NAME = "writePctContributionC2STxn";
        WritableSheet worksheet = null;
        int lrow = 0, lcol = 0;
        try {
            final HashMap map = (HashMap) p_hashMap.get("PCT_CONTRIBUTION-" + p_pct);
            final String frequency = (String) p_hashMap.get("FREQUENCY");
            String keyName = Constants.getProperty("SHEET_NAME_DISTRIBUTION_PCT");
            final String sheetName = keyName + "-" + p_pct;
            int index = 1;
            boolean noDataFound = false;
            boolean isAddIndex = false;
            final ArrayList list = (ArrayList) map.get("TOTAL_PCT_CONTRIBUTION_LIST");
            if (list != null && !list.isEmpty()) {
                if (list.size() > MAX_ROWS) {
                    isAddIndex = true;
                }
            } else {
                noDataFound = true;
            }
            sheetCount++;
            if (isAddIndex) {
                worksheet = p_workbook.createSheet(sheetName + "-" + index++, sheetCount);
            } else {
                worksheet = p_workbook.createSheet(sheetName, sheetCount);
            }
            final Date fromdate = (Date) p_hashMap.get("FROM_DATE");
            final Date todate = (Date) p_hashMap.get("TO_DATE");
            keyName = Constants.getProperty("C2S_DISTRIBUTION_PCT");
            keyName = p_pct + "% " + keyName + " (" + BTSLUtil.getDateStringFromDate(fromdate) + " to " + BTSLUtil.getDateStringFromDate(todate) + ")";
            Label label = new Label(lcol, lrow, keyName, getTopHeadingFont());
            Number number = null;
            worksheet.mergeCells(lcol, lrow, lcol + 5, lrow);
            worksheet.addCell(label);
            lcol = 0;
            lrow++;
            lrow++;
            if (list == null || list.isEmpty()) {
                keyName = Constants.getProperty("DATA_NOT_AVILABLE");
                label = new Label(lcol, lrow, keyName, getDateHeadingFont());
                worksheet.mergeCells(lcol, lrow, lcol + 5, lrow);
                worksheet.addCell(label);
            } else {
                boolean header = false;
                String str[] = null;

                keyName = Constants.getProperty("TOTAL_RECHARGE_AMOUNT");
                label = new Label(lcol, lrow, keyName, getDateHeadingFont());
                worksheet.mergeCells(lcol, lrow, lcol = lcol + 4, lrow);
                worksheet.addCell(label);
                number = new Number(++lcol, lrow, Double.parseDouble((String) map.get("TOTAL_RECHARGE_AMT")), getNumberDataFont());
                worksheet.addCell(number);
                lcol = 0;
                lrow++;

                keyName = p_pct + " " + Constants.getProperty("PCT_AMOUNT");
                label = new Label(lcol, lrow, keyName, getDateHeadingFont());
                worksheet.mergeCells(lcol, lrow, lcol = lcol + 4, lrow);
                worksheet.addCell(label);
                number = new Number(++lcol, lrow, Double.parseDouble((String) map.get("PCT_AMT")), getNumberDataFont());
                worksheet.addCell(number);

                lcol = 0;
                lrow++;
                lrow++;
                for (int i = 0, j = list.size(); i < j; i++) {
                    if (!header) {
                        keyName = Constants.getProperty("SERIAL_NO");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        keyName = Constants.getProperty("USER_NAME");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        keyName = Constants.getProperty("USER_LOGIN_ID");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        keyName = Constants.getProperty("USER_MSISDN");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        keyName = Constants.getProperty("AMOUNT");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        keyName = Constants.getProperty("USER_PCT");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        header = true;
                        lcol = 0;
                        lrow++;
                    }

                    str = ((String) list.get(i)).split(":");
                    number = new Number(lcol++, lrow, i + 1, getNumberDataFont());
                    worksheet.addCell(number);
                    label = new Label(lcol++, lrow, NullToString(str[0]), getStringDataFont());
                    worksheet.addCell(label);
                    label = new Label(lcol++, lrow, NullToString(str[1]), getStringDataFont());
                    worksheet.addCell(label);
                    label = new Label(lcol++, lrow, NullToString(str[2]), getStringDataFont());
                    worksheet.addCell(label);
                    number = new Number(lcol++, lrow, Double.parseDouble(str[3]), getNumberDataFont());
                    worksheet.addCell(number);
                    number = new Number(lcol++, lrow, Double.parseDouble(str[4]), getNumberDataFont());
                    worksheet.addCell(number);
                    lcol = 0;
                    lrow++;
                    if (lrow > MAX_ROWS) {
                        header = false;
                        sheetCount++;
                        worksheet = p_workbook.createSheet(sheetName + "-" + index++, sheetCount);
                        lcol = 0;
                        lrow = 0;
                    }

                }

            }

        } catch (BTSLBaseException e) {
            _log.error("writePctContributionC2STxn", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writePctContributionC2STxn", "Exception=" + e.getMessage());
        } catch (RowsExceededException e) {
            _log.error("writePctContributionC2STxn", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writePctContributionC2STxn", "Exception=" + e.getMessage());
        } catch (WriteException e) {
            _log.error("writePctContributionC2STxn", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writePctContributionC2STxn", "Exception=" + e.getMessage());
        } catch (ParseException e) {
            _log.error("writePctContributionC2STxn", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writePctContributionC2STxn", "Exception=" + e.getMessage());
        } catch (Exception e) {
            _log.error("writePctContributionC2STxn", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writePctContributionC2STxn", "Exception=" + e.getMessage());
        }
    }

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-writeAvgCommissionPerChnlUser
     * 
     * @param p_workbook
     * @param p_hashMap
     * @throws BTSLBaseException
     *             Return :-void
     *             Nov 30, 2009 3:30:04 PM
     */
    private void writeAvgCommissionPerChnlUser(WritableWorkbook p_workbook, HashMap p_hashMap) throws BTSLBaseException {
        final String METHOD_NAME = "writeAvgCommissionPerChnlUser";
        WritableSheet worksheet = null;
        int lrow = 0, lcol = 0;
        try {
            final ArrayList list = (ArrayList) p_hashMap.get("AVG_COMM_PER_CHNL_USERS");
            final String frequency = (String) p_hashMap.get("FREQUENCY");
            String keyName = Constants.getProperty("SHEET_NAME_AVG_COMM_PER_CHNL_USERS");
            final String sheetName = keyName;
            int index = 1;
            boolean noDataFound = false;
            boolean isAddIndex = false;
            if (list != null && list.size() > 0) {
                if (list.size() > MAX_ROWS) {
                    isAddIndex = true;
                }
            } else {
                noDataFound = true;
            }
            sheetCount++;
            if (isAddIndex) {
                worksheet = p_workbook.createSheet(sheetName + "-" + index++, sheetCount);
            } else {
                worksheet = p_workbook.createSheet(sheetName, sheetCount);
            }
            final Date fromdate = (Date) p_hashMap.get("FROM_DATE");
            final Date todate = (Date) p_hashMap.get("TO_DATE");
            keyName = Constants.getProperty("AVG_COMM_PER_CHNL_USERS");
            keyName = keyName + " (" + BTSLUtil.getDateStringFromDate(fromdate) + " to " + BTSLUtil.getDateStringFromDate(todate) + ")";
            Label label = new Label(lcol, lrow, keyName, getTopHeadingFont());
            Number number = null;
            worksheet.mergeCells(lcol, lrow, lcol + 5, lrow);
            worksheet.addCell(label);
            lcol = 0;
            lrow++;
            lrow++;
            if (list == null || list.size() <= 0) {
                keyName = Constants.getProperty("DATA_NOT_AVILABLE");
                label = new Label(lcol, lrow, keyName, getDateHeadingFont());
                worksheet.mergeCells(lcol, lrow, lcol + 5, lrow);
                worksheet.addCell(label);
            } else {
                boolean header = false;
                String str[] = null;
                for (int i = 0, j = list.size(); i < j; i++) {
                    if (!header) {
                        keyName = Constants.getProperty("SERIAL_NO");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        keyName = Constants.getProperty("USER_NAME");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        keyName = Constants.getProperty("USER_LOGIN_ID");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        keyName = Constants.getProperty("USER_MSISDN");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        keyName = Constants.getProperty("PRODUCT");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        keyName = Constants.getProperty("AVERAGE_COMMISSION");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        header = true;
                        lcol = 0;
                        lrow++;
                    }

                    str = ((String) list.get(i)).split(":");
                    number = new Number(lcol++, lrow, i + 1, getNumberDataFont());
                    worksheet.addCell(number);
                    label = new Label(lcol++, lrow, NullToString(str[0]), getStringDataFont());
                    worksheet.addCell(label);
                    label = new Label(lcol++, lrow, NullToString(str[1]), getStringDataFont());
                    worksheet.addCell(label);
                    label = new Label(lcol++, lrow, NullToString(str[2]), getStringDataFont());
                    worksheet.addCell(label);
                    label = new Label(lcol++, lrow, NullToString(str[4]), getStringDataFont());
                    worksheet.addCell(label);
                    number = new Number(lcol++, lrow, Double.parseDouble(str[3]), getNumberDataFont());
                    worksheet.addCell(number);
                    lcol = 0;
                    lrow++;
                    if (lrow > MAX_ROWS) {
                        header = false;
                        sheetCount++;
                        worksheet = p_workbook.createSheet(sheetName + "-" + index++, sheetCount);
                        lcol = 0;
                        lrow = 0;
                    }

                }

            }
        } catch (BTSLBaseException e) {
            _log.error("writeAvgNoC2SPerChnlUser", " BTSLBaseException :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeAvgNoC2SPerChnlUser", "Exception=" + e.getMessage());
        } catch (RowsExceededException e) {
            _log.error("writeAvgNoC2SPerChnlUser", " RowsExceededException :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeAvgNoC2SPerChnlUser", "Exception=" + e.getMessage());
        } catch (WriteException e) {
            _log.error("writeAvgNoC2SPerChnlUser", " WriteException :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeAvgNoC2SPerChnlUser", "Exception=" + e.getMessage());
        } catch (ParseException e) {
            _log.error("writeAvgNoC2SPerChnlUser", " ParseException :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeAvgNoC2SPerChnlUser", "Exception=" + e.getMessage());
        } catch (Exception e) {
            _log.error("writeAvgNoC2SPerChnlUser", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeAvgNoC2SPerChnlUser", "Exception=" + e.getMessage());
        }

    }

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-writeAvgBonusPerChnlUser
     * 
     * @param p_workbook
     * @param p_hashMap
     * @throws BTSLBaseException
     *             Return :-void
     *             Dec 3, 2009 1:28:24 PM
     */
    private void writeAvgBonusPerChnlUser(WritableWorkbook p_workbook, HashMap p_hashMap) throws BTSLBaseException {
        final String METHOD_NAME = "writeAvgBonusPerChnlUser";
        WritableSheet worksheet = null;
        int lrow = 0, lcol = 0;
        try {
            final ArrayList list = (ArrayList) p_hashMap.get("AVG_BONUS_PER_CHNL_USERS");
            final String frequency = (String) p_hashMap.get("FREQUENCY");
            String keyName = Constants.getProperty("SHEET_NAME_AVG_BONUS_PER_CHNL_USERS");
            final String sheetName = keyName;
            int index = 1;
            boolean noDataFound = false;
            boolean isAddIndex = false;
            if (list != null && list.size() > 0) {
                if (list.size() > MAX_ROWS) {
                    isAddIndex = true;
                }
            } else {
                noDataFound = true;
            }
            sheetCount++;
            if (isAddIndex) {
                worksheet = p_workbook.createSheet(sheetName + "-" + index++, sheetCount);
            } else {
                worksheet = p_workbook.createSheet(sheetName, sheetCount);
            }
            final Date fromdate = (Date) p_hashMap.get("FROM_DATE");
            final Date todate = (Date) p_hashMap.get("TO_DATE");
            keyName = Constants.getProperty("AVG_BONUS_PER_CHNL_USERS");
            keyName = keyName + " (" + BTSLUtil.getDateStringFromDate(fromdate) + " to " + BTSLUtil.getDateStringFromDate(todate) + ")";
            Label label = new Label(lcol, lrow, keyName, getTopHeadingFont());
            Number number = null;
            worksheet.mergeCells(lcol, lrow, lcol + 4, lrow);
            worksheet.addCell(label);
            lcol = 0;
            lrow++;
            lrow++;
            if (list == null || list.size() <= 0) {
                keyName = Constants.getProperty("DATA_NOT_AVILABLE");
                label = new Label(lcol, lrow, keyName, getDateHeadingFont());
                worksheet.mergeCells(lcol, lrow, lcol + 4, lrow);
                worksheet.addCell(label);
            } else {
                boolean header = false;
                String str[] = null;
                for (int i = 0, j = list.size(); i < j; i++) {
                    if (!header) {
                        keyName = Constants.getProperty("SERIAL_NO");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        keyName = Constants.getProperty("USER_NAME");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        keyName = Constants.getProperty("USER_LOGIN_ID");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        keyName = Constants.getProperty("USER_MSISDN");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        keyName = Constants.getProperty("AVERAGE_C2S_TXN_BONUS");
                        label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                        worksheet.addCell(label);
                        header = true;
                        lcol = 0;
                        lrow++;
                    }

                    str = ((String) list.get(i)).split(":");
                    number = new Number(lcol++, lrow, i + 1, getNumberDataFont());
                    worksheet.addCell(number);
                    label = new Label(lcol++, lrow, NullToString(str[0]), getStringDataFont());
                    worksheet.addCell(label);
                    label = new Label(lcol++, lrow, NullToString(str[1]), getStringDataFont());
                    worksheet.addCell(label);
                    label = new Label(lcol++, lrow, NullToString(str[2]), getStringDataFont());
                    worksheet.addCell(label);
                    number = new Number(lcol++, lrow, Double.parseDouble(str[3]), getNumberDataFont());
                    worksheet.addCell(number);
                    lcol = 0;
                    lrow++;
                    if (lrow > MAX_ROWS) {
                        header = false;
                        sheetCount++;
                        worksheet = p_workbook.createSheet(sheetName + "-" + index++, sheetCount);
                        lcol = 0;
                        lrow = 0;
                    }

                }

            }
        } catch (BTSLBaseException e) {
            _log.error("writeAvgNoC2SPerChnlUser", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeAvgNoC2SPerChnlUser", "Exception=" + e.getMessage());
        } catch (RowsExceededException e) {
            _log.error("writeAvgNoC2SPerChnlUser", " RowsExceededException :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeAvgNoC2SPerChnlUser", "Exception=" + e.getMessage());
        } catch (WriteException e) {
            _log.error("writeAvgNoC2SPerChnlUser", " WriteException :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeAvgNoC2SPerChnlUser", "Exception=" + e.getMessage());
        } catch (ParseException e) {
            _log.error("writeAvgNoC2SPerChnlUser", " ParseException :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeAvgNoC2SPerChnlUser", "Exception=" + e.getMessage());
        } catch (Exception e) {
            _log.error("writeAvgNoC2SPerChnlUser", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeAvgNoC2SPerChnlUser", "Exception=" + e.getMessage());
        }

    }

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-writeC2STransationDateWise
     * 
     * @param p_row
     * @param worksheet
     * @param p_hashMap
     * @throws BTSLBaseException
     *             Return :-int
     *             Dec 2, 2009 1:15:34 PM
     */
    private void writeC2STransationDateWise(WritableWorkbook p_workbook, HashMap p_hashMap) throws BTSLBaseException {
        final String METHOD_NAME = "writeC2STransationDateWise";
        int lcol = 0;
        int lrow = 0;
        WritableSheet worksheet = null;
        try {

            final ArrayList list = (ArrayList) p_hashMap.get("TOTAL_C2S_TXT_DATE_WISE");
            final String frequency = (String) p_hashMap.get("FREQUENCY");
            String keyName = Constants.getProperty("SHEET_NAME_TOTAL_C2S_TXT_DATE_WISE");
            sheetCount++;
            worksheet = p_workbook.createSheet(keyName, sheetCount);
            final Date fromdate = (Date) p_hashMap.get("FROM_DATE");
            final Date todate = (Date) p_hashMap.get("TO_DATE");
            keyName = Constants.getProperty("TOTAL_C2S_TXT_DATE_WISE");
            keyName = keyName + " (" + BTSLUtil.getDateStringFromDate(fromdate) + " to " + BTSLUtil.getDateStringFromDate(todate) + ")";
            Label label = new Label(lcol, lrow, keyName, getTopHeadingFont());
            Number number = null;
            worksheet.mergeCells(lcol, lrow, lcol + 5, lrow);
            worksheet.addCell(label);
            lcol = 0;
            lrow++;
            lrow++;
            if (list == null || list.size() <= 0) {
                keyName = Constants.getProperty("DATA_NOT_AVILABLE");
                label = new Label(lcol, lrow, keyName, getDateHeadingFont());
                worksheet.mergeCells(lcol, lrow, lcol + 5, lrow);
                worksheet.addCell(label);
            } else {

                keyName = Constants.getProperty("TXN_DATE");
                label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                worksheet.addCell(label);
                keyName = Constants.getProperty("REQUEST_COUNT");
                label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                worksheet.addCell(label);
                keyName = Constants.getProperty("REQUESTED_AMOUNT");
                label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                worksheet.addCell(label);
                keyName = Constants.getProperty("SENDER_TRF_AMOUNT");
                label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                worksheet.addCell(label);
                keyName = Constants.getProperty("RECEIVER_CREDIT_AMOUNT");
                label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                worksheet.addCell(label);
                keyName = Constants.getProperty("RECEIVER_BONUS");
                label = new Label(lcol++, lrow, keyName, getDateHeadingFont());
                worksheet.addCell(label);
                lcol = 0;
                lrow++;

                String[] str = null;
                for (int i = 0, j = list.size(); i < j; i++) {
                    keyName = (String) list.get(i);
                    str = keyName.split(":");
                    label = new Label(lcol++, lrow, str[0], getStringDataFont());
                    worksheet.addCell(label);
                    number = new Number(lcol++, lrow, Double.parseDouble(str[1]), getNumberDataFont());
                    worksheet.addCell(number);
                    number = new Number(lcol++, lrow, Double.parseDouble(str[2]), getNumberDataFont());
                    worksheet.addCell(number);
                    number = new Number(lcol++, lrow, Double.parseDouble(str[3]), getNumberDataFont());
                    worksheet.addCell(number);
                    number = new Number(lcol++, lrow, Double.parseDouble(str[4]), getNumberDataFont());
                    worksheet.addCell(number);
                    number = new Number(lcol++, lrow, Double.parseDouble(str[5]), getNumberDataFont());
                    worksheet.addCell(number);
                    lcol = 0;
                    lrow++;
                }
            }
        } catch (BTSLBaseException e) {
            _log.error("writeC2STransationDateWise", " BTSLBaseException :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeC2STransationDateWise", "Exception=" + e.getMessage());
        } catch (RowsExceededException e) {
            _log.error("writeC2STransationDateWise", " RowsExceededException :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeC2STransationDateWise", "Exception=" + e.getMessage());
        } catch (WriteException e) {
            _log.error("writeC2STransationDateWise", " WriteException :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeC2STransationDateWise", "Exception=" + e.getMessage());
        } catch (ParseException e) {
            _log.error("writeC2STransationDateWise", " ParseException :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeC2STransationDateWise", "Exception=" + e.getMessage());
        } catch (Exception e) {
            _log.error("writeC2STransationDateWise", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeC2STransationDateWise", "Exception=" + e.getMessage());
        }
    }

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-writeRevenuesDomainWise
     * 
     * @param p_row
     * @param worksheet
     * @param p_hashMap
     * @throws BTSLBaseException
     *             Return :-int
     *             Dec 2, 2009 1:19:58 PM
     */
    private int writeRevenuesDomainWise(int p_row, WritableSheet worksheet, HashMap p_hashMap) throws BTSLBaseException {
        final String METHOD_NAME = "writeRevenuesDomainWise";
        int col = 0;
        final int row = p_row;
        try {
            String keyName = Constants.getProperty("TOTAL_REVENUES");
            Label label = new Label(col, p_row, keyName, getTopHeadingFont());
            worksheet.mergeCells(col, p_row, col + 2, row);
            worksheet.addCell(label);
            col = 0;
            p_row++;

            keyName = Constants.getProperty("SERIAL_NO");
            label = new Label(col++, p_row, keyName, getDateHeadingFont());
            worksheet.addCell(label);
            keyName = Constants.getProperty("DOMAIN_NAME");
            label = new Label(col++, p_row, keyName, getDateHeadingFont());
            worksheet.addCell(label);
            keyName = Constants.getProperty("TOTAL_O2C_TRANSFER");
            label = new Label(col++, p_row, keyName, getDateHeadingFont());
            worksheet.addCell(label);
            col = 0;
            p_row++;
            Number number = null;

            final ArrayList list = (ArrayList) p_hashMap.get("TOTAL_REVENUES");
            String[] str = null;
            for (int i = 0, j = list.size(); i < j; i++) {
                keyName = (String) list.get(i);
                str = keyName.split(":");
                number = new Number(col++, p_row, i + 1, getNumberDataFont());
                worksheet.addCell(number);
                ;
                label = new Label(col++, p_row, NullToString(str[0]), getStringDataFont());
                worksheet.addCell(label);
                number = new Number(col++, p_row, Double.parseDouble(str[1]), getNumberDataFont());
                worksheet.addCell(number);
                col = 0;
                p_row++;
            }
        } catch (BTSLBaseException e) {
            _log.error("writeC2STransationDateWise", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeC2STransationDateWise", "Exception=" + e.getMessage());
        } catch (RowsExceededException e) {
            _log.error("writeC2STransationDateWise", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeC2STransationDateWise", "Exception=" + e.getMessage());
        } catch (WriteException e) {
            _log.error("writeC2STransationDateWise", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeC2STransationDateWise", "Exception=" + e.getMessage());
        } catch (Exception e) {
            _log.error("writeC2STransationDateWise", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeC2STransationDateWise", "Exception=" + e.getMessage());
        }
        return p_row;
    }

    /**
     * Description :-
     * Author :-ved.sharma
     * Method :-writeCommissionDomainWise
     * 
     * @param p_row
     * @param worksheet
     * @param p_hashMap
     * @throws BTSLBaseException
     *             Return :-int
     *             Dec 3, 2009 9:39:35 AM
     */
    private int writeCommissionDomainWise(int p_row, WritableSheet worksheet, HashMap p_hashMap) throws BTSLBaseException {
        final String METHOD_NAME = "writeCommissionDomainWise";
        int col = 0;
        final int row = p_row;
        try {
            String keyName = Constants.getProperty("SUM_COMM_DOMAIN_WISE");
            Label label = new Label(col, p_row, keyName, getTopHeadingFont());
            worksheet.mergeCells(col, p_row, col + 3, row);
            worksheet.addCell(label);
            col = 0;
            p_row++;

            keyName = Constants.getProperty("SERIAL_NO");
            label = new Label(col++, p_row, keyName, getDateHeadingFont());
            worksheet.addCell(label);
            keyName = Constants.getProperty("DOMAIN_NAME");
            label = new Label(col++, p_row, keyName, getDateHeadingFont());
            worksheet.addCell(label);
            keyName = Constants.getProperty("PRODUCT");
            label = new Label(col++, p_row, keyName, getDateHeadingFont());
            worksheet.addCell(label);
            keyName = Constants.getProperty("TOTAL_COMMISSION");
            label = new Label(col++, p_row, keyName, getDateHeadingFont());
            worksheet.addCell(label);
            col = 0;
            p_row++;
            Number number = null;

            final ArrayList list = (ArrayList) p_hashMap.get("SUM_COMM_DOMAIN_WISE");
            String[] str = null;
            for (int i = 0, j = list.size(); i < j; i++) {
                keyName = (String) list.get(i);
                str = keyName.split(":");
                number = new Number(col++, p_row, i + 1, getNumberDataFont());
                worksheet.addCell(number);
                ;
                label = new Label(col++, p_row, NullToString(str[0]), getStringDataFont());
                worksheet.addCell(label);
                label = new Label(col++, p_row, NullToString(str[1]), getStringDataFont());
                worksheet.addCell(label);
                number = new Number(col++, p_row, Double.parseDouble(str[2]), getNumberDataFont());
                worksheet.addCell(number);
                col = 0;
                p_row++;
            }
        } catch (BTSLBaseException e) {
            _log.error("writeCommissionDomainWise", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeCommissionDomainWise", "Exception=" + e.getMessage());
        } catch (RowsExceededException e) {
            _log.error("writeCommissionDomainWise", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeCommissionDomainWise", "Exception=" + e.getMessage());
        } catch (WriteException e) {
            _log.error("writeCommissionDomainWise", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeCommissionDomainWise", "Exception=" + e.getMessage());
        } catch (Exception e) {
            _log.error("writeCommissionDomainWise", " Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, "writeCommissionDomainWise", "Exception=" + e.getMessage());
        }
        return p_row;
    }

    public String NullToString(String p_str) {
        if (p_str == null || "null".equalsIgnoreCase(p_str)) {
            return "";
        } else {
            return p_str;
        }
    }
}

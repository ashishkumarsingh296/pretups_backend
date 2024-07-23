package com.btsl.pretups.processes;

/**
 * @(#)DailyReportWriteInXL.java
 *                               Copyright(c) 2006, Bharti Telesoft Ltd. All
 *                               Rights Reserved
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Author Date History
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               Ved Prakash Sharma 21/09/2006 Initial Creation
 *                               ----------------------------------------------
 *                               --
 *                               ----------------------------------------------
 *                               ---
 *                               This class use for generate daily reports in
 *                               xls format.
 */
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.processes.businesslogic.DailyReportVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.xl.ExcelStyle;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * @author ved.sharma
 */
public class DailyReportWriteInXL {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private Locale locale = null;
    private int col = 0;
    private int row = 3;

    /**
     * method writeExcel
     * This method start the XLS sheet.
     * 
     * @param p_hashMap
     * @param p_locale
     * @param p_fileName
     * @throws BTSLBaseException
     */
    public void writeExcel(HashMap p_hashMap, Locale p_locale, String p_fileName) throws BTSLBaseException {
        final String methodName = "writeExcel";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " p_hashMap:" + p_hashMap + " p_locale: " + p_locale + " p_fileName: " + p_fileName);
        }
        WritableWorkbook workbook = null;
        WritableSheet worksheet1 = null;
        WritableSheet worksheet2 = null;
        WritableSheet worksheet3 = null;
        WritableSheet worksheet4 = null;

        locale = p_locale;
        File fileName = null;
        String repHeader = null;
        String keyName = null;
        int index = 0;
        int hourlyRowCount = 0;
        boolean sheetCreated = false;
        int mgtRow = 0;
        try {
            final Date reportDate = (Date) p_hashMap.get("REPORT_DATE");
            final String networkCode = (String) p_hashMap.get("NETWORK_CODE");
            final ArrayList networkList = (ArrayList) p_hashMap.get("NETWORK_LIST");
            fileName = new File(p_fileName);
            workbook = Workbook.createWorkbook(fileName);

            final ArrayList moduleList = LookupsCache.loadLookupDropDown(PretupsI.MODULE_TYPE, true);
            for (int m = 0, n = moduleList.size(); m < n; m++) {
                final ListValueVO listValueVO = (ListValueVO) moduleList.get(m);
                if (PretupsI.C2S_MODULE.equals(listValueVO.getValue())) {
                    repHeader = BTSLUtil.getMessage(locale, "dailyreport.c2s.sheet.name", null);
                    worksheet1 = workbook.createSheet(repHeader, index++);
                    repHeader = BTSLUtil.getMessage(locale, "dailyreport.c2s.failure.recharge.report", null);
                    // write into C2S sheet
                    row = writeExcelHeader(worksheet1, reportDate, repHeader);
                    if (PretupsI.ALL.equalsIgnoreCase(networkCode)) {
                        NetworkVO networkVO = null;
                        for (int i = 0, j = networkList.size(); i < j; i++)// for
                        // multile
                        // network
                        {
                            networkVO = (NetworkVO) networkList.get(i);
                            row = writeExcelC2S(worksheet1, p_hashMap, row, networkVO.getNetworkCode());
                            row++;
                        }
                    } else {
                        row = writeExcelC2S(worksheet1, p_hashMap, row, networkCode);
                    }

                    final ArrayList c2s_productList = (ArrayList) p_hashMap.get("C2S_PRODUCT_LIST");
                    final ArrayList c2sTransferSummaryProductList = (ArrayList) p_hashMap.get("C2S_TRANSFER_SUMMARY_PRODUCT");
                    row += 2;
                    col = 0;
                    keyName = BTSLUtil.getMessage(locale, "dailyreport.c2ssummary", null);
                    row = writeExcelSummaryProductWise(worksheet1, c2sTransferSummaryProductList, c2s_productList, row, keyName);
                    if (hourlyRowCount == 0) {
                        repHeader = BTSLUtil.getMessage(locale, "dailyreport.hourlybreakup.sheet.name", null);
                        worksheet3 = workbook.createSheet(repHeader, index);

                        // write into Hourly sheet
                        row = 0;
                        repHeader = BTSLUtil.getMessage(locale, "dailyreport.success.recharge.report", null);
                        row = hourlyRowCount = writeExcelHeader(worksheet3, reportDate, repHeader);
                    } else {
                        row = ++hourlyRowCount;
                    }
                    hourlyRowCount = writeExcelC2SHourly(worksheet3, p_hashMap, row, networkCode);

                    // added for mgt summary report
                    // ankit
                    repHeader = BTSLUtil.getMessage(locale, "mgtreport.sheet.name", null);
                    if (!sheetCreated) {
                        worksheet4 = workbook.createSheet(repHeader, index++);
                        repHeader = BTSLUtil.getMessage(locale, "mgtreport.recharge.header", null);
                        mgtRow = writeMgtExcelHeader(worksheet4, reportDate, repHeader);
                        sheetCreated = true;
                    } else {
                        mgtRow++;
                    }

                    mgtRow = writeMgtExcelC2S(worksheet4, p_hashMap, mgtRow);
                    mgtRow++;
                } else if (PretupsI.P2P_MODULE.equals(listValueVO.getValue())) {
                    repHeader = BTSLUtil.getMessage(locale, "dailyreport.p2p.sheet.name", null);
                    worksheet2 = workbook.createSheet(repHeader, index++);
                    // write into P2P sheet
                    row = 0;
                    repHeader = BTSLUtil.getMessage(locale, "dailyreport.p2p.failure.recharge.report", null);
                    row = writeExcelHeader(worksheet2, reportDate, repHeader);
                    if (PretupsI.ALL.equalsIgnoreCase(networkCode))// for
                    // multile
                    // network
                    {
                        NetworkVO networkVO = null;
                        for (int i = 0, j = networkList.size(); i < j; i++) {
                            networkVO = (NetworkVO) networkList.get(i);
                            row = row = writeExcelP2P(worksheet2, p_hashMap, row, networkVO.getNetworkCode());
                            row++;
                        }
                    } else {
                        row = writeExcelP2P(worksheet2, p_hashMap, row, networkCode);
                    }

                    row += 2;
                    col = 0;
                    final ArrayList p2p_productList = (ArrayList) p_hashMap.get("P2P_PRODUCT_LIST");
                    final ArrayList p2pTransferSummaryProductList = (ArrayList) p_hashMap.get("P2P_TRANSFER_SUMMARY_PRODUCT");
                    keyName = BTSLUtil.getMessage(locale, "dailyreport.p2psummary", null);
                    row = writeExcelSummaryProductWise(worksheet2, p2pTransferSummaryProductList, p2p_productList, row, keyName);

                    if (hourlyRowCount == 0) {
                        repHeader = BTSLUtil.getMessage(locale, "dailyreport.hourlybreakup.sheet.name", null);
                        worksheet3 = workbook.createSheet(repHeader, index);

                        // write into Hourly sheet
                        row = 0;
                        repHeader = BTSLUtil.getMessage(locale, "dailyreport.success.recharge.report", null);
                        row = hourlyRowCount = writeExcelHeader(worksheet3, reportDate, repHeader);
                    } else {
                        row = ++hourlyRowCount;
                    }
                    hourlyRowCount = writeExcelP2PHourly(worksheet3, p_hashMap, row, networkCode);

                    // added for mgt summary report
                    repHeader = BTSLUtil.getMessage(locale, "mgtreport.sheet.name", null);
                    if (!sheetCreated) {
                        worksheet4 = workbook.createSheet(repHeader, index++);
                        repHeader = BTSLUtil.getMessage(locale, "mgtreport.recharge.header", null);
                        mgtRow = writeMgtExcelHeader(worksheet4, reportDate, repHeader);
                        sheetCreated = true;
                    } else {
                        mgtRow++;
                    }
                    mgtRow = writeMgtExcelP2P(worksheet4, p_hashMap, mgtRow);
                    mgtRow++;
                }
            }
            boolean interfaceAdded = false;
            for (int m = 0, n = moduleList.size(); m < n; m++) {
                col = 0;
                final ListValueVO listValueVO = (ListValueVO) moduleList.get(m);
                if (PretupsI.C2S_MODULE.equals(listValueVO.getValue())) {
                    if (!interfaceAdded) {
                        mgtRow++;
                        keyName = BTSLUtil.getMessage(locale, "mgtreport.header.interfaceWise", null);
                        final Label label = new Label(col, mgtRow, keyName, ExcelStyle.getSecondTopHeadingFont2());
                        worksheet4.mergeCells(col, mgtRow, col + 5, mgtRow);
                        worksheet4.addCell(label);
                        interfaceAdded = true;
                    } else {
                        mgtRow++;
                    }
                    mgtRow = writeMgtExcelInterfaceWiseC2S(worksheet4, p_hashMap, mgtRow);
                } else if (PretupsI.P2P_MODULE.equals(listValueVO.getValue())) {
                    if (!interfaceAdded) {
                        mgtRow++;
                        keyName = BTSLUtil.getMessage(locale, "mgtreport.header.interfaceWise", null);
                        final Label label = new Label(col, mgtRow, keyName, ExcelStyle.getSecondTopHeadingFont2());
                        worksheet4.mergeCells(col, mgtRow, col + 5, mgtRow);
                        worksheet4.addCell(label);
                        interfaceAdded = true;
                    } else {
                        mgtRow++;
                    }
                    mgtRow = writeMgtExcelInterfaceWiseP2P(worksheet4, p_hashMap, mgtRow);
                }
            }
            workbook.write();
        } catch (BTSLBaseException be) {
            if (fileName != null) {
            	boolean isDeleted = fileName.delete();
                if(isDeleted){
                 _log.debug(methodName, "File deleted successfully");
                }
            }
            _log.errorTrace(methodName, be);
            _log.error(methodName, " BTSLBaseException	e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            if (fileName != null) {
            	boolean isDeleted = fileName.delete();
                if(isDeleted){
                 _log.debug(methodName, "File deleted successfully");
                }
            }
            _log.errorTrace(methodName, e);
            _log.error(methodName, " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "Exception=" + e.getMessage());
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            worksheet1 = null;
            worksheet2 = null;
            worksheet3 = null;
            worksheet4 = null;
            workbook = null;
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exiting");
            }
        }
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
        final String methodName = "writeExcelHeader";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " p_reportDate:" + p_reportDate + " p_Header=" + p_Header);
        }
        int rowCount = 0;
        col = 0;
        try {
            p_worksheet1.setColumnView(0, 27);
            p_worksheet1.setColumnView(1, 27);
            for (int i = 2; i < 30; i++) {
                p_worksheet1.setColumnView(i, 16);
            }

            final Date currentDate = new Date();
            String keyName = BTSLUtil.getMessage(locale, "dailyreport.header", null);
            Label label = new Label(col, rowCount, keyName, ExcelStyle.getTopHeadingFont());
            p_worksheet1.mergeCells(col, rowCount, col + 5, rowCount);
            p_worksheet1.addCell(label);
            col = 0;
            rowCount++;
            label = new Label(col, rowCount, "", ExcelStyle.getSecondTopHeadingFont());
            p_worksheet1.mergeCells(col, rowCount, ++col, rowCount);
            p_worksheet1.addCell(label);

            label = new Label(++col, rowCount, p_Header, ExcelStyle.getSecondTopHeadingFont2());
            p_worksheet1.mergeCells(col, rowCount, ++col, rowCount);
            p_worksheet1.addCell(label);

            label = new Label(++col, rowCount, "", ExcelStyle.getSecondTopHeadingFont());
            p_worksheet1.mergeCells(col, rowCount, ++col, rowCount);
            p_worksheet1.addCell(label);

            col = 0;
            rowCount++;
            keyName = BTSLUtil.getMessage(locale, "dailyreport.date", null) + BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(p_reportDate));
            label = new Label(col, rowCount, keyName, ExcelStyle.getSecondTopHeadingFont());
            p_worksheet1.mergeCells(col, rowCount, ++col, rowCount);
            p_worksheet1.addCell(label);

            label = new Label(++col, rowCount, "", ExcelStyle.getSecondTopHeadingFont());
            p_worksheet1.mergeCells(col, rowCount, ++col, rowCount);
            p_worksheet1.addCell(label);

            keyName = BTSLUtil.getMessage(locale, "dailyreport.generatedon", null) + BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(currentDate));
            label = new Label(++col, rowCount, keyName, ExcelStyle.getSecondTopHeadingFont());
            p_worksheet1.mergeCells(col, rowCount, ++col, rowCount);
            p_worksheet1.addCell(label);
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            _log.error(methodName, " BTSLBaseException e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "Exception=" + e.getMessage());
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Exiting row:" + row);
        }
        return rowCount;
    }

    /**
     * method writeExcelC2S
     * This method write to start data on first sheet for c2s.
     * 
     * @param p_worksheet
     * @param p_hashMap
     * @param p_row
     * @param p_NetwokCode
     * @return
     * @throws BTSLBaseException
     */
    private int writeExcelC2S(WritableSheet p_worksheet, HashMap p_hashMap, int p_row, String p_NetwokCode) throws BTSLBaseException {
        final String methodName = "writeExcelC2S";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " p_hashMap.size():" + p_hashMap.size() + " p_row: " + p_row + " p_NetwokCode=" + p_NetwokCode);
        }
        try {
            final ArrayList c2sList = (ArrayList) p_hashMap.get("C2S_FAIL_RECHARGE");
            final ArrayList productList = (ArrayList) p_hashMap.get("C2S_PRODUCT_LIST");
            final ArrayList networkList = (ArrayList) p_hashMap.get("NETWORK_LIST");
            final String loadTest = (String) p_hashMap.get("LOAD_TEST");

            DailyReportVO dailyReportVO = null;
            final int productListSize = productList.size();
            dailyReportVO = null;
            String keyName = null;
            Label label = null;
            Number number = null;
            String networkName = null;
            String oldServiceCode = null;
            String newServiceCode = null;

            final DailyReportAnalysis dailyReportAnalysis = new DailyReportAnalysis();
            networkName = dailyReportAnalysis.getNetworkName(p_NetwokCode, networkList);

            col = 0;
            p_row++;
            keyName = BTSLUtil.getMessage(locale, "dailyreport.network", null) + networkName;
            label = new Label(col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row + 1);
            p_worksheet.addCell(label);

            keyName = BTSLUtil.getMessage(locale, "dailyreport.product", null);
            label = new Label(col + 1, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(++col, p_row, (col + productListSize * 4) - 1, p_row);
            p_worksheet.addCell(label);
            p_row++;
            if (productListSize == 1) {
                final String[] product = { BTSLUtil.getMessage(locale, ((DailyReportVO) productList.get(0)).getProductCode(), null) };
                keyName = BTSLUtil.getMessage(locale, "dailyreport.fortheday", product);
                label = new Label(col, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet.mergeCells(col, p_row, ++col, p_row);
                p_worksheet.addCell(label);

                keyName = BTSLUtil.getMessage(locale, "dailyreport.forthemonth", product);
                label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet.mergeCells(col, p_row, ++col, p_row);
                p_worksheet.addCell(label);
            } else {// heading for multiple product
                for (int k = 0; k < productListSize; k++) {
                    final String[] product = { BTSLUtil.getMessage(locale, ((DailyReportVO) productList.get(k)).getProductCode(), null) };
                    if (BTSLUtil.isNullArray(product)) {
                        product[0] = ((DailyReportVO) productList.get(k)).getProductCode();
                    }
                    keyName = BTSLUtil.getMessage(locale, "dailyreport.fortheday", product);
                    label = new Label(col, p_row, keyName, ExcelStyle.getHeadingFont());
                    p_worksheet.mergeCells(col, p_row, ++col, p_row);
                    p_worksheet.addCell(label);

                    keyName = BTSLUtil.getMessage(locale, "dailyreport.forthemonth", product);
                    label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
                    p_worksheet.mergeCells(col, p_row, ++col, p_row);
                    p_worksheet.addCell(label);
                    col++;
                }
            }
            p_row++;
            col = 0;
            keyName = BTSLUtil.getMessage(locale, "dailyreport.service", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.addCell(label);

            keyName = BTSLUtil.getMessage(locale, "dailyreport.errorcodes", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.addCell(label);
            if (productListSize == 1) {
                keyName = BTSLUtil.getMessage(locale, "dailyreport.count", null);
                label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet.addCell(label);

                keyName = BTSLUtil.getMessage(locale, "dailyreport.amount", null);
                label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet.addCell(label);

                keyName = BTSLUtil.getMessage(locale, "dailyreport.count", null);
                label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet.addCell(label);

                keyName = BTSLUtil.getMessage(locale, "dailyreport.amount", null);
                label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet.addCell(label);
            } else {// heading for multiple product
                for (int k = 0; k < productListSize; k++) {
                    keyName = BTSLUtil.getMessage(locale, "dailyreport.count", null);
                    label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
                    p_worksheet.addCell(label);

                    keyName = BTSLUtil.getMessage(locale, "dailyreport.amount", null);
                    label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
                    p_worksheet.addCell(label);

                    keyName = BTSLUtil.getMessage(locale, "dailyreport.count", null);
                    label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
                    p_worksheet.addCell(label);

                    keyName = BTSLUtil.getMessage(locale, "dailyreport.amount", null);
                    label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
                    p_worksheet.addCell(label);
                }
            }
            if (c2sList != null && !c2sList.isEmpty()) {
                for (int i = 0, j = c2sList.size(); i < j; i++) {
                    dailyReportVO = (DailyReportVO) c2sList.get(i);
                    if (dailyReportVO.getNetworkCode().equalsIgnoreCase(p_NetwokCode)) {
                        col = 0;
                        p_row++;
                        col = 0;
                        newServiceCode = dailyReportVO.getServiceType();
                        if (!newServiceCode.equals(oldServiceCode)) {
                            p_row++;
                            label = new Label(col++, p_row, dailyReportVO.getServiceTypeName(), ExcelStyle.getDataStyle());
                            p_worksheet.addCell(label);
                            oldServiceCode = newServiceCode;
                        }
                        if (col == 0) {
                            col++;
                        }
                        label = new Label(col++, p_row, dailyReportVO.getErrorDesc(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(label);
                        if (productListSize == 1) {
                            number = new Number(col++, p_row, dailyReportVO.getDailyFailCount(), ExcelStyle.getDataStyle());
                            p_worksheet.addCell(number);

                            number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getDailyFailAmountStr()), ExcelStyle.getDataStyle());
                            p_worksheet.addCell(number);

                            number = new Number(col++, p_row, dailyReportVO.getMonthFailCount(), ExcelStyle.getDataStyle());
                            p_worksheet.addCell(number);

                            number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getMonthFailAmountStr()), ExcelStyle.getDataStyle());
                            p_worksheet.addCell(number);
                        } else {// data for multiple product
                            for (int k = 0; k < productListSize; k++) {
                                if ((((DailyReportVO) productList.get(k)).getProductCode()).equals(dailyReportVO.getProductCode())) {
                                    number = new Number(col++, p_row, dailyReportVO.getDailyFailCount(), ExcelStyle.getDataStyle());
                                    p_worksheet.addCell(number);

                                    number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getDailyFailAmountStr()), ExcelStyle.getDataStyle());
                                    p_worksheet.addCell(number);

                                    number = new Number(col++, p_row, dailyReportVO.getMonthFailCount(), ExcelStyle.getDataStyle());
                                    p_worksheet.addCell(number);

                                    number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getMonthFailAmountStr()), ExcelStyle.getDataStyle());
                                    p_worksheet.addCell(number);
                                } else {
                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet.addCell(number);

                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet.addCell(number);

                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet.addCell(number);

                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet.addCell(number);
                                }
                            }
                        }
                    }
                }
            }

            if (PretupsI.YES.equalsIgnoreCase(loadTest))// if load test flag
            // enable
            {
                final ArrayList receiverRequestList = (ArrayList) p_hashMap.get("C2S_RECEIVER_REQUESTS");
                p_row++;
                // write other than c2s recharge service
                p_row = writeExcelReceiverRequest(p_worksheet, receiverRequestList, p_row, p_NetwokCode);

            }

            final ArrayList c2sRechargeList = (ArrayList) p_hashMap.get("C2S_SUMMARY");
            p_row++;

            // Write C2S recarage summary
            p_row = writeExcelSummary(p_worksheet, c2sRechargeList, p_row, p_NetwokCode, networkName);
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            _log.error(methodName, " BTSLBaseException e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "Exception=" + e.getMessage());
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Exiting row:" + row);
        }
        return p_row;
    }

    /**
     * method writeExcelC2S
     * This method write to start data on first sheet for c2s.
     * 
     * @param p_worksheet
     * @param p_hashMap
     * @param p_row
     * @param p_NetwokCode
     * @return
     * @throws BTSLBaseException
     */
    private int writeExcelP2P(WritableSheet p_worksheet, HashMap p_hashMap, int p_row, String p_NetwokCode) throws BTSLBaseException {
        final String methodName = "writeExcelP2P";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " p_hashMap.size():" + p_hashMap.size() + " p_row: " + p_row + " p_NetwokCode=" + p_NetwokCode);
        }
        try {
            final ArrayList c2sList = (ArrayList) p_hashMap.get("P2P_FAIL_RECHARGE");
            final ArrayList productList = (ArrayList) p_hashMap.get("P2P_PRODUCT_LIST");
            final ArrayList networkList = (ArrayList) p_hashMap.get("NETWORK_LIST");
            final String loadTest = (String) p_hashMap.get("LOAD_TEST");

            DailyReportVO dailyReportVO = null;
            final int productListSize = productList.size();
            String keyName = null;
            Label label = null;
            Number number = null;
            String networkName = null;
            String oldServiceCode = null;
            String newServiceCode = null;
            final DailyReportAnalysis dailyReportAnalysis = new DailyReportAnalysis();
            networkName = dailyReportAnalysis.getNetworkName(p_NetwokCode, networkList);

            col = 0;
            p_row++;
            keyName = BTSLUtil.getMessage(locale, "dailyreport.network", null) + networkName;
            label = new Label(col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row + 1);
            p_worksheet.addCell(label);

            keyName = BTSLUtil.getMessage(locale, "dailyreport.product", null);
            label = new Label(col + 1, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(++col, p_row, (col + productListSize * 4) - 1, p_row);
            p_worksheet.addCell(label);
            p_row++;
            if (productListSize == 1) {
                final String[] product = { BTSLUtil.getMessage(locale, ((DailyReportVO) productList.get(0)).getProductCode(), null) };
                keyName = BTSLUtil.getMessage(locale, "dailyreport.fortheday", product);
                label = new Label(col, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet.mergeCells(col, p_row, ++col, p_row);
                p_worksheet.addCell(label);

                keyName = BTSLUtil.getMessage(locale, "dailyreport.forthemonth", product);
                label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet.mergeCells(col, p_row, ++col, p_row);
                p_worksheet.addCell(label);
            } else {// heading for multiple product
                for (int k = 0; k < productListSize; k++) {
                    final String[] product = { BTSLUtil.getMessage(locale, ((DailyReportVO) productList.get(k)).getProductCode(), null) };
                    if (BTSLUtil.isNullArray(product)) {
                        product[0] = ((DailyReportVO) productList.get(k)).getProductCode();
                    }
                    keyName = BTSLUtil.getMessage(locale, "dailyreport.fortheday", product);
                    label = new Label(col, p_row, keyName, ExcelStyle.getHeadingFont());
                    p_worksheet.mergeCells(col, p_row, ++col, p_row);
                    p_worksheet.addCell(label);

                    keyName = BTSLUtil.getMessage(locale, "dailyreport.forthemonth", product);
                    label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
                    p_worksheet.mergeCells(col, p_row, ++col, p_row);
                    p_worksheet.addCell(label);
                    col++;
                }
            }
            p_row++;
            col = 0;
            keyName = BTSLUtil.getMessage(locale, "dailyreport.service", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.addCell(label);

            keyName = BTSLUtil.getMessage(locale, "dailyreport.errorcodes", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.addCell(label);
            if (productListSize == 1) {
                keyName = BTSLUtil.getMessage(locale, "dailyreport.count", null);
                label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet.addCell(label);

                keyName = BTSLUtil.getMessage(locale, "dailyreport.amount", null);
                label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet.addCell(label);

                keyName = BTSLUtil.getMessage(locale, "dailyreport.count", null);
                label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet.addCell(label);

                keyName = BTSLUtil.getMessage(locale, "dailyreport.amount", null);
                label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet.addCell(label);
            } else {// heading for multiple product
                for (int k = 0; k < productListSize; k++) {
                    keyName = BTSLUtil.getMessage(locale, "dailyreport.count", null);
                    label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
                    p_worksheet.addCell(label);

                    keyName = BTSLUtil.getMessage(locale, "dailyreport.amount", null);
                    label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
                    p_worksheet.addCell(label);

                    keyName = BTSLUtil.getMessage(locale, "dailyreport.count", null);
                    label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
                    p_worksheet.addCell(label);

                    keyName = BTSLUtil.getMessage(locale, "dailyreport.amount", null);
                    label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
                    p_worksheet.addCell(label);
                }
            }
            if (c2sList != null && !c2sList.isEmpty()) {
                for (int i = 0, j = c2sList.size(); i < j; i++) {
                    dailyReportVO = (DailyReportVO) c2sList.get(i);
                    if (dailyReportVO.getNetworkCode().equalsIgnoreCase(p_NetwokCode)) {
                        col = 0;
                        p_row++;
                        newServiceCode = dailyReportVO.getServiceType();
                        if (!newServiceCode.equals(oldServiceCode)) {
                            p_row++;
                            label = new Label(col++, p_row, dailyReportVO.getServiceTypeName(), ExcelStyle.getDataStyle());
                            p_worksheet.addCell(label);
                            oldServiceCode = newServiceCode;
                        }
                        if (col == 0) {
                            col++;
                        }
                        label = new Label(col++, p_row, dailyReportVO.getErrorDesc(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(label);
                        if (productListSize == 1) {
                            number = new Number(col++, p_row, dailyReportVO.getDailyFailCount(), ExcelStyle.getDataStyle());
                            p_worksheet.addCell(number);

                            number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getDailyFailAmountStr()), ExcelStyle.getDataStyle());
                            p_worksheet.addCell(number);

                            number = new Number(col++, p_row, dailyReportVO.getMonthFailCount(), ExcelStyle.getDataStyle());
                            p_worksheet.addCell(number);

                            number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getMonthFailAmountStr()), ExcelStyle.getDataStyle());
                            p_worksheet.addCell(number);
                        } else {// data for multiple product
                            for (int k = 0; k < productListSize; k++) {
                                if ((((DailyReportVO) productList.get(k)).getProductCode()).equals(dailyReportVO.getProductCode())) {
                                    number = new Number(col++, p_row, dailyReportVO.getDailyFailCount(), ExcelStyle.getDataStyle());
                                    p_worksheet.addCell(number);

                                    number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getDailyFailAmountStr()), ExcelStyle.getDataStyle());
                                    p_worksheet.addCell(number);

                                    number = new Number(col++, p_row, dailyReportVO.getMonthFailCount(), ExcelStyle.getDataStyle());
                                    p_worksheet.addCell(number);

                                    number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getMonthFailAmountStr()), ExcelStyle.getDataStyle());
                                    p_worksheet.addCell(number);
                                } else {
                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet.addCell(number);

                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet.addCell(number);

                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet.addCell(number);

                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet.addCell(number);
                                }
                            }
                        }
                    }
                }
            }

            if (PretupsI.YES.equalsIgnoreCase(loadTest))// if load test flag
            // enable
            {
                final ArrayList receiverRequestList = (ArrayList) p_hashMap.get("P2P_RECEIVER_REQUESTS");
                p_row++;
                // write other than p2p recharge service
                p_row = writeExcelReceiverRequest(p_worksheet, receiverRequestList, p_row, p_NetwokCode);
            }
            final ArrayList p2pRechargeList = (ArrayList) p_hashMap.get("P2P_SUMMARY");
            p_row++;
            // Write P2P recarage summary
            p_row = writeExcelSummary(p_worksheet, p2pRechargeList, p_row, p_NetwokCode, networkName);
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            _log.error(methodName, " BTSLBaseException e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "Exception=" + e.getMessage());
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Exiting row:" + row);
        }
        return p_row;
    }

    /**
     * method writeExcelC2SReceiverRequest
     * in this method write the other than recharge services with error code.
     * 
     * @param p_worksheet
     * @param p_receiverRequestList
     * @param p_row
     * @param p_NetwokCode
     * @return
     * @throws BTSLBaseException
     */
    private int writeExcelReceiverRequest(WritableSheet p_worksheet, ArrayList p_receiverRequestList, int p_row, String p_NetwokCode) throws BTSLBaseException {
        final String methodName = "writeExcelReceiverRequest";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " p_receiverRequestList.size():" + p_receiverRequestList.size() + " p_row: " + p_row + " p_NetwokCode=" + p_NetwokCode);
        }
        try {
            DailyReportVO dailyReportVO = null;
            String keyName = null;
            Label label = null;
            Number number = null;
            String oldServiceCode = null;
            String newServiceCode = null;
            col = 0;
            p_row++;
            keyName = BTSLUtil.getMessage(locale, "dailyreport.service", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "dailyreport.errorcodes", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.addCell(label);

            final String count[] = { BTSLUtil.getMessage(locale, "dailyreport.count", null) };
            keyName = BTSLUtil.getMessage(locale, "dailyreport.fortheday", count);
            label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "dailyreport.forthemonth", count);
            label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.addCell(label);

            if (p_receiverRequestList != null && !p_receiverRequestList.isEmpty()) {
                for (int i = 0, j = p_receiverRequestList.size(); i < j; i++) {
                    dailyReportVO = (DailyReportVO) p_receiverRequestList.get(i);
                    if (dailyReportVO.getNetworkCode().equals(p_NetwokCode)) {
                        p_row++;
                        col = 0;
                        newServiceCode = dailyReportVO.getServiceType();
                        if (!newServiceCode.equals(oldServiceCode)) {
                            p_row++;
                            label = new Label(col++, p_row, dailyReportVO.getServiceTypeName(), ExcelStyle.getDataStyle());
                            p_worksheet.addCell(label);
                            oldServiceCode = newServiceCode;
                        }
                        if (col == 0) {
                            col++;
                        }
                        label = new Label(col++, p_row, dailyReportVO.getErrorDesc(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(label);
                        number = new Number(col++, p_row, dailyReportVO.getDailyTotalCount(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(number);
                        number = new Number(col++, p_row, dailyReportVO.getMonthTotalCount(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(number);
                    }
                }
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            _log.error(methodName, " BTSLBaseException e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, "writeExcelC2SReceiverRequest", "Exception=" + e.getMessage());
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Exiting row:" + row);
        }
        return p_row;
    }

    /**
     * method writeExcelSummary
     * This method write recharge summary
     * 
     * @param p_worksheet
     * @param p_summaryList
     * @param p_row
     * @param p_NetwokCode
     * @param p_NetwokName
     * @return
     * @throws BTSLBaseException
     */
    private int writeExcelSummary(WritableSheet p_worksheet, ArrayList p_summaryList, int p_row, String p_NetwokCode, String p_NetwokName) throws BTSLBaseException {
        final String methodName = "writeExcelSummary";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " p_summaryList.size(): " + p_summaryList.size() + " p_row: " + p_row + " p_NetwokCode=" + p_NetwokCode + " p_NetwokName=" + p_NetwokName);
        }
        try {
            p_row++;
            String keyName = null;
            Label label = null;
            Number number = null;
            col = 0;
            DailyReportVO dailyReportVO = null;
            String oldNetCode = null;
            String newNetCode = null;
            keyName = BTSLUtil.getMessage(locale, "dailyreport.summary", null);
            label = new Label(col, p_row, keyName, ExcelStyle.getSummaryHeadingFont1());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);

            keyName = BTSLUtil.getMessage(locale, "dailyreport.totalrequest", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getSummaryHeadingFont1());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);

            keyName = BTSLUtil.getMessage(locale, "dailyreport.totalsuccess", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getSummaryHeadingFont1());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);
            p_row++;
            col = 0;

            keyName = BTSLUtil.getMessage(locale, "dailyreport.networks", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getSummaryHeadingFont1());
            p_worksheet.addCell(label);

            keyName = BTSLUtil.getMessage(locale, "dailyreport.service", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getSummaryHeadingFont1());
            p_worksheet.addCell(label);

            keyName = BTSLUtil.getMessage(locale, "dailyreport.forday", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getSummaryHeadingFont1());
            p_worksheet.addCell(label);

            keyName = BTSLUtil.getMessage(locale, "dailyreport.formonth", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getSummaryHeadingFont1());
            p_worksheet.addCell(label);

            keyName = BTSLUtil.getMessage(locale, "dailyreport.forday", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getSummaryHeadingFont1());
            p_worksheet.addCell(label);

            keyName = BTSLUtil.getMessage(locale, "dailyreport.formonth", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getSummaryHeadingFont1());
            p_worksheet.addCell(label);

            for (int i = 0, j = p_summaryList.size(); i < j; i++) {
                dailyReportVO = (DailyReportVO) p_summaryList.get(i);
                newNetCode = dailyReportVO.getNetworkCode();
                if (p_NetwokCode.equals(newNetCode)) {
                    col = 0;
                    p_row++;
                    if (!newNetCode.equals(oldNetCode)) {
                        label = new Label(col++, p_row, p_NetwokName, ExcelStyle.getDataStyle());
                        p_worksheet.addCell(label);
                        oldNetCode = newNetCode;
                    }
                    if (col == 0) {
                        col++;
                    }
                    label = new Label(col++, p_row, dailyReportVO.getServiceTypeName(), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                    number = new Number(col++, p_row, dailyReportVO.getDailyTotalCount(), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(number);
                    number = new Number(col++, p_row, dailyReportVO.getMonthTotalCount(), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(number);
                    number = new Number(col++, p_row, dailyReportVO.getDailySuccessCount(), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(number);
                    number = new Number(col++, p_row, dailyReportVO.getMonthSuccessCount(), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(number);
                }
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            _log.error(methodName, " BTSLBaseException e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "Exception=" + e.getMessage());
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Exiting row:" + row);
        }
        return p_row;
    }

    /**
     * method writeExcelC2SHourly
     * This method write into hourly sheet.
     * 
     * @param p_worksheet3
     * @param p_hashMap
     * @param p_row
     * @param p_networkCode
     * @return
     * @throws BTSLBaseException
     */
    private int writeExcelC2SHourly(WritableSheet p_worksheet3, HashMap p_hashMap, int p_row, String p_networkCode) throws BTSLBaseException {
        final String methodName = "writeExcelC2SHourly";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " p_hashMap.size():" + p_hashMap.size() + " p_row: " + p_row + " p_networkCode=" + p_networkCode);
        }
        try {
            final ArrayList list = (ArrayList) p_hashMap.get("C2S_RECHARGE_HOURLY");
            final String loadtest = (String) p_hashMap.get("LOAD_TEST");
            if (PretupsI.YES.equalsIgnoreCase(loadtest)) {
                final ArrayList loadTestList = (ArrayList) p_hashMap.get("C2S_RECEIVER_REQUEST_HOURLY");
                list.addAll(loadTestList);
                Collections.sort(list);
            }
            String keyName = null;
            Label label = null;
            Number number = null;

            p_row++;
            col = 0;
            keyName = BTSLUtil.getMessage(locale, "dailyreport.success.recharge.report.c2s.hourly", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet3.addCell(label);
            label = new Label(col++, p_row, "", ExcelStyle.getHeadingFont());
            p_worksheet3.addCell(label);
            label = new Label(col++, p_row, "", ExcelStyle.getHeadingFont());
            p_worksheet3.addCell(label);
            label = new Label(col++, p_row, "", ExcelStyle.getHeadingFont());
            p_worksheet3.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "dailyreport.hourrange", null);
            label = new Label(col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet3.mergeCells(col, p_row, (col + (24 * 2) - 1), p_row);
            p_worksheet3.addCell(label);
            p_row++;
            col = 0;
            label = new Label(col++, p_row, "", ExcelStyle.getHeadingFont());
            p_worksheet3.addCell(label);
            label = new Label(col++, p_row, "", ExcelStyle.getHeadingFont());
            p_worksheet3.addCell(label);
            label = new Label(col++, p_row, "", ExcelStyle.getHeadingFont());
            p_worksheet3.addCell(label);
            label = new Label(col++, p_row, "", ExcelStyle.getHeadingFont());
            p_worksheet3.addCell(label);
            final String hourlyHeading = "dailyreport.hourly.breakup";
            for (int i = 1; i <= 24; i++) {
                keyName = BTSLUtil.getMessage(locale, hourlyHeading + i, null);
                label = new Label(col, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet3.mergeCells(col, p_row, ++col, p_row);
                p_worksheet3.addCell(label);
                col++;
            }
            p_row++;
            col = 0;
            keyName = BTSLUtil.getMessage(locale, "dailyreport.networks", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet3.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "dailyreport.service", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet3.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "dailyreport.successcount", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet3.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "dailyreport.totalcount", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet3.addCell(label);
            for (int i = 1; i <= 24; i++) {
                keyName = BTSLUtil.getMessage(locale, "dailyreport.success", null);
                label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet3.addCell(label);
                keyName = BTSLUtil.getMessage(locale, "dailyreport.total", null);
                label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet3.addCell(label);
            }
            DailyReportVO dailyReportVO = null;
            String oldNetCode = null;
            String newNetCode = null;
            int k = 0;
            final long sumColumn[] = new long[50];
            if (list != null && !list.isEmpty()) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    dailyReportVO = (DailyReportVO) list.get(i);
                    newNetCode = dailyReportVO.getNetworkCode();
                    col = 0;
                    p_row++;
                    k = 0;
                    if (!newNetCode.equals(oldNetCode)) {
                        p_row++;
                        label = new Label(col++, p_row, dailyReportVO.getNetworkName(), ExcelStyle.getDataStyle());
                        p_worksheet3.addCell(label);
                        oldNetCode = newNetCode;
                    }
                    if (col == 0) {
                        col++;
                    }
                    label = new Label(col++, p_row, dailyReportVO.getServiceTypeName(), ExcelStyle.getDataStyle());
                    p_worksheet3.addCell(label);
                    number = new Number(col++, p_row, (dailyReportVO.getDailySuccessCount()), ExcelStyle.getDataStyle());
                    p_worksheet3.addCell(number);
                    number = new Number(col++, p_row, (dailyReportVO.getDailyTotalCount()), ExcelStyle.getDataStyle());
                    p_worksheet3.addCell(number);
                    sumColumn[k] = sumColumn[k++] + dailyReportVO.getDailySuccessCount();
                    sumColumn[k] = sumColumn[k++] + dailyReportVO.getDailyTotalCount();
                    for (int m = 0; m < 24; m++) {
                        number = new Number(col++, p_row, (dailyReportVO.getDailySuccessCount(m)), ExcelStyle.getDataStyle());
                        p_worksheet3.addCell(number);
                        number = new Number(col++, p_row, (dailyReportVO.getDailyTotalCount(m)), ExcelStyle.getDataStyle());
                        p_worksheet3.addCell(number);
                        sumColumn[k] = sumColumn[k++] + dailyReportVO.getDailySuccessCount(m);
                        sumColumn[k] = sumColumn[k++] + dailyReportVO.getDailyTotalCount(m);
                    }
                }
                p_row++;
                p_row++;
                col = 0;
                keyName = BTSLUtil.getMessage(locale, "dailyreport.total", null);
                label = new Label(col, p_row, keyName, ExcelStyle.getSecondTopHeadingFont());
                p_worksheet3.mergeCells(col, p_row, ++col, p_row);
                p_worksheet3.addCell(label);
                for (int i = 0, j = sumColumn.length; i < j; i++) {
                    number = new Number(++col, p_row, sumColumn[i], ExcelStyle.getSecondTopHeadingFont());
                    p_worksheet3.addCell(number);
                }
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            _log.error(methodName, " BTSLBaseException e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "Exception=" + e.getMessage());
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Exiting row:" + row);
        }
        return p_row;
    }

    /**
     * method writeExcelP2PHourly
     * This method write into hourly sheet.
     * 
     * @param p_worksheet3
     * @param p_hashMap
     * @param p_row
     * @param p_networkCode
     * @return
     * @throws BTSLBaseException
     */
    private int writeExcelP2PHourly(WritableSheet p_worksheet3, HashMap p_hashMap, int p_row, String p_networkCode) throws BTSLBaseException {
        final String methodName = "writeExcelP2PHourly";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " p_hashMap.size():" + p_hashMap.size() + " p_row: " + p_row + " p_networkCode=" + p_networkCode);
        }
        try {
            final ArrayList list = (ArrayList) p_hashMap.get("P2P_RECHARGE_HOURLY");
            final String loadtest = (String) p_hashMap.get("LOAD_TEST");
            if (PretupsI.YES.equalsIgnoreCase(loadtest)) {
                final ArrayList loadTestList = (ArrayList) p_hashMap.get("P2P_RECEIVER_REQUEST_HOURLY");
                list.addAll(loadTestList);
                Collections.sort(list);
            }
            String keyName = null;
            Label label = null;
            Number number = null;

            p_row++;
            col = 0;
            keyName = BTSLUtil.getMessage(locale, "dailyreport.success.recharge.report.p2p.hourly", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet3.addCell(label);
            label = new Label(col++, p_row, "", ExcelStyle.getHeadingFont());
            p_worksheet3.addCell(label);
            label = new Label(col++, p_row, "", ExcelStyle.getHeadingFont());
            p_worksheet3.addCell(label);
            label = new Label(col++, p_row, "", ExcelStyle.getHeadingFont());
            p_worksheet3.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "dailyreport.hourrange", null);
            label = new Label(col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet3.mergeCells(col, p_row, (col + (24 * 2) - 1), p_row);
            p_worksheet3.addCell(label);
            p_row++;
            col = 0;
            label = new Label(col++, p_row, "", ExcelStyle.getHeadingFont());
            p_worksheet3.addCell(label);
            label = new Label(col++, p_row, "", ExcelStyle.getHeadingFont());
            p_worksheet3.addCell(label);
            label = new Label(col++, p_row, "", ExcelStyle.getHeadingFont());
            p_worksheet3.addCell(label);
            label = new Label(col++, p_row, "", ExcelStyle.getHeadingFont());
            p_worksheet3.addCell(label);
            final String hourlyHeading = "dailyreport.hourly.breakup";
            for (int i = 1; i <= 24; i++) {
                keyName = BTSLUtil.getMessage(locale, hourlyHeading + i, null);
                label = new Label(col, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet3.mergeCells(col, p_row, ++col, p_row);
                p_worksheet3.addCell(label);
                col++;
            }
            p_row++;
            col = 0;
            keyName = BTSLUtil.getMessage(locale, "dailyreport.networks", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet3.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "dailyreport.service", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet3.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "dailyreport.successcount", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet3.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "dailyreport.totalcount", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet3.addCell(label);
            for (int i = 1; i <= 24; i++) {
                keyName = BTSLUtil.getMessage(locale, "dailyreport.success", null);
                label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet3.addCell(label);
                keyName = BTSLUtil.getMessage(locale, "dailyreport.total", null);
                label = new Label(col++, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet3.addCell(label);
            }
            DailyReportVO dailyReportVO = null;
            String oldNetCode = null;
            String newNetCode = null;
            int k = 0;
            final long sumColumn[] = new long[50];
            if (list != null && !list.isEmpty()) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    dailyReportVO = (DailyReportVO) list.get(i);
                    newNetCode = dailyReportVO.getNetworkCode();
                    col = 0;
                    p_row++;
                    k = 0;
                    if (!newNetCode.equals(oldNetCode)) {
                        p_row++;
                        label = new Label(col++, p_row, dailyReportVO.getNetworkName(), ExcelStyle.getDataStyle());
                        p_worksheet3.addCell(label);
                        oldNetCode = newNetCode;
                    }
                    if (col == 0) {
                        col++;
                    }
                    label = new Label(col++, p_row, dailyReportVO.getServiceTypeName(), ExcelStyle.getDataStyle());
                    p_worksheet3.addCell(label);
                    number = new Number(col++, p_row, (dailyReportVO.getDailySuccessCount()), ExcelStyle.getDataStyle());
                    p_worksheet3.addCell(number);
                    number = new Number(col++, p_row, (dailyReportVO.getDailyTotalCount()), ExcelStyle.getDataStyle());
                    p_worksheet3.addCell(number);
                    sumColumn[k] = sumColumn[k++] + dailyReportVO.getDailySuccessCount();
                    sumColumn[k] = sumColumn[k++] + dailyReportVO.getDailyTotalCount();
                    for (int m = 0; m < 24; m++) {
                        number = new Number(col++, p_row, (dailyReportVO.getDailySuccessCount(m)), ExcelStyle.getDataStyle());
                        p_worksheet3.addCell(number);
                        number = new Number(col++, p_row, (dailyReportVO.getDailyTotalCount(m)), ExcelStyle.getDataStyle());
                        p_worksheet3.addCell(number);
                        sumColumn[k] = sumColumn[k++] + dailyReportVO.getDailySuccessCount(m);
                        sumColumn[k] = sumColumn[k++] + dailyReportVO.getDailyTotalCount(m);
                    }
                }
                p_row++;
                p_row++;
                col = 0;
                keyName = BTSLUtil.getMessage(locale, "dailyreport.total", null);
                label = new Label(col, p_row, keyName, ExcelStyle.getSecondTopHeadingFont());
                p_worksheet3.mergeCells(col, p_row, ++col, p_row);
                p_worksheet3.addCell(label);
                for (int i = 0, j = sumColumn.length; i < j; i++) {
                    number = new Number(++col, p_row, sumColumn[i], ExcelStyle.getSecondTopHeadingFont());
                    p_worksheet3.addCell(number);
                }
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            _log.error(methodName, " BTSLBaseException e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "Exception=" + e.getMessage());
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Exiting row:" + row);
        }
        return p_row;
    }

    /**
     * @param p_worksheet3
     * @param p_list
     * @param p_productList
     * @param p_row
     * @param p_heading
     * @return
     * @throws BTSLBaseException
     */
    private int writeExcelSummaryProductWise(WritableSheet p_worksheet3, ArrayList p_list, ArrayList p_productList, int p_row, String p_heading) throws BTSLBaseException {
        final String methodName = "writeExcelSummaryProductWise";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " p_list.size():" + p_list.size() + " p_productList.size()=" + p_productList.size() + " p_row: " + p_row + " p_NetwokCode=" + p_heading);
        }
        try {
            DailyReportVO dailyReportVO = null;
            String keyName = null;
            Label label = null;
            Number number = null;
            String newNetCode = null;
            String oldNetCode = null;
            String newServCode = null;
            String oldServCode = null;
            final int productListSize = p_productList.size();

            label = new Label(col, p_row, p_heading, ExcelStyle.getSummaryHeadingFont());
            p_worksheet3.mergeCells(col, p_row, ++col, p_row + 1);
            p_worksheet3.addCell(label);

            keyName = BTSLUtil.getMessage(locale, "dailyreport.product", null);
            label = new Label(col + 1, p_row, keyName, ExcelStyle.getSummaryHeadingFont());
            p_worksheet3.mergeCells(++col, p_row, (col + productListSize * 4), p_row);
            p_worksheet3.addCell(label);
            p_row++;
            col = 0;
            label = new Label(col++, p_row, "", ExcelStyle.getSummaryHeadingFont());
            p_worksheet3.addCell(label);

            label = new Label(col++, p_row, "", ExcelStyle.getSummaryHeadingFont());
            p_worksheet3.addCell(label);

            label = new Label(col++, p_row, "", ExcelStyle.getSummaryHeadingFont());
            p_worksheet3.addCell(label);
            if (productListSize == 1) {
                final String[] product = { BTSLUtil.getMessage(locale, ((DailyReportVO) p_productList.get(0)).getProductCode(), null) };
                if (BTSLUtil.isNullArray(product)) {
                    product[0] = dailyReportVO.getProductCode();
                }
                keyName = BTSLUtil.getMessage(locale, "dailyreport.fortheday", product);
                label = new Label(col, p_row, keyName, ExcelStyle.getSummaryHeadingFont());
                p_worksheet3.mergeCells(col, p_row, ++col, p_row);
                p_worksheet3.addCell(label);

                keyName = BTSLUtil.getMessage(locale, "dailyreport.forthemonth", product);
                label = new Label(++col, p_row, keyName, ExcelStyle.getSummaryHeadingFont());
                p_worksheet3.mergeCells(col, p_row, ++col, p_row);
                p_worksheet3.addCell(label);
            } else {
                for (int k = 0; k < productListSize; k++) {
                    final String[] product = { BTSLUtil.getMessage(locale, ((DailyReportVO) p_productList.get(k)).getProductCode(), null) };
                    if (BTSLUtil.isNullArray(product)) {
                        product[0] = ((DailyReportVO) p_productList.get(k)).getProductCode();
                    }
                    keyName = BTSLUtil.getMessage(locale, "dailyreport.fortheday", product);
                    label = new Label(col, p_row, keyName, ExcelStyle.getSummaryHeadingFont());
                    p_worksheet3.mergeCells(col, p_row, ++col, p_row);
                    p_worksheet3.addCell(label);

                    keyName = BTSLUtil.getMessage(locale, "dailyreport.forthemonth", product);
                    label = new Label(++col, p_row, keyName, ExcelStyle.getSummaryHeadingFont());
                    p_worksheet3.mergeCells(col, p_row, ++col, p_row);
                    p_worksheet3.addCell(label);
                    col++;
                }
            }

            p_row++;
            col = 0;
            keyName = BTSLUtil.getMessage(locale, "dailyreport.networks", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getSummaryHeadingFont());
            p_worksheet3.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "dailyreport.service", null);
            label = new Label(col++, p_row, keyName, ExcelStyle.getSummaryHeadingFont());
            p_worksheet3.addCell(label);
            label = new Label(col++, p_row, "", ExcelStyle.getSummaryHeadingFont());
            p_worksheet3.addCell(label);
            if (productListSize == 1) {
                keyName = BTSLUtil.getMessage(locale, "dailyreport.count", null);
                label = new Label(col++, p_row, keyName, ExcelStyle.getSummaryHeadingFont());
                p_worksheet3.addCell(label);

                keyName = BTSLUtil.getMessage(locale, "dailyreport.amount", null);
                label = new Label(col++, p_row, keyName, ExcelStyle.getSummaryHeadingFont());
                p_worksheet3.addCell(label);

                keyName = BTSLUtil.getMessage(locale, "dailyreport.count", null);
                label = new Label(col++, p_row, keyName, ExcelStyle.getSummaryHeadingFont());
                p_worksheet3.addCell(label);

                keyName = BTSLUtil.getMessage(locale, "dailyreport.amount", null);
                label = new Label(col++, p_row, keyName, ExcelStyle.getSummaryHeadingFont());
                p_worksheet3.addCell(label);
            } else {
                for (int k = 0; k < productListSize; k++) {
                    keyName = BTSLUtil.getMessage(locale, "dailyreport.count", null);
                    label = new Label(col++, p_row, keyName, ExcelStyle.getSummaryHeadingFont());
                    p_worksheet3.addCell(label);

                    keyName = BTSLUtil.getMessage(locale, "dailyreport.amount", null);
                    label = new Label(col++, p_row, keyName, ExcelStyle.getSummaryHeadingFont());
                    p_worksheet3.addCell(label);

                    keyName = BTSLUtil.getMessage(locale, "dailyreport.count", null);
                    label = new Label(col++, p_row, keyName, ExcelStyle.getSummaryHeadingFont());
                    p_worksheet3.addCell(label);

                    keyName = BTSLUtil.getMessage(locale, "dailyreport.amount", null);
                    label = new Label(col++, p_row, keyName, ExcelStyle.getSummaryHeadingFont());
                    p_worksheet3.addCell(label);
                }
            }
            p_row++;
            int temp_row = 0;
            if (p_list != null && !p_list.isEmpty()) {
                boolean flag = true;
                boolean flag1 = true;
                for (int i = 0, j = p_list.size(); i < j; i++) {
                    flag1 = true;
                    dailyReportVO = (DailyReportVO) p_list.get(i);
                    newNetCode = dailyReportVO.getNetworkCode();
                    newServCode = dailyReportVO.getServiceType();
                    col = 0;
                    if (!newNetCode.equals(oldNetCode)) {
                        if (i != 0) {
                            p_row += 6;
                            flag1 = false;
                        }
                        label = new Label(col, p_row, dailyReportVO.getNetworkName(), ExcelStyle.getDataStyle());
                        p_worksheet3.addCell(label);
                        oldNetCode = newNetCode;
                        flag = true;
                        oldServCode = null;
                    }
                    if (oldNetCode.equals(newNetCode) && !newServCode.equals(oldServCode)) {
                        if (i != 0 && flag1) {
                            p_row += 6;
                        }
                        flag = true;
                        label = new Label(++col, p_row, dailyReportVO.getServiceTypeName(), ExcelStyle.getDataStyle());
                        p_worksheet3.addCell(label);
                        oldServCode = newServCode;
                    }
                    if (flag) {
                        flag = false;
                        col++;
                        temp_row = p_row;
                        keyName = BTSLUtil.getMessage(locale, "dailyreport.totalrequest", null);
                        label = new Label(col, p_row, keyName, ExcelStyle.getDataStyle());
                        p_worksheet3.addCell(label);
                        p_row++;
                        keyName = BTSLUtil.getMessage(locale, "dailyreport.totalsuccess", null);
                        label = new Label(col, p_row, keyName, ExcelStyle.getDataStyle());
                        p_worksheet3.addCell(label);
                        p_row++;
                        keyName = BTSLUtil.getMessage(locale, "dailyreport.totalfailed", null);
                        label = new Label(col, p_row, keyName, ExcelStyle.getDataStyle());
                        p_worksheet3.addCell(label);
                        p_row++;
                        keyName = BTSLUtil.getMessage(locale, "dailyreport.totalamigous", null);
                        label = new Label(col, p_row, keyName, ExcelStyle.getDataStyle());
                        p_worksheet3.addCell(label);
                        p_row++;
                        keyName = BTSLUtil.getMessage(locale, "dailyreport.totalunderprocess", null);
                        label = new Label(col, p_row, keyName, ExcelStyle.getDataStyle());
                        p_worksheet3.addCell(label);
                        p_row = temp_row;
                    }
                    if (newNetCode.equals(oldNetCode)) {
                        if (col == 0) {
                            col += 3;
                        }
                        if (productListSize == 1) {
                            final int temp = col = 3;
                            temp_row = p_row;
                            number = new Number(col++, p_row, dailyReportVO.getDailyTotalCount(), ExcelStyle.getDataStyle());
                            p_worksheet3.addCell(number);
                            number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getDailyTotalAmountStr()), ExcelStyle.getDataStyle());
                            p_worksheet3.addCell(number);
                            number = new Number(col++, p_row, dailyReportVO.getMonthTotalCount(), ExcelStyle.getDataStyle());
                            p_worksheet3.addCell(number);
                            number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getMonthTotalAmountStr()), ExcelStyle.getDataStyle());
                            p_worksheet3.addCell(number);

                            p_row++;
                            col = temp;
                            number = new Number(col++, p_row, dailyReportVO.getDailySuccessCount(), ExcelStyle.getDataStyle());
                            p_worksheet3.addCell(number);
                            number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getDailySuccessAmountStr()), ExcelStyle.getDataStyle());
                            p_worksheet3.addCell(number);
                            number = new Number(col++, p_row, dailyReportVO.getMonthSuccessCount(), ExcelStyle.getDataStyle());
                            p_worksheet3.addCell(number);
                            number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getMonthSuccessAmountStr()), ExcelStyle.getDataStyle());
                            p_worksheet3.addCell(number);

                            p_row++;
                            col = temp;
                            number = new Number(col++, p_row, dailyReportVO.getDailyFailCount(), ExcelStyle.getDataStyle());
                            p_worksheet3.addCell(number);
                            number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getDailyFailAmountStr()), ExcelStyle.getDataStyle());
                            p_worksheet3.addCell(number);
                            number = new Number(col++, p_row, dailyReportVO.getMonthFailCount(), ExcelStyle.getDataStyle());
                            p_worksheet3.addCell(number);
                            number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getMonthFailAmountStr()), ExcelStyle.getDataStyle());
                            p_worksheet3.addCell(number);

                            p_row++;
                            col = temp;
                            number = new Number(col++, p_row, dailyReportVO.getDailyAmbigousCount(), ExcelStyle.getDataStyle());
                            p_worksheet3.addCell(number);
                            number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getDailyAmbigousAmountStr()), ExcelStyle.getDataStyle());
                            p_worksheet3.addCell(number);
                            number = new Number(col++, p_row, dailyReportVO.getMonthAmbigousCount(), ExcelStyle.getDataStyle());
                            p_worksheet3.addCell(number);
                            number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getMonthAmbigousAmountStr()), ExcelStyle.getDataStyle());
                            p_worksheet3.addCell(number);

                            p_row++;
                            col = temp;
                            number = new Number(col++, p_row, dailyReportVO.getDailyUnderProcessCount(), ExcelStyle.getDataStyle());
                            p_worksheet3.addCell(number);
                            number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getDailyUnderProcessAmountStr()), ExcelStyle.getDataStyle());
                            p_worksheet3.addCell(number);
                            number = new Number(col++, p_row, dailyReportVO.getMonthUnderProcessCount(), ExcelStyle.getDataStyle());
                            p_worksheet3.addCell(number);
                            number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getMonthUnderProcessAmountStr()), ExcelStyle.getDataStyle());
                            p_worksheet3.addCell(number);
                            p_row = temp_row;
                        } else {
                            int temp = col = 3;
                            for (int k = 0; k < productListSize; k++) {
                                if ((((DailyReportVO) p_productList.get(k)).getProductCode()).equals(dailyReportVO.getProductCode())) {
                                    col = temp;
                                    temp_row = p_row;
                                    number = new Number(col++, p_row, dailyReportVO.getDailyTotalCount(), ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getDailyTotalAmountStr()), ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, dailyReportVO.getMonthTotalCount(), ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getMonthTotalAmountStr()), ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);

                                    p_row++;
                                    col = temp;
                                    number = new Number(col++, p_row, dailyReportVO.getDailySuccessCount(), ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getDailySuccessAmountStr()), ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, dailyReportVO.getMonthSuccessCount(), ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getMonthSuccessAmountStr()), ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);

                                    p_row++;
                                    col = temp;
                                    number = new Number(col++, p_row, dailyReportVO.getDailyFailCount(), ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getDailyFailAmountStr()), ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, dailyReportVO.getMonthFailCount(), ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getMonthFailAmountStr()), ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);

                                    p_row++;
                                    col = temp;
                                    number = new Number(col++, p_row, dailyReportVO.getDailyAmbigousCount(), ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getDailyAmbigousAmountStr()), ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, dailyReportVO.getMonthAmbigousCount(), ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getMonthAmbigousAmountStr()), ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);

                                    p_row++;
                                    col = temp;
                                    number = new Number(col++, p_row, dailyReportVO.getDailyUnderProcessCount(), ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getDailyUnderProcessAmountStr()), ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, dailyReportVO.getMonthUnderProcessCount(), ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, Double.parseDouble(dailyReportVO.getMonthUnderProcessAmountStr()), ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    p_row = temp_row;
                                } else {
                                    col = temp;
                                    temp_row = p_row;
                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);

                                    p_row++;
                                    col = temp;
                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);

                                    p_row++;
                                    col = temp;
                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);

                                    p_row++;
                                    col = temp;
                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);

                                    p_row++;
                                    col = temp;
                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    number = new Number(col++, p_row, 0, ExcelStyle.getDataStyle());
                                    p_worksheet3.addCell(number);
                                    p_row = temp_row;
                                }
                                temp = col;
                            }
                        }
                    }
                }
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            _log.error(methodName, " BTSLBaseException e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "Exception=" + e.getMessage());
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Exiting row:" + row);
        }
        return p_row;
    }

    /**
     * method writeMgtExcelHeader
     * This method write sheet header.
     * 
     * @param p_worksheet1
     * @param p_reportDate
     * @param p_Header
     * @return
     * @throws BTSLBaseException
     */
    private int writeMgtExcelHeader(WritableSheet p_worksheet, Date p_reportDate, String p_Header) throws BTSLBaseException {
        final String methodName = "writeMgtExcelHeader";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " p_reportDate:" + p_reportDate + " p_Header=" + p_Header);
        }
        int rowCount = 0;
        col = 0;
        try {
            p_worksheet.setColumnView(0, 25);
            p_worksheet.setColumnView(1, 25);
            p_worksheet.setColumnView(2, 25);
            p_worksheet.setColumnView(3, 25);

            for (int i = 4; i < 25; i++) {
                p_worksheet.setColumnView(i, 17);
            }

            final Date currentDate = new Date();
            String keyName = BTSLUtil.getMessage(locale, "mgtreport.header", null);
            Label label = new Label(col, rowCount, keyName, ExcelStyle.getTopHeadingFont());
            p_worksheet.mergeCells(col, rowCount, col + 5, rowCount);
            p_worksheet.addCell(label);

            col = 0;
            rowCount++;
            label = new Label(col, rowCount, p_Header, ExcelStyle.getSecondTopHeadingFont2());
            p_worksheet.mergeCells(col, rowCount, col + 5, rowCount);
            p_worksheet.addCell(label);

            col = 0;
            rowCount++;
            keyName = BTSLUtil.getMessage(locale, "mgtreport.date", null) + BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(p_reportDate));
            label = new Label(col, rowCount, keyName, ExcelStyle.getSecondTopHeadingFont());
            p_worksheet.mergeCells(col, rowCount, ++col, rowCount);
            p_worksheet.addCell(label);

            label = new Label(++col, rowCount, " ", ExcelStyle.getSecondTopHeadingFont());
            p_worksheet.mergeCells(col, rowCount, ++col, rowCount);
            p_worksheet.addCell(label);

            keyName = BTSLUtil.getMessage(locale, "mgtreport.generatedon", null) + BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(currentDate));
            label = new Label(++col, rowCount, keyName, ExcelStyle.getSecondTopHeadingFont());
            p_worksheet.mergeCells(col, rowCount, ++col, rowCount);
            p_worksheet.addCell(label);
            rowCount++;
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            _log.error(methodName, " BTSLBaseException e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "Exception=" + e.getMessage());
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Exiting row:" + row);
        }
        return rowCount;
    }

    /**
     * method writeExcelC2S
     * This method write the c2s transaction data on fourth sheet.
     * 
     * @param p_worksheet
     * @param p_hashMap
     * @param p_row
     * @return
     * @throws BTSLBaseException
     */
    private int writeMgtExcelC2S(WritableSheet p_worksheet, HashMap p_hashMap, int p_row) throws BTSLBaseException {
        final String methodName = "writeMgtExcelC2S";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " p_hashMap.size():" + p_hashMap.size() + " p_row: " + p_row);
        }
        try {
            final ArrayList c2sList = (ArrayList) p_hashMap.get("C2S_RECHARGE");

            DailyReportVO dailyReportVO = null;
            dailyReportVO = null;
            String keyName = null;
            Label label = null;
            String oldNetworkCode = null;
            String oldServiceCode = null;
            String oldCategoryCode = null;
            String oldInterfaceCode = null;

            // header headings
            col = 0;
            keyName = BTSLUtil.getMessage(locale, "mgtreport.module", null) + PretupsI.C2S_MODULE;
            label = new Label(col, p_row, keyName, ExcelStyle.getSecondTopHeadingFont());
            p_worksheet.mergeCells(col, p_row, col + 3, p_row);
            p_worksheet.addCell(label);
            col = col + 3;

            keyName = BTSLUtil.getMessage(locale, "mgtreport.total", null) + BTSLUtil.getMessage(locale, "mgtreport.daily", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "mgtreport.total", null) + BTSLUtil.getMessage(locale, "mgtreport.monthly", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "mgtreport.success", null) + BTSLUtil.getMessage(locale, "mgtreport.daily", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "mgtreport.success", null) + BTSLUtil.getMessage(locale, "mgtreport.monthly", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "mgtreport.fail", null) + BTSLUtil.getMessage(locale, "mgtreport.daily", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "mgtreport.fail", null) + BTSLUtil.getMessage(locale, "mgtreport.monthly", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "mgtreport.ambiguous", null) + BTSLUtil.getMessage(locale, "mgtreport.daily", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "mgtreport.ambiguous", null) + BTSLUtil.getMessage(locale, "mgtreport.monthly", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);

            col = 0;
            p_row++;
            keyName = BTSLUtil.getMessage(locale, "mgtreport.network", null);
            label = new Label(col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "mgtreport.service", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "mgtreport.senderCategory", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "mgtreport.receiverInterface", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.addCell(label);
            for (int i = 0; i < 8; i++) {
                keyName = BTSLUtil.getMessage(locale, "mgtreport.count", null);
                label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet.addCell(label);
                keyName = BTSLUtil.getMessage(locale, "mgtreport.amount", null);
                label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet.addCell(label);
            }

            col = 0;
            p_row++;
            if (c2sList != null && !c2sList.isEmpty()) {
                int i = 0;
                final int j = c2sList.size();
                dailyReportVO = (DailyReportVO) c2sList.get(i);
                label = new Label(col, p_row, dailyReportVO.getNetworkName(), ExcelStyle.getDataStyle());
                p_worksheet.addCell(label);
                label = new Label(++col, p_row, dailyReportVO.getServiceTypeName(), ExcelStyle.getDataStyle());
                p_worksheet.addCell(label);
                label = new Label(++col, p_row, dailyReportVO.getCategoryName(), ExcelStyle.getDataStyle());
                p_worksheet.addCell(label);
                label = new Label(++col, p_row, dailyReportVO.getReceiverInterfaceName(), ExcelStyle.getDataStyle());
                p_worksheet.addCell(label);
                writeMgtExcelDataRow(p_worksheet, p_row, col, dailyReportVO);

                oldNetworkCode = dailyReportVO.getNetworkCode();
                oldServiceCode = dailyReportVO.getServiceType();
                oldCategoryCode = dailyReportVO.getCategoryCode();
                oldInterfaceCode = dailyReportVO.getReceiverInterfaceCode();
                i = i + 1;
                while (i < j) {
                    dailyReportVO = (DailyReportVO) c2sList.get(i);
                    col = 0;
                    p_row++;

                    if (!BTSLUtil.isNullString(oldNetworkCode) && !BTSLUtil.isNullString(dailyReportVO.getNetworkCode()) && !oldNetworkCode.equalsIgnoreCase(dailyReportVO
                        .getNetworkCode())) {
                        label = new Label(col, p_row, dailyReportVO.getNetworkName(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(label);
                    }
                    if (!BTSLUtil.isNullString(oldServiceCode) && !BTSLUtil.isNullString(dailyReportVO.getServiceType()) && !oldServiceCode.equalsIgnoreCase(dailyReportVO
                        .getServiceType())) {
                        label = new Label(++col, p_row, dailyReportVO.getServiceTypeName(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(label);
                    } else {
                        ++col;
                    }
                    if (!BTSLUtil.isNullString(oldCategoryCode) && !BTSLUtil.isNullString(dailyReportVO.getCategoryCode()) && !oldCategoryCode.equalsIgnoreCase(dailyReportVO
                        .getCategoryCode())) {
                        label = new Label(++col, p_row, dailyReportVO.getCategoryName(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(label);
                    } else {
                        ++col;
                    }
                    if (!BTSLUtil.isNullString(oldInterfaceCode) && !BTSLUtil.isNullString(dailyReportVO.getReceiverInterfaceCode()) && !oldInterfaceCode
                        .equalsIgnoreCase(dailyReportVO.getReceiverInterfaceCode())) {
                        label = new Label(++col, p_row, dailyReportVO.getReceiverInterfaceName(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(label);
                    } else {
                        ++col;
                    }
                    writeMgtExcelDataRow(p_worksheet, p_row, col, dailyReportVO);
                    oldNetworkCode = dailyReportVO.getNetworkCode();
                    oldServiceCode = dailyReportVO.getServiceType();
                    oldCategoryCode = dailyReportVO.getCategoryCode();
                    oldInterfaceCode = dailyReportVO.getReceiverInterfaceCode();
                    i++;
                }
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            _log.error(methodName, " BTSLBaseException e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "Exception=" + e.getMessage());
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Exiting row:" + row);
        }
        return p_row;
    }

    /**
     * method writeMgtExcelDataRow
     * This method writes counts and amounts data row
     * 
     * @param p_worksheet
     * @param p_row
     * @param p_col
     * @param p_dailyReportVO
     */
    private void writeMgtExcelDataRow(WritableSheet p_worksheet, int p_row, int p_col, DailyReportVO p_dailyReportVO) throws BTSLBaseException {
        final String methodName = "writeMgtExcelDataRow";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " p_row:" + p_row + " p_dailyReportVO=" + p_dailyReportVO + " p_col:" + p_col);
        }

        Label label = null;
        try {
            // total counts and amounts
            label = new Label(++p_col, p_row, String.valueOf(p_dailyReportVO.getDailyTotalCount()), ExcelStyle.getDataStyle());
            p_worksheet.addCell(label);
            label = new Label(++p_col, p_row, p_dailyReportVO.getDailyTotalAmountStr(), ExcelStyle.getDataStyle());
            p_worksheet.addCell(label);
            label = new Label(++p_col, p_row, String.valueOf(p_dailyReportVO.getMonthTotalCount()), ExcelStyle.getDataStyle());
            p_worksheet.addCell(label);
            label = new Label(++p_col, p_row, p_dailyReportVO.getMonthTotalAmountStr(), ExcelStyle.getDataStyle());
            p_worksheet.addCell(label);

            // Success counts and amounts
            label = new Label(++p_col, p_row, String.valueOf(p_dailyReportVO.getDailySuccessCount()), ExcelStyle.getDataStyle());
            p_worksheet.addCell(label);
            label = new Label(++p_col, p_row, p_dailyReportVO.getDailySuccessAmountStr(), ExcelStyle.getDataStyle());
            p_worksheet.addCell(label);
            label = new Label(++p_col, p_row, String.valueOf(p_dailyReportVO.getMonthSuccessCount()), ExcelStyle.getDataStyle());
            p_worksheet.addCell(label);
            label = new Label(++p_col, p_row, p_dailyReportVO.getMonthSuccessAmountStr(), ExcelStyle.getDataStyle());
            p_worksheet.addCell(label);

            // Fail counts and amounts
            label = new Label(++p_col, p_row, String.valueOf(p_dailyReportVO.getDailyFailCount()), ExcelStyle.getDataStyle());
            p_worksheet.addCell(label);
            label = new Label(++p_col, p_row, p_dailyReportVO.getDailyFailAmountStr(), ExcelStyle.getDataStyle());
            p_worksheet.addCell(label);
            label = new Label(++p_col, p_row, String.valueOf(p_dailyReportVO.getMonthFailCount()), ExcelStyle.getDataStyle());
            p_worksheet.addCell(label);
            label = new Label(++p_col, p_row, p_dailyReportVO.getMonthFailAmountStr(), ExcelStyle.getDataStyle());
            p_worksheet.addCell(label);

            // Ambiguous counts and amounts
            label = new Label(++p_col, p_row, String.valueOf(p_dailyReportVO.getDailyAmbigousCount()), ExcelStyle.getDataStyle());
            p_worksheet.addCell(label);
            label = new Label(++p_col, p_row, p_dailyReportVO.getDailyAmbigousAmountStr(), ExcelStyle.getDataStyle());
            p_worksheet.addCell(label);
            label = new Label(++p_col, p_row, String.valueOf(p_dailyReportVO.getMonthAmbigousCount()), ExcelStyle.getDataStyle());
            p_worksheet.addCell(label);
            label = new Label(++p_col, p_row, p_dailyReportVO.getMonthAmbigousAmountStr(), ExcelStyle.getDataStyle());
            p_worksheet.addCell(label);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "Exception=" + e.getMessage());
        }
    }

    /**
     * method writeExcelP2P
     * This method write the c2s transaction data on fourth sheet.
     * 
     * @param p_worksheet
     * @param p_hashMap
     * @param p_row
     * @return
     * @throws BTSLBaseException
     */
    private int writeMgtExcelP2P(WritableSheet p_worksheet, HashMap p_hashMap, int p_row) throws BTSLBaseException {
        final String methodName = "writeMgtExcelP2P";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " p_hashMap.size():" + p_hashMap.size() + " p_row: " + p_row);
        }
        try {
            final ArrayList p2pList = (ArrayList) p_hashMap.get("P2P_RECHARGE");

            DailyReportVO dailyReportVO = null;
            dailyReportVO = null;
            String keyName = null;
            Label label = null;
            String oldNetworkCode = null;
            String oldServiceCode = null;
            String oldSenderIntCode = null;
            String oldReceiverIntCode = null;

            // header headings
            col = 0;
            keyName = BTSLUtil.getMessage(locale, "mgtreport.module", null) + PretupsI.P2P_MODULE;
            label = new Label(col, p_row, keyName, ExcelStyle.getSecondTopHeadingFont());
            p_worksheet.mergeCells(col, p_row, col + 3, p_row);
            p_worksheet.addCell(label);
            col = col + 3;

            keyName = BTSLUtil.getMessage(locale, "mgtreport.total", null) + BTSLUtil.getMessage(locale, "mgtreport.daily", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "mgtreport.total", null) + BTSLUtil.getMessage(locale, "mgtreport.monthly", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "mgtreport.success", null) + BTSLUtil.getMessage(locale, "mgtreport.daily", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "mgtreport.success", null) + BTSLUtil.getMessage(locale, "mgtreport.monthly", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "mgtreport.fail", null) + BTSLUtil.getMessage(locale, "mgtreport.daily", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "mgtreport.fail", null) + BTSLUtil.getMessage(locale, "mgtreport.monthly", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "mgtreport.ambiguous", null) + BTSLUtil.getMessage(locale, "mgtreport.daily", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "mgtreport.ambiguous", null) + BTSLUtil.getMessage(locale, "mgtreport.monthly", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);

            col = 0;
            p_row++;
            keyName = BTSLUtil.getMessage(locale, "mgtreport.network", null);
            label = new Label(col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "mgtreport.service", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "mgtreport.senderInterface", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "mgtreport.receiverInterface", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.addCell(label);
            for (int i = 0; i < 8; i++) {
                keyName = BTSLUtil.getMessage(locale, "mgtreport.count", null);
                label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet.addCell(label);
                keyName = BTSLUtil.getMessage(locale, "mgtreport.amount", null);
                label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet.addCell(label);
            }

            col = 0;
            p_row++;
            if (p2pList != null && !p2pList.isEmpty()) {
                int i = 0;
                final int j = p2pList.size();
                dailyReportVO = (DailyReportVO) p2pList.get(i);
                label = new Label(col, p_row, dailyReportVO.getNetworkName(), ExcelStyle.getDataStyle());
                p_worksheet.addCell(label);
                label = new Label(++col, p_row, dailyReportVO.getServiceTypeName(), ExcelStyle.getDataStyle());
                p_worksheet.addCell(label);
                label = new Label(++col, p_row, dailyReportVO.getSenderInterfaceName(), ExcelStyle.getDataStyle());
                p_worksheet.addCell(label);
                label = new Label(++col, p_row, dailyReportVO.getReceiverInterfaceName(), ExcelStyle.getDataStyle());
                p_worksheet.addCell(label);
                writeMgtExcelDataRow(p_worksheet, p_row, col, dailyReportVO);

                oldNetworkCode = dailyReportVO.getNetworkCode();
                oldServiceCode = dailyReportVO.getServiceType();
                oldSenderIntCode = dailyReportVO.getCategoryCode();
                oldReceiverIntCode = dailyReportVO.getReceiverInterfaceCode();
                i = i + 1;
                while (i < j) {
                    dailyReportVO = (DailyReportVO) p2pList.get(i);
                    col = 0;
                    p_row++;

                    if (!BTSLUtil.isNullString(oldNetworkCode) && !BTSLUtil.isNullString(dailyReportVO.getNetworkCode()) && !oldNetworkCode.equalsIgnoreCase(dailyReportVO
                        .getNetworkCode())) {
                        label = new Label(col, p_row, dailyReportVO.getNetworkName(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(label);
                    }
                    if (!BTSLUtil.isNullString(oldServiceCode) && !BTSLUtil.isNullString(dailyReportVO.getServiceType()) && !oldServiceCode.equalsIgnoreCase(dailyReportVO
                        .getServiceType())) {
                        label = new Label(++col, p_row, dailyReportVO.getServiceTypeName(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(label);
                    } else {
                        ++col;
                    }
                    if (!BTSLUtil.isNullString(oldSenderIntCode) && !BTSLUtil.isNullString(dailyReportVO.getSenderInterfaceCode()) && !oldSenderIntCode
                        .equalsIgnoreCase(dailyReportVO.getSenderInterfaceCode())) {
                        label = new Label(++col, p_row, dailyReportVO.getSenderInterfaceName(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(label);
                    } else {
                        ++col;
                    }
                    if (!BTSLUtil.isNullString(oldReceiverIntCode) && !BTSLUtil.isNullString(dailyReportVO.getReceiverInterfaceCode()) && !oldReceiverIntCode
                        .equalsIgnoreCase(dailyReportVO.getReceiverInterfaceCode())) {
                        label = new Label(++col, p_row, dailyReportVO.getReceiverInterfaceName(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(label);
                    } else {
                        ++col;
                    }

                    writeMgtExcelDataRow(p_worksheet, p_row, col, dailyReportVO);
                    oldNetworkCode = dailyReportVO.getNetworkCode();
                    oldServiceCode = dailyReportVO.getServiceType();
                    oldSenderIntCode = dailyReportVO.getSenderInterfaceCode();
                    oldReceiverIntCode = dailyReportVO.getReceiverInterfaceCode();
                    i++;
                }
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            _log.error(methodName, " BTSLBaseException e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "Exception=" + e.getMessage());
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Exiting row:" + row);
        }
        return p_row;
    }

    /**
     * method writeMgtExcelInterfaceWiseC2S
     * This method write the c2s transaction data on fourth sheet.
     * 
     * @param p_worksheet
     * @param p_hashMap
     * @param p_row
     * @return
     * @throws BTSLBaseException
     */
    private int writeMgtExcelInterfaceWiseC2S(WritableSheet p_worksheet, HashMap p_hashMap, int p_row) throws BTSLBaseException {
        final String methodName = "writeMgtExcelInterfaceWiseC2S";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " p_hashMap.size():" + p_hashMap.size() + " p_row: " + p_row);
        }
        try {
            final ArrayList c2sInterfaceWiseList = (ArrayList) p_hashMap.get("C2S_INTERFACE_RECHARGE");
            DailyReportVO dailyReportVO = null;
            dailyReportVO = null;
            String keyName = null;
            Label label = null;

            // header headings
            col = 0;
            p_row++;
            keyName = BTSLUtil.getMessage(locale, "mgtreport.module", null) + PretupsI.C2S_MODULE;
            label = new Label(col, p_row, keyName, ExcelStyle.getSecondTopHeadingFont());
            p_worksheet.mergeCells(col, p_row, col, p_row);
            p_worksheet.addCell(label);

            keyName = BTSLUtil.getMessage(locale, "mgtreport.total", null) + BTSLUtil.getMessage(locale, "mgtreport.daily", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "mgtreport.total", null) + BTSLUtil.getMessage(locale, "mgtreport.monthly", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "mgtreport.fail", null) + BTSLUtil.getMessage(locale, "mgtreport.daily", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "mgtreport.fail", null) + BTSLUtil.getMessage(locale, "mgtreport.monthly", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);

            col = 0;
            p_row++;
            keyName = BTSLUtil.getMessage(locale, "mgtreport.interface", null);
            label = new Label(col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, col, p_row);
            p_worksheet.addCell(label);
            for (int i = 0; i < 4; i++) {
                keyName = BTSLUtil.getMessage(locale, "mgtreport.validation", null);
                label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet.addCell(label);
                keyName = BTSLUtil.getMessage(locale, "mgtreport.credit", null);
                label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet.addCell(label);
            }

            if (c2sInterfaceWiseList != null && !c2sInterfaceWiseList.isEmpty()) {
                for (int i = 0, j = c2sInterfaceWiseList.size(); i < j; i++) {
                    dailyReportVO = (DailyReportVO) c2sInterfaceWiseList.get(i);
                    col = 0;
                    p_row++;
                    label = new Label(col, p_row, dailyReportVO.getReceiverInterfaceName(), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                    label = new Label(++col, p_row, String.valueOf(dailyReportVO.getDailyTotalReceiverValCount()), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                    label = new Label(++col, p_row, String.valueOf(dailyReportVO.getDailyTotalCreditCount()), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                    label = new Label(++col, p_row, String.valueOf(dailyReportVO.getMonthlyTotalReceiverValCount()), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                    label = new Label(++col, p_row, String.valueOf(dailyReportVO.getMonthlyTotalCreditCount()), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                    label = new Label(++col, p_row, String.valueOf(dailyReportVO.getDailyFailReceiverValCount()), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                    label = new Label(++col, p_row, String.valueOf(dailyReportVO.getDailyFailCreditCount()), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                    label = new Label(++col, p_row, String.valueOf(dailyReportVO.getMonthlyFailReceiverValCount()), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                    label = new Label(++col, p_row, String.valueOf(dailyReportVO.getMonthlyFailCreditCount()), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                }
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            _log.error(methodName, " BTSLBaseException e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, "writeExcelC2S", "Exception=" + e.getMessage());
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Exiting row:" + row);
        }
        return p_row;
    }

    /**
     * method writeMgtExcelInterfaceWiseP2P
     * This method write the p2p transaction data on fourth sheet.
     * 
     * @param p_worksheet
     * @param p_hashMap
     * @param p_row
     * @return
     * @throws BTSLBaseException
     */
    private int writeMgtExcelInterfaceWiseP2P(WritableSheet p_worksheet, HashMap p_hashMap, int p_row) throws BTSLBaseException {
        final String methodName = "writeMgtExcelInterfaceWiseP2P";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " p_hashMap.size():" + p_hashMap.size() + " p_row: " + p_row);
        }
        try {
            final ArrayList p2pInterfaceWiseList = (ArrayList) p_hashMap.get("P2P_INTERFACE_RECHARGE");

            DailyReportVO dailyReportVO = null;
            dailyReportVO = null;
            String keyName = null;
            Label label = null;

            // header headings
            col = 0;
            p_row++;
            keyName = BTSLUtil.getMessage(locale, "mgtreport.module", null) + PretupsI.P2P_MODULE;
            label = new Label(col, p_row, keyName, ExcelStyle.getSecondTopHeadingFont());
            p_worksheet.mergeCells(col, p_row, col, p_row);
            p_worksheet.addCell(label);

            keyName = BTSLUtil.getMessage(locale, "mgtreport.total", null) + BTSLUtil.getMessage(locale, "mgtreport.daily", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, col + 3, p_row);
            p_worksheet.addCell(label);
            col = col + 3;
            keyName = BTSLUtil.getMessage(locale, "mgtreport.total", null) + BTSLUtil.getMessage(locale, "mgtreport.monthly", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, col + 3, p_row);
            p_worksheet.addCell(label);
            col = col + 3;
            keyName = BTSLUtil.getMessage(locale, "mgtreport.fail", null) + BTSLUtil.getMessage(locale, "mgtreport.daily", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, col + 3, p_row);
            p_worksheet.addCell(label);
            col = col + 3;
            keyName = BTSLUtil.getMessage(locale, "mgtreport.fail", null) + BTSLUtil.getMessage(locale, "mgtreport.monthly", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, col + 3, p_row);
            p_worksheet.addCell(label);
            col = col + 3;

            col = 0;
            p_row++;
            keyName = BTSLUtil.getMessage(locale, "mgtreport.interface", null);
            label = new Label(col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, col, p_row);
            p_worksheet.addCell(label);

            for (int i = 0; i < 4; i++) {
                keyName = BTSLUtil.getMessage(locale, "mgtreport.senderValidation", null);
                label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet.addCell(label);
                keyName = BTSLUtil.getMessage(locale, "mgtreport.debit", null);
                label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet.addCell(label);
                keyName = BTSLUtil.getMessage(locale, "mgtreport.receiverValidation", null);
                label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet.addCell(label);
                keyName = BTSLUtil.getMessage(locale, "mgtreport.credit", null);
                label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet.addCell(label);
            }

            if (p2pInterfaceWiseList != null && !p2pInterfaceWiseList.isEmpty()) {
                for (int i = 0, j = p2pInterfaceWiseList.size(); i < j; i++) {
                    dailyReportVO = (DailyReportVO) p2pInterfaceWiseList.get(i);
                    col = 0;
                    p_row++;
                    label = new Label(col, p_row, dailyReportVO.getReceiverInterfaceName(), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                    label = new Label(++col, p_row, String.valueOf(dailyReportVO.getDailyTotalSenderValCount()), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                    label = new Label(++col, p_row, String.valueOf(dailyReportVO.getDailyTotalDebitCount()), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                    label = new Label(++col, p_row, String.valueOf(dailyReportVO.getDailyTotalReceiverValCount()), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                    label = new Label(++col, p_row, String.valueOf(dailyReportVO.getDailyTotalCreditCount()), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                    label = new Label(++col, p_row, String.valueOf(dailyReportVO.getMonthlyTotalSenderValCount()), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                    label = new Label(++col, p_row, String.valueOf(dailyReportVO.getMonthlyTotalDebitCount()), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                    label = new Label(++col, p_row, String.valueOf(dailyReportVO.getMonthlyTotalReceiverValCount()), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                    label = new Label(++col, p_row, String.valueOf(dailyReportVO.getMonthlyTotalCreditCount()), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                    label = new Label(++col, p_row, String.valueOf(dailyReportVO.getDailyFailSenderValCount()), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                    label = new Label(++col, p_row, String.valueOf(dailyReportVO.getDailyFailDebitCount()), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                    label = new Label(++col, p_row, String.valueOf(dailyReportVO.getDailyFailReceiverValCount()), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                    label = new Label(++col, p_row, String.valueOf(dailyReportVO.getDailyFailCreditCount()), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                    label = new Label(++col, p_row, String.valueOf(dailyReportVO.getMonthlyFailSenderValCount()), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                    label = new Label(++col, p_row, String.valueOf(dailyReportVO.getMonthlyFailDebitCount()), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                    label = new Label(++col, p_row, String.valueOf(dailyReportVO.getMonthlyFailReceiverValCount()), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                    label = new Label(++col, p_row, String.valueOf(dailyReportVO.getMonthlyFailCreditCount()), ExcelStyle.getDataStyle());
                    p_worksheet.addCell(label);
                }
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            _log.error(methodName, " BTSLBaseException e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "Exception=" + e.getMessage());
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Exiting row:" + row);
        }
        return p_row;
    }

    /**
     * method writeTechExcelHeader
     * This method write sheet header.
     * 
     * @param p_worksheet1
     * @param p_reportDate
     * @param p_Header
     * @return
     * @throws BTSLBaseException
     */
    private int writeTechExcelHeader(WritableSheet p_worksheet, Date p_reportDate, String p_Header) throws BTSLBaseException {
        final String methodName = "writeTechExcelHeader";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " p_reportDate:" + p_reportDate + " p_Header=" + p_Header);
        }
        int rowCount = 0;
        col = 0;
        try {
            p_worksheet.setColumnView(0, 20);
            p_worksheet.setColumnView(1, 20);
            p_worksheet.setColumnView(2, 20);

            for (int i = 3; i < 25; i++) {
                p_worksheet.setColumnView(i, 17);
            }

            final Date currentDate = new Date();
            String keyName = BTSLUtil.getMessage(locale, "techreport.header", null);
            Label label = new Label(col, rowCount, keyName, ExcelStyle.getTopHeadingFont());
            p_worksheet.mergeCells(col, rowCount, col + 5, rowCount);
            p_worksheet.addCell(label);

            col = 0;
            rowCount++;
            label = new Label(col, rowCount, p_Header, ExcelStyle.getSecondTopHeadingFont2());
            p_worksheet.mergeCells(col, rowCount, col + 5, rowCount);
            p_worksheet.addCell(label);

            col = 0;
            rowCount++;
            keyName = BTSLUtil.getMessage(locale, "techreport.date", null) + BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(p_reportDate));
            label = new Label(col, rowCount, keyName, ExcelStyle.getSecondTopHeadingFont());
            p_worksheet.mergeCells(col, rowCount, ++col, rowCount);
            p_worksheet.addCell(label);

            label = new Label(++col, rowCount, " ", ExcelStyle.getSecondTopHeadingFont());
            p_worksheet.mergeCells(col, rowCount, ++col, rowCount);
            p_worksheet.addCell(label);

            keyName = BTSLUtil.getMessage(locale, "techreport.generatedon", null) + BTSLDateUtil.getLocaleTimeStamp(BTSLUtil.getDateTimeStringFromDate(currentDate));
            label = new Label(++col, rowCount, keyName, ExcelStyle.getSecondTopHeadingFont());
            p_worksheet.mergeCells(col, rowCount, ++col, rowCount);
            p_worksheet.addCell(label);
            rowCount++;
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            _log.error(methodName, " BTSLBaseException e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "Exception=" + e.getMessage());
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Exiting row:" + row);
        }
        return rowCount;
    }

    /**
     * method writeTechExcelC2S
     * This method write the c2s transaction data.
     * 
     * @param p_worksheet
     * @param p_hashMap
     * @param p_row
     * @return
     * @throws BTSLBaseException
     */
    private int writeTechExcelC2S(WritableSheet p_worksheet, HashMap p_hashMap, int p_row) throws BTSLBaseException {
        final String methodName = "writeTechExcelC2S";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " p_hashMap.size():" + p_hashMap.size() + " p_row: " + p_row);
        }
        try {
            final ArrayList c2sList = (ArrayList) p_hashMap.get("C2S_RECHARGE");

            DailyReportVO dailyReportVO = null;
            dailyReportVO = null;
            String keyName = null;
            Label label = null;
            String oldNetworkCode = null;
            String oldServiceCode = null;
            String oldInterfaceCode = null;

            // header headings
            col = 0;
            keyName = BTSLUtil.getMessage(locale, "techreport.network", null);
            label = new Label(col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, col, p_row + 1);
            p_worksheet.addCell(label);

            keyName = BTSLUtil.getMessage(locale, "techreport.service", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, col, p_row + 1);
            p_worksheet.addCell(label);

            keyName = BTSLUtil.getMessage(locale, "techreport.interface", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, col, p_row + 1);
            p_worksheet.addCell(label);

            keyName = BTSLUtil.getMessage(locale, "techreport.total", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);

            keyName = BTSLUtil.getMessage(locale, "techreport.success", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);

            keyName = BTSLUtil.getMessage(locale, "techreport.fail", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);

            keyName = BTSLUtil.getMessage(locale, "techreport.ambiguous", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);

            keyName = BTSLUtil.getMessage(locale, "techreport.underprocess", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);

            col = 2;
            p_row++;
            for (int i = 0; i < 5; i++) {
                keyName = BTSLUtil.getMessage(locale, "techreport.count", null);
                label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet.addCell(label);
                keyName = BTSLUtil.getMessage(locale, "techreport.amount", null);
                label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet.addCell(label);
            }

            col = 0;
            p_row++;
            if (c2sList != null && !c2sList.isEmpty()) {
                int i = 0;
                final int j = c2sList.size();
                dailyReportVO = (DailyReportVO) c2sList.get(i);
                label = new Label(col, p_row, dailyReportVO.getNetworkName(), ExcelStyle.getDataStyle());
                p_worksheet.addCell(label);
                label = new Label(++col, p_row, dailyReportVO.getServiceTypeName(), ExcelStyle.getDataStyle());
                p_worksheet.addCell(label);
                label = new Label(++col, p_row, dailyReportVO.getReceiverInterfaceName(), ExcelStyle.getDataStyle());
                p_worksheet.addCell(label);
                writeTechExcelDataRow(p_worksheet, p_row, col, dailyReportVO);

                oldNetworkCode = dailyReportVO.getNetworkCode();
                oldServiceCode = dailyReportVO.getServiceType();
                oldInterfaceCode = dailyReportVO.getReceiverInterfaceCode();
                i = i + 1;
                while (i < j) {
                    dailyReportVO = (DailyReportVO) c2sList.get(i);
                    col = 0;
                    p_row++;

                    if (!BTSLUtil.isNullString(oldNetworkCode) && !BTSLUtil.isNullString(dailyReportVO.getNetworkCode()) && !oldNetworkCode.equalsIgnoreCase(dailyReportVO
                        .getNetworkCode())) {
                        label = new Label(col, p_row, dailyReportVO.getNetworkName(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(label);
                    }
                    if (!BTSLUtil.isNullString(oldServiceCode) && !BTSLUtil.isNullString(dailyReportVO.getServiceType()) && !oldServiceCode.equalsIgnoreCase(dailyReportVO
                        .getServiceType())) {
                        label = new Label(++col, p_row, dailyReportVO.getServiceTypeName(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(label);
                    } else {
                        ++col;
                    }
                    if (!BTSLUtil.isNullString(oldInterfaceCode) && !BTSLUtil.isNullString(dailyReportVO.getReceiverInterfaceCode()) && !oldInterfaceCode
                        .equalsIgnoreCase(dailyReportVO.getReceiverInterfaceCode())) {
                        label = new Label(++col, p_row, dailyReportVO.getReceiverInterfaceName(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(label);
                    } else {
                        ++col;
                    }
                    writeTechExcelDataRow(p_worksheet, p_row, col, dailyReportVO);
                    oldNetworkCode = dailyReportVO.getNetworkCode();
                    oldServiceCode = dailyReportVO.getServiceType();
                    oldInterfaceCode = dailyReportVO.getReceiverInterfaceCode();
                    i++;
                }
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            _log.error(methodName, " BTSLBaseException e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "Exception=" + e.getMessage());
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Exiting row:" + row);
        }
        return p_row;
    }

    /**
     * method writeTechExcelDataRow
     * This method writes counts and amounts data row.
     * 
     * @param p_worksheet
     * @param p_row
     * @param p_col
     * @param p_dailyReportVO
     */
    private void writeTechExcelDataRow(WritableSheet p_worksheet, int p_row, int p_col, DailyReportVO p_dailyReportVO) throws BTSLBaseException {
        final String methodName = "writeMgtExcelDataRow";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " p_row:" + p_row + " p_dailyReportVO=" + p_dailyReportVO + " p_col:" + p_col);
        }

        Label label = null;
        try {
            // total counts and amounts
            label = new Label(++p_col, p_row, String.valueOf(p_dailyReportVO.getDailyTotalCount()), ExcelStyle.getDataStyle());
            p_worksheet.addCell(label);
            label = new Label(++p_col, p_row, p_dailyReportVO.getDailyTotalAmountStr(), ExcelStyle.getDataStyle());
            p_worksheet.addCell(label);

            // Success counts and amounts
            label = new Label(++p_col, p_row, String.valueOf(p_dailyReportVO.getDailySuccessCount()), ExcelStyle.getDataStyle());
            p_worksheet.addCell(label);
            label = new Label(++p_col, p_row, p_dailyReportVO.getDailySuccessAmountStr(), ExcelStyle.getDataStyle());
            p_worksheet.addCell(label);

            // Fail counts and amounts
            label = new Label(++p_col, p_row, String.valueOf(p_dailyReportVO.getDailyFailCount()), ExcelStyle.getDataStyle());
            p_worksheet.addCell(label);
            label = new Label(++p_col, p_row, p_dailyReportVO.getDailyFailAmountStr(), ExcelStyle.getDataStyle());
            p_worksheet.addCell(label);

            // Ambiguous counts and amounts
            label = new Label(++p_col, p_row, String.valueOf(p_dailyReportVO.getDailyAmbigousCount()), ExcelStyle.getDataStyle());
            p_worksheet.addCell(label);
            label = new Label(++p_col, p_row, p_dailyReportVO.getDailyAmbigousAmountStr(), ExcelStyle.getDataStyle());
            p_worksheet.addCell(label);

            // Underprocess counts and amounts
            label = new Label(++p_col, p_row, String.valueOf(p_dailyReportVO.getDailyUnderProcessCount()), ExcelStyle.getDataStyle());
            p_worksheet.addCell(label);
            label = new Label(++p_col, p_row, p_dailyReportVO.getDailyUnderProcessAmountStr(), ExcelStyle.getDataStyle());
            p_worksheet.addCell(label);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error("writeTechExcelDataRow", " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, "writeTechExcelDataRow", "Exception=" + e.getMessage());
        }
    }

    /**
     * method writeTechExcelP2P
     * This method write the c2s transaction data on fourth sheet.
     * 
     * @param p_worksheet
     * @param p_hashMap
     * @param p_row
     * @return
     * @throws BTSLBaseException
     */
    private int writeTechExcelP2P(WritableSheet p_worksheet, HashMap p_hashMap, int p_row) throws BTSLBaseException {
        final String methodName = "writeTechExcelP2P";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " p_hashMap.size():" + p_hashMap.size() + " p_row: " + p_row);
        }
        try {
            final ArrayList p2pList = (ArrayList) p_hashMap.get("P2P_RECHARGE");

            DailyReportVO dailyReportVO = null;
            dailyReportVO = null;
            String keyName = null;
            Label label = null;
            String oldNetworkCode = null;
            String oldServiceCode = null;
            String oldSenderIntCode = null;
            String oldReceiverIntCode = null;

            // header headings
            col = 0;

            keyName = BTSLUtil.getMessage(locale, "techreport.network", null);
            label = new Label(col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, col, p_row + 1);
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "techreport.service", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, col, p_row + 1);
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "techreport.senderInterface", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, col, p_row + 1);
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "techreport.receiverInterface", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, col, p_row + 1);
            p_worksheet.addCell(label);

            keyName = BTSLUtil.getMessage(locale, "techreport.total", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "techreport.success", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "techreport.fail", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "techreport.ambiguous", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);
            keyName = BTSLUtil.getMessage(locale, "techreport.underprocess", null);
            label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
            p_worksheet.mergeCells(col, p_row, ++col, p_row);
            p_worksheet.addCell(label);

            col = 3;
            p_row++;
            for (int i = 0; i < 5; i++) {
                keyName = BTSLUtil.getMessage(locale, "techreport.count", null);
                label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet.addCell(label);
                keyName = BTSLUtil.getMessage(locale, "techreport.amount", null);
                label = new Label(++col, p_row, keyName, ExcelStyle.getHeadingFont());
                p_worksheet.addCell(label);
            }

            col = 0;
            p_row++;
            if (p2pList != null && !p2pList.isEmpty()) {
                int i = 0;
                final int j = p2pList.size();
                dailyReportVO = (DailyReportVO) p2pList.get(i);
                label = new Label(col, p_row, dailyReportVO.getNetworkName(), ExcelStyle.getDataStyle());
                p_worksheet.addCell(label);
                label = new Label(++col, p_row, dailyReportVO.getServiceTypeName(), ExcelStyle.getDataStyle());
                p_worksheet.addCell(label);
                label = new Label(++col, p_row, dailyReportVO.getSenderInterfaceName(), ExcelStyle.getDataStyle());
                p_worksheet.addCell(label);
                label = new Label(++col, p_row, dailyReportVO.getReceiverInterfaceName(), ExcelStyle.getDataStyle());
                p_worksheet.addCell(label);
                writeTechExcelDataRow(p_worksheet, p_row, col, dailyReportVO);

                oldNetworkCode = dailyReportVO.getNetworkCode();
                oldServiceCode = dailyReportVO.getServiceType();
                oldSenderIntCode = dailyReportVO.getCategoryCode();
                oldReceiverIntCode = dailyReportVO.getReceiverInterfaceCode();
                i = i + 1;
                while (i < j) {
                    dailyReportVO = (DailyReportVO) p2pList.get(i);
                    col = 0;
                    p_row++;

                    if (!BTSLUtil.isNullString(oldNetworkCode) && !BTSLUtil.isNullString(dailyReportVO.getNetworkCode()) && !oldNetworkCode.equalsIgnoreCase(dailyReportVO
                        .getNetworkCode())) {
                        label = new Label(col, p_row, dailyReportVO.getNetworkName(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(label);
                    }
                    if (!BTSLUtil.isNullString(oldServiceCode) && !BTSLUtil.isNullString(dailyReportVO.getServiceType()) && !oldServiceCode.equalsIgnoreCase(dailyReportVO
                        .getServiceType())) {
                        label = new Label(++col, p_row, dailyReportVO.getServiceTypeName(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(label);
                    } else {
                        ++col;
                    }
                    if (!BTSLUtil.isNullString(oldSenderIntCode) && !BTSLUtil.isNullString(dailyReportVO.getSenderInterfaceCode()) && !oldSenderIntCode
                        .equalsIgnoreCase(dailyReportVO.getSenderInterfaceCode())) {
                        label = new Label(++col, p_row, dailyReportVO.getSenderInterfaceName(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(label);
                    } else {
                        ++col;
                    }
                    if (!BTSLUtil.isNullString(oldReceiverIntCode) && !BTSLUtil.isNullString(dailyReportVO.getReceiverInterfaceCode()) && !oldReceiverIntCode
                        .equalsIgnoreCase(dailyReportVO.getReceiverInterfaceCode())) {
                        label = new Label(++col, p_row, dailyReportVO.getReceiverInterfaceName(), ExcelStyle.getDataStyle());
                        p_worksheet.addCell(label);
                    } else {
                        ++col;
                    }

                    writeTechExcelDataRow(p_worksheet, p_row, col, dailyReportVO);
                    oldNetworkCode = dailyReportVO.getNetworkCode();
                    oldServiceCode = dailyReportVO.getServiceType();
                    oldSenderIntCode = dailyReportVO.getSenderInterfaceCode();
                    oldReceiverIntCode = dailyReportVO.getReceiverInterfaceCode();
                    i++;
                }
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            _log.error(methodName, " BTSLBaseException e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, "writeExcelP2P", "Exception=" + e.getMessage());
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Exiting row:" + row);
        }
        return p_row;
    }

    /**
     * method writeTechnicalExcel
     * This method start the XLS sheet.
     * 
     * @param p_hashMap
     * @param p_locale
     * @param p_fileName
     * @throws BTSLBaseException
     */
    public void writeTechnicalExcel(HashMap p_hashMap, Locale p_locale, String p_fileName) throws BTSLBaseException {
        final String methodName = "writeTechnicalExcel";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " p_hashMap:" + p_hashMap + " p_locale: " + p_locale + " p_fileName: " + p_fileName);
        }
        WritableWorkbook workbook = null;
        WritableSheet worksheet1 = null;
        WritableSheet worksheet2 = null;
        ListValueVO listValueVO = null;
        locale = p_locale;
        File fileName = null;
        String repHeader = null;
        int index = 0;
        int mgtRow = 0;
        try {
            final Date reportDate = (Date) p_hashMap.get("REPORT_DATE");
            fileName = new File(p_fileName);
            workbook = Workbook.createWorkbook(fileName);

            final ArrayList moduleList = LookupsCache.loadLookupDropDown(PretupsI.MODULE_TYPE, true);
            for (int m = 0, n = moduleList.size(); m < n; m++) {
                listValueVO = (ListValueVO) moduleList.get(m);
                if (PretupsI.C2S_MODULE.equals(listValueVO.getValue())) {
                    repHeader = BTSLUtil.getMessage(locale, "techreport.c2s.sheet.name", null);
                    worksheet1 = workbook.createSheet(repHeader, index++);
                    repHeader = BTSLUtil.getMessage(locale, "techreport.c2s.recharge.header", null);
                    mgtRow = writeTechExcelHeader(worksheet1, reportDate, repHeader);
                    mgtRow = writeTechExcelC2S(worksheet1, p_hashMap, mgtRow);
                    mgtRow++;
                } else if (PretupsI.P2P_MODULE.equals(listValueVO.getValue())) {
                    repHeader = BTSLUtil.getMessage(locale, "techreport.p2p.sheet.name", null);
                    worksheet2 = workbook.createSheet(repHeader, index++);
                    repHeader = BTSLUtil.getMessage(locale, "techreport.p2p.recharge.header", null);
                    mgtRow = writeTechExcelHeader(worksheet2, reportDate, repHeader);
                    mgtRow = writeTechExcelP2P(worksheet2, p_hashMap, mgtRow);
                    mgtRow++;
                }
            }
            workbook.write();
        } catch (BTSLBaseException be) {
            if (fileName != null) {
            	boolean isDeleted = fileName.delete();
                if(isDeleted){
                 _log.debug(methodName, "File deleted successfully");
                }
            }
            _log.errorTrace(methodName, be);
            _log.error(methodName, " BTSLBaseException	e: " + be.getMessage());
            throw be;
        } catch (Exception e) {
            if (fileName != null) {
            	boolean isDeleted = fileName.delete();
                if(isDeleted){
                 _log.debug(methodName, "File deleted successfully");
                }
            }
            _log.errorTrace(methodName, e);
            _log.error(methodName, " Exception e: " + e.getMessage());
            throw new BTSLBaseException(this, methodName, "Exception=" + e.getMessage());
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            worksheet1 = null;
            worksheet2 = null;
            workbook = null;
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exiting");
            }
        }
    }
}
/*
 * @# CosExcelRW.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Rajdeep Deb October, 2009 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2006 Bharti Telesoft Ltd.
 * This class use for read write in xls file for COS management.
 */
package com.btsl.pretups.xl;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import com.btsl.util.MessageResources;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cosmgmt.businesslogic.CosVO;
import com.btsl.xl.ExcelFileConstants;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class CosExcelRW {

    private Log _log = LogFactory.getLog(this.getClass().getName());
    private WritableCellFeatures cellFeatures = new WritableCellFeatures();
    private WritableFont times10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false);
    private WritableCellFormat times10format = new WritableCellFormat(times10font);
    private MessageResources p_messages = null;
    private Locale p_locale = null;

    public void writeCosDefineExcel(String p_excelID, MessageResources messages, Locale locale, String p_fileName) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeCosDefineExcel", " p_excelID: " + p_excelID + " p_locale: " + locale + " p_fileName: " + p_fileName);
        }
        final String METHOD_NAME = "writeCosDefineExcel";
        WritableWorkbook workbook = null;
        WritableSheet worksheet = null;
        int col = 0;
        int row = 0;
        try {
            workbook = Workbook.createWorkbook(new File(p_fileName));
            worksheet = workbook.createSheet("Define COS", 0);
            p_messages = messages;
            p_locale = locale;
            Label label = null;
            String keyName = null;
            String comment = null;

            keyName = p_messages.getMessage(p_locale, "cosmanagement.define.xls.label.oldcos");
            label = new Label(col, row, keyName, times10format);
            comment = p_messages.getMessage(p_locale, "cosmanagement.define.xls.label.oldcos.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet.addCell(label);

            keyName = p_messages.getMessage(p_locale, "cosmanagement.define.xls.label.fromrecharge");
            label = new Label(++col, row, keyName, times10format);
            comment = p_messages.getMessage(p_locale, "cosmanagement.define.xls.label.fromrecharge.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet.addCell(label);

            keyName = p_messages.getMessage(p_locale, "cosmanagement.define.xls.label.torecharge");
            label = new Label(++col, row, keyName, times10format);
            comment = p_messages.getMessage(p_locale, "cosmanagement.define.xls.label.torecharge.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet.addCell(label);

            keyName = p_messages.getMessage(p_locale, "cosmanagement.define.xls.label.newcos");
            label = new Label(++col, row, keyName, times10format);
            comment = p_messages.getMessage(p_locale, "cosmanagement.define.xls.label.newcos.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet.addCell(label);

            workbook.write();
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeCosDefineExcel", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeCosDefineExcel", " Exception e: " + e.getMessage());
            throw e;
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            worksheet = null;
            workbook = null;
        }
        if (_log.isDebugEnabled()) {
            _log.debug("writeCosDefineExcel", " Exiting");
        }
    }

    public void writeCosManageExcel(ArrayList p_list, MessageResources messages, Locale locale, String p_fileName) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeCosManageExcel", " p_locale: " + locale + " p_fileName: " + p_fileName);
        }
        final String METHOD_NAME = "writeCosManageExcel";
        WritableWorkbook workbook = null;
        WritableSheet worksheet = null;
        int col = 0;
        int row = 0;
        try {
            workbook = Workbook.createWorkbook(new File(p_fileName));
            worksheet = workbook.createSheet("Manage COS", 0);
            p_messages = messages;
            p_locale = locale;
            Label label = null;
            String keyName = null;
            String comment = null;

            keyName = p_messages.getMessage(p_locale, "cosmanagement.define.xls.label.oldcos");
            label = new Label(col, row, keyName, times10format);
            comment = p_messages.getMessage(p_locale, "cosmanagement.define.xls.label.oldcos.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet.addCell(label);

            keyName = p_messages.getMessage(p_locale, "cosmanagement.define.xls.label.fromrecharge");
            label = new Label(++col, row, keyName, times10format);
            comment = p_messages.getMessage(p_locale, "cosmanagement.define.xls.label.fromrecharge.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet.addCell(label);

            keyName = p_messages.getMessage(p_locale, "cosmanagement.define.xls.label.torecharge");
            label = new Label(++col, row, keyName, times10format);
            comment = p_messages.getMessage(p_locale, "cosmanagement.define.xls.label.torecharge.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet.addCell(label);

            keyName = p_messages.getMessage(p_locale, "cosmanagement.define.xls.label.newcos");
            label = new Label(++col, row, keyName, times10format);
            comment = p_messages.getMessage(p_locale, "cosmanagement.define.xls.label.newcos.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet.addCell(label);

            keyName = p_messages.getMessage(p_locale, "cosmanagement.define.xls.label.status");
            label = new Label(++col, row, keyName, times10format);
            comment = p_messages.getMessage(p_locale, "cosmanagement.define.xls.label.status.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet.addCell(label);
            row++;
            col = 0;

            CosVO cosVO = null;
            if (p_list != null) {
                for (int i = 0, j = p_list.size(); i < j; i++) {
                    col = 0;
                    cosVO = (CosVO) p_list.get(i);
                    label = new Label(col, row, cosVO.getOldCosCode());
                    worksheet.addCell(label);
                    label = new Label(++col, row, cosVO.getFromRecharge());
                    worksheet.addCell(label);
                    label = new Label(++col, row, cosVO.getToRecharge());
                    worksheet.addCell(label);
                    label = new Label(++col, row, cosVO.getNewCosCode());
                    worksheet.addCell(label);
                    label = new Label(++col, row, cosVO.getStatus());
                    worksheet.addCell(label);
                    row++;
                }
            }
            workbook.write();
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeCosManageExcel", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeCosManageExcel", " Exception e: " + e.getMessage());
            throw e;
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            worksheet = null;
            workbook = null;
        }
        if (_log.isDebugEnabled()) {
            _log.debug("writeCosManageExcel", " Exiting");
        }
    }

    /**
     * readDefineExcel
     * 
     * @param p_excelID
     * @param p_fileName
     * @return
     * @throws Exception
     */

    public String[][] readDefineExcel(String p_excelID, String p_fileName) {
        if (_log.isDebugEnabled()) {
            _log.debug("readDefineExcel", " p_excelID: " + p_excelID + " p_fileName: " + p_fileName);
        }
        final String METHOD_NAME = "readDefineExcel";
        String strArr[][] = null;
        Workbook workbook = null;
        Sheet excelsheet = null;
        int noOfSheet = 0;
        try {
            workbook = Workbook.getWorkbook(new File(p_fileName));
            noOfSheet = workbook.getNumberOfSheets();
            excelsheet = workbook.getSheet(0);

            int noOfRows = excelsheet.getRows();
            int noOfcols = excelsheet.getColumns();
            strArr = new String[noOfRows][noOfcols];
            Cell cell = null;
            String content = null;
            String key = null;
            int[] indexMapArray = new int[noOfcols];
            String indexStr = null;
            for (int col = 0; col < noOfcols; col++) {
                indexStr = null;
                key = ExcelFileConstants.getReadProperty(p_excelID, String.valueOf(col));
                if (key == null) {
                    key = String.valueOf(col);
                }
                indexStr = ExcelFileConstants.getReadProperty(p_excelID, String.valueOf(col));
                if (indexStr == null) {
                    indexStr = String.valueOf(col);
                }
                indexMapArray[col] = Integer.parseInt(indexStr);
                strArr[0][indexMapArray[col]] = key;

            }
            for (int row = 1; row < noOfRows; row++) {
                for (int col = 0; col < noOfcols; col++) {
                    cell = excelsheet.getCell(col, row);
                    content = cell.getContents();
                    content = content.replaceAll("\n", " ");
                    content = content.replaceAll("\r", " ");
                    strArr[row][indexMapArray[col]] = content;
                }
            }

            return strArr;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("readDefineExcel", " Exception e: " + e.getMessage());
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            workbook = null;
            excelsheet = null;
            if (_log.isDebugEnabled()) {
                _log.debug("readDefineExcel", " Exiting strArr: " + strArr);
            }
        }
        return strArr;
    }

    /**
     * readManageExcel
     * 
     * @param p_excelID
     * @param p_fileName
     * @return
     * @throws Exception
     */

    public String[][] readManageExcel(String p_excelID, String p_fileName) {
        if (_log.isDebugEnabled()) {
            _log.debug("readManageExcel", " p_excelID: " + p_excelID + " p_fileName: " + p_fileName);
        }
        final String METHOD_NAME = "readManageExcel";
        String strArr[][] = null;
        Workbook workbook = null;
        Sheet excelsheet = null;
        int noOfSheet = 0;
        try {
            workbook = Workbook.getWorkbook(new File(p_fileName));
            noOfSheet = workbook.getNumberOfSheets();
            excelsheet = workbook.getSheet(0);

            int noOfRows = excelsheet.getRows();
            int noOfcols = excelsheet.getColumns();
            strArr = new String[noOfRows][noOfcols];
            Cell cell = null;
            String content = null;
            String key = null;
            int[] indexMapArray = new int[noOfcols];
            String indexStr = null;
            for (int col = 0; col < noOfcols; col++) {
                indexStr = null;
                key = ExcelFileConstants.getReadProperty(p_excelID, String.valueOf(col));
                if (key == null) {
                    key = String.valueOf(col);
                }
                indexStr = ExcelFileConstants.getReadProperty(p_excelID, String.valueOf(col));
                if (indexStr == null) {
                    indexStr = String.valueOf(col);
                }
                indexMapArray[col] = Integer.parseInt(indexStr);
                strArr[0][indexMapArray[col]] = key;

            }
            for (int row = 1; row < noOfRows; row++) {
                for (int col = 0; col < noOfcols; col++) {
                    cell = excelsheet.getCell(col, row);
                    content = cell.getContents();
                    content = content.replaceAll("\n", " ");
                    content = content.replaceAll("\r", " ");
                    strArr[row][indexMapArray[col]] = content;
                }
            }

            return strArr;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("readManageExcel", " Exception e: " + e.getMessage());
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            workbook = null;
            excelsheet = null;
            if (_log.isDebugEnabled()) {
                _log.debug("readManageExcel", " Exiting strArr: " + strArr);
            }
        }
        return strArr;
    }

}

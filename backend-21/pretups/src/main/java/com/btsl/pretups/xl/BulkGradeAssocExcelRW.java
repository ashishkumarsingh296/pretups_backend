/*
 * @# BulkGradeAssocExcelRW.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Lalit March, 2010 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2010 Comviva Ltd.
 * This class use for read write in xls file for Batch grade management.
 */
package com.btsl.pretups.xl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.util.MessageResources;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
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

public class BulkGradeAssocExcelRW {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private WritableCellFeatures cellFeatures = new WritableCellFeatures();
    private WritableFont times10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false);
    private WritableCellFormat times10format = new WritableCellFormat(times10font);
    private MessageResources p_messages = null;
    private Locale p_locale = null;

    private int writeCellGroups(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeCellGroups", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeCellGroups";
        try {
            String keyName = p_messages.getMessage(p_locale, "userbulkgradeassociation.upload.datasheet.gradename");
            Label label = new Label(col, row, keyName, times10format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "userbulkgradeassociation.upload.datasheet.gradecode");
            label = new Label(++col, row, keyName, times10format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_USER_GRADE_LIST);
            // ArrayList list =
            // (ArrayList)p_hashMap.get(PretupsI.BATCH_USER_GRADE_VO_LIST);

            GradeVO gradeVO = null;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    gradeVO = (GradeVO) list.get(i);
                    label = new Label(col, row, gradeVO.getGradeName());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, gradeVO.getGradeCode());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeCellGroups", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeCellGroups", " Exception e: " + e.getMessage());
            throw e;
        }
    }

    /**
     * readExcel
     * 
     * @param p_excelID
     * @param p_fileName
     * @return
     * @throws Exception
     */

    public String[][] readExcel(String p_excelID, String p_fileName) {
        if (_log.isDebugEnabled()) {
            _log.debug("readExcel", " p_excelID: " + p_excelID + " p_fileName: " + p_fileName);
        }
        final String METHOD_NAME = "readExcel";
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
            _log.error("readExcel", " Exception e: " + e.getMessage());
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
                _log.debug("readExcel", " Exiting strArr: " + strArr);
            }
        }
        return strArr;
    }

    public void writeExcelForBulkAssociation(String p_excelID, HashMap p_hashMap, MessageResources messages, Locale locale, String p_fileName) {
        if (_log.isDebugEnabled()) {
            _log.debug("writeExcelForBulkAssociation", " p_excelID: " + p_excelID + " p_hashMap:" + p_hashMap + " p_locale: " + locale + " p_fileName: " + p_fileName);
        }
        final String METHOD_NAME = "writeExcelForBulkAssociation";
        WritableWorkbook workbook = null;
        WritableSheet worksheet1 = null;
        WritableSheet worksheet2 = null;
        int col = 0;
        int row = 0;
        // added by akanksha for batch grade management
        String noOfRowsInOneTemplate = null; // No. of users data in one sheet
        int noOfTotalSheet = 0;
        int gradeListSize = 0;
        ArrayList gradeList = null;

        try {
            p_messages = messages;
            p_locale = locale;
            Label label = null;
            String keyName = null;
            workbook = Workbook.createWorkbook(new File(p_fileName));
            gradeList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USER_GRADE_VO_LIST);
            gradeListSize = gradeList.size();
            noOfRowsInOneTemplate = Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE_BATCH_GRADE_MANAGEMENT");
            int noOfRowsPerTemplate = 0;
            if (!BTSLUtil.isNullString(noOfRowsInOneTemplate)) {
                noOfRowsInOneTemplate = noOfRowsInOneTemplate.trim();
                noOfRowsPerTemplate = Integer.parseInt(noOfRowsInOneTemplate);
            } else {
                noOfRowsPerTemplate = 65500; // Default value of rows
            }
            // Number of sheet to display the grade list
            noOfTotalSheet = BTSLUtil.parseDoubleToInt( Math.ceil(BTSLUtil.parseIntToDouble(gradeListSize) / noOfRowsPerTemplate));

            if (noOfTotalSheet > 1) {
                int i = 0;
                int k = 0;
                ArrayList tempList = new ArrayList();
                for (i = 0; i < noOfTotalSheet; i++) {
                    tempList.clear();
                    if (k + noOfRowsPerTemplate < gradeListSize) {
                        for (int j = k; j < k + noOfRowsPerTemplate; j++) {
                            tempList.add(gradeList.get(j));
                        }
                    } else {
                        for (int j = k; j < gradeListSize; j++) {
                            tempList.add(gradeList.get(j));
                        }
                    }
                    p_hashMap.put(PretupsI.BATCH_USER_GRADE_VO_LIST, tempList);
                    worksheet1 = workbook.createSheet("Template Sheet " + (i + 1), i);
                    this.writeInDataSheetForAssociation(worksheet1, col, row, p_hashMap);
                    k = k + noOfRowsPerTemplate;
                }
                worksheet2 = workbook.createSheet("Master Sheet", i);
                p_hashMap.put(PretupsI.BATCH_USER_GRADE_VO_LIST, gradeList);
            } else {
                worksheet1 = workbook.createSheet("Template Sheet", 0);
                this.writeInDataSheetForAssociation(worksheet1, col, row, p_hashMap);
                worksheet2 = workbook.createSheet("Master Sheet", 1);
            }
            row = this.writeCellGroups(worksheet2, col, row, p_hashMap);
            workbook.write();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeExcelForBulkAssociation", " Exception e: " + e.getMessage());
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            worksheet1 = null;
            worksheet2 = null;
            workbook = null;
            if (_log.isDebugEnabled()) {
                _log.debug("writeExcelForBulkAssociation", " Exiting");
            }
        }
    }

    /**
     * @param worksheet1
     * @param col
     * @param row
     * @param p_hashMap
     * @throws Exception
     */
    private int writeInDataSheetForAssociation(WritableSheet worksheet1, int col, int row, HashMap p_hashMap) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeInDataSheetForAssociation", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeInDataSheetForAssociation";
        try {

            String keyName = p_messages.getMessage(p_locale, "userbulkgradeassociation.upload.datasheet.owneruserid");
            Label label = new Label(col++, row, keyName, times10format);
            // add comment
            String comment = p_messages.getMessage(p_locale, "userbulkgradeassociation.upload.datasheet.owneruserid.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "userbulkgradeassociation.upload.datasheet.ownerusername");
            label = new Label(col++, row, keyName, times10format);
            // add comment
            comment = p_messages.getMessage(p_locale, "userbulkgradeassociation.upload.datasheet.ownerusername.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "userbulkgradeassociation.upload.datasheet.userid");
            label = new Label(col++, row, keyName, times10format);
            // add comment
            comment = p_messages.getMessage(p_locale, "userbulkgradeassociation.upload.datasheet.userid.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "userbulkgradeassociation.upload.datasheet.username");
            label = new Label(col++, row, keyName, times10format);
            // add comment
            comment = p_messages.getMessage(p_locale, "userbulkgradeassociation.upload.datasheet.username.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "userbulkgradeassociation.upload.datasheet.userassociatedgradecode");
            label = new Label(col++, row, keyName, times10format);
            // add comment
            comment = p_messages.getMessage(p_locale, "userbulkgradeassociation.upload.datasheet.userassociatedgradecode.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "userbulkgradeassociation.upload.datasheet.gradecode");
            label = new Label(col++, row, keyName, times10format);
            // add comment
            comment = p_messages.getMessage(p_locale, "userbulkgradeassociation.upload.datasheet.gradecode.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_USER_GRADE_VO_LIST);
            GradeVO gradeVO = null;
            if (list != null) {
                row++;
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    gradeVO = (GradeVO) list.get(i);
                    label = new Label(col, row, gradeVO.getOwnerCategoryCode());
                    worksheet1.addCell(label);
                    label = new Label(++col, row, gradeVO.getOwnerCategoryName());
                    worksheet1.addCell(label);
                    label = new Label(++col, row, gradeVO.getCategoryUserId());
                    worksheet1.addCell(label);
                    label = new Label(++col, row, gradeVO.getCategoryUserName());
                    worksheet1.addCell(label);
                    label = new Label(++col, row, gradeVO.getGradeCode());
                    worksheet1.addCell(label);

                    row++;
                }
            }

            return row;

        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeInDataSheetForAssociation", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeInDataSheetForAssociation", " Exception e: " + e.getMessage());
            throw e;
        }
    }

    /**
     * readReassociationExcel
     * 
     * @param p_excelID
     * @param p_fileName
     * @param boolean p_readLastSheet
     * @param int p_leftHeaderLinesForEachSheet
     * @param HashMap
     *            <String,String> map
     * @return array
     * @throws Exception
     */
    public String[][] readAassociationExcel(String p_excelID, String p_filePath, boolean p_readLastSheet, int p_leftHeaderLinesForEachSheet, HashMap<String, String> map) {
        final String METHOD_NAME = "readAassociationExcel";
        String[][] strArr = null;
        Workbook workbook = null;
        Sheet excelsheet = null;
        int noOfSheet = 0;
        int noOfRows = 0;
        int noOfcols = 0;
        int arrRow = p_leftHeaderLinesForEachSheet;
        try {

            workbook = Workbook.getWorkbook(new File(p_filePath));
            noOfSheet = workbook.getNumberOfSheets();
            // excelsheet = workbook.getSheet(0);
            if (!p_readLastSheet) {
                noOfSheet = noOfSheet - 1;
            }
            for (int i = 0; i < noOfSheet; i++) {
                excelsheet = workbook.getSheet(i);
                noOfRows = noOfRows + (excelsheet.getRows() - p_leftHeaderLinesForEachSheet);
                noOfcols = excelsheet.getColumns();
            }
            strArr = new String[noOfRows + p_leftHeaderLinesForEachSheet][noOfcols];
            for (int i = 0; i < noOfSheet; i++) {
                excelsheet = workbook.getSheet(i);
                noOfRows = excelsheet.getRows();
                noOfcols = excelsheet.getColumns();
                Cell cell = null;
                String content = null;
                String key = null;
                int[] indexMapArray = new int[noOfcols];
                String indexStr = null;
                for (int k = 0; k < p_leftHeaderLinesForEachSheet; k++) {
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
                        strArr[k][indexMapArray[col]] = key;

                    }

                }
                for (int row = p_leftHeaderLinesForEachSheet; row < noOfRows; row++) {
                    map.put(Integer.toString(arrRow + 1), excelsheet.getName() + PretupsI.ERROR_LINE + (row + 1));
                    for (int col = 0; col < noOfcols; col++) {
                        cell = excelsheet.getCell(col, row);
                        content = cell.getContents();
                        content = content.replaceAll("\n", " ");
                        content = content.replaceAll("\r", " ");
                        strArr[arrRow][indexMapArray[col]] = content;
                    }
                    arrRow++;
                }
            }

            return strArr;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("readAassociationExcel", " Exception e: " + e.getMessage());
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
                _log.debug("readAassociationExcel", " Exiting strArr: " + strArr);
            }
        }
        return strArr;
    }

}

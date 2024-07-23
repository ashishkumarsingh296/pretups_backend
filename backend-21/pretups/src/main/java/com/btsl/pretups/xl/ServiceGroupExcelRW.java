/*
 * @# ServiceGroupExcelRW.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Rajdeep Deb September, 2009 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2009 Comviva Ltd.
 * This class use for read write in xls file for service group management.
 */
package com.btsl.pretups.xl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.util.MessageResources;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.servicegpmgt.businesslogic.ServiceGpMgmtVO;
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

public class ServiceGroupExcelRW {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private WritableCellFeatures cellFeatures = new WritableCellFeatures();
    private WritableFont times10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false);
    private WritableCellFormat times10format = new WritableCellFormat(times10font);
    private MessageResources p_messages = null;
    private Locale p_locale = null;

    public void writeExcel(String p_excelID, HashMap p_hashMap, MessageResources messages, Locale locale, String p_fileName) {
        if (_log.isDebugEnabled()) {
            _log.debug("writeExcel", " p_excelID: " + p_excelID + " p_hashMap:" + p_hashMap + " p_locale: " + locale + " p_fileName: " + p_fileName);
        }
        final String METHOD_NAME = "writeExcel";
        WritableWorkbook workbook = null;
        WritableSheet worksheet1 = null;
        WritableSheet worksheet2 = null;
        int col = 0;
        int row = 0;

        try {
            workbook = Workbook.createWorkbook(new File(p_fileName));
            worksheet1 = workbook.createSheet("Template Sheet", 0);
            worksheet2 = workbook.createSheet("Master Sheet", 1);
            p_messages = messages;
            p_locale = locale;
            Label label = null;
            String keyName = null;

            row = this.writeCellGroups(worksheet2, col, row, p_hashMap);
            col = 0;
            row = 0;
            this.writeInDataSheet(worksheet1, col, row, p_hashMap);
            workbook.write();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeExcel", " Exception e: " + e.getMessage());
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
                _log.debug("writeExcel", " Exiting");
            }
        }
    }

    private int writeCellGroups(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) {
        if (_log.isDebugEnabled()) {
            _log.debug("writeCellGroups", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeCellGroups";
        try {

            String keyName = p_messages.getMessage(p_locale, "servicegroup.upload.datasheet.servicegroupcode");
            Label label = new Label(col, row, keyName, times10format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "servicegroup.upload.mastersheet.servicegroupname");
            label = new Label(++col, row, keyName, times10format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.SERVICE_GROUP_LIST);
            ServiceGpMgmtVO serviceVO = null;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    serviceVO = (ServiceGpMgmtVO) list.get(i);
                    label = new Label(col, row, serviceVO.getGroupId());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, serviceVO.getGroupName());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeCellGroups", " Exception e: " + e.getMessage());
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeCellGroups", " Exception e: " + e.getMessage());
        }
		return row;
    }

    /**
     * @param worksheet1
     * @param col
     * @param row
     * @param p_hashMap
     * @throws Exception
     */
    private void writeInDataSheet(WritableSheet worksheet1, int col, int row, HashMap p_hashMap) {
        final String METHOD_NAME = "writeInDataSheet";
        if (_log.isDebugEnabled()) {
            _log.debug("writeInDataSheet", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        try {

            String keyName = p_messages.getMessage(p_locale, "servicegroup.upload.datasheet.servicegroupcode");
            Label label = new Label(col++, row, keyName, times10format);
            // add comment
            String comment = p_messages.getMessage(p_locale, "servicegroup.upload.datasheet.groupid.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "servicegroup.upload.datasheet.serviceid");
            label = new Label(col++, row, keyName, times10format);
            // add comment
            comment = p_messages.getMessage(p_locale, "servicegroup.upload.datasheet.serviceid.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeInDataSheet", " Exception e: " + e.getMessage());
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeInDataSheet", " Exception e: " + e.getMessage());
        }
    }

    /**
     * readExcel
     * 
     * @param p_excelID
     * @param p_fileName
     * @param p_readLastSheet
     * @param p_leftHeaderLinesForEachSheet
     * @param p_map
     * @return
     * @throws Exception
     */

    public String[][] readExcel(String p_excelID, String p_fileName, boolean p_readLastSheet, int p_leftHeaderLinesForEachSheet, HashMap<String, String> p_map) {
        final String METHOD_NAME = "readExcel";
        if (_log.isDebugEnabled()) {
            _log.debug("readExcel", " p_excelID: " + p_excelID + " p_fileName: " + p_fileName + " p_readLastSheet=" + p_readLastSheet, " p_leftHeaderLinesForEachSheet=" + p_leftHeaderLinesForEachSheet);
        }
        String strArr[][] = null;
        Workbook workbook = null;
        Sheet excelsheet = null;
        int arrRow = p_leftHeaderLinesForEachSheet;
        int noOfSheet = 0;
        int noOfRows = 0;
        int noOfcols = 0;

        try {
            workbook = Workbook.getWorkbook(new File(p_fileName));
            noOfSheet = workbook.getNumberOfSheets();
            if (!p_readLastSheet) {
                noOfSheet = noOfSheet - 1;
            }
            // Total number of rows in the excel sheet
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
                        // strArr[0][indexMapArray[col]] = key;
                        strArr[k][indexMapArray[col]] = key;
                    }
                }
                for (int row = p_leftHeaderLinesForEachSheet; row < noOfRows; row++) {
                    p_map.put(Integer.toString(arrRow + 1), excelsheet.getName() + PretupsI.ERROR_LINE + (row + 1));
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

    public void writeExcelForReassociate(String p_excelID, HashMap p_hashMap, MessageResources messages, Locale locale, String p_fileName) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeExcelForReassociate", " p_excelID: " + p_excelID + " p_hashMap:" + p_hashMap + " p_locale: " + locale + " p_fileName: " + p_fileName);
        }
        final String METHOD_NAME = "writeExcelForReassociate";
        WritableWorkbook workbook = null;
        WritableSheet worksheet1 = null;
        WritableSheet worksheet2 = null;
        int col = 0;
        int row = 0;
        String noOfRowsInOneTemplate = null; // No. of users data in one sheet
        int noOfTotalSheet = 0;
        int serviceGroupListSize = 0;
        ArrayList serviceGroupList = null;

        try {
            // added byy akanksha for mutiple excel sheets
            noOfRowsInOneTemplate = Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE_SERVICEGROUPMANAGEMENT");
            serviceGroupList = (ArrayList) p_hashMap.get(PretupsI.SERVICE_ID_VO_LIST);
            serviceGroupListSize = serviceGroupList.size();
            int noOfRowsPerTemplate = 0;
            if (!BTSLUtil.isNullString(noOfRowsInOneTemplate)) {
                noOfRowsInOneTemplate = noOfRowsInOneTemplate.trim();
                noOfRowsPerTemplate = Integer.parseInt(noOfRowsInOneTemplate);
            } else {
                noOfRowsPerTemplate = 65500; // Default value of rows
            }
            // Number of sheet to display the user list
            noOfTotalSheet = BTSLUtil.parseDoubleToInt( Math.ceil(BTSLUtil.parseIntToDouble(serviceGroupListSize) / noOfRowsPerTemplate));
            workbook = Workbook.createWorkbook(new File(p_fileName));
            if (noOfTotalSheet > 1) {
                int i = 0;
                int k = 0;
                ArrayList tempList = new ArrayList();
                p_messages = messages;
                p_locale = locale;
                for (i = 0; i < noOfTotalSheet; i++) {
                    tempList.clear();
                    if (k + noOfRowsPerTemplate < serviceGroupListSize) {
                        for (int j = k; j < k + noOfRowsPerTemplate; j++) {
                            tempList.add(serviceGroupList.get(j));
                        }
                    } else {
                        for (int j = k; j < serviceGroupListSize; j++) {
                            tempList.add(serviceGroupList.get(j));
                        }
                    }
                    p_hashMap.put(PretupsI.SERVICE_ID_VO_LIST, tempList);
                    worksheet1 = workbook.createSheet("Template Sheet" + (i + 1), i);
                    this.writeInDataSheetForReassociation(worksheet1, col, row, p_hashMap);
                    k = k + noOfRowsPerTemplate;
                }
                worksheet2 = workbook.createSheet("Master Sheet", i);
            }
            // End of multiple sheet requirement ------ 07/03/2008

            else {

                worksheet1 = workbook.createSheet("Template Sheet", 0);
                worksheet2 = workbook.createSheet("Master Sheet", 1);
                p_messages = messages;
                p_locale = locale;
                this.writeInDataSheetForReassociation(worksheet1, col, row, p_hashMap);
                col = 0;
                row = 0;
            }
            row = this.writeCellGroups(worksheet2, col, row, p_hashMap);
            workbook.write();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeExcelForReassociate", " Exception e: " + e.getMessage());
            throw new BTSLBaseException(e);
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
                _log.debug("writeExcelForReassociate", " Exiting");
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
    private int writeInDataSheetForReassociation(WritableSheet worksheet1, int col, int row, HashMap p_hashMap) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeInDataSheetForReassociation", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeInDataSheetForReassociation";
        try {

            String keyName = p_messages.getMessage(p_locale, "servicegroup.reassociate.datasheet.serviceid");
            Label label = new Label(col++, row, keyName, times10format);
            // add comment
            String comment = p_messages.getMessage(p_locale, "servicegroup.reassociate.datasheet.serviceid.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "servicegroup.reassociate.datasheet.asscservicegroupid");
            label = new Label(col++, row, keyName, times10format);
            // add comment
            comment = p_messages.getMessage(p_locale, "servicegroup.reassociate.datasheet.asscservicegroupid.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "servicegroup.reassociate.datasheet.serviceidstatus");
            label = new Label(col++, row, keyName, times10format);
            // add comment
            comment = p_messages.getMessage(p_locale, "servicegroup.reassociate.datasheet.serviceidstatus.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "servicegroup.reassociate.datasheet.servicegrouptobeassc");
            label = new Label(col++, row, keyName, times10format);
            // add comment
            comment = p_messages.getMessage(p_locale, "servicegroup.reassociate.datasheet.servicegrouptobeassc.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "servicegroup.reassociate.datasheet.action");
            label = new Label(col++, row, keyName, times10format);
            // add comment
            comment = p_messages.getMessage(p_locale, "servicegroup.reassociate.datasheet.action.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.SERVICE_ID_VO_LIST);
            ServiceGpMgmtVO serviceVO = null;
            if (list != null) {
                row++;
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    serviceVO = (ServiceGpMgmtVO) list.get(i);
                    label = new Label(col, row, serviceVO.getServiceName());
                    worksheet1.addCell(label);
                    label = new Label(++col, row, serviceVO.getGroupId());
                    worksheet1.addCell(label);
                    if (serviceVO.getStatus().equalsIgnoreCase("Y")) {
                        serviceVO.setStatusDescription(PretupsI.SERVICE_ID_STATUS_ACTIVE);
                    } else if (serviceVO.getStatus().equalsIgnoreCase(PretupsI.SERVICE_ID_STATUS_S)) {
                        serviceVO.setStatusDescription(PretupsI.SERVICE_ID_STATUS_SUSPEND);
                    }
                    label = new Label(++col, row, serviceVO.getStatusDescription());
                    worksheet1.addCell(label);
                    row++;
                }
            }

            return row;

        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeInDataSheetForReassociation", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeInDataSheetForReassociation", " Exception e: " + e.getMessage());
            throw e;
        }
    }

    /**
     * readReassociationExcel
     * 
     * @param p_excelID
     * @param p_fileName
     * @param p_readLastSheet
     * @param p_leftHeaderLinesForEachSheet
     * @param p_map
     * @return array
     * @throws Exception
     */
    public String[][] readReassociationExcel(String p_excelID, String p_filePath, boolean p_readLastSheet, int p_leftHeaderLinesForEachSheet, HashMap<String, String> p_map) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("readReassociationExcel", " p_excelID: " + p_excelID + " p_filePath: " + p_filePath + " p_readLastSheet=" + p_readLastSheet, " p_leftHeaderLinesForEachSheet=" + p_leftHeaderLinesForEachSheet);
        }
        final String METHOD_NAME = "readReassociationExcel";
        String[][] strArr = null;
        Workbook workbook = null;
        // Sheet sheet=null;
        int arrRow = p_leftHeaderLinesForEachSheet;
        Sheet excelsheet = null;
        int noOfSheet = 0;
        int noOfRows = 0;
        int noOfcols = 0;

        try {
            workbook = Workbook.getWorkbook(new File(p_filePath));
            noOfSheet = workbook.getNumberOfSheets();
            if (!p_readLastSheet) {
                noOfSheet = noOfSheet - 1;
            }
            // Total number of rows in the excel sheet
            for (int i = 0; i < noOfSheet; i++) {
                excelsheet = workbook.getSheet(i);
                noOfRows = noOfRows + (excelsheet.getRows() - p_leftHeaderLinesForEachSheet);
                noOfcols = excelsheet.getColumns();
            }

            // Initialization of string array
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
                        // strArr[0][indexMapArray[col]] = key;
                        strArr[k][indexMapArray[col]] = key;
                    }
                }
                for (int row = p_leftHeaderLinesForEachSheet; row < noOfRows; row++) {
                    p_map.put(Integer.toString(arrRow + 1), excelsheet.getName() + PretupsI.ERROR_LINE + (row + 1));
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
            _log.error("readReassociationExcel", " Exception e: " + e.getMessage());
            throw new BTSLBaseException(e);
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
                _log.debug("readReassociationExcel", " Exiting strArr: " + strArr);
            }
        }
    }
}

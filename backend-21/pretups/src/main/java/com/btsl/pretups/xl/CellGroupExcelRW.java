/*
 * @# CellGroupExcelRW.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Rajdeep Deb September, 2009 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2006 Bharti Telesoft Ltd.
 * This class use for read write in xls file for cell id management.
 */
package com.btsl.pretups.xl;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;

import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.util.MessageResources;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cellidmgt.businesslogic.CellIdVO;
import com.btsl.pretups.common.PretupsI;
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
import java.util.stream.Collectors;
public class CellGroupExcelRW {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private WritableCellFeatures cellFeatures = new WritableCellFeatures();
    private WritableFont times10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false);
    private WritableCellFormat times10format = new WritableCellFormat(times10font);
    private WritableFont times10fontN = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false);
    private WritableCellFormat times10formatN = new WritableCellFormat(times10font);
    private MessageResources p_messages = null;
    private Locale p_locale = null;
   
    public void writeExcel(String p_excelID, HashMap p_hashMap, MessageResources messages, Locale locale, String p_fileName)  {
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append(" p_excelID: ");
        	loggerValue.append(p_excelID);
        	loggerValue.append(" p_hashMap:");
        	loggerValue.append(p_hashMap);
        	loggerValue.append(" p_locale: ");
        	loggerValue.append(locale);
        	loggerValue.append(" p_fileName: ");
        	loggerValue.append(p_fileName);
            _log.debug("writeExcel", loggerValue);
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
            /*
             * keyName =
             * p_messages.getMessage(p_locale,"cellgroup.upload.mastersheet.heading"
             * );
             * label = new Label(col,row,keyName, times12format);
             * worksheet2.mergeCells(col,row,col+5,row);
             * worksheet2.addCell(label);
             * row++;
             * row++;
             * col=0;
             */
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

    private int writeCellGroups(WritableSheet worksheet2, int col, int row, HashMap hashMap) {
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append(" hashMap size=");
        	loggerValue.append(hashMap.size());
        	loggerValue.append(" locale: ");
        	loggerValue.append(p_locale);
        	loggerValue.append(" col=" );
        	loggerValue.append(col);
        	loggerValue.append(" row=" );
        	loggerValue.append(row);
        	
            _log.debug("writeCellGroups", loggerValue);
        }
        final String METHOD_NAME = "writeCellGroups";
        try {

            String keyName = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.CELL_GROUP_ID,null);
            Label label = new Label(col, row, keyName, times10format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.CELL_GROUP_NAME_HEADER,null);
            label = new Label(++col, row, keyName, times10format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            ArrayList list = (ArrayList) hashMap.get(PretupsI.CELL_GROUP_LIST);
            CellIdVO cellVO = null;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    cellVO = (CellIdVO) list.get(i);
                    label = new Label(col, row, cellVO.getGroupId());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, cellVO.getGroupName());
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
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append(" p_hashMap size=");
        	loggerValue.append(p_hashMap.size());
        	loggerValue.append(" p_locale: ");
        	loggerValue.append(p_locale);
        	loggerValue.append(" col=");
        	loggerValue.append(col);
        	loggerValue.append(" row=");
        	loggerValue.append(row);        	
            _log.debug("writeInDataSheet", loggerValue);
        }
        final String METHOD_NAME = "writeInDataSheet";
        try {

            String keyName = p_messages.getMessage(p_locale, "cellgroup.upload.datasheet.cellgroupcode");
            Label label = new Label(col++, row, keyName, times10format);
            // add comment
            String comment = p_messages.getMessage(p_locale, "cellgroup.upload.datasheet.groupid.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "cellgroup.upload.datasheet.siteid");
            label = new Label(col++, row, keyName, times10format);
            // add comment
            comment = p_messages.getMessage(p_locale, "cellgroup.upload.datasheet.siteid.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "cellgroup.upload.datasheet.cellid");
            label = new Label(col++, row, keyName, times10format);
            // add comment
            comment = p_messages.getMessage(p_locale, "cellgroup.upload.datasheet.cellid.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "cellgroup.upload.datasheet.sitename");
            label = new Label(col++, row, keyName, times10format);
            // add comment
            comment = p_messages.getMessage(p_locale, "cellgroup.upload.datasheet.sitename.comment");
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

    public void writeExcelForReassociate(String excelID, HashMap hashMap, Locale locale, String fileName)  {
        if (_log.isDebugEnabled()) {
            _log.debug("writeExcelForReassociate", " excelID: " + excelID + " hashMap:" + hashMap + " locale: " + locale + " fileName: " + fileName);
        }
        final String METHOD_NAME = "writeExcelForReassociate";
        WritableWorkbook workbook = null;
        WritableSheet worksheet1 = null;
        WritableSheet worksheet2 = null;
        int col = 0;
        int row = 0;
        try {
            workbook = Workbook.createWorkbook(new File(fileName));
            worksheet1 = workbook.createSheet("Template Sheet", 0);
            worksheet2 = workbook.createSheet("Master Sheet", 1);
            p_locale = locale;

            row = this.writeCellGroups(worksheet2, col, row, hashMap);
            col = 0;
            row = 0;
            this.writeInDataSheetForReassociation(worksheet1, col, row, hashMap);
            workbook.write();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeExcelForReassociate", " Exception e: " + e.getMessage());
            
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
     * @param hashMap
     * @throws Exception
     */
    private int writeInDataSheetForReassociation(WritableSheet worksheet1, int col, int row, HashMap hashMap) {
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append(" p_hashMap size=");
        	loggerValue.append(hashMap.size());
        	loggerValue.append(" p_locale: ");
        	loggerValue.append(p_locale);
        	loggerValue.append(" col=");
        	loggerValue.append(col);
        	loggerValue.append(" row=");
        	loggerValue.append(row);
            _log.debug("writeInDataSheetForReassociation", loggerValue);
        }
        final String METHOD_NAME = "writeInDataSheetForReassociation";
        try {

            String keyName = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.CELL_ID,null);
            Label label = new Label(col++, row, keyName, times10format);
            // add comment
            String comment = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.CELL_ID_COMMENT,null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.ID_NAME,null);
            label = new Label(col++, row, keyName, times10format);
            // add comment
            comment = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.ID_COMMENT,null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.SITE_NAME,null);
            label = new Label(col++, row, keyName, times10format);
            // add comment
            comment = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.SITE_COMMENT,null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.ASSOCIATED_CELL_ID,null);
            label = new Label(col++, row, keyName, times10format);
            // add comment
            comment = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.ASSOCIATED_CELL_IN_COMMENT,null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.CELL_ID_STATUS,null);
            label = new Label(col++, row, keyName, times10format);
            // add comment
            comment = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.CELL_ID_STATUS_COMMENT,null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.CELL_ID_TO_BE_ASSOCIATED,null);
            label = new Label(col++, row, keyName, times10format);
            // add comment
            comment = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.CELL_ID_TO_BE_ASSOCIATED_COMMENT,null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.CELL_ID_ACTION,null);
            label = new Label(col++, row, keyName, times10format);
            // add comment
            comment = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.CELL_ID_ACTION_COMMENT,null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            ArrayList list = (ArrayList) hashMap.get(PretupsI.CELL_ID_VO_LIST);
            CellIdVO cellVO = null;
            if (list != null) {
                row++;
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    cellVO = (CellIdVO) list.get(i);
                    label = new Label(col, row, cellVO.getCellId());
                    worksheet1.addCell(label);
                    label = new Label(++col, row, cellVO.getSiteId());
                    worksheet1.addCell(label);
                    label = new Label(++col, row, cellVO.getSiteName());
                    worksheet1.addCell(label);
                    label = new Label(++col, row, cellVO.getGroupId());
                    worksheet1.addCell(label);
                    if (cellVO.getStatus().equalsIgnoreCase("Y")) {
                        cellVO.setStatusDescription(PretupsI.CELL_ID_STATUS_ACTIVE);
                    } else if (cellVO.getStatus().equalsIgnoreCase(PretupsI.CELL_ID_STATUS_S)) {
                        cellVO.setStatusDescription(PretupsI.CELL_ID_STATUS_SUSPEND);
                    }
                    label = new Label(++col, row, cellVO.getStatusDescription());
                    worksheet1.addCell(label);
                    row++;
                }
            }


            return row;

        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeInDataSheetForReassociation", " Exception e: " + e.getMessage());
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeInDataSheetForReassociation", " Exception e: " + e.getMessage());
        }
        return row;
    }

    private int writeInDataSheetForReassociationErrorFile(WritableSheet worksheet1, int col, int row, HashMap hashMap) {
        if (_log.isDebugEnabled()) {
            StringBuilder loggerValue= new StringBuilder();
            loggerValue.setLength(0);
            loggerValue.append(" p_hashMap size=");
            loggerValue.append(hashMap.size());
            loggerValue.append(" p_locale: ");
            loggerValue.append(p_locale);
            loggerValue.append(" col=");
            loggerValue.append(col);
            loggerValue.append(" row=");
            loggerValue.append(row);
            _log.debug("writeInDataSheetForReassociation", loggerValue);
        }
        final String METHOD_NAME = "writeInDataSheetForReassociation";
        try {

            String keyName = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.CELL_ID,null);
            Label label = new Label(col++, row, keyName, times10format);
            // add comment
            String comment = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.CELL_ID_COMMENT,null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.ID_NAME,null);
            label = new Label(col++, row, keyName, times10format);

            comment = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.ID_COMMENT,null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.SITE_NAME,null);
            label = new Label(col++, row, keyName, times10format);

            comment = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.SITE_COMMENT,null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.ASSOCIATED_CELL_ID,null);
            label = new Label(col++, row, keyName, times10format);

            comment = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.ASSOCIATED_CELL_IN_COMMENT,null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.CELL_ID_STATUS,null);
            label = new Label(col++, row, keyName, times10format);

            comment = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.CELL_ID_STATUS_COMMENT,null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.CELL_ID_TO_BE_ASSOCIATED,null);
            label = new Label(col++, row, keyName, times10format);
            // add comment
            comment = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.CELL_ID_TO_BE_ASSOCIATED_COMMENT,null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.CELL_ID_ACTION,null);
            label = new Label(col++, row, keyName, times10format);

            comment = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.CELL_ID_ACTION_COMMENT,null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            //Error Lable
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.ERROR_MESSAGE_LABEL, null);
            label = new Label(col++, row, keyName, times10format);
            cellFeatures = new WritableCellFeatures();
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            List<ArrayList<String>> convertedList = (List<ArrayList<String>>) hashMap.get(ExcelFileIDI.CELL_ID_UPLOAD);
            for (ArrayList<String> innerList : convertedList) {
                if(innerList == null)
                    continue;
                row++;col=0;
                for (int i=1;i<innerList.size();i++) {
                    label = new Label(col++, row, innerList.get(i));
                    worksheet1.addCell(label);
                }
            }

            return row;

        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeInDataSheetForReassociation", " Exception e: " + e.getMessage());
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeInDataSheetForReassociation", " Exception e: " + e.getMessage());
        }
        return row;
    }

    /**
     * readReassociationExcel
     * 
     * @param p_excelID
     * @param p_fileName
     * @return array
     * @throws Exception
     */
    public String[][] readReassociationExcel(String p_excelID, String p_filePath) {
        final String METHOD_NAME = "readReassociationExcel";
        String[][] array = null;
        Workbook workbook = null;
        Sheet sheet = null;
        int noOfSheet = 0;
        try {
            workbook = Workbook.getWorkbook(new File(p_filePath));
            noOfSheet = workbook.getNumberOfSheets();
            sheet = workbook.getSheet(0);

            int noOfRows = sheet.getRows();
            int noOfcols = sheet.getColumns();
            array = new String[noOfRows][noOfcols];
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
                array[0][indexMapArray[col]] = key;

            }
            for (int row = 1; row < noOfRows; row++) {
                for (int col = 0; col < noOfcols; col++) {
                    cell = sheet.getCell(col, row);
                    content = cell.getContents();
                    content = content.replaceAll("\n", " ");
                    content = content.replaceAll("\r", " ");
                    array[row][indexMapArray[col]] = content;
                }
            }

            return array;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("readReassociationExcel", " Exception e: " + e.getMessage());
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            workbook = null;
            sheet = null;
            if (_log.isDebugEnabled()) {
                _log.debug("readReassociationExcel", " Exiting strArr: " + array);
            }
        }
        return array;
    }


    public void writeExcelXLS(String excelID, HashMap hashMap, Locale locale, String fileName) {
        if (_log.isDebugEnabled()) {
            StringBuilder loggerValue = new StringBuilder();
            loggerValue.setLength(0);
            loggerValue.append(" excelID: ");
            loggerValue.append(excelID);
            loggerValue.append(" hashMap:");
            loggerValue.append(hashMap);
            loggerValue.append(" locale: ");
            loggerValue.append(locale);
            loggerValue.append(" fileName: ");
            loggerValue.append(fileName);
            _log.debug("writeExcelXLS", loggerValue);
        }
        final String METHOD_NAME = "writeExcelXLS";
        WritableWorkbook workbook = null;
        WritableSheet worksheet1 = null;
        WritableSheet worksheet2 = null;
        int col = 0;
        int row = 0;

        try {
            workbook = Workbook.createWorkbook(new File(fileName));
            worksheet1 = workbook.createSheet("Template Sheet", 0);
            worksheet2 = workbook.createSheet("Master Sheet", 1);
            Label label = null;
            String keyName = null;

            row = this.writeCellGroupsXLS(worksheet2, col, row, hashMap, locale);
            col = 0;
            row = 0;
            this.writeInDataSheetXLS(worksheet1, col, row, hashMap, locale);
            workbook.write();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME, " Exception e: " + e.getMessage());

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
                _log.debug(METHOD_NAME, " Exiting");
            }
        }
    }

    private int writeCellGroupsXLS(WritableSheet worksheet2, int col, int row, HashMap hashMap, Locale locale) {
        if (_log.isDebugEnabled()) {
            StringBuilder loggerValue = new StringBuilder();
            loggerValue.setLength(0);
            loggerValue.append(" hashMap size=");
            loggerValue.append(hashMap.size());
            loggerValue.append(" locale: ");
            loggerValue.append(locale);
            loggerValue.append(" col=");
            loggerValue.append(col);
            loggerValue.append(" row=");
            loggerValue.append(row);

            _log.debug("writeCellGroupsXLS", loggerValue);
        }
        final String METHOD_NAME = "writeCellGroupsXLS";
        try {


            String keyName = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_GROUP_ID, null);
            Label label = new Label(col, row, keyName, times10format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_GROUP_NAME_HEADER, null);
            label = new Label(++col, row, keyName, times10format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            ArrayList list = (ArrayList) hashMap.get(PretupsI.CELL_GROUP_LIST);
            CellIdVO cellVO = null;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    cellVO = (CellIdVO) list.get(i);
                    label = new Label(col, row, cellVO.getGroupId());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, cellVO.getGroupName());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME, " Exception e: " + e.getMessage());
        }
        return row;
    }

    private void writeInDataSheetXLS(WritableSheet worksheet1, int col, int row, HashMap hashMap, Locale locale) {
        if (_log.isDebugEnabled()) {
            StringBuilder loggerValue = new StringBuilder();
            loggerValue.setLength(0);
            loggerValue.append(" hashMap size=");
            loggerValue.append(hashMap.size());
            loggerValue.append(" locale: ");
            loggerValue.append(locale);
            loggerValue.append(" col=");
            loggerValue.append(col);
            loggerValue.append(" row=");
            loggerValue.append(row);
            _log.debug("writeInDataSheetXLS", loggerValue);
        }
        final String METHOD_NAME = "writeInDataSheetXLS";
        try {

            String keyName = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_GROUP_ID_MAND, null);
            Label label = new Label(col++, row, keyName, times10format);
            // add comment
            String comment = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_GROUP_ID_COMMENT, null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);



            keyName = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_ID, null);
            label = new Label(col++, row, keyName, times10format);
            // add comment
            comment = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_ID_COMMENT, null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ID_NAME, null);
            label = new Label(col++, row, keyName, times10format);
            // add comment
            comment = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ID_COMMENT, null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SITE_NAME, null);
            label = new Label(col++, row, keyName, times10format);
            // add comment
            comment = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SITE_COMMENT, null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME, " Exception e: " + e.getMessage());
        }
    }

    public void writeExcelXLSErrorFile(String excelID, HashMap hashMap, Locale locale, String fileName) {
        if (_log.isDebugEnabled()) {
            StringBuilder loggerValue = new StringBuilder();
            loggerValue.setLength(0);
            loggerValue.append(" excelID: ");
            loggerValue.append(excelID);
            loggerValue.append(" hashMap:");
            loggerValue.append(hashMap);
            loggerValue.append(" locale: ");
            loggerValue.append(locale);
            loggerValue.append(" fileName: ");
            loggerValue.append(fileName);
            _log.debug("writeExcelXLSErrorFile", loggerValue);
        }
        final String METHOD_NAME = "writeExcelXLSErrorFile";
        WritableWorkbook workbook = null;
        WritableSheet worksheet1 = null;
        WritableSheet worksheet2 = null;
        int col = 0;
        int row = 0;

        try {
            workbook = Workbook.createWorkbook(new File(fileName));
            worksheet1 = workbook.createSheet("Template Sheet", 0);
            worksheet2 = workbook.createSheet("Master Sheet", 1);
            Label label = null;
            String keyName = null;

            row = this.writeCellGroupsXLS(worksheet2, col, row, hashMap, locale);
            col = 0;
            row = 0;
            this.writeInDataSheetXLSErrorFile(worksheet1, col, row, hashMap, locale);
            workbook.write();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME, " Exception e: " + e.getMessage());

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
                _log.debug(METHOD_NAME, " Exiting");
            }
        }
    }



    private void writeInDataSheetXLSErrorFile(WritableSheet worksheet1, int col, int row, HashMap hashMap, Locale locale) {
        if (_log.isDebugEnabled()) {
            StringBuilder loggerValue = new StringBuilder();
            loggerValue.setLength(0);
            loggerValue.append(" hashMap size=");
            loggerValue.append(hashMap.size());
            loggerValue.append(" locale: ");
            loggerValue.append(locale);
            loggerValue.append(" col=");
            loggerValue.append(col);
            loggerValue.append(" row=");
            loggerValue.append(row);
            _log.debug("writeInDataSheetXLSErrorFile", loggerValue);
        }
        final String METHOD_NAME = "writeInDataSheetXLSErrorFile";

        try {

            String keyName = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_GROUP_ID_MAND, null);
            Label label = new Label(col++, row, keyName, times10format);
            // add comment
            String comment = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_GROUP_ID_COMMENT, null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);



            keyName = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_ID, null);
            label = new Label(col++, row, keyName, times10format);
            // add comment
            comment = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CELL_ID_COMMENT, null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ID_NAME, null);
            label = new Label(col++, row, keyName, times10format);
            // add comment
            comment = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ID_COMMENT, null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SITE_NAME, null);
            label = new Label(col++, row, keyName, times10format);
            // add comment
            comment = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SITE_COMMENT, null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            //Error Lable
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.ERROR_MESSAGE_LABEL, null);
            label = new Label(col++, row, keyName, times10format);
            cellFeatures = new WritableCellFeatures();
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            List<ArrayList<String>> convertedList = (List<ArrayList<String>>) hashMap.get(ExcelFileIDI.CELL_ID_UPLOAD);
            for (ArrayList<String> innerList : convertedList) {
                if(innerList == null)
                    continue;
                row++;col=0;
                for (int i=1;i<innerList.size();i++) {
                    label = new Label(col++, row, innerList.get(i));
                    worksheet1.addCell(label);
                }
            }
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME, " Exception e: " + e.getMessage());
        }
    }
    public void writeExcelForReassociateErrorFile(String excelID, HashMap hashMap, Locale locale, String fileName)  {
        if (_log.isDebugEnabled()) {
            _log.debug("writeExcelForReassociateErrorFile", " excelID: " + excelID + " hashMap:" + hashMap + " locale: " + locale + " fileName: " + fileName);
        }
        final String METHOD_NAME = "writeExcelForReassociateErrorFile";
        WritableWorkbook workbook = null;
        WritableSheet worksheet1 = null;
        WritableSheet worksheet2 = null;
        int col = 0;
        int row = 0;
        try {
            workbook = Workbook.createWorkbook(new File(fileName));
            worksheet1 = workbook.createSheet("Template Sheet", 0);
            worksheet2 = workbook.createSheet("Master Sheet", 1);
            p_locale = locale;
            row = this.writeCellGroupsErrorFile(worksheet2, col, row, hashMap);
            col = 0;
            row = 0;
            this.writeInDataSheetForReassociationErrorFile(worksheet1, col, row, hashMap);
            workbook.write();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME, " Exception e: " + e.getMessage());

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
                _log.debug(METHOD_NAME, " Exiting");
            }
        }
    }
    private int writeCellGroupsErrorFile(WritableSheet worksheet2, int col, int row, HashMap hashMap) {
        if (_log.isDebugEnabled()) {
            StringBuilder loggerValue= new StringBuilder();
            loggerValue.setLength(0);
            loggerValue.append(" hashMap size=");
            loggerValue.append(hashMap.size());
            loggerValue.append(" locale: ");
            loggerValue.append(p_locale);
            loggerValue.append(" col=" );
            loggerValue.append(col);
            loggerValue.append(" row=" );
            loggerValue.append(row);

            _log.debug("writeCellGroupsErrorFile", loggerValue);
        }
        final String METHOD_NAME = "writeCellGroupsErrorFile";
        try {

            String keyName = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.CELL_GROUP_ID,null);
            Label label = new Label(col, row, keyName, times10format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.CELL_GROUP_NAME_HEADER,null);
            label = new Label(++col, row, keyName, times10format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            ArrayList list = (ArrayList) hashMap.get(PretupsI.CELL_GROUP_LIST);
            CellIdVO cellVO = null;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    cellVO = (CellIdVO) list.get(i);
                    label = new Label(col, row, cellVO.getGroupId());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, cellVO.getGroupName());
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

}

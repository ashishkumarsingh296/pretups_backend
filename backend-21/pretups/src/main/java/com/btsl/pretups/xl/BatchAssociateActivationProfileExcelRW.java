package com.btsl.pretups.xl;

/*
 * @# BatchAssociateActivationProfileExcelRW.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Amit Singh March 23, 2009 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2009 Bharti Telesoft Ltd.
 * This class use for read write in xls file for batch user association.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.util.MessageResources;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.ProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.RetSubsMappingVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.xl.ExcelFileConstants;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class BatchAssociateActivationProfileExcelRW {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private WritableFont times16font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, true);
    private WritableCellFormat times16format = new WritableCellFormat(times16font);
    private MessageResources p_messages = null;
    private Locale p_locale = null;
   


    /**
     * writeMasterData method writes the data in the master sheet.
     * 
     * @param worksheet1
     *            WritableSheet
     * @param col
     *            int
     * @param row
     *            int
     * @param p_hashMap
     *            HashMap
     * @return int
     * @throws Exception
     */
    private int writeMasterData(WritableSheet worksheet1, int col, int row, HashMap p_hashMap) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeMasterData", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeMasterData";
        try {
            String keyName = p_messages.getMessage(p_locale, "associate.batch.activation.profile.datasheet.label.setid");
            Label label = new Label(col, row, keyName, times16format);
            worksheet1.addCell(label);
            keyName = p_messages.getMessage(p_locale, "associate.batch.activation.profile.datasheet.label.setname");
            label = new Label(++col, row, keyName, times16format);
            // worksheet1.mergeCells(col,row,++col,row);
            worksheet1.addCell(label);
            keyName = p_messages.getMessage(p_locale, "associate.batch.activation.profile.datasheet.label.shortcode");
            label = new Label(++col, row, keyName, times16format);
            // worksheet1.mergeCells(col,row,++col,row);
            worksheet1.addCell(label);
            keyName = p_messages.getMessage(p_locale, "associate.batch.activation.profile.datasheet.label.isdefault");
            label = new Label(++col, row, keyName, times16format);
            worksheet1.addCell(label);
            keyName = p_messages.getMessage(p_locale, "associate.batch.activation.profile.datasheet.label.category");
            label = new Label(++col, row, keyName, times16format);
            worksheet1.addCell(label);
            row++;
            col = 0;
            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_ASSOCIATE_PROFILE_LIST);
            ProfileSetVO profileSetVO = null;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    profileSetVO = (ProfileSetVO) list.get(i);
                    label = new Label(col, row, profileSetVO.getSetId().split(":")[0]);
                    worksheet1.addCell(label);
                    label = new Label(++col, row, profileSetVO.getSetName());
                    // worksheet1.mergeCells(col,row,++col,row);
                    worksheet1.addCell(label);
                    label = new Label(++col, row, profileSetVO.getShortCode());
                    // worksheet1.mergeCells(col,row,++col,row);
                    worksheet1.addCell(label);
                    label = new Label(++col, row, profileSetVO.getSetId().split(":")[1]);
                    worksheet1.addCell(label);
                    label = new Label(++col, row, profileSetVO.getSetId().split(":")[2]);
                    worksheet1.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeMasterData", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeMasterData", " Exception e: " + e.getMessage());
            throw e;
        }
    }



    /**
     * This method reads the data from the excel file we are uploading.
     * 
     * @param p_excelID
     *            String
     * @param p_fileName
     *            String
     * @return String[][]
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
            if (noOfSheet > 1) {
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



    /**
     * writeDataInExcel method writes the data in the data sheet.
     * 
     * @param worksheet1
     *            WritableSheet
     * @param col
     *            int
     * @param row
     *            int
     * @param p_list
     *            ArrayList
     * @param p_map
     *            HashMap
     * @return int
     * @throws Exception
     */
    private int writeDataInExcel(WritableSheet worksheet1, int col, int row, ArrayList p_list, HashMap p_map) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeDataInExcel", " p_list size=" + p_list.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeDataInExcel";
        try {
            String keyName = p_messages.getMessage(p_locale, "associate.batch.activation.profile.xls.heading");
            Label label = new Label(col, row, keyName, times16format);
            worksheet1.addCell(label);

            ++row;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "associate.batch.activation.profile.label.domain");
            label = new Label(col, row, keyName, times16format);
            worksheet1.addCell(label);
            label = new Label(++col, row, (String) p_map.get(PretupsI.BATCH_ASSOCIATE_PROFILE_DOMAIN));
            worksheet1.mergeCells(col, row, ++col, row);
            worksheet1.addCell(label);

            ++row;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "associate.batch.activation.profile.label.category");
            label = new Label(col, row, keyName, times16format);
            worksheet1.addCell(label);
            label = new Label(++col, row, (String) p_map.get(PretupsI.BATCH_ASSOCIATE_PROFILE_CATEGORY));
            worksheet1.mergeCells(col, row, ++col, row);
            worksheet1.addCell(label);

            ++row;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "associate.batch.activation.profile.datasheet.label.login");
            label = new Label(col, row, keyName, times16format);
            worksheet1.addCell(label);
            keyName = p_messages.getMessage(p_locale, "associate.batch.activation.profile.datasheet.label.msisdn");
            label = new Label(++col, row, keyName, times16format);
            worksheet1.addCell(label);
            keyName = p_messages.getMessage(p_locale, "associate.batch.activation.profile.datasheet.label.setid");
            label = new Label(++col, row, keyName, times16format);
            worksheet1.addCell(label);
            keyName = p_messages.getMessage(p_locale, "associate.batch.activation.profile.datasheet.label.category");
            label = new Label(++col, row, keyName, times16format);
            // worksheet1.mergeCells(col,row,++col,row);
            worksheet1.addCell(label);

            row++;
            col = 0;
            RetSubsMappingVO retSubsMappingVO = null;
            if (p_list != null) {
                for (int i = 0, j = p_list.size(); i < j; i++) {
                    col = 0;
                    retSubsMappingVO = (RetSubsMappingVO) p_list.get(i);
                    label = new Label(col, row, retSubsMappingVO.getLoginId());
                    worksheet1.addCell(label);
                    label = new Label(++col, row, retSubsMappingVO.getRetailerMsisdn());
                    worksheet1.addCell(label);
                    label = new Label(++col, row, retSubsMappingVO.getSetID());
                    worksheet1.addCell(label);
                    label = new Label(++col, row, retSubsMappingVO.getCategoryCode());
                    // worksheet1.mergeCells(col,row,++col,row);
                    worksheet1.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeDataInExcel", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeDataInExcel", " Exception e: " + e.getMessage());
            throw e;
        }
    }
}

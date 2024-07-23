package com.btsl.xl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.SheetSettings;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Alignment;
import jxl.write.Label;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import com.btsl.util.MessageResources;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.c2s.services.C2SRechargeReversalDetails;

/*
 * ExcelRW.java
 * 
 * 
 * 
 * Name Date History
 * ------------------------------------------------------------------------
 * Abhijit 28/06/2006 Initial Creation
 * 
 * ------------------------------------------------------------------------
 * Copyright (c) 2006 Bharti Telesoft Ltd.
 */
public class ExcelRW {

    private static Log _log = LogFactory.getLog(ExcelRW.class.getName());
    private  final int COLUMN_MARGE = 10;
    private WritableFont times16font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, true);
    private WritableCellFormat times16format = new WritableCellFormat(times16font);
    private MessageResources p_messages = null;
    private Locale p_locale = null;
    private WritableCellFeatures cellFeatures = new WritableCellFeatures();
    private WritableFont times12font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, true);
    private WritableCellFormat times12format = new WritableCellFormat(times12font);
    private WritableFont times20font = new WritableFont(WritableFont.ARIAL, 20, WritableFont.BOLD, true);
    private WritableCellFormat times20format = new WritableCellFormat (times20font);
    private WritableFont data = new WritableFont(WritableFont.ARIAL, 11, WritableFont.NO_BOLD, true);
    private WritableCellFormat dataFormat = new WritableCellFormat (data);
    

    /**
     * @param p_excelID
     * @param p_strArr
     * @param p_headerArray
     * @param p_heading
     * @param p_margeCont
     * @param p_messages
     * @param p_locale
     * @param p_fileName
     * @throws WriteException 
     * @throws RowsExceededException 
     * @throws IOException 
     * @throws Exception
     */

    public void writeExcel(String p_excelID, String[][] p_strArr, String[][] p_headerArray, String p_heading, int p_margeCont, MessageResources p_messages, Locale p_locale, String p_fileName) throws RowsExceededException, WriteException, IOException {
        if (_log.isDebugEnabled()) {
            _log.debug("writeExcel", " p_excelID: " + p_excelID + " p_strArr:" + p_strArr.toString() + " p_headerArray=" + p_headerArray + " p_heading=" + p_heading + " p_margeCont=" + p_margeCont + " p_locale: " + p_locale + " p_strArr length: " + p_strArr.length + " p_fileName: " + p_fileName);
        }
        final String METHOD_NAME = "writeExcel";
        WritableWorkbook workbook = null;
        WritableSheet worksheet = null;
        try {
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;

            String fileName = p_fileName;
            workbook = Workbook.createWorkbook(new File(fileName));
            worksheet = workbook.createSheet("First Sheet", 0);
            String key = null;
            String keyName = null;
            Label label = null;

            WritableFont times16font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, true);
            WritableCellFormat times16format = new WritableCellFormat(times16font);
            // times16format.setBackground(Colour.BROWN);
            // times16format.setWrap(true);
            // times16format.setAlignment(Alignment.JUSTIFY);

            WritableFont times12font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, true);
            WritableCellFormat times12format = new WritableCellFormat(times12font);
            // times12format.setWrap(true);
            // times12format.setAlignment(Alignment.JUSTIFY);

            WritableFont data = new WritableFont(WritableFont.ARIAL, 11, WritableFont.NO_BOLD, true);
            WritableCellFormat dataFormat = new WritableCellFormat(data);
            // dataFormat.setWrap(true);

            WritableFont times20font = new WritableFont(WritableFont.ARIAL, 20, WritableFont.BOLD, true);
            WritableCellFormat times20format = new WritableCellFormat(times20font);
            // times20format.setBackground(Colour.AQUA);
            times20format.setAlignment(Alignment.CENTRE);

            int colnum = p_strArr[0].length;
            int[] indexMapArray = new int[colnum];

            if (p_messages != null) {
                keyName = p_messages.getMessage(p_locale, p_heading);
            } else {
                keyName = p_heading;
            }

            label = new Label(0, 0, keyName, times20format);// Haeding
            worksheet.mergeCells(0, 0, colnum - 1, 0);
            worksheet.addCell(label);

            int cols = p_headerArray[0].length;
            for (int row = 0; row < cols; row++)// Header Headings
            {
                key = p_headerArray[0][row];
                keyName = null;
                if (p_messages != null) {
                    keyName = p_messages.getMessage(p_locale, key);
                } else {
                    keyName = key;
                }
                label = new Label(0, row + 1, keyName, times16format);
                worksheet.mergeCells(0, row + 1, p_margeCont, row + 1);
                worksheet.addCell(label);
            }
            for (int row = 0; row < cols; row++)// Header Headings value
            {
                keyName = null;
                keyName = p_headerArray[1][row];
                label = new Label(p_margeCont + 1, row + 1, keyName, dataFormat);
                worksheet.mergeCells(p_margeCont + 1, row + 1, 3 * p_margeCont, row + 1);
                worksheet.addCell(label);
            }
            int length = p_strArr[0].length;
            String indexStr = null;
            for (int col = 0; col < length; col++)// column heading
            {
                indexStr = null;
                key = p_strArr[0][col];
                keyName = null;
                if (p_messages != null) {
                    keyName = p_messages.getMessage(p_locale, key);
                } else {
                    keyName = key;
                }
                indexStr = ExcelFileConstants.getWriteProperty(p_excelID, String.valueOf(col));
                if (indexStr == null) {
                    indexStr = String.valueOf(col);
                }
                indexMapArray[col] = Integer.parseInt(indexStr);
                label = new Label(indexMapArray[col], cols + 2, keyName, times16format);
                worksheet.addCell(label);
            }
            // setting for the vertical freeze panes
            SheetSettings sheetSetting = new SheetSettings();
            sheetSetting = worksheet.getSettings();
            sheetSetting.setVerticalFreeze(cols + 3);

            // setting for the horizontal freeze panes
            SheetSettings sheetSetting1 = new SheetSettings();
            sheetSetting1 = worksheet.getSettings();
            sheetSetting1.setHorizontalFreeze(6);
            int len = p_strArr.length;
            int lenInt = 0;
            for (int row = 1; row < len; row++)// column value
            {
                lenInt = p_strArr[row].length;
                for (int col = 0; col < lenInt; col++) {
                    label = new Label(indexMapArray[col], row + cols + 2, p_strArr[row][col], dataFormat);
                    worksheet.addCell(label);
                }
            }
            workbook.write();
        } catch (WriteException | IOException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeExcel", " Exception e: " + e.getMessage());
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
            if (_log.isDebugEnabled()) {
                _log.debug("writeExcel", " Exiting");
            }
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;

        }
    }

    /**
     * Write Excel
     * 
     * @param p_excelID
     * @param p_strArr
     * @param p_messages
     * @param p_locale
     * @param p_fileName
     * @throws IOException 
     * @throws WriteException 
     * @throws RowsExceededException 
     * @throws Exception
     */
    public void writeExcel(String p_excelID, String[][] p_strArr, MessageResources p_messages, Locale p_locale, String p_fileName) throws IOException, RowsExceededException, WriteException {
        if (_log.isDebugEnabled()) {
            _log.debug("writeExcel", " p_excelID: " + p_excelID + " p_strArr:" + p_strArr + " p_messages: " + p_messages + " p_locale: " + p_locale + " p_strArr length: " + p_strArr.length + " p_fileName: " + p_fileName);
        }
        final String METHOD_NAME = "writeExcel";
        WritableWorkbook workbook = null;
        WritableSheet worksheet = null;
        try {
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;

            String fileName = p_fileName;
            workbook = Workbook.createWorkbook(new File(fileName));
            worksheet = workbook.createSheet("First Sheet", 0);
            String key = null;
            String keyName = null;
            Label label = null;
            WritableFont times16font = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD, true);
            WritableCellFormat times16format = new WritableCellFormat(times16font);
            int[] indexMapArray = new int[p_strArr[0].length];
            String indexStr = null;
            for (int col = 0, len = p_strArr[0].length; col < len; col++) {
                indexStr = null;
                key = p_strArr[0][col];
                keyName = null;
                if (p_messages != null) {
                    keyName = p_messages.getMessage(p_locale, key);
                } else {
                	keyName = RestAPIStringParser.getMessage(
            				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),key, null);
                    //keyName = key;
                }
                if(BTSLUtil.isNullString(keyName)) {
                	keyName = key;
                }
                indexStr = ExcelFileConstants.getWriteProperty(p_excelID, String.valueOf(col));
                if (indexStr == null) {
                    indexStr = String.valueOf(col);
                }
                indexMapArray[col] = Integer.parseInt(indexStr);
                label = new Label(indexMapArray[col], 0, keyName, times16format);

                if(("BATCH_O2C_INITIATE".equalsIgnoreCase(p_excelID)) && (col==2))
				{
					String commentDate = null;
					commentDate=((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT));
                    WritableCellFeatures cellFeatures0 = new WritableCellFeatures();
                    cellFeatures0.setComment(commentDate);
                    label.setCellFeatures(cellFeatures0);
				}
                if (ExcelFileIDI.BATCH_BAR_FOR_DELETION_SEC.equalsIgnoreCase(p_excelID)) {
					if (!"channeluser.xlsheading.label.comments"
							.equalsIgnoreCase(key)) {
						String commentStr = p_messages.getMessage(p_locale,
								"batch.bar.for.deletion.sec.mandatory.comment");
						cellFeatures = new WritableCellFeatures();
						cellFeatures.setComment(commentStr);
						label.setCellFeatures(cellFeatures);
					}
	
				}
				if (ExcelFileIDI.BATCH_BAR_FOR_DELETION
						.equalsIgnoreCase(p_excelID)) {
					if ("channeluser.xlsheading.label.userid"
							.equalsIgnoreCase(key) || "channeluser.xlsheading.label.msisdn"
							.equalsIgnoreCase(key)) {
						String commentStr = p_messages.getMessage(p_locale,
								"batch.bar.for.deletion.sec.mandatory.comment.approval");
						cellFeatures = new WritableCellFeatures();
						cellFeatures.setComment(commentStr);
						label.setCellFeatures(cellFeatures);
					}
               }
                worksheet.addCell(label);
            }
            // setting for the vertical freeze panes
            SheetSettings sheetSetting = new SheetSettings();
            sheetSetting = worksheet.getSettings();
            sheetSetting.setVerticalFreeze(1);

            // setting for the horizontal freeze panes
            SheetSettings sheetSetting1 = new SheetSettings();
            sheetSetting1 = worksheet.getSettings();
            sheetSetting1.setHorizontalFreeze(6);

            for (int row = 1, len = p_strArr.length; row < len; row++) {
                for (int col = 0, len1 = p_strArr[row].length; col < len1; col++) {
                    label = new Label(indexMapArray[col], row, p_strArr[row][col]);
                    worksheet.addCell(label);
                }
            }

            workbook.write();
        } catch (IOException |  WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeExcel", " Exception e: " + e.getMessage());
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
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;

            if (_log.isDebugEnabled()) {
                _log.debug("writeExcel", " Exiting");
            }
        }
    }

    /**
     * Write Excel
     * 
     * @param p_excelID
     * @param p_strArr
     * @param p_messages
     * @param p_locale
     * @param p_fileName
     * @throws Exception
     */
    public void writeExcelForBC2Cinitiate(String p_excelID, String[][] p_strArr, String downloadType, MessageResources p_messages, Locale p_locale, String p_fileName) throws IOException, RowsExceededException, WriteException {
        if (_log.isDebugEnabled()) {
            _log.debug("writeExcel", " p_excelID: " + p_excelID + " p_strArr:" + p_strArr + " p_messages: " + p_messages + " p_locale: " + p_locale + " p_strArr length: " + p_strArr.length + " p_fileName: " + p_fileName);
        }
        final String METHOD_NAME = "writeExcelForBC2Cinitiate";
        WritableWorkbook workbook = null;
        WritableSheet worksheet = null;
        int totalNoOfRecords = 0, noOfTotalSheet = 0;
        String noOfRowsInOneTemplate;

        try {
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;

            totalNoOfRecords = p_strArr.length - 1;
            noOfRowsInOneTemplate = Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE_BATCHC2C");

            int noOfRowsPerTemplate = 0;
            if (totalNoOfRecords > 0) {
                if (!BTSLUtil.isNullString(noOfRowsInOneTemplate)) {
                    noOfRowsInOneTemplate = noOfRowsInOneTemplate.trim();
                    noOfRowsPerTemplate = Integer.parseInt(noOfRowsInOneTemplate);
                } else {
                    noOfRowsPerTemplate = 65500; // Default value of rows
                }
                noOfTotalSheet = BTSLUtil.parseDoubleToInt( Math.ceil(BTSLUtil.parseIntToDouble(totalNoOfRecords) / noOfRowsPerTemplate));
            } else {
                noOfTotalSheet = 1;
            }
            String fileName = p_fileName;
            workbook = Workbook.createWorkbook(new File(fileName));
            String key = null;
            String keyName = null;
            Label label = null;
            // int len=p_strArr[0].length;
            int recordNo = 0;
            for (int i = 0; i < noOfTotalSheet; i++) {
                worksheet = workbook.createSheet("Sheet" + (i + 1), i);
                WritableFont times16font = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD, true);
                WritableCellFormat times16format = new WritableCellFormat(times16font);
                int[] indexMapArray = new int[p_strArr[0].length];
                String indexStr = null;
                for (int col = 0, len = p_strArr[0].length; col < len; col++) {
                    indexStr = null;
                    key = p_strArr[0][col];
                    keyName = null;
                    if (p_messages != null) {
                        keyName = p_messages.getMessage(p_locale, key);
                    } else {
                        keyName = key;
                    }
                    indexStr = ExcelFileConstants.getWriteProperty(p_excelID, String.valueOf(col));
                    if (indexStr == null) {
                        indexStr = String.valueOf(col);
                    }
                    indexMapArray[col] = Integer.parseInt(indexStr);
                    label = new Label(indexMapArray[col], 0, keyName, times16format);
                    // /
                    if (downloadType != null && downloadType.equals("downloadlist")) {
                        switch (col) {

                        case 0:

                            String commentMsisdn = null;
                            commentMsisdn = p_messages.getMessage(p_locale, "batchc2c.xlsfile.details.msisdn.comment");
                            WritableCellFeatures cellFeatures0 = new WritableCellFeatures();
                            cellFeatures0.setComment(commentMsisdn);
                            label.setCellFeatures(cellFeatures0);
                            break;

                        case 1:
                            String commentLoginID = null;
                            commentLoginID = p_messages.getMessage(p_locale, "batchc2c.xlsfile.details.loginID.comment");
                            WritableCellFeatures cellFeatures1 = new WritableCellFeatures();
                            cellFeatures1.setComment(commentLoginID);
                            label.setCellFeatures(cellFeatures1);
                            break;

                        case 2:
                            String commentUserCat = null;
                            commentUserCat = p_messages.getMessage(p_locale, "batchc2c.xlsfile.details.userCategory.comment");
                            WritableCellFeatures cellFeatures2 = new WritableCellFeatures();
                            cellFeatures2.setComment(commentUserCat);
                            label.setCellFeatures(cellFeatures2);
                            break;

                        case 3:
                            String commentExtCode = null;
                            commentExtCode = p_messages.getMessage(p_locale, "batchc2c.xlsfile.details.externalCode.comment");
                            WritableCellFeatures cellFeatures3 = new WritableCellFeatures();
                            cellFeatures3.setComment(commentExtCode);
                            label.setCellFeatures(cellFeatures3);
                            break;
                        }
                    }

                    else {
                        switch (col) {

                        case 0:

                            String commentMsisdn = null;
                            commentMsisdn = p_messages.getMessage(p_locale, "batchc2c.xlsfile.details.msisdn.comment");
                            WritableCellFeatures cellFeatures0 = new WritableCellFeatures();
                            cellFeatures0.setComment(commentMsisdn);
                            label.setCellFeatures(cellFeatures0);
                            break;

                        case 1:
                            String commentLoginID = null;
                            commentLoginID = p_messages.getMessage(p_locale, "batchc2c.xlsfile.details.loginID.comment");
                            WritableCellFeatures cellFeatures1 = new WritableCellFeatures();
                            cellFeatures1.setComment(commentLoginID);
                            label.setCellFeatures(cellFeatures1);
                            break;

                        case 2:
                            String commentExtCode = null;
                            commentExtCode = p_messages.getMessage(p_locale, "batchc2c.xlsfile.details.externalCode.comment");
                            WritableCellFeatures cellFeatures3 = new WritableCellFeatures();
                            cellFeatures3.setComment(commentExtCode);
                            label.setCellFeatures(cellFeatures3);
                            break;

                        }
                    }
                    // //
                    worksheet.addCell(label);
                }
                // setting for the vertical freeze panes
                SheetSettings sheetSetting = new SheetSettings();
                sheetSetting = worksheet.getSettings();
                sheetSetting.setVerticalFreeze(1);

                // setting for the horizontal freeze panes
                SheetSettings sheetSetting1 = new SheetSettings();
                sheetSetting1 = worksheet.getSettings();
                sheetSetting1.setHorizontalFreeze(6);

                if (noOfTotalSheet > 1) {
                    for (int row = 1; row <= noOfRowsPerTemplate; row++) {
                        recordNo = row + (i * noOfRowsPerTemplate);
                        if (recordNo <= totalNoOfRecords) {
                            int len1 = p_strArr[recordNo].length;
                            for (int col = 0; col < len1; col++) {
                                label = new Label(indexMapArray[col], row, p_strArr[recordNo][col]);
                                worksheet.addCell(label);
                            }
                        } else {
                            break;
                        }
                    }
                } else {
                    for (int row = 1; row <= totalNoOfRecords; row++) {
                        int len1 = p_strArr[row].length;
                        for (int col = 0; col < len1; col++) {
                            label = new Label(indexMapArray[col], row, p_strArr[row][col]);
                            worksheet.addCell(label);
                        }
                    }

                }

            }
            workbook.write();
        } catch (IOException |  WriteException  e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeExcel", " Exception e: " + e.getMessage());
            throw e;
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException |  WriteException  e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            worksheet = null;
            workbook = null;
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            _log.debug("writeExcel", "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));
            if (_log.isDebugEnabled()) {
                _log.debug("writeExcel", " Exiting");
            }
        }
    }

    /**
     * Write Excel
     * 
     * @param p_excelID
     * @param p_strArr
     * @param p_messages
     * @param p_locale
     * @param p_fileName
     * @throws Exception
     */
    public void writeExcelForBC2Capprove(String p_excelID, String[][] p_strArr, MessageResources p_messages, Locale p_locale, String p_fileName) throws IOException, RowsExceededException, WriteException {
        if (_log.isDebugEnabled()) {
            _log.debug("writeExcel", " p_excelID: " + p_excelID + " p_strArr:" + p_strArr + " p_messages: " + p_messages + " p_locale: " + p_locale + " p_strArr length: " + p_strArr.length + " p_fileName: " + p_fileName);
        }
        final String METHOD_NAME = "writeExcelForBC2Capprove";
        WritableWorkbook workbook = null;
        WritableSheet worksheet = null;
        int totalNoOfRecords = 0, noOfTotalSheet = 0;
        String noOfRowsInOneTemplate;
        try {
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            _log.debug("writeExcelForBC2Capprove", "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));
            String fileName = p_fileName;
            workbook = Workbook.createWorkbook(new File(fileName));
            totalNoOfRecords = p_strArr.length - 1;
            noOfRowsInOneTemplate = Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE_BATCHC2C");

            int noOfRowsPerTemplate = 0;
            if (totalNoOfRecords > 0) {
                if (!BTSLUtil.isNullString(noOfRowsInOneTemplate)) {
                    noOfRowsInOneTemplate = noOfRowsInOneTemplate.trim();
                    noOfRowsPerTemplate = Integer.parseInt(noOfRowsInOneTemplate);
                } else {
                    noOfRowsPerTemplate = 65500; // Default value of rows
                }
            } else {
                noOfTotalSheet = 1;
            }
            noOfTotalSheet = BTSLUtil.parseDoubleToInt( Math.ceil(BTSLUtil.parseIntToDouble(totalNoOfRecords) / noOfRowsPerTemplate) );
            String key = null;
            String keyName = null;
            Label label = null;
            int recordNo = 0;
            for (int i = 0; i < noOfTotalSheet; i++) {
                worksheet = workbook.createSheet("Data Sheet" + (i + 1), i);
                WritableFont times16font = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD, true);
                WritableCellFormat times16format = new WritableCellFormat(times16font);
                int[] indexMapArray = new int[p_strArr[0].length];
                String indexStr = null;

                for (int col = 0, len = p_strArr[0].length; col < len; col++) {
                    indexStr = null;
                    key = p_strArr[0][col];
                    keyName = null;
                    if (p_messages != null) {
                        keyName = p_messages.getMessage(p_locale, key);
                    } else {
                    	keyName = PretupsRestUtil.getMessageString(key);
                    }
                    indexStr = ExcelFileConstants.getWriteProperty(p_excelID, String.valueOf(col));
                    if (indexStr == null) {
                        indexStr = String.valueOf(col);
                    }
                    indexMapArray[col] = Integer.parseInt(indexStr);
                    label = new Label(indexMapArray[col], 0, keyName, times16format);
                    worksheet.addCell(label);
                }
                SheetSettings sheetSetting = new SheetSettings();
                sheetSetting = worksheet.getSettings();
                sheetSetting.setVerticalFreeze(1);

                /*
                 * //setting for the horizontal freeze panes
                 * SheetSettings sheetSetting1=new SheetSettings();
                 * sheetSetting1=worksheet.getSettings();
                 * sheetSetting1.setHorizontalFreeze(6);
                 */
                if (noOfTotalSheet > 1) {
                    for (int row = 1; row <= noOfRowsPerTemplate; row++) {
                        recordNo = row + (i * noOfRowsPerTemplate);
                        if (recordNo <= totalNoOfRecords) {
                            int len1 = p_strArr[recordNo].length;
                            for (int col = 0; col < len1; col++) {
                                label = new Label(indexMapArray[col], row, p_strArr[recordNo][col]);
                                worksheet.addCell(label);
                            }
                        } else {
                            break;
                        }
                    }
                } else {
                    for (int row = 1; row <= totalNoOfRecords; row++) {
                        int len1 = p_strArr[row].length;
                        for (int col = 0; col < len1; col++) {
                            label = new Label(indexMapArray[col], row, p_strArr[row][col]);
                            worksheet.addCell(label);
                        }
                    }
                }
            }
            workbook.write();
        } catch (IOException |  WriteException  e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeExcel", " Exception e: " + e.getMessage());
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
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            _log.debug("writeExcelForBC2Capprove", "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));
            if (_log.isDebugEnabled()) {
                _log.debug("writeExcel", " Exiting");
            }
        }
    }

    public static void main(String args[]) throws IOException, BiffException {
        final String METHOD_NAME = "main";
        ExcelRW excel = new ExcelRW();
        // String strArr[][] = new
        // String[][]{{"test.name","test.mobile"},{"cust1","number1"},{"cust2","number2"},{"cust3","number3"},{"cust4",
        // "number4"}};
        String strArr[][] = new String[604][9];
        for (int col = 0; col < 9; col++) {
            strArr[0][col] = "key0" + col;
        }
        for (int row = 1; row < 604; row++) {
            for (int col = 0; col < 9; col++) {
                strArr[row][col] = row + "" + col;
            }
        }
        try {
            ExcelFileConstants.load("c:\\ExcelConfigFile.props");
            // excel.writeExcel(strArr,null,null,"C:\\Sandeep.xls");
            // excel.writeExcel("1",strArr,null,null,"C:\\Sandeep.xls");
            excel.readExcel("1", "C:\\Sandeep.xls");

        } catch (IOException | BiffException e) {
            _log.errorTrace(METHOD_NAME, e);
        }
    }

    /**
     * readExcel
     * 
     * @param p_excelID
     * @param p_fileName
     * @return
     * @throws IOException 
     * @throws BiffException 
     * @throws Exception
     */
    public String[][] readExcel(String p_excelID, String p_fileName) throws BiffException, IOException {
    	final String METHOD_NAME = "readExcel";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " p_excelID: " + p_excelID + " p_fileName: " + p_fileName);
        }
        
        String strArr[][] = null;
        HSSFWorkbook workbook = null;
        HSSFSheet excelsheet = null;
//        Sheet excelsheet = null;
        try {
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            _log.debug(METHOD_NAME, "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));
            String dir = Constants.getProperty("DownloadBatchC2SReversal"); // Upload file path
            String path = dir+File.separator+p_fileName;
            FileInputStream file = new FileInputStream(path);
//            workbook = Workbook.getWorkbook(file);
            workbook = new HSSFWorkbook(file);
            
            excelsheet = workbook.getSheetAt(0);
            int noOfRows = excelsheet.getLastRowNum();
            int noOfcols = excelsheet.getRow(0).getLastCellNum();
            strArr = new String[noOfRows][noOfcols];
//            Cell cell = null;
            String content = null;
            DataFormatter formatter = new DataFormatter();
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
            	int noOfcols1 = excelsheet.getRow(row).getLastCellNum();
                for (int col = 0; col < noOfcols1; col++) {
                    
                    content = formatter.formatCellValue(excelsheet.getRow(row).getCell(col));
                    strArr[row][indexMapArray[col]] = content;
                }
            }
            return strArr;
        } catch (IOException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME, " Exception e: " + e.getMessage());
            throw e;
        } finally {
            try {
            	
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            excelsheet = null;
            workbook = null;
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            _log.debug("readExcel", "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));
            if (_log.isDebugEnabled()) {
                _log.debug("readExcel", " Exiting strArr: " + strArr);
            }
        }
    }

    /**
     * for direct payout
     * Write Excel
     * 
     * @mthod getLengthLongestStringLength
     * @param ArrayListp_array
     * @author lohit.audhkhasi
     */
    private int getLengthLongestStringLength(ArrayList p_array) throws NullPointerException {

        int maxLength = 0;
        String longestString = null;
        ListValueVO listVO = new ListValueVO();
        int arraySize = p_array.size();
        for (int i = 0; i < arraySize; i++) {
            listVO = (ListValueVO) p_array.get(i);
            String s = listVO.getLabel();
            if (s.length() > maxLength) {
                maxLength = s.length();
                longestString = s;
            }
        }
        if (longestString.length() != 0) {
            return (longestString.length() + 10) / 5;
        } else {
            return 0;
        }
    }

    /**
     * for direct payout
     * Write Excel
     * 
     * @param p_excelID
     * @param p_strArr
     * @param p_messages
     * @param p_locale
     * @param p_fileName
     * @throws Exception
     * @author lohit.audhkhasi
     * @throws IOException 
     * @throws WriteException 
     * @throws RowsExceededException 
     */
    public void writeExcelForDirectPayout(String p_excelID, String[][] p_strArr, MessageResources p_messages, Locale p_locale, String p_fileName, ArrayList p_bonusTypeList) throws IOException, RowsExceededException, WriteException {
        if (_log.isDebugEnabled()) {
            _log.debug("writeExcel", " p_excelID: " + p_excelID + " p_strArr:" + p_strArr + " p_messages: " + p_messages + " p_locale: " + p_locale + " p_strArr length: " + p_strArr.length + " p_fileName: " + p_fileName);
        }
        final String METHOD_NAME = "writeExcelForDirectPayout";
        WritableWorkbook workbook = null;
        WritableSheet worksheet = null;
        try {
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            _log.debug("writeExcelForDirectPayout", "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));

            String fileName = p_fileName;
            workbook = Workbook.createWorkbook(new File(fileName));
            worksheet = workbook.createSheet("First Sheet", 0);
            String key = null;
            String keyName = null;
            Label label = null;
            WritableFont times16font = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD, true);
            WritableCellFormat times16format = new WritableCellFormat(times16font);
            int[] indexMapArray = new int[p_strArr[0].length];
            String indexStr = null;
            StringBuffer commentBuff = new StringBuffer();
            String comment = null;
            ListValueVO listVO = new ListValueVO();
            int bonusTypeListSize = p_bonusTypeList.size();
            if (p_bonusTypeList != null) {
                for (int i = 0; i < bonusTypeListSize; i++) {
                    listVO = (ListValueVO) p_bonusTypeList.get(i);
                    commentBuff.append(listVO.getValue().substring(0, listVO.getValue().indexOf(":")) + " : " + listVO.getLabel() + "\n");
                }
                comment = commentBuff.toString();
            }

            if (BTSLUtil.isNullString(comment)) {
                commentBuff = new StringBuffer();
                String message = p_messages.getMessage(p_locale, "batchdirectpayout.processuploadedfile.error.bonustype");
                commentBuff.append(message);
                comment = commentBuff.toString();
            }

            for (int col = 0, len = p_strArr[0].length; col < len; col++) {
                indexStr = null;
                key = p_strArr[0][col];
                keyName = null;
                if (p_messages != null) {
                    keyName = p_messages.getMessage(p_locale, key);
                } else {
                    keyName = key;
                }
                indexStr = ExcelFileConstants.getWriteProperty(p_excelID, String.valueOf(col));
                if (indexStr == null) {
                    indexStr = String.valueOf(col);
                }
                indexMapArray[col] = Integer.parseInt(indexStr);
                label = new Label(indexMapArray[col], 0, keyName, times16format);
                if (p_messages != null && keyName.equals(p_messages.getMessage(p_locale, "batchdirectpayout.xlsheading.label.bonustype"))) {
                    WritableCellFeatures cellFeatures = new WritableCellFeatures();
                    if (p_bonusTypeList != null) {
                        if (p_bonusTypeList.size() > 0) {
                            cellFeatures.setComment(comment, getLengthLongestStringLength(p_bonusTypeList), p_bonusTypeList.size() + 1);
                        } else {
                            cellFeatures.setComment(comment);
                        }

                    } else {
                        cellFeatures.setComment(comment);
                    }
                    label.setCellFeatures(cellFeatures);

                }
                worksheet.addCell(label);
            }
            // setting for the vertical freeze panes
            SheetSettings sheetSetting = new SheetSettings();
            sheetSetting = worksheet.getSettings();
            sheetSetting.setVerticalFreeze(1);

            // label = new Label(indexMapArray[col],row,p_strArr[row][col]);
           int strArrLength = p_strArr.length;
            for (int row = 1, len = strArrLength; row < len; row++) {
                for (int col = 0, len1 = p_strArr[row].length; col < len1; col++) {
                    label = new Label(indexMapArray[col], row, p_strArr[row][col]);
                    worksheet.addCell(label);
                }
            }

            workbook.write();
        } catch (IOException | WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeExcel", " Exception e: " + e.getMessage());
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
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            _log.debug("writeExcelForDirectPayout", "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));
            if (_log.isDebugEnabled()) {
                _log.debug("writeExcel", " Exiting");
            }
        }
    }

    /**
     * Write Excel
     * 
     * @param p_excelID
     * @param p_strArr
     * @param p_messages
     * @param p_locale
     * @param p_fileName
     * @throws WriteException 
     * @throws RowsExceededException 
     * @throws IOException 
     * @throws Exception
     */
    public void writeExcel(String p_excelID, String[][] p_strArr, MessageResources p_messages, Locale p_locale, String p_fileName, String[] p_localeArr) throws RowsExceededException, WriteException, IOException {
        if (_log.isDebugEnabled()) {
            _log.debug("writeExcel", " p_excelID: " + p_excelID + " p_strArr:" + p_strArr + " p_messages: " + p_messages + " p_locale: " + p_locale + " p_strArr length: " + p_strArr.length + " p_fileName: " + p_fileName);
        }
        final String METHOD_NAME = "writeExcel";
        WritableWorkbook workbook = null;
        WritableSheet worksheet = null;
        int totalNoOfRecords = 0, noOfTotalSheet = 0, noOfRowsPerTemplate = 0;
        ;
        String noOfRowsInOneTemplate;
        try {
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            _log.debug("writeExcel", "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));

            String fileName = p_fileName;
            workbook = Workbook.createWorkbook(new File(fileName));

            totalNoOfRecords = p_strArr.length - 1;
            noOfRowsInOneTemplate = Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE");

            if (totalNoOfRecords > 0) {
                if (!BTSLUtil.isNullString(noOfRowsInOneTemplate)) {
                    noOfRowsInOneTemplate = noOfRowsInOneTemplate.trim();
                    noOfRowsPerTemplate = Integer.parseInt(noOfRowsInOneTemplate);
                } else {
                    noOfRowsPerTemplate = 65500; // Default value of rows
                }
                // Number of sheet to display the user list
                noOfTotalSheet = BTSLUtil.parseDoubleToInt( Math.ceil(BTSLUtil.parseIntToDouble(totalNoOfRecords) / noOfRowsPerTemplate));
            } else {
                noOfTotalSheet = 1;
            }

            String key = null;
            String keyName = null;
            Label label = null;
            int len = p_strArr[0].length;
            int recordNo = 0;
            WritableFont times16font = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD, true);
            WritableCellFormat times16format = new WritableCellFormat(times16font);
            int[] indexMapArray = new int[p_strArr[0].length];
            String indexStr = null;
            int i = 0, j = 0, col = 0, row = 0;
            String message = null;

            for (i = 0; i < noOfTotalSheet; i++) {
                worksheet = workbook.createSheet("Data Sheet" + (i + 1), i);
                j = 0;
                for (col = 0; col < len; col++) {
                    indexStr = null;
                    key = p_strArr[0][col];
                    keyName = null;
                    if (p_messages != null) {
                        keyName = p_messages.getMessage(p_locale, key);
                    } else {
                        keyName = key;
                    }
                    indexStr = ExcelFileConstants.getWriteProperty(p_excelID, String.valueOf(col));
                    if (indexStr == null) {
                        indexStr = String.valueOf(col);
                    }
                    indexMapArray[col] = Integer.parseInt(indexStr);
                    label = new Label(indexMapArray[col], 0, keyName, times16format);
                    if (label.getContents().contains("{0}")) {
                        message = label.getString().replace("{0}", "(" + p_localeArr[j] + ")");
                        label = new Label(indexMapArray[col], 0, message, times16format);
                        j++;
                    }
                    if(ExcelFileIDI.MESSAGES_LIST.equalsIgnoreCase(p_excelID) && col == 3){
                    	String commentStr = p_messages.getMessage(p_locale,"this.value.is.not.editable");
          				cellFeatures.setComment(commentStr);
          				label.setCellFeatures(cellFeatures);
                    }
                   
                    worksheet.addCell(label);
                }
                // setting for the vertical freeze panes
                SheetSettings sheetSetting = new SheetSettings();
                sheetSetting = worksheet.getSettings();
                sheetSetting.setVerticalFreeze(1);

                // setting for the horizontal freeze panes
                SheetSettings sheetSetting1 = new SheetSettings();
                sheetSetting1 = worksheet.getSettings();
                sheetSetting1.setHorizontalFreeze(6);

                if (noOfTotalSheet > 1) {
                    for (row = 1; row <= noOfRowsPerTemplate; row++) {
                        recordNo = row + (i * noOfRowsPerTemplate);
                        if (recordNo <= totalNoOfRecords) {
                            int len1 = p_strArr[recordNo].length;
                            for (col = 0; col < len1; col++) {
                                label = new Label(indexMapArray[col], row, p_strArr[recordNo][col]);
                                worksheet.addCell(label);
                            }
                        } else {
                            break;
                        }
                    }
                } else {
                    for (row = 1; row <= totalNoOfRecords; row++) {
                        int len1 = p_strArr[row].length;
                        for (col = 0; col < len1; col++) {
                            label = new Label(indexMapArray[col], row, p_strArr[row][col]);
                            worksheet.addCell(label);
                        }
                    }
                }
            }

            workbook.write();
        } catch (WriteException | IOException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeExcel", " Exception e: " + e.getMessage());
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
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            _log.debug("writeExcel", "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));
            if (_log.isDebugEnabled()) {
                _log.debug("writeExcel", " Exiting");
            }
        }
    }


    /**
     * Write Excel
     *
     * @param p_excelID
     * @param p_strArr
     * @param p_messages
     * @param p_locale
     * @param p_fileName
     * @throws WriteException
     * @throws RowsExceededException
     * @throws IOException
     * @throws Exception
     */
    public void writeMessagesToExcel(String p_excelID, String[][] p_strArr, Locale p_locale, String p_fileName, String[] p_localeArr) throws RowsExceededException, WriteException, IOException {
        if (_log.isDebugEnabled()) {
            _log.debug("writeExcel", " p_excelID: " + p_excelID + " p_strArr:" + p_strArr + " p_locale: " + p_locale + " p_strArr length: " + p_strArr.length + " p_fileName: " + p_fileName);
        }
        final String methodName = "writeMessagesToExcel";
        WritableWorkbook workbook = null;
        WritableSheet worksheet = null;
        int totalNoOfRecords = 0, noOfTotalSheet = 0, noOfRowsPerTemplate = 0;
        ;
        String noOfRowsInOneTemplate;
        try {
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            _log.debug(methodName, "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));

            String fileName = p_fileName;
            workbook = Workbook.createWorkbook(new File(fileName));

            totalNoOfRecords = p_strArr.length - 1;
            noOfRowsInOneTemplate = Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE");

            if (totalNoOfRecords > 0) {
                if (!BTSLUtil.isNullString(noOfRowsInOneTemplate)) {
                    noOfRowsInOneTemplate = noOfRowsInOneTemplate.trim();
                    noOfRowsPerTemplate = Integer.parseInt(noOfRowsInOneTemplate);
                } else {
                    noOfRowsPerTemplate = 65500; // Default value of rows
                }
                // Number of sheet to display the user list
                noOfTotalSheet = BTSLUtil.parseDoubleToInt( Math.ceil(BTSLUtil.parseIntToDouble(totalNoOfRecords) / noOfRowsPerTemplate));
            } else {
                noOfTotalSheet = 1;
            }

            String key = null;
            String keyName = null;
            Label label = null;
            int len = p_strArr[0].length;
            int recordNo = 0;
            WritableFont times16font = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD, true);
            WritableCellFormat times16format = new WritableCellFormat(times16font);
            int[] indexMapArray = new int[p_strArr[0].length];
            String indexStr = null;
            int i = 0, j = 0, col = 0, row = 0;
            String message = null;
            String DataSheet = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.MESSAGE_MANAGEMENT_DATA_SHEET, null);

            for (i = 0; i < noOfTotalSheet; i++) {
                worksheet = workbook.createSheet(DataSheet + (i + 1), i);
                j = 0;
                for (col = 0; col < len; col++) {
                    indexStr = null;
                    key = p_strArr[0][col];
                    keyName = null;
                    if (p_messages != null) {
                        keyName = p_messages.getMessage(p_locale, key);
                    } else {
                        keyName = key;
                    }
                    indexStr = ExcelFileConstants.getWriteProperty(p_excelID, String.valueOf(col));
                    if (indexStr == null) {
                        indexStr = String.valueOf(col);
                    }
                    indexMapArray[col] = Integer.parseInt(indexStr);
                    label = new Label(indexMapArray[col], 0, keyName, times16format);
                    if (label.getContents().contains("{0}")) {
                        message = label.getString().replace("{0}", "(" + p_localeArr[j] + ")");
                        label = new Label(indexMapArray[col], 0, message, times16format);
                        j++;
                    }
                    if(ExcelFileIDI.MESSAGES_LIST.equalsIgnoreCase(p_excelID) && col == 3){

                        String commentStr = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.MESSAGE_NOT_EDITABLE, null);
                        cellFeatures.setComment(commentStr);
                        label.setCellFeatures(cellFeatures);
                    }

                    worksheet.addCell(label);
                }
                // setting for the vertical freeze panes
                SheetSettings sheetSetting = new SheetSettings();
                sheetSetting = worksheet.getSettings();
                sheetSetting.setVerticalFreeze(1);

                // setting for the horizontal freeze panes
                SheetSettings sheetSetting1 = new SheetSettings();
                sheetSetting1 = worksheet.getSettings();
                sheetSetting1.setHorizontalFreeze(6);

                if (noOfTotalSheet > 1) {
                    for (row = 1; row <= noOfRowsPerTemplate; row++) {
                        recordNo = row + (i * noOfRowsPerTemplate);
                        if (recordNo <= totalNoOfRecords) {
                            int len1 = p_strArr[recordNo].length;
                            for (col = 0; col < len1; col++) {
                                label = new Label(indexMapArray[col], row, p_strArr[recordNo][col]);
                                worksheet.addCell(label);
                            }
                        } else {
                            break;
                        }
                    }
                } else {
                    for (row = 1; row <= totalNoOfRecords; row++) {
                        int len1 = p_strArr[row].length;
                        for (col = 0; col < len1; col++) {
                            label = new Label(indexMapArray[col], row, p_strArr[row][col]);
                            worksheet.addCell(label);
                        }
                    }
                }
            }

            workbook.write();
        } catch (WriteException | IOException e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, " Exception e: " + e.getMessage());
            throw e;
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            worksheet = null;
            workbook = null;
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            _log.debug(methodName, "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exiting");
            }
        }
    }






    /**
     * Write Multiple Excel without headings
     * 
     * @param p_excelID
     * @param p_strArr
     * @param p_messages
     * @param p_locale
     * @param p_fileName
     * @author ankur.dhawan
     * @throws IOException 
     * @throws WriteException 
     * @throws RowsExceededException 
     * @throws Exception
     */
    public void writeMultipleExcel(String p_excelID, String[][] p_strArr, MessageResources p_messages, Locale p_locale, String p_fileName) throws IOException, RowsExceededException, WriteException  {
        if (_log.isDebugEnabled()) {
            _log.debug("writeMultipleExcel", " p_excelID: " + p_excelID + " p_strArr:" + p_strArr + " p_messages: " + p_messages + " p_locale: " + p_locale + " p_strArr length: " + p_strArr.length + " p_fileName: " + p_fileName);
        }
        final String METHOD_NAME = "writeMultipleExcel";
        WritableWorkbook workbook = null;
        WritableSheet worksheet = null;
        int totalNoOfRecords = 0, noOfTotalSheet = 0, noOfRowsPerTemplate = 0;
        ;
        String noOfRowsInOneTemplate;
        try {
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;

            String fileName = p_fileName;
            workbook = Workbook.createWorkbook(new File(fileName));

            totalNoOfRecords = p_strArr.length - 1;
            noOfRowsInOneTemplate = Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE");

            if (totalNoOfRecords > 0) {
                if (!BTSLUtil.isNullString(noOfRowsInOneTemplate)) {
                    noOfRowsInOneTemplate = noOfRowsInOneTemplate.trim();
                    noOfRowsPerTemplate = Integer.parseInt(noOfRowsInOneTemplate);
                } else {
                    noOfRowsPerTemplate = 65500; // Default value of rows
                }
                // Number of sheet to display the user list
                noOfTotalSheet = BTSLUtil.parseDoubleToInt( Math.ceil(BTSLUtil.parseIntToDouble(totalNoOfRecords) / noOfRowsPerTemplate) );
            } else {
                noOfTotalSheet = 1;
            }
            WritableFont times16font = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD, true);
            WritableCellFormat times16format = new WritableCellFormat(times16font);
           

            String key = null;
            String keyName = null;
            Label label = null;
            int len = p_strArr[0].length;
            int recordNo = 0, len1 = 0;
            String indexStr = null;

            
            for (int i = 0; i < noOfTotalSheet; i++) {
            	
                worksheet = workbook.createSheet("Data Sheet" + (i + 1), i);

                int[] indexMapArray = new int[len];
                for (int col = 0; col < len; col++) {
                	WritableCellFeatures cellFeatures = new WritableCellFeatures();
                	String comment =null;
                    indexStr = null;
                    key = p_strArr[0][col];
                    keyName = null;
                    if (p_messages != null) {
                        keyName = p_messages.getMessage(p_locale, key);
                    } else {
                        keyName = key;
                    }
                    indexStr = ExcelFileConstants.getWriteProperty(p_excelID, String.valueOf(col));
                    if (indexStr == null) {
                        indexStr = String.valueOf(col);
                    }
                    indexMapArray[col] = Integer.parseInt(indexStr);
                    label = new Label(indexMapArray[col], 0, keyName, times16format);

                    if("channeluser.sos.transfer.allowed.amount.mandatory".equalsIgnoreCase(key) && col==4){
                    comment=p_messages.getMessage(p_locale,"channeluser.sos.transfer.allowed.amount.comment");
                    cellFeatures.setComment(comment);
                    label.setCellFeatures(cellFeatures);
                    }
                    
                    
                    if("channeluser.sos.transfer.minimum.allowed.amount.mandatory".equalsIgnoreCase(key) && col==5){
                       comment=p_messages.getMessage(p_locale,"cannot.be.more.than.ten.digits");
                   	   cellFeatures.setComment(comment);
                       label.setCellFeatures(cellFeatures);
                    }
                    
                    if("autoc2c.xlsheading.label.quantity".equalsIgnoreCase(key) && col==2){
                        comment=p_messages.getMessage(p_locale,"cannot.be.more.than.ten.digits");
                    	cellFeatures.setComment(comment);
                        label.setCellFeatures(cellFeatures);
                     }
                    
                    if("channeluser.last.recharge.transfer.allowed.amount.mandatory".equalsIgnoreCase(key) && col==7){
                        comment=p_messages.getMessage(p_locale,"cannot.be.more.than.ten.digits");
                    	cellFeatures.setComment(comment);
                        label.setCellFeatures(cellFeatures);
                     }
                    
                    worksheet.addCell(label);
                }
                // setting for the vertical freeze panes
                SheetSettings sheetSetting = new SheetSettings();
                sheetSetting = worksheet.getSettings();
                sheetSetting.setVerticalFreeze(1);

                // setting for the horizontal freeze panes
                SheetSettings sheetSetting1 = new SheetSettings();
                sheetSetting1 = worksheet.getSettings();
                sheetSetting1.setHorizontalFreeze(6);

                if (noOfTotalSheet > 1) {
                    for (int row = 1; row <= noOfRowsPerTemplate; row++) {
                        recordNo = row + (i * noOfRowsPerTemplate);
                        if (recordNo <= totalNoOfRecords) {
                            len1 = p_strArr[recordNo].length;
                            for (int col = 0; col < len1; col++) {
                                label = new Label(indexMapArray[col], row, p_strArr[recordNo][col]);
                                worksheet.addCell(label);
                            }
                        } else {
                            break;
                        }
                    }
                } else {
                    for (int row = 1; row <= totalNoOfRecords; row++) {
                        len1 = p_strArr[row].length;
                        for (int col = 0; col < len1; col++) {
                            label = new Label(indexMapArray[col], row, p_strArr[row][col]);
                            worksheet.addCell(label);
                        }
                    }
                }

            }

            workbook.write();
        } catch (IOException | WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeMultipleExcel", " Exception e: " + e.getMessage());
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
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;

            if (_log.isDebugEnabled()) {
                _log.debug("writeMultipleExcel", " Exiting");
            }
        }
    }

    /**
     * @Method Name writeModifyExcel
     * @param p_excelID
     * @param p_strArr
     * @param p_headerArray
     * @param p_heading
     * @param p_margeCont
     * @param locale
     * @param p_fileName
     * @author ankur.dhawan
     * @throws IOException 
     * @throws WriteException 
     * @throws Exception
     */
    public void writeMultipleExcel(String p_excelID, String[][] p_strArr, String[][] p_headerArray, String p_heading, int p_margeCont, MessageResources p_messages, Locale p_locale, String p_fileName) throws IOException, WriteException {
        if (_log.isDebugEnabled()) {
            _log.debug("writeMultipleExcel", " p_excelID: " + p_excelID + " p_strArr:" + p_strArr + " p_headerArray=" + p_headerArray + " p_heading=" + p_heading + " p_margeCont=" + p_margeCont + " p_locale: " + p_locale + " p_strArr length: " + p_strArr.length + " p_fileName: " + p_fileName);
        }
        final String METHOD_NAME = "writeMultipleExcel";
        WritableWorkbook workbook = null;
        WritableSheet worksheet = null;
        int totalNoOfRecords = 0, noOfTotalSheet = 0, noOfRowsPerTemplate = 0;
        String noOfRowsInOneTemplate;
        int recordNo = 0, lenInt = 0;
        try {
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;

            String fileName = p_fileName;
            workbook = Workbook.createWorkbook(new File(fileName));

            String key = null;
            String keyName = null;
            Label label = null;

            totalNoOfRecords = p_strArr.length - 1;
            noOfRowsInOneTemplate = Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE");

            if (totalNoOfRecords > 0) {
                if (!BTSLUtil.isNullString(noOfRowsInOneTemplate)) {
                    noOfRowsInOneTemplate = noOfRowsInOneTemplate.trim();
                    noOfRowsPerTemplate = Integer.parseInt(noOfRowsInOneTemplate);
                } else {
                    noOfRowsPerTemplate = 65500; // Default value of rows
                }
                // Number of sheet to display the user list
                noOfTotalSheet = BTSLUtil.parseDoubleToInt( Math.ceil(BTSLUtil.parseIntToDouble(totalNoOfRecords) / noOfRowsPerTemplate) );
            } else {
                noOfTotalSheet = 1;
            }
            WritableFont times16font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, true);
            WritableCellFormat times16format = new WritableCellFormat(times16font);

            WritableFont times12font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, true);
            WritableCellFormat times12format = new WritableCellFormat(times12font);

            WritableFont data = new WritableFont(WritableFont.ARIAL, 11, WritableFont.NO_BOLD, true);
            WritableCellFormat dataFormat = new WritableCellFormat(data);

            WritableFont times20font = new WritableFont(WritableFont.ARIAL, 20, WritableFont.BOLD, true);
            WritableCellFormat times20format = new WritableCellFormat(times20font);
            times20format.setAlignment(Alignment.CENTRE);

            int colnum = p_strArr[0].length;
            int[] indexMapArray = new int[colnum];
            int cols = p_headerArray[0].length;
            int row = 0, col = 0, length = 0;

            for (int i = 0; i < noOfTotalSheet; i++) {
                worksheet = workbook.createSheet("Data Sheet" + (i + 1), i);

                if (p_messages != null) {
                    keyName = p_messages.getMessage(p_locale, p_heading);
                } else {
                    keyName = p_heading;
                }

                label = new Label(0, 0, keyName, times20format);// Haeding
                worksheet.mergeCells(0, 0, colnum - 1, 0);
                worksheet.addCell(label);

                for (row = 0; row < cols; row++)// Header Headings
                {
                    key = p_headerArray[0][row];
                    keyName = null;
                    if (p_messages != null) {
                        keyName = p_messages.getMessage(p_locale, key);
                    } else {
                        keyName = key;
                    }
                    label = new Label(0, row + 1, keyName, times16format);
                    worksheet.mergeCells(0, row + 1, p_margeCont, row + 1);
                    worksheet.addCell(label);
                }
                for (row = 0; row < cols; row++)// Header Headings value
                {
                    keyName = null;
                    keyName = p_headerArray[1][row];
                    label = new Label(p_margeCont + 1, row + 1, keyName, dataFormat);
                    worksheet.mergeCells(p_margeCont + 1, row + 1, 3 * p_margeCont, row + 1);
                    worksheet.addCell(label);
                }
                length = p_strArr[0].length;
                String indexStr = null;
                for (col = 0; col < length; col++)// column heading
                {
                    indexStr = null;
                    key = p_strArr[0][col];
                    keyName = null;
                    if (p_messages != null) {
                        keyName = p_messages.getMessage(p_locale, key);
                    } else {
                        keyName = key;
                    }
                    indexStr = ExcelFileConstants.getWriteProperty(p_excelID, String.valueOf(col));
                    if (indexStr == null) {
                        indexStr = String.valueOf(col);
                    }
                    indexMapArray[col] = Integer.parseInt(indexStr);
                    label = new Label(indexMapArray[col], cols + 2, keyName, times16format);
                    worksheet.addCell(label);
                }
                // setting for the vertical freeze panes
                SheetSettings sheetSetting = new SheetSettings();
                sheetSetting = worksheet.getSettings();
                sheetSetting.setVerticalFreeze(cols + 3);

                // setting for the horizontal freeze panes
                SheetSettings sheetSetting1 = new SheetSettings();
                sheetSetting1 = worksheet.getSettings();
                sheetSetting1.setHorizontalFreeze(6);

                if (noOfTotalSheet > 1) {
                    for (row = 1; row <= noOfRowsPerTemplate; row++) {
                        recordNo = row + (i * noOfRowsPerTemplate);
                        if (recordNo <= totalNoOfRecords) {
                            lenInt = p_strArr[recordNo].length;
                            for (col = 0; col < lenInt; col++) {
                                label = new Label(indexMapArray[col], row + cols + 2, p_strArr[recordNo][col], dataFormat);
                                worksheet.addCell(label);
                            }
                        } else {
                            break;
                        }
                    }
                } else {
                    for (row = 1; row <= totalNoOfRecords; row++) {
                        lenInt = p_strArr[recordNo].length;
                        for (col = 0; col < lenInt; col++) {
                            label = new Label(indexMapArray[col], row + cols + 2, p_strArr[row][col], dataFormat);
                            worksheet.addCell(label);
                        }
                    }
                }
            }
            workbook.write();
        } catch (IOException | WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeMultipleExcel", " Exception e: " + e.getMessage());
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
            if (_log.isDebugEnabled()) {
                _log.debug("writeMultipleExcel", " Exiting");
            }
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;

        }
    }

    /**
     * readMultipleExcel
     * 
     * @param p_excelID
     * @param p_fileName
     * @param boolean p_readLastSheet
     * @param int p_leftHeaderLinesForEachSheet
     * @param p_map
     * @return String[][] strArr
     * @author ankur.dhawan
     * @throws IOException 
     * @throws BiffException 
     * @throws Exception
     */
    public String[][] readMultipleExcel(String p_excelID, String p_fileName, boolean p_readLastSheet, int p_leftHeaderLinesForEachSheet, HashMap<String, String> map) throws BiffException, IOException {
        if (_log.isDebugEnabled()) {
            _log.debug("readMultipleExcel", " p_excelID: " + p_excelID + " p_fileName: " + p_fileName + " p_readLastSheet=" + p_readLastSheet, " p_leftHeaderLinesForEachSheet=" + p_leftHeaderLinesForEachSheet);
        }
        final String METHOD_NAME = "readMultipleExcel";
        String strArr[][] = null;
        Workbook workbook = null;
        Sheet excelsheet = null;
        // Changes made by Ankur for batch Excel feature
        int noOfSheet = 0;
        int noOfRows = 0;
        int noOfcols = 0;
        int arrRow = p_leftHeaderLinesForEachSheet;
        try {
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            _log.debug("readMultipleExcel", "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));

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
        } catch (BiffException | IOException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("readMultipleExcel", " Exception e: " + e.getMessage());
            throw e;
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            excelsheet = null;
            workbook = null;
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            _log.debug("readMultipleExcel", "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));
            if (_log.isDebugEnabled()) {
                _log.debug("readMultipleExcel", " Exiting strArr: " + strArr.length);
            }
        }
    }

    public void writeMultipleExcelNew(WritableWorkbook workbook, String p_excelID, String[][] p_strArr, MessageResources p_messages, Locale p_locale, int sheetNo) throws RowsExceededException, WriteException {
        if (_log.isDebugEnabled()) {
            _log.debug("writeMultipleExcelNew", " p_excelID: " + p_excelID + " p_strArr:" + p_strArr + " p_messages: " + p_messages + " p_locale: " + p_locale + " p_strArr length: " + p_strArr.length + "sheetNo" + sheetNo);
        }
        final String METHOD_NAME = "writeMultipleExcelNew";
        // WritableWorkbook workbook = null;
        WritableSheet worksheet = null;
        int totalNoOfRecords = 0, noOfTotalSheet = 1, noOfRowsPerTemplate = 0;
        ;
        String noOfRowsInOneTemplate;
        try {
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;

            // String fileName=p_fileName;
            // workbook = Workbook.createWorkbook(new File(fileName));

            totalNoOfRecords = p_strArr.length - 1;
            noOfRowsInOneTemplate = Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE");

            /*
             * if(totalNoOfRecords>0)
             * {
             * if(!BTSLUtil.isNullString(noOfRowsInOneTemplate))
             * {
             * noOfRowsInOneTemplate=noOfRowsInOneTemplate.trim();
             * noOfRowsPerTemplate=Integer.parseInt(noOfRowsInOneTemplate);
             * } else {
             * noOfRowsPerTemplate=65500; //Default value of rows
             * }
             * //Number of sheet to display the user list
             * noOfTotalSheet=(int)Math.ceil((double)totalNoOfRecords/
             * noOfRowsPerTemplate);
             * }
             * else
             * {
             * noOfTotalSheet=1;
             * }
             */
            WritableFont times16font = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD, true);
            WritableCellFormat times16format = new WritableCellFormat(times16font);

            String key = null;
            String keyName = null;
            Label label = null;
            int len = p_strArr[0].length;
            int recordNo = 0, len1 = 0;
            String indexStr = null;

            worksheet = workbook.createSheet("Data Sheet" + (sheetNo + 1), sheetNo);

            int[] indexMapArray = new int[len];
            for (int col = 0; col < len; col++) {
                indexStr = null;
                key = p_strArr[0][col];
                keyName = null;
                if (p_messages != null) {
                    keyName = p_messages.getMessage(p_locale, key);
                } else {
                    keyName = key;
                }
                indexStr = ExcelFileConstants.getWriteProperty(p_excelID, String.valueOf(col));
                if (indexStr == null) {
                    indexStr = String.valueOf(col);
                }
                indexMapArray[col] = Integer.parseInt(indexStr);
                label = new Label(indexMapArray[col], 0, keyName, times16format);

                worksheet.addCell(label);
            }
            // setting for the vertical freeze panes
            SheetSettings sheetSetting = new SheetSettings();
            sheetSetting = worksheet.getSettings();
            sheetSetting.setVerticalFreeze(1);

            // setting for the horizontal freeze panes
            SheetSettings sheetSetting1 = new SheetSettings();
            sheetSetting1 = worksheet.getSettings();
            sheetSetting1.setHorizontalFreeze(6);

            if (noOfTotalSheet > 1) {
                for (int row = 1; row <= noOfRowsPerTemplate; row++) {
                    recordNo = row + (sheetNo * noOfRowsPerTemplate);
                    if (recordNo <= totalNoOfRecords) {
                        len1 = p_strArr[recordNo].length;
                        for (int col = 0; col < len1; col++) {
                            label = new Label(indexMapArray[col], row, p_strArr[recordNo][col]);
                            worksheet.addCell(label);
                        }
                    } else {
                        break;
                    }
                }
            } else {
                for (int row = 1; row <= totalNoOfRecords; row++) {
                    len1 = p_strArr[row].length;
                    for (int col = 0; col < len1; col++) {
                        label = new Label(indexMapArray[col], row, p_strArr[row][col]);
                        worksheet.addCell(label);
                    }
                }
            }
            // workbook.write();
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeMultipleExcelNew", " Exception e: " + e.getMessage());
            throw e;
        } finally {
            
             /* try{if(workbook!=null) {
              workbook.close();
              }}catch(Exception e){}
             */
            worksheet = null;
            // workbook=null;
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;

            if (_log.isDebugEnabled()) {
                _log.debug("writeMultipleExcelNew", " Exiting");
            }
        }
    }
    
    public void writeExcelForDirectPayoutApproval(String p_excelID, String[][] p_strArr, MessageResources p_messages, Locale p_locale, String p_fileName, ArrayList p_bonusTypeList) throws IOException, RowsExceededException, WriteException {
    	StringBuilder loggerValue= new StringBuilder(); 
    	if (_log.isDebugEnabled()) {
    		loggerValue.setLength(0);
        	loggerValue.append(" p_excelID: " );
        	loggerValue.append(p_excelID);
        	loggerValue.append(" p_strArr:");
        	loggerValue.append(p_strArr );
        	loggerValue.append(" p_messages: ");
        	loggerValue.append( p_messages );
        	loggerValue.append(" p_locale: ");
        	loggerValue.append(p_locale );
        	loggerValue.append(" p_strArr length: ");
        	loggerValue.append(p_strArr.length);
        	loggerValue.append(" p_fileName: ");
        	loggerValue.append(p_fileName);
            _log.debug("writeExcel", loggerValue );
        }
        final String METHOD_NAME = "writeExcelForDirectPayout";
        WritableWorkbook workbook = null;
        WritableSheet worksheet = null;
        try {
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            loggerValue.setLength(0);
            loggerValue.append("Total memory :");
            loggerValue.append(t_mem);
            loggerValue.append(" free memmory :");
            loggerValue.append(f_mem);
            loggerValue.append(" Used memory:" );
            loggerValue.append((t_mem - f_mem));
            _log.debug("writeExcelForDirectPayout",  loggerValue );

            String fileName = p_fileName;
            workbook = Workbook.createWorkbook(new File(fileName));
            worksheet = workbook.createSheet("First Sheet", 0);
            String key = null;
            String keyName = null;
            Label label = null;
            WritableFont times16font = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD, true);
            WritableCellFormat times16format = new WritableCellFormat(times16font);
            int[] indexMapArray = new int[p_strArr[0].length];
            String indexStr = null;
            StringBuffer commentBuff = new StringBuffer();
            String comment = null;
            ListValueVO listVO = new ListValueVO();
           
            if (p_bonusTypeList != null) {
            	 int bonusTypeListSize = p_bonusTypeList.size();
                for (int i = 0; i < bonusTypeListSize; i++) {
                    listVO = (ListValueVO) p_bonusTypeList.get(i);
                    commentBuff.append(listVO.getValue().substring(0, listVO.getValue().indexOf(":")) + " : " + listVO.getLabel() + "\n");
                }
                comment = commentBuff.toString();
            }

            if (BTSLUtil.isNullString(comment)) {
                commentBuff = new StringBuffer();
                String message = p_messages.getMessage(p_locale, "batchdirectpayout.processuploadedfile.error.bonustype");
                commentBuff.append(message);
                comment = commentBuff.toString();
            }

            for (int col = 0, len = p_strArr[0].length; col < len; col++) {
                indexStr = null;
                key = p_strArr[0][col];
                keyName = null;
                if (p_messages != null) {
                    keyName = p_messages.getMessage(p_locale, key);
                } else {
                    keyName = key;
                }
                indexStr = ExcelFileConstants.getWriteProperty(p_excelID, String.valueOf(col));
                if (indexStr == null) {
                    indexStr = String.valueOf(col);
                }
                indexMapArray[col] = Integer.parseInt(indexStr);
                label = new Label(indexMapArray[col], 0, keyName, times16format);
                if (p_messages != null && keyName.equals(p_messages.getMessage(p_locale, "batchdirectpayout.xlsheading.label.bonustype"))) {
                    WritableCellFeatures cellFeatures = new WritableCellFeatures();
                    if (p_bonusTypeList != null) {
                        if (p_bonusTypeList.size() > 0) {
                            cellFeatures.setComment(comment, getLengthLongestStringLength(p_bonusTypeList), p_bonusTypeList.size() + 1);
                        } else {
                            cellFeatures.setComment(comment);
                        }

                    } else {
                        cellFeatures.setComment(comment);
                    }
                    label.setCellFeatures(cellFeatures);

                }
                final String externalTxnMandatory = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_MANDATORY_FORDP));
               
                if(key.equalsIgnoreCase("batchdirectpayout.downloadfileforapproval.label.extnum") || key.equalsIgnoreCase("batchdirectpayout.downloadfileforapproval.label.extdate") || key.equalsIgnoreCase("batchdirectpayout.downloadfileforapproval.label.externalcode")){
                	if (!BTSLUtil.isNullString(externalTxnMandatory) && externalTxnMandatory.indexOf("1") != -1) {
                		 String commentStr = p_messages.getMessage(p_locale,"this.value.is.mandatory");
         				cellFeatures.setComment(commentStr);
         				label.setCellFeatures(cellFeatures);
                         worksheet.addCell(label);
                    }else{
                    	//label.setCellFeatures(cellFeatures);
                        worksheet.addCell(label);
                    }
                }else{
                	
                	if(ExcelFileIDI.BATCH_DP_APPRV2.equalsIgnoreCase(p_excelID)){
                		if(key.equalsIgnoreCase("batchdirectpayout.downloadfileforapproval.label.apprv1by") || key.equalsIgnoreCase("batchdirectpayout.downloadfileforapproval.label.apprv1on")){
                			String commentStr = p_messages.getMessage(p_locale,"this.value.is.not.editable");
             				cellFeatures.setComment(commentStr);
             				label.setCellFeatures(cellFeatures);
                		}
                	}
                	
                	if(ExcelFileIDI.BATCH_DP_APPRV3.equalsIgnoreCase(p_excelID)){
                		if(key.equalsIgnoreCase("batchdirectpayout.downloadfileforapproval.label.apprv2by") || key.equalsIgnoreCase("batchdirectpayout.downloadfileforapproval.label.apprv2on") || key.equalsIgnoreCase("batchdirectpayout.downloadfileforapproval.label.apprv1by") || key.equalsIgnoreCase("batchdirectpayout.downloadfileforapproval.label.apprv1on")){
                			String commentStr = p_messages.getMessage(p_locale,"this.value.is.not.editable");
             				cellFeatures.setComment(commentStr);
             				label.setCellFeatures(cellFeatures);
                		}
                	}
                	
                	if(key.equalsIgnoreCase("batchdirectpayout.downloadfileforapproval.label.bachdetailid") || key.equalsIgnoreCase("batchdirectpayout.downloadfileforapproval.label.mobilenumber") || key.equalsIgnoreCase("batchdirectpayout.downloadfileforapproval.label.usercat") || key.equalsIgnoreCase("batchdirectpayout.downloadfileforapproval.label.usergrade") || key.equalsIgnoreCase("batchdirectpayout.downloadfileforapproval.label.userlogin") || key.equalsIgnoreCase("batchdirectpayout.downloadfileforapproval.label.batchid") || key.equalsIgnoreCase("batchdirectpayout.downloadfileforapproval.label.qty") || key.equalsIgnoreCase("batchdirectpayout.downloadfileforapproval.label.bonustype") || key.equalsIgnoreCase("batchdirectpayout.downloadfileforapproval.label.initiatedby") || key.equalsIgnoreCase("batchdirectpayout.downloadfileforapproval.label.Initiatedon") || key.equalsIgnoreCase("batchdirectpayout.downloadfileforapproval.label.currentstatus")){
                		String commentStr = p_messages.getMessage(p_locale,"this.value.is.not.editable");
         				cellFeatures.setComment(commentStr);
         				label.setCellFeatures(cellFeatures);
                         worksheet.addCell(label);
                	}else{
                         worksheet.addCell(label);
                	}
                	 
                }
                
                
               
            }
            // setting for the vertical freeze panes
            SheetSettings sheetSetting = new SheetSettings();
            sheetSetting = worksheet.getSettings();
            sheetSetting.setVerticalFreeze(1);

            // label = new Label(indexMapArray[col],row,p_strArr[row][col]);
            int strArrLength = p_strArr.length;
            for (int row = 1, len = strArrLength; row < len; row++) {
                for (int col = 0, len1 = p_strArr[row].length; col < len1; col++) {
                    label = new Label(indexMapArray[col], row, p_strArr[row][col]);
                    worksheet.addCell(label);
                }
            }

            workbook.write();
        } catch (IOException | WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            loggerValue.setLength(0);
            loggerValue.append(" Exception e: ");
            loggerValue.append(e.getMessage());
            _log.error("writeExcel", loggerValue );
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
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            loggerValue.setLength(0);
            loggerValue.append("Total memory :");
            loggerValue.append(t_mem);
            loggerValue.append("   free memmory :");
            loggerValue.append(f_mem);
            loggerValue.append(" Used memory:");
            loggerValue.append((t_mem - f_mem));
            _log.debug("writeExcelForDirectPayout",  loggerValue );
            if (_log.isDebugEnabled()) {
                _log.debug("writeExcel", " Exiting");
            }
        }
    }
    
   
    /**writeExcelForUserProfile
     * @param p_excelID
     * @param p_hashMap
     * @throws p_headerArray
     * @throws p_heading
     * @param messages
     * @param locale
     * @param p_fileName
     * @throws WriteException 
     * @throws RowsExceededException 
	 * @throws IOException 
     * @throws Exception
     */
    public void writeExcelForUserProfile(String p_excelID,Map p_hashMap,String[][] p_headerArray, String p_heading,MessageResources messages,Locale locale,String p_fileName) throws RowsExceededException, WriteException, IOException 
	{
    	String methodName= "writeExcelForUserProfile";
    	if(_log.isDebugEnabled())
		    _log.debug("methodName"," p_excelID: "+p_excelID+" p_hashMap:"+p_hashMap+" p_locale: "+locale+" p_fileName: "+p_fileName);
		WritableWorkbook workbook = null;
		WritableSheet worksheet = null;
		int row=0;
		int cols=0;
		try
		{
			
			workbook = Workbook.createWorkbook(new File(p_fileName));
			worksheet = workbook.createSheet("First Sheet", 0);
			p_messages=messages;
			p_locale=locale;
			Label label = null;
			String keyName =null;
			String key = null;
			times20format.setAlignment(Alignment.CENTRE);
			if(p_messages!=null)
				keyName =p_messages.getMessage(p_locale,p_heading) ;
			else
				keyName =p_heading;
			// Heading
			label=new Label(0,0,keyName,times20format);
			worksheet.mergeCells(0,0,5,0);
			worksheet.addCell(label);
			
			cols=p_headerArray[0].length;
			//Header Headings
			for(row=0; row<cols; row++)
			{
			    key = p_headerArray[0][row];
				keyName =null;
				if(p_messages!=null)
				    keyName =p_messages.getMessage(p_locale,key) ;
				else
					keyName =key;
				label=new Label(0,row+1,keyName,times16format);
				worksheet.mergeCells(0,row+1,2,row+1);
				worksheet.addCell(label);
				
				keyName =null;
			    keyName = p_headerArray[1][row];
				label=new Label(2+1,row+1,keyName,dataFormat);
				worksheet.mergeCells(2+1,row+1,3*2,row+1);
				worksheet.addCell(label);
				
			}
			row++;
			row++;
			cols=0;
			row=this.writeBalPref(worksheet, cols, row, p_hashMap);
			row++;
			cols=0;
			row=this.writeTrfCtrlPref(worksheet, cols, row, p_hashMap);
			workbook.write();
	}catch ( WriteException | IOException e) {
       _log.errorTrace(methodName, e);
       _log.error(methodName, " Exception e: " + e.getMessage());
        throw e;    
        }finally{

        try {
            if (workbook != null) {
                workbook.close();
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        worksheet = null;
        workbook = null;
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " Exiting");
        }
    
	}
}
    /**writeBalPref
     * @param worksheet
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */
    private int writeBalPref(WritableSheet worksheet,  int col,int rows, Map p_hashMap) throws RowsExceededException,WriteException
    {
    	String methodName= "writeBalPref";
        if(_log.isDebugEnabled())
		    _log.debug(methodName," p_hashMap size="+p_hashMap.size()+" p_locale: "+p_locale+" col="+col+" row="+rows);
        try 
        {
            String keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.balpref");
            
            Label label = new Label(col,rows,keyName, times16format);
            worksheet.mergeCells(col,rows,col+10,rows);
            worksheet.addCell(label);
            int row = rows;
            row++;
            int cols=0;
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.prodname");
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.prodcode");
            cols=cols+1;
            label = new Label(col,row,keyName, times16format);
            worksheet.addCell(label);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.prodstcode");
            cols= cols+1;
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.minresbal");
            cols= cols+1;
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.maxbal");
            cols= cols+1;
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.perc2stxnamtmin");
            cols= cols+1;
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.perc2stxnamtmax");
            cols= cols+1;
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.altbal");
            cols= cols+1;
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.allmaxperc");
            cols= cols+1;
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.currentbal");
            cols= cols+1;
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            row++;
            
            cols=0;
            
            ArrayList list = (ArrayList)p_hashMap.get(PretupsI.USER_PRODUCT_LIST);
            if(list!=null)
	        {
	            Iterator iterator=list.iterator();
	            TransferProfileProductVO profileVO= null;
	            while (iterator.hasNext())
				{
	            	profileVO = (TransferProfileProductVO)iterator.next();
	            	int col1=0;
	            	label = new Label(col1,row,profileVO.getProductName() );
	            	worksheet.addCell(label);
	            	col1=col1+1;
	            	label = new Label(col1,row,profileVO.getProductCode());
	            	worksheet.addCell(label);
	            	col1=col1+1;
	            	label = new Label(col1,row,profileVO.getProductShortCode());
	            	worksheet.addCell(label);
	            	col1=col1+1;
	            	label = new Label(col1,row,profileVO.getMinBalance());
	            	worksheet.addCell(label);
	            	col1=col1+1;
	            	label = new Label(col1,row,profileVO.getMaxBalance());
	            	worksheet.addCell(label);
	            	col1=col1+1;
	            	label = new Label(col1,row,profileVO.getC2sMinTxnAmt());
	            	worksheet.addCell(label);
	            	col1=col1+1;
	            	label = new Label(col1,row,profileVO.getC2sMaxTxnAmt());
	            	worksheet.addCell(label);
	            	col1=col1+1;
	            	label = new Label(col1,row,profileVO.getAltBalance());
	            	worksheet.addCell(label);
	            	col1=col1+1;
	            	label = new Label(col1,row,profileVO.getAllowedMaxPercentage());
	            	worksheet.addCell(label);
	            	col1=col1+1;
	            	label = new Label(col1,row,profileVO.getCurrentBalance());
	            	worksheet.addCell(label);
	            	row++;
	         }
	        }
            return row;
        } 
        catch (RowsExceededException e) 
        {
            e.printStackTrace();
			_log.error(methodName," Exception e: "+e.getMessage());
			throw e;
        } catch (WriteException e) {
            e.printStackTrace();
			_log.error(methodName," Exception e: "+e.getMessage());
			throw e;
        }
    }
    /**writeTrfCtrlPref
     * @param worksheet
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */
    private int writeTrfCtrlPref(WritableSheet worksheet,  int col,int rows, Map p_hashMap) throws RowsExceededException,WriteException
    {
    	String methodName= "writeTrfCtrlPref";
        if(_log.isDebugEnabled())
		    _log.debug(methodName," p_hashMap size="+p_hashMap.size()+" p_locale: "+p_locale+" col="+col+" row="+rows);
        try 
        {
            String keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.trfctrlpref");
            
            Label label = new Label(col,rows,keyName, times16format);
            worksheet.mergeCells(col,rows,col+10,rows);
            worksheet.addCell(label);
            int row =rows;
            row++;
           
            int cols=0;
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.daily");
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.effprfcount");
            cols=cols+1;
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.currentcount");
            cols=cols+1;
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.altcount");
            cols=cols+1;
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.effprfvalue");
            cols=cols+1;
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.currentvalue");
            cols=cols+1;
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.altvalue");
            cols=cols+1;
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            row++;
          
            cols=0;
            TransferProfileVO profileVO=(TransferProfileVO)p_hashMap.get(PretupsI.TRANSFER_PROFILE_VO);
            UserTransferCountsVO trfCountVO=(UserTransferCountsVO)p_hashMap.get(PretupsI.USER_TRF_COUNT_VO);
            String flag=(String)p_hashMap.get(PretupsI.SUB_OUT_COUNT_FLAG);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.trfin");
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,profileVO.getDailyInCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,trfCountVO.getDailyInCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,profileVO.getDailyInAltCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(profileVO.getDailyInValue()));
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(trfCountVO.getDailyInValue()));
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(profileVO.getDailyInAltValue()));
            worksheet.addCell(label);
            row++;
            
            cols=0;
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.chnltrfout");
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,profileVO.getDailyOutCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,trfCountVO.getDailyOutCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,profileVO.getDailyOutAltCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(profileVO.getDailyOutValue()));
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(trfCountVO.getDailyOutValue()));
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(profileVO.getDailyOutAltValue()));
            worksheet.addCell(label);
            row++;
           
            cols=0;
            if("true".equalsIgnoreCase(flag))
	        {
	            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.subtrfout");
	            label = new Label(cols,row,keyName, times16format);
	            worksheet.addCell(label);
	            cols=cols+1;
	            label = new Label(cols,row,profileVO.getDailyC2STransferOutCount()+"");
	            worksheet.addCell(label);
	            cols=cols+1;
	            label = new Label(cols,row,trfCountVO.getDailyC2STransferOutCount()+"");
	            worksheet.addCell(label);
	            cols=cols+1;
	            label = new Label(cols,row,profileVO.getDailySubscriberOutAltCount()+"");
	            worksheet.addCell(label);
	            cols=cols+1;
	            label = new Label(cols,row,PretupsBL.getDisplayAmount(profileVO.getDailyC2STransferOutValue()));
	            worksheet.addCell(label);
	            cols=cols+1;
	            label = new Label(cols,row,PretupsBL.getDisplayAmount(trfCountVO.getDailyC2STransferOutValue()));
	            worksheet.addCell(label);
	            cols=cols+1;
	            label = new Label(cols,row,PretupsBL.getDisplayAmount(profileVO.getDailySubscriberOutAltValue()));
	            worksheet.addCell(label);
	            row++;
	            
	            cols=0;
	            
	          //code added for daily subscriber details
	            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.subtrfin");
	            label = new Label(cols,row,keyName, times16format);
	            worksheet.addCell(label);
	            cols=cols+1;
	            label = new Label(cols,row,profileVO.getDailySubscriberInCount()+"");
	            worksheet.addCell(label);
	            cols=cols+1;
	            label = new Label(cols,row,trfCountVO.getDailySubscriberInCount()+"");
	            worksheet.addCell(label);
	            cols=cols+1;
	            label = new Label(cols,row,profileVO.getDailySubscriberInAltCount()+"");
	            worksheet.addCell(label);
	            cols=cols+1;
	            label = new Label(cols,row,PretupsBL.getDisplayAmount(profileVO.getDailySubscriberInValue()));
	            worksheet.addCell(label);
	            cols=cols+1;
	            label = new Label(cols,row,PretupsBL.getDisplayAmount(trfCountVO.getDailySubscriberInValue()));
	            worksheet.addCell(label);
	            cols=cols+1;
	            label = new Label(cols,row,PretupsBL.getDisplayAmount(profileVO.getDailySubscriberInAltValue()));
	            worksheet.addCell(label);
	            row++;
	           
	            cols=0;
	            
	            
	        }
            row++;
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.weekly");
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.effprfcount");
            cols=cols+1;
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.currentcount");
            cols=cols+1;
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.altcount");
            cols=cols+1;
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.effprfvalue");
            cols=cols+1;
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.currentvalue");
            cols=cols+1;
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.altvalue");
            cols=cols+1;
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            row++;
            
            cols=0;
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.trfin");
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,profileVO.getWeeklyInCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,trfCountVO.getWeeklyInCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,profileVO.getWeeklyInAltCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(profileVO.getWeeklyInValue()));
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(trfCountVO.getWeeklyInValue()));
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(profileVO.getWeeklyInAltValue()));
            worksheet.addCell(label);
            row++;
            cols=0;
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.chnltrfout");
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,profileVO.getWeeklyOutCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,trfCountVO.getWeeklyOutCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,profileVO.getWeeklyOutAltCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(profileVO.getWeeklyOutValue()));
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(trfCountVO.getWeeklyOutValue()));
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(profileVO.getWeeklyOutAltValue()));
            worksheet.addCell(label);
            row++;
            cols=0;
            if("true".equalsIgnoreCase(flag))
	        {
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.subtrfout");
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,profileVO.getWeeklyC2STransferOutCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,trfCountVO.getWeeklyC2STransferOutCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,profileVO.getWeeklySubscriberOutAltCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(profileVO.getWeeklyC2STransferOutValue()));
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(trfCountVO.getWeeklyC2STransferOutValue()));
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(profileVO.getWeeklySubscriberOutAltValue()));
            worksheet.addCell(label);
            row++;
            cols=0;
            // code added for weekly subscriber in values
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.subtrfin");
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,profileVO.getWeeklySubscriberInCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,trfCountVO.getWeeklySubscriberInCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,profileVO.getWeeklySubscriberInAltCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(profileVO.getWeeklySubscriberInValue()));
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(trfCountVO.getWeeklySubscriberInValue()));
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(profileVO.getWeeklySubscriberInAltValue()));
            worksheet.addCell(label);
            row++;
            cols=0;
            
            
	        }
            row++;
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.monthly");
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.effprfcount");
            cols=cols+1;
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.currentcount");
            cols=cols+1;
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.altcount");
            cols=cols+1;
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.effprfvalue");
            cols=cols+1;
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.currentvalue");
            cols=cols+1;
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.altvalue");
            cols=cols+1;
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            row++;
            cols=0;
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.trfin");
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,profileVO.getMonthlyInCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,trfCountVO.getMonthlyInCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,profileVO.getMonthlyInAltCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(profileVO.getMonthlyInValue()));
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(trfCountVO.getMonthlyInValue()));
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(profileVO.getMonthlyInAltValue()));
            worksheet.addCell(label);
            row++;
            cols=0;
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.chnltrfout");
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,profileVO.getMonthlyOutCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,trfCountVO.getMonthlyOutCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,profileVO.getMonthlyOutAltCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(profileVO.getMonthlyOutValue()));
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(trfCountVO.getMonthlyOutValue()));
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(profileVO.getMonthlyOutAltValue()));
            worksheet.addCell(label);
            row++;
            cols=0;
            if("true".equalsIgnoreCase(flag))
	        {
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.subtrfout");
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,profileVO.getMonthlyC2STransferOutCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,trfCountVO.getMonthlyC2STransferOutCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,profileVO.getMonthlySubscriberOutAltCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(profileVO.getMonthlyC2STransferOutValue()));
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(trfCountVO.getMonthlyC2STransferOutValue()));
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(profileVO.getMonthlySubscriberOutAltValue()));
            worksheet.addCell(label);
            row++;
            
            // code added for monthly subscriber in value
            cols=0;
            keyName = p_messages.getMessage(p_locale,"channel.user.threshold.xls.enq.subtrfin");
            label = new Label(cols,row,keyName, times16format);
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,profileVO.getMonthlySubscriberInCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,trfCountVO.getMonthlySubscriberInCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,profileVO.getMonthlySubscriberInAltCount()+"");
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(profileVO.getMonthlySubscriberInValue()));
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(trfCountVO.getMonthlySubscriberInValue()));
            worksheet.addCell(label);
            cols=cols+1;
            label = new Label(cols,row,PretupsBL.getDisplayAmount(profileVO.getMonthlySubscriberInAltValue()));
            worksheet.addCell(label);
            row++;
            
	        }
            return row;
        } 
        catch (RowsExceededException e) 
        {
            e.printStackTrace();
			_log.error(methodName," Exception e: "+e.getMessage());
			throw e;
        } catch (WriteException e) {
            e.printStackTrace();
			_log.error(methodName," Exception e: "+e.getMessage());
			throw e;
        }
    } 
    
    /**
	 * Write Excel
	 * @param p_excelID
	 * @param p_strArr
	 * @param p_messages
	 * @param p_locale
	 * @param p_fileName
	 * @throws Exception
	 */
	public void writeExcelForOptBC2Cinitiate(String p_excelID,String[][] p_strArr,String downloadType,MessageResources p_messages,Locale p_locale,String p_fileName) throws Exception
	{
		final String methodName = "writeExcelForOptBC2Cinitiate";
		if(_log.isDebugEnabled())_log.debug(methodName," p_excelID: "+p_excelID+" p_strArr:"+p_strArr+" p_messages: "+p_messages+" p_locale: "+p_locale+" p_strArr length: "+p_strArr.length+" p_fileName: "+p_fileName);
		WritableWorkbook workbook = null;
		WritableSheet worksheet = null;
		int totalNoOfRecords=0,noOfTotalSheet=0;
		String noOfRowsInOneTemplate;
	
		try
		{
		    double t_mem=Runtime.getRuntime().totalMemory()/1048576;
		    Runtime.getRuntime().gc();
		    double f_mem=Runtime.getRuntime().freeMemory()/1048576; 
		  
		    totalNoOfRecords=p_strArr.length-1;
		    noOfRowsInOneTemplate=Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE_BATCHC2C");
			
			int noOfRowsPerTemplate=0;
			if(totalNoOfRecords>0)
			{
				if(!BTSLUtil.isNullString(noOfRowsInOneTemplate))
				{
					noOfRowsInOneTemplate=noOfRowsInOneTemplate.trim();
					noOfRowsPerTemplate=Integer.parseInt(noOfRowsInOneTemplate);
				}
				else
					noOfRowsPerTemplate=65500;					//Default value of rows
				noOfTotalSheet=(int)Math.ceil((double)totalNoOfRecords/noOfRowsPerTemplate);
			}
			else 
			{
				noOfTotalSheet=1;
			}
			String fileName=p_fileName;
			workbook = Workbook.createWorkbook(new File(fileName));
			String key = null;
			String keyName =null; 
			Label label = null;
			
			int recordNo=0;
			for(int i=0;i<noOfTotalSheet;i++)
			{
				worksheet = workbook.createSheet("Sheet"+(i+1), i);
			WritableFont times16font = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD, true);
			WritableCellFormat times16format = new WritableCellFormat (times16font);
			int[] indexMapArray=new int[p_strArr[0].length];  
			String indexStr=null;
			for(int col = 0, len=p_strArr[0].length; col < len; col++)
			{
			    indexStr=null;
				key = p_strArr[0][col];
				keyName =null;
				if(p_messages!=null)
				{
					keyName =p_messages.getMessage(p_locale,key) ;
				}
				else
				{
					keyName =key;
				}
				indexStr=ExcelFileConstants.getWriteProperty(p_excelID,String.valueOf(col));
				if(indexStr==null)
					indexStr=String.valueOf(col);
				indexMapArray[col]=Integer.parseInt(indexStr);
				label = new Label(indexMapArray[col],0,keyName,times16format);
				///
				if(downloadType!=null && downloadType.equals("downloadlist")){
					switch(col){
					
					case 0: 						
						String commentMsisdn  = null;
						commentMsisdn=p_messages.getMessage(p_locale,"batchc2c.xlsfile.details.msisdn.comment");
						WritableCellFeatures cellFeatures0=new WritableCellFeatures();
			            cellFeatures0.setComment(commentMsisdn);
			            label.setCellFeatures(cellFeatures0);
			            break;
			            
					case 2: 						
						commentMsisdn  = null;
						commentMsisdn=p_messages.getMessage(p_locale,"batchc2c.xlsfile.details.msisdn.comment");
						cellFeatures0=new WritableCellFeatures();
			            cellFeatures0.setComment(commentMsisdn);
			            label.setCellFeatures(cellFeatures0);
			            break;         
					  
					}
				}
				
				else
				{
					//Handling of template format
					switch(col){
					
					case 0: 						
						String commentMsisdn  = null;
						commentMsisdn=p_messages.getMessage(p_locale,"batchc2c.xlsfile.details.msisdn.comment");
						WritableCellFeatures cellFeatures0=new WritableCellFeatures();
			            cellFeatures0.setComment(commentMsisdn);
			            label.setCellFeatures(cellFeatures0);
			            break;
			            
					case 1: 						
						commentMsisdn  = null;
						commentMsisdn=p_messages.getMessage(p_locale,"batchc2c.xlsfile.details.msisdn.comment");
						cellFeatures0=new WritableCellFeatures();
			            cellFeatures0.setComment(commentMsisdn);
			            label.setCellFeatures(cellFeatures0);
			            break;
						
					}
				}
				////
				worksheet.addCell(label);		
			}
				//setting for the vertical freeze panes
			SheetSettings sheetSetting=new SheetSettings();
			sheetSetting=worksheet.getSettings();
			sheetSetting.setVerticalFreeze(1);

			//setting for the horizontal freeze panes
			SheetSettings sheetSetting1=new SheetSettings();
			sheetSetting1=worksheet.getSettings();
			sheetSetting1.setHorizontalFreeze(6);
			
			if(noOfTotalSheet>1)
			{
				for(int row = 1; row <=noOfRowsPerTemplate; row++)
				{
					recordNo=row+(i*noOfRowsPerTemplate);
					if(recordNo<=totalNoOfRecords)
					{
						int len1=p_strArr[recordNo].length;
						for(int col = 0; col < len1; col++)
						{
							label = new Label(indexMapArray[col],row,p_strArr[recordNo][col]);
							worksheet.addCell(label);
						}
					}
					else
						break;
				}
			}
			else
			{
				for(int row = 1; row <=totalNoOfRecords; row++)
				{
					int len1=p_strArr[row].length;
					for(int col = 0; col < len1; col++)
					{
						label = new Label(indexMapArray[col],row,p_strArr[row][col]);
						worksheet.addCell(label);
					}
				}
			
			}
			
		
			
			}
			workbook.write();
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName, e);
			throw e;
		}
		finally
		{
			try{if(workbook!=null)workbook.close();}catch(Exception e){}
			worksheet=null;
			workbook=null;
			double t_mem=Runtime.getRuntime().totalMemory()/1048576;
		    Runtime.getRuntime().gc();
		    double f_mem=Runtime.getRuntime().freeMemory()/1048576; 
		    if(_log.isDebugEnabled())
		    {
		    	_log.debug("Total memory :"+t_mem,"   free memmory :"+f_mem+" Used memory:"+(t_mem-f_mem));
		    	_log.debug(methodName," Exiting");
		    }
		}
	}
	public void writeMultipleExcelNew(String p_excelID, String[][] p_strArr, Locale p_locale, String p_fileName)
			throws IOException, RowsExceededException, WriteException {
		if (_log.isDebugEnabled()) {
			_log.debug("writeMultipleExcel",
					" p_excelID: " + p_excelID + " p_strArr:" + p_strArr + " p_messages: " + p_messages + " p_locale: "
							+ p_locale + " p_strArr length: " + p_strArr.length + " p_fileName: " + p_fileName);
		}
		final String METHOD_NAME = "writeMultipleExcel";
		WritableWorkbook workbook = null;
		WritableSheet worksheet = null;
		int totalNoOfRecords = 0, noOfTotalSheet = 0, noOfRowsPerTemplate = 0;
		;
		String noOfRowsInOneTemplate;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		try {
			double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
			double f_mem = Runtime.getRuntime().freeMemory() / 1048576;

			String fileName = p_fileName;
			workbook = Workbook.createWorkbook(new File(fileName));

			totalNoOfRecords = p_strArr.length - 1;
			noOfRowsInOneTemplate = Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE");

			if (totalNoOfRecords > 0) {
				if (!BTSLUtil.isNullString(noOfRowsInOneTemplate)) {
					noOfRowsInOneTemplate = noOfRowsInOneTemplate.trim();
					noOfRowsPerTemplate = Integer.parseInt(noOfRowsInOneTemplate);
				} else {
					noOfRowsPerTemplate = 65500;
				}
				noOfTotalSheet = BTSLUtil
						.parseDoubleToInt(Math.ceil(BTSLUtil.parseIntToDouble(totalNoOfRecords) / noOfRowsPerTemplate));
			} else {
				noOfTotalSheet = 1;
			}
			WritableFont times16font = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD, true);
			WritableCellFormat times16format = new WritableCellFormat(times16font);

			String key = null;
			String keyName = null;
			Label label = null;
			int len = p_strArr[0].length;
			int recordNo = 0, len1 = 0;
			String indexStr = null;

			for (int i = 0; i < noOfTotalSheet; i++) {

				worksheet = workbook.createSheet("Data Sheet" + (i + 1), i);

				int[] indexMapArray = new int[len];
				String template = com.btsl.util.Constants.getProperty("AUTO_C2C_SOS");
				String templateArr[] = template.split(("\\,"));
				for (int col = 0; col < len; col++) {
					WritableCellFeatures cellFeatures = new WritableCellFeatures();
					String comment = null;
					indexStr = null;
					key = p_strArr[0][col];
					keyName = BTSLUtil.getMessage(locale,key);

					indexStr = ExcelFileConstants.getWriteProperty(p_excelID, String.valueOf(col));
					if (indexStr == null) {
						indexStr = String.valueOf(col);
					}
					indexMapArray[col] = Integer.parseInt(indexStr);
					label = new Label(indexMapArray[col], 0, keyName, times16format);
					  if("channeluser.sos.transfer.allowed.amount.mandatory".equalsIgnoreCase(key) && col==4){
                    comment = BTSLUtil.getMessage(locale,"channeluser.sos.transfer.allowed.amount.comment");
                    cellFeatures.setComment(comment);
                    label.setCellFeatures(cellFeatures);
                    }
                    
                    
                    if("channeluser.sos.transfer.minimum.allowed.amount.mandatory".equalsIgnoreCase(key) && col==5){
                       comment = BTSLUtil.getMessage(locale,"cannot.be.more.than.ten.digits");
                   	   cellFeatures.setComment(comment);
                       label.setCellFeatures(cellFeatures);
                    }
                    
                    if("autoc2c.xlsheading.label.quantity".equalsIgnoreCase(key) && col==2){
                    	comment = BTSLUtil.getMessage(locale,"cannot.be.more.than.ten.digits");
                    	cellFeatures.setComment(comment);
                        label.setCellFeatures(cellFeatures);
                     }
                    
                    if("channeluser.last.recharge.transfer.allowed.amount.mandatory".equalsIgnoreCase(key) && col==7){
                    	comment = BTSLUtil.getMessage(locale,"cannot.be.more.than.ten.digits");
                    	cellFeatures.setComment(comment);
                        label.setCellFeatures(cellFeatures);
                     }

					worksheet.addCell(label);
				}
				// setting for the vertical freeze panes
				SheetSettings sheetSetting = new SheetSettings();
				sheetSetting = worksheet.getSettings();
				sheetSetting.setVerticalFreeze(1);

				// setting for the horizontal freeze panes
				SheetSettings sheetSetting1 = new SheetSettings();
				sheetSetting1 = worksheet.getSettings();
				sheetSetting1.setHorizontalFreeze(6);

				if (noOfTotalSheet > 1) {
					for (int row = 1; row <= noOfRowsPerTemplate; row++) {
						recordNo = row + (i * noOfRowsPerTemplate);
						if (recordNo <= totalNoOfRecords) {
							len1 = p_strArr[recordNo].length;
							for (int col = 0; col < len1; col++) {
								label = new Label(indexMapArray[col], row, p_strArr[recordNo][col]);
								worksheet.addCell(label);
							}
						} else {
							break;
						}
					}
				} else {
					for (int row = 1; row <= totalNoOfRecords; row++) {
						len1 = p_strArr[row].length;
						for (int col = 0; col < len1; col++) {
							label = new Label(indexMapArray[col], row, p_strArr[row][col]);
							worksheet.addCell(label);
						}
					}
				}

			}

			workbook.write();
		} catch (IOException | WriteException e) {
			_log.errorTrace(METHOD_NAME, e);
			_log.error("writeMultipleExcel", " Exception e: " + e.getMessage());
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
			double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
			// Runtime.getRuntime().gc();
			double f_mem = Runtime.getRuntime().freeMemory() / 1048576;

			if (_log.isDebugEnabled()) {
				_log.debug("writeMultipleExcel", " Exiting");
			}
		}
	}
	
	
	/**
     * readBulkc2sReverExcel
     * 
     * @param p_excelID
     * @param p_fileName
     * @return
     * @throws IOException 
     * @throws BiffException 
     * @throws Exception
     */
    public List<C2SRechargeReversalDetails>  readBulkc2sReverExcel(String p_fileName) throws BiffException, IOException {
    	final String methodName ="readBulkc2sReverExcel";
        if (_log.isDebugEnabled()) {
            _log.debug( methodName,"readBulkc2sReverExcel   p_fileName: " + p_fileName);
        }
        
        //String strArr[][] = null;
        List<C2SRechargeReversalDetails>  listBulkC2sReversal = new ArrayList<C2SRechargeReversalDetails>();
        HSSFWorkbook workbook= null;
        HSSFSheet excelsheet=null;
        try {
        	String dir = Constants.getProperty("DownloadBatchC2SReversal"); // Upload file path
            String path = dir+File.separator+p_fileName;
            FileInputStream file = new FileInputStream(path);        	
            workbook = new HSSFWorkbook(file);
            
            excelsheet = workbook.getSheetAt(0);
            int noOfRows = excelsheet.getLastRowNum();
            int noOfcols = excelsheet.getRow(0).getLastCellNum();
          //  strArr = new String[noOfRows+1][noOfcols+1];
            FormulaEvaluator formulaEvaluator=workbook.getCreationHelper().createFormulaEvaluator();
            C2SRechargeReversalDetails c2SRechargeReversalDetails= null;
            
            for(Row rows: excelsheet)     //iteration over row using for each loop  
            {  
	            for(org.apache.poi.ss.usermodel.Cell cellv: rows)    //iteration over cell using for each loop  
	            {  
	            	if(formulaEvaluator.evaluateInCell(cellv).getCellType()== CellType.STRING && rows.getRowNum()>0) {
//	            		strArr[rows.getRowNum()][cellv.getColumnIndex()] =cellv.getStringCellValue() ;
//	            		strArr[rows.getRowNum()][cellv.getColumnIndex()+1] = rows.getRowNum()+"";
	            		c2SRechargeReversalDetails= new C2SRechargeReversalDetails();
	            		c2SRechargeReversalDetails.setTxnid(cellv.getStringCellValue());
	             	}	
	             }
	            if(rows.getRowNum()>0) {
	            	listBulkC2sReversal.add(c2SRechargeReversalDetails);
	            }
            }
         	
       return listBulkC2sReversal;
        } catch (IOException e) {
            _log.errorTrace(methodName, e);
            _log.error(methodName, " Exception e: " + e.getMessage());
            throw e;
        } finally {
            try {
            	
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            excelsheet = null;
            workbook = null;
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            _log.debug(methodName, "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exiting arraylise size: " + listBulkC2sReversal.size());
            }
        }
    }
    /**
     * @param p_excelID
     * @param p_hashMap
     * @param locale
     * @param p_fileName
     * @throws IOException 
     * @throws WriteException 
     * @throws RowsExceededException 
     * @throws Exception
     */

    public void writeExcelForUserDefaultConfigurtion(String p_excelID, HashMap p_hashMap, Locale locale, String p_fileName) throws IOException, RowsExceededException, WriteException {
        if (_log.isDebugEnabled()) {
            _log.debug("writeExcelForUserDefaultConfigurtion", " p_excelID: " + p_excelID + ", p_hashMap size:" + p_hashMap.size() +" p_locale: " + locale + " p_fileName: " + p_fileName);
        }
        final String METHOD_NAME = "writeExcelForUserDefaultConfigurtion";
        WritableWorkbook workbook = null;
        WritableSheet worksheet1 = null;
        WritableSheet worksheet2 = null;
        String noOfRowsInOneTemplate = null; // No. of users data in one sheet
        int noOfTotalSheet = 0;
        int col = 0;
        int row = 0;
        double t_mem = 0;
        double f_mem = 0;
        int userDefaultConfigMapSize = 0;
        HashMap<String, Object> userDefaultMasterMap = null;
        // ArrayList userDefaultConfigList=null;

        try {
            userDefaultMasterMap = new HashMap<String, Object>();
            t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            _log.info("writeExcelForUserDefaultConfigurtion", "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));
            noOfRowsInOneTemplate = Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE_PROFILEMANAGEMENT");
            userDefaultMasterMap = (HashMap<String, Object>) p_hashMap.get(PretupsI.USR_DEF_CONFIG_EXCEL_DATA);
            Iterator<String> iteratorMaster = userDefaultMasterMap.keySet().iterator();
            while (iteratorMaster.hasNext()) {
                String keyCategory = iteratorMaster.next();
                HashMap<String, Object> userDefMap = (HashMap<String, Object>) userDefaultMasterMap.get(keyCategory);
                if (userDefMap != null) {
                    userDefaultConfigMapSize++;
                }
            }
            int noOfRowsPerTemplate = 0;
            if (!BTSLUtil.isNullString(noOfRowsInOneTemplate)) {
                noOfRowsInOneTemplate = noOfRowsInOneTemplate.trim();
                noOfRowsPerTemplate = Integer.parseInt(noOfRowsInOneTemplate);
            } else {
                noOfRowsPerTemplate = 65500;
                // Number of sheet to display the user list
            }

            noOfTotalSheet = BTSLUtil.parseDoubleToInt( Math.ceil(BTSLUtil.parseIntToDouble(userDefaultConfigMapSize) / noOfRowsPerTemplate));
            workbook = Workbook.createWorkbook(new File(p_fileName));

            if (noOfTotalSheet > 1) {
                int i = 0;
                int k = 0;
                HashMap<String, Object> tempMap = new HashMap<String, Object>();
                p_locale = locale;
                Iterator<String> iterator = userDefaultMasterMap.keySet().iterator();
                String key = iterator.next();
                for (i = 0; i < noOfTotalSheet; i++) {
                    tempMap.clear();
                    if (k + noOfRowsPerTemplate < userDefaultConfigMapSize) {

                        for (int j = k; j < k + noOfRowsPerTemplate; j++) {
                            // tempMap.put(userDefaultConfigMap.get(j));
                            while (userDefaultMasterMap.get(key) == null && iterator.hasNext()) {
                                key = iterator.next();
                            }
                            tempMap.put(key, userDefaultMasterMap.get(key));
                            if (iterator.hasNext()) {
                                key = iterator.next();
                            }
                        }
                    } else {
                        for (int j = k; j < userDefaultConfigMapSize; j++) {
                            while (userDefaultMasterMap.get(key) == null && iterator.hasNext()) {
                                key = iterator.next();
                            }
                            tempMap.put(key, userDefaultMasterMap.get(key));
                            if (iterator.hasNext()) {
                                key = iterator.next();
                            }
                        }

                    }
                    p_hashMap.put(PretupsI.USR_DEF_CONFIG_EXCEL_DATA, tempMap);
                    worksheet1 = workbook.createSheet("Template Sheet" + (i + 1), i);
                    this.writeInDataSheetForUserDefaultConfig(worksheet1, col, row, p_hashMap);
                    k = k + noOfRowsPerTemplate;
                }
                worksheet2 = workbook.createSheet("Master Sheet", i);

                // p_hashMap.put(PretupsI.USR_DEF_CONFIG_EXCEL_DATA,userDefaultConfigMap);
            }
            // End of multiple sheet requirement -
            else {

                worksheet1 = workbook.createSheet("Template Sheet", 0);
                p_locale = locale;
                col = 0;
                row = 0;
                this.writeInDataSheetForUserDefaultConfig(worksheet1, col, row, p_hashMap);
                worksheet2 = workbook.createSheet("Master Sheet", 1);

            }

            Label label = null;
            String keyName = null;
        	String arr[] = {(String) p_hashMap.get(PretupsI.USR_DEF_CONFIG_DOMAIN_NAME)};
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_MASTERSHEET_HEADING, arr);
            label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 5, row);
            worksheet2.addCell(label);
            row++;
            row++;
            col = 0;
            row = this.writeCategoryDataForUserDefaultConfig(worksheet2, col, row, p_hashMap);
            workbook.write();
        } catch (IOException | WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeExcelForUserDefaultConfigurtion", " Exception e: " + e.getMessage());
            throw e;
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
            t_mem = Runtime.getRuntime().totalMemory(); 
            // Runtime.getRuntime().gc();
            f_mem = Runtime.getRuntime().freeMemory();
            _log.info("writeExcelForUserDefaultConfigurtion", "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));
            if (_log.isDebugEnabled()) {
                _log.debug("writeExcelForUserDefaultConfigurtion", " Exiting");
            }
        }
    }
    /**
     * @param worksheet1
     * @param col
     * @param row
     * @param p_hashMap
     * @throws WriteException 
     * @throws RowsExceededException 
     * @throws Exception
     */

    private void writeInDataSheetForUserDefaultConfig(WritableSheet worksheet1, int col, int row, HashMap<String, Object> p_hashMap) throws RowsExceededException, WriteException {
        if (_log.isDebugEnabled()) {
            _log.debug("writeInDataSheetForUserDefaultConfig", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeInDataSheetForUserDefaultConfig";
        try {
        	String arr[] = {(String) p_hashMap.get(PretupsI.USR_DEF_CONFIG_DOMAIN_NAME)};

            String keyName = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.USER_DEFAULT_CONFIG_XLXFILE_INITIATE_HEADING , arr);
            String comment = null;
            Label label = new Label(col, row, keyName, times12format);
            worksheet1.mergeCells(col, row, col + 5, row);
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_XLXFILE_HEADER_DOWNLOADEDBY,null);
            label = new Label(col, row, keyName, times16format);
            worksheet1.addCell(label);

            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.USR_DEF_CONFIG_CREATED_BY));
            worksheet1.addCell(label);
            row++;
            col = 0;
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_XLXFILE_HEADER_DOMAINNAME,null);
            label = new Label(col, row, keyName, times16format);
            worksheet1.addCell(label);

            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.USR_DEF_CONFIG_DOMAIN_NAME));
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_XLXFILE_DETAILS_CATEGORYCODE,null);
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_XLXFILE_DETAILS_TRANSFERPROFILE,null);
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_XLXFILE_TRANSFERPROFILE_COMMENT,null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_XLXFILE_DETAILS_COMMISIONPROFILE,null);
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.USER_DEFAULT_CONFIG_XLXFILE_COMMISIONPROFILE_COMMENT,null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_XLXFILE_DETAILS_GRADE,null);
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_XLXFILE_DETAILS_GRADE_COMMENT,null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_XLXFILE_DETAILS_GROUPCODE,null);
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_XLXFILE_DETAILS_GROUPCODE_COMMENT,null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_XLXFILE_DETAILS_ACTIONT,null);
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            // /setting for the vertical freeze panes
            SheetSettings sheetSetting = new SheetSettings();
            sheetSetting = worksheet1.getSettings();
            sheetSetting.setVerticalFreeze(row + 1);

            HashMap<String, Object> userDefaulMasterMap = (HashMap<String, Object>) p_hashMap.get(PretupsI.USR_DEF_CONFIG_EXCEL_DATA);
            Iterator<String> iteratorMaster = userDefaulMasterMap.keySet().iterator();
            while (iteratorMaster.hasNext()) {
                String keyCategory = iteratorMaster.next();
                HashMap<String, Object> userDefMap = (HashMap<String, Object>) userDefaulMasterMap.get(keyCategory);
                if (userDefMap != null) {
                    row++;
                    col = 0;
                    // Category Code
                    label = new Label(col++, row, keyCategory);
                    worksheet1.addCell(label);

                    label = new Label(col++, row, (String) userDefMap.get(keyCategory + "_TRFPRF"));
                    worksheet1.addCell(label);

                    label = new Label(col++, row, (String) userDefMap.get(keyCategory + "_COMPRF"));
                    worksheet1.addCell(label);

                    label = new Label(col++, row, (String) userDefMap.get(keyCategory + "_GRDCODE"));
                    worksheet1.addCell(label);

                    label = new Label(col++, row, (String) userDefMap.get(keyCategory + "_ROLECODE"));
                    worksheet1.addCell(label);
                }
            }
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeInDataSheetForUserDefaultConfig", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeInDataSheetForUserDefaultConfig", " Exception e: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Method writeCategoryDataForUserDefaultConfig
     * This method writes the category details including
     * geographies,grade,commision profile,transfer
     * profile & group information
     * 
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws WriteException 
     * @throws RowsExceededException 
     * @throws Exception
     */

    private int writeCategoryDataForUserDefaultConfig(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws RowsExceededException, WriteException {
        if (_log.isDebugEnabled()) {
            _log.debug("writeCategoryData", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        String keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_MASTERSHEET_CATEGORYHEADING,null);
        Label label = new Label(col, row, keyName, times16format);
        worksheet2.mergeCells(col, row, col + 2, row);
        worksheet2.addCell(label);
        row++;
        col = 0;
        keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_MASTERSHEET_CATEGORYHEADING_NOTE,null);
        label = new Label(col, row, keyName);
        worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
        worksheet2.addCell(label);

        row++;
        col = 0;

        keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_MASTERSHEET_CATEGORYCODE,null);
        label = new Label(col, row, keyName, times16format);
        worksheet2.addCell(label);
        keyName = RestAPIStringParser.getMessage(p_locale,PretupsErrorCodesI.USER_DEFAULT_CONFIG_MASTERSHEET_CATEGORYNAME ,null);
        label = new Label(++col, row, keyName, times16format);
        worksheet2.addCell(label);

        keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_MASTERSHEET_CATEGORYGRADE,null);
        label = new Label(++col, row, keyName, times16format);
        worksheet2.mergeCells(col, row, col + 1, row);
        worksheet2.addCell(label);

        keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_MASTERSHEET_CATEGORYGRADECODE,null);
        label = new Label(col, row + 1, keyName, times16format);
        worksheet2.addCell(label);
        keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_MASTERSHEET_CATEGORYGRADENAME,null);
        label = new Label(++col, row + 1, keyName, times16format);
        worksheet2.addCell(label);

        keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_MASTERSHEET_TRANSFERCONTROLPRF,null);
        label = new Label(++col, row, keyName, times16format);
        worksheet2.mergeCells(col, row, col + 1, row);
        worksheet2.addCell(label);

        keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_MASTERSHEET_TRANSFERCONTROLPRFCODE,null);
        label = new Label(col, row + 1, keyName, times16format);
        worksheet2.addCell(label);
        keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_MASTERSHEET_TRANSFERCONTROLPRFNAME,null);
        label = new Label(++col, row + 1, keyName, times16format);
        worksheet2.addCell(label);

        keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_MASTERSHEET_COMMISIONPROFILE,null);
        label = new Label(++col, row, keyName, times16format);
        worksheet2.mergeCells(col, row, col + 1, row);
        worksheet2.addCell(label);
        keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_MASTERSHEET_COMMISIONPROFILECODE,null);
        label = new Label(col, row + 1, keyName, times16format);
        worksheet2.addCell(label);
        keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_MASTERSHEET_COMMISIONPROFILENAME,null);
        label = new Label(++col, row + 1, keyName, times16format);
        worksheet2.addCell(label);

        keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_MASTERSHEET_GROUPROLE,null);
        label = new Label(++col, row, keyName, times16format);
        worksheet2.mergeCells(col, row, col + 1, row);
        worksheet2.addCell(label);
        keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_MASTERSHEET_GROUPROLECODE,null);
        label = new Label(col, row + 1, keyName, times16format);
        worksheet2.addCell(label);
        keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_MASTERSHEET_GROUPROLENAME,null);
        label = new Label(++col, row + 1, keyName, times16format);
        worksheet2.addCell(label);

        keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.USER_DEFAULT_CONFIG_MASTERSHEET_REMARKS,null);
        label = new Label(++col, row + 1, keyName, times16format);
        worksheet2.addCell(label);

        row++;
        col = 0;

        // Iteration Starts from Row number
        row = row + 1;
        ArrayList categoryList = (ArrayList) p_hashMap.get(PretupsI.USR_DEF_CONFIG_CATEGORY_LIST);
        ArrayList gradeList = (ArrayList) p_hashMap.get(PretupsI.USR_DEF_CONFIG_GRADE_LIST);
        ArrayList transferProfileList = (ArrayList) p_hashMap.get(PretupsI.USR_DEF_CONFIG_TRANSFER_CONTROL_PRF_LIST);
        ArrayList commPrfList = (ArrayList) p_hashMap.get(PretupsI.USR_DEF_CONFIG_COMMISION_PRF_LIST);
        ArrayList grpRoleList = (ArrayList) p_hashMap.get(PretupsI.USR_DEF_CONFIG_GROUP_ROLE_LIST);

        int catSize = 0;
        int tempCol = 0;
        int tempRow = 0;
        int gradeRowSize, gradeListSize = 0;
        int trfPrfRowSize, transferPrfListSize = 0;
        int commPrfRowSize, commPrfListSize = 0;
        int groupRoleRowSize, groupListSize = 0;
        int maxRow = 0;

        CategoryVO categoryVO = null;
        GradeVO gradeVO = null;
        TransferProfileVO profileVO = null;
        CommissionProfileSetVO commissionProfileSetVO = null;
        UserRolesVO rolesVO = null;

        if (categoryList != null && (catSize = categoryList.size()) > 0) {
            for (int i = 0; i < catSize; i++) // Prints One Row Of Category
            {
                int notFoundCounter = 0;
                col = 0;
                categoryVO = (CategoryVO) categoryList.get(i);
                label = new Label(col, row, categoryVO.getCategoryCode());
                worksheet2.addCell(label);
                label = new Label(++col, row, categoryVO.getCategoryName());
                worksheet2.addCell(label);

                // Equate The Sizes of the list first so that it will not be
                // checked in the loop
                if (gradeList != null) {
                    gradeListSize = gradeList.size();
                }
                if (transferProfileList != null) {
                    transferPrfListSize = transferProfileList.size();
                }
                if (commPrfList != null) {
                    commPrfListSize = commPrfList.size();
                }
                if (grpRoleList != null) {
                    groupListSize = grpRoleList.size();
                }

                boolean isExistGrade = false;
                boolean isExistTransferProfile = false;
                boolean isExistCommissionProfile = false;
                boolean isExistGroupRole = false;
                StringBuilder notDefinedArray = null;

                tempRow = row;
                for (int k = 0; k < gradeListSize; k++) {
                    tempCol = col;
                    gradeVO = (GradeVO) gradeList.get(k);
                    if (gradeVO.getCategoryCode().equals(categoryVO.getCategoryCode())) {
                        label = new Label(++tempCol, tempRow, gradeVO.getGradeCode());
                        worksheet2.addCell(label);
                        label = new Label(++tempCol, tempRow, gradeVO.getGradeName());
                        worksheet2.addCell(label);
                        tempRow++;
                        isExistGrade = true;
                    }
                }
                if (!isExistGrade)// add comment
                {
                    notDefinedArray = new StringBuilder("Grade");
                    notFoundCounter++;
                }

                gradeRowSize = tempRow;
                tempRow = row;
                maxRow = gradeRowSize;

                for (int l = 0; l < transferPrfListSize; l++) {
                    tempCol = col + 2;
                    profileVO = (TransferProfileVO) transferProfileList.get(l);
                    if (profileVO.getCategory().equals(categoryVO.getCategoryCode())) {
                        label = new Label(++tempCol, tempRow, profileVO.getProfileId());
                        worksheet2.addCell(label);
                        label = new Label(++tempCol, tempRow, profileVO.getProfileName());
                        worksheet2.addCell(label);
                        tempRow++;
                        isExistTransferProfile = true;
                    }
                }
                if (!isExistTransferProfile)// add comment
                {
                    if (notFoundCounter >= 1) {
                        notDefinedArray.append(", Transfer profile");
                    } else {
                        notDefinedArray = new StringBuilder("Transfer profile");
                    }
                    notFoundCounter++;
                }

                trfPrfRowSize = tempRow;
                tempRow = row;

                if (trfPrfRowSize > maxRow) {
                    maxRow = trfPrfRowSize;
                }

                for (int m = 0; m < commPrfListSize; m++) {
                    tempCol = col + 4;
                    commissionProfileSetVO = (CommissionProfileSetVO) commPrfList.get(m);
                    if (commissionProfileSetVO.getCategoryCode().equals(categoryVO.getCategoryCode())) {
                        label = new Label(++tempCol, tempRow, commissionProfileSetVO.getCommProfileSetId());
                        worksheet2.addCell(label);
                        label = new Label(++tempCol, tempRow, commissionProfileSetVO.getCommProfileSetName());
                        worksheet2.addCell(label);
                        tempRow++;
                        isExistCommissionProfile = true;
                    }
                }
                if (!isExistCommissionProfile)// add comment
                {
                    if (notFoundCounter >= 1) {
                        notDefinedArray.append(", Commission profile");
                    } else {
                        notDefinedArray = new StringBuilder("Commission profile");
                    }
                    notFoundCounter++;
                }

                commPrfRowSize = tempRow;
                tempRow = row;

                if (commPrfRowSize > maxRow) {
                    maxRow = commPrfRowSize;
                }

                for (int n = 0; n < groupListSize; n++) {
                    tempCol = col + 6;
                    rolesVO = (UserRolesVO) grpRoleList.get(n);
                    if (rolesVO.getCategoryCode().equals(categoryVO.getCategoryCode())) {
                        label = new Label(++tempCol, tempRow, rolesVO.getRoleCode());
                        worksheet2.addCell(label);
                        label = new Label(++tempCol, tempRow, rolesVO.getRoleName());
                        worksheet2.addCell(label);
                        tempRow++;
                        isExistGroupRole = true;
                    }
                }
                if (!isExistGroupRole)// add comment
                {
                    if (notFoundCounter >= 1) {
                        notDefinedArray.append(", Group role code");
                    } else {
                        notDefinedArray = new StringBuilder("Group role code");
                    }
                    notFoundCounter++;
                }
                groupRoleRowSize = tempRow;
                tempRow = row;

                if (groupRoleRowSize > maxRow) {
                    maxRow = groupRoleRowSize;
                }

                if (notFoundCounter > 0 && notDefinedArray != null) {
                    tempCol = col + 8;
                    String arr[]= {notDefinedArray.toString()};
                    keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.BULKUSER_XLSFILE_DETAILS_ANOYONENOTDEFINED_COMMENT, arr);
                    label = new Label(++tempCol, tempRow, keyName);
                    worksheet2.addCell(label);
                }

                if (tempRow > maxRow) {
                    maxRow = tempRow;
                }
                // Max size of ROWS according to the data
                row = maxRow;
            }
        }
        return row;
    }
    
	
	
}

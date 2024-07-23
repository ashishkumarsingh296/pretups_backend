package com.btsl.pretups.xl;

/**
 * @(#)ActivationExcelWR.java
 *                            Copyright(c) 2009, Bharti Telesoft Ltd.
 *                            All Rights Reserved
 * 
 *                            <description>
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            rahul.dutt Apr 02,2009 Initital Creation
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 * 
 */
import java.io.File;
import java.util.Locale;

import com.btsl.util.MessageResources;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;
import com.btsl.xl.ExcelFileConstants;

import jxl.SheetSettings;
import jxl.Workbook;
import jxl.write.Alignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ActivationExcelRW {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    
    /**
     * this method is used to write to excel the filearr and headerarray
     * in this method if noOfRowsPerTemplate>65490 then data will be written to
     * next worksheet
     * 
     * @param p_excelID
     * @param p_strArr
     * @param p_headerArray
     * @param p_heading
     * @param p_margeCont
     * @param p_messages
     * @param p_locale
     * @param p_fileName
     * @throws Exception
     * @author rahul.dutt
     */
    public void writeExcel(String p_excelID, String[][] p_strArr, String[][] p_headerArray, String p_heading, int p_margeCont, MessageResources p_messages, Locale p_locale, String p_fileName) {
        final String METHOD_NAME = "writeExcel";
        if (_log.isDebugEnabled()) {
            _log.debug("writeExcel", " p_excelID: " + p_excelID + " p_strArr:" + p_strArr + " p_headerArray=" + p_headerArray + " p_heading=" + p_heading + " p_margeCont=" + p_margeCont + " p_locale: " + p_locale + " p_strArr length: " + p_strArr.length + " p_fileName: " + p_fileName);
        }
        WritableWorkbook workbook = null;
        WritableSheet worksheet = null;
        int noOfTotalSheet = 0;
        double len = 0;
        try {
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;

            String fileName = p_fileName;
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
            workbook = Workbook.createWorkbook(new File(fileName));
            int noOfRowsPerTemplate = 65490;// max no. of records to be
                                            // displayed in one sheet
            // Number of sheet to display the user list
            noOfTotalSheet = BTSLUtil.parseDoubleToInt( Math.ceil(BTSLUtil.parseIntToDouble((p_strArr.length - 1)) / noOfRowsPerTemplate));
            for (int sheetRowCount = 0; sheetRowCount < noOfTotalSheet; sheetRowCount++) {
                worksheet = workbook.createSheet("Data Sheet" + (sheetRowCount + 1), sheetRowCount);
                String key = null;
                String keyName = null;
                Label label = null;
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

                // note added to the excel sheet
                if (p_messages != null) {
                    keyName = p_messages.getMessage(p_locale, "activation.xlsfile.details.Note");
                }
                label = new Label(0, cols + 2, keyName);
                worksheet.mergeCells(0, cols + 2, 5, cols);
                worksheet.addCell(label);
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
                    label = new Label(indexMapArray[col], cols + 3, keyName, times16format);
                    worksheet.addCell(label);
                }
                // setting for the vertical freeze panes
                SheetSettings sheetSetting = new SheetSettings();
                sheetSetting = worksheet.getSettings();
                sheetSetting.setVerticalFreeze(cols + 4);
                // setting for the horizontal freeze panes
                SheetSettings sheetSetting1 = new SheetSettings();
                sheetSetting1 = worksheet.getSettings();
                sheetSetting1.setHorizontalFreeze(6);
                /*
                 * here we check whther sheet is last if it is
                 * last then add no of rows in sheet equal to the no. of records
                 * left
                 */
                if ((sheetRowCount + 1) == noOfTotalSheet) {
                    len = Math.IEEEremainder(p_strArr.length - 1, noOfRowsPerTemplate);
                    if (len <= 0) {
                        len = len + noOfRowsPerTemplate;
                    }
                } else {
                    len = noOfRowsPerTemplate;
                }

                int lenInt = 0;
                for (int row = sheetRowCount * noOfRowsPerTemplate, i = 1; row < len + sheetRowCount * noOfRowsPerTemplate; row++, i++)// column
                                                                                                                                       // value
                {

                    lenInt = p_strArr[row].length;
                    for (int col = 0; col < lenInt; col++) {
                        label = new Label(indexMapArray[col], i + cols + 3, p_strArr[row + 1][col], dataFormat);
                        worksheet.addCell(label);
                    }
                }
            }

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

}

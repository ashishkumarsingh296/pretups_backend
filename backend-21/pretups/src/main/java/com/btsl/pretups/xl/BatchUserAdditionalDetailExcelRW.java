package com.btsl.pretups.xl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.util.MessageResources;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.xl.ExcelFileConstants;

import jxl.Cell;
import jxl.Sheet;
import jxl.SheetSettings;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class BatchUserAdditionalDetailExcelRW {

    private static Log _log = LogFactory.getLog(BatchUserAdditionalDetailExcelRW.class.getName());
    private static  final int COLUMN_MARGE = 10;
    private WritableCellFeatures cellFeatures = new WritableCellFeatures();
    private WritableFont times12font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, true);
    private WritableCellFormat times12format = new WritableCellFormat(times12font);
    private WritableFont times16font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, true);
    private WritableCellFormat times16format = new WritableCellFormat(times16font);
    private MessageResources p_messages = null;
    private  Locale p_locale = null;


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
        double t_mem = 0;
        double f_mem = 0;
        try {
            t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            _log.info("readExcel", "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));
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
            t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            _log.info("readExcel", "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));
            if (_log.isDebugEnabled()) {
                _log.debug("readExcel", " Exiting strArr: " + strArr);
            }
        }
        return strArr;
    }

    /**
     * Method writeGeographyListing
     * This method writes the Geography Details containing zone,area,sub area
     * etc. [ N level geographies can exists ]
     * 
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     * @author Amit Ruwali
     */
    private int writeGeographyListing(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeGeographyListing", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeGeographyListing";
        try {
            String keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.avaliablegeographieslist");
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.avaliablegeographieslist.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            // Logic for generating headings
            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_GEOGRAPHY_LIST);
            UserGeographiesVO userGeographiesVO = null;
            ArrayList geoDomainTypeList = new ArrayList();

            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    userGeographiesVO = (UserGeographiesVO) list.get(i);
                    if (!geoDomainTypeList.contains(userGeographiesVO.getGraphDomainType())) {
                        geoDomainTypeList.add(userGeographiesVO.getGraphDomainType());
                    }
                }
            }
            // Generate Headings from the ArrayList
            String endTagCode = p_messages.getMessage(p_locale, "code");
            String endTagName = p_messages.getMessage(p_locale, "name");

            String geoType = null;
            for (int i = 0, j = geoDomainTypeList.size(); i < j; i++) {
                geoType = ((String) geoDomainTypeList.get(i)).trim();
                keyName = p_messages.getMessage(p_locale, geoType);
                label = new Label(col, row, keyName + " " + endTagCode + "(" + geoType + ")", times16format);
                worksheet2.addCell(label);
                label = new Label(++col, row, keyName + " " + endTagName, times16format);
                worksheet2.addCell(label);
                col++;
            }
            row++;
            col = 0;
            int nameOccurance = 0;
            int oldseqNo = 0;
            int sequence_num = 0;
            if (list != null) {
                sequence_num = ((UserGeographiesVO) list.get(0)).getGraphDomainSequenceNumber();
                for (int i = 0, j = list.size(); i < j; i++) {

                    userGeographiesVO = (UserGeographiesVO) list.get(i);
                    if (oldseqNo > userGeographiesVO.getGraphDomainSequenceNumber()) {
                        nameOccurance -= (oldseqNo - userGeographiesVO.getGraphDomainSequenceNumber()); // for
                    } else if (oldseqNo < userGeographiesVO.getGraphDomainSequenceNumber()) {
                        nameOccurance++;
                    }

                    col = nameOccurance + userGeographiesVO.getGraphDomainSequenceNumber() - sequence_num;
                    // Change made for batch user creation by channel user
                    if (userGeographiesVO.getGraphDomainSequenceNumber() == sequence_num) {
                        col = 0;
                        nameOccurance = 0;
                    }
                    // End of Change made for batch user creation by channel
                    // user
                    label = new Label(col, row, userGeographiesVO.getGraphDomainCode());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, userGeographiesVO.getGraphDomainName());
                    worksheet2.addCell(label);

                    oldseqNo = userGeographiesVO.getGraphDomainSequenceNumber();
                    row++;
                }
            }

            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeGeographyListing", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeGeographyListing", " Exception e: " + e.getMessage());
            throw e;
        }

    }

    /**
     * @param worksheet1
     * @param col
     * @param row
     * @param p_hashMap
     * @throws Exception
     */
    private void writeInDataSheet(WritableSheet worksheet1, int col, int row, HashMap p_hashMap) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeInDataSheet", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeInDataSheet";
        try {
            String keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.initiate.heading", (String) p_hashMap.get(PretupsI.BATCH_USR_DOMAIN_NAME));
            String comment = null;
            Label label = new Label(col, row, keyName, times12format);
            worksheet1.mergeCells(col, row, col + 10, row);
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.header.downloadedby");
            label = new Label(col, row, keyName, times16format);
            worksheet1.addCell(label);

            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.BATCH_USR_CREATED_BY));
            worksheet1.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.header.domainname");
            label = new Label(col, row, keyName, times16format);
            worksheet1.addCell(label);

            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.BATCH_USR_DOMAIN_NAME));
            worksheet1.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.header.geographyname");
            label = new Label(col, row, keyName, times16format);
            worksheet1.addCell(label);

            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.BATCH_USR_GEOGRAPHY_NAME));
            worksheet1.mergeCells(col, row, col + 5, row);
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.strar");
            label = new Label(++col, row, keyName);
            worksheet1.mergeCells(col, row, col + 5, row);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.doublestrar");
            label = new Label(col + 6, row, keyName);
            worksheet1.mergeCells(col + 6, row, col + 15, row);
            worksheet1.addCell(label);

            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.username");
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.username.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.mobilenumber");
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.mobilenumberprisec.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // /setting for the vertical freeze panes
            SheetSettings sheetSetting = new SheetSettings();
            sheetSetting = worksheet1.getSettings();
            sheetSetting.setVerticalFreeze(row + 1);

            // /setting for the horizontal freeze panes
            SheetSettings sheetSetting1 = new SheetSettings();
            sheetSetting1 = worksheet1.getSettings();
            sheetSetting1.setHorizontalFreeze(6);

        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeInDataSheet", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeInDataSheet", " Exception e: " + e.getMessage());
            throw e;
        }
    }



    /**
     * Method writeGroupRoleCode
     * This method writes the GeographyList
     * 
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return row
     * @author sanjeew.kumar
     */
    private int writeGeographyListingForUpdate(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeGeographyListingForUpdate", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeGeographyListingForUpdate";
        try {
            String keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.avaliablegeographieslist");
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.avaliablegeographieslist.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            // Logic for generating headings
            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_GEOGRAPHY_LIST);
            UserGeographiesVO userGeographiesVO = null;
            ArrayList geoDomainTypeList = new ArrayList();

            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    userGeographiesVO = (UserGeographiesVO) list.get(i);
                    if (!geoDomainTypeList.contains(userGeographiesVO.getGraphDomainType())) {
                        geoDomainTypeList.add(userGeographiesVO.getGraphDomainType());
                    }
                }
            }
            // Generate Headings from the ArrayList
            String endTagCode = p_messages.getMessage(p_locale, "code");
            String endTagName = p_messages.getMessage(p_locale, "name");

            String geoType = null;
            for (int i = 0, j = geoDomainTypeList.size(); i < j; i++) {
                geoType = ((String) geoDomainTypeList.get(i)).trim();
                keyName = p_messages.getMessage(p_locale, geoType);
                label = new Label(col, row, keyName + " " + endTagCode + "(" + geoType + ")", times16format);
                worksheet2.addCell(label);
                label = new Label(++col, row, keyName + " " + endTagName, times16format);
                worksheet2.addCell(label);
                col++;
            }
            row++;
            col = 0;
            int nameOccurance = 0;
            int oldseqNo = 0;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {

                    userGeographiesVO = (UserGeographiesVO) list.get(i);
                    if (oldseqNo > userGeographiesVO.getGraphDomainSequenceNumber()) {
                        nameOccurance--;
                    } else if (oldseqNo < userGeographiesVO.getGraphDomainSequenceNumber()) {
                        nameOccurance++;
                    }

                    // col=nameOccurance+userGeographiesVO.getGraphDomainSequenceNumber()-2;
                    col = 0;
                    if (userGeographiesVO.getGraphDomainSequenceNumber() == 2) {
                        col = 0;
                        nameOccurance = 0;
                    }
                    label = new Label(col, row, userGeographiesVO.getGraphDomainCode());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, userGeographiesVO.getGraphDomainName());
                    worksheet2.addCell(label);

                    oldseqNo = userGeographiesVO.getGraphDomainSequenceNumber();
                    row++;
                }
            }

            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeGeographyListingForUpdate", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeGeographyListingForUpdate", " Exception e: " + e.getMessage());
            throw e;
        }
    }

    /**
     * readMultipleExcelSheet
     * This method will upload the exel sheet in to the database
     * 
     * @param String
     *            p_excelID
     * @param String
     *            p_fileName
     * @param boolean p_readLastSheet
     * @param int p_leftHeaderLinesForEachSheet
     * @param _map
     * @return String[][] strArr
     * @throws Exception
     */
    public String[][] readMultipleExcelSheet(String p_excelID, String p_fileName, boolean p_readLastSheet, int p_leftHeaderLinesForEachSheet, HashMap<String, String> map) {
        if (_log.isDebugEnabled()) {
            _log.debug("readMultipleExcelSheet", " p_excelID: " + p_excelID + " p_fileName: " + p_fileName + " p_readLastSheet=" + p_readLastSheet, " p_leftHeaderLinesForEachSheet=" + p_leftHeaderLinesForEachSheet);
        }
        final String METHOD_NAME = "readMultipleExcelSheet";
        String strArr[][] = null;
        int arrRow = p_leftHeaderLinesForEachSheet;
        Workbook workbook = null;
        Sheet excelsheet = null;
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
                    for (int col = 0; col < noOfcols; col++)

                    {
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
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("readMultipleExcelSheet", " Exception e: " + e.getMessage());
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
                _log.debug("readMultipleExcelSheet", " Exiting strArr: " + strArr);
            }
        }
        return strArr;
    }

    /*public static void main(String args[]) {
        final String METHOD_NAME = "main";
        BatchUserCreationExcelRW excel = new BatchUserCreationExcelRW();

        try {
            HashMap hashMap = null;
            excel.writeExcel("TEST", hashMap, null, null, "C:\\Sandeep.xls");

        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
    }*/

    /**
     * Write Multiple Excel without headings
     * 
     * @param p_excelID
     * @param p_strArr
     * @param p_messages
     * @param p_locale
     * @param p_fileName
     * @author ankur.dhawan
     * @throws Exception
     */
    public void writeMultipleExcel(String p_excelID, String[][] p_strArr, MessageResources p_messages, Locale p_locale, String p_fileName) {

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
                noOfTotalSheet =BTSLUtil.parseDoubleToInt( Math.ceil(BTSLUtil.parseIntToDouble(totalNoOfRecords) / noOfRowsPerTemplate));
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
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeMultipleExcel", " Exception e: " + e.getMessage());
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

}

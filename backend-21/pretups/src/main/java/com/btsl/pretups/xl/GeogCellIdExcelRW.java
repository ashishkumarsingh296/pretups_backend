package com.btsl.pretups.xl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.util.MessageResources;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.xl.ExcelFileConstants;
import com.btsl.pretups.master.businesslogic.GeographicalDomainCellsVO;


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

public class GeogCellIdExcelRW {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private WritableCellFeatures cellFeatures = new WritableCellFeatures();
    private WritableFont times10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false);
    private WritableCellFormat times10format = new WritableCellFormat(times10font);
    private MessageResources p_messages = null;
    private Locale p_locale = null;


    // written by Ashutosh
    // method to populate data in excel sheets for geography-cell id mapping
    // module
    public void writeGeogCellToExcel(String p_excelID, HashMap<String, ArrayList<GeographicalDomainCellsVO>> p_hashMap, MessageResources messages, Locale locale, String p_fileName) {
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
            _log.debug("writeGeogCellToExcel", loggerValue);
        }
        final String METHOD_NAME = "writeGeogCellToExcel";
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
            row = this.writeGeogCellIds(worksheet2, col, row, p_hashMap);
            col = 0;
            row = 0;
            this.writeGeogCellInDataSheet(worksheet1, col, row, p_hashMap);
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

    // written by Ashutosh
    // method to populate data of master sheet in geography-cell id mapping
    // module
    private int writeGeogCellIds(WritableSheet worksheet2, int col, int row, HashMap<String, ArrayList<GeographicalDomainCellsVO>> p_hashMap) {
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
            _log.debug("writeGeogCellIds",loggerValue);
        }
        final String METHOD_NAME = "writeGeogCellIds";
        try {
            String keyName = p_messages.getMessage(p_locale, "cellgroup.upload.datasheet.grphcode");
            Label label = new Label(col, row, keyName, times10format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "cellgroup.upload.datasheet.grphname");
            label = new Label(++col, row, keyName, times10format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "cellgroup.upload.datasheet.parentgrphcode");
            label = new Label(++col, row, keyName, times10format);
            worksheet2.addCell(label);

            row++;
            col = 0;

            ArrayList<GeographicalDomainCellsVO> list = (ArrayList<GeographicalDomainCellsVO>) p_hashMap.get(PretupsI.CELL_GROUP_LIST);
            GeographicalDomainCellsVO geogVO = null;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    geogVO = (GeographicalDomainCellsVO) list.get(i);
                    label = new Label(col++, row, geogVO.getGrphDomainCode());
                    worksheet2.addCell(label);
                    label = new Label(col++, row, geogVO.getGrphDomainName());
                    worksheet2.addCell(label);
                    label = new Label(col++, row, geogVO.getParentDomainCode());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeGeogCellIds", " Exception e: " + e.getMessage());
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeGeogCellIds", " Exception e: " + e.getMessage());
        }
        return row;
    }

    // written by Ashutosh
    // method to populate data of template sheet in geography-cell id mapping
    // module
    private void writeGeogCellInDataSheet(WritableSheet worksheet1, int col, int row, HashMap<String, ArrayList<GeographicalDomainCellsVO>> p_hashMap) {
        if (_log.isDebugEnabled()) {
            _log.debug("writeGeogCellInDataSheet", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeGeogCellInDataSheet";
        try {

            String keyName = p_messages.getMessage(p_locale, "cellgroup.upload.datasheet.grphcellid");
            Label label = new Label(col++, row, keyName, times10format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "cellgroup.upload.datasheet.grphcellname");
            label = new Label(col++, row, keyName, times10format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "cellgroup.upload.datasheet.grphcode");
            label = new Label(col++, row, keyName, times10format);
            String comment = p_messages.getMessage(p_locale, "cellgroup.upload.datasheet.parentgrphcode.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            ArrayList<GeographicalDomainCellsVO> list = (ArrayList<GeographicalDomainCellsVO>) p_hashMap.get(PretupsI.CELL_ID_VO_LIST);
            GeographicalDomainCellsVO geogVO = null;
            if (list != null) {
                row++;
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    geogVO = (GeographicalDomainCellsVO) list.get(i);
                    label = new Label(col, row, geogVO.getCellId());
                    worksheet1.addCell(label);
                    label = new Label(++col, row, geogVO.getCellName());
                    worksheet1.addCell(label);
                    label = new Label(++col, row, geogVO.getGrphDomainCode());
                    worksheet1.addCell(label);
                    row++;
                }

            }
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeGeogCellInDataSheet", " Exception e: " + e.getMessage());
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeGeogCellInDataSheet", " Exception e: " + e.getMessage());
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
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append(" p_excelID: ");
        	loggerValue.append(p_excelID);
        	loggerValue.append(" p_fileName: ");
        	loggerValue.append(p_fileName);
            _log.debug("readExcel", loggerValue);
        }
        final String METHOD_NAME = "readExcel";
        String strArr[][] = null;
        Workbook workbook = null;
        Sheet excelsheet = null;

        try {
            workbook = Workbook.getWorkbook(new File(p_fileName));

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

    public void writeGeogCellToExcel(String excelID, HashMap<String, ArrayList<GeographicalDomainCellsVO>> hashMap, Locale locale, String fileName) {

        final String METHOD_NAME = "writeGeogCellToExcel";

        if (_log.isDebugEnabled()) {
            StringBuilder loggerValue= new StringBuilder();
            loggerValue.setLength(0);
            loggerValue.append(" excelID: ");
            loggerValue.append(excelID);
            loggerValue.append(" hashMap:");
            loggerValue.append(hashMap);
            loggerValue.append(" locale: ");
            loggerValue.append(locale);
            loggerValue.append(" fileName: ");
            loggerValue.append(fileName);
            _log.debug(METHOD_NAME, loggerValue);
        }
        WritableWorkbook workbook = null;
        WritableSheet worksheet1 = null;
        WritableSheet worksheet2 = null;
        int col = 0;
        int row = 0;

        try {
            workbook = Workbook.createWorkbook(new File(fileName));
            worksheet1 = workbook.createSheet(PretupsI.DOWNLOAD_SHEET_NAME, PretupsI.TRANS_STAGE_BEFORE_INVAL);
            worksheet2 = workbook.createSheet(PretupsI.DOWNLOAD_MASTER_SHEET_NAME, PretupsI.TRANS_STAGE_AFTER_INVAL);
            p_locale = locale;
            row = this.writeGeogCellIdsToTemplate(worksheet2, col, row, hashMap);
            col = 0;
            row = 0;
            this.writeGeogCellInDataSheetToTemplate(worksheet1, col, row, hashMap);
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
                _log.debug(METHOD_NAME, PretupsI.EXITED);
            }
        }
    }


    private int writeGeogCellIdsToTemplate(WritableSheet worksheet2, int col, int row, HashMap<String, ArrayList<GeographicalDomainCellsVO>> hashMap) {
        final String METHOD_NAME = "writeGeogCellIds";


        if (_log.isDebugEnabled()) {
            StringBuilder loggerValue= new StringBuilder();
            loggerValue.setLength(0);
            loggerValue.append(" hashMap size=");
            loggerValue.append(hashMap.size());
            loggerValue.append(" locale: ");
            loggerValue.append(p_locale);
            loggerValue.append(" col=");
            loggerValue.append(col);
            loggerValue.append(" row=");
            loggerValue.append(row);
            _log.debug(METHOD_NAME,loggerValue);
        }
        try {
            String keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.CELL_GEOGRPHY_DOMAIN_CODE ,null);

            Label label = new Label(col, row, keyName, times10format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.CELL_GEOGRPHY_DOMAIN_NAME ,null);
            label = new Label(++col, row, keyName, times10format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.CELL_GEOGRPHY_PARRENT_DOMAIN_CODE ,null);
            label = new Label(++col, row, keyName, times10format);
            worksheet2.addCell(label);

            row++;
            col = 0;

            ArrayList<GeographicalDomainCellsVO> list = (ArrayList<GeographicalDomainCellsVO>) hashMap.get(PretupsI.CELL_GROUP_LIST);
            GeographicalDomainCellsVO geogVO = null;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    geogVO = (GeographicalDomainCellsVO) list.get(i);
                    label = new Label(col++, row, geogVO.getGrphDomainCode());
                    worksheet2.addCell(label);
                    label = new Label(col++, row, geogVO.getGrphDomainName());
                    worksheet2.addCell(label);
                    label = new Label(col++, row, geogVO.getParentDomainCode());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME, " Exception e: " + e.getMessage());
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME, " Exception e: " + e.getMessage());
        }
        return row;
    }


    private  void writeGeogCellInDataSheetToTemplate(WritableSheet worksheet1, int col, int row, HashMap<String, ArrayList<GeographicalDomainCellsVO>> hashMap) {
        final String METHOD_NAME = "writeGeogCellInDataSheet";

        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " hashMap size=" + hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }

        try {
            String keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.CELL_GEOGRPHY_CELL_ID ,null);

            Label label = new Label(col++, row, keyName, times10format);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.CELL_GEOGRPHY_CELL_NAME ,null);
            label = new Label(col++, row, keyName, times10format);
            worksheet1.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.CELL_GEOGRPHY_DOMAIN_CODE ,null);
            label = new Label(col++, row, keyName, times10format);
            String comment = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.CELL_GEOGRPHY_CELL_COLUMN ,null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            ArrayList<GeographicalDomainCellsVO> list = (ArrayList<GeographicalDomainCellsVO>) hashMap.get(PretupsI.CELL_ID_VO_LIST);
            GeographicalDomainCellsVO geogVO = null;
            if (list != null) {
                row++;
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    geogVO = (GeographicalDomainCellsVO) list.get(i);
                    label = new Label(col, row, geogVO.getCellId());
                    worksheet1.addCell(label);
                    label = new Label(++col, row, geogVO.getCellName());
                    worksheet1.addCell(label);
                    label = new Label(++col, row, geogVO.getGrphDomainCode());
                    worksheet1.addCell(label);
                    row++;
                }

            }
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME, " Exception e: " + e.getMessage());
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME, " Exception e: " + e.getMessage());
        }
    }
}

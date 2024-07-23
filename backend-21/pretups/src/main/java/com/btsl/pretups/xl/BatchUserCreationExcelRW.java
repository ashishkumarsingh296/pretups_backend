package com.btsl.pretups.xl;

/*
 * @# BatchUserCreationExcelRW.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Ved prakash July 14, 2006 Initial creation
 * Amit Ruwali July 15,2006 Modified
 * Samna Soin November 15,2011 Modified
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2006 Bharti Telesoft Ltd.
 * This class use for read write in xls file for batch user creation.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.btsl.util.MessageResources;

import com.btsl.common.ListValueVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainTypeVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.xl.ExcelFileConstants;
import com.web.pretups.channel.user.web.BatchUserUpdateAction;

import jxl.Cell;
import jxl.CellView;
import jxl.Sheet;
import jxl.SheetSettings;
import jxl.Workbook;
import jxl.write.Alignment;
import jxl.write.Label;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * @author ved.sharma
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class BatchUserCreationExcelRW {
   private static Log _log = LogFactory.getLog(BatchUserCreationExcelRW.class.getName());
   private static  final int COLUMN_MARGE = 10;
   private WritableCellFeatures cellFeatures = new WritableCellFeatures();
   private   WritableFont times12font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, true);
   private  WritableCellFormat times12format = new WritableCellFormat(times12font);
   private  WritableFont times16font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, true);
   private  WritableCellFormat times16format = new WritableCellFormat(times16font);
   private  MessageResources p_messages = null;
   private  Locale p_locale = null;

   private  CellView lock = new CellView();

    /**
     * @param p_excelID
     * @param p_hashMap
     * @param messages
     * @param locale
     * @param p_fileName
     * @throws Exception
     */
   /* public void writeExcel(String p_excelID, HashMap p_hashMap, MessageResources messages, Locale locale, String p_fileName) {
        if (_log.isDebugEnabled()) {
            _log.debug("writeExcel", " p_excelID: " + p_excelID + " p_hashMap:" + p_hashMap + " p_locale: " + locale + " p_fileName: " + p_fileName);
        }
        final String METHOD_NAME = "writeExcel";
        WritableWorkbook workbook = null;
        WritableSheet worksheet1 = null;
        WritableSheet worksheet2 = null;
        int col = 0;
        int row = 0;
        String userType = "";
        double t_mem = 0;
        double f_mem = 0;

        try {
            t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            _log.info("writeExcel", "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));

            workbook = Workbook.createWorkbook(new File(p_fileName));
            worksheet1 = workbook.createSheet("Template Sheet", 0);
            worksheet2 = workbook.createSheet("Master Sheet", 1);
            p_messages = messages;
            p_locale = locale;
            Label label = null;
            String keyName = null;

            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.heading", (String) p_hashMap.get(PretupsI.BATCH_USR_DOMAIN_NAME));
            label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 5, row);
            worksheet2.addCell(label);
            row++;
            row++;
            col = 0;
            row = this.writeUserPrefix(worksheet2, col, row, p_hashMap);
            row++;
            col = 0;
            row = this.writeUserOutletSubOutlet(worksheet2, col, row, p_hashMap);
            row++;
            col = 0;
            row = this.writeServiceType(worksheet2, col, row, p_hashMap);
            row++;
            col = 0;
            row = this.writeGeographyListing(worksheet2, col, row, p_hashMap);
            row++;
            col = 0;
            row = this.writeCategoryHierarchy(worksheet2, col, row, p_hashMap);
            row++;
            col = 0;
            row = this.writeCategoryData(worksheet2, col, row, p_hashMap);
            row = this.writeCommissionData(worksheet2, col, row, p_hashMap);

            row++;
            col = 0;
            row = this.writeLanguageList(worksheet2, col, row, p_hashMap);

            row++;
            col = 0;
            row = this.writeGradeData(worksheet2, col, row, p_hashMap);
            userType = (String) p_hashMap.get(PretupsI.USER_TYPE);
            if ((userType != null) && (userType.equals(PretupsI.OPERATOR_USER_TYPE) || (userType.equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign))) {
                if (rsaAuthenticationRequired) {
                    row++;
                    col = 0;
                    row = this.writeRsaAssociations(worksheet2, col, row, p_hashMap);
                    row++;
                }
                if (ptupsMobqutyMergd) {
                    row++;
                    col = 0;
                    row = this.writeMpayProfID(worksheet2, col, row, p_hashMap);
                }
                // for Zebra and Tango by Sanjeew date 09/07/07

                if (isTrfRuleUserLevelAllow) {
                    row++;
                    col = 0;
                    row = this.writeTrfRuleType(worksheet2, col, row, p_hashMap);
                }
                // End Zebra and Tango
            }

            col = 0;
            row = 0;
            this.writeInDataSheet(worksheet1, col, row, p_hashMap);

            // worksheet2.setProtected(true);

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
            t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            _log.info("writeExcel", "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));
            if (_log.isDebugEnabled()) {
                _log.debug("writeExcel", " Exiting");
            }
        }
    }
*/
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
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */
    private int writeUserPrefix(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) {
        if (_log.isDebugEnabled()) {
            _log.debug("writeUserPrifix", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeUserPrefix";
        try {
            String keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.prefixheading");

            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.prefixheading.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.prefixcode");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.prefixname");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_USER_PREFIX_LIST);
            ListValueVO listValueVO = null;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) list.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeUserPrifix", " Exception e: " + e.getMessage());
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeUserPrifix", " Exception e: " + e.getMessage());
        }
        return row;
    }

    /**
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */
    private int writeUserOutletSubOutlet(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) {
        if (_log.isDebugEnabled()) {
            _log.debug("writeUserOutletSubOutlet", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeUserOutletSubOutlet";
        try {
            String keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.outletsubotletheading");

            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.outletsubotletheading.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.outletsubotlet.outletcode");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.outletsubotlet.outletname");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.outletsubotlet.suboutletcode");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.outletsubotlet.suboutletname");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            ArrayList outletList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_OUTLET_LIST);
            ArrayList suboutletList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_SUBOUTLET_LIST);
            ListValueVO listValueVO = null;
            ListValueVO listValueVOSub = null;
            if (outletList != null) {
                for (int i = 0, j = outletList.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) outletList.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    if (suboutletList != null) {
                        for (int k = 0, l = suboutletList.size(); k < l; k++) {
                            listValueVOSub = (ListValueVO) suboutletList.get(k);
                            String sub[] = listValueVOSub.getValue().split(":");
                            if (listValueVO.getValue().equals(sub[1])) {
                                label = new Label(col + 2, row, sub[0]);
                                worksheet2.addCell(label);
                                label = new Label(col + 3, row, listValueVOSub.getLabel());
                                worksheet2.addCell(label);
                                row++;
                            }
                        }
                    }
                    row++;
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeUserOutletSubOutlet", " Exception e: " + e.getMessage());
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeUserOutletSubOutlet", " Exception e: " + e.getMessage());
        }
        return row;
    }

    // Added by Shashank Gaur for Trf rule Authentication
    /**
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */
    private int writeTrfRuleType(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeTrfRuleType", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeTrfRuleType";
        try {
            String keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.trfruletypeheading");

            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.trfruletypeheading.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.trfruletype.trfruletypecode");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.trfruletype.trfruletypename");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            ArrayList outletList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_TRF_RULE_LIST);
            ListValueVO listValueVO = null;
            if (outletList != null) {
                for (int i = 0, j = outletList.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) outletList.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeTrfRuleType", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeTrfRuleType", " Exception e: " + e.getMessage());
            throw e;
        }
    }

    /**
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */
    private int writeServiceType(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeServiceType", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeServiceType";
        try {
            String keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.service");

            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.service.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.servicetype");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.servicename");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_SERVICE_LIST);
            ListValueVO listValueVO = null;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) list.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeServiceType", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeServiceType", " Exception e: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Method writeGeographyListing
     * This method writes the Geography Details containing zone,area,sub area etc. [ N level geographies can exists ]
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
                                                                                                        // proper
                                                                                                        // formatting
                                                                                                        // of
                                                                                                        // geo.
                                                                                                        // list
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
     * Method writeCategoryHierarchy
     * This method writes the Category Hierarchy list
     * 
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */
    private int writeCategoryHierarchy(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeCategoryHierarchy", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeCategoryHierarchy";
        try {
            String keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categoryhierarchy");
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categoryhierarchy.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.parentcategory");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.childcategory");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);

            row++;
            col = 0;
            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_CATEGORY_HIERARCHY_LIST);
            ChannelTransferRuleVO channelTransferRuleVO = null;
            int oldSeqNum = 0;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    channelTransferRuleVO = (ChannelTransferRuleVO) list.get(i);
                    // Parent Category will be displayed according to Seq Num
                    if (i == 0 || oldSeqNum != channelTransferRuleVO.getFromSeqNo()) {
                        label = new Label(col, row, channelTransferRuleVO.getFromCategory());
                        worksheet2.addCell(label);
                    } else {
                        label = new Label(col, row, "");
                        worksheet2.addCell(label);
                    }
                    label = new Label(col + 1, row, channelTransferRuleVO.getToCategory());
                    worksheet2.addCell(label);
                    oldSeqNum = channelTransferRuleVO.getFromSeqNo();
                    row++;
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeCategoryHierarchy", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeCategoryHierarchy", " Exception e: " + e.getMessage());
            throw e;
        }

    }

    /**
     * Method writeCategoryData
     * This method writes the category details including
     * geographies,grade,commision profile,transfer
     * profile & group information
     * 
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */
    private int writeCategoryData(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        int maxrow = row;
        if (_log.isDebugEnabled()) {
            _log.debug("writeCategoryData", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        boolean batchUserProfileAssign = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.BATCH_USER_PROFILE_ASSIGN);
        String keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categoryheading");
        Label label = new Label(col, row, keyName, times16format);
        worksheet2.mergeCells(col, row, col + 2, row);
        worksheet2.addCell(label);
        row++;
        col = 0;
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categoryheading.note");
        label = new Label(col, row, keyName);
        worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
        worksheet2.addCell(label);
        row++;
        col = 0;
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categorycode");
        label = new Label(col, row, keyName, times16format);
        worksheet2.addCell(label);
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categoryname");
        label = new Label(++col, row, keyName, times16format);
        worksheet2.addCell(label);

        // for Zebra and Tango by Sanjeew date 09/07/07
        // For Low balance alert allow
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.lowbalalertallow");
        label = new Label(++col, row, keyName, times16format);
        worksheet2.addCell(label);
        // End Zebra and Tango

        /*
         * keyName = p_messages.getMessage(p_locale,
         * "batchusercreation.mastersheet.webaccessallowed");
         * label = new Label(++col,row,keyName, times16format);
         * 
         * worksheet2.addCell(label);
         * keyName = p_messages.getMessage(p_locale,
         * "batchusercreation.mastersheet.mobileaccessallowed");
         * label = new Label(++col,row,keyName, times16format);
         * worksheet2.addCell(label);
         */
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grphdomaintype");
        label = new Label(++col, row, keyName, times16format);
        worksheet2.mergeCells(col, row, col + 1, row);
        worksheet2.addCell(label);

        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grphdomaincode");
        label = new Label(col, row + 1, keyName, times16format);
        worksheet2.addCell(label);
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grphdomainname");
        label = new Label(++col, row + 1, keyName, times16format);
        worksheet2.addCell(label);

        if (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.OPERATOR_USER_TYPE) || (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {

            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.transfercontrolprf");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 1, row);
            worksheet2.addCell(label);

            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.transfercontrolprfcode");
            label = new Label(col, row + 1, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.transfercontrolprfname");
            label = new Label(++col, row + 1, keyName, times16format);
            worksheet2.addCell(label);
        }

        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grouprole");
        label = new Label(++col, row, keyName, times16format);
        worksheet2.mergeCells(col, row, col + 1, row);
        worksheet2.addCell(label);
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grouprolecode");
        label = new Label(col, row + 1, keyName, times16format);
        worksheet2.addCell(label);
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grouprolename");
        label = new Label(++col, row + 1, keyName, times16format);
        worksheet2.addCell(label);

        row++;
        col = 0;

        // Iteration Starts from Row number
        row++;
        ArrayList categoryList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_CATEGORY_LIST);
        ArrayList geographyList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_GEOGRAPHY_TYPE_LIST);
        ArrayList gradeList = null;
        ArrayList transferProfileList = null;
        ArrayList commPrfList = null;
        if (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.OPERATOR_USER_TYPE) || (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {
            gradeList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_GRADE_LIST);
            transferProfileList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_TRANSFER_CONTROL_PRF_LIST);
            commPrfList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_COMMISION_PRF_LIST);
        }
        ArrayList grpRoleList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_GROUP_ROLE_LIST);

        int catSize = 0;
        int tempCol = 0;
        int tempRow = 0;
        // Change for Batch User Initiate by Channel users
        int colIncrement = 0;
        int geoListSize = 0;
        int gradeListSize = 0;
        int trfPrfRowSize, transferPrfListSize = 0;
        int commPrfListSize = 0;
        int groupListSize = 0;
        int maxRow = 0;

        CategoryVO categoryVO = null;
        GeographicalDomainTypeVO geographicalDomainTypeVO = null;
        TransferProfileVO profileVO = null;
        UserRolesVO rolesVO = null;

        // Equate The Sizes of the list first so that it will not be checked in
        // the loop
        if (geographyList != null) {
            geoListSize = geographyList.size();
        }
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

        if (categoryList != null && (catSize = categoryList.size()) > 0) {
            for (int i = 0; i < catSize; i++) // Prints One Row Of Category
            {
                col = 0;
                colIncrement = 0;
                categoryVO = (CategoryVO) categoryList.get(i);
                label = new Label(col, row, categoryVO.getCategoryCode());
                worksheet2.addCell(label);
                label = new Label(++col, row, categoryVO.getCategoryName());
                worksheet2.addCell(label);

                // for Zebra and Tango by Sanjeew date 09/07/07
                // For Low balance alert allow
                label = new Label(++col, row, categoryVO.getLowBalAlertAllow());
                worksheet2.addCell(label);
                // End Zebra and Tango

                /*
                 * label = new
                 * Label(++col,row,categoryVO.getWebInterfaceAllowed());
                 * worksheet2.addCell(label);
                 * label = new
                 * Label(++col,row,categoryVO.getSmsInterfaceAllowed());
                 * worksheet2.addCell(label);
                 */
                // Now iterate Geographical domain type
                tempRow = row;
                maxrow = row;
                for (int j = 0; j < geoListSize; j++) {
                    tempCol = col;
                    geographicalDomainTypeVO = (GeographicalDomainTypeVO) geographyList.get(j);
                    if (geographicalDomainTypeVO.getCategoryCode().equals(categoryVO.getCategoryCode())) {
                        label = new Label(++tempCol, tempRow, geographicalDomainTypeVO.getGrphDomainType());
                        worksheet2.addCell(label);
                        label = new Label(++tempCol, tempRow, geographicalDomainTypeVO.getGrphDomainTypeName());
                        worksheet2.addCell(label);
                        tempRow++;
                    }
                }

                maxrow = tempRow;
                tempRow = row;

                if (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.OPERATOR_USER_TYPE) || (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {

                    // Now maxRow will contains the greatest value among the two
                    colIncrement += 2;
                    for (int l = 0; l < transferPrfListSize; l++) {
                        tempCol = col + colIncrement;
                        profileVO = (TransferProfileVO) transferProfileList.get(l);
                        if (profileVO.getCategory().equals(categoryVO.getCategoryCode())) {
                            label = new Label(++tempCol, tempRow, profileVO.getProfileId());
                            worksheet2.addCell(label);
                            label = new Label(++tempCol, tempRow, profileVO.getProfileName());
                            worksheet2.addCell(label);
                            tempRow++;
                        }
                    }
                    maxrow = tempRow;
                    trfPrfRowSize = tempRow;
                    tempRow = row;

                    if (trfPrfRowSize > maxRow) {
                        maxRow = trfPrfRowSize;
                    }

                    colIncrement += 2;

                    for (int n = 0; n < groupListSize; n++) {
                        tempCol = col + colIncrement;
                        rolesVO = (UserRolesVO) grpRoleList.get(n);
                        if (rolesVO.getCategoryCode().equals(categoryVO.getCategoryCode())) {
                            label = new Label(++tempCol, tempRow, rolesVO.getRoleCode());
                            worksheet2.addCell(label);
                            label = new Label(++tempCol, tempRow, rolesVO.getRoleName());
                            worksheet2.addCell(label);
                            tempRow++;
                        }
                    }

                    maxrow = tempRow;
                    if (tempRow > maxRow) {
                        maxRow = tempRow;
                    }
                    // Max size of ROWS according to the data
                    row = maxRow;
                    colIncrement = 0;
                }
            }

        }
        return maxrow;
    }

    private int writeMpayProfID(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeMpayProfID", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeMpayProfID";
        try {
            String keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.mpayprofiledetails");
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.gradecode");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.mpayprofileid");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.mpayprofileiddesc");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);

            row++;
            col = 0;
            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.M_PAY_PROFILE_LIST);
            ListValueVO listValueVO = null;
            String strvalue = null;
            String mpayIDName = null;
            String usrGrd = null;
            ArrayList usrGrdList = new ArrayList();

            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) list.get(i);
                    mpayIDName = listValueVO.getLabel();
                    strvalue = listValueVO.getValue();
                    usrGrd = strvalue.split(":")[1];
                    if (usrGrdList.size() > 0 && usrGrdList.contains(usrGrd)) {
                        label = new Label(++col, row, strvalue.split(":")[2]);
                        worksheet2.addCell(label);
                        label = new Label(++col, row, mpayIDName);
                        worksheet2.addCell(label);
                    } else {
                        usrGrdList.add(usrGrd);
                        label = new Label(col, row, usrGrd);
                        worksheet2.addCell(label);
                        label = new Label(++col, row, strvalue.split(":")[2]);
                        worksheet2.addCell(label);
                        label = new Label(++col, row, mpayIDName);
                        worksheet2.addCell(label);
                    }
                    row++;
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeMpayProfID", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeMpayProfID", " Exception e: " + e.getMessage());
            throw e;
        }
    }

    // Added by Shashank Gaur for RSA Authentication
    private int writeRsaAssociations(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeRSAAllowed", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeRsaAssociations";
        try {
            String keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.rsaheading");
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.rsaheading.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categorycode");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.isrsaallowed");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            ArrayList categoryList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_CATEGORY_LIST);
            CategoryVO categoryVO;
            int catSize = 0;
            if (categoryList != null) {
                catSize = categoryList.size();
            }

            if (categoryList != null && (catSize = categoryList.size()) > 0) {
                for (int i = 0; i < catSize; i++) // Prints One Row Of Category
                {
                    col = 0;
                    categoryVO = (CategoryVO) categoryList.get(i);
                    label = new Label(col, row, categoryVO.getCategoryName());
                    worksheet2.addCell(label);
                    Boolean rsarequired = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.RSA_AUTHENTICATION_REQUIRED, "TG", categoryVO.getCategoryCode())).booleanValue();
                    if (rsarequired == true) {
                        label = new Label(++col, row, PretupsI.YES);
                    } else {
                        label = new Label(++col, row, PretupsI.NO);
                    }
                    worksheet2.addCell(label);
                    row++;
                    col = 0;
                }
            }
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeRsaAssociations", " Exception e: " + e.getMessage());
            throw e;
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
    private void writeInDataSheet(WritableSheet worksheet1, int col, int row, HashMap p_hashMap) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeInDataSheet", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        boolean isFnameLnameAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_FNAME_LNAME_ALLOWED);
        boolean batchUserProfileAssign = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.BATCH_USER_PROFILE_ASSIGN);
        boolean rsaAuthenticationRequired = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.RSA_AUTHENTICATION_REQUIRED);
        boolean ptupsMobqutyMergd = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD);
        boolean externalCodeMandatoryForUser = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_CODE_MANDATORY_FORUSER);
        boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
        boolean authTypeReq = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTH_TYPE_REQ);
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
            /**
             * keyName = p_messages.getMessage(p_locale,
             * "bulkuser.xlsfile.details.parentloginid");
             * label = new Label(col++,row,keyName, times16format);
             * //add comment
             * comment=p_messages.getMessage(p_locale,
             * "bulkuser.xlsfile.details.parentloginid.comment");
             * cellFeatures=new WritableCellFeatures();
             * cellFeatures.setComment(comment);
             * label.setCellFeatures(cellFeatures);
             * worksheet1.addCell(label);
             **/

            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.parentmsisdn");
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.parentmsisdn.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

             if(!BTSLUtil.isNullString(Constants.getProperty("IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES"))){
            	if(PretupsI.YES.equalsIgnoreCase(Constants.getProperty("IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES")))
            		keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.usernameprefixnotmandatory");
		else
            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.usernameprefix");
            }
            else{
            	keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.usernameprefix");
            }
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.usernameprefix.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);
            // Modified by deepika aggarwal
            if (!isFnameLnameAllowed) {
                keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.username");
                label = new Label(col++, row, keyName, times16format);
                // add comment
                comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.username.comment");
                cellFeatures = new WritableCellFeatures();
                cellFeatures.setComment(comment);
                label.setCellFeatures(cellFeatures);
                worksheet1.addCell(label);
            } else {
                keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.firstname");
                label = new Label(col++, row, keyName, times16format);
                // add comment
                comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.firstname.comment");
                cellFeatures = new WritableCellFeatures();
                cellFeatures.setComment(comment);
                label.setCellFeatures(cellFeatures);
                worksheet1.addCell(label);

                keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.lastname");
                label = new Label(col++, row, keyName, times16format);
                // add comment
                comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.lastname.comment");
                cellFeatures = new WritableCellFeatures();
                cellFeatures.setComment(comment);
                label.setCellFeatures(cellFeatures);
                worksheet1.addCell(label);

            }

            // end Modified by deepika aggarwal

            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.shortname");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.categorycode");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.categorycode.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);
            // Diwakar on 03-MAY-2014 OCM
            if (externalCodeMandatoryForUser) {
                keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.externalcode.star");
            } else {
                keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.externalcode");
            }
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.contactperson");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.address1");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.city");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.state");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.ssn");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.ssn.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.country");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            // Added by deepika aggarwal
            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.company");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.fax");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);
            String userCreationMandatoryFields = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_CREATION_MANDATORY_FIELDS);
            if (BTSLUtil.isStringContain(userCreationMandatoryFields, "email")) {
                keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.email.star");
            } else {
                keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.email");
            }
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            // ***********************Added By Deepika Aggarwal**************
            if(!BTSLUtil.isNullString(Constants.getProperty("IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES"))){
            	if(PretupsI.YES.equalsIgnoreCase(Constants.getProperty("IS_DEFAULTVALUE_ALLOWED_IN_BATCHUSER_MODULES")))
            		keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.languagenotmandatory");
		else
            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.language");
            }
            else{
            	keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.language");
            }       
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.language.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);
            // end added by deepika aggarwal
            boolean loginPasswordAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED);
            if (loginPasswordAllowed) {
                keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.loginid");
            } else {
                keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.loginid.new");
            }
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.loginid.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

           /* // Diwakar on 03-MAY-2014 OCM
            if (loginPasswordAllowed) {
                keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.password.star");
            } else {
                keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.password");
            }*/
            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.password");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.mobilenumber");
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.mobilenumberprisec.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);
            boolean autoPinGenerateAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PIN_GENERATE_ALLOW);
            // Diwakar on 03-MAY-2014 OCM
            if (!autoPinGenerateAllow) {
                keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.pin.star");
            } else {
                keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.pin");
            }
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.geographycode");
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.geographycode.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.grouprolecode");
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.grouprolecode.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.services");
            label = new Label(col++, row, keyName, times16format);
            // /add comment
            comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.services.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.outlet");
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.outlet.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.suboutletcode");
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.suboutletcode.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            if (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.OPERATOR_USER_TYPE) || (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {
                keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.commisionprofile");
                label = new Label(col++, row, keyName, times16format);
                // add comment
                comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.commisionprofile.comment");
                cellFeatures = new WritableCellFeatures();
                cellFeatures.setComment(comment);
                label.setCellFeatures(cellFeatures);
                worksheet1.addCell(label);

                keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.transferprofile");
                label = new Label(col++, row, keyName, times16format);
                // add comment
                comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.transferprofile.comment");
                cellFeatures = new WritableCellFeatures();
                cellFeatures.setComment(comment);
                label.setCellFeatures(cellFeatures);
                worksheet1.addCell(label);

                // for Zebra and Tango by Sanjeew date 09/07/07

                keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.grade");
                label = new Label(col++, row, keyName, times16format);
                // add comment
                comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.grade.comment");
                cellFeatures = new WritableCellFeatures();
                cellFeatures.setComment(comment);
                label.setCellFeatures(cellFeatures);
                worksheet1.addCell(label);

                if (ptupsMobqutyMergd) {
                    keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.mcomorceflag");
                    label = new Label(col++, row, keyName, times16format);
                    // add comment
                    comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.mcomorceflag.comment");
                    cellFeatures = new WritableCellFeatures();
                    cellFeatures.setComment(comment);
                    label.setCellFeatures(cellFeatures);
                    worksheet1.addCell(label);

                    keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.mpayprofileid");
                    label = new Label(col++, row, keyName, times16format);
                    // add comment
                    comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.mpayprofileid.comment");
                    cellFeatures = new WritableCellFeatures();
                    cellFeatures.setComment(comment);
                    label.setCellFeatures(cellFeatures);
                    worksheet1.addCell(label);
                }

                if (isTrfRuleUserLevelAllow) {
                    keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.trfruletypecode");
                    label = new Label(col++, row, keyName, times16format);
                    // add comment
                    comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.trfruletypecode.comment");
                    cellFeatures = new WritableCellFeatures();
                    cellFeatures.setComment(comment);
                    label.setCellFeatures(cellFeatures);
                    worksheet1.addCell(label);
                }

                if (rsaAuthenticationRequired) {
                    // For RSA Authentication
                    keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.rsaauthentication");
                    label = new Label(col++, row, keyName, times16format);
                    comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.rsaauthentication.comment");
                    cellFeatures = new WritableCellFeatures();
                    cellFeatures.setComment(comment);
                    label.setCellFeatures(cellFeatures);
                    worksheet1.addCell(label);
                }
                if (authTypeReq) {

                    keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.authtypeallowed");
                    label = new Label(col++, row, keyName, times16format);
                    comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.authtypeallowed.comment");
                    cellFeatures = new WritableCellFeatures();
                    cellFeatures.setComment(comment);
                    label.setCellFeatures(cellFeatures);
                    worksheet1.addCell(label);
                }

            }

            // for Low balance alert
            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.lowbalalertallow");
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.lowbalalertallow.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);
            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.longitude");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.longitude.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.latitude");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.latitude.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);
            
            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.documenttype");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.documenttype.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);
            
            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.documentno");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.documentno.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);
            
            keyName = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.paymenttype");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "bulkuser.xlsfile.details.paymenttype.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

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
     * @Method Name writeModifyExcel
     * @author sanjeew.kumar
     * @param p_excelID
     * @param p_hashMap
     * @param messages
     * @param locale
     * @param p_fileName
     * @throws Exception
     */
/*    public void writeModifyExcel(String p_excelID, HashMap p_hashMap, MessageResources messages, Locale locale, String p_fileName) {
        if (_log.isDebugEnabled()) {
            _log.debug("writeModifyExcel", " p_excelID: " + p_excelID + " p_hashMap:" + p_hashMap + " p_locale: " + locale + " p_fileName: " + p_fileName);
        }
        final String METHOD_NAME = "writeModifyExcel";
        WritableWorkbook workbook = null;
        WritableSheet worksheet1 = null;
        WritableSheet worksheet2 = null;
        int col = 0;
        int row = 0;
        String noOfRowsInOneTemplate = null; // No. of users data in one sheet
        int noOfTotalSheet = 0;
        int userListSize = 0;
        ArrayList batchUserList = null;
        try {
            p_messages = messages;
            p_locale = locale;
            Label label = null;
            String keyName = null;

            workbook = Workbook.createWorkbook(new File(p_fileName));

            batchUserList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_EXCEL_DATA);
            userListSize = batchUserList.size();
            noOfRowsInOneTemplate = Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE_BATCHUSER");
            int noOfRowsPerTemplate = 0;
            if (!BTSLUtil.isNullString(noOfRowsInOneTemplate)) {
                noOfRowsInOneTemplate = noOfRowsInOneTemplate.trim();
                noOfRowsPerTemplate = Integer.parseInt(noOfRowsInOneTemplate);
            } else {
                noOfRowsPerTemplate = 65500; // Default value of rows
            }
            // Number of sheet to display the user list
            noOfTotalSheet = (int) Math.ceil((double) userListSize / noOfRowsPerTemplate);

            if (noOfTotalSheet > 1) {
                int i = 0;
                int k = 0;
                ArrayList tempList = new ArrayList();
                for (i = 0; i < noOfTotalSheet; i++) {
                    tempList.clear();
                    if (k + noOfRowsPerTemplate < userListSize) {
                        for (int j = k; j < k + noOfRowsPerTemplate; j++) {
                            tempList.add(batchUserList.get(j));
                        }
                    } else {
                        for (int j = k; j < userListSize; j++) {
                            tempList.add(batchUserList.get(j));
                        }
                    }
                    p_hashMap.put(PretupsI.BATCH_USR_EXCEL_DATA, tempList);
                    worksheet1 = workbook.createSheet("Template Sheet " + (i + 1), i);
                    this.writeModifyInDataSheet(worksheet1, col, row, p_hashMap);
                    k = k + noOfRowsPerTemplate;
                }
                worksheet2 = workbook.createSheet("Master Sheet", i);
                p_hashMap.put(PretupsI.BATCH_USR_EXCEL_DATA, batchUserList);
            }

            else {
                worksheet1 = workbook.createSheet("Template Sheet", 0);
                this.writeModifyInDataSheet(worksheet1, col, row, p_hashMap);
                worksheet2 = workbook.createSheet("Master Sheet", 1);
            }

            CategoryVO categoryVO = (CategoryVO) p_hashMap.get(PretupsI.BATCH_USR_CATEGORY_VO);

            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.heading", p_hashMap.get(PretupsI.BATCH_USR_DOMAIN_NAME));
            label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 5, row);
            worksheet2.addCell(label);
            row++;
            row++;
            col = 0;
            row = this.writeUserPrefix(worksheet2, col, row, p_hashMap);
            row++;
            col = 0;
            row = this.writeGeographyListingForUpdate(worksheet2, col, row, p_hashMap);
            // *******************Added by deepika Aggarwal*******************
            row++;
            col = 0;
            row = this.writeLanguageList(worksheet2, col, row, p_hashMap);
            // if outlet allowed
            if (categoryVO.getOutletsAllowed().equals(PretupsI.YES)) {
                row++;
                col = 0;
                row = this.writeUserOutletSubOutlet(worksheet2, col, row, p_hashMap);
            }
            // If Service Allowed
            if (categoryVO.getServiceAllowed().equals(PretupsI.YES)) {
                row++;
                col = 0;
                row = this.writeServiceType(worksheet2, col, row, p_hashMap);
            }
            // if webaccess allowed
            if (categoryVO.getWebInterfaceAllowed().equals(PretupsI.YES)) {
                row++;
                col = 0;
                row = this.writeRoleCode(worksheet2, col, row, p_hashMap);
                row++;
                col = 0;
                row = this.writeGroupRoleCode(worksheet2, col, row, p_hashMap);
            }
            if (rsaAuthenticationRequired) {
                // Added for RSA Authentication June 2011 by Ankur
                row++;
                col = 0;
                row = this.writeRsaAssociations(worksheet2, col, row, p_hashMap);
            }
            // Added for OTP
            if (authTypeReq) {
                row++;
                col = 0;
            }
            // Writing Transfer Rule Type Details in Master sheet
            if (isTrfRuleUserLevelAllow) {
                row++;
                col = 0;
                row = this.writeTrfRuleType(worksheet2, col, row, p_hashMap);
            }
            if (lmsAppl) {
                row++;
                col = 0;
                row = this.writeLmsProfile(worksheet2, col, row, p_hashMap);
            }
            // worksheet2.setProtected(true);

            workbook.write();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeModifyExcel", " Exception e: " + e.getMessage());
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
                _log.debug("writeModifyExcel", " Exiting");
            }
        }
    }
*/


    /**
     * Method writeRoleCode
     * This method writes the Role code and Role code Name
     * 
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return row
     * @throws Exception
     * @author sanjeew.kumar
     */
    private int writeRoleCode(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeRoleCode", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeRoleCode";
        try {
            String keyName = p_messages.getMessage(p_locale, "bulkuser.modify.mastersheet.systemrolecodedetails");
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "bulkuser.modify.mastersheet.rolecode");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "bulkuser.modify.mastersheet.rolename");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;
            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_ROLE_CODE_LIST);
            UserRolesVO rolesVO = null;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    rolesVO = (UserRolesVO) list.get(i);

                    label = new Label(col, row, rolesVO.getRoleCode());
                    worksheet2.addCell(label);

                    label = new Label(col + 1, row, rolesVO.getRoleName());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeRoleCode", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeRoleCode", " Exception e: " + e.getMessage());
            throw e;
        }

    }

    /**
     * Method writeGroupRoleCode
     * This method writes the Group Role code and Group Role code Name
     * 
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return row
     * @throws Exception
     * @author sanjeew.kumar
     */
    private int writeGroupRoleCode(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeGroupRoleCode", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeGroupRoleCode";
        try {
            String keyName = p_messages.getMessage(p_locale, "bulkuser.modify.mastersheet.grouprolecodedetails");
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "bulkuser.modify.mastersheet.rolecode");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "bulkuser.modify.mastersheet.rolename");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;
            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_GROUP_ROLE_LIST);
            UserRolesVO rolesVO = null;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    rolesVO = (UserRolesVO) list.get(i);

                    label = new Label(col, row, rolesVO.getRoleCode());
                    worksheet2.addCell(label);

                    label = new Label(col + 1, row, rolesVO.getRoleName());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeGroupRoleCode", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeGroupRoleCode", " Exception e: " + e.getMessage());
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

    /**
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */
    // ADDED BY DEEPIKA AGGARWAL
    private int writeLanguageList(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeLanguageList", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeLanguageList";
        try {
            String keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.Languageheading");

            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.languageheading.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            HashMap map = (HashMap) p_hashMap.get(PretupsI.BATCH_USR_LANGUAGE_LIST);

            if (map.size() > 0) {
                Set set = map.entrySet();
                Iterator i = set.iterator();
                while (i.hasNext()) {
                    Map.Entry me = (Map.Entry) i.next();
                    String key = (String) me.getKey();
                    col = 0;
                    label = new Label(col, row, key);
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, String.valueOf(me.getValue()));
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeLanguageList", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeLanguageList", " Exception e: " + e.getMessage());
            throw e;
        }
    }

    /**
     * @param p_excelID
     * @param p_hashMap
     * @param messages
     * @param locale
     * @param p_fileName
     * @throws Exception
     */
    public void writeExcelForApproval(String p_excelID, String[][] p_strArr, String[][] p_headerArray, String p_heading, int p_margeCont, HashMap p_hashMap, MessageResources messages, Locale locale, String p_fileName) {
    	final String METHOD_NAME = "writeExcelForApproval";        
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " p_excelID: " + p_excelID + " p_strArr:" + p_strArr + " p_headerArray=" + p_headerArray + " p_heading=" + p_heading + " p_margeCont=" + p_margeCont + " p_locale: " + p_locale + " p_strArr length: " + p_strArr.length + " p_fileName: " + p_fileName);
        }
        boolean batchUserProfileAssign = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.BATCH_USER_PROFILE_ASSIGN);
        boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
        boolean rsaAuthenticationRequired = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.RSA_AUTHENTICATION_REQUIRED);
        boolean userVoucherTypeAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED);
        boolean ptupsMobqutyMergd = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD);
        WritableWorkbook workbook = null;
        WritableSheet worksheet1 = null;
        WritableSheet worksheet2 = null;
        int row = 0;
        int col = 0;
        p_messages = messages;
        p_locale = locale;
        double t_mem = 0;
        double f_mem = 0;
        // Added by Ankur for Batch Excel feature GT
        String noOfRecordsPerTemplateStr;
        int noOfRecordsPerTemplate = 0, noOfSheets = 0, totalNoOfRecords = 0;
        try {
            t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            _log.info("writeExcelForApproval", "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));

            String fileName = p_fileName;
            workbook = Workbook.createWorkbook(new File(fileName));

            String key = null;
            String keyName = null;
            Label label = null;

            WritableFont times16font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, true);
            WritableCellFormat times16format = new WritableCellFormat(times16font);

            WritableFont data = new WritableFont(WritableFont.ARIAL, 11, WritableFont.NO_BOLD, true);
            WritableCellFormat dataFormat = new WritableCellFormat(data);

            WritableFont times20font = new WritableFont(WritableFont.ARIAL, 20, WritableFont.BOLD, true);
            WritableCellFormat times20format = new WritableCellFormat(times20font);
            times20format.setAlignment(Alignment.CENTRE);

            // Added by Ankur for Batch Excel feature GT
            noOfRecordsPerTemplateStr = Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE_BATCHUSER");

            if (!BTSLUtil.isNullString(noOfRecordsPerTemplateStr)) {
                noOfRecordsPerTemplate = Integer.parseInt(noOfRecordsPerTemplateStr);
            } else {
                noOfRecordsPerTemplate = 65500;
            }

            totalNoOfRecords = p_strArr.length - 1;

            noOfSheets = BTSLUtil.parseDoubleToInt( Math.ceil(BTSLUtil.parseIntToDouble(totalNoOfRecords) / noOfRecordsPerTemplate));

            int colnum = 0, cols = 0, length = 0, lenInt = 0, recordNo = 0;

            for (int i = 0; i < noOfSheets; i++) {
                worksheet1 = workbook.createSheet("Data Sheet" + (i + 1), i);

                colnum = p_strArr[0].length;
                int[] indexMapArray = new int[colnum];

                if (p_messages != null) {
                    keyName = p_messages.getMessage(p_locale, p_heading);
                } else {
                    keyName = p_heading;
                }

                label = new Label(0, 0, keyName, times20format);// Heading
                worksheet1.mergeCells(0, 0, colnum - 1, 0);
                worksheet1.addCell(label);

                cols = p_headerArray[0].length;
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
                    worksheet1.mergeCells(0, row + 1, p_margeCont, row + 1);
                    worksheet1.addCell(label);
                }
                for (row = 0; row < cols; row++)// Header Headings value
                {
                    keyName = null;
                    keyName = p_headerArray[1][row];
                    label = new Label(p_margeCont + 1, row + 1, keyName, dataFormat);
                    worksheet1.mergeCells(p_margeCont + 1, row + 1, 3 * p_margeCont, row + 1);
                    worksheet1.addCell(label);
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
                    worksheet1.addCell(label);
                }
                // setting for the vertical freeze panes
                SheetSettings sheetSetting = new SheetSettings();
                sheetSetting = worksheet1.getSettings();
                sheetSetting.setVerticalFreeze(cols + 3);

                // setting for the horizontal freeze panes
                SheetSettings sheetSetting1 = new SheetSettings();
                sheetSetting1 = worksheet1.getSettings();
                sheetSetting1.setHorizontalFreeze(6);
                if (noOfSheets > 1) {
                    for (row = 1; row <= noOfRecordsPerTemplate; row++) {
                        recordNo = row + (i * noOfRecordsPerTemplate);
                        if (recordNo <= totalNoOfRecords) {
                            lenInt = p_strArr[recordNo].length;
                            for (col = 0; col < lenInt; col++) {
                                label = new Label(indexMapArray[col], row + cols + 2, p_strArr[recordNo][col], dataFormat);
                                worksheet1.addCell(label);
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
                            worksheet1.addCell(label);
                        }
                    }
                }
            }

            col = 0;
            row = 0;
            worksheet2 = workbook.createSheet("Master Sheet", noOfSheets);

            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.heading", (String) p_hashMap.get(PretupsI.BATCH_USR_DOMAIN_NAME));
            label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 5, row);
            worksheet2.addCell(label);
            row++;
            row++;
            col = 0;
            row = this.writeUserPrefix(worksheet2, col, row, p_hashMap);
            row++;
            col = 0;
            row = this.writeUserOutletSubOutlet(worksheet2, col, row, p_hashMap);
            row++;
            col = 0;
            row = this.writeServiceType(worksheet2, col, row, p_hashMap);
            row++;
            col = 0;
            row = this.writeGeographyListing(worksheet2, col, row, p_hashMap);
            row++;
            col = 0;
            row = this.writeCategoryHierarchy(worksheet2, col, row, p_hashMap);
            row++;
            col = 0;
            row = this.writeCategoryData(worksheet2, col, row, p_hashMap);

            if (rsaAuthenticationRequired) {
                // Added for RSA Authentication June 2011 by Ankur
                row++;
                col = 0;
                row = this.writeRsaAssociations(worksheet2, col, row, p_hashMap);
                row++;
            }
            // for Zebra and Tango by Sanjeew date 09/07/07
            if (ptupsMobqutyMergd) {
                if (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.OPERATOR_USER_TYPE) || (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {
                    row++;
                    col = 0;
                    row = this.writeMpayProfID(worksheet2, col, row, p_hashMap);
                }
            }
            // for Zebra and Tango by Sanjeew date 09/07/07
            if (isTrfRuleUserLevelAllow) {
                if (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.OPERATOR_USER_TYPE) || (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {
                    row++;
                    col = 0;
                    row = this.writeTrfRuleType(worksheet2, col, row, p_hashMap);
                }
            }
            row++;
            col = 0;
            row = this.writeUserDocumentTypeList(worksheet2, col, row, p_hashMap);
            row++;
            col = 0;
            row = this.writeUserPaymentTypeList(worksheet2, col, row, p_hashMap);
            if(userVoucherTypeAllowed)
            {
            	ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_OPT_USR_VOUCHERTYPE_LIST);
            	if (list != null && list.size() > 0) {
		            row++;
		            col = 0;
		            row = this.writeVoucherListing(worksheet2, col, row, p_hashMap);
            	}
            }
            
            if (SystemPreferences.USERWISE_LOAN_ENABLE) {
            	 if (_log.isDebugEnabled())
	                    _log.debug(METHOD_NAME,"The Value of the Cell Load Id:-");
            	 
            	row++;
               col = 0;
               //row = this.writeTrfRuleType(worksheet2, col, row, p_hashMap);
               row = this.writeUserLoanProfile(worksheet2, col, row, p_hashMap);
           }
            
            workbook.write();
            
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeExcelForApproval", " Exception e: " + e.getMessage());
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
            t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            Runtime.getRuntime().gc();
            f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            _log.info("writeExcelForApproval", "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));
        }
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

    // Added by Aatif
    private int writeLmsProfile(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeLmsProfile", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeLmsProfile";
        try {
            String keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.lmsprofile");

            Label label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.lmsprofile.note");
            label = new Label(col, row, keyName);
            worksheet2.addCell(label);

            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.profileid");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.profilename");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            ArrayList lmsProfileList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_LMS_PROFILE);
            ListValueVO listValueVO = null;
            if (lmsProfileList != null) {
                for (int i = 0, j = lmsProfileList.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) lmsProfileList.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                }
            }

            /*
             * ArrayList lmsProfileList =
             * (ArrayList)p_hashMap.get(PretupsI.BATCH_USR_LMS_PROFILE);
             * LoyalityVO loyalityVO= null;
             * if(lmsProfileList!=null)
             * {
             * for(int i=0,j=lmsProfileList.size();i<j;i++)
             * {
             * col=0;
             * loyalityVO = (LoyalityVO)lmsProfileList.get(i);
             * label = new Label(col,row,loyalityVO.getPromotionName());
             * worksheet2.addCell(label);
             * row++;
             * }
             * }
             */
            return row;
        } catch (RowsExceededException exception) {
            _log.errorTrace(METHOD_NAME, exception);
            _log.error("writeLmsProfile", " Exception exception: " + exception.getMessage());
            throw exception;
        } catch (WriteException exception) {
            _log.errorTrace(METHOD_NAME, exception);
            _log.error("writeLmsProfile", " Exception exception: " + exception.getMessage());
            throw exception;
        }
    }

    // ///added by Ashutosh
    private int writeCommissionData(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        row += 2; // to give some space before writing the next records
        if (_log.isDebugEnabled()) {
            _log.debug("writeCommissionData", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        boolean batchUserProfileAssign = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.BATCH_USER_PROFILE_ASSIGN);
        String keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.commissionheading");
        Label label = new Label(col, row, keyName, times16format);
        worksheet2.mergeCells(col, row, col + 2, row);
        worksheet2.addCell(label);
        row++;
        col = 0;
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.commissionheading.note");
        label = new Label(col, row, keyName);
        worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
        worksheet2.addCell(label);
        row++;
        col = 0;
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categorycode");
        label = new Label(col, row, keyName, times16format);
        worksheet2.addCell(label);
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categoryname");
        label = new Label(++col, row, keyName, times16format);
        worksheet2.addCell(label);

        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grphcode");
        label = new Label(++col, row, keyName, times16format);
        worksheet2.addCell(label);
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grphname");
        label = new Label(++col, row, keyName, times16format);
        worksheet2.addCell(label);

        if (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.OPERATOR_USER_TYPE) || (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grade");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 1, row);
            worksheet2.addCell(label);

            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.gradecode");
            label = new Label(col, row + 1, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.gradename");
            label = new Label(++col, row + 1, keyName, times16format);
            worksheet2.addCell(label);

            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.commisionprofile");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 1, row);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.commisionprofilecode");
            label = new Label(col, row + 1, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.commisionprofilename");
            label = new Label(++col, row + 1, keyName, times16format);
            worksheet2.addCell(label);
        }

        row++;
        col = 0;

        // Iteration Starts from Row number
        row = row + 1;
        ArrayList commProfList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_COMM_LIST);

        int catSize = 0;
        // Equate The Sizes of the list first so that it will not be checked in
        // the loop
        CommissionProfileSetVO CSVO = null;

        if (commProfList != null && (catSize = commProfList.size()) > 0) {
            for (int i = 0; i < catSize; i++) // Prints One Row Of Category
            {
                col = 0;

                CSVO = (CommissionProfileSetVO) commProfList.get(i);
                label = new Label(col, row, CSVO.getCategoryCode());
                worksheet2.addCell(label);
                label = new Label(++col, row, CSVO.getCategoryName());
                worksheet2.addCell(label);
                label = new Label(++col, row, CSVO.getGrphDomainCode());
                worksheet2.addCell(label);
                label = new Label(++col, row, CSVO.getGrphDomainName());
                worksheet2.addCell(label);
                label = new Label(++col, row, CSVO.getGradeCode());
                worksheet2.addCell(label);
                label = new Label(++col, row, CSVO.getGradeName());
                worksheet2.addCell(label);
                label = new Label(++col, row, CSVO.getCommProfileSetId());
                worksheet2.addCell(label);
                label = new Label(++col, row, CSVO.getCommProfileSetName());
                worksheet2.addCell(label);
                row++;
            }
        }
        return row;

    }

    private int writeGradeData(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        row += 2; // to give some space before writing the next records
        if (_log.isDebugEnabled()) {
            _log.debug("writeGradeData", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        String keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.gradeheading");
        Label label = new Label(col, row, keyName, times16format);
        worksheet2.mergeCells(col, row, col + 2, row);
        worksheet2.addCell(label);
        row++;
        col = 0;
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categorycode");
        label = new Label(col, row, keyName, times16format);
        worksheet2.addCell(label);
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.categoryname");
        label = new Label(++col, row, keyName, times16format);
        worksheet2.addCell(label);
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.grade");
        label = new Label(++col, row, keyName, times16format);
        worksheet2.mergeCells(col, row, col + 1, row);
        worksheet2.addCell(label);

        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.gradecode");
        label = new Label(col, row + 1, keyName, times16format);
        worksheet2.addCell(label);
        keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.gradename");
        label = new Label(++col, row + 1, keyName, times16format);
        worksheet2.addCell(label);
        row += 2;
        // Iteration Starts from Row number
        row = row + 1;
        ArrayList gradeList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_GRADE_LIST);

        int catSize = 0;
        // Equate The Sizes of the list first so that it will not be checked in
        // the loop
        GradeVO gradeVO = null;

        if (gradeList != null && (catSize = gradeList.size()) > 0) {
            for (int i = 0; i < catSize; i++) // Prints One Row Of Category
            {
                col = 0;

                gradeVO = (GradeVO) gradeList.get(i);
                label = new Label(col, row, gradeVO.getCategoryCode());
                worksheet2.addCell(label);
                label = new Label(++col, row, gradeVO.getCategoryName());
                worksheet2.addCell(label);
                label = new Label(++col, row, gradeVO.getGradeCode());
                worksheet2.addCell(label);
                label = new Label(++col, row, gradeVO.getGradeName());
                worksheet2.addCell(label);
                row++;
            }
        }
        return row;

    }

    private int writeUserDocumentTypeList(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) {
    	 final String METHOD_NAME = "writeUserDocumentTypeList";
    	if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
       
        try {
            String keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.documenttypeheading");

            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.documenttypeheading.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.documenttypecode");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.documenttypename");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.USER_DOCUMENT_TYPE);
            ListValueVO listValueVO = null;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) list.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, listValueVO.getLabel());
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

    private int writeUserPaymentTypeList(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) {
   	 final String METHOD_NAME = "writeUserPaymentTypeList";
   	if (_log.isDebugEnabled()) {
           _log.debug(METHOD_NAME, " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
       }
      
       try {
           String keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.paymenttypeheading");

           Label label = new Label(col, row, keyName, times16format);
           worksheet2.mergeCells(col, row, col + 2, row);
           worksheet2.addCell(label);
           row++;
           col = 0;
           keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.paymenttypeheading.note");
           label = new Label(col, row, keyName);
           worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
           worksheet2.addCell(label);
           row++;
           col = 0;

           keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.paymenttypecode");
           label = new Label(col, row, keyName, times16format);
           worksheet2.addCell(label);
           keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.paymenttypename");
           label = new Label(++col, row, keyName, times16format);
           worksheet2.addCell(label);
           row++;
           col = 0;

           ArrayList list = (ArrayList) p_hashMap.get(PretupsI.PAYMENT_INSTRUMENT_TYPE);
           ListValueVO listValueVO = null;
           if (list != null) {
               for (int i = 0, j = list.size(); i < j; i++) {
                   col = 0;
                   listValueVO = (ListValueVO) list.get(i);
                   label = new Label(col, row, listValueVO.getValue());
                   worksheet2.addCell(label);
                   label = new Label(col + 1, row, listValueVO.getLabel());
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
    
    private int writeVoucherListing(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws RowsExceededException, WriteException {
        if (_log.isDebugEnabled())
            _log.debug("writeVoucherListing", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        final String METHOD_NAME = "writeVoucherListing";
        try {
        	String keyName=null;
        	
			keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.voucher");
        	
        	Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
           
			keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.voucher.note");
            
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            
            keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.vouchertype");
            
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            
            	keyName = p_messages.getMessage(p_locale, "user.initiatebatchoperatoruser.mastersheet.vouchername");
            	label = new Label(col + 1, row, keyName, times16format);
                worksheet2.addCell(label);
        	
            
            row++;
            col = 0;

            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_OPT_USR_VOUCHERTYPE_LIST);
            if (list != null && list.size() > 0) {
                ListValueVO listValueVO = null;
                for (int i = 0, j = list.size(); i < j; i++) {
                    listValueVO = (ListValueVO) list.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                    col = 0;
                }
            }

            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeVoucherListing", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeVoucherListing", " Exception e: " + e.getMessage());
            throw e;
        }

    }
    
    
 	private int writeUserLoanProfile(WritableSheet  worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeUserLoanProfile", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeUserLoanProfile";
        try {
            String keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.loanprofileheading");

            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.loanprofileheading.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.loanprofilecode");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.loanprofilename");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.loanprofilecategoryid");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            
            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.loanprofilecategoryname");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            
            
            
            row++;
            col = 0;

            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_LOAN_PROFILE_LIST);
            ListValueVO listValueVO = null;
            
            if (_log.isDebugEnabled())
                _log.debug(METHOD_NAME,"The Value list Size"+list.size());
            
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) list.get(i);
                    //cell = rowdata.createCell((short) col++);
                    if (_log.isDebugEnabled())
                    _log.debug(METHOD_NAME,"The Value:-" +listValueVO.getValue()+"The Value of i:-"+i+"The Lable"+listValueVO.getLabel());
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    
                    label = new Label(++col, row, listValueVO.getLabel().split("_")[0]);
                    worksheet2.addCell(label);
                    
                    label = new Label(++col, row, listValueVO.getLabel().split("_")[1]);
                    worksheet2.addCell(label);
                    
                    label = new Label(++col, row, listValueVO.getLabel().split("_")[2]);
                    worksheet2.addCell(label);
                    row++;
               
                	
                
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeUserPrifix", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeUserPrifix", " Exception e: " + e.getMessage());
            throw e;
        }
    }
 	
 	
 	
 	/**
     * @param p_excelID
     * @param p_hashMap
     * @param messages
     * @param locale
     * @param p_fileName
     * @throws Exception
     */
    public void writeExcelForBatchApproval(String p_excelID, String[][] p_strArr, String[][] p_headerArray, String p_heading, int p_margeCont, HashMap p_hashMap, Locale locale, String p_fileName) {
    	final String METHOD_NAME = "writeExcelForApproval";        
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " p_excelID: " + p_excelID + " p_strArr:" + p_strArr + " p_headerArray=" + p_headerArray + " p_heading=" + p_heading + " p_margeCont=" + p_margeCont + " p_locale: " + p_locale + " p_strArr length: " + p_strArr.length + " p_fileName: " + p_fileName);
        }
        boolean batchUserProfileAssign = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.BATCH_USER_PROFILE_ASSIGN);
        boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
        boolean rsaAuthenticationRequired = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.RSA_AUTHENTICATION_REQUIRED);
        boolean userVoucherTypeAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED);
        boolean ptupsMobqutyMergd = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD);
        WritableWorkbook workbook = null;
        WritableSheet worksheet1 = null;
        WritableSheet worksheet2 = null;
        int row = 0;
        int col = 0;
        //p_messages = messages;
        p_locale = locale;
        double t_mem = 0;
        double f_mem = 0;
        // Added by Ankur for Batch Excel feature GT
        String noOfRecordsPerTemplateStr;
        int noOfRecordsPerTemplate = 0, noOfSheets = 0, totalNoOfRecords = 0;
        try {
            t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            _log.info("writeExcelForApproval", "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));

            String fileName = p_fileName;
            workbook = Workbook.createWorkbook(new File(fileName));

            String key = null;
            String keyName = null;
            Label label = null;

            WritableFont times16font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, true);
            WritableCellFormat times16format = new WritableCellFormat(times16font);

            WritableFont data = new WritableFont(WritableFont.ARIAL, 11, WritableFont.NO_BOLD, true);
            WritableCellFormat dataFormat = new WritableCellFormat(data);

            WritableFont times20font = new WritableFont(WritableFont.ARIAL, 20, WritableFont.BOLD, true);
            WritableCellFormat times20format = new WritableCellFormat(times20font);
            times20format.setAlignment(Alignment.CENTRE);

            // Added by Ankur for Batch Excel feature GT
            noOfRecordsPerTemplateStr = Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE_BATCHUSER");

            if (!BTSLUtil.isNullString(noOfRecordsPerTemplateStr)) {
                noOfRecordsPerTemplate = Integer.parseInt(noOfRecordsPerTemplateStr);
            } else {
                noOfRecordsPerTemplate = 65500;
            }

            totalNoOfRecords = p_strArr.length - 1;

            noOfSheets = BTSLUtil.parseDoubleToInt( Math.ceil(BTSLUtil.parseIntToDouble(totalNoOfRecords) / noOfRecordsPerTemplate));

            int colnum = 0, cols = 0, length = 0, lenInt = 0, recordNo = 0;

            for (int i = 0; i < noOfSheets; i++) {
                worksheet1 = workbook.createSheet("Data Sheet" + (i + 1), i);

                colnum = p_strArr[0].length;
                int[] indexMapArray = new int[colnum];

                
                keyName = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.XLS_SHEET_HEADING, null);

                label = new Label(0, 0, keyName, times20format);// Heading
                worksheet1.mergeCells(0, 0, colnum - 1, 0);
                worksheet1.addCell(label);

                cols = p_headerArray[0].length;
                for (row = 0; row < cols; row++)// Header Headings
                {
                    key = p_headerArray[0][row];
                    keyName = null;
                    keyName = RestAPIStringParser.getMessage(locale, key, null);
                    label = new Label(0, row + 1, keyName, times16format);
                    worksheet1.mergeCells(0, row + 1, p_margeCont, row + 1);
                    worksheet1.addCell(label);
                }
                for (row = 0; row < cols; row++)// Header Headings value
                {
                    keyName = null;
                    keyName = p_headerArray[1][row];
                    label = new Label(p_margeCont + 1, row + 1, keyName, dataFormat);
                    worksheet1.mergeCells(p_margeCont + 1, row + 1, 3 * p_margeCont, row + 1);
                    worksheet1.addCell(label);
                }
                length = p_strArr[0].length;
                String indexStr = null;
                for (col = 0; col < length; col++)// column heading
                {
                    indexStr = null;
                    key = p_strArr[0][col];
                    keyName = null;
                    keyName = RestAPIStringParser.getMessage(locale, key, null);
                    indexStr = ExcelFileConstants.getWriteProperty(p_excelID, String.valueOf(col));
                    if (indexStr == null) {
                        indexStr = String.valueOf(col);
                    }
                    indexMapArray[col] = Integer.parseInt(indexStr);
                    label = new Label(indexMapArray[col], cols + 2, keyName, times16format);
                    worksheet1.addCell(label);
                }
                // setting for the vertical freeze panes
                SheetSettings sheetSetting = new SheetSettings();
                sheetSetting = worksheet1.getSettings();
                sheetSetting.setVerticalFreeze(cols + 3);

                // setting for the horizontal freeze panes
                SheetSettings sheetSetting1 = new SheetSettings();
                sheetSetting1 = worksheet1.getSettings();
                sheetSetting1.setHorizontalFreeze(6);
                if (noOfSheets > 1) {
                    for (row = 1; row <= noOfRecordsPerTemplate; row++) {
                        recordNo = row + (i * noOfRecordsPerTemplate);
                        if (recordNo <= totalNoOfRecords) {
                            lenInt = p_strArr[recordNo].length;
                            for (col = 0; col < lenInt; col++) {
                                label = new Label(indexMapArray[col], row + cols + 2, p_strArr[recordNo][col], dataFormat);
                                worksheet1.addCell(label);
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
                            worksheet1.addCell(label);
                        }
                    }
                }
            }

            col = 0;
            row = 0;
            worksheet2 = workbook.createSheet("Master Sheet", noOfSheets);

//            keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.heading", (String) p_hashMap.get(PretupsI.BATCH_USR_DOMAIN_NAME));
            String str = (String) p_hashMap.get(PretupsI.BATCH_USR_DOMAIN_NAME);
            String[] strArray = new String[] {str};  
            keyName = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MASTER_SHEET_HEADING_BATCH_APPROVE, strArray);
            label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 5, row);
            worksheet2.addCell(label);
            row++;
            row++;
            col = 0;
            row = this.writeUserPrefixForBatchApprove(worksheet2, col, row, p_hashMap);
            row++;
            col = 0;
            row = this.writeUserOutletSubOutletForBatchApprove(worksheet2, col, row, p_hashMap);
            row++;
            col = 0;
            row = this.writeServiceTypeForBatchApprove(worksheet2, col, row, p_hashMap);
            row++;
            col = 0;
            row = this.writeGeographyListingForBatchApprove(worksheet2, col, row, p_hashMap);
            row++;
            col = 0;
            row = this.writeCategoryHierarchyForBatchApprove(worksheet2, col, row, p_hashMap);
            row++;
            col = 0;
            row = this.writeCategoryDataForBatchApprove(worksheet2, col, row, p_hashMap);

            if (rsaAuthenticationRequired) {
                // Added for RSA Authentication June 2011 by Ankur
                row++;
                col = 0;
                row = this.writeRsaAssociationsForBatchApprove(worksheet2, col, row, p_hashMap);
                row++;
            }
            // for Zebra and Tango by Sanjeew date 09/07/07
            if (ptupsMobqutyMergd) {
                if (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.OPERATOR_USER_TYPE) || (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {
                    row++;
                    col = 0;
                    row = this.writeMpayProfIDForBatchApprove(worksheet2, col, row, p_hashMap);
                }
            }
            // for Zebra and Tango by Sanjeew date 09/07/07
            if (isTrfRuleUserLevelAllow) {
                if (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.OPERATOR_USER_TYPE) || (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {
                    row++;
                    col = 0;
                    row = this.writeTrfRuleTypeForBatchApprove(worksheet2, col, row, p_hashMap);
                }
            }
            row++;
            col = 0;
            row = this.writeUserDocumentTypeListForBatchApprove(worksheet2, col, row, p_hashMap);
            row++;
            col = 0;
            row = this.writeUserPaymentTypeListForBatchApprove(worksheet2, col, row, p_hashMap);
            if(userVoucherTypeAllowed)
            {
            	ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_OPT_USR_VOUCHERTYPE_LIST);
            	if (list != null && list.size() > 0) {
		            row++;
		            col = 0;
		            row = this.writeVoucherListingForBatchApprove(worksheet2, col, row, p_hashMap);
            	}
            }
            
            if (SystemPreferences.USERWISE_LOAN_ENABLE) {
            	 if (_log.isDebugEnabled())
	                    _log.debug(METHOD_NAME,"The Value of the Cell Load Id:-");
            	 
            	row++;
               col = 0;
               //row = this.writeTrfRuleType(worksheet2, col, row, p_hashMap);
               row = this.writeUserLoanProfile(worksheet2, col, row, p_hashMap);
           }
            
            workbook.write();
            
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeExcelForApproval", " Exception e: " + e.getMessage());
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
            t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            Runtime.getRuntime().gc();
            f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            _log.info("writeExcelForApproval", "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));
        }
    }
    
    
    /**
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */
    private int writeUserPrefixForBatchApprove(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) {
        if (_log.isDebugEnabled()) {
            _log.debug("writeUserPrefixForBatchApprove", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeUserPrefixForBatchApprove";
        try {
        	String keyName =  RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PREFIX_HEADING, null);
        	
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PREFIX_HEADING_NOTE, null);
            
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName =  RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PREFIX_CODE, null);
            
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);

            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PREFIX_NAME, null);
            
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_USER_PREFIX_LIST);
            ListValueVO listValueVO = null;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) list.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeUserPrifix", " Exception e: " + e.getMessage());
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeUserPrifix", " Exception e: " + e.getMessage());
        }
        return row;
    }
    
    
    /**
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */
    private int writeUserOutletSubOutletForBatchApprove(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) {
        if (_log.isDebugEnabled()) {
            _log.debug("writeUserOutletSubOutletForBatchApprove", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeUserOutletSubOutletForBatchApprove";
        try {
        	String keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.OUTLET_SUB_OUTLET, null);

            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.OUTLET_SUB_OUTLET_HEADING_NOTE, null);
            
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.OUTLET_CODE, null);
            
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.OUTLET_NAME, null);
            
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SUB_OUTLET_CODE, null);
            
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SUB_OUTLET_NAME, null);
            
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            ArrayList outletList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_OUTLET_LIST);
            ArrayList suboutletList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_SUBOUTLET_LIST);
            ListValueVO listValueVO = null;
            ListValueVO listValueVOSub = null;
            if (outletList != null) {
                for (int i = 0, j = outletList.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) outletList.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    if (suboutletList != null) {
                        for (int k = 0, l = suboutletList.size(); k < l; k++) {
                            listValueVOSub = (ListValueVO) suboutletList.get(k);
                            String sub[] = listValueVOSub.getValue().split(":");
                            if (listValueVO.getValue().equals(sub[1])) {
                                label = new Label(col + 2, row, sub[0]);
                                worksheet2.addCell(label);
                                label = new Label(col + 3, row, listValueVOSub.getLabel());
                                worksheet2.addCell(label);
                                row++;
                            }
                        }
                    }
                    row++;
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeUserOutletSubOutlet", " Exception e: " + e.getMessage());
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeUserOutletSubOutlet", " Exception e: " + e.getMessage());
        }
        return row;
    }
    
    
    /**
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */
    private int writeServiceTypeForBatchApprove(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeServiceTypeForBatchApprove", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeServiceTypeForBatchApprove";
        try {
            //String keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.service");
        	String keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.MASTERSHEET_SERVICES, null);

            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SERVICE_NOTE, null);
            
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SERVICE_TYPE, null);
            
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SERVICE_NAME, null);
            
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_SERVICE_LIST);
            ListValueVO listValueVO = null;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) list.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeServiceType", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeServiceType", " Exception e: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Method writeGeographyListing
     * This method writes the Geography Details containing zone,area,sub area etc. [ N level geographies can exists ]
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     * @author Anand Swaraj
     */
    private int writeGeographyListingForBatchApprove(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeGeographyListingForBatchApprove", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeGeographyListingForBatchApprove";
        try {
        	String keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.AVAILIABLE_GEO_LIST, null);
            
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.AVAILIABLE_GEO_LIST_NOTE, null);
            
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
//            String endTagCode = p_messages.getMessage(p_locale, "code");
//            String endTagName = p_messages.getMessage(p_locale, "name");
            String endTagCode = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.CODE, null);
            String endTagName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.NAME, null);

            String geoType = null;
            for (int i = 0, j = geoDomainTypeList.size(); i < j; i++) {
                geoType = ((String) geoDomainTypeList.get(i)).trim();
                //keyName = p_messages.getMessage(p_locale, geoType);
                keyName = RestAPIStringParser.getMessage(p_locale, geoType, null);
                
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
                                                                                                        // proper
                                                                                                        // formatting
                                                                                                        // of
                                                                                                        // geo.
                                                                                                        // list
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
     * Method writeCategoryHierarchyForBatchApprove
     * This method writes the Category Hierarchy list
     * 
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */
    private int writeCategoryHierarchyForBatchApprove(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeCategoryHierarchyForBatchApprove", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeCategoryHierarchyForBatchApprove";
        try {
        	String keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.CATEGORY_HIERARCHY, null);
        	
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.CATEGORY_HIERARCHY_NOTE, null);
            
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PARENT_CATEGORY, null);
            
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.CHILD_CATEGORY, null);
            
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);

            row++;
            col = 0;
            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_CATEGORY_HIERARCHY_LIST);
            ChannelTransferRuleVO channelTransferRuleVO = null;
            int oldSeqNum = 0;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    channelTransferRuleVO = (ChannelTransferRuleVO) list.get(i);
                    // Parent Category will be displayed according to Seq Num
                    if (i == 0 || oldSeqNum != channelTransferRuleVO.getFromSeqNo()) {
                        label = new Label(col, row, channelTransferRuleVO.getFromCategory());
                        worksheet2.addCell(label);
                    } else {
                        label = new Label(col, row, "");
                        worksheet2.addCell(label);
                    }
                    label = new Label(col + 1, row, channelTransferRuleVO.getToCategory());
                    worksheet2.addCell(label);
                    oldSeqNum = channelTransferRuleVO.getFromSeqNo();
                    row++;
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeCategoryHierarchy", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeCategoryHierarchy", " Exception e: " + e.getMessage());
            throw e;
        }

    }
    
    
    
    /**
     * Method writeCategoryDataForBatchApprove
     * This method writes the category details including
     * geographies,grade,commision profile,transfer
     * profile & group information
     * 
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */
    private int writeCategoryDataForBatchApprove(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        int maxrow = row;
        if (_log.isDebugEnabled()) {
            _log.debug("writeCategoryDataForBatchApprove", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeCategoryDataForBatchApprove";
        boolean batchUserProfileAssign = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.BATCH_USER_PROFILE_ASSIGN);
        String keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.CATEGORY_HEADING, null);
        
        Label label = new Label(col, row, keyName, times16format);
        worksheet2.mergeCells(col, row, col + 2, row);
        worksheet2.addCell(label);
        row++;
        col = 0;
        keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.CATEGORY_HEADING_NOTE, null);
        
        label = new Label(col, row, keyName);
        worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
        worksheet2.addCell(label);
        row++;
        col = 0;
        keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.MASTERSHEET_CATEGORY_CODE, null);
        
        label = new Label(col, row, keyName, times16format);
        worksheet2.addCell(label);
        keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.MASTERSHEET_CATEGORY_NAME, null);
        
        label = new Label(++col, row, keyName, times16format);
        worksheet2.addCell(label);

        // for Zebra and Tango by Sanjeew date 09/07/07
        // For Low balance alert allow
        keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.LOW_BALANCE_ALERT_ALLOW, null);
        
        label = new Label(++col, row, keyName, times16format);
        worksheet2.addCell(label);
        // End Zebra and Tango

        /*
         * keyName = p_messages.getMessage(p_locale,
         * "batchusercreation.mastersheet.webaccessallowed");
         * label = new Label(++col,row,keyName, times16format);
         * 
         * worksheet2.addCell(label);
         * keyName = p_messages.getMessage(p_locale,
         * "batchusercreation.mastersheet.mobileaccessallowed");
         * label = new Label(++col,row,keyName, times16format);
         * worksheet2.addCell(label);
         */
        keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.GRAPH_DOMAIN_TYPE, null);
        
        label = new Label(++col, row, keyName, times16format);
        worksheet2.mergeCells(col, row, col + 1, row);
        worksheet2.addCell(label);

        keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.GRAPH_DOMAIN_TYPE_CODE, null);
        
        label = new Label(col, row + 1, keyName, times16format);
        worksheet2.addCell(label);
        keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.GRAPH_DOMAIN_TYPE_NAME, null);
        
        label = new Label(++col, row + 1, keyName, times16format);
        worksheet2.addCell(label);

        if (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.OPERATOR_USER_TYPE) || (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {

            //keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.transfercontrolprf");
        	keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.TRF_CNTRL_PROFILE, null);
        	
            label = new Label(++col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 1, row);
            worksheet2.addCell(label);

            //keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.transfercontrolprfcode");
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.TRF_CNTRL_PROFILE_CODE, null);
            
            label = new Label(col, row + 1, keyName, times16format);
            worksheet2.addCell(label);
            //keyName = p_messages.getMessage(p_locale, "batchusercreation.mastersheet.transfercontrolprfname");
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.TRF_CNTRL_PROFILE_NAME, null);
            
            label = new Label(++col, row + 1, keyName, times16format);
            worksheet2.addCell(label);
        }

        keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.GROUP_ROLE, null);
        label = new Label(++col, row, keyName, times16format);
        worksheet2.mergeCells(col, row, col + 1, row);
        worksheet2.addCell(label);
        keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.GROUP_ROLE_CODE, null);
        
        label = new Label(col, row + 1, keyName, times16format);
        worksheet2.addCell(label);
        keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.GROUP_ROLE_NAME, null);
        
        label = new Label(++col, row + 1, keyName, times16format);
        worksheet2.addCell(label);

        row++;
        col = 0;

        // Iteration Starts from Row number
        row++;
        ArrayList categoryList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_CATEGORY_LIST);
        ArrayList geographyList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_GEOGRAPHY_TYPE_LIST);
        ArrayList gradeList = null;
        ArrayList transferProfileList = null;
        ArrayList commPrfList = null;
        if (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.OPERATOR_USER_TYPE) || (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {
            gradeList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_GRADE_LIST);
            transferProfileList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_TRANSFER_CONTROL_PRF_LIST);
            commPrfList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_COMMISION_PRF_LIST);
        }
        ArrayList grpRoleList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_GROUP_ROLE_LIST);

        int catSize = 0;
        int tempCol = 0;
        int tempRow = 0;
        // Change for Batch User Initiate by Channel users
        int colIncrement = 0;
        int geoListSize = 0;
        int gradeListSize = 0;
        int trfPrfRowSize, transferPrfListSize = 0;
        int commPrfListSize = 0;
        int groupListSize = 0;
        int maxRow = 0;

        CategoryVO categoryVO = null;
        GeographicalDomainTypeVO geographicalDomainTypeVO = null;
        TransferProfileVO profileVO = null;
        UserRolesVO rolesVO = null;

        // Equate The Sizes of the list first so that it will not be checked in
        // the loop
        if (geographyList != null) {
            geoListSize = geographyList.size();
        }
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

        if (categoryList != null && (catSize = categoryList.size()) > 0) {
            for (int i = 0; i < catSize; i++) // Prints One Row Of Category
            {
                col = 0;
                colIncrement = 0;
                categoryVO = (CategoryVO) categoryList.get(i);
                label = new Label(col, row, categoryVO.getCategoryCode());
                worksheet2.addCell(label);
                label = new Label(++col, row, categoryVO.getCategoryName());
                worksheet2.addCell(label);

                // for Zebra and Tango by Sanjeew date 09/07/07
                // For Low balance alert allow
                label = new Label(++col, row, categoryVO.getLowBalAlertAllow());
                worksheet2.addCell(label);
                // End Zebra and Tango

                /*
                 * label = new
                 * Label(++col,row,categoryVO.getWebInterfaceAllowed());
                 * worksheet2.addCell(label);
                 * label = new
                 * Label(++col,row,categoryVO.getSmsInterfaceAllowed());
                 * worksheet2.addCell(label);
                 */
                // Now iterate Geographical domain type
                tempRow = row;
                maxrow = row;
                for (int j = 0; j < geoListSize; j++) {
                    tempCol = col;
                    geographicalDomainTypeVO = (GeographicalDomainTypeVO) geographyList.get(j);
                    if (geographicalDomainTypeVO.getCategoryCode().equals(categoryVO.getCategoryCode())) {
                        label = new Label(++tempCol, tempRow, geographicalDomainTypeVO.getGrphDomainType());
                        worksheet2.addCell(label);
                        label = new Label(++tempCol, tempRow, geographicalDomainTypeVO.getGrphDomainTypeName());
                        worksheet2.addCell(label);
                        tempRow++;
                    }
                }

                maxrow = tempRow;
                tempRow = row;

                if (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.OPERATOR_USER_TYPE) || (p_hashMap.get(PretupsI.USER_TYPE).equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign)) {

                    // Now maxRow will contains the greatest value among the two
                    colIncrement += 2;
                    for (int l = 0; l < transferPrfListSize; l++) {
                        tempCol = col + colIncrement;
                        profileVO = (TransferProfileVO) transferProfileList.get(l);
                        if (profileVO.getCategory().equals(categoryVO.getCategoryCode())) {
                            label = new Label(++tempCol, tempRow, profileVO.getProfileId());
                            worksheet2.addCell(label);
                            label = new Label(++tempCol, tempRow, profileVO.getProfileName());
                            worksheet2.addCell(label);
                            tempRow++;
                        }
                    }
                    maxrow = tempRow;
                    trfPrfRowSize = tempRow;
                    tempRow = row;

                    if (trfPrfRowSize > maxRow) {
                        maxRow = trfPrfRowSize;
                    }

                    colIncrement += 2;

                    for (int n = 0; n < groupListSize; n++) {
                        tempCol = col + colIncrement;
                        rolesVO = (UserRolesVO) grpRoleList.get(n);
                        if (rolesVO.getCategoryCode().equals(categoryVO.getCategoryCode())) {
                            label = new Label(++tempCol, tempRow, rolesVO.getRoleCode());
                            worksheet2.addCell(label);
                            label = new Label(++tempCol, tempRow, rolesVO.getRoleName());
                            worksheet2.addCell(label);
                            tempRow++;
                        }
                    }

                    maxrow = tempRow;
                    if (tempRow > maxRow) {
                        maxRow = tempRow;
                    }
                    // Max size of ROWS according to the data
                    row = maxRow;
                    colIncrement = 0;
                }
            }

        }
        return maxrow;
    }
    
    
    
    
    // Added by Shashank Gaur for RSA Authentication
    private int writeRsaAssociationsForBatchApprove(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeRSAAllowed", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeRsaAssociationsForBatchApprove";
        try {
        	 String keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.RSA_HEADING, null);
        	 
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.RSA_HEADING_NOTE, null);
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.MASTERSHEET_CATEGORY_CODE, null);
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.RSA_ALLOWED, null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            ArrayList categoryList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_CATEGORY_LIST);
            CategoryVO categoryVO;
            int catSize = 0;
            if (categoryList != null) {
                catSize = categoryList.size();
            }

            if (categoryList != null && (catSize = categoryList.size()) > 0) {
                for (int i = 0; i < catSize; i++) // Prints One Row Of Category
                {
                    col = 0;
                    categoryVO = (CategoryVO) categoryList.get(i);
                    label = new Label(col, row, categoryVO.getCategoryName());
                    worksheet2.addCell(label);
                    Boolean rsarequired = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.RSA_AUTHENTICATION_REQUIRED, "TG", categoryVO.getCategoryCode())).booleanValue();
                    if (rsarequired == true) {
                        label = new Label(++col, row, PretupsI.YES);
                    } else {
                        label = new Label(++col, row, PretupsI.NO);
                    }
                    worksheet2.addCell(label);
                    row++;
                    col = 0;
                }
            }
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeRsaAssociations", " Exception e: " + e.getMessage());
            throw e;
        }
        return row;
    }
    
    private int writeMpayProfIDForBatchApprove(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeMpayProfID", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeMpayProfIDForBatchApprove";
        try {
        	String keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.MPAY_PROFILE_DETAILS, null);
        	
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.GRADE_CODE, null);
            
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.M_PAY_PROFILE_ID, null);
            
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.M_PAY_PROFILE_ID_DESC, null);
            
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);

            row++;
            col = 0;
            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.M_PAY_PROFILE_LIST);
            ListValueVO listValueVO = null;
            String strvalue = null;
            String mpayIDName = null;
            String usrGrd = null;
            ArrayList usrGrdList = new ArrayList();

            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) list.get(i);
                    mpayIDName = listValueVO.getLabel();
                    strvalue = listValueVO.getValue();
                    usrGrd = strvalue.split(":")[1];
                    if (usrGrdList.size() > 0 && usrGrdList.contains(usrGrd)) {
                        label = new Label(++col, row, strvalue.split(":")[2]);
                        worksheet2.addCell(label);
                        label = new Label(++col, row, mpayIDName);
                        worksheet2.addCell(label);
                    } else {
                        usrGrdList.add(usrGrd);
                        label = new Label(col, row, usrGrd);
                        worksheet2.addCell(label);
                        label = new Label(++col, row, strvalue.split(":")[2]);
                        worksheet2.addCell(label);
                        label = new Label(++col, row, mpayIDName);
                        worksheet2.addCell(label);
                    }
                    row++;
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeMpayProfID", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeMpayProfID", " Exception e: " + e.getMessage());
            throw e;
        }
    }
    
    
 // Added by Shashank Gaur for Trf rule Authentication
    /**
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */
    private int writeTrfRuleTypeForBatchApprove(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeTrfRuleType", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeTrfRuleTypeForBatchApprove";
        try {
        	String keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.TRANSFER_RULE_TYPE_HEADING, null);

            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.TRANSFER_RULE_TYPE_HEADING_NOTE, null);
            
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.TRANSFER_RULE_TYPE_CODE, null);
            
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.TRANSFER_RULE_TYPE_NAME, null);
            
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            ArrayList outletList = (ArrayList) p_hashMap.get(PretupsI.BATCH_USR_TRF_RULE_LIST);
            ListValueVO listValueVO = null;
            if (outletList != null) {
                for (int i = 0, j = outletList.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) outletList.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeTrfRuleType", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeTrfRuleType", " Exception e: " + e.getMessage());
            throw e;
        }
    }
    
    
    private int writeUserDocumentTypeListForBatchApprove(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) {
   	 final String METHOD_NAME = "writeUserDocumentTypeListForBatchApprove";
   	if (_log.isDebugEnabled()) {
           _log.debug(METHOD_NAME, " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
       }
      
       try {
    	   String keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.DOCUMENT_TYPE_HEADING, null);
    	   
           Label label = new Label(col, row, keyName, times16format);
           worksheet2.mergeCells(col, row, col + 2, row);
           worksheet2.addCell(label);
           row++;
           col = 0;
           keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.DOCUMENT_TYPE_HEADING_NOTE, null);
           label = new Label(col, row, keyName);
           worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
           worksheet2.addCell(label);
           row++;
           col = 0;

           keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.DOCUMENT_TYPE_CODE, null);
           
           label = new Label(col, row, keyName, times16format);
           worksheet2.addCell(label);
           keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.DOCUMENT_TYPE_NAME, null);
           
           label = new Label(++col, row, keyName, times16format);
           worksheet2.addCell(label);
           row++;
           col = 0;

           ArrayList list = (ArrayList) p_hashMap.get(PretupsI.USER_DOCUMENT_TYPE);
           ListValueVO listValueVO = null;
           if (list != null) {
               for (int i = 0, j = list.size(); i < j; i++) {
                   col = 0;
                   listValueVO = (ListValueVO) list.get(i);
                   label = new Label(col, row, listValueVO.getValue());
                   worksheet2.addCell(label);
                   label = new Label(col + 1, row, listValueVO.getLabel());
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
    
    
    private int writeUserPaymentTypeListForBatchApprove(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) {
      	 final String METHOD_NAME = "writeUserPaymentTypeListForBatchApprove";
      	if (_log.isDebugEnabled()) {
              _log.debug(METHOD_NAME, " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
          }
         
          try {
        	  String keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PAYMENT_TYPE_HEADING, null);
        	  
              Label label = new Label(col, row, keyName, times16format);
              worksheet2.mergeCells(col, row, col + 2, row);
              worksheet2.addCell(label);
              row++;
              col = 0;
              keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PAYMENT_TYPE_HEADING_NOTE, null);
              
              label = new Label(col, row, keyName);
              worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
              worksheet2.addCell(label);
              row++;
              col = 0;

              keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PAYMENT_TYPE_CODE, null);
              
              label = new Label(col, row, keyName, times16format);
              worksheet2.addCell(label);
              keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PAYMENT_TYPE_NAME, null);
              
              label = new Label(++col, row, keyName, times16format);
              worksheet2.addCell(label);
              row++;
              col = 0;

              ArrayList list = (ArrayList) p_hashMap.get(PretupsI.PAYMENT_INSTRUMENT_TYPE);
              ListValueVO listValueVO = null;
              if (list != null) {
                  for (int i = 0, j = list.size(); i < j; i++) {
                      col = 0;
                      listValueVO = (ListValueVO) list.get(i);
                      label = new Label(col, row, listValueVO.getValue());
                      worksheet2.addCell(label);
                      label = new Label(col + 1, row, listValueVO.getLabel());
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
    
    
    
    private int writeVoucherListingForBatchApprove(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws RowsExceededException, WriteException {
        if (_log.isDebugEnabled())
            _log.debug("writeVoucherListing", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        final String METHOD_NAME = "writeVoucherListingForBatchApprove";
        try {
        	String keyName=null;
        	keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.VOUCHER, null);
        	
        	Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
           
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.VOUCHER_NOTE, null);
            
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.VOUCHER_TYPE, null);
            
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            
            	keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.VOUCHER_NAME, null);
            
            	label = new Label(col + 1, row, keyName, times16format);
                worksheet2.addCell(label);
        	
            
            row++;
            col = 0;

            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_OPT_USR_VOUCHERTYPE_LIST);
            if (list != null && list.size() > 0) {
                ListValueVO listValueVO = null;
                for (int i = 0, j = list.size(); i < j; i++) {
                    listValueVO = (ListValueVO) list.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                    col = 0;
                }
            }

            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeVoucherListing", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeVoucherListing", " Exception e: " + e.getMessage());
            throw e;
        }

    }
    
    
    
    
    private int writeUserLoanProfileForBatchApprove(WritableSheet  worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeUserLoanProfile", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeUserLoanProfileForBatchApprove";
        try {
        	String keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.LOAN_PROFILE_HEADING, null);

            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.LOAN_PROFILE_HEADING_NOTE, null);
            
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGE, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.LOAN_PROFILE_CODE, null);
            
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.LOAN_PROFILE_NAME, null);
            
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.LOAN_PROFILE_CATEGORY_ID, null);
            
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            
            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.LOAN_PROFILE_CATEGORY_NAME, null);
            
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            
            
            
            row++;
            col = 0;

            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_LOAN_PROFILE_LIST);
            ListValueVO listValueVO = null;
            
            if (_log.isDebugEnabled())
                _log.debug(METHOD_NAME,"The Value list Size"+list.size());
            
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) list.get(i);
                    //cell = rowdata.createCell((short) col++);
                    if (_log.isDebugEnabled())
                    _log.debug(METHOD_NAME,"The Value:-" +listValueVO.getValue()+"The Value of i:-"+i+"The Lable"+listValueVO.getLabel());
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    
                    label = new Label(++col, row, listValueVO.getLabel().split("_")[0]);
                    worksheet2.addCell(label);
                    
                    label = new Label(++col, row, listValueVO.getLabel().split("_")[1]);
                    worksheet2.addCell(label);
                    
                    label = new Label(++col, row, listValueVO.getLabel().split("_")[2]);
                    worksheet2.addCell(label);
                    row++;
               
                	
                
                }
            }
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeUserPrifix", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeUserPrifix", " Exception e: " + e.getMessage());
            throw e;
        }
    }
    
    
}

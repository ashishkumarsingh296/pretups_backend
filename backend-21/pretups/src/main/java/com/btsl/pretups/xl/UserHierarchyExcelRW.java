package com.btsl.pretups.xl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Iterator;
import java.util.Locale;

import jxl.SheetSettings;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.btsl.util.MessageResources;

import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.channel.user.web.ChannelUserTransferForm;


/*
 * UserHierarchyExcelRW.java
 * 
 * 
 * 
 * Name Date History
 * ------------------------------------------------------------------------
 * Shishupal Singh 25/04/2007 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2007 Bharti Telesoft Ltd.
 */

// Praveen//
public class UserHierarchyExcelRW {

    private Log _log = LogFactory.getLog(this.getClass().getName());
    private WritableFont times16font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, true);
    private WritableCellFormat times16format = new WritableCellFormat(times16font);

    /**
     * @param form
     * @param p_messages
     * @param p_locale
     * @param p_fileName
     * @throws Exception
     * @author shishupal.singh
     */
    public void writeExcel(ChannelUserTransferForm form, MessageResources p_messages, Locale p_locale, String p_fileName) {
        final String METHOD_NAME = "writeExcel";
        if (_log.isDebugEnabled()) {
            _log.debug("writeExcel", " p_locale: " + p_locale + ", p_fileName: " + p_fileName);
        }
        WritableWorkbook workbook = null;
        WritableSheet worksheet = null;
        SheetSettings sheetSetting = null;
        int indexcol = 0, productindex = 1;
        int col = 0, row = 0;
        try {
            sheetSetting = new SheetSettings();
            workbook = Workbook.createWorkbook(new File(p_fileName));
            worksheet = workbook.createSheet("Master Sheet", 0);
            sheetSetting = worksheet.getSettings();
            sheetSetting.setShowGridLines(false);
            Label label = null;
            String keyName = null;

            int colSpanValue = form.getCategoryListSize();
            int colSpanV = colSpanValue + 4;
            int colSpan1 = colSpanV / 4;
            int colSpan2 = colSpanV / 2 - colSpan1;
            int colSpan3 = colSpanV / 4;
            int colSpan4 = colSpanV - colSpan1 - colSpan2 - colSpan3;

            col = 0;
            row++;
            keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyviewdetails.heading");
            label = new Label(col, row, keyName, times16format);
            worksheet.mergeCells(col, row, col + colSpanV - 1, row);
            worksheet.addCell(label);
            // 1//
            if (((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_HIERARCHY_SIZE))).intValue() < form.getUserHierarchyListSize()) {
                col = 0;
                row++;
                keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.msg.nooperationallowed", String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_HIERARCHY_SIZE))).intValue()));
                label = new Label(col, row, keyName);
                worksheet.mergeCells(col, row, col + colSpanV - 1, row);
                worksheet.addCell(label);
            }
            // 2//
            col = 0;
            row++;
            keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.domain");
            label = new Label(col, row, keyName + ":");
            worksheet.mergeCells(col, row, col + colSpan1 - 1, row);
            worksheet.addCell(label);
            col = col + colSpan1;
            label = new Label(col, row, form.getDomainCodeDesc());
            worksheet.mergeCells(col, row, col + colSpan2 - 1, row);
            worksheet.addCell(label);
            col = col + colSpan2;
            keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.usercategory");
            label = new Label(col, row, keyName + ":");
            worksheet.mergeCells(col, row, col + colSpan3 - 1, row);
            worksheet.addCell(label);
            col = col + colSpan3;
            label = new Label(col, row, form.getTransferUserCategoryDesc());
            worksheet.mergeCells(col, row, col + colSpan4 - 1, row);
            worksheet.addCell(label);
            // 3//
            if ("transfer".equals(form.getRequestFor())) {
                col = 0;
                row++;
                keyName = p_messages.getMessage(p_locale, "channeluser.userhiertrf.label.fromparentinfo");
                label = new Label(col, row, keyName);
                worksheet.mergeCells(col, row, col + colSpanV - 1, row);
                worksheet.addCell(label);
            }
            // 4//
            if (form.getMsisdn() != null && !"".equals(form.getMsisdn())) {
                col = 0;
                row++;
                keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.msisdn");
                label = new Label(col, row, keyName + ":");
                worksheet.mergeCells(col, row, col + colSpan1 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan1;
                label = new Label(col, row, form.getMsisdn());
                worksheet.mergeCells(col, row, col + colSpan2 + colSpan3 + colSpan4 - 1, row);
                worksheet.addCell(label);
            }
            // 5//
            if (form.getLoginID() != null && !"".equals(form.getLoginID())) {
                col = 0;
                row++;
                keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.loginid");
                label = new Label(col, row, keyName + ":");
                worksheet.mergeCells(col, row, col + colSpan1 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan1;
                label = new Label(col, row, form.getLoginID());
                worksheet.mergeCells(col, row, col + colSpan2 + colSpan3 + colSpan4 - 1, row);
                worksheet.addCell(label);
            }
            // 6//
            col = 0;
            row++;
            keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.zone");
            label = new Label(col, row, keyName + ":");
            worksheet.mergeCells(col, row, col + colSpan1 - 1, row);
            worksheet.addCell(label);
            col = col + colSpan1;
            label = new Label(col, row, form.getZoneCodeDesc());
            worksheet.mergeCells(col, row, col + colSpan2 - 1, row);
            worksheet.addCell(label);
            col = col + colSpan2;
            keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.fromowner");
            label = new Label(col, row, keyName + ":");
            worksheet.mergeCells(col, row, col + colSpan3 - 1, row);
            worksheet.addCell(label);
            col = col + colSpan3;
            label = new Label(col, row, form.getOwnerName());
            worksheet.mergeCells(col, row, col + colSpan4 - 1, row);
            worksheet.addCell(label);
            // 7//
            col = 0;
            row++;
            keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.fromparentuser");
            label = new Label(col, row, keyName + ":");
            worksheet.mergeCells(col, row, col + colSpan1 - 1, row);
            worksheet.addCell(label);
            col = col + colSpan1;
            label = new Label(col, row, form.getParentUserName());
            worksheet.mergeCells(col, row, col + colSpan2 - 1, row);
            worksheet.addCell(label);
            col = col + colSpan2;
            keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.fromparentcategory");
            label = new Label(col, row, keyName + ":");
            worksheet.mergeCells(col, row, col + colSpan3 - 1, row);
            worksheet.addCell(label);
            col = col + colSpan3;
            label = new Label(col, row, form.getParentCategoryDesc());
            worksheet.mergeCells(col, row, col + colSpan4 - 1, row);
            worksheet.addCell(label);

            if (form.getToParentCategoryList() != null) {
                // 8//
                col = 0;
                row++;
                keyName = p_messages.getMessage(p_locale, "channeluser.userhiertrf.label.toparentinfo");
                label = new Label(col, row, keyName);
                worksheet.mergeCells(col, row, col + colSpanV - 1, row);
                worksheet.addCell(label);
                // 9//
                col = 0;
                row++;
                keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.tozone");
                label = new Label(col, row, keyName + ":");
                worksheet.mergeCells(col, row, col + colSpan1 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan1;
                label = new Label(col, row, form.getToZoneCodeDesc());
                worksheet.mergeCells(col, row, col + colSpan2 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan2;
                keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.toowner");
                label = new Label(col, row, keyName + ":");
                worksheet.mergeCells(col, row, col + colSpan3 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan3;
                label = new Label(col, row, form.getToOwnerName());
                worksheet.mergeCells(col, row, col + colSpan4 - 1, row);
                worksheet.addCell(label);
                // 10//
                col = 0;
                row++;
                keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.toparentuser");
                label = new Label(col, row, keyName + ":");
                worksheet.mergeCells(col, row, col + colSpan1 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan1;
                label = new Label(col, row, form.getToParentUserName());
                worksheet.mergeCells(col, row, col + colSpan2 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan2;
                keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.toparentcategory");
                label = new Label(col, row, keyName + ":");
                worksheet.mergeCells(col, row, col + colSpan3 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan3;
                label = new Label(col, row, form.getToParentCategoryDesc());
                worksheet.mergeCells(col, row, col + colSpan4 - 1, row);
                worksheet.addCell(label);
            }

            if (form.getCategoryList() != null) {
                // 11//
                col = 0;
                row++;
                for (int catNo = 1; catNo <= colSpanValue; catNo++, indexcol++) {
                    keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.childlevel");
                    label = new Label(col++, row, keyName + catNo, times16format);
                    worksheet.addCell(label);
                }
                keyName = p_messages.getMessage(p_locale, "channeluser.selectfromowner.label.status");
                label = new Label(col++, row, keyName, times16format);
                worksheet.addCell(label);
                keyName = p_messages.getMessage(p_locale, "channeluser.selectfromowner.label.usercode");
                label = new Label(col++, row, keyName, times16format);
                worksheet.addCell(label);
                keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.productshortname");
                label = new Label(col++, row, keyName, times16format);
                worksheet.addCell(label);
                keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.productbalance");
                label = new Label(col++, row, keyName, times16format);
                worksheet.addCell(label);
            }
            // 12//
            if (form.getUserHierarchyList() != null) {
                col = 0;
                row++;
                if (form.getUserHierarchyListSize() == 0) {
                    keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.msg.nodata");
                    label = new Label(col, row, keyName, times16format);
                    worksheet.mergeCells(col, row, col + colSpanV + 4, row);
                    worksheet.addCell(label);
                }
                Iterator itr = form.getUserHierarchyList().iterator();
                ChannelUserVO channelUserVO = null;
                while (itr.hasNext()) {
                    channelUserVO = (ChannelUserVO) itr.next();
                    productindex = 1;
                    int intlevel = Integer.parseInt(String.valueOf(channelUserVO.getUserlevel()));
                    for (int i = 0; i < ((int) intlevel) - 1; i++) {
                        ++productindex;
                        label = new Label(col++, row, "");
                        worksheet.addCell(label);
                    }
                    label = new Label(col++, row, channelUserVO.getUserNameWithCategory());
                    worksheet.addCell(label);
                    for (int j = productindex; j < indexcol; j++) {
                        label = new Label(col++, row, "");
                        worksheet.addCell(label);
                    }
                    label = new Label(col++, row, channelUserVO.getStatusDesc());
                    worksheet.addCell(label);
                    label = new Label(col++, row, channelUserVO.getUserCode());
                    worksheet.addCell(label);
                    UserBalancesVO userBalanceVO = null;
                    Iterator itr1 = channelUserVO.getUserBalanceList().iterator();
                    while (itr1.hasNext()) {
                        userBalanceVO = (UserBalancesVO) itr1.next();
                        label = new Label(col, row, userBalanceVO.getProductShortName());
                        worksheet.addCell(label);
                        label = new Label(col + 1, row, userBalanceVO.getBalanceStr());
                        worksheet.addCell(label);
                        row++;
                    }
                    if (channelUserVO.getUserBalanceList().size() == 0) {
                        row++;
                    }
                    col = 0;
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
        }
    }

    public void writeMultipleExcel(ChannelUserTransferForm form, MessageResources p_messages, Locale p_locale, String p_fileName) {
        final String METHOD_NAME = "writeMultipleExcel";
        if (_log.isDebugEnabled()) {
            _log.debug("writeMultipleExcel", " p_locale: " + p_locale + ", p_fileName: " + p_fileName);
        }
        WritableWorkbook workbook = null;
        WritableSheet worksheet = null;
        SheetSettings sheetSetting = null;
        int indexcol = 0, productindex = 1;
        int col = 0, row = 0, count = 0;
        int userListSize = 0;
        int noOfTotalSheet = 0;
        userListSize = form.getUserHierarchyList().size();
        String noOfRowsInOneTemplate = Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE_USERHIERARCHY");
        int noOfRowsPerTemplate = 0;
        if (!BTSLUtil.isNullString(noOfRowsInOneTemplate)) {
            noOfRowsInOneTemplate = noOfRowsInOneTemplate.trim();
            noOfRowsPerTemplate = Integer.parseInt(noOfRowsInOneTemplate);
        } else {
            noOfRowsPerTemplate = 65500; // Default value of rows
        }
        // Number of sheet to display the user list
        noOfTotalSheet = BTSLUtil.parseDoubleToInt( Math.ceil(BTSLUtil.parseIntToDouble(userListSize) / noOfRowsPerTemplate));

        try {
            int k = 0;
            sheetSetting = new SheetSettings();
            workbook = Workbook.createWorkbook(new File(p_fileName));
            Iterator itr = form.getUserHierarchyList().iterator();
            while (k < noOfTotalSheet) {

                worksheet = workbook.createSheet("Template Sheet " + (k + 1), k);
                sheetSetting = worksheet.getSettings();
                sheetSetting.setShowGridLines(false);
                Label label = null;
                String keyName = null;
                row = 0;
                count = 0;
                indexcol = 0;
                int colSpanValue = form.getCategoryListSize();
                int colSpanV = colSpanValue + 4;
                int colSpan1 = colSpanV / 4;
                int colSpan2 = colSpanV / 2 - colSpan1;
                int colSpan3 = colSpanV / 4;
                int colSpan4 = colSpanV - colSpan1 - colSpan2 - colSpan3;

                col = 0;
                row++;
                if(!(p_messages==null))
                keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyviewdetails.heading");
                else
                keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyviewdetails.heading");
                label = new Label(col, row, keyName, times16format);
                worksheet.mergeCells(col, row, col + colSpanV - 1, row);
                worksheet.addCell(label);
                // 1//
                if (((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_HIERARCHY_SIZE))).intValue() < form.getUserHierarchyListSize()) {
                    col = 0;
                    row++;
					if(!(p_messages==null))
                    keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.msg.nooperationallowed", String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_HIERARCHY_SIZE))).intValue()));
                    else
                    keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.msg.nooperationallowed");
                    label = new Label(col, row, keyName);
                    worksheet.mergeCells(col, row, col + colSpanV - 1, row);
                    worksheet.addCell(label);
                }
                // 2//
                col = 0;
                row++;
                if(!(p_messages==null))
                keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.domain");
                else
                keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.domain");
                label = new Label(col, row, keyName + ":");
                worksheet.mergeCells(col, row, col + colSpan1 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan1;
                label = new Label(col, row, form.getDomainCodeDesc());
                worksheet.mergeCells(col, row, col + colSpan2 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan2;
                if(!(p_messages==null))
                keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.usercategory");
                else
                keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.usercategory");
                label = new Label(col, row, keyName + ":");
                worksheet.mergeCells(col, row, col + colSpan3 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan3;
                label = new Label(col, row, form.getTransferUserCategoryDesc());
                worksheet.mergeCells(col, row, col + colSpan4 - 1, row);
                worksheet.addCell(label);
                // 3//
                if ("transfer".equals(form.getRequestFor())) {
                    col = 0;
                    row++;
                    if(!(p_messages==null))
                    keyName = p_messages.getMessage(p_locale, "channeluser.userhiertrf.label.fromparentinfo");
                    else
                    keyName = PretupsRestUtil.getMessageString("channeluser.userhiertrf.label.fromparentinfo");
                    label = new Label(col, row, keyName);
                    worksheet.mergeCells(col, row, col + colSpanV - 1, row);
                    worksheet.addCell(label);
                }
                // 4//
                if (form.getMsisdn() != null && !"".equals(form.getMsisdn())) {
                    col = 0;
                    row++;
                    if(!(p_messages==null))
                    keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.msisdn");
                    else
                    keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.msisdn");
                    label = new Label(col, row, keyName + ":");
                    worksheet.mergeCells(col, row, col + colSpan1 - 1, row);
                    worksheet.addCell(label);
                    col = col + colSpan1;
                    label = new Label(col, row, form.getMsisdn());
                    worksheet.mergeCells(col, row, col + colSpan2 + colSpan3 + colSpan4 - 1, row);
                    worksheet.addCell(label);
                }
                // 5//
                if (form.getLoginID() != null && !"".equals(form.getLoginID())) {
                    col = 0;
                    row++;
                    if(!(p_messages==null))
                    keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.loginid");
                    else
                    	keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.loginid");
                    label = new Label(col, row, keyName + ":");
                    worksheet.mergeCells(col, row, col + colSpan1 - 1, row);
                    worksheet.addCell(label);
                    col = col + colSpan1;
                    label = new Label(col, row, form.getLoginID());
                    worksheet.mergeCells(col, row, col + colSpan2 + colSpan3 + colSpan4 - 1, row);
                    worksheet.addCell(label);
                }
                // 6//
                col = 0;
                row++;
                if(!(p_messages==null))
                keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.zone");
                else
                keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.zone");
                label = new Label(col, row, keyName + ":");
                worksheet.mergeCells(col, row, col + colSpan1 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan1;
                label = new Label(col, row, form.getZoneCodeDesc());
                worksheet.mergeCells(col, row, col + colSpan2 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan2;
                if(!(p_messages==null))
                keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.fromowner");
                else
                	keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.fromowner");
                label = new Label(col, row, keyName + ":");
                worksheet.mergeCells(col, row, col + colSpan3 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan3;
                label = new Label(col, row, form.getOwnerName());
                worksheet.mergeCells(col, row, col + colSpan4 - 1, row);
                worksheet.addCell(label);
                // 7//
                col = 0;
                row++;
                if(!(p_messages==null))
                keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.fromparentuser");
                else
                	keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.fromparentuser");
                label = new Label(col, row, keyName + ":");
                worksheet.mergeCells(col, row, col + colSpan1 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan1;
                label = new Label(col, row, form.getParentUserName());
                worksheet.mergeCells(col, row, col + colSpan2 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan2;
                if(!(p_messages==null))
                keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.fromparentcategory");
                else
                	keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.fromparentcategory");
                label = new Label(col, row, keyName + ":");
                worksheet.mergeCells(col, row, col + colSpan3 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan3;
                label = new Label(col, row, form.getParentCategoryDesc());
                worksheet.mergeCells(col, row, col + colSpan4 - 1, row);
                worksheet.addCell(label);

                if (form.getToParentCategoryList() != null) {
                    // 8//
                    col = 0;
                    row++;
                    if(!(p_messages==null))
                    keyName = p_messages.getMessage(p_locale, "channeluser.userhiertrf.label.toparentinfo");
                    else
                    	keyName = PretupsRestUtil.getMessageString("channeluser.userhiertrf.label.toparentinfo");
                    label = new Label(col, row, keyName);
                    worksheet.mergeCells(col, row, col + colSpanV - 1, row);
                    worksheet.addCell(label);
                    // 9//
                    col = 0;
                    row++;
                    if(!(p_messages==null))
                    keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.tozone");
                    else
                    	keyName =PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.tozone");
                    label = new Label(col, row, keyName + ":");
                    worksheet.mergeCells(col, row, col + colSpan1 - 1, row);
                    worksheet.addCell(label);
                    col = col + colSpan1;
                    label = new Label(col, row, form.getToZoneCodeDesc());
                    worksheet.mergeCells(col, row, col + colSpan2 - 1, row);
                    worksheet.addCell(label);
                    col = col + colSpan2;
                    if(!(p_messages==null))
                    keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.toowner");
                    else
                    	keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.toowner");
                    label = new Label(col, row, keyName + ":");
                    worksheet.mergeCells(col, row, col + colSpan3 - 1, row);
                    worksheet.addCell(label);
                    col = col + colSpan3;
                    label = new Label(col, row, form.getToOwnerName());
                    worksheet.mergeCells(col, row, col + colSpan4 - 1, row);
                    worksheet.addCell(label);
                    // 10//
                    col = 0;
                    row++;
                    if(!(p_messages==null))
                    keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.toparentuser");
                    else
                    	keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.toparentuser");
                    label = new Label(col, row, keyName + ":");
                    worksheet.mergeCells(col, row, col + colSpan1 - 1, row);
                    worksheet.addCell(label);
                    col = col + colSpan1;
                    label = new Label(col, row, form.getToParentUserName());
                    worksheet.mergeCells(col, row, col + colSpan2 - 1, row);
                    worksheet.addCell(label);
                    col = col + colSpan2;
                    if(!(p_messages==null))
                    keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.toparentcategory");
                    else
                    	keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.toparentcategory");
                    label = new Label(col, row, keyName + ":");
                    worksheet.mergeCells(col, row, col + colSpan3 - 1, row);
                    worksheet.addCell(label);
                    col = col + colSpan3;
                    label = new Label(col, row, form.getToParentCategoryDesc());
                    worksheet.mergeCells(col, row, col + colSpan4 - 1, row);
                    worksheet.addCell(label);
                }

                if (form.getCategoryList() != null) {
                    // 11//
                    col = 0;
                    row++;
                    for (int catNo = 1; catNo <= colSpanValue; catNo++, indexcol++) {
                    	if(!(p_messages==null))
                        keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.childlevel");
                    	else
                    	keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.childlevel");
                        label = new Label(col++, row, keyName + catNo, times16format);
                        worksheet.addCell(label);
                    }
                    if(!(p_messages==null))
                    keyName = p_messages.getMessage(p_locale, "channeluser.selectfromowner.label.status");
                    else
                    keyName = PretupsRestUtil.getMessageString("channeluser.selectfromowner.label.status");
                    label = new Label(col++, row, keyName, times16format);
                    worksheet.addCell(label);
                    if(!(p_messages==null))
                    keyName = p_messages.getMessage(p_locale, "channeluser.selectfromowner.label.usercode");
                    else
                    keyName = PretupsRestUtil.getMessageString("channeluser.selectfromowner.label.usercode");
                    label = new Label(col++, row, keyName, times16format);
                    worksheet.addCell(label);
                    if(!(p_messages==null))
                    keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.productshortname");
                    else
                    keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.productshortname");
                    label = new Label(col++, row, keyName, times16format);
                    worksheet.addCell(label);
                    if(!(p_messages==null))
                    keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.productbalance");
                    else
                    	keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.productbalance");
                    label = new Label(col++, row, keyName, times16format);
                    worksheet.addCell(label);
                }

                // 12//
                if (form.getUserHierarchyList() != null) {
                    col = 0;
                    row++;
                    if (form.getUserHierarchyListSize() == 0) {
                    	if(!(p_messages==null))
                        keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.msg.nodata");
                    	else
                    		keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.msg.nodata");
                        label = new Label(col, row, keyName, times16format);
                        worksheet.mergeCells(col, row, col + colSpanV + 4, row);
                        worksheet.addCell(label);
                    }

                    ChannelUserVO channelUserVO = null;
                    while (itr.hasNext() && count < noOfRowsPerTemplate) {
                        channelUserVO = (ChannelUserVO) itr.next();
                        productindex = 1;
                        int intlevel = Integer.parseInt(String.valueOf(channelUserVO.getUserlevel()));
                        for (int i = 0; i < ((int) intlevel) - 1; i++) {
                            ++productindex;
                            label = new Label(col++, row, "");
                            worksheet.addCell(label);
                        }
                        label = new Label(col++, row, channelUserVO.getUserNameWithCategory());
                        worksheet.addCell(label);
                        for (int j = productindex; j < indexcol; j++) {
                            label = new Label(col++, row, "");
                            worksheet.addCell(label);
                        }
                        label = new Label(col++, row, channelUserVO.getStatusDesc());
                        worksheet.addCell(label);
                        label = new Label(col++, row, channelUserVO.getUserCode());
                        worksheet.addCell(label);
                        UserBalancesVO userBalanceVO = null;
                        Iterator itr1 = channelUserVO.getUserBalanceList().iterator();
                        while (itr1.hasNext()) {
                            userBalanceVO = (UserBalancesVO) itr1.next();
                            label = new Label(col, row, userBalanceVO.getProductShortName());
                            worksheet.addCell(label);
                            label = new Label(col + 1, row, userBalanceVO.getBalanceStr());
                            worksheet.addCell(label);
                            row++;
                        }
                        if (channelUserVO.getUserBalanceList().size() == 0) {
                            row++;
                        }
                        col = 0;
                        count++;
                    }
                }
                if (count == noOfRowsPerTemplate) {
                	if(!(p_messages==null))
                    keyName = p_messages.getMessage(p_locale, "channeluser.userhierarchyview.label.nextsheet");
                	else
                		keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.nextsheet");
                    label = new Label(col++, row, keyName, times16format);
                    worksheet.addCell(label);
                }
                k++;
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
            if (_log.isDebugEnabled()) {
                _log.debug("writeMultipleExcel", " Exiting");
            }
        }
    }
    
    public void writeMultipleExcel(ChannelUserTransferForm form, Locale p_locale, String p_fileName) {
        final String METHOD_NAME = "writeMultipleExcel";
        if (_log.isDebugEnabled()) {
            _log.debug("writeMultipleExcel", " p_locale: " + p_locale + ", p_fileName: " + p_fileName);
        }
        WritableWorkbook workbook = null;
        WritableSheet worksheet = null;
        SheetSettings sheetSetting = null;
        int indexcol = 0, productindex = 1;
        int col = 0, row = 0, count = 0;
        int userListSize = 0;
        int noOfTotalSheet = 0;
        userListSize = form.getUserHierarchyList().size();
        String noOfRowsInOneTemplate = Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE_USERHIERARCHY");
        int noOfRowsPerTemplate = 0;
        if (!BTSLUtil.isNullString(noOfRowsInOneTemplate)) {
            noOfRowsInOneTemplate = noOfRowsInOneTemplate.trim();
            noOfRowsPerTemplate = Integer.parseInt(noOfRowsInOneTemplate);
        } else {
            noOfRowsPerTemplate = 65500; // Default value of rows
        }
        // Number of sheet to display the user list
        noOfTotalSheet = BTSLUtil.parseDoubleToInt( Math.ceil(BTSLUtil.parseIntToDouble(userListSize) / noOfRowsPerTemplate));

        try {
            int k = 0;
            sheetSetting = new SheetSettings();
            workbook = Workbook.createWorkbook(new File(p_fileName));
            Iterator itr = form.getUserHierarchyList().iterator();
            while (k < noOfTotalSheet) {

                worksheet = workbook.createSheet("Template Sheet " + (k + 1), k);
                sheetSetting = worksheet.getSettings();
                sheetSetting.setShowGridLines(false);
                Label label = null;
                String keyName = null;
                row = 0;
                count = 0;
                indexcol = 0;
                int colSpanValue = form.getCategoryListSize();
                int colSpanV = colSpanValue + 4;
                int colSpan1 = colSpanV / 4;
                int colSpan2 = colSpanV / 2 - colSpan1;
                int colSpan3 = colSpanV / 4;
                int colSpan4 = colSpanV - colSpan1 - colSpan2 - colSpan3;

                col = 0;
                row++;
                keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyviewdetails.heading");
                label = new Label(col, row, keyName, times16format);
                worksheet.mergeCells(col, row, col + colSpanV - 1, row);
                worksheet.addCell(label);
                // 1//
                if (((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_HIERARCHY_SIZE))).intValue() < form.getUserHierarchyListSize()) {
                    col = 0;
                    row++;
                    keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.msg.nooperationallowed");
                    label = new Label(col, row, keyName);
                    worksheet.mergeCells(col, row, col + colSpanV - 1, row);
                    worksheet.addCell(label);
                }
                // 2//
                col = 0;
                row++;
                keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.domain");
                label = new Label(col, row, keyName + ":");
                worksheet.mergeCells(col, row, col + colSpan1 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan1;
                label = new Label(col, row, form.getDomainCodeDesc());
                worksheet.mergeCells(col, row, col + colSpan2 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan2;
                keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.usercategory");
                label = new Label(col, row, keyName + ":");
                worksheet.mergeCells(col, row, col + colSpan3 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan3;
                label = new Label(col, row, form.getTransferUserCategoryDesc());
                worksheet.mergeCells(col, row, col + colSpan4 - 1, row);
                worksheet.addCell(label);
                // 3//
                if ("transfer".equals(form.getRequestFor())) {
                    col = 0;
                    row++;
                    keyName = PretupsRestUtil.getMessageString("channeluser.userhiertrf.label.fromparentinfo");
                    label = new Label(col, row, keyName);
                    worksheet.mergeCells(col, row, col + colSpanV - 1, row);
                    worksheet.addCell(label);
                }
                // 4//
                if (form.getMsisdn() != null && !"".equals(form.getMsisdn())) {
                    col = 0;
                    row++;
                    keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.msisdn");
                    label = new Label(col, row, keyName + ":");
                    worksheet.mergeCells(col, row, col + colSpan1 - 1, row);
                    worksheet.addCell(label);
                    col = col + colSpan1;
                    label = new Label(col, row, form.getMsisdn());
                    worksheet.mergeCells(col, row, col + colSpan2 + colSpan3 + colSpan4 - 1, row);
                    worksheet.addCell(label);
                }
                // 5//
                if (form.getLoginID() != null && !"".equals(form.getLoginID())) {
                    col = 0;
                    row++;
                    keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.loginid");
                    label = new Label(col, row, keyName + ":");
                    worksheet.mergeCells(col, row, col + colSpan1 - 1, row);
                    worksheet.addCell(label);
                    col = col + colSpan1;
                    label = new Label(col, row, form.getLoginID());
                    worksheet.mergeCells(col, row, col + colSpan2 + colSpan3 + colSpan4 - 1, row);
                    worksheet.addCell(label);
                }
                // 6//
                col = 0;
                row++;
                keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.zone");
                label = new Label(col, row, keyName + ":");
                worksheet.mergeCells(col, row, col + colSpan1 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan1;
                label = new Label(col, row, form.getZoneCodeDesc());
                worksheet.mergeCells(col, row, col + colSpan2 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan2;
                keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.fromowner");
                label = new Label(col, row, keyName + ":");
                worksheet.mergeCells(col, row, col + colSpan3 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan3;
                label = new Label(col, row, form.getOwnerName());
                worksheet.mergeCells(col, row, col + colSpan4 - 1, row);
                worksheet.addCell(label);
                // 7//
                col = 0;
                row++;
                keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.fromparentuser");
                label = new Label(col, row, keyName + ":");
                worksheet.mergeCells(col, row, col + colSpan1 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan1;
                label = new Label(col, row, form.getParentUserName());
                worksheet.mergeCells(col, row, col + colSpan2 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan2;
                keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.fromparentcategory");
                label = new Label(col, row, keyName + ":");
                worksheet.mergeCells(col, row, col + colSpan3 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan3;
                label = new Label(col, row, form.getParentCategoryDesc());
                worksheet.mergeCells(col, row, col + colSpan4 - 1, row);
                worksheet.addCell(label);

                if (form.getToParentCategoryList() != null) {
                    // 8//
                    col = 0;
                    row++;
                    keyName = PretupsRestUtil.getMessageString("channeluser.userhiertrf.label.toparentinfo");
                    label = new Label(col, row, keyName);
                    worksheet.mergeCells(col, row, col + colSpanV - 1, row);
                    worksheet.addCell(label);
                    // 9//
                    col = 0;
                    row++;
                    keyName =PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.tozone");
                    label = new Label(col, row, keyName + ":");
                    worksheet.mergeCells(col, row, col + colSpan1 - 1, row);
                    worksheet.addCell(label);
                    col = col + colSpan1;
                    label = new Label(col, row, form.getToZoneCodeDesc());
                    worksheet.mergeCells(col, row, col + colSpan2 - 1, row);
                    worksheet.addCell(label);
                    col = col + colSpan2;
                    keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.toowner");
                    label = new Label(col, row, keyName + ":");
                    worksheet.mergeCells(col, row, col + colSpan3 - 1, row);
                    worksheet.addCell(label);
                    col = col + colSpan3;
                    label = new Label(col, row, form.getToOwnerName());
                    worksheet.mergeCells(col, row, col + colSpan4 - 1, row);
                    worksheet.addCell(label);
                    // 10//
                    col = 0;
                    row++;
                    keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.toparentuser");
                    label = new Label(col, row, keyName + ":");
                    worksheet.mergeCells(col, row, col + colSpan1 - 1, row);
                    worksheet.addCell(label);
                    col = col + colSpan1;
                    label = new Label(col, row, form.getToParentUserName());
                    worksheet.mergeCells(col, row, col + colSpan2 - 1, row);
                    worksheet.addCell(label);
                    col = col + colSpan2;
                    keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.toparentcategory");
                    label = new Label(col, row, keyName + ":");
                    worksheet.mergeCells(col, row, col + colSpan3 - 1, row);
                    worksheet.addCell(label);
                    col = col + colSpan3;
                    label = new Label(col, row, form.getToParentCategoryDesc());
                    worksheet.mergeCells(col, row, col + colSpan4 - 1, row);
                    worksheet.addCell(label);
                }

                row++;// this rwo will stay empth
                
                if (form.getCategoryList() != null) {
                    // 11//
                    col = 0;
                    row++;
                    for (int catNo = 1; catNo <= colSpanValue; catNo++, indexcol++) {

                    	keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.childlevel");
                        label = new Label(col++, row, keyName + catNo, times16format);
                        worksheet.addCell(label);
                    }

                    keyName = PretupsRestUtil.getMessageString("channeluser.selectfromowner.label.status");
                    label = new Label(col++, row, keyName, times16format);
                    worksheet.addCell(label);

                    keyName = PretupsRestUtil.getMessageString("channeluser.selectfromowner.label.usercode");
                    label = new Label(col++, row, keyName, times16format);
                    worksheet.addCell(label);

                    keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.productshortname");
                    label = new Label(col++, row, keyName, times16format);
                    worksheet.addCell(label);
                    keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.productbalance");
                    label = new Label(col++, row, keyName, times16format);
                    worksheet.addCell(label);
                }

                // 12//
                if (form.getUserHierarchyList() != null) {
                    col = 0;
                    row++;
                    if (form.getUserHierarchyListSize() == 0) {
                    	keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.msg.nodata");
                        label = new Label(col, row, keyName, times16format);
                        worksheet.mergeCells(col, row, col + colSpanV + 4, row);
                        worksheet.addCell(label);
                    }

                    ChannelUserVO channelUserVO = null;
                    while (itr.hasNext() && count < noOfRowsPerTemplate) {
                        channelUserVO = (ChannelUserVO) itr.next();
                        productindex = 1;
                        int intlevel = Integer.parseInt(String.valueOf(channelUserVO.getUserlevel()));
                        for (int i = 0; i < ((int) intlevel) - 1; i++) {
                            ++productindex;
                            label = new Label(col++, row, "");
                            worksheet.addCell(label);
                        }
                        label = new Label(col++, row, channelUserVO.getUserNameWithCategory());
                        worksheet.addCell(label);
                        for (int j = productindex; j < indexcol; j++) {
                            label = new Label(col++, row, "");
                            worksheet.addCell(label);
                        }
                        label = new Label(col++, row, channelUserVO.getStatusDesc());
                        worksheet.addCell(label);
                        label = new Label(col++, row, channelUserVO.getUserCode());
                        worksheet.addCell(label);
                        UserBalancesVO userBalanceVO = null;
                        Iterator itr1 = channelUserVO.getUserBalanceList().iterator();
                        while (itr1.hasNext()) {
                            userBalanceVO = (UserBalancesVO) itr1.next();
                            label = new Label(col, row, userBalanceVO.getProductShortName());
                            worksheet.addCell(label);
                            label = new Label(col + 1, row, userBalanceVO.getBalanceStr());
                            worksheet.addCell(label);
                            row++;
                        }
                        if (channelUserVO.getUserBalanceList().size() == 0) {
                            row++;
                        }
                        col = 0;
                        count++;
                    }
                }
                if (count == noOfRowsPerTemplate) {
                	keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.nextsheet");
                    label = new Label(col++, row, keyName, times16format);
                    worksheet.addCell(label);
                }
                k++;
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
            if (_log.isDebugEnabled()) {
                _log.debug("writeMultipleExcel", " Exiting");
            }
        }
    }
    
    public void writeMultipleExcelX(ChannelUserTransferForm form, MessageResources p_messages, Locale p_locale, String p_fileName) {
        final String METHOD_NAME = "writeMultipleExcelX";
        if (_log.isDebugEnabled()) {
            _log.debug("writeMultipleExcelX", " p_locale: " + p_locale + ", p_fileName: " + p_fileName);
        }
       
        int indexcol = 0, productindex = 1;
        int col = 0, row = 0, count = 0;
        int userListSize = 0;
        int noOfTotalSheet = 0;
        userListSize = form.getUserHierarchyList().size();
        org.apache.poi.ss.usermodel.Workbook workbook= new XSSFWorkbook();
        String noOfRowsInOneTemplate = Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE_USERHIERARCHY");
        int noOfRowsPerTemplate = 0;
        
        CellStyle myheaderStyle = null;
        CreationHelper factory = workbook.getCreationHelper();
        myheaderStyle = workbook.createCellStyle();
        Font times16font = workbook.createFont();
        
        final short fontHeight = 14;
        times16font.setFontHeightInPoints(fontHeight);
        times16font.setBold(true);
        myheaderStyle.setFont(times16font);
        
        if (!BTSLUtil.isNullString(noOfRowsInOneTemplate)) {
            noOfRowsInOneTemplate = noOfRowsInOneTemplate.trim();
            noOfRowsPerTemplate = Integer.parseInt(noOfRowsInOneTemplate);
        } else {
            noOfRowsPerTemplate = 65500; // Default value of rows
        }
        // Number of sheet to display the user list
        noOfTotalSheet =BTSLUtil.parseDoubleToInt( Math.ceil(BTSLUtil.parseIntToDouble(userListSize) / noOfRowsPerTemplate));
        Row headerRow = null;
        Cell cell =null;
        try {
            int k = 0;
            String keyName =null;
            Iterator itr = form.getUserHierarchyList().iterator();
            int userHierarchySize = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_HIERARCHY_SIZE))).intValue();
            try (FileOutputStream outputStream = new FileOutputStream(p_fileName)) {
            while (k < noOfTotalSheet) {

            	int colSpanValue = form.getCategoryListSize();
                int colSpanV = colSpanValue + 4;
                int colSpan1 = colSpanV / 4;
                int colSpan2 = colSpanV / 2 - colSpan1;
                int colSpan3 = colSpanV / 4;
                int colSpan4 = colSpanV - colSpan1 - colSpan2 - colSpan3;
            	
            		
        			Sheet sheet = workbook.createSheet("Template sheet "+(k+1));

        			Font headerFontd = workbook.createFont();
        			headerFontd.setColor(Font.COLOR_NORMAL);
        			headerFontd.setFontHeightInPoints(BTSLUtil.parseIntToShort(12));
        			CellStyle headerCellStyle = workbook.createCellStyle();
        			headerCellStyle.setFont(headerFontd);
        			row++;
        			headerRow = sheet.createRow(row);
        			keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyviewdetails.heading");
        			cell = headerRow.createCell(col);
    				cell.setCellValue(keyName);
    				cell.setCellStyle(myheaderStyle);
    				
    				if (userHierarchySize < form.getUserHierarchyListSize()) {
                        col = 0;
                        row++;
                        headerRow = sheet.createRow(row);
                        keyName = PretupsRestUtil.getMessageString( "channeluser.userhierarchyview.msg.nooperationallowed", new String[]{String.valueOf(userHierarchySize)});
                        cell = headerRow.createCell(col);
        				cell.setCellValue(keyName);
        				cell.setCellStyle(headerCellStyle);
                    }
    				row++;
    				headerRow = sheet.createRow(row);
                    keyName = PretupsRestUtil.getMessageString( "channeluser.userhierarchyview.label.domain");
                    cell = headerRow.createCell(col);
    				cell.setCellValue(keyName);
    				cell.setCellStyle(headerCellStyle);
    				col = col + colSpan1;
    				cell = headerRow.createCell(col);
    				cell.setCellValue(form.getDomainCodeDesc());
    				cell.setCellStyle(headerCellStyle);
    				col = col + colSpan2;
    				keyName = PretupsRestUtil.getMessageString( "channeluser.userhierarchyview.label.usercategory");
    				cell = headerRow.createCell(col);
    				cell.setCellValue(keyName);
    				cell.setCellStyle(headerCellStyle);
    				col = col + colSpan3;
    				cell = headerRow.createCell(col);
    				cell.setCellValue(form.getTransferUserCategoryDesc());
    				cell.setCellStyle(headerCellStyle);
    				
    				if ("transfer".equals(form.getRequestFor())) {
                        col = 0;
                        row++;
                        headerRow = sheet.createRow(row);
                        keyName = PretupsRestUtil.getMessageString( "channeluser.userhiertrf.label.fromparentinfo");
                        cell = headerRow.createCell(col);
        				cell.setCellValue(keyName);
        				cell.setCellStyle(headerCellStyle);
                    }
    				if (form.getMsisdn() != null && !"".equals(form.getMsisdn())) {
                        col = 0;
                        row++;
                        headerRow = sheet.createRow(row);
                        keyName = PretupsRestUtil.getMessageString( "channeluser.userhierarchyview.label.msisdn");
                        cell = headerRow.createCell(col);
        				cell.setCellValue(keyName);
        				cell.setCellStyle(headerCellStyle);
                        col = col + colSpan1;
                        cell = headerRow.createCell(col);
        				cell.setCellValue(form.getMsisdn());
        				cell.setCellStyle(headerCellStyle);
                    }
    				if (form.getLoginID() != null && !"".equals(form.getLoginID())) {
                        col = 0;
                        row++;
                        headerRow = sheet.createRow(row);
                        keyName = PretupsRestUtil.getMessageString( "channeluser.userhierarchyview.label.loginid");
                        cell = headerRow.createCell(col);
        				cell.setCellValue(keyName);
        				cell.setCellStyle(headerCellStyle);
                        col = col + colSpan1;
                        cell = headerRow.createCell(col);
        				cell.setCellValue(form.getLoginID());
        				cell.setCellStyle(headerCellStyle);
                    }
    				col = 0;
                    row++;
                    headerRow = sheet.createRow(row);
                    keyName = PretupsRestUtil.getMessageString( "channeluser.userhierarchyview.label.zone");
                    cell = headerRow.createCell(col);
    				cell.setCellValue(keyName);
    				cell.setCellStyle(headerCellStyle);
                    col = col + colSpan1;
                    cell = headerRow.createCell(col);
    				cell.setCellValue(form.getZoneCodeDesc());
    				cell.setCellStyle(headerCellStyle);
                    col = col + colSpan2;
                    keyName = PretupsRestUtil.getMessageString( "channeluser.userhierarchyview.label.fromowner");
                    cell = headerRow.createCell(col);
    				cell.setCellValue(keyName);
    				cell.setCellStyle(headerCellStyle);
                    col = col + colSpan3;
                    cell = headerRow.createCell(col);
    				cell.setCellValue(form.getOwnerName());
    				cell.setCellStyle(headerCellStyle);
                    // 7//
                    col = 0;
                    row++;
                    headerRow = sheet.createRow(row);
                    keyName = PretupsRestUtil.getMessageString( "channeluser.userhierarchyview.label.fromparentuser");
                    cell = headerRow.createCell(col);
    				cell.setCellValue(keyName);
    				cell.setCellStyle(headerCellStyle);
                    col = col + colSpan1;
                    cell = headerRow.createCell(col);
    				cell.setCellValue(form.getParentUserName());
    				cell.setCellStyle(headerCellStyle);
                    col = col + colSpan2;
                    keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.fromparentcategory");
                    cell = headerRow.createCell(col);
    				cell.setCellValue(keyName);
    				cell.setCellStyle(headerCellStyle);
                    col = col + colSpan3;
                    cell = headerRow.createCell(col);
    				cell.setCellValue(form.getParentCategoryDesc());
    				cell.setCellStyle(headerCellStyle);

                    if (form.getToParentCategoryList() != null) {
                        // 8//
                        col = 0;
                        row++;
                        headerRow = sheet.createRow(row);
                        keyName = PretupsRestUtil.getMessageString("channeluser.userhiertrf.label.toparentinfo");
                        cell = headerRow.createCell(col);
        				cell.setCellValue(keyName);
        				cell.setCellStyle(headerCellStyle);
                        // 9//
                        col = 0;
                        row++;
                        headerRow = sheet.createRow(row);
                        keyName = PretupsRestUtil.getMessageString( "channeluser.userhierarchyview.label.tozone");
                        cell = headerRow.createCell(col);
        				cell.setCellValue(keyName);
        				cell.setCellStyle(headerCellStyle);
                        col = col + colSpan1;
                        cell = headerRow.createCell(col);
        				cell.setCellValue(form.getToZoneCodeDesc());
        				cell.setCellStyle(headerCellStyle);
                        col = col + colSpan2;
                        keyName = PretupsRestUtil.getMessageString( "channeluser.userhierarchyview.label.toowner");
                        cell = headerRow.createCell(col);
        				cell.setCellValue(form.getToZoneCodeDesc());
        				cell.setCellStyle(headerCellStyle);
                        col = col + colSpan3;
                        cell = headerRow.createCell(col);
        				cell.setCellValue(form.getToOwnerName());
        				cell.setCellStyle(headerCellStyle);
                        // 10//
                        col = 0;
                        row++;
                        headerRow = sheet.createRow(row);
                        keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.toparentuser");
                        cell = headerRow.createCell(col);
        				cell.setCellValue(form.getToZoneCodeDesc());
        				cell.setCellStyle(headerCellStyle);
        				col = col + colSpan1;
        				 cell = headerRow.createCell(col);
         				cell.setCellValue(form.getToParentUserName());
         				cell.setCellStyle(headerCellStyle);
                        col = col + colSpan2;
                        keyName = PretupsRestUtil.getMessageString( "channeluser.userhierarchyview.label.toparentcategory");
                        cell = headerRow.createCell(col);
        				cell.setCellValue(form.getToZoneCodeDesc());
        				cell.setCellStyle(headerCellStyle);
                        col = col + colSpan3;
                        cell = headerRow.createCell(col);
        				cell.setCellValue(form.getToParentCategoryDesc());
        				cell.setCellStyle(headerCellStyle);
                    }
                    
                    headerRow = sheet.createRow(row);// this row will remain empty
                    
                    if (form.getCategoryList() != null) {
                        // 11//
                        col = 0;
                        row++;
                        headerRow = sheet.createRow(row);
                        for (int catNo = 1; catNo <= colSpanValue; catNo++, indexcol++) {
                            keyName = PretupsRestUtil.getMessageString( "channeluser.userhierarchyview.label.childlevel");
                            cell = headerRow.createCell(col++);
            				cell.setCellValue(keyName + catNo);
            				cell.setCellStyle(myheaderStyle);
                        }
                        keyName = PretupsRestUtil.getMessageString( "channeluser.selectfromowner.label.status");
                        cell = headerRow.createCell(col++);
        				cell.setCellValue(keyName);
        				cell.setCellStyle(myheaderStyle);
                        keyName = PretupsRestUtil.getMessageString("channeluser.selectfromowner.label.usercode");
                        cell = headerRow.createCell(col++);
        				cell.setCellValue(keyName );
        				cell.setCellStyle(myheaderStyle);
                        keyName = PretupsRestUtil.getMessageString( "channeluser.userhierarchyview.label.productshortname");
                        cell = headerRow.createCell(col++);
        				cell.setCellValue(keyName);
        				cell.setCellStyle(myheaderStyle);
                        keyName = PretupsRestUtil.getMessageString( "channeluser.userhierarchyview.label.productbalance");
                        cell = headerRow.createCell(col++);
        				cell.setCellValue(keyName );
        				cell.setCellStyle(myheaderStyle);
                    }
                    if (form.getUserHierarchyList() != null) {
                        col = 0;
                        row++;
                        headerRow = sheet.createRow(row);
                        if (form.getUserHierarchyListSize() == 0) {
                            keyName = PretupsRestUtil.getMessageString( "channeluser.userhierarchyview.msg.nodata");
                            cell = headerRow.createCell(col);
            				cell.setCellValue(keyName );
            				cell.setCellStyle(headerCellStyle);
                        }

                        ChannelUserVO channelUserVO = null;
                        while (itr.hasNext() && count < noOfRowsPerTemplate) {
                            channelUserVO = (ChannelUserVO) itr.next();
                            productindex = 1;
                            int intlevel = Integer.parseInt(String.valueOf(channelUserVO.getUserlevel()));
                            for (int i = 0; i < ((int) intlevel) - 1; i++) {
                                ++productindex;
                                cell = headerRow.createCell(col++);
                				cell.setCellValue("");
                				cell.setCellStyle(headerCellStyle);
                            }
                            cell = headerRow.createCell(col++);
            				cell.setCellValue(channelUserVO.getUserNameWithCategory() );
            				cell.setCellStyle(headerCellStyle);
                            for (int j = productindex; j < indexcol; j++) {
                            	cell = headerRow.createCell(col++);
                				cell.setCellValue("");
                				cell.setCellStyle(headerCellStyle);
                				}
                            cell = headerRow.createCell(col++);
            				cell.setCellValue(channelUserVO.getStatusDesc() );
            				cell.setCellStyle(headerCellStyle);
            				cell = headerRow.createCell(col++);
            				cell.setCellValue(channelUserVO.getUserCode());
            				cell.setCellStyle(headerCellStyle);
                            UserBalancesVO userBalanceVO = null;
                            Iterator itr1 = channelUserVO.getUserBalanceList().iterator();
                            while (itr1.hasNext()) {
                                userBalanceVO = (UserBalancesVO) itr1.next();
                                cell = headerRow.createCell(col);
                				cell.setCellValue(userBalanceVO.getProductShortName());
                				cell.setCellStyle(headerCellStyle);
                				cell = headerRow.createCell(col+1);
                 				cell.setCellValue(userBalanceVO.getBalanceStr());
                 				cell.setCellStyle(headerCellStyle);
                                row++;
                                headerRow = sheet.createRow(row);
                                
                            }
                            if (channelUserVO.getUserBalanceList().size() == 0) {
                                row++;
                                headerRow = sheet.createRow(row);
                            }
                            col = 0;
                            count++;
                        }
                    }
                    if (count == noOfRowsPerTemplate) {
                        keyName = PretupsRestUtil.getMessageString("channeluser.userhierarchyview.label.nextsheet");
                        cell = headerRow.createCell(col);
        				cell.setCellValue(keyName);
        				cell.setCellStyle(headerCellStyle);
                    }
                    k++;

        			
        			
        		} 
            workbook.write(outputStream);
            //String fileDat = new String(Base64.getEncoder().encode(outputStream.toByteArray()));
            //System.out.println(fileDat);
        	//workbook.write(outputStream);
            }catch (IOException e) {
        			_log.error("Error occurred while generating excel file report.", e);
        			throw new Exception("423423");
        		}
            	

            }
            //workbook.write(outputStream);
         catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeMultipleExcelX", " Exception e: " + e.getMessage());
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            workbook = null;
            if (_log.isDebugEnabled()) {
                _log.debug("writeMultipleExcelX", " Exiting");
            }
        }
    }

}

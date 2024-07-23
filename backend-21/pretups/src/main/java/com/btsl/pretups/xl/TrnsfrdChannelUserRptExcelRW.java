package com.btsl.pretups.xl;

/*
 * TrnsfrdChannelUserRptExcelRW.java
 * ------------------------------------------------------------------------
 * Name Date History
 * ------------------------------------------------------------------------
 * Vinay Kumar Singh 17/11/2008 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2008 Bharti Telesoft Ltd.
 * This class is responsible to generate and download the Transferred Channel
 * User Report in Excel format.
 */
import java.io.File;
import java.util.Iterator;
import java.util.Locale;

import com.btsl.util.MessageResources;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.web.pretups.channel.user.web.ChannelUserTransferForm;

import jxl.SheetSettings;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class TrnsfrdChannelUserRptExcelRW {

    private Log _log = LogFactory.getLog(this.getClass().getName());
    private WritableFont times16font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, true);
    private WritableCellFormat times16format = new WritableCellFormat(times16font);

    /**
     * @param form
     * @param p_messages
     * @param p_locale
     * @param p_fileName
     * @throws Exception
     */
    public void writeExcel(ChannelUserTransferForm form, MessageResources p_messages, Locale p_locale, String p_fileName) {
        final String METHOD_NAME = "writeExcel";
        if (_log.isDebugEnabled()) {
            _log.debug("TrnsfrdChannelUserRptExcelRW: writeExcel::", " p_locale: " + p_locale + ", p_fileName: " + p_fileName);
        }
        WritableWorkbook workbook = null;
        WritableSheet worksheet = null;
        SheetSettings sheetSetting = null;
        int indexcol = 0, preIndexCol = 0, productindex = 1;
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
            int colSpanV = (colSpanValue) * 2 + 4;
            int colSpan1 = colSpanV / 3;
            int colSpan2 = colSpan1;
            int colSpan3 = colSpanV / 4;
            int colSpan4 = colSpanV - colSpan1 - colSpan2 - colSpan3;

            col = 0;
            row++;
            keyName = p_messages.getMessage(p_locale, "transferred.channeluser.report.exl.heading");
            label = new Label(col, row, keyName, times16format);
            worksheet.mergeCells(col, row, col + colSpanV - 1, row);
            worksheet.addCell(label);
            // 1//
            if (((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_HIERARCHY_SIZE))).intValue() < form.getUserHierarchyListSize()) {
                col = 0;
                row++;
                keyName = p_messages.getMessage(p_locale, "transferred.channeluser.report.msg.nooperationallowed", String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_HIERARCHY_SIZE))).intValue()));
                label = new Label(col, row, keyName);
                worksheet.mergeCells(col, row, col + colSpanV - 1, row);
                worksheet.addCell(label);
            }
            // 2//
            col = 0;
            row++;
            keyName = p_messages.getMessage(p_locale, "transferred.channeluser.report.label.domain");
            label = new Label(col, row, keyName + ":");
            worksheet.mergeCells(col, row, col + colSpan1 - 1, row);
            worksheet.addCell(label);
            col = col + colSpan1;
            label = new Label(col, row, form.getDomainCodeDesc());
            worksheet.mergeCells(col, row, col + colSpan2 - 1, row);
            worksheet.addCell(label);
            col = col + colSpan2;
            keyName = p_messages.getMessage(p_locale, "transferred.channeluser.report.label.trfusercategory");
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
                keyName = p_messages.getMessage(p_locale, "transferred.channeluser.report.fromparentinfo");
                label = new Label(col, row, keyName);
                worksheet.mergeCells(col, row, col + colSpanV - 1, row);
                worksheet.addCell(label);
            }
            // 4//
            if (form.getMsisdn() != null && !("".equals(form.getMsisdn()))) {
                col = 0;
                row++;
                keyName = p_messages.getMessage(p_locale, "transferred.channeluser.report.label.msisdn");
                label = new Label(col, row, keyName + ":");
                worksheet.mergeCells(col, row, col + colSpan1 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan1;
                label = new Label(col, row, form.getMsisdn());
                worksheet.mergeCells(col, row, col + colSpan2 + colSpan3 + colSpan4 - 1, row);
                worksheet.addCell(label);
            }
            // 5//
            if (form.getLoginID() != null && !("".equals(form.getLoginID()))) {
                col = 0;
                row++;
                keyName = p_messages.getMessage(p_locale, "transferred.channeluser.report.label.loginid");
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
            keyName = p_messages.getMessage(p_locale, "transferred.channeluser.report.label.zone");
            label = new Label(col, row, keyName + ":");
            worksheet.mergeCells(col, row, col + colSpan1 - 1, row);
            worksheet.addCell(label);
            col = col + colSpan1;
            label = new Label(col, row, form.getZoneCodeDesc());
            worksheet.mergeCells(col, row, col + colSpan2 - 1, row);
            worksheet.addCell(label);
            col = col + colSpan2;
            keyName = p_messages.getMessage(p_locale, "transferred.channeluser.report.label.owner");
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
            keyName = p_messages.getMessage(p_locale, "transferred.channeluser.report.label.parentuser");
            label = new Label(col, row, keyName + ":");
            worksheet.mergeCells(col, row, col + colSpan1 - 1, row);
            worksheet.addCell(label);
            col = col + colSpan1;
            label = new Label(col, row, form.getParentUserName());
            worksheet.mergeCells(col, row, col + colSpan2 - 1, row);
            worksheet.addCell(label);
            col = col + colSpan2;
            keyName = p_messages.getMessage(p_locale, "transferred.channeluser.report.label.parentcategory");
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
                keyName = p_messages.getMessage(p_locale, "transferred.channeluser.report.toparentinfo");
                label = new Label(col, row, keyName);
                worksheet.mergeCells(col, row, col + colSpanV - 1, row);
                worksheet.addCell(label);
                // 9//
                col = 0;
                row++;
                keyName = p_messages.getMessage(p_locale, "transferred.channeluser.report.label.zone");
                label = new Label(col, row, keyName + ":");
                worksheet.mergeCells(col, row, col + colSpan1 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan1;
                label = new Label(col, row, form.getToZoneCodeDesc());
                worksheet.mergeCells(col, row, col + colSpan2 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan2;
                keyName = p_messages.getMessage(p_locale, "transferred.channeluser.report.label.owner");
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
                keyName = p_messages.getMessage(p_locale, "transferred.channeluser.report.label.parentuser");
                label = new Label(col, row, keyName + ":");
                worksheet.mergeCells(col, row, col + colSpan1 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan1;
                label = new Label(col, row, form.getToParentUserName());
                worksheet.mergeCells(col, row, col + colSpan2 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan2;
                keyName = p_messages.getMessage(p_locale, "transferred.channeluser.report.label.parentcategory");
                label = new Label(col, row, keyName + ":");
                worksheet.mergeCells(col, row, col + colSpan3 - 1, row);
                worksheet.addCell(label);
                col = col + colSpan3;
                label = new Label(col, row, form.getToParentCategoryDesc());
                worksheet.mergeCells(col, row, col + colSpan4 - 1, row);
                worksheet.addCell(label);
            }

            col = 0;
            row++;
            keyName = p_messages.getMessage(p_locale, "transferred.channeluser.report.label.newhierarchy");
            label = new Label(col, row, keyName, times16format);
            worksheet.mergeCells(col, row, colSpan1 - 1, row);
            worksheet.addCell(label);

            keyName = p_messages.getMessage(p_locale, "transferred.channeluser.report.label.prehierarchy");
            label = new Label(colSpan1, row, keyName, times16format);
            worksheet.mergeCells(colSpan1, row, colSpanV, row);
            worksheet.addCell(label);

            if (form.getCategoryList() != null) {
                // 11//
                col = 0;
                row++;
                for (int catNo = 1; catNo <= colSpanValue; catNo++, indexcol++) {
                    keyName = p_messages.getMessage(p_locale, "transferred.channeluser.report.label.childlevel");
                    label = new Label(col++, row, keyName + catNo, times16format);
                    worksheet.addCell(label);
                }
                for (int catNo = 1; catNo <= colSpanValue; catNo++, preIndexCol++) {
                    keyName = p_messages.getMessage(p_locale, "transferred.channeluser.report.label.childlevel");
                    label = new Label(col++, row, keyName + catNo, times16format);
                    worksheet.addCell(label);
                }
                keyName = p_messages.getMessage(p_locale, "transferred.channeluser.report.label.loginid");
                label = new Label(col++, row, keyName, times16format);
                worksheet.addCell(label);
                keyName = p_messages.getMessage(p_locale, "transferred.channeluser.report.label.usercode");
                label = new Label(col++, row, keyName, times16format);
                worksheet.addCell(label);
                keyName = p_messages.getMessage(p_locale, "transferred.channeluser.report.label.transferredbalance");
                label = new Label(col++, row, keyName, times16format);
                worksheet.addCell(label);
            }
            // 12//
            if (form.getTrnsfrdUsrHierList() != null) {
                col = 0;
                row++;
                if (form.getTrnsfrdUsrHierListSize() == 0) {
                    keyName = p_messages.getMessage(p_locale, "transferred.channeluser.report.msg.nodata");
                    label = new Label(col, row, keyName, times16format);
                    worksheet.mergeCells(col, row, col + colSpanV + 4, row);
                    worksheet.addCell(label);
                }
                Iterator itr = form.getTrnsfrdUsrHierList().iterator();
                ChannelUserVO channelUserVO = null;
                while (itr.hasNext()) {
                    channelUserVO = (ChannelUserVO) itr.next();
                    productindex = 1;
                    int intlevel = Integer.parseInt(String.valueOf(channelUserVO.getUserlevel()));

                    // Loop for current hierarchy
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

                    // Loop for previous hierarchy
                    productindex = 1;
                    for (int i = 0; i < ((int) intlevel) - 1; i++) {
                        ++productindex;
                        label = new Label(col++, row, "");
                        worksheet.addCell(label);
                    }
                    if (intlevel == 1) {
                        label = new Label(col++, row, channelUserVO.getPrevUserParentNameWithCategory());
                    } else {
                        label = new Label(col++, row, channelUserVO.getPrevUserNameWithCategory());
                    }
                    worksheet.addCell(label);
                    for (int j = productindex; j < preIndexCol; j++) {
                        label = new Label(col++, row, "");
                        worksheet.addCell(label);
                    }
                    label = new Label(col++, row, channelUserVO.getLoginID());
                    worksheet.addCell(label);
                    label = new Label(col++, row, channelUserVO.getMsisdn());
                    worksheet.addCell(label);
                    label = new Label(col++, row, channelUserVO.getPrevBalanceStr());
                    worksheet.addCell(label);
                    row++;
                    col = 0;
                }
                workbook.write();
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("TrnsfrdChannelUserRptExcelRW: writeExcel::", " Exception e: " + e.getMessage());
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
                _log.debug("TrnsfrdChannelUserRptExcelRW: writeExcel::", " Exiting");
            }
        }
    }
}

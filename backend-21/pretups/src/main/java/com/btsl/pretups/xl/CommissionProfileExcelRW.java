/*
 * CommissionProfileRW.java
 * 
 * Name Date History
 * ---------------------------------------------------------------
 * Vikas Jauhari 03/05/2011 Initial Creation
 * ---------------------------------------------------------------
 * Copyright (c) 2007 Comviva Technologies Ltd.
 */
package com.btsl.pretups.xl;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import com.btsl.util.MessageResources;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.AdditionalProfileDeatilsVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDeatilsVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.profile.web.CommissionProfileForm;

import jxl.SheetSettings;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * 
 * @author vikas.jauhari
 * 
 */

public class CommissionProfileExcelRW {

    private Log _log = LogFactory.getLog(this.getClass().getName());
    private WritableFont times12font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, true);
    private WritableCellFormat times12format = new WritableCellFormat(times12font);
    private WritableFont times16font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, true);
    private WritableCellFormat times16format = new WritableCellFormat(times16font);

    /**
     * @param form
     * @param p_messages
     * @param p_locale
     * @param p_fileName
     * @throws Exception
     * @author vikas.jauhari
     */
    public void writeExcel(ArrayList p_dataList, CommissionProfileForm p_form, MessageResources p_messages, Locale p_locale, String p_fileName) {
        final String METHOD_NAME = "writeExcel";
        if (_log.isDebugEnabled()) {
            _log.debug("writeExcel", " p_locale: " + p_locale + ", p_fileName: " + p_fileName);
        }
        WritableWorkbook workbook = null;
        WritableSheet worksheet = null;
        SheetSettings sheetSetting = null;
        int col = 0, row = 0;
        Label label = null;
        String keyName = null;
        String keyName1 = null;
        String keyName2 = null;
        CommissionProfileVO comProfDataVO = null;
        ArrayList commSlabData = null;
        ArrayList addCommSlabData = null;

        try {
            sheetSetting = new SheetSettings();
            workbook = Workbook.createWorkbook(new File(p_fileName));
            worksheet = workbook.createSheet("Commission Profile Details ", 0);
            sheetSetting = worksheet.getSettings();
            sheetSetting.setShowGridLines(true);

            int colSpanRowZero = 10;
            int colSpanRowOne1 = 2;
            int colSpanRowOne2 = 8;
            int colSpanRowTwo2 = 3;
            int colSpanheading = 9;
            int colSpantax = 6;
            int colSpantax1 = 4;

            col = 0;
            row = 0;
            keyName = "";
            label = new Label(col, row, keyName, times16format);
            worksheet.mergeCells(col, row, col, row);
            worksheet.addCell(label);

            col = 1;
            row = 0;
            keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.view.heading");
            label = new Label(col, row, keyName, times12format);
            worksheet.mergeCells(col, row, col + colSpanRowZero, row);
            worksheet.addCell(label);
            // Heading of Sheet//
            if (p_dataList != null && p_dataList.size() > 0) {
                int sn = 1;
                for (int i = 0, j = p_dataList.size(); i < j; i++) {
                    comProfDataVO = (CommissionProfileVO) p_dataList.get(i);

                    col = 0;
                    row++;
                    String sno = String.valueOf(sn);
                    label = new Label(col, row, sno);
                    worksheet.mergeCells(col, row, col, row);
                    worksheet.addCell(label);
                    sn++;

                    col = 1;
                    keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.networkname");
                    label = new Label(col, row, keyName + ":");
                    worksheet.mergeCells(col, row, col + colSpanRowOne1 - 1, row);
                    worksheet.addCell(label);

                    col = col + colSpanRowOne1;
                    label = new Label(col, row, comProfDataVO.getCommissionProfileSetVO().getNetworkName());
                    worksheet.mergeCells(col, row, col + colSpanRowOne2 - 1, row);
                    worksheet.addCell(label);
                    // 1//
                    col = 1;
                    row++;
                    keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.domain");
                    label = new Label(col, row, keyName + ":");
                    worksheet.mergeCells(col, row, col + colSpanRowOne1 - 1, row);
                    worksheet.addCell(label);

                    col = col + colSpanRowOne1;
                    label = new Label(col, row, p_form.getDomainName());
                    worksheet.mergeCells(col, row, col + colSpanRowTwo2 - 1, row);
                    worksheet.addCell(label);

                    col = col + colSpanRowTwo2;
                    keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.category");
                    label = new Label(col, row, keyName + ":");
                    worksheet.mergeCells(col, row, col + colSpanRowOne1 - 1, row);
                    worksheet.addCell(label);

                    col = col + colSpanRowOne1;
                    label = new Label(col, row, comProfDataVO.getCommissionProfileSetVO().getCategoryName());
                    worksheet.mergeCells(col, row, col + colSpanRowTwo2 - 1, row);
                    worksheet.addCell(label);
                    // 2//
                    col = 1;
                    row++;
                    keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.profilename");
                    label = new Label(col, row, keyName + ":");
                    worksheet.mergeCells(col, row, col + colSpanRowOne1 - 1, row);
                    worksheet.addCell(label);

                    col = col + colSpanRowOne1;
                    label = new Label(col, row, comProfDataVO.getCommissionProfileSetVO().getCommProfileSetName());
                    worksheet.mergeCells(col, row, col + colSpanRowTwo2 - 1, row);
                    worksheet.addCell(label);

                    col = col + colSpanRowTwo2;
                    keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.shortcode");
                    label = new Label(col, row, keyName + ":");
                    worksheet.mergeCells(col, row, col + colSpanRowOne1 - 1, row);
                    worksheet.addCell(label);

                    col = col + colSpanRowOne1;
                    label = new Label(col, row, comProfDataVO.getCommissionProfileSetVO().getShortCode());
                    worksheet.mergeCells(col, row, col + colSpanRowTwo2 - 1, row);
                    worksheet.addCell(label);
                    // 3//
                    col = 1;
                    row++;
                    keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.version");
                    label = new Label(col, row, keyName + ":");
                    worksheet.mergeCells(col, row, col + colSpanRowOne1 - 1, row);
                    worksheet.addCell(label);

                    col = col + colSpanRowOne1;
                    label = new Label(col, row, comProfDataVO.getCommissionProfileSetVersionVO().getCommProfileSetVersion());
                    worksheet.mergeCells(col, row, col + colSpanRowOne2 - 1, row);
                    worksheet.addCell(label);
                    // 4//
                    col = 1;
                    row++;
                    keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.applicablefrom");
                    label = new Label(col, row, keyName + ":");
                    worksheet.mergeCells(col, row, col + colSpanRowOne1 - 1, row);
                    worksheet.addCell(label);

                    keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.applicablefromhour");
                    keyName1 = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.applicablefromformat");
                    keyName2 = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.applicablefromhourformat");
                    String date = BTSLUtil.getDateStringFromDate(comProfDataVO.getCommissionProfileSetVersionVO().getApplicableFrom());
                    Format formatter = new SimpleDateFormat("HH.mm");
                    String tempTime = formatter.format(comProfDataVO.getCommissionProfileSetVersionVO().getApplicableFrom());
                    col = col + colSpanRowOne1;
                    label = new Label(col, row, date + " " + keyName1 + "  " + keyName + " : " + tempTime + " " + keyName2);
                    worksheet.mergeCells(col, row, col + colSpanRowOne2 - 1, row);
                    worksheet.addCell(label);
                    // 5//

                    col = 1;
                    row++;
                    keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.commissionslabsheading");
                    label = new Label(col, row, keyName, times16format);
                    worksheet.mergeCells(col, row, col + colSpanheading, row);
                    worksheet.addCell(label);
                    // 6//
                    col = 1;
                    row++;
                    keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.product");
                    label = new Label(col, row, keyName + ":");
                    worksheet.mergeCells(col, row, col + colSpanRowOne1 - 1, row);
                    worksheet.addCell(label);

                    col = col + colSpanRowOne1;
                    label = new Label(col, row, comProfDataVO.getCommissionProfileProductsVO().getProductCodeDesc());
                    worksheet.mergeCells(col, row, col + colSpanRowTwo2 - 1, row);
                    worksheet.addCell(label);

                    col = col + colSpanRowTwo2;
                    keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.muitipleof");
                    label = new Label(col, row, keyName + ":");
                    worksheet.mergeCells(col, row, col + colSpanRowOne1 - 1, row);
                    worksheet.addCell(label);
                    String multipleof = String.valueOf(comProfDataVO.getCommissionProfileProductsVO().getTransferMultipleOffAsString());
                    col = col + colSpanRowOne1;
                    label = new Label(col, row, multipleof);
                    worksheet.mergeCells(col, row, col + colSpanRowTwo2 - 1, row);
                    worksheet.addCell(label);
                    // 7//
                    col = 1;
                    row++;
                    keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.mintransfervalue");
                    label = new Label(col, row, keyName + ":");
                    worksheet.mergeCells(col, row, col + colSpanRowOne1 - 1, row);
                    worksheet.addCell(label);

                    String minTransVal = String.valueOf(comProfDataVO.getCommissionProfileProductsVO().getMinTransferValueAsString());
                    col = col + colSpanRowOne1;
                    label = new Label(col, row, minTransVal);
                    worksheet.mergeCells(col, row, col + colSpanRowTwo2 - 1, row);
                    worksheet.addCell(label);

                    col = col + colSpanRowTwo2;
                    keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.maxtransfervalue");
                    label = new Label(col, row, keyName + ":");
                    worksheet.mergeCells(col, row, col + colSpanRowOne1 - 1, row);
                    worksheet.addCell(label);

                    String maxTransVal = String.valueOf(comProfDataVO.getCommissionProfileProductsVO().getMaxTransferValueAsString());
                    col = col + colSpanRowOne1;
                    label = new Label(col, row, maxTransVal);
                    worksheet.mergeCells(col, row, col + colSpanRowTwo2 - 1, row);
                    worksheet.addCell(label);
                    // 8//
                    col = 1;
                    row++;
                    keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.taxonfoc");
                    label = new Label(col, row, keyName + ":");
                    worksheet.mergeCells(col, row, col + colSpanRowOne1 - 1, row);
                    worksheet.addCell(label);

                    col = col + colSpanRowOne1;
                    label = new Label(col, row, comProfDataVO.getCommissionProfileProductsVO().getTaxOnFOCApplicable());
                    worksheet.mergeCells(col, row, col + colSpanRowTwo2 - 1, row);
                    worksheet.addCell(label);

                    col = col + colSpanRowTwo2;
                    keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.taxcalculatedon");
                    label = new Label(col, row, keyName + ":");
                    worksheet.mergeCells(col, row, col + colSpanRowOne1 - 1, row);
                    worksheet.addCell(label);

                    col = col + colSpanRowOne1;
                    label = new Label(col, row, comProfDataVO.getCommissionProfileProductsVO().getTaxOnChannelTransfer());
                    worksheet.mergeCells(col, row, col + colSpanRowTwo2 - 1, row);
                    worksheet.addCell(label);
                    // 9//
                    col = 1;
                    row++;
                    keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.fromrange");
                    label = new Label(col, row, keyName);
                    worksheet.mergeCells(col, row, col, row + 1);
                    worksheet.addCell(label);

                    col = col + 1;
                    keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.torange");
                    label = new Label(col, row, keyName);
                    worksheet.mergeCells(col, row, col, row + 1);
                    worksheet.addCell(label);

                    col = col + 1;
                    keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.heading.margin");
                    label = new Label(col, row, keyName);
                    worksheet.mergeCells(col, row, col + colSpanRowOne1 - 1, row);
                    worksheet.addCell(label);

                    col = col + colSpanRowOne1;
                    keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.heading.taxes");
                    label = new Label(col, row, keyName);
                    worksheet.mergeCells(col, row, col + colSpantax - 1, row);
                    worksheet.addCell(label);
                    // 10//
                    col = 3;
                    row++;
                    keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.margintype");
                    label = new Label(col, row, keyName);
                    worksheet.addCell(label);
                    col++;
                    keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.marginrate");
                    label = new Label(col, row, keyName);
                    worksheet.addCell(label);
                    col++;
                    keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.tax1type");
                    label = new Label(col, row, keyName);
                    worksheet.addCell(label);
                    col++;
                    keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.tax1rate");
                    label = new Label(col, row, keyName);
                    worksheet.addCell(label);
                    col++;
                    keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.tax2type");
                    label = new Label(col, row, keyName);
                    worksheet.addCell(label);
                    col++;
                    keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.tax2rate");
                    label = new Label(col, row, keyName);
                    worksheet.addCell(label);
                    col++;
                    keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.tax3type");
                    label = new Label(col, row, keyName);
                    worksheet.addCell(label);
                    col++;
                    keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.tax3rate");
                    label = new Label(col, row, keyName);
                    worksheet.addCell(label);
                    // 11//
                    commSlabData = comProfDataVO.getCommProfileSlabDetail();
                    if (commSlabData != null && commSlabData.size() > 0) {
                        for (int k = 0, l = commSlabData.size(); k < l; k++) {
                            CommissionProfileDeatilsVO commissionProfileDeatilsVO = (CommissionProfileDeatilsVO) commSlabData.get(k);

                            col = 1;
                            row++;
                            label = new Label(col, row, commissionProfileDeatilsVO.getStartRangeAsString());
                            worksheet.addCell(label);
                            col++;
                            label = new Label(col, row, commissionProfileDeatilsVO.getEndRangeAsString());
                            worksheet.addCell(label);
                            col++;
                            label = new Label(col, row, commissionProfileDeatilsVO.getCommType());
                            worksheet.addCell(label);
                            col++;
                            String commRateAsString = String.valueOf(commissionProfileDeatilsVO.getCommRateAsString());
                            label = new Label(col, row, commRateAsString);
                            worksheet.addCell(label);
                            col++;
                            label = new Label(col, row, commissionProfileDeatilsVO.getTax1Type());
                            worksheet.addCell(label);
                            col++;
                            label = new Label(col, row, commissionProfileDeatilsVO.getTax1RateAsString());
                            worksheet.addCell(label);
                            col++;
                            label = new Label(col, row, commissionProfileDeatilsVO.getTax2Type());
                            worksheet.addCell(label);
                            col++;
                            label = new Label(col, row, commissionProfileDeatilsVO.getTax2RateAsString());
                            worksheet.addCell(label);
                            col++;
                            label = new Label(col, row, commissionProfileDeatilsVO.getTax3Type());
                            worksheet.addCell(label);
                            col++;
                            label = new Label(col, row, commissionProfileDeatilsVO.getTax3RateAsString());
                            worksheet.addCell(label);
                        }
                    } else {
                        throw new BTSLBaseException(this, "loadCommissionProfileList", "channel.processUploadedFile.error.msg.nodaodatalist");
                    }
                    if (comProfDataVO.isFlagAddCommProfExist()) {
                        col = 1;
                        row++;
                        keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.additionalslabsheading");
                        label = new Label(col, row, keyName, times16format);
                        worksheet.mergeCells(col, row, col + colSpanheading, row);
                        worksheet.addCell(label);
                        // 12//
                        col = 1;
                        row++;
                        keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.service");
                        label = new Label(col, row, keyName + ":");
                        worksheet.mergeCells(col, row, col + colSpanRowOne1 - 1, row);
                        worksheet.addCell(label);

                        col = col + colSpanRowOne1;
                        label = new Label(col, row, comProfDataVO.getAdditionalProfileServicesVO().getServiceTypeDesc());
                        worksheet.mergeCells(col, row, col + colSpanRowOne2 - 1, row);
                        worksheet.addCell(label);
                        // 13//
                        col = 1;
                        row++;
                        keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.mintransfervalue");
                        label = new Label(col, row, keyName + ":");
                        worksheet.mergeCells(col, row, col + colSpanRowOne1 - 1, row);
                        worksheet.addCell(label);

                        String minTransAddVal = String.valueOf(comProfDataVO.getAdditionalProfileServicesVO().getMinTransferValueAsString());
                        col = col + colSpanRowOne1;
                        label = new Label(col, row, minTransAddVal);
                        worksheet.mergeCells(col, row, col + colSpanRowTwo2 - 1, row);
                        worksheet.addCell(label);

                        col = col + colSpanRowTwo2;
                        keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.maxtransfervalue");
                        label = new Label(col, row, keyName + ":");
                        worksheet.mergeCells(col, row, col + colSpanRowOne1 - 1, row);
                        worksheet.addCell(label);

                        String maxTransAddVal = String.valueOf(comProfDataVO.getAdditionalProfileServicesVO().getMaxTransferValueAsString());
                        col = col + colSpanRowOne1;
                        label = new Label(col, row, maxTransAddVal);
                        worksheet.mergeCells(col, row, col + colSpanRowTwo2 - 1, row);
                        worksheet.addCell(label);
                        // 14//
                        col = 1;
                        row++;
                        keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.fromrange");
                        label = new Label(col, row, keyName);
                        worksheet.mergeCells(col, row, col, row + 1);
                        worksheet.addCell(label);

                        col = col + 1;
                        keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.torange");
                        label = new Label(col, row, keyName);
                        worksheet.mergeCells(col, row, col, row + 1);
                        worksheet.addCell(label);

                        col = col + 1;
                        keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.heading.margin");
                        label = new Label(col, row, keyName);
                        worksheet.mergeCells(col, row, col + colSpanRowOne1 - 1, row);
                        worksheet.addCell(label);

                        col = col + colSpanRowOne1;
                        keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.heading.diffrentialfactor");
                        label = new Label(col, row, keyName);
                        worksheet.mergeCells(col, row, col + colSpanRowOne1 - 1, row + 1);
                        worksheet.addCell(label);

                        col = col + colSpanRowOne1;
                        keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.heading.taxes");
                        label = new Label(col, row, keyName);
                        worksheet.mergeCells(col, row, col + colSpantax1 - 1, row);
                        worksheet.addCell(label);
                        // 15//

                        col = 3;
                        row++;
                        keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.margintype");
                        label = new Label(col, row, keyName);
                        worksheet.addCell(label);
                        col++;
                        keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.marginrate");
                        label = new Label(col, row, keyName);
                        worksheet.addCell(label);

                        col = col + 3;
                        keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.tax1type");
                        label = new Label(col, row, keyName);
                        worksheet.addCell(label);
                        col++;
                        keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.tax1rate");
                        label = new Label(col, row, keyName);
                        worksheet.addCell(label);
                        col++;
                        keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.tax2type");
                        label = new Label(col, row, keyName);
                        worksheet.addCell(label);
                        col++;
                        keyName = p_messages.getMessage(p_locale, "profile.commissionprofiledetailview.label.tax2rate");
                        label = new Label(col, row, keyName);
                        worksheet.addCell(label);
                        // 16//

                        addCommSlabData = comProfDataVO.getAddCommProfSlabDetails();
                        if (addCommSlabData != null && addCommSlabData.size() > 0) {
                            for (int m = 0, n = addCommSlabData.size(); m < n; m++) {
                                AdditionalProfileDeatilsVO additionalProfileDeatilsVO = (AdditionalProfileDeatilsVO) addCommSlabData.get(m);
                                col = 1;
                                row++;
                                label = new Label(col, row, additionalProfileDeatilsVO.getStartRangeAsString());
                                worksheet.addCell(label);
                                col++;
                                label = new Label(col, row, additionalProfileDeatilsVO.getEndRangeAsString());
                                worksheet.addCell(label);
                                col++;
                                label = new Label(col, row, additionalProfileDeatilsVO.getAddCommType());
                                worksheet.addCell(label);
                                col++;

                                String addCommRateAsString = String.valueOf(additionalProfileDeatilsVO.getAddCommRateAsString());
                                label = new Label(col, row, addCommRateAsString);
                                worksheet.addCell(label);

                                col++;
                                label = new Label(col, row, additionalProfileDeatilsVO.getDiffrentialFactorAsString());
                                worksheet.mergeCells(col, row, col + colSpanRowOne1 - 1, row);
                                worksheet.addCell(label);

                                col = col + colSpanRowOne1;
                                label = new Label(col, row, additionalProfileDeatilsVO.getTax1Type());
                                worksheet.addCell(label);
                                col++;
                                label = new Label(col, row, additionalProfileDeatilsVO.getTax1RateAsString());
                                worksheet.addCell(label);
                                col++;
                                label = new Label(col, row, additionalProfileDeatilsVO.getTax2Type());
                                worksheet.addCell(label);
                                col++;
                                label = new Label(col, row, additionalProfileDeatilsVO.getTax2RateAsString());
                                worksheet.addCell(label);
                                // 17//
                            }
                        } else {
                            throw new BTSLBaseException(this, "loadCommissionProfileList", "channel.processUploadedFile.error.msg.nodaodatalist");
                        }

                    }

                    col = 0;
                    row++;
                    label = new Label(col, row, "");
                    worksheet.mergeCells(col, row, col, row);
                    worksheet.addCell(label);
                    col = 1;
                    label = new Label(col, row, "");
                    worksheet.mergeCells(col, row, col + colSpanheading, row);
                    worksheet.addCell(label);
                    // 18-19//
                }
                workbook.write();
            } else {
                col = 0;
                row = 0;
                keyName = "";
                label = new Label(col, row, keyName, times16format);
                worksheet.mergeCells(col, row, col, row);
                worksheet.addCell(label);

                col = 1;
                row = row + 2;
                keyName = p_messages.getMessage(p_locale, "profile.commissionprofileversionlist.message.commissionprofileversionlistnotexist");
                label = new Label(col, row, keyName);
                worksheet.mergeCells(col, row, col + colSpanRowZero, row);
                worksheet.addCell(label);
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

}

package com.btsl.pretups.xl;

/*
 * @# BatchModifyC2SCardGroupExcelRW.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Sanjeew Feb 4,2009 Initial creation
 * Sanjeew Mar 18,2009 Modified for P2P service
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2009 Bharti Telesoft Ltd.
 * This class use for read write in xls file for batch Modify Card Group
 * creation.
 */

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import com.btsl.util.MessageResources;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cardgroup.businesslogic.BonusAccountDetailsVO;
import com.btsl.pretups.cardgroup.businesslogic.BonusBundleDetailVO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.xl.ExcelFileConstants;

import jxl.Cell;
import jxl.CellView;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class BatchModifyC2SCardGroupExcelRW {
    private static Log _log = LogFactory.getLog(BatchModifyC2SCardGroupExcelRW.class.getName());
    private MessageResources p_messages = null;
    private Locale p_locale = null;

    /**
     * @Method Name writeModifyExcel
     * @author Sanjeew.kumar
     * @param p_excelID
     * @param p_hashMap
     * @param messages
     * @param locale
     * @param p_fileName
     * @throws Exception
     */
    public void writeModifyExcel(String p_excelID, String p_module, HashMap p_hashMap, MessageResources messages, Locale locale, String p_fileName, String p_networkCode) {
        if (_log.isDebugEnabled()) {
            _log.debug("writeModifyExcel", " p_excelID: " + p_excelID + ", p_module:" + p_module + ", p_hashMap:" + p_hashMap + ", p_locale: " + locale + ", p_fileName: " + p_fileName);
        }
        final String METHOD_NAME = "writeModifyExcel";
        WritableWorkbook workbook = null;
        WritableSheet worksheet1 = null;
        WritableSheet worksheet2 = null;
        int col = 0;
        int row = 0;
        try {
            p_messages = messages;
            p_locale = locale;
            workbook = Workbook.createWorkbook(new File(p_fileName));
            worksheet2 = workbook.createSheet("Guideline Sheet", 1);
            if (p_module.equals(PretupsI.P2P_MODULE)) {
                this.writeCardGroupSetP2P(worksheet2, col, row, p_hashMap);
            } else {
                this.writeCardGroupSet(worksheet2, col, row, p_hashMap);
            }
            col = 0;
            row = 0;

            worksheet1 = workbook.createSheet("Data Sheet", 0);
            if (p_module.equals(PretupsI.P2P_MODULE)) {
                this.writeModifyInDataSheetP2P(worksheet1, col, row, p_hashMap, p_networkCode);
            } else {
                this.writeModifyInDataSheet(worksheet1, col, row, p_hashMap, p_networkCode);
            }
            workbook.write();
        }
        // end of try block
        catch (Exception e) {
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
            workbook = null;
            if (_log.isDebugEnabled()) {
                _log.debug("writeModifyExcel", " Exiting");
            }
        }
    }

    /**
     * To wrte the Data in a Sheet
     * 
     * @param worksheet1
     * @param col
     * @param row
     * @param p_hashMap
     * @throws Exception
     * @author sanjeew.kumar
     * @throws ParseException 
     * @throws BTSLBaseException 
     */
    private void writeModifyInDataSheet(WritableSheet worksheet1, int col, int row, HashMap p_hashMap, String p_networkCode) throws ParseException, BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("writeModifyInDataSheet", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeModifyInDataSheet";
        WritableCellFeatures cellFeatures = new WritableCellFeatures();
        WritableFont times16font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, true);
        WritableCellFormat times16format = new WritableCellFormat(times16font);
        CellView lock = new CellView();
        String data = null;
        // Bonus related fields.
        ArrayList bonusList = null;
        BonusAccountDetailsVO bonusAccountDetailsVO = null;
        BonusBundleDetailVO bonusBundleDetailVO = null;
        try {
            String keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.heading");
            String comment = null;
            Label label = new Label(col, row, keyName, times16format);
            worksheet1.mergeCells(col, row, col + 5, row);
            worksheet1.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.nwcode");
            label = new Label(col, row, keyName, times16format);
            worksheet1.mergeCells(col, row, col = col + 2, row);
            worksheet1.addCell(label);
            col++;
            label = new Label(col, row, p_networkCode);
            worksheet1.mergeCells(col, row, col + 3, row);
            worksheet1.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.label.servicetype");
            label = new Label(col, row, keyName, times16format);
            worksheet1.mergeCells(col, row, col = col + 2, row);
            worksheet1.addCell(label);
            col++;
            label = new Label(col, row, (String) p_hashMap.get(PretupsI.SERVICE_TYPE));
            worksheet1.mergeCells(col, row, col + 3, row);
            worksheet1.addCell(label);
            col = 0;

            // ServiceType
            row++;
            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.label.servicetype");
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // cardgroupsetid
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.ID");
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.cgID");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.cgID.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);
            // card group set name
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.cardname");
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // cardgroupcode
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.cgcode");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "restrictedsubs.rescheduletopupdetails.file.cgcode.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.cgname");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "restrictedsubs.rescheduletopupdetails.file.cgname.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);
            // Sub service type
            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.label.subservicetype");
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.label.subservice");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // start range
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.srtrange");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);
            // end range
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.endrange");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);
            // added by gaurav for COS
            // for COS required
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.COS_REQUIRED)).booleanValue()) {
                keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.cosrequired");
                label = new Label(col++, row, keyName, times16format);
                worksheet1.addCell(label);
            }
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.reversalpermitted");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IN_PROMO_REQUIRED)).booleanValue()) {
                keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.bonus.inpromo");
                label = new Label(col++, row, keyName, times16format);
                worksheet1.addCell(label);
            }
            // end here

            // validity Type Highest, Lowest and Commulative
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.validitytype");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.validitytype.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // validity period
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.valdays");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            // grace period
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.graceperiod");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            // multiple of
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.multof");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            // reciever_tax1_name
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recTax1.name");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.value");
            comment = comment + " " + p_messages.getMessage(p_locale, "cardgroup.cardgroupdetails.label.receivertax1namevalue");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // reciever_tax1_type
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recTax1.type");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.amtpct.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // reciever_tax1_rate
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recTax1.rate");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            // reciever_tax2_name
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recTax2.name");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.value");
            comment = comment + " " + p_messages.getMessage(p_locale, "cardgroup.cardgroupdetails.label.receivertax2namevalue");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // reciever_tax2_type
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recTax2.type");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.amtpct.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // reciever_tax2_rate
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recTax2.rate");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            // reciever access fee type
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recAccFee.type");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.amtpct.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // reciever access fee rate
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recAccFee.rate");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);
            // min receiver access fee
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recAccFee.min");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);
            // max access fee
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recAccFee.max");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            // online
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.online");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.online.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // both
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.both");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.both.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // Status
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.status.flag");
            label = new Label(col++, row, keyName, times16format);

            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.status.flag.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // applicable Date
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.applicabledate");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.applicabledate.comment");
            comment = comment + " (" + (Constants.getProperty("CARDGROUP_DATE_FORMAT")).split(" ")[0] + ")";
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // applicable time
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.applicabletime");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.applicabletime.comment");
            comment = comment + " (" + (Constants.getProperty("CARDGROUP_DATE_FORMAT")).split(" ")[1] + ")";
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // Validity Bonus
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.validity.bonus");
            label = new Label(col++, row, keyName, times16format);
            cellFeatures = new WritableCellFeatures();
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // Receiver conversion factor
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.receiver.convfac");
            label = new Label(col++, row, keyName, times16format);
            cellFeatures = new WritableCellFeatures();
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            ArrayList cardGroupDetailsVOList = (ArrayList) p_hashMap.get(PretupsI.BATCH_CARD_GROUP_EXCEL_DATA);
            CardGroupDetailsVO cardGroupDetailsVO = null;

            // Defined bonuses
            bonusList = (ArrayList) p_hashMap.get(PretupsI.BONUS_BUNDLE_LIST);
            if (bonusList != null && bonusList.size() > 0) {
                for (int p = 0, q = bonusList.size(); p < q; p++) {
                    bonusBundleDetailVO = (BonusBundleDetailVO) bonusList.get(p);
                    String str = null;
                    if ("Y".equals(bonusBundleDetailVO.getResINStatus())) {
                        keyName = bonusBundleDetailVO.getBundleCode();
                        // Bonus bundle type label
                        str = keyName + " " + p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.bonus.type");
                        label = new Label(col++, row, str, times16format);
                        cellFeatures = new WritableCellFeatures();
                        label.setCellFeatures(cellFeatures);
                        worksheet1.addCell(label);
                        // Bonus bundle value label
                        str = keyName + " " + p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.bonus.value");
                        label = new Label(col++, row, str, times16format);
                        cellFeatures = new WritableCellFeatures();
                        label.setCellFeatures(cellFeatures);
                        worksheet1.addCell(label);
                        // Bonus bundle validity label
                        str = keyName + " " + p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.bonus.validity");
                        label = new Label(col++, row, str, times16format);
                        cellFeatures = new WritableCellFeatures();
                        label.setCellFeatures(cellFeatures);
                        worksheet1.addCell(label);
                        // Bonus bundle conversion factor label
                        str = keyName + " " + p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.bonus.convfac");
                        label = new Label(col++, row, str, times16format);
                        cellFeatures = new WritableCellFeatures();
                        label.setCellFeatures(cellFeatures);
                        worksheet1.addCell(label);
                    }
                }
                bonusList = null;
            }

            // cellf=new CellFeatures();
            for (int i = 0, j = cardGroupDetailsVOList.size(); i < j; i++) {
                cardGroupDetailsVO = (CardGroupDetailsVO) cardGroupDetailsVOList.get(i);
                row++;
                col = 0;

                // Service Type
                label = new Label(col++, row, cardGroupDetailsVO.getServiceTypeId());
                lock.setHidden(true);
                worksheet1.addCell(label);

                // card GroupsetID
                label = new Label(col++, row, cardGroupDetailsVO.getCardGroupSetID());
                lock.setHidden(true);
                worksheet1.addCell(label);
                label = new Label(col++, row, cardGroupDetailsVO.getCardGroupID());
                worksheet1.addCell(label);
                // set name
                label = new Label(col++, row, cardGroupDetailsVO.getCardGroupSetName());
                worksheet1.addCell(label);

                // cardgroupcode
                label = new Label(col++, row, cardGroupDetailsVO.getCardGroupCode());
                worksheet1.addCell(label);

                label = new Label(col++, row, cardGroupDetailsVO.getCardName());
                worksheet1.addCell(label);
                // C2s card group sub service
                label = new Label(col++, row, cardGroupDetailsVO.getCardGroupSubServiceId());
                worksheet1.addCell(label);

                // start range
                if (cardGroupDetailsVO.getStartRange() != 0) {
                    try {
                        label = new Label(col++, row, PretupsBL.getDisplayAmount(cardGroupDetailsVO.getStartRange()));
                        worksheet1.addCell(label);
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        _log.error("writeModifyInDataSheet", " Exception e: " + e.getMessage());
                        throw new BTSLBaseException(e);
                    }
                } else {
                    label = new Label(col++, row, PretupsBL.getDisplayAmount(0));
                    worksheet1.addCell(label);
                }
                // end range
                if (cardGroupDetailsVO.getEndRange() != 0) {
                    try {
                        label = new Label(col++, row, PretupsBL.getDisplayAmount(cardGroupDetailsVO.getEndRange()));
                        worksheet1.addCell(label);
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        _log.error("writeModifyInDataSheet", " Exception e: " + e.getMessage());
                        throw new BTSLBaseException(e);
                    }
                } else {
                    label = new Label(col++, row, PretupsBL.getDisplayAmount(0));
                    worksheet1.addCell(label);
                }

                // added for COS
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.COS_REQUIRED)).booleanValue()) {
                    data = cardGroupDetailsVO.getCosRequired();
                    if (BTSLUtil.isNullString(data)) {
                        data = PretupsI.NO;
                    }
                    label = new Label(col++, row, data);
                    worksheet1.addCell(label);
                }
                data = cardGroupDetailsVO.getReversalPermitted();
                if (BTSLUtil.isNullString(data)) {
                    data = PretupsI.NO;
                }
                label = new Label(col++, row, data);
                worksheet1.addCell(label);
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IN_PROMO_REQUIRED)).booleanValue()) {
                    // updated by akanksha for Tigo GTCR
                    // label = new
                    // Label(col++,row,""+(cardGroupDetailsVO.getInPromo()));
                    label = new Label(col++, row, "" + (cardGroupDetailsVO.getInPromoAsString()));
                    worksheet1.addCell(label);
                }
                // validity Type
                label = new Label(col++, row, cardGroupDetailsVO.getValidityPeriodType());
                worksheet1.addCell(label);

                // validity period
                label = new Label(col++, row, new Integer(cardGroupDetailsVO.getValidityPeriod()).toString());
                worksheet1.addCell(label);

                // grace period
                label = new Label(col++, row, new Long(cardGroupDetailsVO.getGracePeriod()).toString());
                worksheet1.addCell(label);

                // multi of
                if (cardGroupDetailsVO.getMultipleOf() != 0) {
                    try {
                        label = new Label(col++, row, PretupsBL.getDisplayAmount(cardGroupDetailsVO.getMultipleOf()));
                        worksheet1.addCell(label);
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        _log.error("writeModifyInDataSheet", " Exception e: " + e.getMessage());
                        throw new BTSLBaseException(e);
                    }
                } else {
                    label = new Label(col++, row, PretupsBL.getDisplayAmount(0));
                    worksheet1.addCell(label);
                }
                // rec tax1 name
                label = new Label(col++, row, cardGroupDetailsVO.getReceiverTax1Name());
                worksheet1.addCell(label);

                // rectax1 type
                label = new Label(col++, row, cardGroupDetailsVO.getReceiverTax1Type());
                worksheet1.addCell(label);

                // rec tax1 rate
                if (cardGroupDetailsVO.getReceiverTax1Type().equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
                    // rec tax2 rate
                    label = new Label(col++, row, new Double(cardGroupDetailsVO.getReceiverTax1Rate()).toString());
                    worksheet1.addCell(label);
                } else {
                    //label = new Label(col++, row, PretupsBL.getDisplayAmount((long) cardGroupDetailsVO.getReceiverTax1Rate()));
                	label = new Label(col++, row, PretupsBL.getDisplayAmount(BTSLUtil.parseDoubleToLong(cardGroupDetailsVO.getReceiverTax1Rate())));
                    worksheet1.addCell(label);
                }

                // rec tax2 name
                label = new Label(col++, row, cardGroupDetailsVO.getReceiverTax2Name());
                worksheet1.addCell(label);

                // rec tax2 type
                label = new Label(col++, row, cardGroupDetailsVO.getReceiverTax2Type());
                worksheet1.addCell(label);
                if (cardGroupDetailsVO.getReceiverTax2Type().equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
                    // rec tax2 rate
                    label = new Label(col++, row, new Double(cardGroupDetailsVO.getReceiverTax2Rate()).toString());
                    worksheet1.addCell(label);
                } else {
                    label = new Label(col++, row, PretupsBL.getDisplayAmount(BTSLUtil.parseDoubleToLong(cardGroupDetailsVO.getReceiverTax2Rate()) ));
                    worksheet1.addCell(label);
                }
                // rec access fee type

                label = new Label(col++, row, cardGroupDetailsVO.getReceiverAccessFeeType());
                worksheet1.addCell(label);

                // rec access fee rate
                if (cardGroupDetailsVO.getReceiverAccessFeeType().equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
                    label = new Label(col++, row, new Double(cardGroupDetailsVO.getReceiverAccessFeeRate()).toString());
                    worksheet1.addCell(label);
                } else {
                    try {
                        label = new Label(col++, row, PretupsBL.getDisplayAmount( BTSLUtil.parseDoubleToLong(cardGroupDetailsVO.getReceiverAccessFeeRate()) ));
                        worksheet1.addCell(label);
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        _log.error("writeModifyInDataSheet", " Exception e: " + e.getMessage());
                        throw new BTSLBaseException(e);
                    }
                }
                // min acccess fee
                if (cardGroupDetailsVO.getMinReceiverAccessFee() != 0 || cardGroupDetailsVO.getMaxReceiverAccessFee() != 0) {
                    try {
                        label = new Label(col++, row, PretupsBL.getDisplayAmount(cardGroupDetailsVO.getMinReceiverAccessFee()));
                        worksheet1.addCell(label);
                        // max access fee
                        label = new Label(col++, row, PretupsBL.getDisplayAmount(cardGroupDetailsVO.getMaxReceiverAccessFee()));
                        worksheet1.addCell(label);
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        _log.error("writeModifyInDataSheet", " Exception e: " + e.getMessage());
                        throw new BTSLBaseException(e);
                    }
                } else {
                    label = new Label(col++, row, PretupsBL.getDisplayAmount(0));
                    worksheet1.addCell(label);
                    // max access fee
                    label = new Label(col++, row, PretupsBL.getDisplayAmount(0));
                    worksheet1.addCell(label);
                }
                // Online
                data = cardGroupDetailsVO.getOnline();
                if (BTSLUtil.isNullString(data)) {
                    data = PretupsI.NO;
                }
                label = new Label(col++, row, data);
                worksheet1.addCell(label);

                // Both
                data = cardGroupDetailsVO.getBoth();
                if (BTSLUtil.isNullString(data)) {
                    data = PretupsI.NO;
                }
                label = new Label(col++, row, data);
                worksheet1.addCell(label);

                // Status
                data = cardGroupDetailsVO.getStatus();
                label = new Label(col++, row, data);
                worksheet1.addCell(label);

                // Applicable from: date
                label = new Label(col++, row, BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(cardGroupDetailsVO.getApplicableFrom())));
                label.addCellFeatures();
                worksheet1.addCell(label);

                // Applicable from: time
                label = new Label(col++, row, BTSLUtil.getDateTimeStringFromDate(cardGroupDetailsVO.getApplicableFrom(), ((Constants.getProperty("CARDGROUP_DATE_FORMAT")).split(" ")[1])));
                label.addCellFeatures();
                worksheet1.addCell(label);

                // Validity bonus
                data = String.valueOf(cardGroupDetailsVO.getBonusValidityValue());
                label = new Label(col++, row, data);
                worksheet1.addCell(label);

                // Receiver conversion factor
                data = cardGroupDetailsVO.getReceiverConvFactor();
                label = new Label(col++, row, data);
                worksheet1.addCell(label);

                // Bonuses associated with the card group.
                bonusList = cardGroupDetailsVO.getBonusAccList();
                if (bonusList != null && bonusList.size() > 0) {
                    for (int m = 0, n = bonusList.size(); m < n; m++) {
                        bonusAccountDetailsVO = (BonusAccountDetailsVO) bonusList.get(m);
                        // Bonus code
                        if ("Y".equals(bonusAccountDetailsVO.getRestrictedOnIN())) {
                            // Type
                            data = bonusAccountDetailsVO.getType();
                            label = new Label(col++, row, data);
                            worksheet1.addCell(label);
                            // Bonus value
                            data = bonusAccountDetailsVO.getBonusValue();
                            label = new Label(col++, row, data);
                            worksheet1.addCell(label);
                            // Bonus validity
                            data = bonusAccountDetailsVO.getBonusValidity();
                            label = new Label(col++, row, data);
                            worksheet1.addCell(label);
                            // Bonus Conversion factor
                            data = bonusAccountDetailsVO.getMultFactor();
                            label = new Label(col++, row, data);
                            worksheet1.addCell(label);
                        }
                    }
                }
            }
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeModifyInDataSheet", " Exception e: " + e.getMessage());
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeModifyInDataSheet", " Exception e: " + e.getMessage());
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
     * @return String[][] strArr
     * @throws Exception
     */
    public String[][] readMultipleExcelSheet(String p_excelID, String p_fileName, boolean p_readLastSheet, int p_leftHeaderLinesForEachSheet, HashMap<String, String> map) {
        if (_log.isDebugEnabled()) {
            _log.debug("readMultipleExcelSheet", new StringBuilder().append(" p_excelID: ").append(p_excelID).append(" p_fileName: ").append(p_fileName).append(" p_readLastSheet=").append(p_readLastSheet), " p_leftHeaderLinesForEachSheet=" + p_leftHeaderLinesForEachSheet);
        }
        final String METHOD_NAME = "readMultipleExcelSheet";
        String strArr[][] = null;
        int arrRow = 0;
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
            strArr = new String[noOfRows][noOfcols];
            for (int i = 0; i < noOfSheet; i++) {
                excelsheet = workbook.getSheet(i);
                noOfRows = excelsheet.getRows();
                noOfcols = excelsheet.getColumns();

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
                    if (i == 0) {
                        strArr[i][indexMapArray[col]] = key;
                    }
                }
                for (int row = p_leftHeaderLinesForEachSheet; row < noOfRows; row++) {
                    map.put(Integer.toString(arrRow + p_leftHeaderLinesForEachSheet), excelsheet.getName() + PretupsI.ERROR_LINE + (row + 1));
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
     * writeCardGroupSet
     * This method will write the C2S card group Guideline Sheet for data
     * modification
     * 
     * @param int col
     * @param int row
     * @param HashMap
     *            p_hashMap
     * @param WritableSheet
     *            worksheet1
     * @throws Exception
     */
    private void writeCardGroupSet(WritableSheet worksheet1, int col, int row, HashMap p_hashMap) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeCardGroupSet", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeCardGroupSet";
        WritableFont times16font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, true);
        WritableCellFormat times16format = new WritableCellFormat(times16font);
        WritableFont times12font = new WritableFont(WritableFont.COURIER, 10, WritableFont.BOLD, true);
        WritableCellFormat times12format = new WritableCellFormat(times12font);

        WritableFont times11font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, true);
        WritableCellFormat times11format = new WritableCellFormat(times11font);
        times11format.setAlignment(Alignment.CENTRE);
        times11format.setVerticalAlignment(VerticalAlignment.CENTRE);

        WritableFont times10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, true);
        times10font.setColour(Colour.RED);
        times10font.setItalic(false);
        WritableCellFormat times10format = new WritableCellFormat(times10font);
        times10format.setAlignment(Alignment.LEFT);
        times10format.setVerticalAlignment(VerticalAlignment.TOP);
        times10format.setWrap(true);

        WritableFont times10Afont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, true);
        times10Afont.setColour(Colour.BLACK);
        times10Afont.setItalic(false);
        WritableCellFormat times10Aformat = new WritableCellFormat(times10Afont);
        times10Aformat.setAlignment(Alignment.LEFT);
        times10Aformat.setVerticalAlignment(VerticalAlignment.TOP);
        times10Aformat.setBorder(Border.ALL, BorderLineStyle.NONE);
        times10format.setWrap(true);

        WritableFont times10Bfont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, true);
        times10Bfont.setColour(Colour.BLACK);
        times10Bfont.setItalic(false);
        WritableCellFormat times10Bformat = new WritableCellFormat(times10Bfont);
        times10Bformat.setAlignment(Alignment.LEFT);
        times10Bformat.setVerticalAlignment(VerticalAlignment.TOP);
        times10Bformat.setBorder(Border.ALL, BorderLineStyle.NONE);
        times10Bformat.setWrap(true);

        try {
            String keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.heading");
            Label label = new Label(col, row, keyName, times16format);
            worksheet1.mergeCells(col, row, col + 16, row);
            worksheet1.addCell(label);
            row++;
            col = 0;

            // Guidelines
            row++;
            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.guidelines");
            label = new Label(col, ++row, keyName, times11format);
            worksheet1.mergeCells(col, row, col + 14, row);
            worksheet1.addCell(label);
            row++;
            col = 0;

            // sub head1
            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.guidelines.desc");
            label = new Label(col, ++row, keyName, times10format);
            worksheet1.mergeCells(col, row, col + 16, row = row + 8);
            worksheet1.addCell(label);
            row++;
            col = 0;
            // instruction for modifing card group
            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.guidelines.modify");
            label = new Label(col, ++row, keyName, times10Aformat);
            worksheet1.mergeCells(col, row, col + 10, row);
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.desc.modify");
            label = new Label(col, row, keyName, times10Bformat);
            worksheet1.mergeCells(col, row, col + 14, row = row + 20);
            worksheet1.addCell(label);
            row++;
            col = 0;

            // instruction to add new slab in a card group
            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.guidelines.add");
            label = new Label(col, ++row, keyName, times10Aformat);
            worksheet1.mergeCells(col, row, col + 10, row);
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.desc.add");
            label = new Label(col, row, keyName, times10Bformat);
            worksheet1.mergeCells(col, row, col + 14, row = row + 20);
            worksheet1.addCell(label);
            row++;
            col = 0;
            // C2S card group sub service
            row++;
            row++;
            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.label.subservicetype");
            label = new Label(col, ++row, keyName, times16format);
            worksheet1.mergeCells(col, row, col + 2, row);
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.selectorname");
            label = new Label(col++, row, keyName, times12format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.selectorcode");
            label = new Label(col++, row, keyName, times12format);
            worksheet1.addCell(label);
            row++;
            col = 0;
            // Commented by akanksha for tigo GTCR
            ArrayList ServiceSelectorMappingList = ServiceSelectorMappingCache.loadSelectorDropDownForCardGroup();
            Iterator itr = ServiceSelectorMappingList.iterator();
            ListValueVO ls = null;
            String selectorName = null;
            String value = null;
            String[] tempArr = new String[2];
            while (itr.hasNext()) {
                ls = (ListValueVO) itr.next();
                selectorName = ls.getLabel();
                value = ls.getValue();
                tempArr = value.split(":");
                if (tempArr.length == 2 && tempArr[0].equalsIgnoreCase((String) p_hashMap.get(PretupsI.SERVICE_TYPE)))
                // if(value.split(":")[0].equalsIgnoreCase((String)
                // p_hashMap.get(PretupsI.SERVICE_TYPE)))
                {
                    label = new Label(col++, row, selectorName);
                    worksheet1.addCell(label);

                    label = new Label(col++, row, tempArr[1]);
                    worksheet1.addCell(label);
                    row++;
                    col = 0;
                }
            }
            tempArr = null;
            // CVG
            /*
             * keyName = p_messages.getMessage(p_locale,
             * "cardgroup.cardgroupc2sdetails.modify.cvgname");
             * label = new Label(col++,row,keyName);
             * worksheet1.addCell(label);
             * 
             * keyName = p_messages.getMessage(p_locale,
             * "cardgroup.cardgroupc2sdetails.modify.cvgcode");
             * label = new Label(col++,row,keyName);
             * worksheet1.addCell(label);
             * row++;
             * col=0;
             * //C
             * keyName = p_messages.getMessage(p_locale,
             * "cardgroup.cardgroupc2sdetails.modify.cname");
             * label = new Label(col++,row,keyName);
             * worksheet1.addCell(label);
             * 
             * keyName = p_messages.getMessage(p_locale,
             * "cardgroup.cardgroupc2sdetails.modify.ccode");
             * label = new Label(col++,row,keyName);
             * worksheet1.addCell(label);
             * row++;
             * col=0;
             * //VG
             * keyName = p_messages.getMessage(p_locale,
             * "cardgroup.cardgroupc2sdetails.modify.vgname");
             * label = new Label(col++,row,keyName);
             * worksheet1.addCell(label);
             * 
             * keyName = p_messages.getMessage(p_locale,
             * "cardgroup.cardgroupc2sdetails.modify.vgcode");
             * label = new Label(col++,row,keyName);
             * worksheet1.addCell(label);
             * row++;
             * col=0;
             */
            // Validity Type
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.validitytype");
            label = new Label(col, ++row, keyName, times16format);
            worksheet1.mergeCells(col, row, col + 2, row);
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.validitytype");
            keyName = keyName + " " + p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.name");
            label = new Label(col++, row, keyName, times12format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.validitytype");
            keyName = keyName + " " + p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.code");
            label = new Label(col++, row, keyName, times12format);
            worksheet1.addCell(label);
            row++;
            col = 0;
            // Highest
            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.highest");
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);

            keyName = PretupsI.VALPERIOD_HIGHEST_TYPE;
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);
            row++;
            col = 0;
            // Lowest
            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.lowest");
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);

            keyName = PretupsI.VALPERIOD_LOWEST_TYPE;
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);
            row++;
            col = 0;
            // Cumulative
            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.cummulative");
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);

            keyName = PretupsI.VALPERIOD_CUMMULATIVE_TYPE;
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);
            row++;
            col = 0;

            // Tax1 Type
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recTax1.type");
            label = new Label(col, ++row, keyName, times16format);
            worksheet1.mergeCells(col, row, col + 2, row);
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.name");
            label = new Label(col++, row, keyName, times12format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.code");
            label = new Label(col++, row, keyName, times12format);
            worksheet1.addCell(label);
            row++;
            col = 0;
            // Percentage
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.percentage");
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);

            keyName = PretupsI.AMOUNT_TYPE_PERCENTAGE;
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);
            row++;
            col = 0;
            // Amount
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.amount");
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);

            keyName = PretupsI.AMOUNT_TYPE_AMOUNT;
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);
            row++;
            col = 0;

            // Tax2 Type
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recTax2.type");
            label = new Label(col, ++row, keyName, times16format);
            worksheet1.mergeCells(col, row, col + 2, row);
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.name");
            label = new Label(col++, row, keyName, times12format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.code");
            label = new Label(col++, row, keyName, times12format);
            worksheet1.addCell(label);
            row++;
            col = 0;
            // Percentage
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.percentage");
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);

            keyName = PretupsI.AMOUNT_TYPE_PERCENTAGE;
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);
            row++;
            col = 0;
            // Amount
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.amount");
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);

            keyName = PretupsI.AMOUNT_TYPE_AMOUNT;
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);
            row++;
            col = 0;

            // Processing fee type
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recAccFee.type");
            label = new Label(col, ++row, keyName, times16format);
            worksheet1.mergeCells(col, row, col + 2, row);
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.name");
            label = new Label(col++, row, keyName, times12format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.code");
            label = new Label(col++, row, keyName, times12format);
            worksheet1.addCell(label);
            row++;
            col = 0;
            // Percentage
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.percentage");
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);

            keyName = PretupsI.AMOUNT_TYPE_PERCENTAGE;
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);
            row++;
            col = 0;
            // Amount
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.amount");
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);

            keyName = PretupsI.AMOUNT_TYPE_AMOUNT;
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);
            row++;
            col = 0;

            // Bonus Talk value type
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.bonus.tlkvalType");
            label = new Label(col, ++row, keyName, times16format);
            worksheet1.mergeCells(col, row, col + 2, row);
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.name");
            label = new Label(col++, row, keyName, times12format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.code");
            label = new Label(col++, row, keyName, times12format);
            worksheet1.addCell(label);
            row++;
            col = 0;
            // Percentage
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.percentage");
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);

            keyName = PretupsI.AMOUNT_TYPE_PERCENTAGE;
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);
            row++;
            col = 0;
            // Amount
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.amount");
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);

            keyName = PretupsI.AMOUNT_TYPE_AMOUNT;
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);
            row++;
            col = 0;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeCardGroupSet", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeCardGroupSet", " Exception e: " + e.getMessage());
            throw e;
        }
    }

    /**
     * writeCardGroupSetP2P
     * This method will write the P2P card group Guideline Sheet for data
     * modification
     * 
     * @param int col
     * @param int row
     * @param HashMap
     *            p_hashMap
     * @param WritableSheet
     *            worksheet1
     * @throws Exception
     */
    private void writeCardGroupSetP2P(WritableSheet worksheet1, int col, int row, HashMap p_hashMap) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("writeCardGroupSetP2P", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeCardGroupSetP2P";
        WritableFont times16font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, true);
        WritableCellFormat times16format = new WritableCellFormat(times16font);
        WritableFont times12font = new WritableFont(WritableFont.COURIER, 10, WritableFont.BOLD, true);
        WritableCellFormat times12format = new WritableCellFormat(times12font);

        WritableFont times11font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, true);
        WritableCellFormat times11format = new WritableCellFormat(times11font);
        times11format.setAlignment(Alignment.CENTRE);
        times11format.setVerticalAlignment(VerticalAlignment.CENTRE);

        WritableFont times10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, true);
        times10font.setColour(Colour.RED);
        times10font.setItalic(false);
        WritableCellFormat times10format = new WritableCellFormat(times10font);
        times10format.setAlignment(Alignment.LEFT);
        times10format.setVerticalAlignment(VerticalAlignment.TOP);
        times10format.setWrap(true);

        WritableFont times10Afont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, true);
        times10Afont.setColour(Colour.BLACK);
        times10Afont.setItalic(false);
        WritableCellFormat times10Aformat = new WritableCellFormat(times10Afont);
        times10Aformat.setAlignment(Alignment.LEFT);
        times10Aformat.setVerticalAlignment(VerticalAlignment.TOP);
        times10Aformat.setBorder(Border.ALL, BorderLineStyle.NONE);
        times10format.setWrap(true);

        WritableFont times10Bfont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, true);
        times10Bfont.setColour(Colour.BLACK);
        times10Bfont.setItalic(false);
        WritableCellFormat times10Bformat = new WritableCellFormat(times10Bfont);
        times10Bformat.setAlignment(Alignment.LEFT);
        times10Bformat.setVerticalAlignment(VerticalAlignment.TOP);
        times10Bformat.setBorder(Border.ALL, BorderLineStyle.NONE);
        times10Bformat.setWrap(true);
        try {
        	String keyName = "";
        	if((PretupsI.VOUCHER_CONS_SERVICE).equals(p_hashMap.get(PretupsI.SERVICE_TYPE)))
        		 keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.voucher.heading");
        	else
        		keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.p2p.heading");
            Label label = new Label(col, row, keyName, times16format);
            worksheet1.mergeCells(col, row, col + 16, row);
            worksheet1.addCell(label);
            row++;
            col = 0;

            // Guidelines
            row++;
            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.guidelines");
            label = new Label(col, ++row, keyName, times11format);
            worksheet1.mergeCells(col, row, col + 14, row);
            worksheet1.addCell(label);
            row++;
            col = 0;

            // sub head1
            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.guidelines.desc");
            label = new Label(col, ++row, keyName, times10format);
            worksheet1.mergeCells(col, row, col + 16, row = row + 8);
            worksheet1.addCell(label);
            row++;
            col = 0;
            // instruction for modifing card group
            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.guidelines.modify");
            label = new Label(col, ++row, keyName, times10Aformat);
            worksheet1.mergeCells(col, row, col + 10, row);
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.p2p.desc.modify");
            keyName = keyName + p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.p2p.desc.modify1");
            keyName = keyName + p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.p2p.desc.modify2");
            if((PretupsI.VOUCHER_CONS_SERVICE).equals(p_hashMap.get(PretupsI.SERVICE_TYPE)))
                keyName = keyName + p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.p2p.desc.modify10");
            else{
            	keyName = keyName + p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.p2p.desc.modify3");
            	keyName = keyName + p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.p2p.desc.modify4");
            }
            keyName = keyName + p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.p2p.desc.modify5");
            keyName = keyName + p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.p2p.desc.modify6");
            if((PretupsI.VOUCHER_CONS_SERVICE).equals(p_hashMap.get(PretupsI.SERVICE_TYPE)))
            	keyName = keyName + p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.p2p.desc.modify11");
            else
            	keyName = keyName + p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.p2p.desc.modify7");
            keyName = keyName + p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.p2p.desc.modify8");
            keyName = keyName + p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.p2p.desc.modify9");
            label = new Label(col, row, keyName, times10Bformat);
            worksheet1.mergeCells(col, row, col + 14, row = row + 20);
            worksheet1.addCell(label);
            row++;
            col = 0;

            // instruction to add new slab in a card group
            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.guidelines.add");
            label = new Label(col, ++row, keyName, times10Aformat);
            worksheet1.mergeCells(col, row, col + 10, row);
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.p2p.desc.add");
            keyName = keyName + p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.p2p.desc.add1");
            keyName = keyName + p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.p2p.desc.add2");
            keyName = keyName + p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.p2p.desc.add3");
            keyName = keyName + p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.p2p.desc.add4");
            if((PretupsI.VOUCHER_CONS_SERVICE).equals(p_hashMap.get(PretupsI.SERVICE_TYPE)))
            	keyName = keyName + p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.p2p.desc.add11");
            else{
            	keyName = keyName + p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.p2p.desc.add5");
            	keyName = keyName + p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.p2p.desc.add6");
            }
            keyName = keyName + p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.p2p.desc.add7");
            if((PretupsI.VOUCHER_CONS_SERVICE).equals(p_hashMap.get(PretupsI.SERVICE_TYPE)))
            	keyName = keyName + p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.p2p.desc.add12");
            else
            	keyName = keyName + p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.p2p.desc.add8");
            keyName = keyName + p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.p2p.desc.add9");
            keyName = keyName + p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.p2p.desc.add10");
            label = new Label(col, row, keyName, times10Bformat);
            worksheet1.mergeCells(col, row, col + 14, row = row + 20);
            worksheet1.addCell(label);
            row++;
            col = 0;

            // C2S card group sub service
            row++;
            row++;
            if((PretupsI.VOUCHER_CONS_SERVICE).equals(p_hashMap.get(PretupsI.SERVICE_TYPE)))
            	keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.label.voucher.subservicetype");
            else
                keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.label.p2p.subservicetype");
            label = new Label(col, ++row, keyName, times16format);
            worksheet1.mergeCells(col, row, col + 2, row);
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.selectorname");
            label = new Label(col++, row, keyName, times12format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.selectorcode");
            label = new Label(col++, row, keyName, times12format);
            worksheet1.addCell(label);
            row++;
            col = 0;
            // CVG

            // Commented by akanksha for tigo GTCR
            ArrayList ServiceSelectorMappingList = ServiceSelectorMappingCache.loadSelectorDropDownForCardGroup();
            Iterator itr = ServiceSelectorMappingList.iterator();
            ListValueVO ls = null;
            String selectorName = null;
            String value = null;
            String[] tempArr = new String[2];
            while (itr.hasNext()) {
                ls = (ListValueVO) itr.next();
                selectorName = ls.getLabel();
                value = ls.getValue();
                tempArr = value.split(":");
                if (tempArr.length == 2 && tempArr[0].equalsIgnoreCase((String) p_hashMap.get(PretupsI.SERVICE_TYPE)))
                // if(value.split(":")[0].equalsIgnoreCase((String)
                // p_hashMap.get(PretupsI.SERVICE_TYPE)))
                {
                    label = new Label(col++, row, selectorName);
                    worksheet1.addCell(label);

                    label = new Label(col++, row, tempArr[1]);
                    worksheet1.addCell(label);
                    row++;
                    col = 0;
                }
            }
            tempArr = null;

        //set for voucher data
         if((PretupsI.VOUCHER_CONS_SERVICE).equals(p_hashMap.get(PretupsI.SERVICE_TYPE))){
             keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.label.voucher.details");
             label = new Label(col, ++row, keyName, times16format);
             worksheet1.mergeCells(col, row, col + 2, row);
             worksheet1.addCell(label);
             row++;
             col = 0;           
             keyName = p_messages.getMessage(p_locale,"cardgroup.set.batch.modify.xlsfile.header.voucherType");
             label = new Label(col++,row,keyName,times12format);
             worksheet1.addCell(label);
             keyName = p_messages.getMessage(p_locale,"cardgroup.set.batch.modify.xlsfile.header.voucherTypeCode");
             label = new Label(col++,row,keyName,times12format);
             worksheet1.addCell(label);
             keyName = p_messages.getMessage(p_locale,"voucher.generation.email.notification.voucherSegment");
             label = new Label(col++,row,keyName,times12format);
             worksheet1.addCell(label);
             keyName = p_messages.getMessage(p_locale,"voucher.generation.email.notification.voucherSegmentcode");
             label = new Label(col++,row,keyName,times12format);
             worksheet1.addCell(label);
             keyName = p_messages.getMessage(p_locale,"cardgroup.set.batch.modify.xlsfile.header.voucherDenomination");
             label = new Label(col++,row,keyName,times12format);
             worksheet1.addCell(label);
             keyName = p_messages.getMessage(p_locale,"voucher.generation.email.notification.voucherProfile");
             label = new Label(col++,row,keyName,times12format);
             worksheet1.addCell(label);
             keyName = p_messages.getMessage(p_locale,"voucher.generation.email.notification.voucherProfileId");
             label = new Label(col++,row,keyName,times12format);
             worksheet1.addCell(label);
             row++;
             col=0;
 			
			 ArrayList <CardGroupDetailsVO> cardGroupList =(ArrayList<CardGroupDetailsVO>) p_hashMap.get(PretupsI.VOUCHER_LIST);
		     if(cardGroupList != null && !cardGroupList.isEmpty()){
		    	 for(CardGroupDetailsVO CardGroupDetailsVO: cardGroupList){
		    		 
		    		 label = new Label(col++, row, CardGroupDetailsVO.getVoucherTypeDesc());
		    		 worksheet1.addCell(label);
		    		 label = new Label(col++, row, CardGroupDetailsVO.getVoucherType());
		    		 worksheet1.addCell(label);
		    		 label = new Label(col++, row, CardGroupDetailsVO.getVoucherSegmentDesc());
		             worksheet1.addCell(label);
		             label = new Label(col++, row, CardGroupDetailsVO.getVoucherSegment());
		             worksheet1.addCell(label);
		             label = new Label(col++, row, CardGroupDetailsVO.getVoucherDenomination());
		    		 worksheet1.addCell(label);
		             label = new Label(col++, row, CardGroupDetailsVO.getProductName());
		             worksheet1.addCell(label);
		             label = new Label(col++, row, CardGroupDetailsVO.getVoucherProductId());
		             worksheet1.addCell(label);
		             row++;
		             col = 0;
		    	 }
		     }
            }
            // Validity Type
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.validitytype");
            label = new Label(col, ++row, keyName, times16format);
            worksheet1.mergeCells(col, row, col + 2, row);
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.validitytype");
            keyName = keyName + " " + p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.name");
            label = new Label(col++, row, keyName, times12format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.validitytype");
            keyName = keyName + " " + p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.code");
            label = new Label(col++, row, keyName, times12format);
            worksheet1.addCell(label);
            row++;
            col = 0;
            // Highest
            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.highest");
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);

            keyName = PretupsI.VALPERIOD_HIGHEST_TYPE;
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);
            row++;
            col = 0;
            // Lowest
            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.lowest");
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);

            keyName = PretupsI.VALPERIOD_LOWEST_TYPE;
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);
            row++;
            col = 0;
            // Cumulative
            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.cummulative");
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);

            keyName = PretupsI.VALPERIOD_CUMMULATIVE_TYPE;
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);
            row++;
            col = 0;

            // Tax1 Type
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recTax1.type");
            label = new Label(col, ++row, keyName, times16format);
            worksheet1.mergeCells(col, row, col + 2, row);
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.name");
            label = new Label(col++, row, keyName, times12format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.code");
            label = new Label(col++, row, keyName, times12format);
            worksheet1.addCell(label);
            row++;
            col = 0;
            // Percentage
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.percentage");
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);

            keyName = PretupsI.AMOUNT_TYPE_PERCENTAGE;
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);
            row++;
            col = 0;
            // Amount
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.amount");
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);

            keyName = PretupsI.AMOUNT_TYPE_AMOUNT;
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);
            row++;
            col = 0;

            // Tax2 Type
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recTax2.type");
            label = new Label(col, ++row, keyName, times16format);
            worksheet1.mergeCells(col, row, col + 2, row);
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.name");
            label = new Label(col++, row, keyName, times12format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.code");
            label = new Label(col++, row, keyName, times12format);
            worksheet1.addCell(label);
            row++;
            col = 0;
            // Percentage
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.percentage");
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);

            keyName = PretupsI.AMOUNT_TYPE_PERCENTAGE;
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);
            row++;
            col = 0;
            // Amount
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.amount");
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);

            keyName = PretupsI.AMOUNT_TYPE_AMOUNT;
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);
            row++;
            col = 0;

            // Processing fee type
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recAccFee.type");
            label = new Label(col, ++row, keyName, times16format);
            worksheet1.mergeCells(col, row, col + 2, row);
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.name");
            label = new Label(col++, row, keyName, times12format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.code");
            label = new Label(col++, row, keyName, times12format);
            worksheet1.addCell(label);
            row++;
            col = 0;
            // Percentage
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.percentage");
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);

            keyName = PretupsI.AMOUNT_TYPE_PERCENTAGE;
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);
            row++;
            col = 0;
            // Amount
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.amount");
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);

            keyName = PretupsI.AMOUNT_TYPE_AMOUNT;
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);
            row++;
            col = 0;

            // Bonus Talk value type
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.bonus.tlkvalType");
            label = new Label(col, ++row, keyName, times16format);
            worksheet1.mergeCells(col, row, col + 2, row);
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.name");
            label = new Label(col++, row, keyName, times12format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.code");
            label = new Label(col++, row, keyName, times12format);
            worksheet1.addCell(label);
            row++;
            col = 0;
            // Percentage
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.percentage");
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);

            keyName = PretupsI.AMOUNT_TYPE_PERCENTAGE;
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);
            row++;
            col = 0;
            // Amount
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.amount");
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);

            keyName = PretupsI.AMOUNT_TYPE_AMOUNT;
            label = new Label(col++, row, keyName);
            worksheet1.addCell(label);
            row++;
            col = 0;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeCardGroupSetP2P", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeCardGroupSetP2P", " Exception e: " + e.getMessage());
            throw e;
        }
    }

    /**
     * To wrte the Data in a Sheet
     * 
     * @param worksheet1
     * @param col
     * @param row
     * @param p_hashMap
     * @throws Exception
     * @author sanjeew.kumar
     * @throws ParseException 
     * @throws BTSLBaseException 
     */
    private void writeModifyInDataSheetP2P(WritableSheet worksheet1, int col, int row, HashMap p_hashMap, String p_networkCode) throws ParseException, BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("writeModifyInDataSheetP2P", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        final String METHOD_NAME = "writeModifyInDataSheetP2P";
        WritableCellFeatures cellFeatures = new WritableCellFeatures();
        WritableFont times16font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, true);
        WritableCellFormat times16format = new WritableCellFormat(times16font);
        CellView lock = new CellView();
        String data = null;
        // Bonus related fields.
        ArrayList bonusList = null;
        BonusAccountDetailsVO bonusAccountDetailsVO = null;
        BonusBundleDetailVO bonusBundleDetailVO = null;
        try {
        	String keyName = "";
            if((PretupsI.VOUCHER_CONS_SERVICE).equals(p_hashMap.get(PretupsI.SERVICE_TYPE)))
            	 keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.voucher.heading");
            else
            	 keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.p2p.heading");
            String comment = null;
            Label label = new Label(col, row, keyName, times16format);
            worksheet1.mergeCells(col, row, col + 5, row);
            worksheet1.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.nwcode");
            label = new Label(col, row, keyName, times16format);
            worksheet1.mergeCells(col, row, col = col + 2, row);
            worksheet1.addCell(label);
            col++;
            label = new Label(col, row, p_networkCode);
            worksheet1.mergeCells(col, row, col + 3, row);
            worksheet1.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.label.servicetype");
            label = new Label(col, row, keyName, times16format);
            worksheet1.mergeCells(col, row, col = col + 2, row);
            worksheet1.addCell(label);
            col++;
            label = new Label(col, row, (String) p_hashMap.get(PretupsI.SERVICE_TYPE));
            worksheet1.mergeCells(col, row, col + 3, row);
            worksheet1.addCell(label);
            col = 0;

            // ServiceType
            row++;
            keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.label.servicetype");
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);


            // Sub service type
            if((PretupsI.VOUCHER_CONS_SERVICE).equals(p_hashMap.get(PretupsI.SERVICE_TYPE)))
            	keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.label.voucher.subservicetype");
            else
            	keyName = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.label.p2p.subservicetype");
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = p_messages.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.label.subservice");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);
            
            // cardgroupsetid
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.ID");
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // card group set name
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.cardname");
            label = new Label(col++, row, keyName, times16format);
            // add comment
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // cardgroupcode
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.cgcode");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "restrictedsubs.rescheduletopupdetails.file.cgcode.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);
			/*// cardName
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.cgname");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "restrictedsubs.rescheduletopupdetails.file.cgcode.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);*/

	        if((PretupsI.VOUCHER_CONS_SERVICE).equals(p_hashMap.get(PretupsI.SERVICE_TYPE))){
	            // voucher Type
	             keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.voucherTypeCode");
	             label = new Label(col++, row, keyName, times16format);
	             // add comment
	             comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.cardgroup.voucherType.comment");
	             cellFeatures = new WritableCellFeatures();
	             cellFeatures.setComment(comment);
	             label.setCellFeatures(cellFeatures);
	             worksheet1.addCell(label);
	             // voucher Segment
	             keyName = p_messages.getMessage(p_locale, "voucher.generation.email.notification.voucherSegmentcode");
	             label = new Label(col++, row, keyName, times16format);
	             // add comment
	             comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.cardgroup.voucherSegment.comment");
	             cellFeatures = new WritableCellFeatures();
	             cellFeatures.setComment(comment);
	             label.setCellFeatures(cellFeatures);
	             worksheet1.addCell(label);
	             // voucher Denomination
	             keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.voucherDenomination");
	             label = new Label(col++, row, keyName, times16format);
	             // add comment
	             comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.cardgroup.voucherDenominations.comment");
	             cellFeatures = new WritableCellFeatures();
	             cellFeatures.setComment(comment);
	             label.setCellFeatures(cellFeatures);
	             worksheet1.addCell(label);
	             // voucher Profile
	             keyName = p_messages.getMessage(p_locale, "voucher.generation.email.notification.voucherProfileId");
	             label = new Label(col++, row, keyName, times16format);
	             // add comment
	             comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.cardgroup.voucherProfile.comment");
	             cellFeatures = new WritableCellFeatures();
	             cellFeatures.setComment(comment);
	             label.setCellFeatures(cellFeatures);
	             worksheet1.addCell(label);
	            }
            else{
	            // start range
	            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.srtrange");
	            label = new Label(col++, row, keyName, times16format);
	            worksheet1.addCell(label);
	            // end range
	            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.endrange");
	            label = new Label(col++, row, keyName, times16format);
	            worksheet1.addCell(label);
            }
            
            // added for cos
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.COS_REQUIRED)).booleanValue()) {
                keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.cosrequired");
                label = new Label(col++, row, keyName, times16format);
                worksheet1.addCell(label);
            }
            /*
             * if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IN_PROMO_REQUIRED)).booleanValue())
             * {
             * keyName = p_messages.getMessage(p_locale,
             * "cardgroup.set.batch.modify.xlsfile.header.bonus.inpromo");
             * label = new Label(col++,row,keyName, times16format);
             * worksheet1.addCell(label);
             * }
             */
            // validity Type Highest, Lowest and Commulative
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.validitytype");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.validitytype.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // validity period
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.valdays");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            // grace period
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.graceperiod");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);
            if(!(PretupsI.VOUCHER_CONS_SERVICE).equals(p_hashMap.get(PretupsI.SERVICE_TYPE))){
            // multiple of
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.multof");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            // Sender_tax1_name
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.p2psender.header.tax1.name");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.value");
            comment = comment + " " + p_messages.getMessage(p_locale, "cardgroup.cardgroupdetails.label.receivertax1namevalue");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // Sender_tax1_type
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.p2psender.header.tax1.type");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.amtpct.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // Sender_tax1_rate
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.p2psender.header.tax1.rate");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            // Sender_tax2_name
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.p2psender.header.tax2.name");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.value");
            comment = comment + " " + p_messages.getMessage(p_locale, "cardgroup.cardgroupdetails.label.receivertax2namevalue");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // Sender_tax2_type
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.p2psender.header.tax2.type");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.amtpct.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // Sender_tax2_rate
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.p2psender.header.tax2.rate");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            // Sender access fee type
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.p2psender.header.accfee.type");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.amtpct.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // Sender access fee rate
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.p2psender.header.accfee.rate");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);
            // Sender min receiver access fee
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.p2psender.header.accfee.min");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);
            // Sender max access fee
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.p2psender.header.accfee.max");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);
            }
            // Reciever_tax1_name
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.p2preceiver.header.tax1.name");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.value");
            comment = comment + " " + p_messages.getMessage(p_locale, "cardgroup.cardgroupdetails.label.receivertax1namevalue");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // Reciever_tax1_type
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.p2preceiver.header.tax1.type");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.amtpct.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // Reciever_tax1_rate
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.p2preceiver.header.tax1.rate");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            // Reciever_tax2_name
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.p2preceiver.header.tax2.name");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.value");
            comment = comment + " " + p_messages.getMessage(p_locale, "cardgroup.cardgroupdetails.label.receivertax2namevalue");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // Reciever_tax2_type
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.p2preceiver.header.tax2.type");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.amtpct.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // Reciever_tax2_rate
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.p2preceiver.header.tax2.rate");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            // Reciever access fee type
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.p2preceiver.header.accfee.type");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.amtpct.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // Reciever access fee rate
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.p2preceiver.header.accfee.rate");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);
            // min receiver access fee
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.p2preceiver.header.accfee.min");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);
            // Reciever max access fee
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.p2preceiver.header.accfee.max");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            // online
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.online");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.online.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // both
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.both");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.both.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // Status
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.status.flag");
            label = new Label(col++, row, keyName, times16format);

            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.status.flag.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // applicable Date
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.applicabledate");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.applicabledate.comment");
            comment = comment + " (" + (Constants.getProperty("CARDGROUP_DATE_FORMAT")).split(" ")[0] + ")";
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            // applicable time
            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.applicabletime");
            label = new Label(col++, row, keyName, times16format);
            comment = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.applicabletime.comment");
            comment = comment + " (" + (Constants.getProperty("CARDGROUP_DATE_FORMAT")).split(" ")[1] + ")";
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);
            
            if(!(PretupsI.VOUCHER_CONS_SERVICE).equals(p_hashMap.get(PretupsI.SERVICE_TYPE)))
            {// Validity bonus
	            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.validity.bonus");
	            label = new Label(col++, row, keyName, times16format);
	            cellFeatures = new WritableCellFeatures();
	            label.setCellFeatures(cellFeatures);
	            worksheet1.addCell(label);
	            // Sender conversion factor
	            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.sender.convfac");
	            label = new Label(col++, row, keyName, times16format);
	            cellFeatures = new WritableCellFeatures();
	            label.setCellFeatures(cellFeatures);
	            worksheet1.addCell(label);
	            // Receiver conversion factor
	            keyName = p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.receiver.convfac");
	            label = new Label(col++, row, keyName, times16format);
	            cellFeatures = new WritableCellFeatures();
	            label.setCellFeatures(cellFeatures);
	            worksheet1.addCell(label);
            }
            // Defined bonuses
            bonusList = (ArrayList) p_hashMap.get(PretupsI.BONUS_BUNDLE_LIST);
            if (bonusList != null && bonusList.size() > 0) {
                for (int p = 0, q = bonusList.size(); p < q; p++) {
                    bonusBundleDetailVO = (BonusBundleDetailVO) bonusList.get(p);
                    String str = null;
                    if ("Y".equals(bonusBundleDetailVO.getResINStatus())) {
                        keyName = bonusBundleDetailVO.getBundleCode();

                        // Bonus type AMT or PCT
                        str = keyName + " " + p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.bonus.type");
                        label = new Label(col++, row, str, times16format);
                        cellFeatures = new WritableCellFeatures();
                        label.setCellFeatures(cellFeatures);
                        worksheet1.addCell(label);

                        // Bonus value
                        str = keyName + " " + p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.bonus.value");
                        label = new Label(col++, row, str, times16format);
                        cellFeatures = new WritableCellFeatures();
                        label.setCellFeatures(cellFeatures);
                        worksheet1.addCell(label);

                        // Bonus validity
                        str = keyName + " " + p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.bonus.validity");
                        label = new Label(col++, row, str, times16format);
                        cellFeatures = new WritableCellFeatures();
                        label.setCellFeatures(cellFeatures);
                        worksheet1.addCell(label);

                        // Bonus conversion factor
                        str = keyName + " " + p_messages.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.bonus.convfac");
                        label = new Label(col++, row, str, times16format);
                        cellFeatures = new WritableCellFeatures();
                        label.setCellFeatures(cellFeatures);
                        worksheet1.addCell(label);
                    }
                }
                bonusList = null;
            }

            ArrayList cardGroupDetailsVOList = (ArrayList) p_hashMap.get(PretupsI.BATCH_CARD_GROUP_EXCEL_DATA);
            CardGroupDetailsVO cardGroupDetailsVO = null;

            // cellf=new CellFeatures();
            for (int i = 0, j = cardGroupDetailsVOList.size(); i < j; i++) {
                cardGroupDetailsVO = (CardGroupDetailsVO) cardGroupDetailsVOList.get(i);
                row++;
                col = 0;

                // Service Type
                label = new Label(col++, row, cardGroupDetailsVO.getServiceTypeId());
                lock.setHidden(true);
                worksheet1.addCell(label);
                // card group sub service
                label = new Label(col++, row, cardGroupDetailsVO.getCardGroupSubServiceId());
                worksheet1.addCell(label);
                // card GroupsetID
                label = new Label(col++, row, cardGroupDetailsVO.getCardGroupSetID());
                lock.setHidden(true);
                worksheet1.addCell(label);
                // set name
                label = new Label(col++, row, cardGroupDetailsVO.getCardGroupSetName());
                worksheet1.addCell(label);
                // cardgroupcode
                label = new Label(col++, row, cardGroupDetailsVO.getCardGroupCode());
                worksheet1.addCell(label);
				 /*// cardname
                label = new Label(col++, row, cardGroupDetailsVO.getCardName());
                worksheet1.addCell(label);*/
				
                if((PretupsI.VOUCHER_CONS_SERVICE).equals(p_hashMap.get(PretupsI.SERVICE_TYPE))){
                    //voucher Type
                	label = new Label(col++, row, cardGroupDetailsVO.getVoucherType());
                    worksheet1.addCell(label);
                    //voucher segment
                    label = new Label(col++, row, cardGroupDetailsVO.getVoucherSegment());
                    worksheet1.addCell(label);
                    //voucher denomination
                    label = new Label(col++, row, cardGroupDetailsVO.getVoucherDenomination());
                    worksheet1.addCell(label);
                  //voucher profile
                    label = new Label(col++, row, cardGroupDetailsVO.getVoucherProductId());
                    worksheet1.addCell(label);
                }else{
                // start range
                if (cardGroupDetailsVO.getStartRange() != 0) {
                    try {
                        label = new Label(col++, row, PretupsBL.getDisplayAmount(cardGroupDetailsVO.getStartRange()));
                        worksheet1.addCell(label);
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        _log.error("writeModifyInDataSheetP2P", " Exception e: " + e.getMessage());
                        throw new BTSLBaseException(e);
                    }
                } else {
                    label = new Label(col++, row, PretupsBL.getDisplayAmount(0));
                    worksheet1.addCell(label);
                }
                // end range
                if (cardGroupDetailsVO.getEndRange() != 0) {
                    try {
                        label = new Label(col++, row, PretupsBL.getDisplayAmount(cardGroupDetailsVO.getEndRange()));
                        worksheet1.addCell(label);
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        _log.error("writeModifyInDataSheetP2P", " Exception e: " + e.getMessage());
                        throw new BTSLBaseException(e);
                    }
                } else {
                    label = new Label(col++, row, PretupsBL.getDisplayAmount(0));
                    worksheet1.addCell(label);
                }
                }
                // added for cos
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.COS_REQUIRED)).booleanValue()) {
                    data = cardGroupDetailsVO.getCosRequired();
                    if (BTSLUtil.isNullString(data)) {
                        data = PretupsI.NO;
                    }
                    label = new Label(col++, row, data);
                    worksheet1.addCell(label);
                }
                /*
                 * if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IN_PROMO_REQUIRED)).booleanValue())
                 * {
                 * label = new
                 * Label(col++,row,""+(cardGroupDetailsVO.getInPromo()));
                 * worksheet1.addCell(label);
                 * }
                 */
                // validity Type
                label = new Label(col++, row, cardGroupDetailsVO.getValidityPeriodType());
                worksheet1.addCell(label);
                // validity period
                label = new Label(col++, row, new Integer(cardGroupDetailsVO.getValidityPeriod()).toString());
                worksheet1.addCell(label);
                // grace period
                label = new Label(col++, row, new Long(cardGroupDetailsVO.getGracePeriod()).toString());
                worksheet1.addCell(label);
                if(!(PretupsI.VOUCHER_CONS_SERVICE).equals(p_hashMap.get(PretupsI.SERVICE_TYPE))){
                	// multi of
                if (cardGroupDetailsVO.getMultipleOf() != 0) {
                    try {
                        label = new Label(col++, row, PretupsBL.getDisplayAmount(cardGroupDetailsVO.getMultipleOf()));
                        worksheet1.addCell(label);
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        _log.error("writeModifyInDataSheetP2P", " Exception e: " + e.getMessage());
                        throw new BTSLBaseException(e);
                    }
                } else {
                    label = new Label(col++, row, PretupsBL.getDisplayAmount(0));
                    worksheet1.addCell(label);
                }
                // Sender tax1 name
                label = new Label(col++, row, cardGroupDetailsVO.getSenderTax1Name());
                worksheet1.addCell(label);

                // Sender tax1 type
                label = new Label(col++, row, cardGroupDetailsVO.getSenderTax1Type());
                worksheet1.addCell(label);

                // Sender tax1 rate
                if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equalsIgnoreCase(cardGroupDetailsVO.getSenderTax1Type())) {
                    // rec tax2 rate
                    label = new Label(col++, row, new Double(cardGroupDetailsVO.getSenderTax1Rate()).toString());
                    worksheet1.addCell(label);
                } else {
                    //label = new Label(col++, row, PretupsBL.getDisplayAmount((long) cardGroupDetailsVO.getSenderTax1Rate()));
                	label = new Label(col++, row, PretupsBL.getDisplayAmount(BTSLUtil.parseDoubleToLong(cardGroupDetailsVO.getSenderTax1Rate())));
                    worksheet1.addCell(label);
                }

                // Sender tax2 name
                label = new Label(col++, row, cardGroupDetailsVO.getSenderTax2Name());
                worksheet1.addCell(label);

                // Sender tax2 type
                label = new Label(col++, row, cardGroupDetailsVO.getSenderTax2Type());
                worksheet1.addCell(label);
                if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equalsIgnoreCase(cardGroupDetailsVO.getSenderTax2Type())) {
                    // rec tax2 rate
                    label = new Label(col++, row, new Double(cardGroupDetailsVO.getSenderTax2Rate()).toString());
                    worksheet1.addCell(label);
                } else {
                    label = new Label(col++, row, PretupsBL.getDisplayAmount(BTSLUtil.parseDoubleToLong(cardGroupDetailsVO.getSenderTax2Rate()) ));
                    worksheet1.addCell(label);
                }
                // rec access fee type

                label = new Label(col++, row, cardGroupDetailsVO.getSenderAccessFeeType());
                worksheet1.addCell(label);
                // rec access fee rate
                if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equalsIgnoreCase(cardGroupDetailsVO.getSenderAccessFeeType())) {
                    label = new Label(col++, row, new Double(cardGroupDetailsVO.getSenderAccessFeeRate()).toString());
                    worksheet1.addCell(label);
                } else {
                    try {
                        label = new Label(col++, row, PretupsBL.getDisplayAmount(BTSLUtil.parseDoubleToLong(cardGroupDetailsVO.getSenderAccessFeeRate()) ));
                        worksheet1.addCell(label);
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        _log.error("writeModifyInDataSheetP2P", " Exception e: " + e.getMessage());
                        throw new BTSLBaseException(e);
                    }
                }
                if (cardGroupDetailsVO.getMinSenderAccessFee() != 0 || cardGroupDetailsVO.getMaxSenderAccessFee() != 0)
                // min acccess fee
                {
                    try {
                        label = new Label(col++, row, PretupsBL.getDisplayAmount(cardGroupDetailsVO.getMinSenderAccessFee()));
                        worksheet1.addCell(label);
                        // max access fee
                        label = new Label(col++, row, PretupsBL.getDisplayAmount(cardGroupDetailsVO.getMaxSenderAccessFee()));
                        worksheet1.addCell(label);
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        _log.error("writeModifyInDataSheetP2P", " Exception e: " + e.getMessage());
                        throw new BTSLBaseException(e);
                    }
                } else {
                    label = new Label(col++, row, PretupsBL.getDisplayAmount(0));
                    worksheet1.addCell(label);
                    // max access fee
                    label = new Label(col++, row, PretupsBL.getDisplayAmount(0));
                    worksheet1.addCell(label);
                }
                }
                // rec tax1 name
                label = new Label(col++, row, cardGroupDetailsVO.getReceiverTax1Name());
                worksheet1.addCell(label);

                // rectax1 type
                label = new Label(col++, row, cardGroupDetailsVO.getReceiverTax1Type());
                worksheet1.addCell(label);

                // rec tax1 rate
                if (PretupsI.AMOUNT_TYPE_PERCENTAGE.equalsIgnoreCase(cardGroupDetailsVO.getReceiverTax1Type())) {
                    // rec tax2 rate
                    label = new Label(col++, row, new Double(cardGroupDetailsVO.getReceiverTax1Rate()).toString());
                    worksheet1.addCell(label);
                } else {
                    label = new Label(col++, row, PretupsBL.getDisplayAmount(BTSLUtil.parseDoubleToLong(cardGroupDetailsVO.getReceiverTax1Rate()) ));
                    worksheet1.addCell(label);
                }

                // rec tax2 name
                label = new Label(col++, row, cardGroupDetailsVO.getReceiverTax2Name());
                worksheet1.addCell(label);

                // rec tax2 type
                label = new Label(col++, row, cardGroupDetailsVO.getReceiverTax2Type());
                worksheet1.addCell(label);
                if ((PretupsI.AMOUNT_TYPE_PERCENTAGE).equalsIgnoreCase(cardGroupDetailsVO.getReceiverTax2Type())) {
                    // rec tax2 rate
                    label = new Label(col++, row, new Double(cardGroupDetailsVO.getReceiverTax2Rate()).toString());
                    worksheet1.addCell(label);
                } else {
                    label = new Label(col++, row, PretupsBL.getDisplayAmount(BTSLUtil.parseDoubleToLong(cardGroupDetailsVO.getReceiverTax2Rate())));
                    worksheet1.addCell(label);
                }
                // rec access fee type

                label = new Label(col++, row, cardGroupDetailsVO.getReceiverAccessFeeType());
                worksheet1.addCell(label);

                // rec access fee rate
                if ((PretupsI.AMOUNT_TYPE_PERCENTAGE).equalsIgnoreCase(cardGroupDetailsVO.getReceiverAccessFeeType())) {
                    label = new Label(col++, row, new Double(cardGroupDetailsVO.getReceiverAccessFeeRate()).toString());
                    worksheet1.addCell(label);
                } else {
                    try {
                        label = new Label(col++, row, PretupsBL.getDisplayAmount(BTSLUtil.parseDoubleToLong( cardGroupDetailsVO.getReceiverAccessFeeRate())));
                        worksheet1.addCell(label);
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        _log.error("writeModifyInDataSheetP2P", " Exception e: " + e.getMessage());
                        throw new BTSLBaseException(e);
                    }
                }
                if (cardGroupDetailsVO.getMinReceiverAccessFee() != 0 || cardGroupDetailsVO.getMaxReceiverAccessFee() != 0)
                // min acccess fee
                {
                    try {
                        label = new Label(col++, row, PretupsBL.getDisplayAmount(cardGroupDetailsVO.getMinReceiverAccessFee()));
                        worksheet1.addCell(label);
                        // max access fee
                        label = new Label(col++, row, PretupsBL.getDisplayAmount(cardGroupDetailsVO.getMaxReceiverAccessFee()));
                        worksheet1.addCell(label);
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        _log.error("writeModifyInDataSheetP2P", " Exception e: " + e.getMessage());
                        throw new BTSLBaseException(e);
                    }
                } else {
                    label = new Label(col++, row, PretupsBL.getDisplayAmount(0));
                    worksheet1.addCell(label);
                    // max access fee
                    label = new Label(col++, row, PretupsBL.getDisplayAmount(0));
                    worksheet1.addCell(label);
                }
                // Online
                data = cardGroupDetailsVO.getOnline();
                if (BTSLUtil.isNullString(data)) {
                    data = PretupsI.NO;
                }
                label = new Label(col++, row, data);
                worksheet1.addCell(label);

                // Both
                data = cardGroupDetailsVO.getBoth();
                if (BTSLUtil.isNullString(data)) {
                    data = PretupsI.NO;
                }
                label = new Label(col++, row, data);
                worksheet1.addCell(label);

                // Status
                data = cardGroupDetailsVO.getStatus();
                label = new Label(col++, row, data);
                worksheet1.addCell(label);

                // Applicable from: date
                label = new Label(col++, row, BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(cardGroupDetailsVO.getApplicableFrom())));
                label.addCellFeatures();
                worksheet1.addCell(label);

                // Applicable from: time
                label = new Label(col++, row, BTSLUtil.getDateTimeStringFromDate(cardGroupDetailsVO.getApplicableFrom(), ((Constants.getProperty("CARDGROUP_DATE_FORMAT")).split(" ")[1])));
                label.addCellFeatures();
                worksheet1.addCell(label);
                
                if(!(PretupsI.VOUCHER_CONS_SERVICE).equals(p_hashMap.get(PretupsI.SERVICE_TYPE)))
                {// Validity bonus
	                data = String.valueOf(cardGroupDetailsVO.getBonusValidityValue());
	                label = new Label(col++, row, data);
	                worksheet1.addCell(label);
	                // Sender conversion factor
	                data = cardGroupDetailsVO.getSenderConvFactor();
	                label = new Label(col++, row, data);
	                worksheet1.addCell(label);
	                // Receiver conversion factor
	                data = cardGroupDetailsVO.getReceiverConvFactor();
	                label = new Label(col++, row, data);
	                worksheet1.addCell(label);
                }
                // Bonuses associated with the card group.
                bonusList = cardGroupDetailsVO.getBonusAccList();
                if (bonusList != null && bonusList.size() > 0) {
                    for (int m = 0, n = bonusList.size(); m < n; m++) {
                        bonusAccountDetailsVO = (BonusAccountDetailsVO) bonusList.get(m);
                        if ("Y".equals(bonusAccountDetailsVO.getRestrictedOnIN())) {
                            // Type
                            data = bonusAccountDetailsVO.getType();
                            label = new Label(col++, row, data);
                            worksheet1.addCell(label);
                            // Bonus value
                            data = bonusAccountDetailsVO.getBonusValue();
                            label = new Label(col++, row, data);
                            worksheet1.addCell(label);
                            // Bonus validity
                            data = bonusAccountDetailsVO.getBonusValidity();
                            label = new Label(col++, row, data);
                            worksheet1.addCell(label);
                            // Bonus Conversion factor
                            data = bonusAccountDetailsVO.getMultFactor();
                            label = new Label(col++, row, data);
                            worksheet1.addCell(label);
                        }
                    }
                }
            }
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeModifyInDataSheetP2P", " Exception e: " + e.getMessage());
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeModifyInDataSheetP2P", " Exception e: " + e.getMessage());
        }
    }

    public static void main(String args[]) {
        final String METHOD_NAME = "main";
        BatchModifyC2SCardGroupExcelRW excel = new BatchModifyC2SCardGroupExcelRW();

        try {
            HashMap hashMap = null;
            excel.writeModifyExcel("TEST", "C2S", hashMap, null, null, "C:\\Sanjeew.xls", "");
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        }
    }

    public void writeModifyMultipleExcel(String p_excelID, String p_module, HashMap p_hashMap, MessageResources messages, Locale locale, String p_fileName, String p_networkCode) {
        if (_log.isDebugEnabled()) {
            _log.debug("writeModifyMultipleExcel", " p_excelID: " + p_excelID + ", p_module:" + p_module + ", p_hashMap:" + p_hashMap + ", p_locale: " + locale + ", p_fileName: " + p_fileName);
        }
        final String METHOD_NAME = "writeModifyMultipleExcel";
        WritableWorkbook workbook = null;
        WritableSheet worksheet1 = null;
        WritableSheet worksheet2 = null;
        int col = 0;
        int row = 0;
        String noOfRowsInOneTemplate = null;
        ArrayList cardgroupList = null;
        int cardgroupListsize = 0;
        int noOfTotalSheet = 0;
        try {
            p_messages = messages;
            p_locale = locale;
            workbook = Workbook.createWorkbook(new File(p_fileName));
            cardgroupList = (ArrayList) p_hashMap.get(PretupsI.BATCH_CARD_GROUP_EXCEL_DATA);
            cardgroupListsize = cardgroupList.size();
            noOfRowsInOneTemplate = Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE_CARDGROUP");
            int noOfRowsPerTemplate = 0;
            if (!BTSLUtil.isNullString(noOfRowsInOneTemplate)) {
                noOfRowsInOneTemplate = noOfRowsInOneTemplate.trim();
                noOfRowsPerTemplate = Integer.parseInt(noOfRowsInOneTemplate);
            } else {
                noOfRowsPerTemplate = 65500; // Default value of rows
            }
            // Number of sheet to display the user list
            noOfTotalSheet = BTSLUtil.parseDoubleToInt( Math.ceil(BTSLUtil.parseIntToDouble(cardgroupListsize) / noOfRowsPerTemplate));
            int i = 0;
            int k = 0;
            ArrayList tempList = new ArrayList();
            for (i = 0; i < noOfTotalSheet; i++) {
                tempList.clear();
                if (k + noOfRowsPerTemplate < cardgroupListsize) {
                    for (int j = k; j < k + noOfRowsPerTemplate; j++) {
                        tempList.add(cardgroupList.get(j));
                    }
                } else {
                    for (int j = k; j < cardgroupListsize; j++) {
                        tempList.add(cardgroupList.get(j));
                    }
                }
                p_hashMap.put(PretupsI.BATCH_CARD_GROUP_EXCEL_DATA, tempList);
                worksheet1 = workbook.createSheet("Template Sheet " + (i + 1), i);
                if (p_module.equals(PretupsI.P2P_MODULE)) {
                    this.writeModifyInDataSheetP2P(worksheet1, col, row, p_hashMap, p_networkCode);
                } else {
                    this.writeModifyInDataSheet(worksheet1, col, row, p_hashMap, p_networkCode);
                }
                k = k + noOfRowsPerTemplate;
            }
            col = 0;
            row = 0;
            worksheet2 = workbook.createSheet("Guideline Sheet", i);
            if (p_module.equals(PretupsI.P2P_MODULE)) {
                this.writeCardGroupSetP2P(worksheet2, col, row, p_hashMap);
            } else {
                this.writeCardGroupSet(worksheet2, col, row, p_hashMap);
            }
            ;

            workbook.write();

        }
        // end of try block
        catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeModifyMultipleExcel", " Exception e: " + e.getMessage());
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            worksheet1 = null;
            workbook = null;
            if (_log.isDebugEnabled()) {
                _log.debug("writeModifyMultipleExcel", " Exiting");
            }
        }
    }
    
    /**
	 * 
	 * @param p_excelID
	 * @param p_module
	 * @param p_hashMap
	 * @param locale
	 * @param p_fileName
	 * @param p_networkCode
	 */
	public void writeModifyMultipleExcelAngular(String p_excelID, String p_module, HashMap p_hashMap, Locale locale,
			String p_fileName, String p_networkCode) {
		final String METHOD_NAME = "writeModifyMultipleExcelAngular";

		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, " p_excelID: " + p_excelID + ", p_module:" + p_module + ", p_hashMap:" + p_hashMap
					+ ", p_locale: " + locale + ", p_fileName: " + p_fileName);
		}
		WritableWorkbook workbook = null;
		WritableSheet worksheet1 = null;
		WritableSheet worksheet2 = null;
		int col = 0;
		int row = 0;
		String noOfRowsInOneTemplate = null;
		ArrayList cardgroupList = null;
		int cardgroupListsize = 0;
		int noOfTotalSheet = 0;
		try {
			p_locale = locale;
			workbook = Workbook.createWorkbook(new File(p_fileName));
			cardgroupList = (ArrayList) p_hashMap.get(PretupsI.BATCH_CARD_GROUP_EXCEL_DATA);
			cardgroupListsize = cardgroupList.size();
			noOfRowsInOneTemplate = Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE_CARDGROUP");
			int noOfRowsPerTemplate = 0;
			if (!BTSLUtil.isNullString(noOfRowsInOneTemplate)) {
				noOfRowsInOneTemplate = noOfRowsInOneTemplate.trim();
				noOfRowsPerTemplate = Integer.parseInt(noOfRowsInOneTemplate);
			} else {
				noOfRowsPerTemplate = 65500; // Default value of rows
			}
			// Number of sheet to display the user list
			noOfTotalSheet = BTSLUtil
					.parseDoubleToInt(Math.ceil(BTSLUtil.parseIntToDouble(cardgroupListsize) / noOfRowsPerTemplate));
			int i = 0;
			int k = 0;
			ArrayList tempList = new ArrayList();
			for (i = 0; i < noOfTotalSheet; i++) {
				tempList.clear();
				if (k + noOfRowsPerTemplate < cardgroupListsize) {
					for (int j = k; j < k + noOfRowsPerTemplate; j++) {
						tempList.add(cardgroupList.get(j));
					}
				} else {
					for (int j = k; j < cardgroupListsize; j++) {
						tempList.add(cardgroupList.get(j));
					}
				}
				p_hashMap.put(PretupsI.BATCH_CARD_GROUP_EXCEL_DATA, tempList);
				worksheet1 = workbook.createSheet("Template Sheet " + (i + 1), i);

				this.writeModifyInDataSheetAngular(worksheet1, col, row, p_hashMap, p_networkCode);

				k = k + noOfRowsPerTemplate;
			}
			col = 0;
			row = 0;
			worksheet2 = workbook.createSheet("Guideline Sheet", i);

			this.writeCardGroupSetAngular(worksheet2, col, row, p_hashMap);

			workbook.write();

		}
		// end of try block
		catch (Exception e) {
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
			workbook = null;
			if (_log.isDebugEnabled()) {
				_log.debug(METHOD_NAME, " Exiting");
			}
		}
	}

	
	/**
	 * 
	 * @param worksheet1
	 * @param col
	 * @param row
	 * @param p_hashMap
	 * @param p_networkCode
	 * @throws ParseException
	 * @throws BTSLBaseException
	 */
	private void writeModifyInDataSheetAngular(WritableSheet worksheet1, int col, int row, HashMap p_hashMap,
			String p_networkCode) throws ParseException, BTSLBaseException {
		final String METHOD_NAME = "writeModifyInDataSheetAngular";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,
					" p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
		}
		WritableCellFeatures cellFeatures = new WritableCellFeatures();
		WritableFont times16font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, true);
		WritableCellFormat times16format = new WritableCellFormat(times16font);
		CellView lock = new CellView();
		String data = null;
		// Bonus related fields.
		ArrayList bonusList = null;
		BonusAccountDetailsVO bonusAccountDetailsVO = null;
		BonusBundleDetailVO bonusBundleDetailVO = null;
		try {
			String keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.heading",
					null);
			String comment = null;
			Label label = new Label(col, row, keyName, times16format);
			worksheet1.mergeCells(col, row, col + 5, row);
			worksheet1.addCell(label);
			row++;
			col = 0;
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.nwcode",
					null);
			label = new Label(col, row, keyName, times16format);
			worksheet1.mergeCells(col, row, col = col + 2, row);
			worksheet1.addCell(label);
			col++;
			label = new Label(col, row, p_networkCode);
			worksheet1.mergeCells(col, row, col + 3, row);
			worksheet1.addCell(label);
			row++;
			col = 0;
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.label.servicetype", null);
			label = new Label(col, row, keyName, times16format);
			worksheet1.mergeCells(col, row, col = col + 2, row);
			worksheet1.addCell(label);
			col++;
			label = new Label(col, row, (String) p_hashMap.get(PretupsI.SERVICE_TYPE));
			worksheet1.mergeCells(col, row, col + 3, row);
			worksheet1.addCell(label);
			col = 0;

			// ServiceType
			row++;
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.label.servicetype", null);

			label = new Label(col++, row, keyName, times16format);
			// add comment
			comment = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.comment",
					null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// cardgroupsetid
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.ID", null);
			label = new Label(col++, row, keyName, times16format);
			// add comment
			comment = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.comment",
					null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.cgID", null);
			label = new Label(col++, row, keyName, times16format);

			comment = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.cgID.comment",
					null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);
			// card group set name
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.cardname",
					null);

			label = new Label(col++, row, keyName, times16format);
			// add comment
			comment = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.comment",
					null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// cardgroupcode
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.cgcode",
					null);

			label = new Label(col++, row, keyName, times16format);
			comment = RestAPIStringParser.getMessage(p_locale,
					"restrictedsubs.rescheduletopupdetails.file.cgcode.comment", null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.cgname",
					null);

			label = new Label(col++, row, keyName, times16format);
			comment = RestAPIStringParser.getMessage(p_locale,
					"restrictedsubs.rescheduletopupdetails.file.cgname.comment", null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);
			// Sub service type
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.label.subservicetype",
					null);

			label = new Label(col++, row, keyName, times16format);
			// add comment
			comment = RestAPIStringParser.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.label.subservice", null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// start range
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.srtrange",
					null);

			label = new Label(col++, row, keyName, times16format);
			worksheet1.addCell(label);
			// end range
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.endrange",
					null);

			label = new Label(col++, row, keyName, times16format);
			worksheet1.addCell(label);
			// added by gaurav for COS
			// for COS required
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.COS_REQUIRED)).booleanValue()) {
				keyName = RestAPIStringParser.getMessage(p_locale,
						"cardgroup.set.batch.modify.xlsfile.header.cosrequired", null);
				label = new Label(col++, row, keyName, times16format);
				worksheet1.addCell(label);
			}
			keyName = RestAPIStringParser.getMessage(p_locale,
					"cardgroup.set.batch.modify.xlsfile.header.reversalpermitted", null);

			label = new Label(col++, row, keyName, times16format);
			worksheet1.addCell(label);
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IN_PROMO_REQUIRED)).booleanValue()) {
				keyName = RestAPIStringParser.getMessage(p_locale,
						"cardgroup.set.batch.modify.xlsfile.header.bonus.inpromo", null);
				label = new Label(col++, row, keyName, times16format);
				worksheet1.addCell(label);
			}
			// end here

			// validity Type Highest, Lowest and Commulative
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.validitytype",
					null);
			label = new Label(col++, row, keyName, times16format);
			comment = RestAPIStringParser.getMessage(p_locale,
					"cardgroup.set.batch.modify.xlsfile.header.validitytype.comment", null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// validity period
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.valDays",
					null);
			label = new Label(col++, row, keyName, times16format);
			worksheet1.addCell(label);

			// grace period
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.graceperiod",
					null);
			label = new Label(col++, row, keyName, times16format);
			worksheet1.addCell(label);

			// multiple of
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.multof",
					null);
			label = new Label(col++, row, keyName, times16format);
			worksheet1.addCell(label);

			// reciever_tax1_name
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recTax1.name",
					null);

			label = new Label(col++, row, keyName, times16format);

			comment = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.value", null);
			comment = comment + " " + RestAPIStringParser.getMessage(p_locale,
					"cardgroup.cardgroupdetails.label.receivertax1namevalue", null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// reciever_tax1_type
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recTax1.type",
					null);

			label = new Label(col++, row, keyName, times16format);
			comment = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.amtpct.comment",
					null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// reciever_tax1_rate
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recTax1.rate",
					null);
			label = new Label(col++, row, keyName, times16format);
			worksheet1.addCell(label);

			// reciever_tax2_name
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recTax2.name",
					null);

			label = new Label(col++, row, keyName, times16format);

			comment = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.value", null);
			comment = comment + " " + RestAPIStringParser.getMessage(p_locale,
					"cardgroup.cardgroupdetails.label.receivertax2namevalue", null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// reciever_tax2_type
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recTax2.type",
					null);

			label = new Label(col++, row, keyName, times16format);
			comment = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.amtpct.comment",
					null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// reciever_tax2_rate
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recTax2.rate",
					null);

			label = new Label(col++, row, keyName, times16format);
			worksheet1.addCell(label);

			// reciever access fee type
			keyName = RestAPIStringParser.getMessage(p_locale,
					"cardgroup.set.batch.modify.xlsfile.header.recAccFee.type", null);

			label = new Label(col++, row, keyName, times16format);
			comment = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.amtpct.comment",
					null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// reciever access fee rate
			keyName = RestAPIStringParser.getMessage(p_locale,
					"cardgroup.set.batch.modify.xlsfile.header.recAccFee.rate", null);

			label = new Label(col++, row, keyName, times16format);
			worksheet1.addCell(label);
			// min receiver access fee
			keyName = RestAPIStringParser.getMessage(p_locale,
					"cardgroup.set.batch.modify.xlsfile.header.recAccFee.min", null);

			label = new Label(col++, row, keyName, times16format);
			worksheet1.addCell(label);
			// max access fee
			keyName = RestAPIStringParser.getMessage(p_locale,
					"cardgroup.set.batch.modify.xlsfile.header.recAccFee.max", null);

			label = new Label(col++, row, keyName, times16format);
			worksheet1.addCell(label);

			// online
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.online",
					null);

			label = new Label(col++, row, keyName, times16format);
			comment = RestAPIStringParser.getMessage(p_locale,
					"cardgroup.set.batch.modify.xlsfile.header.online.comment", null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// both
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.both", null);

			label = new Label(col++, row, keyName, times16format);
			comment = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.both.comment",
					null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// Status
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.status.flag", null);

			label = new Label(col++, row, keyName, times16format);

			comment = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.status.flag.comment",
					null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// applicable Date
			keyName = RestAPIStringParser.getMessage(p_locale,
					"cardgroup.set.batch.modify.xlsfile.header.applicabledate", null);

			label = new Label(col++, row, keyName, times16format);

			comment = RestAPIStringParser.getMessage(p_locale,
					"cardgroup.set.batch.modify.xlsfile.header.applicabledate.comment", null);
			comment = comment + " (" + (Constants.getProperty("CARDGROUP_DATE_FORMAT")).split(" ")[0] + ")";

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// applicable time
			keyName = RestAPIStringParser.getMessage(p_locale,
					"cardgroup.set.batch.modify.xlsfile.header.applicabletime", null);

			label = new Label(col++, row, keyName, times16format);
			comment = RestAPIStringParser.getMessage(p_locale,
					"cardgroup.set.batch.modify.xlsfile.header.applicabletime.comment", null);
			comment = comment + " (" + (Constants.getProperty("CARDGROUP_DATE_FORMAT")).split(" ")[1] + ")";

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// Validity Bonus
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.validity.bonus",
					null);

			label = new Label(col++, row, keyName, times16format);
			cellFeatures = new WritableCellFeatures();
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// Receiver conversion factor
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.receiver.convfac",
					null);

			label = new Label(col++, row, keyName, times16format);
			cellFeatures = new WritableCellFeatures();
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			ArrayList cardGroupDetailsVOList = (ArrayList) p_hashMap.get(PretupsI.BATCH_CARD_GROUP_EXCEL_DATA);
			CardGroupDetailsVO cardGroupDetailsVO = null;

			// Defined bonuses
			bonusList = (ArrayList) p_hashMap.get(PretupsI.BONUS_BUNDLE_LIST);
			if (bonusList != null && bonusList.size() > 0) {
				for (int p = 0, q = bonusList.size(); p < q; p++) {
					bonusBundleDetailVO = (BonusBundleDetailVO) bonusList.get(p);
					String str = null;
					if ("Y".equals(bonusBundleDetailVO.getResINStatus())) {
						keyName = bonusBundleDetailVO.getBundleName();
						// Bonus bundle type label
						str = keyName + " " + RestAPIStringParser.getMessage(p_locale,
								"cardgroup.set.batch.modify.xlsfile.bonus.type", null);
						label = new Label(col++, row, str, times16format);
						cellFeatures = new WritableCellFeatures();
						label.setCellFeatures(cellFeatures);
						worksheet1.addCell(label);
						// Bonus bundle value label
						str = keyName + " " + RestAPIStringParser.getMessage(p_locale,
								"cardgroup.set.batch.modify.xlsfile.bonus.value", null);
						label = new Label(col++, row, str, times16format);
						cellFeatures = new WritableCellFeatures();
						label.setCellFeatures(cellFeatures);
						worksheet1.addCell(label);
						// Bonus bundle validity label
						str = keyName + " " + RestAPIStringParser.getMessage(p_locale,
								"cardgroup.set.batch.modify.xlsfile.bonus.validity", null);
						label = new Label(col++, row, str, times16format);
						cellFeatures = new WritableCellFeatures();
						label.setCellFeatures(cellFeatures);
						worksheet1.addCell(label);
						// Bonus bundle conversion factor label
						str = keyName + " " + RestAPIStringParser.getMessage(p_locale,
								"cardgroup.set.batch.modify.xlsfile.bonus.convfac", null);
						label = new Label(col++, row, str, times16format);
						cellFeatures = new WritableCellFeatures();
						label.setCellFeatures(cellFeatures);
						worksheet1.addCell(label);
					}
				}
				bonusList = null;
			}

			// cellf=new CellFeatures();
			for (int i = 0, j = cardGroupDetailsVOList.size(); i < j; i++) {
				cardGroupDetailsVO = (CardGroupDetailsVO) cardGroupDetailsVOList.get(i);
				row++;
				col = 0;

				// Service Type
				label = new Label(col++, row, cardGroupDetailsVO.getServiceTypeId());
				lock.setHidden(true);
				worksheet1.addCell(label);

				// card GroupsetID
				label = new Label(col++, row, cardGroupDetailsVO.getCardGroupSetID());
				lock.setHidden(true);
				worksheet1.addCell(label);
				label = new Label(col++, row, cardGroupDetailsVO.getCardGroupID());
				worksheet1.addCell(label);
				// set name
				label = new Label(col++, row, cardGroupDetailsVO.getCardGroupSetName());
				worksheet1.addCell(label);

				// cardgroupcode
				label = new Label(col++, row, cardGroupDetailsVO.getCardGroupCode());
				worksheet1.addCell(label);

				label = new Label(col++, row, cardGroupDetailsVO.getCardName());
				worksheet1.addCell(label);
				// C2s card group sub service
				label = new Label(col++, row, cardGroupDetailsVO.getCardGroupSubServiceId());
				worksheet1.addCell(label);

				// start range
				if (cardGroupDetailsVO.getStartRange() != 0) {
					try {
						label = new Label(col++, row, PretupsBL.getDisplayAmount(cardGroupDetailsVO.getStartRange()));
						worksheet1.addCell(label);
					} catch (Exception e) {
						_log.errorTrace(METHOD_NAME, e);
						_log.error(METHOD_NAME, " Exception e: " + e.getMessage());
						throw new BTSLBaseException(e);
					}
				} else {
					label = new Label(col++, row, PretupsBL.getDisplayAmount(0));
					worksheet1.addCell(label);
				}
				// end range
				if (cardGroupDetailsVO.getEndRange() != 0) {
					try {
						label = new Label(col++, row, PretupsBL.getDisplayAmount(cardGroupDetailsVO.getEndRange()));
						worksheet1.addCell(label);
					} catch (Exception e) {
						_log.errorTrace(METHOD_NAME, e);
						_log.error(METHOD_NAME, " Exception e: " + e.getMessage());
						throw new BTSLBaseException(e);
					}
				} else {
					label = new Label(col++, row, PretupsBL.getDisplayAmount(0));
					worksheet1.addCell(label);
				}

				// added for COS
				if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.COS_REQUIRED)).booleanValue()) {
					data = cardGroupDetailsVO.getCosRequired();
					if (BTSLUtil.isNullString(data)) {
						data = PretupsI.NO;
					}
					label = new Label(col++, row, data);
					worksheet1.addCell(label);
				}
				data = cardGroupDetailsVO.getReversalPermitted();
				if (BTSLUtil.isNullString(data)) {
					data = PretupsI.NO;
				}
				label = new Label(col++, row, data);
				worksheet1.addCell(label);
				if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IN_PROMO_REQUIRED))
						.booleanValue()) {
					// updated by akanksha for Tigo GTCR
					// label = new
					// Label(col++,row,""+(cardGroupDetailsVO.getInPromo()));
					label = new Label(col++, row, "" + (cardGroupDetailsVO.getInPromoAsString()));
					worksheet1.addCell(label);
				}
				// validity Type
				label = new Label(col++, row, cardGroupDetailsVO.getValidityPeriodType());
				worksheet1.addCell(label);

				// validity period
				label = new Label(col++, row, new Integer(cardGroupDetailsVO.getValidityPeriod()).toString());
				worksheet1.addCell(label);

				// grace period
				label = new Label(col++, row, new Long(cardGroupDetailsVO.getGracePeriod()).toString());
				worksheet1.addCell(label);

				// multi of
				if (cardGroupDetailsVO.getMultipleOf() != 0) {
					try {
						label = new Label(col++, row, PretupsBL.getDisplayAmount(cardGroupDetailsVO.getMultipleOf()));
						worksheet1.addCell(label);
					} catch (Exception e) {
						_log.errorTrace(METHOD_NAME, e);
						_log.error(METHOD_NAME, " Exception e: " + e.getMessage());
						throw new BTSLBaseException(e);
					}
				} else {
					label = new Label(col++, row, PretupsBL.getDisplayAmount(0));
					worksheet1.addCell(label);
				}
				// rec tax1 name
				label = new Label(col++, row, cardGroupDetailsVO.getReceiverTax1Name());
				worksheet1.addCell(label);

				// rectax1 type
				label = new Label(col++, row, cardGroupDetailsVO.getReceiverTax1Type());
				worksheet1.addCell(label);

				// rec tax1 rate
				if (cardGroupDetailsVO.getReceiverTax1Type().equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
					// rec tax2 rate
					label = new Label(col++, row, new Double(cardGroupDetailsVO.getReceiverTax1Rate()).toString());
					worksheet1.addCell(label);
				} else {
					// label = new Label(col++, row, PretupsBL.getDisplayAmount((long)
					// cardGroupDetailsVO.getReceiverTax1Rate()));
					label = new Label(col++, row, PretupsBL
							.getDisplayAmount(BTSLUtil.parseDoubleToLong(cardGroupDetailsVO.getReceiverTax1Rate())));
					worksheet1.addCell(label);
				}

				// rec tax2 name
				label = new Label(col++, row, cardGroupDetailsVO.getReceiverTax2Name());
				worksheet1.addCell(label);

				// rec tax2 type
				label = new Label(col++, row, cardGroupDetailsVO.getReceiverTax2Type());
				worksheet1.addCell(label);
				if (cardGroupDetailsVO.getReceiverTax2Type().equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
					// rec tax2 rate
					label = new Label(col++, row, new Double(cardGroupDetailsVO.getReceiverTax2Rate()).toString());
					worksheet1.addCell(label);
				} else {
					label = new Label(col++, row, PretupsBL
							.getDisplayAmount(BTSLUtil.parseDoubleToLong(cardGroupDetailsVO.getReceiverTax2Rate())));
					worksheet1.addCell(label);
				}
				// rec access fee type

				label = new Label(col++, row, cardGroupDetailsVO.getReceiverAccessFeeType());
				worksheet1.addCell(label);

				// rec access fee rate
				if (cardGroupDetailsVO.getReceiverAccessFeeType().equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
					label = new Label(col++, row, new Double(cardGroupDetailsVO.getReceiverAccessFeeRate()).toString());
					worksheet1.addCell(label);
				} else {
					try {
						label = new Label(col++, row, PretupsBL.getDisplayAmount(
								BTSLUtil.parseDoubleToLong(cardGroupDetailsVO.getReceiverAccessFeeRate())));
						worksheet1.addCell(label);
					} catch (Exception e) {
						_log.errorTrace(METHOD_NAME, e);
						_log.error("writeModifyInDataSheet", " Exception e: " + e.getMessage());
						throw new BTSLBaseException(e);
					}
				}
				// min acccess fee
				if (cardGroupDetailsVO.getMinReceiverAccessFee() != 0
						|| cardGroupDetailsVO.getMaxReceiverAccessFee() != 0) {
					try {
						label = new Label(col++, row,
								PretupsBL.getDisplayAmount(cardGroupDetailsVO.getMinReceiverAccessFee()));
						worksheet1.addCell(label);
						// max access fee
						label = new Label(col++, row,
								PretupsBL.getDisplayAmount(cardGroupDetailsVO.getMaxReceiverAccessFee()));
						worksheet1.addCell(label);
					} catch (Exception e) {
						_log.errorTrace(METHOD_NAME, e);
						_log.error(METHOD_NAME, " Exception e: " + e.getMessage());
						throw new BTSLBaseException(e);
					}
				} else {
					label = new Label(col++, row, PretupsBL.getDisplayAmount(0));
					worksheet1.addCell(label);
					// max access fee
					label = new Label(col++, row, PretupsBL.getDisplayAmount(0));
					worksheet1.addCell(label);
				}
				// Online
				data = cardGroupDetailsVO.getOnline();
				if (BTSLUtil.isNullString(data)) {
					data = PretupsI.NO;
				}
				label = new Label(col++, row, data);
				worksheet1.addCell(label);

				// Both
				data = cardGroupDetailsVO.getBoth();
				if (BTSLUtil.isNullString(data)) {
					data = PretupsI.NO;
				}
				label = new Label(col++, row, data);
				worksheet1.addCell(label);

				// Status
				data = cardGroupDetailsVO.getStatus();
				label = new Label(col++, row, data);
				worksheet1.addCell(label);

				// Applicable from: date
				label = new Label(col++, row, BTSLDateUtil
						.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(cardGroupDetailsVO.getApplicableFrom())));
				label.addCellFeatures();
				worksheet1.addCell(label);

				// Applicable from: time
				label = new Label(col++, row, BTSLUtil.getDateTimeStringFromDate(cardGroupDetailsVO.getApplicableFrom(),
						((Constants.getProperty("CARDGROUP_DATE_FORMAT")).split(" ")[1])));
				label.addCellFeatures();
				worksheet1.addCell(label);

				// Validity bonus
				data = String.valueOf(cardGroupDetailsVO.getBonusValidityValue());
				label = new Label(col++, row, data);
				worksheet1.addCell(label);

				// Receiver conversion factor
				data = cardGroupDetailsVO.getReceiverConvFactor();
				label = new Label(col++, row, data);
				worksheet1.addCell(label);

				// Bonuses associated with the card group.
				bonusList = cardGroupDetailsVO.getBonusAccList();
				if (bonusList != null && bonusList.size() > 0) {
					for (int m = 0, n = bonusList.size(); m < n; m++) {
						bonusAccountDetailsVO = (BonusAccountDetailsVO) bonusList.get(m);
						// Bonus code
						if ("Y".equals(bonusAccountDetailsVO.getRestrictedOnIN())) {
							// Type
							data = bonusAccountDetailsVO.getType();
							label = new Label(col++, row, data);
							worksheet1.addCell(label);
							// Bonus value
							data = bonusAccountDetailsVO.getBonusValue();
							label = new Label(col++, row, data);
							worksheet1.addCell(label);
							// Bonus validity
							data = bonusAccountDetailsVO.getBonusValidity();
							label = new Label(col++, row, data);
							worksheet1.addCell(label);
							// Bonus Conversion factor
							data = bonusAccountDetailsVO.getMultFactor();
							label = new Label(col++, row, data);
							worksheet1.addCell(label);
						}
					}
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

	
	private void writeCardGroupSetAngular(WritableSheet worksheet1, int col, int row, HashMap p_hashMap)
			throws Exception {
		if (_log.isDebugEnabled()) {
			_log.debug("writeCardGroupSetAngular",
					" p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
		}
		final String METHOD_NAME = "writeCardGroupSetAngular";
		WritableFont times16font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, true);
		WritableCellFormat times16format = new WritableCellFormat(times16font);
		WritableFont times12font = new WritableFont(WritableFont.COURIER, 10, WritableFont.BOLD, true);
		WritableCellFormat times12format = new WritableCellFormat(times12font);

		WritableFont times11font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, true);
		WritableCellFormat times11format = new WritableCellFormat(times11font);
		times11format.setAlignment(Alignment.CENTRE);
		times11format.setVerticalAlignment(VerticalAlignment.CENTRE);

		WritableFont times10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, true);
		times10font.setColour(Colour.RED);
		times10font.setItalic(false);
		WritableCellFormat times10format = new WritableCellFormat(times10font);
		times10format.setAlignment(Alignment.LEFT);
		times10format.setVerticalAlignment(VerticalAlignment.TOP);
		times10format.setWrap(true);

		WritableFont times10Afont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, true);
		times10Afont.setColour(Colour.BLACK);
		times10Afont.setItalic(false);
		WritableCellFormat times10Aformat = new WritableCellFormat(times10Afont);
		times10Aformat.setAlignment(Alignment.LEFT);
		times10Aformat.setVerticalAlignment(VerticalAlignment.TOP);
		times10Aformat.setBorder(Border.ALL, BorderLineStyle.NONE);
		times10format.setWrap(true);

		WritableFont times10Bfont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, true);
		times10Bfont.setColour(Colour.BLACK);
		times10Bfont.setItalic(false);
		WritableCellFormat times10Bformat = new WritableCellFormat(times10Bfont);
		times10Bformat.setAlignment(Alignment.LEFT);
		times10Bformat.setVerticalAlignment(VerticalAlignment.TOP);
		times10Bformat.setBorder(Border.ALL, BorderLineStyle.NONE);
		times10Bformat.setWrap(true);

		try {
			String keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.heading",
					null);

			Label label = new Label(col, row, keyName, times16format);
			worksheet1.mergeCells(col, row, col + 16, row);
			worksheet1.addCell(label);
			row++;
			col = 0;

			// Guidelines
			row++;
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.guidelines", null);

			label = new Label(col, ++row, keyName, times11format);
			worksheet1.mergeCells(col, row, col + 14, row);
			worksheet1.addCell(label);
			row++;
			col = 0;

			// sub head1
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.guidelines.desc",
					null);
			label = new Label(col, ++row, keyName, times10format);
			worksheet1.mergeCells(col, row, col + 16, row = row + 8);
			worksheet1.addCell(label);
			row++;
			col = 0;
			// instruction for modifing card group
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.guidelines.modify",
					null);
			label = new Label(col, ++row, keyName, times10Aformat);
			worksheet1.mergeCells(col, row, col + 10, row);
			worksheet1.addCell(label);
			row++;
			col = 0;

			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.desc.modify", null);
			label = new Label(col, row, keyName, times10Bformat);
			worksheet1.mergeCells(col, row, col + 14, row = row + 20);
			worksheet1.addCell(label);
			row++;
			col = 0;

			// instruction to add new slab in a card group
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.guidelines.add",
					null);
			label = new Label(col, ++row, keyName, times10Aformat);
			worksheet1.mergeCells(col, row, col + 10, row);
			worksheet1.addCell(label);
			row++;
			col = 0;

			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.head.desc.add", null);
			label = new Label(col, row, keyName, times10Bformat);
			worksheet1.mergeCells(col, row, col + 14, row = row + 20);
			worksheet1.addCell(label);
			row++;
			col = 0;
			// C2S card group sub service
			row++;
			row++;
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.label.subservicetype",
					null);
			label = new Label(col, ++row, keyName, times16format);
			worksheet1.mergeCells(col, row, col + 2, row);
			worksheet1.addCell(label);
			row++;
			col = 0;

			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.selectorname",
					null);
			label = new Label(col++, row, keyName, times12format);
			worksheet1.addCell(label);

			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.selectorcode",
					null);
			label = new Label(col++, row, keyName, times12format);
			worksheet1.addCell(label);
			row++;
			col = 0;
			// Commented by akanksha for tigo GTCR
			ArrayList ServiceSelectorMappingList = ServiceSelectorMappingCache.loadSelectorDropDownForCardGroup();
			Iterator itr = ServiceSelectorMappingList.iterator();
			ListValueVO ls = null;
			String selectorName = null;
			String value = null;
			String[] tempArr = new String[2];
			while (itr.hasNext()) {
				ls = (ListValueVO) itr.next();
				selectorName = ls.getLabel();
				value = ls.getValue();
				tempArr = value.split(":");
				if (tempArr.length == 2 && tempArr[0].equalsIgnoreCase((String) p_hashMap.get(PretupsI.SERVICE_TYPE)))
				// if(value.split(":")[0].equalsIgnoreCase((String)
				// p_hashMap.get(PretupsI.SERVICE_TYPE)))
				{
					label = new Label(col++, row, selectorName);
					worksheet1.addCell(label);

					label = new Label(col++, row, tempArr[1]);
					worksheet1.addCell(label);
					row++;
					col = 0;
				}
			}
			tempArr = null;
			// CVG
			/*
			 * keyName = p_messages.getMessage(p_locale,
			 * "cardgroup.cardgroupc2sdetails.modify.cvgname"); label = new
			 * Label(col++,row,keyName); worksheet1.addCell(label);
			 * 
			 * keyName = p_messages.getMessage(p_locale,
			 * "cardgroup.cardgroupc2sdetails.modify.cvgcode"); label = new
			 * Label(col++,row,keyName); worksheet1.addCell(label); row++; col=0; //C
			 * keyName = p_messages.getMessage(p_locale,
			 * "cardgroup.cardgroupc2sdetails.modify.cname"); label = new
			 * Label(col++,row,keyName); worksheet1.addCell(label);
			 * 
			 * keyName = p_messages.getMessage(p_locale,
			 * "cardgroup.cardgroupc2sdetails.modify.ccode"); label = new
			 * Label(col++,row,keyName); worksheet1.addCell(label); row++; col=0; //VG
			 * keyName = p_messages.getMessage(p_locale,
			 * "cardgroup.cardgroupc2sdetails.modify.vgname"); label = new
			 * Label(col++,row,keyName); worksheet1.addCell(label);
			 * 
			 * keyName = p_messages.getMessage(p_locale,
			 * "cardgroup.cardgroupc2sdetails.modify.vgcode"); label = new
			 * Label(col++,row,keyName); worksheet1.addCell(label); row++; col=0;
			 */
			// Validity Type
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.validitytype",
					null);
			label = new Label(col, ++row, keyName, times16format);
			worksheet1.mergeCells(col, row, col + 2, row);
			worksheet1.addCell(label);
			row++;
			col = 0;

			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.validitytype",
					null);
			keyName = keyName + " "
					+ RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.name", null);
			label = new Label(col++, row, keyName, times12format);
			worksheet1.addCell(label);

			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.validitytype",
					null);
			keyName = keyName + " "
					+ RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.code", null);
			label = new Label(col++, row, keyName, times12format);
			worksheet1.addCell(label);
			row++;
			col = 0;

			// Highest
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.highest", null);
			label = new Label(col++, row, keyName);
			worksheet1.addCell(label);

			keyName = PretupsI.VALPERIOD_HIGHEST_TYPE;
			label = new Label(col++, row, keyName);
			worksheet1.addCell(label);
			row++;
			col = 0;
			// Lowest
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.lowest", null);
			label = new Label(col++, row, keyName);
			worksheet1.addCell(label);

			keyName = PretupsI.VALPERIOD_LOWEST_TYPE;
			label = new Label(col++, row, keyName);
			worksheet1.addCell(label);
			row++;
			col = 0;

			// Cumulative
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.modify.cummulative",
					null);
			label = new Label(col++, row, keyName);
			worksheet1.addCell(label);

			keyName = PretupsI.VALPERIOD_CUMMULATIVE_TYPE;
			label = new Label(col++, row, keyName);
			worksheet1.addCell(label);
			row++;
			col = 0;

			// Tax1 Type
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recTax1.type",
					null);
			label = new Label(col, ++row, keyName, times16format);
			worksheet1.mergeCells(col, row, col + 2, row);
			worksheet1.addCell(label);
			row++;
			col = 0;

			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.name", null);
			label = new Label(col++, row, keyName, times12format);
			worksheet1.addCell(label);

			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.code", null);
			label = new Label(col++, row, keyName, times12format);
			worksheet1.addCell(label);
			row++;
			col = 0;
			// Percentage
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.percentage",
					null);
			label = new Label(col++, row, keyName);
			worksheet1.addCell(label);

			keyName = PretupsI.AMOUNT_TYPE_PERCENTAGE;
			label = new Label(col++, row, keyName);
			worksheet1.addCell(label);
			row++;
			col = 0;
			// Amount
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.amount",
					null);
			label = new Label(col++, row, keyName);
			worksheet1.addCell(label);

			keyName = PretupsI.AMOUNT_TYPE_AMOUNT;
			label = new Label(col++, row, keyName);
			worksheet1.addCell(label);
			row++;
			col = 0;

			// Tax2 Type
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recTax2.type",
					null);
			label = new Label(col, ++row, keyName, times16format);
			worksheet1.mergeCells(col, row, col + 2, row);
			worksheet1.addCell(label);
			row++;
			col = 0;

			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.name", null);
			label = new Label(col++, row, keyName, times12format);
			worksheet1.addCell(label);

			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.code", null);
			label = new Label(col++, row, keyName, times12format);
			worksheet1.addCell(label);
			row++;
			col = 0;

			// Percentage
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.percentage",
					null);
			label = new Label(col++, row, keyName);
			worksheet1.addCell(label);

			keyName = PretupsI.AMOUNT_TYPE_PERCENTAGE;
			label = new Label(col++, row, keyName);
			worksheet1.addCell(label);
			row++;
			col = 0;
			// Amount
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.amount",
					null);
			label = new Label(col++, row, keyName);
			worksheet1.addCell(label);

			keyName = PretupsI.AMOUNT_TYPE_AMOUNT;
			label = new Label(col++, row, keyName);
			worksheet1.addCell(label);
			row++;
			col = 0;

			// Processing fee type
			keyName = RestAPIStringParser.getMessage(p_locale,
					"cardgroup.set.batch.modify.xlsfile.header.recAccFee.type", null);
			label = new Label(col, ++row, keyName, times16format);
			worksheet1.mergeCells(col, row, col + 2, row);
			worksheet1.addCell(label);
			row++;
			col = 0;

			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.name", null);
			label = new Label(col++, row, keyName, times12format);
			worksheet1.addCell(label);

			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.code", null);
			label = new Label(col++, row, keyName, times12format);
			worksheet1.addCell(label);
			row++;
			col = 0;
			// Percentage
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.percentage",
					null);
			label = new Label(col++, row, keyName);
			worksheet1.addCell(label);

			keyName = PretupsI.AMOUNT_TYPE_PERCENTAGE;
			label = new Label(col++, row, keyName);
			worksheet1.addCell(label);
			row++;
			col = 0;
			// Amount
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.amount",
					null);
			label = new Label(col++, row, keyName);
			worksheet1.addCell(label);

			keyName = PretupsI.AMOUNT_TYPE_AMOUNT;
			label = new Label(col++, row, keyName);
			worksheet1.addCell(label);
			row++;
			col = 0;

			// Bonus Talk value type
			keyName = RestAPIStringParser.getMessage(p_locale,
					"cardgroup.set.batch.modify.xlsfile.header.bonus.tlkvalType", null);
			label = new Label(col, ++row, keyName, times16format);
			worksheet1.mergeCells(col, row, col + 2, row);
			worksheet1.addCell(label);
			row++;
			col = 0;

			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.name", null);
			label = new Label(col++, row, keyName, times12format);
			worksheet1.addCell(label);

			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.code", null);
			label = new Label(col++, row, keyName, times12format);
			worksheet1.addCell(label);
			row++;
			col = 0;
			// Percentage
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.percentage",
					null);
			label = new Label(col++, row, keyName);
			worksheet1.addCell(label);

			keyName = PretupsI.AMOUNT_TYPE_PERCENTAGE;
			label = new Label(col++, row, keyName);
			worksheet1.addCell(label);
			row++;
			col = 0;
			// Amount
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.amount",
					null);
			label = new Label(col++, row, keyName);
			worksheet1.addCell(label);

			keyName = PretupsI.AMOUNT_TYPE_AMOUNT;
			label = new Label(col++, row, keyName);
			worksheet1.addCell(label);
			row++;
			col = 0;
		} catch (RowsExceededException e) {
			_log.errorTrace(METHOD_NAME, e);
			_log.error(METHOD_NAME, " Exception e: " + e.getMessage());
			throw e;
		} catch (WriteException e) {
			_log.errorTrace(METHOD_NAME, e);
			_log.error(METHOD_NAME, " Exception e: " + e.getMessage());
			throw e;
		}
	}

	public void writeModifyMultipleExcelAngularErrorFile(String p_excelID, String p_module, HashMap p_hashMap,
			Locale locale, String p_fileName, String p_networkCode, ArrayList errorList, ArrayList successList) {
		final String METHOD_NAME = "writeModifyMultipleExcelAngularErrorFile";

		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, " p_excelID: " + p_excelID + ", p_module:" + p_module + ", p_hashMap:" + p_hashMap
					+ ", p_locale: " + locale + ", p_fileName: " + p_fileName);
		}
		WritableWorkbook workbook = null;
		WritableSheet worksheet1 = null;
		WritableSheet worksheet2 = null;
		int col = 0;
		int row = 0;
		String noOfRowsInOneTemplate = null;
		ArrayList cardgroupList = null;
		int cardgroupListsize = 0;
		int noOfTotalSheet = 0;
		HashMap<Integer, String> errorLines = new HashMap<>();
		for (Iterator<ListValueVO> i = errorList.iterator(); i.hasNext();) {
			ListValueVO vo = i.next();
			int key;
			try {
				key = Integer.parseInt(vo.getOtherInfo().split("-")[1].substring(4));
			} catch (Exception e) {
				key = Integer.parseInt(vo.getOtherInfo()) + 1;
			}
			errorLines.put(key, vo.getOtherInfo2());
		}

		try {
			p_locale = locale;
			workbook = Workbook.createWorkbook(new File(p_fileName));
			cardgroupList = (ArrayList) p_hashMap.get(PretupsI.BATCH_CARD_GROUP_EXCEL_DATA);
			cardgroupListsize = cardgroupList.size();
			noOfRowsInOneTemplate = Constants.getProperty("NUMBER_OF_ROWS_PER_TEMPLATE_FILE_CARDGROUP");
			int noOfRowsPerTemplate = 0;
			if (!BTSLUtil.isNullString(noOfRowsInOneTemplate)) {
				noOfRowsInOneTemplate = noOfRowsInOneTemplate.trim();
				noOfRowsPerTemplate = Integer.parseInt(noOfRowsInOneTemplate);
			} else {
				noOfRowsPerTemplate = 65500; // Default value of rows
			}
			// Number of sheet to display the user list
			noOfTotalSheet = BTSLUtil
					.parseDoubleToInt(Math.ceil(BTSLUtil.parseIntToDouble(cardgroupListsize) / noOfRowsPerTemplate));
			int i = 0;
			int k = 0;
			ArrayList tempList = new ArrayList();
			for (i = 0; i < noOfTotalSheet; i++) {
				tempList.clear();
				if (k + noOfRowsPerTemplate < cardgroupListsize) {
					for (int j = k; j < k + noOfRowsPerTemplate; j++) {
						tempList.add(cardgroupList.get(j));
					}
				} else {
					for (int j = k; j < cardgroupListsize; j++) {
						tempList.add(cardgroupList.get(j));
					}
				}

				p_hashMap.put(PretupsI.BATCH_CARD_GROUP_EXCEL_DATA, tempList);
				worksheet1 = workbook.createSheet("Template Sheet " + (i + 1), i);

				this.writeModifyInDataSheetAngularError(worksheet1, col, row, p_hashMap, p_networkCode, errorLines,
						successList);

				k = k + noOfRowsPerTemplate;
			}
			col = 0;
			row = 0;
			worksheet2 = workbook.createSheet("Guideline Sheet", i);

			this.writeCardGroupSetAngular(worksheet2, col, row, p_hashMap);

			workbook.write();

		}
		// end of try block
		catch (Exception e) {
			_log.errorTrace(METHOD_NAME, e);
			_log.error("writeModifyMultipleExcel", " Exception e: " + e.getMessage());
		} finally {
			try {
				if (workbook != null) {
					workbook.close();
				}
			} catch (Exception e) {
				_log.errorTrace(METHOD_NAME, e);
			}
			worksheet1 = null;
			workbook = null;
			if (_log.isDebugEnabled()) {
				_log.debug("writeModifyMultipleExcel", " Exiting");
			}
		}

	}

	/**
	 * @author sarthak.saini
	 * @param worksheet1
	 * @param col
	 * @param row
	 * @param p_hashMap
	 * @param p_networkCode
	 * @param errorLines
	 * @param modifyDatalist
	 * @throws ParseException
	 * @throws BTSLBaseException
	 */
	private void writeModifyInDataSheetAngularError(WritableSheet worksheet1, int col, int row, HashMap p_hashMap,
			String p_networkCode, HashMap<Integer, String> errorLines, ArrayList successList)
			throws ParseException, BTSLBaseException {
		final String METHOD_NAME = "writeModifyInDataSheetAngularError";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME,
					" p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
		}
		WritableCellFeatures cellFeatures = new WritableCellFeatures();
		WritableFont times16font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, true);
		WritableCellFormat times16format = new WritableCellFormat(times16font);
		CellView lock = new CellView();
		String data = null;
		// Bonus related fields.
		ArrayList bonusList = null;
		BonusAccountDetailsVO bonusAccountDetailsVO = null;
		BonusBundleDetailVO bonusBundleDetailVO = null;
		try {
			String keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.heading",
					null);
			String comment = null;
			Label label = new Label(col, row, keyName, times16format);
			worksheet1.mergeCells(col, row, col + 5, row);
			worksheet1.addCell(label);
			row++;
			col = 0;
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.nwcode",
					null);
			label = new Label(col, row, keyName, times16format);
			worksheet1.mergeCells(col, row, col = col + 2, row);
			worksheet1.addCell(label);
			col++;
			label = new Label(col, row, p_networkCode);
			worksheet1.mergeCells(col, row, col + 3, row);
			worksheet1.addCell(label);
			row++;
			col = 0;
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.label.servicetype", null);
			label = new Label(col, row, keyName, times16format);
			worksheet1.mergeCells(col, row, col = col + 2, row);
			worksheet1.addCell(label);
			col++;
			label = new Label(col, row, (String) p_hashMap.get(PretupsI.SERVICE_TYPE));
			worksheet1.mergeCells(col, row, col + 3, row);
			worksheet1.addCell(label);
			col = 0;

			// ServiceType
			row++;
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.label.servicetype", null);

			label = new Label(col++, row, keyName, times16format);
			// add comment
			comment = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.comment",
					null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// cardgroupsetid
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.ID", null);
			label = new Label(col++, row, keyName, times16format);
			// add comment
			comment = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.comment",
					null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.cgID", null);
			label = new Label(col++, row, keyName, times16format);

			comment = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.cgID.comment",
					null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);
			// card group set name
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.cardname",
					null);

			label = new Label(col++, row, keyName, times16format);
			// add comment
			comment = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.comment",
					null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// cardgroupcode
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.cgcode",
					null);

			label = new Label(col++, row, keyName, times16format);
			comment = RestAPIStringParser.getMessage(p_locale,
					"restrictedsubs.rescheduletopupdetails.file.cgcode.comment", null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.cgname",
					null);

			label = new Label(col++, row, keyName, times16format);
			comment = RestAPIStringParser.getMessage(p_locale,
					"restrictedsubs.rescheduletopupdetails.file.cgname.comment", null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);
			// Sub service type
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.label.subservicetype",
					null);

			label = new Label(col++, row, keyName, times16format);
			// add comment
			comment = RestAPIStringParser.getMessage(p_locale, "cardgroup.cardgroupc2sdetails.label.subservice", null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// start range
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.srtrange",
					null);

			label = new Label(col++, row, keyName, times16format);
			worksheet1.addCell(label);
			// end range
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.endrange",
					null);

			label = new Label(col++, row, keyName, times16format);
			worksheet1.addCell(label);
			// added by gaurav for COS
			// for COS required
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.COS_REQUIRED)).booleanValue()) {
				keyName = RestAPIStringParser.getMessage(p_locale,
						"cardgroup.set.batch.modify.xlsfile.header.cosrequired", null);
				label = new Label(col++, row, keyName, times16format);
				worksheet1.addCell(label);
			}
			keyName = RestAPIStringParser.getMessage(p_locale,
					"cardgroup.set.batch.modify.xlsfile.header.reversalpermitted", null);

			label = new Label(col++, row, keyName, times16format);
			worksheet1.addCell(label);
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IN_PROMO_REQUIRED)).booleanValue()) {
				keyName = RestAPIStringParser.getMessage(p_locale,
						"cardgroup.set.batch.modify.xlsfile.header.bonus.inpromo", null);
				label = new Label(col++, row, keyName, times16format);
				worksheet1.addCell(label);
			}
			// end here

			// validity Type Highest, Lowest and Commulative
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.validitytype",
					null);
			label = new Label(col++, row, keyName, times16format);
			comment = RestAPIStringParser.getMessage(p_locale,
					"cardgroup.set.batch.modify.xlsfile.header.validitytype.comment", null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// validity period
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.valDays",
					null);
			label = new Label(col++, row, keyName, times16format);
			worksheet1.addCell(label);

			// grace period
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.graceperiod",
					null);
			label = new Label(col++, row, keyName, times16format);
			worksheet1.addCell(label);

			// multiple of
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.multof",
					null);
			label = new Label(col++, row, keyName, times16format);
			worksheet1.addCell(label);

			// reciever_tax1_name
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recTax1.name",
					null);

			label = new Label(col++, row, keyName, times16format);

			comment = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.value", null);
			comment = comment + " " + RestAPIStringParser.getMessage(p_locale,
					"cardgroup.cardgroupdetails.label.receivertax1namevalue", null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// reciever_tax1_type
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recTax1.type",
					null);

			label = new Label(col++, row, keyName, times16format);
			comment = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.amtpct.comment",
					null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// reciever_tax1_rate
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recTax1.rate",
					null);
			label = new Label(col++, row, keyName, times16format);
			worksheet1.addCell(label);

			// reciever_tax2_name
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recTax2.name",
					null);

			label = new Label(col++, row, keyName, times16format);

			comment = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.value", null);
			comment = comment + " " + RestAPIStringParser.getMessage(p_locale,
					"cardgroup.cardgroupdetails.label.receivertax2namevalue", null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// reciever_tax2_type
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recTax2.type",
					null);

			label = new Label(col++, row, keyName, times16format);
			comment = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.amtpct.comment",
					null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// reciever_tax2_rate
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.recTax2.rate",
					null);

			label = new Label(col++, row, keyName, times16format);
			worksheet1.addCell(label);

			// reciever access fee type
			keyName = RestAPIStringParser.getMessage(p_locale,
					"cardgroup.set.batch.modify.xlsfile.header.recAccFee.type", null);

			label = new Label(col++, row, keyName, times16format);
			comment = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.amtpct.comment",
					null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// reciever access fee rate
			keyName = RestAPIStringParser.getMessage(p_locale,
					"cardgroup.set.batch.modify.xlsfile.header.recAccFee.rate", null);

			label = new Label(col++, row, keyName, times16format);
			worksheet1.addCell(label);
			// min receiver access fee
			keyName = RestAPIStringParser.getMessage(p_locale,
					"cardgroup.set.batch.modify.xlsfile.header.recAccFee.min", null);

			label = new Label(col++, row, keyName, times16format);
			worksheet1.addCell(label);
			// max access fee
			keyName = RestAPIStringParser.getMessage(p_locale,
					"cardgroup.set.batch.modify.xlsfile.header.recAccFee.max", null);

			label = new Label(col++, row, keyName, times16format);
			worksheet1.addCell(label);

			// online
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.online",
					null);

			label = new Label(col++, row, keyName, times16format);
			comment = RestAPIStringParser.getMessage(p_locale,
					"cardgroup.set.batch.modify.xlsfile.header.online.comment", null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// both
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.both", null);

			label = new Label(col++, row, keyName, times16format);
			comment = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.header.both.comment",
					null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// Status
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.status.flag", null);

			label = new Label(col++, row, keyName, times16format);

			comment = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.status.flag.comment",
					null);

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// applicable Date
			keyName = RestAPIStringParser.getMessage(p_locale,
					"cardgroup.set.batch.modify.xlsfile.header.applicabledate", null);

			label = new Label(col++, row, keyName, times16format);

			comment = RestAPIStringParser.getMessage(p_locale,
					"cardgroup.set.batch.modify.xlsfile.header.applicabledate.comment", null);
			comment = comment + " (" + (Constants.getProperty("CARDGROUP_DATE_FORMAT")).split(" ")[0] + ")";

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// applicable time
			keyName = RestAPIStringParser.getMessage(p_locale,
					"cardgroup.set.batch.modify.xlsfile.header.applicabletime", null);

			label = new Label(col++, row, keyName, times16format);
			comment = RestAPIStringParser.getMessage(p_locale,
					"cardgroup.set.batch.modify.xlsfile.header.applicabletime.comment", null);
			comment = comment + " (" + (Constants.getProperty("CARDGROUP_DATE_FORMAT")).split(" ")[1] + ")";

			cellFeatures = new WritableCellFeatures();
			cellFeatures.setComment(comment);
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// Validity Bonus
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.validity.bonus",
					null);

			label = new Label(col++, row, keyName, times16format);
			cellFeatures = new WritableCellFeatures();
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			// Receiver conversion factor
			keyName = RestAPIStringParser.getMessage(p_locale, "cardgroup.set.batch.modify.xlsfile.receiver.convfac",
					null);

			label = new Label(col++, row, keyName, times16format);
			cellFeatures = new WritableCellFeatures();
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			ArrayList cardGroupDetailsVOList = (ArrayList) p_hashMap.get(PretupsI.BATCH_CARD_GROUP_EXCEL_DATA);
			CardGroupDetailsVO cardGroupDetailsVO = null;

			// Defined bonuses
			bonusList = (ArrayList) p_hashMap.get(PretupsI.BONUS_BUNDLE_LIST);
			if (bonusList != null && bonusList.size() > 0) {
				for (int p = 0, q = bonusList.size(); p < q; p++) {
					bonusBundleDetailVO = (BonusBundleDetailVO) bonusList.get(p);
					String str = null;
					if ("Y".equals(bonusBundleDetailVO.getResINStatus())) {
						keyName = bonusBundleDetailVO.getBundleName();
						// Bonus bundle type label
						str = keyName + " " + RestAPIStringParser.getMessage(p_locale,
								"cardgroup.set.batch.modify.xlsfile.bonus.type", null);
						label = new Label(col++, row, str, times16format);
						cellFeatures = new WritableCellFeatures();
						label.setCellFeatures(cellFeatures);
						worksheet1.addCell(label);
						// Bonus bundle value label
						str = keyName + " " + RestAPIStringParser.getMessage(p_locale,
								"cardgroup.set.batch.modify.xlsfile.bonus.value", null);
						label = new Label(col++, row, str, times16format);
						cellFeatures = new WritableCellFeatures();
						label.setCellFeatures(cellFeatures);
						worksheet1.addCell(label);
						// Bonus bundle validity label
						str = keyName + " " + RestAPIStringParser.getMessage(p_locale,
								"cardgroup.set.batch.modify.xlsfile.bonus.validity", null);
						label = new Label(col++, row, str, times16format);
						cellFeatures = new WritableCellFeatures();
						label.setCellFeatures(cellFeatures);
						worksheet1.addCell(label);
						// Bonus bundle conversion factor label
						str = keyName + " " + RestAPIStringParser.getMessage(p_locale,
								"cardgroup.set.batch.modify.xlsfile.bonus.convfac", null);
						label = new Label(col++, row, str, times16format);
						cellFeatures = new WritableCellFeatures();
						label.setCellFeatures(cellFeatures);
						worksheet1.addCell(label);

					}
				}
				bonusList = null;
			}
			// Error
			keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.ERROR_MESSAGE_LABEL, null);
			label = new Label(col++, row, keyName, times16format);
			cellFeatures = new WritableCellFeatures();
			label.setCellFeatures(cellFeatures);
			worksheet1.addCell(label);

			int offset = 0;
			String prevCard = "";
			String prevData = "";
			// cellf=new CellFeatures();
			for (int i = 0, j = cardGroupDetailsVOList.size(); i < j; i++) {
				cardGroupDetailsVO = (CardGroupDetailsVO) cardGroupDetailsVOList.get(i);

				if (successList.contains(cardGroupDetailsVO.getCardGroupSetID())) {
					offset++;
					continue;
				}
				row++;
				col = 0;

				// Service Type
				label = new Label(col++, row, cardGroupDetailsVO.getServiceTypeId());
				lock.setHidden(true);
				worksheet1.addCell(label);

				// card GroupsetID
				label = new Label(col++, row, cardGroupDetailsVO.getCardGroupSetID());
				lock.setHidden(true);
				worksheet1.addCell(label);
				label = new Label(col++, row, cardGroupDetailsVO.getCardGroupID());
				worksheet1.addCell(label);
				// set name
				label = new Label(col++, row, cardGroupDetailsVO.getCardGroupSetName());
				worksheet1.addCell(label);

				// cardgroupcode
				label = new Label(col++, row, cardGroupDetailsVO.getCardGroupCode());
				worksheet1.addCell(label);

				label = new Label(col++, row, cardGroupDetailsVO.getCardName());
				worksheet1.addCell(label);
				// C2s card group sub service
				label = new Label(col++, row, cardGroupDetailsVO.getCardGroupSubServiceId());
				worksheet1.addCell(label);

				// start range
				if (cardGroupDetailsVO.getStartRange() != 0) {
					try {
						label = new Label(col++, row, String.valueOf(cardGroupDetailsVO.getStartRange()));
						worksheet1.addCell(label);
					} catch (Exception e) {
						_log.errorTrace(METHOD_NAME, e);
						_log.error(METHOD_NAME, " Exception e: " + e.getMessage());
						throw new BTSLBaseException(e);
					}
				} else {
					label = new Label(col++, row, PretupsBL.getDisplayAmount(0));
					worksheet1.addCell(label);
				}
				// end range
				if (cardGroupDetailsVO.getEndRange() != 0) {
					try {
						label = new Label(col++, row, String.valueOf(cardGroupDetailsVO.getEndRange()));
						worksheet1.addCell(label);
					} catch (Exception e) {
						_log.errorTrace(METHOD_NAME, e);
						_log.error(METHOD_NAME, " Exception e: " + e.getMessage());
						throw new BTSLBaseException(e);
					}
				} else {
					label = new Label(col++, row, PretupsBL.getDisplayAmount(0));
					worksheet1.addCell(label);
				}

				// added for COS
				if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.COS_REQUIRED)).booleanValue()) {
					data = cardGroupDetailsVO.getCosRequired();
					if (BTSLUtil.isNullString(data)) {
						data = PretupsI.NO;
					}
					label = new Label(col++, row, data);
					worksheet1.addCell(label);
				}
				data = cardGroupDetailsVO.getReversalPermitted();
				if (BTSLUtil.isNullString(data)) {
					data = PretupsI.NO;
				}
				label = new Label(col++, row, data);
				worksheet1.addCell(label);
				if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IN_PROMO_REQUIRED))
						.booleanValue()) {
					// updated by akanksha for Tigo GTCR
					// label = new
					// Label(col++,row,""+(cardGroupDetailsVO.getInPromo()));
					label = new Label(col++, row, "" + (cardGroupDetailsVO.getInPromoAsString()));
					worksheet1.addCell(label);
				}
				// validity Type
				label = new Label(col++, row, cardGroupDetailsVO.getValidityPeriodType());
				worksheet1.addCell(label);

				// validity period
				label = new Label(col++, row, new Integer(cardGroupDetailsVO.getValidityPeriod()).toString());
				worksheet1.addCell(label);

				// grace period
				label = new Label(col++, row, new Long(cardGroupDetailsVO.getGracePeriod()).toString());
				worksheet1.addCell(label);

				// multi of
				if (cardGroupDetailsVO.getMultipleOf() != 0) {
					try {
						label = new Label(col++, row, String.valueOf(cardGroupDetailsVO.getMultipleOf()));
						worksheet1.addCell(label);
					} catch (Exception e) {
						_log.errorTrace(METHOD_NAME, e);
						_log.error(METHOD_NAME, " Exception e: " + e.getMessage());
						throw new BTSLBaseException(e);
					}
				} else {
					label = new Label(col++, row, PretupsBL.getDisplayAmount(0));
					worksheet1.addCell(label);
				}
				// rec tax1 name
				label = new Label(col++, row, cardGroupDetailsVO.getReceiverTax1Name());
				worksheet1.addCell(label);

				// rectax1 type
				label = new Label(col++, row, cardGroupDetailsVO.getReceiverTax1Type());
				worksheet1.addCell(label);

				// rec tax1 rate
				if (cardGroupDetailsVO.getReceiverTax1Type().equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
					// rec tax2 rate
					label = new Label(col++, row, new Double(cardGroupDetailsVO.getReceiverTax1Rate()).toString());
					worksheet1.addCell(label);
				} else {
					// label = new Label(col++, row, PretupsBL.getDisplayAmount((long)
					// cardGroupDetailsVO.getReceiverTax1Rate()));
					label = new Label(col++, row,
							String.valueOf(BTSLUtil.parseDoubleToLong(cardGroupDetailsVO.getReceiverTax1Rate())));
					worksheet1.addCell(label);
				}

				// rec tax2 name
				label = new Label(col++, row, cardGroupDetailsVO.getReceiverTax2Name());
				worksheet1.addCell(label);

				// rec tax2 type
				label = new Label(col++, row, cardGroupDetailsVO.getReceiverTax2Type());
				worksheet1.addCell(label);
				if (cardGroupDetailsVO.getReceiverTax2Type().equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
					// rec tax2 rate
					label = new Label(col++, row, new Double(cardGroupDetailsVO.getReceiverTax2Rate()).toString());
					worksheet1.addCell(label);
				} else {
					label = new Label(col++, row,
							String.valueOf(BTSLUtil.parseDoubleToLong(cardGroupDetailsVO.getReceiverTax2Rate())));
					worksheet1.addCell(label);
				}
				// rec access fee type

				label = new Label(col++, row, cardGroupDetailsVO.getReceiverAccessFeeType());
				worksheet1.addCell(label);

				// rec access fee rate
				if (cardGroupDetailsVO.getReceiverAccessFeeType().equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
					label = new Label(col++, row, new Double(cardGroupDetailsVO.getReceiverAccessFeeRate()).toString());
					worksheet1.addCell(label);
				} else {
					try {
						label = new Label(col++, row, String
								.valueOf(BTSLUtil.parseDoubleToLong(cardGroupDetailsVO.getReceiverAccessFeeRate())));
						worksheet1.addCell(label);
					} catch (Exception e) {
						_log.errorTrace(METHOD_NAME, e);
						_log.error("writeModifyInDataSheet", " Exception e: " + e.getMessage());
						throw new BTSLBaseException(e);
					}
				}
				// min acccess fee
				if (cardGroupDetailsVO.getMinReceiverAccessFee() != 0
						|| cardGroupDetailsVO.getMaxReceiverAccessFee() != 0) {
					try {
						label = new Label(col++, row, String.valueOf(cardGroupDetailsVO.getMinReceiverAccessFee()));
						worksheet1.addCell(label);
						// max access fee
						label = new Label(col++, row, String.valueOf(cardGroupDetailsVO.getMaxReceiverAccessFee()));
						worksheet1.addCell(label);
					} catch (Exception e) {
						_log.errorTrace(METHOD_NAME, e);
						_log.error(METHOD_NAME, " Exception e: " + e.getMessage());
						throw new BTSLBaseException(e);
					}
				} else {
					label = new Label(col++, row, PretupsBL.getDisplayAmount(0));
					worksheet1.addCell(label);
					// max access fee
					label = new Label(col++, row, PretupsBL.getDisplayAmount(0));
					worksheet1.addCell(label);
				}
				// Online
				data = cardGroupDetailsVO.getOnline();
				if (BTSLUtil.isNullString(data)) {
					data = PretupsI.NO;
				}
				label = new Label(col++, row, data);
				worksheet1.addCell(label);

				// Both
				data = cardGroupDetailsVO.getBoth();
				if (BTSLUtil.isNullString(data)) {
					data = PretupsI.NO;
				}
				label = new Label(col++, row, data);
				worksheet1.addCell(label);

				// Status
				data = cardGroupDetailsVO.getStatus();
				label = new Label(col++, row, data);
				worksheet1.addCell(label);

				// Applicable from: date
                if(cardGroupDetailsVO.getApplicableFrom() !=null){
				label = new Label(col++, row, BTSLDateUtil
						.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(cardGroupDetailsVO.getApplicableFrom())));
				label.addCellFeatures();
				worksheet1.addCell(label);
                }else{
                    label = new Label(col++, row,"");
                    label.addCellFeatures();
                    worksheet1.addCell(label);
                }

				// Applicable from: time
				label = new Label(col++, row, cardGroupDetailsVO.getBonusTalkTimeValidity()); // Used to store
																								// Applicable Time in
																								// case of error file
																								// creation
				label.addCellFeatures();
				worksheet1.addCell(label);

				// Validity bonus
				data = String.valueOf(cardGroupDetailsVO.getBonusValidityValue());
				label = new Label(col++, row, data);
				worksheet1.addCell(label);

				// Receiver conversion factor
				data = cardGroupDetailsVO.getReceiverConvFactor();
				label = new Label(col++, row, data);
				worksheet1.addCell(label);

				// Bonuses associated with the card group.
				bonusList = cardGroupDetailsVO.getBonusAccList();
				if (bonusList != null && bonusList.size() > 0) {
					for (int m = 0, n = bonusList.size(); m < n; m++) {
						bonusAccountDetailsVO = (BonusAccountDetailsVO) bonusList.get(m);
						// Bonus code
						if ("Y".equals(bonusAccountDetailsVO.getRestrictedOnIN())) {
							// Type
							data = bonusAccountDetailsVO.getType();
							label = new Label(col++, row, data);
							worksheet1.addCell(label);
							// Bonus value
							data = bonusAccountDetailsVO.getBonusValue();
							label = new Label(col++, row, data);
							worksheet1.addCell(label);
							// Bonus validity
							data = bonusAccountDetailsVO.getBonusValidity();
							label = new Label(col++, row, data);
							worksheet1.addCell(label);
							// Bonus Conversion factor
							data = bonusAccountDetailsVO.getMultFactor();
							label = new Label(col++, row, data);
							worksheet1.addCell(label);
						}
					}
				}

				// Error
				if (errorLines.containsKey(row + 1 + offset)) {
					data = errorLines.get(row + 1 + offset);
					prevData = data;
				}
//                else if (prevCard.equals(cardGroupDetailsVO.getCardGroupSetID()))  {
//					data = prevData;
//					prevData = data;
//				}
                else
					data = "";
				label = new Label(col++, row, data);
				worksheet1.addCell(label);

//				prevCard = cardGroupDetailsVO.getCardGroupSetID();

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

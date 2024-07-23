package com.btsl.pretups.xl;

/**
 * @# BatchModifyCommProfileExcelRW.java
 *    Created by Created on History
 *    --------------------------------------------------------------------------
 *    ------
 *    gaurav pandey April 06, 2012 Initial creation
 *    --------------------------------------------------------------------------
 *    ------
 *    This class use for read write in xls file for batch modify commission
 *    profile.
 */
import java.io.File;
import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.util.MessageResources;

import com.btsl.common.ListValueVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.AdditionalProfileDeatilsVO;
import com.btsl.pretups.channel.profile.businesslogic.BatchModifyCommissionProfileVO;
import com.btsl.pretups.channel.profile.businesslogic.OTFDetailsVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.xl.ExcelFileConstants;
import com.restapi.networkadmin.commissionprofile.responseVO.BatchAddCommProfRespVO;
import com.web.pretups.channel.profile.web.CommissionProfileForm;

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

/**
 * 
 * @author gaurav.pandey
 * 
 */

public class BatchModifyCommProfileExcelRW {
    private Log log = LogFactory.getLog(this.getClass().getName());
    private static final  int COLUMN_MARGIN = 10;
    private WritableCellFeatures cellFeatures = new WritableCellFeatures();
   private  WritableCellFeatures cellFeaturesAdd = new WritableCellFeatures();
   private WritableFont times16font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, true);
   private WritableCellFormat times16format = new WritableCellFormat(times16font);

    private MessageResources pMessages = null;

    private Locale pLocale = null;
   private  Connection pCon = null;


    /**
     * 
     * @param pExcelID
     * @param pHashMap
     * @param messages
     * @param locale
     * @param pFileName
     * @throws Exception
     */
    public void writeExcel(String pExcelID, HashMap pHashMap, MessageResources messages, Locale locale, String pFileName, String sequenceNo) {
        final String methodName = "writeExcel";
        if (log.isDebugEnabled()) {
            log.debug("writeExcel", " p_excelID: " + pExcelID + " p_hashMap:" + pHashMap + " locale: " + locale + " fileName: " + pFileName);
        }
        WritableWorkbook workbook = null;
        WritableSheet worksheet1 = null;
        WritableSheet worksheet2 = null;
        WritableSheet worksheet3 = null;
        WritableSheet worksheet4 = null;
        
        int col = 0;
        int row = 0;
        int count = 0;
        try {

            workbook = Workbook.createWorkbook(new File(pFileName));
            worksheet1 = workbook.createSheet("commission profile Sheet", count++);
            if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE)))
            worksheet4 = workbook.createSheet("CBC Slab Sheet", count++);
            worksheet2 = workbook.createSheet("Master Sheet", count++);
            worksheet3 = workbook.createSheet("additional comm profile sheet", count++);
            pMessages = messages;
            pLocale = locale;
            Label label = null;
            String keyName = null;
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.mastersheet.heading", (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN), (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY));
            label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, COLUMN_MARGIN, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            row = this.writeProductName(worksheet2, col, row, pHashMap);
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue()){
            	row++;
            	col = 0;
            	row = this.writeTransactionType(worksheet2, col, row, pHashMap);
            }
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_MODE_ALWD))).booleanValue()){
            	row++;
            	col = 0;
            	row = this.writePaymentModeData(worksheet2, col, row, pHashMap);
            }
            row++;
            col = 0;
            row = this.writeDoaminName(worksheet2, col, row, pHashMap);
            row++;
            col = 0;
            row = this.writeCtegoryName(worksheet2, col, row, pHashMap);
            row++;
            col = 0;
            row = this.writeServiceListData(worksheet2, col, row, pHashMap);
            row++;
            col = 0;
            if (!BTSLUtil.isNullString(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVC_PROD_MAPPING_ALLOWED)))) {
                row = this.writeSubServiceListData(worksheet2, col, row, pHashMap);
                row++;
                col = 0;
            }
            ++row;
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.mastersheet.commission.heading");
            label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.mastersheet.commission.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGIN, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            ++row;
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.mastersheet.tax.type");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batchusercreation.mastersheet.tax.description");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.mastersheet.amt");
            label = new Label(col, row, keyName);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batchusercreation.mastersheet.amount");
            label = new Label(++col, row, keyName);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.mastersheet.pct");
            label = new Label(col, row, keyName);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batchusercreation.mastersheet.percentage");
            label = new Label(++col, row, keyName);
            worksheet2.addCell(label);
            
            //Added by Lalit
            
            row++;
            col = 0;
            ++row;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.dual.profile.type");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.dual.profile.type.description");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            
            final ArrayList dualCommissionTypeList = LookupsCache.loadLookupDropDown(PretupsI.DUAL_COMM_TYPE, true);
            
            for(Object vo : dualCommissionTypeList){
            	ListValueVO lv = (ListValueVO)vo;
            	row++;
                col = 0;
                label = new Label(col, row, lv.getValue());
                worksheet2.addCell(label);
                label = new Label(++col, row, lv.getLabel());
                worksheet2.addCell(label);
            }
           
                   
            
            row++;
            col = 0;
            ++row;
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.mastersheet.tax.on.foc");
            label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, COLUMN_MARGIN, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.mastersheet.tax.value");
            label = new Label(col, row, keyName);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.mastersheet.tax.on.c2c");
            label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, COLUMN_MARGIN, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.mastersheet.tax.value");
            label = new Label(col, row, keyName);
            worksheet2.addCell(label);
            if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE)) || (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))){
            	row++;
                row++;
                col=0;
                keyName = pMessages.getMessage(pLocale, "commission.profile.add.additional.commission.otf.detail.common");
                label = new Label(col, row, keyName, times16format);
                worksheet2.mergeCells(col, row, col + 1, row);
                worksheet2.addCell(label);
                
                row++;
                keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.otf.comment.to.type");
                label = new Label(col, row, keyName);
                worksheet2.addCell(label);
                
                if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))) {
                row++;
                keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.otf.type.ID.comment");
                label = new Label(col, row, keyName);
                worksheet2.addCell(label);
                
                row++;
                keyName = pMessages.getMessage(pLocale, "batch.add.modify.master.otf.comment.date.and.time");
                label = new Label(col, row, keyName);
                worksheet2.addCell(label);
                }
                else if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))) {
                    row++;
                    keyName = pMessages.getMessage(pLocale, "base.batchAddModifyComm.profile.otf.type.ID.comment");
                    label = new Label(col, row, keyName);
                    worksheet2.addCell(label);
                    
                    row++;
                    keyName = pMessages.getMessage(pLocale, "base.batchAddModifyComm.profile.otf.date.time");
                    label = new Label(col, row, keyName);
                    worksheet2.addCell(label);
                }
                
            }
            row = 0;
            col = 0;
            this.writeCommissionSheet(worksheet1, col, row, pHashMap);
            if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))){
            row = 0;
            col = 0;
            this.writeOTFSheetForModify(worksheet4, col, row, pHashMap);
            }
            row = 0;
            col = 0;
            this.writeAdditionalCommissionSheet(worksheet3, col, row, pHashMap,sequenceNo);

            workbook.write();

        } catch (Exception e) {
            log.errorTrace(methodName, e);
            log.error("writeExcel", " Exception e: " + e.getMessage());
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            worksheet1 = null;
            worksheet2 = null;
            workbook = null;
            if (log.isDebugEnabled()) {
                log.debug("writeExcel", " Exiting");
            }
        }
    }

    /**
     * this method is used to write product code and product name in master data
     * sheet
     * 
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */
    public int writeProductName(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) {
        final String METHOD_NAME = "writeProductName";
        if (log.isDebugEnabled()) {
            log.debug("writeProductName", " hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
        }
        try {
            ++row;
            String keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.mastersheet.product.heading");
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.mastersheet.product.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGIN, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.mastersheet.productCode");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batchusercreation.mastersheet.productName");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;
            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_PRODUCT_LIST);
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
            log.errorTrace(METHOD_NAME, e);
            log.error("writeUserPrifix", " Exception e: " + e.getMessage());
        } catch (WriteException e) {
            log.errorTrace(METHOD_NAME, e);
            log.error("writeUserPrifix", " Exception e: " + e.getMessage());
        }
        return row;
    }

    /**
     * 
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */

    public int writeDoaminName(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) {
        final String METHOD_NAME = "writeDoaminName";
        if (log.isDebugEnabled()) {
            log.debug("write domain name", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
        }
        try {
            String keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.mastersheet.domain.heading");
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.mastersheet.domain.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGIN, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.mastersheet.domainCode");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batchusercreation.mastersheet.domainName");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName = (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN_CODE);
            label = new Label(col, row, keyName);
            worksheet2.addCell(label);
            keyName = (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN);
            label = new Label(++col, row, keyName);
            worksheet2.addCell(label);
            return row;

        } catch (RowsExceededException e) {
            log.errorTrace(METHOD_NAME, e);
            log.error("writeDomainName", " Exception e: " + e.getMessage());
        } catch (WriteException e) {
            log.errorTrace(METHOD_NAME, e);
            log.error("writeDomainName", " Exception e: " + e.getMessage());
        }
        return row;
    }

    /**
     * 
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */

    public int writeCtegoryName(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) {
        final String methodName = "writeCtegoryName";
        if (log.isDebugEnabled()) {
            log.debug("write category name", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
        }
        String categoryCode = null;
        MessageGatewayVO msgGateVO = null;
        try {
            ++row;
            String keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.mastersheet.category.heading");
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.mastersheet.category.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGIN, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.mastersheet.categoryCode");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batchusercreation.mastersheet.categoryName");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.gatewaycode");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.gatewayname");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;
            ArrayList gatewayList = (ArrayList) p_hashMap.get(PretupsI.BATCH_COMM_GATEWAY_LIST);

            keyName = (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY_CODE);
            label = new Label(col, row, keyName);
            worksheet2.addCell(label);
            categoryCode = keyName;
            keyName = (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY);
            label = new Label(++col, row, keyName);
            worksheet2.addCell(label);
            int gatewayListSize = gatewayList.size();
            for (int j = 0; j < gatewayListSize; j++) {
                int gateCol = col;
                msgGateVO = (MessageGatewayVO) gatewayList.get(j);
                if (msgGateVO.getCategoryCode().equals(categoryCode)) {
                    label = new Label(++gateCol, row, msgGateVO.getGatewayCode());
                    worksheet2.addCell(label);
                    label = new Label(++gateCol, row, msgGateVO.getGatewayName());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;

        } catch (WriteException e) {
            log.errorTrace(methodName, e);
            log.error("writeDomainName", " Exception e: " + e.getMessage());
        } 
        return row;
    }

    /**
     * 
     * @param worksheet1
     * @param col
     * @param row
     * @param pHashMap
     * @throws Exception
     */

    private void writeCommissionSheet(WritableSheet worksheet1, int col, int row, HashMap pHashMap) throws Exception {
        final String METHOD_NAME = "writeCommissionSheet";
        if (log.isDebugEnabled()) {
            log.debug("writeCommissionSheet", " p_hashMap size=" + pHashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
        }
        try {

            int multiple_factor = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR);
            int slabLengthOTF=0;
            int slabItr;
            String KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.template");
            Label label = new Label(col, row, KeyName, times16format);
            worksheet1.mergeCells(col, row, col + 10, row);
            worksheet1.addCell(label);
            row++;
            col = 0;
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.downloadedby");

            label = new Label(col, row, KeyName, times16format);
            worksheet1.mergeCells(col, row, col + 1, row);
            worksheet1.addCell(label);
            col = col + 2;
            KeyName = (String) pHashMap.get(PretupsI.DOWNLOADED_BY);
            label = new Label(col, row, KeyName, times16format);
            worksheet1.mergeCells(col, row, col + 2, row);
            worksheet1.addCell(label);
            row++;
            col = 0;
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.network");
            label = new Label(col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName = (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_NAME);
            label = new Label(++col, row, KeyName);
            worksheet1.addCell(label);
            row++;
            col = 0;
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.mastersheet.domainCode");
            label = new Label(col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName = (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN_CODE);
            label = new Label(++col, row, KeyName);
            worksheet1.addCell(label);
            row++;
            col = 0;
            KeyName = pMessages.getMessage(pLocale, "batchusercreation.mastersheet.categoryName");
            label = new Label(col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName = (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY);
            label = new Label(++col, row, KeyName);
            worksheet1.addCell(label);
            row++;
            col = 0;
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.star");
            label = new Label(++col, row, KeyName);
            worksheet1.mergeCells(col, row, col + 5, row);
            worksheet1.addCell(label);

            row++;
            col = 0;
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.commission.slab");
            col = col + 9;
            label = new Label(col, row, KeyName, times16format);
            worksheet1.mergeCells(col, row, col + 8, row);
            worksheet1.addCell(label);
            col = col + 9;
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.commission");
            label = new Label(col, row, KeyName, times16format);
            worksheet1.mergeCells(col, row, col + 1, row);
            worksheet1.addCell(label);
            
            col=col+2;
            KeyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.taxes");
            label = new Label(col, row, KeyName, times16format);
            worksheet1.mergeCells(col, row, col + 5, row);
            worksheet1.addCell(label);
            
            row++;
            col = 0;

            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.name");
            label = new Label(col, row, KeyName, times16format);
            worksheet1.addCell(label);

            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.Short.code");
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);

            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.mastersheet.productCode");
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);

			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue()) {
				KeyName = pMessages.getMessage(pLocale,	"batch.add.comm.profile.transaction.type");
				label = new Label(++col, row, KeyName, times16format);
				worksheet1.addCell(label);
				String commentType = pMessages.getMessage(pLocale,	"batch.add.comm.profile.transaction.type.comment");
				cellFeatures = new WritableCellFeatures();
				cellFeatures.setComment(commentType);
				label.setCellFeatures(cellFeatures);
			}
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_MODE_ALWD))).booleanValue()) {
				KeyName = pMessages.getMessage(pLocale,
						"batch.add.comm.profile.paymentcode");
				label = new Label(++col, row, KeyName, times16format);
				worksheet1.addCell(label);
			}
            KeyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.grphdomaincode");
            label = new Label(++col, row, KeyName, times16format);
            String comment = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.geographycode.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            KeyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.gradecode");
            label = new Label(++col, row, KeyName, times16format);
            comment = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.gradecode.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.setID");
            label = new Label(++col, row, KeyName, times16format);
            comment = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.setID.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);
            
            KeyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.dual.profile");
            label = new Label(++col, row, KeyName, times16format);
            cellFeatures = new WritableCellFeatures();
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.version");
            label = new Label(++col, row, KeyName, times16format);
            comment = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.version.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.product.ID");
            label = new Label(++col, row, KeyName, times16format);
            comment = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.product.ID.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);
            //detail id
            KeyName = pMessages.getMessage(pLocale, "base.batchModifyCommProfile.commission.profile.detailID");
            label = new Label(++col, row, KeyName, times16format);
            comment = pMessages.getMessage(pLocale, "base.batchModifyCommProfile.commission.profile.product.detail.ID.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.additional.commission.profile.applicable.from");
            label = new Label(++col, row, KeyName, times16format);
            //Bug Fix
            comment = pMessages.getMessage(pLocale, "profile.commissionprofiledetailview.label.applicablefromformat");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.applicable.time");
            label = new Label(++col, row, KeyName, times16format);
            //
            comment = pMessages.getMessage(pLocale, "profile.commissionprofiledetailview.label.applicablefromhourformat");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.taxon.foc");
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.taxon.c2c");
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.multiple.of");
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.min.transfer");
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.max.transfer");
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.from.range");
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.to.range");
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.commission.type");
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.commisison.rate");
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.tax1.type");
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.tax1.rate");
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.tax2.type");
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.tax2.rate");
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.tax3.type");
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.tax3.rate");
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            col = 0;
            row++;
            BatchModifyCommissionProfileVO batchModifyCommissionProfileVO;
            ArrayList list = (ArrayList) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_SET_NAME);
            int listSize = list.size();
            for (int i = 0; i < listSize; i++) {
                batchModifyCommissionProfileVO = (BatchModifyCommissionProfileVO) list.get(i);
                
                KeyName = batchModifyCommissionProfileVO.getCommProfileSetName();
                label = new Label(col, row, KeyName);
                worksheet1.addCell(label);
                KeyName = batchModifyCommissionProfileVO.getShortCode();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                KeyName = batchModifyCommissionProfileVO.getProductCode();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue()) {
                KeyName = batchModifyCommissionProfileVO.getTransactionType();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                }
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_MODE_ALWD))).booleanValue()) {
                KeyName = batchModifyCommissionProfileVO.getPaymentMode();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                }
                KeyName = batchModifyCommissionProfileVO.getGrphDomainCode();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                KeyName = batchModifyCommissionProfileVO.getGradeCode();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                KeyName = batchModifyCommissionProfileVO.getCommProfileSetId();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                KeyName = batchModifyCommissionProfileVO.getCommissionProfileType() == null ? "" : batchModifyCommissionProfileVO.getCommissionProfileType();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                KeyName = batchModifyCommissionProfileVO.getSetVersion();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                KeyName = batchModifyCommissionProfileVO.getCommProfileProductID();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                KeyName = batchModifyCommissionProfileVO.getCommProfileDetailID();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);

                java.sql.Timestamp time = batchModifyCommissionProfileVO.getApplicableFrom();

                label = new Label(++col, row, BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(BTSLUtil.getUtilDateFromTimestamp(time))));
                worksheet1.addCell(label);
                int hrs = BTSLUtil.getUtilDateFromTimestamp(time).getHours();
                int min = BTSLUtil.getUtilDateFromTimestamp(time).getMinutes();
                label = new Label(++col, row, BTSLUtil.getTimeinHHMM(hrs, min));
                worksheet1.addCell(label);

                KeyName = batchModifyCommissionProfileVO.getTaxOnFOCApplicable();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                KeyName = batchModifyCommissionProfileVO.getTaxOnChannelTransfer();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);

                //Handling of decimal & non decimal allow into the system
	   	         if(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue() == 1){
	   		         long multipleOff=java.lang.Long.parseLong((batchModifyCommissionProfileVO.getTransferMultipleOffAsString()));
	   		         multipleOff=multipleOff/multiple_factor;
	   		         KeyName=String.valueOf(multipleOff);
	   	         } else {
	   	        	 double multipleOff=java.lang.Long.parseLong((batchModifyCommissionProfileVO.getTransferMultipleOffAsString()));
	   		         multipleOff=(double)multipleOff/(double)multiple_factor;
	   		         KeyName=String.valueOf(multipleOff);
	   	         }
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                double minTransfer = java.lang.Long.parseLong(batchModifyCommissionProfileVO.getMinTransferValueAsString());
                minTransfer = minTransfer / multiple_factor;
                KeyName = String.valueOf(minTransfer);
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                double maxTransfer = java.lang.Long.parseLong(batchModifyCommissionProfileVO.getMaxTransferValueAsString());
                maxTransfer = maxTransfer / multiple_factor;
                KeyName = String.valueOf(maxTransfer);

                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                double startRange = java.lang.Long.parseLong(batchModifyCommissionProfileVO.getStartRangeAsString());
                startRange = startRange / multiple_factor;
                KeyName = String.valueOf(startRange);

                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                double endRange = java.lang.Long.parseLong(batchModifyCommissionProfileVO.getEndRangeAsString());
                endRange = endRange / multiple_factor;
                KeyName = String.valueOf(endRange);

                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                KeyName = batchModifyCommissionProfileVO.getCommType();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                if (batchModifyCommissionProfileVO.getCommType().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
                    double commRate = java.lang.Double.parseDouble(batchModifyCommissionProfileVO.getCommRateAsString());
                    commRate = commRate / multiple_factor;
                    KeyName = String.valueOf(commRate);
                } else {
                    KeyName = batchModifyCommissionProfileVO.getCommRateAsString();
                }
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);

                KeyName = batchModifyCommissionProfileVO.getTax1Type();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);

                if (batchModifyCommissionProfileVO.getTax1Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
                    double tax1Rate = java.lang.Double.parseDouble(batchModifyCommissionProfileVO.getTax1RateAsString());
                    tax1Rate = tax1Rate / multiple_factor;
                    KeyName = String.valueOf(tax1Rate);
                } else {
                    KeyName = batchModifyCommissionProfileVO.getTax1RateAsString();
                }
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                KeyName = batchModifyCommissionProfileVO.getTax2Type();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);

                if (batchModifyCommissionProfileVO.getTax2Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
                    double tax2Rate = java.lang.Double.parseDouble(batchModifyCommissionProfileVO.getTax2RateAsString());
                    tax2Rate = tax2Rate / multiple_factor;
                    KeyName = String.valueOf(tax2Rate);
                } else {
                    KeyName = batchModifyCommissionProfileVO.getTax2RateAsString();
                }

                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                KeyName = batchModifyCommissionProfileVO.getTax3Type();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);

                if (batchModifyCommissionProfileVO.getTax3Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
                    double tax3Rate = java.lang.Double.parseDouble(batchModifyCommissionProfileVO.getTax3RateAsString());
                    tax3Rate = tax3Rate / multiple_factor;
                    KeyName = String.valueOf(tax3Rate);
                } else {
                    KeyName = batchModifyCommissionProfileVO.getTax3RateAsString();
                }
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                
               
                row++;
                col = 0;
                
            }

        } catch (RowsExceededException e) {
            log.errorTrace(METHOD_NAME, e);
            log.error("writeCommissionSheet", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            log.errorTrace(METHOD_NAME, e);
            log.error("writeCommissionSheet", " Exception e: " + e.getMessage());
            throw e;
        } finally {

            if (log.isDebugEnabled()) {
                log.debug("writeExcel", " Exiting");
            }
        }
    }

    private void writeAdditionalCommissionSheet(WritableSheet worksheet2, int col, int row, HashMap pHashMap, String sequenceNo) throws ParseException {
        final String methodName = "writeAdditionalCommissionSheet";
        if (log.isDebugEnabled()) {
            log.debug(methodName, " p_hashMap size=" + pHashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
        }
        try {
        	int slabItr;
            int multiplFactor = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR);
            String keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.additional.commission.slab");
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 5, row);
            worksheet2.addCell(label);
            row = row + 2;
            col = 16;
            String[] args = { (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA)) == null ? PretupsI.DATE_FORMAT : ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA))) };
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.commission");
            label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 1, row);
            worksheet2.addCell(label);
            boolean value = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOW_ROAM_ADDCOMM);
            if (value) {
                col = col + 2;
                keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.roam.commission");
                label = new Label(col, row, keyName, times16format);
                worksheet2.mergeCells(col, row, col + 1, row);
                worksheet2.addCell(label);
            }

            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue() && !"1".equals(sequenceNo)) {
            	col=col+7;
    	        keyName=pMessages.getMessage(pLocale, "profile.commissionprofiledetail.label.heading.margin.owner");
    	        label=new Label(col,row,keyName,times16format);
    	        worksheet2.mergeCells(col,row,col+1,row);
    	        worksheet2.addCell(label);
    	        col = col+2;
    	        keyName=pMessages.getMessage(pLocale, "profile.commissionprofiledetail.label.heading.taxes.owner");
    	        label=new Label(col,row,keyName,times16format);
    	        worksheet2.mergeCells(col,row,col+3,row);
    	        worksheet2.addCell(label);
    	      
            }
            col=col+3;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.taxes");
            label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 3, row);
            worksheet2.addCell(label);
            int slabLengthOTF =(Integer) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION_SLABS, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE));			
        	
            if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))){
            	
            	col=col+4;
            	keyName=pMessages.getMessage(pLocale, "commission.profile.add.additional.commission.otf.detail");
    	        label=new Label(col,row,keyName,times16format);
    	        worksheet2.mergeCells(col,row,col+3+slabLengthOTF*3,row);
    	        worksheet2.addCell(label);
            }
            row++;
            col = 0;

            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.name");
            label = new Label(col, row, keyName, times16format);
            String comment = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.name.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.setID");
            label = new Label(++col, row, keyName, times16format);
            comment = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.setID.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.serviceID");
            label = new Label(++col, row, keyName, times16format);
            comment = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.product.service.ID.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet2.addCell(label);
            //Detail ID
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.detailID");
            label = new Label(++col, row, keyName, times16format);
            comment = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.product.detail.ID.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet2.addCell(label);

            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.version");
            label = new Label(++col, row, keyName, times16format);
            comment = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.version.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.applicable.from");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batch.add.additionalcomm.profile.applicable.to");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.timeslab");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.gatewaycode");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.additional.commission.service");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            if (!BTSLUtil.isNullString(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVC_PROD_MAPPING_ALLOWED)))) {
                keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.additional.commission.subservice");
                label = new Label(++col, row, keyName, times16format);
                worksheet2.addCell(label);
            }
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.min.transfer");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.max.transfer");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.additional.commission.status");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.from.range");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.to.range");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.commission.type");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.commisison.rate");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);

            if (value) {
                keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.commission.type");
                label = new Label(++col, row, keyName, times16format);
                worksheet2.addCell(label);
                keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.commisison.rate");
                label = new Label(++col, row, keyName, times16format);
                worksheet2.addCell(label);
            }
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.additional.commission.differential.factor");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.tax1.type");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.tax1.rate");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.tax2.type");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.tax2.rate");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
	        if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue() && !"1".equals(sequenceNo)) {
	        	keyName=pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.commission.type");
	  	        label=new Label(++col,row,keyName,times16format);
	  	        worksheet2.addCell(label);
	  	        keyName=pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.commisison.rate");
	  	        label=new Label(++col,row,keyName,times16format);
	  	        worksheet2.addCell(label);
	  	        keyName=pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.tax1.type");
		        label=new Label(++col,row,keyName,times16format);
		        worksheet2.addCell(label);
		        keyName=pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.tax1.rate");
		        label=new Label(++col,row,keyName,times16format);
		        worksheet2.addCell(label);
		        keyName=pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.tax2.type");
		        label=new Label(++col,row,keyName,times16format);
		        worksheet2.addCell(label);
		        keyName=pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.tax2.rate");
		        label=new Label(++col,row,keyName,times16format);
		        worksheet2.addCell(label);

	        }
	        //for target based commission
	        if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))){
	        	keyName=pMessages.getMessage(pLocale, "commission.profile.add.additional.commission.otf.applicable.from");
		        label=new Label(++col,row,keyName,times16format);
		        comment = pMessages.getMessage(pLocale, "batch.add.comm.profile.otf.comment.from.date", args);
	            cellFeatures = new WritableCellFeatures();
	            cellFeatures.setComment(comment);
	            label.setCellFeatures(cellFeatures);
		        worksheet2.addCell(label);
		        
		        keyName=pMessages.getMessage(pLocale, "commission.profile.add.additional.commission.otf.applicable.to");
		        label=new Label(++col,row,keyName,times16format);
		        comment = pMessages.getMessage(pLocale, "batch.add.comm.profile.otf.comment.to.date",args);
	            cellFeatures = new WritableCellFeatures();
	            cellFeatures.setComment(comment);
	            label.setCellFeatures(cellFeatures);
		        worksheet2.addCell(label);
		        
		        keyName=pMessages.getMessage(pLocale, "commission.profile.add.additional.commission.otf.timeslab");
		        label=new Label(++col,row,keyName,times16format);
		        comment = pMessages.getMessage(pLocale, "batch.add.comm.profile.otf.comment.to.timeslab");
	            cellFeatures = new WritableCellFeatures();
	            cellFeatures.setComment(comment);
	            label.setCellFeatures(cellFeatures);
		        worksheet2.addCell(label);
		        keyName=pMessages.getMessage(pLocale, "commission.profile.add.additional.commission.otf.type");
		        label=new Label(++col,row,keyName,times16format);
		        comment = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.otf.type.ID.comment");
	            cellFeatures = new WritableCellFeatures();
	            cellFeatures.setComment(comment);
	            label.setCellFeatures(cellFeatures);
		        worksheet2.addCell(label);
		        for(int i=1;i<=slabLengthOTF;i++){
		        keyName=pMessages.getMessage(pLocale, "commission.profile.add.additional.commission.otf.detail.value");
		        label=new Label(++col,row,keyName+i,times16format);
		        worksheet2.addCell(label);
		        keyName=pMessages.getMessage(pLocale, "commission.profile.add.additional.commission.otf.detail.type");
		        label=new Label(++col,row,keyName+i,times16format);
		        comment = pMessages.getMessage(pLocale, "batch.add.comm.profile.otf.comment.to.type");
	            cellFeatures = new WritableCellFeatures();
	            cellFeatures.setComment(comment);
	            label.setCellFeatures(cellFeatures);
		        worksheet2.addCell(label);
		        keyName=pMessages.getMessage(pLocale, "commission.profile.add.additional.commission.otf.detail.rate");
		        label=new Label(++col,row,keyName+i,times16format);
		        worksheet2.addCell(label);
		        }
			}
            row++;
            col = 0;
            AdditionalProfileDeatilsVO additionalProfileDeatilsVO = new AdditionalProfileDeatilsVO();
            String networkCode=(String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE);
            ArrayList list = (ArrayList) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_ADDITIONAL_COMMISSION);
            List<OTFDetailsVO> listOtf=new ArrayList();
            int listSize = list.size();
            for (int i = 0; i < listSize; i++) {
                additionalProfileDeatilsVO = (AdditionalProfileDeatilsVO) list.get(i);
                keyName = additionalProfileDeatilsVO.getProfileName();
                label = new Label(col, row, keyName);
                worksheet2.addCell(label);
                keyName = additionalProfileDeatilsVO.getSetID();
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                keyName = additionalProfileDeatilsVO.getServiceID();
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                //detail ID
                keyName = additionalProfileDeatilsVO.getAddCommProfileDetailID();
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                listOtf = (ArrayList) pHashMap.get(additionalProfileDeatilsVO.getAddCommProfileDetailID()+"_"+PretupsI.COMM_TYPE_ADNLCOMM);
                keyName = additionalProfileDeatilsVO.getSetVersion();
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                keyName = BTSLDateUtil.getSystemLocaleDate(additionalProfileDeatilsVO.getApplicableFromAdditional());
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                keyName = BTSLDateUtil.getSystemLocaleDate(additionalProfileDeatilsVO.getApplicableToAdditional());
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                keyName = additionalProfileDeatilsVO.getAdditionalCommissionTimeSlab();
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                keyName = additionalProfileDeatilsVO.getGatewayCode();
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                keyName = additionalProfileDeatilsVO.getServiceType();
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                if (!BTSLUtil.isNullString(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVC_PROD_MAPPING_ALLOWED)))) {
                    if (additionalProfileDeatilsVO.getSubServiceCode() != null) {
                        keyName = additionalProfileDeatilsVO.getSubServiceCode();
                    } else {
                        keyName = "";
                    }
                    label = new Label(++col, row, keyName);
                    worksheet2.addCell(label);
                }
                double minTransfer = java.lang.Long.parseLong(additionalProfileDeatilsVO.getMinTrasferValueAsString());
                minTransfer = minTransfer / multiplFactor;
                keyName = String.valueOf(minTransfer);
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                double maxTransfer = java.lang.Long.parseLong(additionalProfileDeatilsVO.getMaxTransferValueAsString());
                maxTransfer = maxTransfer / multiplFactor;
                keyName = String.valueOf(maxTransfer);
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);

                keyName = additionalProfileDeatilsVO.getAddtnlComStatus();
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                double startRange = java.lang.Long.parseLong(additionalProfileDeatilsVO.getStartRangeAsString());
                startRange = startRange / multiplFactor;
                keyName = String.valueOf(startRange);
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                double endRange = java.lang.Long.parseLong(additionalProfileDeatilsVO.getEndRangeAsString());
                endRange = endRange / multiplFactor;
                keyName = String.valueOf(endRange);
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);

                keyName = additionalProfileDeatilsVO.getAddCommType();
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                if (additionalProfileDeatilsVO.getAddCommType().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
                    double commRate = java.lang.Double.parseDouble(additionalProfileDeatilsVO.getAddCommRateAsString());
                    commRate = commRate / multiplFactor;
                    keyName = String.valueOf(commRate);
                } else {
                    keyName = additionalProfileDeatilsVO.getAddCommRateAsString();
                }
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                if (value) {
                    if (!BTSLUtil.isNullString(additionalProfileDeatilsVO.getAddRoamCommType())) {
                        keyName = additionalProfileDeatilsVO.getAddRoamCommType();
                    } else {
                        keyName = "";
                    }
                    label = new Label(++col, row, keyName);
                    worksheet2.addCell(label);
                    if (!BTSLUtil.isNullString(additionalProfileDeatilsVO.getAddRoamCommType())) {
                        if (additionalProfileDeatilsVO.getAddRoamCommType().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
                            double roamCommRate = java.lang.Double.parseDouble(additionalProfileDeatilsVO.getAddRoamCommRateAsString());
                            roamCommRate = roamCommRate / multiplFactor;
                            keyName = String.valueOf(roamCommRate);
                        } else {

                            keyName = additionalProfileDeatilsVO.getAddRoamCommRateAsString();
                        }
                    } else {
                        keyName = "";
                    }
                    label = new Label(++col, row, keyName);
                    worksheet2.addCell(label);
                }
                keyName = additionalProfileDeatilsVO.getDiffrentialFactorAsString();
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                keyName = additionalProfileDeatilsVO.getTax1Type();
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                if (additionalProfileDeatilsVO.getTax1Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
                    double tax1Rate = java.lang.Double.parseDouble(additionalProfileDeatilsVO.getTax1RateAsString());
                    tax1Rate = tax1Rate / multiplFactor;
                    keyName = String.valueOf(tax1Rate);
                } else {
                    keyName = additionalProfileDeatilsVO.getTax1RateAsString();
                }
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                keyName = additionalProfileDeatilsVO.getTax2Type();
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);

                if (additionalProfileDeatilsVO.getTax2Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
                    double tax2Rate = java.lang.Double.parseDouble(additionalProfileDeatilsVO.getTax2RateAsString());
                    tax2Rate = tax2Rate / multiplFactor;
                    keyName = String.valueOf(tax2Rate);
                } else {
                    keyName = additionalProfileDeatilsVO.getTax2RateAsString();
                }
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
	         if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue() && !"1".equals(sequenceNo)) {
	        	 if(!BTSLUtil.isNullString(additionalProfileDeatilsVO.getAddOwnerCommType()))  {
	        		 keyName=additionalProfileDeatilsVO.getAddOwnerCommType();
	        	 } else {
	        		 keyName="";
	        	 }
	        	 label=new Label(++col,row,keyName);
	        	 worksheet2.addCell(label);
	        	 if(!BTSLUtil.isNullString(additionalProfileDeatilsVO.getAddOwnerCommType()))
	        	 {
	        		 if(additionalProfileDeatilsVO.getAddOwnerCommType().equals(PretupsI.AMOUNT_TYPE_AMOUNT))
	        		 {
	        			 double ownerCommRate=java.lang.Double.parseDouble(additionalProfileDeatilsVO.getAddOwnerCommRateAsString());
	        			 ownerCommRate=ownerCommRate/multiplFactor;
	        			 keyName=String.valueOf(ownerCommRate);
	        		 }
	        		 else
	        		 {

	        			 keyName=additionalProfileDeatilsVO.getAddOwnerCommRateAsString();
	        		 }
	        	 }
	        	 else
	        	 {
	        		 keyName=""; 
	        	 }
	        	 label=new Label(++col,row,keyName);
	        	 worksheet2.addCell(label); 


	        	 keyName=additionalProfileDeatilsVO.getOwnerTax1Type();
	        	 label=new Label(++col,row,keyName);
	        	 worksheet2.addCell(label); 
	        	 if((additionalProfileDeatilsVO.getOwnerTax1Type()!=null)&&(additionalProfileDeatilsVO.getOwnerTax1Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT)))
	        	 {
	        		 double ownerTax1Rate=java.lang.Double.parseDouble(additionalProfileDeatilsVO.getOwnerTax1RateAsString());
	        		 ownerTax1Rate=ownerTax1Rate/multiplFactor;
	        		 keyName=String.valueOf(ownerTax1Rate);
	        	 }
	        	 else
	        	 {
	        		 keyName=additionalProfileDeatilsVO.getOwnerTax1RateAsString();
	        	 }
	        	 label=new Label(++col,row,keyName);
	        	 worksheet2.addCell(label); 
	        	 keyName=additionalProfileDeatilsVO.getOwnerTax2Type();
	        	 label=new Label(++col,row,keyName);
	        	 worksheet2.addCell(label);

	        	 if((additionalProfileDeatilsVO.getOwnerTax2Type()!=null)&&(additionalProfileDeatilsVO.getOwnerTax2Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT)))
	        	 {
	        		 double  ownerTax2Rate=java.lang.Double.parseDouble(additionalProfileDeatilsVO.getOwnerTax2RateAsString());
	        		 ownerTax2Rate=ownerTax2Rate/multiplFactor;
	        		 keyName=String.valueOf(ownerTax2Rate);
	        	 }
	        	 else
	        	 {
	        		 keyName=additionalProfileDeatilsVO.getOwnerTax2RateAsString();
	        	 }
	        	 label=new Label(++col,row,keyName);
	        	 worksheet2.addCell(label); 


	         }
	         
	         if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, networkCode)){
	        	
	        	if(listOtf!=null){
	        	Iterator<OTFDetailsVO> iterator = listOtf.iterator();
	        	 OTFDetailsVO addProfDeOTFVO;
	        	 ArrayList<String> checkSame=new <String>ArrayList();
	        	slabItr=0;
	        	 while (iterator.hasNext() && slabItr<slabLengthOTF) {
	        		 addProfDeOTFVO=iterator.next();
	        		    	if(checkSame.contains(additionalProfileDeatilsVO.getAddCommProfileDetailID())){
	        		    		//value
			 	       	        keyName=addProfDeOTFVO.getOtfValue();
			 	       	        label=new Label(++col,row,keyName);
			 	       	        worksheet2.addCell(label);
			 	       	        
			 	       	        //type
			 	       	        keyName=addProfDeOTFVO.getOtfType();
				 	       	    label=new Label(++col,row,keyName);
				 	       	    worksheet2.addCell(label);
				 	       	    
				 	       	    // rate
				 	       	    keyName=addProfDeOTFVO.getOtfRate();
				 	       	    label=new Label(++col,row,keyName);
				 	       	    worksheet2.addCell(label);
	        		    	}
	        		    	else {
	        		    	//for otf appfrom
	        		    	if(addProfDeOTFVO.getOtfApplicableFrom()!=null){
	        		    	keyName=BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(addProfDeOTFVO.getOtfApplicableFrom()));
	        		    	}
	        		    	else{
	        		    		keyName="";
	        		    	}
	       	        	 	label=new Label(++col,row,keyName);
	       	        	 	worksheet2.addCell(label);
	       	        	 	
	       	        	 	//for otf app to
	       	        	 if(addProfDeOTFVO.getOtfApplicableTo()!=null){
	       	        		keyName=BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(addProfDeOTFVO.getOtfApplicableTo()));
		        		    	}
		        		    	else{
		        		    		keyName="";
		        		    	}
	       	        	 	
	 	       	        	label=new Label(++col,row,keyName);
	 	       	        	worksheet2.addCell(label);
	 	       	        	
	 	       	        	//otf timestamp
	 	       	        	keyName=addProfDeOTFVO.getOtfTimeSlab();
		 	       	        label=new Label(++col,row,keyName);
		 	       	        worksheet2.addCell(label);
	 	       	        	
	 	       	        	//otf_type amount or count
		 	       	        keyName=addProfDeOTFVO.getOtfCountOrAmount();
		 	       	        label=new Label(++col,row,keyName);
		 	       	        worksheet2.addCell(label);
		 	       	        
		 	       	        //value
		 	       	        keyName=addProfDeOTFVO.getOtfValue();
		 	       	        label=new Label(++col,row,keyName);
		 	       	        worksheet2.addCell(label);
		 	       	        
		 	       	        //type
		 	       	        keyName=addProfDeOTFVO.getOtfType();
			 	       	    label=new Label(++col,row,keyName);
			 	       	    worksheet2.addCell(label);
			 	       	    
			 	       	    // rate
			 	       	    keyName=addProfDeOTFVO.getOtfRate();
			 	       	    label=new Label(++col,row,keyName);
			 	       	    worksheet2.addCell(label);
	        		    	}
			 	       	    checkSame.add(additionalProfileDeatilsVO.getAddCommProfileDetailID());
			 	       	    slabItr++;
	        		}
	        	}//end of is empty check
	         }//end if for target commission
                row++;
                col = 0;
            }
        } catch (WriteException e) {
            log.errorTrace(methodName, e);
            log.error(methodName, " Exception e: " + e.getMessage());
        }  finally {

            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting");
            }
        }
    }

    /**
     * 
     * @param p_excelID
     * @param p_fileName
     * @return
     * @throws Exception
     */

    public String[][] readExcel(String p_excelID, String p_fileName, int fileNO) {
        final String methodName = "readExcel";
        if (log.isDebugEnabled()) {
            log.debug("readExcel", " p_excelID: " + p_excelID + " p_fileName: " + p_fileName);
        }
        String[][] strArr = null;
        Workbook workbook = null;
        Sheet excelsheet = null;
        try {
            workbook = Workbook.getWorkbook(new File(p_fileName));
            excelsheet = workbook.getSheet(fileNO);

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
            log.errorTrace(methodName, e);
            log.error("readExcel", " Exception e: " + e.getMessage());
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            workbook = null;
            excelsheet = null;
            if (log.isDebugEnabled()) {
                log.debug("readExcel", " Exiting strArr: " + strArr);
            }
        }
        return strArr;
    }

    public void writeExcelForBatchAddCommProfile(ArrayList p_dataList, HashMap p_hashMap, CommissionProfileForm p_form, MessageResources messages, Locale locale, String p_fileName, String sequenceNo) {
        final String methodName = "writeExcelForBatchAddCommProfile";
        if (log.isDebugEnabled()) {
            log.debug("writeExcelForBatchAddCommProfile", " p_locale: " + pLocale + " p_fileName: " + p_fileName);
        }
        WritableWorkbook workbook = null;
        WritableSheet worksheet1 = null;
        WritableSheet worksheet2 = null;
        WritableSheet worksheet3 = null;
        WritableSheet worksheet4 = null;
        
        int col = 0;
        int row = 0;
        pMessages = messages;
        pLocale = locale;

        try {
            int count = 1;
            workbook = Workbook.createWorkbook(new File(p_fileName));
            worksheet1 = workbook.createSheet("Commission profile sheet", count++);
            if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE)) || (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE)))
            worksheet4 = workbook.createSheet("CBC Slab sheet", count++);
            worksheet3 = workbook.createSheet("Additional comm. profile sheet", count++);
            worksheet2 = workbook.createSheet("Master Sheet", count++);
            //worksheet4 = workbook.createSheet("OTF Profile sheet", 2);

            Label label = null;

            String keyName = null;
            col = 0;
            row = 0;

            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.master.heading", (String) p_hashMap.get(PretupsI.BATCH_USR_DOMAIN_NAME), (String) p_hashMap.get(PretupsI.BATCH_USR_CATEGORY_NAME));
            label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, 8, row);
            worksheet2.addCell(label);

            row++;
            col = 0;
            row = this.writeProductListData(worksheet2, col, row, p_hashMap);
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue()){
            	row++;
            	col = 0;
            	row = this.writeTransactionType(worksheet2, col, row, p_hashMap);
            }
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_MODE_ALWD))).booleanValue()){
            	row++;
            	col = 0;
            	row = this.writePaymentModeData(worksheet2, col, row, p_hashMap);
            }
            row++;
            col = 0;
            row = this.writeDomainCategoryListData(worksheet2, col, row, p_hashMap);
            row++;
            col = 0;
            row = this.writeServiceListData(worksheet2, col, row, p_hashMap);
            row++;
            col = 0;
            if (!BTSLUtil.isNullString(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVC_PROD_MAPPING_ALLOWED)))) {
                row = this.writeSubServiceListData(worksheet2, col, row, p_hashMap);
                row++;
                col = 0;
            }

            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.commission");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);

            row++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.commission.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, col + 3, row);
            worksheet2.addCell(label);

            row++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.tax.type");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);

            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.tax.description");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);

            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.amt");
            label = new Label(col, row, keyName);
            worksheet2.addCell(label);

            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.amount");
            label = new Label(++col, row, keyName);
            worksheet2.addCell(label);

            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.pct");
            label = new Label(col, row, keyName);
            worksheet2.addCell(label);

            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.percentage");
            label = new Label(++col, row, keyName);
            worksheet2.addCell(label);
            
            //Added by Lalit//
            
            row++;
            col=0;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.dual.profile.type");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);

            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.dual.profile.type.description");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);

            final ArrayList dualCommissionTypeList = LookupsCache.loadLookupDropDown(PretupsI.DUAL_COMM_TYPE, true);
            
            for(Object vo : dualCommissionTypeList){
            	ListValueVO lv = (ListValueVO)vo;
            	row++;
                col = 0;
                label = new Label(col, row, lv.getValue());
                worksheet2.addCell(label);
                label = new Label(++col, row, lv.getLabel());
                worksheet2.addCell(label);
            }
           

            //End//

            row++;
            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.tax.cal.FOC");
            label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 3, row);
            worksheet2.addCell(label);

            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.tax.cal.FOC.description");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, col + 3, row);
            worksheet2.addCell(label);

            row++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.tax.cal.FOC.note");
            label = new Label(col, row, keyName);
            worksheet2.addCell(label);

            row++;
            row++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.tax.cal.c2c.transfer");
            label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 6, row);
            worksheet2.addCell(label);

            row++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.tax.cal.c2c.transfer.description");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, col + 6, row);
            worksheet2.addCell(label);

            row++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.tax.cal.c2c.transfer.note");
            label = new Label(col, row, keyName);
            worksheet2.addCell(label);

            row++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.commission.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, col + 3, row);
            worksheet2.addCell(label);

            row++;
            row++;
            keyName = pMessages.getMessage(pLocale, "profile.addadditionalprofile.label.timeslab");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);

            keyName = pMessages.getMessage(pLocale, "profile.addadditionalprofile.label.timeslab.example");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE)) || (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))){
            	row++;
                row++;
                col=0;
                keyName = pMessages.getMessage(pLocale, "commission.profile.add.additional.commission.otf.detail.common");
                label = new Label(col, row, keyName, times16format);
                worksheet2.mergeCells(col, row, col + 1, row);
                worksheet2.addCell(label);
                
                row++;
                keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.otf.comment.to.type");
                label = new Label(col, row, keyName);
                worksheet2.addCell(label);
                
                if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))) {
                row++;
                keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.otf.type.ID.comment");
                label = new Label(col, row, keyName);
                worksheet2.addCell(label);
                
                row++;
                keyName = pMessages.getMessage(pLocale, "batch.add.modify.master.otf.comment.date.and.time");
                label = new Label(col, row, keyName);
                worksheet2.addCell(label);
                }
                else if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))) {
                    row++;
                    keyName = pMessages.getMessage(pLocale, "base.batchAddModifyComm.profile.otf.type.ID.comment");
                    label = new Label(col, row, keyName);
                    worksheet2.addCell(label);
                    
                    row++;
                    keyName = pMessages.getMessage(pLocale, "base.batchAddModifyComm.profile.otf.date.time");
                    label = new Label(col, row, keyName);
                    worksheet2.addCell(label);
                }
                
            }
            
            col = 0;
            row = 0;
            this.writeInCommProfSheet(worksheet1, col, row, p_hashMap);
            if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE)) || (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE)))
            this.writeInCommProfOTFSheet(worksheet4, col, row, p_hashMap);
            this.writeInAddCommprofSheet(worksheet3, col, row, p_hashMap,sequenceNo);
            workbook.write();

        } catch (Exception e) {
            log.errorTrace(methodName, e);
            log.error("writeExcelForBatchAddCommProfile", " Exception e: " + e.getMessage());
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            worksheet1 = null;
            worksheet2 = null;
            worksheet3 = null;
            worksheet4 = null;
            workbook = null;

            if (log.isDebugEnabled()) {
                log.debug("writeExcelForBatchAddCommProfile", " Exiting");
            }
        }
    }

    private int writeProductListData(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) {
        final String METHOD_NAME = "writeProductListData";
        if (log.isDebugEnabled()) {
            log.debug("writeProductListData", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
        }
        try {
            String keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.product.heading");
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.product.heading.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, 10, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.productcode");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.productname");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;

            ArrayList productList = (ArrayList) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_PRODUCT_LIST);
            ListValueVO listValueVO = null;
            if (productList != null) {
                for (int i = 0, j = productList.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) productList.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(++col, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (WriteException e) {
            log.errorTrace(METHOD_NAME, e);
            log.error("writeProductListData", " Exception e: " + e.getMessage());
        } 
        return row;
    }
    
    private int writeTransactionType(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) {
        final String METHOD_NAME = "writeTransactionType";
        if (log.isDebugEnabled()) {
            log.debug("writeTransactionType", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
        }
        try {
            String keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.transaction.type");
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.transaction.type");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.transaction.description");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;

            ArrayList productList = (ArrayList) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_TRANSACTION_TYPE);
            ListValueVO listValueVO = null;
            if (productList != null) {
                for (int i = 0, j = productList.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) productList.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(++col, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (WriteException e) {
            log.errorTrace(METHOD_NAME, e);
            log.error("writeProductListData", " Exception e: " + e.getMessage());
        } 
        return row;
    }

    private int writePaymentModeData(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) {
        final String METHOD_NAME = "writeProductListData";
        if (log.isDebugEnabled()) {
            log.debug("writeProductListData", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
        }
        try {
            String keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.payment.heading");
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.paymentcode");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.paymentname");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;

            ArrayList productList = (ArrayList) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_PAYMENT_MODE);
            ListValueVO listValueVO = null;
            if (productList != null) {
                for (int i = 0, j = productList.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) productList.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(++col, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (WriteException e) {
            log.errorTrace(METHOD_NAME, e);
            log.error("writeProductListData", " Exception e: " + e.getMessage());
        } 
        return row;
    }
    
    private int writeDomainCategoryListData(WritableSheet worksheet2, int col, int row, HashMap pHashMap) {
        final String METHOD_NAME = "writeDomainCategoryListData";
        if (log.isDebugEnabled()) {
            log.debug("writeDomainCategoryListData", " p_hashMap size=" + pHashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
        }

        try {
            String keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.domain.category.heading");
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.domain.category.heading.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, 10, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.domaincode");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.domainname");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.categorycode");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.categoryname");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);

            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.grphdomaincode");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.grphdomainname");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.gradecode");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.gradename");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.gatewaycode");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.gatewayname");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);

            ArrayList geoDomainList = (ArrayList) pHashMap.get(PretupsI.BATCH_COMM_GEOGRAPHY_LIST);
            ArrayList gradeList = (ArrayList) pHashMap.get(PretupsI.BATCH_COMM_GRADE_LIST);
            ArrayList gatewayList = (ArrayList) pHashMap.get(PretupsI.BATCH_COMM_GATEWAY_LIST);
            GeographicalDomainVO geoDomainVO;
            GradeVO gradeVO;
            MessageGatewayVO msgGateVO;

            if ((PretupsI.ALL).equals((String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN_CODE))) {
                row = row + 1;
                ArrayList domainList = (ArrayList) pHashMap.get(PretupsI.BATCH_COMM_DOMAIN_LIST);
                ArrayList categoryList = (ArrayList) pHashMap.get(PretupsI.BATCH_COMM_CATEGORY_LIST);
                ListValueVO domainVO;
                CategoryVO categoryVO;
                int domainListSize = domainList.size();
                if (domainList != null && domainList.size() > 0) {
                    for (int i = 0; i < domainListSize; i++) {
                        col = 0;
                        domainVO = (ListValueVO) domainList.get(i);
                        label = new Label(col, row, domainVO.getValue());
                        worksheet2.addCell(label);
                        label = new Label(++col, row, domainVO.getLabel());
                        worksheet2.addCell(label);
                        if (categoryList != null && categoryList.size() > 0) {
                            col++;
                            int categoryListSize = categoryList.size();
                            for (int j = 0; j < categoryListSize; j++) {
                                int catCol = col;
                                categoryVO = (CategoryVO) categoryList.get(j);
                                if (categoryVO.getDomainCodeforCategory().equals(domainVO.getValue())) {
                                    label = new Label(catCol, row, categoryVO.getCategoryCode());
                                    worksheet2.addCell(label);
                                    label = new Label(++catCol, row, categoryVO.getCategoryName());
                                    worksheet2.addCell(label);
                                    row++;
                                }
                            }
                        }

                        row++;
                    }
                }
            } else {
                row++;
                col = 0;
                label = new Label(col, row, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN_CODE));
                worksheet2.addCell(label);
                label = new Label(++col, row, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN));
                worksheet2.addCell(label);
                if ((PretupsI.ALL).equals((String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY_CODE))) {
                    ArrayList categoryList = (ArrayList) pHashMap.get(PretupsI.BATCH_COMM_CATEGORY_LIST);
                    CategoryVO categoryVO;
                    if (categoryList != null && !categoryList.isEmpty()) {
                        col++;
                        int categoryListSize = categoryList.size();
                        for (int i = 0; i < categoryListSize; i++) {
                            int catCol = col;
                            categoryVO = (CategoryVO) categoryList.get(i);
                            if (categoryVO.getDomainCodeforCategory().equals((String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN_CODE))) {
                                label = new Label(catCol, row, categoryVO.getCategoryCode());
                                worksheet2.addCell(label);
                                label = new Label(++catCol, row, categoryVO.getCategoryName());
                                worksheet2.addCell(label);
                                int geoRow = row;
                                int geoDomainListSize = geoDomainList.size();
                                for (int j = 0; j < geoDomainListSize; j++) {
                                    int geoCol = catCol;
                                    geoDomainVO = (GeographicalDomainVO) geoDomainList.get(j);
                                    if (geoDomainVO.getcategoryCode().equals(categoryVO.getCategoryCode())) {
                                        label = new Label(++geoCol, geoRow, geoDomainVO.getGrphDomainCode());
                                        worksheet2.addCell(label);
                                        label = new Label(++geoCol, geoRow, geoDomainVO.getGrphDomainName());
                                        worksheet2.addCell(label);
                                        geoRow++;
                                    }
                                }
                                int grdRow = row;
                                int gradeListSize = gradeList.size();
                                for (int j = 0; j < gradeListSize; j++) {
                                    int grdCol = catCol + 2;
                                    gradeVO = (GradeVO) gradeList.get(j);
                                    if (gradeVO.getCategoryCode().equals(categoryVO.getCategoryCode())) {
                                        label = new Label(++grdCol, grdRow, gradeVO.getGradeCode());
                                        worksheet2.addCell(label);
                                        label = new Label(++grdCol, grdRow, gradeVO.getGradeName());
                                        worksheet2.addCell(label);
                                        grdRow++;
                                    }
                                }
                                int gateRow = row;
                                int gatewayListSize = gatewayList.size();
                                for (int j = 0; j < gatewayListSize; j++) {
                                    int gateCol = catCol + 4;
                                    msgGateVO = (MessageGatewayVO) gatewayList.get(j);
                                    if (msgGateVO.getCategoryCode().equals(categoryVO.getCategoryCode())) {
                                        label = new Label(++gateCol, gateRow, msgGateVO.getGatewayCode());
                                        worksheet2.addCell(label);
                                        label = new Label(++gateCol, gateRow, msgGateVO.getGatewayName());
                                        worksheet2.addCell(label);
                                        gateRow++;
                                    }
                                }
                                if (geoRow >= grdRow && geoRow >= gateRow) {
                                    row = geoRow;
                                } else if (grdRow >= gateRow) {
                                    row = grdRow;
                                } else {
                                    row = gateRow;
                                }
                            }
                        }
                    }
                } else {
                    label = new Label(++col, row, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY_CODE));
                    worksheet2.addCell(label);
                    label = new Label(++col, row, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY));
                    worksheet2.addCell(label);
                    String categoryCode = (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY_CODE);
                    int geoRow = row;
                    int geoDomainListsize = geoDomainList.size();
                    for (int j = 0; j < geoDomainListsize; j++) {
                        int geoCol = col;
                        geoDomainVO = (GeographicalDomainVO) geoDomainList.get(j);
                        if (geoDomainVO.getcategoryCode().equals(categoryCode)) {
                            label = new Label(++geoCol, geoRow, geoDomainVO.getGrphDomainCode());
                            worksheet2.addCell(label);
                            label = new Label(++geoCol, geoRow, geoDomainVO.getGrphDomainName());
                            worksheet2.addCell(label);
                            geoRow++;
                        }
                    }
                    int grdRow = row;
                    for (int j = 0; j < gradeList.size(); j++) {
                        int grdCol = col + 2;
                        gradeVO = (GradeVO) gradeList.get(j);
                        if (gradeVO.getCategoryCode().equals(categoryCode)) {
                            label = new Label(++grdCol, grdRow, gradeVO.getGradeCode());
                            worksheet2.addCell(label);
                            label = new Label(++grdCol, grdRow, gradeVO.getGradeName());
                            worksheet2.addCell(label);
                            grdRow++;
                        }
                    }
                    int gateRow = row;
                    for (int j = 0; j < gatewayList.size(); j++) {
                        int gateCol = col + 4;
                        msgGateVO = (MessageGatewayVO) gatewayList.get(j);
                        if (msgGateVO.getCategoryCode().equals(categoryCode)) {
                            label = new Label(++gateCol, gateRow, msgGateVO.getGatewayCode());
                            worksheet2.addCell(label);
                            label = new Label(++gateCol, gateRow, msgGateVO.getGatewayName());
                            worksheet2.addCell(label);
                            gateRow++;
                        }
                    }
                    if (geoRow >= grdRow && geoRow >= gateRow) {
                        row = geoRow;
                    } else if (grdRow >= gateRow) {
                        row = grdRow;
                    } else {
                        row = gateRow;
                    }

                }
            }
            return row;
        } catch (WriteException e) {
            log.errorTrace(METHOD_NAME, e);
            log.error("writeDomainCategoryListData", " Exception e: " + e.getMessage());
        } 
        return row;
    }

    private int writeServiceListData(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        final String methodName = "writeServiceListData";
        if (log.isDebugEnabled()) {
            log.debug("writeProductListData", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
        }
        try {
            String keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.service.type.heading");
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            // added by harsh
            row++;
            String keyName1 = pMessages.getMessage(pLocale, "batch.add.comm.profile.mastersheet.servicetype");
            label = new Label(col, row, keyName1, times16format);
            worksheet2.addCell(label);
            col++;
            String keyName2 = pMessages.getMessage(pLocale, "batch.add.comm.profile.mastersheet.servicename");
            label = new Label(col, row, keyName2, times16format);
            worksheet2.addCell(label);
            // end added by harsh
            row++;
            ArrayList serviceList = (ArrayList) p_hashMap.get(PretupsI.BATCH_COMM_SERVICE_LIST);
            ListValueVO listValueVO = null;
            if (serviceList != null) {
                for (int i = 0, j = serviceList.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) serviceList.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(++col, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (WriteException e) {
            log.errorTrace(methodName, e);
            log.error("writeDomainCategoryListData", " Exception e: " + e.getMessage());
            throw e;
        }

    }

    private void writeInCommProfSheet(WritableSheet worksheet1, int col, int row, HashMap p_hashMap) throws Exception {
        final String methodName = "writeInCommProfSheet";
        if (log.isDebugEnabled()) {
            log.debug("writeInCommProfSheet", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
        }
        try {
        	String[] args = { (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA)) == null ? PretupsI.DATE_FORMAT : ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA))) };
        	String keyName;
            Label label;
            // 1
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.comm.template.heading");
            label = new Label(col, row, keyName, times16format);
            worksheet1.mergeCells(col, row, col + 10, row);
            worksheet1.addCell(label);
            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.downloaded.by");
            label = new Label(col, row, keyName, times16format);
            worksheet1.addCell(label);
            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.BATCH_COMM_CREATED_BY));
            worksheet1.mergeCells(col, row, col + 1, row);
            worksheet1.addCell(label);
            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.domain");
            label = new Label(col, row, keyName, times16format);
            worksheet1.addCell(label);

            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN));
            worksheet1.mergeCells(col, row, col + 1, row);
            worksheet1.addCell(label);

            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.category");
            label = new Label(col, row, keyName, times16format);
            worksheet1.addCell(label);

            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY));
            worksheet1.mergeCells(col, row, col + 1, row);
            worksheet1.addCell(label);

            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.version");
            label = new Label(col, row, keyName, times16format);
            worksheet1.addCell(label);

            label = new Label(++col, row, "1");
            worksheet1.mergeCells(col, row, col + 1, row);
            worksheet1.addCell(label);

            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.fields.mendatory.warning");
            label = new Label(col, row, keyName, times16format);
            worksheet1.mergeCells(col, row, col + 5, row);
            worksheet1.addCell(label);

            /*------------------------  NEW BIG LINE-----------------*/
            row++;
            row++;
            col = -1;
            int count = 0;
            if ("ALL".equals((String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN_CODE))) {
                keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.domaincode");
                label = new Label(++col, row, keyName, times16format);
                worksheet1.addCell(label);
                count++;
            }
            if ("ALL".equals((String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY_CODE))) {
                keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.categorycode");
                label = new Label(++col, row, keyName, times16format);
                worksheet1.addCell(label);
                count++;
            }
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.grphdomaincode");
            label = new Label(++col, row, keyName, times16format);
            worksheet1.addCell(label);
            count++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.gradecode");
            label = new Label(++col, row, keyName, times16format);
            worksheet1.addCell(label);
            count++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.profile.name");
            label = new Label(++col, row, keyName, times16format);
            worksheet1.addCell(label);
            count++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.short.code");
            label = new Label(++col, row, keyName, times16format);
            worksheet1.addCell(label);
            count++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.dual.profile");
            label = new Label(++col, row, keyName, times16format);
            worksheet1.addCell(label);
            count++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.applicable.from");
            label = new Label(++col, row, keyName, times16format);
            // Added by Amit Raheja
            String comment_from = pMessages.getMessage(pLocale, "batch.add.comm.profile.applicable.from.comment", args);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment_from);
            label.setCellFeatures(cellFeatures);
            // Addition ends
            worksheet1.addCell(label);
            count++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.applicable.time");
            label = new Label(++col, row, keyName, times16format);
            // Added by Amit Raheja
            String comment_time = pMessages.getMessage(pLocale, "batch.add.comm.profile.applicable.time.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment_time);
            label.setCellFeatures(cellFeatures);
            // Addition ends
            worksheet1.addCell(label);
            count++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.productcode");
            label = new Label(++col, row, keyName, times16format);
            worksheet1.addCell(label);
            count++;
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue()){
            	keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.transaction.type");
            	label = new Label(++col, row, keyName, times16format);
            	worksheet1.addCell(label);
                String commentType = pMessages.getMessage(pLocale, "batch.add.comm.profile.transaction.type.comment");
                cellFeatures = new WritableCellFeatures();
                cellFeatures.setComment(commentType);
                label.setCellFeatures(cellFeatures);
                count++;
            }
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_MODE_ALWD))).booleanValue()){
            	keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.paymentcode");
            	label = new Label(++col, row, keyName, times16format);
            	worksheet1.addCell(label);
            	count++;
            }
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.multiple.of");
            label = new Label(++col, row, keyName, times16format);
            worksheet1.addCell(label);
            count++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.min.transfer.value");
            label = new Label(++col, row, keyName, times16format);
            worksheet1.addCell(label);
            count++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.max.transfer.value");
            label = new Label(++col, row, keyName, times16format);
            worksheet1.addCell(label);
            count++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.tax.cal.FOC");
            label = new Label(++col, row, keyName, times16format);
            worksheet1.addCell(label);
            count++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.tax.cal.c2c.transfer");
            label = new Label(++col, row, keyName, times16format);
            worksheet1.addCell(label);
            count++;
            row--;
            row--;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.assign.commission.slabs");
            label = new Label(col + 1, row, keyName, times16format);
            worksheet1.mergeCells(col + 1, row, col + 10, row);
            worksheet1.addCell(label);
            row++;
            row++;
            row--;
            col++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.from.range");
            label = new Label(col, row, keyName, times16format);
            worksheet1.mergeCells(col, row++, col, row);
            worksheet1.addCell(label);
            row--;
            col++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.to.range");
            label = new Label(col, row, keyName, times16format);
            worksheet1.mergeCells(col, row++, col, row);
            worksheet1.addCell(label);
            row--;
            col++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.commission");
            label = new Label(col, row, keyName, times16format);
            worksheet1.mergeCells(col++, row, col, row);
            worksheet1.addCell(label);
            row++;
            col--;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.type");
            label = new Label(col, row, keyName, times16format);
            worksheet1.mergeCells(col, row, col, row);
            worksheet1.addCell(label);
            col++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.rate");
            label = new Label(col, row, keyName, times16format);
            worksheet1.mergeCells(col, row, col, row);
            worksheet1.addCell(label);
            row--;
            col++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.taxes");
            label = new Label(col, row, keyName, times16format);
            worksheet1.mergeCells(col, row, col + 5, row);
            worksheet1.addCell(label);
            row++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.tax1.type");
            label = new Label(col, row, keyName, times16format);
            worksheet1.mergeCells(col, row, col, row);
            worksheet1.addCell(label);
            col++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.tax1.rate");
            label = new Label(col, row, keyName, times16format);
            worksheet1.mergeCells(col, row, col, row);
            worksheet1.addCell(label);
            col++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.tax2.type");
            label = new Label(col, row, keyName, times16format);
            worksheet1.mergeCells(col, row, col, row);
            worksheet1.addCell(label);
            col++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.tax2.rate");
            label = new Label(col, row, keyName, times16format);
            worksheet1.mergeCells(col, row, col, row);
            worksheet1.addCell(label);
            col++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.tax3.type");
            label = new Label(col, row, keyName, times16format);
            worksheet1.mergeCells(col, row, col, row);
            worksheet1.addCell(label);

            col++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.tax3.rate");
            label = new Label(col, row, keyName, times16format);
            worksheet1.mergeCells(col, row, col, row);
            worksheet1.addCell(label);
            
            // /setting for the vertical freeze panes
            SheetSettings sheetSetting = new SheetSettings();
            sheetSetting = worksheet1.getSettings();
            sheetSetting.setVerticalFreeze(row + 1);

            // /setting for the horizontal freeze panes
      
            SheetSettings sheetSetting1;
            sheetSetting1 = worksheet1.getSettings();
            sheetSetting1.setHorizontalFreeze(count);
        }

        catch (WriteException e) {
            log.errorTrace(methodName, e);
            log.error("writeInCommProfSheet", " Exception e: " + e.getMessage());
            throw e;
        } 
    }

    private void writeInAddCommprofSheet(WritableSheet worksheet3, int col, int row, HashMap p_hashMap, String sequenceNo) throws Exception {
        final String methodName = "writeInAddCommprofSheet";
        if (log.isDebugEnabled()) {
            log.debug("writeInAddCommprofSheet", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
        }
        try {
        	String[] args = {(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA)) == null ? PretupsI.DATE_FORMAT : ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA))) };
            String keyName;
            Label label;
            // 1
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.Additional.comm.template.heading");
            label = new Label(col, row, keyName, times16format);
            worksheet3.mergeCells(col, row, col + 10, row);
            worksheet3.addCell(label);
            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.downloaded.by");
            label = new Label(col, row, keyName, times16format);
            worksheet3.addCell(label);
            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.BATCH_COMM_CREATED_BY));
            worksheet3.mergeCells(col, row, col + 1, row);
            worksheet3.addCell(label);
            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.domain");
            label = new Label(col, row, keyName, times16format);
            worksheet3.addCell(label);

            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN));
            worksheet3.mergeCells(col, row, col + 1, row);
            worksheet3.addCell(label);

            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.category");
            label = new Label(col, row, keyName, times16format);
            worksheet3.addCell(label);

            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY));
            worksheet3.mergeCells(col, row, col + 1, row);
            worksheet3.addCell(label);

            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.version");
            label = new Label(col, row, keyName, times16format);
            worksheet3.addCell(label);

            label = new Label(++col, row, "1");
            worksheet3.mergeCells(col, row, col + 1, row);
            worksheet3.addCell(label);

            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.fields.mendatory.warning");
            label = new Label(col, row, keyName, times16format);
            worksheet3.mergeCells(col, row, col + 5, row);
            worksheet3.addCell(label);
            /*------------------------  NEW BIG LINE-----------------*/
            row++;
            row++;
            col = -1;
            int count = 0;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.profile.name");
            label = new Label(++col, row, keyName, times16format);
            worksheet3.addCell(label);
            count++;
            // Added by Amit Raheja
            String comment = pMessages.getMessage(pLocale, "batch.add.comm.profile.sync.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            // Addition ends
            String comment_add = pMessages.getMessage(pLocale, "batch.add.comm.profile.sync.comment.add");
            cellFeaturesAdd = new WritableCellFeatures();
            cellFeaturesAdd.setComment(comment_add);
            keyName = pMessages.getMessage(pLocale, "batch.add.additionalcomm.profile.applicable.from");
            label = new Label(++col, row, keyName, times16format);
            label.setCellFeatures(cellFeaturesAdd);
            worksheet3.addCell(label);
            count++;
            keyName = pMessages.getMessage(pLocale, "batch.add.additionalcomm.profile.applicable.to");
            label = new Label(++col, row, keyName, times16format);
            label.setCellFeatures(cellFeaturesAdd);
            worksheet3.addCell(label);
            count++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.timeslab");
            label = new Label(++col, row, keyName, times16format);
            worksheet3.addCell(label);
            count++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.gateway.code");
            label = new Label(++col, row, keyName, times16format);
            worksheet3.addCell(label);
            count++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.service.code");
            label = new Label(++col, row, keyName, times16format);
            worksheet3.addCell(label);
            count++;
            if (!BTSLUtil.isNullString(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVC_PROD_MAPPING_ALLOWED)))) {
                keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.subservice.code");
                label = new Label(++col, row, keyName, times16format);
                worksheet3.addCell(label);
                count++;
            }
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.min.transfer.value");
            label = new Label(++col, row, keyName, times16format);
            label.setCellFeatures(cellFeatures);
            worksheet3.addCell(label);
            count++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.max.transfer.value");
            label = new Label(++col, row, keyName, times16format);
            label.setCellFeatures(cellFeatures);
            worksheet3.addCell(label);
            count++;
            row--;
            row--;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.assign.additional.commission.slabs");
            label = new Label(col + 1, row, keyName, times16format);
            worksheet3.mergeCells(col + 1, row, col + 10, row);
            worksheet3.addCell(label);
            row++;
            row++;
            row--;
            col++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.from.range");
            label = new Label(col, row, keyName, times16format);
            worksheet3.mergeCells(col, row++, col, row);
            worksheet3.addCell(label);
            row--;
            col++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.to.range");
            label = new Label(col, row, keyName, times16format);
            worksheet3.mergeCells(col, row++, col, row);
            worksheet3.addCell(label);
            row--;
            col++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.commission");
            label = new Label(col, row, keyName, times16format);
            worksheet3.mergeCells(col++, row, col, row);
            worksheet3.addCell(label);
            boolean value = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOW_ROAM_ADDCOMM);
            if (value) {
                col = col + 1;
                keyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.roam.commission");
                label = new Label(col, row, keyName, times16format);
                worksheet3.mergeCells(col++, row, col, row);
                worksheet3.addCell(label);
            }
            row++;
            // updated by akanksha FOR ETHIOPIA TELECOM
            if (value) {
                col = col - 3;
            } else {
                col = col - 1;
            }
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.type");
            label = new Label(col, row, keyName, times16format);
            worksheet3.mergeCells(col, row, col, row);
            worksheet3.addCell(label);
            col++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.rate");
            label = new Label(col, row, keyName, times16format);
            worksheet3.mergeCells(col, row, col, row);
            worksheet3.addCell(label);

            if (value) {
                col++;
                keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.type");
                label = new Label(col, row, keyName, times16format);
                worksheet3.mergeCells(col, row, col, row);
                worksheet3.addCell(label);
                col++;
                keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.rate");
                label = new Label(col, row, keyName, times16format);
                worksheet3.mergeCells(col, row, col, row);
                worksheet3.addCell(label);
            }

            row--;
            col++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.diffrential.factor");
            label = new Label(col, row, keyName, times16format);
            worksheet3.mergeCells(col, row++, col, row);
            worksheet3.addCell(label);
            row--;
            col++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.taxes");
            label = new Label(col, row, keyName, times16format);
            worksheet3.mergeCells(col, row, col + 3, row);
            worksheet3.addCell(label);
            row++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.tax1.type");
            label = new Label(col, row, keyName, times16format);
            worksheet3.mergeCells(col, row, col, row);
            worksheet3.addCell(label);
            col++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.tax1.rate");
            label = new Label(col, row, keyName, times16format);
            worksheet3.mergeCells(col, row, col, row);
            worksheet3.addCell(label);
            col++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.tax2.type");
            label = new Label(col, row, keyName, times16format);
            worksheet3.mergeCells(col, row, col, row);
            worksheet3.addCell(label);
            col++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.tax2.rate");
            label = new Label(col, row, keyName, times16format);
            worksheet3.mergeCells(col, row, col, row);
            worksheet3.addCell(label);

			
		   if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue() && !"1".equals(sequenceNo)) {
			   	row--;
				col++;
		        keyName=pMessages.getMessage(pLocale, "profile.commissionprofiledetail.label.heading.margin.owner");
		        label=new Label(col,row,keyName,times16format);
		        worksheet3.mergeCells(col,row,col+1,row);
		        worksheet3.addCell(label);
		        col = col+2;
		        keyName=pMessages.getMessage(pLocale, "profile.commissionprofiledetail.label.heading.taxes.owner");
		        label=new Label(col,row,keyName,times16format);
		        worksheet3.mergeCells(col,row,col+3,row);
		        worksheet3.addCell(label);
	    
			   row++;
			   col =col- 3;
			   keyName=pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.commission.type");
			   label=new Label(++col,row,keyName,times16format);
			   worksheet3.addCell(label);
			   keyName=pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.commisison.rate");
			   label=new Label(++col,row,keyName,times16format);
			   worksheet3.addCell(label);
			   keyName=pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.tax1.type");
			   label=new Label(++col,row,keyName,times16format);
			   worksheet3.addCell(label);
			   keyName=pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.tax1.rate");
			   label=new Label(++col,row,keyName,times16format);
			   worksheet3.addCell(label);
			   keyName=pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.tax2.type");
			   label=new Label(++col,row,keyName,times16format);
			   worksheet3.addCell(label);
			   keyName=pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.tax2.rate");
			   label=new Label(++col,row,keyName,times16format);
			   worksheet3.addCell(label);

			   }
		   if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))){
	           
			   row--;
			   col++;
			   int slabLengthOTF =(Integer) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION_SLABS, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE));			
		        	
	           	keyName=pMessages.getMessage(pLocale, "commission.profile.add.additional.commission.otf.detail");
	   	        label=new Label(col,row,keyName,times16format);
	   	        worksheet3.mergeCells(col,row,col+3+slabLengthOTF*3,row);
	   	        worksheet3.addCell(label);
	   	        
	   	        row++;
	   	        col--;
	   	        
	   	        keyName=pMessages.getMessage(pLocale, "commission.profile.add.additional.commission.otf.applicable.from");
		        label=new Label(++col,row,keyName,times16format);
		        comment = pMessages.getMessage(pLocale, "batch.add.comm.profile.otf.comment.from.date", args);
	            cellFeatures = new WritableCellFeatures();
	            cellFeatures.setComment(comment);
	            label.setCellFeatures(cellFeatures);
		        worksheet3.addCell(label);
		        
		        keyName=pMessages.getMessage(pLocale, "commission.profile.add.additional.commission.otf.applicable.to");
		        label=new Label(++col,row,keyName,times16format);
		        comment = pMessages.getMessage(pLocale, "batch.add.comm.profile.otf.comment.to.date", args);
	            cellFeatures = new WritableCellFeatures();
	            cellFeatures.setComment(comment);
	            label.setCellFeatures(cellFeatures);
		        worksheet3.addCell(label);
		        
		        keyName=pMessages.getMessage(pLocale, "commission.profile.add.additional.commission.otf.timeslab");
		        label=new Label(++col,row,keyName,times16format);
		        comment = pMessages.getMessage(pLocale, "batch.add.comm.profile.otf.comment.to.timeslab");
	            cellFeatures = new WritableCellFeatures();
	            cellFeatures.setComment(comment);
	            label.setCellFeatures(cellFeatures);
		        worksheet3.addCell(label);
		        
		        keyName=pMessages.getMessage(pLocale, "commission.profile.add.additional.commission.otf.type");
		        label=new Label(++col,row,keyName,times16format);
		        comment = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.otf.type.ID.comment");
	            cellFeatures = new WritableCellFeatures();
	            cellFeatures.setComment(comment);
	            label.setCellFeatures(cellFeatures);
		        worksheet3.addCell(label);
		        
		        for(int i=1;i<=slabLengthOTF;i++){
		        keyName=pMessages.getMessage(pLocale, "commission.profile.add.additional.commission.otf.detail.value");
		        label=new Label(++col,row,keyName+i,times16format);
		        worksheet3.addCell(label);
		        keyName=pMessages.getMessage(pLocale, "commission.profile.add.additional.commission.otf.detail.type");
		        label=new Label(++col,row,keyName+i,times16format);
		        comment = pMessages.getMessage(pLocale, "batch.add.comm.profile.otf.comment.to.type");
	            cellFeatures = new WritableCellFeatures();
	            cellFeatures.setComment(comment);
	            label.setCellFeatures(cellFeatures);
		        worksheet3.addCell(label);
		        keyName=pMessages.getMessage(pLocale, "commission.profile.add.additional.commission.otf.detail.rate");
		        label=new Label(++col,row,keyName+i,times16format);
		        worksheet3.addCell(label);
	           }
		   }
            // /setting for the vertical freeze panes
            SheetSettings sheetSetting = new SheetSettings();
            sheetSetting = worksheet3.getSettings();
            sheetSetting.setVerticalFreeze(row + 1);

            // /setting for the horizontal freeze panes
            SheetSettings sheetSetting1 = new SheetSettings();
            sheetSetting1 = worksheet3.getSettings();
            sheetSetting1.setHorizontalFreeze(count);

        } catch (RowsExceededException e) {
            log.errorTrace(methodName, e);
            log.error("writeInAddCommprofSheet", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            log.errorTrace(methodName, e);
            log.error("writeInAddCommprofSheet", " Exception e: " + e.getMessage());
            throw e;
        }
    }

    private int writeSubServiceListData(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        final String methodName = "writeSubServiceListData";
        if (log.isDebugEnabled()) {
            log.debug("writeSubServiceListData", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
        }
        try {
            String keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.subservice.type.heading");
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.subservice.type.note");
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.mastersheet.servicetype");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.mastersheet.subservice.type");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.mastersheet.subservice.name");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            ArrayList selectorList = (ArrayList) p_hashMap.get(PretupsI.BATCH_COMM_SUBSERVICE_LIST);
            String prevSrvc = null;
            ListValueVO listValueVO;
            if (selectorList != null) {
                for (int i = 0, j = selectorList.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) selectorList.get(i);
                    if (!listValueVO.getOtherInfo().equalsIgnoreCase(prevSrvc)) {
                        label = new Label(col, row, listValueVO.getOtherInfo());
                        worksheet2.addCell(label);
                    }
                    col++;
                    prevSrvc = listValueVO.getOtherInfo();
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(++col, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (WriteException e) {
            log.errorTrace(methodName, e);
            log.error("writeDomainCategoryListData", " Exception e: " + e.getMessage());
            throw e;
        } 
    }
    
    private void writeInCommProfOTFSheet(WritableSheet worksheet4, int col, int row, HashMap p_hashMap) throws Exception {
        final String methodName = "writeInCommProfSheet";
        if (log.isDebugEnabled()) {
            log.debug("writeInCommProfOTFSheet", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
        }
        try {
        	String[] args = { (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA)) == null ? PretupsI.DATE_FORMAT : ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA))) };
        	String keyName;
            Label label;
            // 1
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.otf.comm.template.heading");
            label = new Label(col, row, keyName, times16format);
            worksheet4.mergeCells(col, row, col + 10, row);
            worksheet4.addCell(label);
            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.downloaded.by");
            label = new Label(col, row, keyName, times16format);
            worksheet4.addCell(label);
            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.BATCH_COMM_CREATED_BY));
            worksheet4.mergeCells(col, row, col + 1, row);
            worksheet4.addCell(label);
            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.domain");
            label = new Label(col, row, keyName, times16format);
            worksheet4.addCell(label);

            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN));
            worksheet4.mergeCells(col, row, col + 1, row);
            worksheet4.addCell(label);

            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.category");
            label = new Label(col, row, keyName, times16format);
            worksheet4.addCell(label);

            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY));
            worksheet4.mergeCells(col, row, col + 1, row);
            worksheet4.addCell(label);

            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.version");
            label = new Label(col, row, keyName, times16format);
            worksheet4.addCell(label);

            label = new Label(++col, row, "1");
            worksheet4.mergeCells(col, row, col + 1, row);
            worksheet4.addCell(label);

            row++;
            col = 0;
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.fields.mendatory.warning");
            label = new Label(col, row, keyName, times16format);
            worksheet4.mergeCells(col, row, col + 5, row);
            worksheet4.addCell(label);

            /*------------------------  NEW BIG LINE-----------------*/
            row++;
            row++;
            col = -1;
            int count = 0;
           
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.profile.name");
            label = new Label(++col, row, keyName, times16format);
            worksheet4.addCell(label);
            count++;

           
            keyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.productcode");
            label = new Label(++col, row, keyName, times16format);
            worksheet4.addCell(label);
            count++;
               row--;
			   col++;
			  int slabLengthOTF =(Integer) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION_SLABS, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE));			
		        	
	           	keyName=pMessages.getMessage(pLocale, "commission.profile.add.base.commission.otf.detail");
	   	        label=new Label(col,row,keyName,times16format);
	   	        worksheet4.mergeCells(col,row,col+2+slabLengthOTF*3,row);
	   	        worksheet4.addCell(label);
	   	        
	   	        row++;
	   	        col--;
	   	        
            keyName=pMessages.getMessage(pLocale, "commission.profile.add.base.commission.otf.applicable.from");
		    label=new Label(++col,row,keyName,times16format);
		    String commentOtfComm = pMessages.getMessage(pLocale, "base.batch.add.comm.profile.otf.comment.from.date", args);
	        cellFeatures = new WritableCellFeatures();
	        cellFeatures.setComment(commentOtfComm);
	        label.setCellFeatures(cellFeatures);
	         worksheet4.addCell(label);
		        
		     keyName=pMessages.getMessage(pLocale, "commission.profile.add.base.commission.otf.applicable.to");
		     label=new Label(++col,row,keyName,times16format);
		     commentOtfComm = pMessages.getMessage(pLocale, "base.batch.add.comm.profile.otf.comment.to.date",args);
	         cellFeatures = new WritableCellFeatures();
	         cellFeatures.setComment(commentOtfComm);
	         label.setCellFeatures(cellFeatures);
	         worksheet4.addCell(label);
		        
		     keyName=pMessages.getMessage(pLocale, "commission.profile.add.base.commission.otf.timeslab");
		     label=new Label(++col,row,keyName,times16format);
		     commentOtfComm = pMessages.getMessage(pLocale, "base.batch.add.comm.profile.otf.comment.to.timeslab");
	         cellFeatures = new WritableCellFeatures();
	         cellFeatures.setComment(commentOtfComm);
	         label.setCellFeatures(cellFeatures);
	         worksheet4.addCell(label);
	         
	         for(int i=1;i<=slabLengthOTF;i++){
	 		        keyName=pMessages.getMessage(pLocale, "commission.profile.add.additional.commission.otf.detail.value");
	 		        label=new Label(++col,row,keyName+i,times16format);
	 		       worksheet4.addCell(label);
	 		        keyName=pMessages.getMessage(pLocale, "commission.profile.add.additional.commission.otf.detail.type");
	 		        label=new Label(++col,row,keyName+i,times16format);
	 		        commentOtfComm = pMessages.getMessage(pLocale, "batch.add.comm.profile.otf.comment.to.type");
	 	            cellFeatures = new WritableCellFeatures();
	 	            cellFeatures.setComment(commentOtfComm);
	 	            label.setCellFeatures(cellFeatures);
	 	           worksheet4.addCell(label);
	 		        keyName=pMessages.getMessage(pLocale, "commission.profile.add.additional.commission.otf.detail.rate");
	 		        label=new Label(++col,row,keyName+i,times16format);
	 		       worksheet4.addCell(label);
	 	           }
	         
            // /setting for the vertical freeze panes
            SheetSettings sheetSetting = new SheetSettings();
            sheetSetting = worksheet4.getSettings();
            sheetSetting.setVerticalFreeze(row + 1);

            // /setting for the horizontal freeze panes
            SheetSettings sheetSetting1;
            sheetSetting1 = worksheet4.getSettings();
            sheetSetting1.setHorizontalFreeze(count);
        }

        catch (WriteException e) {
            log.errorTrace(methodName, e);
            log.error("writeInCommProfOTFSheet", " Exception e: " + e.getMessage());
            throw e;
        } 
    }
    
    private void writeOTFSheetForModify(WritableSheet worksheet4, int col, int row, HashMap pHashMap) throws Exception {
    	final String METHOD_NAME = "writeOTFSheetForModify";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, " p_hashMap size=" + pHashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
        }
        
        try {
            int slabLengthOTF=0;
            int slabItr;

            String KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.otf.profile.template");
            Label label = new Label(col, row, KeyName, times16format);
            worksheet4.mergeCells(col, row, col + 10, row);
            worksheet4.addCell(label);
            row++;
            col = 0;
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.downloadedby");
            
            label = new Label(col, row, KeyName, times16format);
            worksheet4.mergeCells(col, row, col + 1, row);
            worksheet4.addCell(label);
            col = col + 2;
            KeyName = (String) pHashMap.get(PretupsI.DOWNLOADED_BY);
            label = new Label(col, row, KeyName, times16format);
            worksheet4.mergeCells(col, row, col + 2, row);
            worksheet4.addCell(label);
            row++;
            col = 0;
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.network");
            label = new Label(col, row, KeyName, times16format);
            worksheet4.addCell(label);
            KeyName = (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_NAME);
            label = new Label(++col, row, KeyName);
            worksheet4.addCell(label);
            row++;
            col = 0;
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.mastersheet.domainCode");
            label = new Label(col, row, KeyName, times16format);
            worksheet4.addCell(label);
            KeyName = (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN_CODE);
            label = new Label(++col, row, KeyName);
            worksheet4.addCell(label);
            row++;
            col = 0;
            KeyName = pMessages.getMessage(pLocale, "batchusercreation.mastersheet.categoryName");
            label = new Label(col, row, KeyName, times16format);
            worksheet4.addCell(label);
            KeyName = (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY);
            label = new Label(++col, row, KeyName);
            worksheet4.addCell(label);
            row++;
            col = 0;
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.star");
            label = new Label(++col, row, KeyName);
            worksheet4.mergeCells(col, row, col + 5, row);
            worksheet4.addCell(label);

            row++;
            col = 0;
            
            if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))){
            	slabLengthOTF =(Integer) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION_SLABS, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE));			
            	col = col + 7;
            	KeyName=pMessages.getMessage(pLocale, "commission.profile.add.base.commission.otf.detail");
    	        label=new Label(col,row,KeyName,times16format);
    	        worksheet4.mergeCells(col,row,col+2+slabLengthOTF*3,row);
    	        worksheet4.addCell(label);
            }
            
            row++;
            col = 0;
            
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.name");
            label = new Label(col, row, KeyName, times16format);
            worksheet4.addCell(label);



            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.mastersheet.productCode");
            label = new Label(++col, row, KeyName, times16format);
            worksheet4.addCell(label);
            
			KeyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.grphdomaincode");
            label = new Label(++col, row, KeyName, times16format);
            String comment = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.geographycode.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet4.addCell(label);

            KeyName = pMessages.getMessage(pLocale, "batch.add.comm.profile.gradecode");
            label = new Label(++col, row, KeyName, times16format);
            comment = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.gradecode.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet4.addCell(label);

            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.setID");
            label = new Label(++col, row, KeyName, times16format);
            comment = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.setID.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet4.addCell(label);
            
            
            KeyName = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.version");
            label = new Label(++col, row, KeyName, times16format);
            comment = pMessages.getMessage(pLocale, "batchModifyCommProfile.commission.profile.version.comment");
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet4.addCell(label);
            
            
       
            
            String[] args = { (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA)) == null ? PretupsI.DATE_FORMAT : ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA))) };
            if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))){
            	 KeyName=pMessages.getMessage(pLocale, "commission.profile.add.base.commission.otf.applicable.from");
  		        label=new Label(++col,row,KeyName,times16format);
  		        String commentOtfComm = pMessages.getMessage(pLocale, "base.batch.add.comm.profile.otf.comment.from.date",args);
  	            cellFeatures = new WritableCellFeatures();
  	            cellFeatures.setComment(commentOtfComm);
  	            label.setCellFeatures(cellFeatures);
  	            worksheet4.addCell(label);
  		        
  	           KeyName=pMessages.getMessage(pLocale, "commission.profile.add.base.commission.otf.applicable.to");
  		        label=new Label(++col,row,KeyName,times16format);
  		        commentOtfComm = pMessages.getMessage(pLocale, "base.batch.add.comm.profile.otf.comment.to.date",args);
  	            cellFeatures = new WritableCellFeatures();
  	            cellFeatures.setComment(commentOtfComm);
  	            label.setCellFeatures(cellFeatures);
  	            worksheet4.addCell(label);
  		        
  	           KeyName=pMessages.getMessage(pLocale, "commission.profile.add.base.commission.otf.timeslab");
  		        label=new Label(++col,row,KeyName,times16format);
  		        commentOtfComm = pMessages.getMessage(pLocale, "base.batch.add.comm.profile.otf.comment.to.timeslab");
  	            cellFeatures = new WritableCellFeatures();
  	            cellFeatures.setComment(commentOtfComm);
  	            label.setCellFeatures(cellFeatures);
  	            worksheet4.addCell(label);
  		       
  		        for(int i=1;i<=slabLengthOTF;i++){
  		        KeyName=pMessages.getMessage(pLocale, "commission.profile.add.additional.commission.otf.detail.value");
  		        label=new Label(++col,row,KeyName+i,times16format);
  		        worksheet4.addCell(label);
  		        KeyName=pMessages.getMessage(pLocale, "commission.profile.add.additional.commission.otf.detail.type");
  		        label=new Label(++col,row,KeyName+i,times16format);
  		        commentOtfComm = pMessages.getMessage(pLocale, "batch.add.comm.profile.otf.comment.to.type");
  	            cellFeatures = new WritableCellFeatures();
  	            cellFeatures.setComment(commentOtfComm);
  	            label.setCellFeatures(cellFeatures);
  	            worksheet4.addCell(label);
  	            KeyName=pMessages.getMessage(pLocale, "commission.profile.add.additional.commission.otf.detail.rate");
  		        label=new Label(++col,row,KeyName+i,times16format);
  		        worksheet4.addCell(label);
  	           
            }
            }
            
            col = 0;
            row++;
            
            BatchModifyCommissionProfileVO comProfVO;
            String networkCode=(String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE);
            ArrayList list = (ArrayList) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_SET_NAME);
            List<OTFDetailsVO> listBaseOtf = null;
            int listSize = list.size();
            HashMap hm = new HashMap();
            for (int i = 0; i < listSize; i++) {
            	comProfVO = (BatchModifyCommissionProfileVO) list.get(i);
            	listBaseOtf = (ArrayList) pHashMap.get(comProfVO.getCommProfileSetId()+"_"+comProfVO.getSetVersion()+"_"+comProfVO.getProductCode()+"_"+PretupsI.COMM_TYPE_BASECOMM);
            	if(listBaseOtf == null )
            		continue;
            	if(hm.get(comProfVO.getCommProfileSetId()+":"+comProfVO.getSetVersion()+":"+comProfVO.getProductCode()) != null)
            		continue;
            	else
            		hm.put(comProfVO.getCommProfileSetId()+":"+comProfVO.getSetVersion()+":"+comProfVO.getProductCode(),comProfVO);
            		
            	KeyName = comProfVO.getCommProfileSetName();
                label = new Label(col, row, KeyName);
                worksheet4.addCell(label);

                KeyName = comProfVO.getProductCode();
                label = new Label(++col, row, KeyName);
                worksheet4.addCell(label);
               
                    KeyName = comProfVO.getGrphDomainCode();
                    label = new Label(++col, row, KeyName);
                    worksheet4.addCell(label);
                    KeyName = comProfVO.getGradeCode();
                    label = new Label(++col, row, KeyName);
                    worksheet4.addCell(label);
                    KeyName = comProfVO.getCommProfileSetId();
                    label = new Label(++col, row, KeyName);
                    worksheet4.addCell(label);
                    
                    KeyName = comProfVO.getSetVersion();
                    label = new Label(++col, row, KeyName);
                    worksheet4.addCell(label);
                   
                    if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION, networkCode)){
        	        	if(listBaseOtf!=null){
        	        	Iterator<OTFDetailsVO> iterator = listBaseOtf.iterator();
        	        	 OTFDetailsVO baseCommProfDeOTFVO;
        	        	 ArrayList<String> checkSame=new <String>ArrayList();
        	        	 slabItr=0;
        	        	 while (iterator.hasNext() && slabItr<slabLengthOTF) {
        	        		 baseCommProfDeOTFVO=iterator.next();
        	        		    	if(checkSame.contains(comProfVO.getCommProfileDetailID())){
        	        		    		//value
        			 	       	        KeyName=baseCommProfDeOTFVO.getOtfValue();
        			 	       	        label=new Label(++col,row,KeyName);
        			 	       	        worksheet4.addCell(label);
        			 	       	        
        			 	       	        //type
        			 	       	        KeyName=baseCommProfDeOTFVO.getOtfType();
        				 	       	    label=new Label(++col,row,KeyName);
        				 	       	    worksheet4.addCell(label);
        				 	       	    
        				 	       	    // rate
        				 	       	    KeyName=baseCommProfDeOTFVO.getOtfRate();
        				 	       	    label=new Label(++col,row,KeyName);
        				 	       	    worksheet4.addCell(label);
        	        		    	}
        	        		    	else {
        	        		    	//for otf appfrom
        	        		    	if(baseCommProfDeOTFVO.getOtfApplicableFrom()!=null){
        	        		    	KeyName=BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(baseCommProfDeOTFVO.getOtfApplicableFrom()));
        	        		    	}
        	        		    	else{
        	        		    		KeyName="";
        	        		    	}
        	       	        	 	label=new Label(++col,row,KeyName);
        	       	        	 	worksheet4.addCell(label);
        	       	        	 	
        	       	        	 	//for otf app to
        	       	        	 if(baseCommProfDeOTFVO.getOtfApplicableTo()!=null){
        	       	        		KeyName=BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(baseCommProfDeOTFVO.getOtfApplicableTo()));
        		        		    	}
        		        		    	else{
        		        		    		KeyName="";
        		        		    	}
        	       	        	 	
        	 	       	        	label=new Label(++col,row,KeyName);
        	 	       	        	worksheet4.addCell(label);
        	 	       	        	
        	 	       	        	//otf timestamp
        	 	       	        	KeyName=baseCommProfDeOTFVO.getOtfTimeSlab();
        		 	       	        label=new Label(++col,row,KeyName);
        		 	       	        worksheet4.addCell(label);
        	 	       	        	
        		 	       	        //value
        		 	       	        KeyName=baseCommProfDeOTFVO.getOtfValue();
        		 	       	        label=new Label(++col,row,KeyName);
        		 	       	        worksheet4.addCell(label);
        		 	       	        
        		 	       	        //type
        		 	       	        KeyName=baseCommProfDeOTFVO.getOtfType();
        			 	       	    label=new Label(++col,row,KeyName);
        			 	       	    worksheet4.addCell(label);
        			 	       	    
        			 	       	    // rate
        			 	       	    KeyName=baseCommProfDeOTFVO.getOtfRate();
        			 	       	    label=new Label(++col,row,KeyName);
        			 	       	    worksheet4.addCell(label);
        	        		    	}
        			 	       	    checkSame.add(comProfVO.getCommProfileDetailID());
        			 	       	slabItr++;
        	        		}
        	        	}//end of is empty check
        	         }//end if for target based base commission
                    row++;
                    col = 0;
            }
        }
        catch (RowsExceededException e) {
            log.errorTrace(METHOD_NAME, e);
            log.error("writeCommissionSheet", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            log.errorTrace(METHOD_NAME, e);
            log.error("writeCommissionSheet", " Exception e: " + e.getMessage());
            throw e;
        } finally {

            if (log.isDebugEnabled()) {
                log.debug("writeExcel", " Exiting");
            }
    }
    }

    /**
     *
     * @param pExcelID
     * @param pHashMap
     * @param locale
     * @param pFileName
     * @throws Exception
     */
    public void writeExcelForBatchModifyCommissionProfile(String pExcelID, HashMap pHashMap, Locale locale, String pFileName, String sequenceNo) {
        final String methodName = "writeExcelForBatchModifyCommissionProfile";
        if (log.isDebugEnabled()) {
            log.debug("writeExcelForBatchModifyCommissionProfile", " p_excelID: " + pExcelID + " p_hashMap:" + pHashMap + " locale: " + locale + " fileName: " + pFileName);
        }
        WritableWorkbook workbook = null;
        WritableSheet worksheet1 = null;
        WritableSheet worksheet2 = null;
        WritableSheet worksheet3 = null;
        WritableSheet worksheet4 = null;

        int col = 0;
        int row = 0;
        int count = 0;
        try {
            workbook = Workbook.createWorkbook(new File(pFileName));
            worksheet1 = workbook.createSheet("commission profile Sheet", count++);
            if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE)))
                worksheet4 = workbook.createSheet("CBC Slab Sheet", count++);
            worksheet2 = workbook.createSheet("Master Sheet", count++);
            worksheet3 = workbook.createSheet("additional comm profile sheet", count++);
            pLocale = locale;
            Label label = null;
            String keyName = null;
            String arr[] = {(String) pHashMap.get(PretupsI.USR_DEF_CONFIG_DOMAIN_NAME), pHashMap.get(PretupsI.BATCH_USR_CATEGORY_NAME).toString()};
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_MASTERSHEET_HEADING, arr);
            label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, COLUMN_MARGIN, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            row = this.writeProductNameForBatchModifyCommissionProfile(worksheet2, col, row, pHashMap);
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue()){
                row++;
                col = 0;
                row = this.writeTransactionTypeForBatchModifyCommissionProfile(worksheet2, col, row, pHashMap);
            }
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_MODE_ALWD))).booleanValue()){
                row++;
                col = 0;
                row = this.writePaymentModeDataForBatchModifyCommissionProfile(worksheet2, col, row, pHashMap);
            }
            row++;
            col = 0;
            row = this.writeDoaminNameForBatchModifyCommissionProfile(worksheet2, col, row, pHashMap);
            row++;
            col = 0;
            row = this.writeCategoryNameForBatchModifyCommissionProfile(worksheet2, col, row, pHashMap);
            row++;
            col = 0;
            row = this.writeServiceListDataForModifyForBatchModifyCommissionProfile(worksheet2, col, row, pHashMap);
            row++;
            col = 0;
            if (!BTSLUtil.isNullString(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVC_PROD_MAPPING_ALLOWED)))) {
                row = this.writeSubServiceListDataForModifyForBatchModifyCommissionProfile(worksheet2, col, row, pHashMap);
                row++;
                col = 0;
            }
            ++row;
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_MASTERSHEET_COMMISSION_HEADING, arr);
            label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_MASTERSHEET_COMMISSION_NOTE, arr);
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGIN, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            ++row;
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_MASTERSHEET_TAX_TYPE, arr);
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHUSERCREATION_MASTERSHEET_TAX_DESCRIPTION, arr);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_MASTERSHEET_AMT, arr);
            label = new Label(col, row, keyName);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHUSERCREATION_MASTERSHEET_AMOUNT, arr);
            label = new Label(++col, row, keyName);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_MASTERSHEET_PCT, arr);
            label = new Label(col, row, keyName);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHUSERCREATION_MASTERSHEET_PERCENTAGE, arr);
            label = new Label(++col, row, keyName);
            worksheet2.addCell(label);

            //Added by Lalit

            row++;
            col = 0;
            ++row;
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DUAL_PROFILE_TYPE, arr);
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DUAL_PROFILE_TYPE_DESCRIPTION, arr);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);

            final ArrayList dualCommissionTypeList = LookupsCache.loadLookupDropDown(PretupsI.DUAL_COMM_TYPE, true);

            for(Object vo : dualCommissionTypeList){
                ListValueVO lv = (ListValueVO)vo;
                row++;
                col = 0;
                label = new Label(col, row, lv.getValue());
                worksheet2.addCell(label);
                label = new Label(++col, row, lv.getLabel());
                worksheet2.addCell(label);
            }



            row++;
            col = 0;
            ++row;
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_MASTERSHEET_TAX_ON_FOC, arr);
            label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, COLUMN_MARGIN, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_MASTERSHEET_TAX_VALUE, arr);
            label = new Label(col, row, keyName);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_MASTERSHEET_TAX_ON_C2C, arr);
            label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, COLUMN_MARGIN, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_MASTERSHEET_TAX_VALUE, arr);
            label = new Label(col, row, keyName);
            worksheet2.addCell(label);
            if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE)) || (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))){
                row++;
                row++;
                col=0;
                keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL_COMMON, arr);
                label = new Label(col, row, keyName, times16format);
                worksheet2.mergeCells(col, row, col + 1, row);
                worksheet2.addCell(label);

                row++;
                keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_OTF_COMMENT_TO_TYPE, arr);
                label = new Label(col, row, keyName);
                worksheet2.addCell(label);

                if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))) {
                    row++;
                    keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_OTF_TYPE_ID_COMMENT, arr);
                    label = new Label(col, row, keyName);
                    worksheet2.addCell(label);

                    row++;
                    keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_MODIFY_MASTER_OTF_COMMENT_DATE_AND_TIME, arr);
                    label = new Label(col, row, keyName);
                    worksheet2.addCell(label);
                }
                else if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))) {
                    row++;
                    keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_OTF_TYPE_ID_COMMENT, arr);
                    label = new Label(col, row, keyName);
                    worksheet2.addCell(label);

                    row++;
                    keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BASE_BATCHADDMODIFYCOMM_PROFILE_OTF_DATE_TIME, arr);
                    label = new Label(col, row, keyName);
                    worksheet2.addCell(label);
                }

            }
            row = 0;
            col = 0;
            this.writeCommissionSheetForBatchModifyCommissionProfile(worksheet1, col, row, pHashMap);
            if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))){
                row = 0;
                col = 0;
                this.writeOTFSheetForModifyForBatchModifyCommissionProfile(worksheet4, col, row, pHashMap);
            }
            row = 0;
            col = 0;
            this.writeAdditionalCommissionSheetForBatchModifyCommissionProfile(worksheet3, col, row, pHashMap,sequenceNo);

            workbook.write();

        } catch (Exception e) {
            log.errorTrace(methodName, e);
            log.error("writeExcel", " Exception e: " + e.getMessage());
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            worksheet1 = null;
            worksheet2 = null;
            workbook = null;
            if (log.isDebugEnabled()) {
                log.debug("writeExcel", " Exiting");
            }
        }
    }

    /**
     * this method is used to write product code and product name in master data
     * sheet
     *
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */
    public int writeProductNameForBatchModifyCommissionProfile(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) {
        final String METHOD_NAME = "writeProductNameForBatchModifyCommissionProfile";
        if (log.isDebugEnabled()) {
            log.debug("writeProductNameForBatchModifyCommissionProfile", " hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
        }
        try {
            ++row;
            String keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_MASTERSHEET_PRODUCT_HEADING, null);
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_MASTERSHEET_PRODUCT_NOTE, null);
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGIN, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_MASTERSHEET_PRODUCTCODE, null);
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHUSERCREATION_MASTERSHEET_PRODUCTNAME, null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;
            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_PRODUCT_LIST);
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
            log.errorTrace(METHOD_NAME, e);
            log.error("writeUserPrifix", " Exception e: " + e.getMessage());
        } catch (WriteException e) {
            log.errorTrace(METHOD_NAME, e);
            log.error("writeUserPrifix", " Exception e: " + e.getMessage());
        }
        return row;
    }

    /**
     *
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */

    public int writeDoaminNameForBatchModifyCommissionProfile(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) {
        final String METHOD_NAME = "writeDoaminName";
        if (log.isDebugEnabled()) {
            log.debug("write domain name", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
        }
        try {
            String keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_MASTERSHEET_DOMAINCODE, null);
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_MASTERSHEET_DOMAIN_NOTE, null);
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGIN, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_MASTERSHEET_DOMAINCODE, null);
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHUSERCREATION_MASTERSHEET_DOMAINNAME, null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName = (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN_CODE);
            label = new Label(col, row, keyName);
            worksheet2.addCell(label);
            keyName = (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN);
            label = new Label(++col, row, keyName);
            worksheet2.addCell(label);
            return row;

        } catch (RowsExceededException e) {
            log.errorTrace(METHOD_NAME, e);
            log.error("writeDomainName", " Exception e: " + e.getMessage());
        } catch (WriteException e) {
            log.errorTrace(METHOD_NAME, e);
            log.error("writeDomainName", " Exception e: " + e.getMessage());
        }
        return row;
    }

    /**
     *
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return
     * @throws Exception
     */

    public int writeCategoryNameForBatchModifyCommissionProfile(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) {
        final String methodName = "writeCategoryNameForBatchModifyCommissionProfile";
        if (log.isDebugEnabled()) {
            log.debug("write category name", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
        }
        String categoryCode = null;
        MessageGatewayVO msgGateVO = null;
        try {
            ++row;
            String keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_MASTERSHEET_CATEGORYCODE, null);
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_MASTERSHEET_CATEGORY_NOTE, null);
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, COLUMN_MARGIN, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_MASTERSHEET_CATEGORYCODE, null);
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHUSERCREATION_MASTERSHEET_CATEGORYNAME, null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_GATEWAYCODE, null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_GATEWAYNAME, null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            col = 0;
            ArrayList gatewayList = (ArrayList) p_hashMap.get(PretupsI.BATCH_COMM_GATEWAY_LIST);

            keyName = (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY_CODE);
            label = new Label(col, row, keyName);
            worksheet2.addCell(label);
            categoryCode = keyName;
            keyName = (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY);
            label = new Label(++col, row, keyName);
            worksheet2.addCell(label);
            int gatewayListSize = gatewayList.size();
            for (int j = 0; j < gatewayListSize; j++) {
                int gateCol = col;
                msgGateVO = (MessageGatewayVO) gatewayList.get(j);
                if (msgGateVO.getCategoryCode().equals(categoryCode)) {
                    label = new Label(++gateCol, row, msgGateVO.getGatewayCode());
                    worksheet2.addCell(label);
                    label = new Label(++gateCol, row, msgGateVO.getGatewayName());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;

        } catch (WriteException e) {
            log.errorTrace(methodName, e);
            log.error("writeDomainName", " Exception e: " + e.getMessage());
        }
        return row;
    }

    /**
     *
     * @param worksheet1
     * @param col
     * @param row
     * @param pHashMap
     * @throws Exception
     */

    private void writeCommissionSheetForBatchModifyCommissionProfile(WritableSheet worksheet1, int col, int row, HashMap pHashMap) throws Exception {
        final String METHOD_NAME = "writeCommissionSheetForBatchModifyCommissionProfile";
        if (log.isDebugEnabled()) {
            log.debug("writeCommissionSheetForBatchModifyCommissionProfile", " p_hashMap size=" + pHashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
        }
        try {

            int multiple_factor = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR);
            int slabLengthOTF=0;
            int slabItr;
            String KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TEMPLATE, null);
            Label label = new Label(col, row, KeyName, times16format);
            worksheet1.mergeCells(col, row, col + 10, row);
            worksheet1.addCell(label);
            row++;
            col = 0;
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_DOWNLOADEDBY, null);

            label = new Label(col, row, KeyName, times16format);
            worksheet1.mergeCells(col, row, col + 1, row);
            worksheet1.addCell(label);
            col = col + 2;
            KeyName = (String) pHashMap.get(PretupsI.DOWNLOADED_BY);
            label = new Label(col, row, KeyName, times16format);
            worksheet1.mergeCells(col, row, col + 2, row);
            worksheet1.addCell(label);
            row++;
            col = 0;
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_NETWORK, null);
            label = new Label(col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName = (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_NAME);
            label = new Label(++col, row, KeyName);
            worksheet1.addCell(label);
            row++;
            col = 0;
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_MASTERSHEET_DOMAINCODE, null);
            label = new Label(col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName = (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN_CODE);
            label = new Label(++col, row, KeyName);
            worksheet1.addCell(label);
            row++;
            col = 0;
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHUSERCREATION_MASTERSHEET_CATEGORYNAME, null);
            label = new Label(col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName = (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY);
            label = new Label(++col, row, KeyName);
            worksheet1.addCell(label);
            row++;
            col = 0;
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.ALL_FIELDS_MARKED_WITH_ARE_MANDATORY, null);
            label = new Label(++col, row, KeyName);
            worksheet1.mergeCells(col, row, col + 5, row);
            worksheet1.addCell(label);

            row++;
            col = 0;
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_COMMISSION_SLAB, null);
            col = col + 9;
            label = new Label(col, row, KeyName, times16format);
            worksheet1.mergeCells(col, row, col + 8, row);
            worksheet1.addCell(label);
            col = col + 9;
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_COMMISSION, null);
            label = new Label(col, row, KeyName, times16format);
            worksheet1.mergeCells(col, row, col + 1, row);
            worksheet1.addCell(label);

            col=col+2;
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAXES, null);
            label = new Label(col, row, KeyName, times16format);
            worksheet1.mergeCells(col, row, col + 5, row);
            worksheet1.addCell(label);

            row++;
            col = 0;
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_NAME, null);
            label = new Label(col, row, KeyName, times16format);
            worksheet1.addCell(label);

            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_SHORT_CODE, null);
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);

            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_MASTERSHEET_PRODUCTCODE, null);
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);

            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue()) {
                KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TRANSACTION_TYPE, null);
                label = new Label(++col, row, KeyName, times16format);
                worksheet1.addCell(label);
                String commentType = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TRANSACTION_TYPE_COMMENT, null);
                cellFeatures = new WritableCellFeatures();
                cellFeatures.setComment(commentType);
                label.setCellFeatures(cellFeatures);
            }
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_MODE_ALWD))).booleanValue()) {
                KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PAYMENTCODE, null);
                label = new Label(++col, row, KeyName, times16format);
                worksheet1.addCell(label);
            }
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_GRPHDOMAINCODE, null);
            label = new Label(++col, row, KeyName, times16format);
            String comment = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_GEOGRAPHYCODE_COMMENT, null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_GRADECODE, null);
            label = new Label(++col, row, KeyName, times16format);
            comment = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_GRADECODE_COMMENT, null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_SETID, null);
            label = new Label(++col, row, KeyName, times16format);
            comment= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_SETID_COMMENT, null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DUAL_PROFILE, null);
            label = new Label(++col, row, KeyName, times16format);
            cellFeatures = new WritableCellFeatures();
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);

            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_VERSION, null);
            label = new Label(++col, row, KeyName, times16format);
            comment = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_VERSION_COMMENT, null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_PRODUCT_ID, null);
            label = new Label(++col, row, KeyName, times16format);
            comment = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_PRODUCT_ID_COMMENT, null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);
            //detail id
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BASE_BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_DETAILID, null);
            label = new Label(++col, row, KeyName, times16format);
            comment = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BASE_BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_PRODUCT_DETAIL_ID_COMMENT, null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_ADDITIONAL_COMMISSION_PROFILE_APPLICABLE_FROM, null);
            label = new Label(++col, row, KeyName, times16format);
            //Bug Fix
            comment = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.PROFILE_COMMISSIONPROFILEDETAILVIEW_LABEL_APPLICABLEFROMFORMAT, null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_APPLICABLE_TIME, null);
            label = new Label(++col, row, KeyName, times16format);
            //
            comment = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.PROFILE_COMMISSIONPROFILEDETAILVIEW_LABEL_APPLICABLEFROMHOURFORMAT, null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet1.addCell(label);
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAXON_FOC, null);
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAXON_C2C, null);
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_MULTIPLE_OF, null);
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_MIN_TRANSFER, null);
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_MAX_TRANSFER, null);
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_FROM_RANGE, null);
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TO_RANGE, null);
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_COMMISSION_TYPE, null);
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_COMMISISON_RATE, null);
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX1_TYPE, null);
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX1_RATE, null);
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX2_TYPE, null);
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX2_RATE, null);
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX3_TYPE, null);
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX3_RATE, null);
            label = new Label(++col, row, KeyName, times16format);
            worksheet1.addCell(label);
            col = 0;
            row++;
            BatchModifyCommissionProfileVO batchModifyCommissionProfileVO;
            ArrayList list = (ArrayList) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_SET_NAME);
            int listSize = list.size();
            for (int i = 0; i < listSize; i++) {
                batchModifyCommissionProfileVO = (BatchModifyCommissionProfileVO) list.get(i);

                KeyName = batchModifyCommissionProfileVO.getCommProfileSetName();
                label = new Label(col, row, KeyName);
                worksheet1.addCell(label);
                KeyName = batchModifyCommissionProfileVO.getShortCode();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                KeyName = batchModifyCommissionProfileVO.getProductCode();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue()) {
                    KeyName = batchModifyCommissionProfileVO.getTransactionType();
                    label = new Label(++col, row, KeyName);
                    worksheet1.addCell(label);
                }
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_MODE_ALWD))).booleanValue()) {
                    KeyName = batchModifyCommissionProfileVO.getPaymentMode();
                    label = new Label(++col, row, KeyName);
                    worksheet1.addCell(label);
                }
                KeyName = batchModifyCommissionProfileVO.getGrphDomainCode();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                KeyName = batchModifyCommissionProfileVO.getGradeCode();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                KeyName = batchModifyCommissionProfileVO.getCommProfileSetId();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                KeyName = batchModifyCommissionProfileVO.getCommissionProfileType() == null ? "" : batchModifyCommissionProfileVO.getCommissionProfileType();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                KeyName = batchModifyCommissionProfileVO.getSetVersion();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                KeyName = batchModifyCommissionProfileVO.getCommProfileProductID();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                KeyName = batchModifyCommissionProfileVO.getCommProfileDetailID();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);

                java.sql.Timestamp time = batchModifyCommissionProfileVO.getApplicableFrom();

                label = new Label(++col, row, BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(BTSLUtil.getUtilDateFromTimestamp(time))));
                worksheet1.addCell(label);
                int hrs = BTSLUtil.getUtilDateFromTimestamp(time).getHours();
                int min = BTSLUtil.getUtilDateFromTimestamp(time).getMinutes();
                label = new Label(++col, row, BTSLUtil.getTimeinHHMM(hrs, min));
                worksheet1.addCell(label);

                KeyName = batchModifyCommissionProfileVO.getTaxOnFOCApplicable();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                KeyName = batchModifyCommissionProfileVO.getTaxOnChannelTransfer();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);

                //Handling of decimal & non decimal allow into the system
                if(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue() == 1){
                    long multipleOff=java.lang.Long.parseLong((batchModifyCommissionProfileVO.getTransferMultipleOffAsString()));
                    multipleOff=multipleOff/multiple_factor;
                    KeyName=String.valueOf(multipleOff);
                } else {
                    double multipleOff=java.lang.Long.parseLong((batchModifyCommissionProfileVO.getTransferMultipleOffAsString()));
                    multipleOff=(double)multipleOff/(double)multiple_factor;
                    KeyName=String.valueOf(multipleOff);
                }
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                double minTransfer = java.lang.Long.parseLong(batchModifyCommissionProfileVO.getMinTransferValueAsString());
                minTransfer = minTransfer / multiple_factor;
                KeyName = String.valueOf(minTransfer);
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                double maxTransfer = java.lang.Long.parseLong(batchModifyCommissionProfileVO.getMaxTransferValueAsString());
                maxTransfer = maxTransfer / multiple_factor;
                KeyName = String.valueOf(maxTransfer);

                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                double startRange = java.lang.Long.parseLong(batchModifyCommissionProfileVO.getStartRangeAsString());
                startRange = startRange / multiple_factor;
                KeyName = String.valueOf(startRange);

                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                double endRange = java.lang.Long.parseLong(batchModifyCommissionProfileVO.getEndRangeAsString());
                endRange = endRange / multiple_factor;
                KeyName = String.valueOf(endRange);

                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                KeyName = batchModifyCommissionProfileVO.getCommType();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                if (batchModifyCommissionProfileVO.getCommType().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
                    double commRate = java.lang.Double.parseDouble(batchModifyCommissionProfileVO.getCommRateAsString());
                    commRate = commRate / multiple_factor;
                    KeyName = String.valueOf(commRate);
                } else {
                    KeyName = batchModifyCommissionProfileVO.getCommRateAsString();
                }
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);

                KeyName = batchModifyCommissionProfileVO.getTax1Type();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);

                if (batchModifyCommissionProfileVO.getTax1Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
                    double tax1Rate = java.lang.Double.parseDouble(batchModifyCommissionProfileVO.getTax1RateAsString());
                    tax1Rate = tax1Rate / multiple_factor;
                    KeyName = String.valueOf(tax1Rate);
                } else {
                    KeyName = batchModifyCommissionProfileVO.getTax1RateAsString();
                }
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                KeyName = batchModifyCommissionProfileVO.getTax2Type();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);

                if (batchModifyCommissionProfileVO.getTax2Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
                    double tax2Rate = java.lang.Double.parseDouble(batchModifyCommissionProfileVO.getTax2RateAsString());
                    tax2Rate = tax2Rate / multiple_factor;
                    KeyName = String.valueOf(tax2Rate);
                } else {
                    KeyName = batchModifyCommissionProfileVO.getTax2RateAsString();
                }

                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);
                KeyName = batchModifyCommissionProfileVO.getTax3Type();
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);

                if (batchModifyCommissionProfileVO.getTax3Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
                    double tax3Rate = java.lang.Double.parseDouble(batchModifyCommissionProfileVO.getTax3RateAsString());
                    tax3Rate = tax3Rate / multiple_factor;
                    KeyName = String.valueOf(tax3Rate);
                } else {
                    KeyName = batchModifyCommissionProfileVO.getTax3RateAsString();
                }
                label = new Label(++col, row, KeyName);
                worksheet1.addCell(label);


                row++;
                col = 0;

            }

        } catch (RowsExceededException e) {
            log.errorTrace(METHOD_NAME, e);
            log.error("writeCommissionSheet", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            log.errorTrace(METHOD_NAME, e);
            log.error("writeCommissionSheet", " Exception e: " + e.getMessage());
            throw e;
        } finally {

            if (log.isDebugEnabled()) {
                log.debug("writeExcel", " Exiting");
            }
        }
    }

    private int writeTransactionTypeForBatchModifyCommissionProfile(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) {
        final String METHOD_NAME = "writeTransactionType";
        if (log.isDebugEnabled()) {
            log.debug("writeTransactionType", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
        }
        try {
            String keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TRANSACTION_TYPE, null);
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TRANSACTION_TYPE, null);
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TRANSACTION_DESCRIPTION, null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;

            ArrayList productList = (ArrayList) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_TRANSACTION_TYPE);
            ListValueVO listValueVO = null;
            if (productList != null) {
                for (int i = 0, j = productList.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) productList.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(++col, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (WriteException e) {
            log.errorTrace(METHOD_NAME, e);
            log.error("writeProductListData", " Exception e: " + e.getMessage());
        }
        return row;
    }

    private int writePaymentModeDataForBatchModifyCommissionProfile(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) {
        final String METHOD_NAME = "writePaymentModeDataForBatchModifyCommissionProfile";
        if (log.isDebugEnabled()) {
            log.debug("writePaymentModeDataForBatchModifyCommissionProfile", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
        }
        try {
            String keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PAYMENT_HEADING, null);
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PAYMENTCODE, null);
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PAYMENTNAME, null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;

            ArrayList productList = (ArrayList) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_PAYMENT_MODE);
            ListValueVO listValueVO = null;
            if (productList != null) {
                for (int i = 0, j = productList.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) productList.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(++col, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (WriteException e) {
            log.errorTrace(METHOD_NAME, e);
            log.error("writeProductListData", " Exception e: " + e.getMessage());
        }
        return row;
    }

    private void writeAdditionalCommissionSheetForBatchModifyCommissionProfile(WritableSheet worksheet2, int col, int row, HashMap pHashMap, String sequenceNo) throws ParseException {
        final String methodName = "writeAdditionalCommissionSheetForBatchModifyCommissionProfile";
        if (log.isDebugEnabled()) {
            log.debug(methodName, " p_hashMap size=" + pHashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
        }
        try {
            int slabItr;
            int multiplFactor = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR);
            String keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_ADDITIONAL_COMMISSION_SLAB, null);
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 5, row);
            worksheet2.addCell(label);
            row = row + 2;
            col = 16;
            String[] args = { (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA)) == null ? PretupsI.DATE_FORMAT : ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA))) };
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_COMMISSION, null);
            label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 1, row);
            worksheet2.addCell(label);
            boolean value = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOW_ROAM_ADDCOMM);
            if (value) {
                col = col + 2;
                keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_ROAM_COMMISSION, null);
                label = new Label(col, row, keyName, times16format);
                worksheet2.mergeCells(col, row, col + 1, row);
                worksheet2.addCell(label);
            }

            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue() && !"1".equals(sequenceNo)) {
                col=col+7;
                keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.PROFILE_COMMISSIONPROFILEDETAIL_LABEL_HEADING_MARGIN_OWNER, null);
                label=new Label(col,row,keyName,times16format);
                worksheet2.mergeCells(col,row,col+1,row);
                worksheet2.addCell(label);
                col = col+2;
                keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.PROFILE_COMMISSIONPROFILEDETAIL_LABEL_HEADING_TAXES_OWNER, null);
                label=new Label(col,row,keyName,times16format);
                worksheet2.mergeCells(col,row,col+3,row);
                worksheet2.addCell(label);

            }
            col=col+3;
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAXES, null);
            label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 3, row);
            worksheet2.addCell(label);
            int slabLengthOTF =(Integer) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION_SLABS, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE));

            if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))){

                col=col+4;
                keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL, null);
                label=new Label(col,row,keyName,times16format);
                worksheet2.mergeCells(col,row,col+3+slabLengthOTF*3,row);
                worksheet2.addCell(label);
            }
            row++;
            col = 0;

            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_NAME, null);
            label = new Label(col, row, keyName, times16format);
            String comment = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_NAME_COMMENT, null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_SETID, null);
            label = new Label(++col, row, keyName, times16format);
            comment = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_SETID_COMMENT, null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_SERVICEID, null);
            label = new Label(++col, row, keyName, times16format);

            comment = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_PRODUCT_SERVICE_ID_COMMENT, null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet2.addCell(label);
            //Detail ID
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BASE_BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_DETAILID, null);
            label = new Label(++col, row, keyName, times16format);
            comment = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BASE_BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_PRODUCT_DETAIL_ID_COMMENT, null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet2.addCell(label);

            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_VERSION, null);
            label = new Label(++col, row, keyName, times16format);
            comment = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_VERSION_COMMENT, null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_ADDITIONAL_COMMISSION_PROFILE_APPLICABLE_FROM, null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_ADDITIONALCOMM_PROFILE_APPLICABLE_TO, null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TIMESLAB, null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_GATEWAYCODE, null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_ADDITIONAL_COMMISSION_SERVICE, null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            if (!BTSLUtil.isNullString(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVC_PROD_MAPPING_ALLOWED)))) {
                keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_ADDITIONAL_COMMISSION_SUBSERVICE, null);
                label = new Label(++col, row, keyName, times16format);
                worksheet2.addCell(label);
            }
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_MIN_TRANSFER, null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_MAX_TRANSFER, null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_ADDITIONAL_COMMISSION_STATUS, null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_FROM_RANGE, null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TO_RANGE, null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_COMMISSION_TYPE, null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_COMMISISON_RATE, null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);

            if (value) {
                keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_COMMISSION_TYPE, null);
                label = new Label(++col, row, keyName, times16format);
                worksheet2.addCell(label);
                keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_COMMISISON_RATE, null);
                label = new Label(++col, row, keyName, times16format);
                worksheet2.addCell(label);
            }
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_ADDITIONAL_COMMISSION_DIFFERENTIAL_FACTOR, null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX1_TYPE, null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX1_RATE, null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX2_TYPE, null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX2_RATE, null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue() && !"1".equals(sequenceNo)) {
                keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_COMMISSION_TYPE, null);
                label=new Label(++col,row,keyName,times16format);
                worksheet2.addCell(label);
                keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_COMMISISON_RATE, null);
                label=new Label(++col,row,keyName,times16format);
                worksheet2.addCell(label);
                keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX1_TYPE, null);
                label=new Label(++col,row,keyName,times16format);
                worksheet2.addCell(label);
                keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX1_RATE, null);
                label=new Label(++col,row,keyName,times16format);
                worksheet2.addCell(label);
                keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX2_TYPE, null);
                label=new Label(++col,row,keyName,times16format);
                worksheet2.addCell(label);
                keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX2_RATE, null);
                label=new Label(++col,row,keyName,times16format);
                worksheet2.addCell(label);

            }
            //for target based commission
            if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))){
                keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_APPLICABLE_FROM, null);
                label=new Label(++col,row,keyName,times16format);
                String[] arg = { (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA)) == null ? PretupsI.DATE_FORMAT : ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA))) };
                comment = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_OTF_COMMENT_FROM_DATE, arg);
                cellFeatures = new WritableCellFeatures();
                cellFeatures.setComment(comment);
                label.setCellFeatures(cellFeatures);
                worksheet2.addCell(label);

                keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_APPLICABLE_TO, null);
                label=new Label(++col,row,keyName,times16format);
                comment = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_OTF_COMMENT_TO_DATE, arg);
                cellFeatures = new WritableCellFeatures();
                cellFeatures.setComment(comment);
                label.setCellFeatures(cellFeatures);
                worksheet2.addCell(label);

                keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_TIMESLAB, null);
                label=new Label(++col,row,keyName,times16format);
                comment = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_OTF_COMMENT_TO_TIMESLAB, null);
                cellFeatures = new WritableCellFeatures();
                cellFeatures.setComment(comment);
                label.setCellFeatures(cellFeatures);
                worksheet2.addCell(label);
                keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_TYPE, null);
                label=new Label(++col,row,keyName,times16format);
                comment = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_OTF_TYPE_ID_COMMENT, null);
                cellFeatures = new WritableCellFeatures();
                cellFeatures.setComment(comment);
                label.setCellFeatures(cellFeatures);
                worksheet2.addCell(label);
                for(int i=1;i<=slabLengthOTF;i++){
                    keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL_VALUE, null);
                    label=new Label(++col,row,keyName+i,times16format);
                    worksheet2.addCell(label);
                    keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL_TYPE, null);
                    label=new Label(++col,row,keyName+i,times16format);
                    comment = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_OTF_COMMENT_TO_TYPE, null);
                    cellFeatures = new WritableCellFeatures();
                    cellFeatures.setComment(comment);
                    label.setCellFeatures(cellFeatures);
                    worksheet2.addCell(label);
                    keyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL_RATE, null);
                    label=new Label(++col,row,keyName+i,times16format);
                    worksheet2.addCell(label);
                }
            }
            row++;
            col = 0;
            AdditionalProfileDeatilsVO additionalProfileDeatilsVO = new AdditionalProfileDeatilsVO();
            String networkCode=(String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE);
            ArrayList list = (ArrayList) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_ADDITIONAL_COMMISSION);
            List<OTFDetailsVO> listOtf=new ArrayList();
            int listSize = list.size();
            for (int i = 0; i < listSize; i++) {
                additionalProfileDeatilsVO = (AdditionalProfileDeatilsVO) list.get(i);
                keyName = additionalProfileDeatilsVO.getProfileName();
                label = new Label(col, row, keyName);
                worksheet2.addCell(label);
                keyName = additionalProfileDeatilsVO.getSetID();
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                keyName = additionalProfileDeatilsVO.getServiceID();
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                //detail ID
                keyName = additionalProfileDeatilsVO.getAddCommProfileDetailID();
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                listOtf = (ArrayList) pHashMap.get(additionalProfileDeatilsVO.getAddCommProfileDetailID()+"_"+PretupsI.COMM_TYPE_ADNLCOMM);
                keyName = additionalProfileDeatilsVO.getSetVersion();
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                keyName = BTSLDateUtil.getSystemLocaleDate(additionalProfileDeatilsVO.getApplicableFromAdditional());
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                keyName = BTSLDateUtil.getSystemLocaleDate(additionalProfileDeatilsVO.getApplicableToAdditional());
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                keyName = additionalProfileDeatilsVO.getAdditionalCommissionTimeSlab();
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                keyName = additionalProfileDeatilsVO.getGatewayCode();
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                keyName = additionalProfileDeatilsVO.getServiceType();
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                if (!BTSLUtil.isNullString(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVC_PROD_MAPPING_ALLOWED)))) {
                    if (additionalProfileDeatilsVO.getSubServiceCode() != null) {
                        keyName = additionalProfileDeatilsVO.getSubServiceCode();
                    } else {
                        keyName = "";
                    }
                    label = new Label(++col, row, keyName);
                    worksheet2.addCell(label);
                }
                double minTransfer = java.lang.Long.parseLong(additionalProfileDeatilsVO.getMinTrasferValueAsString());
                minTransfer = minTransfer / multiplFactor;
                keyName = String.valueOf(minTransfer);
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                double maxTransfer = java.lang.Long.parseLong(additionalProfileDeatilsVO.getMaxTransferValueAsString());
                maxTransfer = maxTransfer / multiplFactor;
                keyName = String.valueOf(maxTransfer);
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);

                keyName = additionalProfileDeatilsVO.getAddtnlComStatus();
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                double startRange = java.lang.Long.parseLong(additionalProfileDeatilsVO.getStartRangeAsString());
                startRange = startRange / multiplFactor;
                keyName = String.valueOf(startRange);
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                double endRange = java.lang.Long.parseLong(additionalProfileDeatilsVO.getEndRangeAsString());
                endRange = endRange / multiplFactor;
                keyName = String.valueOf(endRange);
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);

                keyName = additionalProfileDeatilsVO.getAddCommType();
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                if (additionalProfileDeatilsVO.getAddCommType().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
                    double commRate = java.lang.Double.parseDouble(additionalProfileDeatilsVO.getAddCommRateAsString());
                    commRate = commRate / multiplFactor;
                    keyName = String.valueOf(commRate);
                } else {
                    keyName = additionalProfileDeatilsVO.getAddCommRateAsString();
                }
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                if (value) {
                    if (!BTSLUtil.isNullString(additionalProfileDeatilsVO.getAddRoamCommType())) {
                        keyName = additionalProfileDeatilsVO.getAddRoamCommType();
                    } else {
                        keyName = "";
                    }
                    label = new Label(++col, row, keyName);
                    worksheet2.addCell(label);
                    if (!BTSLUtil.isNullString(additionalProfileDeatilsVO.getAddRoamCommType())) {
                        if (additionalProfileDeatilsVO.getAddRoamCommType().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
                            double roamCommRate = java.lang.Double.parseDouble(additionalProfileDeatilsVO.getAddRoamCommRateAsString());
                            roamCommRate = roamCommRate / multiplFactor;
                            keyName = String.valueOf(roamCommRate);
                        } else {

                            keyName = additionalProfileDeatilsVO.getAddRoamCommRateAsString();
                        }
                    } else {
                        keyName = "";
                    }
                    label = new Label(++col, row, keyName);
                    worksheet2.addCell(label);
                }
                keyName = additionalProfileDeatilsVO.getDiffrentialFactorAsString();
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                keyName = additionalProfileDeatilsVO.getTax1Type();
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                if (additionalProfileDeatilsVO.getTax1Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
                    double tax1Rate = java.lang.Double.parseDouble(additionalProfileDeatilsVO.getTax1RateAsString());
                    tax1Rate = tax1Rate / multiplFactor;
                    keyName = String.valueOf(tax1Rate);
                } else {
                    keyName = additionalProfileDeatilsVO.getTax1RateAsString();
                }
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                keyName = additionalProfileDeatilsVO.getTax2Type();
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);

                if (additionalProfileDeatilsVO.getTax2Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT)) {
                    double tax2Rate = java.lang.Double.parseDouble(additionalProfileDeatilsVO.getTax2RateAsString());
                    tax2Rate = tax2Rate / multiplFactor;
                    keyName = String.valueOf(tax2Rate);
                } else {
                    keyName = additionalProfileDeatilsVO.getTax2RateAsString();
                }
                label = new Label(++col, row, keyName);
                worksheet2.addCell(label);
                if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue() && !"1".equals(sequenceNo)) {
                    if(!BTSLUtil.isNullString(additionalProfileDeatilsVO.getAddOwnerCommType()))  {
                        keyName=additionalProfileDeatilsVO.getAddOwnerCommType();
                    } else {
                        keyName="";
                    }
                    label=new Label(++col,row,keyName);
                    worksheet2.addCell(label);
                    if(!BTSLUtil.isNullString(additionalProfileDeatilsVO.getAddOwnerCommType()))
                    {
                        if(additionalProfileDeatilsVO.getAddOwnerCommType().equals(PretupsI.AMOUNT_TYPE_AMOUNT))
                        {
                            double ownerCommRate=java.lang.Double.parseDouble(additionalProfileDeatilsVO.getAddOwnerCommRateAsString());
                            ownerCommRate=ownerCommRate/multiplFactor;
                            keyName=String.valueOf(ownerCommRate);
                        }
                        else
                        {

                            keyName=additionalProfileDeatilsVO.getAddOwnerCommRateAsString();
                        }
                    }
                    else
                    {
                        keyName="";
                    }
                    label=new Label(++col,row,keyName);
                    worksheet2.addCell(label);


                    keyName=additionalProfileDeatilsVO.getOwnerTax1Type();
                    label=new Label(++col,row,keyName);
                    worksheet2.addCell(label);
                    if((additionalProfileDeatilsVO.getOwnerTax1Type()!=null)&&(additionalProfileDeatilsVO.getOwnerTax1Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT)))
                    {
                        double ownerTax1Rate=java.lang.Double.parseDouble(additionalProfileDeatilsVO.getOwnerTax1RateAsString());
                        ownerTax1Rate=ownerTax1Rate/multiplFactor;
                        keyName=String.valueOf(ownerTax1Rate);
                    }
                    else
                    {
                        keyName=additionalProfileDeatilsVO.getOwnerTax1RateAsString();
                    }
                    label=new Label(++col,row,keyName);
                    worksheet2.addCell(label);
                    keyName=additionalProfileDeatilsVO.getOwnerTax2Type();
                    label=new Label(++col,row,keyName);
                    worksheet2.addCell(label);

                    if((additionalProfileDeatilsVO.getOwnerTax2Type()!=null)&&(additionalProfileDeatilsVO.getOwnerTax2Type().equals(PretupsI.AMOUNT_TYPE_AMOUNT)))
                    {
                        double  ownerTax2Rate=java.lang.Double.parseDouble(additionalProfileDeatilsVO.getOwnerTax2RateAsString());
                        ownerTax2Rate=ownerTax2Rate/multiplFactor;
                        keyName=String.valueOf(ownerTax2Rate);
                    }
                    else
                    {
                        keyName=additionalProfileDeatilsVO.getOwnerTax2RateAsString();
                    }
                    label=new Label(++col,row,keyName);
                    worksheet2.addCell(label);


                }

                if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, networkCode)){

                    if(listOtf!=null){
                        Iterator<OTFDetailsVO> iterator = listOtf.iterator();
                        OTFDetailsVO addProfDeOTFVO;
                        ArrayList<String> checkSame=new <String>ArrayList();
                        slabItr=0;
                        while (iterator.hasNext() && slabItr<slabLengthOTF) {
                            addProfDeOTFVO=iterator.next();
                            if(checkSame.contains(additionalProfileDeatilsVO.getAddCommProfileDetailID())){
                                //value
                                keyName=addProfDeOTFVO.getOtfValue();
                                label=new Label(++col,row,keyName);
                                worksheet2.addCell(label);

                                //type
                                keyName=addProfDeOTFVO.getOtfType();
                                label=new Label(++col,row,keyName);
                                worksheet2.addCell(label);

                                // rate
                                keyName=addProfDeOTFVO.getOtfRate();
                                label=new Label(++col,row,keyName);
                                worksheet2.addCell(label);
                            }
                            else {
                                //for otf appfrom
                                if(addProfDeOTFVO.getOtfApplicableFrom()!=null){
                                    keyName=BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(addProfDeOTFVO.getOtfApplicableFrom()));
                                }
                                else{
                                    keyName="";
                                }
                                label=new Label(++col,row,keyName);
                                worksheet2.addCell(label);

                                //for otf app to
                                if(addProfDeOTFVO.getOtfApplicableTo()!=null){
                                    keyName=BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(addProfDeOTFVO.getOtfApplicableTo()));
                                }
                                else{
                                    keyName="";
                                }

                                label=new Label(++col,row,keyName);
                                worksheet2.addCell(label);

                                //otf timestamp
                                keyName=addProfDeOTFVO.getOtfTimeSlab();
                                label=new Label(++col,row,keyName);
                                worksheet2.addCell(label);

                                //otf_type amount or count
                                keyName=addProfDeOTFVO.getOtfCountOrAmount();
                                label=new Label(++col,row,keyName);
                                worksheet2.addCell(label);

                                //value
                                keyName=addProfDeOTFVO.getOtfValue();
                                label=new Label(++col,row,keyName);
                                worksheet2.addCell(label);

                                //type
                                keyName=addProfDeOTFVO.getOtfType();
                                label=new Label(++col,row,keyName);
                                worksheet2.addCell(label);

                                // rate
                                keyName=addProfDeOTFVO.getOtfRate();
                                label=new Label(++col,row,keyName);
                                worksheet2.addCell(label);
                            }
                            checkSame.add(additionalProfileDeatilsVO.getAddCommProfileDetailID());
                            slabItr++;
                        }
                    }//end of is empty check
                }//end if for target commission
                row++;
                col = 0;
            }
        } catch (WriteException e) {
            log.errorTrace(methodName, e);
            log.error(methodName, " Exception e: " + e.getMessage());
        }  finally {

            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting");
            }
        }
    }

    private void writeOTFSheetForModifyForBatchModifyCommissionProfile(WritableSheet worksheet4, int col, int row, HashMap pHashMap) throws Exception {
        final String METHOD_NAME = "writeOTFSheetForModifyForBatchModifyCommissionProfile";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, " p_hashMap size=" + pHashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
        }

        try {
            int slabLengthOTF=0;
            int slabItr;

            String KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_OTF_PROFILE_TEMPLATE, null);
            Label label = new Label(col, row, KeyName, times16format);
            worksheet4.mergeCells(col, row, col + 10, row);
            worksheet4.addCell(label);
            row++;
            col = 0;
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_DOWNLOADEDBY, null);

            label = new Label(col, row, KeyName, times16format);
            worksheet4.mergeCells(col, row, col + 1, row);
            worksheet4.addCell(label);
            col = col + 2;
            KeyName = (String) pHashMap.get(PretupsI.DOWNLOADED_BY);
            label = new Label(col, row, KeyName, times16format);
            worksheet4.mergeCells(col, row, col + 2, row);
            worksheet4.addCell(label);
            row++;
            col = 0;
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_NETWORK, null);
            label = new Label(col, row, KeyName, times16format);
            worksheet4.addCell(label);
            KeyName = (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_NAME);
            label = new Label(++col, row, KeyName);
            worksheet4.addCell(label);
            row++;
            col = 0;
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_MASTERSHEET_DOMAINCODE, null);
            label = new Label(col, row, KeyName, times16format);
            worksheet4.addCell(label);
            KeyName = (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN_CODE);
            label = new Label(++col, row, KeyName);
            worksheet4.addCell(label);
            row++;
            col = 0;
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHUSERCREATION_MASTERSHEET_CATEGORYNAME, null);
            label = new Label(col, row, KeyName, times16format);
            worksheet4.addCell(label);
            KeyName = (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY);
            label = new Label(++col, row, KeyName);
            worksheet4.addCell(label);
            row++;
            col = 0;
            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.ALL_FIELDS_MARKED_WITH_ARE_MANDATORY, null);
            label = new Label(++col, row, KeyName);
            worksheet4.mergeCells(col, row, col + 5, row);
            worksheet4.addCell(label);

            row++;
            col = 0;

            if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))){
                slabLengthOTF =(Integer) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION_SLABS, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE));
                col = col + 7;
                KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_BASE_COMMISSION_OTF_DETAIL, null);
                label=new Label(col,row,KeyName,times16format);
                worksheet4.mergeCells(col,row,col+2+slabLengthOTF*3,row);
                worksheet4.addCell(label);
            }

            row++;
            col = 0;

            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_NAME, null);
            label = new Label(col, row, KeyName, times16format);
            worksheet4.addCell(label);


            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_MASTERSHEET_PRODUCTCODE, null);
            label = new Label(++col, row, KeyName, times16format);
            worksheet4.addCell(label);

            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_GRPHDOMAINCODE, null);
            label = new Label(++col, row, KeyName, times16format);
            String comment = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_GEOGRAPHYCODE_COMMENT, null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet4.addCell(label);

            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_GRADECODE, null);
            label = new Label(++col, row, KeyName, times16format);
            comment = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_GRADECODE_COMMENT, null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet4.addCell(label);

            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_SETID, null);
            label = new Label(++col, row, KeyName, times16format);
            comment = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_SETID_COMMENT, null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet4.addCell(label);

            KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_VERSION, null);
            label = new Label(++col, row, KeyName, times16format);
            comment = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_VERSION_COMMENT, null);
            cellFeatures = new WritableCellFeatures();
            cellFeatures.setComment(comment);
            label.setCellFeatures(cellFeatures);
            worksheet4.addCell(label);




            String[] args = { (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA)) == null ? PretupsI.DATE_FORMAT : ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA))) };
            if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))){
                KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_BASE_COMMISSION_OTF_APPLICABLE_FROM, null);
                label=new Label(++col,row,KeyName,times16format);
                String[] arg = { (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA)) == null ? PretupsI.DATE_FORMAT : ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA))) };
                String commentOtfComm = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_OTF_COMMENT_FROM_DATE, arg);
                cellFeatures = new WritableCellFeatures();
                cellFeatures.setComment(commentOtfComm);
                label.setCellFeatures(cellFeatures);
                worksheet4.addCell(label);

                KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_BASE_COMMISSION_OTF_APPLICABLE_TO, null);
                label=new Label(++col,row,KeyName,times16format);
                commentOtfComm = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_OTF_COMMENT_TO_DATE, arg);
                cellFeatures = new WritableCellFeatures();
                cellFeatures.setComment(commentOtfComm);
                label.setCellFeatures(cellFeatures);
                worksheet4.addCell(label);

                KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_BASE_COMMISSION_OTF_TIMESLAB, null);
                label=new Label(++col,row,KeyName,times16format);
                commentOtfComm = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BASE_BATCH_ADD_COMM_PROFILE_OTF_COMMENT_TO_TIMESLAB, null);
                cellFeatures = new WritableCellFeatures();
                cellFeatures.setComment(commentOtfComm);
                label.setCellFeatures(cellFeatures);
                worksheet4.addCell(label);

                for(int i=1;i<=slabLengthOTF;i++){
                    KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL_VALUE, null);
                    label=new Label(++col,row,KeyName+i,times16format);
                    worksheet4.addCell(label);
                    KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL_TYPE, null);
                    label=new Label(++col,row,KeyName+i,times16format);
                    commentOtfComm = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_OTF_COMMENT_TO_TYPE, null);
                    cellFeatures = new WritableCellFeatures();
                    cellFeatures.setComment(commentOtfComm);
                    label.setCellFeatures(cellFeatures);
                    worksheet4.addCell(label);
                    KeyName= RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL_RATE, null);
                    label=new Label(++col,row,KeyName+i,times16format);
                    worksheet4.addCell(label);

                }
            }

            col = 0;
            row++;

            BatchModifyCommissionProfileVO comProfVO;
            String networkCode=(String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE);
            ArrayList list = (ArrayList) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_SET_NAME);
            List<OTFDetailsVO> listBaseOtf = null;
            int listSize = list.size();
            HashMap hm = new HashMap();
            for (int i = 0; i < listSize; i++) {
                comProfVO = (BatchModifyCommissionProfileVO) list.get(i);
                listBaseOtf = (ArrayList) pHashMap.get(comProfVO.getCommProfileSetId()+"_"+comProfVO.getSetVersion()+"_"+comProfVO.getProductCode()+"_"+PretupsI.COMM_TYPE_BASECOMM);
                if(listBaseOtf == null )
                    continue;
                if(hm.get(comProfVO.getCommProfileSetId()+":"+comProfVO.getSetVersion()+":"+comProfVO.getProductCode()) != null)
                    continue;
                else
                    hm.put(comProfVO.getCommProfileSetId()+":"+comProfVO.getSetVersion()+":"+comProfVO.getProductCode(),comProfVO);

                KeyName = comProfVO.getCommProfileSetName();
                label = new Label(col, row, KeyName);
                worksheet4.addCell(label);

                KeyName = comProfVO.getProductCode();
                label = new Label(++col, row, KeyName);
                worksheet4.addCell(label);

                KeyName = comProfVO.getGrphDomainCode();
                label = new Label(++col, row, KeyName);
                worksheet4.addCell(label);
                KeyName = comProfVO.getGradeCode();
                label = new Label(++col, row, KeyName);
                worksheet4.addCell(label);
                KeyName = comProfVO.getCommProfileSetId();
                label = new Label(++col, row, KeyName);
                worksheet4.addCell(label);

                KeyName = comProfVO.getSetVersion();
                label = new Label(++col, row, KeyName);
                worksheet4.addCell(label);

                if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION, networkCode)){
                    if(listBaseOtf!=null){
                        Iterator<OTFDetailsVO> iterator = listBaseOtf.iterator();
                        OTFDetailsVO baseCommProfDeOTFVO;
                        ArrayList<String> checkSame=new <String>ArrayList();
                        slabItr=0;
                        while (iterator.hasNext() && slabItr<slabLengthOTF) {
                            baseCommProfDeOTFVO=iterator.next();
                            if(checkSame.contains(comProfVO.getCommProfileDetailID())){
                                //value
                                KeyName=baseCommProfDeOTFVO.getOtfValue();
                                label=new Label(++col,row,KeyName);
                                worksheet4.addCell(label);

                                //type
                                KeyName=baseCommProfDeOTFVO.getOtfType();
                                label=new Label(++col,row,KeyName);
                                worksheet4.addCell(label);

                                // rate
                                KeyName=baseCommProfDeOTFVO.getOtfRate();
                                label=new Label(++col,row,KeyName);
                                worksheet4.addCell(label);
                            }
                            else {
                                //for otf appfrom
                                if(baseCommProfDeOTFVO.getOtfApplicableFrom()!=null){
                                    KeyName=BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(baseCommProfDeOTFVO.getOtfApplicableFrom()));
                                }
                                else{
                                    KeyName="";
                                }
                                label=new Label(++col,row,KeyName);
                                worksheet4.addCell(label);

                                //for otf app to
                                if(baseCommProfDeOTFVO.getOtfApplicableTo()!=null){
                                    KeyName=BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(baseCommProfDeOTFVO.getOtfApplicableTo()));
                                }
                                else{
                                    KeyName="";
                                }

                                label=new Label(++col,row,KeyName);
                                worksheet4.addCell(label);

                                //otf timestamp
                                KeyName=baseCommProfDeOTFVO.getOtfTimeSlab();
                                label=new Label(++col,row,KeyName);
                                worksheet4.addCell(label);

                                //value
                                KeyName=baseCommProfDeOTFVO.getOtfValue();
                                label=new Label(++col,row,KeyName);
                                worksheet4.addCell(label);

                                //type
                                KeyName=baseCommProfDeOTFVO.getOtfType();
                                label=new Label(++col,row,KeyName);
                                worksheet4.addCell(label);

                                // rate
                                KeyName=baseCommProfDeOTFVO.getOtfRate();
                                label=new Label(++col,row,KeyName);
                                worksheet4.addCell(label);
                            }
                            checkSame.add(comProfVO.getCommProfileDetailID());
                            slabItr++;
                        }
                    }//end of is empty check
                }//end if for target based base commission
                row++;
                col = 0;
            }
        }
        catch (RowsExceededException e) {
            log.errorTrace(METHOD_NAME, e);
            log.error("writeCommissionSheet", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            log.errorTrace(METHOD_NAME, e);
            log.error("writeCommissionSheet", " Exception e: " + e.getMessage());
            throw e;
        } finally {

            if (log.isDebugEnabled()) {
                log.debug("writeExcel", " Exiting");
            }
        }
    }


    private int writeServiceListDataForModifyForBatchModifyCommissionProfile(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        final String methodName = "writeServiceListDataForModifyForBatchModifyCommissionProfile";
        if (log.isDebugEnabled()) {
            log.debug("writeServiceListDataForModifyForBatchModifyCommissionProfile", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
        }
        try {
            String keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_SERVICE_TYPE_HEADING, null);
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            // added by harsh
            row++;
            String keyName1 = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_MASTERSHEET_SERVICETYPE, null);
            label = new Label(col, row, keyName1, times16format);
            worksheet2.addCell(label);
            col++;
            String keyName2 = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_MASTERSHEET_SERVICENAME, null);
            label = new Label(col, row, keyName2, times16format);
            worksheet2.addCell(label);
            // end added by harsh
            row++;
            ArrayList serviceList = (ArrayList) p_hashMap.get(PretupsI.BATCH_COMM_SERVICE_LIST);
            ListValueVO listValueVO = null;
            if (serviceList != null) {
                for (int i = 0, j = serviceList.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) serviceList.get(i);
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(++col, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (WriteException e) {
            log.errorTrace(methodName, e);
            log.error("writeDomainCategoryListData", " Exception e: " + e.getMessage());
            throw e;
        }

    }

    private int writeSubServiceListDataForModifyForBatchModifyCommissionProfile(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        final String methodName = "writeSubServiceListDataForModifyForBatchModifyCommissionProfile";
        if (log.isDebugEnabled()) {
            log.debug("writeSubServiceListDataForModifyForBatchModifyCommissionProfile", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
        }
        try {
            String keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_SUBSERVICE_TYPE_HEADING, null);
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_SUBSERVICE_TYPE_NOTE, null);
            label = new Label(col, row, keyName);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_MASTERSHEET_SERVICETYPE, null);
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_MASTERSHEET_SUBSERVICE_TYPE, null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_MASTERSHEET_SUBSERVICE_NAME, null);
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            row++;
            ArrayList selectorList = (ArrayList) p_hashMap.get(PretupsI.BATCH_COMM_SUBSERVICE_LIST);
            String prevSrvc = null;
            ListValueVO listValueVO;
            if (selectorList != null) {
                for (int i = 0, j = selectorList.size(); i < j; i++) {
                    col = 0;
                    listValueVO = (ListValueVO) selectorList.get(i);
                    if (!listValueVO.getOtherInfo().equalsIgnoreCase(prevSrvc)) {
                        label = new Label(col, row, listValueVO.getOtherInfo());
                        worksheet2.addCell(label);
                    }
                    col++;
                    prevSrvc = listValueVO.getOtherInfo();
                    label = new Label(col, row, listValueVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(++col, row, listValueVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                }
            }
            return row;
        } catch (WriteException e) {
            log.errorTrace(methodName, e);
            log.error("writeDomainCategoryListData", " Exception e: " + e.getMessage());
            throw e;
        }
    }
    
  

   	    /**
   	     * 
   	     * @param list
   	     * @param pHashMap
   	     * @param response
   	     * @param locale
   	     * @param pFileName
   	     * @param sequenceNo
   	     * @throws Exception
   	     */

   	   public void writeExcelForBatchAddCommProfileFromRest(ArrayList list, HashMap<String, Object> p_hashMap,
   				BatchAddCommProfRespVO response, Locale locale, String p_fileName, String sequenceNo) {
   			final String METHOD_NAME = "writeExcelForBatchAddCommProfileFromRest";
   			if (log.isDebugEnabled()) {
   				log.debug(METHOD_NAME, " p_locale: " + locale + " p_fileName: " + p_fileName);
   			}
   			WritableWorkbook workbook = null;
   			WritableSheet worksheet1 = null;
   			WritableSheet worksheet2 = null;
   			WritableSheet worksheet3 = null;
   			WritableSheet worksheet4 = null;

   			int col = 0;
   			int row = 0;

   			locale  = locale;

   			try {
   				int count = 1;
   				workbook = Workbook.createWorkbook(new File(p_fileName));
   				worksheet1 = workbook.createSheet("Commission profile sheet", count++);
   				if ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,
   						(String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))
   						|| (Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,
   								(String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE)))
   					worksheet4 = workbook.createSheet("CBC Slab sheet", count++);
   				worksheet3 = workbook.createSheet("Additional comm. profile sheet", count++);
   				worksheet2 = workbook.createSheet("Master Sheet", count++);
   				// worksheet4 = workbook.createSheet("OTF Profile sheet", 2);

   				Label label = null;

   				String keyName = null;
   				col = 0;
   				row = 0;   
   	            keyName = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_MASTER_HEADING,new String[] {p_hashMap.get(PretupsI.BATCH_USR_DOMAIN_NAME).toString(),p_hashMap.get(PretupsI.BATCH_USR_CATEGORY_NAME).toString() });  	
   				label = new Label(col, row, keyName, times16format);
   				worksheet2.mergeCells(col, row, 8, row);
   				worksheet2.addCell(label);

   				row++;
   				col = 0;
   				row = this.writeProductListDataFromRest(worksheet2, col, row, p_hashMap,locale);
   				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD)))
   						.booleanValue()) {
   					row++;
   					col = 0;
   					row = this.writeTransactionTypeFromRest(worksheet2, col, row, p_hashMap, locale);
   				}
   				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_MODE_ALWD))).booleanValue()) {
   					row++;
   					col = 0;
   					row = this.writePaymentModeDataFromRest(worksheet2, col, row, p_hashMap,locale);
   				}
   				row++;
   				col = 0;
   				row = this.writeDomainCategoryListDataFromRest(worksheet2, col, row, p_hashMap,locale);
   				row++;
   				col = 0;
   				row = this.writeServiceListDataFromRest(worksheet2, col, row, p_hashMap,locale);
   				row++;
   				col = 0;
   				if (!BTSLUtil.isNullString(
   						((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVC_PROD_MAPPING_ALLOWED)))) {
   					row = this.writeSubServiceListDataFromRest(worksheet2, col, row, p_hashMap,locale);
   					row++;
   					col = 0;
   				}

   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_COMMISSION,null);
   				label = new Label(col, row, keyName, times16format);
   				worksheet2.addCell(label);

   				row++;
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_COMMISSION_NOTE,null);
   				label = new Label(col, row, keyName);
   				worksheet2.mergeCells(col, row, col + 3, row);
   				worksheet2.addCell(label);

   				row++;
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX_TYPE,null);
   				label = new Label(col, row, keyName, times16format);
   				worksheet2.addCell(label);

   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX_DESCIPTION,null);
   				label = new Label(++col, row, keyName, times16format);
   				worksheet2.addCell(label);

   				row++;
   				col = 0;
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_AMT,null);
   				label = new Label(col, row, keyName);
   				worksheet2.addCell(label);

   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_AMOUNT,null);
   				label = new Label(++col, row, keyName);
   				worksheet2.addCell(label);

   				row++;
   				col = 0;
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PCT,null);
   				label = new Label(col, row, keyName);
   				worksheet2.addCell(label);

   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PERCENTAGE,null);
   				label = new Label(++col, row, keyName);
   				worksheet2.addCell(label);
   				
   				row++;
   				col = 0;
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DUAL_PROFILE_TYPE,null);
   				label = new Label(col, row, keyName, times16format);
   				worksheet2.addCell(label);

   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DUAL_PROFILE_TYPE_DESCRIPTION,null);
   				label = new Label(++col, row, keyName, times16format);
   				worksheet2.addCell(label);

   				final ArrayList dualCommissionTypeList = LookupsCache.loadLookupDropDown(PretupsI.DUAL_COMM_TYPE, true);

   				for (Object vo : dualCommissionTypeList) {
   					ListValueVO lv = (ListValueVO) vo;
   					row++;
   					col = 0;
   					label = new Label(col, row, lv.getValue());
   					worksheet2.addCell(label);
   					label = new Label(++col, row, lv.getLabel());
   					worksheet2.addCell(label);
   				}

   				// End//

   				row++;
   				row++;
   				col = 0;
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX_CAL_FOC_NOTE,null);
   				label = new Label(col, row, keyName, times16format);
   				worksheet2.mergeCells(col, row, col + 3, row);
   				worksheet2.addCell(label);

   				row++;
   				col = 0;
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DUAL_PROFILE_TYPE_DESCRIPTION,null);
   				label = new Label(col, row, keyName);
   				worksheet2.mergeCells(col, row, col + 3, row);
   				worksheet2.addCell(label);

   				row++;
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX_CAL_FOC_NOTE,null);
   				label = new Label(col, row, keyName);
   				worksheet2.addCell(label);

   				row++;
   				row++;
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX_CAL_C2C_TRANSFER,null);
   				label = new Label(col, row, keyName, times16format);
   				worksheet2.mergeCells(col, row, col + 6, row);
   				worksheet2.addCell(label);

   				row++;
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX_CAL_C2C_TRANSFER_DESCRIPTION,null);
   				label = new Label(col, row, keyName);
   				worksheet2.mergeCells(col, row, col + 6, row);
   				worksheet2.addCell(label);

   				row++;
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX_CAL_C2C_TRANSFER_NOTE,null);
   				label = new Label(col, row, keyName);
   				worksheet2.addCell(label);

   				row++;
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_COMMISSION_NOTE,null);
   				label = new Label(col, row, keyName);
   				worksheet2.mergeCells(col, row, col + 3, row);
   				worksheet2.addCell(label);

   				row++;
   				row++;
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.PROFILE_ADDADDITIONALPROFILE_LABEL_TIMESLAB_EXAMPLE,null);
   				label = new Label(col, row, keyName, times16format);
   				worksheet2.addCell(label);

   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.PROFILE_ADDADDITIONALPROFILE_LABEL_TIMESLAB_EXAMPLE,null);
   				label = new Label(++col, row, keyName, times16format);
   				worksheet2.addCell(label);
   				if ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,
   						(String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))
   						|| (Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,
   								(String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))) {
   					row++;
   					row++;
   					col = 0;
   					keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL_COMMON,null);
   					label = new Label(col, row, keyName, times16format);
   					worksheet2.mergeCells(col, row, col + 1, row);
   					worksheet2.addCell(label);

   					row++;
   					keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_OTF_COMMENT_TO_TYPE,null);
   					label = new Label(col, row, keyName);
   					worksheet2.addCell(label);

   					if ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,
   							(String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))) {
   						row++;
   						keyName = RestAPIStringParser.getMessage(locale ,
   								PretupsErrorCodesI.BASE_BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_OTF_TYPE_ID_COMMENT,null);
   						label = new Label(col, row, keyName);
   						worksheet2.addCell(label);
   						row++;
   						keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_MODIFY_MASTER_OTF_COMMENT_DATE_AND_TIME,null);
   						label = new Label(col, row, keyName);
   						worksheet2.addCell(label);
   					} else if ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,
   							(String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))) {
   						row++;
   						keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BASE_BATCHADDMODIFYCOMM_PROFILE_OTF_TYPE_ID_COMMENT,null);
   						label = new Label(col, row, keyName);
   						worksheet2.addCell(label);

   						row++;
   						keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BASE_BATCHADDMODIFYCOMM_PROFILE_OTF_DATE_TIME,null);
   						label = new Label(col, row, keyName);
   						worksheet2.addCell(label);
   					}

   				}

				col = 0;
				row = 0;
				this.writeInCommProfSheetFromRest(worksheet1, col, row, p_hashMap, locale);
				if ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,
						(String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))
						|| (Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,
								(String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE)))
				this.writeInCommProfOTFSheetFromRest(worksheet4, col, row, p_hashMap, locale);
				this.writeInAddCommprofSheetFromRest(worksheet3, col, row, p_hashMap, sequenceNo, locale);
				workbook.write();

   			} catch (Exception e) {
   				log.errorTrace(METHOD_NAME, e);
   				log.error(METHOD_NAME, " Exception e: " + e.getMessage());
   			} finally {
   				try {
   					if (workbook != null) {
   						workbook.close();
   					}
   				} catch (Exception e) {
   					log.errorTrace(METHOD_NAME, e);
   				}
   				worksheet1 = null;
   				worksheet2 = null;
   				worksheet3 = null;
   				worksheet4 = null;
   				workbook = null;

   				if (log.isDebugEnabled()) {
   					log.debug(METHOD_NAME, " Exiting");
   				}
   			}
   		}
   		
   		private int writeTransactionTypeFromRest(WritableSheet worksheet2, int col, int row, HashMap p_hashMap, Locale locale) {
   			final String METHOD_NAME = "writeTransactionTypeFromRest";
   			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME,
						" p_hashMap size=" + p_hashMap.size() + " p_locale: " + locale + " col=" + col + " row=" + row);
   			}
   			try {
   				String keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TRANSACTION_TYPE,null);
   				Label label = new Label(col, row, keyName, times16format);
   				worksheet2.mergeCells(col, row, col + 2, row);
   				worksheet2.addCell(label);
   				row++;
   				col = 0;
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TRANSACTION_TYPE_COMMENT,null);
   				label = new Label(col, row, keyName, times16format);
   				worksheet2.addCell(label);
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TRANSACTION_DESCRIPTION,null);
   				label = new Label(++col, row, keyName, times16format);
   				worksheet2.addCell(label);
   				row++;

   				ArrayList productList = (ArrayList) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_TRANSACTION_TYPE);
   				ListValueVO listValueVO = null;
   				if (productList != null) {
   					for (int i = 0, j = productList.size(); i < j; i++) {
   						col = 0;
   						listValueVO = (ListValueVO) productList.get(i);
   						label = new Label(col, row, listValueVO.getValue());
   						worksheet2.addCell(label);
   						label = new Label(++col, row, listValueVO.getLabel());
   						worksheet2.addCell(label);
   						row++;
   					}
   				}
   				return row;
   			} catch (WriteException e) {
   				log.errorTrace(METHOD_NAME, e);
   				log.error(METHOD_NAME, " Exception e: " + e.getMessage());
   			}
   			return row;
   		}
   		
   		private int writeProductListDataFromRest(WritableSheet worksheet2, int col, int row, HashMap p_hashMap, Locale locale) {
   			final String METHOD_NAME = "writeProductListDataFromRest";
   			if (log.isDebugEnabled()) {
   				log.debug(METHOD_NAME,
   						" p_hashMap size=" + p_hashMap.size() + " p_locale: " + locale  + " col=" + col + " row=" + row);
   			}
   			try {
   				String keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PRODUCT_HEADING,null);
   				Label label = new Label(col, row, keyName, times16format);
   				worksheet2.mergeCells(col, row, col + 2, row);
   				worksheet2.addCell(label);
   				row++;
   				col = 0;
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PRODUCT_HEADING_NOTE,null);
   				label = new Label(col, row, keyName);
   				worksheet2.mergeCells(col, row, 10, row);
   				worksheet2.addCell(label);
   				row++;
   				col = 0;
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PRODUCTCODE,null);
   				label = new Label(col, row, keyName, times16format);
   				worksheet2.addCell(label);
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PRODUCTNAME,null);
   				label = new Label(++col, row, keyName, times16format);
   				worksheet2.addCell(label);
   				row++;

   				ArrayList productList = (ArrayList) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_PRODUCT_LIST);
   				ListValueVO listValueVO = null;
   				if (productList != null) {
   					for (int i = 0, j = productList.size(); i < j; i++) {
   						col = 0;
   						listValueVO = (ListValueVO) productList.get(i);
   						label = new Label(col, row, listValueVO.getValue());
   						worksheet2.addCell(label);
   						label = new Label(++col, row, listValueVO.getLabel());
   						worksheet2.addCell(label);
   						row++;
   					}
   				}
   				return row;
   			} catch (WriteException e) {
   				log.errorTrace(METHOD_NAME, e);
   				log.error(METHOD_NAME, " Exception e: " + e.getMessage());
   			}
   			return row;
   		}
   		
   		private int writePaymentModeDataFromRest(WritableSheet worksheet2, int col, int row, HashMap p_hashMap,Locale locale) {
   			final String METHOD_NAME = "writePaymentModeDataFromRest";
   			if (log.isDebugEnabled()) {
   				log.debug(METHOD_NAME,
   						" p_hashMap size=" + p_hashMap.size() + " p_locale: " + locale  + " col=" + col + " row=" + row);
   			}
   			try {
   				String keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PAYMENT_HEADING,null);
   				Label label = new Label(col, row, keyName, times16format);
   				worksheet2.mergeCells(col, row, col + 2, row);
   				worksheet2.addCell(label);
   				row++;
   				col = 0;
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PAYMENTCODE,null);
   				label = new Label(col, row, keyName, times16format);
   				worksheet2.addCell(label);
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PAYMENTNAME,null);
   				label = new Label(++col, row, keyName, times16format);
   				worksheet2.addCell(label);
   				row++;

   				ArrayList productList = (ArrayList) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_PAYMENT_MODE);
   				ListValueVO listValueVO = null;
   				if (productList != null) {
   					for (int i = 0, j = productList.size(); i < j; i++) {
   						col = 0;
   						listValueVO = (ListValueVO) productList.get(i);
   						label = new Label(col, row, listValueVO.getValue());
   						worksheet2.addCell(label);
   						label = new Label(++col, row, listValueVO.getLabel());
   						worksheet2.addCell(label);
   						row++;
   					}
   				}
   				return row;
   			} catch (WriteException e) {
   				log.errorTrace(METHOD_NAME, e);
   				log.error(METHOD_NAME, " Exception e: " + e.getMessage());
   			}
   			return row;
   		}

   		private int writeDomainCategoryListDataFromRest(WritableSheet worksheet2, int col, int row, HashMap pHashMap,Locale locale) {
   			final String METHOD_NAME = "writeDomainCategoryListData";
   			if (log.isDebugEnabled()) {
   				log.debug(METHOD_NAME,
   						" p_hashMap size=" + pHashMap.size() + " p_locale: " + locale  + " col=" + col + " row=" + row);
   			}

   			try {
   				String keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DOMAIN_CATEGORY_HEADING,null);
   				Label label = new Label(col, row, keyName, times16format);
   				worksheet2.mergeCells(col, row, col + 2, row);
   				worksheet2.addCell(label);
   				row++;
   				col = 0;
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DOMAIN_CATEGORY_HEADING_NOTE,null);
   				label = new Label(col, row, keyName);
   				worksheet2.mergeCells(col, row, 10, row);
   				worksheet2.addCell(label);
   				row++;
   				col = 0;

   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DOMAINCODE,null);
   				label = new Label(col, row, keyName, times16format);
   				worksheet2.addCell(label);
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DOMAINNAME,null);
   				label = new Label(++col, row, keyName, times16format);
   				worksheet2.addCell(label);
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_CATEGORYCODE,null);
   				label = new Label(++col, row, keyName, times16format);
   				worksheet2.addCell(label);
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_CATEGORYNAME,null);
   				label = new Label(++col, row, keyName, times16format);
   				worksheet2.addCell(label);

   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_GRPHDOMAINCODE,null);
   				label = new Label(++col, row, keyName, times16format);
   				worksheet2.addCell(label);
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_GRPHDOMAINNAME,null);
   				label = new Label(++col, row, keyName, times16format);
   				worksheet2.addCell(label);
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_GEADECODE,null);
   				label = new Label(++col, row, keyName, times16format);
   				worksheet2.addCell(label);
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_GEADENAME,null);
   				label = new Label(++col, row, keyName, times16format);
   				worksheet2.addCell(label);
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_GATEWAYCODE,null);
   				label = new Label(++col, row, keyName, times16format);
   				worksheet2.addCell(label);
   				keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_GATEWAYNAME,null);
   				label = new Label(++col, row, keyName, times16format);
   				worksheet2.addCell(label);

   				ArrayList geoDomainList = (ArrayList) pHashMap.get(PretupsI.BATCH_COMM_GEOGRAPHY_LIST);
   				ArrayList gradeList = (ArrayList) pHashMap.get(PretupsI.BATCH_COMM_GRADE_LIST);
   				ArrayList gatewayList = (ArrayList) pHashMap.get(PretupsI.BATCH_COMM_GATEWAY_LIST);
   				GeographicalDomainVO geoDomainVO;
   				GradeVO gradeVO;
   				MessageGatewayVO msgGateVO;

   				if ((PretupsI.ALL).equals((String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN_CODE))) {
   					row = row + 1;
   					ArrayList domainList = (ArrayList) pHashMap.get(PretupsI.BATCH_COMM_DOMAIN_LIST);
   					ArrayList categoryList = (ArrayList) pHashMap.get(PretupsI.BATCH_COMM_CATEGORY_LIST);
   					ListValueVO domainVO;
   					CategoryVO categoryVO;
   					int domainListSize = domainList.size();
   					if (domainList != null && domainList.size() > 0) {
   						for (int i = 0; i < domainListSize; i++) {
   							col = 0;
   							domainVO = (ListValueVO) domainList.get(i);
   							label = new Label(col, row, domainVO.getValue());
   							worksheet2.addCell(label);
   							label = new Label(++col, row, domainVO.getLabel());
   							worksheet2.addCell(label);
   							if (categoryList != null && categoryList.size() > 0) {
   								col++;
   								int categoryListSize = categoryList.size();
   								for (int j = 0; j < categoryListSize; j++) {
   									int catCol = col;
   									categoryVO = (CategoryVO) categoryList.get(j);
   									if (categoryVO.getDomainCodeforCategory().equals(domainVO.getValue())) {
   										label = new Label(catCol, row, categoryVO.getCategoryCode());
   										worksheet2.addCell(label);
   										label = new Label(++catCol, row, categoryVO.getCategoryName());
   										worksheet2.addCell(label);
   										row++;
   									}
   								}
   							}

   							row++;
   						}
   					}
   				} else {
   					row++;
   					col = 0;
   					label = new Label(col, row, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN_CODE));
   					worksheet2.addCell(label);
   					label = new Label(++col, row, (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN));
   					worksheet2.addCell(label);
   					if ((PretupsI.ALL).equals((String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY_CODE))) {
   						ArrayList categoryList = (ArrayList) pHashMap.get(PretupsI.BATCH_COMM_CATEGORY_LIST);
   						CategoryVO categoryVO;
   						if (categoryList != null && !categoryList.isEmpty()) {
   							col++;
   							int categoryListSize = categoryList.size();
   							for (int i = 0; i < categoryListSize; i++) {
   								int catCol = col;
   								categoryVO = (CategoryVO) categoryList.get(i);
   								if (categoryVO.getDomainCodeforCategory()
   										.equals((String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN_CODE))) {
   									label = new Label(catCol, row, categoryVO.getCategoryCode());
   									worksheet2.addCell(label);
   									label = new Label(++catCol, row, categoryVO.getCategoryName());
   									worksheet2.addCell(label);
   									int geoRow = row;
   									int geoDomainListSize = geoDomainList.size();
   									for (int j = 0; j < geoDomainListSize; j++) {
   										int geoCol = catCol;
   										geoDomainVO = (GeographicalDomainVO) geoDomainList.get(j);
   										if (geoDomainVO.getcategoryCode().equals(categoryVO.getCategoryCode())) {
   											label = new Label(++geoCol, geoRow, geoDomainVO.getGrphDomainCode());
   											worksheet2.addCell(label);
   											label = new Label(++geoCol, geoRow, geoDomainVO.getGrphDomainName());
   											worksheet2.addCell(label);
   											geoRow++;
   										}
   									}
   									int grdRow = row;
   									int gradeListSize = gradeList.size();
   									for (int j = 0; j < gradeListSize; j++) {
   										int grdCol = catCol + 2;
   										gradeVO = (GradeVO) gradeList.get(j);
   										if (gradeVO.getCategoryCode().equals(categoryVO.getCategoryCode())) {
   											label = new Label(++grdCol, grdRow, gradeVO.getGradeCode());
   											worksheet2.addCell(label);
   											label = new Label(++grdCol, grdRow, gradeVO.getGradeName());
   											worksheet2.addCell(label);
   											grdRow++;
   										}
   									}
   									int gateRow = row;
   									int gatewayListSize = gatewayList.size();
   									for (int j = 0; j < gatewayListSize; j++) {
   										int gateCol = catCol + 4;
   										msgGateVO = (MessageGatewayVO) gatewayList.get(j);
   										if (msgGateVO.getCategoryCode().equals(categoryVO.getCategoryCode())) {
   											label = new Label(++gateCol, gateRow, msgGateVO.getGatewayCode());
   											worksheet2.addCell(label);
   											label = new Label(++gateCol, gateRow, msgGateVO.getGatewayName());
   											worksheet2.addCell(label);
   											gateRow++;
   										}
   									}
   									if (geoRow >= grdRow && geoRow >= gateRow) {
   										row = geoRow;
   									} else if (grdRow >= gateRow) {
   										row = grdRow;
   									} else {
   										row = gateRow;
   									}
   								}
   							}
   						}
   					} else {
   						label = new Label(++col, row,(String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY_CODE));
   						worksheet2.addCell(label);
   						keyName = String.valueOf(pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY));
   						label = new Label(++col, row,keyName);
   						worksheet2.addCell(label);
   						String categoryCode = (String) pHashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY_CODE);
   						int geoRow = row;
   						int geoDomainListsize = geoDomainList.size();
   						for (int j = 0; j < geoDomainListsize; j++) {
   							int geoCol = col;
   							geoDomainVO = (GeographicalDomainVO) geoDomainList.get(j);
   							if (geoDomainVO.getcategoryCode().equals(categoryCode)) {
   								label = new Label(++geoCol, geoRow, geoDomainVO.getGrphDomainCode());
   								worksheet2.addCell(label);
   								label = new Label(++geoCol, geoRow, geoDomainVO.getGrphDomainName());
   								worksheet2.addCell(label);
   								geoRow++;
   							}
   						}
   						int grdRow = row;
   						for (int j = 0; j < gradeList.size(); j++) {
   							int grdCol = col + 2;
   							gradeVO = (GradeVO) gradeList.get(j);
   							if (gradeVO.getCategoryCode().equals(categoryCode)) {
   								label = new Label(++grdCol, grdRow, gradeVO.getGradeCode());
   								worksheet2.addCell(label);
   								label = new Label(++grdCol, grdRow, gradeVO.getGradeName());
   								worksheet2.addCell(label);
   								grdRow++;
   							}
   						}
   						int gateRow = row;
   						for (int j = 0; j < gatewayList.size(); j++) {
   							int gateCol = col + 4;
   							msgGateVO = (MessageGatewayVO) gatewayList.get(j);
   							if (msgGateVO.getCategoryCode().equals(categoryCode)) {
   								label = new Label(++gateCol, gateRow, msgGateVO.getGatewayCode());
   								worksheet2.addCell(label);
   								label = new Label(++gateCol, gateRow, msgGateVO.getGatewayName());
   								worksheet2.addCell(label);
   								gateRow++;
   							}
   						}
   						if (geoRow >= grdRow && geoRow >= gateRow) {
   							row = geoRow;
   						} else if (grdRow >= gateRow) {
   							row = grdRow;
   						} else {
   							row = gateRow;
   						}

   					}
   				}
   				return row;
   			} catch (WriteException e) {
   				log.errorTrace(METHOD_NAME, e);
   				log.error(METHOD_NAME, " Exception e: " + e.getMessage());
   			}
   			return row;
   		}
   		private int writeServiceListDataFromRest(WritableSheet worksheet2, int col, int row, HashMap p_hashMap,Locale locale) throws Exception {
   			final String METHOD_NAME = "writeServiceListDataFromRest";
   			if (log.isDebugEnabled()) {
   				log.debug(METHOD_NAME,
   						" p_hashMap size=" + p_hashMap.size() + " p_locale: " + locale  + " col=" + col + " row=" + row);
   			}
   			try {
   				String keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_SERVICE_TYPE_HEADING,null);
   				Label label = new Label(col, row, keyName, times16format);
   				worksheet2.mergeCells(col, row, col + 2, row);
   				worksheet2.addCell(label);
   				// added by harsh
   				row++;
   				String keyName1 = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_MASTERSHEET_SERVICETYPE,null);
   				label = new Label(col, row, keyName1, times16format);
   				worksheet2.addCell(label);
   				col++;
   				String keyName2 = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_MASTERSHEET_SERVICENAME,null);
   				label = new Label(col, row, keyName2, times16format);
   				worksheet2.addCell(label);
   				// end added by harsh
   				row++;
   				ArrayList serviceList = (ArrayList) p_hashMap.get(PretupsI.BATCH_COMM_SERVICE_LIST);
   				ListValueVO listValueVO = null;
   				if (serviceList != null) {
   					for (int i = 0, j = serviceList.size(); i < j; i++) {
   						col = 0;
   						listValueVO = (ListValueVO) serviceList.get(i);
   						label = new Label(col, row, listValueVO.getValue());
   						worksheet2.addCell(label);
   						label = new Label(++col, row, listValueVO.getLabel());
   						worksheet2.addCell(label);
   						row++;
   					}
   				}
   				return row;
   			} catch (WriteException e) {
   				log.errorTrace(METHOD_NAME, e);
   				log.error(METHOD_NAME, " Exception e: " + e.getMessage());
   				throw e;
   			}

   		}
   		
   		private int writeSubServiceListDataFromRest(WritableSheet worksheet2, int col, int row, HashMap p_hashMap,Locale locale)
   				throws Exception {

   			final String METHOD_NAME = "writeSubServiceListDataFromRest";
   			if (log.isDebugEnabled()) {
   				log.debug(METHOD_NAME,
   						" p_hashMap size=" + p_hashMap.size() + " p_locale: " + locale  + " col=" + col + " row=" + row);
   			}
   			try {
   				String keyName = RestAPIStringParser.getMessage(locale ,
   						PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_SUBSERVICE_TYPE_HEADING, null);
   				Label label = new Label(col, row, keyName, times16format);
   				worksheet2.mergeCells(col, row, col + 2, row);
   				worksheet2.addCell(label);
   				row++;
   				keyName = RestAPIStringParser.getMessage(locale ,
   						PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_SUBSERVICE_TYPE_NOTE, null);
   				label = new Label(col, row, keyName);
   				worksheet2.mergeCells(col, row, col + 2, row);
   				worksheet2.addCell(label);
   				row++;
   				keyName = RestAPIStringParser.getMessage(locale ,
   						PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_MASTERSHEET_SERVICETYPE, null);
   				label = new Label(col, row, keyName, times16format);
   				worksheet2.addCell(label);
   				keyName = RestAPIStringParser.getMessage(locale ,
   						PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_MASTERSHEET_SUBSERVICE_TYPE, null);
   				label = new Label(++col, row, keyName, times16format);
   				worksheet2.addCell(label);
   				keyName = RestAPIStringParser.getMessage(locale ,
   						PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_MASTERSHEET_SUBSERVICE_NAME, null);
   				label = new Label(++col, row, keyName, times16format);
   				worksheet2.addCell(label);
   				row++;
   				ArrayList selectorList = (ArrayList) p_hashMap.get(PretupsI.BATCH_COMM_SUBSERVICE_LIST);
   				String prevSrvc = null;
   				ListValueVO listValueVO;
   				if (selectorList != null) {
   					for (int i = 0, j = selectorList.size(); i < j; i++) {
   						col = 0;
   						listValueVO = (ListValueVO) selectorList.get(i);
   						if (!listValueVO.getOtherInfo().equalsIgnoreCase(prevSrvc)) {
   							label = new Label(col, row, listValueVO.getOtherInfo());
   							worksheet2.addCell(label);
   						}
   						col++;
   						prevSrvc = listValueVO.getOtherInfo();
   						label = new Label(col, row, listValueVO.getValue());
   						worksheet2.addCell(label);
   						label = new Label(++col, row, listValueVO.getLabel());
   						worksheet2.addCell(label);
   						row++;
   					}
   				}
   				return row;
   			} catch (WriteException e) {
   				log.errorTrace(METHOD_NAME, e);
   				log.error(METHOD_NAME, " Exception e: " + e.getMessage());
   				throw e;
   			}
   		}
   		
   		private void writeInCommProfSheetFromRest(WritableSheet worksheet1, int col, int row, HashMap p_hashMap,Locale locale) throws Exception {
   	        final String METHOD_NAME = "writeInCommProfSheetFromRest";
   	        if (log.isDebugEnabled()) {
   	            log.debug(METHOD_NAME, " p_hashMap size=" + p_hashMap.size() + " p_locale: " + locale  + " col=" + col + " row=" + row);
   	        }
   	        try {
   	        	String[] args = { (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA)) == null ? PretupsI.DATE_FORMAT : ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA))) };
   	        	String keyName;
   	            Label label;
   	            // 1
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_COMM_TEMPLATE_HEADING,null);
   	            label = new Label(col, row, keyName, times16format);
   	            worksheet1.mergeCells(col, row, col + 10, row);
   	            worksheet1.addCell(label);
   	            row++;
   	            col = 0;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DOWNLOADED_BY,null);
   	            label = new Label(col, row, keyName, times16format);
   	            worksheet1.addCell(label);
   	            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.BATCH_COMM_CREATED_BY));
   	            worksheet1.mergeCells(col, row, col + 1, row);
   	            worksheet1.addCell(label);
   	            row++;
   	            col = 0;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DOMAINNAME,null);
   	            label = new Label(col, row, keyName, times16format);
   	            worksheet1.addCell(label);

   	            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN));
   	            worksheet1.mergeCells(col, row, col + 1, row);
   	            worksheet1.addCell(label);

   	            row++;
   	            col = 0;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_CATEGORY,null);
   	            
   	            label = new Label(col, row, keyName, times16format);
   	            worksheet1.addCell(label);
 
   	            label = new Label(++col, row, String.valueOf(p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY)));
	            worksheet1.mergeCells(col, row, col + 1, row);
	            worksheet1.addCell(label);

   	            

   	            row++;
   	            col = 0;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_VERSION,null);
   	            label = new Label(col, row, keyName, times16format);
   	            worksheet1.addCell(label);

   	            label = new Label(++col, row, "1");
   	            worksheet1.mergeCells(col, row, col + 1, row);
   	            worksheet1.addCell(label);

   	            row++;
   	            col = 0;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_FIELDS_MENDATORY_WARNING,null);
   	            label = new Label(col, row, keyName, times16format);
   	            worksheet1.mergeCells(col, row, col + 5, row);
   	            worksheet1.addCell(label);

   	            /*------------------------  NEW BIG LINE-----------------*/
   	            row++;
   	            row++;
   	            col = -1;
   	            int count = 0;
   	            if ("ALL".equals((String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN_CODE))) {
   	                keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DOMAINCODE,null);
   	                label = new Label(++col, row, keyName, times16format);
   	                worksheet1.addCell(label);
   	                count++;
   	            }
   	            if ("ALL".equals((String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY_CODE))) {
   	                keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_CATEGORYCODE,null);
   	                label = new Label(++col, row, keyName, times16format);
   	                worksheet1.addCell(label);
   	                count++;
   	            }
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_GRPHDOMAINCODE,null);
   	            label = new Label(++col, row, keyName, times16format);
   	            worksheet1.addCell(label);
   	            count++;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_GEADECODE,null);
   	            label = new Label(++col, row, keyName, times16format);
   	            worksheet1.addCell(label);
   	            count++;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PROFILE_NAME,null);
   	            label = new Label(++col, row, keyName, times16format);
   	            worksheet1.addCell(label);
   	            count++;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_SHORT_CODE,null);
   	            label = new Label(++col, row, keyName, times16format);
   	            worksheet1.addCell(label);
   	            count++;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DUAL_PROFILE,null);
   	            label = new Label(++col, row, keyName, times16format);
   	            worksheet1.addCell(label);
   	            count++;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_APPLICABLE_FROM,null);
   	            label = new Label(++col, row, keyName, times16format);
   	            // Added by Amit Raheja
   	            String comment_from = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_APPLICABLE_FROM_COMMENT, args);
   	            cellFeatures = new WritableCellFeatures();
   	            cellFeatures.setComment(comment_from);
   	            label.setCellFeatures(cellFeatures);
   	            // Addition ends
   	            worksheet1.addCell(label);
   	            count++;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_APPLICABLE_TIME,null);
   	            label = new Label(++col, row, keyName, times16format);
   	            // Added by Amit Raheja
   	            String comment_time = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_APPLICABLE_TIME_COMMENT,null);
   	            cellFeatures = new WritableCellFeatures();
   	            cellFeatures.setComment(comment_time);
   	            label.setCellFeatures(cellFeatures);
   	            // Addition ends
   	            worksheet1.addCell(label);
   	            count++;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PRODUCTCODE,null);
   	            label = new Label(++col, row, keyName, times16format);
   	            worksheet1.addCell(label);
   	            count++;
   	            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue()){
   	            	keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TRANSACTION_TYPE,null);
   	            	label = new Label(++col, row, keyName, times16format);
   	            	worksheet1.addCell(label);
   	                String commentType = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TRANSACTION_TYPE_COMMENT,null);
   	                cellFeatures = new WritableCellFeatures();
   	                cellFeatures.setComment(commentType);
   	                label.setCellFeatures(cellFeatures);
   	                count++;
   	            }
   	            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_MODE_ALWD))).booleanValue()){
   	            	keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PAYMENTCODE,null);
   	            	label = new Label(++col, row, keyName, times16format);
   	            	worksheet1.addCell(label);
   	            	count++;
   	            }
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_MULTIPLE_OF,null);
   	            label = new Label(++col, row, keyName, times16format);
   	            worksheet1.addCell(label);
   	            count++;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_MIN_TRANSFER_VALUE,null);
   	            label = new Label(++col, row, keyName, times16format);
   	            worksheet1.addCell(label);
   	            count++;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_MAX_TRANSFER_VALUE,null);
   	            label = new Label(++col, row, keyName, times16format);
   	            worksheet1.addCell(label);
   	            count++;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX_CAL_FOC,null);
   	            label = new Label(++col, row, keyName, times16format);
   	            worksheet1.addCell(label);
   	            count++;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX_CAL_C2C_TRANSFER,null);
   	            label = new Label(++col, row, keyName, times16format);
   	            worksheet1.addCell(label);
   	            count++;
   	            row--;
   	            row--;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_ASSIGN_COMMISSION_SLABS,null);
   	            label = new Label(col + 1, row, keyName, times16format);
   	            worksheet1.mergeCells(col + 1, row, col + 10, row);
   	            worksheet1.addCell(label);
   	            row++;
   	            row++;
   	            row--;
   	            col++;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_FROM_RANGE,null);
   	            label = new Label(col, row, keyName, times16format);
   	            worksheet1.mergeCells(col, row++, col, row);
   	            worksheet1.addCell(label);
   	            row--;
   	            col++;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TO_RANGE,null);
   	            label = new Label(col, row, keyName, times16format);
   	            worksheet1.mergeCells(col, row++, col, row);
   	            worksheet1.addCell(label);
   	            row--;
   	            col++;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_COMMISSION,null);
   	            label = new Label(col, row, keyName, times16format);
   	            worksheet1.mergeCells(col++, row, col, row);
   	            worksheet1.addCell(label);
   	            row++;
   	            col--;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TYPE,null);
   	            label = new Label(col, row, keyName, times16format);
   	            worksheet1.mergeCells(col, row, col, row);
   	            worksheet1.addCell(label);
   	            col++;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_RATE,null);
   	            label = new Label(col, row, keyName, times16format);
   	            worksheet1.mergeCells(col, row, col, row);
   	            worksheet1.addCell(label);
   	            row--;
   	            col++;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAXES,null);
   	            label = new Label(col, row, keyName, times16format);
   	            worksheet1.mergeCells(col, row, col + 5, row);
   	            worksheet1.addCell(label);
   	            row++;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX1_TYPE,null);
   	            label = new Label(col, row, keyName, times16format);
   	            worksheet1.mergeCells(col, row, col, row);
   	            worksheet1.addCell(label);
   	            col++;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX1_RATE,null);
   	            label = new Label(col, row, keyName, times16format);
   	            worksheet1.mergeCells(col, row, col, row);
   	            worksheet1.addCell(label);
   	            col++;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX2_TYPE,null);
   	            label = new Label(col, row, keyName, times16format);
   	            worksheet1.mergeCells(col, row, col, row);
   	            worksheet1.addCell(label);
   	            col++;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX2_RATE,null);
   	            label = new Label(col, row, keyName, times16format);
   	            worksheet1.mergeCells(col, row, col, row);
   	            worksheet1.addCell(label);
   	            col++;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX3_TYPE,null);
   	            label = new Label(col, row, keyName, times16format);
   	            worksheet1.mergeCells(col, row, col, row);
   	            worksheet1.addCell(label);

   	            col++;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX3_RATE,null);
   	            label = new Label(col, row, keyName, times16format);
   	            worksheet1.mergeCells(col, row, col, row);
   	            worksheet1.addCell(label);
   	            
   	            // /setting for the vertical freeze panes
   	            SheetSettings sheetSetting = new SheetSettings();
   	            sheetSetting = worksheet1.getSettings();
   	            sheetSetting.setVerticalFreeze(row + 1);

   	            // /setting for the horizontal freeze panes
   	      
   	            SheetSettings sheetSetting1;
   	            sheetSetting1 = worksheet1.getSettings();
   	            sheetSetting1.setHorizontalFreeze(count);
   	        }

   	        catch (WriteException e) {
   	            log.errorTrace(METHOD_NAME, e);
   	            log.error(METHOD_NAME, " Exception e: " + e.getMessage());
   	            throw e;
   	        } 
   	    }
   		
   		private void writeInCommProfOTFSheetFromRest(WritableSheet worksheet4, int col, int row, HashMap p_hashMap,Locale locale) throws Exception {
   	        final String METHOD_NAME = "writeInCommProfSheetFromRest";
   	        if (log.isDebugEnabled()) {
   	            log.debug(METHOD_NAME, " p_hashMap size=" + p_hashMap.size() + " p_locale: " + locale  + " col=" + col + " row=" + row);
   	        }
   	        try {
   	        	String[] args = { (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA)) == null ? PretupsI.DATE_FORMAT : ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA))) };
   	        	String keyName;
   	            Label label;
   	            // 1
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_OTF_COMM_TEMPLATE_HEADING,null);
   	            label = new Label(col, row, keyName, times16format);
   	            worksheet4.mergeCells(col, row, col + 10, row);
   	            worksheet4.addCell(label);
   	            row++;
   	            col = 0;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DOWNLOADED_BY,null);
   	            label = new Label(col, row, keyName, times16format);
   	            worksheet4.addCell(label);
   	            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.BATCH_COMM_CREATED_BY));
   	            worksheet4.mergeCells(col, row, col + 1, row);
   	            worksheet4.addCell(label);
   	            row++;
   	            col = 0;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DOMAIN,null);
   	            label = new Label(col, row, keyName, times16format);
   	            worksheet4.addCell(label);

   	            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN));
   	            worksheet4.mergeCells(col, row, col + 1, row);
   	            worksheet4.addCell(label);

   	            row++;
   	            col = 0;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_CATEGORY,null);
   	            label = new Label(col, row, keyName, times16format);
   	            worksheet4.addCell(label);

   	            label = new Label(++col, row, String.valueOf(p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY)));
   	            worksheet4.mergeCells(col, row, col + 1, row);
   	            worksheet4.addCell(label);

   	            row++;
   	            col = 0;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_VERSION,null);
   	            label = new Label(col, row, keyName, times16format);
   	            worksheet4.addCell(label);

   	            label = new Label(++col, row, "1");
   	            worksheet4.mergeCells(col, row, col + 1, row);
   	            worksheet4.addCell(label);

   	            row++;
   	            col = 0;
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_FIELDS_MENDATORY_WARNING,null);
   	            label = new Label(col, row, keyName, times16format);
   	            worksheet4.mergeCells(col, row, col + 5, row);
   	            worksheet4.addCell(label);

   	            /*------------------------  NEW BIG LINE-----------------*/
   	            row++;
   	            row++;
   	            col = -1;
   	            int count = 0;
   	           
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PROFILE_NAME,null);
   	            label = new Label(++col, row, keyName, times16format);
   	            worksheet4.addCell(label);
   	            count++;

   	           
   	            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PRODUCTCODE,null);
   	            label = new Label(++col, row, keyName, times16format);
   	            worksheet4.addCell(label);
   	            count++;
   	               row--;
   				   col++;
   				  int slabLengthOTF =(Integer) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION_SLABS, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE));			
   			        	
   		           	keyName=RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.COMMISSION_PROFILE_ADD_BASE_COMMISSION_OTF_DETAIL,null);
   		   	        label=new Label(col,row,keyName,times16format);
   		   	        worksheet4.mergeCells(col,row,col+2+slabLengthOTF*3,row);
   		   	        worksheet4.addCell(label);
   		   	        
   		   	        row++;
   		   	        col--;
   		   	        
   	            keyName=RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.COMMISSION_PROFILE_ADD_BASE_COMMISSION_OTF_APPLICABLE_FROM,null);
   			    label=new Label(++col,row,keyName,times16format);
   			    String commentOtfComm = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BASE_BATCH_ADD_COMM_PROFILE_OTF_COMMENT_FROM_DATE, args);
   		        cellFeatures = new WritableCellFeatures();
   		        cellFeatures.setComment(commentOtfComm);
   		        label.setCellFeatures(cellFeatures);
   		         worksheet4.addCell(label);
   			        
   			     keyName=RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.COMMISSION_PROFILE_ADD_BASE_COMMISSION_OTF_APPLICABLE_TO,null);
   			     label=new Label(++col,row,keyName,times16format);
   			     commentOtfComm = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BASE_BATCH_ADD_COMM_PROFILE_OTF_COMMENT_TO_DATE,args);
   		         cellFeatures = new WritableCellFeatures();
   		         cellFeatures.setComment(commentOtfComm);
   		         label.setCellFeatures(cellFeatures);
   		         worksheet4.addCell(label);
   			        
   			     keyName=RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.COMMISSION_PROFILE_ADD_BASE_COMMISSION_OTF_TIMESLAB,null);
   			     label=new Label(++col,row,keyName,times16format);
   			     commentOtfComm = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BASE_BATCH_ADD_COMM_PROFILE_OTF_COMMENT_TO_TIMESLAB,null);
   			     
   		         cellFeatures = new WritableCellFeatures();
   		         cellFeatures.setComment(commentOtfComm);
   		         label.setCellFeatures(cellFeatures);
   		         worksheet4.addCell(label);
   		         
   		         for(int i=1;i<=slabLengthOTF;i++){
   		 		        keyName=RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL_VALUE,null);
   		 		        label=new Label(++col,row,keyName+i,times16format);
   		 		       worksheet4.addCell(label);
   		 		        keyName=RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL_TYPE,null);
   		 		        label=new Label(++col,row,keyName+i,times16format);
   		 		        commentOtfComm = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_OTF_COMMENT_TO_TYPE,null);
   		 	            cellFeatures = new WritableCellFeatures();
   		 	            cellFeatures.setComment(commentOtfComm);
   		 	            label.setCellFeatures(cellFeatures);
   		 	           worksheet4.addCell(label);
   		 		        keyName=RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL_RATE,null);
   		 		        label=new Label(++col,row,keyName+i,times16format);
   		 		       worksheet4.addCell(label);
   		 	           }
   		         
   	            // /setting for the vertical freeze panes
   	            SheetSettings sheetSetting = new SheetSettings();
   	            sheetSetting = worksheet4.getSettings();
   	            sheetSetting.setVerticalFreeze(row + 1);

   	            // /setting for the horizontal freeze panes
   	            SheetSettings sheetSetting1;
   	            sheetSetting1 = worksheet4.getSettings();
   	            sheetSetting1.setHorizontalFreeze(count);
   	        }

   	        catch (WriteException e) {
   	            log.errorTrace(METHOD_NAME, e);
   	            log.error(METHOD_NAME, " Exception e: " + e.getMessage());
   	            throw e;
   	        } 
   	    }
   		
   		 private void writeInAddCommprofSheetFromRest(WritableSheet worksheet3, int col, int row, HashMap p_hashMap, String sequenceNo,Locale locale) throws Exception {
   		        final String METHOD_NAME = "writeInAddCommprofSheetFromRest";
   		        if (log.isDebugEnabled()) {
   		            log.debug(METHOD_NAME, " p_hashMap size=" + p_hashMap.size() + " p_locale: " + locale  + " col=" + col + " row=" + row);
   		        }
   		        try {
   		        	String[] args = {(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA)) == null ? PretupsI.DATE_FORMAT : ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA))) };
   		            String keyName;
   		            Label label;
   		            // 1
   		            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_ADDITIONAL_COMM_TEMPLATE_HEADING,null);
   		            label = new Label(col, row, keyName, times16format);
   		            worksheet3.mergeCells(col, row, col + 10, row);
   		            worksheet3.addCell(label);
   		            row++;
   		            col = 0;
   		            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DOWNLOADED_BY,null);
   		            label = new Label(col, row, keyName, times16format);
   		            worksheet3.addCell(label);
   		            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.BATCH_COMM_CREATED_BY));
   		            worksheet3.mergeCells(col, row, col + 1, row);
   		            worksheet3.addCell(label);
   		            row++;
   		            col = 0;
   		            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DOMAIN,null);
   		            label = new Label(col, row, keyName, times16format);
   		            worksheet3.addCell(label);

   		            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN));
   		            worksheet3.mergeCells(col, row, col + 1, row);
   		            worksheet3.addCell(label);

   		            row++;
   		            col = 0;
   		            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_CATEGORY,null);
   		            label = new Label(col, row, keyName, times16format);
   		            worksheet3.addCell(label);

   		            label = new Label(++col, row,String.valueOf(p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY)));
   		            worksheet3.mergeCells(col, row, col + 1, row);
   		            worksheet3.addCell(label);

   		            row++;
   		            col = 0;
   		            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_VERSION,null);
   		            label = new Label(col, row, keyName, times16format);
   		            worksheet3.addCell(label);

   		            label = new Label(++col, row, "1");
   		            worksheet3.mergeCells(col, row, col + 1, row);
   		            worksheet3.addCell(label);

   		            row++;
   		            col = 0;
   		            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_FIELDS_MENDATORY_WARNING,null);
   		            label = new Label(col, row, keyName, times16format);
   		            worksheet3.mergeCells(col, row, col + 5, row);
   		            worksheet3.addCell(label);
   		            /*------------------------  NEW BIG LINE-----------------*/
   		            row++;
   		            row++;
   		            col = -1;
   		            int count = 0;
   		            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PROFILE_NAME,null);
   		            label = new Label(++col, row, keyName, times16format);
   		            worksheet3.addCell(label);
   		            count++;
   		            // Added by Amit Raheja
   		            String comment = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_SYNC_COMMENT,null);
   		            cellFeatures = new WritableCellFeatures();
   		            cellFeatures.setComment(comment);
   		            // Addition ends
   		            String comment_add = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_SYNC_COMMENT_ADD,null);
   		            cellFeaturesAdd = new WritableCellFeatures();
   		            cellFeaturesAdd.setComment(comment_add);
   		            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_ADDITIONALCOMM_PROFILE_APPLICABLE_FROM,null);
   		            label = new Label(++col, row, keyName, times16format);
   		            label.setCellFeatures(cellFeaturesAdd);
   		            worksheet3.addCell(label);
   		            count++;
   		            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_ADDITIONALCOMM_PROFILE_APPLICABLE_TO,null);
   		            label = new Label(++col, row, keyName, times16format);
   		            label.setCellFeatures(cellFeaturesAdd);
   		            worksheet3.addCell(label);
   		            count++;
   		            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TIMESLAB,null);
   		            label = new Label(++col, row, keyName, times16format);
   		            worksheet3.addCell(label);
   		            count++;
   		            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_GATEWAY_CODE,null);
   		            label = new Label(++col, row, keyName, times16format);
   		            worksheet3.addCell(label);
   		            count++;
   		            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_SERVICE_CODE,null);
   		            label = new Label(++col, row, keyName, times16format);
   		            worksheet3.addCell(label);
   		            count++;
   		            if (!BTSLUtil.isNullString(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVC_PROD_MAPPING_ALLOWED)))) {
   		                keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_SUBSERVICE_CODE,null);
   		                label = new Label(++col, row, keyName, times16format);
   		                worksheet3.addCell(label);
   		                count++;
   		            }
   		            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_MIN_TRANSFER_VALUE,null);
   		            label = new Label(++col, row, keyName, times16format);
   		            label.setCellFeatures(cellFeatures);
   		            worksheet3.addCell(label);
   		            count++;
   		            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_MAX_TRANSFER_VALUE,null);
   		            label = new Label(++col, row, keyName, times16format);
   		            label.setCellFeatures(cellFeatures);
   		            worksheet3.addCell(label);
   		            count++;
   		            row--;
   		            row--;
   		            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_ASSIGN_ADDITIONAL_COMMISSION_SLABS,null);
   		            label = new Label(col + 1, row, keyName, times16format);
   		            worksheet3.mergeCells(col + 1, row, col + 10, row);
   		            worksheet3.addCell(label);
   		            row++;
   		            row++;
   		            row--;
   		            col++;
   		            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_FROM_RANGE,null);
   		            label = new Label(col, row, keyName, times16format);
   		            worksheet3.mergeCells(col, row++, col, row);
   		            worksheet3.addCell(label);
   		            row--;
   		            col++;
   		            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TO_RANGE,null);
   		            label = new Label(col, row, keyName, times16format);
   		            worksheet3.mergeCells(col, row++, col, row);
   		            worksheet3.addCell(label);
   		            row--;
   		            col++;
   		            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_COMMISSION,null);
   		            label = new Label(col, row, keyName, times16format);
   		            worksheet3.mergeCells(col++, row, col, row);
   		            worksheet3.addCell(label);
   		            boolean value = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOW_ROAM_ADDCOMM);
   		            if (value) {
   		                col = col + 1;
   		                keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_ROAM_COMMISSION,null);
   		                label = new Label(col, row, keyName, times16format);
   		                worksheet3.mergeCells(col++, row, col, row);
   		                worksheet3.addCell(label);
   		            }
   		            row++;
   		            // updated by akanksha FOR ETHIOPIA TELECOM
   		            if (value) {
   		                col = col - 3;
   		            } else {
   		                col = col - 1;
   		            }
   		            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TYPE,null);
   		            label = new Label(col, row, keyName, times16format);
   		            worksheet3.mergeCells(col, row, col, row);
   		            worksheet3.addCell(label);
   		            col++;
   		            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_RATE,null);
   		            label = new Label(col, row, keyName, times16format);
   		            worksheet3.mergeCells(col, row, col, row);
   		            worksheet3.addCell(label);

   		            if (value) {
   		                col++;
   		                keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TYPE,null);
   		                label = new Label(col, row, keyName, times16format);
   		                worksheet3.mergeCells(col, row, col, row);
   		                worksheet3.addCell(label);
   		                col++;
   		                keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_RATE,null);
   		                label = new Label(col, row, keyName, times16format);
   		                worksheet3.mergeCells(col, row, col, row);
   		                worksheet3.addCell(label);
   		            }

   		            row--;
   		            col++;
   		            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DIFFRENTIAL_FACTOR,null);
   		            label = new Label(col, row, keyName, times16format);
   		            worksheet3.mergeCells(col, row++, col, row);
   		            worksheet3.addCell(label);
   		            row--;
   		            col++;
   		            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAXES,null);
   		            label = new Label(col, row, keyName, times16format);
   		            worksheet3.mergeCells(col, row, col + 3, row);
   		            worksheet3.addCell(label);
   		            row++;
   		            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX1_TYPE,null);
   		            label = new Label(col, row, keyName, times16format);
   		            worksheet3.mergeCells(col, row, col, row);
   		            worksheet3.addCell(label);
   		            col++;
   		            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX1_RATE,null);
   		            label = new Label(col, row, keyName, times16format);
   		            worksheet3.mergeCells(col, row, col, row);
   		            worksheet3.addCell(label);
   		            col++;
   		            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX2_TYPE,null);
   		            label = new Label(col, row, keyName, times16format);
   		            worksheet3.mergeCells(col, row, col, row);
   		            worksheet3.addCell(label);
   		            col++;
   		            keyName = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX2_RATE,null);
   		            label = new Label(col, row, keyName, times16format);
   		            worksheet3.mergeCells(col, row, col, row);
   		            worksheet3.addCell(label);

   					
   				   if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue() && !"1".equals(sequenceNo)) {
   					   	row--;
   						col++;
   				        keyName=RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.PROFILE_COMMISSIONPROFILEDETAIL_LABEL_HEADING_MARGIN_OWNER,null);
   				        label=new Label(col,row,keyName,times16format);
   				        worksheet3.mergeCells(col,row,col+1,row);
   				        worksheet3.addCell(label);
   				        col = col+2;
   				        keyName=RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.PROFILE_COMMISSIONPROFILEDETAIL_LABEL_HEADING_TAXES_OWNER,null);
   				        label=new Label(col,row,keyName,times16format);
   				        worksheet3.mergeCells(col,row,col+3,row);
   				        worksheet3.addCell(label);
   			    
   					   row++;
   					   col =col- 3;
   					   keyName=RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_COMMISSION_TYPE,null);
   					   label=new Label(++col,row,keyName,times16format);
   					   worksheet3.addCell(label);
   					   keyName=RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_COMMISSION_RATE,null);
   					   label=new Label(++col,row,keyName,times16format);
   					   worksheet3.addCell(label);
   					   keyName=RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX1_TYPE,null);
   					   label=new Label(++col,row,keyName,times16format);
   					   worksheet3.addCell(label);
   					   keyName=RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX1_RATE,null);
   					   label=new Label(++col,row,keyName,times16format);
   					   worksheet3.addCell(label);
   					   keyName=RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX2_TYPE,null);
   					   label=new Label(++col,row,keyName,times16format);
   					   worksheet3.addCell(label);
   					   keyName=RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX2_RATE,null);
   					   label=new Label(++col,row,keyName,times16format);
   					   worksheet3.addCell(label);

   					   }
   				   if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))){
   			           
   					   row--;
   					   col++;
   					   int slabLengthOTF =(Integer) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION_SLABS, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE));			
   				        	
   			           	keyName=RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL,null);
   			   	        label=new Label(col,row,keyName,times16format);
   			   	        worksheet3.mergeCells(col,row,col+3+slabLengthOTF*3,row);
   			   	        worksheet3.addCell(label);
   			   	        
   			   	        row++;
   			   	        col--;
   			   	        
   			   	        keyName=RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_APPLICABLE_FROM,null);
   				        label=new Label(++col,row,keyName,times16format);
   				        comment = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BASE_BATCH_ADD_COMM_PROFILE_OTF_COMMENT_FROM_DATE, args);
   			            cellFeatures = new WritableCellFeatures();
   			            cellFeatures.setComment(comment);
   			            label.setCellFeatures(cellFeatures);
   				        worksheet3.addCell(label);
   				        
   				        keyName=RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_APPLICABLE_TO,null);
   				        label=new Label(++col,row,keyName,times16format);
   				        comment = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BASE_BATCH_ADD_COMM_PROFILE_OTF_COMMENT_TO_DATE, args);
   			            cellFeatures = new WritableCellFeatures();
   			            cellFeatures.setComment(comment);
   			            label.setCellFeatures(cellFeatures);
   				        worksheet3.addCell(label);
   				        
   				        keyName=RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_TIMESLAB,null);
   				        label=new Label(++col,row,keyName,times16format);
   				        comment = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BASE_BATCH_ADD_COMM_PROFILE_OTF_COMMENT_TO_TIMESLAB,null);
   			            cellFeatures = new WritableCellFeatures();
   			            cellFeatures.setComment(comment);
   			            label.setCellFeatures(cellFeatures);
   				        worksheet3.addCell(label);
   				        
   				        keyName=RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_TYPE,null);
   				        label=new Label(++col,row,keyName,times16format);
   				        comment = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_OTF_TYPE_ID_COMMENT,null);
   			            cellFeatures = new WritableCellFeatures();
   			            cellFeatures.setComment(comment);
   			            label.setCellFeatures(cellFeatures);
   				        worksheet3.addCell(label);
   				        
   				        for(int i=1;i<=slabLengthOTF;i++){
   				        keyName=RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL_VALUE,null);
   				        label=new Label(++col,row,keyName+i,times16format);
   				        worksheet3.addCell(label);
   				        keyName=RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL_TYPE,null);
   				        label=new Label(++col,row,keyName+i,times16format);
   				        comment = RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_OTF_COMMENT_TO_TYPE,null);
   			            cellFeatures = new WritableCellFeatures();
   			            cellFeatures.setComment(comment);
   			            label.setCellFeatures(cellFeatures);
   				        worksheet3.addCell(label);
   				        keyName=RestAPIStringParser.getMessage(locale , PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL_RATE,null);
   				        label=new Label(++col,row,keyName+i,times16format);
   				        worksheet3.addCell(label);
   			           }
   				   }
   		            // /setting for the vertical freeze panes
   		            SheetSettings sheetSetting = new SheetSettings();
   		            sheetSetting = worksheet3.getSettings();
   		            sheetSetting.setVerticalFreeze(row + 1);

   		            // /setting for the horizontal freeze panes
   		            SheetSettings sheetSetting1 = new SheetSettings();
   		            sheetSetting1 = worksheet3.getSettings();
   		            sheetSetting1.setHorizontalFreeze(count);

   		        } catch (RowsExceededException e) {
   		            log.errorTrace(METHOD_NAME, e);
   		            log.error(METHOD_NAME, " Exception e: " + e.getMessage());
   		            throw e;
   		        } catch (WriteException e) {
   		            log.errorTrace(METHOD_NAME, e);
   		            log.error(METHOD_NAME, " Exception e: " + e.getMessage());
   		            throw e;
   		        }
   		    }

    }


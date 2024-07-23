package com.btsl.pretups.xl;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.util.MessageResources;

import com.btsl.common.ListValueVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;
import com.restapi.networkadmin.commissionprofile.responseVO.BatchAddCommProfRespVO;

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


public class BatchAddCommProfExcelRW {

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
			final String methodName = "writeExcelForBatchAddCommProfileFromRest";
			if (log.isDebugEnabled()) {
				log.debug("writeExcelForBatchAddCommProfileFromRest", " p_locale: " + pLocale + " p_fileName: " + p_fileName);
			}
			WritableWorkbook workbook = null;
			WritableSheet worksheet1 = null;
			WritableSheet worksheet2 = null;
			WritableSheet worksheet3 = null;
			WritableSheet worksheet4 = null;

			int col = 0;
			int row = 0;

			pLocale = locale;

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
				row = this.writeProductListDataFromRest(worksheet2, col, row, p_hashMap);
				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD)))
						.booleanValue()) {
					row++;
					col = 0;
					row = this.writeTransactionTypeFromRest(worksheet2, col, row, p_hashMap);
				}
				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_MODE_ALWD))).booleanValue()) {
					row++;
					col = 0;
					row = this.writePaymentModeDataFromRest(worksheet2, col, row, p_hashMap);
				}
				row++;
				col = 0;
				row = this.writeDomainCategoryListDataFromRest(worksheet2, col, row, p_hashMap);
				row++;
				col = 0;
				row = this.writeServiceListDataFromRest(worksheet2, col, row, p_hashMap);
				row++;
				col = 0;
				if (!BTSLUtil.isNullString(
						((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVC_PROD_MAPPING_ALLOWED)))) {
					row = this.writeSubServiceListDataFromRest(worksheet2, col, row, p_hashMap);
					row++;
					col = 0;
				}

				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_COMMISSION,null);
				label = new Label(col, row, keyName, times16format);
				worksheet2.addCell(label);

				row++;
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_COMMISSION_NOTE,null);
				label = new Label(col, row, keyName);
				worksheet2.mergeCells(col, row, col + 3, row);
				worksheet2.addCell(label);

				row++;
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX_TYPE,null);
				label = new Label(col, row, keyName, times16format);
				worksheet2.addCell(label);

				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX_DESCIPTION,null);
				label = new Label(++col, row, keyName, times16format);
				worksheet2.addCell(label);

				row++;
				col = 0;
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_AMT,null);
				label = new Label(col, row, keyName);
				worksheet2.addCell(label);

				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_AMOUNT,null);
				label = new Label(++col, row, keyName);
				worksheet2.addCell(label);

				row++;
				col = 0;
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PCT,null);
				label = new Label(col, row, keyName);
				worksheet2.addCell(label);

				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PERCENTAGE,null);
				label = new Label(++col, row, keyName);
				worksheet2.addCell(label);

				// Added by Lalit//

				row++;
				col = 0;
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DUAL_PROFILE_TYPE,null);
				label = new Label(col, row, keyName, times16format);
				worksheet2.addCell(label);

				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DUAL_PROFILE_TYPE_DESCRIPTION,null);
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
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX_CAL_FOC_NOTE,null);
				label = new Label(col, row, keyName, times16format);
				worksheet2.mergeCells(col, row, col + 3, row);
				worksheet2.addCell(label);

				row++;
				col = 0;
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DUAL_PROFILE_TYPE_DESCRIPTION,null);
				label = new Label(col, row, keyName);
				worksheet2.mergeCells(col, row, col + 3, row);
				worksheet2.addCell(label);

				row++;
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX_CAL_FOC_NOTE,null);
				label = new Label(col, row, keyName);
				worksheet2.addCell(label);

				row++;
				row++;
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX_CAL_C2C_TRANSFER,null);
				label = new Label(col, row, keyName, times16format);
				worksheet2.mergeCells(col, row, col + 6, row);
				worksheet2.addCell(label);

				row++;
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX_CAL_C2C_TRANSFER_DESCRIPTION,null);
				label = new Label(col, row, keyName);
				worksheet2.mergeCells(col, row, col + 6, row);
				worksheet2.addCell(label);

				row++;
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX_CAL_C2C_TRANSFER_NOTE,null);
				label = new Label(col, row, keyName);
				worksheet2.addCell(label);

				row++;
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_COMMISSION_NOTE,null);
				label = new Label(col, row, keyName);
				worksheet2.mergeCells(col, row, col + 3, row);
				worksheet2.addCell(label);

				row++;
				row++;
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.PROFILE_ADDADDITIONALPROFILE_LABEL_TIMESLAB_EXAMPLE,null);
				label = new Label(col, row, keyName, times16format);
				worksheet2.addCell(label);

				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.PROFILE_ADDADDITIONALPROFILE_LABEL_TIMESLAB_EXAMPLE,null);
				label = new Label(++col, row, keyName, times16format);
				worksheet2.addCell(label);
				if ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,
						(String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))
						|| (Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,
								(String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))) {
					row++;
					row++;
					col = 0;
					keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL_COMMON,null);
					label = new Label(col, row, keyName, times16format);
					worksheet2.mergeCells(col, row, col + 1, row);
					worksheet2.addCell(label);

					row++;
					keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_OTF_COMMENT_TO_TYPE,null);
					label = new Label(col, row, keyName);
					worksheet2.addCell(label);

					if ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,
							(String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))) {
						row++;
						keyName = RestAPIStringParser.getMessage(pLocale,
								PretupsErrorCodesI.BASE_BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_OTF_TYPE_ID_COMMENT,null);
						label = new Label(col, row, keyName);
						worksheet2.addCell(label);

						row++;
						keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_MODIFY_MASTER_OTF_COMMENT_DATE_AND_TIME,null);
						label = new Label(col, row, keyName);
						worksheet2.addCell(label);
					} else if ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,
							(String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))) {
						row++;
						keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BASE_BATCHADDMODIFYCOMM_PROFILE_OTF_TYPE_ID_COMMENT,null);
						label = new Label(col, row, keyName);
						worksheet2.addCell(label);

						row++;
						keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BASE_BATCHADDMODIFYCOMM_PROFILE_OTF_DATE_TIME,null);
						label = new Label(col, row, keyName);
						worksheet2.addCell(label);
					}

				}

				col = 0;
				row = 0;
				this.writeInCommProfSheetFromRest(worksheet1, col, row, p_hashMap);
				if ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,
						(String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))
						|| (Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,
								(String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE)))
					this.writeInCommProfOTFSheetFromRest(worksheet4, col, row, p_hashMap);
				this.writeInAddCommprofSheetFromRest(worksheet3, col, row, p_hashMap, sequenceNo);
				workbook.write();

			} catch (Exception e) {
				log.errorTrace(methodName, e);
				log.error("writeExcelForBatchAddCommProfileFromRest", " Exception e: " + e.getMessage());
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
					log.debug("writeExcelForBatchAddCommProfileFromRest", " Exiting");
				}
			}
		}
		
		private int writeTransactionTypeFromRest(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) {
			final String METHOD_NAME = "writeTransactionTypeFromRest";
			if (log.isDebugEnabled()) {
				log.debug("writeTransactionTypeFromRest",
						" p_hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
			}
			try {
				String keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TRANSACTION_TYPE,null);
				Label label = new Label(col, row, keyName, times16format);
				worksheet2.mergeCells(col, row, col + 2, row);
				worksheet2.addCell(label);
				row++;
				col = 0;
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TRANSACTION_TYPE_COMMENT,null);
				label = new Label(col, row, keyName, times16format);
				worksheet2.addCell(label);
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TRANSACTION_DESCRIPTION,null);
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
				log.error("writeProductListDataFromRest", " Exception e: " + e.getMessage());
			}
			return row;
		}
		
		private int writeProductListDataFromRest(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) {
			final String METHOD_NAME = "writeProductListDataFromRest";
			if (log.isDebugEnabled()) {
				log.debug("writeProductListDataFromRest",
						" p_hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
			}
			try {
				String keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PRODUCT_HEADING,null);
				Label label = new Label(col, row, keyName, times16format);
				worksheet2.mergeCells(col, row, col + 2, row);
				worksheet2.addCell(label);
				row++;
				col = 0;
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PRODUCT_HEADING_NOTE,null);
				label = new Label(col, row, keyName);
				worksheet2.mergeCells(col, row, 10, row);
				worksheet2.addCell(label);
				row++;
				col = 0;
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PRODUCTCODE,null);
				label = new Label(col, row, keyName, times16format);
				worksheet2.addCell(label);
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PRODUCTNAME,null);
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
				log.error("writeProductListDataFromRest", " Exception e: " + e.getMessage());
			}
			return row;
		}
		
		private int writePaymentModeDataFromRest(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) {
			final String METHOD_NAME = "writePaymentModeDataFromRest";
			if (log.isDebugEnabled()) {
				log.debug("writePaymentModeDataFromRest",
						" p_hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
			}
			try {
				String keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PAYMENT_HEADING,null);
				Label label = new Label(col, row, keyName, times16format);
				worksheet2.mergeCells(col, row, col + 2, row);
				worksheet2.addCell(label);
				row++;
				col = 0;
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PAYMENTCODE,null);
				label = new Label(col, row, keyName, times16format);
				worksheet2.addCell(label);
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PAYMENTNAME,null);
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

		private int writeDomainCategoryListDataFromRest(WritableSheet worksheet2, int col, int row, HashMap pHashMap) {
			final String METHOD_NAME = "writeDomainCategoryListData";
			if (log.isDebugEnabled()) {
				log.debug("writeDomainCategoryListData",
						" p_hashMap size=" + pHashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
			}

			try {
				String keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DOMAIN_CATEGORY_HEADING,null);
				Label label = new Label(col, row, keyName, times16format);
				worksheet2.mergeCells(col, row, col + 2, row);
				worksheet2.addCell(label);
				row++;
				col = 0;
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DOMAIN_CATEGORY_HEADING_NOTE,null);
				label = new Label(col, row, keyName);
				worksheet2.mergeCells(col, row, 10, row);
				worksheet2.addCell(label);
				row++;
				col = 0;

				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DOMAINCODE,null);
				label = new Label(col, row, keyName, times16format);
				worksheet2.addCell(label);
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DOMAINNAME,null);
				label = new Label(++col, row, keyName, times16format);
				worksheet2.addCell(label);
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_CATEGORYCODE,null);
				label = new Label(++col, row, keyName, times16format);
				worksheet2.addCell(label);
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_CATEGORYNAME,null);
				label = new Label(++col, row, keyName, times16format);
				worksheet2.addCell(label);

				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_GRPHDOMAINCODE,null);
				label = new Label(++col, row, keyName, times16format);
				worksheet2.addCell(label);
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_GRPHDOMAINNAME,null);
				label = new Label(++col, row, keyName, times16format);
				worksheet2.addCell(label);
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_GEADECODE,null);
				label = new Label(++col, row, keyName, times16format);
				worksheet2.addCell(label);
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_GEADENAME,null);
				label = new Label(++col, row, keyName, times16format);
				worksheet2.addCell(label);
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_GATEWAYCODE,null);
				label = new Label(++col, row, keyName, times16format);
				worksheet2.addCell(label);
				keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_GATEWAYNAME,null);
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
				log.error("writeDomainCategoryListDataFromRest", " Exception e: " + e.getMessage());
			}
			return row;
		}
		private int writeServiceListDataFromRest(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
			final String methodName = "writeServiceListDataFromRest";
			if (log.isDebugEnabled()) {
				log.debug("writeServiceListDataFromRest",
						" p_hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
			}
			try {
				String keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_SERVICE_TYPE_HEADING,null);
				Label label = new Label(col, row, keyName, times16format);
				worksheet2.mergeCells(col, row, col + 2, row);
				worksheet2.addCell(label);
				// added by harsh
				row++;
				String keyName1 = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_MASTERSHEET_SERVICETYPE,null);
				label = new Label(col, row, keyName1, times16format);
				worksheet2.addCell(label);
				col++;
				String keyName2 = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_MASTERSHEET_SERVICENAME,null);
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
				log.error("writeServiceListDataFromRest", " Exception e: " + e.getMessage());
				throw e;
			}

		}
		
		private int writeSubServiceListDataFromRest(WritableSheet worksheet2, int col, int row, HashMap p_hashMap)
				throws Exception {

			final String methodName = "writeSubServiceListDataFromRest";
			if (log.isDebugEnabled()) {
				log.debug("writeSubServiceListDataFromRest",
						" p_hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
			}
			try {
				String keyName = RestAPIStringParser.getMessage(pLocale,
						PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_SUBSERVICE_TYPE_HEADING, null);
				Label label = new Label(col, row, keyName, times16format);
				worksheet2.mergeCells(col, row, col + 2, row);
				worksheet2.addCell(label);
				row++;
				keyName = RestAPIStringParser.getMessage(pLocale,
						PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_SUBSERVICE_TYPE_NOTE, null);
				label = new Label(col, row, keyName);
				worksheet2.mergeCells(col, row, col + 2, row);
				worksheet2.addCell(label);
				row++;
				keyName = RestAPIStringParser.getMessage(pLocale,
						PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_MASTERSHEET_SERVICETYPE, null);
				label = new Label(col, row, keyName, times16format);
				worksheet2.addCell(label);
				keyName = RestAPIStringParser.getMessage(pLocale,
						PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_MASTERSHEET_SUBSERVICE_TYPE, null);
				label = new Label(++col, row, keyName, times16format);
				worksheet2.addCell(label);
				keyName = RestAPIStringParser.getMessage(pLocale,
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
				log.errorTrace(methodName, e);
				log.error("writeSubServiceListDataFromRest", " Exception e: " + e.getMessage());
				throw e;
			}
		}
		
		private void writeInCommProfSheetFromRest(WritableSheet worksheet1, int col, int row, HashMap p_hashMap) throws Exception {
	        final String methodName = "writeInCommProfSheetFromRest";
	        if (log.isDebugEnabled()) {
	            log.debug("writeInCommProfSheetFromRest", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
	        }
	        try {
	        	String[] args = { (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA)) == null ? PretupsI.DATE_FORMAT : ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA))) };
	        	String keyName;
	            Label label;
	            // 1
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_COMM_TEMPLATE_HEADING,null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet1.mergeCells(col, row, col + 10, row);
	            worksheet1.addCell(label);
	            row++;
	            col = 0;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DOWNLOADED_BY,null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet1.addCell(label);
	            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.BATCH_COMM_CREATED_BY));
	            worksheet1.mergeCells(col, row, col + 1, row);
	            worksheet1.addCell(label);
	            row++;
	            col = 0;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DOMAINNAME,null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet1.addCell(label);

	            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN));
	            worksheet1.mergeCells(col, row, col + 1, row);
	            worksheet1.addCell(label);

	            row++;
	            col = 0;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_CATEGORY,null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet1.addCell(label);

	            String keyValString=String.valueOf(p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY));
	            label = new Label(++col, row, keyValString, times16format);
	            worksheet1.mergeCells(col, row, col + 1, row);
	            worksheet1.addCell(label);

	            row++;
	            col = 0;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_VERSION,null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet1.addCell(label);

	            label = new Label(++col, row, "1");
	            worksheet1.mergeCells(col, row, col + 1, row);
	            worksheet1.addCell(label);

	            row++;
	            col = 0;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_FIELDS_MENDATORY_WARNING,null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet1.mergeCells(col, row, col + 5, row);
	            worksheet1.addCell(label);

	            /*------------------------  NEW BIG LINE-----------------*/
	            row++;
	            row++;
	            col = -1;
	            int count = 0;
	            if ("ALL".equals((String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN_CODE))) {
	                keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DOMAINCODE,null);
	                label = new Label(++col, row, keyName, times16format);
	                worksheet1.addCell(label);
	                count++;
	            }
	            if ("ALL".equals((String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY_CODE))) {
	                keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_CATEGORYCODE,null);
	                label = new Label(++col, row, keyName, times16format);
	                worksheet1.addCell(label);
	                count++;
	            }
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_GRPHDOMAINCODE,null);
	            label = new Label(++col, row, keyName, times16format);
	            worksheet1.addCell(label);
	            count++;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_GEADECODE,null);
	            label = new Label(++col, row, keyName, times16format);
	            worksheet1.addCell(label);
	            count++;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PROFILE_NAME,null);
	            label = new Label(++col, row, keyName, times16format);
	            worksheet1.addCell(label);
	            count++;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_SHORT_CODE,null);
	            label = new Label(++col, row, keyName, times16format);
	            worksheet1.addCell(label);
	            count++;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DUAL_PROFILE,null);
	            label = new Label(++col, row, keyName, times16format);
	            worksheet1.addCell(label);
	            count++;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_APPLICABLE_FROM,null);
	            label = new Label(++col, row, keyName, times16format);
	            // Added by Amit Raheja
	            String comment_from = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_APPLICABLE_FROM_COMMENT, args);
	            cellFeatures = new WritableCellFeatures();
	            cellFeatures.setComment(comment_from);
	            label.setCellFeatures(cellFeatures);
	            // Addition ends
	            worksheet1.addCell(label);
	            count++;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_APPLICABLE_TIME,null);
	            label = new Label(++col, row, keyName, times16format);
	            // Added by Amit Raheja
	            String comment_time = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_APPLICABLE_TIME_COMMENT,null);
	            cellFeatures = new WritableCellFeatures();
	            cellFeatures.setComment(comment_time);
	            label.setCellFeatures(cellFeatures);
	            // Addition ends
	            worksheet1.addCell(label);
	            count++;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PRODUCTCODE,null);
	            label = new Label(++col, row, keyName, times16format);
	            worksheet1.addCell(label);
	            count++;
	            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue()){
	            	keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TRANSACTION_TYPE,null);
	            	label = new Label(++col, row, keyName, times16format);
	            	worksheet1.addCell(label);
	                String commentType = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TRANSACTION_TYPE_COMMENT,null);
	                cellFeatures = new WritableCellFeatures();
	                cellFeatures.setComment(commentType);
	                label.setCellFeatures(cellFeatures);
	                count++;
	            }
	            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PAYMENT_MODE_ALWD))).booleanValue()){
	            	keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PAYMENTCODE,null);
	            	label = new Label(++col, row, keyName, times16format);
	            	worksheet1.addCell(label);
	            	count++;
	            }
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_MULTIPLE_OF,null);
	            label = new Label(++col, row, keyName, times16format);
	            worksheet1.addCell(label);
	            count++;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_MIN_TRANSFER_VALUE,null);
	            label = new Label(++col, row, keyName, times16format);
	            worksheet1.addCell(label);
	            count++;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_MAX_TRANSFER_VALUE,null);
	            label = new Label(++col, row, keyName, times16format);
	            worksheet1.addCell(label);
	            count++;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX_CAL_FOC,null);
	            label = new Label(++col, row, keyName, times16format);
	            worksheet1.addCell(label);
	            count++;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX_CAL_C2C_TRANSFER,null);
	            label = new Label(++col, row, keyName, times16format);
	            worksheet1.addCell(label);
	            count++;
	            row--;
	            row--;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_ASSIGN_COMMISSION_SLABS,null);
	            label = new Label(col + 1, row, keyName, times16format);
	            worksheet1.mergeCells(col + 1, row, col + 10, row);
	            worksheet1.addCell(label);
	            row++;
	            row++;
	            row--;
	            col++;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_FROM_RANGE,null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet1.mergeCells(col, row++, col, row);
	            worksheet1.addCell(label);
	            row--;
	            col++;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TO_RANGE,null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet1.mergeCells(col, row++, col, row);
	            worksheet1.addCell(label);
	            row--;
	            col++;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_COMMISSION,null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet1.mergeCells(col++, row, col, row);
	            worksheet1.addCell(label);
	            row++;
	            col--;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TYPE,null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet1.mergeCells(col, row, col, row);
	            worksheet1.addCell(label);
	            col++;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_RATE,null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet1.mergeCells(col, row, col, row);
	            worksheet1.addCell(label);
	            row--;
	            col++;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAXES,null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet1.mergeCells(col, row, col + 5, row);
	            worksheet1.addCell(label);
	            row++;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX1_TYPE,null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet1.mergeCells(col, row, col, row);
	            worksheet1.addCell(label);
	            col++;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX1_RATE,null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet1.mergeCells(col, row, col, row);
	            worksheet1.addCell(label);
	            col++;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX2_TYPE,null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet1.mergeCells(col, row, col, row);
	            worksheet1.addCell(label);
	            col++;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX2_RATE,null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet1.mergeCells(col, row, col, row);
	            worksheet1.addCell(label);
	            col++;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX3_TYPE,null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet1.mergeCells(col, row, col, row);
	            worksheet1.addCell(label);

	            col++;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX3_RATE,null);
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
	            log.error("writeInCommProfSheetFromRest", " Exception e: " + e.getMessage());
	            throw e;
	        } 
	    }
		
		private void writeInCommProfOTFSheetFromRest(WritableSheet worksheet4, int col, int row, HashMap p_hashMap) throws Exception {
	        final String methodName = "writeInCommProfSheetFromRest";
	        if (log.isDebugEnabled()) {
	            log.debug("writeInCommProfOTFSheetFromRest", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
	        }
	        try {
	        	String[] args = { (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA)) == null ? PretupsI.DATE_FORMAT : ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA))) };
	        	String keyName;
	            Label label;
	            // 1
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_OTF_COMM_TEMPLATE_HEADING,null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet4.mergeCells(col, row, col + 10, row);
	            worksheet4.addCell(label);
	            row++;
	            col = 0;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DOWNLOADED_BY,null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet4.addCell(label);
	            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.BATCH_COMM_CREATED_BY));
	            worksheet4.mergeCells(col, row, col + 1, row);
	            worksheet4.addCell(label);
	            row++;
	            col = 0;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DOMAIN,null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet4.addCell(label);

	            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN));
	            worksheet4.mergeCells(col, row, col + 1, row);
	            worksheet4.addCell(label);

	            row++;
	            col = 0;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_CATEGORY,null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet4.addCell(label);

	            label = new Label(++col, row, String.valueOf(p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY)));
	            worksheet4.mergeCells(col, row, col + 1, row);
	            worksheet4.addCell(label);

	            row++;
	            col = 0;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_VERSION,null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet4.addCell(label);

	            label = new Label(++col, row, "1");
	            worksheet4.mergeCells(col, row, col + 1, row);
	            worksheet4.addCell(label);

	            row++;
	            col = 0;
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_FIELDS_MENDATORY_WARNING,null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet4.mergeCells(col, row, col + 5, row);
	            worksheet4.addCell(label);

	            /*------------------------  NEW BIG LINE-----------------*/
	            row++;
	            row++;
	            col = -1;
	            int count = 0;
	           
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PROFILE_NAME,null);
	            label = new Label(++col, row, keyName, times16format);
	            worksheet4.addCell(label);
	            count++;

	           
	            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PRODUCTCODE,null);
	            label = new Label(++col, row, keyName, times16format);
	            worksheet4.addCell(label);
	            count++;
	               row--;
				   col++;
				  int slabLengthOTF =(Integer) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION_SLABS, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE));			
			        	
		           	keyName=RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_BASE_COMMISSION_OTF_DETAIL,null);
		   	        label=new Label(col,row,keyName,times16format);
		   	        worksheet4.mergeCells(col,row,col+2+slabLengthOTF*3,row);
		   	        worksheet4.addCell(label);
		   	        
		   	        row++;
		   	        col--;
		   	        
	            keyName=RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_BASE_COMMISSION_OTF_APPLICABLE_FROM,null);
			    label=new Label(++col,row,keyName,times16format);
			    String commentOtfComm = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BASE_BATCH_ADD_COMM_PROFILE_OTF_COMMENT_FROM_DATE, args);
		        cellFeatures = new WritableCellFeatures();
		        cellFeatures.setComment(commentOtfComm);
		        label.setCellFeatures(cellFeatures);
		         worksheet4.addCell(label);
			        
			     keyName=RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_BASE_COMMISSION_OTF_APPLICABLE_TO,null);
			     label=new Label(++col,row,keyName,times16format);
			     commentOtfComm = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BASE_BATCH_ADD_COMM_PROFILE_OTF_COMMENT_TO_DATE,args);
		         cellFeatures = new WritableCellFeatures();
		         cellFeatures.setComment(commentOtfComm);
		         label.setCellFeatures(cellFeatures);
		         worksheet4.addCell(label);
			        
			     keyName=RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_BASE_COMMISSION_OTF_TIMESLAB,null);
			     label=new Label(++col,row,keyName,times16format);
			     commentOtfComm = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BASE_BATCH_ADD_COMM_PROFILE_OTF_COMMENT_TO_TIMESLAB,null);
			     
		         cellFeatures = new WritableCellFeatures();
		         cellFeatures.setComment(commentOtfComm);
		         label.setCellFeatures(cellFeatures);
		         worksheet4.addCell(label);
		         
		         for(int i=1;i<=slabLengthOTF;i++){
		 		        keyName=RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL_VALUE,null);
		 		        label=new Label(++col,row,keyName+i,times16format);
		 		       worksheet4.addCell(label);
		 		        keyName=RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL_TYPE,null);
		 		        label=new Label(++col,row,keyName+i,times16format);
		 		        commentOtfComm = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_OTF_COMMENT_TO_TYPE,null);
		 	            cellFeatures = new WritableCellFeatures();
		 	            cellFeatures.setComment(commentOtfComm);
		 	            label.setCellFeatures(cellFeatures);
		 	           worksheet4.addCell(label);
		 		        keyName=RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL_RATE,null);
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
	            log.error("writeInCommProfOTFSheetFromRest", " Exception e: " + e.getMessage());
	            throw e;
	        } 
	    }
		
		 private void writeInAddCommprofSheetFromRest(WritableSheet worksheet3, int col, int row, HashMap p_hashMap, String sequenceNo) throws Exception {
		        final String methodName = "writeInAddCommprofSheetFromRest";
		        if (log.isDebugEnabled()) {
		            log.debug("writeInAddCommprofSheetFromRest", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
		        }
		        try {
		        	String[] args = {(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA)) == null ? PretupsI.DATE_FORMAT : ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_FORMAT_CAL_JAVA))) };
		            String keyName;
		            Label label;
		            // 1
		            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_ADDITIONAL_COMM_TEMPLATE_HEADING,null);
		            label = new Label(col, row, keyName, times16format);
		            worksheet3.mergeCells(col, row, col + 10, row);
		            worksheet3.addCell(label);
		            row++;
		            col = 0;
		            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DOWNLOADED_BY,null);
		            label = new Label(col, row, keyName, times16format);
		            worksheet3.addCell(label);
		            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.BATCH_COMM_CREATED_BY));
		            worksheet3.mergeCells(col, row, col + 1, row);
		            worksheet3.addCell(label);
		            row++;
		            col = 0;
		            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DOMAIN,null);
		            label = new Label(col, row, keyName, times16format);
		            worksheet3.addCell(label);

		            label = new Label(++col, row, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_DOMAIN));
		            worksheet3.mergeCells(col, row, col + 1, row);
		            worksheet3.addCell(label);

		            row++;
		            col = 0;
		            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_CATEGORY,null);
		            label = new Label(col, row, keyName, times16format);
		            worksheet3.addCell(label);

		            label = new Label(++col, row,String.valueOf(p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_CATEGORY)));
		            worksheet3.mergeCells(col, row, col + 1, row);
		            worksheet3.addCell(label);

		            row++;
		            col = 0;
		            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_VERSION,null);
		            label = new Label(col, row, keyName, times16format);
		            worksheet3.addCell(label);

		            label = new Label(++col, row, "1");
		            worksheet3.mergeCells(col, row, col + 1, row);
		            worksheet3.addCell(label);

		            row++;
		            col = 0;
		            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_FIELDS_MENDATORY_WARNING,null);
		            label = new Label(col, row, keyName, times16format);
		            worksheet3.mergeCells(col, row, col + 5, row);
		            worksheet3.addCell(label);
		            /*------------------------  NEW BIG LINE-----------------*/
		            row++;
		            row++;
		            col = -1;
		            int count = 0;
		            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_PROFILE_NAME,null);
		            label = new Label(++col, row, keyName, times16format);
		            worksheet3.addCell(label);
		            count++;
		            // Added by Amit Raheja
		            String comment = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_SYNC_COMMENT,null);
		            cellFeatures = new WritableCellFeatures();
		            cellFeatures.setComment(comment);
		            // Addition ends
		            String comment_add = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_SYNC_COMMENT_ADD,null);
		            cellFeaturesAdd = new WritableCellFeatures();
		            cellFeaturesAdd.setComment(comment_add);
		            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_ADDITIONALCOMM_PROFILE_APPLICABLE_FROM,null);
		            label = new Label(++col, row, keyName, times16format);
		            label.setCellFeatures(cellFeaturesAdd);
		            worksheet3.addCell(label);
		            count++;
		            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_ADDITIONALCOMM_PROFILE_APPLICABLE_TO,null);
		            label = new Label(++col, row, keyName, times16format);
		            label.setCellFeatures(cellFeaturesAdd);
		            worksheet3.addCell(label);
		            count++;
		            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TIMESLAB,null);
		            label = new Label(++col, row, keyName, times16format);
		            worksheet3.addCell(label);
		            count++;
		            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_GATEWAY_CODE,null);
		            label = new Label(++col, row, keyName, times16format);
		            worksheet3.addCell(label);
		            count++;
		            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_SERVICE_CODE,null);
		            label = new Label(++col, row, keyName, times16format);
		            worksheet3.addCell(label);
		            count++;
		            if (!BTSLUtil.isNullString(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SRVC_PROD_MAPPING_ALLOWED)))) {
		                keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_SUBSERVICE_CODE,null);
		                label = new Label(++col, row, keyName, times16format);
		                worksheet3.addCell(label);
		                count++;
		            }
		            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_MIN_TRANSFER_VALUE,null);
		            label = new Label(++col, row, keyName, times16format);
		            label.setCellFeatures(cellFeatures);
		            worksheet3.addCell(label);
		            count++;
		            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_MAX_TRANSFER_VALUE,null);
		            label = new Label(++col, row, keyName, times16format);
		            label.setCellFeatures(cellFeatures);
		            worksheet3.addCell(label);
		            count++;
		            row--;
		            row--;
		            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_ASSIGN_ADDITIONAL_COMMISSION_SLABS,null);
		            label = new Label(col + 1, row, keyName, times16format);
		            worksheet3.mergeCells(col + 1, row, col + 10, row);
		            worksheet3.addCell(label);
		            row++;
		            row++;
		            row--;
		            col++;
		            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_FROM_RANGE,null);
		            label = new Label(col, row, keyName, times16format);
		            worksheet3.mergeCells(col, row++, col, row);
		            worksheet3.addCell(label);
		            row--;
		            col++;
		            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TO_RANGE,null);
		            label = new Label(col, row, keyName, times16format);
		            worksheet3.mergeCells(col, row++, col, row);
		            worksheet3.addCell(label);
		            row--;
		            col++;
		            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_COMMISSION,null);
		            label = new Label(col, row, keyName, times16format);
		            worksheet3.mergeCells(col++, row, col, row);
		            worksheet3.addCell(label);
		            boolean value = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOW_ROAM_ADDCOMM);
		            if (value) {
		                col = col + 1;
		                keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_ROAM_COMMISSION,null);
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
		            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TYPE,null);
		            label = new Label(col, row, keyName, times16format);
		            worksheet3.mergeCells(col, row, col, row);
		            worksheet3.addCell(label);
		            col++;
		            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_RATE,null);
		            label = new Label(col, row, keyName, times16format);
		            worksheet3.mergeCells(col, row, col, row);
		            worksheet3.addCell(label);

		            if (value) {
		                col++;
		                keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TYPE,null);
		                label = new Label(col, row, keyName, times16format);
		                worksheet3.mergeCells(col, row, col, row);
		                worksheet3.addCell(label);
		                col++;
		                keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_RATE,null);
		                label = new Label(col, row, keyName, times16format);
		                worksheet3.mergeCells(col, row, col, row);
		                worksheet3.addCell(label);
		            }

		            row--;
		            col++;
		            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_DIFFRENTIAL_FACTOR,null);
		            label = new Label(col, row, keyName, times16format);
		            worksheet3.mergeCells(col, row++, col, row);
		            worksheet3.addCell(label);
		            row--;
		            col++;
		            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAXES,null);
		            label = new Label(col, row, keyName, times16format);
		            worksheet3.mergeCells(col, row, col + 3, row);
		            worksheet3.addCell(label);
		            row++;
		            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX1_TYPE,null);
		            label = new Label(col, row, keyName, times16format);
		            worksheet3.mergeCells(col, row, col, row);
		            worksheet3.addCell(label);
		            col++;
		            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX1_RATE,null);
		            label = new Label(col, row, keyName, times16format);
		            worksheet3.mergeCells(col, row, col, row);
		            worksheet3.addCell(label);
		            col++;
		            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX2_TYPE,null);
		            label = new Label(col, row, keyName, times16format);
		            worksheet3.mergeCells(col, row, col, row);
		            worksheet3.addCell(label);
		            col++;
		            keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_TAX2_RATE,null);
		            label = new Label(col, row, keyName, times16format);
		            worksheet3.mergeCells(col, row, col, row);
		            worksheet3.addCell(label);

					
				   if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OWNER_COMMISION_ALLOWED))).booleanValue() && !"1".equals(sequenceNo)) {
					   	row--;
						col++;
				        keyName=RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.PROFILE_COMMISSIONPROFILEDETAIL_LABEL_HEADING_MARGIN_OWNER,null);
				        label=new Label(col,row,keyName,times16format);
				        worksheet3.mergeCells(col,row,col+1,row);
				        worksheet3.addCell(label);
				        col = col+2;
				        keyName=RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.PROFILE_COMMISSIONPROFILEDETAIL_LABEL_HEADING_TAXES_OWNER,null);
				        label=new Label(col,row,keyName,times16format);
				        worksheet3.mergeCells(col,row,col+3,row);
				        worksheet3.addCell(label);
			    
					   row++;
					   col =col- 3;
					   keyName=RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_COMMISSION_TYPE,null);
					   label=new Label(++col,row,keyName,times16format);
					   worksheet3.addCell(label);
					   keyName=RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_COMMISSION_RATE,null);
					   label=new Label(++col,row,keyName,times16format);
					   worksheet3.addCell(label);
					   keyName=RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX1_TYPE,null);
					   label=new Label(++col,row,keyName,times16format);
					   worksheet3.addCell(label);
					   keyName=RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX1_RATE,null);
					   label=new Label(++col,row,keyName,times16format);
					   worksheet3.addCell(label);
					   keyName=RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX2_TYPE,null);
					   label=new Label(++col,row,keyName,times16format);
					   worksheet3.addCell(label);
					   keyName=RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_TAX2_RATE,null);
					   label=new Label(++col,row,keyName,times16format);
					   worksheet3.addCell(label);

					   }
				   if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE))){
			           
					   row--;
					   col++;
					   int slabLengthOTF =(Integer) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION_SLABS, (String) p_hashMap.get(PretupsI.BATCH_MODIFY_COMM_PROFILE_NETWORK_CODE));			
				        	
			           	keyName=RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL,null);
			   	        label=new Label(col,row,keyName,times16format);
			   	        worksheet3.mergeCells(col,row,col+3+slabLengthOTF*3,row);
			   	        worksheet3.addCell(label);
			   	        
			   	        row++;
			   	        col--;
			   	        
			   	        keyName=RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_APPLICABLE_FROM,null);
				        label=new Label(++col,row,keyName,times16format);
				        comment = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BASE_BATCH_ADD_COMM_PROFILE_OTF_COMMENT_FROM_DATE, args);
			            cellFeatures = new WritableCellFeatures();
			            cellFeatures.setComment(comment);
			            label.setCellFeatures(cellFeatures);
				        worksheet3.addCell(label);
				        
				        keyName=RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_APPLICABLE_TO,null);
				        label=new Label(++col,row,keyName,times16format);
				        comment = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BASE_BATCH_ADD_COMM_PROFILE_OTF_COMMENT_TO_DATE, args);
			            cellFeatures = new WritableCellFeatures();
			            cellFeatures.setComment(comment);
			            label.setCellFeatures(cellFeatures);
				        worksheet3.addCell(label);
				        
				        keyName=RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_TIMESLAB,null);
				        label=new Label(++col,row,keyName,times16format);
				        comment = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BASE_BATCH_ADD_COMM_PROFILE_OTF_COMMENT_TO_TIMESLAB,null);
			            cellFeatures = new WritableCellFeatures();
			            cellFeatures.setComment(comment);
			            label.setCellFeatures(cellFeatures);
				        worksheet3.addCell(label);
				        
				        keyName=RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_TYPE,null);
				        label=new Label(++col,row,keyName,times16format);
				        comment = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCHMODIFYCOMMPROFILE_COMMISSION_PROFILE_OTF_TYPE_ID_COMMENT,null);
			            cellFeatures = new WritableCellFeatures();
			            cellFeatures.setComment(comment);
			            label.setCellFeatures(cellFeatures);
				        worksheet3.addCell(label);
				        
				        for(int i=1;i<=slabLengthOTF;i++){
				        keyName=RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL_VALUE,null);
				        label=new Label(++col,row,keyName+i,times16format);
				        worksheet3.addCell(label);
				        keyName=RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL_TYPE,null);
				        label=new Label(++col,row,keyName+i,times16format);
				        comment = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.BATCH_ADD_COMM_PROFILE_OTF_COMMENT_TO_TYPE,null);
			            cellFeatures = new WritableCellFeatures();
			            cellFeatures.setComment(comment);
			            label.setCellFeatures(cellFeatures);
				        worksheet3.addCell(label);
				        keyName=RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.COMMISSION_PROFILE_ADD_ADDITIONAL_COMMISSION_OTF_DETAIL_RATE,null);
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
		            log.error("writeInAddCommprofSheetFromRest", " Exception e: " + e.getMessage());
		            throw e;
		        } catch (WriteException e) {
		            log.errorTrace(methodName, e);
		            log.error("writeInAddCommprofSheetFromRest", " Exception e: " + e.getMessage());
		            throw e;
		        }
		    }

}
	


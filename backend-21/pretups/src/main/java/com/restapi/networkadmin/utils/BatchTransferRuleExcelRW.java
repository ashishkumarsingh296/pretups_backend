package com.restapi.networkadmin.utils;

import java.io.File;
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
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.servicegpmgt.businesslogic.ServiceGpMgmtVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.xl.ExcelFileConstants;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class BatchTransferRuleExcelRW {

	
	
	
	    private Log _log = LogFactory.getLog(this.getClass().getName());
	    private WritableFont times12font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, true);
	    private WritableCellFormat times12format = new WritableCellFormat(times12font);
	    private WritableFont times16font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, true);
	    private WritableCellFormat times16format = new WritableCellFormat(times16font);
	    private MessageResources p_messages = null;
	    private Locale p_locale = null;


	    public void writeExcel(String p_excelID, HashMap p_hashMap, MessageResources messages, Locale p_locale1, String promotionLevel, String p_fileName, String p_dateRange) {
	        final String METHOD_NAME = "writeExcel";
	        if (_log.isDebugEnabled()) {
	            _log.debug("writeExcel", " p_excelID: " + p_excelID + " p_hashMap:" + p_hashMap + " p_locale: " + p_locale + " p_fileName: " + p_fileName);
	        }
	        WritableWorkbook workbook = null;
	        WritableSheet worksheet1 = null;
	        WritableSheet worksheet2 = null;
	        int col = 0;
	        int row = 0;

	        try {
	            workbook = Workbook.createWorkbook(new File(p_fileName));
	            worksheet1 = workbook.createSheet("Data Sheet", 0);
	            worksheet2 = workbook.createSheet("Master Sheet", 1);
	            p_messages = messages;
	            p_locale = p_locale1;
	            Label label = null;
	            String keyName = null;

	            // for Promotional transfer rule master file
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PROMOTIONAL_TRANSFER_RULE_DATA_FILE, null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet2.mergeCells(col, row, col + 5, row);
	            worksheet2.addCell(label);
	            row++;
	            row++;
	            col = 0;
	          
	            row = this.writeCardGroupDescription(worksheet2, col, row, p_hashMap);
	            row++;
	            row++;
	            col = 0;
	            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CELL_GROUP_REQUIRED))).booleanValue() || ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_PROVIDER_PROMO_ALLOW))).booleanValue()) {
	                row = this.writeSubscriberStatusDescription(worksheet2, col, row, p_hashMap);
	                row++;
	                
	                
	                
	                
	                
	                row++;
	                col = 0;
	                row = this.writeServiceProviderGpDescription(worksheet2, col, row, p_hashMap);
	            }
	            if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_GRD)) {
	                row = this.writeGradeList(worksheet2, col, row, p_hashMap);
	            }
	            if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_CAT)) {
	                row = this.writeCategoryDetails(worksheet2, col, row, p_hashMap);
	            }
	            if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_GRP)) {
	                row = this.writeGeographicalDomainCode(worksheet2, col, row, p_hashMap);
	            }
	            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CELL_GROUP_REQUIRED))).booleanValue() || ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_PROVIDER_PROMO_ALLOW))).booleanValue()) {
	                if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_CEL)) {
	                    row = this.writeCellGroupCode(worksheet2, col, row, p_hashMap);
	                }
	            }
	            this.writeInDataSheet(worksheet1, col, 0, p_hashMap, promotionLevel, p_dateRange);
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

	   
	    public String[][] readExcel(String p_excelID, String p_fileName) {
	        final String METHOD_NAME = "readExcel";
	        if (_log.isDebugEnabled()) {
	            _log.debug("readExcel", " p_excelID: " + p_excelID + " p_fileName: " + p_fileName);
	        }
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

	    
	    
	   
	    private int writeCardGroupDescription(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
	        final String METHOD_NAME = "writeCardGroupDescription";
	        if (_log.isDebugEnabled()) {
	            _log.debug("writeCardGroupDescription", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
	        }
	        try {
	            String keyName = null;
	            
	           
	            // Card group Description
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.CARD_GROUP_DESCRIPTION, null);

	            Label label = new Label(col, row, keyName, times16format);
	            worksheet2.mergeCells(col, row, col + 2, row);
	            worksheet2.addCell(label);
	            row++;
	            col = 0;

	            // Subscriber type
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SUBSCRIBER_TYPE_CODE, null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet2.addCell(label);

	            // Subscriber type name
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SUBSCRIBER_TYPE_NAME, null);
	            label = new Label(++col, row, keyName, times16format);
	            worksheet2.addCell(label);

	            // Subscriber service class id
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SERVICE_CLASS_ID, null);
	            label = new Label(++col, row, keyName, times16format);
	            worksheet2.addCell(label);

	            // Subscriber service class name
	          
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SERVICE_CLASS_NAME, null);
	            		
	            label = new Label(++col, row, keyName, times16format);
	            worksheet2.addCell(label);

	            // service class name
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SERVICE_CLASS_NAME, null);
	            
	            label = new Label(++col, row, keyName, times16format);
	            worksheet2.addCell(label);
	            
	            // Service type code
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SERVICE_TYPE_CODE, null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet2.addCell(label);

	            // Service type name
	           
	            		//"Service type name";
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SERVICE_TYPE_NAME, null);
	            
	            label = new Label(++col, row, keyName, times16format);
	            worksheet2.addCell(label);

	            // Subservice code
	            //keyName = "Sub-service code";
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SUB_SERVICE_CODE, null);
	            
	            label = new Label(++col, row, keyName, times16format);
	            worksheet2.addCell(label);

	            // Subservice name
	            //keyName = "Sub-service name";
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SUB_SERVICE_NAME, null);		
	            
	            label = new Label(++col, row, keyName, times16format);
	            worksheet2.addCell(label);

	            // card group set id
	            //keyName = "Card group set ID";
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.CARD_GROUP_SET_ID, null);		
	            
	            label = new Label(++col, row, keyName, times16format);
	            worksheet2.addCell(label);

	            // card group name
	            //keyName = "Card group name";
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.CARD_GROUP_NAME, null);		
		           
	            
	            label = new Label(++col, row, keyName, times16format);
	            worksheet2.addCell(label);
	            row++;
	            col = 0;
	            ArrayList rsubscriberTypeList = (ArrayList) p_hashMap.get(PretupsI.SUBSRICBER_TYPE);
	            ArrayList rsubscriberServiceTypeList = (ArrayList) p_hashMap.get(PretupsI.PROMOTIONAL_INTERFACE_CATEGORY_CLASS);
	            ArrayList subServiceTypeIdList = (ArrayList) p_hashMap.get(PretupsI.SUB_SERVICES_FOR_TRANSFERRULE);
	            ArrayList serviceTypeList = (ArrayList) p_hashMap.get(PretupsI.C2S_MODULE);
	            ArrayList cardGroupIdList = (ArrayList) p_hashMap.get(PretupsI.TRANSFER_RULE_PROMOTIONAL);
	            ListValueVO rsubscriberTypeVO = null;
	            ListValueVO rsubscriberServiceClassVO = null;
	            ListValueVO serviceTypeVO = null;
	            ListValueVO subServiceTypeIdVO = null;
	            ListValueVO cardGroupIdVO = null;
	            // ==================================================================================================
	            if (rsubscriberTypeList != null && rsubscriberTypeList.size() > 0 && cardGroupIdList != null && cardGroupIdList.size() > 0) {
	                for (int sk = 0, sl = rsubscriberTypeList.size(); sk < sl; sk++) {
	                   
	                    rsubscriberTypeVO = (ListValueVO) rsubscriberTypeList.get(sk);
	                    label = new Label(0, row, rsubscriberTypeVO.getValue());
	                    worksheet2.addCell(label);
	                    label = new Label(1, row, rsubscriberTypeVO.getLabel());
	                    worksheet2.addCell(label);
	                    row++;
	                    for (int si = 0, sj = rsubscriberServiceTypeList.size(); si < sj; si++) {
	                   
	                        rsubscriberServiceClassVO = (ListValueVO) rsubscriberServiceTypeList.get(si);
	                        if (rsubscriberTypeVO.getValue().equals(rsubscriberServiceClassVO.getValue().split(":")[0])) {
	                            label = new Label(2, row, rsubscriberServiceClassVO.getValue().split(":")[1]);
	                            worksheet2.addCell(label);
	                            label = new Label(3, row, rsubscriberServiceClassVO.getLabel());
	                            worksheet2.addCell(label);
	                            row++;
	                            for (int s = 0, t = serviceTypeList.size(); s < t; s++) {
	                   
	                                serviceTypeVO = (ListValueVO) serviceTypeList.get(s);
	                                label = new Label(4, row, serviceTypeVO.getValue());
	                                worksheet2.addCell(label);
	                                label = new Label(5, row, serviceTypeVO.getLabel());
	                                worksheet2.addCell(label);
	                                row++;
	                                for (int x = 0, y = subServiceTypeIdList.size(); x < y; x++) {
	                   
	                                    subServiceTypeIdVO = (ListValueVO) subServiceTypeIdList.get(x);
	                                    if (rsubscriberTypeVO.getValue().equals(subServiceTypeIdVO.getValue().split(":")[1]) && serviceTypeVO.getValue().equals(subServiceTypeIdVO.getValue().split(":")[3])) {
	                                        label = new Label(6, row, subServiceTypeIdVO.getValue().split(":")[2]);
	                                        worksheet2.addCell(label);
	                                        label = new Label(7, row, subServiceTypeIdVO.getLabel());
	                                        worksheet2.addCell(label);
	                                        row++;
	                                        for (int u = 0, v = cardGroupIdList.size(); u < v; u++) {
	                                            // System.out.println("5");
	                                            cardGroupIdVO = (ListValueVO) cardGroupIdList.get(u);
	                                            if ((subServiceTypeIdVO.getValue().split(":")[2]).equals(cardGroupIdVO.getValue().split(":")[0]) && (cardGroupIdVO.getValue().split(":")[2]).equals(serviceTypeVO.getValue())) {
	                                                label = new Label(8, row, cardGroupIdVO.getValue().split(":")[1]);
	                                                worksheet2.addCell(label);
	                                                label = new Label(9, row, cardGroupIdVO.getLabel());
	                                                worksheet2.addCell(label);
	                                                row++;
	                                            } // end if
	                                        } // end for loop
	                                    } // end if
	                                } // end for loop
	                            } // end for loop
	                            
	                        } // end if
	                    } // end for loop
	                } // end for loop
	            } // end if

	            return row;
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

	  
	    private int writeGradeList(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
	        final String METHOD_NAME = "writeGradeList";
	        if (_log.isDebugEnabled()) {
	            _log.debug("writeGradeList", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
	        }
	        try {
	            // Grade description
	        	String keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.GRADE_DESCRIPTION, null);
	        	Label label = new Label(col, row, keyName, times16format);
	            worksheet2.mergeCells(col, row, col + 2, row);
	            worksheet2.addCell(label);
	            row++;
	            col = 0;

	            // Grade Code
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SENDER_GRADE_CODE, null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet2.addCell(label);

	            // Grade name
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SENDER_GRADE_NAME, null);
	           
	            label = new Label(++col, row, keyName, times16format);
	            worksheet2.addCell(label);
	            row++;
	            col = 0;

	            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.PROMOTIONAL_LEVEL_GRADE);
	            ListValueVO listValueVO = null;
	            if (list != null) {
	                for (int i = 0, j = list.size(); i < j; i++) {
	                    col = 0;
	                    listValueVO = (ListValueVO) list.get(i);
	                    label = new Label(col, row, (listValueVO.getValue()).split(":")[1]);
	                    worksheet2.addCell(label);
	                    label = new Label(col + 1, row, listValueVO.getLabel());
	                    worksheet2.addCell(label);
	                    row++;
	                }
	            }
	            return row;
	        } catch (RowsExceededException e) {
	            _log.errorTrace(METHOD_NAME, e);
	            _log.error("writeGradeList", " Exception e: " + e.getMessage());
	            throw e;
	        } catch (WriteException e) {
	            _log.errorTrace(METHOD_NAME, e);
	            _log.error("writeGradeList", " Exception e: " + e.getMessage());
	            throw e;
	        }
	    }


	    private int writeCategoryDetails(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
	        final String METHOD_NAME = "writeCategoryDetails";
	        if (_log.isDebugEnabled()) {
	            _log.debug("writeCategoryDetails", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
	        }
	        try {
	            // Category description
	        	String keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SENDER_CATEGORY_DESCRIPTION, null);
	            
	            Label label = new Label(col, row, keyName, times16format);
	            worksheet2.mergeCells(col, row, col + 2, row);
	            worksheet2.addCell(label);
	            row++;
	            col = 0;

	            // Category Code
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SENDER_CATEGORY_CODE, null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet2.addCell(label);

	            // Category name
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SENDER_CATEGORY_NAME, null);
	            label = new Label(++col, row, keyName, times16format);
	            worksheet2.addCell(label);
	            row++;

	            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.PROMOTIONAL_LEVEL_CATEGORY);
	            CategoryVO categoryVO = null;
	            if (list != null) {
	                for (int i = 0, j = list.size(); i < j; i++) {
	                    col = 0;
	                    categoryVO = (CategoryVO) list.get(i);
	                    label = new Label(col++, row, categoryVO.getCategoryCode());
	                    worksheet2.addCell(label);
	                    label = new Label(col, row, categoryVO.getCategoryName());
	                    worksheet2.addCell(label);
	                    row++;
	                }
	            }
	            return row;
	        } catch (RowsExceededException e) {
	            _log.errorTrace(METHOD_NAME, e);
	            _log.error("writeCategoryDetails", " Exception e: " + e.getMessage());
	            throw e;
	        } catch (WriteException e) {
	            _log.errorTrace(METHOD_NAME, e);
	            _log.error("writeCategoryDetails", " Exception e: " + e.getMessage());
	            throw e;
	        }
	    }

	   
	    private int writeGeographicalDomainCode(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
	        final String METHOD_NAME = "writeGeographicalDomainCode";
	        if (_log.isDebugEnabled()) {
	            _log.debug("writeGeographicalDomainCode", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
	        }
	        try {
	            // Geographical domain description
	        	String keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SENDER_GEOGRAPHICAL_DOMAIN_DESCRIPTION, null);
	            Label label = new Label(col, row, keyName, times16format);
	            worksheet2.mergeCells(col, row, col + 2, row);
	            worksheet2.addCell(label);
	            row++;
	            col = 0;

	            // Geographical domain code
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SENDER_GEOGRAPHICAL_DOMAIN_CODE, null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet2.addCell(label);

	            // Geographical domain name
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SENDER_GEOGRAPHICAL_DOMAIN_NAME, null);
	            label = new Label(++col, row, keyName, times16format);
	            worksheet2.addCell(label);
	            row++;

	            // Logic for generating headings
	            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.PROMOTIONAL_LEVEL_GEOGRAPHY);
	            GeographicalDomainVO geographDomainVO = null;

	            if (list != null) {
	                for (int i = 0, j = list.size(); i < j; i++) {
	                    col = 0;
	                    geographDomainVO = (GeographicalDomainVO) list.get(i);
	                    label = new Label(col, row, geographDomainVO.getGrphDomainCode());
	                    worksheet2.addCell(label);
	                    label = new Label(++col, row, geographDomainVO.getGrphDomainName());
	                    worksheet2.addCell(label);
	                    row++;
	                }
	            }
	            return row;
	        } catch (RowsExceededException e) {
	            _log.errorTrace(METHOD_NAME, e);
	            _log.error("writeGeographicalDomainCode", " Exception e: " + e.getMessage());
	            throw e;
	        } catch (WriteException e) {
	            _log.errorTrace(METHOD_NAME, e);
	            _log.error("writeGeographicalDomainCode", " Exception e: " + e.getMessage());
	            throw e;
	        }

	    }


	    private void writeInDataSheet(WritableSheet worksheet1, int col, int row, HashMap p_hashMap, String promotionLevel, String p_dateRange) throws Exception {
	        final String METHOD_NAME = "writeInDataSheet";
	        if (_log.isDebugEnabled()) {
	            _log.debug("writeInDataSheet", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
	        }
	        try {
	            String keyName = null;
	            // Promotional transfer rule data file
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.PROMOTIONAL_TRANSFER_RULE_DATA_FILE, null);

	            //keyName = "Promotional transfer rule data file";//p_messages.getMessage(p_locale, "promotionaltransferrule.datasheet.heading");
	            Label label = new Label(col, row, keyName, times12format);
	            worksheet1.mergeCells(col, row, col + 10, row);
	            worksheet1.addCell(label);
	            row++;
	            col = 0;
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.ALL_FIELDS_MARKED_WITH_ARE_MANDATORY, null);

	            //keyName = "All fields marked with * are mandatory.";//p_messages.getMessage(p_locale, "promotionaltransferrule.xlsfile.details.mandatory");
	            label = new Label(++col, row, keyName);
	            worksheet1.mergeCells(col, row, col + 5, row);
	            worksheet1.addCell(label);
	            row++;
	            col = 0;
	            if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_GRD)) {
	                // Sender Grade
	            	keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SENDER_GRADE_CODE, null);
	                keyName = keyName + "*";
	                label = new Label(col++, row, keyName, times16format);
	                worksheet1.addCell(label);
	            }

	            if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_CAT)) {
	                // Sender Category code
	                keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SENDER_CATEGORY_CODE, null);
	                keyName = keyName + "*";
	                label = new Label(col++, row, keyName, times16format);
	                worksheet1.addCell(label);
	            }
	            if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_GRP)) {
	                // Sender Geographical domain code
	                keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SENDER_GEOGRAPHICAL_DOMAIN_CODE, null);

	                keyName = keyName + "*";
	                label = new Label(col++, row, keyName, times16format);
	                worksheet1.addCell(label);
	            }
	            if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_USR)) {
	                // Sender Mobile No
	                keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SENDER_MOBILE_NUMBER, null);
	                label = new Label(col++, row, keyName, times16format);
	                worksheet1.addCell(label);
	            }
	            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CELL_GROUP_REQUIRED))).booleanValue() || ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_PROVIDER_PROMO_ALLOW))).booleanValue()) {
	                if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_CEL)) {
	                    keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.CELL_GROUP_CODE, null);
	                    keyName = keyName + "*";
	                    label = new Label(col++, row, keyName, times16format);
	                    worksheet1.addCell(label);
	                }

	                if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_SERVICE)) {
	                    keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SERVICE_PROVIDER_GROUP_ID, null);
	                    label = new Label(col++, row, keyName, times16format);
	                    worksheet1.addCell(label);
	                }
	            }

	            // Receiver subscriber type
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.RECEIVER_SUBSCRIBER_TYPE, null);
	            label = new Label(col++, row, keyName, times16format);
	            worksheet1.addCell(label);

	            // Receiver service class id
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.RECEIVER_SERVICE_CLASS_ID, null);
	            label = new Label(col++, row, keyName, times16format);
	            worksheet1.addCell(label);

	            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CELL_GROUP_REQUIRED))).booleanValue() || ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_PROVIDER_PROMO_ALLOW))).booleanValue()) {
	                // Receiver subscriber status
	                keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SUBSCRIBER_STATUS, null);
	                label = new Label(col++, row, keyName, times16format);
	                worksheet1.addCell(label);
	                // Receiver service provider group id
	                if (!promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_SERVICE)) {
	                    keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SERVICE_PROVIDER_GROUP_ID, null);

	
	                    label = new Label(col++, row, keyName, times16format);
	                    worksheet1.addCell(label);
	                }
	            }
	            // Service type
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SERVICE_TYPE_CODE, null);
	            label = new Label(col++, row, keyName, times16format);
	            worksheet1.addCell(label);

	            // subservice code
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SUB_SERVICE_CODE, null);
	            label = new Label(col++, row, keyName, times16format);
	            worksheet1.addCell(label);

	            // Card group set id
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.CARD_GROUP_SET_ID, null);
	            label = new Label(col++, row, keyName, times16format);
	            worksheet1.addCell(label);

	            // Applicabe from date
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.APPLICABLE_FROM_DATE, null);
	            label = new Label(col++, row, keyName, times16format);
	            worksheet1.addCell(label);
	            if ("Y".equalsIgnoreCase(p_dateRange)) {
	                // Applicable from time
	                keyName =RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.APPLICABLE_FROM_TIME, null);

	                		
	                label = new Label(col++, row, keyName, times16format);
	                worksheet1.addCell(label);

	                // Applicabe till date
	                keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.APPLICABLE_TILL_DATE, null);
	                label = new Label(col++, row, keyName, times16format);
	                worksheet1.addCell(label);
	            } else {
	                // Applicabe till date
	                keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.APPLICABLE_TILL_DATE, null);
	                label = new Label(col++, row, keyName, times16format);
	                worksheet1.addCell(label);

	                // Applicable from time
	                keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.APPLICABLE_FROM_TIME, null);
	                label = new Label(col++, row, keyName, times16format);
	                worksheet1.addCell(label);
	            }

	            // Applicable till time
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.APPLICABLE_TILL_TIME, null);
	            label = new Label(col++, row, keyName, times16format);
	            worksheet1.addCell(label);

	            row++;
	            col = 0;
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

	    private int writeCellGroupCode(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
	        final String METHOD_NAME = "writeCellGroupCode";
	        if (_log.isDebugEnabled()) {
	            _log.debug("writeCellGroupCode", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
	        }
	        try {
	            // description
	        	String keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.CELL_GROUP_DESCRIPTION, null);
	           
	        	Label label = new Label(col, row, keyName, times16format);
	            worksheet2.mergeCells(col, row, col + 2, row);
	            worksheet2.addCell(label);
	            row++;
	            col = 0;

	            // Code
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.CELL_GROUP_CODE, null);
	            keyName=keyName+"*";
	           
	            label = new Label(col, row, keyName, times16format);
	            worksheet2.addCell(label);

	            // name
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.CELL_GROUP_NAME, null);
	           
	            label = new Label(++col, row, keyName, times16format);
	            worksheet2.addCell(label);
	            row++;
	            col = 0;

	            ArrayList list = (ArrayList) p_hashMap.get(PretupsI.PROMOTIONAL_LEVEL_CELLGROUP);
	            ListValueVO listValueVO = null;
	            if (list != null) {
	                for (int i = 0, j = list.size(); i < j; i++) {
	                    col = 0;
	                    listValueVO = (ListValueVO) list.get(i);
	                    String cellId= listValueVO.getValue().split(":")[0];
	                    label = new Label(col, row,cellId);
	                    worksheet2.addCell(label);
	                    String cellName = listValueVO.getLabel();
	                    label = new Label(col + 1, row, cellName);
	                    worksheet2.addCell(label);
	                    row++;
	                }
	                
	                
	            }
	            return row;
	        } catch (RowsExceededException e) {
	            _log.errorTrace(METHOD_NAME, e);
	            _log.error("writeCellGroupCode", " Exception e: " + e.getMessage());
	            throw e;
	        } catch (WriteException e) {
	            _log.errorTrace(METHOD_NAME, e);
	            _log.error("writeCellGroupCode", " Exception e: " + e.getMessage());
	            throw e;
	        }
	    }

	    private int writeSubscriberStatusDescription(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
	        final String METHOD_NAME = "writeSubscriberStatusDescription";
	        if (_log.isDebugEnabled()) {
	            _log.debug("writeCardGroupDescription", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
	        }
	        try {
	            String keyName = null;
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SUBSCRIBER_STATUS_DESCRIPTION, null);
	            // Card group Description
	            Label label = new Label(col, row, keyName, times16format);
	            worksheet2.mergeCells(col, row, col + 2, row);
	            worksheet2.addCell(label);
	            row++;
	            col = 0;

	            // Subscriber type
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SUBSCRIBER_TYPE_CODE, null);
	            label = new Label(col, row, keyName, times16format);
	            worksheet2.addCell(label);

	            // Subscriber type name
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SUBSCRIBER_STATUS, null);
	            label = new Label(++col, row, keyName, times16format);
	            worksheet2.addCell(label);

	            row++;
	            col = 0;
	            ArrayList subscriberStatusList = (ArrayList) p_hashMap.get(PretupsI.TRANSFER_RULE_SUBSCRIBER_STATUS);
	            TransferVO transferVO = null;
	            // ==================================================================================================
	            if (subscriberStatusList != null && subscriberStatusList.size() > 0) {
	                for (int sk = 0, sl = subscriberStatusList.size(); sk < sl; sk++) {
	           
	                    transferVO = (TransferVO) subscriberStatusList.get(sk);
	                    label = new Label(0, row, transferVO.getServiceType());
	                    worksheet2.addCell(label);
	                    label = new Label(1, row, transferVO.getSubscriberStatus());
	                    worksheet2.addCell(label);
	                    row++;
	                } // end for loop
	            } // end if

	            return row;
	        } catch (RowsExceededException e) {
	            _log.errorTrace(METHOD_NAME, e);
	            _log.error("writeCardGroupDescription", " Exception e: " + e.getMessage());
	            throw e;
	        } catch (WriteException e) {
	            _log.errorTrace(METHOD_NAME, e);
	            _log.error("writeCardGroupDescription", " Exception e: " + e.getMessage());
	            throw e;
	        }
	    }

	    private int writeServiceProviderGpDescription(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
	        final String METHOD_NAME = "writeServiceProviderGpDescription";
	        if (_log.isDebugEnabled()) {
	            _log.debug("writeCardGroupDescription", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
	        }
	        try {
	            String keyName = null;
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SERVICE_PROVIDER_GROUP_DESCRIPTION, null);
	            

	            // Card group Description
	            Label label = new Label(col, row, keyName, times16format);
	            worksheet2.mergeCells(col, row, col + 2, row);
	            worksheet2.addCell(label);
	            row++;
	            col = 0;

	            // Subscriber type
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SERVICE_PROVIDER_GROUP_NAME, null);

	          
	            label = new Label(col, row, keyName, times16format);
	            worksheet2.addCell(label);

	            // Subscriber type name
	            keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SERVICE_PROVIDER_GROUP_ID, null);
	            label = new Label(++col, row, keyName, times16format);
	            worksheet2.addCell(label);


	            
	            row++;
	            col = 0;

	            ArrayList serviceProviderGpList = (ArrayList) p_hashMap.get(PretupsI.SERVICE_GROUP_TYPE_ID);
	            ServiceGpMgmtVO servicemgmtVO = null;
	            // ==================================================================================================
	            if (serviceProviderGpList != null && serviceProviderGpList.size() > 0) {
	                for (int sk = 0, sl = serviceProviderGpList.size(); sk < sl; sk++) {
	           
	                    servicemgmtVO = (ServiceGpMgmtVO) serviceProviderGpList.get(sk);
	                    label = new Label(0, row, servicemgmtVO.getGroupName());
	                    worksheet2.addCell(label);
	                    label = new Label(1, row, servicemgmtVO.getGroupId());
	                    worksheet2.addCell(label);
	                    row++;
	                } // end for loop
	            } // end if

	            return row;
	        } catch (RowsExceededException e) {
	            _log.errorTrace(METHOD_NAME, e);
	            _log.error("writeCardGroupDescription", " Exception e: " + e.getMessage());
	            throw e;
	        } catch (WriteException e) {
	            _log.errorTrace(METHOD_NAME, e);
	            _log.error("writeCardGroupDescription", " Exception e: " + e.getMessage());
	            throw e;
	        }
	    }

	    public String[][] readMultipleExcelSheet(String p_excelID, String p_fileName, boolean p_readLastSheet, int p_leftHeaderLinesForEachSheet, HashMap<String, String> map) {
	        final String METHOD_NAME = "readMultipleExcelSheet";
	        if (_log.isDebugEnabled()) {
	            _log.debug("readMultipleExcelSheet", " p_excelID: " + p_excelID + " p_fileName: " + p_fileName + " p_readLastSheet=" + p_readLastSheet, " p_leftHeaderLinesForEachSheet=" + p_leftHeaderLinesForEachSheet);
	        }
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
	            strArr = new String[noOfRows+1][noOfcols];
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
	           
	                }
	                for (int row = p_leftHeaderLinesForEachSheet-1; row < noOfRows+1; row++) {
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
	

}


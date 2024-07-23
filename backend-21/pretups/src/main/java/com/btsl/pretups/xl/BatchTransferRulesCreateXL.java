package com.btsl.pretups.xl;

/*
 * @# BatchTransferRulesCreateXL.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Shishupal Singh April 12, 2007 Initial creation
 * Ashish Srivastav July 23, 2008 Modification
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2007 Bharti Telesoft Ltd.
 * This class use for read write in xls file for batch transfer rules creation.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.util.MessageResources;

import com.btsl.common.ListValueVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.transfer.businesslogic.TransferRulesVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.xl.ExcelFileConstants;

import jxl.Cell;
import jxl.Sheet;
import jxl.SheetSettings;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


/**
 * @author shishupal.singh
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class BatchTransferRulesCreateXL {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private WritableFont times12font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, true);
    private WritableCellFormat times12format = new WritableCellFormat(times12font);
    private WritableFont times16font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, true);
    private WritableCellFormat times16format = new WritableCellFormat(times16font);
    private MessageResources p_messages = null;
    private Locale p_locale = null;
	private WritableWorkbook workbook = null;

    /**
     * @param p_hashMap
     * @param messages
     * @param locale
     * @param p_fileName
     * @param p_categoryVO
     *            TODO
     * @throws Exception
     * @author shishupal.singh
     */
    public void writeExcel(HashMap p_hashMap, MessageResources messages, Locale locale, String p_fileName) {
        final String METHOD_NAME = "writeExcel";
        if (_log.isDebugEnabled()) {
            _log.debug("writeExcel", " p_hashMap size:" + p_hashMap.size() + ", p_locale: " + locale + ", p_fileName: " + p_fileName);
        }
       
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
            Label label = null;
            String keyName = null;

            // times16format.setWrap(true);
            // times16format.setAlignment(Alignment.CENTRE);
            keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.mastersheet.heading", "P2P");
            label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 6, row);
            worksheet2.addCell(label);
            row++;

            /*
             * row++;
             * col=0;
             * row=this.writeSenderSubscriberServiceTypeListing(worksheet2, col,
             * row, p_hashMap);
             */

            row++;
            col = 0;
            row = this.writeReceiverSubscriberServiceTypeListing(worksheet2, col, row, p_hashMap);

            col = 0;
            row = 0;
            this.writeInDataSheet(worksheet1, col, row, p_hashMap);
            // worksheet2.setProtected(true);

            workbook.write();
            workbook.close();

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

    /**
     * readExcel
     * 
     * @param p_excelID
     * @param p_fileName
     * @return strArr
     * @throws Exception
     */
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
                    strArr[row][indexMapArray[col]] = content.replaceAll("\n", " ").replaceAll("\r", " ");
                }
            }
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
            excelsheet = null;
            if (_log.isDebugEnabled()) {
                _log.debug("readExcel", " Exiting strArr: " + strArr);
            }
        }
        return strArr;
    }

    /**
     * Method writeSenderSubscriberServiceTypeListing
     * 
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return row
     * @throws Exception
     * @author Shishupal Singh
     */
    /*
     * private int writeSenderSubscriberServiceTypeListing(WritableSheet
     * worksheet2, int col,int row, HashMap p_hashMap) throws Exception
     * {
     * if(_log.isDebugEnabled())
     * _log.debug("writeSenderSubscriberServiceTypeListing"," p_hashMap size="+
     * p_hashMap.size()+" p_locale: "+p_locale+" col="+col+" row="+row);
     * final String METHOD_NAME = "writeSenderSubscriberServiceTypeListing";
     * try
     * {
     * String keyName = p_messages.getMessage(p_locale,
     * "transferrules.xlsfile.mastersheet.sendersubscriber");
     * Label label = new Label(col,row,keyName, times16format);
     * worksheet2.mergeCells(col,row,col+3,row);
     * worksheet2.addCell(label);
     * row++;
     * col=0;
     * keyName = p_messages.getMessage(p_locale,
     * "transferrules.xlsfile.mastersheet.sendersubscriber.note");
     * label = new Label(col,row,keyName);
     * worksheet2.mergeCells(col,row,COLUMN_MARGE,row);
     * worksheet2.addCell(label);
     * row++;
     * col=0;
     * 
     * keyName = p_messages.getMessage(p_locale,
     * "transferrules.xlsfile.mastersheet.sendertypecode");
     * label = new Label(col,row,keyName, times16format);
     * worksheet2.addCell(label);
     * keyName = p_messages.getMessage(p_locale,
     * "transferrules.xlsfile.mastersheet.sendertypename");
     * label = new Label(++col,row,keyName, times16format);
     * worksheet2.addCell(label);
     * keyName = p_messages.getMessage(p_locale,
     * "transferrules.xlsfile.mastersheet.senderserviceclasscode");
     * label = new Label(++col,row,keyName, times16format);
     * worksheet2.addCell(label);
     * keyName = p_messages.getMessage(p_locale,
     * "transferrules.xlsfile.mastersheet.senderserviceclassname");
     * label = new Label(++col,row,keyName, times16format);
     * worksheet2.addCell(label);
     * 
     * ArrayList ssubscriberTypeList =
     * (ArrayList)p_hashMap.get("subscriberTypeList");
     * ArrayList ssubscriberServiceTypeList =
     * (ArrayList)p_hashMap.get("subscriberServiceTypeList");
     * 
     * row++;
     * col=0;
     * ListValueVO ssubscriberTypeVO = null;
     * ListValueVO ssubscriberServiceClassVO = null;
     * if(ssubscriberTypeList!=null && ssubscriberTypeList.size()>0)
     * {
     * for(int sk=0,sl=ssubscriberTypeList.size();sk<sl;sk++)
     * {
     * ssubscriberTypeVO = (ListValueVO)ssubscriberTypeList.get(sk);
     * label = new Label(col,row,ssubscriberTypeVO.getValue());
     * worksheet2.addCell(label);
     * label = new Label(col+1,row,ssubscriberTypeVO.getLabel());
     * worksheet2.addCell(label);
     * row++;
     * for(int si=0,sj=ssubscriberServiceTypeList.size();si<sj;si++)
     * {
     * ssubscriberServiceClassVO
     * =(ListValueVO)ssubscriberServiceTypeList.get(si);
     * if(ssubscriberTypeVO.getValue().equals(ssubscriberServiceClassVO.getValue(
     * ).split(":")[0]))
     * {
     * //label = new Label(col+2,row,listValueVO1.getValue());
     * label = new
     * Label(col+2,row,ssubscriberServiceClassVO.getValue().split(":")[1]);
     * worksheet2.addCell(label);
     * label = new Label(col+3,row,ssubscriberServiceClassVO.getLabel());
     * worksheet2.addCell(label);
     * row++;
     * }
     * }
     * }
     * }
     * return row;
     * }
     * catch (RowsExceededException e)
     * {
     * _log.errorTrace(METHOD_NAME, e);
     * _log.error("writeSenderSubscriberServiceTypeListing"," Exception e: "+e.
     * getMessage());
     * throw e;
     * } catch (WriteException e) {
     * _log.errorTrace(METHOD_NAME, e);
     * _log.error("writeSenderSubscriberServiceTypeListing"," Exception e: "+e.
     * getMessage());
     * throw e;
     * }
     * 
     * }
     */

    /**
     * Method writeReceiverSubscriberServiceTypeListing
     * 
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return row
     * @throws Exception
     * @author Shishupal Singh
     */
    private int writeReceiverSubscriberServiceTypeListing(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
	 
		final String METHOD_NAME = "writeReceiverSubscriberServiceTypeListing";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
		
		int maxRowsAllowed = 65000;
		if(!BTSLUtil.isNullString(Constants.getProperty("MAX_ROW_IN_SHEET_BATCH_XL")))
        	maxRowsAllowed = Integer.parseInt(Constants.getProperty("MAX_ROW_IN_SHEET_BATCH_XL"));
        
		Label label = null;
        
        try {
		
			createP2PMasterDataHeader(worksheet2,col,row);
            
			/*
             * String keyName = p_messages.getMessage(p_locale,
             * "transferrules.xlsfile.mastersheet.receiversubscriber");
             * Label label = new Label(col,row,keyName, times16format);
             * worksheet2.mergeCells(col,row,col+3,row);
             * worksheet2.addCell(label);
             * row++;
             * col=0;
             * keyName = p_messages.getMessage(p_locale,
             * "transferrules.xlsfile.mastersheet.receiversubscriber.note");
             * label = new Label(col,row,keyName);
             * worksheet2.mergeCells(col,row,COLUMN_MARGE,row);
             * worksheet2.addCell(label);
             * row++;
             * col=0;
             */
			 
			ArrayList gatewayCodeList = (ArrayList) p_hashMap.get("gatewayCodeList");
			/*COMMENTED FOR CELL GROUP ID. WILL DISPLAY ONCE IMPLEMENTED FOR C2S TRANSFER RULE
			ArrayList cellGroupIDList = (ArrayList) p_hashMap.get("cellGroupIDList");*/
            ArrayList ssubscriberTypeList = (ArrayList) p_hashMap.get("subscriberTypeList");
            ArrayList ssubscriberServiceTypeList = (ArrayList) p_hashMap.get("subscriberServiceTypeList");
            ArrayList rsubscriberTypeList = (ArrayList) p_hashMap.get("subscriberTypeList");
            ArrayList rsubscriberServiceTypeList = (ArrayList) p_hashMap.get("subscriberServiceTypeList");
            ArrayList serviceTypeList = (ArrayList) p_hashMap.get("serviceTypeList");
            ArrayList subServiceTypeIdList = (ArrayList) p_hashMap.get("subServiceTypeIdList");
            ArrayList cardGroupIdList = (ArrayList) p_hashMap.get("cardGroupIdList");
            row++;
            col = 0;
            /*
             * StringBuffer trnsRuleBuf = null;
             * ArrayList transferRulesList = new ArrayList();
             */
			  int masterSheetCount = 1;
			ListValueVO gatewayCodeVO = null;
			ListValueVO cellGroupIDVO = null;
            ListValueVO ssubscriberTypeVO = null;
            ListValueVO ssubscriberServiceClassVO = null;
            ListValueVO rsubscriberTypeVO = null;
            ListValueVO rsubscriberServiceClassVO = null;
            ListValueVO serviceTypeVO = null;
            ListValueVO subServiceTypeIdVO = null;
            ListValueVO cardGroupIdVO = null;
            
            for (int rg = 0, rc = gatewayCodeList.size(); rg < rc; rg++)
            {
            	if(row> maxRowsAllowed){
            		masterSheetCount++;
            		row = 0;
            		worksheet2 = workbook.createSheet("Master Sheet "+masterSheetCount, masterSheetCount);
            		createP2PMasterDataHeader(worksheet2,col,row);
            		row+=2;
            	}
            	
            	gatewayCodeVO = (ListValueVO) gatewayCodeList.get(rg);
            	label = new Label(col, row, gatewayCodeVO.getLabel());
            	worksheet2.addCell(label);
                label = new Label(col + 1, row, gatewayCodeVO.getValue());
                 worksheet2.addCell(label);
            	row++;
            	
            	/* COMMENTED FOR CELL GROUP ID. WILL DISPLAY ONCE IMPLEMENTED FOR C2S TRANSFER RULE
            	 * for(int cg = 0, ci = cellGroupIDList.size(); cg < ci; cg++)
            	{
            		if(row> maxRowsAllowed){
                		masterSheetCount++;
                		row = 0;
                		worksheet2 = workbook.createSheet("Master Sheet "+masterSheetCount, masterSheetCount);
                		createP2PMasterDataHeader(worksheet2,col,row);
                		row+=2;
                	}
            	
            	cellGroupIDVO = (ListValueVO) cellGroupIDList.get(cg);
            	label = new Label(col + 2, row, cellGroupIDVO.getValue());
            	worksheet2.addCell(label);
            	label = new Label(col + 3, row, cellGroupIDVO.getLabel());
                worksheet2.addCell(label);
                row++;*/
            	
            if (ssubscriberTypeList != null && ssubscriberTypeList.size() > 0 && rsubscriberTypeList != null && rsubscriberTypeList.size() > 0) {
                for (int sk = 0, sl = ssubscriberTypeList.size(); sk < sl; sk++) {
                    // System.out.println("1");
					if(row> maxRowsAllowed){
                		masterSheetCount++;
                		row = 0;
                		worksheet2 = workbook.createSheet("Master Sheet "+masterSheetCount, masterSheetCount);
                		createP2PMasterDataHeader(worksheet2,col,row);
                		row+=2;
                	}
					
                    ssubscriberTypeVO = (ListValueVO) ssubscriberTypeList.get(sk);
                    label = new Label(col + 2, row, ssubscriberTypeVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(col + 3, row, ssubscriberTypeVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                    for (int si = 0, sj = ssubscriberServiceTypeList.size(); si < sj; si++) {
                        // System.out.println("2");
						if(row> maxRowsAllowed){
                    		masterSheetCount++;
                    		row = 0;
                    		worksheet2 = workbook.createSheet("Master Sheet "+masterSheetCount, masterSheetCount);
                    		createP2PMasterDataHeader(worksheet2,col,row);
                    		row+=2;
                    	} 
                        ssubscriberServiceClassVO = (ListValueVO) ssubscriberServiceTypeList.get(si);
                        if (ssubscriberTypeVO.getValue().equals(ssubscriberServiceClassVO.getValue().split(":")[0])) {
                            // label = new
                            // Label(col+2,row,listValueVO1.getValue());
                            label = new Label(col + 4, row, ssubscriberServiceClassVO.getValue().split(":")[1]);
                            worksheet2.addCell(label);
                            label = new Label(col + 5, row, ssubscriberServiceClassVO.getLabel());
                            worksheet2.addCell(label);
                            row++;
                            for (int k = 0, l = rsubscriberTypeList.size(); k < l; k++) {
                                // System.out.println("3");
								if(row> maxRowsAllowed){
									masterSheetCount++;
									row = 0;
									worksheet2 = workbook.createSheet("Master Sheet "+masterSheetCount, masterSheetCount);
									createP2PMasterDataHeader(worksheet2,col,row);
									row+=2;
								}
                                rsubscriberTypeVO = (ListValueVO) rsubscriberTypeList.get(k);
                                label = new Label(col + 6, row, rsubscriberTypeVO.getValue());
                                worksheet2.addCell(label);
                                label = new Label(col + 7, row, rsubscriberTypeVO.getLabel());
                                worksheet2.addCell(label);
                                row++;
                                for (int i = 0, j = rsubscriberServiceTypeList.size(); i < j; i++) {
                                    // System.out.println("4");
									 if(row> maxRowsAllowed){
										masterSheetCount++;
										row = 0;
										worksheet2 = workbook.createSheet("Master Sheet "+masterSheetCount, masterSheetCount);
										createP2PMasterDataHeader(worksheet2,col,row);
										row+=2;
									} 
                                    rsubscriberServiceClassVO = (ListValueVO) rsubscriberServiceTypeList.get(i);
                                    if (rsubscriberTypeVO.getValue().equals(rsubscriberServiceClassVO.getValue().split(":")[0])) {
                                        label = new Label(col + 8, row, rsubscriberServiceClassVO.getValue().split(":")[1]);
                                        worksheet2.addCell(label);
                                        label = new Label(col + 9, row, rsubscriberServiceClassVO.getLabel());
                                        worksheet2.addCell(label);
                                        row++;
                                        for (int s = 0, t = serviceTypeList.size(); s < t; s++) {
                                            // System.out.println("5");
											 if(row> maxRowsAllowed){
												masterSheetCount++;
												row = 0;
												worksheet2 = workbook.createSheet("Master Sheet "+masterSheetCount, masterSheetCount);
												createP2PMasterDataHeader(worksheet2,col,row);
												row+=2;
											} 
                                            serviceTypeVO = (ListValueVO) serviceTypeList.get(s);
                                            label = new Label(col + 10, row, serviceTypeVO.getValue());
                                            worksheet2.addCell(label);
                                            label = new Label(col + 11, row, serviceTypeVO.getLabel());
                                            worksheet2.addCell(label);
                                            row++;
                                            for (int x = 0, y = subServiceTypeIdList.size(); x < y; x++) {
                                                // System.out.println("6");
												 if(row> maxRowsAllowed){
													masterSheetCount++;
													row = 0;
													worksheet2 = workbook.createSheet("Master Sheet "+masterSheetCount, masterSheetCount);
													createP2PMasterDataHeader(worksheet2,col,row);
													row+=2;
												} 
                                                subServiceTypeIdVO = (ListValueVO) subServiceTypeIdList.get(x);
                                                if (ssubscriberTypeVO.getValue().equals(subServiceTypeIdVO.getValue().split(":")[0]) && rsubscriberTypeVO.getValue().equals(subServiceTypeIdVO.getValue().split(":")[1]) && serviceTypeVO.getValue().equals(subServiceTypeIdVO.getValue().split(":")[3])) {
                                                    label = new Label(col + 12, row, subServiceTypeIdVO.getValue().split(":")[2]);
                                                    worksheet2.addCell(label);
                                                    label = new Label(col + 13, row, subServiceTypeIdVO.getLabel());
                                                    worksheet2.addCell(label);
                                                    row++;
                                                    for (int u = 0, v = cardGroupIdList.size(); u < v; u++) {
                                                        // System.out.println("7");
														 if(row> maxRowsAllowed){
																masterSheetCount++;
																row = 0;
																worksheet2 = workbook.createSheet("Master Sheet "+masterSheetCount, masterSheetCount);
																createP2PMasterDataHeader(worksheet2,col,row);
																row+=2;
															} 
                                                        cardGroupIdVO = (ListValueVO) cardGroupIdList.get(u);
                                                        if ((subServiceTypeIdVO.getValue().split(":")[2]).equals(cardGroupIdVO.getValue().split(":")[0]) && (cardGroupIdVO.getValue().split(":")[2]).equals(serviceTypeVO.getValue())) {
                                                            label = new Label(col + 14, row, cardGroupIdVO.getValue().split(":")[1]);
                                                            worksheet2.addCell(label);
                                                            label = new Label(col + 15, row, cardGroupIdVO.getLabel());
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
                    } // end for loop
                } // end for loop
            } // end if
         /* } // end for loop
*/        } // end for loop
            return row;
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeReceiverSubscriberServiceTypeListing", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeReceiverSubscriberServiceTypeListing", " Exception e: " + e.getMessage());
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
            String keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.templatesheet.heading", "P2P");

            Label label = new Label(col, row, keyName, times12format);
            worksheet1.mergeCells(col, row, col + 10, row);
            worksheet1.addCell(label);

            row = row + 2;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.templatesheet.gatewayCode");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);
            
           /* COMMENTED FOR CELL GROUP ID. WILL DISPLAY ONCE IMPLEMENTED FOR C2S TRANSFER RULE
            * keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.templatesheet.groupCellID");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);*/
            
            keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.templatesheet.sendertype");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.templatesheet.senderserviceclass");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.templatesheet.receivertype");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.templatesheet.receiverserviceclass");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.templatesheet.servicetype");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.templatesheet.subservice");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.templatesheet.cardgroupset");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            // setting for the vertical freeze panes
            SheetSettings sheetSetting = new SheetSettings();
            sheetSetting = worksheet1.getSettings();
            sheetSetting.setVerticalFreeze(row + 1);

            // setting for the horizontal freeze panes
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
     * @param p_hashMap
     * @param messages
     * @param locale
     * @param p_fileName
     * @throws Exception
     * @author ashish.srivastav
     */
    public void writeTrfRuleToExcel(String p_excelID,HashMap p_hashMap, MessageResources messages,Locale locale,String p_fileName){
		if(_log.isDebugEnabled())
		    _log.debug("writeTrfRuleToExcel"," p_excelID: "+p_excelID+" p_hashMap:"+p_hashMap+" p_locale: "+locale+" p_fileName: "+p_fileName);
		final String METHOD_NAME = "writeTrfRuleToExcel";

		WritableSheet worksheet1 = null;
		WritableSheet worksheet2 = null;
		int col=0;
		int row=0;
		String keyName=null;
		Label label = null; 
		
		try
		{   
			workbook = Workbook.createWorkbook(new File(p_fileName));
			worksheet1 = workbook.createSheet("Template Sheet", 0);
			worksheet2 = workbook.createSheet("Master Sheet", 1);
			p_messages=messages;
			p_locale=locale;
			keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.mastersheet.heading","C2S");
			label = new Label(col,row,keyName, times16format);
			worksheet2.mergeCells(col,row,col+11,row);
			worksheet2.addCell(label);
			row++;
			col=0;
			row=this.writeC2SMasterDataListing(worksheet2, col, row, p_hashMap);
			row=0;
			col=0;
			keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.templatesheet.heading","C2S");
			label = new Label(col,row,keyName, times16format);
			worksheet1.mergeCells(col,row,col+11,row);
			worksheet1.addCell(label);
			row=0;
			col=0;
			this.writeInC2SDataSheet(worksheet1, col,row, p_hashMap);
			workbook.write();
		}
		catch(Exception e){
			_log.errorTrace(METHOD_NAME, e);
			_log.error("writeTrfRuleToExcel"," Exception e: "+e.getMessage());
		}
		finally
		{
			try{if(workbook!=null)workbook.close();}catch(Exception e)
			{
				_log.errorTrace(METHOD_NAME,e);
			}
			worksheet1=null;
			worksheet2=null;
			workbook=null;
			if(_log.isDebugEnabled())_log.debug("writeTrfRuleToExcel"," Exiting");
		}
	}
    //written by Ashutosh
  //method to populate data of master sheet in geography-cell id mapping module
    private int writeC2SMasterDataListing(WritableSheet worksheet2,int col,int row, HashMap p_hashMap)throws Exception{
		if(_log.isDebugEnabled())
		    _log.debug("writeC2SMasterDataListing"," p_hashMap size="+p_hashMap.size()+" p_locale: "+p_locale+" newCol="+col+" newRow="+row);
		final String METHOD_NAME = "writeC2SMasterDataListing";
		try 
        {
			//ASHU changes
			int newRow = row;
			int newCol = col;
        	String keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.templatesheet.availabledomains");
        	Label label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);
        	newRow++;
        	keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.templatesheet.availabledomainsmention");
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);
        	newRow++;
            newCol = 0;
			keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.templatesheet.channeldomaincode");
			label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);
        	keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.templatesheet.channeldomainname");
        	++newCol;
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);
        	newRow++;
        	newCol=0;
            ArrayList channelDomainList = (ArrayList)p_hashMap.get("channelDomainList");
        	ListValueVO channelDomainVO = null;
        	ListValueVO rsubscriberTypeVO = null;
        	ListValueVO rsubscriberServiceClassVO = null;
        	ListValueVO serviceTypeVO = null;
        	ListValueVO subServiceTypeIdVO = null;
        	ListValueVO cardGroupIdVO = null;
        	ListValueVO domCatMapVO = null;
        	int channelDomainListSize = channelDomainList.size();
        	if(channelDomainList!=null && channelDomainList.size()>0)
        	{
        		for(int dk=0,dl= channelDomainListSize;dk<dl;dk++)
        		{
        			channelDomainVO = (ListValueVO)channelDomainList.get(dk);
                    label = new Label(newCol,newRow,channelDomainVO.getValue());
        	        worksheet2.addCell(label);
        	        label = new Label(newCol+1,newRow,channelDomainVO.getLabel());
        	        worksheet2.addCell(label);
        			newRow++;
        		}
        	}
        	
        	//
        	newRow++;
            newCol = 0;
            keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.templatesheet.availabledreqgateways");
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);
        	newRow++; 
        	newCol = 0;
        	keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.templatesheet.reqgatewaycode");
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);
        	newCol++;
        	keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.templatesheet.reqgatewayname");
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);
        	newRow++; 
        	newCol = 0;
            ArrayList gList = (ArrayList)p_hashMap.get("requestgatewaylist");
            int gListSize = gList.size();
            for(int i=0; i< gListSize; i++) {
            	newCol = 0;
            	ListValueVO lVO = (ListValueVO)gList.get(i);
            	label = new Label(newCol,newRow,lVO.getValue());
				worksheet2.addCell(label);
				newCol++;
				label = new Label(newCol++,newRow,lVO.getLabel());
				worksheet2.addCell(label);
				newRow++;
            }
            
        	//
        	newRow++;
            newCol = 0;	        	
        	//category info for master sheet
        	newRow++;
        	keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.templatesheet.availablecategories");
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);
        	newRow++;
        	keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.templatesheet.availablecategoriesmention");
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);
        	newRow++;
            newCol = 0;
        	keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.templatesheet.channeldomaincode");
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);
        	keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.mastersheet.categorycode");
        	++newCol;
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);
        	keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.mastersheet.categoryname");
        	++newCol;
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);
         	keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.mastersheet.gradecode");
         	++newCol;
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);
        	keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.mastersheet.gradename");
        	++newCol;
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);
        	newRow++;            
        	newCol = 0;
        	ArrayList domainCategoryMappingList = (ArrayList)p_hashMap.get("categoryDomainMappingList");
        	int domainCategorySize = domainCategoryMappingList.size();
        	if(domainCategoryMappingList!=null && domainCategoryMappingList.size()>0)
        	{
        		for(int dk=0,dl= domainCategorySize;dk<dl;dk++)
        		{
        			domCatMapVO = (ListValueVO)domainCategoryMappingList.get(dk);
        			String arr[] = domCatMapVO.getValue().split(":");
        			if(arr.length==4) {
        				label = new Label(newCol,newRow,domCatMapVO.getLabel());
        				worksheet2.addCell(label);
        				label = new Label(newCol+1,newRow,arr[0]);
        				worksheet2.addCell(label);
        				label = new Label(newCol+2,newRow,arr[1]);
        				worksheet2.addCell(label);
        				label = new Label(newCol+3,newRow,arr[2]);
        				worksheet2.addCell(label);
        				label = new Label(newCol+4,newRow,arr[3]);
        				worksheet2.addCell(label);
        			}
        			newRow++;
        		}
        	}
        	
        	//adding the Status and Modify details in the master sheet
        	newRow++;
            newCol = 0;
            keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.templatesheet.availablestatus");
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);
        	newRow++; 
        	newCol = 0;
        	keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.templatesheet.status.comment");
        	label = new Label(newCol,newRow,keyName);
        	worksheet2.addCell(label);
        	// modify
        	
        	newRow+=2;
            newCol = 0;
            keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.templatesheet.modifystatus");
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);
        	newRow++; 
        	newCol = 0;
        	keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.templatesheet.modify.flags");
        	label = new Label(newCol,newRow,keyName);
        	worksheet2.addCell(label);
        	
        	//
        	
        	
        	newRow+=2;
        	newCol = 0;
        	keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.templatesheet.channeldomaincode");
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);
        	keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.mastersheet.channeldomainname");
        	++newCol;
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);

        	keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.mastersheet.receivertypecode");
        	++newCol;
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);
        	keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.mastersheet.receivertypename");
        	++newCol;
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);

        	keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.mastersheet.receiverserviceclasscode");
        	++newCol;
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);
        	keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.mastersheet.receiverserviceclassname");
        	++newCol;
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);

            keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.mastersheet.servicetypecode");
            ++newCol;
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);
        	keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.mastersheet.servicetypename");
        	++newCol;
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);

        	keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.mastersheet.subservicescode");
        	++newCol;
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);
        	keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.mastersheet.subservicesname");
        	++newCol;
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);

        	keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.mastersheet.cardgroupcode");
        	++newCol;
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);
        	keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.mastersheet.cardgroupname");
        	++newCol;
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet2.addCell(label);
            
        	ArrayList rsubscriberTypeList = (ArrayList)p_hashMap.get("subscriberTypeList");
        	ArrayList rsubscriberServiceTypeList = (ArrayList)p_hashMap.get("subscriberServiceTypeList");
        	ArrayList serviceTypeList = (ArrayList)p_hashMap.get("serviceTypeList");
        	ArrayList subServiceTypeIdList = (ArrayList)p_hashMap.get("subServiceTypeIdList");
        	ArrayList cardGroupIdList = (ArrayList)p_hashMap.get("cardGroupIdList");
        	newRow++;
        	newCol=0;
        	int rsubscriberTypeListSize = rsubscriberTypeList.size();
        	int rsubscriberServiceListSize = rsubscriberServiceTypeList.size();
        	int serviceListsSize = serviceTypeList.size();
        	int subServiceIdListSize = subServiceTypeIdList.size();
        	int cardGroupIdListSize = cardGroupIdList.size();
        	if(channelDomainList!=null && channelDomainList.size()>0)
        	{
        		for(int dk=0,dl=channelDomainListSize;dk<dl;dk++)
        		{
        			channelDomainVO = (ListValueVO)channelDomainList.get(dk);
                    label = new Label(newCol,newRow,channelDomainVO.getValue());
        	        worksheet2.addCell(label);
        	        label = new Label(newCol+1,newRow,channelDomainVO.getLabel());
        	        worksheet2.addCell(label);
        	        newRow++;  
        			for(int k=0,l= rsubscriberTypeListSize;k<l;k++)
        			{
        				rsubscriberTypeVO = (ListValueVO)rsubscriberTypeList.get(k);
        				label = new Label(newCol+2,newRow,rsubscriberTypeVO.getValue());
        				worksheet2.addCell(label);
        				label = new Label(newCol+3,newRow,rsubscriberTypeVO.getLabel());
        				worksheet2.addCell(label);
        				newRow++;
        				for(int i=0,j= rsubscriberServiceListSize;i<j;i++)
        				{
        					rsubscriberServiceClassVO = (ListValueVO)rsubscriberServiceTypeList.get(i);
        					if(rsubscriberTypeVO.getValue().equals(rsubscriberServiceClassVO.getValue().split(":")[0]))
        					{
        						label = new Label(newCol+4,newRow,rsubscriberServiceClassVO.getValue().split(":")[1]);
        						worksheet2.addCell(label);
        						label = new Label(newCol+5,newRow,rsubscriberServiceClassVO.getLabel());
        						worksheet2.addCell(label);
        						newRow++;
        						for(int s=0,t= serviceListsSize;s<t;s++)
        						{
        							serviceTypeVO = (ListValueVO)serviceTypeList.get(s);
        							label = new Label(newCol+6,newRow,serviceTypeVO.getValue());
        							worksheet2.addCell(label);
        							label = new Label(newCol+7,newRow,serviceTypeVO.getLabel());
        							worksheet2.addCell(label);
        							newRow++;
        							for(int x=0,y= subServiceIdListSize;x<y;x++)
        							{
        								subServiceTypeIdVO =(ListValueVO)subServiceTypeIdList.get(x);
        								if(rsubscriberTypeVO.getValue().equals(subServiceTypeIdVO.getValue().split(":")[1]) && serviceTypeVO.getValue().equals(subServiceTypeIdVO.getValue().split(":")[3]))
        								{
        									label = new Label(newCol+8,newRow,subServiceTypeIdVO.getValue().split(":")[2]);
        									worksheet2.addCell(label);
        									label = new Label(newCol+9,newRow,subServiceTypeIdVO.getLabel());
        									worksheet2.addCell(label);
        									newRow++;
        									for(int u=0,v= cardGroupIdListSize;u<v;u++)
        									{
        										cardGroupIdVO =(ListValueVO)cardGroupIdList.get(u);
        										if((subServiceTypeIdVO.getValue().split(":")[2]).equals(cardGroupIdVO.getValue().split(":")[0]) && (cardGroupIdVO.getValue().split(":")[2]).equals(serviceTypeVO.getValue()))
        										{
        											label = new Label(newCol+10,newRow,cardGroupIdVO.getValue().split(":")[1]);
        											worksheet2.addCell(label);
        											label = new Label(newCol+11,newRow,cardGroupIdVO.getLabel());
        											worksheet2.addCell(label);
        											newRow++;
        										} // end if
        									} // end for loop
        								} // end if
        							} // end for loop
        						} // end for loop
        					} // end if
        				} // end for loop
        			} // end for loop
        		} // end for loop
        	} // end if
           
            return newRow;
            
        } 
        catch (RowsExceededException e) 
        {
            _log.errorTrace(METHOD_NAME, e);
			_log.error("writeC2SMasterDataListing"," Exception e: "+e.getMessage());
			throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
			_log.error("writeC2SMasterDataListing"," Exception e: "+e.getMessage());
			throw e;
        }
	}
    
     //written by Ashutosh
    //method to populate data of template sheet in geography-cell id mapping module
    private void writeInC2SDataSheet(WritableSheet worksheet1, int col,int row, HashMap p_hashMap) throws Exception
    {
        if(_log.isDebugEnabled())
		    _log.debug("writeInC2SDataSheet"," p_hashMap size="+p_hashMap.size()+" p_locale: "+p_locale+" col="+col+" row="+row);
        final String METHOD_NAME = "writeInC2SDataSheet";
        try 
        {  
        	int newCol=0;
            int newRow = 0;
        	newRow=newRow+2;
        	newCol=0;
        	//incrementing column field after creating label
        	String keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.templatesheet.gatewaycode");        	
        	Label label = new Label(newCol,newRow,keyName, times16format);
        	worksheet1.addCell(label);
        	newCol++;
        	keyName = p_messages.getMessage(p_locale,"batchtransferrules.xlsfile.templatesheet.channeldomain");
        	
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet1.addCell(label);
        	newCol++;
        	keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.templatesheet.channelcategory");
        	
        	label = new Label(newCol,newRow,keyName, times16format);
        	worksheet1.addCell(label);
        	newCol++;
             keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.templatesheet.channelgrade");
             
             label = new Label(newCol,newRow,keyName, times16format);
             worksheet1.addCell(label);
             newCol++;
             keyName = p_messages.getMessage(p_locale,"batchtransferrules.xlsfile.templatesheet.receivertype");
             
             label = new Label(newCol,newRow,keyName, times16format);
             worksheet1.addCell(label);
             newCol++;
             keyName = p_messages.getMessage(p_locale,"batchtransferrules.xlsfile.templatesheet.receiverserviceclass");
             
             label = new Label(newCol,newRow,keyName, times16format);
             worksheet1.addCell(label);
             newCol++;
             keyName = p_messages.getMessage(p_locale,"batchtransferrules.xlsfile.templatesheet.servicetype");
             
             label = new Label(newCol,newRow,keyName, times16format);
             worksheet1.addCell(label);
             newCol++;
             keyName = p_messages.getMessage(p_locale,"batchtransferrules.xlsfile.templatesheet.subservice");
             
             label = new Label(newCol,newRow,keyName, times16format);
             worksheet1.addCell(label);
             newCol++;
             keyName = p_messages.getMessage(p_locale,"batchtransferrules.xlsfile.templatesheet.cardgroupset");
             
             label = new Label(newCol,newRow,keyName, times16format);
             worksheet1.addCell(label);
             newCol++;
             keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.templatesheet.status");
             
             label = new Label(newCol,newRow,keyName, times16format);
             worksheet1.addCell(label);
             newCol++;
             keyName = p_messages.getMessage(p_locale,"transferrules.xlsfile.templatesheet.ismodify");
             
             label = new Label(newCol,newRow,keyName, times16format);
             worksheet1.addCell(label);
             newCol++;            
             //ASHU
             ArrayList transferRuleList = (ArrayList)p_hashMap.get("transferrulelist");
             newCol = 0;
             newRow++;
             if(transferRuleList!=null && transferRuleList.size()>0)
         	{
         		for(int dk=0,dl=transferRuleList.size();dk<dl;dk++)
         		{ 
         			TransferRulesVO trVO = (TransferRulesVO)transferRuleList.get(dk);
         			label = new Label(newCol,newRow,trVO.getGatewayCode());
                     worksheet1.addCell(label);
                     newCol++;
                     label = new Label(newCol,newRow,trVO.getDomainCode());
                     worksheet1.addCell(label);
                     newCol++;
         	        label = new Label(newCol,newRow,trVO.getCategoryCode());
         	        worksheet1.addCell(label);
         	       newCol++;
         	        label = new Label(newCol,newRow,trVO.getGradeCode());
         	        worksheet1.addCell(label);
         	       newCol++;
         	        label = new Label(newCol,newRow,trVO.getReceiverSubscriberType());
         	        worksheet1.addCell(label);
         	       newCol++;
         	        label = new Label(newCol,newRow,trVO.getReceiverServiceClassID());
         	        worksheet1.addCell(label);
         	       newCol++;
         	        label = new Label(newCol,newRow,trVO.getServiceType());
         	        worksheet1.addCell(label);
         	       newCol++;
         	        label = new Label(newCol,newRow,trVO.getSubServiceTypeId());
         	        worksheet1.addCell(label);
         	       newCol++;
         	        label = new Label(newCol,newRow,trVO.getCardGroupSetID());
         	        worksheet1.addCell(label);        	        
         	        //status and is modify
         	       newCol++;
         	        label = new Label(newCol,newRow,trVO.getStatus());
         	        worksheet1.addCell(label);
         	       newCol++;
         	        label = new Label(newCol,newRow,PretupsI.MODIFY_ALLOWED_NO);
         	        worksheet1.addCell(label);
         			newRow++;
         			newCol = 0;
         		}
         	}
            
        }
        catch (RowsExceededException e) 
        {
            _log.errorTrace(METHOD_NAME, e);
			_log.error("writeInC2SDataSheet"," Exception e: "+e.getMessage());
			throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
			_log.error("writeInC2SDataSheet"," Exception e: "+e.getMessage());
			throw e;
        }
    }
    
    /**
	 * readExcel
	 * @param p_excelID
	 * @param p_fileName
	 * @return
	 * @throws Exception
	 */
    
	public String[][] readTRfRuleInExcel(String p_excelID,String p_fileName) 
	{
		if(_log.isDebugEnabled())_log.debug("readTRfRuleInExcel"," p_excelID: "+p_excelID+" p_fileName: "+p_fileName);
		final String METHOD_NAME = "readTRfRuleInExcel";
		String strArr[][] = null;
		Workbook workbook = null;
		Sheet excelsheet = null;
		File f = null;
		
		try
		{
			f = new File(p_fileName);
			workbook = Workbook.getWorkbook(f);
			
			excelsheet = workbook.getSheet(0);
			
			int noOfRows = excelsheet.getRows();
			int noOfcols = excelsheet.getColumns();
			strArr = new String[noOfRows][noOfcols];
			Cell cell = null;
			String content = null;
			String key=null;
			int[] indexMapArray=new int[noOfcols]; 
			String indexStr=null;
			for(int col = 0; col < noOfcols; col++)
			{
			    indexStr=null;
				key=ExcelFileConstants.getReadProperty(p_excelID,String.valueOf(col));
				if(key==null)
					key=String.valueOf(col);
				indexStr=ExcelFileConstants.getReadProperty(p_excelID,String.valueOf(col));
				if(indexStr==null)
					indexStr=String.valueOf(col);
				indexMapArray[col]=Integer.parseInt(indexStr);
				strArr[0][indexMapArray[col]] = key;
				
			}
			for(int row = 1; row < noOfRows; row++)
			{
				for(int col = 0; col < noOfcols; col++)
				{
					cell = excelsheet.getCell(col,row);
					content = cell.getContents();
					content=content.replaceAll("\n", " ");
					content=content.replaceAll("\r", " ");
					strArr[row][indexMapArray[col]] = content;
				}
			}
			
			return strArr;
		}
		catch(Exception e)
		{
			_log.errorTrace(METHOD_NAME, e);
			_log.error("readTRfRuleInExcel"," Exception e: "+e.getMessage());
		}
		finally
		{
			try{if(workbook!=null)workbook.close();}catch(Exception e)
			{
				_log.errorTrace(METHOD_NAME, e);
			}
			workbook=null;
			excelsheet=null;
			if(_log.isDebugEnabled())_log.debug("readTRfRuleInExcel"," Exiting strArr: "+strArr);
		}
		return strArr;
	}
	
	private void createP2PMasterDataHeader(WritableSheet worksheet2, int col, int row) throws WriteException {
		 
		if(_log.isDebugEnabled())
			    _log.debug("createP2PMasterDataHeader","Entered  col="+col+" row="+row);
	        
		try{
			String keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.mastersheet.gatewayCode");
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);
			keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.mastersheet.gatewayCodeName");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
           /* COMMENTED FOR CELL GROUP ID. WILL DISPLAY ONCE IMPLEMENTED FOR C2S TRANSFER RULE
            * keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.mastersheet.groupCellID");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.mastersheet.groupCellIDName");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);*/
			keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.mastersheet.sendertypecode");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.mastersheet.sendertypename");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.mastersheet.senderserviceclasscode");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.mastersheet.senderserviceclassname");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.mastersheet.receivertypecode");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.mastersheet.receivertypename");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.mastersheet.receiverserviceclasscode");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.mastersheet.receiverserviceclassname");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.mastersheet.servicetypecode");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.mastersheet.servicetypename");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.mastersheet.subservicescode");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.mastersheet.subservicesname");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.mastersheet.cardgroupcode");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);
            keyName = p_messages.getMessage(p_locale, "transferrules.xlsfile.mastersheet.cardgroupname");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);

		
		}catch(WriteException we){
			we.printStackTrace();
			_log.error("createP2PMasterDataHeader"," Exception Write Exception: "+we.getMessage());
			throw we;
			
		}
		
		if(_log.isDebugEnabled())
		    _log.debug("createP2PMasterDataHeader"," Exited ");
		
	}
    public void writeTrfRuleToExcelNew(String p_excelID,HashMap p_hashMap,Locale locale,String p_fileName){
        final String METHOD_NAME = "writeTrfRuleToExcelNew";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
        }
        WritableSheet worksheet1 = null;
        WritableSheet worksheet2 = null;
        int col=0;
        int row=0;
        String keyName=null;
        Label label = null;

        try
        {
            workbook = Workbook.createWorkbook(new File(p_fileName));
            worksheet1 = workbook.createSheet("Template Sheet", 0);
            worksheet2 = workbook.createSheet("Master Sheet", 1);
            String arr[] = {(String) "C2S"};
            keyName =RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TEMP_DOWNLOAD_C2S1, arr);
            label = new Label(col,row,keyName, times16format);
            worksheet2.mergeCells(col,row,col+11,row);
            worksheet2.addCell(label);
            row++;
            col=0;
            row=this.writeC2SMasterDataListingNew(worksheet2, col, row, p_hashMap,locale);
            row=0;
            col=0;
            keyName =RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TEMP_DOWNLOAD_C2S, arr);
            label = new Label(col,row,keyName, times16format);
            worksheet1.mergeCells(col,row,col+11,row);
            worksheet1.addCell(label);
            row=0;
            col=0;
            this.writeInC2SDataSheetNew(worksheet1, col,row, p_hashMap,locale);
            workbook.write();
        }
        catch(Exception e){
            _log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME," Exception e: "+e.getMessage());
        }
        finally
        {
            try{if(workbook!=null)workbook.close();}catch(Exception e)
            {
                _log.errorTrace(METHOD_NAME,e);
            }
            worksheet1=null;
            worksheet2=null;
            workbook=null;
            if(_log.isDebugEnabled())_log.debug(METHOD_NAME," Exiting");
        }
    }

    private int writeC2SMasterDataListingNew(WritableSheet worksheet2, int col, int row, HashMap p_hashMap, Locale locale) throws Exception {
        final String METHOD_NAME = "writeTrfRuleToExcelNew";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
        }
        try {
            int newRow = row;
            int newCol = col;
            String arr[] = {(String) ""};
            String keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.templatesheet.availabledomains", arr);
            Label label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);
            newRow++;
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.templatesheet.availabledomainsmention", arr);
            label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);
            newRow++;
            newCol = 0;
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.templatesheet.channeldomaincode", arr);
            label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.templatesheet.channeldomainname", arr);
            ++newCol;
            label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);
            newRow++;
            newCol = 0;
            ArrayList channelDomainList = (ArrayList) p_hashMap.get("channelDomainList");
            ListValueVO channelDomainVO = null;
            ListValueVO rsubscriberTypeVO = null;
            ListValueVO rsubscriberServiceClassVO = null;
            ListValueVO serviceTypeVO = null;
            ListValueVO subServiceTypeIdVO = null;
            ListValueVO cardGroupIdVO = null;
            ListValueVO domCatMapVO = null;
            int channelDomainListSize = channelDomainList.size();
            if (channelDomainList != null && channelDomainList.size() > 0) {
                for (int dk = 0, dl = channelDomainListSize; dk < dl; dk++) {
                    channelDomainVO = (ListValueVO) channelDomainList.get(dk);
                    label = new Label(newCol, newRow, channelDomainVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(newCol + 1, newRow, channelDomainVO.getLabel());
                    worksheet2.addCell(label);
                    newRow++;
                }
            }

            newRow++;
            newCol = 0;
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.templatesheet.availabledreqgateways", arr);
            label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);
            newRow++;
            newCol = 0;
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.templatesheet.reqgatewaycode", arr);
            label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);
            newCol++;
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.templatesheet.reqgatewayname", arr);
            label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);
            newRow++;
            newCol = 0;
            ArrayList gList = (ArrayList) p_hashMap.get("requestgatewaylist");
            int gListSize = gList.size();
            for (int i = 0; i < gListSize; i++) {
                newCol = 0;
                ListValueVO lVO = (ListValueVO) gList.get(i);
                label = new Label(newCol, newRow, lVO.getValue());
                worksheet2.addCell(label);
                newCol++;
                label = new Label(newCol++, newRow, lVO.getLabel());
                worksheet2.addCell(label);
                newRow++;
            }
            newRow++;
            newCol = 0;
            newRow++;
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.templatesheet.availablecategories", arr);
            label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);
            newRow++;
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.templatesheet.availablecategoriesmention", arr);
            label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);
            newRow++;
            newCol = 0;
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.templatesheet.channeldomaincode", arr);
            label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.mastersheet.categorycode", arr);
            ++newCol;
            label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.mastersheet.categoryname", arr);
            ++newCol;
            label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.mastersheet.gradecode", arr);
            ++newCol;
            label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.mastersheet.gradename", arr);
            ++newCol;
            label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);
            newRow++;
            newCol = 0;
            ArrayList domainCategoryMappingList = (ArrayList) p_hashMap.get("categoryDomainMappingList");
            int domainCategorySize = domainCategoryMappingList.size();
            if (domainCategoryMappingList != null && domainCategoryMappingList.size() > 0) {
                for (int dk = 0, dl = domainCategorySize; dk < dl; dk++) {
                    domCatMapVO = (ListValueVO) domainCategoryMappingList.get(dk);
                    String arr1[] = domCatMapVO.getValue().split(":");
                    if (arr1.length == 4) {
                        label = new Label(newCol, newRow, domCatMapVO.getLabel());
                        worksheet2.addCell(label);
                        label = new Label(newCol + 1, newRow, arr1[0]);
                        worksheet2.addCell(label);
                        label = new Label(newCol + 2, newRow, arr1[1]);
                        worksheet2.addCell(label);
                        label = new Label(newCol + 3, newRow, arr1[2]);
                        worksheet2.addCell(label);
                        label = new Label(newCol + 4, newRow, arr1[3]);
                        worksheet2.addCell(label);
                    }
                    newRow++;
                }
            }
            newRow++;
            newCol = 0;
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.templatesheet.availablestatus", arr);
            label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);
            newRow++;
            newCol = 0;
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.templatesheet.status.comment", arr);
            label = new Label(newCol, newRow, keyName);
            worksheet2.addCell(label);
            newRow += 2;
            newCol = 0;
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.templatesheet.modifystatus", arr);
            label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);
            newRow++;
            newCol = 0;
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.templatesheet.modify.flags", arr);
            label = new Label(newCol, newRow, keyName);
            worksheet2.addCell(label);
            newRow += 2;
            newCol = 0;
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.templatesheet.channeldomaincode", arr);
            label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.mastersheet.channeldomainname", arr);
            ++newCol;
            label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.mastersheet.receivertypecode", arr);
            ++newCol;
            label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.mastersheet.receivertypename", arr);
            ++newCol;
            label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.mastersheet.receiverserviceclasscode", arr);
            ++newCol;
            label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.mastersheet.receiverserviceclassname", arr);
            ++newCol;
            label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.mastersheet.servicetypecode", arr);
            ++newCol;
            label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.mastersheet.servicetypename", arr);
            ++newCol;
            label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.mastersheet.subservicescode", arr);
            ++newCol;
            label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.mastersheet.subservicesname", arr);
            ++newCol;
            label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.mastersheet.cardgroupcode", arr);
            ++newCol;
            label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.mastersheet.cardgroupname", arr);
            ++newCol;
            label = new Label(newCol, newRow, keyName, times16format);
            worksheet2.addCell(label);

            ArrayList rsubscriberTypeList = (ArrayList) p_hashMap.get("subscriberTypeList");
            ArrayList rsubscriberServiceTypeList = (ArrayList) p_hashMap.get("subscriberServiceTypeList");
            ArrayList serviceTypeList = (ArrayList) p_hashMap.get("serviceTypeList");
            ArrayList subServiceTypeIdList = (ArrayList) p_hashMap.get("subServiceTypeIdList");
            ArrayList cardGroupIdList = (ArrayList) p_hashMap.get("cardGroupIdList");
            newRow++;
            newCol = 0;
            int rsubscriberTypeListSize = rsubscriberTypeList.size();
            int rsubscriberServiceListSize = rsubscriberServiceTypeList.size();
            int serviceListsSize = serviceTypeList.size();
            int subServiceIdListSize = subServiceTypeIdList.size();
            int cardGroupIdListSize = cardGroupIdList.size();
            if (channelDomainList != null && channelDomainList.size() > 0) {
                for (int dk = 0, dl = channelDomainListSize; dk < dl; dk++) {
                    channelDomainVO = (ListValueVO) channelDomainList.get(dk);
                    label = new Label(newCol, newRow, channelDomainVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(newCol + 1, newRow, channelDomainVO.getLabel());
                    worksheet2.addCell(label);
                    newRow++;
                    for (int k = 0, l = rsubscriberTypeListSize; k < l; k++) {
                        rsubscriberTypeVO = (ListValueVO) rsubscriberTypeList.get(k);
                        label = new Label(newCol + 2, newRow, rsubscriberTypeVO.getValue());
                        worksheet2.addCell(label);
                        label = new Label(newCol + 3, newRow, rsubscriberTypeVO.getLabel());
                        worksheet2.addCell(label);
                        newRow++;
                        for (int i = 0, j = rsubscriberServiceListSize; i < j; i++) {
                            rsubscriberServiceClassVO = (ListValueVO) rsubscriberServiceTypeList.get(i);
                            if (rsubscriberTypeVO.getValue().equals(rsubscriberServiceClassVO.getValue().split(":")[0])) {
                                label = new Label(newCol + 4, newRow, rsubscriberServiceClassVO.getValue().split(":")[1]);
                                worksheet2.addCell(label);
                                label = new Label(newCol + 5, newRow, rsubscriberServiceClassVO.getLabel());
                                worksheet2.addCell(label);
                                newRow++;
                                for (int s = 0, t = serviceListsSize; s < t; s++) {
                                    serviceTypeVO = (ListValueVO) serviceTypeList.get(s);
                                    label = new Label(newCol + 6, newRow, serviceTypeVO.getValue());
                                    worksheet2.addCell(label);
                                    label = new Label(newCol + 7, newRow, serviceTypeVO.getLabel());
                                    worksheet2.addCell(label);
                                    newRow++;
                                    for (int x = 0, y = subServiceIdListSize; x < y; x++) {
                                        subServiceTypeIdVO = (ListValueVO) subServiceTypeIdList.get(x);
                                        if (rsubscriberTypeVO.getValue().equals(subServiceTypeIdVO.getValue().split(":")[1]) && serviceTypeVO.getValue().equals(subServiceTypeIdVO.getValue().split(":")[3])) {
                                            label = new Label(newCol + 8, newRow, subServiceTypeIdVO.getValue().split(":")[2]);
                                            worksheet2.addCell(label);
                                            label = new Label(newCol + 9, newRow, subServiceTypeIdVO.getLabel());
                                            worksheet2.addCell(label);
                                            newRow++;
                                            for (int u = 0, v = cardGroupIdListSize; u < v; u++) {
                                                cardGroupIdVO = (ListValueVO) cardGroupIdList.get(u);
                                                if ((subServiceTypeIdVO.getValue().split(":")[2]).equals(cardGroupIdVO.getValue().split(":")[0]) && (cardGroupIdVO.getValue().split(":")[2]).equals(serviceTypeVO.getValue())) {
                                                    label = new Label(newCol + 10, newRow, cardGroupIdVO.getValue().split(":")[1]);
                                                    worksheet2.addCell(label);
                                                    label = new Label(newCol + 11, newRow, cardGroupIdVO.getLabel());
                                                    worksheet2.addCell(label);
                                                    newRow++;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return newRow;
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
    private void writeInC2SDataSheetNew(WritableSheet worksheet1, int col,int row, HashMap p_hashMap,Locale locale) throws Exception
    {
        final String METHOD_NAME = "writeInC2SDataSheetNew";
        if(_log.isDebugEnabled())
            _log.debug(METHOD_NAME," p_hashMap size="+p_hashMap.size()+" p_locale: "+p_locale+" col="+col+" row="+row);
        try
        {
            int newCol=0;
            int newRow = 0;
            newRow=newRow+2;
            newCol=0;
            String arr[] = {(String) ""};
            String keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.templatesheet.gatewaycode", arr);
            Label label = new Label(newCol,newRow,keyName, times16format);
            worksheet1.addCell(label);
            newCol++;
            keyName = RestAPIStringParser.getMessage(locale, "batchtransferrules.xlsfile.templatesheet.channeldomain", arr);
            label = new Label(newCol,newRow,keyName, times16format);
            worksheet1.addCell(label);
            newCol++;
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.templatesheet.channelcategory", arr);
            label = new Label(newCol,newRow,keyName, times16format);
            worksheet1.addCell(label);
            newCol++;
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.templatesheet.channelgrade", arr);
            label = new Label(newCol,newRow,keyName, times16format);
            worksheet1.addCell(label);
            newCol++;
            keyName = RestAPIStringParser.getMessage(locale, "batchtransferrules.xlsfile.templatesheet.receivertype", arr);
            label = new Label(newCol,newRow,keyName, times16format);
            worksheet1.addCell(label);
            newCol++;
            keyName = RestAPIStringParser.getMessage(locale, "batchtransferrules.xlsfile.templatesheet.receiverserviceclass", arr);
            label = new Label(newCol,newRow,keyName, times16format);
            worksheet1.addCell(label);
            newCol++;
            keyName = RestAPIStringParser.getMessage(locale, "batchtransferrules.xlsfile.templatesheet.servicetype", arr);
            label = new Label(newCol,newRow,keyName, times16format);
            worksheet1.addCell(label);
            newCol++;
            keyName = RestAPIStringParser.getMessage(locale, "batchtransferrules.xlsfile.templatesheet.subservice", arr);
            label = new Label(newCol,newRow,keyName, times16format);
            worksheet1.addCell(label);
            newCol++;
            keyName = RestAPIStringParser.getMessage(locale, "batchtransferrules.xlsfile.templatesheet.cardgroupset", arr);
            label = new Label(newCol,newRow,keyName, times16format);
            worksheet1.addCell(label);
            newCol++;
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.templatesheet.status", arr);
            label = new Label(newCol,newRow,keyName, times16format);
            worksheet1.addCell(label);
            newCol++;
            keyName = RestAPIStringParser.getMessage(locale, "transferrules.xlsfile.templatesheet.ismodify", arr);
            label = new Label(newCol,newRow,keyName, times16format);
            worksheet1.addCell(label);
            ArrayList transferRuleList = (ArrayList)p_hashMap.get("transferrulelist");
            newCol = 0;
            newRow++;
            if(transferRuleList!=null && transferRuleList.size()>0)
            {
                for(int dk=0,dl=transferRuleList.size();dk<dl;dk++)
                {
                    TransferRulesVO trVO = (TransferRulesVO)transferRuleList.get(dk);
                    label = new Label(newCol,newRow,trVO.getGatewayCode());
                    worksheet1.addCell(label);
                    newCol++;
                    label = new Label(newCol,newRow,trVO.getDomainCode());
                    worksheet1.addCell(label);
                    newCol++;
                    label = new Label(newCol,newRow,trVO.getCategoryCode());
                    worksheet1.addCell(label);
                    newCol++;
                    label = new Label(newCol,newRow,trVO.getGradeCode());
                    worksheet1.addCell(label);
                    newCol++;
                    label = new Label(newCol,newRow,trVO.getReceiverSubscriberType());
                    worksheet1.addCell(label);
                    newCol++;
                    label = new Label(newCol,newRow,trVO.getReceiverServiceClassID());
                    worksheet1.addCell(label);
                    newCol++;
                    label = new Label(newCol,newRow,trVO.getServiceType());
                    worksheet1.addCell(label);
                    newCol++;
                    label = new Label(newCol,newRow,trVO.getSubServiceTypeId());
                    worksheet1.addCell(label);
                    newCol++;
                    label = new Label(newCol,newRow,trVO.getCardGroupSetID());
                    worksheet1.addCell(label);
                    //status and is modify
                    newCol++;
                    label = new Label(newCol,newRow,trVO.getStatus());
                    worksheet1.addCell(label);
                    newCol++;
                    label = new Label(newCol,newRow,PretupsI.MODIFY_ALLOWED_NO);
                    worksheet1.addCell(label);
                    newRow++;
                    newCol = 0;
                }
            }

        }
        catch (RowsExceededException e)
        {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME," Exception e: "+e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME," Exception e: "+e.getMessage());
            throw e;
        }
    }
}

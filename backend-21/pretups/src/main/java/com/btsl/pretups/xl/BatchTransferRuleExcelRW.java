package com.btsl.pretups.xl;

/*
 * @# BatchTransferRuleExcelRW.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Sanjeew Kumar March 15,2007 Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2007 Bharti Telesoft Ltd.
 * This class use for read write in xls file for batch user creation.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.util.MessageResources;

import com.btsl.common.ListValueVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
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

    /**
     * @author sanjeew.kumar
     * @Method Name="writeExcel"
     * @param p_excelID
     * @param p_hashMap
     * @param messages
     * @param p_locale1
     * @param promotionLevel
     * @param p_fileName
     * @throws Exception
     */
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
            keyName = p_messages.getMessage("promotionaltransferrule.mastersheet.heading");
            label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 5, row);
            worksheet2.addCell(label);
            row++;
            row++;
            col = 0;
            /*
             * row=this.writeServiceClassDes(worksheet2, col, row, p_hashMap);
             * row++;
             * col=0;
             */
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

    /**
     * @author sanjeew.kumar
     * @method Name: readExcel
     * @param p_excelID
     * @param p_fileName
     * @return String array
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

    /**
     * @author sanjeew.kumar
     *         method: writeServiceClassDes
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return row
     * @throws Exception
     */
    /*
     * private int writeServiceClassDes(WritableSheet worksheet2, int col,int
     * row, HashMap p_hashMap) throws Exception
     * {
     * final string METHOD_NAME = "writeServiceClassDes";
     * if(_log.isDebugEnabled())
     * _log.debug("writeServiceClassDescription"," p_hashMap size="+p_hashMap.size
     * ()+" p_locale: "+p_locale+" col="+col+" row="+row);
     * try
     * {
     * //For Service class description
     * String keyName = p_messages.getMessage(p_locale,
     * "promotionaltransferrule.mastersheet.serviceclassDesc");
     * 
     * Label label = new Label(col,row,keyName, times16format);
     * worksheet2.mergeCells(col,row,col+2,row);
     * worksheet2.addCell(label);
     * row++;
     * col=0;
     * 
     * //Subscriber type
     * keyName = p_messages.getMessage(p_locale,
     * "promotionaltransferrule.mastersheet.subscribertype");
     * label = new Label(col,row,keyName, times16format);
     * worksheet2.addCell(label);
     * //Service class name
     * keyName = p_messages.getMessage(p_locale,
     * "promotionaltransferrule.mastersheet.serviceclassname");
     * label = new Label(++col,row,keyName, times16format);
     * worksheet2.addCell(label);
     * //service class id
     * keyName = p_messages.getMessage(p_locale,
     * "promotionaltransferrule.mastersheet.serviceclassid");
     * label = new Label(++col,row,keyName, times16format);
     * worksheet2.addCell(label);
     * //interface type id
     * //keyName = p_messages.getMessage(p_locale,
     * "promotionaltransferrule.mastersheet.interfacetypeid");
     * //label = new Label(++col,row,keyName, times16format);
     * //worksheet2.addCell(label);
     * row++;
     * col=0;
     * 
     * ArrayList list_Pre =
     * (ArrayList)p_hashMap.get(PretupsI.PROMOTIONAL_INTERFACE_CATEGORY_CLASS);
     * ListValueVO listValueVO= null;
     * 
     * if(list_Pre!=null)
     * {
     * col=0;
     * int pos=0;
     * label = new Label(col,row,PretupsI.INTERFACE_CATEGORY_PRE);
     * worksheet2.addCell(label);
     * int pre=0,post=0;
     * for(int i=0 ,j=list_Pre.size();i<j;i++)
     * {
     * pos=0;
     * col=0;
     * listValueVO = (ListValueVO)list_Pre.get(i);
     * 
     * String serviceClassValue=listValueVO.getValue();
     * String
     * SubscriberType=serviceClassValue.substring(0,(pos=serviceClassValue
     * .indexOf(":")));
     * label = new Label(col++,row,SubscriberType);
     * if(SubscriberType.equalsIgnoreCase(PretupsI.INTERFACE_CATEGORY_PRE)&&
     * pre++==0)
     * worksheet2.addCell(label);
     * if(SubscriberType.equalsIgnoreCase(PretupsI.INTERFACE_CATEGORY_POST)&&post
     * ++==0)
     * worksheet2.addCell(label);
     * SubscriberType=(listValueVO.getLabel()).substring(0,(listValueVO.getLabel(
     * )).indexOf("("));
     * label = new Label(col++,row,SubscriberType);
     * worksheet2.addCell(label);
     * SubscriberType=serviceClassValue.substring(++pos,serviceClassValue.length(
     * ));
     * label = new Label(col++,row,SubscriberType);
     * worksheet2.addCell(label);
     * 
     * SubscriberType=serviceClassValue.substring(++pos);
     * label = new Label(col,row,SubscriberType);
     * worksheet2.addCell(label);
     * row++;
     * }
     * }
     * return row;
     * }
     * catch (RowsExceededException e)
     * {
     * _log.errorTrace(METHOD_NAME, e);
     * _log.error("writeServiceClassDescription"," Exception e: "+e.getMessage())
     * ;
     * throw e;
     * } catch (WriteException e) {
     * _log.errorTrace(METHOD_NAME, e);
     * _log.error("writeServiceClassDescription"," Exception e: "+e.getMessage())
     * ;
     * throw e;
     * }
     * }
     */
    /**
     * @author sanjeew.kumar
     *         Method: writeCardGroupDescription
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return row
     * @throws Exception
     */
    private int writeCardGroupDescription(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        final String METHOD_NAME = "writeCardGroupDescription";
        if (_log.isDebugEnabled()) {
            _log.debug("writeCardGroupDescription", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        try {
            String keyName = null;
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.serviceheading");
            // Card group Description
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            // Subscriber type
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.subscribertype");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);

            // Subscriber type name
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.subscribertypename");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);

            // Subscriber service class id
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.serviceclassid");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);

            // Subscriber service class name
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.serviceclassname");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);

            // service class name
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.serviceclassname");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);

            // Service type code
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.servicetypecod");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);

            // Service type name
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.servicetypename");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);

            // Subservice code
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.sebservicecode");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);

            // Subservice name
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.sebservicename");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);

            // card group set id
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.cardgroupsetid");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);

            // card group name
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.cardgroupname");
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
                    // System.out.println("1");
                    rsubscriberTypeVO = (ListValueVO) rsubscriberTypeList.get(sk);
                    label = new Label(0, row, rsubscriberTypeVO.getValue());
                    worksheet2.addCell(label);
                    label = new Label(1, row, rsubscriberTypeVO.getLabel());
                    worksheet2.addCell(label);
                    row++;
                    for (int si = 0, sj = rsubscriberServiceTypeList.size(); si < sj; si++) {
                        // System.out.println("2");
                        rsubscriberServiceClassVO = (ListValueVO) rsubscriberServiceTypeList.get(si);
                        if (rsubscriberTypeVO.getValue().equals(rsubscriberServiceClassVO.getValue().split(":")[0])) {
                            label = new Label(2, row, rsubscriberServiceClassVO.getValue().split(":")[1]);
                            worksheet2.addCell(label);
                            label = new Label(3, row, rsubscriberServiceClassVO.getLabel());
                            worksheet2.addCell(label);
                            row++;
                            for (int s = 0, t = serviceTypeList.size(); s < t; s++) {
                                // System.out.println("3");
                                serviceTypeVO = (ListValueVO) serviceTypeList.get(s);
                                label = new Label(4, row, serviceTypeVO.getValue());
                                worksheet2.addCell(label);
                                label = new Label(5, row, serviceTypeVO.getLabel());
                                worksheet2.addCell(label);
                                row++;
                                for (int x = 0, y = subServiceTypeIdList.size(); x < y; x++) {
                                    // System.out.println("4");
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
                              // =======================================================================================
                            /*
                             * String cardId=null;
                             * String cardServiceType=null;
                             * String cardGroupId=null;
                             * String cardGroupName=null;
                             * String servCode=null;
                             * String card=null;
                             * int cPos=0;
                             * for(int s=0,t=cardGroupList.size();s<t;s++)
                             * {
                             * listValueVO = (ListValueVO)cardGroupList.get(s);
                             * card=listValueVO.getValue();
                             * cardId=card.substring(0,(cPos=card.indexOf(":")));
                             * cardGroupId=card.substring(++cPos,(cPos=card.indexOf
                             * (":",cPos)));
                             * cardServiceType=card.substring(++cPos,card.length(
                             * ));
                             * cardGroupName=listValueVO.getLabel();
                             * for(int i = 0, listSize = serviceTypeList.size();
                             * i < listSize; i++) {
                             * listValueVO = (ListValueVO)
                             * serviceTypeList.get(i);
                             * if (cardServiceType.equalsIgnoreCase(listValueVO.
                             * getValue())) {
                             * label = new Label(4, row,
                             * listValueVO.getValue());
                             * worksheet2.addCell(label);
                             * label = new Label(5, row,
                             * listValueVO.getLabel());
                             * worksheet2.addCell(label);
                             * }
                             * }
                             * //label = new Label(2,row,cardIntType);
                             * //worksheet2.addCell(label);
                             * for(int i = 0, listSize = subService.size(); i <
                             * listSize; i++) {
                             * listValueVO = (ListValueVO) subService.get(i);
                             * servCode = listValueVO.getValue();
                             * servCode =
                             * servCode.substring(servCode.indexOf(":") + 1);
                             * if (cardId.equalsIgnoreCase(servCode)) {
                             * label = new Label(6, row, servCode);
                             * worksheet2.addCell(label);
                             * label = new Label(7, row,
                             * listValueVO.getLabel());
                             * worksheet2.addCell(label);
                             * }
                             * }
                             * label = new Label(8,row,cardGroupId);
                             * worksheet2.addCell(label);
                             * label = new Label(9,row,cardGroupName);
                             * worksheet2.addCell(label);
                             * row++;
                             * }
                             */
                            // ========================================================================================
                        } // end if
                    } // end for loop
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

    /**
     * @author sanjeew.kumar
     * @Method: writeGradeList
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return row
     * @throws Exception
     */
    private int writeGradeList(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        final String METHOD_NAME = "writeGradeList";
        if (_log.isDebugEnabled()) {
            _log.debug("writeGradeList", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        try {
            // Grade description
            String keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.graddescheading");
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            // Grade Code
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.gradecode");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);

            // Grade name
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.gtradename");
            // updated by akanksha for tigo CR

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
                    label = new Label(col, row, (listValueVO.getValue()).split(":")[0]);
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

    /**
     * @author sanjeew.kumar
     * @method: writeCategoryDetails
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return row
     * @throws Exception
     */
    private int writeCategoryDetails(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        final String METHOD_NAME = "writeCategoryDetails";
        if (_log.isDebugEnabled()) {
            _log.debug("writeCategoryDetails", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        try {
            // Category description
            String keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.categdescrheading");
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            // Category Code
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.categorycode");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);

            // Category name
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.categoryname");
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

    /**
     * @author sanjeew.kumar
     *         Method: writeGeographicalDomainCode
     * @param worksheet2
     * @param col
     * @param row
     * @param p_hashMap
     * @return row
     * @throws Exception
     */
    private int writeGeographicalDomainCode(WritableSheet worksheet2, int col, int row, HashMap p_hashMap) throws Exception {
        final String METHOD_NAME = "writeGeographicalDomainCode";
        if (_log.isDebugEnabled()) {
            _log.debug("writeGeographicalDomainCode", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        try {
            // Geographical domain description
            String keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.geogdomaindescrheading");
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            // Geographical domain code
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.geogdomaincode");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);

            // Geographical domain name
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.geogdomainname");
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

    /**
     * @author sanjeew.kumar
     *         Method: writeInDataSheet
     * @param worksheet1
     * @param col
     * @param row
     * @param p_hashMap
     * @param promotionLevel
     * @throws Exception
     */
    private void writeInDataSheet(WritableSheet worksheet1, int col, int row, HashMap p_hashMap, String promotionLevel, String p_dateRange) throws Exception {
        final String METHOD_NAME = "writeInDataSheet";
        if (_log.isDebugEnabled()) {
            _log.debug("writeInDataSheet", " p_hashMap size=" + p_hashMap.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        try {
            String keyName = null;
            // Promotional transfer rule data file
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.datasheet.heading");
            Label label = new Label(col, row, keyName, times12format);
            worksheet1.mergeCells(col, row, col + 10, row);
            worksheet1.addCell(label);
            row++;
            col = 0;
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.xlsfile.details.mandatory");
            label = new Label(++col, row, keyName);
            worksheet1.mergeCells(col, row, col + 5, row);
            worksheet1.addCell(label);
            row++;
            col = 0;
            if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_GRD)) {
                // Sender Grade
                keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.datasheet.sendergrade");
                keyName = keyName + "*";
                label = new Label(col++, row, keyName, times16format);
                worksheet1.addCell(label);
            }

            if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_CAT)) {
                // Sender Category code
                keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.datasheet.sendercategorycode");
                keyName = keyName + "*";
                label = new Label(col++, row, keyName, times16format);
                worksheet1.addCell(label);
            }
            if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_GRP)) {
                // Sender Geographical domain code
                keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.datasheet.sendergeogdomaincode");
                keyName = keyName + "*";
                label = new Label(col++, row, keyName, times16format);
                worksheet1.addCell(label);
            }
            if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_USR)) {
                // Sender Mobile No
                keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.datasheet.sendermobileno");
                label = new Label(col++, row, keyName, times16format);
                worksheet1.addCell(label);
            }
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CELL_GROUP_REQUIRED))).booleanValue() || ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_PROVIDER_PROMO_ALLOW))).booleanValue()) {
                if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_CEL)) {
                    keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.cellgroupcode");
                    keyName = keyName + "*";
                    label = new Label(col++, row, keyName, times16format);
                    worksheet1.addCell(label);
                }

                if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_SERVICE)) {
                    keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.serviceprovidergpid");
                    label = new Label(col++, row, keyName, times16format);
                    worksheet1.addCell(label);
                }
            }

            // Receiver subscriber type
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.datasheet.receiversubscribertype");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            // Receiver service class id
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.datasheet.receiverserviceclassid");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CELL_GROUP_REQUIRED))).booleanValue() || ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_PROVIDER_PROMO_ALLOW))).booleanValue()) {
                // Receiver subscriber status
                keyName = p_messages.getMessage(p_locale, "promotrfrule.addc2stransferrules.label.subscriberstatus");
                label = new Label(col++, row, keyName, times16format);
                worksheet1.addCell(label);
                // Receiver service provider group id
                if (!promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_SERVICE)) {
                    keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.serviceprovidergpid");
                    label = new Label(col++, row, keyName, times16format);
                    worksheet1.addCell(label);
                }
            }
            // Service type
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.datasheet.servicetype");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            // subservice code
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.datasheet.subservicecode");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            // Card group set id
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.datasheet.cardgroupsetid");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            // Applicabe from date
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.datasheet.applicablefromdate");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);
            if ("Y".equalsIgnoreCase(p_dateRange)) {
                // Applicable from time
                keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.datasheet.applicablefromtime");
                label = new Label(col++, row, keyName, times16format);
                worksheet1.addCell(label);

                // Applicabe till date
                keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.datasheet.applicabletilldate");
                label = new Label(col++, row, keyName, times16format);
                worksheet1.addCell(label);
            } else {
                // Applicabe till date
                keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.datasheet.applicabletilldate");
                label = new Label(col++, row, keyName, times16format);
                worksheet1.addCell(label);

                // Applicable from time
                keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.datasheet.applicablefromtime");
                label = new Label(col++, row, keyName, times16format);
                worksheet1.addCell(label);
            }

            // Applicable till time
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.datasheet.applicabletilltime");
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
            String keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.cellgroupdesc");
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            // Code
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.cellgroupcode");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);

            // name
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.cellgroupname");
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
                    label = new Label(col, row, (listValueVO.getValue()).split(":")[0]);
                    worksheet2.addCell(label);
                    label = new Label(col + 1, row, listValueVO.getLabel());
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
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.subscriberstatusheading");
            // Card group Description
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            // Subscriber type
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.subscribertype");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);

            // Subscriber type name
            keyName = p_messages.getMessage(p_locale, "promotrfrule.addc2stransferrules.label.subscriberstatus");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);

            row++;
            col = 0;
            ArrayList subscriberStatusList = (ArrayList) p_hashMap.get(PretupsI.TRANSFER_RULE_SUBSCRIBER_STATUS);
            TransferVO transferVO = null;
            // ==================================================================================================
            if (subscriberStatusList != null && subscriberStatusList.size() > 0) {
                for (int sk = 0, sl = subscriberStatusList.size(); sk < sl; sk++) {
                    // System.out.println("1");
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
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.serviceprovidergpheading");
            // Card group Description
            Label label = new Label(col, row, keyName, times16format);
            worksheet2.mergeCells(col, row, col + 2, row);
            worksheet2.addCell(label);
            row++;
            col = 0;

            // Subscriber type
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.serviceprovidergpname");
            label = new Label(col, row, keyName, times16format);
            worksheet2.addCell(label);

            // Subscriber type name
            keyName = p_messages.getMessage(p_locale, "promotionaltransferrule.mastersheet.serviceprovidergpid");
            label = new Label(++col, row, keyName, times16format);
            worksheet2.addCell(label);

            row++;
            col = 0;

            ArrayList serviceProviderGpList = (ArrayList) p_hashMap.get(PretupsI.SERVICE_GROUP_TYPE_ID);
            ServiceGpMgmtVO servicemgmtVO = null;
            // ==================================================================================================
            if (serviceProviderGpList != null && serviceProviderGpList.size() > 0) {
                for (int sk = 0, sl = serviceProviderGpList.size(); sk < sl; sk++) {
                    // System.out.println("1");
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
                    // strArr[0][indexMapArray[col]] = key;
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
}

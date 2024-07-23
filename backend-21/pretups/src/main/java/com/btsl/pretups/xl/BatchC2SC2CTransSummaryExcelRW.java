package com.btsl.pretups.xl;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import com.btsl.util.MessageResources;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.reports.businesslogic.BatchC2SC2CTransSummaryVO;
import com.btsl.pretups.common.PretupsI;

import jxl.SheetSettings;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class BatchC2SC2CTransSummaryExcelRW {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private  WritableFont times12font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, true);
    private WritableCellFormat times12format = new WritableCellFormat(times12font);
    private WritableFont times16font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, true);
    private WritableCellFormat times16format = new WritableCellFormat(times16font);
    private MessageResources p_messages = null;
    private Locale p_locale = null;

    /**
     * @param p_excelID
     * @param p_hashMap
     * @param messages
     * @param locale
     * @param p_fileName
     * @throws Exception
     */
    public void writeExcelForUserC2SC2CTrfData(String p_excelID, ArrayList<BatchC2SC2CTransSummaryVO> p_validDataList, MessageResources messages, Locale locale, String p_fileName, String p_transactionType) throws Exception {
        final String METHOD_NAME = "writeExcelForUserC2SC2CTrfData";
        if (_log.isDebugEnabled()) {
            _log.debug("writeExcelForUserC2SC2CTrfData", " p_excelID: " + p_excelID + " p_locale: " + locale + " p_fileName: " + p_fileName);
        }
        WritableWorkbook workbook = null;
        WritableSheet worksheet1 = null;
        // WritableSheet worksheet2 = null;
        int col = 0;
        int row = 0;
        double t_mem = 0;
        double f_mem = 0;

        try {
            t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            _log.info("writeExcelForUserC2SC2CTrfData", "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));
            workbook = Workbook.createWorkbook(new File(p_fileName));
            worksheet1 = workbook.createSheet("Template Sheet", 0);
            // worksheet2 = workbook.createSheet("Master Sheet", 1);
            p_messages = messages;
            p_locale = locale;

            // Label label = null;
            // String keyName =null;
            // keyName =
            // p_messages.getMessage(p_locale,"userdefaultconfiguration.mastersheet.heading",(String)p_hashMap.get(PretupsI.USR_DEF_CONFIG_DOMAIN_NAME));
            // label = new Label(col,row,keyName, times16format);
            // worksheet2.mergeCells(col,row,col+5,row);
            // worksheet2.addCell(label);
            // row++;
            // row++;
            // col=0;
            // row=this.writeCategoryData(worksheet2, col, row, p_hashMap);

            col = 0;
            row = 0;
            if (PretupsI.TRANSACTION_TYPE_C2C.equals(p_transactionType)) {
                this.writeInDataSheetForC2C(worksheet1, col, row, p_validDataList);
            } else {
                this.writeInDataSheetForC2S(worksheet1, col, row, p_validDataList);
            }

            workbook.write();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeExcelForUserC2SC2CTrfData", " Exception e: " + e.getMessage());
            throw new BTSLBaseException(e);
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            worksheet1 = null;
            // worksheet2=null;
            workbook = null;
            t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            // Runtime.getRuntime().gc();
            f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            _log.info("writeExcelForUserC2SC2CTrfData", "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));
            if (_log.isDebugEnabled()) {
                _log.debug("writeExcelForUserC2SC2CTrfData", " Exiting");
            }
        }
    }

    /**
     * @param worksheet1
     * @param col
     * @param row
     * @param p_hashMap
     * @throws Exception
     */
    private void writeInDataSheetForC2C(WritableSheet worksheet1, int col, int row, ArrayList<BatchC2SC2CTransSummaryVO> p_validDataList) throws Exception {
        final String METHOD_NAME = "writeInDataSheetForC2C";
        if (_log.isDebugEnabled()) {
            _log.debug("writeInDataSheetForC2C", " p_validDataList size=" + p_validDataList.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        try {
            String keyName = p_messages.getMessage(p_locale, "c2cTransSummaryRpt.xlsfile.detail.heading");
            // String comment = null;
            Label label = new Label(col, row, keyName, times12format);
            worksheet1.mergeCells(col, row, col + 10, row);
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "c2cTransSummaryRpt.xlsfile.detail.serialnumber");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "c2cTransSummaryRpt.xlsfile.detail.username");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "c2cTransSummaryRpt.xlsfile.detail.msisdn");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "c2cTransSummaryRpt.xlsfile.detail.geodomainname");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "c2cTransSummaryRpt.xlsfile.detail.category");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "c2cTransSummaryRpt.xlsfile.detail.productname");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "c2cTransSummaryRpt.xlsfile.detail.transfersubtype");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "c2cTransSummaryRpt.xlsfile.detail.intotalamount");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "c2cTransSummaryRpt.xlsfile.detail.intotaltransactions");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "c2cTransSummaryRpt.xlsfile.detail.outtotalamount");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "c2cTransSummaryRpt.xlsfile.detail.outtotaltransactions");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            // /setting for the vertical freeze panes
            SheetSettings sheetSetting = new SheetSettings();
            sheetSetting = worksheet1.getSettings();
            sheetSetting.setVerticalFreeze(row + 1);
            int count = p_validDataList.size();
            BatchC2SC2CTransSummaryVO batchC2SC2CTransSummaryVO = null;
            for (int i = 0; i < count; i++) {
                batchC2SC2CTransSummaryVO = p_validDataList.get(i);
                if (batchC2SC2CTransSummaryVO != null) {
                    row++;
                    col = 0;

                    label = new Label(col++, row, String.valueOf(i + 1));
                    worksheet1.addCell(label);

                    label = new Label(col++, row, batchC2SC2CTransSummaryVO.getUserName());
                    worksheet1.addCell(label);

                    label = new Label(col++, row, batchC2SC2CTransSummaryVO.getMsisdn());
                    worksheet1.addCell(label);

                    label = new Label(col++, row, batchC2SC2CTransSummaryVO.getGeographicalName());
                    worksheet1.addCell(label);

                    label = new Label(col++, row, batchC2SC2CTransSummaryVO.getCategoryName());
                    worksheet1.addCell(label);

                    label = new Label(col++, row, batchC2SC2CTransSummaryVO.getProductName());
                    worksheet1.addCell(label);

                    label = new Label(col++, row, batchC2SC2CTransSummaryVO.getTransferSubType());
                    worksheet1.addCell(label);

                    label = new Label(col++, row, String.valueOf(batchC2SC2CTransSummaryVO.getTransInAmount()));
                    worksheet1.addCell(label);

                    label = new Label(col++, row, String.valueOf(batchC2SC2CTransSummaryVO.getTransInCount()));
                    worksheet1.addCell(label);

                    label = new Label(col++, row, String.valueOf(batchC2SC2CTransSummaryVO.getTransOutAmount()));
                    worksheet1.addCell(label);

                    label = new Label(col++, row, String.valueOf(batchC2SC2CTransSummaryVO.getTransOutCount()));
                    worksheet1.addCell(label);

                }
            }
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeInDataSheetForC2C", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeInDataSheetForC2C", " Exception e: " + e.getMessage());
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
    private void writeInDataSheetForC2S(WritableSheet worksheet1, int col, int row, ArrayList<BatchC2SC2CTransSummaryVO> p_validDataList) throws Exception {
        final String METHOD_NAME = "writeInDataSheetForC2S";
        if (_log.isDebugEnabled()) {
            _log.debug("writeInDataSheetForC2S", " p_validDataList size=" + p_validDataList.size() + " p_locale: " + p_locale + " col=" + col + " row=" + row);
        }
        try {
            String keyName = p_messages.getMessage(p_locale, "c2sTransSummaryRpt.xlsfile.detail.heading");
            // String comment = null;
            Label label = new Label(col, row, keyName, times12format);
            worksheet1.mergeCells(col, row, col + 9, row);
            worksheet1.addCell(label);
            row++;
            col = 0;

            keyName = p_messages.getMessage(p_locale, "c2sTransSummaryRpt.xlsfile.detail.serialnumber");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "c2sTransSummaryRpt.xlsfile.detail.username");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "c2sTransSummaryRpt.xlsfile.detail.msisdn");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "c2sTransSummaryRpt.xlsfile.detail.geodomainname");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "c2sTransSummaryRpt.xlsfile.detail.category");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "c2sTransSummaryRpt.xlsfile.detail.servicetype");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "c2sTransSummaryRpt.xlsfile.detail.totaltransaction");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "c2sTransSummaryRpt.xlsfile.detail.totalfailtransactions");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "c2sTransSummaryRpt.xlsfile.detail.totalrechargecount");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            keyName = p_messages.getMessage(p_locale, "c2sTransSummaryRpt.xlsfile.detail.totalrechargeamount");
            label = new Label(col++, row, keyName, times16format);
            worksheet1.addCell(label);

            // /setting for the vertical freeze panes
            SheetSettings sheetSetting = new SheetSettings();
            sheetSetting = worksheet1.getSettings();
            sheetSetting.setVerticalFreeze(row + 1);
            int count = p_validDataList.size();
            BatchC2SC2CTransSummaryVO batchC2SC2CTransSummaryVO = null;
            for (int i = 0; i < count; i++) {
                batchC2SC2CTransSummaryVO = new BatchC2SC2CTransSummaryVO();
                batchC2SC2CTransSummaryVO = p_validDataList.get(i);
                if (batchC2SC2CTransSummaryVO != null) {
                    row++;
                    col = 0;

                    label = new Label(col++, row, String.valueOf(i + 1));
                    worksheet1.addCell(label);

                    label = new Label(col++, row, batchC2SC2CTransSummaryVO.getUserName());
                    worksheet1.addCell(label);

                    label = new Label(col++, row, batchC2SC2CTransSummaryVO.getMsisdn());
                    worksheet1.addCell(label);

                    label = new Label(col++, row, batchC2SC2CTransSummaryVO.getGeographicalName());
                    worksheet1.addCell(label);

                    label = new Label(col++, row, batchC2SC2CTransSummaryVO.getCategoryName());
                    worksheet1.addCell(label);

                    label = new Label(col++, row, batchC2SC2CTransSummaryVO.getServiceType());
                    worksheet1.addCell(label);

                    label = new Label(col++, row, String.valueOf(batchC2SC2CTransSummaryVO.getC2STotalTransactions()));
                    worksheet1.addCell(label);

                    label = new Label(col++, row, String.valueOf(batchC2SC2CTransSummaryVO.getC2STotalFailTransactions()));
                    worksheet1.addCell(label);

                    label = new Label(col++, row, String.valueOf(batchC2SC2CTransSummaryVO.getC2sRechargeCount()));
                    worksheet1.addCell(label);

                    label = new Label(col++, row, String.valueOf(batchC2SC2CTransSummaryVO.getC2sRechargeAmount()));
                    worksheet1.addCell(label);
                }
            }
        } catch (RowsExceededException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeInDataSheetForC2S", " Exception e: " + e.getMessage());
            throw e;
        } catch (WriteException e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("writeInDataSheetForC2S", " Exception e: " + e.getMessage());
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
    /*
     * public void writeExcelForUserC2STrfData(String
     * p_excelID,ArrayList<BatchC2SC2CTransSummaryVO> p_validDataList,
     * MessageResources messages,Locale locale,String p_fileName) throws
     * Exception
     * {
     * final String METHOD_NAME = "writeExcelForUserC2STrfData";
     * if(_log.isDebugEnabled())
     * _log.debug("writeExcelForUserC2CTrfData"," p_excelID: "+p_excelID+
     * " p_locale: "+locale+" p_fileName: "+p_fileName);
     * WritableWorkbook workbook = null;
     * WritableSheet worksheet1 = null;
     * //WritableSheet worksheet2 = null;
     * int col=0;
     * int row=0;
     * double t_mem=0;
     * double f_mem=0;
     * 
     * try
     * {
     * t_mem=Runtime.getRuntime().totalMemory()/1048576;
     * Runtime.getRuntime().gc();
     * f_mem=Runtime.getRuntime().freeMemory()/1048576;
     * _log.info("writeExcelForUserC2CTrfData","Total memory :"+t_mem+
     * "   free memmory :"+f_mem+" Used memory:"+(t_mem-f_mem));
     * workbook = Workbook.createWorkbook(new File(p_fileName));
     * worksheet1 = workbook.createSheet("Template Sheet", 0);
     * //worksheet2 = workbook.createSheet("Master Sheet", 1);
     * p_messages=messages;
     * p_locale=locale;
     * Label label = null;
     * String keyName =null;
     * 
     * //keyName =
     * p_messages.getMessage(p_locale,"userdefaultconfiguration.mastersheet.heading"
     * ,(String)p_hashMap.get(PretupsI.USR_DEF_CONFIG_DOMAIN_NAME));
     * //label = new Label(col,row,keyName, times16format);
     * //worksheet2.mergeCells(col,row,col+5,row);
     * //worksheet2.addCell(label);
     * //row++;
     * //row++;
     * //col=0;
     * //row=this.writeCategoryData(worksheet2, col, row, p_hashMap);
     * 
     * col=0;
     * row=0;
     * this.writeInDataSheetForC2S(worksheet1, col,row, p_validDataList);
     * 
     * workbook.write();
     * }
     * catch(Exception e)
     * {
     * _log.errorTrace(METHOD_NAME,e);
     * _log.error("writeExcelForUserC2CTrfData"," Exception e: "+e.getMessage());
     * throw e;
     * }
     * finally
     * {
     * try{if(workbook!=null)workbook.close();}catch(Exception e)
     * {
     * _log.errorTrace(METHOD_NAME,e);
     * }
     * worksheet1=null;
     * //worksheet2=null;
     * workbook=null;
     * t_mem=Runtime.getRuntime().totalMemory()/1048576;
     * Runtime.getRuntime().gc();
     * f_mem=Runtime.getRuntime().freeMemory()/1048576;
     * _log.info("writeExcelForUserC2CTrfData","Total memory :"+t_mem+
     * "   free memmory :"+f_mem+" Used memory:"+(t_mem-f_mem));
     * if(_log.isDebugEnabled())_log.debug("writeExcelForUserC2CTrfData"," Exiting"
     * );
     * }
     * }
     */
}

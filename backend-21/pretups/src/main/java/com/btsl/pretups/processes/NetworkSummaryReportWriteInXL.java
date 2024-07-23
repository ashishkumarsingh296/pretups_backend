package com.btsl.pretups.processes;
/**
 * @(#)NetworkSummaryReportWriteInXL.java 
 * Copyright(c) 2016, Mahindra Comviva. All
 * Rights Reserved
 *  -------------------------------------------------------------------------------------------------
 *   Author 					Date 			History
 *  -------------------------------------------------------------------------------------------------
 *   Vikas Chaudhary 		09/03/2016 		Initial Creation
 *  -------------------------------------------------------------------------------------------------
 * This class use for generate network summary reports in xls format.
 */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.TransactionSummaryVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.xl.ExcelStyle;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


/**
 * This class use for generate network summary reports in xls format.
 */

		public class NetworkSummaryReportWriteInXL {
			private static final  Log _log = LogFactory.getLog(NetworkSummaryReportWriteInXL.class.getName());
			private WritableCellFormat timesBold;
			private WritableCellFormat times;
			private static Locale _locale=null;
			
			public String write( String reportType,ArrayList<String> labelArray, ArrayList<TransactionSummaryVO> arrayContent) throws IOException, WriteException, Exception {
				 _locale = LocaleMasterCache.getLocaleFromCodeDetails("0");
				String fileName="NetworkSummaryReport.xls";
				String filePath=Constants.getProperty("DAILY_TECHNICAL_REPORT_FILE_PATH");
				String finalFileName=filePath+fileName;
				File file = new File(finalFileName);
				WorkbookSettings wbSettings = new WorkbookSettings();
				
				wbSettings.setLocale(new Locale("en", "EN"));
				
				WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
				String repHeader = BTSLUtil.getMessage(_locale,"nwreport.sheet.name",null);
				workbook.createSheet(repHeader, 0);
				WritableSheet excelSheet = workbook.getSheet(0);
				createLabel(excelSheet,labelArray);
				createContent(reportType,excelSheet,arrayContent);
				
				workbook.write();
				workbook.close();
				
				return finalFileName;
			}
	
			private void createLabel(WritableSheet sheet,ArrayList<String> labelArray) throws WriteException {
				final String methodName = "createLabel";
				for(int i=0;i<16;i++)
					sheet.setColumnView(i,20);
				// Lets create a times font
				WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
				// Define the cell format
				times = new WritableCellFormat(times10pt);
				// Lets automatically wrap the cells
				times.setWrap(true);
				
				// create create a bold font with unterlines
				WritableFont times10ptBold = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE);
				timesBold = new WritableCellFormat(times10ptBold);
				// Lets automatically wrap the cells
				timesBold.setWrap(true);
				
				CellView cView = new CellView();
				cView.setFormat(times);
				cView.setFormat(timesBold);
				int rowCount=0;
				int col=0;
				  _locale = LocaleMasterCache.getLocaleFromCodeDetails("0");
				  String repHeader = BTSLUtil.getMessage(_locale,"nwreport.sheet.name",null);					
				Label label;
				try {
					label = new Label(col,rowCount,repHeader, ExcelStyle.getTopHeadingFont());
				 
				sheet.mergeCells(col,rowCount,col+14,rowCount);
				sheet.addCell(label);
				
				}
				catch(BTSLBaseException be)
				{
					_log.errorTrace(methodName,be);
					_log.error(methodName," BTSLBaseException e: "+be.getMessage());
				}
								// Write a few headers
				int labelArraySizes=labelArray.size();
				for (int i =0; i < labelArraySizes;i++) {
					addLable(sheet, i, 1, labelArray.get(i));
				}
			}
			
	private void createContent(String reportType, WritableSheet sheet,ArrayList<TransactionSummaryVO> arrayContent) throws WriteException,
    RowsExceededException {
			if (_log.isDebugEnabled()) _log.debug("createContent1111111111111","Entered ",reportType);
		// Write a few number
	int arrayContentSizes=arrayContent.size();
		for (int i = 0; i < arrayContentSizes; i++) {
			int j=0;
			int k = i+2;
			String hourDateMonthToDisp = "";
			if(!"SUB TOTAL".equals(arrayContent.get(i).getType()) ) {
				if(i>0) {
					if (!arrayContent.get(i).getTimeDateMonth().equalsIgnoreCase(arrayContent.get(i-1).getTimeDateMonth())) {
						hourDateMonthToDisp = getHourFromDataToDisplay(reportType, arrayContent.get(i).getTimeDateMonth(), arrayContent.get(i).getTransYear());
					}
				}
				else {
					hourDateMonthToDisp = getHourFromDataToDisplay(reportType, arrayContent.get(i).getTimeDateMonth(), arrayContent.get(i).getTransYear());
				}
				addContent(sheet, j++ , k, hourDateMonthToDisp);
				addContent(sheet, j++ , k, arrayContent.get(i).getNetworkName());
				addContent(sheet, j++ , k, arrayContent.get(i).getGatewayCode());
				addContent(sheet, j++ , k, arrayContent.get(i).getCategory());
				addContent(sheet, j++ , k, arrayContent.get(i).getType());
				addContent(sheet, j++ , k, arrayContent.get(i).getSubService());
				addContent(sheet, j++ , k, arrayContent.get(i).getInterfaceId());
				addContent(sheet, j++ , k, Long.toString(arrayContent.get(i).getSuccesfulRecharges()));
				addContent(sheet, j++ , k, Double.toString(arrayContent.get(i).getRechargesDenoms()));
				addContent(sheet, j++ , k, Double.toString(arrayContent.get(i).getServiceTax()));
				addContent(sheet, j++ , k, Double.toString(arrayContent.get(i).getAccessFee()));
				addContent(sheet, j++ , k, Double.toString(arrayContent.get(i).getTalkTimeAmt()));
				addContent(sheet, j++ , k, Double.toString(arrayContent.get(i).getTotalRechargeAmt()));
				addContent(sheet, j++ , k, Long.toString(arrayContent.get(i).getFailCount()));
				addContent(sheet, j++ , k, Double.toString(arrayContent.get(i).getFailAmt()));
			}else{			
			addContent(sheet, j++ , k, hourDateMonthToDisp);
			addContent(sheet, j++ , k, arrayContent.get(i).getNetworkName());
			addContent(sheet, j++ , k, arrayContent.get(i).getGatewayCode());
			addContent(sheet, j++ , k, arrayContent.get(i).getCategory());
			addBoldContent(sheet, j++ , k, arrayContent.get(i).getType());
			addContent(sheet, j++ , k, arrayContent.get(i).getSubService());
			addContent(sheet, j++ , k, arrayContent.get(i).getInterfaceId());
			
			addBoldContent(sheet, j++ , k, Long.toString(arrayContent.get(i).getSuccesfulRecharges()));
			addBoldContent(sheet, j++ , k, BTSLUtil.roundToStr(arrayContent.get(i).getRechargesDenoms(),2));
			addBoldContent(sheet, j++ , k, BTSLUtil.roundToStr(arrayContent.get(i).getServiceTax(),2));
			addBoldContent(sheet, j++ , k, BTSLUtil.roundToStr(arrayContent.get(i).getAccessFee(),2));
			addBoldContent(sheet, j++ , k, BTSLUtil.roundToStr(arrayContent.get(i).getTalkTimeAmt(),2));
			addBoldContent(sheet, j++ , k, BTSLUtil.roundToStr(arrayContent.get(i).getTotalRechargeAmt(),2));
			addBoldContent(sheet, j++ , k, Long.toString(arrayContent.get(i).getFailCount()));
			addBoldContent(sheet, j++ , k, BTSLUtil.roundToStr(arrayContent.get(i).getFailAmt(),2));
			}
			
		}
	}
 	
	private String getHourFromDataToDisplay(String reportType, String timeDateMonth, int transYear) {
		if (_log.isDebugEnabled()) _log.debug("getHourFromDataToDisplay","Entered ",reportType);//timeDateMonth,transYear);
	if (_log.isDebugEnabled()) _log.debug("getHourFromDataToDisplay",timeDateMonth,transYear);	
		String returnTimeDateMonth =null;
		String monthArray[] = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
	
		int tmp =0;
		if (PretupsI.HOURLY_FILTER.equals(reportType)) {
			tmp=Integer.valueOf(timeDateMonth);
			tmp=tmp-1;
			returnTimeDateMonth = tmp+"-"+timeDateMonth;
		}
		else if(PretupsI.DAILY_FILTER.equals(reportType)) {
			returnTimeDateMonth = timeDateMonth;
		}
		else if(PretupsI.MONTHLY_FILTER.equals(reportType)) {
			tmp=Integer.valueOf(timeDateMonth);
			tmp=tmp-1;
			returnTimeDateMonth = monthArray[tmp]+"-"+transYear;
		}
		
		return returnTimeDateMonth;
	}

	private void addLable(WritableSheet sheet, int column, int row, String s) throws RowsExceededException, WriteException {
		final String methodName = "addLable";
		Label label;
		try {
			label = new Label(column, row, s, ExcelStyle.getHeadingFont());
			sheet.addCell(label);
		} catch(BTSLBaseException be)
		{
			_log.errorTrace(methodName,be);
			_log.error(methodName," BTSLBaseException e: "+be.getMessage());
			}
		catch(Exception e)
		{
			_log.errorTrace(methodName,e);
			_log.error(methodName," Exception e: "+e.getMessage());
			}
		
	}
	
	
	  private void addContent(WritableSheet sheet, int column, int row, String s) throws RowsExceededException, WriteException {
		  	Label label;
		  	label = new Label(column, row, s, times);
		  	sheet.addCell(label);
  }
	  private void addBoldContent(WritableSheet sheet, int column, int row, String s) throws RowsExceededException, WriteException {
		  	Label label;
		  	label = new Label(column, row, s, timesBold);
		  	sheet.addCell(label);
}
}


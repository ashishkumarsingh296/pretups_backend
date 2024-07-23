package com.btsl.pretups.xl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import com.btsl.util.MessageResources;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
import com.btsl.pretups.scheduletopup.process.BatchFileParserI;
import com.btsl.util.BTSLUtil;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class DvdBatchDistExcel {
	private MessageResources p_messages = null;
	protected final Log _log = LogFactory.getLog(getClass().getName());
	
	public void writeInXLSX(String pFileName, Locale p_locale, HashMap p_hashMap, MessageResources messages) throws IOException, RowsExceededException, WriteException, BTSLBaseException{
		final String methodName = "writeExcel";
		if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered" );
        }
		SXSSFWorkbook workbook = null;
		SXSSFSheet worksheet1 = null;
		byte[] excelFilebytes = null;
        //WritableSheet worksheet1 = null;
        int rowNum = 0;
        FileOutputStream outputStream = new FileOutputStream(new File(pFileName)); 
        int headerCell = 0;
        try 
        {
        	p_messages = messages;
        	String keyName ;
        	String message = "";
        	CellStyle headerStyle = null;
            workbook = new SXSSFWorkbook();
            worksheet1 = workbook.createSheet("Template Sheet 1");
            //SXSSFCellStyle headerStyle = workbook.createCellStyle(); 
            SXSSFRow row = worksheet1.createRow(rowNum++);
			//SXSSFFont font = workbook.createFont();
            headerStyle = workbook.createCellStyle();
            Font times16font = workbook.createFont();
            //times16font.setFontHeightInPoints((short) 14);
            times16font.setFontHeightInPoints(BTSLUtil.parseIntToShort(14));
        	times16font.setBold(true);
        	headerStyle.setFont(times16font);
			message = "voms.dvd.heading.batch";
			keyName = p_messages.getMessage(p_locale,message);
			SXSSFCell cell =  row.createCell(BTSLUtil.parseIntToShort(headerCell++));
			cell.setCellValue(keyName);
			cell.setCellStyle(headerStyle);
			//worksheet1.autoSizeColumn(headerCell-1);
			
			
			headerCell = 0;
			row = worksheet1.createRow(rowNum++);
			message = "dvd.scheduletopupdetails.file.label.service";
			keyName = p_messages.getMessage(p_locale,message);
			cell =  row.createCell(BTSLUtil.parseIntToShort(headerCell++));
			cell.setCellValue(keyName);
			cell.setCellStyle(headerStyle);
			//worksheet1.autoSizeColumn(headerCell-1);
			
			
			message = "voms.dvd.heading.batch";
			keyName = p_messages.getMessage(p_locale,message);
			cell =  row.createCell(BTSLUtil.parseIntToShort(headerCell++));
			cell.setCellValue(keyName);
			cell.setCellStyle(headerStyle);
			//worksheet1.autoSizeColumn(headerCell-1);
			
			headerCell = 0;
			row = worksheet1.createRow(rowNum++);
			message = "dvd.scheduletopupdetails.file.label.msisdn";
			keyName = p_messages.getMessage(p_locale,message);
			cell =  row.createCell(BTSLUtil.parseIntToShort(headerCell++));
			cell.setCellValue(keyName);
			cell.setCellStyle(headerStyle);
			//worksheet1.autoSizeColumn(headerCell-1);
			
			message = "dvd.scheduletopupdetails.file.label.voucherType";
			keyName = p_messages.getMessage(p_locale,message);
			cell =  row.createCell(BTSLUtil.parseIntToShort(headerCell++));
			cell.setCellValue(keyName);
			cell.setCellStyle(headerStyle);
			//worksheet1.autoSizeColumn(headerCell-1);
			
			message = "dvd.scheduletopupdetails.file.label.voucherSegment";
			keyName = p_messages.getMessage(p_locale,message);
			cell =  row.createCell(BTSLUtil.parseIntToShort(headerCell++));
			cell.setCellValue(keyName);
			cell.setCellStyle(headerStyle);
			//worksheet1.autoSizeColumn(headerCell-1);
			
			message = "dvd.scheduletopupdetails.file.label.denomination";
			keyName = p_messages.getMessage(p_locale,message);
			cell =  row.createCell(BTSLUtil.parseIntToShort(headerCell++));
			cell.setCellValue(keyName);
			cell.setCellStyle(headerStyle);
			//worksheet1.autoSizeColumn(headerCell-1);
			
			message = "dvd.scheduletopupdetails.file.label.ptofileid";
			keyName = p_messages.getMessage(p_locale,message);
			cell =  row.createCell(BTSLUtil.parseIntToShort(headerCell++));
			cell.setCellValue(keyName);
			cell.setCellStyle(headerStyle);
			//worksheet1.autoSizeColumn(headerCell-1);
			
			message = "dvd.scheduletopupdetails.file.label.numberOfVouchers";
			keyName = p_messages.getMessage(p_locale,message);
			cell =  row.createCell(BTSLUtil.parseIntToShort(headerCell++));
			cell.setCellValue(keyName);
			cell.setCellStyle(headerStyle);
			//worksheet1.autoSizeColumn(headerCell-1);
			workbook.write(outputStream);
			outputStream.close();
	        workbook.dispose();
            
        }
        catch (Exception e){
        	throw new BTSLBaseException(e.getMessage());
        }
        finally{
        	
        	if(workbook !=null){
        		workbook.close();
        	}
        	if(outputStream!= null)
        	{
        		outputStream.close();
        	}
        	worksheet1=null;
        	workbook=null;
        }
        
	}
	
	
	public void writeInXLS(String pFileName, Locale p_locale, HashMap p_hashMap, MessageResources messages) throws IOException, RowsExceededException, WriteException{
		final String methodName = "writeExcel";
		if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered" );
        }
		WritableWorkbook workbook = null;
        WritableSheet worksheet1 = null;
    
        int count = 0;
        int col = 0;
        int row = 0;
        try 
        {
        	p_messages = messages;
        	String message = "";
            workbook = Workbook.createWorkbook(new File(pFileName));
            worksheet1 = workbook.createSheet("Template Sheet 1",count++);
           
            message = "voms.dvd.heading.batch";
            this.createHeadingsinExcel(worksheet1, col, row, p_hashMap,p_locale,message);
            row++;
            message = "dvd.scheduletopupdetails.file.label.service";
            this.createHeadingsinExcel(worksheet1, col, row, p_hashMap,p_locale,message);
            col++; 
            message = "voms.dvd.heading.batch";
            this.createHeadingsinExcel(worksheet1, col, row, p_hashMap,p_locale,message);
            col=0;
            row++;
            message = "dvd.scheduletopupdetails.file.label.msisdn";
            this.createHeadingsinExcel(worksheet1, col, row, p_hashMap,p_locale,message);
            col++; 
            message = "dvd.scheduletopupdetails.file.label.voucherType";
            this.createHeadingsinExcel(worksheet1, col, row, p_hashMap,p_locale,message);
            col++; 
            message = "dvd.scheduletopupdetails.file.label.voucherSegment";
            this.createHeadingsinExcel(worksheet1, col, row, p_hashMap,p_locale,message);
            col++; 
            message = "dvd.scheduletopupdetails.file.label.denomination";
            this.createHeadingsinExcel(worksheet1, col, row, p_hashMap,p_locale,message);
            col++; 
            message = "dvd.scheduletopupdetails.file.label.ptofileid";
            this.createHeadingsinExcel(worksheet1, col, row, p_hashMap,p_locale,message);
            col++; 
            message = "dvd.scheduletopupdetails.file.label.numberOfVouchers";
            this.createHeadingsinExcel(worksheet1, col, row, p_hashMap,p_locale,message);
           
            workbook.write();
        }
        finally{
        	if(workbook !=null){
        		workbook.close();
        	}
        	worksheet1=null;
        	
        	workbook=null;
        }
        
	}
	
	
	
   public void fillDatainMasterSheet(String pFileName, Locale p_locale, HashMap p_hashMap, MessageResources messages) throws RowsExceededException, WriteException, IOException{
	   
	   final String methodName = "fillDatainMasterSheet";
		if (_log.isDebugEnabled()) {
           _log.debug(methodName, "Entered" );
       }
		WritableWorkbook workbook = null;
        WritableSheet worksheet1 = null;
       
        int count =0;
        int col = 0;
        int row = 0;
        try 
        {
        	p_messages = messages;
        	String message = "";
            workbook = Workbook.createWorkbook(new File(pFileName));
            worksheet1 = workbook.createSheet("Template Sheet 1",count++);
            col=0;
            row=0;
            message = "cardgroup.cardgroupc2sdetails.label.voucher.details";
            this.createHeadingsinExcel(worksheet1, col, row, p_hashMap,p_locale,message);
            row++;
            message = "cardgroup.viewtransferrule.label.vouchertype";
            this.createHeadingsinExcel(worksheet1, col, row, p_hashMap,p_locale,message);
            col++; 
            message = "dvd.scheduletopupdetails.file.label.voucherType";
            this.createHeadingsinExcel(worksheet1, col, row, p_hashMap,p_locale,message);
            col++; 
            message = "vomsreport.voucherreconcile.vouchersegment";
            this.createHeadingsinExcel(worksheet1, col, row, p_hashMap,p_locale,message);
            col++; 
            message = "dvd.scheduletopupdetails.file.label.voucherSegment";
            this.createHeadingsinExcel(worksheet1, col, row, p_hashMap,p_locale,message);
            col++; 
            message = "dvd.scheduletopupdetails.file.label.denomination";
            this.createHeadingsinExcel(worksheet1, col, row, p_hashMap,p_locale,message);
            col++; 
            message = "voms.burn.rate.profile";
            this.createHeadingsinExcel(worksheet1, col, row, p_hashMap,p_locale,message);
            col++; 
            message = "dvd.scheduletopupdetails.file.label.ptofileid";
            this.createHeadingsinExcel(worksheet1, col, row, p_hashMap,p_locale,message);
            col++; 
            message = "dvd.scheduletopupdetails.file.label.availableQty";
            this.createHeadingsinExcel(worksheet1, col, row, p_hashMap,p_locale,message);
            col=0;
            row++;
            
         
          
	   ArrayList <CardGroupDetailsVO> cardGroupList = (ArrayList<CardGroupDetailsVO>) p_hashMap.get(BatchFileParserI.VOUCHER_LIST);
	   
	     if(cardGroupList != null && !cardGroupList.isEmpty()){
	    	 for(CardGroupDetailsVO CardGroupDetailsVO: cardGroupList){
	    		 Label label = new Label(col++, row, CardGroupDetailsVO.getVoucherType());
	    		 worksheet1.addCell(label);
	    		 label = new Label(col++, row, CardGroupDetailsVO.getVoucherTypeDesc());
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
	             label = new Label(col++, row, CardGroupDetailsVO.getAvailableVouchers());
	             worksheet1.addCell(label);
	             row++;
	             col = 0;
	    	 }
	     }
	     workbook.write();
   }
        finally{
        	if(workbook !=null){
        		workbook.close();
        	}
        	worksheet1 = null;
        }
   }
   private void createHeadingsinExcel(WritableSheet worksheet1, int col, int row, HashMap p_hashMap,Locale p_locale, String message) throws RowsExceededException, WriteException{
		final String methodName = "createHeadingsinExcel";
		if (_log.isDebugEnabled()) {
           _log.debug(methodName, "Entered" );
       }
		String keyName = "";
		WritableFont times16font = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, true);
		WritableCellFormat times16format = new WritableCellFormat(times16font);
		if(p_messages!= null)
		{
			keyName = p_messages.getMessage(p_locale,message);
		}
		else
		{
			keyName = BTSLUtil.getMessage(p_locale, message, null);
		}
		
		Label label = new Label(col, row, keyName, times16format);
       worksheet1.addCell(label);
       
	}
	
}

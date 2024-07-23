package com.btsl.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Locale;

import com.btsl.pretups.gateway.util.*;
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

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.query.businesslogic.C2sBalanceQueryVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.ProductTypeDAO;
import com.btsl.util.BTSLUtil;
import com.btsl.xl.ExcelFileConstants;

/**
 * 
 * @author anshul.goyal2
 *
 */
public class FileWriteUtil {
	 StringBuilder loggerValue= new StringBuilder(); 


	public static final Log log = LogFactory.getLog(FileWriteUtil.class.getName());	
	public static ArrayList p_bonusTypeList;
	public static void writeinXLSX(String p_excelID, String[][] p_strArr, String p_fileName,String noOfRowsInOneTemplate, int headingRowsCount) throws IOException, RowsExceededException, WriteException{

	    if (log.isDebugEnabled()) {
	        log.debug("writeinXLSX", " p_excelID: " + p_excelID + " p_strArr:" + p_strArr + " p_strArr length: " + p_strArr.length + " p_fileName: " + p_fileName);
	    }
	    final String METHOD_NAME = "writeinXLSX";
	    SXSSFWorkbook workbook = null;
	    SXSSFSheet worksheet = null;
	    String fileName = p_fileName;
	    FileOutputStream outputStream = new FileOutputStream(new File(fileName));
	    int totalNoOfRecords = 0, noOfTotalSheet = 0;
	    try {
	    	 totalNoOfRecords = p_strArr.length - headingRowsCount;
	         int noOfRowsPerTemplate = 0;
	         if (totalNoOfRecords > 0) {
	             if (!BTSLUtil.isNullString(noOfRowsInOneTemplate)) {
	                 noOfRowsInOneTemplate = noOfRowsInOneTemplate.trim();
	                 noOfRowsPerTemplate = Integer.parseInt(noOfRowsInOneTemplate);
	             } else {
	                 noOfRowsPerTemplate = 65500; // Default value of rows
	             }
	             // cast issue fix
	             // noOfTotalSheet = (int) Math.ceil((double) totalNoOfRecords / noOfRowsPerTemplate);
	             noOfTotalSheet = totalNoOfRecords / noOfRowsPerTemplate  + (totalNoOfRecords % noOfRowsPerTemplate == 0 ? 0 : 1);
	         } else {
	             noOfTotalSheet = 1;
	         }
	        workbook = new SXSSFWorkbook();
	        String key = null;
	        int rowNum = 0;
	        CellStyle headerStyle = null;
	        for (int i = 0; i < noOfTotalSheet; i++) {
	            worksheet = workbook.createSheet("Sheet"+(i+1));
	            headerStyle = workbook.createCellStyle();
	            Font times16font = workbook.createFont();
	            
	            final short fontHeight = 14;
	            times16font.setFontHeightInPoints(fontHeight);
	        	times16font.setBold(true);
	        	headerStyle.setFont(times16font);
	        	for(int hr = 0; hr < headingRowsCount; hr++)
	        	{
	        		SXSSFRow row = worksheet.createRow(rowNum++);
	            	for (int col = 0, len = p_strArr[hr].length; col < len; col++) {
	            		key = p_strArr[hr][col];
	            		// SXSSFCell cell =  row.createCell((short) col);
	            		SXSSFCell cell =  row.createCell(Short.parseShort(String.valueOf(col)));
	            		cell.setCellValue(key);
	            		cell.setCellStyle(headerStyle);
	            	}
	            }
	            
	            
	            if (noOfTotalSheet > 1) {
	            for (int ro = 1; ro <= noOfRowsPerTemplate; ro++) {
	               int recordNo = ro + (i * noOfRowsPerTemplate);
	                if (recordNo <= totalNoOfRecords) {
	                    int len1 = p_strArr[recordNo].length;
	                    for (int colm = 0; colm < len1; colm++) {
	                    	 SXSSFRow row1 = worksheet.createRow(recordNo+1);
	                        // SXSSFCell cell =  row1.createCell((short) colm);
	                        SXSSFCell cell =  row1.createCell(Short.parseShort(String.valueOf(colm)));
	                        key =  p_strArr[recordNo][colm];
	                        if(!BTSLUtil.isNullString(key)) {
	                        	 cell.setCellValue(key);
	                        }
	                    }
	                } else {
	                    break;
	                }
	            }
	        } else {
	            for (int ro = headingRowsCount; ro < totalNoOfRecords + headingRowsCount; ro++) {
	            	 SXSSFRow row1 = worksheet.createRow(ro);
	                int len1 = p_strArr[ro].length;
	                for (int colm = 0; colm < len1; colm++) {
	                	 key =  p_strArr[ro][colm];
	                	
	                     // SXSSFCell cell =  row1.createCell((short) colm);
	                     SXSSFCell cell =  row1.createCell(Short.parseShort(String.valueOf(colm)));
	                     if(!BTSLUtil.isNullString(key)) {
	                     	 cell.setCellValue(key);
	                     }
	                }
	            }
	        }
	    }
	        workbook.write(outputStream);
	        outputStream.close();
	        workbook.dispose();
	    } catch (IOException  e) {
	        log.errorTrace(METHOD_NAME, e);
	        log.error("writeinXLSX", " Exception e: " + e.getMessage());
	        throw e;
	    } finally {
	        try {
	            if (workbook != null) {
	                workbook.close();
	            }
	            if(outputStream!= null)
	        	{
	        		outputStream.close();
	        	}
	        	worksheet=null;
	        	workbook=null;
	        } catch (IOException  e) {
	            log.errorTrace(METHOD_NAME, e);
	        }
	        worksheet = null;
	        workbook = null;
	        double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
	        double f_mem = Runtime.getRuntime().freeMemory() / 1048576;
	        log.debug("writeinXLSX", "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));
	        if (log.isDebugEnabled()) {
	            log.debug("writeinXLSX", " Exiting");
	        }
	    }

	}

	/**
	 * Method for Writing in xls file
	 * @param p_excelID
	 * @param p_strArr
	 * @param p_locale
	 * @param p_fileName
	 * @throws IOException
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	public static void writeinXLS(String p_excelID, String[][] p_strArr, String p_fileName, String noOfRowsInOneTemplate, int headingRowsCount) throws IOException, RowsExceededException, WriteException{
	   
	  final String METHOD_NAME = "writeinXLS";
	    if (log.isDebugEnabled()) {
	        log.debug("writeinXLS", " p_excelID: " + p_excelID + " p_strArr:" + p_strArr + " p_strArr length: " + p_strArr.length + " p_fileName: " + p_fileName);
	    }
	    WritableWorkbook workbook = null;
	    WritableSheet worksheet = null;
	    int totalNoOfRecords = 0, noOfTotalSheet = 0;
	    Locale locale = new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
	    
	    try {
	     

	        totalNoOfRecords = p_strArr.length - headingRowsCount;
	        

	        int noOfRowsPerTemplate = 0;
	        if (totalNoOfRecords > 0) {
	            if (!BTSLUtil.isNullString(noOfRowsInOneTemplate)) {
	                noOfRowsInOneTemplate = noOfRowsInOneTemplate.trim();
	                noOfRowsPerTemplate = Integer.parseInt(noOfRowsInOneTemplate);
	            } else {
	                noOfRowsPerTemplate = 65500; // Default value of rows
	            }
	            // cast issue fix
	            // noOfTotalSheet = (int) Math.ceil((double) totalNoOfRecords / noOfRowsPerTemplate);
	            noOfTotalSheet =  totalNoOfRecords / noOfRowsPerTemplate  + (totalNoOfRecords % noOfRowsPerTemplate == 0 ? 0 : 1);
	        } else {
	            noOfTotalSheet = 1;
	        }
	        String fileName = p_fileName;
	        workbook = Workbook.createWorkbook(new File(fileName));
	        String key = null;
	        String keyName = null;
	        Label label = null;
	        // int len=p_strArr[0].length;
	        int recordNo = 0;
	        for (int i = 0; i < noOfTotalSheet; i++) {
	            worksheet = workbook.createSheet("Sheet" + (i + 1), i);
	            WritableFont times16font = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD, true);
	            WritableCellFormat times16format = new WritableCellFormat(times16font);
	            int[] indexMapArray = new int[p_strArr[0].length];
	            String indexStr = null;
	            for(int hr = 0; hr < headingRowsCount; hr++)
	            	for (int col = 0; col < p_strArr[hr].length; col++) {
	                indexStr = null;
	                key = p_strArr[hr][col];
	                keyName = key;
	                indexStr = ExcelFileConstants.getWriteProperty(p_excelID, String.valueOf(col));
	                if (indexStr == null) {
	                    indexStr = String.valueOf(col);
	                }
	                indexMapArray[col] = Integer.parseInt(indexStr);
	                label = new Label(indexMapArray[col], hr, keyName, times16format);
	                if(BTSLUtil.getMessage(locale,"batchdirectpayout.xlsheading.label.bonustype").equals(keyName))
					{
							ListValueVO listVO = new ListValueVO();
							StringBuffer commentBuff = new StringBuffer();
							String comment = null;
							

							int bonusTypeListSize = p_bonusTypeList.size();
							if (p_bonusTypeList != null) {
								for (int ij = 0; ij < bonusTypeListSize; ij++) {
									listVO = (ListValueVO) p_bonusTypeList.get(ij);
									commentBuff.append(listVO.getValue().substring(0, listVO.getValue().indexOf(":"))
											+ " : " + listVO.getLabel() + "\n");
								}
								comment = commentBuff.toString();
							}

							if (BTSLUtil.isNullString(comment)) {

				                commentBuff = new StringBuffer();
				                String message = BTSLUtil.getMessage(locale, "batchdirectpayout.processuploadedfile.error.bonustype");
				                commentBuff.append(message);
				                comment = commentBuff.toString();
							}
		                    WritableCellFeatures cellFeatures = new WritableCellFeatures();
		                    if (p_bonusTypeList != null) {
		                        if (p_bonusTypeList.size() > 0) {
		                            cellFeatures.setComment(comment, getLengthLongestStringLength(p_bonusTypeList), p_bonusTypeList.size() + 1);
		                        } else {
		                            cellFeatures.setComment(comment);
		                        }

		                    } else {
		                        cellFeatures.setComment(comment);
		                    }
		                    label.setCellFeatures(cellFeatures);
						}
	                
	                worksheet.addCell(label);
	            }
	            

	            if (noOfTotalSheet > 1) {
	                for (int row = headingRowsCount; row <= noOfRowsPerTemplate; row++) {
	                    recordNo = row + (i * noOfRowsPerTemplate);
	                    if (recordNo <= totalNoOfRecords) {
	                        int len1 = p_strArr[recordNo].length;
	                        for (int col = 0; col < len1; col++) {
	                            label = new Label(indexMapArray[col], row, p_strArr[recordNo][col]);
	                            worksheet.addCell(label);
	                        }
	                    } else {
	                        break;
	                    }
	                }
	            } else {
	                for (int row = headingRowsCount; row < totalNoOfRecords + headingRowsCount; row++) {
	                    int len1 = p_strArr[row].length;
	                    for (int col = 0; col < len1; col++) {
	                        label = new Label(indexMapArray[col], row, p_strArr[row][col]);
	                        worksheet.addCell(label);
	                    }
	                }
	            }
	        }
	        workbook.write();
	    } catch (IOException |  WriteException  e) {
	        log.errorTrace(METHOD_NAME, e);
	        log.error("writeExcel", " Exception e: " + e.getMessage());
	        throw e;
	    } finally {
	        try {
	            if (workbook != null) {
	                workbook.close();
	            }
	        } catch (IOException |  WriteException  e) {
	            log.errorTrace(METHOD_NAME, e);
	        }
	        worksheet = null;
	        workbook = null;
	        double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
	        // Runtime.getRuntime().gc();
	        double f_mem = Runtime.getRuntime().freeMemory() / 1048576;
	        log.debug("writeExcel", "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));
	        if (log.isDebugEnabled()) {
	            log.debug("writeExcel", " Exiting");
	        }
	    }
	}

	/**
	 * Method for writing in csv file
	 * @param p_excelID
	 * @param p_strArr
	 * @param p_locale
	 * @param p_fileName
	 * @throws IOException
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	public static void writeinCSV(String p_excelID, String[][] p_strArr,  String p_fileName) throws IOException, RowsExceededException, WriteException{

	    if (log.isDebugEnabled()) {
	        log.debug("writeinCSV", " p_excelID: " + p_excelID + " p_strArr:" + p_strArr + " p_strArr length: " + p_strArr.length + " p_fileName: " + p_fileName);
	    }
	    final String METHOD_NAME = "writeinCSV";
	    Writer fileWriter = null;
	    try {
	    	fileWriter = new BufferedWriter(new FileWriter(p_fileName,true));
	        String key = null;
	       
	      
	            /*for (int col = 0, len = p_strArr[0].length; col < len; col++) {
	               
	                key = p_strArr[0][col];
	              
	                fileWriter.write(key + ",");
	                
	            }
	            fileWriter.write( "\n");
	            */
	            int totalNoOfRecords = p_strArr.length - 1;
	            for (int row = 0; row <= totalNoOfRecords; row++) {
	                int len1 = p_strArr[row].length;
	                for (int col = 0; col < len1; col++) {
	                	key = p_strArr[row][col];
	                	if(!BTSLUtil.isNullString(key))
	                		 fileWriter.write(key + ",");
	                	else
	                		fileWriter.write(",");
	                }
	                fileWriter.write( "\n");
	            }
	        
	    } catch (IOException e) {
	        log.errorTrace(METHOD_NAME, e);
	        log.error("writeinCSV", " Exception e: " + e.getMessage());
	        throw e;
	    } finally {
	    	 try {
	             if (fileWriter != null) {
	                 fileWriter.close();
	             }
	         } catch (Exception e) {
	             log.errorTrace(METHOD_NAME, e);
	         }
	        double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
	        double f_mem = Runtime.getRuntime().freeMemory() / 1048576;
	        log.debug("writeinCSV", "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));
	        if (log.isDebugEnabled()) {
	            log.debug("writeinCSV", " Exiting");
	        }
	    }
	    }

	/**
	 * This method writes template in csv format
	 * @param p_excelID
	 * @param p_strArr
	 * @param p_fileName
	 * @throws IOException
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	public static void writeinCSVTemplate(String p_excelID, String[][][][] p_strArr, String p_fileName) throws IOException, RowsExceededException, WriteException{
		final String METHOD_NAME = "writeinCSVTemplate";
		if (log.isDebugEnabled()) {
	        log.debug(METHOD_NAME, " p_excelID: " + p_excelID + " p_strArr:" + p_strArr + " p_strArr length: " + p_strArr.length + " p_fileName: " + p_fileName);
	    }
	    Locale locale = new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
	    Writer fileWriter = null;
	    try {
	    	fileWriter = new BufferedWriter(new FileWriter(p_fileName));
	        String key = null;
	        String keyName = null;
	        for(int row1=0;row1<p_strArr.length;row1++){
	        	for (int col = 0; col <  p_strArr[row1].length; col++) {
	        		key = p_strArr[row1][col][0][0];
	        		if(!BTSLUtil.isNullString(key)){
	        			
	        		keyName = null;
	        		String param[] = key.split(":");
	        		keyName = BTSLUtil.getMessage(locale, param[0], null);
	        		fileWriter.write(keyName + (param.length > 1 ? param[1] : ""));
	        		}
	        		fileWriter.write(",");
	        	}
	        	fileWriter.write("\n");
	        }
	    } catch (IOException e) {
	        log.errorTrace(METHOD_NAME, e);
	        log.error(METHOD_NAME, " Exception e: " + e.getMessage());
	        throw e;
	    } finally {
	    	 try {
	             if (fileWriter != null) {
	                 fileWriter.close();
	             }
	         } catch (Exception e) {
	             log.errorTrace(METHOD_NAME, e);
	         }
	        double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
	        double f_mem = Runtime.getRuntime().freeMemory() / 1048576;
	        log.debug(METHOD_NAME, "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));
	        if (log.isDebugEnabled()) {
	            log.debug(METHOD_NAME, " Exiting");
	        }
	    }
	}
	
	/**
	 * This method writes template in xls format
	 * @param p_excelID
	 * @param p_strArr
	 * @param p_fileName
	 * @throws IOException
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	public static void writeinXLSTemplate(String p_excelID, String[][][][] p_strArr, String p_fileName) throws IOException, RowsExceededException, WriteException{
		final String METHOD_NAME = "writeinXLSTemplate";
	    Locale locale = new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, " p_excelID: " + p_excelID + " p_strArr:" + p_strArr + " p_strArr length: " + p_strArr.length + " p_fileName: " + p_fileName);
        }
        
        WritableWorkbook workbook = null;
        WritableSheet worksheet = null;
        int noOfTotalSheet = 1;
        try {
            String fileName = p_fileName;
            workbook = Workbook.createWorkbook(new File(fileName));
            String key = null;
            String keyName = null;
            Label label = null;
            for (int i = 0; i < noOfTotalSheet; i++) {
                worksheet = workbook.createSheet("Sheet" + (i + 1), i);
                WritableFont times16font = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD, true);
                WritableCellFormat times16format = new WritableCellFormat(times16font);
                int[] indexMapArray = new int[p_strArr[0].length];
                String indexStr = null;
                for(int row1=0;row1<p_strArr.length;row1++){
                for (int col = 0; col < p_strArr[row1].length; col++) {
                    indexStr = null;
                    key = p_strArr[row1][col][0][0];
                    keyName = null;
                    if(!BTSLUtil.isNullString(key)){
                	String param[] = key.split(":");
					keyName =	RestAPIStringParser.getMessage(locale, param[0], null);
	        		keyName = keyName + (param.length > 1 ? param[1] : "");
                    }
                    indexStr = ExcelFileConstants.getWriteProperty(p_excelID, String.valueOf(col));
                    if (indexStr == null) {
                        indexStr = String.valueOf(col);
                    }
                    indexMapArray[col] = Integer.parseInt(indexStr);
                    label = new Label(indexMapArray[col], row1, keyName , times16format);
                    if(!BTSLUtil.isNullString(p_strArr[row1][col][1][0]))
                    {
                    	String [] args = null;
                    	String cellComment = null;
                    	if(!BTSLUtil.isNullString(p_strArr[row1][col][1][1])&&!BTSLUtil.isNullString(p_strArr[row1][col][1][2]))
                    	{
                    		args=new String[]{p_strArr[row1][col][1][1],p_strArr[row1][col][1][2]};
                    		cellComment = BTSLUtil.getMessage(locale, p_strArr[row1][col][1][0], args);
                    	}
                    	else
                    	{
                    		cellComment = BTSLUtil.getMessage(locale, p_strArr[row1][col][1][0], null);
                    	}
                    	
                        WritableCellFeatures cellFeatures1 = new WritableCellFeatures();
                        cellFeatures1.setComment(cellComment, 10.0, 5.0);
                        label.setCellFeatures(cellFeatures1);
                    }
                    worksheet.addCell(label);
                }
                }
                // setting for the vertical freeze panes
                SheetSettings sheetSetting = new SheetSettings();
                sheetSetting = worksheet.getSettings();
                sheetSetting.setVerticalFreeze(1);

                // setting for the horizontal freeze panes
                SheetSettings sheetSetting1 = new SheetSettings();
                sheetSetting1 = worksheet.getSettings();
                sheetSetting1.setHorizontalFreeze(10);
            }
            workbook.write();
        } catch (IOException |  WriteException  e) {
            log.errorTrace(METHOD_NAME, e);
            log.error(METHOD_NAME, " Exception e: " + e.getMessage());
            throw e;
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException |  WriteException  e) {
                log.errorTrace(METHOD_NAME, e);
            }
            worksheet = null;
            workbook = null;
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            log.debug(METHOD_NAME, "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, " Exiting");
            }
        }
	}
	
	/**
	 * This method writes template in xlsx format
	 * @param p_excelID
	 * @param p_strArr
	 * @param p_fileName
	 * @throws IOException
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	public static void writeinXLSXTemplate(String p_excelID, String[][][][] p_strArr, String p_fileName) throws IOException, RowsExceededException, WriteException{
		final String METHOD_NAME = "writeinXLSXTemplate";
	    Locale locale = new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
        
        if (log.isDebugEnabled()) {
            log.debug("writeinXLSX", " p_excelID: " + p_excelID + " p_strArr:" + p_strArr + " p_strArr length: " + p_strArr.length + " p_fileName: " + p_fileName);
        }
        SXSSFWorkbook workbook = null;
        SXSSFSheet worksheet = null;
        String fileName = p_fileName;
        FileOutputStream outputStream = new FileOutputStream(new File(fileName));
        try {
            workbook = new SXSSFWorkbook();
            String key = null;
            String keyName = null;
            int rowNum = 0;
            CellStyle headerStyle = null;
            worksheet = workbook.createSheet("Template Sheet");
          
            CreationHelper factory = workbook.getCreationHelper();
            headerStyle = workbook.createCellStyle();
            Font times16font = workbook.createFont();
            
            final short fontHeight = 14;
            times16font.setFontHeightInPoints(fontHeight);
            times16font.setBold(true);
            headerStyle.setFont(times16font);
            Drawing drawing = worksheet.createDrawingPatriarch();
            ClientAnchor anchor = factory.createClientAnchor();
            Comment comment = null;
            for(int row1=0;row1<p_strArr.length;row1++){
            	  SXSSFRow row = worksheet.createRow(row1);
            for (int col = 0; col < p_strArr[row1].length; col++) {
                key = p_strArr[row1][col][0][0];
                keyName = null;
                // cast issue fix
                // SXSSFCell cell =  row.createCell((short) col);
                SXSSFCell cell =  row.createCell(Short.parseShort(String.valueOf(col)));
                if(!BTSLUtil.isNullString(key)){
                	String param[] = key.split(":");
	        		keyName = BTSLUtil.getMessage(locale, param[0], null);
	        		keyName = keyName + (param.length > 1 ? param[1] : "");
                }
                cell.setCellValue(keyName);
                cell.setCellStyle(headerStyle);
                
                if(!BTSLUtil.isNullString(p_strArr[row1][col][1][0]))
                {
                	String commentLoginID = null;
                	String []args=null;
                	if(col<=4){
                    	anchor.setCol1(cell.getColumnIndex());
                    	anchor.setCol2(cell.getColumnIndex()+5);
                    	anchor.setRow1(row.getRowNum());
                    	anchor.setRow2(row.getRowNum()+8);
                    	comment = drawing.createCellComment(anchor);
                    }
                	if(!BTSLUtil.isNullString(p_strArr[row1][col][1][1])&&!BTSLUtil.isNullString(p_strArr[row1][col][1][2]))
                	{
                		args=new String[]{p_strArr[row1][col][1][1],p_strArr[row1][col][1][2]};
                		commentLoginID = BTSLUtil.getMessage(locale, p_strArr[row1][col][1][0], args);	
                	}
                	else{
                		commentLoginID = BTSLUtil.getMessage(locale, p_strArr[row1][col][1][0], null);
                	}
                	RichTextString str = factory.createRichTextString(commentLoginID);
                	comment.setString(str);
                	cell.setCellComment(comment);
                }
            }
            }
            workbook.write(outputStream);
            outputStream.close();
            workbook.dispose();
        } catch (IOException  e) {
            log.errorTrace(METHOD_NAME, e);
            log.error(METHOD_NAME, " Exception e: " + e.getMessage());
            throw e;
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
                if(outputStream!= null)
            	{
            		outputStream.close();
            	}
            	worksheet=null;
            	workbook=null;
            } catch (IOException  e) {
                log.errorTrace(METHOD_NAME, e);
            }
            worksheet = null;
            workbook = null;
            double t_mem = Runtime.getRuntime().totalMemory() / 1048576;
            double f_mem = Runtime.getRuntime().freeMemory() / 1048576;
            log.debug(METHOD_NAME, "Total memory :" + t_mem + "   free memmory :" + f_mem + " Used memory:" + (t_mem - f_mem));
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, " Exiting");
            }
        }
	}
	
	/**
	 * 
	 * @param p_array
	 * @return
	 * @throws NullPointerException
	 */
	private static int getLengthLongestStringLength(ArrayList p_array) throws NullPointerException {

        int maxLength = 0;
        String longestString = null;
        ListValueVO listVO = new ListValueVO();
        int arraySize = p_array.size();
        for (int i = 0; i < arraySize; i++) {
            listVO = (ListValueVO) p_array.get(i);
            String s = listVO.getLabel();
            if (s.length() > maxLength) {
                maxLength = s.length();
                longestString = s;
            }
        }
        if (longestString.length() != 0) {
            return (longestString.length() + 10) / 5;
        } else {
            return 0;
        }
    }

}

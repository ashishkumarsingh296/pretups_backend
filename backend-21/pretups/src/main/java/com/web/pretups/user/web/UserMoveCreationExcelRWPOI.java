package com.web.pretups.user.web;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import com.btsl.util.MessageResources;

import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.util.BTSLUtil;

/**
 * @author akanksha
 * @Purpose Introducing Apache POI for huge excel data reading/writing for User movement
 */
public class UserMoveCreationExcelRWPOI {
	
	    private static final Log log = LogFactory.getLog(UserMoveCreationExcelRWPOI.class.getName());
	    private final int COLUMNMERGE = 10;
	    private MessageResources pMessages = null;
	    private Locale pLocale = null;


	    /**
	     * @param pExcelID
	     * @param pHashMap
	     * @param messages
	     * @param locale
	     * @param pFileName
	     * @throws IOException 
	     * @throws Exception
	     */
	    
	    public void writeExcel(String pExcelID, HashMap<String, Object> pHashMap, MessageResources messages, Locale locale, String pFileName) throws IOException {
	    	
	        final String METHOD_NAME = "writeExcel";
	        if (log.isDebugEnabled()) {
	            log.debug(METHOD_NAME, " p_excelID: " + pExcelID + " pHashMap:" + pHashMap + " locale: " + locale + " pFileName: " + pFileName);
	        }
	        	
        
	        SXSSFWorkbook  workbook = null;
	        SXSSFSheet  worksheet1 = null; 
	        SXSSFSheet worksheet2 = null;
	        int col = 0;
	        int row = 0;
	        CreationHelper factory = null;
	        CellStyle style1=null;
	        FileOutputStream outputStream = null;
	        Row rowdata = null;
			Cell cell = null;
	       
	        
        	
	        try {
	        	workbook = new SXSSFWorkbook();  
	        	workbook.setCompressTempFiles(true);
	        	factory = workbook.getCreationHelper();
	        	outputStream = new FileOutputStream(pFileName);
	        	style1=workbook.createCellStyle();
	        	Font times11font = workbook.createFont();
	        	times11font.setFontName("Arial");
	        	
	        	
	        	Font times12font = workbook.createFont();
	        	times12font.setFontName("Arial");
	        	times12font.setBold(true);
	        	times12font.setItalic(true);
	        	times12font.setFontHeightInPoints(BTSLUtil.parseIntToShort(12));
	        	style1.setFont(times12font);
	        	
	        	worksheet1 =  workbook.createSheet("Template Sheet ");
	        	worksheet2 =  workbook.createSheet("Master Sheet ");
	                
	            pMessages = messages;
	            pLocale = locale;
	            String keyName = null;

//	            keyName = pMessages.getMessage(pLocale, "user.migration.xlx.mastersheet.heading");
	            
	            if(!BTSLUtil.isNullObject(pMessages))
	            	 keyName = pMessages.getMessage(pLocale, "user.migration.xlx.mastersheet.heading");
	            else
	            	 keyName = RestAPIStringParser.getMessage(pLocale, PretupsErrorCodesI.MASTER_UM_HEADING,null);//PretupsRestUtil.getMessageString("user.migration.xlx.mastersheet.heading");
	                
	            rowdata = worksheet2.createRow(row); 
	            cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
	            worksheet2.addMergedRegion(new CellRangeAddress(row,row,col,col+5));
	            cell.setCellStyle(style1);
    			cell.setCellValue(keyName);
	          
	            // Write the available geography list in the Master sheet.
	            row++;
	            row++;
	            row++;
	            col = 0;
	            row = this.writeGeographyListing(worksheet2, col, row, pHashMap, style1);

	            // Write the available category list in the Master sheet.
	            row++;
	            col = 0;
	            row = this.writeCategoryHierarchy(worksheet2, col, row, pHashMap, style1);

	            // Write the required input for user which has to be migrated in the
	            // Template Sheet.
	            row = 0;
	            col = 0;
	            this.writeUserMigSheet(worksheet1, col, row, pHashMap, style1);
	            workbook.write(outputStream);
                workbook.dispose();
                Runtime runtime = Runtime.getRuntime();
                long memory = runtime.totalMemory() - runtime.freeMemory();
                // Run the garbage collector
                runtime.gc();
                // Calculate the used memory
                memory = runtime.totalMemory() - runtime.freeMemory();
                log.debug(METHOD_NAME,"Used memory is megabytes: " + (memory)/1048576);
	        } catch (IOException e) {
	            log.errorTrace(METHOD_NAME, e);
	            log.error(METHOD_NAME, " Exception e: " + e.getMessage());
	            throw e;
	        } finally {
	        	try {
	                if (outputStream != null) {
	                	outputStream.close();
	                }
	            } catch (Exception e1) {
	                log.errorTrace(METHOD_NAME, e1);
	            }
	            closeWorkbook(METHOD_NAME, workbook);
	            //worksheet1 = null;
	            //worksheet2 = null;
	            //workbook = null;
	            if (log.isDebugEnabled()) {
	                log.debug(METHOD_NAME, " Exiting");
	            }
	        }
	    }


		public void closeWorkbook(final String METHOD_NAME,
				SXSSFWorkbook workbook) {
			try {
			    if (workbook != null) {
			        workbook.close();
			    }
			} catch (Exception e) {
			    log.errorTrace(METHOD_NAME, e);
			}
		}
	    
	    
	    /**
	     * Method writeGeographyListing
	     * This method writes the Geography Details containing zone,area,sub area
	     * etc. [ N level geographies can exists ]
	     * 
	     * @param worksheet2
	     * @param col
	     * @param row
	     * @param pHashMap
	     * @return
	     * @throws Exception
	     * @author Puneet.rs
	     */
	    private int writeGeographyListing(SXSSFSheet worksheet2, int col, int row, HashMap<String, Object> pHashMap, CellStyle style1 ) {
	        final String METHOD_NAME = "writeGeographyListing";
	        log.debug(METHOD_NAME, " pHashMap size=" + pHashMap.size() + " pLocale: " + pLocale + " col=" + col + " row=" + row);
	        ArrayList<UserGeographiesVO> geoList = null;
	        UserGeographiesVO userGeographiesVO = null;
	        ArrayList<String> geoDomainTypeList = null;
	        Row rowdata=null;
	        Cell cell=null;
	        try {
	            rowdata = worksheet2.createRow(row); 
	            String keyName;
	            if(!BTSLUtil.isNullObject(pMessages))
	            	keyName = pMessages.getMessage(pLocale, "batchusercreation.mastersheet.avaliablegeographieslist");
	            else
	            	 keyName = RestAPIStringParser.getMessage(pLocale,PretupsErrorCodesI.AVAILIABLE_GEO_LIST,null);

	            cell =rowdata.createCell(BTSLUtil.parseIntToShort(col));
	            worksheet2.addMergedRegion(new CellRangeAddress(row,row,col,col+2));
	            cell.setCellStyle(style1);
    			cell.setCellValue(keyName);
	            

	            row++;
	            col = 0;

	            rowdata = worksheet2.createRow(row); 
//	            keyName = pMessages.getMessage(pLocale, "batchusercreation.mastersheet.avaliablegeographieslist.note");
	            
	            if(!BTSLUtil.isNullObject(pMessages))
	            	keyName = pMessages.getMessage(pLocale, "batchusercreation.mastersheet.avaliablegeographieslist.note");
	            else
					keyName = RestAPIStringParser.getMessage(pLocale,PretupsErrorCodesI.AVAILIABLE_GEO_LIST_NOTE,null);

	            cell =rowdata.createCell(BTSLUtil.parseIntToShort(col));
	            worksheet2.addMergedRegion(new CellRangeAddress(row,row,col,COLUMNMERGE));
    			cell.setCellValue(keyName);
	            

	            row++;
	            col = 0;

	            // Logic for generating headings
	            geoList = (ArrayList<UserGeographiesVO>) pHashMap.get(PretupsI.BATCH_USR_GEOGRAPHY_LIST);
	            geoDomainTypeList = new ArrayList<String>();

	            geoListAdd(geoList, geoDomainTypeList);
	            // Generate Headings from the ArrayList
//	            final String endTagCode = pMessages.getMessage(pLocale, "code");
	            final String endTagCode;
	            if(!BTSLUtil.isNullObject(pMessages))
	            	endTagCode = pMessages.getMessage(pLocale, "code");
	               else
	                endTagCode = PretupsRestUtil.getMessageString( "code");
	               
//	            final String endTagName = pMessages.getMessage(pLocale, "name");
	            final String endTagName;
	            if(!BTSLUtil.isNullObject(pMessages))
	            	endTagName = pMessages.getMessage(pLocale, "name");
	               else
	            	   endTagName = PretupsRestUtil.getMessageString( "name");
	               
	            String geoType;
	            rowdata = worksheet2.createRow(row); 
	            for (int i = 0, j = geoDomainTypeList.size(); i < j; i++) {
	                geoType = ( geoDomainTypeList.get(i)).trim();
//	                keyName = pMessages.getMessage(pLocale, geoType);
	                if(!BTSLUtil.isNullObject(pMessages))
		                keyName = pMessages.getMessage(pLocale, geoType);
		               else
			            keyName = PretupsRestUtil.getMessageString(geoType);
		               
	                cell =rowdata.createCell(BTSLUtil.parseIntToShort(col));
	                cell.setCellStyle(style1);
	    			cell.setCellValue(keyName + " " + endTagCode + "(" + geoType + ")");
	    			col++;
	    			cell =rowdata.createCell(BTSLUtil.parseIntToShort(col));
	    			cell.setCellStyle(style1);
	    			cell.setCellValue(keyName + " " + endTagName);
	    			
	                col++;
	            }
	            row++;
	            col = 0;
	            int nameOccurance = 0;
	            int oldseqNo = 0;
	            if (geoList != null) {
	                for (int i = 0, j = geoList.size(); i < j; i++) {
	                    userGeographiesVO =geoList.get(i);
	                    if (oldseqNo > userGeographiesVO.getGraphDomainSequenceNumber()) {
	                        nameOccurance -= (oldseqNo - userGeographiesVO.getGraphDomainSequenceNumber()); // for
	                    } else if (oldseqNo < userGeographiesVO.getGraphDomainSequenceNumber()) {
	                        nameOccurance++;
	                    }

	                    col = nameOccurance + userGeographiesVO.getGraphDomainSequenceNumber() - 2;

	                    if (userGeographiesVO.getGraphDomainSequenceNumber() == 2) {
	                        col = 0;
	                        nameOccurance = 0;
	                    }
	                    
	                    rowdata = worksheet2.createRow(row); 
	                    cell =rowdata.createCell(BTSLUtil.parseIntToShort(col));
		    			cell.setCellValue(userGeographiesVO.getGraphDomainCode());
		    			col++;
		                    cell =rowdata.createCell(BTSLUtil.parseIntToShort(col));
			    			cell.setCellValue(userGeographiesVO.getGraphDomainName());

	                    oldseqNo = userGeographiesVO.getGraphDomainSequenceNumber();
	                    row++;
	                }
	            }
	            return row;
	        } catch (Exception ree) {
	            log.errorTrace(METHOD_NAME, ree);
	            log.error(METHOD_NAME, " Exception ree: " + ree.getMessage());
	        }
	        return row;
	    }


		private void geoListAdd(ArrayList<UserGeographiesVO> geoList,
				ArrayList<String> geoDomainTypeList) {
			UserGeographiesVO userGeographiesVO;
			if (geoList != null) {
			    for (int i = 0, j = geoList.size(); i < j; i++) {
			        userGeographiesVO = geoList.get(i);
			        if (!geoDomainTypeList.contains(userGeographiesVO.getGraphDomainType())) {
			            geoDomainTypeList.add(userGeographiesVO.getGraphDomainType());
			        }
			    }
			}
		}
	    
	    
	    /**
	     * Method writeCategoryHierarchy
	     * This method writes the Category Hierarchy list
	     * 
	     * @param worksheet2
	     *            WritableSheet
	     * @param col
	     *            int
	     * @param row
	     *            int
	     * @param p_hashMap
	     *            HashMap<String, Object>
	     * @return int
	     * @throws Exception
	     */
	    private int writeCategoryHierarchy(SXSSFSheet worksheet2, int col, int row, HashMap<String, Object> p_hashMap,CellStyle style1) {
	        final String METHOD_NAME = "writeCategoryHierarchy";
	       log.debug(METHOD_NAME, " p_hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);

	        CategoryVO categoryVO = null;
	        ArrayList<CategoryVO> categoryList = null;
	        String domainCode = null;
	        Row rowdata=null;
	        Cell cell=null;
	        try {
	            

	            rowdata = worksheet2.createRow(row); 
	           
	            String keyName;
	            if(!BTSLUtil.isNullObject(pMessages))
	            	keyName = pMessages.getMessage(pLocale, "user.migration.xlx.mastersheet.categoryhierarchy");
	            else
	            	keyName = RestAPIStringParser.getMessage(pLocale,PretupsErrorCodesI.AVAILABLE_HEIRARCHY,null);
//	            	 keyName = PretupsRestUtil.getMessageString("user.migration.xlx.mastersheet.categoryhierarchy");

	            cell =rowdata.createCell(BTSLUtil.parseIntToShort(col));
	            worksheet2.addMergedRegion(new CellRangeAddress(row,row,col,col+2));
	            cell.setCellStyle(style1);
    			cell.setCellValue(keyName);
    			
    			
	            row++;
	            col = 0;

	            
	            rowdata = worksheet2.createRow(row); 
//	            keyName = pMessages.getMessage(pLocale, "user.migration.xlx.mastersheet.categoryhierarchy.note");
	            
	            if(!BTSLUtil.isNullObject(pMessages))
	            	keyName = pMessages.getMessage(pLocale, "user.migration.xlx.mastersheet.categoryhierarchy.note");
	            else
	            	 keyName = RestAPIStringParser.getMessage(pLocale,"user.migration.xlx.mastersheet.categoryhierarchy.note",null);
	             
	            cell =rowdata.createCell(BTSLUtil.parseIntToShort(col));
	            worksheet2.addMergedRegion(new CellRangeAddress(row,row,col,COLUMNMERGE));
    			cell.setCellValue(keyName);

	            // Label for category code.
	            row++;
	            col = 0;
	            

	            rowdata = worksheet2.createRow(row); 
//	            keyName = pMessages.getMessage(pLocale, "user.migration.xlx.mastersheet.categorycode");
	            
	            if(!BTSLUtil.isNullObject(pMessages))
	            	keyName = pMessages.getMessage(pLocale, "user.migration.xlx.mastersheet.categorycode");
	            else
	            	 keyName = RestAPIStringParser.getMessage(pLocale,"user.migration.xlx.mastersheet.categorycode",null);

	            cell =rowdata.createCell(BTSLUtil.parseIntToShort(col));
	            cell.setCellStyle(style1);
    			cell.setCellValue(keyName);

	            // Label for category name.

//    			keyName = pMessages.getMessage(pLocale, "user.migration.xlx.mastersheet.categoryname");
    			
    			if(!BTSLUtil.isNullObject(pMessages))
	            	keyName = pMessages.getMessage(pLocale, "user.migration.xlx.mastersheet.categoryname");
	            else
	            	 keyName = RestAPIStringParser.getMessage(pLocale,"user.migration.xlx.mastersheet.categoryname",null);
	            
    			cell =rowdata.createCell(BTSLUtil.parseIntToShort(++col));
 	            cell.setCellStyle(style1);
     			cell.setCellValue(keyName);

	            // Label for domain code.
    			 
//     			keyName = pMessages.getMessage(pLocale, "user.migration.xlx.mastersheet.domaincode");
     			if(!BTSLUtil.isNullObject(pMessages))
	            	keyName = pMessages.getMessage(pLocale, "user.migration.xlx.mastersheet.domaincode");
	            else
	            	 keyName = RestAPIStringParser.getMessage(pLocale,"user.migration.xlx.mastersheet.domaincode",null);
	            
     			
     			cell =rowdata.createCell(BTSLUtil.parseIntToShort(++col));
 	            cell.setCellStyle(style1);
     			cell.setCellValue(keyName);

	            // Label for domain name.
     			 
//     			keyName = pMessages.getMessage(pLocale, "user.migration.xlx.mastersheet.domainname");
     			if(!BTSLUtil.isNullObject(pMessages))
	            	keyName = pMessages.getMessage(pLocale, "user.migration.xlx.mastersheet.domainname");
	            else
	            	 keyName = RestAPIStringParser.getMessage(pLocale,"user.migration.xlx.mastersheet.domainname",null);
	            
     			cell =rowdata.createCell(BTSLUtil.parseIntToShort(++col));
 	            cell.setCellStyle(style1);
     			cell.setCellValue(keyName);
     			

	            // Label for Geographical domain type.
	             
//     			keyName = pMessages.getMessage(pLocale, "user.migration.xlx.mastersheet.geodomaintype");
     			if(!BTSLUtil.isNullObject(pMessages))
	            	keyName = pMessages.getMessage(pLocale, "user.migration.xlx.mastersheet.geodomaintype");
	            else
	            	 keyName = RestAPIStringParser.getMessage(pLocale,"user.migration.xlx.mastersheet.geodomaintype",null);
	            
     			
     			cell =rowdata.createCell(BTSLUtil.parseIntToShort(++col));
 	            cell.setCellStyle(style1);
     			cell.setCellValue(keyName);

	            // Label for web interface allowed.
	            
//     			keyName = pMessages.getMessage(pLocale, "user.migration.xlx.mastersheet.webinterfaceallowed");
     			if(!BTSLUtil.isNullObject(pMessages))
	            	keyName = pMessages.getMessage(pLocale, "user.migration.xlx.mastersheet.webinterfaceallowed");
	            else
	            	 keyName = RestAPIStringParser.getMessage(pLocale,"user.migration.xlx.mastersheet.webinterfaceallowed",null);
	            
     			
     			cell =rowdata.createCell(BTSLUtil.parseIntToShort(++col));
 	            cell.setCellStyle(style1);
     			cell.setCellValue(keyName);

	            // Label for low balance allowed.
	           
//     			keyName = pMessages.getMessage(pLocale, "user.migration.xlx.mastersheet.lowbalanceallowed");
     			if(!BTSLUtil.isNullObject(pMessages))
	            	keyName = pMessages.getMessage(pLocale, "user.migration.xlx.mastersheet.lowbalanceallowed");
	            else
	            	 keyName = RestAPIStringParser.getMessage(pLocale,"user.migration.xlx.mastersheet.lowbalanceallowed",null);
	            
     			
     			cell =rowdata.createCell(BTSLUtil.parseIntToShort(++col));
 	            cell.setCellStyle(style1);
     			cell.setCellValue(keyName);

	            // Label for SMS interface allowed.
	           
//     			keyName = pMessages.getMessage(pLocale, "user.migration.xlx.mastersheet.smsinterfaceallowed");
     			if(!BTSLUtil.isNullObject(pMessages))
	            	keyName = pMessages.getMessage(pLocale, "user.migration.xlx.mastersheet.smsinterfaceallowed");
	            else
	            	 keyName = RestAPIStringParser.getMessage(pLocale,"user.migration.xlx.mastersheet.smsinterfaceallowed",null);
	            
     			
     			cell =rowdata.createCell(BTSLUtil.parseIntToShort(++col));
 	            cell.setCellStyle(style1);
     			cell.setCellValue(keyName);
     			

	            // Label for services allowed.
     			 
//     			keyName = pMessages.getMessage(pLocale, "user.migration.xlx.mastersheet.servicesallowed");
     			if(!BTSLUtil.isNullObject(pMessages))
	            	keyName = pMessages.getMessage(pLocale, "user.migration.xlx.mastersheet.servicesallowed");
	            else
	            	 keyName = RestAPIStringParser.getMessage(pLocale,"user.migration.xlx.mastersheet.servicesallowed",null);
	            
     			cell =rowdata.createCell(BTSLUtil.parseIntToShort(++col));
 	            cell.setCellStyle(style1);
     			cell.setCellValue(keyName);
     			

	            row++;
	            col = 0;
	            categoryList = (ArrayList<CategoryVO>) p_hashMap.get(PretupsI.BATCH_USR_CATEGORY_HIERARCHY_LIST);
	            
	            if (categoryList != null) {
	                for (int i = 0, j = categoryList.size(); i < j; i++) {
	                    col = 0;
	                    categoryVO = categoryList.get(i);
	                    if (domainCode != null && !domainCode.equals(categoryVO.getDomainCodeforCategory())) {
	                        row++;
	                    }
	                    
	                    rowdata = worksheet2.createRow(row);
	                    // Value of category code.
	                    
	        
	     	            cell =rowdata.createCell(BTSLUtil.parseIntToShort(col++));
	         			cell.setCellValue(categoryVO.getCategoryCode());

	                    // Value of category name.
	                    
	         			cell =rowdata.createCell(BTSLUtil.parseIntToShort(col++));
	         			cell.setCellValue(categoryVO.getCategoryName());

	                    // Value of domain code.
	                    
	         			cell =rowdata.createCell(BTSLUtil.parseIntToShort(col++));
	         			cell.setCellValue(categoryVO.getDomainCodeforCategory());

	                    // Value of domain name.
	                  
	         			cell =rowdata.createCell(BTSLUtil.parseIntToShort(col++));
	         			cell.setCellValue(categoryVO.getDomainName());

	                    // Value of geographical domain type.
	                   
	         			cell =rowdata.createCell(BTSLUtil.parseIntToShort(col++));
	         			cell.setCellValue(categoryVO.getGrphDomainType());

	                    // Value of WEB interface allowed.
	                   
	         			cell =rowdata.createCell(BTSLUtil.parseIntToShort(col++));
	         			cell.setCellValue(categoryVO.getWebInterfaceAllowed());

	                    // Value of low balance allowed.
	                    
	         			cell =rowdata.createCell(BTSLUtil.parseIntToShort(col++));
	         			cell.setCellValue(categoryVO.getLowBalAlertAllow());

	                    // Value of SMS interface allowed.
	                    
	         			cell =rowdata.createCell(BTSLUtil.parseIntToShort(col++));
	         			cell.setCellValue(categoryVO.getSmsInterfaceAllowed());

	                    // Value of services allowed.
	                   
	         			cell =rowdata.createCell(BTSLUtil.parseIntToShort(col++));
	         			cell.setCellValue(categoryVO.getServiceAllowed());

	                    row++;
	                }
	            }
	            return row;
	        } catch (Exception re) {
	            log.errorTrace(METHOD_NAME, re);
	            log.error(METHOD_NAME, " Exception re: " + re.getMessage());
	        }
			return row;
	    }

	    
	    /**
	     * @param worksheet1
	     * @param col
	     * @param row
	     * @param p_hashMap
	     * @throws Exception
	     */
	    private void writeUserMigSheet(SXSSFSheet worksheet1, int col, int row, HashMap<String, Object> p_hashMap,CellStyle style1) {
	        final String METHOD_NAME = "writeUserMigSheet";
	        if (log.isDebugEnabled()) {
	            log.debug(METHOD_NAME, " p_hashMap size=" + p_hashMap.size() + " p_locale: " + pLocale + " col=" + col + " row=" + row);
	        }
	        Row rowdata=null;
	        Cell cell=null;
	        String comment = null;
	        try {
	        	
	        	rowdata = worksheet1.createRow(row);
//	            String keyName = pMessages.getMessage(pLocale, "user.migration.heading", (String) p_hashMap.get(PretupsI.BATCH_USR_DOMAIN_NAME));
	        	String keyName;
	        	if(!BTSLUtil.isNullObject(pMessages))
	            	keyName = pMessages.getMessage(pLocale, "user.migration.heading", (String) p_hashMap.get(PretupsI.BATCH_USR_DOMAIN_NAME));
	            else
	            	 keyName = RestAPIStringParser.getMessage(pLocale,"user.migration.heading", null);
	            
	            cell =rowdata.createCell(BTSLUtil.parseIntToShort(col));
	            worksheet1.addMergedRegion(new CellRangeAddress(row,row,col,col+10));
	            cell.setCellStyle(style1);
    			cell.setCellValue(keyName);
	            
	            row++;
	            col = 0;
	            
	            rowdata = worksheet1.createRow(row);
//	            keyName = pMessages.getMessage(pLocale, "user.migration.header.downloadedby");
	            if(!BTSLUtil.isNullObject(pMessages))
	            	keyName = pMessages.getMessage(pLocale,  "user.migration.header.downloadedby");
	            else
	            	 keyName = RestAPIStringParser.getMessage(pLocale, "user.migration.header.downloadedby",null);
	            
	            
	            cell =rowdata.createCell(BTSLUtil.parseIntToShort(col));
	            cell.setCellStyle(style1);
    			cell.setCellValue(keyName);

    			cell =rowdata.createCell(BTSLUtil.parseIntToShort(++col));
    			cell.setCellValue((String) p_hashMap.get(PretupsI.BATCH_USR_CREATED_BY));
    			
    			

	            row++;
	            col = 0;
	            
//	            keyName = pMessages.getMessage(pLocale, "user.migration.header.domainname");
	            if(!BTSLUtil.isNullObject(pMessages))
	            	keyName = pMessages.getMessage(pLocale,  "user.migration.header.domainname");
	            else
	            	 keyName = RestAPIStringParser.getMessage(pLocale, "user.migration.header.domainname",null);
	            
	            rowdata = worksheet1.createRow(row);
	            cell =rowdata.createCell(BTSLUtil.parseIntToShort(col));
	            cell.setCellStyle(style1);
    			cell.setCellValue(keyName);

    			cell =rowdata.createCell(BTSLUtil.parseIntToShort(++col));
    			cell.setCellValue((String) p_hashMap.get(PretupsI.BATCH_USR_DOMAIN_NAME));

	            row++;
	            col = 0;
	      
	            rowdata = worksheet1.createRow(row);
//	            keyName = pMessages.getMessage(pLocale, "user.migration.details.strar");
	            if(!BTSLUtil.isNullObject(pMessages))
	            	keyName = pMessages.getMessage(pLocale,  "user.migration.details.strar");
	            else
	            	 keyName = RestAPIStringParser.getMessage( pLocale,"user.migration.details.strar",null);
	            
	            cell =rowdata.createCell(BTSLUtil.parseIntToShort(++col));
	            worksheet1.addMergedRegion(new CellRangeAddress(row,row,col,col+5));
	            cell.setCellStyle(style1);
    			cell.setCellValue(keyName);
	            

	            row++;
	            col = 0;
	            
	            rowdata = worksheet1.createRow(row);
//	            keyName = pMessages.getMessage(pLocale, "user.migration.details.from.user.msisdn");
	            if(!BTSLUtil.isNullObject(pMessages))
	            	keyName = pMessages.getMessage(pLocale,  "user.migration.details.from.user.msisdn");
	            else
	            	 keyName = RestAPIStringParser.getMessage( pLocale,"user.migration.details.from.user.msisdn",null);
	            
	            cell =rowdata.createCell(BTSLUtil.parseIntToShort(col++));
	            cell.setCellStyle(style1);
    			cell.setCellValue(keyName);
    			
	            // add comment
//	            comment = pMessages.getMessage(pLocale, "user.migration.details.from.user.comment");
	            if(!BTSLUtil.isNullObject(pMessages))
	            	comment = pMessages.getMessage(pLocale,  "user.migration.details.from.user.comment");
	            else
	            	comment = RestAPIStringParser.getMessage( pLocale,"user.migration.details.from.user.comment",null);
	            
	            setCellComment(cell,comment);


//	            keyName = pMessages.getMessage(pLocale, "user.migration.details.to.parentMSISDN");
	            if(!BTSLUtil.isNullObject(pMessages))
	            	keyName = pMessages.getMessage(pLocale,  "user.migration.details.to.parentMSISDN");
	            else
	            	 keyName = RestAPIStringParser.getMessage(pLocale, "user.migration.details.to.parentMSISDN",null);
	            
	            cell =rowdata.createCell(BTSLUtil.parseIntToShort(col++));
	            cell.setCellStyle(style1);
    			cell.setCellValue(keyName);
//	            comment = pMessages.getMessage(pLocale, "user.migration.details.to.parentlMSISDN.comment");
	            if(!BTSLUtil.isNullObject(pMessages))
	            	comment = pMessages.getMessage(pLocale,  "user.migration.details.to.parentlMSISDN.comment");
	            else
	            	comment = RestAPIStringParser.getMessage(pLocale, "user.migration.details.to.parentlMSISDN.comment",null);
	            
	            setCellComment(cell,comment);


//	            keyName = pMessages.getMessage(pLocale, "user.migration.details.to.user.geography.code");
	            if(!BTSLUtil.isNullObject(pMessages))
	            	keyName = pMessages.getMessage(pLocale,  "user.migration.details.to.user.geography.code");
	            else
	            	 keyName = RestAPIStringParser.getMessage(pLocale, "user.migration.details.to.user.geography.code",null);
	            
	            cell =rowdata.createCell(BTSLUtil.parseIntToShort(col++));
	            cell.setCellStyle(style1);
    			cell.setCellValue(keyName);

	     

//	            keyName = pMessages.getMessage(pLocale, "user.migration.details.to.user.category.code");
	            if(!BTSLUtil.isNullObject(pMessages))
	            	keyName = pMessages.getMessage(pLocale,  "user.migration.details.to.user.category.code");
	            else
	            	 keyName = RestAPIStringParser.getMessage(pLocale, "user.migration.details.to.user.category.code",null);
	            
	            cell =rowdata.createCell(BTSLUtil.parseIntToShort(col++));
	            cell.setCellStyle(style1);
    			cell.setCellValue(keyName);
	            
	        } catch (Exception ee) {
	            log.errorTrace(METHOD_NAME, ee);
	            log.error(METHOD_NAME, " Exception ee: " + ee.getMessage());
	        } 
	    }
	    
	    protected static void setCellComment(Cell cell, String message) {
	    	Drawing drawing = cell.getSheet().createDrawingPatriarch();
	        CreationHelper factory = cell.getSheet().getWorkbook().getCreationHelper();
	        ClientAnchor anchor = factory.createClientAnchor();
	        anchor.setCol1(cell.getColumnIndex());
	        anchor.setCol2(cell.getColumnIndex() + 5);
	        anchor.setRow1(cell.getRowIndex());
	        anchor.setRow2(cell.getRowIndex() + 5);
	        anchor.setDx1(100);
	        anchor.setDx2(100);
	        anchor.setDy1(100);
	        anchor.setDy2(100);
	        Comment comment = drawing.createCellComment(anchor);
	        RichTextString str = factory.createRichTextString(message);
	        comment.setString(str);
	        comment.setAuthor(" ");
	        comment.setRow(cell.getRowIndex());
	        comment.setColumn(cell.getColumnIndex());
	        cell.setCellComment(comment);
	    }


}

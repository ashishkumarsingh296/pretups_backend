package com.restapi.c2sservices.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.RowErrorMsgLists;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDeatilsVO;
import com.btsl.pretups.channel.transfer.businesslogic.BatchO2CItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CBatchItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CBatchMasterVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.NumberConstants;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.channel.transfer.web.FOCBatchForm;
import com.web.pretups.channel.transfer.web.O2CBatchWithdrawForm;

/**
 * 
 * @author md.sohail
 *
 */
public class ReadGenericFileUtil {
	
	public static final Log log = LogFactory.getLog(ReadGenericFileUtil.class.getName());
	private HashMap<String, String> fileDetailsMap;
	private String filePathCons;
	private String base64val;
	private String requestFileName;
	private String fileNamewithextention;
	private String filepathtemp;
	private boolean isFileWritten = false;
	boolean fileExist=false;
	int rowNum= 0;
	private Set<String> uniqueMsisdnWithDenom = null;
	 ArrayList<String> externalTxnNumber= new ArrayList<String>();;
	


    /**
     * Method: uploadAndReadGenericFile
     * This method read file of XLS, XLSX and CSV format.
     * Check for basic client side validation for each col and store error in errorMap.
     * Store all valid data in map with header as key and column data as a list.
     * 
     * @param fileDetailsMap
     * @param headerRow
     * @param errorMap
     * @return
     * @throws BTSLBaseException
     */
	public LinkedHashMap<String, List<String>> uploadAndReadGenericFile(HashMap<String, String> fileDetailsMap, int headerRow, ErrorMap errorMap) throws BTSLBaseException {

		String methodName = "uploadAndValidateFile";
		if (log.isDebugEnabled()) {
			LogFactory.printLog(methodName, "Entered", log);
			log.debug(methodName, "fileDetailsMap " + fileDetailsMap + " headerRow" + headerRow );
		}
		this.fileDetailsMap = fileDetailsMap;
		
		//validate fileDetailsMap data
		// fileType, fileName, fileAttachment and serviceKeyword is mandatory
		validateFileDetailsMap(headerRow);  // throws exception
	    
		this.base64val = fileDetailsMap.get(PretupsI.FILE_ATTACHMENT);
		this.requestFileName = fileDetailsMap.get(PretupsI.FILE_NAME);
		LinkedHashMap<String, List<String>> responseMap = new LinkedHashMap<String, List<String>>();
		filePathCons = Constants.getProperty("UploadBatchC2CUserListFilePath");
		validateFilePathCons(filePathCons);
		
		String filePathConstemp = filePathCons + "temp/";        // C:/apache-tomcat-8.0.321/logs/BatchC2CUpload/temp/
		createDirectory(filePathConstemp);
		

		setFileNameWithExtention();
		
		filepathtemp = filePathConstemp + fileNamewithextention;   // C:/apache-tomcat-8.0.321/logs/BatchC2CUpload/temp/c2cBatchTransfer.xlsx

		byte[] base64Bytes = decodeFile(base64val);
		log.debug("filepathtemp:", filepathtemp);
		log.debug("base64Bytes:", base64Bytes);
		
		String fileSize = Constants.getProperty("VOMS_MAX_FILE_LENGTH");
		validateFileSize(fileSize, base64Bytes);  // throws exception
		
		writeByteArrayToFile(filepathtemp, base64Bytes);

		try 
		{
            List<String> fileHeaderList = new ArrayList<String>();
			String fileValueSeparator = Constants.getProperty("BL_VOMS_FILE_SEPARATOR");
			if (BTSLUtil.isNullorEmpty(fileValueSeparator)) 
			{
				log.error(methodName, "fileValueSeparator is null in Constants.props");
				 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_SEPERATOR_IN_CONSTANTS,
				 PretupsI.RESPONSE_FAIL,null); 
			}
	
			List<String> fileValueArray = readuploadedfile();
			if( fileValueArray.size() == 0) {
				log.error(methodName, "Uploaded file is empty");
				 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_FILE,
				 PretupsI.RESPONSE_FAIL,null); 
			}

			String[] fileRecord = null;
			int headerColumnLength = 0;
			int fileRow = headerRow;
			int validRowCount = 0;
			String serviceKeyword = fileDetailsMap.get(PretupsI.SERVICE_KEYWORD);
			uniqueMsisdnWithDenom = new HashSet<String>();
			try 
			{
                
				for (int loop = 0; loop < fileValueArray.size(); loop++) 
				{  
					String fileData1 = null;
					fileData1 = (String) fileValueArray.get(loop);
					fileRecord = fileData1.split(fileValueSeparator, -1); // fileRecord reads will contain one extra value, and last value will always be empty
					
					if(loop == 0) 
					{
						headerColumnLength =(PretupsI.FILE_CONTENT_TYPE_CSV.equalsIgnoreCase( 
								fileDetailsMap.get(PretupsI.FILE_TYPE1))) ? fileRecord.length : fileRecord.length - 1 ;
						
						if( !validateFileHeaderLength( serviceKeyword, headerColumnLength ) ) {
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_FILE_HEADINGS,
									PretupsI.RESPONSE_FAIL, null); 
						}
						
						for(int col= 0; col < headerColumnLength; col++) 
						{
							responseMap.put(fileRecord[col].trim(), new ArrayList<String>());
							fileHeaderList.add(fileRecord[col].trim());
						}
					} else 
					{   
						fileRow++; 
						boolean isValidRow = validateEachRow( fileRecord, fileRow, errorMap, serviceKeyword ) ;
					    //boolean isValidRow = validateEachRow(fileHeaderList, fileRecord, errorMap, headerColumnLength, loop, headerRow, fileDetailsMap);
						if(isValidRow) 
						{
							validRowCount++;
							for(int col= 0; col < headerColumnLength; col++) 
							{
								String header = fileHeaderList.get(col);
								responseMap.get(header).add(fileRecord[col]);
							}
						}
					}
				}

				if (fileValueArray.size() == 1) {			
				    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.UPLOADEDFILE_DOESNT_CONTAIN_RECORD,
							PretupsI.RESPONSE_FAIL, null); 
				}
				
				if( isFileWritten && validRowCount == 0 ) {
					filedelete();
				}
				
				fileDetailsMap.put(PretupsI.COLUMN_LENGTH, String.valueOf(headerColumnLength ));
				return responseMap;
					
			}
			catch (ArrayIndexOutOfBoundsException e) {
				if(isFileWritten) {
					filedelete();
				}
				
				log.error(methodName, "Exceptin:e=" + e);
				log.errorTrace("uploadAndProcessFile", e);
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ARRAY_INDEX_OUT_OF_BOUND_EXCEPTION,
						PretupsI.RESPONSE_FAIL, null);

			}
			

		} catch (IOException io) {
			if(isFileWritten) {
				filedelete();
			}
			log.error(methodName, "Exceptin:e=" + io);
			log.errorTrace("uploadAndProcessFile", io);
			io.printStackTrace();
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT,
					PretupsI.RESPONSE_FAIL, null);

		} catch(Exception e) {
			if(isFileWritten) {
				filedelete();
			}
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace("uploadAndProcessFile", e);
			if(!BTSLUtil.isNullorEmpty(e.getMessage())) {
				throw new BTSLBaseException(this, methodName, e.getMessage(),
						PretupsI.RESPONSE_FAIL, null);
			}else {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION,
						PretupsI.RESPONSE_FAIL, null);
			}
			
		}
		
		finally {
			if (log.isDebugEnabled()) {
				LogFactory.printLog(methodName, "Exited", log);
				log.debug(methodName, "responseMap " + responseMap );
			}
		}
	}

	/**
     * Method: uploadAndReadGenericFile
     * This method read file of XLS, XLSX and CSV format.
     * Check for basic client side validation for each col and store error in errorMap.
     * Store all valid data in map with header as key and column data as a list.
     * 
     * @param fileDetailsMap
     * @param headerRow
     * @param errorMap
     * @return
     * @throws BTSLBaseException
     */
	public LinkedHashMap<String, List<String>> uploadAndReadGenericFileO2CWithdraw(HashMap<String, String> fileDetailsMap, int headerRow, ErrorMap errorMap,boolean externalCodeMandatory,String externalsTxnMandatory, ArrayList batchItemsList) throws BTSLBaseException {

		String methodName = "uploadAndValidateFile";
		if (log.isDebugEnabled()) {
			LogFactory.printLog(methodName, "Entered", log);
			log.debug(methodName, "fileDetailsMap " + fileDetailsMap + " headerRow" + headerRow );
		}
		this.fileDetailsMap = fileDetailsMap;
		
		//validate fileDetailsMap data
		// fileType, fileName, fileAttachment and serviceKeyword is mandatory
		validateFileDetailsMap(headerRow);  // throws exception
	   
		this.base64val = fileDetailsMap.get(PretupsI.FILE_ATTACHMENT);
		this.requestFileName = fileDetailsMap.get(PretupsI.FILE_NAME);
		LinkedHashMap<String, List<String>> responseMap = new LinkedHashMap<String, List<String>>();
		filePathCons = Constants.getProperty("UploadBatchO2CUserListFilePath");
		validateFilePathCons(filePathCons);
		
		String filePathConstemp = filePathCons + "temp/";        // C:/apache-tomcat-8.0.52/logs/batcho2c/upload/temp/
		createDirectory(filePathConstemp);
		

		setFileNameWithExtention();
		
		filepathtemp = filePathConstemp + fileNamewithextention;   // C:/apache-tomcat-8.0.321/logs/BatchC2CUpload/temp/c2cBatchTransfer.xlsx

		byte[] base64Bytes = decodeFile(base64val);
		log.debug("filepathtemp:", filepathtemp);
		log.debug("base64Bytes:", base64Bytes);
		
		String fileSize = Constants.getProperty("VOMS_MAX_FILE_LENGTH");
		validateFileSize(fileSize, base64Bytes);  // throws exception
		
		writeByteArrayToFile(filepathtemp, base64Bytes);
		try 
		{
            List<String> fileHeaderList = new ArrayList<String>();
			String fileValueSeparator = Constants.getProperty("BL_VOMS_FILE_SEPARATOR");
			if (BTSLUtil.isNullorEmpty(fileValueSeparator)) 
			{
				log.error(methodName, "fileValueSeparator is null in Constants.props");
				 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_SEPERATOR_IN_CONSTANTS,
				 PretupsI.RESPONSE_FAIL,null); 
			}
	
			List<String> fileValueArray = readuploadedfile();
			if( fileValueArray.size() == 0) {
				log.error(methodName, "Uploaded file is empty");
				 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_FILE,
				 PretupsI.RESPONSE_FAIL,null); 
			}

			String[] fileRecord = null;
			int headerColumnLength = 0;
			int fileRow = headerRow;
			int validRowCount = 0;
			String serviceKeyword = fileDetailsMap.get(PretupsI.SERVICE_KEYWORD);
			uniqueMsisdnWithDenom = new HashSet<String>();
			try 
			{
                
				for (int loop = 0; loop < fileValueArray.size(); loop++) 
				{  
					String fileData1 = null;
					fileData1 = (String) fileValueArray.get(loop);
					fileRecord = fileData1.split(fileValueSeparator, -1); // fileRecord reads will contain one extra value, and last value will always be empty
					
					if(loop == 0) 
					{
						headerColumnLength =(PretupsI.FILE_CONTENT_TYPE_CSV.equalsIgnoreCase( 
								fileDetailsMap.get(PretupsI.FILE_TYPE1))) ? fileRecord.length : fileRecord.length - 1 ;
						
						if( !validateFileHeaderLength( serviceKeyword, headerColumnLength ) ) {
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_FILE_HEADINGS,
									PretupsI.RESPONSE_FAIL, null); 
						}
						
						for(int col= 0; col < headerColumnLength; col++) 
						{
							responseMap.put(fileRecord[col].trim(), new ArrayList<String>());
							fileHeaderList.add(fileRecord[col].trim());
						}
					} else 
					{   
						fileRow++; 
						boolean isValidRow = validateEachRowO2C( fileRecord, fileRow, errorMap, serviceKeyword,headerColumnLength,externalCodeMandatory,externalsTxnMandatory ,batchItemsList) ;
						if(isValidRow) 
						{
							validRowCount++;
							for(int col= 0; col < headerColumnLength; col++) 
							{
								String header = fileHeaderList.get(col);
								responseMap.get(header).add(fileRecord[col]);
							}
						}
					}
				}
				
				if( isFileWritten && validRowCount == 0 ) {
					filedelete();
				}
				ArrayList<String>filepathtemp1= new ArrayList<String>();
				filepathtemp1.add(filepathtemp);
				responseMap.put("filepathtemp", filepathtemp1);
				return responseMap;
					
			} catch (ArrayIndexOutOfBoundsException e) {
				if(isFileWritten) {
					filedelete();
				}
				
				log.error(methodName, "Exceptin:e=" + e);
				log.errorTrace("uploadAndProcessFile", e);
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ARRAY_INDEX_OUT_OF_BOUND_EXCEPTION,
						PretupsI.RESPONSE_FAIL, null);

			}

		} catch (IOException io) {
			if(isFileWritten) {
				filedelete();
			}
			log.error(methodName, "Exceptin:e=" + io);
			log.errorTrace("uploadAndProcessFile", io);
			io.printStackTrace();
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT,
					PretupsI.RESPONSE_FAIL, null);

		} catch(Exception e) {
			if(isFileWritten) {
				filedelete();
			}
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace("uploadAndProcessFile", e);
			if(!BTSLUtil.isNullorEmpty(e.getMessage())) {
				throw new BTSLBaseException(this, methodName, e.getMessage(),
						PretupsI.RESPONSE_FAIL, null);
			}else {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION,
						PretupsI.RESPONSE_FAIL, null);
			}
			
		}
		finally {
			if (log.isDebugEnabled()) {
				LogFactory.printLog(methodName, "Exited", log);
				log.debug(methodName, "responseMap " + responseMap );
			}
		}
	}
	
	
	
	
	/**
     * Method: uploadAndReadGenericFileFOC
     * This method read file of XLS, XLSX and CSV format.
     * Check for basic client side validation for each col and store error in errorMap.
     * Store all valid data in map with header as key and column data as a list.
     * 
     * @param fileDetailsMap
     * @param headerRow
     * @param errorMap
     * @return
     * @throws BTSLBaseException
     */
	public LinkedHashMap<String, List<String>> uploadAndReadGenericFileFOC(HashMap<String, String> fileDetailsMap, int headerRow, ErrorMap errorMap,boolean externalCodeMandatory,String externalsTxnMandatory, ArrayList batchItemsList,ArrayList bonusList,Connection con) throws BTSLBaseException {

		String methodName = "uploadAndReadGenericFileFOC";
		if (log.isDebugEnabled()) {
			LogFactory.printLog(methodName, "Entered", log);
			log.debug(methodName, "fileDetailsMap " + fileDetailsMap + " headerRow" + headerRow );
		}
		this.fileDetailsMap = fileDetailsMap;
		
		//validate fileDetailsMap data
		// fileType, fileName, fileAttachment and serviceKeyword is mandatory
		validateFileDetailsMap(headerRow);  // throws exception
	   
		this.base64val = fileDetailsMap.get(PretupsI.FILE_ATTACHMENT);
		this.requestFileName = fileDetailsMap.get(PretupsI.FILE_NAME);
		LinkedHashMap<String, List<String>> responseMap = new LinkedHashMap<String, List<String>>();
		filePathCons = Constants.getProperty("UploadBatchO2CUserListFilePath");
		validateFilePathCons(filePathCons);
		
		String filePathConstemp = filePathCons + "temp/";        // C:/apache-tomcat-8.0.52/logs/batcho2c/upload/temp/
		createDirectory(filePathConstemp);
		

		setFileNameWithExtention();
		
		filepathtemp = filePathConstemp + fileNamewithextention;   // C:/apache-tomcat-8.0.321/logs/BatchC2CUpload/temp/c2cBatchTransfer.xlsx

		byte[] base64Bytes = decodeFile(base64val);
		log.debug("filepathtemp:", filepathtemp);
		log.debug("base64Bytes:", base64Bytes);
		
		String fileSize = PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_BULK_FILE_SIZE_BYTES)+"";
		validateFileSize(fileSize, base64Bytes);  // throws exception
		
		writeByteArrayToFile(filepathtemp, base64Bytes);
		try 
		{
            List<String> fileHeaderList = new ArrayList<String>();
			String fileValueSeparator = Constants.getProperty("BL_VOMS_FILE_SEPARATOR");
			if (BTSLUtil.isNullorEmpty(fileValueSeparator)) 
			{
				log.error(methodName, "fileValueSeparator is null in Constants.props");
				 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_SEPERATOR_IN_CONSTANTS,
				 PretupsI.RESPONSE_FAIL,null); 
			}
	
			List<String> fileValueArray = readuploadedfile();
			if( fileValueArray.size() == 0) {
				log.error(methodName, "Uploaded file is empty");
				 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_FILE,
				 PretupsI.RESPONSE_FAIL,null); 
			}

			String[] fileRecord = null;
			int headerColumnLength = 0;
			int fileRow = headerRow;
			int validRowCount = 0;
			String serviceKeyword = fileDetailsMap.get(PretupsI.SERVICE_KEYWORD);
			uniqueMsisdnWithDenom = new HashSet<String>();
			try 
			{
                
				for (int loop = 0; loop < fileValueArray.size(); loop++) 
				{  
					String fileData1 = null;
					fileData1 = (String) fileValueArray.get(loop);
					fileRecord = fileData1.split(fileValueSeparator, -1); // fileRecord reads will contain one extra value, and last value will always be empty
					
					if(loop == 0) 
					{
						headerColumnLength =(PretupsI.FILE_CONTENT_TYPE_CSV.equalsIgnoreCase( 
								fileDetailsMap.get(PretupsI.FILE_TYPE1))) ? fileRecord.length : fileRecord.length - 1 ;
						
						if( !validateFileHeaderLength( serviceKeyword, headerColumnLength ) ) {
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_FILE_HEADINGS,
									PretupsI.RESPONSE_FAIL, null); 
						}
						
						for(int col= 0; col < headerColumnLength; col++) 
						{
							responseMap.put(fileRecord[col].trim(), new ArrayList<String>());
							fileHeaderList.add(fileRecord[col].trim());
						}
					} else 
					{   
						fileRow++; 
						boolean isValidRow = validateEachRowFOC( fileRecord, fileRow, errorMap, serviceKeyword,headerColumnLength,externalCodeMandatory,externalsTxnMandatory ,batchItemsList,bonusList,con) ;
						if(isValidRow) 
						{
							validRowCount++;
							for(int col= 0; col < headerColumnLength; col++) 
							{
								String header = fileHeaderList.get(col);
								responseMap.get(header).add(fileRecord[col]);
							}
						}
					}
				}
				
				if( isFileWritten && validRowCount == 0 ) {
					filedelete();
				}
				ArrayList<String>filepathtemp1= new ArrayList<String>();
				filepathtemp1.add(filepathtemp);
				responseMap.put("filepathtemp", filepathtemp1);
				return responseMap;
					
			} catch (ArrayIndexOutOfBoundsException e) {
				if(isFileWritten) {
					filedelete();
				}
				
				log.error(methodName, "Exceptin:e=" + e);
				log.errorTrace("uploadAndProcessFile", e);
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ARRAY_INDEX_OUT_OF_BOUND_EXCEPTION,
						PretupsI.RESPONSE_FAIL, null);

			}

		} catch (IOException io) {
			if(isFileWritten) {
				filedelete();
			}
			log.error(methodName, "Exceptin:e=" + io);
			log.errorTrace("uploadAndProcessFile", io);
			io.printStackTrace();
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT,
					PretupsI.RESPONSE_FAIL, null);

		} catch(Exception e) {
			if(isFileWritten) {
				filedelete();
			}
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace("uploadAndProcessFile", e);
			if(!BTSLUtil.isNullorEmpty(e.getMessage())) {
				throw new BTSLBaseException(this, methodName, e.getMessage(),
						PretupsI.RESPONSE_FAIL, null);
			}else {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION,
						PretupsI.RESPONSE_FAIL, null);
			}
			
		}
		finally {
			if (log.isDebugEnabled()) {
				LogFactory.printLog(methodName, "Exited", log);
				log.debug(methodName, "responseMap " + responseMap );
			}
		}
	}
	
	
	
	/**
     * Method: uploadAndReadGenericFile
     * This method read file of XLS, XLSX and CSV format.
     * Check for basic client side validation for each col and store error in errorMap.
     * Store all valid data in map with header as key and column data as a list.
     * 
     * @param fileDetailsMap
     * @param headerRow
     * @param errorMap
     * @return
     * @throws BTSLBaseException
     */
	public LinkedHashMap<String, List<String>> uploadAndReadGenericFileBatchUserInitiate(HashMap<String, String> fileDetailsMap, int startRow, int headerRow ,ErrorMap errorMap) throws BTSLBaseException {

		String methodName = "uploadAndReadGenericFileBatchUserInitiate";
		if (log.isDebugEnabled()) {
			LogFactory.printLog(methodName, "Entered", log);
			log.debug(methodName, "fileDetailsMap " + fileDetailsMap + " StartRow " + startRow + " HeadereRow " + headerRow );
		}
		fileDetailsMap.put("headerRowNum", Integer.toString(headerRow));
		this.fileDetailsMap = fileDetailsMap;
		
		//validate fileDetailsMap data
		// fileType, fileName, fileAttachment and serviceKeyword is mandatory
		validateFileDetailsMap(startRow);  // will set where to start reading from
	    
		this.base64val = fileDetailsMap.get(PretupsI.FILE_ATTACHMENT);
		this.requestFileName = fileDetailsMap.get(PretupsI.FILE_NAME);
		LinkedHashMap<String, List<String>> responseMap = new LinkedHashMap<String, List<String>>();
		filePathCons = Constants.getProperty("UploadBatchUserFilePath");
		validateFilePathCons(filePathCons);
		
		String filePathConstemp = filePathCons + "temp/";        // C:/apache-tomcat-8.0.321/logs/BatchC2CUpload/temp/
		createDirectory(filePathConstemp);
		

		setFileNameWithExtention();
		
		filepathtemp = filePathConstemp + fileNamewithextention;   // C:/apache-tomcat-8.0.321/logs/BatchC2CUpload/temp/c2cBatchTransfer.xlsx

		byte[] base64Bytes = decodeFile(base64val);
		log.debug("filepathtemp:", filepathtemp);
		log.debug("base64Bytes:", base64Bytes);
		
		String fileSize = Constants.getProperty("MAX_XLS_FILE_SIZE_FOR_BULKUSER");
		validateFileSize(fileSize, base64Bytes);  // throws exception
		
		writeByteArrayToFile(filepathtemp, base64Bytes);

		try 
		{
            List<String> fileHeaderList = new ArrayList<String>();
			String fileValueSeparator = Constants.getProperty("BL_VOMS_FILE_SEPARATOR");
			if (BTSLUtil.isNullorEmpty(fileValueSeparator)) 
			{
				log.error(methodName, "fileValueSeparator is null in Constants.props");
				 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_SEPERATOR_IN_CONSTANTS,
				 PretupsI.RESPONSE_FAIL,null); 
			}
	
			List<String> fileValueArray = readuploadedfile();
			if( fileValueArray.size() == 0) {
				log.error(methodName, "Uploaded file is empty");
				 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_FILE,
				 PretupsI.RESPONSE_FAIL,null); 
			}

			String[] fileRecord = null;
			int headerColumnLength = 0;
			int fileRow = headerRow;
			int validRowCount = 0;
			String serviceKeyword = fileDetailsMap.get(PretupsI.SERVICE_KEYWORD);
			uniqueMsisdnWithDenom = new HashSet<String>();
			try 
			{
                
				for (int loop = 0; loop < fileValueArray.size(); loop++) 
				{  
					String fileData1 = null;
					fileData1 = (String) fileValueArray.get(loop);
//					fileRecord = fileData1.split(fileValueSeparator, -1); // fileRecord reads will contain one extra value, and last value will always be empty
					fileRecord = fileData1.split("#", -1);
					if(loop < headerRow) {
						if(loop==1) fileDetailsMap.put("domainName", fileRecord[1]);
						if(loop==2) fileDetailsMap.put("geoName", fileRecord[1]);
						continue;
					}
					if(loop == headerRow) 
					{
						headerColumnLength =(PretupsI.FILE_CONTENT_TYPE_CSV.equalsIgnoreCase( 
								fileDetailsMap.get(PretupsI.FILE_TYPE1))) ? fileRecord.length : fileRecord.length - 1 ;
						
//						if( !validateFileHeaderLength( serviceKeyword, headerColumnLength ) ) {
//							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_FILE_HEADINGS,
//									PretupsI.RESPONSE_FAIL, null); 
//						}
						for(int col= 0; col < headerColumnLength; col++) 
						{
							responseMap.put(fileRecord[col].trim(), new ArrayList<String>());
							fileHeaderList.add(fileRecord[col].trim());
						}
					} else 
					{   
						fileRow++; 
						boolean isValidRow = validateEachRow( fileRecord, fileRow, errorMap, serviceKeyword ) ;
					    //boolean isValidRow = validateEachRow(fileHeaderList, fileRecord, errorMap, headerColumnLength, loop, headerRow, fileDetailsMap);
						if(isValidRow) 
						{
							validRowCount++;
							for(int col= 0; col < headerColumnLength; col++) 
							{
								String header = fileHeaderList.get(col);
								responseMap.get(header).add(fileRecord[col]);
							}
						}
					}
				}
				
				if( isFileWritten && validRowCount == 0 ) {
					filedelete();
				}
				
				fileDetailsMap.put("filePath", filepathtemp );
				fileDetailsMap.put("recordCount", Integer.toString(validRowCount));
				
				return responseMap;
					
			} catch (ArrayIndexOutOfBoundsException e) {
				if(isFileWritten) {
					filedelete();
				}
				
				log.error(methodName, "Exceptin:e=" + e);
				log.errorTrace("uploadAndProcessFile", e);
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ARRAY_INDEX_OUT_OF_BOUND_EXCEPTION,
						PretupsI.RESPONSE_FAIL, null);

			}

		} catch (IOException io) {
			if(isFileWritten) {
				filedelete();
			}
			log.error(methodName, "Exceptin:e=" + io);
			log.errorTrace("uploadAndProcessFile", io);
			io.printStackTrace();
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT,
					PretupsI.RESPONSE_FAIL, null);

		} catch(Exception e) {
			if(isFileWritten) {
				filedelete();
			}
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace("uploadAndProcessFile", e);
			if(!BTSLUtil.isNullorEmpty(e.getMessage())) {
				throw new BTSLBaseException(this, methodName, e.getMessage(),
						PretupsI.RESPONSE_FAIL, null);
			}else {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION,
						PretupsI.RESPONSE_FAIL, null);
			}
			
		}
		finally {
			if (log.isDebugEnabled()) {
				LogFactory.printLog(methodName, "Exited", log);
				log.debug(methodName, "responseMap " + responseMap );
			}
		}
	}
	
	public LinkedHashMap<String, List<String>> uploadAndReadGenericFileBatchUserModify(HashMap<String, String> fileDetailsMap, int startRow, int headerRow ,ErrorMap errorMap,ArrayList fileErrorList) throws BTSLBaseException {

		String methodName = "uploadAndReadGenericFileBatchUserInitiate";
		if (log.isDebugEnabled()) {
			LogFactory.printLog(methodName, "Entered", log);
			log.debug(methodName, "fileDetailsMap " + fileDetailsMap + " StartRow " + startRow + " HeadereRow " + headerRow );
		}
		fileDetailsMap.put("headerRowNum", Integer.toString(headerRow));
		this.fileDetailsMap = fileDetailsMap;
		
		//validate fileDetailsMap data
		// fileType, fileName, fileAttachment and serviceKeyword is mandatory
		validateFileDetailsMap(startRow);  // will set where to start reading from
	    
		this.base64val = fileDetailsMap.get(PretupsI.FILE_ATTACHMENT);
		this.requestFileName = fileDetailsMap.get(PretupsI.FILE_NAME);
		LinkedHashMap<String, List<String>> responseMap = new LinkedHashMap<String, List<String>>();
		filePathCons = Constants.getProperty("UPLOADMODIFYBATCHUSERFILEPATH");
		validateFilePathCons(filePathCons);
		
//		String filePathConstemp = filePathCons + "temp/";        // C:/apache-tomcat-8.0.321/logs/BatchC2CUpload/temp/
		createDirectory(filePathCons);
		

		setFileNameWithExtention();
		
		filepathtemp = filePathCons + fileNamewithextention;   // C:/apache-tomcat-8.0.321/logs/BatchC2CUpload/temp/c2cBatchTransfer.xlsx

		byte[] base64Bytes = decodeFile(base64val);
		log.debug("filepathtemp:", filepathtemp);
		log.debug("base64Bytes:", base64Bytes);
		
		String fileSize = Constants.getProperty("MAX_XLS_FILE_SIZE_FOR_BULKUSER");
		validateFileSize(fileSize, base64Bytes);  // throws exception
		
		writeByteArrayToFile(filepathtemp, base64Bytes);

		try 
		{
            List<String> fileHeaderList = new ArrayList<String>();
			String fileValueSeparator = Constants.getProperty("BL_VOMS_FILE_SEPARATOR");
			if (BTSLUtil.isNullorEmpty(fileValueSeparator)) 
			{
				log.error(methodName, "fileValueSeparator is null in Constants.props");
				 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_SEPERATOR_IN_CONSTANTS,
				 PretupsI.RESPONSE_FAIL,null); 
			}
	
			List<String> fileValueArray = readuploadedfile();
			if( fileValueArray.size() == 0 || fileValueArray.size() == 6) {
				log.error(methodName, "Uploaded file is empty");
				 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_FILE,
				 PretupsI.RESPONSE_FAIL,null); 
			}

			String[] fileRecord = null;
			int headerColumnLength = 0;
			int fileRow = headerRow+2;
			int validRowCount = 0;
			String serviceKeyword = fileDetailsMap.get(PretupsI.SERVICE_KEYWORD);
			uniqueMsisdnWithDenom = new HashSet<String>();
			try 
			{
                
				for (int loop = 0; loop < fileValueArray.size(); loop++) 
				{  
					String fileData1 = null;
					fileData1 = (String) fileValueArray.get(loop);
//					fileRecord = fileData1.split(fileValueSeparator, -1); // fileRecord reads will contain one extra value, and last value will always be empty
					fileRecord = fileData1.split(":", -1);
					if(loop < headerRow) {
						if(loop==1) fileDetailsMap.put("domainName", fileRecord[1]);
						if(loop==2) fileDetailsMap.put("categoryName", fileRecord[1]);
						if(loop==3) fileDetailsMap.put("geoName", fileRecord[1]);
						continue;
					}
					if(loop == headerRow) 
					{
						headerColumnLength =(PretupsI.FILE_CONTENT_TYPE_CSV.equalsIgnoreCase( 
								fileDetailsMap.get(PretupsI.FILE_TYPE1))) ? fileRecord.length : fileRecord.length - 1 ;
						
//						if( !validateFileHeaderLength( serviceKeyword, headerColumnLength ) ) {
//							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_FILE_HEADINGS,
//									PretupsI.RESPONSE_FAIL, null); 
//						}
						for(int col= 0; col < headerColumnLength; col++) 
						{
							responseMap.put(fileRecord[col].trim(), new ArrayList<String>());
							fileHeaderList.add(fileRecord[col].trim());
						}
					} else 
					{   
						fileRow++; 
						boolean isValidRow = validateEachRow( fileRecord, fileRow, errorMap, serviceKeyword ) ;
					    //boolean isValidRow = validateEachRow(fileHeaderList, fileRecord, errorMap, headerColumnLength, loop, headerRow, fileDetailsMap);
						if(isValidRow) 
						{
							validRowCount++;
							for(int col= 0; col < headerColumnLength; col++) 
							{
								String header = fileHeaderList.get(col);
								responseMap.get(header).add(fileRecord[col]);
							}
						}
						else {
						for(int i= 0; i < headerColumnLength; i++)  {
							String header = fileHeaderList.get(i);
							responseMap.get(header).add("");
						}
						fileErrorList.add(errorMap.getRowErrorMsgLists());
						}
					}
				}
				
				if( isFileWritten && validRowCount == 0 ) {
					filedelete();
				}
				
				fileDetailsMap.put("filePath", filepathtemp );
				fileDetailsMap.put("recordCount", Integer.toString(validRowCount));
				
				return responseMap;
					
			} catch (ArrayIndexOutOfBoundsException e) {
				if(isFileWritten) {
					filedelete();
				}
				
				log.error(methodName, "Exceptin:e=" + e);
				log.errorTrace("uploadAndProcessFile", e);
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ARRAY_INDEX_OUT_OF_BOUND_EXCEPTION,
						PretupsI.RESPONSE_FAIL, null);

			}

		} catch (IOException io) {
			if(isFileWritten) {
				filedelete();
			}
			log.error(methodName, "Exceptin:e=" + io);
			log.errorTrace("uploadAndProcessFile", io);
			io.printStackTrace();
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT,
					PretupsI.RESPONSE_FAIL, null);

		} catch(Exception e) {
			if(isFileWritten) {
				filedelete();
			}
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace("uploadAndProcessFile", e);
			if(!BTSLUtil.isNullorEmpty(e.getMessage())) {
				throw new BTSLBaseException(this, methodName, e.getMessage(),
						PretupsI.RESPONSE_FAIL, null);
			}else {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION,
						PretupsI.RESPONSE_FAIL, null);
			}
			
		}
		finally {
			if (log.isDebugEnabled()) {
				LogFactory.printLog(methodName, "Exited", log);
				log.debug(methodName, "responseMap " + responseMap );
			}
		}
	}

	/**
	 * Method: validateFileDetailsMap
	 * validate mandatory field required for reading and validation for file data
	 * 
	 * @param headerRow
	 * @throws BTSLBaseException
	 */
	private void validateFileDetailsMap(int headerRow) throws BTSLBaseException{
		if(headerRow >= 0) {
			this.rowNum = headerRow;
		}
		if(!BTSLUtil.isNullString(fileDetailsMap.get(PretupsI.FILE_TYPE1)) && !BTSLUtil.isNullString(fileDetailsMap.get(PretupsI.FILE_NAME)) 
				&& !BTSLUtil.isNullString(fileDetailsMap.get(PretupsI.FILE_ATTACHMENT))) 
		{
			 validateFileName(fileDetailsMap.get(PretupsI.FILE_NAME)); // throw exception
			 validateFileType(fileDetailsMap.get(PretupsI.FILE_TYPE1));
		} 
		else 
		{
			if (BTSLUtil.isNullString(fileDetailsMap.get(PretupsI.SERVICE_KEYWORD))) //This condition won't occur in actual deployment
			{
				log.error("validateFileInput", "SERVICEKEYWORD IS NULL");
				 throw new BTSLBaseException(this, "validateFileInput", PretupsErrorCodesI.SERVICE_KEYWORD_REQUIRED,
				 PretupsI.RESPONSE_FAIL,null);
			}
			else {
				log.error("validateFileInput", "FILETYPE/FILENAME/FILEATTACHMENT IS NULL");
				 throw new BTSLBaseException(this, "validateFileInput", PretupsErrorCodesI.INVALID_FILE_INPUT,
				 PretupsI.RESPONSE_FAIL,null); 
			}
			 
		}
	}
	
	/**
	 * 
	 * @param filePathCons
	 * @throws BTSLBaseException
	 */
	public void validateFilePathCons(String filePathCons) throws BTSLBaseException {
		if (BTSLUtil.isNullorEmpty(filePathCons)) {
			 throw new BTSLBaseException(this, "validateFilePathCons", PretupsErrorCodesI.EMPTY_FILE_PATH_IN_CONSTANTS,
					 PretupsI.RESPONSE_FAIL,null); 
		}
	}
	
	
	
	
	/**
	 * Method createDirectory will create directory at specified path if direcry do not exists
	 * 
	 * @param filePathConstemp
	 * @throws BTSLBaseException
	 */

	public void createDirectory(String filePathConstemp) throws BTSLBaseException {

		String methodName = "createDirectory";
		File fileTempDir = new File(filePathConstemp);
		if (!fileTempDir.isDirectory()) {
			fileTempDir.mkdirs();
		}
		if (!fileTempDir.exists()) {
			log.debug("Directory does not exist : ", fileTempDir);
			throw new BTSLBaseException("OAuthenticationUtil", methodName,
					PretupsErrorCodesI.BATCH_UPLOAD_DIRECTORY_DO_NOT_EXISTS, PretupsI.RESPONSE_FAIL, null); // provide
																											// your own
		}
	}
	
	

	/**
	 * Method setFileNameWithExtention 
	 */
  
	public void setFileNameWithExtention() {
		if (PretupsI.FILE_CONTENT_TYPE_CSV.equalsIgnoreCase(fileDetailsMap.get(PretupsI.FILE_TYPE1))) {
			fileNamewithextention = requestFileName + ".csv";
		} else if (PretupsI.FILE_CONTENT_TYPE_XLS.equalsIgnoreCase(fileDetailsMap.get(PretupsI.FILE_TYPE1))) {
			fileNamewithextention = requestFileName + ".xls";
		} else if (PretupsI.FILE_CONTENT_TYPE_XLSX.equalsIgnoreCase(fileDetailsMap.get(PretupsI.FILE_TYPE1))) {
			fileNamewithextention = requestFileName + ".xlsx";
		}
	}
	
	
   /**
    * 
    * @param base64value
    * @return
    * @throws BTSLBaseException
    */
	public byte[] decodeFile(String base64value) throws BTSLBaseException {
		byte[] base64Bytes = null;
		try {
			log.debug("decodeFile: ", base64value);
			base64Bytes = Base64.getMimeDecoder().decode(base64value);
			log.debug("base64Bytes: ", base64Bytes);
		} catch (IllegalArgumentException il) {
			log.debug("Invalid file format", il);
			log.error("Invalid file format", il);
			log.errorTrace("Invalid file format", il);
			throw new BTSLBaseException(this, "decodeFile", PretupsErrorCodesI.INVALID_FILE_FORMAT,
					PretupsI.RESPONSE_FAIL, null);
		}
		return base64Bytes;
	}
	
	/**
	 * 
	 * @param fileSize
	 * @param base64Bytes
	 * @throws BTSLBaseException
	 */
	public void validateFileSize(String fileSize, byte[] base64Bytes) throws BTSLBaseException{
		final String methodName = "validateFileSize";
		if (BTSLUtil.isNullorEmpty(fileSize)) {
			log.error(methodName, "VOMS_MAX_FILE_LENGTH is null in Constant.props");
			 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_FILE_SIZE_IN_CONSTANTS,
			 PretupsI.RESPONSE_FAIL,null); 
		}else if(base64Bytes.length > Long.parseLong(fileSize) ){
			 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.FILE_SIZE_LARGE,
					 PretupsI.RESPONSE_FAIL,null); 
		}
	}
	
	

	/**
	 * Method writeByteArrayToFile write decode data at specified path
	 * 
	 * @param filePath
	 * @param base64Bytes
	 * @throws BTSLBaseException
	 */

	public void writeByteArrayToFile(String filePath, byte[] base64Bytes) throws BTSLBaseException {
		try {
			log.debug("writeByteArrayToFile: ", filePath);
			log.debug("writeByteArrayToFile: ", base64Bytes);
			if (new File(filepathtemp).exists()) {
				fileExist=true;
				throw new BTSLBaseException("OAuthenticationUtil", "writeByteArrayToFile",
						PretupsErrorCodesI.BATCH_UPLOAD_FILE_EXISTS, PretupsI.RESPONSE_FAIL, null);
			}
			FileUtils.writeByteArrayToFile(new File(filePath), base64Bytes);
			isFileWritten = true ;
		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			log.debug("writeByteArrayToFile: ", e.getMessage());
			log.error("writeByteArrayToFile", "Exceptin:e=" + e);
			log.errorTrace("writeByteArrayToFile", e);

		}
	}
	
	/**
	 * Method writeByteArrayToFileBatchOptInit write decode data at specified path
	 * 
	 * @param filePath
	 * @param base64Bytes
	 * @throws BTSLBaseException
	 */

	public void writeByteArrayToFileBatchOptInit(String filePath, byte[] base64Bytes) throws BTSLBaseException {
		try {
			log.debug("writeByteArrayToFileBatchOptInit: ", filePath);
			log.debug("writeByteArrayToFileBatchOptInit: ", base64Bytes);
			if (new File(filepathtemp).exists()) {
				fileExist=true;
				throw new BTSLBaseException("ReadGenericFileUtil", "writeByteArrayToFileBatchOptInit",
						PretupsErrorCodesI.BATCH_NAME_UPLOAD_FILE_EXISTS, PretupsI.RESPONSE_FAIL, null);
			}
			FileUtils.writeByteArrayToFile(new File(filePath), base64Bytes);
			isFileWritten = true ;
		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			log.debug("writeByteArrayToFileBatchOptInit: ", e.getMessage());
			log.error("writeByteArrayToFileBatchOptInit", "Exceptin:e=" + e);
			log.errorTrace("writeByteArrayToFileBatchOptInit", e);

		}
	}
	
	
	/**
	 * Method readuploadedfile read data from from and store in in ArrayList of String
	 * 
	 * @return fileValueArray
	 * @throws IOException
	 * @throws BTSLBaseException 
	 */

	public List<String> readuploadedfile() throws IOException, BTSLBaseException {
		List<String> fileValueArray = null;
		if (this.fileDetailsMap.get(PretupsI.SERVICE_KEYWORD).equals(PretupsI.BATCH_OPT_USR_MODIFICATION_SERVICE)){
			if (PretupsI.FILE_CONTENT_TYPE_XLSX.equalsIgnoreCase(fileDetailsMap.get(PretupsI.FILE_TYPE1))) {
				fileValueArray = readExcelForXLSXBulkModify(filepathtemp);
			} else if (PretupsI.FILE_CONTENT_TYPE_XLS.equalsIgnoreCase(fileDetailsMap.get(PretupsI.FILE_TYPE1))) {
				fileValueArray = readExcelForXLS(filepathtemp);

			} else {
				fileValueArray = readFile(filepathtemp);
			}
		}
		else if(!this.fileDetailsMap.get(PretupsI.SERVICE_KEYWORD).equals(PretupsI.BATCH_OPT_USR_INITIATION_SERVICE)) {
			if (PretupsI.FILE_CONTENT_TYPE_XLSX.equalsIgnoreCase(fileDetailsMap.get(PretupsI.FILE_TYPE1))) {
				fileValueArray = readExcelForXLSX(filepathtemp);
			} else if (PretupsI.FILE_CONTENT_TYPE_XLS.equalsIgnoreCase(fileDetailsMap.get(PretupsI.FILE_TYPE1))) {
				fileValueArray = readExcelForXLS(filepathtemp);

			} else {
				fileValueArray = readFile(filepathtemp);
			}
		}else {
			if (PretupsI.FILE_CONTENT_TYPE_XLSX.equalsIgnoreCase(fileDetailsMap.get(PretupsI.FILE_TYPE1))) {
				fileValueArray = readExcelForXLSX(filepathtemp, Integer.parseInt( this.fileDetailsMap.get("headerRowNum") ));
			} else if (PretupsI.FILE_CONTENT_TYPE_XLS.equalsIgnoreCase(fileDetailsMap.get(PretupsI.FILE_TYPE1))) {
				fileValueArray = readExcelForXLS(filepathtemp, Integer.parseInt( this.fileDetailsMap.get("headerRowNum") ));

			} else {
				fileValueArray = readFile(filepathtemp);
			}
		}
		
		return fileValueArray;
	}
	
	/**
	 * Method readuploadedfileBatchOptInit read data from from and store in in ArrayList of String
	 * 
	 * @return fileValueArray
	 * @throws IOException
	 * @throws BTSLBaseException 
	 */

	public List<String> readUploadedFileBatchOptInit() throws IOException, BTSLBaseException {
		List<String> fileValueArray = null;
		
		if(!this.fileDetailsMap.get(PretupsI.SERVICE_KEYWORD).equals(PretupsI.BATCH_OPT_USR_INITIATION_SERVICE)) {
			if (PretupsI.FILE_CONTENT_TYPE_XLSX.equalsIgnoreCase(fileDetailsMap.get(PretupsI.FILE_TYPE1))) {
				fileValueArray = readExcelForXLSX(filepathtemp);
			} else if (PretupsI.FILE_CONTENT_TYPE_XLS.equalsIgnoreCase(fileDetailsMap.get(PretupsI.FILE_TYPE1))) {
				fileValueArray = readExcelForXLS(filepathtemp);

			} else {
				fileValueArray = readFile(filepathtemp);
			}
		}else {
			if (PretupsI.FILE_CONTENT_TYPE_XLSX.equalsIgnoreCase(fileDetailsMap.get(PretupsI.FILE_TYPE1))) {
				fileValueArray = readExcelForXLSXBatchOptInit(filepathtemp, Integer.parseInt( this.fileDetailsMap.get("headerRowNum") ));
			} else if (PretupsI.FILE_CONTENT_TYPE_XLS.equalsIgnoreCase(fileDetailsMap.get(PretupsI.FILE_TYPE1))) {
				fileValueArray = readExcelForXLSBatchOptInit(filepathtemp, Integer.parseInt( this.fileDetailsMap.get("headerRowNum") ));

			} else {
				fileValueArray = readFile(filepathtemp);
			}
		}
		
		return fileValueArray;
	}
	/**
	 * 
	 * @param filepathtemp
	 * @return
	 * @throws IOException
	 * @throws BTSLBaseException 
	 */

	public  List<String> readExcelForXLSX(String filepathtemp ) throws IOException, BTSLBaseException {
		List<String> fileValueArray = new ArrayList<String>();
		XSSFWorkbook workbook = null;
		XSSFSheet excelsheet = null;
		String tempStr = "";
		int lastCellNum;
		DataFormatter formatter = null;
		try (FileInputStream file = new FileInputStream(filepathtemp)) {
			workbook = new XSSFWorkbook(file);
			excelsheet = workbook.getSheetAt(0);
			int rowcount = excelsheet.getLastRowNum();
			if(rowcount <= 0) {
				return fileValueArray;
			}
			Row firstDataRow = excelsheet.getRow(this.rowNum);
			lastCellNum = firstDataRow.getLastCellNum();
			int temp = this.rowNum;
			while (temp != (rowcount + 1)) {
				Row currRowData = excelsheet.getRow(temp);
				if(currRowData!=null) {
				int lCellNum = currRowData.getLastCellNum(); 
				tempStr = "";
				for (int i = 0; i < lCellNum; i++) { // NumberConstants.THREE.getIntValue()
					formatter = new DataFormatter();
					if(!BTSLUtil.isNullorEmpty(excelsheet.getRow(temp))) {
						tempStr = tempStr + formatter.formatCellValue(excelsheet.getRow(temp).getCell(i));
					}
					
					if (i < lCellNum) {
						tempStr = tempStr + ",";
					}
					}
				}
				else {
					tempStr = "";
				}
				temp++;
				fileValueArray.add(tempStr);
			}
		}catch(Exception e) {
			throw new BTSLBaseException(this, "readExcelForXLSX", PretupsErrorCodesI.FILE_FORMAT_XLSX_INVALID,
					PretupsI.RESPONSE_FAIL, null);
		}
		return fileValueArray;
	}


	public  List<String> readExcelForXLSXBulkModify(String filepathtemp ) throws IOException, BTSLBaseException {
		String methodName = "readExcelForXLSXBulkModify";
		List<String> fileValueArray = new ArrayList<String>();
		XSSFWorkbook workbook = null;
		XSSFSheet excelsheet = null;
		String tempStr = "";
		int lastCellNum;
		DataFormatter formatter = null;
		try (FileInputStream file = new FileInputStream(filepathtemp)) {
			workbook = new XSSFWorkbook(file);
			excelsheet = workbook.getSheetAt(0);
			int rowcount = excelsheet.getLastRowNum();
			if(rowcount <= 0) {
				return fileValueArray;
			}
			Row firstDataRow = excelsheet.getRow(this.rowNum);
			lastCellNum = firstDataRow.getLastCellNum();
			int temp = this.rowNum;
			while (temp != (rowcount + 1)) {
				Row currRowData = excelsheet.getRow(temp);
				if(currRowData!=null) {
					int lCellNum = currRowData.getLastCellNum();
					tempStr = "";
					for (int i = 0; i < lCellNum; i++) { // NumberConstants.THREE.getIntValue()
						formatter = new DataFormatter();
						if(!BTSLUtil.isNullorEmpty(excelsheet.getRow(temp))) {
							tempStr = tempStr + formatter.formatCellValue(excelsheet.getRow(temp).getCell(i));
						}

						if (i < lCellNum) {
							tempStr = tempStr + ":";
						}
					}
				}
				else {
					tempStr = "";
				}
				temp++;
				fileValueArray.add(tempStr);
			}
		}catch(Exception e) {
			throw new BTSLBaseException(this, "readExcelForXLSX", PretupsErrorCodesI.FILE_FORMAT_XLSX_INVALID,
					PretupsI.RESPONSE_FAIL, null);
		}
		return fileValueArray;
	}

	/**
	 * 
	 * @param filepathtemp
	 * @param headerRow		row number of header
	 * @return
	 * @throws IOException
	 * @throws BTSLBaseException 
	 */

	public  List<String> readExcelForXLSX(String filepathtemp, int headerRow ) throws IOException, BTSLBaseException {
		List<String> fileValueArray = new ArrayList<String>();
		XSSFWorkbook workbook = null;
		XSSFSheet excelsheet = null;
		String tempStr = "";
		int lastCellNum;
		DataFormatter formatter = null;
		try (FileInputStream file = new FileInputStream(filepathtemp)) {
			workbook = new XSSFWorkbook(file);
			excelsheet = workbook.getSheetAt(0);
			int rowcount = excelsheet.getLastRowNum();
			if(rowcount <= 0) {
				return fileValueArray;
			}
			Row firstDataRow = excelsheet.getRow(headerRow+1);
			lastCellNum = firstDataRow.getLastCellNum();
			int temp = this.rowNum;
			while (temp != (rowcount + 1)) {
				tempStr = "";
				for (int i = 0; i < lastCellNum; i++) { // NumberConstants.THREE.getIntValue()
					formatter = new DataFormatter();
					if(!BTSLUtil.isNullorEmpty(excelsheet.getRow(temp))) {
						tempStr = tempStr + formatter.formatCellValue(excelsheet.getRow(temp).getCell(i));
					}
					
					if (i < lastCellNum) {
						tempStr = tempStr + "#";
					}
				}
				temp++;
				fileValueArray.add(tempStr);
			}
		}catch(Exception e) {
			throw new BTSLBaseException(this, "readExcelForXLSX", PretupsErrorCodesI.FILE_FORMAT_XLSX_INVALID,
					PretupsI.RESPONSE_FAIL, null);
		}
		return fileValueArray;
	}
	
	/**
	 * 
	 * @param filepathtemp
	 * @param headerRow		row number of header
	 * @return
	 * @throws IOException
	 * @throws BTSLBaseException 
	 */

	public  List<String> readExcelForXLSXBatchOptInit(String filepathtemp, int headerRow ) throws IOException, BTSLBaseException {
		List<String> fileValueArray = new ArrayList<String>();
		XSSFWorkbook workbook = null;
		XSSFSheet excelsheet = null;
		String tempStr = "";
		int lastCellNum;
		DataFormatter formatter = null;
		try (FileInputStream file = new FileInputStream(filepathtemp)) {
			workbook = new XSSFWorkbook(file);
			excelsheet = workbook.getSheetAt(0);
			int rowcount = excelsheet.getLastRowNum();
			if(rowcount <= headerRow) {
				return fileValueArray;
			}
			Row firstDataRow = excelsheet.getRow(headerRow+1);
			lastCellNum = firstDataRow.getLastCellNum();
			int temp = this.rowNum;
			while (temp != (rowcount + 1)) {
				tempStr = "";
				for (int i = 0; i < lastCellNum; i++) { // NumberConstants.THREE.getIntValue()
					formatter = new DataFormatter();
					if(!BTSLUtil.isNullorEmpty(excelsheet.getRow(temp))) {
						tempStr = tempStr + formatter.formatCellValue(excelsheet.getRow(temp).getCell(i));
					}
					
					if (i < lastCellNum) {
						tempStr = tempStr + "#";
					}
				}
				temp++;
				fileValueArray.add(tempStr);
			}
		}catch(Exception e) {
			throw new BTSLBaseException(this, "readExcelForXLSX", PretupsErrorCodesI.FILE_FORMAT_XLSX_INVALID,
					PretupsI.RESPONSE_FAIL, null);
		}
		return fileValueArray;
	}
	
	/**
	 * Method readExcelForXLS
	 * 
	 * @param filepathtemp
	 * @return fileValueArray
	 * @throws IOException
	 * @throws BTSLBaseException 
	 */

	public  List<String> readExcelForXLS(String filepathtemp) throws IOException, BTSLBaseException {
		List<String> fileValueArray = new ArrayList<String>();
		HSSFWorkbook workbook = null;
		HSSFSheet excelsheet = null;
		String tempStr = "";
		int lastCellNum;
		DataFormatter formatter = null;
		try (FileInputStream file = new FileInputStream(filepathtemp)) {
			workbook = new HSSFWorkbook(file);
			excelsheet = workbook.getSheetAt(0);
			int rowcount = excelsheet.getLastRowNum();
			if(rowcount <= 0) {
				return fileValueArray;
			}
			Row firstDataRow = excelsheet.getRow(this.rowNum);
			lastCellNum = firstDataRow.getLastCellNum();
			int temp = this.rowNum;
			while (temp != (rowcount + 1)) {
				tempStr = "";
				for (int i = 0; i < lastCellNum; i++) { // NumberConstants.THREE.getIntValue()
					formatter = new DataFormatter();
					if(!BTSLUtil.isNullorEmpty(excelsheet.getRow(temp))) {
						tempStr = tempStr + formatter.formatCellValue(excelsheet.getRow(temp).getCell(i));
					}
					if (i < lastCellNum) {
						tempStr = tempStr + Constants.getProperty("BL_VOMS_FILE_SEPARATOR");;
					}
				}
				temp++;
				fileValueArray.add(tempStr);
			}
		}catch(Exception e) {
			throw new BTSLBaseException(this, "readExcelForXLS", PretupsErrorCodesI.FILE_FORMAT_XLS_INVALID,
					PretupsI.RESPONSE_FAIL, null);
		}
		return fileValueArray;
	}
	
	/**
	 * Method readExcelForXLS
	 * 
	 * @param filepathtemp
	 * @param headerRow  header row number
	 * @return fileValueArray
	 * @throws IOException
	 * @throws BTSLBaseException 
	 */

	public  List<String> readExcelForXLS(String filepathtemp, int headerRow) throws IOException, BTSLBaseException {
		List<String> fileValueArray = new ArrayList<String>();
		HSSFWorkbook workbook = null;
		HSSFSheet excelsheet = null;
		String tempStr = "";
		int lastCellNum;
		DataFormatter formatter = null;
		try (FileInputStream file = new FileInputStream(filepathtemp)) {
			workbook = new HSSFWorkbook(file);
			excelsheet = workbook.getSheetAt(0);
			int rowcount = excelsheet.getLastRowNum();
			if(rowcount <= 0) {
				return fileValueArray;
			}
			Row firstDataRow = excelsheet.getRow(this.rowNum);
			Row headerDataRow = excelsheet.getRow(headerRow + 1);
			lastCellNum = headerDataRow.getLastCellNum();
			int temp = this.rowNum;
			while (temp != (rowcount + 1)) {
				tempStr = "";
				for (int i = 0; i < lastCellNum; i++) { // NumberConstants.THREE.getIntValue()
					formatter = new DataFormatter();
					if(!BTSLUtil.isNullorEmpty(excelsheet.getRow(temp))) {
						tempStr = tempStr + formatter.formatCellValue(excelsheet.getRow(temp).getCell(i));
					}
					if (i < lastCellNum) {
						tempStr = tempStr + "#";
					}
				}
				temp++;
				fileValueArray.add(tempStr);
			}
		}catch(Exception e) {
			throw new BTSLBaseException(this, "readExcelForXLS", PretupsErrorCodesI.FILE_FORMAT_XLS_INVALID,
					PretupsI.RESPONSE_FAIL, null);
		}
		return fileValueArray;
	}
	
	/**
	 * Method readExcelForXLSBatchOptInit
	 * 
	 * @param filepathtemp
	 * @param headerRow  header row number
	 * @return fileValueArray
	 * @throws IOException
	 * @throws BTSLBaseException 
	 */

	public  List<String> readExcelForXLSBatchOptInit(String filepathtemp, int headerRow) throws IOException, BTSLBaseException {
		List<String> fileValueArray = new ArrayList<String>();
		HSSFWorkbook workbook = null;
		HSSFSheet excelsheet = null;
		String tempStr = "";
		int lastCellNum;
		DataFormatter formatter = null;
		try (FileInputStream file = new FileInputStream(filepathtemp)) {
			workbook = new HSSFWorkbook(file);
			excelsheet = workbook.getSheetAt(0);
			int rowcount = excelsheet.getLastRowNum();
			if(rowcount <= headerRow) {
				return fileValueArray;
			}
			Row firstDataRow = excelsheet.getRow(this.rowNum);
			Row headerDataRow = excelsheet.getRow(headerRow + 1);
			lastCellNum = headerDataRow.getLastCellNum();
			int temp = this.rowNum;
			while (temp != (rowcount + 1)) {
				tempStr = "";
				for (int i = 0; i < lastCellNum; i++) { // NumberConstants.THREE.getIntValue()
					formatter = new DataFormatter();
					if(!BTSLUtil.isNullorEmpty(excelsheet.getRow(temp))) {
						tempStr = tempStr + formatter.formatCellValue(excelsheet.getRow(temp).getCell(i));
					}
					if (i < lastCellNum) {
						tempStr = tempStr + "#";
					}
				}
				temp++;
				fileValueArray.add(tempStr);
			}
		}catch(Exception e) {
			throw new BTSLBaseException(this, "readExcelForXLS", PretupsErrorCodesI.FILE_FORMAT_XLS_INVALID,
					PretupsI.RESPONSE_FAIL, null);
		}
		return fileValueArray;
	}
	
	/**
	 * Method readFile
	 * 
	 * @param filePath
	 * @return fileValueArray
	 * @throws FileNotFoundException
	 * @throws IOException
	 */

	public List<String> readFile(String filePath) throws FileNotFoundException, IOException {
		List<String> fileValueArray = new ArrayList<String>();
		try (BufferedReader inFile = new BufferedReader(new java.io.FileReader(filePath))) {
			String fileData = null;
			int headerRow = this.rowNum;
			while ((fileData = inFile.readLine()) != null) {
				if (BTSLUtil.isNullorEmpty(fileData) || headerRow > 0) {
					log.debug("readFile", "Record Number" + 0 + "Not found/Not a header");
					headerRow -= 1;
					continue;
				}
				fileValueArray.add(fileData);
			}
		}
		return fileValueArray;
	}
	

	/**
	 * Validates the name of the file being uploaded
	 * 
	 * @param fileName
	 * @return boolean
	 * @throws BTSLBaseException 
	 */
	public void validateFileName(String fileName) throws BTSLBaseException {
		final String pattern = Constants.getProperty("FILE_NAME_WHITE_LIST");
		final Pattern r = Pattern.compile(pattern);
		final Matcher m = r.matcher(fileName);
		if (!m.find()) {
			throw new BTSLBaseException(this, "validateFileName", PretupsErrorCodesI.INVALID_FILE_NAME1,
					 PretupsI.RESPONSE_FAIL,null); 
		}
	}
	
    /**
     * 
     * @param fileType
     * @throws BTSLBaseException
     */
	public void validateFileType(String fileType) throws BTSLBaseException {
		//getting fileType Preference for validation
				String allowedContentType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOUCHER_UPLOAD_FILE_FORMATS);
				String[] allowedContentTypes = allowedContentType.split(",");

				if (allowedContentTypes.length == 0) {
					allowedContentTypes = new String[] { "xls", "xlsx", "csv" };
				}
				List<String> allowedFileTypelist = Arrays.asList(allowedContentTypes);
				
		if (!allowedFileTypelist.contains(fileType)) {
			throw new BTSLBaseException(this, "validateFileType", PretupsErrorCodesI.INVALID_FILE_FORMAT, PretupsI.RESPONSE_FAIL, null);
		}

	}
/*	
	
	*//**
	 * 
	 * @param fileRecord
	 * @param errorMap
	 * @param headerColumnLength
	 * @param loop
	 * @param headerRow
	 * @return
	 *//*
	 public boolean validateEachRow( List<String> fileHeaderList, String[] fileRecord, ErrorMap errorMap, int headerColumnLength, int loop, int headerRow) 
	 {
	    	boolean isValidRow = true;
	    	String message = null;
	    	String errorCode = null;
	    	RowErrorMsgLists rowErrorMsgListsObj = new RowErrorMsgLists();
	    	ArrayList<MasterErrorList> masterErrorLists = new ArrayList<MasterErrorList>();
	    	String[] args = new String[1];
	    	
	    	for(int col = 0; col< headerColumnLength; col++) 
	    	{   
	    		if(!BTSLUtil.isNullorEmpty(fileRecord[col])) 
	    		{
	    			if(Arrays.asList(PretupsI.VALIDATION_FOR_MSISDN).contains(fileHeaderList.get(col)) && !BTSLUtil.isValidMSISDN(fileRecord[col])) 
	    			{
	    				isValidRow = false;
    					errorCode = PretupsErrorCodesI.INVALID_FILE_DATA;
    					args[0] = fileHeaderList.get(col);
    					message = getErrorMessage( errorCode, args);
    					createFailureResponseForEachCol(masterErrorLists, message, errorCode);
	    			}else if(Arrays.asList(PretupsI.VALIDATION_FOR_AMOUNT).contains(fileHeaderList.get(col)) && !BTSLUtil.isValidAmount(fileRecord[col]))
	    			{
	    				isValidRow = false;
						errorCode = PretupsErrorCodesI.INVALID_FILE_DATA;
						message = getErrorMessage( errorCode, new String[] {fileHeaderList.get(col)} );
						createFailureResponseForEachCol(masterErrorLists, message, errorCode);
	    			}else if(Arrays.asList(PretupsI.VALID_NUMBER).contains(fileHeaderList.get(col)) 
	    					&& !(BTSLUtil.isValidNumber(fileRecord[col]) && ( (Integer.parseInt(fileRecord[col])) > 0 )) )
	    			{
	    				isValidRow = false;
						errorCode = PretupsErrorCodesI.INVALID_FILE_DATA;
						message = getErrorMessage( errorCode, new String[] {fileHeaderList.get(col)} );
						createFailureResponseForEachCol(masterErrorLists, message, errorCode);
	    			}
	    		}
	    		else 
	    		{
	    			isValidRow = false;
					errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
					message = getErrorMessage(errorCode, new String[] {fileHeaderList.get(col)});
					createFailureResponseForEachCol(masterErrorLists, message, errorCode);
	    			
	    		}
	    	}
	    	
			if (!isValidRow) 
			{
				// settign error
				int fileRowNumber = loop + headerRow + 1;
				rowErrorMsgListsObj.setRowName("Line " + fileRowNumber);
				rowErrorMsgListsObj.setRowValue(fileRecord[0]);
				rowErrorMsgListsObj.setMasterErrorList(masterErrorLists);
				if (errorMap.getRowErrorMsgLists() == null) {
					errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
				}
				(errorMap.getRowErrorMsgLists()).add(rowErrorMsgListsObj);
			}
			return isValidRow;
	    }
	 
	 
	 */
	 
	/**
	 * 
	 * @param masterErrorLists
	 * @param message
	 * @param errorCode
	 */
	private void createFailureResponseForEachCol(ArrayList<MasterErrorList> masterErrorLists, String message,
			String errorCode) {
		MasterErrorList masterErrorListObj = new MasterErrorList();

		masterErrorListObj.setErrorCode(errorCode);
		masterErrorListObj.setErrorMsg(message);
		masterErrorLists.add(masterErrorListObj);
		

	}
	private void createFailureResponseForEachCol1(int rowNum,O2CBatchWithdrawForm theForm,String fileRecord,ErrorMap errorMap,RowErrorMsgLists rowErrorMsgListsObj,ArrayList<MasterErrorList> masterErrorLists, String message,
			String errorCode) {
		MasterErrorList masterErrorListObj = new MasterErrorList();

		masterErrorListObj.setErrorCode(errorCode);
		masterErrorListObj.setErrorMsg(message);
		masterErrorLists.add(masterErrorListObj);
		

	rowErrorMsgListsObj.setRowName(!BTSLUtil.isNullString(theForm.getUserId())?theForm.getUserId():fileRecord);
	rowErrorMsgListsObj.setRowValue("Line " + rowNum);
	rowErrorMsgListsObj.setMasterErrorList(masterErrorLists);
	if (errorMap.getRowErrorMsgLists() == null) {
		errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
	}
	
	(errorMap.getRowErrorMsgLists()).add(rowErrorMsgListsObj);
	
	}
	
	private void createFailureResponseForEachCol1(int rowNum,FOCBatchForm theForm,String fileRecord,ErrorMap errorMap,RowErrorMsgLists rowErrorMsgListsObj,ArrayList<MasterErrorList> masterErrorLists, String message,
			String errorCode) {
		MasterErrorList masterErrorListObj = new MasterErrorList();

		masterErrorListObj.setErrorCode(errorCode);
		masterErrorListObj.setErrorMsg(message);
		masterErrorLists.add(masterErrorListObj);
		

	rowErrorMsgListsObj.setRowName(!BTSLUtil.isNullString(theForm.getUserId())?theForm.getUserId():fileRecord);
	rowErrorMsgListsObj.setRowValue("Line " + rowNum);
	rowErrorMsgListsObj.setMasterErrorList(masterErrorLists);
	if (errorMap.getRowErrorMsgLists() == null) {
		errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
	}
	
	(errorMap.getRowErrorMsgLists()).add(rowErrorMsgListsObj);
	
	}
	
	/**
	 * 
	 * @param errorCode
	 * @return
	 */
	public String getErrorMessage(String errorCode) {
		String message = RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), errorCode, null);
		return message;
	}

	/**
	 * 
	 * @param errorCode
	 * @param args
	 * @return
	 */
	public String getErrorMessage(String errorCode, String[] args) {
		String message = RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), errorCode, args);
		return message;
	}

	/**
	 * Method filedelete
	 */

	private void filedelete() {
		if(!BTSLUtil.isNullString(filepathtemp))
		{File file = new File(filepathtemp);
		if (file.delete()) {
			log.debug("filedelete", "******** Method uploadAndProcessFile :: Got exception and deleted the file");
		}
		}
	}
	
	/**
	 * Method file delete
	 * @param filePathtemp
	 */
	public void filedelete(String filepathtemp) {
		if(!BTSLUtil.isNullString(filepathtemp))
		{File file = new File(filepathtemp);
		if (file.delete()) {
			log.debug("filedelete", "******** Method uploadAndProcessFile :: Got exception and deleted the file");
		}
		}
	}
	
	/**
	 * 
	 * @param serviceKeyword
	 * @param headerLength
	 * @return
	 */
	private boolean validateFileHeaderLength( String serviceKeyword, int headerLength) {
		boolean isValidLength = true;
		if ( PretupsI.SERVICE_TYPE_DVDBULK.equalsIgnoreCase(serviceKeyword)  ) 
		{
			return headerLength == PretupsI.DVD_BULK_FILE_HEADER_SIZE;
		} else if ( PretupsI.SERVICE_TYPE_CHNL_RECHARGE.equalsIgnoreCase(serviceKeyword)  )
		{
			return headerLength == 4;
		} else if( PretupsI.SERVICE_TYPE_CHANNEL_GIFT_RECHARGE.equalsIgnoreCase(serviceKeyword)  ) 
		{
			return headerLength == 7;
		} else if( PretupsI.SERVICE_TYPE_CHNL_RECHARGE_INTR.equalsIgnoreCase(serviceKeyword)  ) 
		{
			return headerLength == 4;
		}
		
		return isValidLength;
	}
	
	
	/**
	 * 
	 * @param fileRecord
	 * @param fileRow
	 * @param errorMap
	 * @param serviceKeyword
	 * @return
	 */
	public boolean validateEachRow( String[] fileRecord, int fileRow, ErrorMap errorMap, String serviceKeyword ) 
	 {
		boolean isValidRow = true;
    	
		if( PretupsI.SERVICE_TYPE_DVDBULK.equalsIgnoreCase(serviceKeyword)  ) 
		{
			isValidRow = validateDvdBulkFileData( fileRecord, fileRow, errorMap );
		} else if ( PretupsI.SERVICE_TYPE_CHNL_RECHARGE.equalsIgnoreCase(serviceKeyword) ||  PretupsI.SERVICE_TYPE_EVD.equalsIgnoreCase(serviceKeyword)) 
		{
			if(PretupsI.SERVICE_TYPE_EVD.equalsIgnoreCase(serviceKeyword) && (fileRecord!=null && fileRecord.length >NumberConstants.EIGHT.getIntValue() ) ) {
				isValidRow = 	validateBulkPrepaidRcEVD(fileRecord, fileRow, errorMap );
			}else{
				isValidRow = validateBulkPrepaidRc( fileRecord, fileRow, errorMap );
			}
			
			
			
		} else if( PretupsI.SERVICE_TYPE_CHANNEL_GIFT_RECHARGE.equalsIgnoreCase(serviceKeyword)  ) 
		{
			isValidRow = validateBulkGiftRc( fileRecord, fileRow, errorMap );
		} else if( PretupsI.SERVICE_TYPE_CHNL_RECHARGE_INTR.equalsIgnoreCase(serviceKeyword)  ) 
		{
			isValidRow = validateBulkIntrRc( fileRecord, fileRow, errorMap );
		}
		else if(PretupsI.BATCH_OPT_USR_MODIFICATION_SERVICE.equalsIgnoreCase(serviceKeyword)) {
			isValidRow = validateBulkModifyChannelUser(fileRecord,fileRow,errorMap);
		}
		 
		return isValidRow;
		 
	 }
	public boolean validateEachRowO2C( String[] fileRecord, int fileRow, ErrorMap errorMap, String serviceKeyword,int headerColumnLength,boolean externalCodeMandatory,String externalsTxnMandatory,ArrayList batchItemsList ) 
	 {
		boolean isValidRow = true;
   	
		if( "o2cBatchWithdraw".equalsIgnoreCase(serviceKeyword)  ) 
		{
			isValidRow = validateO2CBatch( fileRecord, fileRow, errorMap,headerColumnLength,externalCodeMandatory, externalsTxnMandatory,batchItemsList);
		} 
		else if( "o2cBatchTrf".equalsIgnoreCase(serviceKeyword)  ) 
		{
			isValidRow = validateO2CBatchTrf(fileRecord, fileRow, errorMap,headerColumnLength,externalCodeMandatory, externalsTxnMandatory,batchItemsList);
		}
		
		return isValidRow;
		 
	 }
	
	
	/**
     * Method: uploadAndReadGenericFile
     * This method read file of XLS, XLSX and CSV format.
     * Check for basic client side validation for each col and store error in errorMap.
     * Store all valid data in map with header as key and column data as a list.
     * 
     * @param fileDetailsMap
     * @param headerRow
     * @param errorMap
     * @return
     * @throws BTSLBaseException
     */
	public LinkedHashMap<String, List<String>> uploadAndReadGenericFileO2CBatchApproval(HashMap<String, String> fileDetailsMap, int headerRow, ErrorMap errorMap,HashMap closedOrderMap,HashMap approveRejectMap,String externalsTxnMandatory, ArrayList batchItemsList,Map map,int rowInduced,O2CBatchWithdrawForm theForm) throws BTSLBaseException {

		String methodName = "uploadAndReadGenericFileO2CBatchApproval";
		if (log.isDebugEnabled()) {
			LogFactory.printLog(methodName, "Entered", log);
			log.debug(methodName, "fileDetailsMap " + fileDetailsMap + " headerRow" + headerRow );
		}
		this.fileDetailsMap = fileDetailsMap;
		
		//validate fileDetailsMap data
		// fileType, fileName, fileAttachment and serviceKeyword is mandatory
		validateFileDetailsMap(headerRow);  // throws exception
		String approveReqYesOrNoOrBlank=null;
	   
		this.base64val = fileDetailsMap.get(PretupsI.FILE_ATTACHMENT);
		this.requestFileName = fileDetailsMap.get(PretupsI.FILE_NAME);
		LinkedHashMap<String, List<String>> responseMap = new LinkedHashMap<String, List<String>>();
		filePathCons = Constants.getProperty("O2C_BATCH_APPROVAL_FILE_PATH");
		validateFilePathCons(filePathCons);
		
		String filePathConstemp = filePathCons + "temp/";        // C:/apache-tomcat-8.0.52/logs/batcho2c/upload/temp/
		createDirectory(filePathConstemp);
		

		setFileNameWithExtention();
		
		filepathtemp = filePathConstemp + fileNamewithextention;   // C:/apache-tomcat-8.0.321/logs/BatchC2CUpload/temp/c2cBatchTransfer.xlsx

		byte[] base64Bytes = decodeFile(base64val);
		log.debug("filepathtemp:", filepathtemp);
		log.debug("base64Bytes:", base64Bytes);
		
		String fileSize = Constants.getProperty("VOMS_MAX_FILE_LENGTH");
		validateFileSize(fileSize, base64Bytes);  // throws exception
		
		writeByteArrayToFile(filepathtemp, base64Bytes);
		try 
		{
            List<String> fileHeaderList = new ArrayList<String>();
			String fileValueSeparator = Constants.getProperty("BL_VOMS_FILE_SEPARATOR");
			if (BTSLUtil.isNullorEmpty(fileValueSeparator)) 
			{
				log.error(methodName, "fileValueSeparator is null in Constants.props");
				 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_SEPERATOR_IN_CONSTANTS,
				 PretupsI.RESPONSE_FAIL,null); 
			}
	
			List<String> fileValueArray = readuploadedfile();
			if( fileValueArray.size() == 0) {
				log.error(methodName, "Uploaded file is empty");
				 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_FILE,
				 PretupsI.RESPONSE_FAIL,null); 
			}

			String[] fileRecord = null;
			int headerColumnLength = 0;
			int fileRow = headerRow;
			int validRowCount = 0;
			String serviceKeyword = fileDetailsMap.get(PretupsI.SERVICE_KEYWORD);
			uniqueMsisdnWithDenom = new HashSet<String>();
			boolean isValidRow=false;
			try 
			{
				int discardCounts = 0;
	            int cancelCounts=0;
	            int blankCount=0;
	            
				for (int loop = 0; loop < fileValueArray.size(); loop++) 
				{  
					String fileData1 = null;
					fileData1 = (String) fileValueArray.get(loop);
					fileRecord = fileData1.split(fileValueSeparator, -1); // fileRecord reads will contain one extra value, and last value will always be empty
					
					if(loop == 0) 
					{
						headerColumnLength =(PretupsI.FILE_CONTENT_TYPE_CSV.equalsIgnoreCase( 
								fileDetailsMap.get(PretupsI.FILE_TYPE1))) ? fileRecord.length : fileRecord.length - 1 ;
						
						if( !validateFileHeaderLength( serviceKeyword, headerColumnLength ) ) {
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_FILE_HEADINGS,
									PretupsI.RESPONSE_FAIL, null); 
						}
						
						for(int col= 0; col < headerColumnLength; col++) 
						{
							responseMap.put(fileRecord[col].trim(), new ArrayList<String>());
							fileHeaderList.add(fileRecord[col].trim());
						}
					} else 
					{   
						fileRow++; 

				        this.rowNum = fileRow +1;
						//isValidRow = true;
						String message = null;
						String errorCode = null;
						RowErrorMsgLists rowErrorMsgListsObj = new RowErrorMsgLists();
						ArrayList<MasterErrorList> masterErrorLists = new ArrayList<MasterErrorList>();
						Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
						int col = 0;
						
						//O2CBatchItemsVO batchItemsVO = (O2CBatchItemsVO) map.get(fileRecord[0]);  // Made this change due to class case Exception.
						BatchO2CItemsVO  batchItemsVO = (BatchO2CItemsVO) map.get(fileRecord[0]);
						
						if (!BTSLUtil.isNullorEmpty(fileRecord[0]) ) 
						{
							if (batchItemsVO==null) 
							{
								isValidRow = true;
								errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
								message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
								createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
								continue;
							}
							
						} else 
						{
							isValidRow = true;
							errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
							message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Detail ID blank" });
							createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
							
		                        blankCount++;
		                        continue;
						}
							if (!batchItemsVO.getMsisdn().equals(fileRecord[1]))
							{
								isValidRow = true;
								
								errorCode = PretupsErrorCodesI.INVALID_MSISDN_USER_TRANSFER;//errorCode = PretupsErrorCodesI.INVALID_LOGIN_ID;
								message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Msisdn" });
								createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
								
								continue;
							}
							if (!BTSLUtil.isNullString(fileRecord[17 - rowInduced]) && !("Y".equalsIgnoreCase(fileRecord[17 - rowInduced])) && !("N"
				                    .equalsIgnoreCase(fileRecord[17 - rowInduced])) && !("D".equalsIgnoreCase(fileRecord[17 - rowInduced]))) 
							{
								isValidRow = true;
								errorCode = PretupsErrorCodesI.ERROR_INVALID_REQUESTINTTYPE;
								message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Request Action" });
								createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
								
								continue;
							}
							if(BTSLUtil.isNullString(fileRecord[17-rowInduced])||fileRecord[17-rowInduced].equalsIgnoreCase("D"))        	//||arr[i][17-indexToBeReduced].equalsIgnoreCase("N") 
				        	{
				            discardCounts++;
				            continue;
				        }
					
				    long reqQuantity=0;
				    try {
				        // check required quantity is numeric or not
				        reqQuantity = PretupsBL.getSystemAmount(fileRecord[6]);
				    } catch (Exception e) {
				    	isValidRow = true;
						errorCode = PretupsErrorCodesI.QUANTITY_NOT_NUMERIC;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Requested Quantity" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
						
						continue;
				    }
				    // check required qty is same or not
				    if (!(batchItemsVO.getRequestedQuantity() == reqQuantity)) {
				    	isValidRow = true;
						errorCode = PretupsErrorCodesI.INVALID_REQ_QUANTITY;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Requested Quantity" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
						
						continue;
				    }
				    // If external txn number is mandatory then check if it is in
				    // file or not
				    if (PretupsI.YES.equals(externalsTxnMandatory)) {
				        // check external txn number is null or not
				        if (BTSLUtil.isNullString(fileRecord[7])) {
				        	isValidRow = true;
				    		errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
				    		message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "External TXN Mandatory" });
				    		createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
							
				    		continue;
				        }
				        // check external txn date
				        if (BTSLUtil.isNullString(fileRecord[8])) {
				        	isValidRow = true;
				    		errorCode = PretupsErrorCodesI.LMS_INVALID_DATE;
				    		message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "External TXN Date" });
				    		createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
							
				    		continue;
				        }
				    }
				    if (!BTSLUtil.isNullString(fileRecord[7])) {
				        // check external txn number for numeric
				        if (fileRecord[7].length() > 20) {
				        	isValidRow = true;
				    		errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
				    		message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "External TXN Mandatory" });
				    		createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
							
				    		continue;
				        }
				        // check external txn number for numeric
				        if (SystemPreferences.EXTERNAL_TXN_NUMERIC) {
				            if (!BTSLUtil.isNumeric(fileRecord[7])) {
				            	isValidRow = true;
				        		errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
				        		message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "External TXN Numeric" });
				        		createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
								
				        		continue;
				            }
				        }
				        // check external txn is unique within the system
				        if (SystemPreferences.EXTERNAL_TXN_UNIQUE) {
				            if (externalTxnNumber != null && externalTxnNumber.contains(fileRecord[7])) {
				            	isValidRow = true;
				        		errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
				        		message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "External TXN Unique" });
				        		createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
								
				        		continue;
				            }
				        }
				        batchItemsVO.setExtTxnNo(fileRecord[7]);
				        externalTxnNumber.add(batchItemsVO.getExtTxnNo());
				    }
				    // Check the external txn date is valid or not
				    if (!BTSLUtil.isNullString(fileRecord[8])) {
				        try {
				            Date date = new Date(fileRecord[8]);
				            BTSLUtil.getDateStringFromDate(date);
				            batchItemsVO.setExtTxnDate(date);
				        } catch (Exception pex) {
				        	isValidRow = true;
				    		errorCode = PretupsErrorCodesI.LMS_INVALID_DATE;
				    		message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "External TXN Date is invalid" });
				    		createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
							
				    		continue;
				        }
				    }
						if (!isValidRow) 
						{
							batchItemsVO.setRecordNumber(rowNum);
							// setting error
							// If file validation not exists then only construct the
				            // batchItemsVO and add map for processing
				                if (!BTSLUtil.isNullString(batchItemsVO.getExtTxnNo())) {
				                    externalTxnNumber.add(batchItemsVO.getExtTxnNo());
				                }
				                if ("approval1".equals(String.valueOf(theForm.getRequestType()))) {
				                    // Check the remarks is null or not
				                    if (fileRecord[18 - rowInduced] != null) {
				                        if (fileRecord[18 - rowInduced].length() > 100) {
				                            batchItemsVO.setFirstApproverRemarks(fileRecord[18 - rowInduced].substring(0, 100));
				                        } else {
				                            batchItemsVO.setFirstApproverRemarks(fileRecord[18 - rowInduced]);
				                        }
				                    }
				                    approveReqYesOrNoOrBlank=fileRecord[17 - rowInduced];
				                    if ("N".equalsIgnoreCase(fileRecord[17 - rowInduced])) {
				                        batchItemsVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
				                        cancelCounts++;
				                    } else if ("Y".equalsIgnoreCase(fileRecord[17 - rowInduced])) {
				                        // If request is to approve the order and in system
				                        // preference approvel level
				                        // is <=1 the close the order otherwise approve at
				                        // level 1
				                        if (theForm.getO2cOrderApprovalLevel() <= 1) {
				                            batchItemsVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
				                        } else {
				                            batchItemsVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
				                        }
				                    }
				                    batchItemsVO.setFirstApprovedBy("userVO.getUserID()");
				                    batchItemsVO.setFirstApprovedOn(new Date());
				                } else if ("approval2".equals(String.valueOf(theForm.getRequestType()))) {
				                    // Check the remarks is null or not
				                    if (fileRecord[18 - rowInduced] != null) {
				                        if (fileRecord[18 - rowInduced].length() > 100) {
				                            batchItemsVO.setSecondApproverRemarks(fileRecord[18 - rowInduced].substring(0, 100));
				                        } else {
				                            batchItemsVO.setSecondApproverRemarks(fileRecord[18 - rowInduced]);
				                        }
				                    }
				                    approveReqYesOrNoOrBlank=fileRecord[17 - rowInduced];
				                    if ("N".equalsIgnoreCase(fileRecord[17 - rowInduced])) {
				                        batchItemsVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
				                    } else if ("Y".equalsIgnoreCase(fileRecord[17 - rowInduced])) {
				                        batchItemsVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
				                    }
				                    batchItemsVO.setSecondApprovedBy("userVO.getUserID()");
				                    batchItemsVO.setSecondApprovedOn(new Date());
				                } else if ("approval3".equals(String.valueOf(theForm.getRequestType()))) {
				                    // Check the remarks is null or not
				                    if (fileRecord[18 - rowInduced] != null) {
				                        if (fileRecord[18 - rowInduced].length() > 100) {
				                            batchItemsVO.setThirdApproverRemarks(fileRecord[18 - rowInduced].substring(0, 100));
				                        } else {
				                            batchItemsVO.setThirdApproverRemarks(fileRecord[18 - rowInduced]);
				                        }
				                    }
				                    approveReqYesOrNoOrBlank=fileRecord[17 - rowInduced];
				                    if ("N".equalsIgnoreCase(fileRecord[17 - rowInduced])) {
				                        batchItemsVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
				                        cancelCounts++;
				                    } else if ("Y".equalsIgnoreCase(fileRecord[17 - rowInduced])) {
				                        batchItemsVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
				                    }
				                }

				                // If status is closed then contruct map for closing
				                // otherwise one map will be there
				                if (batchItemsVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE)) {
				                	if(!BTSLUtil.isNullString(approveReqYesOrNoOrBlank)) { // if blank , ignore this record for processing.
				                		closedOrderMap.put(batchItemsVO.getBatchDetailId(), batchItemsVO);
				                	}
				                } else {
				                	if(!BTSLUtil.isNullString(approveReqYesOrNoOrBlank)) { // if blank , ignore this record for processing.
				                		approveRejectMap.put(batchItemsVO.getBatchDetailId(), batchItemsVO);
				                	}
				                }
						}
						}
					}
				

				if( isFileWritten && isValidRow ) {
					filedelete();
				}
				ArrayList<String>filepathtemp1= new ArrayList<String>();
				filepathtemp1.add(filepathtemp);
				responseMap.put("filepathtemp", filepathtemp1);
				LinkedList<String>valid= new LinkedList<String>();
				String isValidRow1 = null;
				if(isValidRow)
					isValidRow1 = "true";
				else
					isValidRow1 = "false";
				valid.add(isValidRow1);
				valid.add(String.valueOf(blankCount));
				valid.add(String.valueOf(cancelCounts));
				valid.add(String.valueOf(discardCounts));
				valid.add(String.valueOf(rowNum));
				responseMap.put("isValidRow", valid);
				return responseMap;
					
			} catch (ArrayIndexOutOfBoundsException e) {
				if(isFileWritten) {
					filedelete();
				}
				
				log.error(methodName, "Exceptin:e=" + e);
				log.errorTrace("uploadAndProcessFile", e);
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ARRAY_INDEX_OUT_OF_BOUND_EXCEPTION,
						PretupsI.RESPONSE_FAIL, null);

			}

		} catch (IOException io) {
			if(isFileWritten) {
				filedelete();
			}
			log.error(methodName, "Exceptin:e=" + io);
			log.errorTrace("uploadAndProcessFile", io);
			io.printStackTrace();
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT,
					PretupsI.RESPONSE_FAIL, null);

		} catch(Exception e) {
			if(isFileWritten) {
				filedelete();
			}
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace("uploadAndProcessFile", e);
			if(!BTSLUtil.isNullorEmpty(e.getMessage())) {
				throw new BTSLBaseException(this, methodName, e.getMessage(),
						PretupsI.RESPONSE_FAIL, null);
			}else {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION,
						PretupsI.RESPONSE_FAIL, null);
			}
			
		}
		finally {
			if (log.isDebugEnabled()) {
				LogFactory.printLog(methodName, "Exited", log);
				log.debug(methodName, "responseMap " + responseMap );
			}
		}
	}
	
	
	
	/**
	 * Method: uploadAndReadGenericFile
     * This method read file of XLS, XLSX and CSV format.
     * Check for basic client side validation for each col and store error in errorMap.
     * Store all valid data in map with header as key and column data as a list.
     * 
     * 
     * 
	 * @param fileDetailsMap
	 * @param headerRow
	 * @param errorMap
	 * @param closedOrderMap
	 * @param approveRejectMap
	 * @param externalsTxnMandatory
	 * @param batchItemsList
	 * @param map
	 * @param rowInduced
	 * @param theForm
	 * @return
	 * @throws BTSLBaseException
	 */
	public LinkedHashMap<String, List<String>> uploadAndReadGenericFileBulkComProcessApproval(HashMap<String, String> fileDetailsMap, int headerRow, ErrorMap errorMap,HashMap closedOrderMap,HashMap approveRejectMap, ArrayList batchItemsList,Map map,int rowInduced,FOCBatchForm theForm) throws BTSLBaseException {

		String methodName = "uploadAndReadGenericFileBulkComProcessApproval";
		if (log.isDebugEnabled()) {
			LogFactory.printLog(methodName, "Entered", log);
			log.debug(methodName, "fileDetailsMap " + fileDetailsMap + " headerRow" + headerRow );
		}
		this.fileDetailsMap = fileDetailsMap;
		
		//validate fileDetailsMap data
		// fileType, fileName, fileAttachment and serviceKeyword is mandatory
		validateFileDetailsMap(headerRow);  // throws exception
	   
		this.base64val = fileDetailsMap.get(PretupsI.FILE_ATTACHMENT);
		this.requestFileName = fileDetailsMap.get(PretupsI.FILE_NAME);
		LinkedHashMap<String, List<String>> responseMap = new LinkedHashMap<String, List<String>>();
		filePathCons = Constants.getProperty("DP_BATCH_APPROVAL_FILE_PATH");
		validateFilePathCons(filePathCons);
		
		String filePathConstemp = filePathCons + "temp/";        // C:/apache-tomcat-8.0.321/logs/BulkComissionPayout/UploadApprove/temp/
		createDirectory(filePathConstemp);
		

		setFileNameWithExtention();
		
		filepathtemp = filePathConstemp + fileNamewithextention;   // C:/apache-tomcat-8.0.321/logs/BulkComissionPayout/UploadApprove/temp/bulkCommission.xlsx

		byte[] base64Bytes = decodeFile(base64val);
		log.debug("filepathtemp:", filepathtemp);
		log.debug("base64Bytes:", base64Bytes);
		
		String fileSize = Constants.getProperty("DP_BATCH_APPROVAL_FILE_SIZE");
		validateFileSize(fileSize, base64Bytes);  // throws exception
		
		writeByteArrayToFile(filepathtemp, base64Bytes);
		try 
		{
            List<String> fileHeaderList = new ArrayList<String>();
			String fileValueSeparator = Constants.getProperty("BL_VOMS_FILE_SEPARATOR");
			if (BTSLUtil.isNullorEmpty(fileValueSeparator)) 
			{
				log.error(methodName, "fileValueSeparator is null in Constants.props");
				 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_SEPERATOR_IN_CONSTANTS,
				 PretupsI.RESPONSE_FAIL,null); 
			}
	
			List<String> fileValueArray = readuploadedfile();
			if( fileValueArray.size() == 0) {
				log.error(methodName, "Uploaded file is empty");
				 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_FILE,
				 PretupsI.RESPONSE_FAIL,null); 
			}

			String[] fileRecord = null;
			int headerColumnLength = 0;
			int fileRow = headerRow;
			int validRowCount = 0;
			String serviceKeyword = fileDetailsMap.get(PretupsI.SERVICE_KEYWORD);
			uniqueMsisdnWithDenom = new HashSet<String>();
			boolean isValidRow=false;
			try 
			{
				/*
	             * Expected vaules at different places of array are as:
	             * arr[all index][0]=Batch details id
	             * arr[all index][1]=MSISDN
	             * arr[all index][2]=user category
	             * arr[all index][3]=user grade
	             * arr[all index][4]=user login id
	             * arr[all index][5]=batch id
	             * arr[all index][6]=qty
	             * arr[all index][7]=external txn number
	             * arr[all index][8]=external txn date
	             * arr[all index][9]=External code
	             * arr[all index][10]=Bonus Type
	             * arr[all index][11]=initiated by
	             * arr[all index][12]=initiated on
	             * arr[all index][13]=level 1 approved by
	             * arr[all index][14]=level 1 approved on
	             * arr[all index][15]=level 2 approved by
	             * arr[all index][16]=level 2 approved on
	             * arr[all index][17] 0r [15]=current status
	             * arr[all index][18] or [16]=Required action
	             * arr[all index][19] or [17]=Remarks
	             */
				
				int discardCounts = 0;
	            int cancelCounts=0;
	            int blankCount=0;
	            
				for (int loop = 0; loop < fileValueArray.size(); loop++) 
				{  
					String fileData1 = null;
					fileData1 = (String) fileValueArray.get(loop);
					fileRecord = fileData1.split(fileValueSeparator, -1); // fileRecord reads will contain one extra value, and last value will always be empty
					
					if(loop == 0) 
					{
						headerColumnLength =(PretupsI.FILE_CONTENT_TYPE_CSV.equalsIgnoreCase( 
								fileDetailsMap.get(PretupsI.FILE_TYPE1))) ? fileRecord.length : fileRecord.length - 1 ;
						
						if( !validateFileHeaderLength( serviceKeyword, headerColumnLength ) ) {
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_FILE_HEADINGS,
									PretupsI.RESPONSE_FAIL, null); 
						}
						
						for(int col= 0; col < headerColumnLength; col++) 
						{
							responseMap.put(fileRecord[col].trim(), new ArrayList<String>());
							fileHeaderList.add(fileRecord[col].trim());
						}
					} 
					else {   
						fileRow++; 

				        this.rowNum = fileRow +1;
						//isValidRow = true;
						String message = null;
						String errorCode = null;
						RowErrorMsgLists rowErrorMsgListsObj = new RowErrorMsgLists();
						ArrayList<MasterErrorList> masterErrorLists = new ArrayList<MasterErrorList>();
						Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
						int col = 0;
						
						FOCBatchItemsVO focBatchItemVO = (FOCBatchItemsVO) map.get(fileRecord[0]);
						
						if (!BTSLUtil.isNullorEmpty(fileRecord[0]) ) 
						{
							if (focBatchItemVO==null) 
							{
								isValidRow = true;
								errorCode = PretupsErrorCodesI.BATCH_DETAIL_NO_NOT_FOUND;
								message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
								createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
								continue;
							}
							
						} else 
						{
							isValidRow = true;
							errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
							message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Detail ID blank" });
							createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
							
		                        blankCount++;
		                        continue;
						}
							if (!focBatchItemVO.getMsisdn().equals(fileRecord[1]))
							{
								isValidRow = true;
								errorCode = PretupsErrorCodesI.EXT_USRADD_INVALID_MSISDN;
								message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Login Id" });
								createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
								
								continue;
							}
							if (!BTSLUtil.isNullString(fileRecord[18 - rowInduced]) && !("Y".equalsIgnoreCase(fileRecord[18 - rowInduced])) && !("N"
				                    .equalsIgnoreCase(fileRecord[18 - rowInduced])) && !("D".equalsIgnoreCase(fileRecord[18 - rowInduced]))) 
							{
								isValidRow = true;
								errorCode = PretupsErrorCodesI.ERROR_INVALID_REQUESTINTTYPE;
								message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Request Action" });
								createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
								
								continue;
							}
							if(BTSLUtil.isNullString(fileRecord[18-rowInduced])||fileRecord[18-rowInduced].equalsIgnoreCase("D"))        	//||arr[i][17-indexToBeReduced].equalsIgnoreCase("N") 
				        	{
								discardCounts++;
								continue;
				        	}
					if (!focBatchItemVO.getBatchId().equals(fileRecord[5])) {
						isValidRow = true;
						errorCode = PretupsErrorCodesI.BATCH_ID_NOT_FOUND;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
						continue;
				    }
				    long reqQuantity=0;
				    try {
				        // check required quantity is numeric or not
				        reqQuantity = PretupsBL.getSystemAmount(fileRecord[6]);
				    } catch (Exception e) {
				    	isValidRow = true;
						errorCode = PretupsErrorCodesI.QUANTITY_NOT_NUMERIC;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Requested Quantity" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
						
						continue;
				    }
				    // check required qty is same or not
				    if (!(focBatchItemVO.getRequestedQuantity() == reqQuantity)) {
				    	isValidRow = true;
						errorCode = PretupsErrorCodesI.INVALID_QUANTITY;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Requested Quantity" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
						
						continue;
				    }
				    // If external txn number is mandatory then check if it is in
				    // file or not
				    if (PretupsI.YES.equals(theForm.getExternalTxnMandatory())) {
				        // check external txn number is null or not
				        if (BTSLUtil.isNullString(fileRecord[7])) {
				        	isValidRow = true;
				    		errorCode = PretupsErrorCodesI.ERROR_EXT_TXN_NO_BLANK;
				    		message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "External TXN Mandatory" });
				    		createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
							
				    		continue;
				        }
				        // check external txn date
				        if (BTSLUtil.isNullString(fileRecord[8])) {
				        	isValidRow = true;
				    		errorCode = PretupsErrorCodesI.ERROR_EXT_DATE_BLANK;
				    		message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "External TXN Date" });
				    		createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
							
				    		continue;
				        }
				    }
				    if (!BTSLUtil.isNullString(fileRecord[7])) {
				        // check external txn number for numeric
				        if (fileRecord[7].length() > 20) {
				        	isValidRow = true;
				    		errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
				    		message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "External TXN Mandatory" });
				    		createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
							
				    		continue;
				        }
				        // check external txn number for numeric
				        if (SystemPreferences.EXTERNAL_TXN_NUMERIC) {
				            if (!BTSLUtil.isNumeric(fileRecord[7])) {
				            	isValidRow = true;
				        		errorCode = PretupsErrorCodesI.EXTSYS_NOT_NUMERIC;
				        		message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "External TXN Numeric" });
				        		createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
								
				        		continue;
				            }
				        }
				        // check external txn is unique within the system
				        if (SystemPreferences.EXTERNAL_TXN_UNIQUE) {
				            if (externalTxnNumber != null && externalTxnNumber.contains(fileRecord[7])) {
				            	isValidRow = true;
				        		errorCode = PretupsErrorCodesI.ERROR_EXT_TXN_NO_NOT_UNIQUE;
				        		message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "External TXN Unique" });
				        		createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
								
				        		continue;
				            }
				        }
				        focBatchItemVO.setExtTxnNo(fileRecord[7]);
				        externalTxnNumber.add(focBatchItemVO.getExtTxnNo());
				    }
				    // Check the external txn date is valid or not
				    if (!BTSLUtil.isNullString(fileRecord[8])) {
				        try {
				            Date date = new Date(fileRecord[8]);
				            BTSLUtil.getDateStringFromDate(date);
							focBatchItemVO.setExtTxnDate(BTSLDateUtil.getGregorianDate(fileRecord[8]));
				        } catch (Exception pex) {
				        	isValidRow = true;
				    		errorCode = PretupsErrorCodesI.LMS_INVALID_DATE;
				    		message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "External TXN Date is invalid" });
				    		createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
							
				    		continue;
				        }
				    }
				    
				    if (focBatchItemVO.getBonusType() != null && BTSLUtil.isNullString(fileRecord[10])) {
				    	isValidRow = true;
						errorCode = PretupsErrorCodesI.BLANK_BONUS_TYPE;
						
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Bonus Type blank" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
						continue;
	                }
					if (focBatchItemVO.getBonusType() != null && !(focBatchItemVO.getBonusType().equals(fileRecord[10]))){
						isValidRow = true;
						errorCode = PretupsErrorCodesI.INCORRECT_BONUS_TYPE;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[]{PretupsErrorCodesI.INCORRECT_BONUS_TYPE});
						createFailureResponseForEachCol1(rowNum, theForm, fileRecord[0], errorMap, rowErrorMsgListsObj, masterErrorLists, message, errorCode);
						continue;

					}
	                
						if (!isValidRow) 
						{
							focBatchItemVO.setRecordNumber(this.rowNum);
							// setting error
							// If file validation not exists then only construct the
				            // batchItemsVO and add map for processing
				                if (!BTSLUtil.isNullString(focBatchItemVO.getExtTxnNo())) {
				                    externalTxnNumber.add(focBatchItemVO.getExtTxnNo());
				                }
				                if ("approval1".equals(String.valueOf(theForm.getRequestType()))) {
				                    // Check the remarks is null or not
				                    if (fileRecord[19 - rowInduced] != null) {
				                        if (fileRecord[19 - rowInduced].length() > 100) {
				                        	focBatchItemVO.setFirstApproverRemarks(fileRecord[19 - rowInduced].substring(0, 100));
				                        } else {
				                        	focBatchItemVO.setFirstApproverRemarks(fileRecord[19 - rowInduced]);
				                        }
				                    }
				                    if ("N".equalsIgnoreCase(fileRecord[18 - rowInduced])) {
				                    	focBatchItemVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
				                        cancelCounts++;
				                    } else if ("Y".equalsIgnoreCase(fileRecord[18 - rowInduced])) {
				                        // If request is to approve the order and in system
				                        // preference approvel level
				                        // is <=1 the close the order otherwise approve at
				                        // level 1
				                        if (theForm.getFocOrderApprovalLevel() <= 1) {
				                        	focBatchItemVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
				                        } else {
				                        	focBatchItemVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
				                        }
				                    }
//				                    focBatchItemVO.setFirstApprovedBy("userVO.getUserID()");
//				                    focBatchItemVO.setFirstApprovedOn(new Date());
				                } else if ("approval2".equals(String.valueOf(theForm.getRequestType()))) {
				                    // Check the remarks is null or not
				                    if (fileRecord[19 - rowInduced] != null) {
				                        if (fileRecord[19 - rowInduced].length() > 100) {
				                        	focBatchItemVO.setSecondApproverRemarks(fileRecord[19 - rowInduced].substring(0, 100));
				                        } else {
				                        	focBatchItemVO.setSecondApproverRemarks(fileRecord[19 - rowInduced]);
				                        }
				                    }
				                    if ("N".equalsIgnoreCase(fileRecord[18 - rowInduced])) {
				                    	focBatchItemVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
				                    	cancelCounts++;
				                    } else if ("Y".equalsIgnoreCase(fileRecord[18 - rowInduced])) {
				                    	 if (theForm.getFocOrderApprovalLevel() <= 2) {
					                        	focBatchItemVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
					                     } else {
					                        	focBatchItemVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
					                     }
				                    }
//				                    focBatchItemVO.setSecondApprovedBy("userVO.getUserID()");
//				                    focBatchItemVO.setSecondApprovedOn(new Date());
				                } else if ("approval3".equals(String.valueOf(theForm.getRequestType()))) {
				                    // Check the remarks is null or not
				                    if (fileRecord[19 - rowInduced] != null) {
				                        if (fileRecord[19 - rowInduced].length() > 100) {
				                        	focBatchItemVO.setThirdApproverRemarks(fileRecord[18 - rowInduced].substring(0, 100));
				                        } else {
				                        	focBatchItemVO.setThirdApproverRemarks(fileRecord[18 - rowInduced]);
				                        }
				                    }
				                    if ("N".equalsIgnoreCase(fileRecord[18 - rowInduced])) {
				                    	focBatchItemVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
				                        cancelCounts++;
				                    } else if ("Y".equalsIgnoreCase(fileRecord[18 - rowInduced])) {
				                    	focBatchItemVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
				                    }
				                }

				                // If status is closed then contruct map for closing
				                // otherwise one map will be there
				                if (focBatchItemVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE)) {
				                    closedOrderMap.put(focBatchItemVO.getBatchDetailId(), focBatchItemVO);
				                } else {
				                    approveRejectMap.put(focBatchItemVO.getBatchDetailId(), focBatchItemVO);
				                }
						}
						}
					}
				

				if( isFileWritten && isValidRow ) {
					filedelete();
				}
				ArrayList<String>filepathtemp1= new ArrayList<String>();
				filepathtemp1.add(filepathtemp);
				responseMap.put("filepathtemp", filepathtemp1);
				LinkedList<String>valid= new LinkedList<String>();
				String isValidRow1 = null;
				if(isValidRow)
					isValidRow1 = "true";
				else
					isValidRow1 = "false";
				valid.add(isValidRow1);
				valid.add(String.valueOf(blankCount));
				valid.add(String.valueOf(cancelCounts));
				valid.add(String.valueOf(discardCounts));
				valid.add(String.valueOf(this.rowNum));
				responseMap.put("isValidRow", valid);
				return responseMap;
					
			} catch (ArrayIndexOutOfBoundsException e) {
				if(isFileWritten) {
					filedelete();
				}
				
				log.error(methodName, "Exceptin:e=" + e);
				log.errorTrace("uploadAndProcessFile", e);
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ARRAY_INDEX_OUT_OF_BOUND_EXCEPTION,
						PretupsI.RESPONSE_FAIL, null);

			}

		} catch (IOException io) {
			if(isFileWritten) {
				filedelete();
			}
			log.error(methodName, "Exceptin:e=" + io);
			log.errorTrace("uploadAndProcessFile", io);
			io.printStackTrace();
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT,
					PretupsI.RESPONSE_FAIL, null);

		} catch(Exception e) {
			if(isFileWritten) {
				filedelete();
			}
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace("uploadAndProcessFile", e);
			if(!BTSLUtil.isNullorEmpty(e.getMessage())) {
				throw new BTSLBaseException(this, methodName, e.getMessage(),
						PretupsI.RESPONSE_FAIL, null);
			}else {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION,
						PretupsI.RESPONSE_FAIL, null);
			}
			
		}
		finally {
			if (log.isDebugEnabled()) {
				LogFactory.printLog(methodName, "Exited", log);
				log.debug(methodName, "responseMap " + responseMap );
			}
		}
	}
	
	public boolean validateEachRowFOC( String[] fileRecord, int fileRow, ErrorMap errorMap, String serviceKeyword,int headerColumnLength,boolean externalCodeMandatory,String externalsTxnMandatory,ArrayList batchItemsList,ArrayList bonusList,Connection con ) 
	 {
		boolean isValidRow = true;
  	
		 if("DPBATCHTRF".equalsIgnoreCase(serviceKeyword) || "FOCBATCHTRF".equalsIgnoreCase(serviceKeyword) ) {
			isValidRow = validateFOCBatchTrf( fileRecord, fileRow, errorMap,headerColumnLength,externalCodeMandatory, externalsTxnMandatory,batchItemsList,serviceKeyword,bonusList,con);
		}
		
		return isValidRow;
		 
	 }
	
	/**
	 * 
	 * @param fileRecord
	 * @param fileRow
	 * @param errorMap
	 * @return
	 */
	public boolean  validateDvdBulkFileData( String[] fileRecord, int fileRow, ErrorMap errorMap ) 
	{
        int rowNum = fileRow +1;
		boolean isValidRow = true;
		String message = null;
		String errorCode = null;
		RowErrorMsgLists rowErrorMsgListsObj = new RowErrorMsgLists();
		ArrayList<MasterErrorList> masterErrorLists = new ArrayList<MasterErrorList>();
		Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
		int col = 0;

		if (!BTSLUtil.isNullorEmpty(fileRecord[0]) ) 
		{
			if (!BTSLUtil.isValidMSISDN(fileRecord[0])) 
			{
				isValidRow = false;
				errorCode = PretupsErrorCodesI.INVALID_SUBSCRIBER_MSISDN;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Subscriber's MSISIDN" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			}
			
		} else 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Subscriber's MSISIDN" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}
		col++;

		if (BTSLUtil.isNullorEmpty(fileRecord[1])) 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Voucher Type Code" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}
		col++;

		if (BTSLUtil.isNullorEmpty(fileRecord[2])) 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Voucher Segment Code" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}
		col++;

		if (!BTSLUtil.isNullorEmpty(fileRecord[3]) ) 
		{
			if ( !BTSLUtil.isValidAmount(fileRecord[3]) ) 
			{
				isValidRow = false;
				errorCode = PretupsErrorCodesI.INVALID_FILE_DATA;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Voucher Denomination" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			} else if( Double.parseDouble( fileRecord[3]) == 0 ) //Enter only in non-alphanumeric case 
			{
				isValidRow = false;
				errorCode = PretupsErrorCodesI.INVALID_NEGATIVE_AMOUNT;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Voucher Denomination" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			}
			
		} else 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Voucher Denomination" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}
		col++;

		if (BTSLUtil.isNullorEmpty(fileRecord[col])) 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Voucher Profile ID" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}

		if (!BTSLUtil.isNullorEmpty(fileRecord[5]) ) {
			if (!BTSLUtil.isValidNumber(fileRecord[5]) ) 
			{
				isValidRow = false;
				errorCode = PretupsErrorCodesI.INVALID_FILE_DATA;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Number of Vouchers" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			} else if( Double.parseDouble( fileRecord[5]) == 0 ) //Enter only in non-alphanumeric case
			{
				isValidRow = false;
				errorCode = PretupsErrorCodesI.INVALID_NEGATIVE_AMOUNT;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Number of Vouchers" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			}
			
		} else {
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Number of Vouchers" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}
		
		// repeatative msisdn + denomination not allowed
		String msisdnAdnDenomination = fileRecord[0]+ "*" + fileRecord[3];
		if(isValidRow &&  !uniqueMsisdnWithDenom.add(msisdnAdnDenomination) ) {
			isValidRow = false;
			errorCode = PretupsErrorCodesI.DUPLICATE_ROW;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { String.valueOf(rowNum) });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}

		if (!isValidRow) 
		{
			// settign error
			rowErrorMsgListsObj.setRowName("Line " + rowNum);
			rowErrorMsgListsObj.setRowValue(fileRecord[0]);
			rowErrorMsgListsObj.setMasterErrorList(masterErrorLists);
			if (errorMap.getRowErrorMsgLists() == null) {
				errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
			}
			(errorMap.getRowErrorMsgLists()).add(rowErrorMsgListsObj);
		}

		return isValidRow;
	}
	/**
	 * 
	 * @param fileRecord
	 * @param fileRow
	 * @param errorMap
	 * @return
	 */
	public boolean  validateO2CBatch( String[] fileRecord, int fileRow, ErrorMap errorMap,int headerColumnLength,boolean externalCodeMandatory,String externalsTxnMandatory,ArrayList batchItemsList ) 
	{
        int rowNum = fileRow +1;
		boolean isValidRow = true;
		String message = null;
		String errorCode = null;
		RowErrorMsgLists rowErrorMsgListsObj = new RowErrorMsgLists();
		ArrayList<MasterErrorList> masterErrorLists = new ArrayList<MasterErrorList>();
		Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
		int col = 0;
		String loginId=null,externalTxnNum,externalTxnDate=null,externalTxnCode,quantity,remarks,userCat=null,userGrade=null;
		Date extDate=null;
		long reqQuantity = 0;
		if (headerColumnLength == 9) {
            loginId = fileRecord[1];
            userCat = fileRecord[2];
            userGrade = fileRecord[3];
            externalTxnNum = fileRecord[4];
            externalTxnDate = BTSLDateUtil.getGregorianDateInString(fileRecord[5]);
            externalTxnCode = fileRecord[6];
            quantity = fileRecord[7];
            remarks = fileRecord[8];
        } else {
            externalTxnNum = fileRecord[1];
            if(!BTSLUtil.isNullString(fileRecord[2]))
            {
            	externalTxnDate = BTSLDateUtil.getGregorianDateInString(fileRecord[2]);
            }
            externalTxnCode = fileRecord[3];
            quantity = fileRecord[4];
            remarks = fileRecord[5];
        }
		if (headerColumnLength == 9) {
		if (BTSLUtil.isNullorEmpty(userCat)) 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.USER_CATEGORY_INVALID;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "User category" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}
		if (BTSLUtil.isNullorEmpty(userGrade)) 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_REQ_USR_GRADE_INVALID;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "User Grade" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}
		}
		if(BTSLUtil.isNullString(loginId)&&BTSLUtil.isNullString(fileRecord[0]))
		{
        	isValidRow = false;
			errorCode = PretupsErrorCodesI.INVALID_RECIEVER_CREDENTIALS;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Atleast enter one of MSISDN or LOGINID" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			col++;
		}
            if (BTSLUtil.isNullString(externalTxnNum)&&PretupsI.YES.equals(externalsTxnMandatory)) {
            	isValidRow = false;
				errorCode = PretupsErrorCodesI.INVALID_SUBSCRIBER_MSISDN;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Ext txn num missing" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
				col++;
            }
            else
            {
            	 if (!BTSLUtil.isNullString(externalTxnNum) && externalTxnNum.length() > 20) {
            		 isValidRow = false;
     				errorCode = PretupsErrorCodesI.INVALID_SUBSCRIBER_MSISDN;
     				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Ext txn num size greater than 20" });
     				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
                 }
            	 if (!BTSLUtil.isNullString(externalTxnNum)) {
                     if (SystemPreferences.EXTERNAL_TXN_NUMERIC) {
                         if (!BTSLUtil.isNumeric(externalTxnNum)) {
                        	 isValidRow = false;
              				errorCode = PretupsErrorCodesI.INVALID_SUBSCRIBER_MSISDN;
              				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Ext txn num not numeric" });
              				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
                         }
                     }
                     if (SystemPreferences.EXTERNAL_TXN_UNIQUE) {
                         if (externalTxnNumber != null && externalTxnNumber.contains(externalTxnNum)) {
                        	 isValidRow = false;
               				errorCode = PretupsErrorCodesI.INVALID_SUBSCRIBER_MSISDN;
               				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Ext txn num not unique" });
               				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
                         }
                     }
                     externalTxnNumber.add(externalTxnNum);
                 }
            	 col++;
            }
            
            if (BTSLUtil.isNullString(externalTxnDate)&&PretupsI.YES.equals(externalsTxnMandatory)) {
            	isValidRow = false;
				errorCode = PretupsErrorCodesI.ERROR_EXT_DATE_NOT_PROPER;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Ext txn date mising" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
				col++;
            }
            else
            {
            	 
                 if (!BTSLUtil.isNullString(externalTxnDate)) {
                     try {
                         // This is used to validate the date in XLS file. If
                         // we enter the
                         // date in 01/01/06 then it will be treated as
                         // 01/01/2006 in XLS
                         // file so we have to create a new date object and
                         // validate it.
                         if(externalTxnDate.length()==7) {
                        	 externalTxnDate="0"+externalTxnDate;
                         }
                    	 
                         extDate = BTSLUtil.getDateFromDateString(externalTxnDate,"MM/dd/yy");
                         if(extDate.after(new Date()))
							{
                        	 isValidRow = false;
             				errorCode = PretupsErrorCodesI.ERROR_EXT_DATE_NOT_PROPER;
             				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Ext txn date invalid" });
             				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
             				col++;
							}
                     } catch (Exception ex) {
                    	 isValidRow = false;
         				errorCode = PretupsErrorCodesI.ERROR_EXT_DATE_NOT_PROPER;
         				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Ext txn date invalid" });
         				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
         				col++;
                     }
                 }
            }
            
         // check external txn code
           
                if (BTSLUtil.isNullString(externalTxnCode)&&externalCodeMandatory) {
                	isValidRow = false;
     				errorCode = PretupsErrorCodesI.ERROR_ERP_CHNL_USER_NEW_EXTERNAL_CODE_MISSING;
     				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { externalTxnCode });
     				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
     				col++;
                }
                else
                {
                	 if ((!BTSLUtil.isNullString(externalTxnCode) && externalTxnCode.length() > 20) || externalTxnCode.contains(",")) {
                		 isValidRow = false;
          				errorCode = PretupsErrorCodesI.ERROR_ERP_CHNL_USER_NEW_EXTERNAL_CODE_INVALID;
          				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { externalTxnCode });
          				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
          				col++;
                     }
                }

           
		

             // check quantity related validations here
                if (!BTSLUtil.isNullString(quantity)) {
                    try {
                        if (!BTSLUtil.isDecimalValue(quantity)) {
                        	isValidRow = false;
              				errorCode = PretupsErrorCodesI.REQUESTED_QUANTITY_IS_NOT_PROPER;
              				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { quantity });
              				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
              				col++;
                        }

                        else if (!BTSLUtil.isNumeric(quantity)) {
                            final int length = quantity.length();
                            final int index = quantity.indexOf(".");
                            if (index != -1 && length > index + 3) {
                            	isValidRow = false;
                  				errorCode = PretupsErrorCodesI.REQUESTED_QUANTITY_IS_NOT_PROPER;
                  				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { quantity });
                  				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
                  				col++;
                            }
                        }

                        reqQuantity = PretupsBL.getSystemAmount(quantity);
                        if (reqQuantity <= 0) {
                        	isValidRow = false;
              				errorCode = PretupsErrorCodesI.REQUESTED_QUANTITY_IS_NOT_PROPER;
              				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { quantity });
              				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
              				col++;
                        }

                    } catch (Exception e) {
                    	isValidRow = false;
          				errorCode = PretupsErrorCodesI.REQUESTED_QUANTITY_IS_NOT_PROPER;
          				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { quantity });
          				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
          				col++;
                    }
                } else {
                	isValidRow = false;
      				errorCode = PretupsErrorCodesI.REQUESTED_QUANTITY_IS_NOT_PROPER;
      				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { quantity });
      				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
      				col++;
                }

                if (!BTSLUtil.isNullString(remarks)) {
                    if (remarks.length() > 100) {
                        remarks = remarks.substring(0, 100);
                    }

                }

		if (!isValidRow) 
		{
			// settign error
			rowErrorMsgListsObj.setRowName(!BTSLUtil.isNullString(loginId)?loginId:fileRecord[0]);
			rowErrorMsgListsObj.setRowValue("Line " + rowNum);
			rowErrorMsgListsObj.setMasterErrorList(masterErrorLists);
			if (errorMap.getRowErrorMsgLists() == null) {
				errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
			}
			(errorMap.getRowErrorMsgLists()).add(rowErrorMsgListsObj);
		}else
		{
			O2CBatchItemsVO batchItemsVO = new O2CBatchItemsVO();

             batchItemsVO.setRecordNumber(fileRow);
             batchItemsVO.setMsisdn(fileRecord[0]);
             batchItemsVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
             batchItemsVO.setModifiedOn(new Date());
             batchItemsVO.setLoginId(loginId);
             batchItemsVO.setExtTxnNo(externalTxnNum);
             batchItemsVO.setExtTxnDate(extDate);
             batchItemsVO.setTransferDate(new Date());
             batchItemsVO.setRequestedQuantity(reqQuantity);
             batchItemsVO.setInitiatorRemarks(remarks);
             batchItemsVO.setExternalCode(externalTxnCode);

             batchItemsList.add(batchItemsVO);
		}

		return isValidRow;
	}
	
	/**
	 * 
	 * @param fileRecord
	 * @param fileRow
	 * @param errorMap
	 * @return
	 */
	public boolean  validateBulkPrepaidRc( String[] fileRecord, int fileRow, ErrorMap errorMap ) 
	{

		boolean isValidRow = true;
		String message = null;
		String errorCode = null;
		RowErrorMsgLists rowErrorMsgListsObj = new RowErrorMsgLists();
		ArrayList<MasterErrorList> masterErrorLists = new ArrayList<MasterErrorList>();
		Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));

		if (!BTSLUtil.isNullorEmpty(fileRecord[0]) ) 
		{
			if (!BTSLUtil.isValidMSISDN(fileRecord[0])) 
			{
				isValidRow = false;
				errorCode = PretupsErrorCodesI.INVALID_SUBSCRIBER_MSISDN;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Mobile number" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			}
			
		} else 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Mobile number" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}

		if (BTSLUtil.isNullorEmpty(fileRecord[1])) 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Sub-service" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}

		if (!BTSLUtil.isNullorEmpty(fileRecord[2]) ) 
		{
			if ( !BTSLUtil.isValidAmount(fileRecord[2]) ) 
			{
				isValidRow = false;
				errorCode = PretupsErrorCodesI.INVALID_FILE_DATA;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Requested amount" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			} else if( Double.parseDouble( fileRecord[2]) == 0 ) //Enter only in non-alphanumeric case 
			{
				isValidRow = false;
				errorCode = PretupsErrorCodesI.INVALID_NEGATIVE_AMOUNT;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Requested amount" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			}
			
		} else 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Requested amount" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}

		if (BTSLUtil.isNullorEmpty(fileRecord[3])) 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Language code" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}

		if (!isValidRow) 
		{
			// settign error
			rowErrorMsgListsObj.setRowValue(fileRecord[0]);
			rowErrorMsgListsObj.setRowName("Line " + (fileRow+1));
			rowErrorMsgListsObj.setMasterErrorList(masterErrorLists);
			if (errorMap.getRowErrorMsgLists() == null) {
				errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
			}
			(errorMap.getRowErrorMsgLists()).add(rowErrorMsgListsObj);
		}
		

		return isValidRow;
	}

	/**
	 * 
	 * @param fileRecord
	 * @param fileRow
	 * @param errorMap
	 * @return
	 */
	public boolean  validateBulkPrepaidRcEVD( String[] fileRecord, int fileRow, ErrorMap errorMap ) 
	{

		boolean isValidRow = true;
		String message = null;
		String errorCode = null;
		RowErrorMsgLists rowErrorMsgListsObj = new RowErrorMsgLists();
		ArrayList<MasterErrorList> masterErrorLists = new ArrayList<MasterErrorList>();
		Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));

		if (!BTSLUtil.isNullorEmpty(fileRecord[0]) ) 
		{
			if (!BTSLUtil.isValidMSISDN(fileRecord[0])) 
			{
				isValidRow = false;
				errorCode = PretupsErrorCodesI.INVALID_SUBSCRIBER_MSISDN;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Mobile number" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			}
			
		} else 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Mobile number" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}

		if (BTSLUtil.isNullorEmpty(fileRecord[7])) 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Sub-service" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}

		if (!BTSLUtil.isNullorEmpty(fileRecord[8]) ) 
		{
			if ( !BTSLUtil.isValidAmount(fileRecord[8]) ) 
			{
				isValidRow = false;
				errorCode = PretupsErrorCodesI.INVALID_VOUCHER_AMOUNT;
				message = RestAPIStringParser.getMessage(locale, errorCode, null);
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			} else if( Double.parseDouble( fileRecord[8]) == 0 ) //Enter only in non-alphanumeric case 
			{
				isValidRow = false;
				errorCode = PretupsErrorCodesI.INVALID_NEGATIVE_AMOUNT;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Requested amount" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			}
			
		} else 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Requested amount" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}


		if (!isValidRow) 
		{
			// setting error
			rowErrorMsgListsObj.setRowName(PretupsI.LINE + (fileRow+1));
			rowErrorMsgListsObj.setRowValue(fileRecord[0]);
			rowErrorMsgListsObj.setMasterErrorList(masterErrorLists);
			if (errorMap.getRowErrorMsgLists() == null) {
				errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
			}
			(errorMap.getRowErrorMsgLists()).add(rowErrorMsgListsObj);
		}
		

		return isValidRow;
	}

	
	
	
	/**
	 * 
	 * @param fileRecord
	 * @param fileRow
	 * @param errorMap
	 * @return
	 */
	public boolean  validateBulkGiftRc( String[] fileRecord, int fileRow, ErrorMap errorMap ) 
	{

		boolean isValidRow = true;
		String message = null;
		String errorCode = null;
		RowErrorMsgLists rowErrorMsgListsObj = new RowErrorMsgLists();
		ArrayList<MasterErrorList> masterErrorLists = new ArrayList<MasterErrorList>();
		Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));

		if (!BTSLUtil.isNullorEmpty(fileRecord[0]) ) 
		{
			if (!BTSLUtil.isValidMSISDN(fileRecord[0])) 
			{
				isValidRow = false;
				errorCode = PretupsErrorCodesI.INVALID_SUBSCRIBER_MSISDN;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Mobile number" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			}
			
		} else 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Mobile number" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}

		if (BTSLUtil.isNullorEmpty(fileRecord[1])) 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Sub-service" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}

		if (!BTSLUtil.isNullorEmpty(fileRecord[2]) ) 
		{
			if ( !BTSLUtil.isValidAmount(fileRecord[2]) ) 
			{
				isValidRow = false;
				errorCode = PretupsErrorCodesI.INVALID_FILE_DATA;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Requested amount" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			} else if( Double.parseDouble( fileRecord[2]) == 0 ) //Enter only in non-alphanumeric case 
			{
				isValidRow = false;
				errorCode = PretupsErrorCodesI.INVALID_NEGATIVE_AMOUNT;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Requested amount" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			}
			
		} else 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Requested amount" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}

		if (BTSLUtil.isNullorEmpty(fileRecord[3])) 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Receiver language code" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}
		
		if (!BTSLUtil.isNullorEmpty(fileRecord[4]) ) 
		{
			if (!BTSLUtil.isValidMSISDN(fileRecord[4])) 
			{
				isValidRow = false;
				errorCode = PretupsErrorCodesI.INVALID_SUBSCRIBER_MSISDN;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Gifter mobile number" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			}
			
		} else 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Gifter mobile number" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}
		
		if (!BTSLUtil.isNullorEmpty(fileRecord[5]) ) 
		{
			if (!BTSLUtil.isValidName(fileRecord[5])) 
			{
				isValidRow = false;
				errorCode = "restrictedsubs.scheduletopupdetails.errorfile.msg.invalidgiftername";
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { fileRecord[5] });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			}
			
		} else 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Gifter name" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}
		
		if (BTSLUtil.isNullorEmpty(fileRecord[6])) 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Gifter language code" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}

		if (!isValidRow) 
		{
			// settign error
			rowErrorMsgListsObj.setRowName("Line " + fileRow);
			rowErrorMsgListsObj.setRowValue(fileRecord[0]);
			rowErrorMsgListsObj.setMasterErrorList(masterErrorLists);
			if (errorMap.getRowErrorMsgLists() == null) {
				errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
			}
			(errorMap.getRowErrorMsgLists()).add(rowErrorMsgListsObj);
		}

		return isValidRow;
	}
	
	/**
	 * 
	 * @param fileRecord
	 * @param fileRow
	 * @param errorMap
	 * @return
	 */
	public boolean  validateBulkIntrRc( String[] fileRecord, int fileRow, ErrorMap errorMap ) 
	{

		boolean isValidRow = true;
		String message = null;
		String errorCode = null;
		RowErrorMsgLists rowErrorMsgListsObj = new RowErrorMsgLists();
		ArrayList<MasterErrorList> masterErrorLists = new ArrayList<MasterErrorList>();
		Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));

		if (!BTSLUtil.isNullorEmpty(fileRecord[0]) ) 
		{
			if (!BTSLUtil.isValidMSISDN(fileRecord[0])) 
			{
				isValidRow = false;
				errorCode = PretupsErrorCodesI.INVALID_SUBSCRIBER_MSISDN;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Mobile number" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			}
			
		} else 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Mobile number" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}

		if (BTSLUtil.isNullorEmpty(fileRecord[1])) 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Sub-service" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}

		if (!BTSLUtil.isNullorEmpty(fileRecord[2]) ) 
		{
			if ( !BTSLUtil.isValidAmount(fileRecord[2]) ) 
			{
				isValidRow = false;
				errorCode = PretupsErrorCodesI.INVALID_FILE_DATA;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Requested amount" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			} else if( Double.parseDouble( fileRecord[2]) == 0 ) //Enter only in non-alphanumeric case 
			{
				isValidRow = false;
				errorCode = PretupsErrorCodesI.INVALID_NEGATIVE_AMOUNT;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Requested amount" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			}
			
		} else 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Requested amount" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}
		
		if (!BTSLUtil.isNullorEmpty(fileRecord[3]) ) 
		{
			if (!BTSLUtil.isValidMSISDN(fileRecord[3])) 
			{
				isValidRow = false;
				errorCode = PretupsErrorCodesI.INVALID_SUBSCRIBER_MSISDN;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Notification MSISDN" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			}
			
		} else 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Notification MSISDN" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}

		if (!isValidRow) 
		{
			// settign error
			rowErrorMsgListsObj.setRowName("Line " + fileRow);
			rowErrorMsgListsObj.setRowValue(fileRecord[0]);
			rowErrorMsgListsObj.setMasterErrorList(masterErrorLists);
			if (errorMap.getRowErrorMsgLists() == null) {
				errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
			}
			(errorMap.getRowErrorMsgLists()).add(rowErrorMsgListsObj);
		}

		return isValidRow;
	}
	
	
	/**
	 * 
	 * @param fileRecord
	 * @param fileRow
	 * @param errorMap
	 * @return
	 */
	public boolean  validateO2CBatchTrf( String[] fileRecord, int fileRow, ErrorMap errorMap,int headerColumnLength,boolean externalCodeMandatory,String externalsTxnMandatory,ArrayList batchItemsList ) 
	{
        int rowNum = fileRow +1;
		boolean isValidRow = true;
		String message = null;
		String errorCode = null;
		RowErrorMsgLists rowErrorMsgListsObj = new RowErrorMsgLists();
		ArrayList<MasterErrorList> masterErrorLists = new ArrayList<MasterErrorList>();
		Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
		int col = 0;
		String loginId=null,userCat = null,userGrade= null,externalTxnNum,externalTxnDate,externalTxnCode,quantity,remarks,paymentType;
		Date extDate=null;
		long reqQuantity = 0;
		if (headerColumnLength == 10) {
            loginId = fileRecord[1];
            userCat = fileRecord[2];
            userGrade = fileRecord[3];
            externalTxnNum = fileRecord[4];
            paymentType = fileRecord[5];
            externalTxnDate = BTSLDateUtil.getGregorianDateInString(fileRecord[6]);
            externalTxnCode = fileRecord[7];
            quantity = fileRecord[8];
            remarks = fileRecord[9];
        } else {
            externalTxnNum = fileRecord[1];
            paymentType = fileRecord[3];
            externalTxnDate = BTSLDateUtil.getGregorianDateInString(fileRecord[2]);
            externalTxnCode = fileRecord[4];
            quantity = fileRecord[5];
            remarks = fileRecord[6];
        }
		if (!BTSLUtil.isNullorEmpty(fileRecord[0]) ) 
		{
			if (!BTSLUtil.isValidMSISDN(fileRecord[0])) 
			{
				isValidRow = false;
				errorCode = PretupsErrorCodesI.INVALID_SUBSCRIBER_MSISDN;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Mobile number" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			}
			
		} else 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Mobile number" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}
		if (headerColumnLength == 10) {
			if (BTSLUtil.isNullorEmpty(loginId)) 
			{
				isValidRow = false;
				errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Login Id" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			}
			if (BTSLUtil.isNullorEmpty(userCat)) 
			{
				isValidRow = false;
				errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "User category" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			}
			if (BTSLUtil.isNullorEmpty(userGrade)) 
			{
				isValidRow = false;
				errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "User Grade" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			}
		}
		
		 if (BTSLUtil.isNullString(paymentType)) {
			 isValidRow = false;
				errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Payment Type" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
         }else{
		   // load payment type arraylist
         final ArrayList paymentTypeList = LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_INSTRUMENT_TYPE, true);
         // check that the payment type from request should be
         // present in the payment type list from the lookup cache
         ListValueVO listValueVO = null;
         boolean pmtTypeExist = false;
         if (!BTSLUtil.isNullString(paymentType)) {
             for (int i = 0, k = paymentTypeList.size(); i < k; i++) {
                 listValueVO = (ListValueVO) paymentTypeList.get(i);
             	if(PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equals(listValueVO.getValue())) {
             		paymentTypeList.remove(i);
             		continue;
             	}
                 if (paymentType.equals(listValueVO.getValue())) {
                     pmtTypeExist = true;
                     break;
                 }
             }
             if (!pmtTypeExist) {
                isValidRow = false;
 				errorCode = "batcho2c.processuploadedfile.error.paymenttype";
 				message = PretupsRestUtil.getMessageString(errorCode);
 				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
          
             }
         }
         }
        if (PretupsI.YES.equals(externalsTxnMandatory)) {
			if (!PretupsI.PAYMENT_INSTRUMENT_TYPE_CASH.equals(paymentType)) {
                if (BTSLUtil.isNullString(externalTxnNum)) {
                	isValidRow = false;
        			errorCode = "batchfoc.processuploadedfile.error.exttxnmissing";
        			message = PretupsRestUtil.getMessageString(errorCode);
        			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
        			col++;
                }
            }
        }
        else{
        	 if (!BTSLUtil.isNullString(externalTxnNum) && externalTxnNum.length() > 20) {
        		 isValidRow = false;
 				errorCode = "batcho2c.processuploadedfile.error.exttxnmaxsize";
 				message = PretupsRestUtil.getMessageString(errorCode);
				message = message.replaceAll("\\[0\\]", "20");
 				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
             }
        	 if (!BTSLUtil.isNullString(externalTxnNum)) {
                 if (SystemPreferences.EXTERNAL_TXN_NUMERIC) {
                     if (!BTSLUtil.isNumeric(externalTxnNum)) {
                    	 isValidRow = false;
          				errorCode = "batcho2c.processuploadedfile.error.exttxnnumnumeric";
         				message = PretupsRestUtil.getMessageString(errorCode);
          				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
                     }
                 }
                 if (SystemPreferences.EXTERNAL_TXN_UNIQUE) {
                     if (externalTxnNumber != null && externalTxnNumber.contains(externalTxnNum)) {
                    	 isValidRow = false;
           				errorCode = "batcho2c.processuploadedfile.error.exttxnnumunique";
         				message = PretupsRestUtil.getMessageString(errorCode);
           				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
                     }
                 }
                 externalTxnNumber.add(externalTxnNum);
             }
        	 col++;
        }
        if (BTSLUtil.isNullString(externalTxnDate)&&PretupsI.YES.equals(externalsTxnMandatory)) {
        	isValidRow = false;
			errorCode = "batchfoc.processuploadedfile.error.exttxndatemissing";
			message = PretupsRestUtil.getMessageString(errorCode);
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			col++;
        }
        else
        {
         if (!BTSLUtil.isNullString(externalTxnDate)) {
             try {
                 // This is used to validate the date in XLS file. If
                 // we enter the
                 // date in 01/01/06 then it will be treated as
                 // 01/01/2006 in XLS
                 // file so we have to create a new date object and
                 // validate it.
                 if(externalTxnDate.length()==7) {
                	 externalTxnDate="0"+externalTxnDate;
                 }
                 extDate = BTSLUtil.getDateFromDateString(externalTxnDate,PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT).toString());
                 if(extDate.after(new Date()))
					{
                	 isValidRow = false;
     				errorCode = "batchfoc.processuploadedfile.error.exttxndateinvalid";
     				message = PretupsRestUtil.getMessageString(errorCode);
     				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
     				col++;
					}
             } catch (Exception ex) {
            	isValidRow = false;
            	errorCode = "batchfoc.processuploadedfile.error.exttxndateinvalid";
  				message = PretupsRestUtil.getMessageString(errorCode);
  				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
 				col++;
             }
         }
        }
            
         // check external txn code
   
        if (BTSLUtil.isNullString(externalTxnCode)&&externalCodeMandatory) {
        	isValidRow = false;
			errorCode = "batchfoc.processuploadedfile.error.externalcodeisreq";
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Ext Code" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			col++;
        }
        else
        {
        	 if (!BTSLUtil.isNullString(externalTxnCode) && externalTxnCode.length() > 25) {
        		 isValidRow = false;
  				errorCode = "batcho2c.processuploadedfile.error.externalcodemaxsize";
 				message = PretupsRestUtil.getMessageString(errorCode);
				message = message.replaceAll("\\[0\\]", "20");
  				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
  				col++;
             }
        }

         // check quantity related validations here
            if (!BTSLUtil.isNullString(quantity)) {
                try {
                    if (!BTSLUtil.isNumeric(quantity) && quantity.contains(".")) {
                        final int length = quantity.length();
                        final int index = quantity.indexOf(".");
                        if (length > index + 3) {
                            isValidRow = false;
              				errorCode = "batcho2c.processuploadedfile.error.upto2DecimalOnly";
             				message = PretupsRestUtil.getMessageString(errorCode);
              				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
              				col++;
                        }
                    }

                    reqQuantity = PretupsBL.getSystemAmount(quantity);
                    if (reqQuantity < 0) {
                    	isValidRow = false;
          				errorCode = "batcho2c.processuploadedfile.error.qtynonnumeric";
         				message = PretupsRestUtil.getMessageString(errorCode);
          				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
          				col++;
                    }

                } catch (Exception e) {
                	isValidRow = false;
      				errorCode = "batcho2c.processuploadedfile.error.qtynumeric";
     				message = PretupsRestUtil.getMessageString(errorCode);
      				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
      				col++;
                }
            } else {
            	isValidRow = false;
  				errorCode = "batcho2c.processuploadedfile.error.qtyreq";
 				message = PretupsRestUtil.getMessageString(errorCode);
  				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
  				col++;
            }

            if (!BTSLUtil.isNullString(remarks)) {
                if (remarks.length() > 100) {
                    remarks = remarks.substring(0, 100);
                }

            }

		if (!isValidRow) 
		{
			// setting error
			rowErrorMsgListsObj.setRowName(!BTSLUtil.isNullString(loginId)?loginId:fileRecord[0]);
			rowErrorMsgListsObj.setRowValue("Line " + rowNum);
			rowErrorMsgListsObj.setMasterErrorList(masterErrorLists);
			if (errorMap.getRowErrorMsgLists() == null) {
				errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
			}
			(errorMap.getRowErrorMsgLists()).add(rowErrorMsgListsObj);
		}else
		{
			BatchO2CItemsVO batchItemsVO = new BatchO2CItemsVO();
             batchItemsVO.setRecordNumber(fileRow);
             batchItemsVO.setMsisdn(fileRecord[0]);
             batchItemsVO.setPaymentType(paymentType);
             batchItemsVO.setUserCategory(userCat);
             batchItemsVO.setGradeName(userGrade);
             batchItemsVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
             batchItemsVO.setModifiedOn(new Date());
             batchItemsVO.setLoginID(loginId);
             batchItemsVO.setExtTxnNo(externalTxnNum);
             batchItemsVO.setExtTxnDate(extDate);
             batchItemsVO.setTransferDate(new Date());
             batchItemsVO.setRequestedQuantity(reqQuantity);
             batchItemsVO.setInitiatorRemarks(remarks);
             batchItemsVO.setExternalCode(externalTxnCode);
             batchItemsList.add(batchItemsVO);
		}

		return isValidRow;
	}
	
	/**
	 * 
	 * @param fileRecord
	 * @param fileRow
	 * @param errorMap
	 * @param bonusList 
	 * @return
	 * @throws BTSLBaseException 
	 */
	public boolean  validateFOCBatchTrf( String[] fileRecord, int fileRow, ErrorMap errorMap,int headerColumnLength,boolean externalCodeMandatory,String externalsTxnMandatory,ArrayList batchItemsList,String serviceType, ArrayList bonusList,Connection con )  
	{
        int rowNum = fileRow;
		boolean isValidRow = true;
		String message = null;
		String errorCode = null;
		RowErrorMsgLists rowErrorMsgListsObj = new RowErrorMsgLists();
		ArrayList<MasterErrorList> masterErrorLists = new ArrayList<MasterErrorList>();
		Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
		int col = 0;
		String msisdn=null,externalTxnNum,externalTxnDate,externalTxnCode,quantity,remarks,bonustype = null,userCat = null,userGrade = null,loginId = null;
		Date extDate=null;
		long reqQuantity = 0;
				
		if("FOCBATCHTRF".equalsIgnoreCase(serviceType)) {
			if (headerColumnLength == 6) {
				msisdn= fileRecord[0];
	            externalTxnNum = fileRecord[1];
	            externalTxnDate = BTSLDateUtil.getGregorianDateInString(fileRecord[2]);
	            externalTxnCode = fileRecord[3];
	            quantity = fileRecord[4];
	            remarks = fileRecord[5];
	        } else {
	        	msisdn= fileRecord[0];
	        	loginId =  fileRecord[1];
	        	userCat =  fileRecord[2];
	        	userGrade = fileRecord[3];
	            externalTxnNum = fileRecord[4];
	            externalTxnDate = BTSLDateUtil.getGregorianDateInString(fileRecord[5]);
	            externalTxnCode = fileRecord[6];
	            quantity = fileRecord[7];
	            remarks = fileRecord[8];
	        }
			
		}else {
			if (headerColumnLength == 7) {
				msisdn= fileRecord[0];
	            externalTxnNum = fileRecord[1];
	            externalTxnDate = BTSLDateUtil.getGregorianDateInString(fileRecord[2]);
	            externalTxnCode = fileRecord[3];
	            quantity = fileRecord[4];
	            bonustype = fileRecord[5];
	            remarks = fileRecord[6];
	        } else {
	        	msisdn= fileRecord[0];
	        	loginId =  fileRecord[1];
	        	userCat =  fileRecord[2];
	        	userGrade = fileRecord[3];
	            externalTxnNum = fileRecord[4];
	            externalTxnDate = BTSLDateUtil.getGregorianDateInString(fileRecord[5]);
	            externalTxnCode = fileRecord[6];
	            quantity = fileRecord[7];
	            bonustype = fileRecord[8];
	            remarks = fileRecord[9];
	        }
			
		}
		
		if (!BTSLUtil.isNullorEmpty(fileRecord[0]) ) 
		{
			if (!BTSLUtil.isValidMSISDN(fileRecord[0])) 
			{
				isValidRow = false;
				errorCode = PretupsErrorCodesI.INVALID_SUBSCRIBER_MSISDN;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Mobile number" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			}
			
		} else 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Mobile number" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
		}
		if (headerColumnLength > 7) {
			if (BTSLUtil.isNullorEmpty(loginId)) 
			{
				isValidRow = false;
				errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Login Id" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			}
			if (BTSLUtil.isNullorEmpty(userCat)) 
			{
				isValidRow = false;
				errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "User category" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			}
			if (BTSLUtil.isNullorEmpty(userGrade)) 
			{
				isValidRow = false;
				errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "User Grade" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			}
		}
		/*
		 if (BTSLUtil.isNullString(paymentType)) {
			 isValidRow = false;
				errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
				message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Payment Type" });
				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
         }else{
		   // load payment type arraylist
         final ArrayList paymentTypeList = LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_INSTRUMENT_TYPE, true);
         // check that the payment type from request should be
         // present in the payment type list from the lookup cache
         ListValueVO listValueVO = null;
         boolean pmtTypeExist = false;
         if (!BTSLUtil.isNullString(paymentType)) {
             for (int i = 0, k = paymentTypeList.size(); i < k; i++) {
                 listValueVO = (ListValueVO) paymentTypeList.get(i);
             	if(PretupsI.PAYMENT_INSTRUMENT_TYPE_ONLINE.equals(listValueVO.getValue())) {
             		paymentTypeList.remove(i);
             		continue;
             	}
                 if (paymentType.equals(listValueVO.getValue())) {
                     pmtTypeExist = true;
                     break;
                 }
             }
             if (!pmtTypeExist) {
                isValidRow = false;
 				errorCode = "batcho2c.processuploadedfile.error.paymenttype";
 				message = PretupsRestUtil.getMessageString(errorCode);
 				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
          
             }
         }
         }*/
        if (PretupsI.YES.equals(externalsTxnMandatory)) {
			
                if (BTSLUtil.isNullString(externalTxnNum)) {
                	isValidRow = false;
        			errorCode = "batchfoc.processuploadedfile.error.exttxnmissing";
        			message = PretupsRestUtil.getMessageString(errorCode);
        			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
        			col++;
                }
            
        }
        else{
        	 if (!BTSLUtil.isNullString(externalTxnNum) && externalTxnNum.length() > 20) {
        		 isValidRow = false;
 				errorCode = "batcho2c.processuploadedfile.error.exttxnmaxsize";
 				message = PretupsRestUtil.getMessageString(errorCode);
				message = message.replaceAll("\\[0\\]", "20");
 				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
             }
        	 if (!BTSLUtil.isNullString(externalTxnNum)) {
                 if (SystemPreferences.EXTERNAL_TXN_NUMERIC) {
                     if (!BTSLUtil.isNumeric(externalTxnNum)) {
                    	 isValidRow = false;
          				errorCode = "batcho2c.processuploadedfile.error.exttxnnumnumeric";
         				message = PretupsRestUtil.getMessageString(errorCode);
          				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
                     }
                 }
                 if (SystemPreferences.EXTERNAL_TXN_UNIQUE) {
                     if (externalTxnNumber != null && externalTxnNumber.contains(externalTxnNum)) {
                    	 isValidRow = false;
           				errorCode = "batcho2c.processuploadedfile.error.exttxnnumunique";
         				message = PretupsRestUtil.getMessageString(errorCode);
           				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
                     }
                 }
                 externalTxnNumber.add(externalTxnNum);
             }
        	 col++;
        }
        if (BTSLUtil.isNullString(externalTxnDate)&&PretupsI.YES.equals(externalsTxnMandatory)) {
        	isValidRow = false;
			errorCode = "batchfoc.processuploadedfile.error.exttxndatemissing";
			message = PretupsRestUtil.getMessageString(errorCode);
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			col++;
        }
        else
        {
         if (!BTSLUtil.isNullString(externalTxnDate)) {
             try {
                 // This is used to validate the date in XLS file. If
                 // we enter the
                 // date in 01/01/06 then it will be treated as
                 // 01/01/2006 in XLS
                 // file so we have to create a new date object and
                 // validate it.
            	 if(externalTxnDate.length()==7) {
                	 externalTxnDate="0"+externalTxnDate;
                 }
                 
                 extDate = BTSLUtil.getDateFromDateString(externalTxnDate, "MM/dd/yy");
/*                 if(extDate.after(new Date()))
					{
                	 isValidRow = false;
//     				errorCode = "batchfoc.processuploadedfile.error.exttxndateinvalid";
//     				message = PretupsRestUtil.getMessageString(errorCode);
     				errorCode = PretupsErrorCodesI.EXTERNAL_TRANSACTION_DATE_INVALID;
    				message = RestAPIStringParser.getMessage(locale, errorCode, null);
     				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
     				col++;
					}*/
             } catch (Exception ex) {
            	isValidRow = false;
            	errorCode = "batchfoc.processuploadedfile.error.exttxndateinvalid";
  				message = PretupsRestUtil.getMessageString(errorCode);
  				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
 				col++;
             }
         }
        }
            
         // check external txn code
   
        if (BTSLUtil.isNullString(externalTxnCode)&&externalCodeMandatory) {
        	isValidRow = false;
			errorCode = "batchfoc.processuploadedfile.error.externalcodeisreq";
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Ext Code" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
			col++;
        }
        else
        {
        	 if (!BTSLUtil.isNullString(externalTxnCode) && externalTxnCode.length() > 25) {
        		 isValidRow = false;
  				errorCode = "batcho2c.processuploadedfile.error.externalcodemaxsize";
 				message = PretupsRestUtil.getMessageString(errorCode);
				message = message.replaceAll("\\[0\\]", "20");
  				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
  				col++;
             }
        }
        try {
        UserDAO userDAO = new UserDAO();
        if(externalCodeMandatory &&  !BTSLUtil.isNullString(externalTxnCode)) {
          ChannelUserVO channeluserVO = 	userDAO.loadUserDetailsByMsisdn(con, msisdn);
          if( !BTSLUtil.isNullString(channeluserVO.getExternalCode())  &&  !channeluserVO.getExternalCode().equalsIgnoreCase(externalTxnCode)  ) {
        	  isValidRow = false;
  			errorCode = "batchfoc.processuploadedfile.error.InvalidExternalCode";
  			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { externalTxnCode,msisdn });
  			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
  			col++;
        	  
          }
        }
        }catch(Exception ex) {
        	isValidRow = false;
  			errorCode = "batchfoc.processuploadedfile.error.InvalidExternalCode";
  			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { externalTxnCode,msisdn });
  			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
  			col++;
        }

         // check quantity related validations here
            if (!BTSLUtil.isNullString(quantity)) {
                try {
                    if (!BTSLUtil.isNumeric(quantity) && quantity.contains(".")) {
                        final int length = quantity.length();
                        final int index = quantity.indexOf(".");
                        if (length > index + 3) {
                            isValidRow = false;
              				errorCode = "batcho2c.processuploadedfile.error.upto2DecimalOnly";
             				message = PretupsRestUtil.getMessageString(errorCode);
              				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
              				col++;
                        }
                    }

                    reqQuantity = PretupsBL.getSystemAmount(quantity);
                    if (reqQuantity < 0) {
                    	isValidRow = false;
          				errorCode = "batcho2c.processuploadedfile.error.qtynonnumeric";
         				message = PretupsRestUtil.getMessageString(errorCode);
          				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
          				col++;
                    }

                } catch (Exception e) {
                	isValidRow = false;
      				errorCode = "batcho2c.processuploadedfile.error.qtynumeric";
     				message = PretupsRestUtil.getMessageString(errorCode);
      				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
      				col++;
                }
            } else {
            	isValidRow = false;
  				errorCode = "batcho2c.processuploadedfile.error.qtyreq";
 				message = PretupsRestUtil.getMessageString(errorCode);
  				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
  				col++;
            }
            
            
            // validation for bonus type
            if(!"FOCBATCHTRF".equalsIgnoreCase(serviceType)) {
            	
            boolean notFoundBonusType = false;
            if (BTSLUtil.isNullString(bonustype)) {
            	isValidRow = false;
  				errorCode = "batchdirectpayout.processuploadedfile.error.bonustype";
 				message = PretupsRestUtil.getMessageString(errorCode);
  				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
  				col++;
            }

            if (!BTSLUtil.isNullString(bonustype)) {
                notFoundBonusType = true;
                if (bonusList!= null) {
                    for (int i = 0; i < bonusList.size(); i++) {
                        ListValueVO listVO = (ListValueVO) bonusList.get(i);
                        if (bonustype.equals(listVO.getLabel())) {
                        	bonustype = listVO.getValue().split(":")[0];
                            notFoundBonusType = false;
                            break;
                        }
                    }
                }

                if (notFoundBonusType) {
                    isValidRow = false;
      				errorCode =  "batchdirectpayout.batchapprovereject.msg.error.incorrectbonustype";
     				message = PretupsRestUtil.getMessageString(errorCode);
      				createFailureResponseForEachCol(masterErrorLists, message, errorCode);
      				col++;
                    notFoundBonusType = true;
                }

            }
           } 
            if (!BTSLUtil.isNullString(remarks)) {
                if (remarks.length() > 100) {
                    remarks = remarks.substring(0, 100);
                }

            }

		if (!isValidRow) 
		{
			// setting error
			rowErrorMsgListsObj.setRowName(fileRecord[0]);
			rowErrorMsgListsObj.setRowValue("Line " + rowNum);
			rowErrorMsgListsObj.setMasterErrorList(masterErrorLists);
			if (errorMap.getRowErrorMsgLists() == null) {
				errorMap.setRowErrorMsgLists(new ArrayList<RowErrorMsgLists>());
			}
			(errorMap.getRowErrorMsgLists()).add(rowErrorMsgListsObj);
		}else
		{
			FOCBatchItemsVO batchItemsVO = new FOCBatchItemsVO();
             batchItemsVO.setRecordNumber(fileRow);
             batchItemsVO.setMsisdn(fileRecord[0]);
             //batchItemsVO.setPaymentType(paymentType);
             batchItemsVO.setCategoryName(userCat);
             batchItemsVO.setGradeName(userGrade);
             batchItemsVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
             batchItemsVO.setModifiedOn(new Date());
             batchItemsVO.setLoginID(loginId);
             batchItemsVO.setExtTxnNo(externalTxnNum);
             batchItemsVO.setExtTxnDate(extDate);
             batchItemsVO.setTransferDate(new Date());
             batchItemsVO.setRequestedQuantity(reqQuantity);
             batchItemsVO.setBonusType(bonustype);
             batchItemsVO.setInitiatorRemarks(remarks);
             batchItemsVO.setExternalCode(externalTxnCode);
             batchItemsVO.setWalletCode(PretupsI.ACCOUNT_TYPE_MAIN);
             batchItemsList.add(batchItemsVO);
		}

		return isValidRow;
	}
	public LinkedHashMap<String, List<String>> uploadAndReadGenericFileO2CBatchWithdrawApproval(Connection con,O2CBatchMasterVO o2cBatchMasterVO,HashMap<String, String> fileDetailsMap, int headerRow, ErrorMap errorMap,LinkedHashMap closedOrderMap,LinkedHashMap approveRejectMap,String externalsTxnMandatory, ArrayList batchItemsList,LinkedHashMap map,int rowInduced,O2CBatchWithdrawForm theForm) throws BTSLBaseException {

		String methodName = "uploadAndReadGenericFileO2CBatchApproval";
		if (log.isDebugEnabled()) {
			LogFactory.printLog(methodName, "Entered", log);
			log.debug(methodName, "fileDetailsMap " + fileDetailsMap + " headerRow" + headerRow );
		}
		this.fileDetailsMap = fileDetailsMap;
		
		//validate fileDetailsMap data
		// fileType, fileName, fileAttachment and serviceKeyword is mandatory
		validateFileDetailsMap(headerRow);  // throws exception
	   
		this.base64val = fileDetailsMap.get(PretupsI.FILE_ATTACHMENT);
		this.requestFileName = fileDetailsMap.get(PretupsI.FILE_NAME);
		LinkedHashMap<String, List<String>> responseMap = new LinkedHashMap<String, List<String>>();
		filePathCons = Constants.getProperty("O2C_BATCH_APPROVAL_FILE_PATH");
		validateFilePathCons(filePathCons);
		
		String filePathConstemp = filePathCons + "temp/";        // C:/apache-tomcat-8.0.52/logs/batcho2c/upload/temp/
		createDirectory(filePathConstemp);
		

		setFileNameWithExtention();
		
		filepathtemp = filePathConstemp + fileNamewithextention;   // C:/apache-tomcat-8.0.321/logs/BatchC2CUpload/temp/c2cBatchTransfer.xlsx

		byte[] base64Bytes = decodeFile(base64val);
		log.debug("filepathtemp:", filepathtemp);
		log.debug("base64Bytes:", base64Bytes);
		
		String fileSize = Constants.getProperty("VOMS_MAX_FILE_LENGTH");
		validateFileSize(fileSize, base64Bytes);  // throws exception
		
		writeByteArrayToFile(filepathtemp, base64Bytes);
		try 
		{
            List<String> fileHeaderList = new ArrayList<String>();
			String fileValueSeparator = Constants.getProperty("BL_VOMS_FILE_SEPARATOR");
			if (BTSLUtil.isNullorEmpty(fileValueSeparator)) 
			{
				log.error(methodName, "fileValueSeparator is null in Constants.props");
				 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_SEPERATOR_IN_CONSTANTS,
				 PretupsI.RESPONSE_FAIL,null); 
			}
	
			List<String> fileValueArray = readuploadedfile();
			if( fileValueArray.size() == 0) {
				log.error(methodName, "Uploaded file is empty");
				 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_FILE,
				 PretupsI.RESPONSE_FAIL,null); 
			}

			String[] fileRecord = null;
			int headerColumnLength = 0;
			int fileRow = headerRow;
			int validRowCount = 0;
			String serviceKeyword = fileDetailsMap.get(PretupsI.SERVICE_KEYWORD);
			uniqueMsisdnWithDenom = new HashSet<String>();
			boolean isValidRow=false;
			try 
			{
				int discardCounts = 0;
	            int cancelCounts=0;
	            int blankCount=0;
	            
				for (int loop = 0; loop < fileValueArray.size(); loop++) 
				{  
					String fileData1 = null;
					fileData1 = (String) fileValueArray.get(loop);
					fileRecord = fileData1.split(fileValueSeparator, -1); // fileRecord reads will contain one extra value, and last value will always be empty
					
					if(loop == 0) 
					{
						headerColumnLength =(PretupsI.FILE_CONTENT_TYPE_CSV.equalsIgnoreCase( 
								fileDetailsMap.get(PretupsI.FILE_TYPE1))) ? fileRecord.length : fileRecord.length - 1 ;
						
						if( !validateFileHeaderLength( serviceKeyword, headerColumnLength ) ) {
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_FILE_HEADINGS,
									PretupsI.RESPONSE_FAIL, null); 
						}
						
						for(int col= 0; col < headerColumnLength; col++) 
						{
							responseMap.put(fileRecord[col].trim(), new ArrayList<String>());
							fileHeaderList.add(fileRecord[col].trim());
						}
					} else 
					{   
						fileRow++; 

				        this.rowNum = fileRow +1;
						//isValidRow = true;
						String message = null;
						String errorCode = null;
						RowErrorMsgLists rowErrorMsgListsObj = new RowErrorMsgLists();
						ArrayList<MasterErrorList> masterErrorLists = new ArrayList<MasterErrorList>();
						Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
						int col = 0;
						
						BatchO2CItemsVO batchItemsVO = (BatchO2CItemsVO) map.get(fileRecord[0]);
						if (!BTSLUtil.isNullorEmpty(fileRecord[0]) ) 
						{
							if (batchItemsVO==null) 
							{
								isValidRow = true;
								errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
								message = RestAPIStringParser.getMessage(locale, errorCode,null);
								createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
								
								continue;
							}
							
						} else 
						{
							isValidRow = true;
							errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
							message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Detail ID blank" });
							createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
		                        blankCount++;
		                        continue;
						}
							if (!batchItemsVO.getMsisdn().equals(fileRecord[1]))
							{
								isValidRow = true;
								errorCode = PretupsErrorCodesI.INVALID_LOGIN_ID;
								message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Login Id" });
								createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
								continue;
							}
							if (!BTSLUtil.isNullString(fileRecord[38 - rowInduced]) && !("Y".equalsIgnoreCase(fileRecord[38 - rowInduced])) && !("N"
				                    .equalsIgnoreCase(fileRecord[38 - rowInduced])) && !("D".equalsIgnoreCase(fileRecord[38 - rowInduced]))) 
							{
								isValidRow = true;
								errorCode = PretupsErrorCodesI.ERROR_INVALID_REQUESTINTTYPE;
								message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Request Action" });
								createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
								continue;
							}
							if(BTSLUtil.isNullString(fileRecord[38-rowInduced])||fileRecord[38-rowInduced].equalsIgnoreCase("D"))        	//||arr[i][17-indexToBeReduced].equalsIgnoreCase("N") 
				        	{
				            discardCounts++;
				            continue;
				        }
							if (!batchItemsVO.getLoginID().equals(fileRecord[2])) {
								isValidRow = true;
								errorCode = PretupsErrorCodesI.INVALID_LOGIN_ID;
								message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Login Id" });
								createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
			                    continue;
			                }

			                // Check batch id is same or not
			                if (!batchItemsVO.getBatchId().equals(fileRecord[3])) {
			                	isValidRow = true;
								errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
								message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Batch ID invalid" });
								createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
			                    continue;
			                }
			                // check category name is same or not
			                if (!batchItemsVO.getCategoryName().equals(fileRecord[4])) {
			                	isValidRow = true;
								errorCode = PretupsErrorCodesI.INVAILD_CATEGORY_NAME;
								message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Requested Quantity" });
								createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
			                    continue;
			                }
			                // check grade name is same or not
			                if (!batchItemsVO.getGradeName().equals(fileRecord[5])) {
			                	isValidRow = true;
								errorCode = PretupsErrorCodesI.INVALID_GEOGRAPHY;
								message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Requested Quantity" });
								createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
			                    continue;
			                }
			             // check payment type is same or not
			                if (!batchItemsVO.getPaymentType().equals(fileRecord[7])) {
			                	isValidRow = true;
								errorCode = PretupsErrorCodesI.PAYMENT_TYPE_DOES_NOT_EXIST;
								message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Requested Quantity" });
								createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
			                    continue;
			                }
				    long reqQuantity=0;
				    try {
				        // check required quantity is numeric or not
				        reqQuantity = PretupsBL.getSystemAmount(fileRecord[12]);
				    } catch (Exception e) {
				    	isValidRow = true;
						errorCode = PretupsErrorCodesI.QUANTITY_NOT_NUMERIC;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Requested Quantity" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
						continue;
				    }
				    // check required qty is same or not
				    if (!(batchItemsVO.getRequestedQuantity() == reqQuantity)) {
				    	isValidRow = true;
						errorCode = PretupsErrorCodesI.QUANTITY_NOT_NUMERIC;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Requested Quantity" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
						continue;
				    }
				    if (!(fileRecord[13].equals(PretupsBL.getDisplayAmount(batchItemsVO.getPayableAmount())))) {
				    	isValidRow = true;
						errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                    continue;
	                }
	                if (!(fileRecord[14].equals(PretupsBL.getDisplayAmount(batchItemsVO.getNetPayableAmount())))) {
	                	isValidRow = true;
						errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                    continue;
	                }
	                if (!batchItemsVO.getCommissionProfileSetId().equals(fileRecord[15])) {
	                	isValidRow = true;
						errorCode = PretupsErrorCodesI.COMMISSION_SET_ID_INVALID;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                    continue;
	                }
	                if (!batchItemsVO.getCommissionProfileVer().equals(fileRecord[16])) {
	                	isValidRow = true;
						errorCode = PretupsErrorCodesI.COMMISSION_SET_ID_INVALID;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                    continue;
	                }
	                if (!batchItemsVO.getCommissionProfileDetailId().equals(fileRecord[17])) {
	                	isValidRow = true;
						errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                    continue;
	                }
	                if (!batchItemsVO.getTax1Type().equals(fileRecord[18])) {
	                	isValidRow = true;
						errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                    continue;
	                }
	                if (!(String.valueOf(batchItemsVO.getTax1Rate()).equals(fileRecord[19]))) {
	                	isValidRow = true;
						errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                    continue;
	                }
	                if (!((fileRecord[20]).equals(PretupsBL.getDisplayAmount(batchItemsVO.getTax1Value())))) {
	                	isValidRow = true;
						errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                    continue;
	                }
	                if (!batchItemsVO.getTax2Type().equals(fileRecord[21])) {
	                	isValidRow = true;
						errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                    continue;
	                }
	                if (!(String.valueOf(batchItemsVO.getTax2Rate()).equals(fileRecord[22]))) {
	                	isValidRow = true;
						errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                    continue;
	                }
	                if (!((fileRecord[23]).equals(PretupsBL.getDisplayAmount(batchItemsVO.getTax2Value())))) {
	                	isValidRow = true;
						errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                    continue;
	                }
	                if (!batchItemsVO.getTax3Type().equals(fileRecord[24])) {
	                	isValidRow = true;
						errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                    continue;
	                }
	                if (!(String.valueOf(batchItemsVO.getTax3Rate()).equals(fileRecord[25]))) {
	                	isValidRow = true;
						errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                    continue;
	                }
	                if (!((fileRecord[26]).equals(PretupsBL.getDisplayAmount(batchItemsVO.getTax3Value())))) {
	                	isValidRow = true;
						errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                    continue;
	                }
	                if (!batchItemsVO.getCommissionType().equals(fileRecord[27])) {
	                	isValidRow = true;
						errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                    continue;
	                }
	                if (!(String.valueOf(batchItemsVO.getCommissionRate()).equals(fileRecord[28]))) {
	                	isValidRow = true;
						errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                    continue;
	                }
	                if (!((fileRecord[29]).equals(PretupsBL.getDisplayAmount(batchItemsVO.getCommissionValue())))) {
	                	isValidRow = true;
						errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                    continue;
	                }

	                // If external txn number is mandatory then check if it is in
	                // file or not
	                if (!PretupsI.PAYMENT_INSTRUMENT_TYPE_CASH.equals(batchItemsVO.getPaymentType())) {
	                    if (BTSLUtil.isNullString(fileRecord[6]) || (!(batchItemsVO.getExtTxnNo().equals(fileRecord[6])))) {
	                    	isValidRow = true;
							errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
							message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
							createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                        continue;
	                    }
	                }

	                if (!BTSLUtil.isNullString(fileRecord[6])) {
	                    if (PretupsI.PAYMENT_INSTRUMENT_TYPE_CASH.equals(batchItemsVO.getPaymentType())) {
	                        if (!(batchItemsVO.getExtTxnNo().equals(fileRecord[6]))) {
	                        	isValidRow = true;
								errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
								message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
								createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                            continue;

	                        }
	                    }
	                }

	                if (!BTSLUtil.isNullString(fileRecord[6])) {
	                    // check external txn number for numeric
	                    if (fileRecord[6].length() > 20) {
	                    	isValidRow = true;
							errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
							message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
							createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                        continue;
	                    }
	                    // check external txn number for numeric
	                    if (SystemPreferences.EXTERNAL_TXN_NUMERIC) {
	                        if (!BTSLUtil.isNumeric(fileRecord[6])) {
	                        	isValidRow = true;
								errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
								message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
								createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                            continue;
	                        }
	                    }
	                    // check external txn is unique within the system
	                    if (SystemPreferences.EXTERNAL_TXN_UNIQUE) {
	                        if (externalTxnNumber != null && externalTxnNumber.contains(fileRecord[6])) {
	                        	isValidRow = true;
								errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
								message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
								createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                            continue;
	                        }
	                    }
	                    batchItemsVO.setExtTxnNo(fileRecord[6]);
	                    externalTxnNumber.add(batchItemsVO.getExtTxnNo());
	                }
	                // Check the external txn date is valid or not
	                if (!BTSLUtil.isNullString(fileRecord[10])) {
	                    try {
	                       Date date = new Date(fileRecord[10]);
	                        BTSLUtil.getDateStringFromDate(date);
	                        batchItemsVO.setExtTxnDate(BTSLDateUtil.getGregorianDate((fileRecord[10])));
	                    } catch (Exception pex) {
	                    	isValidRow = true;
							errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
							message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
							createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                        continue;
	                    }
	                }
	                try {
	                    // check required quantity is numeric or not
	                    reqQuantity = PretupsBL.getSystemAmount(fileRecord[32]);
	                    if (reqQuantity <= 0) {
	                    	isValidRow = true;
							errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
							message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
							createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                        continue;
	                    }
	                } catch (Exception e) {
	                	isValidRow = true;
						errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                    continue;
	                }
	                // check required qty is same or not
	                if (!(batchItemsVO.getRequestedQuantity() == reqQuantity)) {
	                	isValidRow = true;
						errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                    continue;
	                }
	                // --------approval 1 quantity based checks indexToBeReduced=0
	                // for apprv2 and 3 for apprv1
	      
	                long appr1Q;
	                if (!BTSLUtil.isNullString(fileRecord[33])) {
	                    try {
	                        // check required quantity is numeric or not
	                        appr1Q = PretupsBL.getSystemAmount(fileRecord[33]);
	                        if (appr1Q <= 0) {
	                        	isValidRow = true;
								errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
								message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
								createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                            continue;
	                        }
	                    } catch (Exception e) {
	                    	isValidRow = true;
							errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
							message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
							createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                        continue;
	                    }
	                    if (appr1Q > reqQuantity) {
	                    	isValidRow = true;
							errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
							message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
							createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                        continue;
	                    }
	                    if (rowInduced == 0)// then apprv 1 quantity should
	                    // not be changed
	                    {
	                        if (!(batchItemsVO.getFirstApprovedQuantity() == appr1Q) || appr1Q == 0) {
	                        	isValidRow = true;
								errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
								message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
								createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                            continue;
	                        }
	                    }
	                } else {
	                    appr1Q = reqQuantity;
	                }
	                // if appr1 q != req quqntity
	                // populate commissions in VO according to new amount and
	                // already selected commission id and version
	                try {
	                    if (appr1Q != reqQuantity && appr1Q > 0) {
	                        populatecommison(batchItemsVO, con, appr1Q, o2cBatchMasterVO.getProductCode(), o2cBatchMasterVO.getProductMrp());
	                    } else {
	                        appr1Q = reqQuantity;
	                    }
	                } catch (Exception e) {
	                	isValidRow = true;
						errorCode = PretupsErrorCodesI.EMPTY_BATCH_ID;
						message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Invalid Batch ID" });
						createFailureResponseForEachCol1(rowNum,theForm,fileRecord[0],errorMap,rowErrorMsgListsObj,masterErrorLists, message, errorCode);
	                    continue;
	                }

	                // approve 2 quantity
	                long appr2 = 0;
	                appr2 = appr1Q;


						if (!isValidRow) 
						{
							batchItemsVO.setRecordNumber(rowNum);
							// setting error
							// If file validation not exists then only construct the
				            // batchItemsVO and add map for processing
				                if (!BTSLUtil.isNullString(batchItemsVO.getExtTxnNo())) {
				                    externalTxnNumber.add(batchItemsVO.getExtTxnNo());
				                }
				                if ("approval1".equals(String.valueOf(theForm.getRequestType()))) {
				                    // Check the remarks is null or not
				                	batchItemsVO.setFirstApprovedQuantity(appr1Q);
				                    if (fileRecord[36] != null) {
				                        if (fileRecord[36].length() > 100) {
				                            batchItemsVO.setFirstApproverRemarks(fileRecord[36].substring(0, 100));
				                        } else {
				                            batchItemsVO.setFirstApproverRemarks(fileRecord[36]);
				                        }
				                    }
				                    if ("N".equalsIgnoreCase(fileRecord[35])) {
				                        batchItemsVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
				                        cancelCounts++;
				                    } else if ("Y".equalsIgnoreCase(fileRecord[35])) {
				                        // If request is to approve the order and in system
				                        // preference approvel level
				                        // is <=1 the close the order otherwise approve at
				                        // level 1
				                       /* if (theForm.getO2cOrderApprovalLevel() <= 1) {
				                            batchItemsVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
				                        } else {*/
				                            batchItemsVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
											/* } */
				                    }
				                    batchItemsVO.setFirstApprovedBy("userVO.getUserID()");
				                    batchItemsVO.setFirstApprovedOn(new Date());
				                } else if ("approval2".equals(String.valueOf(theForm.getRequestType()))) {
				                    // Check the remarks is null or not
				                	batchItemsVO.setSecondApprQty(appr2);
				                    if (fileRecord[36] != null) {
				                    	batchItemsVO.setSecondApproverRemarks(fileRecord[36 - rowInduced]);
				                    }
				                    if ("N".equalsIgnoreCase(fileRecord[35])) {
				                        batchItemsVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
				                        cancelCounts++;
				                    } else if ("Y".equalsIgnoreCase(fileRecord[35])) {
				                        batchItemsVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
				                    }
				                    batchItemsVO.setSecondApprovedBy("userVO.getUserID()");
				                    batchItemsVO.setSecondApprovedOn(new Date());
								} /*
									 * else if ("approval3".equals(String.valueOf(theForm.getRequestType()))) { //
									 * Check the remarks is null or not if (fileRecord[18 - rowInduced] != null) {
									 * if (fileRecord[18 - rowInduced].length() > 100) {
									 * batchItemsVO.setThirdApproverRemarks(fileRecord[18 - rowInduced].substring(0,
									 * 100)); } else { batchItemsVO.setThirdApproverRemarks(fileRecord[18 -
									 * rowInduced]); } } if ("N".equalsIgnoreCase(fileRecord[17 - rowInduced])) {
									 * batchItemsVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
									 * cancelCounts++; } else if ("Y".equalsIgnoreCase(fileRecord[17 - rowInduced]))
									 * { batchItemsVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE); } }
									 */

				                // If status is closed then contruct map for closing
				                // otherwise one map will be there
				                if (batchItemsVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE)) {
				                    closedOrderMap.put(batchItemsVO.getBatchDetailId(), batchItemsVO);
				                } else {
				                    approveRejectMap.put(batchItemsVO.getBatchDetailId(), batchItemsVO);
				                }
						}
						}
					}
				
				
				if( isFileWritten && isValidRow ) {
					filedelete();
				}
				ArrayList<String>filepathtemp1= new ArrayList<String>();
				filepathtemp1.add(filepathtemp);
				responseMap.put("filepathtemp", filepathtemp1);
				LinkedList<String>valid= new LinkedList<String>();
				String isValidRow1 = null;
				if(isValidRow)
					isValidRow1 = "true";
				else
					isValidRow1 = "false";
				valid.add(isValidRow1);
				valid.add(String.valueOf(blankCount));
				valid.add(String.valueOf(cancelCounts));
				valid.add(String.valueOf(discardCounts));
				valid.add(String.valueOf(rowNum));
				responseMap.put("isValidRow", valid);
				return responseMap;
					
			} catch (ArrayIndexOutOfBoundsException e) {
				if(isFileWritten) {
					filedelete();
				}
				
				log.error(methodName, "Exceptin:e=" + e);
				log.errorTrace("uploadAndProcessFile", e);
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ARRAY_INDEX_OUT_OF_BOUND_EXCEPTION,
						PretupsI.RESPONSE_FAIL, null);

			}

		} catch (IOException io) {
			if(isFileWritten) {
				filedelete();
			}
			log.error(methodName, "Exceptin:e=" + io);
			log.errorTrace("uploadAndProcessFile", io);
			io.printStackTrace();
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT,
					PretupsI.RESPONSE_FAIL, null);

		} catch(Exception e) {
			if(isFileWritten) {
				filedelete();
			}
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace("uploadAndProcessFile", e);
			if(!BTSLUtil.isNullorEmpty(e.getMessage())) {
				throw new BTSLBaseException(this, methodName, e.getMessage(),
						PretupsI.RESPONSE_FAIL, null);
			}else {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION,
						PretupsI.RESPONSE_FAIL, null);
			}
			
		}
		finally {
			if (log.isDebugEnabled()) {
				LogFactory.printLog(methodName, "Exited", log);
				log.debug(methodName, "responseMap " + responseMap );
			}
		}
	}

	/**
     * @param batchO2CItemsVO
     *            this method is used to populate commision calculations
     *            according to new approved amounts
     */
    private void populatecommison(BatchO2CItemsVO batchO2CItemsVO, Connection p_con, long p_amount, String p_product, long p_prdMrp) throws BTSLBaseException{
        // on basis of commision,version and new approved quantity update the
        // taxes
        final String METHOD_NAME = "populatecommison";
        if (log.isDebugEnabled()) {
            log.debug("populatecommison", "Entered p_amount" + p_amount);
        }
        final String old_commId = batchO2CItemsVO.getCommissionProfileDetailId();
        CommissionProfileDAO commdao = null;
        final CommissionProfileDeatilsVO commVO = null;
        ChannelTransferItemsVO channelTransferItemsVO = null;
        ArrayList transferItemsList = null;
        try {
            commdao = new CommissionProfileDAO();
            channelTransferItemsVO = commdao.getCommProfDetails(p_con, old_commId, p_amount, batchO2CItemsVO.getCommissionProfileSetId(), batchO2CItemsVO
                .getCommissionProfileVer(), p_product);
            if (channelTransferItemsVO != null) {
                channelTransferItemsVO.setUnitValue(p_prdMrp);
                channelTransferItemsVO.setRequiredQuantity(p_amount);
                // this value will be used in the tax calculation.
                channelTransferItemsVO.setRequestedQuantity(PretupsBL.getDisplayAmount(p_amount));
                transferItemsList = new ArrayList();
                transferItemsList.add(channelTransferItemsVO);

                // make a new channel TransferVO to transfer into the method
                // during tax calculataion
                final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
                channelTransferVO.setChannelTransferitemsVOList(transferItemsList);
                channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);

                ChannelTransferBL.calculateMRPWithTaxAndDiscount(channelTransferVO, PretupsI.TRANSFER_TYPE_O2C);
                // set values in o2c batch items vo for update
                batchO2CItemsVO.setCommissionRate(channelTransferItemsVO.getCommRate());
                batchO2CItemsVO.setCommissionType(channelTransferItemsVO.getCommType());
                batchO2CItemsVO.setCommissionValue(channelTransferItemsVO.getCommValue());
                batchO2CItemsVO.setTax1Rate(channelTransferItemsVO.getTax1Rate());
                batchO2CItemsVO.setTax1Type(channelTransferItemsVO.getTax1Type());
                batchO2CItemsVO.setTax1Value(channelTransferItemsVO.getTax1Value());
                batchO2CItemsVO.setTax2Rate(channelTransferItemsVO.getTax2Rate());
                batchO2CItemsVO.setTax2Type(channelTransferItemsVO.getTax2Type());
                batchO2CItemsVO.setTax2Value(channelTransferItemsVO.getTax2Value());
                batchO2CItemsVO.setTax3Rate(channelTransferItemsVO.getTax3Rate());
                batchO2CItemsVO.setTax3Type(channelTransferItemsVO.getTax3Type());
                batchO2CItemsVO.setTax3Value(channelTransferItemsVO.getTax3Value());
                batchO2CItemsVO.setCommissionProfileDetailId(channelTransferItemsVO.getCommProfileDetailID());
                batchO2CItemsVO.setTransferMrp(channelTransferItemsVO.getProductTotalMRP());
                batchO2CItemsVO.setPayableAmount(channelTransferItemsVO.getPayableAmount());
                batchO2CItemsVO.setNetPayableAmount(channelTransferItemsVO.getNetPayableAmount());
                if(channelTransferItemsVO.getApprovedQuantity() == 0 || batchO2CItemsVO.getRequestedQuantity() == channelTransferItemsVO.getApprovedQuantity() )
                {
                batchO2CItemsVO.setCommCalReqd(false);
                }
                else
                {
                batchO2CItemsVO.setCommCalReqd(true);
                }
            } else {
                throw new BTSLBaseException(PretupsErrorCodesI.COMM_PROFILE_SETVERNOT_ASSOCIATED);
            }
        } catch (BTSLBaseException be) {
            log.error("populatecommison", "BTSLBaseException " + be);
            throw be;
        } catch (Exception e) {
            log.errorTrace(METHOD_NAME, e);
            // to add handling here
            log.error("populatecommison", "Exception " + e);
            throw new BTSLBaseException(this, METHOD_NAME, "Exception in populating commision calculations");
        }
        if (log.isDebugEnabled()) {
            log.debug("populatecommison", "Exited");
        }
    }
    public void writeByteArrayToFile2(String filePath, byte[] base64Bytes) throws BTSLBaseException {
		try {
			log.debug("writeByteArrayToFile: ", filePath);
			log.debug("writeByteArrayToFile: ", base64Bytes);
			if (new File(filePath).exists()) {
				fileExist=true;
				throw new BTSLBaseException("OAuthenticationUtil", "writeByteArrayToFile",
						PretupsErrorCodesI.BATCH_UPLOAD_FILE_EXISTS, PretupsI.RESPONSE_FAIL, null);
			}
			FileUtils.writeByteArrayToFile(new File(filePath), base64Bytes);
			isFileWritten = true ;
		} catch (BTSLBaseException be) {
			throw be;
		} catch (Exception e) {
			log.debug("writeByteArrayToFile: ", e.getMessage());
			log.error("writeByteArrayToFile", "Exceptin:e=" + e);
			log.errorTrace("writeByteArrayToFile", e);

		}
	}
	public void writeByteArrayToFile2New(String filePath, byte[] base64Bytes) throws BTSLBaseException {
		final String methodName = "writeByteArrayToFile2New";
		try {
			log.debug(methodName, filePath);
			log.debug(methodName, base64Bytes);
			FileUtils.writeByteArrayToFile(new File(filePath), base64Bytes);
			isFileWritten = true ;
		} catch (Exception e) {
			log.debug(methodName, e.getMessage());
			log.error(methodName, "Exceptin:e=" + e);


		}
	}
    public void deleteUploadedFile(String fullpathAndFileName) {
        final String METHOD_NAME = "deleteUploadedFile";
        final File f = new File(fullpathAndFileName);
        if (f.exists()) {
            try {
            	boolean isDeleted = f.delete();
                if(isDeleted){
                	log.debug(METHOD_NAME, "File deleted successfully");
                }
            } catch (Exception e) {
            	log.errorTrace(METHOD_NAME, e);
            	log.error("deleteUploadedFile", "Error in deleting the uploaded file" + f.getName() + " as file validations are failed Exception::" + e);
            }
        }
    }
    
	public LinkedHashMap<String, List<String>> uploadAndReadGenericFileBatchOperatorUserInitiate(HashMap<String, String> fileDetailsMap, int startRow, int headerRow ,ErrorMap errorMap) throws BTSLBaseException {

		String methodName = "uploadAndReadGenericFileBatchUserInitiate";
		if (log.isDebugEnabled()) {
			LogFactory.printLog(methodName, "Entered", log);
			log.debug(methodName, "fileDetailsMap " + fileDetailsMap + " StartRow " + startRow + " HeadereRow " + headerRow );
		}
		fileDetailsMap.put("headerRowNum", Integer.toString(headerRow));
		this.fileDetailsMap = fileDetailsMap;
		
		//validate fileDetailsMap data
		// fileType, fileName, fileAttachment and serviceKeyword is mandatory
		validateFileDetailsMap(startRow);  // will set where to start reading from
	    
		this.base64val = fileDetailsMap.get(PretupsI.FILE_ATTACHMENT);
		this.requestFileName = fileDetailsMap.get(PretupsI.FILE_NAME);
		LinkedHashMap<String, List<String>> responseMap = new LinkedHashMap<String, List<String>>();
		filePathCons = Constants.getProperty("UploadBatchOPTUserFilePath");
		validateFilePathCons(filePathCons);
		
		String filePathConstemp = filePathCons + "temp/";        
		createDirectory(filePathConstemp);
		

		setFileNameWithExtention();
		
		filepathtemp = filePathConstemp + fileNamewithextention;   

		byte[] base64Bytes = decodeFile(base64val);
		log.debug("filepathtemp:", filepathtemp);
		log.debug("base64Bytes:", base64Bytes);
		
		String fileSize = Constants.getProperty("MAX_XLS_FILE_SIZE_FOR_BATCH_OPT_USER");
		validateFileSize(fileSize, base64Bytes);  // throws exception
		
		writeByteArrayToFileBatchOptInit(filepathtemp, base64Bytes);

		try 
		{
            List<String> fileHeaderList = new ArrayList<String>();
			String fileValueSeparator = Constants.getProperty("BL_VOMS_FILE_SEPARATOR");
			if (BTSLUtil.isNullorEmpty(fileValueSeparator)) 
			{
				log.error(methodName, "fileValueSeparator is null in Constants.props");
				 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_SEPERATOR_IN_CONSTANTS,
				 PretupsI.RESPONSE_FAIL,null); 
			}
	
			List<String> fileValueArray = readUploadedFileBatchOptInit();
			if( fileValueArray.size() == 0) {
				log.error(methodName, "Uploaded file is empty");
				 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_FILE,
				 PretupsI.RESPONSE_FAIL,null); 
			}

			String[] fileRecord = null;
			int headerColumnLength = 0;
			int fileRow = headerRow;
			int validRowCount = 0;
			String serviceKeyword = fileDetailsMap.get(PretupsI.SERVICE_KEYWORD);
			uniqueMsisdnWithDenom = new HashSet<String>();
			try 
			{
                
				for (int loop = 0; loop < fileValueArray.size(); loop++) 
				{  
					String fileData1 = null;
					fileData1 = (String) fileValueArray.get(loop);
					fileRecord = fileData1.split("#", -1);
					if(loop < headerRow) {
						if(loop==1) fileDetailsMap.put("downloadedBy", fileRecord[1]);
						if(loop==2) fileDetailsMap.put("categoryName", fileRecord[1]);
						continue;
					}
					if(loop == headerRow) 
					{
						headerColumnLength =(PretupsI.FILE_CONTENT_TYPE_CSV.equalsIgnoreCase( 
								fileDetailsMap.get(PretupsI.FILE_TYPE1))) ? fileRecord.length : fileRecord.length - 1 ;
						
//						if( !validateFileHeaderLength( serviceKeyword, headerColumnLength ) ) {
//							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_FILE_HEADINGS,
//									PretupsI.RESPONSE_FAIL, null); 
//						}
						for(int col= 0; col < headerColumnLength; col++) 
						{
							responseMap.put(fileRecord[col].trim(), new ArrayList<String>());
							fileHeaderList.add(fileRecord[col].trim());
						}
					} else 
					{   
						fileRow++; 
						boolean isValidRow = validateEachRow( fileRecord, fileRow, errorMap, serviceKeyword ) ;
					    //boolean isValidRow = validateEachRow(fileHeaderList, fileRecord, errorMap, headerColumnLength, loop, headerRow, fileDetailsMap);
						if(isValidRow) 
						{
							validRowCount++;
							for(int col= 0; col < headerColumnLength; col++) 
							{
								String header = fileHeaderList.get(col);
								responseMap.get(header).add(fileRecord[col]);
							}
						}
					}
				}
				
				if( isFileWritten && validRowCount == 0 ) {
					filedelete();
				}
				
				fileDetailsMap.put("filePath", filepathtemp );
				fileDetailsMap.put("recordCount", Integer.toString(validRowCount));
				
				return responseMap;
					
			} catch (ArrayIndexOutOfBoundsException e) {
				if(isFileWritten) {
					filedelete();
				}
				
				log.error(methodName, "Exceptin:e=" + e);
				log.errorTrace("uploadAndProcessFile", e);
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ARRAY_INDEX_OUT_OF_BOUND_EXCEPTION,
						PretupsI.RESPONSE_FAIL, null);

			}

		} catch (IOException io) {
			if(isFileWritten) {
				filedelete();
			}
			log.error(methodName, "Exceptin:e=" + io);
			log.errorTrace("uploadAndProcessFile", io);
			io.printStackTrace();
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT,
					PretupsI.RESPONSE_FAIL, null);

		} catch(Exception e) {
			if(isFileWritten) {
				filedelete();
			}
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace("uploadAndProcessFile", e);
			if(!BTSLUtil.isNullorEmpty(e.getMessage())) {
				throw new BTSLBaseException(this, methodName, e.getMessage(),
						PretupsI.RESPONSE_FAIL, null);
			}else {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION,
						PretupsI.RESPONSE_FAIL, null);
			}
			
		}
		finally {
			if (log.isDebugEnabled()) {
				LogFactory.printLog(methodName, "Exited", log);
				log.debug(methodName, "responseMap " + responseMap );
			}
		}
	}
	
	public List<String> uploadAndReadGenericFileForUserMovement(HashMap<String, String> fileDetailsMap, int headerRow, ErrorMap errorMap) throws BTSLBaseException {

		String methodName = "uploadAndReadGenericFileForUserMovement";
		if (log.isDebugEnabled()) {
			LogFactory.printLog(methodName, "Entered", log);
			log.debug(methodName, "fileDetailsMap " + fileDetailsMap + " headerRow" + headerRow );
		}
		this.fileDetailsMap = fileDetailsMap;
		
		//validate fileDetailsMap data
		// fileType, fileName, fileAttachment and serviceKeyword is mandatory
		validateFileDetailsMap(headerRow);  // throws exception
	    
		this.base64val = fileDetailsMap.get(PretupsI.FILE_ATTACHMENT);
		this.requestFileName = fileDetailsMap.get(PretupsI.FILE_NAME);
		LinkedHashMap<String, List<String>> responseMap = new LinkedHashMap<String, List<String>>();
		filePathCons = Constants.getProperty("DownloadUserMigrationFileNamePrefix");
		validateFilePathCons(filePathCons);
		
		String filePathConstemp = filePathCons + "temp/";        
		createDirectory(filePathConstemp);
		

		setFileNameWithExtention();
		
		filepathtemp = filePathConstemp + fileNamewithextention;   // C:/apache-tomcat-8.0.321/logs/

		byte[] base64Bytes = decodeFile(base64val);
		log.debug("filepathtemp:", filepathtemp);
		log.debug("base64Bytes:", base64Bytes);
		
		String fileSize = Constants.getProperty("VOMS_MAX_FILE_LENGTH");
		validateFileSize(fileSize, base64Bytes);  // throws exception
		
		writeByteArrayToFile(filepathtemp, base64Bytes);

		try 
		{
            List<String> fileHeaderList = new ArrayList<String>();
			String fileValueSeparator = Constants.getProperty("BL_VOMS_FILE_SEPARATOR");
			if (BTSLUtil.isNullorEmpty(fileValueSeparator)) 
			{
				log.error(methodName, "fileValueSeparator is null in Constants.props");
				 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_SEPERATOR_IN_CONSTANTS,
				 PretupsI.RESPONSE_FAIL,null); 
			}
	
			List<String> fileValueArray = readuploadedfile();
			if( fileValueArray.size() == 0) {
				log.error(methodName, "Uploaded file is empty");
				 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EMPTY_FILE,
				 PretupsI.RESPONSE_FAIL,null); 
			}

			String[] fileRecord = null;
			int headerColumnLength = 0;
			int fileRow = headerRow;
			int validRowCount = 0;
			String serviceKeyword = fileDetailsMap.get(PretupsI.SERVICE_KEYWORD);
			uniqueMsisdnWithDenom = new HashSet<String>();

			return fileValueArray;
			

		} catch (IOException io) {
			if(isFileWritten) {
				filedelete();
			}
			log.error(methodName, "Exceptin:e=" + io);
			log.errorTrace(methodName, io);
			io.printStackTrace();
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT,
					PretupsI.RESPONSE_FAIL, null);

		} catch(Exception e) {
			if(isFileWritten) {
				filedelete();
			}
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			if(!BTSLUtil.isNullorEmpty(e.getMessage())) {
				throw new BTSLBaseException(this, methodName, e.getMessage(),
						PretupsI.RESPONSE_FAIL, null);
			}else {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION,
						PretupsI.RESPONSE_FAIL, null);
			}
			
		}
		
		finally {
			if (log.isDebugEnabled()) {
				LogFactory.printLog(methodName, "Exited", log);
				log.debug(methodName, "responseMap " + responseMap );
			}
		}
	}
	public boolean validateBulkModifyChannelUser(String [] fileRecord,int fileRow, ErrorMap errorMap) {
		boolean isValidRow = true;
		String message = null;
		String errorCode = null;
		RowErrorMsgLists rowErrorMsgListsObj = new RowErrorMsgLists();
		ArrayList<MasterErrorList> masterErrorLists = new ArrayList<MasterErrorList>();
		Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
		if(BTSLUtil.isNullorEmpty(fileRecord[0])) 
		{
			isValidRow = false;
			errorCode = PretupsErrorCodesI.EXTSYS_BLANK;
			message = RestAPIStringParser.getMessage(locale, errorCode, new String[] { "Row" });
			createFailureResponseForEachCol(masterErrorLists, message, errorCode);
	    	final ArrayList fileErrorList = new ArrayList();

         	MasterErrorList err = new MasterErrorList();

         	err.setErrorCode(PretupsErrorCodesI.EXTSYS_BLANK);
         	err.setErrorMsg(message);
			ArrayList allRowErrorList = new ArrayList(); allRowErrorList.add(String.valueOf(fileRow)); allRowErrorList.add(message); fileErrorList.add(allRowErrorList);
//			singleRowError.getMasterErrorList().add(err);
	    	errorMap.setRowErrorMsgLists(allRowErrorList);
			
		}
		return isValidRow;
	}
}

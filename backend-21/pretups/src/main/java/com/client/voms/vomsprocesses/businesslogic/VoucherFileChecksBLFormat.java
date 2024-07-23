/* @# VoucherFileChecksBLFormat.java
 * This class is the controller class Voucher Upload Process.
 *
 *	   Created on 				Created by					History
 *	--------------------------------------------------------------------------------
 * 		19/09/16			  	Mahindra Comviva		   Initial creation
 * --------------------------------------------------------------------------------
 *  Copyright(c) 2016 Mahindra Comviva .
 *  This file process the Grameen phone specific EVD file parsing 
 */
package com.client.voms.vomsprocesses.businesslogic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.spec.AlgorithmParameterSpec;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsprocesses.util.VoucherFileChecksI;
import com.btsl.voms.vomsprocesses.util.VoucherFileUploaderUtil;
import com.btsl.voms.vomsprocesses.util.VoucherUploadVO;
import com.btsl.voms.voucher.businesslogic.VomsSerialUploadCheckVO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;


public class VoucherFileChecksBLFormat implements VoucherFileChecksI {
	private static String className="VoucherFileChecksBLFormat";
	private int pinLenght=0;
	private int fileLineCount=0;
	private  String fileValueSeparator=null;
	private String fixedCurrencyValue=null;
	private  String vomsfiledateformat=null;
	private  int numberOfRecordsEntered=0;
	private String productID=null;
	private String filePath=null;
	private String fileName = null;
	private ChannelUserVO channelUserVO=null;
	private Date currentDate=null;
	private ArrayList<VomsVoucherVO> voucherArr=null;
	private VoucherUploadVO voucherUploadVO=null;
	private ArrayList<String> fileDataArr = null;
	private  String startingRecord=null;
	private  String endingRecord=null;
	private int vomsFaceValue = 0;
	private Date expiryDate = null; 
	private String venderName= null; 
	private  int allowedNumberofErrors=0;
	private VomsSerialUploadCheckVO vomsSerialUploadCheckVO=null;
	private String fileEncryAllowStatus=null;
	private static String fileEncrykey=null;
	private String batchID=null;
	private static Log log = LogFactory.getLog(VoucherFileChecksBLFormat.class.getName());
	
	static final String EXCEPTION = "Exception =";
	static final String GETTINGEXCEPTION = "Getting Exception =";
	static final String NODATAFOUND = " No Data Found";
	static final String RECORDNUMBER = " Record Number = ";
	
	
	@Override
	public void loadConstantValues() throws BTSLBaseException {

		final String methodName = "loadConstantValues";
		final String classMethodName="VoucherFileChecksBLFormat[loadConstantValues]";
		if(log.isDebugEnabled())
			log.debug(methodName," Entered the loadContantsValues.....");
		try{
              
               try
               {
            	    pinLenght = Integer.parseInt(Constants.getProperty("VOM_PIN_LENGHT"));
 			        if(log.isDebugEnabled())
 			        	log.debug(className," loadConstantValues   VOM_PIN_LENGHT "+pinLenght);	
                }
               catch (Exception e)
               {
             	    log.errorTrace(methodName,e);
   				    log.error(methodName,"Invalid value for VOM_PIN_LENGHT  in Constant File ");
   				    EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,classMethodName,"","","","Invalid value for VOM_PIN_LENGHT in Constant File for voucher upload process");
   				    throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
               }
              try
              {
                	fileValueSeparator= Constants.getProperty("BL_VOMS_FILE_SEPARATOR");
  			        if(log.isDebugEnabled())
  			        	log.debug(className," loadConstantValues   BL_VOMS_FILE_SEPARATOR "+fileValueSeparator);	
              }
              catch (Exception e)
              {
              	     log.errorTrace(methodName,e);
    				 log.error(methodName,"Invalid value for BL_VOMS_FILE_SEPARATOR  in Constant File ");
    				  EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,classMethodName,"","","","Invalid value for BL_VOMS_FILE_SEPARATOR in Constant File for voucher upload process");
    				 throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
              }
              try
              {
                 	fixedCurrencyValue= Constants.getProperty("BL_VOMS_FIXED_CURRENCY_VALUE");
   			        if(log.isDebugEnabled())
   			        	log.debug(className," loadConstantValues   BL_VOMS_FIXED_CURRENCY_VALUE "+fixedCurrencyValue);	
              }
              catch (Exception e)
              {
               	     log.errorTrace(methodName,e);
     				 log.error(methodName,"Invalid value for BL_VOMS_FIXED_CURRENCY_VALUE  in Constant File ");
     				  EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,classMethodName,"","","","Invalid value for BL_VOMS_FIXED_CURRENCY_VALUE in Constant File for voucher upload process");
     				 throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
              }
              try
              {
            	  vomsfiledateformat= Constants.getProperty("BL_VOUCHER_EXPIRY_DATE_FORMAT");
            	  if(log.isDebugEnabled())
            		  log.debug(className," loadConstantValues   BL_VOUCHER_EXPIRY_DATE_FORMAT "+fixedCurrencyValue);	
              }
              catch (Exception e) 
              {
            	  log.errorTrace(methodName,e);
            	  log.error(methodName,"Invalid value for BL_VOUCHER_EXPIRY_DATE_FORMAT  in Constant File ");
            	  EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,classMethodName,"","","","Invalid value for BL_VOUCHER_EXPIRY_DATE_FORMAT in Constant File for voucher upload process");
            	  throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
              }
              try
              {
            	  allowedNumberofErrors = Integer.parseInt(Constants.getProperty("VOMS_TOTAL_ALLOWED_ERRORS"));
            	  if(log.isDebugEnabled())
            		  log.debug(className+" "," loadConstantValues   VOMS_TOTAL_ALLOWED_ERRORS "+allowedNumberofErrors);
              }
              catch(Exception e)
              {
            	  log.errorTrace(methodName,e);
            	  allowedNumberofErrors  = 0;
            	  log.info("loadConstantValues"," Total number of error (Entry VOMS_TOTAL_ALLOWED_ERRORS) not found in Constants . Thus taking default values as 0");
              }
              try
              {
            	  fileEncryAllowStatus= Constants.getProperty("BL_EVD_FILE_ENCRY_ALLOW");
            	  if(log.isDebugEnabled())
            		  log.debug("VoucherFileChecks"," loadConstantValues   BL_EVD_FILE_ENCRY_ALLOW "+fixedCurrencyValue);	
            	  if(BTSLUtil.isNullString(fileEncryAllowStatus))
            		  throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
              }
              catch (Exception e) 
              {
            	  log.errorTrace(methodName,e);
            	  log.error(methodName,"Invalid value for BL_EVD_FILE_ENCRY_ALLOW  in Constant File ");
            	  EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,classMethodName,"","","","Invalid value for BL_EVD_FILE_ENCRY_ALLOW in Constant File for voucher upload process");
            	  throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
              }
              try
              {
            	  fileEncrykey= Constants.getProperty("BL_EVD_FILE_ENCRY_KEY");
            	  if(log.isDebugEnabled())
            		  log.debug("VoucherFileChecks"," loadConstantValues   BL_EVD_FILE_ENCRY_KEY "+fixedCurrencyValue);	
            	  if(BTSLUtil.isNullString(fileEncrykey))
            		  throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
              }
              catch (Exception e)
              {
            	  log.errorTrace(methodName,e);
            	  log.error(methodName,"Invalid value for BL_EVD_FILE_ENCRY_KEY  in Constant File ");
            	  EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,classMethodName,"","","","Invalid value for BL_EVD_FILE_ENCRY_KEY in Constant File for voucher upload process");
            	  throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
              }
		 }
		catch(BTSLBaseException be)
		{
			log.errorTrace(methodName,be);
			log.error(methodName," BTSLBaseException be = "+be.getMessage());
            throw be;
		}
		//end of catch-BTSLBaseException
	    catch(Exception e)
	    {
	    	log.errorTrace(methodName,e);
	        log.error(methodName," Exception e="+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,classMethodName,"","","","Exception while loading the constant values from the Constants.prop file "+e.getMessage());
			throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
		}//end of catch-Exception
	    finally
	    {
			/*
			 * 
			 * */
	    }//end of finally
		
	}
	@Override
	public VoucherUploadVO validateVoucherFile() throws BTSLBaseException,Exception {
		final String methodName="validateVoucherFile";
		final String classMethodName="VoucherFileChecksBLFormat[validateVoucherFile]";
		int minSerialLength=0;
		int maxSerialLength=0;
		String fileData = null;
		String[] fileRecord=null;
		int runningErrorCount = 0;
		int runningFileRecordCount = 0;
		String dataValueSerial =null;
		boolean firstOnly=true;
		long previousSerialNo = 0L;
		long presentSerialNo = 0L;
		String dataValuePIN =null;
		ArrayList<String> pinList = new ArrayList<String>();
		String encryptedPin=null;
		int loopStartCount=0;
		Connection con = null;
		MComConnectionI mcomCon = null;
		try{
			
			voucherArr=new ArrayList<VomsVoucherVO>();
			minSerialLength=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MIN_LENGTH))).intValue();
			maxSerialLength=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MAX_LENGTH))).intValue();
			if(PretupsI.YES.equalsIgnoreCase(fileEncryAllowStatus))
				loopStartCount=1;
			for(int loop=loopStartCount;loop<fileDataArr.size();loop++)	
			{
				fileData = null;
				fileData = (String)fileDataArr.get(loop);
				fileRecord = fileData.split(fileValueSeparator);
				if(log.isDebugEnabled())
					log.debug(methodName, " Entered ::" +"fileData ::"+ fileData +" fileRecord ::" +fileRecord);
				if(fileRecord.length == 0)
				{
				  log.error(methodName,RECORDNUMBER+runningFileRecordCount+ NODATAFOUND);
					runningErrorCount++;
					continue;
				}
				// Garbage Handling
				if(fileRecord.length >0 && fileRecord.length <5)
				{
					log.error(methodName," file "+RECORDNUMBER+runningFileRecordCount+  NODATAFOUND);
					continue;
				}
				
	            if(log.isDebugEnabled())
	            	log.debug(methodName," fileRecord ::" +  fileRecord.length + "fileRecord[0] :: " + fileRecord[0]+ "fileRecord[1] :: " + fileRecord[1]);
				
			
	        	if(runningErrorCount>allowedNumberofErrors)
				{
					log.error(methodName," Total Number of error ("+runningErrorCount+") in the File ("+fileName+") exceeds the user specified error number= "+allowedNumberofErrors);			
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,classMethodName,"","","","The number of error ("+runningErrorCount+") in the file exceed the user specified value ("+allowedNumberofErrors+")");
					throw new BTSLBaseException(className+ "",methodName,PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);//"Total number of error in the file exceed the specified number of error"					
				}
	            
				numberOfRecordsEntered++;
				runningFileRecordCount++;//keeps record of number of records in file
				
				
				dataValueSerial=fileRecord[1];

				if(BTSLUtil.isNullString(dataValueSerial))
				{
					log.error(methodName,RECORDNUMBER+runningFileRecordCount+ " Serial Number Not Valid");
					runningErrorCount++;
					continue;
				}
				else if(!VoucherFileUploaderUtil.isValidDataLength(dataValueSerial.length(),minSerialLength,maxSerialLength))
				{
					log.error(methodName,RECORDNUMBER+runningFileRecordCount+ " Serial Number Not Valid");
					runningErrorCount++;

				}

				//on the first serial number, length will be validated against min and max values
				//then the min and max values will be set to length of first serial number
				//this will be done for first record only
				else if (firstOnly)
				{
				    minSerialLength=dataValueSerial.length();
				    maxSerialLength=dataValueSerial.length();
				    firstOnly=false;
				}
				//this if-else construct checks for the consequitivity of the serial numbers 
				// initially previousSerialNo and presentSerialNo are both 0
				if(previousSerialNo==0 && presentSerialNo ==0)
					previousSerialNo =Long.parseLong(dataValueSerial);//checks for the first time and set the value
				else
					presentSerialNo =Long.parseLong(dataValueSerial);
				if(presentSerialNo !=0)
				{
					// checks for the consecutivness of the Serial number 
					if(previousSerialNo+1!=presentSerialNo)
					{
						runningErrorCount++;
						log.error(methodName,RECORDNUMBER+runningFileRecordCount+ " Serial Numbers are not Continous");
						EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,className +"["+methodName+"]","","","","The serial number ("+presentSerialNo+") is not consecutive");

						throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_DUPLICATE_SERIAL_NO_ERROR);
					}
					previousSerialNo++;
				}
				dataValuePIN=fileRecord[0];
				if(BTSLUtil.isNullString(dataValuePIN))
				{
					log.error(methodName,RECORDNUMBER+runningFileRecordCount+ " PIN Not Valid");
					runningErrorCount++;
					continue;
				}
				//Above we have validated the pin length in the header with system preference
				//so here just validating, pin length against header pin length
				else if(!VoucherFileUploaderUtil.isValidDataLength(dataValuePIN.length(),((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MIN_LENGTH))).intValue(),((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MAX_LENGTH))).intValue()))
				{
					log.error(methodName,RECORDNUMBER+runningFileRecordCount+ " PIN Not Valid");
					runningErrorCount++;
					continue;
				}
				//this condition checks for the uniqueness of the pin in the file. Adds the pin in the 
				// arraylist pinList and before adding next time searches in this list first
				if(pinList.isEmpty())
				{
					pinList.add(dataValuePIN);
				}
				else 
				{
					if(pinList.contains(dataValuePIN))
					{
						log.error(methodName,RECORDNUMBER+runningFileRecordCount+ " PIN Not Unique");
						runningErrorCount++;
						continue;
					}
					pinList.add(dataValuePIN);
				}
				try
				{
					encryptedPin=VomsUtil.encryptText(dataValuePIN);
				}
				catch(Exception e)
				{
					log.errorTrace(methodName,e);
					log.error(methodName,RECORDNUMBER+runningFileRecordCount+ " Not able to encrypt PIN");
					runningErrorCount++;
					continue;						
				}
				
				//if the record is valid, prepare the 2D array with the parsed values
				if(runningFileRecordCount==1)
				{
            		startingRecord = dataValueSerial;//set only for the first time
             		int multiplyFactor=100/((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
    				String productIDFromQty = null;
    				vomsFaceValue= Integer.parseInt(fileRecord[2].toString())/multiplyFactor;
    				
    				mcomCon = new MComConnection();con=mcomCon.getConnection();
    				if(con==null){
    					log.error(methodName,"Database connection establishing error");
    					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,classMethodName,"","","","Database connection establishing error ");
    				}
    		    	productIDFromQty = new VomsVoucherDAO().loadProductIDFromMRP(con, vomsFaceValue);
    		    	if(BTSLUtil.isNullString(productIDFromQty))
    				{
    		    		log.error(methodName,"Product ID dose no exist for face value "+vomsFaceValue);
    					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,classMethodName,"","","","Product ID dose no exist for face value "+vomsFaceValue);
    					throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.PRODUCT_NOT_EXIST);//"Total number of records in the file are different from the one specified"
    				}
    		    	
    		    	if(!BTSLUtil.isNullString(productID) && !productID.equalsIgnoreCase(productIDFromQty))
    				{
    		    		log.error("readFileHeader","Product ID given by user from console ::" +productID+" and in Voucher file header ::" +productIDFromQty+ " are not matching ");
    					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"VoucherFileChecksBLFormat[readFileHeader]","","","","Product ID given by user from console ::" +productID+" and in Voucher file header ::" +productIDFromQty+ " are not matching ");
    					throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.PRODUCT_NOT_EXIST);//"Total number of records in the file are different from the one specified"
    				}
    		    	voucherUploadVO.setProductID(productIDFromQty);
    		    
    		    	
				}
				endingRecord = dataValueSerial;
				String tempExpiryDate;
				
				tempExpiryDate=fileRecord[4];
				if(BTSLUtil.isNullString(tempExpiryDate))
				{
					log.error(methodName,RECORDNUMBER+runningFileRecordCount+ " Expiry Date Not Valid");
					runningErrorCount++;
					continue;
				}
				else 
				{
					currentDate = new Date();
		    	
					if(!BTSLUtil.isNullString(tempExpiryDate))
					{
						try
						{
							expiryDate = BTSLUtil.getDateFromDateString(tempExpiryDate,vomsfiledateformat);
							if(expiryDate.before(currentDate))
							{
					    	log.error(methodName," Expiry date of vouchers is already expired.");
							}
						}
						catch(Exception e)
						{
							log.errorTrace(methodName,e);
							log.error(methodName," Expiry-date Not Valid");
						}
					}
				}
				populateValuesInVoucherList(dataValueSerial,encryptedPin,voucherUploadVO.getProductID(),expiryDate);
				
			}//end for loop
			voucherUploadVO.setFromSerialNo(startingRecord);
			voucherUploadVO.setToSerialNo(endingRecord);
			voucherUploadVO.setActualNoOfRecords(voucherArr.size());
			voucherUploadVO.setVoucherArrayList(voucherArr);
			voucherUploadVO.setNoOfRecordsInFile(numberOfRecordsEntered);
			voucherUploadVO.setMrp(String.valueOf(vomsFaceValue));
		}
		catch(BTSLBaseException be)
		{
			log.errorTrace(methodName,be);
			log.error(methodName," : BTSLBaseException "+be);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+"["+methodName+"]","","","",be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			log.errorTrace(methodName,e);
			log.error(methodName,EXCEPTION+e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+"["+methodName+"]","","","",e.getMessage());
			throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
			
		}
		finally
		{
			if (mcomCon != null) {
				mcomCon.close("VoucherFileChecksBLFormat#validateVoucherFile");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting");
			}
		}

       return voucherUploadVO;
	
	}
	@Override
	public void getFileLength(VoucherUploadVO pvoucherUploadVO) throws BTSLBaseException, Exception {
    	final String methodName = "getFileLength";
		if(log.isDebugEnabled())
			log.debug(methodName, " Entered with pvoucherUploadVO="+pvoucherUploadVO);
		int lineCount = 0;
		String fileData = null;
		BufferedReader inFile =null;
		
		try
		{	 getValuesFromVO(pvoucherUploadVO);
			fileDataArr = new ArrayList<String>();
			 File srcFile = null;
			//creates the actual path of the file
			if(BTSLUtil.isNullString(filePath))
				srcFile = new File(fileName);
			else	
				srcFile = new File(filePath+File.separator+fileName);
			 	if(log.isDebugEnabled())
			 		log.debug(methodName," Starting processing to get the number of records for source File Path = "+srcFile+" File Name"+fileName);
			    batchID=getBatchID(fileName);
			//creates a new bufferedreader to read the Voucher Upload file		
			 if(PretupsI.YES.equalsIgnoreCase(fileEncryAllowStatus))	
				 inFile = decrypt(new FileInputStream(srcFile),srcFile.getAbsolutePath());
			 else
				 inFile = new BufferedReader(new java.io.FileReader(srcFile));
			 fileData = null;
			while((fileData = inFile.readLine())!=null)
			{
				//this is used to check if the line is blank ie the record is blank
				if(BTSLUtil.isNullString(fileData))
				{
					log.error(methodName,RECORDNUMBER+lineCount+ NODATAFOUND);
					continue;
				}				
				++fileLineCount;
				fileDataArr.add(fileData);				
			}	
		}//end of try block
		catch(IOException io)
		{
			log.errorTrace(methodName,io);
			log.error(methodName,"The file ("+fileName+")could not be read properly to get the number of records" );			
			EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,className+"[getFileLength]","","","","The file ("+fileName+")could not be read properly to get the number of records");
			throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);					
		}//end of IOException
		catch(Exception e)
		{
			log.errorTrace(methodName,e);
			log.error(methodName,EXCEPTION+e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+"[getFileLength]","","","",e.getMessage());
			throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);
		}//end of Exception	
		finally
		{
			try {
				
				if(inFile!=null)
					inFile.close();
				} 
			catch (Exception e) 
			{
				log.errorTrace(methodName,e);
				log.error(methodName," Exception while closing input stream = "+e);
			}
		}//end of finally
		
	}

	
	@Override
	public VomsSerialUploadCheckVO populateVomsSerialUploadCheckVO(Connection pCon) throws BTSLBaseException, Exception {
		 final String methodName = "populateVomsSerialUploadCheckVO";
		 
	     int addCount = 0;	
		 ArrayList voucherList = new ArrayList();
		 try
		 {	
			 voucherList = voucherUploadVO.getVoucherArrayList();
			 int length = voucherList.size();
			 VomsVoucherVO vomsvoucherVO = (VomsVoucherVO) voucherList.get(0);
			 String startSerialNO = vomsvoucherVO.getSerialNo();
			 vomsvoucherVO = (VomsVoucherVO) voucherList.get(length-1);
			 String endSerialNO = vomsvoucherVO.getSerialNo();
		
			 if(log.isDebugEnabled())
				 log.debug(methodName, " Entered ");
		 
			 vomsSerialUploadCheckVO = new VomsSerialUploadCheckVO();
			 vomsSerialUploadCheckVO.setStartSerialNo(startSerialNO);
			 vomsSerialUploadCheckVO.setEndSerialNO(endSerialNO);
			 vomsSerialUploadCheckVO.setDenomination(vomsFaceValue);
			 vomsSerialUploadCheckVO.setExpiryDate(expiryDate);
			 vomsSerialUploadCheckVO.setFileName(voucherUploadVO.getFileName());
			 vomsSerialUploadCheckVO.setUploadDate(currentDate);
			 vomsSerialUploadCheckVO.setCreatedBy(channelUserVO.getUserID());
			 vomsSerialUploadCheckVO.setCreatedOn(currentDate);
		
			 VomsVoucherDAO vomsVoucherDAO = new VomsVoucherDAO();
			 boolean status = vomsVoucherDAO.checkDuplicateSerialNO(pCon,vomsSerialUploadCheckVO);
			 if(status)
			 {
				 fileName = voucherUploadVO.getFileName();
				 String moveLocation=BTSLUtil.NullToString(Constants.getProperty("VOMS_VOUCHER_FILE_MOVE_PATH"));
				 VoucherFileUploaderUtil.moveFileToAnotherDirectory(fileName,filePath+File.separator+fileName,moveLocation);
				 log.error(methodName," The file details already exist in DB");
				 EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MINOR,"VoucherFileChecksBLFormat[populateVomsSerialUploadCheckVO]","","","","File has been uploaded before");
				 throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_ERROR_FILE_ALREADY_UPLOADED);//"The voucher file does not exists at the location specified"				
			 }
			 else
				 addCount = vomsVoucherDAO.insertFileValues(pCon,vomsSerialUploadCheckVO);	
			if(addCount <=0)
			{
				log.error(className+methodName," Not able to update Voucher serial upload check table ");
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"VoucherFileChecksBLFormat[populateVomsSerialUploadCheckVO]","","",""," The Voucher serial check table could not be updated");
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
			}			
		}
		catch(BTSLBaseException be)
		{
			log.errorTrace(methodName,be);
			throw be;
		}
		catch(Exception e)
		{
			log.errorTrace(methodName,e);
			log.error(className+methodName, EXCEPTION+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL, className+"[populateVomsSerialUploadCheckVO]","","","",GETTINGEXCEPTION+e.getMessage());
			throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
		}	
		finally
		{
			if(log.isDebugEnabled())
					log.debug(methodName, " Exiting");
		}
		
		return vomsSerialUploadCheckVO;

	}

	/**
	 * This method will get the values from voucher upload VO passed from controlling class to the global variables
	 * @param pvoucherUploadVO
	 * @throws BTSLBaseException
	 */
	private void getValuesFromVO(VoucherUploadVO pvoucherUploadVO) throws BTSLBaseException 
	{
		final String methodName = "getValuesFromVO";
		if(log.isDebugEnabled()) {
		
			log.debug(methodName, " Entered with  pvoucherUploadVO="+pvoucherUploadVO);
		}
		try
		{
			fileName=pvoucherUploadVO.getFileName();
			filePath=pvoucherUploadVO.getFilePath();
			productID=pvoucherUploadVO.getProductID();
			numberOfRecordsEntered=pvoucherUploadVO.getNoOfRecordsInFile();
			channelUserVO=pvoucherUploadVO.getChannelUserVO();
			currentDate=pvoucherUploadVO.getCurrentDate();
			voucherUploadVO=pvoucherUploadVO;
		}
		catch(Exception e)
		{
			log.errorTrace(methodName,e);
			log.error(className +"["+methodName+"]",EXCEPTION+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+"["+methodName+"]","","","",GETTINGEXCEPTION+e.getMessage());
			throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
		}	
		finally
		{
			if(log.isDebugEnabled())
				log.debug(methodName, " Exiting");
		}
	}

	/**
	 * This method populates the Voms Voucher VO that will be inserted in database
	 * @param pSerialNo
	 * @param pPin
	 * @param pproductID
	 * @throws BTSLBaseException
	 */
	private void populateValuesInVoucherList(String pSerialNo,String pPin,String pproductID,Date expiryDate) throws BTSLBaseException 
	{
		final String methodName = " populateValuesInVoucherList";
		if(log.isDebugEnabled())
			log.debug(methodName, " Entered with pSerialNo="+pSerialNo+" pPin="+pPin+" pproductID="+pproductID);
		try
		{
			VomsVoucherVO vomsVoucherVO=new VomsVoucherVO();
			vomsVoucherVO.setSerialNo(pSerialNo);
			vomsVoucherVO.setPinNo(pPin);
			vomsVoucherVO.setProductID(pproductID);
			vomsVoucherVO.setProductionLocationCode(channelUserVO.getNetworkID());
			vomsVoucherVO.setModifiedBy(channelUserVO.getUserID());
			vomsVoucherVO.setOneTimeUsage(PretupsI.YES);
			vomsVoucherVO.setStatus(VOMSI.VOUCHER_NEW);
			vomsVoucherVO.setCurrentStatus(VOMSI.VOUCHER_NEW);
			vomsVoucherVO.setModifiedOn(new Date());
			vomsVoucherVO.setCreatedOn(currentDate);
			vomsVoucherVO.setVenderName(venderName);
			vomsVoucherVO.SetSaleBatchNo(batchID);
			long time = expiryDate.getTime();
			vomsVoucherVO.setExpiryDate(new Date(time));
			voucherArr.add(vomsVoucherVO);
		}
		catch(Exception e)
		{
			log.errorTrace(methodName,e);
			log.error(className+ "[populateValuesInVoucherList]",EXCEPTION+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL, className+"[populateValuesInVoucherList]","","","",GETTINGEXCEPTION+e.getMessage());
			throw new BTSLBaseException(className+""," populateValuesInVoucherList ",PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
		}	
		finally
		{
			if(log.isDebugEnabled())
				log.debug(methodName, " Exiting ");
		}
	}
	/**
	 * Method is used to decrypt the file 
	 * @param is
	 * @param pfileName
	 * @return
	 * @throws BTSLBaseException
	 */
	private static BufferedReader decrypt(InputStream is, String pfileName) throws BTSLBaseException
	{
		final String methodName ="decrypt";
		Cipher dcipher;	
  		byte[] iv=new byte[] { 00, 00, 00, 00, 00, 00, 00, 00 };
    	BufferedReader br=null;
		AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
		try
		{
			DESKeySpec dks = new DESKeySpec(fileEncrykey.getBytes());
			SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
			SecretKey desKey = skf.generateSecret(dks);
			dcipher = Cipher.getInstance("DES/CBC/NoPadding");
			dcipher.init(Cipher.DECRYPT_MODE, desKey, paramSpec);
			br = new BufferedReader(new InputStreamReader(new CipherInputStream(is, dcipher)));
			return br;		
		}
		catch (java.security.InvalidAlgorithmParameterException e) {
			log.errorTrace(methodName,e);
		}
		catch (Exception e) {
			log.errorTrace(methodName,e);
		}
		
		return br;
	}
	private static String getBatchID(String pfileName) throws BTSLBaseException
	{
		final String methodName = "getBatchID";
		
		
		try{
	
			if(pfileName.contains("_")&&pfileName.contains("."))
            return pfileName.substring(pfileName.lastIndexOf('_')+1,pfileName.lastIndexOf('.'));
			else
			throw new BTSLBaseException(className," getBatchID  unable to get the BatchID from the file",PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);

		}
		 catch(Exception e)
		    {
			 log.errorTrace(methodName,e);
				log.error(methodName," Exception e="+e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VoucherFileChecksBLFormat[getBatchID]","","","","Exception while reading the value of batchid  from the file "+e.getMessage());
				
					throw new BTSLBaseException(className," getBatchID  unable to get the BatchID from the file",PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
			}//end of catch-Exception
		    finally {
			
		    }//end of finally
		}

}

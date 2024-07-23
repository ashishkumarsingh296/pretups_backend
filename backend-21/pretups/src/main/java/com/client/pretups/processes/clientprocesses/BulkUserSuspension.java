package com.client.pretups.processes.clientprocesses;

/**
 * BulkUserSuspension.java
 * Name                                 Date            History
 *------------------------------------------------------------------------
 * Hargovind Karki		              15/12/2015         Initial Creation
 *------------------------------------------------------------------------
 * Copyright (c) 2015 Mahindra Comviva.
 * This class is responsible for the Bulk User Suspension of the users.
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ListValueVO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.loadcontroller.LoadControllerCache;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.BulkUserSuspensionProcessLog;
import com.btsl.pretups.logging.ChannelUserLog;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.SessionInfoVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;


/**
 * @author sanjay.bind1
 *
 */
public class BulkUserSuspension

{ 
    private static Log _log = LogFactory.getLog(BulkUserSuspension.class.getName());
    private long _sleepTime; //Time interval for enquiry with C2STransfer table to get the final status.
    private int _numberOfScheduledDays;//Number of back days from which scheduled batch is picked up(till current date0
    private String _SCHDULE_TOP_UP_URL;//Recharge URL
    //private int _RST_NO_SLEEP_ENQUIRY;//Number of times scheduled process will enquire with C2STransfer table to get the final status.
    private int _RST_CON_REFUSE_COUNTER;//After this allowed number if consecutive failure occurs while creating connection process will abort.
	//private int _SMS_SERVER_CONNECT_TIMEOUT;//SMS server connect time out.
	//private int _SMS_SERVER_READ_TIMEOUT;//SMS server read time out.
    private String _instanceID; //Store the instanceID to load the details of running instance.
	private Date _date = null;
	private ProcessStatusVO _processStatusVO = null;
	private ProcessStatusDAO _processStatusDAO = null;
	
	boolean _processStatusOK = false;
	private static String loginIdOrMsisdn=null;
	private static String networkCode = null;
	//private File fileObject=null;
	
	private String fileName=null;
	
	
	
	//private BatchFileParserI _batchFileParserI=null;
	
	public BulkUserSuspension()
	{
	    super();
		_processStatusDAO = new ProcessStatusDAO();
		
	}
	
	/**
	 * Main Method
	 * @param String args
	 * @throws BTSLBaseException
	 */
	public static void main(String[] args)
	{
		
		final String methodName = "main";
		
		LogFactory.printLog(methodName, "BulkUserSuspension main method", _log);
		BulkUserSuspension bus = new BulkUserSuspension();
		try
		{	
		
			
			//System.out.println("2");
			if(args.length <3 || args.length >4)
			{
				//_log.info(methodName, "Usage : BulkUserSuspension [Constants file] [LogConfig file] [Batch Type=MSISDN/LOGIN]");
				_log.info(methodName, "Usage : BulkUserSuspension [Constants file] [LogConfig file] [Network Code] [Batch Type=MSISDN]");
				return;
			}
			File constantsFile = new File(args[0]);
		    //File constantsFile = new File("c://Constants.props");
			if(!constantsFile.exists())
			{
				
			    _log.debug(methodName, "BulkUserSuspension main() Constants file not found on location:: "+constantsFile.toString());
				return;
			}
			File logconfigFile = new File(args[1]);
			//File logconfigFile = new File("c://ProcessLogConfig.props");
			if(!logconfigFile.exists())
			{
				
				_log.debug(methodName, "BulkUserSuspension main() Logconfig file not found on location:: "+logconfigFile.toString());
				return;
			}
			
			
			if (BTSLUtil.isNullString(args[2]))// Network code check
			{
				_log.debug("BulkUserSuspension ::", " Network code is missing ");
				return;
			}
			else
			{
				networkCode=new String(args[2]);
			}
			
			//check batch type is null or not
			if(args.length==3)
			{
				loginIdOrMsisdn=PretupsI.LOOKUP_MSISDN;
			}
			else
			{
				loginIdOrMsisdn = args[3].trim();
			    
			}
			if(!(loginIdOrMsisdn.equalsIgnoreCase(PretupsI.LOOKUP_LOGIN_ID) 
			        || loginIdOrMsisdn.equalsIgnoreCase(PretupsI.LOOKUP_MSISDN) 
			        ))
			{
			    _log.info(methodName, "BulkUserSuspension main() Invalide batch type::[loginIdOrMsisdn =LOGIN/MSISDN] ");
				return;
			}
			    
		    ConfigServlet.loadProcessCache(constantsFile.toString(),logconfigFile.toString());
		    //load service keyword cache
		    ServiceKeywordCache.loadServiceKeywordCacheOnStartUp();
			//LoadControllerCache.refreshInstanceLoad(Constants.getProperty("DEF_INSTANCE_ID"));
			_log.info(methodName, "BulkUserSuspension After loading Instance Load ");
			LoadControllerCache.refreshNetworkLoad();
			_log.info(methodName, "BulkUserSuspension After loading Network Load ");
			bus.processFile(loginIdOrMsisdn,networkCode);
			
		}
		catch(BTSLBaseException be)
		{
		    _log.errorTrace(methodName, be);
		    _log.errorTrace(methodName,be);
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName, e);
		    _log.errorTrace(methodName,e);
		}
		finally
		{
			/*Locale locale=new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
			String  senderMessage="Bulk User Suspension process executed successfully. Total Record count="+totalProceedRecord+" Success Count="+totalSuccessCount+" Failed Count="+(totalProceedRecord-totalSuccessCount);
			
			String msisdnString=new String(Constants.getProperty("adminmobile"));
			String [] msisdn =msisdnString.split(",");
			for(int i=0; i<msisdn.length;i++)
			{
				PushMessage pushMessage=new PushMessage(msisdn[i],senderMessage,null,null,locale);
				pushMessage.push();
			}*/
			
			ConfigServlet.destroyProcessCache();
		}
	}
	
	
	/**
	 * This method is used to get the values from constants.prop
	 * @throws BTSLBaseException
	 */
	
	
	private void loadConstantValues() throws BTSLBaseException
	{
	    final String methodName="loadConstantValues";
		try
	    {
	        LogFactory.printLog(methodName, "Entered", _log);
		    //sleepTime is used to wait for enquiry with the C2STransfer after getting the 'Validation response'.
		    String str =Constants.getProperty("RST_MAX_DAYS_SCH_BATCH");//Number of back days from which scheduled batch is picked up(till current date0
		    _numberOfScheduledDays = Integer.parseInt(str);
		    //String noSleeptimes = Constants.getProperty("RST_NO_SLEEP_ENQUIRY");
		    //_RST_NO_SLEEP_ENQUIRY = Integer.parseInt(noSleeptimes);
		    String connectionRefuseCounter =Constants.getProperty("RST_CON_REFUSE_COUNTER");
		    _RST_CON_REFUSE_COUNTER=Integer.parseInt(connectionRefuseCounter);     
		    _sleepTime	= Long.parseLong(Constants.getProperty("RST_SLEEP_ENQUIRY_TIME")); 
		    _SCHDULE_TOP_UP_URL = Constants.getProperty("SCHDULE_TOP_UP_URL");
			//_SMS_SERVER_CONNECT_TIMEOUT=Integer.parseInt(Constants.getProperty("SMS_SERVER_CONNECT_TIMEOUT"));
			//_SMS_SERVER_READ_TIMEOUT=Integer.parseInt(Constants.getProperty("SMS_SERVER_READ_TIMEOUT"));
			//_instanceID = Constants.getProperty("RST_INSTANCE_ID");
			_instanceID = Constants.getProperty("DEF_INSTANCE_ID");
			
	    }
	    catch(Exception e)
	    {
	        _log.errorTrace(methodName,e);
	        _log.error("loadConstantValues","Exception e="+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"RestrictedSubscriberTopUp[loadConstantValues]","","","","Exception:"+e.getMessage());
	        throw new BTSLBaseException("RestrictedSubscriberTopUp","loadConstantValues",e.getMessage());
	    }
	    finally
	    {
	        LogFactory.printLog(methodName, "Exiting: numberOfScheduledDays="+_numberOfScheduledDays+" sleepTime="+_sleepTime +" _RST_CON_REFUSE_COUNTER"+_RST_CON_REFUSE_COUNTER+"_SCHDULE_TOP_UP_URL"+_SCHDULE_TOP_UP_URL+" _instanceID = "+_instanceID, _log);
	    }
	    
	}
	/**
	 * This method is used to check the Process Status.
 	 * If there is no entry for corresponding processID STOP the process.
	 * If the status is 'C' set processStatusOK as True.
	 * If the status is 'U' check the exipiry if it expires,update status as 'U' and set processStatusOK as True.
	 * In case of expiry send the alarm also.
	 * @param	Connection p_con
	 * @throws	BTSLBaseException
	 */
	private void processBulkSuspension(Connection con,String loginIdOrMsisdnValue,String netwrkcode) throws BTSLBaseException
	{	
 	    
		int totalProceedRecord=0;  
		int totalSuccessCount=0;
		
		
	    final String methodName="processBulkSuspension";
	    LogFactory.printLog(methodName, "Entered", _log);
	    String loginIdOrMsisdnV=loginIdOrMsisdnValue;
	    String bulkSuspensnCode=null;
        String delimiter = Constants.getProperty("DelimiterForUploadedFileForOffileBulkChnlUserUserSuspension");
		
		String filePath = Constants.getProperty("UploadFileForOffileBulkChnlUserUserSuspensionPath");
		String contentsSize = Constants.getProperty("MAX_RECORDS_IN_FILE");
		String noOfBulkSuspnFiles = Constants.getProperty("NO_OF_BULK_FILE_CONTENTS");
		String filePathAndFileName = filePath+fileName; // path if the file with file name
		
		
		ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		FileReader fileReader = null; //file reader
		BufferedReader bufferReader = null;
		
		File file = null;
		
		
		StringBuilder invalidString = new StringBuilder();
		String invalidStr;
		
		int forDisplayMsg = 0;
		boolean invalidStringFromDao;
		String tempStr = null;
		String filteredMsisdn=null;
		String msisdnPrefix;
		NetworkPrefixVO networkPrefixVO = null;
		//String networkCode;
		BTSLMessages btslMessage = null;
		String msisdnOrLoginID;
		//ArrayList MobileOrId = new ArrayList(); //list to store the contents of the file
		
		Map prepareStatementMap=new HashMap();
		ChannelUserWebDAO channelUserWebDAO=null;
		
		//boolean fileMoved = false;
		
		
		try
		{
			channelUserWebDAO=new ChannelUserWebDAO();
		    // check the DELIMITER defined in the Constant Property file for Blank
		    if(BTSLUtil.isNullString(delimiter))
		    {
				LogFactory.printLog(methodName, "Delimiter not defined in Constant Property file", _log);
			    throw new BTSLBaseException(this, methodName, "user.uploadFileForChUserUnregisterBulk.error.delimitermissingincons","uploadFileForChUserUnregisterBulk");
		    }
		    else
		    {
			    // check the FILEPATH defined in the Constant Property file for Blank
		        if(BTSLUtil.isNullString(filePath))
		        {
					LogFactory.printLog(methodName, "File path not defined in Constant Property file", _log);
				    throw new BTSLBaseException(this, methodName, "user.uploadFileForChUserUnregisterBulk.error.filepathmissingincons","uploadFileForChUserUnregisterBulk");
		        }
		        else
		        {
				    // check the NO_OF_CONTENTS defined in the Constant Property file for Blank
			        if(BTSLUtil.isNullString(contentsSize))
			        {
						LogFactory.printLog(methodName, "Contents size of the file not defined in Constant Property file", _log);
					    throw new BTSLBaseException(this, methodName, "user.uploadFileForChUserUnregisterBulk.error.contentssizehmissingincons","uploadFileForChUserUnregisterBulk");
			        }   
		        }
		    }
		    
		    StringTokenizer startparser = null;
			
			//take out each string from the file & put it in a array list 
			LogFactory.printLog(methodName, "Initializing the fileReader, filepath : "+filePathAndFileName, _log);			
			
			// Code To read multiple files and add their data to the list for processing
			
			File dir= new File(filePath);
    /*        String[] fileArr={};
            if(dir.exists()) {
				fileArr=dir.list();
			} else
            {
				_log.debug(methodName,"Directory does not exist "+filePath);
                throw new BTSLBaseException(methodName,methodName,PretupsErrorCodesI.DIR_NOT_EXIST);
            }
      */      
            //////////////////////////////////////////////////////////////////
			
			//File dir= new File(filePath);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -1);
			String yyyy=""+cal.get(Calendar.YEAR);
			
			String mm=""+(cal.get(Calendar.MONTH)+1);
			if(mm.length()==1){
				mm="0"+mm;
			}
			String dd=""+cal.get(Calendar.DAY_OF_MONTH);
			if(dd.length()==1){
				dd="0"+dd;
			}
			
			final String _previousDay=yyyy+mm+dd;
			FilenameFilter filter=new FilenameFilter(){
				public boolean accept(File dir, String name){
					
					String regex="USER_SUSPEND_"+_previousDay+Constants.getProperty("BULK_USER_SUSPEND_FILE_EXT");
					if (name.matches(regex)){
						return true;
					}
					return false;
				}
			};
			String[] fileArr={};
			if(dir.exists()) {
				fileArr=dir.list(filter);
			} else
			{
				_log.error(methodName,"Directory does not exist "+filePath);
			     EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,methodName,"","","","Directory does not exist .............");
		
				throw new BTSLBaseException("BulkUserSuspension",methodName,PretupsErrorCodesI.DIR_NOT_EXIST);
			}
			
			if(fileArr.length==0){
				_log.error(methodName,"No BulkUserSuspension Files are present in directory folder for date:"+_previousDay+"  or file format should be like USER_SUSPEND_yyyymmdd where yyyymmdd should be SYSDATE-1 date ");
			     EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,methodName,"","","","No BulkUserSuspension Files are present in directory folder for date:"+_previousDay+"  or file format should be like USER_SUSPEND_yyyymmdd where yyyymmdd should be SYSDATE-1 date ");
		
				 throw new BTSLBaseException("BulkUserSuspension",methodName,PretupsErrorCodesI.FILES_NOT_EXIST);
				
			}
            
            //////////////////////////////////////////////////////////////////
            
            
            
            
            
            
            
           /* if(fileArr.length > Integer.parseInt(noOfBulkSuspnFiles))
			{    
				if(_log.isDebugEnabled())
					_log.debug(methodName,"No of File contents size  is not valid in constant properties file : "+fileArr.length);
			    throw new BTSLBaseException(this, methodName, "user.uploadFileForChUserUnregisterBulk.error.novalidcontentssize","uploadFileForChUserUnregisterBulk");
			}*/
            
            //for(int k=0;k<fileArr.length;k++)
            //{
                //fileName=fileArr[k];
                fileName="USER_SUSPEND_"+_previousDay+Constants.getProperty("BULK_USER_SUSPEND_FILE_EXT");
                boolean invalidfile =false;
                String Rejectdescription ="";
                String oldFilePath=filePath+fileName;
                String NewFilePath = Constants.getProperty("PATH_BULK_USER_SUSPENSION_OUT_FOLDER");
                List MobileOrIdEachFile = new ArrayList();
                List finalMobileOrIdList = new ArrayList<String>();
                //boolean duplicateFlag=false;
                //int lineNumber =0;
                List validList = new ArrayList();
                List invalidList = new ArrayList();
                Map<String, String> inValidHashMap = new HashMap<String, String>();
        		int recordCount=0;
                
                
                /*if (!validateFileName(fileName))
                {
                	if(_log.isDebugEnabled())
	    				_log.debug(methodName,"File is rejected due to invalid file name "+fileName);
                		invalidfile = true;
                		Rejectdescription="90006";
                		createAndMoveRejectedFileToFinalDirectory(fileName,oldFilePath,NewFilePath,Rejectdescription);
                		
                }
                
                if (!validateFileDate(fileName))
                {
                	if(_log.isDebugEnabled())
	    				_log.debug(methodName,"File is rejected due to invalid file date "+fileName);
                		invalidfile = true;
                		Rejectdescription="90002";
                		createAndMoveRejectedFileToFinalDirectory(fileName,oldFilePath,NewFilePath,Rejectdescription);
                		
                }
                
                if (!invalidfile)
                {
                	if (!validateFormat(fileName))
                	{
                	if(_log.isDebugEnabled())
	    				_log.debug(methodName,"File is rejected due to invalid file type "+fileName);
                	    invalidfile = true;
                		Rejectdescription="90001";
                		createAndMoveRejectedFileToFinalDirectory(fileName,oldFilePath,NewFilePath,Rejectdescription);
                	
                	}
                }*/
                //if (!invalidfile)
                //{
        			fileReader = new FileReader(""+filePath+fileName);
                
                if(fileReader != null)
    				bufferReader = new BufferedReader(fileReader);
    			else
    				bufferReader = null;
                if(bufferReader != null || bufferReader.ready()) // If File Not Blank Read line by Line
    			{
                	while((tempStr = bufferReader.readLine()) != null) // read the file until it reaches to end
    				{
                		recordCount=recordCount+1;
                		if (tempStr.isEmpty() && tempStr==null)
                    	{
                    		invalidfile = true;
                    		_log.debug(methodName,"Invalid File format 1  "+fileName);
        					Rejectdescription="90003";
        		    		createAndMoveRejectedFileToFinalDirectory(fileName,oldFilePath,NewFilePath,Rejectdescription);
        		    		break;
                    		
                    		
                    	}
    				    
    				    
    				    
    				    // method to check the primary validation -- blank line and 10 digit numeric only
    				    if (primaryValidation(tempStr,loginIdOrMsisdnV))
    				    	{
    				    		invalidfile = true;
    				    		Rejectdescription="90003";
    				    		createAndMoveRejectedFileToFinalDirectory(fileName,oldFilePath,NewFilePath,Rejectdescription);
    				    		break;
    				    	}
    				  
    				    if(recordCount > Integer.parseInt(contentsSize.trim()))
						{    
							LogFactory.printLog(methodName, "Max number of records per file exceeds: "+MobileOrIdEachFile.size(), _log);
							invalidfile = true;
							
							Rejectdescription="90004";
				    		createAndMoveRejectedFileToFinalDirectory(fileName,oldFilePath,NewFilePath,Rejectdescription);
				    		break;
						}	
    				    
    						
    				    if(!invalidfile)
		    			{
		    				//MobileOrId.add(tempStr); //add each string in the list
    				    	if (MobileOrIdEachFile.contains(tempStr))
    				    	{
    				    		invalidfile=true;
    				    		Rejectdescription="90005";
    				    		createAndMoveRejectedFileToFinalDirectory(fileName,oldFilePath,NewFilePath,Rejectdescription);
    				    		break;
    				    		
    				    	}
    				    	else
    				    	{
    				    		MobileOrIdEachFile.add(tempStr);
    				    	}	
    				    }
    						
    					
    					startparser = null;
    					tempStr = null;
    				}
    			}
            //} 
			 // If the file is pass all primary validation then check for secondary validation
            	if(!invalidfile)
    			{ 
            		
            		if(loginIdOrMsisdnV.equals(PretupsI.LOOKUP_LOGIN_ID))
    			    {
    					BulkUserSuspensionProcessLog.logPrint("Bulk User Suspension ===================== Processing starts for Login ID's =================");
            		
    			    }
            		else
            		{
            			BulkUserSuspensionProcessLog.logPrint("Bulk User Suspension ===================== Processing starts for MSISDN ID's =================");
            		}
            		
            		
            		String StrForEachFile = null;
            		fileReader = new FileReader(""+filePath+File.separator+fileName);
                    
                    if(fileReader != null)
        				bufferReader = new BufferedReader(fileReader);
        			else
        				bufferReader = null;
                    if(bufferReader != null || bufferReader.ready()) // If File Not Blank Read line by Line
        			{
        				
        				while((StrForEachFile = bufferReader.readLine()) != null) // read the file until it reaches to end
        				{
        					finalMobileOrIdList.add(StrForEachFile);
        				}
        			}	
            		LogFactory.printLog(methodName, "final list for secondary validation "+finalMobileOrIdList.size(), _log);
                
            		for (int i =0;i<finalMobileOrIdList.size();i++)
            		{
            			_log.debug("processBulkSuspensionDeleltionValidation","finalMobileOrIdList -------"+finalMobileOrIdList.get(i));
            		}
            		
            		
            		  bufferReader.close();
          			  fileReader.close();
    			
    			   
			
				
		    UserVO userVO = new UserVO();
		    List childExistList=new ArrayList();
			BTSLMessages sendBtslMessage =null;
			
			PushMessage pushMessage =null;
			String msisdn = null;
			ChannelUserVO channelUserVO = new ChannelUserVO();
			
			Date currentDate = new Date();
			int countStr=0;
			while(finalMobileOrIdList.size() != countStr)
			
			//for (int countStr=0; countStr<finalMobileOrIdList.size();countStr++)
			{
				
				msisdnOrLoginID = (String)finalMobileOrIdList.get(countStr);		
				countStr++;
				
				//**   Processing the Login ID's   **
				if(loginIdOrMsisdnV.equals(PretupsI.LOOKUP_LOGIN_ID))
			    {
					LogFactory.printLog(methodName, "Processing starts for Login ID's "+loginIdOrMsisdnV, _log);					
					
					//BulkUserSuspensionProcessLog.logPrint("Bulk User Suspension ===================== Processing starts for Login ID's =================");
					forDisplayMsg = 1;
					invalidStringFromDao = channelUserWebDAO.deleteOrSuspendChnlUsersInBulkForLoginID(con, msisdnOrLoginID, "SR",childExistList,PretupsI.SYSTEM,countStr, prepareStatementMap);// call method for delete the channel user on the basis of Login ID
					if(invalidStringFromDao)
					{    
					    invalidString.append(msisdnOrLoginID); // append the invalid Login ID in the string invalidString
					    invalidString.append(delimiter);
					    LogFactory.printLog(methodName, "invalid list  "+msisdnOrLoginID, _log);
					    invalidList.add(msisdnOrLoginID);
					    inValidHashMap.put(msisdnOrLoginID, "Not a valid Login ID/LoginID already Suspended");
					    //single line logger entry
					    BulkUserSuspensionProcessLog.log("FILE PROCESSING",PretupsI.SYSTEM,msisdnOrLoginID,countStr,"Not a valid Login ID","Fail",filePathAndFileName);
					   
					}    
					else
					{
						LogFactory.printLog(methodName, "login id : valid list  "+msisdnOrLoginID, _log);
					    channelUserVO = null;
					    validList.add(msisdnOrLoginID.trim()); // insert the valid Login ID in the validList
					    channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(con, msisdnOrLoginID, null, PretupsI.STATUS_NOTIN, "'N','C'");
					}
			    }    

				//**  Processing the MSISDN's  **
				else
				{
					LogFactory.printLog(methodName, "Processing starts for MSISDN's "+loginIdOrMsisdnV, _log);
					//BulkUserSuspensionProcessLog.logPrint("Bulk User Suspension ====================== Processing starts for MSISDN ====================");
					filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdnOrLoginID); // before process MSISDN filter each-one
					
					//check for valid MSISDN
					if(!BTSLUtil.isValidMSISDN(filteredMsisdn))
					{
						LogFactory.printLog(methodName, "Not a valid MSISDN "+msisdnOrLoginID, _log);
						BulkUserSuspensionProcessLog.log("FILE PROCESSING",PretupsI.SYSTEM,msisdnOrLoginID,countStr,"Not a valid MSISDN","Fail",filePathAndFileName);
						invalidString.append(msisdnOrLoginID);// append the invalid MSISDN in the string invalidString
						invalidString.append(delimiter);
						invalidList.add(filteredMsisdn);
						inValidHashMap.put(filteredMsisdn, "Not a valid MSISDN");
						continue;
					}

					// check prefix of the MSISDN
					msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
					// Added by Naveen For Channel user MNP.
					ListValueVO listValueVO=null;
					if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_USER_MNP_ALLOW))).booleanValue())
					{					 
						 listValueVO=PretupsBL.validateChannelUserForMNP(filteredMsisdn);	
						 if(listValueVO!=null)
							{
							 networkPrefixVO=new NetworkPrefixVO();
							 networkPrefixVO.setNetworkCode(listValueVO.getCodeName());
							 networkPrefixVO.setListValueVO(listValueVO);
							}						 
					}
					
					if(listValueVO==null){
						networkPrefixVO=(NetworkPrefixVO)NetworkPrefixCache.getObject(msisdnPrefix);
			           }
					//networkPrefixVO = (NetworkPrefixVO)NetworkPrefixCache.getObject(msisdnPrefix);

					if(networkPrefixVO == null)
					{
						LogFactory.printLog(methodName, "Not Network prefix found "+msisdnOrLoginID, _log);
						BulkUserSuspensionProcessLog.log("FILE PROCESSING",PretupsI.SYSTEM,msisdnOrLoginID,countStr,"Not Network prefix found","Fail",filePathAndFileName);
						invalidString.append(msisdnOrLoginID);
						invalidString.append(delimiter);
						invalidList.add(filteredMsisdn);
						inValidHashMap.put(filteredMsisdn, "Not Network prefix found");
						continue;
					}

					
					// check network support of the MSISDN
					networkCode = networkPrefixVO.getNetworkCode();
					if(!networkCode.equals(netwrkcode))
					{
						LogFactory.printLog(methodName, "Not supporting Network"+msisdnOrLoginID, _log);
						BulkUserSuspensionProcessLog.log("FILE PROCESSING",PretupsI.SYSTEM,msisdnOrLoginID,countStr,"Not supporting Network","Fail",filePathAndFileName);
						invalidString.append(msisdnOrLoginID);
						invalidString.append(delimiter);
						invalidList.add(filteredMsisdn);
						inValidHashMap.put(filteredMsisdn, "Not supporting Network");
						continue;
					}
					//insert the valid MSISDN in the validMsisdnList
					//validList.add(filteredMsisdn);
					
					invalidStringFromDao = channelUserWebDAO.deleteOrSuspendChnlUsersInBulkForMsisdn(con, filteredMsisdn, "SR",childExistList,PretupsI.SYSTEM,countStr,prepareStatementMap);
					
					if(invalidStringFromDao)
					{    
						LogFactory.printLog(methodName, "invalid list  "+msisdnOrLoginID, _log);
					    invalidString.append(filteredMsisdn);
					    invalidString.append(delimiter);
					    invalidList.add(filteredMsisdn);
					    inValidHashMap.put(filteredMsisdn, "Mobile number does not exist/ Mobile Number is already suspended");
					}
					else
					{
						//insert the valid MSISDN in the validMsisdnList
						LogFactory.printLog(methodName, "valid list  "+msisdnOrLoginID, _log);
						validList.add(filteredMsisdn);
					}
				}//else end
				
				
				if(invalidStringFromDao)
			    {    
					LogFactory.printLog(methodName, "Rollback the transaction for : "+msisdnOrLoginID, _log);
				    con.rollback();
			    }    
				else
			    {    
					LogFactory.printLog(methodName, "Commit the transaction for : "+msisdnOrLoginID, _log);
					con.commit();	
				    
				    if(loginIdOrMsisdnV.equals(PretupsI.LOOKUP_LOGIN_ID) && channelUserVO != null)
    	                msisdn = channelUserVO.getMsisdn();
				    else
				        msisdn = filteredMsisdn;
				    
				    if(!BTSLUtil.isNullString(msisdn))
				    {
				    	
				    	
				    	Locale locale =new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
					    sendBtslMessage = new BTSLMessages(PretupsErrorCodesI.USER_SUSPEND_SUCCESS);
					    bulkSuspensnCode=(String)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.DEFAULT_MESSGATEWAY,netwrkcode);
					    
					    pushMessage=new PushMessage(msisdn,sendBtslMessage,null,null,locale,netwrkcode);
					    pushMessage.push(bulkSuspensnCode,null);
					    
				    }
				   
				    if(channelUserVO==null)
				        channelUserVO = new ChannelUserVO();
				    channelUserVO.setModifiedOn(currentDate);
				    channelUserVO.setMsisdn(msisdn);
				    
				    // code added for set the data in uservo
				    
				    userVO.setUserID(PretupsI.SYSTEM);
				    userVO.setModifiedBy(PretupsI.SYSTEM);
				    userVO.setModifiedOn(currentDate);
				    
				    userVO.setLoginID(PretupsI.SYSTEM);
				    SessionInfoVO s = new SessionInfoVO();
				    s.setRemoteAddr("0.0.0.0");
				    userVO.setSessionInfoVO(s);
				   
				    
				    
				    if("SR".equals(PretupsI.USER_STATUS_SUSPEND_REQUEST))
				    {
				    	channelUserVO.setStatus(PretupsI.USER_STATUS_SUSPEND);
				        ChannelUserLog.log("BLKSUSPCHNLUSR", channelUserVO,userVO, true, null);
				      
				    }
				    
				    /*else if(unregisterChUserForm.getDeleteOrSuspendorResume().equals(PretupsI.USER_STATUS_DELETE_REQUEST))
				    {
				        channelUserVO.setStatus(PretupsI.USER_STATUS_DELETED);
				        ChannelUserLog.log("BLKDELCHNLUSR", channelUserVO, userVO, true, null);
				    }
				    else if(PretupsI.USER_STATUS_RESUME_REQUEST.equals(unregisterChUserForm.getDeleteOrSuspendorResume()))
				    {
				        channelUserVO.setStatus(PretupsI.USER_STATUS_RESUMED);
				        ChannelUserLog.log("BLKRESCHNLUSR", channelUserVO, userVO, true, null);
				    }*/
				   
			    }
					
				
				if(prepareStatementMap.get("psmtIsExist")!=null){
					((PreparedStatement)prepareStatementMap.get("psmtIsExist")).clearParameters();
				}
				if(prepareStatementMap.get("psmtUserID")!=null){
					((PreparedStatement)prepareStatementMap.get("psmtUserID")).clearParameters();
				}
				if(prepareStatementMap.get("psmtDelete")!=null){
					((PreparedStatement)prepareStatementMap.get("psmtDelete")).clearParameters();
				}
				if(prepareStatementMap.get("psmtResumeExist")!=null){
					((PreparedStatement)prepareStatementMap.get("psmtResumeExist")).clearParameters();
				}
				Thread.sleep(50);
			} 
			if (!invalidList.isEmpty() && invalidList.size()>0)
			{
				createAndMoveFailureFileToFinalDirectory(fileName,oldFilePath,NewFilePath,inValidHashMap);
			}	
			
			
			if (!validList.isEmpty() && validList.size()>0)
			{
				createAndMoveSuccessFileToFinalDirectory(fileName,oldFilePath,NewFilePath,validList);
				
			}
			
			totalProceedRecord=inValidHashMap.size() + validList.size();
			totalSuccessCount=validList.size();
			
			if(_log.isDebugEnabled())
			{
				_log.debug("====================processBulkSuspension"," Summary Log =================== File Name "+ fileName);
				_log.debug("====================processBulkSuspension"," Summary Log =================== Total count "+ (inValidHashMap.size() + validList.size()));
				_log.debug("====================processBulkSuspension"," Summary Log =================== Failure count "+ inValidHashMap.size());
				_log.debug("====================processBulkSuspension"," Summary Log =================== Success count "+  + validList.size());
				
				
				
				if(loginIdOrMsisdnV.equals(PretupsI.LOOKUP_LOGIN_ID))
			    {
					LogFactory.printLog(methodName, "==============Processing Completed Through for Login ID ===============================", _log);
			    }
				else
				{
					LogFactory.printLog(methodName, "==============Processing Completed Through for MSISDN ================================", _log);
				}
				
			}
			
			
			BulkUserSuspensionProcessLog.summaryLog("Bulk User Suspension", fileName,inValidHashMap.size()+validList.size(), validList.size(), inValidHashMap.size());
			
			if(loginIdOrMsisdnV.equals(PretupsI.LOOKUP_LOGIN_ID))
		    {
				BulkUserSuspensionProcessLog.logPrint("Bulk User Suspension ===============Processing Completed through LOGIN ID's ===================");
		    }
			else
			{
				
				BulkUserSuspensionProcessLog.logPrint("Bulk User Suspension ================Processing Completed through MSISDN's ====================");
			}
			
            }//if flag
		//} // end of file for loop
            if(bufferReader!=null)
			    bufferReader.close();
			if(fileReader!=null)
			    fileReader.close();
			
		}// Try end
		catch(Exception e)
		{
			_log.error(methodName,"Exception:e="+e);
			_log.errorTrace(methodName,e);
			try
			{
				if(bufferReader!=null)
				    bufferReader.close();
				if(fileReader!=null)
				    fileReader.close();
			}
			catch(Exception e1)
			{
				_log.errorTrace(methodName,e1);
				bufferReader = null;
				fileReader = null;
			}
			//file = new File(filePath,fileName);
			//file.delete();
			//return super.handleError(this, "processUploadedFileForUnReg", e, request, mapping);
	}
	finally {
		try {
			if (prepareStatementMap.get("psmtIsExist") != null) {
				((PreparedStatement) prepareStatementMap.get("psmtIsExist")).close();
			}
		}
		catch (Exception e) {
			_log.error(methodName,e);
		}
		try {
			if (prepareStatementMap.get("psmtUserID") != null) {
				((PreparedStatement) prepareStatementMap.get("psmtUserID")).close();
			}
		}
		catch (Exception e) {
			_log.error(methodName,e);
		}
		try {
			if (prepareStatementMap.get("psmtDelete") != null) {
				((PreparedStatement) prepareStatementMap.get("psmtDelete")).close();
			}
		}
		catch (Exception e) {
			_log.error(methodName,e);
		}
		try {
			if (prepareStatementMap.get("psmtResumeExist") != null) {
				((PreparedStatement) prepareStatementMap.get("psmtResumeExist")).close();
			}
		}
		catch (Exception e) {
			_log.error(methodName,e);
		}			
		try {
			if (prepareStatementMap.get("psmtChildExist") != null) {
				((PreparedStatement) prepareStatementMap.get("psmtChildExist")).close();
			}
		}
		catch (Exception e) {
			_log.error(methodName,e);
		}
		try {
			if (prepareStatementMap.get("psmtUserBalanceExist") != null) {
				((PreparedStatement) prepareStatementMap.get("psmtUserBalanceExist")).close();
			}
		}
		catch (Exception e) {
			_log.error(methodName,e);
		}
		try {
			if (prepareStatementMap.get("psmtChnlTrnsfrPendingTransactionExist") != null) {
				((PreparedStatement) prepareStatementMap.get("psmtChnlTrnsfrPendingTransactionExist")).close();
			}
		}
		catch (Exception e) {
			_log.error(methodName,e);
		}
		try {
			if (prepareStatementMap.get("psmtfocPendingTransactionExist") != null) {
				((PreparedStatement) prepareStatementMap.get("psmtfocPendingTransactionExist")).close();
			}
		}
		catch (Exception e) {
			_log.error(methodName,e);
		}
		
		Locale locale=new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
		String  senderMessage="Bulk User Suspension process executed successfully. Total Record count="+totalProceedRecord+" Success Count="+totalSuccessCount+" Failed Count="+(totalProceedRecord-totalSuccessCount);
		
		String msisdnString=new String(Constants.getProperty("adminmobile"));
		String [] msisdn =msisdnString.split(",");
		for(int i=0; i<msisdn.length;i++)
		{
			PushMessage pushMessage=new PushMessage(msisdn[i],senderMessage,null,null,locale);
			pushMessage.push();
		}
		
		
		
       LogFactory.printLog(methodName, "Exit:return : ", _log);
	}
	
}
	
	/**
	 * This method is invoked by Main Method.
	 * It is used to decide whether to invoke the processBulkSuspension method or not.
	 * This decision is made by maintaining a single entry of processID,date-time and status,in PROCESS_STATUS.
	 * For this it interaly calls the checkProcessUnderProcess method and based on the parameter processStatusOK it calls the process Method.
	 * @throws BTSLBaseException
	 */
	private void processFile(String loginIdOrMsisdn,String networkcode) throws BTSLBaseException
	{	
		final String methodName="processFile";
		LogFactory.printLog(methodName, "method of processBulkSuspension Entered ", _log);
	    
	    String loginIdOrMsisdnValue = loginIdOrMsisdn;
		Connection con = null;
		
		int successU = 0;
		try
		{
		    _date = new Date();
			//load the constatn values from constants.prop
		    loadConstantValues();

		    //Load NetworkPrefixCache
		    NetworkPrefixCache.loadNetworkPrefixesAtStartup();
		    
		    //Load the LoadControllerCache
		    //LoadControllerCache.refreshInstanceLoad(_instanceID);
		    
		    //Load the MessageGatewayCache
			MessageGatewayCache.loadMessageGatewayAtStartup();

			
			con = OracleUtil.getSingleConnection();
			checkProcessUnderProcess(con);
			
			//If the schedulerOK is TRUE call the process
			if(_processStatusOK)
			{
			    //Commiting the status of process status as 'U-Under Process' and call the process.
			    con.commit();
			    processBulkSuspension(con,loginIdOrMsisdnValue,networkcode);
			}
		}
		catch(BTSLBaseException be)
		{
			_log.error(methodName, "BTSLBaseException be= " + be);
		    if(con!=null) {
				try {con.rollback();} catch (Exception e1){_log.errorTrace(methodName,e1);}
			}
			throw be;
		}
		catch(Exception e)
		{
		    _log.error(methodName, "Exception be= " + e);
			_log.errorTrace(methodName,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"processBulkSuspension[invokeProcessByStatus]","processStatusVO.getProcessID()"+_processStatusVO.getProcessID(),"","","Exception:"+e.getMessage());
		}
		finally
		{
		    try
		    {
			    //Setting the process status as 'C-Complete' if the _processStatusOK is true
			    if(_processStatusOK)
			    {
			        _date = new Date();
				    _processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
				    _processStatusVO.setExecutedOn(_date);
				    _processStatusVO.setExecutedUpto(_date);
				    successU = _processStatusDAO.updateProcessDetail(con,_processStatusVO);
				    
				    //Commiting the process status as 'C-Complete'
				    if(successU>0) {
						if(con!=null) {
							con.commit();
						}
					} else {
						throw new BTSLBaseException(methodName,"invokeProcessByStatus",PretupsErrorCodesI.PROCESS_ERROR_UPDATE_STATUS);
					}
			    }
		    }
		    catch(BTSLBaseException be)
		    {
				_log.error(methodName, "BTSLBaseException be= " + be);
			    if(con!=null) {
					try {con.rollback();} catch (Exception e1){_log.errorTrace(methodName,e1);}
				}
				//throw be;
		    }
		    catch(Exception e)
		    {
				_log.errorTrace(methodName,e);
				_log.error(methodName, "Exception e= " + e);
			    if(con!=null) {
					try {con.rollback();} catch (Exception e1){_log.errorTrace(methodName,e1);}
				}
		    }
			if(con!=null) {
				try {con.close();} catch (Exception e1){_log.errorTrace(methodName,e1);}
			}
			LogFactory.printLog(methodName, "Exiting _processStatusVO="+_processStatusVO, _log);
		}
		
	}
	
	
	/**
	 * This method is used to check the Process Status.
 	 * If there is no entry for corresponding processID STOP the process.
	 * If the status is 'C' set processStatusOK as True.
	 * If the status is 'U' check the exipiry if it expires,update status as 'U' and set processStatusOK as True.
	 * In case of expiry send the alarm also.
	 * @param	Connection p_con
	 * @throws	BTSLBaseException
	 */
	private void checkProcessUnderProcess(Connection p_con) throws BTSLBaseException
	{
		final String methodName="checkProcessUnderProcess";
	    LogFactory.printLog(methodName, "Entered ", _log);
		long dateDiffInHour=0;
		int successC = 0;
		try
		{
			//load the Scheduler information - start date and status of scheduler
			_processStatusVO = (ProcessStatusVO)_processStatusDAO.loadProcessDetail(p_con,ProcessI.BULK_USER_SUSPENSION_PROCESSID);
			
		    //Check Process Entry,if no entry for the process throw the exception and stop the process
			if(_processStatusVO==null) {
				throw new BTSLBaseException("BulkUserSuspension",methodName,PretupsErrorCodesI.PROCESS_ENTRY_NOT_FOUND);
			} else if(ProcessI.STATUS_COMPLETE.equals(_processStatusVO.getProcessStatus()))
			{
		        //set the current date while updating the start date of process
			    _processStatusVO.setStartDate(_date);
			    _processStatusVO.setProcessStatus(ProcessI.STATUS_UNDERPROCESS);
			    successC=_processStatusDAO.updateProcessDetail(p_con,_processStatusVO);
			    if(successC>0) {
					_processStatusOK=true;
				} else {
					throw new BTSLBaseException("BulkUserSuspension",methodName,PretupsErrorCodesI.PROCESS_ERROR_UPDATE_STATUS);
				}
			}
		    //if the scheduler status is UnderProcess check the expiry of scheduler.
			else if(ProcessI.STATUS_UNDERPROCESS.equals(_processStatusVO.getProcessStatus()))
			{
			    //dateDiffInHour = BTSLUtil.getDifferenceInUtilDates(_processStatusVO.getStartDate(),_date);
			    if(_processStatusVO.getStartDate()!=null) {
					dateDiffInHour = getDiffOfDateInHour(_date,_processStatusVO.getStartDate());
				} else {
					throw new BTSLBaseException("BulkUserSuspension",methodName,"Process Start Date is NULL");
				}
			    LogFactory.printLog(methodName, "dateDiffInHour="+dateDiffInHour +" _processStatusVO.getExpiryTime() = "+_processStatusVO.getExpiryTime(), _log);
			    if(dateDiffInHour >=_processStatusVO.getExpiryTime())
			    {
			        //set the current date while updating the start date of process
			        _processStatusVO.setStartDate(_date);
			        successC=_processStatusDAO.updateProcessDetail(p_con,_processStatusVO);
					if(successC>0) {
						_processStatusOK=true;
					} else {
						throw new BTSLBaseException("BulkUserSuspension",methodName,PretupsErrorCodesI.PROCESS_ERROR_UPDATE_STATUS);
					}
			    } else {
					throw new BTSLBaseException("BulkUserSuspension",methodName,PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
				}
			}
		}
		catch(BTSLBaseException be)
		{
			_log.error(methodName, "BTSLBaseException : " + be);
			throw be;
		}
		catch(Exception e)
		{
			_log.error(methodName, "Exception : " + e);
			_log.errorTrace(methodName,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"RestrictedSubscriberTopUp[invokeProcessByStatus]","processStatusVO.getProcessID()","","","Exception:"+e.getMessage());
		}
		finally
		{
			LogFactory.printLog(methodName, "Exiting _processStatusVO="+_processStatusVO, _log);
		}
	}
	/**
	 * Used to get the difference of date in Minutes
	 * @param p_currentDate Date
	 * @param p_startDate Date
	 * @return long
	 * @throws BTSLBaseException
	 */
	private long getDiffOfDateInHour(Date p_currentDate, Date p_startDate)
	{
		final String methodName="getDiffOfDateInHour";
		LogFactory.printLog(methodName, "Entered p_currentDate="+p_currentDate+" p_startDate: "+p_startDate, _log);
		
		long diff=0;
	    try
	    {
			diff=(p_currentDate.getTime()-p_startDate.getTime())/(1000*60*60);
	    }
	    catch(Exception e)
	    {
	        _log.errorTrace(methodName,e);
			_log.error(methodName, "Exception : "+e.getMessage());
		}
		LogFactory.printLog(methodName, "Exiting diff="+diff, _log);
		return diff;
	}
	
	
	/**
	 * @param fileName
	 * @return
	 */
	public  boolean validateFileName(String fileName)
	{
		final String methodName = "validateFileName";
		LogFactory.printLog(methodName, "File name to validate is"+fileName, _log);
		boolean validFileName=false;
		
			String[] parts = fileName.split("_");
			if(parts.length > 2)
			{
				LogFactory.printLog(methodName, "File name length"+parts.length, _log);
					
					if("USER".equals(parts[0]) && "SUSPEND".equals(parts[1]))
							validFileName=true;
							
			
			}
		LogFactory.printLog(methodName, "Exit with "+validFileName, _log);
		return validFileName;
	}
	
	/**
	 * @param fileName
	 * @return
	 */
	public  boolean validateFormat(String fileName)
	{
		final String methodName = "validateFormat";
		LogFactory.printLog(methodName, "File name to validate is"+fileName, _log);
		boolean validFileFormat=false;
		
		if(fileName.endsWith(".txt") || fileName.endsWith(".TXT"))
		{
			LogFactory.printLog(methodName, "File name txt "+fileName, _log);
			validFileFormat=true;
			
		}
		LogFactory.printLog(methodName, "Exit with "+validFileFormat, _log);
		return validFileFormat;
	}
	
	/**
	 * @param fileName
	 * @param oldFilePath
	 * @param p_finalDirectoryPath
	 * @param description
	 * @throws BTSLBaseException
	 */
	private static void createAndMoveRejectedFileToFinalDirectory(String fileName,String oldFilePath,String p_finalDirectoryPath,String description) throws BTSLBaseException
	{
		final String methodName = "createAndMoveRejectedFileToFinalDirectory";
		LogFactory.printLog(methodName, " Entered: fileName="+fileName+" p_finalDirectoryPath="+p_finalDirectoryPath + "oldFilePath "+oldFilePath +"description"+description, _log);

		String oldFileName = "";
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
		int lastDot = fileName.lastIndexOf('.');
		oldFileName = fileName.substring(0,lastDot) + "_REJECTED_" + timeStamp + fileName.substring(lastDot);
		
		
		String newFileName = "";
		newFileName=fileName.replaceAll("SUSPEND", "REJECT");
		
		
		if ("90001".equalsIgnoreCase(description))
		{
			newFileName=changeExtension(newFileName,".txt");
			oldFileName=changeExtension(oldFileName,".txt");
		}
		
		
		File parentDir = new File(p_finalDirectoryPath);
		if(!parentDir.exists())
			parentDir.mkdirs();
		
		String newDirName=p_finalDirectoryPath;
		File newDir = new File(newDirName);
		if(!newDir.exists())
			newDir.mkdirs();
		
		File oldFile = new File(oldFilePath);
		
		File actualFile = new File (newDir, newFileName);
		
		LogFactory.printLog(methodName, " newDirName="+newDirName, _log);

		LogFactory.printLog(methodName, " actualFile="+actualFile, _log);
		LogFactory.printLog(methodName, " actualFile1="+ (newDir+File.separator+oldFileName), _log);
		
		InputStream inStream = null;
		OutputStream outStream = null;
		FileWriter writer = null; 
		
		try
		{
			// Code to read the IN folder file and right it to OUT folder file 
			File afile =new File(oldFilePath);
    	    File bfile =new File(newDir+File.separator+oldFileName);
    		
    	    inStream = new FileInputStream(afile);
    	    outStream = new FileOutputStream(bfile);
        	
    	    byte[] buffer = new byte[1024];
    		
    	    int length;
    	    //copy the file content in bytes 
    	    while ((length = inStream.read(buffer)) > 0){
    	  
    	    	outStream.write(buffer, 0, length);
    	 
    	    }
    	 
    	    
    	    // code ends
    	    
			
			writer = new FileWriter(actualFile);
			String des = Constants.getProperty(description).trim(); 
			
		    writer.append("ReasonCode");
		    writer.append(',');
		    writer.append("Description");
		    writer.append('\n');

		    writer.append(description);
		    writer.append(',');
		    writer.append(des);
	        writer.append('\n');
			//generate whatever data you want
				
		    writer.flush();
		    ((Flushable) inStream).flush();
		    outStream.flush();
		    
		    actualFile.delete();
		    oldFile.delete();
		    afile.delete();
		    bfile.delete();
		}
		catch(Exception e)
		{
			_log.error(methodName, "Exception " + e.getMessage());
			_log.errorTrace(methodName,e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DWHFileCreationGP[moveFilesToFinalDirectory]","","","","Exception:"+e.getMessage());
		    //throw new BTSLBaseException(CLASS_NAME,methodName,PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
		}
        finally
        {
			if(parentDir!=null) parentDir = null;
			if(newDir!=null) newDir = null;
			//if(oldDir!=null) oldDir = null;
            LogFactory.printLog(methodName, "Exiting.. ", _log);
            
            try {
				if(writer!=null) {
					writer.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				_log.error(methodName,"IOException e="+e.getMessage());
				throw new BTSLBaseException("BulkUserSuspension",methodName,"Writer is NULL");
			}
            
            try {
            	if(outStream!=null) {
            		outStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				_log.error(methodName,"IOException e="+e.getMessage());
				throw new BTSLBaseException("BulkUserSuspension",methodName,"OutStream is NULL");
			}
            
            try {
				if(inStream!=null) {
					inStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				_log.error(methodName,"IOException e="+e.getMessage());
			}
        } // end of finally
	}
	
	/**
	 * @param data
	 * @param loginIdOrMsisdnV
	 * @return
	 */
	public boolean primaryValidation (String data ,String loginIdOrMsisdnV)
	{
		String methodName = "primaryValidation"; 
		LogFactory.printLog(methodName, "Entered "+data, _log);
		boolean isValidateFlag=false;
		
		if (loginIdOrMsisdnV.equals(PretupsI.LOOKUP_MSISDN))
		{
			if (data.length()!=10 && !BTSLUtil.isNumeric(data))
			{
			LogFactory.printLog(methodName, "MSISDN is not valid "+data, _log);
			isValidateFlag = true;
			}
		}	
		
		if( data.length() == 0) //check for the blank line b/w the records of the file
	    {
			LogFactory.printLog(methodName, "blank line in b/w records "+data, _log);
			isValidateFlag = true;
	    }
		
		
		
		return isValidateFlag;
		
	}
	
	/**
	 * @param fileName
	 * @param oldFilePath
	 * @param p_finalDirectoryPath
	 * @param validList
	 * @throws BTSLBaseException
	 */
	private static void createAndMoveSuccessFileToFinalDirectory(String fileName,String oldFilePath,String p_finalDirectoryPath,List validList) throws BTSLBaseException
	{
		final String methodName = "createAndMoveSuccessFileToFinalDirectory";
		LogFactory.printLog(methodName, " Entered: fileName="+fileName+" p_finalDirectoryPath="+p_finalDirectoryPath + "oldFilePath "+oldFilePath, _log);

		
		String newFileName = "";
		newFileName=fileName.replaceAll("SUSPEND", "SUCCESS");
		
		int validListSize = validList.size();
		
		File parentDir = new File(p_finalDirectoryPath);
		if(!parentDir.exists())
			parentDir.mkdirs();
		
		String newDirName=p_finalDirectoryPath;
		File newDir = new File(newDirName);
		if(!newDir.exists())
			newDir.mkdirs();
		
		File oldFile = new File(oldFilePath);
		
		File actualFile = new File (newDir, newFileName);
		
		LogFactory.printLog(methodName, " newDirName="+newDirName, _log);

		LogFactory.printLog(methodName, " actualFile="+actualFile, _log);
		
		FileWriter writer = null;
		try
		{
			writer = new FileWriter(actualFile);
			
			for (int valid=0; valid< validListSize;valid++)
			{
				writer.append(validList.get(valid).toString());
				writer.append('\n');
				
			}
		    
			writer.flush();
			
		    oldFile.delete();
			
			
		}
		catch(Exception e)
		{
			_log.error(methodName, "Exception " + e.getMessage());
			_log.errorTrace(methodName,e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DWHFileCreationGP[moveFilesToFinalDirectory]","","","","Exception:"+e.getMessage());
		    //throw new BTSLBaseException(CLASS_NAME,methodName,PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
		}
        finally
        {
			
			
			if(parentDir!=null) parentDir = null;
			if(newDir!=null) newDir = null;
			//if(oldDir!=null) oldDir = null;
            LogFactory.printLog(methodName, "Exiting.. ", _log);
            
            try {
				if(writer!=null) {
					writer.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				_log.error(methodName,"IOException e="+e.getMessage());
			}
        } // end of finally
	}
	
	/**
	 * @param fileName
	 * @param oldFilePath
	 * @param p_finalDirectoryPath
	 * @param invalidHashMap
	 * @throws BTSLBaseException
	 */
	private static void createAndMoveFailureFileToFinalDirectory(String fileName,String oldFilePath,String p_finalDirectoryPath,Map<String, String> invalidHashMap) throws BTSLBaseException
	{
		final String methodName = "createAndMoveFailureFileToFinalDirectory";
		LogFactory.printLog(methodName, " Entered: fileName="+ fileName+" p_finalDirectoryPath="+ p_finalDirectoryPath + "oldFilePath "+oldFilePath, _log);
		
		String newFileName = "";
		newFileName=fileName.replaceAll("SUSPEND", "FAILED");
		
		File parentDir = new File(p_finalDirectoryPath);
		if(!parentDir.exists())
			parentDir.mkdirs();
		
		String newDirName=p_finalDirectoryPath;
		File newDir = new File(newDirName);
		if(!newDir.exists())
			newDir.mkdirs();
		
		File oldFile = new File(oldFilePath);
		
		File actualFile = new File (newDir, newFileName);
		
		LogFactory.printLog(methodName, " newDirName="+newDirName, _log);

		LogFactory.printLog(methodName, " actualFile="+actualFile, _log);
		
		FileWriter writer = null;
		try
		{
			writer = new FileWriter(actualFile);
			
			Set<String> keys = invalidHashMap.keySet();
		        for(String key: keys){
		        	LogFactory.printLog(methodName, " key: "+key, _log);
		        }
		
		        Set<String> keySet = invalidHashMap.keySet();
		        Iterator<String> keySetIterator = keySet.iterator();
		        while (keySetIterator.hasNext()) {
		        	
		           String key = keySetIterator.next();
		          LogFactory.printLog(methodName, " Iterating Map in Java using KeySet Iterator key value is ="+key + "value" +invalidHashMap.get(key), _log);
		          
		          	writer.append(key);
				    writer.append(',');
				    writer.append(invalidHashMap.get(key));
			        writer.append('\n');
		           
		        }

		    writer.flush();
		    
			
		    oldFile.delete();
			
			
		}
		catch(Exception e)
		{
			_log.error(methodName, "Exception " + e.getMessage());
			_log.errorTrace(methodName,e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DWHFileCreationGP[moveFilesToFinalDirectory]","","","","Exception:"+e.getMessage());
		    //throw new BTSLBaseException(CLASS_NAME,methodName,PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
		}
        finally
        {
			
			
			if(parentDir!=null) parentDir = null;
			if(newDir!=null) newDir = null;
			//if(oldDir!=null) oldDir = null;
            LogFactory.printLog(methodName, "Exiting.. ", _log);
            
            try {
				if(writer!=null) {
					writer.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				_log.error(methodName,"IOException e="+e.getMessage());
			}
        } // end of finally
	}
	
	/**
	 * @param originalName
	 * @param newExtension
	 * @return
	 */
	public static String changeExtension(String originalName, String newExtension) {
	    int lastDot = originalName.lastIndexOf('.');
	    if (lastDot != -1) {
	        return originalName.substring(0, lastDot) + newExtension;
	    } else {
	        return originalName + newExtension;
	    }
	}
	
	/**
	 * @param fileName
	 * @return
	 */
	public  boolean validateFileDate(String fileName)
	{
		final String methodName="validateFileDate";
		LogFactory.printLog(methodName, "File name to validate is"+fileName, _log);
		boolean validFileName=false;
		
			String[] parts = fileName.split("_");
			
				LogFactory.printLog(methodName, "File name length"+parts.length, _log);
								
								String date2 = parts[2];
								String[] parts1 = date2.split("\\.");
								String date1 = parts1[0];
								
								
								if (date1.length()==6)
								{
									
									String dateString = date1.substring(0, 2);
									String monthString = date1.substring(2, 4);
									String yearString = date1.substring(4, 6);
									
									
									try
									{
										String formatDate = dateString+"/"+monthString+"/"+yearString;
										_log.debug(methodName,"File name formatDate"+formatDate);
										if (BTSLUtil.isValidDatePattern(formatDate)) 
											{
											_log.debug(methodName,"valid date pattern valid"+validFileName);
											
												Date d1 =BTSLUtil.getDateFromDateString (formatDate);
												if (BTSLUtil.getDifferenceInUtilDates(d1,new Date()) >=1)
												{
													validFileName=true;
													LogFactory.printLog(methodName, "date is valid "+validFileName, _log);
												}
											}
									
									}
									
									catch(Exception e)
								    {
										_log.error("process", "Exception " + e.getMessage());
										_log.errorTrace(methodName,e);
								    }
								}
							
			
			
		LogFactory.printLog(methodName, "Exit with "+validFileName, _log);
		return validFileName;
	}
	
}

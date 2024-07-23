package com.client.pretups.processes.clientprocesses.ethio;
/**
 * @(#)C2SERPDetail 
 * Copyright(c) 2016, Mahindra Comviva Ltd. 
 * All Rights Reserved
 * 
 * -------------------------------------------------------------------------------------------------
 * Author 				Date 			History
 * -------------------------------------------------------------------------------------------------
 * Trasha Dewan  		12/10/2016 		Initial Creation
 * -------------------------------------------------------------------------------------------------
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

public class C2SERPDetail 
{
    private static String _rechargeRecordC2SFileLabelForTransaction=null;
    private static String _rechargeRecordC2SFileNameForTransaction=null;
    private static String _masterDirectoryPathAndName=null;
    private static String _masterDirectoryDateFormat=null;
    private static String _finalMasterDirectoryPath=null;
    private static String _fileZipAllowed=null;
    private static String _childDirectory=null;
    private static String _fileEXT=null;
	private static String _rateToCalculateTax=null;
    private static long _maxFileLength=0;
    private static ArrayList<String> _fileNameLst = new ArrayList<String>();
    private static ProcessStatusVO _processStatusVO;
    private static ProcessBL _processBL=null;
    private static int beforeInterval=0;
    
    
	private static Log LOG = LogFactory.getLog(C2SERPDetail.class.getName());
	public static void main(String arg[])
	{
	    try
		{
	    	//ID rechargeRecord003 third argument is to generate rechargeRecord even there is Ambiguous or Under Process Txn.
	    	if(arg.length!=3)
			{
	    		if(arg.length!=2)
			    {
				  System.out.println("Usage : C2SERPDetail [Constants file] [LogConfig file]");
			 	  return;
			    }
			}
			File constantsFile = new File(arg[0]);
			if(!constantsFile.exists())
			{
				System.out.println("C2SERPDetail"+" Constants File Not Found .............");
				return;
			}
			File logconfigFile = new File(arg[1]);
			if(!logconfigFile.exists())
			{
				System.out.println("C2SERPDetail"+" Logconfig File Not Found .............");
				return;
			}
	
			ConfigServlet.loadProcessCache(constantsFile.toString(),logconfigFile.toString());
			
		}//end of try
		catch(Exception e)
		{
			if (LOG.isDebugEnabled())
				LOG.debug("main"," Error in Loading Files ...........................: "+e.getMessage());
			e.printStackTrace();
			ConfigServlet.destroyProcessCache();
			return;
		}// end of catch
		try
		{
			process();
		}
		catch(BTSLBaseException be)
		{
			LOG.error("main", "BTSLBaseException : " + be.getMessage());
			be.printStackTrace();
		}
		finally
		{
			if(LOG.isDebugEnabled())LOG.debug("main", "Exiting..... ");
			ConfigServlet.destroyProcessCache();
		}
	}
	
	private static void process() throws BTSLBaseException
	{
	    Date processedUpto=null;
	    Date dateCount=null;
	    Date currentDate=new Date();
	    Connection con= null;
	    String processId=null;
	    boolean statusOk=false;
	    ProcessStatusDAO processStatusDAO=null;
	    int beforeInterval=0;
		
		int maxDoneDateUpdateCount=0;

		try
		{
		    LOG.debug("process","Memory at startup: Total:"+Runtime.getRuntime().totalMemory()/1049576+" Free:"+Runtime.getRuntime().freeMemory()/1049576);
		    currentDate=BTSLUtil.getSQLDateFromUtilDate(currentDate);
		    //getting all the required parameters from Constants.props
		    loadConstantParameters();
		    
			con = OracleUtil.getSingleConnection();
			if(con==null)
			{
				if (LOG.isDebugEnabled())
					LOG.debug("process"," DATABASE Connection is NULL ");
				EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2SERPDetail[process]","","","","DATABASE Connection is NULL");
				return;
			}
			//getting process id
       	    processId=ProcessI.C2S_ERP_DETAIL_PROCESS;
       	    //method call to check status of the process
       	    _processBL=new ProcessBL();
       	    _processStatusVO=_processBL.checkProcessUnderProcess(con,processId);
       	    statusOk=_processStatusVO.isStatusOkBool();
       	    beforeInterval=(int)_processStatusVO.getBeforeInterval()/(60*24);
       	    if (statusOk)
       	    {
       	        con.commit();
       	        //method call to find maximum date till which process has been executed
       	        processedUpto=_processStatusVO.getExecutedUpto();
	      	    if (processedUpto!=null)
	       	    {
		      	    //ID DWH002 to check whether process has been executed till current date or not
	      	        if (processedUpto.compareTo(currentDate)==0)
				        throw new BTSLBaseException("C2SERPDetail","process",PretupsErrorCodesI.DWH_PROCESS_ALREADY_EXECUTED_TILL_TODAY);
	      	        //adding 1 in processed upto dtae as we have to start from the next day till which process has been executed
	      	        processedUpto=BTSLUtil.addDaysInUtilDate(processedUpto,1);
	      	      String tempFileName=_rechargeRecordC2SFileNameForTransaction;
	      	 
	      	      
	           	    //loop to be started for each date
	      	        //the loop starts from the date till which process has been executed and executes one day before current date
	       	         for(dateCount=BTSLUtil.getSQLDateFromUtilDate(processedUpto);dateCount.before(BTSLUtil.addDaysInUtilDate(currentDate,-beforeInterval));dateCount=BTSLUtil.addDaysInUtilDate(dateCount,1))
	       	        {
	       	        	
	       				
	       	        	_rechargeRecordC2SFileNameForTransaction=tempFileName+BTSLUtil.getDateStringFromDate(dateCount,_masterDirectoryDateFormat)+'_';
	       			    //method call to create master directory and child directory if does not exist
	       				_childDirectory=createDirectory(_masterDirectoryPathAndName);
	       			    //method call to fetch transaction data and write it in files
	   					fetchTransactionData(con,dateCount,beforeInterval,_masterDirectoryPathAndName,_rechargeRecordC2SFileNameForTransaction,_rechargeRecordC2SFileLabelForTransaction,_fileEXT,_maxFileLength);
	       	        	
	       				    //method call to create master directory and child directory if does not exist
	       				  
	       					//method call to update maximum date till which process has been executed
	       					_processStatusVO.setExecutedUpto(dateCount);
	       					_processStatusVO.setExecutedOn(currentDate);
	       					processStatusDAO=new ProcessStatusDAO();
	       					maxDoneDateUpdateCount=processStatusDAO.updateProcessDetail(con,_processStatusVO);

	       					//if the process is successful, transaction is commit, else rollback
	       					if(maxDoneDateUpdateCount>0)
	       					{
	       					  
	       						moveFilesToFinalDirectory(_masterDirectoryPathAndName,_finalMasterDirectoryPath,processId,dateCount);
	       						if(_fileZipAllowed.equalsIgnoreCase("Y"))
	       							fileZip();
		       				    con.commit();
	       					}
	       					else
	       					{
	       					    deleteAllFiles();
	       					    con.rollback();
	    					    throw new BTSLBaseException("C2SERPDetail","process",PretupsErrorCodesI.RECHARGERECORD_COULD_NOT_UPDATE_MAX_DONE_DATE);
	       					}
	       					//DWH002 sleep has been added after processing records of one day
	       					Thread.sleep(500);
	       				
	       	        }//end loop
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"C2SERPDetail[process]","","",""," C2SERPDetail process has already  executed successfully.");
	       	    }
	      	    //ID DWH002 to avoid the null pointer exception thrown, in case processesUpto is null
	      	    else
			        throw new BTSLBaseException("C2SERPDetail","process",PretupsErrorCodesI.RECHARGERECORD_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
       	    }
		}//end of try
		catch(BTSLBaseException be)
		{
			LOG.error("process", "BTSLBaseException : " + be.getMessage());
			be.printStackTrace();
			throw be;
		}
		catch(Exception e)
		{
		    if (_fileNameLst.size()>0)
		        deleteAllFiles();
			LOG.error("process", "Exception : " + e.getMessage());
		    e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"C2SERPDetail[process]","","",""," C2SERPDetail process could not be executed successfully.");
	        throw new BTSLBaseException("C2SERPDetail","process",PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
		}
		finally
		{
		    //if the status was marked as under process by this method call, only then it is marked as complete on termination
		    if (statusOk)
		    {
		        try
		        {
			        if (markProcessStatusAsComplete(con,processId)==1)
			        	try{con.commit();} catch(Exception e){e.printStackTrace();}
			        else
			        	try{con.rollback();} catch(Exception e){e.printStackTrace();}
		        }
		        catch(Exception e)
		        {
		            e.printStackTrace();
		        }
		        try
		        {
		            if(con!=null)
					con.close();
		        }
		        catch(Exception ex)
		        {
					if(LOG.isDebugEnabled())LOG.debug("process", "Exception closing connection ");
		        }
		    }
		    LOG.debug("process","Memory at end: Total:"+Runtime.getRuntime().totalMemory()/1049576+" Free:"+Runtime.getRuntime().freeMemory()/1049576);
			if(LOG.isDebugEnabled())LOG.debug("process", "Exiting..... ");
		}
	}

	
	private static void loadConstantParameters() throws BTSLBaseException
	{
		if (LOG.isDebugEnabled())
			LOG.debug("loadParameters"," Entered: ");
		try
		{
		    _rechargeRecordC2SFileLabelForTransaction  = Constants.getProperty("RechargeRecord_C2S_Detail_FILE_LABEL");
			if(BTSLUtil.isNullString(_rechargeRecordC2SFileLabelForTransaction))
				LOG.error("loadConstantParameters"," Could not find file label for transaction data in the Constants file.");
			else
				LOG.debug("main"," _rechargeRecordFileLabelForTransaction="+_rechargeRecordC2SFileLabelForTransaction);
			
			_rechargeRecordC2SFileNameForTransaction  = Constants.getProperty("RechargeRecord_C2S_Detail_FILE_NAME");
			if(BTSLUtil.isNullString(_rechargeRecordC2SFileNameForTransaction))
				LOG.error("loadConstantParameters"," Could not find file name for transaction data in the Constants file.");
			else
				LOG.debug("loadConstantParameters"," _rechargeRecordFileNameForTransaction="+_rechargeRecordC2SFileNameForTransaction);
			
			_masterDirectoryDateFormat  = Constants.getProperty("RechargeRecord_C2S_Detail_DATE_FORMAT");
			if(BTSLUtil.isNullString(_masterDirectoryDateFormat))
				LOG.error("loadConstantParameters"," Could not find file name for master data in the Constants file.");
			else
				LOG.debug("loadConstantParameters"," _masterDirectoryDateFormat="+_masterDirectoryDateFormat);
			
			_masterDirectoryPathAndName = Constants.getProperty("RechargeRecord_C2S_Detail_DIRECTORY");
			if(BTSLUtil.isNullString(_masterDirectoryPathAndName))
				LOG.error("loadConstantParameters"," Could not find directory path in the Constants file.");
			else
				LOG.debug("loadConstantParameters"," _masterDirectoryPathAndName="+_masterDirectoryPathAndName);
			
			_finalMasterDirectoryPath = Constants.getProperty("RechargeRecord_C2S_Detail_FINAL_DIRECTORY");
			if(BTSLUtil.isNullString(_finalMasterDirectoryPath))
				LOG.error("loadConstantParameters"," Could not find final directory path in the Constants file.");
			else
				LOG.debug("loadConstantParameters"," finalMasterDirectoryPath="+_finalMasterDirectoryPath);
			
			_fileZipAllowed = Constants.getProperty("FILE_C2S_ZIP_ALLOWED");
			if(BTSLUtil.isNullString(_fileZipAllowed))
				LOG.error("loadConstantParameters"," Could not find FILE_ZIP_ALLOWED in the Constants file.");
			else
				LOG.debug("loadConstantParameters"," _fileZipAllowed="+_fileZipAllowed);
			_rateToCalculateTax = Constants.getProperty("RechargeRecord_C2S_Detail_TAX_RATE");
			if(BTSLUtil.isNullString(_rateToCalculateTax))
				LOG.error("loadConstantParameters"," Could not find RechargeRecord_C2S_Detail_TAX_RATE in the Constants file.");
			else
				LOG.debug("loadConstantParameters"," _rateToCalculateTax="+_rateToCalculateTax);
			
			//checking that none of the required parameters should be null
			if(BTSLUtil.isNullString(_rechargeRecordC2SFileLabelForTransaction)||BTSLUtil.isNullString(_rechargeRecordC2SFileNameForTransaction)||BTSLUtil.isNullString(_masterDirectoryPathAndName)||BTSLUtil.isNullString(_finalMasterDirectoryPath))
			    throw new BTSLBaseException("C2SERPDetail","loadConstantParameters",PretupsErrorCodesI.RECHARGERECORD_COULD_NOT_FIND_DATA_IN_CONSTANTS_FILE);
		    try {_fileEXT = Constants.getProperty("RechargeRecord_C2S_Detail_FILE_EXT");}
			catch(Exception e){_fileEXT =".csv";}
			LOG.debug("loadConstantParameters"," _fileEXT="+_fileEXT);
			try{_maxFileLength = Long.parseLong(Constants.getProperty("RechargeRecord_C2S_Detail_MAX_FILE_LENGTH"));}
			catch(Exception e){_maxFileLength = 1000;}
			LOG.debug("loadConstantParameters"," _maxFileLength="+_maxFileLength);
			LOG.debug("loadConstantParameters"," Required information successfuly loaded from Constants.props...............: ");
		}
		catch(BTSLBaseException be)
		{
			LOG.error("loadConstantParameters", "BTSLBaseException : " + be.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2SERPDetail[loadConstantParameters]","","","","Message:"+be.getMessage());
		    be.printStackTrace();
			throw be;
		}
		catch(Exception e)
		{
			LOG.error("loadConstantParameters", "Exception : " + e.getMessage());
			e.printStackTrace();
			BTSLMessages btslMessage=new BTSLMessages(PretupsErrorCodesI.RECHARGERECORD_ERROR_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2SERPDetail[loadConstantParameters]","","","","Message:"+btslMessage);
		    throw new BTSLBaseException("C2SERPDetail","loadConstantParameters",PretupsErrorCodesI.RECHARGERECORD_ERROR_EXCEPTION);
		}
	    
	}
	   
	/**
	 * This method will create master and child directory at the path defined in Constants.props, if it does not exist
	 * @param p_directoryPathAndName String
	 * @param p_processId String
	 * @param p_beingProcessedDate Date
	 * @throws BTSLBaseException
	 * @return String
	 */
	private static String createDirectory(String p_directoryPathAndName) throws BTSLBaseException
	{
	    if (LOG.isDebugEnabled())
			LOG.debug("createDirectory"," Entered: p_directoryPathAndName="+p_directoryPathAndName);
	    String dirName=null;
	    try
	    {
			boolean success=false;
			File parentDir = new File(p_directoryPathAndName);
			if(!parentDir.exists())
				success=parentDir.mkdirs();
			else
			    success=true;
			if (!success)
			    throw new BTSLBaseException("C2SERPDetail","createDirectory",PretupsErrorCodesI.COULD_NOT_CREATE_DIR);
	    }
		catch(BTSLBaseException be)
		{
			LOG.error("createDirectory", "BTSLBaseException : " + be.getMessage());
			be.printStackTrace();
			throw be;
		}
		catch(Exception ex)
		{
			LOG.error("createDirectory", "Exception : " + ex.getMessage());
		    ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2SERPDetail[createDirectory]","","","","SQLException:"+ex.getMessage());
		    throw new BTSLBaseException("C2SERPDetail","createDirectory",PretupsErrorCodesI.RECHARGERECORD_ERROR_EXCEPTION);
		}//end of catch
		finally
		{
			if(LOG.isDebugEnabled())
			    LOG.debug("createDirectory", "Exiting dirName="+dirName);
		}
	    return dirName;
	}
	   
	/**
	 * This method will fetch all the required transactions data from database 
	 * @param p_con Connection
	 * @param p_beingProcessedDate Date 
	 * @param p_dirPath String 
	 * @param p_fileName String 
	 * @param p_fileLabel String 
	 * @param p_fileEXT String 
	 * @param p_maxFileLength long 
	 * @return void
 	 * @throws SQLException,Exception
	 */
	private static void fetchTransactionData(Connection p_con,Date p_beingProcessedDate,int p_beforeInterval,String p_dirPath,String p_fileName,String p_fileLabel,String p_fileEXT,long p_maxFileLength) throws BTSLBaseException
	{
		if (LOG.isDebugEnabled())
			LOG.debug("fetchChannelTransactionData"," Entered: p_beingProcessedDate="+p_beingProcessedDate+" p_dirPath="+p_dirPath+" p_fileName="+p_fileName+" p_fileLabel="+p_fileLabel+" p_fileEXT="+p_fileEXT+" p_maxFileLength="+p_maxFileLength);
		LOG.debug("fetchChannelTransactionData"," Tax rate used is :"+_rateToCalculateTax);
		//Customer Name,Transaction Amount,Amount Before Tax,COMMISION AMOUNT,TAX ON COMMISION,transaction date,Transaction Type,service type
		//local_index implemented
		StringBuffer channelQueryBuf= new StringBuffer();
		channelQueryBuf.append("  SELECT U.USER_NAME||'|'||sum(CT.TRANSFER_VALUE)/100||'|'||round(sum((CT.TRANSFER_VALUE)*" +_rateToCalculateTax.trim() );
		channelQueryBuf.append(" /(100+"+_rateToCalculateTax.trim()+")/100),2)||'|'|| ");
		channelQueryBuf.append(" SUM(nvl(A.TRANSFER_VALUE/100,0))||'|'||sum(nvl(A.TAX1_VALUE+A.TAX2_VALUE+A.TAX3_VALUE,0)/100)  ");
		channelQueryBuf.append(" ||'|'|| Max(TO_CHAR(CT.TRANSFER_DATE,'DD/MM/YYYY'))||'|'||'C2S'||'|'||CT.SERVICE_TYPE    ");
		channelQueryBuf.append(" FROM C2S_TRANSFERS CT,Users U ,Adjustments A WHERE  CT.transfer_id = A.reference_id(+)  ");
		channelQueryBuf.append(" and CT.transfer_DATE = ? and A.adjustment_DATE = ? ");
		channelQueryBuf.append(" AND CT.TRANSFER_STATUS = '200'  AND nvl(A.ENTRY_TYPE,'DR')='DR'  ");
		channelQueryBuf.append(" AND CT.Sender_id=U.User_ID  ");
		channelQueryBuf.append(" AND U.CATEGORY_CODE = 'CPSHP'  ");
		channelQueryBuf.append(" group by CT.SERVICE_TYPE, u.USER_NAME  ");
		channelQueryBuf.append(" order by CT.SERVICE_TYPE asc  ");
		String c2sSelectQuery = channelQueryBuf.toString();
        if (LOG.isDebugEnabled()) 	
            LOG.debug("fetchChannelTransactionData", "channelQueryBuf select query:" + channelQueryBuf);
		PreparedStatement c2sSelectPstmt=null;
		ResultSet c2sSelectRst = null;

		try
		{
			c2sSelectPstmt=p_con.prepareStatement(c2sSelectQuery,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            LOG.debug("fetchChannelTransactionData","Memory after loading C2S transaction data: Total:"+Runtime.getRuntime().totalMemory()/1049576+" Free:"+Runtime.getRuntime().freeMemory()/1049576+" for date:"+p_beingProcessedDate);

			c2sSelectPstmt.setDate(1,BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate));
			c2sSelectPstmt.setDate(2,BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate));
			c2sSelectRst=c2sSelectPstmt.executeQuery();
		    LOG.debug("fetchChannelTransactionData","Memory after loading C2S transaction data: Total:"+Runtime.getRuntime().totalMemory()/1049576+" Free:"+Runtime.getRuntime().freeMemory()/1049576+" for date:"+p_beingProcessedDate);

		    //method call to write data in the files
			writeDataInFile(p_dirPath,p_fileName,p_fileLabel,p_beingProcessedDate,p_fileEXT,p_maxFileLength,c2sSelectRst);
		    LOG.debug("fetchChannelTransactionData","Memory after writing transaction files: Total:"+Runtime.getRuntime().totalMemory()/1049576+" Free:"+Runtime.getRuntime().freeMemory()/1049576+" for date:"+p_beingProcessedDate);
		}
		catch(BTSLBaseException be)
		{
			LOG.error("fetchChannelTransactionData", "BTSLBaseException : " + be.getMessage());
			be.printStackTrace();
			throw be;
		}
		catch(SQLException sqe)
		{
            LOG.error("fetchChannelTransactionData", "SQLException " + sqe.getMessage());
            sqe.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2SERPDetail[fetchChannelTransactionData]","","","","SQLException:"+sqe.getMessage());
		    throw new BTSLBaseException("C2SERPDetail","fetchChannelTransactionData",PretupsErrorCodesI.RECHARGERECORD_ERROR_EXCEPTION);
		}//end of catch
		catch(Exception ex)
		{
			LOG.error("fetchChannelTransactionData", "Exception : " + ex.getMessage());
		    ex.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2SERPDetail[fetchChannelTransactionData]","","","","SQLException:"+ex.getMessage());
		    throw new BTSLBaseException("C2SERPDetail","fetchChannelTransactionData",PretupsErrorCodesI.RECHARGERECORD_ERROR_EXCEPTION);
		}//end of catch
		finally
		{
			if(c2sSelectRst!=null)
			    try {c2sSelectRst.close();} catch (Exception ex) {ex.printStackTrace();}	
			if(c2sSelectPstmt!=null)
				try {c2sSelectPstmt.close();} catch (Exception ex) {ex.printStackTrace();}	

			if(LOG.isDebugEnabled())LOG.debug("fetchChannelTransactionData", "Exiting ");
		}//end of finally
	}
	   
	/**
	 * @param p_dirPath String
	 * @param p_fileName String
	 * @param p_fileLabel String
	 * @param p_beingProcessedDate Date
	 * @param p_fileEXT String
	 * @param p_maxFileLength long
	 * @param rst1 ResultSet
	 * @param rst2 ResultSet
	 * @return void
  	 * @throws Exception

	 */
	private static void writeDataInFile(String p_dirPath,String p_fileName,String p_fileLabel,Date p_beingProcessedDate,String p_fileEXT,long p_maxFileLength,ResultSet rst1)throws BTSLBaseException
	{
		if (LOG.isDebugEnabled())
			LOG.debug("writeDataInFile"," Entered:  p_dirPath="+p_dirPath+" p_fileName="+p_fileName+" p_fileLabel="+p_fileLabel+" p_beingProcessedDate="+p_beingProcessedDate+" p_fileEXT="+p_fileEXT+" p_maxFileLength="+p_maxFileLength);
		long  recordsWrittenInFile=0;
		PrintWriter out =null;
		int fileNumber = 0;
		String fileName=null;
		File newFile = null;
		String fileData = null;
		String fileHeader=null;
		String fileFooter=null;
		String txnStatus=null;
		String bonusDetails=null;

		try
		{
		    
		    //generating file name
			fileNumber = 1;
			//if the length of file number is 1, two zeros are added as prefix
			if (Integer.toString(fileNumber).length()==1)
			    fileName=p_dirPath+File.separator+p_fileName+"00"+fileNumber+p_fileEXT;
			//if the length of file number is 2, one zero is added as prefix
			else if (Integer.toString(fileNumber).length()==2)
			    fileName=p_dirPath+File.separator+p_fileName+"0"+fileNumber+p_fileEXT;
			//else no zeros are added
			else if (Integer.toString(fileNumber).length()==3)
			    fileName=p_dirPath+File.separator+p_fileName+fileNumber+p_fileEXT;

			LOG.debug("writeDataInFile","  fileName="+fileName);
			    
			newFile =new File(fileName);
			_fileNameLst.add(fileName);
			//
			out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));

			//ID rechargeRecord002 to make addition of header and footer optional on the basis of entry in Constants.props 
			if ("Y".equalsIgnoreCase(Constants.getProperty("C2S_RECORD_ADD_HEADER_FOOTER")))
			{
				fileHeader = constructFileHeader(p_beingProcessedDate,fileNumber,p_fileLabel);
				out.write(fileHeader);
			}
			//traverse first resultset
			while(rst1.next())
			{
				fileData = rst1.getString(1);
					out.write(fileData+"\n");
				recordsWrittenInFile++;
				if(recordsWrittenInFile>=p_maxFileLength)
				{
					//ID rechargeRecord002 to make addition of header and footer optional on the basis of entry in Constants.props 
					if ("Y".equalsIgnoreCase(Constants.getProperty("C2S_RECORD_ADD_HEADER_FOOTER")))
					{
					    fileFooter=constructFileFooter(recordsWrittenInFile);
						out.write(fileFooter);
					}
					recordsWrittenInFile=0;
					fileNumber = fileNumber +1 ;
					out.close();

					//if the length of file number is 1, two zeros are added as prefix
					if (Integer.toString(fileNumber).length()==1)
					    fileName=p_dirPath+File.separator+p_fileName+"00"+fileNumber+p_fileEXT;
					//if the length of file number is 2, one zero is added as prefix
					else if (Integer.toString(fileNumber).length()==2)
					    fileName=p_dirPath+File.separator+p_fileName+"0"+fileNumber+p_fileEXT;
					//else no zeros are added
					else if (Integer.toString(fileNumber).length()==3)
					    fileName=p_dirPath+File.separator+p_fileName+fileNumber+p_fileEXT;

					LOG.debug("writeDataInFile","  fileName="+fileName);
					newFile =new File(fileName);
					_fileNameLst.add(fileName);
					out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
					//ID rechargeRecord002 to make addition of header and footer optional on the basis of entry in Constants.props 
					if ("Y".equalsIgnoreCase(Constants.getProperty("C2S_RECORD_ADD_HEADER_FOOTER")))
					{
						fileHeader = constructFileHeader(p_beingProcessedDate, fileNumber, p_fileLabel);
						out.write(fileHeader);
					}
				}
			}
			

			//if number of records are not zero then footer is appended as file is deleted
			if (recordsWrittenInFile>0)
			{
				//ID rechargeRecord002 to make addition of header and footer optional on the basis of entry in Constants.props 
				if ("Y".equalsIgnoreCase(Constants.getProperty("C2S_RECORD_ADD_HEADER_FOOTER")))
				{
				    fileFooter=constructFileFooter(recordsWrittenInFile);
					out.write(fileFooter);
				}
			}
			else
			{
				if (out!=null)
					out.close();
				newFile.delete();
				_fileNameLst.remove(_fileNameLst.size()-1);
			}			
			if (out!=null)
				out.close();
		}
		catch(Exception e)
		{
		    deleteAllFiles();
			LOG.debug("writeDataInFile", "Exception: " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2SERPDetail[writeDataInFile]","","","","Exception:"+e.getMessage());
		    throw new BTSLBaseException("C2SERPDetail","writeDataInFile",PretupsErrorCodesI.RECHARGERECORD_ERROR_EXCEPTION);
    	}
		finally
		{
			if (out!=null)
				out.close();
			if (LOG.isDebugEnabled())
				LOG.debug("writeDataInFile","Exiting ");
		}
	}
   
	/**
	 * @param p_con Connection
	 * @param p_processId String
	 * @throws BTSLBaseException
	 * @return int
	 */
	private static int markProcessStatusAsComplete(Connection p_con,String p_processId) throws BTSLBaseException
	{
		if (LOG.isDebugEnabled())
			LOG.debug("markProcessStatusAsComplete"," Entered:  p_processId:"+p_processId);
		int updateCount=0;
		Date currentDate=new Date();
		ProcessStatusDAO processStatusDAO=new ProcessStatusDAO();
		_processStatusVO.setProcessID(p_processId);
		_processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
		_processStatusVO.setStartDate(currentDate);
		try
		{
		    updateCount =processStatusDAO.updateProcessDetail(p_con,_processStatusVO);
		}
	    catch(Exception e)
	    {
			e.printStackTrace();
			LOG.error("markProcessStatusAsComplete", "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2SERPDetail[markProcessStatusAsComplete]","","","","Exception:"+e.getMessage());
		    throw new BTSLBaseException("C2SERPDetail","markProcessStatusAsComplete",PretupsErrorCodesI.RECHARGERECORD_ERROR_EXCEPTION);
	    }
        finally
        {
            if (LOG.isDebugEnabled())
                LOG.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
        } // end of finally
        return updateCount;

	}
	   
	/**
	 * This method will delete all the files if some error is encountered 
	 * after file creation and files need to be deleted.
	 * @throws BTSLBaseException  
	 * @return void
	 */
	private static void deleteAllFiles() throws BTSLBaseException
	{
		if (LOG.isDebugEnabled())
			LOG.debug("deleteAllFiles"," Entered: ");
		int size =0;
		if(_fileNameLst!=null)
			size=_fileNameLst.size();		
		if (LOG.isDebugEnabled())
			LOG.debug("deleteAllFiles"," : Number of files to be deleted "+size);
		String fileName=null;
		File newFile = null;
		for(int i=0;i<size;i++)
		{
			try
			{
				fileName = (String) _fileNameLst.get(i);
				newFile =new File(fileName);
				if(newFile!=null)
				{
					 newFile.delete();
					 if (LOG.isDebugEnabled())
						LOG.debug("",fileName+" file deleted");
				}
				else
				{
					if (LOG.isDebugEnabled())
						LOG.debug("",fileName+" is null");
				}
			}
			catch(Exception e)
			{
		        LOG.error("deleteAllFiles", "Exception " + e.getMessage());
				e.printStackTrace();
	            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2SERPDetail[deleteAllFiles]","","","","Exception:"+e.getMessage());
			    throw new BTSLBaseException("C2SERPDetail","deleteAllFiles",PretupsErrorCodesI.RECHARGERECORD_ERROR_EXCEPTION);
			}
		}//end of for loop
		EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2SERPDetail[deleteAllFiles]","","",""," Message: C2SERPDetail process has found some error, so deleting all the files.");
		if (_fileNameLst.isEmpty())
		    _fileNameLst.clear();
		if (LOG.isDebugEnabled())
			LOG.debug("deleteAllFiles"," : Exiting.............................");
	}
	
	/**
	 This method is used to constuct file header 
	 * @param p_beingProcessedDate Date
	 * @param p_fileNumber long
	 * @param p_fileLabel String
	 * @return String
	 */
	private static String constructFileHeader(Date p_beingProcessedDate,long p_fileNumber,String p_fileLabel)throws BTSLBaseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
		SimpleDateFormat sdf1 = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
		StringBuffer fileHeaderBuf = new StringBuffer("");
		if ("Y".equalsIgnoreCase(Constants.getProperty("C2S_RECORD_ADD_HEADER")))
				{
					fileHeaderBuf.append("\n"+" Present Date="+sdf1.format(new Date()));
					fileHeaderBuf.append("\n"+" For Date="+sdf.format(p_beingProcessedDate));
					fileHeaderBuf.append("\n"+" File Number="+p_fileNumber);
				}
		if ("Y".equalsIgnoreCase(Constants.getProperty("C2S_RECORD_ADD_LABELS")))
				{
					fileHeaderBuf.append("\n"+p_fileLabel+"\n");
				}
		if ("Y".equalsIgnoreCase(Constants.getProperty("C2S_RECORD_ADD_HEADER")))
				{
					fileHeaderBuf.append("\n"+"[STARTDATA]"+"\n");
				}
		return fileHeaderBuf.toString(); 
	}
	
	/**
	 This method is used to constuct file footer 
	 * @param p_noOfRecords long
	 * @return String
	 */
	private static String constructFileFooter(long p_noOfRecords)
	{
		StringBuffer fileHeaderBuf = null;
		fileHeaderBuf = new StringBuffer("");
		if ("Y".equalsIgnoreCase(Constants.getProperty("C2S_RECORD_ADD_FOOTER")))
		{
						fileHeaderBuf.append("[ENDDATA]"+"\n");
						fileHeaderBuf.append(" Number of records="+p_noOfRecords);
		}
		return fileHeaderBuf.toString(); 
	}
	
	/**
	 * This method will copy all the created files to another location.
	 * the process will generate files in a particular directroy. if the process thats has to read files strarts before copletion of the file generation,
	 * errors will occur. so a different directory is created and files are moved to that final directory.
	 * @param p_oldDirectoryPath String
	 * @param p_finalDirectoryPath String
	 * @param p_processId String
	 * @param p_beingProcessedDate Date
	 * @throws BTSLBaseException
	 * @return String
	 */
	private static void moveFilesToFinalDirectory(String p_oldDirectoryPath,String p_finalDirectoryPath,String p_processId,Date p_beingProcessedDate) throws BTSLBaseException
	{
		if (LOG.isDebugEnabled())
			LOG.debug("moveFilesToFinalDirectory"," Entered: p_oldDirectoryPath="+p_oldDirectoryPath+" p_finalDirectoryPath="+p_finalDirectoryPath+" p_processId="+p_processId+" p_beingProcessedDate="+p_beingProcessedDate);

		String oldFileName=null;
		String newFileName=null;
		File oldFile=null;
		File newFile=null;
		File parentDir = new File(p_finalDirectoryPath);
		if(!parentDir.exists())
			parentDir.mkdirs();
		p_beingProcessedDate=BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate);
		//child directory name includes a file name and being processed date, month and year
		String oldDirName=p_oldDirectoryPath;
		String newDirName=p_finalDirectoryPath;
		File oldDir = new File(oldDirName);
		File newDir = new File(newDirName);
		if(!newDir.exists())
			newDir.mkdirs();
		
		if(LOG.isDebugEnabled())
		    LOG.debug("moveFilesToFinalDirectory", " newDirName="+newDirName);

		int size=_fileNameLst.size();
		try
		{
			for(int i=0;i<size;i++)
			{
				oldFileName = (String) _fileNameLst.get(i);
				oldFile =new File(oldFileName);
				newFileName=oldFileName.replace(p_oldDirectoryPath,p_finalDirectoryPath);
				newFile=new File(newFileName);
				if(oldFile!=null)
				{
				    oldFile.renameTo(newFile);
				    if (LOG.isDebugEnabled())
						LOG.debug("moveFilesToFinalDirectory"," File "+oldFile+" is moved to "+newFile);
				}
				else
				{
					if (LOG.isDebugEnabled())
						LOG.debug("moveFilesToFinalDirectory"," File"+oldFile+" is null");
				}
				if(oldFile.exists())
					oldFile.delete();
				if(_fileZipAllowed.equalsIgnoreCase("Y"))
				{
					_fileNameLst.remove(i);
					_fileNameLst.add(i, newFileName);
				}
			}//end of for loop
			if(!_fileZipAllowed.equalsIgnoreCase("Y"))
				_fileNameLst.clear();
		    //LOG.debug("moveFilesToFinalDirectory"," File "+oldFileName+" is moved to "+newFileName);
		}
		catch(Exception e)
		{
	        LOG.error("moveFilesToFinalDirectory", "Exception " + e.getMessage());
			e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2SERPDetail[moveFilesToFinalDirectory]","","","","Exception:"+e.getMessage());
		    throw new BTSLBaseException("C2SERPDetail","deleteAllFiles",PretupsErrorCodesI.RECHARGERECORD_ERROR_EXCEPTION);
		}
        finally
        {
			if(oldFile!=null) oldFile = null;
			if(newFile!=null) newFile = null;
			if(parentDir!=null) parentDir = null;
			if(newDir!=null) newDir = null;
			if(oldDir!=null) oldDir = null;
            if (LOG.isDebugEnabled())
                LOG.debug("moveFilesToFinalDirectory", "Exiting.. ");
        } // end of finally

	}
	
	 /**
	 * This method is used to zip files 
	 */
	
	private static void fileZip()throws BTSLBaseException
    {
		if (LOG.isDebugEnabled())
            LOG.debug("fileZip", "Entered.. ");
    	byte[] buffer = new byte[1024];
    	File oldFile=null;
    	FileOutputStream fos=null;
    	ZipOutputStream zos=null;
    	FileInputStream in=null;
    	int size=_fileNameLst.size();				
    	try{
    		for(int i=0;i<size;i++)
			{
    			String finalDirectoryPath=(_fileNameLst.get(i).toString().split("\\."))[0];
	    		fos = new FileOutputStream(finalDirectoryPath+".zip");
	    		zos = new ZipOutputStream(fos);
	    		String file[]=(_fileNameLst.get(i).toString().split("\\\\"));
	    		String fileName=file[file.length-1];
	    		ZipEntry ze= new ZipEntry(fileName);
	    		zos.putNextEntry(ze);
	    		in = new FileInputStream(_fileNameLst.get(i).toString());
	   	   
	    		int len;
	    		while ((len = in.read(buffer)) > 0) {
	    			zos.write(buffer, 0, len);
	    		}   
	    		
	    		zos.flush();
	    		fos.flush();
	    		in.close();
	    		
	    		String oldFileName = (String) _fileNameLst.get(i);
				oldFile =new File(oldFileName);
				if(oldFile.exists())
					oldFile.delete();		    		
			}
    		_fileNameLst.clear();
    	}catch(IOException ex){
    	   LOG.error("fileZip", "Exception " + ex.getMessage());
    	   ex.printStackTrace();
           EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2SERPDetail[fileZip]","","","","Exception:"+ex.getMessage());
		    throw new BTSLBaseException("C2SERPDetail","fileZip",PretupsErrorCodesI.RECHARGERECORD_ERROR_EXCEPTION);
    	}
    	catch(Exception e)
		{
	        LOG.error("fileZip", "Exception " + e.getMessage());
			e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2SERPDetail[fileZip]","","","","Exception:"+e.getMessage());
		    throw new BTSLBaseException("C2SERPDetail","fileZip",PretupsErrorCodesI.RECHARGERECORD_ERROR_EXCEPTION);
		}
    	 finally
         {
 			if(oldFile!=null) oldFile = null;
 			try
 			{
	 			if (in!=null)
	 				in.close();
	 			
	 			if (zos!=null)
	 			{
	 				zos.closeEntry();
	 				zos.close();
	 			}
	 			if (fos!=null)
	 				fos.close();
 			}
 		catch(IOException ex){
      	   ex.printStackTrace();
      	}
             if (LOG.isDebugEnabled())
                 LOG.debug("fileZip", "Exiting.. ");
         } // end of finally
    }
}

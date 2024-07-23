package com.btsl.pretups.processes.clientprocesses;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.GreetingMsgSentLog;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.processes.clientprocesses.businesslogic.GreetingMsgVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

/** GreetingMsgProcess.java
 * Name                                 Date            History
 *------------------------------------------------------------------------
 * Mohd Suhel	           		   02/08/2016       Initial Creation
 *------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Main class for Greeting Msg Sending Process 
 */

public class GreetingMsgProcess 
{
	private static Log _logger = LogFactory.getLog(GreetingMsgProcess.class.getName());
    private static ProcessStatusVO _processStatusVO;
    private static ProcessBL _processBL=null;
    private static PreparedStatement checkUserStmt = null;
    	
	private static GreetingMsgVO _greetingMsgVO;
	private static ReceiverVO _recvVO;
	

	/**
     * to ensure no class instantiation 
     */
    private GreetingMsgProcess(){
    	
    }
    public static void main(String[] args) 
    {
		try
		{
			if(args.length!=2)
			{
				System.out.println("Usage : GreetingMsgProcess [Constants file] [LogConfig file]");
				return;
			}
			File constantsFile = new File(args[0]);
			if(!constantsFile.exists())
			{
				System.out.println(" Constants File Not Found .............");
				return;
			}
					File logconfigFile = new File(args[1]);
			if(!logconfigFile.exists())
			{
				System.out.println(" Logconfig File Not Found .............");
				return;
			}
			ConfigServlet.loadProcessCache(constantsFile.toString(),logconfigFile.toString());
		}//end try
		catch(Exception ex)
		{
			_logger.errorTrace("main", ex);
			System.out.println("Error in Loading Configuration files ...........................: "+ex);
			
			ConfigServlet.destroyProcessCache();
			return;
		}
		try
		{
			process();
		}
		catch(BTSLBaseException be)
		{
			_logger.error("main", "BTSLBaseException : " + be.getMessage());
			
		}
		finally
		{
			if (_logger.isDebugEnabled())
				_logger.info("main"," Exiting");
			ConfigServlet.destroyProcessCache();
		}
	}
	private static void process() throws BTSLBaseException
	{
	    Date processedUpto=null;
	    Date dateCount=null;
	    Date currentDate=null;
	    Connection con= null;
	    String processId=null;
	    boolean statusOk=false;
	    int beforeInterval=0;
		int maxDoneDateUpdateCount=0;
		ProcessStatusDAO processStatusDAO=null;
		String uploadDirPath = Constants.getProperty("GREETING_UPLOAD_DIR_PATH");
		String movedDirPath = Constants.getProperty("GREETING_PROCESSED_DIR_PATH");
		boolean isDaySuccess=false;
		try
		{
		    _logger.debug("process","Memory at startup: Total:"+Runtime.getRuntime().totalMemory()/1049576+" Free:"+Runtime.getRuntime().freeMemory()/1049576);
		    currentDate=new Date();
		    currentDate=BTSLUtil.getSQLDateFromUtilDate(currentDate);
		    
			con = OracleUtil.getSingleConnection();
			if(con==null)
			{
				if (_logger.isDebugEnabled())
					_logger.debug("process"," DATABASE Connection is NULL ");
				EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"GreetingMsgProcess[process]","","","","DATABASE Connection is NULL");
				return;
			}
			//getting process id
       	    processId=ProcessI.GREETING_MSG;
			//method call to check status of the process
       	    _processBL=new ProcessBL();
       	    _processStatusVO=_processBL.checkProcessUnderProcess(con,processId);
			_processStatusVO.setExecutedOn(currentDate);
       	    statusOk=_processStatusVO.isStatusOkBool();
       	    beforeInterval=BTSLUtil.parseLongToInt( _processStatusVO.getBeforeInterval()/(60*24) );
       	    if (statusOk)
       	    {
				
       	        con.commit();
       	        //method call to find maximum date till which process has been executed
       	        processedUpto=_processStatusVO.getExecutedUpto();
	      	    if (processedUpto!=null)
	       	    {
	      	       
	      	    ArrayList<GreetingMsgVO> greetingMsgVOList = null;
	      	    	BufferedReader br =null;
	      	    try {
						
						 File folder = new File(uploadDirPath);
						 greetingMsgVOList = new ArrayList<GreetingMsgVO>(10);
						 for (File fileEntry : folder.listFiles()) {
							if (!fileEntry.isDirectory())
							{
								 if (_logger.isDebugEnabled())
									_logger.debug("process","FILE : "+fileEntry.getName());
							br = new BufferedReader(new FileReader(uploadDirPath+fileEntry.getName()));
						 
						 
						 String line = null;
						 br.readLine();
						 int i = 0;
						 while((line = br.readLine())!=null)
						 {
							 try{
								String greetingMsg = "";
								i=0;
								String[] greetingData = line.split(",");
								_greetingMsgVO = new GreetingMsgVO();
								_greetingMsgVO.setMSISDN(greetingData[0]);
								for(String msgPart : greetingData)
								{
									if(i!=0){
										greetingMsg+=msgPart+",";
									}
									i++;
								}
								if(greetingMsg.length()>0)
									greetingMsg = greetingMsg.substring(0,greetingMsg.length()-1);
								
								_greetingMsgVO.setGreetingMsg(greetingMsg);
								greetingMsgVOList.add(_greetingMsgVO);
							
							}
							 catch(Exception e)
							 {
								 
								 if (_logger.isDebugEnabled())
									_logger.debug("process","FAILURE : "+line+"  : this entry is not valid in File");
							 }
						 }
					}
				}}
				catch(Exception e)
				{
					_logger.errorTrace("process", e);
				}
				finally{
					if(br!=null)
					{
						br.close();
					}
				}
				
				String query = "SELECT MSISDN , STATUS from USERS where MSISDN = ? and USER_TYPE = 'CHANNEL'";
				ListIterator<GreetingMsgVO> iterator = greetingMsgVOList.listIterator();
				PreparedStatement stmt = con.prepareStatement(query);
				ResultSet rst = null;
				while(iterator.hasNext())
				{
					
					GreetingMsgVO _greetingMsgVOtemp = new GreetingMsgVO(); 
					_greetingMsgVOtemp = (GreetingMsgVO)iterator.next();
					try{
					
						validateMsisdnString(con , _greetingMsgVOtemp.getMSISDN());
						
						
						stmt.setString(1, _greetingMsgVOtemp.getMSISDN());
						rst = stmt.executeQuery();
						if(rst.next())
						_greetingMsgVOtemp.setStatus(rst.getString("status"));
						else{
						_greetingMsgVOtemp.setStatus(null);
							
						}
						iterator.set(_greetingMsgVOtemp);
					}
					catch (BTSLBaseException e) {
					
						if (_logger.isDebugEnabled())
							_logger.debug("process","USER NOT FOUND : "+_greetingMsgVOtemp.toString());
						
					}
					
	      	    	
				}
				
				if(stmt!=null)
					stmt.close();
				
			//	ListIterator<GreetingMsgVO> iterators = greetingMsgVOList.listIterator();
				
				Locale defaultLocale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
				PushMessage pushMessage = null;
				//while(iterators.hasNext())
				for(GreetingMsgVO temp : greetingMsgVOList)
				{
	//				GreetingMsgVO temp = iterators.next();
					if(temp.getStatus()!=null){
						if(temp.getStatus().equals("Y") && !temp.getGreetingMsg().equals("")){
							
							String greetingMsgString = temp.getGreetingMsg();
							try{
							int MsgLimitLength = 160;
							if(greetingMsgString.length()>MsgLimitLength)
							{
								List<String> msgList = new ArrayList<>();
								int j =0;
								int limitsLength = greetingMsgString.length()-MsgLimitLength;
								for(int i =0 ; j< limitsLength;i++)
								{
									if(greetingMsgString.charAt(j+MsgLimitLength-1)==' '){
									
										msgList.add(greetingMsgString.substring(j , j+MsgLimitLength-1));
										j+=MsgLimitLength-1;
									}
									else
									{
										int check=0;
										for(int k =j+MsgLimitLength ; k>j;k--)
										{
											_logger.debug("process","HELLO "+k +"and char at "+greetingMsgString.charAt(k));
											if(greetingMsgString.charAt(k)==' '){
												msgList.add(greetingMsgString.substring(j , k));
												j=k;
												check=1;
												break;
											}
										}
										if(check==0)
										{
											msgList.add(greetingMsgString.substring(j , j+MsgLimitLength-1));
											j+=MsgLimitLength-1;
										}
									}
									
								}
								msgList.add(greetingMsgString.substring(j , greetingMsgString.length()));
								for(String msg : msgList)
								{
							
									pushMessage = new PushMessage(temp.getMSISDN() , msg , null , null ,defaultLocale );
									pushMessage.push();
									GreetingMsgSentLog.log(temp.getMSISDN() , msg , "SUCCESS");
								
									
								}
								
							}
							else
							{
								pushMessage = new PushMessage(temp.getMSISDN() , temp.getGreetingMsg() , null , null ,defaultLocale );
								pushMessage.push();
								GreetingMsgSentLog.log(temp.getMSISDN() , temp.getGreetingMsg() , "SUCCESS");
							}
								
							
						
							}
							catch(Exception be)
							{
								_logger.error("PushMessage[pushMessageWithStatus]", "Base Exception while sending message=" + be.getMessage());
								_logger.errorTrace("Process", be);
								GreetingMsgSentLog.log(temp.getMSISDN() , "Problem In Message Sending" , "ERROR");
							}
						}
						else{
							if (_logger.isDebugEnabled())
								_logger.debug("process",temp.getMSISDN()+" is not Valid");
							if(temp.getGreetingMsg().equals("") && !temp.getStatus().equals("Y"))
								GreetingMsgSentLog.log(temp.getMSISDN() , "Message is Null and MSISDN is not ACTIVE Status is : "+temp.getStatus() , "FAILED");
							else if(temp.getGreetingMsg().equals(""))	
								GreetingMsgSentLog.log(temp.getMSISDN() , "Message is Null" , "FAILED");
							else
								GreetingMsgSentLog.log(temp.getMSISDN() , "This MSISDN is not ACTIVE , Status is : "+temp.getStatus() , "FAILED");
						}
					}else{
						GreetingMsgSentLog.log(temp.getMSISDN() , "This MSISDN Not Exist" , "FAILED");
					}
					
				
				}
	      	    	
	       	    // moving Procesed file to Other Directory
				moveFilesToFinalDirectory(uploadDirPath ,movedDirPath);
				
	      	   
	      	   }
	      	    else
			        throw new BTSLBaseException("GreetingMsgProcess","process",PretupsErrorCodesI.GREETMSG_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
       	    }
		}//end of try
		catch(BTSLBaseException be)
		{
			_logger.error("process", "BTSLBaseException : " + be.getMessage());
			
			//throw be;
		}
		catch(Exception e)
		{

			_logger.error("process", "Exception : " + e.getMessage());
		    
			EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"GreetingMsgProcess[process]","","",""," GreetingMsgProcess process could not be executed successfully.");
	        throw new BTSLBaseException("GreetingMsgProcess","process",PretupsErrorCodesI.GREETMSG_ERROR_EXCEPTION);
		}
		finally
		{
		    //if the status was marked as under process by this method call, only then it is marked as complete on termination
		    if (statusOk)
		    {
				
								
		        try
		        {
			        if (markProcessStatusAsComplete(con,processId)==1)
			        	try{con.commit();} catch(Exception e){
							_logger.errorTrace("GreetingMsgProcess", e);
						}
			        else
			        	try{con.rollback();} catch(Exception e){
						_logger.errorTrace("GreetingMsgProcess", e);
							
						}
		        }
		        catch(Exception e)
		        {
		           _logger.errorTrace("GreetingMsgProcess", e);
						
		        }
		        try
		        {
		            if(con!=null)
					con.close();
		        }
		        catch(Exception ex)
		        {
					if(_logger.isDebugEnabled())_logger.debug("process", "Exception closing connection ");
		        }
		    }
		    _logger.debug("process","Memory at end: Total:"+Runtime.getRuntime().totalMemory()/1049576+" Free:"+Runtime.getRuntime().freeMemory()/1049576);
			if(_logger.isDebugEnabled())_logger.debug("process", "Exiting..... ");
		}
	}
	
	
	private static void moveFilesToFinalDirectory(String p_oldDirectory , String p_newFileDirectory)
	{
			    
				File newMovedFile = null;
				
				try{
				
				File fileDir = new File(Constants.getProperty("GREETING_PROCESSED_DIR_PATH"));
				
				if(!fileDir.exists())fileDir.mkdirs();
				
					if(!fileDir.exists())
						{
							if(_logger.isDebugEnabled())_logger.debug("process", " NOT MOVED :");
					
						
						}
				
				File folder = new File(p_oldDirectory);
				
				for (File fileEntry : folder.listFiles()) {
					if (!fileEntry.isDirectory())
						{
						newMovedFile = new File(p_newFileDirectory+fileEntry.getName());
						
						fileEntry.renameTo(newMovedFile);
						}
					}
				}
				catch(Exception e)
				{
					if(_logger.isDebugEnabled())_logger.debug("process", "Problem in Moving Files to new Folder");
					_logger.errorTrace("process", e);
						
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
		if (_logger.isDebugEnabled())
			_logger.debug("markProcessStatusAsComplete"," Entered:  p_processId:"+p_processId);
		int updateCount=0;
		Date currentDate=new Date();
		ProcessStatusDAO processStatusDAO=new ProcessStatusDAO();
		_processStatusVO.setProcessID(p_processId);
		_processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
		_processStatusVO.setStartDate(currentDate);
		_processStatusVO.setExecutedUpto(currentDate);
		try
		{
		    updateCount =processStatusDAO.updateProcessDetail(p_con,_processStatusVO);
		}
	    catch(Exception e)
	    {
			
			_logger.error("markProcessStatusAsComplete", "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"GreetingMsgProcess[markProcessStatusAsComplete]","","","","Exception:"+e.getMessage());
		    throw new BTSLBaseException("GreetingMsgProcess","markProcessStatusAsComplete",PretupsErrorCodesI.GREETMSG_ERROR_EXCEPTION);
	    }
        finally
        {
            if (_logger.isDebugEnabled())
                _logger.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
        } // end of finally
        return updateCount;
	}
	/**
	 * This method will fetch all the required transactions data from database 
	 * @param p_con Connection
	 * @param p_beingProcessedDate Date 
	 * @return void
 	 * @throws BTSLBaseException
	 */
	
	
	private static Boolean validateMsisdnString(Connection p_con , String p_msisdn) throws BTSLBaseException
	{
		
		if(_logger.isDebugEnabled()) _logger.debug("validateMsisdn","Entered for p_msisdn= "+p_msisdn);
		String[] strArr=null;
		try
		{
		    if (BTSLUtil.isNullString(p_msisdn))
				throw new BTSLBaseException("PretupsBL","validateMsisdn",PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_BLANK);
			p_msisdn=PretupsBL.getFilteredMSISDN(p_msisdn);
			p_msisdn=PretupsBL._operatorUtil.addRemoveDigitsFromMSISDN(p_msisdn);
			if((p_msisdn.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue() || p_msisdn.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue()))
			{
				System.out.println(p_msisdn.length()+"length");
				if(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue()!=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue())
				{
					strArr=new String[]{p_msisdn,String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue()),String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_MSISDN_LENGTH_CODE))).intValue())};
					throw new BTSLBaseException("PretupsBL","validateMsisdn",PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_NOTINRANGE,0,strArr,null);
				}
				else
				{
					strArr=new String[]{p_msisdn,String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_MSISDN_LENGTH))).intValue())};
					throw new BTSLBaseException("PretupsBL","validateMsisdn",PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_LEN_NOTSAME,0,strArr,null);
				}
			}
			try
			{
				long lng=Long.parseLong(p_msisdn);
			}
			catch(Exception e)
			{
				strArr=new String[]{p_msisdn};
				throw new BTSLBaseException("PretupsBL","validateMsisdn",PretupsErrorCodesI.CHNL_ERROR_RECR_MSISDN_NOTNUMERIC,0,strArr,null);
			}
			
		
			
			
			
		}
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch(Exception e)
		{
			
			_logger.error("validateMsisdn","  Exception while validating msisdn :"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PretupsBL[validateMsisdn]","","","","Exception while validating msisdn" +" ,getting Exception="+e.getMessage());
			throw new BTSLBaseException("PretupsBL","validateMsisdn",PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		}	
		if(_logger.isDebugEnabled()) _logger.debug("validateMsisdn","Exiting for p_msisdn= "+p_msisdn);
	
		return false;
	}
	
	
	
}

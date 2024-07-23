/**AmbiguousServerP2PUpdateProcess.java
 * Name                                 Date            History
 *------------------------------------------------------------------------
 * Rajvi Desai						17/04/2015				Initial Creation
 *------------------------------------------------------------------------
 * Copyright (c) 2015 Mahindra Comviva Technologies Limited.
 * Main class for Airtel Ghana Ambiguous Server Update Process
 */

package com.client.pretups.processes.clientprocesses;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.pretups.p2p.logging.AmbiguousServerP2PUpdateProcesslog;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.client.pretups.processes.SFTPUtils;

public class AmbiguousServerP2PUpdateProcess 
{
    private static Log LOG = LogFactory.getLog(AmbiguousServerP2PUpdateProcess.class.getName());
    final static String className="AmbiguousServerP2PUpdateProcess";
    static int p_processInterval;
    static String p_ftpRequired = null;
    private static long _sequenceNumber; 
    public  static HashMap<String , String> _ambP2pConsParamMap=null;
	public String _serverIP="";
	public String _serverUser="";
	public String _serverPass="";
	public String _folderLocation="";
	public String _localFolderPath="";
	public String _destinationFolderPath="";
	public static String _entirepath=null;
	String _processId=null;
    public AmbiguousServerP2PUpdateProcess() {
		
	}


	/**
	 * Method main
	 * This is the Main by which the execution of process will start
	 * @param args String[]
	 * @return void
	 * @author rajvi.desai
	 */
    public static void main(String[] args) 
    {
    	final String methodName="main";
    	
    	try
	    {
            if(args.length!=3)
			{
				System.out.println("Usage : "+className+" [Constants file] [LogConfig file] [ProcessInterval] [FTPRequired(Y/N)]");
				return;
			}
            File constantsFile = new File(args[0]);
			if(!constantsFile.exists())
			{
			    System.out.println(className+" "+methodName+" Constants file not found on location:: "+constantsFile.toString());
				return;
			}
			File logconfigFile = new File(args[1]);
			if(!logconfigFile.exists())
			{
				System.out.println(className+" "+methodName+" Logconfig file not found on location:: "+logconfigFile.toString());
				return;
			}
			p_ftpRequired = args[2];
			if(BTSLUtil.isNullString(p_ftpRequired))
			{
			    System.out.println(className+" "+methodName + "FTP is Required or not should be given as a input parameter as Y or N");
				return;
			}
			//To load the process & constants file cache.
			ConfigServlet.loadProcessCache(constantsFile.toString(),logconfigFile.toString());
	    }
    	catch(Exception e)
		{
			if (LOG.isDebugEnabled())
				LOG.debug(methodName," Error in Loading Files ...........................: "+e.getMessage());
			AmbiguousServerP2PUpdateProcesslog.log(className, methodName, " Error in Loading Files ...........................: "+e.getMessage());
			e.printStackTrace();
			ConfigServlet.destroyProcessCache();
			return;
		}// end of catch
    	try
    	{
    		
	        new AmbiguousServerP2PUpdateProcess().process();
	        
	        if(PretupsI.YES.equalsIgnoreCase(p_ftpRequired))
	        {
	        	initiate(_entirepath);
	        }
	    }
        catch(BTSLBaseException be)
		{
		    System.out.println(className+methodName+" BTSLBaseException be="+be.getMessage());
		    be.printStackTrace();
		}
		catch(Exception e)
		{
		    System.out.println(className+methodName+" Exception e="+e.getMessage());
		    e.printStackTrace();
		    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+"["+methodName+"]","","","","Exception:"+e.getMessage());
		}
		finally
		{
			ConfigServlet.destroyProcessCache();
			freeStaticMemory();
			
		}
    }

    /**
	 * Method process
	 * This is the method by which the process will start
	 * @return void
	 * @throws BTSLBaseException
	 */
    private void process() throws BTSLBaseException
    {
    	final String methodName="process";
        Connection con=null;
        ProcessStatusVO processVO=null;
        ProcessStatusDAO processDAO=null;
        Date executeUpto=null;
        Date currentDate=null;
        Timestamp _date=null;
        Timestamp startTime=null;
        File createDir=null;
    	String [] files = null;
    	int beforeInterval=0;
        try
        {
            con=OracleUtil.getSingleConnection();
            if(con==null)
			{
				if (LOG.isDebugEnabled())
					LOG.debug(methodName," DATABASE Connection is NULL ");
				AmbiguousServerP2PUpdateProcesslog.log(className, methodName, " DATABASE Connection is NULL");
				EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+"["+methodName+"]","","","","DATABASE Connection is NULL");
				return;
			}
            _processId=ProcessI.AMB_P2P_SERVER_UPDATE;
            ProcessBL processBL=new ProcessBL();
            processVO=processBL.checkProcessUnderProcess(con,ProcessI.AMB_P2P_SERVER_UPDATE);
            executeUpto = processVO.getExecutedUpto();
            startTime=(BTSLUtil.getTimestampFromUtilDate(BTSLUtil.getSQLDateFromUtilDate(executeUpto)));
            beforeInterval=(int)processVO.getBeforeInterval()/(60*24);
            if(processVO.isStatusOkBool())
	        {
	            con.commit();
	            if (executeUpto!=null)
	       	    {
	    			Calendar cal = Calendar.getInstance();
	    			currentDate=cal.getTime(); //Current Date
	    			currentDate=BTSLUtil.addDaysInUtilDate(currentDate,-beforeInterval);
	    			_date=(BTSLUtil.getTimestampFromUtilDate(BTSLUtil.getSQLDateFromUtilDate(currentDate)));
	       	    }
	      	    else
	      	    {
					if (LOG.isDebugEnabled()) LOG.debug(className+"["+methodName+"]"," Date till which process has been executed is not found.");
					return;
	      	    }
	            loadConstantParametres();
	            //Generate the Time Intervals corresponding to the ExecutetUpto time in Process status table
	            this.processIntervals(con,startTime,processVO,_date);
				try
				{
					createDir = new File(_ambP2pConsParamMap.get("AMB_FILE_PATH"));		    		
            		files = createDir.list();
            		int j=files.length;
            		for(int i=0 ; i<j; i++)
            		{
            			moveFilesToFinal(_ambP2pConsParamMap.get("AMB_FILE_PATH"),_ambP2pConsParamMap.get("AMB_FILE_FINAL_PATH_P2P"),files[i]);
            		}
				}
				catch(Exception be)
				{
					if (LOG.isDebugEnabled())
						LOG.error(methodName,"BTSLBaseException: "+be.getMessage());
				}
	        }
            else
            {
                throw new BTSLBaseException(className,methodName,"Process is already running..");
            }
        }
        catch(BTSLBaseException be)
		{
			if (LOG.isDebugEnabled())
				LOG.error(methodName,"BTSLBaseException: "+be.getMessage());
			try{if(con!=null) con.rollback();}catch(Exception e){}
			throw be;
		} 
        catch(Exception e)
		{
			if (LOG.isDebugEnabled())
				LOG.error(methodName,"Exception: "+e.getMessage());
			e.printStackTrace();
			try{if(con!=null) con.rollback();}catch(Exception e1){}
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+"["+methodName+"]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(className,methodName,"Exception :"+e.getMessage());
		}
		finally
		{
		    processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
            try
            {
                processDAO=new ProcessStatusDAO();
                if(processDAO.updateProcessDetail(con,processVO)>0)
                    con.commit();
                else
                    con.rollback();
            }
            catch(Exception e)
            {
                if (LOG.isDebugEnabled())
                	LOG.debug(methodName," Exception in update process detail"+e.getMessage());
            }
           if (LOG.isDebugEnabled())
        	   LOG.debug(methodName," Exiting");
           AmbiguousServerP2PUpdateProcesslog.log(className, methodName,"Exiting");
			try{if(con!=null)try{con.close();}catch(SQLException e1){}}catch(Exception e){}
		}
    }
    
    /**
	 * Method processIntervals
	 * This method is used to genererate the record files and update the info in DB
	 * @param p_con Connection
	 * @param startTime Timestamp
	 * @param p_processVO ProcessStatusVO
	 * @param p_executedUpto Date
	 * @throws BTSLBaseException
	 */
    private void processIntervals(Connection p_con,Timestamp startTime,ProcessStatusVO p_processVO ,Timestamp p_currentDate) throws BTSLBaseException
    {
    	final String methodName="processIntervals";
        if (LOG.isDebugEnabled())
        	LOG.debug(methodName,"p_executedUpto :"+p_currentDate);
        AmbiguousServerP2PUpdateProcesslog.log(className, methodName,"p_executedUpto :"+p_currentDate);
        String fileNames[]=null;
        try
        {
            ArrayList<TransferVO> recordVOList=null;
            ProcessStatusDAO processDAO=new ProcessStatusDAO();
            long fileCreationSleepTime=10;
			if(!BTSLUtil.isNullString(_ambP2pConsParamMap.get("AMB_FILE_CREATE_SLEEP_TIME")) && BTSLUtil.isNumeric(_ambP2pConsParamMap.get("AMB_FILE_CREATE_SLEEP_TIME")))
            	fileCreationSleepTime=Long.parseLong(_ambP2pConsParamMap.get("AMB_FILE_CREATE_SLEEP_TIME")); 
            	try{
            		if (LOG.isDebugEnabled())
            			LOG.debug(methodName,"Process will continue after "+fileCreationSleepTime +"milliseconds to avoid overwriting");
            		AmbiguousServerP2PUpdateProcesslog.log(className, methodName,"Process will continue after "+fileCreationSleepTime +"milliseconds to avoid overwriting");
            		
            		//Below sleep is added to avoid overwrite of the file of the previous part if file name is upto the minute
            		Thread.sleep(fileCreationSleepTime);
            		recordVOList=null;
            		recordVOList=this.getDataForAMBUpdate(p_con,startTime,p_currentDate);
					if(recordVOList.isEmpty())
					{
						fileNames=this.generateAMBRecords(recordVOList,p_currentDate);
						if (LOG.isDebugEnabled())
							LOG.debug(methodName,"\n --------------No Records were found------------");
						p_processVO.setProcessStatus(ProcessI.STATUS_UNDERPROCESS);
        				p_processVO.setExecutedUpto(p_currentDate);
        				p_processVO.setExecutedOn(new Date());
        				if(processDAO.updateProcessDetail(p_con,p_processVO)>0)
        					p_con.commit();
        				else
        				{
        					p_con.rollback();
        					if(fileNames!=null)
                            this.deleteFiles(fileNames);
        				}
					}
					else if(recordVOList!=null && !recordVOList.isEmpty())
            		{
            			fileNames=this.generateAMBRecords(recordVOList,p_currentDate);
            			if(!recordVOList.isEmpty())
            			{
            				p_processVO.setProcessStatus(ProcessI.STATUS_UNDERPROCESS);
            					
            				p_processVO.setExecutedUpto(p_currentDate);
            				p_processVO.setExecutedOn(new Date());
            				if(processDAO.updateProcessDetail(p_con,p_processVO)>0)
            					p_con.commit();
            				else
            				{
            					p_con.rollback();
            					if(fileNames!=null)
                                this.deleteFiles(fileNames);
            				}
            			}
            			else
            			{
            				p_con.rollback();
            				if(fileNames!=null)
            					this.deleteFiles(fileNames);
            			}
            		}
            		else 
            		{
            			if((recordVOList.size()>=1)&&PretupsI.YES.equals(_ambP2pConsParamMap.get("BLANK_AMB_FILE_REQUIRED")))
                			fileNames=this.generateAMBRecords(recordVOList,p_currentDate);
                    
                  //No Data found in queue VO corresponding to the intervals generated
                			if (LOG.isDebugEnabled())
                				LOG.debug(methodName,"No Data found");
                			AmbiguousServerP2PUpdateProcesslog.log(className,methodName, "No Data found");
                		 
            		}
            
            	}
            	catch(Exception ex)
            	{
            		 p_processVO.setExecutedUpto(p_currentDate);
            		 
            			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+"["+methodName+"]","","","","Exception:"+ex.getMessage());
            			LOG.error(methodName,"Error Found during Processing of AMB Server Update Process"+ ex.getMessage());
            		throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
            	}
				
        	//If the process is successfull generate the ALARM
         	EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,className+"["+methodName+"]","","","",className+" process has been executed successfully.");
        }
        catch(BTSLBaseException be)
		{
          try{if(p_con!=null) p_con.rollback();}catch(Exception e){}
       
          if (LOG.isDebugEnabled())
        	  LOG.error(methodName," "+be.getMessage());
          throw be;
		}
        catch(Exception e)
		{
            try{if(p_con!=null) p_con.rollback();}catch(Exception ex){}
			//Remove the files defined in string array
            if(fileNames!=null)
                try{this.deleteFiles(fileNames);} catch(Exception ex1){}
            if (LOG.isDebugEnabled())
            	LOG.error(methodName," "+e.getMessage());
			e.printStackTrace();
			throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_FILE_GENERATION_ERROR);
		}
     	finally
		{
			if (LOG.isDebugEnabled())
				LOG.debug("processIntervals"," Exiting");
			AmbiguousServerP2PUpdateProcesslog.log(className,methodName,"Exiting");
		}
       }
    
    
    /**
	 * Method deleteFiles
	 * This method is used to delete the cdr files generated if any exception is encountered
	 * @param p_files String[]
	 * @return void
	 * @throws BTSLBaseException
	 */

    private void deleteFiles(String [] p_files)
    throws BTSLBaseException
    {
    	final String methodName="deleteFiles";
        if (LOG.isDebugEnabled())
        	LOG.debug("deleteFiles"," Entered p_files="+p_files);
        AmbiguousServerP2PUpdateProcesslog.log(className,methodName," Entered p_files="+p_files);
        File file=null;
        try
        {
            if(p_files!=null) //If any file in the array delete it one by one 
            {
                for(int i=0;i<p_files.length;i++)
                {
                    file=new File(p_files[i]);
                    if(file.exists())
                        file.delete();
                }
            }
        }
        catch(Exception e)
        {
            file=null;
            if (LOG.isDebugEnabled())
            	LOG.debug(methodName," "+e.getMessage());
			e.printStackTrace();
			throw new BTSLBaseException(className,methodName,"Execption :"+e.getMessage());
        }
        finally
        {
            file=null;
            if (LOG.isDebugEnabled())
            	LOG.debug("deleteFiles"," Exited");
            AmbiguousServerP2PUpdateProcesslog.log(className,methodName," Exited");
        }
    }
    
    /**
     * getDataForAMBUpdate
     * @param p_con
     * @param p_startTime
     * @param p_endTime
     * @param p_date
     * @return
     */
    private ArrayList<TransferVO> getDataForAMBUpdate(Connection p_con,Timestamp p_startTime,Date p_currentDate) throws BTSLBaseException
    {
    	final String methodName="getDataForAMBUpdate";
    	if (LOG.isDebugEnabled())
    		LOG.debug(methodName, "p_startTime :: "+p_startTime+"p_date :: "+p_currentDate);
    	 AmbiguousServerP2PUpdateProcesslog.log(className,methodName,"p_startTime :: "+p_startTime+"p_date :: "+p_currentDate);
    	StringBuffer c2sQueryBuf= null;
    	ResultSet rs=null;
  		PreparedStatement pstmt = null;
    	ArrayList<TransferVO> AMBRecordlist =null;
    	TransferVO transferVO = null;
    	String selectDataQuery =null;
    	try{
    		AMBRecordlist=new ArrayList<TransferVO>();
 	     
            /*****Query Optimization *******/
            c2sQueryBuf=new StringBuffer(" select st.TRANSFER_ID,st.TRANSFER_DATE,st.SENDER_MSISDN,st.RECEIVER_MSISDN,st.TRANSFER_VALUE AS TRANSFER_VALUE,ti.INTERFACE_REFERENCE_ID ");
    		c2sQueryBuf.append(" FROM SUBSCRIBER_TRANSFERS st,TRANSFER_ITEMS ti WHERE st.TRANSFER_DATE >= trunc(?) ");
    		c2sQueryBuf.append(" AND st.TRANSFER_STATUS IN('"+_ambP2pConsParamMap.get("AMB_STATUS_FETCH")+"')");
    		c2sQueryBuf.append(" AND st.TRANSFER_ID=ti.TRANSFER_ID AND ti.USER_TYPE='RECEIVER' AND st.TRANSFER_DATE <= trunc(?) ORDER BY st.TRANSFER_DATE_TIME ");
    		
    		selectDataQuery = c2sQueryBuf.toString();    
    		if (LOG.isDebugEnabled())
    			LOG.debug(methodName, "QUERY selectDataQuery=" + selectDataQuery);

			pstmt = p_con.prepareStatement(selectDataQuery);
			pstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getUtilDateFromTimestamp(p_startTime)));
			pstmt.setDate(2,BTSLUtil.getSQLDateFromUtilDate(p_currentDate));
		
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				transferVO = new TransferVO();
				transferVO.setTransferID(rs.getString("transfer_id"));
				transferVO.setTransferDate(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("transfer_date")));	
				transferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
				transferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
				transferVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getInt("transfer_value")));
				transferVO.setInterfaceReferenceId(rs.getString("interface_reference_id"));
				AMBRecordlist.add(transferVO);
			}

			if (LOG.isDebugEnabled())
				LOG.debug("Data from query generated", "QUERY selectDataQuery=" + AMBRecordlist);
	    	 AmbiguousServerP2PUpdateProcesslog.log(className,"Data from query generated","QUERY selectDataQuery=" + AMBRecordlist);
	    }
	    catch (Exception ex)
		{
	    	LOG.error(methodName, "Exception : " + ex);
			ex.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+"["+methodName+"]","","","","Exception:"+ex.getMessage());
		throw new BTSLBaseException(className,"processIntervals",PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
		} 
		finally
		{
		    try{if (rs != null){rs.close();}} catch (Exception e){}
		    try{if (pstmt != null){pstmt.close();}} catch (Exception e){}
			if (LOG.isDebugEnabled())
				LOG.debug(methodName, "Exiting AMBRecordlist.size="+AMBRecordlist.size());
			 AmbiguousServerP2PUpdateProcesslog.log(className,methodName,"Exiting AMBRecordlist.size="+AMBRecordlist.size());
		}
		return AMBRecordlist;
	}
    
    /**
     * generateAMBRecords
     * @param p_ambRecordList
     * @param p_intervalVO
     * @return
     * @throws BTSLBaseException
     */
    public String[] generateAMBRecords(ArrayList<TransferVO> p_ambRecordList,Date p_currentDate) 
    throws BTSLBaseException
    {
    	final String methodName="generateAMBRecords";
        if(LOG.isDebugEnabled())
        	LOG.debug(methodName ,"Entered with p_queueVOList= "+p_ambRecordList);
        AmbiguousServerP2PUpdateProcesslog.log(className,methodName,"Entered with p_queueVOList= "+p_ambRecordList);
        String fileArr[]=null; //stores the name of the CDR files generated
        int fileSize=0;
        int recordSize=0;
        try
	    {
	         try
	        {
	            recordSize=Integer.parseInt(_ambP2pConsParamMap.get("AMB_RECORD_SIZE"));
	        }
	        catch(Exception e)
	        {
	            throw new BTSLBaseException(className,methodName,"RECORD_SIZE is invalid in IN file");
	        }
	        try
	        {
	            fileSize=Integer.parseInt(_ambP2pConsParamMap.get("AMB_FILE_SIZE"));
	            if(fileSize<1) // the size of the file should be at least 1 MB
	            {
	                throw new BTSLBaseException(className,methodName,"FILE_SIZE should be greater than or equal to 1");
	            }
	        }
	        catch(Exception e)
	        {
	            throw new BTSLBaseException(className,methodName,"Invalid FILE_SIZE in IN File");
	        }
	        if(p_ambRecordList!=null)
	        {
	        		fileArr=new String[1];
	                fileArr[0]=writeAMBData(p_ambRecordList,generateFileName(p_currentDate),recordSize);
	            }
	        else {
	        	fileArr=new String[1];
                fileArr[0]=writeAMBData(p_ambRecordList,generateFileName(p_currentDate),recordSize);
	        }
	        
	        
	    }
        catch(BTSLBaseException be)
        {
            if (LOG.isDebugEnabled())
            	LOG.error(methodName," "+be.getMessage());
			throw be;
        }
        catch(Exception e)
		{
			if (LOG.isDebugEnabled())
				LOG.error(methodName," "+e.getMessage());
			e.printStackTrace();
			throw new BTSLBaseException(className,methodName,"Exception e:"+e.getMessage());
		}
        finally
        {
            if(LOG.isDebugEnabled())
            	LOG.debug("generateAMBRecords" ,"Exited ********");
            AmbiguousServerP2PUpdateProcesslog.log(className,methodName,"Exited ********");
        }
    return fileArr;
    }
    
    /**
     * generateFileName() method is used for generation the File name on the basis
     *  of processing date  and Record collection start time and end time 
     * @param IntervalTimeVO p_intervalVO
     * @return String parameter as a File name 
     * @throws BTSLBaseException
     */
    private String generateFileName(Date p_currentDate) throws BTSLBaseException
    {
    	final String methodName="generateFileName";
        if(LOG.isDebugEnabled())
        	LOG.debug(methodName ,"Entered");
        AmbiguousServerP2PUpdateProcesslog.log(className,methodName,"Entered ********");
        StringBuffer strBuffFileName=null;	
        try
	    {
	        strBuffFileName=new StringBuffer(_ambP2pConsParamMap.get("AMB_P2P_FILE_NAME"));
	        String modifiedDate= new SimpleDateFormat("yyyyMMdd").format(p_currentDate);
	        
	        strBuffFileName.append("_");
	        strBuffFileName.append(modifiedDate);
	    }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new BTSLBaseException(className,methodName,"Exception e:"+e.getMessage());
        }
        finally
        {
	    if(LOG.isDebugEnabled())
	    	LOG.debug(methodName ,"Exited fileName="+strBuffFileName.toString());
	    AmbiguousServerP2PUpdateProcesslog.log(className,methodName,"Exited fileName="+strBuffFileName.toString());
        }
        return strBuffFileName.toString();
    }
    
    /**
     * This methods is used for write AMB Record in text files 
     * @param p_queueVOList
     * @param p_fileName
     * @param p_recordSize
     * @return String
     * @throws BTSLBaseException
     */
    private String writeAMBData(List<TransferVO> p_ambRecordVOList,String p_fileName,int p_recordSize) throws BTSLBaseException
    {
    	final String methodName="writeAMBData";
        if(LOG.isDebugEnabled())
        	LOG.debug(methodName ,"Entered with p_ambRecordVOList= "+p_ambRecordVOList +" p_ambRecordVOList.size="+p_ambRecordVOList.size()+"p_fileName="+p_fileName+"p_recordSize="+p_recordSize);
        AmbiguousServerP2PUpdateProcesslog.log(className,methodName,"Entered with p_ambRecordVOList= "+p_ambRecordVOList +" p_ambRecordVOList.size="+p_ambRecordVOList.size()+"p_fileName="+p_fileName+"p_recordSize="+p_recordSize);
        PrintWriter pw=null;
        File file=null;
        String fullFileName=null;
        String modifiedDate=null;
        
        StringBuffer AMBRecordBuff=null;
        TransferVO transferVO = null;
        try
        {
			fullFileName = p_fileName+"."+_ambP2pConsParamMap.get("AMB_FILE_EXTN");
			_sequenceNumber=1;
			_entirepath=_ambP2pConsParamMap.get("AMB_FILE_FINAL_PATH_P2P") + fullFileName;
			file = new File(_ambP2pConsParamMap.get("AMB_FILE_PATH") + fullFileName);
			pw= new PrintWriter(new File(_ambP2pConsParamMap.get("AMB_FILE_FINAL_PATH_P2P") + fullFileName));
			if(PretupsI.YES.equals(_ambP2pConsParamMap.get("AMB_HEADER_DISPLAY")))
			{
			pw.println(_ambP2pConsParamMap.get("AMB_HEADER_PARAMETER"));
			}
			if(p_ambRecordVOList!=null)
            {		
	            int size=p_ambRecordVOList.size();
                for(int i=0;i<size;i++)
	            {
                	transferVO=p_ambRecordVOList.get(i);
                    try
            	    {
                    	AMBRecordBuff=new StringBuffer();
                    	AMBRecordBuff.append(transferVO.getTransferID());
                    	AMBRecordBuff.append(",");
                    	modifiedDate= new SimpleDateFormat("MM/dd/yyyy").format(transferVO.getTransferDate());
                    	AMBRecordBuff.append(modifiedDate);
                    	AMBRecordBuff.append(",");
                        AMBRecordBuff.append(transferVO.getTransferValueStr());
                    	AMBRecordBuff.append(",");
                    	AMBRecordBuff.append(transferVO.getSenderMsisdn());
                    	AMBRecordBuff.append(",");
                    	AMBRecordBuff.append(transferVO.getReceiverMsisdn());
                    	AMBRecordBuff.append(",");
                    	AMBRecordBuff.append(transferVO.getInterfaceReferenceId());
                    	pw.println(AMBRecordBuff.toString());
                    	_sequenceNumber++;
                    	AmbiguousServerP2PUpdateProcesslog.successLog(className,methodName,AMBRecordBuff.toString());
            	    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                        throw new BTSLBaseException(className,methodName,"Exception e:"+e.getMessage());
                    }
	            }
            }
			pw.flush();
        }
        catch(BTSLBaseException be)
        {
            if (LOG.isDebugEnabled())
            	LOG.error(methodName,"BTSLBaseException::"+be.getMessage());
            if(pw!=null) { try{ pw.close();} catch (Exception e) {}}
		    try{if(file!=null) file.delete();} catch(Exception e){}
			throw be;
        }
        catch(Exception e)
        {
            if (LOG.isDebugEnabled())
            	LOG.error(methodName,"Exception::"+e.getMessage());
			e.printStackTrace();
			if(pw!=null) { try{ pw.close();} catch (Exception ex) {}}
		    try{if(file!=null) file.delete();} catch(Exception ex1){}
			throw new BTSLBaseException(className,methodName,"Exception e:"+e.getMessage());
        }
        finally
        {
            file=null;
            if(pw!=null) { try{ pw.close();} catch (Exception e) {} }
            if(LOG.isDebugEnabled())
            	LOG.debug(methodName ,"Exited file Name="+_ambP2pConsParamMap.get("AMB_FILE_PATH")+fullFileName);
            AmbiguousServerP2PUpdateProcesslog.log(className,methodName,"Exited file Name="+_ambP2pConsParamMap.get("AMB_FILE_PATH")+fullFileName);
        }
        return _ambP2pConsParamMap.get("AMB_FILE_PATH")+fullFileName;
    }
    
    /**
     * loadConstantParametres
     * @throws BTSLBaseException
     */
    public static void loadConstantParametres() throws BTSLBaseException
    {
    	final String methodName="loadConstantParametres";
    	if (LOG.isDebugEnabled())
    		LOG.debug("loadConstantParametres"," Entered: "+className+"["+methodName+"] *****");
    	AmbiguousServerP2PUpdateProcesslog.log(className,methodName," Entered: "+className+"["+methodName+"] *****");
    	_ambP2pConsParamMap = new HashMap<String, String>();
    	try
    	{
    		String ambFileCreationSleepTimeStr =Constants.getProperty("AMB_FILE_CREATE_SLEEP_TIME");
			if(BTSLUtil.isNullString(ambFileCreationSleepTimeStr))
			{
				LOG.error(methodName," Could not find AMB_FILE_CREATE_SLEEP_TIME Parameter in Constants file .");
				 	throw new BTSLBaseException(className,PretupsErrorCodesI.AMB_CONSTANT_ENTRY_MISSING);
			}
			_ambP2pConsParamMap.put("AMB_FILE_CREATE_SLEEP_TIME", ambFileCreationSleepTimeStr.trim());
			
			String ambBlankFileRequiredStr= Constants.getProperty("BLANK_AMB_FILE_REQUIRED");
			if(BTSLUtil.isNullString(ambBlankFileRequiredStr))
			{	LOG.error(methodName," Could not find BLANK_amb_FILE_REQUIRED parameter in Constants file .");
			 		throw new BTSLBaseException(className,PretupsErrorCodesI.AMB_CONSTANT_ENTRY_MISSING);
			}
			_ambP2pConsParamMap.put("BLANK_AMB_FILE_REQUIRED", ambBlankFileRequiredStr.trim());
			
			
			 String ambFileRecordSizeStr=Constants.getProperty("AMB_FILE_SIZE_RECORD");
			 if(BTSLUtil.isNullString(ambFileRecordSizeStr))
			 {
				 LOG.error(methodName," Could not find AMB_FILE_SIZE_RECORD parameter in Constants file .");
				 throw new BTSLBaseException(className,PretupsErrorCodesI.AMB_CONSTANT_ENTRY_MISSING);
			 }
			 _ambP2pConsParamMap.put("AMB_FILE_SIZE_RECORD", ambFileRecordSizeStr.trim());
			 
			 
			 String ambRecordSizeStr=Constants.getProperty("AMB_RECORD_SIZE");
			 if(BTSLUtil.isNullString(ambRecordSizeStr))
			 {
				 LOG.error(methodName," Could not find AMB_RECORD_SIZE parameter in Constants file .");
					throw new BTSLBaseException(className,PretupsErrorCodesI.AMB_CONSTANT_ENTRY_MISSING);
			 }
			 _ambP2pConsParamMap.put("AMB_RECORD_SIZE", ambRecordSizeStr.trim());
			 
			 String ambFileSizeStr=Constants.getProperty("AMB_FILE_SIZE");
			 if(BTSLUtil.isNullString(ambFileSizeStr))
			 {
				 LOG.error(methodName," Could not find AMB_FILE_SIZE parameter in Constants file .");
				  throw new BTSLBaseException(className,PretupsErrorCodesI.AMB_CONSTANT_ENTRY_MISSING);
			 }
			 _ambP2pConsParamMap.put("AMB_FILE_SIZE", ambFileSizeStr.trim());
			 
			 String ambFileNameStr=Constants.getProperty("AMB_P2P_FILE_NAME");
			 if(BTSLUtil.isNullString(ambFileNameStr))
			 {
				 LOG.error(methodName," Could not find AMB_P2P_SERVER_FILE_NAME parameter in Constants file .");
					  throw new BTSLBaseException(className,PretupsErrorCodesI.AMB_CONSTANT_ENTRY_MISSING); 
			 }
			 _ambP2pConsParamMap.put("AMB_P2P_FILE_NAME", ambFileNameStr.trim());
			 
			 String ambFilePathStr=Constants.getProperty("AMB_FILE_PATH");
			 if(BTSLUtil.isNullString(ambFilePathStr))
			 {
				 LOG.error(methodName," Could not find AMB_FILE_PATH parameter in Constants file .");
					  throw new BTSLBaseException(className,PretupsErrorCodesI.AMB_CONSTANT_ENTRY_MISSING);
			 }
			 _ambP2pConsParamMap.put("AMB_FILE_PATH", ambFilePathStr.trim());
			 
			 String ambTransactionStatusStr=Constants.getProperty("AMB_STATUS_FETCH");
			 if(BTSLUtil.isNullString(ambTransactionStatusStr))
			 {
				 LOG.error(methodName," Could not find AMB_STATUS_FETCH parameter in Constants file .");
					  throw new BTSLBaseException(className,PretupsErrorCodesI.AMB_CONSTANT_ENTRY_MISSING);
			 }
			 ambTransactionStatusStr = ambTransactionStatusStr.replace(",", "','");
			 _ambP2pConsParamMap.put("AMB_STATUS_FETCH", ambTransactionStatusStr.trim());
			 
			 String ambFileExtnStr=Constants.getProperty("AMB_FILE_EXTN");
			 if(BTSLUtil.isNullString(ambFileExtnStr))
			 {
				 LOG.error(methodName," Could not find AMB_FILE_EXTN parameter in Constants file .");
					  throw new BTSLBaseException(className,PretupsErrorCodesI.AMB_CONSTANT_ENTRY_MISSING);
			 }
			 _ambP2pConsParamMap.put("AMB_FILE_EXTN", ambFileExtnStr.trim());
						 
			 String ambHeaderParameterStr=Constants.getProperty("AMB_HEADER_PARAMETER");
			 if(BTSLUtil.isNullString(ambHeaderParameterStr))
			 {
				 LOG.error(methodName," Could not find AMB_HEADER_PARAMETER parameter in Constants file .");
					  throw new BTSLBaseException(className,PretupsErrorCodesI.AMB_CONSTANT_ENTRY_MISSING);
			 }
			 _ambP2pConsParamMap.put("AMB_HEADER_PARAMETER", ambHeaderParameterStr.trim());
			 
			 String ambHeaderDispayFlagStr=Constants.getProperty("AMB_HEADER_DISPLAY");
			 if(BTSLUtil.isNullString(ambHeaderDispayFlagStr))
			 {
				 LOG.error(methodName," Could not find AMB_HEADER_DISPLAY parameter in Constants file .");
					  throw new BTSLBaseException(className,PretupsErrorCodesI.AMB_CONSTANT_ENTRY_MISSING);	
			 }
			 _ambP2pConsParamMap.put("AMB_HEADER_DISPLAY", ambHeaderDispayFlagStr.trim());
	
			 String ambFtpSrcDir=Constants.getProperty("FTP_AMB_SRC_DIR_P2P");
	    	 if(BTSLUtil.isNullString(ambFtpSrcDir))
	    	 {
	    		 LOG.error(className,"FTP_AMB_SRC_DIR_P2P is not defined in Constant. props");
	            throw new BTSLBaseException(className,PretupsErrorCodesI.AMB_CONSTANT_ENTRY_MISSING);
	    	 }
	    	 _ambP2pConsParamMap.put("FTP_AMB_SRC_DIR_P2P", ambFtpSrcDir.trim());
	    	
	    	String ambFtpDestDir=Constants.getProperty("FTP_AMB_DEST_DIR_P2P");
	    	if(BTSLUtil.isNullString(ambFtpDestDir))
	    	{
	    		LOG.error(className,"FTP_AMB_DEST_DIR_P2P is not defined in Constant. props");
	            throw new BTSLBaseException(className,PretupsErrorCodesI.AMB_CONSTANT_ENTRY_MISSING);
	    	}
	    	_ambP2pConsParamMap.put("FTP_AMB_DEST_DIR_P2P", ambFtpDestDir.trim());
	    	
	    	
			String ambFtpTimeOutStr = Constants.getProperty("FTP_AMB_TIME_OUT");
			if (InterfaceUtil.isNullString(ambFtpTimeOutStr))
			{
				LOG.error(className,"FTP_amb_TIME_OUT is not defined in Constant. props ");
				throw new BTSLBaseException(PretupsErrorCodesI.AMB_CONSTANT_ENTRY_MISSING);
			}
			_ambP2pConsParamMap.put("FTP_AMB_TIME_OUT", ambFtpTimeOutStr.trim());
				
			
			String ambFtpFilePermission = Constants.getProperty("FTP_AMB_FILE_PERMISSION");
			if (InterfaceUtil.isNullString(ambFtpFilePermission) || ambFtpFilePermission.length() !=3)
			{
				LOG.info(className,"FTP_amb_FILE_PERMISSION is not defined in Constant. props ");
				ambFtpFilePermission="555";
			}
			_ambP2pConsParamMap.put("FTP_AMB_FILE_PERMISSION", ambFtpFilePermission.trim());
				
			String ambFileFinalPathStr = Constants.getProperty("AMB_FILE_FINAL_PATH_P2P");
			if (InterfaceUtil.isNullString(ambFileFinalPathStr))
			{
				LOG.info(className,"AMB_FILE_FINAL_PATH_P2P is not defined in Constant. props ");
				throw new BTSLBaseException(PretupsErrorCodesI.AMB_CONSTANT_ENTRY_MISSING);
			}
			_ambP2pConsParamMap.put("AMB_FILE_FINAL_PATH_P2P", ambFileFinalPathStr.trim());
			
			

			
			String hostName = Constants.getProperty("FTP_AMB_SERVER_IP");
			if (BTSLUtil.isNullString(hostName))
			{
				LOG.error(className,"FTP_AMB_SERVER_IP is not defined in Constant. props ");
				throw new BTSLBaseException(PretupsErrorCodesI.AMB_CONSTANT_ENTRY_MISSING);
			}
			_ambP2pConsParamMap.put("FTP_AMB_SERVER_IP", hostName.trim());
			
			
			String encryAllow = Constants.getProperty("FTP_AMB_FILE_ENCRY_ALLOW");
			if (BTSLUtil.isNullString(encryAllow))
			{
				LOG.error(className,"FTP_AMB_FILE_ENCRY_ALLOW is not defined in Constant. props ");
				throw new BTSLBaseException(PretupsErrorCodesI.AMB_CONSTANT_ENTRY_MISSING);
			}
			_ambP2pConsParamMap.put("FTP_AMB_FILE_ENCRY_ALLOW", encryAllow.trim());
			
			String userName = Constants.getProperty("FTP_AMB_USER_NAME");
			if (BTSLUtil.isNullString(userName))
			{
				LOG.error(className,"FTP_AMB_USER_NAME is not defined in Constant. props ");
				throw new BTSLBaseException(PretupsErrorCodesI.AMB_CONSTANT_ENTRY_MISSING);
			}

			//_ambP2pConsParamMap.put("FTP_AMB_USER_NAME", userName.trim());
			
			 String password = Constants.getProperty("FTP_AMB_PASSWD");
			if (BTSLUtil.isNullString(password))
			{
				LOG.error(className,"FTP_AMB_PASSWD is not defined in Constant. props ");
				throw new BTSLBaseException(PretupsErrorCodesI.AMB_CONSTANT_ENTRY_MISSING);
			}
			//_ambP2pConsParamMap.put("FTP_AMB_PASSWD", password.trim());
			if(encryAllow.equalsIgnoreCase("Y")){
				userName=BTSLUtil.decryptText(userName.trim());
				password=BTSLUtil.decryptText(password.trim());
			}
			_ambP2pConsParamMap.put("FTP_AMB_USER_NAME", userName.trim());
			_ambP2pConsParamMap.put("FTP_AMB_PASSWD", password.trim());
			String destinationDir = Constants.getProperty("FTP_AMB_DEST_DIR_P2P");
			if (BTSLUtil.isNullString(destinationDir))
			{
				LOG.info(className,"FTP_AMB_DEST_DIR_P2P is not defined in Constant. props ");
				throw new BTSLBaseException(PretupsErrorCodesI.AMB_CONSTANT_ENTRY_MISSING);
			}
			_ambP2pConsParamMap.put("FTP_AMB_DEST_DIR_P2P", destinationDir.trim());
			
			 String hostPort = Constants.getProperty("FTP_AMB_PORT");
			if (BTSLUtil.isNullString(hostPort))
			{
				LOG.info(className,"FTP_AMB_PORT is not defined in Constant. props ");
				throw new BTSLBaseException(PretupsErrorCodesI.AMB_CONSTANT_ENTRY_MISSING);
			}
			_ambP2pConsParamMap.put("FTP_AMB_PORT", hostPort.trim());
    	}
    	catch(Exception e)
		{
    		LOG.error(methodName, "Exception : " + e.getMessage());
			e.printStackTrace();
			BTSLMessages btslMessage=new BTSLMessages(PretupsErrorCodesI.AMB_CONSTANT_PARAMETER_NOT_FOUND);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+"["+methodName+"]","","","","Message:"+btslMessage);
		    throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_CONSTANT_PARAMETER_NOT_FOUND);
		}
    }
    
    /**
     * moveFilesToFinal() moves the files from 
     * create folder to final folder 
     * @param p_sourceFileLocation
     * @param p_finalFileLocation
     * @param p_fileName
     */
    private void moveFilesToFinal(String p_sourceFileLocation, String p_finalFileLocation ,String p_fileName)
    {
    	final String methodName="moveFilesToFinal";
    	boolean isFileRenamed=false;
    	if(LOG.isDebugEnabled()) LOG.debug(methodName,"Entered p_sourceFileLocation="+p_sourceFileLocation+"p_finalFileLocation="+p_finalFileLocation+"p_fileName="+p_fileName);
    	AmbiguousServerP2PUpdateProcesslog.log(className,methodName,"Entered p_sourceFileLocation="+p_sourceFileLocation+"p_finalFileLocation="+p_finalFileLocation+"p_fileName="+p_fileName);
        try
        {        	         
            File finalFileDir= new File(p_finalFileLocation);
            if(!finalFileDir.exists())
            {
                if(finalFileDir.mkdirs())
                {
                	LOG.info(methodName,"Location p_finalFileLocation:"+p_finalFileLocation+" does not exist. So creating it and is created successfully");
                	isFileRenamed=new File(p_sourceFileLocation+"/"+p_fileName).renameTo(new File(p_finalFileLocation+"/"+p_fileName));
                	if(isFileRenamed)
                	{
                		LOG.info(methodName,p_finalFileLocation + "does not exist so created " +p_finalFileLocation+" directory and then failed txn POST2PRE File "+ p_fileName +" is successfully stored at location :"+p_finalFileLocation);
                	}
                	else
                	{
                		LOG.info(methodName,"Location p_finalFileLocation:"+p_finalFileLocation + "does not exist so created " +p_finalFileLocation+" directory and then failed txn POST2PRE File "+ p_fileName +" could not be stored at location :"+p_finalFileLocation);
                	}                    
                }
                else
                {
                	LOG.info(methodName,"Location p_failedFileLocation:"+p_finalFileLocation+" does not exist and creation also failed ");
                }
            }
            else
            {
            	isFileRenamed=new File(p_sourceFileLocation+"/"+p_fileName).renameTo(new File(p_finalFileLocation+"/"+p_fileName));
            	if(isFileRenamed)
            	{
            		LOG.info(methodName,"successful txn POST2PRE File "+ p_fileName +" is successfully stored at location :"+p_finalFileLocation);
            	}
            	else
            	{
            		LOG.info(methodName,"failed txn POST2PRE File "+ p_fileName +" is could not be stored at location :"+p_finalFileLocation);
            	}                
            }
        }
        catch(Exception ex)
        {
        	LOG.error(methodName,"Error occured during  moving file "+ p_fileName +" to location "+p_finalFileLocation+" got Exception ex:"+ex.getMessage());
           EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,className+"["+methodName+"]","","","","In case  moving file "+ p_fileName +" to location "+p_finalFileLocation+" got Exception ex:"+ex.getMessage());            
        }
        finally
        {
        	if(LOG.isDebugEnabled()) LOG.debug(methodName,"Exited");
        	AmbiguousServerP2PUpdateProcesslog.log(className,methodName,"Exited");
        }
    }
    
    private  static void  freeStaticMemory()
    {
    	     p_ftpRequired = null;
    	     _ambP2pConsParamMap=null;
    }
    /**
     * Method initiate()
     * This method is used to upload file at server end 
     * @param  path String
     * @author rajvi.desai
     */  
    public static void initiate(String path)
    {
    	final String methodName="initiate";
    	    File file = new File(path);
	        FileInputStream fin=null;
	       
	        try {
	        	AmbiguousServerP2PUpdateProcesslog.log(className,methodName,"Entered to upload file to server");
	            fin = new FileInputStream(file);       
	            
	            SFTPUtils sftpUtils = new SFTPUtils();
	            sftpUtils.setHostName(_ambP2pConsParamMap.get("FTP_AMB_SERVER_IP"));
	            sftpUtils.setHostPort(_ambP2pConsParamMap.get("FTP_AMB_PORT"));
	            sftpUtils.setUserName(_ambP2pConsParamMap.get("FTP_AMB_USER_NAME"));
	            sftpUtils.setPassWord(_ambP2pConsParamMap.get("FTP_AMB_PASSWD"));
	            sftpUtils.setDestinationDir(_ambP2pConsParamMap.get("FTP_AMB_DEST_DIR_P2P"));
	            sftpUtils.setFilePermission(_ambP2pConsParamMap.get("FTP_AMB_FILE_PERMISSION"));
	            
	            String result = "";
	            result = sftpUtils.uploadFileToFTP1(file.getName(), fin, true);
	            LOG.info(methodName,result);
	        	} 
	        catch (FileNotFoundException e) {
	        	LOG.error(methodName,"File not found"+e);
	        	}
	        catch (Exception e) {
	        	LOG.error(methodName,"Error when  read file"+e);
	        	}
			 try {
				if (fin != null)
					fin.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			finally
			{
				AmbiguousServerP2PUpdateProcesslog.log(className,methodName,"Exited");
			}

    }
}
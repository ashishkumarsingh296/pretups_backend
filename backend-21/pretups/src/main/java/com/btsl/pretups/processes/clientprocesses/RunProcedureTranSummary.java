package com.btsl.pretups.processes.clientprocesses;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.QueryConstants;
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

/**
	* This Class is cretaed to run the Process for Network Summary report
	
*/

public class RunProcedureTranSummary {
	private static Log LOG = LogFactory.getLog(RunProcedureTranSummary.class.getName());
	private static ProcessStatusVO _processStatusVO1;
	
	private static ProcessBL _processBL=null;

	/**
     * to ensure no class instantiation 
     */
    private RunProcedureTranSummary(){
    	
    }
/**
	* This main is cretaed to run the Process idependentaly
	
*/
	
public static void main(String[] arg)
{
	final String METHOD_NAME = "main";
	try
	{
    	//ID DWH003 third argument is to generate DWH even there is Ambiguous or Under Process Txn.
        if(arg.length<2)
        {
          System.out.println("Usage : RunProcedureTranSummary [Constants file] [LogConfig file]");
          return;
        }
		File constantsFile = new File(arg[0]);
		if(!constantsFile.exists())
		{
			System.out.println("RunProcedureTranSummary"+" Constants File Not Found .............");
			return;
		}
		File logconfigFile = new File(arg[1]);
		if(!logconfigFile.exists())
		{
			System.out.println("RunProcedureTranSummary"+" Logconfig File Not Found .............");
			return;
		}
		if (LOG.isDebugEnabled())
			LOG.debug("constantsFile::",constantsFile+" logconfigFile::",logconfigFile);
		
		ConfigServlet.loadProcessCache(constantsFile.toString(),logconfigFile.toString());
	}//end of try
	catch(Exception e)
	{
		LOG.error(METHOD_NAME, "Exception : " + e);
        LOG.errorTrace(METHOD_NAME, e);
		if (LOG.isDebugEnabled())
			LOG.debug(METHOD_NAME," Error in Loading Files ...........................: "+e.getMessage());
		LOG.error(METHOD_NAME, "BTSLBaseException : " + e.getMessage());
		ConfigServlet.destroyProcessCache();
		return;
	}// end of catch
	try
	{
		
		process();
	}
	catch(BTSLBaseException be)
	{
		LOG.error(METHOD_NAME, "Exception:e=" + be.getMessage());
		LOG.errorTrace(METHOD_NAME, be);
		
	}
	catch(Exception e)
	{
		if (LOG.isDebugEnabled())
			LOG.debug(METHOD_NAME," Error in Loading Files ...........................: "+e.getMessage());
		return;
	}// end of catch
	finally
	{
		if(LOG.isDebugEnabled())
			LOG.debug(METHOD_NAME, "Exiting..... ");
		ConfigServlet.destroyProcessCache();
	}
}
/**
	* This method is cretaed to  Process the actual Functionality
	
*/

private static void process() throws BTSLBaseException
{
	final String methodName = "process";
	if (LOG.isDebugEnabled())
		LOG.debug(methodName," Inside Process:: ");
	
	String RunProcedureTranSummary=null;
	Connection con= null;
	Date processedUptoRunProcedureTranSummary=null;
    Date fromDate=null;
    Date toDate=null;
    Date currentDate=new Date();
    CallableStatement cstmt = null;
    ProcessStatusDAO processStatusDAO=null;
    int maxDoneDateUpdateCount=0;
    boolean statusOk=false;
    Date dateCount=null;
    String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
    try
	{
    LOG.debug(methodName,"Memory at startup: Total:"+Runtime.getRuntime().totalMemory()/1049576+" Free:"+Runtime.getRuntime().freeMemory()/1049576);
	currentDate=BTSLUtil.getSQLDateFromUtilDate(currentDate);
	con = OracleUtil.getSingleConnection();
	if(con==null)
	{
		if (LOG.isDebugEnabled())
			LOG.debug(methodName," DATABASE Connection is NULL ");
		EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"RunProcedureTranSummary[process]","","","","DATABASE Connection is NULL");
		return;
	}
	
	RunProcedureTranSummary=ProcessI.RUN_PROCEDURE_TRNSUM;
	
	//method call to check status of the process
	    _processBL=new ProcessBL();
	     processStatusDAO=new ProcessStatusDAO();

	    _processStatusVO1=_processBL.checkProcessUnderProcess(con,RunProcedureTranSummary);
	   
	  //method call to find maximum date till which process has been executed
	    
	    statusOk=_processStatusVO1.isStatusOkBool();
	    processedUptoRunProcedureTranSummary=_processStatusVO1.getExecutedUpto();
	    if (LOG.isDebugEnabled())
			LOG.debug("RunProcedureTranSummary -- processedUptoRunProcedureTranSummary ::",processedUptoRunProcedureTranSummary);
	    if(statusOk)
	    {
	    	con.commit();
	    if (processedUptoRunProcedureTranSummary!=null)
   	    {
	    	
      	    //ID DWH002 to check whether process has been executed till current date or not
  	        if (processedUptoRunProcedureTranSummary.compareTo(currentDate)==0)
		        throw new BTSLBaseException("RunProcedureTranSummary",methodName,PretupsErrorCodesI.MONTHLYUSRTXNSUMRY_PROCESS_ALREADY_EXECUTED_TILL_TODAY);
  	        
  	        //adding 1 in processed up to date as we have to start from the next day till which process has been executed
  	        	processedUptoRunProcedureTranSummary=BTSLUtil.addDaysInUtilDate(processedUptoRunProcedureTranSummary,1);
  	        	LOG.debug(" RunProcedureTranSummary::",processedUptoRunProcedureTranSummary);
  	        	for(dateCount=processedUptoRunProcedureTranSummary;dateCount.before(BTSLUtil.addDaysInUtilDate(currentDate,-1));dateCount=BTSLUtil.addDaysInUtilDate(dateCount,1))
  	        	{
  	        		
  	        		fromDate=BTSLUtil.getSQLDateFromUtilDate(dateCount);
  	        		toDate=BTSLUtil.getSQLDateFromUtilDate(processedUptoRunProcedureTranSummary);
   	        		if(PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype")))
   	        		{
   	        			cstmt = con.prepareCall("{call "+ Constants.getProperty("currentschema")+".DUMP_TRANS_SUMMARY(?)}");
   	        		}
   	        		else if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
   	        			cstmt = con.prepareCall("{ call dump_trans_summary(?)}");
   	        		}
   	        		else
   	        		{
   	        			cstmt = con.prepareCall("{call DUMP_TRANS_SUMMARY(?)}");
   	        		}
   	        	 if (LOG.isDebugEnabled())
   	        	 {
   	        		 		LOG.debug("Procedure Param 1  ::",fromDate);
   	        				        		 
   	        	 }
   	        	cstmt.setDate(1,BTSLUtil.getSQLDateFromUtilDate(fromDate));
   	        	
   	        	if (LOG.isDebugEnabled()) 
   	        		LOG.debug(methodName,"Before Exceuting Procedure");
   	        	
   	        	if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
   	        		cstmt.executeQuery();
   	        	}
   	        	else{
   	        	cstmt.executeUpdate();
   	        	}
   	        	if (LOG.isDebugEnabled())
   	        		LOG.debug(methodName,"After Exceuting Procedure");
   	        		//method call to update maximum date till which process has been executed
   	        		_processStatusVO1.setExecutedUpto(dateCount);
   	        		_processStatusVO1.setExecutedOn(currentDate);
   	        		maxDoneDateUpdateCount=processStatusDAO.updateProcessDetail(con,_processStatusVO1);
   	        		//if the process is successful, transaction is commit, else roll back
   	        		if(maxDoneDateUpdateCount>0)
   	        		{
   	        			con.commit();
   	        		}
   	        		else
   	        		{
   	        			con.rollback();
   	        			throw new BTSLBaseException("RunProcedureTranSummary",methodName,PretupsErrorCodesI.MONTHLYUSRTXNSUMRY_COULD_NOT_UPDATE_MAX_DONE_DATE);
   	        		}
   	        		Thread.sleep(500);
   	        		EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"RunProcedureTranSummary[process]","","",""," RunProcedureTranSummary process has been executed successfully.");
   	        }//end for
   	    }// end of if 
	    else
	        throw new BTSLBaseException("RunProcedureTranSummary",methodName,PretupsErrorCodesI.MONTHLYUSRTXNSUMRY_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
   	    }// end of if processedUptoRunProcedureTranSummary in not null
    else
    {
    	 // in order not to update the status of table for this processID
        con.rollback();
    }
 }
 catch(Exception e)
    {
	 LOG.error(methodName, "Exception : " + e);
	 EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"RunProcedureTranSummary[process]","","",""," RunProcedureTranSummary process could not be executed successfully.");
	 throw new BTSLBaseException("RunProcedureTranSummary",methodName,PretupsErrorCodesI.MONTHLYUSRTXNSUMRY_ERROR_EXCEPTION);
    }
 finally
 {
	//if the status was marked as under process by this method call, only then it is marked as complete on termination
	    if (statusOk)
	    {
	        try
	        {
		        if (markProcessStatusAsComplete(con,RunProcedureTranSummary)==1)
		        	try{
		        		con.commit();
		        	} 
		        catch(Exception e){
		        	LOG.error(methodName,e.getMessage());
		        	LOG.errorTrace(methodName, e);
		        	}
		        else
		        	try{
		        		con.rollback();} 
		        catch(Exception e){
		        	LOG.error(methodName,e.getMessage());
		        	LOG.errorTrace(methodName, e);
		        	}
		        ConfigServlet.destroyProcessCache();
	        }
	        catch(Exception e)
	        {
	      
	        	LOG.error(methodName, "SQLException : " + e.getMessage());
	        	LOG.errorTrace(methodName, e);
	        }
	        try
	        {
	            if(con!=null)
				con.close();
	        }
	        catch(Exception ex)
	        {
	        	LOG.error(methodName, "SQLException : " + ex.getMessage());
	        	LOG.errorTrace(methodName, ex);
	        	
				if(LOG.isDebugEnabled())
					LOG.debug(methodName, "Exception closing connection ");
				LOG.error(methodName, "Exception : " + ex.getMessage());
	        }
	    }

 	try {
 		if (cstmt != null){
 			cstmt.close();}} 
 	catch (Exception e) 
 	{
 		LOG.error(methodName,e.getMessage());}
	    LOG.debug(methodName,"Memory at end: Total:"+Runtime.getRuntime().totalMemory()/1049576+" Free:"+Runtime.getRuntime().freeMemory()/1049576);
		if(LOG.isDebugEnabled())
			LOG.debug(methodName, "Exiting..... ");
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
		final String methodName = "markProcessStatusAsComplete";
		if (LOG.isDebugEnabled())
			LOG.debug(methodName," Entered:  p_processId:"+p_processId);
		int updateCount=0;
		Date currentDate=new Date();
		ProcessStatusDAO processStatusDAO=new ProcessStatusDAO();
		_processStatusVO1.setProcessID(p_processId);
		_processStatusVO1.setProcessStatus(ProcessI.STATUS_COMPLETE);
		_processStatusVO1.setStartDate(currentDate);
		try
		{
		    updateCount =  processStatusDAO.updateProcessDetail(p_con,_processStatusVO1);
		}
	    catch(Exception e)
	    {
			LOG.error(methodName, "SQLException : " + e.getMessage());
        	LOG.errorTrace(methodName, e);
	        EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"RunProcedureTranSummaryTranSummary[markProcessStatusAsComplete]","","","","Exception:"+e.getMessage());
	        throw new BTSLBaseException(methodName,"markProcessStatusAsComplete", "MONTHLYUSRTXNSUMRY_ERROR_EXCEPTION");
	    }
	    finally
	    {
	        if (LOG.isDebugEnabled())
	            LOG.debug(methodName, "Exiting: updateCount=" + updateCount);
	    } // end of finally
	    return updateCount;
	
	}
		
	private static long getDiffOfDateInMinute(Date p_currentDate, Date p_startDate)
	{
		final String methodName="getDiffOfDateInMinute";
		if(LOG.isDebugEnabled())
			LOG.debug(methodName, "Entered p_currentDate="+p_currentDate+" p_startDate: "+p_startDate);
		long diff=0;
	    try
	    {
	        //Getting the difference between current date and start date of process in Minutes.
			diff=((p_currentDate.getTime()-p_startDate.getTime())/(1000*60));
	    }
	    catch(Exception e)
	    {
	    	LOG.error(methodName, "Exception : " + e.getMessage());
        	LOG.errorTrace(methodName, e);
	       
		}//end of catch-Exception
		if(LOG.isDebugEnabled())
			LOG.debug(methodName, "Exiting diff="+diff);
		return diff;
	}//end of getDiffOfDateInMinute
	
	
}



package com.btsl.pretups.processes.clientprocesses;

import java.io.File;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.EMailSender;
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
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.processes.clientprocesses.businesslogic.ReverseHirerachyCommisionDAO;
import com.btsl.pretups.processes.clientprocesses.businesslogic.ReverseHirerachyCommisionVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.internal.OracleTypes;
import oracle.sql.ARRAY;

/**
 *
 *-------------------------------------------------------------------------------------------------
 * Author                               Date            History
 *-------------------------------------------------------------------------------------------------
 * Vishal                               2               Initial creation
 *-------------------------------------------------------------------------------------------------
 */

public class ReverseHirerachyCommision {

	private ReverseHirerachyCommision()
	{}

	
	
	private static Log logger = LogFactory.getLog(ReverseHirerachyCommision.class.getName());


	private static String fileName;
	private static String failFileName;

	public static void main(String[] args) {
		String methodName="ReverseHirerachyCommision[main()]";
		Connection con=null;

		try
		{
			if(args.length!=2)
			{
				if(logger.isDebugEnabled())
					logger.debug(methodName, "Usage : ReverseHirerachyCommision [Constants file] [LogConfig file]");
				return;
			}

			File constantsFile = new File(args[0]);
			if(!constantsFile.exists() )
            {
            	if(logger.isDebugEnabled())
            		logger.debug(methodName, " ReverseHierarchy "+" Constants File Not Found .............");
                logger.error(methodName, "Constants file not found on location: "+constantsFile.toString() );
                        return;
            }
			
			File logconfigFile = new File(args[1]);
			if(!logconfigFile.exists())
			{
				if(logger.isDebugEnabled())
            		logger.debug(methodName, " ReverseHierarchy "+" Logconfig File Not Found .............");
                logger.error(methodName, "Logconfig File not found on location: "+constantsFile.toString() );
                        return;
			}
			ConfigServlet.loadProcessCache(constantsFile.toString(),logconfigFile.toString());
		}
		catch(Exception e)
		{
			logger.error(methodName, "Exception thrown in ReverseHirerachyCommision: Not able to load files"+e);
			ConfigServlet.destroyProcessCache();
			return;
		}

		try{
			//Make Connection
			con = OracleUtil.getSingleConnection();
			if(con==null)
			{
				if (logger.isDebugEnabled()){
					logger.debug(methodName,"Not able to get Connection for ReverseHirerachyCommision: ");
				}
				throw new SQLException();
			}
			process(con);

		}catch(Exception e){
			if (logger.isDebugEnabled()) {
				logger.debug("ReverseHirerachyCommision[main]","Exception thrown in ReverseHirerachyCommision: Not able to load files"+e);
			}
			ConfigServlet.destroyProcessCache();
		}finally{
			if (logger.isDebugEnabled()) {
				logger.debug("ReverseHirerachyCommision[main]","Exiting" );
			}
			try{
				if(con!=null){
					con.close();
				}
			}
			catch(Exception e){
				logger.errorTrace("main", e);
			}

			ConfigServlet.destroyProcessCache();
		}
	}

	private static void process(Connection con)
	{
		String  methodName="process";
		if (logger.isDebugEnabled()){
			logger.debug(methodName," Entered:" );
		}
		CallableStatement cstmt = null;
		Date currentDate=new Date();
		Date processedUpto=null;
		String processId=null;
		boolean statusOk=false;
		int beforeInterval=0;
		Date dateCount=null;
		int maxDoneDateUpdateCount=0;
		ProcessStatusDAO processStatusDAO=null;
		String revhirerComProcName=null;
		ProcessBL processBL=null;
		ProcessStatusVO processStatusVO = null;
		String message="";
		StringBuilder sb=new StringBuilder();
		try
		{

			processId=ProcessI.RHCOM;
			//method call to check status of the process
			processBL=new ProcessBL();
			processStatusVO=processBL.checkProcessUnderProcess(con,processId);
			statusOk=processStatusVO.isStatusOkBool();
			beforeInterval=BTSLUtil.parseLongToInt( processStatusVO.getBeforeInterval()/(60*24) );
			logger.debug(methodName,"Memory at startup: Total:"+Runtime.getRuntime().totalMemory()/1049576+" Free:"+Runtime.getRuntime().freeMemory()/1049576);
			currentDate=BTSLUtil.getSQLDateFromUtilDate(currentDate);
			if (statusOk)
			{
				con.commit();
				//method call to find maximum date till which process has been executed
				processedUpto=processStatusVO.getExecutedUpto();
				if (processedUpto!=null)
				{
					if (processedUpto.compareTo(currentDate)==0)
						throw new BTSLBaseException("ReverseHirerachyCommision",methodName,PretupsErrorCodesI.REVERSE_COMMISION_EXECUTED_UPTO_DATE_NOT_FOUND);

					processedUpto=BTSLUtil.addDaysInUtilDate(processedUpto,1);
					//loop to be started for each date
					//the loop starts from the date till which process has been executed and executes one day before current date
					for(dateCount=BTSLUtil.getSQLDateFromUtilDate(processedUpto);dateCount.before(BTSLUtil.addDaysInUtilDate(currentDate,-beforeInterval));dateCount=BTSLUtil.addDaysInUtilDate(dateCount,1))
					{
						revhirerComProcName = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DUALWALLET_C2CAUTO_CAL));

						if(!(BTSLUtil.isNullString(revhirerComProcName)||revhirerComProcName==""))
						{

							if(PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype")))
							{
								sb.append("{call ").append(Constants.getProperty("currentschema")).append(".").append(revhirerComProcName).append("(?,?,?,?)}");
								cstmt = con.prepareCall(sb.toString());
							}
							else
							{
								sb.append("{call ").append(revhirerComProcName).append("(?,?,?,?)}");
								cstmt = con.prepareCall(sb.toString());
							}
						}
						else
						{

							if(PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype")))
							{

								cstmt = con.prepareCall("{call "+ Constants.getProperty("currentschema")+".DUALWALLET_C2CAUTO_CAL(?,?,?,?}");
							}
							else
							{

								cstmt = con.prepareCall("{call DUALWALLET_C2CAUTO_CAL(?,?,?,?)}");
							}
						}




						cstmt.registerOutParameter(2,Types.VARCHAR); //Message for log
						cstmt.registerOutParameter(3,Types.VARCHAR);//Message for error output
						cstmt.registerOutParameter(4,OracleTypes.ARRAY,Constants.getProperty("currentschema")+".T");
						cstmt.setDate(1,BTSLUtil.getSQLDateFromUtilDate(dateCount) );


						if (logger.isDebugEnabled()){
							logger.debug(methodName,"Before Exceuting Procedure");
						}
						cstmt.executeUpdate();
						if (logger.isDebugEnabled()){
							logger.debug(methodName,"After Exceuting Procedure");
						}
						if (logger.isDebugEnabled()) {
							logger.debug(methodName,"Parameters Returned : Status="+cstmt.getString(2));
						}

						if(!"SUCCESS".equals(cstmt.getString(2)))
						{
							throw new BTSLBaseException(methodName,methodName,"RHCOM Procedure Execution Fail");
						}
						if(!(BTSLUtil.isNullString(cstmt.getString(2)))){
							if (cstmt.getString(2).equalsIgnoreCase(PretupsI.SUCCESS)){
								if (logger.isDebugEnabled()) {
									logger.debug(methodName,"Parameters Returned : Status="+cstmt.getString(2));
								}
								ReverseHirerachyCommisionDAO reverseHirerachyCommisionDAO =new ReverseHirerachyCommisionDAO();
								List<ReverseHirerachyCommisionVO> data =reverseHirerachyCommisionDAO.fetchReverseHirerachyCommisionData(con, BTSLUtil.getSQLDateFromUtilDate(dateCount));
								String finalDirectoryPath =Constants.getProperty("UploadBatchO2CInitiationFilePath");
								String finalDirectoryErrPath =Constants.getProperty("DUAL_WALLET_FILE_ERR_PATH");
								if(data!=null && !data.isEmpty() )
									fileName=writeToFile(finalDirectoryPath,data,dateCount);

								ARRAY arr = ((OracleCallableStatement)cstmt).getARRAY(4);
								String[] recievedArray = (String[])(arr.getArray());
								if(recievedArray!=null && recievedArray.length >0 && recievedArray[0]!=null)
								{
									failFileName=writeToFileForFail(finalDirectoryErrPath,recievedArray,dateCount);
								}


								String pushEmail = Constants.getProperty("DUAL_WALLET_PUSH_EMAIL");
								if (logger.isDebugEnabled()) {
									logger.debug(methodName,"pushEmail="+pushEmail);
								}
								if(!(BTSLUtil.isNullString(pushEmail)||pushEmail=="")){
									String to=Constants.getProperty("DUAL_EMAIL_TO");
									String from=Constants.getProperty("DUAL_EMAIL_FROM");
									String subject=Constants.getProperty("DUAL_EMAIL_SUBJECT");
									String fileNameTobeDisplayed =Constants.getProperty("DUAL_EMAIL_FILENAME_DISPLAY");
									if (logger.isDebugEnabled()) {
										logger.debug(methodName,"pushEmail="+pushEmail);
									}
									boolean isAttachment=true;
									EMailSender.sendMail( to,from,null,null,subject, message,isAttachment,fileName,fileNameTobeDisplayed);
									EMailSender.sendMail( to,from,null,null,subject, message,isAttachment,failFileName,fileNameTobeDisplayed);

								}



								processStatusVO.setExecutedUpto(dateCount);
								processStatusVO.setExecutedOn(currentDate);
								processStatusDAO=new ProcessStatusDAO();
								maxDoneDateUpdateCount=processStatusDAO.updateProcessDetail(con,processStatusVO);

								if(maxDoneDateUpdateCount>0)
								{
									con.commit();
								}
								else
								{
									con.rollback();
									throw new BTSLBaseException("ReverseHirerachyCommision",methodName,PretupsErrorCodesI.REVERSE_COMMISION_COULD_NOT_UPDATE_MAX_DONE_DATE);
								}
							}
						}
					}
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"ReverseHirerachyCommision[process]","","",""," ReverseHirerachyCommision process has been executed successfully.");

				}
			}
			else
			{
				if (logger.isDebugEnabled()) {
					logger.debug(methodName," Date till which process has been executed is not found.");
				}
				return;
			}
		}

		catch(Exception e){
			logger.errorTrace(methodName, e);
			try
			{
				con.rollback();
			}
			catch(Exception sqlex)
			{
				logger.errorTrace(methodName, sqlex);
			}
			message=e.getMessage();
			//send the message as SMS
		}
		finally
		{
			try
			{
				if (statusOk)
				{
					if (markProcessStatusAsComplete(con,processId,processStatusVO)==1)
						try{
							con.commit();
						}
					catch(Exception e){
						logger.errorTrace(methodName, e);
					}
					else
						try{
							con.rollback();
						}
					catch(Exception e){
						logger.errorTrace(methodName, e);
					}
				}
				if(cstmt != null)
					cstmt.close();
			}
			catch(Exception e){
				logger.errorTrace(methodName, e);
			}
			try{
				Thread.sleep(5000);
			}
			catch(Exception e){
				logger.errorTrace(methodName, e);
			}
		}

	}




	private static int markProcessStatusAsComplete(Connection pCon,String pProcessId,ProcessStatusVO processStatusVO)
	{
		String  methodName="markProcessStatusAsComplete";
		if (logger.isDebugEnabled()) {
			logger.debug(methodName," Entered:  p_processId:"+pProcessId);
		}
		int updateCount=0;
		Date currentDate=new Date();
		ProcessStatusDAO processStatusDAO=new ProcessStatusDAO();
		processStatusVO.setProcessID(pProcessId);
		processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
		processStatusVO.setStartDate(currentDate);
		try
		{
			updateCount =processStatusDAO.updateProcessDetail(pCon,processStatusVO);
		}
		catch(Exception e){
			logger.errorTrace(methodName, e);
			if (logger.isDebugEnabled()) {
				logger.debug(methodName, "Exception= " + e.getMessage());
			}
		}
		finally
		{
			if (logger.isDebugEnabled()){
				logger.debug(methodName,"Exiting: updateCount=" + updateCount);
			}
		} // end of finally
		return updateCount;

	}

	public static String writeToFile(String finalDirectoryPath, List<ReverseHirerachyCommisionVO> hirrechalList,Date forDate){

		String  methodName="writeToFile";

		if (logger.isDebugEnabled())
			logger.debug(methodName,"Entered with FinalDirectoryPath ::"+finalDirectoryPath+"HirrechalList size"+hirrechalList.size()+"For Date "+forDate);

		String sucFileName = null;
		String message = null;
		String forDateLocal=null;

		try{
			forDateLocal=BTSLUtil.getDateTimeStringFromDate(forDate, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_DATE_FORMAT)));
		}
		catch(Exception e){
			logger.errorTrace(methodName, e);
			forDateLocal=forDate.toString();

		}

		sucFileName = finalDirectoryPath+"/"+Constants.getProperty("DUAL_WALLET_FILE_NAME")+forDateLocal+".txt";
		try(PrintWriter sucWriter = new PrintWriter(sucFileName, "UTF-8")){
			Iterator itr = hirrechalList.iterator();
			while (itr.hasNext()){
				ReverseHirerachyCommisionVO  reverseHirCom= (ReverseHirerachyCommisionVO) itr.next();
				message=reverseHirCom.getMsisdn()+","+BTSLUtil.parseDoubleToInt( reverseHirCom.getFocAmount() )+","+PretupsI.PAYMENT_INSTRUMENT_TYPE_CASH +","+ Constants.getProperty("DUAL_REMARKS")+" "+reverseHirCom.getThroughUser() ;
				sucWriter.println(message);

			}
			hirrechalList.clear();

		}catch(Exception e){
			logger.errorTrace(methodName, e);
		}
		return sucFileName;
	}


	public static String writeToFileForFail(String finalDirectoryPath, String[] arrayForFail,Date forDate){

		String  methodName="writeToFileForFail";

		if (logger.isDebugEnabled())
			logger.debug(methodName,"Entered with FinalDirectoryPath ::"+finalDirectoryPath +"For Date "+forDate);

		String sucFileName = null;
		String forDateLocal=null;

		try{
			forDateLocal=BTSLUtil.getDateTimeStringFromDate(forDate, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_DATE_FORMAT)));
		}
		catch(Exception e){
			logger.errorTrace(methodName, e);
			forDateLocal=forDate.toString();

		}

		sucFileName = finalDirectoryPath+"/"+Constants.getProperty("DUAL_WALLET_FILE_ERR_NAME")+forDateLocal+".txt";

		try(PrintWriter sucWriter = new PrintWriter(sucFileName, "UTF-8")){
			for(int i=0;i<arrayForFail.length;i++)
			{
				if(arrayForFail[i]!=null){
					sucWriter.println(arrayForFail[i]);
				}
				else
					break;
			}

			arrayForFail=null;

		}catch(Exception e){
			logger.errorTrace(methodName, e);
		}
		return sucFileName;
	}

}
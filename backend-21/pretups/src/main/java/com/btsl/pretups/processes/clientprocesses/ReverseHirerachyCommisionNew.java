package com.btsl.pretups.processes.clientprocesses;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.EMailSender;
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
 * Diwakar                              13/NOV/2018     Initial creation
 *-------------------------------------------------------------------------------------------------
 */

public class ReverseHirerachyCommisionNew {

        public static String message="";
        private static ProcessStatusVO processStatusVO;
        private static Log logger = LogFactory.getLog(ReverseHirerachyCommisionNew.class.getName());
        private static String fileName;
        private static String failFileName;

        public static void main(String[] args) {
            Connection con=null;
            try
            {
                if(args.length!=2)
                {
                        System.out.println("Usage : ReverseHirerachyCommisionNew [Constants file] [LogConfig file]");
                        return;
                }
                File constantsFile = new File(args[0]);
                if(!constantsFile.exists())
                {
                	System.out.println(" Constants file not found on provided location.");
                    return;
                }
                File logconfigFile = new File(args[1]);
                if(!logconfigFile.exists())
                {
                	System.out.println(" Logconfig file not found on provided location.");
                    return;
                }
                ConfigServlet.loadProcessCache(constantsFile.toString(),logconfigFile.toString());
            }
            catch(Exception e)
            {
                System.out.println("Exception thrown in ReverseHirerachyCommisionNew: Not able to load files"+e);
                ConfigServlet.destroyProcessCache();
                return;
            }

            try{
                //Make Connection
                con = OracleUtil.getSingleConnection();
                if(con==null)
                {
                    if (logger.isDebugEnabled()){
                            logger.debug("ReverseHirerachyCommisionNew[main]","Not able to get Connection for ReverseHirerachyCommisionNew: ");
                    }
                    throw new Exception();
                }
                process(con);

            }catch(Exception e){
                if (logger.isDebugEnabled()) {
                     logger.debug("ReverseHirerachyCommisionNew[main]","Exception thrown in ReverseHirerachyCommisionNew: Not able to load files"+e);
                }
                ConfigServlet.destroyProcessCache();
            }finally{
                if (logger.isDebugEnabled()) {
                        logger.debug("ReverseHirerachyCommisionNew[main]","Exiting" );
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

        /**
         * @descriptions To calculate the Hierarchy commission on each dual wallet transaction based on 
         * configuration of dual wallet user type preference 
         * @param con
         */
        private static void process(Connection con) {
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
            String hierarchyComProcName=null;
            ProcessBL processBL=null;
            ResultSet rs = null;
            try {

                processId=ProcessI.RHCOM;
                //method call to check status of the process
                processBL=new ProcessBL();
                processStatusVO=processBL.checkProcessUnderProcess(con,processId);
                statusOk=processStatusVO.isStatusOkBool();
                beforeInterval=BTSLUtil.parseLongToInt( processStatusVO.getBeforeInterval()/(60*24) );
                logger.debug(methodName,"Memory at startup: Total:"+Runtime.getRuntime().totalMemory()/1049576+" Free:"+Runtime.getRuntime().freeMemory()/1049576);
                currentDate=BTSLUtil.getSQLDateFromUtilDate(currentDate);
                if (statusOk) {
                    con.commit();
                    //method call to find maximum date till which process has been executed
                    processedUpto=processStatusVO.getExecutedUpto();
                    if (processedUpto!=null) {
                        if (processedUpto.compareTo(currentDate)==0)
                                throw new BTSLBaseException(methodName,methodName,PretupsErrorCodesI.REVERSE_COMMISION_EXECUTED_UPTO_DATE_NOT_FOUND);

                        processedUpto=BTSLUtil.addDaysInUtilDate(processedUpto,1);
                        //loop to be started for each date
                        //the loop starts from the date till which process has been executed and executes one day before current date
                        String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
                        for(dateCount=BTSLUtil.getSQLDateFromUtilDate(processedUpto);dateCount.before(BTSLUtil.addDaysInUtilDate(currentDate,-beforeInterval));dateCount=BTSLUtil.addDaysInUtilDate(dateCount,1)) {
                            hierarchyComProcName = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DUALWALLET_C2CAUTO_CAL));
                            String isSuccess = "";
                                
                            if (logger.isDebugEnabled()){
                                logger.debug(methodName,"hierarchyComProcName ="+hierarchyComProcName);
                            }

                            if(!(BTSLUtil.isNullString(hierarchyComProcName)||hierarchyComProcName=="")) {

                                if(PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype")))  {
                                        cstmt = con.prepareCall("{call "+ Constants.getProperty("currentschema")+"."+hierarchyComProcName+"(?,?,?,?)}");
                                } else if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                                	cstmt = con.prepareCall("{call "+ Constants.getProperty("currentschema")+"."+hierarchyComProcName+"(?)}");                                	
                                } 
                                else {
                                        cstmt = con.prepareCall("{call "+hierarchyComProcName+"(?,?,?,?)}");
                                }
                            }
                            else
                            {
                                if(PretupsI.DATABASE_TYPE_DB2.equals(Constants.getProperty("databasetype"))) {

                                        cstmt = con.prepareCall("{call "+ Constants.getProperty("currentschema")+".DUALWALLET_AUTOC2C_CAL(?,?,?,?}");
                                } else if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                                	cstmt = con.prepareCall("{call "+ Constants.getProperty("currentschema")+".DUALWALLET_AUTOC2C_CAL(?)}");
                                }
                                else {

                                        cstmt = con.prepareCall("{call DUALWALLET_AUTOC2C_CAL(?,?,?,?)}");
                                }
                            }
                            if (logger.isDebugEnabled()){
                                logger.debug(methodName,"Exceuting Procedure for date="+dateCount);
                            }
                            cstmt.setDate(1,BTSLUtil.getSQLDateFromUtilDate(dateCount) );
                            if(!QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
	                            cstmt.registerOutParameter(2,Types.VARCHAR); //Message for log
	                            cstmt.registerOutParameter(3,Types.VARCHAR);//Message for error output
	                            cstmt.registerOutParameter(4,OracleTypes.ARRAY,Constants.getProperty("currentschema").toUpperCase()+".T");
                            } 
                            if (logger.isDebugEnabled()){
                                    logger.debug(methodName,"Before Exceuting Procedure");
                            }
                            if(!QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                            	cstmt.executeUpdate();
                            	isSuccess = cstmt.getString(2);
                            	if (logger.isDebugEnabled()){
                                    logger.debug(methodName,"After Exceuting"+ dbConnected+" Procedure :: Parameters Returned : Status="+isSuccess);
	                            }	                            
                            	if(!PretupsI.SUCCESS.equals(isSuccess))
	                            {
	                            	cstmt.close();
	                            	throw new BTSLBaseException(methodName,methodName,"RHCOM Procedure Execution Fail");
	                            }	                           
                            
                            } else {
                            	 rs = cstmt.executeQuery();
                                 if(rs.next()){
                                 	isSuccess=rs.getString("p_message");
                                 }
                                 if (logger.isDebugEnabled()){
                                     logger.debug(methodName,"After Exceuting"+ dbConnected+" Procedure :: Parameters Returned : Status="+isSuccess);
 	                            }	                            
                             	if(!PretupsI.SUCCESS.equals(isSuccess))
 	                            {
                             		 if (QueryConstants.DB_POSTGRESQL.equals(dbConnected) && con!=null)
                             			 con.rollback();
 	                            	throw new BTSLBaseException(methodName,methodName,"RHCOM Procedure Execution Fail");
 	                            }
                            	
                            }
                            if(!(BTSLUtil.isNullString(isSuccess))) {
                            	if (PretupsI.SUCCESS.equalsIgnoreCase(isSuccess)) {
                            		if(!QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                            			if (logger.isDebugEnabled()) {
	                                        logger.debug(methodName,"Parameters Returned : Status="+cstmt.getString(2));
                            			}
                            		}
                                ReverseHirerachyCommisionDAO reverseHirerachyCommisionDAO =new ReverseHirerachyCommisionDAO();
                                ArrayList<ReverseHirerachyCommisionVO> data =reverseHirerachyCommisionDAO.fetchReverseHirerachyCommisionData(con, BTSLUtil.getSQLDateFromUtilDate(dateCount));
                                String finalDirectoryPath =Constants.getProperty("UploadBatchO2CInitiationFilePath");
                                String finalDirectoryErrPath =Constants.getProperty("DUAL_WALLET_FILE_ERR_PATH");
                                if(data!=null && !data.isEmpty() ) {
                                        fileName=writeToFile(finalDirectoryPath,data,dateCount);
                                }
                                
                                String[] recievedArray = null;
                                if(!QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
                                	ARRAY arr = ((OracleCallableStatement)cstmt).getARRAY(4);
                                	recievedArray = (String[])(arr.getArray());
                                } else {
                                	Array  arr = rs.getArray("ret_array");
                                	try {
										recievedArray = (String[]) arr.getArray();
									} catch (Exception e) {
										boolean exceptionOccured = true;
										//logger.errorTrace(methodName,e);
									}
                                }
                               if(recievedArray!=null && recievedArray.length >0 && recievedArray[0]!=null)
                                {
                                	failFileName=writeToFileForFail(finalDirectoryErrPath,recievedArray,dateCount);
                                }
                                

                                String pushEmail = Constants.getProperty("DUAL_WALLET_PUSH_EMAIL");
                                if (logger.isDebugEnabled()) {
                                        logger.debug(methodName,"pushEmail="+pushEmail);
                                }
                                if(!(BTSLUtil.isNullString(pushEmail)||pushEmail=="") && PretupsI.YES.equalsIgnoreCase(pushEmail)) {
                                    String to=Constants.getProperty("DUAL_EMAIL_TO");
                                    String from=Constants.getProperty("DUAL_EMAIL_FROM");
                                    String subject=Constants.getProperty("DUAL_EMAIL_SUBJECT");
                                    String fileNameTobeDisplayed =Constants.getProperty("DUAL_EMAIL_FILENAME_DISPLAY");
                                    if (logger.isDebugEnabled()) {
                                            logger.debug(methodName,"pushEmail="+pushEmail);
                                    }
                                    boolean isAttachment=true;
                                    try {
                                    	if (logger.isDebugEnabled()) {
                                            logger.debug(methodName,"SUCCESS EMAIL :  to="+to+", from="+from+", message="+message+", isAttachment="+isAttachment+", fileName="+fileName+", fileNameTobeDisplayed="+fileNameTobeDisplayed);
                                    	}
										EMailSender.sendMail( to,from,null,null,subject, message,isAttachment,fileName,fileNameTobeDisplayed);
									} catch (Exception e) {
										logger.errorTrace(methodName,e);
									}
                                    try {
                                    	if (logger.isDebugEnabled()) {
                                            logger.debug(methodName,"FAIL EMAIL :  to="+to+", from="+from+", message="+message+", isAttachment="+isAttachment+", failFileName="+failFileName+", fileNameTobeDisplayed="+fileNameTobeDisplayed);
                                    	}
										EMailSender.sendMail( to,from,null,null,subject, message,isAttachment,failFileName,fileNameTobeDisplayed);
									} catch (Exception e) {
										logger.errorTrace(methodName,e);
									}
                         
                                }
		                        processStatusVO.setExecutedUpto(dateCount);
		                        processStatusVO.setExecutedOn(currentDate);
		                        processStatusDAO=new ProcessStatusDAO();
		                        maxDoneDateUpdateCount=processStatusDAO.updateProcessDetail(con,processStatusVO);
		                        if(maxDoneDateUpdateCount>0) {
		                        	con.commit();
		                        }
		                        else {
		                            con.rollback();
		                            cstmt.close();
		                            throw new BTSLBaseException(methodName,methodName,PretupsErrorCodesI.REVERSE_COMMISION_COULD_NOT_UPDATE_MAX_DONE_DATE);
		                        }
                            }
                        }
                    }
                    EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"ReverseHirerachyCommisionNew[process]","","",""," ReverseHirerachyCommisionNew process has been executed successfully.");
                }
            }
            else {
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
                                if (markProcessStatusAsComplete(con,processId)==1)
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

        /**
         * @descriptions To mark process status
         * @param pCon
         * @param pProcessId
         * @return
         */
        private static int markProcessStatusAsComplete(Connection pCon,String pProcessId)
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

        /**
         * @descriptions To write the success logger for dual wallet transactions
         * @param finalDirectoryPath
         * @param hirrechalList
         * @param forDate
         * @return
         */
        public static String writeToFile(String finalDirectoryPath, ArrayList<ReverseHirerachyCommisionVO> hirrechalList,Date forDate){

            String  methodName="writeToFile";
            if (logger.isDebugEnabled())
                    logger.debug(methodName,"Entered with FinalDirectoryPath ::"+finalDirectoryPath+", HirrechalList size"+hirrechalList.size()+", For Date = "+forDate);
            String sucFileName = null;
            String message = null;
            String forDateLocal=null;
            try{
            	forDateLocal=BTSLUtil.getDateTimeStringFromDate(forDate,  Constants.getProperty("DUAL_WALLET_FILE_NAME_DATE_FORMAT"));
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
                        try {
                        	Double doubleFoc =reverseHirCom.getFocAmount(); // 7
                            int intFoc = doubleFoc.intValue();
                            message=reverseHirCom.getMsisdn()+","+intFoc+","+PretupsI.PAYMENT_INSTRUMENT_TYPE_CASH +","+ Constants.getProperty("DUAL_REMARKS")+" "+reverseHirCom.getThroughUser() ;
						} catch (Exception e) {
							logger.errorTrace(methodName, e);
							message=reverseHirCom.getMsisdn()+","+reverseHirCom.getFocAmount()+","+PretupsI.PAYMENT_INSTRUMENT_TYPE_CASH +","+ Constants.getProperty("DUAL_REMARKS")+" "+reverseHirCom.getThroughUser() ;
							
						}
                        sucWriter.println(message);

                }
                hirrechalList.clear();

            }catch(Exception e){
            	logger.errorTrace(methodName, e);
            }
            return sucFileName;
        }
        
        /**
         * @descriptions To write the fail logger for dual wallet transactions
         * @param finalDirectoryPath
         * @param arrayForFail
         * @param forDate
         * @return
         */
        public static String writeToFileForFail(String finalDirectoryPath, String[] arrayForFail,Date forDate){

	        String  methodName="writeToFileForFail";
	        if (logger.isDebugEnabled())
	                logger.debug(methodName,"Entered with FinalDirectoryPath ::"+finalDirectoryPath +", For Date ="+forDate);
	        String failFileName = null;
	        String message = null;
	        String forDateLocal=null;
	        try{
	        	forDateLocal=BTSLUtil.getDateTimeStringFromDate(forDate,  Constants.getProperty("DUAL_WALLET_FILE_NAME_DATE_FORMAT"));
	        	if (logger.isDebugEnabled())
	                logger.debug(methodName,"forDateLocal ="+forDateLocal );
	        }
	        catch(Exception e){
	                logger.errorTrace(methodName, e);
	                forDateLocal=forDate.toString();	                
	
	        }
	        failFileName = finalDirectoryPath+"/"+Constants.getProperty("DUAL_WALLET_FILE_ERR_NAME")+forDateLocal+".txt";
	        try(PrintWriter failWriter = new PrintWriter(failFileName, "UTF-8")){
	        	for(int i=0;i<arrayForFail.length;i++)
	    		{
	    			if(arrayForFail[i]!=null){
	        			failWriter.println(arrayForFail[i]);
	        			if (logger.isDebugEnabled())
	                        logger.debug(methodName,"Element for the Day "+arrayForFail[i]+" forDateLocal "+forDateLocal);
	    			}
	    			else
	    				break;
	    		}
	    
	        	arrayForFail=null;
	            } catch(Exception e){
	            	logger.errorTrace(methodName, e);
	        }
	        return failFileName;
    }

}
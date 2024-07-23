package com.btsl.pretups.processes.clientprocesses;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;


public class ZBFNFUpload {
	
private static Log log = LogFactory.getLog(ZBFNFUpload.class.getName());


private String entered="Entered";
private String exited="Exited";
private String fail="Fail_";
private String filen="File=";

private String filePath; // File path name

private BufferedReader in; // BufferedReader object that is created for
// each file.

private File inputFile; // File object for input file

private String fileExt; // Files are picked up only this extention from the
// specified directory

private File destFile; // Destination File object
private String moveLocation; // Input file is move to this location After

private File rejecFile;

private String rejectedFileLocation;

private ArrayList fileList; // Contain all the file object thats name start
// with file prefix.


private ArrayList<ZBFnFVO> uploadZbFnfList= new ArrayList<ZBFnFVO>();

private ArrayList<ZBFnFVO> rejectedZbFnfList= new ArrayList<ZBFnFVO>();

private ArrayList<ZBFnFVO> deleteZbFnfList= new ArrayList<ZBFnFVO>();


private int max_header_attr = 6;

private String ZBFNF_headers[];

private String ZBFNF_headerString;

private  String network_code;

private StringBuilder errorReason;

private long numberOfLinesinFile=0;

private String[] supportedTypes;

private int ZBFNF_MAX_MSISDNLENGTH;

private String ZBFNF_DateFormat;

private int Max_No_Records;

private static final String CSV_SEPARATOR = ",";

private ProcessStatusVO processStatusVO = null;

private ProcessBL processBL = null;

private boolean processStatusOK = false;

private  String allowedExt = "csv";// Input file should be either txt

private void process() throws BTSLBaseException  {
	 final String methodName = "process";
     if (log.isDebugEnabled()) {
         log.debug(methodName, entered);
     }
    
     int successCount = 0;
     int deleteCount=0;
     boolean rename;
     
     try(Connection con = OracleUtil.getSingleConnection()){
    	 
    	 if (log.isDebugEnabled()) {
             log.debug(methodName, "START::TOTAL MEMORY --->" + Runtime.getRuntime().totalMemory() + " START::FREE MEMORY --->" + Runtime.getRuntime().freeMemory());
         }
    	 
    	 
    	// Create connection
         

         // check the connection for null if it is null stop with showing
         // error message.
         if (con == null) {
             throw new BTSLBaseException(this, methodName, "DB COnnection Null");
         }
               
      // Load constant values from the Constant.props file which are
         // defined for the process
         loadConstantValues();
         

         // Load All the files from the specified directory path.
         loadFilesFromDir();
         

         // processing each file present at the location specified for file
         // upload and stored in the fileList
         for (int l = 0, size = fileList.size(); l < size; l++) {
             // Getting the file object
             inputFile = (File) fileList.get(l);
             
             
             
             try {
                 // Files are stored in memory,before processing check
                 // whether it is exist or not.        	
            	 
            	 
                 if (!inputFile.exists()) {
                     if (log.isDebugEnabled()) {
                         log.debug(methodName, filen + inputFile + "does not exists.");
                     }
                     throw new BTSLBaseException(this, methodName, "InPut File Does not Exist");
                 }
                 
                 
                 if(inputFile.length()==0){
                	 if (log.isDebugEnabled()) {
                         log.debug(methodName, filen + inputFile + "is empty");
                     }
                	 String rejectFileName=fail+"_FileEmpty"+ inputFile.getName();
             		File rejectFile= new File(rejecFile+File.separator+rejectFileName);
             		 rename =inputFile.renameTo(rejectFile);
                	 continue;
                 }
                
                 
                 
                 if(!inputFile.getName().endsWith(".csv")){
                	 if (log.isDebugEnabled()) {
                         log.debug(methodName, filen + inputFile + "not a csv");
                     }
                	 String rejectFileName=fail+"invalid_FileFormat"+ inputFile.getName();
             		File rejectFile= new File(rejecFile+File.separator+rejectFileName);
             		rename=inputFile.renameTo(rejectFile);
                	 continue;
                 }
                 
                 //Counting the number of lines in file.it is a java8 feauture.It wont work in java version below java 8.
                 numberOfLinesinFile= Files.lines(Paths.get(inputFile.getAbsolutePath())).skip(3).count();
                 String lastline =Files.lines(Paths.get(inputFile.getAbsolutePath())).skip(numberOfLinesinFile+2).findFirst().get();
                 
                 if(!"EOF".equalsIgnoreCase(lastline)){
                	 if (log.isDebugEnabled()) {
                         log.debug(methodName, filen + inputFile + "Exceeds the Max Records.");
                     }
                	 String rejectFileName=fail+"_Exceeds_MaxRecords_Size"+ inputFile.getName();
                     File rejectFile= new File(rejecFile+File.separator+rejectFileName);
                     rename=inputFile.renameTo(rejectFile);
                	 continue;
                 }
                 
                 
                 if( numberOfLinesinFile > Max_No_Records){
                	 if (log.isDebugEnabled()) {
                         log.debug(methodName, filen + inputFile + "Exceeds the Max Records.");
                     }
                	 String rejectFileName=fail+"_Exceeds_MaxRecords_Size"+ inputFile.getName();
                     File rejectFile= new File(rejecFile+File.separator+rejectFileName);
                     rename=inputFile.renameTo(rejectFile);
                	 continue;
                 }
                 
                 
                 setBufferReader();
                 
                 String line=in.readLine();
     			
                 network_code= getNetworkCode(line);
                 
                 if(NetworkCache.getNetworkByExtNetworkCode(network_code)==null) {
                	 if (log.isDebugEnabled()) {
                         log.debug(methodName, "Network Code = " + network_code + " not found in cache.");
                     }
                	 String rejectFileName=fail+"_NetworkCodeinvalid"+ inputFile.getName();
                     File rejectFile= new File(rejecFile+File.separator+rejectFileName);
                     rename=inputFile.renameTo(rejectFile);
                	 continue;
                	 
                 }
                 
                 validateProcess(con,network_code);     			
                 
                 validateHeader();
                                  
                 validateAndUpdatelist();               
                 
                 if(!uploadZbFnfList.isEmpty())
                	 successCount=UpdateDataInDB(con,uploadZbFnfList);
                 
                 if(!deleteZbFnfList.isEmpty())
                	 deleteCount=DeleteDataInDb(con, deleteZbFnfList);
                 
                 if (successCount >= 0 ||deleteCount>=0) {
                     if (in != null) {
                         in.close();
                     }
                     // Moving File after Processing
                     Date date = new Date();
                     String fileName = inputFile.getName();
                     String moveFileName = fileName.substring(0, fileName.indexOf('.')) + "_" + date.getDate() + "_" + date.getHours() + "_" + date.getMinutes() + "." + fileName.substring(fileName.indexOf('.') + 1);
                     boolean fileMoved = moveFile(inputFile.getPath(), moveFileName);
                     if (!fileMoved) {
                         throw new BTSLBaseException(this, methodName, "Error Moving File");
                     }
                     
                     
                     //Writing Rejected Records to Csv.
                     
                     if(!rejectedZbFnfList.isEmpty()){
                    	 
                    	 String rejectFilename="Error_"+fileName;
                    	 boolean rejctWriteStatus= writeRejectedRecordstoCsv(rejectedZbFnfList,rejectFilename);
                    	 
                    	 if (!rejctWriteStatus) {
                             throw new BTSLBaseException(this, methodName, "Error Writing Rejecting Records.");
                         }
                     }
                     
                     // Commiting the transaction after successful
                     // proccessing of file data.
                     con.commit();
                 } else {
                     throw new BTSLBaseException(this, methodName, "Error Updating Records");
                 }
                 if (log.isDebugEnabled()) {
                     log.debug(methodName, filen + inputFile + " is proccessed");
                 }
                 
                 
             }// end of inner try-block
             catch (BTSLBaseException be) {
            	 if (log.isDebugEnabled()) {
                     log.debug(methodName, filen + inputFile + "Error_General_Processing :BTSL Excep");
                 }
            	 in.close();
            	 String rejectFileName="Fail_General_ProcessingError_"+ inputFile.getName();
          		 File rejectFile= new File(rejecFile+File.separator+rejectFileName);
             	 if(inputFile.renameTo(rejectFile))
             	 {
               		log.debug(methodName, "File renamed successfully");
               	 }
            	 
                 if (con != null) {
                     try {
                         con.rollback();
                     } catch (Exception e1) {
                         log.errorTrace(methodName, e1);
                     }
                 }
                 log.error(methodName, "BTSLBaseException  = " + be.getMessage());
                 log.errorTrace(methodName, be);
             }// end of catch-BTSLBaseException
             catch (Exception e) {
            	 in.close();
            	 String rejectFileName="Fail_General_ProcessingError_"+ inputFile.getName();
          		 File rejectFile= new File(rejecFile+File.separator+rejectFileName);
             	 rename =inputFile.renameTo(rejectFile);
            	 
                 log.error(methodName, "Exception = " + e.getMessage());
                 if (con != null) {
                     try {
                         con.rollback();
                     } catch (Exception e1) {
                         log.errorTrace(methodName, e1);
                     }
                 }
                 log.error(methodName, "Exception e = " + e.getMessage());
                 log.errorTrace(methodName, e);
                 EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ZB_FNFUpload[ process]", "[File Name = " + inputFile + " ] [File Process Status is Unsuccessfull]", "", "", "Exception :" + e.getMessage());
             } finally {
                 try {
                     if (in != null) {
                         in.close();
                     }
                 } catch (Exception e1) {
                     log.errorTrace(methodName, e1);
                 }
                 if (log.isDebugEnabled()) {
                     log.debug(methodName, "File = " + inputFile + " is proccessed");
                 }
             }// end of inner finally
         }
         
       
         

    	 
     } catch (BTSLBaseException be) {
         log.error(methodName, "BTSLBaseException be= " + be.getMessage());
         
         throw be;
     }// end of catch-BTSLBaseException
     catch (Exception e) {
         log.error(methodName, "Exception be= " + e.getMessage());
         log.errorTrace(methodName, e);
         EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ZB_FNFUpload [process]", "processStatusVO.getProcessID() " +" ZB_FNFUpload", "", "", "Excep " + e.getMessage());
         throw new BTSLBaseException(this, methodName, e.getMessage());
     }// end of catch-Exception
     finally {
         try {
             // Setting the process status as 'C-Complete' if the
             // processStatusOK is true
             if (processStatusOK) {
                 Date date = new Date();
                 processStatusVO.setStartDate(processStatusVO.getStartDate());
                 processStatusVO.setExecutedOn(date);
                 processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                 ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
                 Connection con = OracleUtil.getSingleConnection();
                 int successU = processStatusDAO.updateProcessDetail(con, processStatusVO);
                 
                 // Commiting the process status as 'C-Complete'
                 if (successU > 0) {
                     con.commit();
                 } else {
                     EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ZB_FNFUpload[process]", "processStatusVO.getProcessID()" + ProcessI.ZB_FNF_UPLOAD_PROCESSID, "", "", "Error while updating the process status after completing the process");
                  }
                 con.close();
             }// end of IF-Checks the proccess status
         }// end of try-block
         catch (BTSLBaseException be) {
             log.errorTrace(methodName, be);
             log.error(methodName, "BTSLBaseException be= " + be.getMessage());
             
         }// end of catch-BTSLBaseException
         catch (Exception e) {
             log.errorTrace(methodName, e);
             log.error(methodName, "Exception e " + e.getMessage());
            
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ZB_FNFUpload[process]", "processStatusVO.getProcessID()" + ProcessI.ZB_FNF_UPLOAD_PROCESSID, "", "", "BaseException:" + e.getMessage());
             
         }// end of catch-Exception
         
          
        
         if (log.isDebugEnabled()) {
             log.debug(methodName, "Exiting ");
         }
     }//

	
}


private int DeleteDataInDb(Connection con,ArrayList<ZBFnFVO> deleteList) throws BTSLBaseException{
	final String METHODNAME = "DeleteDataInDb";
    if (log.isDebugEnabled()) {
        log.debug(METHODNAME, entered);
    }
	
 	PreparedStatement deletePstmt = null;
 
    int totRecords = 0;
    int queryResult=-1;
    int msisdnAdded = 0;
    int i = 0;
    
    StringBuilder deleteSql=new StringBuilder("DELETE FROM FNF_ZERO_BASE_CUSTOMER WHERE ");
    deleteSql.append("MSISDN1=? and MSISDN2=? and RECORD_TYPE=? ");
    
    if (log.isDebugEnabled()) {
        log.debug("deleteData", "Query=" + deleteSql);
    }
    
    try{
    	deletePstmt = con.prepareStatement(deleteSql.toString());
    	 // here we try to insert the record in database
         // if record already exists in database then exception is thrown
         // the exception is caught in catch block and check if it is of
         // unique key constraint voilation
         // if exception is of unique key constraint violation then add it into the rejected list.        
    	 
    	 for(ZBFnFVO dataObject:deleteList){
    		 
    		 try{
    			 i=0;
    			 deletePstmt.setString(++i, dataObject.getMsisdn1());
    			 deletePstmt.setString(++i, dataObject.getMsisdn2());
    			 deletePstmt.setString(++i, dataObject.getType());
    			 
    			// Execute Query
                 // if sql exception occurs then it is caught in catch block
                 queryResult = deletePstmt.executeUpdate();
                 if (queryResult <= 0) {
                	 dataObject.setRemarks("Record not Exists in Db");
    				 rejectedZbFnfList.add(dataObject);
                	 log.error(METHODNAME, "Error While delete records.queryResult<0");
                 } else {
                     msisdnAdded += queryResult;
                     totRecords++; // Total records count to be inserted or
                 }
                 
                 deletePstmt.clearParameters();
    			 
    		 }catch(SQLException sqe){
    			 
    				 dataObject.setRemarks("Record not Exists in Db/Error while deleting");
    				 rejectedZbFnfList.add(dataObject);
    			
                     log.error("deleteData", "SQLException= " + sqe.getMessage());
                     log.errorTrace(METHODNAME, sqe);
    			 
    			 deletePstmt.clearParameters();
    		 }
    	 }
    	 }catch(Exception e){
        	 log.error("DeleteDataInDB", "Exc" + e.getMessage());
             log.errorTrace(METHODNAME, e);
             throw new BTSLBaseException(this, "DeleteDataInDB", "error.general.processing");
        }finally {
            if (log.isDebugEnabled()) {
                log.debug(METHODNAME, "processed till record no:" + totRecords);
            }
            // Write in LOGS
            if (log.isDebugEnabled()) {
                log.debug(METHODNAME, "p_userID:" + PretupsI.SYSTEM_USER +"NetworkCode :"+network_code+ " Processed=" + " ,No of records=" + totRecords);
            }
           
            try {
                if (deletePstmt != null) {
                	deletePstmt.close();
                }
            } catch (Exception ex) {
                log.errorTrace(METHODNAME, ex);
            }
            
           
        }// end of finally
       
     
    	return msisdnAdded;
    
    
}

private int UpdateDataInDB(Connection con,ArrayList<ZBFnFVO> uploadList) throws BTSLBaseException{
	final String METHODNAME = "UpdateDataInDB";
    if (log.isDebugEnabled()) {
        log.debug(METHODNAME, entered);
    }
	
 	PreparedStatement insertPstmt = null;
 
    int totRecords = 0;
    int queryResult=-1;
    int msisdnAdded = 0;
    int i = 0;
	StringBuilder insertSql = new StringBuilder("INSERT INTO FNF_ZERO_BASE_CUSTOMER(MSISDN1, MSISDN2, ");
    insertSql.append("RECORD_TYPE, EXPIRY_DATE,created_on) ");
    insertSql.append("VALUES (?,?,?,?,?) ");

    if (log.isDebugEnabled()) {
        log.debug(METHODNAME, "Query=" + insertSql);
    }
    
    try{
    	 insertPstmt = con.prepareStatement(insertSql.toString());
    	 // here we try to insert the record in database
         // if record already exists in database then exception is thrown
         // the exception is caught in catch block and check if it is of
         // unique key constraint voilation
         // if exception is of unique key constraint violation then add it into the rejected list.        
    	 
    	 for(ZBFnFVO dataObject:uploadList){
    		 
    		 try{
    			 i=0;
    			 insertPstmt.setString(++i, dataObject.getMsisdn1());
    			 insertPstmt.setString(++i, dataObject.getMsisdn2());
    			 insertPstmt.setString(++i, dataObject.getType());
    			 insertPstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(dataObject.getExpiryDate()));
    			 insertPstmt.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(new Date()));
    		
    			// Execute Query
                 // if sql exception occurs then it is caught in catch block
                 queryResult = insertPstmt.executeUpdate();
                 if (queryResult <= 0) {
                	 log.error(METHODNAME, "Error While inserting records.queryResult<0");
                 } else {
                     msisdnAdded += queryResult;
                     totRecords++; // Total records count to be inserted or
                 }
                 
                 insertPstmt.clearParameters();
    			 
    		 }catch(SQLException sqe){
    			 if (sqe.getErrorCode() == 00001) {
    				 dataObject.setRemarks("Record Already Exist/Duplicate records in MSISDN1/MSISDN2 ");
    				 rejectedZbFnfList.add(dataObject);
    			 }else {
                     log.error(METHODNAME, "SQLException= " + sqe.getMessage());
                     log.errorTrace(METHODNAME, sqe);
    			 }
    			 insertPstmt.clearParameters();
    		 }
    		 
    	 }
         
    }catch(Exception e){
    	 log.error(METHODNAME, "Exception=" + e.getMessage());
         log.errorTrace(METHODNAME, e);
         throw new BTSLBaseException(this,METHODNAME, "error.general.processing");
    }finally {
        if (log.isDebugEnabled()) {
            log.debug("insertData", "processed till record no:" + totRecords);
        }
        // Write in LOGS
        if (log.isDebugEnabled()) {
            log.debug("insertData", "p_userID:" + PretupsI.SYSTEM_USER + "NetworkCode:"+network_code+" Processed=" + " ,No of records=" + totRecords);
        }
       
        try {
            if (insertPstmt != null) {
                insertPstmt.close();
            }
        } catch (Exception ex) {
            log.errorTrace(METHODNAME, ex);
        }
        
       
    }// end of finally
   
 
	return msisdnAdded;
}

private void validateAndUpdatelist() throws BTSLBaseException{
	final String METHODNAME = "validateAndUpdatemap";
    if (log.isDebugEnabled()) {
        log.debug(METHODNAME, entered);
    }
	String line="";
	ZBFnFVO dataObject=null;
	String[] arr =new String [max_header_attr];

	try{
		while((line=in.readLine()).toUpperCase().startsWith("EOF")){
			 arr=line.split(",");
			 if(isRecordValid(arr[0],arr[1], arr[2], arr[3])){
				 
				 if("U".equalsIgnoreCase(arr[4])){
					 //record upload
				
					 dataObject= prepareVO(arr);
					 	 
					 uploadZbFnfList.add(dataObject);
				
				 }else if("D".equalsIgnoreCase(arr[4])){
					 // record delete
					 dataObject= prepareVO(arr);
					 
					 deleteZbFnfList.add(dataObject);
				 }else{
					// records validation rejected logic 
					 dataObject= prepareVO(arr);
					 dataObject.setRemarks("Invalid Operation Code.");
					 rejectedZbFnfList.add(dataObject);
				 }
				 
			 }else{
				 // records validation rejected logic 
				 dataObject= prepareVO(arr);
				 dataObject.setRemarks(errorReason.toString());
				 rejectedZbFnfList.add(dataObject);
			 }
			 
		
		}
	}catch(IOException io){
		log.error(METHODNAME, "IO Exception = " + io.getMessage());
        log.errorTrace(METHODNAME, io);
        throw new BTSLBaseException(this, "validateAndUpdatemap", io.getMessage());

	} catch (ParseException pe) {
		log.error(METHODNAME, "Date Parse Exception= " + pe.getMessage());
        log.errorTrace(METHODNAME, pe);
        throw new BTSLBaseException(this, METHODNAME, pe.getMessage());

	}
		
	if (log.isDebugEnabled()) {
        log.debug(METHODNAME, exited);
    }
}


public boolean isRecordValid(String msisdn1, String msisdn2, String type ,String expiryDate ) throws  ParseException {

	Date currentDate= new Date();
	
	errorReason = new StringBuilder();
	boolean flag =true;
	try{
		if((!BTSLUtil.isNumeric(msisdn1) && !BTSLUtil.isNullString(msisdn1)) ||(msisdn1.length()!=ZBFNF_MAX_MSISDNLENGTH)){
			errorReason.append("MSISDN1 is Invalid");
			flag= false;
		}
		if((!BTSLUtil.isNumeric(msisdn2) && !BTSLUtil.isNullString(msisdn2)) ||( msisdn2.length()!=ZBFNF_MAX_MSISDNLENGTH) ){
			errorReason.append("MSISDN2 is Invalid");
			flag= false;
		}
	
		if(!BTSLUtil.getDateFromString(expiryDate, ZBFNF_DateFormat).after(currentDate)){
			errorReason.append("expiryDate is Invalid");
			flag= false;
		}
		
		if(!Arrays.asList(supportedTypes).contains(type)){
			errorReason.append("Rule Type is Invalid");
			flag= false;
		}
		
		if(msisdn1.equals(msisdn2)){
			errorReason.append("MSISDN1 and MSISDN2 Are Same.");
			flag= false;
		}
		
	}
	catch(NumberFormatException e){
		
		flag= false;
	}
	return flag;
}

private ZBFnFVO prepareVO(String[] arr) throws ParseException{
	ZBFnFVO  dataObject = new ZBFnFVO();
	String ZBFNF_Dateformat=Constants.getProperty("ZBFNF_DateFormat");
	SimpleDateFormat formatter = new SimpleDateFormat(ZBFNF_Dateformat);
	dataObject.setMsisdn1(arr[0]);
	dataObject.setMsisdn2(arr[1]);
	dataObject.setType(arr[2]);
	dataObject.setExpiryDate(formatter.parse(arr[3]));
	dataObject.setOperation(arr[4]);
	
	
	return dataObject;
}

private void validateProcess(Connection con,String networkCode) throws BTSLBaseException, SQLException{  
	String METHODNAME="validateProcess";
	processBL = new ProcessBL();
    // check the process status by calling checkProcessUnderprocess
    // method of processBL,if its ok continue else stop the process with
    // error message.
    processStatusVO = processBL.checkProcessUnderProcessNetworkWise(con,  ProcessI.ZB_FNF_UPLOAD_PROCESSID, networkCode);

    if (!(processStatusVO != null && processStatusVO.isStatusOkBool())) {
    	
        throw new BTSLBaseException(this, METHODNAME, PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
    }
    processStatusOK = processStatusVO.isStatusOkBool();
    // Commiting the status of process status as 'U-Under Process'.
    con.commit();

}


private String getNetworkCode(String line) throws BTSLBaseException {
	final String methodName = "getNetworkCode()";
	String[] arr=line.split(":");
	if(arr.length > 2)
	{
		 log.debug(methodName,"Error : Invalid network");
		 throw new BTSLBaseException("ZB_FNFUpload", methodName, "ERROR in Reading Network COde" );
	}
		
	return arr[1];
}



private void validateHeader() throws BTSLBaseException{
	final String METHODNAME = "validateHeader";
    if (log.isDebugEnabled()) {
        log.debug(METHODNAME, entered);
    }
    boolean rename;
    String str = "";
    String headerArray[]=null;
        try{
    	  str = in.readLine();
    	  headerArray= str.split(",");
    	  
    	  if(!headerArray[0].equalsIgnoreCase(ZBFNF_headers[0]) || !headerArray[1].equalsIgnoreCase(ZBFNF_headers[1])
    			  ||  !headerArray[2].equalsIgnoreCase(ZBFNF_headers[2])
    			  || !headerArray[3].equalsIgnoreCase(ZBFNF_headers[3])
    			  || !headerArray[4].equalsIgnoreCase(ZBFNF_headers[4])){
    		 
    		  in.close();
    		  String rejectFileName="Fail_"+"_HeaderInvalid"+ inputFile.getName();
       		  File rejectFile= new File(rejecFile+File.separator+rejectFileName);
          	  if(inputFile.renameTo(rejectFile))
          	  {
              		log.debug(METHODNAME, "File renamed successfully");              	  
          	  }
    		  
    		  throw new BTSLBaseException(this, "validateHeader","Header is not in proper Format.");
    	  }
    	  
    }catch(Exception e){
    	log.error(METHODNAME, "Header Access = " + e.getMessage());
        log.errorTrace(METHODNAME, e);
        throw new BTSLBaseException(this, METHODNAME, e.getMessage());

    }
    
    if (log.isDebugEnabled()) {
        log.debug(METHODNAME, "Exiting");
    }
    
}

/**
 * This method is used to set the Buffered Reader object for the file.
 * 
 * @throws BTSLBaseException
 * @throws IOException 
 */
private void setBufferReader() throws BTSLBaseException, IOException {
    final String METHODNAME = "setBufferReader";
    if (log.isDebugEnabled()) {
        log.debug(METHODNAME, entered);
    }
   
    try( FileReader fr=new FileReader(inputFile)) {
        if (log.isDebugEnabled()) {
            log.debug(METHODNAME, "inputFile = " + inputFile);
        }
       
        in = new BufferedReader(fr);
    }// end of try block
    catch (IOException ioe) {
        log.error(METHODNAME, "IOException ioe = " + ioe.getMessage());
        log.errorTrace(METHODNAME, ioe);
        throw new BTSLBaseException(this, METHODNAME, ioe.getMessage());
    }// end of catch-IOException
    finally{
    	
    if (log.isDebugEnabled()) {
        log.debug(METHODNAME, exited);
    }
    }
}// end of setBufferReader


/**
 * This method is used to load all the files with specified file extension
 * from the file location
 * specified in the Constant.props.
 * All these file objects are stored in ArrayList.
 * 
 * @throws BTSLBaseException
 */
private void loadFilesFromDir() throws BTSLBaseException {
    final String METHODNAME = "loadFilesFromDir";
    if (log.isDebugEnabled()) {
        log.debug(METHODNAME, entered);
    }
    File directory = null;
    try {
        directory = new File(filePath);
        // Check if the directory contains any files
        if (directory.list() == null) {
            log.error(METHODNAME, "No file exists at the location specified from where the file will be uploaded");
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "ZB_FNFUpload[loadFilesFromDir]", "", "", "", "No file exists at the location of file upload");
            throw new BTSLBaseException(this, "loadFileFromDir", "No Files Existing in the directory");
        }
        
        // List of files that ends with the specified extension.
        File[] tempFileArray = directory.listFiles();
        fileList = new ArrayList();

        // Storing all the files(not dir)to array list for uploading
        for (int l = 0, size = tempFileArray.length; l < size; l++) {
            if (tempFileArray[l].isFile()) {
                fileList.add(tempFileArray[l]);
                if (log.isDebugEnabled()) {
                    log.debug("loadFileFromDir", "File = " + tempFileArray[l] + " is added to fileList");
                }
            }
        }// end of for loop

        // Check whether the directory contains any file ending with the
        // specified file extension.
        if (fileList.isEmpty()) {
            throw new BTSLBaseException(this, METHODNAME, "No Files Existing in the directory");
        }
    } catch (BTSLBaseException be) {
        log.error(METHODNAME, "BTSLBaseException be = " + be.getMessage());
        throw be;
    }// end of catch-BTSLBaseException
    catch (Exception e) {
        log.errorTrace(METHODNAME, e);
        log.error(METHODNAME, "Exception e = " + e.getMessage());
        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ZB_FNFUpload[loadFilesFromDir]", "", "", "", "Exception:" + e.getMessage());
        throw new BTSLBaseException(this, METHODNAME, e.getMessage());
    }// end of catch-Exception
    if (log.isDebugEnabled()) {
        log.debug(METHODNAME, exited);
    }
}// end of loadFilesFromDir

private boolean writeRejectedRecordstoCsv(ArrayList<ZBFnFVO> rejectedList,String rejectFileName) throws IOException{
	String METHODNAME="writeRejectedRecordstoCsv";
	 if (log.isDebugEnabled()) {
	        log.debug(METHODNAME, "Entering");
	    }
	 File rejectFile= new File(rejecFile+File.separator+rejectFileName);
	try(FileOutputStream fout= new FileOutputStream(rejectFile);BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fout));){
		
		
		
		
		
		StringBuilder rejectedLines= new StringBuilder();
		
		String networkDetails="NetworkCode:"+network_code;
		rejectedLines.append(networkDetails);
		
		bw.write(rejectedLines.toString());
		bw.newLine();
		rejectedLines= new StringBuilder();
		
		rejectedLines.append(ZBFNF_headerString);
		bw.write(rejectedLines.toString());
		bw.newLine();
		
		for(ZBFnFVO dataObject:rejectedList){
			rejectedLines= new StringBuilder();
			rejectedLines.append(dataObject.getMsisdn1());
			
			rejectedLines.append(CSV_SEPARATOR);
			
			rejectedLines.append(dataObject.getMsisdn2());
			
			rejectedLines.append(CSV_SEPARATOR);
			
			rejectedLines.append(dataObject.getType());
			
			rejectedLines.append(CSV_SEPARATOR);
			
			rejectedLines.append(dataObject.getExpiryDate());
			
			rejectedLines.append(CSV_SEPARATOR);
			
			rejectedLines.append(dataObject.getOperation());
			
			rejectedLines.append(CSV_SEPARATOR);
			
			rejectedLines.append(dataObject.getRemarks());
			
			bw.write(rejectedLines.toString());
			 
            bw.newLine();
			
		}
		bw.flush();
		
        bw.close();
        
      
        
        return true;
		
	}catch(Exception e){
		
		 log.errorTrace(METHODNAME, e);
	      log.error(METHODNAME, "Exception e= " + e.getMessage());
	     
	}finally{
		
		
	if (log.isDebugEnabled()) {
        log.debug(METHODNAME, "Exiting");
    }
	}
	return false;
}

/**
 * This method moves the files to the backup location once it is processed
 * and the data within it has been updated
 * in the database.It also checks whether the file being moved already
 * exists at the destination.If yes it throws
 * an exception
 * 
 * @param p_fileName
 *            String
 * @return boolean
 */

private boolean moveFile(String p_fileName, String p_newFileName) throws BTSLBaseException {
    if (log.isDebugEnabled()) {
        log.debug("moveFile", " Entered p_fileName:= " + p_fileName + " p_newFileName" + p_newFileName);
    }

    File fileRead = new File(p_fileName);
    File fileArchive = new File(moveLocation);
    if (!fileArchive.isDirectory()) {
        fileArchive.mkdirs();
    }
   
    fileArchive = new File(moveLocation + File.separator);
  
    boolean flag = fileRead.renameTo(new File(fileArchive, p_newFileName));
    if (log.isDebugEnabled()) {
        log.debug("moveFile", " Exiting File Moved= " + flag);
    }
    return flag;
}// end of moveFileToArchive



/**
 * This method is used to load the Constant values from the Constants file.
 * 
 * @throws BTSLBaseException
 */
private void loadConstantValues() throws BTSLBaseException {
    final String METHODNAME = "loadConstantValues";
    if (log.isDebugEnabled()) {
        log.debug(METHODNAME, entered);
    }
    try {


        // path where the input file will be placed for reading.
        filePath = BTSLUtil.NullToString(Constants.getProperty("ZBFNF_FILE_PATH")).trim();

        // Checking whether the file path is provided and if yes, whether it
        // exists or not.
        if (!(new File(filePath).exists())) {
            log.error(METHODNAME, "Directory Path = " + filePath + " does not exists");
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "ZB_FNFUpload [loadConstantValues]", "", "", "", "Configuration Problem, the location for where the file will be read is not correct");
            throw new BTSLBaseException(this, METHODNAME, "Input File Directory Does not exist.");
        }

        // Checking whether the file extention is provided or not and if
        // provided check that if it allowed or not.
        // only text files are allowed for now.
        fileExt = Constants.getProperty("ZBFNF_FILE_EXT");
        if (BTSLUtil.isNullString(fileExt) || !(allowedExt.contains(fileExt))) {
            log.error(METHODNAME, "File Extension = " + fileExt + " not defined correctly");
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "ZB_FNFUpload[loadConstantValues]", "", "", "", "Configuration Problem, Parameter ZBFNF_FILE_EXT not defined correctly");
            throw new BTSLBaseException(this, METHODNAME, "File Extension Not defined.");
        }
        fileExt = fileExt.trim();
        
     // Destination location for the file where it will be moved after
        // reading is complete and the data in it is uploaded
        moveLocation = BTSLUtil.NullToString(Constants.getProperty("ZBFNF_MOVE_LOCATION")).trim();
        destFile = new File(moveLocation);

        // Checking the destination location whether it exist or not,if not
        // create the directory.
        if (!destFile.exists()) {
            if (log.isDebugEnabled()) {
                log.debug(METHODNAME, "Destination Location = " + moveLocation + " does notexist.");
            }
            boolean fileCreation = destFile.mkdirs();
            if (fileCreation) {
                if (log.isDebugEnabled()) {
                    log.debug(METHODNAME, "New Location = " + destFile + "has been created successfully");
                }
            }
        }
        
        rejectedFileLocation =  BTSLUtil.NullToString(Constants.getProperty("ZBFNF_REJECTEDRECORDS_LOCATION")).trim();
        
        rejecFile=new File(rejectedFileLocation);
        
     // Checking the destination location whether it exist or not,if not
        // create the directory.
        if (!rejecFile.exists()) {
            if (log.isDebugEnabled()) {
                log.debug(METHODNAME, "Destination Location = " + rejectedFileLocation + " does not exist");
            }
            boolean fileCreation = rejecFile.mkdirs();
            if (fileCreation) {
                if (log.isDebugEnabled()) {
                    log.debug(METHODNAME, "New Location = " + rejecFile + "has been created successfully");
                }
            }
        }
        
        ZBFNF_headers = Constants.getProperty("ZBFNF_Header").split(",");
        
        ZBFNF_headerString=Constants.getProperty("ZBFNF_Header");
        
         supportedTypes=Constants.getProperty("ZBFNF_RULETYPES").split(",");
        
         ZBFNF_DateFormat=Constants.getProperty("ZBFNF_DateFormat");
        
         ZBFNF_MAX_MSISDNLENGTH=Integer.parseInt(Constants.getProperty("ZBFNF_MAX_MSISDNLENGTH"));
        
         Max_No_Records= Integer.parseInt(Constants.getProperty("ZBFNF_MAX_No_Records"));
        
    }// end of try block
    catch (BTSLBaseException be) {
        log.error(METHODNAME, "BTSLBaseException be = " + be.getMessage());
        throw be;
    }// end of catch-BTSLBaseException
    catch (Exception e) {
        log.errorTrace(METHODNAME, e);
        log.error(METHODNAME, "Exception e= " + e.getMessage());
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ZB_FNFUpload[loadConstantValues]", "", "", "", "Exception:" + e.getMessage());
        throw new BTSLBaseException(this, METHODNAME, e.getMessage());
    }// end of catch-Exception
    if (log.isDebugEnabled()) {
        log.debug(METHODNAME, "Exiting: _FILE_PATH = " + filePath + " fileExt= " + fileExt + "moveLocation= " );
    }
}// end of loadConstantValues

public static void loadCachesAndLogFiles(String p_arg1, String p_arg2) throws BTSLBaseException {
    final String METHODNAME = "loadCachesAndLogFiles";
    if (log.isDebugEnabled()) {
        log.debug( METHODNAME , " Entered with p_arg1=" + p_arg1 + " p_arg2=" + p_arg2);
    }
    File logconfigFile = null;
    File constantsFile = null;
    try {
        constantsFile = new File(p_arg1);
        if (!constantsFile.exists()) {

            log.error("ZB_FNFUpload[loadCachesAndLogFiles.]", " Constants file not found on location:: " + constantsFile.toString());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "ZB_FNFUpload [loadCachesAndLogFiles]", "", "", "", " The Constants file doesn't exists at the path specified. ");
            throw new BTSLBaseException("ZB_FNFUpload", " loadCachesAndLogFiles ","Missing COnstant file.");
        }

        logconfigFile = new File(p_arg2);
        if (!logconfigFile.exists()) {

            log.error("ZB_FNFUpload[loadCachesAndLogFiles:]", " ProcessLogConfig file not found on location:: " + logconfigFile.toString());
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ZB_FNFUpload[ loadCachesAndLogFiles]", "", "", "", " The ProcessLogConfig file doesn't exists  at the path specified. ");
            throw new BTSLBaseException("ZB_FNFUpload.", "loadCachesAndLogFiles ", "Missing Log File");
        }
        ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
   
    } catch (BTSLBaseException be) {
        log.error("ZB_FNFUpload[loadCachesAndLogFile]", "BTSLBaseException =" + be.getMessage());
        log.errorTrace(METHODNAME, be);
        throw be;
    }// end of BTSLBaseException
    catch (Exception e) {
        log.errorTrace(METHODNAME, e);
        log.error("ZB_FNFUpload[loadCachesAndLogFiles ]", " Exception =" + e.getMessage());
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ZB_FNFUpload[loadCachesAndLogFiles]", "", "", "", "Exception=" + e.getMessage());
        throw new BTSLBaseException("ZB_FNFUpload ", " loadCachesAndLogFiles ", "Error");
    }// end of Exception
    finally {
        if (logconfigFile != null) {
            logconfigFile = null;
        }
        if (constantsFile != null) {
            constantsFile = null;
        }
        if (log.isDebugEnabled()) {
            log.debug("ZB_FNFUpload[loadCachesAndLogFiles]", " Exiting..........");
        }
    }// end of finally
}
public static void main(String[] args) {
	
	final String METHODNAME = "main";
    if (log.isDebugEnabled()) {
        log.debug(" ZB_FNFUpload ", "Entered main ");
    }
    
    ZBFNFUpload upload = new ZBFNFUpload();
    
    try {
        if (args.length != 2) {
            log.error(METHODNAME, " Usage : ZB_FNFUpload [Constants file] [ProcessLogConfig file]");
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "ZB_FNFUpload[main]", "", "", "", "Improper usage. Usage : ZB_FNFUpload [Constants file] [ProcessLogConfig file]");
            throw new BTSLBaseException("ZB_FNFUpload ", " main ", "Missing Initial files.");

        }
        loadCachesAndLogFiles(args[0], args[1]);
        upload.process();
    } catch (BTSLBaseException be) {

        log.error("ZB_FNFUpload main()", "BTSLBaseException be=" + be.getMessage());
        log.errorTrace(METHODNAME, be);
    } catch (Exception e) {

        log.error("ZB_FNFUpload main()", " Exception e= " + e.getMessage());
        log.errorTrace(METHODNAME, e);
    } finally {
        ConfigServlet.destroyProcessCache();
        if (log.isDebugEnabled()) {
            log.debug(" ZB_FNFUpload ", "Exiting main ");
        }
    }

}




}

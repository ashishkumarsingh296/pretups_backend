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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

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
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;


public class UploadZBFNF {
	
private static Log log = LogFactory.getLog(UploadZBFNF.class.getName());


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

private String ZBFNF_File_Name;

private int Max_No_Records;

private static final String CSV_SEPARATOR = ",";

private ProcessStatusVO processStatusVO = null;

private ProcessBL processBL = null;

private boolean processStatusOK = false;
private boolean validateProcessFlag=true;
private  String allowedExt = "csv";// Input file should be either txt
private String _msisdnString = null;

private void process() throws BTSLBaseException  {
	 final String METHODNAME = "process";
     if (log.isDebugEnabled()) {
         log.debug(METHODNAME, entered);
     }
    
     int successCount = 0;
     int deleteCount=0;
     boolean rename;
     String messages=null;
     Locale locale = new Locale("en", "US");
    
     try(Connection con = OracleUtil.getSingleConnection()){
    	 
    	 if (log.isDebugEnabled()) {
             log.debug(METHODNAME, "START::TOTAL MEMORY --->" + Runtime.getRuntime().totalMemory() + " START::FREE MEMORY --->" + Runtime.getRuntime().freeMemory());
         }
    	 _msisdnString = new String(Constants.getProperty("adminmobile"));
			
         if (log.isDebugEnabled())
         	log.debug(METHODNAME, "_msisdnString: " + _msisdnString);
         
         String[] adm_msisdn = _msisdnString.split(",");
         PushMessage pushMessage = null;
    	 
    	// Create connection
         

         // check the connection for null if it is null stop with showing
         // error message.
         if (con == null) {
             throw new BTSLBaseException(this, METHODNAME, "DB COnnection Null");
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
                         log.debug(METHODNAME, filen + inputFile + "does not exists.");
                     }
                     throw new BTSLBaseException(this, METHODNAME, "Input File Does not Exist");
                 }
                 
                 
                 if(inputFile.length()==0){
                	 if (log.isDebugEnabled()) {
                         log.debug(METHODNAME, filen + inputFile + "is empty");
                     }
                	 String rejectFileName=fail+"_FileEmpty"+ inputFile.getName();
             		 File rejectFile= new File(rejecFile+File.separator+rejectFileName);
             		 rename =inputFile.renameTo(rejectFile);
             		throw new BTSLBaseException(this, METHODNAME, "Input File Empty");
                 }
                
                 
                 
                 if(!inputFile.getName().endsWith(".csv")){
                	 if (log.isDebugEnabled()) {
                         log.debug(METHODNAME, filen + inputFile + "not a csv");
                     }
                	 String rejectFileName=fail+"invalid_FileFormat"+ inputFile.getName();
             		File rejectFile= new File(rejecFile+File.separator+rejectFileName);
             		rename=inputFile.renameTo(rejectFile);
             		throw new BTSLBaseException(this, METHODNAME, "Input File is not in CSV ");
                 }
				 
				 String pattern = "^[a-zA-Z0-9_.]*$";
                 if(!inputFile.getName().startsWith(ZBFNF_File_Name) || !inputFile.getName().matches(pattern)){
                	 if (log.isDebugEnabled()) {
                         log.debug(METHODNAME, filen + inputFile + "not a valid File Name");
                     }
                	 String rejectFileName=fail+"invalid_FileFormat"+ inputFile.getName();
             		File rejectFile= new File(rejecFile+File.separator+rejectFileName);
             		rename=inputFile.renameTo(rejectFile);
             		throw new BTSLBaseException(this, METHODNAME, "Input File does not have valid File Name ");
                 }
                 
                 //Counting the number of lines in file.it is a java8 feauture.It wont work in java version below java 8.
                 numberOfLinesinFile= Files.lines(Paths.get(inputFile.getAbsolutePath())).skip(3).count();
                 if(numberOfLinesinFile == 0 ){
                	 if (log.isDebugEnabled()) {
                         log.debug(METHODNAME, filen + inputFile + " EOF is missing or No record found.");
                     }
                	String rejectFileName=fail+"Invalid_File_RecordFormat_"+ inputFile.getName();
             		File rejectFile= new File(rejecFile+File.separator+rejectFileName);
             		rename=inputFile.renameTo(rejectFile);
             		throw new BTSLBaseException(this, METHODNAME, " EOF is missing or No record found.");
                 }
                 
                 
                 String lastline =Files.lines(Paths.get(inputFile.getAbsolutePath())).skip(numberOfLinesinFile+2).findFirst().get();
                 String lastlineArr[]=lastline.split(",");
                 if(!"EOF".equalsIgnoreCase(lastlineArr[0])){
                	 if (log.isDebugEnabled()) {
                         log.debug(METHODNAME, filen + inputFile + "EOF not Found.");
                     }
                	 String rejectFileName=fail+"Invalid_File_RecordFormat_"+ inputFile.getName();
                     File rejectFile= new File(rejecFile+File.separator+rejectFileName);
                     rename=inputFile.renameTo(rejectFile);
                     throw new BTSLBaseException(this, METHODNAME, " EOF is missing or No record found.");
                 }
                 
                 
                 if( numberOfLinesinFile > Max_No_Records){
                	 if (log.isDebugEnabled()) {
                         log.debug(METHODNAME, filen + inputFile + "Exceeds the Max Records.");
                     }
                	 String rejectFileName=fail+"_Exceeds_MaxRecords_Size"+ inputFile.getName();
                     File rejectFile= new File(rejecFile+File.separator+rejectFileName);
                     rename=inputFile.renameTo(rejectFile);
                     throw new BTSLBaseException(this, METHODNAME, "inputFile Exceeds the Max Records Size ");
                 }
                 
                 
                 setBufferReader();
                 
                 String line=in.readLine();
     			
                 network_code= getNetworkCode(line);
                 
                 if(NetworkCache.getNetworkByExtNetworkCode(network_code)==null) {
                	 if (log.isDebugEnabled()) {
                         log.debug(METHODNAME, "Network Code = " + network_code + " not found in cache.");
                     }
                	 String rejectFileName=fail+"_NetworkCodeinvalid"+ inputFile.getName();
                     File rejectFile= new File(rejecFile+File.separator+rejectFileName);
                     rename=inputFile.renameTo(rejectFile);
                     throw new BTSLBaseException(this, METHODNAME, "Network Code Invalid In File ");
                	 
                 }
                 //Flag will be set to false on processin 2 or more Files in the same directory,as it will be considered as single process.
                 if(validateProcessFlag)
                	 validateProcess(con,network_code);     			
                 
                 validateHeader();
                                  
                 validateAndUpdatelist();               
                 
                 if(!uploadZbFnfList.isEmpty()){
                	 successCount=UpdateDataInDB(con,uploadZbFnfList);
                	 uploadZbFnfList.clear();
                 }
                 if(!deleteZbFnfList.isEmpty()){
                	 deleteCount=DeleteDataInDb(con, deleteZbFnfList);
                	 deleteZbFnfList.clear();
                 }
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
                         throw new BTSLBaseException(this, METHODNAME, "Error Moving File");
                     }
                     messages="File "+ inputFile.getName() +" is processed and moved successfully.";
                     for (int i = 0, len = adm_msisdn.length; i < len; i++) {
                         pushMessage = new PushMessage(adm_msisdn[i], messages, null, null, locale);
                         pushMessage.push();
                     }
                                          
                     //Writing Rejected Records to Csv.
                     
                     if(!rejectedZbFnfList.isEmpty()){
                    	 
                    	 String rejectFilename="Error_"+fileName;
                    	 boolean rejctWriteStatus= writeRejectedRecordstoCsv(rejectedZbFnfList,rejectFilename);
                    	 rejectedZbFnfList.clear();
                    	 if (!rejctWriteStatus) {
                             throw new BTSLBaseException(this, METHODNAME, "Error Writing Rejecting Records.");
                         }
                     }
                     
                     // Commiting the transaction after successful
                     // proccessing of file data.
                     con.commit();
                     
                    
                 } else {
                     throw new BTSLBaseException(this, METHODNAME, "Error Updating Records");
                 }
                 if (log.isDebugEnabled()) {
                     log.debug(METHODNAME, filen + inputFile + " is proccessed");
                 }
                 
                 
             }// end of inner try-block
             catch (BTSLBaseException be) {
            	 if (log.isDebugEnabled()) {
                     log.debug(METHODNAME, filen + inputFile + "Error_General_Processing :BTSL Excep");
                 }
                 
            	 if(null != in)
            		 in.close();
            	 String rejectFileName="Fail_General_ProcessingError_"+ inputFile.getName();
          		 File rejectFile= new File(rejecFile+File.separator+rejectFileName);
             	 if(inputFile.renameTo(rejectFile))
             	 {
               		log.debug(METHODNAME, "File renamed successfully");
               	 }
             	 
             	messages="File "+ inputFile.getName() +" Process Status is Unsuccessful .It is moved to rejected directory.";
                for (int i = 0, len = adm_msisdn.length; i < len; i++) {
                    pushMessage = new PushMessage(adm_msisdn[i], messages, null, null, locale);
                    pushMessage.push();
                }

                if (con != null) {
                     try {
                         con.rollback();
                     } catch (Exception e1) {
                    	 log.error(METHODNAME, "Exception:e=" + e1);
             			log.errorTrace(METHODNAME, e1);
                         
                     }
                 }
                 log.error(METHODNAME, "BTSLBaseException  = " + be.getMessage());
                 log.errorTrace(METHODNAME, be);

                 
             }// end of catch-BTSLBaseException
             catch (Exception e) {
            	 
            	 if(null != in)
            		 in.close();
            	 String rejectFileName="Fail_General_ProcessingError_"+ inputFile.getName();
          		 File rejectFile= new File(rejecFile+File.separator+rejectFileName);
             	 rename =inputFile.renameTo(rejectFile);
             	 
             	messages="File "+ inputFile.getName() +" Process Status is Unsuccessful .It is moved to rejected directory.";
                for (int i = 0, len = adm_msisdn.length; i < len; i++) {
                    pushMessage = new PushMessage(adm_msisdn[i], messages, null, null, locale);
                    pushMessage.push();
                }

                log.error(METHODNAME, "Exception = " + e.getMessage());
                 if (con != null) {
                     try {
                         con.rollback();
                     } catch (Exception e1) {
                         log.errorTrace(METHODNAME, e1);
                     }
                 }
                 log.error(METHODNAME, "Exception e = " + e.getMessage());
                 log.errorTrace(METHODNAME, e);
                 
             } finally {
                 try {
                     if (in != null) {
                         in.close();
                     }
                 } catch (Exception e1) {
                     log.errorTrace(METHODNAME, e1);
                 }
                 if (log.isDebugEnabled()) {
                     log.debug(METHODNAME, "File = " + inputFile + " is proccessed");
                 }
             }// end of inner finally
         }
         
       
         

    	 
     } catch (BTSLBaseException be) {
         log.error(METHODNAME, "BTSLBaseException be= " + be.getMessage());
         
         throw be;
     }// end of catch-BTSLBaseException
     catch (Exception e) {
         log.error(METHODNAME, "Exception be= " + e.getMessage());
         log.errorTrace(METHODNAME, e);
         EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ZB_FNFUpload [process]", "processStatusVO.getProcessID() " +" ZB_FNFUpload", "", "", "Excep " + e.getMessage());
         throw new BTSLBaseException(this, METHODNAME, e.getMessage());
     }// end of catch-Exception
     finally {
         try {
             // Setting the process status as 'C-Complete' if the
             // processStatusOK is true
             if (processStatusOK) {
                 Date date = new Date();
                 processStatusVO.setStartDate(processStatusVO.getStartDate());
                 processStatusVO.setExecutedOn(date);
                 processStatusVO.setExecutedUpto(date);
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
             log.errorTrace(METHODNAME, be);
             log.error(METHODNAME, "BTSLBaseException be= " + be.getMessage());
             
         }// end of catch-BTSLBaseException
         catch (Exception e) {
             log.errorTrace(METHODNAME, e);
             log.error(METHODNAME, "Exception e " + e.getMessage());
            
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ZB_FNFUpload[process]", "processStatusVO.getProcessID()" + ProcessI.ZB_FNF_UPLOAD_PROCESSID, "", "", "BaseException:" + e.getMessage());
             
         }// end of catch-Exception
         
          
        
         if (log.isDebugEnabled()) {
             log.debug(METHODNAME, "Exiting ");
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
    				 if (isExists(con, dataObject.getMsisdn1(), dataObject.getType()))
    				 {
    				 dataObject.setRemarks(" Record Already exists for MSISDN1 "+dataObject.getMsisdn1() + " and type "+ dataObject.getType() + ";");
    				 }
    				 else
    				 {
    					 dataObject.setRemarks(" Record Already exists for MSISDN2  "+dataObject.getMsisdn2() + " and type "+ dataObject.getType() + ";"); 
    				 }
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

private void validateAndUpdatelist() throws BTSLBaseException, ParseException{
	final String METHODNAME = "validateAndUpdatemap";
    if (log.isDebugEnabled()) {
        log.debug(METHODNAME, entered);
    }
	String line="";
	ZBFnFVO dataObject=null;
	String[] arr =new String [max_header_attr];

	try{
		while(!(line=in.readLine()).toUpperCase().startsWith("EOF")){
			 arr=line.split(",");
			 if(isRecordValid(arr)){
				 
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
	}
	catch(IOException io){
		log.error(METHODNAME, "IO Exception = " + io.getMessage());
        log.errorTrace(METHODNAME, io);
        throw new BTSLBaseException(this, "validateAndUpdatemap", io.getMessage());

	} 		
	if (log.isDebugEnabled()) {
        log.debug(METHODNAME, exited);
    }
}


public boolean isRecordValid(String arr[] ) {
	final String METHODNAME = "isRecordValid";
    if (log.isDebugEnabled()) {
        log.debug(METHODNAME, entered);
    }
	Date currentDate= new Date();
	String msisdn1= arr.length>0?arr[0]:null; 
	String msisdn2=arr.length>1?arr[1]:null;
	String type=arr.length>2?arr[2]:null;
	String expiryDate=arr.length>3?arr[3]:null;
	String operation=arr.length>4?arr[4]:null;
	errorReason = new StringBuilder();
	boolean flag =true;
	try{
		if(BTSLUtil.isNullString(msisdn1) ||!BTSLUtil.isNumeric(msisdn1) || (msisdn1.length()!=ZBFNF_MAX_MSISDNLENGTH)){
			errorReason.append(" MSISDN1 is Invalid ;");
			flag= false;
		}
		if(BTSLUtil.isNullString(msisdn2) || !BTSLUtil.isNumeric(msisdn2) || ( msisdn2.length()!=ZBFNF_MAX_MSISDNLENGTH) ){
			errorReason.append(" MSISDN2 is Invalid ;");
			flag= false;
		}
	
		
		
		if(BTSLUtil.isNullString(operation)){
			errorReason.append(" operationcode is null ;");
			flag= false;
		}
		
		if(BTSLUtil.isNullString(type) || !Arrays.asList(supportedTypes).contains(type)){
			errorReason.append(" Rule Type is Invalid ;");
			flag= false;
		}
		
		if(msisdn1.equals(msisdn2)){
			errorReason.append(" MSISDN1 and MSISDN2 Are Same. ;");
			flag= false;
		}
		if(BTSLUtil.isNullString(expiryDate) || !BTSLUtil.getDateFromString(expiryDate, ZBFNF_DateFormat).after(currentDate)){
			errorReason.append(" expiryDate is Invalid ;");
			flag= false;
		}
	
		
	}
	catch(ParseException e){
		errorReason.append(" expiryDate format is Invalid ;");
		flag= false;
	}
	 if (log.isDebugEnabled()) {
	        log.debug(METHODNAME, exited);
	    }
	return flag;
}

private ZBFnFVO prepareVO(String[] arr) throws  BTSLBaseException{
	final String METHODNAME = "prepareVO";
    if (log.isDebugEnabled()) {
        log.debug(METHODNAME, entered);
    }
	ZBFnFVO  dataObject = new ZBFnFVO();
	
	String ZBFNF_Dateformat=Constants.getProperty("ZBFNF_DateFormat");
	try{
	SimpleDateFormat formatter = new SimpleDateFormat(ZBFNF_Dateformat);
	String msisdn1= arr.length>0?arr[0]:null; 
	String msisdn2=arr.length>1?arr[1]:null;
	String type=arr.length>2?arr[2]:null;
	String expiryDate=arr.length>3?arr[3]:null;
	String stringDate=arr.length>3?arr[3]:null;
	String operation=arr.length>4?arr[4]:null;
	dataObject.setMsisdn1(msisdn1);
	dataObject.setMsisdn2(msisdn2);
	dataObject.setType(type);
	dataObject.setOperation(operation);
	dataObject.setDateString(stringDate);
	dataObject.setExpiryDate(BTSLUtil.isNullString(expiryDate)?null:formatter.parse(expiryDate));	
	}catch(ParseException pe){
		dataObject.setExpiryDate(null);
	}catch(Exception e){
		log.error(METHODNAME, "Exception : " + e.getMessage());
    	log.errorTrace(METHODNAME, e);
		throw new BTSLBaseException(this, METHODNAME, "Error preparing Vo");
	}
	if (log.isDebugEnabled()) {
        log.debug(METHODNAME, exited);
    }
	return dataObject;
}

private void validateProcess(Connection con,String networkCode) throws BTSLBaseException, SQLException{  
	String METHODNAME="validateProcess";
	  if (log.isDebugEnabled()) {
	        log.debug(METHODNAME, entered);
	    }
	processBL = new ProcessBL();
    // check the process status by calling checkProcessUnderprocess
    // method of processBL,if its ok continue else stop the process with
    // error message.
    processStatusVO = processBL.checkProcessUnderProcessNetworkWise(con,  ProcessI.ZB_FNF_UPLOAD_PROCESSID, networkCode);

    if (!(processStatusVO != null && processStatusVO.isStatusOkBool())) {
    	
        throw new BTSLBaseException(this, METHODNAME, PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
    }
    processStatusOK = processStatusVO.isStatusOkBool();
    validateProcessFlag=false;
    // Commiting the status of process status as 'U-Under Process'.
    con.commit();

	if (log.isDebugEnabled()) {
        log.debug(METHODNAME, exited);
    }
}


private String getNetworkCode(String line) throws BTSLBaseException {
	final String METHODNAME = "getNetworkCode()";
	if (log.isDebugEnabled()) {
        log.debug(METHODNAME, entered);
    }
	String[] arr=line.split(",");
	if(arr.length > 2)
	{
		 log.debug(METHODNAME,"Error : Invalid network");
		 throw new BTSLBaseException("ZB_FNFUpload", METHODNAME, "ERROR in Reading Network COde" );
	}
	if (log.isDebugEnabled()) {
        log.debug(METHODNAME, exited);
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
    	  
    	  if(!headerArray[0].trim().equalsIgnoreCase(ZBFNF_headers[0]) || !headerArray[1].trim().equalsIgnoreCase(ZBFNF_headers[1])
    			  ||  !headerArray[2].trim().equalsIgnoreCase(ZBFNF_headers[2])
    			  || !headerArray[3].trim().equalsIgnoreCase(ZBFNF_headers[3])
    			  || !headerArray[4].trim().equalsIgnoreCase(ZBFNF_headers[4])){
    		 
    		  in.close();
    		  String rejectFileName="Fail_"+"HeaderInvalid_"+ inputFile.getName();
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
   
    try {
        if (log.isDebugEnabled()) {
            log.debug(METHODNAME, "inputFile = " + inputFile);
        }
       
        in = new BufferedReader(new FileReader(inputFile));
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
		
		rejectedLines.append(ZBFNF_headerString+",REMARKS");
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
			
			rejectedLines.append(dataObject.getDateString());
			
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
		 
		 ZBFNF_File_Name=Constants.getProperty("ZBFNF_File_Name");
        
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
    
    UploadZBFNF upload = new UploadZBFNF();
    
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

private boolean isExists(Connection con,String msisdn, String type) throws BTSLBaseException{
	final String METHODNAME = "isExists";
    if (log.isDebugEnabled()) {
        log.debug(METHODNAME, entered);
    }
	
 	PreparedStatement selectPstmt = null;
 	ResultSet rs=null;
    boolean isExists=false;
    int i = 0;
	StringBuilder selectSql = new StringBuilder("select msisdn1 from fnf_zero_base_customer where msisdn1 = ? and record_type = ?  ");


    if (log.isDebugEnabled()) {
        log.debug(METHODNAME, "Query=" + selectSql);
    }
    
    try{
    	 selectPstmt = con.prepareStatement(selectSql.toString());   		 
    		 try{
    			 i=0;
    			 selectPstmt.setString(++i, msisdn);
    			 selectPstmt.setString(++i, type);
                 
    			 rs=selectPstmt.executeQuery();
    			 if(rs.next())
    			 {
    				 isExists=true;
    			 }
                 
                
    			 
    		 }catch(SQLException sqe){
    			 
                     log.error(METHODNAME, "SQLException= " + sqe.getMessage());
                     log.errorTrace(METHODNAME, sqe);
    			 }
    			
    		 
    		 
         
    }catch(Exception e){
    	 log.error(METHODNAME, "Exception=" + e.getMessage());
         log.errorTrace(METHODNAME, e);
         throw new BTSLBaseException(this,METHODNAME, "error.general.processing");
    }finally {
        if (log.isDebugEnabled()) {
            log.debug("isExists", "is Exists" + isExists);
        }
        try{
    		if (rs!= null){
    			rs.close();
    		}
    	}
    	catch (SQLException e){
    		log.error("An error occurred closing result set.", e);
    	}
        OracleUtil.closeQuietly(selectPstmt);  
    }// end of finally
   

	return isExists;
}




}

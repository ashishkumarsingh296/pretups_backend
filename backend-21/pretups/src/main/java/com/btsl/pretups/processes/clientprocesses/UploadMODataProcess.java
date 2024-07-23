package com.btsl.pretups.processes.clientprocesses;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
import java.text.MessageFormat;

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
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;


public class UploadMODataProcess {

	private static Log log = LogFactory.getLog(UploadMODataProcess.class.getName());


	private String entered="Entered";
	private String exited="Exited";
	private String fail="Fail_";
	private String filen="File=";

	private String filePath;
	private BufferedReader in;
	private File inputFile;
	private String fileExt;
	private File destFile;
	private String moveLocation;
	private File rejecFile;
	private String rejectedFileLocation;
	private ArrayList fileList;
	private ArrayList<MODataVO> uploadMOList= new ArrayList<MODataVO>();
	private ArrayList<MODataVO> rejectedMOList= new ArrayList<MODataVO>();
	private int data_count_per_row = 13;
	private long numberOfLinesinFile=0;
	private String creationDate;
	private int Max_No_Records;
	private static String MO_DATA_SEPARATOR = ",";
	private ProcessStatusVO processStatusVO = null;
	private ProcessBL processBL = null;
	private boolean processStatusOK = false;	
	private boolean validateProcessFlag=true;	
	private  String allowedExt[] = null;	
	private String _msisdnString = null;	
	private String networkCode = null;
	private int headerLineCount = 1;
	private int footerLineCount = 1;
	private String moFileName = "";
	private String errorReason = null;

	private void process() throws BTSLBaseException  {
		
		final String METHODNAME = "process";
		if (log.isDebugEnabled()) {
			log.debug(METHODNAME, entered);
		}
		int successCount = 0;
		int deleteCount=0;
		boolean rename;
		String messages=null;
		Locale locale = new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));

		try(Connection con = OracleUtil.getSingleConnection()){

			if (log.isDebugEnabled()) {
				log.debug(METHODNAME, "START :: TOTAL MEMORY --->" + Runtime.getRuntime().totalMemory() + " START :: FREE MEMORY --->" + Runtime.getRuntime().freeMemory());
			}
			_msisdnString = new String(Constants.getProperty("adminmobile"));

			if (log.isDebugEnabled())
				log.debug(METHODNAME, "_msisdnString: " + _msisdnString);

			String[] adm_msisdn = _msisdnString.split(",");
			PushMessage pushMessage = null;

			if (con == null) {
				throw new BTSLBaseException(this, METHODNAME, "DB COnnection Null");
			}
			loadConstantValues();
			loadFilesFromDir();
			validateProcess(con,networkCode);
			for (int l = 0, size = fileList.size(); l < size; l++) {
				inputFile = (File) fileList.get(l);
				try {
					errorReason = "";
					rejectedMOList.clear();
					uploadMOList.clear();
					moFileName = inputFile.getName();
					if (!inputFile.exists()) {
						if (log.isDebugEnabled()) {
							log.debug(METHODNAME, filen + inputFile + "does not exists.");
						}
						throw new BTSLBaseException(this, METHODNAME, "EX: Input File Does not Exist");
					}

					if(inputFile.length()==0){
						if (log.isDebugEnabled()) {
							log.debug(METHODNAME, filen + inputFile + "is empty");
						}
						String rejectFileName=fail+"_FileEmpty"+ inputFile.getName();
						File rejectFile= new File(rejecFile+File.separator+rejectFileName);
						rename =inputFile.renameTo(rejectFile);
						throw new BTSLBaseException(this, METHODNAME, "EX: Input File Empty");
					}

					boolean extFlag = false;					
					for(int c=0;c<allowedExt.length;c++){
						if(inputFile.getName().endsWith(allowedExt[c]))
						{
							extFlag = true;
							break;
						}							
					}
					if(!extFlag){
						if (log.isDebugEnabled()) {
							log.debug(METHODNAME, filen + inputFile + "not in required format: " + Arrays.toString(allowedExt));
						}
						String rejectFileName=fail+"invalid_FileFormat"+ inputFile.getName();
						File rejectFile= new File(rejecFile+File.separator+rejectFileName);
						rename=inputFile.renameTo(rejectFile);
						throw new BTSLBaseException(this, METHODNAME,  "EX: File not in required format: " + Arrays.toString(allowedExt));
					}

					setBufferReader();
					readHeader(inputFile);

					if(numberOfLinesinFile == 0 ){
						if (log.isDebugEnabled()) {
							log.debug(METHODNAME, filen + inputFile + " EOF is missing or No record found.");
						}
						String rejectFileName=fail+"Invalid_File_RecordFormat_"+ inputFile.getName();
						File rejectFile= new File(rejecFile+File.separator+rejectFileName);
						rename=inputFile.renameTo(rejectFile);
						throw new BTSLBaseException(this, METHODNAME, "EX: EOF is missing or No record found.");
					}

					if( numberOfLinesinFile > Max_No_Records){
						if (log.isDebugEnabled()) {
							log.debug(METHODNAME, filen + inputFile + "Exceeds the Max Records.");
						}
						String rejectFileName=fail+"_Exceeds_MaxRecords_Size"+ inputFile.getName();
						File rejectFile= new File(rejecFile+File.separator+rejectFileName);
						rename=inputFile.renameTo(rejectFile);
						throw new BTSLBaseException(this, METHODNAME, "EX: inputFile Exceeds the Max Records Size ");
					}
					
					validateAndUpdatelist();               

					if(!uploadMOList.isEmpty()){
						successCount=UpdateDataInDB(con,uploadMOList);
						uploadMOList.clear();
					}

					if (successCount >= 0) {
						if (in != null) {
							in.close();
						}
						// Moving File after Processing
						Date date = new Date();
						String fileName = inputFile.getName();
						String moveFileName = fileName.substring(0, fileName.indexOf('.')) + "_" + date.getDate() + "_" + date.getHours() + "_" + date.getMinutes() + "." + fileName.substring(fileName.indexOf('.') + 1);
						boolean fileMoved = moveFile(inputFile.getPath(), moveFileName);
						if (!fileMoved) {
							throw new BTSLBaseException(this, METHODNAME, "EX: Error Moving File");
						}
						messages="File "+ inputFile.getName() +" is processed and moved successfully.";
						for (int i = 0, len = adm_msisdn.length; i < len; i++) {
							pushMessage = new PushMessage(adm_msisdn[i], messages, null, null, locale);
							pushMessage.push();
						}

						//Writing Rejected Records to Csv.

						if(!rejectedMOList.isEmpty()){

							String rejectFilename="Error_"+fileName;
							boolean rejctWriteStatus= writeRejectedRecordstoCsv(rejectedMOList,rejectFilename);
						/*	sendMailNotification(rejectedMOList);
							rejectedMOList.clear();*/
							if (!rejctWriteStatus) {
								throw new BTSLBaseException(this, METHODNAME, "EX: Error Writing Rejecting Records.");
							}
						}
						con.commit();
					} else {
						throw new BTSLBaseException(this, METHODNAME, "Error Updating Records");
					}
					if (log.isDebugEnabled()) {
						log.debug(METHODNAME, filen + inputFile + " is proccessed");
					}
				}// end of inner try-block
				catch (BTSLBaseException be) {
					errorReason = be.getMessage();
					if (log.isDebugEnabled()) {
						log.debug(METHODNAME, filen + inputFile + "Error_General_Processing :BTSL Exception :: " + errorReason );
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
					errorReason = e.getMessage();
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

					log.error(METHODNAME, "Exception = " + e.getMessage() + errorReason);
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
					if(!rejectedMOList.isEmpty() || !BTSLUtil.isNullString(errorReason)) {
						sendMailNotification(rejectedMOList,false);
						rejectedMOList.clear();
					}
					/*else if(!uploadMOList.isEmpty()){
						sendMailNotification(uploadMOList,true);
						uploadMOList.clear();
					}*/
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
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UploadMODataProcess [process]", "processStatusVO.getProcessID() " +" UploadMODataProcess", "", "", "Excep " + e.getMessage());
			throw new BTSLBaseException(this, METHODNAME, e.getMessage());
		}// end of catch-Exception
		finally {
			try {
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
						EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UploadMODataProcess[process]", "processStatusVO.getProcessID()" + ProcessI.MO_UPLOAD_PROCESSID, "", "", "Error while updating the process status after completing the process");
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

				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UploadMODataProcess[process]", "processStatusVO.getProcessID()" + ProcessI.MO_UPLOAD_PROCESSID, "", "", "BaseException:" + e.getMessage());

			}// end of catch-Exception
			if (log.isDebugEnabled()) {
				log.debug(METHODNAME, "Exiting ");
			}
		}
	}

	private int UpdateDataInDB(Connection con,ArrayList<MODataVO> uploadList) throws BTSLBaseException{
		final String METHODNAME = "UpdateDataInDB";
		if (log.isDebugEnabled()) {
			log.debug(METHODNAME, entered);
		}
		PreparedStatement insertPstmt = null;
		int totRecords = 0;
		int queryResult=-1;
		int recordAdded = 0;
		int i = 0;
		StringBuilder insertSql = new StringBuilder(" INSERT INTO MO_SO_NUMBER(DONUMBER,FILEREFERENCE,FROMSERIAL_NUMBER, ");
		insertSql.append(" ITEMCODE,MOLINENUMBER,MONUMBER,ORGCODE,CREATION_DATE,QUANTITY,SUBINVENTORY,TOSERIAL_NUMBER,UOM,WMSREFERENCE,CREATED_ON) ");		
		insertSql.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

		if (log.isDebugEnabled()) {
			log.debug(METHODNAME, "Query=" + insertSql);
		}
		try{
			insertPstmt = con.prepareStatement(insertSql.toString());	
			for(MODataVO dataObject:uploadList){
				try{
					i=0;				
					insertPstmt.setString(++i, dataObject.getDoNumber());
					insertPstmt.setString(++i, dataObject.getFileRef());
					insertPstmt.setLong(++i, dataObject.getFromSerialNumber());
					insertPstmt.setString(++i, dataObject.getItemCode());
					insertPstmt.setString(++i, dataObject.getMoLineNumber());
					insertPstmt.setLong(++i, dataObject.getMoNumber());
					insertPstmt.setString(++i, dataObject.getOrgCode());
					insertPstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(dataObject.getCreationDate()));
					insertPstmt.setLong(++i, dataObject.getQuantity());				
					insertPstmt.setString(++i, dataObject.getSubInventory());
					insertPstmt.setLong(++i, dataObject.getToSerialNumber());
					insertPstmt.setString(++i, dataObject.getUom());
					insertPstmt.setString(++i, dataObject.getWmsRef());
					insertPstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(new Date()));
										
					if (isExists(con,Long.toString(dataObject.getMoNumber()),Long.toString(dataObject.getFromSerialNumber()), Long.toString(dataObject.getToSerialNumber())))
					{
						dataObject.setRemarks(" Record exists of either from Serial Number="+dataObject.getFromSerialNumber() + " OR To Serial Number="+ dataObject.getToSerialNumber() + ";");
						rejectedMOList.add(dataObject);
						log.error(METHODNAME, dataObject.getRemarks());
					}else {
						queryResult = insertPstmt.executeUpdate();
						if (queryResult <= 0) {
							log.error(METHODNAME, "Error While inserting records.queryResult<0");
						} else {
							recordAdded += queryResult;
							totRecords++; // Total records count to be inserted or
						}
					}
					insertPstmt.clearParameters();
				}catch(SQLException sqe){
					if (sqe.getErrorCode() == 00001) {
						if (isExists(con,Long.toString(dataObject.getMoNumber()),Long.toString(dataObject.getFromSerialNumber()), Long.toString(dataObject.getToSerialNumber())))
						{
							dataObject.setRemarks(" Record Already exists from Serial Number="+dataObject.getFromSerialNumber() + " To Serial Number="+ dataObject.getToSerialNumber() + " against Mo Number="+dataObject.getMoNumber()+";");
						}
						rejectedMOList.add(dataObject);
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
			if (log.isDebugEnabled()) {
				log.debug("insertData", "p_userID:" + PretupsI.SYSTEM_USER + "NetworkCode:"+networkCode+" Processed=" + " ,No of records=" + totRecords);
			}
			try {
				if (insertPstmt != null) {
					insertPstmt.close();
				}
			} catch (Exception ex) {
				log.errorTrace(METHODNAME, ex);
			}
		}
		return recordAdded;
	}

	private void validateAndUpdatelist() throws BTSLBaseException, ParseException{
		final String METHODNAME = "validateAndUpdatelist";
		if (log.isDebugEnabled()) {
			log.debug(METHODNAME, entered);
		}
		String line="";
		MODataVO dataObject=null;
		String[] arr =new String [data_count_per_row];
		int recordCount = 0;
		try{
			while(recordCount<(numberOfLinesinFile+headerLineCount)){
				if(recordCount<headerLineCount){
					recordCount++;
					continue;
				}
				line=in.readLine();
				recordCount++;
				line=line.substring(line.indexOf(MO_DATA_SEPARATOR)+1); //ignore/skip first parameter
				arr=line.split(MO_DATA_SEPARATOR);
				if(isRecordValid(arr)){	
					dataObject= prepareVO(arr);
					dataObject.setFileRef(dataObject.getFileRef() + "#" + moFileName);
					uploadMOList.add(dataObject);
				}
			}
		}
		catch(Exception eio){
			log.error(METHODNAME, "IO Exception = " + eio.getMessage());
			log.errorTrace(METHODNAME, eio);
			throw new BTSLBaseException(this, "validateAndUpdatelist", eio.getMessage());
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
		boolean flag =true;
		if(!arr[7].startsWith("VO")) {
			flag = false;
		}
		if (log.isDebugEnabled()) {
			log.debug(METHODNAME, exited);
		}
		return flag;
	}

	private MODataVO prepareVO(String[] arr) throws  BTSLBaseException{
		final String METHODNAME = "prepareVO";
		if (log.isDebugEnabled()) {
			log.debug(METHODNAME, entered);
		}
		MODataVO  dataObject = new MODataVO();

		try{
			dataObject.setFileRef(arr[0]);
			dataObject.setMoNumber(Long.parseLong(arr[1]));
			dataObject.setMoLineNumber(arr[2]);
			dataObject.setDoNumber(arr[3]);
			dataObject.setWmsRef(arr[4]);
			dataObject.setOrgCode(arr[5]);
			dataObject.setSubInventory(arr[6]);
			dataObject.setItemCode(arr[7]);
			dataObject.setQuantity(Long.parseLong(arr[8]));
			dataObject.setUom(arr[9]);
			dataObject.setFromSerialNumber(Long.parseLong(arr[10]));
			dataObject.setToSerialNumber(Long.parseLong(arr[11]));	
			SimpleDateFormat sdf = null;
			Date dt = null;
			sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");		
			try {
				dt = sdf.parse(creationDate);				
				
			} catch (ParseException e) {
				log.errorTrace(METHODNAME, e);
			}
			dataObject.setCreationDate(dt);
			/*if(!arr[7].startsWith("VO")) {
				dataObject.setItemCode(arr[7]);
				dataObject.setRemarks(":: Item code doesn't start with VO. ");
				log.debug(METHODNAME, "########## Item code doesn't start with VO ##########");
				throw new BTSLBaseException("Item code doesn't start with VO");
			}*/
		}
		catch(Exception e){
			log.error(METHODNAME, "Exception : " + e.getMessage());
			log.errorTrace(METHODNAME, e);
//			throw new BTSLBaseException(this, METHODNAME, "Error preparing Vo");
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
		processStatusVO = processBL.checkProcessUnderProcessNetworkWise(con,  ProcessI.MO_UPLOAD_PROCESSID, networkCode);

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
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "UploadMODataProcess[loadFilesFromDir]", "", "", "", "No file exists at the location of file upload");
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
			EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "UploadMODataProcess[loadFilesFromDir]", "", "", "", "Exception:" + e.getMessage());
			throw new BTSLBaseException(this, METHODNAME, e.getMessage());
		}// end of catch-Exception
		if (log.isDebugEnabled()) {
			log.debug(METHODNAME, exited);
		}
	}// end of loadFilesFromDir

	private boolean writeRejectedRecordstoCsv(ArrayList<MODataVO> rejectedList,String rejectFileName) throws IOException{
		String METHODNAME="writeRejectedRecordstoCsv";
		if (log.isDebugEnabled()) {
			log.debug(METHODNAME, "Entering");
		}
		File rejectFile= new File(rejecFile+File.separator+rejectFileName);
		try(FileOutputStream fout= new FileOutputStream(rejectFile);BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fout));)
		{		
			StringBuilder rejectedLines= new StringBuilder();
			String networkDetails="NetworkCode:"+networkCode;
			rejectedLines.append(networkDetails);

			bw.write(rejectedLines.toString());
			bw.newLine();
			
			for(MODataVO dataObject:rejectedList){
				rejectedLines= new StringBuilder();
				rejectedLines.append(dataObject.getFileRef() + MO_DATA_SEPARATOR);			
				rejectedLines.append(dataObject.getMoNumber() +  MO_DATA_SEPARATOR);				
				rejectedLines.append(dataObject.getMoLineNumber() +  MO_DATA_SEPARATOR);				
				rejectedLines.append(dataObject.getDoNumber() +  MO_DATA_SEPARATOR);				
				rejectedLines.append(dataObject.getWmsRef() +  MO_DATA_SEPARATOR);				
				rejectedLines.append(dataObject.getOrgCode() +  MO_DATA_SEPARATOR);
				rejectedLines.append(dataObject.getSubInventory() +  MO_DATA_SEPARATOR);
				rejectedLines.append(dataObject.getItemCode() +  MO_DATA_SEPARATOR);
				rejectedLines.append(dataObject.getQuantity() +  MO_DATA_SEPARATOR);
				rejectedLines.append(dataObject.getUom() +  MO_DATA_SEPARATOR);
				rejectedLines.append(dataObject.getFromSerialNumber() +  MO_DATA_SEPARATOR);
				rejectedLines.append(dataObject.getToSerialNumber() +  MO_DATA_SEPARATOR);
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
	}



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
			networkCode = Constants.getProperty("NETWORK_CODE");	

			filePath = BTSLUtil.NullToString(Constants.getProperty("MO_FILE_PATH")).trim();
			if (!(new File(filePath).exists())) {
				log.error(METHODNAME, "Directory Path = " + filePath + " does not exists");
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "UploadMODataProcess [loadConstantValues]", "", "", "", "Configuration Problem, the location for where the file will be read is not correct");
				throw new BTSLBaseException(this, METHODNAME, "Input File Directory Does not exist.");
			}

			moveLocation = BTSLUtil.NullToString(Constants.getProperty("MO_MOVE_LOCATION")).trim();
			destFile = new File(moveLocation);
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
			rejectedFileLocation =  BTSLUtil.NullToString(Constants.getProperty("MO_REJECTEDRECORDS_LOCATION")).trim();
			rejecFile=new File(rejectedFileLocation);
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

			allowedExt=Constants.getProperty("MO_EXTNSN_SUPRT").split(",");

			Max_No_Records= Integer.parseInt(Constants.getProperty("MO_MAX_No_Records"));

			data_count_per_row = Integer.parseInt(Constants.getProperty("DATA_COUNT_PER_RECORD"));

			MO_DATA_SEPARATOR = Constants.getProperty("MO_DATA_SEPARATOR");
			
			headerLineCount = Integer.parseInt(Constants.getProperty("MO_HEADER_LINE_COUNT"));
			
			footerLineCount = Integer.parseInt(Constants.getProperty("MO_FOOTER_LINE_COUNT"));

		}
		catch (BTSLBaseException be) {
			log.error(METHODNAME, "BTSLBaseException be = " + be.getMessage());
			throw be;
		}// end of catch-BTSLBaseException
		catch (Exception e) {
			log.errorTrace(METHODNAME, e);
			log.error(METHODNAME, "Exception e= " + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UploadMODataProcess[loadConstantValues]", "", "", "", "Exception:" + e.getMessage());
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

				log.error("UploadMODataProcess[loadCachesAndLogFiles.]", " Constants file not found on location:: " + constantsFile.toString());
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "UploadMODataProcess [loadCachesAndLogFiles]", "", "", "", " The Constants file doesn't exists at the path specified. ");
				throw new BTSLBaseException("UploadMODataProcess", " loadCachesAndLogFiles ","Missing COnstant file.");
			}

			logconfigFile = new File(p_arg2);
			if (!logconfigFile.exists()) {

				log.error("UploadMODataProcess[loadCachesAndLogFiles:]", " ProcessLogConfig file not found on location:: " + logconfigFile.toString());
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UploadMODataProcess[ loadCachesAndLogFiles]", "", "", "", " The ProcessLogConfig file doesn't exists  at the path specified. ");
				throw new BTSLBaseException("UploadMODataProcess.", "loadCachesAndLogFiles ", "Missing Log File");
			}
			ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());

		} catch (BTSLBaseException be) {
			log.error("UploadMODataProcess[loadCachesAndLogFile]", "BTSLBaseException =" + be.getMessage());
			log.errorTrace(METHODNAME, be);
			throw be;
		}// end of BTSLBaseException
		catch (Exception e) {
			log.errorTrace(METHODNAME, e);
			log.error("UploadMODataProcess[loadCachesAndLogFiles ]", " Exception =" + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UploadMODataProcess[loadCachesAndLogFiles]", "", "", "", "Exception=" + e.getMessage());
			throw new BTSLBaseException("UploadMODataProcess ", " loadCachesAndLogFiles ", "Error");
		}// end of Exception
		finally {
			if (logconfigFile != null) {
				logconfigFile = null;
			}
			if (constantsFile != null) {
				constantsFile = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("UploadMODataProcess[loadCachesAndLogFiles]", " Exiting..........");
			}
		}// end of finally
	}
	public static void main(String[] args) {

		final String METHODNAME = "main";
		if (log.isDebugEnabled()) {
			log.debug(METHODNAME, "Entered main ");
		}

		UploadMODataProcess upload = new UploadMODataProcess();

		try {
			if (args.length != 2) {
				log.error(METHODNAME, " Usage : UploadMODataProcess [Constants file] [LogConfig file]");
				EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "UploadMODataProcess[main]", "", "", "", "Improper usage. Usage : UploadMODataProcess [Constants file] [ProcessLogConfig file]");
				throw new BTSLBaseException("UploadMODataProcess ", " main ", "Missing Initial files.");
			}

			loadCachesAndLogFiles(args[0], args[1]);
			upload.process();

		} catch (BTSLBaseException be) {
			log.error("UploadMODataProcess main()", "BTSLBaseException be=" + be.getMessage());
			log.errorTrace(METHODNAME, be);
		} catch (Exception e) {
			log.error("UploadMODataProcess main()", " Exception e= " + e.getMessage());
			log.errorTrace(METHODNAME, e);
		} finally {
			ConfigServlet.destroyProcessCache();
			if (log.isDebugEnabled()) {
				log.debug(" UploadMODataProcess ", "Exiting main ");
			}
		}
	}

	private boolean isExists(Connection con,String p_moRefId,String p_fSerialNo, String p_tSerialNo) throws BTSLBaseException{
		final String METHODNAME = "isExists";
		if (log.isDebugEnabled()) {
			log.debug(METHODNAME, entered);
		}
		PreparedStatement selectPstmt = null;
		ResultSet rs=null;
		boolean isExists=false;
		int i = 0;
		//StringBuilder selectSql = new StringBuilder("SELECT SOSMOREFID,FROMSERIAL_NUMBER,TOSERIAL_NUMBER FROM MO_SO_NUMBER WHERE SOSMOREFID = ? AND FROMSERIAL_NUMBER = ? AND TOSERIAL_NUMBER = ? ");
		StringBuilder selectSql = new StringBuilder("SELECT MONUMBER,FROMSERIAL_NUMBER,TOSERIAL_NUMBER FROM MO_SO_NUMBER WHERE FROMSERIAL_NUMBER = ? OR TOSERIAL_NUMBER = ? ");
		if (log.isDebugEnabled()) {
			log.debug(METHODNAME, "Query=" + selectSql);
		}
		try{
			selectPstmt = con.prepareStatement(selectSql.toString());   		 
			try{
				i=0;
//				selectPstmt.setString(++i, p_moRefId);
				selectPstmt.setString(++i, p_fSerialNo);
				selectPstmt.setString(++i, p_tSerialNo);

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
			OracleUtil.closeQuietly(rs);
			OracleUtil.closeQuietly(selectPstmt);  
		}// end of finally
		return isExists;
	}


	private void readHeader(File p_file) throws BTSLBaseException{
		final String METHODNAME = "readHeader";
		if (log.isDebugEnabled()) {
			log.debug(METHODNAME, entered);
		}
		boolean rename;
		String str = "";
		String headerArray[]=null;
		try{
			str = in.readLine();
			headerArray= str.split(MO_DATA_SEPARATOR);
			if(headerArray.length!=4){
				in.close();
				String rejectFileName="Fail_"+"HeaderInvalid_"+ inputFile.getName();
				File rejectFile= new File(rejecFile+File.separator+rejectFileName);
				if(inputFile.renameTo(rejectFile))
				{
					log.debug(METHODNAME, "File renamed successfully");
				}
				throw new BTSLBaseException(this, METHODNAME,"Header is not in proper Format.");
			}
			else{
				creationDate = headerArray[2]; 
				numberOfLinesinFile = Long.parseLong(headerArray[3]);
			}
		}catch(Exception e){
			log.error(METHODNAME, "Header Error = " + e.getMessage());
			log.errorTrace(METHODNAME, e);
			throw new BTSLBaseException(this, METHODNAME, e.getMessage());
		}

		if (log.isDebugEnabled()) {
			log.debug(METHODNAME, "Exiting");
		}  
	}
	
	
	private void sendMailNotification(ArrayList p_List,boolean isSuccess) throws BTSLBaseException{
		final String METHODNAME = "sendMailNotification";
		if (log.isDebugEnabled()) {
			log.debug(METHODNAME, entered + " p_rejected: " + p_List.size() + " isSuccess: " + isSuccess);
		}
		
		 final boolean isAttachment = false;
         final String pathofFile = "";
         final String fileNameTobeDisplayed = "";
         final String cc = "";         
         String to = "";
         final String bcc = "";
         String subject = "";
         String message = "";
         String from = "";
         StringBuffer messageBuffer = new StringBuffer();
         String delimeter = " ";
		
		try{			
				to = Constants.getProperty("MO_FAILURE_EMAIL_LIST");					
				from = Constants.getProperty("MO_NOTIFICATION_FROM");
				subject = Constants.getProperty("MO_NOTIFICATION_SUBJECT");			
			//	delimeter = Constants.getProperty("MO_DATA_DELIMETER");
				if(isSuccess)
					messageBuffer.append(MessageFormat.format(Constants.getProperty("MO_NOTIFICATION_SUCCESS"),moFileName));			
				else
					messageBuffer.append(MessageFormat.format(Constants.getProperty("MO_NOTIFICATION_CONTENT"),moFileName));			
			
			if(p_List!=null && p_List.size()>0){
				for(int recordCount=0;recordCount<p_List.size();recordCount++){
					MODataVO dataVO = (MODataVO)p_List.get(recordCount);
					messageBuffer.append(dataVO.getFileRef()+delimeter+dataVO.getMoNumber()+delimeter+dataVO.getMoLineNumber()+delimeter);
					messageBuffer.append(dataVO.getDoNumber()+delimeter+dataVO.getWmsRef()+delimeter+dataVO.getOrgCode()+delimeter);
					messageBuffer.append(dataVO.getSubInventory()+delimeter+dataVO.getItemCode()+delimeter+dataVO.getQuantity()+delimeter);
					messageBuffer.append(dataVO.getUom()+delimeter+dataVO.getFromSerialNumber()+delimeter+dataVO.getToSerialNumber()+delimeter+dataVO.getCreationDate());
					if(!isSuccess)
						messageBuffer.append("Failed"+delimeter+dataVO.getRemarks());	
					messageBuffer.append("<br>");
				}
				message = messageBuffer.toString();
				log.debug(METHODNAME, message);
				EMailSender.sendMail(to, from, bcc, cc, subject, message, isAttachment, pathofFile, fileNameTobeDisplayed);
			}else {
				if(!BTSLUtil.isNullString(errorReason)) {
					if(errorReason.trim().startsWith("EX:"))
						messageBuffer.append("<br> Reason: " + errorReason.substring(errorReason.indexOf(":")+1));
					else
						messageBuffer.append("<br> File not processed");
				}
				messageBuffer.append("<br>");
				message = messageBuffer.toString();
				log.debug(METHODNAME, message);
				EMailSender.sendMail(to, from, bcc, cc, subject, message, isAttachment, pathofFile, fileNameTobeDisplayed);
			}
		}catch(Exception e){
			log.error(METHODNAME, "Header Error = " + e.getMessage());
			log.errorTrace(METHODNAME, e);
			throw new BTSLBaseException(this, METHODNAME, e.getMessage());
		}

		if (log.isDebugEnabled()) {
			log.debug(METHODNAME, "Exiting");
		}  
	}
	
}

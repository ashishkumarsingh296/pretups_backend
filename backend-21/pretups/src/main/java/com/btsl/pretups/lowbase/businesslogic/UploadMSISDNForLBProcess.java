package com.btsl.pretups.lowbase.businesslogic;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;

public class UploadMSISDNForLBProcess{
	private static Log log = LogFactory.getLog(UploadMSISDNForLBProcess.class.getName());
	private static String uploadMSISDNForLBProcess="UploadMSISDNForLBProcess";
	private String _msisdnString = null;
	
	public static void main(String[] args) 
	{
		final String methodName = "main";
		
		try{

			if(args.length!=2)
			{
				log .info(methodName, "uploadMSISDNForLBProcess :: Not sufficient arguments, please pass Constants.props LogConfig.props Locale ");
				return;
			}
			File constantsFile = Constants.validateFilePath(args[0]);
			if(!constantsFile.exists())
			{
				log.debug(methodName, uploadMSISDNForLBProcess+" Constants File Not Found at the path : "+args[0]);
				return;
			}
			File logconfigFile = Constants.validateFilePath(args[1]);
			if(!logconfigFile.exists())
			{
				log.debug(methodName, uploadMSISDNForLBProcess+" ProcessLogConfig File Not Found at the path : "+args[1]);
				return;
			}
			ConfigServlet.loadProcessCache(constantsFile.toString(),logconfigFile.toString());
			UploadMSISDNForLBProcess uploadMSISDNForLBProcess = new UploadMSISDNForLBProcess();
			uploadMSISDNForLBProcess.process();
		}catch (Exception e)
		{
			log.error(methodName,"Main: Error in main method.."+ e.getMessage());
			log.errorTrace(methodName,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "UploadMSISDNForLBProcess[main]", "", "", "", " Exception in main method"+ e.getMessage());
			ConfigServlet.destroyProcessCache();
			return;
		}
		finally
		{
			ConfigServlet.destroyProcessCache();
			log.debug(methodName,"Lowbase upload process completed.");
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "UploadMSISDNForLBProcess[main]", "", "", "", " Lowbase upload process completed.");
		}

	}


	public  void process() throws BTSLBaseException {
		final String methodName = "process";
		ProcessStatusVO processStatusVOForSelectedNetwork = null;
		boolean validFile=false;
		String lbUploadFiledDir = null;
		String lbUploadSuccessDir = null;
		String lbDir = null;
		BufferedReader br=null;
		String messages=null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
		
		
		if(log.isDebugEnabled()) {
			log.debug(methodName,"Entered");
		}
		try
		{   
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			_msisdnString = new String(Constants.getProperty("adminmobile"));
			
            if (log.isDebugEnabled())
            	log.debug(methodName, "_msisdnString: " + _msisdnString);
            
            String[] adm_msisdn = _msisdnString.split(",");
            PushMessage pushMessage = null;

			lbDir = Constants.getProperty("LOW_BASE_DIR");

			lbUploadFiledDir = Constants.getProperty("LOW_BASE_FAIL");
			lbUploadSuccessDir = Constants.getProperty("LOW_BASE_MOVED");


			if(BTSLUtil.isNullString(lbDir)|| lbDir.isEmpty())
			{
				log.debug(methodName,"Lowbase file path  is not defined in Constants.props");
				throw new BTSLBaseException(uploadMSISDNForLBProcess,methodName,PretupsErrorCodesI.LB_CONFIGUARATION_ERROR);
			}
			if(BTSLUtil.isNullString(lbUploadFiledDir)) {
				log.debug(methodName,"Path for uploading failed records for Low Base is not defined in Constants.props");
				throw new BTSLBaseException(uploadMSISDNForLBProcess,methodName,PretupsErrorCodesI.LB_CONFIGUARATION_ERROR);
			}
			if(BTSLUtil.isNullString(lbUploadSuccessDir)) {
				log.debug(methodName,"Path for uploading succesfull records for Low Base is not defined in Constants.props");
				throw new BTSLBaseException(uploadMSISDNForLBProcess,methodName,PretupsErrorCodesI.LB_CONFIGUARATION_ERROR);
			}
			File failDir = new File(lbUploadFiledDir);
			File successDir = new File(lbUploadSuccessDir);
			if(! failDir.isDirectory())
				failDir.mkdir();	
			if(! successDir.isDirectory())
				successDir.mkdir();
			List<String> files =Arrays.asList(new File(lbDir).list());
			for(String file : files){
				File fileObject= new File(lbDir+File.separator+file);

				try(FileReader fr=new FileReader(fileObject))
				{
					long recordsCountInFile=0;
					int validRecordCount=0;

					ArrayList <String> invalidRecordsList=new ArrayList<String>();


					if (fileObject.length() == 0) {

						log.debug(methodName, "File is empty " + fileObject);
						EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UploadMSISDNForLBProcess[process]","","","","file is empty : "+fileObject);
						throw new BTSLBaseException(uploadMSISDNForLBProcess,methodName,PretupsErrorCodesI.LB_FILE_ERROR);
					}
					if(!fileObject.getName().endsWith(".csv")){

						log.debug(methodName,"Not a CSV file : "+fileObject);
						EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UploadMSISDNForLBProcess[process]","","","","file is not a csv file : "+fileObject);
						throw new BTSLBaseException(uploadMSISDNForLBProcess,methodName,PretupsErrorCodesI.LB_FILE_ERROR);

					}
					String pattern = "^[a-zA-Z0-9_.]*$";
					if(!fileObject.getName().startsWith(Constants.getProperty("LOW_BASE_FILENAME")) || !fileObject.getName().matches(pattern))
					{
						log.debug(methodName,"Not a proper file name : "+fileObject);
						EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UploadMSISDNForLBProcess[process]","","","","file is not having a proper name : "+fileObject);
						throw new BTSLBaseException(uploadMSISDNForLBProcess,methodName,PretupsErrorCodesI.LB_FILE_ERROR);
					}
					//Counting the number of lines in file.it is a java8 feauture.It wont work in java version below java 8.

					recordsCountInFile= Files.lines(Paths.get(fileObject.getAbsolutePath())).skip(3).count();
					
					if(recordsCountInFile == 0)
					{
						log.debug(methodName,"File record format is invalid or no records found to upload : "+fileObject);
						EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UploadMSISDNForLBProcess[process]","","","","File record format is invalid or no records found to upload. "+fileObject);
						throw new BTSLBaseException(uploadMSISDNForLBProcess,methodName,PretupsErrorCodesI.LB_FILE_ERROR);
					}

					String lastline=Files.lines(Paths.get(fileObject.getAbsolutePath())).skip(recordsCountInFile+2).findFirst().get();
					String[] array=lastline.split(",");
					if(!(array.length == 1 && array[0].equals("EOF"))){
						if (log.isDebugEnabled()) {
							log.debug("process", "File = " + fileObject + "EOF is missing");
						}
						throw new BTSLBaseException(uploadMSISDNForLBProcess,methodName,PretupsErrorCodesI.LB_FILE_ERROR);
					}

					if(recordsCountInFile > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LB_SYSTEMLEVEL_LIMIT))).intValue())
					{
						if (log.isDebugEnabled()) {
							log.debug("process", "File = " + fileObject + "The number of records in the file exceeds the system limit for upload.");
						}			
						throw new BTSLBaseException(uploadMSISDNForLBProcess,methodName,PretupsErrorCodesI.LB_FILE_ERROR);
					}

					Set<LowBasedRechargeVO> records= new HashSet<>();

					String[] subData=new String[6];
					LowBasedRechargeVO lowBaseDataVO=null;

					br=new BufferedReader(fr);		
					String line=br.readLine();
					String networkCode= getNetworkCode(line);

					if(NetworkCache.getNetworkByExtNetworkCode(networkCode)==null) {
						validFile = false;
						EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UploadMSISDNForLBProcess[process]","","","","Invalid Network Code in file  ");
						log.debug(methodName,"Network code :"+networkCode+" not found in the cache for file  ");
						throw new BTSLBaseException(uploadMSISDNForLBProcess,methodName,PretupsErrorCodesI.LB_FILE_ERROR);
					} 
					else
					{
						validFile=true;
						ProcessBL processBL = new ProcessBL();
						processStatusVOForSelectedNetwork=processBL.checkProcessUnderProcessNetworkWise(con,ProcessI.LB_FILEUPLOADID,networkCode);
						boolean statusOk = processStatusVOForSelectedNetwork.isStatusOkBool();
						if(statusOk)
						{
							mcomCon.finalCommit();
						}else {
							mcomCon.finalRollback();
						}

						if(statusOk){
							line = br.readLine();
							String[] header=line.split(",");

							if(header.length != 6 || !(header[0].trim().equalsIgnoreCase("Subscriber MSISDN") && header[1].trim().equalsIgnoreCase("Min") && header[2].trim().equalsIgnoreCase("Max") && header[3].trim().equalsIgnoreCase("Commission") && header[4].trim().equalsIgnoreCase("Expiry Date") && header[5].trim().equalsIgnoreCase("Operation")))
							{
								log.debug(methodName,"Invalid Header : "+fileObject);
								EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UploadMSISDNForLBProcess[process]","","","","Invalid header : "+fileObject);

								throw new BTSLBaseException(uploadMSISDNForLBProcess,methodName,PretupsErrorCodesI.LB_FILE_ERROR);
							}
							line = br.readLine();
							String msisdn=null;
							while (!(line.toUpperCase()).startsWith("EOF")) {
								try{
									LowBasedRechargeVO lowBasedRechargeVO = new LowBasedRechargeVO();
									boolean fileValidationErrorExists=false;
									if(BTSLUtil.isNullString(line) || line.isEmpty())
									{	

										log.debug(methodName,"Invalid data in the file or blank line: "+fileObject);
										EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UploadMSISDNForLBProcess[process]","","","","Blank line in the file : "+fileObject);

										throw new BTSLBaseException(uploadMSISDNForLBProcess,methodName,PretupsErrorCodesI.LB_SYSTEMLIMIT_ERROR);
									}
									subData = line.split(",");
									fileValidationErrorExists=false;

									msisdn = subData.length > 0 ? subData[0]: null;
									String min=subData.length > 1 ? subData[1]: null;
									String max=subData.length > 2 ? subData[2]: null;
									String commission=subData.length > 3 ? subData[3]: null;
									String expiryDate = subData.length > 4 ? subData[4]: null;
									String operation = subData.length > 5 ? subData[5]: null;

									StringBuilder errorBuff = new StringBuilder(msisdn+","+min+","+max+","+commission+","+expiryDate+","+operation+",");

									Date currentDate= new Date();
									Date expDate=null;
									
									
									if(BTSLUtil.isNullString(msisdn) || !BTSLUtil.isNumeric(msisdn) || msisdn.length()!=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MSISDN_LENGTH_CODE))).intValue())
									{
										fileValidationErrorExists=true;
										errorBuff.append("MSISDN is invalid.");
									}
									if(BTSLUtil.isNullString(min) || !BTSLUtil.isNumeric(min) || BTSLUtil.isNullString(max)  || !BTSLUtil.isNumeric(max) || Long.valueOf(min) < 0 || Long.valueOf(max) < 0 || Long.valueOf(min)>Long.valueOf(max) ){
										fileValidationErrorExists=true;
										errorBuff.append(" Min or Max value is invalid.");
									}

									if(BTSLUtil.isNullString(commission) || Double.valueOf(commission) >100 || Double.valueOf(commission) < 0  ){
										fileValidationErrorExists=true;
										errorBuff.append(" Commission percentage value is invalid ( it should be between 0 to 100).");
									}
									try{
										 expDate=BTSLUtil.getDateFromDateString(expiryDate,"dd-MM-yyyy");
										
										 if(BTSLUtil.isNullString(expiryDate) || expDate.before(currentDate)){
											 fileValidationErrorExists=true;
											 errorBuff.append(" Expiry date should be valid and  greater than today.");
										 }
									}
									catch(ParseException e){
										fileValidationErrorExists=true;
										errorBuff.append(" expiryDate format is Invalid or error in parsing ;");
									}
									if(BTSLUtil.isNullString(operation) || !(operation.equals("U")||operation.equals("D"))){
										fileValidationErrorExists=true;
										errorBuff.append(" Operation type is invalid.");
									}
									if(fileValidationErrorExists) 
										invalidRecordsList.add(errorBuff.toString());
									else{
										validRecordCount++;
										lowBasedRechargeVO.setMsisdn(msisdn);
										lowBasedRechargeVO.setLbmin(PretupsBL.getSystemAmount(min));
										lowBasedRechargeVO.setLbmax(PretupsBL.getSystemAmount(max));
										lowBasedRechargeVO.setCommissionPercentage(Math.round(Double.valueOf(commission)*100.0)/100.0);
										lowBasedRechargeVO.setExpiryDate(expDate);
										lowBasedRechargeVO.setLowBaseSubscriberOperationType(LowBaseSubscriberOperationType.fromType(operation));
										records.add(lowBasedRechargeVO);
									}


									line = br.readLine();
									if(recordsCountInFile%2000 == 0){
										writeLowBaseSubDetailsInDB(records);
										records.clear();
									}
								}
								catch(Exception e1){
									log.error(methodName, "SQLException" + e1);
									log.errorTrace(methodName,e1);
									line = br.readLine();
									log.debug(methodName,"Invalid record in the file is discarded for msisdn="+msisdn);
									EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"UploadMSISDNForLBProcess[process]","","",""," Invalid record in the file is discarded."+e1.getMessage());
									log.error(methodName," Invalid record in the file is discarded. "+e1.getMessage());
								}

							}	//while loop ends here
							if(validRecordCount > 0)
							{
								writeLowBaseSubDetailsInDB(records);
								records.clear();
							}
							if(!invalidRecordsList.isEmpty())   
							{						
								writeInvalidRecordsToCSV(invalidRecordsList,lbUploadFiledDir+File.separator+"Error_"+file);
								invalidRecordsList.clear();
							}

						}
					}
					br.close();
					fr.close();
					if(fileObject!=null)
					{
						String processed="Processed_"+ fileObject.getName();
						if(fileObject.renameTo(new File(lbUploadSuccessDir+File.separator+processed)))
						{
							 log.debug(methodName, "File renamed successfully");
						}						
						 
						messages="Low base Subscribers File "+fileObject.getName()+" processed and moved successfully.";
                         for (int i = 0, len = adm_msisdn.length; i < len; i++) {
                             pushMessage = new PushMessage(adm_msisdn[i], messages, null, null, locale);
                             pushMessage.push();
                         }
					}
				}
				catch (BTSLBaseException e) {
					log.error("process", "Exception=" + e);
					if(fileObject!=null)
					{	
						log.error(methodName," File is rejected for processing. "+e.getMessage());
						String processed="Fail_"+ fileObject.getName();
						if(fileObject.renameTo(new File(lbUploadFiledDir+File.separator+processed)))
						{
							 log.debug(methodName, "File renamed successfully");
						}
					}
					log.debug(methodName,"Exception in Lowbase upload process ..");
					messages="Low base file "+ fileObject.getName() +" is rejected for processing and moved to failed directory.";		
					 for (int i = 0, len = adm_msisdn.length; i < len; i++) {
                         pushMessage = new PushMessage(adm_msisdn[i], messages, null, null, locale);
                         pushMessage.push();
                     }
				} catch (FileNotFoundException e) {
					
					log.error("process", "Exceptin:e=" + e);
					log.errorTrace(methodName, e);
					log.debug(methodName,"File not found Exception in Lowbase upload process ...");
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"UploadMSISDNForLBProcess[process]","","",""," File not found for processing :"+e.getMessage());
				}
				catch(Exception e1)
				{
					log.error(methodName, "Exception" + e1);
					log.errorTrace(methodName,e1);
					if(fileObject!=null)
					{
						log.error(methodName," File is rejected for processing. "+e1.getMessage());
						String processed="Fail_"+ fileObject.getName();
						if(fileObject.renameTo(new File(lbUploadFiledDir+File.separator+processed)))
						{
							 log.debug(methodName, "File renamed successfully");
						}
					}
					log.debug(methodName,"Lowbase upload process completed Unsuccessfully....");
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"UploadMSISDNForLBProcess[process]","","","","File "+ fileObject.getName() +" is rejected for processing and moved to failed directory.");
				}

				finally
				{ 
				try{
	        		if(br!=null){
	        			br.close();
	        		}
	        	}catch(Exception e){
	        		log.error(methodName, e);
	        	}   
					if(validFile) {
						if(processStatusVOForSelectedNetwork!=null && processStatusVOForSelectedNetwork.isStatusOkBool())
						{
							try
							{  
								Date currentDate= new Date();
								processStatusVOForSelectedNetwork.setExecutedOn(currentDate);
								processStatusVOForSelectedNetwork.setProcessStatus(ProcessI.STATUS_COMPLETE);
								processStatusVOForSelectedNetwork.setExecutedUpto(currentDate);     
								processStatusVOForSelectedNetwork.setStartDate(processStatusVOForSelectedNetwork.getStartDate());        
								ProcessStatusDAO processDAO=new ProcessStatusDAO();
								if(processDAO.updateProcessDetailNetworkWiseDP(con,processStatusVOForSelectedNetwork)>0) {
									mcomCon.finalCommit();
								} else {
									mcomCon.finalRollback();
								}
							}
							catch(Exception e1)
							{
								if (log.isDebugEnabled()) {
									log.error(methodName," Exception in update process status details "+e1.getMessage());
									
								}
							}
						}
					}



				}
				
				log.debug(methodName, "Exiting:");
			}	

			


		}
		catch(BTSLBaseException be)
		{
			log.error(methodName, "BTSLBaseException : " + be.getMessage());
			log.errorTrace(methodName,be);
			EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"UploadMSISDNForLBProcess[process]","","",""," BTSLBaseException in UploadMSISDNForLBProcess ."+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			log.error(methodName, "Exception : " + e.getMessage());
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"UploadMSISDNForLBProcess[process]","","",""," Exception in UploadMSISDNForLBProcess ."+e.getMessage());
			throw new BTSLBaseException("UploadMSISDNForLBProcess",methodName,PretupsErrorCodesI.LOW_BASE_GENERAL_EXCEPTION);
		}
		finally {
			if (mcomCon != null) {
				mcomCon.close("UploadMSISDNForLBProcess#process");
				mcomCon = null;
			}
			if(log.isDebugEnabled()) {
				log.debug(methodName, "Exiting.....Low Base MSISDN Upload Process is complete");
			}
		}

	}



	private String getNetworkCode(String line) throws BTSLBaseException {
		final String methodName = "getNetworkCode()";
		String[] arr=line.split(",");
		String networkCode=null;
		if(arr.length > 2)
		{
			log.debug(methodName,"Error : Invalid network");
			throw new BTSLBaseException("UploadMSISDNForLBProcess", methodName, PretupsErrorCodesI.LB_FILE_ERROR);
		}
		if(!BTSLUtil.isNullString(arr[1]))
		{
			networkCode= arr[1];
		}
		return networkCode;
	}

	public void writeInvalidRecordsToCSV(List<String> invalidRecords, String pfileName){

		// write invalidRecordsList to file 
		if(log.isDebugEnabled()) {
			log.debug("writeInvalidRecordsToCSV"," pfileName: "+pfileName);
		}

		final String methodName = "writeInvalidRecordsInCSV";

		String[]afterTrim= pfileName.split(".csv");
		pfileName=afterTrim[0];
		pfileName=pfileName+".csv";
		File file  = new File(pfileName);
		BufferedWriter wr= null;
		try(FileOutputStream outFile=new FileOutputStream(file)) {
			wr = new BufferedWriter(new OutputStreamWriter(outFile));
			wr.write("Subscriber MSISDN,Min,Max,Commission,Expiry Date,Operation,Error");
			wr.newLine();
			for(String record : invalidRecords){
				wr.write(record );
				wr.newLine();
			}
			

		} catch (IOException e) {
			log.error(methodName, "IOException" + e);
			log.errorTrace(methodName,e);
			log.error(methodName, "Exception : " + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UploadMSISDNForLBProcess[writeInvalidRecordsToCSV]","","","","Exception :"+e.getMessage());

		} finally  {
			try{
				if(wr!=null){
					wr.close();
				}
			}catch(Exception e){
				log.errorTrace(methodName,e);
			}
			
			log.debug(methodName, "Writing error in the file is complete..... ");
		}

	}

	private void writeLowBaseSubDetailsInDB (Set<LowBasedRechargeVO> records) throws BTSLBaseException {
		// insert data in the low_base_customer
		final String methodName = "writeLowBaseSubDetailsInDB";
		PreparedStatement pstmtInsertLowBaseSubs=null;
		PreparedStatement pstmtDeleteLowBaseSubs = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		Date date = new Date();
		try{
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			StringBuilder strBuffInsertLowBaseData = new StringBuilder("INSERT INTO low_base_customer (customer_msisdn, min_rech_amount, ");
			strBuffInsertLowBaseData.append("max_rech_amount, created_on, commission, expiry_date )"); 
			strBuffInsertLowBaseData.append("VALUES (?,?,?,?,?,?)");

			if (log.isDebugEnabled()) {
				log.debug(methodName, "strBuffInsertLowBaseData Query ="+strBuffInsertLowBaseData);

			}
			StringBuilder strBuffDeleteLowBaseData = new StringBuilder("delete from low_base_customer where customer_msisdn=?");
			if (log.isDebugEnabled()) {
				log.debug(methodName, "strBuffDeleteLowBaseData Query ="+strBuffDeleteLowBaseData);

			}
			pstmtDeleteLowBaseSubs =  con.prepareStatement(strBuffDeleteLowBaseData.toString());
			pstmtInsertLowBaseSubs  = con.prepareStatement(strBuffInsertLowBaseData.toString());

			for (LowBasedRechargeVO record : records) {
				try{
					if(record.getLowBaseSubscriberOperationType() == LowBaseSubscriberOperationType.UPDATE)
					{

						int queryExecutionCount = -1;
						int index=0;
						pstmtInsertLowBaseSubs.setString(++index,record.getMsisdn());
						pstmtInsertLowBaseSubs.setLong(++index,record.getLbmin());
						pstmtInsertLowBaseSubs.setLong(++index,record.getLbmax());
						pstmtInsertLowBaseSubs.setTimestamp(++index,BTSLUtil.getTimestampFromUtilDate(date));
						pstmtInsertLowBaseSubs.setDouble(++index,record.getCommissionPercentage());
						pstmtInsertLowBaseSubs.setTimestamp(++index,BTSLUtil.getTimestampFromUtilDate(record.getExpiryDate()));

						queryExecutionCount=pstmtInsertLowBaseSubs.executeUpdate();
						if(queryExecutionCount<=0)
						{
							mcomCon.finalRollback();
							log.error(methodName, "Record cannot be inserted in low_base_customer table .");

							continue;
						}
						pstmtInsertLowBaseSubs.clearParameters();

					}

					else if(record.getLowBaseSubscriberOperationType() == LowBaseSubscriberOperationType.DELETE)
					{
						int queryExecutionCount = -1;
						int index=0;
						pstmtDeleteLowBaseSubs.setString(++index,record.getMsisdn());
						queryExecutionCount=pstmtDeleteLowBaseSubs.executeUpdate();
						if(queryExecutionCount<=0)
						{
							mcomCon.finalRollback();
							log.error(methodName, "Record cannot be deleted from low_base_customer table .");

							continue;
						}
						pstmtDeleteLowBaseSubs.clearParameters();
					}	
					mcomCon.finalCommit();

				}
				catch(SQLException sqe) {
					log.errorTrace(methodName, sqe);
					
					if (sqe.getErrorCode() == 00001) {
						EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UploadMSISDNForLBProcess[writeLowBaseSubDetailsInDB]","","","","Record Already exists for MSISDN: "+record.getMsisdn()+" SQL error: "+sqe.getMessage());	
					}
				}
			}
		}
		catch(SQLException e2){
			
			log.error(methodName, "Exception in deleting/writing data  in db : " + e2.getMessage());
			log.errorTrace(methodName,e2);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UploadMSISDNForLBProcess[writeLowBaseSubDetailsInDB]","","","","Exception in deleting/writing data in db :"+e2.getMessage());
		}
		finally
		{
			try{
				if (pstmtInsertLowBaseSubs != null)
				{
					pstmtInsertLowBaseSubs.close();
				}

			}catch (SQLException sqe)
			{
				log.errorTrace(methodName, sqe);
			}
			try{
				if (pstmtDeleteLowBaseSubs != null)
				{
					pstmtDeleteLowBaseSubs.close();
				}

			}catch (SQLException sqe1)
			{
				log.errorTrace(methodName, sqe1);
			}
			if (mcomCon != null) {
				mcomCon.close("UploadMSISDNForLBProcess#writeLowBaseSubDetailsInDB");
				mcomCon = null;
			}
			log.debug(methodName, "Writing/Deleting data in DB is complete..... ");
		}	
	}
}
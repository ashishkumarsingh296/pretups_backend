/*
 * COPYRIGHT: Mahindra Comviva Technologies Pvt. Ltd.
 *
 * This software is the sole property of Comviva and is protected
 * by copyright law and international treaty provisions. Unauthorized
 * reproduction or redistribution of this program, or any portion of
 * it may result in severe civil and criminal penalties and will be
 * prosecuted to the maximum extent possible under the law.
 * Comviva reserves all rights not expressly granted. You may not
 * reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted
 * by applicable law notwithstanding this limitation.
 *
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE. YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.btsl.pretups.processes.clientprocesses;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGeneratorDAO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyPointsRedemptionVO;
import com.btsl.pretups.network.businesslogic.NetworkDAO;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockDAO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.FileUtil;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.OracleUtil;

/**
 * @author Diwakar
 *
 */
public class BatchCommissionProcess {
	private static Log logger = LogFactory.getLog(BatchCommissionProcess.class.getName());
	private static String paymentType = "";
	private static ArrayList<String> statusList=new ArrayList<String>();
	private static Properties batchCommissionProperties = new Properties();
	public static OperatorUtilI calculatorI=null;

	public static void main(String[] args){
		final String methodName = "BatchCommissionProcess[main()]";
		try
		{
			if(args.length<2 || args.length>3 )
			{
				if(logger.isDebugEnabled())
					logger.debug(methodName, "Usage : BatchCommissionProcess [Constants file] [LogConfig file] [Y/N]");
				return;
			}
			//load constants.props
			File constantsFile = new File(args[0]);
			if(!constantsFile.exists() )
			{
				if(logger.isDebugEnabled())
					logger.debug(methodName, "BatchCommissionProcess "+" Constants File Not Found .............");
				logger.error("BatchCommissionProcess[main]", "Constants file not found on location: "+constantsFile.toString() );
				return;
			}
			//load log config file
			File logFile = new File(args[1]);
			if(!logFile.exists())
			{
				if(logger.isDebugEnabled())
					logger.debug(methodName, "BatchCommissionProcess"+" Logconfig File Not Found .............");
				logger.error("BatchCommissionProcess[main]", "Logconfig File not found on location: "+logFile.toString());
				return;
			}


			File batchHierarchyCommissionConfigFile = new File(args[2]);
			if(!batchHierarchyCommissionConfigFile.exists())
			{
				if(logger.isDebugEnabled())
					logger.debug(methodName, "BatchCommissionProcess"+" BatchHierarchyCommission.props File Not Found .............");
				return;
			}
			ConfigServlet.loadProcessCache(constantsFile.toString(),logFile.toString());
			batchCommissionProperties.load(new FileInputStream(batchHierarchyCommissionConfigFile));

		}//end of try block
		catch(Exception e)
		{
			if (logger.isDebugEnabled())
				logger.debug("main"," Error in Loading Files ...........................: "+e.getMessage());
			logger.errorTrace(methodName,e);
			ConfigServlet.destroyProcessCache();
			return;
		}//end of catch block
		try
		{
			process();
		}//end of try block
		catch(BTSLBaseException be)
		{
			logger.error("main", "BTSLBaseException : " + be.getMessage());
			logger.errorTrace(methodName,be);
			return;
		}//end of catch block
		catch(Exception e)
		{
			if (logger.isDebugEnabled())
				logger.debug("main"," "+e.getMessage());
			logger.errorTrace(methodName,e);
			return;
		}//end of catch block
		finally
		{
			if (logger.isDebugEnabled())
				logger.info("main","Exiting");
			try
			{
				Thread.sleep(5000);
			}
			catch(Exception e)
			{
				logger.errorTrace(methodName,e);
			}
			ConfigServlet.destroyProcessCache();
		}//end of finally
	}

	/**
	 * This method checks the process is under process/complete for the process id
	 * specified in process_status table
	 * @return void
	 * @throws BTSLBaseException
	 */
	private static void process() throws BTSLBaseException{
		final String methodName = "process";
		if (logger.isDebugEnabled())
			logger.info(methodName,"Entered ");
		String processId = null;
		ProcessBL processBL = null;
		Connection con = null;
		ProcessStatusVO processStatusVO = null;
		Date currentDate = null;
		Date processedUpto = null;
		String finalDirectoryPath = null;
		int updateCount = 0;                            //check process details are updated or not
		try
		{
			String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
			try
			{
				calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
			}
			catch(Exception e)
			{
				logger.errorTrace(methodName, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelTransferBL[initialize]","","","","Exception while loading the class at the call:"+e.getMessage());
			}
			processId = ProcessI.BATCH_COMM_TRF_PROCESS;
			con = OracleUtil.getSingleConnection();
			processBL = new ProcessBL();
			processStatusVO = processBL.checkProcessUnderProcess(con,processId);
			if(processStatusVO.isStatusOkBool()){
				//method call to find maximum date till which process has been executed
				processedUpto=processStatusVO.getExecutedUpto();
				if (processedUpto != null){
					currentDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(new Date()));
					processedUpto = currentDate;
					String dir = Constants.getProperty("UploadBatchO2CInitiationFilePath");
					File directory = new File(dir);
					File[] directoryListing = directory.listFiles();
					boolean isFileProcessed = false;
					if (directoryListing != null && directoryListing.length > 0) {
						finalDirectoryPath=createDirectory();
						for (File child : directoryListing) {
							isFileProcessed = processFile(child, dir, finalDirectoryPath,con);
						}
					} else {
						logger.debug(methodName, "No Files found in directory structure at path ="+dir);
						//Added to update the executed_upto if no file was exists
						processStatusVO.setExecutedUpto(BTSLUtil.addDaysInUtilDate(processedUpto,-1));
					}
					if(isFileProcessed) {
						processStatusVO.setExecutedUpto(BTSLUtil.addDaysInUtilDate(processedUpto,-1));
						EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "BatchCommissionProcess[process]","","",""," Batch O2C Initiation has been executed successfully.");
						if(logger.isDebugEnabled())
							logger.debug(methodName, "File processed successfully");
					}
				}else
					throw new BTSLBaseException(methodName, PretupsErrorCodesI.BATCH_O2C_INITITATION_EXECUTED_UPTO_DATE_NOT_FOUND);
			}else
				throw new BTSLBaseException(methodName, PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
		}catch(BTSLBaseException be){
			logger.errorTrace(methodName, be);
			throw be;
		}catch(Exception e){
			logger.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "BatchCommissionProcess[process]","","",""," Batch O2C Initiation process could not be executed successfully.");
			throw new BTSLBaseException(methodName, methodName, PretupsErrorCodesI.ERROR_IN_DAILY_ALERT);
		}finally{
			try{
				if (processStatusVO.isStatusOkBool()){
					processStatusVO.setStartDate(currentDate);
					processStatusVO.setExecutedOn(currentDate);
					processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
					updateCount=(new ProcessStatusDAO()).updateProcessDetail(con,processStatusVO);
					if(updateCount>0) {
						con.commit();
					}
				}
			}catch(Exception ex){
				if(logger.isDebugEnabled())
					logger.debug(methodName, "Exception in closing connection ");
				logger.errorTrace(methodName, ex);
			}

			try{
				con.close();
			}catch(SQLException e1)
			{
				logger.errorTrace(methodName, e1);
			}
			if(logger.isDebugEnabled())
				logger.debug(methodName, "Exiting..... ");
		}
	}

	/*
	 * this method is used to read the data from file and create the O2C request through it
	 * @param file File
	 * @param dirPath String
	 * @param finalDirectory String
	 * throws BTSLBaseException
	 * returns boolean value
	 */

	public static boolean processFile(File file, String dirPath, String finalDirectroyPath,Connection con) throws BTSLBaseException{
		BufferedReader br = null;
		String methodName = "processFile";
		if(logger.isDebugEnabled())
			logger.debug(methodName, "FILE IS BEING PROCESSED :: file="+file+", dirPath="+dirPath);
		String requestGatewayCode="DWEXTGW";
		String transactionRemark="Hierarchy commission Transfer on Dual Wallet transactions";
		String requestGatewayType=PretupsI.REQUEST_SOURCE_TYPE_EXTGW;
		String delimeter=null;
		String fileFormat=null;
		int dataStartRow;
		String remarks=null;
		try {
			String sCurrentLine;
			ArrayList<RequestVO> recordList=new ArrayList<RequestVO>();
			RequestVO requestVO1 = null;
			requestGatewayCode = batchCommissionProperties.getProperty("REQUEST_GATEWAY_CODE");                        
			requestGatewayType = batchCommissionProperties.getProperty("REQUEST_GATEWAY_TYPE");
			transactionRemark = batchCommissionProperties.getProperty("TRANSACTION_REMARK");
			if(BTSLUtil.isNullString(requestGatewayCode)|| requestGatewayCode.equalsIgnoreCase("null")){
				requestGatewayCode = "DWEXTGW";
			}
			if(BTSLUtil.isNullString(requestGatewayType) || requestGatewayType.equalsIgnoreCase("null")){
				requestGatewayType = PretupsI.REQUEST_SOURCE_TYPE_EXTGW;
			}
			delimeter = batchCommissionProperties.getProperty("DELIMETER");                        
			fileFormat = batchCommissionProperties.getProperty("FILE_FORMAT");
			dataStartRow=Integer.parseInt(batchCommissionProperties.getProperty("DATA_START_ROW"));

			remarks="";
			String[] fileFormatArray=null;
			HashMap<String,Integer> index = new HashMap<String,Integer>();
			if(!BTSLUtil.isNullString(fileFormat)){
				fileFormatArray=fileFormat.split(delimeter);                        	
			}
			for(int count=0;count<fileFormatArray.length;count++){
				index.put(fileFormatArray[count],count);
			}

			br = new BufferedReader(new FileReader(file));
			int lineNum=0;
			while ((sCurrentLine = br.readLine()) != null) {
				lineNum++;
				if(lineNum < dataStartRow)
					continue;
				if(logger.isDebugEnabled())
					logger.debug(methodName, sCurrentLine);
				RequestVO requestVO = new RequestVO();
				Map<String, String> elementMap = new HashMap<String, String>();
				String msisdn=sCurrentLine.split(delimeter)[index.get("MSISDN")];
				requestVO.setMsisdn(msisdn.replaceAll("[^0-9]", ""));
				String amount=sCurrentLine.split(delimeter)[index.get("AMOUNT")];
				elementMap.put("QTY", amount);
				if(Arrays.asList(fileFormatArray).contains("PAYMENT_TYPE")){
					paymentType=sCurrentLine.split(delimeter)[index.get("PAYMENT_TYPE")];
				}
				elementMap.put("PAYMENT_TYPE", paymentType);
				remarks="";
				if(Arrays.asList(fileFormatArray).contains("REMARKS")){
					remarks=sCurrentLine.split(delimeter)[index.get("REMARKS")];                        		
				}
				elementMap.put("REMARKS", remarks);
				if (logger.isDebugEnabled())
					logger.debug(methodName, "msisdn = "+msisdn+", amount = "+amount+", paymentType = "+paymentType+", remarks = "+remarks);
				requestVO.setRequestGatewayCode(requestGatewayCode);
				requestVO.setRequestGatewayType(requestGatewayType);
				requestVO.setRemarks(transactionRemark);
				requestVO.setRequestMap((HashMap<String, String>)elementMap);
				recordList.add(requestVO);
			}
			if (logger.isDebugEnabled())
				logger.debug(methodName, "recordList = "+recordList);
			
			//Start Processing the FoC Transfer 
			for(int recordIndex=0; recordIndex<recordList.size(); recordIndex++){
				requestVO1 = recordList.get(recordIndex);
				if (logger.isDebugEnabled()) {
					logger.debug(methodName, "record = "+requestVO1);
				}
				ChannelUserVO channelUserVO = new ChannelUserVO ();
				ChannelUserDAO channelUserDAO = new ChannelUserDAO ();
				LoyaltyPointsRedemptionVO redempVO = new LoyaltyPointsRedemptionVO();
				Date currentDate=new Date();
				try {
					channelUserVO=(ChannelUserVO)channelUserDAO.loadChannelUserDetails(con,requestVO1.getMsisdn());
					ChannelUserVO receiverUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con,channelUserVO.getUserID(),false,currentDate,false);
					if(channelUserVO == null || receiverUserVO == null)
					{
						if (logger.isDebugEnabled())
							logger.debug(methodName, "Dual Wallet Commission quantity transfer  is not possible as Receiver does not exists: ");
						 redempVO.setTxnStatus(PretupsErrorCodesI.NO_USER_EXIST);
					 }
					 // check user status
					 else if(!PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus()))
					 {
						 if (logger.isDebugEnabled())
								logger.debug(methodName, "Dual Wallet Commission quantity transfer  is not possible as Receiver is not active: "+channelUserVO.getUserID());
						 redempVO.setTxnStatus(PretupsErrorCodesI.USER_STATUS_NOTACTIVE);
					 }
					 // check user's commission profile.
					 else if(receiverUserVO.getCommissionProfileApplicableFrom().after(currentDate))
					 {
						 if (logger.isDebugEnabled())
								logger.debug(methodName, "Dual Wallet Commission quantity transfer  is not possible as Receiver's commission profile is not applicable : "+channelUserVO.getUserID());
						 redempVO.setTxnStatus(PretupsErrorCodesI.ERROR_USER_COMMISSION_PROFILE_NOT_APPLICABLE);
					 }
					 //check user's commission profile status.
					 else if(!PretupsI.YES.equals(receiverUserVO.getCommissionProfileStatus()))
					 {
						 if (logger.isDebugEnabled())
								logger.debug(methodName, "Dual Wallet Commission quantity transfer  is not possible as Receiver's commission profile is not active : "+channelUserVO.getUserID());
						 redempVO.setTxnStatus(PretupsErrorCodesI.ERROR_USER_COMMISSION_PROFILE_NOT_APPLICABLE);
					}
					 //check user's transfer profile status.
					 else if(!PretupsI.YES.equals(receiverUserVO.getTransferProfileStatus()))
					 {
						 if (logger.isDebugEnabled())
								logger.debug(methodName, "Dual Wallet Commission quantity transfer  is not possible as Receiver's transfer profile is not active : "+channelUserVO.getUserID());
						 redempVO.setTxnStatus(PretupsErrorCodesI.ERROR_USER_COMMISSION_PROFILE_NOT_APPLICABLE);
					}
					//checking receiver's IN suspend 
					else if(receiverUserVO.getInSuspend().equalsIgnoreCase(PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND)) 
					{
						if (logger.isDebugEnabled())
							logger.debug(methodName, "Dual Wallet Commission quantity transfer  is not possible as Receiver is IN suspend, receiver user ID: "+channelUserVO.getUserID() + " receiver's IN suspend: "+channelUserVO.getInSuspend());
						redempVO.setTxnStatus(PretupsErrorCodesI.ERROR_USER_SUSPENDED);
					}				
					else {								
						ArrayList stocklist = new ArrayList();
						NetworkDAO networkDAO=new NetworkDAO();
						NetworkVO networkVO =null;
						NetworkStockDAO networkStockDAO = new NetworkStockDAO();
						NetworkStockVO stockVO=null;
						networkVO=(NetworkVO)networkDAO.loadNetwork(con, channelUserVO.getNetworkID());
						stocklist= networkStockDAO.loadCurrentStockList(con,networkVO.getNetworkCode(),networkVO.getNetworkCode(),networkVO.getNetworkType());	
						stockVO = (NetworkStockVO)stocklist.get(0);
						long commissionTobeTransfer = 0;
						try {
							commissionTobeTransfer = Long.parseLong((String)requestVO1.getRequestMap().get("QTY"));
						} catch (Exception e) {
							logger.errorTrace(methodName,e);
						}
						long currentStock = 0;
						int stockListSizes=stocklist.size();
                        for (int i = 0; i <stockListSizes ; i++) {
                            stockVO = (NetworkStockVO) stocklist.get(i);
                            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue()) {
                                if (PretupsI.FOC_WALLET_TYPE.equals(stockVO.getWalletType())) {
                                    break;
                                }
                            } else {
                                if (PretupsI.SALE_WALLET_TYPE.equals(stockVO.getWalletType())) {
                                    break;
                                }
                            }
                        }
                        currentStock=stockVO.getWalletbalance();
						if(currentStock <commissionTobeTransfer)
						{
							if (logger.isDebugEnabled())
								logger.debug(methodName, "Dual Wallet Commission quantity transfer  is not possible as stock is not sufficient, receiver user ID: "+channelUserVO.getUserID());
							redempVO.setTxnStatus(PretupsErrorCodesI.ERROR_NW_STOCK_LESS);
							
						} else {
							redempVO.setO2cContribution(commissionTobeTransfer);
							performFOC(con, redempVO,channelUserVO,requestVO1.getRequestGatewayCode(),requestVO1.getRequestGatewayType(),requestVO1.getRemarks(),receiverUserVO);
						}
					}
				} catch (Exception e) {
					logger.errorTrace(methodName,e);
				}
			parseResponse(channelUserVO.getMsisdn(),redempVO);
			if(logger.isDebugEnabled())
				logger.debug(methodName,"Response Received  channelUserVO.getMsisdn() =" + channelUserVO.getMsisdn()+", redempVO.getReferenceNo()="+redempVO.getReferenceNo()+", redempVO.getTxnStatus()="+redempVO.getTxnStatus());
			}
		} catch (IOException e) {
			logger.errorTrace(methodName, e);
		} finally {
			writeToFile(finalDirectroyPath,statusList,file.getName());
			moveFilesToFinalDirectory(dirPath, finalDirectroyPath+"/",file.getName());
			FileUtil.closeQuietly(br);
		}
		return true;
	}

	/*
	 * this method parse the response
	 * @param msisdn String
	 * @param response String
	 * returns void
	 */
	public static void parseResponse(String msisdn,LoyaltyPointsRedemptionVO redempVO){
		String txnStatus;
		String txnId ;
		String txnNumber;
		String finalResponse ;

		txnStatus = redempVO.getTxnStatus();
		txnId = redempVO.getReferenceNo();
		txnNumber = redempVO.getReferenceNo();
		if(txnId == null)
			txnId = "";
		finalResponse = msisdn+"|"+txnId +"|" + txnStatus + "|" + txnNumber;
		statusList.add(finalResponse);
		if (logger.isDebugEnabled())
			logger.debug("parseResponse","finalResponse ::"+finalResponse);
	}

	public static void writeToFile(String finalDirectoryPath, List<String> statusList, String fileName){
		if (logger.isDebugEnabled())
			logger.debug("writeToFile","Entered with FinalDirectoryPath ::"+finalDirectoryPath+"statusList size"+statusList.size()+" File Name :: "+fileName);
		String sucFileName = null;
		String failFileName = null;
		String message = null;
		sucFileName = finalDirectoryPath+"/"+fileName.split("[.]")[0]+"_Success."+batchCommissionProperties.getProperty("FILE_EXT_SUCCESS");
		failFileName = finalDirectoryPath+"/"+fileName.split("[.]")[0]+"_Fail."+batchCommissionProperties.getProperty("FILE_EXT_FAIL");
		try(PrintWriter sucWriter = new PrintWriter(sucFileName, "UTF-8");PrintWriter failWriter = new PrintWriter(failFileName, "UTF-8")){
			String status = null;
			Locale locale=new Locale("en","US");
			for(int statusCount=0; statusCount<statusList.size(); statusCount++){
				status = (statusList.get(statusCount)).split("[|]")[2];
				if(PretupsErrorCodesI.TXN_STATUS_SUCCESS.equalsIgnoreCase(status)){
					message = (statusList.get(statusCount)).split("[|]")[0]+"|"+(statusList.get(statusCount)).split("[|]")[1]+"|O2C Direct Transfer is successfull";
					sucWriter.println(message);
				}
				else{
					message = (statusList.get(statusCount)).split("[|]")[0]+"|"+BTSLUtil.getMessage(locale,(statusList.get(statusCount)).split("[|]")[2],null)+"|";
					failWriter.println(message);
				}
			}
			statusList.clear();
		}catch(Exception e){
			logger.errorTrace("writeToFile", e);
		}
	}

	/**
	 * This method will copy all the created files to another location.
	 * the process will generate files in a particular directroy. if the process thats has to read files strarts before copletion of the file generation,
	 * errors will occur. so a different directory is created and files are moved to that final directory.
	 * @param p_oldDirectoryPath String
	 * @param p_finalDirectoryPath String
	 * @param fileName String
	 * @throws BTSLBaseException
	 * @return void
	 */
	private static void moveFilesToFinalDirectory(String oldDirectoryPath,String finalDirectoryPath,String fileName) throws BTSLBaseException{
		final String methodName = "moveFilesToFinalDirectory";
		if (logger.isDebugEnabled())
			logger.debug(methodName," Entered: p_oldDirectoryPath="+oldDirectoryPath+" , p_finalDirectoryPath="+finalDirectoryPath+", fileName ="+fileName);

		String oldFileName=null;
		String newFileName=null;
		File oldFile=null;
		File newFile=null;
		File parentDir = new File(finalDirectoryPath);
		if(!parentDir.exists())
			parentDir.mkdirs();
		//child directory name includes a file name and being processed date, month and year
		File oldDir = new File(oldDirectoryPath);
		File newDir = new File(finalDirectoryPath);
		if(!newDir.exists())
			newDir.mkdirs();
		if(logger.isDebugEnabled())
			logger.debug(methodName, " newDirName=" + finalDirectoryPath);
		try{
			oldFileName = oldDirectoryPath+fileName;
			oldFile = new File(oldFileName);
			newFileName = oldFileName.replace(oldDirectoryPath, finalDirectoryPath);
			newFile = new File(newFileName);
			if(oldFile != null){
				oldFile.renameTo(newFile);
				if (logger.isDebugEnabled())
					logger.debug(methodName," File " + oldFileName + " is moved to " + newFileName);
			}
			logger.debug(methodName," File "+oldFileName+" is moved to "+newFileName);
		}
		catch(Exception e){
			logger.errorTrace(methodName,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DWHFileCreation[moveFilesToFinalDirectory]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(methodName,"deleteAllFiles",PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
		}finally{
			if(oldFile != null) 
				oldFile = null;
			if(newFile != null) 
				newFile = null;
			if(parentDir != null) 
				parentDir = null;
			if(newDir != null) 
				newDir = null;
			if(oldDir != null) 
				oldDir = null;
			if (logger.isDebugEnabled())
				logger.debug(methodName, "Exiting.. ");
		} // end of finally
	}

	public static String createDirectory(){
		String methodName="createDirectory";
		String dir = Constants.getProperty("FinalUploadBatchO2CInitiationFilePath");
		String dirName = null;
		String completeFinalDirPath = null;
		try{
			dirName = (((BTSLUtil.getDateTimeStringFromDate(new Date())).replace("/","")).replace(":","")).replace(" ","");
			completeFinalDirPath = dir + dirName;
			File file = new File(completeFinalDirPath);
			if (!file.exists()) {
				if (file.mkdir()) {
					logger.debug(methodName,"Directory is created!");
				} else {
					logger.debug(methodName,"Failed to create directory!");
				}
			}
		}catch(ParseException e){
			logger.error(methodName, "Exception: "+e.getMessage());
		}finally{
			if (logger.isDebugEnabled())
				logger.debug(methodName, "Exiting.. finalDirectoryName :: " + completeFinalDirPath);   
		}
		return completeFinalDirPath;
	}
	
	public static void performFOC(Connection con, LoyaltyPointsRedemptionVO redempVO, ChannelUserVO channelUserVO,String requestGatewayCode, String requestGatewayType,String transactionRemarks,ChannelUserVO receiverUserVO)
	 {
		final String methodName="performFOC";
		 try
		 {
			 if (logger.isDebugEnabled())
				 logger.debug(methodName, "Entered ChannelTransferVO "); 
		 	int creditCount =0;
			int updateCount=0;
		    int upCount=0;
			long transferAmount=redempVO.getO2cContribution();
			
			ChannelTransferVO channelTransferVO = new ChannelTransferVO();
			Date currentDate=new Date();
			ChannelTransferItemsVO transferItemsVO=null;
			ArrayList<ChannelTransferItemsVO> channelTransferItemVOList= new ArrayList<ChannelTransferItemsVO>();
			ChannelUserTransferDAO channelUserTransferDAO =new ChannelUserTransferDAO();
			ChannelTransferDAO channelTransferDAO=new ChannelTransferDAO();
			ChannelUserDAO channelUserDAO = new ChannelUserDAO ();
			channelTransferVO.setTransactionMode(PretupsI.AUTO_FOC_TXN_MODE);
			channelTransferVO.setNetworkCode(channelUserVO.getNetworkID());
			channelTransferVO.setNetworkCodeFor(channelUserVO.getNetworkID());
			channelTransferVO.setCreatedOn(currentDate);
			genrateOprtToChnnlTrfID(con,channelTransferVO);
			channelTransferVO.setTransferDate(currentDate);
			channelTransferVO.getTransferID();
			channelTransferVO.setActiveUserId(channelUserVO.getUserID());
			channelTransferVO.setToUserID(channelUserVO.getUserID());
			channelTransferVO.setRequestGatewayCode(requestGatewayCode);
			channelTransferVO.setRequestGatewayType(requestGatewayType);
			channelTransferVO.setChannelRemarks(transactionRemarks);
			
			if(BTSLUtil.isNullString(channelUserVO.getProductCode())) {
				channelUserVO.setProductCode(PretupsI.PRODUCT_ETOPUP);
			}
			if (logger.isDebugEnabled()) {
				logger.debug(methodName, PretupsI.ENTERED+": channelUserVO.getUserID()=" + channelUserVO.getUserID()+", transferAmount="+transferAmount+", channelUserVO.getProductCode()="+ channelUserVO.getProductCode());
	        }
			channelTransferItemVOList=channelUserTransferDAO.getTransferlistForAutoFOC(con, channelUserVO.getUserID(),transferAmount, channelUserVO.getProductCode());
			if((channelTransferItemVOList != null) && (channelTransferItemVOList.size() > 0))
			{
				for(int m=0,n=channelTransferItemVOList.size(); m<n ;m++)
				{
					transferItemsVO = (ChannelTransferItemsVO)channelTransferItemVOList.get(m);
					if(transferItemsVO.getProductCode().equalsIgnoreCase(PretupsI.PRODUCT_ETOPUP))
					{
						transferItemsVO=null;
						transferItemsVO=(ChannelTransferItemsVO)channelTransferItemVOList.get(m);
						transferItemsVO.setCommProfileDetailID(receiverUserVO.getCommissionProfileSetID());
						transferItemsVO.setOthCommSetId(receiverUserVO.getOthCommSetId());
						transferItemsVO.setCommType(PretupsI.AMOUNT_TYPE_PERCENTAGE);
						transferItemsVO.setSenderDebitQty(transferAmount);
						transferItemsVO.setReceiverCreditQty(transferAmount);
					}
				}
			}
			channelTransferVO.setChannelTransferitemsVOList(channelTransferItemVOList);
			channelTransferVO.setTransferMRP(transferAmount);
			channelTransferVO.setReceiverCategoryCode(channelUserVO.getCategoryCode());
			channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
			channelTransferVO.setType(PretupsI.TRANSFER_TYPE_O2C);
			channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
			channelTransferVO.setModifiedOn(currentDate);
			channelTransferVO.setRequestedQuantity(transferAmount);
			//added by harsh set Missing Values in O2C Transfer request triggered during LMS Redemption Process
			channelTransferVO.setToUserCode(channelUserVO.getMsisdn());
			channelTransferVO.setReferenceNum(redempVO.getRedemptionID());
			channelTransferVO.setFirstApprovedBy(PretupsI.SYSTEM_USER);
			channelTransferVO.setFirstApprovedOn(currentDate);
			channelTransferVO.setSecondApprovedBy(PretupsI.SYSTEM_USER);
			channelTransferVO.setSecondApprovedOn(currentDate);
			channelTransferVO.setThirdApprovedBy(PretupsI.SYSTEM_USER);
			channelTransferVO.setThirdApprovedOn(currentDate);
			channelTransferVO.setReceiverGgraphicalDomainCode(channelUserVO.getGeographicalCode());
			channelTransferVO.setReceiverDomainCode(channelUserVO.getDomainID());
			channelTransferVO.setTransferInitatedBy(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM);
			channelTransferVO.setReceiverGradeCode(channelUserVO.getUserGrade());
			channelTransferVO.setProductType(PretupsI.PRODUCT_TYPE_AUTO_O2C);
			if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue())
               channelTransferVO.setWalletType(PretupsI.FOC_WALLET_TYPE);
            else
               channelTransferVO.setWalletType(PretupsI.SALE_WALLET_TYPE);

			boolean debit=  true;
			int updateCount1=-1;
			updateCount=ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(con,channelTransferVO,channelUserVO.getUserID(),currentDate, debit);
			if(updateCount<1)
				throw new BTSLBaseException(methodName,methodName,PretupsErrorCodesI.ERROR_UPDATING_DATABASE);

			updateCount1=ChannelTransferBL.updateNetworkStockTransactionDetails(con,channelTransferVO,channelUserVO.getUserID(),currentDate );
			if(updateCount1<1)
				throw new BTSLBaseException(methodName,methodName,PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
			channelTransferVO.setToUserID(channelUserVO.getUserID());
			channelTransferVO.setReceiverTxnProfile(channelUserVO.getTransferProfileID());
			creditCount = channelUserDAO.creditUserBalances(con,channelTransferVO,false,null);
		    channelTransferVO.setReceiverTxnProfile(channelUserVO.getTransferProfileID());
	        channelTransferVO.setReceiverTxnProfileName(channelUserVO.getTransferProfileName());
	        channelTransferVO.setTotalTax1(0);
	        channelTransferVO.setTotalTax2(0);
	        channelTransferVO.setTotalTax3(0);
			channelTransferVO.setRequestedQuantity(transferAmount);
			channelTransferVO.setPayableAmount(0);
			channelTransferVO.setNetPayableAmount(0);
			channelTransferVO.setPayInstrumentType(PretupsI.PAYMENT_INSTRUMENT_TYPE_CASH);
			channelTransferVO.setPayInstrumentAmt(transferAmount);
			channelTransferVO.setTransferMRP(transferAmount);
			channelTransferVO.setFromUserID(PretupsI.OPERATOR_TYPE_OPT);
			channelTransferVO.setToUserName(channelUserVO.getUserName());
	        channelTransferVO.setGraphicalDomainCode(channelUserVO.getGeographicalCode());
			channelTransferVO.setDomainCode(channelUserVO.getDomainID());
			channelTransferVO.setReceiverCategoryCode(channelUserVO.getCategoryCode());
			channelTransferVO.setNetworkCodeFor(channelUserVO.getNetworkID());
			channelTransferVO.setCategoryCode(PretupsI.CATEGORY_TYPE_OPT);
			channelTransferVO.setTransferDate(currentDate);
			channelTransferVO.setCommProfileSetId(receiverUserVO.getCommissionProfileSetID());
			channelTransferVO.setCommProfileVersion(receiverUserVO.getCommissionProfileSetVersion());
		 	channelTransferVO.setCreatedOn(currentDate);
			channelTransferVO.setModifiedOn(currentDate);
			channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
			channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_SYSTEM);
			channelTransferVO.setProductCode(PretupsI.PRODUCT_ETOPUP);
			channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_TRANSFER);
			channelTransferVO.setType(PretupsI.TRANSFER_TYPE_O2C);
		    channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
	        channelTransferVO.setControlTransfer(PretupsI.YES);
	        channelTransferVO.setCommQty(0);
		    channelTransferVO.setSenderDrQty(transferAmount);
		    channelTransferVO.setReceiverCrQty(transferAmount);
		    channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
		    channelTransferVO.setCreatedBy(PretupsI.SYSTEM);
		    channelTransferVO.setModifiedBy(PretupsI.SYSTEM);
		    channelTransferVO.setCreatedOn(currentDate);
		    channelTransferVO.setModifiedOn(currentDate);
		    channelTransferVO.setTransferInitatedByName(PretupsI.SYSTEM);
		    //Setting for Channel Transfer Items
		    
		    UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
			UserBalancesVO userBalancesVO = constructBalanceVOFromTxnVO(channelTransferVO);
			userBalancesVO.setUserID(channelTransferVO.getToUserID());
			upCount=userBalancesDAO.updateUserDailyBalances(con,currentDate,userBalancesVO);
		    int count = channelTransferDAO.addChannelTransfer(con, channelTransferVO);
		    if(upCount>0&&count>0&&updateCount1>0&&creditCount>0)
			{
               con.commit();
               redempVO.setReferenceNo(channelTransferVO.getTransferID());
               redempVO.setTxnStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
               if (logger.isDebugEnabled())
  				 logger.debug(methodName, "Processing for sening the message for receiver msisdn ="+channelUserVO.getMsisdn());
	            //sending msg to receiver
	            try {
					UserPhoneVO phoneVO=null;
					UserPhoneVO primaryPhoneVO_R=null;
					UserDAO userDAO=new UserDAO();
					phoneVO = userDAO.loadUserAnyPhoneVO(con,channelUserVO.getMsisdn());
					if(phoneVO!=null && !(phoneVO.getPrimaryNumber()).equalsIgnoreCase("Y"))
					{
						channelUserVO.setPrimaryMsisdn(channelUserVO.getMsisdn());
						if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue())
							primaryPhoneVO_R=userDAO.loadUserAnyPhoneVO(con,channelUserVO.getPrimaryMsisdn());			        
					}
					if (logger.isDebugEnabled())
		  				 logger.debug(methodName, "Received for sening the message for primaryPhoneVO_R="+primaryPhoneVO_R);			            
					ArrayList itemsList =  channelTransferVO.getChannelTransferitemsVOList();
					String smsKey=null;		
					ArrayList txnList = new ArrayList();
					ArrayList balList = new ArrayList();
					String args[]= null;
					ChannelTransferItemsVO channelTrfItemsVO = null;
					KeyArgumentVO keyArgumentVO = null;
	
					int lSize=itemsList.size();	
					for(int i = 0; i < lSize; i++ )
					{
						channelTrfItemsVO = (ChannelTransferItemsVO) itemsList.get(i); 
						keyArgumentVO = new KeyArgumentVO();
						keyArgumentVO.setKey(PretupsErrorCodesI.O2C_DIRECT_TRANSFER_SUCCESS_TXNSUBKEY);
						args = new String[]{String.valueOf(channelTrfItemsVO.getShortName()),PretupsBL.getDisplayAmount(Double.parseDouble(channelTrfItemsVO.getRequestedQuantity()))};
						keyArgumentVO.setArguments(args);
						txnList.add(keyArgumentVO);
	
						keyArgumentVO = new KeyArgumentVO();
						keyArgumentVO.setKey(PretupsErrorCodesI.O2C_DIRECT_TRANSFER_SUCCESS_BALSUBKEY);
						args = new String[]{String.valueOf(channelTrfItemsVO.getShortName()),PretupsBL.getDisplayAmount(channelTrfItemsVO.getBalance() + channelTrfItemsVO.getRequiredQuantity())};
						keyArgumentVO.setArguments(args);
						balList.add(keyArgumentVO);
					}//end of for
					Locale locale=null;
					locale =new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
					String[] msgArray = null;
					msgArray= new String[]{BTSLUtil.getMessage(locale,txnList),BTSLUtil.getMessage(locale,balList),channelTransferVO.getTransferID(),channelTransferVO.getTransferInitatedByName()};
					smsKey=PretupsErrorCodesI.DW_HIERARCHY_COMMISSION_TRANSFER;
					if(primaryPhoneVO_R != null)
					{
						Locale localePrimary = new Locale(primaryPhoneVO_R.getPhoneLanguage(),primaryPhoneVO_R.getCountry());
						String senderMessage=BTSLUtil.getMessage(localePrimary,smsKey,msgArray);
						try {
							PushMessage pushMessage=new PushMessage(primaryPhoneVO_R.getMsisdn(),senderMessage,null,null,localePrimary);
							pushMessage.push();
						} catch (Exception e) {
							logger.errorTrace(methodName, e);
						}
					} else if(phoneVO !=null){
						Locale localePrimary = new Locale(phoneVO.getPhoneLanguage(),phoneVO.getCountry());
						String senderMessage=BTSLUtil.getMessage(localePrimary,smsKey,msgArray);
						try {
							PushMessage pushMessage=new PushMessage(phoneVO.getMsisdn(),senderMessage,null,null,localePrimary);
							pushMessage.push();
						} catch (Exception e) {
							logger.errorTrace(methodName, e);
						}
					}
					
				} catch (Exception e) {
					logger.errorTrace(methodName, e);				 
				}
			}
		    else
		    {
		    	con.rollback();
		    	redempVO.setTxnStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
		    }
		 }
		 catch (Exception e)
		 {
			 logger.errorTrace(methodName, e);
			 try{
				 con.rollback();
			 }catch(Exception e1){
				 logger.errorTrace(methodName, e1);
			}
		 }
		 finally
		 { 
			 if (logger.isDebugEnabled())
				 logger.debug(methodName, "Exited  ID ="+redempVO.getReferenceNo()+", channelUserVO.getMsisdn()="+channelUserVO.getMsisdn()+", channelUserVO.getUserID()="+channelUserVO.getUserID());
			 
		 }
	}

	public  static UserBalancesVO constructBalanceVOFromTxnVO(ChannelTransferVO p_channelTransferVO)
	{
		final String methodName="constructBalanceVOFromTxnVO";
		if (logger.isDebugEnabled())
			logger.debug(methodName, "Entered:NetworkStockTxnVO=>"+ p_channelTransferVO);
		UserBalancesVO userBalancesVO = new UserBalancesVO();
		userBalancesVO.setLastTransferType(p_channelTransferVO.getTransferType());
		userBalancesVO.setLastTransferID(p_channelTransferVO.getTransferID());
		userBalancesVO.setLastTransferOn(p_channelTransferVO.getTransferDate());
		if (logger.isDebugEnabled())
			logger.debug(methodName, "Exiting userBalancesVO="+userBalancesVO);
		return userBalancesVO;
	}
	
	public  static long getNextID(Connection p_con,String p_idType,String p_year,ChannelTransferVO p_channelTransferVO) throws BTSLBaseException
	{
		try
		{
			IDGeneratorDAO _idGeneratorDAO = new IDGeneratorDAO();
			long id=_idGeneratorDAO.getNextID(p_con,p_idType,p_year,p_channelTransferVO);
			return id;
		}
		finally
		{
			if(p_con!=null)
			{
				try{p_con.commit();}catch(Exception e){logger.error("getNextID", "Exception =" + e);}
			}
		}
	}

	 private  static void genrateOprtToChnnlTrfID(Connection p_con,ChannelTransferVO p_channelTransferVO) throws BTSLBaseException
	 {
		final String methodName="genrateOprtToChnnlTrfID";
		if (logger.isDebugEnabled())
			logger.debug(methodName, "Entered ChannelTransferVO =" + p_channelTransferVO);

		try
		{
		
			long tmpId=getNextID(p_con,PretupsI.CHANNEL_TRANSFER_O2C_ID, BTSLUtil.getFinancialYear() , p_channelTransferVO);
			p_channelTransferVO.setTransferID(calculatorI.formatChannelTransferID(p_channelTransferVO,PretupsI.CHANNEL_TRANSFER_O2C_ID,tmpId));

		}
		catch (Exception e)
		{
			logger.error(methodName, "Exception " + e.getMessage());
			logger.errorTrace(methodName, e);
			
			throw new BTSLBaseException(methodName, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		finally
		{
			if (logger.isDebugEnabled())
				logger.debug(methodName, "Exited  ID ="+p_channelTransferVO.getTransferID());
		}

	}	

}
package com.client.pretups.processes.clientprocesses;
/**
 * @(#)HandleAmbiguousUnsettledCases.java 
 * Copyright(c) 2015, Mahindra Comviva Ltd. 
 * All Rights Reserved
 * 
 * -------------------------------------------------------------------------------------------------
 * Author 				Date 			History
 * -------------------------------------------------------------------------------------------------
 * Rajvi Desai 	April 16, 2015 		Initial Creation
 * 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.logging.BalanceLogger;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferItemVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.logging.HandleUnsettledC2SCasesLog;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.product.businesslogic.NetworkProductServiceTypeCache;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.client.pretups.processes.SFTPDemo;

public class HandleAmbiguousC2SUnsettledCases {
	public static Log LOG = LogFactory.getLog(HandleAmbiguousC2SUnsettledCases.class.getName());
	final static String className="HandleAmbiguousUnsettledCases";
	public static String SUCCESS="SUCCESSLIST";
	public static String FAILED="FAILEDLIST";
	public static HashMap<String, String> _loadConfigParamMap=new HashMap<String,String>();
	public static void main(String args[])
	{
		Connection con=null;
		String specificDate=null;

		final String methodName="main";
		if(args.length!=2 && args.length!=3)
		{
			System.out.println("Usage : "+className +"[Constants file] [LogConfig file] [Specific date dd/MM/YY ] ");
			return;
		}
		File constantsFile = new File(args[0]);
		if(!constantsFile.exists())
		{
		    System.out.println(className +" "+methodName+" Constants file not found on location:: "+constantsFile.toString());
			return;
		}
		File logconfigFile = new File(args[1]);
		if(!logconfigFile.exists())
		{
			System.out.println(className +" Logconfig file not found on location:: "+logconfigFile.toString());
			return;
		}
		if(args.length==3)
			specificDate=args[2];
		try
		{
			ConfigServlet.loadProcessCache(constantsFile.toString(),logconfigFile.toString());
			NetworkProductServiceTypeCache.refreshNetworkProductMapping();
			NetworkProductServiceTypeCache.refreshProductServiceTypeMapping();
			
		}
		catch(Exception e)
		{
			HandleUnsettledC2SCasesLog.log(className, methodName,className+" Not able to load Process Cache");
			ConfigServlet.destroyProcessCache();
			return;
		}
		try
		{ 
		    if(LOG.isDebugEnabled())LOG.debug(methodName,"Entered");
			HandleUnsettledC2SCasesLog.log(className, methodName, "Entered");
				con=OracleUtil.getSingleConnection();
			
			if(con==null)
			{
				if (LOG.isDebugEnabled()) LOG.debug(className+"["+methodName+"]","Not able to get Connection for HandleAmbiguousUnsettledCases : ");
				throw new Exception("Not able to get Oracle database Connection for HandleAmbiguousUnsettledCases");
			}
			
			loadConfigurationParamater();
			// process method for functionality 
				new HandleAmbiguousC2SUnsettledCases().process(con,specificDate);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"["+methodName+"]","","","","Exception:"+e.getMessage());
			if(con!=null) try{con.rollback();}catch(Exception ex){}
		}
		finally
		{
			if(con!=null)
			{
				try{con.close();}catch(Exception e){}
			}
			try{Thread.sleep(5000);}catch(Exception e){e.printStackTrace();}
			ConfigServlet.destroyProcessCache();
			HandleUnsettledC2SCasesLog.log(className, methodName, "Exited");
		}
	}
	
	/**
	 * Method process
	 * This method is used to initialize process
	 * @param p_con
	 * @param p_date
	 */
	void process(Connection p_con,String p_date) throws Exception
	{
		final String methodName = "process";
		if (LOG.isDebugEnabled())
			LOG.debug(methodName, "Entered p_date" + p_date);
		HandleUnsettledC2SCasesLog.log(className, methodName, "Entered p_date" + p_date);

		BufferedReader finalBufferReaderIn = null;
		BufferedReader receiverBufferReaderIn = null;
		String executedDateName = null;
		String receivedDateName = null;
		
		try
		{
			// fetch executed upto from related process 
			ProcessStatusVO processStatusVO = new ProcessStatusDAO().loadProcessDetail(p_con,ProcessI.AMB_SERVER_UPDATE);
			if(processStatusVO==null)
			{
				if (LOG.isDebugEnabled()) LOG.debug(className+"["+methodName+"]","Not able to get Connection for HandleAmbiguousUnsettledCases : ");
				throw new Exception("Not able to get Oracle database Connection for HandleAmbiguousUnsettledCases");
			}
		
			 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			 if(!BTSLUtil.isNullString(p_date)){
				 Date fileDate = formatter.parse(p_date);
				 executedDateName= new SimpleDateFormat("yyyyMMdd").format(fileDate); 
				 receivedDateName=formatter.format(fileDate);
			 }
			 else
			 {
				 executedDateName= new SimpleDateFormat("yyyyMMdd").format(processStatusVO.getExecutedUpto());
				 receivedDateName=formatter.format(processStatusVO.getExecutedUpto());
			 }
			// FTP process 
		 	new SFTPDemo().initaliseProcess(_loadConfigParamMap, receivedDateName);
		
		 	String finalFilePath=(_loadConfigParamMap.get("AMB_FILE_FINAL_PATH")+_loadConfigParamMap.get("AMB_FILE_NAME")+"_"+executedDateName+"."+_loadConfigParamMap.get("AMB_FILE_EXTN"));
		 	String receivedFilePath=(_loadConfigParamMap.get("FTP_AMB_RECV_DIR")+_loadConfigParamMap.get("AMB_SERVER_FILE_NAME")+"_"+receivedDateName+"."+_loadConfigParamMap.get("AMB_FILE_EXTN"));
		 	HandleUnsettledC2SCasesLog.log(className, methodName, "finalFilePath@@ "+finalFilePath+", receivedFilePath## "+receivedFilePath+" , specificDate%% "+p_date);
		     
		     File finalFile = new File(finalFilePath);
		     File receivedFile = new File(receivedFilePath);
		     if(finalFile.exists())
		     {
		    	 finalBufferReaderIn = new BufferedReader(new FileReader(finalFilePath));//source(old file) 
		    	 if(receivedFile.exists())
		    	 {
		    		receiverBufferReaderIn = new BufferedReader(new FileReader(receivedFilePath));
		    		HashMap<String, ArrayList<String>> map=readDataAndPutInObject(p_con,finalBufferReaderIn,receiverBufferReaderIn,",");
		    		receiverBufferReaderIn.close();
		    		this.handleChannelAmbigousCases(p_con,map);
		    	 }//destination(new file)
		    	 else
		    	 {
		    		 HandleUnsettledC2SCasesLog.log(className, methodName, "\nNo file found, Please upload the file at BI server end for Date: "+processStatusVO.getExecutedUpto());
		    	 }
		    	 finalBufferReaderIn.close();
		     }
		     else
		    	 HandleUnsettledC2SCasesLog.log(className, methodName, "\n No Ambiguous Transaction's were found for date: "+processStatusVO.getExecutedUpto());
		}
		catch(Exception e){
			HandleUnsettledC2SCasesLog.log(className, methodName, "Exited");
			if(LOG.isDebugEnabled())LOG.error(methodName,"exception occured "+e.getMessage());
			throw e;
		}
		finally
		{
			if(finalBufferReaderIn!=null)
			{
				try
				{
					finalBufferReaderIn.close(); 
				}catch(Exception e){
					if(LOG.isDebugEnabled())LOG.debug(methodName,"Exception "+e.getMessage());
					
				}
					finalBufferReaderIn=null;
			}
			if(receiverBufferReaderIn!=null){
				try
				{
					receiverBufferReaderIn.close();
				}catch(Exception e)
				{
						if(LOG.isDebugEnabled())LOG.debug(methodName,"Exception "+e.getMessage());
				}
				receiverBufferReaderIn=null;
			}
			HandleUnsettledC2SCasesLog.log(className, methodName, "Exited");
			if(LOG.isDebugEnabled())LOG.debug(methodName,"Exited");
		}
	}
	/**
	 * readDataAndPutInObject
	 * Method to read the file and store in the list
	 * @param p_br
	 * @param q_br
	 * @param p_separator
	 * @author rajvi desai
	 */
	public HashMap<String, ArrayList<String>>  readDataAndPutInObject(Connection p_con,BufferedReader p_br,BufferedReader q_br,String p_separator)
	{
		final String methodName="readDataAndPutInObject";
		String str=null;
		String refID,transID=null;
		HashMap<String, ArrayList<String>> hashMap=new HashMap<String, ArrayList<String>>();
		ArrayList<String> successList =null;
		ArrayList<String> failList=new ArrayList<String>();
		ArrayList<String> refIDList =new ArrayList<String>();
		try
		{	
			HandleUnsettledC2SCasesLog.log(className, methodName, "Entered");
			str = p_br.readLine();
			while(p_br.ready()&& (str = p_br.readLine()) != null)
			{
				if(BTSLUtil.isNullString(str))
					continue;
				if(str.indexOf(p_separator)==-1)
				{
					HandleUnsettledC2SCasesLog.log(className, methodName, "Skipping entry ("+str+") from file as separator ("+p_separator+") not found");
					continue;
				}
				if(new StringTokenizer(str,p_separator).countTokens()<2)
				{
					HandleUnsettledC2SCasesLog.log(className, methodName, "Skipping entry ("+str+") from file as Less than 2 tokens found");
					continue;
				}
					
				transID=str.substring(0,str.indexOf(p_separator));
				//Preparing Fail List based on source file
				failList.add(transID);				
			}
			
			str = q_br.readLine();
			while(q_br.ready()&& (str = q_br.readLine()) != null)
			{
				if(BTSLUtil.isNullString(str))
					continue;
				if(str.indexOf(p_separator)==-1)
				{
					HandleUnsettledC2SCasesLog.log(className, methodName, "Skipping entry ("+str+") from file as separator ("+p_separator+") not found");
					continue;
				}
				if(new StringTokenizer(str,p_separator).countTokens()<2)
				{
					HandleUnsettledC2SCasesLog.log(className, methodName, "Skipping entry ("+str+") from file as Less than 2 tokens found");
					continue;
				}
				refID=str.substring(str.lastIndexOf(p_separator)+1,str.trim().length());
				refIDList.add(refID);
			}
			//Preparing Success List based on Destination file
			C2STransferDAO c2sTransferDAO = new C2STransferDAO();
			successList=c2sTransferDAO.getIDList(p_con,refIDList);
			for(int i=0;i<successList.size();i++){
				//Updating Fail List based on Destination file
				failList.remove(successList.get(i));
			}
			//Finalizing the List 			
			hashMap.put(SUCCESS, successList);
			hashMap.put(FAILED, failList);
			hashMap.put("REFIDLIST", refIDList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+"["+methodName+"]","","","","Exception:"+e.getMessage());
		}
		finally
		{
			HandleUnsettledC2SCasesLog.log(className, methodName, "Exited map"+hashMap);
		}
		
		return hashMap;
	}

	/**
	 * Method handleChannelAmbigousCases
	 * This Method handle Cases : Success as well as Fail
	 * @param p_con
	 * @param p_failList
	 * @param p_successList
	 * @author rajvi desai
	 */
	public void handleChannelAmbigousCases(Connection p_con,HashMap<String, ArrayList<String>> p_map)
	{
		final String methodName="handleChannelAmbigousCases";
		ArrayList<String> successList=p_map.get(SUCCESS);
		ArrayList<String> failedList=p_map.get(FAILED);
		if(LOG.isDebugEnabled()) LOG.debug(className,"Entered with p_failList.size="+failedList.size()+" p_successList.size()="+successList.size());
		HandleUnsettledC2SCasesLog.log(className, methodName, "Entered with p_failList.size="+failedList.size()+" p_successList.size()="+successList.size());
		try
		{
			reconcileTransactionList(p_con,PretupsErrorCodesI.TXN_STATUS_FAIL,"Fail",failedList);
			reconcileTransactionList(p_con,PretupsErrorCodesI.TXN_STATUS_SUCCESS,"Success",successList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+"["+methodName+"]","","","","Exception:"+e.getMessage());
		}
	}
	/**
	 * Method to perform reconcilation process
	 * @param p_con
	 * @param p_status
	 * @param p_list
	 * @param p_statusTxt
	 */
	public void reconcileTransactionList(Connection p_con,String p_status,String p_statusTxt,ArrayList<String> p_list)
	{
		
		final String methodName="reconcileTransactionList";
		if(LOG.isDebugEnabled()) LOG.debug(methodName,"Entered with p_status="+p_status+" p_statusTxt="+p_statusTxt+" p_list.size="+p_list.size());
		HandleUnsettledC2SCasesLog.log(className,methodName, "Entered with p_status="+p_status+" p_statusTxt="+p_statusTxt+" p_list.size="+p_list.size());
		try
		{
			String transactionID=null;
			C2STransferVO c2sTransferVO=null;
			C2STransferItemVO receiverItemVO=null;
			int updateCount=0;
			java.util.Date currentDate=new Date();
			C2STransferDAO c2sTransferDAO = new C2STransferDAO();
			for(int i=0;i<p_list.size();i++)
			{
				transactionID=p_list.get(i);
				if(LOG.isDebugEnabled()) LOG.debug(methodName,"Got transactionID="+transactionID);		
				updateCount=0;
				receiverItemVO=null;
				try
				{
					if(i>2000) //Thread Sleep to avoid the Open Cursor Issue.
						Thread.sleep(100);
					c2sTransferVO=loadC2STransferVO(p_con,transactionID);
					if(c2sTransferVO==null)
					{
						 LOG.info(methodName,"For transactionID="+transactionID+" No information available in transfers table or already settled");
	 					 EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,className+"["+methodName+"]",transactionID,"","","No information available in transfers table or already settled");
						 continue;
					}
					if(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS.equals(c2sTransferVO.getTxnStatus()))
					{
						updateCount=c2sTransferDAO.markC2SReceiverAmbiguous(p_con,c2sTransferVO.getTransferID());
						receiverItemVO=(C2STransferItemVO)c2sTransferVO.getTransferItemList().get(1);
						EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, className+"["+methodName+"]",c2sTransferVO.getTransferID(),"", "", "Receiver transfer status changed to '250' from "+receiverItemVO.getTransferStatus());
						receiverItemVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
					}
					c2sTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
					c2sTransferVO.setTransferStatus(p_status);
					c2sTransferVO.setModifiedOn(currentDate);				
					ArrayList<?> newEntries=ChannelUserBL.prepareNewC2SReconList(p_con,c2sTransferVO,p_statusTxt,null);
					c2sTransferVO.setTransferItemList(newEntries);
					updateCount=c2sTransferDAO.updateReconcilationStatus(p_con,c2sTransferVO);
					if (updateCount>0)
					{
						p_con.commit();
						if(c2sTransferVO.getOtherInfo1()!=null)
							BalanceLogger.log((UserBalancesVO)c2sTransferVO.getOtherInfo1());
						// if differential commission is given by the reconciliation then add the balance logger into the system.
						if(c2sTransferVO.getOtherInfo2()!= null)
							BalanceLogger.log((UserBalancesVO)c2sTransferVO.getOtherInfo2());
						if(LOG.isDebugEnabled()) LOG.debug(methodName,"TransactionID="+transactionID+" Succesfully Settled to status="+p_status);		
					}
					else
					{
						p_con.rollback();
						 LOG.error(methodName,"TransactionID="+transactionID+" Not able Settled to status="+p_status);
	 					 EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,className+"["+methodName+"]",transactionID,"","","Not able Settled to status="+p_status);
					}
				}
				catch(BTSLBaseException be)
				{
					be.printStackTrace();
					p_con.rollback();
					LOG.error(methodName,"TransactionID="+transactionID+" Not able Settled to status="+p_status+" getting Exception="+be.getMessage());
 					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,className+"["+methodName+"]",transactionID,"","","Not able Settled to status="+p_status+" getting Exception="+be.getMessage());
				}
				catch(Exception e)
				{
					e.printStackTrace();
					p_con.rollback();
					LOG.error(methodName,"TransactionID="+transactionID+" Not able Settled to status="+p_status+" getting Exception="+e.getMessage());
 					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,className+"["+methodName+"]",transactionID,"","","Not able Settled to status="+p_status+" getting Exception="+e.getMessage());
				}
								
			}
		}
		catch(Exception e)
		{
			LOG.error(methodName, "Exception : " + e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+"["+methodName+"]","","","","Exception:"+e.getMessage());
		}
		finally
		{
			HandleUnsettledC2SCasesLog.log(className, methodName,"Exited");
		}
	}

	/**
	 * Method loadC2STransferVO
	 * This method to load the TransferVO based on transfer ID
	 * @param p_con
	 * @param p_transferID
	 * @return
	 */
	public C2STransferVO loadC2STransferVO(Connection p_con,String p_transferID)
	{
		final String methodName="loadC2STransferVO";
		if(LOG.isDebugEnabled()) LOG.debug(methodName,"Entered p_transferID="+p_transferID);
		HandleUnsettledC2SCasesLog.log(className, methodName,"Entered p_transferID="+p_transferID);
		PreparedStatement pstmtSelect=null;
		ResultSet rs=null;
		C2STransferVO c2sTransferVO=null;
		ChannelUserVO channelUserVO= null;
		UserPhoneVO userPhoneVO=null;
		try
		{
			C2STransferDAO c2sTransferDAO=new C2STransferDAO();
			StringBuffer selectQueryBuff =new StringBuffer();
			
			selectQueryBuff.append("SELECT KV.value,KV1.value txn_status,U.user_name,ST.name, PROD.short_name, CTRF.transfer_id, ");
			selectQueryBuff.append("CTRF.transfer_date, CTRF.transfer_date_time, CTRF.network_code, sender_id,");
			selectQueryBuff.append("CTRF.sender_category, CTRF.product_code, CTRF.sender_msisdn, CTRF.receiver_msisdn, ");
			selectQueryBuff.append("CTRF.receiver_network_code, CTRF.transfer_value, CTRF.error_code, CTRF.request_gateway_type, ");
			selectQueryBuff.append("CTRF.request_gateway_code, CTRF.reference_id, CTRF.service_type, CTRF.differential_applicable, ");
			selectQueryBuff.append("CTRF.pin_sent_to_msisdn, CTRF.language, CTRF.country, CTRF.skey, CTRF.skey_generation_time, ");
			selectQueryBuff.append("CTRF.skey_sent_to_msisdn, CTRF.request_through_queue, CTRF.credit_back_status, CTRF.quantity, ");
			selectQueryBuff.append("CTRF.reconciliation_flag, CTRF.reconciliation_date, CTRF.reconciliation_by, CTRF.created_on, ");
			selectQueryBuff.append("CTRF.created_by, CTRF.modified_on, CTRF.modified_by, CTRF.transfer_status, CTRF.card_group_set_id, ");
			selectQueryBuff.append("CTRF.version, CTRF.card_group_id, CTRF.sender_transfer_value, CTRF.receiver_access_fee, ");
			selectQueryBuff.append("CTRF.receiver_tax1_type, CTRF.receiver_tax1_rate, CTRF.receiver_tax1_value, CTRF.receiver_tax2_type,");
			selectQueryBuff.append("CTRF.receiver_tax2_rate, CTRF.receiver_tax2_value, CTRF.receiver_validity, CTRF.receiver_transfer_value,");
			selectQueryBuff.append("CTRF.receiver_bonus_value, CTRF.receiver_grace_period, CTRF.receiver_bonus_validity, ");
			selectQueryBuff.append("CTRF.card_group_code, CTRF.receiver_valperiod_type, CTRF.temp_transfer_id, CTRF.transfer_profile_id,");
			selectQueryBuff.append("CTRF.commission_profile_id, CTRF.differential_given, CTRF.grph_domain_code, CTRF.source_type,U.owner_id ");
			selectQueryBuff.append(", UP.phone_language, UP.msisdn, UP.country phcountry ");
			selectQueryBuff.append("FROM c2s_transfers CTRF, products PROD,service_type ST,users U,key_values KV,key_values KV1,user_phones UP   ");
			selectQueryBuff.append("WHERE U.user_id = UP.user_id AND UP.primary_number='Y' AND U.user_id = CTRF.sender_id AND KV.key(+)=CTRF.error_code AND KV.type(+)=? ");
			selectQueryBuff.append("AND KV1.key(+)=CTRF.transfer_status AND KV1.type(+)=? ");			
			selectQueryBuff.append("AND CTRF.product_code=PROD.product_code ");
			selectQueryBuff.append("AND (CTRF.reconciliation_flag <> 'Y' OR CTRF.reconciliation_flag IS NULL ) ");
			selectQueryBuff.append("AND ST.service_type=CTRF.service_type ");
			selectQueryBuff.append("AND (CTRF.transfer_status=? OR CTRF.transfer_status=? ) ");
			selectQueryBuff.append("AND CTRF.transfer_id=? ");

			String selectQuery=selectQueryBuff.toString();
			if(LOG.isDebugEnabled())LOG.debug(methodName,"select query:"+selectQuery);		
			HandleUnsettledC2SCasesLog.log(className, methodName,"select query:"+selectQuery);
			pstmtSelect = p_con.prepareStatement(selectQuery);
			int i=1;
			pstmtSelect.setString(i++, PretupsI.C2S_ERRCODE_VALUS);
			pstmtSelect.setString(i++, PretupsI.KEY_VALUE_TYPE_REOCN);
			pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
			pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
			pstmtSelect.setString(i++, p_transferID);
			rs = pstmtSelect.executeQuery();
			if(rs.next())
			{
				c2sTransferVO =new C2STransferVO();
				
				c2sTransferVO.setProductName(rs.getString("short_name"));
				c2sTransferVO.setServiceName(rs.getString("name"));
				c2sTransferVO.setSenderName(rs.getString("user_name"));
				c2sTransferVO.setOwnerUserID(rs.getString("owner_id"));
				c2sTransferVO.setErrorMessage(rs.getString("value"));
				c2sTransferVO.setTransferID(rs.getString("transfer_id"));
				c2sTransferVO.setTransferDate(rs.getDate("transfer_date"));
				c2sTransferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
				c2sTransferVO.setTransferDateStr(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time")));
				c2sTransferVO.setNetworkCode(rs.getString("network_code"));
				c2sTransferVO.setSenderNetworkCode(rs.getString("network_code"));
				c2sTransferVO.setSenderID(rs.getString("sender_id"));
				c2sTransferVO.setProductCode(rs.getString("product_code"));
				c2sTransferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
				c2sTransferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
				c2sTransferVO.setReceiverNetworkCode(rs.getString("receiver_network_code"));
				c2sTransferVO.setTransferValue(rs.getLong("transfer_value"));
				c2sTransferVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
				c2sTransferVO.setErrorCode(rs.getString("error_code"));
				c2sTransferVO.setRequestGatewayType(rs.getString("request_gateway_type"));
				c2sTransferVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
				c2sTransferVO.setReferenceID(rs.getString("reference_id"));
				c2sTransferVO.setServiceType(rs.getString("service_type"));
				c2sTransferVO.setDifferentialApplicable(rs.getString("differential_applicable"));
				c2sTransferVO.setPinSentToMsisdn(rs.getString("pin_sent_to_msisdn"));
				c2sTransferVO.setLanguage(rs.getString("language"));
				c2sTransferVO.setCountry(rs.getString("country"));
				c2sTransferVO.setSkey(rs.getLong("skey"));
				c2sTransferVO.setSkeyGenerationTime(rs.getDate("skey_generation_time"));
				c2sTransferVO.setSkeySentToMsisdn(rs.getString("skey_sent_to_msisdn"));
				c2sTransferVO.setRequestThroughQueue(rs.getString("request_through_queue"));
				c2sTransferVO.setCreditBackStatus(rs.getString("credit_back_status"));
				c2sTransferVO.setQuantity(rs.getLong("quantity"));
				c2sTransferVO.setReconciliationFlag(rs.getString("reconciliation_flag"));
				c2sTransferVO.setReconciliationDate(rs.getDate("reconciliation_date"));
				c2sTransferVO.setReconciliationBy(rs.getString("reconciliation_by"));
				c2sTransferVO.setCreatedOn(rs.getDate("created_on"));
				c2sTransferVO.setCreatedBy(rs.getString("created_by"));
				c2sTransferVO.setModifiedOn(rs.getDate("modified_on"));
				c2sTransferVO.setModifiedBy(rs.getString("modified_by"));
				c2sTransferVO.setTransferStatus(rs.getString("txn_status"));
				c2sTransferVO.setCardGroupSetID(rs.getString("card_group_set_id"));
				c2sTransferVO.setVersion(rs.getString("version"));
				c2sTransferVO.setCardGroupID(rs.getString("card_group_id"));
				c2sTransferVO.setSenderTransferValue(rs.getLong("sender_transfer_value"));
				c2sTransferVO.setReceiverAccessFee(rs.getLong("receiver_access_fee"));
				c2sTransferVO.setReceiverTax1Type(rs.getString("receiver_tax1_type"));
				c2sTransferVO.setReceiverTax1Rate(rs.getDouble("receiver_tax1_rate"));
				c2sTransferVO.setReceiverTax1Value(rs.getLong("receiver_tax1_value"));
				c2sTransferVO.setReceiverTax2Type(rs.getString("receiver_tax2_type"));
				c2sTransferVO.setReceiverTax2Rate(rs.getDouble("receiver_tax2_rate"));
				c2sTransferVO.setReceiverTax2Value(rs.getLong("receiver_tax2_value"));
				c2sTransferVO.setReceiverValidity(rs.getInt("receiver_validity"));
				c2sTransferVO.setReceiverTransferValue(rs.getLong("receiver_transfer_value"));
				c2sTransferVO.setReceiverBonusValue(rs.getLong("receiver_bonus_value"));
				c2sTransferVO.setReceiverGracePeriod(rs.getInt("receiver_grace_period"));
				c2sTransferVO.setReceiverBonusValidity(rs.getInt("receiver_bonus_validity"));
				c2sTransferVO.setCardGroupCode(rs.getString("card_group_code"));
				c2sTransferVO.setReceiverValPeriodType(rs.getString("receiver_valperiod_type"));
				c2sTransferVO.setDifferentialGiven(rs.getString("differential_given"));
				c2sTransferVO.setGrphDomainCode(rs.getString("grph_domain_code"));
				c2sTransferVO.setTxnStatus(rs.getString("transfer_status"));
				c2sTransferVO.setSourceType(rs.getString("source_type"));
				channelUserVO = new ChannelUserVO();
				channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
				channelUserVO.setCommissionProfileSetID(rs.getString("commission_profile_id"));
				channelUserVO.setCategoryCode(rs.getString("sender_category"));
				userPhoneVO =  new UserPhoneVO();
				userPhoneVO.setCountry(rs.getString("phcountry"));
				userPhoneVO.setPhoneLanguage(rs.getString("phone_language"));
				userPhoneVO.setMsisdn(rs.getString("msisdn"));
				userPhoneVO.setLocale(new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry()));
				channelUserVO.setUserPhoneVO(userPhoneVO);
				c2sTransferVO.setSenderVO(channelUserVO);
				c2sTransferVO.setTransferItemList(c2sTransferDAO.loadC2STransferItemsVOList(p_con,p_transferID));
			}

		}//end of try
		catch (SQLException sqle)
		{
			LOG.error(methodName,"SQLException "+sqle.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+"[loadC2STransferVO]","","","","SQL Exception:"+sqle.getMessage());
		}//end of catch
		catch (Exception e)
		{
			LOG.error(methodName,"Exception "+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+"[loadC2STransferVO]","","","","Exception:"+e.getMessage());
		}//end of catch
		finally
		{
			try{if(rs!=null) rs.close();}catch(Exception e){}
			try{if(pstmtSelect!=null) pstmtSelect.close();}catch(Exception e){}
			if(LOG.isDebugEnabled())LOG.debug(methodName,"Exiting ");
			HandleUnsettledC2SCasesLog.log(className,methodName,"Exiting");
		 }//end of finally
		
		return c2sTransferVO;
	}
	
	/**
	 * Method loadConfigurationParamater
	 * This method to load parameters from constants.props file
	 */
	private static void loadConfigurationParamater() throws BTSLBaseException{

		final String methodName="loadConfigurationParamater";
		if (LOG.isDebugEnabled())
			LOG.debug(methodName," Entered: ");
		try
		{
			
			_loadConfigParamMap.put("MODULE_TYPE",PretupsI.C2S_MODULE);
			
			String hostName=Constants.getProperty("FTP_AMB_SERVER_IP");
			if(BTSLUtil.isNullString(hostName))
			{
				LOG.error(methodName," Could not find file label for hostName in the Constants file.");
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","hostName:"+hostName);
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
             }
			else
				_loadConfigParamMap.put("FTP_AMB_SERVER_IP",hostName.trim());
			
			
			String encryAllow = Constants.getProperty("FTP_AMB_FILE_ENCRY_ALLOW").trim();
			if (BTSLUtil.isNullString(encryAllow))
			{
				LOG.error(className,"FTP_AMB_FILE_ENCRY_ALLOW is not defined in Constant. props ");
				throw new BTSLBaseException(PretupsErrorCodesI.AMB_CONSTANT_ENTRY_MISSING);
			}
			_loadConfigParamMap.put("FTP_AMB_FILE_ENCRY_ALLOW", encryAllow.trim());

			String userName=Constants.getProperty("FTP_AMB_USER_NAME").trim();
			if(BTSLUtil.isNullString(userName))
			{
				LOG.error(methodName," Could not find file label for userName in the Constants file.");
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","userName:"+userName);
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
			}
			//else
				//_loadConfigParamMap.put("FTP_AMB_USER_NAME",userName.trim());
			
			String password=Constants.getProperty("FTP_AMB_PASSWD").trim();
			if(BTSLUtil.isNullString(password))
			{
				LOG.error(methodName," Could not find file label for password in the Constants file.");
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","password:"+password);
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
			}
			//else
				//_loadConfigParamMap.put("FTP_AMB_PASSWD",password.trim());
			
			if(encryAllow.equalsIgnoreCase("Y"))
			{
				userName=BTSLUtil.decryptText(userName.trim());
				password=BTSLUtil.decryptText(password.trim());
			}
			_loadConfigParamMap.put("FTP_AMB_USER_NAME",userName.trim());
			_loadConfigParamMap.put("FTP_AMB_PASSWD",password.trim());
			String destinationDir = Constants.getProperty("FTP_AMB_SRC_DIR").trim();
			if(BTSLUtil.isNullString(destinationDir))
			{
				LOG.error(methodName," Could not find file label for destinationDir in the Constants file.");
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","destinationDir:"+destinationDir);
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
			}
			else
				_loadConfigParamMap.put("FTP_AMB_SRC_DIR",destinationDir.trim());
			
			String sourceFile = Constants.getProperty("FTP_AMB_RECV_DIR").trim();
			if(BTSLUtil.isNullString(sourceFile))
			{
				LOG.error(methodName," Could not find file label for sourceFile in the Constants file.");
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","sourceFile:"+sourceFile);
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
		    }
			else
				_loadConfigParamMap.put("FTP_AMB_RECV_DIR",sourceFile.trim());
			
			String filename = Constants.getProperty("AMB_SERVER_FILE_NAME").trim();
			if(BTSLUtil.isNullString(filename))
			{
				LOG.error(methodName," Could not find file label for filename in the Constants file.");
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","filename:"+filename);
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
			}
			else
				_loadConfigParamMap.put("AMB_SERVER_FILE_NAME",filename.trim());
			
			String movePath = Constants.getProperty("SERVER_MOVE_PATH").trim();
			if(BTSLUtil.isNullString(movePath))
			{
				LOG.error(methodName," Could not find file label for movePath in the Constants file.");
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","movePath:"+movePath);
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
			}
			else
				_loadConfigParamMap.put("SERVER_MOVE_PATH",movePath.trim());
			
			String ftpPort = Constants.getProperty("FTP_AMB_PORT").trim();
			if(BTSLUtil.isNullString(ftpPort))
			{
				LOG.error(methodName," Could not find file label for ftpPort in the Constants file.");
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","ftpPort:"+ftpPort);
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
			}
			else
				_loadConfigParamMap.put("FTP_AMB_PORT",ftpPort.trim());
			
			String ambFileFinalPath = Constants.getProperty("AMB_FILE_FINAL_PATH").trim();
			if(BTSLUtil.isNullString(ambFileFinalPath))
			{
				LOG.error(methodName," Could not find file label for ambFileFinalPath in the Constants file.");
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","ambFileFinalPath:"+ambFileFinalPath);
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
			}
			else
				_loadConfigParamMap.put("AMB_FILE_FINAL_PATH",ambFileFinalPath.trim());
			
			String ambFileName = Constants.getProperty("AMB_FILE_NAME").trim();
			if(BTSLUtil.isNullString(ambFileName))
			{
				LOG.error(methodName," Could not find file label for ambFileName in the Constants file.");
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","ambFileName:"+ambFileName);
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
			}
			else
				_loadConfigParamMap.put("AMB_FILE_NAME",ambFileName.trim());
			
			String ambFileExtn = Constants.getProperty("AMB_FILE_EXTN").trim();
			if(BTSLUtil.isNullString(ambFileExtn))
			{
				LOG.error(methodName," Could not find file label for ambFileExtn in the Constants file.");
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","ambFileExtn:"+ambFileExtn);
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
			}
			else
				_loadConfigParamMap.put("AMB_FILE_EXTN",ambFileExtn.trim());
			
			String ftpAmbRecDir = Constants.getProperty("FTP_AMB_RECV_DIR").trim();
			if(BTSLUtil.isNullString(ftpAmbRecDir))
			{
				LOG.error(methodName," Could not find file label for ftpAmbRecDir in the Constants file.");
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","ftpAmbRecDir:"+ftpAmbRecDir);
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
			}
			else
				_loadConfigParamMap.put("FTP_AMB_RECV_DIR",ftpAmbRecDir.trim());
			
			String ambServerFileName = Constants.getProperty("AMB_SERVER_FILE_NAME").trim();
			if(BTSLUtil.isNullString(ambServerFileName))
			{
				LOG.error(methodName," Could not find file label for ambServerFileName in the Constants file.");
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","ambServerFileName:"+ambServerFileName);
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
			}
			else
				_loadConfigParamMap.put("AMB_SERVER_FILE_NAME",ambServerFileName.trim());
		}
		catch(BTSLBaseException be)
		{
			LOG.error(methodName, "BTSLBaseException : " + be.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","Message:"+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			LOG.error(methodName, "Exception : " + e.getMessage());
			BTSLMessages btslMessage=new BTSLMessages(PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","Message:"+btslMessage);
		    throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
		}	
	}
	
	/**
	 * 
	 * method markC2SReceiverAmbiguous
	 * This method is used in the C2S Reconciliation module, by this method receiver's transfer status is updated
	 * as ambigous and previous transfer status is assigned to the update status.
	 * @param p_con
	 * @param p_transferID
	 * @return
	 * @throws BTSLBaseException int
	 * @author sandeep.goel ID REC001
	 */
	public int markC2SReceiverAmbiguous(Connection p_con, String p_transferID) throws BTSLBaseException
    {
        if (LOG.isDebugEnabled())
            LOG.debug("markC2SReceiverAmbiguous", "Entered p_transferID:" + p_transferID);
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        try
        {
            int i = 1;
            StringBuffer updateQueryBuff = new StringBuffer(" UPDATE c2s_transfer_items ");
            updateQueryBuff.append("SET update_status=transfer_status,transfer_status=? WHERE  transfer_id=? AND user_type=? ");
            String updateQuery = updateQueryBuff.toString();
            if (LOG.isDebugEnabled())
                LOG.debug("updateTransferItemDetails", "Update query:" + updateQuery);

            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate.setString(i++, InterfaceErrorCodesI.AMBIGOUS);
            pstmtUpdate.setString(i++, p_transferID);
            pstmtUpdate.setString(i++, PretupsI.USER_TYPE_RECEIVER);
               updateCount = pstmtUpdate.executeUpdate();
            if (updateCount <= 0)
                throw new BTSLBaseException(this, "markC2SReceiverAmbiguous", "error.general.sql.processing");
            return updateCount;
        }// end of try
        catch (SQLException sqle)
        {
            LOG.error("markC2SReceiverAmbiguous", "SQLException " + sqle.getMessage());
            updateCount = 0;
            sqle.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[markC2SReceiverAmbiguous]", p_transferID,"", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "markC2SReceiverAmbiguous", "error.general.sql.processing");
        }// end of catch
        catch (Exception e)
        {
            LOG.error("markC2SReceiverAmbiguous", "Exception " + e.getMessage());
            updateCount = 0;
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferDAO[markC2SReceiverAmbiguous]", p_transferID,"", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "markC2SReceiverAmbiguous", "error.general.processing");
        }// end of catch
        finally
        {
            try{if (pstmtUpdate != null)pstmtUpdate.close();} catch (Exception e){}
            if (LOG.isDebugEnabled())
                LOG.debug("markC2SReceiverAmbiguous", "Exiting updateCount=" + updateCount);
        }// end of finally
    }
}
package com.client.pretups.processes.clientprocesses;

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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.p2p.logging.HandleUnsettledP2PCasesLog;
import com.btsl.pretups.p2p.reconciliation.businesslogic.ReconciliationBL;
import com.btsl.pretups.p2p.transfer.businesslogic.P2PTransferVO;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.product.businesslogic.NetworkProductServiceTypeCache;
import com.btsl.pretups.transfer.businesslogic.TransferDAO;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.client.pretups.processes.SFTPDemo;
/**
 * @(#)HandleAmbiguousUnsettledP2PCases.java 
 * Copyright(c) 2015, Mahindra Comviva Ltd. 
 * All Rights Reserved
 * 
 * -------------------------------------------------------------------------------------------------
 * Author 				Date 			History
 * -------------------------------------------------------------------------------------------------
 * Rajvi Desai 	April 16, 2015 		Initial Creation
 * 
 */

public class HandleAmbiguousUnsettledP2PCases {
	public static Log LOG = LogFactory.getLog(HandleAmbiguousUnsettledP2PCases.class.getName());
	final static String className="HandleAmbiguousUnsettledP2PCases";
	//public static long _sleepTime=100;
	public static String SUCCESS="SUCCESSLIST";
	public static String FAILED="FAILEDLIST";
	public static HashMap<String, String> _loadP2pConfigParamMap=new HashMap<String,String>();
	
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
			System.out.println(className +" "+methodName+" Logconfig file not found on location:: "+logconfigFile.toString());
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
			HandleUnsettledP2PCasesLog.log(className,methodName,className+" "+methodName+" Not able to load Process Cache");
			ConfigServlet.destroyProcessCache();
			return;
		}
		try
		{ 
			if(LOG.isDebugEnabled())LOG.debug(methodName,"Entered");
			HandleUnsettledP2PCasesLog.log(className, methodName, "Entered");
			
			con=OracleUtil.getSingleConnection();
			if(con==null)
			{
				if (LOG.isDebugEnabled())
					LOG.debug("process"," DATABASE Connection is NULL ");
				EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+"["+methodName+"]","","","","DATABASE Connection is NULL");
				return;
			}
			
			loadP2pConfigurationParamater();
			HandleAmbiguousUnsettledP2PCases handleP2PObj=new HandleAmbiguousUnsettledP2PCases();
		
			
			// process method for functionality 
			handleP2PObj.process(con,specificDate);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"[main]","","","","Exception:"+e.getMessage());
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
			HandleUnsettledP2PCasesLog.log(className, methodName, "Exited");
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
		HandleUnsettledP2PCasesLog.log(className, methodName, "Entered p_date"
				+ p_date);

		// Date date=null;
		BufferedReader finalBufferReaderIn = null;
		BufferedReader receiverBufferReaderIn = null;
		String executedDateName = null;
		String receivedDateName = null;
		
		try {
			// fetch executed upto from related process 
			ProcessStatusVO processStatusVO = new ProcessStatusDAO().loadProcessDetail(p_con,ProcessI.AMB_P2P_SERVER_UPDATE);
			if(processStatusVO==null){
				if (LOG.isDebugEnabled()) LOG.debug(className+"["+methodName+"]","Not able to get Connection for HandleAmbiguousUnsettledCases : ");
				throw new Exception("Not able to get Oracle database Connection for HandleAmbiguousUnsettledCases");
			}
		
			 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			 if(!BTSLUtil.isNullString(p_date)){
				 Date fileDate = formatter.parse(p_date);
				 executedDateName= new SimpleDateFormat("yyyyMMdd").format(fileDate); 
				 receivedDateName=formatter.format(fileDate);
			 }
			 else{
				 executedDateName= new SimpleDateFormat("yyyyMMdd").format(processStatusVO.getExecutedUpto());
				 receivedDateName=formatter.format(processStatusVO.getExecutedUpto());
			 }
			 try{
					new SFTPDemo().initaliseProcess(_loadP2pConfigParamMap, receivedDateName);
				 }
				 catch(Exception e){
					 e.printStackTrace();
				 }
				 String finalFilePath=(_loadP2pConfigParamMap.get("AMB_FILE_FINAL_PATH_P2P")+_loadP2pConfigParamMap.get("AMB_P2P_FILE_NAME")+"_"+executedDateName+"."+_loadP2pConfigParamMap.get("AMB_FILE_EXTN"));
				 String receivedFilePath=(_loadP2pConfigParamMap.get("FTP_AMB_P2P_RECV_DIR")+_loadP2pConfigParamMap.get("AMB_P2P_SERVER_FILE_NAME")+"_"+receivedDateName+"."+_loadP2pConfigParamMap.get("AMB_FILE_EXTN"));
				 HandleUnsettledP2PCasesLog.log(className, methodName, "finalP2PFilePath@@ "+finalFilePath+"receivedP2PFilePath## "+receivedFilePath+" specificP2PDate%% "+p_date);
			     
			     File finalFile = new File(finalFilePath);
			     File receivedFile = new File(receivedFilePath);
			     if(finalFile.exists())
			     {
			    	 finalBufferReaderIn = new BufferedReader(new FileReader(finalFilePath));//source(old file) 
			    	 if(receivedFile.exists()){
			    		 receiverBufferReaderIn = new BufferedReader(new FileReader(receivedFilePath));
			    		HashMap<String, ArrayList<String>> map=	readDataAndPutInObject(p_con,finalBufferReaderIn,receiverBufferReaderIn,",");
			    			receiverBufferReaderIn.close();
			    			
			    			this.handleP2PAmbigousCases(p_con,map);
			    	 }//destination(new file)
			    	 else{
			    		 HandleUnsettledP2PCasesLog.log(className, methodName, "\nNo file found, Please upload the file at BI server end for Date: "+processStatusVO.getExecutedUpto()+" finalFile&&& "+finalFile+" receivedFile $$$ "+receivedFile);
			    	 }
			    	 finalBufferReaderIn.close();
			     }
			     else
			    	 HandleUnsettledP2PCasesLog.log(className, methodName, "\n No Ambiguous Transaction's were found for date: "+processStatusVO.getExecutedUpto());
		}
		catch(Exception e){
			HandleUnsettledP2PCasesLog.log(className, methodName, "Exited");
			if(LOG.isDebugEnabled())LOG.error(methodName,"exception occured "+e.getMessage());
			throw e;
		}
		finally
		{
			
			if(finalBufferReaderIn!=null)
			{
				try{
					finalBufferReaderIn.close(); 
				
				}catch(Exception e)
				 {
						if(LOG.isDebugEnabled())LOG.debug(methodName,"Exception "+e.getMessage());
				 }
					finalBufferReaderIn=null;
			}
			if(receiverBufferReaderIn!=null){
				try{
					receiverBufferReaderIn.close();
			    }catch(Exception e)
			     {
						if(LOG.isDebugEnabled())LOG.debug(methodName,"Exception "+e.getMessage());
				 }
					receiverBufferReaderIn=null;
			}
			HandleUnsettledP2PCasesLog.log(className, methodName, "Exited");
			if(LOG.isDebugEnabled())LOG.debug(methodName,"Exited");
		}
	}
	
	/**
	 * Method readDataAndPutInObject
	 * This method is used to read the file and store in the list
	 * @param p_br
	 * @param q_br
	 * @param p_separator
	 */
	public HashMap<String, ArrayList<String>> readDataAndPutInObject(Connection p_con,BufferedReader p_br,BufferedReader q_br,String p_separator)
	{
		//if(_log.isDebugEnabled()) _log.debug("readDataAndPutInObject");
		String methodName="readDataAndPutInObject";
		String str=null;
		String refID,transID=null;
		HashMap<String, ArrayList<String>> hashMap=new HashMap<String, ArrayList<String>>();
		ArrayList<String> successList =null;
		ArrayList<String> failList=new ArrayList<String>();
		ArrayList<String> refIDList =new ArrayList<String>();
		try
		{	
			HandleUnsettledP2PCasesLog.log(methodName, "readDataAndPutInObject", "Entered");
			str = p_br.readLine();
			while(p_br.ready()&& (str = p_br.readLine()) != null)
			{
				if(BTSLUtil.isNullString(str))
					continue;
				if(str.indexOf(p_separator)==-1)
				{
					HandleUnsettledP2PCasesLog.log(className, methodName, "Skipping entry ("+str+") from file as separator ("+p_separator+") not found");
					continue;
				}
				if(new StringTokenizer(str,p_separator).countTokens()<2)
				{
					HandleUnsettledP2PCasesLog.log(className, methodName, "Skipping entry ("+str+") from file as Less than 2 tokens found");
					continue;
				}
					
				transID=str.substring(0,str.indexOf(p_separator));
				  failList.add(transID);
			}
			str = q_br.readLine();
			while(q_br.ready()&& (str = q_br.readLine()) != null)
			{
				if(BTSLUtil.isNullString(str))
					continue;
				if(str.indexOf(p_separator)==-1)
				{
					HandleUnsettledP2PCasesLog.log(className, methodName, "Skipping entry ("+str+") from file as separator ("+p_separator+") not found");
					continue;
				}
				if(new StringTokenizer(str,p_separator).countTokens()<2)
				{
					HandleUnsettledP2PCasesLog.log(className, methodName, "Skipping entry ("+str+") from file as Less than 2 tokens found");
					continue;
				}
					
				refID=str.substring(str.lastIndexOf(p_separator)+1,str.trim().length());
				refIDList.add(refID);
			}
			
			successList=new TransferDAO().getIDList(p_con,refIDList);
			for(int i=0;i<successList.size();i++)
			{
				failList.remove(successList.get(i));
			}
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
			HandleUnsettledP2PCasesLog.log(className, methodName, "Exited");
		}
		return hashMap;
	}

	/**
	 * Method handleP2PAmbigousCases
	 * This Method handle Cases : Success as well as Fail
	 * @param p_con
	 * @param p_failList
	 * @param p_successList
	 * @author rajvi desai
	 */

	public void handleP2PAmbigousCases(Connection p_con,HashMap<String, ArrayList<String>> p_map)
	{
		final String methodName="handleP2PAmbigousCases";
		ArrayList<String> successList=p_map.get(SUCCESS);
		ArrayList<String> failedList=p_map.get(FAILED);
		if(LOG.isDebugEnabled()) LOG.debug(className,"Entered with p_failList.size="+failedList.size()+" p_successList.size()="+successList.size());
		HandleUnsettledP2PCasesLog.log(className, methodName, "Entered with p_failList.size="+failedList.size()+" p_successList.size()="+successList.size());
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
		HandleUnsettledP2PCasesLog.log(className, methodName, "Entered with p_status="+p_status+" p_statusTxt="+p_statusTxt+" p_list.size="+p_list.size());
		try
		{
			String transactionID=null;
			P2PTransferVO p2pTransferVO = null;
			TransferItemVO receiverItemVO=null;
			int updateCount=0;
			TransferDAO transferDAO= new TransferDAO();
			java.util.Date currentDate=new Date();
			for(int i=0;i<p_list.size();i++)
			{
				transactionID=(String)p_list.get(i);
				if(LOG.isDebugEnabled()) LOG.debug(methodName,"Got transactionID="+transactionID);		
				updateCount=0;
				receiverItemVO=null;
				try
				{
					p2pTransferVO=loadP2PReconciliationVO(p_con,transactionID);
					if(p2pTransferVO==null)
					{
						 LOG.info(methodName,"For transactionID="+transactionID+" No information available in transfers table or already settled");
	 					 EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,className+"["+methodName+"]",transactionID,"","","No information available in transfers table or already settled");
						 continue;
					}
					if(PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS.equals(p2pTransferVO.getTxnStatus()))
					{
						updateCount=transferDAO.markP2PReceiverAmbiguous(p_con,transactionID);
						receiverItemVO=(TransferItemVO)p2pTransferVO.getTransferItemList().get(1);
						EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, className+"["+methodName+"]",transactionID,"", "", "P2P Receiver transfer status changed to '250' from "+receiverItemVO.getTransferStatus());
						receiverItemVO.setTransferStatus(InterfaceErrorCodesI.AMBIGOUS);
					}
					p2pTransferVO.setTransferStatus(p_status);
					p2pTransferVO.setModifiedBy(PretupsI.SYSTEM_USER);
					p2pTransferVO.setModifiedOn(currentDate);
					
					//added by rajvi desai for sender credit back status=250
					int listsize = p2pTransferVO.getTransferItemList().size();
					if(listsize==2)
						p_statusTxt="Success";
					else if(listsize==3)
						p_statusTxt="Fail";

					ArrayList<?> newEntries=ReconciliationBL.prepareNewList(p2pTransferVO,p2pTransferVO.getTransferItemList(),p_statusTxt,null);
					p2pTransferVO.setTransferItemList(newEntries);
					updateCount=transferDAO.updateReconcilationStatus(p_con,p2pTransferVO);
					if (updateCount>0)
					{
						p_con.commit();
						LOG.info(methodName,"TransactionID="+transactionID+" Succesfully Settled to status="+p_status);		
					}
					else
					{
						p_con.rollback();
						if(LOG.isDebugEnabled()) LOG.debug(methodName,"TransactionID="+transactionID+" Not able Settled to status="+p_status);
	 					 EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,className+"["+methodName+"]",transactionID,"","","Not able Settled to status="+p_status);
					}
					//Sleep for some time and then continue
				}
				catch(BTSLBaseException be)
				{
					be.printStackTrace();
					p_con.rollback();
					LOG.info(methodName,"TransactionID="+transactionID+" Not able Settled to status="+p_status+" getting Exception="+be.getMessage());
 					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,className+"["+methodName+"]",transactionID,"","","Not able Settled to status="+p_status+" getting Exception="+be.getMessage());
				}
				catch(Exception e)
				{
					e.printStackTrace();
					p_con.rollback();
					LOG.info(methodName,"TransactionID="+transactionID+" Not able Settled to status="+p_status+" getting Exception="+e.getMessage());
 					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,className+"["+methodName+"]",transactionID,"","","Not able Settled to status="+p_status+" getting Exception="+e.getMessage());
				}
			}//end for loop 
		}
		catch(Exception e)
		{
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+"["+methodName+"]","","","","Exception:"+e.getMessage());
		}
		finally
		{
			HandleUnsettledP2PCasesLog.log(className, methodName,"Exited");
		}
	}
	
	/**
	 * Method loadP2PReconciliationVO
	 * This method to load the TransferVO based on transfer ID
	 * @param p_con
	 * @param p_transferID
	 * @return
	 */
	public P2PTransferVO loadP2PReconciliationVO(Connection p_con,String p_transferID)
	{
		final String methodName="loadP2PReconciliationVO";
		if(LOG.isDebugEnabled())
			LOG.debug(methodName,"Entered p_transferID="+p_transferID);
		HandleUnsettledP2PCasesLog.log(className, methodName,"Entered p_transferID="+p_transferID);
		PreparedStatement pstmtSelect=null;
		ResultSet rs=null;
		P2PTransferVO p2pTransferVO=null;
		try
		{
			StringBuffer selectQueryBuff =new StringBuffer();
			TransferDAO transferDAO= new TransferDAO();
			selectQueryBuff.append("SELECT KV.value,KV1.value txn_status,U.user_name,ST.name, PROD.short_name,STRF.transfer_id, ");
			selectQueryBuff.append("STRF.transfer_date, STRF.transfer_date_time, STRF.network_code, STRF.sender_id,");
			selectQueryBuff.append("STRF.product_code, STRF.sender_msisdn, STRF.receiver_msisdn, ");
			selectQueryBuff.append("STRF.receiver_network_code, STRF.transfer_value, STRF.error_code, ");
			selectQueryBuff.append("STRF.request_gateway_type, STRF.request_gateway_code, STRF.reference_id, ");
			selectQueryBuff.append("STRF.payment_method_type, STRF.service_type, STRF.pin_sent_to_msisdn, ");
			selectQueryBuff.append("STRF.language, STRF.country, STRF.skey, STRF.skey_generation_time, ");
			selectQueryBuff.append("STRF.skey_sent_to_msisdn, STRF.request_through_queue, STRF.credit_back_status, ");
			selectQueryBuff.append("STRF.quantity, STRF.reconciliation_flag, STRF.reconciliation_date, ");
			selectQueryBuff.append("STRF.reconciliation_by, STRF.created_on, STRF.created_by, STRF.modified_on, ");
			selectQueryBuff.append("STRF.modified_by, STRF.transfer_status, STRF.card_group_set_id, STRF.version, ");
			selectQueryBuff.append("STRF.card_group_id, STRF.sender_access_fee, STRF.sender_tax1_type, ");
			selectQueryBuff.append("STRF.sender_tax1_rate, STRF.sender_tax1_value, STRF.sender_tax2_type, ");
			selectQueryBuff.append("STRF.sender_tax2_rate, STRF.sender_tax2_value, STRF.sender_transfer_value, ");
			selectQueryBuff.append("STRF.receiver_access_fee, STRF.receiver_tax1_type, STRF.receiver_tax1_rate, ");
			selectQueryBuff.append("STRF.receiver_tax1_value, STRF.receiver_tax2_type, STRF.receiver_tax2_rate, ");
			selectQueryBuff.append("STRF.receiver_tax2_value, STRF.receiver_validity, STRF.receiver_transfer_value, ");
			selectQueryBuff.append("STRF.receiver_bonus_value, STRF.receiver_grace_period, STRF.transfer_category, ");
			selectQueryBuff.append("STRF.receiver_bonus_validity, STRF.card_group_code, STRF.receiver_valperiod_type ");
			selectQueryBuff.append("FROM subscriber_transfers STRF, products PROD,service_type ST, ");
			selectQueryBuff.append("p2p_subscribers U,key_values KV,key_values KV1  "); 
			selectQueryBuff.append("WHERE U.user_id(+)= STRF.sender_id ");
			selectQueryBuff.append("AND KV.key(+)=STRF.error_code AND KV.type(+)=? ");
			selectQueryBuff.append("AND KV1.key(+)=STRF.transfer_status AND KV1.type(+)=? ");
			selectQueryBuff.append("AND STRF.product_code=PROD.product_code ");
			selectQueryBuff.append("AND (STRF.reconciliation_flag <> 'Y' OR STRF.reconciliation_flag IS NULL ) ");
			selectQueryBuff.append("AND ST.service_type=STRF.service_type ");
			selectQueryBuff.append("AND (STRF.transfer_status=? OR STRF.transfer_status=? ) ");
			selectQueryBuff.append("AND STRF.transfer_id=? ");
			
			String selectQuery=selectQueryBuff.toString();
			if(LOG.isDebugEnabled())LOG.debug(methodName,"select query:"+selectQuery );		
			pstmtSelect = p_con.prepareStatement(selectQuery);
			int i=1;
			pstmtSelect.setString(i++, PretupsI.P2P_ERRCODE_VALUS);
			pstmtSelect.setString(i++, PretupsI.KEY_VALUE_TYPE_REOCN);
			pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
			pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
			pstmtSelect.setString(i++, p_transferID);
			rs = pstmtSelect.executeQuery();
			while(rs.next())
			{
				p2pTransferVO =new P2PTransferVO();
				
				p2pTransferVO.setProductName(rs.getString("short_name"));
				p2pTransferVO.setServiceName(rs.getString("name"));
				p2pTransferVO.setSenderName(rs.getString("user_name"));
				p2pTransferVO.setErrorMessage(rs.getString("value"));
				p2pTransferVO.setTransferID(rs.getString("transfer_id"));
				p2pTransferVO.setTransferDate(rs.getDate("transfer_date"));
				p2pTransferVO.setTransferDateTime(rs.getTimestamp("transfer_date_time"));
				p2pTransferVO.setTransferDateStr(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("transfer_date_time")));
				p2pTransferVO.setNetworkCode(rs.getString("network_code"));
				p2pTransferVO.setSenderID(rs.getString("sender_id"));
				p2pTransferVO.setProductCode(rs.getString("product_code"));
				p2pTransferVO.setSenderMsisdn(rs.getString("sender_msisdn"));
				p2pTransferVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
				p2pTransferVO.setReceiverNetworkCode(rs.getString("receiver_network_code"));
				p2pTransferVO.setTransferValue(rs.getLong("transfer_value"));
				p2pTransferVO.setTransferValueStr(PretupsBL.getDisplayAmount(rs.getLong("transfer_value")));
				p2pTransferVO.setErrorCode(rs.getString("error_code"));
				p2pTransferVO.setRequestGatewayType(rs.getString("request_gateway_type"));
				p2pTransferVO.setRequestGatewayCode(rs.getString("request_gateway_code"));
				p2pTransferVO.setReferenceID(rs.getString("reference_id"));
				p2pTransferVO.setPaymentMethodType(rs.getString("payment_method_type"));
				p2pTransferVO.setServiceType(rs.getString("service_type"));
				p2pTransferVO.setPinSentToMsisdn(rs.getString("pin_sent_to_msisdn"));
				p2pTransferVO.setLanguage(rs.getString("language"));
				p2pTransferVO.setCountry(rs.getString("country"));
				p2pTransferVO.setSkey(rs.getLong("skey"));
				p2pTransferVO.setSkeyGenerationTime(rs.getDate("skey_generation_time"));
				p2pTransferVO.setSkeySentToMsisdn(rs.getString("skey_sent_to_msisdn"));
				p2pTransferVO.setRequestThroughQueue(rs.getString("request_through_queue"));
				p2pTransferVO.setCreditBackStatus(rs.getString("credit_back_status"));
				p2pTransferVO.setQuantity(rs.getLong("quantity"));
				p2pTransferVO.setReconciliationFlag(rs.getString("reconciliation_flag"));
				p2pTransferVO.setReconciliationDate(rs.getDate("reconciliation_date"));
				p2pTransferVO.setReconciliationBy(rs.getString("reconciliation_by"));
				p2pTransferVO.setCreatedOn(rs.getDate("created_on"));
				p2pTransferVO.setCreatedBy(rs.getString("created_by"));
				p2pTransferVO.setModifiedOn(rs.getDate("modified_on"));
				p2pTransferVO.setModifiedBy(rs.getString("modified_by"));
				p2pTransferVO.setTransferStatus(rs.getString("txn_status"));
				p2pTransferVO.setCardGroupSetID(rs.getString("card_group_set_id"));
				p2pTransferVO.setVersion(rs.getString("version"));
				p2pTransferVO.setCardGroupID(rs.getString("card_group_id"));
				p2pTransferVO.setSenderAccessFee(rs.getLong("sender_access_fee"));
				p2pTransferVO.setSenderTax1Type(rs.getString("sender_tax1_type"));
				p2pTransferVO.setSenderTax1Rate(rs.getDouble("sender_tax1_rate"));
				p2pTransferVO.setSenderTax1Value(rs.getLong("sender_tax1_value"));
				p2pTransferVO.setSenderTax2Type(rs.getString("sender_tax2_type"));
				p2pTransferVO.setSenderTax2Rate(rs.getDouble("sender_tax2_rate"));
				p2pTransferVO.setSenderTax2Value(rs.getLong("sender_tax2_value"));
				p2pTransferVO.setSenderTransferValue(rs.getLong("sender_transfer_value"));
				p2pTransferVO.setReceiverAccessFee(rs.getLong("receiver_access_fee"));
				p2pTransferVO.setReceiverTax1Type(rs.getString("receiver_tax1_type"));
				p2pTransferVO.setReceiverTax1Rate(rs.getDouble("receiver_tax1_rate"));
				p2pTransferVO.setReceiverTax1Value(rs.getLong("receiver_tax1_value"));
				p2pTransferVO.setReceiverTax2Type(rs.getString("receiver_tax2_type"));
				p2pTransferVO.setReceiverTax2Rate(rs.getDouble("receiver_tax2_rate"));
				p2pTransferVO.setReceiverTax2Value(rs.getLong("receiver_tax2_value"));
				p2pTransferVO.setReceiverValidity(rs.getInt("receiver_validity"));
				p2pTransferVO.setReceiverTransferValue(rs.getLong("receiver_transfer_value"));
				p2pTransferVO.setReceiverBonusValue(rs.getLong("receiver_bonus_value"));
				p2pTransferVO.setReceiverGracePeriod(rs.getInt("receiver_grace_period"));
				p2pTransferVO.setTransferCategory(rs.getString("transfer_category"));
				p2pTransferVO.setReceiverBonusValidity(rs.getInt("receiver_bonus_validity"));
				p2pTransferVO.setCardGroupCode(rs.getString("card_group_code"));
				p2pTransferVO.setReceiverValPeriodType(rs.getString("receiver_valperiod_type"));
				p2pTransferVO.setTxnStatus(rs.getString("transfer_status"));
				p2pTransferVO.setTransferItemList(transferDAO.loadP2PReconciliationItemsList(p_con,p_transferID));
			}

		}//end of try
		catch (SQLException sqle)
		{
			LOG.error(methodName,"SQLException "+sqle.getMessage());
			sqle.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+"["+methodName+"]","","","","SQL Exception:"+sqle.getMessage());
		}//end of catch
		catch (Exception e)
		{
			LOG.error(methodName,"Exception "+e.getMessage());
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+"["+methodName+"]","","","","Exception:"+e.getMessage());
		}//end of catch
		finally
		{
			try{if(rs!=null) rs.close();}catch(Exception e){}
			try{if(pstmtSelect!=null) pstmtSelect.close();}catch(Exception e){}
			if(LOG.isDebugEnabled())LOG.debug(methodName,"Exiting ");
			HandleUnsettledP2PCasesLog.log(className, methodName,"Exiting");
		 }//end of finally
		
		return p2pTransferVO;
	}
	
	/**
	 * Method loadP2pConfigurationParamater
	 * This method to load parameters from constants.props file
	 */
	private static void loadP2pConfigurationParamater() throws BTSLBaseException{

		final String methodName="loadConfigurationParamater";
		if(LOG.isDebugEnabled())LOG.debug("loadConfigurationParamater","Entered");
		try
		{
			
			_loadP2pConfigParamMap.put("MODULE_TYPE",PretupsI.P2P_MODULE);
			
			String hostName=Constants.getProperty("FTP_AMB_SERVER_IP");
			if(BTSLUtil.isNullString(hostName))
			{
				LOG.error(methodName," Could not find file label for hostName in the Constants file.");
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","hostName:"+hostName);
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
             }
			else
				_loadP2pConfigParamMap.put("FTP_AMB_SERVER_IP",hostName.trim());

			String encryAllow = Constants.getProperty("FTP_AMB_FILE_ENCRY_ALLOW").trim();
			if (BTSLUtil.isNullString(encryAllow))
			{
				LOG.error(className,"FTP_AMB_FILE_ENCRY_ALLOW is not defined in Constant. props ");
				throw new BTSLBaseException(PretupsErrorCodesI.AMB_CONSTANT_ENTRY_MISSING);
			}
			_loadP2pConfigParamMap.put("FTP_AMB_FILE_ENCRY_ALLOW", encryAllow.trim());
			
			String userName=Constants.getProperty("FTP_AMB_USER_NAME").trim();
			if(BTSLUtil.isNullString(userName))
			{
				LOG.error(methodName," Could not find file label for userName in the Constants file.");
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","userName:"+userName);
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
			}
			//else
			//	_loadP2pConfigParamMap.put("FTP_AMB_USER_NAME",userName.trim());
			
			String password=Constants.getProperty("FTP_AMB_PASSWD").trim();
			if(BTSLUtil.isNullString(password))
			{
				LOG.error(methodName," Could not find file label for password in the Constants file.");
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","password:"+password);
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
			}
			//else
				//_loadP2pConfigParamMap.put("FTP_AMB_PASSWD",password.trim());
			if(encryAllow.equalsIgnoreCase("Y"))
			{
				userName=BTSLUtil.decryptText(userName.trim());
				password=BTSLUtil.decryptText(password.trim());
			}
			_loadP2pConfigParamMap.put("FTP_AMB_USER_NAME",userName.trim());
			_loadP2pConfigParamMap.put("FTP_AMB_PASSWD",password.trim());
			String destinationDir = Constants.getProperty("FTP_AMB_SRC_DIR_P2P").trim();
			if(BTSLUtil.isNullString(destinationDir))
			{
				LOG.error(methodName," Could not find file label for destinationDir in the Constants file.");
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","destinationDir:"+destinationDir);
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
			}
			else
				_loadP2pConfigParamMap.put("FTP_AMB_SRC_DIR_P2P",destinationDir.trim());
			
			String sourceFile = Constants.getProperty("FTP_AMB_P2P_RECV_DIR").trim();
			if(BTSLUtil.isNullString(sourceFile))
			{
				LOG.error(methodName," Could not find file label for sourceFile in the Constants file.");
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","sourceFile:"+sourceFile);
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
		    }
			else
				_loadP2pConfigParamMap.put("FTP_AMB_P2P_RECV_DIR",sourceFile.trim());
			
			String filename = Constants.getProperty("AMB_P2P_SERVER_FILE_NAME").trim();
			if(BTSLUtil.isNullString(filename))
			{
				LOG.error(methodName," Could not find file label for filename in the Constants file.");
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","filename:"+filename);
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
			}
			else
				_loadP2pConfigParamMap.put("AMB_P2P_SERVER_FILE_NAME",filename.trim());
			
			String movePath = Constants.getProperty("SERVER_MOVE_PATH_P2P").trim();
			if(BTSLUtil.isNullString(movePath))
			{
				LOG.error(methodName," Could not find file label for movePath in the Constants file.");
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","movePath:"+movePath);
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
			}
			else
				_loadP2pConfigParamMap.put("SERVER_MOVE_PATH_P2P",movePath.trim());
			
			String ftpPort = Constants.getProperty("FTP_AMB_PORT").trim();
			if(BTSLUtil.isNullString(ftpPort))
			{
				LOG.error(methodName," Could not find file label for ftpPort in the Constants file.");
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","ftpPort:"+ftpPort);
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
			}
			else
				_loadP2pConfigParamMap.put("FTP_AMB_PORT",ftpPort.trim());
			
			String ambFileFinalPath = Constants.getProperty("AMB_FILE_FINAL_PATH_P2P").trim();
			if(BTSLUtil.isNullString(ambFileFinalPath))
			{
				LOG.error(methodName," Could not find file label for ambFileFinalPath in the Constants file.");
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","ambFileFinalPath:"+ambFileFinalPath);
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
			}
			else
				_loadP2pConfigParamMap.put("AMB_FILE_FINAL_PATH_P2P",ambFileFinalPath.trim());
			
			String ambFileName = Constants.getProperty("AMB_P2P_FILE_NAME").trim();
			if(BTSLUtil.isNullString(ambFileName))
			{
				LOG.error(methodName," Could not find file label for ambFileName in the Constants file.");
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","ambFileName:"+ambFileName);
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
			}
			else
				_loadP2pConfigParamMap.put("AMB_P2P_FILE_NAME",ambFileName.trim());
			
			String ambFileExtn = Constants.getProperty("AMB_FILE_EXTN").trim();
			if(BTSLUtil.isNullString(ambFileExtn))
			{
				LOG.error(methodName," Could not find file label for ambFileExtn in the Constants file.");
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","ambFileExtn:"+ambFileExtn);
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
			}
			else
				_loadP2pConfigParamMap.put("AMB_FILE_EXTN",ambFileExtn.trim());
			
			String ftpAmbRecDir = Constants.getProperty("FTP_AMB_P2P_RECV_DIR").trim();
			if(BTSLUtil.isNullString(ftpAmbRecDir))
			{
				LOG.error(methodName," Could not find file label for ftpAmbRecDir in the Constants file.");
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","ftpAmbRecDir:"+ftpAmbRecDir);
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
			}
			else
				_loadP2pConfigParamMap.put("FTP_AMB_P2P_RECV_DIR",ftpAmbRecDir.trim());
			
			String ambServerFileName = Constants.getProperty("AMB_P2P_SERVER_FILE_NAME").trim();
			if(BTSLUtil.isNullString(ambServerFileName))
			{
				LOG.error(methodName," Could not find file label for ambServerFileName in the Constants file.");
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","ambServerFileName:"+ambServerFileName);
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.AMB_SERVER_PROCESSING_EXCEPTION);
			}
			else
				_loadP2pConfigParamMap.put("AMB_P2P_SERVER_FILE_NAME",ambServerFileName.trim());
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
}

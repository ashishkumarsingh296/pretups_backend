/** @# QueueTableDAO
 * This class is used to make database interaction with postpaid_cust_pay_master table.
 *
 *	   Created on 				Created by					History
 *	--------------------------------------------------------------------------------
 * 		March 28, 2006			  Ankit Zindal		   Initial creation
 *	--------------------------------------------------------------------------------
 *  Copyright(c) 2006 Bharti Telesoft Ltd.
 */
package com.inter.mobinilpost;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.ArrayList;
import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.module.InterfaceUtil;
import com.btsl.util.BTSLUtil;

public class MobinilPostToPreDAO {
		private Log _log = LogFactory.getLog(this.getClass().getName());
		
		/**
		 * Method for insert Data In Postpaid_cust_pay_master Table.
		 * @param p_con Connection
		 * @param p_queueTableVO QueueTableVO
		 * @return int
		 * @exception BTSLBaseException
		 */
		public int insertDataInQueueTable(Connection p_con,MobinilPostToPreVO p_mobinilPostToPreVO) throws BTSLBaseException
		{
			if (_log.isDebugEnabled())
				_log.debug("insertDataInQueueTable()", "Entered p_queueTableVO="+p_mobinilPostToPreVO.toString());
			int addCount=-1;
			PreparedStatement pstmt = null;
			StringBuffer strBuff = new StringBuffer();
			strBuff.append("INSERT INTO postpaid_cust_pay_master(queue_id,network_code,msisdn,account_id,amount,transfer_id,status, ");
			strBuff.append("entry_date,description,service_type,entry_type,module_code,sender_id, ");
			strBuff.append("created_on,source_type,interface_id,external_id,service_class,product_code,tax_amount,access_fee_amount,entry_for,bonus_amount,sender_msisdn,gateway_code,interface_amount,imsi,receiver_msisdn,type ) ");
			strBuff.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
			String sqlInsert = strBuff.toString();
			if (_log.isDebugEnabled())
			    _log.debug("insertDataInQueueTable()", "QUERY sqlInsert=" + sqlInsert);
			try
			{
				pstmt = p_con.prepareStatement(sqlInsert);
				int i=1;
				pstmt.setString(i++,p_mobinilPostToPreVO.getQueueID());
				pstmt.setString(i++,p_mobinilPostToPreVO.getNetworkID());
				pstmt.setString(i++,p_mobinilPostToPreVO.getMsisdn());
				pstmt.setString(i++,p_mobinilPostToPreVO.getAccountID());
				pstmt.setLong(i++,p_mobinilPostToPreVO.getAmount());
				pstmt.setString(i++,p_mobinilPostToPreVO.getTransferID());
				pstmt.setString(i++,p_mobinilPostToPreVO.getStatus());
				pstmt.setDate(i++,BTSLUtil.getSQLDateFromUtilDate(p_mobinilPostToPreVO.getEntryOn()));
				pstmt.setString(i++,p_mobinilPostToPreVO.getDescription());
				pstmt.setString(i++,p_mobinilPostToPreVO.getServiceType());
				pstmt.setString(i++,p_mobinilPostToPreVO.getEntryType());
				pstmt.setString(i++,p_mobinilPostToPreVO.getModule());
				pstmt.setString(i++,p_mobinilPostToPreVO.getSenderID());
				pstmt.setTimestamp(i++,BTSLUtil.getTimestampFromUtilDate(p_mobinilPostToPreVO.getCreatedOn()));
				pstmt.setString(i++,p_mobinilPostToPreVO.getSourceType());
				pstmt.setString(i++,p_mobinilPostToPreVO.getInterfaceID());
				pstmt.setString(i++,p_mobinilPostToPreVO.getExternalInterfaceID());
				pstmt.setString(i++,p_mobinilPostToPreVO.getServiceClass());
				pstmt.setString(i++,p_mobinilPostToPreVO.getProductCode());
				pstmt.setLong(i++,p_mobinilPostToPreVO.getTaxAmount());
				pstmt.setLong(i++,p_mobinilPostToPreVO.getAccessFee());
				pstmt.setString(i++,p_mobinilPostToPreVO.getEntryFor());
				pstmt.setLong(i++,p_mobinilPostToPreVO.getBonusAmount());
				pstmt.setString(i++,p_mobinilPostToPreVO.getSenderMsisdn());
				pstmt.setString(i++,p_mobinilPostToPreVO.getGatewayCode());
				pstmt.setDouble(i++,p_mobinilPostToPreVO.getInterfaceAmount());
				pstmt.setString(i++,InterfaceUtil.NullToString(p_mobinilPostToPreVO.getImsi()));
				pstmt.setString(i++,p_mobinilPostToPreVO.getReceiverMsisdn());
				pstmt.setString(i++,p_mobinilPostToPreVO.getType());
				addCount = pstmt.executeUpdate();
			} 
			catch (SQLException sqe)
			{
				_log.error("insertDataInQueueTable()", "SQLException : " + sqe);
				sqe.printStackTrace();
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"QueueTableDAO[insertDataInQueueTable]","","","","SQL Exception:"+sqe.getMessage());
				throw new BTSLBaseException(this, "insertDataInQueueTable()", "error.general.sql.processing");
			} 
			catch (Exception ex)
			{
				_log.error("insertDataInQueueTable()", "Exception : " + ex);
				ex.printStackTrace();
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"QueueTableDAO[insertDataInQueueTable]","","","","Exception:"+ex.getMessage());
				throw new BTSLBaseException(this, "insertDataInQueueTable()", "error.general.processing");
			} 
			finally
			{
				try{if (pstmt != null){pstmt.close();}} catch (Exception e){}
				if (_log.isDebugEnabled())
					_log.debug("insertDataInQueueTable()", "Exiting addCount="+addCount);
			}
			return addCount;
		}
		
		
		/**
		 * Method for update Data of the user in postpaid_cust_pay_master.
		 * 
		 * @param p_con java.sql.Connection
		 * @param p_mobinilPostToPreVO QueueTableVO
		 * 
		 * @return int
		 * @exception BTSLBaseException
		 */
		public int updateDataInQueueTable(Connection p_con,MobinilPostToPreVO p_mobinilPostToPreVO) throws BTSLBaseException
		{
			if (_log.isDebugEnabled())
				_log.debug("updateDataInQueueTable()", "Entered p_mobinilPostToPreVO="+p_mobinilPostToPreVO.toString());
			int updateCount=-1;
			PreparedStatement pstmtUpdate = null;
			StringBuffer strBuffUpdate = new StringBuffer("UPDATE postpaid_cust_pay_master SET status=? where transfer_id=?  AND  msisdn=? AND status=? ");
			String sqlUpdate = strBuffUpdate.toString();
			if (_log.isDebugEnabled())
			    _log.debug("updateDataInQueueTable()", "QUERY sqlUpdate=" + sqlUpdate);
			try
			{
				int i=1;
				pstmtUpdate = p_con.prepareStatement(sqlUpdate);
				i=1;
				pstmtUpdate.setString(i++,PretupsI.STATUS_QUEUE_FAIL);
				pstmtUpdate.setString(i++,p_mobinilPostToPreVO.getTransferID());
				pstmtUpdate.setString(i++,p_mobinilPostToPreVO.getMsisdn());
				pstmtUpdate.setString(i++,PretupsI.STATUS_QUEUE_AVAILABLE);
				updateCount=pstmtUpdate.executeUpdate();
				if(updateCount<=0)
					updateCount = insertDataInQueueTable(p_con,p_mobinilPostToPreVO);
			} 
			catch (SQLException sqe)
			{
				_log.error("updateDataInQueueTable()", "SQLException : " + sqe);
				sqe.printStackTrace();
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"QueueTableDAO[updateDataInQueueTable]","","","","SQL Exception:"+sqe.getMessage());
				throw new BTSLBaseException(this, "updateDataInQueueTable()", "error.general.sql.processing");
			} 
			catch (Exception ex)
			{
				_log.error("updateDataInQueueTable()", "Exception : " + ex);
				ex.printStackTrace();
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"QueueTableDAO[updateDataInQueueTable]","","","","Exception:"+ex.getMessage());
				throw new BTSLBaseException(this, "updateDataInQueueTable()", "error.general.processing");
			} 
			finally
			{
				try{if (pstmtUpdate != null){pstmtUpdate.close();}} catch (Exception e){}
				if (_log.isDebugEnabled())
					_log.debug("updateDataInQueueTable()", "Exiting updateCount="+updateCount);
			}
			return updateCount;
		}
		/***
		 * Method to calculate the size of queue table
		 * 
		 * @param p_con java.sql.Connection
		 * @param p_serviceType String
		 * @param p_interfaceID String
		 * 
		 * @return int
		 * @exception BTSLBaseException
		 */
		
		public int calculateQueueTableSize(Connection p_con,String p_serviceType,String p_interfaceID) throws BTSLBaseException
		{
			if (_log.isDebugEnabled())
				_log.debug("calculateQueueTableSize()", "Entered p_serviceType="+p_serviceType+" p_interfaceID="+p_interfaceID);
			ResultSet rs=null;
			PreparedStatement pstmt = null;
			int counts=0;
			StringBuffer strBuff = new StringBuffer();
			strBuff.append("SELECT COUNT(Msisdn) FROM postpaid_cust_pay_master WHERE status=? AND interface_id=?");
			if(!PretupsI.ALL.equals(p_serviceType))
			{
				if(p_serviceType.indexOf(",")!=-1)
					strBuff.append(" AND service_type IN("+p_serviceType+")");
				else
					strBuff.append(" AND service_type ="+p_serviceType);
			}
			String sqlSelect = strBuff.toString();
			if (_log.isDebugEnabled())
			    _log.debug("calculateQueueTableSize()", "QUERY sqlSelect=" + sqlSelect);
			try
			{
				pstmt = p_con.prepareStatement(sqlSelect);
				pstmt.setString(1,PretupsI.STATUS_QUEUE_AVAILABLE);
				pstmt.setString(2,p_interfaceID);
				rs = pstmt.executeQuery();
				if(rs.next())
					counts= rs.getInt(1);
			} 
			catch (SQLException sqe)
			{
				_log.error("calculateQueueTableSize()", "SQLException : " + sqe);
				sqe.printStackTrace();
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"QueueTableDAO[calculateQueueTableSize]","","","","SQL Exception:"+sqe.getMessage());
				throw new BTSLBaseException(this, "calculateQueueTableSize()", "error.general.sql.processing");
			} 
			catch (Exception ex)
			{
				_log.error("calculateQueueTableSize()", "Exception : " + ex);
				ex.printStackTrace();
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"QueueTableDAO[calculateQueueTableSize]","","","","Exception:"+ex.getMessage());
				throw new BTSLBaseException(this, "calculateQueueTableSize()", "error.general.processing");
			} 
			finally
			{
				try{if (pstmt != null){pstmt.close();}} catch (Exception e){}
				try{if (rs != null){rs.close();}} catch (Exception e){}
				if (_log.isDebugEnabled())
					_log.debug("calculateQueueTableSize()", "Exiting queue table size =counts="+counts);
			}
			return counts;
		}
		
		/***
		 * Method to get the queue id from sequence.
		 * 
		 * @param p_con java.sql.Connection
		 * @return String
		 * @exception BTSLBaseException
		 */
		
		public String getQueueID(Connection p_con) throws BTSLBaseException
		{
			if (_log.isDebugEnabled())
				_log.debug("getQueueID()", "Entered");
			ResultSet rs=null;
			PreparedStatement pstmt = null;
			String id=null;
			StringBuffer strBuff = new StringBuffer();
			strBuff.append("SELECT SEQ_QUEUE_ID.nextval from DUAL");
			String sqlSelect = strBuff.toString();
			if (_log.isDebugEnabled())
			    _log.debug("getQueueID()", "QUERY sqlSelect=" + sqlSelect);
			try
			{
				pstmt = p_con.prepareStatement(sqlSelect);
				rs = pstmt.executeQuery();
				if(rs.next())
					id= rs.getString(1);
			} 
			catch (SQLException sqe)
			{
				_log.error("getQueueID()", "SQLException : " + sqe);
				sqe.printStackTrace();
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"QueueTableDAO[getQueueID]","","","","SQL Exception:"+sqe.getMessage());
				throw new BTSLBaseException(this, "getQueueID()", "error.general.sql.processing");
			} 
			catch (Exception ex)
			{
				_log.error("getQueueID()", "Exception : " + ex);
				ex.printStackTrace();
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"QueueTableDAO[getQueueID]","","","","Exception:"+ex.getMessage());
				throw new BTSLBaseException(this, "getQueueID()", "error.general.processing");
			} 
			finally
			{
				try{if (pstmt != null){pstmt.close();}} catch (Exception e){}
				try{if (rs != null){rs.close();}} catch (Exception e){}
				if (_log.isDebugEnabled())
					_log.debug("getQueueID()", "Exiting id="+id);
			}
			return id;
		}
		
		/**
		 * Method getQueueDataForCDRGenerationProcess
		 * Method to laod the data for CDR from postpaid_cust_pay_master table
		 * @author Amit Ruwali
		 * @param p_con Connection
		 * @param p_serviceType String  
		 * @param p_interfaceID String
		 * @param p_startTime TimeStamp
		 * @param p_endTime TimeStamp
		 * @return queueList ArrayList
		 * @exception BTSLBaseException
		 */
		
		public ArrayList getQueueDataForCDRGenerationProcess(Connection p_con,String p_serviceType,String p_interfaceID,Timestamp p_startTime,Timestamp p_endTime)
		throws BTSLBaseException
		{
		    if (_log.isDebugEnabled())
				_log.debug("getQueueDataForCDRGenerationProcess", "Entered p_serviceType="+p_serviceType +"p_interfaceID="+p_interfaceID+"p_startTime="+p_startTime+"p_endTime="+p_endTime);
		    
		    String serType=p_serviceType.replaceAll(",","','");
		    p_serviceType="'"+serType+"'";
		    
		    if (_log.isDebugEnabled())
		    	_log.debug("getQueueDataForCDRGenerationProcess", "p_serviceType="+p_serviceType);
		    
		    ArrayList queueList=new ArrayList();
		    ResultSet rs=null;
			PreparedStatement pstmt = null;
			MobinilPostToPreVO queueTableVO=null;
			StringBuffer strBuff=new StringBuffer("SELECT queue_id,network_code,msisdn,account_id,amount,transfer_id");
			strBuff.append(",status,entry_date,description,process_id,process_date,other_info,service_type,entry_type");
			strBuff.append(",process_status,module_code,sender_id,created_on,source_type,interface_id,external_id,");
			strBuff.append("service_class,product_code,tax_amount,access_fee_amount,entry_for,bonus_amount,");
			strBuff.append("sender_msisdn,cdr_file_name,gateway_code,interface_amount,imsi,receiver_msisdn,type ");
			strBuff.append("FROM postpaid_cust_pay_master WHERE created_on>=? AND created_on<? ");
			strBuff.append("AND service_type IN(" + p_serviceType +")");
			strBuff.append(" AND interface_id =? AND status=0 ORDER BY created_on");
			String sqlSelect = strBuff.toString();
			if (_log.isDebugEnabled())
			    _log.debug("getQueueDataForCDRGenerationProcess", "QUERY sqlSelect=" + sqlSelect);
			try
		    {
				pstmt = p_con.prepareStatement(sqlSelect);
				pstmt.setTimestamp(1,p_startTime);
				pstmt.setTimestamp(2,p_endTime);
				pstmt.setString(3,p_interfaceID);
				rs = pstmt.executeQuery();
				while(rs.next())
				{
				    queueTableVO=new MobinilPostToPreVO();
				    queueTableVO.setQueueID(rs.getString("queue_id"));
				    queueTableVO.setNetworkID(rs.getString("network_code"));
				    queueTableVO.setMsisdn(rs.getString("msisdn"));
				    queueTableVO.setAccountID(rs.getString("account_id"));
				    queueTableVO.setAmount(rs.getLong("amount"));
				    queueTableVO.setTransferID(rs.getString("transfer_id"));
				    queueTableVO.setStatus(rs.getString("status"));
				    queueTableVO.setEntryOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("entry_date")));
				    //queueTableVO.setEntryOn(rs.getDate("entry_date"));Changed by Dhiraj on 25/04/07
				    queueTableVO.setDescription(rs.getString("description"));
				    queueTableVO.setProcessID(rs.getString("process_id"));
				    queueTableVO.setProcessDate(rs.getDate("process_date"));
				    queueTableVO.setOtherInfo(rs.getString("other_info"));
				    queueTableVO.setServiceType(rs.getString("service_type"));
				    queueTableVO.setEntryType(rs.getString("entry_type"));
				    queueTableVO.setProcessStatus(rs.getString("process_status"));
				    queueTableVO.setModule(rs.getString("module_code"));
				    queueTableVO.setSenderID(rs.getString("sender_id"));
				    //queueTableVO.setCreatedOn(rs.getDate("created_on"));Changed by Dhiraj on 25/04/07
				    queueTableVO.setCreatedOn(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("created_on")));
				    queueTableVO.setSourceType(rs.getString("source_type"));
				    queueTableVO.setInterfaceID(rs.getString("interface_id"));
				    queueTableVO.setExternalInterfaceID(rs.getString("external_id"));
				    queueTableVO.setServiceClass(rs.getString("service_class"));
				    queueTableVO.setProductCode(rs.getString("product_code"));
				    queueTableVO.setTaxAmount(rs.getLong("tax_amount"));
				    queueTableVO.setAccessFee(rs.getLong("access_fee_amount"));
				    queueTableVO.setEntryFor(rs.getString("entry_for"));
				    queueTableVO.setBonusAmount(rs.getLong("bonus_amount"));
				    queueTableVO.setSenderMsisdn(rs.getString("sender_msisdn"));
				    queueTableVO.setCdrFileName(rs.getString("cdr_file_name"));
				    queueTableVO.setGatewayCode(rs.getString("gateway_code"));
				    queueTableVO.setInterfaceAmount(rs.getDouble("interface_amount"));
				    queueTableVO.setImsi(rs.getString("imsi"));
				    queueTableVO.setReceiverMsisdn(rs.getString("receiver_msisdn"));
				    queueTableVO.setType(rs.getString("type"));
				    queueList.add(queueTableVO);
				}
		    }
		    catch (Exception ex)
			{
				_log.error("getQueueDataForCDRGenerationProcess", "Exception : " + ex);
				ex.printStackTrace();
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"QueueTableDAO[getQueueDataForCDRGenerationProcess]","","","","Exception:"+ex.getMessage());
				throw new BTSLBaseException(this, "getQueueDataForCDRGenerationProcess", "error.general.processing");
			} 
			finally
			{
			    try{if (rs != null){rs.close();}} catch (Exception e){}
			    try{if (pstmt != null){pstmt.close();}} catch (Exception e){}
				if (_log.isDebugEnabled())
					_log.debug("getQueueDataForCDRGenerationProcess", "Exiting queueList.size="+queueList.size());
			}
			return queueList;
		}
		
		/**
		 * Method updateQueueDataForCDR
		 * Method used to Update CDR data in postpaid_cust_pay_master table
		 * @author Amit Ruwali
		 * @param p_con Connection
		 * @param p_cdrVOList ArrayList
		 * @return updateCount int
		 * @exception BTSLBaseException
		 */
		
		public int updateQueueDataForCDR(Connection p_con,ArrayList p_cdrVOList)
		throws BTSLBaseException
		{
		    if (_log.isDebugEnabled())
				_log.debug("updateQueueDataForCDR", "Entered p_cdrVOList.size()="+p_cdrVOList.size() );
		    PreparedStatement pstmt = null;
			MobinilPostToPreVO queueTableVO=null;
			int updateCount=-1;
			int updatedRecs=0;
			int size=0;
			StringBuffer strBuff=new StringBuffer("UPDATE postpaid_cust_pay_master SET status=?,process_date=?,");
			strBuff.append("process_status=?,process_id=?,cdr_file_name=? WHERE queue_id=?");
			String sqlSelect = strBuff.toString();
			if (_log.isDebugEnabled())
			    _log.debug("updateQueueDataForCDR", "QUERY sqlSelect=" + sqlSelect);
		    try
		    {
		        pstmt=p_con.prepareStatement(sqlSelect);
		        if(p_cdrVOList!=null)
		        {
			        size=p_cdrVOList.size();
		            for (int i = 0; i <size; i++)
			        {
			            queueTableVO=(MobinilPostToPreVO)p_cdrVOList.get(i);
			            pstmt.setString(1,queueTableVO.getStatus());
			            pstmt.setDate(2,BTSLUtil.getSQLDateFromUtilDate(queueTableVO.getProcessDate()));
			            pstmt.setString(3,queueTableVO.getProcessStatus());
			            pstmt.setString(4,queueTableVO.getProcessID());
			            pstmt.setString(5,queueTableVO.getCdrFileName());
			            pstmt.setString(6,queueTableVO.getQueueID());
			            updateCount=pstmt.executeUpdate();
			            pstmt.clearParameters();
			            if(updateCount>0)
			                updatedRecs++;
			        }
			    }
		        if(updatedRecs==size)
		            updateCount=size;
		        else
		            updateCount=-1;
		    }
		    catch (Exception ex)
			{
				_log.error("updateQueueDataForCDR", "Exception : " + ex);
				ex.printStackTrace();
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"QueueTableDAO[updateQueueDataForCDR]","","","","Exception:"+ex.getMessage());
				throw new BTSLBaseException(this, "updateQueueDataForCDR", "error.general.processing");
			} 
			finally
			{
				try{if (pstmt != null){pstmt.close();}} catch (Exception e){}
				if (_log.isDebugEnabled())
					_log.debug("updateQueueDataForCDR", "Exiting updateCount="+updateCount);
			}
		return updateCount;
		}

}

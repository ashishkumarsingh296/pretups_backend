/** @# OperatorC2CBatchTransferDAO.java
*
*	   Created on 				Created by					History
*	--------------------------------------------------------------------------------
* 		June 22, 2006			Amit Ruwali	   			Initial creation
*  		July 20, 2006			Sandeep Goel			Modification
*  		Aug  05, 2006			Sandeep Goel			Modification ID TOG001
*	--------------------------------------------------------------------------------
*  Copyright(c) 2006 Bharti Telesoft Ltd.
* This class use for Batch C2C Transfer.
*/

package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
//commented for DB2
//import oracle.jdbc.OraclePreparedStatement;
import java.util.Map;

import com.btsl.util.MessageResources;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductCache;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.BatchC2CFileProcessLog;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyBL;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyDAO;
import com.btsl.pretups.loyaltymgmt.businesslogic.LoyaltyVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.PromotionDetailsVO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
//added by jasmine for DB2
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;

public class OperatorC2CBatchTransferDAO {
	
	private OperatorC2CBatchTransferQry operatorC2CBatchTransferQry ;
	private C2CBatchTransferQry c2CBatchTransferQry ;
    public OperatorC2CBatchTransferDAO() {
    	super();
    	operatorC2CBatchTransferQry = (OperatorC2CBatchTransferQry)ObjectProducer.getObject(QueryConstants.OPERATOR_C2C_BATCH_TRANSFER_QRY, QueryConstants.QUERY_PRODUCER);
    	c2CBatchTransferQry = (C2CBatchTransferQry)ObjectProducer.getObject(QueryConstants.C2C_BATCH_TRANSFER_QRY, QueryConstants.QUERY_PRODUCER);
    }
    private Log _log = LogFactory.getLog(this.getClass().getName());
    
    /**
	 * Method for loading C2CBatch details..
	 * This method will load the batches that are within the geography of user whose userId is passed
	 * with status(OPEN) also in items table for corresponding master record the status is in p_itemStatus
	 * 
	 * @param p_con java.sql.Connection
	 * @param p_itemStatus String
	 * @param p_currentLevel String
	 * @return java.util.ArrayList
	 * @throws  BTSLBaseException
	 */
	public ArrayList loadBatchC2CMasterDetailsForTxr(Connection p_con,String p_userID,String p_itemStatus, String p_currentLevel) throws BTSLBaseException
	{
		final String methodName = "loadBatchC2CMasterDetailsForTxr";
		if (_log.isDebugEnabled()) 	_log.debug("loadBatchC2CMasterDetailsForTxr", "Entered p_userID="+p_userID+" p_itemStatus="+p_itemStatus+" p_currentLevel="+p_currentLevel);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
					
		String sqlSelect = operatorC2CBatchTransferQry.loadBatchC2CMasterDetailsForTxrQry(p_itemStatus,p_currentLevel);
		if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		ArrayList list = new ArrayList();
		try
		{
			pstmt = p_con.prepareStatement(sqlSelect);
			int index=1;
			pstmt.setString(index++, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
			pstmt.setString(index++, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
			pstmt.setString(index++, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
			//pstmt.setString(4, p_userID);
			pstmt.setString(index++, PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_OPEN);
			pstmt.setString(index++, PretupsI.CHANNEL_TRANSFER_BATCH_C2C_ITEM_RCRDSTATUS_PROCESSED);
			pstmt.setString(index++, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
			rs = pstmt.executeQuery();
			C2CBatchMasterVO c2cBatchMasterVO=null;
			while (rs.next())
			{
				c2cBatchMasterVO=new C2CBatchMasterVO();
				c2cBatchMasterVO.setOptBatchId(rs.getString("opt_batch_id"));
				c2cBatchMasterVO.setBatchId(rs.getString("batch_id"));
				c2cBatchMasterVO.setBatchName(rs.getString("batch_name"));
				c2cBatchMasterVO.setProductName(rs.getString("product_name"));
				c2cBatchMasterVO.setProductMrp(rs.getLong("unit_value"));
				c2cBatchMasterVO.setProductMrpStr(PretupsBL.getDisplayAmount(rs.getLong("unit_value")));
				c2cBatchMasterVO.setBatchTotalRecord(rs.getInt("batch_total_record"));
				c2cBatchMasterVO.setNewRecords(rs.getInt("new"));
				c2cBatchMasterVO.setClosedRecords(rs.getInt("close"));
				c2cBatchMasterVO.setRejectedRecords(rs.getInt("cncl"));
				c2cBatchMasterVO.setNetworkCode(rs.getString("network_code"));
				c2cBatchMasterVO.setNetworkCodeFor(rs.getString("network_code_for"));
				c2cBatchMasterVO.setProductCode(rs.getString("product_code"));
				c2cBatchMasterVO.setModifiedBy(rs.getString("modified_by"));
				c2cBatchMasterVO.setModifiedOn(rs.getTimestamp("modified_on"));
				c2cBatchMasterVO.setProductType(rs.getString("product_type"));
				c2cBatchMasterVO.setProductShortName(rs.getString("short_name"));
				c2cBatchMasterVO.setDomainCode(rs.getString("domain_code"));
				c2cBatchMasterVO.setBatchDate(rs.getDate("batch_date"));
				c2cBatchMasterVO.setDefaultLang(rs.getString("sms_default_lang"));
				c2cBatchMasterVO.setSecondLang(rs.getString("sms_second_lang"));
				c2cBatchMasterVO.setTransferType(rs.getString("transfer_type"));
				c2cBatchMasterVO.setTransferSubType(rs.getString("transfer_sub_type"));
				c2cBatchMasterVO.setCreatedBy(rs.getString("created_by"));
				c2cBatchMasterVO.setUserId(rs.getString("user_id"));
				list.add(c2cBatchMasterVO);
			}
		} 
		catch (SQLException sqe)
		{
			_log.error(methodName, "SQLException : " + sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[loadBatchC2CMasterDetailsForTxr]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}
		catch (Exception ex)
		{
		    _log.error("loadBatchC2CMasterDetails", "Exception : " + ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[loadBatchC2CMasterDetailsForTxr]","","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		}
		finally
		{
		    try{if (rs != null){rs.close();}} catch (Exception e){}
			try{if (pstmt != null){pstmt.close();}} catch (Exception e){}
			if (_log.isDebugEnabled()) _log.debug(methodName, "Exiting: c2cBatchMasterVOList size=" + list.size());
		}
		return list;
	}
	
	/**
	 * This method will load the batches that are within the geography of user whose userId is passed and batch id basis and mobile no basis.
	 * with status(OPEN) also in items table for corresponding master record.
	 * @Connection p_con
	 * @String p_goeDomain
	 * @String p_domain
	 * @String p_productCode
	 * @String p_batchid
	 * @String p_msisdn
	 * @Date p_fromDate
	 * @Date p_toDate
	 * @param p_loginID TODO
	 * @throws  BTSLBaseException
	
	 */
	public ArrayList loadBatchC2CMasterDetails(Connection p_con,String p_domain,String p_productCode, String p_batchid, String p_msisdn, Date p_fromDate, Date p_toDate, String p_loginID) throws BTSLBaseException
	{
		final String methodName = "loadBatchC2CMasterDetails";
		if (_log.isDebugEnabled()) 	_log.debug(methodName, " p_domain="+p_domain+" p_productCode="+p_productCode+" p_batchid="+p_batchid+" p_msisdn="+p_msisdn+" p_fromDate="+p_fromDate+" p_toDate="+p_toDate+" p_loginID="+p_loginID);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sqlSelect = operatorC2CBatchTransferQry.loadBatchC2CMasterDetailsQry(p_batchid);
		if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		ArrayList list = new ArrayList();
		try
		{
			pstmt = p_con.prepareStatement(sqlSelect);
			int i = 0;
			pstmt.setString(++i, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
			pstmt.setString(++i, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
			pstmt.setString(++i, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
			pstmt.setString(++i, p_loginID);
			if(p_batchid !=null)
			    pstmt.setString(++i, p_batchid);
			
			else
			{
			    pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
			    pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
			    if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY BTSLUtil.getSQLDateFromUtilDate(p_fromDate)=" + BTSLUtil.getSQLDateFromUtilDate(p_toDate)+" BTSLUtil.getSQLDateFromUtilDate(p_fromDate)="+BTSLUtil.getSQLDateFromUtilDate(p_toDate));
			}
			rs = pstmt.executeQuery();
			C2CBatchMasterVO c2cBatchMasterVO = null;
			while (rs.next())
			{
				c2cBatchMasterVO=new C2CBatchMasterVO();
				c2cBatchMasterVO.setBatchId(rs.getString("batch_id"));
				c2cBatchMasterVO.setDomainCode(rs.getString("domain_code"));
				c2cBatchMasterVO.setBatchName(rs.getString("batch_name"));
				c2cBatchMasterVO.setProductName(rs.getString("product_name"));
				c2cBatchMasterVO.setProductMrp(rs.getLong("unit_value"));
				c2cBatchMasterVO.setProductMrpStr(PretupsBL.getDisplayAmount(rs.getLong("unit_value")));
				c2cBatchMasterVO.setBatchTotalRecord(rs.getInt("batch_total_record"));
				c2cBatchMasterVO.setNewRecords(rs.getInt("new"));
				c2cBatchMasterVO.setClosedRecords(rs.getInt("close"));
				c2cBatchMasterVO.setRejectedRecords(rs.getInt("cncl"));
				c2cBatchMasterVO.setBatchDate(rs.getDate("batch_date"));
				if(c2cBatchMasterVO.getBatchDate()!=null)
					c2cBatchMasterVO.setBatchDateStr(BTSLUtil.getDateStringFromDate(c2cBatchMasterVO.getBatchDate()));
				list.add(c2cBatchMasterVO);
			}
		} 
		catch (SQLException sqe)
		{
			_log.error(methodName, "SQLException : " + sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[loadBatchC2CMasterDetails]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}
		catch (Exception ex)
		{
		    _log.error(methodName, "Exception : " + ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[loadBatchC2CMasterDetails]","","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		}
		finally
		{
		    try{if (rs != null){rs.close();}} catch (Exception e){}
			try{if (pstmt != null){pstmt.close();}} catch (Exception e){}
			if (_log.isDebugEnabled()) _log.debug(methodName, "Exiting: c2cBatchMasterVOList size=" + list.size());
		}
		return list;
	}
	
	
	
	/**
	 * Method for loading C2CBatch details..
	 * This method will load the batches that are within the geography of user whose userId is passed
	 * with status(OPEN) also in items table for corresponding master record the status is in p_itemStatus
	 * 
	 * @param p_con java.sql.Connection
	 * @param p_itemStatus String
	 * @param p_currentLevel String
	 * @return java.util.ArrayList
	 * @throws  BTSLBaseException
	
	 */
	public ArrayList loadBatchC2CMasterDetailsForWdr(Connection p_con,String p_userID,String p_itemStatus, String p_currentLevel) throws BTSLBaseException
	{
		final String methodName = "loadBatchC2CMasterDetailsForWdr";
		if (_log.isDebugEnabled()) 	_log.debug(methodName, "Entered p_userID="+p_userID+" p_itemStatus="+p_itemStatus+" p_currentLevel="+p_currentLevel);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
				
		String sqlSelect = operatorC2CBatchTransferQry.loadBatchC2CMasterDetailsForWdrQry(p_itemStatus,p_currentLevel);
		if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		ArrayList list = new ArrayList();
		try
		{
			pstmt = p_con.prepareStatement(sqlSelect);
			pstmt.setString(1, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
			pstmt.setString(2, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
			pstmt.setString(3, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
			pstmt.setString(4, p_userID);
			pstmt.setString(5, PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_OPEN);
			pstmt.setString(6, PretupsI.CHANNEL_TRANSFER_BATCH_C2C_ITEM_RCRDSTATUS_PROCESSED);
			pstmt.setString(7, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
			rs = pstmt.executeQuery();
			C2CBatchMasterVO c2cBatchMasterVO=null;
			while (rs.next())
			{
				c2cBatchMasterVO=new C2CBatchMasterVO();
				c2cBatchMasterVO.setBatchId(rs.getString("batch_id"));
				c2cBatchMasterVO.setBatchName(rs.getString("batch_name"));
				c2cBatchMasterVO.setProductName(rs.getString("product_name"));
				c2cBatchMasterVO.setProductMrp(rs.getLong("unit_value"));
				c2cBatchMasterVO.setProductMrpStr(PretupsBL.getDisplayAmount(rs.getLong("unit_value")));
				c2cBatchMasterVO.setBatchTotalRecord(rs.getInt("batch_total_record"));
				c2cBatchMasterVO.setNewRecords(rs.getInt("new"));
				c2cBatchMasterVO.setClosedRecords(rs.getInt("close"));
				c2cBatchMasterVO.setRejectedRecords(rs.getInt("cncl"));
				c2cBatchMasterVO.setNetworkCode(rs.getString("network_code"));
				c2cBatchMasterVO.setNetworkCodeFor(rs.getString("network_code_for"));
				c2cBatchMasterVO.setProductCode(rs.getString("product_code"));
				c2cBatchMasterVO.setModifiedBy(rs.getString("modified_by"));
				c2cBatchMasterVO.setModifiedOn(rs.getTimestamp("modified_on"));
				c2cBatchMasterVO.setProductType(rs.getString("product_type"));
				c2cBatchMasterVO.setProductShortName(rs.getString("short_name"));
				c2cBatchMasterVO.setDomainCode(rs.getString("domain_code"));
				c2cBatchMasterVO.setBatchDate(rs.getDate("batch_date"));
				c2cBatchMasterVO.setDefaultLang(rs.getString("sms_default_lang"));
				c2cBatchMasterVO.setSecondLang(rs.getString("sms_second_lang"));
				c2cBatchMasterVO.setTransferType(rs.getString("transfer_type"));
				c2cBatchMasterVO.setTransferSubType(rs.getString("transfer_sub_type"));
				c2cBatchMasterVO.setCreatedBy(rs.getString("created_by"));
				c2cBatchMasterVO.setUserId(rs.getString("user_id"));
				list.add(c2cBatchMasterVO);
			}
		} 
		catch (SQLException sqe)
		{
			_log.error(methodName, "SQLException : " + sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[loadBatchC2CMasterDetailsForWdr]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}
		catch (Exception ex)
		{
		    _log.error(methodName, "Exception : " + ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[loadBatchC2CMasterDetailsForWdr]","","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		}
		finally
		{
		    try{if (rs != null){rs.close();}} catch (Exception e){}
			try{if (pstmt != null){pstmt.close();}} catch (Exception e){}
			if (_log.isDebugEnabled()) _log.debug(methodName, "Exiting: c2cBatchMasterVOList size=" + list.size());
		}
		return list;
	}
	
	
	
	
	
	/**
	 * This methid will load the data from the c2c_batch_items table corresponding to batch id.
	 * The result will be returned as LinkedHasMap. The key will be batch_detail_id for this map.
	 * 
	 * @param p_con
	 * @param p_batchId
	 * @param p_itemStatus
	 * @return
	 * @throws BTSLBaseException
	 */
	public LinkedHashMap loadBatchItemsMap(Connection p_con,String p_optBatchId,String p_itemStatus) throws BTSLBaseException
	{
		final String methodName = "loadBatchItemsMap" ;
		if (_log.isDebugEnabled()) 	_log.debug(methodName, "Entered p_optBatchId="+p_optBatchId+" p_itemStatus="+p_itemStatus);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sqlSelect = operatorC2CBatchTransferQry.loadBatchItemsMapQry(p_itemStatus);
		if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		LinkedHashMap map=new LinkedHashMap();
		try
		{
			pstmt = p_con.prepareStatement(sqlSelect);
			pstmt.setString(1, p_optBatchId);
			pstmt.setString(2, PretupsI.CHANNEL_TRANSFER_BATCH_C2C_ITEM_RCRDSTATUS_PROCESSED);
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				C2CBatchItemsVO c2cBatchItemsVO=new C2CBatchItemsVO();
				c2cBatchItemsVO.setOptBatchId(p_optBatchId);
				c2cBatchItemsVO.setBatchId(rs.getString("batch_id"));
				c2cBatchItemsVO.setBatchDetailId(rs.getString("batch_detail_id"));
				c2cBatchItemsVO.setCategoryName(rs.getString("category_name"));
				c2cBatchItemsVO.setSenderMsisdn(rs.getString("sendermsisdn"));
				c2cBatchItemsVO.setMsisdn(rs.getString("msisdn"));
				c2cBatchItemsVO.setUserId(rs.getString("user_id"));
				c2cBatchItemsVO.setStatus(rs.getString("status"));
				c2cBatchItemsVO.setGradeName(rs.getString("grade_name"));
				c2cBatchItemsVO.setRequestedQuantity(rs.getLong("requested_quantity"));
				c2cBatchItemsVO.setTransferMrp(rs.getLong("transfer_mrp"));
				c2cBatchItemsVO.setInitiatedBy(rs.getString("created_by"));
				c2cBatchItemsVO.setInitiatedOn(rs.getTimestamp("created_on"));
				c2cBatchItemsVO.setLoginID(rs.getString("login_id"));
				c2cBatchItemsVO.setBatchModifiedOn(rs.getTimestamp("batch_modified_on"));
				c2cBatchItemsVO.setModifiedOn(rs.getTimestamp("modified_on"));
				c2cBatchItemsVO.setModifiedBy(rs.getString("modified_by"));
				c2cBatchItemsVO.setReferenceNo(rs.getString("reference_no"));
				c2cBatchItemsVO.setTransferDate(rs.getTimestamp("transfer_date"));
				c2cBatchItemsVO.setTxnProfile(rs.getString("txn_profile"));
				c2cBatchItemsVO.setCommissionProfileSetId(rs.getString("commission_profile_set_id"));
				c2cBatchItemsVO.setCommissionProfileVer(rs.getString("commission_profile_ver"));
				c2cBatchItemsVO.setCommissionProfileDetailId(rs.getString("commission_profile_detail_id"));
				c2cBatchItemsVO.setCommissionType(rs.getString("commission_type"));
                c2cBatchItemsVO.setCommissionRate(rs.getDouble("commission_rate"));
                c2cBatchItemsVO.setCommissionValue(rs.getLong("commission_value"));
                c2cBatchItemsVO.setTax1Type(rs.getString("tax1_type"));
                c2cBatchItemsVO.setTax1Rate(rs.getDouble("tax1_rate"));
                c2cBatchItemsVO.setTax1Value(rs.getLong("tax1_value"));
                c2cBatchItemsVO.setTax2Type(rs.getString("tax2_type"));
                c2cBatchItemsVO.setTax2Rate(rs.getDouble("tax2_rate"));
                c2cBatchItemsVO.setTax2Value(rs.getLong("tax2_value"));
                c2cBatchItemsVO.setTax3Type(rs.getString("tax3_type"));
                c2cBatchItemsVO.setTax3Rate(rs.getDouble("tax3_rate"));
                c2cBatchItemsVO.setTax3Value(rs.getLong("tax3_value"));
				c2cBatchItemsVO.setRequestedQuantity(rs.getLong("requested_quantity"));
				c2cBatchItemsVO.setTransferMrp(rs.getLong("transfer_mrp"));
				c2cBatchItemsVO.setInitiatorRemarks(rs.getString("initiator_remarks"));
				c2cBatchItemsVO.setApproverRemarks(rs.getString("approver_remarks"));
				c2cBatchItemsVO.setApprovedBy(rs.getString("approved_by"));
				c2cBatchItemsVO.setApprovedOn(rs.getTimestamp("approved_on"));
				c2cBatchItemsVO.setCancelledBy(rs.getString("cancelled_by"));
				c2cBatchItemsVO.setCancelledOn(rs.getTimestamp("cancelled_on"));
				c2cBatchItemsVO.setRcrdStatus(rs.getString("rcrd_status"));
				c2cBatchItemsVO.setGradeCode(rs.getString("user_grade_code"));
				c2cBatchItemsVO.setCategoryCode(rs.getString("category_code"));
				c2cBatchItemsVO.setApproverName(rs.getString("approver_name"));
				c2cBatchItemsVO.setInitiaterName(rs.getString("initiater_name"));
				c2cBatchItemsVO.setExternalCode(rs.getString("external_code"));
				c2cBatchItemsVO.setTransferType(rs.getString("transfer_type"));
				c2cBatchItemsVO.setTransferSubType(rs.getString("transfer_sub_type"));
				map.put(rs.getString("batch_detail_id"),c2cBatchItemsVO);
			}
		} 
		catch (SQLException sqe)
		{
			_log.error(methodName, "SQLException : " + sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[loadBatchItemsMap]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, "loadBatchItemsList", "error.general.sql.processing");
		}
		catch (Exception ex)
		{
		    _log.error(methodName, "Exception : " + ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[loadBatchItemsMap]","","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		}
		finally
		{
		    try{if (rs != null){rs.close();}} catch (Exception e){}
			try{if (pstmt != null){pstmt.close();}} catch (Exception e){}
			if (_log.isDebugEnabled()) _log.debug(methodName, "Exiting: loadBatchItemsMap map=" + map.size());
		}
		return map;
	}
	
	/**
	 *  Method to cancel/approve the batch. This also perform all the data validation.
	 * Also construct error list 
	 * Tables updated are: c2c_batch_items,c2c_batches
	 * @param p_con
	 * @param p_dataMap
	 * @param p_currentLevel
	 * @param p_userID
	 * @param p_messages
	 * @param p_locale
	 * @return
	 * @throws BTSLBaseException
	 */
	public ArrayList processOrderByBatch(Connection p_con,HashMap<C2CBatchMasterVO, ArrayList<C2CBatchItemsVO>> batchMasterItemsListMap,String p_currentLevel,MessageResources p_messages,Locale p_locale,String p_sms_default_lang,String p_sms_second_lang) throws BTSLBaseException
	{
		final String methodName = "processOrderByBatch";
		if (_log.isDebugEnabled()) 	_log.debug(methodName, "Entered p_dataMap="+batchMasterItemsListMap+" p_currentLevel="+p_currentLevel+" p_locale="+p_locale);
		ChannelUserVO p_senderVO = null;
		C2CBatchMasterVO p_batchMasterVO = null;
		C2CBatchItemsVO c2cBatchItemVO=null;		
		ArrayList p_batchItemsList = null;
		ArrayList errorList = new ArrayList();
		
		PreparedStatement pstmtLoadUser = null;
		PreparedStatement psmtCancelC2CBatchItem = null;
		PreparedStatement psmtApprC2CBatchItem = null;
		PreparedStatement pstmtUpdateMaster= null;
		//commented for DB2
		PreparedStatement pstmtSelectItemsDetails= null;
		PreparedStatement pstmtIsModified=null;
		PreparedStatement pstmtIsTxnNumExists1=null;
		PreparedStatement pstmtIsTxnNumExists2=null;
		ListValueVO errorVO=null;
		ResultSet rs = null;
		int updateCount=0;
		String batch_ID=null;
		/*The query below will be used to load user datils.
		 * That details is the validated for eg: transfer profile, commission profile, user status etc.
		 */
        StringBuffer sqlBuffer = new StringBuffer(" SELECT u.status userstatus, cusers.in_suspend, ");
		sqlBuffer.append("cps.status commprofilestatus,tp.status profile_status,cps.language_1_message comprf_lang_1_msg, ");
		sqlBuffer.append("cps.language_2_message comprf_lang_2_msg ");
        sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps,transfer_profile tp "); 
        sqlBuffer.append("WHERE u.user_id = ? AND u.status <> 'N' AND u.status <> 'C' ");
        sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
        sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND " );
        sqlBuffer.append(" tp.profile_id = cusers.transfer_profile_id  ");
		String sqlLoadUser = sqlBuffer.toString();
		if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY sqlLoadUser=" + sqlLoadUser);
		sqlBuffer=null;
		//after validating if request is to cancle the order, the below query is used.
        sqlBuffer = new StringBuffer(" UPDATE  c2c_batch_items SET   ");
        sqlBuffer.append(" modified_by = ?, modified_on = ?,  ");
		if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE.equals(p_currentLevel))
			sqlBuffer.append(" approver_remarks = ?, ");
		sqlBuffer.append(" cancelled_by = ?, ");
		sqlBuffer.append(" cancelled_on = ?, status = ?");
		sqlBuffer.append(" WHERE ");
		sqlBuffer.append(" batch_detail_id = ? ");
		sqlBuffer.append(" AND batch_id = ? AND opt_batch_id = ? ");
		if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE.equals(p_currentLevel))
			sqlBuffer.append(" AND status IN (? , ? )  ");
		String sqlCancelC2CBatchItems = sqlBuffer.toString();
        if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY sqlCancelC2CBatchItems=" + sqlCancelC2CBatchItems);
        sqlBuffer=null;
        
        // after validating if request is of level 1 approve the order, the below query is used.
        sqlBuffer = new StringBuffer(" UPDATE  c2c_batch_items SET   ");
        sqlBuffer.append(" modified_by = ?, modified_on = ?,  ");
		sqlBuffer.append(" approver_remarks = ?, ");
		sqlBuffer.append(" approved_by=?, approved_on=? , status = ?  ");
		sqlBuffer.append(" WHERE ");
		sqlBuffer.append(" batch_detail_id = ? ");
		sqlBuffer.append(" AND batch_id = ? AND opt_batch_id = ? ");
		sqlBuffer.append(" AND status IN (? , ? )  ");
		
        String sqlApprvC2CBatchItems = sqlBuffer.toString();
        if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY sqlApprvC2CBatchItems=" + sqlApprvC2CBatchItems);

        //Afetr all teh records are processed the the below query is used to load the various counts such as new ,
        //apprv1, close ,cancled etc. These couts will be used to deceide what status to be updated in master table
        String selectItemsDetails = operatorC2CBatchTransferQry.processOrderByBatchQry();
        if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY selectItemsDetails=" + selectItemsDetails);
        sqlBuffer=null;
       
        //The query below is used to update the master table after all items are processed
        sqlBuffer = new StringBuffer("UPDATE c2c_batches SET status=? , modified_by=? ,modified_on=?,sms_default_lang=? , sms_second_lang=?  ");
        sqlBuffer.append(" WHERE batch_id=? and opt_batch_id= ? AND status=? ");
        String updateC2CBatches = sqlBuffer.toString();
        if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY updateC2CBatches=" + updateC2CBatches);
        sqlBuffer=null;

        //The query below is used to check if the record is modified or not
        sqlBuffer = new StringBuffer("SELECT modified_on FROM c2c_batch_items ");
        sqlBuffer.append("WHERE batch_detail_id = ? ");
        sqlBuffer.append(" AND batch_id = ? AND opt_batch_id = ? ");
        String isModified = sqlBuffer.toString();
        if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY isModified=" + isModified);
        sqlBuffer=null;

        
        Date date=null;
		try
		{
			ChannelUserVO channelUserVO=null;
			date=new Date();
			//Create the prepared statements
			pstmtLoadUser = p_con.prepareStatement(sqlLoadUser);
			psmtCancelC2CBatchItem=(PreparedStatement)p_con.prepareStatement(sqlCancelC2CBatchItems);
			psmtApprC2CBatchItem=(PreparedStatement)p_con.prepareStatement(sqlApprvC2CBatchItems);
			pstmtSelectItemsDetails=p_con.prepareStatement(selectItemsDetails);
			pstmtUpdateMaster=(PreparedStatement)p_con.prepareStatement(updateC2CBatches);
			pstmtIsModified=p_con.prepareStatement(isModified);
			
			String p_userID = null;
			errorList=new ArrayList();
			Iterator batchMasterItemsListMapIter = batchMasterItemsListMap.entrySet().iterator();
			while(batchMasterItemsListMapIter.hasNext()){
				Map.Entry mapElement = (Map.Entry)batchMasterItemsListMapIter.next(); 
            	p_batchMasterVO = (C2CBatchMasterVO) mapElement.getKey(); 
            	p_batchItemsList = (ArrayList) mapElement.getValue();
            	p_userID = p_batchMasterVO.getUserId();
				for(int i=0,j=p_batchItemsList.size();i<j;i++) 
				{		
					c2cBatchItemVO=(C2CBatchItemsVO) p_batchItemsList.get(i);
					if(BTSLUtil.isNullString(batch_ID))
						batch_ID=c2cBatchItemVO.getBatchId();
				pstmtLoadUser.clearParameters();
				int m=0;
				pstmtLoadUser.setString(++m,c2cBatchItemVO.getUserId());
				rs=pstmtLoadUser.executeQuery();
				if(rs.next())//check data found or not
				{
					channelUserVO = new ChannelUserVO();
	                channelUserVO.setUserID(c2cBatchItemVO.getUserId());
	                channelUserVO.setStatus(rs.getString("userstatus"));
	                channelUserVO.setInSuspend(rs.getString("in_suspend"));
	                channelUserVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
	                channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
	                channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
	                channelUserVO.setTransferProfileStatus(rs.getString("profile_status"));
	                try{if (rs != null){rs.close();}} catch (Exception e){}
	                //(User status is checked) if this condition is true then made entry in logs and leave this data.
	       		 	if(!PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus()))
	       		 	{
	       		 		p_con.rollback();
	       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.usersuspend"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.c2cBatchItemLog(methodName,c2cBatchItemVO,"FAIL : User is not active","Approval level"+p_currentLevel);
						updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
						continue;
	       		 	}
	                //(commission profile status is checked) if this condition is true then made entry in logs and leave this data.
	       		 	else if(!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus()))
	                {
	                	p_con.rollback();
	       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.comprofsuspend"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.c2cBatchItemLog(methodName,c2cBatchItemVO,"FAIL : Commission profile is suspend","Approval level"+p_currentLevel);
						updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
						continue;
	                }
	                //(tranmsfer profile status is checked) if this condition is true then made entry in logs and leave this data.
	                else if(!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus()))
	                {
	                	p_con.rollback();
	       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.trfprofsuspend"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.c2cBatchItemLog(methodName,c2cBatchItemVO,"FAIL : Transfer profile is suspend","Approval level"+p_currentLevel);
						updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
						continue;
	                }
	                //(user in suspend is checked) if this condition is true then made entry in logs and leave this data.
	                else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend()))
	                {
	                	p_con.rollback();
	       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.userinsuspend"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.c2cBatchItemLog(methodName,c2cBatchItemVO,"FAIL : User is IN suspend","Approval level"+p_currentLevel);
						updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
						continue;
	                }
				}
                //(record not found for user) if this condition is true then made entry in logs and leave this data.
				else
				{
					p_con.rollback();
       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.nouser"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.c2cBatchItemLog(methodName,c2cBatchItemVO,"FAIL : User not found","Approval level"+p_currentLevel);
					updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
					continue;

				}
				pstmtIsModified.clearParameters();
				m=0;
				pstmtIsModified.setString(++m,c2cBatchItemVO.getBatchDetailId());
				pstmtIsModified.setString(++m,c2cBatchItemVO.getBatchId());
				pstmtIsModified.setString(++m,c2cBatchItemVO.getOptBatchId());
				rs=null;
				rs=pstmtIsModified.executeQuery();
				java.sql.Timestamp newlastModified = null;
				if (rs.next())
	            {
	                newlastModified = rs.getTimestamp("modified_on");
	                try{if (rs != null){rs.close();}} catch (Exception e){}
	            }
                //(record not found means it is modified) if this condition is true then made entry in logs and leave this data.
				else
				{
					p_con.rollback();
       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordmodified"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.c2cBatchItemLog(methodName,c2cBatchItemVO,"FAIL : Record is already modified by some one else","Approval level"+p_currentLevel);
					updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
					continue;
				}
                //if this condition is true then made entry in logs and leave this data.
				if(newlastModified.getTime()!=BTSLUtil.getTimestampFromUtilDate(c2cBatchItemVO.getModifiedOn()).getTime())
				{
					p_con.rollback();
       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordmodified"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.c2cBatchItemLog(methodName,c2cBatchItemVO,"FAIL : Record is already modified by some one else","Approval level"+p_currentLevel);
					updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
					continue;

				}
                
				//If operation is of cancle then set the fiels in psmtCancelC2CBatchItem
				if(PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL.equals(c2cBatchItemVO.getStatus()))
				{
					psmtCancelC2CBatchItem.clearParameters();
					m=0;
					psmtCancelC2CBatchItem.setString(++m,c2cBatchItemVO.getModifiedBy());
					psmtCancelC2CBatchItem.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
					if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE.equals(p_currentLevel))
					{
						psmtCancelC2CBatchItem.setString(++m,c2cBatchItemVO.getApproverRemarks());
					}
					
					psmtCancelC2CBatchItem.setString(++m,c2cBatchItemVO.getCancelledBy());
					psmtCancelC2CBatchItem.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
					psmtCancelC2CBatchItem.setString(++m,c2cBatchItemVO.getStatus());
					psmtCancelC2CBatchItem.setString(++m,c2cBatchItemVO.getBatchDetailId());
					psmtCancelC2CBatchItem.setString(++m,c2cBatchItemVO.getBatchId());
					psmtCancelC2CBatchItem.setString(++m,c2cBatchItemVO.getOptBatchId());
					if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE.equals(p_currentLevel))
		            {   
						psmtCancelC2CBatchItem.setString(++m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
						psmtCancelC2CBatchItem.setString(++m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE);
		            }
					updateCount=psmtCancelC2CBatchItem.executeUpdate();
				}
				//IF approval 1 is the operation then set parametrs in psmtAppr1C2CBatchItem
				else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE.equals(c2cBatchItemVO.getStatus()))
				{
					psmtApprC2CBatchItem.clearParameters();
					m=0;
					psmtApprC2CBatchItem.setString(++m,c2cBatchItemVO.getModifiedBy());
					psmtApprC2CBatchItem.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
					psmtApprC2CBatchItem.setString(++m,c2cBatchItemVO.getApproverRemarks());
					psmtApprC2CBatchItem.setString(++m,c2cBatchItemVO.getApprovedBy());
					psmtApprC2CBatchItem.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
					psmtApprC2CBatchItem.setString(++m,c2cBatchItemVO.getStatus());
					psmtApprC2CBatchItem.setString(++m,c2cBatchItemVO.getBatchDetailId());
					psmtApprC2CBatchItem.setString(++m,c2cBatchItemVO.getBatchId());
					psmtApprC2CBatchItem.setString(++m,c2cBatchItemVO.getOptBatchId());
					psmtApprC2CBatchItem.setString(++m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
					psmtApprC2CBatchItem.setString(++m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE);
		            updateCount=psmtApprC2CBatchItem.executeUpdate();
				}
				
				//If update count is <=0 that means record not updated in db properly so made entry in logs and leave this data
	            if(updateCount<=0)
				{
	            	p_con.rollback();
       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.c2cBatchItemLog(methodName,c2cBatchItemVO,"FAIL : DB Error while updating items table","Approval level"+p_currentLevel+", updateCount="+updateCount);
					updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
					continue;
				}
	            //commit the transaction after processiong each record
	            p_con.commit();
			}//End of for loop
		}//end of while
		//Check the status to be updated in master table agfter processing of all records
			

		}//end of try
		catch (SQLException sqe)
		{
			try{if(p_con!=null)p_con.rollback();}catch(Exception e){}
			_log.error(methodName, "SQLException : " + sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[processOrderByBatch]","","","","SQL Exception:"+sqe.getMessage());
			BatchC2CFileProcessLog.c2cBatchItemLog(methodName,null,"FAIL : updating batch items SQL Exception:"+sqe.getMessage(),"Approval level"+p_currentLevel+", BATCH_ID="+batch_ID);
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}
		catch (Exception ex)
		{
			try{if(p_con!=null)p_con.rollback();}catch(Exception e){}
		    _log.error(methodName, "Exception : " + ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[processOrderByBatch]","","","","Exception:"+ex.getMessage());
			BatchC2CFileProcessLog.c2cBatchItemLog(methodName,null,"FAIL : updating batch items Exception:"+ex.getMessage(),"Approval level"+p_currentLevel+", BATCH_ID="+batch_ID);
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		}
		finally
		{
			try{if (pstmtLoadUser != null){pstmtLoadUser.close();}} catch (Exception e){}
			try{if (psmtCancelC2CBatchItem != null){psmtCancelC2CBatchItem.close();}} catch (Exception e){}
			try{if (psmtApprC2CBatchItem != null){psmtApprC2CBatchItem.close();}} catch (Exception e){}
			try{if (pstmtIsModified != null){pstmtIsModified.close();}} catch (Exception e){}
			try{if (pstmtIsTxnNumExists1!= null){pstmtIsTxnNumExists1.close();}} catch (Exception e){}
			try{if (pstmtIsTxnNumExists2!= null){pstmtIsTxnNumExists2.close();}} catch (Exception e){}
			try
			{
				if (_log.isDebugEnabled()) _log.debug(methodName, "finally : errorList size=" + errorList.size());
				Iterator batchMasterItemsListMapIter = batchMasterItemsListMap.entrySet().iterator();
				while(batchMasterItemsListMapIter.hasNext()){
					Map.Entry mapElement = (Map.Entry)batchMasterItemsListMapIter.next(); 
	            	p_batchMasterVO = (C2CBatchMasterVO) mapElement.getKey(); 
	            	batch_ID = p_batchMasterVO.getBatchId();
	            	int m=0;
					pstmtSelectItemsDetails.clearParameters();
		            pstmtSelectItemsDetails.setString(++m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
		            pstmtSelectItemsDetails.setString(++m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE);
		            pstmtSelectItemsDetails.setString(++m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
		            pstmtSelectItemsDetails.setString(++m, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
		            pstmtSelectItemsDetails.setString(++m, p_batchMasterVO.getBatchId());
		            pstmtSelectItemsDetails.setString(++m, p_batchMasterVO.getOptBatchId());
		            rs=null;
		            rs=pstmtSelectItemsDetails.executeQuery();
		            if(rs.next())
		            {
		            	int totalCount=rs.getInt("batch_total_record");
		            	int closeCount=rs.getInt("close");
		            	int cnclCount=rs.getInt("cncl");
		            	if (_log.isDebugEnabled()) _log.debug(methodName, "finally : totalCount=" + totalCount+", closeCount="+closeCount+", cnclCount="+cnclCount);
		            	try{if (rs != null){rs.close();}} catch (Exception e){}
		            	String statusOfMaster=null;
		            	//If all records are canle then set cancelled in master table
		            	if(totalCount==cnclCount)
		            		statusOfMaster=PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_CANCEL;
		            	//IF close and cancel count are equal to total means no transaction is pending and mark status as closed
		             	else if(totalCount==closeCount+cnclCount)
		            		statusOfMaster=PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_CLOSE;
		             	//Otherwise set OPEN in mastrer table
		            	else
		            		statusOfMaster=PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_OPEN;
		            	m=0;
		            	pstmtUpdateMaster.clearParameters();
		            	pstmtUpdateMaster.setString(++m,statusOfMaster);
		            	pstmtUpdateMaster.setString(++m,p_batchMasterVO.getUserId());
		            	pstmtUpdateMaster.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
		            	pstmtUpdateMaster.setString(++m,p_sms_default_lang);
		            	pstmtUpdateMaster.setString(++m,p_sms_second_lang);
		            	pstmtUpdateMaster.setString(++m,p_batchMasterVO.getBatchId());
		            	pstmtUpdateMaster.setString(++m,p_batchMasterVO.getOptBatchId());
		            	pstmtUpdateMaster.setString(++m,PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_UNDERPROCESS);	            		            	
		            	
		            	updateCount=pstmtUpdateMaster.executeUpdate();
		            	if(updateCount<=0)
		            	{
		            		p_con.rollback();
		       		 		errorVO=new ListValueVO("","",p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.c2cBatchItemLog(methodName,null,"FAIL : DB Error while updating master table","Approval level"+p_currentLevel+", updateCount="+updateCount);
							EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[processOrderByBatch]","","","","Error while updating C2C_BATCHES table. Batch id="+batch_ID);
		            	}//end of if
		            }//end of if
			}//end of iterator
	            p_con.commit();
			}
			catch (SQLException sqe)
			{
				try{if(p_con!=null)p_con.rollback();}catch(Exception e){}
				_log.error(methodName, "SQLException : " + sqe);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[processOrderByBatch]","","","","SQL Exception:"+sqe.getMessage());
				BatchC2CFileProcessLog.c2cBatchItemLog(methodName,null,"FAIL : updating batch master SQL Exception:"+sqe.getMessage(),"Approval level"+p_currentLevel+", BATCH_ID="+batch_ID);
				throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
			}
			catch (Exception ex)
			{
				try{if(p_con!=null)p_con.rollback();}catch(Exception e){}
			    _log.error(methodName, "Exception : " + ex);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[processOrderByBatch]","","","","Exception:"+ex.getMessage());
				BatchC2CFileProcessLog.c2cBatchItemLog(methodName,null,"FAIL : updating batch master Exception:"+ex.getMessage(),"Approval level"+p_currentLevel+", BATCH_ID="+batch_ID);
				throw new BTSLBaseException(this, methodName, "error.general.processing");
			}
		    try{if (rs != null){rs.close();}} catch (Exception e){}
			try{if (pstmtSelectItemsDetails != null){pstmtSelectItemsDetails.close();}} catch (Exception e){}
			try{if (pstmtUpdateMaster != null){pstmtUpdateMaster.close();}} catch (Exception e){}

			if (_log.isDebugEnabled()) _log.debug(methodName, "Exiting: errorList size=" + errorList.size());
		}
		return errorList;
	}
	
	private void updateOperatorBatchC2CItemForFailedTransaction(Connection p_con, C2CBatchItemsVO p_c2cBatchItemVO,ListValueVO p_errorVO) throws SQLException {
		
		final String methodName = "updateOperatorBatchC2CItemForFailedTransaction";
		// after validating if request is of level 1 approve the order, the below query is used.
		StringBuffer sqlBuffer =null ;
        sqlBuffer = new StringBuffer(" UPDATE  c2c_batch_items SET  other_info= ?");
        sqlBuffer.append(" WHERE ");
		sqlBuffer.append(" batch_detail_id = ? ");
		sqlBuffer.append(" AND batch_id = ? AND opt_batch_id = ? ");
		sqlBuffer.append(" AND status IN (? , ? )  ");
		
        String sqlApprvC2CBatchItems = sqlBuffer.toString();
        if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY sqlApprvC2CBatchItems=" + sqlApprvC2CBatchItems);
        PreparedStatement psmtApprC2CBatchItem = null;
        psmtApprC2CBatchItem=(PreparedStatement)p_con.prepareStatement(sqlApprvC2CBatchItems);
        
        int m=0;
        
        psmtApprC2CBatchItem.clearParameters();
		psmtApprC2CBatchItem.setString(++m, p_errorVO.getOtherInfo2());
		psmtApprC2CBatchItem.setString(++m, p_c2cBatchItemVO.getBatchDetailId());
		psmtApprC2CBatchItem.setString(++m, p_c2cBatchItemVO.getBatchId());
		psmtApprC2CBatchItem.setString(++m, p_c2cBatchItemVO.getOptBatchId());
		psmtApprC2CBatchItem.setString(++m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
		psmtApprC2CBatchItem.setString(++m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE);
        
		int updateCount=psmtApprC2CBatchItem.executeUpdate();
        if (updateCount <= 0)
		{
        	p_con.rollback();
		} else {
			p_con.commit();
		}
	}

	/**
	 * Method to close the c2c order by batch. This also perform all the data validation.
	 * Also construct error list
	 * Tables updated are: network_stocks,network_daily_stocks,network_stock_transactions,network_stock_trans_items
	 * user_balances,user_daily_balances,user_transfer_counts,c2c_batch_items,c2c_batches,
	 * channel_transfers_items,channel_transfers
	 * 
	 * @param p_con
	 * @param p_dataMap
	 * @param p_currentLevel
	 * @param p_userID
	 * @param p_batchMasterVO
	 * @param p_messages
	 * @param p_locale
	 * @return
	 * @throws BTSLBaseException
	 */
	@SuppressWarnings("null")
	public ArrayList closeOrderByBatch(Connection p_con,HashMap<C2CBatchMasterVO, ArrayList<C2CBatchItemsVO>> batchMasterItemsListMap,String p_currentLevel,MessageResources p_messages,Locale p_locale,String p_sms_default_lang ,String p_sms_second_lang) throws BTSLBaseException
	{
		final String methodName =  "closeOrderByBatch";
		if (_log.isDebugEnabled()) 	_log.debug(methodName, "Entered batchMasterItemsListMap="+batchMasterItemsListMap+" p_currentLevel="+p_currentLevel+" p_locale="+p_locale);
		//basic convention in this method.
		//sender user is that who initiated the transfer whether transfer or withdraw
		ChannelUserVO p_senderVO = null;
		C2CBatchMasterVO p_batchMasterVO = null;
		C2CBatchItemsVO c2cBatchItemVO=null;
		ArrayList p_batchItemsList = null;
		ArrayList errorList = new ArrayList();
		
		PreparedStatement pstmtLoadUser = null;
		PreparedStatement pstmtSelectUserBalances=null;
		PreparedStatement pstmtUpdateUserBalances=null;
		PreparedStatement pstmtUpdateSenderBalanceOn=null;
		
		PreparedStatement pstmtInsertUserDailyBalances=null;
		PreparedStatement pstmtSelectBalance=null;
		
		PreparedStatement pstmtSelectSenderBalance=null;
		PreparedStatement pstmtUpdateSenderBalance=null;
		PreparedStatement pstmtInsertSenderDailyBalances=null;
		
		PreparedStatement pstmtUpdateBalance=null;
		PreparedStatement pstmtInsertBalance=null;
		PreparedStatement pstmtSelectTransferCounts=null;
		PreparedStatement pstmtSelectSenderTransferCounts=null;
		PreparedStatement pstmtSelectProfileCounts=null;
		PreparedStatement pstmtSelectSenderProfileOutCounts=null;
		PreparedStatement pstmtUpdateTransferCounts=null;
		PreparedStatement pstmtUpdateSenderTransferCounts=null;
		PreparedStatement pstmtInsertTransferCounts=null;
		PreparedStatement pstmtInsertSenderTransferCounts=null;
		PreparedStatement psmtApprC2CBatchItem = null;
		PreparedStatement pstmtSelectItemsDetails= null;
		PreparedStatement pstmtUpdateMaster= null;
		PreparedStatement pstmtIsModified=null;
		PreparedStatement pstmtLoadTransferProfileProduct=null;
		PreparedStatement handlerStmt = null;
		
		PreparedStatement pstmtInsertIntoChannelTransferItems=null;
		
		PreparedStatement pstmtInsertIntoChannelTranfers=null;
		PreparedStatement pstmtSelectBalanceInfoForMessage=null;
		//added by vikram
		PreparedStatement pstmtSelectCProfileProd = null;
		ArrayList userbalanceList=null;
		UserBalancesVO balancesVO = null;
		ListValueVO errorVO=null;
		ResultSet rs = null;
		String language=null;
		String country=null;
		KeyArgumentVO keyArgumentVO=null;
		String[] argsArr=null;
		ArrayList txnSmsMessageList=null;
		ArrayList balSmsMessageList=null;
		Locale locale=null;
		String[] array=null;
		BTSLMessages messages=null;
		PushMessage pushMessage=null;
		int updateCount=0;
		String c2cTransferID=null;
		PreparedStatement psmtInsertUserThreshold=null;
		long thresholdValue=-1;
		/*The query below will be used to load user datils.
		 * That details is the validated for eg: transfer profile, commission profile, user status etc.
		 */
        StringBuffer sqlBuffer = new StringBuffer(" SELECT u.status userstatus, cusers.in_suspend, ");
		sqlBuffer.append("cps.status commprofilestatus,tp.status profile_status,cps.language_1_message comprf_lang_1_msg, ");
		sqlBuffer.append("cps.language_2_message comprf_lang_2_msg,up.phone_language,up.country, ug.grph_domain_code ");
        sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps,transfer_profile tp, user_phones up,user_geographies ug "); 
        sqlBuffer.append("WHERE u.user_id = ? AND up.user_id=u.user_id AND up.primary_number = 'Y' ");
        sqlBuffer.append(" AND u.status <> 'N' AND u.status <> 'C' ");
        sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
        sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND " );
        sqlBuffer.append(" tp.profile_id = cusers.transfer_profile_id AND ug.user_id = u.user_id ");
		String sqlLoadUser = sqlBuffer.toString();
		if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY sqlLoadUser=" + sqlLoadUser);
		
		//The query below is used to load the user balance
		//This table will basically used to update the daily_balance_updated_on and also to know how many
		//records are to be inserted in user_daily_balances table
			
		String selectUserBalances = c2CBatchTransferQry.closeBatchC2CTransferUserBalanceQry();
		if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY selectUserBalances=" + selectUserBalances);
		sqlBuffer=null;
		
		//update daily_balance_updated_on with current date for user
		sqlBuffer=new StringBuffer(" UPDATE user_balances SET daily_balance_updated_on = ? ");
		sqlBuffer.append("WHERE user_id = ? ");
		String updateUserBalances = sqlBuffer.toString();
		if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY updateUserBalances=" + updateUserBalances);
		sqlBuffer=null;
		
		//Executed if day difference in last updated date and current date is greater then or equal to 1
		//Insert number of records equal to day difference in last updated date and current date in  user_daily_balances
		sqlBuffer=new StringBuffer(" INSERT INTO user_daily_balances(balance_date, user_id, network_code, ");
		sqlBuffer.append("network_code_for, product_code, balance, prev_balance, last_transfer_type, ");
		sqlBuffer.append("last_transfer_no, last_transfer_on, created_on,creation_type )");
		sqlBuffer.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?) ");
		String insertDailyBalances = sqlBuffer.toString();
		if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY insertUserDailyBalances=" + insertDailyBalances);
		
		String selectBalance = c2CBatchTransferQry.closeBatchC2CTransferBalanceQry();
		if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY selectBalance=" + selectBalance);
		sqlBuffer=null;
		
		//Credit the user balance(If balance found in user_balances)
		sqlBuffer=new StringBuffer(" UPDATE user_balances SET prev_balance = ?, balance = ? , last_transfer_type = ? , "); 
		sqlBuffer.append(" last_transfer_no = ? , last_transfer_on = ? ");
		sqlBuffer.append(" WHERE ");
		sqlBuffer.append(" user_id = ? ");
		sqlBuffer.append(" AND "); 
		sqlBuffer.append(" product_code = ? AND network_code = ? AND network_code_for = ? ");
		String updateBalance = sqlBuffer.toString();
		if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY updateBalance=" + updateBalance);
		sqlBuffer=null;
		
				
		//Insert the record of balnce for user (If balance not found in user_balances)
		sqlBuffer=new StringBuffer(" INSERT "); 
		sqlBuffer.append(" INTO user_balances ");
		sqlBuffer.append(" ( prev_balance,daily_balance_updated_on , balance, last_transfer_type, last_transfer_no, last_transfer_on , "); 
		sqlBuffer.append(" user_id, product_code , network_code, network_code_for ) ");
		sqlBuffer.append(" VALUES ");
		sqlBuffer.append(" (?,?,?,?,?,?,?,?,?,?) ");			
		String insertBalance = sqlBuffer.toString();
		if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY insertBalance=" + insertBalance);
		sqlBuffer=null;
		
		//Select the running countres of user(to be checked against the effetive profile counters)
		sqlBuffer=new StringBuffer(" SELECT user_id, daily_in_count, daily_in_value, weekly_in_count, weekly_in_value, ");
		sqlBuffer.append(" monthly_in_count, monthly_in_value,daily_out_count, daily_out_value, weekly_out_count, ");
		sqlBuffer.append(" weekly_out_value, monthly_out_count, monthly_out_value, outside_daily_in_count, ");
		sqlBuffer.append(" outside_daily_in_value, outside_weekly_in_count, outside_weekly_in_value, ");
		sqlBuffer.append(" outside_monthly_in_count, outside_monthly_in_value, outside_daily_out_count, ");
		sqlBuffer.append(" outside_daily_out_value, outside_weekly_out_count, outside_weekly_out_value, ");
		sqlBuffer.append(" outside_monthly_out_count, outside_monthly_out_value, daily_subscriber_out_count, ");
		sqlBuffer.append(" daily_subscriber_out_value, weekly_subscriber_out_count, weekly_subscriber_out_value, ");
		sqlBuffer.append(" monthly_subscriber_out_count, monthly_subscriber_out_value,last_transfer_date ");
		sqlBuffer.append(" FROM user_transfer_counts ");
		//commented for DB2 & DB220120123for update WITH RS 
		//sqlBuffer.append(" WHERE user_id = ? FOR UPDATE ");
		 if(PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype")))
		sqlBuffer.append(" WHERE user_id = ? FOR UPDATE  WITH RS");
		else
			sqlBuffer.append(" WHERE user_id = ? FOR UPDATE ");	
		
		String selectTransferCounts = sqlBuffer.toString();
		if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY selectTransferCounts=" + selectTransferCounts);
		sqlBuffer=null;
		
		//Select the effective profile counters of user to be checked with running counters of user
		StringBuffer strBuff=new StringBuffer();
		strBuff.append(" SELECT tp.profile_id,LEAST(tp.daily_transfer_in_count,catp.daily_transfer_in_count) daily_transfer_in_count, "); 
		strBuff.append(" LEAST(tp.daily_transfer_in_value,catp.daily_transfer_in_value) daily_transfer_in_value ,LEAST(tp.weekly_transfer_in_count,catp.weekly_transfer_in_count) weekly_transfer_in_count, ");
		strBuff.append(" LEAST(tp.weekly_transfer_in_value,catp.weekly_transfer_in_value) weekly_transfer_in_value,LEAST(tp.monthly_transfer_in_count,catp.monthly_transfer_in_count) monthly_transfer_in_count, ");
		strBuff.append(" LEAST(tp.monthly_transfer_in_value,catp.monthly_transfer_in_value) monthly_transfer_in_value");
		strBuff.append(" FROM transfer_profile tp,transfer_profile catp ");
		strBuff.append(" WHERE tp.profile_id = ? AND tp.status = ?	AND tp.network_code = ? ");
		strBuff.append(" AND tp.category_code=catp.category_code ");	
		strBuff.append(" AND catp.parent_profile_id=? AND catp.status=?	AND tp.network_code = catp.network_code ");
		String selectProfileInCounts = strBuff.toString();
		if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY selectProfileInCounts=" + selectProfileInCounts);
	
		//Select the effective profile counters of sender to be checked with running counters of sender added by Gopal
		StringBuffer strBuff1=new StringBuffer();
		strBuff1.append(" SELECT tp.profile_id,LEAST(tp.daily_transfer_out_count,catp.daily_transfer_out_count) daily_transfer_out_count, "); 
		strBuff1.append(" LEAST(tp.daily_transfer_out_value,catp.daily_transfer_out_value) daily_transfer_out_value ,LEAST(tp.weekly_transfer_out_count,catp.weekly_transfer_out_count) weekly_transfer_out_count, ");
		strBuff1.append(" LEAST(tp.weekly_transfer_out_value,catp.weekly_transfer_out_value) weekly_transfer_out_value,LEAST(tp.monthly_transfer_out_count,catp.monthly_transfer_out_count) monthly_transfer_out_count, ");
		strBuff1.append(" LEAST(tp.monthly_transfer_out_value,catp.monthly_transfer_out_value) monthly_transfer_out_value");
		strBuff1.append(" FROM transfer_profile tp,transfer_profile catp ");
		strBuff1.append(" WHERE tp.profile_id = ? AND tp.status = ?	AND tp.network_code = ? ");
		strBuff1.append(" AND tp.category_code=catp.category_code ");	
		strBuff1.append(" AND catp.parent_profile_id=? AND catp.status=?	AND tp.network_code = catp.network_code ");
		String selectProfileOutCounts = strBuff1.toString();
		if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY selectProfileOutCounts=" + selectProfileOutCounts);
	
		//Update the user running countres (If record found for user running counters)
		sqlBuffer=new StringBuffer(" UPDATE user_transfer_counts  SET "); 
		sqlBuffer.append(" daily_in_count = ?, daily_in_value = ?, weekly_in_count = ?, weekly_in_value = ?,");
        sqlBuffer.append(" monthly_in_count = ?, monthly_in_value = ? ,daily_out_count =?, daily_out_value=?, ");
		sqlBuffer.append(" weekly_out_count=?, weekly_out_value =?, monthly_out_count=?, monthly_out_value=?, ");
		sqlBuffer.append(" outside_daily_in_count=?, outside_daily_in_value=?, outside_weekly_in_count=?,");
		sqlBuffer.append(" outside_weekly_in_value=?, outside_monthly_in_count=?, outside_monthly_in_value=?, ");
		sqlBuffer.append(" outside_daily_out_count=?, outside_daily_out_value=?, outside_weekly_out_count=?, ");
		sqlBuffer.append(" outside_weekly_out_value=?, outside_monthly_out_count=?, outside_monthly_out_value=?, ");
		sqlBuffer.append(" daily_subscriber_out_count=?, daily_subscriber_out_value=?, weekly_subscriber_out_count=?, ");
		sqlBuffer.append(" weekly_subscriber_out_value=?, monthly_subscriber_out_count=?, monthly_subscriber_out_value=?, ");
        sqlBuffer.append(" last_in_time = ? , last_transfer_id=?,last_transfer_date=? "); 
        sqlBuffer.append(" WHERE user_id = ?  ");			
        String updateTransferCounts = sqlBuffer.toString();
		if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY updateTransferCounts=" + updateTransferCounts);
		sqlBuffer=null;
		
		
//		Update the Sender running countres (If record found for user running counters)
		sqlBuffer=new StringBuffer(" UPDATE user_transfer_counts  SET "); 
		sqlBuffer.append(" daily_in_count = ?, daily_in_value = ?, weekly_in_count = ?, weekly_in_value = ?,");
        sqlBuffer.append(" monthly_in_count = ?, monthly_in_value = ? ,daily_out_count =?, daily_out_value=?, ");
		sqlBuffer.append(" weekly_out_count=?, weekly_out_value =?, monthly_out_count=?, monthly_out_value=?, ");
		sqlBuffer.append(" outside_daily_in_count=?, outside_daily_in_value=?, outside_weekly_in_count=?,");
		sqlBuffer.append(" outside_weekly_in_value=?, outside_monthly_in_count=?, outside_monthly_in_value=?, ");
		sqlBuffer.append(" outside_daily_out_count=?, outside_daily_out_value=?, outside_weekly_out_count=?, ");
		sqlBuffer.append(" outside_weekly_out_value=?, outside_monthly_out_count=?, outside_monthly_out_value=?, ");
		sqlBuffer.append(" daily_subscriber_out_count=?, daily_subscriber_out_value=?, weekly_subscriber_out_count=?, ");
		sqlBuffer.append(" weekly_subscriber_out_value=?, monthly_subscriber_out_count=?, monthly_subscriber_out_value=?, ");
        sqlBuffer.append(" last_out_time = ? , last_transfer_id=?,last_transfer_date=? "); 
        sqlBuffer.append(" WHERE user_id = ?  ");			
        String updateSenderTransferCounts = sqlBuffer.toString();
		if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY updateSenderTransferCounts=" + updateSenderTransferCounts);
		sqlBuffer=null;
		
		
		
        
		//Insert the record in user_transfer_counts (If no record found for user running counters)
        sqlBuffer=new StringBuffer(" INSERT INTO user_transfer_counts ( ");
        sqlBuffer.append(" daily_in_count, daily_in_value, weekly_in_count, weekly_in_value, monthly_in_count, ");
        sqlBuffer.append(" monthly_in_value, last_in_time, last_transfer_id,last_transfer_date,user_id ) ");
        sqlBuffer.append(" VALUES (?,?,?,?,?,?,?,?,?,?) ");
        String insertTransferCounts = sqlBuffer.toString();
		if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY insertTransferCounts=" + insertTransferCounts);
		sqlBuffer=null;
		
		
		//Insert the record in user_transfer_counts for Sender (If no record found for user running counters)
        sqlBuffer=new StringBuffer(" INSERT INTO user_transfer_counts ( ");
        sqlBuffer.append(" daily_out_count, daily_out_value, weekly_out_count, weekly_out_value, monthly_out_count, ");
        sqlBuffer.append(" monthly_out_value, last_out_time, last_transfer_id,last_transfer_date,user_id ) ");
        sqlBuffer.append(" VALUES (?,?,?,?,?,?,?,?,?,?) ");
        String insertSenderTransferCounts = sqlBuffer.toString();
		if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY insertSenderTransferCounts=" + insertSenderTransferCounts);
		sqlBuffer=null;
		
		
        //If current level of approval is 1 then below query is used to updatwe c2c_batch_items table
        sqlBuffer = new StringBuffer(" UPDATE  c2c_batch_items SET   ");
        sqlBuffer.append(" reference_no=?, modified_by = ?, modified_on = ?,  ");
		sqlBuffer.append(" approver_remarks = ?, ");
		sqlBuffer.append(" approved_by=?, approved_on=? , status = ?  ");
		sqlBuffer.append(" WHERE ");
		sqlBuffer.append(" batch_detail_id = ? ");
		sqlBuffer.append(" AND batch_id = ? AND opt_batch_id = ? ");
		sqlBuffer.append(" AND status IN (? , ? )  ");
        String sqlApprvC2CBatchItems = sqlBuffer.toString();
        if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY sqlApprvC2CBatchItems=" + sqlApprvC2CBatchItems);
        
        //Afetr all the records are processed the the below query is used to load the various counts such as new ,
        //apprv1, close ,cancled etc. These counts will be used to deceide what status to be updated in master table
        
        String selectItemsDetails = operatorC2CBatchTransferQry.closeOrderByBatchQry();
        if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY selectItemsDetails=" + selectItemsDetails);
        sqlBuffer=null;
        
        //Update the master table after all records are processed
        sqlBuffer = new StringBuffer("UPDATE c2c_batches SET status=? , modified_by=? ,modified_on=? ,sms_default_lang=? ,sms_second_lang=?");
        sqlBuffer.append(" WHERE batch_id=? AND opt_batch_id=? AND status=? ");
        String updateC2CBatches = sqlBuffer.toString();
        if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY updateC2CBatches=" + updateC2CBatches);
        sqlBuffer=null;

        //The query below is used to check is record is already modified or not
        sqlBuffer = new StringBuffer("SELECT modified_on FROM c2c_batch_items ");
        sqlBuffer.append("WHERE batch_detail_id = ? AND batch_id=? AND opt_batch_id=? ");
        String isModified = sqlBuffer.toString();
        if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY isModified=" + isModified);
        sqlBuffer=null;

        //Select the transfer profile product values(These will be used for checking max balance of user)
        sqlBuffer = new StringBuffer("SELECT GREATEST(tpp.min_residual_balance,catpp.min_residual_balance) min_residual_balance, ");
        sqlBuffer.append(" LEAST(tpp.max_balance,catpp.max_balance) max_balance ");
        sqlBuffer.append(" FROM transfer_profile_products tpp,transfer_profile tp, transfer_profile catp,transfer_profile_products catpp ");
        sqlBuffer.append(" WHERE tpp.profile_id=? AND tpp.product_code = ? AND tpp.profile_id=tp.profile_id AND catp.profile_id=catpp.profile_id "); 
        sqlBuffer.append(" AND tpp.product_code=catpp.product_code AND tp.category_code=catp.category_code AND catp.parent_profile_id=? AND catp.status=? AND tp.network_code = catp.network_code	 ");
        String loadTransferProfileProduct = sqlBuffer.toString();
        if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY loadTransferProfileProduct=" + loadTransferProfileProduct);
        sqlBuffer=null;
        
        
        
        //The query below is used to insert the record in channel transfer items table for the order that is closed
        sqlBuffer = new StringBuffer(" INSERT INTO channel_transfers_items ");
        sqlBuffer.append("(approved_quantity, commission_profile_detail_id, commission_rate, commission_type, commission_value, mrp,  ");
        sqlBuffer.append(" net_payable_amount, payable_amount, product_code, receiver_previous_stock, required_quantity, s_no,  ");
        sqlBuffer.append(" sender_previous_stock, tax1_rate, tax1_type, tax1_value, tax2_rate, tax2_type, tax2_value, tax3_rate, tax3_type,  ");
        sqlBuffer.append(" tax3_value, transfer_date, transfer_id, user_unit_price, ");
        sqlBuffer.append(" sender_debit_quantity, receiver_credit_quantity, sender_post_stock, receiver_post_stock )  ");
        sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)  ");
        String insertIntoChannelTransferItem = sqlBuffer.toString();
        if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY insertIntoChannelTransferItem=" + insertIntoChannelTransferItem);
        sqlBuffer=null;
        
        //The query below is used to insert the record in channel transfers table for the order that is cloaed
        sqlBuffer = new StringBuffer(" INSERT INTO channel_transfers ");
        sqlBuffer.append(" (cancelled_by, cancelled_on, channel_user_remarks, close_date, commission_profile_set_id, commission_profile_ver, ");
        sqlBuffer.append(" created_by, created_on, domain_code, ext_txn_date, ext_txn_no, first_approved_by, first_approved_on, ");
        sqlBuffer.append(" first_approver_limit, first_approver_remarks, batch_date, batch_no, opt_batch_no, from_user_id, grph_domain_code, ");
        sqlBuffer.append(" modified_by, modified_on, net_payable_amount, network_code, network_code_for, payable_amount, pmt_inst_amount, ");
        sqlBuffer.append("  product_type, receiver_category_code, receiver_grade_code, ");
        sqlBuffer.append(" receiver_txn_profile, reference_no, request_gateway_code, request_gateway_type, requested_quantity, second_approved_by, ");
        sqlBuffer.append(" second_approved_on, second_approver_limit, second_approver_remarks,  ");
        sqlBuffer.append("  source, status, third_approved_by, third_approved_on, third_approver_remarks, to_user_id,  ");
        sqlBuffer.append(" total_tax1, total_tax2, total_tax3, transfer_category, transfer_date, transfer_id, transfer_initiated_by, ");
        sqlBuffer.append(" transfer_mrp, transfer_sub_type, transfer_type, type,sender_category_code,");
        sqlBuffer.append(" sender_grade_code, sender_txn_profile, ");
		sqlBuffer.append(" control_transfer,msisdn,to_msisdn,to_domain_code,to_grph_domain_code, sms_default_lang, sms_second_lang,active_user_id) ");
        sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        String insertIntoChannelTransfer = sqlBuffer.toString();
        if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY insertIntoChannelTransfer=" + insertIntoChannelTransfer);
        sqlBuffer=null;
        
        //The query below is used to get the balance information of user with product.
        //This information will be send in message to user
        sqlBuffer = new StringBuffer(" SELECT UB.product_code,UB.balance, ");
        sqlBuffer.append(" PROD.product_short_code, PROD.short_name ");
        sqlBuffer.append(" FROM user_balances UB,products PROD ");
        sqlBuffer.append(" WHERE UB.user_id = ?  AND UB.network_code = ? AND UB.network_code_for = ? AND UB.product_code=PROD.product_code "); 
        String selectBalanceInfoForMessage = sqlBuffer.toString();
        if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY selectBalanceInfoForMessage=" + selectBalanceInfoForMessage);
        sqlBuffer=null;
        
        // for loading the products associated with the commission profile added by vikram.
		sqlBuffer = new StringBuffer("SELECT cp.discount_type,cp.discount_rate,cp.taxes_on_channel_transfer ");
		sqlBuffer.append("FROM commission_profile_products cp ");
		sqlBuffer.append("WHERE  cp.product_code = ? AND cp.comm_profile_set_id = ? AND cp.comm_profile_set_version = ? ");
		String strBuffSelectCProfileProd=sqlBuffer.toString();
		if (_log.isDebugEnabled()) 	_log.debug(methodName, "strBuffSelectCProfileProd Query ="+strBuffSelectCProfileProd);
		
		StringBuffer strBuffThresholdInsert = new StringBuffer();
        strBuffThresholdInsert.append(" INSERT INTO user_threshold_counter ");
        strBuffThresholdInsert.append(" ( user_id,transfer_id , entry_date, entry_date_time, network_code, product_code , "); 
        strBuffThresholdInsert.append(" type , transaction_type, record_type, category_code,previous_balance,current_balance, threshold_value, threshold_type,remark ) ");
        strBuffThresholdInsert.append(" VALUES ");
        strBuffThresholdInsert.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");   
        String insertUserThreshold = strBuffThresholdInsert.toString();
        if (_log.isDebugEnabled())
        {
            _log.debug(methodName, "QUERY insertUserThreshold=" + insertUserThreshold);
        }
		
		sqlBuffer=null;
        Date date=null;
        String batch_ID=null;
        ChannelTransferVO channelTransferVO=null;
		try
		{
			ChannelUserVO receiverChannelUserVO=null;
			ChannelTransferItemsVO channelTransferItemVO=null;
			date=new Date();
			ArrayList channelTransferItemVOList=null;
			
			pstmtLoadUser = p_con.prepareStatement(sqlLoadUser);
			pstmtSelectUserBalances=p_con.prepareStatement(selectUserBalances);
			pstmtUpdateUserBalances=p_con.prepareStatement(updateUserBalances);
			
			pstmtUpdateSenderBalanceOn=p_con.prepareStatement(updateUserBalances);
			
			pstmtInsertUserDailyBalances=p_con.prepareStatement(insertDailyBalances);
			
			pstmtInsertSenderDailyBalances=p_con.prepareStatement(insertDailyBalances);
			pstmtSelectSenderBalance=p_con.prepareStatement(selectUserBalances);
			pstmtUpdateSenderBalance=p_con.prepareStatement(updateBalance);
			
			pstmtSelectBalance=p_con.prepareStatement(selectBalance);
			pstmtUpdateBalance=p_con.prepareStatement(updateBalance);
			pstmtInsertBalance=p_con.prepareStatement(insertBalance);
			pstmtSelectTransferCounts=p_con.prepareStatement(selectTransferCounts);
			pstmtSelectSenderTransferCounts=p_con.prepareStatement(selectTransferCounts);
			pstmtSelectProfileCounts=p_con.prepareStatement(selectProfileInCounts);
			pstmtSelectSenderProfileOutCounts=p_con.prepareStatement(selectProfileOutCounts);
			pstmtUpdateTransferCounts=p_con.prepareStatement(updateTransferCounts);
			pstmtUpdateSenderTransferCounts=p_con.prepareStatement(updateSenderTransferCounts);
			pstmtInsertTransferCounts=p_con.prepareStatement(insertTransferCounts);
			pstmtInsertSenderTransferCounts=p_con.prepareStatement(insertSenderTransferCounts);
			psmtApprC2CBatchItem=(PreparedStatement)p_con.prepareStatement(sqlApprvC2CBatchItems);
			pstmtSelectItemsDetails=p_con.prepareStatement(selectItemsDetails);
			pstmtUpdateMaster=(PreparedStatement)p_con.prepareStatement(updateC2CBatches);
			pstmtIsModified=p_con.prepareStatement(isModified);
			pstmtLoadTransferProfileProduct=p_con.prepareStatement(loadTransferProfileProduct);
			
			pstmtInsertIntoChannelTransferItems=p_con.prepareStatement(insertIntoChannelTransferItem);
			pstmtInsertIntoChannelTranfers=(PreparedStatement)p_con.prepareStatement(insertIntoChannelTransfer);
			pstmtSelectBalanceInfoForMessage=p_con.prepareStatement(selectBalanceInfoForMessage);
			//added by vikram
			pstmtSelectCProfileProd=p_con.prepareStatement(strBuffSelectCProfileProd);
            psmtInsertUserThreshold=p_con.prepareStatement(insertUserThreshold);
			
			long senderPreviousBal=-1;			//taking sender previous balance as 0
			
			errorList=new ArrayList();
			String key=null;
    		MessageGatewayVO messageGatewayVO=MessageGatewayCache.getObject((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WEB_GATEWAY_CODE)));
    		int dayDifference=0;
    		String network_id=null;
    		Date dailyBalanceUpdatedOn=null;
            TransferProfileProductVO transferProfileProductVO=null;
            UserTransferCountsVO countsVO = null;
            UserTransferCountsVO senderCountsVO=null;
   			TransferProfileVO transferProfileVO=null;
   			TransferProfileVO senderTfrProfileCheckVO=null;
			long maxBalance=0;
			boolean isNotToExecuteQuery = false;
            long balance = -1;
            long senderBalance=-1;
            long previousUserBalToBeSetChnlTrfItems=-1;
			long previousSenderBalToBeSetChnlTrfItems=-1;
            int m=0;
            int k=0;
            boolean flag = true;
			boolean terminateProcessing=false;
			
			Iterator batchMasterItemsListMapIter = batchMasterItemsListMap.entrySet().iterator();
			while(batchMasterItemsListMapIter.hasNext()){
				Map.Entry mapElement = (Map.Entry)batchMasterItemsListMapIter.next(); 
            	p_batchMasterVO = (C2CBatchMasterVO) mapElement.getKey(); 
            	p_batchItemsList = (ArrayList) mapElement.getValue();
            	p_senderVO = p_batchMasterVO.getChannelUserVO();
            	if (_log.isDebugEnabled())  _log.debug(methodName, "PROCESSING For p_senderVO=" + p_senderVO.toString());
				terminateProcessing=false;
				
				for(int i=0,j=p_batchItemsList.size();i<j;i++) 
				{
				c2cBatchItemVO=(C2CBatchItemsVO) p_batchItemsList.get(i);
				if(BTSLUtil.isNullString(batch_ID))
					batch_ID=c2cBatchItemVO.getBatchId();
		        if (_log.isDebugEnabled())  _log.debug(methodName, "Executed c2cBatchItemVO=" + c2cBatchItemVO.toString());
				pstmtLoadUser.clearParameters();
				m=0;
				pstmtLoadUser.setString(++m,c2cBatchItemVO.getUserId());
				rs=pstmtLoadUser.executeQuery();
                //(record found for user i.e. receiver) if this condition is not true then made entry in logs and leave this data.
				if(rs.next())
				{
					receiverChannelUserVO = new ChannelUserVO();
					receiverChannelUserVO.setUserID(c2cBatchItemVO.getUserId());
	                receiverChannelUserVO.setStatus(rs.getString("userstatus"));
	                receiverChannelUserVO.setInSuspend(rs.getString("in_suspend"));
	                receiverChannelUserVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
	                receiverChannelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
	                receiverChannelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
	                receiverChannelUserVO.setTransferProfileStatus(rs.getString("profile_status"));
	                language=rs.getString("phone_language");
	                country=rs.getString("country");
	                receiverChannelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
	                try{if (rs != null){rs.close();}} catch (Exception e){}
	                //(user status is checked) if this condition is true then made entry in logs and leave this data.
	       		 	if(!PretupsI.USER_STATUS_ACTIVE.equals(receiverChannelUserVO.getStatus()))
	       		 	{
	       		 		p_con.rollback();
	       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.usersuspend"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : User is suspend","Approval level"+p_currentLevel);
						updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
						continue;
	       		 	}
	                //(commission profile status is checked) if this condition is true then made entry in logs and leave this data.
	       		 	else if(!PretupsI.YES.equals(receiverChannelUserVO.getCommissionProfileStatus()))
	                {
	                	p_con.rollback();
	       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.comprofsuspend"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : Commission profile suspend","Approval level"+p_currentLevel);
						updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
						continue;
	                }
	                //(transfer profile is checked) if this condition is true then made entry in logs and leave this data.
	                else if(!PretupsI.YES.equals(receiverChannelUserVO.getTransferProfileStatus()))
	                {
	                	p_con.rollback();
	       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.trfprofsuspend"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : Transfer profile suspend","Approval level"+p_currentLevel);
						updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
						continue;
	                }
	                //(user in suspend  is checked) if this condition is true then made entry in logs and leave this data.
	                else if (receiverChannelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(receiverChannelUserVO.getInSuspend()))
	                {
	                	p_con.rollback();
	       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.userinsuspend"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : User is IN suspend","Approval level"+p_currentLevel);
						updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
						continue;
	                }
				}
                //(no record found for user i.e. receiver) if this condition is true then make entry in logs and leave this data.
				else
				{
					p_con.rollback();
       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.nouser"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : User not found","Approval level"+p_currentLevel);
					updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
					continue;
				}
				// creating the channelTransferVO here since C2CTransferID will be required into the network stock
				// transaction table. Other information will be set into this VO later
				channelTransferVO=new ChannelTransferVO();
				// seting the current value for generation of the transfer ID. This will be over write by the
				// bacth c2c items was created.
				channelTransferVO.setCreatedOn(date);
	    		channelTransferVO.setNetworkCode(p_batchMasterVO.getNetworkCode());
	    		channelTransferVO.setNetworkCodeFor(p_batchMasterVO.getNetworkCodeFor());

				//ChannelTransferBL.genrateTransferID(channelTransferVO);
	    		if(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(c2cBatchItemVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(c2cBatchItemVO.getTransferSubType()))
	    		    ChannelTransferBL.genrateChnnlToChnnlTrfID(channelTransferVO);
	    		else if(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(c2cBatchItemVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(c2cBatchItemVO.getTransferSubType()))
	    		    ChannelTransferBL.genrateChnnlToChnnlWithdrawID(channelTransferVO);
	    		/*else if(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(c2cBatchItemVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(c2cBatchItemVO.getTransferSubType()))
	    		    ChannelTransferBL.genrateChnnlToChnnlReturnID(channelTransferVO);
	    		*/
				c2cTransferID=channelTransferVO.getTransferID();
				// value is over writing since in the channel trasnfer table created on should be same as when the
				// batch c2c item was created.
				channelTransferVO.setCreatedOn(c2cBatchItemVO.getInitiatedOn());
				
				dayDifference=0;
				
                dailyBalanceUpdatedOn=null;
				dayDifference=0;
				
				
				pstmtSelectSenderBalance.clearParameters();
				m=0;
				pstmtSelectSenderBalance.setString(++m,p_senderVO.getUserID());
				pstmtSelectSenderBalance.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
				rs=null;
				rs=pstmtSelectSenderBalance.executeQuery();
				while(rs.next())
				{
					dailyBalanceUpdatedOn=rs.getDate("daily_balance_updated_on");
					senderPreviousBal=rs.getLong("balance");
					//if record exist check updated on date with current date
					//day differences to maintain the record of previous days.
					dayDifference=BTSLUtil.getDifferenceInUtilDates(dailyBalanceUpdatedOn,date);
					if(dayDifference>0)
					{
						//if dates are not equal get the day differencts and execute insert qurery no of times of the 
						if(_log.isDebugEnabled())
							_log.debug("closeOrdersByBatch ","Till now daily Stock is not updated on "+date+", day differences = "+dayDifference);
						
						for(k=0;k<dayDifference;k++)
						{
							pstmtInsertSenderDailyBalances.clearParameters();
							m=0;
							pstmtInsertSenderDailyBalances.setDate(++m,BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyBalanceUpdatedOn,k)));
							pstmtInsertSenderDailyBalances.setString(++m,rs.getString("user_id"));
							pstmtInsertSenderDailyBalances.setString(++m,rs.getString("network_code"));

							pstmtInsertSenderDailyBalances.setString(++m,rs.getString("network_code_for"));
							pstmtInsertSenderDailyBalances.setString(++m,rs.getString("product_code"));
							pstmtInsertSenderDailyBalances.setLong(++m,rs.getLong("balance"));
							pstmtInsertSenderDailyBalances.setLong(++m,rs.getLong("prev_balance"));
							//pstmtInsertSenderDailyBalances.setString(++m,PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
							pstmtInsertSenderDailyBalances.setString(++m,c2cBatchItemVO.getTransferType());
							
							pstmtInsertSenderDailyBalances.setString(++m,channelTransferVO.getTransferID());
							pstmtInsertSenderDailyBalances.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
							pstmtInsertSenderDailyBalances.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
							pstmtInsertSenderDailyBalances.setString(++m,PretupsI.DAILY_BALANCE_CREATION_TYPE_MAN);
							updateCount=pstmtInsertSenderDailyBalances.executeUpdate();
							
							if (updateCount <= 0)
							{
								p_con.rollback();
			       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : DB Error while inserting user daily balances table","Approval level = "+p_currentLevel+", updateCount ="+updateCount);
								updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
								terminateProcessing=true;
								break;
							}
						}//end of for loop
						if(terminateProcessing)
						{
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : Terminting the procssing of this user as error while updation daily balance","Approval level = "+p_currentLevel);
							updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
							continue;
						}
						//Update the user balances table
						pstmtUpdateSenderBalanceOn.clearParameters();
						m=0;
						pstmtUpdateSenderBalanceOn.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
						pstmtUpdateSenderBalanceOn.setString(++m,p_senderVO.getUserID());
						updateCount=pstmtUpdateSenderBalanceOn.executeUpdate();
		                //(record not updated properly) if this condition is true then made entry in logs and leave this data.
						if (updateCount <= 0)
						{
							p_con.rollback();
		       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : DB Error while updating user balances table for daily balance","Approval level = "+p_currentLevel+", updateCount="+updateCount);
							updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
							continue;
						}
					}
				}//end of if condition
				try{if (rs != null){rs.close();}} catch (Exception e){}
				maxBalance=0;
				isNotToExecuteQuery = false;
				
				
				
				
				//select the record form the userBalances table.
				pstmtSelectUserBalances.clearParameters();
				m=0;
				pstmtSelectUserBalances.setString(++m,receiverChannelUserVO.getUserID());
				//pstmtSelectUserBalances.setDate(++m,BTSLUtil.getSQLDateFromUtilDate(date));
				pstmtSelectUserBalances.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
				rs=null;
				rs=pstmtSelectUserBalances.executeQuery();
				while(rs.next())
				{
					dailyBalanceUpdatedOn=rs.getDate("daily_balance_updated_on");
					//if record exist check updated on date with current date
					//day differences to maintain the record of previous days.
					dayDifference=BTSLUtil.getDifferenceInUtilDates(dailyBalanceUpdatedOn,date);
					if(dayDifference>0)
					{
						//if dates are not equal get the day differencts and execute insert qurery no of times of the 
						if(_log.isDebugEnabled())
							_log.debug("closeOrdersByBatch ","Till now daily Stock is not updated on "+date+", day differences = "+dayDifference);
						
						for(k=0;k<dayDifference;k++)
						{
							pstmtInsertUserDailyBalances.clearParameters();
							m=0;
							pstmtInsertUserDailyBalances.setDate(++m,BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyBalanceUpdatedOn,k)));
							pstmtInsertUserDailyBalances.setString(++m,rs.getString("user_id"));
							pstmtInsertUserDailyBalances.setString(++m,rs.getString("network_code"));

							pstmtInsertUserDailyBalances.setString(++m,rs.getString("network_code_for"));
							pstmtInsertUserDailyBalances.setString(++m,rs.getString("product_code"));
							pstmtInsertUserDailyBalances.setLong(++m,rs.getLong("balance"));
							pstmtInsertUserDailyBalances.setLong(++m,rs.getLong("prev_balance"));
							//pstmtInsertUserDailyBalances.setString(++m,PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
							pstmtInsertUserDailyBalances.setString(++m,c2cBatchItemVO.getTransferType());
							pstmtInsertUserDailyBalances.setString(++m,channelTransferVO.getTransferID());
							pstmtInsertUserDailyBalances.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
							pstmtInsertUserDailyBalances.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
							pstmtInsertUserDailyBalances.setString(++m,PretupsI.DAILY_BALANCE_CREATION_TYPE_MAN);
							updateCount=pstmtInsertUserDailyBalances.executeUpdate();
							
							if (updateCount <= 0)
							{
								p_con.rollback();
			       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : DB Error while inserting user daily balances table","Approval level = "+p_currentLevel+", updateCount ="+updateCount);
								terminateProcessing=true;
								updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
								break;
							}							
						}//end of for loop
						if(terminateProcessing)
						{
							p_con.rollback();
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : Terminting the procssing of this user as error while updation daily balance","Approval level = "+p_currentLevel);
							continue;
						}
						//Update the user balances table
						pstmtUpdateUserBalances.clearParameters();
						m=0;
						pstmtUpdateUserBalances.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
						pstmtUpdateUserBalances.setString(++m,receiverChannelUserVO.getUserID());
						updateCount=pstmtUpdateUserBalances.executeUpdate();
		                //(record not updated properly) if this condition is true then made entry in logs and leave this data.
						if (updateCount <= 0)
						{
							p_con.rollback();
		       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : DB Error while updating user balances table for daily balance","Approval level = "+p_currentLevel+", updateCount="+updateCount);
							updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
							continue;
						}
					}
				}//end of if condition
				//till now user daily balances is updated. for both sender  and receiver users.
				try{if (rs != null){rs.close();}} catch (Exception e){}
				maxBalance=0;
				isNotToExecuteQuery = false;
				// sender balance to be debited
				//now processing the sender balances.
				pstmtSelectBalance.clearParameters();
				m=0;
				pstmtSelectBalance.setString(++m,p_senderVO.getUserID());
				pstmtSelectBalance.setString(++m,p_batchMasterVO.getProductCode());
				pstmtSelectBalance.setString(++m,p_batchMasterVO.getNetworkCode());
				pstmtSelectBalance.setString(++m,p_batchMasterVO.getNetworkCodeFor());
                rs=null;
                rs = pstmtSelectBalance.executeQuery();
                senderBalance = -1;
                previousSenderBalToBeSetChnlTrfItems=-1;
                if(rs.next())
                {
                    senderBalance = rs.getLong("balance");
                    try{if (rs != null){rs.close();}} catch (Exception e){}
                }
                else
                {
                	p_con.rollback();
        		 	errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchc2capprove.closeOrderByBatch.sendernobal"));
     				errorList.add(errorVO);
     				BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : DB Error while selecting user balances table for daily balance","Approval level = "+p_currentLevel);
     				updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
     				continue;
                }
                if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(p_batchMasterVO.getTransferType())&& PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(p_batchMasterVO.getTransferSubType()))
                	{
                		previousSenderBalToBeSetChnlTrfItems=senderBalance;
                		senderBalance += c2cBatchItemVO.getRequestedQuantity();
                	}
                else if((PretupsI.CHANNEL_TRANSFER_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferType()))&& PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferSubType()))
              		{
                		if(senderBalance==0 ||  senderBalance - c2cBatchItemVO.getRequestedQuantity() < 0 )
                		{
                			p_con.rollback();
                			errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchc2capprove.closeOrderByBatch.senderbalzero"));
                			errorList.add(errorVO);
                			BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : DB Error while selecting user balances table for daily balance","Approval level = "+p_currentLevel);
                			updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
                			continue;
                		}
                		else if(senderBalance != 0 && ( senderBalance - c2cBatchItemVO.getRequestedQuantity() >= 0 ))
                		{
                			previousSenderBalToBeSetChnlTrfItems=senderBalance;
                			senderBalance -= c2cBatchItemVO.getRequestedQuantity();}
                		else 
                			previousSenderBalToBeSetChnlTrfItems=0;
              		}
                	m = 0;
                    //update   sender balance
                    if(senderBalance > -1)
                    {
                    	pstmtUpdateSenderBalance.clearParameters();
                    	handlerStmt = pstmtUpdateSenderBalance;
                    }
                    handlerStmt.setLong(++m,previousSenderBalToBeSetChnlTrfItems);
                    handlerStmt.setLong(++m,senderBalance);
                    //handlerStmt.setString(++m,PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                    handlerStmt.setString(++m,c2cBatchItemVO.getTransferType());
                    handlerStmt.setString(++m,c2cTransferID);
                    handlerStmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(date));
                    handlerStmt.setString(++m,p_senderVO.getUserID());
                    handlerStmt.setString(++m,p_batchMasterVO.getProductCode());
                    handlerStmt.setString(++m,p_batchMasterVO.getNetworkCode());
                    handlerStmt.setString(++m,p_batchMasterVO.getNetworkCodeFor());
                    updateCount = handlerStmt.executeUpdate();
                    handlerStmt.clearParameters();
	                if(updateCount <= 0 )
	                {
	                	p_con.rollback();
	       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : DB error while credit uer balance","Approval level = "+p_currentLevel);
						updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
						continue;
	                }
                   
	              //for zero balance counter..
	                try
	                {
	                    
	                    m=0;
	                    boolean isUserThresholdEntryReq=false;
	                    String thresholdType=null;
	                   /* if(previousSenderBalToBeSetChnlTrfItems>=thresholdValue && senderBalance <=thresholdValue)
	                    {
	                        isUserThresholdEntryReq=true;
	                        thresholdType=PretupsI.BELOW_THRESHOLD_TYPE;
	                    }
	                    else if(previousSenderBalToBeSetChnlTrfItems<=thresholdValue && senderBalance >=thresholdValue)
	                    {
	                        isUserThresholdEntryReq=true;
                            thresholdType=PretupsI.ABOVE_THRESHOLD_TYPE;
	                    }*/
	                    //added by nilesh 
		               
		                transferProfileProductVO =TransferProfileProductCache.getTransferProfileDetails(p_senderVO.getTransferProfileID(),p_batchMasterVO.getProductCode());		               
	                    String remark=null;
	                    String threshold_type=null;
	                    if(senderBalance<=transferProfileProductVO.getAltBalanceLong() && senderBalance>=transferProfileProductVO.getMinResidualBalanceAsLong())
		                {
	                    	//isUserThresholdEntryReq=true;
		                	thresholdValue=transferProfileProductVO.getAltBalanceLong();
		                	threshold_type=PretupsI.THRESHOLD_TYPE_ALERT;
		                }
	                    else if(senderBalance<transferProfileProductVO.getMinResidualBalanceAsLong())
	                    {
	                    	//isUserThresholdEntryReq=true;
		                	thresholdValue=transferProfileProductVO.getMinResidualBalanceAsLong();
		                	threshold_type=PretupsI.THRESHOLD_TYPE_MIN;
	                    }
	                    //new 
	                    if(previousSenderBalToBeSetChnlTrfItems>thresholdValue && senderBalance <thresholdValue)
	                    {
	                        isUserThresholdEntryReq=true;
	                        thresholdType=PretupsI.BELOW_THRESHOLD_TYPE;
	                    }
	                    else if(previousSenderBalToBeSetChnlTrfItems<=thresholdValue && senderBalance >=thresholdValue)
	                    {
	                        isUserThresholdEntryReq=true;
                            thresholdType=PretupsI.ABOVE_THRESHOLD_TYPE;
	                    }
						/*
	                    else if(previousSenderBalToBeSetChnlTrfItems<=thresholdValue && senderBalance <=thresholdValue)
	                    {
	                    	isUserThresholdEntryReq=true;
	                        thresholdType=PretupsI.BELOW_THRESHOLD_TYPE;
	                    } */
	                    //end
	                    if(isUserThresholdEntryReq)
	                    {
	                        if (_log.isDebugEnabled())
	                        {
	                            _log.debug("closeOrdersByBatch", "Entry in threshold counter" + thresholdValue+ ", prvbal: "+previousSenderBalToBeSetChnlTrfItems+ "nbal"+ senderBalance);
	                        }
	                        psmtInsertUserThreshold.clearParameters();
	                        m=0;
	                        psmtInsertUserThreshold.setString(++m, p_senderVO.getUserID());
	                        psmtInsertUserThreshold.setString(++m, c2cTransferID);
	                        psmtInsertUserThreshold.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(date));
	                        psmtInsertUserThreshold.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(date));
	                        psmtInsertUserThreshold.setString(++m, p_batchMasterVO.getNetworkCode());
	                        psmtInsertUserThreshold.setString(++m, p_batchMasterVO.getProductCode());
	                        //psmtInsertUserThreshold.setLong(++m, p_userBalancesVO.getUnitValue());
	                        psmtInsertUserThreshold.setString(++m, PretupsI.CHANNEL_TYPE_C2C);
	                        psmtInsertUserThreshold.setString(++m, c2cBatchItemVO.getTransferType());
	                        psmtInsertUserThreshold.setString(++m, thresholdType);
	                        psmtInsertUserThreshold.setString(++m,p_senderVO.getCategoryCode());
	                        psmtInsertUserThreshold.setLong(++m,previousSenderBalToBeSetChnlTrfItems);
	                        psmtInsertUserThreshold.setLong(++m, senderBalance);
	                        psmtInsertUserThreshold.setLong(++m, thresholdValue);
	                        //added by nilesh
	                        psmtInsertUserThreshold.setString(++m, threshold_type);
	                        psmtInsertUserThreshold.setString(++m, remark);
	                        psmtInsertUserThreshold.executeUpdate();
	                    }
	                }
	                catch (SQLException sqle)
	                {
	                    _log.error(methodName, "SQLException " + sqle.getMessage());
	                    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[closeOrderByBatch]",c2cTransferID,"",p_batchMasterVO.getNetworkCode(),"Error while updating user_threshold_counter table SQL Exception:"+sqle.getMessage());
	                }// end of catch
	            //if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferSubType()))
		          //  {    
		            pstmtSelectSenderTransferCounts.clearParameters();
		            m=0;
	                pstmtSelectSenderTransferCounts.setString(++m,p_senderVO.getUserID());	
	                rs=null;
	                rs = pstmtSelectSenderTransferCounts.executeQuery(); 
	//              get the Sender transfer counts
	                senderCountsVO=null;    
	                if (rs.next())
	                {
	                	senderCountsVO = new UserTransferCountsVO();
	                	senderCountsVO.setUserID(p_senderVO.getUserID() );
	                    
	                	senderCountsVO.setDailyInCount( rs.getLong("daily_in_count") );
	                	senderCountsVO.setDailyInValue( rs.getLong("daily_in_value") );
	                	senderCountsVO.setWeeklyInCount( rs.getLong("weekly_in_count") );
	                	senderCountsVO.setWeeklyInValue( rs.getLong("weekly_in_value") );
	                	senderCountsVO.setMonthlyInCount( rs.getLong("monthly_in_count") );
	                	senderCountsVO.setMonthlyInValue( rs.getLong("monthly_in_value") );
		                
	                	senderCountsVO.setDailyOutCount( rs.getLong("daily_out_count") );
	                	senderCountsVO.setDailyOutValue( rs.getLong("daily_out_value") );
	                	senderCountsVO.setWeeklyOutCount( rs.getLong("weekly_out_count") );
	                	senderCountsVO.setWeeklyOutValue( rs.getLong("weekly_out_value") );
	                	senderCountsVO.setMonthlyOutCount( rs.getLong("monthly_out_count") );
	                	senderCountsVO.setMonthlyOutValue( rs.getLong("monthly_out_value") );				
		                
	                	senderCountsVO.setUnctrlDailyInCount( rs.getLong("outside_daily_in_count") );
	                	senderCountsVO.setUnctrlDailyInValue( rs.getLong("outside_daily_in_value") );
	                	senderCountsVO.setUnctrlWeeklyInCount( rs.getLong("outside_weekly_in_count") );
	                	senderCountsVO.setUnctrlWeeklyInValue( rs.getLong("outside_weekly_in_value") );
	                	senderCountsVO.setUnctrlMonthlyInCount( rs.getLong("outside_monthly_in_count") );
	                	senderCountsVO.setUnctrlMonthlyInValue( rs.getLong("outside_monthly_in_value") );
	
	                	senderCountsVO.setUnctrlDailyOutCount( rs.getLong("outside_daily_out_count") );
	                	senderCountsVO.setUnctrlDailyOutValue( rs.getLong("outside_daily_out_value") );
	                	senderCountsVO.setUnctrlWeeklyOutCount( rs.getLong("outside_weekly_out_count") );
	                	senderCountsVO.setUnctrlWeeklyOutValue( rs.getLong("outside_weekly_out_value") );
	                	senderCountsVO.setUnctrlMonthlyOutCount( rs.getLong("outside_monthly_out_count") );
	                	senderCountsVO.setUnctrlMonthlyOutValue( rs.getLong("outside_monthly_out_value") );
						
	                	senderCountsVO.setDailySubscriberOutCount( rs.getLong("daily_subscriber_out_count") );
	                	senderCountsVO.setDailySubscriberOutValue( rs.getLong("daily_subscriber_out_value") );
	                	senderCountsVO.setWeeklySubscriberOutCount( rs.getLong("weekly_subscriber_out_count") );
	                	senderCountsVO.setWeeklySubscriberOutValue( rs.getLong("weekly_subscriber_out_value") );
	                	senderCountsVO.setMonthlySubscriberOutCount( rs.getLong("monthly_subscriber_out_count") );
	                	senderCountsVO.setMonthlySubscriberOutValue( rs.getLong("monthly_subscriber_out_value") );
		               
	                	senderCountsVO.setLastTransferDate(rs.getDate("last_transfer_date") );
						try{if (rs != null){rs.close();}} catch (Exception e){}
	                }
	                flag=true;
	        		if(senderCountsVO == null)
	        		{
	        			flag = false;
	        			senderCountsVO = new UserTransferCountsVO();
	        		}
	        		//If found then check for reset otherwise no need to check it
	        		if(flag)
	        			ChannelTransferBL.checkResetCountersAfterPeriodChange(senderCountsVO,date);
	        		
	        		pstmtSelectSenderProfileOutCounts.clearParameters();
					m=0;
					pstmtSelectSenderProfileOutCounts.setString(++m,c2cBatchItemVO.getTxnProfile());
					pstmtSelectSenderProfileOutCounts.setString(++m,PretupsI.YES);
					pstmtSelectSenderProfileOutCounts.setString(++m,p_batchMasterVO.getNetworkCode());
					pstmtSelectSenderProfileOutCounts.setString(++m,PretupsI.PARENT_PROFILE_ID_CATEGORY);
					pstmtSelectSenderProfileOutCounts.setString(++m,PretupsI.YES);
	    			rs=null;
	    			rs = pstmtSelectSenderProfileOutCounts.executeQuery();
	    			if (rs.next())
	    			{
	    				senderTfrProfileCheckVO = new TransferProfileVO();
	    				senderTfrProfileCheckVO.setProfileId(rs.getString("profile_id"));
	    				senderTfrProfileCheckVO.setDailyOutCount( rs.getLong("daily_transfer_out_count") );
	    				senderTfrProfileCheckVO.setDailyOutValue( rs.getLong("daily_transfer_out_value"));
	    				senderTfrProfileCheckVO.setWeeklyOutCount( rs.getLong("weekly_transfer_out_count") );
	    				senderTfrProfileCheckVO.setWeeklyOutValue( rs.getLong("weekly_transfer_out_value"));
	    				senderTfrProfileCheckVO.setMonthlyOutCount( rs.getLong("monthly_transfer_out_count") );
	    				senderTfrProfileCheckVO.setMonthlyOutValue( rs.getLong("monthly_transfer_out_value"));
	    				try{if (rs != null){rs.close();}} catch (Exception e){}
	    			}
	//    			(profile counts not found) if this condition is true then made entry in logs and leave this data.
	    			else
	    			{
	    				p_con.rollback();
	       		 		errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.transferprofilenotfound"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : Transfer profile not found","Approval level = "+p_currentLevel);
						updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
						continue;
	    			}
	    			if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferSubType()))
	    			{
		    			//(daily in count reach) if this condition is true then made entry in logs and leave this data.
		    	        if(senderTfrProfileCheckVO.getDailyOutCount() <= senderCountsVO.getDailyOutCount())
		    			{
		    				p_con.rollback();
		       		 		errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.dailyincntreach"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : Daily transfer in count reach","Approval level = "+p_currentLevel);
							updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
							continue;
		    			}
		                //(daily in value reach) if this condition is true then made entry in logs and leave this data.
		    			//else if(senderTfrProfileCheckVO.getDailyOutValue() < (senderCountsVO.getDailyOutValue() + c2cBatchItemVO.getRequestedQuantity() )  )
		    	        else if(senderTfrProfileCheckVO.getDailyOutValue() < (senderCountsVO.getDailyOutValue() + c2cBatchItemVO.getTransferMrp())  )
		    			{
		    				p_con.rollback();
		       		 		errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.dailyinvaluereach"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : Daily transfer in value reach","Approval level = "+p_currentLevel);
							updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
							continue;
		    			}
		                //(weekly in count reach) if this condition is true then made entry in logs and leave this data.
		    			else if(senderTfrProfileCheckVO.getWeeklyOutCount() <=  senderCountsVO.getWeeklyOutCount() )
		    			{
		    				p_con.rollback();
		       		 		errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.weeklyincntreach"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : Weekly transfer in count reach","Approval level = "+p_currentLevel);
							updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
							continue;
		    			}
		                //(weekly in value reach) if this condition is true then made entry in logs and leave this data.
		    			//else if(senderTfrProfileCheckVO.getWeeklyOutValue() < ( senderCountsVO.getWeeklyOutValue() + c2cBatchItemVO.getRequestedQuantity() )  )
		    			else if(senderTfrProfileCheckVO.getWeeklyOutValue() < ( senderCountsVO.getWeeklyOutValue() + c2cBatchItemVO.getTransferMrp() )  )
		    			{
		    				p_con.rollback();
		       		 		errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.weeklyinvaluereach"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : Weekly transfer in value reach","Approval level = "+p_currentLevel);
							updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
							continue;
		    			}
		                //(monthly in count reach) if this condition is true then made entry in logs and leave this data.
		    			else if(senderTfrProfileCheckVO.getMonthlyOutCount() <=  senderCountsVO.getMonthlyOutCount()  )
		    			{
		    				p_con.rollback();
		       		 		errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.monthlyincntreach"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : Monthly transfer in count reach","Approval level = "+p_currentLevel);
							updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
							continue;
		    			}
		                //(monthly in value reach) if this condition is true then made entry in logs and leave this data.
		    			//else if(senderTfrProfileCheckVO.getMonthlyOutValue() < ( senderCountsVO.getMonthlyOutValue() + c2cBatchItemVO.getRequestedQuantity() ) )
		    			else if(senderTfrProfileCheckVO.getMonthlyOutValue() < ( senderCountsVO.getMonthlyOutValue() + c2cBatchItemVO.getTransferMrp() ) )
		    	        {
		    				p_con.rollback();
		       		 		errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.monthlyinvaluereach"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : Monthly transfer in value reach","Approval level = "+p_currentLevel);
							updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
							continue;
		    			}
					}    
	    	        senderCountsVO.setUserID(p_senderVO.getUserID());
	    	        if(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(c2cBatchItemVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(c2cBatchItemVO.getTransferSubType()))
		            {
	    	        	senderCountsVO.setDailyOutValue(senderCountsVO.getDailyOutValue()-c2cBatchItemVO.getTransferMrp());
		    	        senderCountsVO.setWeeklyOutValue(senderCountsVO.getWeeklyOutValue()-c2cBatchItemVO.getTransferMrp());
		    	        senderCountsVO.setMonthlyOutValue(senderCountsVO.getMonthlyOutValue()-c2cBatchItemVO.getTransferMrp());
		            }
	    	        else
	    	        {
	    	        	senderCountsVO.setDailyOutCount(senderCountsVO.getDailyOutCount()+1);
		    	        senderCountsVO.setWeeklyOutCount(senderCountsVO.getWeeklyOutCount()+1);
		    	        senderCountsVO.setMonthlyOutCount(senderCountsVO.getMonthlyOutCount()+1);
		    	        senderCountsVO.setDailyOutValue(senderCountsVO.getDailyOutValue()+c2cBatchItemVO.getTransferMrp());
		    	        senderCountsVO.setWeeklyOutValue(senderCountsVO.getWeeklyOutValue()+c2cBatchItemVO.getTransferMrp());
		    	        senderCountsVO.setMonthlyOutValue(senderCountsVO.getMonthlyOutValue()+c2cBatchItemVO.getTransferMrp());
	    	        }
	    	        senderCountsVO.setLastOutTime(date);
	    	        senderCountsVO.setLastTransferID(c2cTransferID);
	    	        senderCountsVO.setLastTransferDate(date);
		            
	//    	      Update counts if found in db
	        		
	    	        if(flag)
	        		{
	 			        m = 0 ;
	 					pstmtUpdateSenderTransferCounts.clearParameters();
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getDailyInCount());
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getDailyInValue());
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getWeeklyInCount());
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getWeeklyInValue());
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getMonthlyInCount());
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getMonthlyInValue());
	
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getDailyOutCount());
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getDailyOutValue());
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getWeeklyOutCount());
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getWeeklyOutValue());
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getMonthlyOutCount());
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getMonthlyOutValue());
						
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlDailyInCount());
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlDailyInValue());
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlWeeklyInCount());
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlWeeklyInValue());
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlMonthlyInCount());
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlMonthlyInValue());
	
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlDailyOutCount());
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlDailyOutValue());
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlWeeklyOutCount());
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlWeeklyOutValue());
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlMonthlyOutCount());
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlMonthlyOutValue());
						
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getDailySubscriberOutCount());
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getDailySubscriberOutValue());
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getWeeklySubscriberOutCount());
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getWeeklySubscriberOutValue());
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getMonthlySubscriberOutCount());
	 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getMonthlySubscriberOutValue());
						
	 					pstmtUpdateSenderTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(senderCountsVO.getLastOutTime()));
	 					pstmtUpdateSenderTransferCounts.setString(++m,senderCountsVO.getLastTransferID());
	 					pstmtUpdateSenderTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(senderCountsVO.getLastTransferDate()));
	 					pstmtUpdateSenderTransferCounts.setString(++m,senderCountsVO.getUserID());
	        	        updateCount = pstmtUpdateSenderTransferCounts.executeUpdate();
	        		}
	        		//Insert counts if not found in db
	        		else
	        		{
	        			m = 0 ;
	 					pstmtInsertSenderTransferCounts.clearParameters();
	 					pstmtInsertSenderTransferCounts.setLong(++m,senderCountsVO.getDailyOutCount());
	 					pstmtInsertSenderTransferCounts.setLong(++m,senderCountsVO.getDailyOutValue());
	 					pstmtInsertSenderTransferCounts.setLong(++m,senderCountsVO.getWeeklyOutCount());
	 					pstmtInsertSenderTransferCounts.setLong(++m,senderCountsVO.getWeeklyOutValue());
	 					pstmtInsertSenderTransferCounts.setLong(++m,senderCountsVO.getMonthlyOutCount());
	 					pstmtInsertSenderTransferCounts.setLong(++m,senderCountsVO.getMonthlyOutValue());
	 					pstmtInsertSenderTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(senderCountsVO.getLastOutTime()));
	 					pstmtInsertSenderTransferCounts.setString(++m,senderCountsVO.getLastTransferID());
	 					pstmtInsertSenderTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(senderCountsVO.getLastTransferDate()));
	 					pstmtInsertSenderTransferCounts.setString(++m,senderCountsVO.getUserID());
	        	        updateCount = pstmtInsertSenderTransferCounts.executeUpdate();
	        		}
	        		if(updateCount <= 0  )
	    			{
	                	p_con.rollback();
	       		 		errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
						errorList.add(errorVO);
						if(flag)
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : DB error while insert sender trasnfer counts","Approval level = "+p_currentLevel);
						else
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : DB error while uptdate sender trasnfer counts","Approval level = "+p_currentLevel);
						updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
						continue;
	   		 		} 
	        	//}
	            
	            //till now sender user have been validated.
	           
	            //creating channel transfers and respective tranfers items vo
	            //receiver user will be updated the the proper amount creation
	            //added by vikram
	            channelTransferItemVO =new ChannelTransferItemsVO();
	            m=0;
	            rs=null;
	            pstmtSelectCProfileProd.clearParameters();
	            pstmtSelectCProfileProd.setString(++m,p_batchMasterVO.getProductCode());
				pstmtSelectCProfileProd.setString(++m,c2cBatchItemVO.getCommissionProfileSetId());
				pstmtSelectCProfileProd.setString(++m,c2cBatchItemVO.getCommissionProfileVer());
				rs=pstmtSelectCProfileProd.executeQuery();
	            if(rs.next())
				{
	            	channelTransferItemVO.setDiscountType(rs.getString("discount_type"));
	            	channelTransferItemVO.setDiscountRate(rs.getDouble("discount_rate"));
	            	if(PretupsI.YES.equals(rs.getString("taxes_on_channel_transfer")))
					{					
	            		channelTransferItemVO.setTaxOnChannelTransfer(PretupsI.YES);
					}
					else
						channelTransferItemVO.setTaxOnChannelTransfer(PretupsI.NO);
				}
	    		
	            channelTransferVO.setCanceledOn(c2cBatchItemVO.getCancelledOn());
	            channelTransferVO.setCanceledBy(c2cBatchItemVO.getCancelledBy());
	            channelTransferVO.setChannelRemarks(c2cBatchItemVO.getInitiatorRemarks());
	            channelTransferVO.setCommProfileSetId(c2cBatchItemVO.getCommissionProfileSetId());
	            channelTransferVO.setCommProfileVersion(c2cBatchItemVO.getCommissionProfileVer());
	            channelTransferVO.setCreatedBy(c2cBatchItemVO.getInitiatedBy());
	            channelTransferVO.setCreatedOn(c2cBatchItemVO.getInitiatedOn());
	            channelTransferVO.setDomainCode(p_batchMasterVO.getDomainCode());
	            
	            channelTransferVO.setFinalApprovedBy(c2cBatchItemVO.getApprovedBy());
	            channelTransferVO.setFirstApprovedOn(c2cBatchItemVO.getApprovedOn());
	            channelTransferVO.setFirstApproverLimit(0);
	            channelTransferVO.setFirstApprovalRemark(c2cBatchItemVO.getApproverRemarks());
	            channelTransferVO.setSecondApprovalLimit(0);
	            //channelTransferVO.setCategoryCode(p_senderVO.getCategoryCode());
	            channelTransferVO.setBatchNum(c2cBatchItemVO.getBatchId());
	            channelTransferVO.setOptBatchNum(c2cBatchItemVO.getOptBatchId());
	            channelTransferVO.setBatchDate(p_batchMasterVO.getBatchDate());

	    		channelTransferVO.setPayInstrumentAmt(0);
	    		channelTransferVO.setModifiedBy(p_batchMasterVO.getModifiedBy());
	    		channelTransferVO.setModifiedOn(date);
	    		channelTransferVO.setProductType(p_batchMasterVO.getProductType());

	    		channelTransferVO.setReferenceNum(c2cBatchItemVO.getBatchDetailId());
	    		channelTransferVO.setDefaultLang(p_sms_default_lang);
	    		channelTransferVO.setSecondLang(p_sms_second_lang);	    		
				// for balance logger
				channelTransferVO.setReferenceID(network_id);
				//ends here
				if(messageGatewayVO!=null && messageGatewayVO.getRequestGatewayVO()!=null)
				{
					channelTransferVO.setRequestGatewayCode(messageGatewayVO.getRequestGatewayVO().getGatewayCode());
					channelTransferVO.setRequestGatewayType(messageGatewayVO.getGatewayType());
				}
				channelTransferVO.setRequestedQuantity(c2cBatchItemVO.getRequestedQuantity());
				channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);
				channelTransferVO.setStatus(c2cBatchItemVO.getStatus());
	            if(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(c2cBatchItemVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(c2cBatchItemVO.getTransferSubType()))
	            {
	            	channelTransferVO.setToUserID(p_batchMasterVO.getUserId());
	            	channelTransferVO.setFromUserID(receiverChannelUserVO.getUserID());
	            	channelTransferVO.setFromUserCode(c2cBatchItemVO.getMsisdn());
	            	channelTransferVO.setToUserCode(p_senderVO.getMsisdn());
	            	channelTransferVO.setSenderGradeCode(c2cBatchItemVO.getGradeCode());
	            	channelTransferVO.setCategoryCode(c2cBatchItemVO.getCategoryCode());
	            	channelTransferVO.setSenderTxnProfile(c2cBatchItemVO.getTxnProfile());
	            	channelTransferVO.setReceiverCategoryCode(p_senderVO.getCategoryCode());
		    		channelTransferVO.setReceiverGradeCode(p_senderVO.getUserGrade());
		    		channelTransferVO.setReceiverTxnProfile(p_senderVO.getTransferProfileID());
		    		channelTransferVO.setGraphicalDomainCode(receiverChannelUserVO.getGeographicalCode());
		    		channelTransferVO.setReceiverGgraphicalDomainCode(p_senderVO.getGeographicalCode());

	            }
	            else
	            {	//FOR the transfer/return
	            	channelTransferVO.setToUserID(receiverChannelUserVO.getUserID());
	            	channelTransferVO.setFromUserID(p_batchMasterVO.getUserId());
	            	channelTransferVO.setFromUserCode(p_senderVO.getMsisdn());
	            	channelTransferVO.setToUserCode(c2cBatchItemVO.getMsisdn());
	            	channelTransferVO.setSenderGradeCode(p_senderVO.getUserGrade());
	            	channelTransferVO.setCategoryCode(p_senderVO.getCategoryCode());
	            	channelTransferVO.setSenderTxnProfile(p_senderVO.getTransferProfileID());
	            	channelTransferVO.setReceiverCategoryCode(c2cBatchItemVO.getCategoryCode());
		    		channelTransferVO.setReceiverGradeCode(c2cBatchItemVO.getGradeCode());
		    		channelTransferVO.setReceiverTxnProfile(c2cBatchItemVO.getTxnProfile());
		    		channelTransferVO.setGraphicalDomainCode(p_senderVO.getGeographicalCode());
		    		channelTransferVO.setReceiverGgraphicalDomainCode(receiverChannelUserVO.getGeographicalCode());
//	            	channelTransferItemVO.setSenderPreviousStock(senderPreviousBal);
//	            	channelTransferItemVO.setAfterTransSenderPreviousStock(senderPreviousBal);
//	            	channelTransferItemVO.setReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
//	            	channelTransferItemVO.setAfterTransReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
	            }
	            channelTransferVO.setTotalTax1(c2cBatchItemVO.getTax1Value());
	            channelTransferVO.setTotalTax2(c2cBatchItemVO.getTax2Value());
	            channelTransferVO.setTotalTax3(c2cBatchItemVO.getTax3Value());
	            channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_TRANSFER);
	            channelTransferVO.setTransferDate(c2cBatchItemVO.getInitiatedOn());
	            //channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
	            channelTransferVO.setTransferID(c2cTransferID);
	            //channelTransferVO.setTransferInitatedBy(c2cBatchItemVO.getInitiatedBy());
	            channelTransferVO.setTransferInitatedBy(p_batchMasterVO.getUserId());
	            //channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
	            channelTransferVO.setType(PretupsI.CHANNEL_TYPE_C2C);
	            channelTransferVO.setTransferMRP(c2cBatchItemVO.getTransferMrp());
	            
	            //added by vikram
	            //for setting user geo and other imp. things.
	            //channelTransferVO.setFromUserID(p_senderVO.getUserID());
//	            channelTransferVO.setSenderTxnProfile(p_senderVO.getTransferProfileID());
	            //channelTransferVO.setGraphicalDomainCode(channelUserVO.getGeographicalCode());
	            channelTransferVO.setTransferSubType(c2cBatchItemVO.getTransferSubType());
	            channelTransferVO.setTransferType(c2cBatchItemVO.getTransferType());
	            channelTransferVO.setReceiverDomainCode(receiverChannelUserVO.getGeographicalCode());
	            channelTransferItemVO.setRequestedQuantity(PretupsBL.getDisplayAmount(channelTransferVO.getRequestedQuantity()));
	            
	            channelTransferItemVO.setApprovedQuantity(c2cBatchItemVO.getRequestedQuantity());
	            channelTransferItemVO.setCommProfileDetailID(c2cBatchItemVO.getCommissionProfileDetailId());
	            channelTransferItemVO.setCommRate(c2cBatchItemVO.getCommissionRate());
	            channelTransferItemVO.setCommType(c2cBatchItemVO.getCommissionType());
	            channelTransferItemVO.setCommValue(c2cBatchItemVO.getCommissionValue());
	            channelTransferItemVO.setNetPayableAmount(0);
	            channelTransferItemVO.setPayableAmount(0);
	            channelTransferItemVO.setProductTotalMRP(c2cBatchItemVO.getTransferMrp());
	            channelTransferItemVO.setProductCode(p_batchMasterVO.getProductCode());
	            //channelTransferItemVO.setReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
				
	            channelTransferItemVO.setRequiredQuantity(c2cBatchItemVO.getRequestedQuantity());
	            channelTransferItemVO.setSerialNum(1);
	            channelTransferItemVO.setTax1Rate(c2cBatchItemVO.getTax1Rate());
	            channelTransferItemVO.setTax1Type(c2cBatchItemVO.getTax1Type());
	            channelTransferItemVO.setTax1Value(c2cBatchItemVO.getTax1Value());
	            channelTransferItemVO.setTax2Rate(c2cBatchItemVO.getTax2Rate());
	            channelTransferItemVO.setTax2Type(c2cBatchItemVO.getTax2Type());
	            channelTransferItemVO.setTax2Value(c2cBatchItemVO.getTax2Value());
	            channelTransferItemVO.setTax3Rate(c2cBatchItemVO.getTax3Rate());
	            channelTransferItemVO.setTax3Type(c2cBatchItemVO.getTax3Type());
	            channelTransferItemVO.setTax3Value(c2cBatchItemVO.getTax3Value());
	            channelTransferItemVO.setTransferID(c2cTransferID);
	            channelTransferItemVO.setUnitValue(p_batchMasterVO.getProductMrp());
	            // for the balance logger
				//channelTransferItemVO.setAfterTransReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
				
	            //ends here
				channelTransferItemVOList=new ArrayList();
	            channelTransferItemVOList.add(channelTransferItemVO);
	            channelTransferItemVO.setShortName(p_batchMasterVO.getProductShortName());
	            channelTransferVO.setChannelTransferitemsVOList(channelTransferItemVOList);
	            if(((Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL))).booleanValue()){
                	if(_log.isDebugEnabled()){
						_log.debug(methodName,"batchItemsVO.getMsisdn()="+c2cBatchItemVO.getMsisdn()+", batchItemsVO.getLoginID()="+c2cBatchItemVO.getLoginID()+", batchItemsVO.getExternalCode()="+c2cBatchItemVO.getExternalCode());
					}
					if(!BTSLUtil.isNullString(c2cBatchItemVO.getMsisdn()))
                    	channelTransferVO.setToUserMsisdn(c2cBatchItemVO.getMsisdn());
                    else if(!BTSLUtil.isNullString(c2cBatchItemVO.getLoginID()))
                    	channelTransferVO.setToUserMsisdn(((ChannelUserVO)new ChannelUserDAO().loadChannelUserDetailsByLoginIDANDORMSISDN(p_con,"",c2cBatchItemVO.getLoginID())).getMsisdn());
                    else if(!BTSLUtil.isNullString(c2cBatchItemVO.getExternalCode()))
                    	channelTransferVO.setToUserMsisdn(((ChannelUserVO)new ChannelUserDAO().loadChnlUserDetailsByExtCode(p_con, c2cBatchItemVO.getExternalCode())).getMsisdn());
					if(messageGatewayVO!=null && messageGatewayVO.getRequestGatewayVO()!=null)
     				{
     					channelTransferVO.setRequestGatewayCode(messageGatewayVO.getRequestGatewayVO().getGatewayCode());
     					channelTransferVO.setRequestGatewayType(messageGatewayVO.getGatewayType());
     				}
				}
	            ChannelTransferBL.calculateMRPWithTaxAndDiscount(channelTransferVO,PretupsI.TRANSFER_TYPE_C2C);
	            
	            if (_log.isDebugEnabled()) _log.debug(methodName, "Exiting: channelTransferVO=" + channelTransferVO.toString());
	            if (_log.isDebugEnabled()) _log.debug(methodName, "Exiting: channelTransferItemVO=" + channelTransferItemVO.toString());
            	
	            //validate the user here..
	            
	            //now validating user. 
                pstmtSelectBalance.clearParameters();
				m=0;
                pstmtSelectBalance.setString(++m,receiverChannelUserVO.getUserID());
                pstmtSelectBalance.setString(++m,p_batchMasterVO.getProductCode());
                pstmtSelectBalance.setString(++m,p_batchMasterVO.getNetworkCode());
                pstmtSelectBalance.setString(++m,p_batchMasterVO.getNetworkCodeFor());
                rs=null;
                rs = pstmtSelectBalance.executeQuery();
                balance = -1;
                previousUserBalToBeSetChnlTrfItems=-1;
                if(rs.next())
                {
                    balance = rs.getLong("balance");
                    try{if (rs != null){rs.close();}} catch (Exception e){}
                    if (_log.isDebugEnabled())  _log.debug(methodName, "receiverChannelUserVO.getUserID()="+receiverChannelUserVO.getUserID()+" , balance=" +balance);
                }
                
                
	           if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(p_batchMasterVO.getTransferType())&& PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(p_batchMasterVO.getTransferSubType()))
	               {
	        	   	if(balance==0 || (balance - c2cBatchItemVO.getRequestedQuantity() < 0))
		        	   	{
		                  p_con.rollback();
		       		 	  errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchc2capprove.closeOrderByBatch.receiverbalnsuff"));
		    			  errorList.add(errorVO);
		    			  BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : DB Error while selecting user balances table for daily balance","Approval level = "+p_currentLevel);
		    			  updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
		    			  continue;
		                }
	          	    else if(balance != 0 && balance - c2cBatchItemVO.getRequestedQuantity() >= 0)
	          	       {
	          	    	  previousUserBalToBeSetChnlTrfItems=balance;
	          	    	  balance -= c2cBatchItemVO.getRequestedQuantity();
	          	       }
	          	    else 
	          	    	  previousUserBalToBeSetChnlTrfItems=0;
	             }
	           else if((PretupsI.CHANNEL_TRANSFER_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferType()))&& PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferSubType()))
	                {
	        	   		if (_log.isDebugEnabled())  _log.debug(methodName, "CHANNEL_TRANSFER_TYPE_TRANSFER, previousUserBalToBeSetChnlTrfItems=" +previousUserBalToBeSetChnlTrfItems+", balance="+balance);
	                	previousUserBalToBeSetChnlTrfItems=balance;
	                    //balance += c2cBatchItemVO.getRequestedQuantity();
	                	balance += channelTransferItemVO.getReceiverCreditQty();
	                }
	           		if (_log.isDebugEnabled())  _log.debug(methodName, "CHANNEL_TRANSFER_TYPE_TRANSFER, previousUserBalToBeSetChnlTrfItems=" +previousUserBalToBeSetChnlTrfItems+", channelTransferItemVO.getReceiverCreditQty()="+channelTransferItemVO.getReceiverCreditQty()+", balance="+balance);
	           if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferSubType()))
	           { 
					pstmtLoadTransferProfileProduct.clearParameters();
					m=0;
	                pstmtLoadTransferProfileProduct.setString(++m,c2cBatchItemVO.getTxnProfile());
	                pstmtLoadTransferProfileProduct.setString(++m,p_batchMasterVO.getProductCode());
	                pstmtLoadTransferProfileProduct.setString(++m,PretupsI.PARENT_PROFILE_ID_CATEGORY);
	                pstmtLoadTransferProfileProduct.setString(++m,PretupsI.YES);
	    			rs=null;
	                rs = pstmtLoadTransferProfileProduct.executeQuery();
	                //get the transfer profile of user
	    			if(rs.next())
	    			{
	    			    transferProfileProductVO = new TransferProfileProductVO();
	    			    transferProfileProductVO.setProductCode(p_batchMasterVO.getProductCode());
	    			    transferProfileProductVO.setMinResidualBalanceAsLong(rs.getLong("min_residual_balance"));
	    			    transferProfileProductVO.setMaxBalanceAsLong(rs.getLong("max_balance"));
	    			    try{if (rs != null){rs.close();}} catch (Exception e){}
	    			}
	                //(transfer profile not found) if this condition is true then made entry in logs and leave this data.
	    			else
	    			{
	    				p_con.rollback();
	       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.profcountersnotfound"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : User Trf Profile not found for product","Approval level = "+p_currentLevel);
						updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
						continue;
	    			}
	                maxBalance=transferProfileProductVO.getMaxBalanceAsLong();
	                //(max balance reach for the receiver) if this condition is true then made entry in logs and leave this data.
					if(maxBalance< balance )
	                {
	                    if(!isNotToExecuteQuery)
	                        isNotToExecuteQuery = true;
	                    p_con.rollback();
	       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.maxbalancereach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : User Max balance reached","Approval level = "+p_currentLevel);
						updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
						continue;
	                }
					//check for the very first txn of the user containg the order value larger than maxBalance
	                //(max balance reach) if this condition is true then made entry in logs and leave this data.
					else if(balance==-1 && maxBalance<c2cBatchItemVO.getRequestedQuantity())
					 {
	                    if(!isNotToExecuteQuery)
	                        isNotToExecuteQuery = true;
	                    p_con.rollback();
	       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.maxbalancereach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : User Max balance reached","Approval level = "+p_currentLevel);
						updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
						continue;
					  }
	               }
	                if(!isNotToExecuteQuery)
	                {
	                	if (_log.isDebugEnabled())  _log.debug(methodName, "previousUserBalToBeSetChnlTrfItems=" +previousUserBalToBeSetChnlTrfItems);
	                    m = 0;
	                    //update
	                    if(previousUserBalToBeSetChnlTrfItems > -1)
	                    {
	                    	pstmtUpdateBalance.clearParameters();
	                    	handlerStmt = pstmtUpdateBalance;
	                    	handlerStmt.setLong(++m,previousUserBalToBeSetChnlTrfItems);
	                    }
						else
	                    {
							// insert
							pstmtInsertBalance.clearParameters();
	                        handlerStmt = pstmtInsertBalance;
	                        balance = c2cBatchItemVO.getRequestedQuantity();
	                        handlerStmt.setLong(++m,0);//previous balance
	                        previousUserBalToBeSetChnlTrfItems=0;
							handlerStmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(date));//updated on date
	                    }
	                    handlerStmt.setLong(++m,balance);
	                    //handlerStmt.setString(++m,PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
	                    handlerStmt.setString(++m,c2cBatchItemVO.getTransferType());
	                    handlerStmt.setString(++m,c2cTransferID);
	                    handlerStmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(date));
	                    handlerStmt.setString(++m,receiverChannelUserVO.getUserID());
	                    handlerStmt.setString(++m,p_batchMasterVO.getProductCode());
	                    handlerStmt.setString(++m,p_batchMasterVO.getNetworkCode());
	                    handlerStmt.setString(++m,p_batchMasterVO.getNetworkCodeFor());
	                    updateCount = handlerStmt.executeUpdate();
	                    handlerStmt.clearParameters();
		                if(updateCount <= 0 )
		                {
		                	p_con.rollback();
		       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : DB error while credit uer balance","Approval level = "+p_currentLevel);
							updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
							continue;
		                }
	                }
	                //if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferSubType())){
	              //for zero balance counter..
                    try
                    {
    	              
                        m=0;
                        boolean isUserThresholdEntryReq=false;
                        String thresholdType=null;
                       /* if(previousUserBalToBeSetChnlTrfItems>=thresholdValue && balance <=thresholdValue)
                        {
                            isUserThresholdEntryReq=true;
                            thresholdType=PretupsI.BELOW_THRESHOLD_TYPE;
                        }
                        else if(previousUserBalToBeSetChnlTrfItems<=thresholdValue && balance >=thresholdValue)
                        {
                            isUserThresholdEntryReq=true;
                            thresholdType=PretupsI.ABOVE_THRESHOLD_TYPE;
                        }*/
                        //added by nilesh
                        String threshold_type=null;
                        String remark = null;
                        if(balance<=transferProfileProductVO.getAltBalanceLong() && balance>transferProfileProductVO.getMinResidualBalanceAsLong())
		                {
                        	//isUserThresholdEntryReq=true;
		                	thresholdValue=transferProfileProductVO.getAltBalanceLong();
		                	threshold_type=PretupsI.THRESHOLD_TYPE_ALERT;
		                }
                        else if(balance<=transferProfileProductVO.getMinResidualBalanceAsLong())
                        {
                        	//isUserThresholdEntryReq=true;
		                	thresholdValue=transferProfileProductVO.getMinResidualBalanceAsLong();
		                	threshold_type=PretupsI.THRESHOLD_TYPE_MIN;
                        }
                        //new
                        if(previousUserBalToBeSetChnlTrfItems>=thresholdValue && balance <=thresholdValue)
                        {
                            isUserThresholdEntryReq=true;
                            thresholdType=PretupsI.BELOW_THRESHOLD_TYPE;
                        }
                        else if(previousUserBalToBeSetChnlTrfItems<=thresholdValue && balance >=thresholdValue)
                        {
                            isUserThresholdEntryReq=true;
                            thresholdType=PretupsI.ABOVE_THRESHOLD_TYPE;
                        }
                        else if(previousUserBalToBeSetChnlTrfItems<=thresholdValue && balance <=thresholdValue)
                        {
                            isUserThresholdEntryReq=true;
                            thresholdType=PretupsI.BELOW_THRESHOLD_TYPE;
                        }
                        //end
                        
                        if(isUserThresholdEntryReq)
                        {
                            if (_log.isDebugEnabled())
                            {
                                _log.debug(methodName, "Entry in threshold counter" + thresholdValue+ ", prvbal: "+previousUserBalToBeSetChnlTrfItems+ "nbal"+ balance);
                            }
                            psmtInsertUserThreshold.clearParameters();
                            m=0;
                            psmtInsertUserThreshold.setString(++m, receiverChannelUserVO.getUserID());
                            psmtInsertUserThreshold.setString(++m, c2cTransferID);
                            psmtInsertUserThreshold.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(date));
                            psmtInsertUserThreshold.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(date));
                            psmtInsertUserThreshold.setString(++m, p_batchMasterVO.getNetworkCode());
                            psmtInsertUserThreshold.setString(++m, p_batchMasterVO.getProductCode());
                            //psmtInsertUserThreshold.setLong(++m, p_userBalancesVO.getUnitValue());
                            psmtInsertUserThreshold.setString(++m, PretupsI.CHANNEL_TYPE_C2C);
                            psmtInsertUserThreshold.setString(++m, c2cBatchItemVO.getTransferType());
                            psmtInsertUserThreshold.setString(++m, thresholdType);
                            psmtInsertUserThreshold.setString(++m,c2cBatchItemVO.getCategoryCode());
                            psmtInsertUserThreshold.setLong(++m,previousUserBalToBeSetChnlTrfItems);
                            psmtInsertUserThreshold.setLong(++m, balance);
                            psmtInsertUserThreshold.setLong(++m, thresholdValue);
                            //added by nilesh
                            psmtInsertUserThreshold.setString(++m, threshold_type);
                            psmtInsertUserThreshold.setString(++m, remark);
                            
                            psmtInsertUserThreshold.executeUpdate();
                        }
                    }
                    catch (SQLException sqle)
                    {
                        _log.error(methodName, "SQLException " + sqle.getMessage());
                        EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[closeOrderByBatch]",c2cTransferID,"",p_batchMasterVO.getNetworkCode(),"Error while updating user_threshold_counter table SQL Exception:"+sqle.getMessage());
                    }// end of catch
	                pstmtSelectTransferCounts.clearParameters();
					m=0;
	                pstmtSelectTransferCounts.setString(++m,receiverChannelUserVO.getUserID());	
	                rs=null;
	                rs = pstmtSelectTransferCounts.executeQuery();
	                //get the user transfer counts
	                countsVO=null;
	                if (rs.next())
	                {
	                    countsVO = new UserTransferCountsVO();
	                    countsVO.setUserID( c2cBatchItemVO.getUserId() );
	                    
	                    countsVO.setDailyInCount( rs.getLong("daily_in_count") );
	                    countsVO.setDailyInValue( rs.getLong("daily_in_value") );
	                    countsVO.setWeeklyInCount( rs.getLong("weekly_in_count") );
	                    countsVO.setWeeklyInValue( rs.getLong("weekly_in_value") );
	                    countsVO.setMonthlyInCount( rs.getLong("monthly_in_count") );
	                    countsVO.setMonthlyInValue( rs.getLong("monthly_in_value") );
		                
						countsVO.setDailyOutCount( rs.getLong("daily_out_count") );
		                countsVO.setDailyOutValue( rs.getLong("daily_out_value") );
		                countsVO.setWeeklyOutCount( rs.getLong("weekly_out_count") );
		                countsVO.setWeeklyOutValue( rs.getLong("weekly_out_value") );
		                countsVO.setMonthlyOutCount( rs.getLong("monthly_out_count") );
		                countsVO.setMonthlyOutValue( rs.getLong("monthly_out_value") );				
		                
						countsVO.setUnctrlDailyInCount( rs.getLong("outside_daily_in_count") );
		                countsVO.setUnctrlDailyInValue( rs.getLong("outside_daily_in_value") );
		                countsVO.setUnctrlWeeklyInCount( rs.getLong("outside_weekly_in_count") );
		                countsVO.setUnctrlWeeklyInValue( rs.getLong("outside_weekly_in_value") );
		                countsVO.setUnctrlMonthlyInCount( rs.getLong("outside_monthly_in_count") );
		                countsVO.setUnctrlMonthlyInValue( rs.getLong("outside_monthly_in_value") );
	
						countsVO.setUnctrlDailyOutCount( rs.getLong("outside_daily_out_count") );
		                countsVO.setUnctrlDailyOutValue( rs.getLong("outside_daily_out_value") );
		                countsVO.setUnctrlWeeklyOutCount( rs.getLong("outside_weekly_out_count") );
		                countsVO.setUnctrlWeeklyOutValue( rs.getLong("outside_weekly_out_value") );
		                countsVO.setUnctrlMonthlyOutCount( rs.getLong("outside_monthly_out_count") );
		                countsVO.setUnctrlMonthlyOutValue( rs.getLong("outside_monthly_out_value") );
						
						countsVO.setDailySubscriberOutCount( rs.getLong("daily_subscriber_out_count") );
		                countsVO.setDailySubscriberOutValue( rs.getLong("daily_subscriber_out_value") );
		                countsVO.setWeeklySubscriberOutCount( rs.getLong("weekly_subscriber_out_count") );
		                countsVO.setWeeklySubscriberOutValue( rs.getLong("weekly_subscriber_out_value") );
		                countsVO.setMonthlySubscriberOutCount( rs.getLong("monthly_subscriber_out_count") );
		                countsVO.setMonthlySubscriberOutValue( rs.getLong("monthly_subscriber_out_value") );
		               
						countsVO.setLastTransferDate(rs.getDate("last_transfer_date") );
						try{if (rs != null){rs.close();}} catch (Exception e){}
	                }
	                flag=true;
	        		if(countsVO == null)
	        		{
	        			flag = false;
	        			countsVO = new UserTransferCountsVO();
	        		}
	        		//If found then check for reset otherwise no need to check it
	        		if(flag)
	        			ChannelTransferBL.checkResetCountersAfterPeriodChange(countsVO,date);
	        		
					pstmtSelectProfileCounts.clearParameters();
					m=0;
	    			pstmtSelectProfileCounts.setString(++m,c2cBatchItemVO.getTxnProfile());
	    			pstmtSelectProfileCounts.setString(++m,PretupsI.YES);
	    			pstmtSelectProfileCounts.setString(++m,p_batchMasterVO.getNetworkCode());
	    			pstmtSelectProfileCounts.setString(++m,PretupsI.PARENT_PROFILE_ID_CATEGORY);
	    			pstmtSelectProfileCounts.setString(++m,PretupsI.YES);
	    			rs=null;
	    			rs = pstmtSelectProfileCounts.executeQuery();
	     			//get the transfer profile counts
	    			if (rs.next())
	    			{
	    				transferProfileVO = new TransferProfileVO();
	    				transferProfileVO.setProfileId(rs.getString("profile_id"));
	    				transferProfileVO.setDailyInCount( rs.getLong("daily_transfer_in_count") );
	    				transferProfileVO.setDailyInValue( rs.getLong("daily_transfer_in_value"));
	    				transferProfileVO.setWeeklyInCount( rs.getLong("weekly_transfer_in_count") );
	    				transferProfileVO.setWeeklyInValue( rs.getLong("weekly_transfer_in_value"));
	    				transferProfileVO.setMonthlyInCount( rs.getLong("monthly_transfer_in_count") );
	    				transferProfileVO.setMonthlyInValue( rs.getLong("monthly_transfer_in_value"));
	    				try{if (rs != null){rs.close();}} catch (Exception e){}
	    			}
	                //(profile counts not found) if this condition is true then made entry in logs and leave this data.
	    			else
	    			{
	    				p_con.rollback();
	       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.transferprofilenotfound"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : Transfer profile not found","Approval level = "+p_currentLevel);
						updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
						continue;
	    			}
	    			if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferSubType()))
	    			{
	    				//(daily in count reach) if this condition is true then made entry in logs and leave this data.
		    	        if(transferProfileVO.getDailyInCount() <= countsVO.getDailyInCount())
		    			{
		    				p_con.rollback();
		       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.dailyincntreach"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : Daily transfer in count reach","Approval level = "+p_currentLevel);
							updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
							continue;
		    			}
		                //(daily in value reach) if this condition is true then made entry in logs and leave this data.
		    			//else if(transferProfileVO.getDailyInValue() < (countsVO.getDailyInValue() + c2cBatchItemVO.getRequestedQuantity() )  )
		    	        else if(transferProfileVO.getDailyInValue() < (countsVO.getDailyInValue() + channelTransferVO.getTransferMRP())  )
		    	        {
		    				p_con.rollback();
		       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.dailyinvaluereach"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : Daily transfer in value reach","Approval level = "+p_currentLevel);
							updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
							continue;
		    			}
		                //(weekly in count reach) if this condition is true then made entry in logs and leave this data.
		    			else if(transferProfileVO.getWeeklyInCount() <=  countsVO.getWeeklyInCount() )
		    			{
		    				p_con.rollback();
		       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.weeklyincntreach"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : Weekly transfer in count reach","Approval level = "+p_currentLevel);
							updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
							continue;
		    			}
		                //(weekly in value reach) if this condition is true then made entry in logs and leave this data.
		    			//else if(transferProfileVO.getWeeklyInValue() < ( countsVO.getWeeklyInValue() + c2cBatchItemVO.getRequestedQuantity() )  )
		    			else if(transferProfileVO.getWeeklyInValue() < ( countsVO.getWeeklyInValue() + channelTransferVO.getTransferMRP() )  )
		    			{
		    				p_con.rollback();
		       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.weeklyinvaluereach"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : Weekly transfer in value reach","Approval level = "+p_currentLevel);
							updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
							continue;
		    			}
		                //(monthly in count reach) if this condition is true then made entry in logs and leave this data.
		    			else if(transferProfileVO.getMonthlyInCount() <=  countsVO.getMonthlyInCount()  )
		    			{
		    				p_con.rollback();
		       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.monthlyincntreach"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : Monthly transfer in count reach","Approval level = "+p_currentLevel);
							updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
							continue;
		    			}
		                //(mobthly in value reach) if this condition is true then made entry in logs and leave this data.
		    			//else if(transferProfileVO.getMonthlyInValue() < ( countsVO.getMonthlyInValue() + c2cBatchItemVO.getRequestedQuantity() ) )
		    			else if(transferProfileVO.getMonthlyInValue() < ( countsVO.getMonthlyInValue() + channelTransferVO.getTransferMRP() ) )
		    			{
		    				p_con.rollback();
		       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.monthlyinvaluereach"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : Monthly transfer in value reach","Approval level = "+p_currentLevel);
							updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
							continue;
		    			}
					}
	    			countsVO.setUserID(receiverChannelUserVO.getUserID());
	    			if(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(c2cBatchItemVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(c2cBatchItemVO.getTransferSubType()))
		            {
		                countsVO.setDailyInValue(countsVO.getDailyInValue()-channelTransferVO.getTransferMRP());
		                countsVO.setWeeklyInValue(countsVO.getWeeklyInValue()-channelTransferVO.getTransferMRP());
		                countsVO.setMonthlyInValue(countsVO.getMonthlyInValue()-channelTransferVO.getTransferMRP());
		            }
	    			else
	    			{
	    				countsVO.setDailyInCount(countsVO.getDailyInCount()+1);
		                countsVO.setWeeklyInCount(countsVO.getWeeklyInCount()+1);
		                countsVO.setMonthlyInCount(countsVO.getMonthlyInCount()+1);
		                countsVO.setDailyInValue(countsVO.getDailyInValue()+channelTransferVO.getTransferMRP());
		                countsVO.setWeeklyInValue(countsVO.getWeeklyInValue()+channelTransferVO.getTransferMRP());
		                countsVO.setMonthlyInValue(countsVO.getMonthlyInValue()+channelTransferVO.getTransferMRP());
	    			}
	                countsVO.setLastInTime(date);
	        		countsVO.setLastTransferID(c2cTransferID);
	        		countsVO.setLastTransferDate(date);
	        		//Update counts if found in db
	        		
	        		if(flag)
	        		{
	 			        m = 0 ;
	 					pstmtUpdateTransferCounts.clearParameters();
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getDailyInCount());
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getDailyInValue());
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getWeeklyInCount());
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getWeeklyInValue());
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getMonthlyInCount());
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getMonthlyInValue());
	
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getDailyOutCount());
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getDailyOutValue());
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getWeeklyOutCount());
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getWeeklyOutValue());
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getMonthlyOutCount());
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getMonthlyOutValue());
						
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlDailyInCount());
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlDailyInValue());
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlWeeklyInCount());
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlWeeklyInValue());
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlMonthlyInCount());
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlMonthlyInValue());
	
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlDailyOutCount());
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlDailyOutValue());
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlWeeklyOutCount());
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlWeeklyOutValue());
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlMonthlyOutCount());
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlMonthlyOutValue());
						
						pstmtUpdateTransferCounts.setLong(++m,countsVO.getDailySubscriberOutCount());
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getDailySubscriberOutValue());
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getWeeklySubscriberOutCount());
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getWeeklySubscriberOutValue());
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getMonthlySubscriberOutCount());
	        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getMonthlySubscriberOutValue());
						
						pstmtUpdateTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(countsVO.getLastInTime()));
	        	        pstmtUpdateTransferCounts.setString(++m,countsVO.getLastTransferID());
	        	        pstmtUpdateTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(countsVO.getLastTransferDate()));
	        	        pstmtUpdateTransferCounts.setString(++m,countsVO.getUserID());
	        	        updateCount = pstmtUpdateTransferCounts.executeUpdate();
	        		}
	        		//Insert counts if not found in db
	        		else
	        		{
	        			m = 0 ;
	 					pstmtInsertTransferCounts.clearParameters();
	         	        pstmtInsertTransferCounts.setLong(++m,countsVO.getDailyInCount());
	        	        pstmtInsertTransferCounts.setLong(++m,countsVO.getDailyInValue());
	        	        pstmtInsertTransferCounts.setLong(++m,countsVO.getWeeklyInCount());
	        	        pstmtInsertTransferCounts.setLong(++m,countsVO.getWeeklyInValue());
	        	        pstmtInsertTransferCounts.setLong(++m,countsVO.getMonthlyInCount());
	        	        pstmtInsertTransferCounts.setLong(++m,countsVO.getMonthlyInValue());
	        	        pstmtInsertTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(countsVO.getLastInTime()));
	        	        pstmtInsertTransferCounts.setString(++m,countsVO.getLastTransferID());
	        	        pstmtInsertTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(countsVO.getLastTransferDate()));
	        	        pstmtInsertTransferCounts.setString(++m,countsVO.getUserID());
	        	        updateCount = pstmtInsertTransferCounts.executeUpdate();
	        		}
	                //(record not updated properly) if this condition is true then made entry in logs and leave this data.
	                if(updateCount <= 0  )
	    			{
	                	p_con.rollback();
	       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
						errorList.add(errorVO);
						if(flag)
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : DB error while insert user trasnfer counts","Approval level = "+p_currentLevel);
						else
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : DB error while uptdate user trasnfer counts","Approval level = "+p_currentLevel);
						updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
						continue;
	   		 		}
	        	//	}
					pstmtIsModified.clearParameters();
					m=0;
					pstmtIsModified.setString(++m,c2cBatchItemVO.getBatchDetailId());
					pstmtIsModified.setString(++m,c2cBatchItemVO.getBatchId());
					pstmtIsModified.setString(++m,c2cBatchItemVO.getOptBatchId());
					rs=null;
					rs=pstmtIsModified.executeQuery();
					java.sql.Timestamp newlastModified = null;
					//check record is modified or not
					if (rs.next())
		            {
		                newlastModified = rs.getTimestamp("modified_on");
		                try{if (rs != null){rs.close();}} catch (Exception e){}
		            }
	                //(record not found means record modified) if this condition is true then made entry in logs and leave this data.
					else
					{
						p_con.rollback();
	       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordmodified"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : Record is already modified","Approval level = "+p_currentLevel);
						updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
						continue;
					}
	                //if this condition is true then made entry in logs and leave this data.
					if(newlastModified.getTime()!=BTSLUtil.getTimestampFromUtilDate(c2cBatchItemVO.getModifiedOn()).getTime())
					{
						p_con.rollback();
	       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordmodified"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : Record is already modified","Approval level = "+p_currentLevel);
						updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
						continue;
					}
	                
					//If  apperoval then set parameters in psmtApprC2CBatchItem
					if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE.equals(p_currentLevel))
					{
						psmtApprC2CBatchItem.clearParameters();
						//c2cBatchItemVO.setApprovedBy(p_senderVO.getUserID());
						//c2cBatchItemVO.setApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
						m=0;
						psmtApprC2CBatchItem.setString(++m, c2cTransferID);
						psmtApprC2CBatchItem.setString(++m, c2cBatchItemVO.getModifiedBy());
						psmtApprC2CBatchItem.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
						psmtApprC2CBatchItem.setString(++m, c2cBatchItemVO.getApproverRemarks());
						psmtApprC2CBatchItem.setString(++m, c2cBatchItemVO.getApprovedBy());
						psmtApprC2CBatchItem.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
						psmtApprC2CBatchItem.setString(++m, c2cBatchItemVO.getStatus());
						psmtApprC2CBatchItem.setString(++m, c2cBatchItemVO.getBatchDetailId());
						psmtApprC2CBatchItem.setString(++m, c2cBatchItemVO.getBatchId());
						psmtApprC2CBatchItem.setString(++m, c2cBatchItemVO.getOptBatchId());
						psmtApprC2CBatchItem.setString(++m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
						psmtApprC2CBatchItem.setString(++m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE);
			            updateCount=psmtApprC2CBatchItem.executeUpdate();
					}
					
	                //(record not updated properly) if this condition is true then made entry in logs and leave this data.
		            if(updateCount<=0)
					{
		            	p_con.rollback();
	       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : DB Error while updating items table","Approval level = "+p_currentLevel+", updateCount="+updateCount);
						updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
						continue;
					}

	            
	            
	            //for positive commission deduct from network stock
	            final boolean debit=true;
	    		if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.POSITIVE_COMM_APPLY))).booleanValue() && PretupsI.CHANNEL_TRANSFER_TYPE_TRANSFER.equals(channelTransferVO.getTransferType()))
	    		{
	    			ChannelTransferBL.prepareNetworkStockListAndCreditDebitStockForCommision(p_con,channelTransferVO,channelTransferVO.getFromUserID(),date, debit);
	    			ChannelTransferBL.updateNetworkStockTransactionDetailsForCommision(p_con,channelTransferVO,channelTransferVO.getFromUserID(),date);
	    		}
	            
	    		
	    		//added by vikram
	    		if(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(c2cBatchItemVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(c2cBatchItemVO.getTransferSubType()))
	            {
	            	channelTransferItemVO.setSenderPreviousStock(previousUserBalToBeSetChnlTrfItems);
	            	channelTransferItemVO.setAfterTransSenderPreviousStock(previousUserBalToBeSetChnlTrfItems);
	            	channelTransferItemVO.setReceiverPreviousStock(senderPreviousBal);
	            	channelTransferItemVO.setAfterTransReceiverPreviousStock(senderPreviousBal);
	            }
	            else
	            {	//FOR the transfer/return
	            	channelTransferItemVO.setSenderPreviousStock(senderPreviousBal);
	            	channelTransferItemVO.setAfterTransSenderPreviousStock(senderPreviousBal);
	            	channelTransferItemVO.setReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
	            	channelTransferItemVO.setAfterTransReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
	            }
	    		channelTransferVO.setControlTransfer(PretupsI.YES);
	    		channelTransferVO.setActiveUserId(p_batchMasterVO.getCreatedBy());
	    		channelTransferVO.setReceiverDomainCode(channelTransferVO.getDomainCode());
	    		channelTransferVO.setNetPayableAmount(channelTransferItemVO.getNetPayableAmount());
	    		channelTransferVO.setPayableAmount(channelTransferItemVO.getPayableAmount());
	            m = 0;
				pstmtInsertIntoChannelTranfers.clearParameters();
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getCanceledBy());
            	pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getCanceledOn()));
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getChannelRemarks());
            	pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getCommProfileSetId());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getCommProfileVersion());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getCreatedBy());
            	pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getCreatedOn()));
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getDomainCode());
            	pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getExternalTxnDate()));
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getExternalTxnNum());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getFinalApprovedBy());
            	pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getFirstApprovedOn()));
            	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getFirstApproverLimit());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getFirstApprovalRemark());
            	pstmtInsertIntoChannelTranfers.setDate(++m,BTSLUtil.getSQLDateFromUtilDate(channelTransferVO.getBatchDate()));
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getBatchNum());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getOptBatchNum());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getFromUserID());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getGraphicalDomainCode());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getModifiedBy());
            	pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getModifiedOn()));
            	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferItemVO.getNetPayableAmount());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getNetworkCode());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getNetworkCodeFor());
            	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferItemVO.getPayableAmount());
            	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getPayInstrumentAmt());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getProductType());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getReceiverCategoryCode());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getReceiverGradeCode());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getReceiverTxnProfile());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getReferenceNum());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getRequestGatewayCode());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getRequestGatewayType());
            	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getRequestedQuantity());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getSecondApprovedBy());
            	pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getSecondApprovedOn()));
            	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getSecondApprovalLimit());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getSecondApprovalRemark());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getSource());
            	pstmtInsertIntoChannelTranfers.setString(++m,c2cBatchItemVO.getStatus());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getThirdApprovedBy());
            	pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getThirdApprovedOn()));
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getThirdApprovalRemark());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getToUserID());
            	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getTotalTax1());
            	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getTotalTax2());
            	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getTotalTax3());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getTransferCategory());
            	pstmtInsertIntoChannelTranfers.setDate(++m,BTSLUtil.getSQLDateFromUtilDate(channelTransferVO.getTransferDate()));
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getTransferID());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getTransferInitatedBy());
            	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getTransferMRP());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getTransferSubType());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getTransferType());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getType());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getCategoryCode());
            	
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getSenderGradeCode());
            	pstmtInsertIntoChannelTranfers.setString(++m, channelTransferVO.getSenderTxnProfile());
				pstmtInsertIntoChannelTranfers.setString(++m,PretupsI.YES);
				
				pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getFromUserCode());
				pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getToUserCode());
				pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getDomainCode());
					
				// to geographical domain also inserted as the geogrpahical domain that will help in reports
				pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getReceiverGgraphicalDomainCode());
				pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getDefaultLang());
            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getSecondLang());
            	pstmtInsertIntoChannelTranfers.setString(++m,p_batchMasterVO.getCreatedBy());
            	
				//ends here
            	//insert into channel transfer table
            	updateCount=pstmtInsertIntoChannelTranfers.executeUpdate();
                //(record not updated properly) if this condition is true then made entry in logs and leave this data.
            	if(updateCount<=0)
            	{
            		p_con.rollback();
       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : DB Error while inserting in channel transfer table","Approval level = "+p_currentLevel+", updateCount="+updateCount);
					updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
					continue;
            	}
				m=0;
				pstmtInsertIntoChannelTransferItems.clearParameters();
            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getApprovedQuantity());
            	pstmtInsertIntoChannelTransferItems.setString(++m,channelTransferItemVO.getCommProfileDetailID());
            	pstmtInsertIntoChannelTransferItems.setDouble(++m,channelTransferItemVO.getCommRate());
            	pstmtInsertIntoChannelTransferItems.setString(++m,channelTransferItemVO.getCommType());
            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getCommValue());
            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getProductTotalMRP());
            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getNetPayableAmount());
            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getPayableAmount());
            	pstmtInsertIntoChannelTransferItems.setString(++m,channelTransferItemVO.getProductCode());
            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getReceiverPreviousStock());
            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getRequiredQuantity());
            	pstmtInsertIntoChannelTransferItems.setInt(++m,channelTransferItemVO.getSerialNum());
            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getSenderPreviousStock());
            	//pstmtInsertIntoChannelTransferItems.setLong(++m,senderPreviousBal);
            	
            	pstmtInsertIntoChannelTransferItems.setDouble(++m,channelTransferItemVO.getTax1Rate());
            	pstmtInsertIntoChannelTransferItems.setString(++m,channelTransferItemVO.getTax1Type());
            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getTax1Value());
            	pstmtInsertIntoChannelTransferItems.setDouble(++m,channelTransferItemVO.getTax2Rate());
            	pstmtInsertIntoChannelTransferItems.setString(++m,channelTransferItemVO.getTax2Type());
            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getTax2Value());
            	pstmtInsertIntoChannelTransferItems.setDouble(++m,channelTransferItemVO.getTax3Rate());
            	pstmtInsertIntoChannelTransferItems.setString(++m,channelTransferItemVO.getTax3Type());
            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getTax3Value());
            	pstmtInsertIntoChannelTransferItems.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
            	pstmtInsertIntoChannelTransferItems.setString(++m,c2cTransferID);
            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getUnitValue());
            	
            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getSenderDebitQty());
            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getReceiverCreditQty());
            	//added by vikram
	    		if(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(c2cBatchItemVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(c2cBatchItemVO.getTransferSubType()))
	            {
	    			pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getSenderPreviousStock()-channelTransferItemVO.getSenderDebitQty());
	            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getReceiverPreviousStock()+channelTransferItemVO.getReceiverCreditQty());
	            }
	            else
	            {	//FOR the transfer/return
	            	pstmtInsertIntoChannelTransferItems.setLong(++m,senderPreviousBal-channelTransferItemVO.getSenderDebitQty());
	            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getReceiverPreviousStock()+channelTransferItemVO.getReceiverCreditQty());
	            }
            	
            	//insert into channel transfer items table
            	updateCount=pstmtInsertIntoChannelTransferItems.executeUpdate();
                //(record not updated properly) if this condition is true then made entry in logs and leave this data.
            	if(updateCount<=0)
            	{
            		p_con.rollback();
       		 		errorVO=new ListValueVO(c2cBatchItemVO.getMsisdn(),String.valueOf(c2cBatchItemVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"FAIL : DB Error while inserting in channel transfer items table","Approval level = "+p_currentLevel+", updateCount="+updateCount);
					updateOperatorBatchC2CItemForFailedTransaction(p_con,c2cBatchItemVO,errorVO);
					continue;
            	}
            	//commit the transaction after processing each record
            	p_con.commit();
				BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,c2cBatchItemVO,"PASS : Order is closed successfully","Approval level = "+p_currentLevel+", updateCount="+updateCount);
            	//made entry in network stock and balance logger
            	ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);
				pstmtSelectBalanceInfoForMessage.clearParameters();
				m=0;
            	pstmtSelectBalanceInfoForMessage.setString(++m, receiverChannelUserVO.getUserID());
            	pstmtSelectBalanceInfoForMessage.setString(++m, p_batchMasterVO.getNetworkCode());
            	pstmtSelectBalanceInfoForMessage.setString(++m, p_batchMasterVO.getNetworkCodeFor());
                rs=null;
            	rs = pstmtSelectBalanceInfoForMessage.executeQuery();
                userbalanceList= new ArrayList();
                while (rs.next())
                {
                    balancesVO = new UserBalancesVO();
                    balancesVO.setProductCode(rs.getString("product_code"));
                    balancesVO.setBalance(rs.getLong("balance"));
    				balancesVO.setProductShortCode(rs.getString("product_short_code"));
    				balancesVO.setProductShortName(rs.getString("short_name"));
    				userbalanceList.add(balancesVO);
                }
				try{if (rs != null){rs.close();}} catch (Exception e){}
                //generate the message arguments to be send in SMS
                keyArgumentVO = new KeyArgumentVO();
    			argsArr = new String[2];
    			argsArr[1] = PretupsBL.getDisplayAmount(channelTransferItemVO.getRequiredQuantity());
    			argsArr[0] = String.valueOf(channelTransferItemVO.getShortName());
    			keyArgumentVO.setKey(PretupsErrorCodesI.C2C_CHNL_CHNL_TRANSFER_SMS2);
    			keyArgumentVO.setArguments(argsArr);
    			txnSmsMessageList=new ArrayList();
    			balSmsMessageList=new ArrayList();
    			txnSmsMessageList.add(keyArgumentVO);
    			for(int index=0,n=userbalanceList.size();index<n;index++)
    			{
    				balancesVO=(UserBalancesVO)userbalanceList.get(index);
    				if(balancesVO.getProductCode().equals(channelTransferItemVO.getProductCode()))
    				{
    					argsArr=new String[2];
    					argsArr[1]=balancesVO.getBalanceAsString();
    					argsArr[0]=balancesVO.getProductShortName();
    					keyArgumentVO = new KeyArgumentVO();
    					keyArgumentVO.setKey(PretupsErrorCodesI.C2C_CHNL_CHNL_TRANSFER_SMS_BALSUBKEY);
    					keyArgumentVO.setArguments(argsArr);
    					balSmsMessageList.add(keyArgumentVO);
    					break;
    				}
    			}
    			locale=new Locale(language,country);
    			String c2cNotifyMsg=null;
    			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_SMS_NOTIFY))).booleanValue())
    			{
    				LocaleMasterVO localeVO=LocaleMasterCache.getLocaleDetailsFromlocale(locale);
    				if(PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage()))
    					c2cNotifyMsg=channelTransferVO.getDefaultLang();
    				else
    					c2cNotifyMsg=channelTransferVO.getSecondLang();
    				array=new String[] {channelTransferVO.getTransferID(),BTSLUtil.getMessage(locale,txnSmsMessageList),BTSLUtil.getMessage(locale,balSmsMessageList),c2cNotifyMsg};
    			}  			
    				
    			if(c2cNotifyMsg==null)
    				array=new String[] {channelTransferVO.getTransferID(),BTSLUtil.getMessage(locale,txnSmsMessageList),BTSLUtil.getMessage(locale,balSmsMessageList)};
    			
    			if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()){

             		try{			
             				LoyaltyBL _loyaltyBL= new LoyaltyBL();
             				LoyaltyVO loyaltyVO= new LoyaltyVO();
             				PromotionDetailsVO promotionDetailsVO = new PromotionDetailsVO();
             				LoyaltyDAO _loyaltyDAO= new LoyaltyDAO();
             				ArrayList arr = new ArrayList();
             				loyaltyVO.setModuleType(PretupsI.C2C_MODULE);
             				loyaltyVO.setServiceType(PretupsI.C2C_MODULE);				
             				loyaltyVO.setTransferamt(channelTransferVO.getRequestedQuantity());
             				loyaltyVO.setCategory(channelTransferVO.getCategoryCode()); 					
             				loyaltyVO.setFromuserId(channelTransferVO.getFromUserID());
             				loyaltyVO.setTouserId(channelTransferVO.getToUserID()); 					
             				loyaltyVO.setNetworkCode(channelTransferVO.getNetworkCode());					
             				loyaltyVO.setTxnId(channelTransferVO.getTransferID());
             				loyaltyVO.setCreatedOn(channelTransferVO.getCreatedOn());
             				loyaltyVO.setSenderMsisdn(channelTransferVO.getFromUserCode());
             				loyaltyVO.setReciverMsisdn(channelTransferVO.getToUserCode());
             				loyaltyVO.setProductCode(channelTransferVO.getProductCode());
             				arr.add(loyaltyVO.getFromuserId());
             				arr.add(loyaltyVO.getTouserId());
             				promotionDetailsVO = _loyaltyDAO.loadSetIdByUserId(p_con, arr);
             				loyaltyVO.setSetId(promotionDetailsVO.get_setId());
             				loyaltyVO.setToSetId(promotionDetailsVO.get_toSetId());

             				if(loyaltyVO.getSetId()==null && loyaltyVO.getToSetId()==null)
             				{
             					_log.error("process", "Exception during LMS Module.SetId not found");
             				}
             				else{
             					_loyaltyBL.distributeLoyaltyPoints(PretupsI.C2C_MODULE, channelTransferVO.getTransferID(),loyaltyVO);
             				}
             			
             		}
     				catch(Exception ex){
     					_log.error("process", "Exception durign LMS Module " + ex.getMessage());

     				}
             	
             	
    			}
    			
    			
    			messages=new BTSLMessages(PretupsErrorCodesI.C2C_CHNL_CHNL_TRANSFER_SMS1,array);
                pushMessage=new PushMessage(c2cBatchItemVO.getMsisdn(),messages,channelTransferVO.getTransferID(),null,locale,channelTransferVO.getNetworkCode()); 
                //push SMS
                pushMessage.push();
                OneLineTXNLog.log(channelTransferVO,null);
			}//end of for loop
			}//end of while
		}//end of try
		catch(BTSLBaseException be)
		{
			throw be;
		}
		catch (SQLException sqe)
		{
			sqe.printStackTrace();
			try{if(p_con!=null)p_con.rollback();}catch(Exception e){}
			_log.error(methodName, "SQLException : " + sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[closeOrderByBatch]","","","","SQL Exception:"+sqe.getMessage());
			BatchC2CFileProcessLog.c2cBatchMasterLog(methodName,p_batchMasterVO,"FAIL : SQL Exception:"+sqe.getMessage(),"Approval level = "+p_currentLevel);
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			try{if(p_con!=null)p_con.rollback();}catch(Exception e){}
		    _log.error(methodName, "Exception : " + ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[closeOrderByBatch]","","","","Exception:"+ex.getMessage());
			BatchC2CFileProcessLog.c2cBatchMasterLog(methodName,p_batchMasterVO,"FAIL : Exception:"+ex.getMessage(),"Approval level = "+p_currentLevel);
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		}
		finally
		{
		    try{if (pstmtLoadUser != null){pstmtLoadUser.close();}} catch (Exception e){}
		    try{if (pstmtSelectUserBalances!=null){pstmtSelectUserBalances.close();}} catch (Exception e){}
		    try{if (pstmtUpdateUserBalances!=null){pstmtUpdateUserBalances.close();}} catch (Exception e){}
		    try{if (pstmtInsertUserDailyBalances!=null){pstmtInsertUserDailyBalances.close();}} catch (Exception e){}
		    try{if (pstmtSelectBalance!=null){pstmtSelectBalance.close();}} catch (Exception e){}
		    try{if (pstmtUpdateBalance!=null){pstmtUpdateBalance.close();}} catch (Exception e){}
		    try{if (pstmtInsertBalance!=null){pstmtInsertBalance.close();}} catch (Exception e){}
		    try{if (pstmtSelectTransferCounts!=null){pstmtSelectTransferCounts.close();}} catch (Exception e){}
		    try{if (pstmtSelectProfileCounts!=null){pstmtSelectProfileCounts.close();}} catch (Exception e){}
		    try{if (pstmtUpdateTransferCounts!=null){pstmtUpdateTransferCounts.close();}} catch (Exception e){}
		    try{if (pstmtInsertTransferCounts!=null){pstmtInsertTransferCounts.close();}} catch (Exception e){}
		    try{if (psmtApprC2CBatchItem != null){psmtApprC2CBatchItem.close();}} catch (Exception e){}
		    try{if (pstmtIsModified !=null){pstmtIsModified.close();}} catch (Exception e){}
		    try{if (pstmtLoadTransferProfileProduct !=null){pstmtLoadTransferProfileProduct.close();}} catch (Exception e){}
		    try{if (handlerStmt != null){handlerStmt.close();}} catch (Exception e){}
		    try{if (pstmtInsertIntoChannelTransferItems!= null){pstmtInsertIntoChannelTransferItems.close();}} catch (Exception e){}
			try{if (pstmtInsertIntoChannelTranfers!= null){pstmtInsertIntoChannelTranfers.close();}} catch (Exception e){}
			try{if (pstmtSelectBalanceInfoForMessage!= null){pstmtSelectBalanceInfoForMessage.close();}} catch (Exception e){}
			try{if (pstmtSelectCProfileProd!=null){pstmtSelectCProfileProd.close();}} catch (Exception e){}
			try{if (pstmtUpdateSenderTransferCounts!=null){pstmtUpdateSenderTransferCounts.close();}} catch (Exception e){}
			try{if (psmtInsertUserThreshold!=null){psmtInsertUserThreshold.close();}} catch (Exception e){}
			try
			{
				Iterator batchMasterItemsListMapIter = batchMasterItemsListMap.entrySet().iterator();
				while(batchMasterItemsListMapIter.hasNext()){
					Map.Entry mapElement = (Map.Entry)batchMasterItemsListMapIter.next(); 
	            	p_batchMasterVO = (C2CBatchMasterVO) mapElement.getKey();
	            	p_senderVO = p_batchMasterVO.getChannelUserVO();
	            	String p_userID = p_batchMasterVO.getUserId();
	            	batch_ID = p_batchMasterVO.getBatchId();
	            	String optBatch_Id = p_batchMasterVO.getOptBatchId();
					int m=0;
		            pstmtSelectItemsDetails.setString(++m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
		            pstmtSelectItemsDetails.setString(++m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE);
		            pstmtSelectItemsDetails.setString(++m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
		            pstmtSelectItemsDetails.setString(++m, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
		            pstmtSelectItemsDetails.setString(++m, p_batchMasterVO.getBatchId());
		            pstmtSelectItemsDetails.setString(++m, p_batchMasterVO.getOptBatchId());
		            rs=null;
		            rs=pstmtSelectItemsDetails.executeQuery();
		            //Check the final status to be updated in master after processing all records of batch
		            if(rs.next())
		            {
		            	int totalCount=rs.getInt("batch_total_record");
		            	int closeCount=rs.getInt("close");
		            	int cnclCount=rs.getInt("cncl");
		            	try{if (rs != null){rs.close();}} catch (Exception e){}
		            	String statusOfMaster=null;
		             	if(totalCount==cnclCount && totalCount>0)
		            		statusOfMaster=PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_CANCEL;
		             	else if(totalCount==closeCount+cnclCount  && totalCount>0)
		            		statusOfMaster=PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_CLOSE;
		            	else
		            		statusOfMaster=PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_OPEN;
		             	m=0;
		            	pstmtUpdateMaster.setString(++m,statusOfMaster);
		            	pstmtUpdateMaster.setString(++m,p_senderVO.getUserID());
		            	pstmtUpdateMaster.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
		            	pstmtUpdateMaster.setString(++m,p_sms_default_lang);
		            	pstmtUpdateMaster.setString(++m,p_sms_second_lang);
		            	pstmtUpdateMaster.setString(++m,p_batchMasterVO.getBatchId());
		            	pstmtUpdateMaster.setString(++m,p_batchMasterVO.getOptBatchId());
		            	pstmtUpdateMaster.setString(++m,PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_UNDERPROCESS);
		            	
		            	updateCount=pstmtUpdateMaster.executeUpdate();
		                //(record not updated properly) if this condition is true then made entry in logs and leave this data.
		            	if(updateCount<=0)
		            	{
		            		p_con.rollback();
		       		 		errorVO=new ListValueVO("","",p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.c2cBatchMasterLog(methodName,p_batchMasterVO,"FAIL : DB Error while updating master table","Approval level = "+p_currentLevel+", updateCount="+updateCount);
							EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[closeOrederByBatch]","","","","Error while updating C2C_BATCHES table. Batch id="+batch_ID);
		            	}//end of if
		            }//end of if
				}//end of while
	            p_con.commit();
			}
		    catch (SQLException sqe)
			{
		    	sqe.printStackTrace();
				try{if(p_con!=null)p_con.rollback();}catch(Exception e){}
				_log.error(methodName, "SQLException : " + sqe);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[closeOrderByBatch]","","","","SQL Exception:"+sqe.getMessage());
				BatchC2CFileProcessLog.c2cBatchMasterLog(methodName,p_batchMasterVO,"FAIL : SQL Exception:"+sqe.getMessage(),"Approval level = "+p_currentLevel);
				throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
			}
			catch (Exception ex)
			{
				try{if(p_con!=null)p_con.rollback();}catch(Exception e){}
			    _log.error(methodName, "Exception : " + ex);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[closeOrderByBatch]","","","","Exception:"+ex.getMessage());
				BatchC2CFileProcessLog.c2cBatchMasterLog(methodName,p_batchMasterVO,"FAIL : Exception:"+ex.getMessage(),"Approval level = "+p_currentLevel);
				throw new BTSLBaseException(this, methodName, "error.general.processing");
			}
		    try{if (rs != null){rs.close();}} catch (Exception e){}
		    try{if (pstmtSelectItemsDetails != null){pstmtSelectItemsDetails.close();}} catch (Exception e){}
		    try{if (pstmtUpdateMaster != null){pstmtUpdateMaster.close();}} catch (Exception e){}
		    //OneLineTXNLog.log(channelTransferVO);
			if (_log.isDebugEnabled()) _log.debug(methodName, "Exiting: errorList size=" + errorList.size());
		}
		return errorList;
	}

	/**This method load Batch details according to batch id.
	 *  loadBatchDetailsList
	 * @param p_con Connection
	 * @param p_batchId String
	 * @return ArrayList list
	 * @throws BTSLBaseException
	 * ved.sharma
	 */
	public ArrayList loadBatchDetailsList(Connection p_con,String p_batchId) throws BTSLBaseException
	{
		final String methodName = "loadBatchDetailsList";
		if (_log.isDebugEnabled()) 	_log.debug(methodName, "Entered p_batchId="+p_batchId);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer strBuff = new StringBuffer(" SELECT distinct c2cb.batch_id, c2cb.network_code, c2cb.network_code_for,  ");
		strBuff.append(" c2cb.batch_name, c2cb.status, L.lookup_name status_desc, c2cb.domain_code, c2cb.product_code, c2cb.batch_file_name, "); 
		strBuff.append(" c2cb.batch_total_record, c2cb.batch_date, INTU.user_name initated_by, c2cb.created_on, P.product_name, D.domain_name, "); 
		strBuff.append(" cbi.batch_detail_id, cbi.category_code, cbi.msisdn, cbi.user_id, cbi.status status_item,  cbi.user_grade_code, cbi.reference_no, "); 
		strBuff.append(" cbi.transfer_date, cbi.txn_profile, cbi.commission_profile_set_id,  ");
		strBuff.append(" cbi.commission_profile_ver, cbi.commission_profile_detail_id, cbi.commission_type, cbi.commission_rate, ");
		strBuff.append(" cbi.commission_value, cbi.tax1_type, cbi.tax1_rate, cbi.tax1_value, cbi.tax2_type, cbi.tax2_rate, cbi.tax2_value, "); 
		strBuff.append(" cbi.tax3_type, cbi.tax3_rate, cbi.tax3_value, cbi.requested_quantity, cbi.transfer_mrp, cbi.initiator_remarks, cbi.approver_remarks,"); 
	    strBuff.append(" NVL(FAPP.user_name,CNCL_USR.user_name) approved_by, NVL(cbi.approved_on,cbi.cancelled_on) approved_on,");
		strBuff.append(" CNCL_USR.user_name cancelled_by, cbi.cancelled_on, cbi.rcrd_status, cbi.external_code, ");
		strBuff.append(" U.user_name, C.category_name, CG.grade_name ");
		strBuff.append(" FROM c2c_batches c2cb, products P, domains D, c2c_batch_items cbi, categories C, users U, ");
		strBuff.append(" users INTU, users FAPP, users SAPP, users TAPP, channel_grades CG, lookups L, users CNCL_USR ");
		strBuff.append(" WHERE c2cb.batch_id=?  "); 
		strBuff.append(" AND cbi.batch_id = c2cb.batch_id AND cbi.category_code = C.category_code AND cbi.user_id = U.user_id ");
		strBuff.append(" AND P.product_code = c2cb.product_code AND D.domain_code = c2cb.domain_code "); 
		strBuff.append(" AND c2cb.created_by = INTU.user_id(+) ");
		strBuff.append(" AND cbi.approved_by = FAPP.user_id(+) ");
		strBuff.append(" AND cbi.cancelled_by = CNCL_USR.user_id(+) ");
		strBuff.append(" AND CG.grade_code = cbi.user_grade_code ");
		strBuff.append(" AND L.lookup_type = ? ");
		strBuff.append(" AND L.lookup_code = c2cb.status ");
		strBuff.append(" ORDER BY cbi.batch_detail_id DESC, cbi.category_code, cbi.status ");
		String sqlSelect = strBuff.toString();
		if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		C2CBatchMasterVO c2cBatchMasterVO=null;
		C2CBatchItemsVO c2cBatchItemsVO=null;
		ArrayList list = new ArrayList();
		try
		{
			pstmt = p_con.prepareStatement(sqlSelect);
			pstmt.setString(1, p_batchId);
			pstmt.setString(2, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_LOOKUP_TYPE);
			rs = pstmt.executeQuery();
			while (rs.next())
			{
			    c2cBatchMasterVO=new C2CBatchMasterVO();
			    c2cBatchMasterVO.setBatchId(rs.getString("batch_id"));
			    c2cBatchMasterVO.setBatchName(rs.getString("batch_name"));
			    c2cBatchMasterVO.setStatus(rs.getString("status"));
			    c2cBatchMasterVO.setDomainCode(rs.getString("domain_code"));
			    c2cBatchMasterVO.setDomainCodeDesc(rs.getString("domain_name"));
			    c2cBatchMasterVO.setProductCode(rs.getString("product_code"));
			    c2cBatchMasterVO.setProductCodeDesc(rs.getString("product_name"));
			    c2cBatchMasterVO.setBatchFileName(rs.getString("batch_file_name"));
			    c2cBatchMasterVO.setBatchTotalRecord(rs.getInt("batch_total_record"));
			    c2cBatchMasterVO.setBatchDate(rs.getDate("batch_date"));
			    c2cBatchMasterVO.setCreatedBy(rs.getString("initated_by"));
			    c2cBatchMasterVO.setCreatedOn(rs.getTimestamp("created_on"));
			    c2cBatchMasterVO.setStatus(rs.getString("status"));
			    c2cBatchMasterVO.setStatusDesc(rs.getString("status_desc"));
			    
			    c2cBatchItemsVO = new C2CBatchItemsVO();
			    c2cBatchItemsVO.setBatchDetailId(rs.getString("batch_detail_id"));
			    c2cBatchItemsVO.setUserName(rs.getString("user_name"));
			    c2cBatchItemsVO.setExternalCode(rs.getString("external_code"));
			    c2cBatchItemsVO.setMsisdn(rs.getString("msisdn"));
			    c2cBatchItemsVO.setCategoryName(rs.getString("category_name"));			    
			    c2cBatchItemsVO.setCategoryCode(rs.getString("category_code"));			    
			    c2cBatchItemsVO.setStatus(rs.getString("status_item"));			    
			    c2cBatchItemsVO.setUserGradeCode(rs.getString("user_grade_code"));	
			    c2cBatchItemsVO.setGradeCode(rs.getString("user_grade_code"));
			    c2cBatchItemsVO.setGradeName(rs.getString("grade_name"));
			    c2cBatchItemsVO.setReferenceNo(rs.getString("reference_no"));			    
			    c2cBatchItemsVO.setTransferDate(rs.getDate("transfer_date"));
			    if(c2cBatchItemsVO.getTransferDate()!=null)
			        c2cBatchItemsVO.setTransferDateStr(BTSLUtil.getDateStringFromDate(c2cBatchItemsVO.getTransferDate()));			    
			    c2cBatchItemsVO.setTxnProfile(rs.getString("txn_profile"));			    
			    c2cBatchItemsVO.setCommissionProfileSetId(rs.getString("commission_profile_set_id"));			    
			    c2cBatchItemsVO.setCommissionProfileVer(rs.getString("commission_profile_ver"));			    
			    c2cBatchItemsVO.setCommissionProfileDetailId(rs.getString("commission_profile_detail_id"));			    
			    c2cBatchItemsVO.setCommissionRate(rs.getDouble("commission_rate"));			    
			    c2cBatchItemsVO.setCommissionType(rs.getString("commission_type"));			    
			    c2cBatchItemsVO.setRequestedQuantity(rs.getLong("requested_quantity"));			    
			    c2cBatchItemsVO.setTransferMrp(rs.getLong("transfer_mrp"));			    
			    c2cBatchItemsVO.setInitiatorRemarks(rs.getString("initiator_remarks"));	
			    c2cBatchItemsVO.setApprovedBy(rs.getString("approved_by"));
			    c2cBatchItemsVO.setApprovedOn(rs.getTimestamp("approved_on"));
			    c2cBatchItemsVO.setApproverRemarks(rs.getString("approver_remarks"));
			    
			    c2cBatchMasterVO.setC2cBatchItemsVO(c2cBatchItemsVO);
			    
			    list.add(c2cBatchMasterVO);
			}
		} 
		catch (SQLException sqe)
		{
			_log.error(methodName, "SQLException : " + sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[loadBatchDetailsList]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}
		catch (Exception ex)
		{
		    _log.error(methodName, "Exception : " + ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[loadBatchDetailsList]","","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		}
		finally
		{
		    try{if (rs != null){rs.close();}} catch (Exception e){}
			try{if (pstmt != null){pstmt.close();}} catch (Exception e){}
			if (_log.isDebugEnabled()) _log.debug(methodName, "Exiting: loadBatchDetailsList  list.size()=" + list.size());
		}
		return list;
	}
	/**
	 * Method initiateBatchC2CTransfer
	 * This method used for the batch c2c order initiation. The main purpose of this method is to insert the
	 * records in c2c_batches,foc_batch_geographies & c2c_batch_items table.
	 * @param p_con Connection
	 * @param p_batchMasterVO c2cBatchMasterVO
	 * @param p_batchItemsList ArrayList
	 * @param p_messages MessageResources
	 * @param p_locale Locale
	 * @return errorList ArrayList
	 * @throws BTSLBaseException 
	 */
	
	public ArrayList initiateBatchC2CTransfer(Connection p_con,HashMap<C2CBatchMasterVO, ArrayList<C2CBatchItemsVO>> batchMasterItemsListMap, MessageResources p_messages,Locale p_locale)throws BTSLBaseException
	{
		final String methodName = "initiateBatchC2CTransfer";
		if (_log.isDebugEnabled()) 	_log.debug(methodName, "Entered.... batchMasterItemsListMap="+batchMasterItemsListMap + "p_locale="+ p_locale);
		
		C2CBatchMasterVO p_batchMasterVO = null;
		int totalNumberOfRecordsToProcess = 0;
		ArrayList p_batchItemsList = null;
		HashMap<String, ArrayList> errorListHashMap = new HashMap<String,ArrayList>();
		ArrayList errorList = new ArrayList();
		String errorKey = null;
		ListValueVO errorVO=null;
		
		
		// for loading the C2C transfer rule for C2C transfer
		PreparedStatement pstmtSelectTrfRule = null;
		ResultSet rsSelectTrfRule=null;
		StringBuffer strBuffSelectTrfRule = new StringBuffer(" SELECT transfer_rule_id,transfer_type, transfer_allowed ");
		strBuffSelectTrfRule.append("FROM chnl_transfer_rules WHERE network_code = ? AND domain_code = ?  ");
		strBuffSelectTrfRule.append("AND to_category = ? AND status = 'Y' AND type = 'CHANNEL' ");
		if (_log.isDebugEnabled()) 	_log.debug(methodName, "strBuffSelectTrfRule Query ="+strBuffSelectTrfRule);
		// ends here
		
		// for loading the products associated with the transfer rule
		PreparedStatement pstmtSelectTrfRuleProd = null;
		ResultSet rsSelectTrfRuleProd=null;
		StringBuffer strBuffSelectTrfRuleProd = new StringBuffer("SELECT 1 FROM chnl_transfer_rules_products ");
		strBuffSelectTrfRuleProd.append("WHERE transfer_rule_id=?  AND product_code = ? ");
		if (_log.isDebugEnabled()) 	_log.debug(methodName, "strBuffSelectTrfRuleProd Query ="+strBuffSelectTrfRuleProd);
		//ends here

		// for loading the products associated with the commission profile
		PreparedStatement pstmtSelectCProfileProd = null;
		ResultSet rsSelectCProfileProd=null;
		StringBuffer strBuffSelectCProfileProd = new StringBuffer("SELECT cp.min_transfer_value,cp.max_transfer_value,cp.discount_type,cp.discount_rate, ");
		strBuffSelectCProfileProd.append("cp.comm_profile_products_id, cp.transfer_multiple_off, cp.taxes_on_foc_applicable, cp.taxes_on_channel_transfer ");
		strBuffSelectCProfileProd.append("FROM commission_profile_products cp ");
		strBuffSelectCProfileProd.append("WHERE  cp.product_code = ? AND cp.comm_profile_set_id = ? AND cp.comm_profile_set_version = ? ");
		if (_log.isDebugEnabled()) 	_log.debug(methodName, "strBuffSelectCProfileProd Query ="+strBuffSelectCProfileProd);
		
		PreparedStatement pstmtSelectCProfileProdDetail = null;
		ResultSet rsSelectCProfileProdDetail=null;
		StringBuffer strBuffSelectCProfileProdDetail = new StringBuffer("SELECT cpd.tax1_type,cpd.tax1_rate,cpd.tax2_type,cpd.tax2_rate, ");
		strBuffSelectCProfileProdDetail.append("cpd.tax3_type,cpd.tax3_rate,cpd.commission_type,cpd.commission_rate,cpd.comm_profile_detail_id ");
		strBuffSelectCProfileProdDetail.append("FROM commission_profile_details cpd ");
		strBuffSelectCProfileProdDetail.append("WHERE  cpd.comm_profile_products_id = ? AND cpd.start_range <= ? AND cpd.end_range >= ? ");
		if (_log.isDebugEnabled()) 	_log.debug(methodName, "strBuffSelectCProfileProdDetail Query ="+strBuffSelectCProfileProdDetail);
		//ends here

		// for existance of the product in the transfer profile
		PreparedStatement pstmtSelectTProfileProd = null;
		ResultSet rsSelectTProfileProd=null;
		StringBuffer strBuffSelectTProfileProd = new StringBuffer(" SELECT 1 ");
		strBuffSelectTProfileProd.append("FROM transfer_profile_products tpp,transfer_profile tp, transfer_profile catp,transfer_profile_products catpp ");
        strBuffSelectTProfileProd.append("WHERE tpp.profile_id=? AND tpp.product_code = ? AND tpp.profile_id=tp.profile_id AND catp.profile_id=catpp.profile_id ");
		strBuffSelectTProfileProd.append("AND tpp.product_code=catpp.product_code AND tp.category_code=catp.category_code AND catp.parent_profile_id=? AND catp.status='Y' AND tp.network_code = catp.network_code");
		if (_log.isDebugEnabled()) 	_log.debug(methodName, "strBuffSelectTProfileProd Query ="+strBuffSelectTProfileProd);
		//ends here

		// insert data in the batch master table 
		//commented for DB2 
		//OraclePreparedStatement pstmtInsertBatchMaster = null;
		PreparedStatement pstmtInsertBatchMaster = null;
		StringBuffer strBuffInsertBatchMaster = new StringBuffer("INSERT INTO c2c_batches (batch_id, network_code, ");
		strBuffInsertBatchMaster.append("network_code_for, batch_name, status, domain_code, product_code, ");
		strBuffInsertBatchMaster.append("batch_file_name, batch_total_record, batch_date, created_by, created_on, ");
		strBuffInsertBatchMaster.append(" modified_by, modified_on,sms_default_lang,sms_second_lang,user_id,OPT_BATCH_ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
		if (_log.isDebugEnabled()) 	_log.debug(methodName, "strBuffInsertBatchMaster Query ="+strBuffInsertBatchMaster);
		//ends here
		
		
		
		// insert data in the c2c batch items table
		//commented for DB2 
		//OraclePreparedStatement pstmtInsertBatchItems = null;
		PreparedStatement pstmtInsertBatchItems = null;
		StringBuffer strBuffInsertBatchItems = new StringBuffer("INSERT INTO c2c_batch_items (batch_id, batch_detail_id, ");
		strBuffInsertBatchItems.append("category_code, msisdn, user_id, status, modified_by, modified_on, user_grade_code, ");
		strBuffInsertBatchItems.append("transfer_date, txn_profile, ");
		strBuffInsertBatchItems.append("commission_profile_set_id, commission_profile_ver, commission_profile_detail_id, ");
		strBuffInsertBatchItems.append("commission_type, commission_rate, commission_value, tax1_type, tax1_rate, ");
		strBuffInsertBatchItems.append("tax1_value, tax2_type, tax2_rate, tax2_value, tax3_type, tax3_rate, ");
		strBuffInsertBatchItems.append("tax3_value, requested_quantity, transfer_mrp, initiator_remarks, external_code,rcrd_status,transfer_type,transfer_sub_type,opt_batch_id) "); 
		strBuffInsertBatchItems.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		if (_log.isDebugEnabled()) 	_log.debug(methodName, "strBuffInsertBatchItems Query ="+strBuffInsertBatchItems);
		//ends here
		//update master table with OPEN status
		PreparedStatement pstmtUpdateBatchMaster = null;
		StringBuffer strBuffUpdateBatchMaster = new StringBuffer("UPDATE c2c_batches SET batch_total_record=? , status =? WHERE batch_id=? and opt_batch_id=? ");
		if (_log.isDebugEnabled()) 	_log.debug(methodName, "strBuffUpdateBatchMaster Query ="+strBuffUpdateBatchMaster);
		int totalSuccessRecords=0;
		try
		{
			
			pstmtSelectTrfRule=p_con.prepareStatement(strBuffSelectTrfRule.toString());
			pstmtSelectTrfRuleProd=p_con.prepareStatement(strBuffSelectTrfRuleProd.toString());
			pstmtSelectCProfileProd=p_con.prepareStatement(strBuffSelectCProfileProd.toString());
			pstmtSelectCProfileProdDetail=p_con.prepareStatement(strBuffSelectCProfileProdDetail.toString());
			pstmtSelectTProfileProd=p_con.prepareStatement(strBuffSelectTProfileProd.toString());
			pstmtInsertBatchMaster=(PreparedStatement)p_con.prepareStatement(strBuffInsertBatchMaster.toString());
			
			pstmtInsertBatchItems=(PreparedStatement)p_con.prepareStatement(strBuffInsertBatchItems.toString());
			pstmtUpdateBatchMaster=p_con.prepareStatement(strBuffUpdateBatchMaster.toString());
			ChannelTransferRuleVO rulesVO = null;
			int index = 0;
			C2CBatchItemsVO  batchItemsVO = null;
			
			HashMap transferRuleMap = new HashMap();
			HashMap transferRuleNotExistMap = new HashMap();
			HashMap transferRuleProdNotExistMap = new HashMap();
			HashMap transferProfileMap = new HashMap();
			long requestedValue=0;
			long minTrfValue=0;
			long maxTrfValue=0;
			long multipleOf=0;
			ArrayList transferItemsList = null;
			ChannelTransferItemsVO channelTransferItemsVO = null;
			
			Iterator batchMasterItemsListMapIter = batchMasterItemsListMap.entrySet().iterator();
			while(batchMasterItemsListMapIter.hasNext()){
				Map.Entry mapElement = (Map.Entry)batchMasterItemsListMapIter.next(); 
            	p_batchMasterVO = (C2CBatchMasterVO) mapElement.getKey(); 
            	p_batchItemsList = (ArrayList) mapElement.getValue();
            	errorKey = p_batchMasterVO.getBatchId()+"-"+p_batchMasterVO.getOptBatchId();
            	totalNumberOfRecordsToProcess = totalNumberOfRecordsToProcess+p_batchMasterVO.getBatchTotalRecord();
            	// insert the master data
    			index=0;
    			pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getBatchId());
				pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getNetworkCode());
				pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getNetworkCodeFor());
				pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getBatchName());
				pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getStatus());
				pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getDomainCode());
				pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getProductCode());
				pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getBatchFileName());
				pstmtInsertBatchMaster.setLong(++index,p_batchMasterVO.getBatchTotalRecord());
				pstmtInsertBatchMaster.setTimestamp(++index,BTSLUtil.getTimestampFromUtilDate(p_batchMasterVO.getBatchDate()));
				pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getCreatedBy());
				pstmtInsertBatchMaster.setTimestamp(++index,BTSLUtil.getTimestampFromUtilDate(p_batchMasterVO.getCreatedOn()));
				pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getModifiedBy());
				pstmtInsertBatchMaster.setTimestamp(++index,BTSLUtil.getTimestampFromUtilDate(p_batchMasterVO.getModifiedOn()));
				pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getDefaultLang());
				pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getSecondLang());
				pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getUserId());
				pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getOptBatchId());
				
				int queryExecutionCount = pstmtInsertBatchMaster.executeUpdate();
				if(queryExecutionCount<=0)
				{
				    p_con.rollback();
				    _log.error(methodName,"Unable to insert in the batch master table.");
					BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB Error Unable to insert in the batch master table","queryExecutionCount="+queryExecutionCount);
				    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[initiateBatchC2CTransfer]","","","","Unable to insert in the batch master table.");
				    throw new BTSLBaseException(this, methodName,"error.general.sql.processing");
				}
				//ends here
				
				String msgArr[]=null;
				for(int i=0,j=p_batchItemsList.size();i<j;i++)
				{
					batchItemsVO=(C2CBatchItemsVO) p_batchItemsList.get(i);
					//In case of operator batch id is null in batch items
					if(BTSLUtil.isNullString(batchItemsVO.getOptBatchId()) && !BTSLUtil.isNullString(p_batchMasterVO.getOptBatchId())) {
						batchItemsVO.setOptBatchId(p_batchMasterVO.getOptBatchId());
					}
					// load the product's informaiton.
					if(transferRuleNotExistMap.get(batchItemsVO.getCategoryCode())==null)
					{
						if(transferRuleProdNotExistMap.get(batchItemsVO.getCategoryCode())==null)
						{
							if(transferRuleMap.get(batchItemsVO.getCategoryCode())==null)
							{
								index=0;
								pstmtSelectTrfRule.setString(++index,p_batchMasterVO.getNetworkCode());
								pstmtSelectTrfRule.setString(++index,p_batchMasterVO.getDomainCode());
								pstmtSelectTrfRule.setString(++index,batchItemsVO.getCategoryCode());
								rsSelectTrfRule = pstmtSelectTrfRule.executeQuery();
								pstmtSelectTrfRule.clearParameters();
								if (rsSelectTrfRule.next())
								{
									rulesVO = new ChannelTransferRuleVO();
									rulesVO.setTransferRuleID(rsSelectTrfRule.getString("transfer_rule_id"));
									rulesVO.setTransferType(rsSelectTrfRule.getString("transfer_type"));
									rulesVO.setTransferAllowed(rsSelectTrfRule.getString("transfer_allowed"));
									index=0;
									pstmtSelectTrfRuleProd.setString(++index,rulesVO.getTransferRuleID());
									pstmtSelectTrfRuleProd.setString(++index,p_batchMasterVO.getProductCode());
									rsSelectTrfRuleProd  = pstmtSelectTrfRuleProd.executeQuery();
									pstmtSelectTrfRuleProd.clearParameters();
									if(!rsSelectTrfRuleProd.next())
									{
										transferRuleProdNotExistMap.put(batchItemsVO.getCategoryCode(),batchItemsVO.getCategoryCode());
										//put error log Prodcuct is not in the transfer rule
										errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.prodnotintrfrule"));
										prepareOperatorBatchC2CErrorList(errorVO, errorKey, errorListHashMap);
										BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Product is not in the transfer rule","");
										continue;
									}
									transferRuleMap.put(batchItemsVO.getCategoryCode(),rulesVO );
								}
								else
								{
									transferRuleNotExistMap.put(batchItemsVO.getCategoryCode(),batchItemsVO.getCategoryCode());
									// put error log transfer rule not defined
								    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.trfrulenotdefined"));
									prepareOperatorBatchC2CErrorList(errorVO, errorKey, errorListHashMap);
									BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Transfer rule not defined","");
									continue;
								}
							}// transfer rule loading
						}// Procuct is not associated with transfer rule not defined check
						else
						{
							//put error log Procuct is not in the transfer rule
						    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.prodnotintrfrule"));
							prepareOperatorBatchC2CErrorList(errorVO, errorKey, errorListHashMap);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Product is not in the transfer rule","");
							continue;
						}
					}// transfer rule not defined check
					else
					{
						// put error log transfer rule not defined
					    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.trfrulenotdefined"));
						prepareOperatorBatchC2CErrorList(errorVO, errorKey, errorListHashMap);
						BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Transfer rule not defined","");
						continue;
					}
					rulesVO=(ChannelTransferRuleVO)transferRuleMap.get(batchItemsVO.getCategoryCode());
					if(PretupsI.NO.equals(rulesVO.getTransferAllowed()))
		            {
						//put error according to the transfer rule C2C transfer is not allowed.
					    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.c2cnotallowed"));
						prepareOperatorBatchC2CErrorList(errorVO, errorKey, errorListHashMap);
						BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : According to the transfer rule C2C transfer is not allowed","");
						continue;
					}
					// check the transfer profile product code
					// transfer profile check ends here
					if(transferProfileMap.get(batchItemsVO.getTxnProfile())==null)
					{
						index=0;
						pstmtSelectTProfileProd.setString(++index,batchItemsVO.getTxnProfile());
						pstmtSelectTProfileProd.setString(++index,p_batchMasterVO.getProductCode());
						pstmtSelectTProfileProd.setString(++index,PretupsI.PARENT_PROFILE_ID_CATEGORY);
						rsSelectTProfileProd=pstmtSelectTProfileProd.executeQuery();
						pstmtSelectTProfileProd.clearParameters();
						if(!rsSelectTProfileProd.next())
						{
							transferProfileMap.put(batchItemsVO.getTxnProfile(),"false");
							//put error Transfer profile for this product is not define
						    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.trfprofilenotdefined"));
							prepareOperatorBatchC2CErrorList(errorVO, errorKey, errorListHashMap);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Transfer profile for this product is not defined","");
							continue;
						}
						transferProfileMap.put(batchItemsVO.getTxnProfile(),"true");
					}
					else
					{
						
						if("false".equals(transferProfileMap.get(batchItemsVO.getTxnProfile())))
						{
							// put error Transfer profile for this product is not define
						    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.trfprofilenotdefined"));
							prepareOperatorBatchC2CErrorList(errorVO, errorKey, errorListHashMap);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Transfer profile for this product is not defined","");
							continue;
						}
					}
					
					// check the commisson profile applicability and other checks related to the commission profile
					index=0;
					pstmtSelectCProfileProd.setString(++index,p_batchMasterVO.getProductCode());
					pstmtSelectCProfileProd.setString(++index,batchItemsVO.getCommissionProfileSetId());
					pstmtSelectCProfileProd.setString(++index,batchItemsVO.getCommissionProfileVer());
					rsSelectCProfileProd=pstmtSelectCProfileProd.executeQuery();
					pstmtSelectCProfileProd.clearParameters();
					if(!rsSelectCProfileProd.next())
					{
						// put error commission profile for this product is not defined
					    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.commprfnotdefined"));
						prepareOperatorBatchC2CErrorList(errorVO, errorKey, errorListHashMap);
						BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Commission profile for this product is not defined","");
						continue;
					}
					requestedValue=batchItemsVO.getRequestedQuantity();
					minTrfValue=rsSelectCProfileProd.getLong("min_transfer_value");
					maxTrfValue=rsSelectCProfileProd.getLong("max_transfer_value");
					if(minTrfValue > requestedValue || maxTrfValue < requestedValue )
					{
						msgArr=new String[3];
						msgArr[0]=PretupsBL.getDisplayAmount(requestedValue);
						msgArr[1]=PretupsBL.getDisplayAmount(minTrfValue);
						msgArr[2]=PretupsBL.getDisplayAmount(maxTrfValue);
						// put error requested quantity is not between min and max values
					    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.qtymaxmin",msgArr));
					    msgArr=null;
					    prepareOperatorBatchC2CErrorList(errorVO, errorKey, errorListHashMap);
						BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Requested quantity is not between min and max values","minTrfValue="+minTrfValue+", maxTrfValue="+maxTrfValue);
						continue;
					}
					multipleOf=rsSelectCProfileProd.getLong("transfer_multiple_off");
					if(requestedValue%multipleOf != 0)
					{
						// put error requested quantity is not multiple of
					    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.notmulof",new String[]{PretupsBL.getDisplayAmount(multipleOf)}));
						prepareOperatorBatchC2CErrorList(errorVO, errorKey, errorListHashMap);
						BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Requested quantity is not in multiple value","multiple of="+multipleOf);
						continue;
					}
					
					index=0;
					pstmtSelectCProfileProdDetail.setString(++index,rsSelectCProfileProd.getString("comm_profile_products_id"));
					pstmtSelectCProfileProdDetail.setLong(++index,requestedValue);
					pstmtSelectCProfileProdDetail.setLong(++index,requestedValue);
					rsSelectCProfileProdDetail=pstmtSelectCProfileProdDetail.executeQuery();
					pstmtSelectCProfileProdDetail.clearParameters();
					if(!rsSelectCProfileProdDetail.next())
					{
						// put error commission profile slab is not define for the requested value
					    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.commslabnotdefined"));
						prepareOperatorBatchC2CErrorList(errorVO, errorKey, errorListHashMap);
						BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Commission profile slab is not define for the requested value","");
						continue;
					}	
					 // to calculate tax
					transferItemsList = new ArrayList();
					channelTransferItemsVO = new ChannelTransferItemsVO ();
					// this value will be inserted into the table as the requested qty
					channelTransferItemsVO.setRequiredQuantity(requestedValue);
					// this value will be used in the tax calculation.
					channelTransferItemsVO.setRequestedQuantity(PretupsBL.getDisplayAmount(requestedValue));
					channelTransferItemsVO.setCommProfileDetailID(rsSelectCProfileProdDetail.getString("comm_profile_detail_id"));
					channelTransferItemsVO.setUnitValue(p_batchMasterVO.getProductMrp());
					channelTransferItemsVO.setCommRate(rsSelectCProfileProdDetail.getLong("commission_rate"));
					channelTransferItemsVO.setCommType(rsSelectCProfileProdDetail.getString("commission_type"));
					channelTransferItemsVO.setDiscountRate(rsSelectCProfileProd.getLong("discount_rate"));
					channelTransferItemsVO.setDiscountType(rsSelectCProfileProd.getString("discount_type"));
					channelTransferItemsVO.setTax1Rate(rsSelectCProfileProdDetail.getLong("tax1_rate"));
					channelTransferItemsVO.setTax1Type(rsSelectCProfileProdDetail.getString("tax1_type"));
					channelTransferItemsVO.setTax2Rate(rsSelectCProfileProdDetail.getLong("tax2_rate"));
					channelTransferItemsVO.setTax2Type(rsSelectCProfileProdDetail.getString("tax2_type"));
					channelTransferItemsVO.setTax3Rate(rsSelectCProfileProdDetail.getLong("tax3_rate"));
					channelTransferItemsVO.setTax3Type(rsSelectCProfileProdDetail.getString("tax3_type"));
					if(PretupsI.YES.equals(rsSelectCProfileProd.getString("taxes_on_foc_applicable")))
					{					
						channelTransferItemsVO.setTaxOnC2CTransfer(PretupsI.YES);
					}
					else
						channelTransferItemsVO.setTaxOnC2CTransfer(PretupsI.NO);
					//added by vikram
					if(PretupsI.YES.equals(rsSelectCProfileProd.getString("taxes_on_channel_transfer")))
					{					
						channelTransferItemsVO.setTaxOnChannelTransfer(PretupsI.YES);
					}
					else
						channelTransferItemsVO.setTaxOnChannelTransfer(PretupsI.NO);
					transferItemsList.add(channelTransferItemsVO);
	                ChannelTransferVO channelTransferVO=new ChannelTransferVO();
	                channelTransferVO.setChannelTransferitemsVOList(transferItemsList);
	                channelTransferVO.setTransferSubType(p_batchMasterVO.getTransferSubType());
					//ChannelTransferBL.calculateMRPWithTaxAndDiscount(channelTransferVO,PretupsI.TRANSFER_TYPE_FOC);
	                ChannelTransferBL.calculateMRPWithTaxAndDiscount(channelTransferVO,PretupsI.TRANSFER_TYPE_C2C);
					// taxes on C2C required
					// ends commission profile validaiton
					// insert items data here
					index=0;
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getBatchId());
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getBatchDetailId());
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getCategoryCode());
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getMsisdn());
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getUserId());
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getStatus());
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getModifiedBy());
					pstmtInsertBatchItems.setTimestamp(++index,BTSLUtil.getTimestampFromUtilDate(batchItemsVO.getModifiedOn()));
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getUserGradeCode());
					pstmtInsertBatchItems.setDate(++index,BTSLUtil.getSQLDateFromUtilDate(batchItemsVO.getTransferDate()));
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getTxnProfile());
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getCommissionProfileSetId());
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getCommissionProfileVer());
					pstmtInsertBatchItems.setString(++index,channelTransferItemsVO.getCommProfileDetailID());
					pstmtInsertBatchItems.setString(++index,channelTransferItemsVO.getCommType());
					pstmtInsertBatchItems.setDouble(++index,channelTransferItemsVO.getCommRate());
					pstmtInsertBatchItems.setLong(++index,channelTransferItemsVO.getCommValue());
					pstmtInsertBatchItems.setString(++index,channelTransferItemsVO.getTax1Type());
					pstmtInsertBatchItems.setDouble(++index,channelTransferItemsVO.getTax1Rate());
					pstmtInsertBatchItems.setLong(++index,channelTransferItemsVO.getTax1Value());
					pstmtInsertBatchItems.setString(++index,channelTransferItemsVO.getTax2Type());
					pstmtInsertBatchItems.setDouble(++index,channelTransferItemsVO.getTax2Rate());
					pstmtInsertBatchItems.setLong(++index,channelTransferItemsVO.getTax2Value());
					pstmtInsertBatchItems.setString(++index,channelTransferItemsVO.getTax3Type());
					pstmtInsertBatchItems.setDouble(++index,channelTransferItemsVO.getTax3Rate());
					pstmtInsertBatchItems.setLong(++index,channelTransferItemsVO.getTax3Value());
					pstmtInsertBatchItems.setString(++index,String.valueOf(channelTransferItemsVO.getRequiredQuantity()));
					pstmtInsertBatchItems.setLong(++index,channelTransferItemsVO.getProductTotalMRP());
					//pstmtInsertBatchItems.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);//commented for DB2 
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getInitiatorRemarks());
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getExternalCode());
					pstmtInsertBatchItems.setString(++index,PretupsI.CHANNEL_TRANSFER_BATCH_C2C_ITEM_RCRDSTATUS_PROCESSED);
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getTransferType());
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getTransferSubType());
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getOptBatchId());
					queryExecutionCount=pstmtInsertBatchItems.executeUpdate();
					if(queryExecutionCount<=0)
					{
					    p_con.rollback();
					    //put error record can not be inserted
					    _log.error(methodName, "Record cannot be inserted in batch items table");
						BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB Error Record cannot be inserted in batch items table","queryExecutionCount="+queryExecutionCount);
					}
					else
					{
					    p_con.commit();
					    totalSuccessRecords++;
					    // put success in the logger file.
						BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"PASS : Record inserted successfully in batch items table","queryExecutionCount="+queryExecutionCount);
					}
					//ends here
				}// for loop for the batch items
			}//End of Batch for  sender 
		} 
		catch (SQLException sqe)
		{
			_log.error(methodName, "SQLException : " + sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[initiateBatchC2CTransfer]","","","","SQL Exception:"+sqe.getMessage());
			BatchC2CFileProcessLog.c2cBatchMasterLog(methodName,p_batchMasterVO,"FAIL : SQL Exception:"+sqe.getMessage(),"TOTAL SUCCESS RECORDS = "+totalSuccessRecords);
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		}
		catch (Exception ex)
		{
		    _log.error(methodName, "Exception : " + ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[initiateBatchC2CTransfer]","","","","Exception:"+ex.getMessage());
			BatchC2CFileProcessLog.c2cBatchMasterLog(methodName,p_batchMasterVO,"FAIL : Exception:"+ex.getMessage(),"TOTAL SUCCESS RECORDS = "+totalSuccessRecords);
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		}
		finally
		{
		    
		    try{if (rsSelectTrfRule != null){rsSelectTrfRule.close();}} catch (Exception e){}
			try{if (pstmtSelectTrfRule != null){pstmtSelectTrfRule.close();}} catch (Exception e){}
		    try{if (rsSelectTrfRuleProd != null){rsSelectTrfRuleProd.close();}} catch (Exception e){}
			try{if (pstmtSelectTrfRuleProd != null){pstmtSelectTrfRuleProd.close();}} catch (Exception e){}
		    try{if (rsSelectCProfileProd != null){rsSelectCProfileProd.close();}} catch (Exception e){}
			try{if (pstmtSelectCProfileProd != null){pstmtSelectCProfileProd.close();}} catch (Exception e){}
			try{if (rsSelectCProfileProdDetail != null){rsSelectCProfileProdDetail.close();}} catch (Exception e){}
			try{if (pstmtSelectCProfileProdDetail != null){pstmtSelectCProfileProdDetail.close();}} catch (Exception e){}
		    try{if (rsSelectTProfileProd != null){rsSelectTProfileProd.close();}} catch (Exception e){}
			try{if (pstmtSelectTProfileProd != null){pstmtSelectTProfileProd.close();}} catch (Exception e){}
			try{if (pstmtInsertBatchMaster != null){pstmtInsertBatchMaster.close();}} catch (Exception e){}
			try{if (pstmtInsertBatchItems != null){pstmtInsertBatchItems.close();}} catch (Exception e){}
			try
			{
				// if all records contains errors then rollback the master table entry
				Iterator errorListHashMapIter = errorListHashMap.entrySet().iterator();
				while(errorListHashMapIter.hasNext()){
					Map.Entry mapElement = (Map.Entry)errorListHashMapIter.next(); 
					errorList.add((ArrayList) mapElement.getValue());
				}
				if(errorList!=null &&(errorList.size()==totalNumberOfRecordsToProcess))
				{
					p_con.rollback();
					_log.error(methodName, "ALL the records conatins errors and cannot be inserted in db");
					BatchC2CFileProcessLog.c2cBatchMasterLog(methodName,p_batchMasterVO,"FAIL : ALL the records conatins errors and cannot be inserted in DB ","");
				}
				//else update the master table with the open status and total number of records.
				else
				{
					Iterator batchMasterItemsListMapIter = batchMasterItemsListMap.entrySet().iterator();
					while(batchMasterItemsListMapIter.hasNext()){
						Map.Entry mapElement = (Map.Entry)batchMasterItemsListMapIter.next(); 
		            	p_batchMasterVO = (C2CBatchMasterVO) mapElement.getKey(); 
		            	p_batchItemsList = (ArrayList) mapElement.getValue();
		            	int index=0;
						int queryExecutionCount=-1;
						pstmtUpdateBatchMaster.setInt(++index,p_batchMasterVO.getBatchTotalRecord()-errorListHashMap.size());
						pstmtUpdateBatchMaster.setString(++index,PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_OPEN);
						pstmtUpdateBatchMaster.setString(++index,p_batchMasterVO.getBatchId());
						pstmtUpdateBatchMaster.setString(++index,p_batchMasterVO.getOptBatchId());
						queryExecutionCount=pstmtUpdateBatchMaster.executeUpdate();
					    if(queryExecutionCount<=0) //Means No Records Updated
			   		    {
			   		        _log.error(methodName,"Unable to Update the batch size in master table..");
			   		        p_con.rollback();
							EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[initiateBatchC2CTransfer]","","","","Error while updating C2C_BATCHES table. Batch id="+p_batchMasterVO.getBatchId());
			   		    }
			   		    else
			   		    {
			   		        p_con.commit();
			   		    }
					}
		   		}

			}
			catch(Exception e)
			{
				try{if(p_con!=null)p_con.rollback();}catch(Exception ex){}
			}
			try{if (pstmtUpdateBatchMaster != null){pstmtUpdateBatchMaster.close();}} catch (Exception e){}
			if (_log.isDebugEnabled()) _log.debug(methodName, "Exiting: errorList.size()=" + errorListHashMap.size());
		}
		return errorList;
	}
	
	private void prepareOperatorBatchC2CErrorList(ListValueVO errorVO, String errorKey, HashMap<String, ArrayList> errorListHashMap) {
		// TODO Auto-generated method stub
		
	}

	/**
     * To Check whether batch is  modidfied or not
     * 
     * @param p_con
     * @param p_oldlastModified
     * @param p_batchID
     * @return
     * @throws BTSLBaseException
     */
    public boolean isBatchModified(Connection p_con, long p_oldlastModified, String p_batchID,String p_optBatchID) throws BTSLBaseException
    {
    	final String methodName = "isBatchModified";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered:p_oldlastModified=" + p_oldlastModified + ",p_batchID=" + p_batchID+", p_optBatchID="+p_optBatchID);
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean modified = false;
        String sqlRecordModified ="SELECT modified_on FROM c2c_batches WHERE batch_id = ? and opt_batch_id= ? ";
        java.sql.Timestamp newlastModified = null;
        if (p_oldlastModified == 0)
            return false;
        try
        {
            if (_log.isDebugEnabled())
                _log.debug(methodName, "sqlRecordModified=" + sqlRecordModified);
            pstmtSelect = p_con.prepareStatement(sqlRecordModified);
            pstmtSelect.setString(1, p_batchID);
            pstmtSelect.setString(2, p_optBatchID);            
            rs = pstmtSelect.executeQuery();
            if (rs.next())
            {
                newlastModified = rs.getTimestamp("modified_on");
            }
            // The record is not present because the record is modified by other
            // person and the
            // modification is done on the value of the primary key.
            else
            {
                modified = true;
                return true;
            }
            if (newlastModified.getTime() != p_oldlastModified)
                modified = true;
        }// end of try
        catch (SQLException sqe)
        {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorC2CBatchTransferDAO[isBatchModified]", "", "", "", "SQL Exception:"
                    + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e)
        {
            _log.error(methodName, "Exception:" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorC2CBatchTransferDAO[isBatchModified]", "", "", "", "Exception:"
                    + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } 
		finally
        {
            try{if (rs != null)rs.close();} catch (Exception ex){}
            try{if (pstmtSelect != null)pstmtSelect.close();} catch (Exception ex){}
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting:modified=" + modified);
        }// end of finally
        return modified;
    }// end recordModified
	
	/**
     *  isPendingTransactionExist
     *  This method is to check that the user has any panding request of transfer or not
     * @param p_con
     * @param p_userID
     * @return
     * @throws BTSLBaseException boolean
     */
	public boolean isPendingTransactionExist(Connection p_con, String p_userID) throws BTSLBaseException
    {
		final String methodName = "isPendingTransactionExist";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered   p_userID " + p_userID);
		boolean isExist=false;
		PreparedStatement pstmt = null;
        ResultSet rs = null;
		try
        {
			StringBuffer strBuff = new StringBuffer();
			strBuff.append(" SELECT 1  ");
	        strBuff.append(" FROM c2c_batch_items ");
			strBuff.append(" WHERE user_id=? AND ");
			strBuff.append(" (status <> ? AND status <> ? )");
	        String sqlSelect = strBuff.toString();
	        if (_log.isDebugEnabled())
				_log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            pstmt = p_con.prepareStatement(sqlSelect);
			int i=1;
            pstmt.setString(i++, p_userID);
			pstmt.setString(i++, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
			pstmt.setString(i++, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
            rs = pstmt.executeQuery();
            if(rs.next())
				isExist=true;
		}
        catch (SQLException sqe)
        {
            _log.error(methodName, "SQLException : " + sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorC2CBatchTransferDAO[isPendingTransactionExist]", "", "", "", "SQL Exception:"
                    + sqe.getMessage());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        }
        catch (Exception ex)
        {
            _log.error(methodName, "Exception : " + ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorC2CBatchTransferDAO[isPendingTransactionExist]", "", "", "", "Exception:"
                    + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } 
        finally
        {
            try{if (rs != null){rs.close();}}catch (Exception e){}
			try{if (pstmt != null){pstmt.close();}}catch (Exception e){}
			if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting:  isExist=" + isExist);
        }
        return isExist;
    }   
	
	
	/**
     *  updateBatchStatus
     *  This method is to update the status of C2C_BATCHES table
     * @param p_con
     * @param p_batchID
     * @param p_newStatus
     * @param  p_oldStatus
     * @return
     * @throws BTSLBaseException boolean
     */
	public int updateBatchStatus(Connection p_con, String p_batchID, String p_optBatchID,  String p_newStatus ,String p_oldStatus) throws BTSLBaseException
    {
		final String  methodName = "updateBatchStatus" ; 
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered   p_batchID " + p_batchID+" p_newStatus="+p_newStatus+" p_oldStatus="+p_oldStatus+", p_optBatchID="+p_optBatchID);
		PreparedStatement pstmt = null;
        int updateCount=-1;
		try
        {
			StringBuffer sqlBuffer = new StringBuffer("UPDATE c2c_batches SET status=? ");
		    sqlBuffer.append(" WHERE batch_id=? and opt_batch_id=? AND status=? ");
		    String updateC2CBatches = sqlBuffer.toString();
		    if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY updateC2CBatches=" + updateC2CBatches);

            pstmt = p_con.prepareStatement(updateC2CBatches);
			int i=1;
			pstmt.setString(i++, p_newStatus);
			pstmt.setString(i++, p_batchID);
			pstmt.setString(i++, p_optBatchID);
			pstmt.setString(i++, p_oldStatus);			
			
			updateCount = pstmt.executeUpdate();
		}
        catch (SQLException sqe)
        {
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorC2CBatchTransferDAO[updateBatchStatus]", "", "", "", "SQL Exception:"
                    + sqe.getMessage());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
        }
        catch (Exception ex)
        {
            _log.error(methodName, "Exception : " + ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorC2CBatchTransferDAO[updateBatchStatus]", "", "", "", "Exception:"
                    + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } 
        finally
        {
			try{if (pstmt != null){pstmt.close();}}catch (Exception e){}
			if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting:  updateCount=" + updateCount);
        }
        return updateCount;
    }

	/**
	 * This method will load the batches that are within the geography of user whose userId is passed and batch id basis.
	 * with status(OPEN) also in items table for corresponding master record.
	 * @Connection p_con
	 * @String p_goeDomain
	 * @String p_domain
	 * @String p_productCode
	 * @String p_batchid
	 * @String p_msisdn
	 * @Date p_fromDate
	 * @Date p_toDate
	 * @param p_loginID TODO
	 * @throws  BTSLBaseException
	
	 */
    public ArrayList loadBatchC2CMasterDetails(Connection p_con,String p_goeDomain,String p_domain,String p_productCode, String p_batchid, String p_msisdn, Date p_fromDate, Date p_toDate, String p_loginID,String p_categoryCode,String p_loginCatCode,String p_userName) throws BTSLBaseException
    {
    	final String methodName = "loadBatchC2CMasterDetails";
    	if (_log.isDebugEnabled())           _log.debug(methodName, "Entered p_goeDomain="+p_goeDomain+" p_domain="+p_domain+" p_productCode="+p_productCode+" p_batchid="+p_batchid+" p_msisdn="+p_msisdn+" p_fromDate="+p_fromDate+" p_toDate="+p_toDate+" p_loginID="+p_loginID+"p_categoryCode"+p_categoryCode+"p_logincatCode"+p_loginCatCode+"p_userName"+p_userName);
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;

    	String sqlSelect = operatorC2CBatchTransferQry.loadBatchC2CMasterDetailsQry(p_batchid, p_categoryCode, p_loginCatCode, p_userName);
    	if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
    	ArrayList list = new ArrayList();
    	try
    	{
    		pstmt = p_con.prepareStatement(sqlSelect);
    		int i = 0;
    		pstmt.setString(++i, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
    		pstmt.setString(++i, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
    		pstmt.setString(++i, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
    		//pstmt.setString(++i, p_loginID);
    		if(p_batchid !=null)
    		{ pstmt.setString(++i, p_batchid);
    		pstmt.setString(++i, p_loginID);
    		}
    		else
    		{
    			pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
    			pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
    			if(p_categoryCode.equals(p_loginCatCode))
    				pstmt.setString(++i, p_loginID);


    			if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY BTSLUtil.getSQLDateFromUtilDate(p_fromDate)=" + BTSLUtil.getSQLDateFromUtilDate(p_toDate)+" BTSLUtil.getSQLDateFromUtilDate(p_fromDate)="+BTSLUtil.getSQLDateFromUtilDate(p_toDate));
    		}
    		rs = pstmt.executeQuery();
    		C2CBatchMasterVO c2cBatchMasterVO = null;
    		while (rs.next())
    		{
    			c2cBatchMasterVO=new C2CBatchMasterVO();
    			c2cBatchMasterVO.setBatchId(rs.getString("batch_id"));
    			c2cBatchMasterVO.setDomainCode(rs.getString("domain_code"));
    			c2cBatchMasterVO.setBatchName(rs.getString("batch_name"));
    			c2cBatchMasterVO.setProductName(rs.getString("product_name"));
    			c2cBatchMasterVO.setProductMrp(rs.getLong("unit_value"));
    			c2cBatchMasterVO.setProductMrpStr(PretupsBL.getDisplayAmount(rs.getLong("unit_value")));
    			c2cBatchMasterVO.setBatchTotalRecord(rs.getInt("batch_total_record"));
    			c2cBatchMasterVO.setNewRecords(rs.getInt("new"));

    			c2cBatchMasterVO.setClosedRecords(rs.getInt("close"));
    			c2cBatchMasterVO.setRejectedRecords(rs.getInt("cncl"));
    			c2cBatchMasterVO.setBatchDate(rs.getDate("batch_date"));
    			if(c2cBatchMasterVO.getBatchDate()!=null)
    				c2cBatchMasterVO.setBatchDateStr(BTSLUtil.getDateStringFromDate(c2cBatchMasterVO.getBatchDate()));
    			list.add(c2cBatchMasterVO);
    		}
    	} 
    	catch (SQLException sqe)
    	{
    		EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[loadBatchC2CMasterDetails]","","","","SQL Exception:"+sqe.getMessage());
    		throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
    	}
    	catch (Exception ex)
    	{
    		EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[loadBatchC2CMasterDetails]","","","","Exception:"+ex.getMessage());
    		throw new BTSLBaseException(this, methodName, "error.general.processing");
    	}
    	finally
    	{
    		try{if (rs != null){rs.close();}} catch (Exception e){}
    		try{if (pstmt != null){pstmt.close();}} catch (Exception e){}
    		if (_log.isDebugEnabled()) _log.debug(methodName, "Exiting: c2cBatchMasterVOList size=" + list.size());
    	}
    	return list;
    }
    
    public LinkedHashMap loadUserListForC2CXfr(Connection p_con,String p_txnType,ChannelTransferRuleVO p_channelTransferRuleVO,String p_toCategoryCode,String p_userName,ChannelUserVO p_channelUserVO) throws BTSLBaseException
	{
    	final String methodName="loadUserListForC2CXfr";
		if (_log.isDebugEnabled())
			_log.debug(methodName,"Entered p_txnType="+p_txnType+", ToCategoryCode: "+p_toCategoryCode+" User Name: "+p_userName+",p_channelTransferRuleVO="+p_channelTransferRuleVO+",p_channelUserVO="+p_channelUserVO);
		LinkedHashMap linkedHashMap = new LinkedHashMap();
		boolean uncontrollAllowed=false;
		boolean fixedLevelParent=false;
		boolean fixedLevelHierarchy=false;
		String fixedCatStr=null;
		boolean directAllowed=false;
		boolean chnlByPassAllowed=false;
		String unctrlLevel=null;
		String ctrlLevel=null;
		// if txn is for transfer then get the value of the transfer paramenters
		if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_txnType))
		{
			if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_PARENT.equals(p_channelTransferRuleVO.getFixedTransferLevel()))
			{
				fixedLevelParent=true;
				fixedCatStr=getCategoryStrValue(p_channelTransferRuleVO.getFixedTransferCategory());
			}
			else if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_HIERARCHY.equals(p_channelTransferRuleVO.getFixedTransferLevel()))
			{
				fixedLevelHierarchy=true;
				fixedCatStr=getCategoryStrValue(p_channelTransferRuleVO.getFixedTransferCategory());
			}
			if(PretupsI.YES.equals(p_channelTransferRuleVO.getDirectTransferAllowed()))
				directAllowed=true;
			if(PretupsI.YES.equals(p_channelTransferRuleVO.getTransferChnlBypassAllowed()))
				chnlByPassAllowed=true;
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getUncntrlTransferAllowed()))
			{
				uncontrollAllowed=true;
				unctrlLevel=p_channelTransferRuleVO.getUncntrlTransferLevel();
			}
			ctrlLevel=p_channelTransferRuleVO.getCntrlTransferLevel();
		}
		//else if txn is for withdraw then get the value of the withdraw paramenters
		else //if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(p_txnType))
		{
			if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_PARENT.equals(p_channelTransferRuleVO.getFixedWithdrawLevel()))
			{
				fixedLevelParent=true;
				fixedCatStr=getCategoryStrValue(p_channelTransferRuleVO.getFixedWithdrawCategory());
			}
			else if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_HIERARCHY.equals(p_channelTransferRuleVO.getFixedWithdrawLevel()))
			{
				fixedLevelHierarchy=true;
				fixedCatStr=getCategoryStrValue(p_channelTransferRuleVO.getFixedWithdrawCategory());
			}
			if(PretupsI.YES.equals(p_channelTransferRuleVO.getWithdrawAllowed()))
				directAllowed=true;
			if(PretupsI.YES.equals(p_channelTransferRuleVO.getWithdrawChnlBypassAllowed()))
				chnlByPassAllowed=true;
			if (PretupsI.YES.equals(p_channelTransferRuleVO.getUncntrlWithdrawAllowed()))
			{
				uncontrollAllowed=true;
				unctrlLevel=p_channelTransferRuleVO.getUncntrlWithdrawLevel();
			}
			ctrlLevel=p_channelTransferRuleVO.getCntrlWithdrawLevel();
		}

         // to load the user list we will have to apply the check of the fixed level and fixed category in each
         // and every case.
         // Now we divide the whole conditions in various sub conditions as
        
		if(uncontrollAllowed)
		{
			if(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM.equals(unctrlLevel)||PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAINTYPE.equals(unctrlLevel)
				|| PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAIN.equals(unctrlLevel))
			{
				if(BTSLUtil.isNullString(fixedCatStr))
				{
					// load all the users form the system without any check of the fixed category
					linkedHashMap=loadUsersOutsideHireacrhy(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_userName,p_channelUserVO.getUserID());
					return linkedHashMap;
				}// fixed category null check
				else if(fixedLevelHierarchy)
				{
					// load all the users form the system, which are in the hierarchy of the users of fixedCatStr categories
					 // p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
					 // by parentID, if value of this parameter is 2 then check will be done by ownerID
					 //	other wise no check will be required. So here as uncontroll level is DOMAIN OR DOMAINTYPE
					 // pass value 0 for this parameter and null for the p_parentUserID since here no parent and 
					 //	no owner exist for the DOMAIN OR DOMAINTYPE level.
					linkedHashMap = loadUsersForHierarchyFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,null,p_userName,p_channelUserVO.getUserID(),fixedCatStr,0);
					return linkedHashMap;
				}// fixed level hierarchy check
				else if(fixedLevelParent)
				{
					// load all the users form the system, which are in the direct child of the users of fixedCatStr categories
					//	p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
					 // by parentID, if value of this parameter is 2 then check will be done by ownerID
					 //	other wise no check will be required. So here as uncontroll level is DOMAIN OR DOMAINTYPE
					 // pass value 0 for this parameter and null for the p_parentUserID since here no parent and 
					 //	no owner exist for the DOMAIN OR DOMAINTYPE level.
					linkedHashMap = loadUsersForParentFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,null,p_userName,p_channelUserVO.getUserID(),fixedCatStr,0);
					return linkedHashMap;
				}// fixed level parent check
			}// uncontrol domain check
			else if(PretupsI.CHANNEL_TRANSFER_LEVEL_OWNER.equals(unctrlLevel))
			{
				if(BTSLUtil.isNullString(fixedCatStr))
				{
					// load all the users form the system within the sender'owner hierarchy
					//without any check of the fixed category
					linkedHashMap = loadUsersByOwnerID(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getOwnerID(),p_userName,p_channelUserVO.getUserID());
					return linkedHashMap;
				}// fixed category null check
				else if(fixedLevelHierarchy)
				{
					// load all the users form the system within the sender'owner hierarchy 
					// which are in the hierarchy of the users of fixedCatStr categories
					// p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
					// by parentID, if value of this parameter is 2 then check will be done by ownerID
					// other wise no check will be required. So here as uncontroll level is OWNER
					// pass value 2 for this parameter and OWNERID for the p_parentUserID since here list is to be  
					// loaded by owner.
					linkedHashMap = loadUsersForHierarchyFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getOwnerID(),p_userName,p_channelUserVO.getUserID(),fixedCatStr,2);
					return linkedHashMap;
				}// fixed level hierarchy check
				else if(fixedLevelParent)
				{
					// load all the users form the system within the sender'owner hierarchy 
					// which are in the direct child of the users of fixedCatStr categories
					// p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
					// by parentID, if value of this parameter is 2 then check will be done by ownerID
					// other wise no check will be required. So here as uncontroll level is OWNER
					// pass value 2 for this parameter and OWNERID for the p_parentUserID since here list is to be  
					// loaded by owner.
					
					linkedHashMap = loadUsersForParentFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getOwnerID(),p_userName,p_channelUserVO.getUserID(),fixedCatStr,2);
					return linkedHashMap;
				}// fixed level parent check
			}// owner level uncontroll check
			else if(PretupsI.CHANNEL_TRANSFER_LEVEL_PARENT.equals(unctrlLevel))
			{
				if(BTSLUtil.isNullString(fixedCatStr))
				{
					// load all the users form the system within the sender's parent hierarchy 
					//without any check of the fixed category
					linkedHashMap = loadUsersByParentIDRecursive(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getParentID(),p_userName,p_channelUserVO.getUserID());
					return linkedHashMap;
				}// fixed category null check
				else if(fixedLevelHierarchy)
				{
					// load all the users form the system within the sender's parent hierarchy, 
					// which are in the hierarchy of the users of fixedCatStr categories
					// p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
					// by parentID, if value of this parameter is 2 then check will be done by ownerID
					// other wise no check will be required. So here as uncontroll level is PARENT
					// pass value 1 for this parameter and PARENTID for the p_parentUserID since here list is to be  
					// loaded by parent.
					linkedHashMap = loadUsersForHierarchyFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getParentID(),p_userName,p_channelUserVO.getUserID(),fixedCatStr,1);
					return linkedHashMap;
				}// fixed level hierarchy check
				else if(fixedLevelParent)
				{
					// load all the users form the system within the sender's parent hierarchy, 
					// which are in the direct child of the users of fixedCatStr categories
					// p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
					// by parentID, if value of this parameter is 2 then check will be done by ownerID
					// other wise no check will be required. So here as uncontroll level is PARENT
					// pass value 1 for this parameter and PARENTID for the p_parentUserID since here list is to be  
					// loaded by parent.
					linkedHashMap = loadUsersForParentFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getParentID(),p_userName,p_channelUserVO.getUserID(),fixedCatStr,1);
					return linkedHashMap;
				}// fixed level parent check
			}// parent level uncontroll check
			else if(PretupsI.CHANNEL_TRANSFER_LEVEL_SELF.equals(unctrlLevel))
			{
				if(BTSLUtil.isNullString(fixedCatStr))
				{
					// load all the users form the system within the sender hierarchy 
					// without any check of the fixed category so here sender's userID is passed in the calling
					// method as the parentID to load all the users under sender recursively
					linkedHashMap = loadUsersByParentIDRecursive(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getUserID(),p_userName,p_channelUserVO.getUserID());
					return linkedHashMap;
				}// fixed category null check
				else if(fixedLevelHierarchy)
				{
					// load all the users form the system within the sender hierarchy, 
					// which are in the hierarchy of the users of fixedCatStr categories
					// p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
					// by parentID, if value of this parameter is 2 then check will be done by ownerID
					// other wise no check will be required. So here as uncontroll level is SELF but sender user
					// have to be considered as the parent of all the requested users so 
					// pass value 1 for this parameter and sener's userID for the p_parentUserID since here list is to be  
					// loaded by senderID.
					linkedHashMap = loadUsersForHierarchyFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getUserID(),p_userName,p_channelUserVO.getUserID(),fixedCatStr,1);
					return linkedHashMap;
				}// fixed level hierarchy check
				else if(fixedLevelParent)
				{
					// load all the users form the system within the sender hierarchy, 
					// which are in the direct child of the users of fixedCatStr categories
					// p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
					// by parentID, if value of this parameter is 2 then check will be done by ownerID
					// other wise no check will be required. So here as uncontroll level is SELF but sender user
					// have to be considered as the parent of all the requested users so 
					// pass value 1 for this parameter and sener's userID for the p_parentUserID since here list is to be  
					// loaded by senderID.
					linkedHashMap = loadUsersForParentFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getUserID(),p_userName,p_channelUserVO.getUserID(),fixedCatStr,1);
					return linkedHashMap;
				}// fixed level parent check
			}// Self level uncontroll check
		}// uncontrol transfer allowed check
		else
		{
			if(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM.equals(ctrlLevel)|| PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAINTYPE.equals(ctrlLevel)
					|| PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAIN.equals(ctrlLevel))
			{
				if(BTSLUtil.isNullString(fixedCatStr))
				{
					// load all the users form the system within the receiver domain for the direct child of the owner 
					//without any check of the fixed category
					if(directAllowed)
					{
						//  load all the users form the system 
						// which are direct child of the owner 
						// Sandeep goel ID USD001 
						// method is changed to remove the problem as login user is also coming in the list
						
						linkedHashMap = loadUsersByDomainID(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelTransferRuleVO.getToDomainCode(),p_userName,p_channelUserVO.getUserID());
					}// direct transfer check
					if(chnlByPassAllowed)
					{
						//load all the users form the system 
						// which are not direct child of the owner 
						// Sandeep goel ID USD001 
						// method is changed to remove the problem as login user is also coming in the list
						//linkedHashMap.addAll(loadUsersChnlBypassByDomainID(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelTransferRuleVO.getToDomainCode(),p_userName,p_channelUserVO.getUserID()));
						linkedHashMap=loadUsersChnlBypassByDomainID(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelTransferRuleVO.getToDomainCode(),p_userName,p_channelUserVO.getUserID());
					}// channel by pass check
					return linkedHashMap;
				}// fixed category null check
				else if(fixedLevelHierarchy)
				{
					// load all the users form the system within the sender domain, 
					// which are in the hierarchy of the users of fixedCatStr categories
					//  p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
					 // by parentID, if value of this parameter is 2 then check will be done by ownerID
					 //	other wise no check will be required. So here as controll level is DOMAIN OR DOMAINTYPE
					 // pass value 0 for this parameter and null for the p_parentUserID since here no parent and 
					 //	no owner exist for the DOMAIN OR DOMAINTYPE level.					
					linkedHashMap = loadUsersForHierarchyFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,null,p_userName,p_channelUserVO.getUserID(),fixedCatStr,0);
					return linkedHashMap;
				}// fixed level hierarchy check
				else if(fixedLevelParent)
				{
					// load all the users form the system within the sender domain, 
					// which are in the direct child of the users of fixedCatStr categories
					//  p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
					 // by parentID, if value of this parameter is 2 then check will be done by ownerID
					 //	other wise no check will be required. So here as controll level is DOMAIN OR DOMAINTYPE
					 // pass value 0 for this parameter and null for the p_parentUserID since here no parent and 
					 //	no owner exist for the DOMAIN OR DOMAINTYPE level.					
					linkedHashMap = loadUsersForParentFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,null,p_userName,p_channelUserVO.getUserID(),fixedCatStr,0);
					return linkedHashMap;
				}// fixed level parent check
			}// domain level control check
			else if(PretupsI.CHANNEL_TRANSFER_LEVEL_OWNER.equals(ctrlLevel))
			{
				if(BTSLUtil.isNullString(fixedCatStr))
				{
					// load all the users form the system within the sender'owner hierarchy
					//without any check of the fixed category
					if(directAllowed)
					{
						//  load all the users form the system within the sender'owner hierarchy
						// which are direct child of the owner so here in this method calling we are sending sender's
						// ownerID to considered as the parentID in the method
						linkedHashMap = loadUsersByParentID(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getOwnerID(),p_userName,p_channelUserVO.getUserID());
					}// direct transfer check
					if(chnlByPassAllowed)
					{
						//load all the users form the system within the sender'owner hierarchy
						// which are not direct child of the owner so here in this method calling we are sending sender's
						// ownerID to considered as the parentID in the method
						//linkedHashMap.addAll(loadUserForChannelByPass(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getOwnerID(),p_userName,p_channelUserVO.getUserID()));
						linkedHashMap=loadUserForChannelByPass(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getOwnerID(),p_userName,p_channelUserVO.getUserID());
					}// channel by pass check
					return linkedHashMap;
				}// fixed category null check
				else if(fixedLevelHierarchy)
				{
					// load all the users form the system within the sender's owner hierarchy 
					// which are in the hierarchy of the users of fixedCatStr categories
					// p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
					// by parentID, if value of this parameter is 2 then check will be done by ownerID
					// other wise no check will be required. So here as controll level is OWNER
					// pass value 2 for this parameter and OWNERID for the p_parentUserID since here list is to be  
					// loaded by owner.
					linkedHashMap = loadUsersForHierarchyFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getOwnerID(),p_userName,p_channelUserVO.getUserID(),fixedCatStr,2);
					return linkedHashMap;				}// fixed level hierarchy check
				else if(fixedLevelParent)
				{
					// load all the users form the system within the sender's owner hierarchy 
					// which are in the direct child of the users of fixedCatStr categories
					// p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
					// by parentID, if value of this parameter is 2 then check will be done by ownerID
					// other wise no check will be required. So here as controll level is OWNER
					// pass value 2 for this parameter and OWNERID for the p_parentUserID since here list is to be  
					// loaded by owner.
					linkedHashMap = loadUsersForParentFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getOwnerID(),p_userName,p_channelUserVO.getUserID(),fixedCatStr,2);
					return linkedHashMap;
				}// fixed level parent check
			}// owner level control check
			else if(PretupsI.CHANNEL_TRANSFER_LEVEL_PARENT.equals(ctrlLevel))
			{
				if(BTSLUtil.isNullString(fixedCatStr))
				{
					// load all the users form the system within the sender's parent hierarchy 
					//without any check of the fixed category
					if(directAllowed)
					{
						//  load all the users form the system within the sender's parent hierarchy
						// which are direct child of the parent
						linkedHashMap = loadUsersByParentID(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getParentID(),p_userName,p_channelUserVO.getUserID());
					}// direct transfer check
					if(chnlByPassAllowed)
					{
						//load all the users form the system within the sender's parent hierarchy
						// which are not direct child of the parent
						//linkedHashMap.addAll(loadUserForChannelByPass(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getParentID(),p_userName,p_channelUserVO.getUserID()));
						linkedHashMap=loadUserForChannelByPass(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getParentID(),p_userName,p_channelUserVO.getUserID());
					}// channel by pass check
					return linkedHashMap;
				}// fixed category null check
				else if(fixedLevelHierarchy)
				{
					// load all the users form the system within the sender's parent hierarchy, 
					// which are in the hierarchy of the users of fixedCatStr categories
					// p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
					// by parentID, if value of this parameter is 2 then check will be done by ownerID
					// other wise no check will be required. So here as controll level is PARENT
					// pass value 1 for this parameter and PARENTID for the p_parentUserID since here list is to be  
					// loaded by parent.					
					linkedHashMap = loadUsersForHierarchyFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getParentID(),p_userName,p_channelUserVO.getUserID(),fixedCatStr,1);
					return linkedHashMap;
				}// fixed level hierarchy check
				else if(fixedLevelParent)
				{
					// load all the users form the system within the sender's parent hierarchy, 
					// which are in the direct child of the users of fixedCatStr categories
					// p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
					// by parentID, if value of this parameter is 2 then check will be done by ownerID
					// other wise no check will be required. So here as controll level is PARENT
					// pass value 1 for this parameter and PARENTID for the p_parentUserID since here list is to be  
					// loaded by parent.					
					linkedHashMap = loadUsersForParentFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getParentID(),p_userName,p_channelUserVO.getUserID(),fixedCatStr,1);
					return linkedHashMap;
				}// fixed level parent check
			}// parent level control check
			else if(PretupsI.CHANNEL_TRANSFER_LEVEL_SELF.equals(ctrlLevel))
			{
				if(BTSLUtil.isNullString(fixedCatStr))
				{
					// load all the users form the system within the sender hierarchy 
					//without any check of the fixed category
					if(directAllowed)
					{
						//  load all the users form the system within the sender's  hierarchy
						// which are direct child of the sender so here in this method calling we are sending sender's
						// userID to considered as the parentID in the method
						linkedHashMap = loadUsersByParentID(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getUserID(),p_userName,p_channelUserVO.getUserID());
					}// direct transfer check
					if(chnlByPassAllowed)
					{
						//load all the users form the system within the sender's hierarchy
						// which are not direct child of the sender so here in this method calling we are sending sender's
						// userID to considered as the parentID in the method
						
						//linkedHashMap.addAll(loadUserForChannelByPass(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getUserID(),p_userName,p_channelUserVO.getUserID()));
						linkedHashMap=loadUserForChannelByPass(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getUserID(),p_userName,p_channelUserVO.getUserID());
						
					}// channel by pass check
					return linkedHashMap;
				}// fixed category null check
				else if(fixedLevelHierarchy)
				{
					// load all the users form the system within the sender hierarchy, 
					// which are in the hierarchy of the users of fixedCatStr categories
					// p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
					// by parentID, if value of this parameter is 2 then check will be done by ownerID
					// other wise no check will be required. So here as controll level is SELF but sender user
					// have to be considered as the parent of all the requested users so 
					// pass value 1 for this parameter and sener's userID for the p_parentUserID since here list is to be  
					// loaded by senderID.
					linkedHashMap = loadUsersForHierarchyFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getUserID(),p_userName,p_channelUserVO.getUserID(),fixedCatStr,1);
					return linkedHashMap;
				}// fixed level hierarchy check
				else if(fixedLevelParent)
				{
					// load all the users form the system within the sender hierarchy, 
					// which are in the direct child of the users of fixedCatStr categories
					// p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
					// by parentID, if value of this parameter is 2 then check will be done by ownerID
					// other wise no check will be required. So here as controll level is SELF but sender user
					// have to be considered as the parent of all the requested users so 
					// pass value 1 for this parameter and sener's userID for the p_parentUserID since here list is to be  
					// loaded by senderID.
					linkedHashMap = loadUsersForParentFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getUserID(),p_userName,p_channelUserVO.getUserID(),fixedCatStr,1);
					return linkedHashMap;
				}// fixed level parent check
			}// Self level control check
		}// control transaction check
		if(_log.isDebugEnabled())
			_log.debug(methodName,"Exited userList.size() = "+linkedHashMap.size());
		return linkedHashMap;
	
	}
	
	/**
	 * Method getCategoryStrValue.
	 * This method evaluvate entered string and parse it in the form that value can be passed in the database
	 * query for IN condition as it convert a,b to 'a','b' format.
	 * @param p_catString String
	 * @return String
	 */
	private String getCategoryStrValue(String p_catString)
	{
		final String methodName = "getCategoryStrValue";
		if(_log.isDebugEnabled())
			_log.debug(methodName,"Entered p_catString = "+p_catString);

		StringBuffer fixedCatStrBuf=new StringBuffer();
		String tempArr[] = p_catString.split(",");
		for(int i=0;i<tempArr.length;i++)
		{
			fixedCatStrBuf.append("'");
			fixedCatStrBuf.append(tempArr[i]);
			fixedCatStrBuf.append("',");
		}
		String fixedCatStr=fixedCatStrBuf.substring(0,fixedCatStrBuf.length()-1);
		if(_log.isDebugEnabled())
			_log.debug(methodName,"Exited fixedCatStr= "+fixedCatStr);
		return fixedCatStr;
	}
	
	/**
     * This method loads all users of the specific category. In the case of outSide hierarchy all users have 
     * to be loaded.
     * @param p_con
     * @param p_networkCode
     * @param p_toCategoryCode
     * @param p_userName
     * @param p_userID String
     * @return ArrayList
     * @throws BTSLBaseException
     * @author sandeep.goel
     */
    public LinkedHashMap loadUsersOutsideHireacrhy(Connection p_con,String p_networkCode,String p_toCategoryCode,String p_userName,String p_userID) throws BTSLBaseException
    {
    	final String methodName = "loadUsersOutsideHireacrhy";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered  Network Code: "+p_networkCode+" To Category Code: "+p_toCategoryCode+" User Name: "+p_userName+",p_userID="+p_userID);
        //OraclePreparedStatement pstmt = null;//commented for DB2 
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer(" SELECT u.user_id,u.user_name,u.LOGIN_ID,u.msisdn,u.EXTERNAL_CODE, ");
        strBuff.append(" u.CATEGORY_CODE,ub.BALANCE,cat.CATEGORY_NAME,cg.GRADE_CODE, cg.GRADE_NAME,u.STATUS,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend ");
        strBuff.append(", CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
        strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
        strBuff.append("CPS.language_2_message  comprf_lang_2_msg ");
        strBuff.append(" FROM users u,channel_users CU, CHANNEL_GRADES cg ,CATEGORIES cat,USER_BALANCES ub ");
        strBuff.append(", commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
        strBuff.append(" WHERE u.network_code = ? AND u.status = 'Y'  AND u.category_code = ? AND u.user_id != ?");
        strBuff.append(" AND U.user_id=CU.user_id AND U.user_id=ub.user_id AND CU.user_grade=CG.grade_code ");
        strBuff.append(" AND u.user_type='"+PretupsI.CHANNEL_USER_TYPE+"' ");
		// here user_id != ? check is for not to load the sender user in the query for the same level transactions
        strBuff.append(" AND UPPER(u.user_name) LIKE UPPER(?) ");
        strBuff.append(" AND u.CATEGORY_CODE=cat.CATEGORY_CODE AND u.CATEGORY_CODE=cg.CATEGORY_CODE AND cg.STATUS='Y' ");
        strBuff.append(" AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
        strBuff.append(" AND CPSV.applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
        strBuff.append(" WHERE CPSV1.applicable_from <= ? ");
        strBuff.append(" AND CPS.comm_profile_set_id = CPSV1.comm_profile_set_id )");
        strBuff.append(" AND TP.profile_id = CU.transfer_profile_id AND cat.status='Y'");
        strBuff.append(" ORDER BY u.user_name ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        
        try
        {
            //pstmt = (OraclePreparedStatement)  p_con.prepareStatement(sqlSelect);//commented for DB2 
        	 pstmt = (PreparedStatement)  p_con.prepareStatement(sqlSelect);
            int i = 0 ;
            Date currentDate= new Date();
            pstmt.setString(++i,p_networkCode );
            pstmt.setString(++i,p_toCategoryCode );
			pstmt.setString(++i,p_userID);
            //pstmt.setFormOfUse(++i, OraclePreparedStatement.FORM_NCHAR);//commented for DB2 
            pstmt.setString(++i,p_userName );
            pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(currentDate));
            rs = pstmt.executeQuery();              
            while (rs.next())
            {
            	ChannelUserVO channelVO=new ChannelUserVO();
            	channelVO.setUserID(rs.getString("user_id"));
            	channelVO.setUserName(rs.getString("user_name"));
            	channelVO.setLoginID(rs.getString("login_id"));
            	channelVO.setMsisdn(rs.getString("msisdn"));
            	//channelVO.setUserBalance(rs.getString("balance"));
            	channelVO.setUserBalance(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("balance"))));
            	channelVO.setExternalCode(rs.getString("external_code"));
            	channelVO.setCategoryCode(rs.getString("category_code"));
            	channelVO.setCategoryName(rs.getString("category_name"));
            	channelVO.setUserGrade(rs.getString("grade_code"));
            	channelVO.setUserGradeName(rs.getString("grade_name"));
            	channelVO.setStatus(rs.getString("status"));
            	channelVO.setTransferProfileID(rs.getString("transfer_profile_id"));
            	channelVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
            	channelVO.setInSuspend(rs.getString("in_suspend"));
            	channelVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
            	channelVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
            	channelVO.setCommissionProfileApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
            	channelVO.setTransferProfileName(rs.getString("profile_name"));
            	channelVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
            	channelVO.setTransferProfileStatus(rs.getString("profile_status"));
            	channelVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
            	channelVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
            	linkedHashMap.put(channelVO.getUserID(),channelVO);
            }
        } 
		catch (SQLException sqe)
        {
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[loadUsersOutsideHireacrhy]","","","","SQL Exception:"+sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }
		catch (Exception ex)
        {
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[loadUsersOutsideHireacrhy]","","","","Exception:"+ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } 
		finally
        {
            try{if (rs != null){rs.close();}} catch (Exception e){}
            try{if (pstmt != null){pstmt.close();}} catch (Exception e){}
            
            if (_log.isDebugEnabled())
            {
                _log.debug(methodName, "Exiting:  arrayList Size =" + linkedHashMap.size());
            }
        }
        return linkedHashMap;            
    }
    
    /**
	 * Method loadUsersForHierarchyFixedCat
	 * This method loads all the users in the hierarchy of the users of the p_fixedCat categories.
	 * @param p_con
	 * @param p_networkCode
	 * @param p_toCategoryCode
	 * @param p_parentUserID
	 * @param p_userName
	 * @param p_userID
	 * @param p_fixedCat
	 * @param p_ctrlLvl here if value of this parameter is 1 then check will be done by parentID
	 * if value of this parameter is 2 then check will be done by ownerID other wise no check will be required.
	 * @return ArrayList
	 * @throws BTSLBaseException ArrayList
	 * @author sandeep.goel
	 */
	public LinkedHashMap loadUsersForHierarchyFixedCat(Connection p_con,String p_networkCode,String p_toCategoryCode,String p_parentUserID,String p_userName,String p_userID, String p_fixedCat,int p_ctrlLvl) throws BTSLBaseException
    {
		final String methodName = "loadUsersForHierarchyFixedCat";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered  Network Code: "+p_networkCode+", To Category Code: "+p_toCategoryCode+"  p_parentUserID="+p_parentUserID+", User Name: "+p_userName+",p_userID="+p_userID+",p_fixedCat="+p_fixedCat+",p_ctrlLvl="+p_ctrlLvl);
        //OraclePreparedStatement pstmt = null;//commented for DB2 
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        
        StringBuffer strBuff = new StringBuffer(" SELECT u.user_id,u.user_name,u.LOGIN_ID,u.msisdn,u.EXTERNAL_CODE, ");
        strBuff.append(" u.CATEGORY_CODE,ub.BALANCE,cat.CATEGORY_NAME,cg.GRADE_CODE, cg.GRADE_NAME,u.STATUS ");
        strBuff.append(" ,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend" );
        strBuff.append(", CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
        strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
        strBuff.append("CPS.language_2_message  comprf_lang_2_msg ");
        strBuff.append(" FROM users u,channel_users CU,CHANNEL_GRADES cg ,CATEGORIES cat,USER_BALANCES ub ");
        strBuff.append(", commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
        strBuff.append(" WHERE u.network_code = ? AND u.status = 'Y'  AND u.category_code = ? AND u.user_id != ?");
        strBuff.append(" AND U.user_id=CU.user_id AND U.user_id=ub.user_id AND CU.user_grade=CG.grade_code ");
        strBuff.append(" AND UPPER(u.user_name) LIKE UPPER(?) ");
        strBuff.append(" AND u.CATEGORY_CODE=cat.CATEGORY_CODE AND u.CATEGORY_CODE=cg.CATEGORY_CODE AND cg.STATUS='Y' ");
        strBuff.append(" AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
        strBuff.append(" AND CPSV.applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
        strBuff.append(" WHERE CPSV1.applicable_from <= ? ");
        strBuff.append(" AND CPS.comm_profile_set_id = CPSV1.comm_profile_set_id )");
        strBuff.append(" AND TP.profile_id = CU.transfer_profile_id AND cat.status='Y' ");
        // here user_id != ? check is for not to load the sender user in the query for the same level transactions
		strBuff.append("CONNECT BY PRIOR u.user_id=u.parent_id START WITH u.parent_id IN ");
		strBuff.append("(SELECT user_id FROM users WHERE category_code  IN ("+p_fixedCat +")");
		if(p_ctrlLvl>0)
		{
			strBuff.append(" CONNECT BY PRIOR user_id=parent_id START WITH  ");
			if(p_ctrlLvl==1)
				strBuff.append(" parent_id = ? ");
			else if(p_ctrlLvl==2)
				strBuff.append(" owner_id = ? ");
		}
        strBuff.append(" ) ORDER BY u.user_name ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
    
        try
        {
            //pstmt = (OraclePreparedStatement)  p_con.prepareStatement(sqlSelect);//commented for DB2 
        	pstmt = (PreparedStatement)  p_con.prepareStatement(sqlSelect);
            int i = 0 ;
            Date currentDate= new Date();
            pstmt.setString(++i,p_networkCode );
            pstmt.setString(++i,p_toCategoryCode );
			pstmt.setString(++i,p_userID);
            //pstmt.setFormOfUse(++i, OraclePreparedStatement.FORM_NCHAR);//commented for DB2 
            pstmt.setString(++i,p_userName );
            pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(currentDate));
			if(p_ctrlLvl>0)
				pstmt.setString(++i,p_parentUserID);
			
            rs = pstmt.executeQuery();              
            while (rs.next())
            {
            	ChannelUserVO channelVO=new ChannelUserVO();
            	channelVO.setUserID(rs.getString("user_id"));
            	channelVO.setUserName(rs.getString("user_name"));
            	channelVO.setLoginID(rs.getString("login_id"));
            	channelVO.setMsisdn(rs.getString("msisdn"));
            	//channelVO.setUserBalance(rs.getString("balance"));
            	channelVO.setUserBalance(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("balance"))));
            	channelVO.setExternalCode(rs.getString("external_code"));
            	channelVO.setCategoryCode(rs.getString("category_code"));
            	channelVO.setCategoryName(rs.getString("category_name"));
            	channelVO.setUserGrade(rs.getString("grade_code"));
            	channelVO.setUserGradeName(rs.getString("grade_name"));
            	channelVO.setStatus(rs.getString("status"));
            	channelVO.setTransferProfileID(rs.getString("transfer_profile_id"));
            	channelVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
            	channelVO.setInSuspend(rs.getString("in_suspend"));
            	channelVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
            	channelVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
            	channelVO.setCommissionProfileApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
            	channelVO.setTransferProfileName(rs.getString("profile_name"));
            	channelVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
            	channelVO.setTransferProfileStatus(rs.getString("profile_status"));
            	channelVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
            	channelVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
            	linkedHashMap.put(channelVO.getUserID(),channelVO);
            }
            
        } 
		catch (SQLException sqe)
        {
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[loadUsersForHierarchyFixedCat]","","","","SQL Exception:"+sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }
		catch (Exception ex)
        {
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[loadUsersForHierarchyFixedCat]","","","","Exception:"+ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } 
		finally
        {
            try{if (rs != null){rs.close();}} catch (Exception e){}
            try{if (pstmt != null){pstmt.close();}} catch (Exception e){}
            
            if (_log.isDebugEnabled())
            {
                _log.debug(methodName, "Exiting:  arrayList Size =" + linkedHashMap.size());
            }
        }
        return linkedHashMap;            
    }
	
	/**
	 * Method loadUsersForParentFixedCat
	 * This method loads all the users which are the direct child of the users of the p_fixedCat category. 
	 * @param p_con
	 * @param p_networkCode
	 * @param p_parentUserID
	 * @param p_toCategoryCode
	 * @param p_parentUserID
	 * @param p_userName
	 * @param p_userID
	 * @param p_fixedCat
	 * @param p_ctrlLvl here if value of this parameter is 1 then check will be done by parentID
	 * if value of this parameter is 2 then check will be done by ownerID other wise no check will be required.
	 * @return ArrayList
	 * @throws BTSLBaseException ArrayList
	 */
    public LinkedHashMap loadUsersForParentFixedCat(Connection p_con,String p_networkCode,String p_toCategoryCode,String p_parentUserID,String p_userName,String p_userID, String p_fixedCat,int p_ctrlLvl) throws BTSLBaseException
    {
    	final String methodName = "loadUsersForParentFixedCat";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered  Network Code: "+p_networkCode+", To Category Code: "+p_toCategoryCode+"  p_parentUserID="+p_parentUserID+", User Name: "+p_userName+",p_userID="+p_userID+",p_fixedCat="+p_fixedCat+",p_ctrlLvl="+p_ctrlLvl);
        //OraclePreparedStatement pstmt = null;//commented for DB2 
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        
        StringBuffer strBuff = new StringBuffer(" SELECT u.user_id,u.user_name,u.LOGIN_ID,u.msisdn,u.EXTERNAL_CODE, ");
        strBuff.append(" u.CATEGORY_CODE,ub.BALANCE,cat.CATEGORY_NAME,cg.GRADE_CODE, cg.GRADE_NAME,u.STATUS ");
        strBuff.append(" ,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend" );
        strBuff.append(", CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
        strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
        strBuff.append("CPS.language_2_message  comprf_lang_2_msg ");
        strBuff.append(" FROM users u,users pu,channel_users CU,CHANNEL_GRADES cg ,CATEGORIES cat,USER_BALANCES ub ");
        strBuff.append(", commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
        strBuff.append(" WHERE u.network_code = ? AND u.status = 'Y'  AND u.category_code = ? AND u.user_id != ?");
        strBuff.append(" AND U.user_id=CU.user_id AND U.user_id=ub.user_id AND CU.user_grade=CG.grade_code ");
        strBuff.append(" AND u.CATEGORY_CODE=cat.CATEGORY_CODE AND u.CATEGORY_CODE=cg.CATEGORY_CODE AND cg.STATUS='Y' ");
        strBuff.append(" AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
        strBuff.append(" AND CPSV.applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
        strBuff.append(" WHERE CPSV1.applicable_from <= ? ");
        strBuff.append(" AND CPS.comm_profile_set_id = CPSV1.comm_profile_set_id )");
        strBuff.append(" AND TP.profile_id = CU.transfer_profile_id AND cat.status='Y' ");
        // here user_id != ? check is for not to load the sender user in the query for the same level transactions
		strBuff.append("AND u.parent_id=pu.user_id  AND pu.category_code IN ("+p_fixedCat+") ");
		strBuff.append("AND UPPER(u.user_name) LIKE UPPER(?) ");
		if(p_ctrlLvl==1)
		{
			//strBuff.append(" AND ( pu.parent_id = ? OR u.user_id= ? )");
			strBuff.append(" AND pu.parent_id = DECODE(pu.parent_id,'ROOT',pu.parent_id,?) ");
			strBuff.append(" AND u.parent_id = DECODE(pu.parent_id,'ROOT',?,u.parent_id) ");
			// here pu.parent_id = ? check by pu is done since pu.parent_id is the parent of selected user's parent
			// for example POS to POSA and only to POSA which are child of POS, under the hierarchy of POS's parent.
		}
		else if(p_ctrlLvl==2)
		{
			//strBuff.append(" AND ( u.owner_id = ? OR u.user_id= ? ) ");
			strBuff.append(" AND pu.owner_id = ? ");
			// here pu.owner_id = ?  or u.owner_id =? any can be used since owner is same for all.
		}
		strBuff.append("ORDER BY u.user_name ");
        
        
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        try
        {
            //pstmt = (OraclePreparedStatement)  p_con.prepareStatement(sqlSelect);//commented for DB2 
        	 pstmt = (PreparedStatement)  p_con.prepareStatement(sqlSelect);
            int i = 0 ;
            Date currentDate= new Date();
            pstmt.setString(++i,p_networkCode );
            pstmt.setString(++i,p_toCategoryCode );
			pstmt.setString(++i,p_userID);
			pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(currentDate));
            //pstmt.setFormOfUse(++i, OraclePreparedStatement.FORM_NCHAR);//commented for DB2 
            pstmt.setString(++i,p_userName );
			if(p_ctrlLvl==1)
			{
				pstmt.setString(++i,p_parentUserID);
				pstmt.setString(++i,p_parentUserID);
			}
			else if(p_ctrlLvl==2)
			{
				pstmt.setString(++i,p_parentUserID);
			} 
			
            rs = pstmt.executeQuery();              
            while (rs.next())
            {
            	ChannelUserVO channelVO=new ChannelUserVO();
            	channelVO.setUserID(rs.getString("user_id"));
            	channelVO.setUserName(rs.getString("user_name"));
            	channelVO.setLoginID(rs.getString("login_id"));
            	channelVO.setMsisdn(rs.getString("msisdn"));
            	//channelVO.setUserBalance(rs.getString("balance"));
            	channelVO.setUserBalance(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("balance"))));
            	channelVO.setExternalCode(rs.getString("external_code"));
            	channelVO.setCategoryCode(rs.getString("category_code"));
            	channelVO.setCategoryName(rs.getString("category_name"));
            	channelVO.setUserGrade(rs.getString("grade_code"));
            	channelVO.setUserGradeName(rs.getString("grade_name"));
            	channelVO.setStatus(rs.getString("status"));
            	channelVO.setTransferProfileID(rs.getString("transfer_profile_id"));
            	channelVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
            	channelVO.setInSuspend(rs.getString("in_suspend"));
            	channelVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
            	channelVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
            	channelVO.setCommissionProfileApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
            	channelVO.setTransferProfileName(rs.getString("profile_name"));
            	channelVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
            	channelVO.setTransferProfileStatus(rs.getString("profile_status"));
            	channelVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
            	channelVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
            	linkedHashMap.put(channelVO.getUserID(),channelVO);
            }
            
        } 
		catch (SQLException sqe)
        {
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[loadUsersForParentFixedCat]","","","","SQL Exception:"+sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }
		catch (Exception ex)
        {
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[loadUsersForParentFixedCat]","","","","Exception:"+ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } 
		finally
        {
            try{if (rs != null){rs.close();}} catch (Exception e){}
            try{if (pstmt != null){pstmt.close();}} catch (Exception e){}
            
            if (_log.isDebugEnabled())
            {
                _log.debug(methodName, "Exiting:  arrayList Size =" + linkedHashMap.size());
            }
        }
        return linkedHashMap;            
    }
    
    /**
	  * Method loadUsersByOwnerID.
	  * This method loads all the users under the owner user passed as argument.
	  * @author sandeep.goel
	  * @param p_con Connection
	  * @param p_networkCode String
	  * @param p_toCategoryCode String
	  * @param p_ownerID String
	  * @param p_userName String
	  * @param p_userID String
	  * @return ArrayList
	  * @throws BTSLBaseException
	  */
	 public LinkedHashMap loadUsersByOwnerID(Connection p_con,String p_networkCode,String p_toCategoryCode,String p_ownerID,String p_userName,String p_userID) throws BTSLBaseException
	    {
		 	final String methodName = "loadUsersByOwnerID";
	        if (_log.isDebugEnabled())
	            _log.debug(methodName, "Entered  Network Code: "+p_networkCode+" To Category Code: "+p_toCategoryCode+" p_ownerID: "+p_ownerID+"User Name: "+p_userName+"  ,p_userID="+p_userID);
	        //OraclePreparedStatement pstmt = null; //commented for DB2 
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        LinkedHashMap linkedHashMap = new LinkedHashMap();
	        StringBuffer strBuff = new StringBuffer(" SELECT u.user_id,u.user_name,u.LOGIN_ID,u.msisdn,u.EXTERNAL_CODE, ");
	        strBuff.append(" u.CATEGORY_CODE,ub.BALANCE,cat.CATEGORY_NAME,cg.GRADE_CODE, cg.GRADE_NAME,u.STATUS ");
	        strBuff.append(" ,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend" );
	        strBuff.append(", CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
	        strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
	        strBuff.append("CPS.language_2_message  comprf_lang_2_msg ");
	        strBuff.append(" FROM users u,channel_users CU,CHANNEL_GRADES cg ,CATEGORIES cat,USER_BALANCES ub ");
	        strBuff.append(", commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
	        strBuff.append(" WHERE u.network_code = ? AND u.status = 'Y'  AND u.category_code = ? AND u.user_id != ?");
	        strBuff.append(" AND U.user_id=CU.user_id AND U.user_id=ub.user_id AND CU.user_grade=CG.grade_code ");
	        strBuff.append(" AND u.CATEGORY_CODE=cat.CATEGORY_CODE AND u.CATEGORY_CODE=cg.CATEGORY_CODE AND cg.STATUS='Y' ");
            strBuff.append(" AND user_type='"+PretupsI.CHANNEL_USER_TYPE+"' ");
            strBuff.append(" AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
            strBuff.append(" AND CPSV.applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
            strBuff.append(" WHERE CPSV1.applicable_from <= ? ");
            strBuff.append(" AND CPS.comm_profile_set_id = CPSV1.comm_profile_set_id )");
            strBuff.append(" AND TP.profile_id = CU.transfer_profile_id AND cat.status='Y' ");
            // here user_id != ? check is for not to load the sender user in the query for the same level transactions
			strBuff.append(" AND UPPER(u.user_name) LIKE UPPER(?) AND ( u.owner_id = ? OR u.user_id = ? ) ORDER BY u.user_name ");
			// here owner_id = ? OR user_id = ?  check is to load the owner also if transaction is to owner only.
			String sqlSelect=strBuff.toString();
			if (_log.isDebugEnabled())
	            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
	        
	        try
	        {
	            //pstmt = (OraclePreparedStatement)  p_con.prepareStatement(sqlSelect);//commented for DB2
	            pstmt = (PreparedStatement)  p_con.prepareStatement(sqlSelect);
	            int i = 0 ;
	            Date currentDate= new Date();
	            pstmt.setString(++i,p_networkCode );
	            pstmt.setString(++i,p_toCategoryCode );				
				pstmt.setString(++i,p_userID );
				pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(currentDate));
	            //pstmt.setFormOfUse(++i, OraclePreparedStatement.FORM_NCHAR);//commented for DB2
	            pstmt.setString(++i,p_userName );
				pstmt.setString(++i,p_ownerID );
				pstmt.setString(++i,p_ownerID );
	            rs = pstmt.executeQuery();				
	            while (rs.next())
	            {
	            	ChannelUserVO channelVO=new ChannelUserVO();
	            	channelVO.setUserID(rs.getString("user_id"));
	            	channelVO.setUserName(rs.getString("user_name"));
	            	channelVO.setLoginID(rs.getString("login_id"));
	            	channelVO.setMsisdn(rs.getString("msisdn"));
	            	//channelVO.setUserBalance(rs.getString("balance"));
	            	channelVO.setUserBalance(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("balance"))));
	            	channelVO.setExternalCode(rs.getString("external_code"));
	            	channelVO.setCategoryCode(rs.getString("category_code"));
	            	channelVO.setCategoryName(rs.getString("category_name"));
	            	channelVO.setUserGrade(rs.getString("grade_code"));
	            	channelVO.setUserGradeName(rs.getString("grade_name"));
	            	channelVO.setStatus(rs.getString("status"));
	            	channelVO.setTransferProfileID(rs.getString("transfer_profile_id"));
	            	channelVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
	            	channelVO.setInSuspend(rs.getString("in_suspend"));
	            	channelVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
	            	channelVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
	            	channelVO.setCommissionProfileApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
	            	channelVO.setTransferProfileName(rs.getString("profile_name"));
	            	channelVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
	            	channelVO.setTransferProfileStatus(rs.getString("profile_status"));
	            	channelVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
	            	channelVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
	            	linkedHashMap.put(channelVO.getUserID(),channelVO);
	            }
	        }
			catch (SQLException sqe)
	        {
	            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[loadUsersByOwnerID]","","","","SQL Exception:"+sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        }
			catch (Exception ex)
	        {
	            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[loadUsersByOwnerID]","","","","Exception:"+ex.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        }
			finally
	        {
	            try{if (rs != null){rs.close();}} catch (Exception e){}
	            try{if (pstmt != null){pstmt.close();}} catch (Exception e){}
	            if (_log.isDebugEnabled())
	                _log.debug(methodName, "Exiting:  arrayList Size =" + linkedHashMap.size());
	        }
	        return linkedHashMap;
	        
	    }
	 
	 /**
	     * loadUsersByParentIDRecursive
	     * This method loads all the user under the parent user which is passed as argurment.
	     * @author sandeep.goel
	     * @param p_con
	     * @param p_networkCode
	     * @param p_toCategoryCode
	     * @param p_parentID
	     * @param p_userName
	     * @param p_userID String
		 * @return ArrayList
		 * @throws BTSLBaseException
	     */
	    public LinkedHashMap loadUsersByParentIDRecursive(Connection p_con,String p_networkCode,String p_toCategoryCode,String p_parentID,String p_userName,String p_userID) throws BTSLBaseException
	    {
	    	final String methodName = "loadUsersByParentIDRecursive";
	        if (_log.isDebugEnabled())
	            _log.debug(methodName, "Entered  Network Code: "+p_networkCode+" To Category Code: "+p_toCategoryCode+" p_parentID: "+p_parentID+"User Name: "+p_userName+" ,p_userID="+p_userID);
	      //commented for DB2 OraclePreparedStatement pstmt = null;
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        LinkedHashMap linkedHashMap = new LinkedHashMap();
	        StringBuffer strBuff = new StringBuffer(" SELECT u.user_id,u.user_name,u.LOGIN_ID,u.msisdn,u.EXTERNAL_CODE, ");
	        strBuff.append(" u.CATEGORY_CODE,ub.BALANCE,cat.CATEGORY_NAME,cg.GRADE_CODE, cg.GRADE_NAME,u.STATUS ");
	        strBuff.append(" ,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend" );
	        strBuff.append(", CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
	        strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
	        strBuff.append("CPS.language_2_message  comprf_lang_2_msg ");
	        strBuff.append(" FROM users u,users pu,channel_users CU,CHANNEL_GRADES cg ,CATEGORIES cat,USER_BALANCES ub ");
	        strBuff.append(", commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
	        strBuff.append(" WHERE u.network_code = ? AND u.status = 'Y'  AND u.category_code = ? AND u.user_id != ?");
	        strBuff.append(" AND U.user_id=CU.user_id AND U.user_id=ub.user_id AND CU.user_grade=CG.grade_code ");
	        strBuff.append(" AND u.CATEGORY_CODE=cat.CATEGORY_CODE AND u.CATEGORY_CODE=cg.CATEGORY_CODE AND cg.STATUS='Y' ");
	        strBuff.append(" AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
	        strBuff.append(" AND CPSV.applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
	        strBuff.append(" WHERE CPSV1.applicable_from <= ? ");
	        strBuff.append(" AND CPS.comm_profile_set_id = CPSV1.comm_profile_set_id )");
	        strBuff.append(" AND TP.profile_id = CU.transfer_profile_id AND cat.status='Y' ");
	        strBuff.append(" AND u.user_type='"+PretupsI.CHANNEL_USER_TYPE+"' ");
			// here user_id != ? check is for not to load the sender user in the query for the same level transactions
			strBuff.append(" AND UPPER(u.user_name) LIKE UPPER(?) ");
			
			strBuff.append(" CONNECT BY PRIOR u.user_id=u.parent_id START WITH  u.parent_id = ? ");
			strBuff.append(" ORDER BY u.user_name ");
	        String sqlSelect = strBuff.toString();
	        if (_log.isDebugEnabled())
	            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
	        try
	        {
	            //pstmt = (OraclePreparedStatement)  p_con.prepareStatement(sqlSelect);
	        	pstmt = (PreparedStatement)  p_con.prepareStatement(sqlSelect);
	        	int i = 0 ;
	            Date currentDate= new Date();
	            pstmt.setString(++i,p_networkCode );
	            pstmt.setString(++i,p_toCategoryCode );	
				pstmt.setString(++i,p_userID);	
				pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(currentDate));
	            //pstmt.setFormOfUse(++i, OraclePreparedStatement.FORM_NCHAR);//commented for DB2 
	            pstmt.setString(++i,p_userName );
				pstmt.setString(++i,p_parentID );
	            rs = pstmt.executeQuery();				
	            while (rs.next())
	            {
	            	ChannelUserVO channelVO=new ChannelUserVO();
	            	channelVO.setUserID(rs.getString("user_id"));
	            	channelVO.setUserName(rs.getString("user_name"));
	            	channelVO.setLoginID(rs.getString("login_id"));
	            	channelVO.setMsisdn(rs.getString("msisdn"));
	            	//channelVO.setUserBalance(rs.getString("balance"));
	            	channelVO.setUserBalance(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("balance"))));
	            	channelVO.setExternalCode(rs.getString("external_code"));
	            	channelVO.setCategoryCode(rs.getString("category_code"));
	            	channelVO.setCategoryName(rs.getString("category_name"));
	            	channelVO.setUserGrade(rs.getString("grade_code"));
	            	channelVO.setUserGradeName(rs.getString("grade_name"));
	            	channelVO.setStatus(rs.getString("status"));
	            	channelVO.setTransferProfileID(rs.getString("transfer_profile_id"));
	            	channelVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
	            	channelVO.setInSuspend(rs.getString("in_suspend"));
	            	channelVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
	            	channelVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
	            	channelVO.setCommissionProfileApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
	            	channelVO.setTransferProfileName(rs.getString("profile_name"));
	            	channelVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
	            	channelVO.setTransferProfileStatus(rs.getString("profile_status"));
	            	channelVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
	            	channelVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
	            	linkedHashMap.put(channelVO.getUserID(),channelVO);
	            }
	        }
			catch (SQLException sqe)
	        {
	            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[loadUsersByParentIDRecursive]","","","","SQL Exception:"+sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        }
			catch (Exception ex)
	        {
	            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[loadUsersByParentIDRecursive]","","","","Exception:"+ex.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        }
			finally
	        {
	            try{if (rs != null){rs.close();}} catch (Exception e){}
	            try{if (pstmt != null){pstmt.close();}} catch (Exception e){}
	            if (_log.isDebugEnabled())
	                _log.debug(methodName, "Exiting:  arrayList Size =" + linkedHashMap.size());
	        }
	        return linkedHashMap;
	    }
	 
	    /**
		 * method  loadUsersByDomainID
		 * This method load all the users of the specified category which are the direct child of the owner.
		 * This will be called to download users list at domain level and have direct T/R/W allowed
		 * @param p_con
		 * @param p_networkCode
		 * @param p_toCategoryCode
		 * @param p_domainID
		 * @param p_userName
		 * @param p_userID
		 * @return
		 * @throws BTSLBaseException ArrayList
		 */
		public LinkedHashMap loadUsersByDomainID(Connection p_con,String p_networkCode,String p_toCategoryCode,String p_domainID,String p_userName,String p_userId) throws BTSLBaseException
	    {
			final String methodName="loadUsersByDomainID";
	        if (_log.isDebugEnabled())
	            _log.debug(methodName, "Entered  Network Code: "+p_networkCode+" To Category Code: "+p_toCategoryCode+", p_domainID: "+p_domainID+", User Name: "+p_userName+", User ID: "+p_userId);
	        //OraclePreparedStatement pstmt = null;//commented for DB2 
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        StringBuffer strBuff = new StringBuffer(" SELECT u.user_id,u.user_name,u.LOGIN_ID,u.msisdn,u.EXTERNAL_CODE, ");
	        strBuff.append(" u.CATEGORY_CODE,ub.BALANCE,cat.CATEGORY_NAME,cg.GRADE_CODE, cg.GRADE_NAME,u.STATUS ");
	        strBuff.append(" ,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend" );
	        strBuff.append(", CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
	        strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
	        strBuff.append("CPS.language_2_message  comprf_lang_2_msg ");
	        strBuff.append(" FROM users u,channel_users CU,CHANNEL_GRADES cg ,CATEGORIES cat,USER_BALANCES ub ");
	        strBuff.append(", commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
	        strBuff.append(" WHERE u.network_code = ? AND u.status = 'Y'  AND u.category_code = ?");
	        strBuff.append(" AND u.user_id != ?");
	        strBuff.append(" AND U.user_id=CU.user_id AND U.user_id=ub.user_id AND CU.user_grade=CG.grade_code ");
	        strBuff.append(" AND u.CATEGORY_CODE=cat.CATEGORY_CODE AND u.CATEGORY_CODE=cg.CATEGORY_CODE AND cg.STATUS='Y' ");
	        strBuff.append(" AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
	        strBuff.append(" AND CPSV.applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
	        strBuff.append(" WHERE CPSV1.applicable_from <= ? ");
	        strBuff.append(" AND CPS.comm_profile_set_id = CPSV1.comm_profile_set_id )");
	        strBuff.append(" AND TP.profile_id = CU.transfer_profile_id AND cat.status='Y' ");
	        strBuff.append(" AND u.user_type='"+PretupsI.CHANNEL_USER_TYPE+"' ");
			// query is changed to optimization and to remove the problem as owner was not coming in the list.
			// and login user is also coming in the list
			strBuff.append(" AND UPPER(u.user_name) LIKE UPPER(?) AND  (( u.parent_id IN  ( ");
			strBuff.append(" SELECT U.user_id FROM users U, categories C WHERE U.network_code = ? AND U.user_id=U.owner_id  ");
			strBuff.append(" AND U.category_code <> ? AND U.category_code=C.category_code AND C.domain_code= ? ) ) ");
			strBuff.append(" OR (u.parent_id ='ROOT'))ORDER BY u.user_name ");
	        String sqlSelect = strBuff.toString();
	        if (_log.isDebugEnabled())
	            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
	        LinkedHashMap linkedHashMap = new LinkedHashMap();
	        try
	        {
	        	pstmt = (PreparedStatement)  p_con.prepareStatement(sqlSelect);
	        	int i = 0 ;
	            Date currentDate= new Date();
	            pstmt.setString(++i,p_networkCode );
	            pstmt.setString(++i,p_toCategoryCode );
	            pstmt.setString(++i,p_userId );	
				pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(currentDate));
	            pstmt.setString(++i,p_userName );
				pstmt.setString(++i,p_networkCode );
				pstmt.setString(++i,p_toCategoryCode );	
				pstmt.setString(++i,p_domainID );
	            rs = pstmt.executeQuery();				
	            while (rs.next())
	            {
	            	ChannelUserVO channelVO=new ChannelUserVO();
	            	channelVO.setUserID(rs.getString("user_id"));
	            	channelVO.setUserName(rs.getString("user_name"));
	            	channelVO.setLoginID(rs.getString("login_id"));
	            	channelVO.setMsisdn(rs.getString("msisdn"));
	            	//channelVO.setUserBalance(rs.getString("balance"));
	            	channelVO.setUserBalance(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("balance"))));
	            	channelVO.setExternalCode(rs.getString("external_code"));
	            	channelVO.setCategoryCode(rs.getString("category_code"));
	            	channelVO.setCategoryName(rs.getString("category_name"));
	            	channelVO.setUserGrade(rs.getString("grade_code"));
	            	channelVO.setUserGradeName(rs.getString("grade_name"));
	            	channelVO.setStatus(rs.getString("status"));
	            	channelVO.setTransferProfileID(rs.getString("transfer_profile_id"));
	            	channelVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
	            	channelVO.setInSuspend(rs.getString("in_suspend"));
	            	channelVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
	            	channelVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
	            	channelVO.setCommissionProfileApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
	            	channelVO.setTransferProfileName(rs.getString("profile_name"));
	            	channelVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
	            	channelVO.setTransferProfileStatus(rs.getString("profile_status"));
	            	channelVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
	            	channelVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
	            	linkedHashMap.put(channelVO.getUserID(),channelVO);
	            }
	            
	        }
			catch (SQLException sqe)
	        {
	            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[loadUsersByDomainID]","","","","SQL Exception:"+sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        }
			catch (Exception ex)
	        {
	            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[loadUsersByDomainID]","","","","Exception:"+ex.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        }
			finally
	        {
	            try{if (rs != null){rs.close();}} catch (Exception e){}
	            try{if (pstmt != null){pstmt.close();}} catch (Exception e){}
	            if (_log.isDebugEnabled())
	                _log.debug(methodName, "Exiting:  arrayList Size =" + linkedHashMap.size());
	        }
	        return linkedHashMap;
	    }
	    
		/**
		 * method loadUsersChnlBypassByDomainID
		 * This method load all the users of the specified category which are not the direct child of the owner.
		 * This will be called to download users list at domain level and have channel by pass T/R/W allowed
		 * @param p_con
		 * @param p_networkCode
		 * @param p_toCategoryCode
		 * @param p_domainID
		 * @param p_userName
		 * @param p_userID
		 * @return
		 * @throws BTSLBaseException ArrayList
		 */
		public LinkedHashMap loadUsersChnlBypassByDomainID(Connection p_con,String p_networkCode,String p_toCategoryCode,String p_domainID,String p_userName,String p_userID) throws BTSLBaseException
	    {
			final String methodName = "loadUsersChnlBypassByDomainID";
	        if (_log.isDebugEnabled())
	            _log.debug(methodName, "Entered  Network Code: "+p_networkCode+" To Category Code: "+p_toCategoryCode+"p_domainID: "+p_domainID+" User Name: "+p_userName+", p_userID="+p_userID);
	      //commented for DB2
	        //OraclePreparedStatement pstmt = null;
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        StringBuffer strBuff = new StringBuffer(" SELECT u.user_id,u.user_name,u.LOGIN_ID,u.msisdn,u.EXTERNAL_CODE, ");
	        strBuff.append(" u.CATEGORY_CODE,ub.BALANCE,cat.CATEGORY_NAME,cg.GRADE_CODE, cg.GRADE_NAME,u.STATUS ");
	        strBuff.append(" ,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend" );
	        strBuff.append(", CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
	        strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
	        strBuff.append("CPS.language_2_message  comprf_lang_2_msg ");
	        strBuff.append(" FROM users u,channel_users CU,CHANNEL_GRADES cg ,CATEGORIES cat,USER_BALANCES ub ");
	        strBuff.append(", commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
	        strBuff.append(" WHERE u.network_code = ? AND u.status = 'Y'  AND u.category_code = ? AND u.user_id != ?");
	        strBuff.append(" AND U.user_id=CU.user_id AND U.user_id=ub.user_id AND CU.user_grade=CG.grade_code ");
	        strBuff.append(" AND u.CATEGORY_CODE=cat.CATEGORY_CODE AND u.CATEGORY_CODE=cg.CATEGORY_CODE AND cg.STATUS='Y' ");
	        strBuff.append(" AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
	        strBuff.append(" AND CPSV.applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
	        strBuff.append(" WHERE CPSV1.applicable_from <= ? ");
	        strBuff.append(" AND CPS.comm_profile_set_id = CPSV1.comm_profile_set_id )");
	        strBuff.append(" AND TP.profile_id = CU.transfer_profile_id AND cat.status='Y' ");
	        strBuff.append(" AND u.user_type='"+PretupsI.CHANNEL_USER_TYPE+"' ");
			// query is changed to optimization and to remove the problem as owner was not coming in the list.
			// and login user is also coming in the list
			strBuff.append(" AND UPPER(u.user_name) LIKE UPPER(?) AND u.parent_id NOT IN   ( ");
			strBuff.append(" SELECT U.user_id FROM users U, categories C WHERE U.network_code = ? AND U.user_id=U.owner_id  ");
			strBuff.append(" AND U.category_code <> ? AND U.category_code=C.category_code AND C.domain_code= ? ) ");
			strBuff.append(" ORDER BY u.user_name ");
	        String sqlSelect = strBuff.toString();
	        if (_log.isDebugEnabled())
	            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
	        LinkedHashMap linkedHashMap = new LinkedHashMap();
	        try
	        {
	        	//commented for DB2 
	        	//pstmt = (OraclePreparedStatement)  p_con.prepareStatement(sqlSelect);
	        	pstmt = (PreparedStatement)  p_con.prepareStatement(sqlSelect);
	            int i = 0 ;
	            Date currentDate= new Date();
	            pstmt.setString(++i,p_networkCode );
	            pstmt.setString(++i,p_toCategoryCode );	
				pstmt.setString(++i,p_userID );
				pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(currentDate));
	            //pstmt.setFormOfUse(++i, OraclePreparedStatement.FORM_NCHAR); commented for DB2
	            pstmt.setString(++i,p_userName );
				pstmt.setString(++i,p_networkCode );
	            pstmt.setString(++i,p_toCategoryCode );	
				pstmt.setString(++i,p_domainID );
	            rs = pstmt.executeQuery();				
	            while (rs.next())
	            {
	            	ChannelUserVO channelVO=new ChannelUserVO();
	            	channelVO.setUserID(rs.getString("user_id"));
	            	channelVO.setUserName(rs.getString("user_name"));
	            	channelVO.setLoginID(rs.getString("login_id"));
	            	channelVO.setMsisdn(rs.getString("msisdn"));
	            	//channelVO.setUserBalance(rs.getString("balance"));
	            	channelVO.setUserBalance(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("balance"))));
	            	channelVO.setExternalCode(rs.getString("external_code"));
	            	channelVO.setCategoryCode(rs.getString("category_code"));
	            	channelVO.setCategoryName(rs.getString("category_name"));
	            	channelVO.setUserGrade(rs.getString("grade_code"));
	            	channelVO.setUserGradeName(rs.getString("grade_name"));
	            	channelVO.setStatus(rs.getString("status"));
	            	channelVO.setTransferProfileID(rs.getString("transfer_profile_id"));
	            	channelVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
	            	channelVO.setInSuspend(rs.getString("in_suspend"));
	            	channelVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
	            	channelVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
	            	channelVO.setCommissionProfileApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
	            	channelVO.setTransferProfileName(rs.getString("profile_name"));
	            	channelVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
	            	channelVO.setTransferProfileStatus(rs.getString("profile_status"));
	            	channelVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
	            	channelVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
	            	linkedHashMap.put(channelVO.getUserID(),channelVO);
	            }
	            
	        }
			catch (SQLException sqe)
	        {
	            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[loadUsersChnlBypassByDomainID]","","","","SQL Exception:"+sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        }
			catch (Exception ex)
	        {
	            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[loadUsersChnlBypassByDomainID]","","","","Exception:"+ex.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        }
			finally
	        {
	            try{if (rs != null){rs.close();}} catch (Exception e){}
	            try{if (pstmt != null){pstmt.close();}} catch (Exception e){}
	            if (_log.isDebugEnabled())
	                _log.debug(methodName, "Exiting:  arrayList Size =" + linkedHashMap.size());
	        }
	        return linkedHashMap;
	    }
		
		/**
	     * loadUsersByParentID
	     * This method loads all the users which are the direct child of the parent users which is passed as the argument.
	     * @author sandeep.goel
	     * @param p_con
	     * @param p_networkCode
	     * @param p_toCategoryCode
	     * @param p_parentID
	     * @param p_userName
	     * @param p_userID String
		 * @return ArrayList
		 * @throws BTSLBaseException
	     */
	    public LinkedHashMap loadUsersByParentID(Connection p_con,String p_networkCode,String p_toCategoryCode,String p_parentID,String p_userName,String p_userID) throws BTSLBaseException
	    {
	    	final String methodName = "loadUsersByParentID";
	        if (_log.isDebugEnabled())
	            _log.debug(methodName, "Entered  Network Code: "+p_networkCode+" To Category Code: "+p_toCategoryCode+"p_parentID: "+p_parentID+" User Name: "+p_userName+" ,p_userID="+p_userID);
	        // OraclePreparedStatement pstmt = null;//commented for DB2
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        StringBuffer strBuff = new StringBuffer(" SELECT u.user_id,u.user_name,u.LOGIN_ID,u.msisdn,u.EXTERNAL_CODE, ");
	        strBuff.append(" u.CATEGORY_CODE,ub.BALANCE,cat.CATEGORY_NAME,cg.GRADE_CODE, cg.GRADE_NAME,u.STATUS ");
	        strBuff.append(" ,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend" );
	        strBuff.append(", CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
	        strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
	        strBuff.append("CPS.language_2_message  comprf_lang_2_msg ");
	        strBuff.append(" FROM users u,channel_users CU,CHANNEL_GRADES cg ,CATEGORIES cat,USER_BALANCES ub ");
	        strBuff.append(", commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
	        strBuff.append(" WHERE u.network_code = ? AND u.status = 'Y'  AND u.category_code = ? AND u.user_id != ?");
	        strBuff.append(" AND U.user_id=CU.user_id AND U.user_id=ub.user_id AND CU.user_grade=CG.grade_code ");
	        strBuff.append(" AND u.CATEGORY_CODE=cat.CATEGORY_CODE AND u.CATEGORY_CODE=cg.CATEGORY_CODE AND cg.STATUS='Y' ");
	        strBuff.append(" AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
	        strBuff.append(" AND CPSV.applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
	        strBuff.append(" WHERE CPSV1.applicable_from <= ? ");
	        strBuff.append(" AND CPS.comm_profile_set_id = CPSV1.comm_profile_set_id )");
	        strBuff.append(" AND TP.profile_id = CU.transfer_profile_id AND cat.status='Y' ");
	        strBuff.append(" AND u.user_type='"+PretupsI.CHANNEL_USER_TYPE+"' ");
			// here user_id != ? check is for not to load the sender user in the query for the same level transactions
			strBuff.append(" AND UPPER(u.user_name) LIKE UPPER(?) AND ( u.parent_id = ? OR u.user_id = ?) ORDER BY u.user_name ");
			// here u.parent_id = ? OR u.user_id = ? check is to load the parent also if transaciton is done only to parent
	        String sqlSelect = strBuff.toString();
	        if (_log.isDebugEnabled())
	            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
	        LinkedHashMap linkedHashMap = new LinkedHashMap();
	        try
	        {
	            //pstmt = (OraclePreparedStatement)  p_con.prepareStatement(sqlSelect);//commented for DB2 
	            pstmt = (PreparedStatement)  p_con.prepareStatement(sqlSelect);
	            int i = 0 ;
	            Date currentDate= new Date();
	            pstmt.setString(++i,p_networkCode );
	            pstmt.setString(++i,p_toCategoryCode );	
				pstmt.setString(++i,p_userID);
				pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(currentDate));
	            //pstmt.setFormOfUse(++i, OraclePreparedStatement.FORM_NCHAR);//commented for DB2 
	            pstmt.setString(++i,p_userName );
				pstmt.setString(++i,p_parentID );
				pstmt.setString(++i,p_parentID );
	            rs = pstmt.executeQuery();				
	            while (rs.next())
	            {
	            	ChannelUserVO channelVO=new ChannelUserVO();
	            	channelVO.setUserID(rs.getString("user_id"));
	            	channelVO.setUserName(rs.getString("user_name"));
	            	channelVO.setLoginID(rs.getString("login_id"));
	            	channelVO.setMsisdn(rs.getString("msisdn"));
	            	//channelVO.setUserBalance(rs.getString("balance"));
	            	channelVO.setUserBalance(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("balance"))));
	            	channelVO.setExternalCode(rs.getString("external_code"));
	            	channelVO.setCategoryCode(rs.getString("category_code"));
	            	channelVO.setCategoryName(rs.getString("category_name"));
	            	channelVO.setUserGrade(rs.getString("grade_code"));
	            	channelVO.setUserGradeName(rs.getString("grade_name"));
	            	channelVO.setStatus(rs.getString("status"));
	            	channelVO.setTransferProfileID(rs.getString("transfer_profile_id"));
	            	channelVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
	            	channelVO.setInSuspend(rs.getString("in_suspend"));
	            	channelVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
	            	channelVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
	            	channelVO.setCommissionProfileApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
	            	channelVO.setTransferProfileName(rs.getString("profile_name"));
	            	channelVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
	            	channelVO.setTransferProfileStatus(rs.getString("profile_status"));
	            	channelVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
	            	channelVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
	            	linkedHashMap.put(channelVO.getUserID(),channelVO);
	            }
	            
	        }
			catch (SQLException sqe)
	        {
	            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[loadUsersByParentID]","","","","SQL Exception:"+sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        }
			catch (Exception ex)
	        {
	            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[loadUsersByParentID]","","","","Exception:"+ex.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        }
			finally
	        {
	            try{if (rs != null){rs.close();}} catch (Exception e){}
	            try{if (pstmt != null){pstmt.close();}} catch (Exception e){}
	            if (_log.isDebugEnabled())
	                _log.debug(methodName, "Exiting:  arrayList Size =" + linkedHashMap.size());
	        }
	        return linkedHashMap;
	    }
	    
		
		/**
	     * loadUserForChannelByPass
	     * This method loads all the users under the user which is passed as argument and users which are not direct 
	     * child of that user.
	     * @author sandeep.goel
	     * @param p_con
	     * @param p_networkCode
	     * @param p_toCategoryCode
	     * @param p_parentID
	     * @param p_userName
	     * @param p_userID String
		 * @return ArrayList
		 * @throws BTSLBaseException
	     */
	    
		public LinkedHashMap loadUserForChannelByPass(Connection p_con,String p_networkCode,String p_toCategoryCode,String p_parentID,String p_userName,String p_userID) throws BTSLBaseException
	    {
			final String methodName = "loadUserForChannelByPass";
	        if (_log.isDebugEnabled())
	            _log.debug(methodName, "Entered  Network Code: "+p_networkCode+" To Category Code: "+p_toCategoryCode+"  p_parentID: "+p_parentID+" User Name: "+p_userName+",p_userID="+p_userID);
	        //OraclePreparedStatement pstmt = null;  //commented for DB2
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        StringBuffer strBuff = new StringBuffer(" SELECT u.user_id,u.user_name,u.LOGIN_ID,u.msisdn,u.EXTERNAL_CODE, ");
	        strBuff.append(" u.CATEGORY_CODE,ub.BALANCE,cat.CATEGORY_NAME,cg.GRADE_CODE, cg.GRADE_NAME,u.STATUS ");
	        strBuff.append(" ,CU.transfer_profile_id,CU.comm_profile_set_id,CU.in_suspend" );
	        strBuff.append(", CPSV.applicable_from,CPS.comm_profile_set_name ,CPSV.comm_profile_set_version, TP.profile_name, ");
	        strBuff.append("CPS.status commprofilestatus,TP.status profile_status,CPS.language_1_message comprf_lang_1_msg, ");
	        strBuff.append("CPS.language_2_message  comprf_lang_2_msg ");
	        strBuff.append(" FROM users u,users pu,channel_users CU,CHANNEL_GRADES cg ,CATEGORIES cat,USER_BALANCES ub ");
	        strBuff.append(", commission_profile_set CPS, commission_profile_set_version CPSV,transfer_profile TP ");
	        strBuff.append(" WHERE u.network_code = ? AND u.status = 'Y'  AND u.category_code = ? AND u.user_id != ?");
	        strBuff.append(" AND U.user_id=CU.user_id AND U.user_id=ub.user_id AND CU.user_grade=CG.grade_code ");
	        strBuff.append(" AND u.CATEGORY_CODE=cat.CATEGORY_CODE AND u.CATEGORY_CODE=cg.CATEGORY_CODE AND cg.STATUS='Y' ");
	        strBuff.append(" AND CPS.comm_profile_set_id = CU.comm_profile_set_id AND CPSV.comm_profile_set_id = CPS.comm_profile_set_id ");
	        strBuff.append(" AND CPSV.applicable_from =(SELECT MAX(CPSV1.applicable_from) FROM COMMISSION_PROFILE_SET_VERSION CPSV1");
	        strBuff.append(" WHERE CPSV1.applicable_from <= ? ");
	        strBuff.append(" AND CPS.comm_profile_set_id = CPSV1.comm_profile_set_id )");
	        strBuff.append(" AND TP.profile_id = CU.transfer_profile_id AND cat.status='Y' ");
	        // here user_id != ? check is for not to load the sender user in the query for the same level transactions
			strBuff.append(" AND UPPER(u.user_name) LIKE UPPER(?) AND  u.parent_id<>?");
			strBuff.append(" AND u.user_type='"+PretupsI.CHANNEL_USER_TYPE+"' ");
			strBuff.append(" CONNECT BY PRIOR u.user_id=u.parent_id START WITH  u.parent_id=? ");
			strBuff.append(" ORDER BY u.user_name ");
	        String sqlSelect = strBuff.toString();
	        if (_log.isDebugEnabled())
	            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
	        LinkedHashMap linkedHashMap = new LinkedHashMap();
	        try
	        {
	            //pstmt = (OraclePreparedStatement)  p_con.prepareStatement(sqlSelect);//commented for DB2 
	            pstmt = (PreparedStatement)  p_con.prepareStatement(sqlSelect);
	            int i = 0 ;
	            Date currentDate= new Date();
	            pstmt.setString(++i,p_networkCode );
	            pstmt.setString(++i,p_toCategoryCode );	
				pstmt.setString(++i,p_userID);
				pstmt.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(currentDate));
	            //pstmt.setFormOfUse(++i, OraclePreparedStatement.FORM_NCHAR);//commented for DB2 
	            pstmt.setString(++i,p_userName );
				pstmt.setString(++i,p_parentID );
				pstmt.setString(++i,p_parentID );	
				rs = pstmt.executeQuery();				
	            while (rs.next())
	            {
	            	ChannelUserVO channelVO=new ChannelUserVO();
	            	channelVO.setUserID(rs.getString("user_id"));
	            	channelVO.setUserName(rs.getString("user_name"));
	            	channelVO.setLoginID(rs.getString("login_id"));
	            	channelVO.setMsisdn(rs.getString("msisdn"));
	            	//channelVO.setUserBalance(rs.getString("balance"));
	            	channelVO.setUserBalance(PretupsBL.getDisplayAmount(Long.parseLong(rs.getString("balance"))));
	            	channelVO.setExternalCode(rs.getString("external_code"));
	            	channelVO.setCategoryCode(rs.getString("category_code"));
	            	channelVO.setCategoryName(rs.getString("category_name"));
	            	channelVO.setUserGrade(rs.getString("grade_code"));
	            	channelVO.setUserGradeName(rs.getString("grade_name"));
	            	channelVO.setStatus(rs.getString("status"));
	            	channelVO.setTransferProfileID(rs.getString("transfer_profile_id"));
	            	channelVO.setCommissionProfileSetID(rs.getString("comm_profile_set_id"));
	            	channelVO.setInSuspend(rs.getString("in_suspend"));
	            	channelVO.setCommissionProfileSetName(rs.getString("comm_profile_set_name"));
	            	channelVO.setCommissionProfileSetVersion(rs.getString("comm_profile_set_version"));
	            	channelVO.setCommissionProfileApplicableFrom(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")));
	            	channelVO.setTransferProfileName(rs.getString("profile_name"));
	            	channelVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
	            	channelVO.setTransferProfileStatus(rs.getString("profile_status"));
	            	channelVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
	            	channelVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
	            	linkedHashMap.put(channelVO.getUserID(),channelVO);
	            }
	        }
			catch (SQLException sqe)
	        {
	            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[loadUserForChannelByPass]","","","","SQL Exception:"+sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        }
			catch (Exception ex)
	        {
	            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserDAO[loadUserForChannelByPass]","","","","Exception:"+ex.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        }
			finally
	        {
	            try{if (rs != null){rs.close();}} catch (Exception e){}
	            try{if (pstmt != null){pstmt.close();}} catch (Exception e){}
	            if (_log.isDebugEnabled())
	                _log.debug(methodName, "Exiting:  arrayList Size =" + linkedHashMap.size());
	        }
	        return linkedHashMap;
	    }
	
		/**
		 * Method to close the c2c Batch Transfer/Withdraw. This also perform all the data validation.
		 * Also construct error list
		 * Tables updated are: c2c_batches,c2c_batch_items
		 * user_balances,user_daily_balances,user_transfer_counts,c2c_batch_items,c2c_batches,
		 * channel_transfers_items,channel_transfers
		 * 
		 * @param p_con
		 * @param p_dataMap
		 * @param p_senderVO
		 * @param p_batchItemsList
		 * @param p_batchMasterVO
		 * @param p_messages
		 * @param p_locale
		 * @return
		 * @throws BTSLBaseException
		 */
		
		
		
		public ArrayList closeBatchC2CTransfer(Connection p_con,HashMap<C2CBatchMasterVO, ArrayList<C2CBatchItemsVO>> batchMasterItemsListMap, MessageResources p_messages,Locale p_locale,String p_sms_default_lang ,String p_sms_second_lang)throws BTSLBaseException
		{
			final String methodName = "closeBatchC2CTransfer"; 
			if (_log.isDebugEnabled()) 	_log.debug(methodName, "Entered.... batchMasterItemsListMap="+batchMasterItemsListMap+ "p_locale="+ p_locale);
			
			
			C2CBatchMasterVO p_batchMasterVO = null;
			ArrayList p_batchItemsList = null;
			ChannelUserVO p_senderVO = null;
			PreparedStatement pstmtLoadUser = null;
			PreparedStatement pstmtSelectUserBalances=null;
			PreparedStatement pstmtUpdateUserBalances=null;
			PreparedStatement pstmtUpdateSenderBalanceOn=null;
			
			PreparedStatement pstmtInsertUserDailyBalances=null;
			
			PreparedStatement pstmtSelectSenderBalance=null;
			PreparedStatement pstmtUpdateSenderBalance=null;
			PreparedStatement pstmtInsertSenderDailyBalances=null;
			
			PreparedStatement pstmtSelectBalance=null;
			
			PreparedStatement pstmtUpdateBalance=null;
			PreparedStatement pstmtInsertBalance=null;
			PreparedStatement pstmtSelectTransferCounts=null;
			
			PreparedStatement pstmtSelectSenderTransferCounts=null;
			PreparedStatement pstmtSelectProfileCounts=null;
			PreparedStatement pstmtUpdateTransferCounts=null;
			PreparedStatement pstmtSelectSenderProfileOutCounts=null;
			PreparedStatement pstmtUpdateSenderTransferCounts=null;
			PreparedStatement pstmtInsertTransferCounts=null;
			PreparedStatement pstmtInsertSenderTransferCounts=null;
			
			PreparedStatement pstmtLoadTransferProfileProduct=null;
			PreparedStatement handlerStmt = null;
			
			PreparedStatement pstmtInsertIntoChannelTransferItems=null;
			//OraclePreparedStatement pstmtInsertIntoChannelTranfers=null;//commented for DB2
			PreparedStatement pstmtInsertIntoChannelTranfers=null;
			PreparedStatement pstmtSelectBalanceInfoForMessage=null;


			ResultSet rs = null;
			ArrayList errorList = new ArrayList();
			ListValueVO errorVO=null;
			ArrayList userbalanceList=null;
			UserBalancesVO balancesVO = null;
			KeyArgumentVO keyArgumentVO=null;
			String[] argsArr=null;
			ArrayList txnSmsMessageList=null;
			ArrayList balSmsMessageList=null;
			Locale locale=null;
			String[] array=null;
			BTSLMessages messages=null;
			PushMessage pushMessage=null;
			String language=null;
			String country=null;
			String c2cTransferID=null;
			int updateCount=0;
			//added by vikram
			long senderPreviousBal=-1;			//taking sender previous balance as 0
			
			// for loading the C2C transfer rule for C2C transfer
			PreparedStatement pstmtSelectTrfRule = null;
			ResultSet rsSelectTrfRule=null;
			PreparedStatement psmtInsertUserThreshold=null;
			//added by vikram
	        long thresholdValue=-1;
			
			StringBuffer strBuffSelectTrfRule = new StringBuffer(" SELECT transfer_rule_id,transfer_type, transfer_allowed ");
			strBuffSelectTrfRule.append("FROM chnl_transfer_rules WHERE network_code = ? AND domain_code = ?  ");
			strBuffSelectTrfRule.append("AND to_category = ? AND status = 'Y' AND type = 'CHANNEL' ");
			if (_log.isDebugEnabled()) 	_log.debug(methodName, "strBuffSelectTrfRule Query ="+strBuffSelectTrfRule);
			// ends here
			
			// for loading the products associated with the transfer rule
			PreparedStatement pstmtSelectTrfRuleProd = null;
			ResultSet rsSelectTrfRuleProd=null;
			StringBuffer strBuffSelectTrfRuleProd = new StringBuffer("SELECT 1 FROM chnl_transfer_rules_products ");
			strBuffSelectTrfRuleProd.append("WHERE transfer_rule_id=?  AND product_code = ? ");
			if (_log.isDebugEnabled()) 	_log.debug(methodName, "strBuffSelectTrfRuleProd Query ="+strBuffSelectTrfRuleProd);
			//ends here

			// for loading the products associated with the commission profile
			PreparedStatement pstmtSelectCProfileProd = null;
			ResultSet rsSelectCProfileProd=null;
			StringBuffer strBuffSelectCProfileProd = new StringBuffer("SELECT cp.min_transfer_value,cp.max_transfer_value,cp.discount_type,cp.discount_rate, ");
			strBuffSelectCProfileProd.append("cp.comm_profile_products_id, cp.transfer_multiple_off, cp.taxes_on_foc_applicable,cp.taxes_on_channel_transfer  ");
			strBuffSelectCProfileProd.append("FROM commission_profile_products cp ");
			strBuffSelectCProfileProd.append("WHERE  cp.product_code = ? AND cp.comm_profile_set_id = ? AND cp.comm_profile_set_version = ? ");
			if (_log.isDebugEnabled()) 	_log.debug(methodName, "strBuffSelectCProfileProd Query ="+strBuffSelectCProfileProd);
			
			PreparedStatement pstmtSelectCProfileProdDetail = null;
			ResultSet rsSelectCProfileProdDetail=null;
			StringBuffer strBuffSelectCProfileProdDetail = new StringBuffer("SELECT cpd.tax1_type,cpd.tax1_rate,cpd.tax2_type,cpd.tax2_rate, ");
			strBuffSelectCProfileProdDetail.append("cpd.tax3_type,cpd.tax3_rate,cpd.commission_type,cpd.commission_rate,cpd.comm_profile_detail_id ");
			strBuffSelectCProfileProdDetail.append("FROM commission_profile_details cpd ");
			strBuffSelectCProfileProdDetail.append("WHERE  cpd.comm_profile_products_id = ? AND cpd.start_range <= ? AND cpd.end_range >= ? ");
			if (_log.isDebugEnabled()) 	_log.debug(methodName, "strBuffSelectCProfileProdDetail Query ="+strBuffSelectCProfileProdDetail);
			//ends here

			// for existance of the product in the transfer profile
			PreparedStatement pstmtSelectTProfileProd = null;
			ResultSet rsSelectTProfileProd=null;
			StringBuffer strBuffSelectTProfileProd = new StringBuffer(" SELECT 1 ");
			strBuffSelectTProfileProd.append("FROM transfer_profile_products tpp,transfer_profile tp, transfer_profile catp,transfer_profile_products catpp ");
	        strBuffSelectTProfileProd.append("WHERE tpp.profile_id=? AND tpp.product_code = ? AND tpp.profile_id=tp.profile_id AND catp.profile_id=catpp.profile_id ");
			strBuffSelectTProfileProd.append("AND tpp.product_code=catpp.product_code AND tp.category_code=catp.category_code AND catp.parent_profile_id=? AND catp.status='Y' AND tp.network_code = catp.network_code");
			if (_log.isDebugEnabled()) 	_log.debug(methodName, "strBuffSelectTProfileProd Query ="+strBuffSelectTProfileProd);
			//ends here

			// insert data in the batch master table
			//commented for DB2 
			//OraclePreparedStatement pstmtInsertBatchMaster = null;
			PreparedStatement pstmtInsertBatchMaster = null;
			StringBuffer strBuffInsertBatchMaster = new StringBuffer("INSERT INTO c2c_batches (batch_id, network_code, ");
			strBuffInsertBatchMaster.append("network_code_for, batch_name, status, domain_code, product_code, ");
			strBuffInsertBatchMaster.append("batch_file_name, batch_total_record, batch_date, created_by, created_on, ");
			strBuffInsertBatchMaster.append(" modified_by, modified_on,sms_default_lang,sms_second_lang,user_id,batch_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
			if (_log.isDebugEnabled()) 	_log.debug(methodName, "strBuffInsertBatchMaster Query ="+strBuffInsertBatchMaster);
			//ends here
			
			
			
			// insert data in the c2c batch items table
			//OraclePreparedStatement pstmtInsertBatchItems = null;
			PreparedStatement pstmtInsertBatchItems = null;
			StringBuffer strBuffInsertBatchItems = new StringBuffer("INSERT INTO c2c_batch_items (batch_id, batch_detail_id, ");
			strBuffInsertBatchItems.append("category_code, msisdn, user_id, status, modified_by, modified_on, user_grade_code, ");
			strBuffInsertBatchItems.append("transfer_date, txn_profile, ");
			strBuffInsertBatchItems.append("commission_profile_set_id, commission_profile_ver, commission_profile_detail_id, ");
			strBuffInsertBatchItems.append("commission_type, commission_rate, commission_value, tax1_type, tax1_rate, ");
			strBuffInsertBatchItems.append("tax1_value, tax2_type, tax2_rate, tax2_value, tax3_type, tax3_rate, ");
			strBuffInsertBatchItems.append("tax3_value, requested_quantity, transfer_mrp, initiator_remarks, external_code,rcrd_status,transfer_type,transfer_sub_type,opt_batch_id) "); 
			strBuffInsertBatchItems.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			if (_log.isDebugEnabled()) 	_log.debug(methodName, "strBuffInsertBatchItems Query ="+strBuffInsertBatchItems);
			//ends here
			//update master table with OPEN status
			PreparedStatement pstmtUpdateBatchMaster = null;
			StringBuffer strBuffUpdateBatchMaster = new StringBuffer("UPDATE c2c_batches SET batch_total_record=? , status =? WHERE batch_id=? and opt_batch_id=? ");
			if (_log.isDebugEnabled()) 	_log.debug(methodName, "strBuffUpdateBatchMaster Query ="+strBuffUpdateBatchMaster);
			
			/*The query below will be used to load user datils.
			 * That details is the validated for eg: transfer profile, commission profile, user status etc.
			 */
	        StringBuffer sqlBuffer = new StringBuffer(" SELECT u.status userstatus, cusers.in_suspend, ");
			sqlBuffer.append("cps.status commprofilestatus,tp.status profile_status,cps.language_1_message comprf_lang_1_msg, ");
			sqlBuffer.append("cps.language_2_message comprf_lang_2_msg,up.phone_language,up.country, ug.grph_domain_code ");
	        sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps,transfer_profile tp, user_phones up,user_geographies ug "); 
	        sqlBuffer.append("WHERE u.user_id = ? AND up.user_id=u.user_id AND up.primary_number = 'Y' ");
	        sqlBuffer.append(" AND u.status <> 'N' AND u.status <> 'C' ");
	        sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
	        sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND " );
	        sqlBuffer.append(" tp.profile_id = cusers.transfer_profile_id AND ug.user_id = u.user_id ");
			String sqlLoadUser = sqlBuffer.toString();
			if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY sqlLoadUser=" + sqlLoadUser);
			sqlBuffer=null;
			
			//The query below is used to load the user balance
			//This table will basically used to update the daily_balance_updated_on and also to know how many
			//records are to be inserted in user_daily_balances table
			sqlBuffer=new StringBuffer(" SELECT user_id, network_code, network_code_for, product_code, balance, prev_balance, ");
			sqlBuffer.append("last_transfer_type, last_transfer_no, last_transfer_on, daily_balance_updated_on ");
			sqlBuffer.append("FROM user_balances ");
			//commented for DB2 &DB220120123for update WITH RS//sqlBuffer.append("WHERE user_id = ? AND TRUNC(daily_balance_updated_on)<> TRUNC(?) FOR UPDATE OF balance ");
			if(PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype")))
			sqlBuffer.append("WHERE user_id = ? AND to_timestamp(daily_balance_updated_on)<> to_timestamp(?) FOR UPDATE OF balance WITH RS");
			else
				sqlBuffer.append("WHERE user_id = ? AND to_timestamp(daily_balance_updated_on)<> to_timestamp(?) FOR UPDATE OF balance ");	
			String selectUserBalances = sqlBuffer.toString();
			if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY selectUserBalances=" + selectUserBalances);
			sqlBuffer=null;
			
			//update daily_balance_updated_on with current date for user
			sqlBuffer=new StringBuffer(" UPDATE user_balances SET daily_balance_updated_on = ? ");
			sqlBuffer.append("WHERE user_id = ? ");
			String updateUserBalances = sqlBuffer.toString();
			if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY updateUserBalances=" + updateUserBalances);
			sqlBuffer=null;
			
//			Executed if day difference in last updated date and current date is greater then or equal to 1
			//Insert number of records equal to day difference in last updated date and current date in  user_daily_balances
			sqlBuffer=new StringBuffer(" INSERT INTO user_daily_balances(balance_date, user_id, network_code, ");
			sqlBuffer.append("network_code_for, product_code, balance, prev_balance, last_transfer_type, ");
			sqlBuffer.append("last_transfer_no, last_transfer_on, created_on,creation_type )");
			sqlBuffer.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?) ");
			String insertDailyBalances = sqlBuffer.toString();
			if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY insertUserDailyBalances=" + insertDailyBalances);
			sqlBuffer=null;
			
//			Select the balance of user for the perticuler product and network.
			sqlBuffer=new StringBuffer("  SELECT ");
			sqlBuffer.append(" balance "); 
			sqlBuffer.append(" FROM user_balances ");
			if(PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype")))
			sqlBuffer.append(" WHERE user_id = ? and product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE OF balance WITH RS ");
			else
				sqlBuffer.append(" WHERE user_id = ? and product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE OF balance ");	
			String selectBalance = sqlBuffer.toString();
			if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY selectBalance=" + selectBalance);
			sqlBuffer=null;
			
//			Credit the user balance(If balance found in user_balances)
			sqlBuffer=new StringBuffer(" UPDATE user_balances SET prev_balance = ?, balance = ? , last_transfer_type = ? , "); 
			sqlBuffer.append(" last_transfer_no = ? , last_transfer_on = ? ");
			sqlBuffer.append(" WHERE ");
			sqlBuffer.append(" user_id = ? ");
			sqlBuffer.append(" AND "); 
			sqlBuffer.append(" product_code = ? AND network_code = ? AND network_code_for = ? ");
			String updateBalance = sqlBuffer.toString();
			if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY updateBalance=" + updateBalance);
			sqlBuffer=null;
			
//			Insert the record of balnce for user (If balance not found in user_balances)
			sqlBuffer=new StringBuffer(" INSERT "); 
			sqlBuffer.append(" INTO user_balances ");
			sqlBuffer.append(" ( prev_balance,daily_balance_updated_on , balance, last_transfer_type, last_transfer_no, last_transfer_on , "); 
			sqlBuffer.append(" user_id, product_code , network_code, network_code_for ) ");
			sqlBuffer.append(" VALUES ");
			sqlBuffer.append(" (?,?,?,?,?,?,?,?,?,?) ");			
			String insertBalance = sqlBuffer.toString();
			if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY insertBalance=" + insertBalance);
			sqlBuffer=null;
			
//			Select the running countres of user(to be checked against the effetive profile counters)
			sqlBuffer=new StringBuffer(" SELECT user_id, daily_in_count, daily_in_value, weekly_in_count, weekly_in_value, ");
			sqlBuffer.append(" monthly_in_count, monthly_in_value,daily_out_count, daily_out_value, weekly_out_count, ");
			sqlBuffer.append(" weekly_out_value, monthly_out_count, monthly_out_value, outside_daily_in_count, ");
			sqlBuffer.append(" outside_daily_in_value, outside_weekly_in_count, outside_weekly_in_value, ");
			sqlBuffer.append(" outside_monthly_in_count, outside_monthly_in_value, outside_daily_out_count, ");
			sqlBuffer.append(" outside_daily_out_value, outside_weekly_out_count, outside_weekly_out_value, ");
			sqlBuffer.append(" outside_monthly_out_count, outside_monthly_out_value, daily_subscriber_out_count, ");
			sqlBuffer.append(" daily_subscriber_out_value, weekly_subscriber_out_count, weekly_subscriber_out_value, ");
			sqlBuffer.append(" monthly_subscriber_out_count, monthly_subscriber_out_value,last_transfer_date ");
			sqlBuffer.append(" FROM user_transfer_counts ");
			if(PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype")))
			sqlBuffer.append(" WHERE user_id = ? FOR UPDATE WITH RS");
			else
				sqlBuffer.append(" WHERE user_id = ? FOR UPDATE ");
			String selectTransferCounts = sqlBuffer.toString();
			if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY selectTransferCounts=" + selectTransferCounts);
			sqlBuffer=null;
			
//			Select the effective profile counters of user to be checked with running counters of user
			StringBuffer strBuff=new StringBuffer();
			strBuff.append(" SELECT tp.profile_id,LEAST(tp.daily_transfer_in_count,catp.daily_transfer_in_count) daily_transfer_in_count, "); 
			strBuff.append(" LEAST(tp.daily_transfer_in_value,catp.daily_transfer_in_value) daily_transfer_in_value ,LEAST(tp.weekly_transfer_in_count,catp.weekly_transfer_in_count) weekly_transfer_in_count, ");
			strBuff.append(" LEAST(tp.weekly_transfer_in_value,catp.weekly_transfer_in_value) weekly_transfer_in_value,LEAST(tp.monthly_transfer_in_count,catp.monthly_transfer_in_count) monthly_transfer_in_count, ");
			strBuff.append(" LEAST(tp.monthly_transfer_in_value,catp.monthly_transfer_in_value) monthly_transfer_in_value");
			strBuff.append(" FROM transfer_profile tp,transfer_profile catp ");
			strBuff.append(" WHERE tp.profile_id = ? AND tp.status = ?	AND tp.network_code = ? ");
			strBuff.append(" AND tp.category_code=catp.category_code ");	
			strBuff.append(" AND catp.parent_profile_id=? AND catp.status=?	AND tp.network_code = catp.network_code ");
			String selectProfileInCounts = strBuff.toString();
			if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY selectProfileInCounts=" + selectProfileInCounts);
			sqlBuffer=null;
			
//			Update the user running countres (If record found for user running counters)
			sqlBuffer=new StringBuffer(" UPDATE user_transfer_counts  SET "); 
			sqlBuffer.append(" daily_in_count = ?, daily_in_value = ?, weekly_in_count = ?, weekly_in_value = ?,");
	        sqlBuffer.append(" monthly_in_count = ?, monthly_in_value = ? ,daily_out_count =?, daily_out_value=?, ");
			sqlBuffer.append(" weekly_out_count=?, weekly_out_value =?, monthly_out_count=?, monthly_out_value=?, ");
			sqlBuffer.append(" outside_daily_in_count=?, outside_daily_in_value=?, outside_weekly_in_count=?,");
			sqlBuffer.append(" outside_weekly_in_value=?, outside_monthly_in_count=?, outside_monthly_in_value=?, ");
			sqlBuffer.append(" outside_daily_out_count=?, outside_daily_out_value=?, outside_weekly_out_count=?, ");
			sqlBuffer.append(" outside_weekly_out_value=?, outside_monthly_out_count=?, outside_monthly_out_value=?, ");
			sqlBuffer.append(" daily_subscriber_out_count=?, daily_subscriber_out_value=?, weekly_subscriber_out_count=?, ");
			sqlBuffer.append(" weekly_subscriber_out_value=?, monthly_subscriber_out_count=?, monthly_subscriber_out_value=?, ");
	        sqlBuffer.append(" last_in_time = ? , last_transfer_id=?,last_transfer_date=? "); 
	        sqlBuffer.append(" WHERE user_id = ?  ");			
	        String updateTransferCounts = sqlBuffer.toString();
			if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY updateTransferCounts=" + updateTransferCounts);
			sqlBuffer=null;
			
//			Select the effective profile counters of sender to be checked with running counters of sender added by Gopal
			StringBuffer strBuff1=new StringBuffer();
			strBuff1.append(" SELECT tp.profile_id,LEAST(tp.daily_transfer_out_count,catp.daily_transfer_out_count) daily_transfer_out_count, "); 
			strBuff1.append(" LEAST(tp.daily_transfer_out_value,catp.daily_transfer_out_value) daily_transfer_out_value ,LEAST(tp.weekly_transfer_out_count,catp.weekly_transfer_out_count) weekly_transfer_out_count, ");
			strBuff1.append(" LEAST(tp.weekly_transfer_out_value,catp.weekly_transfer_out_value) weekly_transfer_out_value,LEAST(tp.monthly_transfer_out_count,catp.monthly_transfer_out_count) monthly_transfer_out_count, ");
			strBuff1.append(" LEAST(tp.monthly_transfer_out_value,catp.monthly_transfer_out_value) monthly_transfer_out_value");
			strBuff1.append(" FROM transfer_profile tp,transfer_profile catp ");
			strBuff1.append(" WHERE tp.profile_id = ? AND tp.status = ?	AND tp.network_code = ? ");
			strBuff1.append(" AND tp.category_code=catp.category_code ");	
			strBuff1.append(" AND catp.parent_profile_id=? AND catp.status=?	AND tp.network_code = catp.network_code ");
			String selectProfileOutCounts = strBuff1.toString();
			if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY selectProfileOutCounts=" + selectProfileOutCounts);
			sqlBuffer=null;
			
//			Update the Sender running countres (If record found for user running counters)
			sqlBuffer=new StringBuffer(" UPDATE user_transfer_counts  SET "); 
			sqlBuffer.append(" daily_in_count = ?, daily_in_value = ?, weekly_in_count = ?, weekly_in_value = ?,");
	        sqlBuffer.append(" monthly_in_count = ?, monthly_in_value = ? ,daily_out_count =?, daily_out_value=?, ");
			sqlBuffer.append(" weekly_out_count=?, weekly_out_value =?, monthly_out_count=?, monthly_out_value=?, ");
			sqlBuffer.append(" outside_daily_in_count=?, outside_daily_in_value=?, outside_weekly_in_count=?,");
			sqlBuffer.append(" outside_weekly_in_value=?, outside_monthly_in_count=?, outside_monthly_in_value=?, ");
			sqlBuffer.append(" outside_daily_out_count=?, outside_daily_out_value=?, outside_weekly_out_count=?, ");
			sqlBuffer.append(" outside_weekly_out_value=?, outside_monthly_out_count=?, outside_monthly_out_value=?, ");
			sqlBuffer.append(" daily_subscriber_out_count=?, daily_subscriber_out_value=?, weekly_subscriber_out_count=?, ");
			sqlBuffer.append(" weekly_subscriber_out_value=?, monthly_subscriber_out_count=?, monthly_subscriber_out_value=?, ");
	        sqlBuffer.append(" last_out_time = ? , last_transfer_id=?,last_transfer_date=? "); 
	        sqlBuffer.append(" WHERE user_id = ?  ");			
	        String updateSenderTransferCounts = sqlBuffer.toString();
			if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY updateSenderTransferCounts=" + updateSenderTransferCounts);
			sqlBuffer=null;
			
//			Insert the record in user_transfer_counts (If no record found for user running counters)
	        sqlBuffer=new StringBuffer(" INSERT INTO user_transfer_counts ( ");
	        sqlBuffer.append(" daily_in_count, daily_in_value, weekly_in_count, weekly_in_value, monthly_in_count, ");
	        sqlBuffer.append(" monthly_in_value, last_in_time, last_transfer_id,last_transfer_date,user_id ) ");
	        sqlBuffer.append(" VALUES (?,?,?,?,?,?,?,?,?,?) ");
	        String insertTransferCounts = sqlBuffer.toString();
			if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY insertTransferCounts=" + insertTransferCounts);
			sqlBuffer=null;
			
//			Insert the record in user_transfer_counts for Sender (If no record found for user running counters)
	        sqlBuffer=new StringBuffer(" INSERT INTO user_transfer_counts ( ");
	        sqlBuffer.append(" daily_out_count, daily_out_value, weekly_out_count, weekly_out_value, monthly_out_count, ");
	        sqlBuffer.append(" monthly_out_value, last_out_time, last_transfer_id,last_transfer_date,user_id ) ");
	        sqlBuffer.append(" VALUES (?,?,?,?,?,?,?,?,?,?) ");
	        String insertSenderTransferCounts = sqlBuffer.toString();
			if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY insertSenderTransferCounts=" + insertSenderTransferCounts);
			sqlBuffer=null;
			
//			Select the transfer profile product values(These will be used for checking max balance of user)
	        sqlBuffer = new StringBuffer("SELECT GREATEST(tpp.min_residual_balance,catpp.min_residual_balance) min_residual_balance, ");
	        sqlBuffer.append(" LEAST(tpp.max_balance,catpp.max_balance) max_balance ");
	        sqlBuffer.append(" FROM transfer_profile_products tpp,transfer_profile tp, transfer_profile catp,transfer_profile_products catpp ");
	        sqlBuffer.append(" WHERE tpp.profile_id=? AND tpp.product_code = ? AND tpp.profile_id=tp.profile_id AND catp.profile_id=catpp.profile_id "); 
	        sqlBuffer.append(" AND tpp.product_code=catpp.product_code AND tp.category_code=catp.category_code AND catp.parent_profile_id=? AND catp.status=? AND tp.network_code = catp.network_code	 ");
	        String loadTransferProfileProduct = sqlBuffer.toString();
	        if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY loadTransferProfileProduct=" + loadTransferProfileProduct);
	        sqlBuffer=null;
	        
//	      The query below is used to insert the record in channel transfer items table for the order that is closed
	        sqlBuffer = new StringBuffer(" INSERT INTO channel_transfers_items ");
	        sqlBuffer.append("(approved_quantity, commission_profile_detail_id, commission_rate, commission_type, commission_value, mrp,  ");
	        sqlBuffer.append(" net_payable_amount, payable_amount, product_code, receiver_previous_stock, required_quantity, s_no,  ");
	        sqlBuffer.append(" sender_previous_stock, tax1_rate, tax1_type, tax1_value, tax2_rate, tax2_type, tax2_value, tax3_rate, tax3_type,  ");
	        sqlBuffer.append(" tax3_value, transfer_date, transfer_id, user_unit_price, ");
	        sqlBuffer.append(" sender_debit_quantity, receiver_credit_quantity, sender_post_stock, receiver_post_stock,COMMISION_QUANTITY,oth_commission_type,oth_commission_rate,oth_commission_value )  ");
	        sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)  ");
	        String insertIntoChannelTransferItem = sqlBuffer.toString();
	        if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY insertIntoChannelTransferItem=" + insertIntoChannelTransferItem);
	        sqlBuffer=null;
	        
//	      The query below is used to insert the record in channel transfers table for the order that is cloaed
	        sqlBuffer = new StringBuffer(" INSERT INTO channel_transfers ");
	        sqlBuffer.append(" (cancelled_by, cancelled_on, channel_user_remarks, close_date, commission_profile_set_id, commission_profile_ver, ");
	        sqlBuffer.append(" created_by, created_on, domain_code, ext_txn_date, ext_txn_no, first_approved_by, first_approved_on, ");
	        sqlBuffer.append(" first_approver_limit, first_approver_remarks, batch_date, batch_no, opt_batch_no, from_user_id, grph_domain_code, ");
	        sqlBuffer.append(" modified_by, modified_on, net_payable_amount, network_code, network_code_for, payable_amount, pmt_inst_amount, ");
	        sqlBuffer.append("  product_type, receiver_category_code, receiver_grade_code, ");
	        sqlBuffer.append(" receiver_txn_profile, reference_no, request_gateway_code, request_gateway_type, requested_quantity, second_approved_by, ");
	        sqlBuffer.append(" second_approved_on, second_approver_limit, second_approver_remarks,  ");
	        sqlBuffer.append("  source, status, third_approved_by, third_approved_on, third_approver_remarks, to_user_id,  ");
	        sqlBuffer.append(" total_tax1, total_tax2, total_tax3, transfer_category, transfer_date, transfer_id, transfer_initiated_by, ");
	        sqlBuffer.append(" transfer_mrp, transfer_sub_type, transfer_type, type,sender_category_code,");
			sqlBuffer.append(" control_transfer,to_msisdn,to_domain_code,to_grph_domain_code, sms_default_lang, sms_second_lang,active_user_id, ");
			sqlBuffer.append(" sender_grade_code,sender_txn_profile,msisdn,oth_comm_prf_set_id ) ");
	        sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
	        String insertIntoChannelTransfer = sqlBuffer.toString();
	        if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY insertIntoChannelTransfer=" + insertIntoChannelTransfer);
	        sqlBuffer=null;
	        
//	      The query below is used to get the balance information of user with product.
	        //This information will be send in message to user
	        sqlBuffer = new StringBuffer(" SELECT UB.product_code,UB.balance, ");
	        sqlBuffer.append(" PROD.product_short_code, PROD.short_name ");
	        sqlBuffer.append(" FROM user_balances UB,products PROD ");
	        sqlBuffer.append(" WHERE UB.user_id = ?  AND UB.network_code = ? AND UB.network_code_for = ? AND UB.product_code=PROD.product_code "); 
	        String selectBalanceInfoForMessage = sqlBuffer.toString();
	        if (_log.isDebugEnabled())  _log.debug(methodName, "QUERY selectBalanceInfoForMessage=" + selectBalanceInfoForMessage);
	        sqlBuffer=null;
	        
	        //added by nilesh : added two new columns threshold_type and remark
	        StringBuffer strBuffThresholdInsert = new StringBuffer();
	        strBuffThresholdInsert.append(" INSERT INTO user_threshold_counter ");
	        strBuffThresholdInsert.append(" ( user_id,transfer_id , entry_date, entry_date_time, network_code, product_code , "); 
	        strBuffThresholdInsert.append(" type , transaction_type, record_type, category_code,previous_balance,current_balance, threshold_value, threshold_type, remark ) ");
	        strBuffThresholdInsert.append(" VALUES ");
	        strBuffThresholdInsert.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");   
	        String insertUserThreshold = strBuffThresholdInsert.toString();
	        if (_log.isDebugEnabled())
	        {
	            _log.debug(methodName, "QUERY insertUserThreshold=" + insertUserThreshold);
	        }
			
			//added by vikram
	        
			int totalSuccessRecords=0;
			Date date=null;
			ChannelTransferVO channelTransferVO=null;
			try
			{
				
			
				pstmtSelectTrfRule=p_con.prepareStatement(strBuffSelectTrfRule.toString());
				pstmtSelectTrfRuleProd=p_con.prepareStatement(strBuffSelectTrfRuleProd.toString());
				pstmtSelectCProfileProd=p_con.prepareStatement(strBuffSelectCProfileProd.toString());
				pstmtSelectCProfileProdDetail=p_con.prepareStatement(strBuffSelectCProfileProdDetail.toString());
				pstmtSelectTProfileProd=p_con.prepareStatement(strBuffSelectTProfileProd.toString());
				
				pstmtInsertBatchMaster=(PreparedStatement)p_con.prepareStatement(strBuffInsertBatchMaster.toString());
				pstmtInsertBatchItems=(PreparedStatement)p_con.prepareStatement(strBuffInsertBatchItems.toString());
				pstmtUpdateBatchMaster=p_con.prepareStatement(strBuffUpdateBatchMaster.toString());
				
				pstmtLoadUser = p_con.prepareStatement(sqlLoadUser);
				pstmtSelectUserBalances=p_con.prepareStatement(selectUserBalances);
				pstmtUpdateUserBalances=p_con.prepareStatement(updateUserBalances);
				pstmtSelectSenderBalance=p_con.prepareStatement(selectUserBalances);
				
				pstmtInsertSenderDailyBalances=p_con.prepareStatement(insertDailyBalances);
				pstmtUpdateSenderBalanceOn=p_con.prepareStatement(updateUserBalances);
				pstmtUpdateSenderBalance=p_con.prepareStatement(updateBalance);
				pstmtInsertUserDailyBalances=p_con.prepareStatement(insertDailyBalances);
				pstmtSelectTransferCounts=p_con.prepareStatement(selectTransferCounts);
				
				pstmtSelectBalance=p_con.prepareStatement(selectBalance);
				
				pstmtUpdateBalance=p_con.prepareStatement(updateBalance);
				pstmtInsertBalance=p_con.prepareStatement(insertBalance);
				
				pstmtSelectSenderTransferCounts=p_con.prepareStatement(selectTransferCounts);
				pstmtSelectProfileCounts=p_con.prepareStatement(selectProfileInCounts);
				pstmtUpdateTransferCounts=p_con.prepareStatement(updateTransferCounts);
				pstmtInsertTransferCounts = p_con.prepareStatement(insertTransferCounts);
				pstmtSelectSenderProfileOutCounts=p_con.prepareStatement(selectProfileOutCounts);
				pstmtUpdateSenderTransferCounts=p_con.prepareStatement(updateSenderTransferCounts);
				pstmtInsertSenderTransferCounts=p_con.prepareStatement(insertSenderTransferCounts);
								
				pstmtLoadTransferProfileProduct=p_con.prepareStatement(loadTransferProfileProduct);
				
				pstmtInsertIntoChannelTransferItems=p_con.prepareStatement(insertIntoChannelTransferItem);
				pstmtInsertIntoChannelTranfers=(PreparedStatement)p_con.prepareStatement(insertIntoChannelTransfer);
				pstmtSelectBalanceInfoForMessage=p_con.prepareStatement(selectBalanceInfoForMessage);
				psmtInsertUserThreshold=p_con.prepareStatement(insertUserThreshold);

				ChannelTransferRuleVO rulesVO = null;
				ArrayList channelTransferItemVOList=null;
				int index = 0;
				C2CBatchItemsVO  batchItemsVO = null;
				
				HashMap transferRuleMap = new HashMap();
				HashMap transferRuleNotExistMap = new HashMap();
				HashMap transferRuleProdNotExistMap = new HashMap();
				HashMap transferProfileMap = new HashMap();
				long requestedValue=0;
				long minTrfValue=0;
				long maxTrfValue=0;
				long multipleOf=0;
				ArrayList transferItemsList = null;
				MessageGatewayVO messageGatewayVO=MessageGatewayCache.getObject((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WEB_GATEWAY_CODE)));
				ChannelTransferItemsVO channelTransferItemsVO = null;
				int m=0;
				String network_id=null;
				TransferProfileProductVO transferProfileProductVO=null;
				ChannelUserVO channelUserVO=null;
				TransferProfileVO transferProfileVO=null;
	   			TransferProfileVO senderTfrProfileCheckVO=null;
				date=new Date();
				int dayDifference=0;
				Date dailyBalanceUpdatedOn=null;
				int k=0;
				boolean terminateProcessing=false;
				long maxBalance=0;
				boolean isNotToExecuteQuery = false;
				long balance = -1;
				long senderBalance=-1;
	            long previousUserBalToBeSetChnlTrfItems=-1;
	            long previousSenderBalToBeSetChnlTrfItems=-1;
	            UserTransferCountsVO countsVO = null;
	            UserTransferCountsVO senderCountsVO=null;
	            boolean flag = true;
	         	// insert the master data
				index=0;
				Iterator batchMasterItemsListMapIter = batchMasterItemsListMap.entrySet().iterator();
				while(batchMasterItemsListMapIter.hasNext()){
					Map.Entry mapElement = (Map.Entry)batchMasterItemsListMapIter.next();
					p_batchMasterVO = (C2CBatchMasterVO) mapElement.getKey(); 
					p_senderVO = p_batchMasterVO.getChannelUserVO();
	            	p_batchItemsList = (ArrayList) mapElement.getValue();
					pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getBatchId());
					pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getNetworkCode());
					pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getNetworkCodeFor());
					
					pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getBatchName());
					
					pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getStatus());
					pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getDomainCode());
					pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getProductCode());
					pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getBatchFileName());
					pstmtInsertBatchMaster.setLong(++index,p_batchMasterVO.getBatchTotalRecord());
					pstmtInsertBatchMaster.setTimestamp(++index,BTSLUtil.getTimestampFromUtilDate(p_batchMasterVO.getBatchDate()));
					pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getCreatedBy());
					pstmtInsertBatchMaster.setTimestamp(++index,BTSLUtil.getTimestampFromUtilDate(p_batchMasterVO.getCreatedOn()));
					pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getModifiedBy());
					pstmtInsertBatchMaster.setTimestamp(++index,BTSLUtil.getTimestampFromUtilDate(p_batchMasterVO.getModifiedOn()));
					
					pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getDefaultLang());
					pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getSecondLang());
					pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getUserId());
					pstmtInsertBatchMaster.setString(++index,p_batchMasterVO.getOptBatchId());
					
					int queryExecutionCount = pstmtInsertBatchMaster.executeUpdate();
					if(queryExecutionCount<=0)
					{
					    p_con.rollback();
					    _log.error(methodName,"Unable to insert in the batch master table.");
						BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB Error Unable to insert in the batch master table","queryExecutionCount="+queryExecutionCount);
					    EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[closeBatchC2CTransfer]","","","","Unable to insert in the batch master table.");
					    throw new BTSLBaseException(this, methodName,"error.general.sql.processing");
					}
					//ends here
					
					String msgArr[]=null;
					for(int i=0,j=p_batchItemsList.size();i<j;i++)
					{
						terminateProcessing=false;
						batchItemsVO=(C2CBatchItemsVO) p_batchItemsList.get(i);
						if(_log.isDebugEnabled()){
							_log.debug(methodName,"batchItemsVO.toString()="+batchItemsVO.toString());
						}
						// check the uniqueness of the external txn number
						
	
						// load the product's informaiton.
						if(transferRuleNotExistMap.get(batchItemsVO.getCategoryCode())==null)
						{
							if(transferRuleProdNotExistMap.get(batchItemsVO.getCategoryCode())==null)
							{
								if(transferRuleMap.get(batchItemsVO.getCategoryCode())==null)
								{
									index=0;
									pstmtSelectTrfRule.setString(++index,p_batchMasterVO.getNetworkCode());
									pstmtSelectTrfRule.setString(++index,p_batchMasterVO.getDomainCode());
									pstmtSelectTrfRule.setString(++index,batchItemsVO.getCategoryCode());
									rsSelectTrfRule = pstmtSelectTrfRule.executeQuery();
									pstmtSelectTrfRule.clearParameters();
									if (rsSelectTrfRule.next())
									{
										rulesVO = new ChannelTransferRuleVO();
										rulesVO.setTransferRuleID(rsSelectTrfRule.getString("transfer_rule_id"));
										rulesVO.setTransferType(rsSelectTrfRule.getString("transfer_type"));
										rulesVO.setTransferAllowed(rsSelectTrfRule.getString("transfer_allowed"));
										index=0;
										pstmtSelectTrfRuleProd.setString(++index,rulesVO.getTransferRuleID());
										pstmtSelectTrfRuleProd.setString(++index,p_batchMasterVO.getProductCode());
										rsSelectTrfRuleProd  = pstmtSelectTrfRuleProd.executeQuery();
										pstmtSelectTrfRuleProd.clearParameters();
										if(!rsSelectTrfRuleProd.next())
										{
											transferRuleProdNotExistMap.put(batchItemsVO.getCategoryCode(),batchItemsVO.getCategoryCode());
											//put error log Prodcuct is not in the transfer rule
											errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.prodnotintrfrule"));
											errorList.add(errorVO);
											BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Product is not in the transfer rule","");
											continue;
										}
										transferRuleMap.put(batchItemsVO.getCategoryCode(),rulesVO );
									}
									else
									{
										transferRuleNotExistMap.put(batchItemsVO.getCategoryCode(),batchItemsVO.getCategoryCode());
										// put error log transfer rule not defined
									    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.trfrulenotdefined"));
										errorList.add(errorVO);
										BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Transfer rule not defined","");
										continue;
									}
								}// transfer rule loading
							}// Procuct is not associated with transfer rule not defined check
							else
							{
								//put error log Procuct is not in the transfer rule
							    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.prodnotintrfrule"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Product is not in the transfer rule","");
								continue;
							}
						}// transfer rule not defined check
						else
						{
							// put error log transfer rule not defined
						    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.trfrulenotdefined"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Transfer rule not defined","");
							continue;
						}
						rulesVO=(ChannelTransferRuleVO)transferRuleMap.get(batchItemsVO.getCategoryCode());
						if(PretupsI.NO.equals(rulesVO.getTransferAllowed()))
			            {
							//put error according to the transfer rule C2C transfer is not allowed.
						    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.c2cnotallowed"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : According to the transfer rule C2C transfer is not allowed","");
							continue;
						}
						// check the transfer profile product code
						// transfer profile check ends here
						if(transferProfileMap.get(batchItemsVO.getTxnProfile())==null)
						{
							index=0;
							pstmtSelectTProfileProd.setString(++index,batchItemsVO.getTxnProfile());
							pstmtSelectTProfileProd.setString(++index,p_batchMasterVO.getProductCode());
							pstmtSelectTProfileProd.setString(++index,PretupsI.PARENT_PROFILE_ID_CATEGORY);
							rsSelectTProfileProd=pstmtSelectTProfileProd.executeQuery();
							pstmtSelectTProfileProd.clearParameters();
							if(!rsSelectTProfileProd.next())
							{
								transferProfileMap.put(batchItemsVO.getTxnProfile(),"false");
								//put error Transfer profile for this product is not define
							    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.trfprofilenotdefined"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Transfer profile for this product is not defined","");
								continue;
							}
							transferProfileMap.put(batchItemsVO.getTxnProfile(),"true");
						}
						else
						{
							
							if("false".equals(transferProfileMap.get(batchItemsVO.getTxnProfile())))
							{
								// put error Transfer profile for this product is not define
							    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.trfprofilenotdefined"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Transfer profile for this product is not defined","");
								continue;
							}
						}
						
						// check the commisson profile applicability and other checks related to the commission profile
						index=0;
						pstmtSelectCProfileProd.setString(++index,p_batchMasterVO.getProductCode());
						pstmtSelectCProfileProd.setString(++index,batchItemsVO.getCommissionProfileSetId());
						pstmtSelectCProfileProd.setString(++index,batchItemsVO.getCommissionProfileVer());
						rsSelectCProfileProd=pstmtSelectCProfileProd.executeQuery();
						pstmtSelectCProfileProd.clearParameters();
						if(!rsSelectCProfileProd.next())
						{
							// put error commission profile for this product is not defined
						    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.commprfnotdefined"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Commission profile for this product is not defined","");
							continue;
						}
						requestedValue=batchItemsVO.getRequestedQuantity();
						minTrfValue=rsSelectCProfileProd.getLong("min_transfer_value");
						maxTrfValue=rsSelectCProfileProd.getLong("max_transfer_value");
						if(minTrfValue > requestedValue || maxTrfValue < requestedValue )
						{
							msgArr=new String[3];
							msgArr[0]=PretupsBL.getDisplayAmount(requestedValue);
							msgArr[1]=PretupsBL.getDisplayAmount(minTrfValue);
							msgArr[2]=PretupsBL.getDisplayAmount(maxTrfValue);
							// put error requested quantity is not between min and max values
						    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.qtymaxmin",msgArr));
						    msgArr=null;
						    errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Requested quantity is not between min and max values","minTrfValue="+minTrfValue+", maxTrfValue="+maxTrfValue);
							continue;
						}
						multipleOf=rsSelectCProfileProd.getLong("transfer_multiple_off");
						if(requestedValue%multipleOf != 0)
						{
							// put error requested quantity is not multiple of
						    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.notmulof",new String[]{PretupsBL.getDisplayAmount(multipleOf)}));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Requested quantity is not in multiple value","multiple of="+multipleOf);
							continue;
						}
						
						index=0;
						pstmtSelectCProfileProdDetail.setString(++index,rsSelectCProfileProd.getString("comm_profile_products_id"));
						pstmtSelectCProfileProdDetail.setLong(++index,requestedValue);
						pstmtSelectCProfileProdDetail.setLong(++index,requestedValue);
						rsSelectCProfileProdDetail=pstmtSelectCProfileProdDetail.executeQuery();
						pstmtSelectCProfileProdDetail.clearParameters();
						if(!rsSelectCProfileProdDetail.next())
						{
							// put error commission profile slab is not define for the requested value
						    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.initiatebatchc2ctransfer.msg.error.commslabnotdefined"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Commission profile slab is not define for the requested value","");
							continue;
						}	
						 // to calculate tax
						transferItemsList = new ArrayList();
						channelTransferItemsVO = new ChannelTransferItemsVO ();
						// this value will be inserted into the table as the requested qty
						channelTransferItemsVO.setRequiredQuantity(requestedValue);
						// this value will be used in the tax calculation.
						channelTransferItemsVO.setRequestedQuantity(PretupsBL.getDisplayAmount(requestedValue));
						channelTransferItemsVO.setCommProfileDetailID(rsSelectCProfileProdDetail.getString("comm_profile_detail_id"));
						channelTransferItemsVO.setUnitValue(p_batchMasterVO.getProductMrp());
						channelTransferItemsVO.setCommRate(rsSelectCProfileProdDetail.getLong("commission_rate"));
						channelTransferItemsVO.setCommType(rsSelectCProfileProdDetail.getString("commission_type"));
						channelTransferItemsVO.setDiscountRate(rsSelectCProfileProd.getLong("discount_rate"));
						channelTransferItemsVO.setDiscountType(rsSelectCProfileProd.getString("discount_type"));
						channelTransferItemsVO.setTax1Rate(rsSelectCProfileProdDetail.getLong("tax1_rate"));
						channelTransferItemsVO.setTax1Type(rsSelectCProfileProdDetail.getString("tax1_type"));
						channelTransferItemsVO.setTax2Rate(rsSelectCProfileProdDetail.getLong("tax2_rate"));
						channelTransferItemsVO.setTax2Type(rsSelectCProfileProdDetail.getString("tax2_type"));
						channelTransferItemsVO.setTax3Rate(rsSelectCProfileProdDetail.getLong("tax3_rate"));
						channelTransferItemsVO.setTax3Type(rsSelectCProfileProdDetail.getString("tax3_type"));
	
						//added by vikram
						if(PretupsI.YES.equals(rsSelectCProfileProd.getString("taxes_on_channel_transfer")))
						{					
							channelTransferItemsVO.setTaxOnChannelTransfer(PretupsI.YES);
						}
						else
							channelTransferItemsVO.setTaxOnChannelTransfer(PretupsI.NO);
						transferItemsList.add(channelTransferItemsVO);
	                    
	                    channelTransferVO=new ChannelTransferVO();
	                    channelTransferVO.setChannelTransferitemsVOList(transferItemsList);
	                    //channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
	                    channelTransferVO.setTransferSubType(batchItemsVO.getTransferSubType());
	                    //ChannelTransferBL.calculateMRPWithTaxAndDiscount(channelTransferVO,PretupsI.TRANSFER_TYPE_FOC);
	                    if(((Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL))).booleanValue()){
	                    	if(_log.isDebugEnabled()){
								_log.debug(methodName,"batchItemsVO.getMsisdn()="+batchItemsVO.getMsisdn()+", batchItemsVO.getLoginID()="+batchItemsVO.getLoginID()+", batchItemsVO.getExternalCode()="+batchItemsVO.getExternalCode());
							}
							if(!BTSLUtil.isNullString(batchItemsVO.getMsisdn()))
		                    	channelTransferVO.setToUserMsisdn(batchItemsVO.getMsisdn());
		                    else if(!BTSLUtil.isNullString(batchItemsVO.getLoginID()))
		                    	channelTransferVO.setToUserMsisdn(((ChannelUserVO)new ChannelUserDAO().loadChannelUserDetailsByLoginIDANDORMSISDN(p_con,"",batchItemsVO.getLoginID())).getMsisdn());
		                    else if(!BTSLUtil.isNullString(batchItemsVO.getExternalCode()))
		                    	channelTransferVO.setToUserMsisdn(((ChannelUserVO)new ChannelUserDAO().loadChnlUserDetailsByExtCode(p_con, batchItemsVO.getExternalCode())).getMsisdn());
							 channelTransferVO.setCommProfileSetId(batchItemsVO.getCommissionProfileSetId());
		        	         channelTransferVO.setCommProfileVersion(batchItemsVO.getCommissionProfileVer()); 
		        	         if(messageGatewayVO!=null && messageGatewayVO.getRequestGatewayVO()!=null)
		     				{
		     					channelTransferVO.setRequestGatewayCode(messageGatewayVO.getRequestGatewayVO().getGatewayCode());
		     					channelTransferVO.setRequestGatewayType(messageGatewayVO.getGatewayType());
		     				}
						}
	                    ChannelTransferBL.calculateMRPWithTaxAndDiscount(channelTransferVO,PretupsI.TRANSFER_TYPE_C2C);
						// taxes on C2C required
						// ends commission profile validaiton
						
						pstmtLoadUser.clearParameters();
						m=0;
						pstmtLoadUser.setString(++m,batchItemsVO.getUserId());
						rs=pstmtLoadUser.executeQuery();
		                //(record found for user i.e. receiver) if this condition is not true then made entry in logs and leave this data.
						if(rs.next())
						{
							channelUserVO = new ChannelUserVO();
			                channelUserVO.setUserID(batchItemsVO.getUserId());
			                channelUserVO.setStatus(rs.getString("userstatus"));
			                channelUserVO.setInSuspend(rs.getString("in_suspend"));
			                channelUserVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
			                channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
			                channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
			                channelUserVO.setTransferProfileStatus(rs.getString("profile_status"));
			                language=rs.getString("phone_language");
			                country=rs.getString("country");
			                channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
			                try{if (rs != null){rs.close();}} catch (Exception e){}
			                //(user status is checked) if this condition is true then made entry in logs and leave this data.
			       		 	if(!PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus()))
			       		 	{
			       		 		p_con.rollback();
			       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.usersuspend"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog.operatorDetailLog(methodName,null,batchItemsVO,"FAIL : User is suspend","Approval level");
								continue;
			       		 	}
			                //(commission profile status is checked) if this condition is true then made entry in logs and leave this data.
			       		 	else if(!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus()))
			                {
			                	p_con.rollback();
			       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.comprofsuspend"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog.operatorDetailLog(methodName,null,batchItemsVO,"FAIL : Commission profile suspend","Approval level");
								continue;
			                }
			                //(transfer profile is checked) if this condition is true then made entry in logs and leave this data.
			                else if(!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus()))
			                {
			                	p_con.rollback();
			       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.trfprofsuspend"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog.operatorDetailLog(methodName,null,batchItemsVO,"FAIL : Transfer profile suspend","Approval level");
								continue;
			                }
			                //(user in suspend  is checked) if this condition is true then made entry in logs and leave this data.
			                else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend()))
			                {
			                	p_con.rollback();
			       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.userinsuspend"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog.operatorDetailLog(methodName,null,batchItemsVO,"FAIL : User is IN suspend","Approval level");
								continue;
			                }
						}
		                //(no record found for user i.e. receiver) if this condition is true then make entry in logs and leave this data.
						else
						{
							p_con.rollback();
		       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.nouser"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,null,batchItemsVO,"FAIL : User not found","Approval level");
							continue;
						}
						
					    // creating the channelTransferVO here since C2CTransferID will be required into the network stock
						// transaction table. Other information will be set into this VO later
						// seting the current value for generation of the transfer ID. This will be over write by the
						// bacth c2c items was created.
						channelTransferVO.setCreatedOn(date);
			    		channelTransferVO.setNetworkCode(p_batchMasterVO.getNetworkCode());
			    		channelTransferVO.setNetworkCodeFor(p_batchMasterVO.getNetworkCodeFor());
	
						//ChannelTransferBL.genrateTransferID(channelTransferVO);
			    		if(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION.equals(batchItemsVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(batchItemsVO.getTransferSubType()))
			    		    ChannelTransferBL.genrateChnnlToChnnlTrfID(channelTransferVO);
			    		else if(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(batchItemsVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(batchItemsVO.getTransferSubType()))
			    		    ChannelTransferBL.genrateChnnlToChnnlWithdrawID(channelTransferVO);
			    		/*else if(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(batchItemsVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN.equals(batchItemsVO.getTransferSubType()))
			    		    ChannelTransferBL.genrateChnnlToChnnlReturnID(channelTransferVO);*/
						c2cTransferID=channelTransferVO.getTransferID();
						// value is over writing since in the channel trasnfer table created on should be same as when the
						// batch c2c item was created.
						channelTransferVO.setCreatedOn(batchItemsVO.getInitiatedOn());
						
						dayDifference=0;
						
		                dailyBalanceUpdatedOn=null;
						dayDifference=0;
						
						
						pstmtSelectSenderBalance.clearParameters();
						m=0;
						pstmtSelectSenderBalance.setString(++m,p_senderVO.getUserID());
						pstmtSelectSenderBalance.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
						rs=null;
						rs=pstmtSelectSenderBalance.executeQuery();
						while(rs.next())
						{
							dailyBalanceUpdatedOn=rs.getDate("daily_balance_updated_on");
							senderPreviousBal=rs.getLong("balance");
							//if record exist check updated on date with current date
							//day differences to maintain the record of previous days.
							dayDifference=BTSLUtil.getDifferenceInUtilDates(dailyBalanceUpdatedOn,date);
							if(dayDifference>0)
							{
								//if dates are not equal get the day differencts and execute insert qurery no of times of the 
								if(_log.isDebugEnabled())
									_log.debug(methodName,"Till now daily Stock is not updated on "+date+", day differences = "+dayDifference);
								
								for(k=0;k<dayDifference;k++)
								{
									pstmtInsertSenderDailyBalances.clearParameters();
									m=0;
									pstmtInsertSenderDailyBalances.setDate(++m,BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyBalanceUpdatedOn,k)));
									pstmtInsertSenderDailyBalances.setString(++m,rs.getString("user_id"));
									pstmtInsertSenderDailyBalances.setString(++m,rs.getString("network_code"));
	
									pstmtInsertSenderDailyBalances.setString(++m,rs.getString("network_code_for"));
									pstmtInsertSenderDailyBalances.setString(++m,rs.getString("product_code"));
									pstmtInsertSenderDailyBalances.setLong(++m,rs.getLong("balance"));
									pstmtInsertSenderDailyBalances.setLong(++m,rs.getLong("prev_balance"));
									//pstmtInsertSenderDailyBalances.setString(++m,PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
									pstmtInsertSenderDailyBalances.setString(++m,batchItemsVO.getTransferType());
									pstmtInsertSenderDailyBalances.setString(++m,channelTransferVO.getTransferID());
									pstmtInsertSenderDailyBalances.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
									pstmtInsertSenderDailyBalances.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
									pstmtInsertSenderDailyBalances.setString(++m,PretupsI.DAILY_BALANCE_CREATION_TYPE_MAN);
									updateCount=pstmtInsertSenderDailyBalances.executeUpdate();
									
									if (updateCount <= 0)
									{
										p_con.rollback();
					       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
										errorList.add(errorVO);
										BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB Error while inserting user daily balances table","Approval level = "+"No Approval required"+", updateCount ="+updateCount);
										terminateProcessing=true;
										break;
									}
								}//end of for loop
								if(terminateProcessing)
								{
									BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Terminting the procssing of this user as error while updation daily balance","No Approval required");
									continue;
								}
								//Update the user balances table
								pstmtUpdateSenderBalanceOn.clearParameters();
								m=0;
								pstmtUpdateSenderBalanceOn.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
								pstmtUpdateSenderBalanceOn.setString(++m,p_senderVO.getUserID());
								updateCount=pstmtUpdateSenderBalanceOn.executeUpdate();
				                //(record not updated properly) if this condition is true then made entry in logs and leave this data.
								if (updateCount <= 0)
								{
									p_con.rollback();
				       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
									errorList.add(errorVO);
									BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB Error while updating user balances table for daily balance","Approval level = "+"No Approval required"+", updateCount="+updateCount);
									continue;
								}
							}
						}//end of if condition
						try{if (rs != null){rs.close();}} catch (Exception e){}
						maxBalance=0;
						isNotToExecuteQuery = false;
						
						
						
						
						//select the record form the userBalances table.
						pstmtSelectUserBalances.clearParameters();
						m=0;
						pstmtSelectUserBalances.setString(++m,channelUserVO.getUserID());
						pstmtSelectUserBalances.setDate(++m,BTSLUtil.getSQLDateFromUtilDate(date));
						rs=null;
						rs=pstmtSelectUserBalances.executeQuery();
						while(rs.next())
						{
							dailyBalanceUpdatedOn=rs.getDate("daily_balance_updated_on");
							//if record exist check updated on date with current date
							//day differences to maintain the record of previous days.
							dayDifference=BTSLUtil.getDifferenceInUtilDates(dailyBalanceUpdatedOn,date);
							if(dayDifference>0)
							{
								//if dates are not equal get the day differencts and execute insert qurery no of times of the 
								if(_log.isDebugEnabled())
									_log.debug(methodName,"Till now daily Stock is not updated on "+date+", day differences = "+dayDifference);
								
								for(k=0;k<dayDifference;k++)
								{
									pstmtInsertUserDailyBalances.clearParameters();
									m=0;
									pstmtInsertUserDailyBalances.setDate(++m,BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyBalanceUpdatedOn,k)));
									pstmtInsertUserDailyBalances.setString(++m,rs.getString("user_id"));
									pstmtInsertUserDailyBalances.setString(++m,rs.getString("network_code"));
	
									pstmtInsertUserDailyBalances.setString(++m,rs.getString("network_code_for"));
									pstmtInsertUserDailyBalances.setString(++m,rs.getString("product_code"));
									pstmtInsertUserDailyBalances.setLong(++m,rs.getLong("balance"));
									pstmtInsertUserDailyBalances.setLong(++m,rs.getLong("prev_balance"));
									//pstmtInsertUserDailyBalances.setString(++m,PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
									pstmtInsertUserDailyBalances.setString(++m,batchItemsVO.getTransferType());
									pstmtInsertUserDailyBalances.setString(++m,channelTransferVO.getTransferID());
									pstmtInsertUserDailyBalances.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
									pstmtInsertUserDailyBalances.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
									pstmtInsertUserDailyBalances.setString(++m,PretupsI.DAILY_BALANCE_CREATION_TYPE_MAN);
									updateCount=pstmtInsertUserDailyBalances.executeUpdate();
									
									if (updateCount <= 0)
									{
										p_con.rollback();
					       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
										errorList.add(errorVO);
										BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB Error while inserting user daily balances table","Approval level = "+"No Approval required"+", updateCount ="+updateCount);
										terminateProcessing=true;
										break;
									}							
								}//end of for loop
								if(terminateProcessing)
								{
									BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Terminting the procssing of this user as error while updation daily balance","Approval level = "+"No Approval required");
									continue;
								}
								//Update the user balances table
								pstmtUpdateUserBalances.clearParameters();
								m=0;
								pstmtUpdateUserBalances.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
								pstmtUpdateUserBalances.setString(++m,channelUserVO.getUserID());
								updateCount=pstmtUpdateUserBalances.executeUpdate();
				                //(record not updated properly) if this condition is true then made entry in logs and leave this data.
								if (updateCount <= 0)
								{
									p_con.rollback();
				       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
									errorList.add(errorVO);
									BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB Error while updating user balances table for daily balance","Approval level = "+"No Approval required"+", updateCount="+updateCount);
									continue;
								}
							}
						}//end of if condition
						try{if (rs != null){rs.close();}} catch (Exception e){}
						maxBalance=0;
						isNotToExecuteQuery = false;
						// sender balance to be debited
						pstmtSelectBalance.clearParameters();
						m=0;
						pstmtSelectBalance.setString(++m,p_senderVO.getUserID());
						pstmtSelectBalance.setString(++m,p_batchMasterVO.getProductCode());
						pstmtSelectBalance.setString(++m,p_batchMasterVO.getNetworkCode());
						pstmtSelectBalance.setString(++m,p_batchMasterVO.getNetworkCodeFor());
		                rs=null;
		                rs = pstmtSelectBalance.executeQuery();
		                senderBalance = -1;
		                previousSenderBalToBeSetChnlTrfItems=-1;
		                if(rs.next())
		                {
		                    senderBalance = rs.getLong("balance");
		                    try{if (rs != null){rs.close();}} catch (Exception e){}
		                }
		                else
		                {
		                	p_con.rollback();
		        		 	errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchc2capprove.closeOrderByBatch.sendernobal"));
		     				errorList.add(errorVO);
		     				BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB Error while selecting user balances table for daily balance","Approval level = "+"No Approval required");
		     				continue;
		                }
		                
		                if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(p_batchMasterVO.getTransferType())&& PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(p_batchMasterVO.getTransferSubType()))
	                	{
	                		previousSenderBalToBeSetChnlTrfItems=senderBalance;
	                		senderBalance += batchItemsVO.getRequestedQuantity();
	                	}
	                    else if((PretupsI.CHANNEL_TRANSFER_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferType()))&& PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferSubType()))
	              		{
	                		if(senderBalance==0 ||  senderBalance - batchItemsVO.getRequestedQuantity() < 0 )
	                		{
	                			p_con.rollback();
	                			errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchc2capprove.closeOrderByBatch.senderbalzero"));
	                			errorList.add(errorVO);
	                			BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB Error while selecting user balances table for daily balance","Approval level = "+"No Approval required");
	                			continue;
	                		}
	                		else if(senderBalance != 0 && ( senderBalance - batchItemsVO.getRequestedQuantity() >= 0 ))
	                		{
	                			previousSenderBalToBeSetChnlTrfItems=senderBalance;
	                			senderBalance -= batchItemsVO.getRequestedQuantity();}
	                		else 
	                			previousSenderBalToBeSetChnlTrfItems=0;
	              		}
	                	m = 0;
	                    //update   sender balance 
	                    if(senderBalance > -1)
	                    {
	                    	pstmtUpdateSenderBalance.clearParameters();
	                    	handlerStmt = pstmtUpdateSenderBalance;
	                    }
	                    handlerStmt.setLong(++m,previousSenderBalToBeSetChnlTrfItems);
	                    handlerStmt.setLong(++m,senderBalance);
	                    handlerStmt.setString(++m,batchItemsVO.getTransferType());
	                    handlerStmt.setString(++m,c2cTransferID);
	                    handlerStmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(date));
	                    handlerStmt.setString(++m,p_senderVO.getUserID());
	                    handlerStmt.setString(++m,p_batchMasterVO.getProductCode());
	                    handlerStmt.setString(++m,p_batchMasterVO.getNetworkCode());
	                    handlerStmt.setString(++m,p_batchMasterVO.getNetworkCodeFor());
	                    updateCount = handlerStmt.executeUpdate();
	                    handlerStmt.clearParameters();
		                if(updateCount <= 0 )
		                {
		                	p_con.rollback();
		       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB error while credit uer balance","Approval level = "+"No Approval required");
							continue;
		                }
		               
		               
		                // thresholdValue=(Long)PreferenceCache.getControlPreference(PreferenceI.ZERO_BAL_THRESHOLD_VALUE,p_batchMasterVO.getNetworkCode(), p_senderVO.getCategoryCode()); //threshold value
	                  //for zero balance counter..
	                    try
	                    {
	                        m=0;
	                        boolean isUserThresholdEntryReq=false;
	                        String thresholdType=null;

	                        //added by nilesh 
	    	                transferProfileProductVO =TransferProfileProductCache.getTransferProfileDetails(p_senderVO.getTransferProfileID(),p_batchMasterVO.getProductCode());
	    	                String remark=null;
	    	                String threshold_type=null;
	                        if(senderBalance<=transferProfileProductVO.getAltBalanceLong() && senderBalance>transferProfileProductVO.getMinResidualBalanceAsLong())
	    	                {
	                        	//isUserThresholdEntryReq=true;
	    	                	thresholdValue=transferProfileProductVO.getAltBalanceLong();
	    	                	threshold_type=PretupsI.THRESHOLD_TYPE_ALERT;
	    	                }
	                        else if(senderBalance<=transferProfileProductVO.getMinResidualBalanceAsLong())
	                        {
	                        	//isUserThresholdEntryReq=true;
	                        	thresholdValue=transferProfileProductVO.getMinResidualBalanceAsLong();
	    	                	threshold_type=PretupsI.THRESHOLD_TYPE_MIN;
	                        }
	                        //new
	                        if(previousSenderBalToBeSetChnlTrfItems>=thresholdValue && senderBalance <=thresholdValue)
	                        {
	                            isUserThresholdEntryReq=true;
	                            thresholdType=PretupsI.BELOW_THRESHOLD_TYPE;
	                        }
	                        else if(previousSenderBalToBeSetChnlTrfItems<=thresholdValue && senderBalance >=thresholdValue)
	                        {
	                            isUserThresholdEntryReq=true;
	                            thresholdType=PretupsI.ABOVE_THRESHOLD_TYPE;
	                        }
	                        else if(previousSenderBalToBeSetChnlTrfItems<=thresholdValue && senderBalance <=thresholdValue)
	                        {
	                            isUserThresholdEntryReq=true;
	                            thresholdType=PretupsI.BELOW_THRESHOLD_TYPE;
	                        }
	                        //end
	                        
	                        if(isUserThresholdEntryReq)
	                        {
	                            if (_log.isDebugEnabled())
	                            {
	                                _log.debug(methodName, "Entry in threshold counter" + thresholdValue+ ", prvbal: "+previousSenderBalToBeSetChnlTrfItems+ "nbal"+ senderBalance);
	                            }
	                            psmtInsertUserThreshold.clearParameters();
	                            m=0;
	                            psmtInsertUserThreshold.setString(++m, p_senderVO.getUserID());
	                            psmtInsertUserThreshold.setString(++m, c2cTransferID);
	                            psmtInsertUserThreshold.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(date));
	                            psmtInsertUserThreshold.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(date));
	                            psmtInsertUserThreshold.setString(++m, p_batchMasterVO.getNetworkCode());
	                            psmtInsertUserThreshold.setString(++m, p_batchMasterVO.getProductCode());
	                            //psmtInsertUserThreshold.setLong(++m, p_userBalancesVO.getUnitValue());
	                            psmtInsertUserThreshold.setString(++m, PretupsI.CHANNEL_TYPE_C2C);
	                            psmtInsertUserThreshold.setString(++m, p_batchMasterVO.getTransferType());
	                            psmtInsertUserThreshold.setString(++m, thresholdType);
	                            psmtInsertUserThreshold.setString(++m,p_senderVO.getCategoryCode());
	                            psmtInsertUserThreshold.setLong(++m,previousSenderBalToBeSetChnlTrfItems);
	                            psmtInsertUserThreshold.setLong(++m, senderBalance);
	                            psmtInsertUserThreshold.setLong(++m, thresholdValue);
	                            //added by nilesh
	                            psmtInsertUserThreshold.setString(++m, threshold_type);
	                            psmtInsertUserThreshold.setString(++m, remark);
	                            
	                            psmtInsertUserThreshold.executeUpdate();
	                        }
	                    }
	                    catch (SQLException sqle)
	                    {
	                        _log.error(methodName, "SQLException " + sqle.getMessage());
	                        EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[closeBatchC2CTransfer]",c2cTransferID,"",p_batchMasterVO.getNetworkCode(),"Error while updating user_threshold_counter table SQL Exception:"+sqle.getMessage());
	                    }// end of catch
		                
		                
		                //if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferSubType()))
			           //{    
			            pstmtSelectSenderTransferCounts.clearParameters();
			            m=0;
		                pstmtSelectSenderTransferCounts.setString(++m,p_senderVO.getUserID());	
		                rs=null;
		                rs = pstmtSelectSenderTransferCounts.executeQuery(); 
		//              get the Sender transfer counts
		                senderCountsVO=null;    
		                if (rs.next())
		                {
		                	senderCountsVO = new UserTransferCountsVO();
		                	senderCountsVO.setUserID(p_senderVO.getUserID() );
		                    
		                	senderCountsVO.setDailyInCount( rs.getLong("daily_in_count") );
		                	senderCountsVO.setDailyInValue( rs.getLong("daily_in_value") );
		                	senderCountsVO.setWeeklyInCount( rs.getLong("weekly_in_count") );
		                	senderCountsVO.setWeeklyInValue( rs.getLong("weekly_in_value") );
		                	senderCountsVO.setMonthlyInCount( rs.getLong("monthly_in_count") );
		                	senderCountsVO.setMonthlyInValue( rs.getLong("monthly_in_value") );
			                
		                	senderCountsVO.setDailyOutCount( rs.getLong("daily_out_count") );
		                	senderCountsVO.setDailyOutValue( rs.getLong("daily_out_value") );
		                	senderCountsVO.setWeeklyOutCount( rs.getLong("weekly_out_count") );
		                	senderCountsVO.setWeeklyOutValue( rs.getLong("weekly_out_value") );
		                	senderCountsVO.setMonthlyOutCount( rs.getLong("monthly_out_count") );
		                	senderCountsVO.setMonthlyOutValue( rs.getLong("monthly_out_value") );				
			                
		                	senderCountsVO.setUnctrlDailyInCount( rs.getLong("outside_daily_in_count") );
		                	senderCountsVO.setUnctrlDailyInValue( rs.getLong("outside_daily_in_value") );
		                	senderCountsVO.setUnctrlWeeklyInCount( rs.getLong("outside_weekly_in_count") );
		                	senderCountsVO.setUnctrlWeeklyInValue( rs.getLong("outside_weekly_in_value") );
		                	senderCountsVO.setUnctrlMonthlyInCount( rs.getLong("outside_monthly_in_count") );
		                	senderCountsVO.setUnctrlMonthlyInValue( rs.getLong("outside_monthly_in_value") );
		
		                	senderCountsVO.setUnctrlDailyOutCount( rs.getLong("outside_daily_out_count") );
		                	senderCountsVO.setUnctrlDailyOutValue( rs.getLong("outside_daily_out_value") );
		                	senderCountsVO.setUnctrlWeeklyOutCount( rs.getLong("outside_weekly_out_count") );
		                	senderCountsVO.setUnctrlWeeklyOutValue( rs.getLong("outside_weekly_out_value") );
		                	senderCountsVO.setUnctrlMonthlyOutCount( rs.getLong("outside_monthly_out_count") );
		                	senderCountsVO.setUnctrlMonthlyOutValue( rs.getLong("outside_monthly_out_value") );
							
		                	senderCountsVO.setDailySubscriberOutCount( rs.getLong("daily_subscriber_out_count") );
		                	senderCountsVO.setDailySubscriberOutValue( rs.getLong("daily_subscriber_out_value") );
		                	senderCountsVO.setWeeklySubscriberOutCount( rs.getLong("weekly_subscriber_out_count") );
		                	senderCountsVO.setWeeklySubscriberOutValue( rs.getLong("weekly_subscriber_out_value") );
		                	senderCountsVO.setMonthlySubscriberOutCount( rs.getLong("monthly_subscriber_out_count") );
		                	senderCountsVO.setMonthlySubscriberOutValue( rs.getLong("monthly_subscriber_out_value") );
			               
		                	senderCountsVO.setLastTransferDate(rs.getDate("last_transfer_date") );
							try{if (rs != null){rs.close();}} catch (Exception e){}
		                }
		                flag=true;
		        		if(senderCountsVO == null)
		        		{
		        			flag = false;
		        			senderCountsVO = new UserTransferCountsVO();
		        		}
		        		//If found then check for reset otherwise no need to check it
		        		if(flag)
		        			ChannelTransferBL.checkResetCountersAfterPeriodChange(senderCountsVO,date);
		        		
		        		pstmtSelectSenderProfileOutCounts.clearParameters();
						m=0;
						pstmtSelectSenderProfileOutCounts.setString(++m,batchItemsVO.getTxnProfile());
						pstmtSelectSenderProfileOutCounts.setString(++m,PretupsI.YES);
						pstmtSelectSenderProfileOutCounts.setString(++m,p_batchMasterVO.getNetworkCode());
						pstmtSelectSenderProfileOutCounts.setString(++m,PretupsI.PARENT_PROFILE_ID_CATEGORY);
						pstmtSelectSenderProfileOutCounts.setString(++m,PretupsI.YES);
		    			rs=null;
		    			rs = pstmtSelectSenderProfileOutCounts.executeQuery();
		    			if (rs.next())
		    			{
		    				senderTfrProfileCheckVO = new TransferProfileVO();
		    				senderTfrProfileCheckVO.setProfileId(rs.getString("profile_id"));
		    				senderTfrProfileCheckVO.setDailyOutCount( rs.getLong("daily_transfer_out_count") );
		    				senderTfrProfileCheckVO.setDailyOutValue( rs.getLong("daily_transfer_out_value"));
		    				senderTfrProfileCheckVO.setWeeklyOutCount( rs.getLong("weekly_transfer_out_count") );
		    				senderTfrProfileCheckVO.setWeeklyOutValue( rs.getLong("weekly_transfer_out_value"));
		    				senderTfrProfileCheckVO.setMonthlyOutCount( rs.getLong("monthly_transfer_out_count") );
		    				senderTfrProfileCheckVO.setMonthlyOutValue( rs.getLong("monthly_transfer_out_value"));
		    				try{if (rs != null){rs.close();}} catch (Exception e){}
		    			}
	                   //(profile counts not found) if this condition is true then made entry in logs and leave this data.
		    			else
		    			{
		    				p_con.rollback();
		       		 		errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.transferprofilenotfound"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Transfer profile not found","Approval level = "+"No Approval required");
							continue;
		    			}
		    			if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferSubType()))
		    			{
		    				//(daily in count reach) if this condition is true then made entry in logs and leave this data.
			    	        if(senderTfrProfileCheckVO.getDailyOutCount() <= senderCountsVO.getDailyOutCount())
			    			{
			    				p_con.rollback();
			       		 		errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.dailyincntreach"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Daily transfer in count reach","Approval level = "+"No Approval required");
								continue;
			    			}
			                //(daily in value reach) if this condition is true then made entry in logs and leave this data.
			    			//else if(senderTfrProfileCheckVO.getDailyOutValue() < (senderCountsVO.getDailyOutValue() + batchItemsVO.getRequestedQuantity() )  )
			    	        else if(senderTfrProfileCheckVO.getDailyOutValue() < (senderCountsVO.getDailyOutValue() +channelTransferItemsVO.getProductTotalMRP() )  )
			    			{
			    				p_con.rollback();
			       		 		errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.dailyinvaluereach"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Daily transfer in value reach","Approval level = "+"No Approval required");
								continue;
			    			}
			                //(weekly in count reach) if this condition is true then made entry in logs and leave this data.
			    			else if(senderTfrProfileCheckVO.getWeeklyOutCount() <=  senderCountsVO.getWeeklyOutCount() )
			    			{
			    				p_con.rollback();
			       		 		errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.weeklyincntreach"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Weekly transfer in count reach","Approval level = "+"No Approval required");
								continue;
			    			}
			                //(weekly in value reach) if this condition is true then made entry in logs and leave this data.
			    			//else if(senderTfrProfileCheckVO.getWeeklyOutValue() < ( senderCountsVO.getWeeklyOutValue() + batchItemsVO.getRequestedQuantity() )  )
			    			else if(senderTfrProfileCheckVO.getWeeklyOutValue() < ( senderCountsVO.getWeeklyOutValue() + channelTransferItemsVO.getProductTotalMRP() )  )
			    	        {
			    				p_con.rollback();
			       		 		errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.weeklyinvaluereach"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Weekly transfer in value reach","Approval level = "+"No Approval required");
								continue;
			    			}
			                //(monthly in count reach) if this condition is true then made entry in logs and leave this data.
			    			else if(senderTfrProfileCheckVO.getMonthlyOutCount() <=  senderCountsVO.getMonthlyOutCount()  )
			    			{
			    				p_con.rollback();
			       		 		errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.monthlyincntreach"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Monthly transfer in count reach","Approval level = "+"No Approval required");
								continue;
			    			}
			                //(monthly in value reach) if this condition is true then made entry in logs and leave this data.
			    			//else if(senderTfrProfileCheckVO.getMonthlyOutValue() < ( senderCountsVO.getMonthlyOutValue() + batchItemsVO.getRequestedQuantity() ) )
			    			else if(senderTfrProfileCheckVO.getMonthlyOutValue() < ( senderCountsVO.getMonthlyOutValue() + channelTransferItemsVO.getProductTotalMRP() ) )
			    			{
			    				p_con.rollback();
			       		 		errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.monthlyinvaluereach"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Monthly transfer in value reach","Approval level = "+"No Approval required");
								continue;
			    			}
						}        
		    	        senderCountsVO.setUserID(p_senderVO.getUserID());
		    	        if(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(batchItemsVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(batchItemsVO.getTransferSubType()))
			            {

			    	        senderCountsVO.setDailyOutValue(senderCountsVO.getDailyOutValue()-channelTransferItemsVO.getProductTotalMRP());
			    	        senderCountsVO.setWeeklyOutValue(senderCountsVO.getWeeklyOutValue()-channelTransferItemsVO.getProductTotalMRP());
			    	        senderCountsVO.setMonthlyOutValue(senderCountsVO.getMonthlyOutValue()-channelTransferItemsVO.getProductTotalMRP());
			            }
		    	        else
		    	        {
		    	        	senderCountsVO.setDailyOutCount(senderCountsVO.getDailyOutCount()+1);
			    	        senderCountsVO.setWeeklyOutCount(senderCountsVO.getWeeklyOutCount()+1);
			    	        senderCountsVO.setMonthlyOutCount(senderCountsVO.getMonthlyOutCount()+1);

			    	        senderCountsVO.setDailyOutValue(senderCountsVO.getDailyOutValue()+channelTransferItemsVO.getProductTotalMRP());
			    	        senderCountsVO.setWeeklyOutValue(senderCountsVO.getWeeklyOutValue()+channelTransferItemsVO.getProductTotalMRP());
			    	        senderCountsVO.setMonthlyOutValue(senderCountsVO.getMonthlyOutValue()+channelTransferItemsVO.getProductTotalMRP());
		    	        }
		    	        senderCountsVO.setLastOutTime(date);
		    	        senderCountsVO.setLastTransferID(c2cTransferID);
		    	        senderCountsVO.setLastTransferDate(date);
		    	        
	//    	      Update counts if found in db
		        		
		    	        if(flag)
		        		{
		 			        m = 0 ;
		 					pstmtUpdateSenderTransferCounts.clearParameters();
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getDailyInCount());
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getDailyInValue());
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getWeeklyInCount());
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getWeeklyInValue());
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getMonthlyInCount());
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getMonthlyInValue());
		
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getDailyOutCount());
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getDailyOutValue());
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getWeeklyOutCount());
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getWeeklyOutValue());
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getMonthlyOutCount());
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getMonthlyOutValue());
							
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlDailyInCount());
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlDailyInValue());
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlWeeklyInCount());
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlWeeklyInValue());
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlMonthlyInCount());
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlMonthlyInValue());
		
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlDailyOutCount());
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlDailyOutValue());
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlWeeklyOutCount());
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlWeeklyOutValue());
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlMonthlyOutCount());
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getUnctrlMonthlyOutValue());
							
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getDailySubscriberOutCount());
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getDailySubscriberOutValue());
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getWeeklySubscriberOutCount());
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getWeeklySubscriberOutValue());
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getMonthlySubscriberOutCount());
		 					pstmtUpdateSenderTransferCounts.setLong(++m,senderCountsVO.getMonthlySubscriberOutValue());
							
		 					pstmtUpdateSenderTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(senderCountsVO.getLastOutTime()));
		 					pstmtUpdateSenderTransferCounts.setString(++m,senderCountsVO.getLastTransferID());
		 					pstmtUpdateSenderTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(senderCountsVO.getLastTransferDate()));
		 					pstmtUpdateSenderTransferCounts.setString(++m,senderCountsVO.getUserID());
		        	        updateCount = pstmtUpdateSenderTransferCounts.executeUpdate();
		        		}
		        		//Insert counts if not found in db
		        		else
		        		{
		        			m = 0 ;
		 					pstmtInsertSenderTransferCounts.clearParameters();
		 					pstmtInsertSenderTransferCounts.setLong(++m,senderCountsVO.getDailyOutCount());
		 					pstmtInsertSenderTransferCounts.setLong(++m,senderCountsVO.getDailyOutValue());
		 					pstmtInsertSenderTransferCounts.setLong(++m,senderCountsVO.getWeeklyOutCount());
		 					pstmtInsertSenderTransferCounts.setLong(++m,senderCountsVO.getWeeklyOutValue());
		 					pstmtInsertSenderTransferCounts.setLong(++m,senderCountsVO.getMonthlyOutCount());
		 					pstmtInsertSenderTransferCounts.setLong(++m,senderCountsVO.getMonthlyOutValue());
		 					pstmtInsertSenderTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(senderCountsVO.getLastOutTime()));
		 					pstmtInsertSenderTransferCounts.setString(++m,senderCountsVO.getLastTransferID());
		 					pstmtInsertSenderTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(senderCountsVO.getLastTransferDate()));
		 					pstmtInsertSenderTransferCounts.setString(++m,senderCountsVO.getUserID());
		        	        updateCount = pstmtInsertSenderTransferCounts.executeUpdate();
		        		}
		        		if(updateCount <= 0  )
		    			{
		                	p_con.rollback();
		       		 		errorVO=new ListValueVO(p_senderVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
							errorList.add(errorVO);
							if(flag)
								BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB error while insert sender trasnfer counts","Approval level = "+"No Approval required");
							else
								BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB error while uptdate sender trasnfer counts","Approval level = "+"No Approval required");
							continue;
		   		 		} 
		        		//}
		                
		                pstmtSelectBalance.clearParameters();
						m=0;
		                pstmtSelectBalance.setString(++m,channelUserVO.getUserID());
		                pstmtSelectBalance.setString(++m,p_batchMasterVO.getProductCode());
		                pstmtSelectBalance.setString(++m,p_batchMasterVO.getNetworkCode());
		                pstmtSelectBalance.setString(++m,p_batchMasterVO.getNetworkCodeFor());
		                rs=null;
		                rs = pstmtSelectBalance.executeQuery();
		                balance = -1;
		                previousUserBalToBeSetChnlTrfItems=-1;
		                if(rs.next())
		                {
		                    balance = rs.getLong("balance");
		                    try{if (rs != null){rs.close();}} catch (Exception e){}
		                }
		                
		                
		           if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(p_batchMasterVO.getTransferType())&& PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(p_batchMasterVO.getTransferSubType()))
		               {
		        	   	if(balance==0 || (balance - batchItemsVO.getRequestedQuantity() < 0))
			        	   	{
			                  p_con.rollback();
			       		 	  errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchc2capprove.closeOrderByBatch.receiverbalnsuff"));
			    			  errorList.add(errorVO);
			    			  BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB Error while selecting user balances table for daily balance","Approval level = "+"No Approval required");
			    			  continue;
			                }
		          	    else if(balance != 0 && balance - batchItemsVO.getRequestedQuantity() >= 0)
		          	       {
		          	    	  previousUserBalToBeSetChnlTrfItems=balance;
		          	    	  //balance -= batchItemsVO.getRequestedQuantity();
		          	    	  balance -= channelTransferItemsVO.getRequiredQuantity();
		          	       }
		          	    else 
		          	    	  previousUserBalToBeSetChnlTrfItems=0;
		             }
		           else if((PretupsI.CHANNEL_TRANSFER_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferType()))&& PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferSubType()))
		                {
		                	previousUserBalToBeSetChnlTrfItems=balance;
		                    //balance += batchItemsVO.getRequestedQuantity();
		                	balance += channelTransferItemsVO.getReceiverCreditQty();
		                }
		           if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferSubType()))
		           { 
						pstmtLoadTransferProfileProduct.clearParameters();
						m=0;
		                pstmtLoadTransferProfileProduct.setString(++m,batchItemsVO.getTxnProfile());
		                pstmtLoadTransferProfileProduct.setString(++m,p_batchMasterVO.getProductCode());
		                pstmtLoadTransferProfileProduct.setString(++m,PretupsI.PARENT_PROFILE_ID_CATEGORY);
		                pstmtLoadTransferProfileProduct.setString(++m,PretupsI.YES);
		    			rs=null;
		                rs = pstmtLoadTransferProfileProduct.executeQuery();
		                //get the transfer profile of user
		    			if(rs.next())
		    			{
		    			    transferProfileProductVO = new TransferProfileProductVO();
		    			    transferProfileProductVO.setProductCode(p_batchMasterVO.getProductCode());
		    			    transferProfileProductVO.setMinResidualBalanceAsLong(rs.getLong("min_residual_balance"));
		    			    transferProfileProductVO.setMaxBalanceAsLong(rs.getLong("max_balance"));
		    			    try{if (rs != null){rs.close();}} catch (Exception e){}
		    			}
		                //(transfer profile not found) if this condition is true then made entry in logs and leave this data.
		    			else
		    			{
		    				p_con.rollback();
		       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.profcountersnotfound"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : User Trf Profile not found for product","Approval level = "+"No Approval required");
							continue;
		    			}
		                maxBalance=transferProfileProductVO.getMaxBalanceAsLong();
		                //(max balance reach for the receiver) if this condition is true then made entry in logs and leave this data.
						if(maxBalance< balance )
		                {
		                    if(!isNotToExecuteQuery)
		                        isNotToExecuteQuery = true;
		                    p_con.rollback();
		       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.maxbalancereach"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : User Max balance reached","Approval level = "+"No Approval required");
							continue;
		                }
						//check for the very first txn of the user containg the order value larger than maxBalance
		                //(max balance reach) if this condition is true then made entry in logs and leave this data.
						else if(balance==-1 && maxBalance<batchItemsVO.getRequestedQuantity())
						 {
		                    if(!isNotToExecuteQuery)
		                        isNotToExecuteQuery = true;
		                    p_con.rollback();
		       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.maxbalancereach"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : User Max balance reached","Approval level = "+"No Approval required");
							continue;
						  }
		               }
		           
		           if(!isNotToExecuteQuery)
	               {
	                   m = 0;
	                   //update
	                   if(previousUserBalToBeSetChnlTrfItems > -1)
	                   {
	                   	pstmtUpdateBalance.clearParameters();
	                   	handlerStmt = pstmtUpdateBalance;
	                   	handlerStmt.setLong(++m,previousUserBalToBeSetChnlTrfItems);
	                   }
						else
	                   {
							// insert
							pstmtInsertBalance.clearParameters();
	                       handlerStmt = pstmtInsertBalance;
	                       balance = batchItemsVO.getRequestedQuantity();
	                       handlerStmt.setLong(++m,0);//previous balance
						   handlerStmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(date));//updated on date
	                   }
	                   
	                   handlerStmt.setLong(++m,balance);
	                   //handlerStmt.setString(++m,PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
	                   handlerStmt.setString(++m,batchItemsVO.getTransferType());
	                   handlerStmt.setString(++m,c2cTransferID);
	                   handlerStmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(date));
	                   handlerStmt.setString(++m,channelUserVO.getUserID());
	                   handlerStmt.setString(++m,p_batchMasterVO.getProductCode());
	                   handlerStmt.setString(++m,p_batchMasterVO.getNetworkCode());
	                   handlerStmt.setString(++m,p_batchMasterVO.getNetworkCodeFor());
	                   updateCount = handlerStmt.executeUpdate();
	                   handlerStmt.clearParameters();
		                if(updateCount <= 0 )
		                {
		                	p_con.rollback();
		       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB error while credit uer balance","Approval level = "+"No Approval required");
							continue;
		                }
		                
		              //for zero balance counter..
	                    try
	                    {
	
	                        //thresholdValue=(Long)PreferenceCache.getControlPreference(PreferenceI.ZERO_BAL_THRESHOLD_VALUE,p_batchMasterVO.getNetworkCode(), batchItemsVO.getCategoryCode()); //threshold value
	                        m=0;
	                        boolean isUserThresholdEntryReq=false;
	                        String thresholdType=null;
	                       /* if(previousUserBalToBeSetChnlTrfItems>=thresholdValue && balance <=thresholdValue)
	                        {
	                            isUserThresholdEntryReq=true;
	                            thresholdType=PretupsI.BELOW_THRESHOLD_TYPE;
	                        }
	                        else if(previousUserBalToBeSetChnlTrfItems<=thresholdValue && balance >=thresholdValue)
	                        {
	                            isUserThresholdEntryReq=true;
	                            thresholdType=PretupsI.ABOVE_THRESHOLD_TYPE;
	                        }*/
	                        
	//                    	added by nilesh
	                    	String remark=null;
	                    	String threshold_type=null;
	                        if(balance<=transferProfileProductVO.getAltBalanceLong() && balance>transferProfileProductVO.getMinResidualBalanceAsLong())
			                {
	                        	//isUserThresholdEntryReq=true;
			                	thresholdValue=transferProfileProductVO.getAltBalanceLong();
			                	threshold_type=PretupsI.THRESHOLD_TYPE_ALERT;
			                }
	                        else if(balance<=transferProfileProductVO.getMinResidualBalanceAsLong())
	                        {
	                        	//isUserThresholdEntryReq=true;
	                        	thresholdValue=transferProfileProductVO.getMinResidualBalanceAsLong();
			                	threshold_type=PretupsI.THRESHOLD_TYPE_MIN;
	                        }
	                        //new
	                        if(previousUserBalToBeSetChnlTrfItems>=thresholdValue && balance <=thresholdValue)
	                        {
	                            isUserThresholdEntryReq=true;
	                            thresholdType=PretupsI.BELOW_THRESHOLD_TYPE;
	                        }
	                        else if(previousUserBalToBeSetChnlTrfItems<=thresholdValue && balance >=thresholdValue)
	                        {
	                            isUserThresholdEntryReq=true;
	                            thresholdType=PretupsI.ABOVE_THRESHOLD_TYPE;
	                        }
	                        else if(previousUserBalToBeSetChnlTrfItems<=thresholdValue && balance <=thresholdValue)
	                        {
	                            isUserThresholdEntryReq=true;
	                            thresholdType=PretupsI.BELOW_THRESHOLD_TYPE;
	                        }
	                        //end
	                        
	                        if(isUserThresholdEntryReq)
	                        {
	                            if (_log.isDebugEnabled())
	                            {
	                                _log.debug(methodName, "Entry in threshold counter" + thresholdValue+ ", prvbal: "+previousUserBalToBeSetChnlTrfItems+ "nbal"+ balance);
	                            }
	                            psmtInsertUserThreshold.clearParameters();
	                            m=0;
	                            psmtInsertUserThreshold.setString(++m, channelUserVO.getUserID());
	                            psmtInsertUserThreshold.setString(++m, c2cTransferID);
	                            psmtInsertUserThreshold.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(date));
	                            psmtInsertUserThreshold.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(date));
	                            psmtInsertUserThreshold.setString(++m, p_batchMasterVO.getNetworkCode());
	                            psmtInsertUserThreshold.setString(++m, p_batchMasterVO.getProductCode());
	                            //psmtInsertUserThreshold.setLong(++m, p_userBalancesVO.getUnitValue());
	                            psmtInsertUserThreshold.setString(++m, PretupsI.CHANNEL_TYPE_C2C);
	                            psmtInsertUserThreshold.setString(++m, p_batchMasterVO.getTransferType());
	                            psmtInsertUserThreshold.setString(++m, thresholdType);
	                            psmtInsertUserThreshold.setString(++m,batchItemsVO.getCategoryCode());
	                            psmtInsertUserThreshold.setLong(++m,previousUserBalToBeSetChnlTrfItems);
	                            psmtInsertUserThreshold.setLong(++m, balance);
	                            psmtInsertUserThreshold.setLong(++m, thresholdValue);
	                            //added by nilesh
	                            psmtInsertUserThreshold.setString(++m, threshold_type);
	                            psmtInsertUserThreshold.setString(++m, remark);
	                            
	                            psmtInsertUserThreshold.executeUpdate();
	                        }
	                    }
	                    catch (SQLException sqle)
	                    {
	                        _log.error(methodName, "SQLException " + sqle.getMessage());
	                        EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[closeBatchC2CTransfer]",c2cTransferID,"",p_batchMasterVO.getNetworkCode(),"Error while updating user_threshold_counter table SQL Exception:"+sqle.getMessage());
	                    }// end of catch
	               }
		           
		           //if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferSubType())){
						pstmtSelectTransferCounts.clearParameters();
						m=0;
		                pstmtSelectTransferCounts.setString(++m,channelUserVO.getUserID());	
		                rs=null;
		                rs = pstmtSelectTransferCounts.executeQuery();
		                //get the user transfer counts
		                countsVO=null;
		                if (rs.next())
		                {
		                    countsVO = new UserTransferCountsVO();
		                    countsVO.setUserID( batchItemsVO.getUserId() );
		                    
		                    countsVO.setDailyInCount( rs.getLong("daily_in_count") );
		                    countsVO.setDailyInValue( rs.getLong("daily_in_value") );
		                    countsVO.setWeeklyInCount( rs.getLong("weekly_in_count") );
		                    countsVO.setWeeklyInValue( rs.getLong("weekly_in_value") );
		                    countsVO.setMonthlyInCount( rs.getLong("monthly_in_count") );
		                    countsVO.setMonthlyInValue( rs.getLong("monthly_in_value") );
			                
							countsVO.setDailyOutCount( rs.getLong("daily_out_count") );
			                countsVO.setDailyOutValue( rs.getLong("daily_out_value") );
			                countsVO.setWeeklyOutCount( rs.getLong("weekly_out_count") );
			                countsVO.setWeeklyOutValue( rs.getLong("weekly_out_value") );
			                countsVO.setMonthlyOutCount( rs.getLong("monthly_out_count") );
			                countsVO.setMonthlyOutValue( rs.getLong("monthly_out_value") );				
			                
							countsVO.setUnctrlDailyInCount( rs.getLong("outside_daily_in_count") );
			                countsVO.setUnctrlDailyInValue( rs.getLong("outside_daily_in_value") );
			                countsVO.setUnctrlWeeklyInCount( rs.getLong("outside_weekly_in_count") );
			                countsVO.setUnctrlWeeklyInValue( rs.getLong("outside_weekly_in_value") );
			                countsVO.setUnctrlMonthlyInCount( rs.getLong("outside_monthly_in_count") );
			                countsVO.setUnctrlMonthlyInValue( rs.getLong("outside_monthly_in_value") );
	
							countsVO.setUnctrlDailyOutCount( rs.getLong("outside_daily_out_count") );
			                countsVO.setUnctrlDailyOutValue( rs.getLong("outside_daily_out_value") );
			                countsVO.setUnctrlWeeklyOutCount( rs.getLong("outside_weekly_out_count") );
			                countsVO.setUnctrlWeeklyOutValue( rs.getLong("outside_weekly_out_value") );
			                countsVO.setUnctrlMonthlyOutCount( rs.getLong("outside_monthly_out_count") );
			                countsVO.setUnctrlMonthlyOutValue( rs.getLong("outside_monthly_out_value") );
							
							countsVO.setDailySubscriberOutCount( rs.getLong("daily_subscriber_out_count") );
			                countsVO.setDailySubscriberOutValue( rs.getLong("daily_subscriber_out_value") );
			                countsVO.setWeeklySubscriberOutCount( rs.getLong("weekly_subscriber_out_count") );
			                countsVO.setWeeklySubscriberOutValue( rs.getLong("weekly_subscriber_out_value") );
			                countsVO.setMonthlySubscriberOutCount( rs.getLong("monthly_subscriber_out_count") );
			                countsVO.setMonthlySubscriberOutValue( rs.getLong("monthly_subscriber_out_value") );
			               
							countsVO.setLastTransferDate(rs.getDate("last_transfer_date") );
							try{if (rs != null){rs.close();}} catch (Exception e){}
		                }
		                flag=true;
		        		if(countsVO == null)
		        		{
		        			flag = false;
		        			countsVO = new UserTransferCountsVO();
		        		}
		        		//If found then check for reset otherwise no need to check it
		        		if(flag)
		        			ChannelTransferBL.checkResetCountersAfterPeriodChange(countsVO,date);
		        		
						pstmtSelectProfileCounts.clearParameters();
						m=0;
		    			pstmtSelectProfileCounts.setString(++m,batchItemsVO.getTxnProfile());
		    			pstmtSelectProfileCounts.setString(++m,PretupsI.YES);
		    			pstmtSelectProfileCounts.setString(++m,p_batchMasterVO.getNetworkCode());
		    			pstmtSelectProfileCounts.setString(++m,PretupsI.PARENT_PROFILE_ID_CATEGORY);
		    			pstmtSelectProfileCounts.setString(++m,PretupsI.YES);
		    			rs=null;
		    			rs = pstmtSelectProfileCounts.executeQuery();
		     			//get the transfer profile counts
		    			if (rs.next())
		    			{
		    				transferProfileVO = new TransferProfileVO();
		    				transferProfileVO.setProfileId(rs.getString("profile_id"));
		    				transferProfileVO.setDailyInCount( rs.getLong("daily_transfer_in_count") );
		    				transferProfileVO.setDailyInValue( rs.getLong("daily_transfer_in_value"));
		    				transferProfileVO.setWeeklyInCount( rs.getLong("weekly_transfer_in_count") );
		    				transferProfileVO.setWeeklyInValue( rs.getLong("weekly_transfer_in_value"));
		    				transferProfileVO.setMonthlyInCount( rs.getLong("monthly_transfer_in_count") );
		    				transferProfileVO.setMonthlyInValue( rs.getLong("monthly_transfer_in_value"));
		    				try{if (rs != null){rs.close();}} catch (Exception e){}
		    			}
		                //(profile counts not found) if this condition is true then made entry in logs and leave this data.
		    			else
		    			{
		    				p_con.rollback();
		       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.transferprofilenotfound"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Transfer profile not found","Approval level = "+"No Approval required");
							continue;
		    			}
		    			if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferSubType())){
			                //(daily in count reach) if this condition is true then made entry in logs and leave this data.
			    	        if(transferProfileVO.getDailyInCount() <= countsVO.getDailyInCount())
			    			{
			    				p_con.rollback();
			       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.dailyincntreach"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Daily transfer in count reach","Approval level = "+"No Approval required");
								continue;
			    			}
			                //(daily in value reach) if this condition is true then made entry in logs and leave this data.
			    			//else if(transferProfileVO.getDailyInValue() < (countsVO.getDailyInValue() + batchItemsVO.getRequestedQuantity() )  )
			    			else if(transferProfileVO.getDailyInValue() < (countsVO.getDailyInValue() + channelTransferItemsVO.getProductTotalMRP() )  )
			    			{
			    				p_con.rollback();
			       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.dailyinvaluereach"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Daily transfer in value reach","Approval level = "+"No Approval required");
								continue;
			    			}
			                //(weekly in count reach) if this condition is true then made entry in logs and leave this data.
			    			else if(transferProfileVO.getWeeklyInCount() <=  countsVO.getWeeklyInCount() )
			    			{
			    				p_con.rollback();
			       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.weeklyincntreach"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Weekly transfer in count reach","Approval level = "+"No Approval required");
								continue;
			    			}
			                //(weekly in value reach) if this condition is true then made entry in logs and leave this data.
			    			//else if(transferProfileVO.getWeeklyInValue() < ( countsVO.getWeeklyInValue() + batchItemsVO.getRequestedQuantity() )  )
			    			else if(transferProfileVO.getWeeklyInValue() < ( countsVO.getWeeklyInValue() + channelTransferItemsVO.getProductTotalMRP() )  )
			    			{
			    				p_con.rollback();
			       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.weeklyinvaluereach"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Weekly transfer in value reach","Approval level = "+"No Approval required");
								continue;
			    			}
			                //(monthly in count reach) if this condition is true then made entry in logs and leave this data.
			    			else if(transferProfileVO.getMonthlyInCount() <=  countsVO.getMonthlyInCount()  )
			    			{
			    				p_con.rollback();
			       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.monthlyincntreach"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Monthly transfer in count reach","Approval level = "+"No Approval required");
								continue;
			    			}
			                //(mobthly in value reach) if this condition is true then made entry in logs and leave this data.
			    			//else if(transferProfileVO.getMonthlyInValue() < ( countsVO.getMonthlyInValue() + batchItemsVO.getRequestedQuantity() ) )
			    			else if(transferProfileVO.getMonthlyInValue() < ( countsVO.getMonthlyInValue() + channelTransferItemsVO.getProductTotalMRP() ) )
			    			{
			    				p_con.rollback();
			       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.monthlyinvaluereach"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : Monthly transfer in value reach","Approval level = "+"No Approval required");
								continue;
			    			}
		    			}
		    			countsVO.setUserID(channelUserVO.getUserID());
		    			if(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(batchItemsVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(batchItemsVO.getTransferSubType()))
			            {
		    				//countsVO.setDailyInCount(countsVO.getDailyInCount()-1);
			                //countsVO.setWeeklyInCount(countsVO.getWeeklyInCount()-1);
			                //countsVO.setMonthlyInCount(countsVO.getMonthlyInCount()-1);
	//		                countsVO.setDailyInValue(countsVO.getDailyInValue()+batchItemsVO.getRequestedQuantity());
	//		                countsVO.setWeeklyInValue(countsVO.getWeeklyInValue()+batchItemsVO.getRequestedQuantity());
	//		                countsVO.setMonthlyInValue(countsVO.getMonthlyInValue()+batchItemsVO.getRequestedQuantity());
			                countsVO.setDailyInValue(countsVO.getDailyInValue()-channelTransferItemsVO.getProductTotalMRP());
			                countsVO.setWeeklyInValue(countsVO.getWeeklyInValue()-channelTransferItemsVO.getProductTotalMRP());
			                countsVO.setMonthlyInValue(countsVO.getMonthlyInValue()-channelTransferItemsVO.getProductTotalMRP());
			            }
		    			else
		    			{
		    				countsVO.setDailyInCount(countsVO.getDailyInCount()+1);
			                countsVO.setWeeklyInCount(countsVO.getWeeklyInCount()+1);
			                countsVO.setMonthlyInCount(countsVO.getMonthlyInCount()+1);
	//		                countsVO.setDailyInValue(countsVO.getDailyInValue()+batchItemsVO.getRequestedQuantity());
	//		                countsVO.setWeeklyInValue(countsVO.getWeeklyInValue()+batchItemsVO.getRequestedQuantity());
	//		                countsVO.setMonthlyInValue(countsVO.getMonthlyInValue()+batchItemsVO.getRequestedQuantity());
			                countsVO.setDailyInValue(countsVO.getDailyInValue()+channelTransferItemsVO.getProductTotalMRP());
			                countsVO.setWeeklyInValue(countsVO.getWeeklyInValue()+channelTransferItemsVO.getProductTotalMRP());
			                countsVO.setMonthlyInValue(countsVO.getMonthlyInValue()+channelTransferItemsVO.getProductTotalMRP());
		    			}
		                countsVO.setLastInTime(date);
		        		countsVO.setLastTransferID(c2cTransferID);
		        		countsVO.setLastTransferDate(date);
		        		
	//	        		Update counts if found in db
		        		
		        		if(flag)
		        		{
		 			        m = 0 ;
		 					pstmtUpdateTransferCounts.clearParameters();
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getDailyInCount());
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getDailyInValue());
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getWeeklyInCount());
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getWeeklyInValue());
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getMonthlyInCount());
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getMonthlyInValue());
	
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getDailyOutCount());
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getDailyOutValue());
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getWeeklyOutCount());
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getWeeklyOutValue());
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getMonthlyOutCount());
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getMonthlyOutValue());
							
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlDailyInCount());
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlDailyInValue());
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlWeeklyInCount());
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlWeeklyInValue());
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlMonthlyInCount());
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlMonthlyInValue());
	
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlDailyOutCount());
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlDailyOutValue());
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlWeeklyOutCount());
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlWeeklyOutValue());
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlMonthlyOutCount());
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlMonthlyOutValue());
							
							pstmtUpdateTransferCounts.setLong(++m,countsVO.getDailySubscriberOutCount());
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getDailySubscriberOutValue());
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getWeeklySubscriberOutCount());
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getWeeklySubscriberOutValue());
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getMonthlySubscriberOutCount());
		        	        pstmtUpdateTransferCounts.setLong(++m,countsVO.getMonthlySubscriberOutValue());
							
							pstmtUpdateTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(countsVO.getLastInTime()));
		        	        pstmtUpdateTransferCounts.setString(++m,countsVO.getLastTransferID());
		        	        pstmtUpdateTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(countsVO.getLastTransferDate()));
		        	        pstmtUpdateTransferCounts.setString(++m,countsVO.getUserID());
		        	        updateCount = pstmtUpdateTransferCounts.executeUpdate();
		        		}
		        		//Insert counts if not found in db
		        		else
		        		{
		        			m = 0 ;
		 					pstmtInsertTransferCounts.clearParameters();
		         	        pstmtInsertTransferCounts.setLong(++m,countsVO.getDailyInCount());
		        	        pstmtInsertTransferCounts.setLong(++m,countsVO.getDailyInValue());
		        	        pstmtInsertTransferCounts.setLong(++m,countsVO.getWeeklyInCount());
		        	        pstmtInsertTransferCounts.setLong(++m,countsVO.getWeeklyInValue());
		        	        pstmtInsertTransferCounts.setLong(++m,countsVO.getMonthlyInCount());
		        	        pstmtInsertTransferCounts.setLong(++m,countsVO.getMonthlyInValue());
		        	        pstmtInsertTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(countsVO.getLastInTime()));
		        	        pstmtInsertTransferCounts.setString(++m,countsVO.getLastTransferID());
		        	        pstmtInsertTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(countsVO.getLastTransferDate()));
		        	        pstmtInsertTransferCounts.setString(++m,countsVO.getUserID());
		        	        updateCount = pstmtInsertTransferCounts.executeUpdate();
		        		}
		                //(record not updated properly) if this condition is true then made entry in logs and leave this data.
		                if(updateCount <= 0  )
		    			{
		                	p_con.rollback();
		       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
							errorList.add(errorVO);
							if(flag)
								BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB error while insert user trasnfer counts","Approval level = "+"No Approval required");
							else
								BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB error while uptdate user trasnfer counts","Approval level = "+"No Approval required");
							continue;
		   		 		}
		        		//}
		           
	                channelTransferVO.setCanceledOn(batchItemsVO.getCancelledOn());
		            channelTransferVO.setCanceledBy(batchItemsVO.getCancelledBy());
		            channelTransferVO.setChannelRemarks(batchItemsVO.getInitiatorRemarks());
		            channelTransferVO.setCommProfileSetId(batchItemsVO.getCommissionProfileSetId());
		            channelTransferVO.setCommProfileVersion(batchItemsVO.getCommissionProfileVer());
		            channelTransferVO.setCreatedBy(p_batchMasterVO.getCreatedBy());
		            channelTransferVO.setCreatedOn(p_batchMasterVO.getCreatedOn());
		            channelTransferVO.setDomainCode(p_batchMasterVO.getDomainCode());
		            channelTransferVO.setFinalApprovedBy(batchItemsVO.getApprovedBy());
		            channelTransferVO.setFirstApprovedOn(batchItemsVO.getApprovedOn());
		            channelTransferVO.setFirstApproverLimit(0);
		            channelTransferVO.setFirstApprovalRemark(batchItemsVO.getApproverRemarks());
		            channelTransferVO.setSecondApprovalLimit(0);
		            //channelTransferVO.setCategoryCode(p_senderVO.getCategoryCode());
		            channelTransferVO.setBatchNum(batchItemsVO.getBatchId());
		            channelTransferVO.setOptBatchNum(batchItemsVO.getOptBatchId());
		            channelTransferVO.setBatchDate(p_batchMasterVO.getBatchDate());
		            channelTransferVO.setPayableAmount(channelTransferItemsVO.getPayableAmount());
		    		channelTransferVO.setNetPayableAmount(channelTransferItemsVO.getNetPayableAmount());
		    		channelTransferVO.setPayInstrumentAmt(0);
		    		//channelTransferVO.setModifiedBy(p_senderVO.getUserID());
		    		channelTransferVO.setModifiedBy(p_batchMasterVO.getModifiedBy());
		    		channelTransferVO.setModifiedOn(date);
		    		channelTransferVO.setProductType(p_batchMasterVO.getProductType());
		    		channelTransferVO.setReceiverCategoryCode(batchItemsVO.getCategoryCode());
		    		channelTransferVO.setReceiverGradeCode(batchItemsVO.getGradeCode());
		    		channelTransferVO.setReceiverTxnProfile(batchItemsVO.getTxnProfile());
		    		channelTransferVO.setReferenceNum(batchItemsVO.getBatchDetailId());	    		
		    		channelTransferVO.setDefaultLang(p_sms_default_lang);
		    		channelTransferVO.setSecondLang(p_sms_second_lang);	    		
					// for balance logger
					channelTransferVO.setReferenceID(network_id);
					//ends here
					if(messageGatewayVO!=null && messageGatewayVO.getRequestGatewayVO()!=null)
					{
						channelTransferVO.setRequestGatewayCode(messageGatewayVO.getRequestGatewayVO().getGatewayCode());
						channelTransferVO.setRequestGatewayType(messageGatewayVO.getGatewayType());
					}
					channelTransferVO.setRequestedQuantity(batchItemsVO.getRequestedQuantity());
					channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);
					channelTransferVO.setStatus(batchItemsVO.getStatus());
		            channelTransferVO.setTotalTax1(channelTransferItemsVO.getTax1Value());
		            channelTransferVO.setTotalTax2(channelTransferItemsVO.getTax2Value());
		            channelTransferVO.setTotalTax3(channelTransferItemsVO.getTax3Value());
		            channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_TRANSFER);
		            channelTransferVO.setTransferDate(p_batchMasterVO.getCreatedOn());
		            //channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
		            channelTransferVO.setTransferSubType(batchItemsVO.getTransferSubType());
		            channelTransferVO.setTransferID(c2cTransferID);
		            channelTransferVO.setTransferInitatedBy(p_batchMasterVO.getUserId());
		            //channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
		            channelTransferVO.setTransferType(batchItemsVO.getTransferType());
		            channelTransferVO.setType(PretupsI.CHANNEL_TYPE_C2C);
		            //channelTransferVO.setTransferMRP(batchItemsVO.getTransferMrp());
		            channelTransferVO.setTransferMRP(channelTransferItemsVO.getProductTotalMRP());
		            //modified by vikram.
		            
		            if(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(batchItemsVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(batchItemsVO.getTransferSubType()))
		            {
		            	channelTransferVO.setToUserID(p_batchMasterVO.getUserId());
		            	channelTransferVO.setFromUserID(channelUserVO.getUserID());
		            	channelTransferVO.setFromUserCode(batchItemsVO.getMsisdn());
		            	channelTransferVO.setToUserCode(p_senderVO.getMsisdn());
		            	channelTransferVO.setSenderGradeCode(batchItemsVO.getUserGradeCode());
		            	channelTransferVO.setCategoryCode(batchItemsVO.getCategoryCode());
		            	channelTransferVO.setSenderTxnProfile(batchItemsVO.getTxnProfile());
		            	channelTransferVO.setReceiverCategoryCode(p_senderVO.getCategoryCode());
			    		channelTransferVO.setReceiverGradeCode(p_senderVO.getUserGrade());
			    		channelTransferVO.setReceiverTxnProfile(p_senderVO.getTransferProfileID());
			    		channelTransferVO.setGraphicalDomainCode(channelUserVO.getGeographicalCode());
			    		channelTransferVO.setReceiverGgraphicalDomainCode(p_senderVO.getGeographicalCode());

		            }
		            else
		            {	//FOR the transfer/return
		            	channelTransferVO.setToUserID(channelUserVO.getUserID());
		            	channelTransferVO.setFromUserID(p_batchMasterVO.getUserId());
		            	channelTransferVO.setFromUserCode(p_senderVO.getMsisdn());
		            	channelTransferVO.setToUserCode(batchItemsVO.getMsisdn());
		            	channelTransferVO.setSenderGradeCode(p_senderVO.getUserGrade());
		            	channelTransferVO.setCategoryCode(p_senderVO.getCategoryCode());
		            	channelTransferVO.setSenderTxnProfile(p_senderVO.getTransferProfileID());
		            	channelTransferVO.setReceiverCategoryCode(batchItemsVO.getCategoryCode());
			    		channelTransferVO.setReceiverGradeCode(batchItemsVO.getUserGradeCode());
			    		channelTransferVO.setReceiverTxnProfile(batchItemsVO.getTxnProfile());
			    		channelTransferVO.setGraphicalDomainCode(p_senderVO.getGeographicalCode());
			    		channelTransferVO.setReceiverGgraphicalDomainCode(channelUserVO.getGeographicalCode());

		            }
		            

		            channelTransferItemsVO.setProductCode(p_batchMasterVO.getProductCode());
		            channelTransferItemsVO.setReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
					
		            channelTransferItemsVO.setRequiredQuantity(batchItemsVO.getRequestedQuantity());
		            channelTransferItemsVO.setSerialNum(1);

		            channelTransferItemsVO.setTransferID(c2cTransferID);
		           // channelTransferItemsVO.setUnitValue(p_batchMasterVO.getProductMrp());
					// for the balance logger
					channelTransferItemsVO.setAfterTransReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
					
					
		            //ends here
					channelTransferItemVOList=new ArrayList();
		            channelTransferItemVOList.add(channelTransferItemsVO);
		            channelTransferItemsVO.setShortName(p_batchMasterVO.getProductShortName());
		            //channelTransferVO.setChannelTransferitemsVOList(channelTransferItemVOList);
		            if (_log.isDebugEnabled()) _log.debug(methodName, "Exiting: channelTransferVO=" + channelTransferVO.toString());
		            if (_log.isDebugEnabled()) _log.debug(methodName, "Exiting: channelTransferItemsVO=" + channelTransferItemsVO.toString());
		            
		            
		            //for positive commission deduct from network stock
		           
		            final boolean debit=true;
		    		if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.POSITIVE_COMM_APPLY))).booleanValue() && PretupsI.CHANNEL_TRANSFER_TYPE_TRANSFER.equals(channelTransferVO.getTransferType()))
		    		{
		    			ChannelTransferBL.prepareNetworkStockListAndCreditDebitStockForCommision(p_con,channelTransferVO,channelTransferVO.getFromUserID(),date, debit);
		    			ChannelTransferBL.updateNetworkStockTransactionDetailsForCommision(p_con,channelTransferVO,channelTransferVO.getFromUserID(),date);
		    		}
		            
		    		
		    		//added by vikram
		    		if(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(batchItemsVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(batchItemsVO.getTransferSubType()))
		            {
		    			channelTransferItemsVO.setSenderPreviousStock(previousUserBalToBeSetChnlTrfItems);
		    			channelTransferItemsVO.setAfterTransSenderPreviousStock(previousUserBalToBeSetChnlTrfItems);
		    			channelTransferItemsVO.setReceiverPreviousStock(senderPreviousBal);
		    			channelTransferItemsVO.setAfterTransReceiverPreviousStock(senderPreviousBal);
		            }
		            else
		            {	//FOR the transfer/return
		            	channelTransferItemsVO.setSenderPreviousStock(senderPreviousBal);
		            	channelTransferItemsVO.setAfterTransSenderPreviousStock(senderPreviousBal);
		            	channelTransferItemsVO.setReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
		            	channelTransferItemsVO.setAfterTransReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
		            }
		    		
		            m = 0;
					pstmtInsertIntoChannelTranfers.clearParameters();
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getCanceledBy());
	            	pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getCanceledOn()));
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getChannelRemarks());
	            	pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getCommProfileSetId());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getCommProfileVersion());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getCreatedBy());
	            	pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getCreatedOn()));
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getDomainCode());
	            	pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getExternalTxnDate()));
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getExternalTxnNum());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getFinalApprovedBy());
	            	pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getFirstApprovedOn()));
	            	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getFirstApproverLimit());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getFirstApprovalRemark());
	            	pstmtInsertIntoChannelTranfers.setDate(++m,BTSLUtil.getSQLDateFromUtilDate(channelTransferVO.getBatchDate()));
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getBatchNum());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getOptBatchNum());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getFromUserID());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getGraphicalDomainCode());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getModifiedBy());
	            	pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getModifiedOn()));
	            	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getNetPayableAmount());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getNetworkCode());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getNetworkCodeFor());
	            	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getPayableAmount());
	            	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getPayInstrumentAmt());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getProductType());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getReceiverCategoryCode());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getReceiverGradeCode());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getReceiverTxnProfile());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getReferenceNum());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getRequestGatewayCode());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getRequestGatewayType());
	            	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getRequestedQuantity());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getSecondApprovedBy());
	            	pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getSecondApprovedOn()));
	            	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getSecondApprovalLimit());
	            	//pstmtInsertIntoChannelTranfers.setFormOfUse(++m, OraclePreparedStatement.FORM_NCHAR);//commented for DB2 
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getSecondApprovalRemark());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getSource());
	            	pstmtInsertIntoChannelTranfers.setString(++m,batchItemsVO.getStatus());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getThirdApprovedBy());
	            	pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getThirdApprovedOn()));
	            	//pstmtInsertIntoChannelTranfers.setFormOfUse(++m, OraclePreparedStatement.FORM_NCHAR);//commented for DB2 
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getThirdApprovalRemark());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getToUserID());
	            	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getTotalTax1());
	            	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getTotalTax2());
	            	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getTotalTax3());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getTransferCategory());
	            	pstmtInsertIntoChannelTranfers.setDate(++m,BTSLUtil.getSQLDateFromUtilDate(channelTransferVO.getTransferDate()));
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getTransferID());
	            	//pstmtInsertIntoChannelTranfers.setString(++m,p_senderVO.getUserID());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getTransferInitatedBy());
	            	pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getTransferMRP());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getTransferSubType());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getTransferType());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getType());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getCategoryCode());
					pstmtInsertIntoChannelTranfers.setString(++m,PretupsI.YES);
					pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getToUserCode());
					pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getDomainCode());
						
					// to geographical domain also inserted as the geogrpahical domain that will help in reports
					pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getGraphicalDomainCode());
					
					//pstmtInsertIntoChannelTranfers.setFormOfUse(++m, OraclePreparedStatement.FORM_NCHAR);//commented for DB2 
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getDefaultLang());
	            	//pstmtInsertIntoChannelTranfers.setFormOfUse(++m, OraclePreparedStatement.FORM_NCHAR);//commented for DB2 
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getSecondLang());
	            	pstmtInsertIntoChannelTranfers.setString(++m,p_batchMasterVO.getCreatedBy());
	            	//added by vikram
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getSenderGradeCode());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getSenderTxnProfile());
	            	pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getFromUserCode());
	            	//Added for inserting the other commision profile set ID
	            	pstmtInsertIntoChannelTranfers.setString(++m,((ChannelTransferItemsVO)channelTransferVO.getChannelTransferitemsVOList().get(0)).getOthCommSetId());
					//ends here
	            	//insert into channel transfer table
	            	updateCount=pstmtInsertIntoChannelTranfers.executeUpdate();
	                //(record not updated properly) if this condition is true then made entry in logs and leave this data.
	            	if(updateCount<=0)
	            	{
	            		p_con.rollback();
	       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB Error while inserting in channel transfer table","Approval level = "+"No Approval required"+", updateCount="+updateCount);
						continue;
	            	}
	            	
	            	m=0;
	            	pstmtInsertIntoChannelTransferItems.clearParameters();
	            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getApprovedQuantity());
	            	pstmtInsertIntoChannelTransferItems.setString(++m,channelTransferItemsVO.getCommProfileDetailID());
	            	pstmtInsertIntoChannelTransferItems.setDouble(++m,channelTransferItemsVO.getCommRate());
	            	pstmtInsertIntoChannelTransferItems.setString(++m,channelTransferItemsVO.getCommType());
	            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getCommValue());
	            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getProductTotalMRP());
	            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getNetPayableAmount());
	            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getPayableAmount());
	            	pstmtInsertIntoChannelTransferItems.setString(++m,channelTransferItemsVO.getProductCode());
	            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getReceiverPreviousStock());
	            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getRequiredQuantity());
	            	pstmtInsertIntoChannelTransferItems.setInt(++m,channelTransferItemsVO.getSerialNum());
	            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getSenderPreviousStock());
	            	//pstmtInsertIntoChannelTransferItems.setLong(++m,senderPreviousBal);
	            	
	            	pstmtInsertIntoChannelTransferItems.setDouble(++m,channelTransferItemsVO.getTax1Rate());
	            	pstmtInsertIntoChannelTransferItems.setString(++m,channelTransferItemsVO.getTax1Type());
	            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getTax1Value());
	            	pstmtInsertIntoChannelTransferItems.setDouble(++m,channelTransferItemsVO.getTax2Rate());
	            	pstmtInsertIntoChannelTransferItems.setString(++m,channelTransferItemsVO.getTax2Type());
	            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getTax2Value());
	            	pstmtInsertIntoChannelTransferItems.setDouble(++m,channelTransferItemsVO.getTax3Rate());
	            	pstmtInsertIntoChannelTransferItems.setString(++m,channelTransferItemsVO.getTax3Type());
	            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getTax3Value());
	            	pstmtInsertIntoChannelTransferItems.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
	            	pstmtInsertIntoChannelTransferItems.setString(++m,channelTransferItemsVO.getTransferID());
	            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getUnitValue());
	            	
	            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getSenderDebitQty());
	            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getReceiverCreditQty());
	            	//added by vikram
		    		if(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(batchItemsVO.getTransferType()) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(batchItemsVO.getTransferSubType()))
		            {
		    			pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getSenderPreviousStock()-channelTransferItemsVO.getSenderDebitQty());
		            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getReceiverPreviousStock()+channelTransferItemsVO.getReceiverCreditQty());
		            }
		            else
		            {	//FOR the transfer/return
		            	pstmtInsertIntoChannelTransferItems.setLong(++m,senderPreviousBal-channelTransferItemsVO.getSenderDebitQty());
		            	pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemsVO.getReceiverPreviousStock()+channelTransferItemsVO.getReceiverCreditQty());
		            }
		    		pstmtInsertIntoChannelTransferItems.setLong(++m, channelTransferItemsVO.getCommQuantity());
		    		pstmtInsertIntoChannelTransferItems.setString(++m, channelTransferItemsVO.getOthCommType());
		    		pstmtInsertIntoChannelTransferItems.setDouble(++m, channelTransferItemsVO.getOthCommRate());
		    		pstmtInsertIntoChannelTransferItems.setLong(++m, channelTransferItemsVO.getOthCommValue());
	            	//insert into channel transfer items table
	            	updateCount=pstmtInsertIntoChannelTransferItems.executeUpdate();
	                //(record not updated properly) if this condition is true then made entry in logs and leave this data.
	            	if(updateCount<=0)
	            	{
	            		p_con.rollback();
	       		 		errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB Error while inserting in channel transfer items table","Approval level = "+"No Approval required"+", updateCount="+updateCount);
						continue;
	            	}
	            	//commit the transaction after processing each record
	//            	 insert items data here
					index=0;
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getBatchId());
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getBatchDetailId());
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getCategoryCode());
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getMsisdn());
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getUserId());
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getStatus());
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getModifiedBy());
					pstmtInsertBatchItems.setTimestamp(++index,BTSLUtil.getTimestampFromUtilDate(batchItemsVO.getModifiedOn()));
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getUserGradeCode());
					pstmtInsertBatchItems.setDate(++index,BTSLUtil.getSQLDateFromUtilDate(batchItemsVO.getTransferDate()));
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getTxnProfile());
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getCommissionProfileSetId());
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getCommissionProfileVer());
					pstmtInsertBatchItems.setString(++index,channelTransferItemsVO.getCommProfileDetailID());
					pstmtInsertBatchItems.setString(++index,channelTransferItemsVO.getCommType());
					pstmtInsertBatchItems.setDouble(++index,channelTransferItemsVO.getCommRate());
					pstmtInsertBatchItems.setLong(++index,channelTransferItemsVO.getCommValue());
					pstmtInsertBatchItems.setString(++index,channelTransferItemsVO.getTax1Type());
					pstmtInsertBatchItems.setDouble(++index,channelTransferItemsVO.getTax1Rate());
					pstmtInsertBatchItems.setLong(++index,channelTransferItemsVO.getTax1Value());
					pstmtInsertBatchItems.setString(++index,channelTransferItemsVO.getTax2Type());
					pstmtInsertBatchItems.setDouble(++index,channelTransferItemsVO.getTax2Rate());
					pstmtInsertBatchItems.setLong(++index,channelTransferItemsVO.getTax2Value());
					pstmtInsertBatchItems.setString(++index,channelTransferItemsVO.getTax3Type());
					pstmtInsertBatchItems.setDouble(++index,channelTransferItemsVO.getTax3Rate());
					pstmtInsertBatchItems.setLong(++index,channelTransferItemsVO.getTax3Value());
					pstmtInsertBatchItems.setString(++index,String.valueOf(channelTransferItemsVO.getRequiredQuantity()));
					pstmtInsertBatchItems.setLong(++index,channelTransferItemsVO.getProductTotalMRP());
					//pstmtInsertBatchItems.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);//commented for DB2 
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getInitiatorRemarks());
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getExternalCode());
					pstmtInsertBatchItems.setString(++index,PretupsI.CHANNEL_TRANSFER_BATCH_C2C_ITEM_RCRDSTATUS_PROCESSED);
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getTransferType());
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getTransferSubType());
					pstmtInsertBatchItems.setString(++index,batchItemsVO.getOptBatchId());
					queryExecutionCount=pstmtInsertBatchItems.executeUpdate();
					if(queryExecutionCount<=0)
					{
					    p_con.rollback();
					    //put error record can not be inserted
					    _log.error(methodName, "Record cannot be inserted in batch items table");
						BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"FAIL : DB Error Record cannot be inserted in batch items table","queryExecutionCount="+queryExecutionCount);
					}
					else
					{
					    p_con.commit();
					    totalSuccessRecords++;
					    // put success in the logger file.
						BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"PASS : Record inserted successfully in batch items table","queryExecutionCount="+queryExecutionCount);
					}
					//ends here
	            	BatchC2CFileProcessLog.operatorDetailLog(methodName,p_batchMasterVO,batchItemsVO,"PASS : Order is closed successfully","Approval level = "+"No Approval required"+", updateCount="+updateCount);
	            	//made entry in network stock and balance logger
	            	ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);
					pstmtSelectBalanceInfoForMessage.clearParameters();
					m=0;
	            	pstmtSelectBalanceInfoForMessage.setString(++m, channelUserVO.getUserID());
	            	pstmtSelectBalanceInfoForMessage.setString(++m, p_batchMasterVO.getNetworkCode());
	            	pstmtSelectBalanceInfoForMessage.setString(++m, p_batchMasterVO.getNetworkCodeFor());
	                rs=null;
	            	rs = pstmtSelectBalanceInfoForMessage.executeQuery();
	                userbalanceList= new ArrayList();
	                while (rs.next())
	                {
	                    balancesVO = new UserBalancesVO();
	                    balancesVO.setProductCode(rs.getString("product_code"));
	                    balancesVO.setBalance(rs.getLong("balance"));
	    				balancesVO.setProductShortCode(rs.getString("product_short_code"));
	    				balancesVO.setProductShortName(rs.getString("short_name"));
	    				userbalanceList.add(balancesVO);
	                }
					try{if (rs != null){rs.close();}} catch (Exception e){}
	//				generate the message arguments to be send in SMS
	                keyArgumentVO = new KeyArgumentVO();
	    			argsArr = new String[2];
	    			argsArr[1] = PretupsBL.getDisplayAmount(channelTransferItemsVO.getRequiredQuantity());
	    			argsArr[0] = String.valueOf(channelTransferItemsVO.getShortName());
	    			keyArgumentVO.setKey(PretupsErrorCodesI.C2C_CHNL_CHNL_TRANSFER_SMS2);
	    			keyArgumentVO.setArguments(argsArr);
	    			txnSmsMessageList=new ArrayList();
	    			balSmsMessageList=new ArrayList();
	    			txnSmsMessageList.add(keyArgumentVO);
	    			for(int index1=0,n=userbalanceList.size();index1<n;index1++)
	    			{
	    				balancesVO=(UserBalancesVO)userbalanceList.get(index1);
	    				if(balancesVO.getProductCode().equals(channelTransferItemsVO.getProductCode()))
	    				{
	    					argsArr=new String[2];
	    					argsArr[1]=balancesVO.getBalanceAsString();
	    					argsArr[0]=balancesVO.getProductShortName();
	    					keyArgumentVO = new KeyArgumentVO();
	    					keyArgumentVO.setKey(PretupsErrorCodesI.C2C_CHNL_CHNL_TRANSFER_SMS_BALSUBKEY);
	    					keyArgumentVO.setArguments(argsArr);
	    					balSmsMessageList.add(keyArgumentVO);
	    					break;
	    				}
	    			}
	    			locale=new Locale(language,country);
	    			String c2cNotifyMsg=null;
	    			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_SMS_NOTIFY))).booleanValue())
	    			{
	    				LocaleMasterVO localeVO=LocaleMasterCache.getLocaleDetailsFromlocale(locale);
	    				if(PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage()))
	    					c2cNotifyMsg=channelTransferVO.getDefaultLang();
	    				else
	    					c2cNotifyMsg=channelTransferVO.getSecondLang();
	    				array=new String[] {channelTransferVO.getTransferID(),BTSLUtil.getMessage(locale,txnSmsMessageList),BTSLUtil.getMessage(locale,balSmsMessageList),c2cNotifyMsg};
	    			}  			
	    				
	    			if(c2cNotifyMsg==null)
	    				array=new String[] {channelTransferVO.getTransferID(),BTSLUtil.getMessage(locale,txnSmsMessageList),BTSLUtil.getMessage(locale,balSmsMessageList)};
	    			
	    			messages=new BTSLMessages(PretupsErrorCodesI.C2C_CHNL_CHNL_TRANSFER_SMS1,array);
	                pushMessage=new PushMessage(batchItemsVO.getMsisdn(),messages,channelTransferVO.getTransferID(),null,locale,channelTransferVO.getNetworkCode()); 
	                //push SMS
	                pushMessage.push();
					
	            	
		        		
						
					}// for loop for the batch items
				}//End of  Sender loop
			} 
			catch(BTSLBaseException be)
			{
				throw be;
			}
			catch (SQLException sqe)
			{
				try{if(p_con!=null)p_con.rollback();}catch(Exception e){}
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[closeBatchC2CTransfer]","","","","SQL Exception:"+sqe.getMessage());
				BatchC2CFileProcessLog.c2cBatchMasterLog(methodName,p_batchMasterVO,"FAIL : SQL Exception:"+sqe.getMessage(),"TOTAL SUCCESS RECORDS = "+totalSuccessRecords);
				throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
			}
			catch (Exception ex)
			{
				try{if(p_con!=null)p_con.rollback();}catch(Exception e){}
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[closeBatchC2CTransfer]","","","","Exception:"+ex.getMessage());
				BatchC2CFileProcessLog.c2cBatchMasterLog(methodName,p_batchMasterVO,"FAIL : Exception:"+ex.getMessage(),"TOTAL SUCCESS RECORDS = "+totalSuccessRecords);
				throw new BTSLBaseException(this, methodName, "error.general.processing");
			}
			finally
			{
			    
			    try{if (rsSelectTrfRule != null){rsSelectTrfRule.close();}} catch (Exception e){}
				try{if (pstmtSelectTrfRule != null){pstmtSelectTrfRule.close();}} catch (Exception e){}
			    try{if (rsSelectTrfRuleProd != null){rsSelectTrfRuleProd.close();}} catch (Exception e){}
				try{if (pstmtSelectTrfRuleProd != null){pstmtSelectTrfRuleProd.close();}} catch (Exception e){}
			    try{if (rsSelectCProfileProd != null){rsSelectCProfileProd.close();}} catch (Exception e){}
				try{if (pstmtSelectCProfileProd != null){pstmtSelectCProfileProd.close();}} catch (Exception e){}
				try{if (rsSelectCProfileProdDetail != null){rsSelectCProfileProdDetail.close();}} catch (Exception e){}
				try{if (pstmtSelectCProfileProdDetail != null){pstmtSelectCProfileProdDetail.close();}} catch (Exception e){}
			    try{if (rsSelectTProfileProd != null){rsSelectTProfileProd.close();}} catch (Exception e){}
				try{if (pstmtSelectTProfileProd != null){pstmtSelectTProfileProd.close();}} catch (Exception e){}
				try{if (pstmtInsertBatchMaster != null){pstmtInsertBatchMaster.close();}} catch (Exception e){}
				try{if (pstmtInsertBatchItems != null){pstmtInsertBatchItems.close();}} catch (Exception e){}
				try{if (pstmtLoadUser != null){pstmtLoadUser.close();}} catch (Exception e){}
			    try{if (pstmtSelectUserBalances!=null){pstmtSelectUserBalances.close();}} catch (Exception e){}
			    try{if (pstmtUpdateUserBalances!=null){pstmtUpdateUserBalances.close();}} catch (Exception e){}
			    try{if (pstmtInsertUserDailyBalances!=null){pstmtInsertUserDailyBalances.close();}} catch (Exception e){}
			    try{if (pstmtSelectBalance!=null){pstmtSelectBalance.close();}} catch (Exception e){}
			    try{if (pstmtUpdateBalance!=null){pstmtUpdateBalance.close();}} catch (Exception e){}
			    try{if (pstmtInsertBalance!=null){pstmtInsertBalance.close();}} catch (Exception e){}
			    try{if (pstmtSelectTransferCounts!=null){pstmtSelectTransferCounts.close();}} catch (Exception e){}
			    try{if (pstmtSelectProfileCounts!=null){pstmtSelectProfileCounts.close();}} catch (Exception e){}
			    try{if (pstmtUpdateTransferCounts!=null){pstmtUpdateTransferCounts.close();}} catch (Exception e){}
			    try{if (pstmtInsertTransferCounts!=null){pstmtInsertTransferCounts.close();}} catch (Exception e){}
			    try{if (pstmtLoadTransferProfileProduct !=null){pstmtLoadTransferProfileProduct.close();}} catch (Exception e){}
			    try{if (handlerStmt != null){handlerStmt.close();}} catch (Exception e){}
			    try{if (pstmtInsertIntoChannelTransferItems!= null){pstmtInsertIntoChannelTransferItems.close();}} catch (Exception e){}
				try{if (pstmtInsertIntoChannelTranfers!= null){pstmtInsertIntoChannelTranfers.close();}} catch (Exception e){}
				try{if (pstmtSelectBalanceInfoForMessage!= null){pstmtSelectBalanceInfoForMessage.close();}} catch (Exception e){}
				try{if (psmtInsertUserThreshold!= null){psmtInsertUserThreshold.close();}} catch (Exception e){}
				
				try
				{
					// if all records contains errors then rollback the master table entry
					if(errorList!=null &&(errorList.size()==p_batchItemsList.size()))
					{
						p_con.rollback();
						_log.error(methodName, "ALL the records conatins errors and cannot be inserted in db");
						BatchC2CFileProcessLog.c2cBatchMasterLog(methodName,p_batchMasterVO,"FAIL : ALL the records conatins errors and cannot be inserted in DB ","");
					}
					//else update the master table with the open status and total number of records.
					else
					{
						int index=0;
						int queryExecutionCount=-1;
						pstmtUpdateBatchMaster.setInt(++index,p_batchMasterVO.getBatchTotalRecord()-errorList.size());
						pstmtUpdateBatchMaster.setString(++index,PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_CLOSE);
						pstmtUpdateBatchMaster.setString(++index,p_batchMasterVO.getBatchId());
						pstmtUpdateBatchMaster.setString(++index,p_batchMasterVO.getOptBatchId());
						queryExecutionCount=pstmtUpdateBatchMaster.executeUpdate();
					    if(queryExecutionCount<=0) //Means No Records Updated
			   		    {
			   		        _log.error(methodName,"Unable to Update the batch size in master table..");
			   		        p_con.rollback();
							EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorC2CBatchTransferDAO[closeBatchC2CTransfer]","","","","Error while updating C2C_BATCHES table. Batch id="+p_batchMasterVO.getBatchId());
			   		    }
			   		    else
			   		    {
			   		        p_con.commit();
			   		    }
			   		}

				}
				catch(Exception e)
				{
					try{if(p_con!=null)p_con.rollback();}catch(Exception ex){}
				}
				try{if (pstmtUpdateBatchMaster != null){pstmtUpdateBatchMaster.close();}} catch (Exception e){}
				//OneLineTXNLog.log(channelTransferVO);
				if (_log.isDebugEnabled()) _log.debug(methodName, "Exiting: errorList.size()=" + errorList.size());
			}
			return errorList;
		}
		
	    public LinkedHashMap loadUserListForC2CXfrOpt(Connection p_con,String p_txnType,ChannelTransferRuleVO p_channelTransferRuleVO,String p_toCategoryCode,String p_userName,ChannelUserVO p_channelUserVO) throws BTSLBaseException
		{
	    	final String methodName="loadUserListForC2CXfrOpt";
			if (_log.isDebugEnabled())
				_log.debug(methodName,"Entered p_txnType="+p_txnType+", ToCategoryCode: "+p_toCategoryCode+" User Name: "+p_userName+",p_channelTransferRuleVO="+p_channelTransferRuleVO+",p_channelUserVO="+p_channelUserVO);
			LinkedHashMap linkedHashMap = new LinkedHashMap();
			boolean uncontrollAllowed=false;
			boolean fixedLevelParent=false;
			boolean fixedLevelHierarchy=false;
			String fixedCatStr=null;
			boolean directAllowed=false;
			boolean chnlByPassAllowed=false;
			String unctrlLevel=null;
			String ctrlLevel=null;
			// if txn is for transfer then get the value of the transfer paramenters
			if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_txnType))
			{
				if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_PARENT.equals(p_channelTransferRuleVO.getFixedTransferLevel()))
				{
					fixedLevelParent=true;
					fixedCatStr=getCategoryStrValue(p_channelTransferRuleVO.getFixedTransferCategory());
				}
				else if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_HIERARCHY.equals(p_channelTransferRuleVO.getFixedTransferLevel()))
				{
					fixedLevelHierarchy=true;
					fixedCatStr=getCategoryStrValue(p_channelTransferRuleVO.getFixedTransferCategory());
				}
				if(PretupsI.YES.equals(p_channelTransferRuleVO.getDirectTransferAllowed()))
					directAllowed=true;
				if(PretupsI.YES.equals(p_channelTransferRuleVO.getTransferChnlBypassAllowed()))
					chnlByPassAllowed=true;
				if (PretupsI.YES.equals(p_channelTransferRuleVO.getUncntrlTransferAllowed()))
				{
					uncontrollAllowed=true;
					unctrlLevel=p_channelTransferRuleVO.getUncntrlTransferLevel();
				}
				ctrlLevel=p_channelTransferRuleVO.getCntrlTransferLevel();
			}
			//else if txn is for withdraw then get the value of the withdraw paramenters
			else //if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equals(p_txnType))
			{
				if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_PARENT.equals(p_channelTransferRuleVO.getFixedWithdrawLevel()))
				{
					fixedLevelParent=true;
					fixedCatStr=getCategoryStrValue(p_channelTransferRuleVO.getFixedWithdrawCategory());
				}
				else if (PretupsI.CHANNEL_TRANSFER_FIXED_LEVEL_HIERARCHY.equals(p_channelTransferRuleVO.getFixedWithdrawLevel()))
				{
					fixedLevelHierarchy=true;
					fixedCatStr=getCategoryStrValue(p_channelTransferRuleVO.getFixedWithdrawCategory());
				}
				if(PretupsI.YES.equals(p_channelTransferRuleVO.getWithdrawAllowed()))
					directAllowed=true;
				if(PretupsI.YES.equals(p_channelTransferRuleVO.getWithdrawChnlBypassAllowed()))
					chnlByPassAllowed=true;
				if (PretupsI.YES.equals(p_channelTransferRuleVO.getUncntrlWithdrawAllowed()))
				{
					uncontrollAllowed=true;
					unctrlLevel=p_channelTransferRuleVO.getUncntrlWithdrawLevel();
				}
				ctrlLevel=p_channelTransferRuleVO.getCntrlWithdrawLevel();
			}

	         // to load the user list we will have to apply the check of the fixed level and fixed category in each
	         // and every case.
	         // Now we divide the whole conditions in various sub conditions as
	        
			if(uncontrollAllowed)
			{
				if(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM.equals(unctrlLevel)||PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAINTYPE.equals(unctrlLevel)
					|| PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAIN.equals(unctrlLevel))
				{
					if(BTSLUtil.isNullString(fixedCatStr))
					{
						// load all the users form the system without any check of the fixed category
						linkedHashMap=loadUsersOutsideHireacrhy(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_userName,p_channelUserVO.getUserID());
						return linkedHashMap;
					}// fixed category null check
					else if(fixedLevelHierarchy)
					{
						// load all the users form the system, which are in the hierarchy of the users of fixedCatStr categories
						 // p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
						 // by parentID, if value of this parameter is 2 then check will be done by ownerID
						 //	other wise no check will be required. So here as uncontroll level is DOMAIN OR DOMAINTYPE
						 // pass value 0 for this parameter and null for the p_parentUserID since here no parent and 
						 //	no owner exist for the DOMAIN OR DOMAINTYPE level.
						linkedHashMap = loadUsersForHierarchyFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,null,p_userName,p_channelUserVO.getUserID(),fixedCatStr,0);
						return linkedHashMap;
					}// fixed level hierarchy check
					else if(fixedLevelParent)
					{
						// load all the users form the system, which are in the direct child of the users of fixedCatStr categories
						//	p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
						 // by parentID, if value of this parameter is 2 then check will be done by ownerID
						 //	other wise no check will be required. So here as uncontroll level is DOMAIN OR DOMAINTYPE
						 // pass value 0 for this parameter and null for the p_parentUserID since here no parent and 
						 //	no owner exist for the DOMAIN OR DOMAINTYPE level.
						linkedHashMap = loadUsersForParentFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,null,p_userName,p_channelUserVO.getUserID(),fixedCatStr,0);
						return linkedHashMap;
					}// fixed level parent check
				}// uncontrol domain check
				else if(PretupsI.CHANNEL_TRANSFER_LEVEL_OWNER.equals(unctrlLevel))
				{
					if(BTSLUtil.isNullString(fixedCatStr))
					{
						// load all the users form the system within the sender'owner hierarchy
						//without any check of the fixed category
						linkedHashMap = loadUsersByOwnerID(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getOwnerID(),p_userName,p_channelUserVO.getUserID());
						return linkedHashMap;
					}// fixed category null check
					else if(fixedLevelHierarchy)
					{
						// load all the users form the system within the sender'owner hierarchy 
						// which are in the hierarchy of the users of fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
						// by parentID, if value of this parameter is 2 then check will be done by ownerID
						// other wise no check will be required. So here as uncontroll level is OWNER
						// pass value 2 for this parameter and OWNERID for the p_parentUserID since here list is to be  
						// loaded by owner.
						linkedHashMap = loadUsersForHierarchyFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getOwnerID(),p_userName,p_channelUserVO.getUserID(),fixedCatStr,2);
						return linkedHashMap;
					}// fixed level hierarchy check
					else if(fixedLevelParent)
					{
						// load all the users form the system within the sender'owner hierarchy 
						// which are in the direct child of the users of fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
						// by parentID, if value of this parameter is 2 then check will be done by ownerID
						// other wise no check will be required. So here as uncontroll level is OWNER
						// pass value 2 for this parameter and OWNERID for the p_parentUserID since here list is to be  
						// loaded by owner.
						
						linkedHashMap = loadUsersForParentFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getOwnerID(),p_userName,p_channelUserVO.getUserID(),fixedCatStr,2);
						return linkedHashMap;
					}// fixed level parent check
				}// owner level uncontroll check
				else if(PretupsI.CHANNEL_TRANSFER_LEVEL_PARENT.equals(unctrlLevel))
				{
					if(BTSLUtil.isNullString(fixedCatStr))
					{
						// load all the users form the system within the sender's parent hierarchy 
						//without any check of the fixed category
						linkedHashMap = loadUsersByParentIDRecursive(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getParentID(),p_userName,p_channelUserVO.getUserID());
						return linkedHashMap;
					}// fixed category null check
					else if(fixedLevelHierarchy)
					{
						// load all the users form the system within the sender's parent hierarchy, 
						// which are in the hierarchy of the users of fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
						// by parentID, if value of this parameter is 2 then check will be done by ownerID
						// other wise no check will be required. So here as uncontroll level is PARENT
						// pass value 1 for this parameter and PARENTID for the p_parentUserID since here list is to be  
						// loaded by parent.
						linkedHashMap = loadUsersForHierarchyFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getParentID(),p_userName,p_channelUserVO.getUserID(),fixedCatStr,1);
						return linkedHashMap;
					}// fixed level hierarchy check
					else if(fixedLevelParent)
					{
						// load all the users form the system within the sender's parent hierarchy, 
						// which are in the direct child of the users of fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
						// by parentID, if value of this parameter is 2 then check will be done by ownerID
						// other wise no check will be required. So here as uncontroll level is PARENT
						// pass value 1 for this parameter and PARENTID for the p_parentUserID since here list is to be  
						// loaded by parent.
						linkedHashMap = loadUsersForParentFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getParentID(),p_userName,p_channelUserVO.getUserID(),fixedCatStr,1);
						return linkedHashMap;
					}// fixed level parent check
				}// parent level uncontroll check
				else if(PretupsI.CHANNEL_TRANSFER_LEVEL_SELF.equals(unctrlLevel))
				{
					if(BTSLUtil.isNullString(fixedCatStr))
					{
						// load all the users form the system within the sender hierarchy 
						// without any check of the fixed category so here sender's userID is passed in the calling
						// method as the parentID to load all the users under sender recursively
						linkedHashMap = loadUsersByParentIDRecursive(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getUserID(),p_userName,p_channelUserVO.getUserID());
						return linkedHashMap;
					}// fixed category null check
					else if(fixedLevelHierarchy)
					{
						// load all the users form the system within the sender hierarchy, 
						// which are in the hierarchy of the users of fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
						// by parentID, if value of this parameter is 2 then check will be done by ownerID
						// other wise no check will be required. So here as uncontroll level is SELF but sender user
						// have to be considered as the parent of all the requested users so 
						// pass value 1 for this parameter and sener's userID for the p_parentUserID since here list is to be  
						// loaded by senderID.
						linkedHashMap = loadUsersForHierarchyFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getUserID(),p_userName,p_channelUserVO.getUserID(),fixedCatStr,1);
						return linkedHashMap;
					}// fixed level hierarchy check
					else if(fixedLevelParent)
					{
						// load all the users form the system within the sender hierarchy, 
						// which are in the direct child of the users of fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
						// by parentID, if value of this parameter is 2 then check will be done by ownerID
						// other wise no check will be required. So here as uncontroll level is SELF but sender user
						// have to be considered as the parent of all the requested users so 
						// pass value 1 for this parameter and sener's userID for the p_parentUserID since here list is to be  
						// loaded by senderID.
						linkedHashMap = loadUsersForParentFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getUserID(),p_userName,p_channelUserVO.getUserID(),fixedCatStr,1);
						return linkedHashMap;
					}// fixed level parent check
				}// Self level uncontroll check
			}// uncontrol transfer allowed check
			else
			{
				//Handling of control transfer
				if(PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM.equals(ctrlLevel)|| PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAINTYPE.equals(ctrlLevel)
						|| PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAIN.equals(ctrlLevel))
				{
					if(BTSLUtil.isNullString(fixedCatStr))
					{
						// load all the users form the system within the receiver domain for the direct child of the owner 
						//without any check of the fixed category
						if(directAllowed)
						{
							//  load all the users form the system 
							// which are direct child of the owner 
							// Sandeep goel ID USD001 
							// method is changed to remove the problem as login user is also coming in the list
							
							linkedHashMap = loadUsersByDomainID(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelTransferRuleVO.getToDomainCode(),p_userName,p_channelUserVO.getUserID());
						}// direct transfer check
						if(chnlByPassAllowed)
						{
							//load all the users form the system 
							// which are not direct child of the owner 
							// Sandeep goel ID USD001 
							// method is changed to remove the problem as login user is also coming in the list
							linkedHashMap=loadUsersChnlBypassByDomainID(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelTransferRuleVO.getToDomainCode(),p_userName,p_channelUserVO.getUserID());
						}// channel by pass check
						return linkedHashMap;
					}// fixed category null check
					else if(fixedLevelHierarchy)
					{
						// load all the users form the system within the sender domain, 
						// which are in the hierarchy of the users of fixedCatStr categories
						//  p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
						 // by parentID, if value of this parameter is 2 then check will be done by ownerID
						 //	other wise no check will be required. So here as controll level is DOMAIN OR DOMAINTYPE
						 // pass value 0 for this parameter and null for the p_parentUserID since here no parent and 
						 //	no owner exist for the DOMAIN OR DOMAINTYPE level.					
						linkedHashMap = loadUsersForHierarchyFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,null,p_userName,p_channelUserVO.getUserID(),fixedCatStr,0);
						return linkedHashMap;
					}// fixed level hierarchy check
					else if(fixedLevelParent)
					{
						// load all the users form the system within the sender domain, 
						// which are in the direct child of the users of fixedCatStr categories
						//  p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
						 // by parentID, if value of this parameter is 2 then check will be done by ownerID
						 //	other wise no check will be required. So here as controll level is DOMAIN OR DOMAINTYPE
						 // pass value 0 for this parameter and null for the p_parentUserID since here no parent and 
						 //	no owner exist for the DOMAIN OR DOMAINTYPE level.					
						linkedHashMap = loadUsersForParentFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,null,p_userName,p_channelUserVO.getUserID(),fixedCatStr,0);
						return linkedHashMap;
					}// fixed level parent check
				}// domain level control check
				else if(PretupsI.CHANNEL_TRANSFER_LEVEL_OWNER.equals(ctrlLevel))
				{
					if(BTSLUtil.isNullString(fixedCatStr))
					{
						// load all the users form the system within the sender'owner hierarchy
						//without any check of the fixed category
						if(directAllowed)
						{
							//  load all the users form the system within the sender'owner hierarchy
							// which are direct child of the owner so here in this method calling we are sending sender's
							// ownerID to considered as the parentID in the method
							linkedHashMap = loadUsersByParentID(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getOwnerID(),p_userName,p_channelUserVO.getUserID());
						}// direct transfer check
						if(chnlByPassAllowed)
						{
							//load all the users form the system within the sender'owner hierarchy
							// which are not direct child of the owner so here in this method calling we are sending sender's
							// ownerID to considered as the parentID in the method
							//linkedHashMap.addAll(loadUserForChannelByPass(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getOwnerID(),p_userName,p_channelUserVO.getUserID()));
							linkedHashMap=loadUserForChannelByPass(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getOwnerID(),p_userName,p_channelUserVO.getUserID());
						}// channel by pass check
						return linkedHashMap;
					}// fixed category null check
					else if(fixedLevelHierarchy)
					{
						// load all the users form the system within the sender's owner hierarchy 
						// which are in the hierarchy of the users of fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
						// by parentID, if value of this parameter is 2 then check will be done by ownerID
						// other wise no check will be required. So here as controll level is OWNER
						// pass value 2 for this parameter and OWNERID for the p_parentUserID since here list is to be  
						// loaded by owner.
						linkedHashMap = loadUsersForHierarchyFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getOwnerID(),p_userName,p_channelUserVO.getUserID(),fixedCatStr,2);
						return linkedHashMap;				}// fixed level hierarchy check
					else if(fixedLevelParent)
					{
						// load all the users form the system within the sender's owner hierarchy 
						// which are in the direct child of the users of fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
						// by parentID, if value of this parameter is 2 then check will be done by ownerID
						// other wise no check will be required. So here as controll level is OWNER
						// pass value 2 for this parameter and OWNERID for the p_parentUserID since here list is to be  
						// loaded by owner.
						linkedHashMap = loadUsersForParentFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getOwnerID(),p_userName,p_channelUserVO.getUserID(),fixedCatStr,2);
						return linkedHashMap;
					}// fixed level parent check
				}// owner level control check
				else if(PretupsI.CHANNEL_TRANSFER_LEVEL_PARENT.equals(ctrlLevel))
				{
					if(BTSLUtil.isNullString(fixedCatStr))
					{
						// load all the users form the system within the sender's parent hierarchy 
						//without any check of the fixed category
						if(directAllowed)
						{
							//  load all the users form the system within the sender's parent hierarchy
							// which are direct child of the parent
							linkedHashMap = loadUsersByParentID(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getParentID(),p_userName,p_channelUserVO.getUserID());
						}// direct transfer check
						if(chnlByPassAllowed)
						{
							//load all the users form the system within the sender's parent hierarchy
							// which are not direct child of the parent
							//linkedHashMap.addAll(loadUserForChannelByPass(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getParentID(),p_userName,p_channelUserVO.getUserID()));
							linkedHashMap=loadUserForChannelByPass(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getParentID(),p_userName,p_channelUserVO.getUserID());
						}// channel by pass check
						return linkedHashMap;
					}// fixed category null check
					else if(fixedLevelHierarchy)
					{
						// load all the users form the system within the sender's parent hierarchy, 
						// which are in the hierarchy of the users of fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
						// by parentID, if value of this parameter is 2 then check will be done by ownerID
						// other wise no check will be required. So here as controll level is PARENT
						// pass value 1 for this parameter and PARENTID for the p_parentUserID since here list is to be  
						// loaded by parent.					
						linkedHashMap = loadUsersForHierarchyFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getParentID(),p_userName,p_channelUserVO.getUserID(),fixedCatStr,1);
						return linkedHashMap;
					}// fixed level hierarchy check
					else if(fixedLevelParent)
					{
						// load all the users form the system within the sender's parent hierarchy, 
						// which are in the direct child of the users of fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
						// by parentID, if value of this parameter is 2 then check will be done by ownerID
						// other wise no check will be required. So here as controll level is PARENT
						// pass value 1 for this parameter and PARENTID for the p_parentUserID since here list is to be  
						// loaded by parent.					
						linkedHashMap = loadUsersForParentFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getParentID(),p_userName,p_channelUserVO.getUserID(),fixedCatStr,1);
						return linkedHashMap;
					}// fixed level parent check
				}// parent level control check
				else if(PretupsI.CHANNEL_TRANSFER_LEVEL_SELF.equals(ctrlLevel))
				{
					if(BTSLUtil.isNullString(fixedCatStr))
					{
						// load all the users form the system within the sender hierarchy 
						//without any check of the fixed category
						if(directAllowed)
						{
							//  load all the users form the system within the sender's  hierarchy
							// which are direct child of the sender so here in this method calling we are sending sender's
							// userID to considered as the parentID in the method
							linkedHashMap = loadUsersByParentID(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getUserID(),p_userName,p_channelUserVO.getUserID());
						}// direct transfer check
						if(chnlByPassAllowed)
						{
							//load all the users form the system within the sender's hierarchy
							// which are not direct child of the sender so here in this method calling we are sending sender's
							// userID to considered as the parentID in the method
							
							//linkedHashMap.addAll(loadUserForChannelByPass(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getUserID(),p_userName,p_channelUserVO.getUserID()));
							linkedHashMap=loadUserForChannelByPass(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getUserID(),p_userName,p_channelUserVO.getUserID());
							
						}// channel by pass check
						return linkedHashMap;
					}// fixed category null check
					else if(fixedLevelHierarchy)
					{
						// load all the users form the system within the sender hierarchy, 
						// which are in the hierarchy of the users of fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
						// by parentID, if value of this parameter is 2 then check will be done by ownerID
						// other wise no check will be required. So here as controll level is SELF but sender user
						// have to be considered as the parent of all the requested users so 
						// pass value 1 for this parameter and sener's userID for the p_parentUserID since here list is to be  
						// loaded by senderID.
						linkedHashMap = loadUsersForHierarchyFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getUserID(),p_userName,p_channelUserVO.getUserID(),fixedCatStr,1);
						return linkedHashMap;
					}// fixed level hierarchy check
					else if(fixedLevelParent)
					{
						// load all the users form the system within the sender hierarchy, 
						// which are in the direct child of the users of fixedCatStr categories
						// p_ctrlLvl (last parameter) here if value of this parameter is 1 then check will be done 
						// by parentID, if value of this parameter is 2 then check will be done by ownerID
						// other wise no check will be required. So here as controll level is SELF but sender user
						// have to be considered as the parent of all the requested users so 
						// pass value 1 for this parameter and sener's userID for the p_parentUserID since here list is to be  
						// loaded by senderID.
						linkedHashMap = loadUsersForParentFixedCat(p_con,p_channelUserVO.getNetworkID(),p_toCategoryCode,p_channelUserVO.getUserID(),p_userName,p_channelUserVO.getUserID(),fixedCatStr,1);
						return linkedHashMap;
					}// fixed level parent check
				}// Self level control check
			}// control transaction check
			if(_log.isDebugEnabled())
				_log.debug(methodName,"Exited userList.size() = "+linkedHashMap.size());
			return linkedHashMap;
		
		}
	    
   	    
		
}
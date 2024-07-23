package com.web.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

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
import com.btsl.pretups.channel.transfer.businesslogic.C2CBatchItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CBatchMasterVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.BatchC2CFileProcessLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

public class C2CBatchTransferWebDAO {

	private Log _log = LogFactory.getLog(this.getClass().getName());
	private static C2CBatchTransferWebQry c2CBatchTransferWebQry;

	public C2CBatchTransferWebDAO() {
		super();
		c2CBatchTransferWebQry = (C2CBatchTransferWebQry) ObjectProducer
				.getObject(QueryConstants.C2C_BATCH_TRANSFER_WEB_QRY,
						QueryConstants.QUERY_PRODUCER);
	}

	private static final Log LOG = LogFactory
			.getLog(C2CBatchTransferWebDAO.class.getName());

	private static OperatorUtilI operatorUtili = null;
	static {
		try {
			final String utilClass = (String) PreferenceCache
					.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
			operatorUtili = (OperatorUtilI) Class.forName(utilClass)
					.newInstance();
		} catch (Exception e) {
			LOG.errorTrace("static", e);
			EventHandler.handle(
					EventIDI.SYSTEM_ERROR,
					EventComponentI.SYSTEM,
					EventStatusI.RAISED,
					EventLevelI.FATAL,
					"C2CBatchTransferWebDAO[static]",
					"",
					"",
					"",
					"Exception while loading the class at the call:"
							+ e.getMessage());
		}
	}

	/**
	 * Method for loading C2CBatch details.. This method will load the batches
	 * that are within the geography of user whose userId is passed with
	 * status(OPEN) also in items table for corresponding master record the
	 * status is in p_itemStatus
	 * 
	 * @param p_con
	 *            java.sql.Connection
	 * @param p_itemStatus
	 *            String
	 * @param p_currentLevel
	 *            String
	 * @return java.util.ArrayList
	 * @throws BTSLBaseException
	 */
	public ArrayList loadBatchC2CMasterDetailsForTxr(Connection p_con,
			String p_userID, String p_itemStatus, String p_currentLevel)
			throws BTSLBaseException {
		final String methodName = "loadBatchC2CMasterDetailsForTxr";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered p_userID=" + p_userID
					+ " p_itemStatus=" + p_itemStatus + " p_currentLevel="
					+ p_currentLevel);
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sqlSelect = c2CBatchTransferWebQry
				.loadBatchC2CMasterDetailsForTxrQry(p_itemStatus,
						p_currentLevel);
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		}
		final ArrayList list = new ArrayList();
		try {
			pstmt = p_con.prepareStatement(sqlSelect);
			pstmt.setString(1, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
			pstmt.setString(2, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
			pstmt.setString(3, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
			pstmt.setString(4, p_userID);
			pstmt.setString(5, PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_OPEN);
			pstmt.setString(
					6,
					PretupsI.CHANNEL_TRANSFER_BATCH_C2C_ITEM_RCRDSTATUS_PROCESSED);
			pstmt.setString(7, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
			rs = pstmt.executeQuery();
			C2CBatchMasterVO c2cBatchMasterVO = null;
			while (rs.next()) {
				c2cBatchMasterVO = new C2CBatchMasterVO();
				c2cBatchMasterVO.setBatchId(rs.getString("batch_id"));
				c2cBatchMasterVO.setBatchName(rs.getString("batch_name"));
				//c2cBatchMasterVO.setProductMrp(rs.getLong("unit_value"));
				//c2cBatchMasterVO.setProductMrpStr(PretupsBL.getDisplayAmount(rs
					//	.getLong("unit_value")));
				c2cBatchMasterVO.setBatchTotalRecord(rs
						.getInt("batch_total_record"));
				c2cBatchMasterVO.setNewRecords(rs.getInt("new"));
				c2cBatchMasterVO.setClosedRecords(rs.getInt("close"));
				c2cBatchMasterVO.setRejectedRecords(rs.getInt("cncl"));
				c2cBatchMasterVO.setNetworkCode(rs.getString("network_code"));
				c2cBatchMasterVO.setNetworkCodeFor(rs
						.getString("network_code_for"));
				c2cBatchMasterVO.setProductCode(rs.getString("product_code"));
				c2cBatchMasterVO.setModifiedBy(rs.getString("modified_by"));
				c2cBatchMasterVO.setModifiedOn(rs.getTimestamp("modified_on"));
				c2cBatchMasterVO.setProductType(rs.getString("PRODUCT_NAME"));
				//c2cBatchMasterVO
					//	.setProductShortName(rs.getString("short_name"));
				c2cBatchMasterVO.setDomainCode(rs.getString("domain_code"));
				c2cBatchMasterVO.setBatchDate(rs.getDate("batch_date"));
				c2cBatchMasterVO.setDefaultLang(rs
						.getString("sms_default_lang"));
				c2cBatchMasterVO.setSecondLang(rs.getString("sms_second_lang"));
				c2cBatchMasterVO.setTransferType(rs.getString("transfer_type"));
				c2cBatchMasterVO.setTransferSubType(rs
						.getString("transfer_sub_type"));
				c2cBatchMasterVO.setCreatedBy(rs.getString("created_by"));
				c2cBatchMasterVO.setUserId(rs.getString("user_id"));
				c2cBatchMasterVO.setCategoryCode(rs.getString("category_code"));
				list.add(c2cBatchMasterVO);
			}
		} catch (SQLException sqe) {
			LOG.error(methodName, "SQLException : " + sqe);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2CBatchTransferWebDAO[loadBatchC2CMasterDetailsForTxr]",
					"", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		} catch (Exception ex) {
			LOG.error("loadBatchC2CMasterDetails", "Exception : " + ex);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2CBatchTransferWebDAO[loadBatchC2CMasterDetailsForTxr]",
					"", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting: c2cBatchMasterVOList size="
						+ list.size());
			}
		}
		return list;
	}

	/**
	 * Method for loading C2CBatch details.. This method will load the batches
	 * that are within the geography of user whose userId is passed with
	 * status(OPEN) also in items table for corresponding master record the
	 * status is in p_itemStatus
	 * 
	 * @param p_con
	 *            java.sql.Connection
	 * @param p_itemStatus
	 *            String
	 * @param p_currentLevel
	 *            String
	 * @return java.util.ArrayList
	 * @throws BTSLBaseException
	 */
	public ArrayList loadBatchC2CMasterDetailsForWdr(Connection p_con,
			String p_userID, String p_itemStatus, String p_currentLevel)
			throws BTSLBaseException {
		final String methodName = "loadBatchC2CMasterDetailsForWdr";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered p_userID=" + p_userID
					+ " p_itemStatus=" + p_itemStatus + " p_currentLevel="
					+ p_currentLevel);
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sqlSelect = c2CBatchTransferWebQry
				.loadBatchC2CMasterDetailsForWdrQry(p_itemStatus,
						p_currentLevel);
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		}
		final ArrayList list = new ArrayList();
		try {
			pstmt = p_con.prepareStatement(sqlSelect);
			pstmt.setString(1, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
			pstmt.setString(2, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
			pstmt.setString(3, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
			pstmt.setString(4, p_userID);
			pstmt.setString(5, PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_OPEN);
			pstmt.setString(
					6,
					PretupsI.CHANNEL_TRANSFER_BATCH_C2C_ITEM_RCRDSTATUS_PROCESSED);
			pstmt.setString(7, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
			rs = pstmt.executeQuery();
			C2CBatchMasterVO c2cBatchMasterVO = null;
			while (rs.next()) {
				c2cBatchMasterVO = new C2CBatchMasterVO();
				c2cBatchMasterVO.setBatchId(rs.getString("batch_id"));
				c2cBatchMasterVO.setBatchName(rs.getString("batch_name"));
				//c2cBatchMasterVO.setProductName(rs.getString("product_name"));
				//c2cBatchMasterVO.setProductMrp(rs.getLong("unit_value"));
				//c2cBatchMasterVO.setProductMrpStr(PretupsBL.getDisplayAmount(rs
				//		.getLong("unit_value")));
				c2cBatchMasterVO.setBatchTotalRecord(rs
						.getInt("batch_total_record"));
				c2cBatchMasterVO.setNewRecords(rs.getInt("new"));
				c2cBatchMasterVO.setClosedRecords(rs.getInt("close"));
				c2cBatchMasterVO.setRejectedRecords(rs.getInt("cncl"));
				c2cBatchMasterVO.setNetworkCode(rs.getString("network_code"));
				c2cBatchMasterVO.setNetworkCodeFor(rs
						.getString("network_code_for"));
				c2cBatchMasterVO.setProductCode(rs.getString("product_code"));
				c2cBatchMasterVO.setModifiedBy(rs.getString("modified_by"));
				c2cBatchMasterVO.setModifiedOn(rs.getTimestamp("modified_on"));
				c2cBatchMasterVO.setProductType(rs.getString("PRODUCT_NAME"));
				//c2cBatchMasterVO
				//		.setProductShortName(rs.getString("short_name"));
				c2cBatchMasterVO.setDomainCode(rs.getString("domain_code"));
				c2cBatchMasterVO.setBatchDate(rs.getDate("batch_date"));
				c2cBatchMasterVO.setDefaultLang(rs
						.getString("sms_default_lang"));
				c2cBatchMasterVO.setSecondLang(rs.getString("sms_second_lang"));
				c2cBatchMasterVO.setTransferType(rs.getString("transfer_type"));
				c2cBatchMasterVO.setTransferSubType(rs
						.getString("transfer_sub_type"));
				c2cBatchMasterVO.setCreatedBy(rs.getString("created_by"));
				c2cBatchMasterVO.setUserId(rs.getString("user_id"));
				c2cBatchMasterVO.setCategoryCode(rs.getString("category_code"));
				list.add(c2cBatchMasterVO);
			}
		} catch (SQLException sqe) {
			LOG.error(methodName, "SQLException : " + sqe);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2CBatchTransferWebDAO[loadBatchC2CMasterDetailsForWdr]",
					"", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		} catch (Exception ex) {
			LOG.error(methodName, "Exception : " + ex);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2CBatchTransferWebDAO[loadBatchC2CMasterDetailsForWdr]",
					"", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting: c2cBatchMasterVOList size="
						+ list.size());
			}
		}
		return list;
	}

	/**
	 * Method to cancel/approve the batch. This also perform all the data
	 * validation. Also construct error list Tables updated are:
	 * c2c_batch_items,c2c_batches
	 * 
	 * @param p_con
	 * @param p_dataMap
	 * @param p_currentLevel
	 * @param p_userID
	 * @param p_messages
	 * @param p_locale
	 * @return
	 * @throws BTSLBaseException
	 */
	public ArrayList processOrderByBatch(Connection p_con,
			LinkedHashMap p_dataMap, String p_currentLevel, String p_userID,
			MessageResources p_messages, Locale p_locale,
			String p_sms_default_lang, String p_sms_second_lang)
			throws BTSLBaseException {
		final String methodName = "processOrderByBatch";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered p_dataMap=" + p_dataMap
					+ " p_currentLevel=" + p_currentLevel + " p_locale="
					+ p_locale + " p_userID=" + p_userID);
		}
		PreparedStatement pstmtLoadUser = null;
		PreparedStatement psmtCancelC2CBatchItem = null;
		PreparedStatement psmtApprC2CBatchItem = null;
		PreparedStatement pstmtUpdateMaster = null;
		// commented for DB2
		// OraclePreparedStatement psmtCancelC2CBatchItem = null
		// OraclePreparedStatement psmtApprC2CBatchItem = null
		// OraclePreparedStatement pstmtUpdateMaster= null
		// PreparedStatement pstmtUpdateMaster= null
		PreparedStatement pstmtSelectItemsDetails = null;
		PreparedStatement pstmtIsModified = null;
		final PreparedStatement pstmtIsTxnNumExists1 = null;
		final PreparedStatement pstmtIsTxnNumExists2 = null;
		ArrayList errorList = null;
		ListValueVO errorVO = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		int updateCount = 0;
		String batch_ID = null;
		/*
		 * The query below will be used to load user datils. That details is the
		 * validated for eg: transfer profile, commission profile, user status
		 * etc.
		 */
		StringBuffer sqlBuffer = new StringBuffer(
				" SELECT u.status userstatus, cusers.in_suspend, ");
		sqlBuffer
				.append("cps.status commprofilestatus,tp.status profile_status,cps.language_1_message comprf_lang_1_msg, ");
		sqlBuffer
				.append("cps.language_2_message comprf_lang_2_msg, u.network_code, u.category_code ");
		sqlBuffer
				.append("FROM users u,channel_users cusers,commission_profile_set cps,transfer_profile tp ");
		sqlBuffer
				.append("WHERE u.user_id = ? AND u.status <> 'N' AND u.status <> 'C' ");
		sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
		sqlBuffer
				.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
		sqlBuffer.append(" tp.profile_id = cusers.transfer_profile_id  ");
		final String sqlLoadUser = sqlBuffer.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY sqlLoadUser=" + sqlLoadUser);
		}
		sqlBuffer = null;
		// after validating if request is to cancle the order, the below query
		// is used.
		sqlBuffer = new StringBuffer(" UPDATE  c2c_batch_items SET   ");
		sqlBuffer.append(" modified_by = ?, modified_on = ?,  ");
		if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE.equals(p_currentLevel)) {
			sqlBuffer.append(" approver_remarks = ?, ");
		}
		sqlBuffer.append(" cancelled_by = ?, ");
		sqlBuffer.append(" cancelled_on = ?, status = ?");
		sqlBuffer.append(" WHERE ");
		sqlBuffer.append(" batch_detail_id = ? ");
		if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE.equals(p_currentLevel)) {
			sqlBuffer.append(" AND status IN (? , ? )  ");
		}
		final String sqlCancelC2CBatchItems = sqlBuffer.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY sqlCancelC2CBatchItems="
					+ sqlCancelC2CBatchItems);
		}
		sqlBuffer = null;

		// after validating if request is of level 1 approve the order, the
		// below query is used.
		sqlBuffer = new StringBuffer(" UPDATE  c2c_batch_items SET   ");
		sqlBuffer.append(" modified_by = ?, modified_on = ?,  ");
		sqlBuffer.append(" approver_remarks = ?, ");
		sqlBuffer.append(" approved_by=?, approved_on=? , status = ?  ");
		sqlBuffer.append(" WHERE ");
		sqlBuffer.append(" batch_detail_id = ? ");
		sqlBuffer.append(" AND status IN (? , ? )  ");

		final String sqlApprvC2CBatchItems = sqlBuffer.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY sqlApprvC2CBatchItems="
					+ sqlApprvC2CBatchItems);
		}
		sqlBuffer = null;

		// Afetr all teh records are processed the the below query is used to
		// load the various counts such as new ,
		// apprv1, close ,cancled etc. These couts will be used to deceide what
		// status to be updated in master table
		final String selectItemsDetails = c2CBatchTransferWebQry
				.processOrderByBatchQry();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY selectItemsDetails="
					+ selectItemsDetails);
		}
		sqlBuffer = null;

		// The query below is used to update the master table after all items
		// are processed
		sqlBuffer = new StringBuffer(
				"UPDATE c2c_batches SET status=? , modified_by=? ,modified_on=?,sms_default_lang=? , sms_second_lang=?  ");
		sqlBuffer.append(" WHERE batch_id=? AND status=? ");
		final String updateC2CBatches = sqlBuffer.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY updateC2CBatches=" + updateC2CBatches);
		}
		sqlBuffer = null;

		// The query below is used to check if the record is modified or not
		sqlBuffer = new StringBuffer("SELECT modified_on FROM c2c_batch_items ");
		sqlBuffer.append("WHERE batch_detail_id = ? ");
		final String isModified = sqlBuffer.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY isModified=" + isModified);
		}
		sqlBuffer = null;

		Date date = null;
		try {
			C2CBatchItemsVO c2cBatchItemVO = null;
			ChannelUserVO channelUserVO = null;
			date = new Date();
			// Create the prepared statements
			pstmtLoadUser = p_con.prepareStatement(sqlLoadUser);
			psmtCancelC2CBatchItem = (PreparedStatement) p_con
					.prepareStatement(sqlCancelC2CBatchItems);
			psmtApprC2CBatchItem = (PreparedStatement) p_con
					.prepareStatement(sqlApprvC2CBatchItems);
			// commented for DB2
			// psmtCancelC2CBatchItem=(OraclePreparedStatement)p_con.prepareStatement(sqlCancelC2CBatchItems)
			// psmtApprC2CBatchItem=(OraclePreparedStatement)p_con.prepareStatement(sqlApprvC2CBatchItems)
			// pstmtUpdateMaster=(OraclePreparedStatement)p_con.prepareStatement(updateC2CBatches)
			pstmtSelectItemsDetails = p_con
					.prepareStatement(selectItemsDetails);
			pstmtUpdateMaster = (PreparedStatement) p_con
					.prepareStatement(updateC2CBatches);
			pstmtIsModified = p_con.prepareStatement(isModified);

			errorList = new ArrayList();
			final Iterator iterator = p_dataMap.keySet().iterator();
			String key = null;
			int m = 0;
			while (iterator.hasNext()) {
				key = (String) iterator.next();
				c2cBatchItemVO = (C2CBatchItemsVO) p_dataMap.get(key);
				if (BTSLUtil.isNullString(batch_ID)) {
					batch_ID = c2cBatchItemVO.getBatchId();
				}
				pstmtLoadUser.clearParameters();
				m = 0;
				++m;
				pstmtLoadUser.setString(m, c2cBatchItemVO.getUserId());
				rs = pstmtLoadUser.executeQuery();
				if (rs.next())// check data found or not
				{
					channelUserVO = ChannelUserVO.getInstance();
					channelUserVO.setUserID(c2cBatchItemVO.getUserId());
					channelUserVO.setStatus(rs.getString("userstatus"));
					channelUserVO.setInSuspend(rs.getString("in_suspend"));
					channelUserVO.setCommissionProfileStatus(rs
							.getString("commprofilestatus"));
					channelUserVO.setCommissionProfileLang1Msg(rs
							.getString("comprf_lang_1_msg"));
					channelUserVO.setCommissionProfileLang2Msg(rs
							.getString("comprf_lang_2_msg"));
					channelUserVO.setTransferProfileStatus(rs
							.getString("profile_status"));
					channelUserVO.setNetworkCode(rs.getString("network_code"));
					channelUserVO
							.setCategoryCode(rs.getString("category_code"));
					// (User status is checked) if this condition is true then
					// made entry in logs and leave this data.

					// user life cycle
					boolean senderStatusAllowed = false;
					final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache
							.getObject(channelUserVO.getNetworkCode(),
									channelUserVO.getCategoryCode(),
									PretupsI.USER_TYPE_CHANNEL,
									PretupsI.REQUEST_SOURCE_TYPE_WEB);
					if (userStatusVO != null) {
						final String userStatusAllowed = userStatusVO
								.getUserSenderAllowed();
						final String status[] = userStatusAllowed.split(",");
						for (int i = 0; i < status.length; i++) {
							if (status[i].equals(channelUserVO.getStatus())) {
								senderStatusAllowed = true;
							}
						}
					} else {
						throw new BTSLBaseException(this,
								"processOrderByBatch",
								"error.status.processing");
					}
					/*
					 * if(!PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.
					 * getStatus())) { p_con.rollback() errorVO=new
					 * ListValueVO(c2cBatchItemVO.getMsisdn(),String.
					 * valueOf(c2cBatchItemVO
					 * .getRecordNumber()),p_messages.getMessage
					 * (p_locale,"batchc2c.batchapprovereject.msg.error.usersuspend"
					 * )) errorList.add(errorVO)
					 * BatchC2CFileProcessLog.c2cBatchItemLog(methodName,
					 * c2cBatchItemVO
					 * ,"FAIL : User is not active","Approval level"
					 * +p_currentLevel) continue; }
					 */
					// (commission profile status is checked) if this condition
					// is true then made entry in logs and leave this data.
					if (!PretupsI.YES.equals(channelUserVO
							.getCommissionProfileStatus())) {
						p_con.rollback();
						errorVO = new ListValueVO(
								c2cBatchItemVO.getMsisdn(),
								String.valueOf(c2cBatchItemVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.batchapprovereject.msg.error.comprofsuspend"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.c2cBatchItemLog(methodName,
								c2cBatchItemVO,
								"FAIL : Commission profile is suspend",
								"Approval level" + p_currentLevel);
						continue;
					}
					// (tranmsfer profile status is checked) if this condition
					// is true then made entry in logs and leave this data.
					else if (!PretupsI.YES.equals(channelUserVO
							.getTransferProfileStatus())) {
						p_con.rollback();
						errorVO = new ListValueVO(
								c2cBatchItemVO.getMsisdn(),
								String.valueOf(c2cBatchItemVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.batchapprovereject.msg.error.trfprofsuspend"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.c2cBatchItemLog(methodName,
								c2cBatchItemVO,
								"FAIL : Transfer profile is suspend",
								"Approval level" + p_currentLevel);
						continue;
					}
					// (user in suspend is checked) if this condition is true
					// then made entry in logs and leave this data.
					else if (channelUserVO.getInSuspend() != null
							&& PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND
									.equals(channelUserVO.getInSuspend())) {
						p_con.rollback();
						errorVO = new ListValueVO(
								c2cBatchItemVO.getMsisdn(),
								String.valueOf(c2cBatchItemVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.batchapprovereject.msg.error.userinsuspend"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.c2cBatchItemLog(methodName,
								c2cBatchItemVO, "FAIL : User is IN suspend",
								"Approval level" + p_currentLevel);
						continue;
					}
				}
				// (record not found for user) if this condition is true then
				// made entry in logs and leave this data.
				else {
					p_con.rollback();
					errorVO = new ListValueVO(
							c2cBatchItemVO.getMsisdn(),
							String.valueOf(c2cBatchItemVO.getRecordNumber()),
							p_messages
									.getMessage(p_locale,
											"batchc2c.batchapprovereject.msg.error.nouser"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.c2cBatchItemLog(methodName,
							c2cBatchItemVO, "FAIL : User not found",
							"Approval level" + p_currentLevel);
					continue;

				}
				pstmtIsModified.clearParameters();
				m = 0;
				++m;
				pstmtIsModified.setString(m, c2cBatchItemVO.getBatchDetailId());
				rs1 = pstmtIsModified.executeQuery();
				java.sql.Timestamp newlastModified = null;
				if (rs1.next()) {
					newlastModified = rs1.getTimestamp("modified_on");
				}
				// (record not found means it is modified) if this condition is
				// true then made entry in logs and leave this data.
				else {
					p_con.rollback();
					errorVO = new ListValueVO(
							c2cBatchItemVO.getMsisdn(),
							String.valueOf(c2cBatchItemVO.getRecordNumber()),
							p_messages
									.getMessage(p_locale,
											"batchc2c.batchapprovereject.msg.error.recordmodified"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog
							.c2cBatchItemLog(
									methodName,
									c2cBatchItemVO,
									"FAIL : Record is already modified by some one else",
									"Approval level" + p_currentLevel);
					continue;
				}
				// if this condition is true then made entry in logs and leave
				// this data.
				if (newlastModified.getTime() != BTSLUtil
						.getTimestampFromUtilDate(
								c2cBatchItemVO.getModifiedOn()).getTime()) {
					p_con.rollback();
					errorVO = new ListValueVO(
							c2cBatchItemVO.getMsisdn(),
							String.valueOf(c2cBatchItemVO.getRecordNumber()),
							p_messages
									.getMessage(p_locale,
											"batchc2c.batchapprovereject.msg.error.recordmodified"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog
							.c2cBatchItemLog(
									methodName,
									c2cBatchItemVO,
									"FAIL : Record is already modified by some one else",
									"Approval level" + p_currentLevel);
					continue;

				}

				// If operation is of cancle then set the fiels in
				// psmtCancelC2CBatchItem
				if (PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL
						.equals(c2cBatchItemVO.getStatus())) {
					psmtCancelC2CBatchItem.clearParameters();
					m = 0;
					++m;
					psmtCancelC2CBatchItem.setString(m, p_userID);
					++m;
					psmtCancelC2CBatchItem.setTimestamp(m,
							BTSLUtil.getTimestampFromUtilDate(date));
					if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE
							.equals(p_currentLevel)) {
						// OraclePreparedStatement.FORM_NCHAR)
						++m;
						psmtCancelC2CBatchItem.setString(m,
								c2cBatchItemVO.getApproverRemarks());
					}
					++m;
					psmtCancelC2CBatchItem.setString(m, p_userID);
					++m;
					psmtCancelC2CBatchItem.setTimestamp(m,
							BTSLUtil.getTimestampFromUtilDate(date));
					++m;
					psmtCancelC2CBatchItem.setString(m,
							c2cBatchItemVO.getStatus());
					++m;
					psmtCancelC2CBatchItem.setString(m,
							c2cBatchItemVO.getBatchDetailId());
					if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE
							.equals(p_currentLevel)) {
						++m;
						psmtCancelC2CBatchItem.setString(m,
								PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
						++m;
						psmtCancelC2CBatchItem.setString(m,
								PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE);
					}
					updateCount = psmtCancelC2CBatchItem.executeUpdate();
				}
				// IF approval 1 is the operation then set parametrs in
				// psmtAppr1C2CBatchItem
				else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE
						.equals(c2cBatchItemVO.getStatus())) {
					psmtApprC2CBatchItem.clearParameters();
					m = 0;
					++m;
					psmtApprC2CBatchItem.setString(m, p_userID);
					++m;
					psmtApprC2CBatchItem.setTimestamp(m,
							BTSLUtil.getTimestampFromUtilDate(date));
					// commented for DB2
					// OraclePreparedStatement.FORM_NCHAR)
					++m;
					psmtApprC2CBatchItem.setString(m,
							c2cBatchItemVO.getApproverRemarks());
					++m;
					psmtApprC2CBatchItem.setString(m, p_userID);
					++m;
					psmtApprC2CBatchItem.setTimestamp(m,
							BTSLUtil.getTimestampFromUtilDate(date));
					++m;
					psmtApprC2CBatchItem.setString(m,
							c2cBatchItemVO.getStatus());
					++m;
					psmtApprC2CBatchItem.setString(m,
							c2cBatchItemVO.getBatchDetailId());
					++m;
					psmtApprC2CBatchItem.setString(m,
							PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
					++m;
					psmtApprC2CBatchItem.setString(m,
							PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE);
					updateCount = psmtApprC2CBatchItem.executeUpdate();
				}

				// If update count is <=0 that means record not updated in db
				// properly so made entry in logs and leave this data
				if (updateCount <= 0) {
					p_con.rollback();
					errorVO = new ListValueVO(
							c2cBatchItemVO.getMsisdn(),
							String.valueOf(c2cBatchItemVO.getRecordNumber()),
							p_messages
									.getMessage(p_locale,
											"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.c2cBatchItemLog(methodName,
							c2cBatchItemVO,
							"FAIL : DB Error while updating items table",
							"Approval level" + p_currentLevel
									+ ", updateCount=" + updateCount);
					continue;
				}
				// commit the transaction after processiong each record
				p_con.commit();
			}// end of while
				// Check the status to be updated in master table agfter
				// processing
				// of all records

		}// end of try
		catch (SQLException sqe) {
			try {
				if (p_con != null) {
					p_con.rollback();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			LOG.error(methodName, "SQLException : " + sqe);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2CBatchTransferWebDAO[processOrderByBatch]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			BatchC2CFileProcessLog.c2cBatchItemLog(
					methodName,
					null,
					"FAIL : updating batch items SQL Exception:"
							+ sqe.getMessage(), "Approval level"
							+ p_currentLevel + ", BATCH_ID=" + batch_ID);
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		} catch (Exception ex) {
			try {
				if (p_con != null) {
					p_con.rollback();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			LOG.error(methodName, "Exception : " + ex);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2CBatchTransferWebDAO[processOrderByBatch]", "", "", "",
					"Exception:" + ex.getMessage());
			BatchC2CFileProcessLog.c2cBatchItemLog(methodName, null,
					"FAIL : updating batch items Exception:" + ex.getMessage(),
					"Approval level" + p_currentLevel + ", BATCH_ID="
							+ batch_ID);
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (rs1 != null) {
					rs1.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtLoadUser != null) {
					pstmtLoadUser.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (psmtCancelC2CBatchItem != null) {
					psmtCancelC2CBatchItem.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (psmtApprC2CBatchItem != null) {
					psmtApprC2CBatchItem.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtIsModified != null) {
					pstmtIsModified.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtIsTxnNumExists1 != null) {
					pstmtIsTxnNumExists1.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtIsTxnNumExists2 != null) {
					pstmtIsTxnNumExists2.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				int m = 0;
				++m;
				pstmtSelectItemsDetails.setString(m,
						PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
				++m;
				pstmtSelectItemsDetails.setString(m,
						PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE);
				++m;
				pstmtSelectItemsDetails.setString(m,
						PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
				++m;
				pstmtSelectItemsDetails.setString(m,
						PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
				++m;
				pstmtSelectItemsDetails.setString(m, batch_ID);
				rs2 = pstmtSelectItemsDetails.executeQuery();
				if (rs2.next()) {
					final int totalCount = rs2.getInt("batch_total_record");
					final int closeCount = rs2.getInt("close");
					final int cnclCount = rs2.getInt("cncl");
					String statusOfMaster = null;
					// If all records are canle then set cancelled in master
					// table
					if (totalCount == cnclCount) {
						statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_CANCEL;
					} else if (totalCount == closeCount + cnclCount) {
						statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_CLOSE;
						// Otherwise set OPEN in mastrer table
					} else {
						statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_OPEN;
					}
					m = 0;
					++m;
					pstmtUpdateMaster.setString(m, statusOfMaster);
					++m;
					pstmtUpdateMaster.setString(m, p_userID);
					++m;
					pstmtUpdateMaster.setTimestamp(m,
							BTSLUtil.getTimestampFromUtilDate(date));

					// OraclePreparedStatement.FORM_NCHAR)
					++m;
					pstmtUpdateMaster.setString(m, p_sms_default_lang);
					// OraclePreparedStatement.FORM_NCHAR)
					++m;
					pstmtUpdateMaster.setString(m, p_sms_second_lang);
					++m;
					pstmtUpdateMaster.setString(m, batch_ID);
					++m;
					pstmtUpdateMaster
							.setString(
									m,
									PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_UNDERPROCESS);

					updateCount = pstmtUpdateMaster.executeUpdate();
					if (updateCount <= 0) {
						p_con.rollback();
						errorVO = new ListValueVO(
								"",
								"",
								p_messages
										.getMessage(p_locale,
												"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.c2cBatchItemLog(methodName,
								null,
								"FAIL : DB Error while updating master table",
								"Approval level" + p_currentLevel
										+ ", updateCount=" + updateCount);
						EventHandler.handle(EventIDI.SYSTEM_ERROR,
								EventComponentI.SYSTEM, EventStatusI.RAISED,
								EventLevelI.FATAL,
								"C2CBatchTransferWebDAO[processOrderByBatch]",
								"", "", "",
								"Error while updating C2C_BATCHES table. Batch id="
										+ batch_ID);
					}// end of if
				}// end of if
				p_con.commit();
			} catch (SQLException sqe) {
				try {
					if (p_con != null) {
						p_con.rollback();
					}
				} catch (Exception e) {
					LOG.errorTrace(methodName, e);
				}
				LOG.error(methodName, "SQLException : " + sqe);
				LOG.errorTrace(methodName, sqe);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,
						EventComponentI.SYSTEM, EventStatusI.RAISED,
						EventLevelI.FATAL,
						"C2CBatchTransferWebDAO[processOrderByBatch]", "", "",
						"", "SQL Exception:" + sqe.getMessage());
				BatchC2CFileProcessLog.c2cBatchItemLog(
						methodName,
						null,
						"FAIL : updating batch master SQL Exception:"
								+ sqe.getMessage(), "Approval level"
								+ p_currentLevel + ", BATCH_ID=" + batch_ID);
				// throw new BTSLBaseException(this, methodName,
				// "error.general.sql.processing")
			} catch (Exception ex) {
				try {
					if (p_con != null) {
						p_con.rollback();
					}
				} catch (Exception e) {
					LOG.errorTrace(methodName, e);
				}
				LOG.error(methodName, "Exception : " + ex);
				LOG.errorTrace(methodName, ex);
				;
				EventHandler.handle(EventIDI.SYSTEM_ERROR,
						EventComponentI.SYSTEM, EventStatusI.RAISED,
						EventLevelI.FATAL,
						"C2CBatchTransferWebDAO[processOrderByBatch]", "", "",
						"", "Exception:" + ex.getMessage());
				BatchC2CFileProcessLog.c2cBatchItemLog(
						methodName,
						null,
						"FAIL : updating batch master Exception:"
								+ ex.getMessage(), "Approval level"
								+ p_currentLevel + ", BATCH_ID=" + batch_ID);
				// throw new BTSLBaseException(this, methodName,
				// "error.general.processing")
			}finally{
				try {
					if (rs2 != null) {
						rs2.close();
					}
				} catch (Exception e) {
					LOG.errorTrace(methodName, e);
				}
				try {
					if (pstmtSelectItemsDetails != null) {
						pstmtSelectItemsDetails.close();
					}
				} catch (Exception e) {
					LOG.errorTrace(methodName, e);
				}
				try {
					if (pstmtUpdateMaster != null) {
						pstmtUpdateMaster.close();
					}
				} catch (Exception e) {
					LOG.errorTrace(methodName, e);
				}
			}


			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName,
						"Exiting: errorList size=" + errorList.size());
			}
		}
		return errorList;
	}

	/**
	 * This method load Batch details according to batch id.
	 * loadBatchDetailsList
	 * 
	 * @param p_con
	 *            Connection
	 * @param p_batchId
	 *            String
	 * @return ArrayList list
	 * @throws BTSLBaseException
	 *             ved.sharma
	 */
	public ArrayList loadBatchDetailsList(Connection p_con, String p_batchId)
			throws BTSLBaseException {
		final String methodName = "loadBatchDetailsList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered p_batchId=" + p_batchId);
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sqlSelect = c2CBatchTransferWebQry
				.loadBatchDetailsListQry();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		}
		C2CBatchMasterVO c2cBatchMasterVO = null;
		C2CBatchItemsVO c2cBatchItemsVO = null;
		final ArrayList list = new ArrayList();
		try {
			pstmt = p_con.prepareStatement(sqlSelect);
			pstmt.setString(1, p_batchId);
			pstmt.setString(2,
					PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_LOOKUP_TYPE);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				c2cBatchMasterVO = new C2CBatchMasterVO();
				c2cBatchMasterVO.setBatchId(rs.getString("batch_id"));
				c2cBatchMasterVO.setBatchName(rs.getString("batch_name"));
				c2cBatchMasterVO.setStatus(rs.getString("status"));
				c2cBatchMasterVO.setDomainCode(rs.getString("domain_code"));
				c2cBatchMasterVO.setDomainCodeDesc(rs.getString("domain_name"));
				c2cBatchMasterVO.setProductCode(rs.getString("product_code"));
				c2cBatchMasterVO.setProductCodeDesc(rs
						.getString("product_name"));
				c2cBatchMasterVO.setBatchFileName(rs
						.getString("batch_file_name"));
				c2cBatchMasterVO.setBatchTotalRecord(rs
						.getInt("batch_total_record"));
				c2cBatchMasterVO.setBatchDate(rs.getDate("batch_date"));
				c2cBatchMasterVO.setCreatedBy(rs.getString("initated_by"));
				c2cBatchMasterVO.setCreatedOn(rs.getTimestamp("created_on"));
				c2cBatchMasterVO.setStatus(rs.getString("status"));
				c2cBatchMasterVO.setStatusDesc(rs.getString("status_desc"));

				c2cBatchItemsVO = C2CBatchItemsVO.getInstance();
				c2cBatchItemsVO.setBatchDetailId(rs
						.getString("batch_detail_id"));
				c2cBatchItemsVO.setUserName(rs.getString("user_name"));
				c2cBatchItemsVO.setExternalCode(rs.getString("external_code"));
				c2cBatchItemsVO.setMsisdn(rs.getString("msisdn"));
				c2cBatchItemsVO.setCategoryName(rs.getString("category_name"));
				c2cBatchItemsVO.setCategoryCode(rs.getString("category_code"));
				c2cBatchItemsVO.setStatus(rs.getString("status_item"));
				c2cBatchItemsVO.setUserGradeCode(rs
						.getString("user_grade_code"));
				c2cBatchItemsVO.setGradeCode(rs.getString("user_grade_code"));
				c2cBatchItemsVO.setGradeName(rs.getString("grade_name"));
				c2cBatchItemsVO.setReferenceNo(rs.getString("reference_no"));
				c2cBatchItemsVO.setTransferDate(rs.getDate("transfer_date"));
				if (c2cBatchItemsVO.getTransferDate() != null) {
					c2cBatchItemsVO.setTransferDateStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil
							.getDateStringFromDate(c2cBatchItemsVO
									.getTransferDate())));
				}
				c2cBatchItemsVO.setTxnProfile(rs.getString("txn_profile"));
				c2cBatchItemsVO.setCommissionProfileSetId(rs
						.getString("commission_profile_set_id"));
				c2cBatchItemsVO.setCommissionProfileVer(rs
						.getString("commission_profile_ver"));
				c2cBatchItemsVO.setCommissionProfileDetailId(rs
						.getString("commission_profile_detail_id"));
				c2cBatchItemsVO.setCommissionRate(rs
						.getDouble("commission_rate"));
				c2cBatchItemsVO.setCommissionType(rs
						.getString("commission_type"));
				c2cBatchItemsVO.setRequestedQuantity(rs
						.getLong("requested_quantity"));
				c2cBatchItemsVO.setTransferMrp(rs.getLong("transfer_mrp"));
				c2cBatchItemsVO.setInitiatorRemarks(rs
						.getString("initiator_remarks"));
				c2cBatchItemsVO.setApprovedBy(rs.getString("approved_by"));
				c2cBatchItemsVO.setApprovedOn(rs.getTimestamp("approved_on"));
				c2cBatchItemsVO.setApproverRemarks(rs
						.getString("approver_remarks"));

				c2cBatchMasterVO.setC2cBatchItemsVO(c2cBatchItemsVO);

				list.add(c2cBatchMasterVO);
			}
		} catch (SQLException sqe) {
			LOG.error(methodName, "SQLException : " + sqe);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2CBatchTransferWebDAO[loadBatchDetailsList]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		} catch (Exception ex) {
			LOG.error(methodName, "Exception : " + ex);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2CBatchTransferWebDAO[loadBatchDetailsList]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(
						methodName,
						"Exiting: loadBatchDetailsList  list.size()="
								+ list.size());
			}
		}
		return list;
	}

	/**
	 * This method will load the batches that are within the geography of user
	 * whose userId is passed and batch id basis. with status(OPEN) also in
	 * items table for corresponding master record.
	 * 
	 * @Connection p_con
	 * @String p_goeDomain
	 * @String p_domain
	 * @String p_productCode
	 * @String p_batchid
	 * @String p_msisdn
	 * @Date p_fromDate
	 * @Date p_toDate
	 * @param pLOGinID
	 *            TODO
	 * @throws BTSLBaseException
	 */
	public ArrayList loadBatchC2CMasterDetails(Connection p_con,
			String p_goeDomain, String p_domain, String p_productCode,
			String p_batchid, String p_msisdn, Date p_fromDate, Date p_toDate,
			String pLOGinID, String p_categoryCode, String pLOGinCatCode,
			String p_userName) throws BTSLBaseException {
		final String methodName = "loadBatchC2CMasterDetails";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered p_goeDomain=" + p_goeDomain
					+ " p_domain=" + p_domain + " p_productCode="
					+ p_productCode + " p_batchid=" + p_batchid + " p_msisdn="
					+ p_msisdn + " p_fromDate=" + p_fromDate + " p_toDate="
					+ p_toDate + " pLOGinID=" + pLOGinID + "p_categoryCode"
					+ p_categoryCode + "pLOGincatCode" + pLOGinCatCode
					+ "p_userName" + p_userName);
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sqlSelect = c2CBatchTransferWebQry
				.loadBatchC2CMasterDetailsQry(p_batchid, pLOGinID,
						p_categoryCode, pLOGinCatCode, p_userName);
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		}
		final ArrayList list = new ArrayList();
		try {
			pstmt = p_con.prepareStatement(sqlSelect);
			int i = 0;
			++i;
			pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
			++i;
			pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
			++i;
			pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
			// pstmt.setString(++i, pLOGinID)
			if (p_batchid != null) {
				++i;
				pstmt.setString(i, p_batchid);
				++i;
				pstmt.setString(i, pLOGinID);
			} else {
				++i;
				pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
				++i;
				pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
				if (p_categoryCode.equals(pLOGinCatCode)) {
					++i;
					pstmt.setString(i, pLOGinID);
				}
				if (LOG.isDebugEnabled()) {
					LOG.debug(
							methodName,
							"QUERY BTSLUtil.getSQLDateFromUtilDate(p_fromDate)="
									+ BTSLUtil.getSQLDateFromUtilDate(p_toDate)
									+ " BTSLUtil.getSQLDateFromUtilDate(p_fromDate)="
									+ BTSLUtil.getSQLDateFromUtilDate(p_toDate));
				}
			}
			rs = pstmt.executeQuery();
			C2CBatchMasterVO c2cBatchMasterVO = null;
			while (rs.next()) {
				c2cBatchMasterVO = new C2CBatchMasterVO();
				c2cBatchMasterVO.setBatchId(rs.getString("batch_id"));
				c2cBatchMasterVO.setDomainCode(rs.getString("domain_code"));
				c2cBatchMasterVO.setBatchName(rs.getString("batch_name"));
				c2cBatchMasterVO.setProductName(rs.getString("product_name"));
				c2cBatchMasterVO.setProductCode(rs.getString("product_code"));
				c2cBatchMasterVO.setProductMrp(rs.getLong("unit_value"));
				c2cBatchMasterVO.setProductMrpStr(PretupsBL.getDisplayAmount(rs
						.getLong("unit_value")));
				c2cBatchMasterVO.setBatchTotalRecord(rs
						.getInt("batch_total_record"));
				c2cBatchMasterVO.setNewRecords(rs.getInt("new"));

				c2cBatchMasterVO.setClosedRecords(rs.getInt("close"));
				c2cBatchMasterVO.setRejectedRecords(rs.getInt("cncl"));
				c2cBatchMasterVO.setBatchDate(rs.getDate("batch_date"));
				if (c2cBatchMasterVO.getBatchDate() != null) {
					c2cBatchMasterVO.setBatchDateStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil
							.getDateStringFromDate(c2cBatchMasterVO
									.getBatchDate())));
				}
				list.add(c2cBatchMasterVO);
			}
		} catch (SQLException sqe) {
			LOG.error(methodName, "SQLException : " + sqe);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2CBatchTransferWebDAO[loadBatchC2CMasterDetails]", "",
					"", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		} catch (Exception ex) {
			LOG.error(methodName, "Exception : " + ex);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2CBatchTransferWebDAO[loadBatchC2CMasterDetails]", "",
					"", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting: c2cBatchMasterVOList size="
						+ list.size());
			}
		}
		return list;
	}

	/**
	 * Method to close the c2c Batch Transfer/Withdraw. This also perform all
	 * the data validation. Also construct error list Tables updated are:
	 * c2c_batches,c2c_batch_items
	 * user_balances,user_daily_balances,user_transfer_counts,c2c_batch_items,
	 * c2c_batches, channel_transfers_items,channel_transfers
	 * 
	 * @param p_con
	 * @param p_dataMap
	 * @param p_senderVO
	 * @param p_batchItemsList
	 * @param p_c2cBatchMatserVO
	 * @param p_messages
	 * @param p_locale
	 * @return
	 * @throws BTSLBaseException
	 */

	public ArrayList closeBatchC2CTransfer(Connection p_con,
			C2CBatchMasterVO p_batchMasterVO, ChannelUserVO p_senderVO,
			ArrayList p_batchItemsList, MessageResources p_messages,
			Locale p_locale, String p_sms_default_lang, String p_sms_second_lang,String _partialProceesAllowed,Boolean _errorExistInfile)
			throws BTSLBaseException {
		final String methodName = "closeBatchC2CTransfer";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered.... p_batchMasterVO="
					+ p_batchMasterVO + ", p_batchItemsList.size() = "
					+ p_batchItemsList.size() + ", p_batchItemsList="
					+ p_batchItemsList + "p_locale=" + p_locale);
		}
		Boolean isC2CSmsNotify = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_SMS_NOTIFY);
		String defaultWebGatewayCode = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WEB_GATEWAY_CODE);
		Boolean isOthComChnl = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL);
		Boolean isTransationTypeAlwd = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD);
		String txnReceiverUserStatusChang = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_RECEIVER_USER_STATUS_CHANG));
		String txnSenderUserStatusChang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_SENDER_USER_STATUS_CHANG);
		PreparedStatement pstmtLoadUser = null;
		PreparedStatement pstmtSelectUserBalances = null;
		PreparedStatement pstmtUpdateUserBalances = null;
		PreparedStatement pstmtUpdateSenderBalanceOn = null;

		PreparedStatement pstmtInsertUserDailyBalances = null;

		PreparedStatement pstmtSelectSenderBalance = null;
		PreparedStatement pstmtUpdateSenderBalance = null;
		PreparedStatement pstmtInsertSenderDailyBalances = null;

		PreparedStatement pstmtSelectBalance = null;

		PreparedStatement pstmtUpdateBalance = null;
		PreparedStatement pstmtInsertBalance = null;
		PreparedStatement pstmtSelectTransferCounts = null;

		PreparedStatement pstmtSelectSenderTransferCounts = null;
		PreparedStatement pstmtSelectProfileCounts = null;
		PreparedStatement pstmtUpdateTransferCounts = null;
		PreparedStatement pstmtSelectSenderProfileOutCounts = null;
		PreparedStatement pstmtUpdateSenderTransferCounts = null;
		PreparedStatement pstmtInsertTransferCounts = null;
		PreparedStatement pstmtInsertSenderTransferCounts = null;

		PreparedStatement pstmtLoadTransferProfileProduct = null;
		PreparedStatement handlerStmt = null;

		PreparedStatement pstmtInsertIntoChannelTransferItems = null;
		// OraclePreparedStatement
		// pstmtInsertIntoChannelTranfers=null;//commented for DB2
		PreparedStatement pstmtInsertIntoChannelTranfers = null;
		PreparedStatement pstmtSelectBalanceInfoForMessage = null;

		ResultSet rs = null;
		final ArrayList errorList = new ArrayList();
		ListValueVO errorVO = null;
		ArrayList userbalanceList = null;
		UserBalancesVO balancesVO = null;
		KeyArgumentVO keyArgumentVO = null;
		String[] argsArr = null;
		ArrayList txnSmsMessageList = null;
		ArrayList balSmsMessageList = null;
		Locale locale = null;
		String[] array = null;
		BTSLMessages messages = null;
		PushMessage pushMessage = null;
		String language = null;
		String country = null;
		String c2cTransferID = null;
		int updateCount = 0;
		// added by vikram
		long senderPreviousBal = -1; // taking sender previous balance as 0

		// for loading the C2C transfer rule for C2C transfer
		PreparedStatement pstmtSelectTrfRule = null;
		ResultSet rsSelectTrfRule = null;
		PreparedStatement psmtInsertUserThreshold = null;
		// added by vikram
		long thresholdValue = -1;
		// user life cycle
		String receiverStatusAllowed = null;
		//partial
		String partialProceesAllowed= _partialProceesAllowed;
		
		final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache
				.getObject(p_batchMasterVO.getNetworkCode(),
						p_batchMasterVO.getCategoryCode(),
						PretupsI.USER_TYPE_CHANNEL,
						PretupsI.REQUEST_SOURCE_TYPE_WEB);
		if (userStatusVO != null) {
			receiverStatusAllowed = "'"
					+ (userStatusVO.getUserReceiverAllowed()).replaceAll(",",
							"','") + "'";
		} else {
			throw new BTSLBaseException(this, methodName,
					"error.status.processing");
		}

		final StringBuffer strBuffSelectTrfRule = new StringBuffer(
				" SELECT transfer_rule_id,transfer_type, transfer_allowed ");
		strBuffSelectTrfRule
				.append("FROM chnl_transfer_rules WHERE network_code = ? AND domain_code = ?  ");
		strBuffSelectTrfRule
				.append("AND to_category = ? AND status = 'Y' AND type = 'CHANNEL' ");
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "strBuffSelectTrfRule Query ="
					+ strBuffSelectTrfRule);
			// ends here
		}

		// for loading the products associated with the transfer rule
		PreparedStatement pstmtSelectTrfRuleProd = null;
		ResultSet rsSelectTrfRuleProd = null;
		final StringBuffer strBuffSelectTrfRuleProd = new StringBuffer(
				"SELECT 1 FROM chnl_transfer_rules_products ");
		strBuffSelectTrfRuleProd
				.append("WHERE transfer_rule_id=?  AND product_code = ? ");
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "strBuffSelectTrfRuleProd Query ="
					+ strBuffSelectTrfRuleProd);
			// ends here
		}

		// for loading the products associated with the commission profile
		PreparedStatement pstmtSelectCProfileProd = null;
		ResultSet rsSelectCProfileProd = null;
		final StringBuffer strBuffSelectCProfileProd = new StringBuffer("SELECT cp.min_transfer_value,cp.max_transfer_value,cp.discount_type,cp.discount_rate, ");
		strBuffSelectCProfileProd.append("cp.comm_profile_products_id, cp.transfer_multiple_off, cp.taxes_on_foc_applicable,cp.taxes_on_channel_transfer  ");
		strBuffSelectCProfileProd.append("FROM commission_profile_products cp ");
		strBuffSelectCProfileProd.append("WHERE  cp.product_code = ? AND cp.comm_profile_set_id = ? AND cp.comm_profile_set_version = ? ");
		strBuffSelectCProfileProd.append("AND cp.transaction_type = ? AND cp.payment_mode = ? ");
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "strBuffSelectCProfileProd Query ="
					+ strBuffSelectCProfileProd);
		}

		PreparedStatement pstmtSelectCProfileProdDetail = null;
		ResultSet rsSelectCProfileProdDetail = null;
		final StringBuffer strBuffSelectCProfileProdDetail = new StringBuffer(
				"SELECT cpd.tax1_type,cpd.tax1_rate,cpd.tax2_type,cpd.tax2_rate, ");
		strBuffSelectCProfileProdDetail
				.append("cpd.tax3_type,cpd.tax3_rate,cpd.commission_type,cpd.commission_rate,cpd.comm_profile_detail_id ");
		strBuffSelectCProfileProdDetail
				.append("FROM commission_profile_details cpd ");
		strBuffSelectCProfileProdDetail
				.append("WHERE  cpd.comm_profile_products_id = ? AND cpd.start_range <= ? AND cpd.end_range >= ? ");
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "strBuffSelectCProfileProdDetail Query ="
					+ strBuffSelectCProfileProdDetail);
			// ends here
		}

		// for existance of the product in the transfer profile
		PreparedStatement pstmtSelectTProfileProd = null;
		ResultSet rsSelectTProfileProd = null;
		final StringBuffer strBuffSelectTProfileProd = new StringBuffer(
				" SELECT 1 ");
		strBuffSelectTProfileProd
				.append("FROM transfer_profile_products tpp,transfer_profile tp, transfer_profile catp,transfer_profile_products catpp ");
		strBuffSelectTProfileProd
				.append("WHERE tpp.profile_id=? AND tpp.product_code = ? AND tpp.profile_id=tp.profile_id AND catp.profile_id=catpp.profile_id ");
		strBuffSelectTProfileProd
				.append("AND tpp.product_code=catpp.product_code AND tp.category_code=catp.category_code AND catp.parent_profile_id=? AND catp.status='Y' AND tp.network_code = catp.network_code");
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "strBuffSelectTProfileProd Query ="
					+ strBuffSelectTProfileProd);
			// ends here
		}

		// insert data in the batch master table
		// commented for DB2
		// OraclePreparedStatement pstmtInsertBatchMaster = null
		PreparedStatement pstmtInsertBatchMaster = null;
		final StringBuffer strBuffInsertBatchMaster = new StringBuffer(
				"INSERT INTO c2c_batches (batch_id, network_code, ");
		strBuffInsertBatchMaster
				.append("network_code_for, batch_name, status, domain_code, product_code, ");
		strBuffInsertBatchMaster
				.append("batch_file_name, batch_total_record, batch_date, created_by, created_on, ");
		strBuffInsertBatchMaster
				.append(" modified_by, modified_on,sms_default_lang,sms_second_lang,user_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "strBuffInsertBatchMaster Query ="
					+ strBuffInsertBatchMaster);
			// ends here
		}

		// insert data in the c2c batch items table
		// OraclePreparedStatement pstmtInsertBatchItems = null
		PreparedStatement pstmtInsertBatchItems = null;
		final StringBuffer strBuffInsertBatchItems = new StringBuffer(
				"INSERT INTO c2c_batch_items (batch_id, batch_detail_id, ");
		strBuffInsertBatchItems
				.append("category_code, msisdn, user_id, status, modified_by, modified_on, user_grade_code, ");
		strBuffInsertBatchItems.append("transfer_date, txn_profile, ");
		strBuffInsertBatchItems
				.append("commission_profile_set_id, commission_profile_ver, commission_profile_detail_id, ");
		strBuffInsertBatchItems
				.append("commission_type, commission_rate, commission_value, tax1_type, tax1_rate, ");
		strBuffInsertBatchItems
				.append("tax1_value, tax2_type, tax2_rate, tax2_value, tax3_type, tax3_rate, ");
		strBuffInsertBatchItems
				.append("tax3_value, requested_quantity, transfer_mrp, initiator_remarks, external_code,rcrd_status,transfer_type,transfer_sub_type,product_code) ");
		strBuffInsertBatchItems
				.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "strBuffInsertBatchItems Query ="
					+ strBuffInsertBatchItems);
		}
		// ends here
		// update master table with OPEN status
		PreparedStatement pstmtUpdateBatchMaster = null;
		final StringBuffer strBuffUpdateBatchMaster = new StringBuffer(
				"UPDATE c2c_batches SET batch_total_record=? , status =? WHERE batch_id=?");
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "strBuffUpdateBatchMaster Query ="
					+ strBuffUpdateBatchMaster);
		}

		/*
		 * The query below will be used to load user datils. That details is the
		 * validated for eg: transfer profile, commission profile, user status
		 * etc.
		 */
		StringBuffer sqlBuffer = new StringBuffer(
				" SELECT u.status userstatus, cusers.in_suspend, ");
		sqlBuffer
				.append("cps.status commprofilestatus,tp.status profile_status,cps.language_1_message comprf_lang_1_msg, ");
		sqlBuffer
				.append("cps.language_2_message comprf_lang_2_msg,up.phone_language,up.country, ug.grph_domain_code ");
		sqlBuffer
				.append("FROM users u,channel_users cusers,commission_profile_set cps,transfer_profile tp, user_phones up,user_geographies ug ");
		sqlBuffer
				.append("WHERE u.user_id = ? AND up.user_id=u.user_id AND up.primary_number = 'Y' ");
		sqlBuffer.append(" AND u.status IN (" + receiverStatusAllowed + ")");
		sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
		sqlBuffer
				.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
		sqlBuffer
				.append(" tp.profile_id = cusers.transfer_profile_id AND ug.user_id = u.user_id ");
		final String sqlLoadUser = sqlBuffer.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY sqlLoadUser=" + sqlLoadUser);
		}
		sqlBuffer = null;

		// The query below is used to load the user balance
		// This table will basically used to update the daily_balance_updated_on
		// and also to know how many
		// records are to be inserted in user_daily_balances table
		StringBuilder sqlBuffer1 = new StringBuilder();
		sqlBuffer1
				.append("SELECT user_id, network_code, network_code_for, product_code, balance, prev_balance,  ");
		sqlBuffer1
				.append("last_transfer_type, last_transfer_no, last_transfer_on, daily_balance_updated_on ");
		sqlBuffer1.append("FROM user_balances ");
		if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants
				.getProperty("databasetype"))) {
			sqlBuffer1
					.append("WHERE user_id = ? AND TRUNC(daily_balance_updated_on)<> TRUNC(?) FOR UPDATE ");
		} else {
			sqlBuffer1
					.append("WHERE user_id = ? AND TRUNC(daily_balance_updated_on)<> TRUNC(?) FOR UPDATE  ");
		}
		final String selectUserBalances=sqlBuffer1.toString();

		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY selectUserBalances="
					+ selectUserBalances);
		}
		sqlBuffer = null;

		// update daily_balance_updated_on with current date for user
		sqlBuffer = new StringBuffer(
				" UPDATE user_balances SET daily_balance_updated_on = ? ");
		sqlBuffer.append("WHERE user_id = ? ");
		final String updateUserBalances = sqlBuffer.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY updateUserBalances="
					+ updateUserBalances);
		}
		sqlBuffer = null;

		// Executed if day difference in last updated date and current date is
		// greater then or equal to 1
		// Insert number of records equal to day difference in last updated date
		// and current date in user_daily_balances
		sqlBuffer = new StringBuffer(
				" INSERT INTO user_daily_balances(balance_date, user_id, network_code, ");
		sqlBuffer
				.append("network_code_for, product_code, balance, prev_balance, last_transfer_type, ");
		sqlBuffer
				.append("last_transfer_no, last_transfer_on, created_on,creation_type )");
		sqlBuffer.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?) ");
		final String insertDailyBalances = sqlBuffer.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY insertUserDailyBalances="
					+ insertDailyBalances);
		}
		sqlBuffer = null;

		// Select the balance of user for the perticuler product and network.
		StringBuilder sqlBuffer2 = new StringBuilder();
		sqlBuffer2.append("SELECT  ");
		sqlBuffer2.append(" balance ");
		sqlBuffer2.append(" FROM user_balances ");
		if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants
				.getProperty("databasetype"))) {
			sqlBuffer2
					.append(" WHERE user_id = ? and product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE  ");
		} else {
			sqlBuffer2
					.append(" WHERE user_id = ? and product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE  ");
		}
		final String selectBalance = sqlBuffer2.toString();
		
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY selectBalance=" + selectBalance);
		}
		sqlBuffer = null;

		// Credit the user balance(If balance found in user_balances)
		sqlBuffer = new StringBuffer(
				" UPDATE user_balances SET prev_balance = ?, balance = ? , last_transfer_type = ? , ");
		sqlBuffer.append(" last_transfer_no = ? , last_transfer_on = ? ");
		sqlBuffer.append(" WHERE ");
		sqlBuffer.append(" user_id = ? ");
		sqlBuffer.append(" AND ");
		sqlBuffer
				.append(" product_code = ? AND network_code = ? AND network_code_for = ? ");
		final String updateBalance = sqlBuffer.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY updateBalance=" + updateBalance);
		}
		sqlBuffer = null;

		// Insert the record of balnce for user (If balance not found in
		// user_balances)
		sqlBuffer = new StringBuffer(" INSERT ");
		sqlBuffer.append(" INTO user_balances ");
		sqlBuffer
				.append(" ( prev_balance,daily_balance_updated_on , balance, last_transfer_type, last_transfer_no, last_transfer_on , ");
		sqlBuffer
				.append(" user_id, product_code , network_code, network_code_for ) ");
		sqlBuffer.append(" VALUES ");
		sqlBuffer.append(" (?,?,?,?,?,?,?,?,?,?) ");
		final String insertBalance = sqlBuffer.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY insertBalance=" + insertBalance);
		}
		sqlBuffer = null;

		// Select the running countres of user(to be checked against the
		// effetive profile counters)
		StringBuilder sqlBuffer3 = new StringBuilder();
		sqlBuffer3.append("SELECT user_id, daily_in_count, daily_in_value, weekly_in_count, weekly_in_value,  ");
		sqlBuffer3.append(" monthly_in_count, monthly_in_value,daily_out_count, daily_out_value, weekly_out_count, ");
		sqlBuffer3.append(" weekly_out_value, monthly_out_count, monthly_out_value, outside_daily_in_count, ");
		sqlBuffer3.append(" outside_daily_in_value, outside_weekly_in_count, outside_weekly_in_value, ");
		sqlBuffer3.append(" outside_monthly_in_count, outside_monthly_in_value, outside_daily_out_count, ");
		sqlBuffer3.append(" outside_daily_out_value, outside_weekly_out_count, outside_weekly_out_value, ");
		sqlBuffer3.append(" outside_monthly_out_count, outside_monthly_out_value, daily_subscriber_out_count, ");
		sqlBuffer3.append(" daily_subscriber_out_value, weekly_subscriber_out_count, weekly_subscriber_out_value, ");
		sqlBuffer3.append(" monthly_subscriber_out_count, monthly_subscriber_out_value,last_transfer_date ");
		sqlBuffer3.append(" FROM user_transfer_counts ");
        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
        	sqlBuffer3.append(" WHERE user_id = ? FOR UPDATE ");
          } else {
        	  sqlBuffer3.append(" WHERE user_id = ? FOR UPDATE ");
         }
		final String selectTransferCounts = sqlBuffer3.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY selectTransferCounts="
					+ selectTransferCounts);
		}
		sqlBuffer = null;

		// Select the effective profile counters of user to be checked with
		// running counters of user
		final StringBuffer strBuff = new StringBuffer();
		strBuff.append(" SELECT tp.profile_id,LEAST(tp.daily_transfer_in_count,catp.daily_transfer_in_count) daily_transfer_in_count, ");
		strBuff.append(" LEAST(tp.daily_transfer_in_value,catp.daily_transfer_in_value) daily_transfer_in_value ,LEAST(tp.weekly_transfer_in_count,catp.weekly_transfer_in_count) weekly_transfer_in_count, ");
		strBuff.append(" LEAST(tp.weekly_transfer_in_value,catp.weekly_transfer_in_value) weekly_transfer_in_value,LEAST(tp.monthly_transfer_in_count,catp.monthly_transfer_in_count) monthly_transfer_in_count, ");
		strBuff.append(" LEAST(tp.monthly_transfer_in_value,catp.monthly_transfer_in_value) monthly_transfer_in_value");
		strBuff.append(" FROM transfer_profile tp,transfer_profile catp ");
		strBuff.append(" WHERE tp.profile_id = ? AND tp.status = ?	AND tp.network_code = ? ");
		strBuff.append(" AND tp.category_code=catp.category_code ");
		strBuff.append(" AND catp.parent_profile_id=? AND catp.status=?	AND tp.network_code = catp.network_code ");
		final String selectProfileInCounts = strBuff.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY selectProfileInCounts="
					+ selectProfileInCounts);
		}
		sqlBuffer = null;

		// Update the user running countres (If record found for user running
		// counters)
		sqlBuffer = new StringBuffer(" UPDATE user_transfer_counts  SET ");
		sqlBuffer
				.append(" daily_in_count = ?, daily_in_value = ?, weekly_in_count = ?, weekly_in_value = ?,");
		sqlBuffer
				.append(" monthly_in_count = ?, monthly_in_value = ? ,daily_out_count =?, daily_out_value=?, ");
		sqlBuffer
				.append(" weekly_out_count=?, weekly_out_value =?, monthly_out_count=?, monthly_out_value=?, ");
		sqlBuffer
				.append(" outside_daily_in_count=?, outside_daily_in_value=?, outside_weekly_in_count=?,");
		sqlBuffer
				.append(" outside_weekly_in_value=?, outside_monthly_in_count=?, outside_monthly_in_value=?, ");
		sqlBuffer
				.append(" outside_daily_out_count=?, outside_daily_out_value=?, outside_weekly_out_count=?, ");
		sqlBuffer
				.append(" outside_weekly_out_value=?, outside_monthly_out_count=?, outside_monthly_out_value=?, ");
		sqlBuffer
				.append(" daily_subscriber_out_count=?, daily_subscriber_out_value=?, weekly_subscriber_out_count=?, ");
		sqlBuffer
				.append(" weekly_subscriber_out_value=?, monthly_subscriber_out_count=?, monthly_subscriber_out_value=?, ");
		sqlBuffer
				.append(" last_in_time = ? , last_transfer_id=?,last_transfer_date=? ");
		sqlBuffer.append(" WHERE user_id = ?  ");
		final String updateTransferCounts = sqlBuffer.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY updateTransferCounts="
					+ updateTransferCounts);
		}
		sqlBuffer = null;

		// Select the effective profile counters of sender to be checked with
		// running counters of sender added by Gopal
		final StringBuffer strBuff1 = new StringBuffer();
		strBuff1.append(" SELECT tp.profile_id,LEAST(tp.daily_transfer_out_count,catp.daily_transfer_out_count) daily_transfer_out_count, ");
		strBuff1.append(" LEAST(tp.daily_transfer_out_value,catp.daily_transfer_out_value) daily_transfer_out_value ,LEAST(tp.weekly_transfer_out_count,catp.weekly_transfer_out_count) weekly_transfer_out_count, ");
		strBuff1.append(" LEAST(tp.weekly_transfer_out_value,catp.weekly_transfer_out_value) weekly_transfer_out_value,LEAST(tp.monthly_transfer_out_count,catp.monthly_transfer_out_count) monthly_transfer_out_count, ");
		strBuff1.append(" LEAST(tp.monthly_transfer_out_value,catp.monthly_transfer_out_value) monthly_transfer_out_value");
		strBuff1.append(" FROM transfer_profile tp,transfer_profile catp ");
		strBuff1.append(" WHERE tp.profile_id = ? AND tp.status = ?	AND tp.network_code = ? ");
		strBuff1.append(" AND tp.category_code=catp.category_code ");
		strBuff1.append(" AND catp.parent_profile_id=? AND catp.status=?	AND tp.network_code = catp.network_code ");
		final String selectProfileOutCounts = strBuff1.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY selectProfileOutCounts="
					+ selectProfileOutCounts);
		}
		sqlBuffer = null;

		// Update the Sender running countres (If record found for user running
		// counters)
		sqlBuffer = new StringBuffer(" UPDATE user_transfer_counts  SET ");
		sqlBuffer
				.append(" daily_in_count = ?, daily_in_value = ?, weekly_in_count = ?, weekly_in_value = ?,");
		sqlBuffer
				.append(" monthly_in_count = ?, monthly_in_value = ? ,daily_out_count =?, daily_out_value=?, ");
		sqlBuffer
				.append(" weekly_out_count=?, weekly_out_value =?, monthly_out_count=?, monthly_out_value=?, ");
		sqlBuffer
				.append(" outside_daily_in_count=?, outside_daily_in_value=?, outside_weekly_in_count=?,");
		sqlBuffer
				.append(" outside_weekly_in_value=?, outside_monthly_in_count=?, outside_monthly_in_value=?, ");
		sqlBuffer
				.append(" outside_daily_out_count=?, outside_daily_out_value=?, outside_weekly_out_count=?, ");
		sqlBuffer
				.append(" outside_weekly_out_value=?, outside_monthly_out_count=?, outside_monthly_out_value=?, ");
		sqlBuffer
				.append(" daily_subscriber_out_count=?, daily_subscriber_out_value=?, weekly_subscriber_out_count=?, ");
		sqlBuffer
				.append(" weekly_subscriber_out_value=?, monthly_subscriber_out_count=?, monthly_subscriber_out_value=?, ");
		sqlBuffer
				.append(" last_out_time = ? , last_transfer_id=?,last_transfer_date=? ");
		sqlBuffer.append(" WHERE user_id = ?  ");
		final String updateSenderTransferCounts = sqlBuffer.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY updateSenderTransferCounts="
					+ updateSenderTransferCounts);
		}
		sqlBuffer = null;

		// Insert the record in user_transfer_counts (If no record found for
		// user running counters)
		sqlBuffer = new StringBuffer(" INSERT INTO user_transfer_counts ( ");
		sqlBuffer
				.append(" daily_in_count, daily_in_value, weekly_in_count, weekly_in_value, monthly_in_count, ");
		sqlBuffer
				.append(" monthly_in_value, last_in_time, last_transfer_id,last_transfer_date,user_id ) ");
		sqlBuffer.append(" VALUES (?,?,?,?,?,?,?,?,?,?) ");
		final String insertTransferCounts = sqlBuffer.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY insertTransferCounts="
					+ insertTransferCounts);
		}
		sqlBuffer = null;

		// Insert the record in user_transfer_counts for Sender (If no record
		// found for user running counters)
		sqlBuffer = new StringBuffer(" INSERT INTO user_transfer_counts ( ");
		sqlBuffer
				.append(" daily_out_count, daily_out_value, weekly_out_count, weekly_out_value, monthly_out_count, ");
		sqlBuffer
				.append(" monthly_out_value, last_out_time, last_transfer_id,last_transfer_date,user_id ) ");
		sqlBuffer.append(" VALUES (?,?,?,?,?,?,?,?,?,?) ");
		final String insertSenderTransferCounts = sqlBuffer.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY insertSenderTransferCounts="
					+ insertSenderTransferCounts);
		}
		sqlBuffer = null;

		// Select the transfer profile product values(These will be used for
		// checking max balance of user)
		sqlBuffer = new StringBuffer(
				"SELECT GREATEST(tpp.min_residual_balance,catpp.min_residual_balance) min_residual_balance, ");
		sqlBuffer
				.append(" LEAST(tpp.max_balance,catpp.max_balance) max_balance ");
		sqlBuffer
				.append(" FROM transfer_profile_products tpp,transfer_profile tp, transfer_profile catp,transfer_profile_products catpp ");
		sqlBuffer
				.append(" WHERE tpp.profile_id=? AND tpp.product_code = ? AND tpp.profile_id=tp.profile_id AND catp.profile_id=catpp.profile_id ");
		sqlBuffer
				.append(" AND tpp.product_code=catpp.product_code AND tp.category_code=catp.category_code AND catp.parent_profile_id=? AND catp.status=? AND tp.network_code = catp.network_code	 ");
		final String loadTransferProfileProduct = sqlBuffer.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY loadTransferProfileProduct="
					+ loadTransferProfileProduct);
		}
		sqlBuffer = null;

		// The query below is used to insert the record in channel transfer
		// items table for the order that is closed
		sqlBuffer = new StringBuffer(" INSERT INTO channel_transfers_items ");
		sqlBuffer
				.append("(approved_quantity, commission_profile_detail_id, commission_rate, commission_type, commission_value, mrp,  ");
		sqlBuffer
				.append(" net_payable_amount, payable_amount, product_code, receiver_previous_stock, required_quantity, s_no,  ");
		sqlBuffer
				.append(" sender_previous_stock, tax1_rate, tax1_type, tax1_value, tax2_rate, tax2_type, tax2_value, tax3_rate, tax3_type,  ");
		sqlBuffer
				.append(" tax3_value, transfer_date, transfer_id, user_unit_price, ");
		sqlBuffer
				.append(" sender_debit_quantity, receiver_credit_quantity, sender_post_stock, receiver_post_stock,COMMISION_QUANTITY,oth_commission_type,oth_commission_rate,oth_commission_value )  ");
		sqlBuffer
				.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)  ");
		final String insertIntoChannelTransferItem = sqlBuffer.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY insertIntoChannelTransferItem="
					+ insertIntoChannelTransferItem);
		}
		sqlBuffer = null;

		// The query below is used to insert the record in channel transfers
		// table for the order that is cloaed
		sqlBuffer = new StringBuffer(" INSERT INTO channel_transfers ");
		sqlBuffer
				.append(" (cancelled_by, cancelled_on, channel_user_remarks, close_date, commission_profile_set_id, commission_profile_ver, ");
		sqlBuffer
				.append(" created_by, created_on, domain_code, ext_txn_date, ext_txn_no, first_approved_by, first_approved_on, ");
		sqlBuffer
				.append(" first_approver_limit, first_approver_remarks, batch_date, batch_no, from_user_id, grph_domain_code, ");
		sqlBuffer
				.append(" modified_by, modified_on, net_payable_amount, network_code, network_code_for, payable_amount, pmt_inst_amount, ");
		sqlBuffer
				.append("  product_type, receiver_category_code, receiver_grade_code, ");
		sqlBuffer
				.append(" receiver_txn_profile, reference_no, request_gateway_code, request_gateway_type, requested_quantity, second_approved_by, ");
		sqlBuffer
				.append(" second_approved_on, second_approver_limit, second_approver_remarks,  ");
		sqlBuffer
				.append("  source, status, third_approved_by, third_approved_on, third_approver_remarks, to_user_id,  ");
		sqlBuffer
				.append(" total_tax1, total_tax2, total_tax3, transfer_category, transfer_date, transfer_id, transfer_initiated_by, ");
		sqlBuffer
				.append(" transfer_mrp, transfer_sub_type, transfer_type, type,sender_category_code,");
		sqlBuffer
				.append(" control_transfer,to_msisdn,to_domain_code,to_grph_domain_code, sms_default_lang, sms_second_lang,active_user_id, ");
		sqlBuffer.append(" sender_grade_code,sender_txn_profile,msisdn,dual_comm_type,oth_comm_prf_set_id) ");
		sqlBuffer
				.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
		final String insertIntoChannelTransfer = sqlBuffer.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY insertIntoChannelTransfer="
					+ insertIntoChannelTransfer);
		}
		sqlBuffer = null;

		// The query below is used to get the balance information of user with
		// product.
		// This information will be send in message to user
		sqlBuffer = new StringBuffer(" SELECT UB.product_code,UB.balance, ");
		sqlBuffer.append(" PROD.product_short_code, PROD.short_name ");
		sqlBuffer.append(" FROM user_balances UB,products PROD ");
		sqlBuffer
				.append(" WHERE UB.user_id = ?  AND UB.network_code = ? AND UB.network_code_for = ? AND UB.product_code=PROD.product_code ");
		final String selectBalanceInfoForMessage = sqlBuffer.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY selectBalanceInfoForMessage="
					+ selectBalanceInfoForMessage);
		}
		sqlBuffer = null;

		// added by nilesh : added two new columns threshold_type and remark
		final StringBuffer strBuffThresholdInsert = new StringBuffer();
		strBuffThresholdInsert.append(" INSERT INTO user_threshold_counter ");
		strBuffThresholdInsert
				.append(" ( user_id,transfer_id , entry_date, entry_date_time, network_code, product_code , ");
		strBuffThresholdInsert
				.append(" type , transaction_type, record_type, category_code,previous_balance,current_balance, threshold_value, threshold_type, remark ) ");
		strBuffThresholdInsert.append(" VALUES ");
		strBuffThresholdInsert.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
		final String insertUserThreshold = strBuffThresholdInsert.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY insertUserThreshold="
					+ insertUserThreshold);
		}

		// added by vikram

		int totalSuccessRecords = 0;
		Date date = null;
		ChannelTransferVO channelTransferVO = null;
		//partial
		ArrayList<PushMessage> pushMessList =null;
		try {
			

			pstmtSelectTrfRule = p_con.prepareStatement(strBuffSelectTrfRule
					.toString());
			pstmtSelectTrfRuleProd = p_con
					.prepareStatement(strBuffSelectTrfRuleProd.toString());
			pstmtSelectCProfileProd = p_con
					.prepareStatement(strBuffSelectCProfileProd.toString());
			pstmtSelectCProfileProdDetail = p_con
					.prepareStatement(strBuffSelectCProfileProdDetail
							.toString());
			pstmtSelectTProfileProd = p_con
					.prepareStatement(strBuffSelectTProfileProd.toString());

			// pstmtInsertBatchMaster=(OraclePreparedStatement)p_con.prepareStatement(strBuffInsertBatchMaster.toString());//commented
			// for DB2
			// pstmtInsertBatchItems=(OraclePreparedStatement)p_con.prepareStatement(strBuffInsertBatchItems.toString());//commented
			// for DB2
			pstmtInsertBatchMaster = (PreparedStatement) p_con
					.prepareStatement(strBuffInsertBatchMaster.toString());
			pstmtInsertBatchItems = (PreparedStatement) p_con
					.prepareStatement(strBuffInsertBatchItems.toString());
			pstmtUpdateBatchMaster = p_con
					.prepareStatement(strBuffUpdateBatchMaster.toString());

			pstmtLoadUser = p_con.prepareStatement(sqlLoadUser);
			pstmtSelectUserBalances = p_con
					.prepareStatement(selectUserBalances);
			pstmtUpdateUserBalances = p_con
					.prepareStatement(updateUserBalances);
			pstmtSelectSenderBalance = p_con
					.prepareStatement(selectUserBalances);

			pstmtInsertSenderDailyBalances = p_con
					.prepareStatement(insertDailyBalances);
			pstmtUpdateSenderBalanceOn = p_con
					.prepareStatement(updateUserBalances);
			pstmtUpdateSenderBalance = p_con.prepareStatement(updateBalance);
			pstmtInsertUserDailyBalances = p_con
					.prepareStatement(insertDailyBalances);
			pstmtSelectTransferCounts = p_con
					.prepareStatement(selectTransferCounts);

			pstmtSelectBalance = p_con.prepareStatement(selectBalance);

			pstmtUpdateBalance = p_con.prepareStatement(updateBalance);
			pstmtInsertBalance = p_con.prepareStatement(insertBalance);

			pstmtSelectSenderTransferCounts = p_con
					.prepareStatement(selectTransferCounts);
			pstmtSelectProfileCounts = p_con
					.prepareStatement(selectProfileInCounts);
			pstmtUpdateTransferCounts = p_con
					.prepareStatement(updateTransferCounts);
			pstmtSelectSenderProfileOutCounts = p_con
					.prepareStatement(selectProfileOutCounts);
			pstmtUpdateSenderTransferCounts = p_con
					.prepareStatement(updateSenderTransferCounts);
			pstmtInsertSenderTransferCounts = p_con
					.prepareStatement(insertSenderTransferCounts);
			// pstmtUpdateTransferCounts=p_con.prepareStatement(updateTransferCounts)

			pstmtInsertTransferCounts = p_con
					.prepareStatement(insertTransferCounts);

			pstmtLoadTransferProfileProduct = p_con
					.prepareStatement(loadTransferProfileProduct);

			pstmtInsertIntoChannelTransferItems = p_con
					.prepareStatement(insertIntoChannelTransferItem);
			// pstmtInsertIntoChannelTranfers=(OraclePreparedStatement)p_con.prepareStatement(insertIntoChannelTransfer);//commented
			// for DB2
			pstmtInsertIntoChannelTranfers = (PreparedStatement) p_con
					.prepareStatement(insertIntoChannelTransfer);
			pstmtSelectBalanceInfoForMessage = p_con
					.prepareStatement(selectBalanceInfoForMessage);
			psmtInsertUserThreshold = p_con
					.prepareStatement(insertUserThreshold);

			// pstmtUpdateSenderBalanceOn=p_con.prepareStatement(updateUserBalances)
			ChannelTransferRuleVO rulesVO = null;
			ArrayList channelTransferItemVOList = null;
			int index = 0;
			C2CBatchItemsVO batchItemsVO = null;

			final HashMap transferRuleMap = new HashMap();
			final HashMap transferRuleNotExistMap = new HashMap();
			final HashMap transferRuleProdNotExistMap = new HashMap();
			final HashMap transferProfileMap = new HashMap();
			long requestedValue = 0;
			long minTrfValue = 0;
			long maxTrfValue = 0;
			long multipleOf = 0;
			ArrayList transferItemsList = null;
			final MessageGatewayVO messageGatewayVO = MessageGatewayCache.getObject(defaultWebGatewayCode);
			ChannelTransferItemsVO channelTransferItemsVO = null;
			int m = 0;
			final String network_id = null;
			TransferProfileProductVO transferProfileProductVO = null;
			ChannelUserVO channelUserVO = null;
			// ChannelTransferVO channelTransferVO=null
			// ChannelTransferItemsVO channelTransferItemVO=null
			TransferProfileVO transferProfileVO = null;
			TransferProfileVO senderTfrProfileCheckVO = null;
			date = new Date();
			int dayDifference = 0;
			Date dailyBalanceUpdatedOn = null;
			int k = 0;
			boolean terminateProcessing = false;
			long maxBalance = 0;
			boolean isNotToExecuteQuery = false;
			long balance = -1;
			long senderBalance = -1;
			long previousUserBalToBeSetChnlTrfItems = -1;
			long previousSenderBalToBeSetChnlTrfItems = -1;
			UserTransferCountsVO countsVO = null;
			UserTransferCountsVO senderCountsVO = null;
			boolean flag = true;
			// insert the master data
			index = 0;
			++index;
			pstmtInsertBatchMaster.setString(index,
					p_batchMasterVO.getBatchId());
			++index;
			pstmtInsertBatchMaster.setString(index,
					p_batchMasterVO.getNetworkCode());
			++index;
			pstmtInsertBatchMaster.setString(index,
					p_batchMasterVO.getNetworkCodeFor());

			// pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);//commented
			// for DB2
			++index;
			pstmtInsertBatchMaster.setString(index,
					p_batchMasterVO.getBatchName());
			++index;
			pstmtInsertBatchMaster
					.setString(index, p_batchMasterVO.getStatus());
			++index;
			pstmtInsertBatchMaster.setString(index,
					p_batchMasterVO.getDomainCode());
			++index;
			pstmtInsertBatchMaster.setString(index,
					p_batchMasterVO.getProductCode());
			++index;
			pstmtInsertBatchMaster.setString(index,
					p_batchMasterVO.getBatchFileName());
			++index;
			pstmtInsertBatchMaster.setLong(index,
					p_batchMasterVO.getBatchTotalRecord());
			++index;
			pstmtInsertBatchMaster.setTimestamp(index, BTSLUtil
					.getTimestampFromUtilDate(p_batchMasterVO.getBatchDate()));
			++index;
			pstmtInsertBatchMaster.setString(index,
					p_batchMasterVO.getCreatedBy());
			++index;
			pstmtInsertBatchMaster.setTimestamp(index, BTSLUtil
					.getTimestampFromUtilDate(p_batchMasterVO.getCreatedOn()));
			++index;
			pstmtInsertBatchMaster.setString(index,
					p_batchMasterVO.getModifiedBy());
			++index;
			pstmtInsertBatchMaster.setTimestamp(index, BTSLUtil
					.getTimestampFromUtilDate(p_batchMasterVO.getModifiedOn()));

			// pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);//commented
			// for DB2
			++index;
			pstmtInsertBatchMaster.setString(index,
					p_batchMasterVO.getDefaultLang());
			// pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);//commented
			// for DB2
			++index;
			pstmtInsertBatchMaster.setString(index,
					p_batchMasterVO.getSecondLang());
			++index;
			pstmtInsertBatchMaster
					.setString(index, p_batchMasterVO.getUserId());

			int queryExecutionCount = pstmtInsertBatchMaster.executeUpdate();
			if (queryExecutionCount <= 0) {
				p_con.rollback();
				LOG.error(methodName,
						"Unable to insert in the batch master table.");
				BatchC2CFileProcessLog
						.detailLog(
								methodName,
								p_batchMasterVO,
								batchItemsVO,
								"FAIL : DB Error Unable to insert in the batch master table",
								"queryExecutionCount=" + queryExecutionCount);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,
						EventComponentI.SYSTEM, EventStatusI.RAISED,
						EventLevelI.FATAL,
						"C2CBatchTransferWebDAO[closeBatchC2CTransfer]", "",
						"", "", "Unable to insert in the batch master table.");
				throw new BTSLBaseException(this, methodName,
						"error.general.sql.processing");
			}
			// ends here

			String msgArr[] = null;
			//partial
			pushMessList = new ArrayList<PushMessage>();
			int batchItemsListSize = p_batchItemsList.size();
			for (int i = 0, j = batchItemsListSize; i < j; i++) {
				
				boolean isExpectionOccuerred=false;
				
				try{
				terminateProcessing = false;
				batchItemsVO = (C2CBatchItemsVO) p_batchItemsList.get(i);
				// check the uniqueness of the external txn number

				// load the product's informaiton.
				if (transferRuleNotExistMap.get(batchItemsVO.getCategoryCode()) == null) {
					if (transferRuleProdNotExistMap.get(batchItemsVO
							.getCategoryCode()) == null) {
						if (transferRuleMap.get(batchItemsVO.getCategoryCode()) == null) {
							index = 0;
							++index;
							pstmtSelectTrfRule.setString(index,
									p_batchMasterVO.getNetworkCode());
							++index;
							pstmtSelectTrfRule.setString(index,
									p_batchMasterVO.getDomainCode());
							++index;
							pstmtSelectTrfRule.setString(index,
									batchItemsVO.getCategoryCode());
							rsSelectTrfRule = pstmtSelectTrfRule.executeQuery();
							pstmtSelectTrfRule.clearParameters();
							if (rsSelectTrfRule.next()) {
								rulesVO = new ChannelTransferRuleVO();
								rulesVO.setTransferRuleID(rsSelectTrfRule
										.getString("transfer_rule_id"));
								rulesVO.setTransferType(rsSelectTrfRule
										.getString("transfer_type"));
								rulesVO.setTransferAllowed(rsSelectTrfRule
										.getString("transfer_allowed"));
								index = 0;
								++index;
								pstmtSelectTrfRuleProd.setString(index,
										rulesVO.getTransferRuleID());
								++index;
								pstmtSelectTrfRuleProd.setString(index,
										p_batchMasterVO.getProductCode());
								rsSelectTrfRuleProd = pstmtSelectTrfRuleProd
										.executeQuery();
								pstmtSelectTrfRuleProd.clearParameters();
								if (!rsSelectTrfRuleProd.next()) {
									transferRuleProdNotExistMap.put(
											batchItemsVO.getCategoryCode(),
											batchItemsVO.getCategoryCode());
									// put error log Prodcuct is not in the
									// transfer rule
									errorVO = new ListValueVO(
											batchItemsVO.getMsisdn(),
											String.valueOf(batchItemsVO
													.getRecordNumber()),
											p_messages
													.getMessage(p_locale,
															"batchc2c.initiatebatchc2ctransfer.msg.error.prodnotintrfrule"));
									errorList.add(errorVO);
									BatchC2CFileProcessLog
											.detailLog(
													methodName,
													p_batchMasterVO,
													batchItemsVO,
													"FAIL : Product is not in the transfer rule",
													"");
									continue;
								}
								transferRuleMap
										.put(batchItemsVO.getCategoryCode(),
												rulesVO);
							} else {
								transferRuleNotExistMap.put(
										batchItemsVO.getCategoryCode(),
										batchItemsVO.getCategoryCode());
								// put error log transfer rule not defined
								errorVO = new ListValueVO(
										batchItemsVO.getMsisdn(),
										String.valueOf(batchItemsVO
												.getRecordNumber()),
										p_messages
												.getMessage(p_locale,
														"batchc2c.initiatebatchc2ctransfer.msg.error.trfrulenotdefined"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog.detailLog(methodName,
										p_batchMasterVO, batchItemsVO,
										"FAIL : Transfer rule not defined", "");
								continue;
							}
						}// transfer rule loading
					}// Procuct is not associated with transfer rule not defined
						// check
					else {
						// put error log Procuct is not in the transfer rule
						errorVO = new ListValueVO(
								batchItemsVO.getMsisdn(),
								String.valueOf(batchItemsVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.initiatebatchc2ctransfer.msg.error.prodnotintrfrule"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,
								p_batchMasterVO, batchItemsVO,
								"FAIL : Product is not in the transfer rule",
								"");
						continue;
					}
				}// transfer rule not defined check
				else {
					// put error log transfer rule not defined
					errorVO = new ListValueVO(
							batchItemsVO.getMsisdn(),
							String.valueOf(batchItemsVO.getRecordNumber()),
							p_messages
									.getMessage(p_locale,
											"batchc2c.initiatebatchc2ctransfer.msg.error.trfrulenotdefined"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.detailLog(methodName,
							p_batchMasterVO, batchItemsVO,
							"FAIL : Transfer rule not defined", "");
					continue;
				}
				rulesVO = (ChannelTransferRuleVO) transferRuleMap
						.get(batchItemsVO.getCategoryCode());
				if (PretupsI.NO.equals(rulesVO.getTransferAllowed())) {
					// put error according to the transfer rule C2C transfer is
					// not allowed.
					errorVO = new ListValueVO(
							batchItemsVO.getMsisdn(),
							String.valueOf(batchItemsVO.getRecordNumber()),
							p_messages
									.getMessage(p_locale,
											"batchc2c.initiatebatchc2ctransfer.msg.error.c2cnotallowed"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog
							.detailLog(
									methodName,
									p_batchMasterVO,
									batchItemsVO,
									"FAIL : According to the transfer rule C2C transfer is not allowed",
									"");
					continue;
				}
				// check the transfer profile product code
				// transfer profile check ends here
				if (transferProfileMap.get(batchItemsVO.getTxnProfile()) == null) {
					index = 0;
					++index;
					pstmtSelectTProfileProd.setString(index,
							batchItemsVO.getTxnProfile());
					++index;
					pstmtSelectTProfileProd.setString(index,
							p_batchMasterVO.getProductCode());
					++index;
					pstmtSelectTProfileProd.setString(index,
							PretupsI.PARENT_PROFILE_ID_CATEGORY);
					rsSelectTProfileProd = pstmtSelectTProfileProd
							.executeQuery();
					pstmtSelectTProfileProd.clearParameters();
					if (!rsSelectTProfileProd.next()) {
						transferProfileMap.put(batchItemsVO.getTxnProfile(),
								"false");
						// put error Transfer profile for this product is not
						// define
						errorVO = new ListValueVO(
								batchItemsVO.getMsisdn(),
								String.valueOf(batchItemsVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.initiatebatchc2ctransfer.msg.error.trfprofilenotdefined"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog
								.detailLog(
										methodName,
										p_batchMasterVO,
										batchItemsVO,
										"FAIL : Transfer profile for this product is not defined",
										"");
						continue;
					}
					transferProfileMap
							.put(batchItemsVO.getTxnProfile(), "true");
				} else {

					if ("false".equals(transferProfileMap.get(batchItemsVO
							.getTxnProfile()))) {
						// put error Transfer profile for this product is not
						// define
						errorVO = new ListValueVO(
								batchItemsVO.getMsisdn(),
								String.valueOf(batchItemsVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.initiatebatchc2ctransfer.msg.error.trfprofilenotdefined"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog
								.detailLog(
										methodName,
										p_batchMasterVO,
										batchItemsVO,
										"FAIL : Transfer profile for this product is not defined",
										"");
						continue;
					}
				}

				// check the commisson profile applicability and other checks
				// related to the commission profile
				index = 0;
				++index;
				pstmtSelectCProfileProd.setString(index, p_batchMasterVO.getProductCode());
				++index;
				pstmtSelectCProfileProd.setString(index, batchItemsVO.getCommissionProfileSetId());
				++index;
				pstmtSelectCProfileProd.setString(index, batchItemsVO.getCommissionProfileVer());
				++index;
                pstmtSelectCProfileProd.setString(index, (isTransationTypeAlwd)?PretupsI.TRANSFER_TYPE_C2C:PretupsI.ALL);
                ++index;
                pstmtSelectCProfileProd.setString(index, PretupsI.ALL);
				rsSelectCProfileProd = pstmtSelectCProfileProd.executeQuery();
				pstmtSelectCProfileProd.clearParameters();
				if (!rsSelectCProfileProd.next()) {
					// put error commission profile for this product is not
					// defined
					errorVO = new ListValueVO(
							batchItemsVO.getMsisdn(),
							String.valueOf(batchItemsVO.getRecordNumber()),
							p_messages
									.getMessage(p_locale,
											"batchc2c.initiatebatchc2ctransfer.msg.error.commprfnotdefined"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog
							.detailLog(
									methodName,
									p_batchMasterVO,
									batchItemsVO,
									"FAIL : Commission profile for this product is not defined",
									"");
					continue;
				}
				requestedValue = batchItemsVO.getRequestedQuantity();
				minTrfValue = rsSelectCProfileProd
						.getLong("min_transfer_value");
				maxTrfValue = rsSelectCProfileProd
						.getLong("max_transfer_value");
				if (minTrfValue > requestedValue
						|| maxTrfValue < requestedValue) {
					msgArr = new String[3];
					msgArr[0] = PretupsBL.getDisplayAmount(requestedValue);
					msgArr[1] = PretupsBL.getDisplayAmount(minTrfValue);
					msgArr[2] = PretupsBL.getDisplayAmount(maxTrfValue);
					// put error requested quantity is not between min and max
					// values
					errorVO = new ListValueVO(
							batchItemsVO.getMsisdn(),
							String.valueOf(batchItemsVO.getRecordNumber()),
							p_messages
									.getMessage(
											p_locale,
											"batchc2c.initiatebatchc2ctransfer.msg.error.qtymaxmin",
											msgArr));
					msgArr = null;
					errorList.add(errorVO);
					BatchC2CFileProcessLog
							.detailLog(
									methodName,
									p_batchMasterVO,
									batchItemsVO,
									"FAIL : Requested quantity is not between min and max values",
									"minTrfValue=" + minTrfValue
											+ ", maxTrfValue=" + maxTrfValue);
					continue;
				}
				multipleOf = rsSelectCProfileProd
						.getLong("transfer_multiple_off");
				if (requestedValue % multipleOf != 0) {
					// put error requested quantity is not multiple of
					errorVO = new ListValueVO(
							batchItemsVO.getMsisdn(),
							String.valueOf(batchItemsVO.getRecordNumber()),
							p_messages
									.getMessage(
											p_locale,
											"batchc2c.initiatebatchc2ctransfer.msg.error.notmulof",
											new String[] { PretupsBL
													.getDisplayAmount(multipleOf) }));
					errorList.add(errorVO);
					BatchC2CFileProcessLog
							.detailLog(
									methodName,
									p_batchMasterVO,
									batchItemsVO,
									"FAIL : Requested quantity is not in multiple value",
									"multiple of=" + multipleOf);
					continue;
				}

				index = 0;
				++index;
				pstmtSelectCProfileProdDetail.setString(index,
						rsSelectCProfileProd
								.getString("comm_profile_products_id"));
				++index;
				pstmtSelectCProfileProdDetail.setLong(index, requestedValue);
				++index;
				pstmtSelectCProfileProdDetail.setLong(index, requestedValue);
				rsSelectCProfileProdDetail = pstmtSelectCProfileProdDetail
						.executeQuery();
				pstmtSelectCProfileProdDetail.clearParameters();
				if (!rsSelectCProfileProdDetail.next()) {
					// put error commission profile slab is not define for the
					// requested value
					errorVO = new ListValueVO(
							batchItemsVO.getMsisdn(),
							String.valueOf(batchItemsVO.getRecordNumber()),
							p_messages
									.getMessage(p_locale,
											"batchc2c.initiatebatchc2ctransfer.msg.error.commslabnotdefined"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog
							.detailLog(
									methodName,
									p_batchMasterVO,
									batchItemsVO,
									"FAIL : Commission profile slab is not define for the requested value",
									"");
					continue;
				}
				// to calculate tax
				transferItemsList = new ArrayList();
				channelTransferItemsVO = new ChannelTransferItemsVO();
				// this value will be inserted into the table as the requested
				// qty
				channelTransferItemsVO.setRequiredQuantity(requestedValue);
				// this value will be used in the tax calculation.
				channelTransferItemsVO.setRequestedQuantity(PretupsBL
						.getDisplayAmount(requestedValue));
				channelTransferItemsVO
						.setCommProfileDetailID(rsSelectCProfileProdDetail
								.getString("comm_profile_detail_id"));
				channelTransferItemsVO.setUnitValue(p_batchMasterVO
						.getProductMrp());
				channelTransferItemsVO.setCommRate(rsSelectCProfileProdDetail
						.getLong("commission_rate"));
				channelTransferItemsVO.setCommType(rsSelectCProfileProdDetail
						.getString("commission_type"));
				channelTransferItemsVO.setDiscountRate(rsSelectCProfileProd
						.getLong("discount_rate"));
				channelTransferItemsVO.setDiscountType(rsSelectCProfileProd
						.getString("discount_type"));
				channelTransferItemsVO.setTax1Rate(rsSelectCProfileProdDetail
						.getLong("tax1_rate"));
				channelTransferItemsVO.setTax1Type(rsSelectCProfileProdDetail
						.getString("tax1_type"));
				channelTransferItemsVO.setTax2Rate(rsSelectCProfileProdDetail
						.getLong("tax2_rate"));
				channelTransferItemsVO.setTax2Type(rsSelectCProfileProdDetail
						.getString("tax2_type"));
				channelTransferItemsVO.setTax3Rate(rsSelectCProfileProdDetail
						.getLong("tax3_rate"));
				channelTransferItemsVO.setTax3Type(rsSelectCProfileProdDetail
						.getString("tax3_type"));
				// if(PretupsI.YES.equals(rsSelectCProfileProd.getString("taxes_on_foc_applicable")))
				// {
				// channelTransferItemsVO.setTaxOnC2CTransfer(PretupsI.YES)
				// }
				// else
				// channelTransferItemsVO.setTaxOnC2CTransfer(PretupsI.NO)
				// added by vikram
				if (PretupsI.YES.equals(rsSelectCProfileProd
						.getString("taxes_on_channel_transfer"))) {
					channelTransferItemsVO
							.setTaxOnChannelTransfer(PretupsI.YES);
				} else {
					channelTransferItemsVO.setTaxOnChannelTransfer(PretupsI.NO);
				}
				transferItemsList.add(channelTransferItemsVO);

				channelTransferVO = new ChannelTransferVO();
				channelTransferVO
						.setChannelTransferitemsVOList(transferItemsList);
				// channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER)
				channelTransferVO.setTransferSubType(batchItemsVO
						.getTransferSubType());
				// ChannelTransferBL.calculateMRPWithTaxAndDiscount(channelTransferVO,PretupsI.TRANSFER_TYPE_FOC)
				if(isOthComChnl){
					if(!BTSLUtil.isNullString(batchItemsVO.getMsisdn()))
						channelTransferVO.setToUserMsisdn(batchItemsVO.getMsisdn());
					else if(!BTSLUtil.isNullString(batchItemsVO.getLoginID()))
						channelTransferVO.setToUserMsisdn(((ChannelUserVO)new ChannelUserWebDAO().loadChannelUserDetailsByLoginIDANDORMSISDN(p_con,"",batchItemsVO.getLoginID())).getMsisdn());
					else if(!BTSLUtil.isNullString(batchItemsVO.getExternalCode()))
						channelTransferVO.setToUserMsisdn(((ChannelUserVO)new ChannelUserDAO().loadChnlUserDetailsByExtCode(p_con, batchItemsVO.getExternalCode())).getMsisdn());
				 channelTransferVO.setCommProfileSetId(batchItemsVO.getCommissionProfileSetId());
				 channelTransferVO.setCommProfileVersion(batchItemsVO.getCommissionProfileVer());
					if(messageGatewayVO!=null && messageGatewayVO.getRequestGatewayVO()!=null) {
	  					channelTransferVO.setRequestGatewayCode(messageGatewayVO.getRequestGatewayVO().getGatewayCode());
	  					channelTransferVO.setRequestGatewayType(messageGatewayVO.getGatewayType());
  					}
				}
				ChannelTransferBL.calculateMRPWithTaxAndDiscount(
						channelTransferVO, PretupsI.TRANSFER_TYPE_C2C);
				// taxes on C2C required
				// ends commission profile validaiton

				pstmtLoadUser.clearParameters();
				m = 0;
				++m;
				pstmtLoadUser.setString(m, batchItemsVO.getUserId());
				try{
				rs = pstmtLoadUser.executeQuery();
				// (record found for user i.e. receiver) if this condition is
				// not true then made entry in logs and leave this data.
				if (rs.next()) {
					channelUserVO = new ChannelUserVO();
					channelUserVO.setUserID(batchItemsVO.getUserId());
					channelUserVO.setStatus(rs.getString("userstatus"));
					channelUserVO.setInSuspend(rs.getString("in_suspend"));
					channelUserVO.setCommissionProfileStatus(rs
							.getString("commprofilestatus"));
					channelUserVO.setCommissionProfileLang1Msg(rs
							.getString("comprf_lang_1_msg"));
					channelUserVO.setCommissionProfileLang2Msg(rs
							.getString("comprf_lang_2_msg"));
					channelUserVO.setTransferProfileStatus(rs
							.getString("profile_status"));
					language = rs.getString("phone_language");
					country = rs.getString("country");
					channelUserVO.setGeographicalCode(rs
							.getString("grph_domain_code"));
					
					// (user status is checked) if this condition is true then
					// made entry in logs and leave this data.
					/*
					 * if(!PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.
					 * getStatus())) { p_con.rollback() errorVO=new
					 * ListValueVO(batchItemsVO.getMsisdn(),String.valueOf
					 * (batchItemsVO
					 * .getRecordNumber()),p_messages.getMessage(p_locale
					 * ,"batchc2c.batchapprovereject.msg.error.usersuspend"))
					 * errorList.add(errorVO)
					 * BatchC2CFileProcessLog.detailLog(methodName
					 * ,null,batchItemsVO
					 * ,"FAIL : User is suspend","Approval level") continue }
					 */
					// (commission profile status is checked) if this condition
					// is true then made entry in logs and leave this data.
					if (!PretupsI.YES.equals(channelUserVO
							.getCommissionProfileStatus())) {
						p_con.rollback();
						errorVO = new ListValueVO(
								batchItemsVO.getMsisdn(),
								String.valueOf(batchItemsVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.batchapprovereject.msg.error.comprofsuspend"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName, null,
								batchItemsVO,
								"FAIL : Commission profile suspend",
								"Approval level");
						continue;
					}
					// (transfer profile is checked) if this condition is true
					// then made entry in logs and leave this data.
					else if (!PretupsI.YES.equals(channelUserVO
							.getTransferProfileStatus())) {
						p_con.rollback();
						errorVO = new ListValueVO(
								batchItemsVO.getMsisdn(),
								String.valueOf(batchItemsVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.batchapprovereject.msg.error.trfprofsuspend"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName, null,
								batchItemsVO,
								"FAIL : Transfer profile suspend",
								"Approval level");
						continue;
					}
					// (user in suspend is checked) if this condition is true
					// then made entry in logs and leave this data.
					else if (channelUserVO.getInSuspend() != null
							&& PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND
									.equals(channelUserVO.getInSuspend())) {
						p_con.rollback();
						errorVO = new ListValueVO(
								batchItemsVO.getMsisdn(),
								String.valueOf(batchItemsVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.batchapprovereject.msg.error.userinsuspend"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName, null,
								batchItemsVO, "FAIL : User is IN suspend",
								"Approval level");
						continue;
					}
				}
				// (no record found for user i.e. receiver) if this condition is
				// true then make entry in logs and leave this data.
				else {
					p_con.rollback();
					errorVO = new ListValueVO(
							batchItemsVO.getMsisdn(),
							String.valueOf(batchItemsVO.getRecordNumber()),
							p_messages
									.getMessage(p_locale,
											"batchc2c.batchapprovereject.msg.error.nouser"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.detailLog(methodName, null,
							batchItemsVO, "FAIL : User not found",
							"Approval level");
					continue;
				}
				}
				finally{
					if(rs!=null)
						rs.close();
				}

				// creating the channelTransferVO here since C2CTransferID will
				// be required into the network stock
				// transaction table. Other information will be set into this VO
				// later
				// seting the current value for generation of the transfer ID.
				// This will be over write by the
				// bacth c2c items was created.
				channelTransferVO.setCreatedOn(date);
				channelTransferVO.setNetworkCode(p_batchMasterVO
						.getNetworkCode());
				channelTransferVO.setNetworkCodeFor(p_batchMasterVO
						.getNetworkCodeFor());

				// ChannelTransferBL.genrateTransferID(channelTransferVO)
				if (PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION
						.equals(batchItemsVO.getTransferType())
						&& PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER
								.equals(batchItemsVO.getTransferSubType())) {
					ChannelTransferBL
							.genrateChnnlToChnnlTrfID(channelTransferVO);
				} else if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN
						.equals(batchItemsVO.getTransferType())
						&& PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW
								.equals(batchItemsVO.getTransferSubType())) {
					ChannelTransferBL
							.genrateChnnlToChnnlWithdrawID(channelTransferVO);
				}
				/*
				 * else
				 * if(PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(batchItemsVO
				 * .getTransferType()) &&
				 * PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN
				 * .equals(batchItemsVO.getTransferSubType()))
				 * ChannelTransferBL.
				 * genrateChnnlToChnnlReturnID(channelTransferVO )
				 */
				c2cTransferID = channelTransferVO.getTransferID();
				// value is over writing since in the channel trasnfer table
				// created on should be same as when the
				// batch c2c item was created.
				channelTransferVO.setCreatedOn(batchItemsVO.getInitiatedOn());

				dayDifference = 0;

				dailyBalanceUpdatedOn = null;
				dayDifference = 0;

				pstmtSelectSenderBalance.clearParameters();
				m = 0;
				++m;
				pstmtSelectSenderBalance.setString(m, p_senderVO.getUserID());
				++m;
				pstmtSelectSenderBalance.setDate(m,
						BTSLUtil.getSQLDateFromUtilDate(date));
				// pstmtSelectSenderBalance.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date))
				try{
				rs = null;
				rs = pstmtSelectSenderBalance.executeQuery();
				while (rs.next()) {
					dailyBalanceUpdatedOn = rs
							.getDate("daily_balance_updated_on");
					senderPreviousBal = rs.getLong("balance");
					// if record exist check updated on date with current date
					// day differences to maintain the record of previous days.
					dayDifference = BTSLUtil.getDifferenceInUtilDates(
							dailyBalanceUpdatedOn, date);
					if (dayDifference > 0) {
						// if dates are not equal get the day differencts and
						// execute insert qurery no of times of the
						if (LOG.isDebugEnabled()) {
							LOG.debug("closeBatchC2CTransfer ",
									"Till now daily Stock is not updated on "
											+ date + ", day differences = "
											+ dayDifference);
						}

						for (k = 0; k < dayDifference; k++) {
							pstmtInsertSenderDailyBalances.clearParameters();
							m = 0;
							++m;
							pstmtInsertSenderDailyBalances.setDate(m, BTSLUtil
									.getSQLDateFromUtilDate(BTSLUtil
											.addDaysInUtilDate(
													dailyBalanceUpdatedOn, k)));
							++m;
							pstmtInsertSenderDailyBalances.setString(m,
									rs.getString("user_id"));
							++m;
							pstmtInsertSenderDailyBalances.setString(m,
									rs.getString("network_code"));
							++m;
							pstmtInsertSenderDailyBalances.setString(m,
									rs.getString("network_code_for"));
							++m;
							pstmtInsertSenderDailyBalances.setString(m,
									rs.getString("product_code"));
							++m;
							pstmtInsertSenderDailyBalances.setLong(m,
									rs.getLong("balance"));
							++m;
							pstmtInsertSenderDailyBalances.setLong(m,
									rs.getLong("prev_balance"));
							// pstmtInsertSenderDailyBalances.setString(++m,PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION)
							++m;
							pstmtInsertSenderDailyBalances.setString(m,
									batchItemsVO.getTransferType());
							++m;
							pstmtInsertSenderDailyBalances.setString(m,
									channelTransferVO.getTransferID());
							++m;
							pstmtInsertSenderDailyBalances.setTimestamp(m,
									BTSLUtil.getTimestampFromUtilDate(date));
							++m;
							pstmtInsertSenderDailyBalances.setTimestamp(m,
									BTSLUtil.getTimestampFromUtilDate(date));
							++m;
							pstmtInsertSenderDailyBalances.setString(m,
									PretupsI.DAILY_BALANCE_CREATION_TYPE_MAN);
							updateCount = pstmtInsertSenderDailyBalances
									.executeUpdate();
						// added to make code compatible with insertion in partitioned table in postgres
							updateCount = BTSLUtil.getInsertCount(updateCount); 
							if (updateCount <= 0) {
								p_con.rollback();
								errorVO = new ListValueVO(
										batchItemsVO.getMsisdn(),
										String.valueOf(batchItemsVO
												.getRecordNumber()),
										p_messages
												.getMessage(p_locale,
														"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog
										.detailLog(
												methodName,
												p_batchMasterVO,
												batchItemsVO,
												"FAIL : DB Error while inserting user daily balances table",
												"Approval level = "
														+ "No Approval required"
														+ ", updateCount ="
														+ updateCount);
								terminateProcessing = true;
								break;
							}
						}// end of for loop
						if (terminateProcessing) {
							BatchC2CFileProcessLog
									.detailLog(
											methodName,
											p_batchMasterVO,
											batchItemsVO,
											"FAIL : Terminting the procssing of this user as error while updation daily balance",
											"No Approval required");
							continue;
						}
						// Update the user balances table
						pstmtUpdateSenderBalanceOn.clearParameters();
						m = 0;
						++m;
						pstmtUpdateSenderBalanceOn.setTimestamp(m,
								BTSLUtil.getTimestampFromUtilDate(date));
						++m;
						pstmtUpdateSenderBalanceOn.setString(m,
								p_senderVO.getUserID());
						updateCount = pstmtUpdateSenderBalanceOn
								.executeUpdate();
						// (record not updated properly) if this condition is
						// true then made entry in logs and leave this data.
						if (updateCount <= 0) {
							p_con.rollback();
							errorVO = new ListValueVO(
									batchItemsVO.getMsisdn(),
									String.valueOf(batchItemsVO
											.getRecordNumber()),
									p_messages
											.getMessage(p_locale,
													"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog
									.detailLog(
											methodName,
											p_batchMasterVO,
											batchItemsVO,
											"FAIL : DB Error while updating user balances table for daily balance",
											"Approval level = "
													+ "No Approval required"
													+ ", updateCount="
													+ updateCount);
							continue;
						}
					}
				}// end of if condition
				}
				finally{
					if(rs!=null)
						rs.close();
				}
				maxBalance = 0;
				isNotToExecuteQuery = false;

				// select the record form the userBalances table.
				pstmtSelectUserBalances.clearParameters();
				m = 0;
				++m;
				pstmtSelectUserBalances.setString(m, channelUserVO.getUserID());
				++m;
				pstmtSelectUserBalances.setDate(m,
						BTSLUtil.getSQLDateFromUtilDate(date));
				try{
				rs = null;
				rs = pstmtSelectUserBalances.executeQuery();
				while (rs.next()) {
					dailyBalanceUpdatedOn = rs
							.getDate("daily_balance_updated_on");
					// if record exist check updated on date with current date
					// day differences to maintain the record of previous days.
					dayDifference = BTSLUtil.getDifferenceInUtilDates(
							dailyBalanceUpdatedOn, date);
					if (dayDifference > 0) {
						// if dates are not equal get the day differencts and
						// execute insert qurery no of times of the
						if (LOG.isDebugEnabled()) {
							LOG.debug("closeBatchC2CTransfer ",
									"Till now daily Stock is not updated on "
											+ date + ", day differences = "
											+ dayDifference);
						}

						for (k = 0; k < dayDifference; k++) {
							pstmtInsertUserDailyBalances.clearParameters();
							m = 0;
							++m;
							pstmtInsertUserDailyBalances.setDate(m, BTSLUtil
									.getSQLDateFromUtilDate(BTSLUtil
											.addDaysInUtilDate(
													dailyBalanceUpdatedOn, k)));
							++m;
							pstmtInsertUserDailyBalances.setString(m,
									rs.getString("user_id"));
							++m;
							pstmtInsertUserDailyBalances.setString(m,
									rs.getString("network_code"));
							++m;
							pstmtInsertUserDailyBalances.setString(m,
									rs.getString("network_code_for"));
							++m;
							pstmtInsertUserDailyBalances.setString(m,
									rs.getString("product_code"));
							++m;
							pstmtInsertUserDailyBalances.setLong(m,
									rs.getLong("balance"));
							++m;
							pstmtInsertUserDailyBalances.setLong(m,
									rs.getLong("prev_balance"));
							// pstmtInsertUserDailyBalances.setString(++m,PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION)
							++m;
							pstmtInsertUserDailyBalances.setString(m,
									batchItemsVO.getTransferType());
							++m;
							pstmtInsertUserDailyBalances.setString(m,
									channelTransferVO.getTransferID());
							++m;
							pstmtInsertUserDailyBalances.setTimestamp(m,
									BTSLUtil.getTimestampFromUtilDate(date));
							++m;
							pstmtInsertUserDailyBalances.setTimestamp(m,
									BTSLUtil.getTimestampFromUtilDate(date));
							++m;
							pstmtInsertUserDailyBalances.setString(m,
									PretupsI.DAILY_BALANCE_CREATION_TYPE_MAN);
							updateCount = pstmtInsertUserDailyBalances
									.executeUpdate();

							if (updateCount <= 0) {
								p_con.rollback();
								errorVO = new ListValueVO(
										batchItemsVO.getMsisdn(),
										String.valueOf(batchItemsVO
												.getRecordNumber()),
										p_messages
												.getMessage(p_locale,
														"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog
										.detailLog(
												methodName,
												p_batchMasterVO,
												batchItemsVO,
												"FAIL : DB Error while inserting user daily balances table",
												"Approval level = "
														+ "No Approval required"
														+ ", updateCount ="
														+ updateCount);
								terminateProcessing = true;
								break;
							}
						}// end of for loop
						if (terminateProcessing) {
							BatchC2CFileProcessLog
									.detailLog(
											methodName,
											p_batchMasterVO,
											batchItemsVO,
											"FAIL : Terminting the procssing of this user as error while updation daily balance",
											"Approval level = "
													+ "No Approval required");
							continue;
						}
						// Update the user balances table
						pstmtUpdateUserBalances.clearParameters();
						m = 0;
						++m;
						pstmtUpdateUserBalances.setTimestamp(m,
								BTSLUtil.getTimestampFromUtilDate(date));
						++m;
						pstmtUpdateUserBalances.setString(m,
								channelUserVO.getUserID());
						updateCount = pstmtUpdateUserBalances.executeUpdate();
						// (record not updated properly) if this condition is
						// true then made entry in logs and leave this data.
						if (updateCount <= 0) {
							p_con.rollback();
							errorVO = new ListValueVO(
									batchItemsVO.getMsisdn(),
									String.valueOf(batchItemsVO
											.getRecordNumber()),
									p_messages
											.getMessage(p_locale,
													"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
							errorList.add(errorVO);
							BatchC2CFileProcessLog
									.detailLog(
											methodName,
											p_batchMasterVO,
											batchItemsVO,
											"FAIL : DB Error while updating user balances table for daily balance",
											"Approval level = "
													+ "No Approval required"
													+ ", updateCount="
													+ updateCount);
							continue;
						}
					}
				}// end of if condition
				}
				finally{
					if(rs!=null)
						rs.close();
				}
				maxBalance = 0;
				isNotToExecuteQuery = false;
				// sender balance to be debited
				pstmtSelectBalance.clearParameters();
				m = 0;
				++m;
				pstmtSelectBalance.setString(m, p_senderVO.getUserID());
				++m;
				pstmtSelectBalance.setString(m,
						p_batchMasterVO.getProductCode());
				++m;
				pstmtSelectBalance.setString(m,
						p_batchMasterVO.getNetworkCode());
				++m;
				pstmtSelectBalance.setString(m,
						p_batchMasterVO.getNetworkCodeFor());
				try{
				rs = null;
				rs = pstmtSelectBalance.executeQuery();
				senderBalance = -1;
				previousSenderBalToBeSetChnlTrfItems = -1;
				if (rs.next()) {
					senderBalance = rs.getLong("balance");
					
				} else {
					p_con.rollback();
					errorVO = new ListValueVO(
							p_senderVO.getMsisdn(),
							String.valueOf(batchItemsVO.getRecordNumber()),
							p_messages
									.getMessage(p_locale,
											"batchc2c.batchc2capprove.closeOrderByBatch.sendernobal"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog
							.detailLog(
									methodName,
									p_batchMasterVO,
									batchItemsVO,
									"FAIL : DB Error while selecting user balances table for daily balance",
									"Approval level = "
											+ "No Approval required");
					continue;
				}
				}
				finally{
					if(rs!=null)
						rs.close();
				}
				if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN
						.equals(p_batchMasterVO.getTransferType())
						&& PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW
								.equals(p_batchMasterVO.getTransferSubType())) {
					previousSenderBalToBeSetChnlTrfItems = senderBalance;
					senderBalance += batchItemsVO.getRequestedQuantity();
				} else if ((PretupsI.CHANNEL_TRANSFER_TYPE_TRANSFER
						.equals(p_batchMasterVO.getTransferType()))
						&& PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER
								.equals(p_batchMasterVO.getTransferSubType())) {
					if (senderBalance == 0
							|| senderBalance
									- batchItemsVO.getRequestedQuantity() < 0) {
						p_con.rollback();
						errorVO = new ListValueVO(
								p_senderVO.getMsisdn(),
								String.valueOf(batchItemsVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.batchc2capprove.closeOrderByBatch.senderbalzero"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog
								.detailLog(
										methodName,
										p_batchMasterVO,
										batchItemsVO,
										"FAIL : DB Error while selecting user balances table for daily balance",
										"Approval level = "
												+ "No Approval required");
						continue;
					} else if (senderBalance != 0
							&& (senderBalance
									- batchItemsVO.getRequestedQuantity() >= 0)) {
						previousSenderBalToBeSetChnlTrfItems = senderBalance;
						senderBalance -= batchItemsVO.getRequestedQuantity();
					} else {
						previousSenderBalToBeSetChnlTrfItems = 0;
					}
				}
				m = 0;
				// update sender balance
				if (senderBalance > -1) {
					pstmtUpdateSenderBalance.clearParameters();
					handlerStmt = pstmtUpdateSenderBalance;
				}
				++m;
				handlerStmt.setLong(m, previousSenderBalToBeSetChnlTrfItems);
				++m;
				handlerStmt.setLong(m, senderBalance);
				++m;
				handlerStmt.setString(m, batchItemsVO.getTransferType());
				++m;
				handlerStmt.setString(m, c2cTransferID);
				++m;
				handlerStmt.setTimestamp(m,
						BTSLUtil.getTimestampFromUtilDate(date));
				++m;
				handlerStmt.setString(m, p_senderVO.getUserID());
				++m;
				handlerStmt.setString(m, p_batchMasterVO.getProductCode());
				++m;
				handlerStmt.setString(m, p_batchMasterVO.getNetworkCode());
				++m;
				handlerStmt.setString(m, p_batchMasterVO.getNetworkCodeFor());
				updateCount = handlerStmt.executeUpdate();
				handlerStmt.clearParameters();
				if (updateCount <= 0) {
					p_con.rollback();
					errorVO = new ListValueVO(
							batchItemsVO.getMsisdn(),
							String.valueOf(batchItemsVO.getRecordNumber()),
							p_messages
									.getMessage(p_locale,
											"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.detailLog(methodName,
							p_batchMasterVO, batchItemsVO,
							"FAIL : DB error while credit uer balance",
							"Approval level = " + "No Approval required");
					continue;
				}

				// thresholdValue=(Long)PreferenceCache.getControlPreference(PreferenceI.ZERO_BAL_THRESHOLD_VALUE,p_batchMasterVO.getNetworkCode(),
				// p_senderVO.getCategoryCode()); //threshold value
				// for zero balance counter..
				try {
					m = 0;
					boolean isUserThresholdEntryReq = false;
					String thresholdType = null;
					/*
					 * if(previousSenderBalToBeSetChnlTrfItems>=thresholdValue
					 * && senderBalance <=thresholdValue) {
					 * isUserThresholdEntryReq=true;
					 * thresholdType=PretupsI.BELOW_THRESHOLD_TYPE; } else
					 * if(previousSenderBalToBeSetChnlTrfItems<=thresholdValue
					 * && senderBalance >=thresholdValue) {
					 * isUserThresholdEntryReq=true;
					 * thresholdType=PretupsI.ABOVE_THRESHOLD_TYPE; }
					 */
					// added by nilesh
					transferProfileProductVO = TransferProfileProductCache
							.getTransferProfileDetails(
									p_senderVO.getTransferProfileID(),
									p_batchMasterVO.getProductCode());
					final String remark = null;
					String threshold_type = null;
					if (senderBalance <= transferProfileProductVO
							.getAltBalanceLong()
							&& senderBalance > transferProfileProductVO
									.getMinResidualBalanceAsLong()) {
						// isUserThresholdEntryReq=true
						thresholdValue = transferProfileProductVO
								.getAltBalanceLong();
						threshold_type = PretupsI.THRESHOLD_TYPE_ALERT;
					} else if (senderBalance <= transferProfileProductVO
							.getMinResidualBalanceAsLong()) {
						// isUserThresholdEntryReq=true
						thresholdValue = transferProfileProductVO
								.getMinResidualBalanceAsLong();
						threshold_type = PretupsI.THRESHOLD_TYPE_MIN;
					}
					// new
					if (previousSenderBalToBeSetChnlTrfItems >= thresholdValue
							&& senderBalance <= thresholdValue) {
						isUserThresholdEntryReq = true;
						thresholdType = PretupsI.BELOW_THRESHOLD_TYPE;
					} else if (previousSenderBalToBeSetChnlTrfItems <= thresholdValue
							&& senderBalance >= thresholdValue) {
						isUserThresholdEntryReq = true;
						thresholdType = PretupsI.ABOVE_THRESHOLD_TYPE;
					} else if (previousSenderBalToBeSetChnlTrfItems <= thresholdValue
							&& senderBalance <= thresholdValue) {
						isUserThresholdEntryReq = true;
						thresholdType = PretupsI.BELOW_THRESHOLD_TYPE;
					}
					// end

					if (isUserThresholdEntryReq) {
						if (LOG.isDebugEnabled()) {
							LOG.debug(methodName, "Entry in threshold counter"
									+ thresholdValue + ", prvbal: "
									+ previousSenderBalToBeSetChnlTrfItems
									+ "nbal" + senderBalance);
						}
						psmtInsertUserThreshold.clearParameters();
						m = 0;
						++m;
						psmtInsertUserThreshold.setString(m,
								p_senderVO.getUserID());
						++m;
						psmtInsertUserThreshold.setString(m, c2cTransferID);
						++m;
						psmtInsertUserThreshold.setDate(m,
								BTSLUtil.getSQLDateFromUtilDate(date));
						++m;
						psmtInsertUserThreshold.setTimestamp(m,
								BTSLUtil.getTimestampFromUtilDate(date));
						++m;
						psmtInsertUserThreshold.setString(m,
								p_batchMasterVO.getNetworkCode());
						++m;
						psmtInsertUserThreshold.setString(m,
								p_batchMasterVO.getProductCode());
						// psmtInsertUserThreshold.setLong(++m,
						// p_userBalancesVO.getUnitValue())
						++m;
						psmtInsertUserThreshold.setString(m,
								PretupsI.CHANNEL_TYPE_C2C);
						++m;
						psmtInsertUserThreshold.setString(m,
								p_batchMasterVO.getTransferType());
						++m;
						psmtInsertUserThreshold.setString(m, thresholdType);
						++m;
						psmtInsertUserThreshold.setString(m,
								p_senderVO.getCategoryCode());
						++m;
						psmtInsertUserThreshold.setLong(m,
								previousSenderBalToBeSetChnlTrfItems);
						++m;
						psmtInsertUserThreshold.setLong(m, senderBalance);
						++m;
						psmtInsertUserThreshold.setLong(m, thresholdValue);
						// added by nilesh
						++m;
						psmtInsertUserThreshold.setString(m, threshold_type);
						++m;
						psmtInsertUserThreshold.setString(m, remark);

						psmtInsertUserThreshold.executeUpdate();
					}
				} catch (SQLException sqle) {
					LOG.error(methodName, "SQLException " + sqle.getMessage());
					LOG.errorTrace(methodName, sqle);
					EventHandler.handle(EventIDI.SYSTEM_ERROR,
							EventComponentI.SYSTEM, EventStatusI.RAISED,
							EventLevelI.FATAL,
							"C2CBatchTransferWebDAO[closeBatchC2CTransfer]",
							c2cTransferID, "",
							p_batchMasterVO.getNetworkCode(),
							"Error while updating user_threshold_counter table SQL Exception:"
									+ sqle.getMessage());
				}// end of catch

				// if
				// (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferSubType()))
				// {
				pstmtSelectSenderTransferCounts.clearParameters();
				m = 0;
				++m;
				pstmtSelectSenderTransferCounts.setString(m,
						p_senderVO.getUserID());
				try{
				rs = null;
				rs = pstmtSelectSenderTransferCounts.executeQuery();
				// get the Sender transfer counts
				senderCountsVO = null;
				if (rs.next()) {
					senderCountsVO = new UserTransferCountsVO();
					senderCountsVO.setUserID(p_senderVO.getUserID());

					senderCountsVO
							.setDailyInCount(rs.getLong("daily_in_count"));
					senderCountsVO
							.setDailyInValue(rs.getLong("daily_in_value"));
					senderCountsVO.setWeeklyInCount(rs
							.getLong("weekly_in_count"));
					senderCountsVO.setWeeklyInValue(rs
							.getLong("weekly_in_value"));
					senderCountsVO.setMonthlyInCount(rs
							.getLong("monthly_in_count"));
					senderCountsVO.setMonthlyInValue(rs
							.getLong("monthly_in_value"));

					senderCountsVO.setDailyOutCount(rs
							.getLong("daily_out_count"));
					senderCountsVO.setDailyOutValue(rs
							.getLong("daily_out_value"));
					senderCountsVO.setWeeklyOutCount(rs
							.getLong("weekly_out_count"));
					senderCountsVO.setWeeklyOutValue(rs
							.getLong("weekly_out_value"));
					senderCountsVO.setMonthlyOutCount(rs
							.getLong("monthly_out_count"));
					senderCountsVO.setMonthlyOutValue(rs
							.getLong("monthly_out_value"));

					senderCountsVO.setUnctrlDailyInCount(rs
							.getLong("outside_daily_in_count"));
					senderCountsVO.setUnctrlDailyInValue(rs
							.getLong("outside_daily_in_value"));
					senderCountsVO.setUnctrlWeeklyInCount(rs
							.getLong("outside_weekly_in_count"));
					senderCountsVO.setUnctrlWeeklyInValue(rs
							.getLong("outside_weekly_in_value"));
					senderCountsVO.setUnctrlMonthlyInCount(rs
							.getLong("outside_monthly_in_count"));
					senderCountsVO.setUnctrlMonthlyInValue(rs
							.getLong("outside_monthly_in_value"));

					senderCountsVO.setUnctrlDailyOutCount(rs
							.getLong("outside_daily_out_count"));
					senderCountsVO.setUnctrlDailyOutValue(rs
							.getLong("outside_daily_out_value"));
					senderCountsVO.setUnctrlWeeklyOutCount(rs
							.getLong("outside_weekly_out_count"));
					senderCountsVO.setUnctrlWeeklyOutValue(rs
							.getLong("outside_weekly_out_value"));
					senderCountsVO.setUnctrlMonthlyOutCount(rs
							.getLong("outside_monthly_out_count"));
					senderCountsVO.setUnctrlMonthlyOutValue(rs
							.getLong("outside_monthly_out_value"));

					senderCountsVO.setDailySubscriberOutCount(rs
							.getLong("daily_subscriber_out_count"));
					senderCountsVO.setDailySubscriberOutValue(rs
							.getLong("daily_subscriber_out_value"));
					senderCountsVO.setWeeklySubscriberOutCount(rs
							.getLong("weekly_subscriber_out_count"));
					senderCountsVO.setWeeklySubscriberOutValue(rs
							.getLong("weekly_subscriber_out_value"));
					senderCountsVO.setMonthlySubscriberOutCount(rs
							.getLong("monthly_subscriber_out_count"));
					senderCountsVO.setMonthlySubscriberOutValue(rs
							.getLong("monthly_subscriber_out_value"));

					senderCountsVO.setLastTransferDate(rs
							.getDate("last_transfer_date"));
					
				}
				}
				finally{
					if(rs!=null)
						rs.close();
				}
				flag = true;
				
				if (senderCountsVO == null) {
					flag = false;
					senderCountsVO = new UserTransferCountsVO();
				}
				// If found then check for reset otherwise no need to check it
				if (flag) {
					ChannelTransferBL.checkResetCountersAfterPeriodChange(
							senderCountsVO, date);
				}

				pstmtSelectSenderProfileOutCounts.clearParameters();
				m = 0;
				++m;
				pstmtSelectSenderProfileOutCounts.setString(m,
						batchItemsVO.getTxnProfile());
				++m;
				pstmtSelectSenderProfileOutCounts.setString(m, PretupsI.YES);
				++m;
				pstmtSelectSenderProfileOutCounts.setString(m,
						p_batchMasterVO.getNetworkCode());
				++m;
				pstmtSelectSenderProfileOutCounts.setString(m,
						PretupsI.PARENT_PROFILE_ID_CATEGORY);
				++m;
				pstmtSelectSenderProfileOutCounts.setString(m, PretupsI.YES);
				try{
				rs = null;
				rs = pstmtSelectSenderProfileOutCounts.executeQuery();
				if (rs.next()) {
					senderTfrProfileCheckVO = new TransferProfileVO();
					senderTfrProfileCheckVO.setProfileId(rs
							.getString("profile_id"));
					senderTfrProfileCheckVO.setDailyOutCount(rs
							.getLong("daily_transfer_out_count"));
					senderTfrProfileCheckVO.setDailyOutValue(rs
							.getLong("daily_transfer_out_value"));
					senderTfrProfileCheckVO.setWeeklyOutCount(rs
							.getLong("weekly_transfer_out_count"));
					senderTfrProfileCheckVO.setWeeklyOutValue(rs
							.getLong("weekly_transfer_out_value"));
					senderTfrProfileCheckVO.setMonthlyOutCount(rs
							.getLong("monthly_transfer_out_count"));
					senderTfrProfileCheckVO.setMonthlyOutValue(rs
							.getLong("monthly_transfer_out_value"));
					
				}
				// (profile counts not found) if this condition is true then
				// made entry in logs and leave this data.
				else {
					p_con.rollback();
					errorVO = new ListValueVO(
							p_senderVO.getMsisdn(),
							String.valueOf(batchItemsVO.getRecordNumber()),
							p_messages
									.getMessage(p_locale,
											"batchc2c.batchapprovereject.msg.error.transferprofilenotfound"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.detailLog(methodName,
							p_batchMasterVO, batchItemsVO,
							"FAIL : Transfer profile not found",
							"Approval level = " + "No Approval required");
					continue;
				}
				}
				finally{
					if(rs!=null)
						rs.close();
				}
				if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER
						.equals(p_batchMasterVO.getTransferSubType())) {
					// (daily in count reach) if this condition is true then
					// made entry in logs and leave this data.
					if (senderTfrProfileCheckVO.getDailyOutCount() <= senderCountsVO
							.getDailyOutCount()) {
						p_con.rollback();
						errorVO = new ListValueVO(
								p_senderVO.getMsisdn(),
								String.valueOf(batchItemsVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.batchapprovereject.msg.error.dailyincntreach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,
								p_batchMasterVO, batchItemsVO,
								"FAIL : Daily transfer in count reach",
								"Approval level = " + "No Approval required");
						continue;
					}
					// (daily in value reach) if this condition is true then
					// made entry in logs and leave this data.
					// else if(senderTfrProfileCheckVO.getDailyOutValue() <
					// (senderCountsVO.getDailyOutValue() +
					// batchItemsVO.getRequestedQuantity() ) )
					else if (senderTfrProfileCheckVO.getDailyOutValue() < (senderCountsVO
							.getDailyOutValue() + channelTransferItemsVO
							.getProductTotalMRP())) {
						p_con.rollback();
						errorVO = new ListValueVO(
								p_senderVO.getMsisdn(),
								String.valueOf(batchItemsVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.batchapprovereject.msg.error.dailyinvaluereach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,
								p_batchMasterVO, batchItemsVO,
								"FAIL : Daily transfer in value reach",
								"Approval level = " + "No Approval required");
						continue;
					}
					// (weekly in count reach) if this condition is true then
					// made entry in logs and leave this data.
					else if (senderTfrProfileCheckVO.getWeeklyOutCount() <= senderCountsVO
							.getWeeklyOutCount()) {
						p_con.rollback();
						errorVO = new ListValueVO(
								p_senderVO.getMsisdn(),
								String.valueOf(batchItemsVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.batchapprovereject.msg.error.weeklyincntreach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,
								p_batchMasterVO, batchItemsVO,
								"FAIL : Weekly transfer in count reach",
								"Approval level = " + "No Approval required");
						continue;
					}
					// (weekly in value reach) if this condition is true then
					// made entry in logs and leave this data.
					// else if(senderTfrProfileCheckVO.getWeeklyOutValue() < (
					// senderCountsVO.getWeeklyOutValue() +
					// batchItemsVO.getRequestedQuantity() ) )
					else if (senderTfrProfileCheckVO.getWeeklyOutValue() < (senderCountsVO
							.getWeeklyOutValue() + channelTransferItemsVO
							.getProductTotalMRP())) {
						p_con.rollback();
						errorVO = new ListValueVO(
								p_senderVO.getMsisdn(),
								String.valueOf(batchItemsVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.batchapprovereject.msg.error.weeklyinvaluereach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,
								p_batchMasterVO, batchItemsVO,
								"FAIL : Weekly transfer in value reach",
								"Approval level = " + "No Approval required");
						continue;
					}
					// (monthly in count reach) if this condition is true then
					// made entry in logs and leave this data.
					else if (senderTfrProfileCheckVO.getMonthlyOutCount() <= senderCountsVO
							.getMonthlyOutCount()) {
						p_con.rollback();
						errorVO = new ListValueVO(
								p_senderVO.getMsisdn(),
								String.valueOf(batchItemsVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.batchapprovereject.msg.error.monthlyincntreach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,
								p_batchMasterVO, batchItemsVO,
								"FAIL : Monthly transfer in count reach",
								"Approval level = " + "No Approval required");
						continue;
					}
					// (monthly in value reach) if this condition is true then
					// made entry in logs and leave this data.
					// else if(senderTfrProfileCheckVO.getMonthlyOutValue() < (
					// senderCountsVO.getMonthlyOutValue() +
					// batchItemsVO.getRequestedQuantity() ) )
					else if (senderTfrProfileCheckVO.getMonthlyOutValue() < (senderCountsVO
							.getMonthlyOutValue() + channelTransferItemsVO
							.getProductTotalMRP())) {
						p_con.rollback();
						errorVO = new ListValueVO(
								p_senderVO.getMsisdn(),
								String.valueOf(batchItemsVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.batchapprovereject.msg.error.monthlyinvaluereach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,
								p_batchMasterVO, batchItemsVO,
								"FAIL : Monthly transfer in value reach",
								"Approval level = " + "No Approval required");
						continue;
					}
				}
				senderCountsVO.setUserID(p_senderVO.getUserID());
				if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(batchItemsVO
						.getTransferType())
						&& PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW
								.equals(batchItemsVO.getTransferSubType())) {
					// senderCountsVO.setDailyOutCount(senderCountsVO.getDailyOutCount()-1)
					// senderCountsVO.setWeeklyOutCount(senderCountsVO.getWeeklyOutCount()-1)
					// senderCountsVO.setMonthlyOutCount(senderCountsVO.getMonthlyOutCount()-1)
					// senderCountsVO.setDailyOutValue(senderCountsVO.getDailyOutValue()+batchItemsVO.getRequestedQuantity())
					// senderCountsVO.setWeeklyOutValue(senderCountsVO.getWeeklyOutValue()+batchItemsVO.getRequestedQuantity())
					// senderCountsVO.setMonthlyOutValue(senderCountsVO.getMonthlyOutValue()+batchItemsVO.getRequestedQuantity())
					senderCountsVO.setDailyOutValue(senderCountsVO
							.getDailyOutValue()
							- channelTransferItemsVO.getProductTotalMRP());
					senderCountsVO.setWeeklyOutValue(senderCountsVO
							.getWeeklyOutValue()
							- channelTransferItemsVO.getProductTotalMRP());
					senderCountsVO.setMonthlyOutValue(senderCountsVO
							.getMonthlyOutValue()
							- channelTransferItemsVO.getProductTotalMRP());
				} else {
					senderCountsVO.setDailyOutCount(senderCountsVO
							.getDailyOutCount() + 1);
					senderCountsVO.setWeeklyOutCount(senderCountsVO
							.getWeeklyOutCount() + 1);
					senderCountsVO.setMonthlyOutCount(senderCountsVO
							.getMonthlyOutCount() + 1);
					// senderCountsVO.setDailyOutValue(senderCountsVO.getDailyOutValue()+batchItemsVO.getRequestedQuantity())
					// senderCountsVO.setWeeklyOutValue(senderCountsVO.getWeeklyOutValue()+batchItemsVO.getRequestedQuantity())
					// senderCountsVO.setMonthlyOutValue(senderCountsVO.getMonthlyOutValue()+batchItemsVO.getRequestedQuantity())
					senderCountsVO.setDailyOutValue(senderCountsVO
							.getDailyOutValue()
							+ channelTransferItemsVO.getProductTotalMRP());
					senderCountsVO.setWeeklyOutValue(senderCountsVO
							.getWeeklyOutValue()
							+ channelTransferItemsVO.getProductTotalMRP());
					senderCountsVO.setMonthlyOutValue(senderCountsVO
							.getMonthlyOutValue()
							+ channelTransferItemsVO.getProductTotalMRP());
				}
				senderCountsVO.setLastOutTime(date);
				senderCountsVO.setLastTransferID(c2cTransferID);
				senderCountsVO.setLastTransferDate(date);

				// Update counts if found in db

				if (flag) {
					m = 0;
					pstmtUpdateSenderTransferCounts.clearParameters();
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getDailyInCount());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getDailyInValue());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getWeeklyInCount());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getWeeklyInValue());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getMonthlyInCount());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getMonthlyInValue());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getDailyOutCount());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getDailyOutValue());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getWeeklyOutCount());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getWeeklyOutValue());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getMonthlyOutCount());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getMonthlyOutValue());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getUnctrlDailyInCount());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getUnctrlDailyInValue());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getUnctrlWeeklyInCount());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getUnctrlWeeklyInValue());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getUnctrlMonthlyInCount());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getUnctrlMonthlyInValue());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getUnctrlDailyOutCount());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getUnctrlDailyOutValue());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getUnctrlWeeklyOutCount());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getUnctrlWeeklyOutValue());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getUnctrlMonthlyOutCount());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getUnctrlMonthlyOutValue());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getDailySubscriberOutCount());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getDailySubscriberOutValue());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getWeeklySubscriberOutCount());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getWeeklySubscriberOutValue());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getMonthlySubscriberOutCount());
					++m;
					pstmtUpdateSenderTransferCounts.setLong(m,
							senderCountsVO.getMonthlySubscriberOutValue());
					++m;
					pstmtUpdateSenderTransferCounts.setTimestamp(m, BTSLUtil
							.getTimestampFromUtilDate(senderCountsVO
									.getLastOutTime()));
					++m;
					pstmtUpdateSenderTransferCounts.setString(m,
							senderCountsVO.getLastTransferID());
					++m;
					pstmtUpdateSenderTransferCounts.setTimestamp(m, BTSLUtil
							.getTimestampFromUtilDate(senderCountsVO
									.getLastTransferDate()));
					++m;
					pstmtUpdateSenderTransferCounts.setString(m,
							senderCountsVO.getUserID());
					updateCount = pstmtUpdateSenderTransferCounts
							.executeUpdate();
				}
				// Insert counts if not found in db
				else {
					m = 0;
					pstmtInsertSenderTransferCounts.clearParameters();
					++m;
					pstmtInsertSenderTransferCounts.setLong(m,
							senderCountsVO.getDailyOutCount());
					++m;
					pstmtInsertSenderTransferCounts.setLong(m,
							senderCountsVO.getDailyOutValue());
					++m;
					pstmtInsertSenderTransferCounts.setLong(m,
							senderCountsVO.getWeeklyOutCount());
					++m;
					pstmtInsertSenderTransferCounts.setLong(m,
							senderCountsVO.getWeeklyOutValue());
					++m;
					pstmtInsertSenderTransferCounts.setLong(m,
							senderCountsVO.getMonthlyOutCount());
					++m;
					pstmtInsertSenderTransferCounts.setLong(m,
							senderCountsVO.getMonthlyOutValue());
					++m;
					pstmtInsertSenderTransferCounts.setTimestamp(m, BTSLUtil
							.getTimestampFromUtilDate(senderCountsVO
									.getLastOutTime()));
					++m;
					pstmtInsertSenderTransferCounts.setString(m,
							senderCountsVO.getLastTransferID());
					++m;
					pstmtInsertSenderTransferCounts.setTimestamp(m, BTSLUtil
							.getTimestampFromUtilDate(senderCountsVO
									.getLastTransferDate()));
					++m;
					pstmtInsertSenderTransferCounts.setString(m,
							senderCountsVO.getUserID());
					updateCount = pstmtInsertSenderTransferCounts
							.executeUpdate();
				}
				if (updateCount <= 0) {
					p_con.rollback();
					errorVO = new ListValueVO(
							p_senderVO.getMsisdn(),
							String.valueOf(batchItemsVO.getRecordNumber()),
							p_messages
									.getMessage(p_locale,
											"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
					errorList.add(errorVO);
					if (flag) {
						BatchC2CFileProcessLog
								.detailLog(
										methodName,
										p_batchMasterVO,
										batchItemsVO,
										"FAIL : DB error while insert sender trasnfer counts",
										"Approval level = "
												+ "No Approval required");
					} else {
						BatchC2CFileProcessLog
								.detailLog(
										methodName,
										p_batchMasterVO,
										batchItemsVO,
										"FAIL : DB error while uptdate sender trasnfer counts",
										"Approval level = "
												+ "No Approval required");
					}
					continue;
				}
				// }

				pstmtSelectBalance.clearParameters();
				m = 0;
				++m;
				pstmtSelectBalance.setString(m, channelUserVO.getUserID());
				++m;
				pstmtSelectBalance.setString(m,
						p_batchMasterVO.getProductCode());
				++m;
				pstmtSelectBalance.setString(m,
						p_batchMasterVO.getNetworkCode());
				++m;
				pstmtSelectBalance.setString(m,
						p_batchMasterVO.getNetworkCodeFor());
				try{
				rs = null;
				rs = pstmtSelectBalance.executeQuery();
				balance = -1;
				previousUserBalToBeSetChnlTrfItems = -1;
				if (rs.next()) {
					balance = rs.getLong("balance");
					
				}
				}
				finally{
					if(rs!=null)
						rs.close();
				}
				if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN
						.equals(p_batchMasterVO.getTransferType())
						&& PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW
								.equals(p_batchMasterVO.getTransferSubType())) {
					if (balance == 0
							|| (balance - batchItemsVO.getRequestedQuantity() < 0)) {
						p_con.rollback();
						errorVO = new ListValueVO(
								batchItemsVO.getMsisdn(),
								String.valueOf(batchItemsVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.batchc2capprove.closeOrderByBatch.receiverbalnsuff"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog
								.detailLog(
										methodName,
										p_batchMasterVO,
										batchItemsVO,
										"FAIL : DB Error while selecting user balances table for daily balance",
										"Approval level = "
												+ "No Approval required");
						continue;
					} else if (balance != 0
							&& balance - batchItemsVO.getRequestedQuantity() >= 0) {
						previousUserBalToBeSetChnlTrfItems = balance;
						// balance -= batchItemsVO.getRequestedQuantity()
						balance -= channelTransferItemsVO.getRequiredQuantity();
					} else {
						previousUserBalToBeSetChnlTrfItems = 0;
					}
				} else if ((PretupsI.CHANNEL_TRANSFER_TYPE_TRANSFER
						.equals(p_batchMasterVO.getTransferType()))
						&& PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER
								.equals(p_batchMasterVO.getTransferSubType())) {
					previousUserBalToBeSetChnlTrfItems = balance;
					// balance += batchItemsVO.getRequestedQuantity()
					balance += channelTransferItemsVO.getReceiverCreditQty();
				}
				if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER
						.equals(p_batchMasterVO.getTransferSubType())) {
					pstmtLoadTransferProfileProduct.clearParameters();
					m = 0;
					++m;
					pstmtLoadTransferProfileProduct.setString(m,
							batchItemsVO.getTxnProfile());
					++m;
					pstmtLoadTransferProfileProduct.setString(m,
							p_batchMasterVO.getProductCode());
					++m;
					pstmtLoadTransferProfileProduct.setString(m,
							PretupsI.PARENT_PROFILE_ID_CATEGORY);
					++m;
					pstmtLoadTransferProfileProduct.setString(m, PretupsI.YES);
					try{
					rs = null;
					rs = pstmtLoadTransferProfileProduct.executeQuery();
					// get the transfer profile of user
					if (rs.next()) {
						transferProfileProductVO = new TransferProfileProductVO();
						transferProfileProductVO.setProductCode(p_batchMasterVO
								.getProductCode());
						transferProfileProductVO.setMinResidualBalanceAsLong(rs
								.getLong("min_residual_balance"));
						transferProfileProductVO.setMaxBalanceAsLong(rs
								.getLong("max_balance"));
						
					}
					// (transfer profile not found) if this condition is true
					// then made entry in logs and leave this data.
					else {
						p_con.rollback();
						errorVO = new ListValueVO(
								batchItemsVO.getMsisdn(),
								String.valueOf(batchItemsVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.batchapprovereject.msg.error.profcountersnotfound"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog
								.detailLog(
										methodName,
										p_batchMasterVO,
										batchItemsVO,
										"FAIL : User Trf Profile not found for product",
										"Approval level = "
												+ "No Approval required");
						continue;
					}
					}
					finally{
						if(rs!=null)
							rs.close();
					}
					maxBalance = transferProfileProductVO.getMaxBalanceAsLong();
					// (max balance reach for the receiver) if this condition is
					// true then made entry in logs and leave this data.
					if (maxBalance < balance) {
						if (!isNotToExecuteQuery) {
							isNotToExecuteQuery = true;
						}
						p_con.rollback();
						errorVO = new ListValueVO(
								batchItemsVO.getMsisdn(),
								String.valueOf(batchItemsVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.batchapprovereject.msg.error.maxbalancereach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,
								p_batchMasterVO, batchItemsVO,
								"FAIL : User Max balance reached",
								"Approval level = " + "No Approval required");
						continue;
					}
					// check for the very first txn of the user containg the
					// order value larger than maxBalance
					// (max balance reach) if this condition is true then made
					// entry in logs and leave this data.
					else if (balance == -1
							&& maxBalance < batchItemsVO.getRequestedQuantity()) {
						if (!isNotToExecuteQuery) {
							isNotToExecuteQuery = true;
						}
						p_con.rollback();
						errorVO = new ListValueVO(
								batchItemsVO.getMsisdn(),
								String.valueOf(batchItemsVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.batchapprovereject.msg.error.maxbalancereach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,
								p_batchMasterVO, batchItemsVO,
								"FAIL : User Max balance reached",
								"Approval level = " + "No Approval required");
						continue;
					}
				}

				if (!isNotToExecuteQuery) {
					m = 0;
					// update
					if (previousUserBalToBeSetChnlTrfItems > -1) {
						pstmtUpdateBalance.clearParameters();
						handlerStmt = pstmtUpdateBalance;
						++m;
						handlerStmt.setLong(m,
								previousUserBalToBeSetChnlTrfItems);
					} else {
						// insert
						pstmtInsertBalance.clearParameters();
						handlerStmt = pstmtInsertBalance;
						balance = batchItemsVO.getRequestedQuantity();
						++m;
						handlerStmt.setLong(m, 0);// previous balance
						++m;
						handlerStmt.setTimestamp(m,
								BTSLUtil.getTimestampFromUtilDate(date));// updated
						// on
						// date
					}
					++m;
					handlerStmt.setLong(m, balance);
					// handlerStmt.setString(++m,PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION)
					++m;
					handlerStmt.setString(m, batchItemsVO.getTransferType());
					++m;
					handlerStmt.setString(m, c2cTransferID);
					++m;
					handlerStmt.setTimestamp(m,
							BTSLUtil.getTimestampFromUtilDate(date));
					++m;
					handlerStmt.setString(m, channelUserVO.getUserID());
					++m;
					handlerStmt.setString(m, p_batchMasterVO.getProductCode());
					++m;
					handlerStmt.setString(m, p_batchMasterVO.getNetworkCode());
					++m;
					handlerStmt.setString(m,
							p_batchMasterVO.getNetworkCodeFor());
					updateCount = handlerStmt.executeUpdate();
					handlerStmt.clearParameters();
					if (updateCount <= 0) {
						p_con.rollback();
						errorVO = new ListValueVO(
								batchItemsVO.getMsisdn(),
								String.valueOf(batchItemsVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,
								p_batchMasterVO, batchItemsVO,
								"FAIL : DB error while credit uer balance",
								"Approval level = " + "No Approval required");
						continue;
					}

					// for zero balance counter..
					try {

						// thresholdValue=(Long)PreferenceCache.getControlPreference(PreferenceI.ZERO_BAL_THRESHOLD_VALUE,p_batchMasterVO.getNetworkCode(),
						// batchItemsVO.getCategoryCode()) //threshold value
						m = 0;
						boolean isUserThresholdEntryReq = false;
						String thresholdType = null;
						/*
						 * if(previousUserBalToBeSetChnlTrfItems>=thresholdValue
						 * && balance <=thresholdValue) {
						 * isUserThresholdEntryReq=true;
						 * thresholdType=PretupsI.BELOW_THRESHOLD_TYPE } else
						 * if(previousUserBalToBeSetChnlTrfItems<=thresholdValue
						 * && balance >=thresholdValue) {
						 * isUserThresholdEntryReq=true
						 * thresholdType=PretupsI.ABOVE_THRESHOLD_TYPE; }
						 */

						// added by nilesh
						final String remark = null;
						String threshold_type = null;
						if (balance <= transferProfileProductVO
								.getAltBalanceLong()
								&& balance > transferProfileProductVO
										.getMinResidualBalanceAsLong()) {
							// isUserThresholdEntryReq=true
							thresholdValue = transferProfileProductVO
									.getAltBalanceLong();
							threshold_type = PretupsI.THRESHOLD_TYPE_ALERT;
						} else if (balance <= transferProfileProductVO
								.getMinResidualBalanceAsLong()) {
							// isUserThresholdEntryReq=true
							thresholdValue = transferProfileProductVO
									.getMinResidualBalanceAsLong();
							threshold_type = PretupsI.THRESHOLD_TYPE_MIN;
						}
						// new
						if (previousUserBalToBeSetChnlTrfItems >= thresholdValue
								&& balance <= thresholdValue) {
							isUserThresholdEntryReq = true;
							thresholdType = PretupsI.BELOW_THRESHOLD_TYPE;
						} else if (previousUserBalToBeSetChnlTrfItems <= thresholdValue
								&& balance >= thresholdValue) {
							isUserThresholdEntryReq = true;
							thresholdType = PretupsI.ABOVE_THRESHOLD_TYPE;
						} else if (previousUserBalToBeSetChnlTrfItems <= thresholdValue
								&& balance <= thresholdValue) {
							isUserThresholdEntryReq = true;
							thresholdType = PretupsI.BELOW_THRESHOLD_TYPE;
						}
						// end

						if (isUserThresholdEntryReq) {
							if (LOG.isDebugEnabled()) {
								LOG.debug(
										methodName,
										"Entry in threshold counter"
												+ thresholdValue
												+ ", prvbal: "
												+ previousUserBalToBeSetChnlTrfItems
												+ "nbal" + balance);
							}
							psmtInsertUserThreshold.clearParameters();
							m = 0;
							++m;
							psmtInsertUserThreshold.setString(m,
									channelUserVO.getUserID());
							++m;
							psmtInsertUserThreshold.setString(m, c2cTransferID);
							++m;
							psmtInsertUserThreshold.setDate(m,
									BTSLUtil.getSQLDateFromUtilDate(date));
							++m;
							psmtInsertUserThreshold.setTimestamp(m,
									BTSLUtil.getTimestampFromUtilDate(date));
							++m;
							psmtInsertUserThreshold.setString(m,
									p_batchMasterVO.getNetworkCode());
							++m;
							psmtInsertUserThreshold.setString(m,
									p_batchMasterVO.getProductCode());
							// psmtInsertUserThreshold.setLong(++m,
							// p_userBalancesVO.getUnitValue());
							++m;
							psmtInsertUserThreshold.setString(m,
									PretupsI.CHANNEL_TYPE_C2C);
							++m;
							psmtInsertUserThreshold.setString(m,
									p_batchMasterVO.getTransferType());
							++m;
							psmtInsertUserThreshold.setString(m, thresholdType);
							++m;
							psmtInsertUserThreshold.setString(m,
									batchItemsVO.getCategoryCode());
							++m;
							psmtInsertUserThreshold.setLong(m,
									previousUserBalToBeSetChnlTrfItems);
							++m;
							psmtInsertUserThreshold.setLong(m, balance);
							++m;
							psmtInsertUserThreshold.setLong(m, thresholdValue);
							// added by nilesh
							++m;
							psmtInsertUserThreshold
									.setString(m, threshold_type);
							++m;
							psmtInsertUserThreshold.setString(m, remark);

							psmtInsertUserThreshold.executeUpdate();
						}
					} catch (SQLException sqle) {
						LOG.error(methodName,
								"SQLException " + sqle.getMessage());
						LOG.errorTrace(methodName, sqle);
						EventHandler
								.handle(EventIDI.SYSTEM_ERROR,
										EventComponentI.SYSTEM,
										EventStatusI.RAISED,
										EventLevelI.FATAL,
										"C2CBatchTransferWebDAO[closeBatchC2CTransfer]",
										c2cTransferID, "",
										p_batchMasterVO.getNetworkCode(),
										"Error while updating user_threshold_counter table SQL Exception:"
												+ sqle.getMessage());
					}// end of catch
				}

				// if
				// (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_batchMasterVO.getTransferSubType())){
				pstmtSelectTransferCounts.clearParameters();
				m = 0;
				++m;
				pstmtSelectTransferCounts.setString(m,
						channelUserVO.getUserID());
				try{
				rs = null;
				rs = pstmtSelectTransferCounts.executeQuery();
				// get the user transfer counts
				countsVO = null;
				if (rs.next()) {
					countsVO = new UserTransferCountsVO();
					countsVO.setUserID(batchItemsVO.getUserId());

					countsVO.setDailyInCount(rs.getLong("daily_in_count"));
					countsVO.setDailyInValue(rs.getLong("daily_in_value"));
					countsVO.setWeeklyInCount(rs.getLong("weekly_in_count"));
					countsVO.setWeeklyInValue(rs.getLong("weekly_in_value"));
					countsVO.setMonthlyInCount(rs.getLong("monthly_in_count"));
					countsVO.setMonthlyInValue(rs.getLong("monthly_in_value"));

					countsVO.setDailyOutCount(rs.getLong("daily_out_count"));
					countsVO.setDailyOutValue(rs.getLong("daily_out_value"));
					countsVO.setWeeklyOutCount(rs.getLong("weekly_out_count"));
					countsVO.setWeeklyOutValue(rs.getLong("weekly_out_value"));
					countsVO.setMonthlyOutCount(rs.getLong("monthly_out_count"));
					countsVO.setMonthlyOutValue(rs.getLong("monthly_out_value"));

					countsVO.setUnctrlDailyInCount(rs
							.getLong("outside_daily_in_count"));
					countsVO.setUnctrlDailyInValue(rs
							.getLong("outside_daily_in_value"));
					countsVO.setUnctrlWeeklyInCount(rs
							.getLong("outside_weekly_in_count"));
					countsVO.setUnctrlWeeklyInValue(rs
							.getLong("outside_weekly_in_value"));
					countsVO.setUnctrlMonthlyInCount(rs
							.getLong("outside_monthly_in_count"));
					countsVO.setUnctrlMonthlyInValue(rs
							.getLong("outside_monthly_in_value"));

					countsVO.setUnctrlDailyOutCount(rs
							.getLong("outside_daily_out_count"));
					countsVO.setUnctrlDailyOutValue(rs
							.getLong("outside_daily_out_value"));
					countsVO.setUnctrlWeeklyOutCount(rs
							.getLong("outside_weekly_out_count"));
					countsVO.setUnctrlWeeklyOutValue(rs
							.getLong("outside_weekly_out_value"));
					countsVO.setUnctrlMonthlyOutCount(rs
							.getLong("outside_monthly_out_count"));
					countsVO.setUnctrlMonthlyOutValue(rs
							.getLong("outside_monthly_out_value"));

					countsVO.setDailySubscriberOutCount(rs
							.getLong("daily_subscriber_out_count"));
					countsVO.setDailySubscriberOutValue(rs
							.getLong("daily_subscriber_out_value"));
					countsVO.setWeeklySubscriberOutCount(rs
							.getLong("weekly_subscriber_out_count"));
					countsVO.setWeeklySubscriberOutValue(rs
							.getLong("weekly_subscriber_out_value"));
					countsVO.setMonthlySubscriberOutCount(rs
							.getLong("monthly_subscriber_out_count"));
					countsVO.setMonthlySubscriberOutValue(rs
							.getLong("monthly_subscriber_out_value"));

					countsVO.setLastTransferDate(rs
							.getDate("last_transfer_date"));
					
				}
				}
				finally{
					if(rs!=null)
						rs.close();
				}
				flag = true;
				if (countsVO == null) {
					flag = false;
					countsVO = new UserTransferCountsVO();
				}
				// If found then check for reset otherwise no need to check it
				if (flag) {
					ChannelTransferBL.checkResetCountersAfterPeriodChange(
							countsVO, date);
				}

				pstmtSelectProfileCounts.clearParameters();
				m = 0;
				++m;
				pstmtSelectProfileCounts.setString(m,
						batchItemsVO.getTxnProfile());
				++m;
				pstmtSelectProfileCounts.setString(m, PretupsI.YES);
				++m;
				pstmtSelectProfileCounts.setString(m,
						p_batchMasterVO.getNetworkCode());
				++m;
				pstmtSelectProfileCounts.setString(m,
						PretupsI.PARENT_PROFILE_ID_CATEGORY);
				++m;
				pstmtSelectProfileCounts.setString(m, PretupsI.YES);
				try{
				rs = null;
				rs = pstmtSelectProfileCounts.executeQuery();
				// get the transfer profile counts
				if (rs.next()) {
					transferProfileVO = new TransferProfileVO();
					transferProfileVO.setProfileId(rs.getString("profile_id"));
					transferProfileVO.setDailyInCount(rs
							.getLong("daily_transfer_in_count"));
					transferProfileVO.setDailyInValue(rs
							.getLong("daily_transfer_in_value"));
					transferProfileVO.setWeeklyInCount(rs
							.getLong("weekly_transfer_in_count"));
					transferProfileVO.setWeeklyInValue(rs
							.getLong("weekly_transfer_in_value"));
					transferProfileVO.setMonthlyInCount(rs
							.getLong("monthly_transfer_in_count"));
					transferProfileVO.setMonthlyInValue(rs
							.getLong("monthly_transfer_in_value"));
					
				}
				// (profile counts not found) if this condition is true then
				// made entry in logs and leave this data.
				else {
					p_con.rollback();
					errorVO = new ListValueVO(
							batchItemsVO.getMsisdn(),
							String.valueOf(batchItemsVO.getRecordNumber()),
							p_messages
									.getMessage(p_locale,
											"batchc2c.batchapprovereject.msg.error.transferprofilenotfound"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.detailLog(methodName,
							p_batchMasterVO, batchItemsVO,
							"FAIL : Transfer profile not found",
							"Approval level = " + "No Approval required");
					continue;
				}
				}
				finally{
					if(rs!=null)
						rs.close();
				}
				if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER
						.equals(p_batchMasterVO.getTransferSubType())) {
					// (daily in count reach) if this condition is true then
					// made entry in logs and leave this data.
					if (transferProfileVO.getDailyInCount() <= countsVO
							.getDailyInCount()) {
						p_con.rollback();
						errorVO = new ListValueVO(
								batchItemsVO.getMsisdn(),
								String.valueOf(batchItemsVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.batchapprovereject.msg.error.dailyincntreach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,
								p_batchMasterVO, batchItemsVO,
								"FAIL : Daily transfer in count reach",
								"Approval level = " + "No Approval required");
						continue;
					}
					// (daily in value reach) if this condition is true then
					// made entry in logs and leave this data.
					// else if(transferProfileVO.getDailyInValue() <
					// (countsVO.getDailyInValue() +
					// batchItemsVO.getRequestedQuantity() ) )
					else if (transferProfileVO.getDailyInValue() < (countsVO
							.getDailyInValue() + channelTransferItemsVO
							.getProductTotalMRP())) {
						p_con.rollback();
						errorVO = new ListValueVO(
								batchItemsVO.getMsisdn(),
								String.valueOf(batchItemsVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.batchapprovereject.msg.error.dailyinvaluereach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,
								p_batchMasterVO, batchItemsVO,
								"FAIL : Daily transfer in value reach",
								"Approval level = " + "No Approval required");
						continue;
					}
					// (weekly in count reach) if this condition is true then
					// made entry in logs and leave this data.
					else if (transferProfileVO.getWeeklyInCount() <= countsVO
							.getWeeklyInCount()) {
						p_con.rollback();
						errorVO = new ListValueVO(
								batchItemsVO.getMsisdn(),
								String.valueOf(batchItemsVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.batchapprovereject.msg.error.weeklyincntreach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,
								p_batchMasterVO, batchItemsVO,
								"FAIL : Weekly transfer in count reach",
								"Approval level = " + "No Approval required");
						continue;
					}
					// (weekly in value reach) if this condition is true then
					// made entry in logs and leave this data.
					// else if(transferProfileVO.getWeeklyInValue() < (
					// countsVO.getWeeklyInValue() +
					// batchItemsVO.getRequestedQuantity() ) )
					else if (transferProfileVO.getWeeklyInValue() < (countsVO
							.getWeeklyInValue() + channelTransferItemsVO
							.getProductTotalMRP())) {
						p_con.rollback();
						errorVO = new ListValueVO(
								batchItemsVO.getMsisdn(),
								String.valueOf(batchItemsVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.batchapprovereject.msg.error.weeklyinvaluereach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,
								p_batchMasterVO, batchItemsVO,
								"FAIL : Weekly transfer in value reach",
								"Approval level = " + "No Approval required");
						continue;
					}
					// (monthly in count reach) if this condition is true then
					// made entry in logs and leave this data.
					else if (transferProfileVO.getMonthlyInCount() <= countsVO
							.getMonthlyInCount()) {
						p_con.rollback();
						errorVO = new ListValueVO(
								batchItemsVO.getMsisdn(),
								String.valueOf(batchItemsVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.batchapprovereject.msg.error.monthlyincntreach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,
								p_batchMasterVO, batchItemsVO,
								"FAIL : Monthly transfer in count reach",
								"Approval level = " + "No Approval required");
						continue;
					}
					// (mobthly in value reach) if this condition is true then
					// made entry in logs and leave this data.
					// else if(transferProfileVO.getMonthlyInValue() < (
					// countsVO.getMonthlyInValue() +
					// batchItemsVO.getRequestedQuantity() ) )
					else if (transferProfileVO.getMonthlyInValue() < (countsVO
							.getMonthlyInValue() + channelTransferItemsVO
							.getProductTotalMRP())) {
						p_con.rollback();
						errorVO = new ListValueVO(
								batchItemsVO.getMsisdn(),
								String.valueOf(batchItemsVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.batchapprovereject.msg.error.monthlyinvaluereach"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,
								p_batchMasterVO, batchItemsVO,
								"FAIL : Monthly transfer in value reach",
								"Approval level = " + "No Approval required");
						continue;
					}
				}
				countsVO.setUserID(channelUserVO.getUserID());
				if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(batchItemsVO
						.getTransferType())
						&& PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW
								.equals(batchItemsVO.getTransferSubType())) {
					// countsVO.setDailyInCount(countsVO.getDailyInCount()-1)
					// countsVO.setWeeklyInCount(countsVO.getWeeklyInCount()-1)
					// countsVO.setMonthlyInCount(countsVO.getMonthlyInCount()-1)
					// countsVO.setDailyInValue(countsVO.getDailyInValue()+batchItemsVO.getRequestedQuantity())
					// countsVO.setWeeklyInValue(countsVO.getWeeklyInValue()+batchItemsVO.getRequestedQuantity())
					// countsVO.setMonthlyInValue(countsVO.getMonthlyInValue()+batchItemsVO.getRequestedQuantity())
					countsVO.setDailyInValue(countsVO.getDailyInValue()
							- channelTransferItemsVO.getProductTotalMRP());
					countsVO.setWeeklyInValue(countsVO.getWeeklyInValue()
							- channelTransferItemsVO.getProductTotalMRP());
					countsVO.setMonthlyInValue(countsVO.getMonthlyInValue()
							- channelTransferItemsVO.getProductTotalMRP());
				} else {
					countsVO.setDailyInCount(countsVO.getDailyInCount() + 1);
					countsVO.setWeeklyInCount(countsVO.getWeeklyInCount() + 1);
					countsVO.setMonthlyInCount(countsVO.getMonthlyInCount() + 1);
					// countsVO.setDailyInValue(countsVO.getDailyInValue()+batchItemsVO.getRequestedQuantity())
					// countsVO.setWeeklyInValue(countsVO.getWeeklyInValue()+batchItemsVO.getRequestedQuantity())
					// countsVO.setMonthlyInValue(countsVO.getMonthlyInValue()+batchItemsVO.getRequestedQuantity())
					countsVO.setDailyInValue(countsVO.getDailyInValue()
							+ channelTransferItemsVO.getProductTotalMRP());
					countsVO.setWeeklyInValue(countsVO.getWeeklyInValue()
							+ channelTransferItemsVO.getProductTotalMRP());
					countsVO.setMonthlyInValue(countsVO.getMonthlyInValue()
							+ channelTransferItemsVO.getProductTotalMRP());
				}
				countsVO.setLastInTime(date);
				countsVO.setLastTransferID(c2cTransferID);
				countsVO.setLastTransferDate(date);

				// Update counts if found in db

				if (flag) {
					m = 0;
					pstmtUpdateTransferCounts.clearParameters();
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getDailyInCount());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getDailyInValue());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getWeeklyInCount());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getWeeklyInValue());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getMonthlyInCount());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getMonthlyInValue());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getDailyOutCount());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getDailyOutValue());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getWeeklyOutCount());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getWeeklyOutValue());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getMonthlyOutCount());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getMonthlyOutValue());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getUnctrlDailyInCount());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getUnctrlDailyInValue());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getUnctrlWeeklyInCount());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getUnctrlWeeklyInValue());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getUnctrlMonthlyInCount());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getUnctrlMonthlyInValue());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getUnctrlDailyOutCount());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getUnctrlDailyOutValue());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getUnctrlWeeklyOutCount());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getUnctrlWeeklyOutValue());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getUnctrlMonthlyOutCount());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getUnctrlMonthlyOutValue());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getDailySubscriberOutCount());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getDailySubscriberOutValue());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getWeeklySubscriberOutCount());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getWeeklySubscriberOutValue());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getMonthlySubscriberOutCount());
					++m;
					pstmtUpdateTransferCounts.setLong(m,
							countsVO.getMonthlySubscriberOutValue());
					++m;
					pstmtUpdateTransferCounts
							.setTimestamp(m, BTSLUtil
									.getTimestampFromUtilDate(countsVO
											.getLastInTime()));
					++m;
					pstmtUpdateTransferCounts.setString(m,
							countsVO.getLastTransferID());
					++m;
					pstmtUpdateTransferCounts.setTimestamp(m, BTSLUtil
							.getTimestampFromUtilDate(countsVO
									.getLastTransferDate()));
					++m;
					pstmtUpdateTransferCounts
							.setString(m, countsVO.getUserID());
					updateCount = pstmtUpdateTransferCounts.executeUpdate();
				}
				// Insert counts if not found in db
				else {
					m = 0;
					pstmtInsertTransferCounts.clearParameters();
					++m;
					pstmtInsertTransferCounts.setLong(m,
							countsVO.getDailyInCount());
					++m;
					pstmtInsertTransferCounts.setLong(m,
							countsVO.getDailyInValue());
					++m;
					pstmtInsertTransferCounts.setLong(m,
							countsVO.getWeeklyInCount());
					++m;
					pstmtInsertTransferCounts.setLong(m,
							countsVO.getWeeklyInValue());
					++m;
					pstmtInsertTransferCounts.setLong(m,
							countsVO.getMonthlyInCount());
					++m;
					pstmtInsertTransferCounts.setLong(m,
							countsVO.getMonthlyInValue());
					++m;
					pstmtInsertTransferCounts
							.setTimestamp(m, BTSLUtil
									.getTimestampFromUtilDate(countsVO
											.getLastInTime()));
					++m;
					pstmtInsertTransferCounts.setString(m,
							countsVO.getLastTransferID());
					++m;
					pstmtInsertTransferCounts.setTimestamp(m, BTSLUtil
							.getTimestampFromUtilDate(countsVO
									.getLastTransferDate()));
					++m;
					pstmtInsertTransferCounts
							.setString(m, countsVO.getUserID());
					updateCount = pstmtInsertTransferCounts.executeUpdate();
				}
				// (record not updated properly) if this condition is true then
				// made entry in logs and leave this data.
				if (updateCount <= 0) {
					p_con.rollback();
					errorVO = new ListValueVO(
							batchItemsVO.getMsisdn(),
							String.valueOf(batchItemsVO.getRecordNumber()),
							p_messages
									.getMessage(p_locale,
											"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
					errorList.add(errorVO);
					if (flag) {
						BatchC2CFileProcessLog
								.detailLog(
										methodName,
										p_batchMasterVO,
										batchItemsVO,
										"FAIL : DB error while insert user trasnfer counts",
										"Approval level = "
												+ "No Approval required");
					} else {
						BatchC2CFileProcessLog
								.detailLog(
										methodName,
										p_batchMasterVO,
										batchItemsVO,
										"FAIL : DB error while uptdate user trasnfer counts",
										"Approval level = "
												+ "No Approval required");
					}
					continue;
				}
				// }

				// channelTransferVO=new ChannelTransferVO()
				channelTransferVO.setCanceledOn(batchItemsVO.getCancelledOn());
				channelTransferVO.setCanceledBy(batchItemsVO.getCancelledBy());
				channelTransferVO.setChannelRemarks(batchItemsVO
						.getInitiatorRemarks());
				channelTransferVO.setCommProfileSetId(batchItemsVO
						.getCommissionProfileSetId());
				channelTransferVO.setCommProfileVersion(batchItemsVO
						.getCommissionProfileVer());
				channelTransferVO.setCreatedBy(p_batchMasterVO.getCreatedBy());
				channelTransferVO.setCreatedOn(p_batchMasterVO.getCreatedOn());
				channelTransferVO
						.setDomainCode(p_batchMasterVO.getDomainCode());
				channelTransferVO.setFinalApprovedBy(batchItemsVO
						.getApprovedBy());
				channelTransferVO.setFirstApprovedOn(batchItemsVO
						.getApprovedOn());
				channelTransferVO.setFirstApproverLimit(0);
				channelTransferVO.setFirstApprovalRemark(batchItemsVO
						.getApproverRemarks());
				channelTransferVO.setSecondApprovalLimit(0);
				// channelTransferVO.setCategoryCode(p_senderVO.getCategoryCode())
				channelTransferVO.setBatchNum(batchItemsVO.getBatchId());
				channelTransferVO.setBatchDate(p_batchMasterVO.getBatchDate());
				// channelTransferVO.setFromUserID(p_senderVO.getCategoryCode())
				// channelTransferVO.setTotalTax3(0)
				// channelTransferVO.setPayableAmount(0)
				// channelTransferVO.setNetPayableAmount(0)
				channelTransferVO.setPayableAmount(channelTransferItemsVO
						.getPayableAmount());
				channelTransferVO.setNetPayableAmount(channelTransferItemsVO
						.getNetPayableAmount());
				channelTransferVO.setPayInstrumentAmt(0);
				// channelTransferVO.setModifiedBy(p_senderVO.getUserID())
				channelTransferVO
						.setModifiedBy(p_batchMasterVO.getModifiedBy());
				channelTransferVO.setModifiedOn(date);
				channelTransferVO.setProductType(p_batchMasterVO
						.getProductType());
				channelTransferVO.setReceiverCategoryCode(batchItemsVO
						.getCategoryCode());
				channelTransferVO.setReceiverGradeCode(batchItemsVO
						.getGradeCode());
				channelTransferVO.setReceiverTxnProfile(batchItemsVO
						.getTxnProfile());
				channelTransferVO.setReferenceNum(batchItemsVO
						.getBatchDetailId());
				channelTransferVO.setDefaultLang(p_sms_default_lang);
				channelTransferVO.setSecondLang(p_sms_second_lang);
				// for balance logger
				channelTransferVO.setReferenceID(network_id);
				// ends here
				if (messageGatewayVO != null
						&& messageGatewayVO.getRequestGatewayVO() != null) {
					channelTransferVO.setRequestGatewayCode(messageGatewayVO
							.getRequestGatewayVO().getGatewayCode());
					channelTransferVO.setRequestGatewayType(messageGatewayVO
							.getGatewayType());
				}
				channelTransferVO.setRequestedQuantity(batchItemsVO
						.getRequestedQuantity());
				channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);
				channelTransferVO.setStatus(batchItemsVO.getStatus());
				// channelTransferVO.setToUserID(channelUserVO.getUserID())
				// channelTransferVO.setTotalTax1(batchItemsVO.getTax1Value())
				// channelTransferVO.setTotalTax2(batchItemsVO.getTax2Value())
				channelTransferVO.setTotalTax1(channelTransferItemsVO
						.getTax1Value());
				channelTransferVO.setTotalTax2(channelTransferItemsVO
						.getTax2Value());
				channelTransferVO.setTotalTax3(channelTransferItemsVO
						.getTax3Value());
				channelTransferVO
						.setTransferCategory(PretupsI.TRANSFER_CATEGORY_TRANSFER);
				channelTransferVO.setTransferDate(p_batchMasterVO
						.getCreatedOn());
				// channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER)
				channelTransferVO.setTransferSubType(batchItemsVO
						.getTransferSubType());
				channelTransferVO.setTransferID(c2cTransferID);
				channelTransferVO.setTransferInitatedBy(p_batchMasterVO
						.getUserId());
				// channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION)
				channelTransferVO.setTransferType(batchItemsVO
						.getTransferType());
				channelTransferVO.setType(PretupsI.CHANNEL_TYPE_C2C);
				// channelTransferVO.setTransferMRP(batchItemsVO.getTransferMrp())
				channelTransferVO.setTransferMRP(channelTransferItemsVO
						.getProductTotalMRP());
				// modified by vikram.

				if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(batchItemsVO
						.getTransferType())
						&& PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW
								.equals(batchItemsVO.getTransferSubType())) {
					channelTransferVO.setToUserID(p_batchMasterVO.getUserId());
					channelTransferVO.setFromUserID(channelUserVO.getUserID());
					channelTransferVO.setFromUserCode(batchItemsVO.getMsisdn());
					channelTransferVO.setToUserCode(p_senderVO.getMsisdn());
					channelTransferVO.setSenderGradeCode(batchItemsVO
							.getUserGradeCode());
					channelTransferVO.setCategoryCode(batchItemsVO
							.getCategoryCode());
					channelTransferVO.setSenderTxnProfile(batchItemsVO
							.getTxnProfile());
					channelTransferVO.setReceiverCategoryCode(p_senderVO
							.getCategoryCode());
					channelTransferVO.setReceiverGradeCode(p_senderVO
							.getUserGrade());
					channelTransferVO.setReceiverTxnProfile(p_senderVO
							.getTransferProfileID());
					channelTransferVO.setGraphicalDomainCode(channelUserVO
							.getGeographicalCode());
					channelTransferVO
							.setReceiverGgraphicalDomainCode(p_senderVO
									.getGeographicalCode());
					// channelTransferItemVO.setSenderPreviousStock(previousUserBalToBeSetChnlTrfItems)
					// channelTransferItemVO.setAfterTransSenderPreviousStock(previousUserBalToBeSetChnlTrfItems)
					// channelTransferItemVO.setReceiverPreviousStock(senderPreviousBal)
					// channelTransferItemVO.setAfterTransReceiverPreviousStock(senderPreviousBal)
				} else { // FOR the transfer/return
					channelTransferVO.setToUserID(channelUserVO.getUserID());
					channelTransferVO
							.setFromUserID(p_batchMasterVO.getUserId());
					channelTransferVO.setFromUserCode(p_senderVO.getMsisdn());
					channelTransferVO.setToUserCode(batchItemsVO.getMsisdn());
					channelTransferVO.setSenderGradeCode(p_senderVO
							.getUserGrade());
					channelTransferVO.setCategoryCode(p_senderVO
							.getCategoryCode());
					channelTransferVO.setSenderTxnProfile(p_senderVO
							.getTransferProfileID());
					channelTransferVO.setReceiverCategoryCode(batchItemsVO
							.getCategoryCode());
					channelTransferVO.setReceiverGradeCode(batchItemsVO
							.getUserGradeCode());
					channelTransferVO.setReceiverTxnProfile(batchItemsVO
							.getTxnProfile());
					channelTransferVO.setGraphicalDomainCode(p_senderVO
							.getGeographicalCode());
					channelTransferVO
							.setReceiverGgraphicalDomainCode(channelUserVO
									.getGeographicalCode());
					// channelTransferItemVO.setSenderPreviousStock(senderPreviousBal)
					// channelTransferItemVO.setAfterTransSenderPreviousStock(senderPreviousBal)
					// channelTransferItemVO.setReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems)
					// channelTransferItemVO.setAfterTransReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems)
				}

				// channelTransferItemVO =new ChannelTransferItemsVO()
				// channelTransferItemsVO.setApprovedQuantity(batchItemsVO.getRequestedQuantity())
				// channelTransferItemVO.setCommProfileDetailID(batchItemsVO.getCommissionProfileDetailId())
				// channelTransferItemVO.setCommRate(batchItemsVO.getCommissionRate())
				// channelTransferItemVO.setCommType(batchItemsVO.getCommissionType())
				// channelTransferItemVO.setCommValue(batchItemsVO.getCommissionValue())
				// channelTransferItemVO.setNetPayableAmount(0)
				// channelTransferItemVO.setPayableAmount(0)
				// channelTransferItemVO.setProductTotalMRP(batchItemsVO.getTransferMrp())
				channelTransferItemsVO.setProductCode(p_batchMasterVO
						.getProductCode());
				channelTransferItemsVO
						.setReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);

				channelTransferItemsVO.setRequiredQuantity(batchItemsVO
						.getRequestedQuantity());
				channelTransferItemsVO.setSerialNum(1);
				// channelTransferItemVO.setTax1Rate(batchItemsVO.getTax1Rate())
				// channelTransferItemVO.setTax1Type(batchItemsVO.getTax1Type())
				// channelTransferItemVO.setTax1Value(batchItemsVO.getTax1Value())
				// channelTransferItemVO.setTax2Rate(batchItemsVO.getTax2Rate())
				// channelTransferItemVO.setTax2Type(batchItemsVO.getTax2Type())
				// channelTransferItemVO.setTax2Value(batchItemsVO.getTax2Value())
				// channelTransferItemVO.setTax3Rate(batchItemsVO.getTax3Rate())
				// channelTransferItemVO.setTax3Type(batchItemsVO.getTax3Type())
				// channelTransferItemVO.setTax3Value(batchItemsVO.getTax3Value())
				channelTransferItemsVO.setTransferID(c2cTransferID);
				// channelTransferItemsVO.setUnitValue(p_batchMasterVO.getProductMrp())
				// for the balance logger
				channelTransferItemsVO
						.setAfterTransReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);

				// ends here
				channelTransferItemVOList = new ArrayList();
				channelTransferItemVOList.add(channelTransferItemsVO);
				channelTransferItemsVO.setShortName(p_batchMasterVO
						.getProductShortName());
				// channelTransferVO.setChannelTransferitemsVOList(channelTransferItemVOList)
				if (LOG.isDebugEnabled()) {
					LOG.debug(methodName, "Exiting: channelTransferVO="
							+ channelTransferVO.toString());
				}
				if (LOG.isDebugEnabled()) {
					LOG.debug(methodName, "Exiting: channelTransferItemsVO="
							+ channelTransferItemsVO.toString());
				}

				// for positive commission deduct from network stock

				final boolean debit = true;
				if (PretupsI.COMM_TYPE_POSITIVE.equals(channelTransferVO.getDualCommissionType())
						&& PretupsI.CHANNEL_TRANSFER_TYPE_TRANSFER
								.equals(channelTransferVO.getTransferType())) {
					ChannelTransferBL
							.prepareNetworkStockListAndCreditDebitStockForCommision(
									p_con, channelTransferVO,
									channelTransferVO.getFromUserID(), date,
									debit);
					ChannelTransferBL
							.updateNetworkStockTransactionDetailsForCommision(
									p_con, channelTransferVO,
									channelTransferVO.getFromUserID(), date);
				}

				// added by vikram
				if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(batchItemsVO
						.getTransferType())
						&& PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW
								.equals(batchItemsVO.getTransferSubType())) {
					channelTransferItemsVO
							.setSenderPreviousStock(previousUserBalToBeSetChnlTrfItems);
					channelTransferItemsVO
							.setAfterTransSenderPreviousStock(previousUserBalToBeSetChnlTrfItems);
					channelTransferItemsVO
							.setReceiverPreviousStock(senderPreviousBal);
					channelTransferItemsVO
							.setAfterTransReceiverPreviousStock(senderPreviousBal);
				} else { // FOR the transfer/return
					channelTransferItemsVO
							.setSenderPreviousStock(senderPreviousBal);
					channelTransferItemsVO
							.setAfterTransSenderPreviousStock(senderPreviousBal);
					channelTransferItemsVO
							.setReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
					channelTransferItemsVO
							.setAfterTransReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
				}

				m = 0;
				pstmtInsertIntoChannelTranfers.clearParameters();
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getCanceledBy());
				++m;
				pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil
						.getTimestampFromUtilDate(channelTransferVO
								.getCanceledOn()));
				// pstmtInsertIntoChannelTranfers.setFormOfUse(++m,
				// OraclePreparedStatement.FORM_NCHAR);//commented for DB2
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getChannelRemarks());
				++m;
				pstmtInsertIntoChannelTranfers.setTimestamp(m,
						BTSLUtil.getTimestampFromUtilDate(date));
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getCommProfileSetId());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getCommProfileVersion());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getCreatedBy());
				++m;
				pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil
						.getTimestampFromUtilDate(channelTransferVO
								.getCreatedOn()));
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getDomainCode());
				++m;
				pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil
						.getTimestampFromUtilDate(channelTransferVO
								.getExternalTxnDate()));
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getExternalTxnNum());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getFinalApprovedBy());
				++m;
				pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil
						.getTimestampFromUtilDate(channelTransferVO
								.getFirstApprovedOn()));
				++m;
				pstmtInsertIntoChannelTranfers.setLong(m,
						channelTransferVO.getFirstApproverLimit());
				// pstmtInsertIntoChannelTranfers.setFormOfUse(++m,
				// OraclePreparedStatement.FORM_NCHAR);//commented for DB2
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getFirstApprovalRemark());
				++m;
				pstmtInsertIntoChannelTranfers.setDate(m, BTSLUtil
						.getSQLDateFromUtilDate(channelTransferVO
								.getBatchDate()));
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getBatchNum());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getFromUserID());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getGraphicalDomainCode());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getModifiedBy());
				++m;
				pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil
						.getTimestampFromUtilDate(channelTransferVO
								.getModifiedOn()));
				++m;
				pstmtInsertIntoChannelTranfers.setLong(m,
						channelTransferVO.getNetPayableAmount());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getNetworkCode());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getNetworkCodeFor());
				++m;
				pstmtInsertIntoChannelTranfers.setLong(m,
						channelTransferVO.getPayableAmount());
				++m;
				pstmtInsertIntoChannelTranfers.setLong(m,
						channelTransferVO.getPayInstrumentAmt());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getProductType());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getReceiverCategoryCode());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getReceiverGradeCode());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getReceiverTxnProfile());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getReferenceNum());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getRequestGatewayCode());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getRequestGatewayType());
				++m;
				pstmtInsertIntoChannelTranfers.setLong(m,
						channelTransferVO.getRequestedQuantity());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getSecondApprovedBy());
				++m;
				pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil
						.getTimestampFromUtilDate(channelTransferVO
								.getSecondApprovedOn()));
				++m;
				pstmtInsertIntoChannelTranfers.setLong(m,
						channelTransferVO.getSecondApprovalLimit());
				// pstmtInsertIntoChannelTranfers.setFormOfUse(++m,
				// OraclePreparedStatement.FORM_NCHAR);//commented for DB2
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getSecondApprovalRemark());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getSource());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						batchItemsVO.getStatus());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getThirdApprovedBy());
				++m;
				pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil
						.getTimestampFromUtilDate(channelTransferVO
								.getThirdApprovedOn()));
				// pstmtInsertIntoChannelTranfers.setFormOfUse(++m,
				// OraclePreparedStatement.FORM_NCHAR);//commented for DB2
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getThirdApprovalRemark());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getToUserID());
				++m;
				pstmtInsertIntoChannelTranfers.setLong(m,
						channelTransferVO.getTotalTax1());
				++m;
				pstmtInsertIntoChannelTranfers.setLong(m,
						channelTransferVO.getTotalTax2());
				++m;
				pstmtInsertIntoChannelTranfers.setLong(m,
						channelTransferVO.getTotalTax3());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getTransferCategory());
				++m;
				pstmtInsertIntoChannelTranfers.setDate(m, BTSLUtil
						.getSQLDateFromUtilDate(channelTransferVO
								.getTransferDate()));
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getTransferID());
				// pstmtInsertIntoChannelTranfers.setString(++m,p_senderVO.getUserID())
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getTransferInitatedBy());
				++m;
				pstmtInsertIntoChannelTranfers.setLong(m,
						channelTransferVO.getTransferMRP());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getTransferSubType());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getTransferType());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getType());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getCategoryCode());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m, PretupsI.YES);
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getToUserCode());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getDomainCode());

				// to geographical domain also inserted as the geogrpahical
				// domain that will help in reports
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getGraphicalDomainCode());

				// pstmtInsertIntoChannelTranfers.setFormOfUse(++m,
				// OraclePreparedStatement.FORM_NCHAR);//commented for DB2
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getDefaultLang());
				// pstmtInsertIntoChannelTranfers.setFormOfUse(++m,
				// OraclePreparedStatement.FORM_NCHAR);//commented for DB2
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getSecondLang());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						p_batchMasterVO.getCreatedBy());
				// added by vikram
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getSenderGradeCode());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getSenderTxnProfile());
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						channelTransferVO.getFromUserCode());
				
				++m;
				pstmtInsertIntoChannelTranfers.setString(m,
						batchItemsVO.getDualCommissionType());
				++m;
				//Added for inserting the other commision profile set ID
            	pstmtInsertIntoChannelTranfers.setString(m,((ChannelTransferItemsVO)channelTransferVO.getChannelTransferitemsVOList().get(0)).getOthCommSetId());
				// ends here
				// insert into channel transfer table
				updateCount = pstmtInsertIntoChannelTranfers.executeUpdate();
				updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
				// (record not updated properly) if this condition is true then
				// made entry in logs and leave this data.
				if (updateCount <= 0) {
					p_con.rollback();
					errorVO = new ListValueVO(
							batchItemsVO.getMsisdn(),
							String.valueOf(batchItemsVO.getRecordNumber()),
							p_messages
									.getMessage(p_locale,
											"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog
							.detailLog(
									methodName,
									p_batchMasterVO,
									batchItemsVO,
									"FAIL : DB Error while inserting in channel transfer table",
									"Approval level = "
											+ "No Approval required"
											+ ", updateCount=" + updateCount);
					continue;
				}

				m = 0;
				pstmtInsertIntoChannelTransferItems.clearParameters();
				++m;
				pstmtInsertIntoChannelTransferItems.setLong(m,
						channelTransferItemsVO.getApprovedQuantity());
				++m;
				pstmtInsertIntoChannelTransferItems.setString(m,
						channelTransferItemsVO.getCommProfileDetailID());
				++m;
				pstmtInsertIntoChannelTransferItems.setDouble(m,
						channelTransferItemsVO.getCommRate());
				++m;
				pstmtInsertIntoChannelTransferItems.setString(m,
						channelTransferItemsVO.getCommType());
				++m;
				pstmtInsertIntoChannelTransferItems.setLong(m,
						channelTransferItemsVO.getCommValue());
				++m;
				pstmtInsertIntoChannelTransferItems.setLong(m,
						channelTransferItemsVO.getProductTotalMRP());
				++m;
				pstmtInsertIntoChannelTransferItems.setLong(m,
						channelTransferItemsVO.getNetPayableAmount());
				++m;
				pstmtInsertIntoChannelTransferItems.setLong(m,
						channelTransferItemsVO.getPayableAmount());
				++m;
				pstmtInsertIntoChannelTransferItems.setString(m,
						channelTransferItemsVO.getProductCode());
				++m;
				pstmtInsertIntoChannelTransferItems.setLong(m,
						channelTransferItemsVO.getReceiverPreviousStock());
				++m;
				pstmtInsertIntoChannelTransferItems.setLong(m,
						channelTransferItemsVO.getRequiredQuantity());
				++m;
				pstmtInsertIntoChannelTransferItems.setInt(m,
						channelTransferItemsVO.getSerialNum());
				++m;
				pstmtInsertIntoChannelTransferItems.setLong(m,
						channelTransferItemsVO.getSenderPreviousStock());
				// pstmtInsertIntoChannelTransferItems.setLong(++m,senderPreviousBal)
				++m;
				pstmtInsertIntoChannelTransferItems.setDouble(m,
						channelTransferItemsVO.getTax1Rate());
				++m;
				pstmtInsertIntoChannelTransferItems.setString(m,
						channelTransferItemsVO.getTax1Type());
				++m;
				pstmtInsertIntoChannelTransferItems.setLong(m,
						channelTransferItemsVO.getTax1Value());
				++m;
				pstmtInsertIntoChannelTransferItems.setDouble(m,
						channelTransferItemsVO.getTax2Rate());
				++m;
				pstmtInsertIntoChannelTransferItems.setString(m,
						channelTransferItemsVO.getTax2Type());
				++m;
				pstmtInsertIntoChannelTransferItems.setLong(m,
						channelTransferItemsVO.getTax2Value());
				++m;
				pstmtInsertIntoChannelTransferItems.setDouble(m,
						channelTransferItemsVO.getTax3Rate());
				++m;
				pstmtInsertIntoChannelTransferItems.setString(m,
						channelTransferItemsVO.getTax3Type());
				++m;
				pstmtInsertIntoChannelTransferItems.setLong(m,
						channelTransferItemsVO.getTax3Value());
				++m;
				pstmtInsertIntoChannelTransferItems.setTimestamp(m,
						BTSLUtil.getTimestampFromUtilDate(date));
				++m;
				pstmtInsertIntoChannelTransferItems.setString(m,
						channelTransferItemsVO.getTransferID());
				++m;
				pstmtInsertIntoChannelTransferItems.setLong(m,
						channelTransferItemsVO.getUnitValue());
				++m;
				pstmtInsertIntoChannelTransferItems.setLong(m,
						channelTransferItemsVO.getSenderDebitQty());
				++m;
				pstmtInsertIntoChannelTransferItems.setLong(m,
						channelTransferItemsVO.getReceiverCreditQty());
				// added by vikram
				if (PretupsI.CHANNEL_TRANSFER_TYPE_RETURN.equals(batchItemsVO
						.getTransferType())
						&& PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW
								.equals(batchItemsVO.getTransferSubType())) {
					++m;
					pstmtInsertIntoChannelTransferItems.setLong(
							m,
							channelTransferItemsVO.getSenderPreviousStock()
									- channelTransferItemsVO
											.getSenderDebitQty());
					++m;
					pstmtInsertIntoChannelTransferItems.setLong(
							m,
							channelTransferItemsVO.getReceiverPreviousStock()
									+ channelTransferItemsVO
											.getReceiverCreditQty());
				} else { // FOR the transfer/return
					++m;
					pstmtInsertIntoChannelTransferItems.setLong(
							m,
							senderPreviousBal
									- channelTransferItemsVO
											.getSenderDebitQty());
					++m;
					pstmtInsertIntoChannelTransferItems.setLong(
							m,
							channelTransferItemsVO.getReceiverPreviousStock()
									+ channelTransferItemsVO
											.getReceiverCreditQty());
				}
				++m;
				pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemsVO.getCommQuantity());
				++m;
	    		pstmtInsertIntoChannelTransferItems.setString(m, channelTransferItemsVO.getOthCommType());
	    		++m;
	    		pstmtInsertIntoChannelTransferItems.setDouble(m, channelTransferItemsVO.getOthCommRate());
	    		++m;
	    		pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemsVO.getOthCommValue());            	
				// insert into channel transfer items table
				updateCount = pstmtInsertIntoChannelTransferItems
						.executeUpdate();
				updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
				// (record not updated properly) if this condition is true then
				// made entry in logs and leave this data.
				if (updateCount <= 0) {
					p_con.rollback();
					errorVO = new ListValueVO(
							batchItemsVO.getMsisdn(),
							String.valueOf(batchItemsVO.getRecordNumber()),
							p_messages
									.getMessage(p_locale,
											"batchc2c.batchapprovereject.msg.error.recordnotupdated"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog
							.detailLog(
									methodName,
									p_batchMasterVO,
									batchItemsVO,
									"FAIL : DB Error while inserting in channel transfer items table",
									"Approval level = "
											+ "No Approval required"
											+ ", updateCount=" + updateCount);
					continue;
				}
				// commit the transaction after processing each record
				// insert items data here
				index = 0;
				++index;
				pstmtInsertBatchItems.setString(index,
						batchItemsVO.getBatchId());
				++index;
				pstmtInsertBatchItems.setString(index,
						batchItemsVO.getBatchDetailId());
				++index;
				pstmtInsertBatchItems.setString(index,
						batchItemsVO.getCategoryCode());
				++index;
				pstmtInsertBatchItems
						.setString(index, batchItemsVO.getMsisdn());
				++index;
				pstmtInsertBatchItems
						.setString(index, batchItemsVO.getUserId());
				++index;
				pstmtInsertBatchItems
						.setString(index, batchItemsVO.getStatus());
				++index;
				pstmtInsertBatchItems.setString(index,
						batchItemsVO.getModifiedBy());
				++index;
				pstmtInsertBatchItems
						.setTimestamp(index, BTSLUtil
								.getTimestampFromUtilDate(batchItemsVO
										.getModifiedOn()));
				++index;
				pstmtInsertBatchItems.setString(index,
						batchItemsVO.getUserGradeCode());
				++index;
				pstmtInsertBatchItems
						.setDate(index, BTSLUtil
								.getSQLDateFromUtilDate(batchItemsVO
										.getTransferDate()));
				++index;
				pstmtInsertBatchItems.setString(index,
						batchItemsVO.getTxnProfile());
				++index;
				pstmtInsertBatchItems.setString(index,
						batchItemsVO.getCommissionProfileSetId());
				++index;
				pstmtInsertBatchItems.setString(index,
						batchItemsVO.getCommissionProfileVer());
				++index;
				pstmtInsertBatchItems.setString(index,
						channelTransferItemsVO.getCommProfileDetailID());
				++index;
				pstmtInsertBatchItems.setString(index,
						channelTransferItemsVO.getCommType());
				++index;
				pstmtInsertBatchItems.setDouble(index,
						channelTransferItemsVO.getCommRate());
				++index;
				pstmtInsertBatchItems.setLong(index,
						channelTransferItemsVO.getCommValue());
				++index;
				pstmtInsertBatchItems.setString(index,
						channelTransferItemsVO.getTax1Type());
				++index;
				pstmtInsertBatchItems.setDouble(index,
						channelTransferItemsVO.getTax1Rate());
				++index;
				pstmtInsertBatchItems.setLong(index,
						channelTransferItemsVO.getTax1Value());
				++index;
				pstmtInsertBatchItems.setString(index,
						channelTransferItemsVO.getTax2Type());
				++index;
				pstmtInsertBatchItems.setDouble(index,
						channelTransferItemsVO.getTax2Rate());
				++index;
				pstmtInsertBatchItems.setLong(index,
						channelTransferItemsVO.getTax2Value());
				++index;
				pstmtInsertBatchItems.setString(index,
						channelTransferItemsVO.getTax3Type());
				++index;
				pstmtInsertBatchItems.setDouble(index,
						channelTransferItemsVO.getTax3Rate());
				++index;
				pstmtInsertBatchItems.setLong(index,
						channelTransferItemsVO.getTax3Value());
				++index;
				pstmtInsertBatchItems.setString(index, String
						.valueOf(channelTransferItemsVO.getRequiredQuantity()));
				++index;
				pstmtInsertBatchItems.setLong(index,
						channelTransferItemsVO.getProductTotalMRP());
				// pstmtInsertBatchItems.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);//commented
				// for DB2
				++index;
				pstmtInsertBatchItems.setString(index,
						batchItemsVO.getInitiatorRemarks());
				++index;
				pstmtInsertBatchItems.setString(index,
						batchItemsVO.getExternalCode());
				++index;
				pstmtInsertBatchItems
						.setString(
								index,
								PretupsI.CHANNEL_TRANSFER_BATCH_C2C_ITEM_RCRDSTATUS_PROCESSED);
				++index;
				pstmtInsertBatchItems.setString(index,
						batchItemsVO.getTransferType());
				++index;
				pstmtInsertBatchItems.setString(index,
						batchItemsVO.getTransferSubType());
				++index;
				pstmtInsertBatchItems.setString(index,
						channelTransferItemsVO.getProductCode());
				queryExecutionCount = pstmtInsertBatchItems.executeUpdate();
				if (queryExecutionCount <= 0) {
					p_con.rollback();
					// put error record can not be inserted
					LOG.error(methodName,
							"Record cannot be inserted in batch items table");
					BatchC2CFileProcessLog
							.detailLog(
									methodName,
									p_batchMasterVO,
									batchItemsVO,
									"FAIL : DB Error Record cannot be inserted in batch items table",
									"queryExecutionCount="
											+ queryExecutionCount);
				} else {
					//p_con.commit();
					/*BatchC2CFileProcessLog
							.detailLog(
									methodName,
									p_batchMasterVO,
									batchItemsVO,
									"PASS : Record inserted successfully in batch items table",
									"queryExecutionCount="
											+ queryExecutionCount);*/

					if (batchItemsVO.getStatus().equals(
							PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_CLOSE)) {
						boolean changeStatusRequired = false;
						String str[] = null;
						String newStatus[] = null;
						int updatecount2 = 0;
						int updatecount1 = 0;
						if (!PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO
								.getStatus())) {
							// int
							// updatecount1=operatorUtili.changeUserStatusToActive(
							// p_con,channelTransferVO.getToUserID(),channelUserVO.getStatus())

							str = txnReceiverUserStatusChang.split(","); // "CH:Y,EX:Y".split(",")

							for (int l = 0; l < str.length; l++) {
								newStatus = str[l].split(":");
								if (newStatus[0].equals(channelUserVO
										.getStatus())) {
									changeStatusRequired = true;
									updatecount1 = operatorUtili
											.changeUserStatusToActive(p_con,
													channelTransferVO
															.getToUserID(),
													channelUserVO.getStatus(),
													newStatus[1]);
									break;
								}
							}
						}
						/* if(updatecount1>0){ */
						if (!PretupsI.USER_STATUS_ACTIVE.equals(p_senderVO
								.getStatus())) {
							// int
							// updatecount2=operatorUtili.changeUserStatusToActive(
							// p_con,p_batchMasterVO.getUserId(),p_senderVO.getStatus())

							str = txnSenderUserStatusChang.split(","); // "CH:Y,EX:Y".split(",")
							for (int l = 0; l < str.length; l++) {
								newStatus = str[l].split(":");
								if (newStatus[0].equals(p_senderVO.getStatus())) {
									changeStatusRequired = true;
									updatecount2 = operatorUtili
											.changeUserStatusToActive(
													p_con,
													p_batchMasterVO.getUserId(),
													p_senderVO.getStatus(),
													newStatus[1]);
									break;
								}
							}

						}
						if (changeStatusRequired) {
							if (updatecount2 > 0 || updatecount1 > 0) {
								
								BatchC2CFileProcessLog.detailLog(methodName,
										p_batchMasterVO, batchItemsVO,
										"PASS : Order is closed successfully",
										"updateCount=" + updateCount);
							} else {
								p_con.rollback();
								
								errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"error.status.updating"));
	        					errorList.add(errorVO);
	        					
								
							}
						}
					}
					/*
					 * else{ p_con.commit();
					 * BatchC2CFileProcessLog.detailLog(methodName
					 * ,p_batchMasterVO
					 * ,batchItemsVO,"PASS : Order is closed successfully",
					 * " updateCount="+updateCount) }
					 */
					// }
					/*
					 * else{ p_con.rollback() throw new
					 * BTSLBaseException(this,methodName,"error.status.updating"
					 * ) }
					 */
					/*
					 * else{
					 * 
					 * if(!PretupsI.USER_STATUS_ACTIVE.equals(p_senderVO.getStatus
					 * ())){ //int
					 * updatecount2=operatorUtili.changeUserStatusToActive(
					 * p_con,p_batchMasterVO.getUserId(),p_senderVO.getStatus(),
					 * newStatus[1]) int updatecount2=0 String
					 * str[]=(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_SENDER_USER_STATUS_CHANG)
					 * .split(","); //"CH:Y,EX:Y".split(",") String newStatus[]
					 * = null; for(int l=0;l<str.length;l++){
					 * newStatus=str[l].split(":")
					 * if(newStatus[0].equals(p_senderVO.getStatus())){
					 * updatecount2=operatorUtili.changeUserStatusToActive(
					 * p_con,p_batchMasterVO.getUserId(),p_senderVO.getStatus(),
					 * newStatus[1]) break } } if(updatecount2>0){
					 * p_con.commit()
					 * BatchC2CFileProcessLog.detailLog(methodName
					 * ,p_batchMasterVO
					 * ,batchItemsVO,"PASS : Order is closed successfully",
					 * " updateCount="+updateCount) }else{ p_con.rollback()
					 * throw new
					 * BTSLBaseException(this,methodName,"error.status.updating"
					 * ) } }else{ p_con.commit();
					 * BatchC2CFileProcessLog.detailLog
					 * (methodName,p_batchMasterVO
					 * ,batchItemsVO,"PASS : Order is closed successfully",
					 * " updateCount="+updateCount) } }
					 */

					// p_con.commit()
					totalSuccessRecords++;
					// put success in the logger file.
					// BatchC2CFileProcessLog.detailLog(methodName,p_batchMasterVO,batchItemsVO,"PASS : Record inserted successfully in batch items table","queryExecutionCount="+queryExecutionCount)
				}
				// ends here
				BatchC2CFileProcessLog.detailLog(methodName, p_batchMasterVO,
						batchItemsVO, "PASS : Order is closed successfully",
						"Approval level = " + "No Approval required"
								+ ", updateCount=" + updateCount);
				// made entry in network stock and balance logger
				ChannelTransferBL
						.prepareUserBalancesListForLogger(channelTransferVO);
				pstmtSelectBalanceInfoForMessage.clearParameters();
				m = 0;
				++m;
				pstmtSelectBalanceInfoForMessage.setString(m,
						channelUserVO.getUserID());
				++m;
				pstmtSelectBalanceInfoForMessage.setString(m,
						p_batchMasterVO.getNetworkCode());
				++m;
				pstmtSelectBalanceInfoForMessage.setString(m,
						p_batchMasterVO.getNetworkCodeFor());
				try{
				rs = null;
				rs = pstmtSelectBalanceInfoForMessage.executeQuery();
				userbalanceList = new ArrayList();
				while (rs.next()) {
					balancesVO = new UserBalancesVO();
					balancesVO.setProductCode(rs.getString("product_code"));
					balancesVO.setBalance(rs.getLong("balance"));
					balancesVO.setProductShortCode(rs
							.getString("product_short_code"));
					balancesVO.setProductShortName(rs.getString("short_name"));
					userbalanceList.add(balancesVO);
				}
				}
				finally{
					if(rs!=null)
						rs.close();
				}
				// generate the message arguments to be send in SMS
				keyArgumentVO = new KeyArgumentVO();
				argsArr = new String[2];
				argsArr[1] = PretupsBL.getDisplayAmount(channelTransferItemsVO
						.getRequiredQuantity());
				argsArr[0] = String.valueOf(channelTransferItemsVO
						.getShortName());
				keyArgumentVO
						.setKey(PretupsErrorCodesI.C2C_CHNL_CHNL_TRANSFER_SMS2);
				keyArgumentVO.setArguments(argsArr);
				txnSmsMessageList = new ArrayList();
				balSmsMessageList = new ArrayList();
				txnSmsMessageList.add(keyArgumentVO);
				for (int index1 = 0, n = userbalanceList.size(); index1 < n; index1++) {
					balancesVO = (UserBalancesVO) userbalanceList.get(index1);
					if (balancesVO.getProductCode().equals(
							channelTransferItemsVO.getProductCode())) {
						argsArr = new String[2];
						argsArr[1] = balancesVO.getBalanceAsString();
						argsArr[0] = balancesVO.getProductShortName();
						keyArgumentVO = new KeyArgumentVO();
						keyArgumentVO
								.setKey(PretupsErrorCodesI.C2C_CHNL_CHNL_TRANSFER_SMS_BALSUBKEY);
						keyArgumentVO.setArguments(argsArr);
						balSmsMessageList.add(keyArgumentVO);
						break;
					}
				}
				locale = new Locale(language, country);
				String c2cNotifyMsg = null;
				if (isC2CSmsNotify) {
					final LocaleMasterVO localeVO = LocaleMasterCache
							.getLocaleDetailsFromlocale(locale);
					if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
						c2cNotifyMsg = channelTransferVO.getDefaultLang();
					} else {
						c2cNotifyMsg = channelTransferVO.getSecondLang();
					}
					array = new String[] { channelTransferVO.getTransferID(),
							BTSLUtil.getMessage(locale, txnSmsMessageList),
							BTSLUtil.getMessage(locale, balSmsMessageList),
							c2cNotifyMsg };
				}

				if (c2cNotifyMsg == null) {
					array = new String[] { channelTransferVO.getTransferID(),
							BTSLUtil.getMessage(locale, txnSmsMessageList),
							BTSLUtil.getMessage(locale, balSmsMessageList) };
				}

				messages = new BTSLMessages(
						PretupsErrorCodesI.C2C_CHNL_CHNL_TRANSFER_SMS1, array);
				pushMessage = new PushMessage(batchItemsVO.getMsisdn(),
						messages, channelTransferVO.getTransferID(), null,
						locale, channelTransferVO.getNetworkCode());
				 if(!BTSLUtil.isNullString(partialProceesAllowed) && partialProceesAllowed.equalsIgnoreCase(PretupsI.NO))
						pushMessList.add(pushMessage);
				
				// push SMS
				//pushMessage.push();

				}
				//try end for loop
				catch (BTSLBaseException be)
				{
					isExpectionOccuerred=true;	
				LOG.error(methodName, "BTSLBaseException : " + be);
				LOG.errorTrace(methodName,be);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2CBatchTransferWebDAO[initiateBatchC2CTransfer]","","","","SQL Exception:"+be.getMessage());
				errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"error.sql.record.processing"));
				errorList.add(errorVO);
				
				BatchC2CFileProcessLog.c2cBatchMasterLog(methodName,p_batchMasterVO,"FAIL : SQL Exception:"+be.getMessage(),"TOTAL SUCCESS RECORDS = "+totalSuccessRecords);
				if(!BTSLUtil.isNullString(partialProceesAllowed) && partialProceesAllowed.equalsIgnoreCase(PretupsI.NO))
					p_con.rollback();
					
				}	
				
				catch (SQLException sqe)
				{
					isExpectionOccuerred=true;	
					LOG.error(methodName, "SQLException : " + sqe);
					LOG.errorTrace(methodName,sqe);
					errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"error.sql.record.processing"));
					errorList.add(errorVO);
					
					EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2CBatchTransferWebDAO[initiateBatchC2CTransfer]","","","","SQL Exception:"+sqe.getMessage());
					BatchC2CFileProcessLog.c2cBatchMasterLog(methodName,p_batchMasterVO,"FAIL : SQL Exception:"+sqe.getMessage(),"TOTAL SUCCESS RECORDS = "+totalSuccessRecords);
					if(!BTSLUtil.isNullString(partialProceesAllowed) && partialProceesAllowed.equalsIgnoreCase(PretupsI.NO))
						p_con.rollback();
					
				}
				catch (Exception ex)
				{
					isExpectionOccuerred=true;	
				    LOG.error(methodName, "Exception : " + ex);
				    LOG.errorTrace(methodName,ex);
					EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2CBatchTransferWebDAO[initiateBatchC2CTransfer]","","","","Exception:"+ex.getMessage());
					BatchC2CFileProcessLog.c2cBatchMasterLog(methodName,p_batchMasterVO,"FAIL : Exception:"+ex.getMessage(),"TOTAL SUCCESS RECORDS = "+totalSuccessRecords);
					
					errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"error.sql.record.processing"));
					errorList.add(errorVO);
					
					if(!BTSLUtil.isNullString(partialProceesAllowed) && partialProceesAllowed.equalsIgnoreCase(PretupsI.NO))
						p_con.rollback();
					
				}
				if(!BTSLUtil.isNullString(partialProceesAllowed) && partialProceesAllowed.equalsIgnoreCase(PretupsI.YES) && !isExpectionOccuerred)
				{
					p_con.commit();
					pushMessage.push();
				}
				
				
			}// for loop for the batch items
			
			if(!BTSLUtil.isNullString(partialProceesAllowed) && partialProceesAllowed.equalsIgnoreCase(PretupsI.NO))
			{
				if(!errorList.isEmpty() || _errorExistInfile)
					p_con.rollback();
				else
				{
					p_con.commit();
					
					for(int p=0; p< pushMessList.size();p++)
					{
						PushMessage pushMessage1= pushMessList.get(p);
						pushMessage1.push();
					}
				
				}
			}
			
		} catch (BTSLBaseException be) {
			throw be;
		} catch (SQLException sqe) {
			try {
				if (p_con != null) {
					p_con.rollback();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			LOG.error(methodName, "SQLException : " + sqe);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2CBatchTransferWebDAO[closeBatchC2CTransfer]", "", "",
					"", "SQL Exception:" + sqe.getMessage());
			BatchC2CFileProcessLog.c2cBatchMasterLog(methodName,
					p_batchMasterVO,
					"FAIL : SQL Exception:" + sqe.getMessage(),
					"TOTAL SUCCESS RECORDS = " + totalSuccessRecords);
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		} catch (Exception ex) {
			try {
				if (p_con != null) {
					p_con.rollback();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			LOG.error(methodName, "Exception : " + ex);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2CBatchTransferWebDAO[closeBatchC2CTransfer]", "", "",
					"", "Exception:" + ex.getMessage());
			BatchC2CFileProcessLog.c2cBatchMasterLog(methodName,
					p_batchMasterVO, "FAIL : Exception:" + ex.getMessage(),
					"TOTAL SUCCESS RECORDS = " + totalSuccessRecords);
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		} finally {
			
			try{
				if(pstmtUpdateSenderTransferCounts!=null)
					pstmtUpdateSenderTransferCounts.close();
			}
			catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
		
			try {
				if (pstmtSelectSenderBalance != null) {
					pstmtSelectSenderBalance.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtInsertSenderDailyBalances != null) {
					pstmtInsertSenderDailyBalances.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtUpdateSenderBalanceOn != null) {
					pstmtUpdateSenderBalanceOn.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtUpdateSenderBalance != null) {
					pstmtUpdateSenderBalance.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtSelectSenderTransferCounts != null) {
					pstmtSelectSenderTransferCounts.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtUpdateTransferCounts != null) {
					pstmtUpdateTransferCounts.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtSelectSenderProfileOutCounts != null) {
					pstmtSelectSenderProfileOutCounts.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtInsertSenderTransferCounts != null) {
					pstmtInsertSenderTransferCounts.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (rsSelectTrfRule != null) {
					rsSelectTrfRule.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtSelectTrfRule != null) {
					pstmtSelectTrfRule.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (rsSelectTrfRuleProd != null) {
					rsSelectTrfRuleProd.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtSelectTrfRuleProd != null) {
					pstmtSelectTrfRuleProd.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (rsSelectCProfileProd != null) {
					rsSelectCProfileProd.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtSelectCProfileProd != null) {
					pstmtSelectCProfileProd.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (rsSelectCProfileProdDetail != null) {
					rsSelectCProfileProdDetail.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtSelectCProfileProdDetail != null) {
					pstmtSelectCProfileProdDetail.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (rsSelectTProfileProd != null) {
					rsSelectTProfileProd.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtSelectTProfileProd != null) {
					pstmtSelectTProfileProd.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtInsertBatchMaster != null) {
					pstmtInsertBatchMaster.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtInsertBatchItems != null) {
					pstmtInsertBatchItems.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtLoadUser != null) {
					pstmtLoadUser.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtSelectUserBalances != null) {
					pstmtSelectUserBalances.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtUpdateUserBalances != null) {
					pstmtUpdateUserBalances.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtInsertUserDailyBalances != null) {
					pstmtInsertUserDailyBalances.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtSelectBalance != null) {
					pstmtSelectBalance.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtUpdateBalance != null) {
					pstmtUpdateBalance.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtInsertBalance != null) {
					pstmtInsertBalance.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtSelectTransferCounts != null) {
					pstmtSelectTransferCounts.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtSelectProfileCounts != null) {
					pstmtSelectProfileCounts.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtUpdateTransferCounts != null) {
					pstmtUpdateTransferCounts.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtInsertTransferCounts != null) {
					pstmtInsertTransferCounts.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtLoadTransferProfileProduct != null) {
					pstmtLoadTransferProfileProduct.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (handlerStmt != null) {
					handlerStmt.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtInsertIntoChannelTransferItems != null) {
					pstmtInsertIntoChannelTransferItems.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtInsertIntoChannelTranfers != null) {
					pstmtInsertIntoChannelTranfers.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtSelectBalanceInfoForMessage!= null) {
					pstmtSelectBalanceInfoForMessage.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (psmtInsertUserThreshold != null) {
					psmtInsertUserThreshold.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}

			try {
				// if all records contains errors then rollback the master table
				// entry
				if (errorList != null
						&& (errorList.size() == p_batchItemsList.size())) {
					if (!BTSLUtil.isNullString(partialProceesAllowed) && partialProceesAllowed.equalsIgnoreCase(PretupsI.YES))
					{
						int index=0;
						int queryExecutionCount=-1;
						pstmtUpdateBatchMaster.setInt(++index,p_batchMasterVO.getBatchTotalRecord()-errorList.size());
						pstmtUpdateBatchMaster.setString(++index,PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_CANCEL);
						pstmtUpdateBatchMaster.setString(++index,p_batchMasterVO.getBatchId());
						queryExecutionCount=pstmtUpdateBatchMaster.executeUpdate();
					    if(queryExecutionCount<=0) //Means No Records Updated
			   		    {
			   		        LOG.error(methodName,"Unable to Update the batch size in master table..");
			   		        p_con.rollback();
							EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2CBatchTransferWebDAO[closeBatchC2CTransfer]","","","","Error while updating C2C_BATCHES table. Batch id="+p_batchMasterVO.getBatchId());
			   		    }
			   		    else
			   		    {
			   		        p_con.commit();
			   		    }
						
						
					}
					else
					p_con.rollback();
					LOG.error(methodName,
							"ALL the records conatins errors and cannot be inserted in db");
					BatchC2CFileProcessLog
							.c2cBatchMasterLog(
									methodName,
									p_batchMasterVO,
									"FAIL : ALL the records conatins errors and cannot be inserted in DB ",
									"");
				}
				// else update the master table with the open status and total
				// number of records.
				else {
					int index = 0;
					int queryExecutionCount = -1;
					++index;
					pstmtUpdateBatchMaster.setInt(
							index,
							p_batchMasterVO.getBatchTotalRecord()
									- errorList.size());
					++index;
					pstmtUpdateBatchMaster.setString(index,
							PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_CLOSE);
					++index;
					pstmtUpdateBatchMaster.setString(index,
							p_batchMasterVO.getBatchId());
					queryExecutionCount = pstmtUpdateBatchMaster
							.executeUpdate();
					if (queryExecutionCount <= 0) // Means No Records Updated
					{
						LOG.error(methodName,
								"Unable to Update the batch size in master table..");
						p_con.rollback();
						EventHandler
								.handle(EventIDI.SYSTEM_ERROR,
										EventComponentI.SYSTEM,
										EventStatusI.RAISED,
										EventLevelI.FATAL,
										"C2CBatchTransferWebDAO[closeBatchC2CTransfer]",
										"", "", "",
										"Error while updating C2C_BATCHES table. Batch id="
												+ p_batchMasterVO.getBatchId());
					} else {
						p_con.commit();
					}
				}

			} catch (Exception e) {
				try {
					if (p_con != null) {
						p_con.rollback();
					}
				} catch (Exception ex) {
					LOG.errorTrace(methodName, ex);
				}
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtUpdateBatchMaster != null) {
					pstmtUpdateBatchMaster.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			// OneLineTXNLog.log(channelTransferVO)
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName,
						"Exiting: errorList.size()=" + errorList.size());
			}
		}
		return errorList;
	}

	/**
	 * Method initiateBatchC2CTransfer This method used for the batch c2c order
	 * initiation. The main purpose of this method is to insert the records in
	 * c2c_batches,foc_batch_geographies & c2c_batch_items table.
	 * 
	 * @param p_con
	 *            Connection
	 * @param p_batchMasterVO
	 *            c2cBatchMasterVO
	 * @param p_batchItemsList
	 *            ArrayList
	 * @param p_messages
	 *            MessageResources
	 * @param p_locale
	 *            Locale
	 * @return errorList ArrayList
	 * @throws BTSLBaseException
	 */

	public ArrayList initiateBatchC2CTransfer(Connection p_con,
			C2CBatchMasterVO p_batchMasterVO, ArrayList p_batchItemsList,
			MessageResources p_messages, Locale p_locale, String fromCatgory,String _partialProceesAllowed,Boolean _errorExistInfile)
			throws BTSLBaseException {
		final String methodName = "initiateBatchC2CTransfer";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered.... p_batchMasterVO="
					+ p_batchMasterVO + ", p_batchItemsList.size() = "
					+ p_batchItemsList.size() + ", p_batchItemsList="
					+ p_batchItemsList + "p_locale=" + p_locale +"_errorExistInfile"+_errorExistInfile);
		}
		Boolean isTransationTypeAlwd = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD);
		final ArrayList errorList = new ArrayList();
		ListValueVO errorVO = null;
		//partial
		String partialProceesAllowed = _partialProceesAllowed;

		// for loading the C2C transfer rule for C2C transfer
		PreparedStatement pstmtSelectTrfRule = null;
		ResultSet rsSelectTrfRule = null;
		final StringBuffer strBuffSelectTrfRule = new StringBuffer(
				" SELECT transfer_rule_id,transfer_type, transfer_allowed ");
		strBuffSelectTrfRule
				.append("FROM chnl_transfer_rules WHERE network_code = ? AND domain_code = ?  ");
		strBuffSelectTrfRule
				.append("AND from_category = ? AND to_category = ? AND status = 'Y' AND type = 'CHANNEL' ");
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "strBuffSelectTrfRule Query ="
					+ strBuffSelectTrfRule);
			// ends here
		}

		// for loading the products associated with the transfer rule
		PreparedStatement pstmtSelectTrfRuleProd = null;
		ResultSet rsSelectTrfRuleProd = null;
		final StringBuffer strBuffSelectTrfRuleProd = new StringBuffer(
				"SELECT 1 FROM chnl_transfer_rules_products ");
		strBuffSelectTrfRuleProd
				.append("WHERE transfer_rule_id=?  AND product_code = ? ");
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "strBuffSelectTrfRuleProd Query ="
					+ strBuffSelectTrfRuleProd);
			// ends here
		}

		// for loading the products associated with the commission profile
		PreparedStatement pstmtSelectCProfileProd = null;
		ResultSet rsSelectCProfileProd = null;
		final StringBuffer strBuffSelectCProfileProd = new StringBuffer("SELECT cp.min_transfer_value,cp.max_transfer_value,cp.discount_type,cp.discount_rate, ");
		strBuffSelectCProfileProd.append("cp.comm_profile_products_id, cp.transfer_multiple_off, cp.taxes_on_foc_applicable, cp.taxes_on_channel_transfer ");
		strBuffSelectCProfileProd.append("FROM commission_profile_products cp ");
		strBuffSelectCProfileProd.append("WHERE cp.product_code = ? AND cp.comm_profile_set_id = ? AND cp.comm_profile_set_version = ? ");
		strBuffSelectCProfileProd.append("AND cp.transaction_type = ? AND cp.payment_mode = ? ");
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "strBuffSelectCProfileProd Query ="
					+ strBuffSelectCProfileProd);
		}

		PreparedStatement pstmtSelectCProfileProdDetail = null;
		ResultSet rsSelectCProfileProdDetail = null;
		final StringBuffer strBuffSelectCProfileProdDetail = new StringBuffer(
				"SELECT cpd.tax1_type,cpd.tax1_rate,cpd.tax2_type,cpd.tax2_rate, ");
		strBuffSelectCProfileProdDetail
				.append("cpd.tax3_type,cpd.tax3_rate,cpd.commission_type,cpd.commission_rate,cpd.comm_profile_detail_id ");
		strBuffSelectCProfileProdDetail
				.append("FROM commission_profile_details cpd ");
		strBuffSelectCProfileProdDetail
				.append("WHERE  cpd.comm_profile_products_id = ? AND cpd.start_range <= ? AND cpd.end_range >= ? ");
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "strBuffSelectCProfileProdDetail Query ="
					+ strBuffSelectCProfileProdDetail);
			// ends here
		}

		// for existance of the product in the transfer profile
		PreparedStatement pstmtSelectTProfileProd = null;
		ResultSet rsSelectTProfileProd = null;
		final StringBuffer strBuffSelectTProfileProd = new StringBuffer(
				" SELECT 1 ");
		strBuffSelectTProfileProd
				.append("FROM transfer_profile_products tpp,transfer_profile tp, transfer_profile catp,transfer_profile_products catpp ");
		strBuffSelectTProfileProd
				.append("WHERE tpp.profile_id=? AND tpp.product_code = ? AND tpp.profile_id=tp.profile_id AND catp.profile_id=catpp.profile_id ");
		strBuffSelectTProfileProd
				.append("AND tpp.product_code=catpp.product_code AND tp.category_code=catp.category_code AND catp.parent_profile_id=? AND catp.status='Y' AND tp.network_code = catp.network_code");
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "strBuffSelectTProfileProd Query ="
					+ strBuffSelectTProfileProd);
			// ends here
		}

		// insert data in the batch master table
		// commented for DB2
		// OraclePreparedStatement pstmtInsertBatchMaster = null
		PreparedStatement pstmtInsertBatchMaster = null;
		final StringBuffer strBuffInsertBatchMaster = new StringBuffer(
				"INSERT INTO c2c_batches (batch_id, network_code, ");
		strBuffInsertBatchMaster
				.append("network_code_for, batch_name, status, domain_code, product_code, ");
		strBuffInsertBatchMaster
				.append("batch_file_name, batch_total_record, batch_date, created_by, created_on, ");
		strBuffInsertBatchMaster
				.append(" modified_by, modified_on,sms_default_lang,sms_second_lang,user_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "strBuffInsertBatchMaster Query ="
					+ strBuffInsertBatchMaster);
			// ends here
		}

		// insert data in the c2c batch items table
		// commented for DB2
		// OraclePreparedStatement pstmtInsertBatchItems = null
		PreparedStatement pstmtInsertBatchItems = null;
		final StringBuffer strBuffInsertBatchItems = new StringBuffer(
				"INSERT INTO c2c_batch_items (batch_id, batch_detail_id, ");
		strBuffInsertBatchItems
				.append("category_code, msisdn, user_id, status, modified_by, modified_on, user_grade_code, ");
		strBuffInsertBatchItems.append("transfer_date, txn_profile, ");
		strBuffInsertBatchItems
				.append("commission_profile_set_id, commission_profile_ver, commission_profile_detail_id, ");
		strBuffInsertBatchItems
				.append("commission_type, commission_rate, commission_value, tax1_type, tax1_rate, ");
		strBuffInsertBatchItems
				.append("tax1_value, tax2_type, tax2_rate, tax2_value, tax3_type, tax3_rate, ");
		strBuffInsertBatchItems
				.append("tax3_value, requested_quantity, transfer_mrp, initiator_remarks, external_code,rcrd_status,transfer_type,transfer_sub_type,dual_comm_type,product_code) ");
		strBuffInsertBatchItems
				.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "strBuffInsertBatchItems Query ="
					+ strBuffInsertBatchItems);
		}
		// ends here
		// update master table with OPEN status
		PreparedStatement pstmtUpdateBatchMaster = null;
		final StringBuffer strBuffUpdateBatchMaster = new StringBuffer(
				"UPDATE c2c_batches SET batch_total_record=? , status =? WHERE batch_id=?");
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "strBuffUpdateBatchMaster Query ="
					+ strBuffUpdateBatchMaster);
		}
		int totalSuccessRecords = 0;
		try {

			pstmtSelectTrfRule = p_con.prepareStatement(strBuffSelectTrfRule
					.toString());
			pstmtSelectTrfRuleProd = p_con
					.prepareStatement(strBuffSelectTrfRuleProd.toString());
			pstmtSelectCProfileProd = p_con
					.prepareStatement(strBuffSelectCProfileProd.toString());
			pstmtSelectCProfileProdDetail = p_con
					.prepareStatement(strBuffSelectCProfileProdDetail
							.toString());
			pstmtSelectTProfileProd = p_con
					.prepareStatement(strBuffSelectTProfileProd.toString());
			// commented for DB2
			// pstmtInsertBatchMaster=(OraclePreparedStatement)p_con.prepareStatement(strBuffInsertBatchMaster.toString())
			// pstmtInsertBatchItems=(OraclePreparedStatement)p_con.prepareStatement(strBuffInsertBatchItems.toString())
			pstmtInsertBatchMaster = (PreparedStatement) p_con
					.prepareStatement(strBuffInsertBatchMaster.toString());

			pstmtInsertBatchItems = (PreparedStatement) p_con
					.prepareStatement(strBuffInsertBatchItems.toString());
			pstmtUpdateBatchMaster = p_con
					.prepareStatement(strBuffUpdateBatchMaster.toString());
			ChannelTransferRuleVO rulesVO = null;
			int index = 0;
			C2CBatchItemsVO batchItemsVO = null;

			final HashMap transferRuleMap = new HashMap();
			final HashMap transferRuleNotExistMap = new HashMap();
			final HashMap transferRuleProdNotExistMap = new HashMap();
			final HashMap transferProfileMap = new HashMap();
			long requestedValue = 0;
			long minTrfValue = 0;
			long maxTrfValue = 0;
			long multipleOf = 0;
			ArrayList transferItemsList = null;
			ChannelTransferItemsVO channelTransferItemsVO = null;

			// insert the master data
			index = 0;
			++index;
			pstmtInsertBatchMaster.setString(index,
					p_batchMasterVO.getBatchId());
			++index;
			pstmtInsertBatchMaster.setString(index,
					p_batchMasterVO.getNetworkCode());
			++index;
			pstmtInsertBatchMaster.setString(index,
					p_batchMasterVO.getNetworkCodeFor());

			// for DB2
			++index;
			pstmtInsertBatchMaster.setString(index,
					p_batchMasterVO.getBatchName());
			++index;
			pstmtInsertBatchMaster
					.setString(index, p_batchMasterVO.getStatus());
			++index;
			pstmtInsertBatchMaster.setString(index,
					p_batchMasterVO.getDomainCode());
			++index;
			pstmtInsertBatchMaster.setString(index,
					p_batchMasterVO.getProductCode());
			++index;
			pstmtInsertBatchMaster.setString(index,
					p_batchMasterVO.getBatchFileName());
			++index;
			pstmtInsertBatchMaster.setLong(index,
					p_batchMasterVO.getBatchTotalRecord());
			++index;
			pstmtInsertBatchMaster.setTimestamp(index, BTSLUtil
					.getTimestampFromUtilDate(p_batchMasterVO.getBatchDate()));
			++index;
			pstmtInsertBatchMaster.setString(index,
					p_batchMasterVO.getCreatedBy());
			++index;
			pstmtInsertBatchMaster.setTimestamp(index, BTSLUtil
					.getTimestampFromUtilDate(p_batchMasterVO.getCreatedOn()));
			++index;
			pstmtInsertBatchMaster.setString(index,
					p_batchMasterVO.getModifiedBy());
			++index;
			pstmtInsertBatchMaster.setTimestamp(index, BTSLUtil
					.getTimestampFromUtilDate(p_batchMasterVO.getModifiedOn()));

			// for DB2
			++index;
			pstmtInsertBatchMaster.setString(index,
					p_batchMasterVO.getDefaultLang());

			// for DB2
			++index;
			pstmtInsertBatchMaster.setString(index,
					p_batchMasterVO.getSecondLang());
			++index;
			pstmtInsertBatchMaster
					.setString(index, p_batchMasterVO.getUserId());

			int queryExecutionCount = pstmtInsertBatchMaster.executeUpdate();
			if (queryExecutionCount <= 0) {
				p_con.rollback();
				LOG.error(methodName,
						"Unable to insert in the batch master table.");
				BatchC2CFileProcessLog
						.detailLog(
								methodName,
								p_batchMasterVO,
								batchItemsVO,
								"FAIL : DB Error Unable to insert in the batch master table",
								"queryExecutionCount=" + queryExecutionCount);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,
						EventComponentI.SYSTEM, EventStatusI.RAISED,
						EventLevelI.FATAL,
						"C2CBatchTransferWebDAO[initiateBatchC2CTransfer]", "",
						"", "", "Unable to insert in the batch master table.");
				throw new BTSLBaseException(this, methodName,
						"error.general.sql.processing");
			}
			// ends here

			String msgArr[] = null;
			for (int i = 0, j = p_batchItemsList.size(); i < j; i++) {
				
				try{
					
				batchItemsVO = (C2CBatchItemsVO) p_batchItemsList.get(i);

				// load the product's informaiton.
				if (transferRuleNotExistMap.get(batchItemsVO.getCategoryCode()) == null) {
					if (transferRuleProdNotExistMap.get(batchItemsVO
							.getCategoryCode()) == null) {
						if (transferRuleMap.get(batchItemsVO.getCategoryCode()) == null) {
							index = 0;
							++index;
							pstmtSelectTrfRule.setString(index,
									p_batchMasterVO.getNetworkCode());
							++index;
							pstmtSelectTrfRule.setString(index,
									p_batchMasterVO.getDomainCode());
							++index;
							pstmtSelectTrfRule.setString(index, fromCatgory);
							++index;
							pstmtSelectTrfRule.setString(index,
									batchItemsVO.getCategoryCode());
							rsSelectTrfRule = pstmtSelectTrfRule.executeQuery();
							pstmtSelectTrfRule.clearParameters();
							if (rsSelectTrfRule.next()) {
								rulesVO = new ChannelTransferRuleVO();
								rulesVO.setTransferRuleID(rsSelectTrfRule
										.getString("transfer_rule_id"));
								rulesVO.setTransferType(rsSelectTrfRule
										.getString("transfer_type"));
								rulesVO.setTransferAllowed(rsSelectTrfRule
										.getString("transfer_allowed"));
								index = 0;
								++index;
								pstmtSelectTrfRuleProd.setString(index,
										rulesVO.getTransferRuleID());
								++index;
								pstmtSelectTrfRuleProd.setString(index,
										p_batchMasterVO.getProductCode());
								rsSelectTrfRuleProd = pstmtSelectTrfRuleProd
										.executeQuery();
								pstmtSelectTrfRuleProd.clearParameters();
								if (!rsSelectTrfRuleProd.next()) {
									transferRuleProdNotExistMap.put(
											batchItemsVO.getCategoryCode(),
											batchItemsVO.getCategoryCode());
									// put error log Prodcuct is not in the
									// transfer rule
									errorVO = new ListValueVO(
											batchItemsVO.getMsisdn(),
											String.valueOf(batchItemsVO
													.getRecordNumber()),
											p_messages
													.getMessage(p_locale,
															"batchc2c.initiatebatchc2ctransfer.msg.error.prodnotintrfrule"));
									errorList.add(errorVO);
									BatchC2CFileProcessLog
											.detailLog(
													methodName,
													p_batchMasterVO,
													batchItemsVO,
													"FAIL : Product is not in the transfer rule",
													"");
									continue;
								}
								transferRuleMap
										.put(batchItemsVO.getCategoryCode(),
												rulesVO);
							} else {
								transferRuleNotExistMap.put(
										batchItemsVO.getCategoryCode(),
										batchItemsVO.getCategoryCode());
								// put error log transfer rule not defined
								errorVO = new ListValueVO(
										batchItemsVO.getMsisdn(),
										String.valueOf(batchItemsVO
												.getRecordNumber()),
										p_messages
												.getMessage(p_locale,
														"batchc2c.initiatebatchc2ctransfer.msg.error.trfrulenotdefined"));
								errorList.add(errorVO);
								BatchC2CFileProcessLog.detailLog(methodName,
										p_batchMasterVO, batchItemsVO,
										"FAIL : Transfer rule not defined", "");
								continue;
							}
						}// transfer rule loading
					}// Procuct is not associated with transfer rule not defined
						// check
					else {
						// put error log Procuct is not in the transfer rule
						errorVO = new ListValueVO(
								batchItemsVO.getMsisdn(),
								String.valueOf(batchItemsVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.initiatebatchc2ctransfer.msg.error.prodnotintrfrule"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog.detailLog(methodName,
								p_batchMasterVO, batchItemsVO,
								"FAIL : Product is not in the transfer rule",
								"");
						continue;
					}
				}// transfer rule not defined check
				else {
					// put error log transfer rule not defined
					errorVO = new ListValueVO(
							batchItemsVO.getMsisdn(),
							String.valueOf(batchItemsVO.getRecordNumber()),
							p_messages
									.getMessage(p_locale,
											"batchc2c.initiatebatchc2ctransfer.msg.error.trfrulenotdefined"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog.detailLog(methodName,
							p_batchMasterVO, batchItemsVO,
							"FAIL : Transfer rule not defined", "");
					continue;
				}
				rulesVO = (ChannelTransferRuleVO) transferRuleMap
						.get(batchItemsVO.getCategoryCode());
				if (PretupsI.NO.equals(rulesVO.getTransferAllowed())) {
					// put error according to the transfer rule C2C transfer is
					// not allowed.
					errorVO = new ListValueVO(
							batchItemsVO.getMsisdn(),
							String.valueOf(batchItemsVO.getRecordNumber()),
							p_messages
									.getMessage(p_locale,
											"batchc2c.initiatebatchc2ctransfer.msg.error.c2cnotallowed"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog
							.detailLog(
									methodName,
									p_batchMasterVO,
									batchItemsVO,
									"FAIL : According to the transfer rule C2C transfer is not allowed",
									"");
					continue;
				}
				// check the transfer profile product code
				// transfer profile check ends here
				if (transferProfileMap.get(batchItemsVO.getTxnProfile()) == null) {
					index = 0;
					++index;
					pstmtSelectTProfileProd.setString(index,
							batchItemsVO.getTxnProfile());
					++index;
					pstmtSelectTProfileProd.setString(index,
							p_batchMasterVO.getProductCode());
					++index;
					pstmtSelectTProfileProd.setString(index,
							PretupsI.PARENT_PROFILE_ID_CATEGORY);
					rsSelectTProfileProd = pstmtSelectTProfileProd
							.executeQuery();
					pstmtSelectTProfileProd.clearParameters();
					if (!rsSelectTProfileProd.next()) {
						transferProfileMap.put(batchItemsVO.getTxnProfile(),
								"false");
						// put error Transfer profile for this product is not
						// define
						errorVO = new ListValueVO(
								batchItemsVO.getMsisdn(),
								String.valueOf(batchItemsVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.initiatebatchc2ctransfer.msg.error.trfprofilenotdefined"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog
								.detailLog(
										methodName,
										p_batchMasterVO,
										batchItemsVO,
										"FAIL : Transfer profile for this product is not defined",
										"");
						continue;
					}
					transferProfileMap
							.put(batchItemsVO.getTxnProfile(), "true");
				} else {

					if ("false".equals(transferProfileMap.get(batchItemsVO
							.getTxnProfile()))) {
						// put error Transfer profile for this product is not
						// define
						errorVO = new ListValueVO(
								batchItemsVO.getMsisdn(),
								String.valueOf(batchItemsVO.getRecordNumber()),
								p_messages
										.getMessage(p_locale,
												"batchc2c.initiatebatchc2ctransfer.msg.error.trfprofilenotdefined"));
						errorList.add(errorVO);
						BatchC2CFileProcessLog
								.detailLog(
										methodName,
										p_batchMasterVO,
										batchItemsVO,
										"FAIL : Transfer profile for this product is not defined",
										"");
						continue;
					}
				}

				// check the commisson profile applicability and other checks
				// related to the commission profile
				index = 0;
				++index;
				pstmtSelectCProfileProd.setString(index, p_batchMasterVO.getProductCode());
				++index;
				pstmtSelectCProfileProd.setString(index, batchItemsVO.getCommissionProfileSetId());
				++index;
				pstmtSelectCProfileProd.setString(index, batchItemsVO.getCommissionProfileVer());
				++index;
                pstmtSelectCProfileProd.setString(index, (isTransationTypeAlwd)?PretupsI.TRANSFER_TYPE_C2C:PretupsI.ALL);
                ++index;
                pstmtSelectCProfileProd.setString(index, PretupsI.ALL);
				rsSelectCProfileProd = pstmtSelectCProfileProd.executeQuery();
				pstmtSelectCProfileProd.clearParameters();
				if (!rsSelectCProfileProd.next()) {
					// put error commission profile for this product is not
					// defined
					errorVO = new ListValueVO(
							batchItemsVO.getMsisdn(),
							String.valueOf(batchItemsVO.getRecordNumber()),
							p_messages
									.getMessage(p_locale,
											"batchc2c.initiatebatchc2ctransfer.msg.error.commprfnotdefined"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog
							.detailLog(
									methodName,
									p_batchMasterVO,
									batchItemsVO,
									"FAIL : Commission profile for this product is not defined",
									"");
					continue;
				}
				requestedValue = batchItemsVO.getRequestedQuantity();
				minTrfValue = rsSelectCProfileProd
						.getLong("min_transfer_value");
				maxTrfValue = rsSelectCProfileProd
						.getLong("max_transfer_value");
				if (minTrfValue > requestedValue
						|| maxTrfValue < requestedValue) {
					msgArr = new String[3];
					msgArr[0] = PretupsBL.getDisplayAmount(requestedValue);
					msgArr[1] = PretupsBL.getDisplayAmount(minTrfValue);
					msgArr[2] = PretupsBL.getDisplayAmount(maxTrfValue);
					// put error requested quantity is not between min and max
					// values
					errorVO = new ListValueVO(
							batchItemsVO.getMsisdn(),
							String.valueOf(batchItemsVO.getRecordNumber()),
							p_messages
									.getMessage(
											p_locale,
											"batchc2c.initiatebatchc2ctransfer.msg.error.qtymaxmin",
											msgArr));
					msgArr = null;
					errorList.add(errorVO);
					BatchC2CFileProcessLog
							.detailLog(
									methodName,
									p_batchMasterVO,
									batchItemsVO,
									"FAIL : Requested quantity is not between min and max values",
									"minTrfValue=" + minTrfValue
											+ ", maxTrfValue=" + maxTrfValue);
					continue;
				}
				multipleOf = rsSelectCProfileProd
						.getLong("transfer_multiple_off");
				if (requestedValue % multipleOf != 0) {
					// put error requested quantity is not multiple of
					errorVO = new ListValueVO(
							batchItemsVO.getMsisdn(),
							String.valueOf(batchItemsVO.getRecordNumber()),
							p_messages
									.getMessage(
											p_locale,
											"batchc2c.initiatebatchc2ctransfer.msg.error.notmulof",
											new String[] { PretupsBL
													.getDisplayAmount(multipleOf) }));
					errorList.add(errorVO);
					BatchC2CFileProcessLog
							.detailLog(
									methodName,
									p_batchMasterVO,
									batchItemsVO,
									"FAIL : Requested quantity is not in multiple value",
									"multiple of=" + multipleOf);
					continue;
				}

				index = 0;
				++index;
				pstmtSelectCProfileProdDetail.setString(index,
						rsSelectCProfileProd
								.getString("comm_profile_products_id"));
				++index;
				pstmtSelectCProfileProdDetail.setLong(index, requestedValue);
				++index;
				pstmtSelectCProfileProdDetail.setLong(index, requestedValue);
				rsSelectCProfileProdDetail = pstmtSelectCProfileProdDetail
						.executeQuery();
				pstmtSelectCProfileProdDetail.clearParameters();
				if (!rsSelectCProfileProdDetail.next()) {
					// put error commission profile slab is not define for the
					// requested value
					errorVO = new ListValueVO(
							batchItemsVO.getMsisdn(),
							String.valueOf(batchItemsVO.getRecordNumber()),
							p_messages
									.getMessage(p_locale,
											"batchc2c.initiatebatchc2ctransfer.msg.error.commslabnotdefined"));
					errorList.add(errorVO);
					BatchC2CFileProcessLog
							.detailLog(
									methodName,
									p_batchMasterVO,
									batchItemsVO,
									"FAIL : Commission profile slab is not define for the requested value",
									"");
					continue;
				}
				// to calculate tax
				transferItemsList = new ArrayList();
				channelTransferItemsVO = new ChannelTransferItemsVO();
				// this value will be inserted into the table as the requested
				// qty
				channelTransferItemsVO.setRequiredQuantity(requestedValue);
				// this value will be used in the tax calculation.
				channelTransferItemsVO.setRequestedQuantity(PretupsBL
						.getDisplayAmount(requestedValue));
				channelTransferItemsVO
						.setCommProfileDetailID(rsSelectCProfileProdDetail
								.getString("comm_profile_detail_id"));
				channelTransferItemsVO.setUnitValue(p_batchMasterVO
						.getProductMrp());
				channelTransferItemsVO.setCommRate(rsSelectCProfileProdDetail
						.getLong("commission_rate"));
				channelTransferItemsVO.setCommType(rsSelectCProfileProdDetail
						.getString("commission_type"));
				channelTransferItemsVO.setDiscountRate(rsSelectCProfileProd
						.getLong("discount_rate"));
				channelTransferItemsVO.setDiscountType(rsSelectCProfileProd
						.getString("discount_type"));
				channelTransferItemsVO.setTax1Rate(rsSelectCProfileProdDetail
						.getLong("tax1_rate"));
				channelTransferItemsVO.setTax1Type(rsSelectCProfileProdDetail
						.getString("tax1_type"));
				channelTransferItemsVO.setTax2Rate(rsSelectCProfileProdDetail
						.getLong("tax2_rate"));
				channelTransferItemsVO.setTax2Type(rsSelectCProfileProdDetail
						.getString("tax2_type"));
				channelTransferItemsVO.setTax3Rate(rsSelectCProfileProdDetail
						.getLong("tax3_rate"));
				channelTransferItemsVO.setTax3Type(rsSelectCProfileProdDetail
						.getString("tax3_type"));
				if (PretupsI.YES.equals(rsSelectCProfileProd
						.getString("taxes_on_foc_applicable"))) {
					channelTransferItemsVO.setTaxOnC2CTransfer(PretupsI.YES);
				} else {
					channelTransferItemsVO.setTaxOnC2CTransfer(PretupsI.NO);
				}
				// added by vikram
				if (PretupsI.YES.equals(rsSelectCProfileProd
						.getString("taxes_on_channel_transfer"))) {
					channelTransferItemsVO
							.setTaxOnChannelTransfer(PretupsI.YES);
				} else {
					channelTransferItemsVO.setTaxOnChannelTransfer(PretupsI.NO);
				}
				transferItemsList.add(channelTransferItemsVO);
				final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
				channelTransferVO
						.setChannelTransferitemsVOList(transferItemsList);
				channelTransferVO.setTransferSubType(p_batchMasterVO
						.getTransferSubType());
				// ChannelTransferBL.calculateMRPWithTaxAndDiscount(channelTransferVO,PretupsI.TRANSFER_TYPE_FOC)
				channelTransferVO.setDualCommissionType(batchItemsVO.getDualCommissionType());
				ChannelTransferBL.calculateMRPWithTaxAndDiscount(
						channelTransferVO, PretupsI.TRANSFER_TYPE_C2C);
				// taxes on C2C required
				// ends commission profile validaiton
				// insert items data here
				index = 0;
				++index;
				pstmtInsertBatchItems.setString(index,
						batchItemsVO.getBatchId());
				++index;
				pstmtInsertBatchItems.setString(index,
						batchItemsVO.getBatchDetailId());
				++index;
				pstmtInsertBatchItems.setString(index,
						batchItemsVO.getCategoryCode());
				++index;
				pstmtInsertBatchItems
						.setString(index, batchItemsVO.getMsisdn());
				++index;
				pstmtInsertBatchItems
						.setString(index, batchItemsVO.getUserId());
				++index;
				pstmtInsertBatchItems
						.setString(index, batchItemsVO.getStatus());
				++index;
				pstmtInsertBatchItems.setString(index,
						batchItemsVO.getModifiedBy());
				++index;
				pstmtInsertBatchItems
						.setTimestamp(index, BTSLUtil
								.getTimestampFromUtilDate(batchItemsVO
										.getModifiedOn()));
				++index;
				pstmtInsertBatchItems.setString(index,
						batchItemsVO.getUserGradeCode());
				++index;
				pstmtInsertBatchItems
						.setDate(index, BTSLUtil
								.getSQLDateFromUtilDate(batchItemsVO
										.getTransferDate()));
				++index;
				pstmtInsertBatchItems.setString(index,
						batchItemsVO.getTxnProfile());
				++index;
				pstmtInsertBatchItems.setString(index,
						batchItemsVO.getCommissionProfileSetId());
				++index;
				pstmtInsertBatchItems.setString(index,
						batchItemsVO.getCommissionProfileVer());
				++index;
				pstmtInsertBatchItems.setString(index,
						channelTransferItemsVO.getCommProfileDetailID());
				++index;
				pstmtInsertBatchItems.setString(index,
						channelTransferItemsVO.getCommType());
				++index;
				pstmtInsertBatchItems.setDouble(index,
						channelTransferItemsVO.getCommRate());
				++index;
				pstmtInsertBatchItems.setLong(index,
						channelTransferItemsVO.getCommValue());
				++index;
				pstmtInsertBatchItems.setString(index,
						channelTransferItemsVO.getTax1Type());
				++index;
				pstmtInsertBatchItems.setDouble(index,
						channelTransferItemsVO.getTax1Rate());
				++index;
				pstmtInsertBatchItems.setLong(index,
						channelTransferItemsVO.getTax1Value());
				++index;
				pstmtInsertBatchItems.setString(index,
						channelTransferItemsVO.getTax2Type());
				++index;
				pstmtInsertBatchItems.setDouble(index,
						channelTransferItemsVO.getTax2Rate());
				++index;
				pstmtInsertBatchItems.setLong(index,
						channelTransferItemsVO.getTax2Value());
				++index;
				pstmtInsertBatchItems.setString(index,
						channelTransferItemsVO.getTax3Type());
				++index;
				pstmtInsertBatchItems.setDouble(index,
						channelTransferItemsVO.getTax3Rate());
				++index;
				pstmtInsertBatchItems.setLong(index,
						channelTransferItemsVO.getTax3Value());
				++index;
				pstmtInsertBatchItems.setLong(index, 
						channelTransferItemsVO.getRequiredQuantity());
				++index;
				pstmtInsertBatchItems.setLong(index,
						channelTransferItemsVO.getProductTotalMRP());
				// for DB2
				++index;
				pstmtInsertBatchItems.setString(index,
						batchItemsVO.getInitiatorRemarks());
				++index;
				pstmtInsertBatchItems.setString(index,
						batchItemsVO.getExternalCode());
				++index;
				pstmtInsertBatchItems
						.setString(
								index,
								PretupsI.CHANNEL_TRANSFER_BATCH_C2C_ITEM_RCRDSTATUS_PROCESSED);
				++index;
				pstmtInsertBatchItems.setString(index,
						batchItemsVO.getTransferType());
				++index;
				pstmtInsertBatchItems.setString(index,
						batchItemsVO.getTransferSubType());
				++index;
				pstmtInsertBatchItems.setString(index,batchItemsVO.getDualCommissionType()
						);
				++index;
				pstmtInsertBatchItems.setString(index,channelTransferItemsVO.getProductCode()
						);
				queryExecutionCount = pstmtInsertBatchItems.executeUpdate();
				if (queryExecutionCount <= 0) {
					p_con.rollback();
					// put error record can not be inserted
					LOG.error(methodName,
							"Record cannot be inserted in batch items table");
					BatchC2CFileProcessLog
							.detailLog(
									methodName,
									p_batchMasterVO,
									batchItemsVO,
									"FAIL : DB Error Record cannot be inserted in batch items table",
									"queryExecutionCount="
											+ queryExecutionCount);
				} else {
					//p_con.commit();
					totalSuccessRecords++;
					// put success in the logger file.
					/*BatchC2CFileProcessLog
							.detailLog(
									methodName,
									p_batchMasterVO,
									batchItemsVO,
									"PASS : Record inserted successfully in batch items table",
									"queryExecutionCount="
											+ queryExecutionCount);*/
				}
				}
				//try end for loop
					catch (SQLException sqe)
					{
						LOG.error(methodName, "SQLException : " + sqe);
						LOG.errorTrace(methodName,sqe);
						errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"error.sql.record.processing"));
						errorList.add(errorVO);
						
						EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2CBatchTransferWebDAO[initiateBatchC2CTransfer]","","","","SQL Exception:"+sqe.getMessage());
						BatchC2CFileProcessLog.c2cBatchMasterLog(methodName,p_batchMasterVO,"FAIL : SQL Exception:"+sqe.getMessage(),"TOTAL SUCCESS RECORDS = "+totalSuccessRecords);
						if(!BTSLUtil.isNullString(partialProceesAllowed) && partialProceesAllowed.equalsIgnoreCase(PretupsI.NO))
							p_con.rollback();
					
					}
					catch (Exception ex)
					{
					    LOG.error(methodName, "Exception : " + ex);
					    LOG.errorTrace(methodName,ex);
					    errorVO=new ListValueVO(batchItemsVO.getMsisdn(),String.valueOf(batchItemsVO.getRecordNumber()),p_messages.getMessage(p_locale,"error.sql.record.processing"));
						errorList.add(errorVO);
					    
						EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2CBatchTransferWebDAO[initiateBatchC2CTransfer]","","","","Exception:"+ex.getMessage());
						BatchC2CFileProcessLog.c2cBatchMasterLog(methodName,p_batchMasterVO,"FAIL : Exception:"+ex.getMessage(),"TOTAL SUCCESS RECORDS = "+totalSuccessRecords);
						
						if(!BTSLUtil.isNullString(partialProceesAllowed) && partialProceesAllowed.equalsIgnoreCase(PretupsI.NO))
							p_con.rollback();
						
					}
					if(!BTSLUtil.isNullString(partialProceesAllowed) && partialProceesAllowed.equalsIgnoreCase(PretupsI.YES))
					p_con.commit();
				}// end of for loop for the batch items
				if(!BTSLUtil.isNullString(partialProceesAllowed) && partialProceesAllowed.equalsIgnoreCase(PretupsI.NO))
				{
					if(!errorList.isEmpty() || _errorExistInfile)
						p_con.rollback();
					else
					p_con.commit();
				}	
				// ends here
			// for loop for the batch items
		} catch (SQLException sqe) {
			LOG.error(methodName, "SQLException : " + sqe);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2CBatchTransferWebDAO[initiateBatchC2CTransfer]", "", "",
					"", "SQL Exception:" + sqe.getMessage());
			BatchC2CFileProcessLog.c2cBatchMasterLog(methodName,
					p_batchMasterVO,
					"FAIL : SQL Exception:" + sqe.getMessage(),
					"TOTAL SUCCESS RECORDS = " + totalSuccessRecords);
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		} catch (Exception ex) {
			LOG.error(methodName, "Exception : " + ex);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2CBatchTransferWebDAO[initiateBatchC2CTransfer]", "", "",
					"", "Exception:" + ex.getMessage());
			BatchC2CFileProcessLog.c2cBatchMasterLog(methodName,
					p_batchMasterVO, "FAIL : Exception:" + ex.getMessage(),
					"TOTAL SUCCESS RECORDS = " + totalSuccessRecords);
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		} finally {

			try {
				if (rsSelectTrfRule != null) {
					rsSelectTrfRule.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtSelectTrfRule != null) {
					pstmtSelectTrfRule.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (rsSelectTrfRuleProd != null) {
					rsSelectTrfRuleProd.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtSelectTrfRuleProd != null) {
					pstmtSelectTrfRuleProd.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (rsSelectCProfileProd != null) {
					rsSelectCProfileProd.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtSelectCProfileProd != null) {
					pstmtSelectCProfileProd.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (rsSelectCProfileProdDetail != null) {
					rsSelectCProfileProdDetail.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtSelectCProfileProdDetail != null) {
					pstmtSelectCProfileProdDetail.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (rsSelectTProfileProd != null) {
					rsSelectTProfileProd.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtSelectTProfileProd != null) {
					pstmtSelectTProfileProd.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtInsertBatchMaster != null) {
					pstmtInsertBatchMaster.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtInsertBatchItems != null) {
					pstmtInsertBatchItems.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				// if all records contains errors then rollback the master table
				// entry
				if (errorList != null
						&& (errorList.size() == p_batchItemsList.size())) {
					if (!BTSLUtil.isNullString(partialProceesAllowed) && partialProceesAllowed.equalsIgnoreCase(PretupsI.YES))
					{
						int index=0;
						int queryExecutionCount=-1;
						pstmtUpdateBatchMaster.setInt(++index,p_batchMasterVO.getBatchTotalRecord()-errorList.size());
						pstmtUpdateBatchMaster.setString(++index,PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_CANCEL);
						pstmtUpdateBatchMaster.setString(++index,p_batchMasterVO.getBatchId());
						queryExecutionCount=pstmtUpdateBatchMaster.executeUpdate();
					    if(queryExecutionCount<=0) //Means No Records Updated
			   		    {
			   		        LOG.error(methodName,"Unable to Update the batch size in master table..");
			   		        p_con.rollback();
							EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2CBatchTransferWebDAO[initiateBatchC2CTransfer]","","","","Error while updating C2C_BATCHES table. Batch id="+p_batchMasterVO.getBatchId());
			   		    }
			   		    else
			   		    {
			   		        p_con.commit();
			   		    }
					}else
					p_con.rollback();
					LOG.error(methodName,
							"ALL the records conatins errors and cannot be inserted in db");
					BatchC2CFileProcessLog
							.c2cBatchMasterLog(
									methodName,
									p_batchMasterVO,
									"FAIL : ALL the records conatins errors and cannot be inserted in DB ",
									"");
				}
				// else update the master table with the open status and total
				// number of records.
				else {
					int index = 0;
					int queryExecutionCount = -1;
					++index;
					pstmtUpdateBatchMaster.setInt(
							index,
							p_batchMasterVO.getBatchTotalRecord()
									- errorList.size());
					++index;
					pstmtUpdateBatchMaster.setString(index,
							PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_OPEN);
					++index;
					pstmtUpdateBatchMaster.setString(index,
							p_batchMasterVO.getBatchId());
					queryExecutionCount = pstmtUpdateBatchMaster
							.executeUpdate();
					if (queryExecutionCount <= 0) // Means No Records Updated
					{
						LOG.error(methodName,
								"Unable to Update the batch size in master table..");
						p_con.rollback();
						EventHandler
								.handle(EventIDI.SYSTEM_ERROR,
										EventComponentI.SYSTEM,
										EventStatusI.RAISED,
										EventLevelI.FATAL,
										"C2CBatchTransferWebDAO[initiateBatchC2CTransfer]",
										"", "", "",
										"Error while updating C2C_BATCHES table. Batch id="
												+ p_batchMasterVO.getBatchId());
					} else {
						p_con.commit();
					}
				}

			} catch (Exception e) {
				try {
					if (p_con != null) {
						p_con.rollback();
					}
				} catch (Exception ex) {
					LOG.errorTrace(methodName, ex);
				}
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmtUpdateBatchMaster != null) {
					pstmtUpdateBatchMaster.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName,
						"Exiting: errorList.size()=" + errorList.size());
			}
		}
		return errorList;
	}
	
	
	/**
	 * Method for loading C2CBatch details for both Transfer and Withdraw.. This method will load the batches
	 * that are within the geography of user whose userId is passed with
	 * status(OPEN) also in items table for corresponding master record the
	 * status is in p_itemStatus
	 * Also if category is passed, filter on category will be applied
	 * 
	 * @param p_con
	 *            java.sql.Connection
	 * @param p_itemStatus
	 *            String
	 * @param p_currentLevel
	 *            String
	 * @param category
	 *            String
	 * @return java.util.ArrayList
	 * @throws BTSLBaseException
	 */
	public ArrayList loadBatchC2CMasterDetailsForTxrAndWdr(Connection p_con,
			String p_userID, String p_itemStatus, String p_currentLevel, String category)
			throws BTSLBaseException {
		final String methodName = "loadBatchC2CMasterDetailsForTxrAndWdr";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered p_userID=" + p_userID
					+ " p_itemStatus=" + p_itemStatus + " p_currentLevel="
					+ p_currentLevel);
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sqlSelect = c2CBatchTransferWebQry
				.loadBatchC2CMasterDetailsForTxrAndWdrQry(p_itemStatus,
						p_currentLevel, category);
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		}
		final ArrayList list = new ArrayList();
		int index = 0;
		try {
			pstmt = p_con.prepareStatement(sqlSelect);
			index = 0;
			++index;
			pstmt.setString(index, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
			++index;
			pstmt.setString(index, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
			++index;
			pstmt.setString(index, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
			++index;
			pstmt.setString(index, p_userID);
			
			if(!category.equalsIgnoreCase("ALL")) {
				++index;
				pstmt.setString(index, category);
			}
			
			++index;
			pstmt.setString(index, PretupsI.CHANNEL_TRANSFER_BATCH_C2C_STATUS_OPEN);
			++index;
			pstmt.setString(index,PretupsI.CHANNEL_TRANSFER_BATCH_C2C_ITEM_RCRDSTATUS_PROCESSED);
			++index;
			pstmt.setString(index, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
			++index;
			pstmt.setString(index, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW);
			
			rs = pstmt.executeQuery();
			C2CBatchMasterVO c2cBatchMasterVO = null;
			while (rs.next()) {
				c2cBatchMasterVO = new C2CBatchMasterVO();
				c2cBatchMasterVO.setBatchId(rs.getString("batch_id"));
				c2cBatchMasterVO.setBatchName(rs.getString("batch_name"));
				//c2cBatchMasterVO.setProductMrp(rs.getLong("unit_value"));
				//c2cBatchMasterVO.setProductMrpStr(PretupsBL.getDisplayAmount(rs
					//	.getLong("unit_value")));
				c2cBatchMasterVO.setBatchTotalRecord(rs
						.getInt("batch_total_record"));
				c2cBatchMasterVO.setNewRecords(rs.getInt("new"));
				c2cBatchMasterVO.setClosedRecords(rs.getInt("close"));
				c2cBatchMasterVO.setRejectedRecords(rs.getInt("cncl"));
				c2cBatchMasterVO.setNetworkCode(rs.getString("network_code"));
				c2cBatchMasterVO.setNetworkCodeFor(rs
						.getString("network_code_for"));
				c2cBatchMasterVO.setProductCode(rs.getString("product_code"));
				c2cBatchMasterVO.setProductName(rs.getString("product_name"));
				c2cBatchMasterVO.setModifiedBy(rs.getString("modified_by"));
				c2cBatchMasterVO.setModifiedOn(rs.getTimestamp("modified_on"));
				//c2cBatchMasterVO.setProductType(rs.getString("product_type"));
				//c2cBatchMasterVO
					//	.setProductShortName(rs.getString("short_name"));
				c2cBatchMasterVO.setDomainCode(rs.getString("domain_code"));
				c2cBatchMasterVO.setBatchDate(rs.getDate("batch_date"));
				c2cBatchMasterVO.setDefaultLang(rs
						.getString("sms_default_lang"));
				c2cBatchMasterVO.setSecondLang(rs.getString("sms_second_lang"));
				c2cBatchMasterVO.setTransferType(rs.getString("transfer_type"));
				c2cBatchMasterVO.setTransferSubType(rs
						.getString("transfer_sub_type"));
				c2cBatchMasterVO.setCreatedBy(rs.getString("created_by"));
				c2cBatchMasterVO.setUserId(rs.getString("user_id"));
				c2cBatchMasterVO.setCategoryCode(rs.getString("category_code"));
				list.add(c2cBatchMasterVO);
			}
		} catch (SQLException sqe) {
			LOG.error(methodName, "SQLException : " + sqe);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2CBatchTransferWebDAO[loadBatchC2CMasterDetailsForTxrAndWdr]",
					"", "", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		} catch (Exception ex) {
			LOG.error("loadBatchC2CMasterDetails", "Exception : " + ex);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2CBatchTransferWebDAO[loadBatchC2CMasterDetailsForTxrAndWdr]",
					"", "", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting: c2cBatchMasterVOList size="
						+ list.size());
			}
		}
		return list;
	}
	/**
	 * This method load Batch details according to batch id.
	 * 
	 * loadBatchDetailsListDownload
	 * 
	 * @param p_con
	 *            Connection
	 * @param p_batchId
	 *            String
	 * @return List<C2CBatchMasterVO> list
	 * @throws BTSLBaseException
	 *            
	 */
	public List<C2CBatchMasterVO> loadBatchDetailsListDownload(Connection p_con, String p_batchId)
			throws BTSLBaseException {
		final String methodName = "loadBatchDetailsListDownload";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered p_batchId=" + p_batchId);
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sqlSelect = c2CBatchTransferWebQry
				.loadBatchDetailsListDownloadQry();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		}
		C2CBatchMasterVO c2cBatchMasterVO = null;
		C2CBatchItemsVO c2cBatchItemsVO = null;
		final List<C2CBatchMasterVO> list = new ArrayList();
		try {
			pstmt = p_con.prepareStatement(sqlSelect);
			pstmt.setString(1, p_batchId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				c2cBatchMasterVO = new C2CBatchMasterVO();
				c2cBatchMasterVO.setBatchId(rs.getString("batch_id"));
				c2cBatchMasterVO.setBatchName(rs.getString("batch_name"));
				c2cBatchMasterVO.setStatus(rs.getString("status"));
				c2cBatchMasterVO.setDomainCode(rs.getString("domain_code"));
				c2cBatchMasterVO.setDomainCodeDesc(rs.getString("domain_name"));
				c2cBatchMasterVO.setProductCode(rs.getString("product_code"));
				c2cBatchMasterVO.setProductCodeDesc(rs
						.getString("product_name"));
				c2cBatchMasterVO.setBatchFileName(rs
						.getString("batch_file_name"));
				c2cBatchMasterVO.setBatchTotalRecord(rs
						.getInt("batch_total_record"));
				c2cBatchMasterVO.setBatchDate(rs.getDate("batch_date"));
				
				c2cBatchMasterVO.setBatchDateStr(BTSLUtil.getDateTimeStringFromDate(c2cBatchMasterVO.getBatchDate()));
				
				if (c2cBatchMasterVO.getBatchDate()!= null) {
					c2cBatchMasterVO.setBatchDateStr(
							BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(c2cBatchMasterVO.getBatchDate())));
				}
				c2cBatchMasterVO.setCreatedBy(rs.getString("created_by"));
				c2cBatchMasterVO.setCreatedOn(rs.getTimestamp("created_on"));		
				c2cBatchItemsVO = C2CBatchItemsVO.getInstance();
				c2cBatchItemsVO.setBatchDetailId(rs
						.getString("batch_detail_id"));
				c2cBatchItemsVO.setUserName(rs.getString("user_name"));
				c2cBatchItemsVO.setExternalCode(rs.getString("external_code"));
				c2cBatchItemsVO.setMsisdn(rs.getString("msisdn"));
				c2cBatchItemsVO.setCategoryName(rs.getString("category_name"));
				c2cBatchItemsVO.setCategoryCode(rs.getString("category_code"));
				//c2cBatchItemsVO.setStatus(rs.getString("status_item"));
				c2cBatchItemsVO.setUserGradeCode(rs
						.getString("user_grade_code"));
				c2cBatchItemsVO.setGradeCode(rs.getString("user_grade_code"));
				//c2cBatchItemsVO.setGradeName(rs.getString("grade_name"));
				c2cBatchItemsVO.setReferenceNo(rs.getString("reference_no"));
				c2cBatchItemsVO.setTransferDate(rs.getDate("transfer_date"));
				if (c2cBatchItemsVO.getTransferDate() != null) {
					c2cBatchItemsVO.setTransferDateStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil
							.getDateStringFromDate(c2cBatchItemsVO
									.getTransferDate())));
				}
				c2cBatchItemsVO.setTxnProfile(rs.getString("txn_profile"));
				c2cBatchItemsVO.setCommissionProfileSetId(rs
						.getString("commission_profile_set_id"));
				c2cBatchItemsVO.setCommissionProfileVer(rs
						.getString("commission_profile_ver"));
				c2cBatchItemsVO.setCommissionProfileDetailId(rs
						.getString("commission_profile_detail_id"));
				c2cBatchItemsVO.setCommissionRate(rs
						.getDouble("commission_rate"));
				c2cBatchItemsVO.setCommissionType(rs
						.getString("commission_type"));
				c2cBatchItemsVO.setRequestedQuantity(rs
						.getLong("requested_quantity"));
				c2cBatchItemsVO.setTransferMrp(rs.getLong("transfer_mrp"));
				c2cBatchItemsVO.setInitiatorRemarks(rs
						.getString("initiator_remarks"));
				c2cBatchItemsVO.setApprovedBy(rs.getString("approved_by"));
				c2cBatchItemsVO.setApprovedOn(rs.getTimestamp("approved_on"));
				c2cBatchItemsVO.setApproverRemarks(rs
						.getString("approver_remarks"));

				c2cBatchMasterVO.setC2cBatchItemsVO(c2cBatchItemsVO);

				list.add(c2cBatchMasterVO);
			}
		} catch (SQLException sqe) {
			LOG.error(methodName, "SQLException : " + sqe);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2CBatchTransferWebDAO[loadBatchDetailsList]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		} catch (Exception ex) {
			LOG.error(methodName, "Exception : " + ex);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2CBatchTransferWebDAO[loadBatchDetailsList]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(
						methodName,
						"Exiting: loadBatchDetailsList  list.size()="
								+ list.size());
			}
		}
		return list;
	}
	/**
	 * This method load Batch details according to batch id.
	 * 
	 * loadBatchDetailsListByBatchId
	 * 
	 * @param p_con
	 *            Connection
	 * @param p_batchId
	 *            String
	 * @return List<C2CBatchMasterVO> list
	 * @throws BTSLBaseException
	 *            
	 */
	public List<C2CBatchMasterVO> loadBatchDetailsListByBatchId(Connection p_con, String p_batchId)
		throws BTSLBaseException {
		
			final String methodName = "loadBatchDetailsListByBatchId";
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Entered p_batchId=" + p_batchId);
			}
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			final String sqlSelect = c2CBatchTransferWebQry
					.loadBatchDetailsByBatchIdQry();
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
			}
			C2CBatchMasterVO c2cBatchMasterVO = null;
			C2CBatchItemsVO c2cBatchItemsVO = null;
			final List<C2CBatchMasterVO> list = new ArrayList();
			try {
				pstmt = p_con.prepareStatement(sqlSelect);
				int i = 0;
				++i;
				pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
				++i;
				pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
				++i;
				pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
				++i;
				pstmt.setString(i, p_batchId);
				
				rs = pstmt.executeQuery();
				while (rs.next()) {
					c2cBatchMasterVO = new C2CBatchMasterVO();
					c2cBatchMasterVO.setBatchId(rs.getString("batch_id"));
					c2cBatchMasterVO.setBatchName(rs.getString("batch_name"));
					c2cBatchMasterVO.setProductCode(rs.getString("product_code"));
					c2cBatchMasterVO.setProductName(rs.getString("product_name"));
					c2cBatchMasterVO.setNewRecords(rs.getInt("new"));
					c2cBatchMasterVO.setClosedRecords(rs.getInt("close"));
					c2cBatchMasterVO.setRejectedRecords(rs.getInt("cncl"));
					c2cBatchMasterVO.setBatchTotalRecord(rs
							.getInt("batch_total_record"));
					c2cBatchMasterVO.setBatchDate(rs.getDate("batch_date"));
					c2cBatchMasterVO.setBatchDateStr(BTSLUtil.getDateTimeStringFromDate(c2cBatchMasterVO.getBatchDate()));
					if (c2cBatchMasterVO.getBatchDate() != null) {
						c2cBatchMasterVO.setBatchDateStr(BTSLUtil.getDateTimeStringFromDate(c2cBatchMasterVO.getBatchDate()));
					}

					list.add(c2cBatchMasterVO);
				}
			} catch (SQLException sqe) {
				LOG.error(methodName, "SQLException : " + sqe);
				LOG.errorTrace(methodName, sqe);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
						EventStatusI.RAISED, EventLevelI.FATAL,
						"C2CBatchTransferWebDAO[loadBatchDetailsList]", "", "", "",
						"SQL Exception:" + sqe.getMessage());
				throw new BTSLBaseException(this, methodName,
						"error.general.sql.processing");
			} catch (Exception ex) {
				LOG.error(methodName, "Exception : " + ex);
				LOG.errorTrace(methodName, ex);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
						EventStatusI.RAISED, EventLevelI.FATAL,
						"C2CBatchTransferWebDAO[loadBatchDetailsList]", "", "", "",
						"Exception:" + ex.getMessage());
				throw new BTSLBaseException(this, methodName,
						"error.general.processing");
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
				} catch (Exception e) {
					LOG.errorTrace(methodName, e);
				}
				try {
					if (pstmt != null) {
						pstmt.close();
					}
				} catch (Exception e) {
					LOG.errorTrace(methodName, e);
				}
				if (LOG.isDebugEnabled()) {
					LOG.debug(
							methodName,
							"Exiting: loadBatchDetailsList  list.size()="
									+ list.size());
				}
			}
			return list;		
		
	}
	
	/**
	 * This method load Batch details according to batch id.
	 * 
	 * loadBatchDetailsListByAdvance
	 * 
	 * @param p_con
	 *            Connection
	 * @param p_fromDate
	 *            Date
	 *            
	 *  @param p_toDate 
	 *            Date
	 *  @param domainCode
	 *            String    
	 *  @param createdUserId
	 *            String 
	 *  @param  categoryCode
	 *            String       
	 *  
	 *            
	 * @return List<C2CBatchMasterVO> list
	 * @throws BTSLBaseException
	 *            
	 */
	public List<C2CBatchMasterVO> loadBatchDetailsListByAdvance(Connection p_con, Date p_fromDate, 
			Date p_toDate,String domainCode,String createdUserId,String categoryCode, String loggedinUserId) throws BTSLBaseException {
		final String methodName = "loadBatchDetailsListByAdvance";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered from date :"
					+ ""+ p_fromDate +"to date: "+p_toDate+"  domaincode :"+domainCode+" createdUserId"+createdUserId + " "
							+ " categoryCode"+categoryCode+" ");
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sqlSelect = c2CBatchTransferWebQry
				.loadBatchDetailsByAdvancedQry();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		}
		final List<C2CBatchMasterVO> list = new ArrayList();
		try {
			pstmt = p_con.prepareStatement(sqlSelect);
			int i = 0;
			++i;
			pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
			++i;
			pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
			++i;
			pstmt.setString(i, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
			if (LOG.isDebugEnabled()) {
				LOG.debug(
						methodName,
						"QUERY BTSLUtil.getSQLDateFromUtilDate(p_fromDate)="
								+ BTSLUtil.getSQLDateFromUtilDate(p_toDate)
								+ " BTSLUtil.getSQLDateFromUtilDate(p_fromDate)="
								+ BTSLUtil.getSQLDateFromUtilDate(p_toDate));
			
		    }
			++i;
			pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
			++i;
			pstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
			++i;
			pstmt.setString(i, domainCode);
			++i;
			pstmt.setString(i, loggedinUserId);
			++i;
			pstmt.setString(i, categoryCode);
			++i;
			pstmt.setString(i, createdUserId);
			rs = pstmt.executeQuery();
			C2CBatchMasterVO c2cBatchMasterVO = null;
			while (rs.next()) {
				c2cBatchMasterVO = new C2CBatchMasterVO();
				c2cBatchMasterVO.setBatchId(rs.getString("batch_id"));
				c2cBatchMasterVO.setDomainCode(rs.getString("domain_code"));
				c2cBatchMasterVO.setBatchName(rs.getString("batch_name"));
				c2cBatchMasterVO.setProductName(rs.getString("product_name"));
				c2cBatchMasterVO.setProductCode(rs.getString("product_code"));
				c2cBatchMasterVO.setProductMrp(rs.getLong("unit_value"));
				c2cBatchMasterVO.setProductMrpStr(PretupsBL.getDisplayAmount(rs
						.getLong("unit_value")));
				c2cBatchMasterVO.setBatchTotalRecord(rs
						.getInt("batch_total_record"));
				c2cBatchMasterVO.setNewRecords(rs.getInt("new"));

				c2cBatchMasterVO.setClosedRecords(rs.getInt("close"));
				c2cBatchMasterVO.setRejectedRecords(rs.getInt("cncl"));
				c2cBatchMasterVO.setBatchDate(rs.getDate("batch_date"));
				if (c2cBatchMasterVO.getBatchDate() != null) {
					c2cBatchMasterVO.setBatchDateStr(BTSLUtil.getDateTimeStringFromDate(c2cBatchMasterVO.getBatchDate()));
				}
				list.add(c2cBatchMasterVO);
			}
		} catch (SQLException sqe) {
			LOG.error(methodName, "SQLException : " + sqe);
			LOG.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2CBatchTransferWebDAO[loadBatchDetailsListByAdvance]", "",
					"", "", "SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		} catch (Exception ex) {
			LOG.error(methodName, "Exception : " + ex);
			LOG.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"C2CBatchTransferWebDAO[loadBatchDetailsListByAdvance]", "",
					"", "", "Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting: c2cBatchMasterVOList size="
						+ list.size());
			}
		}
		return list;
	}
}

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
import java.util.Locale;
import java.util.Map;

import com.btsl.util.MessageResources;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
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
import com.btsl.pretups.channel.transfer.businesslogic.ChannelSoSWithdrawBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchMasterVO;
import com.btsl.pretups.channel.transfer.businesslogic.FocListValueVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.BatchFocFileProcessLog;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockBL;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnItemsVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserLoanWithdrawBL;

public class FOCBatchTransferWebDAO {

    /**
     * 
     */
	private FOCBatchTransferWebQry focBatchTransferWebQry;
    public FOCBatchTransferWebDAO() {
        super();
        focBatchTransferWebQry = (FOCBatchTransferWebQry)ObjectProducer.getObject(QueryConstants.FOC_BATCHTRANSFER_WEB_QRY, QueryConstants.QUERY_PRODUCER);
        // TODO Auto-generated constructor stub
    }

    /**
     * Field _log.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method for loading FOCBatch details..
     * This method will load the batches that are within the geography of user
     * whose userId is passed
     * with status(OPEN) also in items table for corresponding master record the
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
    public ArrayList loadBatchFOCMasterDetails(Connection p_con, String p_userID, String p_itemStatus, String p_currentLevel) throws BTSLBaseException {
        final String methodName = "loadBatchFOCMasterDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_userID=" + p_userID + " p_itemStatus=" + p_itemStatus + " p_currentLevel=" + p_currentLevel);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final String sqlSelect = focBatchTransferWebQry.loadBatchFOCMasterDetailsQry(p_itemStatus, p_currentLevel); 
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
            pstmt.setString(2, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
            pstmt.setString(3, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
            pstmt.setString(4, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
            pstmt.setString(5, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
            pstmt.setString(6, p_userID);
            pstmt.setString(7, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_OPEN);
            pstmt.setString(8, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_ITEM_RCRDSTATUS_PROCESSED);
            pstmt.setString(9, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
            pstmt.setString(10, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
            pstmt.setString(11, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
            pstmt.setString(12, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
            pstmt.setString(13, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);

            rs = pstmt.executeQuery();
            FOCBatchMasterVO focBatchMasterVO = null;
            while (rs.next()) {
                focBatchMasterVO = new FOCBatchMasterVO();
                focBatchMasterVO.setBatchId(rs.getString("batch_id"));
                focBatchMasterVO.setBatchName(rs.getString("batch_name"));
                focBatchMasterVO.setProductName(rs.getString("product_name"));
                focBatchMasterVO.setProductMrp(rs.getLong("unit_value"));
                focBatchMasterVO.setProductMrpStr(PretupsBL.getDisplayAmount(rs.getLong("unit_value")));
                focBatchMasterVO.setBatchTotalRecord(rs.getInt("batch_total_record"));
                focBatchMasterVO.setNewRecords(rs.getInt("new"));
                focBatchMasterVO.setLevel1ApprovedRecords(rs.getInt("appr1"));
                focBatchMasterVO.setLevel2ApprovedRecords(rs.getInt("appr2"));
                focBatchMasterVO.setClosedRecords(rs.getInt("closed"));
                focBatchMasterVO.setRejectedRecords(rs.getInt("cncl"));
                focBatchMasterVO.setNetworkCode(rs.getString("network_code"));
                focBatchMasterVO.setNetworkCodeFor(rs.getString("network_code_for"));
                focBatchMasterVO.setProductCode(rs.getString("product_code"));
                focBatchMasterVO.setModifiedBy(rs.getString("modified_by"));
                focBatchMasterVO.setModifiedOn(rs.getTimestamp("modified_on"));
                focBatchMasterVO.setProductType(rs.getString("product_type"));
                focBatchMasterVO.setProductShortName(rs.getString("short_name"));
                focBatchMasterVO.setDomainCode(rs.getString("domain_code"));
                focBatchMasterVO.setBatchDate(rs.getDate("batch_date"));
                focBatchMasterVO.setDefaultLang(rs.getString("sms_default_lang"));
                focBatchMasterVO.setSecondLang(rs.getString("sms_second_lang"));
                focBatchMasterVO.setCreatedBy(rs.getString("created_by"));
                focBatchMasterVO.setCategoryCode(rs.getString("category_code"));
                focBatchMasterVO.setWallet_type(rs.getString("txn_Wallet"));
                list.add(focBatchMasterVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[loadBatchFOCMasterDetails]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[loadBatchFOCMasterDetails]",
                "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: focBatchMasterVOList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method to cancel/approve the batch. This also perform all the data
     * validation.
     * Also construct error list
     * Tables updated are: foc_batch_items,foc_batches
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
    public ArrayList processOrderByBatch(Connection p_con, LinkedHashMap p_dataMap, String p_currentLevel, String p_userID, MessageResources p_messages, Locale p_locale, String p_sms_default_lang, String p_sms_second_lang) throws BTSLBaseException {
        final String methodName = "processOrderByBatch";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_dataMap = " + p_dataMap + ", p_currentLevel = " + p_currentLevel + ", p_locale = " + p_locale + ", p_userID = " + p_userID);
        }
        Boolean isExternalTxnUnique = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE);
        PreparedStatement pstmtLoadUser = null;
        // commented for DB2
        // OraclePreparedStatement psmtCancelFOCBatchItem = null;
        // OraclePreparedStatement psmtAppr1FOCBatchItem = null;
        // OraclePreparedStatement psmtAppr2FOCBatchItem = null;
        // OraclePreparedStatement pstmtUpdateMaster= null;
        PreparedStatement psmtCancelFOCBatchItem = null;
        PreparedStatement psmtAppr1FOCBatchItem = null;
        PreparedStatement psmtAppr2FOCBatchItem = null;
        PreparedStatement pstmtUpdateMaster = null;

        PreparedStatement pstmtSelectItemsDetails = null;
        // PreparedStatement pstmtUpdateMaster= null;
        PreparedStatement pstmtIsModified = null;
        PreparedStatement pstmtIsTxnNumExists1 = null;
        PreparedStatement pstmtIsTxnNumExists2 = null;
        ArrayList errorList = null;
        ListValueVO errorVO = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        ResultSet rs4 = null;
        int updateCount = 0;
        String batch_ID = null;
        /*
         * The query below will be used to load user datils.
         * That details is the validated for eg: transfer profile, commission
         * profile, user status etc.
         */
        StringBuffer sqlBuffer = new StringBuffer(" SELECT u.status userstatus, cusers.in_suspend, ");
        sqlBuffer.append("cps.status commprofilestatus,tp.status profile_status,cps.language_1_message comprf_lang_1_msg, ");
        sqlBuffer.append("cps.language_2_message comprf_lang_2_msg, u.category_code,u.network_code ");
        sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps,transfer_profile tp ");
        sqlBuffer.append("WHERE u.user_id = ? AND u.status <> 'N' AND u.status <> 'C' ");
        sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
        sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
        sqlBuffer.append(" tp.profile_id = cusers.transfer_profile_id  ");
        final String sqlLoadUser = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlLoadUser=" + sqlLoadUser);
        }
        sqlBuffer = null;
        // after validating if request is to cancle the order, the below query
        // is used.
        sqlBuffer = new StringBuffer(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" modified_by = ?, modified_on = ?,  ");
        if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentLevel)) {
            sqlBuffer.append(" first_approver_remarks = ?, ");
        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentLevel)) {
            sqlBuffer.append(" second_approver_remarks = ?, ");
        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentLevel)) {
            sqlBuffer.append(" third_approver_remarks = ?, ");
        }
        sqlBuffer.append(" cancelled_by = ?, ");
        sqlBuffer.append(" cancelled_on = ?, status = ?");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        if (!PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentLevel)) {
            sqlBuffer.append(" AND status IN (? , ? )  ");
        } else {
            sqlBuffer.append(" AND status  = ?   ");
        }
        final String sqlCancelFOCBatchItems = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlCancelFOCBatchItems=" + sqlCancelFOCBatchItems);
        }
        sqlBuffer = null;

        // after validating if request is of level 1 approve the order, the
        // below query is used.
        sqlBuffer = new StringBuffer(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" first_approver_remarks = ?, ");
        sqlBuffer.append(" first_approved_by=?, first_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status IN (? , ? )  ");

        final String sqlApprv1FOCBatchItems = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlApprv1FOCBatchItems=" + sqlApprv1FOCBatchItems);
        }
        sqlBuffer = null;

        // after validating if request is of level 2 approve the order, the
        // below query is used.
        sqlBuffer = new StringBuffer(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" second_approver_remarks = ?, ");
        sqlBuffer.append(" second_approved_by=? , second_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status IN (? , ? )  ");
        final String sqlApprv2FOCBatchItems = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlApprv2FOCBatchItems=" + sqlApprv2FOCBatchItems);
        }
        sqlBuffer = null;

        // Afetr all teh records are processed the the below query is used to
        // load the various counts such as new ,
        // apprv1, close ,cancled etc. These couts will be used to deceide what
        // status to be updated in mater table
       // FOCBatchTransferWebQry focBatchTransferWebQry = (FOCBatchTransferWebQry)ObjectProducer.getObject(QueryConstants.FOC_BATCHTRANSFER_WEB_QRY, QueryConstants.QUERY_PRODUCER);
        final String selectItemsDetails = focBatchTransferWebQry.processOrderByBatchQry(); 
        

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY selectItemsDetails=" + selectItemsDetails);
        }
        sqlBuffer = null;

        // The query below is used to update the master table after all items
        // are processed
        sqlBuffer = new StringBuffer("UPDATE foc_batches SET status=? , modified_by=? ,modified_on=?,sms_default_lang=? , sms_second_lang=?  ");
        sqlBuffer.append(" WHERE batch_id=? AND status=? ");
        final String updateFOCBatches = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY updateFOCBatches=" + updateFOCBatches);
        }
        sqlBuffer = null;

        // The query below is used to check if the record is modified or not
        sqlBuffer = new StringBuffer("SELECT modified_on FROM foc_batch_items ");
        sqlBuffer.append("WHERE batch_detail_id = ? ");
        final String isModified = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY isModified=" + isModified);
        }
        sqlBuffer = null;

        // The below query will be exceute if external txn number unique is "Y"
        // in system preferences
        // This will check the existence of external txn number in
        // foc_batch_items table
        sqlBuffer = new StringBuffer(" SELECT 1 FROM foc_batch_items ");
        sqlBuffer.append("WHERE ext_txn_no=? AND status <> ? AND batch_detail_id<>? ");
        final String isExistsTxnNum1 = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY isExistsTxnNum1=" + isExistsTxnNum1);
        }
        sqlBuffer = null;

        // The below query will be exceute if external txn number unique is "Y"
        // in system preferences
        // and external txn number is not exists in foc_batch_items table.
        // This will check the existence of external txn number in
        // channel_transfers table
        sqlBuffer = new StringBuffer("  SELECT 1 FROM channel_transfers ");
        sqlBuffer.append("WHERE type=? AND ext_txn_no=? AND status <> ? ");
        final String isExistsTxnNum2 = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY isExistsTxnNum2=" + isExistsTxnNum2);
        }
        sqlBuffer = null;
        Date date = null;
        try {
            FOCBatchItemsVO focBatchItemVO = null;
            ChannelUserVO channelUserVO = null;
            date = new Date();
            // Create the prepared statements
            pstmtLoadUser = p_con.prepareStatement(sqlLoadUser);
            // commented for DB2
            // psmtCancelFOCBatchItem=(OraclePreparedStatement)p_con.prepareStatement(sqlCancelFOCBatchItems);
            // psmtAppr1FOCBatchItem=(OraclePreparedStatement)p_con.prepareStatement(sqlApprv1FOCBatchItems);
            // psmtAppr2FOCBatchItem=(OraclePreparedStatement)p_con.prepareStatement(sqlApprv2FOCBatchItems);
            psmtCancelFOCBatchItem = p_con.prepareStatement(sqlCancelFOCBatchItems);
            psmtAppr1FOCBatchItem = p_con.prepareStatement(sqlApprv1FOCBatchItems);
            psmtAppr2FOCBatchItem = p_con.prepareStatement(sqlApprv2FOCBatchItems);
            pstmtSelectItemsDetails = p_con.prepareStatement(selectItemsDetails);
            // commented for DB2
            // pstmtUpdateMaster=(OraclePreparedStatement)p_con.prepareStatement(updateFOCBatches);
            pstmtUpdateMaster = p_con.prepareStatement(updateFOCBatches);
            pstmtIsModified = p_con.prepareStatement(isModified);
            pstmtIsTxnNumExists1 = p_con.prepareStatement(isExistsTxnNum1);
            pstmtIsTxnNumExists2 = p_con.prepareStatement(isExistsTxnNum2);
            errorList = new ArrayList();
            final Iterator iterator = p_dataMap.keySet().iterator();
            String key = null;
            int m = 0;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                focBatchItemVO = (FOCBatchItemsVO) p_dataMap.get(key);
                if (BTSLUtil.isNullString(batch_ID)) {
                    batch_ID = focBatchItemVO.getBatchId();
                }
                pstmtLoadUser.clearParameters();
                m = 0;
                ++m;
                pstmtLoadUser.setString(m, focBatchItemVO.getUserId());
                rs = pstmtLoadUser.executeQuery();
                if (rs.next())// check data found or not
                {
                    channelUserVO =  ChannelUserVO.getInstance();
                    channelUserVO.setUserID(focBatchItemVO.getUserId());
                    channelUserVO.setStatus(rs.getString("userstatus"));
                    channelUserVO.setInSuspend(rs.getString("in_suspend"));
                    channelUserVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
                    channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                    channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                    channelUserVO.setTransferProfileStatus(rs.getString("profile_status"));
                    channelUserVO.setNetworkCode(rs.getString("network_code"));
                    channelUserVO.setCategoryCode(rs.getString("category_code"));
                    // (User status is checked) if this condition is true then
                    // made entry in logs and leave this data.

                    // user life cycle
                    boolean receiverStatusAllowed = false;
                    final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(channelUserVO.getNetworkCode(), channelUserVO.getCategoryCode(),
                        PretupsI.USER_TYPE_CHANNEL, PretupsI.REQUEST_SOURCE_TYPE_WEB);
                    if (userStatusVO != null) {
                        final String userStatusAllowed = userStatusVO.getUserReceiverAllowed();
                        final String status[] = userStatusAllowed.split(",");
                        for (int i = 0; i < status.length; i++) {
                            if (status[i].equals(channelUserVO.getStatus())) {
                                receiverStatusAllowed = true;
                            }
                        }
                    } else {
                        throw new BTSLBaseException(this, "processOrderByBatch", "error.status.processing");
                    }
                    if (!receiverStatusAllowed) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.usersuspend"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.focBatchItemLog(methodName, focBatchItemVO, "FAIL : User is not active", "Approval level" + p_currentLevel);
                        continue;
                    }
                    // (commission profile status is checked) if this condition
                    // is true then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.comprofsuspend"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.focBatchItemLog(methodName, focBatchItemVO, "FAIL : Commission profile is suspend", "Approval level" + p_currentLevel);
                        continue;
                    }
                    // (tranmsfer profile status is checked) if this condition
                    // is true then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.trfprofsuspend"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.focBatchItemLog(methodName, focBatchItemVO, "FAIL : Transfer profile is suspend", "Approval level" + p_currentLevel);
                        continue;
                    }
                    // (user in suspend is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.userinsuspend"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.focBatchItemLog(methodName, focBatchItemVO, "FAIL : User is IN suspend", "Approval level" + p_currentLevel);
                        continue;
                    }
                }
                // (record not found for user) if this condition is true then
                // made entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.nouser"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.focBatchItemLog(methodName, focBatchItemVO, "FAIL : User not found", "Approval level" + p_currentLevel);
                    continue;

                }
                pstmtIsModified.clearParameters();
                m = 0;
                ++m;
                pstmtIsModified.setString(m, focBatchItemVO.getBatchDetailId());
                rs1 = pstmtIsModified.executeQuery();
                java.sql.Timestamp newlastModified = null;
                if (rs1.next()) {
                    newlastModified = rs1.getTimestamp("modified_on");
                }
                // (record not found means it is modified) if this condition is
                // true then made entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog
                        .focBatchItemLog(methodName, focBatchItemVO, "FAIL : Record is already modified by some one else", "Approval level" + p_currentLevel);
                    continue;
                }
                // if this condition is true then made entry in logs and leave
                // this data.
                if (newlastModified.getTime() != BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getModifiedOn()).getTime()) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog
                        .focBatchItemLog(methodName, focBatchItemVO, "FAIL : Record is already modified by some one else", "Approval level" + p_currentLevel);
                    continue;

                }
                // (external txn number is checked) if this condition is true
                // then made entry in logs and leave this data.
                if (isExternalTxnUnique && !BTSLUtil.isNullString(focBatchItemVO.getExtTxnNo()) && !PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL
                    .equals(focBatchItemVO.getStatus())) {
                    // check in foc_batch-item table
                    pstmtIsTxnNumExists1.clearParameters();
                    m = 0;
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, focBatchItemVO.getBatchDetailId());
                    rs2 = pstmtIsTxnNumExists1.executeQuery();
                    // (external txn number is checked) if this condition is
                    // true then made entry in logs and leave this data.
                    if (rs2.next()) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.focBatchItemLog(methodName, focBatchItemVO, "FAIL : External transaction number already exists BATCH FOC",
                            "Approval level" + p_currentLevel);
                        continue;
                    }
                    // check in channel_transfers table
                    pstmtIsTxnNumExists2.clearParameters();
                    m = 0;
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, PretupsI.CHANNEL_TYPE_O2C);
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    rs3 = pstmtIsTxnNumExists2.executeQuery();
                    // (external txn number is checked) if this condition is
                    // true then made entry in logs and leave this data.
                    if (rs3.next()) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.focBatchItemLog(methodName, focBatchItemVO, "FAIL : External transaction number already exists CHANNEL TRANSFER",
                            "Approval level" + p_currentLevel);
                        continue;
                    }
                }
                // If operation is of cancle then set the fiels in
                // psmtCancelFOCBatchItem
                if (PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL.equals(focBatchItemVO.getStatus())) {
                    psmtCancelFOCBatchItem.clearParameters();
                    m = 0;
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtCancelFOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentLevel)) {
                        // psmtCancelFOCBatchItem.setFormOfUse(++m,
                        // OraclePreparedStatement.FORM_NCHAR);//commented for
                        // DB2
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, focBatchItemVO.getFirstApproverRemarks());
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentLevel)) {
                        // psmtCancelFOCBatchItem.setFormOfUse(++m,
                        // OraclePreparedStatement.FORM_NCHAR);//commented for
                        // DB2
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, focBatchItemVO.getSecondApproverRemarks());
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentLevel)) {
                        // psmtCancelFOCBatchItem.setFormOfUse(++m,
                        // OraclePreparedStatement.FORM_NCHAR);//commented for
                        // DB2
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, focBatchItemVO.getThirdApproverRemarks());
                    }
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtCancelFOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, focBatchItemVO.getStatus());
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, focBatchItemVO.getBatchDetailId());
                    if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentLevel)) {
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentLevel)) {
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentLevel)) {
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    }
                    updateCount = psmtCancelFOCBatchItem.executeUpdate();
                }
                // IF approval 1 is the operation then set parametrs in
                // psmtAppr1FOCBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(focBatchItemVO.getStatus())) {
                    psmtAppr1FOCBatchItem.clearParameters();
                    m = 0;
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    // psmtAppr1FOCBatchItem.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR);//commented for DB2
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getFirstApproverRemarks());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getStatus());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    updateCount = psmtAppr1FOCBatchItem.executeUpdate();
                }
                // IF approval 2 is the operation then set parametrs in
                // psmtAppr2FOCBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(focBatchItemVO.getStatus())) {
                    psmtAppr2FOCBatchItem.clearParameters();
                    m = 0;
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    // psmtAppr2FOCBatchItem.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR);//commented for DB2
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getSecondApproverRemarks());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getStatus());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    updateCount = psmtAppr2FOCBatchItem.executeUpdate();
                }
                // If update count is <=0 that means record not updated in db
                // properly so made entry in logs and leave this data
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.focBatchItemLog(methodName, focBatchItemVO, "FAIL : DB Error while updating items table",
                        "Approval level" + p_currentLevel + ", updateCount=" + updateCount);
                    continue;
                }
                // commit the transaction after processiong each record
                p_con.commit();
            }// end of while
             // Check the status to be updated in master table agfter processing
             // of all records

        }// end of try
        catch (SQLException sqe) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[processOrderByBatch]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            BatchFocFileProcessLog.focBatchItemLog(methodName, null, "FAIL : updating batch items SQL Exception:" + sqe.getMessage(),
                "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[processOrderByBatch]", "", "",
                "", "Exception:" + ex.getMessage());
            BatchFocFileProcessLog.focBatchItemLog(methodName, null, "FAIL : updating batch items Exception:" + ex.getMessage(),
                "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
        	try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
        	try {
                if (rs2 != null) {
                    rs2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
        	try {
                if (rs3 != null) {
                    rs3.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtLoadUser != null) {
                    pstmtLoadUser.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtCancelFOCBatchItem != null) {
                    psmtCancelFOCBatchItem.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr1FOCBatchItem != null) {
                    psmtAppr1FOCBatchItem.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr2FOCBatchItem != null) {
                    psmtAppr2FOCBatchItem.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsModified != null) {
                    pstmtIsModified.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsTxnNumExists1 != null) {
                    pstmtIsTxnNumExists1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsTxnNumExists2 != null) {
                    pstmtIsTxnNumExists2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                int m = 0;
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
                ++m;
                pstmtSelectItemsDetails.setString(m, batch_ID);
                rs4 = pstmtSelectItemsDetails.executeQuery();
                if (rs4.next()) {
                    final int totalCount = rs4.getInt("batch_total_record");
                    final int closeCount = rs4.getInt("closed");
                    final int cnclCount = rs4.getInt("cncl");
                    String statusOfMaster = null;
                    // If all records are canle then set cancelled in master
                    // table
                    if (totalCount == cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_CANCEL;
                    } else if (totalCount == closeCount + cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_CLOSE;
                        // Otherwise set OPEN in mastrer table
                    } else {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_OPEN;
                    }
                    m = 0;
                    ++m;
                    pstmtUpdateMaster.setString(m, statusOfMaster);
                    ++m;
                    pstmtUpdateMaster.setString(m, p_userID);
                    ++m;
                    pstmtUpdateMaster.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));

                    // pstmtUpdateMaster.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR);//commented for DB2
                    ++m;
                    pstmtUpdateMaster.setString(m, p_sms_default_lang);
                    // pstmtUpdateMaster.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR);//commented for DB2
                    ++m;
                    pstmtUpdateMaster.setString(m, p_sms_second_lang);
                    ++m;
                    pstmtUpdateMaster.setString(m, batch_ID);
                    ++m;
                    pstmtUpdateMaster.setString(m, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_UNDERPROCESS);

                    updateCount = pstmtUpdateMaster.executeUpdate();
                    if (updateCount <= 0) {
                        p_con.rollback();
                        errorVO = new ListValueVO("", "", p_messages.getMessage(p_locale, "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.focBatchItemLog(methodName, null, "FAIL : DB Error while updating master table",
                            "Approval level" + p_currentLevel + ", updateCount=" + updateCount);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "FOCBatchTransferWebDAO[processOrderByBatch]", "", "", "", "Error while updating FOC_BATCHES table. Batch id=" + batch_ID);
                    }// end of if
                }// end of if
                p_con.commit();
            } catch (SQLException sqe) {
                try {
                    if (p_con != null) {
                        p_con.rollback();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                _log.error(methodName, "SQLException : " + sqe);
                _log.errorTrace(methodName, sqe);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[processOrderByBatch]", "",
                    "", "", "SQL Exception:" + sqe.getMessage());
                BatchFocFileProcessLog.focBatchItemLog(methodName, null, "FAIL : updating batch master SQL Exception:" + sqe.getMessage(),
                    "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
                // throw new BTSLBaseException(this, methodName,
                // "error.general.sql.processing");
            } catch (Exception ex) {
                try {
                    if (p_con != null) {
                        p_con.rollback();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                _log.error(methodName, "Exception : " + ex);
                _log.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[processOrderByBatch]", "",
                    "", "", "Exception:" + ex.getMessage());
                BatchFocFileProcessLog.focBatchItemLog(methodName, null, "FAIL : updating batch master Exception:" + ex.getMessage(),
                    "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
                // throw new BTSLBaseException(this, methodName,
                // "error.general.processing");
            }finally{
            	try {
                    if (rs4 != null) {
                        rs4.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                try {
                    if (pstmtSelectItemsDetails != null) {
                        pstmtSelectItemsDetails.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                try {
                    if (pstmtUpdateMaster != null) {
                        pstmtUpdateMaster.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
            }
            

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: errorList size=" + errorList.size());
            }
        }
        return errorList;
    }

    /**
     * Method to close the foc order by batch. This also perform all the data
     * validation.
     * Also construct error list
     * Tables updated are:
     * network_stocks,network_daily_stocks,network_stock_transactions
     * ,network_stock_trans_items
     * user_balances,user_daily_balances,user_transfer_counts,foc_batch_items,
     * foc_batches,
     * channel_transfers_items,channel_transfers
     * 
     * @param p_con
     * @param p_dataMap
     * @param p_currentLevel
     * @param p_userID
     * @param p_focBatchMatserVO
     * @param p_messages
     * @param p_locale
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList closeOrderByBatch(Connection p_con, LinkedHashMap p_dataMap, String p_currentLevel, String p_userID, FOCBatchMasterVO p_focBatchMatserVO, MessageResources p_messages, Locale p_locale, String p_sms_default_lang, String p_sms_second_lang) throws BTSLBaseException {
        final String methodName = "closeOrderByBatch";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_dataMap=" + p_dataMap + " p_currentLevel=" + p_currentLevel + " p_locale=" + p_locale);
        }
        String defaultWebGatewayCode = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WEB_GATEWAY_CODE);
        Boolean isExternalTxnUnique = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE);
        Boolean isFOCSmsNotify = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.FOC_SMS_NOTIFY);
        Boolean isMultipleWalletApply = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY);
        String txnReceiverUserStatusChang = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_RECEIVER_USER_STATUS_CHANG));
        PreparedStatement pstmtLoadUser = null;
        PreparedStatement pstmtLoadNetworkStock = null;
        PreparedStatement pstmtUpdateNetworkStock = null;
        PreparedStatement pstmtInsertNetworkDailyStock = null;
        PreparedStatement pstmtSelectNetworkStock = null;
        PreparedStatement pstmtupdateSelectedNetworkStock = null;
        // OraclePreparedStatement
        // pstmtInsertNetworkStockTransaction=null;//commented for DB2
        PreparedStatement pstmtInsertNetworkStockTransaction = null;
        PreparedStatement pstmtInsertNetworkStockTransactionItem = null;
        PreparedStatement pstmtSelectUserBalances = null;
        PreparedStatement pstmtUpdateUserBalances = null;
        PreparedStatement pstmtInsertUserDailyBalances = null;
        PreparedStatement pstmtSelectBalance = null;
        PreparedStatement pstmtUpdateBalance = null;
        PreparedStatement pstmtInsertBalance = null;
        PreparedStatement pstmtSelectTransferCounts = null;
        PreparedStatement pstmtSelectProfileCounts = null;
        PreparedStatement pstmtUpdateTransferCounts = null;
        PreparedStatement pstmtInsertTransferCounts = null;
        // OraclePreparedStatement psmtAppr1FOCBatchItem = null;//commented for
        // DB2
        // OraclePreparedStatement psmtAppr2FOCBatchItem = null;//commented for
        // DB2
        // OraclePreparedStatement psmtAppr3FOCBatchItem = null;//commented for
        // DB2
        PreparedStatement psmtAppr1FOCBatchItem = null;
        PreparedStatement psmtAppr2FOCBatchItem = null;
        PreparedStatement psmtAppr3FOCBatchItem = null;
        PreparedStatement pstmtSelectItemsDetails = null;
        // PreparedStatement pstmtUpdateMaster= null;
        // OraclePreparedStatement pstmtUpdateMaster= null;//commented for DB2
        PreparedStatement pstmtUpdateMaster = null;
        PreparedStatement pstmtIsModified = null;
        PreparedStatement pstmtLoadTransferProfileProduct = null;
        PreparedStatement handlerStmt = null;
        PreparedStatement pstmtIsTxnNumExists1 = null;
        PreparedStatement pstmtIsTxnNumExists2 = null;
        PreparedStatement pstmtInsertIntoChannelTransferItems = null;
        // OraclePreparedStatement
        // pstmtInsertIntoChannelTranfers=null;//commented for DB2
        PreparedStatement pstmtInsertIntoChannelTranfers = null;
        PreparedStatement pstmtSelectBalanceInfoForMessage = null;
        ArrayList userbalanceList = null;
        UserBalancesVO balancesVO = null;
        ArrayList errorList = null;
        ListValueVO errorVO = null;
        ResultSet rs = null;
        String language = null;
        String country = null;
        KeyArgumentVO keyArgumentVO = null;
        String[] argsArr = null;
        ArrayList txnSmsMessageList = null;
        ArrayList balSmsMessageList = null;
        Locale locale = null;
        String[] array = null;
        BTSLMessages messages = null;
        PushMessage pushMessage = null;
        int updateCount = 0;
        String o2cTransferID = null;
        PreparedStatement psmtInsertUserThreshold = null;
        long thresholdValue = -1;
        OperatorUtilI operatorUtili = null;
        String m_receiverStatusAllowed[] = null;
        try {
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("closeOrderByBatch", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[closeOrderByBatch]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
        // user life cycle
        String receiverStatusAllowed = null;
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_focBatchMatserVO.getNetworkCode(), p_focBatchMatserVO.getCategoryCode(),
            PretupsI.USER_TYPE_CHANNEL, PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            receiverStatusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
        } else {
            throw new BTSLBaseException(this, "closeOrderByBatch", "error.status.processing");
        }
        final String receiverStatusAllowed1 = receiverStatusAllowed.replaceAll("'", "");
        final String sa = receiverStatusAllowed1.replaceAll("\" ", "");
        m_receiverStatusAllowed = sa.split(",");

        /*
         * The query below will be used to load user datils.
         * That details is the validated for eg: transfer profile, commission
         * profile, user status etc.
         */
        StringBuffer sqlBuffer = new StringBuffer(" SELECT u.status userstatus, cusers.in_suspend, cusers.transfer_profile_id, ");
        sqlBuffer.append("cps.status commprofilestatus,tp.status profile_status,cps.language_1_message comprf_lang_1_msg, ");
        sqlBuffer.append("cps.language_2_message comprf_lang_2_msg,up.phone_language,up.country, ug.grph_domain_code ");
        sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps,transfer_profile tp, user_phones up,user_geographies ug ");
        sqlBuffer.append("WHERE u.user_id = ? AND up.user_id=u.user_id AND up.primary_number = 'Y' ");
        sqlBuffer.append(" AND u.status IN (");
        for (int i = 0; i < m_receiverStatusAllowed.length; i++) {
            sqlBuffer.append(" ?");
            if (i != m_receiverStatusAllowed.length - 1) {
                sqlBuffer.append(",");
            }
        }
        sqlBuffer.append(")");
        sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
        sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
        sqlBuffer.append(" tp.profile_id = cusers.transfer_profile_id AND ug.user_id = u.user_id ");
        final String sqlLoadUser = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlLoadUser=" + sqlLoadUser);
        }
        sqlBuffer = null;

        // The query below is used to load the network stock details for network
        // in between sender and receiver
        // This table will basically used to update the daily_stock_updated_on
        // and also to know how many
        // records are to be insert in network_daily_stocks

        
        final String sqlLoadNetworkStock = focBatchTransferWebQry.closeOrderByBatchQry();
        
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlLoadNetworkStock=" + sqlLoadNetworkStock);
        }
        sqlBuffer = null;

        // Update daily_stock_updated_on with current date
        sqlBuffer = new StringBuffer("UPDATE network_stocks SET daily_stock_updated_on = ? ");
        sqlBuffer.append("WHERE network_code = ? AND network_code_for = ? AND wallet_type = ?");
        final String sqlUpdateNetworkStock = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlUpdateNetworkStock=" + sqlUpdateNetworkStock);
        }
        sqlBuffer = null;

        // Executed if day difference in last updated date and current date is
        // greater then or equal to 1
        // Insert number of records equal to day difference in last updated date
        // and current date in network_daily_stocks
        sqlBuffer = new StringBuffer("INSERT INTO network_daily_stocks(wallet_date, wallet_type, network_code, network_code_for, ");
        sqlBuffer.append("product_code, wallet_created, wallet_returned, wallet_balance, wallet_sold, last_txn_no, ");
        sqlBuffer.append("last_txn_type, last_txn_balance, previous_balance, created_on,creation_type )");
        sqlBuffer.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        // }
        final String sqlInsertNetworkDailyStock = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlInsertNetworkDailyStock=" + sqlInsertNetworkDailyStock);
        }
        sqlBuffer = null;

        // Select the stock for the requested product for network.
        final String sqlSelectNetworkStock = focBatchTransferWebQry.CloseOrderBatchSelectWalletQry();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelectNetworkStock=" + sqlSelectNetworkStock);
        }
        sqlBuffer = null;

        // Debit the network stock
        sqlBuffer = new StringBuffer(" UPDATE network_stocks SET previous_balance = wallet_balance , wallet_balance = ?, ");
        sqlBuffer.append(" wallet_sold = ? , last_txn_no = ? , last_txn_type = ?, last_txn_balance= ?, ");
        sqlBuffer.append(" modified_by =?, modified_on =? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" network_code = ? ");
        sqlBuffer.append(" AND ");
        sqlBuffer.append(" product_code = ? AND network_code_for = ?  AND wallet_type = ?");
        // }
        final String updateSelectedNetworkStock = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY updateSelectedNetworkStock=" + updateSelectedNetworkStock);
        }
        sqlBuffer = null;

        // Insert record into network_stock_transactions table.
        sqlBuffer = new StringBuffer(" INSERT INTO network_stock_transactions ( ");
        sqlBuffer.append(" txn_no, network_code, network_code_for, stock_type, reference_no, txn_date, requested_quantity, ");
        sqlBuffer.append(" approved_quantity, initiater_remarks, first_approved_remarks, second_approved_remarks, ");
        sqlBuffer.append(" first_approved_by, second_approved_by, first_approved_on, second_approved_on, ");
        sqlBuffer.append(" cancelled_by, cancelled_on, created_by, created_on, modified_on, modified_by, ");
        sqlBuffer.append(" txn_status, entry_type, txn_type, initiated_by, first_approver_limit, user_id, txn_mrp  ");
        if (isMultipleWalletApply) {
            sqlBuffer.append(",txn_wallet,ref_txn_id ");
            sqlBuffer.append(" )VALUES ");
            sqlBuffer.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        } else {
            // added by akanksha for tigo guatemala CR
            sqlBuffer.append(",txn_wallet ");
            sqlBuffer.append(" )VALUES ");
            sqlBuffer.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        }
        final String insertNetworkStockTransaction = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY insertNetworkStockTransaction=" + insertNetworkStockTransaction);
        }
        sqlBuffer = null;

        // Insert record into network_stock_trans_items
        sqlBuffer = new StringBuffer(" INSERT INTO network_stock_trans_items ");
        sqlBuffer.append(" (s_no, txn_no, product_code, required_quantity, approved_quantity, stock, mrp, amount, date_time) ");
        sqlBuffer.append(" VALUES ");
        sqlBuffer.append(" (?,?,?,?,?,?,?,?,?) ");
        final String insertNetworkStockTransactionItem = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY insertNetworkStockTransactionItem=" + insertNetworkStockTransactionItem);
        }
        sqlBuffer = null;

        // The query below is used to load the user balance
        // This table will basically used to update the daily_balance_updated_on
        // and also to know how many
        // records are to be inseert in user_daily_balances table

        final String selectUserBalances = focBatchTransferWebQry.UserBalancesQry();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY selectUserBalances=" + selectUserBalances);
        }
        sqlBuffer = null;

        // update daily_balance_updated_on with current date for user
        sqlBuffer = new StringBuffer(" UPDATE user_balances SET daily_balance_updated_on = ? ");
        sqlBuffer.append("WHERE user_id = ? ");
        final String updateUserBalances = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY updateUserBalances=" + updateUserBalances);
        }
        sqlBuffer = null;

        // Executed if day difference in last updated date and current date is
        // greater then or equal to 1
        // Insert number of records equal to day difference in last updated date
        // and current date in user_daily_balances
        sqlBuffer = new StringBuffer(" INSERT INTO user_daily_balances(balance_date, user_id, network_code, ");
        sqlBuffer.append("network_code_for, product_code, balance, prev_balance, last_transfer_type, ");
        sqlBuffer.append("last_transfer_no, last_transfer_on, created_on,creation_type )");
        sqlBuffer.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?) ");
        final String insertUserDailyBalances = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY insertUserDailyBalances=" + insertUserDailyBalances);
        }
        sqlBuffer = null;

        // Select the balance of user for the perticuler product and network.
        final String selectBalance = focBatchTransferWebQry.SelectBalanceQry();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY selectBalance=" + selectBalance);
        }
        sqlBuffer = null;

        // Credit the user balance(If balance found in user_balances)
        sqlBuffer = new StringBuffer(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
        sqlBuffer.append(" last_transfer_no = ? , last_transfer_on = ? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" user_id = ? ");
        sqlBuffer.append(" AND ");
        sqlBuffer.append(" product_code = ? AND network_code = ? AND network_code_for = ? ");
        /** START: Birendra: 30JAN2015 */
        sqlBuffer.append(" AND balance_type = ?");
        /** STOP: Birendra: 30JAN2015 */
        final String updateBalance = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY updateBalance=" + updateBalance);
        }
        sqlBuffer = null;

        // Insert the record of balnce for user (If balance not found in
        // user_balances)
        sqlBuffer = new StringBuffer(" INSERT ");
        sqlBuffer.append(" INTO user_balances ");
        sqlBuffer.append(" ( prev_balance,daily_balance_updated_on , balance, last_transfer_type, last_transfer_no, last_transfer_on , ");
        sqlBuffer.append(" user_id, product_code , network_code, network_code_for,  ");
        /** START: Birendra: 30JAN2015 */
        sqlBuffer.append(" balance_type  ");
        /** STOP: Birendra: 30JAN2015 */
        sqlBuffer.append(" ) VALUES ");
        sqlBuffer.append(" (?,?,?,?,?,?,?,?,?,?,?) ");
        final String insertBalance = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY insertBalance=" + insertBalance);
        }
        sqlBuffer = null;

        // Select the running countres of user(to be checked against the
        // effetive profile counters)
        sqlBuffer = new StringBuffer(" SELECT user_id, daily_in_count, daily_in_value, weekly_in_count, weekly_in_value, ");
        sqlBuffer.append(" monthly_in_count, monthly_in_value,daily_out_count, daily_out_value, weekly_out_count, ");
        sqlBuffer.append(" weekly_out_value, monthly_out_count, monthly_out_value, outside_daily_in_count, ");
        sqlBuffer.append(" outside_daily_in_value, outside_weekly_in_count, outside_weekly_in_value, ");
        sqlBuffer.append(" outside_monthly_in_count, outside_monthly_in_value, outside_daily_out_count, ");
        sqlBuffer.append(" outside_daily_out_value, outside_weekly_out_count, outside_weekly_out_value, ");
        sqlBuffer.append(" outside_monthly_out_count, outside_monthly_out_value, daily_subscriber_out_count, ");
        sqlBuffer.append(" daily_subscriber_out_value, weekly_subscriber_out_count, weekly_subscriber_out_value, ");
        sqlBuffer.append(" monthly_subscriber_out_count, monthly_subscriber_out_value,last_transfer_date ");
        sqlBuffer.append(" ,last_sos_txn_status,last_lr_status ");
        sqlBuffer.append(" FROM user_transfer_counts ");
        // DB220120123for update WITH RS
        // sqlBuffer.append(" WHERE user_id = ? FOR UPDATE ");

        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
            sqlBuffer.append(" WHERE user_id = ? FOR UPDATE WITH RS");
        } else {
            sqlBuffer.append(" WHERE user_id = ? FOR UPDATE ");
        }
        final String selectTransferCounts = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY selectTransferCounts=" + selectTransferCounts);
        }
        sqlBuffer = null;

        // Select the effective profile counters of user to be checked with
        // running counters of user
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT tp.profile_id,LEAST(tp.daily_transfer_in_count,catp.daily_transfer_in_count) daily_transfer_in_count, ");
        strBuff
            .append(" LEAST(tp.daily_transfer_in_value,catp.daily_transfer_in_value) daily_transfer_in_value ,LEAST(tp.weekly_transfer_in_count,catp.weekly_transfer_in_count) weekly_transfer_in_count, ");
        strBuff
            .append(" LEAST(tp.weekly_transfer_in_value,catp.weekly_transfer_in_value) weekly_transfer_in_value,LEAST(tp.monthly_transfer_in_count,catp.monthly_transfer_in_count) monthly_transfer_in_count, ");
        strBuff.append(" LEAST(tp.monthly_transfer_in_value,catp.monthly_transfer_in_value) monthly_transfer_in_value");
        strBuff.append(" FROM transfer_profile tp,transfer_profile catp ");
        strBuff.append(" WHERE tp.profile_id = ? AND tp.status = ?	AND tp.network_code = ? ");
        strBuff.append(" AND tp.category_code=catp.category_code ");
        strBuff.append(" AND catp.parent_profile_id=? AND catp.status=?	AND tp.network_code = catp.network_code ");
        final String selectProfileCounts = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY selectProfileCounts=" + selectProfileCounts);
        }

        // Update the user running countres (If record found for user running
        // counters)
        sqlBuffer = new StringBuffer(" UPDATE user_transfer_counts  SET ");
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
        final String updateTransferCounts = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY updateTransferCounts=" + updateTransferCounts);
        }
        sqlBuffer = null;

        // Insert the record in user_transfer_counts (If no record found for
        // user running counters)
        sqlBuffer = new StringBuffer(" INSERT INTO user_transfer_counts ( ");
        sqlBuffer.append(" daily_in_count, daily_in_value, weekly_in_count, weekly_in_value, monthly_in_count, ");
        sqlBuffer.append(" monthly_in_value, last_in_time, last_transfer_id,last_transfer_date,user_id ) ");
        sqlBuffer.append(" VALUES (?,?,?,?,?,?,?,?,?,?) ");
        final String insertTransferCounts = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY insertTransferCounts=" + insertTransferCounts);
        }
        sqlBuffer = null;

        // If current level of approval is 1 then below query is used to updatwe
        // foc_batch_items table
        sqlBuffer = new StringBuffer(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" reference_no=?, modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" first_approver_remarks = ?, ");
        sqlBuffer.append(" first_approved_by=?, first_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status IN (? , ? )  ");
        final String sqlApprv1FOCBatchItems = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlApprv1FOCBatchItems=" + sqlApprv1FOCBatchItems);
        }
        sqlBuffer = null;

        // If current level of approval is 2 then below query is used to updatwe
        // foc_batch_items table
        sqlBuffer = new StringBuffer(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" reference_no=?, modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" second_approver_remarks = ?, ");
        sqlBuffer.append(" second_approved_by=? , second_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status IN (? , ? )  ");
        final String sqlApprv2FOCBatchItems = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlApprv2FOCBatchItems=" + sqlApprv2FOCBatchItems);
        }
        sqlBuffer = null;

        // If current level of approval is 3 then below query is used to updatwe
        // foc_batch_items table
        sqlBuffer = new StringBuffer(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" reference_no=?, modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" third_approver_remarks = ?, ");
        sqlBuffer.append(" third_approved_by=? , third_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=?");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status = ?  ");
        final String sqlApprv3FOCBatchItems = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlApprv3FOCBatchItems=" + sqlApprv3FOCBatchItems);
        }
        sqlBuffer = null;

        // Afetr all teh records are processed the the below query is used to
        // load the various counts such as new ,
        // apprv1, close ,cancled etc. These couts will be used to deceide what
        // status to be updated in mater table
        final String selectItemsDetails = focBatchTransferWebQry.SelectItemsDetailsQry();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY selectItemsDetails=" + selectItemsDetails);
        }
        sqlBuffer = null;

        // Update the master table after all records are processed
        sqlBuffer = new StringBuffer("UPDATE foc_batches SET status=? , modified_by=? ,modified_on=? ,sms_default_lang=? ,sms_second_lang=?");
        sqlBuffer.append(" WHERE batch_id=? AND status=? ");
        final String updateFOCBatches = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY updateFOCBatches=" + updateFOCBatches);
        }
        sqlBuffer = null;

        // The query below is used to check is record is already modified or not
        sqlBuffer = new StringBuffer("SELECT modified_on FROM foc_batch_items ");
        sqlBuffer.append("WHERE batch_detail_id = ? ");
        final String isModified = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY isModified=" + isModified);
        }
        sqlBuffer = null;

        // Select the transfer profile product values(These will be used for
        // checking max balance of user)
        sqlBuffer = new StringBuffer("SELECT GREATEST(tpp.min_residual_balance,catpp.min_residual_balance) min_residual_balance, ");
        sqlBuffer.append(" LEAST(tpp.max_balance,catpp.max_balance) max_balance ");
        sqlBuffer.append(" FROM transfer_profile_products tpp,transfer_profile tp, transfer_profile catp,transfer_profile_products catpp ");
        sqlBuffer.append(" WHERE tpp.profile_id=? AND tpp.product_code = ? AND tpp.profile_id=tp.profile_id AND catp.profile_id=catpp.profile_id ");
        sqlBuffer
            .append(" AND tpp.product_code=catpp.product_code AND tp.category_code=catp.category_code AND catp.parent_profile_id=? AND catp.status=? AND tp.network_code = catp.network_code	 ");
        final String loadTransferProfileProduct = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY loadTransferProfileProduct=" + loadTransferProfileProduct);
        }
        sqlBuffer = null;

        // The below query will be exceute if external txn number unique is "Y"
        // in system preferences
        // This will check the existence of external txn number in
        // foc_batch_items table
        sqlBuffer = new StringBuffer(" SELECT 1 FROM foc_batch_items ");
        sqlBuffer.append("WHERE ext_txn_no=? AND status <> ? AND batch_detail_id<>? ");
        final String isExistsTxnNum1 = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY isExistsTxnNum1=" + isExistsTxnNum1);
        }
        sqlBuffer = null;

        // The below query will be exceute if external txn number unique is "Y"
        // in system preferences
        // and external txn number is not exists in foc_batch_items table.
        // This will check the existence of external txn number in
        // channel_transfers table
        sqlBuffer = new StringBuffer("  SELECT 1 FROM channel_transfers ");
        sqlBuffer.append("WHERE type=? AND ext_txn_no=? AND status <> ? ");
        final String isExistsTxnNum2 = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY isExistsTxnNum2=" + isExistsTxnNum2);
        }
        sqlBuffer = null;

        // The query below is used to insert the record in channel transfer
        // items table for the order that is closed
        sqlBuffer = new StringBuffer(" INSERT INTO channel_transfers_items ");
        sqlBuffer.append("(approved_quantity, commission_profile_detail_id, commission_rate, commission_type, commission_value, mrp,  ");
        sqlBuffer.append(" net_payable_amount, payable_amount, product_code, receiver_previous_stock, required_quantity, s_no,  ");
        sqlBuffer.append(" sender_previous_stock, tax1_rate, tax1_type, tax1_value, tax2_rate, tax2_type, tax2_value, tax3_rate, tax3_type,  ");
        sqlBuffer
            .append(" tax3_value, transfer_date, transfer_id, user_unit_price, sender_debit_quantity, receiver_credit_quantity,commision_quantity, sender_post_stock, receiver_post_stock)  ");
        sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)  ");
        final String insertIntoChannelTransferItem = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY insertIntoChannelTransferItem=" + insertIntoChannelTransferItem);
        }
        sqlBuffer = null;

        // The query below is used to insert the record in channel transfers
        // table for the order that is closed
        sqlBuffer = new StringBuffer(" INSERT INTO channel_transfers ");
        sqlBuffer.append(" (cancelled_by, cancelled_on, channel_user_remarks, close_date, commission_profile_set_id, commission_profile_ver, ");
        sqlBuffer.append(" created_by, created_on, domain_code, ext_txn_date, ext_txn_no, first_approved_by, first_approved_on, ");
        sqlBuffer.append(" first_approver_limit, first_approver_remarks, batch_date, batch_no, from_user_id, grph_domain_code, ");
        sqlBuffer.append(" modified_by, modified_on, net_payable_amount, network_code, network_code_for, payable_amount, pmt_inst_amount, ");
        sqlBuffer.append("  product_type, receiver_category_code, receiver_grade_code, ");
        sqlBuffer.append(" receiver_txn_profile, reference_no, request_gateway_code, request_gateway_type, requested_quantity, second_approved_by, ");
        sqlBuffer.append(" second_approved_on, second_approver_limit, second_approver_remarks,  ");
        sqlBuffer.append("  source, status, third_approved_by, third_approved_on, third_approver_remarks, to_user_id,  ");
        sqlBuffer.append(" total_tax1, total_tax2, total_tax3, transfer_category, transfer_date, transfer_id, transfer_initiated_by, ");
        sqlBuffer.append(" transfer_mrp, transfer_sub_type, transfer_type, type,sender_category_code,");
        sqlBuffer.append(" control_transfer,to_msisdn,to_domain_code,to_grph_domain_code, sms_default_lang, sms_second_lang,active_user_id,dual_comm_type");
        if (isMultipleWalletApply) {
            sqlBuffer.append(",TXN_WALLET)");
            sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        } else {
            sqlBuffer.append(") ");
            sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        }
        final String insertIntoChannelTransfer = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY insertIntoChannelTransfer=" + insertIntoChannelTransfer);
        }
        sqlBuffer = null;

        // The query below is used to get the balance information of user with
        // product.
        // This information will be send in message to user
        sqlBuffer = new StringBuffer(" SELECT UB.product_code,UB.balance, ");
        sqlBuffer.append(" PROD.product_short_code, PROD.short_name ");
        sqlBuffer.append(" FROM user_balances UB,products PROD ");
        sqlBuffer.append(" WHERE UB.user_id = ?  AND UB.network_code = ? AND UB.network_code_for = ? AND UB.product_code=PROD.product_code ");
        final String selectBalanceInfoForMessage = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY selectBalanceInfoForMessage=" + selectBalanceInfoForMessage);
        }

        // added by nilesh:added two new columns threshold_type and remark
        final StringBuffer strBuffThresholdInsert = new StringBuffer();
        strBuffThresholdInsert.append(" INSERT INTO user_threshold_counter ");
        strBuffThresholdInsert.append(" ( user_id,transfer_id , entry_date, entry_date_time, network_code, product_code , ");
        strBuffThresholdInsert.append(" type , transaction_type, record_type, category_code,previous_balance,current_balance, threshold_value, threshold_type, remark ) ");
        strBuffThresholdInsert.append(" VALUES ");
        strBuffThresholdInsert.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        final String insertUserThreshold = strBuffThresholdInsert.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("closeOrederByBatch", "QUERY insertUserThreshold=" + insertUserThreshold);
        }

        sqlBuffer = null;
        Date date = null;
        String batch_ID = null;
        ChannelTransferVO channelTransferVO = null;
        try {
            FOCBatchItemsVO focBatchItemVO = null;
            ChannelUserVO channelUserVO = null;
            // ChannelTransferVO channelTransferVO=null;
            ChannelTransferItemsVO channelTransferItemVO = null;
            date = new Date();
            ArrayList channelTransferItemVOList = null;
            pstmtLoadUser = p_con.prepareStatement(sqlLoadUser);
            pstmtLoadNetworkStock = p_con.prepareStatement(sqlLoadNetworkStock);
            pstmtUpdateNetworkStock = p_con.prepareStatement(sqlUpdateNetworkStock);
            pstmtInsertNetworkDailyStock = p_con.prepareStatement(sqlInsertNetworkDailyStock);
            pstmtSelectNetworkStock = p_con.prepareStatement(sqlSelectNetworkStock);
            pstmtupdateSelectedNetworkStock = p_con.prepareStatement(updateSelectedNetworkStock);
            // pstmtInsertNetworkStockTransaction=(OraclePreparedStatement)p_con.prepareStatement(insertNetworkStockTransaction);
            pstmtInsertNetworkStockTransaction = p_con.prepareStatement(insertNetworkStockTransaction);
            pstmtInsertNetworkStockTransactionItem = p_con.prepareStatement(insertNetworkStockTransactionItem);
            pstmtSelectUserBalances = p_con.prepareStatement(selectUserBalances);
            pstmtUpdateUserBalances = p_con.prepareStatement(updateUserBalances);
            pstmtInsertUserDailyBalances = p_con.prepareStatement(insertUserDailyBalances);
            pstmtSelectBalance = p_con.prepareStatement(selectBalance);
            pstmtUpdateBalance = p_con.prepareStatement(updateBalance);
            pstmtInsertBalance = p_con.prepareStatement(insertBalance);
            pstmtSelectTransferCounts = p_con.prepareStatement(selectTransferCounts);
            pstmtSelectProfileCounts = p_con.prepareStatement(selectProfileCounts);
            pstmtUpdateTransferCounts = p_con.prepareStatement(updateTransferCounts);
            pstmtInsertTransferCounts = p_con.prepareStatement(insertTransferCounts);

            // psmtAppr1FOCBatchItem=(OraclePreparedStatement)p_con.prepareStatement(sqlApprv1FOCBatchItems);//commented
            // for DB2
            // psmtAppr2FOCBatchItem=(OraclePreparedStatement)p_con.prepareStatement(sqlApprv2FOCBatchItems);//commented
            // for DB2
            // psmtAppr3FOCBatchItem=(OraclePreparedStatement)p_con.prepareStatement(sqlApprv3FOCBatchItems);//commented
            // for DB2
            psmtAppr1FOCBatchItem = p_con.prepareStatement(sqlApprv1FOCBatchItems);
            psmtAppr2FOCBatchItem = p_con.prepareStatement(sqlApprv2FOCBatchItems);
            psmtAppr3FOCBatchItem = p_con.prepareStatement(sqlApprv3FOCBatchItems);
            pstmtSelectItemsDetails = p_con.prepareStatement(selectItemsDetails);
            // pstmtUpdateMaster=(OraclePreparedStatement)p_con.prepareStatement(updateFOCBatches);//commented
            // for DB2
            pstmtUpdateMaster = p_con.prepareStatement(updateFOCBatches);
            pstmtIsModified = p_con.prepareStatement(isModified);
            pstmtLoadTransferProfileProduct = p_con.prepareStatement(loadTransferProfileProduct);
            pstmtIsTxnNumExists1 = p_con.prepareStatement(isExistsTxnNum1);
            pstmtIsTxnNumExists2 = p_con.prepareStatement(isExistsTxnNum2);
            pstmtInsertIntoChannelTransferItems = p_con.prepareStatement(insertIntoChannelTransferItem);
            // commented for DB2
            // pstmtInsertIntoChannelTranfers=(OraclePreparedStatement)p_con.prepareStatement(insertIntoChannelTransfer);
            pstmtInsertIntoChannelTranfers = p_con.prepareStatement(insertIntoChannelTransfer);
            pstmtSelectBalanceInfoForMessage = p_con.prepareStatement(selectBalanceInfoForMessage);
            psmtInsertUserThreshold = p_con.prepareStatement(insertUserThreshold);
            errorList = new ArrayList();
            final Iterator iterator = p_dataMap.keySet().iterator();
            String key = null;
            final MessageGatewayVO messageGatewayVO = MessageGatewayCache.getObject(defaultWebGatewayCode);
            NetworkStockVO networkStocksVO = null;
            int dayDifference = 0;
            Date dailyStockUpdatedOn = null;
            long stock = -1;
            long stockSold = -1;
            NetworkStockTxnVO networkStockTxnVO = null;
            String network_id = null;
            Date dailyBalanceUpdatedOn = null;
            NetworkStockTxnItemsVO networkItemsVO = null;
            TransferProfileProductVO transferProfileProductVO = null;
            UserTransferCountsVO countsVO = null;
            TransferProfileVO transferProfileVO = null;
            long maxBalance = 0;
            boolean isNotToExecuteQuery = false;
            long balance = -1;
            long previousUserBalToBeSetChnlTrfItems = -1;
            long previousNwStockToBeSetChnlTrfItems = -1;
            int m = 0;
            int k = 0;
            boolean flag = true;
            boolean terminateProcessing = false;
            while (iterator.hasNext()) {
                terminateProcessing = false;
                key = (String) iterator.next();
                focBatchItemVO = (FOCBatchItemsVO) p_dataMap.get(key);
                if (BTSLUtil.isNullString(batch_ID)) {
                    batch_ID = focBatchItemVO.getBatchId();
                }
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Executed focBatchItemVO=" + focBatchItemVO.toString());
                }
                pstmtLoadUser.clearParameters();
                m = 0;
                ++m;
                pstmtLoadUser.setString(m, focBatchItemVO.getUserId());
                for (int x = 0; x < m_receiverStatusAllowed.length; x++) {
                    ++m;
                    pstmtLoadUser.setString(m, m_receiverStatusAllowed[x]);
                }
                try {
                rs = pstmtLoadUser.executeQuery();
                // (record found for user i.e. receiver) if this condition is
                // not true then made entry in logs and leave this data.
                if (rs.next()) {
                    channelUserVO = new ChannelUserVO();
                    channelUserVO.setUserID(focBatchItemVO.getUserId());
                    channelUserVO.setStatus(rs.getString("userstatus"));
                    channelUserVO.setInSuspend(rs.getString("in_suspend"));
                    channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                    channelUserVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
                    channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                    channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                    channelUserVO.setTransferProfileStatus(rs.getString("profile_status"));
                    language = rs.getString("phone_language");
                    country = rs.getString("country");
                    channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
                    
                    // (user status is checked) if this condition is true then
                    // made entry in logs and leave this data.
                    /*
                     * if(!PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.
                     * getStatus()))
                     * {
                     * p_con.rollback();
                     * errorVO=new
                     * ListValueVO(focBatchItemVO.getMsisdn(),String.
                     * valueOf(focBatchItemVO
                     * .getRecordNumber()),p_messages.getMessage
                     * (p_locale,"batchfoc.batchapprovereject.msg.error.usersuspend"
                     * ));
                     * errorList.add(errorVO);
                     * BatchFocFileProcessLog.detailLog("closeOrederByBatch",
                     * p_focBatchMatserVO
                     * ,focBatchItemVO,"FAIL : User is suspend"
                     * ,"Approval level"+p_currentLevel);
                     * continue;
                     * }
                     */
                    // (commission profile status is checked) if this condition
                    // is true then made entry in logs and leave this data.
                    if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.comprofsuspend"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Commission profile suspend",
                            "Approval level" + p_currentLevel);
                        continue;
                    }
                    // (transfer profile is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.trfprofsuspend"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Transfer profile suspend", "Approval level" + p_currentLevel);
                        continue;
                    }
                    // (user in suspend is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.userinsuspend"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : User is IN suspend", "Approval level" + p_currentLevel);
                        continue;
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL.equalsIgnoreCase(focBatchItemVO.getStatus())) {
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "CANCEL : ", focBatchItemVO.getBatchDetailId());
                        continue;
                    }
                }
                // (no record found for user i.e. receiver) if this condition is
                // true then made entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.nouser"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : User not found", "Approval level" + p_currentLevel);
                    continue;
                }
                }
                finally {
                	if(rs!=null)
                		rs.close();
                }
                networkStocksVO = new NetworkStockVO();
                networkStocksVO.setProductCode(p_focBatchMatserVO.getProductCode());
                networkStocksVO.setNetworkCode(p_focBatchMatserVO.getNetworkCode());
                networkStocksVO.setNetworkCodeFor(p_focBatchMatserVO.getNetworkCodeFor());

                // creating the channelTransferVO here since O2CTransferID will
                // be required into the network stock
                // transaction table. Other information will be set into this VO
                // later
                channelTransferVO = new ChannelTransferVO();
                // seting the current value for generation of the transfer ID.
                // This will be over write by the
                // bacth foc items was created.
                channelTransferVO.setCreatedOn(date);
                channelTransferVO.setNetworkCode(p_focBatchMatserVO.getNetworkCode());
                
                channelTransferVO.setNetworkCodeFor(p_focBatchMatserVO.getNetworkCodeFor());
                channelTransferVO.setToUserID(focBatchItemVO.getUserId());
                channelTransferVO.setProductCode(p_focBatchMatserVO.getProductCode()); 
                ChannelTransferBL.genrateTransferID(channelTransferVO);
                o2cTransferID = channelTransferVO.getTransferID();
                // value is over writing since in the channel trasnfer table
                // created on should be same as when the
                // batch foc item was created.
                channelTransferVO.setCreatedOn(focBatchItemVO.getInitiatedOn());

                networkStocksVO.setLastTxnNum(o2cTransferID);
                /*
                 * changed on 20/07/06 as already in batch items the entries are
                 * in lowest denomination
                 * networkStocksVO.setLastTxnStock(PretupsBL.getSystemAmount(
                 * focBatchItemVO.getRequestedQuantity()));
                 * networkStocksVO.setStock(PretupsBL.getSystemAmount(focBatchItemVO
                 * .getRequestedQuantity()));
                 */
                networkStocksVO.setLastTxnBalance(focBatchItemVO.getRequestedQuantity());
                networkStocksVO.setWalletBalance(focBatchItemVO.getRequestedQuantity());
                if (isMultipleWalletApply) {
                    networkStocksVO.setWalletType(PretupsI.FOC_WALLET_TYPE);
                } else {
                	networkStocksVO.setWalletType(PretupsI.SALE_WALLET_TYPE);
                }
                networkStocksVO.setLastTxnType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                networkStocksVO.setModifiedBy(p_userID);
                networkStocksVO.setModifiedOn(date);
                dailyStockUpdatedOn = null;
                dayDifference = 0;
                // select the record form the network stock table.
                pstmtLoadNetworkStock.clearParameters();
                m = 0;
                ++m;
                pstmtLoadNetworkStock.setString(m, networkStocksVO.getNetworkCode());
                ++m;
                pstmtLoadNetworkStock.setString(m, networkStocksVO.getNetworkCodeFor());
                if (isMultipleWalletApply) {
                    ++m;
                    pstmtLoadNetworkStock.setString(m, PretupsI.FOC_WALLET_TYPE);
                } else {
                    ++m;
                    pstmtLoadNetworkStock.setString(m, PretupsI.SALE_WALLET_TYPE);
                }
                ++m;
                pstmtLoadNetworkStock.setDate(m, BTSLUtil.getSQLDateFromUtilDate(date));
               
                rs = null;
                try {
                rs = pstmtLoadNetworkStock.executeQuery();
                while (rs.next()) {
                    dailyStockUpdatedOn = rs.getDate("daily_stock_updated_on");

                    // if record exist check updated on date with current date
                    // day differences to maintain the record of previous days.
                    dayDifference = BTSLUtil.getDifferenceInUtilDates(dailyStockUpdatedOn, date);

                    if (dayDifference > 0) {
                        // if dates are not equal get the day differencts and
                        // execute insert qurery no of times of the difference
                        // is.
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "Till now daily Stock is not updated on = " + date + ", day differences = " + dayDifference);
                        }

                        for (k = 0; k < dayDifference; k++) {
                            pstmtInsertNetworkDailyStock.clearParameters();
                            m = 0;
                            ++m;
                            pstmtInsertNetworkDailyStock.setDate(m, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyStockUpdatedOn, k)));
                            if (isMultipleWalletApply) {
                                ++m;
                                pstmtInsertNetworkDailyStock.setString(m, PretupsI.FOC_WALLET_TYPE);
                            } else {
                                ++m;
                                pstmtInsertNetworkDailyStock.setString(m, PretupsI.SALE_WALLET_TYPE);
                            }
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, rs.getString("network_code"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, rs.getString("network_code_for"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, rs.getString("product_code"));
                            /*
                             * if(isMultipleWalletApply)
                             * {
                             * pstmtInsertNetworkDailyStock.setLong(++m,rs.getLong
                             * ("wallet_created"));
                             * pstmtInsertNetworkDailyStock.setLong(++m,rs.getLong
                             * ("wallet_returned"));
                             * pstmtInsertNetworkDailyStock.setLong(++m,rs.getLong
                             * ("wallet_balance"));
                             * pstmtInsertNetworkDailyStock.setLong(++m,rs.getLong
                             * ("wallet_sold"));
                             * 
                             * pstmtInsertNetworkDailyStock.setString(++m,
                             * channelTransferVO.getTransferID());
                             * pstmtInsertNetworkDailyStock.setString(++m,
                             * networkStocksVO.getLastTxnType());
                             * pstmtInsertNetworkDailyStock.setLong(++m,rs.getLong
                             * ("last_txn_balance"));
                             * pstmtInsertNetworkDailyStock.setLong(++m,rs.getLong
                             * ("previous_balance"));
                             * }
                             * else
                             * {
                             */
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs.getLong("wallet_created"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs.getLong("wallet_returned"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs.getLong("wallet_balance"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs.getLong("wallet_sold"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, channelTransferVO.getTransferID());
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, networkStocksVO.getLastTxnType());
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs.getLong("last_txn_balance"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs.getLong("previous_balance"));
                            ++m;
                            // }
                            pstmtInsertNetworkDailyStock.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, PretupsI.DAILY_STOCK_CREATION_TYPE_MAN);
                            updateCount = pstmtInsertNetworkDailyStock.executeUpdate();
							// added to make code compatible with insertion in partitioned table in postgres
							updateCount = BTSLUtil.getInsertCount(updateCount); 
                            if (updateCount <= 0) {
                                p_con.rollback();
                                errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                    "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                                errorList.add(errorVO);
                                BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while insert in network daily stock table",
                                    "Approval level = " + p_currentLevel + ", updateCount = " + updateCount);
                                terminateProcessing = true;
                                break;
                            }
                        }// end of for loop
                         // if updation of daily network stock is fail then
                         // terminate the processing
                        if (terminateProcessing) {
                            BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Termination of the procissing",
                                "Approval level = " + p_currentLevel);
                            break;
                        }
                        // Update the network stock table
                        pstmtUpdateNetworkStock.clearParameters();
                        m = 0;
                        ++m;
                        pstmtUpdateNetworkStock.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                        ++m;
                        pstmtUpdateNetworkStock.setString(m, networkStocksVO.getNetworkCode());
                        ++m;
                        pstmtUpdateNetworkStock.setString(m, networkStocksVO.getNetworkCodeFor());
                        if (isMultipleWalletApply) {
                            ++m;
                            pstmtUpdateNetworkStock.setString(m, PretupsI.FOC_WALLET_TYPE);
                        } else {
                            ++m;
                            pstmtUpdateNetworkStock.setString(m, PretupsI.SALE_WALLET_TYPE);
                        }
                        updateCount = pstmtUpdateNetworkStock.executeUpdate();
                        // (record not updated properly in db) if this condition
                        // is true then made entry in logs and leave this data.
                        if (updateCount <= 0) {
                            p_con.rollback();
                            errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                            errorList.add(errorVO);
                            BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating network daily stock table",
                                "Approval level = " + p_currentLevel + ", updateCount = " + updateCount);
                            continue;
                        }
                    }
                }// end of if () for daily network stock updation
                }
                finally {
                	if(rs!=null)
                		rs.close();
                }
                pstmtSelectNetworkStock.clearParameters();
                m = 0;
                ++m;
                pstmtSelectNetworkStock.setString(m, networkStocksVO.getNetworkCode());
                ++m;
                pstmtSelectNetworkStock.setString(m, networkStocksVO.getProductCode());
                ++m;
                pstmtSelectNetworkStock.setString(m, networkStocksVO.getNetworkCodeFor());
                
                rs = null;
                try {
                rs = pstmtSelectNetworkStock.executeQuery();
                stock = -1;
                stockSold = -1;
                previousNwStockToBeSetChnlTrfItems = -1;
                // get the network stock
                if (rs.next()) {
                    /*
                     * if(isMultipleWalletApply)
                     * {
                     * stock = rs.getLong("foc_stock");
                     * stockSold = rs.getLong("foc_stock_sold");
                     * }
                     * else
                     * {
                     */
                    stock = rs.getLong("wallet_balance");
                    stockSold = rs.getLong("wallet_sold");
                    // }
                    previousNwStockToBeSetChnlTrfItems = stock;
                   
                }
                // (network stock not found) if this condition is true then made
                // entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(p_messages.getMessage(p_locale, "label.all"), String.valueOf(focBatchItemVO.getRecordNumber()) + " - " + p_messages.getMessage(
                        p_locale, "label.all"), p_messages.getMessage(p_locale, "batchfoc.batchapprovereject.msg.error.networkstocknotexiststopprocess"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO,
                        "FAIL : Network stock not exists. So all records after this can not be processed", "Approval level = " + p_currentLevel);
                    throw new BTSLBaseException(this, methodName, "batchfoc.batchapprovereject.msg.error.networkstocknotexiststopprocess");

                }
                }
                finally {
                	if(rs!=null)
                		rs.close();
                }
                // (network stock is less) if this condition is true then made
                // entry in logs and leave this data.
                if (stock <= networkStocksVO.getWalletbalance()) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.networkstocklessstopprocess"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO,
                        "FAIL : Network stock is less than requested quantity. So all records after this can not be processed", "Approval level = " + p_currentLevel);
                    continue;
                }
                if (stock != -1) {
                    stock -= networkStocksVO.getWalletbalance();
                }
                if (stockSold != -1) {
                    stockSold += networkStocksVO.getWalletbalance();
                }
                m = 0;
                // Debit the network stock
                pstmtupdateSelectedNetworkStock.clearParameters();
                ++m;
                pstmtupdateSelectedNetworkStock.setLong(m, stock);
                ++m;
                pstmtupdateSelectedNetworkStock.setLong(m, stockSold);
                ++m;
                pstmtupdateSelectedNetworkStock.setString(m, networkStocksVO.getLastTxnNum());
                ++m;
                pstmtupdateSelectedNetworkStock.setString(m, networkStocksVO.getLastTxnType());
                ++m;
                pstmtupdateSelectedNetworkStock.setLong(m, networkStocksVO.getLastTxnBalance());
                ++m;
                pstmtupdateSelectedNetworkStock.setString(m, networkStocksVO.getModifiedBy());
                ++m;
                pstmtupdateSelectedNetworkStock.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStocksVO.getModifiedOn()));
                ++m;
                pstmtupdateSelectedNetworkStock.setString(m, networkStocksVO.getNetworkCode());
                ++m;
                pstmtupdateSelectedNetworkStock.setString(m, networkStocksVO.getProductCode());
                ++m;
                pstmtupdateSelectedNetworkStock.setString(m, networkStocksVO.getNetworkCodeFor());
                if (isMultipleWalletApply) {
                    ++m;
                    pstmtupdateSelectedNetworkStock.setString(m, PretupsI.FOC_WALLET_TYPE);
                } else {
                    ++m;
                    pstmtupdateSelectedNetworkStock.setString(m, PretupsI.SALE_WALLET_TYPE);
                }
                updateCount = pstmtupdateSelectedNetworkStock.executeUpdate();
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating network stock table",
                        "Approval level = " + p_currentLevel + ", updateCount = " + updateCount);
                    continue;
                }

                // for logging
                networkStocksVO.setPreviousBalance(stock);
             // AutoNetworkStockCreation logic
                if((boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.AUTO_NWSTK_CRTN_ALWD, networkStocksVO.getNetworkCode())){
                	new com.btsl.pretups.channel.transfer.businesslogic.AutoNetworkStockBL().networkStockThresholdValidation(networkStocksVO);
                }
                networkStockTxnVO = new NetworkStockTxnVO();
                networkStockTxnVO.setNetworkCode(networkStocksVO.getNetworkCode());
                networkStockTxnVO.setNetworkFor(networkStocksVO.getNetworkCodeFor());
                if (networkStocksVO.getNetworkCode().equals(p_focBatchMatserVO.getNetworkCodeFor())) {
                    networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_HOME);
                } else {
                    networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_ROAM);
                }
                // As discussed with sandeep in channel transfer table's
                // reference number field we have
                // to insert batch details id.So In network stock where channel
                // transfer table's reference number
                // was inserted, I insert batch detail id.
                networkStockTxnVO.setReferenceNo(focBatchItemVO.getBatchDetailId());
                networkStockTxnVO.setTxnDate(focBatchItemVO.getInitiatedOn());
                networkStockTxnVO.setRequestedQuantity(focBatchItemVO.getRequestedQuantity());
                networkStockTxnVO.setApprovedQuantity(focBatchItemVO.getRequestedQuantity());
                networkStockTxnVO.setInitiaterRemarks(focBatchItemVO.getInitiatorRemarks());
                networkStockTxnVO.setFirstApprovedRemarks(focBatchItemVO.getFirstApproverRemarks());
                networkStockTxnVO.setSecondApprovedRemarks(focBatchItemVO.getSecondApproverRemarks());
                networkStockTxnVO.setFirstApprovedBy(focBatchItemVO.getFirstApprovedBy());
                networkStockTxnVO.setSecondApprovedBy(focBatchItemVO.getSecondApprovedBy());
                networkStockTxnVO.setFirstApprovedOn(focBatchItemVO.getFirstApprovedOn());
                networkStockTxnVO.setSecondApprovedOn(focBatchItemVO.getSecondApprovedOn());
                networkStockTxnVO.setCancelledBy(focBatchItemVO.getCancelledBy());
                networkStockTxnVO.setCancelledOn(focBatchItemVO.getCancelledOn());
                networkStockTxnVO.setCreatedBy(p_userID);
                networkStockTxnVO.setCreatedOn(date);
                networkStockTxnVO.setModifiedOn(date);
                networkStockTxnVO.setModifiedBy(p_userID);

                networkStockTxnVO.setTxnStatus(focBatchItemVO.getStatus());
                networkStockTxnVO.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_TRANSFER);
                networkStockTxnVO.setTxnType(PretupsI.DEBIT);
                networkStockTxnVO.setInitiatedBy(p_userID);
                networkStockTxnVO.setFirstApproverLimit(0);
                networkStockTxnVO.setUserID(focBatchItemVO.getInitiatedBy());
                networkStockTxnVO.setTxnMrp(focBatchItemVO.getTransferMrp());

                // generate network stock transaction id
                network_id = NetworkStockBL.genrateStockTransctionID(networkStockTxnVO);
                networkStockTxnVO.setTxnNo(network_id);

                networkItemsVO = new NetworkStockTxnItemsVO();
                networkItemsVO.setSNo(1);
                networkItemsVO.setTxnNo(networkStockTxnVO.getTxnNo());
                networkItemsVO.setRequiredQuantity(focBatchItemVO.getRequestedQuantity());
                networkItemsVO.setApprovedQuantity(focBatchItemVO.getRequestedQuantity());
                networkItemsVO.setMrp(focBatchItemVO.getTransferMrp());
                networkItemsVO.setProductCode(p_focBatchMatserVO.getProductCode());
                networkItemsVO.setAmount(0);
                networkItemsVO.setProductCode(p_focBatchMatserVO.getProductCode());
                networkItemsVO.setStock(previousNwStockToBeSetChnlTrfItems);
                // Added on 07/02/08
                networkItemsVO.setDateTime(p_focBatchMatserVO.getBatchDate());
                m = 0;
                pstmtInsertNetworkStockTransaction.clearParameters();
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getTxnNo());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getNetworkCode());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getNetworkFor());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getStockType());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getReferenceNo());
                if (networkStockTxnVO.getTxnDate() != null) {
                    ++m;
                    pstmtInsertNetworkStockTransaction.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getTxnDate()));
                } else {
                    ++m;
                    pstmtInsertNetworkStockTransaction.setTimestamp(m, null);
                }
                ++m;
                pstmtInsertNetworkStockTransaction.setLong(m, networkStockTxnVO.getRequestedQuantity());
                ++m;
                pstmtInsertNetworkStockTransaction.setLong(m, networkStockTxnVO.getApprovedQuantity());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getInitiaterRemarks());

                // for multilanguage support
                // commented for DB2
                // pstmtInsertNetworkStockTransaction.setFormOfUse(++m,
                // OraclePreparedStatement.FORM_NCHAR);
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getFirstApprovedRemarks());

                // for multilanguage support
                // commented for DB2
                // pstmtInsertNetworkStockTransaction.setFormOfUse(++m,
                // OraclePreparedStatement.FORM_NCHAR);
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getSecondApprovedRemarks());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getFirstApprovedBy());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getSecondApprovedBy());
                ++m;
                pstmtInsertNetworkStockTransaction.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getFirstApprovedOn()));
                ++m;
                pstmtInsertNetworkStockTransaction.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getSecondApprovedOn()));
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getCancelledBy());
                ++m;
                pstmtInsertNetworkStockTransaction.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getCancelledOn()));
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getCreatedBy());
                ++m;
                pstmtInsertNetworkStockTransaction.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getCreatedOn()));
                ++m;
                pstmtInsertNetworkStockTransaction.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getModifiedOn()));
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getModifiedBy());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getTxnStatus());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getEntryType());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getTxnType());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getInitiatedBy());
                ++m;
                pstmtInsertNetworkStockTransaction.setLong(m, networkStockTxnVO.getFirstApproverLimit());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getUserID());
                ++m;
                pstmtInsertNetworkStockTransaction.setLong(m, networkStockTxnVO.getTxnMrp());
                if (isMultipleWalletApply) {
                    ++m;
                    pstmtInsertNetworkStockTransaction.setString(m, PretupsI.FOC_WALLET_TYPE);
                    ++m;
                    pstmtInsertNetworkStockTransaction.setString(m, channelTransferVO.getTransferID());
                } else {
                    ++m;
                    pstmtInsertNetworkStockTransaction.setString(m, PretupsI.SALE_WALLET_TYPE);
                }
                updateCount = pstmtInsertNetworkStockTransaction.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating network stock TXN table",
                        "Approval level = " + p_currentLevel + ", updateCount = " + updateCount);
                    continue;
                }
                m = 0;
                pstmtInsertNetworkStockTransactionItem.clearParameters();
                ++m;
                pstmtInsertNetworkStockTransactionItem.setInt(m, networkItemsVO.getSNo());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setString(m, networkItemsVO.getTxnNo());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setString(m, networkItemsVO.getProductCode());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setLong(m, networkItemsVO.getRequiredQuantity());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setLong(m, networkItemsVO.getApprovedQuantity());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setLong(m, networkItemsVO.getStock());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setLong(m, networkItemsVO.getMrp());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setLong(m, networkItemsVO.getAmount());
                // Date 07/02/08
                ++m;
                pstmtInsertNetworkStockTransactionItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));

                updateCount = pstmtInsertNetworkStockTransactionItem.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating network stock TXN itmes table",
                        "Approval level = " + p_currentLevel + ", updateCount = " + updateCount);
                    continue;
                }
                dailyBalanceUpdatedOn = null;
                dayDifference = 0;
                // select the record form the userBalances table.
                pstmtSelectUserBalances.clearParameters();
                m = 0;
                ++m;
                pstmtSelectUserBalances.setString(m, channelUserVO.getUserID());
                ++m;
                pstmtSelectUserBalances.setDate(m, BTSLUtil.getSQLDateFromUtilDate(date));
                
                rs = null;
                try {
                rs = pstmtSelectUserBalances.executeQuery();
                while (rs.next()) {
                    dailyBalanceUpdatedOn = rs.getDate("daily_balance_updated_on");
                    // if record exist check updated on date with current date
                    // day differences to maintain the record of previous days.
                    dayDifference = BTSLUtil.getDifferenceInUtilDates(dailyBalanceUpdatedOn, date);
                    if (dayDifference > 0) {
                        // if dates are not equal get the day differencts and
                        // execute insert qurery no of times of the
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "Till now daily Stock is not updated on = " + date + ", day differences = " + dayDifference);
                        }

                        for (k = 0; k < dayDifference; k++) {
                            pstmtInsertUserDailyBalances.clearParameters();
                            m = 0;
                            ++m;
                            pstmtInsertUserDailyBalances.setDate(m, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyBalanceUpdatedOn, k)));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, rs.getString("user_id"));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, rs.getString("network_code"));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, rs.getString("network_code_for"));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, rs.getString("product_code"));
                            ++m;
                            pstmtInsertUserDailyBalances.setLong(m, rs.getLong("balance"));
                            ++m;
                            pstmtInsertUserDailyBalances.setLong(m, rs.getLong("prev_balance"));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, channelTransferVO.getTransferID());
                            ++m;
                            pstmtInsertUserDailyBalances.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                            ++m;
                            pstmtInsertUserDailyBalances.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, PretupsI.DAILY_BALANCE_CREATION_TYPE_MAN);
                            updateCount = pstmtInsertUserDailyBalances.executeUpdate();
							// added to make code compatible with insertion in partitioned table in postgres
							updateCount = BTSLUtil.getInsertCount(updateCount); 
                            if (updateCount <= 0) {
                                p_con.rollback();
                                errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                    "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                                errorList.add(errorVO);
                                BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while inserting user daily balances table",
                                    "Approval level = " + p_currentLevel + ", updateCount = " + updateCount);
                                terminateProcessing = true;
                                break;
                            }
                        }// end of for loop
                        if (terminateProcessing) {
                            BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO,
                                "FAIL : Terminting the procssing of this user as error while updation daily balance", "Approval level = " + p_currentLevel);
                            continue;
                        }
                        // Update the user balances table
                        pstmtUpdateUserBalances.clearParameters();
                        m = 0;
                        ++m;
                        pstmtUpdateUserBalances.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                        ++m;
                        pstmtUpdateUserBalances.setString(m, channelUserVO.getUserID());
                        updateCount = pstmtUpdateUserBalances.executeUpdate();
                        // (record not updated properly) if this condition is
                        // true then made entry in logs and leave this data.
                        if (updateCount <= 0) {
                            p_con.rollback();
                            errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                            errorList.add(errorVO);
                            BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO,
                                "FAIL : DB Error while updating user balances table for daily balance",
                                "Approval level = " + p_currentLevel + ", updateCount = " + updateCount);
                            continue;
                        }
                    }
                }// end of if condition
                }
                finally {
                	if(rs!=null)
                		rs.close();
                }
                maxBalance = 0;
                isNotToExecuteQuery = false;
                pstmtSelectBalance.clearParameters();
                m = 0;
                ++m;
                pstmtSelectBalance.setString(m, channelUserVO.getUserID());
                ++m;
                pstmtSelectBalance.setString(m, p_focBatchMatserVO.getProductCode());
                ++m;
                pstmtSelectBalance.setString(m, p_focBatchMatserVO.getNetworkCode());
                ++m;
                pstmtSelectBalance.setString(m, p_focBatchMatserVO.getNetworkCodeFor());
                
                rs = null;
                try {
                rs = pstmtSelectBalance.executeQuery();
                balance = -1;
                previousUserBalToBeSetChnlTrfItems = -1;
                if (rs.next()) {
                    balance = rs.getLong("balance");
                                    }
                if (balance > -1) {
                    previousUserBalToBeSetChnlTrfItems = balance;
                    balance += focBatchItemVO.getRequestedQuantity();
                } else {
                    previousUserBalToBeSetChnlTrfItems = 0;
                }
                }
                finally {
                	if(rs!=null)
                		rs.close();
                }
                pstmtLoadTransferProfileProduct.clearParameters();
                m = 0;
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, focBatchItemVO.getTxnProfile());
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, p_focBatchMatserVO.getProductCode());
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, PretupsI.PARENT_PROFILE_ID_CATEGORY);
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, PretupsI.YES);
                
                rs = null;
                try {
                rs = pstmtLoadTransferProfileProduct.executeQuery();
                // get the transfer profile of user
                if (rs.next()) {
                    transferProfileProductVO = new TransferProfileProductVO();
                    transferProfileProductVO.setProductCode(p_focBatchMatserVO.getProductCode());
                    transferProfileProductVO.setMinResidualBalanceAsLong(rs.getLong("min_residual_balance"));
                    transferProfileProductVO.setMaxBalanceAsLong(rs.getLong("max_balance"));
                    
                }
                // (transfer profile not found) if this condition is true then
                // made entry in logs and leave this data.
                else {
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.profcountersnotfound"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : User Trf Profile not found for product",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                }
                finally {
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
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.maxbalancereach"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : User Max balance reached", "Approval level = " + p_currentLevel);
                    continue;
                }
                // check for the very first txn of the user containg the order
                // value larger than maxBalance
                // (max balance reach) if this condition is true then made entry
                // in logs and leave this data.
                else if (balance == -1 && maxBalance < focBatchItemVO.getRequestedQuantity()) {
                    if (!isNotToExecuteQuery) {
                        isNotToExecuteQuery = true;
                    }
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.maxbalancereach"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : User Max balance reached", "Approval level = " + p_currentLevel);
                    continue;
                }
                if (!isNotToExecuteQuery) {
                    m = 0;
                    // update
                    if (balance > -1) {
                        pstmtUpdateBalance.clearParameters();
                        handlerStmt = pstmtUpdateBalance;
                    } else {
                        // insert
                        pstmtInsertBalance.clearParameters();
                        handlerStmt = pstmtInsertBalance;
                        balance = focBatchItemVO.getRequestedQuantity();
                        ++m;
                        handlerStmt.setLong(m, 0);// previous balance
                        ++m;
                        handlerStmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));// updated
                        // on
                        // date
                    }
                    ++m;
                    handlerStmt.setLong(m, balance);
                    ++m;
                    handlerStmt.setString(m, PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                    ++m;
                    handlerStmt.setString(m, o2cTransferID);
                    ++m;
                    handlerStmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    handlerStmt.setString(m, channelUserVO.getUserID());
                    // where
                    ++m;
                    handlerStmt.setString(m, p_focBatchMatserVO.getProductCode());
                    ++m;
                    handlerStmt.setString(m, p_focBatchMatserVO.getNetworkCode());
                    ++m;
                    handlerStmt.setString(m, p_focBatchMatserVO.getNetworkCodeFor());

                    /** START: Birendra: 30JAN2015 */
                    ++m;
                    handlerStmt.setString(m, focBatchItemVO.getWalletCode());
                    /** START: Birendra: 30JAN2015 */

                    updateCount = handlerStmt.executeUpdate();
                    handlerStmt.clearParameters();
                    if (updateCount <= 0) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB error while credit uer balance",
                            "Approval level = " + p_currentLevel);
                        continue;
                    }
                    transferProfileProductVO = TransferProfileProductCache
                        .getTransferProfileDetails(channelUserVO.getTransferProfileID(), p_focBatchMatserVO.getProductCode());
                    thresholdValue = transferProfileProductVO.getMinResidualBalanceAsLong();
                    String threshold_type = PretupsI.THRESHOLD_TYPE_MIN;
                    final String remark = null;
                    if (balance <= transferProfileProductVO.getAltBalanceLong() && balance >= transferProfileProductVO.getMinResidualBalanceAsLong()) {
                        thresholdValue = transferProfileProductVO.getAltBalanceLong();
                        threshold_type = PretupsI.THRESHOLD_TYPE_ALERT;
                    }
                    // end
                    // thresholdValue=(Long)PreferenceCache.getControlPreference(PreferenceI.ZERO_BAL_THRESHOLD_VALUE,p_focBatchMatserVO.getNetworkCode(),
                    // focBatchItemVO.getCategoryCode()); //threshold value

                    // for zero balance counter..
                    try {
                        m = 0;
                        final boolean isUserThresholdEntryReq = false;
                        final String thresholdType = null;
                        // 24dec addded by nilesh:if previous bal is below
                        // threshold and current bal is above threshold,
                        // then entry in user_threshold_counter.Also,if previous
                        // bal is already below threshold and current bal is
                        // also below threshold
                        // then also entry in user_threshold_counter
                        // table(Discussed with Ved Sir and Protim Sir)
                        // if(previousUserBalToBeSetChnlTrfItems<=thresholdValue
                        // && balance >=thresholdValue)
                        if ((previousUserBalToBeSetChnlTrfItems <= thresholdValue && balance >= thresholdValue) || (previousUserBalToBeSetChnlTrfItems <= thresholdValue && balance <= thresholdValue)) {
                            if (_log.isDebugEnabled()) {
                                _log.debug(methodName, "Entry in threshold counter" + thresholdValue + ", prvbal: " + previousUserBalToBeSetChnlTrfItems + "nbal" + balance);
                            }
                            psmtInsertUserThreshold.clearParameters();
                            m = 0;
                            ++m;
                            psmtInsertUserThreshold.setString(m, channelUserVO.getUserID());
                            ++m;
                            psmtInsertUserThreshold.setString(m, o2cTransferID);
                            ++m;
                            psmtInsertUserThreshold.setDate(m, BTSLUtil.getSQLDateFromUtilDate(date));
                            ++m;
                            psmtInsertUserThreshold.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                            ++m;
                            psmtInsertUserThreshold.setString(m, p_focBatchMatserVO.getNetworkCode());
                            ++m;
                            psmtInsertUserThreshold.setString(m, p_focBatchMatserVO.getProductCode());
                            // psmtInsertUserThreshold.setLong(++m,
                            // p_userBalancesVO.getUnitValue());
                            ++m;
                            psmtInsertUserThreshold.setString(m, PretupsI.CHANNEL_TYPE_O2C);
                            ++m;
                            psmtInsertUserThreshold.setString(m, PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                            if (balance >= thresholdValue) {
                                ++m;
                                psmtInsertUserThreshold.setString(m, PretupsI.ABOVE_THRESHOLD_TYPE);
                            } else {
                                ++m;
                                psmtInsertUserThreshold.setString(m, PretupsI.BELOW_THRESHOLD_TYPE);
                            }
                            ++m;
                            psmtInsertUserThreshold.setString(m, focBatchItemVO.getCategoryCode());
                            ++m;
                            psmtInsertUserThreshold.setLong(m, previousUserBalToBeSetChnlTrfItems);
                            ++m;
                            psmtInsertUserThreshold.setLong(m, balance);
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
                        _log.error(methodName, "SQLException " + sqle.getMessage());
                        _log.errorTrace(methodName, sqle);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "FOCBatchTransferWebDAO[closeOrderByBatch]", o2cTransferID, "", p_focBatchMatserVO.getNetworkCode(),
                            "Error while updating user_threshold_counter table SQL Exception:" + sqle.getMessage());
                    }// end of catch

                }
                pstmtSelectTransferCounts.clearParameters();
                m = 0;
                ++m;
                pstmtSelectTransferCounts.setString(m, channelUserVO.getUserID());
                
                rs = null;
                try {
                rs = pstmtSelectTransferCounts.executeQuery();
                // get the user transfer counts
                countsVO = null;
                if (rs.next()) {
                    countsVO = new UserTransferCountsVO();
                    countsVO.setUserID(focBatchItemVO.getUserId());

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

                    countsVO.setUnctrlDailyInCount(rs.getLong("outside_daily_in_count"));
                    countsVO.setUnctrlDailyInValue(rs.getLong("outside_daily_in_value"));
                    countsVO.setUnctrlWeeklyInCount(rs.getLong("outside_weekly_in_count"));
                    countsVO.setUnctrlWeeklyInValue(rs.getLong("outside_weekly_in_value"));
                    countsVO.setUnctrlMonthlyInCount(rs.getLong("outside_monthly_in_count"));
                    countsVO.setUnctrlMonthlyInValue(rs.getLong("outside_monthly_in_value"));

                    countsVO.setUnctrlDailyOutCount(rs.getLong("outside_daily_out_count"));
                    countsVO.setUnctrlDailyOutValue(rs.getLong("outside_daily_out_value"));
                    countsVO.setUnctrlWeeklyOutCount(rs.getLong("outside_weekly_out_count"));
                    countsVO.setUnctrlWeeklyOutValue(rs.getLong("outside_weekly_out_value"));
                    countsVO.setUnctrlMonthlyOutCount(rs.getLong("outside_monthly_out_count"));
                    countsVO.setUnctrlMonthlyOutValue(rs.getLong("outside_monthly_out_value"));

                    countsVO.setDailySubscriberOutCount(rs.getLong("daily_subscriber_out_count"));
                    countsVO.setDailySubscriberOutValue(rs.getLong("daily_subscriber_out_value"));
                    countsVO.setWeeklySubscriberOutCount(rs.getLong("weekly_subscriber_out_count"));
                    countsVO.setWeeklySubscriberOutValue(rs.getLong("weekly_subscriber_out_value"));
                    countsVO.setMonthlySubscriberOutCount(rs.getLong("monthly_subscriber_out_count"));
                    countsVO.setMonthlySubscriberOutValue(rs.getLong("monthly_subscriber_out_value"));

                    countsVO.setLastTransferDate(rs.getDate("last_transfer_date"));
                    countsVO.setLastSOSTxnStatus(rs.getString("last_sos_txn_status"));
                    countsVO.setLastLrStatus(rs.getString("last_lr_status"));
                    
                }
                }
                finally {
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
                    ChannelTransferBL.checkResetCountersAfterPeriodChange(countsVO, date);
                }
                pstmtSelectProfileCounts.clearParameters();
                m = 0;
                ++m;
                pstmtSelectProfileCounts.setString(m, focBatchItemVO.getTxnProfile());
                ++m;
                pstmtSelectProfileCounts.setString(m, PretupsI.YES);
                ++m;
                pstmtSelectProfileCounts.setString(m, p_focBatchMatserVO.getNetworkCode());
                ++m;
                pstmtSelectProfileCounts.setString(m, PretupsI.PARENT_PROFILE_ID_CATEGORY);
                ++m;
                pstmtSelectProfileCounts.setString(m, PretupsI.YES);
                rs = null;
                try {
                rs = pstmtSelectProfileCounts.executeQuery();
                // get the transfwer profile counts
                if (rs.next()) {
                    transferProfileVO = new TransferProfileVO();
                    transferProfileVO.setProfileId(rs.getString("profile_id"));
                    transferProfileVO.setDailyInCount(rs.getLong("daily_transfer_in_count"));
                    transferProfileVO.setDailyInValue(rs.getLong("daily_transfer_in_value"));
                    transferProfileVO.setWeeklyInCount(rs.getLong("weekly_transfer_in_count"));
                    transferProfileVO.setWeeklyInValue(rs.getLong("weekly_transfer_in_value"));
                    transferProfileVO.setMonthlyInCount(rs.getLong("monthly_transfer_in_count"));
                    transferProfileVO.setMonthlyInValue(rs.getLong("monthly_transfer_in_value"));
                    
                }
                // (profile counts not found) if this condition is true then
                // made entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.transferprofilenotfound"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog
                        .detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Transfer profile not found", "Approval level = " + p_currentLevel);
                    continue;
                }
                }
                finally {
                	if(rs!=null)
                		rs.close();
                }
                // (daily in count reach) if this condition is true then made
                // entry in logs and leave this data.
                 if (transferProfileVO.getDailyInCount() <= countsVO.getDailyInCount()) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.dailyincntreach"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Daily transfer in count reach",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                // (daily in value reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getDailyInValue() < (countsVO.getDailyInValue() + focBatchItemVO.getRequestedQuantity())) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.dailyinvaluereach"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Daily transfer in value reach",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                // (weekly in count reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getWeeklyInCount() <= countsVO.getWeeklyInCount()) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.weeklyincntreach"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Weekly transfer in count reach",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                // (weekly in value reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getWeeklyInValue() < (countsVO.getWeeklyInValue() + focBatchItemVO.getRequestedQuantity())) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.weeklyinvaluereach"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Weekly transfer in value reach",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                // (monthly in count reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getMonthlyInCount() <= countsVO.getMonthlyInCount()) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.monthlyincntreach"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Monthly transfer in count reach",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                // (mobthly in value reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getMonthlyInValue() < (countsVO.getMonthlyInValue() + focBatchItemVO.getRequestedQuantity())) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.monthlyinvaluereach"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Monthly transfer in value reach",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                
                
                countsVO.setUserID(channelUserVO.getUserID());
                countsVO.setDailyInCount(countsVO.getDailyInCount() + 1);
                countsVO.setWeeklyInCount(countsVO.getWeeklyInCount() + 1);
                countsVO.setMonthlyInCount(countsVO.getMonthlyInCount() + 1);
                countsVO.setDailyInValue(countsVO.getDailyInValue() + focBatchItemVO.getRequestedQuantity());
                countsVO.setWeeklyInValue(countsVO.getWeeklyInValue() + focBatchItemVO.getRequestedQuantity());
                countsVO.setMonthlyInValue(countsVO.getMonthlyInValue() + focBatchItemVO.getRequestedQuantity());
                countsVO.setLastInTime(date);
                countsVO.setLastTransferID(o2cTransferID);
                countsVO.setLastTransferDate(date);
                // Update counts if found in db
                if (flag) {
                    m = 0;
                    pstmtUpdateTransferCounts.clearParameters();
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getDailyInCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getDailyInValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getWeeklyInCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getWeeklyInValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getMonthlyInCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getMonthlyInValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getDailyOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getDailyOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getWeeklyOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getWeeklyOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getMonthlyOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getMonthlyOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlDailyInCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlDailyInValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlWeeklyInCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlWeeklyInValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlMonthlyInCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlMonthlyInValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlDailyOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlDailyOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlWeeklyOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlWeeklyOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlMonthlyOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlMonthlyOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getDailySubscriberOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getDailySubscriberOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getWeeklySubscriberOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getWeeklySubscriberOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getMonthlySubscriberOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getMonthlySubscriberOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(countsVO.getLastInTime()));
                    ++m;
                    pstmtUpdateTransferCounts.setString(m, countsVO.getLastTransferID());
                    ++m;
                    pstmtUpdateTransferCounts.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(countsVO.getLastTransferDate()));
                    ++m;
                    pstmtUpdateTransferCounts.setString(m, countsVO.getUserID());
                    updateCount = pstmtUpdateTransferCounts.executeUpdate();
                }
                // Insert counts if not found in db
                else {
                    m = 0;
                    pstmtInsertTransferCounts.clearParameters();
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getDailyInCount());
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getDailyInValue());
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getWeeklyInCount());
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getWeeklyInValue());
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getMonthlyInCount());
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getMonthlyInValue());
                    ++m;
                    pstmtInsertTransferCounts.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(countsVO.getLastInTime()));
                    ++m;
                    pstmtInsertTransferCounts.setString(m, countsVO.getLastTransferID());
                    ++m;
                    pstmtInsertTransferCounts.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(countsVO.getLastTransferDate()));
                    ++m;
                    pstmtInsertTransferCounts.setString(m, countsVO.getUserID());
                    updateCount = pstmtInsertTransferCounts.executeUpdate();
                }
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    if (flag) {
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB error while insert user trasnfer counts",
                            "Approval level = " + p_currentLevel);
                    } else {
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB error while uptdate user trasnfer counts",
                            "Approval level = " + p_currentLevel);
                    }
                    continue;
                }
                pstmtIsModified.clearParameters();
                m = 0;
                ++m;
                pstmtIsModified.setString(m, focBatchItemVO.getBatchDetailId());
                java.sql.Timestamp newlastModified = null;
                rs = null;
                try {
                rs = pstmtIsModified.executeQuery();
                // check record is modified or not
                if (rs.next()) {
                    newlastModified = rs.getTimestamp("modified_on");
                    
                }
                // (record not found means record modified) if this condition is
                // true then made entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog
                        .detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Record is already modified", "Approval level = " + p_currentLevel);
                    continue;
                }
                }
                finally {
                	if(rs!=null)
                		rs.close();
                }
                // if this condition is true then made entry in logs and leave
                // this data.
                if (newlastModified.getTime() != BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getModifiedOn()).getTime()) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog
                        .detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Record is already modified", "Approval level = " + p_currentLevel);
                    continue;
                }
                // (external txn number is checked)
                if (isExternalTxnUnique && !BTSLUtil.isNullString(focBatchItemVO.getExtTxnNo())) {
                    // check in foc_batch_item table
                    pstmtIsTxnNumExists1.clearParameters();
                    m = 0;
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, focBatchItemVO.getBatchDetailId());
                    
                    rs = null;
                    try {
                    rs = pstmtIsTxnNumExists1.executeQuery();
                    // if this condition is true then made entry in logs and
                    // leave this data.
                    if (rs.next()) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, focBatchItemVO,
                            "FAIL : External transaction number already exists in FOC Batch", "Approval level = " + p_currentLevel);
                        continue;
                    }
                    }
                    finally {
                    	if(rs!=null)
                    		rs.close();
                    }
                    // check in channel transfer table
                    pstmtIsTxnNumExists2.clearParameters();
                    m = 0;
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, PretupsI.CHANNEL_TYPE_O2C);
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                   
                    rs = null;
                    try {
                    rs = pstmtIsTxnNumExists2.executeQuery();
                    // if this condition is true then made entry in logs and
                    // leave this data.
                    if (rs.next()) {
                        p_con.rollback();
                        
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : External transaction number already exists in CHANNEL TRF",
                            "Approval level = " + p_currentLevel);
                        continue;
                    }
                    }
                    finally {
                    	if(rs!=null)
                    		rs.close();
                    }
                }
                // If level 1 apperoval then set parameters in
                // psmtAppr1FOCBatchItem
                if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentLevel)) {
                    psmtAppr1FOCBatchItem.clearParameters();
                    focBatchItemVO.setFirstApprovedBy(p_userID);
                    focBatchItemVO.setFirstApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
                    m = 0;
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, o2cTransferID);
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    // commented for DB2 psmtAppr1FOCBatchItem.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR);
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getFirstApproverRemarks());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getStatus());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    updateCount = psmtAppr1FOCBatchItem.executeUpdate();
                }
                // If level 2 apperoval then set parameters in
                // psmtAppr2FOCBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentLevel)) {
                    psmtAppr2FOCBatchItem.clearParameters();
                    focBatchItemVO.setSecondApprovedBy(p_userID);
                    focBatchItemVO.setSecondApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
                    m = 0;
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, o2cTransferID);
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    // commented for DB2 psmtAppr2FOCBatchItem.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR);
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getSecondApproverRemarks());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getStatus());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    updateCount = psmtAppr2FOCBatchItem.executeUpdate();
                }
                // If level 2 apperoval then set parameters in
                // psmtAppr1FOCBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentLevel)) {
                    psmtAppr3FOCBatchItem.clearParameters();
                    focBatchItemVO.setThirdApprovedBy(p_userID);
                    focBatchItemVO.setThirdApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
                    m = 0;
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, o2cTransferID);
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr3FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    // commented for DB2 psmtAppr3FOCBatchItem.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR);
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, focBatchItemVO.getThirdApproverRemarks());
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr3FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, focBatchItemVO.getStatus());
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr3FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, focBatchItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    updateCount = psmtAppr3FOCBatchItem.executeUpdate();
                }
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating items table",
                        "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                    continue;
                }
                // channelTransferVO=new ChannelTransferVO();
                channelTransferVO.setCanceledOn(focBatchItemVO.getCancelledOn());
                channelTransferVO.setCanceledBy(focBatchItemVO.getCancelledBy());
                channelTransferVO.setChannelRemarks(focBatchItemVO.getInitiatorRemarks());
                channelTransferVO.setCommProfileSetId(focBatchItemVO.getCommissionProfileSetId());
                channelTransferVO.setCommProfileVersion(focBatchItemVO.getCommissionProfileVer());
                channelTransferVO.setDualCommissionType(focBatchItemVO.getDualCommissionType());
                channelTransferVO.setCreatedBy(focBatchItemVO.getInitiatedBy());
                // channelTransferVO.setCreatedOn(focBatchItemVO.getInitiatedOn());
                channelTransferVO.setDomainCode(p_focBatchMatserVO.getDomainCode());
                channelTransferVO.setExternalTxnDate(focBatchItemVO.getExtTxnDate());
                channelTransferVO.setExternalTxnNum(focBatchItemVO.getExtTxnNo());
                channelTransferVO.setFirstApprovedBy(focBatchItemVO.getFirstApprovedBy());
                channelTransferVO.setFirstApprovedOn(focBatchItemVO.getFirstApprovedOn());
                channelTransferVO.setFirstApproverLimit(0);
                channelTransferVO.setFirstApprovalRemark(focBatchItemVO.getFirstApproverRemarks());
                channelTransferVO.setSecondApprovedBy(focBatchItemVO.getSecondApprovedBy());
                channelTransferVO.setSecondApprovedOn(focBatchItemVO.getSecondApprovedOn());
                channelTransferVO.setSecondApprovalLimit(0);
                channelTransferVO.setSecondApprovalRemark(focBatchItemVO.getSecondApproverRemarks());
                channelTransferVO.setCategoryCode(PretupsI.OPERATOR_TYPE_OPT);
                channelTransferVO.setBatchNum(focBatchItemVO.getBatchId());
                channelTransferVO.setBatchDate(p_focBatchMatserVO.getBatchDate());
                channelTransferVO.setFromUserID(PretupsI.OPERATOR_TYPE_OPT);
                channelTransferVO.setTotalTax3(0);
                channelTransferVO.setPayableAmount(0);
                channelTransferVO.setNetPayableAmount(0);
                channelTransferVO.setPayInstrumentAmt(0);
                channelTransferVO.setGraphicalDomainCode(channelUserVO.getGeographicalCode());
                channelTransferVO.setModifiedBy(p_userID);
                channelTransferVO.setModifiedOn(date);
                channelTransferVO.setProductType(p_focBatchMatserVO.getProductType());
                channelTransferVO.setReceiverCategoryCode(focBatchItemVO.getCategoryCode());
                channelTransferVO.setReceiverGradeCode(focBatchItemVO.getGradeCode());
                channelTransferVO.setReceiverTxnProfile(focBatchItemVO.getTxnProfile());
                channelTransferVO.setReferenceNum(focBatchItemVO.getBatchDetailId());

                channelTransferVO.setDefaultLang(p_sms_default_lang);
                channelTransferVO.setSecondLang(p_sms_second_lang);
                // for balance logger
                channelTransferVO.setReferenceID(network_id);
                // ends here
                if (messageGatewayVO != null && messageGatewayVO.getRequestGatewayVO() != null) {
                    channelTransferVO.setRequestGatewayCode(messageGatewayVO.getRequestGatewayVO().getGatewayCode());
                    channelTransferVO.setRequestGatewayType(messageGatewayVO.getGatewayType());
                }
                channelTransferVO.setRequestedQuantity(focBatchItemVO.getRequestedQuantity());
                channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);
                channelTransferVO.setStatus(focBatchItemVO.getStatus());
                channelTransferVO.setThirdApprovedBy(focBatchItemVO.getThirdApprovedBy());
                channelTransferVO.setThirdApprovedOn(focBatchItemVO.getThirdApprovedOn());
                channelTransferVO.setThirdApprovalRemark(focBatchItemVO.getThirdApproverRemarks());
                channelTransferVO.setToUserID(channelUserVO.getUserID());
                channelTransferVO.setTotalTax1(focBatchItemVO.getTax1Value());
                channelTransferVO.setTotalTax2(focBatchItemVO.getTax2Value());
                channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_TRANSFER);
                channelTransferVO.setTransferDate(focBatchItemVO.getInitiatedOn());
                channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
                channelTransferVO.setTransferID(o2cTransferID);
                channelTransferVO.setTransferInitatedBy(focBatchItemVO.getInitiatedBy());
                channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
                channelTransferVO.setTransferMRP(focBatchItemVO.getTransferMrp());
                // added for logger
                channelTransferVO.setControlTransfer(PretupsI.YES);
                channelTransferVO.setToUserCode(focBatchItemVO.getMsisdn());
                channelTransferVO.setReceiverDomainCode(p_focBatchMatserVO.getDomainCode());
                channelTransferVO.setReceiverGgraphicalDomainCode(channelTransferVO.getGraphicalDomainCode());
                channelTransferVO.setWalletType(PretupsI.FOC_WALLET_TYPE);
                channelTransferVO.setActiveUserId(p_focBatchMatserVO.getCreatedBy());
                // end
                channelTransferItemVO = new ChannelTransferItemsVO();
                channelTransferItemVO.setApprovedQuantity(focBatchItemVO.getRequestedQuantity());
                channelTransferItemVO.setCommProfileDetailID(focBatchItemVO.getCommissionProfileDetailId());
                channelTransferItemVO.setCommRate(focBatchItemVO.getCommissionRate());
                channelTransferItemVO.setCommType(focBatchItemVO.getCommissionType());
                channelTransferItemVO.setCommValue(focBatchItemVO.getCommissionValue());
                channelTransferItemVO.setNetPayableAmount(0);
                channelTransferItemVO.setPayableAmount(0);
                channelTransferItemVO.setProductTotalMRP(focBatchItemVO.getTransferMrp());
                channelTransferItemVO.setProductCode(p_focBatchMatserVO.getProductCode());
                channelTransferItemVO.setReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
                channelTransferItemVO.setSenderPreviousStock(previousNwStockToBeSetChnlTrfItems);
                channelTransferItemVO.setRequiredQuantity(focBatchItemVO.getRequestedQuantity());
                channelTransferItemVO.setSerialNum(1);
                channelTransferItemVO.setTax1Rate(focBatchItemVO.getTax1Rate());
                channelTransferItemVO.setTax1Type(focBatchItemVO.getTax1Type());
                channelTransferItemVO.setTax1Value(focBatchItemVO.getTax1Value());
                channelTransferItemVO.setTax2Rate(focBatchItemVO.getTax2Rate());
                channelTransferItemVO.setTax2Type(focBatchItemVO.getTax2Type());
                channelTransferItemVO.setTax2Value(focBatchItemVO.getTax2Value());
                channelTransferItemVO.setTax3Rate(focBatchItemVO.getTax3Rate());
                channelTransferItemVO.setTax3Type(focBatchItemVO.getTax3Type());
                channelTransferItemVO.setTax3Value(focBatchItemVO.getTax3Value());
                channelTransferItemVO.setTransferID(o2cTransferID);
                channelTransferItemVO.setUnitValue(p_focBatchMatserVO.getProductMrp());
                channelTransferItemVO.setSenderDebitQty(focBatchItemVO.getRequestedQuantity());
                channelTransferItemVO.setReceiverCreditQty(focBatchItemVO.getRequestedQuantity());
                // for the balance logger
                channelTransferItemVO.setAfterTransReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
                channelTransferItemVO.setAfterTransSenderPreviousStock(previousNwStockToBeSetChnlTrfItems);
                // ends here
                channelTransferItemVOList = new ArrayList();
                channelTransferItemVOList.add(channelTransferItemVO);
                channelTransferItemVO.setShortName(p_focBatchMatserVO.getProductShortName());
                channelTransferVO.setChannelTransferitemsVOList(channelTransferItemVOList);
                channelTransferVO.setTransactionCode(PretupsI.TRANSFER_TYPE_FOC);
                
                
            	if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USERWISE_LOAN_ENABLE)).booleanValue() && channelTransferVO.getUserLoanVOList() !=null && channelTransferVO.getUserLoanVOList().size()>0 ) {

    				Map hashmap = ChannelTransferBL.checkUserLoanstatusAndAmount(p_con, channelTransferVO);
    				if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(false) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(true)) {
            			p_con.rollback();
            			final String args[] = { PretupsBL.getDisplayAmount((long)hashmap.get(PretupsI.WITHDRAW_AMOUNT)) };
    					
            			errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
            					"batcho2c.batchapprovereject.msg.error.loanPending",args));
            			errorList.add(errorVO);
            			BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Loan Status PENDING FOR USER",
            					"Approval level = " + p_currentLevel);
            			continue;
            		}

    				if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(true) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(false)) {
    					UserLoanWithdrawBL  userLoanWithdrawBL = new UserLoanWithdrawBL();
    					userLoanWithdrawBL.autoChannelLoanSettlement(channelTransferVO, PretupsI.USER_LOAN_REQUEST_TYPE,(long)hashmap.get(PretupsI.WITHDRAW_AMOUNT));
    				}

    			}else {
    				 Map<String, Object> hashmap = ChannelTransferBL.checkSOSstatusAndAmount(p_con, countsVO, channelTransferVO);
    	                if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(false) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(true))
    	                {
    	                	p_con.rollback();
    	                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
    	                        "batcho2c.batchapprovereject.msg.error.sosPending"));
    	                    errorList.add(errorVO);
    	                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : SOS Status PENDING FOR USER",
    	                        "Approval level = " + p_currentLevel);
    	                    continue;
    	                }else if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(true) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(false)) {
    	    				ChannelSoSWithdrawBL  channelSoSWithdrawBL = new ChannelSoSWithdrawBL();
    	    				channelSoSWithdrawBL.autoChannelSoSSettlement(channelTransferVO,PretupsI.SOS_REQUEST_TYPE);
    	    			}
    	                Map<String, Object> lrHashMap = ChannelTransferBL.checkLRstatusAndAmount(p_con, countsVO, channelTransferVO);
    	    			if(!lrHashMap.isEmpty()&& lrHashMap.get(PretupsI.DO_WITHDRAW).equals(true)){
    	    				ChannelSoSWithdrawBL  channelSoSWithdrawBL = new ChannelSoSWithdrawBL();
    	    				channelTransferVO.setLrWithdrawAmt((long)lrHashMap.get(PretupsI.WITHDRAW_AMOUNT));		
    	    				channelSoSWithdrawBL.autoChannelSoSSettlement(channelTransferVO,PretupsI.LR_REQUEST_TYPE);
    	    			}
    	                	
    			}
                
                
               
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Exiting: channelTransferVO=" + channelTransferVO.toString());
                }
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Exiting: channelTransferItemVO=" + channelTransferItemVO.toString());
                }
                m = 0;
                pstmtInsertIntoChannelTranfers.clearParameters();
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getCanceledBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getCanceledOn()));
                // commented for DB2
                // pstmtInsertIntoChannelTranfers.setFormOfUse(++m,
                // OraclePreparedStatement.FORM_NCHAR);
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getChannelRemarks());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getCommProfileSetId());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getCommProfileVersion());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getCreatedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getCreatedOn()));
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getDomainCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getExternalTxnDate()));
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getExternalTxnNum());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getFirstApprovedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getFirstApprovedOn()));
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getFirstApproverLimit());
                // commented for DB2
                // pstmtInsertIntoChannelTranfers.setFormOfUse(++m,
                // OraclePreparedStatement.FORM_NCHAR);
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getFirstApprovalRemark());
                ++m;
                pstmtInsertIntoChannelTranfers.setDate(m, BTSLUtil.getSQLDateFromUtilDate(channelTransferVO.getBatchDate()));
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getBatchNum());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getFromUserID());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getGraphicalDomainCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getModifiedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getModifiedOn()));
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getNetPayableAmount());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getNetworkCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getNetworkCodeFor());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getPayableAmount());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getPayInstrumentAmt());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getProductType());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getReceiverCategoryCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getReceiverGradeCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getReceiverTxnProfile());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getReferenceNum());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getRequestGatewayCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getRequestGatewayType());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getRequestedQuantity());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getSecondApprovedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getSecondApprovedOn()));
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getSecondApprovalLimit());
                // commented for DB2
                // pstmtInsertIntoChannelTranfers.setFormOfUse(++m,
                // OraclePreparedStatement.FORM_NCHAR);
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getSecondApprovalRemark());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getSource());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, focBatchItemVO.getStatus());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getThirdApprovedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getThirdApprovedOn()));
                // commented for DB2
                // pstmtInsertIntoChannelTranfers.setFormOfUse(++m,
                // OraclePreparedStatement.FORM_NCHAR);
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getThirdApprovalRemark());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getToUserID());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getTotalTax1());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getTotalTax2());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getTotalTax3());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getTransferCategory());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp (m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getTransferDate()));
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getTransferID());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getTransferInitatedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getTransferMRP());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getTransferSubType());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getTransferType());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getType());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getCategoryCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, PretupsI.YES);
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, focBatchItemVO.getMsisdn());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getDomainCode());

                // By sandeep ID TOG001
                // to geographical domain also inserted as the geogrpahical
                // domain that will help in reports
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getGraphicalDomainCode());

                // commented for DB2
                // pstmtInsertIntoChannelTranfers.setFormOfUse(++m,
                // OraclePreparedStatement.FORM_NCHAR);
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getDefaultLang());
                // commented for DB2
                // pstmtInsertIntoChannelTranfers.setFormOfUse(++m,
                // OraclePreparedStatement.FORM_NCHAR);
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getSecondLang());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, p_focBatchMatserVO.getCreatedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, focBatchItemVO.getDualCommissionType());
                if (isMultipleWalletApply) {
                    ++m;
                    pstmtInsertIntoChannelTranfers.setString(m, PretupsI.FOC_WALLET_TYPE);
                }
                // ends here
                // insert into channel transfer table
                updateCount = pstmtInsertIntoChannelTranfers.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while inserting in channel transfer table",
                        "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                    continue;
                }
                m = 0;
                pstmtInsertIntoChannelTransferItems.clearParameters();
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getApprovedQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, channelTransferItemVO.getCommProfileDetailID());
                ++m;
                pstmtInsertIntoChannelTransferItems.setDouble(m, channelTransferItemVO.getCommRate());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, channelTransferItemVO.getCommType());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getCommValue());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getProductTotalMRP());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getNetPayableAmount());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getPayableAmount());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, channelTransferItemVO.getProductCode());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getReceiverPreviousStock());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getRequiredQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setInt(m, channelTransferItemVO.getSerialNum());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getSenderPreviousStock());
                ++m;
                pstmtInsertIntoChannelTransferItems.setDouble(m, channelTransferItemVO.getTax1Rate());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, channelTransferItemVO.getTax1Type());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getTax1Value());
                ++m;
                pstmtInsertIntoChannelTransferItems.setDouble(m, channelTransferItemVO.getTax2Rate());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, channelTransferItemVO.getTax2Type());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getTax2Value());
                ++m;
                pstmtInsertIntoChannelTransferItems.setDouble(m, channelTransferItemVO.getTax3Rate());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, channelTransferItemVO.getTax3Type());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getTax3Value());
                ++m;
                pstmtInsertIntoChannelTransferItems.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, o2cTransferID);
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getUnitValue());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getApprovedQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getApprovedQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getCommQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getSenderPreviousStock() - channelTransferItemVO.getApprovedQuantity());
                
                ++m;
                
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getReceiverPreviousStock() + channelTransferItemVO.getApprovedQuantity());
                
       
                // insert into channel transfer items table
                updateCount = pstmtInsertIntoChannelTransferItems.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, focBatchItemVO,
                        "FAIL : DB Error while inserting in channel transfer items table", "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                    continue;
                }
                // commit the transaction after processing each record
                // user life cycle
                if (focBatchItemVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_CLOSE)) {
                    if (!PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus())) {
                        // int
                        // updatecount=operatorUtili.changeUserStatusToActive(
                        // p_con,channelTransferVO.getToUserID(),channelUserVO.getStatus());
                        int updatecount = 0;
                        final String str[] = txnReceiverUserStatusChang.split(","); // "CH:Y,EX:Y".split(",");
                        String newStatus[] = null;
                        for (int i = 0; i < str.length; i++) {
                            newStatus = str[i].split(":");
                            if (newStatus[0].equals(channelUserVO.getStatus())) {
                                updatecount = operatorUtili.changeUserStatusToActive(p_con, channelTransferVO.getToUserID(), channelUserVO.getStatus(), newStatus[1]);
                                break;
                            }
                        }
                        if (updatecount > 0) {
                            p_con.commit();
                            BatchFocFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, focBatchItemVO, "PASS : Order is closed successfully",
                                "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                        } else {
                            p_con.rollback();
                            throw new BTSLBaseException(this, "closeOrderByBatch", "error.status.updating");
                        }
                    } else {
                        p_con.commit();
                        BatchFocFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, focBatchItemVO, "PASS : Order is closed successfully",
                            "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                    }

                } else {
                    p_con.commit();

                }
                // made entry in network stock and balance logger
                ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);
                pstmtSelectBalanceInfoForMessage.clearParameters();
                m = 0;
                ++m;
                pstmtSelectBalanceInfoForMessage.setString(m, channelUserVO.getUserID());
                ++m;
                pstmtSelectBalanceInfoForMessage.setString(m, p_focBatchMatserVO.getNetworkCode());
                ++m;
                pstmtSelectBalanceInfoForMessage.setString(m, p_focBatchMatserVO.getNetworkCodeFor());
                try {
                rs = null;
                rs = pstmtSelectBalanceInfoForMessage.executeQuery();
                userbalanceList = new ArrayList();
                while (rs.next()) {
                    balancesVO = new UserBalancesVO();
                    balancesVO.setProductCode(rs.getString("product_code"));
                    balancesVO.setBalance(rs.getLong("balance"));
                    balancesVO.setProductShortCode(rs.getString("product_short_code"));
                    balancesVO.setProductShortName(rs.getString("short_name"));
                    userbalanceList.add(balancesVO);
                }
                }
                finally {
                	if(rs!=null)
                		rs.close();
                }
                // generate the message arguments to be send in SMS
                keyArgumentVO = new KeyArgumentVO();
                argsArr = new String[2];
                argsArr[1] = PretupsBL.getDisplayAmount(channelTransferItemVO.getRequiredQuantity());
                argsArr[0] = String.valueOf(channelTransferItemVO.getShortName());
                keyArgumentVO.setKey(PretupsErrorCodesI.FOC_OPT_CHNL_TRANSFER_SMS2);
                keyArgumentVO.setArguments(argsArr);
                txnSmsMessageList = new ArrayList();
                balSmsMessageList = new ArrayList();
                txnSmsMessageList.add(keyArgumentVO);
                for (int index = 0, n = userbalanceList.size(); index < n; index++) {
                    balancesVO = (UserBalancesVO) userbalanceList.get(index);
                    if (balancesVO.getProductCode().equals(channelTransferItemVO.getProductCode())) {
                        argsArr = new String[2];
                        argsArr[1] = balancesVO.getBalanceAsString();
                        argsArr[0] = balancesVO.getProductShortName();
                        keyArgumentVO = new KeyArgumentVO();
                        keyArgumentVO.setKey(PretupsErrorCodesI.FOC_OPT_CHNL_TRANSFER_SMS_BALSUBKEY);
                        keyArgumentVO.setArguments(argsArr);
                        balSmsMessageList.add(keyArgumentVO);
                        break;
                    }
                }
                locale = new Locale(language, country);
                String focNotifyMsg = null;
                if (isFOCSmsNotify) {
                    final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
                    if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
                        focNotifyMsg = channelTransferVO.getDefaultLang();
                    } else {
                        focNotifyMsg = channelTransferVO.getSecondLang();
                    }
                    //array = new String[] { channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, txnSmsMessageList), BTSLUtil.getMessage(locale, balSmsMessageList), focNotifyMsg };
                      array = new String[] { channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale,txnSmsMessageList),  BTSLUtil.getMessage(locale,balSmsMessageList), BTSLUtil.NullToString(focBatchItemVO.getInitiatorRemarks()),focNotifyMsg};
                }

                if (focNotifyMsg == null) {
                    //array = new String[] { channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, txnSmsMessageList), BTSLUtil.getMessage(locale, balSmsMessageList) };
                	  array = new String[] { channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, txnSmsMessageList), BTSLUtil.getMessage(locale,balSmsMessageList),BTSLUtil.NullToString(focBatchItemVO.getInitiatorRemarks())};
                }

                if(BTSLUtil.isNullString(focBatchItemVO.getInitiatorRemarks()))
    				messages=new BTSLMessages(PretupsErrorCodesI.FOC_OPT_CHNL_TRANSFER_SMS1,array);
    			else
    				messages=new BTSLMessages(PretupsErrorCodesI.FOC_OPT_CHNL_TRANSFER_SMS4,array);
                
                //messages = new BTSLMessages(PretupsErrorCodesI.FOC_OPT_CHNL_TRANSFER_SMS1, array);
                pushMessage = new PushMessage(focBatchItemVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, locale, channelTransferVO.getNetworkCode());
                // push SMS
                pushMessage.push();
                OneLineTXNLog.log(channelTransferVO, focBatchItemVO);
            }// end of while
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[closeOrderByBatch]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            BatchFocFileProcessLog.focBatchMasterLog(methodName, p_focBatchMatserVO, "FAIL : SQL Exception:" + sqe.getMessage(), "Approval level = " + p_currentLevel);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[closeOrderByBatch]", "", "",
                "", "Exception:" + ex.getMessage());
            BatchFocFileProcessLog.focBatchMasterLog(methodName, p_focBatchMatserVO, "FAIL : Exception:" + ex.getMessage(), "Approval level = " + p_currentLevel);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtLoadUser != null) {
                    pstmtLoadUser.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtLoadNetworkStock != null) {
                    pstmtLoadNetworkStock.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateNetworkStock != null) {
                    pstmtUpdateNetworkStock.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertNetworkDailyStock != null) {
                    pstmtInsertNetworkDailyStock.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectNetworkStock != null) {
                    pstmtSelectNetworkStock.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtupdateSelectedNetworkStock != null) {
                    pstmtupdateSelectedNetworkStock.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertNetworkStockTransaction != null) {
                    pstmtInsertNetworkStockTransaction.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertNetworkStockTransactionItem != null) {
                    pstmtInsertNetworkStockTransactionItem.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectUserBalances != null) {
                    pstmtSelectUserBalances.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateUserBalances != null) {
                    pstmtUpdateUserBalances.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertUserDailyBalances != null) {
                    pstmtInsertUserDailyBalances.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectBalance != null) {
                    pstmtSelectBalance.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateBalance != null) {
                    pstmtUpdateBalance.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertBalance != null) {
                    pstmtInsertBalance.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectTransferCounts != null) {
                    pstmtSelectTransferCounts.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectProfileCounts != null) {
                    pstmtSelectProfileCounts.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateTransferCounts != null) {
                    pstmtUpdateTransferCounts.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertTransferCounts != null) {
                    pstmtInsertTransferCounts.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr1FOCBatchItem != null) {
                    psmtAppr1FOCBatchItem.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr2FOCBatchItem != null) {
                    psmtAppr2FOCBatchItem.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr3FOCBatchItem != null) {
                    psmtAppr3FOCBatchItem.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsModified != null) {
                    pstmtIsModified.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtLoadTransferProfileProduct != null) {
                    pstmtLoadTransferProfileProduct.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (handlerStmt != null) {
                    handlerStmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsTxnNumExists1 != null) {
                    pstmtIsTxnNumExists1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsTxnNumExists2 != null) {
                    pstmtIsTxnNumExists2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertIntoChannelTransferItems != null) {
                    pstmtInsertIntoChannelTransferItems.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertIntoChannelTranfers != null) {
                    pstmtInsertIntoChannelTranfers.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectBalanceInfoForMessage != null) {
                    pstmtSelectBalanceInfoForMessage.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtInsertUserThreshold != null) {
                    psmtInsertUserThreshold.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                int m = 0;
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
                ++m;
                pstmtSelectItemsDetails.setString(m, batch_ID);
                rs = null;
                rs = pstmtSelectItemsDetails.executeQuery();
                // Check the final status to be updated in master after
                // processing all records of batch
                if (rs.next()) {
                    final int totalCount = rs.getInt("batch_total_record");
                    final int closeCount = rs.getInt("closed");
                    final int cnclCount = rs.getInt("cncl");
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    } catch (Exception e) {
                        _log.errorTrace(methodName, e);
                    }
                    String statusOfMaster = null;
                    if (totalCount == cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_CANCEL;
                    } else if (totalCount == closeCount + cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_CLOSE;
                    } else {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_OPEN;
                    }
                    m = 0;
                    ++m;
                    pstmtUpdateMaster.setString(m, statusOfMaster);
                    ++m;
                    pstmtUpdateMaster.setString(m, p_userID);
                    ++m;
                    pstmtUpdateMaster.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));

                    // commented for DB2 pstmtUpdateMaster.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR);
                    ++m;
                    pstmtUpdateMaster.setString(m, p_sms_default_lang);
                    // commented for DB2 pstmtUpdateMaster.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR);
                    ++m;
                    pstmtUpdateMaster.setString(m, p_sms_second_lang);
                    ++m;
                    pstmtUpdateMaster.setString(m, batch_ID);
                    ++m;
                    pstmtUpdateMaster.setString(m, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_UNDERPROCESS);

                    updateCount = pstmtUpdateMaster.executeUpdate();
                    // (record not updated properly) if this condition is true
                    // then made entry in logs and leave this data.
                    if (updateCount <= 0) {
                        p_con.rollback();
                        errorVO = new ListValueVO("", "", p_messages.getMessage(p_locale, "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.focBatchMasterLog(methodName, p_focBatchMatserVO, "FAIL : DB Error while updating master table",
                            "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "FOCBatchTransferWebDAO[closeOrederByBatch]", "", "", "", "Error while updating FOC_BATCHES table. Batch id=" + batch_ID);
                    }// end of if
                }// end of if
                p_con.commit();
            } catch (SQLException sqe) {
                try {
                    if (p_con != null) {
                        p_con.rollback();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                _log.error(methodName, "SQLException : " + sqe);
                _log.errorTrace(methodName, sqe);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[closeOrderByBatch]", "",
                    "", "", "SQL Exception:" + sqe.getMessage());
                BatchFocFileProcessLog.focBatchMasterLog("closeOrederByBatch", p_focBatchMatserVO, "FAIL : SQL Exception:" + sqe.getMessage(),
                    "Approval level = " + p_currentLevel);
                // throw new BTSLBaseException(this, methodName,
                // "error.general.sql.processing");
            } catch (Exception ex) {
                try {
                    if (p_con != null) {
                        p_con.rollback();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                _log.error(methodName, "Exception : " + ex);
                _log.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[closeOrderByBatch]", "",
                    "", "", "Exception:" + ex.getMessage());
                BatchFocFileProcessLog
                    .focBatchMasterLog("closeOrederByBatch", p_focBatchMatserVO, "FAIL : Exception:" + ex.getMessage(), "Approval level = " + p_currentLevel);
                // throw new BTSLBaseException(this, methodName,
                // "error.general.processing");
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectItemsDetails != null) {
                    pstmtSelectItemsDetails.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateMaster != null) {
                    pstmtUpdateMaster.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            // OneLineTXNLog.log(channelTransferVO);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: errorList size=" + errorList.size());
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
    public ArrayList loadBatchDetailsList(Connection p_con, String p_batchId) throws BTSLBaseException {
        final String methodName = "loadBatchDetailsList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_batchId=" + p_batchId);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final String sqlSelect = focBatchTransferWebQry.loadBatchDetailsListQry();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        FOCBatchMasterVO fOCBatchMasterVO = null;
        FOCBatchItemsVO fOCBatchItemsVO = null;
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_batchId);
            pstmt.setString(2, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_LOOKUP_TYPE);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                fOCBatchMasterVO = new FOCBatchMasterVO();
                fOCBatchMasterVO.setBatchId(rs.getString("batch_id"));
                fOCBatchMasterVO.setBatchName(rs.getString("batch_name"));
                fOCBatchMasterVO.setStatus(rs.getString("status"));
                fOCBatchMasterVO.setDomainCode(rs.getString("domain_code"));
                fOCBatchMasterVO.setDomainCodeDesc(rs.getString("domain_name"));
                fOCBatchMasterVO.setProductCode(rs.getString("product_code"));
                fOCBatchMasterVO.setProductCodeDesc(rs.getString("product_name"));
                fOCBatchMasterVO.setBatchFileName(rs.getString("batch_file_name"));
                fOCBatchMasterVO.setBatchTotalRecord(rs.getInt("batch_total_record"));
                fOCBatchMasterVO.setBatchDate(rs.getDate("batch_date"));
                fOCBatchMasterVO.setCreatedBy(rs.getString("initated_by"));
                fOCBatchMasterVO.setCreatedOn(rs.getTimestamp("created_on"));
                fOCBatchMasterVO.setStatus(rs.getString("status"));
                fOCBatchMasterVO.setStatusDesc(rs.getString("status_desc"));

                fOCBatchItemsVO = FOCBatchItemsVO.getInstance();
                fOCBatchItemsVO.setBatchDetailId(rs.getString("batch_detail_id"));
                fOCBatchItemsVO.setUserName(rs.getString("user_name"));
                fOCBatchItemsVO.setExternalCode(rs.getString("external_code"));
                fOCBatchItemsVO.setMsisdn(rs.getString("msisdn"));
                fOCBatchItemsVO.setCategoryName(rs.getString("category_name"));
                fOCBatchItemsVO.setCategoryCode(rs.getString("category_code"));
                fOCBatchItemsVO.setStatus(rs.getString("status_item"));
                fOCBatchItemsVO.setUserGradeCode(rs.getString("user_grade_code"));
                fOCBatchItemsVO.setGradeCode(rs.getString("user_grade_code"));
                fOCBatchItemsVO.setGradeName(rs.getString("grade_name"));
                fOCBatchItemsVO.setReferenceNo(rs.getString("reference_no"));
                fOCBatchItemsVO.setExtTxnNo(rs.getString("ext_txn_no"));
                fOCBatchItemsVO.setExtTxnDate(rs.getDate("ext_txn_date"));
                if (fOCBatchItemsVO.getExtTxnDate() != null) {
                    fOCBatchItemsVO.setExtTxnDateStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(fOCBatchItemsVO.getExtTxnDate())));
                }
                fOCBatchItemsVO.setTransferDate(rs.getDate("transfer_date"));
                if (fOCBatchItemsVO.getTransferDate() != null) {
                    fOCBatchItemsVO.setTransferDateStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(fOCBatchItemsVO.getTransferDate())));
                }
                fOCBatchItemsVO.setTxnProfile(rs.getString("txn_profile"));
                fOCBatchItemsVO.setCommissionProfileSetId(rs.getString("commission_profile_set_id"));
                fOCBatchItemsVO.setCommissionProfileVer(rs.getString("commission_profile_ver"));
                fOCBatchItemsVO.setCommissionProfileDetailId(rs.getString("commission_profile_detail_id"));
                fOCBatchItemsVO.setCommissionRate(rs.getDouble("commission_rate"));
                fOCBatchItemsVO.setCommissionType(rs.getString("commission_type"));
                fOCBatchItemsVO.setRequestedQuantity(rs.getLong("requested_quantity"));
                fOCBatchItemsVO.setTransferMrp(rs.getLong("transfer_mrp"));
                fOCBatchItemsVO.setInitiatorRemarks(rs.getString("initiator_remarks"));
                fOCBatchItemsVO.setFirstApprovedBy(rs.getString("first_approved_by"));
                fOCBatchItemsVO.setFirstApprovedOn(rs.getTimestamp("first_approved_on"));
                fOCBatchItemsVO.setFirstApproverRemarks(rs.getString("first_approver_remarks"));
                fOCBatchItemsVO.setSecondApprovedBy(rs.getString("second_approved_by"));
                fOCBatchItemsVO.setSecondApprovedOn(rs.getTimestamp("second_approved_on"));
                fOCBatchItemsVO.setSecondApproverRemarks(rs.getString("second_approver_remarks"));
                fOCBatchItemsVO.setThirdApprovedBy(rs.getString("third_approved_by"));
                fOCBatchItemsVO.setThirdApprovedOn(rs.getTimestamp("third_approved_on"));
                fOCBatchItemsVO.setThirdApproverRemarks(rs.getString("third_approver_remarks"));
                fOCBatchItemsVO.setOwnerName(rs.getString("ownername"));
                fOCBatchItemsVO.setOwnerMSISDN(rs.getString("ownermsisdn"));
                fOCBatchMasterVO.setFocBatchItemsVO(fOCBatchItemsVO);

                list.add(fOCBatchMasterVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[loadBatchDetailsList]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[loadBatchDetailsList]", "", "",
                "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: loadBatchDetailsList  list.size()=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method initiateBatchFOCTransferREST
     * This method used for the batch foc order initiation. The main purpose of
     * this method is to insert the
     * records in foc_batches,foc_batch_geographies & foc_batch_items table.
     * 
     * @param p_con
     *            Connection
     * @param p_batchMasterVO
     *            FOCBatchMasterVO
     * @param p_batchItemsList
     *            ArrayList
     * @param p_messages
     *            MessageResources
     * @param p_locale
     *            Locale
     * @return errorList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList initiateBatchFOCTransferREST(Connection p_con, FOCBatchMasterVO p_batchMasterVO, ArrayList p_batchItemsList, Locale p_locale, ArrayList<FocListValueVO> arrayFocListValueVO) throws BTSLBaseException {
        final String methodName = "initiateBatchFOCTransfer";
        if (_log.isDebugEnabled()) {
            _log.debug(
                methodName,
                "Entered.... p_batchMasterVO=" + p_batchMasterVO + ", p_batchItemsList.size() = " + p_batchItemsList.size() + ", p_batchItemsList=" + p_batchItemsList + "p_locale=" + p_locale);
        }
        Boolean isExternalTxnUnique = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE);
        Boolean isPartialBatchAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_PARTIAL_BATCH_ALLOWED);
        Boolean isTransactionTypeAlwd = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD);
        final ArrayList errorList = new ArrayList();
        ListValueVO errorVO = null;
        // for uniqueness of the external Txn ID
        PreparedStatement pstmtSelectExtTxnID1 = null;
        ResultSet rsSelectExtTxnID1 = null;
        final StringBuffer strBuffSelectExtTxnID1 = new StringBuffer(" SELECT 1 FROM foc_batch_items ");
        strBuffSelectExtTxnID1.append("WHERE ext_txn_no=? AND status <> ?  ");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffSelectExtTxnID1 Query =" + strBuffSelectExtTxnID1);
        }

        PreparedStatement pstmtSelectExtTxnID2 = null;
        ResultSet rsSelectExtTxnID2 = null;
        final StringBuffer strBuffSelectExtTxnID2 = new StringBuffer(" SELECT 1 FROM channel_transfers ");
        strBuffSelectExtTxnID2.append("WHERE type=? AND ext_txn_no=? AND status <> ? ");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffSelectExtTxnID2 Query =" + strBuffSelectExtTxnID2);
            // ends here
        }

        // for loading the O2C transfer rule for FOC transfer
        PreparedStatement pstmtSelectTrfRule = null;
        ResultSet rsSelectTrfRule = null;
        final StringBuffer strBuffSelectTrfRule = new StringBuffer(" SELECT transfer_rule_id,foc_transfer_type, foc_allowed ");
        strBuffSelectTrfRule.append("FROM chnl_transfer_rules WHERE network_code = ? AND domain_code = ? AND ");
        strBuffSelectTrfRule.append("from_category = 'OPT' AND to_category = ? AND status = 'Y' AND type = 'OPT' ");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffSelectTrfRule Query =" + strBuffSelectTrfRule);
            // ends here
        }

        // for loading the products associated with the transfer rule
        PreparedStatement pstmtSelectTrfRuleProd = null;
        ResultSet rsSelectTrfRuleProd = null;
        final StringBuffer strBuffSelectTrfRuleProd = new StringBuffer("SELECT 1 FROM chnl_transfer_rules_products ");
        strBuffSelectTrfRuleProd.append("WHERE transfer_rule_id=?  AND product_code = ? ");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffSelectTrfRuleProd Query =" + strBuffSelectTrfRuleProd);
            // ends here
        }

        // for loading the products associated with the commission profile
        PreparedStatement pstmtSelectCProfileProd = null;
        ResultSet rsSelectCProfileProd = null;
        final StringBuffer strBuffSelectCProfileProd = new StringBuffer("SELECT cp.min_transfer_value,cp.max_transfer_value,cp.discount_type,cp.discount_rate, ");
        strBuffSelectCProfileProd.append("cp.comm_profile_products_id, cp.transfer_multiple_off, cp.taxes_on_foc_applicable  ");
        strBuffSelectCProfileProd.append("FROM commission_profile_products cp ");
        strBuffSelectCProfileProd.append("WHERE cp.product_code = ? AND cp.comm_profile_set_id = ? AND cp.comm_profile_set_version = ? ");
        //strBuffSelectCProfileProd.append("AND cp.transaction_type = ? AND cp.payment_mode = ? ");
        if(isTransactionTypeAlwd)
            strBuffSelectCProfileProd.append("AND cp.transaction_type in (?,?) ");
            else
            	strBuffSelectCProfileProd.append("AND cp.transaction_type = ? ");
            strBuffSelectCProfileProd.append("AND cp.payment_mode = ? ORDER BY cp.TRANSACTION_TYPE desc");
          
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffSelectCProfileProd Query =" + strBuffSelectCProfileProd+",isTransactionTypeAlwd="+isTransactionTypeAlwd);
        }

        PreparedStatement pstmtSelectCProfileProdDetail = null;
        ResultSet rsSelectCProfileProdDetail = null;
        final StringBuffer strBuffSelectCProfileProdDetail = new StringBuffer("SELECT cpd.tax1_type,cpd.tax1_rate,cpd.tax2_type,cpd.tax2_rate, ");
        strBuffSelectCProfileProdDetail.append("cpd.tax3_type,cpd.tax3_rate,cpd.commission_type,cpd.commission_rate,cpd.comm_profile_detail_id ");
        strBuffSelectCProfileProdDetail.append("FROM commission_profile_details cpd ");
        strBuffSelectCProfileProdDetail.append("WHERE  cpd.comm_profile_products_id = ? AND cpd.start_range <= ? AND cpd.end_range >= ? ");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffSelectCProfileProdDetail Query =" + strBuffSelectCProfileProdDetail);
            // ends here
        }

        // for existance of the product in the transfer profile
        PreparedStatement pstmtSelectTProfileProd = null;
        ResultSet rsSelectTProfileProd = null;
        final StringBuffer strBuffSelectTProfileProd = new StringBuffer(" SELECT 1 ");
        strBuffSelectTProfileProd.append("FROM transfer_profile_products tpp,transfer_profile tp, transfer_profile catp,transfer_profile_products catpp ");
        strBuffSelectTProfileProd.append("WHERE tpp.profile_id=? AND tpp.product_code = ? AND tpp.profile_id=tp.profile_id AND catp.profile_id=catpp.profile_id ");
        strBuffSelectTProfileProd
            .append("AND tpp.product_code=catpp.product_code AND tp.category_code=catp.category_code AND catp.parent_profile_id=? AND catp.status='Y' AND tp.network_code = catp.network_code");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffSelectTProfileProd Query =" + strBuffSelectTProfileProd);
            // ends here
        }

        // insert data in the batch master table
        // commented for DB2 OraclePreparedStatement pstmtInsertBatchMaster =
        // null;
        PreparedStatement pstmtInsertBatchMaster = null;
        final StringBuffer strBuffInsertBatchMaster = new StringBuffer("INSERT INTO foc_batches (batch_id, network_code, ");
        strBuffInsertBatchMaster.append("network_code_for, batch_name, status, domain_code, product_code, ");
        strBuffInsertBatchMaster.append("batch_file_name, batch_total_record, batch_date, created_by, created_on, ");
        strBuffInsertBatchMaster
            .append(" modified_by, modified_on,sms_default_lang,sms_second_lang,transfer_type,transfer_sub_type) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffInsertBatchMaster Query =" + strBuffInsertBatchMaster);
            // ends here
        }

        // insert data in the batch Geographies table
        PreparedStatement pstmtInsertBatchGeo = null;
        final StringBuffer strBuffInsertBatchGeo = new StringBuffer("INSERT INTO foc_batch_geographies(batch_id,geography_code,date_time) VALUES (?,?,?)");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffInsertBatchGeo Query =" + strBuffInsertBatchGeo);
            // ends here
        }

        // insert data in the batch items table
        // commented for DB2OraclePreparedStatement pstmtInsertBatchItems =
        // null;
        PreparedStatement pstmtInsertBatchItems = null;
        final StringBuffer strBuffInsertBatchItems = new StringBuffer("INSERT INTO foc_batch_items (batch_id, batch_detail_id, ");
        strBuffInsertBatchItems.append("category_code, msisdn, user_id, status, modified_by, modified_on, user_grade_code, ");
        strBuffInsertBatchItems.append("ext_txn_no, ext_txn_date, transfer_date, txn_profile, ");
        strBuffInsertBatchItems.append("commission_profile_set_id, commission_profile_ver, commission_profile_detail_id, ");
        strBuffInsertBatchItems.append("commission_type, commission_rate, commission_value, tax1_type, tax1_rate, ");
        strBuffInsertBatchItems.append("tax1_value, tax2_type, tax2_rate, tax2_value, tax3_type, tax3_rate, ");
        strBuffInsertBatchItems.append("tax3_value, requested_quantity, transfer_mrp, initiator_remarks, external_code,rcrd_status");

        /** START: Birendra: 29JAN2015 */
        strBuffInsertBatchItems.append(", user_wallet");
        /** STOP: Birendra: 29JAN2015 */
         
        strBuffInsertBatchItems.append(", dual_comm_type");
        
        strBuffInsertBatchItems.append(") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");
        /** START: Birendra: 29JAN2015 */
        strBuffInsertBatchItems.append(",?)");
        
        
        /** START: Birendra: 29JAN2015 */
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffInsertBatchItems Query = " + strBuffInsertBatchItems);
            // ends here
        }

        // update master table with OPEN status
        PreparedStatement pstmtUpdateBatchMaster = null;
        final StringBuffer strBuffUpdateBatchMaster = new StringBuffer("UPDATE foc_batches SET batch_total_record=? , status =?, TXN_WALLET = ?  WHERE batch_id=?");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffUpdateBatchMaster Query =" + strBuffUpdateBatchMaster);
        }
        int totalSuccessRecords = 0;
        try {
            pstmtSelectExtTxnID1 = p_con.prepareStatement(strBuffSelectExtTxnID1.toString());
            pstmtSelectExtTxnID2 = p_con.prepareStatement(strBuffSelectExtTxnID2.toString());
            pstmtSelectTrfRule = p_con.prepareStatement(strBuffSelectTrfRule.toString());
            pstmtSelectTrfRuleProd = p_con.prepareStatement(strBuffSelectTrfRuleProd.toString());
            pstmtSelectCProfileProd = p_con.prepareStatement(strBuffSelectCProfileProd.toString());
            pstmtSelectCProfileProdDetail = p_con.prepareStatement(strBuffSelectCProfileProdDetail.toString());
            pstmtSelectTProfileProd = p_con.prepareStatement(strBuffSelectTProfileProd.toString());

            // pstmtInsertBatchMaster=(OraclePreparedStatement)p_con.prepareStatement(strBuffInsertBatchMaster.toString());
            pstmtInsertBatchMaster = p_con.prepareStatement(strBuffInsertBatchMaster.toString());
            pstmtInsertBatchGeo = p_con.prepareStatement(strBuffInsertBatchGeo.toString());

            // pstmtInsertBatchItems=(OraclePreparedStatement)p_con.prepareStatement(strBuffInsertBatchItems.toString());
            pstmtInsertBatchItems = p_con.prepareStatement(strBuffInsertBatchItems.toString());
            pstmtUpdateBatchMaster = p_con.prepareStatement(strBuffUpdateBatchMaster.toString());
            ChannelTransferRuleVO rulesVO = null;
            int index = 0;
            FOCBatchItemsVO batchItemsVO = null;

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
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getBatchId());
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getNetworkCode());
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getNetworkCodeFor());

            // commented for DB2
            // pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getBatchName());
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getStatus());
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getDomainCode());
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getProductCode());
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getBatchFileName());
            ++index;
            pstmtInsertBatchMaster.setLong(index, p_batchMasterVO.getBatchTotalRecord());
            ++index;
            pstmtInsertBatchMaster.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(p_batchMasterVO.getBatchDate()));
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getCreatedBy());
            ++index;
            pstmtInsertBatchMaster.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(p_batchMasterVO.getCreatedOn()));
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getModifiedBy());
            ++index;
            pstmtInsertBatchMaster.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(p_batchMasterVO.getModifiedOn()));

            // commented for
            // DB2pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getDefaultLang());
            // commented for
            // DB2pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getSecondLang());
            ++index;
            pstmtInsertBatchMaster.setString(index, PretupsI.TRANSFER_TYPE_FOC);
            ++index;
            pstmtInsertBatchMaster.setString(index, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
            int queryExecutionCount = pstmtInsertBatchMaster.executeUpdate();
            if (queryExecutionCount <= 0) {
                p_con.rollback();
                _log.error(methodName, "Unable to insert in the batch master table.");
                BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : DB Error Unable to insert in the batch master table",
                    "queryExecutionCount=" + queryExecutionCount);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[initiateBatchFOCTransfer]",
                    "", "", "", "Unable to insert in the batch master table.");
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
            // ends here
            // insert batch geographics
            ListValueVO listValueVO = null;
            final int size = p_batchMasterVO.getGeographyList().size();
            for (int i = 0; i < size; i++) {
                index = 0;
                listValueVO = (ListValueVO) p_batchMasterVO.getGeographyList().get(i);
                ++index;
                pstmtInsertBatchGeo.setString(index, p_batchMasterVO.getBatchId());
                ++index;
                pstmtInsertBatchGeo.setString(index, listValueVO.getValue());
                // Added on 07/02/08
                ++index;
                pstmtInsertBatchGeo.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(p_batchMasterVO.getBatchDate()));
                queryExecutionCount = pstmtInsertBatchGeo.executeUpdate();
                if (queryExecutionCount <= 0) {
                    p_con.rollback();
                    _log.error(methodName, "Unable to insert in the batch geographics table.");
                    BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : DB Error Unable to insert in the batch geographics table",
                        "queryExecutionCount=" + queryExecutionCount);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "FOCBatchTransferWebDAO[initiateBatchFOCTransfer]", "", "", "", "Unable to insert in the batch geographics table.");
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
                pstmtInsertBatchGeo.clearParameters();
            }
            // ends here
            String msgArr[] = null;
            for (int i = 0, j = p_batchItemsList.size(); i < j; i++) {
                batchItemsVO = (FOCBatchItemsVO) p_batchItemsList.get(i);
                // check the uniqueness of the external txn number
                if (!BTSLUtil.isNullString(batchItemsVO.getExtTxnNo()) && isExternalTxnUnique) {
                    index = 0;
                    ++index;
                    pstmtSelectExtTxnID1.setString(index, batchItemsVO.getExtTxnNo());
                    ++index;
                    pstmtSelectExtTxnID1.setString(index, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    rsSelectExtTxnID1 = pstmtSelectExtTxnID1.executeQuery();
                    pstmtSelectExtTxnID1.clearParameters();
                    if (rsSelectExtTxnID1.next()) {
                        // put error external txn number already exist
                        errorVO = new ListValueVO();
                        errorVO.setCodeName(batchItemsVO.getMsisdn());
                        errorVO.setOtherInfo(String.valueOf(batchItemsVO.getRecordNumber()));
                        errorVO.setOtherInfo2(PretupsRestUtil.getMessageString( "batchfoc.initiatebatchfoctransfer.msg.error.exttxnalreadyexists"));
                        errorList.add(errorVO);
                        // Handling the failed transaction details for
                        // generating the Excel file
                        if (isPartialBatchAllowed) {
                            addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                        }
                        BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : External txn number already exist FOC BATCCH", "");
                        continue;
                    }
                    index = 0;
                    ++index;
                    pstmtSelectExtTxnID2.setString(index, PretupsI.CHANNEL_TYPE_O2C);
                    ++index;
                    pstmtSelectExtTxnID2.setString(index, batchItemsVO.getExtTxnNo());
                    ++index;
                    pstmtSelectExtTxnID2.setString(index, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    rsSelectExtTxnID2 = pstmtSelectExtTxnID2.executeQuery();
                    pstmtSelectExtTxnID2.clearParameters();
                    if (rsSelectExtTxnID2.next()) {
                        // put error external txn number already exist
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                            "batchfoc.initiatebatchfoctransfer.msg.error.exttxnalreadyexists"));
                        errorList.add(errorVO);
                        // Handling the failed transaction details for
                        // generating the Excel file
                        if (isPartialBatchAllowed) {
                            addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                        }
                        BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : External txn number already exist CHANNEL TRF", "");
                        continue;
                    }
                }// external txn number uniqueness check ends here

                // load the product's informaiton.
                if (transferRuleNotExistMap.get(batchItemsVO.getCategoryCode()) == null) {
                    if (transferRuleProdNotExistMap.get(batchItemsVO.getCategoryCode()) == null) {
                        if (transferRuleMap.get(batchItemsVO.getCategoryCode()) == null) {
                            index = 0;
                            ++index;
                            pstmtSelectTrfRule.setString(index, p_batchMasterVO.getNetworkCode());
                            ++index;
                            pstmtSelectTrfRule.setString(index, p_batchMasterVO.getDomainCode());
                            ++index;
                            pstmtSelectTrfRule.setString(index, batchItemsVO.getCategoryCode());
                            rsSelectTrfRule = pstmtSelectTrfRule.executeQuery();
                            pstmtSelectTrfRule.clearParameters();
                            if (rsSelectTrfRule.next()) {
                                rulesVO = new ChannelTransferRuleVO();
                                rulesVO.setTransferRuleID(rsSelectTrfRule.getString("transfer_rule_id"));
                                rulesVO.setFocTransferType(rsSelectTrfRule.getString("foc_transfer_type"));
                                rulesVO.setFocAllowed(rsSelectTrfRule.getString("foc_allowed"));
                                index = 0;
                                ++index;
                                pstmtSelectTrfRuleProd.setString(index, rulesVO.getTransferRuleID());
                                ++index;
                                pstmtSelectTrfRuleProd.setString(index, p_batchMasterVO.getProductCode());
                                rsSelectTrfRuleProd = pstmtSelectTrfRuleProd.executeQuery();
                                pstmtSelectTrfRuleProd.clearParameters();
                                if (!rsSelectTrfRuleProd.next()) {
                                    transferRuleProdNotExistMap.put(batchItemsVO.getCategoryCode(), batchItemsVO.getCategoryCode());
                                    // put error log Prodcuct is not in the
                                    // transfer rule
                                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                                        "batchfoc.initiatebatchfoctransfer.msg.error.prodnotintrfrule"));
                                    errorList.add(errorVO);
                                    // Handling the failed transaction details
                                    // for generating the Excel file
                                    if (isPartialBatchAllowed) {
                                        addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                                    }
                                    BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : Product is not in the transfer rule", "");
                                    continue;
                                }
                                transferRuleMap.put(batchItemsVO.getCategoryCode(), rulesVO);
                            } else {
                                transferRuleNotExistMap.put(batchItemsVO.getCategoryCode(), batchItemsVO.getCategoryCode());
                                // put error log transfer rule not defined
                                errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()),PretupsRestUtil.getMessageString(
                                    "batchfoc.initiatebatchfoctransfer.msg.error.trfrulenotdefined"));
                                errorList.add(errorVO);
                                // Handling the failed transaction details for
                                // generating the Excel file
                                if (isPartialBatchAllowed) {
                                    addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                                }
                                BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : Transfer rule not defined", "");
                                continue;
                            }
                        }// transfer rule loading
                    }// Procuct is not associated with transfer rule not defined
                     // check
                    else {
                        // put error log Procuct is not in the transfer rule
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchfoc.initiatebatchfoctransfer.msg.error.prodnotintrfrule"));
                        errorList.add(errorVO);
                        // Handling the failed transaction details for
                        // generating the Excel file
                        if (isPartialBatchAllowed) {
                            addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                        }
                        BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : Product is not in the transfer rule", "");
                        continue;
                    }
                }// transfer rule not defined check
                else {
                    // put error log transfer rule not defined
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.initiatebatchfoctransfer.msg.error.trfrulenotdefined"));
                    errorList.add(errorVO);
                    // Handling the failed transaction details for generating
                    // the Excel file
                    if (isPartialBatchAllowed) {
                        addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                    }
                    BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : Transfer rule not defined", "");
                    continue;
                }
                rulesVO = (ChannelTransferRuleVO) transferRuleMap.get(batchItemsVO.getCategoryCode());
                if (PretupsI.NO.equals(rulesVO.getFocAllowed())) {
                    // put error according to the transfer rule FOC transfer is
                    // not allowed.
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.initiatebatchfoctransfer.msg.error.focnotallowed"));
                    errorList.add(errorVO);
                    // Handling the failed transaction details for generating
                    // the Excel file
                    if (isPartialBatchAllowed) {
                        addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                    }
                    BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : According to the transfer rule FOC transfer is not allowed", "");
                    continue;
                }
                // check the transfer profile product code

                // transfer profile check ends here
                if (transferProfileMap.get(batchItemsVO.getTxnProfile()) == null) {
                    index = 0;
                    ++index;
                    pstmtSelectTProfileProd.setString(index, batchItemsVO.getTxnProfile());
                    ++index;
                    pstmtSelectTProfileProd.setString(index, p_batchMasterVO.getProductCode());
                    ++index;
                    pstmtSelectTProfileProd.setString(index, PretupsI.PARENT_PROFILE_ID_CATEGORY);
                    rsSelectTProfileProd = pstmtSelectTProfileProd.executeQuery();
                    pstmtSelectTProfileProd.clearParameters();
                    if (!rsSelectTProfileProd.next()) {
                        transferProfileMap.put(batchItemsVO.getTxnProfile(), "false");
                        // put error Transfer profile for this product is not
                        // define
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchfoc.initiatebatchfoctransfer.msg.error.trfprofilenotdefined"));
                        errorList.add(errorVO);
                        // Handling the failed transaction details for
                        // generating the Excel file
                        if (isPartialBatchAllowed) {
                            addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                        }
                        BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : Transfer profile for this product is not defined", "");
                        continue;
                    }
                    transferProfileMap.put(batchItemsVO.getTxnProfile(), "true");
                } else {

                    if ("false".equals(transferProfileMap.get(batchItemsVO.getTxnProfile()))) {
                        // put error Transfer profile for this product is not
                        // define
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchfoc.initiatebatchfoctransfer.msg.error.trfprofilenotdefined"));
                        errorList.add(errorVO);
                        // Handling the failed transaction details for
                        // generating the Excel file
                        if (isPartialBatchAllowed) {
                            addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                        }
                        BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : Transfer profile for this product is not defined", "");
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
                if(isTransactionTypeAlwd)
                {
                	++index;
                	pstmtSelectCProfileProd.setString(index, PretupsI.TRANSFER_TYPE_O2C);
                	++index;
                	pstmtSelectCProfileProd.setString(index, PretupsI.ALL);
                }
                else
                {
                	++index;
                	pstmtSelectCProfileProd.setString(index, PretupsI.ALL);
                }
                ++index;
                pstmtSelectCProfileProd.setString(index, PretupsI.ALL);
                rsSelectCProfileProd = pstmtSelectCProfileProd.executeQuery();
                pstmtSelectCProfileProd.clearParameters();
                if (!rsSelectCProfileProd.next()) {
                    // put error commission profile for this product is not
                    // defined
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.initiatebatchfoctransfer.msg.error.commprfnotdefined"));
                    errorList.add(errorVO);
                    // Handling the failed transaction details for generating
                    // the Excel file
                    if (isPartialBatchAllowed) {
                        addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                    }
                    BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : Commission profile for product & transactiontype combination is not defined", "");
                    continue;
                }
                requestedValue = batchItemsVO.getRequestedQuantity();
                minTrfValue = rsSelectCProfileProd.getLong("min_transfer_value");
                maxTrfValue = rsSelectCProfileProd.getLong("max_transfer_value");
                if (minTrfValue > requestedValue || maxTrfValue < requestedValue) {
                    msgArr = new String[3];
                    msgArr[0] = PretupsBL.getDisplayAmount(requestedValue);
                    msgArr[1] = PretupsBL.getDisplayAmount(minTrfValue);
                    msgArr[2] = PretupsBL.getDisplayAmount(maxTrfValue);
                    // put error requested quantity is not between min and max
                    // values
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.initiatebatchfoctransfer.msg.error.qtymaxmin", msgArr));
                    msgArr = null;
                    errorList.add(errorVO);
                    // Handling the failed transaction details for generating
                    // the Excel file
                    if (isPartialBatchAllowed) {
                        addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                    }
                    BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : Requested quantity is not between min and max values",
                        "minTrfValue=" + minTrfValue + ", maxTrfValue=" + maxTrfValue);
                    continue;
                }
                multipleOf = rsSelectCProfileProd.getLong("transfer_multiple_off");
                if (requestedValue % multipleOf != 0) {
                    // put error requested quantity is not multiple of
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.initiatebatchfoctransfer.msg.error.notmulof", new String[] { PretupsBL.getDisplayAmount(multipleOf) }));
                    // Handling the failed transaction details for generating
                    // the Excel file
                    if (isPartialBatchAllowed) {
                        addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                    }
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : Requested quantity is not in multiple value",
                        "multiple of=" + multipleOf);
                    continue;
                }

                index = 0;
                ++index;
                pstmtSelectCProfileProdDetail.setString(index, rsSelectCProfileProd.getString("comm_profile_products_id"));
                ++index;
                pstmtSelectCProfileProdDetail.setLong(index, requestedValue);
                ++index;
                pstmtSelectCProfileProdDetail.setLong(index, requestedValue);
                rsSelectCProfileProdDetail = pstmtSelectCProfileProdDetail.executeQuery();
                pstmtSelectCProfileProdDetail.clearParameters();
                if (!rsSelectCProfileProdDetail.next()) {
                    // put error commission profile slab is not define for the
                    // requested value
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.initiatebatchfoctransfer.msg.error.commslabnotdefined"));
                    // Handling the failed transaction details for generating
                    // the Excel file
                    if (isPartialBatchAllowed) {
                        addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                    }
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : Commission profile slab is not define for the requested value", "");
                    continue;
                }
                // to calculate tax
                transferItemsList = new ArrayList();
                channelTransferItemsVO = new ChannelTransferItemsVO();
                // this value will be inserted into the table as the requested
                // qty
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

                if (PretupsI.YES.equals(rsSelectCProfileProd.getString("taxes_on_foc_applicable"))) {
                    channelTransferItemsVO.setTaxOnFOCTransfer(PretupsI.YES);
                } else {
                    channelTransferItemsVO.setTaxOnFOCTransfer(PretupsI.NO);
                }

                transferItemsList.add(channelTransferItemsVO);
                // make a new channel TransferVO to transfer into the method
                // during tax calculataion
                final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
                channelTransferVO.setChannelTransferitemsVOList(transferItemsList);
                channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN);

                ChannelTransferBL.calculateMRPWithTaxAndDiscount(channelTransferVO, PretupsI.TRANSFER_TYPE_FOC);

                // taxes on FOC required
                // ends commission profile validaiton

                // insert items data here
                index = 0;
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getBatchId());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getBatchDetailId());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getCategoryCode());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getMsisdn());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getUserId());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getStatus());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getModifiedBy());
                ++index;
                pstmtInsertBatchItems.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchItemsVO.getModifiedOn()));
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getUserGradeCode());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getExtTxnNo());
                ++index;
                pstmtInsertBatchItems.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchItemsVO.getExtTxnDate()));
                ++index;
                pstmtInsertBatchItems.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchItemsVO.getTransferDate()));
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getTxnProfile());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getCommissionProfileSetId());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getCommissionProfileVer());
                ++index;
                pstmtInsertBatchItems.setString(index, channelTransferItemsVO.getCommProfileDetailID());
                ++index;
                pstmtInsertBatchItems.setString(index, channelTransferItemsVO.getCommType());
                ++index;
                pstmtInsertBatchItems.setDouble(index, channelTransferItemsVO.getCommRate());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getCommValue());
                ++index;
                pstmtInsertBatchItems.setString(index, channelTransferItemsVO.getTax1Type());
                ++index;
                pstmtInsertBatchItems.setDouble(index, channelTransferItemsVO.getTax1Rate());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getTax1Value());
                ++index;
                pstmtInsertBatchItems.setString(index, channelTransferItemsVO.getTax2Type());
                ++index;
                pstmtInsertBatchItems.setDouble(index, channelTransferItemsVO.getTax2Rate());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getTax2Value());
                ++index;
                pstmtInsertBatchItems.setString(index, channelTransferItemsVO.getTax3Type());
                ++index;
                pstmtInsertBatchItems.setDouble(index, channelTransferItemsVO.getTax3Rate());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getTax3Value());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getRequiredQuantity());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getProductTotalMRP());
                // commented for DB2
                // pstmtInsertBatchItems.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getInitiatorRemarks());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getExternalCode());
                ++index;
                pstmtInsertBatchItems.setString(index, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_ITEM_RCRDSTATUS_PROCESSED);

                /** START: Birendra: 29JAN2015 */
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getWalletCode());
                /** STOP: Birendra: 29JAN2015 */
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getDualCommissionType());
                queryExecutionCount = pstmtInsertBatchItems.executeUpdate();
                if (queryExecutionCount <= 0) {
                    p_con.rollback();
                    // put error record can not be inserted
                    _log.error(methodName, "Record cannot be inserted in batch items table");
                    BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : DB Error Record cannot be inserted in batch items table",
                        "queryExecutionCount=" + queryExecutionCount);
                } else {
                    p_con.commit();
                    totalSuccessRecords++;
                    // put success in the logger file.
                    BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "PASS : Record inserted successfully in batch items table",
                        "queryExecutionCount=" + queryExecutionCount);
                }
                // ends here

            }// for loop for the batch items
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[initiateBatchFOCTransfer]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            BatchFocFileProcessLog
                .focBatchMasterLog(methodName, p_batchMasterVO, "FAIL : SQL Exception:" + sqe.getMessage(), "TOTAL SUCCESS RECORDS = " + totalSuccessRecords);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[initiateBatchFOCTransfer]", "",
                "", "", "Exception:" + ex.getMessage());
            BatchFocFileProcessLog.focBatchMasterLog(methodName, p_batchMasterVO, "FAIL : Exception:" + ex.getMessage(), "TOTAL SUCCESS RECORDS = " + totalSuccessRecords);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rsSelectExtTxnID1 != null) {
                    rsSelectExtTxnID1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectExtTxnID1 != null) {
                    pstmtSelectExtTxnID1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectExtTxnID2 != null) {
                    rsSelectExtTxnID2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectExtTxnID2 != null) {
                    pstmtSelectExtTxnID2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectTrfRule != null) {
                    rsSelectTrfRule.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectTrfRule != null) {
                    pstmtSelectTrfRule.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectTrfRuleProd != null) {
                    rsSelectTrfRuleProd.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectTrfRuleProd != null) {
                    pstmtSelectTrfRuleProd.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectCProfileProd != null) {
                    rsSelectCProfileProd.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectCProfileProd != null) {
                    pstmtSelectCProfileProd.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectCProfileProdDetail != null) {
                    rsSelectCProfileProdDetail.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectCProfileProdDetail != null) {
                    pstmtSelectCProfileProdDetail.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectTProfileProd != null) {
                    rsSelectTProfileProd.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectTProfileProd != null) {
                    pstmtSelectTProfileProd.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertBatchMaster != null) {
                    pstmtInsertBatchMaster.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertBatchGeo != null) {
                    pstmtInsertBatchGeo.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertBatchItems != null) {
                    pstmtInsertBatchItems.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {

                // if all records contains errors then rollback the master table
                // entry
                if (errorList != null && (errorList.size() == p_batchItemsList.size())) {
                    p_con.rollback();
                    _log.error(methodName, "ALL the records conatins errors and cannot be inserted in db");
                    BatchFocFileProcessLog.focBatchMasterLog(methodName, p_batchMasterVO, "FAIL : ALL the records conatins errors and cannot be inserted in DB ", "");
                }
                // else update the master table with the open status and total
                // number of records.
                else {
                    int index = 0;
                    int queryExecutionCount = -1;
                    ++index;
                    pstmtUpdateBatchMaster.setInt(index, p_batchMasterVO.getBatchTotalRecord() - errorList.size());
                    ++index;
                    pstmtUpdateBatchMaster.setString(index, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_OPEN);
                    ++index;
                    pstmtUpdateBatchMaster.setString(index, p_batchMasterVO.getWallet_type());
                    ++index;
                    pstmtUpdateBatchMaster.setString(index, p_batchMasterVO.getBatchId());
                    queryExecutionCount = pstmtUpdateBatchMaster.executeUpdate();
                    if (queryExecutionCount <= 0) // Means No Records Updated
                    {
                        _log.error(methodName, "Unable to Update the batch size in master table..");
                        p_con.rollback();
                        EventHandler
                            .handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[initiateBatchFOCTransfer]",
                                "", "", "", "Error while updating FOC_BATCHES table. Batch id=" + p_batchMasterVO.getBatchId());
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
                    _log.errorTrace(methodName, ex);
                }
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateBatchMaster != null) {
                    pstmtUpdateBatchMaster.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: errorList.size()=" + errorList.size());
            }
        }
        return errorList;
    }
    
    
    
    
    
    
    
    
    /**
     * Method initiateBatchFOCTransfer
     * This method used for the batch foc order initiation. The main purpose of
     * this method is to insert the
     * records in foc_batches,foc_batch_geographies & foc_batch_items table.
     * 
     * @param p_con
     *            Connection
     * @param p_batchMasterVO
     *            FOCBatchMasterVO
     * @param p_batchItemsList
     *            ArrayList
     * @param p_messages
     *            MessageResources
     * @param p_locale
     *            Locale
     * @return errorList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList initiateBatchFOCTransfer(Connection p_con, FOCBatchMasterVO p_batchMasterVO, ArrayList p_batchItemsList, MessageResources p_messages, Locale p_locale, ArrayList<FocListValueVO> arrayFocListValueVO) throws BTSLBaseException {
        final String methodName = "initiateBatchFOCTransfer";
        if (_log.isDebugEnabled()) {
            _log.debug(
                methodName,
                "Entered.... p_batchMasterVO=" + p_batchMasterVO + ", p_batchItemsList.size() = " + p_batchItemsList.size() + ", p_batchItemsList=" + p_batchItemsList + "p_locale=" + p_locale);
        }
        Boolean isExternalTxnUnique = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE);
        Boolean isPartialBatchAllowed = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_PARTIAL_BATCH_ALLOWED);
        Boolean isTransactionTypeAlwd = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD);
        final ArrayList errorList = new ArrayList();
        ListValueVO errorVO = null;
        // for uniqueness of the external Txn ID
        PreparedStatement pstmtSelectExtTxnID1 = null;
        ResultSet rsSelectExtTxnID1 = null;
        final StringBuffer strBuffSelectExtTxnID1 = new StringBuffer(" SELECT 1 FROM foc_batch_items ");
        strBuffSelectExtTxnID1.append("WHERE ext_txn_no=? AND status <> ?  ");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffSelectExtTxnID1 Query =" + strBuffSelectExtTxnID1);
        }

        PreparedStatement pstmtSelectExtTxnID2 = null;
        ResultSet rsSelectExtTxnID2 = null;
        final StringBuffer strBuffSelectExtTxnID2 = new StringBuffer(" SELECT 1 FROM channel_transfers ");
        strBuffSelectExtTxnID2.append("WHERE type=? AND ext_txn_no=? AND status <> ? ");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffSelectExtTxnID2 Query =" + strBuffSelectExtTxnID2);
            // ends here
        }

        // for loading the O2C transfer rule for FOC transfer
        PreparedStatement pstmtSelectTrfRule = null;
        ResultSet rsSelectTrfRule = null;
        final StringBuffer strBuffSelectTrfRule = new StringBuffer(" SELECT transfer_rule_id,foc_transfer_type, foc_allowed ");
        strBuffSelectTrfRule.append("FROM chnl_transfer_rules WHERE network_code = ? AND domain_code = ? AND ");
        strBuffSelectTrfRule.append("from_category = 'OPT' AND to_category = ? AND status = 'Y' AND type = 'OPT' ");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffSelectTrfRule Query =" + strBuffSelectTrfRule);
            // ends here
        }

        // for loading the products associated with the transfer rule
        PreparedStatement pstmtSelectTrfRuleProd = null;
        ResultSet rsSelectTrfRuleProd = null;
        final StringBuffer strBuffSelectTrfRuleProd = new StringBuffer("SELECT 1 FROM chnl_transfer_rules_products ");
        strBuffSelectTrfRuleProd.append("WHERE transfer_rule_id=?  AND product_code = ? ");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffSelectTrfRuleProd Query =" + strBuffSelectTrfRuleProd);
            // ends here
        }

        // for loading the products associated with the commission profile
        PreparedStatement pstmtSelectCProfileProd = null;
        ResultSet rsSelectCProfileProd = null;
        final StringBuffer strBuffSelectCProfileProd = new StringBuffer("SELECT cp.min_transfer_value,cp.max_transfer_value,cp.discount_type,cp.discount_rate, ");
        strBuffSelectCProfileProd.append("cp.comm_profile_products_id, cp.transfer_multiple_off, cp.taxes_on_foc_applicable  ");
        strBuffSelectCProfileProd.append("FROM commission_profile_products cp ");
        strBuffSelectCProfileProd.append("WHERE cp.product_code = ? AND cp.comm_profile_set_id = ? AND cp.comm_profile_set_version = ? ");
        //strBuffSelectCProfileProd.append("AND cp.transaction_type = ? AND cp.payment_mode = ? ");
        if(isTransactionTypeAlwd)
            strBuffSelectCProfileProd.append("AND cp.transaction_type in (?,?) ");
            else
            	strBuffSelectCProfileProd.append("AND cp.transaction_type = ? ");
            strBuffSelectCProfileProd.append("AND cp.payment_mode = ? ORDER BY cp.TRANSACTION_TYPE desc");
          
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffSelectCProfileProd Query =" + strBuffSelectCProfileProd+",isTransactionTypeAlwd="+isTransactionTypeAlwd);
        }

        PreparedStatement pstmtSelectCProfileProdDetail = null;
        ResultSet rsSelectCProfileProdDetail = null;
        final StringBuffer strBuffSelectCProfileProdDetail = new StringBuffer("SELECT cpd.tax1_type,cpd.tax1_rate,cpd.tax2_type,cpd.tax2_rate, ");
        strBuffSelectCProfileProdDetail.append("cpd.tax3_type,cpd.tax3_rate,cpd.commission_type,cpd.commission_rate,cpd.comm_profile_detail_id ");
        strBuffSelectCProfileProdDetail.append("FROM commission_profile_details cpd ");
        strBuffSelectCProfileProdDetail.append("WHERE  cpd.comm_profile_products_id = ? AND cpd.start_range <= ? AND cpd.end_range >= ? ");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffSelectCProfileProdDetail Query =" + strBuffSelectCProfileProdDetail);
            // ends here
        }

        // for existance of the product in the transfer profile
        PreparedStatement pstmtSelectTProfileProd = null;
        ResultSet rsSelectTProfileProd = null;
        final StringBuffer strBuffSelectTProfileProd = new StringBuffer(" SELECT 1 ");
        strBuffSelectTProfileProd.append("FROM transfer_profile_products tpp,transfer_profile tp, transfer_profile catp,transfer_profile_products catpp ");
        strBuffSelectTProfileProd.append("WHERE tpp.profile_id=? AND tpp.product_code = ? AND tpp.profile_id=tp.profile_id AND catp.profile_id=catpp.profile_id ");
        strBuffSelectTProfileProd
            .append("AND tpp.product_code=catpp.product_code AND tp.category_code=catp.category_code AND catp.parent_profile_id=? AND catp.status='Y' AND tp.network_code = catp.network_code");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffSelectTProfileProd Query =" + strBuffSelectTProfileProd);
            // ends here
        }

        // insert data in the batch master table
        // commented for DB2 OraclePreparedStatement pstmtInsertBatchMaster =
        // null;
        PreparedStatement pstmtInsertBatchMaster = null;
        final StringBuffer strBuffInsertBatchMaster = new StringBuffer("INSERT INTO foc_batches (batch_id, network_code, ");
        strBuffInsertBatchMaster.append("network_code_for, batch_name, status, domain_code, product_code, ");
        strBuffInsertBatchMaster.append("batch_file_name, batch_total_record, batch_date, created_by, created_on, ");
        strBuffInsertBatchMaster
            .append(" modified_by, modified_on,sms_default_lang,sms_second_lang,transfer_type,transfer_sub_type) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffInsertBatchMaster Query =" + strBuffInsertBatchMaster);
            // ends here
        }

        // insert data in the batch Geographies table
        PreparedStatement pstmtInsertBatchGeo = null;
        final StringBuffer strBuffInsertBatchGeo = new StringBuffer("INSERT INTO foc_batch_geographies(batch_id,geography_code,date_time) VALUES (?,?,?)");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffInsertBatchGeo Query =" + strBuffInsertBatchGeo);
            // ends here
        }

        // insert data in the batch items table
        // commented for DB2OraclePreparedStatement pstmtInsertBatchItems =
        // null;
        PreparedStatement pstmtInsertBatchItems = null;
        final StringBuffer strBuffInsertBatchItems = new StringBuffer("INSERT INTO foc_batch_items (batch_id, batch_detail_id, ");
        strBuffInsertBatchItems.append("category_code, msisdn, user_id, status, modified_by, modified_on, user_grade_code, ");
        strBuffInsertBatchItems.append("ext_txn_no, ext_txn_date, transfer_date, txn_profile, ");
        strBuffInsertBatchItems.append("commission_profile_set_id, commission_profile_ver, commission_profile_detail_id, ");
        strBuffInsertBatchItems.append("commission_type, commission_rate, commission_value, tax1_type, tax1_rate, ");
        strBuffInsertBatchItems.append("tax1_value, tax2_type, tax2_rate, tax2_value, tax3_type, tax3_rate, ");
        strBuffInsertBatchItems.append("tax3_value, requested_quantity, transfer_mrp, initiator_remarks, external_code,rcrd_status");

        /** START: Birendra: 29JAN2015 */
        strBuffInsertBatchItems.append(", user_wallet");
        /** STOP: Birendra: 29JAN2015 */
         
        strBuffInsertBatchItems.append(", dual_comm_type");
        
        strBuffInsertBatchItems.append(") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");
        /** START: Birendra: 29JAN2015 */
        strBuffInsertBatchItems.append(",?)");
        
        
        /** START: Birendra: 29JAN2015 */
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffInsertBatchItems Query = " + strBuffInsertBatchItems);
            // ends here
        }

        // update master table with OPEN status
        PreparedStatement pstmtUpdateBatchMaster = null;
        final StringBuffer strBuffUpdateBatchMaster = new StringBuffer("UPDATE foc_batches SET batch_total_record=? , status =? WHERE batch_id=?");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "strBuffUpdateBatchMaster Query =" + strBuffUpdateBatchMaster);
        }
        int totalSuccessRecords = 0;
        try {
            pstmtSelectExtTxnID1 = p_con.prepareStatement(strBuffSelectExtTxnID1.toString());
            pstmtSelectExtTxnID2 = p_con.prepareStatement(strBuffSelectExtTxnID2.toString());
            pstmtSelectTrfRule = p_con.prepareStatement(strBuffSelectTrfRule.toString());
            pstmtSelectTrfRuleProd = p_con.prepareStatement(strBuffSelectTrfRuleProd.toString());
            pstmtSelectCProfileProd = p_con.prepareStatement(strBuffSelectCProfileProd.toString());
            pstmtSelectCProfileProdDetail = p_con.prepareStatement(strBuffSelectCProfileProdDetail.toString());
            pstmtSelectTProfileProd = p_con.prepareStatement(strBuffSelectTProfileProd.toString());

            // pstmtInsertBatchMaster=(OraclePreparedStatement)p_con.prepareStatement(strBuffInsertBatchMaster.toString());
            pstmtInsertBatchMaster = p_con.prepareStatement(strBuffInsertBatchMaster.toString());
            pstmtInsertBatchGeo = p_con.prepareStatement(strBuffInsertBatchGeo.toString());

            // pstmtInsertBatchItems=(OraclePreparedStatement)p_con.prepareStatement(strBuffInsertBatchItems.toString());
            pstmtInsertBatchItems = p_con.prepareStatement(strBuffInsertBatchItems.toString());
            pstmtUpdateBatchMaster = p_con.prepareStatement(strBuffUpdateBatchMaster.toString());
            ChannelTransferRuleVO rulesVO = null;
            int index = 0;
            FOCBatchItemsVO batchItemsVO = null;

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
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getBatchId());
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getNetworkCode());
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getNetworkCodeFor());

            // commented for DB2
            // pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getBatchName());
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getStatus());
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getDomainCode());
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getProductCode());
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getBatchFileName());
            ++index;
            pstmtInsertBatchMaster.setLong(index, p_batchMasterVO.getBatchTotalRecord());
            ++index;
            pstmtInsertBatchMaster.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(p_batchMasterVO.getBatchDate()));
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getCreatedBy());
            ++index;
            pstmtInsertBatchMaster.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(p_batchMasterVO.getCreatedOn()));
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getModifiedBy());
            ++index;
            pstmtInsertBatchMaster.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(p_batchMasterVO.getModifiedOn()));

            // commented for
            // DB2pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getDefaultLang());
            // commented for
            // DB2pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);
            ++index;
            pstmtInsertBatchMaster.setString(index, p_batchMasterVO.getSecondLang());
            ++index;
            pstmtInsertBatchMaster.setString(index, PretupsI.TRANSFER_TYPE_FOC);
            ++index;
            pstmtInsertBatchMaster.setString(index, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
            int queryExecutionCount = pstmtInsertBatchMaster.executeUpdate();
            if (queryExecutionCount <= 0) {
                p_con.rollback();
                _log.error(methodName, "Unable to insert in the batch master table.");
                BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : DB Error Unable to insert in the batch master table",
                    "queryExecutionCount=" + queryExecutionCount);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[initiateBatchFOCTransfer]",
                    "", "", "", "Unable to insert in the batch master table.");
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
            // ends here
            // insert batch geographics
            ListValueVO listValueVO = null;
            final int size = p_batchMasterVO.getGeographyList().size();
            for (int i = 0; i < size; i++) {
                index = 0;
                listValueVO = (ListValueVO) p_batchMasterVO.getGeographyList().get(i);
                ++index;
                pstmtInsertBatchGeo.setString(index, p_batchMasterVO.getBatchId());
                ++index;
                pstmtInsertBatchGeo.setString(index, listValueVO.getValue());
                // Added on 07/02/08
                ++index;
                pstmtInsertBatchGeo.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(p_batchMasterVO.getBatchDate()));
                queryExecutionCount = pstmtInsertBatchGeo.executeUpdate();
                if (queryExecutionCount <= 0) {
                    p_con.rollback();
                    _log.error(methodName, "Unable to insert in the batch geographics table.");
                    BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : DB Error Unable to insert in the batch geographics table",
                        "queryExecutionCount=" + queryExecutionCount);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                        "FOCBatchTransferWebDAO[initiateBatchFOCTransfer]", "", "", "", "Unable to insert in the batch geographics table.");
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
                pstmtInsertBatchGeo.clearParameters();
            }
            // ends here
            String msgArr[] = null;
            for (int i = 0, j = p_batchItemsList.size(); i < j; i++) {
                batchItemsVO = (FOCBatchItemsVO) p_batchItemsList.get(i);
                // check the uniqueness of the external txn number
                if (!BTSLUtil.isNullString(batchItemsVO.getExtTxnNo()) && isExternalTxnUnique) {
                    index = 0;
                    ++index;
                    pstmtSelectExtTxnID1.setString(index, batchItemsVO.getExtTxnNo());
                    ++index;
                    pstmtSelectExtTxnID1.setString(index, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    rsSelectExtTxnID1 = pstmtSelectExtTxnID1.executeQuery();
                    pstmtSelectExtTxnID1.clearParameters();
                    if (rsSelectExtTxnID1.next()) {
                        // put error external txn number already exist
                        errorVO = new ListValueVO();
                        errorVO.setCodeName(batchItemsVO.getMsisdn());
                        errorVO.setOtherInfo(String.valueOf(batchItemsVO.getRecordNumber()));
                        errorVO.setOtherInfo2(p_messages.getMessage(p_locale, "batchfoc.initiatebatchfoctransfer.msg.error.exttxnalreadyexists"));
                        errorList.add(errorVO);
                        // Handling the failed transaction details for
                        // generating the Excel file
                        if (isPartialBatchAllowed) {
                            addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                        }
                        BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : External txn number already exist FOC BATCCH", "");
                        continue;
                    }
                    index = 0;
                    ++index;
                    pstmtSelectExtTxnID2.setString(index, PretupsI.CHANNEL_TYPE_O2C);
                    ++index;
                    pstmtSelectExtTxnID2.setString(index, batchItemsVO.getExtTxnNo());
                    ++index;
                    pstmtSelectExtTxnID2.setString(index, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    rsSelectExtTxnID2 = pstmtSelectExtTxnID2.executeQuery();
                    pstmtSelectExtTxnID2.clearParameters();
                    if (rsSelectExtTxnID2.next()) {
                        // put error external txn number already exist
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.initiatebatchfoctransfer.msg.error.exttxnalreadyexists"));
                        errorList.add(errorVO);
                        // Handling the failed transaction details for
                        // generating the Excel file
                        if (isPartialBatchAllowed) {
                            addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                        }
                        BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : External txn number already exist CHANNEL TRF", "");
                        continue;
                    }
                }// external txn number uniqueness check ends here

                // load the product's informaiton.
                if (transferRuleNotExistMap.get(batchItemsVO.getCategoryCode()) == null) {
                    if (transferRuleProdNotExistMap.get(batchItemsVO.getCategoryCode()) == null) {
                        if (transferRuleMap.get(batchItemsVO.getCategoryCode()) == null) {
                            index = 0;
                            ++index;
                            pstmtSelectTrfRule.setString(index, p_batchMasterVO.getNetworkCode());
                            ++index;
                            pstmtSelectTrfRule.setString(index, p_batchMasterVO.getDomainCode());
                            ++index;
                            pstmtSelectTrfRule.setString(index, batchItemsVO.getCategoryCode());
                            rsSelectTrfRule = pstmtSelectTrfRule.executeQuery();
                            pstmtSelectTrfRule.clearParameters();
                            if (rsSelectTrfRule.next()) {
                                rulesVO = new ChannelTransferRuleVO();
                                rulesVO.setTransferRuleID(rsSelectTrfRule.getString("transfer_rule_id"));
                                rulesVO.setFocTransferType(rsSelectTrfRule.getString("foc_transfer_type"));
                                rulesVO.setFocAllowed(rsSelectTrfRule.getString("foc_allowed"));
                                index = 0;
                                ++index;
                                pstmtSelectTrfRuleProd.setString(index, rulesVO.getTransferRuleID());
                                ++index;
                                pstmtSelectTrfRuleProd.setString(index, p_batchMasterVO.getProductCode());
                                rsSelectTrfRuleProd = pstmtSelectTrfRuleProd.executeQuery();
                                pstmtSelectTrfRuleProd.clearParameters();
                                if (!rsSelectTrfRuleProd.next()) {
                                    transferRuleProdNotExistMap.put(batchItemsVO.getCategoryCode(), batchItemsVO.getCategoryCode());
                                    // put error log Prodcuct is not in the
                                    // transfer rule
                                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                        "batchfoc.initiatebatchfoctransfer.msg.error.prodnotintrfrule"));
                                    errorList.add(errorVO);
                                    // Handling the failed transaction details
                                    // for generating the Excel file
                                    if (isPartialBatchAllowed) {
                                        addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                                    }
                                    BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : Product is not in the transfer rule", "");
                                    continue;
                                }
                                transferRuleMap.put(batchItemsVO.getCategoryCode(), rulesVO);
                            } else {
                                transferRuleNotExistMap.put(batchItemsVO.getCategoryCode(), batchItemsVO.getCategoryCode());
                                // put error log transfer rule not defined
                                errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                                    "batchfoc.initiatebatchfoctransfer.msg.error.trfrulenotdefined"));
                                errorList.add(errorVO);
                                // Handling the failed transaction details for
                                // generating the Excel file
                                if (isPartialBatchAllowed) {
                                    addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                                }
                                BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : Transfer rule not defined", "");
                                continue;
                            }
                        }// transfer rule loading
                    }// Procuct is not associated with transfer rule not defined
                     // check
                    else {
                        // put error log Procuct is not in the transfer rule
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.initiatebatchfoctransfer.msg.error.prodnotintrfrule"));
                        errorList.add(errorVO);
                        // Handling the failed transaction details for
                        // generating the Excel file
                        if (isPartialBatchAllowed) {
                            addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                        }
                        BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : Product is not in the transfer rule", "");
                        continue;
                    }
                }// transfer rule not defined check
                else {
                    // put error log transfer rule not defined
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.initiatebatchfoctransfer.msg.error.trfrulenotdefined"));
                    errorList.add(errorVO);
                    // Handling the failed transaction details for generating
                    // the Excel file
                    if (isPartialBatchAllowed) {
                        addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                    }
                    BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : Transfer rule not defined", "");
                    continue;
                }
                rulesVO = (ChannelTransferRuleVO) transferRuleMap.get(batchItemsVO.getCategoryCode());
                if (PretupsI.NO.equals(rulesVO.getFocAllowed())) {
                    // put error according to the transfer rule FOC transfer is
                    // not allowed.
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.initiatebatchfoctransfer.msg.error.focnotallowed"));
                    errorList.add(errorVO);
                    // Handling the failed transaction details for generating
                    // the Excel file
                    if (isPartialBatchAllowed) {
                        addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                    }
                    BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : According to the transfer rule FOC transfer is not allowed", "");
                    continue;
                }
                // check the transfer profile product code

                // transfer profile check ends here
                if (transferProfileMap.get(batchItemsVO.getTxnProfile()) == null) {
                    index = 0;
                    ++index;
                    pstmtSelectTProfileProd.setString(index, batchItemsVO.getTxnProfile());
                    ++index;
                    pstmtSelectTProfileProd.setString(index, p_batchMasterVO.getProductCode());
                    ++index;
                    pstmtSelectTProfileProd.setString(index, PretupsI.PARENT_PROFILE_ID_CATEGORY);
                    rsSelectTProfileProd = pstmtSelectTProfileProd.executeQuery();
                    pstmtSelectTProfileProd.clearParameters();
                    if (!rsSelectTProfileProd.next()) {
                        transferProfileMap.put(batchItemsVO.getTxnProfile(), "false");
                        // put error Transfer profile for this product is not
                        // define
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.initiatebatchfoctransfer.msg.error.trfprofilenotdefined"));
                        errorList.add(errorVO);
                        // Handling the failed transaction details for
                        // generating the Excel file
                        if (isPartialBatchAllowed) {
                            addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                        }
                        BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : Transfer profile for this product is not defined", "");
                        continue;
                    }
                    transferProfileMap.put(batchItemsVO.getTxnProfile(), "true");
                } else {

                    if ("false".equals(transferProfileMap.get(batchItemsVO.getTxnProfile()))) {
                        // put error Transfer profile for this product is not
                        // define
                        errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                            "batchfoc.initiatebatchfoctransfer.msg.error.trfprofilenotdefined"));
                        errorList.add(errorVO);
                        // Handling the failed transaction details for
                        // generating the Excel file
                        if (isPartialBatchAllowed) {
                            addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                        }
                        BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : Transfer profile for this product is not defined", "");
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
                if(isTransactionTypeAlwd)
                {
                	++index;
                	pstmtSelectCProfileProd.setString(index, PretupsI.TRANSFER_TYPE_O2C);
                	++index;
                	pstmtSelectCProfileProd.setString(index, PretupsI.ALL);
                }
                else
                {
                	++index;
                	pstmtSelectCProfileProd.setString(index, PretupsI.ALL);
                }
                ++index;
                pstmtSelectCProfileProd.setString(index, PretupsI.ALL);
                rsSelectCProfileProd = pstmtSelectCProfileProd.executeQuery();
                pstmtSelectCProfileProd.clearParameters();
                if (!rsSelectCProfileProd.next()) {
                    // put error commission profile for this product is not
                    // defined
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.initiatebatchfoctransfer.msg.error.commprfnotdefined"));
                    errorList.add(errorVO);
                    // Handling the failed transaction details for generating
                    // the Excel file
                    if (isPartialBatchAllowed) {
                        addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                    }
                    BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : Commission profile for product & transactiontype combination is not defined", "");
                    continue;
                }
                requestedValue = batchItemsVO.getRequestedQuantity();
                minTrfValue = rsSelectCProfileProd.getLong("min_transfer_value");
                maxTrfValue = rsSelectCProfileProd.getLong("max_transfer_value");
                if (minTrfValue > requestedValue || maxTrfValue < requestedValue) {
                    msgArr = new String[3];
                    msgArr[0] = PretupsBL.getDisplayAmount(requestedValue);
                    msgArr[1] = PretupsBL.getDisplayAmount(minTrfValue);
                    msgArr[2] = PretupsBL.getDisplayAmount(maxTrfValue);
                    // put error requested quantity is not between min and max
                    // values
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.initiatebatchfoctransfer.msg.error.qtymaxmin", msgArr));
                    msgArr = null;
                    errorList.add(errorVO);
                    // Handling the failed transaction details for generating
                    // the Excel file
                    if (isPartialBatchAllowed) {
                        addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                    }
                    BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : Requested quantity is not between min and max values",
                        "minTrfValue=" + minTrfValue + ", maxTrfValue=" + maxTrfValue);
                    continue;
                }
                multipleOf = rsSelectCProfileProd.getLong("transfer_multiple_off");
                if (requestedValue % multipleOf != 0) {
                    // put error requested quantity is not multiple of
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.initiatebatchfoctransfer.msg.error.notmulof", new String[] { PretupsBL.getDisplayAmount(multipleOf) }));
                    // Handling the failed transaction details for generating
                    // the Excel file
                    if (isPartialBatchAllowed) {
                        addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                    }
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : Requested quantity is not in multiple value",
                        "multiple of=" + multipleOf);
                    continue;
                }

                index = 0;
                ++index;
                pstmtSelectCProfileProdDetail.setString(index, rsSelectCProfileProd.getString("comm_profile_products_id"));
                ++index;
                pstmtSelectCProfileProdDetail.setLong(index, requestedValue);
                ++index;
                pstmtSelectCProfileProdDetail.setLong(index, requestedValue);
                rsSelectCProfileProdDetail = pstmtSelectCProfileProdDetail.executeQuery();
                pstmtSelectCProfileProdDetail.clearParameters();
                if (!rsSelectCProfileProdDetail.next()) {
                    // put error commission profile slab is not define for the
                    // requested value
                    errorVO = new ListValueVO(batchItemsVO.getMsisdn(), String.valueOf(batchItemsVO.getRecordNumber()), p_messages.getMessage(p_locale,
                        "batchfoc.initiatebatchfoctransfer.msg.error.commslabnotdefined"));
                    // Handling the failed transaction details for generating
                    // the Excel file
                    if (isPartialBatchAllowed) {
                        addErrorList(errorVO, batchItemsVO, arrayFocListValueVO);
                    }
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : Commission profile slab is not define for the requested value", "");
                    continue;
                }
                // to calculate tax
                transferItemsList = new ArrayList();
                channelTransferItemsVO = new ChannelTransferItemsVO();
                // this value will be inserted into the table as the requested
                // qty
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

                if (PretupsI.YES.equals(rsSelectCProfileProd.getString("taxes_on_foc_applicable"))) {
                    channelTransferItemsVO.setTaxOnFOCTransfer(PretupsI.YES);
                } else {
                    channelTransferItemsVO.setTaxOnFOCTransfer(PretupsI.NO);
                }

                transferItemsList.add(channelTransferItemsVO);
                // make a new channel TransferVO to transfer into the method
                // during tax calculataion
                final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
                channelTransferVO.setChannelTransferitemsVOList(transferItemsList);
                channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_RETURN);

                ChannelTransferBL.calculateMRPWithTaxAndDiscount(channelTransferVO, PretupsI.TRANSFER_TYPE_FOC);

                // taxes on FOC required
                // ends commission profile validaiton

                // insert items data here
                index = 0;
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getBatchId());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getBatchDetailId());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getCategoryCode());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getMsisdn());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getUserId());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getStatus());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getModifiedBy());
                ++index;
                pstmtInsertBatchItems.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchItemsVO.getModifiedOn()));
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getUserGradeCode());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getExtTxnNo());
                ++index;
                pstmtInsertBatchItems.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchItemsVO.getExtTxnDate()));
                ++index;
                pstmtInsertBatchItems.setTimestamp(index, BTSLUtil.getTimestampFromUtilDate(batchItemsVO.getTransferDate()));
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getTxnProfile());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getCommissionProfileSetId());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getCommissionProfileVer());
                ++index;
                pstmtInsertBatchItems.setString(index, channelTransferItemsVO.getCommProfileDetailID());
                ++index;
                pstmtInsertBatchItems.setString(index, channelTransferItemsVO.getCommType());
                ++index;                pstmtInsertBatchItems.setDouble(index, channelTransferItemsVO.getCommRate());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getCommValue());
                ++index;
                pstmtInsertBatchItems.setString(index, channelTransferItemsVO.getTax1Type());
                ++index;
                pstmtInsertBatchItems.setDouble(index, channelTransferItemsVO.getTax1Rate());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getTax1Value());
                ++index;
                pstmtInsertBatchItems.setString(index, channelTransferItemsVO.getTax2Type());
                ++index;
                pstmtInsertBatchItems.setDouble(index, channelTransferItemsVO.getTax2Rate());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getTax2Value());
                ++index;
                pstmtInsertBatchItems.setString(index, channelTransferItemsVO.getTax3Type());
                ++index;
                pstmtInsertBatchItems.setDouble(index, channelTransferItemsVO.getTax3Rate());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getTax3Value());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getRequiredQuantity());
                ++index;
                pstmtInsertBatchItems.setLong(index, channelTransferItemsVO.getProductTotalMRP());
                // commented for DB2
                // pstmtInsertBatchItems.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getInitiatorRemarks());
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getExternalCode());
                ++index;
                pstmtInsertBatchItems.setString(index, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_ITEM_RCRDSTATUS_PROCESSED);

                /** START: Birendra: 29JAN2015 */
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getWalletCode());
                /** STOP: Birendra: 29JAN2015 */
                ++index;
                pstmtInsertBatchItems.setString(index, batchItemsVO.getDualCommissionType());
                queryExecutionCount = pstmtInsertBatchItems.executeUpdate();
                if (queryExecutionCount <= 0) {
                    p_con.rollback();
                    // put error record can not be inserted
                    _log.error(methodName, "Record cannot be inserted in batch items table");
                    BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "FAIL : DB Error Record cannot be inserted in batch items table",
                        "queryExecutionCount=" + queryExecutionCount);
                } else {
                    p_con.commit();
                    totalSuccessRecords++;
                    // put success in the logger file.
                    BatchFocFileProcessLog.detailLog(methodName, p_batchMasterVO, batchItemsVO, "PASS : Record inserted successfully in batch items table",
                        "queryExecutionCount=" + queryExecutionCount);
                }
                // ends here

            }// for loop for the batch items
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[initiateBatchFOCTransfer]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            BatchFocFileProcessLog
                .focBatchMasterLog(methodName, p_batchMasterVO, "FAIL : SQL Exception:" + sqe.getMessage(), "TOTAL SUCCESS RECORDS = " + totalSuccessRecords);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[initiateBatchFOCTransfer]", "",
                "", "", "Exception:" + ex.getMessage());
            BatchFocFileProcessLog.focBatchMasterLog(methodName, p_batchMasterVO, "FAIL : Exception:" + ex.getMessage(), "TOTAL SUCCESS RECORDS = " + totalSuccessRecords);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rsSelectExtTxnID1 != null) {
                    rsSelectExtTxnID1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectExtTxnID1 != null) {
                    pstmtSelectExtTxnID1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectExtTxnID2 != null) {
                    rsSelectExtTxnID2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectExtTxnID2 != null) {
                    pstmtSelectExtTxnID2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectTrfRule != null) {
                    rsSelectTrfRule.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectTrfRule != null) {
                    pstmtSelectTrfRule.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectTrfRuleProd != null) {
                    rsSelectTrfRuleProd.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectTrfRuleProd != null) {
                    pstmtSelectTrfRuleProd.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectCProfileProd != null) {
                    rsSelectCProfileProd.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectCProfileProd != null) {
                    pstmtSelectCProfileProd.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectCProfileProdDetail != null) {
                    rsSelectCProfileProdDetail.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectCProfileProdDetail != null) {
                    pstmtSelectCProfileProdDetail.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rsSelectTProfileProd != null) {
                    rsSelectTProfileProd.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectTProfileProd != null) {
                    pstmtSelectTProfileProd.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertBatchMaster != null) {
                    pstmtInsertBatchMaster.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertBatchGeo != null) {
                    pstmtInsertBatchGeo.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertBatchItems != null) {
                    pstmtInsertBatchItems.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {

                // if all records contains errors then rollback the master table
                // entry
                if (errorList != null && (errorList.size() == p_batchItemsList.size())) {
                    p_con.rollback();
                    _log.error(methodName, "ALL the records conatins errors and cannot be inserted in db");
                    BatchFocFileProcessLog.focBatchMasterLog(methodName, p_batchMasterVO, "FAIL : ALL the records conatins errors and cannot be inserted in DB ", "");
                }
                // else update the master table with the open status and total
                // number of records.
                else {
                    int index = 0;
                    int queryExecutionCount = -1;
                    ++index;
                    pstmtUpdateBatchMaster.setInt(index, p_batchMasterVO.getBatchTotalRecord() - errorList.size());
                    ++index;
                    pstmtUpdateBatchMaster.setString(index, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_OPEN);
                    ++index;
                    pstmtUpdateBatchMaster.setString(index, p_batchMasterVO.getBatchId());
                    queryExecutionCount = pstmtUpdateBatchMaster.executeUpdate();
                    if (queryExecutionCount <= 0) // Means No Records Updated
                    {
                        _log.error(methodName, "Unable to Update the batch size in master table..");
                        p_con.rollback();
                        EventHandler
                            .handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[initiateBatchFOCTransfer]",
                                "", "", "", "Error while updating FOC_BATCHES table. Batch id=" + p_batchMasterVO.getBatchId());
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
                    _log.errorTrace(methodName, ex);
                }
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateBatchMaster != null) {
                    pstmtUpdateBatchMaster.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: errorList.size()=" + errorList.size());
            }
        }
        return errorList;
    }
    
    
    
    
    
    
    

    /**
     * @author diwakar
     * @param errorVO
     * @param batchItemsVO
     */

    private void addErrorList(ListValueVO errorVO, FOCBatchItemsVO batchItemsVO, ArrayList<FocListValueVO> arrayFocListValueVO) {
        final String methodName = "addErrorList";
        FocListValueVO focListValueVO = new FocListValueVO();
        focListValueVO.setCodeName(errorVO.getCodeName());
        focListValueVO.setOtherInfo(errorVO.getOtherInfo());
        focListValueVO.setOtherInfo2(errorVO.getOtherInfo2());
        focListValueVO.setMsisdn(batchItemsVO.getMsisdn());
        focListValueVO.setLoginID(batchItemsVO.getLoginID());
        focListValueVO.setUserCategory(batchItemsVO.getCategoryCode());
        focListValueVO.setUserGrade(batchItemsVO.getUserGradeCode());
        focListValueVO.setExtTXNNumber(batchItemsVO.getExtTxnNo());
        try {
            _log.debug(methodName, batchItemsVO.getExtTxnDate() + " " + BTSLUtil.getDateTimeStringFromDate(batchItemsVO.getExtTxnDate(), "MM-dd-yyyy"));
            focListValueVO.setExtTXNDate(BTSLUtil.getDateTimeStringFromDate(batchItemsVO.getExtTxnDate(), "MM-dd-yyyy"));
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        }
        focListValueVO.setExtCode(batchItemsVO.getExternalCode());
        focListValueVO.setQuantity(batchItemsVO.getRequestedQuantity());
        focListValueVO.setRemarks(errorVO.getOtherInfo2());
        arrayFocListValueVO.add(focListValueVO);
        focListValueVO = null;
    }

    /**
     * This method will load the batches that are within the geography of user
     * whose userId is passed and batch id basis and mobile no basis.
     * with status(OPEN) also in items table for corresponding master record.
     * 
     * @Connection p_con
     * @String p_goeDomain
     * @String p_domain
     * @String p_productCode
     * @String p_batchid
     * @String p_msisdn
     * @Date p_fromDate
     * @Date p_toDate
     * @param p_loginID
     *            TODO
     * @throws BTSLBaseException
     */
    public ArrayList loadBatchFOCMasterDetails(Connection p_con, String p_goeDomain, String p_domain, String p_productCode, String p_batchid, String p_msisdn, Date p_fromDate, Date p_toDate, String p_loginID, String p_type) throws BTSLBaseException {
        final String methodName = "loadBatchFOCMasterDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(
                methodName,
                "Entered p_goeDomain=" + p_goeDomain + " p_domain=" + p_domain + " p_productCode=" + p_productCode + " p_batchid=" + p_batchid + " p_msisdn=" + p_msisdn + " p_fromDate=" + p_fromDate + " p_toDate=" + p_toDate + " p_loginID=" + p_loginID + " p_type=" + p_type);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final ArrayList list = new ArrayList();
        try {
        	pstmt = focBatchTransferWebQry.loadBatchFOCMasterDetailsQuery(p_con, p_goeDomain, p_domain, p_productCode, p_batchid, p_msisdn, p_fromDate, p_toDate, p_loginID, p_type);
            rs = pstmt.executeQuery();
            FOCBatchMasterVO focBatchMasterVO = null;
            while (rs.next()) {
                focBatchMasterVO = new FOCBatchMasterVO();
                focBatchMasterVO.setBatchId(rs.getString("batch_id"));
                focBatchMasterVO.setDomainCode(rs.getString("domain_code"));
                focBatchMasterVO.setBatchName(rs.getString("batch_name"));
                focBatchMasterVO.setProductName(rs.getString("product_name"));
                focBatchMasterVO.setProductMrp(rs.getLong("unit_value"));
                focBatchMasterVO.setProductMrpStr(PretupsBL.getDisplayAmount(rs.getLong("unit_value")));
                focBatchMasterVO.setBatchTotalRecord(rs.getInt("batch_total_record"));
                focBatchMasterVO.setNewRecords(rs.getInt("new"));
                focBatchMasterVO.setLevel1ApprovedRecords(rs.getInt("appr1"));
                focBatchMasterVO.setLevel2ApprovedRecords(rs.getInt("appr2"));
                focBatchMasterVO.setClosedRecords(rs.getInt("closed"));
                focBatchMasterVO.setRejectedRecords(rs.getInt("cncl"));
                focBatchMasterVO.setBatchDate(rs.getDate("batch_date"));
                if (focBatchMasterVO.getBatchDate() != null) {
                    focBatchMasterVO.setBatchDateStr(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(focBatchMasterVO.getBatchDate())));
                }
                list.add(focBatchMasterVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[loadBatchFOCMasterDetails]",
                "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[loadBatchFOCMasterDetails]",
                "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: focBatchMasterVOList size=" + list.size());
            }
        }
        return list;
    }
    
    
    /**
     * Method to cancel/approve the batch. This also perform all the data
     * validation.
     * Also construct error list
     * Tables updated are: foc_batch_items,foc_batches
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
    public ArrayList processOrderByBatchRest(Connection p_con, LinkedHashMap p_dataMap, String p_currentLevel, String p_userID, Locale p_locale, String p_sms_default_lang, String p_sms_second_lang) throws BTSLBaseException {
    

        final String methodName = "processOrderByBatch";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName,  " p_currentLevel = " + p_currentLevel + ", p_locale = " + p_locale + ", p_userID = " + p_userID);
        }
        Boolean isExternalTxnUnique = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE);
        PreparedStatement pstmtLoadUser = null;
        // commented for DB2
        // OraclePreparedStatement psmtCancelFOCBatchItem = null;
        // OraclePreparedStatement psmtAppr1FOCBatchItem = null;
        // OraclePreparedStatement psmtAppr2FOCBatchItem = null;
        // OraclePreparedStatement pstmtUpdateMaster= null;
        PreparedStatement psmtCancelFOCBatchItem = null;
        PreparedStatement psmtAppr1FOCBatchItem = null;
        PreparedStatement psmtAppr2FOCBatchItem = null;
        PreparedStatement pstmtUpdateMaster = null;

        PreparedStatement pstmtSelectItemsDetails = null;
        // PreparedStatement pstmtUpdateMaster= null;
        PreparedStatement pstmtIsModified = null;
        PreparedStatement pstmtIsTxnNumExists1 = null;
        PreparedStatement pstmtIsTxnNumExists2 = null;
        ArrayList errorList = null;
        ListValueVO errorVO = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        ResultSet rs4 = null;
        int updateCount = 0;
        String batch_ID = null;
        /*
         * The query below will be used to load user datils.
         * That details is the validated for eg: transfer profile, commission
         * profile, user status etc.
         */
        StringBuffer sqlBuffer = new StringBuffer(" SELECT u.status userstatus, cusers.in_suspend, ");
        sqlBuffer.append("cps.status commprofilestatus,tp.status profile_status,cps.language_1_message comprf_lang_1_msg, ");
        sqlBuffer.append("cps.language_2_message comprf_lang_2_msg, u.category_code,u.network_code ");
        sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps,transfer_profile tp ");
        sqlBuffer.append("WHERE u.user_id = ? AND u.status <> 'N' AND u.status <> 'C' ");
        sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
        sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
        sqlBuffer.append(" tp.profile_id = cusers.transfer_profile_id  ");
        final String sqlLoadUser = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlLoadUser=" + sqlLoadUser);
        }
        sqlBuffer = null;
        // after validating if request is to cancle the order, the below query
        // is used.
        sqlBuffer = new StringBuffer(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" modified_by = ?, modified_on = ?,  ");
        if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentLevel)) {
            sqlBuffer.append(" first_approver_remarks = ?, ");
        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentLevel)) {
            sqlBuffer.append(" second_approver_remarks = ?, ");
        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentLevel)) {
            sqlBuffer.append(" third_approver_remarks = ?, ");
        }
        sqlBuffer.append(" cancelled_by = ?, ");
        sqlBuffer.append(" cancelled_on = ?, status = ?");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        if (!PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentLevel)) {
            sqlBuffer.append(" AND status IN (? , ? )  ");
        } else {
            sqlBuffer.append(" AND status  = ?   ");
        }
        final String sqlCancelFOCBatchItems = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlCancelFOCBatchItems=" + sqlCancelFOCBatchItems);
        }
        sqlBuffer = null;

        // after validating if request is of level 1 approve the order, the
        // below query is used.
        sqlBuffer = new StringBuffer(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" first_approver_remarks = ?, ");
        sqlBuffer.append(" first_approved_by=?, first_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status IN (? , ? )  ");

        final String sqlApprv1FOCBatchItems = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlApprv1FOCBatchItems=" + sqlApprv1FOCBatchItems);
        }
        sqlBuffer = null;

        // after validating if request is of level 2 approve the order, the
        // below query is used.
        sqlBuffer = new StringBuffer(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" second_approver_remarks = ?, ");
        sqlBuffer.append(" second_approved_by=? , second_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status IN (? , ? )  ");
        final String sqlApprv2FOCBatchItems = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlApprv2FOCBatchItems=" + sqlApprv2FOCBatchItems);
        }
        sqlBuffer = null;

        // Afetr all teh records are processed the the below query is used to
        // load the various counts such as new ,
        // apprv1, close ,cancled etc. These couts will be used to deceide what
        // status to be updated in mater table
       // FOCBatchTransferWebQry focBatchTransferWebQry = (FOCBatchTransferWebQry)ObjectProducer.getObject(QueryConstants.FOC_BATCHTRANSFER_WEB_QRY, QueryConstants.QUERY_PRODUCER);
        final String selectItemsDetails = focBatchTransferWebQry.processOrderByBatchQry(); 
        

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY selectItemsDetails=" + selectItemsDetails);
        }
        sqlBuffer = null;

        // The query below is used to update the master table after all items
        // are processed
        sqlBuffer = new StringBuffer("UPDATE foc_batches SET status=? , modified_by=? ,modified_on=?,sms_default_lang=? , sms_second_lang=?  ");
        sqlBuffer.append(" WHERE batch_id=? AND status=? ");
        final String updateFOCBatches = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY updateFOCBatches=" + updateFOCBatches);
        }
        sqlBuffer = null;

        // The query below is used to check if the record is modified or not
        sqlBuffer = new StringBuffer("SELECT modified_on FROM foc_batch_items ");
        sqlBuffer.append("WHERE batch_detail_id = ? ");
        final String isModified = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY isModified=" + isModified);
        }
        sqlBuffer = null;

        // The below query will be exceute if external txn number unique is "Y"
        // in system preferences
        // This will check the existence of external txn number in
        // foc_batch_items table
        sqlBuffer = new StringBuffer(" SELECT 1 FROM foc_batch_items ");
        sqlBuffer.append("WHERE ext_txn_no=? AND status <> ? AND batch_detail_id<>? ");
        final String isExistsTxnNum1 = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY isExistsTxnNum1=" + isExistsTxnNum1);
        }
        sqlBuffer = null;

        // The below query will be exceute if external txn number unique is "Y"
        // in system preferences
        // and external txn number is not exists in foc_batch_items table.
        // This will check the existence of external txn number in
        // channel_transfers table
        sqlBuffer = new StringBuffer("  SELECT 1 FROM channel_transfers ");
        sqlBuffer.append("WHERE type=? AND ext_txn_no=? AND status <> ? ");
        final String isExistsTxnNum2 = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY isExistsTxnNum2=" + isExistsTxnNum2);
        }
        sqlBuffer = null;
        Date date = null;
        try {
            FOCBatchItemsVO focBatchItemVO = null;
            ChannelUserVO channelUserVO = null;
            date = new Date();
            // Create the prepared statements
            pstmtLoadUser = p_con.prepareStatement(sqlLoadUser);
            // commented for DB2
            // psmtCancelFOCBatchItem=(OraclePreparedStatement)p_con.prepareStatement(sqlCancelFOCBatchItems);
            // psmtAppr1FOCBatchItem=(OraclePreparedStatement)p_con.prepareStatement(sqlApprv1FOCBatchItems);
            // psmtAppr2FOCBatchItem=(OraclePreparedStatement)p_con.prepareStatement(sqlApprv2FOCBatchItems);
            psmtCancelFOCBatchItem = p_con.prepareStatement(sqlCancelFOCBatchItems);
            psmtAppr1FOCBatchItem = p_con.prepareStatement(sqlApprv1FOCBatchItems);
            psmtAppr2FOCBatchItem = p_con.prepareStatement(sqlApprv2FOCBatchItems);
            pstmtSelectItemsDetails = p_con.prepareStatement(selectItemsDetails);
            // commented for DB2
            // pstmtUpdateMaster=(OraclePreparedStatement)p_con.prepareStatement(updateFOCBatches);
            pstmtUpdateMaster = p_con.prepareStatement(updateFOCBatches);
            pstmtIsModified = p_con.prepareStatement(isModified);
            pstmtIsTxnNumExists1 = p_con.prepareStatement(isExistsTxnNum1);
            pstmtIsTxnNumExists2 = p_con.prepareStatement(isExistsTxnNum2);
            errorList = new ArrayList();
            final Iterator iterator = p_dataMap.keySet().iterator();
            String key = null;
            int m = 0;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                focBatchItemVO = (FOCBatchItemsVO) p_dataMap.get(key);
                if (BTSLUtil.isNullString(batch_ID)) {
                    batch_ID = focBatchItemVO.getBatchId();
                }
                pstmtLoadUser.clearParameters();
                m = 0;
                ++m;
                pstmtLoadUser.setString(m, focBatchItemVO.getUserId());
                rs = pstmtLoadUser.executeQuery();
                if (rs.next())// check data found or not
                {
                    channelUserVO =  ChannelUserVO.getInstance();
                    channelUserVO.setUserID(focBatchItemVO.getUserId());
                    channelUserVO.setStatus(rs.getString("userstatus"));
                    channelUserVO.setInSuspend(rs.getString("in_suspend"));
                    channelUserVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
                    channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                    channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                    channelUserVO.setTransferProfileStatus(rs.getString("profile_status"));
                    channelUserVO.setNetworkCode(rs.getString("network_code"));
                    channelUserVO.setCategoryCode(rs.getString("category_code"));
                    // (User status is checked) if this condition is true then
                    // made entry in logs and leave this data.

                    // user life cycle
                    boolean receiverStatusAllowed = false;
                    final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(channelUserVO.getNetworkCode(), channelUserVO.getCategoryCode(),
                        PretupsI.USER_TYPE_CHANNEL, PretupsI.REQUEST_SOURCE_TYPE_WEB);
                    if (userStatusVO != null) {
                        final String userStatusAllowed = userStatusVO.getUserReceiverAllowed();
                        final String status[] = userStatusAllowed.split(",");
                        for (int i = 0; i < status.length; i++) {
                            if (status[i].equals(channelUserVO.getStatus())) {
                                receiverStatusAllowed = true;
                            }
                        }
                    } else {
                        throw new BTSLBaseException(this, "processOrderByBatch", "error.status.processing");
                    }
                    if (!receiverStatusAllowed) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchfoc.batchapprovereject.msg.error.usersuspend"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.focBatchItemLog(methodName, focBatchItemVO, "FAIL : User is not active", "Approval level" + p_currentLevel);
                        continue;
                    }
                    // (commission profile status is checked) if this condition
                    // is true then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchfoc.batchapprovereject.msg.error.comprofsuspend"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.focBatchItemLog(methodName, focBatchItemVO, "FAIL : Commission profile is suspend", "Approval level" + p_currentLevel);
                        continue;
                    }
                    // (tranmsfer profile status is checked) if this condition
                    // is true then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchfoc.batchapprovereject.msg.error.trfprofsuspend"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.focBatchItemLog(methodName, focBatchItemVO, "FAIL : Transfer profile is suspend", "Approval level" + p_currentLevel);
                        continue;
                    }
                    // (user in suspend is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchfoc.batchapprovereject.msg.error.userinsuspend"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.focBatchItemLog(methodName, focBatchItemVO, "FAIL : User is IN suspend", "Approval level" + p_currentLevel);
                        continue;
                    }
                }
                // (record not found for user) if this condition is true then
                // made entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.nouser"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.focBatchItemLog(methodName, focBatchItemVO, "FAIL : User not found", "Approval level" + p_currentLevel);
                    continue;

                }
                pstmtIsModified.clearParameters();
                m = 0;
                ++m;
                pstmtIsModified.setString(m, focBatchItemVO.getBatchDetailId());
                rs1 = pstmtIsModified.executeQuery();
                java.sql.Timestamp newlastModified = null;
                if (rs1.next()) {
                    newlastModified = rs1.getTimestamp("modified_on");
                }
                // (record not found means it is modified) if this condition is
                // true then made entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog
                        .focBatchItemLog(methodName, focBatchItemVO, "FAIL : Record is already modified by some one else", "Approval level" + p_currentLevel);
                    continue;
                }
                // if this condition is true then made entry in logs and leave
                // this data.
                if (newlastModified.getTime() != BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getModifiedOn()).getTime()) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog
                        .focBatchItemLog(methodName, focBatchItemVO, "FAIL : Record is already modified by some one else", "Approval level" + p_currentLevel);
                    continue;

                }
                // (external txn number is checked) if this condition is true
                // then made entry in logs and leave this data.
                if (isExternalTxnUnique && !BTSLUtil.isNullString(focBatchItemVO.getExtTxnNo()) && !PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL
                    .equals(focBatchItemVO.getStatus())) {
                    // check in foc_batch-item table
                    pstmtIsTxnNumExists1.clearParameters();
                    m = 0;
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, focBatchItemVO.getBatchDetailId());
                    rs2 = pstmtIsTxnNumExists1.executeQuery();
                    // (external txn number is checked) if this condition is
                    // true then made entry in logs and leave this data.
                    if (rs2.next()) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchfoc.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.focBatchItemLog(methodName, focBatchItemVO, "FAIL : External transaction number already exists BATCH FOC",
                            "Approval level" + p_currentLevel);
                        continue;
                    }
                    // check in channel_transfers table
                    pstmtIsTxnNumExists2.clearParameters();
                    m = 0;
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, PretupsI.CHANNEL_TYPE_O2C);
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    rs3 = pstmtIsTxnNumExists2.executeQuery();
                    // (external txn number is checked) if this condition is
                    // true then made entry in logs and leave this data.
                    if (rs3.next()) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchfoc.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.focBatchItemLog(methodName, focBatchItemVO, "FAIL : External transaction number already exists CHANNEL TRANSFER",
                            "Approval level" + p_currentLevel);
                        continue;
                    }
                }
                // If operation is of cancle then set the fiels in
                // psmtCancelFOCBatchItem
                if (PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL.equals(focBatchItemVO.getStatus())) {
                    psmtCancelFOCBatchItem.clearParameters();
                    m = 0;
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtCancelFOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentLevel)) {
                        // psmtCancelFOCBatchItem.setFormOfUse(++m,
                        // OraclePreparedStatement.FORM_NCHAR);//commented for
                        // DB2
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, focBatchItemVO.getFirstApproverRemarks());
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentLevel)) {
                        // psmtCancelFOCBatchItem.setFormOfUse(++m,
                        // OraclePreparedStatement.FORM_NCHAR);//commented for
                        // DB2
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, focBatchItemVO.getSecondApproverRemarks());
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentLevel)) {
                        // psmtCancelFOCBatchItem.setFormOfUse(++m,
                        // OraclePreparedStatement.FORM_NCHAR);//commented for
                        // DB2
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, focBatchItemVO.getThirdApproverRemarks());
                    }
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtCancelFOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, focBatchItemVO.getStatus());
                    ++m;
                    psmtCancelFOCBatchItem.setString(m, focBatchItemVO.getBatchDetailId());
                    if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentLevel)) {
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentLevel)) {
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentLevel)) {
                        ++m;
                        psmtCancelFOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    }
                    updateCount = psmtCancelFOCBatchItem.executeUpdate();
                }
                // IF approval 1 is the operation then set parametrs in
                // psmtAppr1FOCBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(focBatchItemVO.getStatus())) {
                    psmtAppr1FOCBatchItem.clearParameters();
                    m = 0;
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    // psmtAppr1FOCBatchItem.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR);//commented for DB2
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getFirstApproverRemarks());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getStatus());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    updateCount = psmtAppr1FOCBatchItem.executeUpdate();
                }
                // IF approval 2 is the operation then set parametrs in
                // psmtAppr2FOCBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(focBatchItemVO.getStatus())) {
                    psmtAppr2FOCBatchItem.clearParameters();
                    m = 0;
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    // psmtAppr2FOCBatchItem.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR);//commented for DB2
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getSecondApproverRemarks());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getStatus());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    updateCount = psmtAppr2FOCBatchItem.executeUpdate();
                }
                // If update count is <=0 that means record not updated in db
                // properly so made entry in logs and leave this data
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.focBatchItemLog(methodName, focBatchItemVO, "FAIL : DB Error while updating items table",
                        "Approval level" + p_currentLevel + ", updateCount=" + updateCount);
                    continue;
                }
                // commit the transaction after processiong each record
                p_con.commit();
            }// end of while
             // Check the status to be updated in master table agfter processing
             // of all records

        }// end of try
        catch (SQLException sqe) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[processOrderByBatch]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            BatchFocFileProcessLog.focBatchItemLog(methodName, null, "FAIL : updating batch items SQL Exception:" + sqe.getMessage(),
                "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[processOrderByBatch]", "", "",
                "", "Exception:" + ex.getMessage());
            BatchFocFileProcessLog.focBatchItemLog(methodName, null, "FAIL : updating batch items Exception:" + ex.getMessage(),
                "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
        	try {
                if (rs1 != null) {
                    rs1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
        	try {
                if (rs2 != null) {
                    rs2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
        	try {
                if (rs3 != null) {
                    rs3.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtLoadUser != null) {
                    pstmtLoadUser.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtCancelFOCBatchItem != null) {
                    psmtCancelFOCBatchItem.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr1FOCBatchItem != null) {
                    psmtAppr1FOCBatchItem.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr2FOCBatchItem != null) {
                    psmtAppr2FOCBatchItem.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsModified != null) {
                    pstmtIsModified.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsTxnNumExists1 != null) {
                    pstmtIsTxnNumExists1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsTxnNumExists2 != null) {
                    pstmtIsTxnNumExists2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                int m = 0;
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
                ++m;
                pstmtSelectItemsDetails.setString(m, batch_ID);
                rs4 = pstmtSelectItemsDetails.executeQuery();
                if (rs4.next()) {
                    final int totalCount = rs4.getInt("batch_total_record");
                    final int closeCount = rs4.getInt("closed");
                    final int cnclCount = rs4.getInt("cncl");
                    String statusOfMaster = null;
                    // If all records are canle then set cancelled in master
                    // table
                    if (totalCount == cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_CANCEL;
                    } else if (totalCount == closeCount + cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_CLOSE;
                        // Otherwise set OPEN in mastrer table
                    } else {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_OPEN;
                    }
                    m = 0;
                    ++m;
                    pstmtUpdateMaster.setString(m, statusOfMaster);
                    ++m;
                    pstmtUpdateMaster.setString(m, p_userID);
                    ++m;
                    pstmtUpdateMaster.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));

                    // pstmtUpdateMaster.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR);//commented for DB2
                    ++m;
                    pstmtUpdateMaster.setString(m, p_sms_default_lang);
                    // pstmtUpdateMaster.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR);//commented for DB2
                    ++m;
                    pstmtUpdateMaster.setString(m, p_sms_second_lang);
                    ++m;
                    pstmtUpdateMaster.setString(m, batch_ID);
                    ++m;
                    pstmtUpdateMaster.setString(m, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_UNDERPROCESS);

                    updateCount = pstmtUpdateMaster.executeUpdate();
                    if (updateCount <= 0) {
                        p_con.rollback();
                        errorVO = new ListValueVO("", "", PretupsRestUtil.getMessageString( "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.focBatchItemLog(methodName, null, "FAIL : DB Error while updating master table",
                            "Approval level" + p_currentLevel + ", updateCount=" + updateCount);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "FOCBatchTransferWebDAO[processOrderByBatch]", "", "", "", "Error while updating FOC_BATCHES table. Batch id=" + batch_ID);
                    }// end of if
                }// end of if
                p_con.commit();
            } catch (SQLException sqe) {
                try {
                    if (p_con != null) {
                        p_con.rollback();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                _log.error(methodName, "SQLException : " + sqe);
                _log.errorTrace(methodName, sqe);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[processOrderByBatch]", "",
                    "", "", "SQL Exception:" + sqe.getMessage());
                BatchFocFileProcessLog.focBatchItemLog(methodName, null, "FAIL : updating batch master SQL Exception:" + sqe.getMessage(),
                    "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
                // throw new BTSLBaseException(this, methodName,
                // "error.general.sql.processing");
            } catch (Exception ex) {
                try {
                    if (p_con != null) {
                        p_con.rollback();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                _log.error(methodName, "Exception : " + ex);
                _log.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[processOrderByBatch]", "",
                    "", "", "Exception:" + ex.getMessage());
                BatchFocFileProcessLog.focBatchItemLog(methodName, null, "FAIL : updating batch master Exception:" + ex.getMessage(),
                    "Approval level" + p_currentLevel + ", BATCH_ID=" + batch_ID);
                // throw new BTSLBaseException(this, methodName,
                // "error.general.processing");
            }finally{
            	try {
                    if (rs4 != null) {
                        rs4.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                try {
                    if (pstmtSelectItemsDetails != null) {
                        pstmtSelectItemsDetails.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                try {
                    if (pstmtUpdateMaster != null) {
                        pstmtUpdateMaster.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
            }
            

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: errorList size=" + errorList.size());
            }
        }
        return errorList;
    
    }
    
    
    /**
     * Method to close the foc order by batch. This also perform all the data
     * validation.
     * Also construct error list
     * Tables updated are:
     * network_stocks,network_daily_stocks,network_stock_transactions
     * ,network_stock_trans_items
     * user_balances,user_daily_balances,user_transfer_counts,foc_batch_items,
     * foc_batches,
     * channel_transfers_items,channel_transfers
     * 
     * @param p_con
     * @param p_dataMap
     * @param p_currentLevel
     * @param p_userID
     * @param p_focBatchMatserVO
     * @param p_messages
     * @param p_locale
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList closeOrderByBatchRest(Connection p_con, LinkedHashMap p_dataMap, String p_currentLevel, String p_userID, FOCBatchMasterVO p_focBatchMatserVO,Locale p_locale, String p_sms_default_lang, String p_sms_second_lang) throws BTSLBaseException {

        final String methodName = "closeOrderByBatch";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, " p_currentLevel=" + p_currentLevel + " p_locale=" + p_locale);
        }
        String defaultWebGatewayCode = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WEB_GATEWAY_CODE);
        Boolean isExternalTxnUnique = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE);
        Boolean isFOCSmsNotify = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.FOC_SMS_NOTIFY);
        Boolean isMultipleWalletApply = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY);
        String txnReceiverUserStatusChang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_RECEIVER_USER_STATUS_CHANG);
        PreparedStatement pstmtLoadUser = null;
        PreparedStatement pstmtLoadNetworkStock = null;
        PreparedStatement pstmtUpdateNetworkStock = null;
        PreparedStatement pstmtInsertNetworkDailyStock = null;
        PreparedStatement pstmtSelectNetworkStock = null;
        PreparedStatement pstmtupdateSelectedNetworkStock = null;
        // OraclePreparedStatement
        // pstmtInsertNetworkStockTransaction=null;//commented for DB2
        PreparedStatement pstmtInsertNetworkStockTransaction = null;
        PreparedStatement pstmtInsertNetworkStockTransactionItem = null;
        PreparedStatement pstmtSelectUserBalances = null;
        PreparedStatement pstmtUpdateUserBalances = null;
        PreparedStatement pstmtInsertUserDailyBalances = null;
        PreparedStatement pstmtSelectBalance = null;
        PreparedStatement pstmtUpdateBalance = null;
        PreparedStatement pstmtInsertBalance = null;
        PreparedStatement pstmtSelectTransferCounts = null;
        PreparedStatement pstmtSelectProfileCounts = null;
        PreparedStatement pstmtUpdateTransferCounts = null;
        PreparedStatement pstmtInsertTransferCounts = null;
        // OraclePreparedStatement psmtAppr1FOCBatchItem = null;//commented for
        // DB2
        // OraclePreparedStatement psmtAppr2FOCBatchItem = null;//commented for
        // DB2
        // OraclePreparedStatement psmtAppr3FOCBatchItem = null;//commented for
        // DB2
        PreparedStatement psmtAppr1FOCBatchItem = null;
        PreparedStatement psmtAppr2FOCBatchItem = null;
        PreparedStatement psmtAppr3FOCBatchItem = null;
        PreparedStatement pstmtSelectItemsDetails = null;
        // PreparedStatement pstmtUpdateMaster= null;
        // OraclePreparedStatement pstmtUpdateMaster= null;//commented for DB2
        PreparedStatement pstmtUpdateMaster = null;
        PreparedStatement pstmtIsModified = null;
        PreparedStatement pstmtLoadTransferProfileProduct = null;
        PreparedStatement handlerStmt = null;
        PreparedStatement pstmtIsTxnNumExists1 = null;
        PreparedStatement pstmtIsTxnNumExists2 = null;
        PreparedStatement pstmtInsertIntoChannelTransferItems = null;
        // OraclePreparedStatement
        // pstmtInsertIntoChannelTranfers=null;//commented for DB2
        PreparedStatement pstmtInsertIntoChannelTranfers = null;
        PreparedStatement pstmtSelectBalanceInfoForMessage = null;
        ArrayList userbalanceList = null;
        UserBalancesVO balancesVO = null;
        ArrayList errorList = null;
        ListValueVO errorVO = null;
        ResultSet rs = null;
        String language = null;
        String country = null;
        KeyArgumentVO keyArgumentVO = null;
        String[] argsArr = null;
        ArrayList txnSmsMessageList = null;
        ArrayList balSmsMessageList = null;
        Locale locale = null;
        String[] array = null;
        BTSLMessages messages = null;
        PushMessage pushMessage = null;
        int updateCount = 0;
        String o2cTransferID = null;
        PreparedStatement psmtInsertUserThreshold = null;
        long thresholdValue = -1;
        OperatorUtilI operatorUtili = null;
        String m_receiverStatusAllowed[] = null;
        try {
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("closeOrderByBatch", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchO2CTransferDAO[closeOrderByBatch]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
        // user life cycle
        String receiverStatusAllowed = null;
        final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(p_focBatchMatserVO.getNetworkCode(), p_focBatchMatserVO.getCategoryCode(),
            PretupsI.USER_TYPE_CHANNEL, PretupsI.REQUEST_SOURCE_TYPE_WEB);
        if (userStatusVO != null) {
            receiverStatusAllowed = "'" + (userStatusVO.getUserReceiverAllowed()).replaceAll(",", "','") + "'";
        } else {
            throw new BTSLBaseException(this, "closeOrderByBatch", "error.status.processing");
        }
        final String receiverStatusAllowed1 = receiverStatusAllowed.replaceAll("'", "");
        final String sa = receiverStatusAllowed1.replaceAll("\" ", "");
        m_receiverStatusAllowed = sa.split(",");

        /*
         * The query below will be used to load user datils.
         * That details is the validated for eg: transfer profile, commission
         * profile, user status etc.
         */
        StringBuffer sqlBuffer = new StringBuffer(" SELECT u.status userstatus, cusers.in_suspend, cusers.transfer_profile_id, ");
        sqlBuffer.append("cps.status commprofilestatus,tp.status profile_status,cps.language_1_message comprf_lang_1_msg, ");
        sqlBuffer.append("cps.language_2_message comprf_lang_2_msg,up.phone_language,up.country, ug.grph_domain_code ");
        sqlBuffer.append("FROM users u,channel_users cusers,commission_profile_set cps,transfer_profile tp, user_phones up,user_geographies ug ");
        sqlBuffer.append("WHERE u.user_id = ? AND up.user_id=u.user_id AND up.primary_number = 'Y' ");
        sqlBuffer.append(" AND u.status IN (");
        for (int i = 0; i < m_receiverStatusAllowed.length; i++) {
            sqlBuffer.append(" ?");
            if (i != m_receiverStatusAllowed.length - 1) {
                sqlBuffer.append(",");
            }
        }
        sqlBuffer.append(")");
        sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
        sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
        sqlBuffer.append(" tp.profile_id = cusers.transfer_profile_id AND ug.user_id = u.user_id ");
        final String sqlLoadUser = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlLoadUser=" + sqlLoadUser);
        }
        sqlBuffer = null;

        // The query below is used to load the network stock details for network
        // in between sender and receiver
        // This table will basically used to update the daily_stock_updated_on
        // and also to know how many
        // records are to be insert in network_daily_stocks

        
        final String sqlLoadNetworkStock = focBatchTransferWebQry.closeOrderByBatchQry();
        
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlLoadNetworkStock=" + sqlLoadNetworkStock);
        }
        sqlBuffer = null;

        // Update daily_stock_updated_on with current date
        sqlBuffer = new StringBuffer("UPDATE network_stocks SET daily_stock_updated_on = ? ");
        sqlBuffer.append("WHERE network_code = ? AND network_code_for = ? AND wallet_type = ?");
        final String sqlUpdateNetworkStock = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlUpdateNetworkStock=" + sqlUpdateNetworkStock);
        }
        sqlBuffer = null;

        // Executed if day difference in last updated date and current date is
        // greater then or equal to 1
        // Insert number of records equal to day difference in last updated date
        // and current date in network_daily_stocks
        sqlBuffer = new StringBuffer("INSERT INTO network_daily_stocks(wallet_date, wallet_type, network_code, network_code_for, ");
        sqlBuffer.append("product_code, wallet_created, wallet_returned, wallet_balance, wallet_sold, last_txn_no, ");
        sqlBuffer.append("last_txn_type, last_txn_balance, previous_balance, created_on,creation_type )");
        sqlBuffer.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        // }
        final String sqlInsertNetworkDailyStock = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlInsertNetworkDailyStock=" + sqlInsertNetworkDailyStock);
        }
        sqlBuffer = null;

        // Select the stock for the requested product for network.
        final String sqlSelectNetworkStock = focBatchTransferWebQry.CloseOrderBatchSelectWalletQry();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelectNetworkStock=" + sqlSelectNetworkStock);
        }
        sqlBuffer = null;

        // Debit the network stock
        sqlBuffer = new StringBuffer(" UPDATE network_stocks SET previous_balance = wallet_balance , wallet_balance = ?, ");
        sqlBuffer.append(" wallet_sold = ? , last_txn_no = ? , last_txn_type = ?, last_txn_balance= ?, ");
        sqlBuffer.append(" modified_by =?, modified_on =? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" network_code = ? ");
        sqlBuffer.append(" AND ");
        sqlBuffer.append(" product_code = ? AND network_code_for = ?  AND wallet_type = ?");
        // }
        final String updateSelectedNetworkStock = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY updateSelectedNetworkStock=" + updateSelectedNetworkStock);
        }
        sqlBuffer = null;

        // Insert record into network_stock_transactions table.
        sqlBuffer = new StringBuffer(" INSERT INTO network_stock_transactions ( ");
        sqlBuffer.append(" txn_no, network_code, network_code_for, stock_type, reference_no, txn_date, requested_quantity, ");
        sqlBuffer.append(" approved_quantity, initiater_remarks, first_approved_remarks, second_approved_remarks, ");
        sqlBuffer.append(" first_approved_by, second_approved_by, first_approved_on, second_approved_on, ");
        sqlBuffer.append(" cancelled_by, cancelled_on, created_by, created_on, modified_on, modified_by, ");
        sqlBuffer.append(" txn_status, entry_type, txn_type, initiated_by, first_approver_limit, user_id, txn_mrp  ");
        if (isMultipleWalletApply) {
            sqlBuffer.append(",txn_wallet,ref_txn_id ");
            sqlBuffer.append(" )VALUES ");
            sqlBuffer.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        } else {
            // added by akanksha for tigo guatemala CR
            sqlBuffer.append(",txn_wallet ");
            sqlBuffer.append(" )VALUES ");
            sqlBuffer.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        }
        final String insertNetworkStockTransaction = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY insertNetworkStockTransaction=" + insertNetworkStockTransaction);
        }
        sqlBuffer = null;

        // Insert record into network_stock_trans_items
        sqlBuffer = new StringBuffer(" INSERT INTO network_stock_trans_items ");
        sqlBuffer.append(" (s_no, txn_no, product_code, required_quantity, approved_quantity, stock, mrp, amount, date_time) ");
        sqlBuffer.append(" VALUES ");
        sqlBuffer.append(" (?,?,?,?,?,?,?,?,?) ");
        final String insertNetworkStockTransactionItem = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY insertNetworkStockTransactionItem=" + insertNetworkStockTransactionItem);
        }
        sqlBuffer = null;

        // The query below is used to load the user balance
        // This table will basically used to update the daily_balance_updated_on
        // and also to know how many
        // records are to be inseert in user_daily_balances table

        final String selectUserBalances = focBatchTransferWebQry.UserBalancesQry();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY selectUserBalances=" + selectUserBalances);
        }
        sqlBuffer = null;

        // update daily_balance_updated_on with current date for user
        sqlBuffer = new StringBuffer(" UPDATE user_balances SET daily_balance_updated_on = ? ");
        sqlBuffer.append("WHERE user_id = ? ");
        final String updateUserBalances = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY updateUserBalances=" + updateUserBalances);
        }
        sqlBuffer = null;

        // Executed if day difference in last updated date and current date is
        // greater then or equal to 1
        // Insert number of records equal to day difference in last updated date
        // and current date in user_daily_balances
        sqlBuffer = new StringBuffer(" INSERT INTO user_daily_balances(balance_date, user_id, network_code, ");
        sqlBuffer.append("network_code_for, product_code, balance, prev_balance, last_transfer_type, ");
        sqlBuffer.append("last_transfer_no, last_transfer_on, created_on,creation_type )");
        sqlBuffer.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?) ");
        final String insertUserDailyBalances = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY insertUserDailyBalances=" + insertUserDailyBalances);
        }
        sqlBuffer = null;

        // Select the balance of user for the perticuler product and network.
        final String selectBalance = focBatchTransferWebQry.SelectBalanceQry();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY selectBalance=" + selectBalance);
        }
        sqlBuffer = null;

        // Credit the user balance(If balance found in user_balances)
        sqlBuffer = new StringBuffer(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , ");
        sqlBuffer.append(" last_transfer_no = ? , last_transfer_on = ? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" user_id = ? ");
        sqlBuffer.append(" AND ");
        sqlBuffer.append(" product_code = ? AND network_code = ? AND network_code_for = ? ");
        /** START: Birendra: 30JAN2015 */
        sqlBuffer.append(" AND balance_type = ?");
        /** STOP: Birendra: 30JAN2015 */
        final String updateBalance = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY updateBalance=" + updateBalance);
        }
        sqlBuffer = null;

        // Insert the record of balnce for user (If balance not found in
        // user_balances)
        sqlBuffer = new StringBuffer(" INSERT ");
        sqlBuffer.append(" INTO user_balances ");
        sqlBuffer.append(" ( prev_balance,daily_balance_updated_on , balance, last_transfer_type, last_transfer_no, last_transfer_on , ");
        sqlBuffer.append(" user_id, product_code , network_code, network_code_for,  ");
        /** START: Birendra: 30JAN2015 */
        sqlBuffer.append(" balance_type  ");
        /** STOP: Birendra: 30JAN2015 */
        sqlBuffer.append(" ) VALUES ");
        sqlBuffer.append(" (?,?,?,?,?,?,?,?,?,?,?) ");
        final String insertBalance = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY insertBalance=" + insertBalance);
        }
        sqlBuffer = null;

        // Select the running countres of user(to be checked against the
        // effetive profile counters)
        sqlBuffer = new StringBuffer(" SELECT user_id, daily_in_count, daily_in_value, weekly_in_count, weekly_in_value, ");
        sqlBuffer.append(" monthly_in_count, monthly_in_value,daily_out_count, daily_out_value, weekly_out_count, ");
        sqlBuffer.append(" weekly_out_value, monthly_out_count, monthly_out_value, outside_daily_in_count, ");
        sqlBuffer.append(" outside_daily_in_value, outside_weekly_in_count, outside_weekly_in_value, ");
        sqlBuffer.append(" outside_monthly_in_count, outside_monthly_in_value, outside_daily_out_count, ");
        sqlBuffer.append(" outside_daily_out_value, outside_weekly_out_count, outside_weekly_out_value, ");
        sqlBuffer.append(" outside_monthly_out_count, outside_monthly_out_value, daily_subscriber_out_count, ");
        sqlBuffer.append(" daily_subscriber_out_value, weekly_subscriber_out_count, weekly_subscriber_out_value, ");
        sqlBuffer.append(" monthly_subscriber_out_count, monthly_subscriber_out_value,last_transfer_date ");
        sqlBuffer.append(" ,last_sos_txn_status,last_lr_status ");
        sqlBuffer.append(" FROM user_transfer_counts ");
        // DB220120123for update WITH RS
        // sqlBuffer.append(" WHERE user_id = ? FOR UPDATE ");

        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
            sqlBuffer.append(" WHERE user_id = ? FOR UPDATE WITH RS");
        } else {
            sqlBuffer.append(" WHERE user_id = ? FOR UPDATE ");
        }
        final String selectTransferCounts = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY selectTransferCounts=" + selectTransferCounts);
        }
        sqlBuffer = null;

        // Select the effective profile counters of user to be checked with
        // running counters of user
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT tp.profile_id,LEAST(tp.daily_transfer_in_count,catp.daily_transfer_in_count) daily_transfer_in_count, ");
        strBuff
            .append(" LEAST(tp.daily_transfer_in_value,catp.daily_transfer_in_value) daily_transfer_in_value ,LEAST(tp.weekly_transfer_in_count,catp.weekly_transfer_in_count) weekly_transfer_in_count, ");
        strBuff
            .append(" LEAST(tp.weekly_transfer_in_value,catp.weekly_transfer_in_value) weekly_transfer_in_value,LEAST(tp.monthly_transfer_in_count,catp.monthly_transfer_in_count) monthly_transfer_in_count, ");
        strBuff.append(" LEAST(tp.monthly_transfer_in_value,catp.monthly_transfer_in_value) monthly_transfer_in_value");
        strBuff.append(" FROM transfer_profile tp,transfer_profile catp ");
        strBuff.append(" WHERE tp.profile_id = ? AND tp.status = ?	AND tp.network_code = ? ");
        strBuff.append(" AND tp.category_code=catp.category_code ");
        strBuff.append(" AND catp.parent_profile_id=? AND catp.status=?	AND tp.network_code = catp.network_code ");
        final String selectProfileCounts = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY selectProfileCounts=" + selectProfileCounts);
        }

        // Update the user running countres (If record found for user running
        // counters)
        sqlBuffer = new StringBuffer(" UPDATE user_transfer_counts  SET ");
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
        final String updateTransferCounts = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY updateTransferCounts=" + updateTransferCounts);
        }
        sqlBuffer = null;

        // Insert the record in user_transfer_counts (If no record found for
        // user running counters)
        sqlBuffer = new StringBuffer(" INSERT INTO user_transfer_counts ( ");
        sqlBuffer.append(" daily_in_count, daily_in_value, weekly_in_count, weekly_in_value, monthly_in_count, ");
        sqlBuffer.append(" monthly_in_value, last_in_time, last_transfer_id,last_transfer_date,user_id ) ");
        sqlBuffer.append(" VALUES (?,?,?,?,?,?,?,?,?,?) ");
        final String insertTransferCounts = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY insertTransferCounts=" + insertTransferCounts);
        }
        sqlBuffer = null;

        // If current level of approval is 1 then below query is used to updatwe
        // foc_batch_items table
        sqlBuffer = new StringBuffer(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" reference_no=?, modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" first_approver_remarks = ?, ");
        sqlBuffer.append(" first_approved_by=?, first_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status IN (? , ? )  ");
        final String sqlApprv1FOCBatchItems = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlApprv1FOCBatchItems=" + sqlApprv1FOCBatchItems);
        }
        sqlBuffer = null;

        // If current level of approval is 2 then below query is used to updatwe
        // foc_batch_items table
        sqlBuffer = new StringBuffer(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" reference_no=?, modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" second_approver_remarks = ?, ");
        sqlBuffer.append(" second_approved_by=? , second_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=? ");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status IN (? , ? )  ");
        final String sqlApprv2FOCBatchItems = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlApprv2FOCBatchItems=" + sqlApprv2FOCBatchItems);
        }
        sqlBuffer = null;

        // If current level of approval is 3 then below query is used to updatwe
        // foc_batch_items table
        sqlBuffer = new StringBuffer(" UPDATE  foc_batch_items SET   ");
        sqlBuffer.append(" reference_no=?, modified_by = ?, modified_on = ?,  ");
        sqlBuffer.append(" third_approver_remarks = ?, ");
        sqlBuffer.append(" third_approved_by=? , third_approved_on=? , status = ? , ext_txn_no=? , ext_txn_date=?");
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(" batch_detail_id = ? ");
        sqlBuffer.append(" AND status = ?  ");
        final String sqlApprv3FOCBatchItems = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlApprv3FOCBatchItems=" + sqlApprv3FOCBatchItems);
        }
        sqlBuffer = null;

        // Afetr all teh records are processed the the below query is used to
        // load the various counts such as new ,
        // apprv1, close ,cancled etc. These couts will be used to deceide what
        // status to be updated in mater table
        final String selectItemsDetails = focBatchTransferWebQry.SelectItemsDetailsQry();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY selectItemsDetails=" + selectItemsDetails);
        }
        sqlBuffer = null;

        // Update the master table after all records are processed
        sqlBuffer = new StringBuffer("UPDATE foc_batches SET status=? , modified_by=? ,modified_on=? ,sms_default_lang=? ,sms_second_lang=?");
        sqlBuffer.append(" WHERE batch_id=? AND status=? ");
        final String updateFOCBatches = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY updateFOCBatches=" + updateFOCBatches);
        }
        sqlBuffer = null;

        // The query below is used to check is record is already modified or not
        sqlBuffer = new StringBuffer("SELECT modified_on FROM foc_batch_items ");
        sqlBuffer.append("WHERE batch_detail_id = ? ");
        final String isModified = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY isModified=" + isModified);
        }
        sqlBuffer = null;

        // Select the transfer profile product values(These will be used for
        // checking max balance of user)
        sqlBuffer = new StringBuffer("SELECT GREATEST(tpp.min_residual_balance,catpp.min_residual_balance) min_residual_balance, ");
        sqlBuffer.append(" LEAST(tpp.max_balance,catpp.max_balance) max_balance ");
        sqlBuffer.append(" FROM transfer_profile_products tpp,transfer_profile tp, transfer_profile catp,transfer_profile_products catpp ");
        sqlBuffer.append(" WHERE tpp.profile_id=? AND tpp.product_code = ? AND tpp.profile_id=tp.profile_id AND catp.profile_id=catpp.profile_id ");
        sqlBuffer
            .append(" AND tpp.product_code=catpp.product_code AND tp.category_code=catp.category_code AND catp.parent_profile_id=? AND catp.status=? AND tp.network_code = catp.network_code	 ");
        final String loadTransferProfileProduct = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY loadTransferProfileProduct=" + loadTransferProfileProduct);
        }
        sqlBuffer = null;

        // The below query will be exceute if external txn number unique is "Y"
        // in system preferences
        // This will check the existence of external txn number in
        // foc_batch_items table
        sqlBuffer = new StringBuffer(" SELECT 1 FROM foc_batch_items ");
        sqlBuffer.append("WHERE ext_txn_no=? AND status <> ? AND batch_detail_id<>? ");
        final String isExistsTxnNum1 = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY isExistsTxnNum1=" + isExistsTxnNum1);
        }
        sqlBuffer = null;

        // The below query will be exceute if external txn number unique is "Y"
        // in system preferences
        // and external txn number is not exists in foc_batch_items table.
        // This will check the existence of external txn number in
        // channel_transfers table
        sqlBuffer = new StringBuffer("  SELECT 1 FROM channel_transfers ");
        sqlBuffer.append("WHERE type=? AND ext_txn_no=? AND status <> ? ");
        final String isExistsTxnNum2 = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY isExistsTxnNum2=" + isExistsTxnNum2);
        }
        sqlBuffer = null;

        // The query below is used to insert the record in channel transfer
        // items table for the order that is closed
        sqlBuffer = new StringBuffer(" INSERT INTO channel_transfers_items ");
        sqlBuffer.append("(approved_quantity, commission_profile_detail_id, commission_rate, commission_type, commission_value, mrp,  ");
        sqlBuffer.append(" net_payable_amount, payable_amount, product_code, receiver_previous_stock, required_quantity, s_no,  ");
        sqlBuffer.append(" sender_previous_stock, tax1_rate, tax1_type, tax1_value, tax2_rate, tax2_type, tax2_value, tax3_rate, tax3_type,  ");
        sqlBuffer
            .append(" tax3_value, transfer_date, transfer_id, user_unit_price, sender_debit_quantity, receiver_credit_quantity,commision_quantity, sender_post_stock, receiver_post_stock)  ");
        sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)  ");
        final String insertIntoChannelTransferItem = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY insertIntoChannelTransferItem=" + insertIntoChannelTransferItem);
        }
        sqlBuffer = null;

        // The query below is used to insert the record in channel transfers
        // table for the order that is closed
        sqlBuffer = new StringBuffer(" INSERT INTO channel_transfers ");
        sqlBuffer.append(" (cancelled_by, cancelled_on, channel_user_remarks, close_date, commission_profile_set_id, commission_profile_ver, ");
        sqlBuffer.append(" created_by, created_on, domain_code, ext_txn_date, ext_txn_no, first_approved_by, first_approved_on, ");
        sqlBuffer.append(" first_approver_limit, first_approver_remarks, batch_date, batch_no, from_user_id, grph_domain_code, ");
        sqlBuffer.append(" modified_by, modified_on, net_payable_amount, network_code, network_code_for, payable_amount, pmt_inst_amount, ");
        sqlBuffer.append("  product_type, receiver_category_code, receiver_grade_code, ");
        sqlBuffer.append(" receiver_txn_profile, reference_no, request_gateway_code, request_gateway_type, requested_quantity, second_approved_by, ");
        sqlBuffer.append(" second_approved_on, second_approver_limit, second_approver_remarks,  ");
        sqlBuffer.append("  source, status, third_approved_by, third_approved_on, third_approver_remarks, to_user_id,  ");
        sqlBuffer.append(" total_tax1, total_tax2, total_tax3, transfer_category, transfer_date, transfer_id, transfer_initiated_by, ");
        sqlBuffer.append(" transfer_mrp, transfer_sub_type, transfer_type, type,sender_category_code,");
        sqlBuffer.append(" control_transfer,to_msisdn,to_domain_code,to_grph_domain_code, sms_default_lang, sms_second_lang,active_user_id,dual_comm_type");
        if (isMultipleWalletApply) {
            sqlBuffer.append(",TXN_WALLET)");
            sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        } else {
            sqlBuffer.append(") ");
            sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        }
        final String insertIntoChannelTransfer = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY insertIntoChannelTransfer=" + insertIntoChannelTransfer);
        }
        sqlBuffer = null;

        // The query below is used to get the balance information of user with
        // product.
        // This information will be send in message to user
        sqlBuffer = new StringBuffer(" SELECT UB.product_code,UB.balance, ");
        sqlBuffer.append(" PROD.product_short_code, PROD.short_name ");
        sqlBuffer.append(" FROM user_balances UB,products PROD ");
        sqlBuffer.append(" WHERE UB.user_id = ?  AND UB.network_code = ? AND UB.network_code_for = ? AND UB.product_code=PROD.product_code ");
        final String selectBalanceInfoForMessage = sqlBuffer.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY selectBalanceInfoForMessage=" + selectBalanceInfoForMessage);
        }

        // added by nilesh:added two new columns threshold_type and remark
        final StringBuffer strBuffThresholdInsert = new StringBuffer();
        strBuffThresholdInsert.append(" INSERT INTO user_threshold_counter ");
        strBuffThresholdInsert.append(" ( user_id,transfer_id , entry_date, entry_date_time, network_code, product_code , ");
        strBuffThresholdInsert.append(" type , transaction_type, record_type, category_code,previous_balance,current_balance, threshold_value, threshold_type, remark ) ");
        strBuffThresholdInsert.append(" VALUES ");
        strBuffThresholdInsert.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        final String insertUserThreshold = strBuffThresholdInsert.toString();
        if (_log.isDebugEnabled()) {
            _log.debug("closeOrederByBatch", "QUERY insertUserThreshold=" + insertUserThreshold);
        }

        sqlBuffer = null;
        Date date = null;
        String batch_ID = null;
        ChannelTransferVO channelTransferVO = null;
        try {
            FOCBatchItemsVO focBatchItemVO = null;
            ChannelUserVO channelUserVO = null;
            // ChannelTransferVO channelTransferVO=null;
            ChannelTransferItemsVO channelTransferItemVO = null;
            date = new Date();
            ArrayList channelTransferItemVOList = null;
            pstmtLoadUser = p_con.prepareStatement(sqlLoadUser);
            pstmtLoadNetworkStock = p_con.prepareStatement(sqlLoadNetworkStock);
            pstmtUpdateNetworkStock = p_con.prepareStatement(sqlUpdateNetworkStock);
            pstmtInsertNetworkDailyStock = p_con.prepareStatement(sqlInsertNetworkDailyStock);
            pstmtSelectNetworkStock = p_con.prepareStatement(sqlSelectNetworkStock);
            pstmtupdateSelectedNetworkStock = p_con.prepareStatement(updateSelectedNetworkStock);
            // pstmtInsertNetworkStockTransaction=(OraclePreparedStatement)p_con.prepareStatement(insertNetworkStockTransaction);
            pstmtInsertNetworkStockTransaction = p_con.prepareStatement(insertNetworkStockTransaction);
            pstmtInsertNetworkStockTransactionItem = p_con.prepareStatement(insertNetworkStockTransactionItem);
            pstmtSelectUserBalances = p_con.prepareStatement(selectUserBalances);
            pstmtUpdateUserBalances = p_con.prepareStatement(updateUserBalances);
            pstmtInsertUserDailyBalances = p_con.prepareStatement(insertUserDailyBalances);
            pstmtSelectBalance = p_con.prepareStatement(selectBalance);
            pstmtUpdateBalance = p_con.prepareStatement(updateBalance);
            pstmtInsertBalance = p_con.prepareStatement(insertBalance);
            pstmtSelectTransferCounts = p_con.prepareStatement(selectTransferCounts);
            pstmtSelectProfileCounts = p_con.prepareStatement(selectProfileCounts);
            pstmtUpdateTransferCounts = p_con.prepareStatement(updateTransferCounts);
            pstmtInsertTransferCounts = p_con.prepareStatement(insertTransferCounts);

            // psmtAppr1FOCBatchItem=(OraclePreparedStatement)p_con.prepareStatement(sqlApprv1FOCBatchItems);//commented
            // for DB2
            // psmtAppr2FOCBatchItem=(OraclePreparedStatement)p_con.prepareStatement(sqlApprv2FOCBatchItems);//commented
            // for DB2
            // psmtAppr3FOCBatchItem=(OraclePreparedStatement)p_con.prepareStatement(sqlApprv3FOCBatchItems);//commented
            // for DB2
            psmtAppr1FOCBatchItem = p_con.prepareStatement(sqlApprv1FOCBatchItems);
            psmtAppr2FOCBatchItem = p_con.prepareStatement(sqlApprv2FOCBatchItems);
            psmtAppr3FOCBatchItem = p_con.prepareStatement(sqlApprv3FOCBatchItems);
            pstmtSelectItemsDetails = p_con.prepareStatement(selectItemsDetails);
            // pstmtUpdateMaster=(OraclePreparedStatement)p_con.prepareStatement(updateFOCBatches);//commented
            // for DB2
            pstmtUpdateMaster = p_con.prepareStatement(updateFOCBatches);
            pstmtIsModified = p_con.prepareStatement(isModified);
            pstmtLoadTransferProfileProduct = p_con.prepareStatement(loadTransferProfileProduct);
            pstmtIsTxnNumExists1 = p_con.prepareStatement(isExistsTxnNum1);
            pstmtIsTxnNumExists2 = p_con.prepareStatement(isExistsTxnNum2);
            pstmtInsertIntoChannelTransferItems = p_con.prepareStatement(insertIntoChannelTransferItem);
            // commented for DB2
            // pstmtInsertIntoChannelTranfers=(OraclePreparedStatement)p_con.prepareStatement(insertIntoChannelTransfer);
            pstmtInsertIntoChannelTranfers = p_con.prepareStatement(insertIntoChannelTransfer);
            pstmtSelectBalanceInfoForMessage = p_con.prepareStatement(selectBalanceInfoForMessage);
            psmtInsertUserThreshold = p_con.prepareStatement(insertUserThreshold);
            errorList = new ArrayList();
            final Iterator iterator = p_dataMap.keySet().iterator();
            String key = null;
            final MessageGatewayVO messageGatewayVO = MessageGatewayCache.getObject(defaultWebGatewayCode);
            NetworkStockVO networkStocksVO = null;
            int dayDifference = 0;
            Date dailyStockUpdatedOn = null;
            long stock = -1;
            long stockSold = -1;
            NetworkStockTxnVO networkStockTxnVO = null;
            String network_id = null;
            Date dailyBalanceUpdatedOn = null;
            NetworkStockTxnItemsVO networkItemsVO = null;
            TransferProfileProductVO transferProfileProductVO = null;
            UserTransferCountsVO countsVO = null;
            TransferProfileVO transferProfileVO = null;
            long maxBalance = 0;
            boolean isNotToExecuteQuery = false;
            long balance = -1;
            long previousUserBalToBeSetChnlTrfItems = -1;
            long previousNwStockToBeSetChnlTrfItems = -1;
            int m = 0;
            int k = 0;
            boolean flag = true;
            boolean terminateProcessing = false;
            while (iterator.hasNext()) {
                terminateProcessing = false;
                key = (String) iterator.next();
                focBatchItemVO = (FOCBatchItemsVO) p_dataMap.get(key);
                if (BTSLUtil.isNullString(batch_ID)) {
                    batch_ID = focBatchItemVO.getBatchId();
                }
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Executed focBatchItemVO=" + focBatchItemVO.toString());
                }
                pstmtLoadUser.clearParameters();
                m = 0;
                ++m;
                pstmtLoadUser.setString(m, focBatchItemVO.getUserId());
                for (int x = 0; x < m_receiverStatusAllowed.length; x++) {
                    ++m;
                    pstmtLoadUser.setString(m, m_receiverStatusAllowed[x]);
                }
                try {
                rs = pstmtLoadUser.executeQuery();
                // (record found for user i.e. receiver) if this condition is
                // not true then made entry in logs and leave this data.
                if (rs.next()) {
                    channelUserVO = new ChannelUserVO();
                    channelUserVO.setUserID(focBatchItemVO.getUserId());
                    channelUserVO.setStatus(rs.getString("userstatus"));
                    channelUserVO.setInSuspend(rs.getString("in_suspend"));
                    channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
                    channelUserVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
                    channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
                    channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
                    channelUserVO.setTransferProfileStatus(rs.getString("profile_status"));
                    language = rs.getString("phone_language");
                    country = rs.getString("country");
                    channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
                    
                    // (user status is checked) if this condition is true then
                    // made entry in logs and leave this data.
                    /*
                     * if(!PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.
                     * getStatus()))
                     * {
                     * p_con.rollback();
                     * errorVO=new
                     * ListValueVO(focBatchItemVO.getMsisdn(),String.
                     * valueOf(focBatchItemVO
                     * .getRecordNumber()),p_messages.getMessage
                     * (p_locale,"batchfoc.batchapprovereject.msg.error.usersuspend"
                     * ));
                     * errorList.add(errorVO);
                     * BatchFocFileProcessLog.detailLog("closeOrederByBatch",
                     * p_focBatchMatserVO
                     * ,focBatchItemVO,"FAIL : User is suspend"
                     * ,"Approval level"+p_currentLevel);
                     * continue;
                     * }
                     */
                    // (commission profile status is checked) if this condition
                    // is true then made entry in logs and leave this data.
                    if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchfoc.batchapprovereject.msg.error.comprofsuspend"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Commission profile suspend",
                            "Approval level" + p_currentLevel);
                        continue;
                    }
                    // (transfer profile is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchfoc.batchapprovereject.msg.error.trfprofsuspend"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Transfer profile suspend", "Approval level" + p_currentLevel);
                        continue;
                    }
                    // (user in suspend is checked) if this condition is true
                    // then made entry in logs and leave this data.
                    else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchfoc.batchapprovereject.msg.error.userinsuspend"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : User is IN suspend", "Approval level" + p_currentLevel);
                        continue;
                    } else if (PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL.equalsIgnoreCase(focBatchItemVO.getStatus())) {
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "CANCEL : ", focBatchItemVO.getBatchDetailId());
                        continue;
                    }
                }
                // (no record found for user i.e. receiver) if this condition is
                // true then made entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.nouser"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : User not found", "Approval level" + p_currentLevel);
                    continue;
                }
                }
                finally {
                	if(rs!=null)
                		rs.close();
                }
                networkStocksVO = new NetworkStockVO();
                networkStocksVO.setProductCode(p_focBatchMatserVO.getProductCode());
                networkStocksVO.setNetworkCode(p_focBatchMatserVO.getNetworkCode());
                networkStocksVO.setNetworkCodeFor(p_focBatchMatserVO.getNetworkCodeFor());

                // creating the channelTransferVO here since O2CTransferID will
                // be required into the network stock
                // transaction table. Other information will be set into this VO
                // later
                channelTransferVO = new ChannelTransferVO();
                // seting the current value for generation of the transfer ID.
                // This will be over write by the
                // bacth foc items was created.
                channelTransferVO.setCreatedOn(date);
                channelTransferVO.setNetworkCode(p_focBatchMatserVO.getNetworkCode());
                
                channelTransferVO.setNetworkCodeFor(p_focBatchMatserVO.getNetworkCodeFor());
                channelTransferVO.setToUserID(focBatchItemVO.getUserId());
                channelTransferVO.setProductCode(p_focBatchMatserVO.getProductCode()); 
                ChannelTransferBL.genrateTransferID(channelTransferVO);
                o2cTransferID = channelTransferVO.getTransferID();
                // value is over writing since in the channel trasnfer table
                // created on should be same as when the
                // batch foc item was created.
                channelTransferVO.setCreatedOn(focBatchItemVO.getInitiatedOn());

                networkStocksVO.setLastTxnNum(o2cTransferID);
                /*
                 * changed on 20/07/06 as already in batch items the entries are
                 * in lowest denomination
                 * networkStocksVO.setLastTxnStock(PretupsBL.getSystemAmount(
                 * focBatchItemVO.getRequestedQuantity()));
                 * networkStocksVO.setStock(PretupsBL.getSystemAmount(focBatchItemVO
                 * .getRequestedQuantity()));
                 */
                networkStocksVO.setLastTxnBalance(focBatchItemVO.getRequestedQuantity());
                networkStocksVO.setWalletBalance(focBatchItemVO.getRequestedQuantity());
                if (isMultipleWalletApply) {
                    networkStocksVO.setWalletType(PretupsI.FOC_WALLET_TYPE);
                } else {
                	networkStocksVO.setWalletType(PretupsI.SALE_WALLET_TYPE);
                }
                networkStocksVO.setLastTxnType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                networkStocksVO.setModifiedBy(p_userID);
                networkStocksVO.setModifiedOn(date);
                dailyStockUpdatedOn = null;
                dayDifference = 0;
                // select the record form the network stock table.
                pstmtLoadNetworkStock.clearParameters();
                m = 0;
                ++m;
                pstmtLoadNetworkStock.setString(m, networkStocksVO.getNetworkCode());
                ++m;
                pstmtLoadNetworkStock.setString(m, networkStocksVO.getNetworkCodeFor());
                if (isMultipleWalletApply) {
                    ++m;
                    pstmtLoadNetworkStock.setString(m, PretupsI.FOC_WALLET_TYPE);
                } else {
                    ++m;
                    pstmtLoadNetworkStock.setString(m, PretupsI.SALE_WALLET_TYPE);
                }
                ++m;
                pstmtLoadNetworkStock.setDate(m, BTSLUtil.getSQLDateFromUtilDate(date));
               
                rs = null;
                try {
                rs = pstmtLoadNetworkStock.executeQuery();
                while (rs.next()) {
                    dailyStockUpdatedOn = rs.getDate("daily_stock_updated_on");

                    // if record exist check updated on date with current date
                    // day differences to maintain the record of previous days.
                    dayDifference = BTSLUtil.getDifferenceInUtilDates(dailyStockUpdatedOn, date);

                    if (dayDifference > 0) {
                        // if dates are not equal get the day differencts and
                        // execute insert qurery no of times of the difference
                        // is.
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "Till now daily Stock is not updated on = " + date + ", day differences = " + dayDifference);
                        }

                        for (k = 0; k < dayDifference; k++) {
                            pstmtInsertNetworkDailyStock.clearParameters();
                            m = 0;
                            ++m;
                            pstmtInsertNetworkDailyStock.setDate(m, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyStockUpdatedOn, k)));
                            if (isMultipleWalletApply) {
                                ++m;
                                pstmtInsertNetworkDailyStock.setString(m, PretupsI.FOC_WALLET_TYPE);
                            } else {
                                ++m;
                                pstmtInsertNetworkDailyStock.setString(m, PretupsI.SALE_WALLET_TYPE);
                            }
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, rs.getString("network_code"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, rs.getString("network_code_for"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, rs.getString("product_code"));
                            /*
                             * if(isMultipleWalletApply)
                             * {
                             * pstmtInsertNetworkDailyStock.setLong(++m,rs.getLong
                             * ("wallet_created"));
                             * pstmtInsertNetworkDailyStock.setLong(++m,rs.getLong
                             * ("wallet_returned"));
                             * pstmtInsertNetworkDailyStock.setLong(++m,rs.getLong
                             * ("wallet_balance"));
                             * pstmtInsertNetworkDailyStock.setLong(++m,rs.getLong
                             * ("wallet_sold"));
                             * 
                             * pstmtInsertNetworkDailyStock.setString(++m,
                             * channelTransferVO.getTransferID());
                             * pstmtInsertNetworkDailyStock.setString(++m,
                             * networkStocksVO.getLastTxnType());
                             * pstmtInsertNetworkDailyStock.setLong(++m,rs.getLong
                             * ("last_txn_balance"));
                             * pstmtInsertNetworkDailyStock.setLong(++m,rs.getLong
                             * ("previous_balance"));
                             * }
                             * else
                             * {
                             */
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs.getLong("wallet_created"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs.getLong("wallet_returned"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs.getLong("wallet_balance"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs.getLong("wallet_sold"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, channelTransferVO.getTransferID());
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, networkStocksVO.getLastTxnType());
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs.getLong("last_txn_balance"));
                            ++m;
                            pstmtInsertNetworkDailyStock.setLong(m, rs.getLong("previous_balance"));
                            ++m;
                            // }
                            pstmtInsertNetworkDailyStock.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                            ++m;
                            pstmtInsertNetworkDailyStock.setString(m, PretupsI.DAILY_STOCK_CREATION_TYPE_MAN);
                            updateCount = pstmtInsertNetworkDailyStock.executeUpdate();
							// added to make code compatible with insertion in partitioned table in postgres
							updateCount = BTSLUtil.getInsertCount(updateCount); 
                            if (updateCount <= 0) {
                                p_con.rollback();
                                errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                                    "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                                errorList.add(errorVO);
                                BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while insert in network daily stock table",
                                    "Approval level = " + p_currentLevel + ", updateCount = " + updateCount);
                                terminateProcessing = true;
                                break;
                            }
                        }// end of for loop
                         // if updation of daily network stock is fail then
                         // terminate the processing
                        if (terminateProcessing) {
                            BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Termination of the procissing",
                                "Approval level = " + p_currentLevel);
                            break;
                        }
                        // Update the network stock table
                        pstmtUpdateNetworkStock.clearParameters();
                        m = 0;
                        ++m;
                        pstmtUpdateNetworkStock.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                        ++m;
                        pstmtUpdateNetworkStock.setString(m, networkStocksVO.getNetworkCode());
                        ++m;
                        pstmtUpdateNetworkStock.setString(m, networkStocksVO.getNetworkCodeFor());
                        if (isMultipleWalletApply) {
                            ++m;
                            pstmtUpdateNetworkStock.setString(m, PretupsI.FOC_WALLET_TYPE);
                        } else {
                            ++m;
                            pstmtUpdateNetworkStock.setString(m, PretupsI.SALE_WALLET_TYPE);
                        }
                        updateCount = pstmtUpdateNetworkStock.executeUpdate();
                        // (record not updated properly in db) if this condition
                        // is true then made entry in logs and leave this data.
                        if (updateCount <= 0) {
                            p_con.rollback();
                            errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                                "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                            errorList.add(errorVO);
                            BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating network daily stock table",
                                "Approval level = " + p_currentLevel + ", updateCount = " + updateCount);
                            continue;
                        }
                    }
                }// end of if () for daily network stock updation
                }
                finally {
                	if(rs!=null)
                		rs.close();
                }
                pstmtSelectNetworkStock.clearParameters();
                m = 0;
                ++m;
                pstmtSelectNetworkStock.setString(m, networkStocksVO.getNetworkCode());
                ++m;
                pstmtSelectNetworkStock.setString(m, networkStocksVO.getProductCode());
                ++m;
                pstmtSelectNetworkStock.setString(m, networkStocksVO.getNetworkCodeFor());
                ++m;
                pstmtSelectNetworkStock.setString(m,focBatchItemVO.getCommWalletType());
                
                
                
                rs = null;
                try {
                rs = pstmtSelectNetworkStock.executeQuery();
                stock = -1;
                stockSold = -1;
                previousNwStockToBeSetChnlTrfItems = -1;
                // get the network stock
                if (rs.next()) {
                    /*
                     * if(isMultipleWalletApply)
                     * {
                     * stock = rs.getLong("foc_stock");
                     * stockSold = rs.getLong("foc_stock_sold");
                     * }
                     * else
                     * {
                     */
                    stock = rs.getLong("wallet_balance");
                    stockSold = rs.getLong("wallet_sold");
                    // }
                    previousNwStockToBeSetChnlTrfItems = stock;
                   
                }
                // (network stock not found) if this condition is true then made
                // entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(PretupsRestUtil.getMessageString( "label.all"), String.valueOf(focBatchItemVO.getRecordNumber()) + " - " + PretupsRestUtil.getMessageString( "label.all"), PretupsRestUtil.getMessageString( "batchfoc.batchapprovereject.msg.error.networkstocknotexiststopprocess"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO,
                        "FAIL : Network stock not exists. So all records after this can not be processed", "Approval level = " + p_currentLevel);
                    throw new BTSLBaseException(this, methodName, "batchfoc.batchapprovereject.msg.error.networkstocknotexiststopprocess");

                }
                }
                finally {
                	if(rs!=null)
                		rs.close();
                }
                // (network stock is less) if this condition is true then made
                // entry in logs and leave this data.
                if (stock <= networkStocksVO.getWalletbalance()) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.networkstocklessstopprocess"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO,
                        "FAIL : Network stock is less than requested quantity. So all records after this can not be processed", "Approval level = " + p_currentLevel);
                    continue;
                }
                if (stock != -1) {
                    stock -= networkStocksVO.getWalletbalance();
                }
                if (stockSold != -1) {
                    stockSold += networkStocksVO.getWalletbalance();
                }
                m = 0;
                // Debit the network stock
                pstmtupdateSelectedNetworkStock.clearParameters();
                ++m;
                pstmtupdateSelectedNetworkStock.setLong(m, stock);
                ++m;
                pstmtupdateSelectedNetworkStock.setLong(m, stockSold);
                ++m;
                pstmtupdateSelectedNetworkStock.setString(m, networkStocksVO.getLastTxnNum());
                ++m;
                pstmtupdateSelectedNetworkStock.setString(m, networkStocksVO.getLastTxnType());
                ++m;
                pstmtupdateSelectedNetworkStock.setLong(m, networkStocksVO.getLastTxnBalance());
                ++m;
                pstmtupdateSelectedNetworkStock.setString(m, networkStocksVO.getModifiedBy());
                ++m;
                pstmtupdateSelectedNetworkStock.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStocksVO.getModifiedOn()));
                ++m;
                pstmtupdateSelectedNetworkStock.setString(m, networkStocksVO.getNetworkCode());
                ++m;
                pstmtupdateSelectedNetworkStock.setString(m, networkStocksVO.getProductCode());
                ++m;
                pstmtupdateSelectedNetworkStock.setString(m, networkStocksVO.getNetworkCodeFor());
                if (isMultipleWalletApply) {
                    ++m;
                    pstmtupdateSelectedNetworkStock.setString(m, PretupsI.FOC_WALLET_TYPE);
                } else {
                    ++m;
                    pstmtupdateSelectedNetworkStock.setString(m, PretupsI.SALE_WALLET_TYPE);
                }
                updateCount = pstmtupdateSelectedNetworkStock.executeUpdate();
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating network stock table",
                        "Approval level = " + p_currentLevel + ", updateCount = " + updateCount);
                    continue;
                }

                // for logging
                networkStocksVO.setPreviousBalance(stock);
             // AutoNetworkStockCreation logic
                if((boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.AUTO_NWSTK_CRTN_ALWD, networkStocksVO.getNetworkCode())){
                	new com.btsl.pretups.channel.transfer.businesslogic.AutoNetworkStockBL().networkStockThresholdValidation(networkStocksVO);
                }
                networkStockTxnVO = new NetworkStockTxnVO();
                networkStockTxnVO.setNetworkCode(networkStocksVO.getNetworkCode());
                networkStockTxnVO.setNetworkFor(networkStocksVO.getNetworkCodeFor());
                if (networkStocksVO.getNetworkCode().equals(p_focBatchMatserVO.getNetworkCodeFor())) {
                    networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_HOME);
                } else {
                    networkStockTxnVO.setStockType(PretupsI.TRANSFER_STOCK_TYPE_ROAM);
                }
                // As discussed with sandeep in channel transfer table's
                // reference number field we have
                // to insert batch details id.So In network stock where channel
                // transfer table's reference number
                // was inserted, I insert batch detail id.
                networkStockTxnVO.setReferenceNo(focBatchItemVO.getBatchDetailId());
                networkStockTxnVO.setTxnDate(focBatchItemVO.getInitiatedOn());
                networkStockTxnVO.setRequestedQuantity(focBatchItemVO.getRequestedQuantity());
                networkStockTxnVO.setApprovedQuantity(focBatchItemVO.getRequestedQuantity());
                networkStockTxnVO.setInitiaterRemarks(focBatchItemVO.getInitiatorRemarks());
                networkStockTxnVO.setFirstApprovedRemarks(focBatchItemVO.getFirstApproverRemarks());
                networkStockTxnVO.setSecondApprovedRemarks(focBatchItemVO.getSecondApproverRemarks());
                networkStockTxnVO.setFirstApprovedBy(focBatchItemVO.getFirstApprovedBy());
                networkStockTxnVO.setSecondApprovedBy(focBatchItemVO.getSecondApprovedBy());
                networkStockTxnVO.setFirstApprovedOn(focBatchItemVO.getFirstApprovedOn());
                networkStockTxnVO.setSecondApprovedOn(focBatchItemVO.getSecondApprovedOn());
                networkStockTxnVO.setCancelledBy(focBatchItemVO.getCancelledBy());
                networkStockTxnVO.setCancelledOn(focBatchItemVO.getCancelledOn());
                networkStockTxnVO.setCreatedBy(p_userID);
                networkStockTxnVO.setCreatedOn(date);
                networkStockTxnVO.setModifiedOn(date);
                networkStockTxnVO.setModifiedBy(p_userID);

                networkStockTxnVO.setTxnStatus(focBatchItemVO.getStatus());
                networkStockTxnVO.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_TRANSFER);
                networkStockTxnVO.setTxnType(PretupsI.DEBIT);
                networkStockTxnVO.setInitiatedBy(p_userID);
                networkStockTxnVO.setFirstApproverLimit(0);
                networkStockTxnVO.setUserID(focBatchItemVO.getInitiatedBy());
                networkStockTxnVO.setTxnMrp(focBatchItemVO.getTransferMrp());

                // generate network stock transaction id
                network_id = NetworkStockBL.genrateStockTransctionID(networkStockTxnVO);
                networkStockTxnVO.setTxnNo(network_id);

                networkItemsVO = new NetworkStockTxnItemsVO();
                networkItemsVO.setSNo(1);
                networkItemsVO.setTxnNo(networkStockTxnVO.getTxnNo());
                networkItemsVO.setRequiredQuantity(focBatchItemVO.getRequestedQuantity());
                networkItemsVO.setApprovedQuantity(focBatchItemVO.getRequestedQuantity());
                networkItemsVO.setMrp(focBatchItemVO.getTransferMrp());
                networkItemsVO.setProductCode(p_focBatchMatserVO.getProductCode());
                networkItemsVO.setAmount(0);
                networkItemsVO.setProductCode(p_focBatchMatserVO.getProductCode());
                networkItemsVO.setStock(previousNwStockToBeSetChnlTrfItems);
                // Added on 07/02/08
                networkItemsVO.setDateTime(p_focBatchMatserVO.getBatchDate());
                m = 0;
                pstmtInsertNetworkStockTransaction.clearParameters();
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getTxnNo());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getNetworkCode());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getNetworkFor());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getStockType());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getReferenceNo());
                if (networkStockTxnVO.getTxnDate() != null) {
                    ++m;
                    pstmtInsertNetworkStockTransaction.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getTxnDate()));
                } else {
                    ++m;
                    pstmtInsertNetworkStockTransaction.setTimestamp(m, null);
                }
                ++m;
                pstmtInsertNetworkStockTransaction.setLong(m, networkStockTxnVO.getRequestedQuantity());
                ++m;
                pstmtInsertNetworkStockTransaction.setLong(m, networkStockTxnVO.getApprovedQuantity());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getInitiaterRemarks());

                // for multilanguage support
                // commented for DB2
                // pstmtInsertNetworkStockTransaction.setFormOfUse(++m,
                // OraclePreparedStatement.FORM_NCHAR);
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getFirstApprovedRemarks());

                // for multilanguage support
                // commented for DB2
                // pstmtInsertNetworkStockTransaction.setFormOfUse(++m,
                // OraclePreparedStatement.FORM_NCHAR);
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getSecondApprovedRemarks());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getFirstApprovedBy());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getSecondApprovedBy());
                ++m;
                pstmtInsertNetworkStockTransaction.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getFirstApprovedOn()));
                ++m;
                pstmtInsertNetworkStockTransaction.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getSecondApprovedOn()));
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getCancelledBy());
                ++m;
                pstmtInsertNetworkStockTransaction.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getCancelledOn()));
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getCreatedBy());
                ++m;
                pstmtInsertNetworkStockTransaction.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getCreatedOn()));
                ++m;
                pstmtInsertNetworkStockTransaction.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getModifiedOn()));
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getModifiedBy());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getTxnStatus());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getEntryType());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getTxnType());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getInitiatedBy());
                ++m;
                pstmtInsertNetworkStockTransaction.setLong(m, networkStockTxnVO.getFirstApproverLimit());
                ++m;
                pstmtInsertNetworkStockTransaction.setString(m, networkStockTxnVO.getUserID());
                ++m;
                pstmtInsertNetworkStockTransaction.setLong(m, networkStockTxnVO.getTxnMrp());
                if (isMultipleWalletApply) {
                    ++m;
                    pstmtInsertNetworkStockTransaction.setString(m, PretupsI.FOC_WALLET_TYPE);
                    ++m;
                    pstmtInsertNetworkStockTransaction.setString(m, channelTransferVO.getTransferID());
                } else {
                    ++m;
                    pstmtInsertNetworkStockTransaction.setString(m, PretupsI.SALE_WALLET_TYPE);
                }
                updateCount = pstmtInsertNetworkStockTransaction.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating network stock TXN table",
                        "Approval level = " + p_currentLevel + ", updateCount = " + updateCount);
                    continue;
                }
                m = 0;
                pstmtInsertNetworkStockTransactionItem.clearParameters();
                ++m;
                pstmtInsertNetworkStockTransactionItem.setInt(m, networkItemsVO.getSNo());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setString(m, networkItemsVO.getTxnNo());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setString(m, networkItemsVO.getProductCode());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setLong(m, networkItemsVO.getRequiredQuantity());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setLong(m, networkItemsVO.getApprovedQuantity());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setLong(m, networkItemsVO.getStock());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setLong(m, networkItemsVO.getMrp());
                ++m;
                pstmtInsertNetworkStockTransactionItem.setLong(m, networkItemsVO.getAmount());
                // Date 07/02/08
                ++m;
                pstmtInsertNetworkStockTransactionItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));

                updateCount = pstmtInsertNetworkStockTransactionItem.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating network stock TXN itmes table",
                        "Approval level = " + p_currentLevel + ", updateCount = " + updateCount);
                    continue;
                }
                dailyBalanceUpdatedOn = null;
                dayDifference = 0;
                // select the record form the userBalances table.
                pstmtSelectUserBalances.clearParameters();
                m = 0;
                ++m;
                pstmtSelectUserBalances.setString(m, channelUserVO.getUserID());
                ++m;
                pstmtSelectUserBalances.setDate(m, BTSLUtil.getSQLDateFromUtilDate(date));
                
                rs = null;
                try {
                rs = pstmtSelectUserBalances.executeQuery();
                while (rs.next()) {
                    dailyBalanceUpdatedOn = rs.getDate("daily_balance_updated_on");
                    // if record exist check updated on date with current date
                    // day differences to maintain the record of previous days.
                    dayDifference = BTSLUtil.getDifferenceInUtilDates(dailyBalanceUpdatedOn, date);
                    if (dayDifference > 0) {
                        // if dates are not equal get the day differencts and
                        // execute insert qurery no of times of the
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "Till now daily Stock is not updated on = " + date + ", day differences = " + dayDifference);
                        }

                        for (k = 0; k < dayDifference; k++) {
                            pstmtInsertUserDailyBalances.clearParameters();
                            m = 0;
                            ++m;
                            pstmtInsertUserDailyBalances.setDate(m, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyBalanceUpdatedOn, k)));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, rs.getString("user_id"));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, rs.getString("network_code"));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, rs.getString("network_code_for"));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, rs.getString("product_code"));
                            ++m;
                            pstmtInsertUserDailyBalances.setLong(m, rs.getLong("balance"));
                            ++m;
                            pstmtInsertUserDailyBalances.setLong(m, rs.getLong("prev_balance"));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, channelTransferVO.getTransferID());
                            ++m;
                            pstmtInsertUserDailyBalances.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                            ++m;
                            pstmtInsertUserDailyBalances.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                            ++m;
                            pstmtInsertUserDailyBalances.setString(m, PretupsI.DAILY_BALANCE_CREATION_TYPE_MAN);
                            updateCount = pstmtInsertUserDailyBalances.executeUpdate();
							// added to make code compatible with insertion in partitioned table in postgres
							updateCount = BTSLUtil.getInsertCount(updateCount); 
                            if (updateCount <= 0) {
                                p_con.rollback();
                                errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                                    "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                                errorList.add(errorVO);
                                BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while inserting user daily balances table",
                                    "Approval level = " + p_currentLevel + ", updateCount = " + updateCount);
                                terminateProcessing = true;
                                break;
                            }
                        }// end of for loop
                        if (terminateProcessing) {
                            BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO,
                                "FAIL : Terminting the procssing of this user as error while updation daily balance", "Approval level = " + p_currentLevel);
                            continue;
                        }
                        // Update the user balances table
                        pstmtUpdateUserBalances.clearParameters();
                        m = 0;
                        ++m;
                        pstmtUpdateUserBalances.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                        ++m;
                        pstmtUpdateUserBalances.setString(m, channelUserVO.getUserID());
                        updateCount = pstmtUpdateUserBalances.executeUpdate();
                        // (record not updated properly) if this condition is
                        // true then made entry in logs and leave this data.
                        if (updateCount <= 0) {
                            p_con.rollback();
                            errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                                "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                            errorList.add(errorVO);
                            BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO,
                                "FAIL : DB Error while updating user balances table for daily balance",
                                "Approval level = " + p_currentLevel + ", updateCount = " + updateCount);
                            continue;
                        }
                    }
                }// end of if condition
                }
                finally {
                	if(rs!=null)
                		rs.close();
                }
                maxBalance = 0;
                isNotToExecuteQuery = false;
                pstmtSelectBalance.clearParameters();
                m = 0;
                ++m;
                pstmtSelectBalance.setString(m, channelUserVO.getUserID());
                ++m;
                pstmtSelectBalance.setString(m, p_focBatchMatserVO.getProductCode());
                ++m;
                pstmtSelectBalance.setString(m, p_focBatchMatserVO.getNetworkCode());
                ++m;
                pstmtSelectBalance.setString(m, p_focBatchMatserVO.getNetworkCodeFor());
                
                rs = null;
                try {
                rs = pstmtSelectBalance.executeQuery();
                balance = -1;
                previousUserBalToBeSetChnlTrfItems = -1;
                if (rs.next()) {
                    balance = rs.getLong("balance");
                                    }
                if (balance > -1) {
                    previousUserBalToBeSetChnlTrfItems = balance;
                    balance += focBatchItemVO.getRequestedQuantity();
                } else {
                    previousUserBalToBeSetChnlTrfItems = 0;
                }
                }
                finally {
                	if(rs!=null)
                		rs.close();
                }
                pstmtLoadTransferProfileProduct.clearParameters();
                m = 0;
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, focBatchItemVO.getTxnProfile());
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, p_focBatchMatserVO.getProductCode());
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, PretupsI.PARENT_PROFILE_ID_CATEGORY);
                ++m;
                pstmtLoadTransferProfileProduct.setString(m, PretupsI.YES);
                
                rs = null;
                try {
                rs = pstmtLoadTransferProfileProduct.executeQuery();
                // get the transfer profile of user
                if (rs.next()) {
                    transferProfileProductVO = new TransferProfileProductVO();
                    transferProfileProductVO.setProductCode(p_focBatchMatserVO.getProductCode());
                    transferProfileProductVO.setMinResidualBalanceAsLong(rs.getLong("min_residual_balance"));
                    transferProfileProductVO.setMaxBalanceAsLong(rs.getLong("max_balance"));
                    
                }
                // (transfer profile not found) if this condition is true then
                // made entry in logs and leave this data.
                else {
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.profcountersnotfound"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : User Trf Profile not found for product",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                }
                finally {
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
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.maxbalancereach"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : User Max balance reached", "Approval level = " + p_currentLevel);
                    continue;
                }
                // check for the very first txn of the user containg the order
                // value larger than maxBalance
                // (max balance reach) if this condition is true then made entry
                // in logs and leave this data.
                else if (balance == -1 && maxBalance < focBatchItemVO.getRequestedQuantity()) {
                    if (!isNotToExecuteQuery) {
                        isNotToExecuteQuery = true;
                    }
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.maxbalancereach"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : User Max balance reached", "Approval level = " + p_currentLevel);
                    continue;
                }
                if (!isNotToExecuteQuery) {
                    m = 0;
                    // update
                    if (balance > -1) {
                        pstmtUpdateBalance.clearParameters();
                        handlerStmt = pstmtUpdateBalance;
                    } else {
                        // insert
                        pstmtInsertBalance.clearParameters();
                        handlerStmt = pstmtInsertBalance;
                        balance = focBatchItemVO.getRequestedQuantity();
                        ++m;
                        handlerStmt.setLong(m, 0);// previous balance
                        ++m;
                        handlerStmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));// updated
                        // on
                        // date
                    }
                    ++m;
                    handlerStmt.setLong(m, balance);
                    ++m;
                    handlerStmt.setString(m, PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                    ++m;
                    handlerStmt.setString(m, o2cTransferID);
                    ++m;
                    handlerStmt.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    handlerStmt.setString(m, channelUserVO.getUserID());
                    // where
                    ++m;
                    handlerStmt.setString(m, p_focBatchMatserVO.getProductCode());
                    ++m;
                    handlerStmt.setString(m, p_focBatchMatserVO.getNetworkCode());
                    ++m;
                    handlerStmt.setString(m, p_focBatchMatserVO.getNetworkCodeFor());

                    /** START: Birendra: 30JAN2015 */
                    ++m;
                    handlerStmt.setString(m, focBatchItemVO.getWalletCode());
                    /** START: Birendra: 30JAN2015 */

                    updateCount = handlerStmt.executeUpdate();
                    handlerStmt.clearParameters();
                    if (updateCount <= 0) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB error while credit uer balance",
                            "Approval level = " + p_currentLevel);
                        continue;
                    }
                    transferProfileProductVO = TransferProfileProductCache
                        .getTransferProfileDetails(channelUserVO.getTransferProfileID(), p_focBatchMatserVO.getProductCode());
                    thresholdValue = transferProfileProductVO.getMinResidualBalanceAsLong();
                    String threshold_type = PretupsI.THRESHOLD_TYPE_MIN;
                    final String remark = null;
                    if (balance <= transferProfileProductVO.getAltBalanceLong() && balance >= transferProfileProductVO.getMinResidualBalanceAsLong()) {
                        thresholdValue = transferProfileProductVO.getAltBalanceLong();
                        threshold_type = PretupsI.THRESHOLD_TYPE_ALERT;
                    }
                    // end
                    // thresholdValue=(Long)PreferenceCache.getControlPreference(PreferenceI.ZERO_BAL_THRESHOLD_VALUE,p_focBatchMatserVO.getNetworkCode(),
                    // focBatchItemVO.getCategoryCode()); //threshold value

                    // for zero balance counter..
                    try {
                        m = 0;
                        final boolean isUserThresholdEntryReq = false;
                        final String thresholdType = null;
                        // 24dec addded by nilesh:if previous bal is below
                        // threshold and current bal is above threshold,
                        // then entry in user_threshold_counter.Also,if previous
                        // bal is already below threshold and current bal is
                        // also below threshold
                        // then also entry in user_threshold_counter
                        // table(Discussed with Ved Sir and Protim Sir)
                        // if(previousUserBalToBeSetChnlTrfItems<=thresholdValue
                        // && balance >=thresholdValue)
                        if ((previousUserBalToBeSetChnlTrfItems <= thresholdValue && balance >= thresholdValue) || (previousUserBalToBeSetChnlTrfItems <= thresholdValue && balance <= thresholdValue)) {
                            if (_log.isDebugEnabled()) {
                                _log.debug(methodName, "Entry in threshold counter" + thresholdValue + ", prvbal: " + previousUserBalToBeSetChnlTrfItems + "nbal" + balance);
                            }
                            psmtInsertUserThreshold.clearParameters();
                            m = 0;
                            ++m;
                            psmtInsertUserThreshold.setString(m, channelUserVO.getUserID());
                            ++m;
                            psmtInsertUserThreshold.setString(m, o2cTransferID);
                            ++m;
                            psmtInsertUserThreshold.setDate(m, BTSLUtil.getSQLDateFromUtilDate(date));
                            ++m;
                            psmtInsertUserThreshold.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                            ++m;
                            psmtInsertUserThreshold.setString(m, p_focBatchMatserVO.getNetworkCode());
                            ++m;
                            psmtInsertUserThreshold.setString(m, p_focBatchMatserVO.getProductCode());
                            // psmtInsertUserThreshold.setLong(++m,
                            // p_userBalancesVO.getUnitValue());
                            ++m;
                            psmtInsertUserThreshold.setString(m, PretupsI.CHANNEL_TYPE_O2C);
                            ++m;
                            psmtInsertUserThreshold.setString(m, PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                            if (balance >= thresholdValue) {
                                ++m;
                                psmtInsertUserThreshold.setString(m, PretupsI.ABOVE_THRESHOLD_TYPE);
                            } else {
                                ++m;
                                psmtInsertUserThreshold.setString(m, PretupsI.BELOW_THRESHOLD_TYPE);
                            }
                            ++m;
                            psmtInsertUserThreshold.setString(m, focBatchItemVO.getCategoryCode());
                            ++m;
                            psmtInsertUserThreshold.setLong(m, previousUserBalToBeSetChnlTrfItems);
                            ++m;
                            psmtInsertUserThreshold.setLong(m, balance);
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
                        _log.error(methodName, "SQLException " + sqle.getMessage());
                        _log.errorTrace(methodName, sqle);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "FOCBatchTransferWebDAO[closeOrderByBatch]", o2cTransferID, "", p_focBatchMatserVO.getNetworkCode(),
                            "Error while updating user_threshold_counter table SQL Exception:" + sqle.getMessage());
                    }// end of catch

                }
                pstmtSelectTransferCounts.clearParameters();
                m = 0;
                ++m;
                pstmtSelectTransferCounts.setString(m, channelUserVO.getUserID());
                
                rs = null;
                try {
                rs = pstmtSelectTransferCounts.executeQuery();
                // get the user transfer counts
                countsVO = null;
                if (rs.next()) {
                    countsVO = new UserTransferCountsVO();
                    countsVO.setUserID(focBatchItemVO.getUserId());

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

                    countsVO.setUnctrlDailyInCount(rs.getLong("outside_daily_in_count"));
                    countsVO.setUnctrlDailyInValue(rs.getLong("outside_daily_in_value"));
                    countsVO.setUnctrlWeeklyInCount(rs.getLong("outside_weekly_in_count"));
                    countsVO.setUnctrlWeeklyInValue(rs.getLong("outside_weekly_in_value"));
                    countsVO.setUnctrlMonthlyInCount(rs.getLong("outside_monthly_in_count"));
                    countsVO.setUnctrlMonthlyInValue(rs.getLong("outside_monthly_in_value"));

                    countsVO.setUnctrlDailyOutCount(rs.getLong("outside_daily_out_count"));
                    countsVO.setUnctrlDailyOutValue(rs.getLong("outside_daily_out_value"));
                    countsVO.setUnctrlWeeklyOutCount(rs.getLong("outside_weekly_out_count"));
                    countsVO.setUnctrlWeeklyOutValue(rs.getLong("outside_weekly_out_value"));
                    countsVO.setUnctrlMonthlyOutCount(rs.getLong("outside_monthly_out_count"));
                    countsVO.setUnctrlMonthlyOutValue(rs.getLong("outside_monthly_out_value"));

                    countsVO.setDailySubscriberOutCount(rs.getLong("daily_subscriber_out_count"));
                    countsVO.setDailySubscriberOutValue(rs.getLong("daily_subscriber_out_value"));
                    countsVO.setWeeklySubscriberOutCount(rs.getLong("weekly_subscriber_out_count"));
                    countsVO.setWeeklySubscriberOutValue(rs.getLong("weekly_subscriber_out_value"));
                    countsVO.setMonthlySubscriberOutCount(rs.getLong("monthly_subscriber_out_count"));
                    countsVO.setMonthlySubscriberOutValue(rs.getLong("monthly_subscriber_out_value"));

                    countsVO.setLastTransferDate(rs.getDate("last_transfer_date"));
                    countsVO.setLastSOSTxnStatus(rs.getString("last_sos_txn_status"));
                    countsVO.setLastLrStatus(rs.getString("last_lr_status"));
                    
                }
                }
                finally {
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
                    ChannelTransferBL.checkResetCountersAfterPeriodChange(countsVO, date);
                }
                pstmtSelectProfileCounts.clearParameters();
                m = 0;
                ++m;
                pstmtSelectProfileCounts.setString(m, focBatchItemVO.getTxnProfile());
                ++m;
                pstmtSelectProfileCounts.setString(m, PretupsI.YES);
                ++m;
                pstmtSelectProfileCounts.setString(m, p_focBatchMatserVO.getNetworkCode());
                ++m;
                pstmtSelectProfileCounts.setString(m, PretupsI.PARENT_PROFILE_ID_CATEGORY);
                ++m;
                pstmtSelectProfileCounts.setString(m, PretupsI.YES);
                rs = null;
                try {
                rs = pstmtSelectProfileCounts.executeQuery();
                // get the transfwer profile counts
                if (rs.next()) {
                    transferProfileVO = new TransferProfileVO();
                    transferProfileVO.setProfileId(rs.getString("profile_id"));
                    transferProfileVO.setDailyInCount(rs.getLong("daily_transfer_in_count"));
                    transferProfileVO.setDailyInValue(rs.getLong("daily_transfer_in_value"));
                    transferProfileVO.setWeeklyInCount(rs.getLong("weekly_transfer_in_count"));
                    transferProfileVO.setWeeklyInValue(rs.getLong("weekly_transfer_in_value"));
                    transferProfileVO.setMonthlyInCount(rs.getLong("monthly_transfer_in_count"));
                    transferProfileVO.setMonthlyInValue(rs.getLong("monthly_transfer_in_value"));
                    
                }
                // (profile counts not found) if this condition is true then
                // made entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.transferprofilenotfound"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog
                        .detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Transfer profile not found", "Approval level = " + p_currentLevel);
                    continue;
                }
                }
                finally {
                	if(rs!=null)
                		rs.close();
                }
                // (daily in count reach) if this condition is true then made
                // entry in logs and leave this data.
                 if (transferProfileVO.getDailyInCount() <= countsVO.getDailyInCount()) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.dailyincntreach"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Daily transfer in count reach",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                // (daily in value reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getDailyInValue() < (countsVO.getDailyInValue() + focBatchItemVO.getRequestedQuantity())) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.dailyinvaluereach"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Daily transfer in value reach",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                // (weekly in count reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getWeeklyInCount() <= countsVO.getWeeklyInCount()) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.weeklyincntreach"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Weekly transfer in count reach",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                // (weekly in value reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getWeeklyInValue() < (countsVO.getWeeklyInValue() + focBatchItemVO.getRequestedQuantity())) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.weeklyinvaluereach"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Weekly transfer in value reach",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                // (monthly in count reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getMonthlyInCount() <= countsVO.getMonthlyInCount()) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.monthlyincntreach"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Monthly transfer in count reach",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                // (mobthly in value reach) if this condition is true then made
                // entry in logs and leave this data.
                else if (transferProfileVO.getMonthlyInValue() < (countsVO.getMonthlyInValue() + focBatchItemVO.getRequestedQuantity())) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.monthlyinvaluereach"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Monthly transfer in value reach",
                        "Approval level = " + p_currentLevel);
                    continue;
                }
                
                
                countsVO.setUserID(channelUserVO.getUserID());
                countsVO.setDailyInCount(countsVO.getDailyInCount() + 1);
                countsVO.setWeeklyInCount(countsVO.getWeeklyInCount() + 1);
                countsVO.setMonthlyInCount(countsVO.getMonthlyInCount() + 1);
                countsVO.setDailyInValue(countsVO.getDailyInValue() + focBatchItemVO.getRequestedQuantity());
                countsVO.setWeeklyInValue(countsVO.getWeeklyInValue() + focBatchItemVO.getRequestedQuantity());
                countsVO.setMonthlyInValue(countsVO.getMonthlyInValue() + focBatchItemVO.getRequestedQuantity());
                countsVO.setLastInTime(date);
                countsVO.setLastTransferID(o2cTransferID);
                countsVO.setLastTransferDate(date);
                // Update counts if found in db
                if (flag) {
                    m = 0;
                    pstmtUpdateTransferCounts.clearParameters();
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getDailyInCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getDailyInValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getWeeklyInCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getWeeklyInValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getMonthlyInCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getMonthlyInValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getDailyOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getDailyOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getWeeklyOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getWeeklyOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getMonthlyOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getMonthlyOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlDailyInCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlDailyInValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlWeeklyInCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlWeeklyInValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlMonthlyInCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlMonthlyInValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlDailyOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlDailyOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlWeeklyOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlWeeklyOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlMonthlyOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getUnctrlMonthlyOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getDailySubscriberOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getDailySubscriberOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getWeeklySubscriberOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getWeeklySubscriberOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getMonthlySubscriberOutCount());
                    ++m;
                    pstmtUpdateTransferCounts.setLong(m, countsVO.getMonthlySubscriberOutValue());
                    ++m;
                    pstmtUpdateTransferCounts.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(countsVO.getLastInTime()));
                    ++m;
                    pstmtUpdateTransferCounts.setString(m, countsVO.getLastTransferID());
                    ++m;
                    pstmtUpdateTransferCounts.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(countsVO.getLastTransferDate()));
                    ++m;
                    pstmtUpdateTransferCounts.setString(m, countsVO.getUserID());
                    updateCount = pstmtUpdateTransferCounts.executeUpdate();
                }
                // Insert counts if not found in db
                else {
                    m = 0;
                    pstmtInsertTransferCounts.clearParameters();
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getDailyInCount());
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getDailyInValue());
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getWeeklyInCount());
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getWeeklyInValue());
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getMonthlyInCount());
                    ++m;
                    pstmtInsertTransferCounts.setLong(m, countsVO.getMonthlyInValue());
                    ++m;
                    pstmtInsertTransferCounts.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(countsVO.getLastInTime()));
                    ++m;
                    pstmtInsertTransferCounts.setString(m, countsVO.getLastTransferID());
                    ++m;
                    pstmtInsertTransferCounts.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(countsVO.getLastTransferDate()));
                    ++m;
                    pstmtInsertTransferCounts.setString(m, countsVO.getUserID());
                    updateCount = pstmtInsertTransferCounts.executeUpdate();
                }
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    if (flag) {
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB error while insert user trasnfer counts",
                            "Approval level = " + p_currentLevel);
                    } else {
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB error while uptdate user trasnfer counts",
                            "Approval level = " + p_currentLevel);
                    }
                    continue;
                }
                pstmtIsModified.clearParameters();
                m = 0;
                ++m;
                pstmtIsModified.setString(m, focBatchItemVO.getBatchDetailId());
                java.sql.Timestamp newlastModified = null;
                rs = null;
                try {
                rs = pstmtIsModified.executeQuery();
                // check record is modified or not
                if (rs.next()) {
                    newlastModified = rs.getTimestamp("modified_on");
                    
                }
                // (record not found means record modified) if this condition is
                // true then made entry in logs and leave this data.
                else {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog
                        .detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Record is already modified", "Approval level = " + p_currentLevel);
                    continue;
                }
                }
                finally {
                	if(rs!=null)
                		rs.close();
                }
                // if this condition is true then made entry in logs and leave
                // this data.
                if (newlastModified.getTime() != BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getModifiedOn()).getTime()) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.recordmodified"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog
                        .detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : Record is already modified", "Approval level = " + p_currentLevel);
                    continue;
                }
                // (external txn number is checked)
                if (isExternalTxnUnique && !BTSLUtil.isNullString(focBatchItemVO.getExtTxnNo())) {
                    // check in foc_batch_item table
                    pstmtIsTxnNumExists1.clearParameters();
                    m = 0;
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                    ++m;
                    pstmtIsTxnNumExists1.setString(m, focBatchItemVO.getBatchDetailId());
                    
                    rs = null;
                    try {
                    rs = pstmtIsTxnNumExists1.executeQuery();
                    // if this condition is true then made entry in logs and
                    // leave this data.
                    if (rs.next()) {
                        p_con.rollback();
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchfoc.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, focBatchItemVO,
                            "FAIL : External transaction number already exists in FOC Batch", "Approval level = " + p_currentLevel);
                        continue;
                    }
                    }
                    finally {
                    	if(rs!=null)
                    		rs.close();
                    }
                    // check in channel transfer table
                    pstmtIsTxnNumExists2.clearParameters();
                    m = 0;
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, PretupsI.CHANNEL_TYPE_O2C);
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    pstmtIsTxnNumExists2.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                   
                    rs = null;
                    try {
                    rs = pstmtIsTxnNumExists2.executeQuery();
                    // if this condition is true then made entry in logs and
                    // leave this data.
                    if (rs.next()) {
                        p_con.rollback();
                        
                        errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                            "batchfoc.batchapprovereject.msg.error.externaltxnnumberexists"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : External transaction number already exists in CHANNEL TRF",
                            "Approval level = " + p_currentLevel);
                        continue;
                    }
                    }
                    finally {
                    	if(rs!=null)
                    		rs.close();
                    }
                }
                // If level 1 apperoval then set parameters in
                // psmtAppr1FOCBatchItem
                if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(p_currentLevel)) {
                    psmtAppr1FOCBatchItem.clearParameters();
                    focBatchItemVO.setFirstApprovedBy(p_userID);
                    focBatchItemVO.setFirstApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
                    m = 0;
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, o2cTransferID);
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    // commented for DB2 psmtAppr1FOCBatchItem.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR);
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getFirstApproverRemarks());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getStatus());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr1FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, focBatchItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                    ++m;
                    psmtAppr1FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    updateCount = psmtAppr1FOCBatchItem.executeUpdate();
                }
                // If level 2 apperoval then set parameters in
                // psmtAppr2FOCBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(p_currentLevel)) {
                    psmtAppr2FOCBatchItem.clearParameters();
                    focBatchItemVO.setSecondApprovedBy(p_userID);
                    focBatchItemVO.setSecondApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
                    m = 0;
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, o2cTransferID);
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    // commented for DB2 psmtAppr2FOCBatchItem.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR);
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getSecondApproverRemarks());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getStatus());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr2FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, focBatchItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                    ++m;
                    psmtAppr2FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    updateCount = psmtAppr2FOCBatchItem.executeUpdate();
                }
                // If level 2 apperoval then set parameters in
                // psmtAppr1FOCBatchItem
                else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE3.equals(p_currentLevel)) {
                    psmtAppr3FOCBatchItem.clearParameters();
                    focBatchItemVO.setThirdApprovedBy(p_userID);
                    focBatchItemVO.setThirdApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
                    m = 0;
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, o2cTransferID);
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr3FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    // commented for DB2 psmtAppr3FOCBatchItem.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR);
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, focBatchItemVO.getThirdApproverRemarks());
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, p_userID);
                    ++m;
                    psmtAppr3FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, focBatchItemVO.getStatus());
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, focBatchItemVO.getExtTxnNo());
                    ++m;
                    psmtAppr3FOCBatchItem.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(focBatchItemVO.getExtTxnDate()));
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, focBatchItemVO.getBatchDetailId());
                    ++m;
                    psmtAppr3FOCBatchItem.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                    updateCount = psmtAppr3FOCBatchItem.executeUpdate();
                }
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while updating items table",
                        "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                    continue;
                }
                // channelTransferVO=new ChannelTransferVO();
                channelTransferVO.setCanceledOn(focBatchItemVO.getCancelledOn());
                channelTransferVO.setCanceledBy(focBatchItemVO.getCancelledBy());
                channelTransferVO.setChannelRemarks(focBatchItemVO.getInitiatorRemarks());
                channelTransferVO.setCommProfileSetId(focBatchItemVO.getCommissionProfileSetId());
                channelTransferVO.setCommProfileVersion(focBatchItemVO.getCommissionProfileVer());
                channelTransferVO.setDualCommissionType(focBatchItemVO.getDualCommissionType());
                channelTransferVO.setCreatedBy(focBatchItemVO.getInitiatedBy());
                // channelTransferVO.setCreatedOn(focBatchItemVO.getInitiatedOn());
                channelTransferVO.setDomainCode(p_focBatchMatserVO.getDomainCode());
                channelTransferVO.setExternalTxnDate(focBatchItemVO.getExtTxnDate());
                channelTransferVO.setExternalTxnNum(focBatchItemVO.getExtTxnNo());
                channelTransferVO.setFirstApprovedBy(focBatchItemVO.getFirstApprovedBy());
                channelTransferVO.setFirstApprovedOn(focBatchItemVO.getFirstApprovedOn());
                channelTransferVO.setFirstApproverLimit(0);
                channelTransferVO.setFirstApprovalRemark(focBatchItemVO.getFirstApproverRemarks());
                channelTransferVO.setSecondApprovedBy(focBatchItemVO.getSecondApprovedBy());
                channelTransferVO.setSecondApprovedOn(focBatchItemVO.getSecondApprovedOn());
                channelTransferVO.setSecondApprovalLimit(0);
                channelTransferVO.setSecondApprovalRemark(focBatchItemVO.getSecondApproverRemarks());
                channelTransferVO.setCategoryCode(PretupsI.OPERATOR_TYPE_OPT);
                channelTransferVO.setBatchNum(focBatchItemVO.getBatchId());
                channelTransferVO.setBatchDate(p_focBatchMatserVO.getBatchDate());
                channelTransferVO.setFromUserID(PretupsI.OPERATOR_TYPE_OPT);
                channelTransferVO.setTotalTax3(0);
                channelTransferVO.setPayableAmount(0);
                channelTransferVO.setNetPayableAmount(0);
                channelTransferVO.setPayInstrumentAmt(0);
                channelTransferVO.setGraphicalDomainCode(channelUserVO.getGeographicalCode());
                channelTransferVO.setModifiedBy(p_userID);
                channelTransferVO.setModifiedOn(date);
                channelTransferVO.setProductType(p_focBatchMatserVO.getProductType());
                channelTransferVO.setReceiverCategoryCode(focBatchItemVO.getCategoryCode());
                channelTransferVO.setReceiverGradeCode(focBatchItemVO.getGradeCode());
                channelTransferVO.setReceiverTxnProfile(focBatchItemVO.getTxnProfile());
                channelTransferVO.setReferenceNum(focBatchItemVO.getBatchDetailId());

                channelTransferVO.setDefaultLang(p_sms_default_lang);
                channelTransferVO.setSecondLang(p_sms_second_lang);
                // for balance logger
                channelTransferVO.setReferenceID(network_id);
                // ends here
                if (messageGatewayVO != null && messageGatewayVO.getRequestGatewayVO() != null) {
                    channelTransferVO.setRequestGatewayCode(messageGatewayVO.getRequestGatewayVO().getGatewayCode());
                    channelTransferVO.setRequestGatewayType(messageGatewayVO.getGatewayType());
                }
                channelTransferVO.setRequestedQuantity(focBatchItemVO.getRequestedQuantity());
                channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);
                channelTransferVO.setStatus(focBatchItemVO.getStatus());
                channelTransferVO.setThirdApprovedBy(focBatchItemVO.getThirdApprovedBy());
                channelTransferVO.setThirdApprovedOn(focBatchItemVO.getThirdApprovedOn());
                channelTransferVO.setThirdApprovalRemark(focBatchItemVO.getThirdApproverRemarks());
                channelTransferVO.setToUserID(channelUserVO.getUserID());
                channelTransferVO.setTotalTax1(focBatchItemVO.getTax1Value());
                channelTransferVO.setTotalTax2(focBatchItemVO.getTax2Value());
                channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_TRANSFER);
                channelTransferVO.setTransferDate(focBatchItemVO.getInitiatedOn());
                channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
                channelTransferVO.setTransferID(o2cTransferID);
                channelTransferVO.setTransferInitatedBy(focBatchItemVO.getInitiatedBy());
                channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
                channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
                channelTransferVO.setTransferMRP(focBatchItemVO.getTransferMrp());
                // added for logger
                channelTransferVO.setControlTransfer(PretupsI.YES);
                channelTransferVO.setToUserCode(focBatchItemVO.getMsisdn());
                channelTransferVO.setReceiverDomainCode(p_focBatchMatserVO.getDomainCode());
                channelTransferVO.setReceiverGgraphicalDomainCode(channelTransferVO.getGraphicalDomainCode());
                channelTransferVO.setWalletType(PretupsI.FOC_WALLET_TYPE);
                channelTransferVO.setActiveUserId(p_focBatchMatserVO.getCreatedBy());
                // end
                channelTransferItemVO = new ChannelTransferItemsVO();
                channelTransferItemVO.setApprovedQuantity(focBatchItemVO.getRequestedQuantity());
                channelTransferItemVO.setCommProfileDetailID(focBatchItemVO.getCommissionProfileDetailId());
                channelTransferItemVO.setCommRate(focBatchItemVO.getCommissionRate());
                channelTransferItemVO.setCommType(focBatchItemVO.getCommissionType());
                channelTransferItemVO.setCommValue(focBatchItemVO.getCommissionValue());
                channelTransferItemVO.setNetPayableAmount(0);
                channelTransferItemVO.setPayableAmount(0);
                channelTransferItemVO.setProductTotalMRP(focBatchItemVO.getTransferMrp());
                channelTransferItemVO.setProductCode(p_focBatchMatserVO.getProductCode());
                channelTransferItemVO.setReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
                channelTransferItemVO.setSenderPreviousStock(previousNwStockToBeSetChnlTrfItems);
                channelTransferItemVO.setRequiredQuantity(focBatchItemVO.getRequestedQuantity());
                channelTransferItemVO.setSerialNum(1);
                channelTransferItemVO.setTax1Rate(focBatchItemVO.getTax1Rate());
                channelTransferItemVO.setTax1Type(focBatchItemVO.getTax1Type());
                channelTransferItemVO.setTax1Value(focBatchItemVO.getTax1Value());
                channelTransferItemVO.setTax2Rate(focBatchItemVO.getTax2Rate());
                channelTransferItemVO.setTax2Type(focBatchItemVO.getTax2Type());
                channelTransferItemVO.setTax2Value(focBatchItemVO.getTax2Value());
                channelTransferItemVO.setTax3Rate(focBatchItemVO.getTax3Rate());
                channelTransferItemVO.setTax3Type(focBatchItemVO.getTax3Type());
                channelTransferItemVO.setTax3Value(focBatchItemVO.getTax3Value());
                channelTransferItemVO.setTransferID(o2cTransferID);
                channelTransferItemVO.setUnitValue(p_focBatchMatserVO.getProductMrp());
                channelTransferItemVO.setSenderDebitQty(focBatchItemVO.getRequestedQuantity());
                channelTransferItemVO.setReceiverCreditQty(focBatchItemVO.getRequestedQuantity());
                // for the balance logger
                channelTransferItemVO.setAfterTransReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
                channelTransferItemVO.setAfterTransSenderPreviousStock(previousNwStockToBeSetChnlTrfItems);
                // ends here
                channelTransferItemVOList = new ArrayList();
                channelTransferItemVOList.add(channelTransferItemVO);
                channelTransferItemVO.setShortName(p_focBatchMatserVO.getProductShortName());
                channelTransferVO.setChannelTransferitemsVOList(channelTransferItemVOList);
                channelTransferVO.setTransactionCode(PretupsI.TRANSFER_TYPE_FOC);
                Map<String, Object> hashmap = ChannelTransferBL.checkSOSstatusAndAmount(p_con, countsVO, channelTransferVO);
                if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(false) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(true))
                {
                	p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batcho2c.batchapprovereject.msg.error.sosPending"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : SOS Status PENDING FOR USER",
                        "Approval level = " + p_currentLevel);
                    continue;
                }else if (!hashmap.isEmpty() && hashmap.get(PretupsI.DO_WITHDRAW).equals(true) && hashmap.get(PretupsI.BLOCK_TRANSACTION).equals(false)) {
    				ChannelSoSWithdrawBL  channelSoSWithdrawBL = new ChannelSoSWithdrawBL();
    				channelSoSWithdrawBL.autoChannelSoSSettlement(channelTransferVO,PretupsI.SOS_REQUEST_TYPE);
    			}
                Map<String, Object> lrHashMap = ChannelTransferBL.checkLRstatusAndAmount(p_con, countsVO, channelTransferVO);
    			if(!lrHashMap.isEmpty()&& lrHashMap.get(PretupsI.DO_WITHDRAW).equals(true)){
    				ChannelSoSWithdrawBL  channelSoSWithdrawBL = new ChannelSoSWithdrawBL();
    				channelTransferVO.setLrWithdrawAmt((long)lrHashMap.get(PretupsI.WITHDRAW_AMOUNT));		
    				channelSoSWithdrawBL.autoChannelSoSSettlement(channelTransferVO,PretupsI.LR_REQUEST_TYPE);
    			}
                
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Exiting: channelTransferVO=" + channelTransferVO.toString());
                }
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "Exiting: channelTransferItemVO=" + channelTransferItemVO.toString());
                }
                m = 0;
                pstmtInsertIntoChannelTranfers.clearParameters();
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getCanceledBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getCanceledOn()));
                // commented for DB2
                // pstmtInsertIntoChannelTranfers.setFormOfUse(++m,
                // OraclePreparedStatement.FORM_NCHAR);
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getChannelRemarks());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getCommProfileSetId());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getCommProfileVersion());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getCreatedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getCreatedOn()));
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getDomainCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getExternalTxnDate()));
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getExternalTxnNum());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getFirstApprovedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getFirstApprovedOn()));
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getFirstApproverLimit());
                // commented for DB2
                // pstmtInsertIntoChannelTranfers.setFormOfUse(++m,
                // OraclePreparedStatement.FORM_NCHAR);
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getFirstApprovalRemark());
                ++m;
                pstmtInsertIntoChannelTranfers.setDate(m, BTSLUtil.getSQLDateFromUtilDate(channelTransferVO.getBatchDate()));
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getBatchNum());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getFromUserID());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getGraphicalDomainCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getModifiedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getModifiedOn()));
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getNetPayableAmount());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getNetworkCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getNetworkCodeFor());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getPayableAmount());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getPayInstrumentAmt());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getProductType());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getReceiverCategoryCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getReceiverGradeCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getReceiverTxnProfile());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getReferenceNum());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getRequestGatewayCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getRequestGatewayType());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getRequestedQuantity());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getSecondApprovedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getSecondApprovedOn()));
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getSecondApprovalLimit());
                // commented for DB2
                // pstmtInsertIntoChannelTranfers.setFormOfUse(++m,
                // OraclePreparedStatement.FORM_NCHAR);
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getSecondApprovalRemark());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getSource());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, focBatchItemVO.getStatus());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getThirdApprovedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getThirdApprovedOn()));
                // commented for DB2
                // pstmtInsertIntoChannelTranfers.setFormOfUse(++m,
                // OraclePreparedStatement.FORM_NCHAR);
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getThirdApprovalRemark());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getToUserID());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getTotalTax1());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getTotalTax2());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getTotalTax3());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getTransferCategory());
                ++m;
                pstmtInsertIntoChannelTranfers.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getTransferDate()));
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getTransferID());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getTransferInitatedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setLong(m, channelTransferVO.getTransferMRP());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getTransferSubType());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getTransferType());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getType());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getCategoryCode());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, PretupsI.YES);
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, focBatchItemVO.getMsisdn());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getDomainCode());

                // By sandeep ID TOG001
                // to geographical domain also inserted as the geogrpahical
                // domain that will help in reports
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getGraphicalDomainCode());

                // commented for DB2
                // pstmtInsertIntoChannelTranfers.setFormOfUse(++m,
                // OraclePreparedStatement.FORM_NCHAR);
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getDefaultLang());
                // commented for DB2
                // pstmtInsertIntoChannelTranfers.setFormOfUse(++m,
                // OraclePreparedStatement.FORM_NCHAR);
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, channelTransferVO.getSecondLang());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, p_focBatchMatserVO.getCreatedBy());
                ++m;
                pstmtInsertIntoChannelTranfers.setString(m, focBatchItemVO.getDualCommissionType());
                if (isMultipleWalletApply) {
                    ++m;
                    pstmtInsertIntoChannelTranfers.setString(m, PretupsI.FOC_WALLET_TYPE);
                }
                // ends here
                // insert into channel transfer table
                updateCount = pstmtInsertIntoChannelTranfers.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog(methodName, p_focBatchMatserVO, focBatchItemVO, "FAIL : DB Error while inserting in channel transfer table",
                        "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                    continue;
                }
                m = 0;
                pstmtInsertIntoChannelTransferItems.clearParameters();
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getApprovedQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, channelTransferItemVO.getCommProfileDetailID());
                ++m;
                pstmtInsertIntoChannelTransferItems.setDouble(m, channelTransferItemVO.getCommRate());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, channelTransferItemVO.getCommType());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getCommValue());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getProductTotalMRP());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getNetPayableAmount());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getPayableAmount());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, channelTransferItemVO.getProductCode());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getReceiverPreviousStock());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getRequiredQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setInt(m, channelTransferItemVO.getSerialNum());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getSenderPreviousStock());
                ++m;
                pstmtInsertIntoChannelTransferItems.setDouble(m, channelTransferItemVO.getTax1Rate());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, channelTransferItemVO.getTax1Type());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getTax1Value());
                ++m;
                pstmtInsertIntoChannelTransferItems.setDouble(m, channelTransferItemVO.getTax2Rate());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, channelTransferItemVO.getTax2Type());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getTax2Value());
                ++m;
                pstmtInsertIntoChannelTransferItems.setDouble(m, channelTransferItemVO.getTax3Rate());
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, channelTransferItemVO.getTax3Type());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getTax3Value());
                ++m;
                pstmtInsertIntoChannelTransferItems.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));
                ++m;
                pstmtInsertIntoChannelTransferItems.setString(m, o2cTransferID);
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getUnitValue());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getApprovedQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getApprovedQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getCommQuantity());
                ++m;
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getSenderPreviousStock() - channelTransferItemVO.getApprovedQuantity());
                
                ++m;
                
                pstmtInsertIntoChannelTransferItems.setLong(m, channelTransferItemVO.getReceiverPreviousStock() + channelTransferItemVO.getApprovedQuantity());
                
       
                // insert into channel transfer items table
                updateCount = pstmtInsertIntoChannelTransferItems.executeUpdate();
                updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
                // (record not updated properly) if this condition is true then
                // made entry in logs and leave this data.
                if (updateCount <= 0) {
                    p_con.rollback();
                    errorVO = new ListValueVO(focBatchItemVO.getMsisdn(), String.valueOf(focBatchItemVO.getRecordNumber()), PretupsRestUtil.getMessageString(
                        "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                    errorList.add(errorVO);
                    BatchFocFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, focBatchItemVO,
                        "FAIL : DB Error while inserting in channel transfer items table", "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                    continue;
                }
                // commit the transaction after processing each record
                // user life cycle
                if (focBatchItemVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_BATCH_O2C_STATUS_CLOSE)) {
                    if (!PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus())) {
                        // int
                        // updatecount=operatorUtili.changeUserStatusToActive(
                        // p_con,channelTransferVO.getToUserID(),channelUserVO.getStatus());
                        int updatecount = 0;
                        final String str[] = txnReceiverUserStatusChang.split(","); // "CH:Y,EX:Y".split(",");
                        String newStatus[] = null;
                        for (int i = 0; i < str.length; i++) {
                            newStatus = str[i].split(":");
                            if (newStatus[0].equals(channelUserVO.getStatus())) {
                                updatecount = operatorUtili.changeUserStatusToActive(p_con, channelTransferVO.getToUserID(), channelUserVO.getStatus(), newStatus[1]);
                                break;
                            }
                        }
                        if (updatecount > 0) {
                            p_con.commit();
                            BatchFocFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, focBatchItemVO, "PASS : Order is closed successfully",
                                "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                        } else {
                            p_con.rollback();
                            throw new BTSLBaseException(this, "closeOrderByBatch", "error.status.updating");
                        }
                    } else {
                        p_con.commit();
                        BatchFocFileProcessLog.detailLog("closeOrederByBatch", p_focBatchMatserVO, focBatchItemVO, "PASS : Order is closed successfully",
                            "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                    }

                } else {
                    p_con.commit();

                }
                // made entry in network stock and balance logger
                ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);
                pstmtSelectBalanceInfoForMessage.clearParameters();
                m = 0;
                ++m;
                pstmtSelectBalanceInfoForMessage.setString(m, channelUserVO.getUserID());
                ++m;
                pstmtSelectBalanceInfoForMessage.setString(m, p_focBatchMatserVO.getNetworkCode());
                ++m;
                pstmtSelectBalanceInfoForMessage.setString(m, p_focBatchMatserVO.getNetworkCodeFor());
                try {
                rs = null;
                rs = pstmtSelectBalanceInfoForMessage.executeQuery();
                userbalanceList = new ArrayList();
                while (rs.next()) {
                    balancesVO = new UserBalancesVO();
                    balancesVO.setProductCode(rs.getString("product_code"));
                    balancesVO.setBalance(rs.getLong("balance"));
                    balancesVO.setProductShortCode(rs.getString("product_short_code"));
                    balancesVO.setProductShortName(rs.getString("short_name"));
                    userbalanceList.add(balancesVO);
                }
                }
                finally {
                	if(rs!=null)
                		rs.close();
                }
                // generate the message arguments to be send in SMS
                keyArgumentVO = new KeyArgumentVO();
                argsArr = new String[2];
                argsArr[1] = PretupsBL.getDisplayAmount(channelTransferItemVO.getRequiredQuantity());
                argsArr[0] = String.valueOf(channelTransferItemVO.getShortName());
                keyArgumentVO.setKey(PretupsErrorCodesI.FOC_OPT_CHNL_TRANSFER_SMS2);
                keyArgumentVO.setArguments(argsArr);
                txnSmsMessageList = new ArrayList();
                balSmsMessageList = new ArrayList();
                txnSmsMessageList.add(keyArgumentVO);
                for (int index = 0, n = userbalanceList.size(); index < n; index++) {
                    balancesVO = (UserBalancesVO) userbalanceList.get(index);
                    if (balancesVO.getProductCode().equals(channelTransferItemVO.getProductCode())) {
                        argsArr = new String[2];
                        argsArr[1] = balancesVO.getBalanceAsString();
                        argsArr[0] = balancesVO.getProductShortName();
                        keyArgumentVO = new KeyArgumentVO();
                        keyArgumentVO.setKey(PretupsErrorCodesI.FOC_OPT_CHNL_TRANSFER_SMS_BALSUBKEY);
                        keyArgumentVO.setArguments(argsArr);
                        balSmsMessageList.add(keyArgumentVO);
                        break;
                    }
                }
                locale = new Locale(language, country);
                String focNotifyMsg = null;
                if (isFOCSmsNotify) {
                    final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
                    if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
                        focNotifyMsg = channelTransferVO.getDefaultLang();
                    } else {
                        focNotifyMsg = channelTransferVO.getSecondLang();
                    }
                    //array = new String[] { channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, txnSmsMessageList), BTSLUtil.getMessage(locale, balSmsMessageList), focNotifyMsg };
                      array = new String[] { channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale,txnSmsMessageList),  BTSLUtil.getMessage(locale,balSmsMessageList), BTSLUtil.NullToString(focBatchItemVO.getInitiatorRemarks()),focNotifyMsg};
                }

                if (focNotifyMsg == null) {
                    //array = new String[] { channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, txnSmsMessageList), BTSLUtil.getMessage(locale, balSmsMessageList) };
                	  array = new String[] { channelTransferVO.getTransferID(), BTSLUtil.getMessage(locale, txnSmsMessageList), BTSLUtil.getMessage(locale,balSmsMessageList),BTSLUtil.NullToString(focBatchItemVO.getInitiatorRemarks())};
                }

                if(BTSLUtil.isNullString(focBatchItemVO.getInitiatorRemarks()))
    				messages=new BTSLMessages(PretupsErrorCodesI.FOC_OPT_CHNL_TRANSFER_SMS1,array);
    			else
    				messages=new BTSLMessages(PretupsErrorCodesI.FOC_OPT_CHNL_TRANSFER_SMS4,array);
                
                //messages = new BTSLMessages(PretupsErrorCodesI.FOC_OPT_CHNL_TRANSFER_SMS1, array);
                pushMessage = new PushMessage(focBatchItemVO.getMsisdn(), messages, channelTransferVO.getTransferID(), null, locale, channelTransferVO.getNetworkCode());
                // push SMS
                pushMessage.push();
                OneLineTXNLog.log(channelTransferVO, focBatchItemVO);
            }// end of while
        }// end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqe) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[closeOrderByBatch]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            BatchFocFileProcessLog.focBatchMasterLog(methodName, p_focBatchMatserVO, "FAIL : SQL Exception:" + sqe.getMessage(), "Approval level = " + p_currentLevel);
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            try {
                if (p_con != null) {
                    p_con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[closeOrderByBatch]", "", "",
                "", "Exception:" + ex.getMessage());
            BatchFocFileProcessLog.focBatchMasterLog(methodName, p_focBatchMatserVO, "FAIL : Exception:" + ex.getMessage(), "Approval level = " + p_currentLevel);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtLoadUser != null) {
                    pstmtLoadUser.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtLoadNetworkStock != null) {
                    pstmtLoadNetworkStock.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateNetworkStock != null) {
                    pstmtUpdateNetworkStock.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertNetworkDailyStock != null) {
                    pstmtInsertNetworkDailyStock.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectNetworkStock != null) {
                    pstmtSelectNetworkStock.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtupdateSelectedNetworkStock != null) {
                    pstmtupdateSelectedNetworkStock.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertNetworkStockTransaction != null) {
                    pstmtInsertNetworkStockTransaction.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertNetworkStockTransactionItem != null) {
                    pstmtInsertNetworkStockTransactionItem.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectUserBalances != null) {
                    pstmtSelectUserBalances.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateUserBalances != null) {
                    pstmtUpdateUserBalances.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertUserDailyBalances != null) {
                    pstmtInsertUserDailyBalances.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectBalance != null) {
                    pstmtSelectBalance.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateBalance != null) {
                    pstmtUpdateBalance.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertBalance != null) {
                    pstmtInsertBalance.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectTransferCounts != null) {
                    pstmtSelectTransferCounts.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectProfileCounts != null) {
                    pstmtSelectProfileCounts.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateTransferCounts != null) {
                    pstmtUpdateTransferCounts.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertTransferCounts != null) {
                    pstmtInsertTransferCounts.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr1FOCBatchItem != null) {
                    psmtAppr1FOCBatchItem.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr2FOCBatchItem != null) {
                    psmtAppr2FOCBatchItem.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtAppr3FOCBatchItem != null) {
                    psmtAppr3FOCBatchItem.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsModified != null) {
                    pstmtIsModified.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtLoadTransferProfileProduct != null) {
                    pstmtLoadTransferProfileProduct.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (handlerStmt != null) {
                    handlerStmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsTxnNumExists1 != null) {
                    pstmtIsTxnNumExists1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsTxnNumExists2 != null) {
                    pstmtIsTxnNumExists2.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertIntoChannelTransferItems != null) {
                    pstmtInsertIntoChannelTransferItems.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtInsertIntoChannelTranfers != null) {
                    pstmtInsertIntoChannelTranfers.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectBalanceInfoForMessage != null) {
                    pstmtSelectBalanceInfoForMessage.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (psmtInsertUserThreshold != null) {
                    psmtInsertUserThreshold.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                int m = 0;
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2);
                ++m;
                pstmtSelectItemsDetails.setString(m, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
                ++m;
                pstmtSelectItemsDetails.setString(m, batch_ID);
                rs = null;
                rs = pstmtSelectItemsDetails.executeQuery();
                // Check the final status to be updated in master after
                // processing all records of batch
                if (rs.next()) {
                    final int totalCount = rs.getInt("batch_total_record");
                    final int closeCount = rs.getInt("closed");
                    final int cnclCount = rs.getInt("cncl");
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    } catch (Exception e) {
                        _log.errorTrace(methodName, e);
                    }
                    String statusOfMaster = null;
                    if (totalCount == cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_CANCEL;
                    } else if (totalCount == closeCount + cnclCount) {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_CLOSE;
                    } else {
                        statusOfMaster = PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_OPEN;
                    }
                    m = 0;
                    ++m;
                    pstmtUpdateMaster.setString(m, statusOfMaster);
                    ++m;
                    pstmtUpdateMaster.setString(m, p_userID);
                    ++m;
                    pstmtUpdateMaster.setTimestamp(m, BTSLUtil.getTimestampFromUtilDate(date));

                    // commented for DB2 pstmtUpdateMaster.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR);
                    ++m;
                    pstmtUpdateMaster.setString(m, p_sms_default_lang);
                    // commented for DB2 pstmtUpdateMaster.setFormOfUse(++m,
                    // OraclePreparedStatement.FORM_NCHAR);
                    ++m;
                    pstmtUpdateMaster.setString(m, p_sms_second_lang);
                    ++m;
                    pstmtUpdateMaster.setString(m, batch_ID);
                    ++m;
                    pstmtUpdateMaster.setString(m, PretupsI.CHANNEL_TRANSFER_BATCH_FOC_STATUS_UNDERPROCESS);

                    updateCount = pstmtUpdateMaster.executeUpdate();
                    // (record not updated properly) if this condition is true
                    // then made entry in logs and leave this data.
                    if (updateCount <= 0) {
                        p_con.rollback();
                        errorVO = new ListValueVO("", "", PretupsRestUtil.getMessageString( "batchfoc.batchapprovereject.msg.error.recordnotupdated"));
                        errorList.add(errorVO);
                        BatchFocFileProcessLog.focBatchMasterLog(methodName, p_focBatchMatserVO, "FAIL : DB Error while updating master table",
                            "Approval level = " + p_currentLevel + ", updateCount=" + updateCount);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "FOCBatchTransferWebDAO[closeOrederByBatch]", "", "", "", "Error while updating FOC_BATCHES table. Batch id=" + batch_ID);
                    }// end of if
                }// end of if
                p_con.commit();
            } catch (SQLException sqe) {
                try {
                    if (p_con != null) {
                        p_con.rollback();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                _log.error(methodName, "SQLException : " + sqe);
                _log.errorTrace(methodName, sqe);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[closeOrderByBatch]", "",
                    "", "", "SQL Exception:" + sqe.getMessage());
                BatchFocFileProcessLog.focBatchMasterLog("closeOrederByBatch", p_focBatchMatserVO, "FAIL : SQL Exception:" + sqe.getMessage(),
                    "Approval level = " + p_currentLevel);
                // throw new BTSLBaseException(this, methodName,
                // "error.general.sql.processing");
            } catch (Exception ex) {
                try {
                    if (p_con != null) {
                        p_con.rollback();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                _log.error(methodName, "Exception : " + ex);
                _log.errorTrace(methodName, ex);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FOCBatchTransferWebDAO[closeOrderByBatch]", "",
                    "", "", "Exception:" + ex.getMessage());
                BatchFocFileProcessLog
                    .focBatchMasterLog("closeOrederByBatch", p_focBatchMatserVO, "FAIL : Exception:" + ex.getMessage(), "Approval level = " + p_currentLevel);
                // throw new BTSLBaseException(this, methodName,
                // "error.general.processing");
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelectItemsDetails != null) {
                    pstmtSelectItemsDetails.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtUpdateMaster != null) {
                    pstmtUpdateMaster.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            // OneLineTXNLog.log(channelTransferVO);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: errorList size=" + errorList.size());
            }
        }
        return errorList;
    
    }
    
}

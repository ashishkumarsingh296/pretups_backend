/**
 * @(#)ScheduledBatchesDAO.java
 *                              Name Date History
 *                              ------------------------------------------------
 *                              ------------------------
 *                              Ashish Kumar 29/03/2006 Initial Creation
 *                              ------------------------------------------------
 *                              ------------------------
 *                              Copyright (c) 2006 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.scheduletopup.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

/**
 * ScheduledBatchesDAO is responsible to interact with the Data stores
 * 1.SCHEDULED_BATCH_MASTER
 * 2.SCHEDULED_BATCH_DETAIL
 * 3.RESTRICTED_MSISDNS
 * 4.C2S_TRANSFER
 * 5.REST_SUBS_SCHEDULER_STATUS
 * and provides the functionality to load the information from above data
 * source..
 */
public class ScheduledBatchesDAO {
    private Log _log = LogFactory.getLog(this.getClass().getName());

    public ScheduledBatchesDAO() {

    }

    /**
     * This method is used to load the all the batches whose status is 'S' or
     * 'U'
     * and scheduled date is in between current date(System date) and the days
     * which is configured in costants.prop
     * 
     * @param Connection
     *            p_con
     * @param Date
     *            p_fromDate
     * @param Date
     *            p_toDate
     * @param String
     *            p_status
     * @return ArrayList batchList
     * @throws BTSLBaseException
     *             modify method signature add batch type and modify query
     *             according to batch type
     */
    public ArrayList loadBatchList(Connection p_con, Date p_fromDate, Date p_toDate, String p_status, String p_batchType ,String p_batchID) throws BTSLBaseException {
        final String METHOD_NAME = "loadBatchList";
        if (_log.isDebugEnabled()) {
            _log.debug("loadBatchList Entered", "p_fromDate= " + p_fromDate + " p_currentDate= " + p_toDate + " p_status=" + p_status + " p_batchType=" + p_batchType+" , p_batchID="+p_batchID);
        }
        ArrayList batchList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ScheduleBatchMasterVO scheduledBatchesVO = null;
        StringBuffer strBuff = new StringBuffer(" SELECT batch_id,status,network_code,total_count,successful_count,upload_failed_count,");
        strBuff.append("process_failed_count,cancelled_count,scheduled_date,");
        strBuff.append("parent_id,owner_id,parent_category,");
        strBuff.append("parent_domain,service_type,created_on,created_by,modified_on,modified_by,initiated_by,batch_type,active_user_id, ");
        strBuff.append("frequency, iteration, processed_on, executed_iterations ");
        strBuff.append(" FROM scheduled_batch_master");
        strBuff.append(" WHERE status IN(" + p_status + ") AND scheduled_date>=? AND scheduled_date<=?  AND executed_iterations < iteration");
        if (!BTSLUtil.isNullString(p_batchType) && !(p_batchType.equalsIgnoreCase(PretupsI.ALL) || p_batchType.equalsIgnoreCase(PretupsI.BATCH_TYPE_BOTH))) {
            strBuff.append(" AND batch_type=? ");
            }
        
        if (!BTSLUtil.isNullString(p_batchID) && !(PretupsI.ALL.equalsIgnoreCase(p_batchID)))
        		strBuff.append(" AND batch_id=? ");	
        
        strBuff.append(" ORDER BY scheduled_date, service_type ASC");
        if (_log.isDebugEnabled()) {
            _log.debug("loadBatchList", "SelectQuery:strBuff.toString()" + strBuff.toString());
        }
        try {
            int i = 0;
            pstmtSelect = p_con.prepareStatement(strBuff.toString());
            pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
            pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
            if (!BTSLUtil.isNullString(p_batchType) && !(p_batchType.equalsIgnoreCase(PretupsI.ALL) || p_batchType.equalsIgnoreCase(PretupsI.BATCH_TYPE_BOTH))) {
                pstmtSelect.setString(++i, p_batchType);
            }
            if (!BTSLUtil.isNullString(p_batchID) && !(PretupsI.ALL.equalsIgnoreCase(p_batchID)))
            	pstmtSelect.setString(++i, p_batchID);
            
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                scheduledBatchesVO = new ScheduleBatchMasterVO();
                scheduledBatchesVO.setBatchID(rs.getString("batch_id"));
                scheduledBatchesVO.setStatus(rs.getString("status"));
                scheduledBatchesVO.setPrevStatus(rs.getString("status"));
                scheduledBatchesVO.setNetworkCode(rs.getString("network_code"));
                scheduledBatchesVO.setTotalCount(rs.getLong("total_count"));
                scheduledBatchesVO.setSuccessfulCount(rs.getLong("successful_count"));
                scheduledBatchesVO.setUploadFailedCount(rs.getLong("upload_failed_count"));
                scheduledBatchesVO.setProcessFailedCount(rs.getLong("process_failed_count"));
                scheduledBatchesVO.setCancelledCount(rs.getLong("cancelled_count"));
                scheduledBatchesVO.setScheduledDate(rs.getDate("scheduled_date"));
                scheduledBatchesVO.setParentID(rs.getString("parent_id"));
                scheduledBatchesVO.setOwnerID(rs.getString("owner_id"));
                scheduledBatchesVO.setParentCategory(rs.getString("parent_category"));
                scheduledBatchesVO.setParentDomain(rs.getString("parent_domain"));
                scheduledBatchesVO.setServiceType(rs.getString("service_type"));
                scheduledBatchesVO.setCreatedOn(rs.getTimestamp("created_on"));
                scheduledBatchesVO.setCreatedBy(rs.getString("created_by"));
                scheduledBatchesVO.setModifiedOn(rs.getTimestamp("modified_on"));
                scheduledBatchesVO.setModifiedBy(rs.getString("modified_by"));
                scheduledBatchesVO.setInitiatedBy(rs.getString("initiated_by"));
                scheduledBatchesVO.setBatchType(rs.getString("batch_type"));
                scheduledBatchesVO.setActiveUserId(rs.getString("active_user_id"));
                scheduledBatchesVO.setFrequency(rs.getString("frequency"));
                scheduledBatchesVO.setIterations(rs.getInt("iteration"));
                scheduledBatchesVO.setProcessedOn(rs.getDate("processed_on"));
                scheduledBatchesVO.setExecutedIterations(rs.getInt("executed_iterations"));
                batchList.add(scheduledBatchesVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadBatchList", "SQLException=" + sqe);
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchesDAO[loadBatchList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException("ScheduledBatchesDAO", "loadBatchList", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadBatchList", "Exception : " + e);
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchesDAO[loadBatchList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("ScheduledBatchesDAO", "loadBatchList", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadBatchList", "Exiting batchList.size()=" + batchList.size());
            }
        }
        return batchList;
    }

    /**
     * This method is used to load all the batch msisdn details with
     * corresponding batch_id and whose status is 'S'
     * and construct ScheduledBatchDetailVO
     * 
     * @param Connection
     *            p_con
     * @param String
     *            p_batchID
     * @return ArrayList batchDetailsList
     * @throws BTSLBaseException
     */
    public ArrayList loadBatchDetailsList(Connection p_con, String p_batchID) throws BTSLBaseException {
        final String METHOD_NAME = "loadBatchDetailsList";
        if (_log.isDebugEnabled()) {
            _log.debug("loadBatchDetailsList", "Entered p_batchID= " + p_batchID);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ScheduleBatchDetailVO scheduledBatchDetailVO = null;
        ArrayList batchDetailsList = new ArrayList();
        StringBuffer strBuff = new StringBuffer(" SELECT sbd.batch_id, sbd.subscriber_id sbdsubsid, sbd.msisdn sbdmsisdn, sbd.amount, ");
        strBuff.append("sbd.processed_on, sbd.status schstatus,sbd.transfer_id, sbd.transfer_status, sbd.created_on, sbd.created_by, sbd.modified_on, sbd.modified_by,sbd.sub_service,sbd.error_code, ");
        strBuff.append("rm.msisdn rmmsisdn, rm.subscriber_id rmsubsid, rm.channel_user_id, rm.channel_user_category, rm.owner_id, rm.employee_code, ");
        strBuff.append("rm.employee_name, rm.network_code, rm.monthly_limit, rm.min_txn_amount, rm.max_txn_amount, rm.total_txn_count, rm.total_txn_amount, ");
        strBuff.append("rm.black_list_status, rm.remark, rm.approved_by, rm.approved_on, rm.associated_by, rm.status rmstatus, rm.association_date, ");
        strBuff.append("rm.created_on,rm.created_by, rm.modified_on, rm.modified_by, rm.language, rm.country, rm.restricted_type, sbd.donor_msisdn, ");
        strBuff.append("sbd.donor_name, sbd.d_language, sbd.d_country, sbd.executed_iterations ");
        strBuff.append(" FROM scheduled_batch_detail sbd , restricted_msisdns rm WHERE sbd.subscriber_id=rm.subscriber_id AND sbd.batch_id =? AND sbd.status =? ");
        if (_log.isDebugEnabled()) {
            _log.debug("loadBatchDetailsList", "SelectQuery:strBuff.toString()=" + strBuff.toString());
        }
        try {
            int i = 0;
            pstmtSelect = p_con.prepareStatement(strBuff.toString());
            pstmtSelect.setString(++i, p_batchID);
            pstmtSelect.setString(++i, PretupsI.REST_SCH_BATCH_STATUS_SCHEDULED);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                scheduledBatchDetailVO = new ScheduleBatchDetailVO();
                scheduledBatchDetailVO.setBatchID(rs.getString("batch_id"));
                scheduledBatchDetailVO.setSubscriberID(rs.getString("sbdsubsid"));
                scheduledBatchDetailVO.setMsisdn(rs.getString("sbdmsisdn"));
                scheduledBatchDetailVO.setAmount(rs.getLong("amount"));
                scheduledBatchDetailVO.setAmountForDisp(PretupsBL.getDisplayAmount(rs.getLong("amount")));
                scheduledBatchDetailVO.setProcessedOn(rs.getTimestamp("processed_on"));
                scheduledBatchDetailVO.setScheduleStatus(rs.getString("schstatus"));
                scheduledBatchDetailVO.setPrevScheduleStatus(rs.getString("schstatus"));
                scheduledBatchDetailVO.setTransactionID(rs.getString("transfer_id"));
                scheduledBatchDetailVO.setTransactionStatus(rs.getString("transfer_status"));
                scheduledBatchDetailVO.setCreatedOn(rs.getTimestamp("created_on"));
                scheduledBatchDetailVO.setCreatedBy(rs.getString("created_by"));
                scheduledBatchDetailVO.setModifiedOn(rs.getTimestamp("modified_on"));
                scheduledBatchDetailVO.setModifiedBy(rs.getString("modified_by"));
                scheduledBatchDetailVO.setChannelUserCategory(rs.getString("channel_user_category"));
                scheduledBatchDetailVO.setOwnerID(rs.getString("owner_id"));
                scheduledBatchDetailVO.setMonthlyLimit(rs.getLong("monthly_limit"));
                scheduledBatchDetailVO.setEmployeeName(rs.getString("employee_name"));
                scheduledBatchDetailVO.setMonthlyLimitForDisp(PretupsBL.getDisplayAmount(rs.getLong("monthly_limit")));
                scheduledBatchDetailVO.setMinTxnAmount(rs.getLong("min_txn_amount"));
                scheduledBatchDetailVO.setMinTxnAmtForDisp(PretupsBL.getDisplayAmount(rs.getLong("min_txn_amount")));
                scheduledBatchDetailVO.setMaxTxnAmount(rs.getLong("max_txn_amount"));
                scheduledBatchDetailVO.setMaxTxnAmtForDisp(PretupsBL.getDisplayAmount(rs.getLong("max_txn_amount")));
                scheduledBatchDetailVO.setTotalTransferAmount(rs.getLong("total_txn_amount"));
                scheduledBatchDetailVO.setTotalTransferAmountForDisp(PretupsBL.getDisplayAmount(rs.getLong("total_txn_amount")));
                scheduledBatchDetailVO.setBlackListStatus(rs.getString("black_list_status"));
                scheduledBatchDetailVO.setRemarks(rs.getString("remark"));
                scheduledBatchDetailVO.setApprovedBy(rs.getString("approved_by"));
                scheduledBatchDetailVO.setApprovedOn(rs.getDate("approved_on"));
                scheduledBatchDetailVO.setAssociatedBy(rs.getString("associated_by"));
                scheduledBatchDetailVO.setStatus(rs.getString("rmstatus"));
                scheduledBatchDetailVO.setLanguage(rs.getString("language"));
                scheduledBatchDetailVO.setCountry(rs.getString("country"));
                scheduledBatchDetailVO.setSubService(rs.getString("sub_service"));
                scheduledBatchDetailVO.setTransferErrorCode(rs.getString("error_code"));
                scheduledBatchDetailVO.setRestrictedType(rs.getString("restricted_type"));
                scheduledBatchDetailVO.setDonorMsisdn(rs.getString("donor_msisdn"));
                scheduledBatchDetailVO.setDonorName(rs.getString("donor_name"));
                scheduledBatchDetailVO.setDonorLanguage(rs.getString("d_language"));
                scheduledBatchDetailVO.setDonorCountry(rs.getString("d_country"));
                scheduledBatchDetailVO.setExecutedIterations(rs.getInt("executed_iterations"));
                batchDetailsList.add(scheduledBatchDetailVO);
            }
        } catch (SQLException sqle) {
            _log.error("loadBatchDetailsList", "SQLException : " + sqle);
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchesDAO[loadBatchDetailsList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("ScheduledBatchesDAO", "loadBatchDetailsList", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadBatchDetailsList", "Exception : " + e);
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchesDAO[loadBatchDetailsList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("ScheduledBatchesDAO", "loadBatchDetailsList", e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadBatchDetailsList", "Exiting batchDetailsList.size()" + batchDetailsList.size());
            }
        }
        return batchDetailsList;
    }

    /**
     * This method is used to update the status of Batch in
     * SCHEDULED_BATCH_MASTER.
     * Update the following field
     * 1.status from 'S-Scheduled' to 'Under Process'
     * or
     * 2.status form 'U-Under Process' to 'E-Executed'
     * 
     * @param p_con
     * @param p_scheduleBatchMasterVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateBatchStatus(Connection p_con, ScheduleBatchMasterVO p_scheduleBatchMasterVO) throws BTSLBaseException {
        final String METHOD_NAME = "updateBatchStatus";
        if (_log.isDebugEnabled()) {
            _log.debug("updateBatchStatus", "Entered p_scheduleBatchMasterVO =" + p_scheduleBatchMasterVO);
        }
        PreparedStatement pstmtUpdate = null;
        ResultSet rs = null;
        int updateSuccess = 0;

        StringBuffer strBuff = new StringBuffer(" UPDATE scheduled_batch_master SET status=?,successful_count=?,process_failed_count=?,modified_on=?,modified_by=?,");
        strBuff.append(" processed_on=?,executed_iterations=?");
        strBuff.append(" WHERE batch_id = ? AND status=? ");
        if (_log.isDebugEnabled()) {
            _log.debug("updateBatchStatus", "UpdateQuery:strBuff.toString()=" + strBuff.toString());
        }
        try {
            int i = 0;
            pstmtUpdate = p_con.prepareStatement(strBuff.toString());
            pstmtUpdate.setString(++i, p_scheduleBatchMasterVO.getStatus());
            pstmtUpdate.setLong(++i, p_scheduleBatchMasterVO.getSuccessfulCount());
            pstmtUpdate.setLong(++i, p_scheduleBatchMasterVO.getProcessFailedCount());
            pstmtUpdate.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_scheduleBatchMasterVO.getModifiedOn()));
            pstmtUpdate.setString(++i, p_scheduleBatchMasterVO.getModifiedBy());
            pstmtUpdate.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_scheduleBatchMasterVO.getProcessedOn()));
            pstmtUpdate.setLong(++i, p_scheduleBatchMasterVO.getExecutedIterations());
            pstmtUpdate.setString(++i, p_scheduleBatchMasterVO.getBatchID());
            pstmtUpdate.setString(++i, p_scheduleBatchMasterVO.getPrevStatus());
            updateSuccess = pstmtUpdate.executeUpdate();
        } catch (SQLException sqle) {
            _log.error("updateBatchStatus", "SQLException : " + sqle);
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchesDAO[updateBatchStatus]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("ScheduledBatchesDAO", "updateBatchStatus", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("updateBatchStatus", "Exception : " + e);
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchesDAO[updateBatchStatus]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateBatchStatus", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("updateBatchStatus", "Exiting updateSuccess:" + updateSuccess);
            }
        }
        return updateSuccess;
    }

    /**
     * This method is used to update the status of msisdn in
     * SCHEDULED_BATCH_DETAILS
     * update the following
     * 1.status from 'S-Scheduled' to 'U-Under Process'
     * or
     * 2.status form 'U-Under Process' to 'E-Executed'
     * 
     * @param Connection
     *            p_con
     * @param ScheduleBatchDetailVO
     *            p_scheduledBatchDetailVO
     * @return int updateSuccess
     * @throws BTSLBaseException
     */
    public int updateBatchDetailStatus(Connection p_con, ScheduleBatchDetailVO p_scheduledBatchDetailVO) throws BTSLBaseException {
        final String METHOD_NAME = "updateBatchDetailStatus";
        if (_log.isDebugEnabled()) {
            _log.debug("updateBatchDetailStatus", "Entered p_scheduledBatchDetailVO=" + p_scheduledBatchDetailVO);
        }

        PreparedStatement pstmtUpdate = null;
        ResultSet rs = null;
        int updateSuccess = 0;

        StringBuffer strBuff = new StringBuffer(" UPDATE scheduled_batch_detail SET status=?,transfer_status=?,error_code=?,transfer_id=?,processed_on=?,modified_on=?,modified_by=?,executed_iterations=?");
        strBuff.append(" WHERE batch_id = ? AND msisdn = ? AND status=?");
        if (_log.isDebugEnabled()) {
            _log.debug("updateBatchDetailStatus", "UpdateQuery:strBuff.toString()=" + strBuff.toString());
        }
        try {
            int i = 0;
            pstmtUpdate = p_con.prepareStatement(strBuff.toString());
            pstmtUpdate.setString(++i, p_scheduledBatchDetailVO.getScheduleStatus());
            pstmtUpdate.setString(++i, p_scheduledBatchDetailVO.getTransactionStatus());
            pstmtUpdate.setString(++i, BTSLUtil.NullToString(p_scheduledBatchDetailVO.getTransferErrorCode()));
            pstmtUpdate.setString(++i, BTSLUtil.NullToString(p_scheduledBatchDetailVO.getTransactionID()));
            pstmtUpdate.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_scheduledBatchDetailVO.getProcessedOn()));
            pstmtUpdate.setTimestamp(++i, BTSLUtil.getTimestampFromUtilDate(p_scheduledBatchDetailVO.getModifiedOn()));
            pstmtUpdate.setString(++i, p_scheduledBatchDetailVO.getModifiedBy());
            pstmtUpdate.setInt(++i, p_scheduledBatchDetailVO.getExecutedIterations());
            pstmtUpdate.setString(++i, p_scheduledBatchDetailVO.getBatchID());
            pstmtUpdate.setString(++i, p_scheduledBatchDetailVO.getMsisdn());
            pstmtUpdate.setString(++i, p_scheduledBatchDetailVO.getPrevScheduleStatus());
            updateSuccess = pstmtUpdate.executeUpdate();
        } catch (SQLException sqle) {
            _log.error("updateBatchDetailStatus", "SQLException : " + sqle);
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchesDAO[updateBatchDetailStatus]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "updateBatchDetailStatus", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("updateBatchDetailStatus", "Exception : " + e);
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduledBatchesDAO[updateBatchDetailStatus]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "updateBatchDetailStatus", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("updateBatchDetailStatus", "Exiting updateSuccess:" + updateSuccess);
            }
        }
        return updateSuccess;
    }

}

/**
 * @(#)RestrictedSubscriberDAO.java
 *                                  Name Date History
 *                                  --------------------------------------------
 *                                  ----------------------------
 *                                  Abhijit Singh 03/07/2005 Initial Creation
 *                                  Change #1 for file TelesoftPreTUPsv5.0-test
 *                                  record sheet-Channel Admin General GUI
 *                                  Validations.xls, the bug no.-283 on 13/10/06
 *                                  by Amit Singh
 *                                  --------------------------------------------
 *                                  ----------------------------
 *                                  Copyright (c) 2006 Bharti Telesoft Ltd.
 */

package com.btsl.pretups.restrictedsubs.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchDetailVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * 
 */
public class RestrictedSubscriberDAO {
    /**
     * Field _log.
     */
    private final Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * This method is used to load the details of a restricted subscriber
     * Method :loadRestrictedSubscriberDetails
     * 
     * @param p_con
     *            java.sql.Connection
     * @param _c2sTransferVO
     *            C2STransferVO
     * @param p_channelUserId
     *            String
     * @param p_msisdn
     *            String
     * @param p_amount
     *            long
     * @param p_doLock
     *            boolean
     * @throws BTSLBaseException
     */
    public RestrictedSubscriberVO loadRestrictedSubscriberDetails(Connection p_con, String p_ownerId, String p_msisdn, boolean p_doLock) throws BTSLBaseException {
        final String methodName = "loadRestrictedSubscriberDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_ownerId=" + p_ownerId + ", p_msisdn=" + p_msisdn + ", p_doLock=" + p_doLock);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        RestrictedSubscriberVO restrictedSubscriberVO = null;
        StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT subscriber_id, channel_user_id, channel_user_category,  owner_id, ");
        strBuff.append(" employee_code, employee_name, monthly_limit,  min_txn_amount, max_txn_amount, ");
        strBuff.append(" total_txn_count, total_txn_amount,  black_list_status, remark, ");
        strBuff.append(" approved_by, approved_on, associated_by,  status, association_date, last_transaction_date, ");
        strBuff.append(" subscriber_type, language, country FROM restricted_msisdns WHERE msisdn = ? AND owner_id = ? AND restricted_type = ? ");
        strBuff.append(" AND status IN( ? , ? ) ");
        if (p_doLock) {
            // DB220120123for update WITH RS
            if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
                strBuff.append("FOR UPDATE WITH RS ");
            } else {
                strBuff.append("FOR UPDATE ");
            }
        }

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmtSelect.setString(i++, p_msisdn);
            pstmtSelect.setString(i++, p_ownerId);
            pstmtSelect.setString(i++, PretupsI.DEFAULT_RESTRICTED_TYPE);
            pstmtSelect.setString(i++, PretupsI.STATUS_ACTIVE);
            pstmtSelect.setString(i++, PretupsI.STATUS_SUSPEND);

            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                restrictedSubscriberVO = new RestrictedSubscriberVO();
                restrictedSubscriberVO.setMsisdn(p_msisdn);
                restrictedSubscriberVO.setSubscriberID(rs.getString("subscriber_id"));
                restrictedSubscriberVO.setChannelUserID(rs.getString("channel_user_id"));
                restrictedSubscriberVO.setChannelUserCategory(rs.getString("channel_user_category"));
                restrictedSubscriberVO.setOwnerID(rs.getString("owner_id"));
                restrictedSubscriberVO.setEmployeeCode(rs.getString("employee_code"));
                restrictedSubscriberVO.setEmployeeName(rs.getString("employee_name"));
                restrictedSubscriberVO.setMonthlyLimit(rs.getLong("monthly_limit"));
                restrictedSubscriberVO.setMinTxnAmount(rs.getLong("min_txn_amount"));
                restrictedSubscriberVO.setMaxTxnAmount(rs.getLong("max_txn_amount"));
                restrictedSubscriberVO.setBlackListStatus(rs.getString("black_list_status"));
                restrictedSubscriberVO.setRemarks(rs.getString("remark"));
                restrictedSubscriberVO.setApprovedBy(rs.getString("approved_by"));
                restrictedSubscriberVO.setApprovedOn(rs.getDate("approved_on"));
                restrictedSubscriberVO.setAssociatedBy(rs.getString("associated_by"));
                restrictedSubscriberVO.setAssociationDate(rs.getDate("association_date"));
                restrictedSubscriberVO.setMonthlyTransferAmount(rs.getLong("total_txn_amount"));
                restrictedSubscriberVO.setStatus(rs.getString("status"));
                restrictedSubscriberVO.setSubscriberType(rs.getString("subscriber_type"));
                restrictedSubscriberVO.setMonthlyTransferCount(rs.getLong("total_txn_count"));
                restrictedSubscriberVO.setLanguage(rs.getString("language"));
                restrictedSubscriberVO.setCountry("country");
                restrictedSubscriberVO.setLastTransferOn(rs.getDate("last_transaction_date"));
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);

            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberDAO[loadRestrictedSubscriberDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberDAO[loadRestrictedSubscriberDetails]", "", "", "", "Exception:" + ex.getMessage());
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
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting restrictedSubscriberVO:" + restrictedSubscriberVO);
            }
        }
        return restrictedSubscriberVO;
    }

    /**
     * This method is used to update the details of a restricted subscriber
     * Method :updateRestrictedSubscriberDetails
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_receiverVO
     *            ReceiverVO
     * @return int
     * @throws BTSLBaseException
     */
    public int updateRestrictedSubscriberDetails(Connection p_con, RestrictedSubscriberVO p_restrictedSubscriberVO) throws BTSLBaseException {
        final String methodName = "updateRestrictedSubscriberDetails";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_restrictedSubscriberVO" + p_restrictedSubscriberVO);
        }
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        RestrictedSubscriberVO restrictedSubscriberVO = null;
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" UPDATE restricted_msisdns SET total_txn_count=?, last_transaction_date=?, ");
        strBuff.append(" total_txn_amount= ? WHERE owner_id=? AND msisdn=? ");

        String sqlUpdate = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlUpdate=" + sqlUpdate);
        }

        try {
            restrictedSubscriberVO = loadRestrictedSubscriberDetails(p_con, p_restrictedSubscriberVO.getOwnerID(), p_restrictedSubscriberVO.getMsisdn(), true);
        } catch (Exception e) {
            // no exception is being thrown as the code need to be executed
            // further
            _log.error(methodName, "Exception : " + e);
            _log.errorTrace(methodName, e);
        }

        try {
            if (restrictedSubscriberVO != null) {
                // if the information is not found, then it means that it has
                // been deleted
                // so no update is required
                pstmtUpdate = p_con.prepareStatement(sqlUpdate);
                int i = 1;
                pstmtUpdate.setLong(i++, restrictedSubscriberVO.getMonthlyTransferCount() + 1);
                pstmtUpdate.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_restrictedSubscriberVO.getLastTransferOn()));
                pstmtUpdate.setLong(i++, p_restrictedSubscriberVO.getAmount() + restrictedSubscriberVO.getMonthlyTransferAmount());
                pstmtUpdate.setString(i++, restrictedSubscriberVO.getOwnerID());
                pstmtUpdate.setString(i++, restrictedSubscriberVO.getMsisdn());
                updateCount = pstmtUpdate.executeUpdate();
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberDAO[updateRestrictedSubscriberDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            // throw new BTSLBaseException(this,
            // "loadRestrictedSubscriberDetails",
            // "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberDAO[updateRestrictedSubscriberDetails]", "", "", "", "Exception:" + ex.getMessage());
            // throw new BTSLBaseException(this,
            // "updateRestrictedSubscriberDetails", "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * This method is used to decrease count and amount of the restricted
     * subscriber on reconciliation
     * Method :decreaseRestrictedSubscriberThresholds
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_c2sTransferVO
     *            C2STransferVO
     * @return int
     * @throws BTSLBaseException
     */
    public int decreaseRestrictedSubscriberThresholds(Connection p_con, C2STransferVO p_c2sTransferVO) {
        final String methodName = "decreaseRestrictedSubscriberThresholds";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_c2sTransferVO" + p_c2sTransferVO);
        }
        ChannelUserDAO channelUserDAO = null;
        ChannelUserVO channelUserVO = null;

        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;

        String ownerId = p_c2sTransferVO.getOwnerUserID();
        String senderMsisdn = p_c2sTransferVO.getSenderMsisdn();
        String receiverMsisdn = p_c2sTransferVO.getReceiverMsisdn();
        long amount = p_c2sTransferVO.getTransferValue();
        Date transferDate = p_c2sTransferVO.getTransferDate();

        RestrictedSubscriberVO restrictedSubscriberVO = null;

        String sqlUpdate = ("UPDATE restricted_msisdns SET total_txn_count=? , total_txn_amount=? WHERE owner_id=? AND msisdn=? ");
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlUpdate=" + sqlUpdate);
        }

        try {
            channelUserDAO = new ChannelUserDAO();
            channelUserVO = channelUserDAO.loadChannelUserDetails(p_con, senderMsisdn);
            if (channelUserVO != null && PretupsI.STATUS_ACTIVE.equalsIgnoreCase(channelUserVO.getCategoryVO().getRestrictedMsisdns()) && PretupsI.STATUS_ACTIVE.equalsIgnoreCase(channelUserVO.getCategoryVO().getTransferToListOnly()))
            // if(channelUserVO!=null &&
            // PretupsI.STATUS_ACTIVE.equalsIgnoreCase(((CategoryVO)channelUserVO.getCategoryVO()).getRestrictedMsisdns()))
            {
                Date currentDate = new Date();
                int transferYear = transferDate.getYear();
                int transferMonth = transferDate.getMonth();
                int currentYear = currentDate.getYear();
                int currentMonth = currentDate.getMonth();
                if ((transferYear == currentYear) && (currentMonth == transferMonth)) {
                    restrictedSubscriberVO = loadRestrictedSubscriberDetails(p_con, ownerId, receiverMsisdn, true);
                    if (restrictedSubscriberVO != null && restrictedSubscriberVO.getLastTransferOn() != null) {
                        pstmtUpdate = p_con.prepareStatement(sqlUpdate);
                        int i = 1;
                        pstmtUpdate.setLong(i++, restrictedSubscriberVO.getMonthlyTransferCount() - 1);
                        pstmtUpdate.setLong(i++, restrictedSubscriberVO.getMonthlyTransferAmount() - amount);
                        pstmtUpdate.setString(i++, restrictedSubscriberVO.getOwnerID());
                        pstmtUpdate.setString(i++, restrictedSubscriberVO.getMsisdn());
                        updateCount = pstmtUpdate.executeUpdate();
                    }
                }
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberDAO[decreaseRestrictedSubscriberThresholds]", "", "", "", "SQL Exception:" + sqe.getMessage());
            // commented intentionaly
            // throw new BTSLBaseException(this,
            // "loadRestrictedSubscriberDetails",
            // "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberDAO[decreaseRestrictedSubscriberThresholds]", "", "", "", "Exception:" + ex.getMessage());
            // commented intentionaly
            // throw new BTSLBaseException(this,
            // "updateRestrictedSubscriberDetails", "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * Method isSubscriberExistByStatus.
     * This method is to check that the subscriber is exist of the passed status
     * under the passed owner and msisdn
     * 
     * @param p_con
     *            Connection
     * @param p_userID
     *            String
     * @param p_subscriberList
     *            ArrayList
     * @param p_statusUsed
     *            String
     * @param p_status
     *            String
     * @param p_subscriberType
     *            String
     * @return String
     * @throws BTSLBaseException
     * @author sandeep.goel
     */
    public String isSubscriberExistByStatus(Connection p_con, String p_ownerID, List p_subscriberList, String p_statusUsed, String p_status, String p_subscriberType) throws BTSLBaseException {
        final String methodName = "isSubscriberExistByStatus";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_ownerID = " + p_ownerID + ",p_subscriberList size = " + p_subscriberList.size() + ",p_status=" + p_status + ",p_statusUsed=" + p_statusUsed + "p_subscriberType=" + p_subscriberType);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        StringBuffer returnDataStrBuff = new StringBuffer();
        String returnStr = null;
        StringBuffer strBuff = new StringBuffer("SELECT 1 FROM restricted_msisdns WHERE owner_id =? AND msisdn = ? AND restricted_type= ?");

        if (p_subscriberType != null) {
            strBuff.append("AND subscriber_type=? ");
        }
        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
            strBuff.append("AND status IN (" + p_status + ")");
        } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
            strBuff.append("AND status =? ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
            strBuff.append("AND status <> ? ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
            strBuff.append("AND status NOT IN (" + p_status + ")");
        }

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + strBuff);
        }
        try {
            pstmtSelect = p_con.prepareStatement(strBuff.toString());
            RestrictedSubscriberVO restrictedSubscriberVO = null;
            for (int index = 0, j = p_subscriberList.size(); index < j; index++) {
                restrictedSubscriberVO = (RestrictedSubscriberVO) p_subscriberList.get(index);
                int i = 1;
                pstmtSelect.setString(i++, p_ownerID);
                pstmtSelect.setString(i++, restrictedSubscriberVO.getMsisdn());
                pstmtSelect.setString(i++, PretupsI.DEFAULT_RESTRICTED_TYPE);
                if (p_subscriberType != null) {
                    pstmtSelect.setString(i++, p_subscriberType);
                }
                if (p_statusUsed.equals(PretupsI.STATUS_EQUAL) || p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
                    pstmtSelect.setString(i++, p_status);
                }
                rs = pstmtSelect.executeQuery();
                if (rs.next()) {
                    returnDataStrBuff.append(restrictedSubscriberVO.getMsisdn() + ",");
                }
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (Exception e) {
                    _log.errorTrace(methodName, e);
                }
                pstmtSelect.clearParameters();
            }
            if (returnDataStrBuff.length() > 0) {
                returnStr = returnDataStrBuff.substring(0, returnDataStrBuff.length() - 1);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberDAO[isSubscriberExistByStatus]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberDAO[isSubscriberExistByStatus]", "", "", "", "Exception:" + ex.getMessage());
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
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting returnStr=" + returnStr);
            }
        }
        return returnStr;
    }

    /**
     * This method load schedule batch details on the basis of batch_id and
     * status.
     * This method return HashMap, In HashMap key is msisdn and value is
     * ScheduleBatchDetailVO.
     * Method loadScheduleBatchDetailsList
     * 
     * @param p_con
     * @param p_batch_id
     *            String
     * @param p_statusUsed
     *            String
     * @param p_status
     *            String
     * @return LinkedHashMap
     * @throws BTSLBaseException
     *             int
     * @author ved.sharma
     *         modify query select rm.language,rm.country
     */
    public LinkedHashMap loadScheduleBatchDetailsList(Connection p_con, String p_batch_id, String p_statusUsed, String p_status) throws BTSLBaseException {
        final String methodName = "loadScheduleBatchDetailsList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_batch_id = " + p_batch_id + ",p_status=" + p_status + ",p_statusUsed=" + p_statusUsed);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ScheduleBatchDetailVO scheduleDetailVO = null;
        LinkedHashMap detailHashMap = new LinkedHashMap();
        try {
            /*StringBuffer selectSQL = new StringBuffer(" SELECT SBD.msisdn,SBD.amount,SBD.subscriber_id,SBD.status, L.lookup_name status_desc ,SBD.transfer_status,");
            selectSQL.append(" KV.value  transfer_status_desc, SBD.created_on, SBD.processed_on, SBM.service_type stype,");
            selectSQL.append(" RM.employee_name, RM.employee_code,  RM.max_txn_amount, RM.min_txn_amount, RM.monthly_limit,");
            selectSQL.append(" RM.total_txn_amount, RM.total_txn_count,SBD.modified_on,SBD.sub_service,rm.language,rm.country,SBD.donor_msisdn,SBD.executed_iterations ");
            selectSQL.append(" FROM scheduled_batch_detail SBD, scheduled_batch_master SBM, restricted_msisdns RM, lookups L, key_values KV ");
            selectSQL.append(" WHERE SBM.batch_id = ? AND SBM.batch_id=SBD.batch_id ");
            selectSQL.append(" AND SBD.subscriber_id = RM.subscriber_id");
            selectSQL.append(" AND SBD.status = L.lookup_code");
            selectSQL.append(" AND L.lookup_type = ?");
            selectSQL.append(" AND SBD.transfer_status = KV.key(+)");
            selectSQL.append(" AND KV.type (+) = ? ");

            if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
                selectSQL.append("AND SBD.status IN (" + p_status + ")");
            } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
                selectSQL.append("AND SBD.status =? ");
            } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
                selectSQL.append("AND SBD.status <> ? ");
            } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
                selectSQL.append("AND SBD.status NOT IN (" + p_status + ")");
            }
            selectSQL.append("ORDER BY RM.employee_name ");
            
            String selectSQL = restrictedSubscriberQry.loadScheduleBatchDetailsList(p_statusUsed, p_status);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY SelectQuery:" + selectSQL);
            }

            pstmtSelect = p_con.prepareStatement(selectSQL.toString());
            int i = 0;
            pstmtSelect.setString(++i, p_batch_id);
            pstmtSelect.setString(++i, PretupsI.SCHEDULE_BATCH_STATUS_LOOKUP_TYPE);
            pstmtSelect.setString(++i, PretupsI.KEY_VALUE_C2C_STATUS);
            if (p_statusUsed.equals(PretupsI.STATUS_EQUAL) || p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
                pstmtSelect.setString(++i, p_status);
            }
            *
            */
        	
        	RestrictedSubscriberQry restrictedSubscriberQry = (RestrictedSubscriberQry)ObjectProducer.getObject(QueryConstants.REST_SUB_QRY, QueryConstants.QUERY_PRODUCER);
        	pstmtSelect = restrictedSubscriberQry.loadScheduleBatchDetailsListQry(p_con, p_batch_id, p_statusUsed, p_status);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                scheduleDetailVO = new ScheduleBatchDetailVO();
                scheduleDetailVO.setMsisdn(rs.getString("msisdn"));
                scheduleDetailVO.setSubscriberID(rs.getString("subscriber_id"));
                scheduleDetailVO.setAmount(rs.getLong("amount"));
                scheduleDetailVO.setAmountForDisp(PretupsBL.getDisplayAmount(scheduleDetailVO.getAmount()));
                scheduleDetailVO.setStatusDes(rs.getString("status_desc"));
                scheduleDetailVO.setTransactionStatus(rs.getString("transfer_status_desc"));
                scheduleDetailVO.setCreatedOn(rs.getTimestamp("created_on"));
                scheduleDetailVO.setCreatedOnAsString(BTSLUtil.getDateTimeStringFromDate(scheduleDetailVO.getCreatedOn()));
                scheduleDetailVO.setProcessedOn(rs.getTimestamp("processed_on"));
                if (scheduleDetailVO.getProcessedOn() != null) {
                    scheduleDetailVO.setProcessedOnAsString(BTSLUtil.getDateTimeStringFromDate(scheduleDetailVO.getProcessedOn()));
                }
                scheduleDetailVO.setEmployeeName(rs.getString("employee_name"));
                scheduleDetailVO.setEmployeeCode(rs.getString("employee_code"));
                scheduleDetailVO.setMaxTxnAmount(rs.getLong("max_txn_amount"));
                scheduleDetailVO.setMaxTxnAmtForDisp(PretupsBL.getDisplayAmount(scheduleDetailVO.getMaxTxnAmount()));
                scheduleDetailVO.setMinTxnAmount(rs.getLong("min_txn_amount"));
                scheduleDetailVO.setMinTxnAmtForDisp(PretupsBL.getDisplayAmount(scheduleDetailVO.getMinTxnAmount()));
                scheduleDetailVO.setMonthlyLimit(rs.getLong("monthly_limit"));
                scheduleDetailVO.setMonthlyLimitForDisp(PretupsBL.getDisplayAmount(scheduleDetailVO.getMonthlyLimit()));
                scheduleDetailVO.setTotalTransferAmount(rs.getLong("total_txn_amount"));
                scheduleDetailVO.setTotalTransferAmountForDisp(PretupsBL.getDisplayAmount(scheduleDetailVO.getTotalTransferAmount()));
                scheduleDetailVO.setTotalTxnCount(rs.getLong("total_txn_count"));
                scheduleDetailVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                scheduleDetailVO.setSubService(rs.getString("sub_service"));
                scheduleDetailVO.setSubServiceDesc(PretupsBL.getSelectorDescriptionFromCode(rs.getString("stype") + "_" + scheduleDetailVO.getSubService()));
                ;
                scheduleDetailVO.setLanguage(rs.getString("language"));
                scheduleDetailVO.setCountry(rs.getString("country"));
                scheduleDetailVO.setDonorMsisdn(rs.getString("donor_msisdn"));
                scheduleDetailVO.setExecutedIterations(rs.getInt("executed_iterations"));
                detailHashMap.put(scheduleDetailVO.getMsisdn(), scheduleDetailVO);
            }

        } // end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberDAO[loadScheduleBatchDetailsList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberDAO[loadScheduleBatchDetailsList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: detailHashMap Size=" + detailHashMap.size());
            }
        } // end of finally
        return detailHashMap;
    }

    /**
     * Method for checking subscriber's existence under the channel user
     * Used in deletion of channel user
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_channelUserID
     *            String
     * @return isSubscriberExist boolean
     * @throws BTSLBaseException
     */
    public boolean isSubscriberExistByChannelUser(Connection p_con, String p_channelUserID) throws BTSLBaseException {
        final String methodName = "isSubscriberExistByChannelUser";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered with parameters :: p_channelUserID : " + p_channelUserID);
        }

        PreparedStatement pstmtIsExist = null;
        ResultSet rs = null;
        boolean existFlag = false;
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("SELECT 1 FROM restricted_msisdns WHERE channel_user_id=? OR owner_id= ? ");
            String sqlSelect = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }
            pstmtIsExist = p_con.prepareStatement(sqlSelect);

            pstmtIsExist.setString(1, p_channelUserID);
            pstmtIsExist.setString(2, p_channelUserID);
            rs = pstmtIsExist.executeQuery();
            if (rs.next()) {
                existFlag = true;
            }
            return existFlag;
        } // end of try
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberDAO[isSubscriberExistByChannelUser]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberDAO[isSubscriberExistByChannelUser]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtIsExist != null) {
                    pstmtIsExist.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: existFlag=" + existFlag);
            }
        }
    }

    /**
     * This method is used to check whether the subscriber is black-listed for a
     * p2p transaction.
     * Method :isSubscriberBlacklisted
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_msisdn
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isSubscriberBlacklisted(Connection p_con, String p_msisdn) throws BTSLBaseException {
        final String methodName = "isSubscriberBlacklisted";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_msisdn=" + p_msisdn);
        }
        PreparedStatement pstmtSelect = null;
        boolean subscriberBlacklisted = false;
        ResultSet rs = null;
        String sqlSelect = new String(" SELECT 1 FROM restricted_msisdns WHERE msisdn =? AND black_list_status=? ");

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmtSelect.setString(i++, p_msisdn);
            pstmtSelect.setString(i++, PretupsI.RES_MSISDN_BLACKLIST_STATUS);

            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                subscriberBlacklisted = true;
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberDAO[isSubscriberBlacklisted]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberDAO[isSubscriberBlacklisted]", "", "", "", "Exception:" + ex.getMessage());
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
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting subscriberBlacklisted:" + subscriberBlacklisted);
            }
        }
        return subscriberBlacklisted;
    }

    private ArrayList loadBatchDetailVOList(Connection p_con, String p_batchID, String p_statusUsed, String p_status, String batchType) throws BTSLBaseException {

        final String methodName = "loadBatchDetailVOList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_status=" + p_status + ",p_statusUsed=" + p_statusUsed + ", p_batchID=" + p_batchID + ", batchType=" + batchType);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList scheduleDetailsVOList = new ArrayList();
        try {
        	RestrictedSubscriberQry restrictedSubscriberQry = (RestrictedSubscriberQry)ObjectProducer.getObject(QueryConstants.REST_SUB_QRY, QueryConstants.QUERY_PRODUCER);
        	pstmtSelect = restrictedSubscriberQry.loadBatchDetailVOListQry(p_con, p_batchID, p_statusUsed, p_status, batchType);
            rs = pstmtSelect.executeQuery();
            ScheduleBatchDetailVO scheduleDetailVO = null;

            while (rs.next()) {
                scheduleDetailVO = new ScheduleBatchDetailVO();
                scheduleDetailVO.setMsisdn(rs.getString("msisdn"));
                scheduleDetailVO.setStatus(rs.getString("status"));
                scheduleDetailVO.setScheduleStatus(scheduleDetailVO.getStatus());
                scheduleDetailVO.setPrevScheduleStatus(scheduleDetailVO.getStatus());
                scheduleDetailVO.setStatusDes(rs.getString("status_desc"));
                scheduleDetailVO.setTransactionStatus(rs.getString("transfer_status"));
                scheduleDetailVO.setTransactionID(rs.getString("transfer_id"));
                scheduleDetailVO.setAmount(rs.getLong("amount"));
                scheduleDetailVO.setAmountForDisp(PretupsBL.getDisplayAmount(scheduleDetailVO.getAmount()));
                scheduleDetailVO.setBatchID(rs.getString("batch_id"));
                scheduleDetailVO.setSubscriberID(rs.getString("subscriber_id"));
                scheduleDetailVO.setNetworkCode(rs.getString("network_code"));
                scheduleDetailVO.setSubscriberType(rs.getString("service_type"));
                scheduleDetailVO.setSubscriberTypeDescription(rs.getString("description"));
                scheduleDetailVO.setScheduleDateStr(BTSLUtil.getDateStringFromDate(rs.getDate("scheduled_date")));
                scheduleDetailVO.setCreatedBy(rs.getString("created_by"));
                scheduleDetailVO.setSubService(rs.getString("sub_service"));
                scheduleDetailVO.setSubServiceDesc(PretupsBL.getSelectorDescriptionFromCode(rs.getString("stype") + "_" + scheduleDetailVO.getSubService()));
                scheduleDetailVO.setProcessedOn(rs.getTimestamp("processed_on"));
                if (scheduleDetailVO.getProcessedOn() != null) {
                    scheduleDetailVO.setProcessedOnAsString(BTSLUtil.getDateTimeStringFromDate(scheduleDetailVO.getProcessedOn()));
                }
                if(PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(batchType)) {
                	scheduleDetailVO.setEmployeeName(rs.getString("employee_name"));
                	scheduleDetailVO.setEmployeeCode(rs.getString("employee_code"));
                	scheduleDetailVO.setMaxTxnAmount(rs.getLong("max_txn_amount"));
                    scheduleDetailVO.setMaxTxnAmtForDisp(PretupsBL.getDisplayAmount(scheduleDetailVO.getMaxTxnAmount()));
                    scheduleDetailVO.setMinTxnAmount(rs.getLong("min_txn_amount"));
                    scheduleDetailVO.setMinTxnAmtForDisp(PretupsBL.getDisplayAmount(scheduleDetailVO.getMinTxnAmount()));
                    scheduleDetailVO.setMonthlyLimit(rs.getLong("monthly_limit"));
                    scheduleDetailVO.setMonthlyLimitForDisp(PretupsBL.getDisplayAmount(scheduleDetailVO.getMonthlyLimit()));
                    scheduleDetailVO.setTotalTransferAmount(rs.getLong("total_txn_amount"));
                    scheduleDetailVO.setTotalTransferAmountForDisp(PretupsBL.getDisplayAmount(scheduleDetailVO.getTotalTransferAmount()));
                    scheduleDetailVO.setTotalTxnCount(rs.getLong("total_txn_count"));
                }
                scheduleDetailVO.setCreatedOn(rs.getTimestamp("created_on"));
                scheduleDetailVO.setCreatedOnAsString(BTSLUtil.getDateTimeStringFromDate(scheduleDetailVO.getCreatedOn()));
                scheduleDetailVO.setModifiedOn(rs.getTimestamp("modified_on"));
                scheduleDetailVO.setModifiedBy(rs.getString("modified_by"));
                scheduleDetailVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                scheduleDetailVO.setTransferErrorCode(rs.getString("error_code"));
                scheduleDetailVO.setLanguage(rs.getString("r_language"));
                scheduleDetailVO.setCountry(rs.getString("r_country"));
                scheduleDetailVO.setDonorLanguage(rs.getString("d_language"));
                scheduleDetailVO.setDonorCountry(rs.getString("d_country"));
                scheduleDetailVO.setDonorMsisdn(rs.getString("donor_msisdn"));
                scheduleDetailVO.setDonorName(rs.getString("donor_name"));
                scheduleDetailsVOList.add(scheduleDetailVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberDAO[loadBatchDetailVOList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberDAO[loadBatchDetailVOList]", "", "", "", "Exception:" + ex.getMessage());
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
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting scheduleDetailsVOList.size=" + scheduleDetailsVOList.size());
            }
        }
        return scheduleDetailsVOList;
    
    }
    /**
     * @param p_con
     * @param p_batchID
     * @param p_statusUsed
     * @param p_status
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadBatchDetailVOList(Connection p_con, String p_batchID, String p_statusUsed, String p_status) throws BTSLBaseException {
        return loadBatchDetailVOList(p_con, p_batchID, p_statusUsed, p_status, null);
    }

    /**
     * @param p_con
     * @param p_batchID
     * @param p_statusUsed
     * @param p_status
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadBatchDetailVOListNormal(Connection p_con, String p_batchID, String p_statusUsed, String p_status, String batchType) throws BTSLBaseException {
        return loadBatchDetailVOList(p_con, p_batchID, p_statusUsed, p_status, batchType);
    }
    
    /**
     * @param p_con
     * @param p_batch_id
     * @param p_statusUsed
     * @param p_status
     * @return
     * @throws BTSLBaseException
     */
    public LinkedHashMap loadScheduleBatchDetailsMap(Connection p_con, String p_batch_id, String p_statusUsed, String p_status) throws BTSLBaseException {
        LinkedHashMap detailHashMap = new LinkedHashMap();
        try {
            ArrayList list = loadBatchDetailVOList(p_con, p_batch_id, p_statusUsed, p_status);
            ScheduleBatchDetailVO scheduleDetailVO = null;
            for (int i = 0, j = list.size(); i < j; i++) {
                scheduleDetailVO = (ScheduleBatchDetailVO) list.get(i);
                detailHashMap.put(scheduleDetailVO.getMsisdn(), scheduleDetailVO);
            }
        } catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        }
        return detailHashMap;
    }

    // method for loading sender and receiver info from restricted msisdn
    public HashMap loadRestrictedMsisdnDetail(Connection p_con, String p_msisdn, String p_senderMsisdn) throws BTSLBaseException {
        final String methodName = "loadRestrictedMsisdnDetail";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_con=" + p_con + ",p_msisdn=" + p_msisdn + ", p_senderMsisdn=" + p_senderMsisdn);
        }
        PreparedStatement pstmtSelect = null;
        RestrictedSubscriberVO restrictedSubscriberVO = null;
        HashMap map = new HashMap();
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT rm.msisdn,rm.subscriber_id, rm.channel_user_id, rm.channel_user_category,  rm.owner_id, ");
        strBuff.append(" rm.employee_code, rm.employee_name, rm.monthly_limit,  rm.min_txn_amount, rm.max_txn_amount, ");
        strBuff.append(" rm.total_txn_count, rm.total_txn_amount,  rm.black_list_status, rm.remark, ");
        strBuff.append(" rm.approved_by, rm.approved_on, rm.associated_by,  rm.status, rm.association_date, rm.last_transaction_date, ");
        strBuff.append(" rm.subscriber_type, rm.language, rm.country,c.domain_code,c.cp2p_payer_status,c.cp2p_payee_status,c.cp2p_within_list,c.CP2P_WITHIN_LIST_LEVEL FROM restricted_msisdns rm,categories c WHERE rm.msisdn in ( ?,?) ");
        strBuff.append(" AND rm.status IN( ? , ? ) AND rm.restricted_type = ? and c.category_code=rm.channel_user_category");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmtSelect.setString(i++, p_msisdn);
            pstmtSelect.setString(i++, p_senderMsisdn);
            pstmtSelect.setString(i++, PretupsI.STATUS_ACTIVE);
            pstmtSelect.setString(i++, PretupsI.STATUS_SUSPEND);
            pstmtSelect.setString(i++, PretupsI.DEFAULT_RESTRICTED_TYPE);

            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                restrictedSubscriberVO = new RestrictedSubscriberVO();
                restrictedSubscriberVO.setMsisdn(rs.getString("msisdn"));
                restrictedSubscriberVO.setSubscriberID(rs.getString("subscriber_id"));
                restrictedSubscriberVO.setChannelUserID(rs.getString("channel_user_id"));
                restrictedSubscriberVO.setChannelUserCategory(rs.getString("channel_user_category"));
                restrictedSubscriberVO.setOwnerID(rs.getString("owner_id"));
                restrictedSubscriberVO.setEmployeeCode(rs.getString("employee_code"));
                restrictedSubscriberVO.setEmployeeName(rs.getString("employee_name"));
                restrictedSubscriberVO.setMonthlyLimit(rs.getLong("monthly_limit"));
                restrictedSubscriberVO.setMinTxnAmount(rs.getLong("min_txn_amount"));
                restrictedSubscriberVO.setMaxTxnAmount(rs.getLong("max_txn_amount"));
                restrictedSubscriberVO.setBlackListStatus(rs.getString("black_list_status"));
                restrictedSubscriberVO.setRemarks(rs.getString("remark"));
                restrictedSubscriberVO.setApprovedBy(rs.getString("approved_by"));
                restrictedSubscriberVO.setApprovedOn(rs.getDate("approved_on"));
                restrictedSubscriberVO.setAssociatedBy(rs.getString("associated_by"));
                restrictedSubscriberVO.setAssociationDate(rs.getDate("association_date"));
                restrictedSubscriberVO.setMonthlyTransferAmount(rs.getLong("total_txn_amount"));
                restrictedSubscriberVO.setStatus(rs.getString("status"));
                restrictedSubscriberVO.setSubscriberType(rs.getString("subscriber_type"));
                restrictedSubscriberVO.setMonthlyTransferCount(rs.getLong("total_txn_count"));
                restrictedSubscriberVO.setLanguage(rs.getString("language"));
                restrictedSubscriberVO.setCountry(rs.getString("country"));
                restrictedSubscriberVO.setLastTransferOn(rs.getDate("last_transaction_date"));
                restrictedSubscriberVO.setSubscriberDomainCode(rs.getString("domain_code"));
                restrictedSubscriberVO.setCp2pPayeeStatus(rs.getString("cp2p_payee_status"));
                restrictedSubscriberVO.setCp2pPayerStatus(rs.getString("cp2p_payer_status"));
                restrictedSubscriberVO.setCp2pWithInList(rs.getString("cp2p_within_list"));
                restrictedSubscriberVO.setCp2pListLevel(rs.getString("CP2P_WITHIN_LIST_LEVEL"));
                map.put(rs.getString("msisdn"), restrictedSubscriberVO);
            }

        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberDAO[checkPayeeAllowed]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberDAO[checkPayeeAllowed]", "", "", "", "Exception:" + ex.getMessage());
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
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting loadRestrictedMsisdnDetail:" + map);
            }
        }
        return map;
    }

    public RestrictedSubscriberVO loadRestrictedSubscriberDetailsForC2S(Connection p_con, String p_ownerId, String p_msisdn, boolean p_doLock) throws BTSLBaseException {
        final String methodName = "loadRestrictedSubscriberDetailsForC2S";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_ownerId=" + p_ownerId + ", p_msisdn=" + p_msisdn + ", p_doLock=" + p_doLock);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        RestrictedSubscriberVO restrictedSubscriberVO = null;
        StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT rm.subscriber_id, rm.channel_user_id, rm.channel_user_category,  rm.owner_id, ");
        strBuff.append(" rm.employee_code, rm.employee_name, rm.monthly_limit,  rm.min_txn_amount, rm.max_txn_amount, ");
        strBuff.append(" rm.total_txn_count, rm.total_txn_amount,  rm.black_list_status, rm.remark, ");
        strBuff.append(" rm.approved_by, rm.approved_on, rm.associated_by,  rm.status, rm.association_date, rm.last_transaction_date, ");
        strBuff.append(" rm.subscriber_type, rm.language, rm.country,c.domain_code,c.C2S_PAYEE_STATUS FROM restricted_msisdns rm,categories c WHERE msisdn = ? AND owner_id = ? ");
        strBuff.append(" AND rm.status IN( ? , ? ) AND c.category_code=rm.channel_user_category");
        if (p_doLock) {
            // DB220120123for update WITH RS
            if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
                strBuff.append("FOR UPDATE WITH RS ");
            } else {
                strBuff.append("FOR UPDATE ");
            }
        }

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmtSelect.setString(i++, p_msisdn);
            pstmtSelect.setString(i++, p_ownerId);
            pstmtSelect.setString(i++, PretupsI.STATUS_ACTIVE);
            pstmtSelect.setString(i++, PretupsI.STATUS_SUSPEND);

            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                restrictedSubscriberVO = new RestrictedSubscriberVO();
                restrictedSubscriberVO.setMsisdn(p_msisdn);
                restrictedSubscriberVO.setSubscriberID(rs.getString("subscriber_id"));
                restrictedSubscriberVO.setChannelUserID(rs.getString("channel_user_id"));
                restrictedSubscriberVO.setChannelUserCategory(rs.getString("channel_user_category"));
                restrictedSubscriberVO.setOwnerID(rs.getString("owner_id"));
                restrictedSubscriberVO.setEmployeeCode(rs.getString("employee_code"));
                restrictedSubscriberVO.setEmployeeName(rs.getString("employee_name"));
                restrictedSubscriberVO.setMonthlyLimit(rs.getLong("monthly_limit"));
                restrictedSubscriberVO.setMinTxnAmount(rs.getLong("min_txn_amount"));
                restrictedSubscriberVO.setMaxTxnAmount(rs.getLong("max_txn_amount"));
                restrictedSubscriberVO.setBlackListStatus(rs.getString("black_list_status"));
                restrictedSubscriberVO.setRemarks(rs.getString("remark"));
                restrictedSubscriberVO.setApprovedBy(rs.getString("approved_by"));
                restrictedSubscriberVO.setApprovedOn(rs.getDate("approved_on"));
                restrictedSubscriberVO.setAssociatedBy(rs.getString("associated_by"));
                restrictedSubscriberVO.setAssociationDate(rs.getDate("association_date"));
                restrictedSubscriberVO.setMonthlyTransferAmount(rs.getLong("total_txn_amount"));
                restrictedSubscriberVO.setStatus(rs.getString("status"));
                restrictedSubscriberVO.setSubscriberType(rs.getString("subscriber_type"));
                restrictedSubscriberVO.setMonthlyTransferCount(rs.getLong("total_txn_count"));
                restrictedSubscriberVO.setLanguage(rs.getString("language"));
                restrictedSubscriberVO.setCountry(rs.getString("country"));
                restrictedSubscriberVO.setLastTransferOn(rs.getDate("last_transaction_date"));
                restrictedSubscriberVO.setSubscriberDomainCode(rs.getString("domain_code"));
                restrictedSubscriberVO.setRechargeThroughParent(rs.getString("C2S_PAYEE_STATUS"));
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberDAO[loadRestrictedSubscriberDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadRestrictedSubscriberDetails", "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberDAO[loadRestrictedSubscriberDetails]", "", "", "", "Exception:" + ex.getMessage());
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
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting restrictedSubscriberVO:" + restrictedSubscriberVO);
            }
        }
        return restrictedSubscriberVO;
    }

    public boolean loadChannelUserDetailsForC2SViaSms(Connection p_con, String p_msisdn, String s_msisdn) throws BTSLBaseException {
        final String methodName = "loadChannelUserDetailsForC2SViaSms";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:  p_msisdn=" + p_msisdn);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        PreparedStatement pstmtSelect1 = null;
        ResultSet rs1 = null;
        boolean userExist = false;
        String userID = null;
        String listAllowed = null;
        String c2spayeestatus = null;
        String channeluserID = null;
        String ownerID = null;
        StringBuffer strBuff = new StringBuffer();
        StringBuffer strBuff1 = new StringBuffer();

        strBuff.append("SELECT user_id, TRANSFERTOLISTONLY,C2S_PAYEE_STATUS FROM USERS U, CATEGORIES C ");
        strBuff.append(" WHERE U.status = ? and U.msisdn = ? and U.CATEGORY_CODE=C.CATEGORY_CODE ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmtSelect.setString(i++, PretupsI.STATUS_ACTIVE);
            pstmtSelect.setString(i++, p_msisdn);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                userID = rs.getString("user_id");
                listAllowed = rs.getString("TRANSFERTOLISTONLY");
                c2spayeestatus = rs.getString("C2S_PAYEE_STATUS");

                strBuff1.append("SELECT CHANNEL_USER_ID,OWNER_ID FROM RESTRICTED_MSISDNS RM,USER_SERVICES US WHERE RM.msisdn= ? ");
                strBuff1.append(" AND RM.status=? AND RM.CHANNEL_USER_ID=US.USER_ID AND US.SERVICE_TYPE= ? ");
                strBuff1.append(" AND CHANNEL_USER_ID=? ");

                String sqlSelect1 = strBuff1.toString();
                if (_log.isDebugEnabled()) {
                    _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect1);
                }

                pstmtSelect1 = p_con.prepareStatement(sqlSelect1);
                pstmtSelect1.setString(1, s_msisdn);
                pstmtSelect1.setString(2, PretupsI.STATUS_ACTIVE);
                pstmtSelect1.setString(3, PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
                pstmtSelect1.setString(4, userID);

                rs1 = pstmtSelect1.executeQuery();
                if (rs1.next()) {
                    channeluserID = rs1.getString("CHANNEL_USER_ID");
                    ownerID = rs1.getString("OWNER_ID");
                } else {
                    if ("N".equalsIgnoreCase(listAllowed) && "N".equalsIgnoreCase(c2spayeestatus)) {
                        userExist = true;
                    } else {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CREDITREQVIASMS_CHANNELUSER_FAILED);
                    }

                }
                if ("Y".equalsIgnoreCase(listAllowed) && "Y".equalsIgnoreCase(c2spayeestatus) && (userID.equalsIgnoreCase(channeluserID) || userID.equalsIgnoreCase(ownerID))) {
                    userExist = true;
                }
            } else {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.REQVIASMS_CHANNEL_USER_NOT_EXIST);
            }

        }

        catch (BTSLBaseException be) {
           throw new BTSLBaseException(be) ;
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "RestrictedSubscriberDAO[loadChannelUserDetailsForC2SViaSms]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
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
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect1 != null) {
                    pstmtSelect1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting blacklistType:" + userExist);
            }
        }
        return userExist;
    }

}

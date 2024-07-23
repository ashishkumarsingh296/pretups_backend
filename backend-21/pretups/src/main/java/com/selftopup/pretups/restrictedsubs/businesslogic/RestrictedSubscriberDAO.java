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

package com.selftopup.pretups.restrictedsubs.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.Constants;

/**
 * 
 */
public class RestrictedSubscriberDAO {
    /**
     * Field _log.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

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
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered: p_ownerId=" + p_ownerId + ", p_msisdn=" + p_msisdn + ", p_doLock=" + p_doLock);
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
        if (p_doLock)
            // DB220120123for update WITH RS
            if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype")))
                strBuff.append("FOR UPDATE WITH RS ");
            else
                strBuff.append("FOR UPDATE ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
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
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting restrictedSubscriberVO:" + restrictedSubscriberVO);
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
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered: p_restrictedSubscriberVO" + p_restrictedSubscriberVO);
        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        RestrictedSubscriberVO restrictedSubscriberVO = null;
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" UPDATE restricted_msisdns SET total_txn_count=?, last_transaction_date=?, ");
        strBuff.append(" total_txn_amount= ? WHERE owner_id=? AND msisdn=? ");

        String sqlUpdate = strBuff.toString();
        if (_log.isDebugEnabled())
            _log.debug(methodName, "QUERY sqlUpdate=" + sqlUpdate);

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
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting: updateCount=" + updateCount);
        }
        return updateCount;
    }

    // method for loading sender and receiver info from restricted msisdn
    public HashMap loadRestrictedMsisdnDetail(Connection p_con, String p_msisdn, String p_senderMsisdn) throws BTSLBaseException {
        final String methodName = "loadRestrictedMsisdnDetail";
        if (_log.isDebugEnabled())
            _log.debug(methodName, "Entered: p_con=" + p_con + ",p_msisdn=" + p_msisdn + ", p_senderMsisdn=" + p_senderMsisdn);
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
        if (_log.isDebugEnabled())
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
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
            if (_log.isDebugEnabled())
                _log.debug(methodName, "Exiting loadRestrictedMsisdnDetail:" + map);
        }
        return map;
    }

}

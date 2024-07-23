/**
 * IATRestrictedSubscriberDAO.java
 * ----------------------------------------------------------------------------
 * ------
 * Name Date History
 * ----------------------------------------------------------------------------
 * ------
 * Babu Kunwar 27/09/2011 Initial Creation
 * ----------------------------------------------------------------------------
 * ------
 * Copyright (c) 2011 Comviva Technologies Ltd.
 * This class is responsible for the DB activity of Corporate IAT Recharge of
 * the
 * restricted subscribers.
 */

package com.btsl.pretups.iatrestrictedsubs.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberVO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchDetailVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

public class IATRestrictedSubscriberDAO {

    private static Log _log = LogFactory.getLog(IATRestrictedSubscriberDAO.class.getName());

    /**
     * Method isIATSubscriberExistByStatus.
     * This method is to check that the subscriber is exist of the
     * passed status under the passed owner and msisdn
     * 
     * @author babu.kunwar
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
    public String isIATSubscriberExistByStatus(Connection p_con, String p_ownerID, ArrayList p_subscriberList, String p_statusUsed, String p_status, String p_subscriberType) throws BTSLBaseException {

        final String methodName = "isIATSubscriberExistByStatus";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_ownerID = " + p_ownerID + ",p_subscriberList size = " + p_subscriberList.size() + ",p_status=" + p_status + ",p_statusUsed=" + p_statusUsed + "p_subscriberType=" + p_subscriberType);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        StringBuffer returnDataStrBuff = new StringBuffer();
        String returnStr = null;
        StringBuffer strBuff = new StringBuffer("SELECT 1 FROM restricted_msisdns WHERE owner_id =? AND msisdn = ? AND restricted_type=?");

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
            RestrictedSubscriberVO iatRestrictedSubscriberVO = null;
            for (int index = 0, j = p_subscriberList.size(); index < j; index++) {
                iatRestrictedSubscriberVO = (RestrictedSubscriberVO) p_subscriberList.get(index);
                int i = 1;
                pstmtSelect.setString(i++, p_ownerID);
                pstmtSelect.setString(i++, iatRestrictedSubscriberVO.getMsisdn());
                pstmtSelect.setString(i++, PretupsI.RESTRICTED_TYPE);
                if (p_subscriberType != null) {
                    pstmtSelect.setString(i++, p_subscriberType);
                }
                if (p_statusUsed.equals(PretupsI.STATUS_EQUAL) || p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
                    pstmtSelect.setString(i++, p_status);
                }
                rs = pstmtSelect.executeQuery();
                if (rs.next()) {
                    returnDataStrBuff.append(iatRestrictedSubscriberVO.getMsisdn() + ",");
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATRestrictedSubscriberDAO[isIATSubscriberExistByStatus]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATRestrictedSubscriberDAO[isIATSubscriberExistByStatus]", "", "", "", "Exception:" + ex.getMessage());
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

    /*
     * ==========================================================================
     * ===========
     * * Susbcriber Suspend and Resume Methods *
     * ==========================================================================
     * ===========
     */

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
     * @author babu.kunwar
     *         modify query select rm.language,rm.country
     */
    public LinkedHashMap loadScheduleBatchDetailsList(Connection p_con, String p_batch_id, String p_statusUsed, String p_status) throws BTSLBaseException {
        String methodName = "loadScheduleBatchDetailsList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_batch_id = " + p_batch_id + ",p_status=" + p_status + ",p_statusUsed=" + p_statusUsed);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ScheduleBatchDetailVO scheduleDetailVO = null;
        LinkedHashMap<String, ScheduleBatchDetailVO> detailHashMap = new LinkedHashMap<String, ScheduleBatchDetailVO>();
        try {
           
        	IATRestrictedSubscriberQry iatRestrictedSubscriberQry = (IATRestrictedSubscriberQry)ObjectProducer.getObject(QueryConstants.IAT_RESTRICTED_SUBSC_QRY, QueryConstants.QUERY_PRODUCER);
        	pstmtSelect = iatRestrictedSubscriberQry.loadScheduleBatchDetailsListQry(p_con, p_batch_id, p_statusUsed, p_status);
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
                detailHashMap.put(scheduleDetailVO.getMsisdn(), scheduleDetailVO);
            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATRestrictedSubscriberDAO[loadScheduleBatchDetailsList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "IATRestrictedSubscriberDAO[loadScheduleBatchDetailsList]", "", "", "", "Exception:" + e.getMessage());
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
                _log.debug(methodName, "Exiting: detailHashMap Size=" + detailHashMap.size());
            }
        }
        return detailHashMap;
    }
}

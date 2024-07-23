package com.selftopup.pretups.p2p.query.businesslogic;

/*
 * #P2pQueryHistoryDAO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * july 29, 2005 ved prakash sharma Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.util.BTSLUtil;

/**
 * 
 */

public class P2pQueryHistoryDAO {

    /**
     * Field _log.
     */
    private Log _log = LogFactory.getFactory().getInstance(P2pQueryHistoryDAO.class.getName());

    /**
     * Method loadSubscriberDetails.
     * This method is used to load subscriber details into ArrayList
     * If there is any error then throws the SQLException or Exception
     * 
     * @param p_con
     *            Connection
     * @param p_queryHistoryVO
     *            P2pQueryHistoryVO
     * @return interfaceDetails ArrayList
     * @throws SQLException
     * @throws Exception
     */

    public ArrayList loadSubscriberDetails(Connection p_con, P2pQueryHistoryVO p_queryHistoryVO) throws SQLException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("loadSubscriberDetails()", "Entered::p_queryHistoryVO= " + p_queryHistoryVO);

        StringBuffer strBuff = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList subscriberDetails = new ArrayList();

        try {
            /*
             * strBuff=new StringBuffer(
             * "SELECT activated_on, billing_cycle_date, billing_type, buddy_seq_number,"
             * );
             * strBuff.append(
             * " consecutive_failures, created_by, created_on, credit_limit, daily_transfer_amount,"
             * );
             * strBuff.append(
             * " daily_transfer_count, last_success_transfer_date, last_transfer_amount, last_transfer_id,"
             * );
             * strBuff.append(
             * " last_transfer_msisdn, last_transfer_on, last_transfer_status, last_transfer_type,"
             * );
             * strBuff.append(
             * " modified_by, modified_on, monthly_transfer_amount, monthly_transfer_count, msisdn,"
             * );
             * strBuff.append(
             * " network_code, pin, pin_block_count, prefix_id, prev_daily_transfer_amount,"
             * );
             * strBuff.append(
             * " prev_daily_transfer_count, prev_monthly_transfer_amount, prev_monthly_transfer_count,"
             * );
             * strBuff.append(
             * " prev_transfer_date, prev_transfer_month_date, prev_transfer_week_date,"
             * );
             * strBuff.append(
             * " prev_weekly_transfer_amount, prev_weekly_transfer_count, registered_on,"
             * );
             * strBuff.append(
             * " service_class_code, skey_required, status, subscriber_type, total_transfer_amount,"
             * );
             * strBuff.append(
             * " total_transfers, user_id, user_name, weekly_transfer_amount, weekly_transfer_count"
             * );
             * strBuff.append(" FROM p2p_subscribers_history");
             * strBuff.append(" WHERE msisdn=?");
             */

            /*
             * SELECT activated_on, billing_cycle_date, billing_type,
             * buddy_seq_number,
             * consecutive_failures, PSUB.created_by, PSUB.created_on,
             * credit_limit, daily_transfer_amount,
             * daily_transfer_count, last_success_transfer_date,
             * last_transfer_amount, last_transfer_id,
             * last_transfer_msisdn, last_transfer_on, last_transfer_status,
             * last_transfer_type,
             * PSUB.modified_by, PSUB.modified_on, monthly_transfer_amount,
             * monthly_transfer_count, msisdn,
             * network_code, pin, pin_block_count, prefix_id,
             * prev_daily_transfer_amount,
             * prev_daily_transfer_count, prev_monthly_transfer_amount,
             * prev_monthly_transfer_count,
             * prev_transfer_date, prev_transfer_month_date,
             * prev_transfer_week_date,
             * prev_weekly_transfer_amount, prev_weekly_transfer_count,
             * registered_on,
             * service_class_code, skey_required, subscriber_type,
             * total_transfer_amount,
             * total_transfers, user_id, user_name, weekly_transfer_amount,
             * weekly_transfer_count,
             * LOOK.lookup_name status
             * FROM p2p_subscribers_history PSUB, lookups LOOK
             * WHERE msisdn='9810800002'
             * AND LOOK.lookup_code(+) = PSUB.status
             * AND LOOK.lookup_type(+) = 'URTYP'
             */

            strBuff = new StringBuffer("SELECT activated_on, billing_cycle_date, billing_type, buddy_seq_number,");
            strBuff.append(" consecutive_failures, PSUB.created_by, PSUB.created_on, credit_limit, daily_transfer_amount,");
            strBuff.append(" daily_transfer_count, last_success_transfer_date, last_transfer_amount, last_transfer_id,");
            strBuff.append(" last_transfer_msisdn, last_transfer_on, last_transfer_status, last_transfer_type,");
            strBuff.append(" PSUB.modified_by, PSUB.modified_on, monthly_transfer_amount, monthly_transfer_count, msisdn,");
            strBuff.append(" network_code, pin, pin_block_count, prefix_id, prev_daily_transfer_amount,");
            strBuff.append(" prev_daily_transfer_count, prev_monthly_transfer_amount, prev_monthly_transfer_count,");
            strBuff.append(" prev_transfer_date, prev_transfer_month_date, prev_transfer_week_date,");
            strBuff.append(" prev_weekly_transfer_amount, prev_weekly_transfer_count, registered_on,");
            strBuff.append(" service_class_code, skey_required,  subscriber_type, total_transfer_amount,");
            strBuff.append(" total_transfers, user_id, user_name, weekly_transfer_amount, weekly_transfer_count,");
            strBuff.append(" LOOK.lookup_name status");
            strBuff.append(" FROM p2p_subscribers_history PSUB, lookups LOOK");
            strBuff.append(" WHERE msisdn = ?");
            strBuff.append(" AND LOOK.lookup_code(+) = PSUB.status");
            strBuff.append(" AND LOOK.lookup_type(+) = ?");

            int i = 0;
            if ((p_queryHistoryVO.getFromDate().trim().length() > 0) && (p_queryHistoryVO.getSubscriberMsisdn().length() > 0)) {
                i = 0;
                strBuff.append(" AND (trunc(PSUB.modified_on)>=? AND trunc(PSUB.modified_on)<=?)");
                strBuff.append(" ORDER BY PSUB.modified_on");
                pstmtSelect = p_con.prepareStatement(strBuff.toString());
                pstmtSelect.setString(++i, p_queryHistoryVO.getSubscriberMsisdn());
                pstmtSelect.setString(++i, PretupsI.USER_STATUS_TYPE);
                pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_queryHistoryVO.getFromDate())));
                pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(p_queryHistoryVO.getToDate())));
            }

            String selectQuery = strBuff.toString();
            if (_log.isDebugEnabled())
                _log.debug("loadInterfaceDetails()", "QUERY= " + selectQuery);

            rs = pstmtSelect.executeQuery();

            if (_log.isDebugEnabled())
                _log.debug("loadSubscriberDetails()", "QUERY Executed= " + selectQuery);
            while (rs.next()) {
                P2pQueryHistoryVO queryHistoryVO = new P2pQueryHistoryVO();

                queryHistoryVO.setSubscriberMsisdn(rs.getString("msisdn"));
                queryHistoryVO.setModified_on(rs.getTimestamp("modified_on"));
                queryHistoryVO.setStatus(rs.getString("status"));
                subscriberDetails.add(queryHistoryVO);
            }
        } catch (SQLException sqe) {
            if (_log.isDebugEnabled())
                _log.error("loadSubscriberDetails()", " SQL Exception::" + sqe.getMessage());
            sqe.printStackTrace();
            throw new BTSLBaseException(this, "loadSubscriberDetails()", "error.general.processing");
        } catch (Exception e) {
            if (_log.isDebugEnabled())
                _log.error("loadSubscriberDetails()", " Exception::" + e.getMessage());
            e.printStackTrace();
            throw new BTSLBaseException(this, "loadSubscriberDetails()", "error.general.processing");
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception ex) {
            }
            try {
                if (pstmtSelect != null)
                    pstmtSelect.close();
            } catch (Exception ex) {
            }
            if (_log.isDebugEnabled())
                _log.debug("loadSubscriberDetails()", " Exiting.. subscriberDetails size=" + subscriberDetails.size());
        }
        return subscriberDetails;
    }
}

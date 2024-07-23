package com.btsl.pretups.p2p.query.businesslogic;

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

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;


/**
 * P2pQueryHistoryDAO
 */
public class P2pQueryHistoryDAO {

    /**
     * Field log.
     */
    private Log log = LogFactory.getFactory().getInstance(P2pQueryHistoryDAO.class.getName());

    /**
     * Method loadSubscriberDetails.
     * This method is used to load subscriber details into ArrayList
     * If there is any error then throws the SQLException or Exception
     * 
     * @param pCon
     *            Connection
     * @param pqueryHistoryVO
     *            P2pQueryHistoryVO
     * @return interfaceDetails ArrayList
     * @throws SQLException
     * @throws Exception
     */

    public ArrayList loadSubscriberDetails(Connection pCon, P2pQueryHistoryVO pqueryHistoryVO) throws SQLException, Exception {
        
    	final String methodName = "loadSubscriberDetails";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered::pqueryHistoryVO= " + pqueryHistoryVO);
        }
     
       
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList subscriberDetails = new ArrayList();

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

     
        	P2PQueryHistoryQry p2pHistoryQry= (P2PQueryHistoryQry)ObjectProducer.getObject(QueryConstants.P2P_QUERY_HISTORY_QRY, QueryConstants.QUERY_PRODUCER);
        	
        	pstmtSelect = p2pHistoryQry.loadSubscriberDetails(pCon, pqueryHistoryVO);

            if (pstmtSelect != null) {
                rs = pstmtSelect.executeQuery();
            }

           
            while (rs != null && rs.next()) {
                final P2pQueryHistoryVO queryHistoryVO = new P2pQueryHistoryVO();

                queryHistoryVO.setSubscriberMsisdn(rs.getString("msisdn"));
                queryHistoryVO.setModified_on(rs.getTimestamp("modified_on"));
                queryHistoryVO.setStatus(rs.getString("status"));
                subscriberDetails.add(queryHistoryVO);
            }
        } catch (SQLException sqe) {
            if (log.isDebugEnabled()) {
                log.error(methodName, " SQL Exception::" + sqe.getMessage());
            }
            log.errorTrace(methodName, sqe);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.error(methodName, " Exception::" + e.getMessage());
            }
            log.errorTrace(methodName, e);
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                log.errorTrace(methodName, ex);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting.. subscriberDetails size=" + subscriberDetails.size());
            }
        }
        return subscriberDetails;
    }
}

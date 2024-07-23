package com.btsl.pretups.p2p.subscriber.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;

public class SubscriberPostgresQry  implements SubscriberQry{

	private Log log = LogFactory.getLog(this.getClass());

	@Override
	public PreparedStatement loadSubscriberDetailsByMsisdnQry(Connection con, String msisdn,
			String serviceType)throws SQLException {
		final StringBuilder selectQueryBuff = new StringBuilder(" SELECT p2ps.user_id,p2ps.subscriber_type,p2ps.prefix_id,p2ps.user_name,p2ps.status,p2ps.network_code,p2ps.pin,p2ps.pin_block_count,");
		selectQueryBuff.append(" p2ps.last_transfer_amount,p2ps.last_transfer_on,p2ps.last_transfer_type,p2ps.last_transfer_status,p2ps.last_transfer_id,p2ps.last_transfer_msisdn,");
		selectQueryBuff
		.append(" p2ps.pin_modified_on,p2ps.first_invalid_pin_time,p2ps.buddy_seq_number,p2ps.total_transfers,p2ps.total_transfer_amount,p2ps.request_status,p2ps.billing_type,p2ps.billing_cycle_date, ");
		selectQueryBuff.append(" p2ps.credit_limit,p2ps.activated_on,p2ps.registered_on,p2ps.created_on,p2ps.created_by,p2ps.modified_on,p2ps.modified_by,p2ps.consecutive_failures,p2ps.skey_required,");
		if(serviceType!=null){
			selectQueryBuff.append(" p2psc.daily_transfer_count,p2psc.monthly_transfer_count,p2psc.weekly_transfer_count,p2psc.daily_transfer_amount,p2psc.service_type, ");
			selectQueryBuff.append(" p2psc.monthly_transfer_amount,p2psc.weekly_transfer_amount,p2psc.prev_daily_transfer_count,p2psc.prev_monthly_transfer_count,");
			selectQueryBuff.append(" p2psc.prev_weekly_transfer_count,p2psc.prev_daily_transfer_amount,p2psc.prev_monthly_transfer_amount,p2psc.prev_weekly_transfer_amount,");
			selectQueryBuff.append(" p2psc.prev_transfer_date,p2psc.prev_transfer_week_date,p2psc.prev_transfer_month_date,p2psc.VPIN_INVALID_COUNT,  ");
		}
		selectQueryBuff.append(" p2ps.service_class_code,p2ps.last_success_transfer_date,p2ps.service_class_id,p2ps.language, p2ps.country,p2ps.email_id,p2ps.imei");
		selectQueryBuff.append(" FROM P2P_SUBSCRIBERS p2ps ");
		if(serviceType!=null){
			selectQueryBuff.append(" left join P2P_SUBSCRIBERS_COUNTERS p2psc on (p2ps.msisdn=p2psc.msisdn AND p2psc.service_type=? ) ");
		}
		selectQueryBuff.append(" WHERE p2ps.msisdn=? AND p2ps.status <> ? AND p2ps.status <> ? ");

		final String selectQuery = selectQueryBuff.toString();
		LogFactory.printLog("loadSubscriberDetailsByMsisdnQry", selectQueryBuff.toString(), log);

		PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);
		int i = 0;
		if(serviceType!=null){
			pstmtSelect.setString(++i, serviceType);
		}
		pstmtSelect.setString(++i, msisdn);
		pstmtSelect.setString(++i, PretupsI.USER_STATUS_DELETED);
		pstmtSelect.setString(++i, PretupsI.USER_STATUS_CANCELED);
		return pstmtSelect;
	}

	@Override
	public PreparedStatement loadSubscriberDetailsQry(Connection con, String msisdn,
			String serviceType,Date fromDate, Date toDate, String status) throws SQLException {
		final StringBuilder selectQueryBuff = new StringBuilder();
		selectQueryBuff
		.append(" SELECT KV.value txnstatus,P2P_SUB.user_id,P2P_SUB.msisdn,P2P_SUB.subscriber_type,P2P_SUB.prefix_id,P2P_SUB.user_name,P2P_SUB.status,P2P_SUB.network_code,P2P_SUB.pin,P2P_SUB.pin_block_count,");
		selectQueryBuff
		.append(" P2P_SUB.last_transfer_amount,P2P_SUB.last_transfer_on,P2P_SUB.last_transfer_type,P2P_SUB.last_transfer_status,P2P_SUB.last_transfer_id,P2P_SUB.last_transfer_msisdn,");
		selectQueryBuff
		.append(" P2P_SUB.buddy_seq_number,P2P_SUB.total_transfers,P2P_SUB.total_transfer_amount,P2P_SUB.request_status,P2P_SUB.billing_type,P2P_SUB.billing_cycle_date, ");
		selectQueryBuff
		.append(" P2P_SUB.credit_limit,P2P_SUB.activated_on,P2P_SUB.registered_on,P2P_SUB.created_on,P2P_SUB.created_by,P2P_SUB.modified_on,P2P_SUB.modified_by,P2P_SUB.consecutive_failures,L.lookup_name, P2P_SUB.skey_required, ");
		if(serviceType!=null && !(PretupsI.ALL.equals(serviceType))){
			selectQueryBuff
			.append(" P2P_SUB_CNT.daily_transfer_count,P2P_SUB_CNT.monthly_transfer_count,P2P_SUB_CNT.weekly_transfer_count,P2P_SUB_CNT.daily_transfer_amount, P2P_SUB_CNT.service_type, ");
			selectQueryBuff.append(" P2P_SUB_CNT.monthly_transfer_amount,P2P_SUB_CNT.weekly_transfer_amount,P2P_SUB_CNT.prev_daily_transfer_count,P2P_SUB_CNT.prev_monthly_transfer_count, ");
			selectQueryBuff
			.append(" P2P_SUB_CNT.prev_weekly_transfer_count,P2P_SUB_CNT.prev_daily_transfer_amount,P2P_SUB_CNT.prev_monthly_transfer_amount,P2P_SUB_CNT.prev_weekly_transfer_amount, ");
			selectQueryBuff.append(" P2P_SUB_CNT.prev_transfer_date,P2P_SUB_CNT.prev_transfer_week_date,P2P_SUB_CNT.prev_transfer_month_date, ");
		}
		selectQueryBuff
		.append(" P2P_SUB.service_class_code,P2P_SUB.last_success_transfer_date,P2P_SUB.language,P2P_SUB.country,P2P_SUB.service_class_id ");
		selectQueryBuff.append(" FROM p2p_subscribers P2P_SUB left join key_values KV on (KV.key=P2P_SUB.last_transfer_status AND KV.type=?) ");
		if(serviceType!=null && !(PretupsI.ALL.equals(serviceType))){
			selectQueryBuff.append(" left join P2P_SUBSCRIBERS_COUNTERS P2P_SUB_CNT on P2P_SUB.MSISDN = P2P_SUB_CNT.MSISDN ");
		}
		selectQueryBuff.append(", lookups L   ");
		selectQueryBuff.append("WHERE P2P_SUB.subscriber_type=L.lookup_code AND L.lookup_type=?  ");
		if(serviceType!=null && !(PretupsI.ALL.equals(serviceType))){
			selectQueryBuff.append(" AND P2P_SUB_CNT.service_type=? ");
		}
		if (!status.equals(PretupsI.ALL)) {
			selectQueryBuff.append("AND P2P_SUB.status=? ");
		}
		if (msisdn != null && msisdn.length() > 0) {
			selectQueryBuff.append("AND P2P_SUB.msisdn=? ");
		}
		if (fromDate != null && toDate != null) {
			selectQueryBuff.append("AND date_trunc('day',registered_on::TIMESTAMP)  >= ? AND date_trunc('day',registered_on::TIMESTAMP)  <=? ");
		}

		final String selectQuery = selectQueryBuff.toString();
		LogFactory.printLog("loadSubscriberDetailsByMsisdnQry", selectQuery, log);
		PreparedStatement pstmtSelect = con.prepareStatement(selectQuery);

		int i = 1;
		pstmtSelect.setString(i, PretupsI.KEY_VALUE_P2P_STATUS);
		i++;
		pstmtSelect.setString(i, PretupsI.SUBSRICBER_TYPE);
		i++;
		if(serviceType!=null && !(PretupsI.ALL.equals(serviceType))){
			pstmtSelect.setString(i, serviceType);
			i++;
		}
		if (!status.equals(PretupsI.ALL)) {
			pstmtSelect.setString(i, status);
			i++;
		}
		if (msisdn != null && msisdn.length() > 0) {
			pstmtSelect.setString(i, msisdn);
			i++;
		}
		if (fromDate != null && toDate != null) {
			pstmtSelect.setDate(i, BTSLUtil.getSQLDateFromUtilDate(fromDate));
			i++;
			pstmtSelect.setDate(i, BTSLUtil.getSQLDateFromUtilDate(toDate));
		}
		return pstmtSelect;
	}

	@Override
	public PreparedStatement loadSubscriberDetailsByIDForUpdateQry(
			Connection con, String userID, String serviceType)
					throws SQLException {
		final StringBuilder selectQueryBuff = new StringBuilder(
				"SELECT p2ps.user_id,p2ps.msisdn,p2ps.subscriber_type,p2ps.prefix_id,p2ps.network_code,p2ps.last_transfer_amount,p2ps.last_transfer_on,p2ps.last_transfer_type,p2ps.last_transfer_status,p2ps.last_transfer_id,p2ps.last_transfer_msisdn,");
		selectQueryBuff.append(" p2ps.total_transfers,p2ps.total_transfer_amount, p2ps.credit_limit,p2ps.consecutive_failures,p2ps.status, ");
		if(serviceType!=null){
			selectQueryBuff.append(" p2psc.daily_transfer_count,p2psc.monthly_transfer_count,p2psc.weekly_transfer_count,p2psc.daily_transfer_amount,p2psc.service_type, ");
			selectQueryBuff.append(" p2psc.monthly_transfer_amount,p2psc.weekly_transfer_amount,p2psc.prev_daily_transfer_count,p2psc.prev_monthly_transfer_count,");
			selectQueryBuff.append(" p2psc.prev_weekly_transfer_count,p2psc.prev_daily_transfer_amount,p2psc.prev_monthly_transfer_amount,p2psc.prev_weekly_transfer_amount,");
			selectQueryBuff.append(" p2psc.prev_transfer_date,p2psc.prev_transfer_week_date,p2psc.prev_transfer_month_date,p2psc.last_success_date,p2psc.VPIN_INVALID_COUNT, ");
		}
		selectQueryBuff.append(" p2ps.service_class_code,p2ps.last_success_transfer_date");

		selectQueryBuff.append(" FROM P2P_SUBSCRIBERS p2ps ");
		if(serviceType!=null){
			selectQueryBuff.append(" left join P2P_SUBSCRIBERS_COUNTERS p2psc on ( p2ps.msisdn = p2psc.msisdn and service_type=? ) ");
		}
		selectQueryBuff.append("WHERE p2ps.user_id=? ");
		selectQueryBuff.append(" FOR UPDATE OF p2ps");

		final String selectQuery = selectQueryBuff.toString();
		LogFactory.printLog("loadSubscriberDetailsByIDForUpdateQry", selectQuery, log);
		PreparedStatement  pstmtSelect = con.prepareStatement(selectQuery);
		int i = 0;
		if(serviceType!=null){
			pstmtSelect.setString(++i,serviceType);
		}
		pstmtSelect.setString(++i, userID);
		
		return pstmtSelect;
	}

}

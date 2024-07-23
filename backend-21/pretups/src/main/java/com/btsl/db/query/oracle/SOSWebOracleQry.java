package com.btsl.db.query.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;
import com.web.pretups.sos.businesslogic.SOSWebQry;

public class SOSWebOracleQry implements SOSWebQry {
	private Log _log = LogFactory.getLog(this.getClass().getName());
	private String className = "SOSWebOracleQry";
	
	@Override
	public PreparedStatement loadSOSReconciliationListQry(Connection p_con,Date p_fromDate, Date p_toDate, String p_networkCode) throws SQLException {
		PreparedStatement pstmtSelect = null;
		final String methodName = className+"#loadSOSReconciliationList";
		StringBuilder selectQueryBuff = new StringBuilder();
		selectQueryBuff.append("SELECT STD.transaction_id, STD.subscriber_msisdn, STD.recharge_date, ");
        selectQueryBuff.append("STD.recharge_date_time, STD.sos_recharge_amount, STD.sos_credit_amount, ");
        selectQueryBuff.append("STD.sos_debit_amount, STD.sos_recharge_status, STD.settlement_status, ");
        selectQueryBuff.append("STD.error_status, STD.interface_response_code, STD.network_code, STD.product_code, ");
        selectQueryBuff.append("STD.request_gateway_type, STD.request_gateway_code, STD.service_type, ");
        selectQueryBuff.append("STD.subscriber_type, STD.reference_id, STD.created_on, STD.created_by, ");
        selectQueryBuff.append("STD.modified_on, STD.modified_by, STD.card_group_set_id, STD.version, STD.card_group_id, ");
        selectQueryBuff.append("STD.tax1_type, STD.tax1_rate, STD.tax1_value, STD.tax2_type, STD.tax2_rate, ");
        selectQueryBuff.append("STD.tax2_value, STD.PROCESS_FEE_type, STD.PROCESS_FEE_rate, STD.PROCESS_FEE_value, STD.card_group_code, ");
        selectQueryBuff.append("STD.sub_service, STD.start_time, STD.end_time, STD.reconciliation_flag, STD.reconciliation_date, ");
        selectQueryBuff.append("STD.reconciliation_by, STD.settlement_date, STD.settlement_flag, STD.settlement_recon_flag, ");
        selectQueryBuff.append("STD.settlement_recon_date, STD.settlement_recon_by, STD.type, STD.previous_balance, ");
        selectQueryBuff.append("STD.post_balance, STD.account_status, STD.service_class_code, PROD.short_name, KV.value, KV2.value rechargestatus ");
        selectQueryBuff.append("FROM sos_transaction_details STD, products PROD, key_values KV, key_values KV2 ");
        selectQueryBuff.append("WHERE STD.recharge_date >=? AND STD.recharge_date < ? ");
        selectQueryBuff.append("AND (STD.reconciliation_flag <> 'Y' OR STD.reconciliation_flag IS NULL ) ");
        selectQueryBuff.append("AND (STD.sos_recharge_status=? OR STD.sos_recharge_status=? ) ");
        selectQueryBuff.append("AND STD.network_code=? AND STD.product_code=PROD.product_code ");
        selectQueryBuff.append("AND KV.key(+)=STD.error_status AND KV.type(+)=? ");
        selectQueryBuff.append("AND KV2.key(+)=STD.sos_recharge_status AND KV2.type(+)=? ");
        selectQueryBuff.append("ORDER BY STD.recharge_date_time DESC ,STD.transaction_id ");

        final String selectQuery = selectQueryBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "select query:" + selectQuery);
        }
        pstmtSelect = p_con.prepareStatement(selectQuery);
        int i = 1;
        pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
        pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(p_toDate, 1)));
        pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
        pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
        pstmtSelect.setString(i++, p_networkCode);
        pstmtSelect.setString(i++, PretupsI.C2S_ERRCODE_VALUS);
        pstmtSelect.setString(i, PretupsI.C2S_ERRCODE_VALUS);
        return pstmtSelect;
		
	}
	
	@Override
	public String updateReconcilationStatusQry() {
		StringBuilder updateQuery = new StringBuilder();
		updateQuery.append("UPDATE sos_transaction_details SET sos_recharge_status=?, reconciliation_by=?, reconciliation_date=?, ");
        updateQuery.append("reconciliation_flag='Y', modified_by=?, modified_on=? ");
        updateQuery.append(", error_status = nvl(error_status," + PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS + ")");
        updateQuery.append("WHERE transaction_id=? AND (sos_recharge_status=? OR sos_recharge_status=?)");
		return updateQuery.toString();
		
	}
	
	@Override
	public PreparedStatement loadSettlementReconciliationListQry(Connection p_con,Date p_fromDate, Date p_toDate, String p_networkCode) throws SQLException {
		PreparedStatement pstmtSelect = null;
		final String methodName = className+"#loadSettlementReconciliationList";
		StringBuilder selectQueryBuff = new StringBuilder();
		
		 selectQueryBuff.append("SELECT STD.transaction_id, STD.subscriber_msisdn, STD.recharge_date, ");
	     selectQueryBuff.append("STD.recharge_date_time, STD.sos_recharge_amount, STD.sos_credit_amount, ");
	     selectQueryBuff.append("STD.sos_debit_amount, STD.sos_recharge_status, STD.settlement_status, ");
	     selectQueryBuff.append("STD.error_status, STD.interface_response_code, STD.network_code, STD.product_code, ");
	     selectQueryBuff.append("STD.request_gateway_type, STD.request_gateway_code, STD.service_type, ");
	     selectQueryBuff.append("STD.subscriber_type, STD.reference_id, STD.created_on, STD.created_by, ");
	     selectQueryBuff.append("STD.modified_on, STD.modified_by, STD.card_group_set_id, STD.version, STD.card_group_id, ");
	     selectQueryBuff.append("STD.tax1_type, STD.tax1_rate, STD.tax1_value, STD.tax2_type, STD.tax2_rate, ");
	     selectQueryBuff.append("STD.tax2_value, STD.PROCESS_FEE_type, STD.PROCESS_FEE_rate, STD.PROCESS_FEE_value, STD.card_group_code, ");
	     selectQueryBuff.append("STD.sub_service, STD.start_time, STD.end_time, STD.reconciliation_flag, STD.reconciliation_date, ");
	     selectQueryBuff.append("STD.reconciliation_by, STD.settlement_date, STD.settlement_flag, STD.settlement_recon_flag, ");
	     selectQueryBuff.append("STD.settlement_recon_date, STD.settlement_recon_by, STD.type, STD.previous_balance, ");
	     selectQueryBuff.append("STD.post_balance, STD.account_status, STD.service_class_code, PROD.short_name, KV.value, KV2.value settlementstatus ");
	     selectQueryBuff.append("FROM sos_transaction_details STD, products PROD, key_values KV, key_values KV2 ");
	     selectQueryBuff.append("WHERE STD.recharge_date >=? AND STD.recharge_date < =? ");
	     selectQueryBuff.append("AND (STD.settlement_recon_flag <> 'Y' OR STD.settlement_recon_flag IS NULL )");
	     selectQueryBuff.append("AND STD.sos_recharge_status = ? AND (STD.settlement_status=? OR STD.settlement_status=? ) ");
	     selectQueryBuff.append("AND STD.network_code=? AND STD.product_code=PROD.product_code ");
	     selectQueryBuff.append("AND KV.key(+)=STD.error_status AND KV.type(+)=? ");
	     selectQueryBuff.append("AND KV2.key(+)=STD.settlement_status AND KV2.type(+)=? ");
	     selectQueryBuff.append("ORDER BY STD.recharge_date_time DESC ,STD.transaction_id ");

	     final String selectQuery = selectQueryBuff.toString();
	     if (_log.isDebugEnabled()) {
	         _log.debug(methodName, "select query:" + selectQuery);
	     }
	     pstmtSelect = p_con.prepareStatement(selectQuery);
	     int i = 1;
	     pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
	     pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(p_toDate, 1)));
	     pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
	     pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
	     pstmtSelect.setString(i++, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
	     pstmtSelect.setString(i++, p_networkCode);
	     pstmtSelect.setString(i++, PretupsI.C2S_ERRCODE_VALUS);
	     pstmtSelect.setString(i, PretupsI.C2S_ERRCODE_VALUS);
        return pstmtSelect;
		
	}
	

	@Override
	public String updateSettlementReconcilationStatusQry() {
		StringBuilder updateQuery = new StringBuilder();
		 updateQuery.append("UPDATE sos_transaction_details SET settlement_status=?, settlement_recon_by=?, settlement_recon_date=?, ");
         updateQuery.append("settlement_recon_flag='Y', modified_by=?, modified_on=? ");
         updateQuery.append(", settlement_flag=?");
         updateQuery.append(", error_status = nvl(error_status," + PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS + ")");
         updateQuery.append("WHERE transaction_id=? AND (settlement_status=? OR settlement_status=?)");
		return updateQuery.toString();
		
	}
	
	@Override
	public PreparedStatement loadSOSTransferDetailsListQry(Connection p_con,Date p_fromDate, Date p_toDate, String p_networkCode,String p_msisdn, String p_transid) throws SQLException {
		PreparedStatement pstmtSelect = null;
		final String methodName = className+"#loadSOSTransferDetailsList";
		StringBuilder selectQueryBuff = new StringBuilder();
		
		selectQueryBuff.append("SELECT subscriber_msisdn  , transaction_id , recharge_date_time , KV1.value recharge_status,");
        selectQueryBuff.append("settlement_date ,  KV2.value settlement_status, sos_recharge_amount , network_code , subscriber_type ,");
        selectQueryBuff.append("previous_balance , post_balance , settlement_flag,sos_debit_amount, KV3.value error_message ");
        selectQueryBuff.append(",cell_id,switch_id ");
        selectQueryBuff.append("FROM sos_transaction_details STD, key_values KV1, key_values KV2, key_values KV3 ");
        selectQueryBuff.append("WHERE recharge_date >= ? AND recharge_date <= ? ");
        selectQueryBuff.append("AND  subscriber_msisdn = ?  AND network_code = ? ");
        selectQueryBuff.append("AND STD.SOS_RECHARGE_STATUS = KV1.KEY(+) AND KV1.type(+)=? AND STD.settlement_status=KV2.KEY(+) AND KV2.type(+)=? ");
        selectQueryBuff.append("AND STD.error_status=KV3.KEY(+) AND KV3.type(+)=? ");
        if (!(BTSLUtil.isNullString(p_transid))) {
            selectQueryBuff.append("AND transaction_id = ? ");
        }
        selectQueryBuff.append("ORDER BY recharge_date_time desc  ");

        final String selectQuery = selectQueryBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "select query:" + selectQuery);
        }
        pstmtSelect = p_con.prepareStatement(selectQuery);
        int i = 1;
        pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
        pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(p_toDate, 1)));
        pstmtSelect.setString(i++, p_msisdn);
        pstmtSelect.setString(i++, p_networkCode);
        pstmtSelect.setString(i++, PretupsI.P2P_ERRCODE_VALUS);
        pstmtSelect.setString(i++, PretupsI.P2P_ERRCODE_VALUS);
        pstmtSelect.setString(i++, PretupsI.P2P_ERRCODE_VALUS);
        if (!(BTSLUtil.isNullString(p_transid))) {
            pstmtSelect.setString(i, p_transid);
        }
        return pstmtSelect;
		
	}
	
	@Override
	public String lmbForcedSettlementSelectQry() {
		StringBuilder queryBuf = new StringBuilder();
		queryBuf.append(" SELECT transaction_id,settlement_flag,settlement_status FROM sos_transaction_details ");
        queryBuf.append(" WHERE subscriber_msisdn=? AND sos_recharge_status=? ");
        queryBuf.append(" AND recharge_date=?  AND ROWNUM<2 ORDER BY recharge_date_time DESC ");
		return queryBuf.toString();
		
	}
	
	@Override
	public String lmbForcedSettlementQry() {
		StringBuilder queryBuf = new StringBuilder();
		queryBuf.append(" SELECT transaction_id,settlement_flag,settlement_status FROM sos_transaction_details ");
        queryBuf.append(" WHERE subscriber_msisdn=? AND sos_recharge_status=? ");
        queryBuf.append(" AND ROWNUM<2 ORDER BY recharge_date_time DESC ");
		return queryBuf.toString();
		
	}
	
	
	@Override
	public String lmbBlkUploadQry() {
		StringBuilder queryBuf = new StringBuilder();
		queryBuf.append(" INSERT INTO SOS_SUBSCRIBER_SUMMARY(MSISDN, BONUS_AMOUNT,CREATED_ON, VALIDITY_EXPIRED) values (?,?,sysdate,?)");
		return queryBuf.toString();
	}
}

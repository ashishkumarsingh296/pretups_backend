package com.txn.pretups.sos.businesslogic;

public class SOSTxnPostgresQry implements SOSTxnQry{
	@Override
	public String loadSOSSettlementListQry(){
        StringBuilder queryBuf = new StringBuilder();
        queryBuf.append(" Select transaction_id,network_code,type,service_type,subscriber_msisdn,subscriber_type,");
        queryBuf.append(" sos_debit_amount,SOS_RECHARGE_AMOUNT,SOS_CREDIT_AMOUNT,");
        queryBuf.append(" request_gateway_type,request_gateway_code");
        queryBuf.append(" FROM sos_transaction_details STD  ");
        queryBuf.append(" WHERE date_trunc('day',STD.recharge_date::TIMESTAMP)<=? AND STD.settlement_flag='N' AND STD.SOS_RECHARGE_STATUS='200'"); // AND
                                                                                                                             // RECONCILIATION_FLAG='N'");
        queryBuf.append(" ORDER BY network_code");
		return queryBuf.toString();
	}
	@Override
	public String loadSOSReconSettlementListQry(){
		StringBuilder reconquery = new StringBuilder();
        reconquery.append(" Select transaction_id,network_code,type,service_type,subscriber_msisdn,subscriber_type,");
        reconquery.append(" sos_debit_amount,SOS_RECHARGE_AMOUNT,SOS_CREDIT_AMOUNT");
        reconquery.append(" FROM sos_transaction_details STD  ");
        reconquery.append(" WHERE STD.settlement_flag='N' AND STD.SOS_RECHARGE_STATUS='200' AND RECONCILIATION_FLAG='Y'");
        reconquery.append(" AND date_trunc('day',STD.reconciliation_date::TIMESTAMP)=? and ( date_trunc('day',RECONCILIATION_DATE::TIMESTAMP)-date_trunc('day',RECHARGE_DATE::TIMESTAMP))>= cast(? as interval ) ORDER BY network_code");
        return reconquery.toString();
	}
	@Override
	public String loadSOSDetailsQry(){
        StringBuffer queryBuf = new StringBuffer();
        queryBuf.append(" Select transaction_id,network_code,type,service_type,subscriber_msisdn,subscriber_type,");
        queryBuf.append(" sos_debit_amount,SOS_RECHARGE_AMOUNT,SOS_CREDIT_AMOUNT,");
        queryBuf.append(" request_gateway_type,request_gateway_code");
        queryBuf.append(" FROM sos_transaction_details STD  ");
        queryBuf.append(" WHERE subscriber_msisdn=? AND date_trunc('day',STD.recharge_date::TIMESTAMP)<=?  ");
        queryBuf.append(" AND STD.SOS_RECHARGE_STATUS='200' AND STD.settlement_flag='N' ");
        return queryBuf.toString();
	}
	@Override
	public String loadSOSValidityChkListQry(){
        StringBuilder queryBuf = new StringBuilder();
        queryBuf.append(" Select created_on,msisdn,bonus_amount,validity_expired");
        queryBuf.append(" FROM SOS_SUBSCRIBER_SUMMARY SCS");
        queryBuf.append(" WHERE date_trunc('day',SCS.created_on::TIMESTAMP)<=? AND SCS.validity_expired='N'");
        return queryBuf.toString();
	}
}

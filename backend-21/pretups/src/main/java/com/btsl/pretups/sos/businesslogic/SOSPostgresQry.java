package com.btsl.pretups.sos.businesslogic;

/**
 * @author satakshi.gaur
 *
 */
public class SOSPostgresQry implements SOSQry {
	@Override
	public String loadLastSOSRechargeByMsisdnQry(){
		StringBuilder selectQueryBuff = new StringBuilder();
        selectQueryBuff.append("SELECT transaction_id, recharge_date, recharge_date_time, sos_recharge_amount, ");
        selectQueryBuff.append("sos_recharge_status, settlement_status, settlement_flag, sos_credit_amount, error_status from( ");
        selectQueryBuff.append("select transaction_id, recharge_date, recharge_date_time, sos_recharge_amount, ");
        selectQueryBuff.append("sos_recharge_status, settlement_status, settlement_flag, sos_credit_amount, error_status ");
        selectQueryBuff.append("FROM sos_transaction_details WHERE subscriber_msisdn=? ");
        selectQueryBuff.append("and sos_recharge_status in(?,?,?) AND network_code=? ");
        selectQueryBuff.append("ORDER BY recharge_date_time DESC ) AS SOS_ALIAS  limit 1  ");

		return selectQueryBuff.toString();
	}
	
	@Override
	public	String loadSOSDetailsQry(){
		StringBuilder queryBuf= new StringBuilder();		  
		queryBuf.append(" Select transaction_id,network_code,type,service_type,subscriber_msisdn,subscriber_type,");
		queryBuf.append(" sos_debit_amount,SOS_RECHARGE_AMOUNT,SOS_CREDIT_AMOUNT,");	
		queryBuf.append(" request_gateway_type,request_gateway_code");
		queryBuf.append(" FROM sos_transaction_details STD  ");
		queryBuf.append(" WHERE subscriber_msisdn=? AND date_trunc('day',STD.recharge_date::TIMESTAMP)<=?  ");
		queryBuf.append(" AND STD.SOS_RECHARGE_STATUS='200' AND STD.settlement_flag='N' "); 
		return queryBuf.toString();
	}
}

package com.txn.pretups.sos.businesslogic;

public class SOSTxnOracleQry implements SOSTxnQry {
		@Override
		public String loadSOSSettlementListQry(){
	        StringBuilder queryBuf = new StringBuilder();
	        queryBuf.append(" Select transaction_id,network_code,type,service_type,subscriber_msisdn,subscriber_type,");
	        queryBuf.append(" sos_debit_amount,SOS_RECHARGE_AMOUNT,SOS_CREDIT_AMOUNT,");
	        queryBuf.append(" request_gateway_type,request_gateway_code");
	        queryBuf.append(" FROM sos_transaction_details STD  ");
	        queryBuf.append(" WHERE trunc(STD.recharge_date)<=? AND STD.settlement_flag='N' AND STD.SOS_RECHARGE_STATUS='200'"); // AND
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
	        reconquery.append(" AND trunc(STD.reconciliation_date)=? and (trunc(RECONCILIATION_DATE)-trunc(RECHARGE_DATE))>=? ORDER BY network_code");
	        return reconquery.toString();
		}
		@Override
		public String loadSOSDetailsQry(){
	        StringBuffer queryBuf = new StringBuffer();
	        queryBuf.append(" Select transaction_id,network_code,type,service_type,subscriber_msisdn,subscriber_type,");
	        queryBuf.append(" sos_debit_amount,SOS_RECHARGE_AMOUNT,SOS_CREDIT_AMOUNT,");
	        queryBuf.append(" request_gateway_type,request_gateway_code");
	        queryBuf.append(" FROM sos_transaction_details STD  ");
	        queryBuf.append(" WHERE subscriber_msisdn=? AND trunc(STD.recharge_date)<=?  ");
	        queryBuf.append(" AND STD.SOS_RECHARGE_STATUS='200' AND STD.settlement_flag='N' ");
	        return queryBuf.toString();
		}
		@Override
		public String loadSOSValidityChkListQry(){
	        StringBuilder queryBuf = new StringBuilder();
	        queryBuf.append(" Select created_on,msisdn,bonus_amount,validity_expired");
	        queryBuf.append(" FROM SOS_SUBSCRIBER_SUMMARY SCS");
	        queryBuf.append(" WHERE trunc(SCS.created_on)<=? AND SCS.validity_expired='N'");
	        return queryBuf.toString();
		}
}

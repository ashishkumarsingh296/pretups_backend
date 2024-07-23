package com.btsl.db.query.oracle;

import com.btsl.cp2p.registration.businesslogic.CP2PRegistrationQry;
import com.btsl.logging.LogFactory;

public class CP2PRegistrationOracleQry implements CP2PRegistrationQry{
	
	private String className="CP2PRegistrationOracleQry";

	@Override
	public String loadCP2PSubscriberDetails(String servicetype) {
		String methodName=className+"#loadCP2PSubscriberDetails";
		 StringBuilder sqlBuffer = new StringBuilder(" SELECT p2ps.ACTIVATED_ON, p2ps.BILLING_CYCLE_DATE, p2ps.BILLING_TYPE, p2ps.BUDDY_SEQ_NUMBER, p2ps.CONSECUTIVE_FAILURES, l.network_name,l.report_header_name,");
	        sqlBuffer.append(" p2ps.COUNTRY, p2ps.CREATED_BY, p2ps.CREATED_ON, p2ps.CREDIT_LIMIT,p2ps.pswd_reset, ");
	        sqlBuffer.append(" p2ps.FIRST_INVALID_PIN_TIME, p2ps.INVALID_PASSWORD_COUNT, p2ps.LANGUAGE, p2ps.LAST_LOGIN_ON, p2ps.LAST_SUCCESS_TRANSFER_DATE, ");
	        sqlBuffer.append(" p2ps.LAST_TRANSFER_AMOUNT, p2ps.LAST_TRANSFER_ID, p2ps.LAST_TRANSFER_MSISDN, p2ps.LAST_TRANSFER_ON, p2ps.LAST_TRANSFER_STATUS, ");
	        sqlBuffer.append(" p2ps.LAST_TRANSFER_TYPE, p2ps.LOGIN_ID, p2ps.MODIFIED_BY, p2ps.MODIFIED_ON,  ");
	        sqlBuffer.append(" p2ps.MSISDN, p2ps.NETWORK_CODE, p2ps.PASSWORD, p2ps.PIN, p2ps.PIN_BLOCK_COUNT, p2ps.PIN_MODIFIED_ON, p2ps.PREFIX_ID, l.status networkstatus,l.language_1_message,l.language_2_message,  ");
	        if(servicetype!=null){
	        	sqlBuffer.append("  p2psc.DAILY_TRANSFER_AMOUNT, p2psc.DAILY_TRANSFER_COUNT, ");
	        	sqlBuffer.append(" p2psc.PREV_DAILY_TRANSFER_AMOUNT, p2psc.PREV_DAILY_TRANSFER_COUNT, p2psc.PREV_MONTHLY_TRANSFER_AMOUNT,");
	            sqlBuffer.append(" p2psc.PREV_MONTHLY_TRANSFER_COUNT, p2psc.PREV_TRANSFER_DATE, p2psc.PREV_TRANSFER_MONTH_DATE, p2psc.PREV_TRANSFER_WEEK_DATE, ");
	            sqlBuffer.append("p2psc.PREV_WEEKLY_TRANSFER_AMOUNT, p2psc.PREV_WEEKLY_TRANSFER_COUNT,p2psc.WEEKLY_TRANSFER_AMOUNT, p2psc.WEEKLY_TRANSFER_COUNT, ");	
	            sqlBuffer.append("  p2psc.MONTHLY_TRANSFER_AMOUNT, p2psc.MONTHLY_TRANSFER_COUNT, ");
	        }
	        sqlBuffer.append("  p2ps.PSWD_MODIFIED_ON, p2ps.REGISTERED_ON, p2ps.REQUEST_STATUS,");
	       sqlBuffer.append(" p2ps.SERVICE_CLASS_CODE, p2ps.SERVICE_CLASS_ID, p2ps.SKEY_REQUIRED, p2ps.STATUS, p2ps.SUBSCRIBER_TYPE, p2ps.TOTAL_TRANSFER_AMOUNT, ");
	        sqlBuffer.append(" p2ps.TOTAL_TRANSFERS, p2ps.USER_ID, p2ps.USER_NAME,p2ps.PASSWORD_COUNT_UPDATED_ON");
	        sqlBuffer.append(" FROM  P2P_SUBSCRIBERS p2ps, NETWORKS l");
	        if(servicetype!=null){
	        	sqlBuffer.append(" ,P2P_SUBSCRIBERS_COUNTERS p2psc");
	        }
	        sqlBuffer.append(" WHERE  p2ps.msisdn=? ");
	        if(servicetype!=null){
	        	sqlBuffer.append(" AND p2ps.msisdn=p2psc.msisdn(+) AND p2psc.service_type=?");
	        }
	        sqlBuffer.append(" AND p2ps.status <> ? AND p2ps.status <> ? AND p2ps.network_code=L.network_code(+) ");
	        LogFactory.printLog(methodName, QUERY+sqlBuffer.toString(), LOG);
	        return sqlBuffer.toString();
	}

	@Override
	public String loadCP2PSubscriberDetails1(String servicetype) {
		String mmethodName = className+"loadCP2PSubscriberDetails1";
		StringBuilder sqlBuffer = new StringBuilder(" SELECT p2ps.ACTIVATED_ON, p2ps.BILLING_CYCLE_DATE, p2ps.BILLING_TYPE, p2ps.BUDDY_SEQ_NUMBER, p2ps.CONSECUTIVE_FAILURES, l.network_name,l.report_header_name,");
        sqlBuffer.append(" p2ps.COUNTRY, p2ps.CREATED_BY, p2ps.CREATED_ON, p2ps.CREDIT_LIMIT, p2ps.pswd_reset, ");
        sqlBuffer.append(" p2ps.FIRST_INVALID_PIN_TIME, p2ps.INVALID_PASSWORD_COUNT, p2ps.LANGUAGE, p2ps.LAST_LOGIN_ON, p2ps.LAST_SUCCESS_TRANSFER_DATE, ");
        sqlBuffer.append(" p2ps.LAST_TRANSFER_AMOUNT, p2ps.LAST_TRANSFER_ID, p2ps.LAST_TRANSFER_MSISDN, p2ps.LAST_TRANSFER_ON, p2ps.LAST_TRANSFER_STATUS, ");
        if(servicetype!=null){
        	sqlBuffer.append("  p2psc.DAILY_TRANSFER_AMOUNT, p2psc.DAILY_TRANSFER_COUNT, ");
        	sqlBuffer.append(" p2psc.PREV_DAILY_TRANSFER_AMOUNT, p2psc.PREV_DAILY_TRANSFER_COUNT, p2psc.PREV_MONTHLY_TRANSFER_AMOUNT,");
            sqlBuffer.append(" p2psc.PREV_MONTHLY_TRANSFER_COUNT, p2psc.PREV_TRANSFER_DATE, p2psc.PREV_TRANSFER_MONTH_DATE, p2psc.PREV_TRANSFER_WEEK_DATE, ");
            sqlBuffer.append("p2psc.PREV_WEEKLY_TRANSFER_AMOUNT, p2psc.PREV_WEEKLY_TRANSFER_COUNT,p2psc.WEEKLY_TRANSFER_AMOUNT, p2psc.WEEKLY_TRANSFER_COUNT, ");	
            sqlBuffer.append("  p2psc.MONTHLY_TRANSFER_AMOUNT, p2psc.MONTHLY_TRANSFER_COUNT, ");
        }
        sqlBuffer.append(" p2ps.LAST_TRANSFER_TYPE, p2ps.LOGIN_ID, p2ps.MODIFIED_BY, p2ps.MODIFIED_ON, ");
        sqlBuffer.append(" p2ps.MSISDN, p2ps.NETWORK_CODE, p2ps.PASSWORD, p2ps.PIN, p2ps.PIN_BLOCK_COUNT, p2ps.PIN_MODIFIED_ON, p2ps.PREFIX_ID, l.status networkstatus,l.language_1_message,l.language_2_message,  ");
        
        sqlBuffer.append("  p2ps.PSWD_MODIFIED_ON, p2ps.REGISTERED_ON, p2ps.REQUEST_STATUS,");
        sqlBuffer.append(" p2ps.SERVICE_CLASS_CODE, p2ps.SERVICE_CLASS_ID, p2ps.SKEY_REQUIRED, p2ps.STATUS, p2ps.SUBSCRIBER_TYPE, p2ps.TOTAL_TRANSFER_AMOUNT, ");
        sqlBuffer.append(" p2ps.TOTAL_TRANSFERS, p2ps.USER_ID, p2ps.USER_NAME,p2ps.PASSWORD_COUNT_UPDATED_ON");
        sqlBuffer.append(" FROM  P2P_SUBSCRIBERS p2ps, NETWORKS l");
        if(servicetype!=null){
        	sqlBuffer.append(" ,P2P_SUBSCRIBERS_COUNTERS p2psc");
        }
        sqlBuffer.append(" WHERE ( p2ps.msisdn=? or p2ps.login_id=? ) ");
        if(servicetype!=null){
        	sqlBuffer.append(" AND p2ps.msisdn=p2psc.msisdn(+) AND p2psc.service_type=?");
        }
        sqlBuffer.append(" AND p2ps.status <> ? AND p2ps.status <> ? AND p2ps.network_code=L.network_code(+) ");
        LogFactory.printLog(mmethodName, QUERY+sqlBuffer.toString(), LOG);
        return sqlBuffer.toString();
	}

}

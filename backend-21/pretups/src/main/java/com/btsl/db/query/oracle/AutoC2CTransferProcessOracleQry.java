package com.btsl.db.query.oracle;

import com.btsl.pretups.processes.AutoC2CTransferProcessQry;

public class AutoC2CTransferProcessOracleQry implements AutoC2CTransferProcessQry {

	@Override
	public String balanceAlertUsersQry() {
		 final StringBuilder queryBuf = new StringBuilder(
	                "select distinct UTC.user_id, U.msisdn, U.parent_id,U.network_code,U.category_code,CU.transfer_profile_id,UB.balance,UTC.product_code  from ");
	            queryBuf.append("user_threshold_counter UTC, users U, channel_users CU, user_balances UB,control_preferences CP,control_preferences CP2 ,(select USER_ID user_id1,product_code product_code1,Max(ENTRY_DATE_TIME) ENTRY_DATE_TIME1  ");
	            queryBuf.append("from user_threshold_counter where RECORD_TYPE='BT' group by user_id,product_code  order by user_id) X ");
	            queryBuf.append("where UTC.user_id=U.user_id AND UTC.RECORD_TYPE=? AND CU.user_id=U.user_id AND UB.user_id=U.user_id AND U.status <> 'N'  ");
	            queryBuf.append("AND U.parent_id <> ? AND U.CATEGORY_CODE=CP.CONTROL_CODE(+) AND U.NETWORK_CODE=CP.NETWORK_CODE(+) AND CP.PREFERENCE_CODE='AUTO_C2C_TRANSFER_AMT'  ");
	            queryBuf.append("AND cp2.preference_code = 'AUTO_C2C_SOS_CAT_ALLOWED' AND cp.control_code = cp2.control_code AND cp2.VALUE = 'true' ");
	            queryBuf.append("and CP.VALUE>0  AND UB.BALANCE>=UTC.CURRENT_BALANCE AND UTC.ENTRY_DATE_TIME =X.ENTRY_DATE_TIME1 and  UTC.user_id=X.USER_ID1 and  UTC.product_code = X.product_code1 and ub.product_code =utc.product_code");

		return queryBuf.toString();
	}

}

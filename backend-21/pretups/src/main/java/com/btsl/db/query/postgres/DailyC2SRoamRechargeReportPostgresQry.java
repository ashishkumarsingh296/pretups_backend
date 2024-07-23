package com.btsl.db.query.postgres;

import com.btsl.pretups.processes.DailyC2SRoamRechargeReportQry;

/**
 * DailyC2SRoamRechargeReportPostgresQry
 * @author sadhan.k
 *
 */
public class DailyC2SRoamRechargeReportPostgresQry implements DailyC2SRoamRechargeReportQry{

	@Override
	public String loadC2SRoamRechargeDetails() {
		
		StringBuilder strBuff = new StringBuilder("SELECT DCT.USER_ID,DCT.TRANSACTION_AMOUNT,DCT.ROAM_AMOUNT,DCT.PENALTY,DCT.OWNER_PENALTY, DCT.SERVICE_TYPE,");
        strBuff.append("USR.USER_NAME,USR.NETWORK_CODE,USR.CATEGORY_CODE,USR.OWNER_ID,USR1.USER_NAME OWNER_NAME, DCT.PENALTY_COUNT");
        strBuff.append(",USP.MSISDN,USP1.MSISDN OWNER_MSISDN,USR1.NETWORK_CODE OWNER_NW,USR1.CATEGORY_CODE OWNER_CATEGORY,DCT.TRANS_DATE,DCT.Differential_count ");
        strBuff.append(" FROM  DAILY_C2S_TRANS_DETAILS DCT,USERS USR, USERS USR1, USER_PHONES USP, USER_PHONES USP1 WHERE ");
        strBuff.append("DCT.USER_ID=USR.USER_ID AND USR.OWNER_ID=USR1.USER_ID AND USP.USER_ID=USR.USER_ID AND USR1.USER_ID=USP1.USER_ID ");
        strBuff.append("AND DCT.ROAM_AMOUNT >0 AND   DCT.TRANS_DATE >=date_trunc('day',?::timestamp) AND dct.TRANS_DATE <=date_trunc('day',?::timestamp) and USR.NETWORK_CODE=? and dct.service_type in ('RC','RRC','RCREV','RRCREV')");
 
	return strBuff.toString();
	}

}

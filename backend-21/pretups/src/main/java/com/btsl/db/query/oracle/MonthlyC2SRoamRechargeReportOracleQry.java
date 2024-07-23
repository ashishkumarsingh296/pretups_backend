package com.btsl.db.query.oracle;

import com.btsl.pretups.processes.MonthlyC2SRoamRechargeReportQry;

public class MonthlyC2SRoamRechargeReportOracleQry implements MonthlyC2SRoamRechargeReportQry {

	@Override
	public String loadC2SRoamRechargeDetails() {

		  final StringBuilder strBuff = new StringBuilder("SELECT DCT.USER_ID,DCT.TRANSACTION_AMOUNT,DCT.ROAM_AMOUNT,DCT.PENALTY,DCT.OWNER_PENALTY,DCT.SERVICE_TYPE,");
	        strBuff.append("USR.USER_NAME,USR.NETWORK_CODE,USR.CATEGORY_CODE,USR.OWNER_ID,USR1.USER_NAME OWNER_NAME");
	        strBuff.append(",USP.MSISDN,USP1.MSISDN OWNER_MSISDN,USR1.NETWORK_CODE OWNER_NW,USR1.CATEGORY_CODE OWNER_CATEGORY,DCT.TRANS_DATE  ");
	        strBuff.append("FROM  MONTHLY_C2S_TRANS_DETAILS DCT,USERS USR, USERS USR1, USER_PHONES USP, USER_PHONES USP1 WHERE ");
	        strBuff.append("DCT.USER_ID=USR.USER_ID AND USR.OWNER_ID=USR1.USER_ID AND USP.USER_ID=USR.USER_ID AND USR1.USER_ID=USP1.USER_ID ");
	        strBuff.append("AND DCT.ROAM_AMOUNT >0 AND   DCT.TRANS_DATE >=trunc(?) AND dct.TRANS_DATE <=trunc(?) AND  USR.NETWORK_CODE=?  and dct.service_type in ('RC','RRC','RCREV','RRCREV')");
	     
		
		return strBuff.toString();
	}

}

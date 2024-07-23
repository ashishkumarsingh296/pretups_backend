package com.btsl.db.query.oracle;

import com.btsl.pretups.processes.KPIProcessQry;

public class KPIProcessOracleQry implements KPIProcessQry{

	@Override
	public String fetchKPIDataCountUserUsersQry() {
		 String  selectQuery = "SELECT COUNT(U.USER_ID) user_count FROM USERS U, CHANNEL_USERS CU WHERE U.USER_ID=CU.USER_ID AND STATUS='Y'";
          selectQuery += " AND TRUNC(CU.ACTIVATED_ON)>=? AND TRUNC(CU.ACTIVATED_ON)<=? AND U.network_code=?";
		return selectQuery;
	}

	@Override
	public String fetchKPIDataCountUserUsersFromP2PSubscriberQry() {
		return "SELECT COUNT(USER_ID)user_count FROM P2P_SUBSCRIBERS WHERE STATUS='Y' AND TRUNC(ACTIVATED_ON)>=? AND TRUNC(ACTIVATED_ON)<=? and NETWORK_CODE=?";
	}

	@Override
	public String fetchKPIDataAvgCommQry() {
		String selectQuery = "SELECT U.USER_NAME,U.LOGIN_ID,U.MSISDN,CTI.PRODUCT_CODE,AVG(CTI.COMMISSION_VALUE) avg_com ";
         selectQuery += " FROM CHANNEL_TRANSFERS CT,CHANNEL_TRANSFERS_ITEMS CTI,USERS U WHERE CT.TRANSFER_ID=CTI.TRANSFER_ID and CT.STATUS='CLOSE' ";
         selectQuery += " AND U.USER_ID=CT.TO_USER_ID AND U.STATUS='Y' AND TRUNC(CT.CLOSE_DATE)>=? AND TRUNC(CT.CLOSE_DATE)<=? and U.NETWORK_CODE=? ";
         selectQuery += " GROUP BY CTI.PRODUCT_CODE,U.USER_NAME,U.LOGIN_ID,U.MSISDN ORDER BY CTI.PRODUCT_CODE ";
		return selectQuery;
	}

	@Override
	public String fetchKPIDataSumCommQry() {
		String selectQuery = "SELECT domain_name,CTI.PRODUCT_CODE,SUM(CTI.COMMISSION_VALUE) sum_com FROM CHANNEL_TRANSFERS CT,CHANNEL_TRANSFERS_ITEMS CTI,domains D ";
         selectQuery += " WHERE CT.TRANSFER_ID=CTI.TRANSFER_ID and CT.STATUS='CLOSE' AND D.domain_code=ct.DOMAIN_CODE AND TRUNC(CT.CLOSE_DATE)>=? AND TRUNC(CT.CLOSE_DATE)<=? ";
         selectQuery += " and CT.NETWORK_CODE=? GROUP BY CTI.PRODUCT_CODE,domain_name ORDER BY CTI.PRODUCT_CODE ";
		return selectQuery;
	}

}

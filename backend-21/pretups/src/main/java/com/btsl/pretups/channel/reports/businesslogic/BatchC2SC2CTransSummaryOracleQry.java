package com.btsl.pretups.channel.reports.businesslogic;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;

public class BatchC2SC2CTransSummaryOracleQry implements BatchC2SC2CTransSummaryQry{
	
	private Log log = LogFactory.getLog(this.getClass());

	@Override
	public String loadUserC2CDataByMsisdnOrLoginIdQry(String accessType,
			String[] geographicalCodes) {
		
		   final StringBuilder strBuff = new StringBuilder();
		  strBuff.append("SELECT U.user_name user_name, UP.msisdn  msisdn, GD.grph_domain_name, ");
	        strBuff.append("C.category_name, P.product_name,LK.lookup_name  transfer_sub_type, ");
	        strBuff.append("SUM(DCTD.trans_in_amount)  in_amount,SUM(DCTD.trans_in_count)  in_count,");
	        strBuff.append("SUM(DCTD.trans_out_amount) out_amount,SUM(DCTD.trans_out_count) out_count ");
	        strBuff
	            .append("FROM (SELECT USR.user_id FROM users USR CONNECT BY PRIOR USR.user_id = USR.parent_id START WITH USR.user_id= ? )X, daily_chnl_trans_details DCTD, users U, categories C, products P, geographical_domains GD,");
	        strBuff.append("lookups LK, user_geographies UG, user_phones UP ");
	        strBuff.append("WHERE DCTD.trans_date >= ?");
	        strBuff.append("AND  DCTD.trans_date <= ? ");
	        if (PretupsI.LOOKUP_MSISDN.equals(accessType)) {
	            strBuff.append("AND U.msisdn = ? ");
	        } else {
	            strBuff.append(" AND U.login_id = ? ");
	        }
	        strBuff.append("AND U.user_id = X.user_id ");
	        strBuff.append("AND U.category_code = C.category_code ");
	        strBuff.append("AND U.status NOT IN ('N','W','C') ");
	        strBuff.append("AND UP.user_id(+) = U.user_id ");
	        strBuff.append("AND UG.user_id = U.user_id ");
	        strBuff.append("AND UP.primary_number(+)='Y' ");
	        strBuff.append("AND (DCTD.trans_in_count <> 0 OR DCTD.trans_in_amount <> 0 OR DCTD.trans_out_count <> 0 OR DCTD.trans_out_amount <> 0) ");
	        strBuff.append("AND DCTD.product_code = P.product_code ");
	        strBuff.append("AND DCTD.user_id = U.user_id ");
	        strBuff.append("AND DCTD.type = ? ");
	        strBuff.append("AND LK.lookup_type = ? ");
	        strBuff.append("AND LK.lookup_code = dctd.transfer_sub_type ");
	        strBuff.append("AND UG.grph_domain_code = GD.grph_domain_code ");
	        strBuff.append("AND UG.grph_domain_code IN ( ");
	        strBuff.append("SELECT GD1.grph_domain_code FROM ");
	        strBuff.append("geographical_domains GD1 WHERE GD1.status IN('Y','S') ");
	        strBuff.append("CONNECT BY PRIOR GD1.grph_domain_code = GD1.parent_grph_domain_code ");
	        strBuff.append("START WITH GD1.grph_domain_code IN (");
	        for (int i = 0; i < geographicalCodes.length; i++) {
	            strBuff.append(" ?");
	            if (i != geographicalCodes.length - 1) {
	                strBuff.append(",");
	            }
	        }
	        strBuff.append("))");
	        strBuff.append("GROUP BY U.user_name, UP.msisdn, GD.grph_domain_name, C.category_name, P.product_name, lk.lookup_name ");
	        LogFactory.printLog("loadUserC2CDataByMsisdnOrLoginIdQry", strBuff.toString(), log);
	        return strBuff.toString();
	}

	@Override
	public String loadUserC2SDataByMsisdnOrLoginIdQry(String accessType,
			String geographicalCode) {
		final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT U.user_name, UP.msisdn, GD.grph_domain_name, C.category_name , ST.name, SUM(DCTD.failure_count) failure_count,  ");
        strBuff.append("SUM(DCTD.transaction_count) success_transaction_count, SUM(DCTD.transaction_amount) success_transaction_amount ");
        strBuff
            .append("FROM (SELECT USR.user_id FROM users USR CONNECT BY PRIOR USR.user_id = USR.parent_id START WITH USR.user_id= ? )X, daily_c2s_trans_details DCTD, users U, categories C, user_geographies UG, geographical_domains GD, user_phones UP, service_type ST ");
        strBuff.append("WHERE DCTD.user_id = U.user_id ");
        strBuff.append("AND DCTD.trans_date>= ? ");
        strBuff.append("AND DCTD.trans_date<= ? ");
        if (PretupsI.LOOKUP_MSISDN.equals(accessType)) {
            strBuff.append("AND U.msisdn = ? ");
        } else {
            strBuff.append(" AND U.login_id = ? ");
        }
        strBuff.append("AND U.user_id = X.user_id ");
        strBuff.append("AND U.category_code = C.category_code ");
        strBuff.append("AND U.status NOT IN ('N','W','C') ");
        strBuff.append("AND U.user_id = UG.user_id ");
        strBuff.append("AND U.user_id = UP.user_id(+) ");
        strBuff.append("AND UP.primary_number(+)='Y' ");
        strBuff.append("AND DCTD.service_type = ST.service_type ");
        strBuff.append("AND UG.grph_domain_code = GD.grph_domain_code ");
        strBuff.append("AND UG.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains GD1 WHERE status IN('Y', 'S') ");
        strBuff.append("CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
        strBuff.append("START WITH grph_domain_code IN (" + geographicalCode + ") )");
        strBuff.append("GROUP BY U.user_name, UP.msisdn, GD.grph_domain_name, C.category_name, ST.name ");
        LogFactory.printLog("loadUserC2SDataByMsisdnOrLoginId", strBuff.toString(), log);
		return strBuff.toString();
	}

	@Override
	public String loadUserC2CDataByMsisdnOrLoginIdForOperatorQry(
			String accessType, String geographicalCodes) {
		final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT U.user_name user_name, UP.msisdn  msisdn, GD.grph_domain_name, ");
        strBuff.append("C.category_name, P.product_name,LK.lookup_name  transfer_sub_type, ");
        strBuff.append("SUM(DCTD.trans_in_amount)  in_amount,SUM(DCTD.trans_in_count)  in_count,");
        strBuff.append("SUM(DCTD.trans_out_amount) out_amount,SUM(DCTD.trans_out_count) out_count ");
        strBuff.append("FROM daily_chnl_trans_details DCTD, users U, categories C, products P, geographical_domains GD,");
        strBuff.append("lookups LK, user_geographies UG, user_phones UP ");
        strBuff.append("WHERE DCTD.trans_date >= ?");
        strBuff.append("AND  DCTD.trans_date <= ? ");
        if (PretupsI.LOOKUP_MSISDN.equals(accessType)) {
            strBuff.append("AND U.msisdn = ? ");
        } else {
            strBuff.append(" AND U.login_id = ? ");
        }
        strBuff.append("AND U.category_code = C.category_code ");
        strBuff.append("AND U.status NOT IN ('N','W','C') ");
        strBuff.append("AND UP.user_id(+) = U.user_id ");
        strBuff.append("AND UG.user_id = U.user_id ");
        strBuff.append("AND UP.primary_number(+)='Y' ");
        strBuff.append("AND (DCTD.trans_in_count <> 0 OR DCTD.trans_in_amount <> 0 OR DCTD.trans_out_count <> 0 OR DCTD.trans_out_amount <> 0) ");
        strBuff.append("AND DCTD.product_code = P.product_code ");
        strBuff.append("AND DCTD.user_id = U.user_id ");
        strBuff.append("AND DCTD.type = ? ");
        strBuff.append("AND LK.lookup_type = ? ");
        strBuff.append("AND LK.lookup_code = dctd.transfer_sub_type ");
        strBuff.append("AND UG.grph_domain_code = GD.grph_domain_code ");
        strBuff.append("AND UG.grph_domain_code IN ( ");
        strBuff.append("SELECT GD1.grph_domain_code FROM ");
        strBuff.append("geographical_domains GD1 WHERE GD1.status IN('Y','S') ");
        strBuff.append("CONNECT BY PRIOR GD1.grph_domain_code = GD1.parent_grph_domain_code ");
        strBuff.append("START WITH GD1.grph_domain_code IN (" + geographicalCodes + ") )");
        strBuff.append("GROUP BY U.user_name, UP.msisdn, GD.grph_domain_name, C.category_name, P.product_name, lk.lookup_name ");
        LogFactory.printLog("loadUserC2SDataByMsisdnOrLoginId", strBuff.toString(), log);
		return strBuff.toString();
	}

	@Override
	public String loadUserC2SDataByMsisdnOrLoginIdForOperatorQry(
			String accessType, String geographicalCodes) {
		  final StringBuilder strBuff = new StringBuilder();
	        strBuff.append("SELECT U.user_name, UP.msisdn, GD.grph_domain_name, C.category_name , ST.name, SUM(DCTD.failure_count) failure_count,  ");
	        strBuff.append("SUM(DCTD.transaction_count) success_transaction_count, SUM(DCTD.transaction_amount) success_transaction_amount ");
	        strBuff.append("FROM daily_c2s_trans_details DCTD, users U, categories C, user_geographies UG, geographical_domains GD, user_phones UP, service_type ST ");
	        strBuff.append("WHERE DCTD.user_id = U.user_id ");
	        strBuff.append("AND DCTD.trans_date>= ? ");
	        strBuff.append("AND DCTD.trans_date<= ? ");
	        if (PretupsI.LOOKUP_MSISDN.equals(accessType)) {
	            strBuff.append("AND U.msisdn = ? ");
	        } else {
	            strBuff.append(" AND U.login_id = ? ");
	        }
	        strBuff.append("AND U.category_code = C.category_code ");
	        strBuff.append("AND U.status NOT IN ('N','W','C') ");
	        strBuff.append("AND U.user_id = UG.user_id ");
	        strBuff.append("AND U.user_id = UP.user_id(+) ");
	        strBuff.append("AND UP.primary_number(+)='Y' ");
	        strBuff.append("AND DCTD.service_type = ST.service_type ");
	        strBuff.append("AND UG.grph_domain_code = GD.grph_domain_code ");
	        strBuff.append("AND UG.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains GD1 WHERE status IN('Y', 'S') ");
	        strBuff.append("CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
	        strBuff.append("START WITH grph_domain_code IN (" + geographicalCodes + ") )");
	        strBuff.append("GROUP BY U.user_name, UP.msisdn, GD.grph_domain_name, C.category_name, ST.name ");
	        LogFactory.printLog("loadUserC2SDataByMsisdnOrLoginIdForOperatorQry", strBuff.toString(), log);
		return strBuff.toString();
	}

}

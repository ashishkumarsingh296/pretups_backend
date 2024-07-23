package com.btsl.pretups.channel.reports.businesslogic;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;

public class BatchC2SC2CTransSummaryPostgresQry  implements BatchC2SC2CTransSummaryQry{
	
	private Log log = LogFactory.getLog(this.getClass());

	@Override
	public String loadUserC2CDataByMsisdnOrLoginIdQry(String accessType,
			String[] geographicalCodes) {
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT U.user_name user_name, UP.msisdn  msisdn, GD.grph_domain_name, ");
		strBuff.append("C.category_name, P.product_name,LK.lookup_name  transfer_sub_type, ");
		strBuff.append("SUM(DCTD.trans_in_amount)  in_amount,SUM(DCTD.trans_in_count)  in_count,");
		strBuff.append("SUM(DCTD.trans_out_amount) out_amount,SUM(DCTD.trans_out_count) out_count ");
		strBuff.append("FROM (");
		strBuff.append("WITH RECURSIVE q AS (");
		strBuff.append("SELECT USR.user_id FROM users USR");
		strBuff.append("where USR.user_id= ?");
		strBuff.append("union all");
		strBuff.append("SELECT USR1.user_id FROM users USR1");
		strBuff.append("join q on q.user_id = USR1.parent_id ");
		strBuff.append(") select  user_id FROM  q");
		strBuff.append(")X, daily_chnl_trans_details DCTD, categories C, products P, geographical_domains GD,");
		strBuff.append("lookups LK, user_geographies UG, user_phones UP right join  users U on  (UP.user_id = U.user_id AND UP.primary_number='Y' )");
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
		strBuff.append("AND UG.user_id = U.user_id ");
		strBuff.append("AND (DCTD.trans_in_count <> 0 OR DCTD.trans_in_amount <> 0 OR DCTD.trans_out_count <> 0 OR DCTD.trans_out_amount <> 0) ");
		strBuff.append("AND DCTD.product_code = P.product_code ");
		strBuff.append("AND DCTD.user_id = U.user_id ");
		strBuff.append("AND DCTD.type = ? ");
		strBuff.append("AND LK.lookup_type = ? ");
		strBuff.append("AND LK.lookup_code = dctd.transfer_sub_type ");
		strBuff.append("AND UG.grph_domain_code = GD.grph_domain_code ");
		strBuff.append("AND UG.grph_domain_code IN (");

		strBuff.append("WITH RECURSIVE q1 AS (");
		strBuff.append("SELECT GD1.grph_domain_code, gd1.status FROM ");
		strBuff.append("geographical_domains GD1 WHERE ");
		strBuff.append("GD1.grph_domain_code IN ( ");
		 for (int i = 0; i < geographicalCodes.length; i++) {
	            strBuff.append(" ?");
	            if (i != geographicalCodes.length - 1) {
	                strBuff.append(",");
	            }
	        }
		strBuff.append(")");
		strBuff.append("union all");
		strBuff.append("SELECT GD2.grph_domain_code , gd2.status FROM ");
		strBuff.append("geographical_domains GD2");
		strBuff.append("join q1 on q1.grph_domain_code = GD2.parent_grph_domain_code ");
		strBuff.append(")SELECT grph_domain_code FROM q1");
		strBuff.append("WHERE status IN('Y','S')");
		strBuff.append(")");
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
        strBuff.append("FROM (");
        strBuff.append(" WITH RECURSIVE q AS ( ");
        strBuff.append(" SELECT USR.user_id FROM users USR where USR.user_id= ? ");
        strBuff.append(" union all ");
        strBuff.append(" SELECT USR1.user_id FROM users USR1 ");
        strBuff.append(" join q on q.user_id = USR1.parent_id ");
        strBuff.append(") select  user_id FROM  q ");
        strBuff.append(")X,");
        strBuff.append(" daily_c2s_trans_details DCTD, users U left join user_phones UP on ( U.user_id = UP.user_id AND UP.primary_number='Y') , categories C, user_geographies UG, geographical_domains GD,  service_type ST ");
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
        strBuff.append("AND DCTD.service_type = ST.service_type ");
        strBuff.append("AND UG.grph_domain_code = GD.grph_domain_code ");
        strBuff.append("AND UG.grph_domain_code IN (" );
        strBuff.append(" WITH RECURSIVE q1 AS ( ");
        strBuff.append(" SELECT GD1.grph_domain_code, gd1.status FROM geographical_domains GD1 where ");
        strBuff.append(" GD1.grph_domain_code IN (" + geographicalCode + ")");
        strBuff.append("union all");
        strBuff.append("SELECT GD2.grph_domain_code , gd2.status FROM geographical_domains GD2");
        strBuff.append("join q1 on q1.grph_domain_code = GD2.parent_grph_domain_code");
        strBuff.append("where GD2.status IN('Y', 'S')");
        strBuff.append(") select  grph_domain_code FROM  q1 where status IN('Y', 'S') ");
        strBuff.append( ")");
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
        strBuff.append("FROM daily_chnl_trans_details DCTD, categories C, products P, geographical_domains GD,");
        strBuff.append("lookups LK, user_geographies UG, user_phones UP right join users U on (UP.user_id = U.user_id AND  UP.primary_number='Y' ) ");
        strBuff.append("WHERE DCTD.trans_date >= ?");
        strBuff.append("AND  DCTD.trans_date <= ? ");
        if (PretupsI.LOOKUP_MSISDN.equals(accessType)) {
            strBuff.append("AND U.msisdn = ? ");
        } else {
            strBuff.append(" AND U.login_id = ? ");
        }
        strBuff.append("AND U.category_code = C.category_code ");
        strBuff.append("AND U.status NOT IN ('N','W','C') ");
        strBuff.append("AND UG.user_id = U.user_id ");
        strBuff.append("AND (DCTD.trans_in_count <> 0 OR DCTD.trans_in_amount <> 0 OR DCTD.trans_out_count <> 0 OR DCTD.trans_out_amount <> 0) ");
        strBuff.append("AND DCTD.product_code = P.product_code ");
        strBuff.append("AND DCTD.user_id = U.user_id ");
        strBuff.append("AND DCTD.type = ? ");
        strBuff.append("AND LK.lookup_type = ? ");
        strBuff.append("AND LK.lookup_code = dctd.transfer_sub_type ");
        strBuff.append("AND UG.grph_domain_code = GD.grph_domain_code ");
        strBuff.append("AND UG.grph_domain_code IN ( ");
        strBuff.append(" WITH RECURSIVE q1 AS ( ");
        strBuff.append(" SELECT GD1.grph_domain_code, GD1.status FROM  geographical_domains GD1 WHERE  ");
        strBuff.append("  GD1.grph_domain_code IN (" + geographicalCodes + ")  ");
        strBuff.append(" union all");
        strBuff.append(" SELECT GD2.grph_domain_code, GD2.status FROM  geographical_domains GD2");
        strBuff.append(" join q1 on q1.grph_domain_code = GD2.parent_grph_domain_code");
        strBuff.append(" ) select  grph_domain_code FROM  q1 ");
        strBuff.append(" WHERE status IN('Y','S') ");

        strBuff.append(" )");
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
	        strBuff.append("FROM daily_c2s_trans_details DCTD, users U left join user_phones UP on (U.user_id = UP.user_id AND  UP.primary_number='Y'), categories C, user_geographies UG, geographical_domains GD, service_type ST ");
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
	        strBuff.append("AND DCTD.service_type = ST.service_type ");
	        strBuff.append("AND UG.grph_domain_code = GD.grph_domain_code ");
	        strBuff.append("AND UG.grph_domain_code IN (");	       
	        strBuff.append(" WITH RECURSIVE q1 AS ( ");
	        strBuff.append(" SELECT GD1.grph_domain_code,GD1.status FROM geographical_domains GD1 WHERE GD1.grph_domain_code IN (" + geographicalCodes + ")");
	        strBuff.append("union all ");
	        strBuff.append("SELECT GD2.grph_domain_code, GD2.status FROM geographical_domains GD2 ");
	        strBuff.append("join q1 on q1.grph_domain_code = GD2.parent_grph_domain_code");
	        strBuff.append(" ) select  grph_domain_code FROM  q1 ");
	        strBuff.append("WHERE status IN('Y', 'S')");

	        strBuff.append( ")" );
	        strBuff.append("GROUP BY U.user_name, UP.msisdn, GD.grph_domain_name, C.category_name, ST.name ");
	        LogFactory.printLog("loadUserC2SDataByMsisdnOrLoginIdForOperatorQry", strBuff.toString(), log);
		return strBuff.toString();
	}
}

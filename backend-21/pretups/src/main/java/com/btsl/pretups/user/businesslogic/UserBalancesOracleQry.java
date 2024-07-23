package com.btsl.pretups.user.businesslogic;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.Constants;

public class UserBalancesOracleQry implements UserBalancesQry {
	private Log log = LogFactory.getLog(this.getClass());

	@Override
	public String selectForUserbalancesQry() {
		final StringBuilder strBuffSelect = new StringBuilder();
		strBuffSelect.append(" SELECT balance,balance_type FROM user_balances");
		if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
			strBuffSelect.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE OF balance with RS");
		} else {
			strBuffSelect.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE OF balance");
		}
		LogFactory.printLog("selectForUserbalancesQry", strBuffSelect.toString(), log);
		return strBuffSelect.toString();
	}

	@Override
	public String updateUserDailyBalancesQry() {
		final StringBuilder selectStrBuff = new StringBuilder();
		selectStrBuff.append("SELECT user_id, network_code, network_code_for, product_code, balance, prev_balance, ");
		selectStrBuff.append("last_transfer_type, last_transfer_no, last_transfer_on, daily_balance_updated_on,balance_type ");
		selectStrBuff.append("FROM user_balances ");
		if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
			selectStrBuff.append("WHERE user_id = ? AND TRUNC(daily_balance_updated_on)<> TRUNC(?) FOR UPDATE OF balance WITH RS ");
		} else {
			selectStrBuff.append("WHERE user_id = ? AND TRUNC(daily_balance_updated_on)<> TRUNC(?) FOR UPDATE OF balance ");
		}
		LogFactory.printLog("updateUserDailyBalancesQry", selectStrBuff.toString(), log);
		return selectStrBuff.toString();
	}

	@Override
	public String creditUserBalanceForBonusAccQry() {
		final StringBuilder strBuffSelect = new StringBuilder();
		strBuffSelect.append(" SELECT balance,balance_type FROM user_balances");
		if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
			strBuffSelect.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? AND balance_type =? FOR UPDATE OF balance with RS");
		} else {
			strBuffSelect.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ?  AND balance_type =? FOR UPDATE OF balance");
		}
		LogFactory.printLog("creditUserBalanceForBonusAccQry", strBuffSelect.toString(), log);
		return strBuffSelect.toString();
	}

	@Override
	public String loadUserBalanceForProductAndWalletsRSQry(String walletTypeCondition) {
		final StringBuilder selectQueryBuff = new StringBuilder(" SELECT balance, balance_type, prev_balance ");
		selectQueryBuff.append(" FROM user_balances ");
		selectQueryBuff.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? ");
		selectQueryBuff.append(walletTypeCondition);
		if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
			selectQueryBuff.append(" FOR UPDATE OF balance WITH RS ");
		} else {
			selectQueryBuff.append(" FOR UPDATE OF balance ");
		}
		LogFactory.printLog("loadUserBalanceForProductAndWalletsRSQry", selectQueryBuff.toString(), log);
		return selectQueryBuff.toString();
	}

	@Override
	public String updateUserDailyBalancesForWalletsSelectForUpdateQry() {
		String forUpdateClause = "";
		if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
			forUpdateClause = " AND TRUNC(daily_balance_updated_on)<> TRUNC(?) FOR UPDATE OF balance WITH RS  ";
		} else {
			forUpdateClause = " AND TRUNC(daily_balance_updated_on)<> TRUNC(?) FOR UPDATE OF balance  ";
		}
		return forUpdateClause;
	}

	@Override
	public String updateUserDailyBalancesForMultipleProductAndWalletQry() {
		final StringBuilder selectStrBuff = new StringBuilder();
		selectStrBuff.append("SELECT user_id, network_code, network_code_for, product_code, balance, prev_balance, ");
		selectStrBuff.append("last_transfer_type, last_transfer_no, last_transfer_on, daily_balance_updated_on,balance_type ");
		selectStrBuff.append("FROM user_balances ");
		if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
			selectStrBuff.append("WHERE user_id = ? AND product_code=? ");
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
				selectStrBuff.append(" AND  balance_type=? ");
			}
			selectStrBuff.append(" AND TRUNC(daily_balance_updated_on)<> TRUNC(?) FOR UPDATE OF balance WITH RS ");
		} else {
			selectStrBuff.append("WHERE user_id = ? AND product_code=? ");
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
				selectStrBuff.append(" AND  balance_type=? ");
			}
			selectStrBuff.append(" AND TRUNC(daily_balance_updated_on)<> TRUNC(?) FOR UPDATE OF balance ");
		}
		LogFactory.printLog("updateUserDailyBalancesForMultipleProductAndWallet", selectStrBuff.toString(), log);
		return selectStrBuff.toString();
	}

	@Override
	public String diffCreditAndDebitUserBalancesQry() {
		StringBuilder strBuffSelect = new StringBuilder();
		strBuffSelect.append(" SELECT balance "); 
		strBuffSelect.append(" FROM user_balances ");
		strBuffSelect.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ?   FOR UPDATE OF balance ");
		LogFactory.printLog("diffCreditAndDebitUserBalancesQry", strBuffSelect.toString(), log);
		return strBuffSelect.toString();
	}
	
	@Override
	public String userBalanceCSVReportCurrentDate() {
		// TODO Auto-generated method stub
		StringBuilder sqlBuff = new StringBuilder("SELECT   x1.user_id, x1.user_name user_name, x1.msisdn msisdn, x1.user_category, x1.user_code,x1.user_geography, x1.product_name product_name, x1.parent_name, ");
		sqlBuff.append("x1.parent_msisdn,x1.owner_name, x1.owner_msisdn, x1.stat stat, x1.user_balance/").append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(" user_balance, ");
		sqlBuff.append("(agent_balance - x1.user_balance)/").append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(" agent_balance from ( SELECT UB.user_id, U.user_name, ");
		sqlBuff.append("U.msisdn, C.category_name user_category, U.user_code, (CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PU.user_name END) parent_name, ");
		sqlBuff.append("(CASE U.parent_id WHEN 'ROOT' THEN '' ELSE PU.msisdn END) parent_msisdn, (CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OU.user_name END) owner_name, ");
		sqlBuff.append("(CASE  WHEN U.parent_id = 'ROOT' THEN ''  WHEN U.user_id = OU.user_id THEN '' ELSE OU.msisdn END) owner_msisdn, LOOK.lookup_name stat, GD.grph_domain_name user_geography, ");
		sqlBuff.append("P.product_name, UB.balance user_balance FROM user_balances UB, users U, categories C, users OU, users PU,user_geographies UG, geographical_domains GD, LOOKUPS LOOK, products P ");
		sqlBuff.append("WHERE UB.user_id=U.user_id AND U.category_code=C.category_code AND PU.user_id = (case U.parent_id when 'ROOT' then U.user_id else U.parent_id end) AND U.owner_id=OU.user_id ");
		sqlBuff.append("AND UB.product_code= P.product_code AND U.user_id = UG.user_id AND UG.grph_domain_code = GD.grph_domain_code AND LOOK.lookup_code = U.status AND LOOK.lookup_type = 'URTYP' ");
		sqlBuff.append("AND U.network_code= ? AND UG.grph_domain_code IN ( SELECT GD1.grph_domain_code FROM geographical_domains GD1 WHERE GD1.status IN ('Y', 'S') ");
		sqlBuff.append("CONNECT BY PRIOR GD1.grph_domain_code = GD1.parent_grph_domain_code START WITH GD1.grph_domain_code IN (SELECT UG1.grph_domain_code FROM user_geographies UG1 ");
		sqlBuff.append("WHERE ug1.GRPH_DOMAIN_CODE=case ? when 'ALL' then UG1.grph_domain_code else ? end AND UG1.user_id=? ) ) AND C.domain_code = ? ");
		sqlBuff.append("AND C.category_code = case ? when 'ALL' then C.category_code else ? end AND U.user_id = case ? when 'ALL' then U.user_id else ? end)X1, (select u_id ,sum(balance) agent_balance ");
		sqlBuff.append("from ( select CONNECT_BY_ROOT u_id u_id,balance,level -1 ABD from ( select u.USER_ID u_id,balance,parent_id From    users u ,user_balances v where ");
		sqlBuff.append("u.USER_ID=v.user_id ) k CONNECT BY PRIOR k.u_id = parent_id) group by u_id ) y where x1.user_id=y.u_id ");
		LogFactory.printLog("userBalanceCSVReportCurrentDate", sqlBuff.toString(), log);
		return sqlBuff.toString();
	}

	@Override
	public String userBalanceCSVReportPreviousDate() {
		// TODO Auto-generated method stub
		StringBuilder sqlBuff = new StringBuilder("SELECT x1.user_id, x1.user_name, x1.msisdn, x1.user_category, x1.user_code,x1.user_geography, x1.product_name, x1.parent_name, x1.parent_msisdn,x1.owner_name, x1.owner_msisdn, x1.stat, x1.user_balance/").append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(" user_balance,  ");
        sqlBuff.append(" ((agent_balance - x1.user_balance)/").append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(") agent_balance from ( SELECT UB.user_id, U.user_name, U.msisdn, C.category_name user_category, U.user_code, (CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PU.user_name END) parent_name, (CASE U.parent_id WHEN 'ROOT' THEN ");
        sqlBuff.append(" '' ELSE PU.msisdn END) parent_msisdn, (CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OU.user_name END) owner_name, (CASE  WHEN U.parent_id = 'ROOT' THEN ''  WHEN U.user_id = OU.user_id THEN '' ELSE OU.msisdn END) owner_msisdn, LOOK.lookup_name stat, GD.grph_domain_name user_geography, P.product_name, UB.balance user_balance ");
        sqlBuff.append("FROM USER_DAILY_BALANCES UB, USERS U, CATEGORIES C, USERS OU, USERS PU,USER_GEOGRAPHIES UG, GEOGRAPHICAL_DOMAINS GD, LOOKUPS LOOK, PRODUCTS P WHERE UB.user_id=U.user_id ");
        sqlBuff.append("   AND U.category_code=C.category_code AND PU.user_id = (CASE U.parent_id WHEN 'ROOT' THEN U.user_id ELSE U.parent_id END) AND U.owner_id=OU.user_id ");
        sqlBuff.append(" AND UB.product_code= P.product_code  AND U.user_id = UG.user_id AND UG.grph_domain_code = GD.grph_domain_code ");
        sqlBuff.append("AND LOOK.lookup_code = U.status AND LOOK.lookup_type = 'URTYP' AND U.network_code=? AND UG.grph_domain_code IN ( ");
        sqlBuff.append("SELECT GD1.grph_domain_code FROM GEOGRAPHICAL_DOMAINS GD1 WHERE GD1.status IN ('Y', 'S') CONNECT BY PRIOR GD1.grph_domain_code = GD1.parent_grph_domain_code ");
        sqlBuff.append("  START WITH GD1.grph_domain_code IN (SELECT UG1.grph_domain_code FROM USER_GEOGRAPHIES UG1 WHERE ug1.GRPH_DOMAIN_CODE=CASE ? WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END ");
        sqlBuff.append("AND UG1.user_id=? ) )   AND C.domain_code = ? AND C.category_code = CASE ? WHEN 'ALL' THEN C.category_code ELSE ? END ");
        sqlBuff.append(" AND U.user_id = CASE ? WHEN 'ALL' THEN U.user_id ELSE ? END  AND UB.balance_date=TO_DATE(?,'dd/mm/yy') )X1 ,  (select u_id ,sum(balance) agent_balance ");
        sqlBuff.append("from ( select CONNECT_BY_ROOT u_id  u_id,balance,level -1 ABD from  ( select u.USER_ID u_id,balance,parent_id  From    users u ,USER_DAILY_BALANCES v ");
        sqlBuff.append("where   u.USER_ID=v.user_id(+)   AND v.balance_date(+)=TO_DATE(?,'dd/mm/yy') ) k  CONNECT BY PRIOR k.u_id = parent_id  )");
        sqlBuff.append("group by u_id )  y where x1.user_id=y.u_id ");
        LogFactory.printLog("userBalanceCSVReportPreviousDate", sqlBuff.toString(), log);
		return sqlBuff.toString();
	}

}

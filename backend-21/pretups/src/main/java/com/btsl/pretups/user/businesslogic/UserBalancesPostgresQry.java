package com.btsl.pretups.user.businesslogic;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.Constants;

public class UserBalancesPostgresQry implements UserBalancesQry{
	private Log log = LogFactory.getLog(this.getClass());
	@Override
	public String selectForUserbalancesQry() {
		 final StringBuilder strBuffSelect = new StringBuilder();
	        strBuffSelect.append("SELECT balance , balance_type ");
	        strBuffSelect.append("FROM user_balances ");
	        strBuffSelect.append("WHERE ");
	        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
	            strBuffSelect.append("user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE OF balance WITH RS ");
	        } else {
	            strBuffSelect.append("user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE  ");
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
	            selectStrBuff.append("WHERE user_id = ? AND TRUNC(daily_balance_updated_on) <> TRUNC(?) FOR UPDATE OF balance WITH RS ");
	        } else {
	            selectStrBuff.append("WHERE user_id = ? AND date_trunc('day', daily_balance_updated_on::TIMESTAMP) <> date_trunc('day',?::TIMESTAMP) FOR UPDATE ");
	        }
	        LogFactory.printLog("updateUserDailyBalancesQry", selectStrBuff.toString(), log);
	        return selectStrBuff.toString();
	}
	@Override
	public String creditUserBalanceForBonusAccQry() {
		  final StringBuilder strBuffSelect = new StringBuilder();
	        strBuffSelect.append(" SELECT balance ");
	        strBuffSelect.append(" FROM user_balances ");
	        strBuffSelect.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? AND balance_type = ?");
	        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
	            strBuffSelect.append(" FOR UPDATE OF balance  WITH RS ");
	        } else {
	            strBuffSelect.append(" FOR UPDATE  ");
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
              selectQueryBuff.append(" FOR UPDATE ");
          }
          LogFactory.printLog("loadUserBalanceForProductAndWalletsRSQry", selectQueryBuff.toString(), log);
		return selectQueryBuff.toString();
	}
	
	@Override
	public String updateUserDailyBalancesForWalletsSelectForUpdateQry() {
		String forUpdateClause;
		if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
            forUpdateClause = " AND TRUNC(daily_balance_updated_on)<> TRUNC(?) FOR UPDATE OF balance WITH RS  ";
        } else {
            forUpdateClause = " AND date_trunc('day', daily_balance_updated_on::TIMESTAMP) <> date_trunc('day',?::TIMESTAMP) FOR UPDATE   ";
        }
		return forUpdateClause;
	}
	
	@Override
	public String updateUserDailyBalancesForMultipleProductAndWalletQry() {
		final StringBuilder selectStrBuff = new StringBuilder();
		selectStrBuff.append(" SELECT user_id, network_code, network_code_for, product_code, balance, prev_balance, ");
		selectStrBuff.append(" last_transfer_type, last_transfer_no, last_transfer_on, daily_balance_updated_on,balance_type ");
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
			selectStrBuff.append(" AND date_trunc('day', daily_balance_updated_on::TIMESTAMP)  <> date_trunc('day',?::TIMESTAMP) FOR UPDATE  ");
		}
		LogFactory.printLog("updateUserDailyBalancesForMultipleProductAndWallet", selectStrBuff.toString(), log);
		return selectStrBuff.toString();
	}
	
	@Override
	public String diffCreditAndDebitUserBalancesQry() {
		StringBuilder strBuffSelect = new StringBuilder();
		strBuffSelect.append(" SELECT balance "); 
		strBuffSelect.append(" FROM user_balances ");
		strBuffSelect.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ?   FOR UPDATE  ");
		LogFactory.printLog("diffCreditAndDebitUserBalancesQry", strBuffSelect.toString(), log);
		return strBuffSelect.toString();
	}
	
	@Override
	public String userBalanceCSVReportCurrentDate() {
		// TODO Auto-generated method stub
		StringBuilder sqlBuff = new StringBuilder("SELECT X1.user_id, X1.user_name, X1.msisdn, X1.user_category, X1.user_code, X1.user_geography,X1.product_name,X1.parent_name, X1.parent_msisdn, X1.owner_name, X1.owner_msisdn, X1.stat, X1.user_balance, coalesce(SUM(X1.agent_balance), 0)");
		sqlBuff.append("sqlBuff.append(agent_balance FROM(SELECT X.user_id, X.user_name, X.msisdn, X.user_category, X.user_code, X.user_geography,X.product_name,X.parent_name, X.parent_msisdn, X.owner_name, X.owner_msisdn, X.stat, X.user_balance ,UD.category_code,UD.parent_id,");
		sqlBuff.append("CD.category_type, CUB.balance agent_balance FROM (SELECT UB.user_id, U.user_name, U.msisdn, C.category_name user_category, U.user_code,(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PU.user_name END) parent_name, (CASE U.parent_id WHEN 'ROOT' THEN '' ELSE PU.msisdn END)");
		sqlBuff.append("parent_msisdn, (CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OU.user_name END) owner_name,(CASE  WHEN U.parent_id = 'ROOT' THEN ''  WHEN U.user_id = OU.user_id THEN '' ELSE OU.msisdn END) owner_msisdn, LOOK.lookup_name stat,"); 
		sqlBuff.append("GD.grph_domain_name user_geography, P.product_name, UB.balance user_balance,UB.PRODUCT_CODE FROM user_balances UB, users U, categories C, users OU, users PU,user_geographies UG, geographical_domains GD, LOOKUPS LOOK, products P");
		sqlBuff.append("WHERE UB.user_id=U.user_id AND U.category_code=C.category_code AND PU.user_id = (case U.parent_id when 'ROOT' then U.user_id else U.parent_id end) AND U.owner_id=OU.user_id AND UB.product_code= P.product_code AND U.user_id = UG.user_id");
		sqlBuff.append("AND UG.grph_domain_code = GD.grph_domain_code AND LOOK.lookup_code = U.status AND LOOK.lookup_type = 'URTYP' AND U.network_code='{?networkCode}' AND UG.grph_domain_code IN (with recursive q as(");
		sqlBuff.append("SELECT gd1.grph_domain_code, gd1.status FROM geographical_domains GD1 where grph_domain_code IN  (SELECT grph_domain_code FROM user_geographies UG1 WHERE UG1.grph_domain_code = (case '{?zoneCode}' when 'ALL' then UG1.grph_domain_code else '{?zoneCode}' end)");
		sqlBuff.append("AND UG1.user_id='{?loginUserID}') union all SELECT gd1.grph_domain_code, gd1.status FROM geographical_domains GD1 join q on q.grph_domain_code = gd1.parent_grph_domain_code) SELECT grph_domain_code FROM q"); 
		sqlBuff.append("WHERE status IN('Y','S')) AND C.domain_code = '{?domainCode}' AND C.category_code = case '{?categoryCode}' when 'ALL' then C.category_code else '{?categoryCode}' end AND U.user_id = case '{?userId}' when 'ALL' then U.user_id else '{?userId}' end");
		sqlBuff.append(")X left join  (users UD left join categories CD on UD.category_code =CD.category_code left join  user_balances CUB on UD.user_id=CUB.user_id ) on X.User_id =UD.parent_id AND X.product_code= CUB.product_code)X1");
		sqlBuff.append("WHERE coalesce(X1.category_type,'AGENT' )=CASE coalesce(X1.category_type,'AGENT' ) WHEN 'AGENT' THEN 'AGENT' ELSE X1.category_type END GROUP BY X1.user_id, X1.user_name, X1.msisdn, X1.user_category, X1.user_code, X1.user_geography,X1.product_name,"); 
		sqlBuff.append("X1.parent_name, X1.parent_msisdn, X1.owner_name, X1.owner_msisdn, X1.stat, X1.user_balance, X1.parent_id");
		LogFactory.printLog("userBalanceCSVReportCurrentDate", sqlBuff.toString(), log);
		return sqlBuff.toString();
	}

	@Override
	public String userBalanceCSVReportPreviousDate() {
		// TODO Auto-generated method stub
		StringBuilder sqlBuff = new StringBuilder("SELECT X1.user_id, X1.user_name, X1.msisdn, X1.user_category, X1.user_code, X1.user_geography,X1.product_name,X1.parent_name,  X1.parent_msisdn, X1.owner_name, X1.owner_msisdn, X1.stat, X1.user_balance, coalesce(SUM(X1.agent_balance), 0) agent_balance FROM (SELECT X.user_id, X.user_name, X.msisdn, X.user_category, X.user_code, X.user_geography,X.product_name,X.parent_name, X.parent_msisdn, X.owner_name, X.owner_msisdn, X.stat, X.user_balance ,UD.category_code,UD.parent_id, CD.category_type, CUB.balance agent_balance FROM (SELECT UB.user_id, U.user_name, U.msisdn, C.category_name user_category, U.user_code,   ");
		sqlBuff.append("(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PU.user_name END) parent_name, (CASE U.parent_id WHEN 'ROOT' THEN '' ELSE PU.msisdn END) parent_msisdn, (CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OU.user_name END) owner_name, (CASE  WHEN U.parent_id = 'ROOT' THEN ''  WHEN U.user_id = OU.user_id THEN '' ELSE OU.msisdn END) owner_msisdn, LOOK.lookup_name stat,"); 
		sqlBuff.append("GD.grph_domain_name user_geography, P.product_name, UB.balance user_balance,UB.PRODUCT_CODE FROM USER_DAILY_BALANCES UB, USERS U, CATEGORIES C, USERS OU, USERS PU,USER_GEOGRAPHIES UG, GEOGRAPHICAL_DOMAINS GD, LOOKUPS LOOK, PRODUCTS P WHERE UB.user_id=U.user_id AND U.category_code=C.category_code AND PU.user_id = (CASE U.parent_id WHEN 'ROOT' THEN U.user_id ELSE U.parent_id END)");
		sqlBuff.append("AND U.owner_id=OU.user_id AND UB.product_code= P.product_code AND U.user_id = UG.user_id AND UG.grph_domain_code = GD.grph_domain_code AND LOOK.lookup_code = U.status AND LOOK.lookup_type = 'URTYP'");
		sqlBuff.append("AND U.network_code='{?networkCode}' AND UG.grph_domain_code IN (with recursive q as(SELECT gd1.grph_domain_code, gd1.status FROM geographical_domains GD1 where grph_domain_code IN  (SELECT grph_domain_code ");
		sqlBuff.append("FROM user_geographies UG1 WHERE UG1.grph_domain_code = (case '{?zoneCode}' when 'ALL' then UG1.grph_domain_code else '{?zoneCode}' end) AND UG1.user_id='{?loginUserID}') union all SELECT gd1.grph_domain_code, gd1.status ");
		sqlBuff.append("FROM geographical_domains GD1 join q on q.grph_domain_code = gd1.parent_grph_domain_code) SELECT grph_domain_code FROM q WHERE status IN('Y','S')) AND C.domain_code = '{?domainCode}' AND C.category_code = CASE '{?categoryCode}' WHEN 'ALL' THEN C.category_code ELSE '{?categoryCode}' END ");
		sqlBuff.append("AND U.user_id = CASE '{?userId}' WHEN 'ALL' THEN U.user_id ELSE '{?userId}' END AND UB.balance_date={?fromDate} ) X left join (USERS UD left join CATEGORIES CD on  UD.category_code =CD.category_code");
		sqlBuff.append("left join USER_DAILY_BALANCES CUB on UD.user_id=CUB.user_id and CUB.balance_date={?fromDate} ) on X.User_id =UD.parent_id AND X.product_code= CUB.product_code)X1 WHERE coalesce(X1.category_type,'AGENT' )=CASE coalesce(X1.category_type,'AGENT' ) WHEN 'AGENT' THEN 'AGENT' ELSE X1.category_type END");
		sqlBuff.append("GROUP BY X1.user_id, X1.user_name, X1.msisdn, X1.user_category, X1.user_code, X1.user_geography,X1.product_name, X1.parent_name, X1.parent_msisdn, X1.owner_name,X1.owner_msisdn, X1.stat,  X1.user_balance,x1.parent_id");
        LogFactory.printLog("userBalanceCSVReportPreviousDate", sqlBuff.toString(), log);
		return sqlBuff.toString();
	}
	
	

}

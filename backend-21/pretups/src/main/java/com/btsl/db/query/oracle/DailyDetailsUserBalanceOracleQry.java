package com.btsl.db.query.oracle;

import com.btsl.pretups.processes.DailyDetailsUserBalanceQry;

public class DailyDetailsUserBalanceOracleQry implements DailyDetailsUserBalanceQry{

	@Override
	public String fetchUBSDataQry() {
		 final StringBuffer selectQueryBuffcatwise = new StringBuffer("SELECT n.network_code,n.network_name,cat.category_name,cat.category_code, ");
         selectQueryBuffcatwise.append(" nvl(count(u.user_id),0)  persons , ((nvl(sum(udb.BALANCE),0))/sp.default_value)  BALANCE	");
         selectQueryBuffcatwise.append(" FROM users u ,user_daily_balances udb,networks n,categories cat ,system_preferences sp where u.USER_TYPE NOT IN(?,?) and cat.category_code=u.category_code and u.user_id=udb.user_id");
         selectQueryBuffcatwise.append(" and udb.balance_date=?  and sp.PREFERENCE_CODE=? and u.network_code=n.network_code  and u.status not in(?,?)  group by cat.category_code,n.network_code, n.network_name, cat.category_name,sp.default_value order by CAT.category_code");
		return selectQueryBuffcatwise.toString();
	}

	@Override
	public String fetchUBSDataSelectUserBalQry() {
		 final StringBuffer selectQueryBuffUserWise = new StringBuffer(
	                "SELECT u.category_code,u.user_id||','||u.user_name||','||(udb.balance)/sp.default_value  closingBal   ");
	            selectQueryBuffUserWise
	                .append(" FROM users u,user_daily_balances udb , categories cat , system_preferences sp where u.USER_TYPE NOT IN(?,?) and udb.balance_date=?");
	            selectQueryBuffUserWise.append(" and cat.category_code=u.category_code and sp.PREFERENCE_CODE=? AND");
	            selectQueryBuffUserWise.append(" u.user_id=udb.user_id  and u.network_code=udb.network_code order by u.category_code");
		return selectQueryBuffUserWise.toString();
	}

}

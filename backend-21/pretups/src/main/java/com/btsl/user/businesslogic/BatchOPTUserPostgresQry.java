package com.btsl.user.businesslogic;

public class BatchOPTUserPostgresQry implements BatchOPTUserQry {
	@Override
	public String confirmBatchDeleteQry(){
	StringBuilder strSelectAuth = new StringBuilder("SELECT 1 FROM users USR ");
    strSelectAuth.append("WHERE USR.network_code = ? AND USR.status NOT IN ('N','C') ");
    strSelectAuth.append("AND USR.category_code IN(SELECT lookup_code FROM lookups WHERE lookup_type=?) ");
    strSelectAuth.append("AND USR.user_id IN ( with recursive q as ( SELECT user_id FROM users WHERE user_id = ? union all  select m.user_id from ");
    strSelectAuth.append("users m join q on q.user_id=m.parent_id  ) select q.user_id from q where user_id!=? ) ");
    strSelectAuth.append("AND USR.category_code <>? ");
    strSelectAuth.append("AND UPPER(USR.login_id)=UPPER(?)");
    return strSelectAuth.toString();
	}
}

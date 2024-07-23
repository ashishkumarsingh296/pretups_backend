package com.btsl.user.businesslogic;

public class BatchOPTUserOracleQry implements BatchOPTUserQry{
	@Override
	public String confirmBatchDeleteQry(){
	StringBuilder strSelectAuth = new StringBuilder("SELECT 1 FROM users USR ");
    strSelectAuth.append("WHERE USR.network_code = ? AND USR.status NOT IN ('N','C') ");
    strSelectAuth.append("AND USR.category_code IN(SELECT lookup_code FROM lookups WHERE lookup_type=?) ");
    strSelectAuth.append("AND USR.user_id IN ( SELECT user_id FROM users WHERE user_id != ? ");
    strSelectAuth.append("CONNECT BY PRIOR user_id = parent_id START WITH user_id=?) ");
    strSelectAuth.append("AND USR.category_code <>? ");
    strSelectAuth.append("AND UPPER(USR.login_id)=UPPER(?)");
    return strSelectAuth.toString();
	}
}

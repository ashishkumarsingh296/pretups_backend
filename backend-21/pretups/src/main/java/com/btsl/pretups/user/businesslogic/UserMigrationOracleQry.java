package com.btsl.pretups.user.businesslogic;

public class UserMigrationOracleQry implements UserMigrationQry{

	@Override
	public String markNpUsersQry() {
		StringBuilder strBuffMarkNpUsers = new StringBuilder();
		strBuffMarkNpUsers.append("UPDATE USERS SET status='NP'");
        strBuffMarkNpUsers.append(" WHERE user_id IN (SELECT user_id FROM USERS WHERE status NOT IN ('C','N')");
        strBuffMarkNpUsers.append(" CONNECT BY PRIOR user_id=parent_id");
        strBuffMarkNpUsers.append(" start with user_id=?)");
		return strBuffMarkNpUsers.toString();
	}

	@Override
	public String userMigrationProcessQry() {
		StringBuilder selectBffr = new StringBuilder("SELECT 1 FROM (SELECT grph_domain_code FROM ");
        selectBffr.append(" GEOGRAPHICAL_DOMAINS GD1 WHERE status IN('Y','S')");
        selectBffr.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
        selectBffr.append(" START WITH grph_domain_code =(SELECT grph_domain_code FROM USER_GEOGRAPHIES ug1");
        selectBffr.append(" WHERE UG1.user_id =? ))X");
        selectBffr.append(" WHERE X.grph_domain_code=?");
		return selectBffr.toString();
	}

	@Override
	public String userMigrationProcessQry2() {
		final StringBuilder selectBffrCheck = new StringBuilder("SELECT X.grph_domain_code,X.grph_domain_type FROM (SELECT grph_domain_code,grph_domain_type FROM ");
        selectBffrCheck.append(" GEOGRAPHICAL_DOMAINS GD1 WHERE status IN('Y','S')");
        selectBffrCheck.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
        selectBffrCheck.append(" START WITH grph_domain_code =(SELECT grph_domain_code FROM USER_GEOGRAPHIES ug1");
        selectBffrCheck.append(" WHERE UG1.user_id=(SELECT user_id FROM USERS WHERE msisdn=? and status='Y' and user_type='CHANNEL')))X");
        selectBffrCheck.append(" where  X.grph_domain_type=(select GRPH_DOMAIN_TYPE from CATEGORIES ");
        selectBffrCheck.append(" where CATEGORY_CODE=(select CATEGORY_CODE from users WHERE msisdn=? and status='Y' and user_type='CHANNEL'))");
		return selectBffrCheck.toString();
	}

	@Override
	public String loadMasterGeographyListQry() {
		StringBuilder strBuff = new StringBuilder(" SELECT GD.grph_domain_code geography_code,GD.grph_domain_name geography_name, GD.grph_domain_type grph_domain_type, ");
         strBuff.append(" GDT.sequence_no, GDT.grph_domain_type_name ");
         strBuff.append(" FROM geographical_domains GD,geographical_domain_types GDT ");
         strBuff.append(" WHERE GD.status IN('Y', 'S')  ");
         strBuff.append(" AND GDT.grph_domain_type = GD.grph_domain_type ");
         strBuff.append(" CONNECT BY PRIOR GD.grph_domain_code = GD.parent_grph_domain_code ");
         strBuff.append(" START WITH GD.grph_domain_type IN (select grph_domain_type from  GEOGRAPHICAL_DOMAINS ");
         strBuff.append(" where  grph_domain_code in (select grph_domain_code from user_geographies where user_id=?)) ");
		return strBuff.toString();
	}

}

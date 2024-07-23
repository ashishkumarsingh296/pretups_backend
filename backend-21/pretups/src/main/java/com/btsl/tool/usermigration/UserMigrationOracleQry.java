package com.btsl.tool.usermigration;

public class UserMigrationOracleQry implements UserMigrationToolQry{

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
		  StringBuilder selectBffrCheck = new StringBuilder("SELECT X.grph_domain_code,X.grph_domain_type FROM (SELECT grph_domain_code,grph_domain_type FROM ");
          selectBffrCheck.append(" GEOGRAPHICAL_DOMAINS GD1 WHERE status IN('Y','S')");
          selectBffrCheck.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
          selectBffrCheck.append(" START WITH grph_domain_code =(SELECT grph_domain_code FROM USER_GEOGRAPHIES ug1");
          selectBffrCheck.append(" WHERE UG1.user_id=(SELECT user_id FROM USERS WHERE msisdn=? and status='Y' and user_type='CHANNEL')))X");
          selectBffrCheck.append(" where  X.grph_domain_type=(select GRPH_DOMAIN_TYPE from CATEGORIES ");
          selectBffrCheck.append(" where CATEGORY_CODE=(select CATEGORY_CODE from users WHERE msisdn=? and status='Y' and user_type='CHANNEL'))");
          return selectBffrCheck.toString();
	}
}

package com.btsl.tool.usermigration;

public class UserMigrationPostgresQry  implements UserMigrationToolQry{

	@Override
	public String userMigrationProcessQry() {
		StringBuilder selectBffr = new StringBuilder("SELECT 1 FROM (with recursive q as ( SELECT grph_domain_code, status FROM GEOGRAPHICAL_DOMAINS GD1 ");
        selectBffr.append(" WHERE grph_domain_code =(SELECT grph_domain_code FROM USER_GEOGRAPHIES ug1 ");
        selectBffr.append(" WHERE UG1.user_id =? ) union all select m.grph_domain_code, m.status from GEOGRAPHICAL_DOMAINS m join q on ");
        selectBffr.append(" q.grph_domain_code=m.parent_grph_domain_code   )");
        selectBffr.append(" select q.grph_domain_code from q where status IN('Y','S') )X");
        selectBffr.append(" WHERE X.grph_domain_code=?");
        return selectBffr.toString();
	}
	@Override
	public String userMigrationProcessQry2() {
	 StringBuilder selectBffrCheck = new StringBuilder("SELECT X.grph_domain_code,X.grph_domain_type FROM ( ");
     selectBffrCheck.append(" with recursive q as (SELECT grph_domain_code,grph_domain_type, status FROM ");
     selectBffrCheck.append(" GEOGRAPHICAL_DOMAINS GD1 WHERE grph_domain_code =(SELECT grph_domain_code FROM USER_GEOGRAPHIES ug1");
     selectBffrCheck.append("  WHERE UG1.user_id=(SELECT user_id FROM USERS WHERE msisdn=? and status='Y' and user_type='CHANNEL')) union all ");
     selectBffrCheck.append(" select m.grph_domain_code,m.grph_domain_type , m.status from GEOGRAPHICAL_DOMAINS m join q on q.grph_domain_code = m.parent_grph_domain_code");
     selectBffrCheck.append("  )SELECT q.grph_domain_code,q.grph_domain_type FROM q where status IN('Y','S') ");
     selectBffrCheck.append(" )X");
     selectBffrCheck.append(" where  X.grph_domain_type=(select GRPH_DOMAIN_TYPE from CATEGORIES ");
     selectBffrCheck.append(" where CATEGORY_CODE=(select CATEGORY_CODE from users WHERE msisdn=? and status='Y' and user_type='CHANNEL'))");
     return selectBffrCheck.toString();
	}
}

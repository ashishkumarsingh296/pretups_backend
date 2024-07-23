package com.btsl.pretups.user.businesslogic;

public class UserMigrationPostgresQry implements UserMigrationQry {

	@Override
	public String markNpUsersQry() {
		StringBuilder strBuffMarkNpUsers = new StringBuilder();
		strBuffMarkNpUsers.append("UPDATE USERS SET status='NP'");
        strBuffMarkNpUsers.append(" WHERE user_id IN ( ");
        strBuffMarkNpUsers.append(" with recursive q as ( SELECT user_id, status FROM USERS WHERE user_id=? ");
        strBuffMarkNpUsers.append(" union all  select m.user_id, m.status from USERS m join q on q.user_id=m.parent_id  ) ");
        strBuffMarkNpUsers.append("  SELECT q.user_id from q where status NOT IN ('C','N') ");
        strBuffMarkNpUsers.append(" )");
		return strBuffMarkNpUsers.toString();
	}
	
	@Override
	public String userMigrationProcessQry() {
		StringBuilder selectBffr = new StringBuilder("SELECT 1 FROM ( ");
		selectBffr.append("  with recursive q as ( SELECT grph_domain_code, status FROM ");
        selectBffr.append(" GEOGRAPHICAL_DOMAINS GD1 WHERE grph_domain_code =(SELECT grph_domain_code FROM USER_GEOGRAPHIES ug1");
        selectBffr.append(" WHERE UG1.user_id =? )  union all  SELECT m.grph_domain_code, m.status FROM  ");
        selectBffr.append(" GEOGRAPHICAL_DOMAINS m join q on q.grph_domain_code=m.parent_grph_domain_code   )");
        selectBffr.append(" select q.grph_domain_code from q where status IN('Y','S') ");
        selectBffr.append(" )X");
        selectBffr.append(" WHERE X.grph_domain_code=?");
		return selectBffr.toString();
	}
	@Override
	public String userMigrationProcessQry2() {
		final StringBuilder selectBffrCheck = new StringBuilder("SELECT X.grph_domain_code,X.grph_domain_type FROM ( ");
		selectBffrCheck.append(" with recursive q as (SELECT grph_domain_code,grph_domain_type, status FROM ");
        selectBffrCheck.append(" GEOGRAPHICAL_DOMAINS GD1 WHERE  grph_domain_code =(SELECT grph_domain_code FROM USER_GEOGRAPHIES ug1 ");
        selectBffrCheck.append(" WHERE UG1.user_id=(SELECT user_id FROM USERS WHERE msisdn=? and status='Y' and user_type='CHANNEL')) union all ");
        selectBffrCheck.append(" select m.grph_domain_code,m.grph_domain_type, m.status from GEOGRAPHICAL_DOMAINS m join q on q.grph_domain_code = m.parent_grph_domain_code ");
        selectBffrCheck.append(" ) SELECT q.grph_domain_code,q.grph_domain_type FROM q where status IN('Y','S')  ");
        selectBffrCheck.append(" )X");
        selectBffrCheck.append(" where  X.grph_domain_type=(select GRPH_DOMAIN_TYPE from CATEGORIES ");
        selectBffrCheck.append(" where CATEGORY_CODE=(select CATEGORY_CODE from users WHERE msisdn=? and status='Y' and user_type='CHANNEL'))");
		return selectBffrCheck.toString();
	}
	@Override
	public String loadMasterGeographyListQry() {
		StringBuilder strBuff = new StringBuilder(" with recursive q as (SELECT GD.parent_grph_domain_code, GD.grph_domain_code ,GD.grph_domain_name , GD.grph_domain_type ,  ");
         strBuff.append(" GDT.sequence_no, GDT.grph_domain_type_name, GD.status,array[grph_domain_code] as path_info FROM geographical_domains GD,geographical_domain_types GDT ");
         strBuff.append(" WHERE GDT.grph_domain_type = GD.grph_domain_type and GD.grph_domain_type IN ( select grph_domain_type from  GEOGRAPHICAL_DOMAINS  ");
         strBuff.append(" where  grph_domain_code in (select grph_domain_code from user_geographies where user_id=?) )union all   ");
         strBuff.append(" SELECT m.parent_grph_domain_code, m.grph_domain_code ,m.grph_domain_name , m.grph_domain_type , GDTT.sequence_no,GDTT.grph_domain_type_name ,m.status,path_info ");
         strBuff.append(" FROM geographical_domain_types GDTT ,geographical_domains m join q on  m.parent_grph_domain_code=q.grph_domain_code  where GDTT.grph_domain_type = m.grph_domain_type  ) ");
         strBuff.append(" SELECT q.grph_domain_code geography_code,q.grph_domain_name geography_name, q.grph_domain_type grph_domain_type,  ");
         strBuff.append(" q.sequence_no, q.grph_domain_type_name FROM q ");
         strBuff.append(" where q.status IN('Y', 'S')  order by path_info ,q.sequence_no  ");
		return strBuff.toString();
	}
}

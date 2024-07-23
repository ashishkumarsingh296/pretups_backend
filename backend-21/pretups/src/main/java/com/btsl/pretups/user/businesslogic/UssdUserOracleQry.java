package com.btsl.pretups.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;

public class UssdUserOracleQry implements UssdUserQry{
	
	private Log log = LogFactory.getLog(this.getClass());

	@Override
	public String loadUsersDetailsQry() {
		 final StringBuilder strBuff = new StringBuilder();
	        strBuff.append(" SELECT USR.user_id usr_user_id,USR.user_name usr_user_name,USR.network_code,");
	        strBuff.append("USR.login_id,USR.password password1,USR.category_code usr_category_code,USR.parent_id,");
	        strBuff.append("USR.owner_id,USR.allowed_ip,USR.allowed_days,");
	        strBuff.append("USR.from_time,USR.to_time,USR.employee_code,");
	        strBuff.append("USR.status usr_status,USR.email,USR.pswd_modified_on,USR.contact_no,");
	        strBuff.append("USR.designation,USR.division,USR.department,USR.msisdn usr_msisdn,USR.user_type,");
	        strBuff.append("USR.created_by,USR.created_on,USR.modified_by,USR.modified_on,USR.address1, ");
	        strBuff.append("USR.address2,USR.city,USR.state,USR.country,USR.ssn,USR.user_name_prefix, ");
	        strBuff.append("USR.external_code,USR.short_name,USR.level1_approved_by,USR.level1_approved_on,");
	        strBuff.append("USR.level2_approved_by,USR.level2_approved_on,USR.user_code,USR.appointment_date, ");
	        strBuff.append("USR_CAT.category_code usr_cat_category_code,USR_CAT.category_name,");
	        strBuff.append("USR_CAT.domain_code,USR_CAT.sequence_no,USR_CAT.grph_domain_type, ");
	        strBuff.append("USR_CAT.multiple_grph_domains,USR_CAT.web_interface_allowed,USR_CAT.sms_interface_allowed, ");
	        strBuff.append("USR_CAT.fixed_roles,USR_CAT.status usr_cat_status,USR_CAT.multiple_login_allowed,");
	        strBuff.append("PRNT_USR.user_name parent_name, PRNT_USR.msisdn parent_msisdn, ");
	        strBuff.append("PRNT_CAT.category_name parent_cat, ONR_USR.user_name owner_name, ONR_USR.msisdn owner_msisdn, ");
	        strBuff.append("ONR_CAT.category_name owner_cat ");
	        strBuff.append("FROM users USR, users PRNT_USR,users ONR_USR,categories USR_CAT, ");
	        strBuff.append("categories ONR_CAT, categories  PRNT_CAT ");
	        strBuff.append("WHERE USR.status <> 'N' AND USR.status <> 'C' AND USR.msisdn=? ");
	        strBuff.append("AND USR.parent_id=PRNT_USR.user_id(+) AND USR.owner_id=ONR_USR.user_id ");
	        strBuff.append("AND USR.category_code=USR_CAT.category_code ");
	        strBuff.append("AND ONR_CAT.category_code=ONR_USR.category_code ");
	        strBuff.append("AND PRNT_USR.category_code=PRNT_CAT.category_code(+) ");
	        LogFactory.printLog("loadUsersDetailsQry", strBuff.toString(), log);
		return strBuff.toString();
	}

	@Override
	public PreparedStatement loadDefaultGeographyQry(Connection con, String parentMsisdn, String childCatgCode) throws SQLException {
	       final StringBuilder strBuff = new StringBuilder(" SELECT X.grph_domain_code,X.grph_domain_type FROM (SELECT grph_domain_code,grph_domain_type FROM ");
	        strBuff.append(" GEOGRAPHICAL_DOMAINS GD1 WHERE status =? AND GD1.IS_DEFAULT=?");
	        strBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
	        strBuff.append(" START WITH grph_domain_code =(SELECT grph_domain_code FROM USER_GEOGRAPHIES ug1 ");
	        strBuff.append(" WHERE UG1.user_id=(SELECT user_id FROM USERS WHERE msisdn=? AND status=? AND user_type='CHANNEL')))X ");
	        strBuff.append(" WHERE  X.grph_domain_type=(SELECT GRPH_DOMAIN_TYPE FROM CATEGORIES ");
	        strBuff.append(" WHERE CATEGORY_CODE=? )");
	        LogFactory.printLog("loadDefaultGeographyQry", strBuff.toString(), log);
	        PreparedStatement pstmtSelect = con.prepareStatement(strBuff.toString());
            pstmtSelect.setString(1, PretupsI.YES);
            pstmtSelect.setString(2, PretupsI.YES);
            pstmtSelect.setString(3, parentMsisdn);
            pstmtSelect.setString(4, PretupsI.YES);
            pstmtSelect.setString(5, childCatgCode);
		return pstmtSelect;
	}

}

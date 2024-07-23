package com.btsl.db.query.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.pretups.channel.reports.businesslogic.ChannelUserOperatorUserRolesQuery;
import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * @author mohit.miglani
 *
 */
public class ChannelUserOperatorUserRolesPostgresQry implements
		ChannelUserOperatorUserRolesQuery {
	@Override
	public PreparedStatement loadExternalUserRolesOperatorReportQry(
			UsersReportModel usersReportModel, Connection con)
			throws SQLException {

		StringBuilder strBuff = new StringBuilder();

		strBuff.append("SELECT (CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PU.user_name END) parent_name, (CASE  WHEN U.parent_id = 'ROOT' THEN '' ELSE PU.msisdn ");
		strBuff.append("END) parent_msisdn, (CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OU.user_name END) owner_name, (CASE ");

		strBuff.append("WHEN U.parent_id = 'ROOT' THEN ''  WHEN U.user_id = OU.user_id THEN '' ELSE OU.msisdn END)");
		strBuff.append("owner_msisdn,u.CATEGORY_CODE,u.STATUS,u.USER_NAME,u.LOGIN_ID,u.MSISDN,c.CATEGORY_NAME,d.DOMAIN_NAME,UG.grph_domain_code,");
		strBuff.append(" Getuserroles(U.user_id,'Y') role_name,Getuserrolestype(U.user_id,'Y') roletype");

		strBuff.append(" FROM USERS u,CATEGORIES c,DOMAINS d,GEOGRAPHICAL_DOMAINS GD,USER_GEOGRAPHIES UG,USERS PU,USERS OU ");
		strBuff.append("WHERE u.CATEGORY_CODE=c.CATEGORY_CODE ");

		strBuff.append("AND c.DOMAIN_CODE=d.DOMAIN_CODE AND u.USER_ID=ug.USER_ID AND PU.user_id = (CASE U.parent_id WHEN 'ROOT' THEN U.user_id ELSE U.parent_id END)");

		strBuff.append(" AND OU.USER_ID=U.OWNER_ID AND ug.GRPH_DOMAIN_CODE=gd.GRPH_DOMAIN_CODE AND UG.grph_domain_code IN (with recursive q as(");

		strBuff.append("SELECT grph_domain_code, status from GEOGRAPHICAL_DOMAINS where grph_domain_code IN ");

		strBuff.append(" (SELECT grph_domain_code FROM USER_GEOGRAPHIES ug1 WHERE UG1.grph_domain_code = CASE ?  WHEN 'ALL' THEN UG1.grph_domain_code ELSE  ? END ");

		strBuff.append("  AND UG1.user_id=?) union all SELECT GD1.grph_domain_code , GD1.status ");
		strBuff.append("from GEOGRAPHICAL_DOMAINS GD1 join q on q.grph_domain_code = GD1.parent_grph_domain_code) SELECT grph_domain_code");
		strBuff.append(" from q WHERE status IN('Y', 'S'))AND U.STATUS = CASE ? WHEN  'ALL' THEN U.STATUS ELSE ? END");

		strBuff.append(" AND c.DOMAIN_CODE IN (");

		String[] arr1 = usersReportModel.getDomainListString().split("\\,");
		for (int i = 0; i < arr1.length; i++) {
			strBuff.append(" ?");
			if (i != arr1.length - 1) {
				strBuff.append(",");
			}
		}

		strBuff.append(") AND C.CATEGORY_CODE = CASE ? WHEN  'ALL' THEN C.CATEGORY_CODE ELSE ? END");

		strBuff.append(" AND U.USER_ID = CASE ? WHEN  'ALL' THEN U.USER_ID ELSE ? END ");

		PreparedStatement pstmt;

		String selectQuery = strBuff.toString();
		pstmt = con.prepareStatement(selectQuery);
		int i = 1;

		pstmt.setString(i++, usersReportModel.getZoneCode());
		pstmt.setString(i++, usersReportModel.getZoneCode());
		pstmt.setString(i++, usersReportModel.getLoginUserID());
		pstmt.setString(i++, usersReportModel.getUserStatus());
		pstmt.setString(i++, usersReportModel.getUserStatus());
		String[] arr2 = usersReportModel.getDomainListString().split("\\,");

		for (int x = 0; x < arr2.length; x++) {
			arr2[x] = arr2[x].replace("'", "");
			pstmt.setString(i++, arr2[x]);

		}
		pstmt.setString(i++, usersReportModel.getParentCategoryCode());
		pstmt.setString(i++, usersReportModel.getParentCategoryCode());
		pstmt.setString(i++, usersReportModel.getUserID());
		pstmt.setString(i, usersReportModel.getUserID());
		return pstmt;

	}

	@Override
	public PreparedStatement loadExternalUserRolesChannelReportQry(
			UsersReportModel usersReportModel, Connection con)
			throws SQLException {

		StringBuilder strBuff = new StringBuilder();

		strBuff.append("SELECT u.CATEGORY_CODE,u.STATUS,u.USER_NAME,u.LOGIN_ID,u.MSISDN,c.CATEGORY_NAME,d.DOMAIN_NAME,UG.grph_domain_code,");

		strBuff.append("Getuserroles(U.user_id,'Y') role_name,Getuserrolestype(U.user_id,'Y') roletype FROM (");
		strBuff.append(" with recursive q as(SELECT USR.user_id FROM users USR where USR.user_id=case  ?   when 'ALL' then USr.USER_ID else ? end union all");
		strBuff.append(" SELECT USR.user_id FROM users USR join q on q.user_id = USR.parent_id )select user_id from q ");
		strBuff.append(") X,USERS u,CATEGORIES c,DOMAINS d,GEOGRAPHICAL_DOMAINS GD,USER_GEOGRAPHIES UG WHERE u.CATEGORY_CODE=c.CATEGORY_CODE");
		strBuff.append(" AND c.DOMAIN_CODE=d.DOMAIN_CODE AND u.USER_ID=ug.USER_ID AND u.USER_ID=X.USER_ID");

		strBuff.append(" AND ug.GRPH_DOMAIN_CODE=gd.GRPH_DOMAIN_CODE AND UG.grph_domain_code IN (with recursive q as(");
		strBuff.append(" SELECT gd1.grph_domain_code, gd1.status FROM geographical_domains GD1 where grph_domain_code IN  (SELECT grph_domain_code FROM user_geographies UG1");
		strBuff.append(" WHERE UG1.grph_domain_code = (case ? when 'ALL' then UG1.grph_domain_code else ? end) AND UG1.user_id= ?) union all");
		strBuff.append(" SELECT gd1.grph_domain_code, gd1.status FROM geographical_domains GD1 join q on q.grph_domain_code = gd1.parent_grph_domain_code)");

		strBuff.append(" SELECT grph_domain_code FROM q WHERE status IN('Y','S')");
		strBuff.append(")AND U.STATUS = CASE 'ALL' WHEN  'ALL' THEN U.STATUS ELSE 'ALL' END");

		strBuff.append(" AND c.DOMAIN_CODE IN (");

		String[] arr1 = usersReportModel.getDomainListString().split("\\,");
		for (int i = 0; i < arr1.length; i++) {
			strBuff.append(" ?");
			if (i != arr1.length - 1) {
				strBuff.append(",");
			}
		}

		strBuff.append(") AND C.CATEGORY_CODE = CASE ? WHEN  'ALL' THEN C.CATEGORY_CODE ELSE ? END");

		PreparedStatement pstmt;

		String selectQuery = strBuff.toString();
		pstmt = con.prepareStatement(selectQuery);
		int i = 1;

		pstmt.setString(i++, usersReportModel.getUserID());
		pstmt.setString(i++, usersReportModel.getUserID());
		pstmt.setString(i++, usersReportModel.getZoneCode());
		pstmt.setString(i++, usersReportModel.getZoneCode());
		pstmt.setString(i++, usersReportModel.getLoginUserID());
	
		String[] arr2 = usersReportModel.getDomainListString().split("\\,");

		for (int x = 0; x < arr2.length; x++) {
			arr2[x] = arr2[x].replace("'", "");
			pstmt.setString(i++, arr2[x]);

		}
		pstmt.setString(i++, usersReportModel.getParentCategoryCode());
		pstmt.setString(i, usersReportModel.getParentCategoryCode());

		return pstmt;

	}

}

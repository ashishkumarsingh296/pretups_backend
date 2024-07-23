package com.btsl.db.query.oracle;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.springframework.expression.ParseException;

import com.btsl.pretups.channel.reports.businesslogic.UserZeroBalanceCounterSummaryQry;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.channel.reports.web.UsersReportModel;

public class UserZeroBalanceCounterSummaryOracleQry implements
		UserZeroBalanceCounterSummaryQry {

	@Override
	public PreparedStatement loadUserBalanceReportQry(
			UsersReportModel usersReportModel, Connection con,
			Timestamp fromDateTimeValue, Timestamp toDateTimeValue)
			throws SQLException, ParseException{
		// TODO Auto-generated method stub

		StringBuilder strBuff = new StringBuilder();

		strBuff.append("SELECT U.user_name,U.msisdn, L2.lookup_name user_status,");
		strBuff.append("CAT.category_name, (CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PU.user_name END)   parent_name,");
		strBuff.append("(CASE U.parent_id WHEN 'ROOT' THEN '' ELSE PU.msisdn END) parent_msisdn,(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE OU.user_name END) owner_name,");
		strBuff.append("(CASE U.parent_id WHEN 'ROOT' THEN '' ELSE OU.msisdn END) owner_msisdn,");
		strBuff.append("TO_CHAR( UTC.ENTRY_DATE, ?) ENTRY_DATE,");

		strBuff.append("P.product_name,L1.lookup_name record_type,TO_CHAR(COUNT (record_type))threshold_count FROM USERS U, USER_THRESHOLD_COUNTER UTC, LOOKUPS L1, LOOKUPS L2, CATEGORIES CAT, ");
		strBuff.append("PRODUCTS P, USER_GEOGRAPHIES UG, GEOGRAPHICAL_DOMAINS GD,USERS PU,USERS OU ");
		strBuff.append("WHERE UTC.ENTRY_DATE>=TO_DATE(?,?) ");
		strBuff.append("AND UTC.entry_date<=TO_DATE(?,?) ");
		strBuff.append("AND UTC.network_code=? ");
		strBuff.append("AND U.user_id=UTC.user_id ");
		strBuff.append("AND UTC.RECORD_TYPE=CASE ?  WHEN 'ALL' THEN UTC.RECORD_TYPE ELSE  ? END ");
		strBuff.append("AND L1.lookup_type='THRTP' ");
		strBuff.append("AND L1.lookup_code=UTC.RECORD_TYPE ");

		strBuff.append("AND UTC.category_code = CASE ?  WHEN 'ALL' THEN UTC.category_code ELSE ? END ");
		strBuff.append("AND CAT.category_code=UTC.category_code ");

		strBuff.append("AND CAT.domain_code = ? ");
		strBuff.append("AND p.PRODUCT_CODE=UTC.PRODUCT_CODE ");
		strBuff.append("AND UG.user_id = UTC.user_id ");
		strBuff.append("AND PU.user_id = (CASE U.parent_id WHEN 'ROOT' THEN U.user_id ELSE U.parent_id END) ");
		strBuff.append("AND OU.USER_ID=U.OWNER_ID ");
		strBuff.append("AND L2.lookup_type='URTYP' ");
		strBuff.append("AND L2.lookup_code=U.status ");
		strBuff.append("AND UG.grph_domain_code = GD.grph_domain_code ");
		strBuff.append("AND UG.grph_domain_code IN ( ");
		strBuff.append("SELECT grph_domain_code ");
		strBuff.append("FROM ");
		strBuff.append("GEOGRAPHICAL_DOMAINS GD1 ");
		strBuff.append("WHERE status IN('Y', 'S') ");
		strBuff.append("CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
		strBuff.append("START WITH grph_domain_code IN ");
		strBuff.append("( ");
		strBuff.append("SELECT grph_domain_code ");
		strBuff.append("FROM USER_GEOGRAPHIES ug1 ");

		strBuff.append("WHERE UG1.grph_domain_code =CASE ?  WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END ");

		strBuff.append("AND UG1.user_id=?) ");
		strBuff.append(") ");
		strBuff.append("group by U.user_name,U.msisdn, L2.lookup_name,CAT.category_name,PU.user_name,PU.msisdn,OU.user_name,OU.msisdn, UTC.ENTRY_DATE,P.product_name,L1.lookup_name,U.parent_id ");
		PreparedStatement pstmt;
		String selectQuery = strBuff.toString();
		pstmt = con.prepareStatement(selectQuery);
		int i = 1;
		pstmt.setString(i++, Constants.getProperty("report.onlydateformat"));
		pstmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(new Date(
				fromDateTimeValue.getTime())));
		pstmt.setString(i++, Constants.getProperty("report.onlydateformat"));
		pstmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(new Date(
				toDateTimeValue.getTime())));
		pstmt.setString(i++, Constants.getProperty("report.onlydateformat"));
		pstmt.setString(i++, usersReportModel.getNetworkCode());

		pstmt.setString(i++, usersReportModel.getThresholdType());
		pstmt.setString(i++, usersReportModel.getThresholdType());
		pstmt.setString(i++, usersReportModel.getParentCategoryCode());
		pstmt.setString(i++, usersReportModel.getParentCategoryCode());
		pstmt.setString(i++, usersReportModel.getDomainCode());
		pstmt.setString(i++, usersReportModel.getZoneCode());
		pstmt.setString(i++, usersReportModel.getZoneCode());
		pstmt.setString(i++, usersReportModel.getLoginUserID());
		return pstmt;

	}

	@Override
	public PreparedStatement loadzeroBalSummChannelUserReportQry(
			UsersReportModel usersReportModel, Connection con,
			Timestamp fromDateTimeValue, Timestamp toDateTimeValue)
			throws SQLException, ParseException {

		StringBuilder strBuff = new StringBuilder();

		strBuff.append("SELECT U.user_name,U.msisdn,L2.lookup_name ");
		strBuff.append("user_status,CAT.category_name, TO_CHAR( UTC.ENTRY_DATE,");
		strBuff.append("?) ENTRY_DATE,");
		strBuff.append("P.product_name ,L1.lookup_name record_type,to_char(count ");
		strBuff.append("(record_type))threshold_count FROM USERS U, USER_THRESHOLD_COUNTER UTC, LOOKUPS L1, LOOKUPS L2, CATEGORIES CAT,");

		strBuff.append("PRODUCTS P, USER_GEOGRAPHIES UG, GEOGRAPHICAL_DOMAINS GD Where UTC.entry_date>=TO_DATE(?,?) ");
		strBuff.append("AND UTC.entry_date<=TO_DATE(?,?) AND UTC.network_code=? AND UTC.user_id IN(SELECT U11.user_id FROM users U11 ");
		strBuff.append("WHERE U11.user_id=U11.user_id CONNECT BY PRIOR U11.user_id = U11.parent_id START WITH U11.user_id = ?)");
		strBuff.append("AND U.user_id=UTC.user_id AND UTC.RECORD_TYPE=CASE ?  WHEN 'ALL' THEN UTC.RECORD_TYPE ELSE  ? END ");
		strBuff.append("AND L1.lookup_type='THRTP' AND L1.lookup_code=UTC.RECORD_TYPE AND UTC.category_code = CASE ?  WHEN 'ALL' THEN UTC.category_code ELSE ? END ");
		strBuff.append("AND CAT.category_code=UTC.category_code AND CAT.domain_code = ? AND P.PRODUCT_CODE=UTC.PRODUCT_CODE AND UG.user_id = UTC.user_id ");
		strBuff.append("AND L2.lookup_type='URTYP' AND L2.lookup_code=U.status AND UG.grph_domain_code = GD.grph_domain_code AND UG.grph_domain_code IN (");
		strBuff.append("SELECT grph_domain_code FROM GEOGRAPHICAL_DOMAINS GD1 WHERE status IN('Y', 'S')");
		strBuff.append("CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START WITH grph_domain_code IN(SELECT grph_domain_code ");
		strBuff.append("FROM USER_GEOGRAPHIES ug1 WHERE UG1.grph_domain_code =CASE ?  WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END AND UG1.user_id=?))");
		strBuff.append("group by U.user_name,U.msisdn ,L2.lookup_name,CAT.category_name,UTC.ENTRY_DATE,P.product_name,L1.lookup_name");

		PreparedStatement pstmt;
		String selectQuery = strBuff.toString();
		pstmt = con.prepareStatement(selectQuery);
		int i = 1;
		pstmt.setString(i++, Constants.getProperty("report.onlydateformat"));
		pstmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(new Date(
				fromDateTimeValue.getTime())));
		pstmt.setString(i++, Constants.getProperty("report.onlydateformat"));
		pstmt.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(new Date(
				toDateTimeValue.getTime())));
		pstmt.setString(i++, Constants.getProperty("report.onlydateformat"));
		pstmt.setString(i++, usersReportModel.getNetworkCode());
		pstmt.setString(i++, usersReportModel.getLoginUserID());
		pstmt.setString(i++, usersReportModel.getThresholdType());
		pstmt.setString(i++, usersReportModel.getThresholdType());
		pstmt.setString(i++, usersReportModel.getParentCategoryCode());
		pstmt.setString(i++, usersReportModel.getParentCategoryCode());
		pstmt.setString(i++, usersReportModel.getDomainCode());
		pstmt.setString(i++, usersReportModel.getZoneCode());
		pstmt.setString(i++, usersReportModel.getZoneCode());

		pstmt.setString(i++, usersReportModel.getLoginUserID());
		return pstmt;

	}

}

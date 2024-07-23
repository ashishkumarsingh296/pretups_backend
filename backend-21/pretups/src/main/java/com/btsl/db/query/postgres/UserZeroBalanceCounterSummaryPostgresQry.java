package com.btsl.db.query.postgres;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.expression.ParseException;

import com.btsl.pretups.channel.reports.businesslogic.UserZeroBalanceCounterSummaryQry;
import com.btsl.util.Constants;
import com.web.pretups.channel.reports.web.UsersReportModel;

public class UserZeroBalanceCounterSummaryPostgresQry implements
		UserZeroBalanceCounterSummaryQry {

	@Override
	public PreparedStatement loadUserBalanceReportQry(
			UsersReportModel usersReportModel, Connection con,
			Timestamp fromDateTimeValue, Timestamp toDateTimeValue)
			throws SQLException, ParseException {

		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT U.user_name,U.msisdn, L2.lookup_name user_status,");
		strBuff.append("CAT.category_name, (CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PU.user_name END)   parent_name,");
		strBuff.append("(CASE U.parent_id WHEN 'ROOT' THEN '' ELSE PU.msisdn END)   parent_msisdn,(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE OU.user_name END)   owner_name, ");
		strBuff.append("(CASE U.parent_id WHEN 'ROOT' THEN '' ELSE OU.msisdn END) owner_msisdn,");
		strBuff.append("TO_CHAR( UTC.ENTRY_DATE, ?) ENTRY_DATE,");

		strBuff.append("P.product_name,L1.lookup_name record_type,TO_CHAR(COUNT (record_type),'FM999999999999999999')threshold_count FROM USERS U, USER_THRESHOLD_COUNTER UTC,LOOKUPS L1,LOOKUPS L2, CATEGORIES CAT,");
		strBuff.append("PRODUCTS P, USER_GEOGRAPHIES UG, GEOGRAPHICAL_DOMAINS GD,USERS PU,USERS OU ");

		strBuff.append("WHERE UTC.ENTRY_DATE>=? ");

		strBuff.append("AND UTC.entry_date<=? ");

		strBuff.append("AND UTC.network_code=? ");
		strBuff.append("AND U.user_id=UTC.user_id ");

		strBuff.append(" AND UTC.RECORD_TYPE=CASE ?  WHEN 'ALL' THEN UTC.RECORD_TYPE ELSE  ? END  ");
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
		strBuff.append("with recursive q as ( ");
		strBuff.append("SELECT grph_domain_code,status ");
		strBuff.append("from geographical_domains ");
		strBuff.append("WHERE grph_domain_code IN ");
		strBuff.append("(SELECT grph_domain_code ");
		strBuff.append("FROM user_geographies UG1 ");
		strBuff.append("WHERE UG1.grph_domain_code = (case ? when 'ALL' then UG1.grph_domain_code else ? end) ");

		strBuff.append("AND UG1.user_id=?) ");

		strBuff.append("union all ");

		strBuff.append("select m.grph_domain_code,m.status  ");

		strBuff.append("from geographical_domains m join q on q.grph_domain_code=m.parent_grph_domain_code ) ");
		strBuff.append("select q.grph_domain_code ");

		strBuff.append("from q ");
		strBuff.append("where status IN('Y', 'S')");
		strBuff.append(") ");
		strBuff.append("group by U.user_name,U.msisdn, L2.lookup_name,CAT.category_name,PU.user_name,PU.msisdn,OU.user_name,OU.msisdn, UTC.ENTRY_DATE,P.product_name,L1.lookup_name,U.parent_id");

		PreparedStatement pstmt;

		String selectQuery = strBuff.toString();
		pstmt = con.prepareStatement(selectQuery);
		int i = 1;
		pstmt.setString(i++, Constants.getProperty("report.datetimeformat"));
		usersReportModel.setFromDateTimeStamp(fromDateTimeValue);
		usersReportModel.setToDateTimeStamp(toDateTimeValue);
		pstmt.setTimestamp(i++, usersReportModel.getFromDateTimeStamp());
		pstmt.setTimestamp(i++, usersReportModel.getToDateTimeStamp());
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
		strBuff.append("SELECT U.user_name,U.msisdn,L2.lookup_name user_status,CAT.category_name, TO_CHAR( UTC.ENTRY_DATE, ?) ENTRY_DATE,");
		strBuff.append(" P.product_name ,L1.lookup_name record_type,to_char(count (record_type),'FM999999999999999999')threshold_count FROM USERS U, USER_THRESHOLD_COUNTER UTC, LOOKUPS L1, LOOKUPS L2, CATEGORIES CAT,");

		strBuff.append("PRODUCTS P, USER_GEOGRAPHIES UG, GEOGRAPHICAL_DOMAINS GD Where UTC.entry_date>= ? ");
		strBuff.append(" AND UTC.entry_date<= ? AND UTC.network_code=? AND UTC.user_id IN(with recursive q as ( SELECT U11.user_id ");
		strBuff.append("FROM users U11 where U11.user_id=? union all select m.user_id from USERS m join q on");
		strBuff.append(" q.user_id=m.parent_id) select user_id from q where q.user_id=q.user_id) ");
		strBuff.append("AND U.user_id=UTC.user_id AND UTC.RECORD_TYPE=CASE ?  WHEN 'ALL' THEN UTC.RECORD_TYPE ELSE  ? END AND L1.lookup_type='THRTP'");
		strBuff.append("AND L1.lookup_code=UTC.RECORD_TYPE AND UTC.category_code = CASE ?  WHEN 'ALL' THEN UTC.category_code ELSE ? END");

		strBuff.append(" AND CAT.category_code=UTC.category_code  AND CAT.domain_code = ? AND P.PRODUCT_CODE=UTC.PRODUCT_CODE AND UG.user_id = UTC.user_id");
		strBuff.append(" AND L2.lookup_type='URTYP' AND L2.lookup_code=U.status AND UG.grph_domain_code = GD.grph_domain_code AND UG.grph_domain_code IN (");
		strBuff.append(" with recursive q as (SELECT grph_domain_code,status  from geographical_domains WHERE grph_domain_code IN(SELECT grph_domain_code");
		strBuff.append(" FROM user_geographies UG1 WHERE UG1.grph_domain_code = (case ? when 'ALL' then UG1.grph_domain_code else ? end) AND UG1.user_id=?)");
		strBuff.append(" union all select m.grph_domain_code,m.status from geographical_domains m join q on q.grph_domain_code=m.parent_grph_domain_code )");

		strBuff.append("select q.grph_domain_code from q where status IN('Y', 'S'))group by U.user_name,U.msisdn");
		strBuff.append(",L2.lookup_name,CAT.category_name,UTC.ENTRY_DATE,P.product_name,L1.lookup_name");

		PreparedStatement pstmt;

		String selectQuery = strBuff.toString();
		pstmt = con.prepareStatement(selectQuery);
		int i = 1;
		pstmt.setString(i++, Constants.getProperty("report.datetimeformat"));
		usersReportModel.setFromDateTimeStamp(fromDateTimeValue);
		usersReportModel.setToDateTimeStamp(toDateTimeValue);
		pstmt.setTimestamp(i++, usersReportModel.getFromDateTimeStamp());
		pstmt.setTimestamp(i++, usersReportModel.getToDateTimeStamp());
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

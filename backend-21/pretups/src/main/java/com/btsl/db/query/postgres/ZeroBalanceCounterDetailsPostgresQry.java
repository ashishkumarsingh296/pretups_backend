package com.btsl.db.query.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.reports.businesslogic.ZeroBalanceCounterDetailsRptQry;
import com.btsl.util.Constants;
import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * @author tarun.kumar
 *
 */
public class ZeroBalanceCounterDetailsPostgresQry implements ZeroBalanceCounterDetailsRptQry{
	
	private Log log = LogFactory.getLog(this.getClass().getName());	
	private int i=1;	
	private String datePattern="report.datetimeformat";
	
	/* (non-Javadoc)
	 * @see com.btsl.pretups.channel.reports.businesslogic.ZeroBalanceCounterDetailsRptQry#loadoZeroBalCounterDetailsReportQry(java.sql.Connection, com.btsl.pretups.channel.reports.web.UsersReportModel)
	 */
	@Override
	public PreparedStatement loadoZeroBalCounterDetailsReportQry(Connection con, UsersReportModel usersReportModel) {
	
		 final String methodName ="loadoZeroBalCounterDetailsReportQry";
		 final StringBuilder strBuff = new StringBuilder(); 		
		
		 strBuff.append("SELECT U.user_name , U.msisdn , UTC.transfer_id, TO_CHAR(UTC.entry_date_time, ?) entry_date_time, CAT.category_name, (CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PU.user_name END) parent_name, (CASE U.parent_id WHEN 'ROOT' THEN '' ELSE PU.msisdn END) parent_msisdn,(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE OU.user_name END) owner_name, (CASE U.parent_id WHEN 'ROOT' THEN '' ELSE OU.msisdn END) owner_msisdn, UTC.transaction_type, P.product_name, UTC.TYPE, L1.lookup_name record_type, L2.lookup_name user_status,UTC.threshold_value, UTC.previous_balance, UTC.current_balance FROM USERS U, USER_THRESHOLD_COUNTER UTC,LOOKUPS L1,LOOKUPS L2, CATEGORIES CAT, PRODUCTS P, USER_GEOGRAPHIES UG, GEOGRAPHICAL_DOMAINS GD,USERS PU,USERS OU WHERE UTC.entry_date>=? AND UTC.entry_date<=? AND UTC.network_code=? AND UTC.user_id=CASE ? WHEN 'ALL' THEN UTC.user_id ELSE ? END AND U.user_id=UTC.user_id AND PU.user_id = (CASE U.parent_id WHEN 'ROOT' THEN U.user_id ELSE U.parent_id END) AND OU.USER_ID=U.OWNER_ID AND UTC.RECORD_TYPE=CASE ? WHEN 'ALL' THEN UTC.RECORD_TYPE ELSE ? END AND L1.lookup_type='THRTP' AND L1.lookup_code=UTC.RECORD_TYPE AND UTC.category_code = CASE ? WHEN 'ALL' THEN UTC.category_code ELSE ? END AND CAT.category_code=UTC.category_code AND CAT.domain_code = ? AND p.PRODUCT_CODE=UTC.PRODUCT_CODE AND UG.user_id = UTC.user_id AND L2.lookup_type='URTYP' AND L2.lookup_code=U.status AND UG.grph_domain_code = GD.grph_domain_code AND UG.grph_domain_code IN ( with recursive q as ( SELECT grph_domain_code,status from geographical_domains WHERE grph_domain_code IN (SELECT grph_domain_code FROM user_geographies UG1 WHERE UG1.grph_domain_code = (case ? when 'ALL' then UG1.grph_domain_code else ? end) AND UG1.user_id=?) union all select m.grph_domain_code,m.status from geographical_domains m join q on q.grph_domain_code=m.parent_grph_domain_code ) select q.grph_domain_code from q where status IN('Y', 'S') )");
		 String sqlSelect=strBuff.toString();		
		 PreparedStatement pstmt = null;
		 if (log.isDebugEnabled()) {
	            log.debug("loadZeroBalanceCounterDetailsReport",sqlSelect);
	            log.debug("FromDateTimestamp",usersReportModel.getFromDateTimestamp());
	            log.debug("ToDateTimestamp",usersReportModel.getToDateTimestamp());
	            log.debug("UserId",usersReportModel.getUserID());
	            log.debug("ThresholdType",usersReportModel.getThresholdType());
	            log.debug("ParentCategoryCode",usersReportModel.getParentCategoryCode());
	            log.debug("DomainCode",usersReportModel.getDomainCode());
	            log.debug("ZoneCode",usersReportModel.getZoneCode());
	            log.debug("LoginUserID",usersReportModel.getLoginUserID());
	            log.debug("NetworkCode",usersReportModel.getNetworkCode());	          
	        }
		try {
			 pstmt = con.prepareStatement(sqlSelect);
			 pstmt.setString(i++,Constants.getProperty(datePattern));		
	         pstmt.setTimestamp(i++, usersReportModel.getFromDateTimestamp()); 
	         pstmt.setTimestamp(i++, usersReportModel.getToDateTimestamp()); 
	         pstmt.setString(i++, usersReportModel.getNetworkCode()); 
	         pstmt.setString(i++, usersReportModel.getUserID());
	         pstmt.setString(i++, usersReportModel.getUserID());	        
	         pstmt.setString(i++, usersReportModel.getThresholdType());
	         pstmt.setString(i++, usersReportModel.getThresholdType());
	         pstmt.setString(i++, usersReportModel.getParentCategoryCode());
	         pstmt.setString(i++, usersReportModel.getParentCategoryCode());
	         pstmt.setString(i++, usersReportModel.getDomainCode());
	         pstmt.setString(i++, usersReportModel.getZoneCode());
	         pstmt.setString(i++, usersReportModel.getZoneCode()); 
	         pstmt.setString(i++, usersReportModel.getLoginUserID());		
	         
		} catch (SQLException e) {
			 log.errorTrace(methodName, e);
		}	 		 
		 
		return pstmt;			 
        			
	}

	@Override
	public PreparedStatement loadoZeroBalCounterChnlUserDetailsReportQry(Connection con, UsersReportModel usersReportModel) {
			
		 final String methodName ="loadoZeroBalCounterChnlUserDetailsReportQry";
		 final StringBuilder strBuff = new StringBuilder(); 
		 strBuff.append("select U.user_name, U.msisdn, UTC.transfer_id, TO_CHAR( UTC.entry_date_time, ? ) entry_date_time, CAT.category_name, UTC.transaction_type, P.product_name, UTC.type, L1.lookup_name record_type, L2.lookup_name user_status, UTC.threshold_value, UTC.previous_balance, UTC.current_balance from USERS U, USER_THRESHOLD_COUNTER UTC, LOOKUPS L1, LOOKUPS L2, CATEGORIES CAT, PRODUCTS P, USER_GEOGRAPHIES UG, GEOGRAPHICAL_DOMAINS GD where UTC.ENTRY_DATE >= ? and UTC.entry_date <= ? and UTC.network_code = ? and UTC.user_id in( with recursive q as( select U11.user_id from users U11 where U11.user_id = ? union all select m.user_id from USERS m join q on q.user_id = m.parent_id ) select user_id from q where q.user_id = case ? when 'ALL' then q.user_id else ? end ) and U.user_id = UTC.user_id and UTC.RECORD_TYPE = case ? when 'ALL' then UTC.RECORD_TYPE else ? end and L1.lookup_type = 'THRTP' and L1.lookup_code = UTC.RECORD_TYPE and UTC.category_code = case ? when 'ALL' then UTC.category_code else ? end and CAT.category_code = UTC.category_code and CAT.domain_code = ? and p.PRODUCT_CODE = UTC.PRODUCT_CODE and UG.user_id = UTC.user_id and L2.lookup_type = 'URTYP' and L2.lookup_code = U.status and UG.grph_domain_code = GD.grph_domain_code and UG.grph_domain_code in( with recursive q as( select grph_domain_code, status from geographical_domains where grph_domain_code in( select grph_domain_code from user_geographies UG1 where UG1.grph_domain_code =( case ? when 'ALL' then UG1.grph_domain_code else ? end ) and UG1.user_id = ? ) union all select m.grph_domain_code, m.status from geographical_domains m join q on q.grph_domain_code = m.parent_grph_domain_code ) select q.grph_domain_code from q where status in( 'Y', 'S' ) )");		
		 String sqlSelect=strBuff.toString();		 
		 PreparedStatement pstmt = null;
		 if (log.isDebugEnabled()) {
	            log.debug("loadZeroBalanceCounterDetailsReport",sqlSelect);
	            log.debug("FromDateTimestamp",usersReportModel.getFromDateTimestamp());
	            log.debug("ToDateTimestamp",usersReportModel.getToDateTimestamp());
	            log.debug("UserId",usersReportModel.getUserID());
	            log.debug("ThresholdType",usersReportModel.getThresholdType());
	            log.debug("ParentCategoryCode",usersReportModel.getParentCategoryCode());
	            log.debug("DomainCode",usersReportModel.getDomainCode());
	            log.debug("ZoneCode",usersReportModel.getZoneCode());
	            log.debug("LoginUserID",usersReportModel.getLoginUserID());
	            log.debug("NetworkCode",usersReportModel.getNetworkCode());	          
	        }
		try {
			pstmt = con.prepareStatement(sqlSelect);
			pstmt.setString(i++,Constants.getProperty("report.datetimeformat")); 		
	         pstmt.setTimestamp(i++, usersReportModel.getFromDateTimestamp()); 
	         pstmt.setTimestamp(i++, usersReportModel.getToDateTimestamp());  
	         pstmt.setString(i++, usersReportModel.getNetworkCode()); 
	         pstmt.setString(i++, usersReportModel.getUserID());
	         pstmt.setString(i++, usersReportModel.getUserID());
	         pstmt.setString(i++, usersReportModel.getUserID());
	         pstmt.setString(i++, usersReportModel.getThresholdType());
	         pstmt.setString(i++, usersReportModel.getThresholdType());
	         pstmt.setString(i++, usersReportModel.getParentCategoryCode()); 
	         pstmt.setString(i++, usersReportModel.getParentCategoryCode());
	         pstmt.setString(i++, usersReportModel.getDomainCode());
	         pstmt.setString(i++, usersReportModel.getZoneCode());
	         pstmt.setString(i++, usersReportModel.getZoneCode());
	         pstmt.setString(i++, usersReportModel.getLoginUserID());                   		 		 
	        
		} catch (SQLException e) {
			 log.errorTrace(methodName, e);
		}
		return pstmt;				
		
		 		
         
	}		
}

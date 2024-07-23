package com.btsl.db.query.oracle;

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
public class ZeroBalanceCounterDetailsOracleQry implements ZeroBalanceCounterDetailsRptQry{
	
	private Log log = LogFactory.getLog(this.getClass().getName());		
	private int i=1;	
	private String datePattern="report.datetimeformat";
	
	@Override
	public PreparedStatement loadoZeroBalCounterDetailsReportQry(Connection con, UsersReportModel usersReportModel)	 {
		
		 final String methodName ="loadoZeroBalCounterDetailsReportQry";
		 final StringBuilder strBuff = new StringBuilder();		 
		
		 strBuff.append("SELECT U.user_name , U.msisdn , UTC.transfer_id, TO_CHAR(UTC.entry_date_time, ?) entry_date_time, CAT.category_name, (CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PU.user_name END) parent_name, (CASE U.parent_id WHEN 'ROOT' THEN '' ELSE PU.msisdn END) parent_msisdn,(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE OU.user_name END) owner_name, (CASE U.parent_id WHEN 'ROOT' THEN '' ELSE OU.msisdn END) owner_msisdn, UTC.transaction_type, P.product_name, UTC.TYPE, L1.lookup_name record_type, L2.lookup_name user_status,UTC.threshold_value, UTC.previous_balance, UTC.current_balance FROM USERS U, USER_THRESHOLD_COUNTER UTC,LOOKUPS L1,LOOKUPS L2, CATEGORIES CAT, PRODUCTS P, USER_GEOGRAPHIES UG, GEOGRAPHICAL_DOMAINS GD,USERS PU,USERS OU WHERE UTC.entry_date>=? AND UTC.entry_date<=? AND UTC.network_code=? AND UTC.user_id=CASE ? WHEN 'ALL' THEN UTC.user_id ELSE ? END AND U.user_id=UTC.user_id AND PU.user_id = (CASE U.parent_id WHEN 'ROOT' THEN U.user_id ELSE U.parent_id END) AND OU.USER_ID=U.OWNER_ID AND UTC.RECORD_TYPE=CASE ? WHEN 'ALL' THEN UTC.RECORD_TYPE ELSE ? END AND L1.lookup_type='THRTP' AND L1.lookup_code=UTC.RECORD_TYPE AND UTC.category_code = CASE ? WHEN 'ALL' THEN UTC.category_code ELSE ? END AND CAT.category_code=UTC.category_code AND CAT.domain_code = ? AND p.PRODUCT_CODE=UTC.PRODUCT_CODE AND UG.user_id = UTC.user_id AND L2.lookup_type='URTYP' AND L2.lookup_code=U.status AND UG.grph_domain_code = GD.grph_domain_code AND UG.grph_domain_code IN ( SELECT grph_domain_code FROM GEOGRAPHICAL_DOMAINS GD1 WHERE status IN('Y', 'S') CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START WITH grph_domain_code IN ( SELECT grph_domain_code FROM USER_GEOGRAPHIES ug1 WHERE UG1.grph_domain_code =CASE ? WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END AND UG1.user_id=?) )");
		 String sqlSelect=strBuff.toString();		
		 PreparedStatement pstmt = null;
		 if (log.isDebugEnabled()) {
	            log.debug("loadZeroBalanceCounterDetailsReport",sqlSelect);
	            log.debug("FromDateTimestamp",usersReportModel.getFromDateTimestamp());
	            log.debug("ToDateTimestamp",usersReportModel.getToDateTimestamp());
	            log.debug("FromDate",new java.sql.Date(usersReportModel.getFromDateTime().getTime()));
	            log.debug("ToDate",new java.sql.Date(usersReportModel.getToDateTime().getTime()));
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
	         //pstmt.setTimestamp(i++, usersReportModel.getFromDateTimestamp()); 
	        // pstmt.setTimestamp(i++, usersReportModel.getToDateTimestamp()); 
			 pstmt.setDate(i++, new java.sql.Date(usersReportModel.getFromDateTime().getTime()));
			 pstmt.setDate(i++, new java.sql.Date(usersReportModel.getToDateTime().getTime()));
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
	public PreparedStatement loadoZeroBalCounterChnlUserDetailsReportQry(Connection con, UsersReportModel usersReportModel){
		
		final String methodName ="loadoZeroBalCounterChnlUserDetailsReportQry";
        final StringBuilder strBuff = new StringBuilder();		
		strBuff.append("SELECT U.user_name, U.msisdn, UTC.transfer_id, TO_CHAR( UTC.entry_date_time, ? ) entry_date_time, CAT.category_name, UTC.transaction_type, P.product_name, UTC.type, L1.lookup_name record_type, L2.lookup_name user_status, UTC.threshold_value, UTC.previous_balance, UTC.current_balance FROM USERS U, USER_THRESHOLD_COUNTER UTC, LOOKUPS L1, LOOKUPS L2, CATEGORIES CAT, PRODUCTS P, USER_GEOGRAPHIES UG, GEOGRAPHICAL_DOMAINS GD WHERE UTC.ENTRY_DATE >= TO_DATE(?,?) AND UTC.entry_date <= TO_DATE(?,?) AND UTC.network_code = ? AND UTC.user_id IN( SELECT U11.user_id FROM users U11 WHERE U11.user_id = CASE ? WHEN 'ALL' THEN U11.user_id ELSE ? END CONNECT BY PRIOR U11.user_id = U11.parent_id START WITH U11.user_id = ? ) AND U.user_id = UTC.user_id AND UTC.RECORD_TYPE = CASE ? WHEN 'ALL' THEN UTC.RECORD_TYPE ELSE ? END AND L1.lookup_type = 'THRTP' AND L1.lookup_code = UTC.RECORD_TYPE AND UTC.category_code = CASE ? WHEN 'ALL' THEN UTC.category_code ELSE ? END AND CAT.category_code = UTC.category_code AND CAT.domain_code = ? AND p.PRODUCT_CODE = UTC.PRODUCT_CODE AND UG.user_id = UTC.user_id AND L2.lookup_type = 'URTYP' AND L2.lookup_code = U.status AND UG.grph_domain_code = GD.grph_domain_code AND UG.grph_domain_code IN( SELECT grph_domain_code FROM GEOGRAPHICAL_DOMAINS GD1 WHERE status IN( 'Y', 'S' ) CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START WITH grph_domain_code IN( SELECT grph_domain_code FROM USER_GEOGRAPHIES ug1 WHERE UG1.grph_domain_code = CASE ? WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END AND UG1.user_id = ? ) )");				
		String sqlSelect=strBuff.toString();		
		PreparedStatement pstmt = null;
		if (log.isDebugEnabled()) {
            log.debug("loadZeroBalanceCounterDetailsReport",sqlSelect);
            log.debug("FromDateTimestamp",usersReportModel.getFromDateTimestamp());
            log.debug("ToDateTimestamp",usersReportModel.getToDateTimestamp());
            log.debug("FromDate",new java.sql.Date(usersReportModel.getFromDateTime().getTime()));
            log.debug("ToDate",new java.sql.Date(usersReportModel.getToDateTime().getTime()));
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
	        pstmt.setDate(i++, new java.sql.Date(usersReportModel.getFromDateTime().getTime()));
	        pstmt.setString(i++,Constants.getProperty(datePattern));
	        pstmt.setDate(i++, new java.sql.Date(usersReportModel.getToDateTime().getTime()));
	        pstmt.setString(i++,Constants.getProperty(datePattern));
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

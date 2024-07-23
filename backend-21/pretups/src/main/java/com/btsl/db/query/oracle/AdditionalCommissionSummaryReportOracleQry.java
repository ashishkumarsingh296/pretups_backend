package com.btsl.db.query.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.reports.businesslogic.AdditionalCommissionSummaryReportQry;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.channel.reports.web.UsersReportModel;

public class AdditionalCommissionSummaryReportOracleQry implements AdditionalCommissionSummaryReportQry{
	
	public static final Log log = LogFactory.getLog(AdditionalCommissionSummaryReportOracleQry.class.getName());
	private static final String LOGINID_LOG = "Entered loginUserID: ";
	private static final String NETWORK_LOG = " NetworkCode: ";
	private static final String DOMAIN_LOG = " DomainCode: ";
	private static final String PARRENTCATCODE_LOG = " ParentCatCode: ";
	private static final String FROMDATE_LOG = " FromDate: ";
	private static final String TODATE_LOG = " ToDate: ";
	private static final String SERVICE_TYPE_LOG = " ServiceType: ";
	private static final String ZONECODE_LOG = " ZoneCode: ";
	private static final String EXCEPTION_KEY = "Exception: ";
	private static final String SQLEXCEPTION_KEY = "SQLException: ";
	private static final String SELECTQUERY_KEY = "SelectQuery: ";
	private static final String DATEFORMAT_KEY = "p2p.report.monthwisep2ptransactionsummary.month.format";
	private static final String CLAS_NAME = "AdditionalCommissionSummaryReportOracleQry";

	@Override
	public PreparedStatement loadAdditionalCommisionDetailsOperatorDailyQry(
			Connection con, UsersReportModel usersReportModel) {
		String methodName = "loadAdditionalCommisionDetailsOperatorDailyQry";
		java.sql.Date fromDate = null;
	    java.sql.Date toDate = null;
	    try {
			fromDate = BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(usersReportModel.getRptfromDate()));
			toDate = BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(usersReportModel.getRpttoDate()));
		} catch (ParseException e1) {
			if (log.isDebugEnabled()) {
				log.debug(CLAS_NAME + methodName, e1);
			}
		}
	    
		if(log.isDebugEnabled()){ 
			log.debug(methodName,LOGINID_LOG+usersReportModel.getLoginUserID()+NETWORK_LOG+usersReportModel.getNetworkCode()+DOMAIN_LOG+usersReportModel.getDomainCode()+
					PARRENTCATCODE_LOG+usersReportModel.getParentCategoryCode()+FROMDATE_LOG+usersReportModel.getRptfromDate()+TODATE_LOG+usersReportModel.getRpttoDate()+
					SERVICE_TYPE_LOG+usersReportModel.getServiceType()+ZONECODE_LOG+usersReportModel.getZoneCode());
	     } 
		
		StringBuilder selectQueryBuff = new StringBuilder();
		selectQueryBuff.append(" SELECT SUM(DCTD.transaction_count) transaction_count, SUM(DCTD.differential_amount) differential_amount, to_char(DCTD.trans_date, ?) trans_date, u.user_id,U.user_name, U.login_id, U.msisdn, C.category_name, ");
		selectQueryBuff.append(" GD.grph_domain_name, PU.user_id,(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PU.user_name END) parent_name,(CASE  WHEN U.parent_id = 'ROOT' THEN '' ELSE PU.msisdn END) parent_msisdn,(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PC.category_name END) parent_cat, (CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PGD.grph_domain_name END) parent_geo, ");  
		selectQueryBuff.append(" (CASE WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OU.user_name END) owner_name,(CASE  WHEN U.parent_id = 'ROOT' THEN ''  WHEN U.user_id = OU.user_id THEN '' ELSE OU.msisdn END) owner_msisdn, (CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OC.category_name END) owner_category,(CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OGD.grph_domain_name END)  owner_geo,C.sequence_no, ST.NAME service_type_name, "); 
		selectQueryBuff.append(" GU.user_id,GU.user_name grand_name, GU.msisdn grand_msisdn,GGD.grph_domain_name grand_geo_domain,GC.category_name grand_category,stsm.selector_name "); 
		selectQueryBuff.append(" FROM DAILY_C2S_TRANS_DETAILS DCTD, USERS U, SERVICE_TYPE ST, CATEGORIES C, GEOGRAPHICAL_DOMAINS GD,USER_GEOGRAPHIES UG, USERS PU, USERS OU, CATEGORIES PC, CATEGORIES OC, "); 
		selectQueryBuff.append(" USER_GEOGRAPHIES PUG, GEOGRAPHICAL_DOMAINS PGD, USER_GEOGRAPHIES OUG, GEOGRAPHICAL_DOMAINS OGD,USERS GU, USER_GEOGRAPHIES GUG, CATEGORIES GC,GEOGRAPHICAL_DOMAINS GGD,SERVICE_TYPE_SELECTOR_MAPPING stsm "); 
		selectQueryBuff.append(" WHERE DCTD.user_id=U.user_id AND U.category_code=C.category_code AND DCTD.SERVICE_TYPE=ST.SERVICE_TYPE AND UG.user_id=U.user_id AND UG.grph_domain_code = GD.grph_domain_code  AND PUG.grph_domain_code = PGD.grph_domain_code "); 
		selectQueryBuff.append(" AND PUG.user_id=PU.user_id AND OUG.grph_domain_code = OGD.grph_domain_code AND OUG.user_id=OU.user_id AND PU.user_id=CASE U.parent_id WHEN 'ROOT' THEN U.user_id ELSE U.parent_id END  AND PU.category_code=PC.category_code "); 
		selectQueryBuff.append(" AND OU.user_id=U.owner_id AND OU.category_code=OC.category_code AND GUG.grph_domain_code = GGD.grph_domain_code AND GUG.user_id=GU.user_id AND GU.user_id=CASE PU.parent_id WHEN 'ROOT' THEN PU.user_id ELSE PU.parent_id END AND GU.category_code=GC.category_code "); 
		selectQueryBuff.append(" AND C.domain_code=? AND U.network_code=? AND U.category_code =CASE ? WHEN  'ALL' THEN U.category_code ELSE ? END AND DCTD.SERVICE_TYPE=CASE ? WHEN  'ALL' THEN DCTD.SERVICE_TYPE ELSE ? END "); 
		selectQueryBuff.append(" AND DCTD.trans_date>=? AND DCTD.trans_date<=? AND DCTD.service_type=stsm.service_type AND DCTD.SUB_SERVICE=stsm.selector_code  AND differential_amount <>0  AND UG.grph_domain_code IN (SELECT GD1.grph_domain_code  "); 
		selectQueryBuff.append(" FROM GEOGRAPHICAL_DOMAINS GD1 WHERE GD1.status IN('Y', 'S') CONNECT BY PRIOR GD1.grph_domain_code = GD1.parent_grph_domain_code START WITH GD1.grph_domain_code IN (SELECT UG1.grph_domain_code FROM USER_GEOGRAPHIES UG1 "); 
		selectQueryBuff.append(" WHERE UG1.grph_domain_code = (CASE ? WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END) AND UG1.user_id=?)) group by  DCTD.trans_date , u.user_id,U.user_name, U.login_id, U.msisdn, C.category_name, "); 
		selectQueryBuff.append(" GD.grph_domain_name,PU.user_id,PU.user_name ,PU.msisdn ,PC.category_name , PGD.grph_domain_name , OU.user_name ,OU.msisdn , OC.category_name , OGD.grph_domain_name ,C.sequence_no, ST.NAME , GU.user_id,GU.user_name , GU.msisdn ,GGD.grph_domain_name ,GC.category_name,stsm.selector_name,U.parent_id,OU.user_id "); 
		 
		
		String sqlSelect = selectQueryBuff.toString();

		if(log.isDebugEnabled()){
			log.debug(methodName, SELECTQUERY_KEY+sqlSelect);
		}

		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(sqlSelect);
			int i = 1;
			pstmt.setString(i++,Constants.getProperty(DATEFORMAT_KEY));
			pstmt.setString(i++,usersReportModel.getDomainCode());
			pstmt.setString(i++,usersReportModel.getNetworkCode());
			pstmt.setString(i++,usersReportModel.getParentCategoryCode());
			pstmt.setString(i++,usersReportModel.getParentCategoryCode());
			pstmt.setString(i++, usersReportModel.getServiceType());
			pstmt.setString(i++, usersReportModel.getServiceType());
			pstmt.setDate(i++, fromDate);
			pstmt.setDate(i++, toDate);
			pstmt.setString(i++, usersReportModel.getZoneCode());
			pstmt.setString(i++, usersReportModel.getZoneCode());
			pstmt.setString(i, usersReportModel.getLoginUserID());

		} catch (SQLException e) {
			log.error(methodName, SQLEXCEPTION_KEY+e.getMessage());
			log.errorTrace(methodName, e);
		} catch (Exception e) {
			log.error(methodName, EXCEPTION_KEY+e.getMessage());
			log.errorTrace(methodName, e);
		} 
		return pstmt;

	}

	@Override
	public PreparedStatement loadAdditionalCommisionDetailsOperatorMonthlyQry(
			Connection con, UsersReportModel usersReportModel) {
		String methodName = "loadAdditionalCommisionDetailsOperatorMonthlyQry";
		java.sql.Date fromDate = null;
	    java.sql.Date toDate = null;
	    try {
			fromDate = BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(usersReportModel.getRptfromDate()));
			toDate = BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(usersReportModel.getRpttoDate()));
		} catch (ParseException e1) {
			if (log.isDebugEnabled()) {
				log.debug(CLAS_NAME + methodName, e1);
			}
		}
		
		if(log.isDebugEnabled()){ 
			log.debug(methodName,LOGINID_LOG+usersReportModel.getLoginUserID()+NETWORK_LOG+usersReportModel.getNetworkCode()+DOMAIN_LOG+usersReportModel.getDomainCode()+
					PARRENTCATCODE_LOG+usersReportModel.getParentCategoryCode()+FROMDATE_LOG+usersReportModel.getRptfromDate()+TODATE_LOG+usersReportModel.getRpttoDate()+
					SERVICE_TYPE_LOG+usersReportModel.getServiceType()+ZONECODE_LOG+usersReportModel.getZoneCode());
	     } 
		
		StringBuilder selectQueryBuff = new StringBuilder();
		selectQueryBuff.append(" SELECT sum(MCTD.transaction_count)transaction_count, sum(MCTD.differential_amount) differential_amount, to_char(MCTD.trans_date,?) trans_date, u.user_id, U.user_name, U.login_id, U.msisdn, C.category_name, GD.grph_domain_name, ");
		selectQueryBuff.append(" PU.user_id,(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PU.user_name END) parent_name,(CASE  WHEN U.parent_id = 'ROOT' THEN '' ELSE PU.msisdn END) parent_msisdn,(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PC.category_name END) parent_cat, (CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PGD.grph_domain_name END) parent_geo, ");  
		selectQueryBuff.append(" (CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OU.user_name END) owner_name,(CASE  WHEN U.parent_id = 'ROOT' THEN ''  WHEN U.user_id = OU.user_id THEN '' ELSE OU.msisdn END)  owner_msisdn, (CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OC.category_name END) owner_cat,(CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OGD.grph_domain_name END)  owner_geo , "); 
		selectQueryBuff.append(" C.sequence_no, ST.NAME service_type_name, GU.user_id,GU.user_name grand_name,  GU.msisdn grand_msisdn,GGD.grph_domain_name grand_geo_domain,GC.category_name grand_category,stsm.selector_name "); 
		selectQueryBuff.append(" FROM MONTHLY_C2S_TRANS_DETAILS MCTD, USERS U, SERVICE_TYPE ST, CATEGORIES C, GEOGRAPHICAL_DOMAINS GD,USER_GEOGRAPHIES UG, USERS PU, USERS OU, CATEGORIES PC, CATEGORIES OC, USER_GEOGRAPHIES PUG, GEOGRAPHICAL_DOMAINS PGD, USER_GEOGRAPHIES OUG, GEOGRAPHICAL_DOMAINS OGD,USERS GU, USER_GEOGRAPHIES GUG, CATEGORIES GC,GEOGRAPHICAL_DOMAINS GGD,SERVICE_TYPE_SELECTOR_MAPPING stsm "); 
		selectQueryBuff.append(" WHERE MCTD.user_id=U.user_id AND U.category_code=C.category_code AND MCTD.SERVICE_TYPE=ST.SERVICE_TYPE AND UG.user_id=U.user_id AND UG.grph_domain_code = GD.grph_domain_code AND PUG.grph_domain_code = PGD.grph_domain_code AND PUG.user_id=PU.user_id AND OUG.grph_domain_code = OGD.grph_domain_code AND OUG.user_id=OU.user_id "); 
		selectQueryBuff.append(" AND PU.user_id=CASE U.parent_id WHEN 'ROOT' THEN U.user_id ELSE U.parent_id END AND PU.category_code=PC.category_code AND OU.user_id=U.owner_id AND OU.category_code=OC.category_code AND GUG.grph_domain_code = GGD.grph_domain_code AND GUG.user_id=GU.user_id AND GU.user_id=CASE PU.parent_id WHEN 'ROOT' THEN PU.user_id ELSE PU.parent_id END "); 
		selectQueryBuff.append(" AND GU.category_code=GC.category_code AND C.domain_code=? AND U.network_code=? AND U.category_code =CASE ? WHEN  'ALL' THEN U.category_code ELSE ? END AND MCTD.SERVICE_TYPE=CASE ? WHEN  'ALL' THEN MCTD.SERVICE_TYPE ELSE ? END AND MCTD.trans_date>=? "); 
		selectQueryBuff.append(" AND MCTD.trans_date<=? AND MCTD.service_type=stsm.service_type AND MCTD.sub_service=stsm.selector_code AND MCTD.TRANSACTION_COUNT >0 AND differential_amount <>0 AND UG.grph_domain_code IN (SELECT GD1.grph_domain_code  "); 
		selectQueryBuff.append(" FROM GEOGRAPHICAL_DOMAINS GD1 WHERE GD1.status IN('Y', 'S') CONNECT BY PRIOR GD1.grph_domain_code = GD1.parent_grph_domain_code START WITH GD1.grph_domain_code IN (SELECT UG1.grph_domain_code FROM USER_GEOGRAPHIES UG1 WHERE UG1.grph_domain_code = (CASE ? WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END) "); 
		selectQueryBuff.append(" AND UG1.user_id=?)) group by MCTD.trans_date , u.user_id, U.user_name, U.login_id, U.msisdn, C.category_name, GD.grph_domain_name, PU.user_id,PU.user_name ,PU.msisdn ,PC.category_name , PGD.grph_domain_name ,  "); 
		selectQueryBuff.append(" OU.user_name ,OU.msisdn , OC.category_name , OGD.grph_domain_name , C.sequence_no, ST.NAME , GU.user_id,GU.user_name , GU.msisdn ,GGD.grph_domain_name ,GC.category_name,stsm.selector_name,U.parent_id,OU.user_id "); 		 
		
		String sqlSelect = selectQueryBuff.toString();

		if(log.isDebugEnabled()){
			log.debug(methodName, SELECTQUERY_KEY+sqlSelect);
		}

		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(sqlSelect);
			int i = 1;
			pstmt.setString(i++,Constants.getProperty(DATEFORMAT_KEY));
			pstmt.setString(i++,usersReportModel.getDomainCode());
			pstmt.setString(i++,usersReportModel.getNetworkCode());
			pstmt.setString(i++,usersReportModel.getParentCategoryCode());
			pstmt.setString(i++,usersReportModel.getParentCategoryCode());
			pstmt.setString(i++, usersReportModel.getServiceType());
			pstmt.setString(i++, usersReportModel.getServiceType());
			pstmt.setDate(i++, fromDate);
			pstmt.setDate(i++, toDate);
			pstmt.setString(i++, usersReportModel.getZoneCode());
			pstmt.setString(i++, usersReportModel.getZoneCode());
			pstmt.setString(i, usersReportModel.getLoginUserID());

		} catch (SQLException e) {
			log.error(methodName, SQLEXCEPTION_KEY+e.getMessage());
			log.errorTrace(methodName, e);
		} catch (Exception e) {
			log.error(methodName, EXCEPTION_KEY+e.getMessage());
			log.errorTrace(methodName, e);
		} 
		return pstmt;

	}

	@Override
	public PreparedStatement loadAdditionalCommisionDetailsChannelDailyQry(
			Connection con, UsersReportModel usersReportModel) {
		String methodName = "loadAdditionalCommisionDetailsChannelDailyQry";
		java.sql.Date fromDate = null;
	    java.sql.Date toDate = null;
	    try {
			fromDate = BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(usersReportModel.getRptfromDate()));
			toDate = BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(usersReportModel.getRpttoDate()));
		} catch (ParseException e1) {
			if (log.isDebugEnabled()) {
				log.debug(CLAS_NAME + methodName, e1);
			}
		}
		
		if(log.isDebugEnabled()){ 
			log.debug(methodName,LOGINID_LOG+usersReportModel.getLoginUserID()+NETWORK_LOG+usersReportModel.getNetworkCode()+DOMAIN_LOG+usersReportModel.getDomainCode()+
					PARRENTCATCODE_LOG+usersReportModel.getParentCategoryCode()+FROMDATE_LOG+usersReportModel.getRptfromDate()+TODATE_LOG+usersReportModel.getRpttoDate()+
					SERVICE_TYPE_LOG+usersReportModel.getServiceType()+ZONECODE_LOG+usersReportModel.getZoneCode());
	     } 
		
		StringBuilder selectQueryBuff = new StringBuilder();
		selectQueryBuff.append(" SELECT sum(DCTD.transaction_count)transaction_count, sum(DCTD.differential_amount)differential_amount, to_char(DCTD.trans_date, ?) trans_date, u.user_id,U.user_name, U.login_id, U.msisdn, C.category_name, GD.grph_domain_name, PU.user_id,PU.user_name parent_name,PU.msisdn parent_msisdn,PC.category_name parent_cat, PGD.grph_domain_name parent_geo, ");
		selectQueryBuff.append(" OU.user_name owner_name,OU.msisdn owner_msisdn, OC.category_name owner_category, OGD.grph_domain_name owner_geo, C.sequence_no, ST.NAME service_type_name, GU.user_id,GU.user_name grand_name, GU.msisdn grand_msisdn,GGD.grph_domain_name grand_geo_domain,GC.category_name grand_category,stsm.selector_name ");  
		selectQueryBuff.append(" FROM  (SELECT U11.user_id FROM USERS U11 CONNECT BY PRIOR U11.user_id = U11.parent_id START WITH U11.user_id=?)  X, DAILY_C2S_TRANS_DETAILS DCTD, USERS U, SERVICE_TYPE ST, CATEGORIES C, GEOGRAPHICAL_DOMAINS GD,USER_GEOGRAPHIES UG, USERS PU, USERS OU, CATEGORIES PC, CATEGORIES OC, USER_GEOGRAPHIES PUG, GEOGRAPHICAL_DOMAINS PGD, USER_GEOGRAPHIES OUG, GEOGRAPHICAL_DOMAINS OGD,USERS GU, "); 
		selectQueryBuff.append(" USER_GEOGRAPHIES GUG, CATEGORIES GC,GEOGRAPHICAL_DOMAINS GGD,SERVICE_TYPE_SELECTOR_MAPPING stsm "); 
		selectQueryBuff.append(" WHERE DCTD.user_id=U.user_id AND X.user_id=U.user_id AND U.category_code=C.category_code AND DCTD.SERVICE_TYPE=ST.SERVICE_TYPE AND UG.user_id=U.user_id AND UG.grph_domain_code = GD.grph_domain_code AND PUG.grph_domain_code = PGD.grph_domain_code AND PUG.user_id=PU.user_id AND OUG.grph_domain_code = OGD.grph_domain_code "); 
		selectQueryBuff.append(" AND OUG.user_id=OU.user_id AND PU.user_id=CASE U.parent_id WHEN 'ROOT' THEN U.user_id ELSE U.parent_id END AND PU.category_code=PC.category_code AND OU.user_id=U.owner_id AND OU.category_code=OC.category_code AND GUG.grph_domain_code = GGD.grph_domain_code AND GUG.user_id=GU.user_id AND GU.user_id=CASE PU.parent_id WHEN 'ROOT' THEN PU.user_id ELSE PU.parent_id END "); 
		selectQueryBuff.append(" AND GU.category_code=GC.category_code AND C.domain_code=? AND U.network_code=? AND U.category_code =CASE ? WHEN 'ALL' THEN U.category_code ELSE ? END AND DCTD.SERVICE_TYPE=CASE ? WHEN 'ALL' THEN DCTD.SERVICE_TYPE ELSE ? END AND DCTD.trans_date>=?  AND DCTD.trans_date<=? "); 
		selectQueryBuff.append(" AND DCTD.service_type=stsm.service_type  AND DCTD.SUB_SERVICE = stsm.selector_code AND DCTD.TRANSACTION_COUNT >0 AND differential_amount <>0 AND UG.grph_domain_code IN (SELECT GD1.grph_domain_code FROM GEOGRAPHICAL_DOMAINS GD1  "); 
		selectQueryBuff.append(" WHERE GD1.status IN('Y', 'S') CONNECT BY PRIOR GD1.grph_domain_code = GD1.parent_grph_domain_code START WITH GD1.grph_domain_code IN (SELECT UG1.grph_domain_code FROM USER_GEOGRAPHIES UG1 WHERE UG1.grph_domain_code = (CASE ? WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END) AND UG1.user_id=?))  "); 
		selectQueryBuff.append(" group by DCTD.trans_date , u.user_id,U.user_name, U.login_id, U.msisdn, C.category_name, GD.grph_domain_name, PU.user_id,PU.user_name ,PU.msisdn ,PC.category_name , PGD.grph_domain_name ,  OU.user_name ,OU.msisdn , OC.category_name , OGD.grph_domain_name ,  C.sequence_no, ST.NAME , GU.user_id,GU.user_name ,  GU.msisdn ,GGD.grph_domain_name ,GC.category_name,stsm.selector_name "); 
		
		String sqlSelect = selectQueryBuff.toString();

		if(log.isDebugEnabled()){
			log.debug(methodName, SELECTQUERY_KEY+sqlSelect);
		}

		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(sqlSelect);
			int i = 1;
			pstmt.setString(i++,Constants.getProperty(DATEFORMAT_KEY));
			pstmt.setString(i++, usersReportModel.getLoginUserID());
			pstmt.setString(i++,usersReportModel.getDomainCode());
			pstmt.setString(i++,usersReportModel.getNetworkCode());
			pstmt.setString(i++,usersReportModel.getParentCategoryCode());
			pstmt.setString(i++,usersReportModel.getParentCategoryCode());
			pstmt.setString(i++, usersReportModel.getServiceType());
			pstmt.setString(i++, usersReportModel.getServiceType());
			pstmt.setDate(i++, fromDate);
			pstmt.setDate(i++, toDate);
			pstmt.setString(i++, usersReportModel.getZoneCode());
			pstmt.setString(i++, usersReportModel.getZoneCode());
			pstmt.setString(i, usersReportModel.getLoginUserID());

		} catch (SQLException e) {
			log.error(methodName, SQLEXCEPTION_KEY+e.getMessage());
			log.errorTrace(methodName, e);
		} catch (Exception e) {
			log.error(methodName, EXCEPTION_KEY+e.getMessage());
			log.errorTrace(methodName, e);
		} 
		return pstmt;

	}

	@Override
	public PreparedStatement loadAdditionalCommisionDetailsChannelMonthlyQry(
			Connection con, UsersReportModel usersReportModel) {
		String methodName = "loadAdditionalCommisionDetailsChannelMonthlyQry";
		java.sql.Date fromDate = null;
	    java.sql.Date toDate = null;
	    try {
			fromDate = BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(usersReportModel.getRptfromDate()));
			toDate = BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(usersReportModel.getRpttoDate()));
		} catch (ParseException e1) {
			if (log.isDebugEnabled()) {
				log.debug(CLAS_NAME + methodName, e1);
			}
		}
		
		if(log.isDebugEnabled()){ 
			log.debug(methodName,LOGINID_LOG+usersReportModel.getLoginUserID()+NETWORK_LOG+usersReportModel.getNetworkCode()+DOMAIN_LOG+usersReportModel.getDomainCode()+
					PARRENTCATCODE_LOG+usersReportModel.getParentCategoryCode()+FROMDATE_LOG+usersReportModel.getRptfromDate()+TODATE_LOG+usersReportModel.getRpttoDate()+
					SERVICE_TYPE_LOG+usersReportModel.getServiceType()+ZONECODE_LOG+usersReportModel.getZoneCode());
	     } 
		
		StringBuilder selectQueryBuff = new StringBuilder();
		selectQueryBuff.append(" SELECT  sum(MCTD.transaction_count) transaction_count, sum(MCTD.differential_amount) differential_amount, to_char(MCTD.trans_date,?) trans_date, u.user_id, U.user_name, U.login_id, U.msisdn, C.category_name, GD.grph_domain_name,  PU.user_id,(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PU.user_name END) parent_name,(CASE U.parent_id WHEN 'ROOT' THEN '' ELSE PU.msisdn END) parent_msisdn,(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PC.category_name END) parent_category,(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PGD.grph_domain_name END)  parent_geography, ");
		selectQueryBuff.append(" (CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OU.user_name END) owner_name,(CASE  WHEN U.parent_id = 'ROOT' THEN '' WHEN U.user_id = OU.user_id THEN '' ELSE OU.msisdn END)  owner_msisdn,(CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OC.category_name END)  owner_category,(CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE  OGD.grph_domain_name END)  owner_geo, C.sequence_no, ST.NAME service_type_name, GU.user_id,(CASE WHEN PU.parent_id = 'ROOT' or U.parent_id = 'ROOT' THEN N'' ELSE GU.user_name END) grand_name, ");  
		selectQueryBuff.append(" (CASE WHEN PU.parent_id = 'ROOT' or U.parent_id = 'ROOT' THEN '' ELSE GU.msisdn END) grand_msisdn, (CASE WHEN PU.parent_id = 'ROOT' or U.parent_id = 'ROOT' THEN N'' ELSE GGD.grph_domain_name END) grand_geo_domain,(CASE WHEN PU.parent_id = 'ROOT' or U.parent_id = 'ROOT' THEN N'' ELSE GC.category_name END) grand_category,stsm.selector_name FROM (SELECT U11.user_id  "); 
		selectQueryBuff.append(" FROM USERS U11 CONNECT BY PRIOR U11.user_id = U11.parent_id START WITH U11.user_id=?) X, MONTHLY_C2S_TRANS_DETAILS MCTD, USERS U, SERVICE_TYPE ST, CATEGORIES C, GEOGRAPHICAL_DOMAINS GD,USER_GEOGRAPHIES UG, USERS PU, USERS OU, CATEGORIES PC, CATEGORIES OC,  USER_GEOGRAPHIES PUG, GEOGRAPHICAL_DOMAINS PGD, USER_GEOGRAPHIES OUG, GEOGRAPHICAL_DOMAINS OGD,USERS GU, USER_GEOGRAPHIES GUG, CATEGORIES GC,GEOGRAPHICAL_DOMAINS GGD,SERVICE_TYPE_SELECTOR_MAPPING stsm "); 
		selectQueryBuff.append(" WHERE MCTD.user_id=U.user_id AND X.user_id=U.user_id AND U.category_code=C.category_code AND MCTD.SERVICE_TYPE=ST.SERVICE_TYPE AND UG.user_id=U.user_id AND UG.grph_domain_code = GD.grph_domain_code  AND PUG.grph_domain_code = PGD.grph_domain_code AND PUG.user_id=PU.user_id AND OUG.grph_domain_code = OGD.grph_domain_code AND OUG.user_id=OU.user_id AND PU.user_id=CASE U.parent_id WHEN 'ROOT' THEN U.user_id ELSE U.parent_id END "); 
		selectQueryBuff.append(" AND PU.category_code=PC.category_code AND OU.user_id=U.owner_id AND OU.category_code=OC.category_code AND GUG.grph_domain_code = GGD.grph_domain_code AND GUG.user_id=GU.user_id AND GU.user_id=CASE PU.parent_id WHEN 'ROOT' THEN PU.user_id ELSE PU.parent_id END AND GU.category_code=GC.category_code AND C.domain_code=?  AND U.network_code=? AND U.category_code =CASE ? WHEN  'ALL' THEN U.category_code ELSE ? END "); 
		selectQueryBuff.append(" AND MCTD.SERVICE_TYPE=CASE ? WHEN  'ALL' THEN MCTD.SERVICE_TYPE ELSE ? END AND MCTD.trans_date>=? AND MCTD.trans_date<=? AND MCTD.service_type=stsm.service_type AND MCTD.SUB_SERVICE =stsm.selector_code AND MCTD.transaction_count >0 AND differential_amount<>0 AND UG.grph_domain_code IN (SELECT GD1.grph_domain_code FROM  GEOGRAPHICAL_DOMAINS GD1  "); 
		selectQueryBuff.append(" WHERE GD1.status IN('Y', 'S') CONNECT BY PRIOR GD1.grph_domain_code = GD1.parent_grph_domain_code START WITH GD1.grph_domain_code IN (SELECT UG1.grph_domain_code FROM USER_GEOGRAPHIES UG1 WHERE UG1.grph_domain_code = (CASE ? WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END) AND UG1.user_id=?)) group BY MCTD.trans_date , u.user_id, U.user_name, U.login_id, U.msisdn, C.category_name, GD.grph_domain_name,  "); 
		selectQueryBuff.append(" PU.user_id,PU.user_name ,PU.msisdn ,PC.category_name , PGD.grph_domain_name , OU.user_name ,OU.msisdn , OC.category_name, OGD.grph_domain_name , C.sequence_no, ST.NAME , GU.user_id,GU.user_name, GU.msisdn ,GGD.grph_domain_name ,GC.category_name,stsm.selector_name,PU.parent_id,U.parent_id,OU.user_id  "); 		
		String sqlSelect = selectQueryBuff.toString();

		if(log.isDebugEnabled()){
			log.debug(methodName, SELECTQUERY_KEY+sqlSelect);
		}

		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(sqlSelect);
			int i = 1;
			pstmt.setString(i++,Constants.getProperty(DATEFORMAT_KEY));
			pstmt.setString(i++, usersReportModel.getLoginUserID());
			pstmt.setString(i++,usersReportModel.getDomainCode());
			pstmt.setString(i++,usersReportModel.getNetworkCode());
			pstmt.setString(i++,usersReportModel.getParentCategoryCode());
			pstmt.setString(i++,usersReportModel.getParentCategoryCode());
			pstmt.setString(i++, usersReportModel.getServiceType());
			pstmt.setString(i++, usersReportModel.getServiceType());
			pstmt.setDate(i++, fromDate);
			pstmt.setDate(i++, toDate);
			pstmt.setString(i++, usersReportModel.getZoneCode());
			pstmt.setString(i++, usersReportModel.getZoneCode());
			pstmt.setString(i, usersReportModel.getLoginUserID());

		} catch (SQLException e) {
			log.error(methodName, SQLEXCEPTION_KEY+e.getMessage());
			log.errorTrace(methodName, e);
		} catch (Exception e) {
			log.error(methodName, EXCEPTION_KEY+e.getMessage());
			log.errorTrace(methodName, e);
		} 
		return pstmt;

	}

}

package com.btsl.db.query.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.reports.businesslogic.OperationSummaryRptQry;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * @author anubhav.pandey1
 *
 */
public class OperationSummaryReportOracleQry implements OperationSummaryRptQry {
	
	public static final Log log = LogFactory.getLog(OperationSummaryReportOracleQry.class.getName());
	private static final String QUERYSELECT = " QUERY SELECT = ";
	private static final String REPORTDATAFORMAT = "report.onlydateformat";
	@Override
	public PreparedStatement loadOperationSummaryChannelUserMainReport(	UsersReportModel thisForm, Connection con) {
		 StringBuilder strBuff = new StringBuilder();
		final String methodName = "loadOperationSummaryChannelUserMainReport";
		if(log.isDebugEnabled()){ 
			log.debug(methodName,"Enter loginUserID "+thisForm.getLoginUserID()+" NetworkCODE "+thisForm.getNetworkCode()+" DomainCODE "+thisForm.getDomainCode()+
					" FROM date "+thisForm.getRptfromDate()+" To Date "+thisForm.getRpttoDate()+ "Category Code"+thisForm.getParentCategoryCode()+
					" UserID "+thisForm.getUserID()+" ZoneCode "+thisForm.getZoneCode());
	     
	     } 
		
		java.sql.Date fromDate = null;
        java.sql.Date toDate = null;
        PreparedStatement pstmt = null;
        try {
			fromDate = BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(thisForm.getRptfromDate()));
			toDate = BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(thisForm.getRpttoDate()));

			strBuff.append("SELECT TO_CHAR(DC.trans_date, ? ) trans_date, U.user_name,U.msisdn, DC.product_code, ");
			
			strBuff = BTSLUtil.appendZero(strBuff, "DC.opening_balance", "opening_balance").append(" , ");
			
			strBuff = BTSLUtil.appendZero(strBuff, "DC.c2c_return_in_amount + DC.c2c_withdraw_in_amount", "c2c_return_plus_with_in_amount").append(" , ");
			
			
			strBuff = BTSLUtil.appendZero(strBuff, "DC.o2c_return_out_amount + DC.o2c_withdraw_out_amount", "o2c_return_plus_with_out_amount").append(" , ");
			strBuff = BTSLUtil.appendZero(strBuff, "DC.c2c_return_out_amount + DC.c2c_withdraw_out_amount", "c2c_return_plus_with_out_amount").append(" , ");
			strBuff.append("(SUM (DC.c2c_return_in_count + DC.c2c_withdraw_in_count)) as c2c_return_plus_with_in_count,   ");
			strBuff.append("(SUM (DC.o2c_return_out_count + DC.o2c_withdraw_out_count)) as o2c_return_plus_with_out_count,   ");
			strBuff.append("(SUM (DC.c2c_return_out_count + DC.c2c_withdraw_out_count)) as c2c_return_plus_with_out_count, ");
			strBuff = BTSLUtil.appendZero(strBuff, "DC.c2c_transfer_in_amount", "c2c_transfer_in_amount").append(" , ");
			strBuff.append("(DC.c2c_transfer_in_count) c2c_transfer_in_count, ");
			strBuff = BTSLUtil.appendZero(strBuff, "DC.c2c_transfer_out_amount", "c2c_transfer_out_amount").append(" , ");
			strBuff.append("(DC.c2c_transfer_out_count) c2c_transfer_out_count, ");
			strBuff = BTSLUtil.appendZero(strBuff, "DC.c2s_transfer_out_amount", "c2s_transfer_out_amount").append(" , ");
			strBuff.append("(DC.c2s_transfer_out_count) c2s_transfer_out_count, ");
			strBuff = BTSLUtil.appendZero(strBuff, "DC.o2c_transfer_in_amount", "o2c_transfer_in_amount").append(" , ");
			strBuff.append("(DC.o2c_transfer_in_count) o2c_transfer_in_count, ");
			strBuff = BTSLUtil.appendZero(strBuff, "DC.closing_balance", "closing_balance");
			strBuff.append(" FROM daily_chnl_trans_main DC, users U,categories C,user_geographies UG ");
			strBuff.append(" WHERE DC.user_id=U.user_id  ");
			strBuff.append(" AND U.network_code=?  AND U.category_code=C.category_code AND C.domain_code=?  AND C.category_code= ?  ");
			strBuff.append(" AND DC.user_id IN(SELECT U11.user_id FROM users U11  ");
			strBuff.append(" WHERE U11.user_id=CASE  ?  WHEN 'ALL' THEN U11.user_id ELSE  ? END  ");
			strBuff.append(" CONNECT BY PRIOR U11.user_id = U11.parent_id START WITH U11.user_id=?)  ");
			strBuff.append(" AND DC.trans_date>=?   AND DC.trans_date<=?  ");
			strBuff.append(" AND UG.user_id=DC.user_id   AND UG.grph_domain_code IN ( ");
			strBuff.append(" SELECT grph_domain_code FROM  geographical_domains GD1 ");
			strBuff.append(" WHERE status IN('Y', 'S')  CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
			strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code ");
			strBuff.append(" FROM user_geographies UG1 WHERE UG1.grph_domain_code =CASE ? WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END ");
			strBuff.append(" AND UG1.user_id=?))  ");
			strBuff.append(" group by DC.trans_date,U.user_name,U.msisdn,DC.product_code,DC.opening_balance,DC.c2c_transfer_in_amount,DC.c2c_transfer_in_count, ");
			strBuff.append(" DC.c2c_transfer_out_amount,DC.c2c_transfer_out_count,DC.c2s_transfer_out_amount,DC.c2s_transfer_out_count,DC.o2c_transfer_in_amount, ");
			strBuff.append(" DC.o2c_transfer_in_count,DC.closing_balance  ORDER BY DC.trans_date desc  ");

			String selectQuery=strBuff.toString();
		      if(log.isDebugEnabled()){ 
				  log.debug(methodName,QUERYSELECT+ selectQuery); 
		     } 
		      
		      pstmt = con.prepareStatement(selectQuery);
				int i = 1;
				pstmt.setString(i++,Constants.getProperty(REPORTDATAFORMAT));

		        pstmt.setString(i++,thisForm.getNetworkCode());
		        pstmt.setString(i++,thisForm.getDomainCode());
		        pstmt.setString(i++,thisForm.getParentCategoryCode());

		        pstmt.setString(i++,thisForm.getUserID());
		        pstmt.setString(i++,thisForm.getUserID());
		        
		        pstmt.setString(i++,thisForm.getLoginUserID());
		        
		        pstmt.setDate(i++,fromDate);
		        pstmt.setDate(i++,toDate);
		        
		        pstmt.setString(i++,thisForm.getZoneCode());
		        pstmt.setString(i++,thisForm.getZoneCode());
		        pstmt.setString(i,thisForm.getLoginUserID());
		        
           } catch (ParseException |SQLException e1) {
			
			log.errorTrace(methodName, e1);
		}
		return pstmt;
		
		
	}

	@Override
	public PreparedStatement loadOperationSummaryChannelUserTotalReport(UsersReportModel thisForm, Connection con) {
		 StringBuilder strBuff = new StringBuilder();
		final String methodName = "loadOperationSummaryChannelUserTotalReport";
		if(log.isDebugEnabled()){ 
			log.debug(methodName,"Enter loginUserID "+thisForm.getLoginUserID()+" NetworkCode "+thisForm.getNetworkCode()+" DomainCode "+thisForm.getDomainCode()+
					" FROM date "+thisForm.getRptfromDate()+" To Date "+thisForm.getRpttoDate()+ "Category Code"+thisForm.getParentCategoryCode()+
					" UserID "+thisForm.getUserID()+" ZoneCode "+thisForm.getZoneCode());
	     
	     } 
		
		java.sql.Date fromDate = null;
        java.sql.Date toDate = null;
        PreparedStatement pstmt = null;
        try {
			fromDate = BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(thisForm.getRptfromDate()));
			toDate = BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(thisForm.getRpttoDate()));

			strBuff.append(" SELECT TO_CHAR(DC.trans_date,  ?) trans_date, U.user_id, U.user_name,U.msisdn, DC.product_code,  ");
			strBuff = appendZeroNVL(strBuff, "DC.opening_balance", "AGT.opening_balance", "opening_balance").append(" , ");
			strBuff = appendZeroNVLSUM(strBuff, "DC.c2c_return_in_amount + DC.c2c_withdraw_in_amount", "AGT.c2c_return_plus_with_in_amount", "c2c_return_plus_with_in_amount").append(" , ");
			strBuff = appendZeroNVLSUM(strBuff, "DC.o2c_return_out_amount + DC.o2c_withdraw_out_amount", "AGT.o2c_return_plus_with_out_amount", "o2c_return_plus_with_out_amount").append(" , ");
			strBuff = appendZeroNVLSUM(strBuff, "DC.c2c_return_out_amount + DC.c2c_withdraw_out_amount", "AGT.c2c_return_plus_with_out_amount", "c2c_return_plus_with_out_amount").append(" , ");
			strBuff.append(" (SUM ((DC.c2c_return_in_count) + (DC.c2c_withdraw_in_count))) + NVL(AGT.c2c_return_plus_with_in_count,'0') as c2c_return_plus_with_in_count, ");
			strBuff.append(" (SUM ((DC.o2c_return_out_count) + (DC.o2c_withdraw_out_count))) + NVL(AGT.o2c_return_plus_with_out_count,'0') as o2c_return_plus_with_out_count, ");
			strBuff.append(" (SUM ((DC.c2c_return_out_count) + (DC.c2c_withdraw_out_count))) + NVL(AGT.c2c_return_plus_with_out_count,'0') as c2c_return_plus_with_out_count, ");
			strBuff.append(" ((DC.c2c_transfer_in_count) + NVL(AGT.c2c_transfer_in_count,'0')) c2c_transfer_in_count, ");
			
			strBuff = appendZeroNVL(strBuff, "DC.c2c_transfer_out_amount", "AGT.c2c_transfer_out_amount", "c2c_transfer_out_amount").append(" , ");
			strBuff.append(" ((DC.c2c_transfer_out_count) + NVL(AGT.c2c_transfer_out_count,'0')) c2c_transfer_out_count, ");
			
			strBuff = appendZeroNVL(strBuff, "DC.c2s_transfer_out_amount", "AGT.c2s_transfer_out_amount", "c2s_transfer_out_amount").append(" , ");
			strBuff.append(" ((DC.c2s_transfer_out_count) + NVL(AGT.c2s_transfer_out_count,'0')) c2s_transfer_out_count, ");
			strBuff.append(" ((DC.o2c_transfer_in_count) + NVL(AGT.o2c_transfer_in_count,'0')) o2c_transfer_in_count, ");
			
			
			strBuff = appendZeroNVL(strBuff, "DC.closing_balance", "AGT.closing_balance", "closing_balance");
			strBuff.append(" FROM  users U,categories C,user_geographies UG,daily_chnl_trans_main DC left join  ");
			strBuff.append(" ( SELECT user_id,trans_date_str, product_code, ");
			strBuff.append(" trans_date, opening_balance, c2c_transfer_out_count, c2c_transfer_out_amount,c2c_transfer_in_count, c2c_transfer_in_amount, c2s_transfer_out_amount, ");
			strBuff.append(" c2s_transfer_out_count, o2c_transfer_in_amount,o2c_transfer_in_count, ");
			strBuff.append(" (SUM (c2c_return_in_amount_prev) + (c2c_withdraw_in_amount_prev)) as c2c_return_plus_with_in_amount, ");
			strBuff.append(" (SUM (o2c_return_out_amount_prev) + (o2c_withdraw_out_amount_prev)) as o2c_return_plus_with_out_amount, ");
			strBuff.append(" (SUM (c2c_return_out_amount_prev) + (c2c_withdraw_out_amount_prev)) as c2c_return_plus_with_out_amount,  ");
			strBuff.append(" (SUM (c2c_return_in_count_prev) + (c2c_withdraw_in_count_prev)) as c2c_return_plus_with_in_count, ");
			strBuff.append(" (SUM (o2c_return_out_count_prev) + (o2c_withdraw_out_count_prev)) as o2c_return_plus_with_out_count, ");
			strBuff.append(" (SUM (c2c_return_out_count_prev) + (c2c_withdraw_out_count_prev)) as c2c_return_plus_with_out_count,closing_balance  ");
			strBuff.append("  from ( SELECT X.user_id user_id,TO_CHAR(DCA.trans_date,  ?) trans_date_str, X.product_code product_code, DCA.trans_date trans_date, ");
			strBuff.append(" (SUM(DCA.opening_balance)) opening_balance, ");
			strBuff.append(" (SUM(DCA.c2c_transfer_out_count)) c2c_transfer_out_count, ");
			strBuff.append(" (SUM(DCA.c2c_transfer_out_amount)) c2c_transfer_out_amount, ");
			strBuff.append(" (SUM(DCA.c2c_transfer_in_count)) c2c_transfer_in_count, ");
			strBuff.append(" (SUM(DCA.c2c_transfer_in_amount)) c2c_transfer_in_amount, ");
			strBuff.append(" (SUM(DCA.c2s_transfer_out_amount)) c2s_transfer_out_amount,  ");
			strBuff.append(" (SUM(DCA.c2s_transfer_out_count)) c2s_transfer_out_count,  ");
			strBuff.append(" (SUM(DCA.o2c_transfer_in_amount)) o2c_transfer_in_amount, ");
			strBuff.append(" (SUM(DCA.o2c_transfer_in_count)) o2c_transfer_in_count, ");
			strBuff.append(" SUM(DCA.c2c_return_in_amount) c2c_return_in_amount_prev,SUM(DCA.c2c_withdraw_in_amount) c2c_withdraw_in_amount_prev,  ");
			strBuff.append(" SUM(DCA.o2c_return_out_amount) o2c_return_out_amount_prev,SUM(DCA.o2c_withdraw_out_amount) o2c_withdraw_out_amount_prev,  ");
			strBuff.append(" SUM(DCA.c2c_return_out_amount) c2c_return_out_amount_prev,SUM(DCA.c2c_withdraw_out_amount) c2c_withdraw_out_amount_prev, ");
			strBuff.append(" SUM(DCA.c2c_return_in_count) c2c_return_in_count_prev,SUM(DCA.c2c_withdraw_in_count)c2c_withdraw_in_count_prev, ");
			strBuff.append(" SUM(DCA.o2c_return_out_count) o2c_return_out_count_prev,   SUM(DCA.o2c_withdraw_out_count) o2c_withdraw_out_count_prev, ");
			strBuff.append(" SUM(DCA.c2c_return_out_count) c2c_return_out_count_prev, SUM(DCA.c2c_withdraw_out_count) c2c_withdraw_out_count_prev, ");
			strBuff.append(" (SUM(DCA.closing_balance)) closing_balance  ");			
			strBuff.append("  FROM daily_chnl_trans_main DCA, users UA,categories CA,  (SELECT DCG.user_id, DCG.product_code  ");
			strBuff.append(" FROM daily_chnl_trans_main DCG, users UG,categories CG,user_geographies UGG WHERE DCG.user_id=UG.user_id ");
			strBuff.append(" AND UG.category_code=CG.category_code  AND UG.network_code= ?  AND CG.domain_code= ?  AND CG.category_code= ? ");
			strBuff.append(" AND DCG.user_id IN (SELECT U11.user_id FROM users U11 WHERE U11.user_id=CASE ?  WHEN 'ALL'  ");
			strBuff.append(" THEN U11.user_id ELSE  ?  END  CONNECT BY PRIOR U11.user_id = U11.parent_id START WITH U11.user_id=?) ");
			strBuff.append(" AND UGG.user_id=DCG.user_id AND DCG.trans_date>= ?  AND DCG.trans_date<=? ");
			strBuff.append(" AND UGG.grph_domain_code IN ( SELECT grph_domain_code FROM geographical_domains GD1 ");
			strBuff.append(" WHERE status IN('Y', 'S') CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
			strBuff.append("  START WITH grph_domain_code IN (SELECT grph_domain_code ");			
			strBuff.append(" FROM user_geographies UG1 WHERE UG1.grph_domain_code =CASE ?  WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END  ");
            strBuff.append(" AND UG1.user_id=?  ))   )X  ");
			strBuff.append(" WHERE DCA.user_id=UA.user_id AND UA.category_code=CA.category_code  AND CA.category_code= ? AND UA.parent_id=X.user_id  ");
			strBuff.append(" GROUP BY X.user_id, DCA.trans_date, X.product_code	) PREV	GROUP BY ");
			strBuff.append(" user_id,trans_date_str, product_code,  trans_date, opening_balance, c2c_transfer_out_count, c2c_transfer_out_amount,c2c_transfer_in_count,  ");
			strBuff.append(" c2c_transfer_in_amount, c2s_transfer_out_amount, c2s_transfer_out_count, o2c_transfer_in_amount,o2c_transfer_in_count,  ");
			strBuff.append(" c2c_return_in_amount_prev,c2c_withdraw_in_amount_prev,o2c_return_out_amount_prev,o2c_withdraw_out_amount_prev,c2c_return_out_amount_prev, ");
			strBuff.append(" c2c_withdraw_out_amount_prev,c2c_return_in_count_prev,c2c_withdraw_in_count_prev,o2c_return_out_count_prev,o2c_withdraw_out_count_prev, c2c_return_out_count_prev,c2c_withdraw_out_count_prev,closing_balance ");
			strBuff.append(" )AGT on (DC.trans_date=AGT.trans_date AND DC.product_code=AGT.product_code AND DC.user_id=AGT.user_id) WHERE DC.user_id=U.user_id ");
			strBuff.append("   AND U.network_code= ?  AND U.category_code=C.category_code ");
			strBuff.append(" AND C.domain_code=? AND C.category_code=? AND DC.user_id IN(SELECT U11.user_id FROM users U11 ");
			strBuff.append(" WHERE U11.user_id=CASE ?  WHEN 'ALL' THEN U11.user_id ELSE  ?  END  ");
			strBuff.append(" CONNECT BY PRIOR U11.user_id = U11.parent_id START WITH U11.user_id=?) AND  DC.trans_date>=? AND DC.trans_date<=? ");
			strBuff.append(" AND UG.user_id=DC.user_id AND UG.grph_domain_code IN (SELECT grph_domain_code FROM  geographical_domains GD1 ");
			strBuff.append(" WHERE status IN('Y', 'S') CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START WITH grph_domain_code IN ");
			strBuff.append(" (SELECT grph_domain_code FROM user_geographies UG1 WHERE UG1.grph_domain_code =CASE ?  WHEN ? THEN UG1.grph_domain_code ELSE ? END ");
            strBuff.append(" AND UG1.user_id=?)) group by ");
			strBuff.append(" DC.trans_date,U.user_id, U.user_name,U.msisdn, DC.product_code, DC.opening_balance, AGT.opening_balance, AGT.c2c_return_plus_with_in_amount, AGT.o2c_return_plus_with_out_amount ");
			strBuff.append(" ,AGT.c2c_return_plus_with_out_amount, AGT.c2c_return_plus_with_in_count,  AGT.o2c_return_plus_with_out_count, AGT.c2c_return_plus_with_out_count,DC.c2c_transfer_in_amount  ");
			strBuff.append(" ,DC.c2c_transfer_in_count,DC.c2c_transfer_out_amount,AGT.c2c_transfer_in_amount,AGT.c2c_transfer_in_count,AGT.c2c_transfer_out_amount ");
			strBuff.append(" ,DC.c2c_transfer_out_count,AGT.c2c_transfer_out_count,DC.c2s_transfer_out_amount,DC.c2s_transfer_out_count,AGT.c2s_transfer_out_amount ");
			strBuff.append(" ,AGT.c2s_transfer_out_count,DC.o2c_transfer_in_amount,AGT.o2c_transfer_in_amount,DC.o2c_transfer_in_count,AGT.o2c_transfer_in_count ");
			strBuff.append(" ,DC.closing_balance,AGT.closing_balance ORDER BY DC.trans_date DESC ");
			String selectQuery=strBuff.toString();
		      if(log.isDebugEnabled()){ 
				  log.debug(methodName,QUERYSELECT+ selectQuery); 
		     } 
		      
		      pstmt = con.prepareStatement(selectQuery);
				int i = 1;
				pstmt.setString(i++,Constants.getProperty(REPORTDATAFORMAT));
				
				pstmt.setString(i++,Constants.getProperty(REPORTDATAFORMAT));
				pstmt.setString(i++,thisForm.getNetworkCode());
		        pstmt.setString(i++,thisForm.getDomainCode());
		        pstmt.setString(i++,thisForm.getParentCategoryCode());
		        
              
		        pstmt.setString(i++,thisForm.getUserID());
		        pstmt.setString(i++,thisForm.getUserID());	
		        pstmt.setString(i++,thisForm.getLoginUserID());
		        
		        pstmt.setDate(i++,fromDate);
		        pstmt.setDate(i++,toDate);	
		        
		        pstmt.setString(i++,thisForm.getZoneCode());
		        pstmt.setString(i++,thisForm.getZoneCode());		        
		        pstmt.setString(i++,thisForm.getLoginUserID());

		        pstmt.setString(i++,thisForm.getAgentCatCode());
		        
		        pstmt.setString(i++,thisForm.getNetworkCode());
		        pstmt.setString(i++,thisForm.getDomainCode());
		        pstmt.setString(i++,thisForm.getParentCategoryCode());
		        

		        pstmt.setString(i++,thisForm.getUserID());
		        pstmt.setString(i++,thisForm.getUserID());	
                pstmt.setString(i++,thisForm.getLoginUserID());
		        pstmt.setDate(i++,fromDate);
		        pstmt.setDate(i++,toDate);                
		        pstmt.setString(i++,thisForm.getZoneCode());
		        pstmt.setString(i++,thisForm.getZoneCode());	
		        pstmt.setString(i++,thisForm.getZoneCode());
		        pstmt.setString(i,thisForm.getLoginUserID());
           } catch (ParseException |SQLException e1) {
			
			log.errorTrace(methodName, e1);
		}
		return pstmt;
        }

	@Override
	public PreparedStatement loadOperationSummaryOperatorMainReport(UsersReportModel thisForm, Connection con) {
		 StringBuilder strBuff = new StringBuilder();
		final String methodName = "loadOperationSummaryOperatorMainReport";
		if(log.isDebugEnabled()){ 
			log.debug(methodName,"Entered loginUserID "+thisForm.getLoginUserID()+" NetworkCode "+thisForm.getNetworkCode()+" DomainCode "+thisForm.getDomainCode()+
					" FROM date "+thisForm.getRptfromDate()+" To Date "+thisForm.getRpttoDate()+ "Category Code"+thisForm.getParentCategoryCode()+
					" UserID "+thisForm.getUserID()+" ZoneCode "+thisForm.getZoneCode());
	     
	     } 
		
		java.sql.Date fromDate = null;
        java.sql.Date toDate = null;
        PreparedStatement pstmt = null;
        try {
			fromDate = BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(thisForm.getRptfromDate()));
			toDate = BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(thisForm.getRpttoDate()));

			strBuff.append(" SELECT TO_CHAR(DC.trans_date,  ?) trans_date, U.user_name,U.msisdn, DC.product_code, ");
			strBuff = BTSLUtil.appendZero(strBuff, "DC.opening_balance", "opening_balance").append(" , ");
			strBuff = BTSLUtil.appendZero(strBuff, "DC.c2c_return_in_amount + DC.c2c_withdraw_in_amount", "c2c_return_plus_with_in_amount").append(" , ");
			strBuff = BTSLUtil.appendZero(strBuff, "DC.o2c_return_out_amount + DC.o2c_withdraw_out_amount", "o2c_return_plus_with_out_amount").append(" , ");
			strBuff = BTSLUtil.appendZero(strBuff, "DC.c2c_return_out_amount + DC.c2c_withdraw_out_amount", "c2c_return_plus_with_out_amount").append(" , ");
			strBuff.append(" (SUM (DC.c2c_return_in_count + DC.c2c_withdraw_in_count)) as c2c_return_plus_with_in_count,   ");
			strBuff.append(" (SUM (DC.o2c_return_out_count + DC.o2c_withdraw_out_count)) as o2c_return_plus_with_out_count,   ");
			strBuff.append(" (SUM (DC.c2c_return_out_count + DC.c2c_withdraw_out_count)) as c2c_return_plus_with_out_count, ");
			strBuff = BTSLUtil.appendZero(strBuff, "DC.c2c_transfer_in_amount", "c2c_transfer_in_amount").append(" , ");
			strBuff.append(" (DC.c2c_transfer_in_count) c2c_transfer_in_count, ");
			strBuff = BTSLUtil.appendZero(strBuff, "DC.c2c_transfer_out_amount", "c2c_transfer_out_amount").append(" , ");
			strBuff.append(" (DC.c2c_transfer_out_count) c2c_transfer_out_count, ");
			strBuff = BTSLUtil.appendZero(strBuff, "DC.c2s_transfer_out_amount", "c2s_transfer_out_amount").append(" , ");
			strBuff.append(" (DC.c2s_transfer_out_count) c2s_transfer_out_count, ");
			strBuff = BTSLUtil.appendZero(strBuff, "DC.o2c_transfer_in_amount", "o2c_transfer_in_amount").append(" , ");
			strBuff.append(" (DC.o2c_transfer_in_count) o2c_transfer_in_count, ");
			strBuff = BTSLUtil.appendZero(strBuff, "DC.closing_balance", "closing_balance");
			strBuff.append(" FROM daily_chnl_trans_main DC, users U,categories C,user_geographies UG ");
			strBuff.append(" WHERE DC.user_id=U.user_id  ");
			strBuff.append(" AND U.network_code=?  AND U.category_code=C.category_code AND C.domain_code=?  AND C.category_code= ?  ");
			strBuff.append(" AND DC.user_id=CASE ?  WHEN 'ALL' THEN DC.user_id ELSE ? END  ");
			strBuff.append(" AND DC.trans_date>=?  AND DC.trans_date<=?  AND UG.user_id=DC.user_id ");
			strBuff.append(" AND UG.grph_domain_code IN (  SELECT grph_domain_code FROM geographical_domains GD1 ");
			strBuff.append(" WHERE status IN('Y', 'S') CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START WITH grph_domain_code IN ");
			strBuff.append(" (SELECT grph_domain_code FROM user_geographies UG1 WHERE UG1.grph_domain_code =CASE ? WHEN 'ALL' THEN UG1.grph_domain_code ELSE ?  END ");
			strBuff.append(" AND UG1.user_id=?)) ");
			strBuff.append("group by DC.trans_date,U.user_name,U.msisdn,DC.product_code,DC.opening_balance,DC.c2c_transfer_in_amount,DC.c2c_transfer_in_count, ");
			strBuff.append("DC.c2c_transfer_out_amount,DC.c2c_transfer_out_count,DC.c2s_transfer_out_amount,DC.c2s_transfer_out_count,DC.o2c_transfer_in_amount, ");
			strBuff.append("DC.o2c_transfer_in_count,DC.closing_balance  ORDER BY DC.trans_date desc  ");

			String selectQuery=strBuff.toString();
		      if(log.isDebugEnabled()){ 
				  log.debug(methodName,QUERYSELECT+ selectQuery); 
		     } 
		      
		      pstmt = con.prepareStatement(selectQuery);
				int i = 1;
				pstmt.setString(i++,Constants.getProperty(REPORTDATAFORMAT));

		        pstmt.setString(i++,thisForm.getNetworkCode());
		        pstmt.setString(i++,thisForm.getDomainCode());
		        pstmt.setString(i++,thisForm.getParentCategoryCode());
		        pstmt.setString(i++,thisForm.getUserID());
		        pstmt.setString(i++,thisForm.getUserID());
		        pstmt.setDate(i++,fromDate);
		        pstmt.setDate(i++,toDate);
		        pstmt.setString(i++,thisForm.getZoneCode());
		        pstmt.setString(i++,thisForm.getZoneCode());		        
		        pstmt.setString(i,thisForm.getLoginUserID());

           } catch (ParseException |SQLException e1) {
			
			log.errorTrace(methodName, e1);
		}
		return pstmt;
		
		
	
	}

	@Override
	public PreparedStatement loadOperationSummaryOperatorTotalReport(UsersReportModel thisForm, Connection con) {
		 StringBuilder strBuff = new StringBuilder();
		final String methodName = "loadOperationSummaryChannelUserTotalReport";
		if(log.isDebugEnabled()){ 
			log.debug(methodName,"Entered loginUserID "+thisForm.getLoginUserID()+" NetworkCode "+thisForm.getNetworkCode()+" DomainCode "+thisForm.getDomainCode()+
					" FROM date "+thisForm.getRptfromDate()+" To Date "+thisForm.getRpttoDate()+ "Category Code"+thisForm.getParentCategoryCode()+
					" UserID "+thisForm.getUserID()+" ZoneCode "+thisForm.getZoneCode());
	     
	     } 
		
		java.sql.Date fromDate = null;
        java.sql.Date toDate = null;
        PreparedStatement pstmt = null;
        try {
			fromDate = BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(thisForm.getRptfromDate()));
			toDate = BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(thisForm.getRpttoDate()));

			strBuff.append(" SELECT TO_CHAR(DC.trans_date,  ?) trans_date, U.user_id, U.user_name,U.msisdn, DC.product_code,  ");
			strBuff = appendZeroNVL(strBuff, "DC.opening_balance", "AGT.opening_balance", "opening_balance").append(" , ");
			strBuff = appendZeroNVLSUM(strBuff, "DC.c2c_return_in_amount + DC.c2c_withdraw_in_amount", "AGT.c2c_return_plus_with_in_amount", "c2c_return_plus_with_in_amount").append(" , ");
			strBuff = appendZeroNVLSUM(strBuff, "DC.o2c_return_out_amount + DC.o2c_withdraw_out_amount", "AGT.o2c_return_plus_with_out_amount", "o2c_return_plus_with_out_amount").append(" , ");
			strBuff = appendZeroNVLSUM(strBuff, "DC.c2c_return_out_amount + DC.c2c_withdraw_out_amount", "AGT.c2c_return_plus_with_out_amount", "c2c_return_plus_with_out_amount").append(" , ");
			strBuff.append(" (SUM ((DC.c2c_return_in_count) + (DC.c2c_withdraw_in_count))) + NVL(AGT.c2c_return_plus_with_in_count,'0') as c2c_return_plus_with_in_count, ");
			strBuff.append(" (SUM ((DC.o2c_return_out_count) + (DC.o2c_withdraw_out_count))) + NVL(AGT.o2c_return_plus_with_out_count,'0') as o2c_return_plus_with_out_count, ");
			strBuff.append(" (SUM ((DC.c2c_return_out_count) + (DC.c2c_withdraw_out_count))) + NVL(AGT.c2c_return_plus_with_out_count,'0') as c2c_return_plus_with_out_count, ");
			strBuff.append(" ((DC.c2c_transfer_in_count) + NVL(AGT.c2c_transfer_in_count,'0')) c2c_transfer_in_count, ");
			strBuff.append(" ((DC.c2c_transfer_out_count) + NVL(AGT.c2c_transfer_out_count,'0')) c2c_transfer_out_count, ");
			strBuff.append(" ((DC.c2s_transfer_out_count) + NVL(AGT.c2s_transfer_out_count,'0')) c2s_transfer_out_count, ");
			strBuff.append(" ((DC.o2c_transfer_in_count) + NVL(AGT.o2c_transfer_in_count,'0')) o2c_transfer_in_count, ");
			strBuff = appendZeroNVL(strBuff, "DC.c2c_transfer_in_amount", "AGT.c2c_transfer_in_amount", "c2c_transfer_in_amount").append(" , ");
			strBuff = appendZeroNVL(strBuff, "DC.c2s_transfer_out_amount", "AGT.c2s_transfer_out_amount", "c2s_transfer_out_amount").append(" , ");
			strBuff = appendZeroNVL(strBuff, "DC.o2c_transfer_in_amount", "AGT.o2c_transfer_in_amount", "o2c_transfer_in_amount").append(" , ");
			strBuff = appendZeroNVL(strBuff, "DC.closing_balance", "AGT.closing_balance", "closing_balance");
			strBuff.append(" FROM  users U,categories C,user_geographies UG,daily_chnl_trans_main DC left join  ");
			strBuff.append(" ( SELECT user_id,trans_date_str, product_code, ");
			strBuff.append(" trans_date, opening_balance, c2c_transfer_out_count, c2c_transfer_out_amount,c2c_transfer_in_count, c2c_transfer_in_amount, c2s_transfer_out_amount, ");
			strBuff.append(" c2s_transfer_out_count, o2c_transfer_in_amount,o2c_transfer_in_count, ");
			strBuff.append(" (SUM (c2c_return_in_amount_prev) + (c2c_withdraw_in_amount_prev)) as c2c_return_plus_with_in_amount, ");
			strBuff.append(" (SUM (o2c_return_out_amount_prev) + (o2c_withdraw_out_amount_prev)) as o2c_return_plus_with_out_amount, ");
			strBuff.append(" (SUM (c2c_return_out_amount_prev) + (c2c_withdraw_out_amount_prev)) as c2c_return_plus_with_out_amount,  ");
			strBuff.append(" (SUM (c2c_return_in_count_prev) + (c2c_withdraw_in_count_prev)) as c2c_return_plus_with_in_count, ");
			strBuff.append(" (SUM (o2c_return_out_count_prev) + (o2c_withdraw_out_count_prev)) as o2c_return_plus_with_out_count, ");
			strBuff.append(" (SUM (c2c_return_out_count_prev) + (c2c_withdraw_out_count_prev)) as c2c_return_plus_with_out_count,closing_balance  ");
			strBuff.append("  from ( SELECT X.user_id user_id,TO_CHAR(DCA.trans_date,  ?) trans_date_str, X.product_code product_code, DCA.trans_date trans_date, ");
			strBuff.append(" (SUM(DCA.opening_balance)) opening_balance, ");
			strBuff.append(" (SUM(DCA.c2c_transfer_out_count)) c2c_transfer_out_count, ");
			strBuff.append(" (SUM(DCA.c2c_transfer_out_amount)) c2c_transfer_out_amount, ");
			strBuff.append(" (SUM(DCA.c2c_transfer_in_count)) c2c_transfer_in_count, ");
			strBuff.append(" (SUM(DCA.c2c_transfer_in_amount)) c2c_transfer_in_amount, ");
			strBuff.append(" (SUM(DCA.c2s_transfer_out_amount)) c2s_transfer_out_amount,  ");
			strBuff.append(" (SUM(DCA.c2s_transfer_out_count)) c2s_transfer_out_count,  ");
			strBuff.append(" (SUM(DCA.o2c_transfer_in_amount)) o2c_transfer_in_amount, ");
			strBuff.append(" (SUM(DCA.o2c_transfer_in_count)) o2c_transfer_in_count, ");
			strBuff.append(" SUM(DCA.c2c_return_in_amount) c2c_return_in_amount_prev,SUM(DCA.c2c_withdraw_in_amount) c2c_withdraw_in_amount_prev,  ");
			strBuff.append(" SUM(DCA.o2c_return_out_amount) o2c_return_out_amount_prev,SUM(DCA.o2c_withdraw_out_amount) o2c_withdraw_out_amount_prev,  ");
			strBuff.append(" SUM(DCA.c2c_return_out_amount) c2c_return_out_amount_prev,SUM(DCA.c2c_withdraw_out_amount) c2c_withdraw_out_amount_prev, ");
			strBuff.append(" SUM(DCA.c2c_return_in_count) c2c_return_in_count_prev,SUM(DCA.c2c_withdraw_in_count)c2c_withdraw_in_count_prev, ");
			strBuff.append(" SUM(DCA.o2c_return_out_count) o2c_return_out_count_prev,   SUM(DCA.o2c_withdraw_out_count) o2c_withdraw_out_count_prev, ");
			strBuff.append(" SUM(DCA.c2c_return_out_count) c2c_return_out_count_prev, SUM(DCA.c2c_withdraw_out_count) c2c_withdraw_out_count_prev, ");
			strBuff.append(" (SUM(DCA.closing_balance)) closing_balance ");
			strBuff.append(" FROM daily_chnl_trans_main DCA, users UA,categories CA,  (SELECT DCG.user_id, DCG.product_code  ");
			strBuff.append(" FROM daily_chnl_trans_main DCG, users UG,categories CG,user_geographies UGG WHERE DCG.user_id=UG.user_id ");
			strBuff.append(" AND UG.category_code=CG.category_code  AND UG.network_code= ?  AND CG.domain_code= ?  AND CG.category_code= ? ");
			strBuff.append(" AND DCG.user_id=CASE ?  WHEN 'ALL' THEN DCG.user_id ELSE ?  END  ");
			strBuff.append(" AND UGG.user_id=DCG.user_id AND DCG.trans_date>= ?  AND DCG.trans_date<=? ");
			strBuff.append(" AND UGG.grph_domain_code IN ( SELECT grph_domain_code FROM geographical_domains GD1 ");
			strBuff.append(" WHERE status IN('Y', 'S') CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
			strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code ");			
			strBuff.append(" FROM user_geographies UG1 WHERE UG1.grph_domain_code =CASE ?  WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END  ");
            strBuff.append(" AND UG1.user_id=?  ))   )X  ");
			strBuff.append(" WHERE DCA.user_id=UA.user_id AND UA.category_code=CA.category_code  AND CA.category_code= ? AND UA.parent_id=X.user_id  ");
			strBuff.append(" GROUP BY X.user_id, DCA.trans_date, X.product_code	) PREV	GROUP BY ");
			strBuff.append(" user_id,trans_date_str, product_code,  trans_date, opening_balance, c2c_transfer_out_count, c2c_transfer_out_amount,c2c_transfer_in_count,  ");
			strBuff.append(" c2c_transfer_in_amount, c2s_transfer_out_amount, c2s_transfer_out_count, o2c_transfer_in_amount,o2c_transfer_in_count,  ");
			strBuff.append(" c2c_return_in_amount_prev,c2c_withdraw_in_amount_prev,o2c_return_out_amount_prev,o2c_withdraw_out_amount_prev,c2c_return_out_amount_prev, ");
			strBuff.append(" c2c_withdraw_out_amount_prev,c2c_return_in_count_prev,c2c_withdraw_in_count_prev,o2c_return_out_count_prev,o2c_withdraw_out_count_prev, c2c_return_out_count_prev,c2c_withdraw_out_count_prev,closing_balance ");
			strBuff.append(" )AGT on (DC.trans_date=AGT.trans_date AND DC.product_code=AGT.product_code AND DC.user_id=AGT.user_id) WHERE DC.user_id=U.user_id ");
			strBuff.append("   AND U.network_code= ?  AND U.category_code=C.category_code ");
			strBuff.append(" AND C.domain_code=? AND C.category_code=? AND DC.user_id=CASE ?  WHEN 'ALL' THEN DC.user_id ELSE ?  END ");
			strBuff.append("  AND DC.trans_date>=? AND DC.trans_date<=? ");
			strBuff.append(" AND UG.user_id=DC.user_id AND UG.grph_domain_code IN (SELECT grph_domain_code FROM  geographical_domains GD1 ");
			strBuff.append(" WHERE status IN('Y', 'S') CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code START WITH grph_domain_code IN ");
			strBuff.append(" (SELECT grph_domain_code FROM user_geographies UG1 WHERE UG1.grph_domain_code =CASE ?  WHEN ? THEN UG1.grph_domain_code ELSE ? END ");
            strBuff.append(" AND UG1.user_id=?)) group by ");
			strBuff.append(" DC.trans_date,U.user_id, U.user_name,U.msisdn, DC.product_code, DC.opening_balance, AGT.opening_balance, AGT.c2c_return_plus_with_in_amount, AGT.o2c_return_plus_with_out_amount ");
			strBuff.append(" ,AGT.c2c_return_plus_with_out_amount, AGT.c2c_return_plus_with_in_count,  AGT.o2c_return_plus_with_out_count, AGT.c2c_return_plus_with_out_count,DC.c2c_transfer_in_amount  ");
			strBuff.append(" ,DC.c2c_transfer_in_count,DC.c2c_transfer_out_amount,AGT.c2c_transfer_in_amount,AGT.c2c_transfer_in_count,AGT.c2c_transfer_out_amount ");
			strBuff.append(" ,DC.c2c_transfer_out_count,AGT.c2c_transfer_out_count,DC.c2s_transfer_out_amount,DC.c2s_transfer_out_count,AGT.c2s_transfer_out_amount ");
			strBuff.append(" ,AGT.c2s_transfer_out_count,DC.o2c_transfer_in_amount,AGT.o2c_transfer_in_amount,DC.o2c_transfer_in_count,AGT.o2c_transfer_in_count ");
			strBuff.append(" ,DC.closing_balance,AGT.closing_balance ORDER BY DC.trans_date DESC ");
			String selectQuery=strBuff.toString();
		      if(log.isDebugEnabled()){ 
				  log.debug(methodName,QUERYSELECT+ selectQuery); 
		     } 
		      
		      pstmt = con.prepareStatement(selectQuery);
				int i = 1;
				pstmt.setString(i++,Constants.getProperty(REPORTDATAFORMAT));
				
				pstmt.setString(i++,Constants.getProperty(REPORTDATAFORMAT));
				pstmt.setString(i++,thisForm.getNetworkCode());
		        pstmt.setString(i++,thisForm.getDomainCode());
		        pstmt.setString(i++,thisForm.getParentCategoryCode());
		        
		        pstmt.setString(i++,thisForm.getUserID());
		        pstmt.setString(i++,thisForm.getUserID());        
		        pstmt.setDate(i++,fromDate);
		        pstmt.setDate(i++,toDate);	
		        
		        pstmt.setString(i++,thisForm.getZoneCode());
		        pstmt.setString(i++,thisForm.getZoneCode());		        
		        pstmt.setString(i++,thisForm.getLoginUserID());

		        pstmt.setString(i++,thisForm.getAgentCatCode());
		        
		        pstmt.setString(i++,thisForm.getNetworkCode());
		        pstmt.setString(i++,thisForm.getDomainCode());
		        pstmt.setString(i++,thisForm.getParentCategoryCode());
		        
		        pstmt.setString(i++,thisForm.getUserID());
		        pstmt.setString(i++,thisForm.getUserID());	
		        
		        pstmt.setDate(i++,fromDate);
		        pstmt.setDate(i++,toDate); 
		        
		        pstmt.setString(i++,thisForm.getZoneCode());
		        pstmt.setString(i++,thisForm.getZoneCode());	
		        pstmt.setString(i++,thisForm.getZoneCode());
		        pstmt.setString(i,thisForm.getLoginUserID());
           } catch (ParseException |SQLException e1) {
			
			log.errorTrace(methodName, e1);
		}
		return pstmt;
        }
	
	
    public static StringBuilder appendZeroNVL(StringBuilder selectQueryBuff, String field1, String field2, String returnName) {
		StringBuilder fieldStr1 = new StringBuilder(field1);
		StringBuilder fieldStr2 = new StringBuilder(field2);
		String FORMAT = "'9999999999999999999999D99'";
		int amountMultFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
		StringBuilder returnNameStr = new StringBuilder(returnName);
		selectQueryBuff.append(" CASE WHEN to_number(trim(To_char(((").append(fieldStr1).append(")+ NVL(").append(fieldStr2).append(",'0'))/").
		append(amountMultFactor).append(",").append(FORMAT).
		append(")),").append(FORMAT).append(") < '1' THEN '0' || trim(To_char(SUM(to_number(((").append(fieldStr1).append(")+ NVL(").append(fieldStr2).append(",'0')),").append(FORMAT).append("))/").
		
		append(amountMultFactor).append(",").append(FORMAT).
		append(")) ELSE To_char(SUM(to_number(((").append(fieldStr1).append(")+ NVL(").append(fieldStr2).append(",'0')),").append(FORMAT).append("))/").
		append(amountMultFactor).append(",").append(FORMAT).
		append(") END ").append(returnNameStr);
		return selectQueryBuff;
	}
    
    public static StringBuilder appendZeroNVLSUM(StringBuilder selectQueryBuff, String field1, String field2, String returnName) {
		StringBuilder fieldStr1 = new StringBuilder(field1);
		StringBuilder fieldStr2 = new StringBuilder(field2);
		String FORMAT = "'9999999999999999999999D99'";
		int amountMultFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
		StringBuilder returnNameStr = new StringBuilder(returnName);
		selectQueryBuff.append(" CASE WHEN to_number(trim(To_char((( SUM ((").append(fieldStr1).append("))) + NVL(").append(fieldStr2).append(",'0'))/").
		append(amountMultFactor).append(",").append(FORMAT).
		append(")),").append(FORMAT).append(") < '1' THEN '0' || trim(To_char(((SUM ((").append(fieldStr1).append("))) + NVL(").append(fieldStr2).append(",'0'))/").
		
		append(amountMultFactor).append(",").append(FORMAT).
		append(")) ELSE To_char(((SUM ((").append(fieldStr1).append("))) + NVL(").append(fieldStr2).append(",'0'))/").
		append(amountMultFactor).append(",").append(FORMAT).
		append(") END ").append(returnNameStr);
		return selectQueryBuff;
	}
	


}

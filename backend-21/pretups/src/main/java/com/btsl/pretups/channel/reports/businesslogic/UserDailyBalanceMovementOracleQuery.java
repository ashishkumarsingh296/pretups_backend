package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * 
 * @author rahul.arya
 *
 */
public class UserDailyBalanceMovementOracleQuery implements
		UserDailyBalanceMovementRptQuery {

	public static final Log log = LogFactory.getLog(UserDailyBalanceMovementOracleQuery.class.getName());
	@Override
	public PreparedStatement dailyBalanceMovementChnlUserRpt(UsersReportModel usersReportModel,Connection con) throws SQLException, ParseException {
		
		final String methodName = "dailyBalanceMovementChnlUserRpt";
		java.sql.Date fromDate = null;
	    java.sql.Date toDate = null;
	    fromDate = BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(usersReportModel.getRptfromDate()));
	    toDate = BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(usersReportModel.getRpttoDate()));
		if(log.isDebugEnabled()){ 
			 
			log.debug(methodName,"Entered loginUserID "+usersReportModel.getLoginUserID()+" NetworkCode "+usersReportModel.getNetworkCode()+" DomainCode "+usersReportModel.getDomainCode()+
					" ParentCategoryCode "+usersReportModel.getParentCategoryCode()+" Category User "+usersReportModel.getUserName()+
					" UserID "+usersReportModel.getUserID()+" ToUserID "+usersReportModel.getTouserID()+" ZoneCode "+usersReportModel.getZoneCode());
	     
	     } 
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT TO_CHAR(CT.trans_date,?)transfer_date,U.user_name,U.msisdn,U.external_code, ");
		strBuff.append("GD.grph_domain_name,GP.user_name grand_name,GP.msisdn grand_msisdn,GD1.GRPH_DOMAIN_NAME grand_geo,GD2.GRPH_DOMAIN_NAME owner_geo, ");
		strBuff.append("P.product_name product_name,TO_CHAR(CT.opening_balance/").append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(", '9999999999999999999999D99') as opening_balance,TO_CHAR(CT.o2c_transfer_in_amount/").append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(", '9999999999999999999999D99') as o2c_transfer_in_amount,TO_CHAR(CT.closing_balance/").append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(", '9999999999999999999999D99') as closing_balance, ");
		strBuff.append("TO_CHAR(CT.c2s_transfer_out_amount/").append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(", '9999999999999999999999D99') as c2s_transfer_out_amount,TO_CHAR(CT.o2c_return_out_amount/").append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(", '9999999999999999999999D99') as o2c_return_out_amount,TO_CHAR(CT.o2c_withdraw_out_amount/").append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(", '9999999999999999999999D99') as o2c_withdraw_out_amount, ");
		strBuff.append("TO_CHAR(CT.c2c_transfer_out_amount/").append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(", '9999999999999999999999D99') as c2c_transfer_out_amount,TO_CHAR(CT.c2c_withdraw_out_amount/").append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(", '9999999999999999999999D99') as c2c_withdraw_out_amount,TO_CHAR(CT.c2c_return_out_amount/").append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(", '9999999999999999999999D99') as c2c_return_out_amount, ");
		strBuff.append("TO_CHAR(CT.c2c_withdraw_in_amount/").append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(", '9999999999999999999999D99') as c2c_withdraw_in_amount,TO_CHAR(CT.c2c_return_in_amount/").append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(", '9999999999999999999999D99') as c2c_return_in_amount,TO_CHAR(CT.c2c_transfer_in_amount/").append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(", '9999999999999999999999D99') as c2c_transfer_in_amount,TO_CHAR(SUM(CT.o2c_withdraw_out_amount + CT.o2c_return_out_amount )/").append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(", '9999999999999999999999D99') as stock_return,TO_CHAR(sum(CT.c2c_transfer_out_amount + CT.c2c_withdraw_out_amount +  CT.c2c_return_out_amount)/").append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(", '9999999999999999999999D99') as channel_transfer,TO_CHAR(sum(CT.c2c_withdraw_in_amount + CT.c2c_return_in_amount )/").append(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue()).append(", '9999999999999999999999D99') as channel_return,CT.trans_date,UP.user_name parent_name,UP.msisdn parent_msisdn, ");
		strBuff.append("CASE WHEN CT.closing_balance = (CT.opening_balance + CT.o2c_transfer_in_amount-CT.o2c_withdraw_out_amount + CT.o2c_return_out_amount - CT.c2c_transfer_out_amount + CT.c2c_withdraw_out_amount +  CT.c2c_return_out_amount + CT.c2c_withdraw_in_amount + CT.c2c_return_in_amount - CT.c2s_transfer_out_amount) THEN 'N' ELSE 'Y' end as recon_status ");
		strBuff.append("FROM (SELECT user_id,parent_id,owner_id ");
		strBuff.append("FROM USERS CONNECT BY PRIOR user_id = parent_id START WITH user_id=?) X, ");
		strBuff.append("DAILY_CHNL_TRANS_MAIN CT, USERS U,CATEGORIES CAT, USER_GEOGRAPHIES UG, ");
		strBuff.append("GEOGRAPHICAL_DOMAINS GD,PRODUCTS P,USERS UP,USERS GP,USERS OU,USER_GEOGRAPHIES UGG,USER_GEOGRAPHIES UGW,GEOGRAPHICAL_DOMAINS GD1, ");
		strBuff.append("GEOGRAPHICAL_DOMAINS GD2 ");
		strBuff.append("WHERE  X.user_id = CT.user_id ");
		strBuff.append("AND CT.user_id = U.user_id ");
		strBuff.append("AND P.product_code=CT.product_code ");
		strBuff.append("AND CAT.category_code = U.category_code ");
		strBuff.append("AND U.user_id = UG.user_id ");
		strBuff.append("AND UG.grph_domain_code = GD.grph_domain_code ");
		strBuff.append("AND UP.USER_ID=CASE X.parent_id WHEN 'ROOT' THEN X.user_id ELSE X.parent_id END ");
		strBuff.append("AND GP.USER_ID=CASE UP.parent_id WHEN 'ROOT' THEN UP.user_id ELSE UP.parent_id END ");
		strBuff.append("AND OU.USER_ID=X.OWNER_ID ");
		strBuff.append("AND UGG.user_id=GP.USER_ID ");
		strBuff.append("AND UGG.GRPH_DOMAIN_CODE=GD1.GRPH_DOMAIN_CODE ");
		strBuff.append("AND UGW.USER_ID=OU.USER_ID ");
		strBuff.append("AND UGW.GRPH_DOMAIN_CODE=GD2.GRPH_DOMAIN_CODE ");
		strBuff.append("AND UG.grph_domain_code IN ( ");
		strBuff.append("SELECT grph_domain_code ");
		strBuff.append("FROM ");
		strBuff.append("GEOGRAPHICAL_DOMAINS GD1 ");
		strBuff.append("WHERE status IN('Y', 'S') ");
		strBuff.append("CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
		strBuff.append("START WITH grph_domain_code IN ");
		strBuff.append("(SELECT grph_domain_code ");
		strBuff.append("FROM USER_GEOGRAPHIES ug1 ");
		strBuff.append("WHERE UG1.grph_domain_code =CASE ?  WHEN 'ALL' THEN UG1.grph_domain_code ELSE  ? END ");
		strBuff.append("AND UG1.user_id=?)) ");
		strBuff.append("AND CT.network_code = ? ");
		strBuff.append("AND CAT.domain_code = ? ");
		strBuff.append("AND CAT.category_code = CASE  ?   WHEN 'ALL' THEN CAT.category_code ELSE   ?  END ");
		strBuff.append("AND CT.user_id =  CASE ? WHEN 'ALL' THEN CT.user_id ELSE ?  END ");
		strBuff.append("AND CT.trans_date >= ? ");
		strBuff.append("AND CT.trans_date <= ? ");
		strBuff.append("group by ");
		strBuff.append(" CT.trans_date,U.user_name,U.msisdn,U.external_code,GD.grph_domain_name,GP.user_name,GP.msisdn,GD1.GRPH_DOMAIN_NAME  "); 
		strBuff.append(" ,GD2.GRPH_DOMAIN_NAME,P.product_name,CT.opening_balance,CT.o2c_transfer_in_amount,CT.closing_balance, ");
		strBuff.append("CT.c2s_transfer_out_amount,CT.o2c_return_out_amount,CT.o2c_withdraw_out_amount,CT.c2c_transfer_out_amount,CT.c2c_withdraw_out_amount,CT.c2c_return_out_amount,  ");
		strBuff.append(" CT.c2c_withdraw_in_amount,CT.c2c_return_in_amount, CT.c2c_transfer_in_amount, CT.trans_date,UP.user_name,UP.msisdn ");
		
		String selectQuery=strBuff.toString();
	      if(log.isDebugEnabled()){ 
			  log.debug(methodName," QUERY SELECT = "+ selectQuery); 
	     } 
	      PreparedStatement pstmt;
	      
				
				pstmt = con.prepareStatement(selectQuery);
				int i = 1;
				pstmt.setString(i++,Constants.getProperty("report.onlydateformat"));
	            pstmt.setString(i++,usersReportModel.getLoginUserID());
	            pstmt.setString(i++,usersReportModel.getZoneCode());
	            pstmt.setString(i++,usersReportModel.getZoneCode());
	            pstmt.setString(i++,usersReportModel.getLoginUserID());
	            pstmt.setString(i++,usersReportModel.getNetworkCode());
	            pstmt.setString(i++,usersReportModel.getDomainCode());
	            pstmt.setString(i++,usersReportModel.getParentCategoryCode());
	            pstmt.setString(i++,usersReportModel.getParentCategoryCode());
	            pstmt.setString(i++,usersReportModel.getUserID());
	            pstmt.setString(i++,usersReportModel.getUserID());
	            pstmt.setDate(i++,fromDate);
	            pstmt.setDate(i,toDate);
				
		return pstmt;
	}

	@Override
	public PreparedStatement dailyBalanceMovementOptRpt(UsersReportModel usersReportModel,Connection con) throws SQLException, ParseException {
		final StringBuilder strBuff = new StringBuilder();
		final String methodName = "dailyBalanceMovementOptRpt";
	    java.sql.Date fromDate = null;
	    java.sql.Date toDate = null;
	    fromDate = BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(usersReportModel.getRptfromDate()));
	    toDate = BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(usersReportModel.getRpttoDate()));
		
		if(log.isDebugEnabled()){ 
			 
			log.debug(methodName,"Entered loginUserID "+usersReportModel.getLoginUserID()+" NetworkCode "+usersReportModel.getNetworkCode()+" DomainCode "+usersReportModel.getDomainCode()+
					" ParentCategoryCode "+usersReportModel.getParentCategoryCode()+" Category User "+usersReportModel.getUserName()+
					" UserID "+usersReportModel.getUserID()+" ToUserID "+usersReportModel.getTouserID()+" ZoneCode "+usersReportModel.getZoneCode());
	     
	     } 
		strBuff.append("SELECT TO_CHAR(CT.trans_date,?)transfer_date, ");
		strBuff.append("U.user_name, ");
		strBuff.append("U.msisdn, ");
		strBuff.append("U.external_code, ");
		strBuff.append("GD.grph_domain_name, ");
		strBuff.append("(GP.user_name)gp_name, ");
		strBuff.append("(GP.msisdn) gp_msisdn, ");
		strBuff.append("(GD1.GRPH_DOMAIN_NAME)gp_grp_domain_name, ");
		strBuff.append("(GD2.GRPH_DOMAIN_NAME)parent_grph_domain_name, ");
		strBuff.append("P.product_name product_name, ");
		strBuff.append("(TO_CHAR((CT.opening_balance)/?,'9999999999999999999999D99')) as opening_balance, ");
		strBuff.append("(TO_CHAR((CT.closing_balance)/?,'9999999999999999999999D99')) as closing_balance, ");
		strBuff.append("(TO_CHAR((CT.o2c_transfer_in_amount)/?,'9999999999999999999999D99')) as stock_bought, ");
		strBuff.append("(TO_CHAR((SUM (CT.o2c_withdraw_out_amount + CT.o2c_return_out_amount))/?,'9999999999999999999999D99')) as stock_return, ");
		strBuff.append("(TO_CHAR((SUM(CT.c2c_transfer_out_amount + CT.c2c_withdraw_out_amount + CT.c2c_return_out_amount))/?,'9999999999999999999999D99')) as channel_transfer, ");
		strBuff.append("(TO_CHAR((CT.c2s_transfer_out_amount)/?,'9999999999999999999999D99')) as c2s_transfer, ");
		strBuff.append("(TO_CHAR((SUM(CT.c2c_withdraw_in_amount + CT.c2c_return_in_amount + CT.c2c_transfer_in_amount))/?,'9999999999999999999999D99')) as channel_return, ");
		strBuff.append("(TO_CHAR((SUM(CT.opening_balance + CT.o2c_transfer_in_amount +CT.c2c_withdraw_in_amount + CT.c2c_return_in_amount + ");
		strBuff.append("CT.c2c_transfer_in_amount-CT.o2c_withdraw_out_amount - CT.o2c_return_out_amount -CT.c2c_transfer_out_amount ");
		strBuff.append("- CT.c2c_withdraw_out_amount - CT.c2c_return_out_amount-CT.c2s_transfer_out_amount))/?,'9999999999999999999999D99')) as recon_value, ");
		strBuff.append("CT.trans_date,(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE UP.user_name END) p_name, ");
		strBuff.append("(CASE U.parent_id WHEN 'ROOT' THEN ");
		strBuff.append("'' ELSE UP.msisdn END)p_msisdn, ");
		strBuff.append("(TO_CHAR((SUM(CT.c2c_transfer_out_amount + CT.c2c_withdraw_out_amount + CT.c2c_return_out_amount-CT.c2c_withdraw_in_amount - ");
		strBuff.append("CT.c2c_return_in_amount - CT.c2c_transfer_in_amount) )/?,'9999999999999999999999D99')) as net_balance, ");
		strBuff.append("(TO_CHAR((SUM( CT.o2c_transfer_in_amount- CT.c2c_transfer_out_amount -CT.C2C_WITHDRAW_OUT_AMOUNT + CT.C2C_RETURN_IN_AMOUNT + ");
		strBuff.append("CT.c2c_transfer_in_amount -CT.o2c_withdraw_out_amount- CT.c2c_return_out_amount+ CT.c2c_withdraw_in_amount - CT.o2c_return_out_amount ");
		strBuff.append("))/?,'9999999999999999999999D99') ) as net_lifting, ");
		strBuff.append("(CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OU.user_name END)  owner_name, ");
		strBuff.append("(CASE  WHEN U.parent_id = 'ROOT' THEN ''  WHEN U.user_id = OU.user_id THEN '' ELSE OU.msisdn END) owner_msisdn ");
		strBuff.append("FROM DAILY_CHNL_TRANS_MAIN CT, ");
		strBuff.append("USERS U, ");
		strBuff.append("CATEGORIES CAT, ");
		strBuff.append("USER_GEOGRAPHIES UG, ");
		strBuff.append("GEOGRAPHICAL_DOMAINS GD, ");
		strBuff.append("PRODUCTS P, ");
		strBuff.append("USERS UP, ");
		strBuff.append("USERS GP, ");
		strBuff.append("USERS OU, ");
		strBuff.append("USER_GEOGRAPHIES UGG ");
		strBuff.append(",USER_GEOGRAPHIES UGW, ");
		strBuff.append("GEOGRAPHICAL_DOMAINS GD1, ");
		strBuff.append("GEOGRAPHICAL_DOMAINS GD2 ");
		strBuff.append("WHERE  CT.user_id = U.user_id ");
		strBuff.append("AND P.product_code=CT.product_code ");
		strBuff.append("AND CAT.category_code = U.category_code ");
		strBuff.append("AND U.user_id = UG.user_id ");
		strBuff.append("AND UG.grph_domain_code = GD.grph_domain_code ");
		strBuff.append("AND UP.USER_ID=CASE U.parent_id WHEN 'ROOT' THEN U.user_id ELSE U.parent_id END ");
		strBuff.append("AND GP.USER_ID=CASE UP.parent_id WHEN 'ROOT' THEN UP.user_id ELSE UP.parent_id END ");
		strBuff.append("AND OU.USER_ID=U.OWNER_ID ");
		strBuff.append("AND UGG.user_id=GP.USER_ID ");
		strBuff.append("AND UGG.GRPH_DOMAIN_CODE=GD1.GRPH_DOMAIN_CODE ");
		strBuff.append("AND UGW.USER_ID=OU.USER_ID ");
		strBuff.append("AND UGW.GRPH_DOMAIN_CODE=GD2.GRPH_DOMAIN_CODE ");
		strBuff.append("AND UG.grph_domain_code IN ( ");
		strBuff.append("SELECT grph_domain_code ");
		strBuff.append("FROM ");
		strBuff.append("GEOGRAPHICAL_DOMAINS GD1 ");
		strBuff.append("WHERE status IN('Y', 'S') ");
		strBuff.append("CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
		strBuff.append("START WITH grph_domain_code IN ");
		strBuff.append("(SELECT grph_domain_code ");
		strBuff.append("FROM USER_GEOGRAPHIES ug1 ");
		strBuff.append("WHERE UG1.grph_domain_code =CASE ?  WHEN 'ALL' THEN UG1.grph_domain_code ELSE  ? END ");
		strBuff.append("AND UG1.user_id=? )) ");
		strBuff.append("AND CT.network_code = ? ");
		strBuff.append("AND CAT.domain_code = ? ");
		strBuff.append("AND CAT.category_code = CASE  ?   WHEN 'ALL' THEN CAT.category_code ELSE  ?  END ");
		strBuff.append("AND CT.user_id =  CASE ? WHEN 'ALL' THEN CT.user_id ELSE ?  END ");
		strBuff.append("AND CT.trans_date >= ? ");
		strBuff.append("AND CT.trans_date <= ? ");
		strBuff.append("group by ");
		strBuff.append("CT.trans_date,U.user_name,U.msisdn,U.external_code,GD.grph_domain_name,GP.user_name,GP.msisdn,GD1.GRPH_DOMAIN_NAME,GD2.GRPH_DOMAIN_NAME, ");
		strBuff.append("P.product_name,UP.user_name,UP.msisdn,CT.opening_balance,CT.closing_balance,CT.o2c_transfer_in_amount,CT.c2s_transfer_out_amount, ");
		strBuff.append("CT.c2c_transfer_in_amount, ");
		strBuff.append("OU.user_name, OU.msisdn,U.parent_id,OU.user_id,U.user_id ");
		

		String selectQuery=strBuff.toString();
	      if(log.isDebugEnabled()){ 
			  log.debug(methodName," QUERY SELECT = "+ selectQuery); 
	     } 
	      PreparedStatement pstmt;
	     
	        
		pstmt = con.prepareStatement(selectQuery);
		int i = 1;
		pstmt.setString(i++,Constants.getProperty("report.onlydateformat"));
        pstmt.setLong(i++,((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue());
        pstmt.setLong(i++,((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue());
        pstmt.setLong(i++,((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue());
        pstmt.setLong(i++,((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue());
        pstmt.setLong(i++,((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue());
        pstmt.setLong(i++,((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue());
        pstmt.setLong(i++,((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue());
        pstmt.setLong(i++,((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue());
        pstmt.setLong(i++,((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue());
        pstmt.setLong(i++,((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue());
        pstmt.setString(i++,usersReportModel.getZoneCode());
        pstmt.setString(i++,usersReportModel.getZoneCode());
        pstmt.setString(i++,usersReportModel.getLoginUserID());
        pstmt.setString(i++,usersReportModel.getNetworkCode());
        pstmt.setString(i++,usersReportModel.getDomainCode());
        pstmt.setString(i++,usersReportModel.getParentCategoryCode());
        pstmt.setString(i++,usersReportModel.getParentCategoryCode());
        pstmt.setString(i++,usersReportModel.getUserID());
        pstmt.setString(i++,usersReportModel.getUserID());
        pstmt.setDate(i++,fromDate);
        pstmt.setDate(i,toDate);
		return pstmt;
	}
}

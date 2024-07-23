package com.btsl.db.query.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.reports.businesslogic.Channel2ChannelTransferRetWidRptQry;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.channel.reports.web.UsersReportModel;
/**
 * 
 * @author yogesh.keshari
 *
 */
public class Channel2ChannelTransferRetWidRptOracleQry implements Channel2ChannelTransferRetWidRptQry {
	public static final Log log = LogFactory.getLog(Channel2ChannelTransferRetWidRptOracleQry.class.getName());
	
	private static final String CTI_COMMISSION_VALUE = "CTI.commission_value";
	private static final String CTI_PAYABLE_AMOUNT = "CTI.payable_amount";
	private static final String CTI_TAX1_VALUE = "CTI.tax1_value";
	private static final String CTI_TAX2_VALUE = "CTI.tax2_value";
	private static final String CTI_TAX3_VALUE = "CTI.tax3_value";
	private static final String CTI_NET_PAYABLE_AMOUNT = "CTI.net_payable_amount";
	private static final String CTI_REQUIRED_QUANTITY = "CTI.required_quantity";
	private static final String CTI_MRP = "CTI.mrp";
	private static final String CTI_RECEIVER_CREDIT_QUANTITY = "CTI.receiver_credit_quantity";
	private static final String CTI_SENDER_DEBIT_QUANTITY = "CTI.sender_debit_quantity";
	private static final String CTI_COMMISION_QUANTITY = "CTI.commision_quantity";
	/**
	 * 
	 * @param conn
	 * @param usersReportModel
	 * @return PreparedStatement
	 * @throws SQLException
	 * @throws ParseException
	 */
	@Override
	public PreparedStatement loadC2cRetWidTransferChannelUserUnionListQry(Connection conn,
			UsersReportModel usersReportModel) throws SQLException, ParseException {
		final String methodName = "loadC2cRetWidTransferChannelUserUnionListQry";
		StringBuilder loggerValue= new StringBuilder();
		if(log.isDebugEnabled()){ 
			loggerValue.setLength(0);
        	loggerValue.append("Entered loginUserID ");
        	loggerValue.append(usersReportModel.getLoginUserID());
			loggerValue.append(" NetworkCode ");
        	loggerValue.append(usersReportModel.getNetworkCode());
        	loggerValue.append(" DomainCode ");
        	loggerValue.append(usersReportModel.getDomainCode());
			loggerValue.append(" FromTrfCatCode ");
        	loggerValue.append(usersReportModel.getFromtransferCategoryCode());
        	loggerValue.append(" ToTrfCatCode ");
        	loggerValue.append(usersReportModel.getTotransferCategoryCode());
			loggerValue.append(" UserID ");
        	loggerValue.append(usersReportModel.getUserID());
        	loggerValue.append(" ToUserID ");
        	loggerValue.append(usersReportModel.getTouserID());
        	loggerValue.append(" TxnSubType ");
        	loggerValue.append(usersReportModel.getTxnSubType());
			loggerValue.append(" ZoneCode ");
        	loggerValue.append(usersReportModel.getZoneCode());
			log.debug(methodName,loggerValue);
	     } 
		StringBuilder selectQueryBuff =new StringBuilder();
		selectQueryBuff.append(" SELECT CTRF.from_user_id, CTRF.to_user_id, (U.user_name || '(' ||CTRF.msisdn||')') from_user, ");
		selectQueryBuff.append(" (U2.user_name || '(' ||CTRF.to_msisdn||')') to_user,CTRF.transfer_id, L.lookup_name transfer_sub_type, CTRF.type,  ");
		selectQueryBuff.append("  TO_CHAR(CTRF.transfer_date,?) transfer_date,  TO_CHAR(CTRF.modified_ON, ?) modified_ON,P.product_name,  ");
		
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_REQUIRED_QUANTITY, "transfer_mrp").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_PAYABLE_AMOUNT, "payable_amount").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_NET_PAYABLE_AMOUNT, "net_payable_amount").append(" , ");
		  selectQueryBuff.append(" L1.lookup_name status, ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_MRP, "mrp").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_COMMISSION_VALUE, "commision").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_COMMISION_QUANTITY, "commision_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_RECEIVER_CREDIT_QUANTITY, "receiver_credit_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_SENDER_DEBIT_QUANTITY, "sender_debit_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX3_VALUE, "tax3_value").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX1_VALUE, "tax1_value").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX2_VALUE, "tax2_value").append(" , ");
		  
		selectQueryBuff.append(" SUM(to_number(CTI.otf_amount))otf_amount, ");
		selectQueryBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code,SEND_CAT.category_name sender_category_name, REC_CAT.category_name receiver_category_name,CTRF.source FROM (SELECT USR.user_id FROM users USR CONNECT BY PRIOR USR.user_id = USR.parent_id START WITH USR.user_id=? )X, ");
		selectQueryBuff.append(" channel_transfers CTRF, channel_transfers_items CTI,users U,users U2,products P,lookups L, lookups L1, categories SEND_CAT, ");	
		selectQueryBuff.append(" categories REC_CAT,user_geographies UG    ");
		selectQueryBuff.append(" WHERE CTRF.type = 'C2C'   ");
		selectQueryBuff.append(" AND NVL(CTRF.close_date,CTRF.created_on) >=TO_DATE(?,?)  ");
		selectQueryBuff.append(" AND NVL(CTRF.close_date,CTRF.created_on) <=TO_DATE(?,?) ");
		selectQueryBuff.append(" AND CTRF.network_code = ?     ");
		selectQueryBuff.append(" AND SEND_CAT.domain_code  = ?   ");
		selectQueryBuff.append(" AND CTRF.control_transfer<>'A'  ");
		selectQueryBuff.append(" AND CTRF.sender_category_code = SEND_CAT.category_code     ");
		selectQueryBuff.append(" AND CTRF.receiver_category_code = REC_CAT.category_code    ");
		selectQueryBuff.append(" AND CTRF.sender_category_code=CASE ? WHEN 'ALL' THEN CTRF.sender_category_code ELSE ? END  ");
		selectQueryBuff.append(" AND CTRF.receiver_category_code=CASE ? WHEN 'ALL' THEN CTRF.receiver_category_code ELSE? END   ");
		selectQueryBuff.append(" AND CTRF.from_user_id=CASE ? WHEN 'ALL' THEN from_user_id ELSE? END     ");
		selectQueryBuff.append(" AND CTRF.to_user_id=CASE ? WHEN 'ALL' THEN to_user_id ELSE ? END   ");
		selectQueryBuff.append(" AND CTRF.transfer_sub_type = case ? when 'ALL' then CTRF.transfer_sub_type else ? end    ");
		selectQueryBuff.append(" AND CTRF.from_user_id = X.user_id   ");
		selectQueryBuff.append(" AND U.user_id = CTRF.from_user_id     ");
		selectQueryBuff.append(" AND U2.user_id =CTRF.to_user_id   ");
		selectQueryBuff.append(" AND CTRF.transfer_id = CTI.transfer_id    ");
		selectQueryBuff.append(" AND CTI.product_code = P.product_code  ");
		selectQueryBuff.append(" AND L.lookup_type ='TRFT'     ");
		selectQueryBuff.append(" AND L.lookup_code = CTRF.transfer_sub_type  ");
		selectQueryBuff.append(" AND CTRF.status = 'CLOSE'  ");
		selectQueryBuff.append(" AND L1.lookup_code = CTRF.status   ");
		selectQueryBuff.append(" AND L1.lookup_type = 'CTSTA'     ");
		selectQueryBuff.append(" AND CTRF.to_user_id = UG.user_id   ");
		selectQueryBuff.append(" AND UG.grph_domain_code IN (     ");
		selectQueryBuff.append(" SELECT grph_domain_code FROM   geographical_domains GD1 WHERE status IN('Y','S')  ");
		selectQueryBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code   ");
		selectQueryBuff.append(" START WITH grph_domain_code IN   ");
		selectQueryBuff.append(" (SELECT grph_domain_code FROM user_geographies UG1  ");
		selectQueryBuff.append(" WHERE UG1.grph_domain_code = (case ? when 'ALL' then UG1.grph_domain_code else ? end) ");
		selectQueryBuff.append(" AND UG1.user_id= ?))  ");
		selectQueryBuff.append(" GROUP BY CTRF.from_user_id, CTRF.to_user_id, U.user_name, CTRF.msisdn,U2.user_name, CTRF.to_msisdn ,CTRF.transfer_id,   ");
		selectQueryBuff.append(" L.lookup_name , CTRF.type, CTRF.transfer_date,CTRF.modified_ON,P.product_name, L1.lookup_name,   ");
		selectQueryBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code,SEND_CAT.category_name , REC_CAT.category_name,CTRF.source ");
		selectQueryBuff.append(" UNION  ");
		selectQueryBuff.append(" SELECT CTRF.from_user_id, CTRF.to_user_id, (U.user_name || '(' ||CTRF.msisdn||')') from_user,  ");
		selectQueryBuff.append("(U2.user_name || '(' ||CTRF.to_msisdn||')') to_user,CTRF.transfer_id, L.lookup_name transfer_sub_type,CTRF.type,  ");
		selectQueryBuff.append("  TO_CHAR(CTRF.transfer_date,?) transfer_date,  TO_CHAR(CTRF.modified_ON, ?) modified_ON,P.product_name, ");
		
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_REQUIRED_QUANTITY, "transfer_mrp").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_PAYABLE_AMOUNT, "payable_amount").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_NET_PAYABLE_AMOUNT, "net_payable_amount").append(" , ");
		  selectQueryBuff.append(" L1.lookup_name status, ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_MRP, "mrp").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_COMMISSION_VALUE, "commision").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_COMMISION_QUANTITY, "commision_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_RECEIVER_CREDIT_QUANTITY, "receiver_credit_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_SENDER_DEBIT_QUANTITY, "sender_debit_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX3_VALUE, "tax3_value").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX1_VALUE, "tax1_value").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX2_VALUE, "tax2_value").append(" , ");
		  
		  selectQueryBuff.append(" SUM(to_number(CTI.otf_amount))otf_amount, ");
		selectQueryBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code,   ");
		selectQueryBuff.append(" SEND_CAT.category_name sender_category_name, REC_CAT.category_name receiver_category_name,CTRF.source   ");
		selectQueryBuff.append(" FROM (SELECT USR.user_id   ");
		selectQueryBuff.append(" FROM users USR CONNECT BY PRIOR USR.user_id = USR.parent_id START WITH USR.user_id=? )X,  ");
		selectQueryBuff.append(" channel_transfers CTRF, channel_transfers_items CTI,users U,users U2,products P,lookups L, lookups L1, categories SEND_CAT,  ");
		selectQueryBuff.append(" categories REC_CAT,user_geographies UG    ");
		selectQueryBuff.append(" WHERE CTRF.type = 'C2C'     ");
		selectQueryBuff.append(" AND NVL(CTRF.close_date,CTRF.created_on) >=TO_DATE(?,?)  ");
		selectQueryBuff.append(" AND NVL(CTRF.close_date,CTRF.created_on) <=TO_DATE(?,?) ");
		selectQueryBuff.append(" AND CTRF.network_code = ?    ");
		selectQueryBuff.append(" AND REC_CAT.domain_code = ?   ");
		selectQueryBuff.append(" AND CTRF.control_transfer<>'A'  ");
		selectQueryBuff.append(" AND CTRF.sender_category_code = SEND_CAT.category_code   ");
		selectQueryBuff.append(" AND CTRF.receiver_category_code = REC_CAT.category_code");
		selectQueryBuff.append(" AND CTRF.receiver_category_code=CASE ? WHEN 'ALL' THEN CTRF.receiver_category_code ELSE ? END    ");
		selectQueryBuff.append(" AND CTRF.sender_category_code=CASE ? WHEN 'ALL' THEN CTRF.sender_category_code ELSE ? END  ");
		selectQueryBuff.append(" AND CTRF.TO_user_id=CASE ? WHEN 'ALL' THEN TO_user_id ELSE? END  ");
		selectQueryBuff.append(" AND CTRF.from_user_id=CASE ? WHEN 'ALL' THEN from_user_id ELSE ? END    ");
		selectQueryBuff.append(" AND CTRF.transfer_sub_type = case ? when 'ALL' then CTRF.transfer_sub_type else ? end  ");
		selectQueryBuff.append(" AND CTRF.to_user_id = X.user_id   ");
		selectQueryBuff.append(" AND U.user_id = CTRF.from_user_id ");
		selectQueryBuff.append(" AND U2.user_id =CTRF.to_user_id ");
		selectQueryBuff.append(" AND CTRF.transfer_id = CTI.transfer_id ");
		selectQueryBuff.append(" AND CTI.product_code = P.product_code ");
		selectQueryBuff.append(" AND L.lookup_type ='TRFT' ");
		selectQueryBuff.append(" AND L.lookup_code = CTRF.transfer_sub_type   ");
		selectQueryBuff.append(" AND CTRF.status = 'CLOSE'    ");
		selectQueryBuff.append(" AND L1.lookup_code = CTRF.status  ");
		selectQueryBuff.append(" AND L1.lookup_type = 'CTSTA'   ");
		selectQueryBuff.append(" AND CTRF.from_user_id = UG.user_id  ");
		selectQueryBuff.append(" AND UG.grph_domain_code IN (   ");
		selectQueryBuff.append(" SELECT grph_domain_code FROM  geographical_domains GD1 WHERE status IN('Y','S')  ");
		selectQueryBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
		selectQueryBuff.append(" START WITH grph_domain_code IN  (SELECT grph_domain_code FROM user_geographies UG1   ");
		selectQueryBuff.append(" WHERE UG1.grph_domain_code = (case ? when 'ALL' then UG1.grph_domain_code else ? end)  ");
		selectQueryBuff.append(" AND UG1.user_id= ?))  ");
		selectQueryBuff.append(" GROUP BY CTRF.from_user_id, CTRF.to_user_id, U.user_name, CTRF.msisdn,U2.user_name, CTRF.to_msisdn ,CTRF.transfer_id,  ");
		selectQueryBuff.append(" L.lookup_name , CTRF.type, CTRF.transfer_date,CTRF.modified_ON,P.product_name, L1.lookup_name,  ");
		selectQueryBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code,SEND_CAT.category_name , REC_CAT.category_name,CTRF.source  ");
		selectQueryBuff.append(" ORDER BY 8 Desc , 9 Desc ");
		
		 String sqlSelect=selectQueryBuff.toString();
		 if(log.isDebugEnabled()){ 
			 loggerValue.setLength(0);
	         loggerValue.append("QUERY SELECT = ");
	         loggerValue.append(sqlSelect);
			  log.debug(methodName,loggerValue); 
	     }
		 PreparedStatement pstmt =  conn.prepareStatement(sqlSelect);
        int i = 1;
        pstmt.setString(i++,Constants.getProperty("report.onlydateformat"));
		pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
		pstmt.setString(i++,usersReportModel.getLoginUserID());
		
		pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));
		pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
		pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));
		pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
		
		pstmt.setString(i++, usersReportModel.getNetworkCode());
		pstmt.setString(i++,usersReportModel.getDomainCode());
		
		pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
		pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
				
		pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());
		pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());
		
		pstmt.setString(i++,usersReportModel.getUserID());
		pstmt.setString(i++,usersReportModel.getUserID());
		
		pstmt.setString(i++,usersReportModel.getTouserID());
		pstmt.setString(i++,usersReportModel.getTouserID());
		
		pstmt.setString(i++,usersReportModel.getTxnSubType());
		pstmt.setString(i++,usersReportModel.getTxnSubType());
		
		pstmt.setString(i++,usersReportModel.getZoneCode());
		pstmt.setString(i++,usersReportModel.getZoneCode());
		
		pstmt.setString(i++,usersReportModel.getLoginUserID());
		
		pstmt.setString(i++,Constants.getProperty("report.onlydateformat"));
		pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
		
		pstmt.setString(i++,usersReportModel.getLoginUserID());

		pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));
		pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
		pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));
		pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
		
		pstmt.setString(i++, usersReportModel.getNetworkCode());
		pstmt.setString(i++,usersReportModel.getDomainCode());
		
		pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
		pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
				
		pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());
		pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());
		
		pstmt.setString(i++,usersReportModel.getUserID());
		pstmt.setString(i++,usersReportModel.getUserID());
		
		pstmt.setString(i++,usersReportModel.getTouserID());
		pstmt.setString(i++,usersReportModel.getTouserID());
		
		pstmt.setString(i++,usersReportModel.getTxnSubType());
		pstmt.setString(i++,usersReportModel.getTxnSubType());
		
		pstmt.setString(i++,usersReportModel.getZoneCode());
		pstmt.setString(i++,usersReportModel.getZoneCode());
		
		pstmt.setString(i,usersReportModel.getLoginUserID());
		
        return pstmt;
	}
	/**
	 * 
	 * @param conn
	 * @param usersReportModel
	 * @return PreparedStatement
	 * @throws SQLException
	 * @throws ParseException
	 */
	@Override
	public PreparedStatement loadC2cRetWidTransferChannelUserUnionStaffListQry(Connection conn,
			UsersReportModel usersReportModel) throws SQLException, ParseException {
		final String methodName = "loadC2cRetWidTransferChannelUserUnionStaffListQry";
		StringBuilder loggerValue= new StringBuilder(); 
		if(log.isDebugEnabled()){ 
			loggerValue.setLength(0);
        	loggerValue.append("Entered loginUserID ");
        	loggerValue.append(usersReportModel.getLoginUserID());
			loggerValue.append(" NetworkCode ");
        	loggerValue.append(usersReportModel.getNetworkCode());
        	loggerValue.append(" DomainCode ");
        	loggerValue.append(usersReportModel.getDomainCode());
			loggerValue.append(" FromTrfCatCode ");
        	loggerValue.append(usersReportModel.getFromtransferCategoryCode());
        	loggerValue.append(" ToTrfCatCode ");
        	loggerValue.append(usersReportModel.getTotransferCategoryCode());
        	loggerValue.append(" UserID ");
        	loggerValue.append(usersReportModel.getUserID());
        	loggerValue.append(" ToUserID ");
        	loggerValue.append(usersReportModel.getTouserID());
        	loggerValue.append(" TxnSubType ");
        	loggerValue.append(usersReportModel.getTxnSubType());
        	loggerValue.append(" ZoneCode ");
        	loggerValue.append(usersReportModel.getZoneCode());
			log.debug(methodName,loggerValue);
	     } 
		StringBuilder selectQueryBuff = new StringBuilder();
		  selectQueryBuff.append(" SELECT (OU.user_name || ' (' || OU.msisdn||')')owner_profile,(PU.user_name ||' (' ||NVL(PU.msisdn,'ROOT')||')')parent_profile, ");
		  selectQueryBuff.append(" CTRF.from_user_id, CTRF.to_user_id, (U.user_name || '(' ||CTRF.msisdn||')') from_user,(U2.user_name || '(' ||CTRF.to_msisdn||')') to_user,UC.user_name initiator_user,CTRF.transfer_id, ");
		
		  selectQueryBuff.append(" L.lookup_name transfer_sub_type, CTRF.type, TO_CHAR(CTRF.transfer_date, ? ) transfer_date, TO_CHAR(CTRF.modified_ON, ? ) modified_ON, P.product_name, ");
		 
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_REQUIRED_QUANTITY, "transfer_mrp").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_PAYABLE_AMOUNT, "payable_amount").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_NET_PAYABLE_AMOUNT, "net_payable_amount").append(" , ");
		  selectQueryBuff.append(" L1.lookup_name status, ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_MRP, "mrp").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_COMMISSION_VALUE, "commision").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_COMMISION_QUANTITY, "commision_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_RECEIVER_CREDIT_QUANTITY, "receiver_credit_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_SENDER_DEBIT_QUANTITY, "sender_debit_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX3_VALUE, "tax3_value").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX1_VALUE, "tax1_value").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX2_VALUE, "tax2_value").append(" , ");
		  
		  selectQueryBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code, ");
		  selectQueryBuff.append(" SEND_CAT.category_name sender_category_name, REC_CAT.category_name receiver_category_name,CTRF.source ");
		
		  selectQueryBuff.append(" FROM (SELECT USR.user_id FROM users USR CONNECT BY PRIOR USR.user_id = USR.parent_id START WITH USR.user_id= ? )X,  ");
		  selectQueryBuff.append(" channel_transfers CTRF, channel_transfers_items CTI,users U,users U2,users UC,products P,lookups L, lookups L1, categories SEND_CAT,  ");
		  selectQueryBuff.append(" categories REC_CAT,user_geographies UG,USERS PU,USERS OU ");
		  selectQueryBuff.append(" WHERE CTRF.type = 'C2C' ");
		
		  selectQueryBuff.append(" AND NVL(CTRF.close_date,CTRF.created_on) >=  TO_DATE(?,?) ");
			
		  selectQueryBuff.append(" AND NVL(CTRF.close_date,CTRF.created_on) <=  TO_DATE(?,?)	 ");	
		
		  selectQueryBuff.append(" AND CTRF.network_code = ?   AND SEND_CAT.domain_code  = ? ");
		  selectQueryBuff.append(" AND CTRF.control_transfer<>'A'   AND CTRF.sender_category_code = SEND_CAT.category_code ");
		  selectQueryBuff.append(" AND CTRF.receiver_category_code = REC_CAT.category_code ");
		
		  selectQueryBuff.append(" AND CTRF.sender_category_code=CASE ? WHEN 'ALL' THEN CTRF.sender_category_code ELSE ? END ");
		
		  selectQueryBuff.append(" AND CTRF.receiver_category_code=CASE ? WHEN 'ALL' THEN CTRF.receiver_category_code ELSE ? END ");
		
		  selectQueryBuff.append(" AND CTRF.from_user_id=CASE ? WHEN 'ALL' THEN from_user_id ELSE ? END ");
		
		  selectQueryBuff.append(" AND CTRF.to_user_id=CASE ? WHEN 'ALL' THEN to_user_id ELSE ? END ");
		 
		  selectQueryBuff.append(" AND CTRF.transfer_sub_type = case ? when 'ALL' then CTRF.transfer_sub_type else ? end ");
		  selectQueryBuff.append(" AND CTRF.from_user_id = X.user_id    AND U.user_id = CTRF.from_user_id   AND U2.user_id =CTRF.to_user_id ");
		  selectQueryBuff.append(" AND UC.user_id= CTRF.active_user_id   AND CTRF.transfer_id = CTI.transfer_id   AND CTI.product_code = P.product_code ");
		  selectQueryBuff.append(" AND U.parent_id=PU.user_id(+)   AND OU.USER_ID=U.OWNER_ID   AND L.lookup_type ='TRFT' ");
		  selectQueryBuff.append(" AND L.lookup_code = CTRF.transfer_sub_type   AND CTRF.status = 'CLOSE'   AND L1.lookup_code = CTRF.status ");
		  selectQueryBuff.append(" AND L1.lookup_type = 'CTSTA'   AND CTRF.to_user_id = UG.user_id   AND UG.grph_domain_code IN ( ");
		  selectQueryBuff.append(" SELECT grph_domain_code FROM   geographical_domains GD1 WHERE status IN('Y','S') ");
		  selectQueryBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code   START WITH grph_domain_code IN ");
		  selectQueryBuff.append(" (SELECT grph_domain_code FROM user_geographies UG1 ");
		 
		  selectQueryBuff.append(" WHERE UG1.grph_domain_code = (case ? when 'ALL' then UG1.grph_domain_code else ? end)   AND UG1.user_id= ? )) ");
		  selectQueryBuff.append(" GROUP BY OU.user_name,OU.msisdn,PU.user_name,PU.msisdn,CTRF.from_user_id, CTRF.to_user_id, U.user_name, CTRF.msisdn,U2.user_name, CTRF.to_msisdn ,UC.user_name, CTRF.transfer_id, ");
		  selectQueryBuff.append(" L.lookup_name , CTRF.type, CTRF.transfer_date,CTRF.modified_ON,P.product_name, L1.lookup_name, ");
		  selectQueryBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code,SEND_CAT.category_name , REC_CAT.category_name,CTRF.source ");
		  selectQueryBuff.append(" UNION   SELECT (OU.user_name || ' (' || OU.msisdn||')')owner_profile, ");
		  selectQueryBuff.append(" (PU.user_name ||' (' ||NVL(PU.msisdn,'ROOT')||')')parent_profile, ");
		  selectQueryBuff.append(" CTRF.from_user_id, CTRF.to_user_id, (U.user_name || '(' ||CTRF.msisdn||')') from_user,(U2.user_name || '(' ||CTRF.to_msisdn||')') to_user, UC.user_name initiator_user,CTRF.transfer_id, ");
		 
		  selectQueryBuff.append(" L.lookup_name transfer_sub_type, CTRF.type, TO_CHAR(CTRF.transfer_date,?) transfer_date,TO_CHAR(CTRF.modified_ON, ?) modified_ON, P.product_name,  ");
		  
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_REQUIRED_QUANTITY, "transfer_mrp").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_PAYABLE_AMOUNT, "payable_amount").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_NET_PAYABLE_AMOUNT, "net_payable_amount").append(" , ");
		  selectQueryBuff.append(" L1.lookup_name status, ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_MRP, "mrp").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_COMMISSION_VALUE, "commision").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_COMMISION_QUANTITY, "commision_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_RECEIVER_CREDIT_QUANTITY, "receiver_credit_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_SENDER_DEBIT_QUANTITY, "sender_debit_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX3_VALUE, "tax3_value").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX1_VALUE, "tax1_value").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX2_VALUE, "tax2_value").append(" , ");
		  
		  selectQueryBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code, ");
		  selectQueryBuff.append(" SEND_CAT.category_name sender_category_name, REC_CAT.category_name receiver_category_name,CTRF.source ");
		  selectQueryBuff.append(" FROM (SELECT USR.user_id "); 
		 
		  selectQueryBuff.append(" FROM users USR CONNECT BY PRIOR USR.user_id = USR.parent_id START WITH USR.user_id= ? )X,  ");
		  selectQueryBuff.append(" channel_transfers CTRF, channel_transfers_items CTI,users U,users U2,users UC,products P,lookups L, lookups L1, categories SEND_CAT,  ");
		  selectQueryBuff.append(" categories REC_CAT,user_geographies UG,USERS PU,USERS OU ");
		  selectQueryBuff.append(" WHERE CTRF.type = 'C2C' ");
		 
		  selectQueryBuff.append(" AND NVL(CTRF.close_date,CTRF.created_on) >=  TO_DATE(?,?) ");
		 	
		  selectQueryBuff.append(" AND NVL(CTRF.close_date,CTRF.created_on) <=  TO_DATE(?,?) ");	
		 
		  selectQueryBuff.append(" AND CTRF.network_code = ?   AND REC_CAT.domain_code = ? ");
		  selectQueryBuff.append(" AND CTRF.control_transfer<>'A'   AND CTRF.sender_category_code = SEND_CAT.category_code ");
		  selectQueryBuff.append(" AND CTRF.receiver_category_code = REC_CAT.category_code ");
		 
		  selectQueryBuff.append(" AND CTRF.receiver_category_code=CASE ? WHEN 'ALL' THEN CTRF.receiver_category_code ELSE ? END ");
		 
		  selectQueryBuff.append(" AND CTRF.sender_category_code=CASE ? WHEN 'ALL' THEN CTRF.sender_category_code ELSE ? END ");
		  
		  selectQueryBuff.append(" AND CTRF.TO_user_id=CASE ? WHEN 'ALL' THEN TO_user_id ELSE ? END ");
		  
		  selectQueryBuff.append(" AND CTRF.from_user_id=CASE ? WHEN 'ALL' THEN from_user_id ELSE ? END ");
		  
		  selectQueryBuff.append(" AND CTRF.transfer_sub_type = case ? when 'ALL' then CTRF.transfer_sub_type else ? end ");
		  selectQueryBuff.append(" AND CTRF.to_user_id = X.user_id   AND U.user_id = CTRF.from_user_id   AND U2.user_id =CTRF.to_user_id ");
		  selectQueryBuff.append(" AND UC.user_id= CTRF.active_user_id   AND CTRF.transfer_id = CTI.transfer_id   AND CTI.product_code = P.product_code ");
		  selectQueryBuff.append(" AND U.parent_id=PU.user_id(+)   AND OU.USER_ID=U.OWNER_ID   AND L.lookup_type ='TRFT' ");
		  selectQueryBuff.append(" AND L.lookup_code = CTRF.transfer_sub_type   AND CTRF.status = 'CLOSE'   AND L1.lookup_code = CTRF.status ");
		  selectQueryBuff.append(" AND L1.lookup_type = 'CTSTA'   AND CTRF.from_user_id = UG.user_id   AND UG.grph_domain_code IN ( ");
		  selectQueryBuff.append(" SELECT grph_domain_code FROM   geographical_domains GD1 WHERE status IN('Y','S') ");
		  selectQueryBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code   START WITH grph_domain_code IN ");
		  selectQueryBuff.append(" (SELECT grph_domain_code FROM user_geographies UG1 ");
		  selectQueryBuff.append(" WHERE UG1.grph_domain_code = (case ? when 'ALL' then UG1.grph_domain_code else ? end)  AND UG1.user_id= ? )) ");
		  selectQueryBuff.append(" GROUP BY OU.user_name,OU.msisdn,PU.user_name,PU.msisdn,CTRF.from_user_id, CTRF.to_user_id, U.user_name, CTRF.msisdn,U2.user_name, CTRF.to_msisdn ,UC.user_name,CTRF.transfer_id, ");
		  selectQueryBuff.append(" L.lookup_name , CTRF.type, CTRF.transfer_date,CTRF.modified_ON,P.product_name, L1.lookup_name, ");
		  selectQueryBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code,SEND_CAT.category_name , REC_CAT.category_name,CTRF.source ");
		  selectQueryBuff.append(" ORDER BY 11 Desc , 12 Desc ");
		String sqlSelect = selectQueryBuff.toString();
		if(log.isDebugEnabled()){ 
			loggerValue.setLength(0);
        	loggerValue.append("QUERY SELECT = ");
        	loggerValue.append(sqlSelect);
			  log.debug(methodName,loggerValue); 
	     }

		PreparedStatement pstmt = conn.prepareStatement(sqlSelect);
		int i = 1;
		i++;
		pstmt.setString(i,Constants.getProperty("report.onlydateformat"));
		i++;
		pstmt.setString(i,Constants.getProperty("report.datetimeformat"));
		i++;
		pstmt.setString(i,usersReportModel.getLoginUserID());
		i++;
		pstmt.setString(i, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));
		i++;
		pstmt.setString(i,Constants.getProperty("report.datetimeformat"));
		i++;
		pstmt.setString(i, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));
		i++;
		pstmt.setString(i,Constants.getProperty("report.datetimeformat"));
		i++;
		pstmt.setString(i, usersReportModel.getNetworkCode());
		i++;
		pstmt.setString(i,usersReportModel.getDomainCode());
		i++;
		pstmt.setString(i,usersReportModel.getFromtransferCategoryCode());
		i++;
		pstmt.setString(i,usersReportModel.getFromtransferCategoryCode());
		i++;		
		pstmt.setString(i,usersReportModel.getTotransferCategoryCode());
		i++;
		pstmt.setString(i,usersReportModel.getTotransferCategoryCode());
		i++;
		pstmt.setString(i,usersReportModel.getUserID());
		i++;
		pstmt.setString(i,usersReportModel.getUserID());
		i++;
		pstmt.setString(i,usersReportModel.getTouserID());
		i++;
		pstmt.setString(i,usersReportModel.getTouserID());
		i++;
		pstmt.setString(i,usersReportModel.getTxnSubType());
		i++;
		pstmt.setString(i,usersReportModel.getTxnSubType());
		i++;
		pstmt.setString(i,usersReportModel.getZoneCode());
		i++;
		pstmt.setString(i,usersReportModel.getZoneCode());
		i++;
		
		pstmt.setString(i,usersReportModel.getLoginUserID());
		i++;
		pstmt.setString(i,Constants.getProperty("report.onlydateformat"));
		i++;
		pstmt.setString(i,Constants.getProperty("report.datetimeformat"));
		i++;
		
		pstmt.setString(i,usersReportModel.getLoginUserID());
		i++;
        pstmt.setString(i, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));
        i++;
		pstmt.setString(i,Constants.getProperty("report.datetimeformat"));
		i++;
		pstmt.setString(i, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));
		i++;
		pstmt.setString(i,Constants.getProperty("report.datetimeformat"));
		i++;
		pstmt.setString(i, usersReportModel.getNetworkCode());
		i++;
		pstmt.setString(i,usersReportModel.getDomainCode());
		i++;
		
		pstmt.setString(i,usersReportModel.getFromtransferCategoryCode());
		i++;
		pstmt.setString(i,usersReportModel.getFromtransferCategoryCode());
		i++;	
		pstmt.setString(i,usersReportModel.getTotransferCategoryCode());
		i++;
		pstmt.setString(i,usersReportModel.getTotransferCategoryCode());
		i++;
		pstmt.setString(i,usersReportModel.getUserID());
		i++;
		pstmt.setString(i,usersReportModel.getUserID());
		i++;
		pstmt.setString(i,usersReportModel.getTouserID());
		i++;
		pstmt.setString(i,usersReportModel.getTouserID());
		i++;
		pstmt.setString(i,usersReportModel.getTxnSubType());
		i++;
		pstmt.setString(i,usersReportModel.getTxnSubType());
		i++;
		pstmt.setString(i,usersReportModel.getZoneCode());
		i++;
		pstmt.setString(i,usersReportModel.getZoneCode());
		i++;
		pstmt.setString(i,usersReportModel.getLoginUserID());
		return pstmt;

	}
	/**
	 * 
	 * @param conn
	 * @param usersReportModel
	 * @return PreparedStatement
	 * @throws SQLException
	 * @throws ParseException
	 */
	@Override
	public PreparedStatement loadC2cRetWidTransferChannelUserListQry(Connection conn, UsersReportModel usersReportModel)
			throws SQLException, ParseException {
		final String methodName = "loadC2cRetWidTransferChannelUserListQry";
		StringBuilder loggerValue= new StringBuilder(); 
		if(log.isDebugEnabled()){ 
			loggerValue.append("Entered loginUserID ");
        	loggerValue.append(usersReportModel.getLoginUserID());
			loggerValue.append(" NetworkCode ");
        	loggerValue.append(usersReportModel.getNetworkCode());
        	loggerValue.append(" DomainCode ");
        	loggerValue.append(usersReportModel.getDomainCode());
			loggerValue.append(" FromTrfCatCode ");
        	loggerValue.append(usersReportModel.getFromtransferCategoryCode());
        	loggerValue.append(" ToTrfCatCode ");
        	loggerValue.append(usersReportModel.getTotransferCategoryCode());
        	loggerValue.append(" UserID ");
        	loggerValue.append(usersReportModel.getUserID());
			loggerValue.append(" ToUserID ");
        	loggerValue.append(usersReportModel.getTouserID());
        	loggerValue.append(" TxnSubType ");
        	loggerValue.append(usersReportModel.getTxnSubType());
        	loggerValue.append(" ZoneCode ");
			loggerValue.append(usersReportModel.getZoneCode());
        	loggerValue.append(" TrfIN/OUT ");
        	loggerValue.append(usersReportModel.getTransferInOrOut());
			log.debug(methodName,loggerValue);
	     } 
		StringBuilder selectQueryBuff = new StringBuilder();
		 selectQueryBuff.append(" SELECT (OU.user_name || ' (' || OU.msisdn||')')owner_profile,   (PU.user_name ||' (' ||NVL(PU.msisdn,'ROOT')||')')parent_profile, ");
		 selectQueryBuff.append(" CTRF.from_user_id, CTRF.to_user_id, U.user_name from_user,CTRF.msisdn from_msisdn ,U2.user_name to_user , ");
		 selectQueryBuff.append(" CTRF.to_msisdn to_msisdn,GD2.GRPH_DOMAIN_NAME,CTRF.transfer_id, L.lookup_name transfer_sub_type, GD2.GRPH_DOMAIN_NAME,CTRF.type, ");
		 selectQueryBuff.append(" TO_CHAR(CTRF.transfer_date, ? ) transfer_date,   TO_CHAR(CTRF.modified_ON, ? ) modified_ON,P.product_name, ");
		 
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_REQUIRED_QUANTITY, "transfer_mrp").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_PAYABLE_AMOUNT, "payable_amount").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_NET_PAYABLE_AMOUNT, "net_payable_amount").append(" , ");
		  selectQueryBuff.append(" L1.lookup_name status, ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_MRP, "mrp").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_COMMISSION_VALUE, "commision").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_COMMISION_QUANTITY, "commision_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_RECEIVER_CREDIT_QUANTITY, "receiver_credit_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_SENDER_DEBIT_QUANTITY, "sender_debit_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX3_VALUE, "tax3_value").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX1_VALUE, "tax1_value").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX2_VALUE, "tax2_value").append(" , ");
		
		 selectQueryBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code,   SEND_CAT.category_name sender_category_name,  ");
		 selectQueryBuff.append(" REC_CAT.category_name receiver_category_name,CTRF.SOURCE,GD.GRPH_DOMAIN_NAME   FROM (SELECT USR.user_id  ");
		 
		 selectQueryBuff.append(" FROM users USR CONNECT BY PRIOR USR.user_id = USR.parent_id START WITH USR.user_id= ? )X, ");
		 selectQueryBuff.append(" channel_transfers CTRF, channel_transfers_items CTI,users U,users U2,products P,lookups L, lookups L1, categories SEND_CAT, "); 
		 selectQueryBuff.append(" CATEGORIES REC_CAT,USER_GEOGRAPHIES UG,USERS OU,USER_GEOGRAPHIES UGW,GEOGRAPHICAL_DOMAINS GD2,GEOGRAPHICAL_DOMAINS GD, ");
		 selectQueryBuff.append(" USERS PU WHERE CTRF.type = 'C2C' ");
		 	
		 selectQueryBuff.append(" AND NVL(CTRF.close_date,CTRF.created_on) >=TO_DATE(?,?) ");
		 	
		 selectQueryBuff.append(" AND NVL(CTRF.close_date,CTRF.created_on) <=TO_DATE(?,?) ");
		 
		 selectQueryBuff.append(" AND CTRF.network_code = ?   AND CTRF.control_transfer<>'A' ");
		 
		 selectQueryBuff.append(" AND case ? when 'OUT' then SEND_CAT.domain_code else REC_CAT.domain_code end = ? ");
		 selectQueryBuff.append(" AND CTRF.sender_category_code = SEND_CAT.category_code   AND CTRF.receiver_category_code = REC_CAT.category_code ");
		 
		 selectQueryBuff.append(" AND CASE ? WHEN 'OUT' THEN CTRF.sender_category_code ELSE CTRF.receiver_category_code END = CASE ? WHEN 'OUT' THEN (CASE ? WHEN 'ALL' THEN CTRF.sender_category_code ELSE ? END) ELSE (CASE ?  WHEN  'ALL' THEN CTRF.receiver_category_code ELSE ? END) END ");
		 
		 selectQueryBuff.append(" AND CASE ? WHEN 'OUT' THEN CTRF.receiver_category_code ELSE CTRF.sender_category_code END = CASE ? WHEN 'OUT' THEN (CASE ? WHEN 'ALL' THEN CTRF.receiver_category_code ELSE ?  END) ELSE (CASE ?  WHEN  'ALL' THEN CTRF.sender_category_code ELSE ? END) END ");
		 
		 selectQueryBuff.append(" AND CASE ? WHEN 'OUT' THEN CTRF.from_user_id ELSE CTRF.to_user_id END = CASE ? WHEN 'OUT' THEN (CASE ? WHEN 'ALL' THEN from_user_id ELSE ? END) ELSE (CASE ? WHEN 'ALL' THEN CTRF.to_user_id ELSE ? END) END ");
		 
		 selectQueryBuff.append(" AND CASE ? WHEN 'OUT' THEN CTRF.to_user_id ELSE CTRF.from_user_id END = CASE ? WHEN 'OUT' THEN (CASE ? WHEN 'ALL' THEN to_user_id ELSE ? END) ELSE (CASE ? WHEN 'ALL' THEN CTRF.from_user_id ELSE ? END) END ");
		 
		 selectQueryBuff.append(" AND CTRF.transfer_sub_type = case ? when 'ALL' then CTRF.transfer_sub_type else ? end ");
		 
		 selectQueryBuff.append(" AND CASE ? WHEN 'OUT' THEN CTRF.from_user_id ELSE CTRF.to_user_id END =  X.user_id ");
		 selectQueryBuff.append(" AND U.user_id = CTRF.from_user_id   AND U2.user_id =CTRF.to_user_id   AND CTRF.transfer_id = CTI.transfer_id ");
		 selectQueryBuff.append(" AND CTI.product_code = P.product_code   AND U.parent_id=PU.user_id(+)   AND OU.USER_ID=U.OWNER_ID ");
		 selectQueryBuff.append(" AND UGW.USER_ID=OU.USER_ID   AND UGW.GRPH_DOMAIN_CODE=GD2.GRPH_DOMAIN_CODE   AND UG.GRPH_DOMAIN_CODE =GD.GRPH_DOMAIN_CODE ");
		 selectQueryBuff.append(" AND L.lookup_type ='TRFT'   AND L.lookup_code = CTRF.transfer_sub_type   AND CTRF.status = 'CLOSE' ");
		 selectQueryBuff.append(" AND L1.lookup_code = CTRF.status   AND L1.lookup_type = 'CTSTA' ");
		 
		 selectQueryBuff.append(" AND case ? when 'OUT' then CTRF.to_user_id else CTRF.from_user_id end= UG.user_id ");
		 selectQueryBuff.append(" GROUP BY OU.user_name,OU.msisdn,PU.user_name,PU.msisdn,CTRF.from_user_id, CTRF.to_user_id, U.user_name, CTRF.msisdn,U2.user_name, CTRF.to_msisdn ,CTRF.transfer_id, ");
		 selectQueryBuff.append(" L.lookup_name , CTRF.type, CTRF.transfer_date,CTRF.modified_ON,P.product_name, L1.lookup_name, GD2.GRPH_DOMAIN_NAME, ");
		 selectQueryBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code,SEND_CAT.category_name , REC_CAT.category_name,CTRF.SOURCE,GD.GRPH_DOMAIN_NAME ");
		 selectQueryBuff.append(" ORDER BY 14 Desc , 15 Desc  ");
		String sqlSelect = selectQueryBuff.toString();
		if(log.isDebugEnabled()){ 
			loggerValue.setLength(0);
        	loggerValue.append("QUERY SELECT = ");
        	loggerValue.append(sqlSelect);
			  log.debug(methodName,"QUERY SELECT = "+ sqlSelect); 
	     }
		PreparedStatement pstmt = conn.prepareStatement(sqlSelect);
		int i = 1;
		 pstmt.setString(i++,Constants.getProperty("report.onlydateformat"));
			pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
			pstmt.setString(i++,usersReportModel.getLoginUserID());
			
			pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));
			pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
			pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));
			pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
			
			pstmt.setString(i++, usersReportModel.getNetworkCode());
			
			pstmt.setString(i++,usersReportModel.getTransferInOrOut());
			pstmt.setString(i++,usersReportModel.getDomainCode());
			
			pstmt.setString(i++,usersReportModel.getTransferInOrOut());
			pstmt.setString(i++,usersReportModel.getTransferInOrOut());
			pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
			pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
			pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
			pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
			
			pstmt.setString(i++,usersReportModel.getTransferInOrOut());
			pstmt.setString(i++,usersReportModel.getTransferInOrOut());
			pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());
			pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());
			pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());
			pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());
			
			pstmt.setString(i++,usersReportModel.getTransferInOrOut());
			pstmt.setString(i++,usersReportModel.getTransferInOrOut());
			pstmt.setString(i++,usersReportModel.getUserID());
			pstmt.setString(i++,usersReportModel.getUserID());
			pstmt.setString(i++,usersReportModel.getUserID());
			pstmt.setString(i++,usersReportModel.getUserID());
			
			pstmt.setString(i++,usersReportModel.getTransferInOrOut());
			pstmt.setString(i++,usersReportModel.getTransferInOrOut());
			pstmt.setString(i++,usersReportModel.getTouserID());
			pstmt.setString(i++,usersReportModel.getTouserID());
			pstmt.setString(i++,usersReportModel.getTouserID());
			pstmt.setString(i++,usersReportModel.getTouserID());
			
			pstmt.setString(i++,usersReportModel.getTxnSubType());
			pstmt.setString(i++,usersReportModel.getTxnSubType());
			
			pstmt.setString(i++,usersReportModel.getTransferInOrOut());
			pstmt.setString(i,usersReportModel.getTransferInOrOut());
		return pstmt;
	}
	/**
	 * 
	 * @param conn
	 * @param usersReportModel
	 * @return PreparedStatement
	 * @throws SQLException
	 * @throws ParseException
	 */
	@Override
	public PreparedStatement loadC2cRetWidTransferChnlUserStaffListQry(Connection conn,
			UsersReportModel usersReportModel) throws SQLException, ParseException {
		 final String methodName = "loadC2cRetWidTransferChnlUserStaffListQry";
		 StringBuilder loggerValue= new StringBuilder(); 
		 if(log.isDebugEnabled()){ 
			loggerValue.setLength(0);
         	loggerValue.append("Entered loginUserID ");
         	loggerValue.append(usersReportModel.getLoginUserID());
			loggerValue.append(" NetworkCode ");
         	loggerValue.append(usersReportModel.getNetworkCode());
         	loggerValue.append(" DomainCode ");
         	loggerValue.append(usersReportModel.getDomainCode());
			loggerValue.append(" FromTrfCatCode ");
         	loggerValue.append(usersReportModel.getFromtransferCategoryCode());
         	loggerValue.append(" ToTrfCatCode ");
			loggerValue.append(usersReportModel.getTotransferCategoryCode());
         	loggerValue.append(" UserID ");
         	loggerValue.append(usersReportModel.getUserID());
         	loggerValue.append(" ToUserID ");
			loggerValue.append(usersReportModel.getTouserID());
         	loggerValue.append(" TxnSubType ");
         	loggerValue.append(usersReportModel.getTxnSubType());
			loggerValue.append(" ZoneCode ");
         	loggerValue.append(usersReportModel.getZoneCode());
         	loggerValue.append(" TrfIN/OUT ");
         	loggerValue.append(usersReportModel.getTransferInOrOut());
				log.debug(methodName,loggerValue);
		     } 
		StringBuilder selectQueryBuff = new StringBuilder();
		selectQueryBuff.append(" SELECT (OU.user_name || ' (' || OU.msisdn||')')owner_profile, (PU.user_name ||' (' ||NVL(PU.msisdn,'ROOT')||')')parent_profile, ");
		selectQueryBuff.append(" CTRF.from_user_id, CTRF.to_user_id, (U.user_name || ' (' ||CTRF.msisdn||')') from_user, ");
		selectQueryBuff.append(" (U2.user_name || ' (' ||CTRF.to_msisdn||')') to_user,UC.user_name initiator_user,CTRF.transfer_id,  L.lookup_name transfer_sub_type, ");
		selectQueryBuff.append(" CTRF.type, TO_CHAR(CTRF.transfer_date, ? ) transfer_date, TO_CHAR(CTRF.modified_ON, ? ) modified_ON,P.product_name, ");
		 
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_REQUIRED_QUANTITY, "transfer_mrp").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_PAYABLE_AMOUNT, "payable_amount").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_NET_PAYABLE_AMOUNT, "net_payable_amount").append(" , ");
		  selectQueryBuff.append(" L1.lookup_name status, ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_MRP, "mrp").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_COMMISSION_VALUE, "commision").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_COMMISION_QUANTITY, "commision_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_RECEIVER_CREDIT_QUANTITY, "receiver_credit_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_SENDER_DEBIT_QUANTITY, "sender_debit_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX3_VALUE, "tax3_value").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX1_VALUE, "tax1_value").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX2_VALUE, "tax2_value").append(" , ");
		  
		selectQueryBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code,  SEND_CAT.category_name sender_category_name, REC_CAT.category_name receiver_category_name,CTRF.source ");
		selectQueryBuff.append(" FROM (SELECT USR.user_id   FROM users USR CONNECT BY PRIOR USR.user_id = USR.parent_id START WITH USR.user_id= ? )X, ");
		selectQueryBuff.append(" channel_transfers CTRF, channel_transfers_items CTI,users U,users U2,users UC,products P,lookups L, lookups L1, categories SEND_CAT,  ");
		selectQueryBuff.append(" categories REC_CAT,user_geographies UG,USERS PU,USERS OU WHERE CTRF.type = 'C2C' ");
		selectQueryBuff.append(" AND NVL(CTRF.close_date,CTRF.created_on) >=  TO_DATE(?,?) ");
		selectQueryBuff.append(" AND NVL(CTRF.close_date,CTRF.created_on) <=  TO_DATE(?,?) ");
		selectQueryBuff.append(" AND CTRF.network_code = ?   AND CTRF.control_transfer<>'A' ");
		selectQueryBuff.append(" AND case ? when 'OUT' then SEND_CAT.domain_code else REC_CAT.domain_code end = ? ");
		selectQueryBuff.append(" AND CTRF.sender_category_code = SEND_CAT.category_code   AND CTRF.receiver_category_code = REC_CAT.category_code ");
		selectQueryBuff.append(" AND CASE ? WHEN 'OUT' THEN CTRF.sender_category_code ELSE CTRF.receiver_category_code END = CASE ? WHEN 'OUT' THEN (CASE ? WHEN 'ALL' THEN CTRF.sender_category_code ELSE ? END) ELSE (CASE ?  WHEN  'ALL' THEN CTRF.receiver_category_code ELSE ? END) END ");
		selectQueryBuff.append(" AND CASE ? WHEN 'OUT' THEN CTRF.receiver_category_code ELSE CTRF.sender_category_code END = CASE ? WHEN 'OUT' THEN (CASE ? WHEN 'ALL' THEN CTRF.receiver_category_code ELSE ?  END) ELSE (CASE ?  WHEN  'ALL' THEN CTRF.sender_category_code ELSE ? END) END ");
		selectQueryBuff.append(" AND CASE ? WHEN 'OUT' THEN CTRF.from_user_id ELSE CTRF.to_user_id END = CASE ? WHEN 'OUT' THEN (CASE ? WHEN 'ALL' THEN from_user_id ELSE ? END) ELSE (CASE ? WHEN 'ALL' THEN CTRF.to_user_id ELSE ? END) END ");
		selectQueryBuff.append(" AND CASE ? WHEN 'OUT' THEN CTRF.to_user_id ELSE CTRF.from_user_id END = CASE ? WHEN 'OUT' THEN (CASE ? WHEN 'ALL' THEN to_user_id ELSE ? END) ELSE (CASE ? WHEN 'ALL' THEN CTRF.from_user_id ELSE ? END) END ");
		selectQueryBuff.append(" AND CTRF.transfer_sub_type = case ? when 'ALL' then CTRF.transfer_sub_type else ? end ");
		selectQueryBuff.append(" AND CASE ? WHEN 'OUT' THEN CTRF.from_user_id ELSE CTRF.to_user_id END =  X.user_id ");
		selectQueryBuff.append(" AND U.user_id = CTRF.from_user_id   AND U2.user_id =CTRF.to_user_id   AND UC.user_id= CTRF.active_user_id ");
		selectQueryBuff.append(" AND CTRF.transfer_id = CTI.transfer_id   AND CTI.product_code = P.product_code   AND U.parent_id=PU.user_id(+) ");
		selectQueryBuff.append(" AND OU.USER_ID=U.OWNER_ID   AND L.lookup_type ='TRFT'   AND L.lookup_code = CTRF.transfer_sub_type ");
		selectQueryBuff.append(" AND CTRF.status = 'CLOSE'   AND L1.lookup_code = CTRF.status   AND L1.lookup_type = 'CTSTA' ");
		selectQueryBuff.append(" AND case ? when 'OUT' then CTRF.to_user_id else CTRF.from_user_id end= UG.user_id ");
		selectQueryBuff.append(" AND UG.grph_domain_code IN (SELECT grph_domain_code FROM   geographical_domains GD1 WHERE status IN('Y','S') ");
		selectQueryBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code   START WITH grph_domain_code IN ");
		selectQueryBuff.append(" (SELECT grph_domain_code FROM user_geographies UG1 ");
		selectQueryBuff.append(" WHERE UG1.grph_domain_code = (case ? when 'ALL' then UG1.grph_domain_code else ? end)   AND UG1.user_id= ? )) ");
		selectQueryBuff.append(" GROUP BY OU.user_name,OU.msisdn,PU.user_name,PU.msisdn,CTRF.from_user_id, CTRF.to_user_id, U.user_name, CTRF.msisdn,U2.user_name, CTRF.to_msisdn ,UC.user_name,CTRF.transfer_id, ");
		selectQueryBuff.append(" L.lookup_name , CTRF.type, CTRF.transfer_date,CTRF.modified_ON,P.product_name, L1.lookup_name, ");
		selectQueryBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code,SEND_CAT.category_name , REC_CAT.category_name,CTRF.source ");
		selectQueryBuff.append(" ORDER BY 11 Desc , 12 Desc ");
		String sqlSelect = selectQueryBuff.toString();
		if(log.isDebugEnabled()){ 
			loggerValue.setLength(0);
         	loggerValue.append("QUERY SELECT = ");
         	loggerValue.append(sqlSelect);
			  log.debug(methodName,loggerValue); 
	     }
		PreparedStatement pstmt = conn.prepareStatement(sqlSelect);
		int i = 1;
		pstmt.setString(i++,Constants.getProperty("report.onlydateformat"));
		pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
		pstmt.setString(i++,usersReportModel.getLoginUserID());
				
		pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));
		pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
		pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));
		pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
		
		pstmt.setString(i++, usersReportModel.getNetworkCode());
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getDomainCode());
		
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
		pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
		pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
		pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
		
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());
		pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());
		pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());
		pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());
		
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getUserID());
		pstmt.setString(i++,usersReportModel.getUserID());
		pstmt.setString(i++,usersReportModel.getUserID());
		pstmt.setString(i++,usersReportModel.getUserID());
		
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getTouserID());
		pstmt.setString(i++,usersReportModel.getTouserID());
		pstmt.setString(i++,usersReportModel.getTouserID());
		pstmt.setString(i++,usersReportModel.getTouserID());
		
		pstmt.setString(i++,usersReportModel.getTxnSubType());
		pstmt.setString(i++,usersReportModel.getTxnSubType());
		
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		
		pstmt.setString(i++,usersReportModel.getZoneCode());
		pstmt.setString(i++,usersReportModel.getZoneCode());
		pstmt.setString(i,usersReportModel.getLoginUserID());
		return pstmt;
	}
	/**
	 * 
	 * @param conn
	 * @param usersReportModel
	 * @return PreparedStatement
	 * @throws SQLException
	 * @throws ParseException
	 */
	@Override
	public PreparedStatement loadC2cRetWidTransferUnionListQry(Connection conn, UsersReportModel usersReportModel)
			throws SQLException, ParseException {
		final String methodName = "loadC2cRetWidTransferUnionListQry";
		StringBuilder loggerValue= new StringBuilder(); 
		if(log.isDebugEnabled()){ 
			loggerValue.setLength(0);
        	loggerValue.append("Entered loginUserID ");
        	loggerValue.append(usersReportModel.getLoginUserID());
			loggerValue.append(" NetworkCode ");
        	loggerValue.append(usersReportModel.getNetworkCode());
        	loggerValue.append(" DomainCode ");
        	loggerValue.append(usersReportModel.getDomainCode());
			loggerValue.append(" FromTrfCatCode ");
        	loggerValue.append(usersReportModel.getFromtransferCategoryCode());
        	loggerValue.append(" ToTrfCatCode ");
			loggerValue.append(usersReportModel.getTotransferCategoryCode());
        	loggerValue.append(" UserID ");
        	loggerValue.append(usersReportModel.getUserID());
        	loggerValue.append(" ToUserID ");
			loggerValue.append(usersReportModel.getTouserID());
        	loggerValue.append(" TxnSubType ");
			loggerValue.append(usersReportModel.getTxnSubType());
        	loggerValue.append(" ZoneCode ");
			loggerValue.append(usersReportModel.getZoneCode());
			log.debug(methodName,loggerValue);
	     } 
		StringBuilder selectQueryBuff = new StringBuilder();
		 selectQueryBuff.append(" SELECT (OU.user_name || ' (' || OU.msisdn||')')owner_profile,(PU.user_name ||' (' ||NVL(PU.msisdn,'ROOT')||')')parent_profile,CTRF.from_user_id, CTRF.to_user_id, ");
		 selectQueryBuff.append(" FU.user_name from_user, CTRF.msisdn from_msisdn, TU.user_name to_user,CTRF.to_msisdn to_msisdn,CTRF.transfer_id,L.lookup_name transfer_sub_type, CTRF.TYPE, ");
		 selectQueryBuff.append(" TO_CHAR(CTRF.transfer_date, ? ) transfer_date, TO_CHAR(CTRF.modified_ON, ? ) modified_ON,P.product_name, ");
		 
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_REQUIRED_QUANTITY, "transfer_mrp").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_PAYABLE_AMOUNT, "payable_amount").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_NET_PAYABLE_AMOUNT, "net_payable_amount").append(" , ");
		  selectQueryBuff.append(" L1.lookup_name status, ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_MRP, "mrp").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_COMMISSION_VALUE, "commision").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_COMMISION_QUANTITY, "commision_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_RECEIVER_CREDIT_QUANTITY, "receiver_credit_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_SENDER_DEBIT_QUANTITY, "sender_debit_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX3_VALUE, "tax3_value").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX1_VALUE, "tax1_value").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX2_VALUE, "tax2_value").append(" , ");
		  
		 selectQueryBuff.append(" SUM(to_number(CTI.otf_amount)) otf_amount,");
		 selectQueryBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code,SEND_CAT.category_name sender_category_name, ");
		 selectQueryBuff.append(" REC_CAT.category_name receiver_category_name,CTRF.SOURCE,   TUGD.grph_domain_name to_user_geo,TWGD.grph_domain_name to_owner_geo,  ");
		 selectQueryBuff.append(" FUGD.grph_domain_name from_user_geo ,FWGD.grph_domain_name from_owner_geo,FU.external_code from_ext_code,TU.external_code to_ext_code ");
		 selectQueryBuff.append(" FROM CHANNEL_TRANSFERS CTRF, CHANNEL_TRANSFERS_ITEMS CTI,USERS FU,USERS TU,PRODUCTS P,LOOKUPS L, LOOKUPS L1,  ");
		 selectQueryBuff.append(" CATEGORIES SEND_CAT, CATEGORIES REC_CAT,USER_GEOGRAPHIES UG,USERS OU,USER_GEOGRAPHIES UGW, ");
		 selectQueryBuff.append(" GEOGRAPHICAL_DOMAINS TUGD, GEOGRAPHICAL_DOMAINS TWGD, GEOGRAPHICAL_DOMAINS FUGD,GEOGRAPHICAL_DOMAINS FWGD, ");
		 selectQueryBuff.append(" USER_GEOGRAPHIES FUG,USER_GEOGRAPHIES FUGW,USERS PU  WHERE CTRF.TYPE = 'C2C' ");
		
		 selectQueryBuff.append(" AND NVL(CTRF.close_date,CTRF.created_on) >=  TO_DATE(?,?) ");
		    
		 selectQueryBuff.append(" AND NVL(CTRF.close_date,CTRF.created_on) <=  TO_DATE(?,?) ");  
		
		 selectQueryBuff.append(" AND CTRF.network_code = ?   AND SEND_CAT.domain_code  = ?   AND CTRF.control_transfer<>'A' ");
		 selectQueryBuff.append(" AND CTRF.sender_category_code = SEND_CAT.category_code   AND CTRF.receiver_category_code = REC_CAT.category_code ");
		 
		 selectQueryBuff.append(" AND CTRF.sender_category_code=CASE  ?  WHEN 'ALL' THEN CTRF.sender_category_code ELSE  ? END ");
		
		 selectQueryBuff.append(" AND CTRF.receiver_category_code=CASE ? WHEN 'ALL' THEN CTRF.receiver_category_code ELSE ? END ");
		
		 selectQueryBuff.append(" AND CTRF.from_user_id=CASE ? WHEN 'ALL' THEN from_user_id ELSE ? END ");
		
		 selectQueryBuff.append(" AND CTRF.to_user_id=CASE ? WHEN 'ALL' THEN to_user_id ELSE ? END ");
		
		 selectQueryBuff.append(" AND CTRF.transfer_sub_type = CASE ? WHEN 'ALL' THEN CTRF.transfer_sub_type ELSE ? END ");
		 selectQueryBuff.append(" AND FU.user_id = CTRF.from_user_id   AND TU.user_id =CTRF.to_user_id   AND FU.owner_id=FUGW.user_id ");
		 selectQueryBuff.append(" AND FUGW.grph_domain_code=FWGD.grph_domain_code   AND FU.user_id=FUG.user_id   AND FUG.grph_domain_code=FUGD.grph_domain_code ");
		 selectQueryBuff.append(" AND CTRF.transfer_id = CTI.transfer_id   AND CTI.product_code = P.product_code   AND FU.parent_id=PU.user_id(+) ");
		 selectQueryBuff.append(" AND OU.user_id=FU.owner_id   AND UGW.user_id=OU.user_id   AND UGW.grph_domain_code=TWGD.grph_domain_code ");
		 selectQueryBuff.append(" AND L.lookup_type ='TRFT'   AND L.lookup_code = CTRF.transfer_sub_type   AND CTRF.status = 'CLOSE' ");
		 selectQueryBuff.append(" AND L1.lookup_code = CTRF.status   AND L1.lookup_type = 'CTSTA'   AND CTRF.to_user_id = UG.user_id ");
		 selectQueryBuff.append(" AND UG.grph_domain_code=TUGD.grph_domain_code   AND UG.grph_domain_code IN (SELECT grph_domain_code  ");
		 selectQueryBuff.append(" FROM   GEOGRAPHICAL_DOMAINS GD1 WHERE status IN('Y','S') CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
		 selectQueryBuff.append(" START WITH grph_domain_code IN   (SELECT grph_domain_code FROM USER_GEOGRAPHIES UG1 ");
		
		 selectQueryBuff.append(" WHERE UG1.grph_domain_code = (CASE ?  WHEN 'ALL' THEN UG1.grph_domain_code ELSE  ? END)   AND UG1.user_id= ? )) ");
		 selectQueryBuff.append(" GROUP BY OU.user_name,OU.msisdn,PU.user_name,PU.msisdn, CTRF.from_user_id, CTRF.to_user_id, FU.user_name, CTRF.msisdn,TU.user_name, CTRF.to_msisdn ,CTRF.transfer_id, ");
		 selectQueryBuff.append(" TWGD.grph_domain_name, L.lookup_name , CTRF.TYPE, CTRF.transfer_date,CTRF.modified_ON,P.product_name, L1.lookup_name, ");
		 selectQueryBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code,SEND_CAT.category_name , ");
		 selectQueryBuff.append(" REC_CAT.category_name,CTRF.SOURCE,TUGD.grph_domain_name,FUGD.grph_domain_name,FWGD.grph_domain_name,FU.external_code,TU.external_code ");
		 selectQueryBuff.append(" UNION   SELECT  (OU.user_name || ' (' || OU.msisdn||')')owner_profile, ");
		 selectQueryBuff.append(" (PU.user_name ||' (' ||NVL(PU.msisdn,'ROOT')||')')parent_profile,CTRF.from_user_id, CTRF.to_user_id, FU.user_name from_user,  ");
		 selectQueryBuff.append(" CTRF.msisdn from_msisdn, TU.user_name to_user,    CTRF.to_msisdn to_msisdn,CTRF.transfer_id,L.lookup_name transfer_sub_type, CTRF.TYPE,  ");
		 selectQueryBuff.append(" TO_CHAR(CTRF.transfer_date, ? ) transfer_date,   TO_CHAR(CTRF.modified_ON, ? ) modified_ON, P.product_name, ");
		 
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_REQUIRED_QUANTITY, "transfer_mrp").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_PAYABLE_AMOUNT, "payable_amount").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_NET_PAYABLE_AMOUNT, "net_payable_amount").append(" , ");
		  selectQueryBuff.append(" L1.lookup_name status, ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_MRP, "mrp").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_COMMISSION_VALUE, "commision").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_COMMISION_QUANTITY, "commision_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_RECEIVER_CREDIT_QUANTITY, "receiver_credit_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_SENDER_DEBIT_QUANTITY, "sender_debit_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX3_VALUE, "tax3_value").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX1_VALUE, "tax1_value").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX2_VALUE, "tax2_value").append(" , ");
		  
		  selectQueryBuff.append(" SUM(to_number(CTI.otf_amount)) otf_amount,");
		selectQueryBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code, ");
		 selectQueryBuff.append(" SEND_CAT.category_name sender_category_name, REC_CAT.category_name receiver_category_name,CTRF.SOURCE, ");
		 selectQueryBuff.append(" TUGD.grph_domain_name to_user_geo,TWGD.grph_domain_name to_owner_geo, ");
		 selectQueryBuff.append(" FUGD.grph_domain_name from_user_geo ,FWGD.grph_domain_name from_owner_geo,FU.external_code from_ext_code,TU.external_code to_ext_code ");
		 selectQueryBuff.append(" FROM CHANNEL_TRANSFERS CTRF, CHANNEL_TRANSFERS_ITEMS CTI,USERS FU,USERS TU,PRODUCTS P,LOOKUPS L, LOOKUPS L1, ");
		 selectQueryBuff.append(" CATEGORIES SEND_CAT, CATEGORIES REC_CAT,USER_GEOGRAPHIES UG,USERS OU,USER_GEOGRAPHIES UGW, ");
		 selectQueryBuff.append(" GEOGRAPHICAL_DOMAINS TUGD, GEOGRAPHICAL_DOMAINS TWGD,  GEOGRAPHICAL_DOMAINS FUGD,GEOGRAPHICAL_DOMAINS FWGD, ");
		 selectQueryBuff.append(" USER_GEOGRAPHIES FUG,USER_GEOGRAPHIES FUGW,USERS PU  WHERE CTRF.TYPE = 'C2C' ");
		 
		 selectQueryBuff.append(" AND NVL(CTRF.close_date,CTRF.created_on) >=  TO_DATE(?,?) ");
		  
		 selectQueryBuff.append(" AND NVL(CTRF.close_date,CTRF.created_on) <=  TO_DATE(?,?)  ");
		 selectQueryBuff.append(" AND CTRF.network_code = ?   AND SEND_CAT.domain_code  = ?   AND CTRF.control_transfer<>'A' ");
		 selectQueryBuff.append(" AND CTRF.sender_category_code = SEND_CAT.category_code   AND CTRF.receiver_category_code = REC_CAT.category_code "); 
		 
		 selectQueryBuff.append(" AND CTRF.sender_category_code=CASE ?  WHEN 'ALL' THEN CTRF.sender_category_code ELSE ? END ");
		 
		 selectQueryBuff.append(" AND CTRF.receiver_category_code=CASE  ? WHEN 'ALL' THEN CTRF.receiver_category_code ELSE ? END ");
		 
		 selectQueryBuff.append(" AND CTRF.from_user_id=CASE ? WHEN 'ALL' THEN from_user_id ELSE ? END ");
		 
		 selectQueryBuff.append(" AND CTRF.to_user_id=CASE  ? WHEN 'ALL' THEN to_user_id ELSE  ? END ");
		 
		 selectQueryBuff.append(" AND CTRF.transfer_sub_type = CASE ? WHEN 'ALL' THEN CTRF.transfer_sub_type ELSE ? END ");
		 selectQueryBuff.append(" AND FU.user_id = CTRF.from_user_id   AND TU.user_id =CTRF.to_user_id   AND FU.owner_id=FUGW.user_id ");
		 selectQueryBuff.append(" AND FUGW.grph_domain_code=FWGD.grph_domain_code   AND TU.user_id=FUG.user_id   AND UG.grph_domain_code=FUGD.grph_domain_code ");
		 selectQueryBuff.append(" AND CTRF.transfer_id = CTI.transfer_id   AND CTI.product_code = P.product_code   AND FU.parent_id=PU.user_id(+) ");
		 selectQueryBuff.append(" AND OU.user_id=FU.owner_id   AND UGW.user_id=OU.user_id   AND UGW.grph_domain_code=TWGD.grph_domain_code ");
		 selectQueryBuff.append(" AND L.lookup_type ='TRFT'   AND L.lookup_code = CTRF.transfer_sub_type   AND CTRF.status = 'CLOSE' ");
		 selectQueryBuff.append(" AND L1.lookup_code = CTRF.status   AND L1.lookup_type = 'CTSTA'   AND CTRF.from_user_id = UG.user_id ");
		 selectQueryBuff.append(" AND FUG.grph_domain_code=TUGD.grph_domain_code   AND UG.grph_domain_code IN (SELECT grph_domain_code "); 
		 selectQueryBuff.append(" FROM   GEOGRAPHICAL_DOMAINS GD1 WHERE status IN('Y','S')   CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
		 selectQueryBuff.append(" START WITH grph_domain_code IN   (SELECT grph_domain_code FROM USER_GEOGRAPHIES UG1 ");
		 
		 selectQueryBuff.append(" WHERE UG1.grph_domain_code = (CASE ?  WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END)   AND UG1.user_id= ? )) ");
		 selectQueryBuff.append(" GROUP BY OU.user_name,OU.msisdn,PU.user_name,PU.msisdn,CTRF.from_user_id, CTRF.to_user_id, FU.user_name, CTRF.msisdn,TU.user_name, CTRF.to_msisdn ,CTRF.transfer_id, ");
		 selectQueryBuff.append(" TWGD.grph_domain_name, L.lookup_name , CTRF.TYPE, CTRF.transfer_date,CTRF.modified_ON,P.product_name, L1.lookup_name, ");
		 selectQueryBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code,SEND_CAT.category_name ,REC_CAT.category_name,CTRF.SOURCE,TUGD.grph_domain_name,FUGD.grph_domain_name,FWGD.grph_domain_name,TU.external_code,FU.external_code ");
		 selectQueryBuff.append(" ORDER BY 12 desc ,13 Desc ");
		String sqlSelect = selectQueryBuff.toString();
		if(log.isDebugEnabled()){ 
			loggerValue.setLength(0);
        	loggerValue.append("QUERY SELECT = ");
        	loggerValue.append(sqlSelect);
			  log.debug(methodName,loggerValue); 
	     }
		PreparedStatement pstmt = conn.prepareStatement(sqlSelect);
		int i = 1;
		 pstmt.setString(i++,Constants.getProperty("report.onlydateformat"));
			pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
					
			pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));
			pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
			pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));
			pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
			
			pstmt.setString(i++, usersReportModel.getNetworkCode());
			pstmt.setString(i++,usersReportModel.getDomainCode());
			
			pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
			pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
					
			pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());
			pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());
			
			pstmt.setString(i++,usersReportModel.getUserID());
			pstmt.setString(i++,usersReportModel.getUserID());
			
			pstmt.setString(i++,usersReportModel.getTouserID());
			pstmt.setString(i++,usersReportModel.getTouserID());
			
			pstmt.setString(i++,usersReportModel.getTxnSubType());
			pstmt.setString(i++,usersReportModel.getTxnSubType());
			
			pstmt.setString(i++,usersReportModel.getZoneCode());
			pstmt.setString(i++,usersReportModel.getZoneCode());
			pstmt.setString(i++,usersReportModel.getLoginUserID());
			
			pstmt.setString(i++,Constants.getProperty("report.onlydateformat"));
			pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
			
			pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));
			pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
			pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));
			pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
			
			pstmt.setString(i++, usersReportModel.getNetworkCode());
			pstmt.setString(i++,usersReportModel.getDomainCode());
			
			pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());
			pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());
			
			pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
			pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
					
			
			
			pstmt.setString(i++,usersReportModel.getTouserID());
			pstmt.setString(i++,usersReportModel.getTouserID());
			
			
			pstmt.setString(i++,usersReportModel.getUserID());
			pstmt.setString(i++,usersReportModel.getUserID());
			
			
			pstmt.setString(i++,usersReportModel.getTxnSubType());
			pstmt.setString(i++,usersReportModel.getTxnSubType());
			
			pstmt.setString(i++,usersReportModel.getZoneCode());
			pstmt.setString(i++,usersReportModel.getZoneCode());
			pstmt.setString(i,usersReportModel.getLoginUserID());
		
			return pstmt;
	}
	/**
	 * 
	 * @param conn
	 * @param usersReportModel
	 * @return PreparedStatement
	 * @throws SQLException
	 * @throws ParseException
	 */
	@Override
	public PreparedStatement loadC2cRetWidTransferUnionStaffListQry(Connection conn, UsersReportModel usersReportModel)
			throws SQLException, ParseException {
		 final String methodName = "loadC2cRetWidTransferUnionStaffListQry";
		 StringBuilder loggerValue= new StringBuilder();
		 if(log.isDebugEnabled()){ 
				loggerValue.setLength(0);
            	loggerValue.append("Entered loginUserID ");
            	loggerValue.append(usersReportModel.getLoginUserID());
				loggerValue.append(" NetworkCode ");
            	loggerValue.append(usersReportModel.getNetworkCode());
            	loggerValue.append(" DomainCode ");
            	loggerValue.append(usersReportModel.getDomainCode());
				loggerValue.append(" FromTrfCatCode ");
            	loggerValue.append(usersReportModel.getFromtransferCategoryCode());
            	loggerValue.append(" ToTrfCatCode ");
				loggerValue.append(usersReportModel.getTotransferCategoryCode());
            	loggerValue.append(" UserID ");
            	loggerValue.append(usersReportModel.getUserID());
            	loggerValue.append(" ToUserID ");
				loggerValue.append(usersReportModel.getTouserID());
            	loggerValue.append(" TxnSubType ");
				loggerValue.append(usersReportModel.getTxnSubType());
            	loggerValue.append(" ZoneCode ");
				loggerValue.append(usersReportModel.getZoneCode());
				log.debug(methodName,loggerValue);
		     } 
		StringBuilder selectQueryBuff = new StringBuilder();
		
		selectQueryBuff.append(" SELECT (OU.user_name || ' (' || OU.msisdn||')')owner_profile,(PU.user_name ||' (' ||NVL(PU.msisdn,'ROOT')||')')parent_profile,CTRF.from_user_id, ");
		  selectQueryBuff.append(" CTRF.to_user_id, FU.user_name from_user,CTRF.msisdn from_msisdn, TU.user_name to_user, CTRF.to_msisdn to_msisdn,CTRF.transfer_id,L.lookup_name transfer_sub_type, CTRF.TYPE, ");
		  selectQueryBuff.append(" UC.user_name initiator_user,TO_CHAR(CTRF.transfer_date, ? ) transfer_date, TO_CHAR(CTRF.modified_ON, ? ) modified_ON, P.product_name,  ");
		  
			  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_REQUIRED_QUANTITY, "transfer_mrp").append(" , ");
			  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_PAYABLE_AMOUNT, "payable_amount").append(" , ");
			  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_NET_PAYABLE_AMOUNT, "net_payable_amount").append(" , ");
			  selectQueryBuff.append(" L1.lookup_name status, ");
			  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_MRP, "mrp").append(" , ");
			  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_COMMISSION_VALUE, "commision").append(" , ");
			  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_COMMISION_QUANTITY, "commision_quantity").append(" , ");
			  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_RECEIVER_CREDIT_QUANTITY, "receiver_credit_quantity").append(" , ");
			  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_SENDER_DEBIT_QUANTITY, "sender_debit_quantity").append(" , ");
			  selectQueryBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code, ");
			  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX3_VALUE, "tax3_value").append(" , ");
			  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX1_VALUE, "tax1_value").append(" , ");
			  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX2_VALUE, "tax2_value").append(" , ");
		  
		  selectQueryBuff.append(" SEND_CAT.category_name sender_category_name, REC_CAT.category_name receiver_category_name,CTRF.SOURCE,UC.user_name intiator_user, ");
		  selectQueryBuff.append(" TUGD.grph_domain_name to_user_geo,TWGD.grph_domain_name to_owner_geo, FUGD.grph_domain_name from_user_geo ,FWGD.grph_domain_name from_owner_geo, ");
		  selectQueryBuff.append(" FU.external_code from_ext_code, TU.external_code to_ext_code  FROM CHANNEL_TRANSFERS CTRF, CHANNEL_TRANSFERS_ITEMS CTI, ");
		  selectQueryBuff.append(" USERS FU,USERS TU,USERS UC,PRODUCTS P,LOOKUPS L, LOOKUPS L1,CATEGORIES SEND_CAT, CATEGORIES REC_CAT,USER_GEOGRAPHIES UG,USERS OU,USER_GEOGRAPHIES UGW, ");
		  selectQueryBuff.append(" GEOGRAPHICAL_DOMAINS TUGD, GEOGRAPHICAL_DOMAINS TWGD,   GEOGRAPHICAL_DOMAINS FUGD,GEOGRAPHICAL_DOMAINS FWGD,   USER_GEOGRAPHIES FUG,USER_GEOGRAPHIES FUGW,USERS PU ");
		  selectQueryBuff.append(" WHERE CTRF.TYPE = 'C2C' ");
		  selectQueryBuff.append(" AND NVL(CTRF.close_date,CTRF.created_on) >=  TO_DATE(?,?) ");
		  selectQueryBuff.append(" AND NVL(CTRF.close_date,CTRF.created_on) <=  TO_DATE(?,?)  "); 
		  selectQueryBuff.append(" AND CTRF.network_code = ?   AND SEND_CAT.domain_code  = ?   AND CTRF.control_transfer<>'A' ");
		  selectQueryBuff.append(" AND CTRF.sender_category_code = SEND_CAT.category_code   AND CTRF.receiver_category_code = REC_CAT.category_code ");
		  selectQueryBuff.append(" AND CTRF.sender_category_code=CASE ? WHEN 'ALL' THEN CTRF.sender_category_code ELSE   ?  END ");
		  selectQueryBuff.append(" AND CTRF.receiver_category_code=CASE  ? WHEN 'ALL' THEN CTRF.receiver_category_code ELSE ? END ");
		  selectQueryBuff.append(" AND CTRF.from_user_id=CASE  ? WHEN 'ALL' THEN from_user_id ELSE ? END ");
		  
		  selectQueryBuff.append(" AND CTRF.to_user_id=CASE ? WHEN 'ALL' THEN to_user_id ELSE ? END ");
		  
		  selectQueryBuff.append(" AND CTRF.transfer_sub_type = CASE ? WHEN 'ALL' THEN CTRF.transfer_sub_type ELSE ? END ");
		  selectQueryBuff.append(" AND FU.user_id = CTRF.from_user_id   AND TU.user_id =CTRF.to_user_id   AND UC.user_id=CTRF.active_user_id ");
		  selectQueryBuff.append(" AND FU.owner_id=FUGW.user_id   AND FUGW.grph_domain_code=FWGD.grph_domain_code   AND FU.user_id=FUG.user_id ");
		  selectQueryBuff.append(" AND FUG.grph_domain_code=FUGD.grph_domain_code   AND CTRF.transfer_id = CTI.transfer_id   AND CTI.product_code = P.product_code ");
		  selectQueryBuff.append(" AND FU.parent_id=PU.user_id(+)   AND OU.USER_ID=FU.OWNER_ID   AND UGW.user_id=OU.user_id ");
		  selectQueryBuff.append(" AND UGW.grph_domain_code=TWGD.grph_domain_code   AND L.lookup_type ='TRFT'   AND L.lookup_code = CTRF.transfer_sub_type ");
		  selectQueryBuff.append(" AND CTRF.status = 'CLOSE'   AND L1.lookup_code = CTRF.status   AND L1.lookup_type = 'CTSTA' ");
		  selectQueryBuff.append(" AND CTRF.to_user_id = UG.user_id   AND UG.grph_domain_code=TUGD.grph_domain_code   AND UG.grph_domain_code IN ( ");
		  selectQueryBuff.append(" SELECT grph_domain_code FROM   GEOGRAPHICAL_DOMAINS GD1 WHERE status IN('Y','S') ");
		  selectQueryBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code   START WITH grph_domain_code IN ");
		  selectQueryBuff.append(" (SELECT grph_domain_code FROM USER_GEOGRAPHIES UG1 ");
		  
		  selectQueryBuff.append(" WHERE UG1.grph_domain_code = (CASE ?  WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END)   AND UG1.user_id= ? )) ");
		  selectQueryBuff.append(" GROUP BY OU.user_name,OU.msisdn,PU.user_name,PU.msisdn, CTRF.from_user_id, CTRF.to_user_id, FU.user_name, CTRF.msisdn,TU.user_name, CTRF.to_msisdn ,CTRF.transfer_id, ");
		  selectQueryBuff.append(" TWGD.grph_domain_name, L.lookup_name , CTRF.TYPE, CTRF.transfer_date,CTRF.modified_ON,P.product_name, L1.lookup_name, ");
		  selectQueryBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code,SEND_CAT.category_name ,  ");
		  selectQueryBuff.append(" REC_CAT.category_name,CTRF.SOURCE,TUGD.grph_domain_name,FUGD.grph_domain_name,FWGD.grph_domain_name,UC.user_name,FU.external_code, TU.external_code ");
		  selectQueryBuff.append(" UNION ");
		  selectQueryBuff.append(" SELECT  (OU.user_name || ' (' || OU.msisdn||')')owner_profile, (PU.user_name ||' (' ||NVL(PU.msisdn,'ROOT')||')')parent_profile,CTRF.from_user_id, ");
		  selectQueryBuff.append(" CTRF.to_user_id, FU.user_name from_user, CTRF.msisdn from_msisdn, TU.user_name to_user, CTRF.to_msisdn to_msisdn,CTRF.transfer_id,L.lookup_name transfer_sub_type, CTRF.TYPE,  ");
		  
		  selectQueryBuff.append(" UC.user_name initiator_user,TO_CHAR(CTRF.transfer_date, ? ) transfer_date,   TO_CHAR(CTRF.modified_ON, ? ) modified_ON,P.product_name, ");
		  
			  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_REQUIRED_QUANTITY, "transfer_mrp").append(" , ");
			  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_PAYABLE_AMOUNT, "payable_amount").append(" , ");
			  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_NET_PAYABLE_AMOUNT, "net_payable_amount").append(" , ");
			  selectQueryBuff.append(" L1.lookup_name status, ");
			  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_MRP, "mrp").append(" , ");
			  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_COMMISSION_VALUE, "commision").append(" , ");
			  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_COMMISION_QUANTITY, "commision_quantity").append(" , ");
			  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_RECEIVER_CREDIT_QUANTITY, "receiver_credit_quantity").append(" , ");
			  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_SENDER_DEBIT_QUANTITY, "sender_debit_quantity").append(" , ");
			  selectQueryBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code, ");
			  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX3_VALUE, "tax3_value").append(" , ");
			  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX1_VALUE, "tax1_value").append(" , ");
			  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX2_VALUE, "tax2_value").append(" , ");
			  
		  selectQueryBuff.append(" SEND_CAT.category_name sender_category_name, REC_CAT.category_name receiver_category_name,CTRF.SOURCE,UC.user_name intiator_user, ");
		  selectQueryBuff.append(" TUGD.grph_domain_name to_user_geo,TWGD.grph_domain_name to_owner_geo, FUGD.grph_domain_name from_user_geo ,FWGD.grph_domain_name from_owner_geo,FU.external_code from_ext_code,  ");
		  selectQueryBuff.append(" TU.external_code to_ext_code  FROM CHANNEL_TRANSFERS CTRF, CHANNEL_TRANSFERS_ITEMS CTI,USERS FU,USERS TU,USERS UC,PRODUCTS P,LOOKUPS L, LOOKUPS L1,  ");
		  selectQueryBuff.append(" CATEGORIES SEND_CAT, CATEGORIES REC_CAT,USER_GEOGRAPHIES UG,USERS OU,USER_GEOGRAPHIES UGW,GEOGRAPHICAL_DOMAINS TUGD, GEOGRAPHICAL_DOMAINS TWGD, ");
		  selectQueryBuff.append(" GEOGRAPHICAL_DOMAINS FUGD,GEOGRAPHICAL_DOMAINS FWGD, USER_GEOGRAPHIES FUG,USER_GEOGRAPHIES FUGW,USERS PU WHERE CTRF.TYPE = 'C2C' ");
		  
		  selectQueryBuff.append(" AND NVL(CTRF.close_date,CTRF.created_on) >=  TO_DATE(?,?) ");
		  
		  selectQueryBuff.append(" AND NVL(CTRF.close_date,CTRF.created_on) <=  TO_DATE(?,?)  "); 
		  
		  selectQueryBuff.append(" AND CTRF.network_code = ?   AND SEND_CAT.domain_code  = ?   AND CTRF.control_transfer<>'A' ");
		  selectQueryBuff.append(" AND CTRF.sender_category_code = SEND_CAT.category_code   AND CTRF.receiver_category_code = REC_CAT.category_code ");
		 
		  selectQueryBuff.append(" AND CTRF.sender_category_code=CASE  ?  WHEN 'ALL' THEN CTRF.sender_category_code ELSE  ? END ");
		 
		  selectQueryBuff.append(" AND CTRF.receiver_category_code=CASE  ?  WHEN 'ALL' THEN CTRF.receiver_category_code ELSE  ? END ");
		 
		  selectQueryBuff.append(" AND CTRF.from_user_id=CASE ? WHEN 'ALL' THEN from_user_id ELSE ? END ");
		 
		  selectQueryBuff.append(" AND CTRF.to_user_id=CASE  ? WHEN 'ALL' THEN to_user_id ELSE  ? END ");
		 
		  selectQueryBuff.append(" AND CTRF.transfer_sub_type = CASE ? WHEN 'ALL' THEN CTRF.transfer_sub_type ELSE ? END ");
		  selectQueryBuff.append(" AND FU.user_id = CTRF.from_user_id   AND TU.user_id =CTRF.to_user_id   AND UC.user_id=CTRF.active_user_id "); 
		  selectQueryBuff.append(" AND FU.owner_id=FUGW.user_id   AND FUGW.grph_domain_code=FWGD.grph_domain_code   AND TU.user_id=FUG.user_id ");
		  selectQueryBuff.append(" AND UG.grph_domain_code=FUGD.grph_domain_code   AND CTRF.transfer_id = CTI.transfer_id   AND CTI.product_code = P.product_code ");
		  selectQueryBuff.append(" AND FU.parent_id=PU.user_id(+)   AND OU.USER_ID=FU.OWNER_ID   AND UGW.user_id=OU.user_id ");
		  selectQueryBuff.append(" AND UGW.grph_domain_code=TWGD.grph_domain_code   AND L.lookup_type ='TRFT'   AND L.lookup_code = CTRF.transfer_sub_type ");
		  selectQueryBuff.append(" AND CTRF.status = 'CLOSE'   AND L1.lookup_code = CTRF.status   AND L1.lookup_type = 'CTSTA' ");
		  selectQueryBuff.append(" AND CTRF.from_user_id = UG.user_id   AND FUG.grph_domain_code=TUGD.grph_domain_code   AND UG.grph_domain_code IN ( ");
		  selectQueryBuff.append(" SELECT grph_domain_code FROM   GEOGRAPHICAL_DOMAINS GD1 WHERE status IN('Y','S') ");
		  selectQueryBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code   START WITH grph_domain_code IN (SELECT grph_domain_code FROM USER_GEOGRAPHIES UG1 ");
		  
		  selectQueryBuff.append(" WHERE UG1.grph_domain_code = (CASE ?  WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END)   AND UG1.user_id= ? )) ");
		  selectQueryBuff.append(" GROUP BY OU.user_name,OU.msisdn,PU.user_name,PU.msisdn,CTRF.from_user_id, CTRF.to_user_id, FU.user_name, CTRF.msisdn,TU.user_name, CTRF.to_msisdn ,CTRF.transfer_id, ");
		  selectQueryBuff.append(" TWGD.grph_domain_name, L.lookup_name , CTRF.TYPE, CTRF.transfer_date,CTRF.modified_ON,P.product_name, L1.lookup_name, ");
		  selectQueryBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code,SEND_CAT.category_name , "); 
		  selectQueryBuff.append(" REC_CAT.category_name,CTRF.SOURCE,TUGD.grph_domain_name,FUGD.grph_domain_name,FWGD.grph_domain_name,UC.user_name,FU.external_code, TU.external_code ");
		  selectQueryBuff.append(" ORDER BY 12 desc , 13 Desc ");
		String sqlSelect = selectQueryBuff.toString();
		if(log.isDebugEnabled()){ 
			loggerValue.setLength(0);
        	loggerValue.append("QUERY SELECT = ");
        	loggerValue.append(sqlSelect);
			  log.debug(methodName,loggerValue); 
	     }
		PreparedStatement pstmt = conn.prepareStatement(sqlSelect);
		int i = 1;
		pstmt.setString(i++,Constants.getProperty("report.onlydateformat"));
		pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
				
		pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));
		pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
		pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));
		pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
		
		pstmt.setString(i++, usersReportModel.getNetworkCode());
		pstmt.setString(i++,usersReportModel.getDomainCode());
		
		pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
		pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
				
		pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());
		pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());
		
		pstmt.setString(i++,usersReportModel.getUserID());
		pstmt.setString(i++,usersReportModel.getUserID());
		
		pstmt.setString(i++,usersReportModel.getTouserID());
		pstmt.setString(i++,usersReportModel.getTouserID());
		
		pstmt.setString(i++,usersReportModel.getTxnSubType());
		pstmt.setString(i++,usersReportModel.getTxnSubType());
		
		pstmt.setString(i++,usersReportModel.getZoneCode());
		pstmt.setString(i++,usersReportModel.getZoneCode());
		pstmt.setString(i++,usersReportModel.getLoginUserID());
		
		pstmt.setString(i++,Constants.getProperty("report.onlydateformat"));
		pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
		
		pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));
		pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
		pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));
		pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
		
		pstmt.setString(i++, usersReportModel.getNetworkCode());
		pstmt.setString(i++,usersReportModel.getDomainCode());
		
		pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());
		pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());

		pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
		pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
				
	
		
		
		pstmt.setString(i++,usersReportModel.getTouserID());
		pstmt.setString(i++,usersReportModel.getTouserID());
		
		pstmt.setString(i++,usersReportModel.getUserID());
		pstmt.setString(i++,usersReportModel.getUserID());
				
		
		pstmt.setString(i++,usersReportModel.getTxnSubType());
		pstmt.setString(i++,usersReportModel.getTxnSubType());
		
		pstmt.setString(i++,usersReportModel.getZoneCode());
		pstmt.setString(i++,usersReportModel.getZoneCode());
		pstmt.setString(i,usersReportModel.getLoginUserID());
		return pstmt;
	}
	/**
	 * 
	 * @param conn
	 * @param usersReportModel
	 * @return PreparedStatement
	 * @throws SQLException
	 * @throws ParseException
	 */
	@Override
	public PreparedStatement loadC2cRetWidTransferListQry(Connection conn, UsersReportModel usersReportModel)
			throws SQLException, ParseException {
		 final String methodName = "loadC2cRetWidTransferListQry";
		 StringBuilder loggerValue= new StringBuilder(); 
		 if(log.isDebugEnabled()){ 
         	loggerValue.append("Entered loginUserID ");
         	loggerValue.append(usersReportModel.getLoginUserID());
			loggerValue.append(" NetworkCode ");
         	loggerValue.append(usersReportModel.getNetworkCode());
        	loggerValue.append(" DomainCode ");
         	loggerValue.append(usersReportModel.getDomainCode());
			loggerValue.append(" FromTrfCatCode ");
         	loggerValue.append(usersReportModel.getFromtransferCategoryCode());
        	loggerValue.append(" ToTrfCatCode ");
         	loggerValue.append(usersReportModel.getTotransferCategoryCode());
			loggerValue.append(" UserID ");
         	loggerValue.append(usersReportModel.getUserID());
        	loggerValue.append(" ToUserID ");
         	loggerValue.append(usersReportModel.getTouserID());
			loggerValue.append(" TxnSubType ");
         	loggerValue.append(usersReportModel.getTxnSubType());
         	loggerValue.append(" ZoneCode ");
         	loggerValue.append(usersReportModel.getZoneCode());
				log.debug(methodName,loggerValue);
		     } 
		StringBuilder selectQueryBuff = new StringBuilder();
		 selectQueryBuff.append(" SELECT (OU.user_name || ' (' || OU.msisdn||')')owner_profile,(PU.user_name ||' (' ||NVL(PU.msisdn,'ROOT')||')')parent_profile,CTRF.from_user_id, CTRF.to_user_id, FU.user_name from_user, ");
		 selectQueryBuff.append(" CTRF.msisdn from_msisdn, TU.user_name to_user, CTRF.to_msisdn to_msisdn,CTRF.transfer_id,L.lookup_name transfer_sub_type, CTRF.TYPE, ");
		 selectQueryBuff.append(" TO_CHAR(CTRF.transfer_date, ? ) transfer_date,TO_CHAR(CTRF.modified_ON, ? ) modified_ON,P.product_name, ");
		 
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_REQUIRED_QUANTITY, "transfer_mrp").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_PAYABLE_AMOUNT, "payable_amount").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_NET_PAYABLE_AMOUNT, "net_payable_amount").append(" , ");
		  selectQueryBuff.append(" L1.lookup_name status, ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_MRP, "mrp").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_COMMISSION_VALUE, "commision").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_COMMISION_QUANTITY, "commision_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_RECEIVER_CREDIT_QUANTITY, "receiver_credit_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_SENDER_DEBIT_QUANTITY, "sender_debit_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX3_VALUE, "tax3_value").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX1_VALUE, "tax1_value").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX2_VALUE, "tax2_value").append(" , ");
		  
		 selectQueryBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code, SEND_CAT.category_name sender_category_name, REC_CAT.category_name receiver_category_name,CTRF.SOURCE, ");
		 selectQueryBuff.append(" TUGD.grph_domain_name to_user_geo,TWGD.grph_domain_name to_owner_geo, FUGD.grph_domain_name from_user_geo ,FWGD.grph_domain_name from_owner_geo ");
		 selectQueryBuff.append(" FROM CHANNEL_TRANSFERS CTRF, CHANNEL_TRANSFERS_ITEMS CTI,USERS FU,USERS TU,PRODUCTS P,LOOKUPS L, LOOKUPS L1,  ");
		 selectQueryBuff.append(" CATEGORIES SEND_CAT, CATEGORIES REC_CAT,USER_GEOGRAPHIES UG,USERS OU,USER_GEOGRAPHIES UGW,GEOGRAPHICAL_DOMAINS TUGD, GEOGRAPHICAL_DOMAINS TWGD, ");
		 selectQueryBuff.append(" GEOGRAPHICAL_DOMAINS FUGD,GEOGRAPHICAL_DOMAINS FWGD,USER_GEOGRAPHIES FUG,USER_GEOGRAPHIES FUGW,USERS PU   WHERE CTRF.TYPE = 'C2C' ");
		 selectQueryBuff.append(" AND NVL(CTRF.close_date,CTRF.created_on) >=  TO_DATE(?,?) ");
		 selectQueryBuff.append(" AND NVL(CTRF.close_date,CTRF.created_on) >=  TO_DATE(?,?)  "); 
		 selectQueryBuff.append(" AND CTRF.network_code = ?   AND CTRF.control_transfer<>'A' ");
		 selectQueryBuff.append(" AND CTRF.sender_category_code = SEND_CAT.category_code   AND CTRF.receiver_category_code = REC_CAT.category_code ");
		 selectQueryBuff.append(" AND CASE ? WHEN 'OUT' THEN SEND_CAT.domain_code ELSE REC_CAT.domain_code END = ? ");
		 selectQueryBuff.append(" AND CASE ? WHEN 'OUT' THEN CTRF.sender_category_code ELSE CTRF.receiver_category_code END = CASE ? WHEN 'OUT' THEN (CASE ? WHEN 'ALL' THEN CTRF.sender_category_code ELSE ? END) ELSE (CASE ?  WHEN  'ALL' THEN CTRF.receiver_category_code ELSE ? END) END ");
		 selectQueryBuff.append(" AND CASE ? WHEN 'OUT' THEN CTRF.receiver_category_code ELSE CTRF.sender_category_code END = CASE ? WHEN 'OUT' THEN (CASE ? WHEN 'ALL' THEN CTRF.receiver_category_code ELSE ?  END) ELSE (CASE ?  WHEN  'ALL' THEN CTRF.sender_category_code ELSE ? END) END ");
		 selectQueryBuff.append(" AND CASE ? WHEN 'OUT' THEN CTRF.from_user_id ELSE CTRF.to_user_id END = CASE ? WHEN 'OUT' THEN (CASE ? WHEN 'ALL' THEN from_user_id ELSE ? END) ELSE (CASE ? WHEN 'ALL' THEN CTRF.to_user_id ELSE ? END) END ");
		 selectQueryBuff.append(" AND CASE ? WHEN 'OUT' THEN CTRF.to_user_id ELSE CTRF.from_user_id END = CASE ? WHEN 'OUT' THEN (CASE ? WHEN 'ALL' THEN to_user_id ELSE ? END) ELSE (CASE ? WHEN 'ALL' THEN CTRF.from_user_id ELSE ? END) END ");
	     selectQueryBuff.append(" AND CTRF.transfer_sub_type = CASE ? WHEN 'ALL' THEN CTRF.transfer_sub_type ELSE ? END ");
		 selectQueryBuff.append(" AND FU.user_id = CTRF.from_user_id   AND TU.user_id =CTRF.to_user_id   AND FU.owner_id=FUGW.user_id ");
		 selectQueryBuff.append(" AND FUGW.grph_domain_code=FWGD.grph_domain_code   AND FU.user_id=FUG.user_id   AND FUG.grph_domain_code=FUGD.grph_domain_code ");
		 selectQueryBuff.append(" AND CTRF.transfer_id = CTI.transfer_id   AND CTI.product_code = P.product_code   AND FU.parent_id=PU.user_id(+) ");
		 selectQueryBuff.append(" AND OU.user_id=FU.owner_id   AND UGW.user_id=OU.user_id   AND UGW.grph_domain_code=TWGD.grph_domain_code ");
		 selectQueryBuff.append(" AND L.lookup_type ='TRFT'   AND L.lookup_code = CTRF.transfer_sub_type   AND CTRF.status = 'CLOSE' ");
		 selectQueryBuff.append(" AND L1.lookup_code = CTRF.status   AND L1.lookup_type = 'CTSTA' ");
		 selectQueryBuff.append(" AND CASE ? WHEN 'OUT' THEN CTRF.to_user_id ELSE CTRF.from_user_id END= UG.user_id ");
		 selectQueryBuff.append(" AND UG.grph_domain_code=TUGD.grph_domain_code   AND UG.grph_domain_code IN (SELECT grph_domain_code FROM ");
		 selectQueryBuff.append(" GEOGRAPHICAL_DOMAINS GD1 WHERE status IN('Y','S')   CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
		 selectQueryBuff.append(" START WITH grph_domain_code IN   (SELECT grph_domain_code FROM USER_GEOGRAPHIES UG1 ");
		 selectQueryBuff.append(" WHERE UG1.grph_domain_code = (CASE ?  WHEN 'ALL' THEN UG1.grph_domain_code ELSE  ? END)   AND UG1.user_id= ? )) ");
		 selectQueryBuff.append(" GROUP BY OU.user_name,OU.msisdn,PU.user_name,PU.msisdn, CTRF.from_user_id, CTRF.to_user_id, FU.user_name, CTRF.msisdn,TU.user_name, CTRF.to_msisdn ,CTRF.transfer_id, ");
		 selectQueryBuff.append(" TWGD.grph_domain_name, L.lookup_name , CTRF.TYPE, CTRF.transfer_date,CTRF.modified_ON,P.product_name, L1.lookup_name, ");
		 selectQueryBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code,SEND_CAT.category_name ,  ");
		 selectQueryBuff.append(" REC_CAT.category_name,CTRF.SOURCE,TUGD.grph_domain_name,FUGD.grph_domain_name,FWGD.grph_domain_name ");
		 selectQueryBuff.append(" ORDER BY 12 desc ,13 Desc  ");
		String sqlSelect = selectQueryBuff.toString();
		if(log.isDebugEnabled()){
			loggerValue.setLength(0);
         	loggerValue.append("QUERY SELECT = ");
         	loggerValue.append(sqlSelect);
			  log.debug(methodName,loggerValue); 
	     }
		PreparedStatement pstmt = conn.prepareStatement(sqlSelect);
		int i = 1;
		pstmt.setString(i++,Constants.getProperty("report.onlydateformat"));
		pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
				
		pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));
		pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
		pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));
		pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
		
		pstmt.setString(i++, usersReportModel.getNetworkCode());
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getDomainCode());
		
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
		pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
		pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
		pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
		
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());
		pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());
		pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());
		pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());
		
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getUserID());
		pstmt.setString(i++,usersReportModel.getUserID());
		pstmt.setString(i++,usersReportModel.getUserID());
		pstmt.setString(i++,usersReportModel.getUserID());
		
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getTouserID());
		pstmt.setString(i++,usersReportModel.getTouserID());
		pstmt.setString(i++,usersReportModel.getTouserID());
		pstmt.setString(i++,usersReportModel.getTouserID());
		
		pstmt.setString(i++,usersReportModel.getTxnSubType());
		pstmt.setString(i++,usersReportModel.getTxnSubType());
		
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		
		pstmt.setString(i++,usersReportModel.getZoneCode());
		pstmt.setString(i++,usersReportModel.getZoneCode());
		pstmt.setString(i,usersReportModel.getLoginUserID());
		
		return pstmt;
	}

	@Override
	public PreparedStatement loadC2cRetWidTransferStaffListQry(Connection conn, UsersReportModel usersReportModel)
			throws SQLException, ParseException {
		 final String methodName = "loadC2cRetWidTransferStaffListQry";
		 StringBuilder loggerValue= new StringBuilder(); 
		 if(log.isDebugEnabled()){ 
				loggerValue.setLength(0);
            	loggerValue.append("Entered loginUserID ");
            	loggerValue.append(usersReportModel.getLoginUserID());
				loggerValue.append(" NetworkCode ");
            	loggerValue.append(usersReportModel.getNetworkCode());
            	loggerValue.append(" DomainCode ");
            	loggerValue.append(usersReportModel.getDomainCode());
				loggerValue.append(" FromTrfCatCode ");
            	loggerValue.append(usersReportModel.getFromtransferCategoryCode());
            	loggerValue.append(" ToTrfCatCode ");
				loggerValue.append(usersReportModel.getTotransferCategoryCode());
            	loggerValue.append(" UserID ");
            	loggerValue.append(usersReportModel.getUserID());
            	loggerValue.append(" ToUserID ");
				loggerValue.append(usersReportModel.getTouserID());
            	loggerValue.append(" TxnSubType ");
            	loggerValue.append(usersReportModel.getTxnSubType());
				loggerValue.append(" ZoneCode ");
            	loggerValue.append(usersReportModel.getZoneCode());
				loggerValue.append(" TrfIN/OUT ");
            	loggerValue.append(usersReportModel.getTransferInOrOut());
				log.debug(methodName,loggerValue);
		     } 
		StringBuilder selectQueryBuff = new StringBuilder();
		 selectQueryBuff.append(" SELECT (OU.user_name || ' (' || OU.msisdn||')')owner_profile,(PU.user_name ||' (' ||NVL(PU.msisdn,'ROOT')||')')parent_profile, ");
		 selectQueryBuff.append(" CTRF.from_user_id, CTRF.to_user_id, U.user_name from_user, CTRF.msisdn from_msisdn,U2.user_name to_user, CTRF.to_msisdn to_msisdn, ");
		 selectQueryBuff.append(" UC.user_name initiator_user,CTRF.transfer_id,L.lookup_name transfer_sub_type, CTRF.TYPE, ");
		 selectQueryBuff.append(" TO_CHAR(CTRF.transfer_date,? ) transfer_date,TO_CHAR(CTRF.modified_ON, ? ) modified_ON,P.product_name, ");
		 
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_REQUIRED_QUANTITY, "transfer_mrp").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_PAYABLE_AMOUNT, "payable_amount").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_NET_PAYABLE_AMOUNT, "net_payable_amount").append(" , ");
		  selectQueryBuff.append(" L1.lookup_name status, ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_MRP, "mrp").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_COMMISSION_VALUE, "commision").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_COMMISION_QUANTITY, "commision_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_RECEIVER_CREDIT_QUANTITY, "receiver_credit_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_SENDER_DEBIT_QUANTITY, "sender_debit_quantity").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX3_VALUE, "tax3_value").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX1_VALUE, "tax1_value").append(" , ");
		  selectQueryBuff = BTSLUtil.appendZero(selectQueryBuff, CTI_TAX2_VALUE, "tax2_value").append(" , ");
		  
		  selectQueryBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code,SEND_CAT.category_name sender_category_name, REC_CAT.category_name receiver_category_name,CTRF.SOURCE ");
		 selectQueryBuff.append(" FROM CHANNEL_TRANSFERS CTRF, CHANNEL_TRANSFERS_ITEMS CTI,USERS U,USERS U2, USERS UC,PRODUCTS P,LOOKUPS L, LOOKUPS L1, CATEGORIES SEND_CAT,  ");
		 selectQueryBuff.append(" CATEGORIES REC_CAT,USER_GEOGRAPHIES UG,USERS PU,USERS OU ");
		 selectQueryBuff.append(" WHERE CTRF.TYPE = 'C2C' ");
		 selectQueryBuff.append(" AND NVL(CTRF.close_date,CTRF.created_on) >=  TO_DATE(?,?) ");
		 selectQueryBuff.append(" AND NVL(CTRF.close_date,CTRF.created_on) <=  TO_DATE(?,?) ");
		 selectQueryBuff.append(" AND CTRF.network_code = ?  AND CTRF.control_transfer<>'A' ");
		 selectQueryBuff.append(" AND CASE ? WHEN 'OUT' THEN SEND_CAT.domain_code ELSE REC_CAT.domain_code END = ? ");
		 selectQueryBuff.append(" AND CTRF.sender_category_code = SEND_CAT.category_code  AND CTRF.receiver_category_code = REC_CAT.category_code ");
		 selectQueryBuff.append(" AND CASE ? WHEN 'OUT' THEN CTRF.sender_category_code ELSE CTRF.receiver_category_code END = CASE ? WHEN 'OUT' THEN (CASE ? WHEN 'ALL' THEN CTRF.sender_category_code ELSE ? END) ELSE (CASE ?  WHEN  'ALL' THEN CTRF.receiver_category_code ELSE ? END) END ");
		 selectQueryBuff.append(" AND CASE ? WHEN 'OUT' THEN CTRF.receiver_category_code ELSE CTRF.sender_category_code END = CASE ? WHEN 'OUT' THEN (CASE ? WHEN 'ALL' THEN CTRF.receiver_category_code ELSE ?  END) ELSE (CASE ?  WHEN  'ALL' THEN CTRF.sender_category_code ELSE ? END) END ");
		 selectQueryBuff.append(" AND CASE ? WHEN 'OUT' THEN CTRF.from_user_id ELSE CTRF.to_user_id END = CASE ? WHEN 'OUT' THEN (CASE ? WHEN 'ALL' THEN from_user_id ELSE ? END) ELSE (CASE ? WHEN 'ALL' THEN CTRF.to_user_id ELSE ? END) END ");
		 selectQueryBuff.append(" AND CASE ? WHEN 'OUT' THEN CTRF.to_user_id ELSE CTRF.from_user_id END = CASE ? WHEN 'OUT' THEN (CASE ? WHEN 'ALL' THEN to_user_id ELSE ? END) ELSE (CASE ? WHEN 'ALL' THEN CTRF.from_user_id ELSE ? END) END ");
		 selectQueryBuff.append(" AND CTRF.transfer_sub_type = CASE ? WHEN 'ALL' THEN CTRF.transfer_sub_type ELSE ? END ");
		 selectQueryBuff.append(" AND U.user_id = CTRF.from_user_id   AND U2.user_id =CTRF.to_user_id   AND UC.user_id= CTRF.active_user_id ");
		 selectQueryBuff.append(" AND CTRF.transfer_id = CTI.transfer_id   AND CTI.product_code = P.product_code   AND U.parent_id=PU.user_id(+)   ");
		 selectQueryBuff.append(" AND OU.USER_ID=U.OWNER_ID   AND L.lookup_type ='TRFT'   AND L.lookup_code = CTRF.transfer_sub_type ");
		 selectQueryBuff.append(" AND CTRF.status = 'CLOSE'   AND L1.lookup_code = CTRF.status   AND L1.lookup_type = 'CTSTA' ");
		 selectQueryBuff.append(" AND CASE ? WHEN 'OUT' THEN CTRF.to_user_id ELSE CTRF.from_user_id END= UG.user_id ");
		 selectQueryBuff.append(" AND UG.grph_domain_code IN (SELECT grph_domain_code FROM   GEOGRAPHICAL_DOMAINS GD1 WHERE status IN('Y','S') ");
		 selectQueryBuff.append(" CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code   START WITH grph_domain_code IN ");
		 selectQueryBuff.append(" (SELECT grph_domain_code FROM USER_GEOGRAPHIES UG1 ");
		 selectQueryBuff.append("  WHERE UG1.grph_domain_code = (CASE ? WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END)   AND UG1.user_id= ?)) ");
		 selectQueryBuff.append("  GROUP BY OU.user_name,OU.msisdn,PU.user_name,PU.msisdn,CTRF.from_user_id, CTRF.to_user_id, U.user_name, CTRF.msisdn,U2.user_name, CTRF.to_msisdn ,UC.user_name, CTRF.transfer_id, ");
		 selectQueryBuff.append(" L.lookup_name , CTRF.TYPE, CTRF.transfer_date,CTRF.modified_ON,P.product_name, L1.lookup_name, ");
		 selectQueryBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code,SEND_CAT.category_name , REC_CAT.category_name,CTRF.source ");
		 selectQueryBuff.append(" ORDER BY 12 desc , 13 desc ");
		String sqlSelect = selectQueryBuff.toString();
		if(log.isDebugEnabled()){ 
			loggerValue.setLength(0);
        	loggerValue.append("QUERY SELECT = ");
        	loggerValue.append(sqlSelect);
			log.debug(methodName,loggerValue); 
	     }

		PreparedStatement pstmt = conn.prepareStatement(sqlSelect);
		int i = 1;
		pstmt.setString(i++,Constants.getProperty("report.onlydateformat"));
		pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
				
		pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));
		pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
		pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));
		pstmt.setString(i++,Constants.getProperty("report.datetimeformat"));
		
		pstmt.setString(i++, usersReportModel.getNetworkCode());
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getDomainCode());
		
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
		pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
		pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
		pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
		
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());
		pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());
		pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());
		pstmt.setString(i++,usersReportModel.getTotransferCategoryCode());
		
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getUserID());
		pstmt.setString(i++,usersReportModel.getUserID());
		pstmt.setString(i++,usersReportModel.getUserID());
		pstmt.setString(i++,usersReportModel.getUserID());
		
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		pstmt.setString(i++,usersReportModel.getTouserID());
		pstmt.setString(i++,usersReportModel.getTouserID());
		pstmt.setString(i++,usersReportModel.getTouserID());
		pstmt.setString(i++,usersReportModel.getTouserID());
		
		pstmt.setString(i++,usersReportModel.getTxnSubType());
		pstmt.setString(i++,usersReportModel.getTxnSubType());
		
		pstmt.setString(i++,usersReportModel.getTransferInOrOut());
		
		pstmt.setString(i++,usersReportModel.getZoneCode());
		pstmt.setString(i++,usersReportModel.getZoneCode());
		pstmt.setString(i,usersReportModel.getLoginUserID());
		return pstmt;
	}





	}

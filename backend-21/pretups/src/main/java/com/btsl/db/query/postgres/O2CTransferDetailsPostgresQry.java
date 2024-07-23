package com.btsl.db.query.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.reports.businesslogic.O2CTransferDetailsRptQry;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.channel.reports.web.UsersReportModel;



/**
 * @author rahul.arya
 *
 */
public class O2CTransferDetailsPostgresQry implements O2CTransferDetailsRptQry {

	public static final Log log = LogFactory.getLog(O2CTransferDetailsPostgresQry.class.getName());
	
	private static final String DATE_FORMAT ="report.onlydateformat";
	
	private static final String TIME_FORMAT = "report.datetimeformat";
	
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
	private static final String CTI_APPROVED_QUANTITY = "CTI.approved_quantity";
	
	@Override 
	public PreparedStatement loado2cTransferDetailsReportQry(UsersReportModel usersReportModel,Connection con) throws SQLException, ParseException
	{
		final String methodName = "loado2cTransferDailyReportQry";
		if(log.isDebugEnabled()){ 
			log.debug(methodName,"Entered loginUserID "+usersReportModel.getLoginUserID()+" NetworkCode "+usersReportModel.getNetworkCode()+" DomainCode "+usersReportModel.getDomainCode()+
					" FromTrfCatCode "+usersReportModel.getFromtransferCategoryCode()+" ToTrfCatCode "+usersReportModel.getTotransferCategoryCode()+
					" UserID "+usersReportModel.getUserID()+" ToUserID "+usersReportModel.getTouserID()+" TxnSubType "+usersReportModel.getTxnSubType()+" ZoneCode "+usersReportModel.getZoneCode());
	     } 
		StringBuilder strBuff = new StringBuilder();
		String mobopt = PretupsRestUtil.getMessageString("pretups.o2cDetails.opt.label.operator");
        strBuff.append("SELECT CASE WHEN CTRF.from_user_id = 'OPT'  THEN '").append(mobopt).append("' ELSE U.user_name end as from_user, ");
        strBuff.append(" CASE WHEN CTRF.to_user_id = 'OPT'  THEN '").append(mobopt).append("' ELSE U2.user_name  end as to_user, ");
		strBuff.append(" U.user_name from_user1,U.msisdn from_msisdn , U2.user_name to_user1, U2.msisdn to_msisdn,CTRF.transfer_id, ");
		   strBuff.append("L.lookup_name transfer_sub_type,  CTRF.transfer_sub_type trf_sub_type, CTRF.TYPE,CASE WHEN CTRF.transaction_mode = 'N' then 'NORMAL' else 'AUTO' END AS transaction_mode, "); 		   
		   strBuff.append("TO_CHAR(CTRF.transfer_date,?) transfer_date, ");		   
		   strBuff.append("TO_CHAR(CTRF.modified_on,?) modified_on, ");
		   strBuff.append("CTRF.transfer_date txn_date, ");
		   strBuff.append("P.product_name, ");
		   
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_COMMISSION_VALUE, "commission_value").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_PAYABLE_AMOUNT, "payable_amount").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_TAX1_VALUE, "tax1").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_TAX2_VALUE, "tax2").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_TAX3_VALUE, "tax3").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_NET_PAYABLE_AMOUNT, "net_payable_amount").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_REQUIRED_QUANTITY, "required_quantity").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_MRP, "mrp").append(" , ");
		   
		   
		   strBuff.append("L2.lookup_name trf_cat_name,(otf_amount::float)otf_amount, ");
		   
		   strBuff = BTSLUtil.appendZero(strBuff, "CTI.commision_quantity", "commission_quantity").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_RECEIVER_CREDIT_QUANTITY, "receiver_credit_quantity").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_SENDER_DEBIT_QUANTITY, "sender_debit_quantity").append(" , ");
		   
		   strBuff.append("L1.lookup_name status, CTRF.domain_code, D.domain_name, CTRF.transfer_category, ");		   
		   strBuff.append("TO_CHAR(CTRF.ext_txn_date,?) ext_txn_date, CTRF.ext_txn_no, ");
		   
		   strBuff = BTSLUtil.appendZero(strBuff, "CTRF.first_level_approved_quantity", "first_level_approved_quantity").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, "CTRF.second_level_approved_quantity", "second_level_approved_quantity").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, "CTRF.third_level_approved_quantity", "third_level_approved_quantity").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_APPROVED_QUANTITY, "approved_quantity").append(" , ");
		   
		   strBuff.append("CTRF.REQUEST_GATEWAY_TYPE,CTRF.pmt_inst_type, ");		   
		   strBuff.append("CTRF.pmt_inst_no,TO_CHAR(CTRF.pmt_inst_date, ?) pmt_inst_date ");
		strBuff.append("FROM  CHANNEL_TRANSFERS_ITEMS CTI,PRODUCTS P,CHANNEL_TRANSFERS CTRF "); 
		   strBuff.append("left join LOOKUPS L on L.lookup_code = CTRF.transfer_sub_type AND L.lookup_type ='TRFT' "); 
		   strBuff.append("left join LOOKUPS L1 on L1.lookup_code = CTRF.status     AND L1.lookup_type = 'CTSTA' ");  
		   strBuff.append("left join USERS U on U.user_id = CASE CTRF.from_user_id WHEN 'OPT' THEN '' ELSE CTRF.from_user_id END ");
		   strBuff.append("left join USERS U2 on U2.user_id = CASE CTRF.to_user_id WHEN 'OPT' THEN '' ELSE CTRF.to_user_id END , "); 
		   strBuff.append("DOMAINS D, CATEGORIES C, LOOKUPS L2 ");
		strBuff.append("WHERE CTRF.TYPE = 'O2C' "); 		  
		strBuff.append(" AND date_trunc ('minute',COALESCE(CTRF.close_date,CTRF.created_on) :: timestamp) >=  date_trunc ('minute',"); 
		strBuff.append(" TO_timestamp(?,?)) ");		   
		strBuff.append("AND date_trunc ('minute',COALESCE(CTRF.close_date,CTRF.created_on) :: timestamp)  <= date_trunc ('minute',"); 
		strBuff.append(" TO_timestamp(?,?)) ");   		
		strBuff.append("AND CTRF.network_code = ? ");		   
		strBuff.append("AND CTRF.domain_code IN ( ");
		String[] arr = usersReportModel.getDomainListString().split("\\,");
		for (int i = 0; i < arr.length; i++) {
			strBuff.append(" ?");
			if (i != arr.length - 1) {
				strBuff.append(",");
			}
		}
		 strBuff.append(") ");		
		   strBuff.append(" AND CTRF.domain_code = D.domain_code ");
		   strBuff.append(" AND CTRF.transfer_category=L2.lookup_code ");
		   strBuff.append(" AND L2.lookup_type='TRFTY' ");		   
		   strBuff.append("AND CTRF.transfer_category = CASE ? WHEN 'ALL' THEN CTRF.transfer_category ELSE ? END ");		   
		   strBuff.append("AND CTRF.sender_category_code = CASE ?  WHEN 'ALL' THEN CTRF.sender_category_code ELSE ? END ");		   		   
		   strBuff.append("AND CTRF.from_user_id = CASE ?  WHEN 'ALL' THEN CTRF.from_user_id ELSE ? END ");		  
		   strBuff.append("AND CTRF.transfer_sub_type = (CASE ? WHEN 'ALL' THEN CTRF.transfer_sub_type ELSE ? END) ");
		   strBuff.append("AND C.category_code = CTRF.sender_category_code "); 
		   strBuff.append(" AND CTRF.transfer_id = CTI.transfer_id ");
		   strBuff.append(" AND CTI.product_code = P.product_code ");
		   strBuff.append(" AND CTRF.status = 'CLOSE' ");
		   strBuff.append(" AND CTRF.grph_domain_code IN ( ");
		   strBuff.append("with recursive q as(");
		strBuff.append(" SELECT gd1.grph_domain_code, gd1.status "); 
		strBuff.append(" FROM geographical_domains GD1  ");
		strBuff.append(" where grph_domain_code IN  (SELECT grph_domain_code "); 
		strBuff.append(" FROM user_geographies UG1 ");		
		strBuff.append(" WHERE UG1.grph_domain_code = (case ? when 'ALL' then UG1.grph_domain_code else ? end) ");		  
		strBuff.append(" AND UG1.user_id=?) ");
		   strBuff.append(" union all ");
		strBuff.append(" SELECT gd1.grph_domain_code, gd1.status ");
		strBuff.append(" FROM geographical_domains GD1 join q on q.grph_domain_code = gd1.parent_grph_domain_code ");
		strBuff.append("   )");
		strBuff.append(" SELECT grph_domain_code "); 
		strBuff.append(" FROM q ");
		strBuff.append(" WHERE status IN('Y','S') ");
		   strBuff.append(") ");
		   strBuff.append("GROUP BY CTRF.from_user_id, CTRF.to_user_id, L2.lookup_name ,otf_amount, P.product_name, U.user_name, U2.user_name, U.msisdn, U2.msisdn, CTRF.transfer_id, L.lookup_name, CTRF.transfer_sub_type, CTRF.TYPE, CTRF.transaction_mode, CTRF.transfer_date, CTRF.modified_on, CTI.commission_value,CTI.payable_amount, CTI.tax1_value, CTI.tax2_value, CTI.tax3_value, CTI.net_payable_amount, CTI.required_quantity, CTI.mrp, CTI.commision_quantity,CTI.receiver_credit_quantity, CTI.sender_debit_quantity, L1.lookup_name, CTRF.domain_code, D.domain_name, CTRF.transfer_category, CTRF.ext_txn_date,CTRF.ext_txn_no, CTRF.first_level_approved_quantity, CTRF.second_level_approved_quantity, CTRF.third_level_approved_quantity, CTI.approved_quantity, CTRF.REQUEST_GATEWAY_TYPE, CTRF.pmt_inst_type,CTRF.pmt_inst_no, CTRF.pmt_inst_date ");
		   strBuff.append("UNION ");
		   strBuff.append("SELECT CASE WHEN CTRF.from_user_id = 'OPT'  THEN '").append(mobopt).append("' ELSE U.user_name end as from_user , ");
	        strBuff.append(" CASE WHEN CTRF.to_user_id = 'OPT'  THEN '").append(mobopt).append("' ELSE U2.user_name  end as to_user , ");
		strBuff.append(" U.user_name from_user1,U.msisdn from_msisdn , U2.user_name to_user1, U2.msisdn ");
		strBuff.append("to_msisdn,CTRF.transfer_id, ");
		   strBuff.append("L.lookup_name transfer_sub_type, CTRF.transfer_sub_type trf_sub_type, CTRF.TYPE, CASE WHEN CTRF.transaction_mode = 'N' then 'NORMAL' else 'AUTO' END AS transaction_mode, ");		    
		   strBuff.append("TO_CHAR(CTRF.transfer_date,?) transfer_date, ");		   
		   strBuff.append("TO_CHAR(CTRF.modified_on,?) modified_on, ");
		   strBuff.append("CTRF.transfer_date txn_date, ");
		   strBuff.append("P.product_name, ");
		   
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_COMMISSION_VALUE, "commission_value").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_PAYABLE_AMOUNT, "payable_amount").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_TAX1_VALUE, "tax1").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_TAX2_VALUE, "tax2").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_TAX3_VALUE, "tax3").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_NET_PAYABLE_AMOUNT, "net_payable_amount").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_REQUIRED_QUANTITY, "required_quantity").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_MRP, "mrp").append(" , ");
		   
		   strBuff.append("L2.lookup_name trf_cat_name,(otf_amount::float)otf_amount, ");
		   
		   strBuff = BTSLUtil.appendZero(strBuff, "CTI.commision_quantity", "commission_quantity").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_RECEIVER_CREDIT_QUANTITY, "receiver_credit_quantity").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_SENDER_DEBIT_QUANTITY, "sender_debit_quantity").append(" , ");
		   
		   strBuff.append("L1.lookup_name status,CTRF.domain_code, D.domain_name, CTRF.transfer_category,");		   
		   strBuff.append("TO_CHAR(CTRF.ext_txn_date,?) ext_txn_date, CTRF.ext_txn_no, ");
		   
		   strBuff = BTSLUtil.appendZero(strBuff, "CTRF.first_level_approved_quantity", "first_level_approved_quantity").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, "CTRF.second_level_approved_quantity", "second_level_approved_quantity").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, "CTRF.third_level_approved_quantity", "third_level_approved_quantity").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_APPROVED_QUANTITY, "approved_quantity").append(" , ");
		   
		   strBuff.append("CTRF.REQUEST_GATEWAY_TYPE, ");
		   strBuff.append("CTRF.pmt_inst_type,CTRF.pmt_inst_no,TO_CHAR(CTRF.pmt_inst_date, ?) pmt_inst_date ");
		strBuff.append("FROM CHANNEL_TRANSFERS CTRF ");
		   strBuff.append("left join LOOKUPS L on L.lookup_code = CTRF.transfer_sub_type and L.lookup_type ='TRFT' ");
		   strBuff.append("left join LOOKUPS L1 on  L1.lookup_code = CTRF.status and  L1.lookup_type = 'CTSTA' ");
		   strBuff.append("left join USERS U on U.user_id= CASE CTRF.from_user_id WHEN 'OPT' THEN '' ELSE CTRF.from_user_id END ");
		   strBuff.append("left join USERS U2 on U2.user_id = CASE CTRF.to_user_id WHEN 'OPT' THEN '' ELSE CTRF.to_user_id END ");
		   strBuff.append(", CHANNEL_TRANSFERS_ITEMS CTI,PRODUCTS P, ");
		   strBuff.append("DOMAINS D, CATEGORIES C,LOOKUPS L2 ");
		strBuff.append("WHERE CTRF.TYPE = 'O2C' ");		   
		strBuff.append("AND date_trunc ('minute',COALESCE(CTRF.close_date,CTRF.created_on) :: timestamp) >=  date_trunc ('minute',"); 
		strBuff.append(" TO_timestamp(?,?)) ");		   
		strBuff.append("AND date_trunc ('minute',COALESCE(CTRF.close_date,CTRF.created_on) :: timestamp)  <= date_trunc ('minute',"); 
		strBuff.append(" TO_timestamp(?,?)) ");		   
		strBuff.append("AND CTRF.network_code =  ? ");		   
		strBuff.append("AND CTRF.domain_code IN ( ");
		String[] arr1 = usersReportModel.getDomainListString().split("\\,");
		for (int i = 0; i < arr1.length; i++) {
			strBuff.append(" ?");
			if (i != arr1.length - 1) {
				strBuff.append(",");
			}
		}
		 strBuff.append(") ");
		   strBuff.append(" AND CTRF.domain_code = D.domain_code ");
		   strBuff.append(" AND CTRF.transfer_category=L2.lookup_code ");
		   strBuff.append("AND L2.lookup_type='TRFTY'");		   
		   strBuff.append("AND CTRF.transfer_category = CASE ? WHEN 'ALL' THEN CTRF.transfer_category ELSE ? END ");		  
		   strBuff.append("AND CTRF.receiver_category_code = CASE ?  WHEN 'ALL' THEN CTRF.receiver_category_code ELSE ?  END ");		   
		   strBuff.append("AND CTRF.to_user_id = CASE ?  WHEN 'ALL' THEN CTRF.to_user_id ELSE ? END ");		   
		   strBuff.append("AND CTRF.transfer_sub_type = CASE ? WHEN 'ALL' THEN CTRF.transfer_sub_type ELSE ? END ");
		   strBuff.append("AND C.category_code = CTRF.receiver_category_code "); 
		   strBuff.append(" AND CTRF.transfer_id = CTI.transfer_id ");
		   strBuff.append(" AND CTI.product_code = P.product_code "); 
		   strBuff.append(" AND CTRF.status = 'CLOSE' ");
		   strBuff.append(" AND CTRF.grph_domain_code IN ( ");
		   strBuff.append("  with recursive q as( ");
		strBuff.append(" SELECT gd1.grph_domain_code, gd1.status "); 
		strBuff.append(" FROM geographical_domains GD1  ");
		strBuff.append(" where grph_domain_code IN  (SELECT grph_domain_code "); 
		strBuff.append(" FROM user_geographies UG1 ");		
		strBuff.append(" WHERE UG1.grph_domain_code = (case ? when 'ALL' then UG1.grph_domain_code else ? end) ");		   
		strBuff.append(" AND UG1.user_id=?) ");
		strBuff.append(" union all ");
		strBuff.append(" SELECT gd1.grph_domain_code, gd1.status ");
		strBuff.append(" FROM geographical_domains GD1 join q on q.grph_domain_code = gd1.parent_grph_domain_code ");
		strBuff.append(") ");
		strBuff.append(" SELECT grph_domain_code "); 
		strBuff.append(" FROM q ");
		strBuff.append(" WHERE status IN('Y','S') ");
		strBuff.append(") ");
		strBuff.append("GROUP BY CTRF.from_user_id, P.product_name, L2.lookup_name ,otf_amount, CTRF.to_user_id, U.user_name, U2.user_name, U.msisdn, U2.msisdn, CTRF.transfer_id, L.lookup_name, CTRF.transfer_sub_type, CTRF.TYPE, CTRF.transaction_mode, CTRF.transfer_date, CTRF.modified_on, CTI.commission_value,CTI.payable_amount, CTI.tax1_value, CTI.tax2_value, CTI.tax3_value, CTI.net_payable_amount, CTI.required_quantity, CTI.mrp, CTI.commision_quantity,CTI.receiver_credit_quantity, CTI.sender_debit_quantity, L1.lookup_name, CTRF.domain_code, D.domain_name, CTRF.transfer_category, CTRF.ext_txn_date,CTRF.ext_txn_no, CTRF.first_level_approved_quantity, CTRF.second_level_approved_quantity, CTRF.third_level_approved_quantity, CTI.approved_quantity, CTRF.REQUEST_GATEWAY_TYPE, CTRF.pmt_inst_type,CTRF.pmt_inst_no, CTRF.pmt_inst_date ");
		strBuff.append("ORDER BY 12 desc ");
	      String selectQuery=strBuff.toString();
	      if(log.isDebugEnabled()){ 
			  log.debug(methodName," QUERY SELECT = "+ selectQuery); 
	     } 
	      PreparedStatement pstmt;
	       
	        
				pstmt = con.prepareStatement(selectQuery);
				int i=1;
				 pstmt.setString(i++,Constants.getProperty(DATE_FORMAT));
	             pstmt.setString(i++,Constants.getProperty(TIME_FORMAT));
	             pstmt.setString(i++,Constants.getProperty(DATE_FORMAT));
	             pstmt.setString(i++,Constants.getProperty(DATE_FORMAT));
	             pstmt.setString(i++,BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));
	             pstmt.setString(i++,Constants.getProperty(TIME_FORMAT) ); 
	             pstmt.setString(i++,BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));
	             pstmt.setString(i++, Constants.getProperty(TIME_FORMAT));
	             pstmt.setString(i++, usersReportModel.getNetworkCode());
	             
	             String[] arr3 = usersReportModel.getDomainListString().split("\\,");
					
					for (int x = 0; x < arr3.length; x++) {
						arr3[x] =arr3[x].replace("'", "");
						pstmt.setString(i++, arr3[x]);

		            }
	             pstmt.setString(i++, usersReportModel.getTransferCategory());
	             pstmt.setString(i++, usersReportModel.getTransferCategory());
	             pstmt.setString(i++, usersReportModel.getFromtransferCategoryCode());
	             pstmt.setString(i++, usersReportModel.getFromtransferCategoryCode());
	             pstmt.setString(i++, usersReportModel.getUserID());
	             pstmt.setString(i++, usersReportModel.getUserID());
	             pstmt.setString(i++, usersReportModel.getTxnSubType());
	             pstmt.setString(i++, usersReportModel.getTxnSubType());
	             pstmt.setString(i++, usersReportModel.getZoneCode());
	             pstmt.setString(i++, usersReportModel.getZoneCode());
	             pstmt.setString(i++, usersReportModel.getLoginUserID());
	             pstmt.setString(i++, Constants.getProperty(DATE_FORMAT));
	             pstmt.setString(i++, Constants.getProperty(TIME_FORMAT));
	             pstmt.setString(i++, Constants.getProperty(DATE_FORMAT));
	             pstmt.setString(i++, Constants.getProperty(DATE_FORMAT));
	             pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));
	             pstmt.setString(i++,Constants.getProperty(TIME_FORMAT) );
	             pstmt.setString(i++, BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()));
	             pstmt.setString(i++, Constants.getProperty(TIME_FORMAT));
	             pstmt.setString(i++, usersReportModel.getNetworkCode());
	             
	             String[] arr4 = usersReportModel.getDomainListString().split("\\,");
					
					for (int x = 0; x < arr4.length; x++) {
						arr4[x] =arr4[x].replace("'", "");
						pstmt.setString(i++, arr4[x]);

		            }
	             pstmt.setString(i++, usersReportModel.getTransferCategory());
	             pstmt.setString(i++, usersReportModel.getTransferCategory());
	             pstmt.setString(i++, usersReportModel.getFromtransferCategoryCode());
	             pstmt.setString(i++, usersReportModel.getFromtransferCategoryCode());
	             pstmt.setString(i++, usersReportModel.getUserID());
	             pstmt.setString(i++, usersReportModel.getUserID());
	             pstmt.setString(i++, usersReportModel.getTxnSubType());
	             pstmt.setString(i++, usersReportModel.getTxnSubType());
	             pstmt.setString(i++, usersReportModel.getZoneCode());
	             pstmt.setString(i++, usersReportModel.getZoneCode());
	             pstmt.setString(i, usersReportModel.getLoginUserID());
		         
	             return pstmt;
	
	}
	
	@Override
	public PreparedStatement loado2cTransferDetailsChannelUserReportQry(UsersReportModel usersReportModel,Connection con) throws SQLException, ParseException
	{
		final String methodName = "loado2cTransferDetailsChannelUserReportQry";
		if(log.isDebugEnabled()){ 
			log.debug(methodName,"Entered loginUserID "+usersReportModel.getLoginUserID()+" NetworkCode "+usersReportModel.getNetworkCode()+" DomainCode "+usersReportModel.getDomainCode()+
					" FromTrfCatCode "+usersReportModel.getFromtransferCategoryCode()+" ToTrfCatCode "+usersReportModel.getTotransferCategoryCode()+
					" UserID "+usersReportModel.getUserID()+" ToUserID "+usersReportModel.getTouserID()+" TxnSubType "+usersReportModel.getTxnSubType()+" ZoneCode "+usersReportModel.getZoneCode());
	     } 
		StringBuilder strBuff = new StringBuilder();
		String mobopt = PretupsRestUtil.getMessageString("pretups.o2cDetails.opt.label.operator");
		 strBuff.append("SELECT CASE WHEN CTRF.from_user_id = 'OPT'  THEN '").append(mobopt).append("' ELSE U.user_name||'('||U.msisdn||')' end as from_user, ");
	        strBuff.append(" CASE WHEN CTRF.to_user_id = 'OPT'  THEN '").append(mobopt).append("' ELSE U2.user_name||'('||U2.msisdn||')'  end as to_user, ");
	   	strBuff.append( " U.user_name||'('||U.msisdn||')' from_user1,U2.user_name||'('||U2.msisdn||')' ");   
		strBuff.append("to_user1,CTRF.transfer_id, ");  
		   strBuff.append("L.lookup_name transfer_sub_type,  CTRF.transfer_sub_type trf_sub_type, CTRF.type, CASE WHEN CTRF.transaction_mode = 'N' then 'NORMAL' else 'AUTO' END AS transaction_mode, ");    
		   strBuff.append("TO_CHAR(CTRF.transfer_date,?) transfer_date, TO_CHAR(CTRF.modified_on,?) modified_on, TO_CHAR(CTRF.transfer_date,?) txn_date, ");  
		   strBuff.append("P.product_name, ");
		   
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_COMMISSION_VALUE, "commission_value").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_RECEIVER_CREDIT_QUANTITY, "receiver_credit_quantity").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_SENDER_DEBIT_QUANTITY, "sender_debit_quantity").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_PAYABLE_AMOUNT, "payable_amount").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_TAX1_VALUE, "tax1_value").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_TAX2_VALUE, "tax2_value").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_TAX3_VALUE, "tax3_value").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_NET_PAYABLE_AMOUNT, "net_payable_amount").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_REQUIRED_QUANTITY, "required_quantity").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_MRP, "mrp").append(" , ");
		   
		   strBuff.append("L2.lookup_name trf_cat_name, ");  
		   strBuff.append("L1.lookup_name status, CTRF.domain_code, D.domain_name, CTRF.transfer_category, ");    
		   strBuff.append("TO_CHAR(CTRF.ext_txn_date,?) ext_txn_date, ");   
		   strBuff.append("CTRF.ext_txn_no,CTRF.request_gateway_type, ");
		   
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_APPROVED_QUANTITY, "approved_quantity").append(" , ");
		   
		   strBuff.append("(otf_amount::float)otf_amount ");  
		strBuff.append("FROM channel_transfers CTRF ");  
		   strBuff.append("left join users U on U.user_id = case CTRF.from_user_id when 'OPT' then '' else CTRF.from_user_id end ");  
		   strBuff.append("left join users U2 on U2.user_id = case CTRF.to_user_id when 'OPT' then '' else CTRF.to_user_id end ");  
		   strBuff.append("left join lookups L on L.lookup_code = CTRF.transfer_sub_type and L.lookup_type='TRFT' ");  
		   strBuff.append("left join lookups L1 on L1.lookup_code = CTRF.status    AND L1.lookup_type = 'CTSTA' ");  
		   strBuff.append(", channel_transfers_items CTI,products P, ");  
		   strBuff.append("domains D, categories C,lookups L2 ");  
		strBuff.append("WHERE CTRF.type = 'O2C' ");      
		strBuff.append("AND date_trunc ('minute',COALESCE(CTRF.close_date,CTRF.created_on) :: timestamp) >=  date_trunc ('minute',");   
		strBuff.append(" TO_timestamp(?,?)) ");      		  		
		strBuff.append("AND date_trunc ('minute',COALESCE(CTRF.close_date,CTRF.created_on) :: timestamp) <= date_trunc ('minute',");   
		strBuff.append(" TO_timestamp(?,?)) ");   		  		    
		strBuff.append("AND CTRF.network_code = ? ");   
		strBuff.append("AND CTRF.domain_code IN ( ?) ");  
		   strBuff.append("AND CTRF.domain_code = D.domain_code ");  
		   strBuff.append("AND CTRF.transfer_category=L2.lookup_code ");  
		   strBuff.append("AND L2.lookup_type='TRFTY' ");  
		   strBuff.append("AND CTRF.transfer_category = case ? when 'ALL' then CTRF.transfer_category else ? end "); 
		   strBuff.append("AND CTRF.sender_category_code = case ?  when 'ALL' then CTRF.sender_category_code else ? end "); 
		   strBuff.append("AND CTRF.from_user_id IN( ");  
		   strBuff.append(" with recursive q as( ");  
		strBuff.append("SELECT U11.user_id  ");  
		strBuff.append("FROM users U11 ");  
		strBuff.append("where U11.user_id=? ");  
		   strBuff.append(" union all ");  
		strBuff.append("SELECT U11.user_id ");   
		strBuff.append("FROM users U11 join q on q.user_id = U11.parent_id ");  
		   strBuff.append(") ");  
		strBuff.append("SELECT user_id ");  
		strBuff.append(" FROM q ");    
		strBuff.append("where   user_id=CASE ? WHEN 'ALL' THEN user_id ELSE  ? END ");  
		   strBuff.append(") 		 ");   
		   strBuff.append("AND CTRF.transfer_sub_type = (case ? when 'ALL' then CTRF.transfer_sub_type else ? end) ");  
		   strBuff.append("AND C.category_code = CTRF.sender_category_code ");  
		   strBuff.append("AND C.sequence_no >= ? ");  
		   strBuff.append("AND CTRF.transfer_id = CTI.transfer_id ");  
		   strBuff.append("AND CTI.product_code = P.product_code ");  
		   strBuff.append("AND CTRF.status = 'CLOSE' ");  
		   strBuff.append("AND CTRF.grph_domain_code IN ( ");  
		   strBuff.append(" with recursive q as( ");  
		strBuff.append(" SELECT gd1.grph_domain_code, gd1.status ");   
		strBuff.append("FROM geographical_domains GD1  ");  
		strBuff.append("where grph_domain_code IN  (SELECT grph_domain_code ");   
		strBuff.append(" FROM user_geographies UG1 ");  
		strBuff.append(" WHERE UG1.grph_domain_code = (case ? when 'ALL' then UG1.grph_domain_code else ? end) ");  
		strBuff.append(" AND UG1.user_id=?) ");  
		   strBuff.append(" union all ");  
		strBuff.append(" SELECT gd1.grph_domain_code, gd1.status ");   
		strBuff.append("FROM geographical_domains GD1 join q on q.grph_domain_code = gd1.parent_grph_domain_code ");  
		   strBuff.append(") ");  
		strBuff.append("SELECT grph_domain_code ");   
		strBuff.append(" FROM q ");  
		strBuff.append("WHERE status IN('Y','S') ");  
		   strBuff.append(") ");
		   strBuff.append("GROUP BY CTRF.from_user_id, CTRF.to_user_id, L2.lookup_name ,otf_amount, P.product_name, U.user_name, U2.user_name, U.msisdn, U2.msisdn, CTRF.transfer_id, L.lookup_name, CTRF.transfer_sub_type, CTRF.TYPE, CTRF.transaction_mode, CTRF.transfer_date, CTRF.modified_on, CTI.commission_value,CTI.payable_amount, CTI.tax1_value, CTI.tax2_value, CTI.tax3_value, CTI.net_payable_amount, CTI.required_quantity, CTI.mrp, CTI.commision_quantity,CTI.receiver_credit_quantity, CTI.sender_debit_quantity, L1.lookup_name, CTRF.domain_code, D.domain_name, CTRF.transfer_category, CTRF.ext_txn_date,CTRF.ext_txn_no, CTRF.first_level_approved_quantity, CTRF.second_level_approved_quantity, CTRF.third_level_approved_quantity, CTI.approved_quantity, CTRF.REQUEST_GATEWAY_TYPE, CTRF.pmt_inst_type,CTRF.pmt_inst_no, CTRF.pmt_inst_date ");
		   strBuff.append("UNION ");  
		   strBuff.append("SELECT CASE WHEN CTRF.from_user_id = 'OPT'  THEN '").append(mobopt).append("' ELSE U.user_name||'('||U.msisdn||')' end as from_user, ");
	        strBuff.append(" CASE WHEN CTRF.to_user_id = 'OPT'  THEN '").append(mobopt).append("' ELSE U2.user_name||'('||U2.msisdn||')'  end as to_user, ");
	        strBuff.append(" U.user_name||'('||U.msisdn||')' from_user1,U2.user_name||'('||U2.msisdn||')' ");    
			strBuff.append("to_user1,CTRF.transfer_id,");  
			   strBuff.append("L.lookup_name transfer_sub_type, CTRF.transfer_sub_type trf_sub_type, CTRF.type, CASE WHEN CTRF.transaction_mode = 'N' then 'NORMAL' else 'AUTO' END AS transaction_mode,");   
			   strBuff.append("TO_CHAR(CTRF.transfer_date,?) transfer_date,TO_CHAR(CTRF.modified_on,?) modified_on, TO_CHAR(CTRF.transfer_date,?) txn_date,");  
		   strBuff.append("P.product_name, ");
		   
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_COMMISSION_VALUE, "commission_value").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_RECEIVER_CREDIT_QUANTITY, "receiver_credit_quantity").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_SENDER_DEBIT_QUANTITY, "sender_debit_quantity").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_PAYABLE_AMOUNT, "payable_amount").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_TAX1_VALUE, "tax1_value").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_TAX2_VALUE, "tax2_value").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_TAX3_VALUE, "tax3_value").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_NET_PAYABLE_AMOUNT, "net_payable_amount").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_REQUIRED_QUANTITY, "required_quantity").append(" , ");
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_MRP, "mrp").append(" , ");
		   
		   strBuff.append("L2.lookup_name trf_cat_name, ");  
		   strBuff.append("L1.lookup_name status,CTRF.domain_code, D.domain_name, CTRF.transfer_category, ");  
		   strBuff.append("TO_CHAR(CTRF.ext_txn_date,?) ext_txn_date, ");  
		   strBuff.append("CTRF.ext_txn_no,CTRF.request_gateway_type, ");
		   
		   strBuff = BTSLUtil.appendZero(strBuff, CTI_APPROVED_QUANTITY, "approved_quantity").append(" , ");
		   
		   strBuff.append("(otf_amount::float)otf_amount ");  
		strBuff.append("FROM channel_transfers CTRF ");  
		   strBuff.append("left join users U on U.user_id = case CTRF.from_user_id when 'OPT' then '' else CTRF.from_user_id end ");  
		   strBuff.append("left join users U2 on U2.user_id= case CTRF.to_user_id when 'OPT' then '' else CTRF.to_user_id end ");  
		   strBuff.append("left join lookups L on L.lookup_code= CTRF.transfer_sub_type and L.lookup_type='TRFT' ");  
		   strBuff.append("left join lookups L1 on L1.lookup_code = CTRF.status and L1.lookup_type = 'CTSTA' ");  
		   strBuff.append(", channel_transfers_items CTI,products P, ");  
		   strBuff.append("domains D, categories C,lookups L2 ");  
		strBuff.append("WHERE CTRF.type = 'O2C' ");      
		strBuff.append("AND date_trunc ('minute',COALESCE(CTRF.close_date,CTRF.created_on) :: timestamp) >=  date_trunc ('minute', "); 
		strBuff.append("TO_timestamp(?,?)) ");      		  		
		strBuff.append("AND date_trunc ('minute',COALESCE(CTRF.close_date,CTRF.created_on) :: timestamp)  <= date_trunc ('minute', ");  
		strBuff.append("TO_timestamp(?,?)) ");   
		strBuff.append("AND CTRF.network_code =  ? ");  
		strBuff.append("AND CTRF.domain_code IN ( ? ) ");  
		   strBuff.append("AND CTRF.domain_code = D.domain_code ");  
		   strBuff.append("AND CTRF.transfer_category=L2.lookup_code ");  
		   strBuff.append("AND L2.lookup_type='TRFTY' ");  
		   strBuff.append("AND CTRF.transfer_category = case ? when 'ALL' then CTRF.transfer_category else ? end ");  		 
		   strBuff.append("AND CTRF.receiver_category_code = case ?  when 'ALL' then CTRF.receiver_category_code else ?  end ");  
		   strBuff.append("AND CTRF.to_user_id IN( ");  
		   strBuff.append("with recursive q as( ");  
		strBuff.append("SELECT U11.user_id  ");  
		strBuff.append("FROM users U11 ");  		
		strBuff.append("where U11.user_id=? ");  
		   strBuff.append("union all ");  
		strBuff.append("SELECT U11.user_id ");  
		strBuff.append("FROM users U11 join q on q.user_id = U11.parent_id ");   
		   strBuff.append(") ");  
		strBuff.append("SELECT user_id ");   
		strBuff.append("FROM q ");  		  
		strBuff.append("where   user_id=CASE ? WHEN 'ALL' THEN user_id ELSE  ? END ");  
		   strBuff.append(") 		   ");  		    
		   strBuff.append("AND CTRF.transfer_sub_type = case ? when 'ALL' then CTRF.transfer_sub_type else ? end ");  
		   strBuff.append("AND C.category_code = CTRF.receiver_category_code ");  		    
		   strBuff.append("AND C.sequence_no >= ? ");  
		   strBuff.append("AND CTRF.transfer_id = CTI.transfer_id ");  
		   strBuff.append("AND CTI.product_code = P.product_code ");  
		   strBuff.append("AND CTRF.status = 'CLOSE' ");  
		   strBuff.append("AND CTRF.grph_domain_code IN ( ");  
		   strBuff.append("with recursive q as( ");  
		strBuff.append("SELECT gd1.grph_domain_code, gd1.status ");  
		strBuff.append("FROM geographical_domains GD1  ");  
		strBuff.append("where grph_domain_code IN  (SELECT grph_domain_code ");  
		strBuff.append("FROM user_geographies UG1 ");  		  
		strBuff.append("WHERE UG1.grph_domain_code = (case ? when 'ALL' then UG1.grph_domain_code else ? end) ");  		    
		strBuff.append("AND UG1.user_id=?) ");  
		   strBuff.append("union all ");  
		strBuff.append("SELECT gd1.grph_domain_code, gd1.status ");  
		strBuff.append("FROM geographical_domains GD1 join q on q.grph_domain_code = gd1.parent_grph_domain_code ");  
		   strBuff.append(") ");  
		strBuff.append("SELECT grph_domain_code ");   
		strBuff.append("FROM q ");  
		strBuff.append("WHERE status IN('Y','S') ");  
		   strBuff.append(") ");
		strBuff.append("GROUP BY CTRF.from_user_id, CTRF.to_user_id, L2.lookup_name ,otf_amount, P.product_name, U.user_name, U2.user_name, U.msisdn, U2.msisdn, CTRF.transfer_id, L.lookup_name, CTRF.transfer_sub_type, CTRF.TYPE, CTRF.transaction_mode, CTRF.transfer_date, CTRF.modified_on, CTI.commission_value,CTI.payable_amount, CTI.tax1_value, CTI.tax2_value, CTI.tax3_value, CTI.net_payable_amount, CTI.required_quantity, CTI.mrp, CTI.commision_quantity,CTI.receiver_credit_quantity, CTI.sender_debit_quantity, L1.lookup_name, CTRF.domain_code, D.domain_name, CTRF.transfer_category, CTRF.ext_txn_date,CTRF.ext_txn_no, CTRF.first_level_approved_quantity, CTRF.second_level_approved_quantity, CTRF.third_level_approved_quantity, CTI.approved_quantity, CTRF.REQUEST_GATEWAY_TYPE, CTRF.pmt_inst_type,CTRF.pmt_inst_no, CTRF.pmt_inst_date ");
		strBuff.append("ORDER BY 10 desc ");  
		String selectQuery=strBuff.toString();
      		if(log.isDebugEnabled()){ 
			  log.debug(methodName,"QUERY SELECT = "+ selectQuery); 
	     } 
        PreparedStatement pstmt;
        	pstmt = con.prepareStatement(selectQuery);
			int i=1;
			 pstmt.setString(i++,Constants.getProperty(DATE_FORMAT));
             pstmt.setString(i++,Constants.getProperty(TIME_FORMAT));
             pstmt.setString(i++,Constants.getProperty(TIME_FORMAT));
             pstmt.setString(i++,Constants.getProperty(DATE_FORMAT));
             pstmt.setString(i++,BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));
             pstmt.setString(i++,Constants.getProperty(TIME_FORMAT));
             pstmt.setString(i++,BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()) ); 
             pstmt.setString(i++,Constants.getProperty(TIME_FORMAT));
             pstmt.setString(i++, usersReportModel.getNetworkCode());
             pstmt.setString(i++, usersReportModel.getDomainListString());
             pstmt.setString(i++, usersReportModel.getTransferCategory());
             pstmt.setString(i++, usersReportModel.getTransferCategory());
             pstmt.setString(i++, usersReportModel.getFromtransferCategoryCode());
             pstmt.setString(i++, usersReportModel.getFromtransferCategoryCode());
             pstmt.setString(i++, usersReportModel.getLoginUserID());
             pstmt.setString(i++, usersReportModel.getUserID());
             pstmt.setString(i++, usersReportModel.getUserID());
             pstmt.setString(i++, usersReportModel.getTxnSubType());
             pstmt.setString(i++, usersReportModel.getTxnSubType());
             pstmt.setInt(i++, Integer.parseInt(usersReportModel.getCategorySeqNo()));
             pstmt.setString(i++, usersReportModel.getZoneCode());
             pstmt.setString(i++, usersReportModel.getZoneCode());
             pstmt.setString(i++, usersReportModel.getLoginUserID());
             pstmt.setString(i++,Constants.getProperty(DATE_FORMAT));
             pstmt.setString(i++,Constants.getProperty(TIME_FORMAT));
             pstmt.setString(i++,Constants.getProperty(TIME_FORMAT));
             pstmt.setString(i++,Constants.getProperty(DATE_FORMAT));
             pstmt.setString(i++,BTSLUtil.getDateTimeStringFromDate(usersReportModel.getFromDateTime()));
             pstmt.setString(i++,Constants.getProperty(TIME_FORMAT));
             pstmt.setString(i++,BTSLUtil.getDateTimeStringFromDate(usersReportModel.getToDateTime()) ); 
             pstmt.setString(i++,Constants.getProperty(TIME_FORMAT));
             pstmt.setString(i++, usersReportModel.getNetworkCode());
             pstmt.setString(i++, usersReportModel.getDomainListString());
             pstmt.setString(i++, usersReportModel.getTransferCategory());
             pstmt.setString(i++, usersReportModel.getTransferCategory());
             pstmt.setString(i++, usersReportModel.getFromtransferCategoryCode());
             pstmt.setString(i++, usersReportModel.getFromtransferCategoryCode());
             pstmt.setString(i++, usersReportModel.getLoginUserID());
             pstmt.setString(i++, usersReportModel.getUserID());
             pstmt.setString(i++, usersReportModel.getUserID());
             pstmt.setString(i++, usersReportModel.getTxnSubType());
             pstmt.setString(i++, usersReportModel.getTxnSubType()); 
             pstmt.setInt(i++, Integer.parseInt(usersReportModel.getCategorySeqNo()));
             pstmt.setString(i++, usersReportModel.getZoneCode());
             pstmt.setString(i++, usersReportModel.getZoneCode());
             pstmt.setString(i, usersReportModel.getLoginUserID());
             
             return pstmt;
	
}
	@Override
	public PreparedStatement loado2cTransferDailyReportQry(UsersReportModel usersReportModel,Connection con) throws SQLException
	{
		
		final String methodName = "loado2cTransferDailyReportQry";
		if(log.isDebugEnabled()){ 
			log.debug(methodName,"Entered loginUserID "+usersReportModel.getLoginUserID()+" NetworkCode "+usersReportModel.getNetworkCode()+" DomainCode "+usersReportModel.getDomainCode()+
					" FromTrfCatCode "+usersReportModel.getFromtransferCategoryCode()+" ToTrfCatCode "+usersReportModel.getTotransferCategoryCode()+
					" UserID "+usersReportModel.getUserID()+" ToUserID "+usersReportModel.getTouserID()+" TxnSubType "+usersReportModel.getTxnSubType()+" ZoneCode "+usersReportModel.getZoneCode());
	     } 
		final StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT  U.user_name, U.msisdn, D.domain_name, C.category_name,(CASE U.parent_id WHEN 'ROOT' THEN N'' ELSE PU.user_name END) parent_name, (CASE  WHEN U.parent_id = 'ROOT' THEN '' ELSE PU.msisdn END) parent_msisdn, (CASE  WHEN U.parent_id = 'ROOT' THEN N'' WHEN U.user_id = OU.user_id THEN N'' ELSE OU.user_name END) owner_name, "); 
		   strBuff.append("(CASE  WHEN U.parent_id = 'ROOT' THEN ''  WHEN U.user_id = OU.user_id THEN '' ELSE OU.msisdn END) owner_msisdn, GD.grph_domain_name, "); 
		   strBuff.append("P.product_name,LK.lookup_name transfer_sub_type, LK1.lookup_name transfer_category,  ");
		   strBuff.append("SUM(DCTD.trans_in_amount) trans_in_amount, SUM(DCTD.trans_in_count) trans_in_count, SUM(DCTD.trans_out_amount) trans_out_amount, "); 		   
		   strBuff.append("SUM(DCTD.trans_out_count) trans_out_count,TO_CHAR(DCTD.trans_date,?) trans_date ");
		strBuff.append("FROM DAILY_CHNL_TRANS_DETAILS DCTD, USERS U, CATEGORIES C, PRODUCTS P, GEOGRAPHICAL_DOMAINS GD, "); 
		   strBuff.append("LOOKUPS LK, DOMAINS D, USER_GEOGRAPHIES UG, LOOKUPS LK1,USERS PU,USERS OU ");
		strBuff.append("WHERE DCTD.TYPE ='O2C' ");		   
		strBuff.append("AND DCTD.trans_date >=? ");		   
		strBuff.append("AND DCTD.trans_date <= ? ");		   
		strBuff.append("AND U.network_code =? ");		   
		strBuff.append("AND D.domain_code IN (?) ");		   
		strBuff.append("AND DCTD.user_id = CASE ? WHEN 'ALL' THEN DCTD.user_id ELSE ? END ");		   
		strBuff.append("AND LK.lookup_code =  CASE ? WHEN 'ALL' THEN LK.lookup_code ELSE ? END ");
		   strBuff.append("AND LK.lookup_type = 'TRFT' ");
		   strBuff.append("AND LK.lookup_code = DCTD.transfer_sub_type ");		  
		   strBuff.append("AND C.category_code = CASE '{?categoryCode}'  WHEN 'ALL' THEN C.category_code ELSE '{?categoryCode}'  END ");		   
		   strBuff.append("AND DCTD.transfer_category = CASE ? WHEN 'ALL' THEN DCTD.transfer_category ELSE ? END ");
		   strBuff.append("AND LK1.lookup_code =  DCTD.transfer_category ");
		   strBuff.append("AND LK1.lookup_type = 'TRFTY' ");
		   strBuff.append("AND DCTD.user_id = U.user_id ");
		   strBuff.append("AND PU.user_id = (CASE U.parent_id WHEN 'ROOT' THEN U.user_id ELSE U.parent_id END) ");
		   strBuff.append("AND OU.USER_ID=U.OWNER_ID ");
		   strBuff.append("AND U.category_code = C.category_code ");
		   strBuff.append("AND C.domain_code = D.domain_code ");
		   strBuff.append("AND DCTD.product_code = P.product_code ");
		   strBuff.append("AND DCTD.user_id = UG.user_id   ");
		   strBuff.append("AND UG.grph_domain_code = GD.grph_domain_code ");
		   strBuff.append("AND GD.grph_domain_code IN ( ");
		   strBuff.append("with recursive q as ( ");
		strBuff.append("SELECT grph_domain_code,status ");  
		strBuff.append("from geographical_domains  ");
		strBuff.append("WHERE grph_domain_code IN ");
		   strBuff.append("(SELECT grph_domain_code ");
		strBuff.append("FROM user_geographies UG1 ");		
		strBuff.append("WHERE UG1.grph_domain_code = (case ? when 'ALL' then UG1.grph_domain_code else ? end) ");		  
		strBuff.append("AND UG1.user_id=?) ");
		   strBuff.append("union all  ");
		strBuff.append("select m.grph_domain_code,m.status  ");
		strBuff.append("from geographical_domains m join q on q.grph_domain_code=m.parent_grph_domain_code ) ");
		strBuff.append("select q.grph_domain_code "); 
		strBuff.append("from q  ");
		strBuff.append("where status IN('Y', 'S')) ");
		strBuff.append("GROUP BY  U.user_name, U.msisdn, DCTD.trans_date , D.domain_name,C.category_name,PU.user_name,PU.msisdn,OU.user_name,OU.msisdn,  ");
		   strBuff.append("LK.lookup_name, LK1.lookup_name,GD.grph_domain_name, P.product_name, U.parent_id,OU.user_id,U.user_id ");
		    String selectQuery=strBuff.toString();
		    if(log.isDebugEnabled()){ 
				  log.debug(methodName,"QUERY SELECT = "+ selectQuery); 
		     } 
	           PreparedStatement pstmt;
		        	pstmt = con.prepareStatement(selectQuery);
					int i=1;
					pstmt.setString(i++, Constants.getProperty(DATE_FORMAT));
		            pstmt.setString(i++, usersReportModel.getRptfromDate());
		            pstmt.setString(i++,usersReportModel.getRpttoDate());
		            pstmt.setString(i++,usersReportModel.getNetworkCode());
		            pstmt.setString(i++,usersReportModel.getDomainListString());
		            pstmt.setString(i++,usersReportModel.getUserID()  ); 
		            pstmt.setString(i++,usersReportModel.getUserID() );
		            pstmt.setString(i++,usersReportModel.getTxnSubType());
		            pstmt.setString(i++,usersReportModel.getTxnSubType());
		            pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
		            pstmt.setString(i++,usersReportModel.getFromtransferCategoryCode());
		            pstmt.setString(i++,usersReportModel.getTransferCategory());
		            pstmt.setString(i++,usersReportModel.getTransferCategory());
		            pstmt.setString(i++,usersReportModel.getZoneCode());
		            pstmt.setString(i++,usersReportModel.getZoneCode());
		            pstmt.setString(i,usersReportModel.getLoginUserID());
		            return pstmt;
	}
}

package com.btsl.db.query.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.pretups.channel.reports.businesslogic.StaffSelfC2CQuery;
import com.btsl.util.Constants;
import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * @author mohit.miglani
 *
 */
public class StaffSelfC2CPostgresQry implements StaffSelfC2CQuery {

	
	
	
	
	
	@Override
	public PreparedStatement loadStaffSelfC2CChannelReportQry(UsersReportModel usersReportModel,Connection con) throws SQLException
	
	{
		
		StringBuilder strBuff = new StringBuilder();

		strBuff.append("SELECT CTRF.ACTIVE_USER_ID, CTRF.to_user_id, (U.user_name ) from_user,(U2.user_name || '(' ||CTRF.to_msisdn||')') to_user,CTRF.transfer_id,");
		strBuff.append("L.lookup_name transfer_sub_type, CTRF.TYPE, TO_CHAR(CTRF.close_date,?) close_date,");
		strBuff.append(" P.product_name, SUM(CTI.required_quantity) transfer_mrp, SUM(CTI.payable_amount) payable_amount, SUM(CTI.net_payable_amount) net_payable_amount, L1.lookup_name status,");
		
		strBuff.append(" SUM(CTI.mrp) mrp, SUM(CTI.commission_value) commision,SUM(CTI.commision_quantity) commision_quantity,SUM(CTI.receiver_credit_quantity) receiver_credit_quantity,SUM(CTI.sender_debit_quantity) sender_debit_quantity,");
		strBuff.append(" SUM(CTI.tax3_value)tax3_value,SUM(CTI.tax1_value)tax1_value,SUM(CTI.tax2_value)tax2_value,CTRF.sender_category_code, CTRF.receiver_category_code,");
		strBuff.append("   REC_CAT.category_name receiver_category_name,CTRF.SOURCE FROM USERS X, ");
		strBuff.append(" CHANNEL_TRANSFERS CTRF, CHANNEL_TRANSFERS_ITEMS CTI,USERS U,USERS U2,PRODUCTS P,LOOKUPS L, LOOKUPS L1, CATEGORIES REC_CAT WHERE CTRF.TYPE = 'C2C'");
		strBuff.append(" AND CTRF.close_date >=TO_DATE(?,?) AND CTRF.close_date <=TO_DATE(?,?) ");
		strBuff.append(" AND CTRF.network_code = ? AND CTRF.control_transfer<>'A' AND CTRF.receiver_category_code = REC_CAT.category_code");
		strBuff.append(" AND CTRF.ACTIVE_USER_ID=? AND CTRF.ACTIVE_USER_ID = X.user_id AND CTRF.transfer_sub_type = CASE ? WHEN 'ALL' THEN CTRF.transfer_sub_type ELSE ? END");
		strBuff.append(" AND U.user_id = CTRF.ACTIVE_USER_ID AND U2.user_id =CTRF.to_user_id AND CTRF.transfer_id = CTI.transfer_id");
		strBuff.append(" AND CTI.product_code = P.product_code AND L.lookup_type ='TRFT' AND L.lookup_code = CTRF.transfer_sub_type");
		strBuff.append(" AND CTRF.status = 'CLOSE' AND L1.lookup_code = CTRF.status AND L1.lookup_type = 'CTSTA'");
		strBuff.append(" GROUP BY CTRF.ACTIVE_USER_ID, CTRF.to_user_id, U.user_name, CTRF.msisdn,U2.user_name, CTRF.to_msisdn ,CTRF.transfer_id,");
		strBuff.append("L.lookup_name , CTRF.TYPE, CTRF.close_date,P.product_name, L1.lookup_name,");
		strBuff.append("  CTRF.sender_category_code, CTRF.receiver_category_code , REC_CAT.category_name,CTRF.SOURCE");
		
		

		PreparedStatement pstmt;

		String selectQuery = strBuff.toString();
		pstmt = con.prepareStatement(selectQuery);
		int i = 1;
		
		pstmt.setString(i++,Constants.getProperty("report.datetimeformat")); 
		pstmt.setString(i++, usersReportModel.getFromDate());
		pstmt.setString(i++,Constants.getProperty("report.datetimeformat")); 
		pstmt.setString(i++, usersReportModel.getToDate());
		pstmt.setString(i++,Constants.getProperty("report.datetimeformat")); 
		pstmt.setString(i++, usersReportModel.getNetworkCode());
		pstmt.setString(i++, usersReportModel.getLoginUserID());
		pstmt.setString(i++, usersReportModel.getTxnSubType());
		pstmt.setString(i, usersReportModel.getTxnSubType());
		
		
		
		
		
		
		
		
		
		
		
		
		
		return pstmt;
		
	}
}

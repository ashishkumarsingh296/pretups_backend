package com.btsl.db.query.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.reports.businesslogic.StaffC2CTransferdetailsRptQry;
import com.btsl.util.Constants;
import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * @author pankaj.kumar
 *
 */
public class StaffC2CTransferDetailsOracleQry implements StaffC2CTransferdetailsRptQry {

	private static final Log log=LogFactory.getLog(StaffC2CTransferDetailsOracleQry.class.getName());
	private static final String DATETIMES = "report.datetimeformat";

	@Override
	public PreparedStatement loadstaffc2cTransferDetailsReportQry(
			UsersReportModel usersReportModel, Connection con)
			throws SQLException, ParseException {
		return null;
	}


	@Override
	public PreparedStatement loadstaffc2cTransferDetailsChannelUserReportQry(
			UsersReportModel usersReportModel, Connection con)
			throws SQLException, ParseException {
		
		final String methodName = "loadstaffc2cTransferDetailsChannelUserReportQry";
 
		final StringBuilder strBuff = new StringBuilder();
		
		strBuff.append(" SELECT CTRF.ACTIVE_USER_ID, CTRF.to_user_id, (U.user_name ) from_user,(U2.user_name || '(' ||CTRF.to_msisdn||')') to_user,CTRF.transfer_id, L.lookup_name transfer_sub_type, ");
		strBuff.append("  CTRF.TYPE, TO_CHAR(CTRF.close_date,? ) close_date, ");
		strBuff.append(" P.product_name, SUM(CTI.required_quantity) transfer_mrp, SUM(CTI.payable_amount) payable_amount, SUM(CTI.net_payable_amount) net_payable_amount, ");
		strBuff.append("  L1.lookup_name status, SUM(CTI.mrp) mrp, SUM(CTI.commission_value) commision, ");
		strBuff.append(" SUM(CTI.commision_quantity) commision_quantity,SUM(CTI.receiver_credit_quantity) receiver_credit_quantity,SUM(CTI.sender_debit_quantity) sender_debit_quantity,  SUM(CTI.tax3_value)tax3_value, ");
		strBuff.append(" SUM(CTI.tax1_value)tax1_value,SUM(CTI.tax2_value)tax2_value, ");
		strBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code, SEND_CAT.category_name sender_category_name, REC_CAT.category_name receiver_category_name,CTRF.SOURCE");
		strBuff.append(" FROM  CHANNEL_TRANSFERS CTRF, CHANNEL_TRANSFERS_ITEMS CTI,USERS U,USERS U2,PRODUCTS P,LOOKUPS L, LOOKUPS L1, CATEGORIES SEND_CAT, ");
		strBuff.append(" CATEGORIES REC_CAT WHERE CTRF.TYPE = 'C2C' ");
		strBuff.append(" AND CTRF.close_date >=TO_DATE(? , ? ) AND CTRF.close_date <= TO_DATE(? , ? ) "); 
		strBuff.append(" AND CTRF.network_code = ?  ");
		strBuff.append(" AND SEND_CAT.domain_code  = ? ");
		strBuff.append(" AND CTRF.control_transfer<>'A'  AND CTRF.sender_category_code = SEND_CAT.category_code ");
		strBuff.append(" AND CTRF.receiver_category_code = REC_CAT.category_code AND CTRF.sender_category_code= ?  ");
		strBuff.append(" AND CTRF.FROM_USER_ID= ?  ");
		strBuff.append(" AND CTRF.ACTIVE_USER_ID=CASE ? WHEN 'ALL' THEN ACTIVE_USER_ID ELSE ?  END ");
		strBuff.append(" AND CTRF.transfer_sub_type = CASE ? WHEN 'ALL' THEN CTRF.transfer_sub_type ELSE ?  END  AND U.user_id = CTRF.ACTIVE_USER_ID ");
		strBuff.append(" AND U2.user_id =CTRF.to_user_id AND CTRF.transfer_id = CTI.transfer_id ");
		strBuff.append(" AND CTI.product_code = P.product_code AND L.lookup_type ='TRFT' AND CTRF.status = 'CLOSE' ");
		strBuff.append(" AND L.lookup_code = CTRF.transfer_sub_type AND L1.lookup_code = CTRF.status ");
		strBuff.append(" AND L1.lookup_type = 'CTSTA' ");
		strBuff.append(" GROUP BY CTRF.ACTIVE_USER_ID, CTRF.to_user_id, U.user_name, CTRF.msisdn,U2.user_name, CTRF.to_msisdn ,CTRF.transfer_id,L.lookup_name , CTRF.TYPE,CTRF.close_date,P.product_name, L1.lookup_name,CTRF.sender_category_code,  ");
		strBuff.append("  CTRF.receiver_category_code,SEND_CAT.category_name , REC_CAT.category_name,CTRF.SOURCE ");
		strBuff.append(" UNION ");
		strBuff.append(" SELECT CTRF.ACTIVE_USER_ID, CTRF.to_user_id, (U.user_name ) from_user,(U2.user_name || '(' ||CTRF.to_msisdn||')') to_user,CTRF.transfer_id,  L.lookup_name transfer_sub_type, ");
		strBuff.append("  CTRF.TYPE, TO_CHAR(CTRF.close_date, ? ) close_date, P.product_name, ");
		strBuff.append("  SUM(CTI.required_quantity) transfer_mrp, SUM(CTI.payable_amount) payable_amount, ");
		strBuff.append(" SUM(CTI.net_payable_amount) net_payable_amount, L1.lookup_name status, SUM(CTI.mrp) mrp, SUM(CTI.commission_value) commision, ");
		strBuff.append(" SUM(CTI.commision_quantity) commision_quantity,SUM(CTI.receiver_credit_quantity) receiver_credit_quantity,SUM(CTI.sender_debit_quantity) sender_debit_quantity, SUM(CTI.tax3_value)tax3_value, ");
		strBuff.append(" SUM(CTI.tax1_value)tax1_value,SUM(CTI.tax2_value)tax2_value, CTRF.sender_category_code, CTRF.receiver_category_code, SEND_CAT.category_name sender_category_name, ");
		strBuff.append("  REC_CAT.category_name receiver_category_name,CTRF.SOURCE ");
		strBuff.append(" FROM  ");
		strBuff.append("CHANNEL_TRANSFERS CTRF, CHANNEL_TRANSFERS_ITEMS CTI,USERS U,USERS U2,PRODUCTS P,LOOKUPS L, LOOKUPS L1, CATEGORIES SEND_CAT, ");
		strBuff.append(" CATEGORIES REC_CAT ");
		strBuff.append(" WHERE CTRF.TYPE = 'C2C' ");
		strBuff.append(" AND CTRF.close_date >=TO_DATE(? , ? ) AND CTRF.close_date <= TO_DATE(? ,? ) ");	  
		strBuff.append("  AND CTRF.network_code = ? ");
		strBuff.append(" AND REC_CAT.domain_code  = ? ");
		strBuff.append(" AND CTRF.control_transfer<>'A' AND CTRF.sender_category_code = SEND_CAT.category_code  ");
		strBuff.append(" AND CTRF.receiver_category_code = REC_CAT.category_code AND CTRF.receiver_category_code= ? ");
		strBuff.append(" AND CTRF.TO_USER_ID= ? ");
		strBuff.append(" AND CTRF.ACTIVE_USER_ID=CASE ? WHEN 'ALL' THEN ACTIVE_USER_ID ELSE ? END ");
		strBuff.append(" AND CTRF.transfer_sub_type = CASE ? WHEN 'ALL' THEN CTRF.transfer_sub_type ELSE ? END ");
		strBuff.append(" AND U.user_id = CTRF.ACTIVE_USER_ID  AND U2.user_id =CTRF.to_user_id");
		strBuff.append(" AND CTRF.transfer_id = CTI.transfer_id AND CTI.product_code = P.product_code  ");
		strBuff.append("  AND L.lookup_type ='TRFT' AND L.lookup_code = CTRF.transfer_sub_type ");
		strBuff.append(" AND CTRF.status = 'CLOSE' AND L1.lookup_code = CTRF.status  ");
		strBuff.append("  AND L1.lookup_type = 'CTSTA' ");
		strBuff.append(" GROUP BY CTRF.ACTIVE_USER_ID, CTRF.to_user_id, U.user_name, CTRF.msisdn,U2.user_name, CTRF.to_msisdn ,CTRF.transfer_id, L.lookup_name , ");
		strBuff.append("   CTRF.TYPE,CTRF.close_date,P.product_name, L1.lookup_name, ");
		strBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code,SEND_CAT.category_name , REC_CAT.category_name,CTRF.SOURCE ");
	
		String selectQuery=strBuff.toString();
		if(log.isDebugEnabled()){ 
			  log.debug(methodName,"QUERY SELECT = "+ selectQuery); 
	     }  
		PreparedStatement pstmt = null;
	       
	        try{
				pstmt = con.prepareStatement(selectQuery);
				int i=1;
				   
				
				    pstmt.setString(i++,Constants.getProperty(DATETIMES));
				
				    pstmt.setString(i++,usersReportModel.getRptfromDate());
				    pstmt.setString(i++,Constants.getProperty(DATETIMES));
				    
				    pstmt.setString(i++,usersReportModel.getRpttoDate());
		            pstmt.setString(i++, Constants.getProperty(DATETIMES));
				    
		            pstmt.setString(i++, usersReportModel.getNetworkCode());
		            pstmt.setString(i++, usersReportModel.getDomainCode()); //domainCode
		            pstmt.setString(i++, usersReportModel.getTransferUserCategoryCode());// searchCategory
		            pstmt.setString(i++, usersReportModel.getParentUserID());//ParentUserID
		            pstmt.setString(i++, usersReportModel.getUserID());  //searchUserID
		            pstmt.setString(i++, usersReportModel.getUserID()); //searchUserID
		            
		            pstmt.setString(i++, usersReportModel.getTxnSubType()); //transferSubType
		            pstmt.setString(i++, usersReportModel.getTxnSubType()); //transferSubType
		            
		            pstmt.setString(i++,Constants.getProperty(DATETIMES));
					
				    pstmt.setString(i++,usersReportModel.getRptfromDate());
				    pstmt.setString(i++,Constants.getProperty(DATETIMES));
				    
				    pstmt.setString(i++,usersReportModel.getRpttoDate());
		            pstmt.setString(i++, Constants.getProperty(DATETIMES));
				    
		            pstmt.setString(i++, usersReportModel.getNetworkCode());
		            pstmt.setString(i++, usersReportModel.getDomainCode()); //domainCode
		            pstmt.setString(i++, usersReportModel.getTransferUserCategoryCode());// searchCategory
		            pstmt.setString(i++, usersReportModel.getParentUserID());//ParentUserID
		            pstmt.setString(i++, usersReportModel.getUserID());  //searchUserID
		            pstmt.setString(i++, usersReportModel.getUserID()); //searchUserID
		            
		            pstmt.setString(i++, usersReportModel.getTxnSubType()); //transferSubType
		            pstmt.setString(i, usersReportModel.getTxnSubType()); //transferSubType
		            
		            
		      
	        } catch (SQLException e) {
				log.error(methodName, "SQLException: "+e.getMessage());
				log.errorTrace(methodName, e);
			} catch (Exception e) {
				log.error(methodName, "Exception: "+e.getMessage());
				log.errorTrace(methodName, e);
			}
	             return pstmt;
		
		

	}


	@Override
	public PreparedStatement loadstaffc2cTransferDetailsDailyReportQry(
			UsersReportModel usersReportModel, Connection con)
			throws SQLException {

		final String methodName = "loadstaffc2cTransferDetailsDailyReportQry";
		 
		final StringBuilder strBuff = new StringBuilder();
		
		strBuff.append(" SELECT CTRF.ACTIVE_USER_ID, CTRF.to_user_id, (U.user_name ) from_user,(U2.user_name || '(' ||CTRF.to_msisdn||')') to_user,CTRF.transfer_id, ");
		strBuff.append(" L.lookup_name transfer_sub_type, CTRF.TYPE, TO_CHAR(CTRF.close_date,? ) close_date, ");
		strBuff.append(" P.product_name, SUM(CTI.required_quantity) transfer_mrp, SUM(CTI.payable_amount) payable_amount, ");
		strBuff.append(" SUM(CTI.net_payable_amount) net_payable_amount, L1.lookup_name status, ");
		strBuff.append(" SUM(CTI.mrp) mrp, SUM(CTI.commission_value) commision, ");
		strBuff.append(" SUM(CTI.commision_quantity) commision_quantity,SUM(CTI.receiver_credit_quantity) receiver_credit_quantity,SUM(CTI.sender_debit_quantity) sender_debit_quantity, ");
		strBuff.append(" SUM(CTI.tax3_value)tax3_value,SUM(CTI.tax1_value)tax1_value,SUM(CTI.tax2_value)tax2_value, ");
		strBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code, ");
		strBuff.append(" SEND_CAT.category_name sender_category_name, REC_CAT.category_name receiver_category_name,CTRF.SOURCE ");
		strBuff.append(" FROM  CHANNEL_TRANSFERS CTRF, CHANNEL_TRANSFERS_ITEMS CTI,USERS U,USERS U2,PRODUCTS P,LOOKUPS L, LOOKUPS L1, CATEGORIES SEND_CAT, ");
		strBuff.append(" CATEGORIES REC_CAT WHERE CTRF.TYPE = 'C2C' ");
		strBuff.append(" AND CTRF.close_date >=TO_DATE(? , ? ) "); 
		strBuff.append(" AND CTRF.close_date <= TO_DATE(? , ? )  ");  
		strBuff.append(" AND CTRF.network_code = ?  ");
		strBuff.append(" AND SEND_CAT.domain_code  = ? ");
		strBuff.append(" AND CTRF.control_transfer<>'A' ");
		strBuff.append(" AND CTRF.sender_category_code = SEND_CAT.category_code ");
		strBuff.append(" AND CTRF.receiver_category_code = REC_CAT.category_code ");
		strBuff.append(" AND CTRF.sender_category_code= ? ");
		strBuff.append(" AND CTRF.FROM_USER_ID= ?  ");
		strBuff.append(" AND CTRF.ACTIVE_USER_ID=CASE ? WHEN 'ALL' THEN ACTIVE_USER_ID ELSE ?  END ");
		strBuff.append(" AND CTRF.transfer_sub_type = CASE ? WHEN 'ALL' THEN CTRF.transfer_sub_type ELSE ?  END ");
		strBuff.append(" AND U.user_id = CTRF.ACTIVE_USER_ID ");
		strBuff.append(" AND U2.user_id =CTRF.to_user_id ");
		strBuff.append(" AND CTRF.transfer_id = CTI.transfer_id ");
		strBuff.append(" AND CTI.product_code = P.product_code ");
		strBuff.append(" AND L.lookup_type ='TRFT' ");
		strBuff.append(" AND L.lookup_code = CTRF.transfer_sub_type ");
		strBuff.append(" AND CTRF.status = 'CLOSE' ");
		strBuff.append(" AND L1.lookup_code = CTRF.status ");
		strBuff.append(" AND L1.lookup_type = 'CTSTA' ");
		strBuff.append(" GROUP BY CTRF.ACTIVE_USER_ID, CTRF.to_user_id, U.user_name, CTRF.msisdn,U2.user_name, CTRF.to_msisdn ,CTRF.transfer_id, ");
		strBuff.append(" L.lookup_name , CTRF.TYPE,CTRF.close_date,P.product_name, L1.lookup_name, ");
		strBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code,SEND_CAT.category_name , REC_CAT.category_name,CTRF.SOURCE ");
		strBuff.append(" UNION ");
		strBuff.append(" SELECT CTRF.ACTIVE_USER_ID, CTRF.to_user_id, (U.user_name ) from_user,(U2.user_name || '(' ||CTRF.to_msisdn||')') to_user,CTRF.transfer_id, ");
		strBuff.append(" L.lookup_name transfer_sub_type, CTRF.TYPE, TO_CHAR(CTRF.close_date, ? ) close_date, ");
		strBuff.append(" P.product_name, SUM(CTI.required_quantity) transfer_mrp, SUM(CTI.payable_amount) payable_amount, ");
		strBuff.append(" SUM(CTI.net_payable_amount) net_payable_amount, L1.lookup_name status, ");
		strBuff.append(" SUM(CTI.mrp) mrp, SUM(CTI.commission_value) commision, ");
		strBuff.append(" SUM(CTI.commision_quantity) commision_quantity,SUM(CTI.receiver_credit_quantity) receiver_credit_quantity,SUM(CTI.sender_debit_quantity) sender_debit_quantity, ");
		strBuff.append(" SUM(CTI.tax3_value)tax3_value,SUM(CTI.tax1_value)tax1_value,SUM(CTI.tax2_value)tax2_value, ");
		strBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code, ");
		strBuff.append(" SEND_CAT.category_name sender_category_name, REC_CAT.category_name receiver_category_name,CTRF.SOURCE ");
		strBuff.append(" FROM  ");
		strBuff.append("CHANNEL_TRANSFERS CTRF, CHANNEL_TRANSFERS_ITEMS CTI,USERS U,USERS U2,PRODUCTS P,LOOKUPS L, LOOKUPS L1, CATEGORIES SEND_CAT, ");
		strBuff.append(" CATEGORIES REC_CAT ");
		strBuff.append(" WHERE CTRF.TYPE = 'C2C' ");
		strBuff.append(" AND CTRF.close_date >=TO_DATE(? , ? ) ");	
		strBuff.append("  AND CTRF.close_date <= TO_DATE(? ,? ) ");	  
		strBuff.append("  AND CTRF.network_code = ? ");
		strBuff.append(" AND REC_CAT.domain_code  = ? ");
		strBuff.append(" AND CTRF.control_transfer<>'A' ");
		strBuff.append(" AND CTRF.sender_category_code = SEND_CAT.category_code ");
		strBuff.append(" AND CTRF.receiver_category_code = REC_CAT.category_code ");
		strBuff.append(" AND CTRF.receiver_category_code= ? "); 
		strBuff.append(" AND CTRF.TO_USER_ID= ? ");
		strBuff.append(" AND CTRF.ACTIVE_USER_ID=CASE ? WHEN 'ALL' THEN ACTIVE_USER_ID ELSE ? END ");
		strBuff.append(" AND CTRF.transfer_sub_type = CASE ? WHEN 'ALL' THEN CTRF.transfer_sub_type ELSE ? END ");
		strBuff.append(" AND U.user_id = CTRF.ACTIVE_USER_ID ");
		strBuff.append("  AND U2.user_id =CTRF.to_user_id ");
		strBuff.append(" AND CTRF.transfer_id = CTI.transfer_id ");
		strBuff.append(" AND CTI.product_code = P.product_code ");
		strBuff.append("  AND L.lookup_type ='TRFT' ");
		strBuff.append(" AND L.lookup_code = CTRF.transfer_sub_type ");
		strBuff.append(" AND CTRF.status = 'CLOSE' ");
		strBuff.append(" AND L1.lookup_code = CTRF.status ");
		strBuff.append("  AND L1.lookup_type = 'CTSTA' ");
		strBuff.append(" GROUP BY CTRF.ACTIVE_USER_ID, CTRF.to_user_id, U.user_name, CTRF.msisdn,U2.user_name, CTRF.to_msisdn ,CTRF.transfer_id, ");
		strBuff.append("  L.lookup_name , CTRF.TYPE,CTRF.close_date,P.product_name, L1.lookup_name, ");
		strBuff.append(" CTRF.sender_category_code, CTRF.receiver_category_code,SEND_CAT.category_name , REC_CAT.category_name,CTRF.SOURCE ");
	
		String selectQuery=strBuff.toString();
		if(log.isDebugEnabled()){ 
			  log.debug(methodName,"QUERY SELECT = "+ selectQuery); 
	     }  
		PreparedStatement pstmt = null;
	       
	        try{
				pstmt = con.prepareStatement(selectQuery);
				int i=1;
				   
				
				    pstmt.setString(i++,Constants.getProperty(DATETIMES));
				
				    pstmt.setString(i++,usersReportModel.getRptfromDate());
				    pstmt.setString(i++,Constants.getProperty(DATETIMES));
				    
				    pstmt.setString(i++,usersReportModel.getRpttoDate());
		            pstmt.setString(i++, Constants.getProperty(DATETIMES));
				    
		            pstmt.setString(i++, usersReportModel.getNetworkCode());
		            pstmt.setString(i++, usersReportModel.getDomainCode()); //domainCode
		            pstmt.setString(i++, usersReportModel.getTransferUserCategoryCode());// searchCategory
		            pstmt.setString(i++, usersReportModel.getParentUserID());//ParentUserID
		            pstmt.setString(i++, usersReportModel.getUserID());  //searchUserID
		            pstmt.setString(i++, usersReportModel.getUserID()); //searchUserID
		            
		            pstmt.setString(i++, usersReportModel.getTxnSubType()); //transferSubType
		            pstmt.setString(i, usersReportModel.getTxnSubType()); //transferSubType
		            
		      
	        } catch (SQLException e) {
				log.error(methodName, "SQLException: "+e.getMessage());
				log.errorTrace(methodName, e);
			} catch (Exception e) {
				log.error(methodName, "Exception: "+e.getMessage());
				log.errorTrace(methodName, e);
			}
	             return pstmt;
		
	}
	

}

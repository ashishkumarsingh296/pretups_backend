package com.btsl.db.query.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import com.btsl.pretups.channel.reports.businesslogic.O2CTransfernumberAckRptQry;
import com.btsl.util.Constants;
import com.web.pretups.channel.transfer.web.ChannelTransferAckModel;

/**
 * @author pankaj.kumar
 *
 */
public class O2CTransferNumberAckOracleQry implements O2CTransfernumberAckRptQry {

	@Override
	public PreparedStatement loado2cTransferAskDetailsReportQry(
			ChannelTransferAckModel channelTransferAckModel, Connection con)
			throws SQLException, ParseException {
		
		return null;
	}

	@Override
	public PreparedStatement loado2cTransfeAskChannelUserReportQry(        
			ChannelTransferAckModel channelTransferAckModel, Connection con)
			throws SQLException, ParseException {
		
	
					final StringBuilder strBuffer = new StringBuilder("SELECT CT.transfer_id, G.grph_domain_name, D.domain_name, ");
				   strBuffer.append(" (case  when CT.receiver_category_code='OPT' then C1.category_name when CT.sender_category_code='OPT' then C.category_name end) category_name, ");
				   strBuffer.append(" CT.transfer_type,U.user_name, CT.ext_txn_no, TO_CHAR(CT.ext_txn_date,? ) ext_txn_date, CPS.comm_profile_set_name, TP.profile_name, ");
				   strBuffer.append(" TO_CHAR(CT.transfer_date,? ) transfer_date, L.lookup_name transfer_category,CT.reference_no, ");
				   strBuffer.append(" U.msisdn ,U.address1,U.address2,U.city,U.state,U.country, U.external_code, ");
				   strBuffer.append(" P.product_name, P.product_short_code, CTI.product_code, ");
				   strBuffer.append(" CTI.commission_rate, CTI.commission_type, CTI.commission_value, CTI.mrp, CTI.net_payable_amount, CTI.payable_amount,CTI.user_unit_price, ");
				   strBuffer.append(" CTI.required_quantity, CTI.tax1_rate, CTI.tax1_type, CTI.tax1_value, CTI.tax2_rate, CTI.tax2_type, CTI.tax2_value, CTI.tax3_value,CTI.otf_type,CTI.otf_rate,CTI.otf_amount, ");
				   strBuffer.append(" CTI.commision_quantity,CTI.receiver_credit_quantity,CTI.sender_debit_quantity, ");
				   strBuffer.append(" CT.pmt_inst_no,TO_CHAR(CT.pmt_inst_date, ?) pmt_inst_date, CT.pmt_inst_amount, CT.source, CT.pmt_inst_source , ");
				   strBuffer.append(" CT.first_approver_remarks, CT.second_approver_remarks, CT.third_approver_remarks, CT.channel_user_remarks, ");
				   strBuffer.append("  APPU1.user_name first_approved_by, TO_CHAR(CT.first_approved_on, ?) first_approved_on, ");
				   strBuffer.append("  CT.first_level_approved_quantity, CT.second_level_approved_quantity, CT.third_level_approved_quantity,CTI.approved_quantity, ");
				   strBuffer.append(" APPU5.user_name transfer_initiated_by, L1.lookup_name status, L2.lookup_name transfer_sub_type , L3.lookup_name pmt_inst_type ");
				   strBuffer.append(" FROM channel_transfers CT, geographical_domains G, categories C ,  categories C1 ,users U , commission_profile_set CPS, domains D , ");
				   strBuffer.append(" transfer_profile TP ,transfer_profile TP2, users APPU1, users APPU2 ,users APPU3 , users APPU5 , users APPU6, ");
				   strBuffer.append(" lookups L, lookups L1, lookups L2, lookups L3, channel_transfers_items CTI, products P ");
				   strBuffer.append("WHERE CT.transfer_id =? ");
				   strBuffer.append(" AND CT.transfer_id = CTI.transfer_id ");
				   strBuffer.append(" AND CTI.product_code = P.product_code ");
				   strBuffer.append(" AND CT.network_code = ?  ");
				   strBuffer.append(" AND CT.domain_code = D.domain_code ");
					  strBuffer.append(" AND CT.grph_domain_code = G.grph_domain_code ");
					  strBuffer.append(" AND CT.receiver_category_code = C.category_code ");
					  strBuffer.append(" AND CT.sender_category_code = C1.category_code ");
					  strBuffer.append(" AND (case CT.from_user_id when 'OPT' then  CT.to_user_id end = U.user_id  OR  case CT.to_user_id when 'OPT' then  CT.from_user_id end = U.user_id) ");
					  strBuffer.append(" AND CT.commission_profile_set_id =  CPS.comm_profile_set_id ");
					  strBuffer.append("AND CT.receiver_txn_profile = TP.profile_id(+) ");
					  strBuffer.append(" AND CT.sender_txn_profile = TP2.profile_id(+) ");
					  strBuffer.append(" AND CT.first_approved_by = APPU1.user_id(+)  ");
					  strBuffer.append(" AND CT.second_approved_by = APPU2.user_id(+) ");
					  strBuffer.append(" AND CT.third_approved_by = APPU3.user_id(+) ");
					  strBuffer.append(" AND CT.transfer_initiated_by = APPU5.user_id(+) ");
					  strBuffer.append(" AND CT.modified_by = APPU6.user_id(+) ");
					  strBuffer.append("AND CT.transfer_category = L.lookup_code(+) ");
					  strBuffer.append(" AND L.lookup_type(+) = 'TRFTY' ");
					  strBuffer.append(" AND CT.status = L1.lookup_code(+) ");
					  strBuffer.append(" AND L1.lookup_type(+) = 'TSTAT' ");
					  strBuffer.append(" AND CT.transfer_sub_type =  L2.lookup_code(+) ");
					  strBuffer.append(" AND L2.lookup_type(+) = 'TRFT' ");
					  strBuffer.append(" AND CT.pmt_inst_type =  L3.lookup_code(+) ");
					  strBuffer.append(" AND L3.lookup_type(+) = 'PMTYP'");
					  
				  
				  String sqlSelect=strBuffer.toString();
				  PreparedStatement pstmtSelect;
				  pstmtSelect =  con.prepareStatement(sqlSelect);
				  int i = 1;
				 
				
				  pstmtSelect.setString(i++, Constants.getProperty("report.onlydateformat"));
				  pstmtSelect.setString(i++, Constants.getProperty("report.onlydateformat"));
				  pstmtSelect.setString(i++, Constants.getProperty("report.onlydateformat"));
				
				  pstmtSelect.setString(i++, Constants.getProperty("report.datetimeformat"));
				
				  pstmtSelect.setString(i++, channelTransferAckModel.getTransferNum());
				  pstmtSelect.setString(i, channelTransferAckModel.getNetworkCode());
				
				return pstmtSelect;
	}

	@Override
	public PreparedStatement loado2cTransferAskDailyReportQry(
			ChannelTransferAckModel channelTransferAckModel, Connection con)
			throws SQLException {
		
		return null;
	}

}

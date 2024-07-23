package com.btsl.db.query.postgres;

import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferWebQry;

public class ChannelTransferWebPostgresQry implements ChannelTransferWebQry{
	private String className = "ChannelTransferWebPostgresQry";
	@Override
	public String loadO2CChannelTransfersListQry(String isPrimary,String p_transferID,String p_userCode, String p_transferTypeCode, String p_transferCategory ) {
		String methodName = className+"#loadO2CChannelTransfersListQry";   
		final StringBuffer strBuff = new StringBuffer(" SELECT LKP.lookup_name,ct.transfer_sub_type,ct.requested_quantity,ct.transfer_type,  ct.transaction_mode,");
	        strBuff.append(" gd.grph_domain_name,gd.grph_domain_code ,ct.transfer_id, ct.network_code, ct.network_code_for, ");
	        strBuff.append(" ct.transfer_date, ct.first_approved_by ,ct.first_approved_on, ct.second_approved_by, ");
	        strBuff.append(" ct.second_approved_on, ct.third_approved_by,ct.third_approved_on, ct.cancelled_by,  ");
	        strBuff.append(" ct.cancelled_on, ct.modified_by, ct.modified_on, ct.status,ct.type, ct.payable_amount, ");
	        strBuff.append(" ct.net_payable_amount, u.user_name, appu1.user_name firstapprovedby,appu2.user_name ");
	        strBuff.append(" secondapprovedby,appu3.user_name thirdapprovedby, appu4.user_name cancelledby,u.msisdn,ct.msisdn from_msisdn,ct.to_msisdn to_msisdn, ");
	        strBuff.append(" ct.transfer_category,ct.ext_txn_date, ct.ext_txn_no,ct.to_user_id,ct.from_user_id,ct.domain_code, ");
	        strBuff.append(" ct.first_level_approved_quantity, ct.second_level_approved_quantity, ct.third_level_approved_quantity, ");
	        strBuff.append(" cti.SENDER_POST_STOCK, cti.SENDER_PREVIOUS_STOCK, cti.RECEIVER_POST_STOCK, cti.RECEIVER_PREVIOUS_STOCK");
	        strBuff.append(" FROM channel_transfers ct left outer join users appu1 on ct.first_approved_by = appu1.user_id left outer join users appu2 on ct.second_approved_by = appu2.user_id " );
	        strBuff.append(" left outer join users appu3 on  ct.third_approved_by = appu3.user_id left outer join users appu4 on ct.cancelled_by = appu4.user_id , " );
	        strBuff.append(" CHANNEL_TRANSFERS_ITEMS cti, users u,lookups LKP, ");
	        if (!BTSLUtil.isNullString(isPrimary) && ("N".equalsIgnoreCase(isPrimary))) {
	            strBuff.append(" user_phones up, ");
	        }
	        strBuff.append(" user_geographies ug,geographical_domains gd");
	        strBuff.append(" WHERE ");
	        if (!BTSLUtil.isNullString(p_transferID)) {
	            strBuff.append(" ct.transfer_id = ? AND ");
	            strBuff.append(" (u.user_id=ct.to_user_id OR u.user_id=ct.from_user_id) AND ");
	            strBuff.append(" ct.transfer_date >= ? AND ct.transfer_date <= ? AND ");
	            strBuff.append("  ct.transfer_sub_type= ? AND ");
	        } else if (!BTSLUtil.isNullString(p_userCode)) {
	            if (!BTSLUtil.isNullString(isPrimary) && ("N".equalsIgnoreCase(isPrimary))) {
	                strBuff.append(" up.msisdn = ? AND up.user_id= u.user_id AND");
	            } else {
	                strBuff.append(" u.user_code = ? AND ");
	            }
	            strBuff.append(" (u.user_id=ct.to_user_id OR u.user_id=ct.from_user_id) AND ");
	            if (!BTSLUtil.isNullString(isPrimary) && ("N".equalsIgnoreCase(isPrimary))) {
	                strBuff.append(" (ct.msisdn=up.msisdn OR ct.to_msisdn=up.msisdn) AND");
	            }
	            strBuff.append(" ct.transfer_date >= ? AND ct.transfer_date <= ? AND ");
	            strBuff.append(" ct.transfer_category = ? AND ");
	            strBuff.append(" u.status IN ('Y','S','SR') AND ");
	            strBuff.append("  ct.transfer_sub_type= ? AND ");
	        } else {
	            strBuff.append(" ct.transfer_date >= ? AND ct.transfer_date <= ? AND ct.product_type=? AND ");
	            strBuff.append(" ct.transfer_category = ? AND ");
	            if (!PretupsI.ALL.equals(p_transferTypeCode) && PretupsI.TRANSFER_CATEGORY_SALE.equals(p_transferCategory)) {
	                strBuff.append("  ct.transfer_sub_type= ? AND ");
	            }

	            if (PretupsI.ALL.equals(p_transferTypeCode)) {
	                strBuff.append(" ( (u.user_id=ct.to_user_id AND ct.to_user_id=?) OR (u.user_id=ct.from_user_id AND ct.from_user_id = ?)) AND ");
	            } else if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_transferTypeCode)) {
	                strBuff.append(" u.user_id=ct.to_user_id AND ct.to_user_id=? AND ");
	            } else {
	                strBuff.append(" u.user_id=ct.from_user_id AND ct.from_user_id = ? AND ");
	            }
	            strBuff.append(" u.status IN ('Y','S','SR') AND ");
	        }
	        strBuff.append(" ct.status = ? AND ");
	        strBuff.append(" ct.type = 'O2C' ");
	        strBuff.append(" AND ug.user_id=u.user_id AND gd.grph_domain_code=ug.grph_domain_code ");
	        strBuff.append(" AND LKP.lookup_type=? AND LKP.lookup_code=ct.transfer_sub_type ");
	        strBuff.append(" AND ct.transfer_id=cti.transfer_id ");
	        strBuff.append(" AND ct.ref_transfer_id is null");
	        strBuff.append(" ORDER BY  ct.created_on DESC,ct.transfer_sub_type ");
	        String query = strBuff.toString();
	    	LogFactory.printLog(methodName, QUERY + query, LOG);
	    	return query;
	}

}

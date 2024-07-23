package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.mcom.common.CommonUtil;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

/**
 * @author satakshi.gaur
 *
 */
public class ChannelTransferOracleQry implements ChannelTransferQry {
	
	private Log log = LogFactory.getLog(ChannelTransferOracleQry.class.getName());

	@Override
	public String loadChannelTransfersListQry(String reveiverCategoryCode, String geoCode, String domainCode, String searchParam, String approvalLevel) {
		
		final StringBuilder strBuff = new StringBuilder(" SELECT DISTINCT ct.transfer_id, ct.transfer_date, ct.type,ct.transfer_sub_type,ct.transfer_type, ");
        strBuff.append(" ct.payable_amount, ct.net_payable_amount,ct.ext_txn_no, ct.ext_txn_date,d.domain_name , g.grph_domain_name, ct.dual_comm_type, ");
        strBuff.append(" c.category_name,ct.status , u.user_name, ct.transfer_mrp,ct.reference_no,ct.to_user_id, ");
        strBuff.append(" ct.first_level_approved_quantity, ct.first_approved_by, ct.first_approved_on, ct.second_level_approved_quantity, ct.second_approved_by, ct.second_approved_on, ct.third_level_approved_quantity, ct.third_approved_by, ct.third_approved_on, ct.product_type, ct.receiver_txn_profile,cvi.bundle_id, ");
        strBuff.append(" cti.user_wallet, ct.COMMISSION_PROFILE_SET_ID, ct.COMMISSION_PROFILE_VER , ct.pmt_inst_type ,ct.to_msisdn");
        strBuff.append(" FROM channel_transfers ct , domains d , geographical_domains g ,categories c,users u,channel_voucher_items cvi, ");
        strBuff.append(" users su,channel_transfers_items cti ");
        strBuff.append(" WHERE ct.type='O2C' AND ct.transfer_category=? ");
        strBuff.append(" AND ct.network_code = ? AND ct.network_code_for = ? AND ct.domain_code = d.domain_code ");
        strBuff.append(" AND ct.grph_domain_code = g.grph_domain_code AND ct.transfer_id = cvi.transfer_id(+) AND cvi.s_no(+) = '1' ");
        if (!BTSLUtil.isNullString(reveiverCategoryCode) && !reveiverCategoryCode.equalsIgnoreCase(PretupsI.ALL)) {
            strBuff.append(" AND ct.receiver_category_code=? ");
        }
        strBuff.append(" AND cti.transfer_id = ct.transfer_id ");
        strBuff.append(" AND ct.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains gd1 WHERE status='Y' ");
        strBuff.append(" CONNECT BY PRIOR grph_domain_code=parent_grph_domain_code  ");
        strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code FROM user_geographies WHERE user_id=? ");
        if (!BTSLUtil.isNullString(geoCode)) {
            strBuff.append(" AND grph_domain_code = decode(?,?,grph_domain_code,?)  ");
        }
        strBuff.append(" ) ) AND ct.receiver_category_code = c.category_code AND g.status = 'Y' ");
        strBuff.append(" AND ct.transfer_initiated_by = u.user_id ");

        if (!domainCode.equalsIgnoreCase(PretupsI.ALL)) {
            strBuff.append(" AND trim(ct.domain_code)= ? ");
        }
        if (!PretupsI.ALL.equals(searchParam)) {
            strBuff.append(" AND ct.to_user_id = ?  ");
        }

        if (PretupsI.CHANNEL_TRANSFER_ORDER_NEW.equals(approvalLevel)) {
            strBuff.append(" AND ct.status IN (? , ? ) ");
        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(approvalLevel)) {
            strBuff.append(" AND ( ct.status = ? OR ct.status = ? )  ");
        } else if (PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(approvalLevel)) {
            strBuff.append(" AND ct.status = ? ");
        }

        strBuff.append(" AND ct.to_user_id= su.user_id ");
        strBuff.append(" AND su.OWNER_ID=DECODE(?,'ALL',su.OWNER_ID,?) ");
       // strBuff.append(" ORDER BY ct.created_on DESC ");
		return strBuff.toString();
	}
	
	@Override
	public String loadChannelTransfersVOQry(ChannelTransferVO channelTransferVO)
	{
		 final StringBuilder strBuff = new StringBuilder(" SELECT ct.transfer_id, ct.first_level_approved_quantity, ");
		 	// added for reversal
		 	strBuff.append(" ct.ref_transfer_id, ");
	        strBuff.append(" ct.second_level_approved_quantity, ct.third_level_approved_quantity, ct.network_code, ct.network_code_for, ");
	        strBuff.append(" ct.grph_domain_code, ct.domain_code, ct.sender_category_code, ct.sender_grade_code, ");
	        strBuff.append(" ct.receiver_grade_code, ct.from_user_id, ct.to_user_id, ct.transfer_date, ct.reference_no, ");
	        strBuff.append(" ct.ext_txn_no, ct.ext_txn_date,ct.transfer_category,ct.commission_profile_set_id, ");
	        strBuff.append(" ct.commission_profile_ver, ct.requested_quantity, ct.channel_user_remarks, ");
	        strBuff.append(" ct.first_approver_remarks, ct.second_approver_remarks, ct.third_approver_remarks, ");
	        strBuff.append(" ct.first_approved_by, ct.first_approved_on, ct.second_approved_by, ct.second_approved_on, ");
	        strBuff.append(" ct.third_approved_by, ct.third_approved_on, ct.cancelled_by, ct.cancelled_on, ");
	        strBuff.append(" ct.transfer_initiated_by, ct.modified_by, ct.modified_on, ct.status, ct.type, ");
	        strBuff.append(" ct.transfer_mrp, ct.first_approver_limit, ct.second_approver_limit,  ");
	        strBuff.append(" ct.payable_amount, ct.net_payable_amount, ct.batch_no, ct.batch_date, ct.pmt_inst_type, ct.pmt_inst_no, ");
	        strBuff.append(" ct.pmt_inst_date, ct.pmt_inst_amount, ct.sender_txn_profile, ct.receiver_txn_profile, ct.total_tax1, ct.total_tax2,  ");
	        strBuff.append(" ct.total_tax3, ct.source, ct.receiver_category_code, ct.request_gateway_code, ct.request_gateway_type, ct.pmt_inst_source ,");
	        strBuff.append(" ct.product_type , ct.sms_default_lang,ct.sms_second_lang, d.domain_name , g.grph_domain_name, c.category_name , cg.grade_name, u.user_name,u.address1,u.address2,u.city,u.state,u.country,cps.comm_profile_set_name,tp.profile_name, ");
	        strBuff.append(" appu1.user_name firstapprovedby,appu2.user_name secondapprovedby,appu3.user_name thirdapprovedby, ");
	        strBuff.append(" appu4.user_name cancelledby,appu5.user_name initatedby ,appu5.login_id initiatorLoginID, u.msisdn ,ct.msisdn from_msisdn,ct.to_msisdn to_msisdn, u.external_code , appu6.user_name fromusername ,ct.transfer_type,ct.transfer_sub_type,tp2.profile_name sender_txn_profile_name, ");
	        strBuff.append(" cti.commision_quantity,cti.sender_debit_quantity,cti.receiver_credit_quantity,");
	        strBuff.append(" ct.txn_wallet,ct.control_transfer,ct.to_grph_domain_code,ct.to_domain_code,ct.created_on,ct.created_by,ct.active_user_id,");
	        strBuff.append(" cti.user_wallet, ct.stock_updated,ct.dual_comm_type, ct.info1,ct.PMT_INST_TYPE,cti.product_code,  g1.GRPH_DOMAIN_NAME to_grph_domain_name, ct.SOS_SETTLEMENT_DATE, ct.SOS_STATUS, d1.DOMAIN_NAME to_domain_name");
	        strBuff.append(" FROM    ");
	        strBuff.append(" channel_transfers ct, geographical_domains g, geographical_domains g1, categories c , users u , commission_profile_set cps, domains d , domains d1 , ");
	        strBuff.append(" transfer_profile tp ,transfer_profile tp2, users appu1, users appu2 ,users appu3 , users appu4 ,users appu5 ,channel_grades cg ,users appu6 ,channel_transfers_items cti ");
	        strBuff.append(" WHERE ");
	        strBuff.append(" ct.transfer_id = ? AND ct.transfer_id=cti.transfer_id(+) AND ct.network_code = ? AND ct.network_code_for = ? AND ");
	        strBuff.append(" ct.domain_code = d.domain_code(+) AND ct.TO_DOMAIN_CODE = d1.DOMAIN_CODE(+) AND ct.grph_domain_code = g.grph_domain_code(+) AND ct.TO_GRPH_DOMAIN_CODE = g1.grph_domain_code(+) AND ct.receiver_category_code = c.category_code(+) AND ");
	        /*if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(channelTransferVO.getTransferSubType())) {*/
	            strBuff.append(" ct.to_user_id  = u.user_id(+) AND ct.receiver_grade_code = cg.grade_code(+)  AND ct.receiver_txn_profile = tp.profile_id(+) AND ct.sender_txn_profile = tp2.profile_id(+) AND ");
	        /*} else {
	            strBuff.append(" ct.from_user_id  = u.user_id AND ct.sender_grade_code = cg.grade_code  AND ct.receiver_txn_profile = tp.profile_id(+) AND ct.sender_txn_profile = tp2.profile_id AND ");
	        }*/
	        strBuff.append(" ct.commission_profile_set_id = cps.comm_profile_set_id(+) AND ");
	        strBuff.append(" ct.first_approved_by = appu1.user_id(+)  AND ct.second_approved_by = appu2.user_id(+) AND ct.third_approved_by = appu3.user_id(+) AND ");
	        strBuff.append(" ct.cancelled_by = appu4.user_id(+) AND ct.transfer_initiated_by = appu5.user_id(+) AND ");
	        strBuff.append(" ct.from_user_id = appu6.user_id(+)");

	        return strBuff.toString();
	}
	
	@Override
	public String loadChannelTransfersVOTcpQry(ChannelTransferVO channelTransferVO)
	{
		 final StringBuilder strBuff = new StringBuilder(" SELECT ct.transfer_id, ct.first_level_approved_quantity, ");
	        strBuff.append(" ct.second_level_approved_quantity, ct.third_level_approved_quantity, ct.network_code, ct.network_code_for, ");
	        strBuff.append(" ct.grph_domain_code, ct.domain_code, ct.sender_category_code, ct.sender_grade_code, ");
	        strBuff.append(" ct.receiver_grade_code, ct.from_user_id, ct.to_user_id, ct.transfer_date, ct.reference_no, ");
	        strBuff.append(" ct.ext_txn_no, ct.ext_txn_date,ct.transfer_category,ct.commission_profile_set_id, ");
	        strBuff.append(" ct.commission_profile_ver, ct.requested_quantity, ct.channel_user_remarks, ");
	        strBuff.append(" ct.first_approver_remarks, ct.second_approver_remarks, ct.third_approver_remarks, ");
	        strBuff.append(" ct.first_approved_by, ct.first_approved_on, ct.second_approved_by, ct.second_approved_on, ");
	        strBuff.append(" ct.third_approved_by, ct.third_approved_on, ct.cancelled_by, ct.cancelled_on, ");
	        strBuff.append(" ct.transfer_initiated_by, ct.modified_by, ct.modified_on, ct.status, ct.type, ");
	        strBuff.append(" ct.transfer_mrp, ct.first_approver_limit, ct.second_approver_limit,  ");
	        strBuff.append(" ct.payable_amount, ct.net_payable_amount, ct.batch_no, ct.batch_date, ct.pmt_inst_type, ct.pmt_inst_no, ");
	        strBuff.append(" ct.pmt_inst_date, ct.pmt_inst_amount, ct.sender_txn_profile, ct.receiver_txn_profile, ct.total_tax1, ct.total_tax2,  ");
	        strBuff.append(" ct.total_tax3, ct.source, ct.receiver_category_code, ct.request_gateway_code, ct.request_gateway_type, ct.pmt_inst_source ,");
	        strBuff.append(" ct.product_type , ct.sms_default_lang,ct.sms_second_lang, d.domain_name , g.grph_domain_name, c.category_name , cg.grade_name, u.user_name,u.address1,u.address2,u.city,u.state,u.country,cps.comm_profile_set_name, ");
	        strBuff.append(" appu1.user_name firstapprovedby,appu2.user_name secondapprovedby,appu3.user_name thirdapprovedby, ");
	        strBuff.append(" appu4.user_name cancelledby,appu5.user_name initatedby , u.msisdn ,ct.msisdn from_msisdn,ct.to_msisdn to_msisdn, u.external_code , appu6.user_name fromusername ,ct.transfer_type,ct.transfer_sub_type, ");
	        strBuff.append(" cti.commision_quantity,cti.sender_debit_quantity,cti.receiver_credit_quantity,");
	        strBuff.append(" ct.txn_wallet,ct.control_transfer,ct.to_grph_domain_code,ct.to_domain_code,ct.created_on,ct.created_by,ct.active_user_id,");
	        strBuff.append(" cti.user_wallet, ct.stock_updated,ct.dual_comm_type, ct.info1,ct.PMT_INST_TYPE,cti.product_code  ");
	        strBuff.append(" FROM ");
	        strBuff.append(" channel_transfers ct, geographical_domains g, categories c , users u , commission_profile_set cps, domains d , ");
	        strBuff.append("  users appu1, users appu2 ,users appu3 , users appu4 ,users appu5 ,channel_grades cg ,users appu6 ,channel_transfers_items cti");
	        strBuff.append(" WHERE ");
	        strBuff.append(" ct.transfer_id = ? AND ct.transfer_id=cti.transfer_id AND ct.network_code = ? AND ct.network_code_for = ? AND ");
	        strBuff.append(" ct.domain_code = d.domain_code AND ct.grph_domain_code = g.grph_domain_code AND ct.receiver_category_code = c.category_code AND ");
	        /*if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(channelTransferVO.getTransferSubType())) {*/
	            strBuff.append(" ct.to_user_id  = u.user_id AND ct.receiver_grade_code = cg.grade_code  AND ");
	        /*} else {
	            strBuff.append(" ct.from_user_id  = u.user_id AND ct.sender_grade_code = cg.grade_code  AND ct.receiver_txn_profile = tp.profile_id(+) AND ct.sender_txn_profile = tp2.profile_id AND ");
	        }*/
	        strBuff.append(" ct.commission_profile_set_id = cps.comm_profile_set_id AND ");
	        strBuff.append(" ct.first_approved_by = appu1.user_id(+)  AND ct.second_approved_by = appu2.user_id(+) AND ct.third_approved_by = appu3.user_id(+) AND ");
	        strBuff.append(" ct.cancelled_by = appu4.user_id(+) AND ct.transfer_initiated_by = appu5.user_id(+) AND ");
	        strBuff.append(" ct.from_user_id = appu6.user_id(+) ");

	        return strBuff.toString();
	}

	
	@Override
	public String loadChannelTransferItemsQry()
	{
		Boolean othComChnl = (Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL);
		final StringBuilder strBuff = new StringBuilder(
	            "SELECT NVL(ns.wallet_balance,0)wallet_balance ,ns.wallet_type,TMP.txn_wallet,TMP.mrp,TMP.s_no,TMP.transfer_id,TMP.product_code,");
	        strBuff.append("TMP.required_quantity,TMP.approved_quantity, TMP.user_unit_price,TMP. commission_profile_detail_id,");
	        strBuff.append("TMP.commission_type,TMP.commission_rate,TMP.commission_value,TMP.tax1_type,TMP.tax1_rate,");
	        strBuff.append("TMP.tax1_value,TMP.tax2_type,TMP.tax2_rate,TMP.tax2_value,TMP.tax3_type,TMP.tax3_rate,TMP.tax3_value,");
	        strBuff.append("TMP.payable_amount,TMP.net_payable_amount,TMP.sender_previous_stock,TMP.receiver_previous_stock,TMP.short_name,");
	        strBuff.append("TMP.SENDER_POST_STOCK,TMP.RECEIVER_POST_STOCK, "); 
	        strBuff
	            .append("TMP.product_short_code, TMP.product_name, TMP.unit_value,TMP.min_transfer_value,TMP.max_transfer_value,TMP.network_code,TMP.network_code_for,TMP.commision_quantity,TMP.sender_debit_quantity,TMP.receiver_credit_quantity, ");
	        strBuff.append("TMP.FIRST_LEVEL_APPROVED_QUANTITY,TMP.SECOND_LEVEL_APPROVED_QUANTITY,TMP.THIRD_LEVEL_APPROVED_QUANTITY,TMP.transfer_multiple_off,tmp.cell_id,tmp.otf_type,tmp.otf_rate,tmp.otf_amount,tmp.otf_applicable ,TMP.PMT_INST_TYPE,TMP.FIRST_LEVEL_APPROVED_QTY,TMP.SECOND_LEVEL_APPROVED_QTY ");
			if(othComChnl)
			strBuff.append(",TMP.OTH_COMMISSION_TYPE,TMP.OTH_COMMISSION_RATE,TMP.OTH_COMMISSION_VALUE,TMP.OTH_COMM_PRF_SET_ID ");
	        strBuff.append("FROM (SELECT cti.s_no,cti.transfer_id ,cti.product_code,cti.required_quantity,cti.approved_quantity,");
	        strBuff.append("cti.user_unit_price,cti.commission_profile_detail_id,cti.commission_type,cti.mrp, cti.commission_rate,");
	        strBuff.append("cti.commission_value,cti.tax1_type, cti.tax1_rate,cti.tax1_value,cti.tax2_type, cti.tax2_rate,  cti.tax2_value, ");
	        strBuff.append("cti.SENDER_POST_STOCK,cti.RECEIVER_POST_STOCK, "); 
	        strBuff.append("cti.tax3_type, cti.tax3_rate, cti.tax3_value,  cti.payable_amount,   cti.net_payable_amount, cti.sender_previous_stock,");
	        strBuff.append("cti.receiver_previous_stock ,p.short_name,p.product_short_code, p.product_name,p.unit_value,cpp.min_transfer_value,");
	        strBuff
	            .append("cpp.max_transfer_value,ct.network_code ,ct.network_code_for,cti.commision_quantity,cti.sender_debit_quantity,cti.receiver_credit_quantity, cpp.transfer_multiple_off, ");
	        strBuff.append("ct.txn_wallet,ct.FIRST_LEVEL_APPROVED_QUANTITY, ct.SECOND_LEVEL_APPROVED_QUANTITY, ct.THIRD_LEVEL_APPROVED_QUANTITY , ct.PMT_INST_TYPE ");

	        strBuff.append(",ct.cell_id,cti.otf_type,cti.otf_rate,cti.otf_amount,cti.otf_applicable,cti.FIRST_LEVEL_APPROVED_QTY,cti.SECOND_LEVEL_APPROVED_QTY  ");
			if(othComChnl)
			strBuff.append(",cti.OTH_COMMISSION_TYPE,cti.OTH_COMMISSION_RATE,cti.OTH_COMMISSION_VALUE,ct.OTH_COMM_PRF_SET_ID ");

	        strBuff.append("FROM channel_transfers_items cti,products p,commission_profile_details cpd,commission_profile_products cpp,channel_transfers ct ");
	        strBuff.append("WHERE ct.transfer_id = ? AND p.product_code=cti.product_code AND ");
	        strBuff.append("cti.commission_profile_detail_id = cpd.comm_profile_detail_id AND cpp.comm_profile_products_id = cpd.comm_profile_products_id ");
	        strBuff.append("AND cti.transfer_id = ct.transfer_id)  TMP,   network_stocks ns ");
	        strBuff.append("WHERE ns.product_code(+)= TMP.product_code AND ns.network_code (+)=  TMP.network_code ");
	        strBuff.append("AND ns.network_code_for(+) =  TMP.network_code_for ");
	        return strBuff.toString();
	}

	@Override
	public StringBuilder loadEnquiryChannelTransfersListQry(String isPrimary,String transferID,String userCode,String status,String transferCategory,String transferTypeCode)
	{
		final StringBuilder strBuff = new StringBuilder(" SELECT LKP.lookup_name,ct.transfer_sub_type,ct.requested_quantity,ct.transfer_type,");
        strBuff.append(" gd.grph_domain_name,gd.grph_domain_code ,ct.transfer_id, ct.network_code, ct.network_code_for, ");
        strBuff.append(" ct.transfer_date, ct.first_approved_by ,ct.first_approved_on, ct.second_approved_by, ct.dual_comm_type, ");
        strBuff.append(" ct.second_approved_on, ct.third_approved_by,ct.third_approved_on, ct.cancelled_by,  ");
        strBuff.append(" ct.cancelled_on, ct.modified_by, ct.modified_on, ct.status,ct.type, ct.payable_amount, ");
        strBuff.append(" ct.net_payable_amount, u.user_name, appu1.user_name firstapprovedby,appu2.user_name ");
        strBuff.append(" secondapprovedby,appu3.user_name thirdapprovedby, appu4.user_name cancelledby,u.msisdn,ct.msisdn from_msisdn,ct.to_msisdn to_msisdn, ");
        strBuff.append(" ct.transfer_category,ct.ext_txn_date, ct.ext_txn_no,ct.to_user_id,ct.from_user_id,ct.domain_code, ");
        strBuff.append(" ct.first_level_approved_quantity, ct.second_level_approved_quantity, ct.third_level_approved_quantity,ct.channel_user_remarks, ");
        strBuff.append(" cti.SENDER_POST_STOCK, cti.SENDER_PREVIOUS_STOCK, cti.RECEIVER_POST_STOCK, cti.RECEIVER_PREVIOUS_STOCK, ct.PMT_INST_TYPE, ct.transaction_mode,cti.otf_type,cti.otf_rate,cti.otf_amount");
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue())  {
            strBuff.append(",ct.SOS_STATUS, ct.SOS_SETTLEMENT_DATE");
            }
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_TRANSFERS_INFO_REQUIRED))).booleanValue())
        {
        	strBuff.append(",ct.info1,ct.info2 ");
        }
		/*strBuff.append(",vb.batch_no,vp.product_name,vb.batch_type,vb.total_no_of_vouchers,vb.from_serial_no,vb.to_serial_no ");*/	
        strBuff.append(" FROM channel_transfers ct , CHANNEL_TRANSFERS_ITEMS cti, users u,lookups LKP, ");
        if (!BTSLUtil.isNullString(isPrimary) && ("N".equalsIgnoreCase(isPrimary))) {
            strBuff.append(" user_phones up, ");
        }
        strBuff.append(" users appu1, users appu2, users appu3, users appu4 , user_geographies ug,geographical_domains gd");
        /*strBuff.append(" ,voms_batches vb,voms_products vp ");*/
        strBuff.append(" WHERE ");
        if (!BTSLUtil.isNullString(transferID)) {
            strBuff.append(" ct.transfer_id = ? AND ");
            strBuff.append(" (u.user_id=ct.to_user_id OR u.user_id=ct.from_user_id) AND ");
        } else if (!BTSLUtil.isNullString(userCode)) {
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
            strBuff.append(" u.status IN (" + PretupsBL.userStatusIn() + ",'SR')" + "AND ");
        } else {
            strBuff.append(" ct.transfer_date >= ? AND ct.transfer_date <= ? AND ct.product_type=? AND ");
            strBuff.append(" ct.transfer_category = ? AND ");
            if (!PretupsI.ALL.equals(status) && (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(transferTypeCode) || PretupsI.TRANSFER_CATEGORY_TRANSFER
                .equals(transferCategory))) {
                strBuff.append(" ct.status = ? AND ");
            }
            if (!PretupsI.ALL.equals(transferTypeCode) && PretupsI.TRANSFER_CATEGORY_SALE.equals(transferCategory)) {
                strBuff.append("  ct.transfer_sub_type= ? AND ");
            }

            if (PretupsI.ALL.equals(transferTypeCode)) {
                strBuff.append(" ( (u.user_id=ct.to_user_id AND ct.to_user_id=?) OR (u.user_id=ct.from_user_id AND ct.from_user_id = ?)) AND ");
            } else if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(transferTypeCode)) {
                strBuff.append(" u.user_id=ct.to_user_id AND ct.to_user_id=? AND ");
            } else {
                strBuff.append(" u.user_id=ct.from_user_id AND ct.from_user_id = ? AND ");
            }
            strBuff.append(" u.status IN (" + PretupsBL.userStatusIn() + ",'SR')" + "AND ");
        }
        strBuff.append(" ct.type = 'O2C' ");
        strBuff.append(" AND ct.first_approved_by = appu1.user_id(+)  AND ct.second_approved_by = appu2.user_id(+) ");
        strBuff.append(" AND ct.third_approved_by = appu3.user_id(+) AND ct.cancelled_by = appu4.user_id(+)  ");
        strBuff.append(" AND ug.user_id=u.user_id AND gd.grph_domain_code=ug.grph_domain_code ");
        strBuff.append(" AND LKP.lookup_type=? AND LKP.lookup_code=ct.transfer_sub_type ");
        strBuff.append(" AND ct.transfer_id=cti.transfer_id ");
        /*strBuff.append(" AND ct.transfer_id=vb.ext_txn_no(+) ");
        strBuff.append(" AND vb.product_id=vp.product_id(+) ");*/
        strBuff.append(" ORDER BY  ct.created_on DESC,ct.transfer_sub_type ");
	        
	    return strBuff;
	}
	
	@Override
	public StringBuilder getEmailIdOfApproverQry(){
		StringBuilder selQuery = new StringBuilder("SELECT U.user_id,U.email,U.msisdn FROM users U,user_roles UR,user_geographies UG, categories C,geographical_domains GDD ");
          selQuery.append("WHERE UR.role_code=? AND U.user_id=UR.user_id AND ");
          selQuery.append("U.user_type='OPERATOR' AND U.email is not NULL AND ");
          selQuery.append("U.user_id=UG.user_id AND U.CATEGORY_CODE=C.CATEGORY_CODE AND UG.GRPH_DOMAIN_CODE=GDD.GRPH_DOMAIN_CODE AND C.GRPH_DOMAIN_TYPE=GDD.GRPH_DOMAIN_TYPE AND ");
          selQuery.append("UG.grph_domain_code in( ");
          selQuery.append("select GD.grph_domain_code from geographical_domains GD where GD.status in ('Y','S') ");
          selQuery.append("connect by prior GD.parent_grph_domain_code=GD.grph_domain_code ");
          selQuery.append("start with GD.grph_domain_code IN (SELECT grph_domain_code FROM user_geographies ");
          selQuery.append(" ) ) ");
          selQuery.append(" UNION ");
          selQuery.append("SELECT distinct u.user_id, u.email,u.msisdn FROM category_roles cr,roles rr, user_roles UR,users u, ");
          selQuery.append("group_roles GR, categories c, domains d,user_geographies ug,geographical_domains gdd ");
          selQuery.append("WHERE rr.status <> 'N' ");
          selQuery.append("AND UR.user_id =u.user_id ");
          selQuery.append("AND UR.role_code=cr.role_code ");
          selQuery.append("AND rr.group_role = 'Y'  ");
          selQuery.append("AND cr.role_code = rr.role_code ");
          selQuery.append("AND GR.GROUP_ROLE_CODE = UR.role_code ");
          selQuery.append("AND c.category_code=u.category_code ");
          selQuery.append("and d.DOMAIN_CODE=c.DOMAIN_CODE ");
          selQuery.append("and d.DOMAIN_TYPE_CODE=rr.DOMAIN_TYPE ");
          selQuery.append("and GR.role_code= ? ");
          selQuery.append("AND u.user_type = 'OPERATOR' ");
          selQuery.append("AND u.email IS NOT NULL ");
          selQuery.append("AND u.user_id = ug.user_id ");
          selQuery.append("AND ug.grph_domain_code = gdd.grph_domain_code ");
          selQuery.append("AND ug.grph_domain_code IN ( ");
          selQuery.append("SELECT  gd.grph_domain_code ");
          selQuery.append("FROM geographical_domains gd ");
          selQuery.append("WHERE gd.status IN ('Y', 'S') ");
          selQuery.append("CONNECT BY PRIOR gd.parent_grph_domain_code = gd.grph_domain_code ");
          selQuery.append("START WITH gd.grph_domain_code IN (SELECT grph_domain_code ");
          selQuery.append("FROM user_geographies ");
          selQuery.append("	 )) ");
          return selQuery;
	}
	
	@Override
	public StringBuilder getEmailIdOfApproversQry(String parentUserId){
		StringBuilder selQuery = new StringBuilder("SELECT U.email ,U.msisdn FROM USERS U, ");
		selQuery.append(" USER_ROLES UR");
		selQuery.append(" WHERE U.USER_ID = UR.USER_ID AND");
		selQuery.append(" ( U.USER_ID = ? OR U.PARENT_ID = ?) AND");
		selQuery.append(" UR.ROLE_CODE = ? ");
		
		
          return selQuery;
	}
	
	@Override
	public StringBuilder getEmailIdOfRoleApproversQry(){
		StringBuilder selQuery = new StringBuilder("  SELECT U1.USER_ID,U1.email ,U1.msisdn ");
		selQuery.append(" FROM USERS U1 WHERE U1.USER_ID=? UNION ");
		selQuery.append(" SELECT U.USER_ID,U.email ,U.msisdn FROM USERS U ");  
		selQuery.append(" JOIN USER_ROLES UR ON UR.ROLE_CODE = ? ");
		selQuery.append(" AND u.USER_ID=ur.USER_ID AND u.parent_id = ? AND U.USER_TYPE= ? ");
		return selQuery;
	}
	@Override
	public PreparedStatement loadLastXC2STransfersServiceWiseQry(Connection con,
			String serviceType,int noDays, StringBuilder services,Date differenceDate,String userId,int noLastTxn)throws SQLException
	{
		//local_index_implemented
		String methodName ="loadLastXC2STransfersServiceWise";	
		StringBuilder strBuff = new StringBuilder(" SELECT transfer_id, transfer_date_time, transfer_status,net_payable_amount, sender_post_balance, receiver_msisdn, created_on, service, name, type, statusname, error_code from ( ");
    	strBuff.append("  select CS.transfer_id, CS.transfer_date_time, CS.transfer_status, CS.transfer_value net_payable_amount, CS.sender_post_balance , CS.receiver_msisdn, ");
        strBuff.append("  CS.transfer_date_time created_on, CS.service_type service, ST.name, 'C2S' type, KV.value statusname, CS.error_code ");
        strBuff.append("  from C2S_TRANSFERS CS,key_values KV,SERVICE_TYPE ST where ");
        if(noDays!=0 )
        	strBuff.append(" CS.transfer_date >= ? AND ");
        strBuff.append(" CS.active_user_id = ? ");
        strBuff.append("  AND CS.transfer_status=KV.key(+) AND KV.type(+)=? AND CS.service_type=ST.service_type" );
        if(PretupsI.ALL.equals(serviceType))
        	strBuff.append(" AND CS.service_type=CS.service_type ");
        else
        	strBuff.append(" AND CS.service_type in ("+services+") ");
        strBuff.append(" ORDER BY created_on desc)  WHERE  rowNum<=? ");
        
		final String sqlSelect = strBuff.toString();
		 LogFactory.printLog(methodName, sqlSelect, log);
        int i = 0;
		PreparedStatement pstmt = con.prepareStatement(sqlSelect);
        if(noDays!=0 )
        	pstmt.setDate(++i,BTSLUtil.getSQLDateFromUtilDate(differenceDate));
        pstmt.setString(++i,userId);
        pstmt.setString(++i,PretupsI.KEY_VALUE_C2C_STATUS);
        pstmt.setInt(++i,noLastTxn);
        
        return pstmt;
	}
	
	@Override
	public PreparedStatement loadLastXC2CTransfersServiceWiseQry(Connection con,
			String serviceType,int noDays, StringBuilder services,Date differenceDate,String userId,int noLastTxn,String[] aa,String c2cInOut )throws SQLException
	{
		String methodName ="loadLastXC2CTransfersServiceWiseQry";	
		StringBuilder strBuff = new StringBuilder(" SELECT transfer_id,CLOSE_DATE,status, net_payable_amount, to_msisdn, msisdn, created_on, service, name, type, transfer_sub_type, sender_previous_stock, receiver_previous_stock, approved_quantity, statusname,MODIFIED_ON FROM ( ");
        strBuff.append(" SELECT CT.transfer_id, CT.CLOSE_DATE, CT.status , CT.net_payable_amount, CT.to_msisdn, CT.msisdn, CT.created_on, CT.transfer_type service, LK.lookup_name name, ");
        strBuff.append(" CT.type, CT.transfer_sub_type, CTI.sender_previous_stock, CTI.receiver_previous_stock , CTI.approved_quantity, KV2.value statusname, CT.MODIFIED_ON  FROM LOOKUPS LK,KEY_VALUES KV2 ,CHANNEL_TRANSFERS CT, CHANNEL_TRANSFERS_ITEMS CTI WHERE  ");
        if("OUT".equals(c2cInOut))
        	strBuff.append(" CT.from_user_id = ? ");
        if("IN".equals(c2cInOut))
        	strBuff.append(" CT.to_user_id  = ? ");
        if(noDays!=0 )
        	strBuff.append(" AND CT.transfer_date >= ? ");
        if(aa.length==2)
        	strBuff.append(" AND CT.transfer_sub_type  = ? ");
        else if(aa.length==3)
        	strBuff.append(" AND CT.transfer_sub_type IN (?,?) ");
        strBuff.append("  AND CTI.transfer_id = CT.transfer_id AND LK.lookup_type=? AND CT.type = ? ");
        strBuff.append("  AND LK.lookup_code=CT.TRANSFER_SUB_TYPE AND CT.status=KV2.key(+) AND KV2.type(+) =? ");
        
        
        strBuff.append(" ORDER BY created_on desc)  WHERE  rowNum<=? ");
        String sqlSelect1 = strBuff.toString();
        LogFactory.printLog(methodName, sqlSelect1, log);
        int i=0;
    	PreparedStatement pstmt1 = con.prepareStatement(sqlSelect1);
        pstmt1.setString(++i,userId);
        if(noDays!=0 )
        	pstmt1.setDate(++i,BTSLUtil.getSQLDateFromUtilDate(differenceDate));
        if(aa.length==2)
        	pstmt1.setString(++i,aa[1]);
        else if(aa.length==3)
        {
        	pstmt1.setString(++i,aa[1]);
        	pstmt1.setString(++i,aa[2]);
        }
        pstmt1.setString(++i,PretupsI.TRANSFER_TYPE);
        pstmt1.setString(++i,PretupsI.TRANSFER_TYPE_C2C);
        pstmt1.setString(++i,PretupsI.CHANNEL_TRANSFER_STATUS);
        pstmt1.setInt(++i,noLastTxn);
        
        return pstmt1;
	}
	
	
	@Override
	public PreparedStatement loadLastXO2CTransfersServiceWiseQry(Connection con,
			String serviceType,int noDays, StringBuilder services,Date differenceDate,String userId,int noLastTxn,String[] aa)throws SQLException
	{
		String methodName ="loadLastXO2CTransfersServiceWiseQry";	
		StringBuilder strBuff = new StringBuilder(" SELECT transfer_id,CLOSE_DATE, status, approved_quantity, msisdn, to_msisdn, created_on, service, name, type, transfer_sub_type, sender_previous_stock, receiver_previous_stock, statusname, MODIFIED_ON FROM ( ");
        strBuff.append(" SELECT CT.transfer_id, CT.CLOSE_DATE, CT.status, CT.transfer_sub_type,CTI.approved_quantity, CTI.sender_previous_stock, CTI.receiver_previous_stock, CT.msisdn, CT.to_msisdn, CT.created_on, CT.transfer_type service, LK.lookup_name name, ");
        strBuff.append(" CT.type, KV2.value statusname, CT.MODIFIED_ON FROM LOOKUPS LK,KEY_VALUES KV2 ,CHANNEL_TRANSFERS CT, CHANNEL_TRANSFERS_ITEMS CTI WHERE (CT.to_user_id = ? OR CT.from_user_id = ? ) ");
        if(noDays!=0 )
        	strBuff.append(" AND CT.transfer_date >= ? ");
        if(aa.length==2)
        	strBuff.append(" AND CT.transfer_sub_type  = ? ");
        else if(aa.length==3)
        	strBuff.append(" AND CT.transfer_sub_type IN (?,?) ");
        strBuff.append("  AND CTI.transfer_id = CT.transfer_id AND LK.lookup_type=? AND CT.type = ? ");
        strBuff.append("  AND LK.lookup_code=CT.TRANSFER_SUB_TYPE AND CT.status=KV2.key(+) AND KV2.type(+) =? ");
        strBuff.append(" ORDER BY created_on desc )  WHERE  rowNum<=? ");
        String sqlSelect2 = strBuff.toString();
        LogFactory.printLog(methodName, sqlSelect2, log);
        int i = 0;
        PreparedStatement pstmt2 =  con.prepareStatement(sqlSelect2);
        pstmt2.setString(++i,userId);
        pstmt2.setString(++i,userId);
        if(noDays!=0 )
        	pstmt2.setDate(++i,BTSLUtil.getSQLDateFromUtilDate(differenceDate));
        if(aa.length==2)
        	pstmt2.setString(++i,aa[1]);
        else if(aa.length==3)
        {
        	pstmt2.setString(++i,aa[1]);
        	pstmt2.setString(++i,aa[2]);
        }
        pstmt2.setString(++i,PretupsI.TRANSFER_TYPE);
        pstmt2.setString(++i,PretupsI.TRANSFER_TYPE_O2C);
        pstmt2.setString(++i,PretupsI.CHANNEL_TRANSFER_STATUS);
        pstmt2.setInt(++i,noLastTxn);
       
        return pstmt2;
	}
	@Override
	public PreparedStatement loadLastXO2CTransferDetailsQry(Connection con,String userId,int lastNoOfTxn,Date differenceDate,String txnType,String txnSubType)throws SQLException
	{
		String methodName ="loadLastXO2CTransferDetailsQry";	
		StringBuilder strBuff = new StringBuilder(" SELECT transfer_id,CLOSE_DATE, status, approved_quantity, msisdn, to_msisdn, created_on, service, name, type, transfer_sub_type, sender_previous_stock, receiver_previous_stock, statusname, MODIFIED_ON FROM ( ");
		strBuff.append(" SELECT CT.transfer_id, CT.CLOSE_DATE, CT.status, CT.transfer_sub_type,CTI.approved_quantity, CTI.sender_previous_stock, CTI.receiver_previous_stock, CT.msisdn, CT.to_msisdn, CT.created_on, CT.transfer_type service, LK.lookup_name name, ");
		strBuff.append(" CT.type, KV2.value statusname, CT.MODIFIED_ON FROM LOOKUPS LK,KEY_VALUES KV2 ,CHANNEL_TRANSFERS CT, CHANNEL_TRANSFERS_ITEMS CTI WHERE (CT.to_user_id = ? OR CT.from_user_id = ? ) ");
		if(lastNoOfTxn!=0 )
			strBuff.append(" AND CT.transfer_date >= ? ");
		
		if(PretupsI.TRANSFER_TYPE_FOC.equalsIgnoreCase(txnType) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equalsIgnoreCase(txnSubType))
			strBuff.append(" AND CT.TXN_WALLET  = ? ");
		
		strBuff.append(" AND CT.transfer_category  = ? ");
		
		if(txnSubType!=null)
			strBuff.append(" AND CT.transfer_sub_type  = ? ");
		
		strBuff.append("  AND CTI.transfer_id = CT.transfer_id AND LK.lookup_type=? AND CT.type = ? ");
		strBuff.append("  AND LK.lookup_code=CT.TRANSFER_SUB_TYPE AND CT.status=KV2.key(+) AND KV2.type(+) =? ");
		strBuff.append(" ORDER BY created_on desc )  WHERE  rowNum<=? ");
		String sqlSelect2 = strBuff.toString();
		LogFactory.printLog(methodName, sqlSelect2, log);
		int i = 0;
		PreparedStatement pstmt2 =  con.prepareStatement(sqlSelect2);
		pstmt2.setString(++i,userId);
		pstmt2.setString(++i,userId);
		if(lastNoOfTxn!=0 )
			pstmt2.setDate(++i,BTSLUtil.getSQLDateFromUtilDate(differenceDate));
		
		if(PretupsI.TRANSFER_TYPE_FOC.equalsIgnoreCase(txnType) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_WITHDRAW.equalsIgnoreCase(txnSubType))
			pstmt2.setString(++i,PretupsI.TRANSFER_TYPE_FOC);
		
		if(PretupsI.TRANSFER_TYPE_FOC.equalsIgnoreCase(txnType) && PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equalsIgnoreCase(txnSubType))
			pstmt2.setString(++i,PretupsI.SERVICE_TYPE_CHNL_TRANSFER);
		else
			pstmt2.setString(++i,PretupsI.TRANSFER_TYPE_SALE);
		if(txnSubType!=null)
			pstmt2.setString(++i,txnSubType);

		
		pstmt2.setString(++i,PretupsI.TRANSFER_TYPE);
		pstmt2.setString(++i,PretupsI.TRANSFER_TYPE_O2C);
		pstmt2.setString(++i,PretupsI.CHANNEL_TRANSFER_STATUS);
		pstmt2.setInt(++i,lastNoOfTxn);
		return pstmt2;
	}
	
	@Override
	public PreparedStatement loadLastXC2CTransferDetailsQry(Connection con,String userId,int lastNoOfTxn,Date differenceDate,String txnType,String txnSubType,String c2cInOut)throws SQLException{

		String methodName ="loadLastXC2CTransferDetailsQry";	
		StringBuilder strBuff = new StringBuilder(" SELECT transfer_id,CLOSE_DATE,status, net_payable_amount, to_msisdn, msisdn, created_on, service, name, type, transfer_sub_type, sender_previous_stock, receiver_previous_stock, approved_quantity, statusname,MODIFIED_ON FROM ( ");
		strBuff.append(" SELECT CT.transfer_id, CT.CLOSE_DATE, CT.status , CT.net_payable_amount, CT.to_msisdn, CT.msisdn, CT.created_on, CT.transfer_type service, LK.lookup_name name, ");
		strBuff.append(" CT.type, CT.transfer_sub_type, CTI.sender_previous_stock, CTI.receiver_previous_stock , CTI.approved_quantity, KV2.value statusname, CT.MODIFIED_ON  FROM LOOKUPS LK,KEY_VALUES KV2 ,CHANNEL_TRANSFERS CT, CHANNEL_TRANSFERS_ITEMS CTI WHERE  ");
		if("OUT".equals(c2cInOut))
			strBuff.append(" CT.from_user_id = ? ");
		if("IN".equals(c2cInOut))
			strBuff.append(" CT.to_user_id  = ? ");
		if(differenceDate != null )
			strBuff.append(" AND CT.transfer_date >= ? ");
		if(txnSubType != null)
			strBuff.append(" AND CT.transfer_sub_type  = ? ");
		strBuff.append("  AND CTI.transfer_id = CT.transfer_id AND LK.lookup_type=? AND CT.type = ? ");
		strBuff.append("  AND LK.lookup_code=CT.TRANSFER_SUB_TYPE AND CT.status=KV2.key(+) AND KV2.type(+) =? ");
		strBuff.append(" ORDER BY created_on desc)  WHERE  rowNum<=? ");
		String sqlSelect1 = strBuff.toString();
		LogFactory.printLog(methodName, sqlSelect1, log);
		int i=0;
		PreparedStatement pstmt1 = con.prepareStatement(sqlSelect1);
		pstmt1.setString(++i,userId);
		if(lastNoOfTxn != 0 )
			pstmt1.setDate(++i,BTSLUtil.getSQLDateFromUtilDate(differenceDate));
		if(txnSubType != null)
			pstmt1.setString(++i,txnSubType);
		pstmt1.setString(++i,PretupsI.TRANSFER_TYPE);
		pstmt1.setString(++i,PretupsI.TRANSFER_TYPE_C2C);
		pstmt1.setString(++i,PretupsI.CHANNEL_TRANSFER_STATUS);
		pstmt1.setInt(++i,lastNoOfTxn);

		return pstmt1;

	}
	@Override
	public PreparedStatement loadLastXC2STransferDetailsQry(Connection con,String userId,int lastNoOfTxn,Date differenceDate,String txnType,String txnSubType)throws SQLException{
		//local_index_implemented
		String methodName ="loadLastXC2STransfersServiceWise";	
		StringBuilder strBuff = new StringBuilder(" SELECT transfer_id, transfer_date_time, transfer_status,net_payable_amount, sender_post_balance, receiver_msisdn, created_on, service, name, type, statusname, error_code from ( ");
    	strBuff.append("  select CS.transfer_id, CS.transfer_date_time, CS.transfer_status, CS.transfer_value net_payable_amount, CS.sender_post_balance , CS.receiver_msisdn, ");
        strBuff.append("  CS.transfer_date_time created_on, CS.service_type service, ST.name, 'C2S' type, KV.value statusname, CS.error_code ");
        strBuff.append("  from C2S_TRANSFERS CS,key_values KV,SERVICE_TYPE ST where ");
        if(lastNoOfTxn!=0 )
        	strBuff.append(" CS.transfer_date >= ? AND ");
        strBuff.append(" CS.active_user_id = ? ");
        strBuff.append("  AND CS.transfer_status=KV.key(+) AND KV.type(+)=? AND CS.service_type=ST.service_type" );
        strBuff.append(" AND CS.service_type=CS.service_type ");
    	strBuff.append(" AND CS.txn_type = ? ");
        strBuff.append(" ORDER BY created_on desc)  WHERE  rowNum<=? ");
        
		final String sqlSelect = strBuff.toString();
		 LogFactory.printLog(methodName, sqlSelect, log);
        int i = 0;
		PreparedStatement pstmt = con.prepareStatement(sqlSelect);
        if(lastNoOfTxn!=0 )
        	pstmt.setDate(++i,BTSLUtil.getSQLDateFromUtilDate(differenceDate));
        pstmt.setString(++i,userId);
        pstmt.setString(++i,PretupsI.KEY_VALUE_C2C_STATUS);
        pstmt.setString(++i,txnSubType);
        pstmt.setInt(++i,lastNoOfTxn);
        
        return pstmt;
		
	}

	@Override
	public StringBuilder loadChannelTxnDetailsQry(Connection con, String pType,String userType) throws SQLException {
		StringBuilder selectQueryBuff =new StringBuilder();
		selectQueryBuff.append("SELECT CT.TRANSFER_ID,CT.NETWORK_CODE,CT.REFERENCE_NO,CT.TRANSFER_MRP,CT.CHANNEL_USER_REMARKS,CT.STATUS,CT.TYPE,CT.GRPH_DOMAIN_CODE, ");
		selectQueryBuff.append("CT.CLOSE_DATE,CT.TXN_WALLET,CT.TO_MSISDN,TRANSFER_SUB_TYPE,CT.PRODUCT_TYPE,CT.FROM_USER_ID,CT.TO_USER_ID,CTI.PRODUCT_CODE ");
		selectQueryBuff.append("FROM CHANNEL_TRANSFERS CT,CHANNEL_TRANSFERS_ITEMS CTI WHERE ");
		if("EXT".equals(pType))
		{
			selectQueryBuff.append("CT.REFERENCE_NO = ? ");
		}
		else
		{
			selectQueryBuff.append("CT.TRANSFER_ID = ? ");
		}
		selectQueryBuff.append(" and CT.TRANSFER_ID=CTI.TRANSFER_ID and CT.NETWORK_CODE=? ");
		if(PretupsI.CHANNEL_USER_TYPE.equals(userType))
		{
			selectQueryBuff.append(" and (from_user_id in ( SELECT u.user_id FROM users u WHERE u.user_type = ? AND NOT u.status IN ('N', 'C') ");
			selectQueryBuff.append(" CONNECT BY PRIOR u.user_id = u.parent_id START WITH u.user_id = ? ) OR to_user_id in ( SELECT u.user_id FROM users u WHERE u.user_type = ? AND NOT u.status IN ('N', 'C') ");
			selectQueryBuff.append(" CONNECT BY PRIOR u.user_id = u.parent_id START WITH u.user_id = ?))");
		}
		return selectQueryBuff;
	}

	@Override
	public String loadChannelTransferDetailQry()
	{
		 final StringBuilder strBuff = new StringBuilder(" SELECT ct.transfer_id, ct.first_level_approved_quantity, ");
	        strBuff.append(" ct.second_level_approved_quantity, ct.third_level_approved_quantity, ct.network_code, ct.network_code_for, ");
	        strBuff.append(" ct.grph_domain_code, ct.domain_code, ct.sender_category_code, ct.sender_grade_code, ");
	        strBuff.append(" ct.receiver_grade_code, ct.from_user_id, ct.to_user_id, ct.transfer_date, ct.reference_no, ");
	        strBuff.append(" ct.ext_txn_no, ct.ext_txn_date,ct.transfer_category,ct.commission_profile_set_id, ");
	        strBuff.append(" ct.commission_profile_ver, ct.requested_quantity, ct.channel_user_remarks, ");
	        strBuff.append(" ct.first_approver_remarks, ct.second_approver_remarks, ct.third_approver_remarks, ");
	        strBuff.append(" ct.first_approved_by, ct.first_approved_on, ct.second_approved_by, ct.second_approved_on, ");
	        strBuff.append(" ct.third_approved_by, ct.third_approved_on, ct.cancelled_by, ct.cancelled_on, ");
	        strBuff.append(" ct.transfer_initiated_by, ct.modified_by, ct.modified_on, ct.status, ct.type, ");
	        strBuff.append(" ct.transfer_mrp, ct.first_approver_limit, ct.second_approver_limit,  ");
	        strBuff.append(" ct.payable_amount, ct.net_payable_amount, ct.batch_no, ct.batch_date, ct.pmt_inst_type, ct.pmt_inst_no, ");
	        strBuff.append(" ct.pmt_inst_date, ct.pmt_inst_amount, ct.sender_txn_profile, ct.receiver_txn_profile, ct.total_tax1, ct.total_tax2,  ");
	        strBuff.append(" ct.total_tax3, ct.source, ct.receiver_category_code, ct.request_gateway_code, ct.request_gateway_type, ct.pmt_inst_source ,");
	        strBuff.append(" ct.product_type , ct.sms_default_lang,ct.sms_second_lang, d.domain_name , g.grph_domain_name, c.category_name , cg.grade_name, u.user_name,u.address1,u.address2,u.city,u.state,u.country,cps.comm_profile_set_name,tp.profile_name, ");
	        strBuff.append(" appu1.user_name firstapprovedby,appu2.user_name secondapprovedby,appu3.user_name thirdapprovedby, ");
	        strBuff.append(" appu4.user_name cancelledby,appu5.user_name initatedby , u.msisdn ,ct.msisdn from_msisdn,ct.to_msisdn to_msisdn, u.external_code , appu6.user_name fromusername ,ct.transfer_type,ct.transfer_sub_type,tp2.profile_name sender_txn_profile_name, ");
	        strBuff.append(" cti.commision_quantity,cti.sender_debit_quantity,cti.receiver_credit_quantity,");
	        strBuff.append(" ct.txn_wallet,ct.control_transfer,ct.to_grph_domain_code,ct.to_domain_code,ct.created_on,ct.created_by,ct.active_user_id,");
	        strBuff.append(" cti.user_wallet, ct.stock_updated,ct.dual_comm_type, ct.info1,ct.PMT_INST_TYPE ");
	        strBuff.append(" FROM ");
	        strBuff.append(" channel_transfers ct, geographical_domains g, categories c , users u , commission_profile_set cps, domains d , ");
	        strBuff.append(" transfer_profile tp ,transfer_profile tp2, users appu1, users appu2 ,users appu3 , users appu4 ,users appu5 ,channel_grades cg ,users appu6 ,channel_transfers_items cti");
	        strBuff.append(" WHERE ");
	        strBuff.append(" ct.transfer_id = ? AND ct.transfer_id=cti.transfer_id AND ct.status = ? AND ");
	        strBuff.append(" ct.domain_code = d.domain_code AND ct.grph_domain_code = g.grph_domain_code AND ct.receiver_category_code = c.category_code AND ");
	        strBuff.append(" ct.to_user_id  = u.user_id AND ct.receiver_grade_code = cg.grade_code  AND ct.receiver_txn_profile = tp.profile_id AND ct.sender_txn_profile = tp2.profile_id(+) AND ");
	        strBuff.append(" ct.commission_profile_set_id = cps.comm_profile_set_id AND ");
	        strBuff.append(" ct.first_approved_by = appu1.user_id(+)  AND ct.second_approved_by = appu2.user_id(+) AND ct.third_approved_by = appu3.user_id(+) AND ");
	        strBuff.append(" ct.cancelled_by = appu4.user_id(+) AND ct.transfer_initiated_by = appu5.user_id(+) AND ");
	        strBuff.append(" ct.from_user_id = appu6.user_id(+) ");

	        return strBuff.toString();
	}

	@Override
	public String loadChannelTransferDetailTcpQry()
	{
		 final StringBuilder strBuff = new StringBuilder(" SELECT ct.transfer_id, ct.first_level_approved_quantity, ");
	        strBuff.append(" ct.second_level_approved_quantity, ct.third_level_approved_quantity, ct.network_code, ct.network_code_for, ");
	        strBuff.append(" ct.grph_domain_code, ct.domain_code, ct.sender_category_code, ct.sender_grade_code, ");
	        strBuff.append(" ct.receiver_grade_code, ct.from_user_id, ct.to_user_id, ct.transfer_date, ct.reference_no, ");
	        strBuff.append(" ct.ext_txn_no, ct.ext_txn_date,ct.transfer_category,ct.commission_profile_set_id, ");
	        strBuff.append(" ct.commission_profile_ver, ct.requested_quantity, ct.channel_user_remarks, ");
	        strBuff.append(" ct.first_approver_remarks, ct.second_approver_remarks, ct.third_approver_remarks, ");
	        strBuff.append(" ct.first_approved_by, ct.first_approved_on, ct.second_approved_by, ct.second_approved_on, ");
	        strBuff.append(" ct.third_approved_by, ct.third_approved_on, ct.cancelled_by, ct.cancelled_on, ");
	        strBuff.append(" ct.transfer_initiated_by, ct.modified_by, ct.modified_on, ct.status, ct.type, ");
	        strBuff.append(" ct.transfer_mrp, ct.first_approver_limit, ct.second_approver_limit,  ");
	        strBuff.append(" ct.payable_amount, ct.net_payable_amount, ct.batch_no, ct.batch_date, ct.pmt_inst_type, ct.pmt_inst_no, ");
	        strBuff.append(" ct.pmt_inst_date, ct.pmt_inst_amount, ct.sender_txn_profile, ct.receiver_txn_profile, ct.total_tax1, ct.total_tax2,  ");
	        strBuff.append(" ct.total_tax3, ct.source, ct.receiver_category_code, ct.request_gateway_code, ct.request_gateway_type, ct.pmt_inst_source ,");
	        strBuff.append(" ct.product_type , ct.sms_default_lang,ct.sms_second_lang, d.domain_name , g.grph_domain_name, c.category_name , cg.grade_name, u.user_name,u.address1,u.address2,u.city,u.state,u.country,cps.comm_profile_set_name, ");
	        strBuff.append(" appu1.user_name firstapprovedby,appu2.user_name secondapprovedby,appu3.user_name thirdapprovedby, ");
	        strBuff.append(" appu4.user_name cancelledby,appu5.user_name initatedby , u.msisdn ,ct.msisdn from_msisdn,ct.to_msisdn to_msisdn, u.external_code , appu6.user_name fromusername ,ct.transfer_type,ct.transfer_sub_type, ");
	        strBuff.append(" cti.commision_quantity,cti.sender_debit_quantity,cti.receiver_credit_quantity,");
	        strBuff.append(" ct.txn_wallet,ct.control_transfer,ct.to_grph_domain_code,ct.to_domain_code,ct.created_on,ct.created_by,ct.active_user_id,");
	        strBuff.append(" cti.user_wallet, ct.stock_updated,ct.dual_comm_type, ct.info1,ct.PMT_INST_TYPE ");
	        strBuff.append(" FROM ");
	        strBuff.append(" channel_transfers ct, geographical_domains g, categories c , users u , commission_profile_set cps, domains d , ");
	        strBuff.append("  users appu1, users appu2 ,users appu3 , users appu4 ,users appu5 ,channel_grades cg ,users appu6 ,channel_transfers_items cti");
	        strBuff.append(" WHERE ");
	        strBuff.append(" ct.transfer_id = ? AND ct.transfer_id=cti.transfer_id AND ct.status = ? AND ");
	        strBuff.append(" ct.domain_code = d.domain_code AND ct.grph_domain_code = g.grph_domain_code AND ct.receiver_category_code = c.category_code AND ");
	        strBuff.append(" ct.to_user_id  = u.user_id AND ct.receiver_grade_code = cg.grade_code    AND ");
	        strBuff.append(" ct.commission_profile_set_id = cps.comm_profile_set_id AND ");
	        strBuff.append(" ct.first_approved_by = appu1.user_id(+)  AND ct.second_approved_by = appu2.user_id(+) AND ct.third_approved_by = appu3.user_id(+) AND ");
	        strBuff.append(" ct.cancelled_by = appu4.user_id(+) AND ct.transfer_initiated_by = appu5.user_id(+) AND ");
	        strBuff.append(" ct.from_user_id = appu6.user_id(+) ");

	        return strBuff.toString();
	}

	
	@Override
	public StringBuilder loadVoucherDetailsForTransactionIdQry()
	{
		final StringBuilder strBuff = new StringBuilder(" SELECT vb.batch_no,  vp.product_name, vb.batch_type, vb.total_no_of_vouchers, vb.from_serial_no, vb.to_serial_no, vp.VOUCHER_SEGMENT, vp.CATEGORY_ID, vc.VOUCHER_TYPE, vc.MRP  ");
		strBuff.append(" FROM channel_transfers ct, voms_batches vb, voms_products vp, VOMS_CATEGORIES vc ");
		strBuff.append(" WHERE ct.transfer_id = ? AND ct.transfer_id = vb.ext_txn_no(+) AND vb.product_id = vp.product_id(+) AND vc.CATEGORY_ID=vp.CATEGORY_ID ");
        return strBuff;
	}
	
	@Override
	public StringBuilder loadVoucherDetailsForTransactionIdChannelQry()
	{
		final StringBuilder strBuff = new StringBuilder(" SELECT requested_quantity,from_serial_no,to_serial_no ");
		strBuff.append(" FROM channel_voucher_items ");
		strBuff.append(" WHERE transfer_id = ? ");
        return strBuff;
	}
	
	@Override
	public String loadChannelC2CVoucherTransfersListQryPagination(String reveiverCategoryCode, String geoCode, String domainCode, String searchParam, String approvalLevel,String pageNumber,String entriesPerPage,String userNameSearch) {
		
		final StringBuilder strBuff = new StringBuilder(" SELECT ct.approval_doc_file_path, ct.transfer_id, ct.transfer_date, ct.type,ct.transfer_sub_type,ct.transfer_type, ");
        strBuff.append(" ct.payable_amount, ct.net_payable_amount,ct.ext_txn_no, ct.ext_txn_date,d.domain_name , g.grph_domain_name, ct.dual_comm_type, ");
        strBuff.append(" c.category_name,ct.status , u.user_name, ct.transfer_mrp,ct.reference_no,ct.to_user_id,cti.commision_quantity,cti.sender_debit_quantity,cti.receiver_credit_quantity, ");
        strBuff.append(" ct.first_level_approved_quantity, ct.second_level_approved_quantity, ct.third_level_approved_quantity,ct.product_type, ct.receiver_txn_profile, ");
        strBuff.append(" cti.user_wallet, ct.COMMISSION_PROFILE_SET_ID, ct.COMMISSION_PROFILE_VER , ct.pmt_inst_type ,ct.to_msisdn,ct.transfer_initiated_by");
        if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(approvalLevel)) {
        	strBuff.append(" , app1.user_name first_app_name,ct.first_approved_on ");
        } else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(approvalLevel)) {
        	strBuff.append(" , app1.user_name first_app_name,ct.first_approved_on ");
        	strBuff.append(" , app2.user_name second_app_name, ct.second_approved_on ");
        }
        //strBuff.append(" FROM channel_transfers ct , domains d , geographical_domains g ,categories c,users u, users app1,users app2, ");
        strBuff.append(" FROM channel_transfers ct , domains d , geographical_domains g ,categories c,users u,  ");
        if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(approvalLevel)) {
        	strBuff.append(" users app1, ");
        } else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(approvalLevel)) {
        	strBuff.append(" users app1,users app2,");
        	
        }
        strBuff.append(" users su,channel_transfers_items cti ");
        strBuff.append(" WHERE ct.type='C2C' AND ct.from_user_id= ? ");
        strBuff.append(" AND ct.network_code = ? AND ct.network_code_for = ? AND ct.domain_code = d.domain_code AND ct.transfer_sub_type = 'V'  ");
        strBuff.append(" AND ct.grph_domain_code = g.grph_domain_code ");
        if (!BTSLUtil.isNullString(reveiverCategoryCode) && !reveiverCategoryCode.equalsIgnoreCase(PretupsI.ALL)) {
            strBuff.append(" AND ct.receiver_category_code=? ");
        }
        if (!BTSLUtil.isNullString(userNameSearch)) {
            strBuff.append(" AND u.user_name LIKE '"+userNameSearch +"%'");
        }
        strBuff.append(" AND cti.transfer_id = ct.transfer_id ");
        strBuff.append(" AND ct.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains gd1 WHERE status='Y' ");
        strBuff.append(" CONNECT BY PRIOR grph_domain_code=parent_grph_domain_code  ");
        strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code FROM user_geographies WHERE user_id=? ");
        if (!BTSLUtil.isNullString(geoCode)) {
            strBuff.append(" AND grph_domain_code = decode(?,?,grph_domain_code,?)  ");
        }
        strBuff.append(" ) ) AND ct.receiver_category_code = c.category_code AND g.status = 'Y' ");
        strBuff.append(" AND ct.transfer_initiated_by = u.user_id ");
        if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(approvalLevel)) {
        	strBuff.append(" AND ct.first_approved_by = app1.user_id ");
        } else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(approvalLevel)) {
        	strBuff.append(" AND ct.first_approved_by = app1.user_id ");
        	strBuff.append(" AND ct.second_approved_by = app2.user_id ");	
        }
        if (!domainCode.equalsIgnoreCase(PretupsI.ALL)) {
            strBuff.append(" AND trim(ct.domain_code)= ? ");
        }
        if (!PretupsI.ALL.equals(searchParam)) {
            strBuff.append(" AND ct.to_user_id = ?  ");
        }

        strBuff.append(" AND ct.status = ? ");
        
        strBuff.append(" AND ct.to_user_id= su.user_id ");
        strBuff.append(" AND su.OWNER_ID=DECODE(?,'ALL',su.OWNER_ID,?) ");
        strBuff.append(" ORDER BY ct.created_on DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
		return strBuff.toString();
	}

	@Override
	public String loadChannelC2CVoucherTransfersListTransactionIdQry(String reveiverCategoryCode, String geoCode, String domainCode, String searchParam, String approvalLevel) {
		
		final StringBuilder strBuff = new StringBuilder(" SELECT ct.approval_doc_file_path, ct.transfer_id, ct.transfer_date, ct.type,ct.transfer_sub_type,ct.transfer_type, ");
        strBuff.append(" ct.payable_amount, ct.net_payable_amount,ct.ext_txn_no, ct.ext_txn_date,d.domain_name , g.grph_domain_name, ct.dual_comm_type, ");
        strBuff.append(" c.category_name,ct.status , u.user_name, ct.transfer_mrp,ct.reference_no,ct.to_user_id,cti.commision_quantity,cti.sender_debit_quantity,cti.receiver_credit_quantity, ");
        strBuff.append(" ct.first_level_approved_quantity, ct.second_level_approved_quantity, ct.third_level_approved_quantity,ct.product_type, ct.receiver_txn_profile, ");
        strBuff.append(" cti.user_wallet, ct.COMMISSION_PROFILE_SET_ID, ct.COMMISSION_PROFILE_VER , ct.pmt_inst_type ,ct.to_msisdn,ct.transfer_initiated_by");
        if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(approvalLevel)) {
        	strBuff.append(" , app1.user_name first_app_name,ct.first_approved_on ");
        } else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(approvalLevel)) {
        	strBuff.append(" , app1.user_name first_app_name,ct.first_approved_on ");
        	strBuff.append(" , app2.user_name second_app_name, ct.second_approved_on ");
        }
        //strBuff.append(" FROM channel_transfers ct , domains d , geographical_domains g ,categories c,users u, users app1,users app2, ");
        strBuff.append(" FROM channel_transfers ct , domains d , geographical_domains g ,categories c,users u,  ");
        if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(approvalLevel)) {
        	strBuff.append(" users app1, ");
        } else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(approvalLevel)) {
        	strBuff.append(" users app1,users app2,");
        	
        }
        strBuff.append(" users su,channel_transfers_items cti ");
        strBuff.append(" WHERE ct.type='C2C' AND ct.from_user_id= ? ");
        strBuff.append(" AND ct.network_code = ? AND ct.network_code_for = ? AND ct.domain_code = d.domain_code AND ct.transfer_sub_type = 'V'  ");
        strBuff.append(" AND ct.grph_domain_code = g.grph_domain_code ");
        if (!BTSLUtil.isNullString(reveiverCategoryCode) && !reveiverCategoryCode.equalsIgnoreCase(PretupsI.ALL)) {
            strBuff.append(" AND ct.receiver_category_code=? ");
        }
        strBuff.append(" AND cti.transfer_id = ct.transfer_id ");
        strBuff.append(" AND ct.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains gd1 WHERE status='Y' ");
        strBuff.append(" CONNECT BY PRIOR grph_domain_code=parent_grph_domain_code  ");
        strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code FROM user_geographies WHERE user_id=? ");
        if (!BTSLUtil.isNullString(geoCode)) {
            strBuff.append(" AND grph_domain_code = decode(?,?,grph_domain_code,?)  ");
        }
        strBuff.append(" ) ) AND ct.receiver_category_code = c.category_code AND g.status = 'Y' ");
        strBuff.append(" AND ct.transfer_initiated_by = u.user_id ");
        if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(approvalLevel)) {
        	strBuff.append(" AND ct.first_approved_by = app1.user_id ");
        } else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(approvalLevel)) {
        	strBuff.append(" AND ct.first_approved_by = app1.user_id ");
        	strBuff.append(" AND ct.second_approved_by = app2.user_id ");	
        }
        if (!domainCode.equalsIgnoreCase(PretupsI.ALL)) {
            strBuff.append(" AND trim(ct.domain_code)= ? ");
        }
        if (!PretupsI.ALL.equals(searchParam)) {
            strBuff.append(" AND ct.to_user_id = ?  ");
        }

        strBuff.append(" AND ct.status = ? AND ct.transfer_id = ? ");
        
        strBuff.append(" AND ct.to_user_id= su.user_id ");
        strBuff.append(" AND su.OWNER_ID=DECODE(?,'ALL',su.OWNER_ID,?) ");
        strBuff.append(" ORDER BY ct.created_on DESC ");
		return strBuff.toString();
	}

	
	@Override
	public String loadChannelC2CVoucherTransfersListQry(String reveiverCategoryCode, String geoCode, String domainCode, String searchParam, String approvalLevel) {
		
		final StringBuilder strBuff = new StringBuilder(" SELECT ct.approval_doc_file_path, ct.transfer_id, ct.transfer_date, ct.type,ct.transfer_sub_type,ct.transfer_type, ");
        strBuff.append(" ct.payable_amount, ct.net_payable_amount,ct.ext_txn_no, ct.ext_txn_date,d.domain_name , g.grph_domain_name, ct.dual_comm_type, ");
        strBuff.append(" c.category_name,ct.status , u.user_name, ct.transfer_mrp,ct.reference_no,ct.to_user_id,cti.commision_quantity,cti.sender_debit_quantity,cti.receiver_credit_quantity, ");
        strBuff.append(" ct.first_level_approved_quantity, ct.second_level_approved_quantity, ct.third_level_approved_quantity,ct.product_type, ct.receiver_txn_profile, ");
        strBuff.append(" cti.user_wallet, ct.COMMISSION_PROFILE_SET_ID, ct.COMMISSION_PROFILE_VER , ct.pmt_inst_type ,ct.to_msisdn,ct.transfer_initiated_by");
        if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(approvalLevel)) {
        	strBuff.append(" , app1.user_name first_app_name,ct.first_approved_on ");
        } else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(approvalLevel)) {
        	strBuff.append(" , app1.user_name first_app_name,ct.first_approved_on ");
        	strBuff.append(" , app2.user_name second_app_name, ct.second_approved_on ");
        }
        //strBuff.append(" FROM channel_transfers ct , domains d , geographical_domains g ,categories c,users u, users app1,users app2, ");
        strBuff.append(" FROM channel_transfers ct , domains d , geographical_domains g ,categories c,users u,  ");
        if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(approvalLevel)) {
        	strBuff.append(" users app1, ");
        } else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(approvalLevel)) {
        	strBuff.append(" users app1,users app2,");
        	
        }
        strBuff.append(" users su,channel_transfers_items cti ");
        strBuff.append(" WHERE ct.type='C2C' AND ct.from_user_id= ? ");
        strBuff.append(" AND ct.network_code = ? AND ct.network_code_for = ? AND ct.domain_code = d.domain_code AND ct.transfer_sub_type = 'V'  ");
        strBuff.append(" AND ct.grph_domain_code = g.grph_domain_code ");
        if (!BTSLUtil.isNullString(reveiverCategoryCode) && !reveiverCategoryCode.equalsIgnoreCase(PretupsI.ALL)) {
            strBuff.append(" AND ct.receiver_category_code=? ");
        }
        strBuff.append(" AND cti.transfer_id = ct.transfer_id ");
        strBuff.append(" AND ct.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains gd1 WHERE status='Y' ");
        strBuff.append(" CONNECT BY PRIOR grph_domain_code=parent_grph_domain_code  ");
        strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code FROM user_geographies WHERE user_id=? ");
        if (!BTSLUtil.isNullString(geoCode)) {
            strBuff.append(" AND grph_domain_code = decode(?,?,grph_domain_code,?)  ");
        }
        strBuff.append(" ) ) AND ct.receiver_category_code = c.category_code AND g.status = 'Y' ");
        strBuff.append(" AND ct.transfer_initiated_by = u.user_id ");
        if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(approvalLevel)) {
        	strBuff.append(" AND ct.first_approved_by = app1.user_id ");
        } else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(approvalLevel)) {
        	strBuff.append(" AND ct.first_approved_by = app1.user_id ");
        	strBuff.append(" AND ct.second_approved_by = app2.user_id ");	
        }
        if (!domainCode.equalsIgnoreCase(PretupsI.ALL)) {
            strBuff.append(" AND trim(ct.domain_code)= ? ");
        }
        if (!PretupsI.ALL.equals(searchParam)) {
            strBuff.append(" AND ct.to_user_id = ?  ");
        }

        strBuff.append(" AND ct.status = ? ");
        
        strBuff.append(" AND ct.to_user_id= su.user_id ");
        strBuff.append(" AND su.OWNER_ID=DECODE(?,'ALL',su.OWNER_ID,?) ");
        strBuff.append(" ORDER BY ct.created_on DESC ");
		return strBuff.toString();
	}
	/**
	 * method for wild card search for list
	 */
	@Override
	public String loadChannelToChannelVoucherTransfersListQryWildCard(String reveiverCategoryCode, String geoCode, String domainCode, String searchParam, String approvalLevel,String userNameSearch) {
		
		final StringBuilder strBuff = new StringBuilder(" SELECT ct.approval_doc_file_path, ct.transfer_id, ct.transfer_date, ct.type,ct.transfer_sub_type,ct.transfer_type, ");
        strBuff.append(" ct.payable_amount, ct.net_payable_amount,ct.ext_txn_no, ct.ext_txn_date,d.domain_name , g.grph_domain_name, ct.dual_comm_type, ");
        strBuff.append(" c.category_name,ct.status , u.user_name, ct.transfer_mrp,ct.reference_no,ct.to_user_id,cti.commision_quantity,cti.sender_debit_quantity,cti.receiver_credit_quantity, ");
        strBuff.append(" ct.first_level_approved_quantity, ct.second_level_approved_quantity, ct.third_level_approved_quantity,ct.product_type, ct.receiver_txn_profile, ");
        strBuff.append(" cti.user_wallet, ct.COMMISSION_PROFILE_SET_ID, ct.COMMISSION_PROFILE_VER , ct.pmt_inst_type ,ct.to_msisdn,ct.transfer_initiated_by");
        if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(approvalLevel)) {
        	strBuff.append(" , app1.user_name first_app_name,ct.first_approved_on ");
        } else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(approvalLevel)) {
        	strBuff.append(" , app1.user_name first_app_name,ct.first_approved_on ");
        	strBuff.append(" , app2.user_name second_app_name, ct.second_approved_on ");
        }
        //strBuff.append(" FROM channel_transfers ct , domains d , geographical_domains g ,categories c,users u, users app1,users app2, ");
        strBuff.append(" FROM channel_transfers ct , domains d , geographical_domains g ,categories c,users u,  ");
        if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(approvalLevel)) {
        	strBuff.append(" users app1, ");
        } else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(approvalLevel)) {
        	strBuff.append(" users app1,users app2,");
        	
        }
        strBuff.append(" users su,channel_transfers_items cti ");
        strBuff.append(" WHERE ct.type='C2C' AND ct.from_user_id= ? ");
        strBuff.append(" AND ct.network_code = ? AND ct.network_code_for = ? AND ct.domain_code = d.domain_code AND ct.transfer_sub_type = 'V'  ");
        strBuff.append(" AND ct.grph_domain_code = g.grph_domain_code ");
        if (!BTSLUtil.isNullString(reveiverCategoryCode) && !reveiverCategoryCode.equalsIgnoreCase(PretupsI.ALL)) {
            strBuff.append(" AND ct.receiver_category_code=? ");
        }
        strBuff.append(" AND cti.transfer_id = ct.transfer_id ");
        if (!BTSLUtil.isNullString(userNameSearch)) {
            strBuff.append(" AND u.user_name LIKE '"+userNameSearch +"%'");
        }
        strBuff.append(" AND ct.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains gd1 WHERE status='Y' ");
        strBuff.append(" CONNECT BY PRIOR grph_domain_code=parent_grph_domain_code  ");
        strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code FROM user_geographies WHERE user_id=? ");
        if (!BTSLUtil.isNullString(geoCode)) {
            strBuff.append(" AND grph_domain_code = decode(?,?,grph_domain_code,?)  ");
        }
        strBuff.append(" ) ) AND ct.receiver_category_code = c.category_code AND g.status = 'Y' ");
        strBuff.append(" AND ct.transfer_initiated_by = u.user_id ");
        if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1.equals(approvalLevel)) {
        	strBuff.append(" AND ct.first_approved_by = app1.user_id ");
        } else if(PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2.equals(approvalLevel)) {
        	strBuff.append(" AND ct.first_approved_by = app1.user_id ");
        	strBuff.append(" AND ct.second_approved_by = app2.user_id ");	
        }
        if (!domainCode.equalsIgnoreCase(PretupsI.ALL)) {
            strBuff.append(" AND trim(ct.domain_code)= ? ");
        }
        if (!PretupsI.ALL.equals(searchParam)) {
            strBuff.append(" AND ct.to_user_id = ?  ");
        }

        strBuff.append(" AND ct.status = ? ");
        
        strBuff.append(" AND ct.to_user_id= su.user_id ");
        strBuff.append(" AND su.OWNER_ID=DECODE(?,'ALL',su.OWNER_ID,?) ");
        strBuff.append(" ORDER BY ct.created_on DESC ");
		return strBuff.toString();
	}


	
	@Override
	public String loadChannelTransfersVOC2CQry(ChannelTransferVO channelTransferVO)
	{
		final StringBuilder strBuff = new StringBuilder(" SELECT ct.transfer_id, ct.first_level_approved_quantity, ");
        strBuff.append(" ct.second_level_approved_quantity, ct.third_level_approved_quantity, ct.network_code, ct.network_code_for, ");
        strBuff.append(" ct.grph_domain_code, ct.domain_code, ct.sender_category_code, ct.sender_grade_code, ");
        strBuff.append(" ct.receiver_grade_code, ct.from_user_id, ct.to_user_id, ct.transfer_date, ct.reference_no, ");
        strBuff.append(" ct.ext_txn_no, ct.ext_txn_date,ct.transfer_category,ct.commission_profile_set_id, ");
        strBuff.append(" ct.commission_profile_ver, ct.requested_quantity, ct.channel_user_remarks, ");
        strBuff.append(" ct.first_approver_remarks, ct.second_approver_remarks, ct.third_approver_remarks, ");
        strBuff.append(" ct.first_approved_by, ct.first_approved_on, ct.second_approved_by, ct.second_approved_on, ");
        strBuff.append(" ct.third_approved_by, ct.third_approved_on, ct.cancelled_by, ct.cancelled_on, ");
        strBuff.append(" ct.transfer_initiated_by, ct.modified_by, ct.modified_on, ct.status, ct.type, ");
        strBuff.append(" ct.transfer_mrp, ct.first_approver_limit, ct.second_approver_limit,  ");
        strBuff.append(" ct.payable_amount, ct.net_payable_amount, ct.batch_no, ct.batch_date, ct.pmt_inst_type, ct.pmt_inst_no, ");
        strBuff.append(" ct.pmt_inst_date, ct.pmt_inst_amount, ");
        strBuff.append(" ct.sender_txn_profile, ct.receiver_txn_profile, ");
        strBuff.append(" ct.total_tax1, ct.total_tax2, ");
        strBuff.append(" ct.total_tax3, ct.source, ct.receiver_category_code, ct.request_gateway_code, ct.request_gateway_type, ct.pmt_inst_source ,");
        strBuff.append(" ct.product_type , ct.sms_default_lang,ct.sms_second_lang, d.domain_name , g.grph_domain_name, c.category_name , cg.grade_name, u.user_name,u.address1,u.address2,u.city,u.state,u.country,cps.comm_profile_set_name,tp.profile_name, ");
        strBuff.append(" appu1.user_name firstapprovedby,appu2.user_name secondapprovedby,appu3.user_name thirdapprovedby, ");
        strBuff.append(" appu4.user_name cancelledby,appu5.user_name initatedby , u.msisdn ,ct.msisdn from_msisdn,ct.to_msisdn to_msisdn, u.external_code , appu6.user_name fromusername ,ct.transfer_type,ct.transfer_sub_type,tp2.profile_name sender_txn_profile_name, ");
        strBuff.append(" cti.commision_quantity,cti.sender_debit_quantity,cti.receiver_credit_quantity,");
        strBuff.append(" ct.txn_wallet,ct.control_transfer,ct.to_grph_domain_code,ct.to_domain_code,ct.created_on,ct.created_by,ct.active_user_id,");
        strBuff.append(" cti.user_wallet, ct.stock_updated,ct.dual_comm_type, ct.info1,ct.PMT_INST_TYPE,cti.product_code,  ");
        strBuff.append(" c1.CATEGORY_NAME fromCategoryDesc, ct.sender_grade_code fromGradeCodeDesc, cps1.COMM_PROFILE_SET_NAME fromCommissionProfileIDDesc, u1.USER_NAME fromUserName , d1.DOMAIN_NAME to_domain_name, g1.GRPH_DOMAIN_NAME to_grph_domain_name ");
        strBuff.append(" FROM ");
        strBuff.append(" channel_transfers ct, geographical_domains g, categories c , users u , commission_profile_set cps, domains d , ");
        strBuff.append(" transfer_profile tp ,transfer_profile tp2, users appu1, users appu2 ,users appu3 , users appu4 ,users appu5 ,channel_grades cg ,users appu6 ,channel_transfers_items cti, ");
        strBuff.append(" categories c1, channel_users cu, commission_profile_set cps1, channel_grades cg1, users u1 , geographical_domains g1 , domains d1 ");
        strBuff.append(" WHERE ");
        strBuff.append(" ct.transfer_id = ? AND ct.transfer_id=cti.transfer_id AND ct.network_code = ? AND ct.network_code_for = ? AND ");
        strBuff.append(" ct.domain_code = d.domain_code AND ct.grph_domain_code = g.grph_domain_code AND ct.receiver_category_code = c.category_code AND ");
        strBuff.append(" ct.to_user_id  = u.user_id AND ct.receiver_grade_code = cg.grade_code  AND ct.receiver_txn_profile = tp.profile_id AND ct.sender_txn_profile = tp2.profile_id(+) AND ");
        strBuff.append(" ct.commission_profile_set_id = cps.comm_profile_set_id AND ");
        strBuff.append(" ct.first_approved_by = appu1.user_id(+)  AND ct.second_approved_by = appu2.user_id(+) AND ct.third_approved_by = appu3.user_id(+) AND ");
        strBuff.append(" ct.cancelled_by = appu4.user_id(+) AND ct.transfer_initiated_by = appu5.user_id(+) AND ");
        strBuff.append(" ct.from_user_id = appu6.user_id(+) ");
        strBuff.append(" AND cu.COMM_PROFILE_SET_ID = cps1.COMM_PROFILE_SET_ID ");
        strBuff.append(" AND ct.sender_grade_code = cg1.grade_code(+) ");
        strBuff.append(" AND ct.from_user_id  = u1.user_id(+) ");
        strBuff.append(" AND cu.USER_ID = u1.USER_ID ");
        strBuff.append(" AND ct.sender_category_code = c1.category_code(+) AND ct.TO_DOMAIN_CODE = d1.DOMAIN_CODE AND ct.TO_GRPH_DOMAIN_CODE = g1.grph_domain_code ");

        return strBuff.toString();
	}

	
	@Override
	public String loadChannelTransfersVOC2CTcpQry(ChannelTransferVO channelTransferVO)
	{
		final StringBuilder strBuff = new StringBuilder(" SELECT ct.transfer_id, ct.first_level_approved_quantity, ");
        strBuff.append(" ct.second_level_approved_quantity, ct.third_level_approved_quantity, ct.network_code, ct.network_code_for, ");
        strBuff.append(" ct.grph_domain_code, ct.domain_code, ct.sender_category_code, ct.sender_grade_code, ");
        strBuff.append(" ct.receiver_grade_code, ct.from_user_id, ct.to_user_id, ct.transfer_date, ct.reference_no, ");
        strBuff.append(" ct.ext_txn_no, ct.ext_txn_date,ct.transfer_category,ct.commission_profile_set_id, ");
        strBuff.append(" ct.commission_profile_ver, ct.requested_quantity, ct.channel_user_remarks, ");
        strBuff.append(" ct.first_approver_remarks, ct.second_approver_remarks, ct.third_approver_remarks, ");
        strBuff.append(" ct.first_approved_by, ct.first_approved_on, ct.second_approved_by, ct.second_approved_on, ");
        strBuff.append(" ct.third_approved_by, ct.third_approved_on, ct.cancelled_by, ct.cancelled_on, ");
        strBuff.append(" ct.transfer_initiated_by, ct.modified_by, ct.modified_on, ct.status, ct.type, ");
        strBuff.append(" ct.transfer_mrp, ct.first_approver_limit, ct.second_approver_limit,  ");
        strBuff.append(" ct.payable_amount, ct.net_payable_amount, ct.batch_no, ct.batch_date, ct.pmt_inst_type, ct.pmt_inst_no, ");
        strBuff.append(" ct.pmt_inst_date, ct.pmt_inst_amount, ");
        strBuff.append(" ct.sender_txn_profile, ct.receiver_txn_profile, ");
        strBuff.append(" ct.total_tax1, ct.total_tax2, ");
        strBuff.append(" ct.total_tax3, ct.source, ct.receiver_category_code, ct.request_gateway_code, ct.request_gateway_type, ct.pmt_inst_source ,");
        strBuff.append(" ct.product_type , ct.sms_default_lang,ct.sms_second_lang, d.domain_name , g.grph_domain_name, c.category_name , cg.grade_name, u.user_name,u.address1,u.address2,u.city,u.state,u.country,cps.comm_profile_set_name, ");
        strBuff.append(" appu1.user_name firstapprovedby,appu2.user_name secondapprovedby,appu3.user_name thirdapprovedby, ");
        strBuff.append(" appu4.user_name cancelledby,appu5.user_name initatedby , u.msisdn ,ct.msisdn from_msisdn,ct.to_msisdn to_msisdn, u.external_code , appu6.user_name fromusername ,ct.transfer_type,ct.transfer_sub_type, ");
        strBuff.append(" cti.commision_quantity,cti.sender_debit_quantity,cti.receiver_credit_quantity,");
        strBuff.append(" ct.txn_wallet,ct.control_transfer,ct.to_grph_domain_code,ct.to_domain_code,ct.created_on,ct.created_by,ct.active_user_id,");
        strBuff.append(" cti.user_wallet, ct.stock_updated,ct.dual_comm_type, ct.info1,ct.PMT_INST_TYPE,cti.product_code,  ");
        strBuff.append(" c1.CATEGORY_NAME fromCategoryDesc, ct.sender_grade_code fromGradeCodeDesc, cps1.COMM_PROFILE_SET_NAME fromCommissionProfileIDDesc, u1.USER_NAME fromUserName  , d1.DOMAIN_NAME to_domain_name, g1.GRPH_DOMAIN_NAME to_grph_domain_name ");
        strBuff.append(" FROM ");
        strBuff.append(" channel_transfers ct, geographical_domains g, categories c , users u , commission_profile_set cps, domains d , ");
        strBuff.append("  users appu1, users appu2 ,users appu3 , users appu4 ,users appu5 ,channel_grades cg ,users appu6 ,channel_transfers_items cti, ");
        strBuff.append(" categories c1, channel_users cu, commission_profile_set cps1, channel_grades cg1, users u1 , geographical_domains g1 , domains d1 ");
        strBuff.append(" WHERE ");
        strBuff.append(" ct.transfer_id = ? AND ct.transfer_id=cti.transfer_id AND ct.network_code = ? AND ct.network_code_for = ? AND ");
        strBuff.append(" ct.domain_code = d.domain_code AND ct.grph_domain_code = g.grph_domain_code AND ct.receiver_category_code = c.category_code AND ");
        strBuff.append(" ct.to_user_id  = u.user_id AND ct.receiver_grade_code = cg.grade_code   AND ");
        strBuff.append(" ct.commission_profile_set_id = cps.comm_profile_set_id AND ");
        strBuff.append(" ct.first_approved_by = appu1.user_id(+)  AND ct.second_approved_by = appu2.user_id(+) AND ct.third_approved_by = appu3.user_id(+) AND ");
        strBuff.append(" ct.cancelled_by = appu4.user_id(+) AND ct.transfer_initiated_by = appu5.user_id(+) AND ");
        strBuff.append(" ct.from_user_id = appu6.user_id(+) ");
        strBuff.append(" AND cu.COMM_PROFILE_SET_ID = cps1.COMM_PROFILE_SET_ID ");
        strBuff.append(" AND ct.sender_grade_code = cg1.grade_code(+) ");
        strBuff.append(" AND ct.from_user_id  = u1.user_id(+) ");
        strBuff.append(" AND cu.USER_ID = u1.USER_ID ");
        strBuff.append(" AND ct.sender_category_code = c1.category_code(+)   AND ct.TO_DOMAIN_CODE = d1.DOMAIN_CODE AND ct.TO_GRPH_DOMAIN_CODE = g1.grph_domain_code ");

        return strBuff.toString();
	}

	@Override
	public String loadChannelToChannelStockTransfersListQry(String reveiverCategoryCode, String geoCode,
			String domainCode, String searchParam, String approvalLevel) {

		
		
		final StringBuilder strBuff = new StringBuilder(" SELECT ct.transfer_id, ct.transfer_date, ct.first_approved_on, ct.first_approved_by, ct.second_approved_on, ct.second_approved_by, ct.type,ct.transfer_sub_type,ct.transfer_type, ");
        strBuff.append(" ct.payable_amount, ct.net_payable_amount,ct.ext_txn_no, ct.ext_txn_date,d.domain_name , g.grph_domain_name, ct.dual_comm_type, ct.approval_doc as APPROVALDOC, ct.approval_doc_type, ct.approval_doc_file_path, ");
        strBuff.append(" c.category_name,ct.status , u.user_name, ct.transfer_mrp,ct.reference_no,ct.to_user_id,cti.commision_quantity,cti.sender_debit_quantity,cti.receiver_credit_quantity, ");
        strBuff.append(" ct.first_level_approved_quantity, ct.second_level_approved_quantity, ct.third_level_approved_quantity,ct.product_type, ct.receiver_txn_profile, ");
        strBuff.append(" cti.user_wallet, ct.COMMISSION_PROFILE_SET_ID, ct.COMMISSION_PROFILE_VER , ct.pmt_inst_type ,ct.to_msisdn,ct.transfer_initiated_by");
        strBuff.append(" FROM channel_transfers ct , domains d , geographical_domains g ,categories c,users u, ");
        strBuff.append(" users su,channel_transfers_items cti ");
        strBuff.append(" WHERE ct.type='C2C' AND ct.from_user_id = ? ");
        strBuff.append(" AND ct.network_code = ? AND ct.network_code_for = ? AND ct.domain_code = d.domain_code AND ct.transfer_sub_type = 'T'  ");
        strBuff.append(" AND ct.grph_domain_code = g.grph_domain_code ");
        if (!BTSLUtil.isNullString(reveiverCategoryCode) && !reveiverCategoryCode.equalsIgnoreCase(PretupsI.ALL)) {
            strBuff.append(" AND ct.receiver_category_code=? ");
        }
        strBuff.append(" AND cti.transfer_id = ct.transfer_id ");
        strBuff.append(" AND ct.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains gd1 WHERE status='Y' ");
        strBuff.append(" CONNECT BY PRIOR grph_domain_code=parent_grph_domain_code  ");
        strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code FROM user_geographies WHERE user_id=? ");
        if (!BTSLUtil.isNullString(geoCode)) {
            strBuff.append(" AND grph_domain_code = decode(?,?,grph_domain_code,?)  ");
        }
        strBuff.append(" ) ) AND ct.receiver_category_code = c.category_code AND g.status = 'Y' ");
        strBuff.append(" AND ct.transfer_initiated_by = u.user_id ");

        if (!domainCode.equalsIgnoreCase(PretupsI.ALL)) {
            strBuff.append(" AND trim(ct.domain_code)= ? ");
        }
        if (!PretupsI.ALL.equals(searchParam)) {
            strBuff.append(" AND ct.to_user_id = ?  ");
        }

        strBuff.append(" AND ct.status = ? ");
       

        strBuff.append(" AND ct.to_user_id= su.user_id ");
        strBuff.append(" AND su.OWNER_ID=DECODE(?,'ALL',su.OWNER_ID,?) ");
        strBuff.append(" ORDER BY ct.created_on DESC ");
		return strBuff.toString();
	
	}

	@Override
	public String loadChannelToChannelStockTransfersListTransferIdQry(String reveiverCategoryCode, String geoCode,
			String domainCode, String searchParam, String approvalLevel) {

		
		
		final StringBuilder strBuff = new StringBuilder(" SELECT ct.transfer_id, ct.transfer_date, ct.first_approved_on, ct.first_approved_by, ct.second_approved_on, ct.second_approved_by, ct.type,ct.transfer_sub_type,ct.transfer_type, ");
        strBuff.append(" ct.payable_amount, ct.net_payable_amount,ct.ext_txn_no, ct.ext_txn_date,d.domain_name , g.grph_domain_name, ct.dual_comm_type, ct.approval_doc as APPROVALDOC, ct.approval_doc_type, ct.approval_doc_file_path, ");
        strBuff.append(" c.category_name,ct.status , u.user_name, ct.transfer_mrp,ct.reference_no,ct.to_user_id,cti.commision_quantity,cti.sender_debit_quantity,cti.receiver_credit_quantity, ");
        strBuff.append(" ct.first_level_approved_quantity, ct.second_level_approved_quantity, ct.third_level_approved_quantity,ct.product_type, ct.receiver_txn_profile, ");
        strBuff.append(" cti.user_wallet, ct.COMMISSION_PROFILE_SET_ID, ct.COMMISSION_PROFILE_VER , ct.pmt_inst_type ,ct.to_msisdn,ct.transfer_initiated_by");
        strBuff.append(" FROM channel_transfers ct , domains d , geographical_domains g ,categories c,users u, ");
        strBuff.append(" users su,channel_transfers_items cti ");
        strBuff.append(" WHERE ct.type='C2C' AND ct.from_user_id = ? ");
        strBuff.append(" AND ct.network_code = ? AND ct.network_code_for = ? AND ct.domain_code = d.domain_code AND ct.transfer_sub_type = 'T'  ");
        strBuff.append(" AND ct.grph_domain_code = g.grph_domain_code ");
        if (!BTSLUtil.isNullString(reveiverCategoryCode) && !reveiverCategoryCode.equalsIgnoreCase(PretupsI.ALL)) {
            strBuff.append(" AND ct.receiver_category_code=? ");
        }
        strBuff.append(" AND cti.transfer_id = ct.transfer_id ");
        strBuff.append(" AND ct.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains gd1 WHERE status='Y' ");
        strBuff.append(" CONNECT BY PRIOR grph_domain_code=parent_grph_domain_code  ");
        strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code FROM user_geographies WHERE user_id=? ");
        if (!BTSLUtil.isNullString(geoCode)) {
            strBuff.append(" AND grph_domain_code = decode(?,?,grph_domain_code,?)  ");
        }
        strBuff.append(" ) ) AND ct.receiver_category_code = c.category_code AND g.status = 'Y' ");
        strBuff.append(" AND ct.transfer_initiated_by = u.user_id ");

        if (!domainCode.equalsIgnoreCase(PretupsI.ALL)) {
            strBuff.append(" AND trim(ct.domain_code)= ? ");
        }
        if (!PretupsI.ALL.equals(searchParam)) {
            strBuff.append(" AND ct.to_user_id = ?  ");
        }

        strBuff.append(" AND ct.status = ? ");
       

        strBuff.append("  AND ct.transfer_id=? AND ct.to_user_id= su.user_id ");
        strBuff.append(" AND su.OWNER_ID=DECODE(?,'ALL',su.OWNER_ID,?)");
        strBuff.append(" ORDER BY ct.created_on DESC ");
		return strBuff.toString();
	
	}

	@Override
	public String loadChannelToChannelStockTransfersListQryWildCard(String reveiverCategoryCode, String geoCode,
			String domainCode, String searchParam, String approvalLevel,String userNameSearch) {

		
		final StringBuilder strBuff = new StringBuilder(" SELECT ct.transfer_id, ct.transfer_date, ct.first_approved_on, ct.first_approved_by, ct.second_approved_on, ct.second_approved_by, ct.type,ct.transfer_sub_type,ct.transfer_type, ");
        strBuff.append(" ct.payable_amount, ct.net_payable_amount,ct.ext_txn_no, ct.ext_txn_date,d.domain_name , g.grph_domain_name, ct.dual_comm_type, ct.approval_doc as APPROVALDOC, ct.approval_doc_type, ct.approval_doc_file_path, ");
        strBuff.append(" c.category_name,ct.status , u.user_name, ct.transfer_mrp,ct.reference_no,ct.to_user_id,cti.commision_quantity,cti.sender_debit_quantity,cti.receiver_credit_quantity, ");
        strBuff.append(" ct.first_level_approved_quantity, ct.second_level_approved_quantity, ct.third_level_approved_quantity,ct.product_type, ct.receiver_txn_profile, ");
        strBuff.append(" cti.user_wallet, ct.COMMISSION_PROFILE_SET_ID, ct.COMMISSION_PROFILE_VER , ct.pmt_inst_type ,ct.to_msisdn,ct.transfer_initiated_by");
        strBuff.append(" FROM channel_transfers ct , domains d , geographical_domains g ,categories c,users u, ");
        strBuff.append(" users su,channel_transfers_items cti ");
        strBuff.append(" WHERE ct.type='C2C' AND ct.from_user_id = ? ");
        strBuff.append(" AND ct.network_code = ? AND ct.network_code_for = ? AND ct.domain_code = d.domain_code AND ct.transfer_sub_type = 'T'  ");
        strBuff.append(" AND ct.grph_domain_code = g.grph_domain_code ");
        if (!BTSLUtil.isNullString(reveiverCategoryCode) && !reveiverCategoryCode.equalsIgnoreCase(PretupsI.ALL)) {
            strBuff.append(" AND ct.receiver_category_code=? ");
        }
        strBuff.append(" AND cti.transfer_id = ct.transfer_id ");
        if (!BTSLUtil.isNullString(userNameSearch)) {
            strBuff.append(" AND u.user_name LIKE '"+userNameSearch +"%'");
        }
        strBuff.append(" AND ct.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains gd1 WHERE status='Y' ");
        
        strBuff.append(" CONNECT BY PRIOR grph_domain_code=parent_grph_domain_code  ");
        strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code FROM user_geographies WHERE user_id=? ");
        if (!BTSLUtil.isNullString(geoCode)) {
            strBuff.append(" AND grph_domain_code = decode(?,?,grph_domain_code,?)  ");
        }
        strBuff.append(" ) ) AND ct.receiver_category_code = c.category_code AND g.status = 'Y' ");
        strBuff.append(" AND ct.transfer_initiated_by = u.user_id ");

        if (!domainCode.equalsIgnoreCase(PretupsI.ALL)) {
            strBuff.append(" AND trim(ct.domain_code)= ? ");
        }
        if (!PretupsI.ALL.equals(searchParam)) {
            strBuff.append(" AND ct.to_user_id = ?  ");
        }

        strBuff.append(" AND ct.status = ? ");
       

        strBuff.append(" AND ct.to_user_id= su.user_id ");
        strBuff.append(" AND su.OWNER_ID=DECODE(?,'ALL',su.OWNER_ID,?) ");
        strBuff.append(" ORDER BY ct.created_on DESC ");
		return strBuff.toString();
	
	}

	
	@Override
	public String loadChannelToChannelStockTransfersListQryPagination(String reveiverCategoryCode, String geoCode,
			String domainCode, String searchParam, String approvalLevel,String pageNumber,String entriesPerPage,String userNameSearch) {

		
		final StringBuilder strBuff = new StringBuilder(" SELECT ct.transfer_id, ct.transfer_date, ct.first_approved_on, ct.first_approved_by, ct.second_approved_on, ct.second_approved_by, ct.type,ct.transfer_sub_type,ct.transfer_type, ");
        strBuff.append(" ct.payable_amount, ct.net_payable_amount,ct.ext_txn_no, ct.ext_txn_date,d.domain_name , g.grph_domain_name, ct.dual_comm_type, ct.approval_doc as APPROVALDOC, ct.approval_doc_type, ct.approval_doc_file_path, ");
        strBuff.append(" c.category_name,ct.status , u.user_name, ct.transfer_mrp,ct.reference_no,ct.to_user_id,");
        strBuff.append(" ct.first_level_approved_quantity, ct.second_level_approved_quantity, ct.third_level_approved_quantity,ct.product_type, ct.receiver_txn_profile, ");
        strBuff.append("  ct.COMMISSION_PROFILE_SET_ID, ct.COMMISSION_PROFILE_VER , ct.pmt_inst_type ,ct.to_msisdn,ct.transfer_initiated_by");
        strBuff.append(" FROM channel_transfers ct , domains d , geographical_domains g ,categories c,users u, ");
        strBuff.append(" users su ");
        strBuff.append(" WHERE ct.type='C2C' AND ct.from_user_id= ? ");
        strBuff.append(" AND ct.network_code = ? AND ct.network_code_for = ? AND ct.domain_code = d.domain_code AND ct.transfer_sub_type = 'T'  ");
        strBuff.append(" AND ct.grph_domain_code = g.grph_domain_code ");
        if (!BTSLUtil.isNullString(reveiverCategoryCode) && !reveiverCategoryCode.equalsIgnoreCase(PretupsI.ALL)) {
            strBuff.append(" AND ct.receiver_category_code=? ");
        }
        if (!BTSLUtil.isNullString(userNameSearch)) {
            strBuff.append(" AND u.user_name LIKE '"+userNameSearch +"%'");
        }
        strBuff.append(" AND ct.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains gd1 WHERE status='Y' ");
       
        strBuff.append(" CONNECT BY PRIOR grph_domain_code=parent_grph_domain_code  ");
        strBuff.append(" START WITH grph_domain_code IN (SELECT grph_domain_code FROM user_geographies WHERE user_id=? ");
        if (!BTSLUtil.isNullString(geoCode)) {
            strBuff.append(" AND grph_domain_code = decode(?,?,grph_domain_code,?)  ");
        }
        strBuff.append(" ) ) AND ct.receiver_category_code = c.category_code AND g.status = 'Y' ");
        strBuff.append(" AND ct.transfer_initiated_by = u.user_id ");

        if (!domainCode.equalsIgnoreCase(PretupsI.ALL)) {
            strBuff.append(" AND trim(ct.domain_code)= ? ");
        }
        if (!PretupsI.ALL.equals(searchParam)) {
            strBuff.append(" AND ct.to_user_id = ?  ");
        }

        strBuff.append(" AND ct.status = ? ");
       

        strBuff.append(" AND ct.to_user_id= su.user_id ");
        strBuff.append(" AND su.OWNER_ID=DECODE(?,'ALL',su.OWNER_ID,?) ");
        strBuff.append(" ORDER BY ct.created_on DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
		return strBuff.toString();
	
	}


	@Override
	public StringBuilder loadBundleIDForTransactionIdQry()
	{
		final StringBuilder strBuff = new StringBuilder(" SELECT BUNDLE_ID ");
		strBuff.append(" FROM channel_voucher_items ");
		strBuff.append(" WHERE transfer_id = ? ");
        return strBuff;
	}
	
	@Override
	public StringBuilder loadPackageVoucherDetailsForTransactionIdQry()
	{
		final StringBuilder strBuff = new StringBuilder(" SELECT CVT.TRANSFER_ID,VP.PRODUCT_NAME,CVT.BUNDLE_ID,VBM.BUNDLE_NAME,CVT.REQUESTED_QUANTITY/VBD.QUANTITY AS BUNDLE_COUNT_REQUESTED,VBD.QUANTITY AS PRODUCT_COUNT,VBM.RETAIL_PRICE ,CT.REQUESTED_QUANTITY  ");
		strBuff.append(" FROM CHANNEL_VOUCHER_ITEMS CVT,VOMS_BUNDLE_MASTER VBM,VOMS_BUNDLE_DETAILS VBD,VOMS_PRODUCTS VP,CHANNEL_TRANSFERS CT ");
		strBuff.append(" WHERE CT.TRANSFER_ID = ? ");
		strBuff.append(" AND CVT.TRANSFER_ID = CT.TRANSFER_ID ");
		strBuff.append(" AND CVT.PRODUCT_ID = VBD.PROFILE_ID ");
		strBuff.append(" AND CVT.PRODUCT_ID = VP.PRODUCT_ID ");
		strBuff.append(" AND CVT.BUNDLE_ID = VBM.VOMS_BUNDLE_ID ");
		strBuff.append(" AND VBM.VOMS_BUNDLE_ID = VBD.VOMS_BUNDLE_ID ");		
        return strBuff;
	}

	@Override
	public String loadChannelTransferDetailsQry(boolean p_chnlTxnMrpBlockTimeoutAllowed,boolean p_requestGatewayCodeCheckRequired,ChannelTransferVO p_channelTransferVO) {
		 StringBuilder strBuff = new StringBuilder(" SELECT ct.transfer_id, ct.first_level_approved_quantity, ");
	 		strBuff.append(" ct.second_level_approved_quantity, ct.third_level_approved_quantity, ct.network_code, ct.network_code_for, ");
	 		strBuff.append(" ct.grph_domain_code, ct.domain_code, ct.sender_category_code, ct.sender_grade_code, ");
	         strBuff.append(" ct.receiver_grade_code, ct.from_user_id, ct.to_user_id, ct.transfer_date, ct.close_date, ct.reference_no, ");
	 		strBuff.append(" ct.ext_txn_no, ct.ext_txn_date,ct.transfer_category,ct.commission_profile_set_id, ");
	         strBuff.append(" ct.commission_profile_ver, ct.requested_quantity, ct.channel_user_remarks, ");
	         strBuff.append(" ct.first_approver_remarks, ct.second_approver_remarks, ct.third_approver_remarks, ");
	         strBuff.append(" ct.first_approved_by, ct.first_approved_on, ct.second_approved_by, ct.second_approved_on, ");
	         strBuff.append(" ct.third_approved_by, ct.third_approved_on, ct.cancelled_by, ct.cancelled_on, ");
	 		strBuff.append(" ct.transfer_initiated_by, ct.modified_by, ct.modified_on, ct.status, ct.type, ");
	 		strBuff.append(" ct.transfer_mrp, ct.first_approver_limit, ct.second_approver_limit,  ");
	 		strBuff.append(" ct.payable_amount, ct.net_payable_amount, ct.batch_no, ct.batch_date, ct.pmt_inst_type, ct.pmt_inst_no, ");
	 		strBuff.append(" ct.pmt_inst_date, ct.pmt_inst_amount, ct.sender_txn_profile, ct.receiver_txn_profile, ct.total_tax1, ct.total_tax2,  ");
	 		strBuff.append(" ct.total_tax3, ct.source, ct.receiver_category_code, ct.request_gateway_code, ct.request_gateway_type, ct.pmt_inst_source ,");
	 		strBuff.append(" ct.product_type , ct.sms_default_lang,ct.sms_second_lang, d.domain_name , g.grph_domain_name, c.category_name , cg.grade_name, u.user_name,u.address1,u.address2,u.city,u.state,u.country,cps.comm_profile_set_name,tp.profile_name, ");
	 		strBuff.append(" appu1.user_name firstapprovedby,appu2.user_name secondapprovedby,appu3.user_name thirdapprovedby, ");
	 		strBuff.append(" appu4.user_name cancelledby,appu5.user_name initatedby , u.msisdn ,ct.msisdn from_msisdn,ct.to_msisdn to_msisdn, u.external_code , appu6.user_name fromusername ,ct.transfer_type,ct.transfer_sub_type,tp2.profile_name sender_txn_profile_name, ");
	 		strBuff.append(" cti.commision_quantity,cti.sender_debit_quantity,cti.receiver_credit_quantity,");
	 		strBuff.append(" ct.txn_wallet,ct.control_transfer,ct.to_grph_domain_code,ct.to_domain_code,ct.created_on,ct.created_by,ct.active_user_id,");
	 		strBuff.append(" cti.user_wallet");
	 		strBuff.append(" FROM ");
	 		strBuff.append(" channel_transfers ct, geographical_domains g, categories c , users u , commission_profile_set cps, domains d , ");
	 		strBuff.append(" transfer_profile tp ,transfer_profile tp2, users appu1, users appu2 ,users appu3 , users appu4 ,users appu5 ,channel_grades cg ,users appu6 ,channel_transfers_items cti");
	 		strBuff.append(" WHERE ");
	 		strBuff.append(" ct.from_user_id = ? AND ct.to_user_id = ? AND ");
	 		if(p_chnlTxnMrpBlockTimeoutAllowed) {
	 			strBuff.append(" ct.transfer_mrp = ? AND ");
	        }
	        if(p_requestGatewayCodeCheckRequired) {
	         	strBuff.append(" ct.request_gateway_code = ? AND ");
	         	strBuff.append(" ct.request_gateway_type = ? AND ");
	        }
	        strBuff.append(" ct.status = ? AND ");
	        if(!BTSLUtil.isNullString(p_channelTransferVO.getProductType())) {
	         	strBuff.append(" ct.product_type = ? AND ");
	        }
	        strBuff.append(" ct.type = ? AND ");
	        strBuff.append(" ct.transfer_category = ? AND ");
	        strBuff.append(" ct.transfer_type = ? AND ");
	        strBuff.append(" ct.transfer_sub_type = ? AND ");
	 		strBuff.append(" ct.transfer_id=cti.transfer_id AND ct.network_code = ? AND ct.network_code_for = ? AND ");
	 		strBuff.append(" ct.domain_code = d.domain_code AND ct.grph_domain_code = g.grph_domain_code AND ct.receiver_category_code = c.category_code AND ");
	 		if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_channelTransferVO.getTransferSubType())) {
	 			strBuff.append(" ct.to_user_id  = u.user_id AND ct.receiver_grade_code = cg.grade_code  AND ct.receiver_txn_profile = tp.profile_id AND ct.sender_txn_profile = tp2.profile_id(+) AND ");
	 		}
	 		else{
	 			strBuff.append(" ct.from_user_id  = u.user_id AND ct.sender_grade_code = cg.grade_code  AND ct.receiver_txn_profile = tp.profile_id(+) AND ct.sender_txn_profile = tp2.profile_id AND ");
	 		}
	 		strBuff.append(" ct.commission_profile_set_id = cps.comm_profile_set_id AND ");
	        strBuff.append(" ct.first_approved_by = appu1.user_id(+)  AND ct.second_approved_by = appu2.user_id(+) AND ct.third_approved_by = appu3.user_id(+) AND ");
	        strBuff.append(" ct.cancelled_by = appu4.user_id(+) AND ");
	        strBuff.append(" ct.transfer_initiated_by = appu5.user_id(+) AND ");
	        strBuff.append(" ct.from_user_id = appu6.user_id(+)  ");
	        //strBuff.append(" AND rownum <=2 ");
	        strBuff.append(" order by ct.transfer_id desc, ct.close_date desc, ct.transfer_date desc ");
	        return strBuff.toString();
	}



	@Override
	public String getC2CTransferCommissiondetails(C2CTransferCommReqDTO c2cTransferCommReqDTO) {
		StringBuilder sb  = new StringBuilder( );
		if(!c2cTransferCommReqDTO.getIncludeStaffUserDetails().trim().toUpperCase().equals(PretupsI.TRUE)  ) {
		
		sb.append(" SELECT (OU.user_name || ' (' || OU.msisdn||')') owner_profile, ");
		sb.append(" CASE WHEN ? = CTRF.TO_USER_ID  THEN 'IN' ELSE 'OUT' END AS TransferINOUT, ");
		sb.append(" (PU.user_name ||' (' ||NVL(PU.msisdn,'ROOT')||')') parent_profile,CTRF.from_user_id, CTRF.to_user_id, FU.user_name as fromUserName, CTRF.msisdn from_Msisdn, TU.user_name as ReceiverName, UC.user_name initiator_user,");	
		sb.append(" CTRF.to_msisdn as ReceiverMSISDN,CTRF.transfer_id,L.lookup_name transferSubType, CTRF.TYPE, ");
		sb.append("    CASE CTRF.transfer_sub_type ");
		sb.append("     WHEN 'V' THEN 'Voucher' ");
		sb.append("     ELSE 'Stock' ");
		sb.append("     END                    AS DISTRIBUTION_TYPE, ");
		sb.append("    L2.lookup_name   trf_cat_name, ");
		sb.append("  CTRF.TRANSFER_ID as TRANSFER_ID,CTI.transfer_date as TRANSFER_DATE,  ");
		sb.append("   CTRF.status as TransactionStatus, ");
		sb.append("   CTRF.modified_ON modified_ON, P.product_name as productName, CTI.required_quantity transfer_mrp, CTI.payable_amount payableAmount, "); 
		sb.append("  CTI.net_payable_amount  netPayableAmount, L1.lookup_name status, ");
		sb.append("   CTI.mrp mrp, CTI.commission_value commission_value, ");
		sb.append("   CTI.otf_amount  otf_amount,CTI.receiver_credit_quantity as  RECEIVER_CREDIT_QUANTITY, CTI.sender_debit_quantity as SENDER_DEBIT_QUANTITY,CTI.COMMISION_QUANTITY,  ");
		sb.append("    CTI.tax3_value tax3_Value, CTI.tax1_value tax1_value,CTI.tax2_value tax2_value, ");
		sb.append("      CTRF.sender_category_code, CTRF.receiver_category_code, ");
		sb.append("    SEND_CAT.category_name as senderCategoryName, REC_CAT.category_name as receiverCategoryName,L4.lookup_name as source, ");
		sb.append("     TUGD.grph_domain_name to_user_geo,TWGD.grph_domain_name to_owner_geo, "); 
		sb.append("   FUGD.grph_domain_name from_user_geo ,FWGD.grph_domain_name from_owner_geo, ");
		sb.append("    L3.lookup_name     AS request_Gateway_Desc ,");
		sb.append("    CTI.SENDER_PREVIOUS_STOCK     AS SENDER_PREVIOUS_STOCK, ");
		sb.append("   CTI.RECEIVER_PREVIOUS_STOCK     AS RECEIVER_PREVIOUS_STOCK,  ");
		sb.append("    CTI.SENDER_POST_STOCK     AS SENDER_POST_STOCK, ");
		sb.append("   CTI.RECEIVER_POST_STOCK     AS RECEIVER_POST_STOCK  ");
		sb.append("   FROM CHANNEL_TRANSFERS CTRF, CHANNEL_TRANSFERS_ITEMS CTI,USERS FU,USERS TU,PRODUCTS P,LOOKUPS L, LOOKUPS L1, USERS UC, LOOKUPS L2, LOOKUPS L3,LOOKUPS L4, "); 
		sb.append("     CATEGORIES SEND_CAT, CATEGORIES REC_CAT,USER_GEOGRAPHIES UG,USERS OU,USER_GEOGRAPHIES UGW, ");
		sb.append("    GEOGRAPHICAL_DOMAINS TUGD, GEOGRAPHICAL_DOMAINS TWGD, " );
		sb.append("     GEOGRAPHICAL_DOMAINS FUGD,GEOGRAPHICAL_DOMAINS FWGD, ");
		sb.append(" USER_GEOGRAPHIES FUG,USER_GEOGRAPHIES FUGW,USERS PU,( SELECT user_id,parent_id,owner_id FROM USERS CONNECT BY PRIOR user_id = parent_id START WITH user_id=? ) X ");
		sb.append(" WHERE  ");
		sb.append("    NVL(CTRF.close_date,CTRF.created_on) >=  ? ");
		sb.append("   AND NVL(CTRF.close_date,CTRF.created_on) <=  ? ");
		sb.append("   AND CTRF.network_code = ? ");
		sb.append("  AND CTRF.TYPE = 'C2C' ");
		sb.append("    AND CTRF.transfer_category = CASE ? WHEN 'ALL' THEN CTRF.transfer_category  ELSE ? END  ");
		sb.append("    AND CTRF.control_transfer<>'A' ");
		sb.append("    AND CTRF.sender_category_code = SEND_CAT.category_code ");
		sb.append("    AND CTRF.receiver_category_code = REC_CAT.category_code ");
		sb.append("     AND CASE ?  WHEN 'OUT' THEN SEND_CAT.domain_code ELSE REC_CAT.domain_code END = ?  ");
		sb.append("    AND CASE ?  WHEN 'OUT' THEN CTRF.sender_category_code ELSE CTRF.receiver_category_code END = CASE ?  WHEN 'OUT' THEN (CASE ? WHEN 'ALL' THEN CTRF.sender_category_code ELSE ? END) ELSE (CASE ?  WHEN  'ALL' THEN CTRF.receiver_category_code ELSE ? END) END  ");
		sb.append(" AND CASE ? WHEN 'OUT' THEN CTRF.receiver_category_code ELSE CTRF.sender_category_code END = CASE ?  WHEN 'OUT' THEN (CASE ? WHEN 'ALL' THEN CTRF.receiver_category_code ELSE ?  END) ELSE (CASE ? WHEN  'ALL' THEN CTRF.sender_category_code ELSE ? END) END ");
		sb.append(" AND CASE ? WHEN 'OUT' THEN CTRF.from_user_id ELSE CTRF.to_user_id END = CASE ? WHEN 'OUT' THEN (CASE ? WHEN 'ALL' THEN from_user_id ELSE ? END) ELSE (CASE ? WHEN 'ALL' THEN CTRF.to_user_id ELSE ? END) END ");
		sb.append("   AND CASE ? WHEN 'OUT' THEN CTRF.to_user_id ELSE CTRF.from_user_id END = CASE ? WHEN 'OUT' THEN (CASE ? WHEN 'ALL' THEN to_user_id ELSE ? END) ELSE (CASE ? WHEN 'ALL' THEN CTRF.from_user_id ELSE ? END) END ");
		sb.append(" AND  (CTRF.FROM_USER_ID = X.user_id    OR  CTRF.to_USER_ID =  X.user_id ) ");
		sb.append("   AND UC.user_id = CTRF.ACTIVE_USER_ID ");
		sb.append("   AND UC.user_TYPE <> 'STAFF' ");
		sb.append("   AND FU.user_id = CTRF.from_user_id ");
		sb.append("    AND TU.user_id =CTRF.to_user_id ");
		sb.append("   AND FU.owner_id=FUGW.user_id ");
		sb.append("    AND FUGW.grph_domain_code=FWGD.grph_domain_code ");
		sb.append("    AND FU.user_id=FUG.user_id ");
		sb.append("      AND FUG.grph_domain_code=FUGD.grph_domain_code ");
		sb.append("   AND CTRF.transfer_id = CTI.transfer_id ");
		sb.append("    AND CTI.product_code = P.product_code ");
		sb.append("      AND FU.parent_id=PU.user_id(+) ");
		sb.append("   AND OU.user_id=FU.owner_id ");
		sb.append("    AND UGW.user_id=OU.user_id ");
		sb.append("     AND UGW.grph_domain_code=TWGD.grph_domain_code ");
		sb.append("    AND L.lookup_type ='TRFT' ");
		sb.append("   AND L.lookup_code = CTRF.transfer_sub_type ");
		sb.append("   AND L2.lookup_code =CTRF.transfer_category ");
		sb.append("   AND L2.lookup_type = 'TRFTY' ");
		sb.append("    AND CTRF.status = 'CLOSE' ");
		sb.append("    AND L1.lookup_code = CTRF.status ");
		sb.append("   AND L1.lookup_type = 'CTSTA' ");
		sb.append(" AND L3.lookup_type = 'SRTYP' ");
		sb.append(" AND L3.lookup_code= CTRF.REQUEST_GATEWAY_CODE ");
		sb.append(" AND L4.lookup_type = 'SRTYP' ");
		sb.append(" AND L4.lookup_code= CTRF.source ");
		sb.append("    AND CASE ? WHEN 'OUT' THEN CTRF.to_user_id ELSE CTRF.from_user_id END= UG.user_id  ");
		if(c2cTransferCommReqDTO.getDistributionType()!=null & (c2cTransferCommReqDTO.getDistributionType().trim().equals(PretupsI.VOUCHER_PRODUCT_O2C) )){
			if(!c2cTransferCommReqDTO.getTransferSubType().trim().equals(PretupsI.ALL)) { // In this case UI is sending values as 'V', INSTEAD OF 'ALL'
				sb.append(" AND CTRF.transfer_sub_type =? ");
			 }
		
		} else if(c2cTransferCommReqDTO.getDistributionType()!=null & (c2cTransferCommReqDTO.getDistributionType().trim().equals(PretupsI.STOCK) )) { 
			 //Incase of STOCK
			if(c2cTransferCommReqDTO.getTransferSubType().trim().equals("T,R,W,X")) { // In this case UI is sending values as 'T,R,W,X', INSTEAD OF 'ALL'
				sb.append(" AND CTRF.transfer_sub_type <> ? "); // CAN SUBSTIUTE ANY VALUE
			}else {
				//sbquery.append(" AND CTRF.transfer_sub_type = ? "); // CAN SUBSTIUTE ANY VALUE
				sb.append(" AND CTRF.transfer_sub_type = ? "); // CAN SUBSTIUTE ANY VALUE
			}
        
		} else {
			if(c2cTransferCommReqDTO.getTransferSubType()!=null  && !c2cTransferCommReqDTO.getTransferSubType().trim().equals(PretupsI.ALL)) {
				String trfArray[]=null;
				if (c2cTransferCommReqDTO.getTransferSubType()!=null && c2cTransferCommReqDTO.getTransferSubType().indexOf(",") > 0 ) {
					trfArray=c2cTransferCommReqDTO.getTransferSubType().split(",");
				}else {
					trfArray= new String[1];
					trfArray[0]=c2cTransferCommReqDTO.getTransferSubType();
				}
				CommonUtil commonUtil = new CommonUtil();
				String inclause =commonUtil.createQueryINclause(trfArray.length);
				
				log.debug("searchO2CTransferDetails", "Distribution type -> ALL selected");
				sb.append(" AND CTRF.transfer_sub_type IN ");
				sb.append(inclause);
			 
			}
			
		}	


		
		
		
		sb.append("     AND UG.grph_domain_code=TUGD.grph_domain_code ");
		sb.append("    AND UG.grph_domain_code IN (  ");
		sb.append(" SELECT grph_domain_code  "); 
		sb.append("  FROM    GEOGRAPHICAL_DOMAINS GD1 		WHERE status IN('Y','S') ");
		sb.append("     CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
		sb.append("     START WITH grph_domain_code IN ");
		sb.append("      (SELECT grph_domain_code  "); 
		sb.append("  FROM USER_GEOGRAPHIES UG1 ");
		sb.append("  WHERE UG1.grph_domain_code = (CASE ?  WHEN 'ALL' THEN UG1.grph_domain_code ELSE  ? END) ");
		sb.append("    AND UG1.user_id=?)) ");    
		
				
	  
		 
		} else {
			// if include staff details true;
			
			sb.append(" SELECT   (OU.user_name || ' (' || OU.msisdn||')')owner_profile, ");
			sb.append(" CASE WHEN ?=TO_USER_ID  THEN 'IN' ELSE 'OUT' END AS TransferINOUT, ");
			sb.append(" (PU.user_name ||' (' ||NVL(PU.msisdn,'ROOT')||')') parent_profile,CTRF.from_user_id, CTRF.to_user_id, U.user_name as fromUserName, CTRF.msisdn from_Msisdn, U2.user_name as ReceiverName,UC.user_name initiator_user,");	
			sb.append(" CTRF.to_msisdn as ReceiverMSISDN,CTRF.transfer_id,L.lookup_name transferSubType, CTRF.TYPE, ");
			sb.append("    CASE CTRF.transfer_sub_type ");
			sb.append("     WHEN 'V' THEN 'Voucher' ");
			sb.append("     ELSE 'Stock' ");
			sb.append("     END                    AS DISTRIBUTION_TYPE, ");
			sb.append("    L2.lookup_name   trf_cat_name, ");
			sb.append("   CTRF.status as TransactionStatus, ");
			sb.append("  CTRF.TRANSFER_ID as TRANSFER_ID,CTRF.transfer_date as TRANSFER_DATE,  ");
			sb.append("   CTRF.modified_ON modified_ON, P.product_name as productName, CTI.required_quantity transfer_mrp, CTI.payable_amount payableAmount, "); 
			sb.append("  CTI.net_payable_amount  netPayableAmount, L1.lookup_name status, ");
			sb.append("   CTI.mrp mrp, CTI.commission_value commission_value, ");
			sb.append("   CTI.otf_amount  otf_amount ,CTI.receiver_credit_quantity as  RECEIVER_CREDIT_QUANTITY, CTI.sender_debit_quantity as SENDER_DEBIT_QUANTITY, ");
			sb.append("    CTI.tax3_value tax3_Value, CTI.tax1_value tax1_value,CTI.tax2_value tax2_value, ");
			sb.append("      CTRF.sender_category_code, CTRF.receiver_category_code, ");
			sb.append("    SEND_CAT.category_name as senderCategoryName, REC_CAT.category_name as receiverCategoryName,CTRF.SOURCE as source, ");
			sb.append("    L3.lookup_name     AS request_Gateway_Desc, ");
			sb.append("    CTI.SENDER_PREVIOUS_STOCK     AS SENDER_PREVIOUS_STOCK, ");
			sb.append("   CTI.RECEIVER_PREVIOUS_STOCK     AS RECEIVER_PREVIOUS_STOCK,  ");
			sb.append("    CTI.SENDER_POST_STOCK     AS SENDER_POST_STOCK, ");
			sb.append("   CTI.RECEIVER_POST_STOCK     AS RECEIVER_POST_STOCK  ");
			
			sb.append("	 FROM CHANNEL_TRANSFERS CTRF, CHANNEL_TRANSFERS_ITEMS CTI,USERS U,USERS U2, USERS UC,PRODUCTS P,LOOKUPS L, LOOKUPS L1, CATEGORIES SEND_CAT, LOOKUPS L2, LOOKUPS L3, "); 
			sb.append("	   CATEGORIES REC_CAT,USER_GEOGRAPHIES UG,USERS PU,USERS OU ,( SELECT user_id,parent_id,owner_id FROM USERS CONNECT BY PRIOR user_id = parent_id START WITH user_id=? ) X ");
			sb.append("	   WHERE CTRF.TYPE = 'C2C' ");
			sb.append("	   AND NVL(CTRF.close_date,CTRF.created_on) >= ? ");
			sb.append("	     AND NVL(CTRF.close_date,CTRF.created_on) <=  ? ");	
			sb.append("	   AND CTRF.network_code = ? ");
			sb.append("    AND CTRF.transfer_category = CASE ? WHEN 'ALL' THEN CTRF.transfer_category  ELSE ? END  ");
			sb.append("	   AND CTRF.control_transfer<>'A' ");
			sb.append("   AND CASE ? WHEN 'OUT' THEN SEND_CAT.domain_code ELSE REC_CAT.domain_code END = ? ");
			sb.append("   AND CTRF.sender_category_code = SEND_CAT.category_code ");
			sb.append("     AND CTRF.receiver_category_code = REC_CAT.category_code ");
			sb.append("    AND CASE ? WHEN 'OUT' THEN CTRF.sender_category_code ELSE CTRF.receiver_category_code END = CASE ? WHEN 'OUT' THEN (CASE ? WHEN 'ALL' THEN CTRF.sender_category_code ELSE ? END) ELSE (CASE ?  WHEN  'ALL' THEN CTRF.receiver_category_code ELSE ? END) END ");
			sb.append("    AND CASE ? WHEN 'OUT' THEN CTRF.receiver_category_code ELSE CTRF.sender_category_code END = CASE ? WHEN 'OUT' THEN (CASE ? WHEN 'ALL' THEN CTRF.receiver_category_code ELSE ?  END) ELSE (CASE ?  WHEN  'ALL' THEN CTRF.sender_category_code ELSE ? END) END  ");
			sb.append("     AND CASE ? WHEN 'OUT' THEN CTRF.from_user_id ELSE CTRF.to_user_id END = CASE ? WHEN 'OUT' THEN (CASE ? WHEN 'ALL' THEN from_user_id ELSE ? END) ELSE (CASE ? WHEN 'ALL' THEN CTRF.to_user_id ELSE ? END) END ");
			sb.append("     AND CASE ? WHEN 'OUT' THEN CTRF.to_user_id ELSE CTRF.from_user_id END = CASE ? WHEN 'OUT' THEN (CASE ? WHEN 'ALL' THEN to_user_id ELSE ?  END) ELSE (CASE ? WHEN 'ALL' THEN CTRF.from_user_id ELSE ? END) END ");
			sb.append("    AND U.user_id = CTRF.from_user_id ");
			sb.append("    AND U2.user_id =CTRF.to_user_id ");
			sb.append(" AND  (CTRF.FROM_USER_ID = X.user_id    OR  CTRF.to_USER_ID =  X.user_id ) ");
			sb.append("    AND UC.user_id= CTRF.active_user_id ");
			sb.append("      AND CTRF.transfer_id = CTI.transfer_id ");
			sb.append("     AND CTI.product_code = P.product_code  ");
			sb.append("      AND U.parent_id=PU.user_id(+) ");
			sb.append("     AND OU.USER_ID=U.OWNER_ID ");
			sb.append("     AND L.lookup_type ='TRFT' ");
			sb.append("      AND L.lookup_code = CTRF.transfer_sub_type ");
			sb.append("    AND CTRF.status = 'CLOSE' ");
			sb.append("     AND L1.lookup_code = CTRF.status ");
			sb.append("     AND L1.lookup_type = 'CTSTA'  ");
			sb.append("   AND L2.lookup_code =CTRF.transfer_category ");
			sb.append("   AND L2.lookup_type = 'TRFTY' ");
			sb.append(" AND L3.lookup_type = 'SRTYP' ");
			sb.append(" AND L3.lookup_code= CTRF.REQUEST_GATEWAY_CODE ");
			sb.append("    AND CASE ? WHEN 'OUT' THEN CTRF.to_user_id ELSE CTRF.from_user_id END= UG.user_id ");
			
			if(c2cTransferCommReqDTO.getDistributionType()!=null & (c2cTransferCommReqDTO.getDistributionType().trim().equals(PretupsI.VOUCHER_PRODUCT_O2C) )){
				if(!c2cTransferCommReqDTO.getTransferSubType().trim().equals(PretupsI.ALL)) { // In this case UI is sending values as 'V', INSTEAD OF 'ALL'
					sb.append(" AND CTRF.transfer_sub_type =? ");
				 }
			
			} else if(c2cTransferCommReqDTO.getDistributionType()!=null & (c2cTransferCommReqDTO.getDistributionType().trim().equals(PretupsI.STOCK) )) { 
				 //Incase of STOCK
				if(c2cTransferCommReqDTO.getTransferSubType().trim().equals("T,R,W,X")) { // In this case UI is sending values as 'T,R,W,X', INSTEAD OF 'ALL'
					sb.append(" AND CTRF.transfer_sub_type <> ? "); // CAN SUBSTIUTE ANY VALUE
				}else {
					//sbquery.append(" AND CTRF.transfer_sub_type = ? "); // CAN SUBSTIUTE ANY VALUE
					sb.append(" AND CTRF.transfer_sub_type = ? "); // CAN SUBSTIUTE ANY VALUE
				}
	        
			} else {
				if(c2cTransferCommReqDTO.getTransferSubType()!=null  && !c2cTransferCommReqDTO.getTransferSubType().trim().equals(PretupsI.ALL)) {
					String trfArray[]=null;
					if (c2cTransferCommReqDTO.getTransferSubType()!=null && c2cTransferCommReqDTO.getTransferSubType().indexOf(",") > 0 ) {
						trfArray=c2cTransferCommReqDTO.getTransferSubType().split(",");
					}else {
						trfArray= new String[1];
						trfArray[0]=c2cTransferCommReqDTO.getTransferSubType();
					}
					CommonUtil commonUtil = new CommonUtil();
					String inclause =commonUtil.createQueryINclause(trfArray.length);
					
					log.debug("searchO2CTransferDetails", "Distribution type -> ALL selected");
					sb.append(" AND CTRF.transfer_sub_type IN ");
					sb.append(inclause);
				 
				}
				
			}	
			
			sb.append("       AND UG.grph_domain_code IN (  ");
			sb.append("   SELECT grph_domain_code  "); 
			sb.append("  FROM ");
			sb.append("    GEOGRAPHICAL_DOMAINS GD1 "); 
			sb.append("    WHERE status IN('Y','S') ");
			sb.append("     CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
			sb.append("    START WITH grph_domain_code IN  ");
			sb.append("     (SELECT grph_domain_code  "); 
			sb.append("  FROM USER_GEOGRAPHIES UG1  ");
			sb.append(" WHERE UG1.grph_domain_code = (CASE ? WHEN 'ALL' THEN UG1.grph_domain_code ELSE ? END) ");
			sb.append("   AND UG1.user_id=? ))  ");
			
			
			
		}
		
		if(!BTSLUtil.isEmpty(c2cTransferCommReqDTO.getTransferInout())   && c2cTransferCommReqDTO.getTransferInout().equals(PretupsI.IN) ) {
	  		sb.append(" and CTRF.TO_USER_ID  = ? ");	
	  	} else if(!BTSLUtil.isEmpty(c2cTransferCommReqDTO.getTransferInout())   && c2cTransferCommReqDTO.getTransferInout().equals(PretupsI.OUT) ) {
	  		sb.append(" and CTRF.TO_USER_ID  <>  ? ");	
	   } else {
		   log.debug("getC2CTransferCommissiondetails -> Transfer IN/OUT/ALL ->", c2cTransferCommReqDTO.getTransferInout());	   
	   }
		
		if(!BTSLUtil.isEmpty(c2cTransferCommReqDTO.getSenderMobileNumber())) {
	  		sb.append(" and CTRF.msisdn  = ? ");	
	  	}
	  	if(!BTSLUtil.isEmpty(c2cTransferCommReqDTO.getReceiverMobileNumber())) {
	  		sb.append(" and   CTRF.to_msisdn = ?  ");
	  	}
		sb.append(" ORDER BY CTRF.transfer_date Desc ");
if(log.isDebugEnabled()) {
	log.debug("getC2CTransferCommissiondetails", sb.toString());
}
		
		return sb.toString();
	}

	@Override
	public String getO2CTransferAcknowldgementDetails(O2CTransfAckDownloadReqDTO getO2CTransfAcknReqVO) {
		 StringBuilder sbquery = new StringBuilder();
	      
		 if(getO2CTransfAcknReqVO.getDistributionType()!=null & getO2CTransfAcknReqVO.getDistributionType().trim().equals(PretupsI.VOUCHER) ) {	
	      
	      sbquery.append("	SELECT CT.transfer_id  , CT.from_user_id,CT.to_user_id, G.grph_domain_name, D.domain_name, n.network_name, "); 
	      sbquery.append("   (case  when CT.receiver_category_code='OPT' then C1.category_name when CT.sender_category_code='OPT' then C.category_name end) category_name, ");
	      sbquery.append("	   CT.transfer_type,U.user_name, CT.ext_txn_no, TO_CHAR(CT.ext_txn_date,'dd/mm/yyy') ext_txn_date, CPS.comm_profile_set_name, TP.profile_name, ");
	      sbquery.append("		   CT.transfer_date transfer_date, L.lookup_name transfer_category,CT.reference_no, ");
	      sbquery.append("   U.msisdn ,U.address1,U.address2,U.city,U.state,U.country, U.external_code, ");
	      sbquery.append("   P.product_name, P.product_short_code, CTI.product_code, ");
	      sbquery.append("	   CTI.commission_rate, CTI.commission_type, CTI.commission_value, CTI.mrp, CTI.net_payable_amount, CTI.payable_amount,CTI.user_unit_price, "); 
	      sbquery.append("		   CTI.required_quantity, CTI.tax1_rate, CTI.tax1_type, CTI.tax1_value, CTI.tax2_rate, CTI.tax2_type, CTI.tax2_value, CTI.tax3_value,CTI.otf_type,CTI.otf_rate,CTI.otf_amount, ");
	      sbquery.append("		   CTI.commision_quantity,CTI.receiver_credit_quantity,CTI.sender_debit_quantity, ");
	      sbquery.append("	   CT.pmt_inst_no,TO_CHAR(CT.pmt_inst_date,'dd/mm/yyy') pmt_inst_date, CT.pmt_inst_amount, CT.source, CT.pmt_inst_source , ");
	      sbquery.append("	   CT.first_approver_remarks, CT.second_approver_remarks, CT.third_approver_remarks, CT.channel_user_remarks, ");
	      sbquery.append("	  APPU1.user_name first_approved_by, TO_CHAR(CT.first_approved_on, 'dd/mm/yyy') first_approved_on, "); 
	      sbquery.append("	   CT.first_level_approved_quantity, CT.second_level_approved_quantity, CT.third_level_approved_quantity,CTI.approved_quantity, ");
	      sbquery.append("		   APPU5.user_name transfer_initiated_by, L1.lookup_name status, L2.lookup_name transfer_sub_type , L3.lookup_name pmt_inst_type,VB.batch_no,VP.product_name voms_product_name,VB.batch_type,L4.lookup_name   batch_Type_desc,VB.total_no_of_vouchers,VB.from_serial_no,VB.to_serial_no "); 
	      sbquery.append("	 FROM channel_transfers CT, geographical_domains G, categories C ,  categories C1 ,users U , commission_profile_set CPS, domains D , ");
	      sbquery.append("	   transfer_profile TP ,transfer_profile TP2, users APPU1, users APPU2 ,users APPU3 , users APPU5 , users APPU6, ");
	      sbquery.append("		   lookups L, lookups L1, lookups L2, lookups L3,lookups L4, channel_transfers_items CTI, products P,voms_batches VB,voms_products VP,networks n ");
	      sbquery.append("	      WHERE   CT.transfer_id = ? "); 
	      sbquery.append("		   and CT.transfer_id = CTI.transfer_id ");
	      sbquery.append("	   AND CTI.product_code = P.product_code ");
	      sbquery.append("		   AND CT.network_code = ?  "); 
	      sbquery.append("		   AND n.network_code = CT.network_code  ");
	      sbquery.append("		   AND CT.domain_code = D.domain_code "); 
	      sbquery.append("		   AND CT.grph_domain_code = G.grph_domain_code "); 
	      sbquery.append("		  AND CT.receiver_category_code = C.category_code "); 
	      sbquery.append("		   AND CT.sender_category_code = C1.category_code  "); 
	      sbquery.append("		   AND (case CT.from_user_id when 'OPT' then  CT.to_user_id end = U.user_id  OR  case CT.to_user_id when 'OPT' then  CT.from_user_id end = U.user_id)   "); 
	      sbquery.append("				   AND CT.commission_profile_set_id =  CPS.comm_profile_set_id   "); 
	      sbquery.append("	   AND CT.receiver_txn_profile = TP.profile_id(+)  "); 
	      sbquery.append("		   AND CT.sender_txn_profile = TP2.profile_id(+) "); 
	      sbquery.append("		   AND CT.first_approved_by = APPU1.user_id(+)  ");  
	      sbquery.append("		   AND CT.second_approved_by = APPU2.user_id(+)  ");
	      sbquery.append("		   AND CT.third_approved_by = APPU3.user_id(+)  ");
	      sbquery.append("		   AND CT.transfer_initiated_by = APPU5.user_id(+)  "); 
	      sbquery.append("		 	   AND CT.modified_by = APPU6.user_id(+)  "); 
	      sbquery.append("		   AND CT.transfer_category = L.lookup_code(+)  ");
	      sbquery.append("			   AND L.lookup_type(+) = 'TRFTY'  ");
	      sbquery.append("	   AND CT.status = L1.lookup_code(+)  ");
	      sbquery.append("	   AND L1.lookup_type(+) = 'TSTAT'  ");
	      sbquery.append("	   AND CT.transfer_sub_type =  L2.lookup_code(+)  ");
	      sbquery.append("		   AND L2.lookup_type(+) = 'TRFT'  ");
	      sbquery.append("		   AND CT.pmt_inst_type =  L3.lookup_code(+)  ");
	      sbquery.append("		   AND L3.lookup_type(+) = 'PMTYP'  ");
	      sbquery.append("		   AND VB.BATCH_TYPE = L4.lookup_code(+) ");
	      sbquery.append("	       AND L4.lookup_type(+) = 'VSTAT' ");
	      sbquery.append("	   AND CT.TRANSFER_SUB_TYPE ='V'  ");
	      sbquery.append("		   AND CT.transfer_id = VB.ext_txn_no(+)  ");
	      sbquery.append("		   AND  VB.product_id = VP.product_id(+)  ");
//	      sbquery.append("		   AND  VB.product_id = VP.product_id(+)  ");
	      //sbquery.append("	       AND  VC.CATEGORY_ID = VP.CATEGORY_ID  ");
		 } else {
			 
			 sbquery.append("    SELECT CT.from_user_id,CT.to_user_id,CT.transfer_id, G.grph_domain_name, D.domain_name, n.network_name as network_name ,"); 
			 sbquery.append("     (case  when CT.receiver_category_code='OPT' then C1.category_name when CT.sender_category_code='OPT' then C.category_name end) category_name, ");
			 sbquery.append("    CT.transfer_type,U.user_name, CT.ext_txn_no, CT.ext_txn_date ext_txn_date, CPS.comm_profile_set_name, TP.profile_name, ");
			 sbquery.append("   CT.transfer_date transfer_date, L.lookup_name transfer_category,CT.reference_no, ");
			 sbquery.append("   U.msisdn ,U.address1,U.address2,U.city,U.state,U.country, U.external_code, ");
			 sbquery.append("    P.product_name, P.product_short_code, CTI.product_code, ");
			 sbquery.append("    CTI.commission_rate, CTI.commission_type, CTI.commission_value, CTI.mrp, CTI.net_payable_amount, CTI.payable_amount,CTI.user_unit_price, "); 
			 sbquery.append("    CTI.required_quantity, CTI.tax1_rate, CTI.tax1_type, CTI.tax1_value, CTI.tax2_rate, CTI.tax2_type, CTI.tax2_value, CTI.tax3_value, CTI.otf_type, CTI.otf_rate, CTI.otf_amount, ");
			 sbquery.append("  CTI.commision_quantity,CTI.receiver_credit_quantity,CTI.sender_debit_quantity, ");
			 sbquery.append("    CT.pmt_inst_no,CT.pmt_inst_date pmt_inst_date, CT.pmt_inst_amount, CT.source, CT.pmt_inst_source , ");
			 sbquery.append("    CT.first_approver_remarks, CT.second_approver_remarks, CT.third_approver_remarks, CT.channel_user_remarks, ");
			 sbquery.append("     APPU1.user_name first_approved_by, CT.first_approved_on first_approved_on, "); 
			 sbquery.append("   CT.first_level_approved_quantity, CT.second_level_approved_quantity, CT.third_level_approved_quantity,CTI.approved_quantity, ");
			 sbquery.append("     APPU5.user_name transfer_initiated_by, L1.lookup_name status, L2.lookup_name transfer_sub_type , L3.lookup_name pmt_inst_type ");
			 sbquery.append("  FROM channel_transfers CT "); 
			 sbquery.append("    left join transfer_profile TP on CT.receiver_txn_profile = TP.profile_id ");
			 sbquery.append("   left join  transfer_profile TP2 on CT.sender_txn_profile = TP2.profile_id ");
			 sbquery.append("    left join  users APPU1 on CT.first_approved_by = APPU1.user_id ");
			 sbquery.append("    left join  users APPU2 on  CT.second_approved_by = APPU2.user_id "); 
			 sbquery.append("      left join  users APPU3 on CT.third_approved_by = APPU3.user_id ");
			 sbquery.append("    left join  users APPU5  on CT.transfer_initiated_by = APPU5.user_id ");
			 sbquery.append("     left join  users APPU6 on CT.modified_by = APPU6.user_id ");
			 sbquery.append("        left join lookups L on CT.transfer_category = L.lookup_code and L.lookup_type = 'TRFTY' ");
			 sbquery.append("   left join lookups L1 on CT.status = L1.lookup_code  AND L1.lookup_type = 'TSTAT' ");
			 sbquery.append("    left join lookups L2 on CT.transfer_sub_type =  L2.lookup_code AND L2.lookup_type = 'TRFT' ");
			 sbquery.append("    left join  lookups L3 on CT.pmt_inst_type =  L3.lookup_code   AND L3.lookup_type = 'PMTYP' ");
			 sbquery.append("   , geographical_domains G, categories C ,  categories C1 ,users U , commission_profile_set CPS, domains D ");  
			 sbquery.append("    , channel_transfers_items CTI, products P ,networks n ");
			 sbquery.append("     WHERE    CT.transfer_id =? "); 
			 sbquery.append("       AND CT.transfer_id = CTI.transfer_id ");
			 sbquery.append("      AND CTI.product_code = P.product_code ");
			 sbquery.append("    AND CT.network_code = ? "); 
			 sbquery.append("    AND n.network_Code = CT.network_code  ");
			 sbquery.append("   AND CT.domain_code = D.domain_code "); 
			 sbquery.append("      AND CT.grph_domain_code = G.grph_domain_code "); 
			 sbquery.append("   AND CT.receiver_category_code = C.category_code "); 
			 sbquery.append("     AND CT.sender_category_code = C1.category_code "); 
			 sbquery.append("     AND (case CT.from_user_id when 'OPT' then  CT.to_user_id end = U.user_id  OR "); 
			 sbquery.append("     case CT.to_user_id when 'OPT' then  CT.from_user_id end = U.user_id) "); 
			 sbquery.append("     AND CT.commission_profile_set_id =  CPS.comm_profile_set_id ");
			 sbquery.append("	   AND CT.TRANSFER_SUB_TYPE <> 'V'  ");
		 }

		 if(log.isDebugEnabled()) {
				log.debug("getO2CTransferAcknowldgementDetails", sbquery.toString());
			}	

			return sbquery.toString();


	}

	@Override
	public String searchO2CTransferDetails(O2CTransferDetailsReqDTO o2CTransferDetailsReqDTO) {
		StringBuilder sbquery = new StringBuilder();
		
		
		
		sbquery.append("   SELECT CTRF.from_user_id,CTRF.CREATED_ON, ");
		sbquery.append("   CTRF.to_user_id, ");
		sbquery.append("   U.user_name            from_user, ");
		sbquery.append("   U.msisdn               from_msisdn, ");
		sbquery.append(" U2.user_name           to_user, ");
		sbquery.append("   U2.msisdn              to_msisdn, ");
		sbquery.append("   CTRF.transfer_id, ");
		sbquery.append("   L.lookup_name          transfer_sub_type, ");
		sbquery.append("   CTRF.transfer_sub_type trf_sub_type, ");
		sbquery.append("    CTRF.TYPE, ");
		sbquery.append("    CASE CTRF.transfer_sub_type ");
		sbquery.append("     WHEN 'V' THEN 'Voucher' ");
		sbquery.append("     ELSE 'Stock' ");
		sbquery.append("     END                    AS DISTRIBUTION_TYPE, ");
		sbquery.append("    CTI.transfer_date     transfer_date, ");
		sbquery.append("    CTRF.close_date     close_date, ");
		sbquery.append("    CTRF.modified_on       modified_on, ");
		sbquery.append("    CTRF.transfer_date     txn_date, ");
		sbquery.append("     P.product_name, ");
		sbquery.append("    CTI.commission_value, ");
		sbquery.append("    CTRF.REQUEST_GATEWAY_CODE,  ");
		sbquery.append("    CTI.required_quantity  requested_quantity ,");
		sbquery.append("    CTI.OTF_AMOUNT CBC_AMOUNT, ");
		sbquery.append("    CTI.payable_amount, ");
		sbquery.append("    CTI.tax1_value, ");
		sbquery.append("    CTI.tax2_value, ");
		sbquery.append("    CTI.tax3_value, ");
		sbquery.append("    CTI.net_payable_amount, ");
		sbquery.append("    CTI.required_quantity, ");
		sbquery.append("   CTI.mrp, ");
		sbquery.append("    L2.lookup_name   trf_cat_name, ");
		sbquery.append("   CTI.commision_quantity, ");
		sbquery.append("  CTI.receiver_credit_quantity, ");
		sbquery.append("   CTI.sender_debit_quantity, ");
		sbquery.append("    L1.lookup_name         status, ");
		sbquery.append("     CTRF.domain_code, ");
		sbquery.append("     D.domain_name, ");
		sbquery.append("   CTRF.transfer_category, ");
		sbquery.append("   CTRF.ext_txn_date      ext_txn_date, ");
		sbquery.append("    CTRF.ext_txn_no, ");
		sbquery.append("    CTI.first_level_approved_qty, ");
		sbquery.append("    CTI.second_level_approved_qty, ");
		//sbquery.append("     CTI.second_level_approved_qty, ");  // No third level in DB coulumn
		sbquery.append("    CTI.approved_quantity, ");
		sbquery.append("    CTRF.channel_user_remarks, ");
		sbquery.append("    CTRF.first_approver_remarks, ");
		sbquery.append("  CTRF.second_approver_remarks, ");
		sbquery.append("    CTRF.third_approver_remarks, ");
		sbquery.append("     CTRF.request_gateway_type, ");
		sbquery.append("   CTRF.pmt_inst_type, ");
		sbquery.append("    CTRF.pmt_inst_no, ");
		sbquery.append("   CTRF.pmt_inst_date     pmt_inst_date, ");
		sbquery.append("    n.network_name         AS networkName, ");
		sbquery.append(" L5.LOOKUP_NAME AS TRANSACTION_MODE, ");
		sbquery.append("    VB.batch_no AS BATCH_NO , ");
		sbquery.append("    VP.product_name as voms_product_name, ");
		sbquery.append("    VB.batch_type AS batch_type, ");
		sbquery.append("    L3.lookup_name     AS batch_Type_desc, ");
		sbquery.append("   VB.total_no_of_vouchers  AS total_no_of_vouchers, ");
		sbquery.append("    VB.from_serial_no  AS from_serial_no, ");
		sbquery.append("    VB.to_serial_no  AS to_serial_no, ");
		sbquery.append("    VC.voucher_type AS voucher_type, ");
		sbquery.append("    VC.voucher_segment AS voucher_segment, ");
		sbquery.append("  	L4.lookup_name AS VOUCHER_SEGMENT_NAME, ");
		sbquery.append("   VC.mrp                 AS VOUCHER_DENOMINATION, ");
		sbquery.append("    CTRF.transaction_mode as trans_mode, ");
		sbquery.append("    L6.lookup_name     AS request_Gateway_Desc, ");
		sbquery.append(" CTI.receiver_previous_stock receiver_PRETVIOUS_BALANCE, ");
		sbquery.append(" CTI.receiver_post_stock receiver_POST_BALANCE ");
		sbquery.append("  FROM   channel_transfers CTRF, ");
		sbquery.append("    channel_transfers_items CTI, ");
		sbquery.append("   users U, ");
		sbquery.append("    users U2, ");
		sbquery.append("    products P, ");
		sbquery.append("    lookups L, ");
		sbquery.append("   lookups L1, ");
		sbquery.append("    domains D, categories C,lookups L2,networks n,lookups L3,voms_batches VB, voms_products VP, voms_categories VC,lookups L4,LOOKUPS L5,LOOKUPS L6, ");
		sbquery.append(" ( SELECT user_id,parent_id,owner_id FROM USERS CONNECT BY PRIOR user_id = parent_id START WITH user_id=? ) X  ");
		sbquery.append("  WHERE    CTI.transfer_date >= ? ");
		sbquery.append("    AND  CTI.transfer_date <=  ? ");
		sbquery.append("    AND CTRF.network_code = ? ");
		sbquery.append(" AND 	  CTRF.TYPE = 'O2C' ");
		sbquery.append(" AND ( (CTRF.FROM_USER_ID = X.user_id AND CTRF.TO_USER_ID = 'OPT' )   OR  (CTRF.to_USER_ID =  X.user_id AND CTRF.FROM_USER_ID = 'OPT' ) ) ");
		sbquery.append("    AND CTRF.transfer_category = CASE ? WHEN 'ALL' THEN CTRF.transfer_category  ELSE ? END  ");
		sbquery.append("   AND n.network_code = CTRF.network_code ");
		sbquery.append("   AND CTRF.domain_code IN ( ? ) ");
		sbquery.append("   AND CTRF.domain_code = D.domain_code ");
		sbquery.append("   AND L2.lookup_type = 'TRFTY' ");
		sbquery.append("   AND L2.lookup_code=CTRF.transfer_category ");
		sbquery.append("   AND ( CTRF.receiver_category_code = CASE ? WHEN 'ALL' THEN CTRF.receiver_category_code  ELSE ? END    ");
		sbquery.append("    OR CTRF.sender_category_code = CASE ? WHEN 'ALL' THEN CTRF.sender_category_code ELSE ?  END )  ");
		sbquery.append(" AND (CTRF.to_user_id = CASE ? WHEN 'ALL' THEN CTRF.to_user_id  ELSE ?  END ");
		sbquery.append("  OR CTRF.from_user_id = CASE ?  WHEN 'ALL' THEN CTRF.from_user_id   ELSE ? END ) ");
		sbquery.append(" AND CTRF.transfer_sub_type = CASE 'ALL' WHEN 'ALL' THEN CTRF.transfer_sub_type ELSE 'ALL'   END ");
		sbquery.append("   AND U.user_id(+) = CASE CTRF.from_user_id  WHEN 'OPT' THEN '' ELSE CTRF.from_user_id   END   AND U2.user_id(+) = CASE CTRF.to_user_id WHEN 'OPT' THEN ''  ELSE CTRF.to_user_id  END ");
		sbquery.append("   AND C.category_code = CTRF.receiver_category_code ");
		sbquery.append("   AND CTRF.transfer_id = CTI.transfer_id ");
		sbquery.append("   AND CTI.product_code = P.product_code ");
		sbquery.append("   AND L.lookup_type(+) = 'TRFT' ");
		sbquery.append("    AND L.lookup_code(+) = CTRF.transfer_sub_type ");
		sbquery.append(" AND L5.lookup_type(+) = 'TXMOD' ");
		sbquery.append(" AND L5.lookup_code(+) = CTRF.TRANSACTION_MODE ");
		sbquery.append(" AND L6.lookup_type(+) = 'SRTYP' ");
		sbquery.append(" AND L6.lookup_code(+) = CTRF.REQUEST_GATEWAY_TYPE ");
		sbquery.append("    AND CTRF.status = 'CLOSE' ");
		sbquery.append("   AND L1.lookup_code(+) = CTRF.status ");
		sbquery.append("   AND L1.lookup_type(+) = 'CTSTA' ");
		sbquery.append("   AND VB.batch_type = L3.lookup_code(+) ");
		sbquery.append("    AND L3.lookup_type(+) = 'VSTAT' ");
		sbquery.append("   AND VC.voucher_segment = L4.lookup_code(+) ");
		sbquery.append("   AND L4.lookup_type(+) = 'VMSSEG' ");
		sbquery.append(" AND VB.ext_txn_no(+)  = CTRF.TRANSFER_ID ");
		sbquery.append(" AND   VP.product_id(+)= VB.product_id ");
		sbquery.append(" AND VC.category_id(+) = VP.category_id ");
		
     	if(o2CTransferDetailsReqDTO.getDistributionType()!=null & (o2CTransferDetailsReqDTO.getDistributionType().trim().equals(PretupsI.VOUCHER_PRODUCT_O2C) )){
			if(!o2CTransferDetailsReqDTO.getTransferSubType().trim().equals(PretupsI.ALL)) { // In this case UI is sending values as 'V', INSTEAD OF 'ALL'
				   sbquery.append(" AND CTRF.transfer_sub_type =? ");
			 }
		
		} else if(o2CTransferDetailsReqDTO.getDistributionType()!=null & (o2CTransferDetailsReqDTO.getDistributionType().trim().equals(PretupsI.STOCK) )) { 
			 //Incase of STOCK
			if(o2CTransferDetailsReqDTO.getTransferSubType().trim().equals("T,R,W,X")) { // In this case UI is sending values as 'T,R,W,X', INSTEAD OF 'ALL'
			   sbquery.append(" AND CTRF.transfer_sub_type <> ? "); // CAN SUBSTIUTE ANY VALUE
			}else {
				//sbquery.append(" AND CTRF.transfer_sub_type = ? "); // CAN SUBSTIUTE ANY VALUE
				sbquery.append(" AND CTRF.transfer_sub_type = ? "); // CAN SUBSTIUTE ANY VALUE
			}
        
		} else {
			if(o2CTransferDetailsReqDTO.getTransferSubType()!=null  && !o2CTransferDetailsReqDTO.getTransferSubType().trim().equals(PretupsI.ALL)) {
				String trfArray[]=null;
				if (o2CTransferDetailsReqDTO.getTransferSubType()!=null && o2CTransferDetailsReqDTO.getTransferSubType().indexOf(",") > 0 ) {
					trfArray=o2CTransferDetailsReqDTO.getTransferSubType().split(",");
				}else {
					trfArray= new String[1];
					trfArray[0]=o2CTransferDetailsReqDTO.getTransferSubType();
				}
				CommonUtil commonUtil = new CommonUtil();
				String inclause =commonUtil.createQueryINclause(trfArray.length);
				
				log.debug("searchO2CTransferDetails", "Distribution type -> ALL selected");
				sbquery.append(" AND CTRF.transfer_sub_type IN ");
				sbquery.append(inclause);
			 
			}
			
		}	

		
		
		
		sbquery.append(" AND CTRF.grph_domain_code IN (SELECT GD1.grph_domain_code ");
		sbquery.append("     FROM   geographical_domains GD1 ");
		sbquery.append("     WHERE  GD1.status IN( 'Y', 'S' ) ");
		sbquery.append("   CONNECT BY PRIOR GD1.grph_domain_code = ");
		sbquery.append(" GD1.parent_grph_domain_code ");
		sbquery.append("  START WITH GD1.grph_domain_code IN ");
		sbquery.append("  (SELECT UG1.grph_domain_code ");
		sbquery.append(" FROM   user_geographies UG1 ");
		sbquery.append(" WHERE  UG1.grph_domain_code = CASE ? ");
		sbquery.append(" WHEN 'ALL' THEN UG1.grph_domain_code ");
		sbquery.append(" ELSE ?  END 	AND UG1.user_id =? ))  ORDER BY 12 DESC ");  

		
			if(log.isDebugEnabled()) {
			log.debug("searchO2CTransferDetails", sbquery.toString());
		}
			
		
		return sbquery.toString();
	}
	
	@Override
	public String loadChannelTransferDetailsTcpQry(boolean p_chnlTxnMrpBlockTimeoutAllowed,boolean p_requestGatewayCodeCheckRequired,ChannelTransferVO p_channelTransferVO) {
		 StringBuilder strBuff = new StringBuilder(" SELECT ct.transfer_id, ct.first_level_approved_quantity, ");
	 		strBuff.append(" ct.second_level_approved_quantity, ct.third_level_approved_quantity, ct.network_code, ct.network_code_for, ");
	 		strBuff.append(" ct.grph_domain_code, ct.domain_code, ct.sender_category_code, ct.sender_grade_code, ");
	         strBuff.append(" ct.receiver_grade_code, ct.from_user_id, ct.to_user_id, ct.transfer_date, ct.close_date, ct.reference_no, ");
	 		strBuff.append(" ct.ext_txn_no, ct.ext_txn_date,ct.transfer_category,ct.commission_profile_set_id, ");
	         strBuff.append(" ct.commission_profile_ver, ct.requested_quantity, ct.channel_user_remarks, ");
	         strBuff.append(" ct.first_approver_remarks, ct.second_approver_remarks, ct.third_approver_remarks, ");
	         strBuff.append(" ct.first_approved_by, ct.first_approved_on, ct.second_approved_by, ct.second_approved_on, ");
	         strBuff.append(" ct.third_approved_by, ct.third_approved_on, ct.cancelled_by, ct.cancelled_on, ");
	 		strBuff.append(" ct.transfer_initiated_by, ct.modified_by, ct.modified_on, ct.status, ct.type, ");
	 		strBuff.append(" ct.transfer_mrp, ct.first_approver_limit, ct.second_approver_limit,  ");
	 		strBuff.append(" ct.payable_amount, ct.net_payable_amount, ct.batch_no, ct.batch_date, ct.pmt_inst_type, ct.pmt_inst_no, ");
	 		strBuff.append(" ct.pmt_inst_date, ct.pmt_inst_amount, ct.sender_txn_profile, ct.receiver_txn_profile, ct.total_tax1, ct.total_tax2,  ");
	 		strBuff.append(" ct.total_tax3, ct.source, ct.receiver_category_code, ct.request_gateway_code, ct.request_gateway_type, ct.pmt_inst_source ,");
	 		strBuff.append(" ct.product_type , ct.sms_default_lang,ct.sms_second_lang, d.domain_name , g.grph_domain_name, c.category_name , cg.grade_name, u.user_name,u.address1,u.address2,u.city,u.state,u.country,cps.comm_profile_set_name, ");
	 		strBuff.append(" appu1.user_name firstapprovedby,appu2.user_name secondapprovedby,appu3.user_name thirdapprovedby, ");
	 		strBuff.append(" appu4.user_name cancelledby,appu5.user_name initatedby , u.msisdn ,ct.msisdn from_msisdn,ct.to_msisdn to_msisdn, u.external_code , appu6.user_name fromusername ,ct.transfer_type,ct.transfer_sub_type, ");
	 		strBuff.append(" cti.commision_quantity,cti.sender_debit_quantity,cti.receiver_credit_quantity,");
	 		strBuff.append(" ct.txn_wallet,ct.control_transfer,ct.to_grph_domain_code,ct.to_domain_code,ct.created_on,ct.created_by,ct.active_user_id,");
	 		strBuff.append(" cti.user_wallet");
	 		strBuff.append(" FROM ");
	 		strBuff.append(" channel_transfers ct, geographical_domains g, categories c , users u , commission_profile_set cps, domains d , ");
	 		strBuff.append("  users appu1, users appu2 ,users appu3 , users appu4 ,users appu5 ,channel_grades cg ,users appu6 ,channel_transfers_items cti");
	 		strBuff.append(" WHERE ");
	 		strBuff.append(" ct.from_user_id = ? AND ct.to_user_id = ? AND ");
	 		if(p_chnlTxnMrpBlockTimeoutAllowed) {
	 			strBuff.append(" ct.transfer_mrp = ? AND ");
	        }
	        if(p_requestGatewayCodeCheckRequired) {
	         	strBuff.append(" ct.request_gateway_code = ? AND ");
	         	strBuff.append(" ct.request_gateway_type = ? AND ");
	        }
	        strBuff.append(" ct.status = ? AND ");
	        if(!BTSLUtil.isNullString(p_channelTransferVO.getProductType())) {
	         	strBuff.append(" ct.product_type = ? AND ");
	        }
	        strBuff.append(" ct.type = ? AND ");
	        strBuff.append(" ct.transfer_category = ? AND ");
	        strBuff.append(" ct.transfer_type = ? AND ");
	        strBuff.append(" ct.transfer_sub_type = ? AND ");
	 		strBuff.append(" ct.transfer_id=cti.transfer_id AND ct.network_code = ? AND ct.network_code_for = ? AND ");
	 		strBuff.append(" ct.domain_code = d.domain_code AND ct.grph_domain_code = g.grph_domain_code AND ct.receiver_category_code = c.category_code AND ");
	 		if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_channelTransferVO.getTransferSubType())) {
	 			strBuff.append(" ct.to_user_id  = u.user_id AND ct.receiver_grade_code = cg.grade_code   AND ");
	 		}
	 		else{
	 			strBuff.append(" ct.from_user_id  = u.user_id AND ct.sender_grade_code = cg.grade_code   AND ");
	 		}
	 		strBuff.append(" ct.commission_profile_set_id = cps.comm_profile_set_id AND ");
	        strBuff.append(" ct.first_approved_by = appu1.user_id(+)  AND ct.second_approved_by = appu2.user_id(+) AND ct.third_approved_by = appu3.user_id(+) AND ");
	        strBuff.append(" ct.cancelled_by = appu4.user_id(+) AND ");
	        strBuff.append(" ct.transfer_initiated_by = appu5.user_id(+) AND ");
	        strBuff.append(" ct.from_user_id = appu6.user_id(+)  ");
	        //strBuff.append(" AND rownum <=2 ");
	        strBuff.append(" order by ct.transfer_id desc, ct.close_date desc, ct.transfer_date desc ");
	        return strBuff.toString();
	}
	
	
	@Override
	public StringBuilder loadEnquiryO2cListQry( String isPrimary, String searchBy, String p_transferID,String p_userID, Date p_fromDate, Date p_toDate, String p_status, String[] p_transferSubTypeCodeArr, String p_userCode,  String p_transferCategory,String userType)
	{
		
	    final StringBuilder strBuff = new StringBuilder(" SELECT LKP.lookup_name,ct.transfer_sub_type,ct.requested_quantity,ct.transfer_type,");
        strBuff.append(" gd.grph_domain_name,gd.grph_domain_code ,ct.transfer_id, ct.network_code, ct.network_code_for, ");
        strBuff.append(" ct.transfer_date, ct.first_approved_by ,ct.first_approved_on, ct.second_approved_by, ct.dual_comm_type, ");
        strBuff.append(" ct.second_approved_on, ct.third_approved_by,ct.third_approved_on, ct.cancelled_by,  ");
        strBuff.append(" ct.cancelled_on, ct.modified_by, ct.modified_on, ct.status,ct.type, ct.payable_amount, ");
        strBuff.append(" ct.net_payable_amount, u.user_name, appu1.user_name firstapprovedby,appu2.user_name ");
        strBuff.append(" secondapprovedby,appu3.user_name thirdapprovedby, appu4.user_name cancelledby,u.msisdn,ct.msisdn from_msisdn,ct.to_msisdn to_msisdn, ");
        strBuff.append(" ct.transfer_category,ct.ext_txn_date, ct.ext_txn_no,ct.to_user_id,ct.from_user_id,ct.domain_code, ");
        strBuff.append(" ct.first_level_approved_quantity, ct.second_level_approved_quantity, ct.third_level_approved_quantity,ct.channel_user_remarks, ");
//        strBuff.append(" cti.product_code, ");
        strBuff.append(" ct.PMT_INST_TYPE, ct.transaction_mode");
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue())  {
            strBuff.append(",ct.SOS_STATUS, ct.SOS_SETTLEMENT_DATE");
            }
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_TRANSFERS_INFO_REQUIRED))).booleanValue())
        {
        	strBuff.append(",ct.info1,ct.info2 ");
        }
		/*strBuff.append(",vb.batch_no,vp.product_name,vb.batch_type,vb.total_no_of_vouchers,vb.from_serial_no,vb.to_serial_no ");*/	
		strBuff.append(" ,ct.reference_no, ct.source, ct.created_by, ct.created_on, ct.control_transfer  ");
        strBuff.append(" FROM channel_transfers ct , users u,lookups LKP, ");
        if (!BTSLUtil.isNullString(isPrimary) && ("N".equalsIgnoreCase(isPrimary))) {
            strBuff.append(" user_phones up, ");
        }
        strBuff.append(" users appu1, users appu2, users appu3, users appu4 , user_geographies ug,geographical_domains gd");
        /*strBuff.append(" ,voms_batches vb,voms_products vp ");*/
//        strBuff.append(" , products p ");
        strBuff.append(" WHERE ");
        if (PretupsI.SEARCH_BY_TRANSACTIONID.equalsIgnoreCase(searchBy)) {
            strBuff.append(" ct.transfer_id = ? AND ");
            strBuff.append(" (u.user_id=ct.to_user_id OR u.user_id=ct.from_user_id) AND ");
            
        } else if (PretupsI.SEARCH_BY_MSISDN.equalsIgnoreCase(searchBy)) {
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
            
			if (!BTSLUtil.isNullorEmpty(p_transferSubTypeCodeArr) && !PretupsI.ALL.equalsIgnoreCase( p_transferSubTypeCodeArr[0])) {
				strBuff.append(" ( ct.transfer_sub_type = ?");
				for (int i = 1; i < p_transferSubTypeCodeArr.length; i++) {
					strBuff.append(" OR ct.transfer_sub_type = ?");
				}

				strBuff.append(" ) AND ");
			}
			
            strBuff.append(" u.status IN (" + PretupsBL.userStatusIn() + ",'SR')" + "AND ");
        } else {
        	strBuff.append(" ct.transfer_date >= ? AND ct.transfer_date <= ? AND ");
            
//            if(!PretupsI.ALL.equals(p_productCode)) {
//            	strBuff.append(" cti.product_code=? AND ");
//            }
            
            if (!PretupsI.ALL.equals(p_status)) {
                strBuff.append(" ct.status = ? AND ");
            }
            
			if (!BTSLUtil.isNullorEmpty(p_transferSubTypeCodeArr) && !PretupsI.ALL.equalsIgnoreCase( p_transferSubTypeCodeArr[0])) {
				strBuff.append(" ( ct.transfer_sub_type = ?");
				for (int i = 1; i < p_transferSubTypeCodeArr.length; i++) {
					strBuff.append(" OR ct.transfer_sub_type = ?");
				}

				strBuff.append(" ) AND ");
			}

//            if (PretupsI.ALL.equals(p_transferSubTypeCode)) {
//                strBuff.append(" ( (u.user_id=ct.to_user_id AND ct.to_user_id=?) OR (u.user_id=ct.from_user_id AND ct.from_user_id = ?)) AND ");
//            } else if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(p_transferSubTypeCode)) {
//                strBuff.append(" u.user_id=ct.to_user_id AND ct.to_user_id=? AND ");
//            } else {
//                strBuff.append(" u.user_id=ct.from_user_id AND ct.from_user_id = ? AND ");
//            }
			if(!userType.equals(PretupsI.OPERATOR_USER_TYPE))
            	strBuff.append(" ( (u.user_id=ct.to_user_id AND ct.to_user_id=?) OR (u.user_id=ct.from_user_id AND ct.from_user_id = ?)) AND ");
			else
				strBuff.append(" u.user_id = ct.to_user_id AND ");
            strBuff.append(" u.status IN (" + PretupsBL.userStatusIn() + ",'SR')" + "AND ");
            if(!PretupsI.ALL.equalsIgnoreCase(p_transferCategory)) {

                strBuff.append(" ct.transfer_category=? AND ");
            }
        }
        strBuff.append(" ct.type = 'O2C' ");
        strBuff.append(" AND ct.first_approved_by = appu1.user_id(+)  AND ct.second_approved_by = appu2.user_id(+) ");
        strBuff.append(" AND ct.third_approved_by = appu3.user_id(+) AND ct.cancelled_by = appu4.user_id(+)  ");
        strBuff.append(" AND ug.user_id=u.user_id AND gd.grph_domain_code=ug.grph_domain_code ");
        strBuff.append(" AND LKP.lookup_type=? AND LKP.lookup_code=ct.transfer_sub_type ");
//        strBuff.append(" AND ct.transfer_id=cti.transfer_id ");
        /*strBuff.append(" AND ct.transfer_id=vb.ext_txn_no(+) ");
        strBuff.append(" AND vb.product_id=vp.product_id(+) ");*/
//        strBuff.append(" AND cti.product_code= p.product_code  ");
        strBuff.append(" ORDER BY  ct.created_on DESC,ct.transfer_sub_type ");
	        
	    return strBuff;
	}
	
	
	@Override
	public StringBuilder loadEnquiryC2cListQry( String isFromUserPrimary, String isToUserPrimary, String searchBy, String p_transferID,
    		String p_userID, Date p_fromDate, Date p_toDate, String p_status, String[] p_transferSubTypeCodeArr, 
    		String p_fromUserCode, String p_toUserCode, String p_transferCategory, String p_staffUserID, String p_userType,String sessionUserDomain)
	{
		
	    final StringBuilder strBuff = new StringBuilder(" SELECT LKP.lookup_name, ct.transfer_sub_type,ct.requested_quantity,ct.transfer_type,");
        strBuff.append(" gd.grph_domain_name,gd.grph_domain_code ,ct.transfer_id, ct.network_code, ct.network_code_for, ");
        strBuff.append(" ct.transfer_date, ct.first_approved_by ,ct.first_approved_on, ct.second_approved_by, ct.dual_comm_type, ");
        strBuff.append(" ct.second_approved_on, ct.third_approved_by,ct.third_approved_on, ct.cancelled_by,  ");
        strBuff.append(" ct.cancelled_on, ct.modified_by, ct.modified_on, ct.status,ct.type, ct.payable_amount, ");
        strBuff.append(" ct.net_payable_amount, u1.user_name fromUserName, u2.user_name toUserName, appu1.user_name firstapprovedby,appu2.user_name ");
        strBuff.append(" secondapprovedby,appu3.user_name thirdapprovedby, appu4.user_name cancelledby,ct.msisdn from_msisdn,ct.to_msisdn to_msisdn, ");
        strBuff.append(" ct.transfer_category,ct.ext_txn_date, ct.ext_txn_no,ct.to_user_id,ct.from_user_id,ct.domain_code, ");
        strBuff.append(" ct.first_level_approved_quantity, ct.second_level_approved_quantity, ct.third_level_approved_quantity,ct.channel_user_remarks, ");
//        strBuff.append(" cti.product_code, ");
        strBuff.append(" ct.PMT_INST_TYPE, ct.transaction_mode");
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue())  {
            strBuff.append(",ct.SOS_STATUS, ct.SOS_SETTLEMENT_DATE");
            }
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_TRANSFERS_INFO_REQUIRED))).booleanValue())
        {
        	strBuff.append(",ct.info1,ct.info2 ");
        }
		/*strBuff.append(",vb.batch_no,vp.product_name,vb.batch_type,vb.total_no_of_vouchers,vb.from_serial_no,vb.to_serial_no ");*/
		strBuff.append(" , ct.reference_no, ct.source, ct.created_by, ct.created_on, ct.control_transfer, ct.active_user_id, appu5.user_name active_user_name, appu5.user_type active_user_type ");
        strBuff.append(" FROM channel_transfers ct , users u1, users u2, lookups LKP, ");
        if ((!BTSLUtil.isNullString(isFromUserPrimary)) || (!BTSLUtil.isNullString(isToUserPrimary))) {
            strBuff.append(" user_phones up1, user_phones up2, ");
        }
        strBuff.append(" users appu1, users appu2, users appu3, users appu4 , users appu5, user_geographies ug,geographical_domains gd");
        /*strBuff.append(" ,voms_batches vb,voms_products vp ");*/
//        strBuff.append(" , products p ");
        strBuff.append(" WHERE ");
        if (PretupsI.SEARCH_BY_TRANSACTIONID.equalsIgnoreCase(searchBy)) {
            strBuff.append(" ct.transfer_id = ? AND ");
            
        } else if (PretupsI.SEARCH_BY_MSISDN.equalsIgnoreCase(searchBy)) {
        	
        	    if(!BTSLUtil.isNullString(p_fromUserCode) ){
        	    	 if (BTSLUtil.isNullString(isFromUserPrimary)) {
                         strBuff.append("(u1.user_code=? ) AND ");
                     } else {
                         strBuff.append("(up1.msisdn=? ) AND ");
                         strBuff.append("(u1.user_id=up1.user_id ) AND ");
                     }
        	    }
        	    if(!BTSLUtil.isNullString(p_toUserCode) ){
        	    	 if (BTSLUtil.isNullString(isToUserPrimary)) {
                         strBuff.append(" ( u2.user_code=? ) AND ");
                     } else {
                         strBuff.append("(up2.msisdn=? ) AND ");
                         strBuff.append("(u2.user_id=up2.user_id ) AND ");
                     }
        	    }
               
	            strBuff.append(" ct.transfer_date >= ? AND ct.transfer_date <= ? AND ");
	            
	            if(!BTSLUtil.isNullorEmpty(p_transferSubTypeCodeArr)  && !PretupsI.ALL.equalsIgnoreCase( p_transferSubTypeCodeArr[0]) ) {
	            	strBuff.append(" ( ct.transfer_sub_type = ?");
	            	for(int i=1; i <p_transferSubTypeCodeArr.length; i++) {
	            		strBuff.append(" OR ct.transfer_sub_type = ?");
		            }

		            strBuff.append(" ) AND ");
	            }
	            
//            	strBuff.append(" u1.status IN (" + PretupsBL.userStatusIn() + ",'SR')" + "AND ");
        } else {
        	strBuff.append(" ct.transfer_date >= ? AND ct.transfer_date <= ? AND ");
            
//            if(!PretupsI.ALL.equals(p_productCode)) {
//            	strBuff.append(" cti.product_code=? AND ");
//            }
            
            if (!PretupsI.ALL.equals(p_status)) {
                strBuff.append(" ct.status = ? AND ");
            }
            
            if(!BTSLUtil.isNullorEmpty(p_transferSubTypeCodeArr) && !PretupsI.ALL.equalsIgnoreCase( p_transferSubTypeCodeArr[0])) {
            	strBuff.append(" ( ct.transfer_sub_type = ?");
            	for(int i=1; i <p_transferSubTypeCodeArr.length; i++) {
            		strBuff.append(" OR ct.transfer_sub_type = ?");
	            }

                strBuff.append(" ) AND ");
            }
			if (!sessionUserDomain.equals(PretupsI.OPERATOR_TYPE_OPT)) {
				if (!PretupsI.USER_TYPE_STAFF.equalsIgnoreCase(p_userType)) {
					strBuff.append(" (ct.from_user_id=? ) AND ");
				} else {
					strBuff.append(" ct.active_user_id in (" + p_staffUserID + ") AND ");
				}
			}
            
            if(!PretupsI.ALL.equalsIgnoreCase(p_transferCategory)) {

                strBuff.append(" ct.transfer_category=? AND ");
            }
//            strBuff.append(" u1.status IN (" + PretupsBL.userStatusIn() + ",'SR')" + "AND ");
        }
        strBuff.append(" ct.type = 'C2C' ");
        strBuff.append(" AND u1.user_id=ct.from_user_id AND u2.user_id=ct.to_user_id ");
        strBuff.append(" AND ct.first_approved_by = appu1.user_id(+)  AND ct.second_approved_by = appu2.user_id(+) ");
        strBuff.append(" AND ct.third_approved_by = appu3.user_id(+) AND ct.cancelled_by = appu4.user_id(+)  ");
        strBuff.append(" AND ct.active_user_id = appu5.user_id(+) ");
        strBuff.append(" AND ug.user_id=u1.user_id AND gd.grph_domain_code=ug.grph_domain_code ");
        strBuff.append(" AND LKP.lookup_type=? AND LKP.lookup_code=ct.transfer_sub_type ");
//        strBuff.append(" AND ct.transfer_id=cti.transfer_id ");
        /*strBuff.append(" AND ct.transfer_id=vb.ext_txn_no(+) ");
        strBuff.append(" AND vb.product_id=vp.product_id(+) ");*/ 
//        strBuff.append(" AND cti.product_code= p.product_code  ");
        if (!BTSLUtil.isNullString(isFromUserPrimary) || (!BTSLUtil.isNullString(isToUserPrimary))) {
            strBuff.append(" AND ct.msisdn=up1.msisdn AND ct.to_msisdn=up2.msisdn ");
        }
        strBuff.append(" ORDER BY  ct.created_on DESC,ct.transfer_sub_type ");
	        
	    return strBuff;
	}

	@Override
	public String viewTransactionIDAllowCheck() {
	    final StringBuilder strBuff = new StringBuilder();
	    strBuff.append( " SELECT * FROM  ( SELECT user_id,parent_id,owner_id FROM USERS CONNECT BY PRIOR user_id = parent_id START WITH user_id=(select user_id from users where login_id =?))  X	, ");
	    strBuff.append( " channel_transfers ct WHERE (ct.from_user_id = X.user_id OR ct.to_user_id =X.user_id ) AND ct.transfer_id =? ");
	     if(log.isDebugEnabled()) {
	    	 log.debug("viewTransactionIDAllowCheck", strBuff.toString());
	     }
		return strBuff.toString();
	}
	
	public String viewTransactionIDAllowCheckNew() {
	    final StringBuilder strBuff = new StringBuilder();
	    strBuff.append( " SELECT * FROM  ( SELECT user_id,parent_id,owner_id FROM USERS CONNECT BY PRIOR user_id = parent_id START WITH user_id=(select user_id from users where login_id =?))  X	, ");
	    strBuff.append( " channel_transfers ct WHERE ct.transfer_id =? ");
	     if(log.isDebugEnabled()) {
	    	 log.debug("viewTransactionIDAllowCheck", strBuff.toString());
	     }
		return strBuff.toString();
	}
	
	@Override
	public String loadO2CChannelTransfersListQry(String isPrimary,String p_transferID,String p_userCode, String p_transferTypeCode, String p_transferCategory ) {
		final StringBuffer strBuff = new StringBuffer(" SELECT LKP.lookup_name,ct.transfer_sub_type,ct.requested_quantity,ct.transfer_type, ct.transaction_mode,");
        strBuff.append(" gd.grph_domain_name,gd.grph_domain_code ,ct.transfer_id, ct.network_code, ct.network_code_for, ");
        strBuff.append(" ct.transfer_date, ct.first_approved_by ,ct.first_approved_on, ct.second_approved_by, ");
        strBuff.append(" ct.second_approved_on, ct.third_approved_by,ct.third_approved_on, ct.cancelled_by,  ");
        strBuff.append(" ct.cancelled_on, ct.modified_by, ct.modified_on, ct.status,ct.type, ct.payable_amount, ");
        strBuff.append(" ct.net_payable_amount, u.user_name, u.category_code, (SELECT category_name FROM categories c2 WHERE c2.category_code =u.category_code) categoryName,appu1.user_name firstapprovedby,appu2.user_name ");
        strBuff.append(" secondapprovedby,appu3.user_name thirdapprovedby, appu4.user_name cancelledby,u.msisdn,ct.msisdn from_msisdn,ct.to_msisdn to_msisdn, ");
        strBuff.append(" ct.transfer_category,ct.ext_txn_date, ct.ext_txn_no,ct.to_user_id,ct.from_user_id,ct.domain_code, (SELECT domain_name FROM domains d2 WHERE d2.domain_code =ct.domain_code) domainName, ");
        strBuff.append(" ct.first_level_approved_quantity, ct.second_level_approved_quantity, ct.third_level_approved_quantity ");
        strBuff.append(" FROM channel_transfers ct , users u,lookups LKP, ");
        if (!BTSLUtil.isNullString(isPrimary) && ("N".equalsIgnoreCase(isPrimary))) {
            strBuff.append(" user_phones up, ");
        }
        strBuff.append(" users appu1, users appu2, users appu3, users appu4 , user_geographies ug,geographical_domains gd");
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
        strBuff.append(" AND ct.first_approved_by = appu1.user_id(+)  AND ct.second_approved_by = appu2.user_id(+) ");
        strBuff.append(" AND ct.third_approved_by = appu3.user_id(+) AND ct.cancelled_by = appu4.user_id(+)  ");
        strBuff.append(" AND ug.user_id=u.user_id AND gd.grph_domain_code=ug.grph_domain_code ");
        strBuff.append(" AND LKP.lookup_type=? AND LKP.lookup_code=ct.transfer_sub_type ");
        strBuff.append(" AND ct.ref_transfer_id is null");
        strBuff.append(" ORDER BY  ct.created_on DESC,ct.transfer_sub_type ");
        String query = strBuff.toString();
	        if(log.isDebugEnabled()) {
		    	 log.debug("loadO2CChannelTransfersListQry", query);
		     }
	    	return query;
	}

	
	
	
	
	
}

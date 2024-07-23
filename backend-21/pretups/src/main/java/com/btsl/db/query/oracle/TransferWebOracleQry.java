package com.btsl.db.query.oracle;

import com.btsl.pretups.common.PretupsI;
import com.web.pretups.transfer.businesslogic.TransferWebQry;

public class TransferWebOracleQry implements TransferWebQry{
	@Override
	public String loadTransferRuleListQry(){
		StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT tr.module, tr.network_code, tr.sender_subscriber_type, tr.receiver_subscriber_type,tr.status, ");
        strBuff.append("tr.sender_service_class_id,tr.receiver_service_class_id, tr.card_group_set_id,  tr.modified_on, ");
        strBuff.append("tr.modified_by , tr.created_on, tr.created_by,tr.sub_service,tr.service_type, tr.cell_group_id,  ");
        strBuff.append("tr.gateway_code,tr.grade_code,tr.category_code, ct.domain_code,ct.category_code,ct.category_name,ct.status cat_status, ");
        strBuff.append("gr.grade_code,gr.grade_name ");
        strBuff.append("FROM transfer_rules tr, categories ct , channel_grades gr ");
        strBuff.append("WHERE tr.network_code=? AND tr.status <> ? AND tr.module=? ");
        strBuff.append(" AND tr.rule_type=? ");      
        strBuff.append(" AND ct.category_code(+)=tr.category_code and gr.grade_code(+)=tr.grade_code ");
        strBuff.append("ORDER BY modified_on,sender_subscriber_type, sub_service,service_type");
        return strBuff.toString();
	}
	
	public String loadTransferRuleListQry1(String status1 ,String gatewayCode ,String domain ,String category ,String grade){
		StringBuilder strBuff = new StringBuilder();
		   strBuff.append("SELECT tr.module, tr.network_code, tr.sender_subscriber_type, tr.receiver_subscriber_type,tr.status, ");
	        strBuff.append("tr.sender_service_class_id,tr.receiver_service_class_id, tr.card_group_set_id,  tr.modified_on, ");
	        strBuff.append("tr.modified_by , tr.created_on, tr.created_by,tr.sub_service,tr.service_type, tr.cell_group_id, ");
	        strBuff.append("tr.gateway_code,tr.grade_code,tr.category_code, ct.domain_code,ct.category_code,ct.category_name,ct.status cat_status, ");
	        strBuff.append("gr.grade_code,gr.grade_name,d.domain_code,d.domain_name,cgs.card_group_set_id,cgs.card_group_set_name ");
	        strBuff.append("FROM transfer_rules tr left join categories ct on  ct.category_code=tr.category_code left join channel_grades gr on  gr.grade_code=tr.grade_code left join domains d on d.domain_code = tr.sender_subscriber_type left join card_group_set cgs on cgs.card_group_set_id = tr.card_group_set_id ");
	        strBuff.append("WHERE tr.network_code=? AND tr.module=? ");
	        strBuff.append("AND tr.rule_type=? ");
	        if(!status1.equals(PretupsI.ALL)) {
	        strBuff.append("AND tr.status=? ");
	        }
	        if(!gatewayCode.equals(PretupsI.ALL)) {
	        strBuff.append("AND tr.gateway_code=? ");
	        }
	        if(!domain.equals(PretupsI.ALL)) {
	        strBuff.append("AND tr.sender_subscriber_type=? ");
	        }
	        if(!category.equals(PretupsI.ALL)) {
	        strBuff.append("AND tr.category_code=? ");
	        }
	        if(!grade.equals(PretupsI.ALL)) {
	        strBuff.append("AND tr.grade_code=? ");
	        }      
	        strBuff.append("ORDER BY modified_on,sender_subscriber_type, sub_service,service_type");
	        return strBuff.toString();
	}
	@Override
	public String loadP2PReconciliationListQry(String pnetworkCodeType){
		final StringBuilder selectQueryBuff = new StringBuilder();
        selectQueryBuff.append("SELECT KV.value,KV1.value txn_status,U.user_name,ST.name, PROD.short_name,STRF.transfer_id, ");
        selectQueryBuff.append("STRF.transfer_date, STRF.transfer_date_time, STRF.network_code, STRF.sender_id,");
        selectQueryBuff.append("STRF.product_code, STRF.sender_msisdn, STRF.receiver_msisdn, ");
        selectQueryBuff.append("STRF.receiver_network_code, STRF.transfer_value, STRF.error_code, ");
        selectQueryBuff.append("STRF.request_gateway_type, STRF.request_gateway_code, STRF.reference_id, ");
        selectQueryBuff.append("STRF.payment_method_type, STRF.service_type, STRF.pin_sent_to_msisdn, ");
        selectQueryBuff.append("STRF.language, STRF.country, STRF.skey, STRF.skey_generation_time, ");
        selectQueryBuff.append("STRF.skey_sent_to_msisdn, STRF.request_through_queue, STRF.credit_back_status, ");
        selectQueryBuff.append("STRF.quantity, STRF.reconciliation_flag, STRF.reconciliation_date, ");
        selectQueryBuff.append("STRF.reconciliation_by, STRF.created_on, STRF.created_by, STRF.modified_on, ");
        selectQueryBuff.append("STRF.modified_by, STRF.transfer_status, STRF.card_group_set_id, STRF.version, ");
        selectQueryBuff.append("STRF.card_group_id, STRF.sender_access_fee, STRF.sender_tax1_type, ");
        selectQueryBuff.append("STRF.sender_tax1_rate, STRF.sender_tax1_value, STRF.sender_tax2_type, ");
        selectQueryBuff.append("STRF.sender_tax2_rate, STRF.sender_tax2_value, STRF.sender_transfer_value, ");
        selectQueryBuff.append("STRF.receiver_access_fee, STRF.receiver_tax1_type, STRF.receiver_tax1_rate, ");
        selectQueryBuff.append("STRF.receiver_tax1_value, STRF.receiver_tax2_type, STRF.receiver_tax2_rate, ");
        selectQueryBuff.append("STRF.receiver_tax2_value, STRF.receiver_validity, STRF.receiver_transfer_value, ");
        selectQueryBuff.append("STRF.receiver_bonus_value, STRF.receiver_grace_period, STRF.transfer_category, ");
        selectQueryBuff.append("STRF.receiver_bonus_validity, STRF.card_group_code, STRF.receiver_valperiod_type,STRF.card_reference,STRF.VOUCHER_SERIAL_NUMBER  ");
        selectQueryBuff.append("FROM subscriber_transfers STRF, products PROD,service_type ST, ");
        selectQueryBuff.append("p2p_subscribers U,key_values KV,key_values KV1  ");
        selectQueryBuff.append("WHERE U.user_id(+)= STRF.sender_id ");
        selectQueryBuff.append("AND KV.key(+)=STRF.error_code AND KV.type(+)=? ");
        selectQueryBuff.append("AND KV1.key(+)=STRF.transfer_status AND KV1.type(+)=? ");
        selectQueryBuff.append("AND STRF.transfer_date >=? AND STRF.transfer_date < ? ");
        selectQueryBuff.append("AND STRF.service_type=? AND STRF.product_code=PROD.product_code ");
        selectQueryBuff.append("AND (STRF.reconciliation_flag <> 'Y' OR STRF.reconciliation_flag IS NULL ) ");
        selectQueryBuff.append("AND ST.service_type=STRF.service_type ");
        // by sandeep ID REC001
        // as now we are loading all the UNDERPROCESS or AMBIGUOUS txn. for
        // the reconciliation
        selectQueryBuff.append("AND (STRF.transfer_status=? OR STRF.transfer_status=? ) ");
     
        if (pnetworkCodeType.equals(PretupsI.SENDER_NETWORK_CODE)) {
            selectQueryBuff.append("AND STRF.network_code=? ");
        } else if (pnetworkCodeType.equals(PretupsI.RECEIVER_NETWORK_CODE)) {
            selectQueryBuff.append("AND STRF.receiver_network_code=? ");
        }

        selectQueryBuff.append("ORDER BY STRF.transfer_date_time DESC,STRF.transfer_id ");
        return selectQueryBuff.toString();
	}
	@Override
	public String loadUserListQry(){
		final StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT U.user_id, U.owner_id, U.user_name, U.login_id  FROM users U, user_geographies UG, categories CAT,");
        strBuff.append("user_phones UP WHERE U.category_code = CAT.category_code ");
        strBuff.append("AND U.user_id=UG.user_id AND UG.grph_domain_code IN (SELECT grph_domain_code FROM ");
        strBuff.append("geographical_domains GD1 WHERE status IN ('Y','S') ");
        strBuff.append("CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
        strBuff.append("START WITH grph_domain_code =? ) AND U.user_type= ? AND u.status IN ('Y','S','SR') ");
        strBuff.append("AND U.network_code = ? AND U.category_code = ? ");
        strBuff.append("AND U.user_id=UP.user_id AND UP.primary_number='Y' AND UPPER(U.user_name) like UPPER(?)	ORDER BY U.user_name");
        return strBuff.toString();
	}
	@Override
	public String addPromotionalTransferRuleFileQry(){
		final StringBuilder selectMSISDN = new StringBuilder();
        selectMSISDN.append("SELECT U.user_id FROM users U, user_geographies UG, categories CAT,user_phones UP ");
        selectMSISDN.append("WHERE U.category_code = CAT.category_code AND U.user_id=UG.user_id ");
        selectMSISDN.append("AND UG.grph_domain_code IN (SELECT grph_domain_code FROM geographical_domains GD1 WHERE status IN ('Y','S') ");
        selectMSISDN.append("CONNECT BY PRIOR grph_domain_code = parent_grph_domain_code ");
        selectMSISDN.append("START WITH GRPH_DOMAIN_TYPE=?) AND u.status IN ('Y','S','SR') ");
        selectMSISDN.append("AND U.network_code = ? ");
        selectMSISDN.append("AND U.category_code = ? ");
        selectMSISDN.append("AND U.user_id=UP.user_id ");
        selectMSISDN.append("AND UP.primary_number='Y' ");
        selectMSISDN.append("AND UP.msisdn=? ");
        selectMSISDN.append("ORDER BY U.user_name ");
        return selectMSISDN.toString();
	}
}

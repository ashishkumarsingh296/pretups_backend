package com.btsl.db.query.oracle;

import com.btsl.pretups.p2p.reconciliation.businesslogic.HandleUnsettledP2PCasesQry;

/**
 * HandleUnsettledP2PCasesOracleQry
 * @author sadhan.k
 *
 */
public class HandleUnsettledP2PCasesOracleQry implements
		HandleUnsettledP2PCasesQry {

	@Override
	public String loadP2PReconciliationVO() {
		
		StringBuilder selectQueryBuff= new StringBuilder();
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
        selectQueryBuff.append("STRF.receiver_bonus_validity, STRF.card_group_code, STRF.receiver_valperiod_type ");
        selectQueryBuff.append("FROM subscriber_transfers STRF, products PROD,service_type ST, ");
        selectQueryBuff.append("p2p_subscribers U,key_values KV,key_values KV1  ");
        selectQueryBuff.append("WHERE U.user_id(+)= STRF.sender_id ");
        selectQueryBuff.append("AND KV.key(+)=STRF.error_code AND KV.type(+)=? ");
        selectQueryBuff.append("AND KV1.key(+)=STRF.transfer_status AND KV1.type(+)=? ");
        selectQueryBuff.append("AND STRF.product_code=PROD.product_code ");
        selectQueryBuff.append("AND (STRF.reconciliation_flag <> 'Y' OR STRF.reconciliation_flag IS NULL ) ");
        selectQueryBuff.append("AND ST.service_type=STRF.service_type ");
        selectQueryBuff.append("AND (STRF.transfer_status=? OR STRF.transfer_status=? ) ");
        selectQueryBuff.append("AND STRF.transfer_id=? ");
		
		return selectQueryBuff.toString();
	}

}

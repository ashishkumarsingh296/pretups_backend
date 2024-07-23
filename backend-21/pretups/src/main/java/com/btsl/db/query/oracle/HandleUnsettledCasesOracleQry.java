package com.btsl.db.query.oracle;

import com.btsl.pretups.channel.user.businesslogic.HandleUnsettledCasesQry;

/**
 * HandleUnsettledCasesOracleQry
 * @author sadhan.k
 *
 */
public class HandleUnsettledCasesOracleQry implements HandleUnsettledCasesQry{

	@Override
	public String loadC2STransferVO() {
		
		final StringBuilder selectQueryBuff = new StringBuilder();
		//local_index_implemented
        selectQueryBuff.append("SELECT KV.value,KV1.value txn_status,U.user_name,ST.name, PROD.short_name, CTRF.transfer_id, ");
        selectQueryBuff.append("CTRF.transfer_date, CTRF.transfer_date_time, CTRF.network_code,CTRF.serial_number, sender_id,");
        selectQueryBuff.append("CTRF.sender_category, CTRF.product_code, CTRF.sender_msisdn, CTRF.receiver_msisdn, ");
        selectQueryBuff.append("CTRF.receiver_network_code, CTRF.transfer_value, CTRF.error_code, CTRF.request_gateway_type, ");
        selectQueryBuff.append("CTRF.request_gateway_code, CTRF.reference_id, CTRF.service_type, CTRF.differential_applicable, ");
        selectQueryBuff.append("CTRF.pin_sent_to_msisdn, CTRF.language, CTRF.country, CTRF.skey, CTRF.skey_generation_time, ");
        selectQueryBuff.append("CTRF.skey_sent_to_msisdn, CTRF.request_through_queue, CTRF.credit_back_status, CTRF.quantity, ");
        selectQueryBuff.append("CTRF.reconciliation_flag, CTRF.reconciliation_date, CTRF.reconciliation_by, CTRF.created_on, ");
        selectQueryBuff.append("CTRF.created_by, CTRF.modified_on, CTRF.modified_by, CTRF.transfer_status, CTRF.card_group_set_id, ");
        selectQueryBuff.append("CTRF.version, CTRF.card_group_id, CTRF.sender_transfer_value, CTRF.receiver_access_fee, ");
        selectQueryBuff.append("CTRF.receiver_tax1_type, CTRF.receiver_tax1_rate, CTRF.receiver_tax1_value, CTRF.receiver_tax2_type,");
        selectQueryBuff.append("CTRF.receiver_tax2_rate, CTRF.receiver_tax2_value, CTRF.receiver_validity, CTRF.receiver_transfer_value,");
        selectQueryBuff.append("CTRF.receiver_bonus_value, CTRF.receiver_grace_period, CTRF.receiver_bonus_validity, ");
        selectQueryBuff.append("CTRF.card_group_code, CTRF.receiver_valperiod_type, CTRF.temp_transfer_id, CTRF.transfer_profile_id,");
        selectQueryBuff.append("CTRF.commission_profile_id, CTRF.differential_given, CTRF.grph_domain_code, CTRF.source_type,U.owner_id ");
        selectQueryBuff.append(", UP.phone_language, UP.msisdn, UP.country phcountry,CTRF.reversal_id ");
        selectQueryBuff.append(",CTRF.penalty, CTRF.owner_penalty,CTRF.penalty_details ");
        selectQueryBuff.append("FROM c2s_transfers CTRF, products PROD,service_type ST,users U,key_values KV,key_values KV1,user_phones UP   ");
        selectQueryBuff.append("WHERE CTRF.transfer_id=? AND CTRF.transfer_date=? AND U.user_id = UP.user_id AND UP.primary_number='Y' AND U.user_id = CTRF.sender_id AND KV.key(+)=CTRF.error_code AND KV.type(+)=? ");
        selectQueryBuff.append("AND KV1.key(+)=CTRF.transfer_status AND KV1.type(+)=? ");
        selectQueryBuff.append("AND CTRF.product_code=PROD.product_code ");
        selectQueryBuff.append("AND (CTRF.reconciliation_flag <> 'Y' OR CTRF.reconciliation_flag IS NULL ) ");
        selectQueryBuff.append("AND ST.service_type=CTRF.service_type ");
        selectQueryBuff.append("AND (CTRF.transfer_status=? OR CTRF.transfer_status=? ) ");

		
		return selectQueryBuff.toString();
	}

}

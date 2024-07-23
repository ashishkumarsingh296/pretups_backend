package com.btsl.db.query.postgres;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.processes.businesslogic.HandleUnsettledCombinedCasesQry;

public class HandleUnsettledCombinedCasePostgresQry implements HandleUnsettledCombinedCasesQry {

	/**
	 * Query for Loading C2S Transfer VO
	 * @return String sql query
	 */
	@Override
	public String loadC2STransferVOQuery() {
		StringBuilder queryBuilder = new StringBuilder("SELECT KV.value,KV1.value txn_status,U.user_name,ST.name, PROD.short_name, CTRF.transfer_id,");
		queryBuilder.append(" CTRF.transfer_date, CTRF.transfer_date_time, CTRF.network_code, sender_id,");
		queryBuilder.append(" CTRF.sender_category, CTRF.product_code, CTRF.sender_msisdn, CTRF.receiver_msisdn,");
		queryBuilder.append(" CTRF.receiver_network_code, CTRF.transfer_value, CTRF.error_code, CTRF.request_gateway_type,");
        queryBuilder.append(" CTRF.request_gateway_code, CTRF.reference_id, CTRF.service_type, CTRF.differential_applicable,");
        queryBuilder.append(" CTRF.pin_sent_to_msisdn, CTRF.language, CTRF.country, CTRF.skey, CTRF.skey_generation_time,");
        queryBuilder.append(" CTRF.skey_sent_to_msisdn, CTRF.request_through_queue, CTRF.credit_back_status, CTRF.quantity,");
        queryBuilder.append(" CTRF.reconciliation_flag, CTRF.reconciliation_date, CTRF.reconciliation_by, CTRF.created_on,");
        queryBuilder.append(" CTRF.created_by, CTRF.modified_on, CTRF.modified_by, CTRF.transfer_status, CTRF.card_group_set_id,");
        queryBuilder.append(" CTRF.version, CTRF.card_group_id, CTRF.sender_transfer_value, CTRF.receiver_access_fee,");
        queryBuilder.append(" CTRF.receiver_tax1_type, CTRF.receiver_tax1_rate, CTRF.receiver_tax1_value, CTRF.receiver_tax2_type,");
        queryBuilder.append(" CTRF.receiver_tax2_rate, CTRF.receiver_tax2_value, CTRF.receiver_validity, CTRF.receiver_transfer_value,");
        queryBuilder.append(" CTRF.receiver_bonus_value, CTRF.receiver_grace_period, CTRF.receiver_bonus_validity,");
        queryBuilder.append(" CTRF.card_group_code, CTRF.receiver_valperiod_type, CTRF.temp_transfer_id, CTRF.transfer_profile_id,");
        queryBuilder.append(" CTRF.commission_profile_id, CTRF.differential_given, CTRF.grph_domain_code, CTRF.source_type,U.owner_id ");
        queryBuilder.append(" , CTRF.sender_previous_balance, CTRF.sender_post_balance,CTRF.receiver_previous_balance ");
        queryBuilder.append(" , CTRF.receiver_post_balance,CTRF.SENDER_CR_BK_PREV_BAL,CTRF.SENDER_CR_BK_POST_BAL, CTRF.transfer_type, CTRF.validation_status, CTRF.debit_status, CTRF.credit_status,");
        queryBuilder.append(" CTRF.credit_back_status, CTRF.reconcile_status, CTRF.interface_type, CTRF.interface_id, ");
        queryBuilder.append(" CTRF.interface_response_code, CTRF.interface_reference_id, CTRF.subscriber_type, CTRF.service_class_code, CTRF.msisdn_previous_expiry,");
        queryBuilder.append(" CTRF.msisdn_new_expiry, CTRF.transfer_status, CTRF.service_class_id, CTRF.protocol_status, CTRF.account_status, CTRF.sub_service,");
        queryBuilder.append(" CTRF.prefix_id, KV2.value transfer_type_value, KV.value in_response_code_desc, UP.phone_language, UP.msisdn, UP.country phcountry ,CTRF.first_call");
        queryBuilder.append(" FROM products PROD,service_type ST,users U,user_phones UP,c2s_transfers CTRF LEFT JOIN key_values KV ON (KV.key=CTRF.error_code AND KV.type=? AND KV.key=interface_response_code)");
        queryBuilder.append(" LEFT JOIN key_values KV1 ON (KV1.key=CTRF.transfer_status AND KV1.type=?)");
        queryBuilder.append(" LEFT JOIN key_values KV2 ON KV2.key=transfer_type");
        //queryBuilder.append("  LEFT JOIN key_values KV3 ON KV3.key=interface_response_code"); 
        queryBuilder.append(" WHERE CTRF.transfer_id=? AND CTRF.transfer_date = ? AND U.user_id = UP.user_id AND UP.primary_number='Y' AND U.user_id = CTRF.sender_id");
        queryBuilder.append(" AND CTRF.product_code=PROD.product_code AND (CTRF.reconciliation_flag <> 'Y' OR CTRF.reconciliation_flag IS NULL ) ");
        queryBuilder.append(" AND ST.service_type=CTRF.service_type AND (CTRF.transfer_status=? OR CTRF.transfer_status=? )");
		return queryBuilder.toString(); 
     }

	/**
	 * Query for updating reconciliation status
	 * @return String sql query
	 */
	@Override
	public String updateReconcilationStatusQuery() {
		StringBuffer updateQuery1 = new StringBuffer();
        updateQuery1.append("UPDATE c2s_transfers SET transfer_status=?, reconciliation_by=?, reconciliation_date=?, ");
        updateQuery1.append("reconciliation_flag='Y', modified_by=?, modified_on=? ");
        updateQuery1.append(", error_code = coalesce(error_code,quote_literal(").append(PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS).append("))");
        updateQuery1.append(", credit_back_status = ? , SENDER_CR_SETL_PREV_BAL = ? , SENDER_CR_SETL_POST_BAL = ? , reconcile_entry_type=? , DIFFERENTIAL_APPLICABLE = ? ");
        updateQuery1.append("WHERE transfer_id=? AND transfer_date=? AND (transfer_status=? OR transfer_status=?)");
		return updateQuery1.toString();
	}

	/**
	 * Query for Loading P2P reconciliation VO
	 * @return String sql query
	 */
	@Override
	public String loadP2PReconciliationVOQuery() {
		StringBuilder queryBuilder = new StringBuilder("SELECT KV.value,KV1.value txn_status,U.user_name,ST.name, PROD.short_name,STRF.transfer_id,");
		queryBuilder.append(" STRF.transfer_date, STRF.transfer_date_time, STRF.network_code, STRF.sender_id,");
		queryBuilder.append(" STRF.product_code, STRF.sender_msisdn, STRF.receiver_msisdn,");
		queryBuilder.append(" STRF.receiver_network_code, STRF.transfer_value, STRF.error_code,");
		queryBuilder.append(" STRF.request_gateway_type, STRF.request_gateway_code, STRF.reference_id,");
		queryBuilder.append(" STRF.payment_method_type, STRF.service_type, STRF.pin_sent_to_msisdn,");
		queryBuilder.append(" STRF.language, STRF.country, STRF.skey, STRF.skey_generation_time,");
		queryBuilder.append(" STRF.skey_sent_to_msisdn, STRF.request_through_queue, STRF.credit_back_status,");
		queryBuilder.append(" STRF.quantity, STRF.reconciliation_flag, STRF.reconciliation_date,");
		queryBuilder.append(" STRF.reconciliation_by, STRF.created_on, STRF.created_by, STRF.modified_on,");
		queryBuilder.append(" STRF.modified_by, STRF.transfer_status, STRF.card_group_set_id, STRF.version,");
		queryBuilder.append(" STRF.card_group_id, STRF.sender_access_fee, STRF.sender_tax1_type,");
		queryBuilder.append(" STRF.sender_tax1_rate, STRF.sender_tax1_value, STRF.sender_tax2_type,");
		queryBuilder.append(" STRF.sender_tax2_rate, STRF.sender_tax2_value, STRF.sender_transfer_value,");
		queryBuilder.append(" STRF.receiver_access_fee, STRF.receiver_tax1_type, STRF.receiver_tax1_rate,");
		queryBuilder.append(" STRF.receiver_tax1_value, STRF.receiver_tax2_type, STRF.receiver_tax2_rate,");
		queryBuilder.append(" STRF.receiver_tax2_value, STRF.receiver_validity, STRF.receiver_transfer_value,");
		queryBuilder.append(" STRF.receiver_bonus_value, STRF.receiver_grace_period, STRF.transfer_category,");
		queryBuilder.append(" STRF.receiver_bonus_validity, STRF.card_group_code, STRF.receiver_valperiod_type,STRF.SERVICE_TYPE,STRF.VOUCHER_SERIAL_NUMBER ");
		queryBuilder.append(" FROM products PROD,service_type ST,subscriber_transfers STRF LEFT JOIN p2p_subscribers U ON U.user_id= STRF.sender_id");
		queryBuilder.append(" LEFT JOIN key_values KV ON KV.key=STRF.error_code AND KV.type=?");
		queryBuilder.append(" LEFT JOIN key_values KV1 ON KV1.key=STRF.transfer_status AND KV1.type=?");
		queryBuilder.append(" WHERE STRF.product_code=PROD.product_code");
		queryBuilder.append(" AND (STRF.reconciliation_flag <> 'Y' OR STRF.reconciliation_flag IS NULL )");
		queryBuilder.append(" AND ST.service_type=STRF.service_type  AND (STRF.transfer_status=? OR STRF.transfer_status=? ) AND STRF.transfer_id=?");
	    return queryBuilder.toString();
	}

	/**
	 * Query for Loading P2P reconciliation item list
	 * @return String sql query
	 */
	@Override
	public String loadP2PReconciliationItemsListQuery() {
		StringBuilder builder = new StringBuilder("SELECT KV.value,transfer_id, msisdn, entry_date, request_value, previous_balance,");
		builder.append(" post_balance, user_type, transfer_type, entry_type, validation_status,");
        builder.append(" update_status, transfer_value, interface_type, interface_id, ");
        builder.append(" interface_response_code, interface_reference_id, subscriber_type,");
        builder.append(" service_class_code, msisdn_previous_expiry, msisdn_new_expiry, transfer_status,");
        builder.append(" transfer_date, transfer_date_time, entry_date_time, first_call, sno, prefix_id,");
        builder.append(" protocol_status, account_status, service_class_id, reference_id");
        builder.append(" FROM transfer_items TI LEFT JOIN key_values KV ON (KV.key=transfer_status AND KV.type=?) WHERE transfer_id=? ORDER BY sno");
		return builder.toString();
	}


	/**
	 * Query for updating P2P reconciliation Status
	 * @return String sql query
	 */
	@Override
	public String updateP2PReconcilationStatusQuery() {
		StringBuilder updateQuery = new StringBuilder();
        updateQuery.append("UPDATE subscriber_transfers SET transfer_status=?, reconciliation_by=?, reconciliation_date=?, ");
        updateQuery.append("reconciliation_flag='Y', modified_by=?, modified_on=? ");
        updateQuery.append(", error_code = coalesce(error_code," + PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS + ")");
        updateQuery.append("WHERE transfer_id=? AND (transfer_status=? OR transfer_status=?)");
        return updateQuery.toString();
	}

	/**
	 * Query for Loading Channel user Details
	 * @return String sql query
	 */
	@Override
	public String loadChannelUserDetailQuery() {
		StringBuilder updateQuery = new StringBuilder("SELECT u.user_id, u.password webpassword,u.user_name, u.network_code,u.login_id, u.category_code, u.parent_id, u.owner_id, u.msisdn,");
		updateQuery.append(" u.employee_code,u.status userstatus,u.created_by,u.created_on,u.modified_by,u.modified_on,");
		updateQuery.append(" cusers.contact_person,u.contact_no,u.designation,u.division,u.department,u.user_type,cusers.in_suspend,cusers.out_suspend,");
		updateQuery.append(" u.address1,u.address2,u.city,u.state,u.country,u.ssn,u.user_name_prefix,u.external_code,u.user_code,u.short_name,u.reference_id,");
		updateQuery.append(" cat.domain_code,dom.domain_type_code,cat.sequence_no catseq,cat.sms_interface_allowed,geo.grph_domain_code,gdomains.status geostatus,");
		updateQuery.append(" uphones.user_phones_id,uphones.primary_number, uphones.sms_pin, uphones.pin_required, uphones.phone_profile, uphones.phone_language phlang,");
		updateQuery.append(" uphones.country phcountry, uphones.invalid_pin_count, uphones.last_transaction_status, uphones.last_transaction_on,");
		updateQuery.append(" uphones.pin_modified_on,uphones.last_transfer_id, uphones.last_transfer_type,uphones.prefix_id,uphones.temp_transfer_id, uphones.first_invalid_pin_time,");
		updateQuery.append(" cat.agent_allowed,cat.hierarchy_allowed, cat.category_type,cat.category_name,cat.grph_domain_type,cusers.comm_profile_set_id,cusers.transfer_profile_id, tp.status tpstatus,cusers.user_grade,cset.status csetstatus,");
		updateQuery.append(" cset.language_1_message comprf_lang_1_msg,cset.language_2_message  comprf_lang_2_msg,cat.restricted_msisdns,gdt.sequence_no grphSeq, cat.transfertolistonly, cat.USER_ID_PREFIX,");
        updateQuery.append(" uphones.access_type, uphones.created_on, cusers.application_id, cusers.mpay_profile_id, cusers.user_profile_id, cusers.mcommerce_service_allow,cusers.low_bal_alert_allow, uphones.created_on userphone_created_on");
        updateQuery.append(" , u.from_time,u.to_time,u.allowed_days");
        updateQuery.append(" FROM channel_users cusers RIGHT JOIN users u ON u.user_id=cusers.user_id LEFT JOIN transfer_profile tp ON cusers.transfer_profile_id=tp.profile_id LEFT JOIN commission_profile_set cset ON cusers.comm_profile_set_id=cset.comm_profile_set_id");
        updateQuery.append(" , user_geographies geo ,categories cat,domains dom, user_phones uphones, geographical_domains gdomains,geographical_domain_types gdt WHERE uphones.msisdn=? AND uphones.user_id=u.user_id AND u.status <> ? AND u.status <> ? AND u.category_code = cat.category_code AND u.user_id=geo.user_id AND geo.grph_domain_code=gdomains.grph_domain_code"); 
        updateQuery.append(" AND cat.domain_code= dom.domain_code AND gdt.grph_domain_type=gdomains.grph_domain_type");
        return updateQuery.toString();
	}

	/**
	 * Query for Loading user balance 
	 * @return String sql query
	 */
	@Override
	public String loadUserBalanceQuery() {
		StringBuilder selectStrBuff = new StringBuilder();
        selectStrBuff.append("SELECT user_id, network_code, network_code_for, product_code, balance, prev_balance, ");
        selectStrBuff.append("last_transfer_type, last_transfer_no, last_transfer_on, daily_balance_updated_on ");
        selectStrBuff.append("FROM user_balances ");
        
        selectStrBuff.append("WHERE user_id = ? AND date_trunc('day', daily_balance_updated_on::TIMESTAMP)<> date_trunc('day', ?::TIMESTAMP) FOR UPDATE");
        
        
        return selectStrBuff.toString();
	}

	/**
	 * Query for Loading product balance
	 * @return String sql query
	 */
	@Override
	public String loadUserProductBalanceQuery() {
		StringBuilder strBuffSelect = new StringBuilder();
        strBuffSelect.append(" SELECT balance ");
        strBuffSelect.append(" FROM user_balances ");
        strBuffSelect.append(" WHERE user_id = ? AND product_code = ? AND network_code = ? AND network_code_for = ? FOR UPDATE");
        
		return strBuffSelect.toString();
	}

	/**
	 * Query for Loading Transfer count
	 * @return String sql query
	 */
	
	@Override
	public String loadTransferCountsWithLockQuery() {
		StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT user_id, daily_in_count, daily_in_value, weekly_in_count, weekly_in_value, monthly_in_count, ");
        strBuff.append(" monthly_in_value, daily_out_count, daily_out_value, weekly_out_count, weekly_out_value, ");
        strBuff.append(" monthly_out_count, monthly_out_value, outside_daily_in_count, outside_daily_in_value, ");
        strBuff.append(" outside_weekly_in_count, outside_weekly_in_value, outside_monthly_in_count, ");
        strBuff.append(" outside_monthly_in_value,  ");
        strBuff.append(" outside_daily_out_count, outside_daily_out_value, outside_weekly_out_count, ");
        strBuff.append(" outside_weekly_out_value, outside_monthly_out_count, outside_monthly_out_value, ");
        strBuff.append(" daily_subscriber_out_count, weekly_subscriber_out_count, monthly_subscriber_out_count, ");
        strBuff.append(" daily_subscriber_out_value, weekly_subscriber_out_value, monthly_subscriber_out_value, ");
        strBuff.append(" outside_last_in_time, last_in_time, last_out_time, outside_last_out_time,last_transfer_id,last_transfer_date ");
        strBuff.append(" FROM user_transfer_counts ");
        strBuff.append(" WHERE user_id = ? ");
        strBuff.append(" FOR UPDATE ");
        
        
        return strBuff.toString();
	}

	public String loadP2PReconciliationList() {
		 StringBuffer selectQueryBuff = new StringBuffer();
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
	        selectQueryBuff.append("STRF.receiver_bonus_validity, STRF.card_group_code, STRF.receiver_valperiod_type,STRF.SERVICE_TYPE,STRF.VOUCHER_SERIAL_NUMBER ");
	        selectQueryBuff.append("FROM subscriber_transfers STRF, products PROD,service_type ST, ");
	        selectQueryBuff.append("p2p_subscribers U,key_values KV,key_values KV1  ");
	        selectQueryBuff.append("WHERE U.user_id(+)= STRF.sender_id ");
	        selectQueryBuff.append("AND KV.key(+)=STRF.error_code AND KV.type(+)=? ");
	        selectQueryBuff.append("AND KV1.key(+)=STRF.transfer_status AND KV1.type(+)=? ");
	        selectQueryBuff.append("AND STRF.product_code=PROD.product_code ");
	        selectQueryBuff.append("AND (STRF.reconciliation_flag <> 'Y' OR STRF.reconciliation_flag IS NULL ) ");
	        selectQueryBuff.append("AND ST.service_type=STRF.service_type ");
	        selectQueryBuff.append("AND (STRF.transfer_status=? OR STRF.transfer_status=? ) ");
	        selectQueryBuff.append("AND STRF.transfer_date>=? and  STRF.TRANSFER_DATE_TIME<=?");
	        return selectQueryBuff.toString();
	}

	
	public String loadC2STransferListQuery() {

	    StringBuffer selectQueryBuff = new StringBuffer();
        selectQueryBuff.append("SELECT KV.value,KV1.value txn_status,U.user_name,ST.name, PROD.short_name, CTRF.transfer_id, ");
        selectQueryBuff.append("CTRF.transfer_date, CTRF.transfer_date_time, CTRF.network_code, sender_id,");
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
        selectQueryBuff.append(", CTRF.sender_previous_balance, CTRF.sender_post_balance,CTRF.receiver_previous_balance ");
        selectQueryBuff.append(", CTRF.receiver_post_balance,CTRF.SENDER_CR_BK_PREV_BAL,CTRF.SENDER_CR_BK_POST_BAL, CTRF.transfer_type, CTRF.validation_status, CTRF.debit_status, CTRF.credit_status, ");
        selectQueryBuff.append(" CTRF.credit_back_status, CTRF.reconcile_status, CTRF.interface_type, CTRF.interface_id, ");
        selectQueryBuff.append(" CTRF.interface_response_code, CTRF.interface_reference_id, CTRF.subscriber_type, CTRF.service_class_code, CTRF.msisdn_previous_expiry, ");
        selectQueryBuff.append(" CTRF.msisdn_new_expiry, CTRF.transfer_status, CTRF.service_class_id, CTRF.protocol_status, CTRF.account_status, CTRF.sub_service,");
        selectQueryBuff.append(" CTRF.prefix_id, KV2.value transfer_type_value, KV3.value in_response_code_desc, UP.phone_language, UP.msisdn, UP.country phcountry ,CTRF.first_call ");
        selectQueryBuff.append("FROM c2s_transfers CTRF, products PROD,service_type ST,users U,key_values KV,key_values KV1,user_phones UP, key_values KV2, key_values KV3   ");
        selectQueryBuff.append("WHERE U.user_id = UP.user_id AND UP.primary_number='Y' AND U.user_id = CTRF.sender_id AND KV.key(+)=CTRF.error_code AND KV.type(+)=? ");
        selectQueryBuff.append("AND KV1.key(+)=CTRF.transfer_status AND KV1.type(+)=? ");
        selectQueryBuff.append("AND KV2.key(+)=transfer_type  ");
        selectQueryBuff.append("AND KV3.key(+)=interface_response_code ");
        selectQueryBuff.append("AND CTRF.product_code=PROD.product_code ");
        selectQueryBuff.append("AND (CTRF.reconciliation_flag <> 'Y' OR CTRF.reconciliation_flag IS NULL ) ");
        selectQueryBuff.append("AND ST.service_type=CTRF.service_type ");
        selectQueryBuff.append("AND (CTRF.transfer_status=? OR CTRF.transfer_status=? ) ");
        selectQueryBuff.append("AND CTRF.transfer_date>=? and  CTRF.TRANSFER_DATE_TIME<=?");
        return selectQueryBuff.toString();
	}

	@Override
	public String updateSOSReconcilationStatus() {
		StringBuilder updateQuery = new StringBuilder();
		updateQuery.append("UPDATE sos_transaction_details SET sos_recharge_status=?, reconciliation_by=?, reconciliation_date=?, ");
        updateQuery.append("reconciliation_flag='Y', modified_by=?, modified_on=? ");
        updateQuery.append(", error_status = nvl(error_status," + PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS + ")");
        updateQuery.append("WHERE transaction_id=? AND (sos_recharge_status=? OR sos_recharge_status=?)");
		return updateQuery.toString();
		
	}
	
	public String loadSOSReconciliationList() {
		StringBuilder selectQueryBuff = new StringBuilder();
		selectQueryBuff.append("SELECT STD.transaction_id, STD.subscriber_msisdn, STD.recharge_date, ");
        selectQueryBuff.append("STD.recharge_date_time, STD.sos_recharge_amount, STD.sos_credit_amount, ");
        selectQueryBuff.append("STD.sos_debit_amount, STD.sos_recharge_status, STD.settlement_status, ");
        selectQueryBuff.append("STD.error_status, STD.interface_response_code, STD.network_code, STD.product_code, ");
        selectQueryBuff.append("STD.request_gateway_type, STD.request_gateway_code, STD.service_type, ");
        selectQueryBuff.append("STD.subscriber_type, STD.reference_id, STD.created_on, STD.created_by, ");
        selectQueryBuff.append("STD.modified_on, STD.modified_by, STD.card_group_set_id, STD.version, STD.card_group_id, ");
        selectQueryBuff.append("STD.tax1_type, STD.tax1_rate, STD.tax1_value, STD.tax2_type, STD.tax2_rate, ");
        selectQueryBuff.append("STD.tax2_value, STD.PROCESS_FEE_type, STD.PROCESS_FEE_rate, STD.PROCESS_FEE_value, STD.card_group_code, ");
        selectQueryBuff.append("STD.sub_service, STD.start_time, STD.end_time, STD.reconciliation_flag, STD.reconciliation_date, ");
        selectQueryBuff.append("STD.reconciliation_by, STD.settlement_date, STD.settlement_flag, STD.settlement_recon_flag, ");
        selectQueryBuff.append("STD.settlement_recon_date, STD.settlement_recon_by, STD.type, STD.previous_balance, ");
        selectQueryBuff.append("STD.post_balance, STD.account_status, STD.service_class_code, PROD.short_name, KV.value, KV2.value rechargestatus ");
        selectQueryBuff.append("FROM sos_transaction_details STD, products PROD, key_values KV, key_values KV2 ");
        selectQueryBuff.append("WHERE STD.recharge_date >=? AND STD.recharge_date < ? ");
        selectQueryBuff.append("AND (STD.reconciliation_flag <> 'Y' OR STD.reconciliation_flag IS NULL ) ");
        selectQueryBuff.append("AND (STD.sos_recharge_status=? OR STD.sos_recharge_status=? ) ");
        selectQueryBuff.append("AND STD.product_code=PROD.product_code ");
        selectQueryBuff.append("AND KV.key(+)=STD.error_status AND KV.type(+)=? ");
        selectQueryBuff.append("AND KV2.key(+)=STD.sos_recharge_status AND KV2.type(+)=? ");
        selectQueryBuff.append("ORDER BY STD.recharge_date_time DESC ,STD.transaction_id ");

        return selectQueryBuff.toString();
		
	}


}

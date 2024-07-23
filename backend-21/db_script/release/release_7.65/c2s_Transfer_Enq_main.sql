UPDATE REPORT_ACTION_MAPPING_CLOB
SET RPT_ID='RPT_C2S_TRNS_ENQ', SRC_TYPE='postgresql', RPT_ACTION='SELECT
	KV.value errcode,
	KV2.value txnstatus,
	U.user_name,
	ST.name,
	PROD.short_name,
	CTRF.transfer_id,
	CTRF.transfer_date,
	CTRF.transfer_date_time,
	CTRF.network_code,
	sender_id,
	CTRF.sender_category,
	CTRF.product_code,
	CTRF.sender_msisdn,
	CTRF.receiver_msisdn,
	CTRF.receiver_network_code,
	CTRF.transfer_value,
	CTRF.error_code,
	CTRF.request_gateway_type,
	CTRF.request_gateway_code,
	CTRF.reference_id,
	CTRF.service_type,
	CTRF.differential_applicable,
	CTRF.pin_sent_to_msisdn,
	CTRF.language,
	CTRF.country,
	CTRF.skey,
	CTRF.skey_generation_time,
	CTRF.skey_sent_to_msisdn,
	CTRF.request_through_queue,
	CTRF.credit_back_status,
	CTRF.quantity,
	CTRF.reconciliation_flag,
	CTRF.reconciliation_date,
	CTRF.reconciliation_by,
	CTRF.created_on,
	CTRF.created_by,
	CTRF.modified_on,
	CTRF.modified_by,
	CTRF.transfer_status,
	CTRF.card_group_set_id,
	CTRF.version,
	CTRF.card_group_id,
	CTRF.sender_transfer_value,
	CTRF.receiver_access_fee,
	CTRF.receiver_tax1_type,
	CTRF.receiver_tax1_rate,
	CTRF.receiver_tax1_value,
	CTRF.receiver_tax2_type,
	CTRF.receiver_tax2_rate,
	CTRF.receiver_tax2_value,
	CTRF.receiver_validity,
	CTRF.receiver_transfer_value,
	CTRF.receiver_bonus_value,
	CTRF.receiver_grace_period,
	CTRF.receiver_bonus_validity,
	CTRF.subs_sid,
	CTRF.card_group_code,
	CTRF.receiver_valperiod_type,
	CTRF.temp_transfer_id,
	CTRF.transfer_profile_id,
	CTRF.commission_profile_id,
	CTRF.differential_given,
	CTRF.grph_domain_code,
	CTRF.source_type,
	CTRF.sub_service,
	CTRF.serial_number
FROM
	c2s_transfers CTRF
LEFT JOIN key_values KV ON
	(
		KV.key = CTRF.error_code
			AND KV.TYPE =''C2S_ERR_CD''
	)
LEFT JOIN key_values KV2 ON
	(
		KV2.key = CTRF.transfer_status
			AND KV2.type =''C2S_STATUS''
	),
	products PROD,
	service_type ST,
	users U,
	networks NTWK
WHERE
	CTRF.transfer_date >= TO_DATE(:startDate, ''DD/MM/YY'') 
	AND CTRF.transfer_date < TO_DATE(:endDate, ''DD/MM/YY'') 
	AND CTRF.network_code = :NetworkCode
	AND U.user_id = CTRF.sender_id
	AND CTRF.service_type = CASE
		:serviceType WHEN ''ALL'' THEN CTRF.service_type
		ELSE :serviceType
	END
	AND CTRF.product_code = PROD.product_code
	AND ST.service_type = CTRF.service_type
	AND CTRF.network_code = NTWK.network_code
	AND ctrf.transfer_id = :transferId
ORDER BY
	CTRF.service_type,
	CTRF.transfer_date_time DESC,
	CTRF.transfer_idSELECT * FROM users WHERE ROWNUM <=10', PANEL_ID='p_transNo'
WHERE ROWID='AAAattAAmAADtTPAAE';
UPDATE REPORT_ACTION_MAPPING_CLOB
SET RPT_ID='RPT_C2S_TRNS_ENQ', SRC_TYPE='postgresql', RPT_ACTION='SELECT
	KV.value errcode,
	KV2.value txnstatus,
	U.user_name,
	ST.name,
	PROD.short_name,
	CTRF.transfer_id,
	CTRF.transfer_date,
	CTRF.transfer_date_time,
	CTRF.network_code,
	sender_id,
	CTRF.sender_category,
	CTRF.product_code,
	CTRF.sender_msisdn,
	CTRF.receiver_msisdn,
	CTRF.receiver_network_code,
	CTRF.transfer_value,
	CTRF.error_code,
	CTRF.request_gateway_type,
	CTRF.request_gateway_code,
	CTRF.reference_id,
	CTRF.service_type,
	CTRF.differential_applicable,
	CTRF.pin_sent_to_msisdn,
	CTRF.language,
	CTRF.country,
	CTRF.skey,
	CTRF.skey_generation_time,
	CTRF.skey_sent_to_msisdn,
	CTRF.request_through_queue,
	CTRF.credit_back_status,
	CTRF.quantity,
	CTRF.reconciliation_flag,
	CTRF.reconciliation_date,
	CTRF.reconciliation_by,
	CTRF.created_on,
	CTRF.created_by,
	CTRF.modified_on,
	CTRF.modified_by,
	CTRF.transfer_status,
	CTRF.card_group_set_id,
	CTRF.version,
	CTRF.card_group_id,
	CTRF.sender_transfer_value,
	CTRF.receiver_access_fee,
	CTRF.receiver_tax1_type,
	CTRF.receiver_tax1_rate,
	CTRF.receiver_tax1_value,
	CTRF.receiver_tax2_type,
	CTRF.receiver_tax2_rate,
	CTRF.receiver_tax2_value,
	CTRF.receiver_validity,
	CTRF.receiver_transfer_value,
	CTRF.receiver_bonus_value,
	CTRF.receiver_grace_period,
	CTRF.receiver_bonus_validity,
	CTRF.subs_sid,
	CTRF.card_group_code,
	CTRF.receiver_valperiod_type,
	CTRF.temp_transfer_id,
	CTRF.transfer_profile_id,
	CTRF.commission_profile_id,
	CTRF.differential_given,
	CTRF.grph_domain_code,
	CTRF.source_type,
	CTRF.sub_service,
	CTRF.serial_number
FROM
	c2s_transfers CTRF
LEFT JOIN key_values KV ON
	(
		KV.key = CTRF.error_code
			AND KV.TYPE =''C2S_ERR_CD''
	)
LEFT JOIN key_values KV2 ON
	(
		KV2.key = CTRF.transfer_status
			AND KV2.type =''C2S_STATUS''
	),
	products PROD,
	service_type ST,
	users U,
	networks NTWK
WHERE
	CTRF.transfer_date >= TO_DATE(:startDate, ''DD/MM/YY'') 
	AND CTRF.transfer_date < TO_DATE(:endDate, ''DD/MM/YY'') 
	AND CTRF.network_code = :NetworkCode
	AND U.user_id = CTRF.sender_id
	AND CTRF.service_type = CASE
		:serviceType WHEN ''ALL'' THEN CTRF.service_type
		ELSE :serviceType
	END
	AND CTRF.product_code = PROD.product_code
	AND ST.service_type = CTRF.service_type
	AND CTRF.network_code = NTWK.network_code
	AND ctrf.receiver_msisdn = :receiverMobNo
ORDER BY
	CTRF.service_type,
	CTRF.transfer_date_time DESC,
	CTRF.transfer_id', PANEL_ID='p_toMobNo'
WHERE ROWID='AAAattAAmAADtTPAAG';
UPDATE REPORT_ACTION_MAPPING_CLOB
SET RPT_ID='RPT_C2S_TRNS_ENQ', SRC_TYPE='postgresql', RPT_ACTION='SELECT
	KV.value errcode,
	KV2.value txnstatus,
	U.user_name,
	ST.name,
	PROD.short_name,
	CTRF.transfer_id,
	CTRF.transfer_date,
	CTRF.transfer_date_time,
	CTRF.network_code,
	sender_id,
	CTRF.sender_category,
	CTRF.product_code,
	CTRF.sender_msisdn,
	CTRF.receiver_msisdn,
	CTRF.receiver_network_code,
	CTRF.transfer_value,
	CTRF.error_code,
	CTRF.request_gateway_type,
	CTRF.request_gateway_code,
	CTRF.reference_id,
	CTRF.service_type,
	CTRF.differential_applicable,
	CTRF.pin_sent_to_msisdn,
	CTRF.language,
	CTRF.country,
	CTRF.skey,
	CTRF.skey_generation_time,
	CTRF.skey_sent_to_msisdn,
	CTRF.request_through_queue,
	CTRF.credit_back_status,
	CTRF.quantity,
	CTRF.reconciliation_flag,
	CTRF.reconciliation_date,
	CTRF.reconciliation_by,
	CTRF.created_on,
	CTRF.created_by,
	CTRF.modified_on,
	CTRF.modified_by,
	CTRF.transfer_status,
	CTRF.card_group_set_id,
	CTRF.version,
	CTRF.card_group_id,
	CTRF.sender_transfer_value,
	CTRF.receiver_access_fee,
	CTRF.receiver_tax1_type,
	CTRF.receiver_tax1_rate,
	CTRF.receiver_tax1_value,
	CTRF.receiver_tax2_type,
	CTRF.receiver_tax2_rate,
	CTRF.receiver_tax2_value,
	CTRF.receiver_validity,
	CTRF.receiver_transfer_value,
	CTRF.receiver_bonus_value,
	CTRF.receiver_grace_period,
	CTRF.receiver_bonus_validity,
	CTRF.subs_sid,
	CTRF.card_group_code,
	CTRF.receiver_valperiod_type,
	CTRF.temp_transfer_id,
	CTRF.transfer_profile_id,
	CTRF.commission_profile_id,
	CTRF.differential_given,
	CTRF.grph_domain_code,
	CTRF.source_type,
	CTRF.sub_service,
	CTRF.serial_number
FROM
	c2s_transfers CTRF
LEFT JOIN key_values KV ON
	(
		KV.key = CTRF.error_code
			AND KV.TYPE =''C2S_ERR_CD''
	)
LEFT JOIN key_values KV2 ON
	(
		KV2.key = CTRF.transfer_status
			AND KV2.type =''C2S_STATUS''
	),
	products PROD,
	service_type ST,
	users U,
	networks NTWK
WHERE
	CTRF.transfer_date >= TO_DATE(:startDate, ''DD/MM/YY'') 
	AND CTRF.transfer_date < TO_DATE(:endDate, ''DD/MM/YY'') 
	AND CTRF.network_code = :NetworkCode
	AND U.user_id = CTRF.sender_id
	AND CTRF.service_type = CASE
		:serviceType WHEN ''ALL'' THEN CTRF.service_type
		ELSE :serviceType
	END
	AND CTRF.product_code = PROD.product_code
	AND ST.service_type = CTRF.service_type
	AND CTRF.network_code = NTWK.network_code
	AND (CTRF.sender_msisdn = :senderMsisdn or CTRF.sender_id = :userId)
ORDER BY
	CTRF.service_type,
	CTRF.transfer_date_time DESC,
	CTRF.transfer_id', PANEL_ID='p_userMobNo'
WHERE ROWID='AAAattAAmAADtTPAAI';
UPDATE REPORT_ACTION_MAPPING_CLOB
SET RPT_ID='RPT_C2S_TRNS_ENQ', SRC_TYPE='oracle', RPT_ACTION='SELECT
	KV.value errcode,
	KV2.value txnstatus,
	U.user_name,
	ST.name,
	PROD.short_name,
	CTRF.transfer_id,
	CTRF.transfer_date,
	CTRF.transfer_date_time,
	CTRF.network_code,
	sender_id,
	CTRF.sender_category,
	CTRF.product_code,
	CTRF.sender_msisdn,
	CTRF.receiver_msisdn,
	CTRF.receiver_network_code,
	CTRF.transfer_value,
	CTRF.error_code,
	CTRF.request_gateway_type,
	CTRF.request_gateway_code,
	CTRF.reference_id,
	CTRF.service_type,
	CTRF.differential_applicable,
	CTRF.pin_sent_to_msisdn,
	CTRF.language,
	CTRF.country,
	CTRF.skey,
	CTRF.skey_generation_time,
	CTRF.skey_sent_to_msisdn,
	CTRF.request_through_queue,
	CTRF.credit_back_status,
	CTRF.quantity,
	CTRF.reconciliation_flag,
	CTRF.reconciliation_date,
	CTRF.reconciliation_by,
	CTRF.created_on,
	CTRF.created_by,
	CTRF.modified_on,
	CTRF.modified_by,
	CTRF.transfer_status,
	CTRF.card_group_set_id,
	CTRF.version,
	CTRF.card_group_id,
	CTRF.sender_transfer_value,
	CTRF.receiver_access_fee,
	CTRF.receiver_tax1_type,
	CTRF.receiver_tax1_rate,
	CTRF.receiver_tax1_value,
	CTRF.receiver_tax2_type,
	CTRF.receiver_tax2_rate,
	CTRF.receiver_tax2_value,
	CTRF.receiver_validity,
	CTRF.receiver_transfer_value,
	CTRF.receiver_bonus_value,
	CTRF.receiver_grace_period,
	CTRF.receiver_bonus_validity,
	CTRF.subs_sid,
	CTRF.card_group_code,
	CTRF.receiver_valperiod_type,
	CTRF.temp_transfer_id,
	CTRF.transfer_profile_id,
	CTRF.commission_profile_id,
	CTRF.differential_given,
	CTRF.grph_domain_code,
	CTRF.source_type,
	CTRF.sub_service,
	CTRF.serial_number ,
	CTRF.subs_sid,
	CTRF.cell_id,
	CTRF.switch_id ,
	CTRF.reversal_id ,
	CTRF.info1,
	CTRF.info2,
	CTRF.info3,
	CTRF.info4,
	CTRF.info5,
	CTRF.info6,
	CTRF.info7,
	CTRF.info8,
	CTRF.info9,
	CTRF.info10 ,
	CTRF.ext_credit_intfce_type,
	CTRF.multicurrency_detail ,
	CTRF.bonus_details,
	CTRF.bonus_amount,
	CTRF.promo_previous_balance,
	CTRF.promo_post_balance,
	CTRF.promo_previous_expiry,
	CTRF.promo_new_expiry,
	NTWK.network_name
FROM
	c2s_transfers CTRF,
	products PROD,
	service_type ST,
	users U,
	key_values KV,
	key_values KV2 ,
	networks NTWK
WHERE
	CTRF.transfer_date >= TO_DATE(:dateRangeC2Sfrom , ''dd/mm/yy'')
	AND CTRF.transfer_date < TO_DATE(:dateRangeC2Sto , ''dd/mm/yy'')
	AND CTRF.network_code =:networkCode
	AND U.user_id = CTRF.sender_id
	AND KV.key(+)= CTRF.error_code
	AND KV.type(+)=''C2S_ERR_CD''
	AND KV2.key(+)= CTRF.transfer_status
	AND KV2.type(+)=''C2S_STATUS''
	AND CTRF.service_type = decode(:c2sServiceType, ''ALL'', CTRF.service_type,:c2sServiceType)
	AND CTRF.product_code = PROD.product_code
	AND ST.service_type = CTRF.service_type
	AND CTRF.network_code = NTWK.network_code
	AND CTRF.transfer_id =:transferId
ORDER BY
	CTRF.service_type,
	CTRF.transfer_date_time DESC,
	CTRF.transfer_id', PANEL_ID='p_transNo'
WHERE ROWID='AAAattAAmAADtTPAAD';
UPDATE REPORT_ACTION_MAPPING_CLOB
SET RPT_ID='RPT_C2S_TRNS_ENQ', SRC_TYPE='oracle', RPT_ACTION='SELECT
	KV.value errcode,
	KV2.value txnstatus,
	U.user_name,
	ST.name,
	PROD.short_name,
	CTRF.transfer_id,
	CTRF.transfer_date,
	CTRF.transfer_date_time,
	CTRF.network_code,
	sender_id,
	CTRF.sender_category,
	CTRF.product_code,
	CTRF.sender_msisdn,
	CTRF.receiver_msisdn,
	CTRF.receiver_network_code,
	CTRF.transfer_value,
	CTRF.error_code,
	CTRF.request_gateway_type,
	CTRF.request_gateway_code,
	CTRF.reference_id,
	CTRF.service_type,
	CTRF.differential_applicable,
	CTRF.pin_sent_to_msisdn,
	CTRF.language,
	CTRF.country,
	CTRF.skey,
	CTRF.skey_generation_time,
	CTRF.skey_sent_to_msisdn,
	CTRF.request_through_queue,
	CTRF.credit_back_status,
	CTRF.quantity,
	CTRF.reconciliation_flag,
	CTRF.reconciliation_date,
	CTRF.reconciliation_by,
	CTRF.created_on,
	CTRF.created_by,
	CTRF.modified_on,
	CTRF.modified_by,
	CTRF.transfer_status,
	CTRF.card_group_set_id,
	CTRF.version,
	CTRF.card_group_id,
	CTRF.sender_transfer_value,
	CTRF.receiver_access_fee,
	CTRF.receiver_tax1_type,
	CTRF.receiver_tax1_rate,
	CTRF.receiver_tax1_value,
	CTRF.receiver_tax2_type,
	CTRF.receiver_tax2_rate,
	CTRF.receiver_tax2_value,
	CTRF.receiver_validity,
	CTRF.receiver_transfer_value,
	CTRF.receiver_bonus_value,
	CTRF.receiver_grace_period,
	CTRF.receiver_bonus_validity,
	CTRF.subs_sid,
	CTRF.card_group_code,
	CTRF.receiver_valperiod_type,
	CTRF.temp_transfer_id,
	CTRF.transfer_profile_id,
	CTRF.commission_profile_id,
	CTRF.differential_given,
	CTRF.grph_domain_code,
	CTRF.source_type,
	CTRF.sub_service,
	CTRF.serial_number ,
	CTRF.subs_sid,
	CTRF.cell_id,
	CTRF.switch_id ,
	CTRF.reversal_id ,
	CTRF.info1,
	CTRF.info2,
	CTRF.info3,
	CTRF.info4,
	CTRF.info5,
	CTRF.info6,
	CTRF.info7,
	CTRF.info8,
	CTRF.info9,
	CTRF.info10 ,
	CTRF.ext_credit_intfce_type,
	CTRF.multicurrency_detail ,
	CTRF.bonus_details,
	CTRF.bonus_amount,
	CTRF.promo_previous_balance,
	CTRF.promo_post_balance,
	CTRF.promo_previous_expiry,
	CTRF.promo_new_expiry,
	NTWK.network_name
FROM
	c2s_transfers CTRF,
	products PROD,
	service_type ST,
	users U,
	key_values KV,
	key_values KV2 ,
	networks NTWK
WHERE
	CTRF.transfer_date >= TO_DATE(:dateRangeC2Sfrom , ''dd/mm/yy'')
	AND CTRF.transfer_date < TO_DATE(:dateRangeC2Sto , ''dd/mm/yy'')
	AND CTRF.network_code =:networkCode
	AND U.user_id = CTRF.sender_id
	AND KV.key(+)= CTRF.error_code
	AND KV.type(+)=''C2S_ERR_CD''
	AND KV2.key(+)= CTRF.transfer_status
	AND KV2.type(+)=''C2S_STATUS''
	AND CTRF.service_type = decode(:c2sServiceType, ''ALL'', CTRF.service_type,:c2sServiceType)
	AND CTRF.product_code = PROD.product_code
	AND ST.service_type = CTRF.service_type
	AND CTRF.network_code = NTWK.network_code
	AND (CTRF.sender_msisdn = :senderMobNo OR CTRF.sender_id = :userId)
ORDER BY
	CTRF.service_type,
	CTRF.transfer_date_time DESC,
	CTRF.transfer_id', PANEL_ID='p_userMobNo'
WHERE ROWID='AAAattAAmAADtTPAAH';
UPDATE REPORT_ACTION_MAPPING_CLOB
SET RPT_ID='RPT_C2S_TRNS_ENQ', SRC_TYPE='oracle', RPT_ACTION='SELECT
	KV.value errcode,
	KV2.value txnstatus,
	U.user_name,
	ST.name,
	PROD.short_name,
	CTRF.transfer_id,
	CTRF.transfer_date,
	CTRF.transfer_date_time,
	CTRF.network_code,
	sender_id,
	CTRF.sender_category,
	CTRF.product_code,
	CTRF.sender_msisdn,
	CTRF.receiver_msisdn,
	CTRF.receiver_network_code,
	CTRF.transfer_value,
	CTRF.error_code,
	CTRF.request_gateway_type,
	CTRF.request_gateway_code,
	CTRF.reference_id,
	CTRF.service_type,
	CTRF.differential_applicable,
	CTRF.pin_sent_to_msisdn,
	CTRF.language,
	CTRF.country,
	CTRF.skey,
	CTRF.skey_generation_time,
	CTRF.skey_sent_to_msisdn,
	CTRF.request_through_queue,
	CTRF.credit_back_status,
	CTRF.quantity,
	CTRF.reconciliation_flag,
	CTRF.reconciliation_date,
	CTRF.reconciliation_by,
	CTRF.created_on,
	CTRF.created_by,
	CTRF.modified_on,
	CTRF.modified_by,
	CTRF.transfer_status,
	CTRF.card_group_set_id,
	CTRF.version,
	CTRF.card_group_id,
	CTRF.sender_transfer_value,
	CTRF.receiver_access_fee,
	CTRF.receiver_tax1_type,
	CTRF.receiver_tax1_rate,
	CTRF.receiver_tax1_value,
	CTRF.receiver_tax2_type,
	CTRF.receiver_tax2_rate,
	CTRF.receiver_tax2_value,
	CTRF.receiver_validity,
	CTRF.receiver_transfer_value,
	CTRF.receiver_bonus_value,
	CTRF.receiver_grace_period,
	CTRF.receiver_bonus_validity,
	CTRF.subs_sid,
	CTRF.card_group_code,
	CTRF.receiver_valperiod_type,
	CTRF.temp_transfer_id,
	CTRF.transfer_profile_id,
	CTRF.commission_profile_id,
	CTRF.differential_given,
	CTRF.grph_domain_code,
	CTRF.source_type,
	CTRF.sub_service,
	CTRF.serial_number ,
	CTRF.subs_sid,
	CTRF.cell_id,
	CTRF.switch_id ,
	CTRF.reversal_id ,
	CTRF.info1,
	CTRF.info2,
	CTRF.info3,
	CTRF.info4,
	CTRF.info5,
	CTRF.info6,
	CTRF.info7,
	CTRF.info8,
	CTRF.info9,
	CTRF.info10 ,
	CTRF.ext_credit_intfce_type,
	CTRF.multicurrency_detail ,
	CTRF.bonus_details,
	CTRF.bonus_amount,
	CTRF.promo_previous_balance,
	CTRF.promo_post_balance,
	CTRF.promo_previous_expiry,
	CTRF.promo_new_expiry,
	NTWK.network_name
FROM
	c2s_transfers CTRF,
	products PROD,
	service_type ST,
	users U,
	key_values KV,
	key_values KV2 ,
	networks NTWK
WHERE
	CTRF.transfer_date >= TO_DATE(:dateRangeC2Sfrom , ''dd/mm/yy'')
	AND CTRF.transfer_date < TO_DATE(:dateRangeC2Sto , ''dd/mm/yy'')
	AND CTRF.network_code =:networkCode
	AND U.user_id = CTRF.sender_id
	AND KV.key(+)= CTRF.error_code
	AND KV.type(+)=''C2S_ERR_CD''
	AND KV2.key(+)= CTRF.transfer_status
	AND KV2.type(+)=''C2S_STATUS''
	AND CTRF.service_type = decode(:c2sServiceType, ''ALL'', CTRF.service_type,:c2sServiceType)
	AND CTRF.product_code = PROD.product_code
	AND ST.service_type = CTRF.service_type
	AND CTRF.network_code = NTWK.network_code
	AND CTRF.RECEIVER_MSISDN = :receiverMobNo 
ORDER BY
	CTRF.service_type,
	CTRF.transfer_date_time DESC,
	CTRF.transfer_id', PANEL_ID='p_toMobNo'
WHERE ROWID='AAAattAAmAADtTPAAF';
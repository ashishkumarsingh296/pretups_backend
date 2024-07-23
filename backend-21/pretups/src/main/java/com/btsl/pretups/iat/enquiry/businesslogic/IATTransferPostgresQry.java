package com.btsl.pretups.iat.enquiry.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;

public class IATTransferPostgresQry implements IATTransferQry{

	private String className = "IATTransferPostgresQry";
	@Override
	public PreparedStatement loadIATTransferVOListNewQry(Connection pCon, String pNetworkCode, Date pFromdate, Date p_toDate, String pSenderMsisdn, String pTransferID, String pServiceType,  UserPhoneVO phoneVo) throws SQLException {
		//local_index_implemented
		String methodName = className+"#loadIATTransferVOList_newQry";
		String isSenderPrimary = phoneVo.getPrimaryNumber();
		StringBuilder selectQueryBuff = new StringBuilder();

		selectQueryBuff.append(" SELECT DISTINCT KV.VALUE errcode,KV2.VALUE txnstatus,KV3.VALUE iat_txn_status, U.user_name,ST.NAME,  ");
		selectQueryBuff.append(" PROD.short_name, CTRF.transfer_id,CTRF.transfer_date, CTRF.transfer_date_time, ");
		selectQueryBuff.append(" CTRF.network_code, CTRF.sender_id,CTRF.sender_category, CTRF.product_code,  ");
		selectQueryBuff.append(" CTRF.sender_msisdn, CTRF.receiver_msisdn,CTRF.receiver_network_code, CTRF.transfer_value, ");
		selectQueryBuff.append(" CTRF.error_code, CTRF.request_gateway_type, ");
		selectQueryBuff.append(" CTRF.request_gateway_code, CTRF.reference_id, CTRF.SERVICE_TYPE, CTRF.differential_applicable, ");
		selectQueryBuff.append(" CTRF.pin_sent_to_msisdn, CTRF.LANGUAGE, CTRF.country, CTRF.skey, CTRF.skey_generation_time,  ");
		selectQueryBuff.append(" CTRF.skey_sent_to_msisdn, CTRF.request_through_queue, CTRF.credit_back_status, CTRF.quantity, ");
		selectQueryBuff.append(" CTRF.reconciliation_flag, CTRF.reconciliation_date, CTRF.reconciliation_by, CTRF.created_on, ");
		selectQueryBuff.append(" CTRF.created_by, CTRF.modified_on, CTRF.modified_by, CTRF.transfer_status, CTRF.card_group_set_id, ");
		selectQueryBuff.append(" CTRF.VERSION, CTRF.card_group_id, CTRF.sender_transfer_value, CTRF.receiver_access_fee, ");
		selectQueryBuff.append(" CTRF.receiver_tax1_type, CTRF.receiver_tax1_rate, CTRF.receiver_tax1_value, CTRF.receiver_tax2_type,");
		selectQueryBuff.append(" CTRF.receiver_tax2_rate, CTRF.receiver_tax2_value, CTRF.receiver_validity, CTRF.receiver_transfer_value,");
		selectQueryBuff.append(" CTRF.receiver_bonus_value, CTRF.receiver_grace_period, CTRF.receiver_bonus_validity, ");
		selectQueryBuff.append(" CTRF.card_group_code, CTRF.receiver_valperiod_type, CTRF.temp_transfer_id, CTRF.transfer_profile_id, CTRF.interface_id, ");
		selectQueryBuff.append(" CTRF.commission_profile_id, CTRF.differential_given, CTRF.grph_domain_code, CTRF.source_type,CTRF.sub_service, CTRF.serial_number,CTRF.ext_credit_intfce_type, ");
		selectQueryBuff.append(" ITI.iat_txn_id, ITI.iat_timestamp, ITI.rec_country_code,ITI.rec_nw_code, ITI.iat_error_code, ITI.iat_message, ICM.rec_country_short_name, ICM.currency, ICM.rec_country_name, ");
		selectQueryBuff.append(" ITI.credit_resp_code, ITI.credit_msg, ITI.rec_nw_error_code, ITI.rec_nw_message, ITI.chk_status_resp_code, ITI.sent_amt,  ");
		selectQueryBuff.append(" ITI.fees, ITI.prov_ratio, ITI.exchange_rate, ITI.failed_at, ITI.rec_bonus,ITI.rcvd_amt, ITI.sent_amt_iattorec ");
		selectQueryBuff.append(" FROM PRODUCTS PROD, C2S_TRANSFERS CTRF left outer join  KEY_VALUES KV on (KV.KEY=CTRF.error_code AND KV.TYPE=?) left outer join KEY_VALUES KV2 on  (KV2.KEY=CTRF.transfer_status AND KV2.TYPE=?) , ");
		selectQueryBuff.append(" KEY_VALUES KV3 right outer join C2S_IAT_TRANSFER_ITEMS ITI on (KV3.KEY=ITI.transfer_status AND KV3.TYPE=? ) , SERVICE_TYPE ST,IAT_COUNTRY_MASTER ICM ,USERS U ");
		selectQueryBuff.append(" WHERE CTRF.transfer_date >=? AND CTRF.transfer_date < ? ");

		if (!BTSLUtil.isNullString(pTransferID)) {
			selectQueryBuff.append(" AND CTRF.transfer_id=? ");
		}
		if (!BTSLUtil.isNullString(pSenderMsisdn)) {
			if (!BTSLUtil.isNullString(isSenderPrimary) && ("Y".equalsIgnoreCase(isSenderPrimary))) {
				selectQueryBuff.append(" AND CTRF.sender_id=? ");
			} else {
				selectQueryBuff.append(" AND CTRF.sender_msisdn=? ");
			}
		}
		selectQueryBuff.append(" AND  CTRF.sender_id =U.user_id  AND CTRF.transfer_id =ITI.transfer_id ");

		selectQueryBuff.append(" AND ICM.rec_country_code=ITI.rec_country_code AND ICM.country_status IN ('Y','S') ");
		selectQueryBuff.append(" AND ITI.SERVICE_TYPE=(CASE WHEN ? = 'ALL' THEN CTRF.SERVICE_TYPE  ELSE ? END) ");
		selectQueryBuff.append(" AND ITI.SERVICE_TYPE =ST.SERVICE_TYPE AND CTRF.product_code=PROD.product_code ");
		selectQueryBuff.append(" AND CTRF.network_code=? ");
		selectQueryBuff.append(" ORDER BY CTRF.service_type,CTRF.transfer_date_time DESC,CTRF.transfer_id ");
		String selectQuery = selectQueryBuff.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, QUERY + selectQuery);
		}
		PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery);
		int i = 1;

		pstmtSelect.setString(i++, PretupsI.C2S_ERRCODE_VALUS);
		pstmtSelect.setString(i++, PretupsI.KEY_VALUE_C2C_STATUS);
		pstmtSelect.setString(i++, PretupsI.KEY_VALUE_C2C_STATUS);
		pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(pFromdate));
		pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(p_toDate, 1)));
		if (!BTSLUtil.isNullString(pTransferID)) {
			pstmtSelect.setString(i++, pTransferID);
		}
		if (!BTSLUtil.isNullString(pSenderMsisdn)) {
			if (!BTSLUtil.isNullString(isSenderPrimary) && ("Y".equalsIgnoreCase(isSenderPrimary))) {
				pstmtSelect.setString(i++, phoneVo.getUserId());
			} else {
				pstmtSelect.setString(i++, pSenderMsisdn);
			}
		}
		pstmtSelect.setString(i++, pServiceType);
		pstmtSelect.setString(i++, pServiceType);
		pstmtSelect.setString(i, pNetworkCode);

		return pstmtSelect;
	}

	@Override
	public PreparedStatement loadIATTransferVOListOldQry(Connection pCon,
			String pNetworkCode, Date pFromdate, Date p_toDate,
			String pSenderMsisdn, String pTransferID, String pServiceType,
			UserPhoneVO phoneVo) throws SQLException {
		String methodName = className + "#loadIATTransferVOList_oldQry";
		String isSenderPrimary = phoneVo.getPrimaryNumber();
		  StringBuilder selectQueryBuff = new StringBuilder();

          selectQueryBuff.append("SELECT DISTINCT KV.VALUE errcode,KV2.VALUE txnstatus,KV3.VALUE iat_txn_status, U.user_name,ST.NAME,  ");
          selectQueryBuff.append("PROD.short_name, CTRF.transfer_id,CTRF.transfer_date, CTRF.transfer_date_time, ");
          selectQueryBuff.append("CTRF.network_code, CTRF.sender_id,CTRF.sender_category, CTRF.product_code,  ");
          selectQueryBuff.append("CTRF.sender_msisdn, CTRF.receiver_msisdn,CTRF.receiver_network_code, CTRF.transfer_value, ");
          selectQueryBuff.append("CTRF.error_code, CTRF.request_gateway_type, ");
          selectQueryBuff.append("CTRF.request_gateway_code, CTRF.reference_id, CTRF.SERVICE_TYPE, CTRF.differential_applicable, ");
          selectQueryBuff.append("CTRF.pin_sent_to_msisdn, CTRF.LANGUAGE, CTRF.country, CTRF.skey, CTRF.skey_generation_time,  ");
          selectQueryBuff.append("CTRF.skey_sent_to_msisdn, CTRF.request_through_queue, CTRF.credit_back_status, CTRF.quantity, ");
          selectQueryBuff.append("CTRF.reconciliation_flag, CTRF.reconciliation_date, CTRF.reconciliation_by, CTRF.created_on, ");
          selectQueryBuff.append("CTRF.created_by, CTRF.modified_on, CTRF.modified_by, CTRF.transfer_status, CTRF.card_group_set_id, ");
          selectQueryBuff.append("CTRF.VERSION, CTRF.card_group_id, CTRF.sender_transfer_value, CTRF.receiver_access_fee, ");
          selectQueryBuff.append("CTRF.receiver_tax1_type, CTRF.receiver_tax1_rate, CTRF.receiver_tax1_value, CTRF.receiver_tax2_type,");
          selectQueryBuff.append("CTRF.receiver_tax2_rate, CTRF.receiver_tax2_value, CTRF.receiver_validity, CTRF.receiver_transfer_value,");
          selectQueryBuff.append("CTRF.receiver_bonus_value, CTRF.receiver_grace_period, CTRF.receiver_bonus_validity, ");
          selectQueryBuff.append("CTRF.card_group_code, CTRF.receiver_valperiod_type, CTRF.temp_transfer_id, CTRF.transfer_profile_id, CTRIS.interface_id, ");
          selectQueryBuff.append("CTRF.commission_profile_id, CTRF.differential_given, CTRF.grph_domain_code, CTRF.source_type,CTRF.sub_service, CTRF.serial_number,CTRF.ext_credit_intfce_type, ");
          selectQueryBuff.append("ITI.iat_txn_id, ITI.iat_timestamp, ITI.rec_country_code,ITI.rec_nw_code, ITI.iat_error_code, ITI.iat_message, ICM.rec_country_short_name, ICM.currency, ICM.rec_country_name, ");
          selectQueryBuff.append("ITI.credit_resp_code, ITI.credit_msg, ITI.rec_nw_error_code, ITI.rec_nw_message, ITI.chk_status_resp_code, ITI.sent_amt,  ");
          selectQueryBuff.append("ITI.fees, ITI.prov_ratio, ITI.exchange_rate, ITI.failed_at, ITI.rec_bonus,ITI.rcvd_amt, ITI.sent_amt_iattorec ");
          selectQueryBuff.append("FROM PRODUCTS PROD,C2S_TRANSFERS_OLD CTRF left outer join KEY_VALUES KV on (KV.KEY=CTRF.error_code AND KV.TYPE=?)  left outer join KEY_VALUES KV2 on (KV2.KEY=CTRF.transfer_status AND KV2.TYPE=? ) ," );
          selectQueryBuff.append("KEY_VALUES KV3 right outer join C2S_IAT_TRANSFER_ITEMS ITI on (KV3.KEY=ITI.transfer_status AND KV3.TYPE=? ) , SERVICE_TYPE ST,IAT_COUNTRY_MASTER ICM ,USERS U, C2S_TRANSFER_ITEMS CTRIS");
          selectQueryBuff.append("WHERE CTRF.transfer_date >=? AND CTRF.transfer_date < ? ");

          if (!BTSLUtil.isNullString(pTransferID)) {
              selectQueryBuff.append("AND CTRF.transfer_id=? ");
          }
          if (!BTSLUtil.isNullString(pSenderMsisdn)) {
              if (!BTSLUtil.isNullString(isSenderPrimary) && ("Y".equalsIgnoreCase(isSenderPrimary))) {
                  selectQueryBuff.append("AND CTRF.sender_id=? ");
              } else {
                  selectQueryBuff.append("AND CTRF.sender_msisdn=? ");
              }
          }
          selectQueryBuff.append("AND  CTRF.sender_id =U.user_id  AND CTRF.transfer_id =ITI.transfer_id ");
          selectQueryBuff.append("AND CTRF.transfer_id = CTRIS.transfer_id AND CTRIS.msisdn=CTRF.receiver_msisdn ");
          selectQueryBuff.append("AND ICM.rec_country_code=ITI.rec_country_code AND ICM.country_status IN ('Y','S') ");
          selectQueryBuff.append("AND ITI.SERVICE_TYPE=(CASE WHEN ? = 'ALL' THEN CTRF.SERVICE_TYPE  ELSE ? END) ");
          selectQueryBuff.append("AND ITI.SERVICE_TYPE =ST.SERVICE_TYPE AND CTRF.product_code=PROD.product_code ");
          selectQueryBuff.append("AND CTRF.network_code=? AND  ");
          selectQueryBuff.append("ORDER BY CTRF.service_type,CTRF.transfer_date_time DESC,CTRF.transfer_id");
          String selectQuery = selectQueryBuff.toString();
          if (LOG.isDebugEnabled()) {
        	  LOG.debug(methodName, QUERY + selectQuery);
          }
         PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery);
          int i = 1;
          
          pstmtSelect.setString(i++, PretupsI.C2S_ERRCODE_VALUS);
          pstmtSelect.setString(i++, PretupsI.KEY_VALUE_C2C_STATUS);
          pstmtSelect.setString(i++, PretupsI.KEY_VALUE_C2C_STATUS);
          pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(pFromdate));
          pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(p_toDate, 1)));
          if (!BTSLUtil.isNullString(pTransferID)) {
              pstmtSelect.setString(i++, pTransferID);
          }
          if (!BTSLUtil.isNullString(pSenderMsisdn)) {
              if (!BTSLUtil.isNullString(isSenderPrimary) && ("Y".equalsIgnoreCase(isSenderPrimary))) {
                  pstmtSelect.setString(i++, phoneVo.getUserId());
              } else {
                  pstmtSelect.setString(i++, pSenderMsisdn);
              }
          }
          pstmtSelect.setString(i++, pServiceType);
          pstmtSelect.setString(i++, pServiceType);
          pstmtSelect.setString(i, pNetworkCode);
         
		return pstmtSelect;
	}

	@Override
	public PreparedStatement loadIATTransferItemsVOListQry(Connection pCon,
			String pTransferID) throws SQLException, ParseException {
		//local_index_implemented
		String methodName = className+"#loadIATTransferItemsVOListQry";
		StringBuilder selectQueryBuff = new StringBuilder();
        selectQueryBuff.append("SELECT KV.value,KV2.value transfer_type_value,KV3.value in_response_code_desc, ");
        selectQueryBuff.append("transfer_id, sender_msisdn,receiver_msisdn, quantity, created_on, sender_previous_balance, ");
        selectQueryBuff.append("sender_post_balance,receiver_previous_balance,receiver_post_balance, SENDER_CR_BK_POST_BAL, ");
        selectQueryBuff.append("SENDER_CR_BK_POST_BAL, transfer_type, reconcile_entry_type, reconciliation_date, SENDER_CR_SETL_PREV_BAL, SENDER_CR_SETL_POST_BAL, validation_status, ");
        selectQueryBuff.append("update_status,credit_back_status,DEBIT_STATUS,credit_status,reconciliation_flag ");
        selectQueryBuff.append("sender_transfer_value,receiver_transfer_value, interface_type, interface_id, ");
        selectQueryBuff.append("interface_response_code, interface_reference_id, subscriber_type, ");
        selectQueryBuff.append("service_class_code, msisdn_previous_expiry, msisdn_new_expiry, transfer_status, ");
        selectQueryBuff.append("transfer_date, transfer_date_time,  first_call, prefix_id, ");
        selectQueryBuff.append("service_class_id, protocol_status, account_status,reference_id,language,country ");
        selectQueryBuff.append("FROM c2s_transfers left outer join key_values KV on (KV.key=transfer_status AND KV.type=?) left outer join key_values KV2 on ( KV2.key=transfer_type AND KV2.type=? ) left outer join key_values KV3 on (KV3.key=interface_response_code AND KV3.type=? ) ");
        selectQueryBuff.append("WHERE transfer_date=? AND transfer_id=?  ");

        String selectQuery = selectQueryBuff.toString();
        if (LOG.isDebugEnabled()) {
        	LOG.debug(methodName, QUERY + selectQuery);
        }
        PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery);
        int i = 1;
        
        pstmtSelect.setString(i++, PretupsI.KEY_VALUE_C2C_STATUS);
        pstmtSelect.setString(i++, PretupsI.KEY_VALUE_C2C_STATUS);
        pstmtSelect.setString(i++, PretupsI.KEY_VALUE_IN_RESPONSE_CODE);
        pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId(pTransferID)));
        pstmtSelect.setString(i, pTransferID);
		return pstmtSelect;
	}

	@Override
	public PreparedStatement loadIATTransferItemsVOListOldQry(Connection pCon,
			String pTransferID) throws SQLException {
		String methodName = className+"#loadIATTransferItemsVOList_oldQry";
		StringBuilder selectQueryBuff = new StringBuilder();
        selectQueryBuff.append("SELECT KV.value,KV2.value transfer_type_value,KV3.value in_response_code_desc,");
        selectQueryBuff.append("transfer_id, msisdn, entry_date, request_value, previous_balance, ");
        selectQueryBuff.append("post_balance, user_type, transfer_type, entry_type, validation_status, ");
        selectQueryBuff.append("update_status, transfer_value, interface_type, interface_id, ");
        selectQueryBuff.append("interface_response_code, interface_reference_id, subscriber_type, ");
        selectQueryBuff.append("service_class_code, msisdn_previous_expiry, msisdn_new_expiry, transfer_status,");
        selectQueryBuff.append("transfer_date, transfer_date_time, entry_date_time, first_call, sno, prefix_id,");
        selectQueryBuff.append("service_class_id, protocol_status, account_status,reference_id,language,country ");
        selectQueryBuff.append("FROM c2s_transfer_items left outer join key_values KV on ( KV.key=transfer_status AND KV.type=? ) left outer join key_values KV2 on (KV2.key=transfer_type AND KV2.type=? ) left outer join key_values KV3 on (KV3.key=interface_response_code AND KV3.type=?)  ");
        selectQueryBuff.append("WHERE transfer_id=?  ");
        selectQueryBuff.append("ORDER BY sno");
        String selectQuery = selectQueryBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, QUERY + selectQuery);
        }
        PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery);
        int i = 1;
        pstmtSelect.setString(i++, PretupsI.KEY_VALUE_C2C_STATUS);
        pstmtSelect.setString(i++, PretupsI.KEY_VALUE_C2C_STATUS);
        pstmtSelect.setString(i++, PretupsI.KEY_VALUE_IN_RESPONSE_CODE);
        pstmtSelect.setString(i, pTransferID);
       
        return pstmtSelect;
	}
	
	

}

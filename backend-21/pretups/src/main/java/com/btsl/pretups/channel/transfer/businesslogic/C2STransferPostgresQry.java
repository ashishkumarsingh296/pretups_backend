package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;

public class C2STransferPostgresQry implements C2STransferQry {

	private static Log LOG = LogFactory.getLog(C2STransferDAO.class.getName());
	public static OperatorUtilI _operatorUtilI = null;
	private String className = "C2STransferOracleQry";
	static {
		try {
			_operatorUtilI = (OperatorUtilI) Class
					.forName(
							(String) PreferenceCache
									.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS))
					.newInstance();
		} catch (Exception e) {

			LOG.errorTrace("static", e);
			EventHandler.handle(
					EventIDI.SYSTEM_ERROR,
					EventComponentI.SYSTEM,
					EventStatusI.RAISED,
					EventLevelI.FATAL,
					"BuddyMgtAction",
					"",
					"",
					"",
					"Exception while loading the operator util class in class :"
							+ C2STransferDAO.class.getName() + ":"
							+ e.getMessage());
		}
	}

	@Override
	public PreparedStatement loadC2STransferVOListQry(Connection pCon,
			Date p_fromDate, Date p_toDate, String p_senderMsisdn,
			String isSenderPrimary, String p_receiverMsisdn,
			String p_transferID, String p_networkCode, String p_serviceType,
			UserPhoneVO phoneVo) throws SQLException {
		final String methodName = className + "#loadC2STransferVOList";
		//local_index_implemented
		String tbl_name = "c2s_transfers";
		StringBuilder selectQueryBuff = new StringBuilder();
		selectQueryBuff
				.append(" SELECT KV.value errcode,KV2.value txnstatus,U.user_name,ST.name, ");
		selectQueryBuff
				.append("PROD.short_name, CTRF.transfer_id,CTRF.transfer_date, CTRF.transfer_date_time, ");
		selectQueryBuff
				.append("CTRF.network_code, sender_id,CTRF.sender_category, CTRF.product_code, ");
		selectQueryBuff
				.append("CTRF.sender_msisdn, CTRF.receiver_msisdn,CTRF.receiver_network_code, CTRF.transfer_value, ");
		selectQueryBuff.append("CTRF.error_code, CTRF.request_gateway_type, ");
		selectQueryBuff
				.append("CTRF.request_gateway_code, CTRF.reference_id, CTRF.service_type, CTRF.differential_applicable, ");
		selectQueryBuff
				.append("CTRF.pin_sent_to_msisdn, CTRF.language, CTRF.country, CTRF.skey, CTRF.skey_generation_time, ");
		selectQueryBuff
				.append("CTRF.skey_sent_to_msisdn, CTRF.request_through_queue, CTRF.credit_back_status, CTRF.quantity, ");
		selectQueryBuff
				.append("CTRF.reconciliation_flag, CTRF.reconciliation_date, CTRF.reconciliation_by, CTRF.created_on, ");
		selectQueryBuff
				.append("CTRF.created_by, CTRF.modified_on, CTRF.modified_by, CTRF.transfer_status, CTRF.card_group_set_id, ");
		selectQueryBuff
				.append("CTRF.version, CTRF.card_group_id, CTRF.sender_transfer_value, CTRF.receiver_access_fee, ");
		selectQueryBuff
				.append("CTRF.receiver_tax1_type, CTRF.receiver_tax1_rate, CTRF.receiver_tax1_value, CTRF.receiver_tax2_type, ");
		selectQueryBuff
				.append("CTRF.receiver_tax2_rate, CTRF.receiver_tax2_value, CTRF.receiver_validity, CTRF.receiver_transfer_value, ");
		selectQueryBuff
				.append("CTRF.receiver_bonus_value, CTRF.receiver_grace_period, CTRF.receiver_bonus_validity, CTRF.subs_sid, ");
		selectQueryBuff
				.append("CTRF.card_group_code, CTRF.receiver_valperiod_type, CTRF.temp_transfer_id, CTRF.transfer_profile_id, ");
		selectQueryBuff
				.append("CTRF.commission_profile_id, CTRF.differential_given, CTRF.grph_domain_code, CTRF.source_type,CTRF.sub_service, CTRF.serial_number ");
		try {
			if (_operatorUtilI.getNewDataAftrTbleMerging(p_fromDate, p_toDate)) {
				selectQueryBuff
						.append(" , CTRF.subs_sid,CTRF.cell_id,CTRF.switch_id ,CTRF.reversal_id ");
				selectQueryBuff
						.append(",CTRF.info1,CTRF.info2,CTRF.info3,CTRF.info4,CTRF.info5,CTRF.info6,CTRF.info7,CTRF.info8,CTRF.info9,CTRF.info10 ");
				selectQueryBuff
						.append(",CTRF.ext_credit_intfce_type,CTRF.multicurrency_detail ");
				selectQueryBuff
						.append(", CTRF.bonus_details,CTRF.bonus_amount, CTRF.promo_previous_balance, CTRF.promo_post_balance, CTRF.promo_previous_expiry, CTRF.promo_new_expiry ,NTWK.network_name ");
			} else {
				tbl_name = "c2s_transfers_old ";
			}
		} catch (BTSLBaseException e) {
			LOG.errorTrace(methodName, e);
		}
		selectQueryBuff
				.append("FROM "
						+ tbl_name
						+ " CTRF LEFT JOIN key_values KV ON (KV.key=CTRF.error_code AND KV.TYPE=?) LEFT JOIN key_values KV2 ON (KV2.key=CTRF.transfer_status AND KV2.type=?),products PROD,service_type ST,users U,networks NTWK ");
		selectQueryBuff
				.append("WHERE CTRF.transfer_date >=? AND CTRF.transfer_date <? AND CTRF.network_code=? AND U.user_id = CTRF.sender_id  ");
		selectQueryBuff
				.append("AND CTRF.service_type=case ? when 'ALL' then CTRF.service_type else ? end ");
		selectQueryBuff
				.append("AND CTRF.product_code=PROD.product_code AND ST.service_type=CTRF.service_type AND CTRF.network_code=NTWK.network_code ");
		if (!BTSLUtil.isNullString(p_senderMsisdn)) {
			if (!BTSLUtil.isNullString(isSenderPrimary)
					&& ("Y".equalsIgnoreCase(isSenderPrimary))) {
				selectQueryBuff.append("AND CTRF.sender_id=? ");
			} else {
				selectQueryBuff.append("AND CTRF.sender_msisdn=? ");
			}
		}
		if (!BTSLUtil.isNullString(p_receiverMsisdn)) {
			selectQueryBuff.append("AND CTRF.receiver_msisdn=? ");
		}
		if (!BTSLUtil.isNullString(p_transferID)) {
			selectQueryBuff.append("AND CTRF.transfer_id=? ");
		}
		selectQueryBuff
				.append("ORDER BY CTRF.service_type,CTRF.transfer_date_time DESC,CTRF.transfer_id ");

		final String selectQuery = selectQueryBuff.toString();
		PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery);
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "select query:" + selectQuery);
		}
		int i = 1;
		pstmtSelect.setString(i, PretupsI.C2S_ERRCODE_VALUS);
		i++;
		pstmtSelect.setString(i, PretupsI.KEY_VALUE_C2C_STATUS);
		i++;
		pstmtSelect.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
		i++;
		pstmtSelect.setDate(i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil
				.addDaysInUtilDate(p_toDate, 1)));
		i++;
		pstmtSelect.setString(i, p_networkCode);
		i++;
		pstmtSelect.setString(i, p_serviceType);
		i++;
		pstmtSelect.setString(i, p_serviceType);
		i++;
		if (!BTSLUtil.isNullString(p_senderMsisdn)) {
			if (!BTSLUtil.isNullString(isSenderPrimary)
					&& ("Y".equalsIgnoreCase(isSenderPrimary))) {
				pstmtSelect.setString(i, phoneVo.getUserId());
				i++;
			} else {
				pstmtSelect.setString(i, p_senderMsisdn);
				i++;
			}
		}
		if (!BTSLUtil.isNullString(p_receiverMsisdn)) {
			pstmtSelect.setString(i, p_receiverMsisdn);
			i++;
		}
		if (!BTSLUtil.isNullString(p_transferID)) {
			pstmtSelect.setString(i, p_transferID);
			i++;
		}
		return pstmtSelect;
	}

	@Override
	public String updateReconcilationStatusQry(C2STransferVO p_c2sTransferVO) {
		StringBuilder updateQuery = new StringBuilder();
		//local_index_implemented
		updateQuery
				.append(" UPDATE c2s_transfers SET transfer_status=?, reconciliation_by=?, reconciliation_date=?,");
		updateQuery
				.append("reconciliation_flag='Y', modified_by=?, modified_on=? ");
		updateQuery.append(", error_code = coalesce(error_code,").append("'").append(PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS).append("')");
		updateQuery.append(", credit_back_status = ? ");
		if (p_c2sTransferVO.getDifferentialApplicable() != null) {
			updateQuery
					.append(", differential_applicable = ?, differential_given=? ");
		}
		updateQuery.append(", OTF_APPLICABLE = ? ");
		updateQuery
				.append("WHERE transfer_date=? AND transfer_id=? AND (transfer_status=? OR transfer_status=?)");
		return updateQuery.toString();
	}

	@Override
	public PreparedStatement loadC2STransferItemsVOList_oldQry(
			Connection p_con, String p_transferID) throws SQLException {
		final String methodName = className + "#loadC2STransferItemsVOList_old";
		StringBuilder selectQueryBuff = new StringBuilder();
		selectQueryBuff
				.append("SELECT KV.value,KV2.value transfer_type_value,KV3.value in_response_code_desc,");
		selectQueryBuff
				.append("transfer_id, msisdn, entry_date, request_value, previous_balance, ");
		selectQueryBuff
				.append("post_balance, user_type, transfer_type, entry_type, validation_status, ");
		selectQueryBuff
				.append("update_status, transfer_value, interface_type, interface_id, ");
		selectQueryBuff
				.append("interface_response_code, interface_reference_id, subscriber_type, ");
		selectQueryBuff
				.append("service_class_code, msisdn_previous_expiry, msisdn_new_expiry, transfer_status,");
		selectQueryBuff
				.append("transfer_date, transfer_date_time, entry_date_time, first_call, sno, prefix_id,");
		selectQueryBuff
				.append("service_class_id, protocol_status, account_status,reference_id,language,country ");
		selectQueryBuff
				.append("FROM c2s_transfer_items LEFT JOIN key_values KV ON (KV.key=transfer_status AND KV.type=?)   ");
		selectQueryBuff
				.append("LEFT JOIN key_values KV2 ON (KV.key=transfer_type AND KV2.type=?)  ");
		selectQueryBuff
				.append("LEFT JOIN key_values KV3 ON (KV3.key=interface_response_code AND KV3.type=?) ");
		selectQueryBuff.append("WHERE transfer_id=? ");
		selectQueryBuff.append("ORDER BY sno");
		final String selectQuery = selectQueryBuff.toString();
		PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "select query:" + selectQuery);
		}
		pstmtSelect = p_con.prepareStatement(selectQuery);
		int i = 1;
		pstmtSelect.setString(i, PretupsI.KEY_VALUE_C2C_STATUS);
		i++;
		pstmtSelect.setString(i, PretupsI.KEY_VALUE_C2C_STATUS);
		i++;
		pstmtSelect.setString(i, PretupsI.KEY_VALUE_IN_RESPONSE_CODE);
		i++;
		pstmtSelect.setString(i, p_transferID);
		i++;

		return pstmtSelect;

	}

	@Override
	public PreparedStatement loadC2STransferItemsVOListQry(Connection p_con,
			String p_transferID) throws SQLException, ParseException {
		final String methodName = className + "#loadC2STransferItemsVOList";
		StringBuilder selectQueryBuff = new StringBuilder();
		//local_index_implemented
		selectQueryBuff
				.append("SELECT KV.value,KV2.value transfer_type_value,KV3.value in_response_code_desc,");
		selectQueryBuff
				.append("transfer_id, sender_msisdn,receiver_msisdn, transfer_value, created_on, sender_previous_balance,SENDER_CR_SETL_PREV_BAL,SENDER_CR_SETL_POST_BAL,SENDER_CR_BK_PREV_BAL,SENDER_CR_BK_POST_BAL ,");
		selectQueryBuff
				.append("SENDER_CR_SETL_PREV_BAL,SENDER_CR_SETL_POST_BAL,sender_post_balance,receiver_previous_balance,receiver_post_balance, transfer_type,  validation_status, ");
		selectQueryBuff
				.append("reconciliation_date,reconciliation_flag,reconcile_entry_type, credit_back_status,DEBIT_STATUS,credit_status, sender_transfer_value,receiver_transfer_value, interface_type, interface_id, ");
		selectQueryBuff
				.append("interface_response_code, interface_reference_id, subscriber_type, ");
		selectQueryBuff
				.append("service_class_code, msisdn_previous_expiry, msisdn_new_expiry, transfer_status,");
		selectQueryBuff
				.append("transfer_date, transfer_date_time,  first_call, prefix_id,");
		selectQueryBuff
				.append("service_class_id, protocol_status, account_status,reference_id,language,country,multicurrency_detail ");
		selectQueryBuff
				.append(",SERVICE_PROVIDER_NAME , lms_profile, lms_version, subs_sid ,ct.bonus_amount, (select adj.transfer_value from  adjustments adj ");  
		selectQueryBuff 
						.append( " where ct.transfer_id = adj.reference_id and adj.ADJUSTMENT_ID like '%U' ) differntial "); 
		selectQueryBuff
				.append(" FROM c2s_transfers ct LEFT JOIN key_values KV ON (KV.key=transfer_status AND KV.type=?)  ");
		selectQueryBuff
				.append("LEFT JOIN key_values KV2 ON (KV2.key=transfer_type AND KV2.type=?)  ");
		selectQueryBuff
				.append("LEFT JOIN key_values KV3 ON (KV3.key=interface_response_code AND KV3.type=?)  ");
		selectQueryBuff
				.append("WHERE transfer_id=? AND transfer_date=?  ");
		final String selectQuery = selectQueryBuff.toString();
		PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "select query:" + selectQuery);
		}
		pstmtSelect = p_con.prepareStatement(selectQuery);
		int i = 1;
		pstmtSelect.setString(i, PretupsI.KEY_VALUE_C2C_STATUS);
		i++;
		pstmtSelect.setString(i, PretupsI.KEY_VALUE_C2C_STATUS);
		i++;
		pstmtSelect.setString(i, PretupsI.KEY_VALUE_IN_RESPONSE_CODE);
		i++;
		pstmtSelect.setString(i, p_transferID);
		i++;
		pstmtSelect.setDate(i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId(p_transferID)));
		i++;
		return pstmtSelect;

	}
	
	@Override
	public String loadLastTransfersStatusVOForC2SWithExtRefNumQry() {
		StringBuilder strBuff = new StringBuilder();
		//local_index_already_implemented
		strBuff
				.append(" SELECT C2S.transfer_id,C2S.sender_id, C2S.service_type,C2S.sender_msisdn,C2S.receiver_msisdn,C2S.transfer_value,C2S.transfer_status,C2S.receiver_transfer_value,");
		strBuff.append(" KV.value,C2S.transfer_date_time,P.short_name,P.product_short_code,ST.name,C2S.error_code ");
        strBuff.append(" FROM c2s_transfers C2S,products P, key_values KV,service_type ST WHERE ");
        strBuff.append(" TRANSFER_DATE >= date_trunc('day',?::TIMESTAMP) AND TRANSFER_DATE <= date_trunc('day',?::TIMESTAMP)  AND ");
        strBuff.append(" C2S.REFERENCE_ID=?  ");
        strBuff.append(" AND C2S.product_code=P.product_code AND C2S.transfer_status=KV.key AND KV.type=?  ");
        strBuff.append(" AND C2S.service_type=ST.service_type AND ST.module=? ");
		return strBuff.toString();
	}
	
	@Override
	public PreparedStatement loadC2STransferVOListQry(Connection p_con, String p_networkCode, Date p_fromDate, Date p_toDate, ArrayList userList, String p_receiverMsisdn, String p_transferID, String p_serviceType, String senderCat,ListValueVO user ) throws SQLException {
		final String methodName = className + "#loadC2STransferVOList";
		//local_index_already_implemented
		 String tbl_name = "c2s_transfers";
		StringBuilder selectQueryBuff = new StringBuilder();
		selectQueryBuff.append("SELECT KV.value errcode,KV2.value txnstatus,U.user_name,ST.name, ");
        selectQueryBuff.append("PROD.short_name, CTRF.transfer_id,CTRF.transfer_date, CTRF.transfer_date_time, ");
        selectQueryBuff.append("CTRF.network_code, sender_id,CTRF.sender_category, CTRF.product_code, ");
        selectQueryBuff.append("CTRF.sender_msisdn, CTRF.receiver_msisdn,CTRF.receiver_network_code, CTRF.transfer_value, ");
        selectQueryBuff.append("CTRF.error_code, CTRF.request_gateway_type, ");
        selectQueryBuff.append("CTRF.request_gateway_code, CTRF.reference_id, CTRF.service_type, CTRF.differential_applicable, ");
        selectQueryBuff.append("CTRF.pin_sent_to_msisdn, CTRF.language, CTRF.country, CTRF.skey, CTRF.skey_generation_time, ");
        selectQueryBuff.append("CTRF.skey_sent_to_msisdn, CTRF.request_through_queue, CTRF.credit_back_status, CTRF.quantity, ");
        selectQueryBuff.append("CTRF.reconciliation_flag, CTRF.reconciliation_date, CTRF.reconciliation_by, CTRF.created_on, ");
        selectQueryBuff.append("CTRF.created_by, CTRF.modified_on, CTRF.modified_by, CTRF.transfer_status, CTRF.card_group_set_id, ");
        selectQueryBuff.append("CTRF.version, CTRF.card_group_id, CTRF.sender_transfer_value, CTRF.receiver_access_fee, ");
        selectQueryBuff.append("CTRF.receiver_tax1_type, CTRF.receiver_tax1_rate, CTRF.receiver_tax1_value, CTRF.receiver_tax2_type, ");
        selectQueryBuff.append("CTRF.receiver_tax2_rate, CTRF.receiver_tax2_value, CTRF.receiver_validity, CTRF.receiver_transfer_value,");
        selectQueryBuff.append("CTRF.receiver_bonus_value, CTRF.receiver_grace_period, CTRF.receiver_bonus_validity, ");
        selectQueryBuff.append("CTRF.card_group_code, CTRF.receiver_valperiod_type, CTRF.temp_transfer_id, CTRF.transfer_profile_id,");
        selectQueryBuff
            .append("CTRF.commission_profile_id, CTRF.differential_given, CTRF.grph_domain_code, CTRF.source_type,CTRF.sub_service, CTRF.serial_number, CTRF.active_user_id ");
        selectQueryBuff.append(" ,CTRF.cell_id,CTRF.switch_id ");

        try {
			if (_operatorUtilI.getNewDataAftrTbleMerging(p_fromDate, p_toDate)) {
			    selectQueryBuff.append(", CTRF.bonus_details, CTRF.promo_previous_balance, CTRF.promo_post_balance, CTRF.promo_previous_expiry, CTRF.promo_new_expiry ");
			} else {
			    tbl_name = "c2s_transfers_old";
			}
		} catch (BTSLBaseException e) {
			LOG.errorTrace(methodName, e);
		}

        selectQueryBuff.append("FROM "+ tbl_name +"CTRF LEFT JOIN key_values KV ON (KV.key=CTRF.error_code AND KV.type= ?) LEFT JOIN key_values KV2 ON(KV2.key=CTRF.transfer_status AND KV2.type= ?), products PROD,service_type ST,users U");
        selectQueryBuff.append("WHERE CTRF.transfer_date >=? AND CTRF.transfer_date < ?  ");
        if (!BTSLUtil.isNullString(p_transferID)) {
            selectQueryBuff.append("AND CTRF.transfer_id=? ");
        }
        if (!BTSLUtil.isNullString(p_receiverMsisdn)) {
            selectQueryBuff.append("AND CTRF.receiver_msisdn=? ");
        }
        if (userList != null && userList.size() == 1) {
            user = (ListValueVO) userList.get(0);
            if (user.getType().equals(PretupsI.CHANNEL_USER_TYPE)) {
                selectQueryBuff.append("AND CTRF.sender_id = ? AND CTRF.active_user_id= ? ");
            } else {
                if (PretupsI.BCU_USER.equalsIgnoreCase(senderCat) || PretupsI.CUSTOMER_CARE.equalsIgnoreCase(senderCat) || TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(senderCat) || TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(senderCat)) {
                    senderCat = user.getCodeName();
                }
                selectQueryBuff.append(" AND CTRF.sender_category = ? AND CTRF.active_user_id= ? ");
            }
        }
        if (!(PretupsI.BCU_USER.equalsIgnoreCase(senderCat) || PretupsI.CUSTOMER_CARE.equalsIgnoreCase(senderCat) || TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(senderCat) || TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(senderCat)) && (userList != null && userList.size() > 1)) {
            selectQueryBuff.append("AND CTRF.sender_id = ? ");
        }
        selectQueryBuff.append("AND CTRF.network_code= ? AND U.user_id = CTRF.sender_id ");
        selectQueryBuff.append("AND CTRF.service_type= CASE ? WHEN 'ALL' THEN CTRF.service_type ELSE ? END ");
        selectQueryBuff.append("AND CTRF.product_code=PROD.product_code AND ST.service_type=CTRF.service_type ");

        selectQueryBuff.append("ORDER BY CTRF.service_type,CTRF.transfer_date_time DESC,CTRF.transfer_id ");
        final String selectQuery = selectQueryBuff.toString();
        PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "select query:" + selectQuery);
        }
        pstmtSelect = p_con.prepareStatement(selectQuery);
        int i = 1;
        pstmtSelect.setString(i, PretupsI.C2S_ERRCODE_VALUS);
        i++;
        pstmtSelect.setString(i, PretupsI.KEY_VALUE_C2C_STATUS);
        i++;
        pstmtSelect.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
        i++;
        pstmtSelect.setDate(i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(p_toDate, 1)));
        i++;
        if (!BTSLUtil.isNullString(p_transferID)) {
            pstmtSelect.setString(i, p_transferID);
            i++;
        }
        if (!BTSLUtil.isNullString(p_receiverMsisdn)) {
            pstmtSelect.setString(i, p_receiverMsisdn);
            i++;
        }
        if (userList != null && userList.size() == 1) {
            if (user.getType().equals(PretupsI.CHANNEL_USER_TYPE)) {
                pstmtSelect.setString(i, user.getValue());
                i++;
                pstmtSelect.setString(i, user.getValue());
                i++;
            } else {
                pstmtSelect.setString(i, senderCat);
                i++;
                pstmtSelect.setString(i, user.getValue());
                i++;
            }
        } else if (userList != null && userList.size() > 1) {
            for (int k = 0; k < userList.size(); k++) {
                user = (ListValueVO) userList.get(k);
                if (user.getType().equals(PretupsI.CHANNEL_USER_TYPE)) {
                    pstmtSelect.setString(i, user.getValue());
                    i++;
                    break;
                } else {
                    user = new ListValueVO();
                }
            }
        }
        pstmtSelect.setString(i, p_networkCode);
        i++;
        pstmtSelect.setString(i, p_serviceType);
        i++;
        pstmtSelect.setString(i, p_serviceType);
        i++;

		return pstmtSelect;
	}

	@Override
	public PreparedStatement loadC2SReconciliationListQry(Connection p_con, String p_networkCode, Date p_fromDate, Date p_toDate, String p_serviceType ) throws SQLException {
		final String methodName = className + "#loadC2SReconciliationList";
		//local_index_implemented
		StringBuilder selectQueryBuff = new StringBuilder();
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
        selectQueryBuff.append("CTRF.commission_profile_id, CTRF.differential_given, CTRF.grph_domain_code, CTRF.source_type,CTRF.serial_number,U.owner_id ");
        selectQueryBuff.append(", UP.phone_language, UP.msisdn, UP.country phcountry,CTRF.ext_credit_intfce_type,CTRF.SUB_SERVICE ");
        selectQueryBuff.append("FROM c2s_transfers CTRF LEFT JOIN key_values KV ON (KV.key=CTRF.error_code AND KV.type=?) LEFT JOIN key_values KV1 ON (KV1.key=CTRF.transfer_status AND KV1.type=?), products PROD,service_type ST,users U,user_phones UP  ");
        selectQueryBuff.append("WHERE CTRF.transfer_date >=? AND CTRF.transfer_date < ? AND U.user_id = UP.user_id AND UP.primary_number='Y' AND U.user_id = CTRF.sender_id");
        selectQueryBuff.append("AND CTRF.service_type=? AND CTRF.product_code=PROD.product_code ");
        selectQueryBuff.append("AND (CTRF.reconciliation_flag <> 'Y' OR CTRF.reconciliation_flag IS NULL ) ");
        selectQueryBuff.append("AND ST.service_type=CTRF.service_type ");
        selectQueryBuff.append("AND (CTRF.transfer_status=? OR CTRF.transfer_status=? ) ");
        selectQueryBuff.append("AND CTRF.network_code=? ");
        selectQueryBuff.append("ORDER BY CTRF.transfer_date_time DESC ,CTRF.transfer_id ");
        final String selectQuery = selectQueryBuff.toString();
        PreparedStatement pstmtSelect = p_con.prepareStatement(selectQuery);
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadC2STransferVOList", "select query:" + selectQuery);
        }
        pstmtSelect = p_con.prepareStatement(selectQuery);
        int i = 1;
        pstmtSelect.setDate(i, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
        i++;
        pstmtSelect.setDate(i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(p_toDate, 1)));
        i++;
        pstmtSelect.setString(i, PretupsI.C2S_ERRCODE_VALUS);
        i++;
        pstmtSelect.setString(i, PretupsI.KEY_VALUE_TYPE_REOCN);
        i++;
        pstmtSelect.setString(i, p_serviceType);
        i++;
        pstmtSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
        i++;
        // by sandeep ID REC001
        // as now we are loading all the UNDERPROCESS or AMBIGUOUS txn. for
        // the reconciliation

        pstmtSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
        i++;
        pstmtSelect.setString(i, p_networkCode);
        i++;

		return pstmtSelect;

	}
	
	/*@Override
	public String getChanneltransAmtDatewiseQry(Date p_fromDate, Date p_toDate,String p_amount) {
		final String methodName="getChanneltransAmtDatewiseQry";
		//local_index_implemented
		String tbl_name = "c2s_transfers";
        try {
			if (!_operatorUtilI.getNewDataAftrTbleMerging(p_fromDate, p_toDate)) {
			    tbl_name = "c2s_transfers_old";
			}
		} catch (BTSLBaseException e) {
			LOG.errorTrace(methodName, e);
		}

		StringBuilder selectQueryBuff = new StringBuilder();
		selectQueryBuff.append("SELECT  CTRF.transfer_id,CTRF.transfer_date,CTRF.network_code,CTRF.sender_msisdn,CTRF.receiver_msisdn,CTRF.transfer_date_time ");
        selectQueryBuff.append(",CTRF.error_code,CTRF.transfer_status,CTRF.SERVICE_TYPE,CTRF.quantity,CTRF.transfer_value ");
        selectQueryBuff.append(",KV1.VALUE transtatus,ST.NAME servicename ");
        selectQueryBuff.append("FROM " + tbl_name + " CTRF RIGHT JOIN KEY_VALUES KV1 ON (KV1.KEY=CTRF.transfer_status AND KV1.TYPE=?),SERVICE_TYPE ST ");
        selectQueryBuff.append("WHERE CTRF.transfer_date>=? AND CTRF.transfer_date<=? AND CTRF.network_code=? ");
        selectQueryBuff.append("AND CTRF.sender_msisdn=? ");
        selectQueryBuff.append("AND CTRF.receiver_msisdn=? ");
        // if quantity is all then bypass join on amount
        if (!PretupsI.ALL.equals(p_amount)) {
            selectQueryBuff.append("AND CTRF.quantity=? ");
        }
        selectQueryBuff.append("AND CTRF.SERVICE_TYPE=ST.SERVICE_TYPE ");
        selectQueryBuff.append("ORDER BY CTRF.service_type ");
		return selectQueryBuff.toString();
	}*/
	
	/*
	@Override
	public PreparedStatement loadLastXCustTransfersQry(Connection p_con,String receiverMsisdn, String p_user_id, int p_noLastTxn, ArrayList transfersList) throws SQLException {
		final String methodName = className + "#loadLastXCustTransfers";
		PreparedStatement pstmt = null;
		//local_index_missing
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT transfer_id, transfer_date_time, net_payable_amount, receiver_msisdn, created_on, service, name, ");
		strBuff.append(" type, statusname FROM ( SELECT  CS.transfer_status,CS.transfer_id, CS.transfer_date_time, CS.transfer_value net_payable_amount, CS.receiver_msisdn,");
        strBuff.append(" CS.transfer_date_time created_on, CS.service_type service, ST.name, 'C2S' AS type, KV.value statusname ");
        strBuff.append(" FROM C2S_TRANSFERS CS LEFT JOIN key_values KV ON (CS.transfer_status=KV.key AND KV.type=?) ,SERVICE_TYPE ST ");
        strBuff.append(" WHERE CS.ACTIVE_USER_ID=? AND CS.service_type=ST.service_type ");
        if (!BTSLUtil.isNullString(receiverMsisdn)) {
            strBuff.append(" AND CS.receiver_msisdn=? ");
        }
        strBuff.append(" ORDER BY created_on desc) A WHERE  limit ? ");
        final String sqlSelect = strBuff.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
 
            transfersList = new ArrayList();
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            ++i;
            pstmt.setString(i, PretupsI.KEY_VALUE_C2C_STATUS);
            ++i;
            pstmt.setString(i, p_user_id);
            if (!BTSLUtil.isNullString(receiverMsisdn)) {// added by rahuld
                ++i;
                pstmt.setString(i, receiverMsisdn);
            }
            ++i;
            pstmt.setInt(i, p_noLastTxn);

		return pstmt;

	}*/

	
    /*@Override
	public PreparedStatement getReversalTransactionsQry(String msisdn,Connection con, String senderMsisdn,String txID,Date date,String time) throws SQLException{

		final StringBuilder selectQueryBuff = new StringBuilder();
		 String methodName="getReversalTransactionsQry";
		 final Calendar cal = BTSLDateUtil.getInstance();
			java.util.Date dt = cal.getTime(); // Current Date
			try{
				dt = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(BTSLUtil.addDaysInUtilDate(dt, -1), PretupsI.DATE_FORMAT));
			}catch(Exception e){
				LOG.error(methodName, e);			
				LOG.errorTrace(methodName, e);
			}
        selectQueryBuff.append("Select transfer_id,sender_msisdn,sender_category, receiver_msisdn, subscriber_type,service_class_code,transfer_value ");
        selectQueryBuff.append("FROM c2s_transfers ");
        PreparedStatement pstmtSelect = null;

        if (BTSLUtil.isNullString(msisdn)) {
            selectQueryBuff
                .append("WHERE transfer_date >= ? and sender_msisdn = ? and  transfer_id like ? and (transfer_date_time between (?::timestamp without time zone  - interval '");
             selectQueryBuff.append(time);
             selectQueryBuff.append("' hour) and ?::timestamp without time zone ) ");
             selectQueryBuff.append("  and service_type =? and reversal_id is null and transfer_status=? ");
            final String selectQuery = selectQueryBuff.toString();
            pstmtSelect = con.prepareStatement(selectQuery);
            pstmtSelect.setDate(1, BTSLUtil.getSQLDateFromUtilDate(dt));
            pstmtSelect.setString(2, senderMsisdn);
            pstmtSelect.setString(3, "%" + txID);
            pstmtSelect.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(date));
            pstmtSelect.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(date));
            pstmtSelect.setString(6, PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
            pstmtSelect.setString(7, PretupsI.TXN_STATUS_SUCCESS);
        } else {
            selectQueryBuff
                .append("WHERE transfer_date >= ? and sender_msisdn = ? and  receiver_msisdn= ? and (transfer_date_time between (?::timestamp without time zone - interval '");
            selectQueryBuff.append(time);
            selectQueryBuff.append("' hour) and ?::timestamp without time zone) ");
            selectQueryBuff.append( "and service_type=? and reversal_id is null and transfer_status=?");
            final String selectQuery = selectQueryBuff.toString();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect = con.prepareStatement(selectQuery);
            pstmtSelect.setDate(1, BTSLUtil.getSQLDateFromUtilDate(dt));
            pstmtSelect.setString(2, senderMsisdn);
            pstmtSelect.setString(3, msisdn);
            pstmtSelect.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(date));
            pstmtSelect.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(date));
            pstmtSelect.setString(6, PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
            pstmtSelect.setString(7, PretupsI.TXN_STATUS_SUCCESS);
        }
        
        return pstmtSelect;
	
		
	}*/
    
    @Override
	public PreparedStatement loadOldTxnIDForReversalQry(Connection con,C2STransferVO c2sTransferVO, RequestVO requestVO, String serviceType,Date date,String time) throws SQLException, ParseException{
    	 final StringBuilder selectQueryBuff = new StringBuilder();
    	 //local_index_implemented
		 String methodName="loadOldTxnIDForReversalQry";
         selectQueryBuff.append("SELECT ct.transfer_id, ct.sender_msisdn, ct.receiver_msisdn, ct.transfer_value, ct.error_code, ct.request_gateway_code,ct.quantity, ");
         selectQueryBuff.append("ct.transfer_status, ct.sender_transfer_value,ct.interface_id,ct.interface_type,ct.product_code,ct.request_gateway_type,ct.service_type, ");
         selectQueryBuff.append("ct.receiver_access_fee, ct.receiver_validity, ct.receiver_transfer_value, ct.receiver_grace_period, ");
         selectQueryBuff
             .append("ct.receiver_network_code,ct.transfer_date,ct.transfer_date_time ,ct.network_code,ct.created_on,ct.serial_number,ct.pin_sent_to_msisdn,ct.sender_id, ");
         selectQueryBuff
             .append("ct.sender_post_balance ,ct.receiver_post_balance ,ct.debit_status supdate,ct.credit_status rupdate,ct.card_group_set_id,ct.version,ct.card_group_id,ct.transfer_profile_id,ct.commission_profile_id,differential_applicable,differential_given ");
         selectQueryBuff
             .append(",ct.receiver_tax1_type,ct.receiver_tax1_rate,ct.receiver_tax1_value,ct.receiver_tax2_type,ct.receiver_tax2_rate,ct.receiver_tax2_value,ct.receiver_bonus_value,ct.receiver_grace_period,ct.receiver_bonus_validity,ct.receiver_valperiod_type,ct.cell_id,ct.switch_id ");
         selectQueryBuff
             .append(",ct.card_group_code,ct.REVERSAL_ID,ct.SUB_SERVICE ,ct.RECEIVER_ACCESS_FEE,ct.SERVICE_CLASS_ID,ct.SERVICE_CLASS_CODE ,ct.SENDER_PREFIX_ID ,INFO1,INFO2,INFO3,ct.otf_applicable,ct.subs_sid ");
         selectQueryBuff.append("FROM c2s_transfers ct ");
         selectQueryBuff.append("WHERE ct.transfer_date=? AND ct.transfer_id=? ");

         if (PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL.equals(requestVO.getServiceType())) {
             selectQueryBuff.append(" and (transfer_date_time between (? ::timestamp without time zone - interval '");
             selectQueryBuff.append(time);
             selectQueryBuff.append("' hour) and ? ::timestamp without time zone ) ");
         } else {
             selectQueryBuff.append(" and service_type= ? ");
         }

         final String selectQuery = selectQueryBuff.toString();

         if (LOG.isDebugEnabled()) {
             LOG.debug(methodName, "select query:" + selectQuery);
         }

         PreparedStatement pstmtSelect =null;
         pstmtSelect = con.prepareStatement(selectQuery);
         int i = 1;
         pstmtSelect.setDate(i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromTransactionId(c2sTransferVO.getOldTxnId())));
         i++;
         pstmtSelect.setString(i, c2sTransferVO.getOldTxnId());
         i++;
         if (PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL.equals(requestVO.getServiceType())) {
             pstmtSelect.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(date));
             i++;
             pstmtSelect.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(date));
             i++;
         } else {
             pstmtSelect.setString(i, serviceType);
             i++;
         }
    	return pstmtSelect;
    }
	
	@Override
	public String insertTPSDetailsQry(Map<Date,Integer> pTpsMap) {
		
		
		StringBuilder strBuff = new StringBuilder("INSERT INTO TPS_DETAILS(TPS_DATE_TIME , INSTANCE_CODE , TPS ,TPS_DATE) VALUES ");
		int sizeOfEntrySet= pTpsMap.entrySet().size();
		int counter=1;
			for(Map.Entry<Date,Integer> tpsPair : pTpsMap.entrySet())
			{
				if(sizeOfEntrySet>=1 && counter==1)
					strBuff.append("  (?,?,?,?) ");
				else if(sizeOfEntrySet>1 && (sizeOfEntrySet-counter)>0)
					strBuff.append("  (?,?,?,?), ");
				else if(sizeOfEntrySet>1 && (sizeOfEntrySet-counter)==0)
					strBuff.append("  (?,?,?,?) ");
				counter++;
			}
			if (LOG.isDebugEnabled()) {
	             LOG.debug("insertTPSDetailsQry", "select query:" + strBuff);
	         }
		
			return strBuff.toString();
	}
}

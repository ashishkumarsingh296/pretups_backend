package com.btsl.pretups.p2p.query.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.util.BTSLUtil;

/**
 * SubscriberTransferPostgresQry
 */
public class SubscriberTransferPostgresQry implements SubscriberTransferQry{

	@Override
	public PreparedStatement loadSubscriberDetails(Connection pCon,
			TransferVO psubscriberVO) throws SQLException, ParseException {


		
		PreparedStatement pstmtSelect;
	    int i = 0;
		
		StringBuilder strBuff = new StringBuilder("SELECT STRS.transfer_id, STRS.transfer_date_time,");
        strBuff.append(" coalesce(KV2.value,STRS.error_code) error_code, STRS.reference_id, ST.name name1, STRS.quantity,");
        strBuff.append(" STRS.sender_msisdn, STRS.receiver_msisdn, STRS.transfer_value,");
        strBuff.append(" coalesce(KV.value,STRS.transfer_status) transfer_status, ");
        strBuff.append(" NET.network_name, MSG.gateway_name,");
        strBuff.append(" PRD.product_name");
        
        strBuff.append(" FROM subscriber_transfers STRS left join products PRD on (PRD.product_code=STRS.product_code )");
        strBuff.append(" left join message_gateway MSG on (STRS.request_gateway_code=MSG.gateway_code)");
        strBuff.append(" left join  key_values KV on (STRS.transfer_status = KV.key AND KV.type = ?)");
        strBuff.append(" left join  key_values KV2 on(STRS.error_code = KV2.key AND KV2.type = ?),");
        strBuff.append(" networks NET,service_type ST WHERE");
        strBuff.append(" STRS.network_code=NET.network_code  AND STRS.sender_msisdn=? AND STRS.service_type=ST.service_type");

         if (((psubscriberVO.getToDate()).trim()).length() == 0) {
             psubscriberVO.setToDate(psubscriberVO.getFromDate());
         }

         if ((psubscriberVO.getFromDate().length() > 0) && (psubscriberVO.getTransferID().length() > 0)) {
             i = 0;
             // both date and transfer id is enetered
             strBuff.append(" AND (transfer_date >=? AND transfer_date<=?)");
             strBuff.append(" AND UPPER(transfer_id) =UPPER(?)");
             strBuff.append(" ORDER BY STRS.transfer_date_time DESC");
             pstmtSelect = pCon.prepareStatement(strBuff.toString());

           
             pstmtSelect.setString(++i, PretupsI.P2P_STATUS_KEY_VALUS);
             pstmtSelect.setString(++i, PretupsI.P2P_ERRCODE_VALUS);
             pstmtSelect.setString(++i, ((SenderVO) psubscriberVO.getSenderVO()).getMsisdn());
             pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(psubscriberVO.getFromDate())));
             pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(psubscriberVO.getToDate())));
             pstmtSelect.setString(++i, psubscriberVO.getTransferID());

         } else if (psubscriberVO.getTransferID().length() > 0 && (psubscriberVO.getFromDate().trim()).length() == 0) {
             i = 0;
             strBuff.append(" AND UPPER(transfer_id)=UPPER(?)"); // only
             // transfer
             // id is
             // entered
             strBuff.append(" ORDER BY STRS.transfer_date_time DESC ");
             pstmtSelect = pCon.prepareStatement(strBuff.toString());
             
             pstmtSelect.setString(++i, PretupsI.P2P_STATUS_KEY_VALUS);
             pstmtSelect.setString(++i, PretupsI.P2P_ERRCODE_VALUS);
             pstmtSelect.setString(++i, ((SenderVO) psubscriberVO.getSenderVO()).getMsisdn());
             pstmtSelect.setString(++i, psubscriberVO.getTransferID());

         } else if ((psubscriberVO.getTransferID().trim()).length() == 0 && (psubscriberVO.getFromDate().trim()).length() == 0) {
             i = 0;
             strBuff.append(" ORDER BY STRS.transfer_date_time DESC ");
             pstmtSelect = pCon.prepareStatement(strBuff.toString());
            
             pstmtSelect.setString(++i, PretupsI.P2P_STATUS_KEY_VALUS);
             pstmtSelect.setString(++i, PretupsI.P2P_ERRCODE_VALUS);
             pstmtSelect.setString(++i, ((SenderVO) psubscriberVO.getSenderVO()).getMsisdn());

         } else {
             // only todate is entered
             i = 0;
             strBuff.append(" AND transfer_date >=? AND transfer_date<=?");
             strBuff.append(" ORDER BY STRS.transfer_date_time DESC ");
             pstmtSelect = pCon.prepareStatement(strBuff.toString());
             
             pstmtSelect.setString(++i, PretupsI.P2P_STATUS_KEY_VALUS);
             pstmtSelect.setString(++i, PretupsI.P2P_ERRCODE_VALUS);
             pstmtSelect.setString(++i, ((SenderVO) psubscriberVO.getSenderVO()).getMsisdn());
             pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(psubscriberVO.getFromDate())));
             pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(psubscriberVO.getToDate())));

         }
         
         LogFactory.printLog("ReceiverTransferOracleQry:loadReceiverDetails", strBuff.toString(), LOG);
         
         return pstmtSelect;
	}
	
	
	@Override
	public PreparedStatement loadSubscriberItemList(Connection pCon,
			TransferVO transferVO) throws SQLException, ParseException {
		PreparedStatement pstmtSelect;

		StringBuilder strBuff = new StringBuilder("SELECT TRS.transfer_id, TRS.entry_date, TRS.entry_date_time,");
         strBuff.append(" TRS.entry_type, TRS.first_call,");
         strBuff.append(" TRS.interface_id, TRS.interface_reference_id, TRS.interface_response_code,");
         strBuff.append(" TRS.interface_type, TRS.msisdn,");
         strBuff.append(" TRS.msisdn_new_expiry, TRS.msisdn_previous_expiry, TRS.post_balance,");
         strBuff.append(" TRS.prefix_id, TRS.previous_balance,");
         strBuff.append(" TRS.request_value, TRS.sno,");
         strBuff.append(" TRS.service_class_code,TRS.account_status, ");
         strBuff.append(" LOOK1.lookup_name subscriber_type,");
         strBuff.append(" TRS.transfer_date, TRS.transfer_date_time AS transfer_date_time_item,");
         strBuff.append(" TRS.transfer_id, TRS.interface_reference_id, ");
         strBuff.append(" coalesce(KV.value,TRS.transfer_status) transfer_status,");
         strBuff.append(" TRS.transfer_type, coalesce(KV2.value,STRS.error_code) error_code, ");
         strBuff.append(" TRS.transfer_value As transfer_value_item, TRS.update_status, TRS.user_type,");
         strBuff.append(" TRS.validation_status, INTF.interface_description,TRS.msisdn_new_expiry, TRS.msisdn_previous_expiry,");
         strBuff.append(" STRS.card_group_id,");
         strBuff.append(" CGS.card_group_set_name,");
         strBuff.append(" LOOK.lookup_name payment_method_type,");
         strBuff.append(" STRS.receiver_access_fee,");
         strBuff.append(" STRS.receiver_bonus_validity, STRS.receiver_validity, STRS.receiver_grace_period,");
         strBuff.append(" STRS.receiver_bonus_value, STRS.receiver_msisdn, STRS.receiver_tax1_rate,");
         strBuff.append(" STRS.receiver_tax1_type, STRS.receiver_tax1_value, STRS.receiver_tax2_rate,");
         strBuff.append(" STRS.receiver_tax2_type, STRS.receiver_tax2_value, STRS.receiver_transfer_value,");
         strBuff.append(" STRS.reconciliation_by, STRS.reconciliation_date, STRS.sender_access_fee,");
         strBuff.append(" STRS.sender_msisdn, STRS.sender_tax1_rate, STRS.sender_tax1_type, STRS.sender_tax1_value,");
         strBuff.append(" STRS.sender_tax2_rate, STRS.sender_tax2_type, STRS.sender_tax2_value,");
         strBuff.append(" STRS.sender_transfer_value, coalesce(TRANST.value,STRS.transfer_status) fitrans_status, ");
         strBuff.append(" ST.name service_type, STRS.transfer_date_time, STRS.transfer_value, STRS.transfer_category,TRS.reference_id ");
         strBuff.append(",STRS.cell_id,STRS.switch_id,TRS.service_provider_name spname ");
         strBuff.append(" FROM subscriber_transfers STRS left join lookups LOOK on(LOOK.lookup_code = STRS.payment_method_type AND LOOK.lookup_type = ?)");
         strBuff.append(" left join card_group_set CGS on  CGS.card_group_set_id = STRS.card_group_set_id");
         strBuff.append(" left join service_type ST on ST.service_type = STRS.service_type");
         strBuff.append(" left join key_values KV2 on (STRS.error_code = KV2.key AND KV2.type = ?)");
         strBuff.append(" left join key_values TRANST on (STRS.transfer_status = TRANST.key AND TRANST.type = ?),");
         strBuff.append(" transfer_items TRS left join interfaces INTF on INTF.interface_id = TRS.interface_id");
         strBuff.append(" left join lookups LOOK1 on (LOOK1.lookup_code = TRS.subscriber_type AND LOOK1.lookup_type = ?)");
         strBuff.append(" left join key_values KV on (KV.key = TRS.transfer_status AND KV.type = ?)");
         strBuff.append(" WHERE TRS.transfer_id = ?");
         strBuff.append("AND STRS.transfer_id = TRS.transfer_id");
         strBuff.append(" ORDER BY TRS.transfer_id");
         
         LogFactory.printLog("ReceiverTransferOracleQry:loadReceiverDetails", strBuff.toString(), LOG);
        
         pstmtSelect = pCon.prepareStatement(strBuff.toString());
         int i = 0;
        
         pstmtSelect.setString(++i, PretupsI.PAYMENT_INSTRUMENT_TYPE);
         pstmtSelect.setString(++i, PretupsI.P2P_ERRCODE_VALUS);
         pstmtSelect.setString(++i, PretupsI.P2P_STATUS_KEY_VALUS);

         pstmtSelect.setString(++i, PretupsI.SUBSRICBER_TYPE);
         pstmtSelect.setString(++i, PretupsI.P2P_STATUS_KEY_VALUS);
         pstmtSelect.setString(++i, transferVO.getTransferID());
		
		return pstmtSelect;
	}

	
}

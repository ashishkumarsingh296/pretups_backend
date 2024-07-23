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
 * ReceiverTransferOracleQry
 */
public class ReceiverTransferOracleQry implements ReceiverTransferQry {

	@Override
	public PreparedStatement loadReceiverDetails(Connection pCon,
			TransferVO preceiverVO) throws SQLException, ParseException {
		
		 StringBuilder strBuff ;
	     PreparedStatement pstmtSelect;
	     int i = 0;
	     
		 strBuff = new StringBuilder("SELECT STRS.transfer_id, STRS.transfer_date_time,");
         strBuff.append(" nvl(KV2.value,STRS.error_code) error_code, STRS.reference_id, ST.name name1, STRS.quantity,");
         strBuff.append(" STRS.sender_msisdn, STRS.receiver_msisdn, STRS.transfer_value,");
         strBuff.append(" nvl(KV.value,STRS.transfer_status) transfer_status, ");
         strBuff.append(" NET.network_name, MSG.gateway_name,");
         strBuff.append(" PRD.product_name,PRD.product_code");
         strBuff.append(" FROM subscriber_transfers STRS, networks NET, message_gateway MSG, products PRD, key_values KV, key_values KV2,service_type ST ");
         strBuff.append(" WHERE STRS.network_code=NET.network_code");
         strBuff.append(" AND PRD.product_code(+)=STRS.product_code");
         strBuff.append(" AND STRS.request_gateway_code=MSG.gateway_code(+)");
         strBuff.append(" AND STRS.receiver_msisdn=?");
         strBuff.append(" AND STRS.transfer_status = KV.key(+)");
         strBuff.append(" AND KV.type(+) = ? AND STRS.service_type=ST.service_type AND STRS.error_code = KV2.key(+) AND KV2.type(+) = ? ");

         if (((preceiverVO.getToDate()).trim()).length() == 0) {
             preceiverVO.setToDate(preceiverVO.getFromDate());
         }

         if ((preceiverVO.getFromDate().length() > 0) && (preceiverVO.getTransferID().length() > 0)) {
             i = 0;
             // both date and transfer id is enetered
             strBuff.append(" AND (transfer_date >=? AND transfer_date<=?)");
             strBuff.append(" AND UPPER(transfer_id) =UPPER(?)");
             strBuff.append(" ORDER BY STRS.transfer_date_time DESC");
             pstmtSelect = pCon.prepareStatement(strBuff.toString());
             pstmtSelect.setString(++i, ((SenderVO) preceiverVO.getSenderVO()).getMsisdn());
             pstmtSelect.setString(++i, PretupsI.P2P_STATUS_KEY_VALUS);
             pstmtSelect.setString(++i, PretupsI.P2P_ERRCODE_VALUS);
             pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(preceiverVO.getFromDate())));
             pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(preceiverVO.getToDate())));
             pstmtSelect.setString(++i, preceiverVO.getTransferID());

         } else if (preceiverVO.getTransferID().length() > 0 && (preceiverVO.getFromDate().trim()).length() == 0) {
             i = 0;
             strBuff.append(" AND UPPER(transfer_id)=UPPER(?)"); // only
             // transfer
             // id is
             // entered
             strBuff.append(" ORDER BY STRS.transfer_date_time DESC");
             pstmtSelect = pCon.prepareStatement(strBuff.toString());
             pstmtSelect.setString(++i, ((SenderVO) preceiverVO.getSenderVO()).getMsisdn());
             pstmtSelect.setString(++i, PretupsI.P2P_STATUS_KEY_VALUS);
             pstmtSelect.setString(++i, PretupsI.P2P_ERRCODE_VALUS);
             pstmtSelect.setString(++i, preceiverVO.getTransferID());

         } else if ((preceiverVO.getTransferID().trim()).length() == 0 && (preceiverVO.getFromDate().trim()).length() == 0) {
             i = 0;
             strBuff.append(" ORDER BY STRS.transfer_date_time DESC");
             pstmtSelect = pCon.prepareStatement(strBuff.toString());
             pstmtSelect.setString(++i, ((SenderVO) preceiverVO.getSenderVO()).getMsisdn());
             pstmtSelect.setString(++i, PretupsI.P2P_STATUS_KEY_VALUS);
             pstmtSelect.setString(++i, PretupsI.P2P_ERRCODE_VALUS);
         } else {
             i = 0;
             strBuff.append(" AND transfer_date >=? AND transfer_date<=?");
             strBuff.append(" ORDER BY STRS.transfer_date_time DESC");
             pstmtSelect = pCon.prepareStatement(strBuff.toString());
             pstmtSelect.setString(++i, ((SenderVO) preceiverVO.getSenderVO()).getMsisdn());
             pstmtSelect.setString(++i, PretupsI.P2P_STATUS_KEY_VALUS);
             pstmtSelect.setString(++i, PretupsI.P2P_ERRCODE_VALUS);
             pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(preceiverVO.getFromDate())));
             pstmtSelect.setDate(++i, BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(preceiverVO.getToDate())));
         }
         LogFactory.printLog("ReceiverTransferOracleQry:loadReceiverDetails", strBuff.toString(), LOG);
         return pstmtSelect;
	}

}

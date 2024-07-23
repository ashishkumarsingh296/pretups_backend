package com.btsl.pretups.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;

public class TransferPostgresQry implements TransferQry{
	
	private String className = "TransferPostgresQry";
	
	@Override
	public PreparedStatement loadP2PReconciliationItemsList(Connection con, String transferID) throws SQLException {
		String methodName = className + "#loadP2PReconciliationItemsList";
		   final StringBuilder selectQueryBuff = new StringBuilder();
           selectQueryBuff.append("SELECT KV.value,transfer_id, msisdn, entry_date, request_value, previous_balance, ");
           selectQueryBuff.append("post_balance, user_type, transfer_type, entry_type, validation_status, ");
           selectQueryBuff.append("update_status, transfer_value, interface_type, interface_id, ");
           selectQueryBuff.append("interface_response_code, interface_reference_id, subscriber_type, ");
           selectQueryBuff.append("service_class_code, msisdn_previous_expiry, msisdn_new_expiry, transfer_status,");
           selectQueryBuff.append("transfer_date, transfer_date_time, entry_date_time, first_call, sno, prefix_id, ");
           selectQueryBuff.append("protocol_status, account_status, service_class_id, reference_id ");
           selectQueryBuff.append("FROM key_values KV right outer join transfer_items on KV.key=transfer_status AND KV.type=? ");
           selectQueryBuff.append("WHERE transfer_id=? ");
           selectQueryBuff.append("ORDER BY sno ");
           PreparedStatement pstmtSelect = con.prepareStatement(selectQueryBuff.toString());
           int i = 1;          
           pstmtSelect.setString(i, PretupsI.KEY_VALUE_P2P_STATUS);
           i++;
           pstmtSelect.setString(i, transferID);
           LogFactory.printLog(methodName, QUERY+selectQueryBuff.toString(), LOG);
           return pstmtSelect;
	}

	@Override
	public String updateReconcilationStatusQry() {
		String methodName = className + "#updateReconcilationStatus";
		final StringBuilder updateQuery = new StringBuilder();
        updateQuery.append("UPDATE subscriber_transfers SET transfer_status=?, reconciliation_by=?, reconciliation_date=?, ");
        updateQuery.append("reconciliation_flag='Y', modified_by=?, modified_on=? ");
        updateQuery.append(", error_code = COALESCE(error_code,'" + PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS + "')");
        // By sandeep ID REC001
        // to perform the check "is Already modify"
        updateQuery.append("WHERE transfer_id=? AND (transfer_status=? OR transfer_status=?)");
        LogFactory.printLog(methodName, QUERY+updateQuery.toString(), LOG);
        return updateQuery.toString();

	}
}

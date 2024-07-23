package com.btsl.pretups.iatrestrictedsubs.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;

public class IATRestrictedSubscriberOracleQry implements IATRestrictedSubscriberQry{
	private String className = "IATRestrictedSubscriberOracleQry";
	
	@Override
	public PreparedStatement loadScheduleBatchDetailsListQry(Connection p_con,
			String p_batch_id, String p_statusUsed, String p_status)
			throws SQLException {
		String methodname = className+"#loadScheduleBatchDetailsListQry";
		 StringBuffer selectSQL = new StringBuffer(" SELECT SBD.msisdn,SBD.amount,SBD.subscriber_id,SBD.status, L.lookup_name status_desc ,SBD.transfer_status,");
         selectSQL.append(" KV.value  transfer_status_desc, SBD.created_on, SBD.processed_on, SBM.service_type stype,");
         selectSQL.append(" RM.employee_name, RM.employee_code,  RM.max_txn_amount, RM.min_txn_amount, RM.monthly_limit,");
         selectSQL.append(" RM.total_txn_amount, RM.total_txn_count,SBD.modified_on,SBD.sub_service,rm.language,rm.country,SBD.donor_msisdn ");
         selectSQL.append(" FROM scheduled_batch_detail SBD, scheduled_batch_master SBM, restricted_msisdns RM, lookups L, key_values KV ");
         selectSQL.append(" WHERE SBM.batch_id = ? AND SBM.batch_id=SBD.batch_id ");
         selectSQL.append(" AND SBD.subscriber_id = RM.subscriber_id");
         selectSQL.append(" AND SBD.status = L.lookup_code");
         selectSQL.append(" AND L.lookup_type = ?");
         selectSQL.append(" AND SBD.transfer_status = KV.key(+)");
         selectSQL.append(" AND KV.type (+) = ? ");

         if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
             selectSQL.append("AND SBD.status IN (" + p_status + ")");
         } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
             selectSQL.append("AND SBD.status =? ");
         } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
             selectSQL.append("AND SBD.status <> ? ");
         } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
             selectSQL.append("AND SBD.status NOT IN (" + p_status + ")");
         }
         selectSQL.append("ORDER BY RM.employee_name ");

       LogFactory.printLog(methodname, selectSQL.toString(), LOG);

         PreparedStatement pstmtSelect = p_con.prepareStatement(selectSQL.toString());
         int i = 0;
         pstmtSelect.setString(++i, p_batch_id);
         pstmtSelect.setString(++i, PretupsI.SCHEDULE_BATCH_STATUS_LOOKUP_TYPE);
         pstmtSelect.setString(++i, PretupsI.KEY_VALUE_C2C_STATUS);
         if (p_statusUsed.equals(PretupsI.STATUS_EQUAL) || p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
             pstmtSelect.setString(++i, p_status);
         }
		return pstmtSelect;
	}

}

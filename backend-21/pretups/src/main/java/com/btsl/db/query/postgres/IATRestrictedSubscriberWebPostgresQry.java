package com.btsl.db.query.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.web.pretups.iatrestrictedsubs.businesslogic.IATRestrictedSubscriberWebQry;

public class IATRestrictedSubscriberWebPostgresQry implements IATRestrictedSubscriberWebQry{
	
	private Log _log = LogFactory.getLog(this.getClass().getName());
	private String className = "IATRestrictedSubscriberWebOracleQry";
	@Override
	public PreparedStatement loadBatchDetailVOListQry(Connection p_con,
			 String p_batchID, String p_statusUsed, String p_status)
			throws SQLException {
         
		PreparedStatement pstmtSelect = null;
		final String methodName = className+"#loadBatchDetailVOList";
		StringBuilder strBuff = new StringBuilder();
		strBuff.append("SELECT SBD.batch_id, SBD.subscriber_id, SBD.msisdn, SBD.amount, SBD.processed_on, SBD.status, ");
		strBuff.append(" SBD.transfer_id, SBD.transfer_status, SBD.created_on, SBD.created_by, SBD.modified_on, SBD.modified_by, ");
        strBuff.append(" SBD.sub_service, SBD.error_code, KV.value transfer_status_desc, U.user_name created_by_name, L.lookup_name status_desc,");
        strBuff.append(" SBM.network_code, SBM.scheduled_date,SBM.service_type, SBM.created_on, SBM.service_type stype,ST.description ");
        strBuff.append(", SBD.r_language, SBD.r_country, SBD.donor_msisdn, SBD.donor_name, SBD.d_language, SBD.d_country ");
        strBuff.append(" FROM scheduled_batch_detail SBD LEFT JOIN  key_values KV ON (SBD.transfer_status = KV.key AND KV.type = ?),scheduled_batch_master SBM,lookups L, ");
        strBuff.append(" users U,service_type ST ");
        strBuff.append(" WHERE SBM.batch_id=SBD.batch_id AND U.user_id=SBM.initiated_by AND ST.service_type= SBM.service_type ");
        strBuff.append(" AND L.lookup_type = ?  AND SBD.status = L.lookup_code ");
        strBuff.append(" AND SBM.batch_id=? ");

        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
            strBuff.append("AND SBD.status IN (" + p_status + ")");
        } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
            strBuff.append("AND SBD.status =? ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
            strBuff.append("AND SBD.status <> ? ");
        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
            strBuff.append("AND SBD.status NOT IN (" + p_status + ")");
        }
        strBuff.append(" ORDER BY scheduled_date ASC");

        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + strBuff);
        }
        pstmtSelect = p_con.prepareStatement(strBuff.toString());
        int i = 1;
        pstmtSelect.setString(i++, PretupsI.KEY_VALUE_C2C_STATUS);
        pstmtSelect.setString(i++, PretupsI.SCHEDULE_BATCH_STATUS_LOOKUP_TYPE);
        pstmtSelect.setString(i++, p_batchID);
        if (p_statusUsed.equals(PretupsI.STATUS_EQUAL) || p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
            pstmtSelect.setString(i++, p_status);
        }
        return pstmtSelect;
	}

}

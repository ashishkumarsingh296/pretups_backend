package com.btsl.pretups.restrictedsubs.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;

public class ScheduleBatchDetailOracleQry implements ScheduleBatchDetailQry{

	private String className = "ScheduleBatchDetailOracleQry";
	@Override
	public PreparedStatement loadScheduleDetailVOListQry(Connection p_con,
			String p_ownerID, String p_parentID, String p_msisdn,
			String p_statusUsed, String p_status, boolean p_isStaffUser,
			String p_userId) throws SQLException {
		String methodName = className+"loadScheduleDetailVOList";
		 StringBuilder strBuff = new StringBuilder(" SELECT SBD.msisdn, SBD.status, L.lookup_name status_desc,SBD.transfer_status,");
	        strBuff.append(" KV.value transfer_status_desc,SBD.processed_on, SBD.transfer_id, SBD.amount, SBD.batch_id, ");
	        strBuff.append(" SBD.subscriber_id, SBM.network_code, SBM.scheduled_date,SBM.service_type, SBM.created_on, SBM.service_type stype, SBM.FREQUENCY,SBM.ITERATION,SBM.EXECUTED_ITERATIONS,");
	        strBuff.append(" SBM.initiated_by, U.user_name created_by_name, ST.description, SBD.sub_service, SBM.batch_type, U1.user_name active_user_name ");
	        strBuff.append(" FROM scheduled_batch_detail SBD,scheduled_batch_master SBM,lookups L, key_values KV, ");
	        strBuff.append(" users U,service_type ST, Users U1 ");
	        strBuff.append(" WHERE SBM.batch_id=SBD.batch_id AND U.user_id=SBM.initiated_by AND ST.service_type= SBM.service_type");
	        strBuff.append(" AND SBD.msisdn = ? AND SBM.owner_id = ? AND SBD.status = L.lookup_code");
	        strBuff.append(" AND L.lookup_type = ? AND SBD.transfer_status = KV.key(+) AND KV.type (+) = ? AND U1.user_id= SBM.active_user_id ");

	        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
	            strBuff.append("AND SBD.status IN (" + p_status + ")");
	        } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
	            strBuff.append("AND SBD.status =? ");
	        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
	            strBuff.append("AND SBD.status <> ? ");
	        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
	            strBuff.append("AND SBD.status NOT IN (" + p_status + ")");
	        }
	        strBuff.append(" AND SBM.parent_id=? ");
	        if (p_isStaffUser) {
	            strBuff.append(" AND SBM.active_user_Id='" + p_userId + "' ");
	        }
	        strBuff.append(" ORDER BY SBM.scheduled_date DESC, SBD.batch_id DESC ");
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "QUERY sqlSelect=" + strBuff);
	        }
	        PreparedStatement pstmtSelect = p_con.prepareStatement(strBuff.toString());
	        int i = 1;
            pstmtSelect.setString(i++, p_msisdn);
            pstmtSelect.setString(i++, p_ownerID);
            pstmtSelect.setString(i++, PretupsI.SCHEDULE_BATCH_STATUS_LOOKUP_TYPE);
            pstmtSelect.setString(i++, PretupsI.KEY_VALUE_C2C_STATUS);
            if (p_statusUsed.equals(PretupsI.STATUS_EQUAL) || p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
                pstmtSelect.setString(i++, p_status);
            }
            pstmtSelect.setString(i++, p_parentID);
		return pstmtSelect;
	}
	
	@Override
	public PreparedStatement loadScheduleDetailReportVOListQry(Connection p_con,
			String p_ownerID, String p_parentID, String p_msisdn,
			String p_statusUsed, String p_status, boolean p_isStaffUser,
			String p_userId, Date p_fromScheduleDate, Date p_toScheduleDate) throws SQLException {
		String methodName = className+"loadScheduleDetailVOList";
		 StringBuilder strBuff = new StringBuilder(" SELECT SBD.msisdn, SBD.status, L.lookup_name status_desc,SBD.transfer_status,");
	        strBuff.append(" KV.value transfer_status_desc,SBD.processed_on, SBD.transfer_id, SBD.amount, SBD.batch_id, ");
	        strBuff.append(" SBD.subscriber_id, SBM.network_code, SBM.scheduled_date,SBM.service_type, SBM.created_on, SBM.service_type stype, SBM.FREQUENCY,SBM.ITERATION,SBM.EXECUTED_ITERATIONS,");
	        strBuff.append(" SBM.initiated_by, U.user_name created_by_name, ST.description, SBD.sub_service, SBM.batch_type, U1.user_name active_user_name ");
	        strBuff.append(" FROM scheduled_batch_detail SBD,scheduled_batch_master SBM,lookups L, key_values KV, ");
	        strBuff.append(" users U,service_type ST, Users U1 ");
	        strBuff.append(" WHERE SBM.batch_id=SBD.batch_id AND U.user_id=SBM.initiated_by AND ST.service_type= SBM.service_type");
	        strBuff.append(" AND SBM.owner_id = ? AND SBD.status = L.lookup_code");
	        strBuff.append(" AND L.lookup_type = ? AND SBD.transfer_status = KV.key(+) AND KV.type (+) = ? AND U1.user_id= SBM.active_user_id ");
	        strBuff.append(" AND SBM.scheduled_date >= ? ");
	        strBuff.append(" AND SBM.scheduled_date <= ? ");

	        
	        if(!BTSLUtil.isNullString(p_msisdn)  && !p_msisdn.equals("null")) {
	        	strBuff.append(" AND SBD.msisdn = ? ");
	        }
	        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
	            strBuff.append("AND SBD.status IN (" + p_status + ")");
	        } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
	            strBuff.append("AND SBD.status =? ");
	        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
	            strBuff.append("AND SBD.status <> ? ");
	        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
	            strBuff.append("AND SBD.status NOT IN (" + p_status + ")");
	        }
	        strBuff.append(" AND SBM.parent_id=? ");
	        if (p_isStaffUser) {
	            strBuff.append(" AND SBM.active_user_Id='" + p_userId + "' ");
	        }
	        strBuff.append(" ORDER BY SBM.scheduled_date DESC, SBD.batch_id DESC ");
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "QUERY sqlSelect=" + strBuff);
	        }
	        PreparedStatement pstmtSelect = p_con.prepareStatement(strBuff.toString());
	        int i = 1;
           
            pstmtSelect.setString(i++, p_ownerID);
            pstmtSelect.setString(i++, PretupsI.SCHEDULE_BATCH_STATUS_LOOKUP_TYPE);
            pstmtSelect.setString(i++, PretupsI.KEY_VALUE_C2C_STATUS);
            
            pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_fromScheduleDate));
            pstmtSelect.setDate(i++, BTSLUtil.getSQLDateFromUtilDate(p_toScheduleDate));
            
            if(!BTSLUtil.isNullString(p_msisdn) && !p_msisdn.equals("null")) {
            	pstmtSelect.setString(i++, p_msisdn);
            }
            
            if (p_statusUsed.equals(PretupsI.STATUS_EQUAL) || p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
                pstmtSelect.setString(i++, p_status);
            }
            pstmtSelect.setString(i++, p_parentID);
            return pstmtSelect;
	}

}

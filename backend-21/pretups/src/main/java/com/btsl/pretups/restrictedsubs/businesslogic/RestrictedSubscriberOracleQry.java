package com.btsl.pretups.restrictedsubs.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;

public class RestrictedSubscriberOracleQry implements RestrictedSubscriberQry{
	
	private String className = "RestrictedSubscriberOracleQry";
	@Override
	public PreparedStatement loadScheduleBatchDetailsListQry(Connection p_con,String p_batch_id,String p_statusUsed, String p_status) throws SQLException {
		String mathodname = className+"#loadScheduleBatchDetailsListQry";
		StringBuilder selectSQL = new StringBuilder(" SELECT SBD.msisdn,SBD.amount,SBD.subscriber_id,SBD.status, L.lookup_name status_desc ,SBD.transfer_status,");
		selectSQL.append(" KV.value  transfer_status_desc, SBD.created_on, SBD.processed_on, SBM.service_type stype,");
		selectSQL.append(" RM.employee_name, RM.employee_code,  RM.max_txn_amount, RM.min_txn_amount, RM.monthly_limit,");
		selectSQL.append(" RM.total_txn_amount, RM.total_txn_count,SBD.modified_on,SBD.sub_service,rm.language,rm.country,SBD.donor_msisdn,SBD.executed_iterations ");
		selectSQL.append(" FROM scheduled_batch_detail SBD, scheduled_batch_master SBM, restricted_msisdns RM, lookups L, key_values KV ");
		selectSQL.append(" WHERE SBM.batch_id = ? AND SBM.batch_id=SBD.batch_id ");
		selectSQL.append(" AND SBD.subscriber_id = RM.subscriber_id ");
		selectSQL.append(" AND SBD.status = L.lookup_code");
		selectSQL.append(" AND L.lookup_type = ?");
		selectSQL.append(" AND SBD.transfer_status = KV.key(+)");
		selectSQL.append(" AND KV.type (+) = ? ");
		String []args = p_status.split(",");
        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
           selectSQL.append("AND SBD.status IN");
           BTSLUtil.pstmtForInQuery(args,selectSQL);
        }else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
			selectSQL.append("AND SBD.status =? ");
		} else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
			selectSQL.append("AND SBD.status <> ? ");
		} else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
			selectSQL.append("AND SBD.status NOT IN");
			BTSLUtil.pstmtForInQuery(args,selectSQL);
		}
		selectSQL.append("ORDER BY RM.employee_name ");
		
        if (_log.isDebugEnabled()) {
            _log.debug(mathodname, "QUERY SelectQuery:" + selectSQL);
        }

        PreparedStatement  pstmtSelect = p_con.prepareStatement(selectSQL.toString());
        int i = 0;
        pstmtSelect.setString(++i, p_batch_id);
        pstmtSelect.setString(++i, PretupsI.SCHEDULE_BATCH_STATUS_LOOKUP_TYPE);
        pstmtSelect.setString(++i, PretupsI.KEY_VALUE_C2C_STATUS);
        if (p_statusUsed.equals(PretupsI.STATUS_EQUAL) || p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
            pstmtSelect.setString(++i, p_status);
        }
        else if (p_statusUsed.equals(PretupsI.STATUS_IN) || p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
        	for(int j=0;j<args.length;j++)
        	{
        		 pstmtSelect.setString(++i, args[j]);
        	}
        }
		return pstmtSelect;
	}
	
	public PreparedStatement loadBatchDetailVOListQry(Connection p_con,
			String p_batchID, String p_statusUsed, String p_status) throws SQLException {
		String mathodName = className+"#loadBatchDetailVOListQry";
		 StringBuilder strBuff = new StringBuilder("SELECT SBD.batch_id, SBD.subscriber_id, SBD.msisdn, SBD.amount, SBD.processed_on, SBD.status,");
	        strBuff.append(" SBD.transfer_id, SBD.transfer_status, SBD.created_on, SBD.created_by, SBD.modified_on, SBD.modified_by, ");
	        strBuff.append(" SBD.sub_service, SBD.error_code, KV.value transfer_status_desc, U.user_name created_by_name, L.lookup_name status_desc,");
	        	strBuff.append(" RM.employee_name, RM.employee_code,  RM.max_txn_amount, RM.min_txn_amount, RM.monthly_limit,RM.total_txn_amount, RM.total_txn_count,");	        	
	        strBuff.append(" SBM.network_code, SBM.scheduled_date,SBM.service_type, SBM.created_on, SBM.service_type stype,ST.description ");
	        strBuff.append(", SBD.r_language, SBD.r_country, SBD.donor_msisdn, SBD.donor_name, SBD.d_language, SBD.d_country ");
	        strBuff.append(" FROM scheduled_batch_detail SBD,scheduled_batch_master SBM,lookups L, key_values KV,restricted_msisdns RM, ");
	        strBuff.append(" users U,service_type ST ");
	        strBuff.append(" WHERE SBM.batch_id=SBD.batch_id AND U.user_id=SBM.initiated_by AND ST.service_type= SBM.service_type ");
	        strBuff.append(" AND L.lookup_type = ? AND SBD.transfer_status = KV.key(+) AND KV.type (+) = ? AND SBD.status = L.lookup_code ");
	        strBuff.append(" AND SBD.subscriber_id = RM.subscriber_id (+)");
	        strBuff.append(" AND SBM.batch_id=? ");
	        String []args = p_status.split(",");
	        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
	           strBuff.append("AND SBD.status IN");
	           BTSLUtil.pstmtForInQuery(args,strBuff);
	        }
	        else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
	            strBuff.append("AND SBD.status =? ");
	        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
	            strBuff.append("AND SBD.status <> ? ");
	        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
	            strBuff.append("AND SBD.status NOT IN");
	            BTSLUtil.pstmtForInQuery(args,strBuff);
	        }
	        strBuff.append(" ORDER BY scheduled_date ASC");

	        if (_log.isDebugEnabled()) {
	            _log.debug(mathodName, "QUERY sqlSelect=" + strBuff);
	        }
	        PreparedStatement pstmtSelect = p_con.prepareStatement(strBuff.toString());
            int i = 1;
            pstmtSelect.setString(i++, PretupsI.SCHEDULE_BATCH_STATUS_LOOKUP_TYPE);
            pstmtSelect.setString(i++, PretupsI.KEY_VALUE_C2C_STATUS);
            pstmtSelect.setString(i++, p_batchID);
            if (p_statusUsed.equals(PretupsI.STATUS_EQUAL) || p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
                pstmtSelect.setString(i++, p_status);
            }
            else if ( p_statusUsed.equals(PretupsI.STATUS_IN) || p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
            	for(int j=0;j<args.length;j++)
            	{
            		 pstmtSelect.setString(i++, args[j]);
            	}
            }
		return pstmtSelect;
	}
	
	@Override
	public PreparedStatement loadBatchDetailVOListQry(Connection p_con,
			String p_batchID, String p_statusUsed, String p_status, String batchType) throws SQLException {
		String mathodName = className+"#loadBatchDetailVOListQry";
		 StringBuilder strBuff = new StringBuilder("SELECT SBD.batch_id, SBD.subscriber_id, SBD.msisdn, SBD.amount, SBD.processed_on, SBD.status,");
	        strBuff.append(" SBD.transfer_id, SBD.transfer_status, SBD.created_on, SBD.created_by, SBD.modified_on, SBD.modified_by, ");
	        strBuff.append(" SBD.sub_service, SBD.error_code, KV.value transfer_status_desc, U.user_name created_by_name, L.lookup_name status_desc,");
	        if(PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(batchType)) {
	        	strBuff.append(" RM.employee_name, RM.employee_code,  RM.max_txn_amount, RM.min_txn_amount, RM.monthly_limit,RM.total_txn_amount, RM.total_txn_count,");	        	
	        }
	        strBuff.append(" SBM.network_code, SBM.scheduled_date,SBM.service_type, SBM.created_on, SBM.service_type stype,ST.description ");
	        strBuff.append(", SBD.r_language, SBD.r_country, SBD.donor_msisdn, SBD.donor_name, SBD.d_language, SBD.d_country ");
	        strBuff.append(" FROM scheduled_batch_detail SBD,scheduled_batch_master SBM,lookups L, key_values KV, ");
	        if(PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(batchType)) {
	        	strBuff.append(" restricted_msisdns RM, ");
	        }
	        strBuff.append(" users U,service_type ST ");
	        strBuff.append(" WHERE SBM.batch_id=SBD.batch_id AND U.user_id=SBM.initiated_by AND ST.service_type= SBM.service_type ");
	        strBuff.append(" AND L.lookup_type = ? AND SBD.transfer_status = KV.key(+) AND KV.type (+) = ? AND SBD.status = L.lookup_code ");
	        if(PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(batchType)) {
	        	strBuff.append(" AND SBD.subscriber_id = RM.subscriber_id");	        	
	        }
	        strBuff.append(" AND SBM.batch_id=? ");
	        
	        String []args = p_status.split(",");
	        if (p_statusUsed.equals(PretupsI.STATUS_IN)) {
	           strBuff.append("AND SBD.status IN");
	           BTSLUtil.pstmtForInQuery(args,strBuff);
	           
	        } else if (p_statusUsed.equals(PretupsI.STATUS_EQUAL)) {
	            strBuff.append("AND SBD.status =? ");
	        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
	            strBuff.append("AND SBD.status <> ? ");
	        } else if (p_statusUsed.equals(PretupsI.STATUS_NOTIN)) {
	            //strBuff.append("AND SBD.status NOT IN (?)");
	            strBuff.append("AND SBD.status NOT IN");
	            BTSLUtil.pstmtForInQuery(args,strBuff);
	        }
	        strBuff.append(" ORDER BY scheduled_date ASC");

	        if (_log.isDebugEnabled()) {
	            _log.debug(mathodName, "QUERY sqlSelect=" + strBuff);
	        }
	        PreparedStatement pstmtSelect = p_con.prepareStatement(strBuff.toString());
            int i = 1;
            pstmtSelect.setString(i++, PretupsI.SCHEDULE_BATCH_STATUS_LOOKUP_TYPE);
            pstmtSelect.setString(i++, PretupsI.KEY_VALUE_C2C_STATUS);
            pstmtSelect.setString(i++, p_batchID);
            if (p_statusUsed.equals(PretupsI.STATUS_EQUAL) || p_statusUsed.equals(PretupsI.STATUS_NOTEQUAL)) {
                pstmtSelect.setString(i++, p_status);
            }
            
            else if(p_statusUsed.equals(PretupsI.STATUS_IN) || p_statusUsed.equals(PretupsI.STATUS_NOTIN))
            {	
            	for(int j=0;j<args.length;j++)
            	{
            		String param = args[j];
            		param = param.replace("'", "");
            		 pstmtSelect.setString(i++, param);
            	}
            }
		return pstmtSelect;
	}

}

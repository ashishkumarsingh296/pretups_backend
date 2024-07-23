package com.btsl.pretups.restrictedsubs.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public interface RestrictedSubscriberQry {
	 Log _log = LogFactory.getLog(RestrictedSubscriberQry.class.getName());
	PreparedStatement loadScheduleBatchDetailsListQry(Connection p_con,String p_batch_id,String p_statusUsed,  String p_status) throws SQLException ;
	PreparedStatement loadBatchDetailVOListQry(Connection p_con, String p_batchID, String p_statusUsed, String p_status, String batchType) throws SQLException;
}

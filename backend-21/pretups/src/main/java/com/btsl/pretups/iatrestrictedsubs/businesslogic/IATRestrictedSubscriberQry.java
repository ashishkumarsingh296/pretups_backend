package com.btsl.pretups.iatrestrictedsubs.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public interface IATRestrictedSubscriberQry {
	Log LOG = LogFactory.getLog(IATRestrictedSubscriberQry.class.getName());
	String QUERY = "Query : ";
	PreparedStatement loadScheduleBatchDetailsListQry(Connection p_con, String p_batch_id, String p_statusUsed, String p_status) throws SQLException ;
}

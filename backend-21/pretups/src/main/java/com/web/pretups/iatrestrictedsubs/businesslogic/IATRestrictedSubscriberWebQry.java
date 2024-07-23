package com.web.pretups.iatrestrictedsubs.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface IATRestrictedSubscriberWebQry {
	
	public PreparedStatement loadBatchDetailVOListQry(Connection p_con, String p_batchID, String p_statusUsed, String p_status) throws SQLException;

}

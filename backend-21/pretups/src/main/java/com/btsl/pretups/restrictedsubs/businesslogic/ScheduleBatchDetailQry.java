package com.btsl.pretups.restrictedsubs.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public interface ScheduleBatchDetailQry {
	 Log log = LogFactory.getLog(ScheduleBatchDetailQry.class.getName());

	PreparedStatement loadScheduleDetailVOListQry(Connection p_con, String p_ownerID, String p_parentID, String p_msisdn,
			String p_statusUsed, String p_status, boolean p_isStaffUser, String p_userId)throws SQLException;

	PreparedStatement loadScheduleDetailReportVOListQry(Connection p_con, String p_ownerID, String p_parentID,
			String p_msisdn, String p_statusUsed, String p_status, boolean p_isStaffUser, String p_userId,
			Date p_fromScheduleDate, Date p_toScheduleDate)throws SQLException;
}

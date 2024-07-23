package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * @author mohit.miglani
 *
 */
public interface StaffSelfC2CQuery {

	
	/**
	 * @param usersReportModel
	 * @param con
	 * @return
	 * @throws SQLException
	 */
	public PreparedStatement loadStaffSelfC2CChannelReportQry(UsersReportModel usersReportModel,Connection con) throws SQLException ;
	
	
}

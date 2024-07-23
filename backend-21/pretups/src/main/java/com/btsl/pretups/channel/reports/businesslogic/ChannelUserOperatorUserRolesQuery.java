package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * @author mohit.miglani
 *
 */
public interface ChannelUserOperatorUserRolesQuery {
	/**
	 * @param usersReportModel
	 * @param con
	 * @return
	 * @throws SQLException
	 */
	public PreparedStatement loadExternalUserRolesOperatorReportQry(UsersReportModel usersReportModel,Connection con) throws SQLException ;
	/**
	 * @param usersReportModel
	 * @param con
	 * @return
	 * @throws SQLException
	 */
	public PreparedStatement loadExternalUserRolesChannelReportQry(UsersReportModel usersReportModel,Connection con) throws SQLException ;
}

package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * @author pankaj.kumar
 *
 */
public interface StaffC2CTransferdetailsRptQry {

	
/**
 * @param usersReportModel
 * @param con
 * @return
 * @throws SQLException
 * @throws ParseException
 */
public PreparedStatement loadstaffc2cTransferDetailsReportQry(UsersReportModel usersReportModel,Connection con)throws SQLException, ParseException;
	
	/**
	 * @param usersReportModel
	 * @param con
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	public PreparedStatement loadstaffc2cTransferDetailsChannelUserReportQry(UsersReportModel usersReportModel,Connection con)throws SQLException, ParseException;
	
	/**
	 * @param usersReportModel
	 * @param con
	 * @return
	 * @throws SQLException
	 */
	public PreparedStatement loadstaffc2cTransferDetailsDailyReportQry(UsersReportModel usersReportModel,Connection con)throws SQLException;
	
}

package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import com.web.pretups.channel.reports.web.UsersReportModel;
/**
 * 
 * @author simarnoor.bains
 *
 */
public interface UserDailyBalanceMovementRptQuery{
	
	/**
	 * 
	 * @return
	 */
	public PreparedStatement dailyBalanceMovementChnlUserRpt(UsersReportModel usersReportModel,Connection con) throws SQLException, ParseException ;
	
	/**
	 * 
	 * @return
	 */
	public PreparedStatement dailyBalanceMovementOptRpt(UsersReportModel usersReportModel,Connection con) throws SQLException, ParseException;
	
}

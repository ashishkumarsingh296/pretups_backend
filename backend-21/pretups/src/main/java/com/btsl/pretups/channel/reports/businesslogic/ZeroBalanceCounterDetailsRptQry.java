package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * @author tarun.kumar
 *
 */
public interface ZeroBalanceCounterDetailsRptQry {

	/**
	 * @param con
	 * @param usersReportModel
	 * @return
	 */
	public PreparedStatement loadoZeroBalCounterDetailsReportQry(Connection con,UsersReportModel usersReportModel) ;

	/**
	 * @param con
	 * @param usersReportModel
	 * @return
	 */
	public PreparedStatement loadoZeroBalCounterChnlUserDetailsReportQry(Connection con,UsersReportModel usersReportModel) ;  
	
}

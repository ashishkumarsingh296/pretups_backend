package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * @author anubhav.pandey1
 *
 */
public interface OperationSummaryRptQry {



	/**
	 * @param thisForm
	 * @param con
	 * @return
	 */
	PreparedStatement loadOperationSummaryChannelUserMainReport(
			UsersReportModel thisForm, Connection con);

	/**
	 * @param thisForm
	 * @param con
	 * @return
	 */
	PreparedStatement loadOperationSummaryChannelUserTotalReport(
			UsersReportModel thisForm, Connection con);

	/**
	 * @param thisForm
	 * @param con
	 * @return
	 */
	PreparedStatement loadOperationSummaryOperatorMainReport(
			UsersReportModel thisForm, Connection con);

	/**
	 * @param thisForm
	 * @param con
	 * @return
	 */
	PreparedStatement loadOperationSummaryOperatorTotalReport(
			UsersReportModel thisForm, Connection con);

}

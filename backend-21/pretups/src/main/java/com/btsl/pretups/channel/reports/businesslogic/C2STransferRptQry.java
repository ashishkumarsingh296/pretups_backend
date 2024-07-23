package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.web.pretups.channel.reports.web.UsersReportForm;
import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * @author tarun.kumar
 *
 */
public interface C2STransferRptQry {

	/**
	 * @param con
	 * @param usersReportModel
	 * @return
	 */
	public PreparedStatement loadC2sTransferChannelUserReport(Connection con,UsersReportModel usersReportModel); 

	/**
	 * @param con
	 * @param usersReportModel
	 * @return
	 */
	public PreparedStatement loadC2sTransferChannelUserStaffReport(Connection con,UsersReportModel usersReportModel);

	/**
	 * @param con
	 * @param usersReportModel
	 * @return
	 */
	public PreparedStatement loadC2sTransferChannelUserNewReport(Connection con, UsersReportModel usersReportModel);

	/**
	 * @param con
	 * @param usersReportModel
	 * @return
	 */
	public PreparedStatement loadC2sTransferChannelUserStaffNewReport(Connection con, UsersReportModel usersReportModel);

	/**
	 * @param con
	 * @param usersReportModel
	 * @return
	 */
	public PreparedStatement loadC2sTransferNewReport(Connection con,UsersReportModel usersReportModel);

	/**
	 * @param con
	 * @param usersReportModel
	 * @return
	 */
	public PreparedStatement loadC2sTransferReport(Connection con,UsersReportModel usersReportModel);

	/**
	 * @param con
	 * @param usersReportModel
	 * @return
	 */
	public PreparedStatement loadC2sTransferStaffNewReport(Connection con,UsersReportModel usersReportModel);

	/**
	 * @param con
	 * @param usersReportModel
	 * @return
	 */
	public PreparedStatement loadC2sTransferStaffReport(Connection con,UsersReportModel usersReportModel);

	
	/**
	 * @param con
	 * @param usersReportModel
	 * @return
	 */
	public PreparedStatement getEtopBundleChargeQuery(Connection con,UsersReportForm form);
	
	
	public PreparedStatement getC2SIncreaseDecreaseQuery(Connection con,UsersReportForm form);
	
	public PreparedStatement getC2CWithRevIncreaseDecreaseQuery(Connection con,UsersReportForm form);
	
	public PreparedStatement getO2CWithRevIncreaseDecreaseQuery(Connection con,UsersReportForm form);
	
	public PreparedStatement getO2CPaymentTypeIncreaseDecreaseQuery(Connection con,UsersReportForm form);
	
	
		/**
	 * @param con
	 * @param usersReportModel
	 * @return
	 */
	public PreparedStatement getTotalSalesQuery(Connection con,UsersReportForm form);
	
	/**
	 * @param con
	 * @param usersReportModel
	 * @return
	 */
	public PreparedStatement getTotalC2SQuery(Connection con,UsersReportForm form);
	
	public PreparedStatement getO2CPaymentTypeQuery(Connection con,UsersReportForm form);
	
}

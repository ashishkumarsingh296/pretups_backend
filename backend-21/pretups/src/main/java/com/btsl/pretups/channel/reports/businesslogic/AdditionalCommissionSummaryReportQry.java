package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.web.pretups.channel.reports.web.UsersReportModel;

public interface AdditionalCommissionSummaryReportQry {

	public PreparedStatement loadAdditionalCommisionDetailsOperatorDailyQry(Connection con, UsersReportModel usersReportModel);

	public PreparedStatement loadAdditionalCommisionDetailsOperatorMonthlyQry(Connection con, UsersReportModel usersReportModel);

	public PreparedStatement loadAdditionalCommisionDetailsChannelDailyQry(Connection con, UsersReportModel usersReportModel);

	public PreparedStatement loadAdditionalCommisionDetailsChannelMonthlyQry(Connection con, UsersReportModel usersReportModel);
}

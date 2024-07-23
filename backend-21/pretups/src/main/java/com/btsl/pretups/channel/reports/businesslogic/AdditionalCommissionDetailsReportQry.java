package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.web.pretups.channel.reports.web.UsersReportModel;

public interface AdditionalCommissionDetailsReportQry {

	public PreparedStatement loadAdditionalCommisionDetailsOperatorQry(Connection con, UsersReportModel usersReportModel);
	
	public PreparedStatement loadAdditionalCommisionDetailsOperatorOldQry(Connection con, UsersReportModel usersReportModel);
	
	public PreparedStatement loadAdditionalCommisionDetailsChannelQry(Connection con, UsersReportModel usersReportModel);
	
	public PreparedStatement loadAdditionalCommisionDetailsChannelOldQry(Connection con, UsersReportModel usersReportModel);
}

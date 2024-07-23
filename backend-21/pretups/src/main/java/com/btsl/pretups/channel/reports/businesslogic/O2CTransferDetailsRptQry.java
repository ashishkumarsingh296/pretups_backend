package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import com.web.pretups.channel.reports.web.UsersReportModel;

public interface O2CTransferDetailsRptQry {
 
	
	public PreparedStatement loado2cTransferDetailsReportQry(UsersReportModel usersReportModel,Connection con)throws SQLException, ParseException;
	
	public PreparedStatement loado2cTransferDetailsChannelUserReportQry(UsersReportModel usersReportModel,Connection con)throws SQLException, ParseException;
	
	public PreparedStatement loado2cTransferDailyReportQry(UsersReportModel usersReportModel,Connection con)throws SQLException;
	
	
}

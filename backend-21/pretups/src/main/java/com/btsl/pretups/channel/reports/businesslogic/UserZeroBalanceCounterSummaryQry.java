package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.expression.ParseException;

import com.web.pretups.channel.reports.web.UsersReportModel;

public interface UserZeroBalanceCounterSummaryQry {

	public PreparedStatement loadUserBalanceReportQry(UsersReportModel usersReportModel,Connection con,Timestamp fromDateTimeValue,Timestamp toDateTimeValue)throws SQLException, ParseException;
	public PreparedStatement loadzeroBalSummChannelUserReportQry(UsersReportModel usersReportModel,Connection con,Timestamp fromDateTimeValue,Timestamp toDateTimeValue) throws SQLException, ParseException;
	
	
	
	
	
	
}

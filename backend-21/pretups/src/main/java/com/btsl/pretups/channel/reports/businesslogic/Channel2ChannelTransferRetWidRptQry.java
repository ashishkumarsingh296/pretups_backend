package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import com.web.pretups.channel.reports.web.UsersReportModel;

public interface Channel2ChannelTransferRetWidRptQry {
	/*Channel User Start*/
	public PreparedStatement loadC2cRetWidTransferChannelUserUnionListQry(Connection pCon,UsersReportModel usersReportModel) throws SQLException, ParseException ;
	public PreparedStatement loadC2cRetWidTransferChannelUserUnionStaffListQry(Connection pCon,UsersReportModel usersReportModel) throws SQLException, ParseException ;
	public PreparedStatement loadC2cRetWidTransferChannelUserListQry(Connection pCon,UsersReportModel usersReportModel) throws SQLException, ParseException ;
	public PreparedStatement loadC2cRetWidTransferChnlUserStaffListQry(Connection pCon,UsersReportModel usersReportModel) throws SQLException, ParseException ;
	/*Channel User Start Ends*/
	
	/*Operator User Starts*/
	public PreparedStatement loadC2cRetWidTransferUnionListQry(Connection pCon,UsersReportModel usersReportModel) throws SQLException, ParseException ;
	public PreparedStatement loadC2cRetWidTransferUnionStaffListQry(Connection pCon,UsersReportModel usersReportModel) throws SQLException, ParseException ;
	public PreparedStatement loadC2cRetWidTransferListQry(Connection pCon,UsersReportModel usersReportModel) throws SQLException, ParseException ;
	public PreparedStatement loadC2cRetWidTransferStaffListQry(Connection pCon,UsersReportModel usersReportModel) throws SQLException, ParseException ;
	/*Operator User Ends*/
}

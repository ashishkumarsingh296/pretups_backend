package com.web.pretups.channel.reports.service;

import java.sql.SQLException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.channel.reports.web.UsersReportModel;

public interface UserZeroBalanceCounterSummaryService {
	public void loadThresholdtype(UsersReportModel usersReportModel, HttpServletRequest request,HttpServletResponse response,UserVO userVO);
	public boolean displayZeroBalSumReport(UsersReportModel usersReportModel, HttpServletRequest request, HttpServletResponse response, UserVO userVO, ChannelUserVO userVO1,BindingResult bindingResult);
	public String downloadFileforSumm(UsersReportModel usersReportModel)throws BTSLBaseException, SQLException, InterruptedException;
	
	
	
	
}

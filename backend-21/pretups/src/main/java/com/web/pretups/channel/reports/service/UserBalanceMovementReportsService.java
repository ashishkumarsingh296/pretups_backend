package com.web.pretups.channel.reports.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.channel.reports.web.UsersReportModel;

public interface UserBalanceMovementReportsService {

	public Boolean loadUserBalMovementSummaryReportPage(HttpServletRequest request, UserVO userVO,UsersReportModel usersReportModel,Model model) throws  IOException;
	public List<ChannelUserTransferVO> loadFromUserList(UsersReportModel usersReportModel,UserVO userVO, String zoneCode, String domainCode,String fromTransferCategorycode,String userName,HttpServletRequest request) throws BTSLBaseException;
	public boolean displayUserBalanceReport(HttpServletRequest request,HttpServletResponse response,UsersReportModel usersReportModel,UserVO userVO,Model model,BindingResult bindingResult);
	public String downloadFileforSumm(UsersReportModel usersReportModel)
			throws InterruptedException, BTSLBaseException, SQLException;
}

package com.web.pretups.channel.reports.service;

import java.sql.SQLException;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.channel.reports.web.UsersReportModel;

public interface AdditionalCommissionSummaryService {

	public UsersReportModel loadUsersForAdditionalCommissionSummary(UserVO userVO, HttpServletRequest request);
	public boolean loadAdditionalCommissionSummaryReport(UsersReportModel usersReportModel, BindingResult bindingResult, ChannelUserVO userVO, HttpServletRequest request, Model model);
	public String downloadAddCommSummaryCSVReportFile(Model model, UsersReportModel usersReportModel) throws SQLException;
}


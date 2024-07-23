package com.web.pretups.channel.reports.service;

import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.channel.reports.web.UsersReportModel;

public interface AdditionalCommissionDetailsService {

	public UsersReportModel loadUsersForAdditionalCommission(HttpServletRequest request);
	
	public List loadUserList( UsersReportModel usersReportModel, UserVO userVO, String userName, String domainCode, String zoneCode, String parentCategory);
	
	public boolean loadAdditionalCommissionReport(UsersReportModel usersReportModel, BindingResult bindingResult, UserVO userVO, HttpServletRequest request, Model model);
	
	public String downloadAddCommDetailsCSVReportFile(Model model, UsersReportModel usersReportModel) throws SQLException;
}

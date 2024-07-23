package com.web.pretups.channel.reports.service;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * @author anubhav.pandey1
 *
 */
@Service
public interface OperationSummaryReportService {

	/**
	 * @param request
	 * @param userVO
	 * @return
	 */
	public UsersReportModel loadOperationSummaryReport(HttpServletRequest request, UserVO userVO);

	/**
	 * @param userVO
	 * @param parentCategoryCode
	 * @param domainList
	 * @param zoneList
	 * @param userName
	 * @return
	 */
	public List<ChannelUserTransferVO> loadUserList(UserVO userVO, String parentCategoryCode,String domainList, String zoneList, String userName);

	/**
	 * @param model
	 * @param userVO
	 * @param usersReportModel
	 * @param sessionUserReportForm
	 * @param request
	 * @param response
	 * @param bindingResult
	 * @return
	 */
	public boolean displayOperationSummaryReport(Model model, UserVO userVO,UsersReportModel usersReportModel,UsersReportModel sessionUserReportForm, HttpServletRequest request,
			HttpServletResponse response, BindingResult bindingResult);

	/**
	 * @param usersReportModelNew
	 * @return
	 */
	public String downloadFileforSumm(UsersReportModel usersReportModelNew);

}

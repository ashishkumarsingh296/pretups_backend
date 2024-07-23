package com.web.pretups.channel.reports.service;

import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * @author mohit.miglani
 *
 */
public interface ChannelUserOperatorUserRolesService {
	/**
	 * @param usersReportModel
	 * @param request
	 * @param response
	 * @param userVO
	 */
	public void loadO2cUserRoles(UsersReportModel usersReportModel, HttpServletRequest request,HttpServletResponse response,UserVO userVO);
	
	/**
	 * @param usersReportModel
	 * @param request
	 * @param response
	 * @param userVO
	 * @param userVO1
	 * @param bindingResult
	 * @return
	 */
	public boolean displayChannelUserRolesReport(UsersReportModel usersReportModel, HttpServletRequest request, HttpServletResponse response, UserVO userVO, ChannelUserVO userVO1,BindingResult bindingResult);
	/**
	 * @param userVO
	 * @param parentCategoryCode
	 * @param domainList
	 * @param zoneList
	 * @param userName
	 * @return
	 */
	public List loadUserList(UserVO userVO, String parentCategoryCode,
			String domainList, String zoneList, String userName);
	/**
	 * @param usersReportModel
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws InterruptedException
	 */
	public String downloadFileforSumm(UsersReportModel usersReportModel)throws BTSLBaseException, SQLException, InterruptedException;

}

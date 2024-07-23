package com.web.pretups.channel.reports.service;

import java.sql.SQLException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * @author mohit.miglani
 *
 */
public interface StaffSelfC2CService {
	
	/**
	 * @param usersReportModel
	 * @param request
	 * @param response
	 * @param userVO
	 * @return
	 */
	public boolean loadStaffC2CTransferDetails(UsersReportModel usersReportModel, HttpServletRequest request,HttpServletResponse response,UserVO userVO);
	
	
	/**
	 * @param usersReportModel
	 * @param request
	 * @param response
	 * @param model
	 * @param userVO
	 * @param userVO1
	 * @param bindingResult
	 * @return
	 */
	public boolean displaySelfTransactionReport(UsersReportModel usersReportModel, HttpServletRequest request, HttpServletResponse response, Model model,UserVO userVO, ChannelUserVO userVO1,BindingResult bindingResult);
	
	/**
	 * @param usersReportModel
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws InterruptedException
	 */
	public String downloadFileforSumm(UsersReportModel usersReportModel)throws BTSLBaseException, SQLException, InterruptedException;
	
}

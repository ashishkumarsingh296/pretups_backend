package com.web.pretups.channel.reports.service;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.channel.reports.web.LmsRedemptionReportModel;
import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * 
 * @author sweta.verma
 *
 */
public interface LMSRedemptionReportsService {
	/**
	 * 
	 * @param userVO
	 * @param lmsRedemptionReportModel
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public Boolean loadLmsRedemptionReportPage(UserVO userVO,LmsRedemptionReportModel lmsRedemptionReportModel,Model model, HttpServletRequest request, HttpServletResponse response) throws  IOException;
	
	/**
	 * 
	 * @param userVO
	 * @param lmsRedemptionReportModel
	 * @param model
	 * @param bindingResult
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public boolean displayLMSRedemptionDetailsReportPage(UserVO userVO,LmsRedemptionReportModel lmsRedemptionReportModel, Model model,BindingResult bindingResult,HttpServletRequest request)throws IOException;
	
	/**
	 * 
	 * @param lmsRedemptionReportModel
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws InterruptedException
	 */
	public String downloadLmsReport(
			LmsRedemptionReportModel lmsRedemptionReportModel) throws BTSLBaseException, SQLException, InterruptedException;
	
	
}

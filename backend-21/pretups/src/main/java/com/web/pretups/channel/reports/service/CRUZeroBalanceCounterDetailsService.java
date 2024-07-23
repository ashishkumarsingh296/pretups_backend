package com.web.pretups.channel.reports.service;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.common.ListValueVO;
import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * @author tarun.kumar
 *
 */
public interface CRUZeroBalanceCounterDetailsService {

/**
 * @param request
 * @param response
 * @param usersReportModel
 * @param userVO
 * @param model
 */
public 	void loadZeroBalCounterDetail(HttpServletRequest request,HttpServletResponse response, UsersReportModel usersReportModel,UserVO userVO, Model model) ;

/**
 * @param request
 * @param response
 * @param usersReportModelNew
 * @param userVO
 * @param model
 * @param bindingResult
 * @return
 */
public 	boolean displayUserBalanceReportList(HttpServletRequest request,HttpServletResponse response, UsersReportModel usersReportModelNew,UserVO userVO, Model model,BindingResult bindingResult);

/**
 * @param usersReportModelNew
 * @return
 */
public	String downloadCSVReportZeroBalCounterReportFile(UsersReportModel usersReportModelNew) ;

/**
 * @param categorycode
 * @param userName
 * @param channelUserVO
 * @return
 */
public List<ListValueVO> getUserList(UserVO userVO, String zoneCode,String domainCode, String userName, String parentCategoryCode);



}  

package com.web.pretups.channel.reports.service;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.common.ListValueVO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.channel.reports.web.UsersReportModel;



/**
 * @author tarun.kumar
 *
 */
public interface AddChannelUserService {

	/**
	 * @param request
	 * @param response
	 * @param usersReportModel
	 * @param userVO
	 * @param model
	 */
	void loadDomainList(HttpServletRequest request,	HttpServletResponse response, UsersReportModel usersReportModel,UserVO userVO, Model model);

	/**
	 * @param usersReportModelNew
	 * @param userVO
	 * @param domainCode
	 * @return
	 */
	List<CategoryVO> loadCategoryLst(UsersReportModel usersReportModelNew,UserVO userVO, String domainCode);

	/**
	 * @param userVO
	 * @param zoneCode
	 * @param domainCode
	 * @param userName
	 * @param parentCategoryCode
	 * @return
	 */
	List<ListValueVO> getAddChannelUserList(UserVO userVO, String zoneCode,String domainCode, String userName, String parentCategoryCode,HttpServletRequest request, HttpServletResponse response);

	/**
	 * @param request
	 * @param response
	 * @param usersReportModelNew
	 * @param userVO
	 * @param model
	 * @param bindingResult
	 * @return 
	 */
	boolean addChannelUserSubmit(HttpServletRequest request,HttpServletResponse response,UsersReportModel usersReportModel, UsersReportModel usersReportModelNew,	UserVO userVO, Model model, BindingResult bindingResult);

	

	

}

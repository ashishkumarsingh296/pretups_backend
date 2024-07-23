package com.web.pretups.channel.reports.service;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.common.ListValueVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * @author tarun.kumar
 *
 */
public interface CRC2STransferService {

	/**
	 * @param request
	 * @param response
	 * @param usersReportModel
	 * @param userVO
	 * @param model
	 */
	public void loadC2STransferDetails(HttpServletRequest request,	HttpServletResponse response, UsersReportModel usersReportModel,UserVO userVO, Model model);

	/**
	 * @param arr
	 * @param userName
	 * @param channelUserVO
	 * @return
	 */
	public List<ListValueVO> c2sLoadUserList(UserVO userVO, String zoneCode,String domainCode, String userName, String parentCategoryCode);

	/**
	 * @param request
	 * @param response
	 * @param usersReportModelNew
	 * @param userVO
	 * @param model
	 * @param bindingResult
	 * @return
	 */
	public boolean c2sTransferSubmit(HttpServletRequest request,HttpServletResponse response, UsersReportModel usersReportModelNew,UserVO userVO, Model model, BindingResult bindingResult);

	/**
	 * @param usersReportModelNew
	 * @return
	 */
	public String c2STransferDownloadCSVReportFile(UsersReportModel usersReportModelNew);

	

	

	

}

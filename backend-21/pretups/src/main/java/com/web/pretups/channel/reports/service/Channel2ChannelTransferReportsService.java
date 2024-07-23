package com.web.pretups.channel.reports.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.channel.reports.web.UsersReportModel;

public interface Channel2ChannelTransferReportsService {
	public Boolean loadC2CTransferReportPage(UserVO userVO,UsersReportModel usersReportModel,Model model) throws  IOException;
	public boolean displayC2CTransferReportPage(UserVO userVO,UsersReportModel usersReportModel,Model model,BindingResult bindingResult, HttpServletRequest request) throws  IOException;
	public List<ListValueVO> loadToUserList(UsersReportModel usersReportModel,String domainCode,UserVO userVO,String fromUserID,String fromTransferCategorycode,String toTransferCategorycode, String userName) throws BTSLBaseException;
	public List<ListValueVO> loadFromUserList(UserVO userVO, String zoneCode, String domainCode,String fromTransferCategorycode,String userName) throws BTSLBaseException;
	public String downloadCSVReportFile(Model model, UsersReportModel usersReportModelNew) throws BTSLBaseException, SQLException, InterruptedException;
	
	
	

	

}

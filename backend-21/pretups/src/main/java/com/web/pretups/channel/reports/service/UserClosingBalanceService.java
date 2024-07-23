package com.web.pretups.channel.reports.service;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.validator.ValidatorException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.xml.sax.SAXException;

import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.channel.reports.web.UsersReportModel;

@Service
public interface UserClosingBalanceService {
	
	public UsersReportModel loadUserClosingBalance(UserVO userVO);

	public ArrayList loadUserList(UserVO userVO, String parentCategoryCode,
			String domainList, String zoneList, String userName);


	public boolean downloadClosingBalance(Model model, UserVO userVO, UsersReportModel userForm,
			UsersReportModel sessionUserReportForm, HttpServletRequest request, HttpServletResponse response, BindingResult bindingResult) throws ValidatorException, IOException, SAXException;

}

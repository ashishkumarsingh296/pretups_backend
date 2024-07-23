package com.web.pretups.channel.user.service;

import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.channel.user.web.ChangeLocaleModel;

public interface ChangeNotificationLanguageService {

	int loadSelfinfo(HttpServletRequest request, UserVO userVO,
			ChangeLocaleModel changeLocaleModel, Model model);
	
	public List<UserVO> loadUserList(ChangeLocaleModel changeLocaleModel,UserVO userVO, String categorycode,
			String userName,HttpServletRequest request) throws BTSLBaseException;
	
	public boolean loadUserPhoneDetails(ChangeLocaleModel changeLocaleModel,HttpServletRequest request,HttpServletResponse response,Model model,UserVO userVO,BindingResult bindingResult);
	
	public void loadconfirmSelfLang(ChangeLocaleModel changeLocaleModel,HttpServletRequest request,HttpServletResponse response,Model model,UserVO userVO,BindingResult bindingResult)throws SQLException;
}

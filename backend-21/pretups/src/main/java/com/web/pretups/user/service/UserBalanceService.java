package com.web.pretups.user.service;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.web.user.web.UserModel;



@Service
public interface UserBalanceService {
	
	public UserVO loadSelfBalance(ChannelUserVO channelUserSessionVO, UserVO userVO);
    public String downloadFileForEnq(UserVO userVO,  HttpServletRequest request);
	public UserModel loadCategory(UserVO userVO);
	public List<UserVO> loadUserList(UserVO userVO, String categorycode, String ownerId,
			String userName, String domainCode, String prntDomainCode, HttpServletRequest request, String index);
	public boolean loadUserBalance(Model model, UserModel userModel,
			UserVO userVO, ChannelUserVO channelUserSessionVO, BindingResult bindingResult, HttpServletRequest request);
	public void getCategoryList(UserModel userModel);

	public List<UserVO> loadOwnerList(UserVO userVO, String prntDomaincode, String ownerName, String domainCode, HttpServletRequest request);
}

package com.web.pretups.user.service;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.web.user.web.UserModel;


public interface AssociateProfileService {
	
	public UserModel loadAssociateProfile(UserVO userVO);
	public List<UserVO> loadUserList(UserVO userVO, String categorycode, String ownerId,
			String userName, String domainCode, String prntDomainCode, HttpServletRequest request, String index);
	public void getCategoryList(UserModel userModel);

	public List<UserVO> loadOwnerList(UserVO userVO, String prntDomaincode, String ownerName, String domainCode, HttpServletRequest request);
	public boolean getAssociationDetails(ChannelUserVO channelUserSessionVO, UserModel userModel, Model model, BindingResult bindingResult, HttpServletRequest request);
	 public boolean processProfileAssociation(UserModel userModel, UserVO sessionUserVO, Model model, BindingResult bindingResult);
}

package com.client.pretups.user.service;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.web.user.web.UserModel;

public interface UserProfileThresholdService {
	public List<ListValueVO> loadDomain() throws BTSLBaseException;

	public List<CategoryVO> loadCategory(String domain, String networkId);

	public List<UserVO> loadUserList(ChannelUserVO channelUserVO,
			HttpServletRequest request, Model model, UserModel userModel,
			String ownerId, String geodomainCode, String sequenceNo)
			throws BTSLBaseException;

	public List<UserVO> loadParentUserList(HttpServletRequest request,
			UserModel userModel, ChannelUserVO channelUserVO,
			String categorycode, String ownName, String geodomaincode,
			String domainCode) throws BTSLBaseException;

	public boolean loadUserProfile(Model model, UserModel userModel,
			ChannelUserVO channelUserSessionVO, HttpServletRequest request,
			HttpServletResponse response, BindingResult bindingResult);

	public boolean loadUserProfileByMobileNo(Model model, UserModel userModel,
			ChannelUserVO channelUserSessionVO, BindingResult bindingResult,
			HttpServletRequest request, HttpServletResponse response);

}

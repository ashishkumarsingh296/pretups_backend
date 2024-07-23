package com.web.pretups.user.service;

import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.web.user.web.UserModel;

/**
 * @author pankaj.rawat
 */
@Service
public interface ChannelUserService 
{
	
	/**
	 * @param userModel
	 * @param model
	 * @param request
	 * @param channelUserVO
	 */
	public void loadDomainList(ChannelUserVO channelUserVO,UserModel userModel,final Model model, HttpServletRequest request) throws SQLException;
	public void loadCategoryList(UserModel theForm);
	public boolean loadChnlUserDetails(Model model, ChannelUserVO channelUserSessionVO, UserModel userModel, BindingResult bindingResult, HttpServletRequest request);
	public List<UserVO> loadUserList(UserVO userVO, String categorycode, String ownerId,
			String userName, String domainCode, String prntDomainCode, HttpServletRequest request, String index);
	public List<UserVO> loadOwnerList(UserVO userVO, String prntDomaincode, String ownerName, String domainCode, HttpServletRequest request);
	
	public void getCategoryList(UserModel userModel);
}

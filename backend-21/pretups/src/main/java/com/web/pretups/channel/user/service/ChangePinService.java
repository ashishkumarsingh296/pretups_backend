package com.web.pretups.channel.user.service;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.web.user.web.UserModel;

public interface ChangePinService {

	public ArrayList<UserPhoneVO> loadSelfPin(ChannelUserVO channelUserVO, Model model);
	
	public List<String> processData(UserModel userModel, ChannelUserVO channelUserVO, Model model, BindingResult bindingResult, ArrayList<UserPhoneVO> phoneListData,HttpServletRequest request);
	
	public boolean changePin(Model model, ChannelUserVO channelUserSessionVO, UserModel userModel, BindingResult bindingResult, HttpServletRequest request);
}

package com.btsl.pretups.channel.userreturn.service;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.pretups.channel.userreturn.web.ChnnlToChnnlReturnWithdrawModel;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;

/**
 * @author akanksha
 *Interface which provides base for ChnnlToChnnlReturnWithdrawServiceImpl class
 * also declares different method for C2C withdraw return via channel users functionalities
 */
public interface ChnnlToChnnlReturnWithdrawService {

	public List<ListValueVO> loadCategory(ChannelUserVO channelUserVO) throws  IOException,BTSLBaseException;
	public List<ListValueVO> loadUserList(ChannelUserVO channelUserVO,String toCategory, String username) throws  IOException,BTSLBaseException;
	public Boolean showProductDetails(ChannelUserVO userVO,ChnnlToChnnlReturnWithdrawModel withdrawReturnModel, BindingResult bindingResult,Locale locale,Model model,HttpServletRequest request) throws  IOException;
	public Boolean  confirmWithdrawUserProducts(ChannelUserVO userVO,ChnnlToChnnlReturnWithdrawModel withdrawReturnModel, BindingResult bindingResult,Locale locale,Model model,HttpServletRequest request) throws  IOException;
	public Boolean approveWithdrawReturn(ChannelUserVO userVO,ChnnlToChnnlReturnWithdrawModel withdrawReturnModel, BindingResult bindingResult,Locale locale,Model model) throws  IOException;
}


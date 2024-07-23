package com.web.pretups.channel.transfer.service;


import java.util.List;
import java.util.Locale;

import com.btsl.pretups.channel.transfer.businesslogic.AutoCompleteUserDetailsResponseVO;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.web.pretups.channel.transfer.web.C2CTransferModel;



public interface C2CTransferService {

	List<ListValueVO> loadCategoryList(ChannelUserVO channelUserVO) throws BTSLBaseException;
	List<AutoCompleteUserDetailsResponseVO> loadUserList(String categorycode, String userName, ChannelUserVO channelUserVO) throws BTSLBaseException;
	public boolean loadUserProductsdetails(C2CTransferModel theForm, ChannelUserVO sessionUser,BindingResult bindingResult, Locale locale,Model model,HttpServletRequest request) throws BTSLBaseException, Exception;
	public boolean channelproductConfirm(C2CTransferModel theFormNew, ChannelUserVO sessionUserVO, Model model, BindingResult bindingResult,HttpServletRequest request);
	public boolean approveTransferOrder(C2CTransferModel theFormNew, ChannelUserVO sessionUserVO,Model model, BindingResult bindingResult,HttpServletRequest request) throws BTSLBaseException;
	void loadloggedinUserdetails(C2CTransferModel c2cTransferModel, Model model, ChannelUserVO channelUserVO);
}

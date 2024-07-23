package com.web.pretups.channel.transfer.service;

import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.web.pretups.channel.transfer.web.C2SRechargeModel;


public interface RechargeService {
	
	
	
	/**
	 * loads assigned services and product balance of the channel user
	 * @return
	 */
	public C2SRechargeModel loadServicesBalance(ChannelUserVO channelUserVO,C2SRechargeModel c2sRechargeModel,Model model)throws Exception;
	
	/**
	 *  checks whether the user is barred as sender or not
	 * @param channelUserVO
	 * @param c2sRechargeModel
	 * @throws BTSLBaseException
	 */
	public boolean confirmC2SRecharge(ChannelUserVO channelUserVO,C2SRechargeModel c2sRechargeModel,HttpServletRequest request,Model model,BindingResult bindingresults)throws BTSLBaseException;
	
	
	/**
	 * method called when confirm is clicked from recharge screen
	 * @param channelUserVO
	 * @param c2sRechargeModel
	 * @param senderLanguage
	 * @throws BTSLBaseException
	 */
	public C2SRechargeModel recharge(ChannelUserVO channelUserVO,C2SRechargeModel c2sRechargeModel,Locale senderLanguage,HttpServletRequest request,Model model) throws BTSLBaseException;
	
	/**
	 * method is called when link is clicked from recharge screen
	 * @param request
	 * @param c2sRechargeModel
	 * @return
	 * @throws BTSLBaseException
	 */
	public String notify(HttpServletRequest request,C2SRechargeModel theForm,ChannelUserVO channelUserVO) throws BTSLBaseException;

}

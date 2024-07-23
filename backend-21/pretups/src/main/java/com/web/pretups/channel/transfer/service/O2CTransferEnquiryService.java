package com.web.pretups.channel.transfer.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.validator.ValidatorException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.channel.transfer.web.ChannelTransferEnquiryModel;

public interface O2CTransferEnquiryService {
	
	
	
	public void channelUserEnquiry (ChannelUserVO sessionUser ,Model model,ChannelTransferEnquiryModel channelTransferEnquiryModel) throws IOException,BTSLBaseException,ParseException;
	
	public List<ListValueVO> loadUserList(String userType,UserVO userVO, String categorycode,
			String userName,ChannelUserVO channelUserVO,ChannelUserVO sessionUserVO,ChannelTransferEnquiryModel channeltransferEnqModel) throws BTSLBaseException;
	public String downloadFileforEnq(ChannelTransferEnquiryModel channelTransferEnqModel,HttpServletRequest request) throws IOException,BTSLBaseException,ParseException;

	public boolean enquirySearch(ChannelUserVO channelUserVO,Model model,ChannelTransferEnquiryModel channelTransferEnqModel,HttpServletRequest request,BindingResult bindingResult) throws IOException,BTSLBaseException,ParseException,ValidatorException,SAXException;
	
	public void enquiryDetail(ChannelTransferEnquiryModel channelTransferEnquiryModel) throws BTSLBaseException, ParseException;
	
	
}

package com.web.pretups.channel.transfer.service;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.channel.transfer.web.ChnlToChnlEnquiryModel;


/**
 * @author Himanshu.Kumar
 *
 */
public interface ChnlToChnlEnquiryService {

	public boolean loadTransferTypeList(ChnlToChnlEnquiryModel chnlToChnlEnquiryModel,UserVO userVO, Model model, HttpServletRequest request) throws  IOException;
	public boolean showEnquiryDetails(UserVO userVO,ChnlToChnlEnquiryModel chnlToChnlEnquiryModel, Model model, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) throws IOException;
	public String downloadFileForEnq(ChnlToChnlEnquiryModel chnlToChnlEnquiryModel, Model model,HttpServletRequest request, HttpServletResponse response) throws IOException;
	public boolean enquiryDetail(ChnlToChnlEnquiryModel chnlToChnlEnquiryModel, HttpServletRequest request) throws IOException;
}

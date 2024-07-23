package com.web.pretups.channel.query.Service;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.validator.ValidatorException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.channel.query.web.C2STransferEnquiryModel;

/**
 * 
 * @author Deepa.Shyam
 *This interface provides base for C2STransferEnquiryServiceImpl class
 * also provides method declaration for C2S Transfer Enquiry via channel users and operator users functionalities
 */
@Service
public interface C2STransferEnquiryService {

	public void loadList(UserVO userVO, C2STransferEnquiryModel c2sTransferEnquiryModel, Model model) throws BTSLBaseException;
	public boolean loadTransferEnquiryListFromData(UserVO userVO, C2STransferEnquiryModel c2sTransferEnquiryModel,BindingResult bindingResult, HttpServletRequest request, Model model) throws ValidatorException, IOException, SAXException,BTSLBaseException;
	public String downloadFileForEnq(C2STransferEnquiryModel c2sTransferEnquiryModel,HttpServletRequest request,Model model);
	public void loadTransferItemsVOList(
			 C2STransferEnquiryModel theForm,HttpServletRequest request);
}

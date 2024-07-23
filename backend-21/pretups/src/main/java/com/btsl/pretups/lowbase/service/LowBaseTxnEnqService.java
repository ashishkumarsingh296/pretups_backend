package com.btsl.pretups.lowbase.service;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.lowbase.businesslogic.LowBasedRechargeVO;

/**
 * This Interface provides basic method for Low Base Transaction Enquiry
 * @author lalit.chattar
 *
 */
public interface LowBaseTxnEnqService {

	/**
	 * This method declaration is for loading Low Base Transaction Enquiry Details
	 * @param basedRechargeVO The LowBasedRechargeVO object
	 * @param bindingResult The BindingResult object
	 * @param model The Model object
	 * @return boolean
	 */
	public Boolean processLowBaseTransactionEnquiryForm(LowBasedRechargeVO basedRechargeVO, BindingResult bindingResult, Model model) throws BTSLBaseException;

	/**
	 * This method declaration is for loading Low Base Subscriber Eligibility Enquiry data
	 * @param basedRechargeVO The LowBasedRechargeVO object
	 * @param bindingResult The BindingResult object
	 * @param model The Model object
	 * @return boolean
	 */
	public Boolean processLowBaseSubEligibilityRequest(LowBasedRechargeVO basedRechargeVO, BindingResult bindingResult, Model model) throws BTSLBaseException;
}

package com.btsl.pretups.lowbase.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestClient;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.lowbase.businesslogic.LowBasedRechargeVO;
import com.btsl.pretups.subscriber.service.BarredUserServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * This class provides basic method implementation of all the methods 
 * declared in LowBaseTxnEnqService interface. Which provides implementation
 * for performing Low Base Transaction Enquiry and Eligibility Enquiry
 * @author lalit.chattar
 *
 */

@Service("lowBaseTxnEnqService")
public class LowBaseTxnEnqServiceImpl implements LowBaseTxnEnqService {

	public static final Log _log = LogFactory.getLog(BarredUserServiceImpl.class.getName());
	
	private static final String CLASS_NAME = "LowBaseTxnEnqServiceImpl";
	
	
	@Autowired
	private PretupsRestClient pretupsRestClient;
	
	
	/**
	 * This method load data for Low Base Transaction Enquiry by calling
	 * rest service
	 * 
	 * @param lowBaseVO LowBaseVO Object
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Boolean processLowBaseTransactionEnquiryForm(LowBasedRechargeVO basedRechargeVO, BindingResult bindingResult, Model model) throws BTSLBaseException{

		final String methodName = "#loadLowBaseTransactionEnquiry";
		if (_log.isDebugEnabled()) {
			_log.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
		}
		
		try {
			Map<String, Object> requestObject = new HashMap<>();
			requestObject.put("data", basedRechargeVO);
			requestObject.put("type", PretupsI.LOW_BASE_TRANSACTION_ENQUIRY);

			String responseString = pretupsRestClient.postJSONRequest(requestObject, PretupsI.LOW_BASE_TRANSACTION_ENQUIRY);

			PretupsResponse<List<LowBasedRechargeVO>> pretupsResponse = (PretupsResponse<List<LowBasedRechargeVO>>) PretupsRestUtil
					.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<LowBasedRechargeVO>>>() {
					});

			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			if (!pretupsRestUtil.processFormAndFieldError(pretupsResponse, bindingResult)) {
				return false;
			}
			
			model.addAttribute("lowBaseSubEnquiryObject", pretupsResponse.getDataObject());
			return true;
		} catch (IOException | RuntimeException e) {
			throw new BTSLBaseException(e);
		}finally{
			if (_log.isDebugEnabled()) {
				_log.debug(CLASS_NAME+methodName, PretupsI.EXITED);
			}
		}
	}
	
	
	
	/**
	 * This method fetch data for Low Base Subscriber Eligibility Enquiry by calling
	 * rest service
	 * 
	 * @param basedRechargeVO LowBasedRechargeVO Object
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Boolean processLowBaseSubEligibilityRequest(LowBasedRechargeVO basedRechargeVO, BindingResult bindingResult, Model model) throws BTSLBaseException {

		final String methodName = "#processLowBaseSubEligibilityRequest";
		if (_log.isDebugEnabled()) {
			_log.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
		}
		 
		try {
			Map<String, Object> requestObject = new HashMap<>();
			requestObject.put("data", basedRechargeVO);
			requestObject.put("type", PretupsI.LOW_BASE_ELIGIBILITY_ENQUIRY);

			String responseString = pretupsRestClient.postJSONRequest(requestObject, PretupsI.LOW_BASE_ELIGIBILITY_ENQUIRY);

			PretupsResponse<LowBasedRechargeVO> pretupsResponse = (PretupsResponse<LowBasedRechargeVO>) PretupsRestUtil
					.convertJSONToObject(responseString, new TypeReference<PretupsResponse<LowBasedRechargeVO>>() {
					});

			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			if (!pretupsRestUtil.processFormAndFieldError(pretupsResponse, bindingResult)) {
				return false;
			}
			
			model.addAttribute("lowBaseSubEligibityObject", pretupsResponse.getDataObject());
			return true;
		} catch (IOException | RuntimeException e) {
			throw new BTSLBaseException(e);
		}finally{
			if (_log.isDebugEnabled()) {
				_log.debug(CLASS_NAME+methodName, PretupsI.EXITED);
			}
		}
	}

}

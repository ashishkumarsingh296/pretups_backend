package com.btsl.pretups.channel.profile.businesslogic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.validator.ValidatorException;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.btsl.common.CommonValidator;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.WebServiceKeywordCacheVO;
import com.web.pretups.channel.profile.web.CommissionProfileModel;

/**
 * @(#)CommissionProfileValidator.java
 *                                  Copyright(c) 2016,Mahindra Comviva.
 *                                  All Rights Reserved
 * 
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Author Date History
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                 jashobanta.mahapatra 20/06/2016
 * 
 *                                 This class validates the external requests 
 *                                 as well as UI requests for commission profile module.
 * 
 */

@Component
public class CommissionProfileValidator {
	
	public static final Log _log = LogFactory.getLog(CommissionProfileValidator.class.getName());


	public List<String> validateCommissionProfileList(CommissionProfileModel commissionProfileModel){
		
		String METHOD_NAME =  "CommissionProfileValidator: validateC2SReversal";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Entered");
		}
		
		List<String> errorMessages = new ArrayList<String>();
		for (CommissionProfileSetVO commissionProfileSetVO:commissionProfileModel.getSelectCommProfileSetList()) {
			if(commissionProfileSetVO.getStatus() == null || commissionProfileSetVO.getStatus().equals("S")){
				
				if(commissionProfileSetVO.getLanguage1Message()== null||  commissionProfileSetVO.getLanguage1Message().equals("")){
					//errorMessages.add(new ApplicationContextProvider().getApplicationContext().getMessage("profile.commissionprofilelist.errors.language1.required", new String[]{"Language 1", commissionProfileSetVO.getCommProfileSetName()}, null));
					errorMessages.add(PretupsRestUtil.getMessageString("profile.commissionprofilelist.errors.language1.required", new String[]{"Language 1", commissionProfileSetVO.getCommProfileSetName()} ));
				}
				if(commissionProfileSetVO.getLanguage2Message()== null||  commissionProfileSetVO.getLanguage2Message().equals("")){
					//errorMessages.add(new ApplicationContextProvider().getApplicationContext().getMessage("profile.commissionprofilelist.errors.language2.required", new String[]{"Language 2", commissionProfileSetVO.getCommProfileSetName()}, null));
					errorMessages.add(PretupsRestUtil.getMessageString("profile.commissionprofilelist.errors.language2.required",  new String[]{"Language 2", commissionProfileSetVO.getCommProfileSetName()}));
				}
			}
		}
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Exist");
		}
		
		return errorMessages;
	}
	
	public void formValidateCommissionProfileList(PretupsResponse<CommissionProfileModel> pretupsResponse , CommissionProfileModel commissionProfileModel){
		String METHOD_NAME =  "CommissionProfileValidator: formValidateCommissionProfileList";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Entered");
		}
		List<String> errorMessages = validateCommissionProfileList(commissionProfileModel);
		if(errorMessages != null && !errorMessages.isEmpty()){
			pretupsResponse.setStatus(false);
			pretupsResponse.setStatusCode(PretupsI.RESPONSE_FAIL);
			pretupsResponse.setFormError("profile.commissionprofilestatus.suspend.language.message.required");
			pretupsResponse.setMessageCode(PretupsErrorCodesI.COMMISSION_PROFILE_STATUS_CHANGE_FAILED_WITHOUT_LANGUAGE_MESSAGE);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Exist");
		}
	}
	
	public void  validateCommissionProfileStatus(String type,String logInId,PretupsResponse<CommissionProfileModel> pretupsResponse , CommissionProfileModel commissionProfileModel) throws ValidatorException, IOException, SAXException{
		
		String METHOD_NAME =  "CommissionProfileValidator: validateCommissionProfileStatus";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Entered");
		}
		/* common validator */
		WebServiceKeywordCacheVO webServiceKeywordCacheVO = ServiceKeywordCache
				.getWebServiceTypeObject(type);
		String validationXMLpath = webServiceKeywordCacheVO.getValidatorName();
		CommonValidator commonValidator = new CommonValidator(validationXMLpath, commissionProfileModel, "CommissionProfileModel");
		Map<String, String> errorMessages = commonValidator.validateModel();
		pretupsResponse.setFieldError(errorMessages);
		/*end common validator */	
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Exist");
		}
	}
}

package com.btsl.pretups.channel.userreturn.web;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.validator.ValidatorException;
import org.xml.sax.SAXException;

import com.btsl.common.CommonValidator;
import com.btsl.common.PretupsResponse;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.WebServiceKeywordCacheVO;


public class C2CWithdrawViaAdmValidator {
	
	public static final Log logger = LogFactory.getLog(C2CWithdrawViaAdmValidator.class.getName());
	
	/**
	 * @param type
	 * @param response
	 * @param withdrawVO
	 * @param referenceName
	 * @throws ValidatorException
	 * @throws IOException
	 * @throws SAXException
	 */
	public void validateRequestData(String type,PretupsResponse<?> response,C2CWithdrawVO withdrawVO,String referenceName) throws ValidatorException, IOException, SAXException {
		if (logger.isDebugEnabled()) {
			logger.debug("C2CwithdrawValidator#validateRequestData", "Entered: ");
		}
		
		WebServiceKeywordCacheVO webServiceKeywordCacheVO=ServiceKeywordCache
				.getWebServiceTypeObject(type);
		String validationXMLpath = webServiceKeywordCacheVO.getValidatorName();
		CommonValidator commonValidator = new CommonValidator(validationXMLpath, withdrawVO, referenceName);
		Map<String, String> errorMessages = commonValidator.validateModel();
		response.setFieldError(errorMessages);
		if (logger.isDebugEnabled()) {
			logger.debug("C2CwithdrawValidator#validateRequestData", "Exiting" + errorMessages.toString());
		}

	}

}

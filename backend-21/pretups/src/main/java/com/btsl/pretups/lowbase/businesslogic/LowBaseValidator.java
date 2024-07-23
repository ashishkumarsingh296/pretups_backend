package com.btsl.pretups.lowbase.businesslogic;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.validator.ValidatorException;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonValidator;
import com.btsl.common.PretupsResponse;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.WebServiceKeywordCacheVO;

/**
 * This class provides methods to validate data for Low Base request
 * @author lalit.chattar
 *
 */
public class LowBaseValidator {

	
	private static final Log _log = LogFactory.getLog(LowBaseValidator.class.getName());
	private static final String CLASS_NAME = "LowBaseValidator";
	
	/**
	 * Validate request data using common validator api
	 *
	 * @param lowBasedRechargeVO
	 *            The LowBasedRechargeVO object having data to be processed
	 * @param type type of validation
	 *            
	 * @param response
	 *            PretupsResponse for storing response
	 * @param referenceName Validator reference 
	 * @return
	 * @throws BTSLBaseException
	 */
	public void validateRequestData(String type, PretupsResponse<?> response, LowBasedRechargeVO lowBasedRechargeVO, String referenceName) throws BTSLBaseException{
		
		final String methodName = "#validateRequestData";
		try {
			if (_log.isDebugEnabled()) {
				_log.debug(CLASS_NAME+methodName, "Entered");
			}
			
			WebServiceKeywordCacheVO webServiceKeywordCacheVO = ServiceKeywordCache
					.getWebServiceTypeObject(type);
			String validationXMLpath = webServiceKeywordCacheVO.getValidatorName();
			CommonValidator commonValidator = new CommonValidator(validationXMLpath, lowBasedRechargeVO, referenceName);
			Map<String, String> errorMessages = commonValidator.validateModel();
			if (_log.isDebugEnabled()) {
				_log.debug(CLASS_NAME+methodName,  "Error Messages >> " + errorMessages);
			}
			response.setFieldError(errorMessages);
			
		} catch (ValidatorException | IOException | SAXException e) {
			throw new BTSLBaseException(e);
		}finally{
			if (_log.isDebugEnabled()) {
				_log.debug(CLASS_NAME+methodName, "Exiting");
			}
		}

	}
}

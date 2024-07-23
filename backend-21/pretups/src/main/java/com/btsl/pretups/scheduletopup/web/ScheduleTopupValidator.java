package com.btsl.pretups.scheduletopup.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.validator.ValidatorException;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonValidator;
import com.btsl.common.PretupsResponse;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.WebServiceKeywordCacheVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.restrictedsubs.web.RestrictedSubscriberModel;

public class ScheduleTopupValidator {

	public static final Log _log = LogFactory.getLog(ScheduleTopupValidator.class.getName());
	
	/**
	 * Validate request data using common validator api
	 *
	 * @param subscriberModel
	 *            The RestrictedSubscriberModel object having data to be processed
	 * @param type type of validation
	 *            
	 * @param response
	 *            PretupsResponse for storing response
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws ValidatorException
	 */
	public void validateRequestDataForViewScheduleTopup(String type, PretupsResponse<?> response, RestrictedSubscriberModel subscriberModel, String referenceName) throws ValidatorException, IOException, SAXException {
		if (_log.isDebugEnabled()) {
			_log.debug("ScheduleTopupValidator - validateRequestData", PretupsI.ENTERED);
		}
		
		WebServiceKeywordCacheVO webServiceKeywordCacheVO = ServiceKeywordCache
				.getWebServiceTypeObject(type);
		String validationXMLpath = webServiceKeywordCacheVO.getValidatorName();
		CommonValidator commonValidator = new CommonValidator(validationXMLpath, subscriberModel, referenceName);
		Map<String, String> errorMessages = commonValidator.validateModel();
		response.setFieldError(errorMessages);
		if (_log.isDebugEnabled()) {
			_log.debug("ScheduleTopupValidator - validateRequestData", PretupsI.EXITED);
		}

	}
	
	/**
	 * Validate business rules for View Schedule Topup
	 *
	 * 
	 * @param userVO
	 *            UserVO object
	 * @param connection
	 *            Connection object
	 * @param pretupsResponse
	 *            PretupsResponse for storing response
	 * @return 
	 * @return
	 * @throws BTSLBaseException,
	 *             SQLException
	 */
	@SuppressWarnings("unchecked")
	public  void validateUser( UserVO userVO, Connection connection, PretupsResponse<?> pretupsResponse) throws SQLException, BTSLBaseException {

		try {
			if (_log.isDebugEnabled()) {
				_log.debug("ScheduleTopupValidator : validateUser", "Entered ");
			}
			if(userVO==null){
				pretupsResponse.setFormError("invalid.login.id");
				pretupsResponse.setMessageCode(PretupsErrorCodesI.INVALID_LOGIN_ID);
				return;
			}
			
							
		} finally {
			if (_log.isDebugEnabled()) {
				_log.debug("ScheduleTopupValidator : validateUser", "Exiting");
			}
		}
		

	}
	
	/**
	 * Validate business rules for Schedule Topup
	 *
	 * @param msisdn
	 * 			msisdn which is entered to fetch scheduled list
	 * @param userVO
	 *            UserVO object
	 * @param connection
	 *            Connection object
	 * @param pretupsResponse
	 *            PretupsResponse for storing response
	 * @return 
	 * @return
	 * @throws BTSLBaseException,
	 *             SQLException
	 */
	public void validateMsisdn(String msisdn, UserVO userVO,  PretupsResponse<?> response) {
		
		final String METHOD_NAME = "validateMsisdn";
		String filteredMsisdn = null;
        try {
        	
            filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(msisdn);
        
	        // This is done because this field can contains msisdn or account id
	        if (!BTSLUtil.isValidIdentificationNumber(filteredMsisdn)) {
	        	response.setFormError("restrictedsubs.viewsingletrfschedule.msg.invalidmsisdn");
	        }
	        // check prefix of the MSISDN
	        String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn); // get the prefix of the MSISDN
	        NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
	        if (networkPrefixVO == null) {
	        	response.setFormError("restrictedsubs.viewsingletrfschedule.msg.nonetworkprefix");
				response.setMessageCode(PretupsErrorCodesI.SUBSCRIBER_SCHEDULE_USER_UNSUPPORTED_NETWORK);
				return;
	        }
	        // check network support of the MSISDN
	        if (!networkPrefixVO.getNetworkCode().equalsIgnoreCase(userVO.getNetworkID())) {
	        	response.setFormError("restrictedsubs.viewsingletrfschedule.msg.nonetworksupportmsisdn");
				response.setMessageCode(PretupsErrorCodesI.MSISDN_FROM_OTHER_NETWORK);
				return;
	        }
	        } catch (Exception e) {
				_log.errorTrace(METHOD_NAME, e);
			}
		
		finally {
			if (_log.isDebugEnabled()) {
				_log.debug("ScheduleTopupValidator : validateMsisdn", "Exiting");
			}
		}
	}
	
	/**
	 * Validate Batch Schedule Recharge Model
	 * @param type
	 * @param restrictedSubscriberModel
	 * @param referenceName
	 * @return
	 * @throws BTSLBaseException
	 */
	public Map<String, String> validateRequestDataForBatchSchedule(String validationXMLpath, RestrictedSubscriberModel restrictedSubscriberModel, String referenceName) throws BTSLBaseException {
		
		String methodName = "validateRequestDataForBatchSchedule";
		try{
			LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
			CommonValidator commonValidator = new CommonValidator(validationXMLpath, restrictedSubscriberModel, referenceName);
			return commonValidator.validateModel();
			
		}catch(ValidatorException | IOException | SAXException e){
			throw new BTSLBaseException(e);
		}finally{
			LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		}
		

	}
}

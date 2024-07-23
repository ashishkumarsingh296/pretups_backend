package com.btsl.pretups.restrictedsubs.businesslogic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.apache.commons.validator.ValidatorException;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonValidator;
import com.btsl.common.PretupsResponse;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.WebServiceKeywordCacheVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.restrictedsubs.web.RestrictedSubscriberModel;

/**
 * Validate data for all Schedule Topup modules
 * 
 * @author lalit.chattar
 *
 */
public class ScheduleTopupValidator {

	public static final Log _log = LogFactory
			.getLog(ScheduleTopupValidator.class.getName());

	/**
	 * Validate Batch Schedule Recharge Model
	 * 
	 * @param validationXMLpath
	 * @param restrictedSubscriberModel
	 * @param referenceName
	 * @return
	 * @throws BTSLBaseException
	 */
	public Map<String, String> validateRequestDataForBatchSchedule(
			String validationXMLpath,
			RestrictedSubscriberModel restrictedSubscriberModel,
			String referenceName) throws BTSLBaseException {

		String methodName = "validateRequestData";
		try {
			LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
			CommonValidator commonValidator = new CommonValidator(
					validationXMLpath, restrictedSubscriberModel, referenceName);
			return commonValidator.validateModel();

		} catch (ValidatorException | IOException | SAXException e) {
			throw new BTSLBaseException(e);
		} finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		}

	}

	/**
	 * Validate request data using common validator api
	 *
	 * @param subscriberModel
	 *            The RestrictedSubscriberModel object having data to be
	 *            processed
	 * @param type
	 *            type of validation
	 * @param referenceName
	 *            reference name for searching in web_service_type table
	 * @param response
	 *            PretupsResponse for storing response
	 * @return
	 * @throws BTSLBaseException
	 */
	public void validateRequestData(String type, PretupsResponse<?> response, RestrictedSubscriberModel subscriberModel, String referenceName)
			throws BTSLBaseException {
		String methodName = "validateRequestData";
		try {
			LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
			WebServiceKeywordCacheVO webServiceKeywordCacheVO = ServiceKeywordCache
					.getWebServiceTypeObject(type);
			String validationXMLpath = webServiceKeywordCacheVO
					.getValidatorName();
			CommonValidator commonValidator = new CommonValidator(
					validationXMLpath, subscriberModel, referenceName);
			Map<String, String> errorMessages = commonValidator.validateModel();
			response.setFieldError(errorMessages);
		} catch (ValidatorException | IOException | SAXException e) {
			throw new BTSLBaseException(e);
		} finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		}

	}

	/**
	 * Validate user details for View Schedule Topup
	 * 
	 * @param sessionUserVO
	 *            UserVO object
	 * @param connection
	 *            Connection object
	 * @param response
	 *            PretupsResponse for storing response
	 * @param restSubsModel
	 *            RestrictedSubscriberModel object
	 * @return void
	 */
	public void validateUserDetails(PretupsResponse<?> response,
			UserVO sessionUserVO, RestrictedSubscriberModel restSubsModel) {
		final String methodName = "validateUserDetails";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		try {
			if (sessionUserVO != null) {
				
				if (!validateGeographicalDomain(sessionUserVO, restSubsModel)) {
					response.setFieldError("geoDomainCode", "invalid.geographical.domain.code");
					return;
				}

				// validating domain code
				if (sessionUserVO.getDomainID().equals(
						PretupsI.OPERATOR_TYPE_OPT)) {
					response.setFieldError("loginId", "invalid.login.id.for.view.schedule.topup");
					return;
				}
				if (!restSubsModel.getDomainCode().equals(
						sessionUserVO.getDomainID())) {
					response.setFieldError("domainCode", "invalid.domain.code");
					return;
				}

				
				if (restSubsModel.getCategoryCode().equals(
						sessionUserVO.getCategoryCode())) {
					restSubsModel.setUserID(sessionUserVO.getUserID());
					restSubsModel.setOwnerID(sessionUserVO.getOwnerID());
					restSubsModel.setOwnerName(sessionUserVO.getOwnerName());
					restSubsModel.setOwnerCategoryName(sessionUserVO
							.getOwnerCategoryName());
					restSubsModel.setCategoryName(sessionUserVO.getCategoryVO()
							.getCategoryName());
				} else {
					response.setFieldError("categoryCode", "invalid.category.code");
					return;
				}

			} else {
				response.setFieldError("loginId", "invalid.login.id");
				return;
			}
		} finally {
			LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		}
	}
	
	/**
	 * Validate Geo-Graphical Domain
	 * @param sessionUserVO
	 * @param restSubsModel
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Boolean validateGeographicalDomain(UserVO sessionUserVO, RestrictedSubscriberModel restSubsModel){
		ArrayList<UserGeographiesVO> geographicalAreaList = sessionUserVO.getGeographicalAreaList();
		Boolean flag = false;
		for (UserGeographiesVO userGeographiesVO : geographicalAreaList) {
			if (userGeographiesVO.getGraphDomainCode().equals(
					restSubsModel.getGeoDomainCode())) {
				restSubsModel.setGeoDomainName(userGeographiesVO
						.getGraphDomainName());
				flag = true;
				break;
			}
		}
		
		return flag;
	}
	
	
	

	/**
	 * Validate business rules for Schedule Topup
	 *
	 * @param msisdn
	 *            msisdn which is entered to fetch scheduled list
	 * @param userVO
	 *            UserVO object
	 * @param response
	 *            PretupsResponse for storing response
	 * @return
	 */
	public void validateMsisdn(String msisdn, UserVO userVO,
			PretupsResponse<?> response) {

		String methodName = "validateMsisdn";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		String filteredMsisdn = null;
		try {

			filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(msisdn);

			// This is done because this field can contains msisdn or account id
			if (!BTSLUtil.isValidIdentificationNumber(filteredMsisdn)) {
				response.setFieldError("msisdn","restrictedsubs.viewsingletrfschedule.msg.invalidmsisdn");
			}
			// check prefix of the MSISDN
			String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn); // get
																				// the
																				// prefix
																				// of
																				// the
																				// MSISDN
			NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache
					.getObject(msisdnPrefix);
			if (networkPrefixVO == null) {
				response.setFieldError("msisdn", "restrictedsubs.viewsingletrfschedule.msg.nonetworkprefix");
				return;
			}
			// check network support of the MSISDN
			if (!networkPrefixVO.getNetworkCode().equalsIgnoreCase(
					userVO.getNetworkID())) {
				response.setFieldError("msisdn", "restrictedsubs.viewsingletrfschedule.msg.nonetworksupportmsisdn");
				return;
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}

		finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		}
	}


	/**
	 * Validate business rules for Schedule Topup
	 *
	 * @param fromScheduleDate
	 * 			fromScheduleDate which is entered to fetch scheduled list
	 * @param toScheduleDate
	 *            toScheduleDate which is entered to fetch scheduled list
	 * @param response
	 *            PretupsResponse for storing response
	 * @return
	 */
	public void validateDateDifference(Date fromScheduleDate,Date toScheduleDate ,  PretupsResponse<?> response) {
		
		final String methodName = "validateDateDifference";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
        try {
        	int  maxDays = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_DATEDIFF))).intValue();
        	int diff=BTSLUtil.getDifferenceInUtilDates(fromScheduleDate, toScheduleDate);
        	String arr[]={String.valueOf(maxDays)};
        	 if (!fromScheduleDate.after(toScheduleDate)) {
                 if (diff > maxDays || diff < 0) {
                    response.setFormError("btsl.date.error.datecompare",arr);
                 	return;
                 }
             } else {
                 response.setFormError("btsl.error.msg.fromdatebeforetodate");
                 return;
             }
        	
	        } catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		
		finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		}
	}

	
	

	
}

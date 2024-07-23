package com.btsl.pretups.restrictedsubs.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestClient;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchDetailVO;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;
import com.btsl.pretups.scheduletopup.web.ScheduleTopupValidator;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.web.pretups.restrictedsubs.web.RestrictedSubscriberModel;

/**
 * This class implements RestrictedSubscriberService and define method for
 * Schedule Recharge
 * 
 * @author lalit.chattar
 *
 */

@Service("restrictedSubscriberService")
public class RestrictedSubscriberServiceImpl implements
		RestrictedSubscriberService {

	@Autowired
	private PretupsRestClient pretupsRestClient;

	private static final Log log = LogFactory
			.getLog(RestrictedSubscriberServiceImpl.class.getName());

	private static final String NO_DOMAIN_MESSAGE = "restrictedsubs.commonjsp.error.msg.nodomain";
	private static final String CLASS_NAME = "RestrictedSubscriberServiceImpl";
	@Autowired
	private static final String LOGIN_ID = "loginId";
	private static final String MSISDN = "msisdn";
	private static final String LOOK_UP_TYPE = "lookupType";

	/**
	 * 
	 * @param userVO
	 * @param model
	 * @param restrictedSubscriberModel
	 * @throws BTSLBaseException
	 */
	@Override
	public void loadloadBatchRechargeFormData(UserVO userVO,
			RestrictedSubscriberModel restrictedSubscriberModel,
			HttpServletRequest request, Model model) throws BTSLBaseException {
		final String methodName = "loadloadBatchRechargeFormData";

		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		try {
			checkIfRestrictedMsisdnNotAllowed(userVO, model);
			if (model.containsAttribute("fail")) {
				return;
			}
			loadUserInformationByUserType(userVO, restrictedSubscriberModel);
			loadGeographyDomainList(userVO, restrictedSubscriberModel);
			loadDomainList(userVO, restrictedSubscriberModel);
			checkForScheduledAndRestricted(userVO, request,
					restrictedSubscriberModel);
			loadCategory(userVO, restrictedSubscriberModel, model);
			loadservices(userVO, restrictedSubscriberModel, model);
		} catch (BTSLBaseException e) {
			throw e;
		} finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}

	/**
	 * This method loads services
	 * 
	 * @param userVO
	 * @param restrictedSubscriberModel
	 * @param model
	 * @throws BTSLBaseException
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void loadViewScheduleRechargeFormData(UserVO userVO,
			RestrictedSubscriberModel restrictedSubscriberModel,
			HttpServletRequest request, Model model) throws BTSLBaseException {
		final String methodName = " - loadViewScheduleRechargeFormData";

		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
		}
		try {
			checkIfRestrictedMsisdnNotAllowed(userVO, model);
			if (model.containsAttribute("fail")) {
				return;
			}
			loadUserInformationByUserType(userVO, restrictedSubscriberModel);
			loadGeographyDomainList(userVO, restrictedSubscriberModel);
			loadDomainList(userVO, restrictedSubscriberModel);
			checkForScheduledAndRestricted(userVO, request,
					restrictedSubscriberModel);
			loadCategory(userVO, restrictedSubscriberModel, model);

		} catch (BTSLBaseException e) {
			throw e;
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME + methodName, PretupsI.EXITED);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Boolean viewScheduleList(RestrictedSubscriberModel subscriberModel,
			UserVO userVO, Model model, BindingResult bindingResult)
			throws IOException, RuntimeException {
		final String METHOD_NAME = "viewScheduleList";
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME + METHOD_NAME, PretupsI.ENTERED);
		}

		Map<String, Object> requestObject = new HashMap<>();
		Map<String, String> dataSet = new HashMap<>();

		dataSet.put("geoDomainCode", subscriberModel.getGeoDomainCode());
		dataSet.put("domainCode", subscriberModel.getDomainCode());
		dataSet.put("categoryCode", subscriberModel.getCategoryCode());
		dataSet.put("loginId", userVO.getLoginID());
		dataSet.put("msisdn", subscriberModel.getMsisdn());

		requestObject.put("data", dataSet);
		requestObject.put(LOGIN_ID, userVO.getLoginID());
		requestObject.put(MSISDN, subscriberModel.getMsisdn());
		requestObject.put("type", PretupsI.VIEWSUBSSCHE);

		String responseString = pretupsRestClient.postJSONRequest(
				requestObject, PretupsI.VIEWSUBSSCHE);

		PretupsResponse<JsonNode> pretupsResponse = (PretupsResponse<JsonNode>) PretupsRestUtil
				.convertJSONToObject(responseString,
						new TypeReference<PretupsResponse<JsonNode>>() {
						});

		PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();

		if (!pretupsRestUtil.processFormAndFieldError(pretupsResponse,
				bindingResult)) {
			return false;
		}

		PretupsResponse<List<Object>> responseObject = (PretupsResponse<List<Object>>) PretupsRestUtil
				.convertJSONToObject(responseString,
						new TypeReference<PretupsResponse<List<Object>>>() {
						});
		model.addAttribute("scheduleDetails", responseObject.getDataObject());

		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME + METHOD_NAME, PretupsI.EXITED);
		}
		return true;
	}

	private void loadservices(UserVO userVO,
			RestrictedSubscriberModel restrictedSubscriberModel, Model model)
			throws BTSLBaseException {
		String methodName = "loadservices";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		try {
			Map<String, Object> data = new HashMap<>();
			data.put(LOGIN_ID, userVO.getLoginID());
			Map<String, Object> requestData = new HashMap<>();
			requestData.put(PretupsI.DATA, data);
			String responseString = pretupsRestClient.postJSONRequest(
					requestData, PretupsI.LOADSERVICE);
			JsonNode json = (JsonNode) PretupsRestUtil.convertJSONToObject(
					responseString, new TypeReference<JsonNode>() {
					});
			List<Object> serviceList = (List<Object>) PretupsRestUtil
					.convertJSONToObject(json.get("dataObject").toString(),
							new TypeReference<List<ListValueVO>>() {
							});
			if (serviceList.isEmpty()) {
				model.addAttribute(
						"fail",
						PretupsRestUtil
								.getMessageString("restrictedsubs.rescheduletopupdetails.msg.noservicetype"));
				return;
			}

			if (!serviceList.isEmpty() && serviceList.size() == 1) {
				ListValueVO listValueVO = (ListValueVO) serviceList.get(0);
				ServiceKeywordCacheVO serviceKeywordCacheVO = ServiceKeywordCache
						.getServiceTypeObject(listValueVO.getValue(),
								PretupsI.C2S_MODULE);
				if (BTSLUtil
						.isNullString(serviceKeywordCacheVO.getFileParser())) {
					model.addAttribute(
							"fail",
							PretupsRestUtil
									.getMessageString("restrictedsubs.scheduletopupdetails.msg.sevicenotallowedtoschedule"));
				}
			}
			List<Object> allowedServiceList = new ArrayList<>();
			int i = 0;
			for (Object object : serviceList) {
				ListValueVO listValueVO = (ListValueVO) serviceList.get(i++);
				ServiceKeywordCacheVO serviceKeywordCacheVO = ServiceKeywordCache
						.getServiceTypeObject(listValueVO.getValue(),
								PretupsI.C2S_MODULE);
				if (!BTSLUtil.isNullString(serviceKeywordCacheVO
						.getFileParser())) {
					allowedServiceList.add(listValueVO);
				}
			}
			serviceList = allowedServiceList;
			restrictedSubscriberModel.setServiceList(serviceList);
		} catch (IOException | RuntimeException e) {
			throw new BTSLBaseException(e);
		} finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}

	/**
	 * Loads category if domain is not fixed and not assigned
	 * 
	 * @param userVO
	 * @param restrictedSubscriberModel
	 * @param model
	 * @param catList
	 * @return
	 * @throws BTSLBaseException
	 */
	private List<Object> loadCategoryIfDomainNotFixedNotAssigned(UserVO userVO,
			RestrictedSubscriberModel restrictedSubscriberModel, Model model,
			List<Object> catList) throws BTSLBaseException {

		if (!"Y".equalsIgnoreCase(userVO.getCategoryVO()
				.getScheduledTransferAllowed())
				&& restrictedSubscriberModel.isScheduled()) {
			model.addAttribute(
					"fail",
					PretupsRestUtil
							.getMessageString("restrictedsubs.commonjsp.error.msg.nocategory"));
		}

		if ((PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(userVO
				.getDomainTypeCode()) || restrictedSubscriberModel
				.isRestricted())
				&& !restrictedSubscriberModel.isSelfAllow()) {
			return loadTrfRuleCatListForRestrictedMsisdn(userVO,
					restrictedSubscriberModel);
		}
		return catList;
	}

	/**
	 * Load transfer rules category list for restricted msisdn
	 * 
	 * @param userVO
	 * @param restrictedSubscriberModel
	 * @return
	 * @throws BTSLBaseException
	 */
	@SuppressWarnings("unchecked")
	private List<Object> loadTrfRuleCatListForRestrictedMsisdn(UserVO userVO,
			RestrictedSubscriberModel restrictedSubscriberModel)
			throws BTSLBaseException {
		String methodName = "loadTrfRuleCatListForRestrictedMsisdn";
		LogFactory.printLog(methodName, PretupsI.EXITED, log);
		try {
			Map<String, Object> data = new HashMap<>();
			data.put(LOGIN_ID, userVO.getLoginID());
			data.put(PretupsI.OWNER_ONLY,
					restrictedSubscriberModel.isOwnerOnly());
			data.put(PretupsI.IS_RESTRICTED,
					restrictedSubscriberModel.isRestricted());

			Map<String, Object> requestData = new HashMap<>();
			requestData.put(PretupsI.DATA, data);
			String responseString = pretupsRestClient.postJSONRequest(
					requestData, PretupsI.LOADCATEGORY);
			JsonNode json = (JsonNode) PretupsRestUtil.convertJSONToObject(
					responseString, new TypeReference<JsonNode>() {
					});
			return (List<Object>) PretupsRestUtil.convertJSONToObject(
					json.get("dataObject").toString(),
					new TypeReference<List<ChannelTransferRuleVO>>() {
					});

		} catch (IOException | RuntimeException e) {
			throw new BTSLBaseException(e);
		} finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}

	/**
	 * Load domain Category If Domain Fixed and Assigned
	 * 
	 * @param userVO
	 * @param restrictedSubscriberModel
	 * @param model
	 * @param catList
	 * @return
	 * @throws BTSLBaseException
	 */
	private List<Object> loadCategoryIfDomainFixedAndAssigned(UserVO userVO,
			RestrictedSubscriberModel restrictedSubscriberModel, Model model,
			List<Object> catList) throws BTSLBaseException {
		String methodName = "loadCategoryIfDomainFixedAndAssigned";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		try {
			StringBuilder domainStr = new StringBuilder();
			ListValueVO listValueVO;
			for (int i = 0, j = restrictedSubscriberModel.getDomainList()
					.size(); i < j; i++) {
				domainStr.append("'");
				listValueVO = (ListValueVO) restrictedSubscriberModel
						.getDomainList().get(i);
				domainStr.append(listValueVO.getValue());
				domainStr.append("',");
			}

			if (PretupsI.OPERATOR_CATEGORY.equalsIgnoreCase(userVO
					.getCategoryCode())) {
				List<Object> domainList = loadRestrictedMsisdnsDomainList(
						domainStr.substring(0, (domainStr.length() - 1)),
						restrictedSubscriberModel.isScheduled(),
						userVO.getUserID());
				restrictedSubscriberModel.setDomainList(domainList);
				domainStr = new StringBuilder();
				for (int i = 0, j = domainList.size(); i < j; i++) {
					domainStr.append("'");
					listValueVO = (ListValueVO) domainList.get(i);
					domainStr.append(listValueVO.getValue());
					domainStr.append("',");
				}
			}
			if (BTSLUtil.isNullString(domainStr.toString())) {
				return loadRestrictedCatList(
						domainStr.substring(0, domainStr.length() - 1),
						restrictedSubscriberModel);
			} else {
				model.addAttribute(
						"fail",
						PretupsRestUtil
								.getMessageString("restrictedsubs.commonjsp.error.msg.nocorporatedomain"));
			}
			if (restrictedSubscriberModel.getDomainList().isEmpty()) {
				model.addAttribute("fail",
						PretupsRestUtil.getMessageString(NO_DOMAIN_MESSAGE));
			}
			if (catList == null || catList.isEmpty()) {
				model.addAttribute(
						"fail",
						PretupsRestUtil
								.getMessageString("restrictedsubs.commonjsp.error.msg.nocategory"));
			}

			return catList;
		} catch (BTSLBaseException e) {
			throw e;
		} finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}

	}

	@SuppressWarnings("unchecked")
	private List<Object> loadRestrictedCatList(String domainList,
			RestrictedSubscriberModel restrictedSubscriberModel)
			throws BTSLBaseException {
		String methodName = "loadRestrictedCatList";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		try {
			Map<String, Object> data = new HashMap<>();
			data.put("domainList", domainList);
			data.put(PretupsI.OWNER_ONLY,
					restrictedSubscriberModel.isOwnerOnly());
			data.put(PretupsI.IS_RESTRICTED,
					restrictedSubscriberModel.isRestricted());
			data.put(PretupsI.IS_SCHEDULED,
					restrictedSubscriberModel.isScheduled());

			Map<String, Object> requestData = new HashMap<>();
			requestData.put(PretupsI.DATA, data);
			String responseString = pretupsRestClient.postJSONRequest(
					requestData, PretupsI.LOADCATEGORY);
			JsonNode json = (JsonNode) PretupsRestUtil.convertJSONToObject(
					responseString, new TypeReference<JsonNode>() {
					});
			return (List<Object>) PretupsRestUtil.convertJSONToObject(
					json.get("dataObject").toString(),
					new TypeReference<List<ChannelTransferRuleVO>>() {
					});

		} catch (IOException | RuntimeException e) {
			throw new BTSLBaseException(e);
		} finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}

	/**
	 * Load Restricted Msisdn Domain List
	 * 
	 * @param domainList
	 * @param scheduled
	 * @param userID
	 * @return
	 * @throws BTSLBaseException
	 */
	@SuppressWarnings("unchecked")
	private List<Object> loadRestrictedMsisdnsDomainList(String domainList,
			boolean scheduled, String userID) throws BTSLBaseException {
		final String methodName = "loadRestrictedMsisdnsDomainList";
		try {
			LogFactory.printLog(methodName, PretupsI.ENTERED, log);
			Map<String, Object> data = new HashMap<>();
			data.put("domainList", domainList);
			data.put(PretupsI.IS_SCHEDULED, scheduled);
			data.put(PretupsI.USER_ID, userID);

			Map<String, Object> requestData = new HashMap<>();
			requestData.put(PretupsI.DATA, data);

			String responseString = pretupsRestClient.postJSONRequest(
					requestData, PretupsI.LOADDOMAIN);

			JsonNode json = (JsonNode) PretupsRestUtil.convertJSONToObject(
					responseString, new TypeReference<JsonNode>() {
					});
			return (List<Object>) PretupsRestUtil.convertJSONToObject(
					json.get("dataObject").toString(),
					new TypeReference<List<Object>>() {
					});

		} catch (IOException | RuntimeException e) {
			throw new BTSLBaseException(e);
		} finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}

	/**
	 * Load Category
	 * 
	 * @param userVO
	 * @param restrictedSubscriberModel
	 * @param model
	 * @throws BTSLBaseException
	 */
	private void loadCategory(UserVO userVO,
			RestrictedSubscriberModel restrictedSubscriberModel, Model model)
			throws BTSLBaseException {
		String methodName = "loadCategory";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		try {
			List<Object> catList = new ArrayList<>();
			if (PretupsI.DOMAINS_NOTFIXED_NOTASSIGNED.equals(userVO
					.getCategoryVO().getFixedDomains())) {
				catList = loadCategoryIfDomainNotFixedNotAssigned(userVO,
						restrictedSubscriberModel, model, catList);
			} else {
				if (restrictedSubscriberModel.getDomainList() == null
						|| restrictedSubscriberModel.getDomainList().isEmpty()) {
					model.addAttribute("fail",
							PretupsRestUtil.getMessageString(NO_DOMAIN_MESSAGE));
					return;
				}
				catList = loadCategoryIfDomainFixedAndAssigned(userVO,
						restrictedSubscriberModel, model, catList);
			}

			if (!catList.isEmpty()) {
				restrictedSubscriberModel.setCategoryList(catList);
			}
		} catch (BTSLBaseException e) {
			throw e;
		} finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}

	/**
	 * 
	 * @param userVO
	 * @param request
	 * @param restrictedSubscriberModel
	 */
	private void checkForScheduledAndRestricted(UserVO userVO,
			HttpServletRequest request,
			RestrictedSubscriberModel restrictedSubscriberModel) {
		HttpSession session = request.getSession();
		String moduleCode = request.getParameter("moduleCode");
		if (!"SCHEDULE".equalsIgnoreCase(moduleCode)
				&& !"RESMSISLST".equalsIgnoreCase(moduleCode)) {
			moduleCode = (String) session.getAttribute("moduleCode");
		}
		if ("SCHEDULE".equalsIgnoreCase(moduleCode)) {
			restrictedSubscriberModel.setScheduled(true);
		}

		if (PretupsI.USER_TYPE_CHANNEL.equalsIgnoreCase(userVO.getUserType())) {
			if ("Y".equalsIgnoreCase(userVO.getCategoryVO()
					.getTransferToListOnly())
					&& "Y".equalsIgnoreCase(userVO.getCategoryVO()
							.getRestrictedMsisdns())) {
				restrictedSubscriberModel.setRestricted(true);
				restrictedSubscriberModel.setCorporate(true);
				restrictedSubscriberModel
						.setFileType(PretupsI.BATCH_TYPE_CORPORATE);
			} else if ("N".equalsIgnoreCase(userVO.getCategoryVO()
					.getTransferToListOnly())
					&& "Y".equalsIgnoreCase(userVO.getCategoryVO()
							.getRestrictedMsisdns())) {
				restrictedSubscriberModel.setRestricted(true);
				restrictedSubscriberModel.setSoho(true);
				restrictedSubscriberModel
						.setFileType(PretupsI.BATCH_TYPE_CORPORATE);
			} else {
				restrictedSubscriberModel.setNormal(true);
				restrictedSubscriberModel
						.setFileType(PretupsI.BATCH_TYPE_NORMAL);
			}
		} else if (!restrictedSubscriberModel.isScheduled()) {
			restrictedSubscriberModel.setRestricted(true);
		}
	}

	@SuppressWarnings("unchecked")
	private void loadDomainList(UserVO userVO,
			RestrictedSubscriberModel restrictedSubscriberModel)
			throws BTSLBaseException {
		final String methodName = "loadDomainList";

		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		try {
			if (PretupsI.DOMAINS_NOTFIXED_NOTASSIGNED.equals(userVO
					.getCategoryVO().getFixedDomains())) {
				restrictedSubscriberModel.setDomainCode(userVO.getDomainID());
				restrictedSubscriberModel.setDomainName(userVO.getDomainName());
			} else {
				List<Object> domainList = userVO.getDomainList();
				if ((domainList == null || domainList.isEmpty())
						&& PretupsI.YES.equals(userVO.getCategoryVO()
								.getDomainAllowed())
						&& PretupsI.DOMAINS_FIXED.equals(userVO.getCategoryVO()
								.getFixedDomains())) {
					domainList = loadDomain(userVO);
				}
				domainList = BTSLUtil.displayDomainList(domainList);
				restrictedSubscriberModel.setDomainList(domainList);
			}
		} catch (BTSLBaseException e) {
			throw e;
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
		}

	}

	private void checkIfRestrictedMsisdnNotAllowed(UserVO userVO, Model model) {
		String methodName = "checkIfRestrictedMsisdnNotAllowed";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		if ((PretupsI.DOMAINS_NOTFIXED_NOTASSIGNED.equals(userVO
				.getCategoryVO().getFixedDomains()) && PretupsI.BATCH_TYPE_CORPORATE
				.equalsIgnoreCase(userVO.getDomainTypeCode()))
				&& !PretupsI.YES.equals(userVO.getRestrictedMsisdnAllow())) {
			model.addAttribute("fail",
					PretupsRestUtil.getMessageString(NO_DOMAIN_MESSAGE));
		}
		LogFactory.printLog(methodName, PretupsI.EXITED, log);
	}

	@SuppressWarnings("unchecked")
	private void loadGeographyDomainList(UserVO userVO,
			RestrictedSubscriberModel restrictedSubscriberModel)
			throws BTSLBaseException {

		final String methodName = "loadGeographyDomainList";

		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		try {
			List<UserGeographiesVO> geographicalAreaList = userVO
					.getGeographicalAreaList();
			restrictedSubscriberModel.setGeoDomainList(geographicalAreaList);
		} finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}

	/**
	 * Load user information by type
	 * 
	 * @param userVO
	 * @param restrictedSubscriberModel
	 */
	private void loadUserInformationByUserType(UserVO userVO,
			RestrictedSubscriberModel restrictedSubscriberModel) {
		final String methodName = "loadUserInformationByUserType";

		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		if (PretupsI.USER_TYPE_CHANNEL.equalsIgnoreCase(userVO.getUserType())) {
			restrictedSubscriberModel.setSelfAllow(true);
			restrictedSubscriberModel.setLoginUserID(userVO.getUserID());
			restrictedSubscriberModel.setLoginUserType(userVO.getUserType());
			if (userVO.isStaffUser()) {
				restrictedSubscriberModel.setLoginUserName(userVO
						.getParentName());
			} else {
				restrictedSubscriberModel
						.setLoginUserName(userVO.getUserName());
			}
			restrictedSubscriberModel.setLoginUserCatCode(userVO
					.getCategoryCode());
			restrictedSubscriberModel.setLoginUserCatName(userVO
					.getCategoryVO().getCategoryName());
			restrictedSubscriberModel.setLoginId(userVO.getLoginID());
		}
		restrictedSubscriberModel.setUserName(userVO.getUserName());
		LogFactory.printLog(methodName, PretupsI.EXITED, log);
	}

	/**
	 * This method loads geography
	 * 
	 * @param userVO
	 *            UserVO object
	 * @return geographyList List of UserGeographiesVO
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserGeographiesVO> loadGeography(UserVO userVO)
			throws BTSLBaseException {
		final String methodName = "loadGeography";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		try {

			Map<String, String> data = new HashMap<>();
			data.put(PretupsI.LOGIN_ID, userVO.getLoginID());
			data.put(PretupsI.NETWORK_CODE, userVO.getNetworkID());

			Map<String, Object> requestData = new HashMap<>();
			requestData.put(PretupsI.DATA, data);

			String responseString = pretupsRestClient.postJSONRequest(
					requestData, PretupsI.LOADGEODOMAIN);

			JsonNode json = (JsonNode) PretupsRestUtil.convertJSONToObject(
					responseString, new TypeReference<JsonNode>() {
					});
			return (List<UserGeographiesVO>) PretupsRestUtil
					.convertJSONToObject(json.get("dataObject").toString(),
							new TypeReference<List<UserGeographiesVO>>() {
							});

		} catch (IOException | RuntimeException e) {
			throw new BTSLBaseException(e);
		} finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}

	/**
	 * Load Domain list
	 * 
	 * @param userVO
	 * @throws BTSLBaseException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Object> loadDomain(UserVO userVO) throws BTSLBaseException {
		final String methodName = "loadDomain";
		try {
			LogFactory.printLog(methodName, PretupsI.ENTERED, log);
			Map<String, String> data = new HashMap<>();
			data.put(PretupsI.BY_USER_TYPE, PretupsI.BY_OPT_USER_TYPE);

			Map<String, Object> requestData = new HashMap<>();
			requestData.put(PretupsI.DATA, data);

			String responseString = pretupsRestClient.postJSONRequest(
					requestData, PretupsI.LOADDOMAIN);

			JsonNode json = (JsonNode) PretupsRestUtil.convertJSONToObject(
					responseString, new TypeReference<JsonNode>() {
					});
			return (List<Object>) PretupsRestUtil.convertJSONToObject(
					json.get("dataObject").toString(),
					new TypeReference<List<Object>>() {
					});

		} catch (IOException | RuntimeException e) {
			throw new BTSLBaseException(e);
		} finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getFileLocationForTemplate(
			RestrictedSubscriberModel restrictedSubscriberModel, String fileType)
			throws BTSLBaseException {
		final String methodName = "loadDomain";
		try {
			LogFactory.printLog(methodName, PretupsI.ENTERED, log);
			Map<String, String> data = new HashMap<>();
			data.put(PretupsI.SERVICE_CODE,
					restrictedSubscriberModel.getServiceCode());
			data.put(PretupsI.LOGIN_ID, restrictedSubscriberModel.getLoginId());
			data.put(PretupsI.FILE_TYPE, fileType);

			Map<String, Object> requestData = new HashMap<>();
			requestData.put(PretupsI.DATA, data);
			requestData.put(PretupsI.TYPE, PretupsI.SCTPTEMPL);

			String responseString = pretupsRestClient.postJSONRequest(
					requestData, PretupsI.SCTPTEMPL);

			PretupsResponse<String> response = (PretupsResponse<String>) PretupsRestUtil
					.convertJSONToObject(responseString,
							new TypeReference<PretupsResponse<String>>() {
							});
			return response.getDataObject();

		} catch (IOException | RuntimeException e) {
			throw new BTSLBaseException(e);
		} finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}

	/**
	 * Validate form data
	 * 
	 * @param bindingResult
	 * @param restrictedSubscriberModel
	 * @throws BTSLBaseException
	 */
	@Override
	public void validateRequestDataForBatchRecharge(
			BindingResult bindingResult,
			RestrictedSubscriberModel restrictedSubscriberModel)
			throws BTSLBaseException {
		String methodName = "validateRequestDataForBatchRecharge";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		try {
			ScheduleTopupValidator scheduleTopupValidator = new ScheduleTopupValidator();
			Map<String, String> errorMap = scheduleTopupValidator
					.validateRequestDataForBatchSchedule(
							"configfiles/restrictedsubs/restricted-subs-validator.xml",
							restrictedSubscriberModel, "BatchScheduleTopUp");
			PretupsResponse<Object> pretupsResponse = new PretupsResponse<>();
			pretupsResponse.setFieldError(errorMap);
			if (!errorMap.isEmpty()) {
				PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
				pretupsRestUtil.processFormAndFieldError(pretupsResponse,
						bindingResult);
			}
		} catch (BTSLBaseException e) {
			throw e;
		} finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	/**
	 * Upload file and send request on rest for processing uploaded file
	 * @param restrictedSubscriberModel
	 * @param bindingResult
	 * @param request
	 * @param model
	 * @thorws BTSLBaseException
	 */
	public Boolean processUploadedFile(
			RestrictedSubscriberModel restrictedSubscriberModel,
			BindingResult bindingResult, HttpServletRequest request, Model model)
			throws BTSLBaseException {
		String methodName = "processUploadedFile";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		try {
			MultipartFile multipartFile = restrictedSubscriberModel
					.getMultipartFile();
			File file = PretupsRestUtil.getFile(
					multipartFile.getOriginalFilename(),
					"UploadRestrictedMSISDNFilePath");
			PretupsRestUtil.checkForLocation(file.getParent());
			if (file.exists()) {
				model.addAttribute(
						"errorMessage",
						PretupsRestUtil
								.getMessageString(
										"restrictedsubs.scheduletopupdetails.file.already.exists",
										new String[] { file.getName() }));
				return false;
			}
			FileCopyUtils.copy(restrictedSubscriberModel.getMultipartFile()
					.getBytes(), file);

			UserVO userVO = (UserVO) request.getSession().getAttribute("user");

			RestrictedSubscriberModel modelObj = (RestrictedSubscriberModel) request
					.getSession().getAttribute(
							"restrictedSubscriberModelObject");
			restrictedSubscriberModel.setGeoDomainCode(modelObj
					.getGeoDomainCode());
			restrictedSubscriberModel.setUploadedFileLocation(file
					.getAbsolutePath());
			restrictedSubscriberModel.setServiceCode(modelObj.getServiceCode());
			restrictedSubscriberModel.setDomainCode(modelObj.getDomainCode());
			restrictedSubscriberModel.setCategoryCode(modelObj
					.getCategoryCode());
			restrictedSubscriberModel.setFileType(modelObj.getFileType());
			restrictedSubscriberModel.setRequestFor("schedule");
			restrictedSubscriberModel.setLoginId(modelObj.getLoginId());

			Map<String, Object> data = new HashMap<>();
			data.put(PretupsI.DATA, restrictedSubscriberModel);
			data.put(PretupsI.LOGIN_ID, userVO.getLoginID());
			data.put(PretupsI.TYPE, PretupsI.SCHTOUPFLUP);
			PretupsRestClient client = new PretupsRestClient();
			String response = client
					.postJSONRequest(data, PretupsI.SCHTOUPFLUP);

			PretupsResponse<Object> pretupsResponse = (PretupsResponse<Object>) PretupsRestUtil
					.convertJSONToObject(response,
							new TypeReference<PretupsResponse<Object>>() {
							});
			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			pretupsRestUtil.processFormAndFieldError(pretupsResponse,
					bindingResult);

			if (pretupsResponse.hasFormError()
					&& pretupsResponse.getDataObject() == null) {
				model.addAttribute(
						"errorMessage",
						PretupsRestUtil.getMessageString(
								pretupsResponse.getFormError(),
								pretupsResponse.getParameters()));
				return false;
			} else if (pretupsResponse.hasFormError()
					&& pretupsResponse.getDataObject() != null
					&& pretupsResponse.getParameters() == null) {
				model.addAttribute("errorMessage", PretupsRestUtil
						.getMessageString(pretupsResponse.getFormError()));
				model.addAttribute("fileLocation",
						BTSLUtil.encryptText(pretupsResponse.getDataObject()
								.toString()));
				return false;
			} else if (pretupsResponse.hasFormError()
					&& pretupsResponse.getParameters() != null) {
				model.addAttribute(
						"errorMessage",
						PretupsRestUtil.getMessageString(
								pretupsResponse.getFormError(),
								pretupsResponse.getParameters()));
				model.addAttribute("fileLocation",
						BTSLUtil.encryptText(pretupsResponse.getDataObject()
								.toString()));
				return false;
			} else {
				model.addAttribute("successMessage", PretupsRestUtil
						.getMessageString(pretupsResponse.getSuccessMsg(),
								pretupsResponse.getParameters()));
				return true;
			}
		} catch (IOException | RuntimeException e) {
			throw new BTSLBaseException(e);
		} finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}

	/**
	 * load frequency for batch schedule recharge
	 * 
	 * @param userType
	 * @return
	 * @throws BTSLBaseException
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<ListValueVO> loadFrequency() throws BTSLBaseException {
		String methodName = "loadFrequency";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		List<ListValueVO> list = null;
		try {
			Map<String, Object> data = new HashMap<>();
			data.put(PretupsI.LOOK_UP_TYPE, PretupsI.FREQUENCY);
			data.put("active", true);
			Map<String, Object> object = new HashMap<>();
			object.put("data", data);
			String responseString = pretupsRestClient.postJSONRequest(object,
					PretupsI.LOOKUP);
			PretupsResponse<List<ListValueVO>> response = (PretupsResponse<List<ListValueVO>>) PretupsRestUtil
					.convertJSONToObject(
							responseString,
							new TypeReference<PretupsResponse<List<ListValueVO>>>() {
							});
			list = response.getDataObject();

		} catch (IOException e) {
			throw new BTSLBaseException(e);
		} finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}

		return list;
	}

	@Override
	public void updateCancelInfo(UserVO userVO,
			RestrictedSubscriberModel restrictedSubscriberModel,
			HttpServletRequest request, Model model) throws BTSLBaseException {
		final String methodName = CLASS_NAME + "#loadloadBatchRechargeFormData";

		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
		}
		try {
			checkIfRestrictedMsisdnNotAllowed(userVO, model);
			if (model.containsAttribute("fail")) {
				return;
			}
			loadUserInformationByUserType(userVO, restrictedSubscriberModel);
			loadGeographyDomainList(userVO, restrictedSubscriberModel);
			loadDomainList(userVO, restrictedSubscriberModel);
			checkForScheduledAndRestricted(userVO, request,
					restrictedSubscriberModel);
			loadCategory(userVO, restrictedSubscriberModel, model);
		} catch (BTSLBaseException e) {
			throw e;
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME + methodName, PretupsI.EXITED);
			}
		}

	}

	@Override
	public void loadBatchDetailsforSingle(
			RestrictedSubscriberModel restrictedSubscriberModel,
			BindingResult bindingresult, UserVO userVO, Model model)
			throws IOException, BTSLBaseException {

		ArrayList<ScheduleBatchMasterVO> SchedulebatchList = new ArrayList<ScheduleBatchMasterVO>();
          String[] arr =new String[1];
		// validateSelectedUser(model,restrictedSubscriberModel,userVO) ;
		if (model.containsAttribute("fail")) {
			return;
		} else {
			restrictedSubscriberModel.setScheduleFromDate(null);
			restrictedSubscriberModel.setScheduleToDate(null);
			restrictedSubscriberModel
					.setScheduleStatus(PretupsI.SCHEDULE_STATUS_SCHEDULED);
			processviewScheduleRCBatchForm(restrictedSubscriberModel,
					bindingresult, model, userVO, SchedulebatchList);
			if (SchedulebatchList.isEmpty()) {
				arr[0]=restrictedSubscriberModel.getLoginId();
				model.addAttribute("fail",
						PretupsRestUtil.getMessageString("restrictedsubs.error.batchnotexist", arr));
			}
			restrictedSubscriberModel.setScheduleList(SchedulebatchList);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public RestrictedSubscriberModel viewCancelledScheduleSubscriber(
			RestrictedSubscriberModel restrictedSubscriberModel, String bID,
			UserVO userVO, Model model) throws IOException {
		Map<String, Object> data = new HashMap<>();
		data.put("loginId", restrictedSubscriberModel.getLoginId());
		data.put("batchID", bID);
		Map<String, Object> object = new HashMap<>();
		object.put("data", data);
		object.put("type", PretupsI.VIEWCANCEL);
		pretupsRestClient = new PretupsRestClient();
		String responseString = pretupsRestClient.postJSONRequest(object,
				PretupsI.VIEWCANCEL);
		PretupsResponse<RestrictedSubscriberModel> response = (PretupsResponse<RestrictedSubscriberModel>) PretupsRestUtil
				.convertJSONToObject(
						responseString,
						new TypeReference<PretupsResponse<RestrictedSubscriberModel>>() {
						});
		RestrictedSubscriberModel Cancelschedule = response.getDataObject();
		return Cancelschedule;
	}

	@Override
	public void loadDetailsForSingle(
			RestrictedSubscriberModel restrictedSubscriberModel, UserVO userVO,
			Model model) throws IOException {
		RestrictedSubscriberModel restrictedSubscriberModeldetails;
		restrictedSubscriberModeldetails = loadDetailsForSingleRest(
				restrictedSubscriberModel, userVO, model);
		if (model.containsAttribute("fail")) {
			return;
		} else {
			model.addAttribute("restrictedSubscriberModeldetails",
					restrictedSubscriberModeldetails);
			return;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public RestrictedSubscriberModel loadDetailsForSingleRest(
			RestrictedSubscriberModel restrictedSubscriberModel, UserVO userVO,
			Model model) throws IOException {
		Map<String, Object> data = new HashMap<>();
		data.put("loginId", restrictedSubscriberModel.getLoginId());
		data.put("batchID", restrictedSubscriberModel.getBatchID());
		data.put("mobileNumbers", restrictedSubscriberModel.getMobileNumbers());
		Map<String, Object> object = new HashMap<>();
		object.put("data", data);
		object.put("type", PretupsI.VIEWCANCELMSISDN);
		pretupsRestClient = new PretupsRestClient();
		String responseString = pretupsRestClient.postJSONRequest(object,
				PretupsI.VIEWCANCELMSISDN);
		PretupsResponse<RestrictedSubscriberModel> response = (PretupsResponse<RestrictedSubscriberModel>) PretupsRestUtil
				.convertJSONToObject(
						responseString,
						new TypeReference<PretupsResponse<RestrictedSubscriberModel>>() {
						});
		RestrictedSubscriberModel Cancelschedule = response.getDataObject();
		if (!response.getStatusCode().equals(PretupsI.RESPONSE_SUCCESS)) {
			model.addAttribute("fail", PretupsRestUtil.getMessageString(
					response.getFormError(), response.getParameters()));
			return Cancelschedule;
		}
		return Cancelschedule;

	}

	@Override
	public void loadDetailsForSelected(
			RestrictedSubscriberModel restrictedSubscriberModel,
			List<String> checklist, UserVO userVO, Model model)
			throws IOException {

		if (checklist.isEmpty()) {
			model.addAttribute("fail", restrictedSubscriberModel);
			return;
		}

		ArrayList deleteList = new ArrayList();
		int endIndex = restrictedSubscriberModel.getScheduleMasterVOList()
				.size();
		List<ScheduleBatchDetailVO> scheduledMasterVOList = restrictedSubscriberModel
				.getScheduleMasterVOList();
		// find the selected records and add into delete list

		for (int i = 0; i < endIndex; i++) {
			for (int j = 0; j < checklist.size(); j++) {
				if (scheduledMasterVOList.get(i).getMsisdn()
						.equals(checklist.get(j))) {
					scheduledMasterVOList.get(i).setCheckBoxVal("D");
					break;
				}
			}
		}

		for (int index = 0; index < endIndex; index++) {
			if (((scheduledMasterVOList.get(index)).getCheckBoxVal() != null)
					&& "D".equals((scheduledMasterVOList.get(index))
							.getCheckBoxVal())) {
				deleteList.add(scheduledMasterVOList.get(index));
			}
		}
		// show message on the confirm screen if all msisdn of batch are
		// selected to be cancelled
		ScheduleBatchMasterVO scheduleBatchMasterVO;
		int restrictedScheduleListSize = restrictedSubscriberModel.getScheduleListSize();
		for (int i = 0; i < restrictedScheduleListSize; i++) {
			scheduleBatchMasterVO = restrictedSubscriberModel.getScheduleList()
					.get(i);
			if (scheduleBatchMasterVO.getBatchID().equals(
					restrictedSubscriberModel.getBatchID())) {
				restrictedSubscriberModel
						.setScheduleBatchMasterVO(scheduleBatchMasterVO);
				break;
			}
		}
		if (restrictedSubscriberModel.getScheduleBatchMasterVO()
				.getNoOfRecords() == (restrictedSubscriberModel
				.getScheduleBatchMasterVO().getCancelledCount() + endIndex)) {
			model.addAttribute("success",
					"restrictedsubs.displaydetailsforcancelsinglesub.allmsisdncancelled");
		}
		// if delete list is null gives error otherwise go to confirm
		// page
		if (deleteList != null && !deleteList.isEmpty()) {
			restrictedSubscriberModel.setDeleteList(deleteList);
			if (!model.containsAttribute("success")) {
				model.addAttribute("success", "success");
			}
			return;
		}
		model.addAttribute("fail",
				"restrictedsubs.displaydetailsforcancelsinglesub.msg.recordselected");

		return;
	}

	@Override
	public RestrictedSubscriberModel deleteDetailsForSelected(
			RestrictedSubscriberModel restrictedSubscriberModel, UserVO userVO,
			Model model) throws IOException {

		Map<String, Object> data = new HashMap<>();
		data.put("loginId", restrictedSubscriberModel.getLoginId());
		data.put("batchID", restrictedSubscriberModel.getBatchID());
		ArrayList<String> deleteList = new ArrayList();
        int   restrictedDeleteListSize = restrictedSubscriberModel.getDeleteList().size();
		for (int i = 0; i < restrictedDeleteListSize; i++) {
			deleteList.add(restrictedSubscriberModel.getDeleteList().get(i)
					.getMsisdn());
		}

		data.put("checklist", deleteList);

		Map<String, Object> object = new HashMap<>();
		object.put("data", data);
		object.put("type", PretupsI.CANCELMSISDN);
		pretupsRestClient = new PretupsRestClient();
		String responseString = pretupsRestClient.postJSONRequest(object,
				PretupsI.CANCELMSISDN);
		PretupsResponse<RestrictedSubscriberModel> response = (PretupsResponse<RestrictedSubscriberModel>) PretupsRestUtil
				.convertJSONToObject(
						responseString,
						new TypeReference<PretupsResponse<RestrictedSubscriberModel>>() {
						});
		RestrictedSubscriberModel Cancelschedule = response.getDataObject();
		if (!response.getStatusCode().equals(PretupsI.RESPONSE_SUCCESS)) {
			model.addAttribute("fail", response.getFormError());
			return Cancelschedule;
		} else if (response.getStatusCode().equals(PretupsI.RESPONSE_SUCCESS)) {
			model.addAttribute("success",
					"restrictedsubs.displaydetailsforcancelsinglesub.msg.success");
			return Cancelschedule;
		}
		return Cancelschedule;
	}

	/**
	 * Method declaration for loading status
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ListValueVO> loadStatus() throws IOException {
		final String methodName = "loadStatus";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		Map<String, Object> data = new HashMap<>();
		data.put(LOOK_UP_TYPE, PretupsI.SCHEDULE_BATCH_STATUS_LOOKUP_TYPE);
		data.put("active", true);
		Map<String, Object> object = new HashMap<>();
		object.put("data", data);
		String responseString = pretupsRestClient.postJSONRequest(object,
				PretupsI.LOOKUP);
		PretupsResponse<List<ListValueVO>> response = (PretupsResponse<List<ListValueVO>>) PretupsRestUtil
				.convertJSONToObject(
						responseString,
						new TypeReference<PretupsResponse<List<ListValueVO>>>() {
						});
		List<ListValueVO> list = response.getDataObject();
		LogFactory.printLog(methodName, PretupsI.EXITED, log);
		return list;
	}

	/**
	 * Method declaration for viewing the batch list
	 * 
	 * @param restrictedSubscriberModel
	 *            ,model,bindingResult,userVO,
	 * @return boolean
	 * @throws BTSLBaseException
	 */
	@Override
	public Boolean processviewScheduleRCBatchForm(
			RestrictedSubscriberModel restrictedSubscriberModel,
			BindingResult bindingResult, Model model, UserVO userVO,
			List<ScheduleBatchMasterVO> barredUserList)
			throws BTSLBaseException {
		final String methodName = "processviewScheduleRCBatchForm";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		try {
			Map<String, Object> requestObject = new HashMap<>();
			requestObject.put("loginId", userVO.getLoginID());
			requestObject.put("geoDomainCode",
					restrictedSubscriberModel.getGeoDomainCode());
			requestObject.put("domainCode",
					restrictedSubscriberModel.getDomainCode());
			requestObject.put("categoryCode",
					restrictedSubscriberModel.getCategoryCode());
			requestObject.put("statusin", PretupsI.STATUS_IN);
			requestObject.put("scheduleFromDate",
					restrictedSubscriberModel.getScheduleFromDate());
			requestObject.put("scheduleToDate",
					restrictedSubscriberModel.getScheduleToDate());
			String status = null;
			if (restrictedSubscriberModel.getScheduleStatus().equals(
					PretupsI.ALL)) {
				status = "'" + PretupsI.SCHEDULE_STATUS_SCHEDULED + "','"
						+ PretupsI.SCHEDULE_STATUS_CANCELED + "','"
						+ PretupsI.SCHEDULE_STATUS_EXECUTED + "'";
			} else {
				status = "'" + restrictedSubscriberModel.getScheduleStatus()
						+ "'";
			}
			requestObject.put("status", status);
			Map<String, Object> object = new HashMap<>();
			object.put("data", requestObject);
			object.put("type", "VIEWSCHRCBATCH");
			pretupsRestClient = new PretupsRestClient();
			String responseString = pretupsRestClient.postJSONRequest(object,
					"VIEWSCHRCBATCH");

			PretupsResponse<List<ScheduleBatchMasterVO>> pretupsResponse = (PretupsResponse<List<ScheduleBatchMasterVO>>) PretupsRestUtil
					.convertJSONToObject(
							responseString,
							new TypeReference<PretupsResponse<List<ScheduleBatchMasterVO>>>() {
							});

			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			if (!pretupsRestUtil.processFormAndFieldError(pretupsResponse,
					bindingResult)) {
				return false;
			}
			barredUserList.addAll(pretupsResponse.getDataObject());
			model.addAttribute("schList", pretupsResponse.getDataObject());

			return true;
		} catch (IOException | RuntimeException e) {
			throw new BTSLBaseException(e);
		} finally {
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}

	}

	/**
	 * Method declaration for viewing the details of batches
	 * 
	 * @param restrictedSubscriberModel
	 *            ,model,bindingResult,userVO,bid,schList
	 * @return boolean
	 * @throws BTSLBaseException
	 */
	@Override
	public Boolean detailedviewScheduleRCBatchForm(
			RestrictedSubscriberModel restrictedSubscriberModel,
			BindingResult bindingResult, Model model,
			List<ScheduleBatchMasterVO> schList, String bid)
			throws BTSLBaseException {
		final String methodName = "#detailedviewScheduleRCBatchForm";
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
		}
		try {
			String status = "'" + PretupsI.SCHEDULE_STATUS_SCHEDULED + "','"
					+ PretupsI.SCHEDULE_STATUS_CANCELED + "','"
					+ PretupsI.SCHEDULE_STATUS_EXECUTED + "','"
					+ PretupsI.SCHEDULE_STATUS_UNDERPROCESSED + "'";
			boolean isRestricted = false;
			int schListSize = schList.size();
			for (int i = 0; i < schListSize; i++) {
				if (schList.get(i).getBatchID().equals(bid)) {
					restrictedSubscriberModel.setFileType(schList.get(i)
							.getBatchType());
					if (PretupsI.BATCH_TYPE_CORPORATE.equalsIgnoreCase(schList
							.get(i).getBatchType())) {
						isRestricted = true;
						break;
					}
				}
			}
			Map<String, Object> requestObject = new HashMap<>();
			requestObject.put("batchID", bid);
			requestObject.put("statusin", PretupsI.STATUS_IN);
			requestObject.put("status", status);
			Map<String, Object> object = new HashMap<>();
			object.put("data", requestObject);
			String responseString = null;

			if (isRestricted)
				responseString = pretupsRestClient.postJSONRequest(object,
						"VIEWSCHRCBATCH2");
			else
				responseString = pretupsRestClient.postJSONRequest(object,
						"VIEWSCHRCBATCH3");
			PretupsResponse<LinkedHashMap> pretupsResponse = (PretupsResponse<LinkedHashMap>) PretupsRestUtil
					.convertJSONToObject(
							responseString,
							new TypeReference<PretupsResponse<LinkedHashMap>>() {
							});
			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			if (!pretupsRestUtil.processFormAndFieldError(pretupsResponse,
					bindingResult)) {
				return false;
			}

			model.addAttribute("schList", pretupsResponse.getDataObject());

		} catch (Exception e) {
			throw new BTSLBaseException(e);
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME + methodName, PretupsI.EXITED);
			}
		}

		return true;
	}

	@Override
	public void loadScheduleBatchMasterList(UserVO userVO,
			RestrictedSubscriberModel restrictedSubscriberModel,
			HttpServletRequest request, Model model) throws BTSLBaseException {
		final String methodName = CLASS_NAME + "#loadScheduleBatchMasterList";

		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
		}
		try {
			checkIfRestrictedMsisdnNotAllowed(userVO, model);
			if (model.containsAttribute("fail")) {
				return;

			}
			loadUserInformationByUserType(userVO, restrictedSubscriberModel);
			loadGeographyDomainList(userVO, restrictedSubscriberModel);
			loadDomainList(userVO, restrictedSubscriberModel);
			checkForScheduledAndRestricted(userVO, request,
					restrictedSubscriberModel);
			loadCategory(userVO, restrictedSubscriberModel, model);

		} catch (BTSLBaseException e) {
			throw e;
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME + methodName, PretupsI.EXITED);
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public PretupsResponse<List<UserVO>> loadUserDetailsSearch(
			RestrictedSubscriberModel restrictedSubscriberModel,
			String categoryCode, String userName, String geoDomain,
			String domain, UserVO userVO, Model model) throws BTSLBaseException {
		Map<String, Object> requestObject = new HashMap<>();
		try {
			requestObject.put("loginId", userVO.getLoginID());
			requestObject.put("categoryCode", categoryCode);
			requestObject.put("userName", userName);
			requestObject.put("geoDomainCode", geoDomain);
			requestObject.put("domainCode", domain);
			Map<String, Object> object = new HashMap<>();
			object.put("data", requestObject);
			pretupsRestClient = new PretupsRestClient();
			String responseString = pretupsRestClient.postJSONRequest(object,
					"LOADUSERSBATCHRECHARGE");
			
			PretupsResponse<List<UserVO>> response = (PretupsResponse<List<UserVO>>) PretupsRestUtil
					.convertJSONToObject(
							responseString,
							new TypeReference<PretupsResponse<List<UserVO>>>() {
							});
			return response;
		} catch (IOException e) {
			throw new BTSLBaseException(e);
		}

	}

	public UserVO loadUserVO( List<UserVO> userList, String loginID) {
		 for (UserVO userVO : userList) {
			if(loginID.equalsIgnoreCase(userVO.getLoginID())){
				return userVO;
			}
		}
		return null;
		
	}

	public RestrictedSubscriberModel cancelSelectedBatch(
			RestrictedSubscriberModel restrictedSubscriberModel, UserVO userVO,
			Model model) throws IOException {
		
		Map<String, Object> data = new HashMap<>();
		data.put("loginId", restrictedSubscriberModel.getLoginId());
		ArrayList<String> deleteList = new ArrayList();
       
		int restrictedCheckListSize = restrictedSubscriberModel.getChecklist().size(); 
		for (int i = 0; i < restrictedCheckListSize; i++) {
			deleteList.add(restrictedSubscriberModel.getChecklist().get(i));
		}
		String arr[] = new String[1];
		data.put("checklist", deleteList);

		Map<String, Object> object = new HashMap<>();
		object.put("data", data);
		object.put("type", PretupsI.CANCELBATCH);
		pretupsRestClient = new PretupsRestClient();
		String responseString = pretupsRestClient.postJSONRequest(object,
				PretupsI.CANCELBATCH);
		PretupsResponse<RestrictedSubscriberModel> response = (PretupsResponse<RestrictedSubscriberModel>) PretupsRestUtil
				.convertJSONToObject(
						responseString,
						new TypeReference<PretupsResponse<RestrictedSubscriberModel>>() {
						});
		RestrictedSubscriberModel Cancelschedule = response.getDataObject();
		if (!response.getStatusCode().equals(PretupsI.RESPONSE_SUCCESS)) {
			model.addAttribute("fail",  PretupsRestUtil.getMessageString(
					response.getFormError(), response.getParameters()));
			return Cancelschedule;
		} else if (response.getStatusCode().equals(PretupsI.RESPONSE_SUCCESS)) {
			arr[0]=Cancelschedule.getBatchID();
			model.addAttribute("success",
					 PretupsRestUtil.getMessageString("restrictedsubs.web.viewscheduleaction.cancelconfirmschedule.batchid.success.msg",arr));
			return Cancelschedule;
		}
		return Cancelschedule;
	}
	
	
}

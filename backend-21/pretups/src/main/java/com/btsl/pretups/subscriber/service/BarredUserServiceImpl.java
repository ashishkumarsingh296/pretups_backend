package com.btsl.pretups.subscriber.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestClient;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.SubLookUpVO;
import com.btsl.pretups.subscriber.businesslogic.BarredUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class implements BarredUserService and define method for performing 
 * BAR User related operations 
 */
@Service("barredUserService")
public class BarredUserServiceImpl implements BarredUserService {

	public static final Log _log = LogFactory.getLog(BarredUserServiceImpl.class.getName());
	
	@Autowired
	private PretupsRestClient pretupsRestClient;
	private static final String LOOK_UP_TYPE = "lookupType";
	private static final String LOGIN_ID = "loginId";

	/**
	 * Load modules for BAR User
	 * 
	 * @return List The list of lookup filtered from DB
	 * @throws IOException
	 *         
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<ListValueVO> loadModules(String userType) throws IOException {
		if (_log.isDebugEnabled()) {
			_log.debug("BarredUserServiceImpl#loadModules", PretupsI.ENTERED);
		}
		Map<String, Object> data = new HashMap<>();
		data.put(LOOK_UP_TYPE, PretupsI.MODULE_TYPE);
		data.put("active", true);
		Map<String, Object> object = new HashMap<>();
		object.put("data", data);
		String responseString = pretupsRestClient.postJSONRequest(object, PretupsI.LOOKUP);
		PretupsResponse<List<ListValueVO>> response = (PretupsResponse<List<ListValueVO>>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<ListValueVO>>>() {
				});
		List<ListValueVO> list =  response.getDataObject();
		if (PretupsI.CHANNEL_USER_TYPE.equalsIgnoreCase(userType)) {
			ListIterator<ListValueVO> listItr = list.listIterator();
			while(listItr.hasNext()){
			    if(!listItr.next().getValue().equals(PretupsI.C2S_MODULE)){
			    	listItr.remove();
			    }
			}
		}
		if (_log.isDebugEnabled()) {
			_log.debug("BarredUserServiceImpl#loadModules", PretupsI.EXITED);
		}
		return list;
	}

	/**
	 * Load user types for BAR User
	 * 
	 * @return List The list of lookup filtered from DB
	 * @throws IOException
	 *             
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ListValueVO> loadUserType(String userType) throws IOException {
		if (_log.isDebugEnabled()) {
			_log.debug("BarredUserServiceImpl#loadUserType", PretupsI.ENTERED);
		}
		Map<String, Object> data = new HashMap<>();
		data.put(LOOK_UP_TYPE, PretupsI.BARRED_USER_TYPE);
		data.put("active", true);
		Map<String, Object> object = new HashMap<>();
		object.put("data", data);
		String responseString = pretupsRestClient.postJSONRequest(object, PretupsI.LOOKUP);
		PretupsResponse<List<ListValueVO>> response = (PretupsResponse<List<ListValueVO>>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<ListValueVO>>>() {
				});
		List<ListValueVO> list = response.getDataObject();
		if (PretupsI.CHANNEL_USER_TYPE.equalsIgnoreCase(userType)) {
			ListIterator<ListValueVO> listItr = list.listIterator();
			while(listItr.hasNext()){
			    if(!listItr.next().getValue().equals(PretupsI.USER_TYPE_SENDER)){
			    	listItr.remove();
			    }
			}
		}
		if (_log.isDebugEnabled()) {
			_log.debug("BarredUserServiceImpl#loadUserType", PretupsI.EXITED);
		}
		return list;
	}

	/**
	 * Load barring type for BAR User
	 * 
	 * @return List The list of sublookups filtered from DB
	 * @throws IOException
	 *             
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SubLookUpVO> loadBarringType(String module, String userType) throws IOException {
		String methodName = "loadBarringType";
		LogFactory.printLog(methodName, PretupsI.ENTERED, _log);
		
		Map<String, Object> data = new HashMap<>();
		data.put(LOOK_UP_TYPE, PretupsI.BARRING_TYPE);
		Map<String, Object> object = new HashMap<>();
		object.put("data", data);
		String responseString = pretupsRestClient.postJSONRequest(object, PretupsI.SUBLOOKUP);
		PretupsResponse<List<SubLookUpVO>> response = (PretupsResponse<List<SubLookUpVO>>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<SubLookUpVO>>>() {
				});
		List<SubLookUpVO> list =  response.getDataObject();
		List<SubLookUpVO> dropDown = new ArrayList<>();
		if (module.equalsIgnoreCase(PretupsI.P2P_MODULE)) {
			list.forEach(subLookUpVO -> {
				if (subLookUpVO.getLookupCode().equalsIgnoreCase(PretupsI.P2P_BARTYPE_LOOKUP_CODE)) {
					subLookUpVO.setSubLookupCode(PretupsI.P2P_MODULE + ":" + subLookUpVO.getSubLookupCode());
					dropDown.add(subLookUpVO);
				}

			});
		} else {
			list.forEach(subLookUpVO -> {
				if (!subLookUpVO.getLookupCode().equalsIgnoreCase(PretupsI.P2P_BARTYPE_LOOKUP_CODE)) {
					subLookUpVO.setSubLookupCode(PretupsI.C2S_MODULE + ":" + subLookUpVO.getSubLookupCode());
					dropDown.add(subLookUpVO);
				}

			});
		}

		if (PretupsI.CHANNEL_USER_TYPE.equalsIgnoreCase(userType)) {
			ListIterator<SubLookUpVO> iterator = dropDown.listIterator();
			while(iterator.hasNext()){
			    if(!iterator.next().getLookupCode().equals(PretupsI.CHANNLE_USER_BARTYPE_LOOKUP_CODE)){
			    	iterator.remove();
			    }
			}
		}
		LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		
		return dropDown;
	}

	/**
	 * Process Bar User data submitted from web form
	 * 
	 * @param barUser
	 *            BarredUserVO object having all the values for processing bar
	 *            user
	 * @param userVO
	 *            UserVO object
	 * @param bindingResult
	 *            BindingResult object for handling error message
	 * @return boolean true if bar user successful else false
	 * @throws IOException
	 *             
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Boolean addBarUser(BarredUserVO barUser, UserVO userVO, BindingResult bindingResult) throws IOException {
		if (_log.isDebugEnabled()) {
			_log.debug("BarredUserServiceImpl#addBarUser", PretupsI.ENTERED);
		}
		if (!BTSLUtil.isNullString(barUser.getBarredType())) {
			barUser.setBarredType(barUser.getBarredType().split(":")[1]);
		}

		Map<String, Object> requestObject = new HashMap<>();
		requestObject.put("data", barUser);
		requestObject.put(LOGIN_ID, userVO.getLoginID());
		requestObject.put("type", PretupsI.BARUSER);

		String responseString = pretupsRestClient.postJSONRequest(requestObject, PretupsI.BARUSER);

		PretupsResponse<JsonNode> pretupsResponse = (PretupsResponse<JsonNode>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<JsonNode>>() {
				});

		PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
		if (!pretupsRestUtil.processFormAndFieldError(pretupsResponse, bindingResult)) {
			return false;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("BarredUserServiceImpl#addBarUser", PretupsI.EXITED);
		}
		return true;

	}


	/**
	 * Process View Bar List request
	 * @param barUser Object of BarredUserVO
	 * @param userVO Object of UserVO
	 * @param bindingResult Object of BindingResult
	 * @param barredUserList Object of List<BarredUserVO>
	 * @return Boolean
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Boolean viewBarUserList(BarredUserVO barUser, UserVO userVO, BindingResult bindingResult, List<BarredUserVO> barredUserList)
			throws IOException, RuntimeException {
		if (_log.isDebugEnabled()) {
			_log.debug("BarredUserServiceImpl#viewBarUserList", PretupsI.ENTERED);
		}
		if (!BTSLUtil.isNullString(barUser.getBarredType())) {
			barUser.setBarredType(barUser.getBarredType().split(":")[1]);
		}

		Map<String, Object> requestObject = new HashMap<>();
		requestObject.put("data", barUser);
		requestObject.put(LOGIN_ID, userVO.getLoginID());
		requestObject.put("type", PretupsI.VIEWBARUSER);

		String responseString = pretupsRestClient.postJSONRequest(requestObject, PretupsI.VIEWBARUSER);

		PretupsResponse<JsonNode> pretupsResponse = (PretupsResponse<JsonNode>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<JsonNode>>() {
				});

		PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
		
		if (!pretupsRestUtil.processFormAndFieldError(pretupsResponse, bindingResult)) {
			return false;
		}
		
		PretupsResponse<List<BarredUserVO>> responseObject = (PretupsResponse<List<BarredUserVO>>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<BarredUserVO>>>() {
				});
		
		barredUserList.addAll(responseObject.getDataObject());
		
		if (_log.isDebugEnabled()) {
			_log.debug("BarredUserServiceImpl#viewBarUserList", PretupsI.EXITED);
		}
		return true;
	}
	
	
	
	/**
	 * Process UnBar List request
	 * @param barUser Object of BarredUserVO
	 * @param userVO Object of UserVO
	 * @param bindingResult Object of BindingResult
	 * @param barredUserList Object of List<BarredUserVO>
	 * @return Boolean
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Boolean processUnBarUser(BarredUserVO barUser, UserVO userVO, BindingResult bindingResult, List<BarredUserVO> barredUserList)
			throws IOException, RuntimeException {
		if (_log.isDebugEnabled()) {
			_log.debug("BarredUserServiceImpl#processUnBarUser", PretupsI.ENTERED);
		}
		
		Map<String, Object> requestObject = new HashMap<>();
		requestObject.put("data", barUser);
		requestObject.put(LOGIN_ID, userVO.getLoginID());
		requestObject.put("type", PretupsI.UNBARUSER);

		String responseString = pretupsRestClient.postJSONRequest(requestObject, PretupsI.UNBARUSER);

		PretupsResponse<JsonNode> pretupsResponse = (PretupsResponse<JsonNode>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<JsonNode>>() {
				});

		PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
		
		if (!pretupsRestUtil.processFormAndFieldError(pretupsResponse, bindingResult)) {
			return false;
		}
		
		PretupsResponse<List<BarredUserVO>> responseObject = (PretupsResponse<List<BarredUserVO>>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<BarredUserVO>>>() {
				});
		
		barredUserList.addAll(responseObject.getDataObject());
		
		if (_log.isDebugEnabled()) {
			_log.debug("BarredUserServiceImpl#processUnBarUser", PretupsI.EXITED);
		}
		return true;
	}

	
	/**
	 * Process Barred user list to unbar
	 * @param barUser Object of BarredUserVO
	 * @param userVO Object of UserVO
	 * @param bindingResult Object of BindingResult
	 * @return 
	 * @throws RuntimeException 
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Boolean processSelectedBarredUserToUnbar(BarredUserVO barUser, UserVO userVO, BindingResult bindingResult) throws IOException, RuntimeException {
		
		Map<String, Object> requestMap = new HashMap<>();
		
		requestMap.put(LOGIN_ID, userVO.getLoginID());
		requestMap.put("type", PretupsI.CONUNBARUSER);
		
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("module", barUser.getModule());
		dataMap.put("msisdn", barUser.getMsisdn());
		dataMap.put("userType", barUser.getUserType());
		dataMap.put("barredReason", barUser.getBarredReason());
		dataMap.put("barredTypeList", barUser.getBarredTypeList());
		requestMap.put("data", dataMap);
		
		String responseString = pretupsRestClient.postJSONRequest(requestMap, PretupsI.CONUNBARUSER);

		PretupsResponse<JsonNode> pretupsResponse = (PretupsResponse<JsonNode>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<JsonNode>>() {
				});

		PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
		
		if (!pretupsRestUtil.processFormAndFieldError(pretupsResponse, bindingResult)) {
			return false;
		}
		
		return true;
	}
	
}

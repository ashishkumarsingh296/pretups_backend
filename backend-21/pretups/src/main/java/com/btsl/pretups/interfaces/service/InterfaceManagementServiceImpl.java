package com.btsl.pretups.interfaces.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
import com.btsl.pretups.interfaces.businesslogic.InterfaceVO;
import com.btsl.user.businesslogic.UserVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

@Service("interfaceManagementService")
public class InterfaceManagementServiceImpl implements InterfaceManagementService {

	private static final Log _log = LogFactory.getLog(InterfaceManagementServiceImpl.class.getName());
	
	@Autowired
	PretupsRestClient pretupsRestClient;
	
	@Autowired
	PretupsRestUtil pretupsRestUtil;
	
	/**
	 * Load Interface Category form 
	 * @return List of list values
	 * @throws RuntimeException 
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ListValueVO> loadInterfaceCategory() throws IOException, RuntimeException {
		if (_log.isDebugEnabled()) {
			_log.debug("InterfaceManagementServiceImpl#loadInterfaceCategory", "Entered ");
		}
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("lookupType", PretupsI.INTERFACE_CATEGORY);
		data.put("active", true);
		Map<String, Object> object = new HashMap<String, Object>();
		object.put("data", data);
		String responseString = pretupsRestClient.postJSONRequest(object, PretupsI.LOOKUP);
		PretupsResponse<List<ListValueVO>> response = (PretupsResponse<List<ListValueVO>>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<ListValueVO>>>() {
				});
		List<ListValueVO> list = (List<ListValueVO>) response.getDataObject();
		if (_log.isDebugEnabled()) {
			_log.debug("InterfaceManagementServiceImpl#loadInterfaceCategory", "Exiting");
		}
		return list;
	}

	
	/**
	 * Load Interface Category Details 
	 * @return Boolean true/false
	 * @throws RuntimeException 
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Boolean loadInterfaceDetails(InterfaceVO interfaceCategoryVO, UserVO userVO, BindingResult bindingResult, List<InterfaceVO> list) throws IOException, RuntimeException {
		if (_log.isDebugEnabled()) {
			_log.debug("InterfaceManagementServiceImpl#loadInterfaceDetails", "Entered ");
		}
		Map<String, Object> requestData = new HashMap<String, Object>();
		requestData.put("data", interfaceCategoryVO);
		requestData.put("loginId", userVO.getLoginID());
		String responseString = pretupsRestClient.postJSONRequest(requestData, PretupsI.INTERFACE_DETAIL);
		PretupsResponse<List<InterfaceVO>> response = (PretupsResponse<List<InterfaceVO>>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<InterfaceVO>>>() {
				});
		
		if (!pretupsRestUtil.processFormAndFieldError(response, bindingResult)) {
			return false;
		}
		
		list.addAll(response.getDataObject());
		
		if (_log.isDebugEnabled()) {
			_log.debug("Interface Detail List is", list);
		}
		if (_log.isDebugEnabled()) {
			_log.debug("InterfaceManagementServiceImpl#loadInterfaceDetails", "Exiting");
		}
		
		return true;
	}

	/**
	 * Delete interface and associates nodes
	 * @return Boolean true/false
	 * @throws RuntimeException 
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Boolean deleteInterface(String interfaceID, String interfaceTypeID, BindingResult bindingResult, UserVO userVO) throws IOException, RuntimeException {
		if (_log.isDebugEnabled()) {
			_log.debug("InterfaceManagementServiceImpl#deleteInterface", "Exiting");
		}
		
		Map<String, Object> requestData = new HashMap<String, Object>();
		Map<String, String> interfaceDetail = new HashMap<String, String>();
		interfaceDetail.put("interfaceTypeId", interfaceTypeID);
		interfaceDetail.put("interfaceId", interfaceID);
		requestData.put("data", interfaceDetail);
		requestData.put("loginId", userVO.getLoginID());
		
		String responseString = pretupsRestClient.postJSONRequest(requestData, PretupsI.DELETE_INTERFACE);
		PretupsResponse<JsonNode> response = (PretupsResponse<JsonNode>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<JsonNode>>() {
				});
		
		if (!pretupsRestUtil.processFormAndFieldError(response, bindingResult)) {
			return false;
		}
		
		if (_log.isDebugEnabled()) {
			_log.debug("InterfaceManagementServiceImpl#deleteInterface", "Exiting");
		}
		return true;
		
	}

}

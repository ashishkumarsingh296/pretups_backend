package com.web.pretups.channel.profile.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestClient;
import com.btsl.common.PretupsRestUtil;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileBL;
import com.btsl.pretups.common.PretupsI;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.web.pretups.channel.profile.web.CommissionProfileModel;

/**
 * @(#)CommissionProfileServiceImpl.java
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
 *                                 This class creates the rest request based on 
 *                                 request code and processes towards rest service.
 * 
 */

@Service("commissionProfileService")
public class CommissionProfileServiceImpl implements CommissionProfileService{
	
	/* This method sends a rest request to fetch domain/catagory/gdomain/grade list
	 * (non-Javadoc)
	 * @see com.btsl.pretups.channel.profile.service.CommissionProfileService#commissionProfileStatus(java.lang.String, org.springframework.validation.BindingResult, org.springframework.ui.ModelMap)
	 */
	@Override
	public CommissionProfileModel commissionProfileStatus(String loginId,BindingResult bindingResult,ModelMap modelMap) throws JsonProcessingException,IOException, Exception {
		
		Map<String, Object> requestObject = new HashMap<String, Object>();
		requestObject.put("loginId", loginId);
		requestObject.put("type", PretupsI.COMMPS);
		PretupsRestClient client = new PretupsRestClient();
		String responseString = client.postJSONRequest(requestObject, PretupsI.COMMPS);
		PretupsResponse<CommissionProfileModel> pretupsResponse = (PretupsResponse<CommissionProfileModel>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<CommissionProfileModel>>() {});
		CommissionProfileModel commissionProfileModel = (CommissionProfileModel)pretupsResponse.getDataObject();
		if(pretupsResponse.getStatus()){
			//modelMap.put("success",pretupsResponse.getSuccessMsg());
		}
		else{
		if(pretupsResponse.hasFormError())
			modelMap.put("fail",PretupsRestUtil.getMessageString(pretupsResponse.getFormError()));
		else if(pretupsResponse.hasGlobalError())
			modelMap.put("fail",PretupsRestUtil.getMessageString(pretupsResponse.getGlobalError()));
		}	
		return commissionProfileModel;
	}

	/* This method sends a rest request to get commission profile list based on domain/catagory/gdomain/grade
	 * (non-Javadoc)
	 * @see com.btsl.pretups.channel.profile.service.CommissionProfileService#commissionProfileList(com.btsl.pretups.channel.profile.web.CommissionProfileModel, java.lang.String, org.springframework.validation.BindingResult, org.springframework.ui.ModelMap)
	 */
	@Override
	public CommissionProfileModel commissionProfileList(CommissionProfileModel commissionProfileModel, String loginId,  BindingResult bindingResult,ModelMap modelMap)  throws JsonProcessingException, IOException, Exception{
		
		Map<String, Object> requestObject = new HashMap<String, Object>();
		requestObject.put("data", commissionProfileModel);
		requestObject.put("loginId", loginId);
		requestObject.put("type", PretupsI.COMMPSL);
		PretupsRestClient client = new PretupsRestClient();
		String responseString = client.postJSONRequest(requestObject, PretupsI.COMMPSL);
		PretupsResponse<CommissionProfileModel> pretupsResponse = (PretupsResponse<CommissionProfileModel>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<CommissionProfileModel>>() {});
		commissionProfileModel = pretupsResponse.getDataObject();
		if(pretupsResponse.getStatus()){
			//modelMap.put("success",pretupsResponse.getSuccessMsg());
		}
		else{
		if(pretupsResponse.hasFieldError()){
			Map<String, String> errorMessage = pretupsResponse.getFieldError();
			
			for (Map.Entry<String, String> entry : errorMessage.entrySet()){
				bindingResult.rejectValue(entry.getKey() , entry.getValue());
			}
		}
		else if(pretupsResponse.hasFormError())
			modelMap.put("fail",PretupsRestUtil.getMessageString(pretupsResponse.getFormError()));
		else if(pretupsResponse.hasGlobalError())
			modelMap.put("fail",PretupsRestUtil.getMessageString(pretupsResponse.getGlobalError()));
		}	
		return commissionProfileModel;
	}

	/*
	 * This method sends a rest request to save/suspend list ofcommission profiles
	 *  (non-Javadoc)
	 * @see com.btsl.pretups.channel.profile.service.CommissionProfileService#commissionProfileSaveSuspend(com.btsl.pretups.channel.profile.web.CommissionProfileModel, java.lang.String, org.springframework.validation.BindingResult, org.springframework.ui.ModelMap)
	 */
	@Override
	public CommissionProfileModel commissionProfileSaveSuspend(CommissionProfileModel commissionProfileModel, String loginId,BindingResult bindingResult,ModelMap modelMap) throws JsonProcessingException,IOException, Exception {
		Map<String, Object> requestObject = new HashMap<String, Object>();
		requestObject.put("data", commissionProfileModel);
		requestObject.put("loginId", loginId);
		requestObject.put("type", "COMMPLSS");
		PretupsRestClient client = new PretupsRestClient();
		String responseString = client.postJSONRequest(requestObject, PretupsI.COMMPLSS);
		PretupsResponse<CommissionProfileModel> pretupsResponse = (PretupsResponse<CommissionProfileModel>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<CommissionProfileModel>>() {});
		commissionProfileModel = pretupsResponse.getDataObject();
		
		if(pretupsResponse.getStatus()){
			modelMap.put("success",PretupsRestUtil.getMessageString(pretupsResponse.getSuccessMsg()));
			
		}
		else{
		
		if(pretupsResponse.hasFormError())
			modelMap.put("fail",PretupsRestUtil.getMessageString(pretupsResponse.getFormError()));
		else if(pretupsResponse.hasGlobalError())
			modelMap.put("fail",PretupsRestUtil.getMessageString(pretupsResponse.getGlobalError()));
		}			
		return commissionProfileModel;
	}
	
	public void loadCommissionProfilePage(String loginId,ModelMap modelMap) throws SQLException, BTSLBaseException, Exception{
		CommissionProfileBL commissionProfileBL = new CommissionProfileBL();
		CommissionProfileModel commissionProfileModel = commissionProfileBL.loadDomainListForSuspend(loginId);
		modelMap.put("commissionProfileModel", commissionProfileModel);
	}
	
	public void populateCommissionProfilePage(String loginId,CommissionProfileModel commissionProfileModel,ModelMap modelMap) throws SQLException, BTSLBaseException, Exception{
		CommissionProfileBL commissionProfileBL = new CommissionProfileBL();
		CommissionProfileModel commissionProfileModel1 = commissionProfileBL.loadDomainListForSuspend(loginId);
		commissionProfileModel1.setDomainCode(commissionProfileModel.getDomainCode());
		commissionProfileModel1.setCategoryCode(commissionProfileModel.getCategoryCode());
		commissionProfileModel1.setGrphDomainCode(commissionProfileModel.getGrphDomainCode());
		commissionProfileModel1.setGradeCode(commissionProfileModel.getGradeCode());
		modelMap.put("commissionProfileModel", commissionProfileModel1);
	}
	
}
package com.btsl.pretups.channel.profile.businesslogic;

import java.sql.SQLException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.web.pretups.channel.profile.web.CommissionProfileModel;


/**
 * @(#)CommissionProfileRestServiceImpl.java
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
 *                                 This class is the rest class which listens the request
 *                                 in json format and sends it to the validator class,
 *                                 and based up the validator class it process towards
 *                                 Businness Logic or else send to client.
 * 
 */

public class CommissionProfileRestServiceImpl implements CommissionProfileRestService{

	private Log _log = LogFactory.getLog(this.getClass().getName());

	@Override
	public PretupsResponse<CommissionProfileModel> loadCommissionStatus(String requestData)throws  BTSLBaseException, SQLException, Exception{
		String METHOD_NAME =  "CommissionProfileRestServiceImpl: loadCommissionStatus";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Entered");
		}
		PretupsResponse<CommissionProfileModel> response = new PretupsResponse<CommissionProfileModel>();
		CommissionProfileBL commissionProfileBL = new CommissionProfileBL();
		try{
			JsonNode dataObject =  (JsonNode) PretupsRestUtil.convertJSONToObject(requestData,	new TypeReference<JsonNode>() {});
			String loginId =  (String) PretupsRestUtil.convertJSONToObject(dataObject.get("loginId").toString(), new TypeReference<String>() {});
			CommissionProfileModel commissionProfileModel = commissionProfileBL.loadDomainListForSuspend(loginId);
			response.setDataObject(commissionProfileModel);	
			response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "profile.commissionprofilestatus.domainlist.message");
			response.setSuccessMsg("profile.commissionprofilestatus.domainlist.message");
			response.setMessageCode(PretupsErrorCodesI.LOAD_DOMAIN_LIST_SUCCESS);
		}
		catch(BTSLBaseException ex){
			response.setFormError(ex.getBtslMessages().getMessageKey());
			response.setStatus(false);
			response.setStatusCode(PretupsI.RESPONSE_FAIL);
			response.setMessageCode(Integer.toString(ex.getErrorCode()));
			_log.error(METHOD_NAME, "Exceptin:e=" + ex);
			_log.errorTrace(METHOD_NAME, ex);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Exits");
		}
		return response;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see com.btsl.pretups.channel.profile.businesslogic.CommissionProfileRestService#loadCommissionSetList(java.lang.String)
	 */
	public PretupsResponse<CommissionProfileModel> loadCommissionSetList(String requestData) throws BTSLBaseException, SQLException, Exception{
		String METHOD_NAME =  "CommissionProfileRestServiceImpl: loadCommissionSetList";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Entered");
		}
		PretupsResponse<CommissionProfileModel> response = new PretupsResponse<CommissionProfileModel> ();
		CommissionProfileBL commissionProfileBL = new CommissionProfileBL();
		try{
			JsonNode dataObject =  (JsonNode) PretupsRestUtil.convertJSONToObject(requestData,	new TypeReference<JsonNode>() {});
			CommissionProfileModel commissionProfileModel = (CommissionProfileModel) PretupsRestUtil.convertJSONToObject(dataObject.get("data").toString(), new TypeReference<CommissionProfileModel>() {});
			String loginId =  (String) PretupsRestUtil.convertJSONToObject(dataObject.get("loginId").toString(), new TypeReference<String>() {});

			CommissionProfileValidator commissionProfileValidator = new CommissionProfileValidator();
			
			commissionProfileValidator.validateCommissionProfileStatus(dataObject.get("type").textValue(),loginId, response, commissionProfileModel);	

			if(response.hasFieldError()){
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			
			commissionProfileModel = commissionProfileBL.loadCommissionList(commissionProfileModel, loginId);
			response.setDataObject(commissionProfileModel);
			response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "profile.commissionprofilestatus.commissionprofilelist.message");
			response.setSuccessMsg("profile.commissionprofilestatus.commissionprofilelist.message");
			response.setMessageCode(PretupsErrorCodesI.LOAD_COMMISSION_PROFILE_LIST_SUCCESS);
		}
		catch(BTSLBaseException ex){
			response.setFormError(ex.getBtslMessages().getMessageKey());
			response.setStatus(false);
			response.setStatusCode(PretupsI.RESPONSE_FAIL);
			response.setMessageCode(Integer.toString(ex.getErrorCode()));
			_log.error(METHOD_NAME, "Exceptin:e=" + ex);
			_log.errorTrace(METHOD_NAME, ex);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Exits");
		}
		return response;
	}

	@Override
	public PretupsResponse<CommissionProfileModel> commissionProfileSetSuspend(String requestData) throws BTSLBaseException, SQLException, Exception {
		String METHOD_NAME =  "CommissionProfileRestServiceImpl: commissionProfileSetSuspend";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Entered");
		}
		PretupsResponse<CommissionProfileModel> response = new PretupsResponse<CommissionProfileModel> ();
		CommissionProfileBL commissionProfileBL = new CommissionProfileBL();
		try{
			JsonNode dataObject =  (JsonNode) PretupsRestUtil.convertJSONToObject(requestData,	new TypeReference<JsonNode>() {});
			CommissionProfileModel commissionProfileModel = (CommissionProfileModel) PretupsRestUtil.convertJSONToObject(dataObject.get("data").toString(), new TypeReference<CommissionProfileModel>() {});
			String loginId =  (String) PretupsRestUtil.convertJSONToObject(dataObject.get("loginId").toString(), new TypeReference<String>() {});
			CommissionProfileValidator commissionProfileValidator = new CommissionProfileValidator();
			commissionProfileValidator.formValidateCommissionProfileList(response, commissionProfileModel);
			
			if(response.hasFormError()){
				return response;
			}
			commissionProfileModel= commissionProfileBL.saveSuspend(loginId, commissionProfileModel);
			response.setDataObject(commissionProfileModel);
			response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "profile.addadditionalprofile.message.successsuspendmessage");
			response.setSuccessMsg("profile.addadditionalprofile.message.successsuspendmessage");
			response.setMessageCode(PretupsErrorCodesI.COMMISSION_PROFILE_SAVE_SUSPEND_SUCCESS);
		}
		catch(BTSLBaseException ex){
			response.setFormError(ex.getBtslMessages().getMessageKey());
			response.setStatus(false);
			response.setStatusCode(PretupsI.RESPONSE_FAIL);
			response.setMessageCode(Integer.toString(ex.getErrorCode()));
			_log.error(METHOD_NAME, "Exceptin:e=" + ex);
			_log.errorTrace(METHOD_NAME, ex);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Exits");
		}
		return response;
	}

}
package com.web.pretups.channel.transfer.service;

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
import com.btsl.pretups.channel.transfer.businesslogic.C2SReversalBL;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.web.pretups.channel.transfer.web.C2SReversalModel;

@Service("c2SReversalService")
public class C2SReversalServiceImpl implements C2SReversalService{

	/* (non-Javadoc)
	 * @see com.btsl.pretups.channel.transfer.service.C2SReversalService#c2sReversal(java.lang.String, org.springframework.ui.ModelMap)
	 * 
	 * This method sends request to display c2s reversal page
	 */
	@Override
	public C2SReversalModel c2sReversal(String loginId, ModelMap modelMap) throws JsonProcessingException,BTSLBaseException, IOException {
		Map<String, Object> requestObject = new HashMap<String, Object>();
		requestObject.put("loginId", loginId);
		requestObject.put("type", PretupsI.C2SREV);
		PretupsRestClient client = new PretupsRestClient();
		String responseString = client.postJSONRequest(requestObject, PretupsI.C2SREV);
		PretupsResponse<C2SReversalModel> pretupsResponse = (PretupsResponse<C2SReversalModel>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<C2SReversalModel>>() {});
		C2SReversalModel c2SReversalModel = (C2SReversalModel)pretupsResponse.getDataObject();
		modelMap.put("c2SReversalModelUI", c2SReversalModel);
		if(pretupsResponse.getStatus()){
			//modelMap.put("success",pretupsResponse.getSuccessMsg());
		}
		else{
		if(pretupsResponse.hasFormError())
			modelMap.addAttribute("fail",PretupsRestUtil.getMessageString(pretupsResponse.getFormError()));
		else if(pretupsResponse.hasGlobalError())
			modelMap.addAttribute("fail",PretupsRestUtil.getMessageString(pretupsResponse.getGlobalError()));
		}	
		return c2SReversalModel;
	}

	/* (non-Javadoc)
	 * @see com.btsl.pretups.channel.transfer.service.C2SReversalService#confirmC2SReversal(com.btsl.pretups.channel.transfer.web.C2SReversalModel, java.lang.String, org.springframework.validation.BindingResult, org.springframework.ui.ModelMap)
	 * 
	 * This method sends partial details of recharge transaction which brings a list of recharge matching list 
	 */
	@Override
	public C2SReversalModel confirmC2SReversal(
			C2SReversalModel c2sReversalModel, String loginId,
			BindingResult bindingResult, ModelMap modelMap)
			throws JsonProcessingException, BTSLBaseException, IOException {
		Map<String, Object> requestObject = new HashMap<String, Object>();
		requestObject.put("loginId", loginId);
		requestObject.put("type", PretupsI.C2SLOADTXN);
		requestObject.put("data", c2sReversalModel);
		PretupsRestClient client = new PretupsRestClient();
		String responseString = client.postJSONRequest(requestObject, PretupsI.C2SLOADTXN);
		PretupsResponse<C2SReversalModel> pretupsResponse = (PretupsResponse<C2SReversalModel>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<C2SReversalModel>>() {});
		C2SReversalModel c2SReversalModel = (C2SReversalModel)pretupsResponse.getDataObject();
		modelMap.addAttribute("c2SReversalModel", c2SReversalModel);
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
			modelMap.addAttribute("fail",PretupsRestUtil.getMessageString(pretupsResponse.getFormError()));
		else if(pretupsResponse.hasGlobalError())
			modelMap.addAttribute("fail",PretupsRestUtil.getMessageString(pretupsResponse.getGlobalError()));
		}	
		return c2SReversalModel;
	}

	/* (non-Javadoc)
	 * @see com.btsl.pretups.channel.transfer.service.C2SReversalService#doReversal(com.btsl.pretups.channel.transfer.web.C2SReversalModel, java.lang.String, org.springframework.validation.BindingResult, org.springframework.ui.ModelMap)
	 * 
	 * This method sends the details of recharge transaction to be reversed
	 */
	@Override
	public C2SReversalModel doReversal(C2SReversalModel c2sReversalModel,String loginId, BindingResult bindingResult, ModelMap modelMap)throws JsonProcessingException, BTSLBaseException, IOException {

		Map<String, Object> requestObject = new HashMap<String, Object>();
		requestObject.put("loginId", loginId);
		requestObject.put("type", PretupsI.C2SDOREV);
		requestObject.put("data", c2sReversalModel);
		PretupsRestClient client = new PretupsRestClient();
		String responseString = client.postJSONRequest(requestObject, PretupsI.C2SDOREV);
		PretupsResponse<C2SReversalModel> pretupsResponse = (PretupsResponse<C2SReversalModel>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<C2SReversalModel>>() {});
		C2SReversalModel c2SReversalModel = (C2SReversalModel)pretupsResponse.getDataObject();
		modelMap.put("c2SReversalModel", c2SReversalModel);
		
		if(Integer.parseInt(PretupsErrorCodesI.TXN_STATUS_SUCCESS) == pretupsResponse.getStatusCode()){
			modelMap.put("success",c2SReversalModel.getC2sReverseResponseMessage());
		}
		if(Integer.parseInt(PretupsErrorCodesI.TXN_STATUS_FAIL) == pretupsResponse.getStatusCode()){
			modelMap.put("fail",c2SReversalModel.getC2sReverseResponseMessage());
		}
		else{
			if(pretupsResponse.hasFormError())
				modelMap.put("fail",PretupsRestUtil.getMessageString(pretupsResponse.getFormError()));
			else if(pretupsResponse.hasGlobalError())
				modelMap.put("fail",PretupsRestUtil.getMessageString(pretupsResponse.getGlobalError()));

		}	
		return c2SReversalModel;
	
	}
	
	/**
	 * @param c2SReversalModelUI
	 * @param loginId
	 * @param modelMap
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * 
	 * This method loads reversal page with populating without rest
	 */
	public void populateReversalData(C2SReversalModel c2SReversalModelUI,String loginId,ModelMap modelMap) throws BTSLBaseException, SQLException{
		C2SReversalBL c2sReversalBL = new C2SReversalBL();
		C2SReversalModel c2SReversalModel1 =  c2sReversalBL.c2sReversal(loginId);
		c2SReversalModel1.setModuleType(c2SReversalModelUI.getModuleType());
		c2SReversalModel1.setServiceType(c2SReversalModelUI.getServiceType());
		c2SReversalModel1.setCurrentBalance(c2SReversalModelUI.getCurrentBalance());
		c2SReversalModel1.setSenderMsisdn(c2SReversalModelUI.getSenderMsisdn());
		c2SReversalModel1.setPin(c2SReversalModelUI.getPin());
		c2SReversalModel1.setSubscriberMsisdn(c2SReversalModelUI.getSubscriberMsisdn());
		c2SReversalModel1.setTxID(c2SReversalModelUI.getTxID());
		modelMap.put("c2SReversalModelUI", c2SReversalModel1);
	}
	
	/**
	 * @param loginId
	 * @param modelMap
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * 
	 * This method loads the reversal page without rest
	 */
	public C2SReversalModel loadReversalPage(String loginId,ModelMap modelMap) throws BTSLBaseException, SQLException{
		C2SReversalBL c2sReversalBL = new C2SReversalBL();
		C2SReversalModel c2SReversalModel =  c2sReversalBL.c2sReversal(loginId);
		modelMap.put("c2SReversalModelUI", c2SReversalModel);
		return c2SReversalModel;
	}
	
	/**
	 * @param c2sReversalModel
	 * @param loginId
	 * @param modelMap
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * 
	 * This method loads reversal list page without rest
	 */
	public void loadReverselistPage(C2SReversalModel c2sReversalModel, String loginId , ModelMap modelMap) throws BTSLBaseException, SQLException{
		C2SReversalBL c2sReversalBL = new C2SReversalBL();
		C2SReversalModel c2SReversalModel =  c2sReversalBL.confirmC2SReversal(c2sReversalModel,loginId);
		modelMap.put("c2SReversalModelUI", c2SReversalModel);
	}

	/* (non-Javadoc)
	 * @see com.btsl.pretups.channel.transfer.service.C2SReversalService#txnStatusByTransferId(com.btsl.pretups.channel.transfer.web.C2SReversalModel, java.lang.String, org.springframework.validation.BindingResult, org.springframework.ui.ModelMap)
	 * 
	 * This method sends reverse transaction id to check the status
	 */
	@Override
	public C2SReversalModel txnStatusByTransferId(
			C2SReversalModel c2sReversalModel, String loginId,
			BindingResult bindingResult, ModelMap modelMap)
			throws JsonProcessingException, BTSLBaseException, IOException {
		Map<String, Object> requestObject = new HashMap<String, Object>();
		requestObject.put("loginId", loginId);
		requestObject.put("type", PretupsI.C2SREVSTAT);
		requestObject.put("data", c2sReversalModel);
		PretupsRestClient client = new PretupsRestClient();
		String responseString = client.postJSONRequest(requestObject, PretupsI.C2SREVSTAT);
		PretupsResponse<C2SReversalModel> pretupsResponse = (PretupsResponse<C2SReversalModel>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<C2SReversalModel>>() {});
		C2SReversalModel c2SReversalModel = (C2SReversalModel)pretupsResponse.getDataObject();
		modelMap.put("c2SReversalModel", c2SReversalModel);
		if(pretupsResponse.getStatus()){
			modelMap.put("success",PretupsRestUtil.getMessageString(pretupsResponse.getSuccessMsg()));
		}
		else{
		if(pretupsResponse.hasFormError())
			modelMap.addAttribute("fail",PretupsRestUtil.getMessageString(pretupsResponse.getFormError()));
		else if(pretupsResponse.hasGlobalError())
			modelMap.addAttribute("fail",PretupsRestUtil.getMessageString(pretupsResponse.getGlobalError()));
		}	
		return c2SReversalModel;
	}

}

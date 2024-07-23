package com.btsl.pretups.channel.transfer.businesslogic;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.validator.ValidatorException;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.web.pretups.channel.transfer.web.C2SReversalModel;

/**
 * @(#)C2SReversalRestServiceImpl.java
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
 *                                 jashobanta.mahapatra 30/06/2016
 * 
 *                                 This class is the rest class which listens the request
 *                                 in json format and sends it to the validator class,
 *                                 and based on the validator class it process towards
 *                                 Businness Logic or else send to client.
 * 
 */

public class C2SReversalRestServiceImpl implements C2SReversalRestService{
	
	private Log _log = LogFactory.getLog(this.getClass().getName());

	/* (non-Javadoc)
	 * @see com.btsl.pretups.channel.transfer.businesslogic.C2SReversalRestService#c2sReversal(java.lang.String)
	 * 
	 * This api return c2s reversal required input to captured
	 */
	@Override
	public PretupsResponse<C2SReversalModel> c2sReversal(String requestData) throws JsonParseException, SQLException, IOException,BTSLBaseException {

		final String METHOD_NAME =  "C2SReversalRestServiceImpl: c2sReversal";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Entered");
		}
		PretupsResponse<C2SReversalModel> response = new PretupsResponse<C2SReversalModel>();
		C2SReversalBL c2SReversalBL = new C2SReversalBL();
		try{
			JsonNode dataObject =  (JsonNode) PretupsRestUtil.convertJSONToObject(requestData,	new TypeReference<JsonNode>() {});
			String loginId =  (String) PretupsRestUtil.convertJSONToObject(dataObject.get("loginId").toString(), new TypeReference<String>() {});
			C2SReversalModel model = c2SReversalBL.c2sReversal(loginId);
			response.setDataObject(model);
	
			response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "channel.transfer.reversal.initiated");
			response.setSuccessMsg("channel.transfer.reversal.initiated");
			response.setMessageCode(PretupsErrorCodesI.C2S_REVERSAL_REQUEST_INITIATED);
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

	/* (non-Javadoc)
	 * @see com.btsl.pretups.channel.transfer.businesslogic.C2SReversalRestService#confirmC2SReversal(java.lang.String)
	 * 
	 * This api returns list of recharge transcations based on some input filter
	 */
	@Override
	public PretupsResponse<C2SReversalModel> confirmC2SReversal(
			String requestData)  throws JsonParseException, SQLException, IOException,BTSLBaseException, ValidatorException, SAXException {

		final String METHOD_NAME =  "C2SReversalRestServiceImpl: confirmC2SReversal";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Entered");
		}
		PretupsResponse<C2SReversalModel> response = new PretupsResponse<C2SReversalModel>();
		C2SReversalBL c2SReversalBL = new C2SReversalBL();
		try{
			JsonNode dataObject =  (JsonNode) PretupsRestUtil.convertJSONToObject(requestData,	new TypeReference<JsonNode>() {});
			String loginId =  (String) PretupsRestUtil.convertJSONToObject(dataObject.get("loginId").toString(), new TypeReference<String>() {});
			C2SReversalModel c2sReversalModel = (C2SReversalModel) PretupsRestUtil.convertJSONToObject(dataObject.get("data").toString(), new TypeReference<C2SReversalModel>() {});
		
			C2SReversalValidator reversalValidator = new C2SReversalValidator();
			
			reversalValidator.validateC2SReversal(dataObject.get("type").textValue(),loginId, c2sReversalModel, response);

			if(response.hasFieldError()){
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			
			c2sReversalModel = c2SReversalBL.confirmC2SReversal(c2sReversalModel,loginId);
			response.setDataObject(c2sReversalModel);
	
			response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "channel.transfer.reversal.load.txn.success");
			response.setSuccessMsg("channel.transfer.reversal.load.txn.success");
			response.setMessageCode(PretupsErrorCodesI.C2S_REVERSAL_REQUEST_TO_GET_TXN_LIST_SUCCESS);
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

	/* (non-Javadoc)
	 * @see com.btsl.pretups.channel.transfer.businesslogic.C2SReversalRestService#doReverse(java.lang.String)
	 * 
	 * This api does reverse of a recharge 
	 */
	@Override
	public PretupsResponse<C2SReversalModel> doReverse(String requestData)throws Exception {

		final String METHOD_NAME =  "C2SReversalRestServiceImpl: doReverse";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Entered");
		}
		PretupsResponse<C2SReversalModel> response = new PretupsResponse<C2SReversalModel>();
		C2SReversalBL c2SReversalBL = new C2SReversalBL();
		try{
			JsonNode dataObject =  (JsonNode) PretupsRestUtil.convertJSONToObject(requestData,	new TypeReference<JsonNode>() {});
			String loginId =  (String) PretupsRestUtil.convertJSONToObject(dataObject.get("loginId").toString(), new TypeReference<String>() {});
			C2SReversalModel c2sReversalModel = (C2SReversalModel) PretupsRestUtil.convertJSONToObject(dataObject.get("data").toString(), new TypeReference<C2SReversalModel>() {});
			//response.setDataObject(c2sReversalModel);
			
			C2SReversalValidator c2sReversalValidator = new C2SReversalValidator();
			response.setFieldError(new HashMap<>());
			c2sReversalValidator.validatePinOrSendermsisdn(loginId,c2sReversalModel, response.getFieldError());
			c2sReversalValidator.validateTransferIdOrSubscribermsisdn(c2sReversalModel, response.getFieldError());
			
			if(response.hasFieldError()){
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			
			c2sReversalModel = c2SReversalBL.reverse(c2sReversalModel,loginId);
	
			if(true == c2sReversalModel.getC2sReverseResponseStatus()){
				response.setResponse(Integer.parseInt(PretupsErrorCodesI.TXN_STATUS_SUCCESS), true, "c2s.reversal.successful");
				response.setSuccessMsg("c2s.reversal.successful");
				response.setMessageCode(PretupsErrorCodesI.C2S_REVERSAL_SUCCESSFULL);
			}
			else{
				//response.setResponse(Integer.parseInt(PretupsErrorCodesI.TXN_STATUS_FAIL), false, "c2s.reversal.unsuccessful");
				response.setFormError("c2s.reversal.unsuccessful");
				response.setStatus(false);
				response.setStatusCode(Integer.parseInt(PretupsErrorCodesI.TXN_STATUS_FAIL));
				response.setMessageCode(PretupsErrorCodesI.C2S_RECEIVER_FAIL_KEY);
			}
			response.setDataObject(c2sReversalModel);
		}
		catch(BTSLBaseException ex){
			if(Integer.parseInt(PretupsErrorCodesI.C2S_RECEIVER_FAIL_KEY ) == ex.getErrorCode()){
				//response.setFormError("c2stranfer.c2srecharge.error.unsuccess");
				response.setFormError(ex.getBtslMessages().getMessageKey());
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				response.setMessageCode(Integer.toString(ex.getErrorCode()));
			}
			else{
				response.setResponse(PretupsI.RESPONSE_FAIL, false, "global.error");
				response.setMessageCode(PretupsErrorCodesI.PRETUPS_REST_GENERAL_ERROR);
			}
			_log.error(METHOD_NAME, "Exceptin:e=" + ex);
			_log.errorTrace(METHOD_NAME, ex);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Exits");
		}
		return response;
	
	}

	/* (non-Javadoc)
	 * @see com.btsl.pretups.channel.transfer.businesslogic.C2SReversalRestService#reverseStatus(java.lang.String)
	 * 
	 * This api is to check the status of a reverse transaction based on transfer id
	 */
	@Override
	public PretupsResponse<C2SReversalModel> reverseStatus(String requestData)
			throws JsonParseException, SQLException, IOException,
			BTSLBaseException, Exception {

		final String METHOD_NAME =  "C2SReversalRestServiceImpl: reverseStatus";
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Entered");
		}
		PretupsResponse<C2SReversalModel> response = new PretupsResponse<C2SReversalModel>();
		C2SReversalBL c2SReversalBL = new C2SReversalBL();
		try{
			JsonNode dataObject =  (JsonNode) PretupsRestUtil.convertJSONToObject(requestData,	new TypeReference<JsonNode>() {});
			String loginId =  (String) PretupsRestUtil.convertJSONToObject(dataObject.get("loginId").toString(), new TypeReference<String>() {});
			C2SReversalModel c2sReversalModel = (C2SReversalModel) PretupsRestUtil.convertJSONToObject(dataObject.get("data").toString(), new TypeReference<C2SReversalModel>() {});
			response.setDataObject(c2sReversalModel);
			
			C2SReversalValidator c2sReversalValidator = new C2SReversalValidator();
			c2sReversalValidator.validateTxID( c2sReversalModel, response);

			if(response.hasFieldError()){
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			c2sReversalModel = c2SReversalBL.reverseStatus(c2sReversalModel,loginId);
			
			response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "c2s.reversal.txnstatus.success");
			response.setSuccessMsg("c2s.reversal.txnstatus.success");
			response.setMessageCode(PretupsErrorCodesI.C2S_REVERSAL_TXN_STATUS_CHECK_SUCCESS);
		}
		catch(BTSLBaseException ex){
			if(Integer.parseInt(PretupsErrorCodesI.C2S_RECEIVER_FAIL_KEY ) == ex.getErrorCode()){
				//response.setFormError("c2stranfer.c2srecharge.error.unsuccess");
				response.setFormError(ex.getBtslMessages().getMessageKey());
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				response.setMessageCode(Integer.toString(ex.getErrorCode()));
			}
			else{
				response.setResponse(PretupsI.RESPONSE_FAIL, false, "global.error");
				response.setMessageCode(PretupsErrorCodesI.PRETUPS_REST_GENERAL_ERROR);
			}
			_log.error(METHOD_NAME, "Exceptin:e=" + ex);
			_log.errorTrace(METHOD_NAME, ex);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(METHOD_NAME, "Exits");
		}
		return response;
	
	}

}

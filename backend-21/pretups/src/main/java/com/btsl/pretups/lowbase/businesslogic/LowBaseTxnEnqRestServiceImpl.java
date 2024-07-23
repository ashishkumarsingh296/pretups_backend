package com.btsl.pretups.lowbase.businesslogic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;


/**
 * This class provides basic method implementation of all the methods 
 * declared in LowBaseTxnEnqRestService interface. Which provides implementation
 * for performing Low Base Transaction Enquiry and Eligibility Enquiry by implementing Rest Web Service
 * 
 * @author lalit.chattar
 *
 */
public class LowBaseTxnEnqRestServiceImpl implements LowBaseTxnEnqRestService {

	private static final Log _log = LogFactory.getLog(LowBaseTxnEnqRestServiceImpl.class.getName());
	
	private static final String CLASS_NAME = "LowBaseTxnEnqRestServiceIMPL";
	
	
	
	/**
	 * This method load data for Low Base Transaction Enquiry from DB by calling
	 * DAO methods, And Work as Rest Service
	 * 
	 * @param requestData JSON String of request data
	 * @throws BTSLBaseException 
	 */
	@Override
	public PretupsResponse<List<LowBasedRechargeVO>> loadLowBaseTransactionDetails(String requestData) throws BTSLBaseException {
		
		final String methodName = "#loadLowBaseTransactionDetails";
		Connection con = null;
		MComConnectionI mcomCon = null;
		try {
			if (_log.isDebugEnabled()) {
				_log.debug(CLASS_NAME+methodName, "Entered");
			}
			
			mcomCon = new MComConnection();
			try{con=mcomCon.getConnection();}catch(SQLException e){
				_log.error(methodName, "SQLException" + e);
				 _log.errorTrace(methodName,e);
			}
			PretupsResponse<List<LowBasedRechargeVO>> response = new PretupsResponse<>();
			JsonNode dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<JsonNode>() {});
			
			LowBasedRechargeVO basedRechargeVO = (LowBasedRechargeVO) PretupsRestUtil.convertJSONToObject(dataObject.get("data").toString(), new TypeReference<LowBasedRechargeVO>() {});
			
			LowBaseValidator baseValidator = new LowBaseValidator();
			baseValidator.validateRequestData(dataObject.get("type").textValue(), response, basedRechargeVO, "LowBaseEnquiryDetails");
			
			if (response.hasFieldError()) {
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			
			List<LowBasedRechargeVO> dataList = new LowBaseRechargeDAO().loadLowBaseTransactionDetails(basedRechargeVO, con);
			
			if(dataList.isEmpty()){
				response.setResponse("low.base.transaction.no.data.found.for.this.retailer", null);
				response.setMessageCode(PretupsErrorCodesI.LOW_BASE_RECHARGE_NO_DATA_FOUND);
				return response;
			}else{
				response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, dataList);
				return response;
			}
			
		} catch (BTSLBaseException | IOException e) {
			throw new BTSLBaseException(e);
		}finally{
			if (mcomCon != null) {
				mcomCon.close("LowBaseTxnEnqRestServiceImpl#loadLowBaseTransactionDetails");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(CLASS_NAME+methodName, "Exit");
			}
		}
		
	}
	
	
	/**
	 * This method load eligibility details for recharge amount
	 * 
	 * @param requestData JSON String of request data
	 * @throws BTSLBaseException 
	 */
	@Override
	public PretupsResponse<LowBasedRechargeVO> loadLowBaseEligibilityDetails(String requestData) throws BTSLBaseException {
		
		final String methodName = "#loadLowBaseEligibilityDetails";
		Connection con = null;
		MComConnectionI mcomCon = null;
		try {
			if (_log.isDebugEnabled()) {
				_log.debug(CLASS_NAME+methodName, "Entered");
			}
			
			mcomCon = new MComConnection();
			try{con=mcomCon.getConnection();}catch(SQLException e){
				_log.error(methodName, "SQLException" + e);
				 _log.errorTrace(methodName,e);
			}
			PretupsResponse<LowBasedRechargeVO> response = new PretupsResponse<>();
			JsonNode dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<JsonNode>() {});
			
			LowBasedRechargeVO basedRechargeVO = (LowBasedRechargeVO) PretupsRestUtil.convertJSONToObject(dataObject.get("data").toString(), new TypeReference<LowBasedRechargeVO>() {});
			
			LowBaseValidator baseValidator = new LowBaseValidator();
			baseValidator.validateRequestData(dataObject.get("type").textValue(), response, basedRechargeVO, "LowBaseEligibilityDetails");
			
			if (response.hasFieldError()) {
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			
			LowBasedRechargeVO lowBasedRechargeVO = new LowBaseRechargeDAO().loadLowBaseTransactionEligibilityDetails(basedRechargeVO, con);
			
			if(lowBasedRechargeVO == null){
				response.setResponse("low.base.eligibility.no.data.found.for.this.subscriber", null);
				response.setMessageCode(PretupsErrorCodesI.LOW_BASE_RECHARGE_NO_DATA_FOUND);
				return response;
			}else{
				response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, lowBasedRechargeVO);
				return response;
			}
			
		} catch (BTSLBaseException | IOException e) {
			throw new BTSLBaseException(e);
		}finally{
			if(mcomCon != null){mcomCon.close("LowBaseTxnEnqRestServiceImpl#loadLowBaseEligibilityDetails");mcomCon=null;}
			if (_log.isDebugEnabled()) {
				_log.debug(CLASS_NAME+methodName, "Exit");
			}
		}
		
	}

	
}

package com.btsl.pretups.domain.businesslogic;

import java.util.List;

import com.btsl.common.PretupsResponse;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;
import com.fasterxml.jackson.databind.JsonNode;


/**
 * This class provides method for Validating data
 * @author lalit.chattar
 * @param <T>
 *
 */
public class DomainValidator {

	public static final Log log = LogFactory.getLog(DomainValidator.class.getName());
	
	/**
	 * This method validate request data for Loading geographical domain
	 * @param jsonNode
	 * @param pretupsResponse
	 * @return pretupsResponse
	 */
	public PretupsResponse<List<Object>> validateRequestDataForGeographyDomain(JsonNode jsonNode, PretupsResponse<List<Object>> pretupsResponse){
		return validateLoginId(jsonNode, pretupsResponse);
	}
	
	
	/**
	 * This method validate request data for Loading domain
	 * @param <T>
	 * @param jsonNode
	 * @param pretupsResponse
	 * @return pretupsResponse
	 */
	public PretupsResponse<List<Object>> validateRequestDataForDomain(JsonNode jsonNode, PretupsResponse<List<Object>> pretupsResponse){
		if(jsonNode.has(PretupsI.DATA) && jsonNode.get(PretupsI.DATA).has(PretupsI.LOGIN_ID)){
			return  validateLoginId(jsonNode.get(PretupsI.DATA), pretupsResponse);
		}else if(jsonNode.has(PretupsI.DATA) && jsonNode.get(PretupsI.DATA).has(PretupsI.BY_USER_TYPE)){
			return  validateOperatorUserType(jsonNode.get(PretupsI.DATA), pretupsResponse);
		}else if(jsonNode.has(PretupsI.DATA) && jsonNode.get(PretupsI.DATA).has(PretupsI.IS_SCHEDULED)){
			return validateIfScheduled(jsonNode.get(PretupsI.DATA), pretupsResponse);
		}
		return pretupsResponse;
	}
	
	private PretupsResponse<List<Object>> validateLoginId(JsonNode jsonNode, PretupsResponse<List<Object>> pretupsResponse){
		if(jsonNode.has(PretupsI.LOGIN_ID)){
			if(BTSLUtil.isNullString(jsonNode.get(PretupsI.LOGIN_ID).textValue())){
				pretupsResponse.setResponse("", null);
			}
		}else{
			pretupsResponse.setResponse("", null);
		}
		return pretupsResponse;
	}
	
	private PretupsResponse<List<Object>> validateOperatorUserType(JsonNode jsonNode, PretupsResponse<List<Object>> pretupsResponse){
		if(jsonNode.has(PretupsI.BY_USER_TYPE) && PretupsI.OPERATOR_USER_TYPE.equals(jsonNode.get(PretupsI.BY_USER_TYPE).textValue())){
			if(BTSLUtil.isNullString(jsonNode.get(PretupsI.BY_USER_TYPE).textValue())){
				pretupsResponse.setResponse("", null);
			}
		}else{
			pretupsResponse.setResponse("", null);
		}
		return pretupsResponse;
	}
	
	private PretupsResponse<List<Object>> validateIfScheduled(JsonNode jsonNode, PretupsResponse<List<Object>> pretupsResponse){
		if(jsonNode.has(PretupsI.IS_SCHEDULED) && jsonNode.has(PretupsI.USER_ID)){
			if(BTSLUtil.isNullString(jsonNode.get(PretupsI.USER_ID).textValue())){
				pretupsResponse.setResponse("", null);
			}
		}else{
			pretupsResponse.setResponse("", null);
		}
		return pretupsResponse;
	}
}

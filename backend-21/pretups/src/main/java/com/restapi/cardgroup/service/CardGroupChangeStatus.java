package com.restapi.cardgroup.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ws.rs.core.MediaType;


import com.annotation.RestEasyAnnotation;
import org.apache.commons.validator.ValidatorException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonValidator;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDAO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetDAO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO;
import com.btsl.pretups.channel.profile.businesslogic.CardGroupStatusVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.WebServiceKeywordCacheVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

/*
 * This class implements LookupsRestService and provides basic method to load Look and sublookups
 */
@RestController
@RestEasyAnnotation
@RequestMapping(value = "/cardGroup")
@Tag(name = "${CardGroupChangeStatus.name}", description = "${CardGroupChangeStatus.desc}")//@Api(value="Voucher card group change status")
public class CardGroupChangeStatus {

	public static final Log log = LogFactory.getLog(CardGroupChangeStatus.class.getName());
	
	/**
	 * This method load sub-lookup from cache basis on lookup type
	 * @param requestData Json string of lookup type
	 * @return  PretupsResponse<List<CardGroupSetVO>>
	 * @throws IOException, Exception
	 */
	@PostMapping(value = "/loadCardGroupSetList", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	//@ApiOperation(value = "Load voucher card group name list", response = PretupsResponse.class)
	public PretupsResponse<List<CardGroupSetVO>> loadCardGroupSet(
			@Parameter(description = SwaggerAPIDescriptionI.LOAD_CARD_GROUP_SET, required = true)
			@RequestBody CardGroupStatusVO defaultCardGroupVO) throws IOException, SQLException, BTSLBaseException {
		final String methodName =  "loadCardGroupSet";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		Connection con = null;
        MComConnectionI mcomCon = null;
        PretupsResponse<List<CardGroupSetVO>> response = null;
        try {
			mcomCon = new MComConnection();
            con=mcomCon.getConnection();
			response = new PretupsResponse<List<CardGroupSetVO>>();
        	
        	/*validateRequestData(PretupsRestI.LOAD_CARDGROUP_SET, response, defaultCardGroupVO,
					"CardGroupStatusList");

			if (response.hasFieldError()) {
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}*/
			
			String categories = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CARD_GROUP_ALLOWED_CATEGORIES));
			String[] allowedCategories = categories.split(",");
			PretupsRestUtil.validateLoggedInUser(defaultCardGroupVO.getIdentifierType(),defaultCardGroupVO.getIdentifierValue(),
					con, response, allowedCategories);
			if (response.hasFormError()) {
				response.setStatus(false);
				response.setMessageCode("user.unauthorized");
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			if(BTSLUtil.isEmpty(defaultCardGroupVO.getNetworkCode()) || BTSLUtil.isEmpty(defaultCardGroupVO.getModuleCode())){
				 response.setDataObject(PretupsI.RESPONSE_FAIL, false, new ArrayList<CardGroupSetVO>());
				return response;
			}
			CardGroupDAO cardGroupDAO = new CardGroupDAO();
			response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,cardGroupDAO.loadCardGroupSetWithDate(con, defaultCardGroupVO.getNetworkCode(),
					defaultCardGroupVO.getModuleCode()));
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting");
			}
        } catch (Exception e) {
            log.error(methodName, "Exceptin:e=" + e);
            log.errorTrace(methodName, e);
        } finally {
            try {
            	if (mcomCon != null) {
    				mcomCon.close("CardGroupChangeStatus#loadCardGroupSet");
    				mcomCon = null;
    			}
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exited ");
            }
        }
		return response;
	}
	
	
	/**
	 * This method load sub-lookup from cache basis on lookup type
	 * @param requestData Json string of lookup type
	 * @return  PretupsResponse<List<CardGroupSetVO>>
	 * @throws IOException, Exception
	 */
	@PostMapping(value = "/updateCardGroupSetStatus", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	//@ApiOperation(value = "Voucher card group status update", response = PretupsResponse.class)
	public PretupsResponse<List<CardGroupSetVO>> updateCardGroupStatus(
			@Parameter(description = SwaggerAPIDescriptionI.UPDATE_CARD_GROUP_STATUS, required = true)
		@RequestBody  String requestData) throws IOException, SQLException, BTSLBaseException{
		final String methodName =  "updateCardGroupStatus";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		Connection con = null;
        MComConnectionI mcomCon = null;
		PretupsResponse<List<CardGroupSetVO>> response = new PretupsResponse<List<CardGroupSetVO>>();
		int count = 0;
		StringBuilder key = null;
		try {
			mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            JsonNode requestNode = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<JsonNode>() {});
            
			String categories = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CARD_GROUP_ALLOWED_CATEGORIES));
			String[] allowedCategories = categories.split(",");
			PretupsRestUtil.validateLoggedInUser(requestNode, con, response, allowedCategories);
			if (response.hasFormError()) {
				response.setStatus(false);
				response.setMessageCode("user.unauthorized");
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			
			JsonNode dataNode =  requestNode.get("data");
    		if(!dataNode.has("cardGroupSetList") || dataNode.size() == 0){
    			 response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, new ArrayList<CardGroupSetVO>());
    			return response;
    		}
    		CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
    		if (log.isDebugEnabled()) {
    			log.debug(methodName, "Exiting");
    		}
    		ObjectMapper mapper = new ObjectMapper();
    		CardGroupDAO cardGroupDAO = new CardGroupDAO();
    		
    		ArrayList<CardGroupSetVO> cardGroupList = cardGroupDAO.loadCardGroupSet(con, dataNode.get("networkCode").textValue(), dataNode.get("moduleCode").textValue());
    		
    		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    		
    		List cardGroupListToUpdate = Arrays.asList(mapper.readValue(dataNode.get("cardGroupSetList").toString(), CardGroupSetVO[].class));
    		CardGroupSetVO cgSetVo = null;
    		for(int i =0;i<cardGroupListToUpdate.size();i++){
    			cgSetVo = (CardGroupSetVO) cardGroupListToUpdate.get(i);
    			if(BTSLUtil.isNullString(cgSetVo.getCardGroupSetName())){
    				throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.CARD_GROUP_NAME_EMPTY, 0,null,null);
    			}
    			if(BTSLUtil.isNullString(cgSetVo.getServiceTypeDesc())){
    				throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.SERVICE_TYPE_DESC_EMPTY, 0,null,null);
    			}
    			if(BTSLUtil.isNullString(cgSetVo.getSubServiceTypeDescription())){
    				throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.SUB_SERVICE_TYPE_DESC_EMPTY, 0,null,null);
    			}
    			if(BTSLUtil.isNullString(cgSetVo.getModifiedBy())){
    				throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.MODIFIED_BY_EMPTY, 0,null,null);
    			}
    			if(BTSLUtil.isNullString(cgSetVo.getStatus())){
    				throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.STATUS_EMPTY, 0,null,null);
    			}
    			if(!cgSetVo.getStatus().equalsIgnoreCase("S")&&!cgSetVo.getStatus().equalsIgnoreCase("Y")){
    				throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.STATUS_INVALID, 0,null,null);
    			}
    			if(cgSetVo.getStatus().equalsIgnoreCase("S")){
    				if(BTSLUtil.isNullString(cgSetVo.getLanguage1Message())){
    					throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.LANGUAGE1_MSG_EMPTY, 0,null,null);
        			}
    				if(BTSLUtil.isNullString(cgSetVo.getLanguage2Message())){
    					throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.LANGUAGE2_MSG_EMPTY, 0,null,null);
        			}
    			}
    			
    		}
    		HashMap<String, CardGroupSetVO> cardGroupSetMap = new HashMap<String, CardGroupSetVO>();
    		key = new StringBuilder();
    		for(CardGroupSetVO cardGroupSetVO : cardGroupList) {
    			key.setLength(0);
    			key.append(cardGroupSetVO.getCardGroupSetName()).append("_").append(cardGroupSetVO.getServiceTypeDesc()).
    				append("_").append(cardGroupSetVO.getSubServiceTypeDescription());
    			cardGroupSetMap.put(key.toString(), cardGroupSetVO);
    		}
    		
    		key = null;
    		CardGroupSetVO cardGroupSetVo = null;
    		Date currentDate = new Date();
    		key = new StringBuilder();
    		if(cardGroupListToUpdate != null && cardGroupListToUpdate.size() > 0 && cardGroupSetMap != null) {
    			for(int i=0; i < cardGroupListToUpdate.size(); i++) {
    				key.setLength(0);
    				cardGroupSetVo = (CardGroupSetVO) cardGroupListToUpdate.get(i);
					
					key.append(cardGroupSetVo.getCardGroupSetName()).append("_").append(cardGroupSetVo.getServiceTypeDesc()).
	    				append("_").append(cardGroupSetVo.getSubServiceTypeDescription());
					if(cardGroupSetMap.containsKey(key.toString())) {
						CardGroupSetVO cardGroupSetVO1 = cardGroupSetMap.get(key.toString());
						if(cardGroupSetVO1 != null) {
							cardGroupSetVo.setModifiedOn(currentDate);
							cardGroupSetVo.setCardGroupSetID(cardGroupSetVO1.getCardGroupSetID());
						}
					}
					else{
						throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.CARD_GROUP_CHNG_STATUS_INVALID, 0,null,null);
					}
				}
    		}
    		
    		count = cardGroupSetDAO.suspendCardGroupSetList(con, cardGroupListToUpdate);
    		if(count > 0) {
    			response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "Card group sets successfully suspended or activated");
    			response.setMessageCode(PretupsErrorCodesI.CARD_GROUP_CHANGE_STATUS_SUCCESS);
    			mcomCon.finalCommit();
    		} else {
    			response.setStatus(false);
    			response.setStatusCode(PretupsI.RESPONSE_FAIL);
    			mcomCon.finalRollback();
    		}
		}  catch (BTSLBaseException be) {
	       	 log.error(methodName, "Exception:e=" + be);
	         log.errorTrace(methodName, be);
	         response.setStatus(false);
	 	     String resmsg  = RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), be.getMessage(), null);
	 	     response.setMessageCode(be.getMessage());
	 	     response.setMessage(resmsg);
	 	    response.setStatusCode(PretupsI.RESPONSE_FAIL);
	        }
		catch (Exception e) { 
			log.error(methodName, "Exception:e=" + e);
     	  response.setStatus(false);
     	 response.setStatusCode(PretupsI.RESPONSE_FAIL);
     	  response.setMessageCode("error.general.processing");
     	  response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");	
    } finally {
            try {
            	if (mcomCon != null) {
    				mcomCon.close("CardGroupChangeStatus#updateCardGroupStatus");
    				mcomCon = null;
    			}
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exited ");
            }
        }
		
		return response;
	}
	
	public void validateRequestData(String type, PretupsResponse<?> response, CardGroupStatusVO cardGroupStatusVO, String referenceName) throws ValidatorException, IOException, SAXException {
		
		WebServiceKeywordCacheVO webServiceKeywordCacheVO = ServiceKeywordCache
				.getWebServiceTypeObject(type);
		String validationXMLpath = webServiceKeywordCacheVO.getValidatorName();
		CommonValidator commonValidator = new CommonValidator(validationXMLpath, cardGroupStatusVO, referenceName);
		Map<String, String> errorMessages = commonValidator.validateModel();
		response.setFieldError(errorMessages);

	}

}

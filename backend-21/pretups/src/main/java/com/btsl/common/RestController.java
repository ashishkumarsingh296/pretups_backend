package com.btsl.common;

import java.io.IOException;
import java.sql.Connection;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.login.LoginDAO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.WebServiceKeywordCacheVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class is single lending point for all the rest service.
 */
@Path("/common")
public class RestController {

	public static final Log log = LogFactory.getLog(RestController.class.getName());

	private static final String LOGIN_ID = "loginId";
	private static final String PASSWROD = "password";
	private static final String EXTERNAL_CODE = "externalCode";
	/**
	 * This method process request comming form client
	 *
	 * @param requestData  Json String of requested Object
	 * @return pretupsResponse PretupsResponse Object
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 * @throws Exception
	 */

	@POST
	@Path("/rest-controller")
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PretupsResponse<JsonNode> processPostRequest(String requestData) throws JsonParseException, JsonMappingException, IOException, RuntimeException, Exception{
		if (log.isDebugEnabled()) {
			log.debug("RestController#processPostRequest", PretupsI.ENTERED);
		}
		PretupsResponse<JsonNode> pretupsResponse = new PretupsResponse<>();
		Connection con = null;
		MComConnectionI mcomCon = null;

		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			JsonNode jsonNode = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData,
					new TypeReference<JsonNode>() {
			});
			if (!jsonNode.has("type") || jsonNode.get("type").isNull()
					|| "".equalsIgnoreCase(jsonNode.get("type").textValue())) {
				pretupsResponse.setResponse(PretupsRestUtil.getMessageString("action.type.does.not.exists.in.the.request"), null);
				return pretupsResponse;
			}
			WebServiceKeywordCacheVO webServiceKeywordCacheVO = ServiceKeywordCache
					.getWebServiceTypeObject(jsonNode.get("type").textValue());
			if (webServiceKeywordCacheVO == null) {
				pretupsResponse.setResponse(PretupsRestUtil.getMessageString("action.type.does.not.exists.in.the.system"), null);				
				return pretupsResponse;
			}
			if("Y".equalsIgnoreCase(webServiceKeywordCacheVO.getIsDataValidationRequired()) ){
				if (!jsonNode.has("data") || jsonNode.get("data").isNull() || jsonNode.get("data").size() == 0) {
					pretupsResponse.setResponse(PretupsRestUtil.getMessageString("data.not.found.in.the.request"), null);	
					return pretupsResponse;
				}
			}

			if (jsonNode.has(LOGIN_ID) && !jsonNode.get(LOGIN_ID).isNull()
					&& !"".equalsIgnoreCase(jsonNode.get(LOGIN_ID).textValue())) {
				if (jsonNode.has(PASSWROD) && !jsonNode.get(PASSWROD).isNull()
						&& !"".equalsIgnoreCase(jsonNode.get(PASSWROD).textValue())) {
					if ("Y".equalsIgnoreCase(webServiceKeywordCacheVO.getIsRBARequired())) {
						return this.validateUserAndRoleCodeAndProcessRequest(jsonNode, con, pretupsResponse, webServiceKeywordCacheVO.getRoleCode());
					} else {
						return this.validateUserAndProcessRequest(jsonNode, con, pretupsResponse);
					}
				} else {
					pretupsResponse.setResponse(PretupsRestUtil.getMessageString("password.not.found.in.the.request"), null);	
				}
			} else if (jsonNode.has(EXTERNAL_CODE) && !jsonNode.get(EXTERNAL_CODE).isNull()
					&& !"".equalsIgnoreCase(jsonNode.get(EXTERNAL_CODE).textValue())) {
				if ("Y".equalsIgnoreCase(webServiceKeywordCacheVO.getIsRBARequired())) {
					return this.validateUserByExternalCodeAndRoleCodeAndProcessRequest(jsonNode, con, pretupsResponse, webServiceKeywordCacheVO.getRoleCode());
				} else {
					return this.validateUserByExternalCodeAndProcessRequest(jsonNode, con, pretupsResponse);
				}
			} else {
				pretupsResponse.setResponse(PretupsRestUtil.getMessageString("loginId.or.externalcode.not.available"), null);	
			} 
		} finally {
			if(mcomCon != null)
			{
				mcomCon.close("RestController#processPostRequest");
				mcomCon=null;
			}
			if (log.isDebugEnabled()) {
				log.debug("RestController#processPostRequest", PretupsI.EXITED);
			}
		}

		return pretupsResponse;

	}

	/**
	 * This method validate User by external code as well as check for Role  
	 *
	 * @param requestDataNode  Map object of request data
	 * @param connection Connection Object
	 * @param pretupsResponse = Pretupsresponse Object
	 * @return pretupsResponse PretupsResponse Object
	 * @throws IOException, RuntimeException, Exception
	 */
	private PretupsResponse<JsonNode> validateUserByExternalCodeAndRoleCodeAndProcessRequest(JsonNode requestDataNode, Connection connection, PretupsResponse<JsonNode> pretupsResponse, String roleCode) throws IOException, RuntimeException, Exception{
		if (log.isDebugEnabled()) {
			log.debug("RestController#validateUserByExternalCodeAndRoleCodeAndProcessRequest", PretupsI.ENTERED);
		}
		UserDAO userDAO = new UserDAO();
		ChannelUserVO channelUserVO = userDAO.loadAllUserDetailsByExternalCode(connection, requestDataNode.get(EXTERNAL_CODE).textValue());
		if(channelUserVO != null){
			if(this.checkForRoleAvailability(channelUserVO, connection, roleCode)){
				pretupsResponse = this.processRequestData(requestDataNode, pretupsResponse);
			}else{
				pretupsResponse.setResponse(PretupsRestUtil.getMessageString("not.authorize.to.perform.this.action"), null);	
			}
		}else{
			pretupsResponse.setResponse(PretupsRestUtil.getMessageString("user.does.not.exists.in.the.system"), null);
		}
		if (log.isDebugEnabled()) {
			log.debug("RestController#validateUserByExternalCodeAndRoleCodeAndProcessRequest", PretupsI.EXITED);
		}
		return pretupsResponse;
	}

	/**
	 * This method validate User by external code only and process request
	 *
	 * @param requestDataNode  Map object of request data
	 * @param connection Connection Object
	 * @param pretupsResponse = Pretupsresponse Object
	 * @return pretupsResponse PretupsResponse Object
	 * @throws IOException, RuntimeException, Exception
	 */
	private PretupsResponse<JsonNode> validateUserByExternalCodeAndProcessRequest(JsonNode requestDataNode, Connection connection, PretupsResponse<JsonNode> pretupsResponse) throws IOException, RuntimeException, Exception{
		if (log.isDebugEnabled()) {
			log.debug("RestController#validateUserByExternalCodeAndProcessRequest", PretupsI.ENTERED);
		}
		UserDAO userDAO = new UserDAO();
		ChannelUserVO channelUserVO = userDAO.loadAllUserDetailsByExternalCode(connection, requestDataNode.get(EXTERNAL_CODE).textValue());
		if(channelUserVO != null){
			pretupsResponse = this.processRequestData(requestDataNode, pretupsResponse);
		}else{
			pretupsResponse.setResponse(PretupsRestUtil.getMessageString("user.does.not.exists.in.the.system"), null);
		}
		if (log.isDebugEnabled()) {
			log.debug("RestController#validateUserByExternalCodeAndProcessRequest", PretupsI.EXITED);
		}
		return pretupsResponse;
	}

	/**
	 * This method validate User by credential as well as check role
	 *
	 * @param requestDataNode  Map object of request data
	 * @param connection Connection Object
	 * @param pretupsResponse = Pretupsresponse Object
	 * @return pretupsResponse PretupsResponse Object
	 * @throws IOException, RuntimeException, Exception
	 */
	private PretupsResponse<JsonNode> validateUserAndRoleCodeAndProcessRequest(JsonNode requestDataNode, Connection connection, PretupsResponse<JsonNode> pretupsResponse, String roleCode) throws IOException, RuntimeException, Exception{
		if (log.isDebugEnabled()) {
			log.debug("RestController#validateUserAndRoleCodeAndProcessRequest", PretupsI.ENTERED);
		}
		LoginDAO loginDAO = new LoginDAO();
		ChannelUserVO channelUserVO = loginDAO.loadUserDetails(connection, requestDataNode.get(LOGIN_ID).textValue(), requestDataNode.get(PASSWROD).textValue(), PretupsRestUtil.getSystemLocal());
		if(channelUserVO != null){
			if(!this.validatePassword(channelUserVO, pretupsResponse, requestDataNode)){
				return pretupsResponse;
			}
			if(this.checkForRoleAvailability(channelUserVO, connection, roleCode)){
				pretupsResponse = this.processRequestData(requestDataNode, pretupsResponse);
			}else{
				pretupsResponse.setResponse(PretupsRestUtil.getMessageString("not.authorize.to.perform.this.action"), null);
			}
		}else{
			pretupsResponse.setResponse(PretupsRestUtil.getMessageString("user.does.not.exists.in.the.system"), null);
		}
		if (log.isDebugEnabled()) {
			log.debug("RestController#validateUserAndRoleCodeAndProcessRequest", PretupsI.EXITED);
		}
		return pretupsResponse;
	}

	/**
	 * This method validate User by credential only
	 *
	 * @param requestDataNode  Map object of request data
	 * @param connection Connection Object
	 * @param pretupsResponse = Pretupsresponse Object
	 * @return pretupsResponse PretupsResponse Object
	 * @throws IOException, RuntimeException, Exception
	 */
	private PretupsResponse<JsonNode> validateUserAndProcessRequest(JsonNode requestDataNode, Connection connection, PretupsResponse<JsonNode> pretupsResponse) throws IOException, RuntimeException, Exception{
		if (log.isDebugEnabled()) {
			log.debug("RestController#validateUserAndProcessRequest", PretupsI.ENTERED);
		}
		LoginDAO loginDAO = new LoginDAO();
		ChannelUserVO channelUserVO = loginDAO.loadUserDetails(connection, requestDataNode.get(LOGIN_ID).textValue(), requestDataNode.get(PASSWROD).textValue(), PretupsRestUtil.getSystemLocal());

		if(channelUserVO != null){
			if(!this.validatePassword(channelUserVO, pretupsResponse, requestDataNode)){
				return pretupsResponse;
			}
			pretupsResponse = this.processRequestData(requestDataNode, pretupsResponse);
		}else{
			pretupsResponse.setResponse(PretupsRestUtil.getMessageString("user.does.not.exists.in.the.system"), null);
		}
		if (log.isDebugEnabled()) {
			log.debug("RestController#validateUserAndProcessRequest", PretupsI.EXITED);
		}
		return pretupsResponse;
	}


	/**
	 * This process response received by PretupsRestClient
	 *
	 * @param requestDataNode  Map object of request data
	 * @param pretupsResponse = Pretupsresponse Object
	 * @return pretupsResponse PretupsResponse Object
	 * @throws IOException, RuntimeException, Exception
	 */
	@SuppressWarnings("unchecked")
	private PretupsResponse<JsonNode> processRequestData(JsonNode requestDataNode, PretupsResponse<JsonNode> pretupsResponse) throws IOException, RuntimeException, Exception {
		if (log.isDebugEnabled()) {
			log.debug("RestController#processRequestData", PretupsI.ENTERED);
		}
		PretupsRestClient client = new PretupsRestClient();
		String responseString = client.postJSONRequest(requestDataNode, requestDataNode.get("type").textValue());

		pretupsResponse  = (PretupsResponse<JsonNode>) PretupsRestUtil.convertJSONToObject(responseString, new TypeReference<PretupsResponse<JsonNode>>() {});
		if(pretupsResponse.getFieldError() != null && pretupsResponse.getFieldError().size() <=0 ){
			pretupsResponse.setFieldError(null);
		}
		return pretupsResponse;
	}

	/**
	 * This method check for role availability for the user
	 *
	 * @param channelUserVO  ChannelUserVO object
	 * @param connection Connection Object
	 * @param type operation type
	 * @return Boolean if user is allowed to performthis opration return true else false
	 * @throws Exception
	 */
	private Boolean checkForRoleAvailability(ChannelUserVO channelUserVO, Connection connection, String roleCode) throws Exception{
		if (log.isDebugEnabled()) {
			log.debug("RestController#checkForRoleAvailability", PretupsI.ENTERED);
		}

		Boolean roleavailability;
		UserDAO userDAO = new UserDAO();
		if("Y".equalsIgnoreCase(channelUserVO.getCategoryVO().getFixedRoles())){
			roleavailability = userDAO.isFixedRoleAndExist(connection, channelUserVO.getCategoryCode(), roleCode, channelUserVO.getDomainTypeCode().toUpperCase());
		}else{
			roleavailability = userDAO.isAssignedRoleAndExist(connection, channelUserVO.getUserID(), roleCode, channelUserVO.getDomainTypeCode().toUpperCase());
		}
		if (log.isDebugEnabled()) {
			log.debug("RestController#checkForRoleAvailability", PretupsI.EXITED);
		}
		return roleavailability;
	}


	/**
	 * Validate Password for Channel user
	 * @param channelUserVO  ChannelUserVO object
	 * @param requestDataNode  Map object of request data
	 * @param pretupsResponse = Pretupsresponse Object
	 * @return Boolean

	 */
	private Boolean validatePassword(ChannelUserVO channelUserVO, PretupsResponse<JsonNode> pretupsResponse, JsonNode requestDataNode){
		if(!channelUserVO.getPassword().equals(BTSLUtil.encryptText(requestDataNode.get(PASSWROD).textValue()))){
			pretupsResponse.setResponse(PretupsRestUtil.getMessageString("userid.or.password.incorrect"), null);
			return false;
		}

		return true;
	}

}

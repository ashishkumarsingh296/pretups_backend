package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


import jakarta.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.springframework.web.bind.annotation.RequestBody;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.receiver.RestReceiver;
import com.btsl.pretups.channel.transfer.businesslogic.C2CRecentTxnRequestMessage;
import com.btsl.pretups.channel.transfer.businesslogic.C2CRecentUserBuyRequestParentVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserHierarchyVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRestI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

@Path("/chnluserenq")
@Tag(name = "${dummy.name}", description = "${dummy.desc}")//@Api(value="Channel User Purchase enquiry")
/*@RestController
@RequestMapping(value = "/chnluserenq")*/
public class C2CUserBuyEnquiryController implements ServiceKeywordControllerI {

	protected final Log _log = LogFactory.getLog(getClass().getName());
	Connection con = null;
	MComConnectionI mcomCon = null;
	@Context
 	private HttpServletRequest httpServletRequest;
	
	@POST
	@Path("/c2s-receiver/c2cbuyusenq")
 	/*@PostMapping("/c2s-receiver/c2cbuyusenq")
	@ResponseBody*/
	@Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
 	//@ApiOperation(value = "Channel User Info ", response = PretupsResponse.class)


	@io.swagger.v3.oas.annotations.Operation(summary = "${c2cbuyusenq.summary}", description="${c2cbuyusenq.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PretupsResponse.class))
							)
					}

					),


					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, description = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_DESC,  content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))

									 , examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.BAD_REQUEST)}

							)
					}),
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
							, examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.UNAUTH)}

									 )
					}),
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
							, examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.NOT_FOUND)}

							)
					}),
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								, examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)}
					)})
			}
	)




	public PretupsResponse<JsonNode> processCP2PUserRequest(@Parameter(description = SwaggerAPIDescriptionI.CHANNEL_USER_DETAILS) @RequestBody  C2CRecentUserBuyRequestParentVO c2CRecentUserBuyRequestParentVO, HttpServletRequest req) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
 		PretupsResponse<JsonNode> response;
         RestReceiver restReceiver;
         RestReceiver.updateRequestIdChannel();
         final String requestIDStr = String.valueOf(RestReceiver.getRequestIdChannel());
         restReceiver = new RestReceiver();
         response = restReceiver.processCP2PRequestOperator(req,(JsonNode)(PretupsRestUtil.convertJSONToObject(PretupsRestUtil.convertObjectToJSONString(c2CRecentUserBuyRequestParentVO), new TypeReference<JsonNode>(){})), PretupsRestI.C2C_BUY_ENQUIRY,requestIDStr);
         return response;
     }	
	private ArrayList<UserHierarchyVO> recentc2ctransactions(UserHierarchyVO userHierarchyVO) throws BTSLBaseException, Exception {

		final String methodName = "recentc2ctransactions";
		UserDAO userDao = new UserDAO();
		ArrayList<UserHierarchyVO> recenttxnList = null;
		
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			recenttxnList = userDao.recentC2cTxn(con,
					userHierarchyVO.getMsisdn(),userHierarchyVO.getLoginId());
		} catch (Exception e) {
			throw new BTSLBaseException(this, methodName, "Could not create Database connection", "Exception " + e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("C2CVoucherApprovalController#saveVoucherProductDetalis");
				mcomCon = null;
			}
		}
		return recenttxnList;

	}

	
	
	
	
	@Override
	public void process(RequestVO p_requestVO) {
		
		
		final String methodName = "process";
		
	
		PretupsResponse<JsonNode> jsonReponse = new PretupsResponse<JsonNode>();;
		JsonNode dataObject = null;
		HashMap responseMap = new HashMap();
		Gson gson = new Gson();
		UserHierarchyVO userHierarchyVO = new UserHierarchyVO();
		C2CRecentTxnRequestMessage reqMsgObj = null;
		ArrayList<UserHierarchyVO> recentC2cRes = null;
		StringBuffer responseStr = new StringBuffer("");
		
		
		try {
			HashMap reqMap = p_requestVO.getRequestMap();
			if (reqMap != null && (reqMap.get("MSISDN") != null || reqMap.get("LOGINID") != null) && p_requestVO.getRequestMessage() != null  &&  !p_requestVO.getRequestMessage().trim().startsWith("{")) {
				userHierarchyVO.setMsisdn((String) reqMap.get("MSISDN"));
				userHierarchyVO.setLoginId((String) reqMap.get("LOGINID"));
				if(BTSLUtil.isNullString(userHierarchyVO.getMsisdn()) && BTSLUtil.isNullString(userHierarchyVO.getLoginId()) ){
					p_requestVO.setSuccessTxn(false);
					p_requestVO.setSenderReturnMessage("Msisdn or loginId is required.");
					p_requestVO.setMessageCode(PretupsErrorCodesI.C2C_RECENT_TXN_FAILED);
					return;
				}
				recentC2cRes = recentc2ctransactions(userHierarchyVO);
				if ("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())){
					String resType = null;
					resType = reqMap.get("TYPE") + "RES";
					responseStr.append("{ \"type\": \"" + resType + "\" ,");
					responseStr.append(" \"txnStatus\": \"" + PretupsI.TXN_STATUS_SUCCESS + "\" ,");
					responseStr.append(" \"c2cRecentEnq\": [");
				}
				int i = 0;
			for (UserHierarchyVO resObj : recentC2cRes) {
				
				if ("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())) {
					
					i++;
					responseStr.append("{ \"userNamePrefix\": \"" + resObj.getUserNamePrefix() + "\" ,");
					responseStr.append("\"userNamePrefixCode\": \"" + resObj.getUserNamePrefixCode() + "\" ,");
					responseStr.append("\"firstName\":   \"" + resObj.getFirstName() + "\" , ");
					responseStr.append(" \"lastName\":    \"" + resObj.getLastName() + "\" , ");
					responseStr.append(" \"loginId\":   \"" + resObj.getLoginId() + "\" , ");
					responseStr.append(" \"categoryName\":   \"" + resObj.getCategoryName() + "\" , ");
					responseStr.append("   \"categoryCode\":  \"" + resObj.getCategoryCode() + "\",");
					if(i == recentC2cRes.size()){
						responseStr.append("   \"msisdn\":  \"" + resObj.getMsisdn() + "\" }");
					}
					else{
					responseStr.append("   \"msisdn\":  \"" + resObj.getMsisdn() + "\" },");
					}

				} 
				else {
					responseStr.append("<USERDETAIL>");
					responseStr.append("<MSISDN>" + resObj.getMsisdn() + "</MSISDN>");
					responseStr.append("<FIRSTNAME>" + resObj.getFirstName() + "</FIRSTNAME>");
					responseStr.append("<LASTNAME>" + resObj.getLastName() + "</LASTNAME>");
					responseStr.append("<CATEGORY_CODE>" + resObj.getCategoryCode() + "</CATEGORY_CODE>");
					responseStr.append("<CATEGORY_NAME>" + resObj.getCategoryName() + "</CATEGORY_NAME>");
					responseStr.append("<LOGINID>" + resObj.getLoginId() + "</LOGINID>");
					responseStr.append("<USERNAMEPREFIX>" + resObj.getUserNamePrefix() + "</USERNAMEPREFIX>");
					responseStr.append("<USERNAMEPREFIXCODE>" + resObj.getUserNamePrefixCode() + "</USERNAMEPREFIXCODE>");
					responseStr.append("</USERDETAIL>");
				}
			}
			if ("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())){
				responseStr.append("]}");
			}
			_log.debug("response ", "C2C Recent User responseStr  " + responseStr);

			responseMap.put("RESPONSE", responseStr);
			
			p_requestVO.setResponseMap(responseMap);
			}
			else{

				reqMsgObj = gson.fromJson(p_requestVO.getRequestMessage(), C2CRecentTxnRequestMessage.class);
				userHierarchyVO.setMsisdn(reqMsgObj.getMsisdn());
				userHierarchyVO.setLoginId(reqMsgObj.getLoginid());
				
				if(BTSLUtil.isNullString(userHierarchyVO.getMsisdn()) && BTSLUtil.isNullString(userHierarchyVO.getLoginId()) ){
					p_requestVO.setSuccessTxn(false);
					p_requestVO.setSenderReturnMessage("Msisdn or loginId is required.");
					p_requestVO.setMessageCode(PretupsErrorCodesI.C2C_RECENT_TXN_FAILED);
					return;
				}
				recentC2cRes = recentc2ctransactions(userHierarchyVO);
				dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(
						PretupsRestUtil.convertObjectToJSONString(recentC2cRes), new TypeReference<JsonNode>() {
						});

				jsonReponse.setDataObject(dataObject);

				p_requestVO.setJsonReponse(jsonReponse);

			
			}
			p_requestVO.setSuccessTxn(true);
			p_requestVO.setMessageCode(PretupsErrorCodesI.USER_HIERRACHY_SUCCESS);
			p_requestVO.setSenderReturnMessage("Transaction has been completed!");

				
		} catch (BTSLBaseException be) {
			p_requestVO.setSuccessTxn(false);
			p_requestVO.setSenderReturnMessage("Transaction has been failed!");

			 if ("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())) {
				String resType = null;
				HashMap reqMap = p_requestVO.getRequestMap();
				resType = reqMap.get("TYPE") + "RES";
				responseStr.append("{ \"Type\": \"" + resType + "\" ,");
				responseStr.append(" \"TxnStatus\": \"" + PretupsI.TXN_STATUS_FAIL + "\" ,");
				responseMap.put("RESPONSE", responseStr);

				p_requestVO.setResponseMap(responseMap);

			} 
			
			try {
				if (mcomCon != null) {
					mcomCon.finalRollback();
				}
			}

			catch (SQLException esql) {
				_log.error(methodName, "SQLException : ", esql.getMessage());
			}
			_log.error("process", "BTSLBaseException " + be.getMessage());
			if (be.getMessageList() != null && be.getMessageList().size() > 0) {
				final String[] array = {
						BTSLUtil.getMessage(p_requestVO.getLocale(), (ArrayList) be.getMessageList()) };
				p_requestVO.setMessageArguments(array);
			}
			if (be.getArgs() != null) {
				p_requestVO.setMessageArguments(be.getArgs());
			}

			if (be.getMessageKey() != null) {
				p_requestVO.setMessageCode(be.getMessageKey());
			} else {
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
			}
			_log.errorTrace(methodName, be);
			return;

		} catch (Exception e) {

			
			
			if ("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())) {
				responseStr.append("Exception occured - " + e);
				responseMap.put("RESPONSE", responseStr);

				p_requestVO.setResponseMap(responseMap);

			} 

			
			p_requestVO.setSenderReturnMessage("Transaction has been failed!");
			p_requestVO.setSuccessTxn(false);

			_log.error(methodName, "Exception " + e);
			_log.errorTrace(methodName, e);
			p_requestVO.setSuccessTxn(false);
			try {
				if (mcomCon != null) {
					mcomCon.finalRollback();
				}
			}

			catch (SQLException esql) {
				_log.error(methodName, "SQLException : ", esql.getMessage());
			}
			_log.error("process", "BTSLBaseException " + e.getMessage());
			_log.errorTrace(methodName, e);
			
			p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);

			return;
		}

	}

}

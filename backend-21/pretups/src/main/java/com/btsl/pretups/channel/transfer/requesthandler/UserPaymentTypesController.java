package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.btsl.common.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.receiver.RestReceiver;
import com.btsl.pretups.channel.transfer.businesslogic.UserPaymentRequestMessage;
import com.btsl.pretups.channel.transfer.businesslogic.UserPaymentRequestParentVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserPaymentVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserPaymentVO.ListValue;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRestI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.AESEncryptionUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

import io.swagger.v3.oas.annotations.tags.Tag;

import io.swagger.v3.oas.annotations.Parameter;



import io.swagger.v3.oas.annotations.Parameter;
/**
 * 
 * @author anshul.goyal2
 *
 */

/*@Path("")*/
@io.swagger.v3.oas.annotations.tags.Tag(name = "${UserPaymentTypesController.name}", description = "${UserPaymentTypesController.desc}")//@Api(tags= "C2S Receiver", value="User Payment Types")
@RestController
@RequestMapping(value = "/v1/c2sReceiver")
public class UserPaymentTypesController implements ServiceKeywordControllerI {

	protected final Log _log = LogFactory.getLog(getClass().getName());
	Connection con = null;
	MComConnectionI mcomCon = null;
	/*@Context
 	private HttpServletRequest httpServletRequest;*/
 	/*@POST
 	@Path("/usrpmtype")*/
	@PostMapping( value = "/usrpmtype", consumes = MediaType.APPLICATION_JSON, produces =MediaType.APPLICATION_JSON )
	@ResponseBody
   /* @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)*/
 	/*@ApiOperation(tags= "C2S Receiver", value = "User Payment Types", response = PretupsResponse.class,
 			authorizations = {
    	            @Authorization(value = "Authorization")})
	
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = PretupsResponse.class),
	        @ApiResponse(code = 201, message = "Created"),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })*/

	@Operation(summary = "${UserPaymentTypesController.processCP2PUserRequest.name}", description = "${UserPaymentTypesController.processCP2PUserRequest.desc}",

			responses = {
					@ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PretupsResponse.class))) }

					),

					@ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class))

									, examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST) }

							) }),
					@ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH) }

							) }),
					@ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND) }

							) }),
					@ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
									@ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR) }) }) })
	
     public PretupsResponse<JsonNode> processCP2PUserRequest(HttpServletRequest httpServletRequest,
    		 @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
    		 @Parameter(description = SwaggerAPIDescriptionI.USER_PMTYP)
    		 @RequestBody UserPaymentRequestParentVO userPaymentRequestParentVO, HttpServletResponse response1) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		
		final String methodName = "processCP2PUserRequest_UserPaymentTypesController";
 		PretupsResponse<JsonNode> response;
         RestReceiver restReceiver;
         RestReceiver.updateRequestIdChannel();
         final String requestIDStr = String.valueOf(RestReceiver.getRequestIdChannel());
         restReceiver = new RestReceiver();
         OAuthUser oAuthUser= null;
         OAuthUserData oAuthUserData =null;
         

         
         try {
 			/*
 			 * Authentication
 			 * @throws BTSLBaseException
 			 */
 			//OAuthenticationUtil.validateTokenApi(headers);
        	 
        	oAuthUser = new OAuthUser();
 			oAuthUserData =new OAuthUserData();
 			oAuthUser.setData(oAuthUserData);
 			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response1);
 			userPaymentRequestParentVO.setServicePort(oAuthUser.getServicePort());
 			userPaymentRequestParentVO.setReqGatewayCode(oAuthUser.getReqGatewayCode());
 			userPaymentRequestParentVO.setReqGatewayLoginId(oAuthUser.getReqGatewayLoginId());
 			userPaymentRequestParentVO.setReqGatewayPassword(oAuthUser.getReqGatewayPassword());
 			userPaymentRequestParentVO.setReqGatewayType(oAuthUser.getReqGatewayType());
 			userPaymentRequestParentVO.setSourceType(oAuthUser.getSourceType());
 			//password set from auth
 			userPaymentRequestParentVO.getData().setPassword( oAuthUser.getData().getPassword() );

 			
 			response = restReceiver.processCP2PRequestOperator(httpServletRequest,(JsonNode)(PretupsRestUtil.convertJSONToObject(PretupsRestUtil.convertObjectToJSONString(userPaymentRequestParentVO), new TypeReference<JsonNode>(){})), PretupsRestI.USERPMTYPE,requestIDStr);
         if(response.getStatusCode()!=200)
      	   response1.setStatus(HttpStatus.SC_BAD_REQUEST);
      if(response!=null && response.getDataObject()!=null && response.getDataObject().get("txnstatus") != null){
      	if(!response.getDataObject().get("txnstatus").textValue().equals("200"))
      		response1.setStatus(HttpStatus.SC_BAD_REQUEST);
      }
         return response;
         
         } catch (BTSLBaseException be) {
 			PretupsResponse<JsonNode> baseResponse= new PretupsResponse<JsonNode>() ;
 			
 			_log.error(methodName, "BTSLBaseException " + be.getMessage());
             _log.errorTrace(methodName, be);
             if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
               	 response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
               }
                else{
                response1.setStatus(HttpStatus.SC_BAD_REQUEST);
                }
             	baseResponse.setMessageCode(be.getMessageKey());
             	String resmsg = RestAPIStringParser.getMessage(
     					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessageKey(),
     					null);
   
             	baseResponse.setStatusCode(be.getErrorCode());
             	baseResponse.setMessage(resmsg);
                 return baseResponse;
             
         } catch (Exception e) {
         	PretupsResponse<JsonNode> baseResponse= new PretupsResponse<JsonNode>() ;
         	_log.error(methodName, "Exception " + e.getMessage());
             _log.errorTrace(methodName, e);
             baseResponse.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
             String resmsg = RestAPIStringParser.getMessage(
 					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.REQ_NOT_PROCESS,
 					null);
         	baseResponse.setStatusCode(PretupsI.UNABLE_TO_PROCESS_REQUEST);
         	baseResponse.setMessage(resmsg);
         	response1.setStatus(HttpStatus.SC_BAD_REQUEST);
             return baseResponse;
         }
 		
 		finally {
 			if (mcomCon != null) {
				mcomCon.close(methodName);
				mcomCon = null;
			}
             LogFactory.printLog(methodName, " Exited ", _log);
         }

     }
	
	
	@Override
	public void process(RequestVO p_requestVO) {
		final String methodName = "process";
		PretupsResponse<JsonNode> jsonReponse = new PretupsResponse<JsonNode>();;
		JsonNode dataObject = null;
		HashMap responseMap = new HashMap();
		Gson gson = new Gson();
		UserPaymentRequestMessage reqMsgObj = null;
		StringBuffer responseStr = new StringBuffer("");
		String userId = null;
		
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			HashMap reqMap = p_requestVO.getRequestMap();
			ChannelUserVO userVO = new ChannelUserVO();
			userId = p_requestVO.getActiverUserId();
			boolean isChannelUser = false;
			userVO = (ChannelUserVO) p_requestVO.getSenderVO();
			 ArrayList<ListValueVO> instTypeList = new ArrayList<ListValueVO>();
			 ArrayList<ListValueVO> instTypeListFinal = new ArrayList<ListValueVO>();
				reqMsgObj = gson.fromJson(p_requestVO.getRequestMessage(), UserPaymentRequestMessage.class);
				String type = reqMsgObj.getType();
				if(!PretupsI.C2C_MODULE.equals(type) &&  !PretupsI.CHANNEL_TYPE_O2C.equals(type)){
					p_requestVO.setSuccessTxn(false);
					p_requestVO.setSenderReturnMessage("Type is invalid");
					p_requestVO.setMessageCode(PretupsErrorCodesI.TYPE_INVALID);
					return;
				}
				if(PretupsI.CHANNEL_TYPE_C2C.equals(type) && PretupsI.OPERATOR_CATEGORY.equals(p_requestVO.getCategoryCode())){
					p_requestVO.setSuccessTxn(false);
					p_requestVO.setSenderReturnMessage("Type is invalid");
					p_requestVO.setMessageCode(PretupsErrorCodesI.TYPE_INVALID);
					return;
				}
				else if(PretupsI.CHANNEL_TYPE_O2C.equals(type)){
					isChannelUser = false;
					instTypeList = LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_INSTRUMENT_TYPE, true);
				}
				else{
					isChannelUser = true;  
					instTypeList = LookupsCache.loadLookupDropDown(PretupsI.C2C_PAYMENT_INSTRUMENT_TYPE, true);
				}
				
	    		String[] paymentTypes = null;
	    		UserDAO userDAO = new UserDAO();
	    		String paymentType = userDAO.getPaymentTypes(con,userId);
	            if(!BTSLUtil.isNullString(paymentType)) {
	            	paymentTypes = paymentType.split(",");                                                                
	            }
	            
	            if(userVO.isStaffUser())
	            {
	            	ChannelUserVO parentUserVO = new ChannelUserDAO().loadChannelUserByUserID(con, userVO.getUserID());
	            	if(!BTSLUtil.isNullString(parentUserVO.getPaymentTypes())) {
	                	paymentTypes = parentUserVO.getPaymentTypes().split(",");                                                                
	                }
	            }
	            instTypeListFinal = BTSLUtil.getInstrumentListForUser(instTypeList, paymentTypes,true);
	            ArrayList<UserPaymentVO> paymentTypeList = new ArrayList<UserPaymentVO>();
	           
	            	  for(int i =0;i<instTypeListFinal.size();i++){
	  	            	ListValueVO listValueVo  = (ListValueVO)(instTypeListFinal.get(i));
	  	            	UserPaymentVO userPaymentVO = new UserPaymentVO();
	  	            	if(listValueVo.getValue().equals("ONLINE")){
	  	            		List<ListValueVO> gatewayList=(LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_GATEWAY_TYPE, true));
	  	            		List<ListValue> gatewayLists = new ArrayList<>();
	  	            		for(int k=0;k<gatewayList.size();k++)
	  	            		{
	  	            			ListValue list = userPaymentVO.new ListValue();
	  	            			list.setCodeName(((ListValueVO)gatewayList.get(k)).getLabel());
	  	            			list.setValue(((ListValueVO)gatewayList.get(k)).getValue());
	  	            			gatewayLists.add(list);
	  	            		}
	  	            		userPaymentVO.setPaymentGatewayList(gatewayLists);
	  	            	}
	  	            	userPaymentVO.setPaymentCode(listValueVo.getValue());
	  	            	userPaymentVO.setPaymentType(listValueVo.getLabel());
	  	            	paymentTypeList.add(userPaymentVO);
	  	            }
	            
	            	
				dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(
						PretupsRestUtil.convertObjectToJSONString(paymentTypeList), new TypeReference<JsonNode>() {
						});
				jsonReponse.setDataObject(dataObject);
				p_requestVO.setJsonReponse(jsonReponse);
				p_requestVO.setSuccessTxn(true);
				p_requestVO.setMessageCode(PretupsErrorCodesI.PMTYPE_SUCCESS);
		
		} catch (BTSLBaseException be) {
			p_requestVO.setSuccessTxn(false);

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
			
			p_requestVO.setSenderReturnMessage("Enquiry is not successful!");
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
		finally{
		if (mcomCon != null) {
			mcomCon.close("UserPaymentTypesController#process");
			mcomCon = null;
		}
		p_requestVO.setSenderReturnMessage(BTSLUtil.getMessage(p_requestVO.getLocale(),p_requestVO.getMessageCode(),p_requestVO.getMessageArguments()));
		if (_log.isDebugEnabled()) {
			_log.debug("process", " Exited ");
		}
	
		}
	}

}

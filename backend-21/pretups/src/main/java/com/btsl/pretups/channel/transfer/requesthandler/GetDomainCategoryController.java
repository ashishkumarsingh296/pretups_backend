package com.btsl.pretups.channel.transfer.requesthandler;


import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

import java.io.IOException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.receiver.RestReceiver;
import com.btsl.pretups.channel.transfer.businesslogic.GetDomainCategoryRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.TotTrnxDetailMsg;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRestI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.GetDomainCategoryResponseVO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;


@io.swagger.v3.oas.annotations.tags.Tag(name = "${GetDomainCategoryController.name}", description = "${GetDomainCategoryController.desc}")//@Api(tags="C2S Receiver")
@RestController
@RequestMapping(value = "/v1/c2sReceiver")
public class GetDomainCategoryController implements ServiceKeywordControllerI {
	protected final Log _log = LogFactory.getLog(getClass().getName());
	Connection con = null;
	MComConnectionI mcomCon = null;
	/*@Context
 	private HttpServletRequest httpServletRequest;*/
 	/*//@POST
 	@Path("/getdomaincategory")*/
	
	@PostMapping(value= "/getdomaincategory", consumes=  MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON )
	@ResponseBody
    /*@Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)*/
 	/*@ApiOperation(tags="C2S Receiver", value = "Get Domain And Category", response = PretupsResponse.class,
 			authorizations = {
    	            @Authorization(value = "Authorization")
    })
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = PretupsResponse.class),
	        @ApiResponse(code = 201, message = "Created"),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${getdomaincategory.summary}", description="${getdomaincategory.description}",

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



	public PretupsResponse<JsonNode> processCP2PUserRequest(HttpServletRequest httpServletRequest,
			 @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			 @Parameter(description = SwaggerAPIDescriptionI.GET_DOMAIN_CATEGORY)
			 @RequestBody GetDomainCategoryRequestVO getDomainCategoryRequestVO, HttpServletResponse response1) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		
		final String methodName = "processCP2PUserRequest_GetDomainCategoryController";
 		
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
    	   
    	   	oAuthUser = new OAuthUser();
			oAuthUserData =new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response1);
			getDomainCategoryRequestVO.setServicePort(oAuthUser.getServicePort());
			getDomainCategoryRequestVO.setReqGatewayCode(oAuthUser.getReqGatewayCode());
			getDomainCategoryRequestVO.setReqGatewayLoginId(oAuthUser.getReqGatewayLoginId());
			getDomainCategoryRequestVO.setReqGatewayPassword(oAuthUser.getReqGatewayPassword());
			getDomainCategoryRequestVO.setReqGatewayType(oAuthUser.getReqGatewayType());
			getDomainCategoryRequestVO.setSourceType(oAuthUser.getSourceType());
			//password set from auth
			getDomainCategoryRequestVO.getData().setPassword( oAuthUser.getData().getPassword() );
			

			//OAuthenticationUtil.validateTokenApi(headers);
       response = restReceiver.processCP2PRequestOperator(httpServletRequest,(JsonNode)(PretupsRestUtil.convertJSONToObject(PretupsRestUtil.convertObjectToJSONString(getDomainCategoryRequestVO), new TypeReference<JsonNode>(){})), PretupsRestI.GETDOMAINCATEGORY,requestIDStr);
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
		_log.debug(methodName, "entered");
	
		String msisdn = "";
		String msisdn2 = "";
		String loginId = "";
		String extCode = "";
		String domainCode="";
		String domainCodeName="";
		
		
		Connection con = null;
		TotTrnxDetailMsg reqMsgObj = null;
		ChannelUserVO channelUserVO = null;
		ChannelUserVO user2= null;
        MComConnectionI mcomCon = null;
        UserDAO userDao = new UserDAO();
        Gson gson = new Gson();
		StringBuffer responseStr = new StringBuffer();
		
   
        HashMap responseMap = new HashMap<>();
		
		HashMap reqMap = p_requestVO.getRequestMap();
		try
		{
			if (reqMap != null && (reqMap.get("MSISDN") != null || reqMap.get("LOGINID") != null || reqMap.get("EXTCODE")!=null ) ) 
			{
				
				msisdn = (String) reqMap.get("MSISDN");
				loginId = (String) reqMap.get("LOGINID");
				msisdn2 =  (String) reqMap.get("MSISDN2");
				extCode=	(String) reqMap.get("EXTCODE");
			}
			else if ("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode()))
			{
				_log.debug(methodName, "getting msisdn in mobile gateway");
				
			}
			else
			{
				p_requestVO.setSenderReturnMessage("Msisdn or loginId is required.");
				p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_REQ_EITHER_MSISDN_LOGINID_REQ);
				return;
			}
			
			mcomCon = new MComConnection();
	        con=mcomCon.getConnection();
	    
	        if("REST".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())) {
	        	reqMsgObj = gson.fromJson(p_requestVO.getRequestMessage(), TotTrnxDetailMsg.class);
		          }
	        

	        if(!"".equals(msisdn))
	        {
	        	channelUserVO = userDao.loadUserDetailsByMsisdn(con, msisdn);
	        }
	        else if(!"".equals(loginId))
	        {
	        	channelUserVO = userDao.loadAllUserDetailsByLoginID(con, loginId);
	        	msisdn=channelUserVO.getMsisdn();
	        }
	        else if(!"".equals(extCode)) {
	        	channelUserVO = userDao.loadAllUserDetailsByExternalCode(con, extCode);
	        	msisdn=channelUserVO.getMsisdn();
	        }
	        
	        if(!BTSLUtil.isNullString(msisdn2)) {
	        	user2=userDao.loadUserDetailsByMsisdn(con, msisdn2);
	        	if(BTSLUtil.isNullObject(user2)) {
		        	
		        	p_requestVO.setSuccessTxn(false);
					p_requestVO.setMessageCode(PretupsErrorCodesI.NO_DOMAIN_FOUND);
					String[] errorArg = { msisdn2 };
					p_requestVO.setMessageArguments(errorArg);
					return;
		        }
	        	
	        }
	        
	        
	        if(!BTSLUtil.isNullObject(user2)) {
	        	domainCode=user2.getDomainID();
	        	domainCodeName=user2.getCategoryName();
	        }
	        else {
	        	if(BTSLUtil.isNullObject(channelUserVO)) {
	        		p_requestVO.setSuccessTxn(false);
					p_requestVO.setMessageCode(PretupsErrorCodesI.NO_DOMAIN_FOUND);
					
					if(!BTSLUtil.isNullString(msisdn)) {
						String[] errorArg = { msisdn };
						p_requestVO.setMessageArguments(errorArg);
					}else {
						String[] errorArg = { loginId };
						p_requestVO.setMessageArguments(errorArg);
					}
					
					return;
					
	        	}
	        	domainCode=channelUserVO.getDomainID();
	        	domainCodeName=channelUserVO.getCategoryName();
	        	
	        }
	        if(BTSLUtil.isNullString(domainCodeName)) {
	        	domainCodeName=userDao.getDomainName(con,domainCode);
	        	
	        }
	       
	        
	        GetDomainCategoryResponseVO DomainCategorylist = new GetDomainCategoryResponseVO();
	        DomainCategorylist=userDao.loadDomainCategory(con,domainCode,domainCodeName,p_requestVO);
	        
	    
        	HashMap<String, Object> resMap= new HashMap<>();
         
        	if(BTSLUtil.isNullOrEmptyList(DomainCategorylist.getCategoryList())) {
        		p_requestVO.setSuccessTxn(false);
 				p_requestVO.setMessageCode(PretupsErrorCodesI.NO_CATEGORYLIST);
 				return;
        		
        	}
        	
	        if("REST".equalsIgnoreCase(p_requestVO.getRequestGatewayCode()))
	        {
	        	_log.debug(methodName, "Preparing rest response");
	        	p_requestVO.setMessageCode(PretupsErrorCodesI.COMMISSION_SUCCESS);
	        	
	        	
	        	 resMap.put("map", DomainCategorylist);
	            if(!p_requestVO.getSenderReturnMessage().equals("NO Details Found For Input")) {
	            p_requestVO.setResponseMap(resMap);
	            p_requestVO.setSuccessTxn(true);
				p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_TRANSFER_SUCCESS);
				p_requestVO.setSenderReturnMessage("Enquiry has been successfully done!");}
	            else {
	            	p_requestVO.setResponseMap(resMap);
	            	p_requestVO.setSuccessTxn(false);
	 				p_requestVO.setMessageCode(PretupsErrorCodesI.NO_CATEGORYLIST);
	 				p_requestVO.setSenderReturnMessage("NO Details Found For Input");
	            	
	            }
	            
	       }
	        else if("MAPPGW".equalsIgnoreCase(p_requestVO.getRequestGatewayCode()))
	        {   _log.debug(methodName, "Preparing mobile app gateway response");
	       
	        	 	String resType = null;
					resType = reqMap.get("TYPE") + "RES";
					responseStr.append("{ \"type\": \"" + resType + "\" ,");
					responseStr.append(" \"txnStatus\": \"" + PretupsI.TXN_STATUS_SUCCESS + "\" ,");
					responseStr.append(" \"chnlTransferRules\": [");
					responseStr.append("{ \"domainCode\": \"" + DomainCategorylist.getDomainCode() + "\" ,");
					responseStr.append("\"domainCodeName\": \"" +DomainCategorylist.getDomainCodeName()+ "\" ,");
					responseStr.append("\"categoryList\": \"" +DomainCategorylist.getCategoryList()+ "\" ");
					responseStr.append("}]}");
	        	
	        	responseMap.put("RESPONSE", responseStr);
				p_requestVO.setResponseMap(responseMap);
				p_requestVO.setSuccessTxn(true);
				p_requestVO.setMessageCode("20000");
				p_requestVO.setSenderReturnMessage("Transaction has been completed!");


			} else if ("WEB".equals(p_requestVO.getRequestGatewayCode()))
				_log.debug(methodName, "Preparing web response");
				p_requestVO.setMessageCode(PretupsErrorCodesI.COMMISSION_SUCCESS);


				resMap.put("map", DomainCategorylist);
			if (!p_requestVO.getSenderReturnMessage().equals("NO Details Found For Input")) {
				p_requestVO.setResponseMap(resMap);
				p_requestVO.setSuccessTxn(true);
				p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_TRANSFER_SUCCESS);
				p_requestVO.setSenderReturnMessage("Enquiry has been successfully done!");
			} else {
				p_requestVO.setResponseMap(resMap);
				p_requestVO.setSuccessTxn(false);
				p_requestVO.setMessageCode(PretupsErrorCodesI.NO_CATEGORYLIST);
				p_requestVO.setSenderReturnMessage("NO Details Found For Input");

			}
		} catch (Exception e) {
			
			_log.debug(methodName, "In catch block");
      	  	_log.error(methodName, "Exception:e=" + e);
      	  p_requestVO.setSuccessTxn(false);
      	  p_requestVO.setMessageCode(PretupsErrorCodesI.NO_CATEGORYLIST);				
		}finally {
			

        	try {
        		if (mcomCon != null) {
        			mcomCon.close("GetDomainCategoryController#" + methodName);
        			mcomCon = null;
        		}
        	} 
        	catch (Exception e) {
        		_log.errorTrace(methodName, e);
        	}
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, " Exited ");
        	}
        
		}
		
		
	}

}

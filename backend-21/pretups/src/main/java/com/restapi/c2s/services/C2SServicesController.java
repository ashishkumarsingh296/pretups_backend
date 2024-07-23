package com.restapi.c2s.services;


import com.btsl.loadcontroller.InstanceLoadVO;
import com.btsl.loadcontroller.LoadControllerCache;
import com.btsl.loadcontroller.NetworkLoadVO;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import io.swagger.v3.oas.annotations.Parameter;

import java.io.*;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Parameter;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.services.businesslogic.SimProfileCache;
import com.btsl.ota.services.businesslogic.SimProfileVO;
import com.btsl.pretups.channel.logging.ChannelGatewayRequestLog;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.GatewayParsersI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.privaterecharge.businesslogic.PrivateRchrgDAO;
import com.btsl.pretups.privaterecharge.businesslogic.PrivateRchrgVO;
import com.btsl.pretups.receiver.FixedInformationVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.transfer.businesslogic.MessageFormater;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.AESEncryptionUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.OracleUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/*@Path("")*/
@io.swagger.v3.oas.annotations.tags.Tag(name = "${C2SServicesController.name}", description = "${C2SServicesController.desc}")//@Api(tags ="C2S Services", value="C2S Services")
@RestController
@RequestMapping(value = "/v1/c2sServices")
public class C2SServicesController {
	
	 @Context
	    private HttpServletRequest httpServletRequest;
	 /*@Context
	 	private HttpServletResponse response1;*/
	    private final Log log = LogFactory.getLog(this.getClass().getName());
	    private static final String PROCESSREQUEST = "C2SServicesController[processRequest]";
	    private static final String RSTRECEIVER = "RestReceiver";
	    private static final String TXNSTATUS = "TXNSTATUS";
	    private static final String DATE = "DATE";
	    private static final String MESSAGE = "MESSAGE";
	    private static final String GATEWAYTYPE = " For Gateway Type=";
	    private static final String MESSAGECODE = " Message Code=";
	    private static final String ARGS = "Args=";
	    private static final String SERVICEPORT = "Service Port=";
	    private static final String BTSLBASEEXP ="BTSLBaseException be:";
	    private static final String REQSTART =" requestStartTime=";
	    
	    private static long requestIdChannel = 0;
	   
	    @Autowired
		private C2SServiceI c2SServiceI;
	    @PostMapping(value="/rctrf", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@Consumes(value = MediaType.APPLICATION_JSON)
	    @Produces(MediaType.APPLICATION_JSON)*/
	    /*@ApiOperation(value = "Prepaid Recharge ", response = PretupsResponse.class,
	    		authorizations = {
	    	            @Authorization(value = "Authorization")
	    })
	    
	    @ApiResponses(value = {
		        @ApiResponse(code = 400, message = "Bad Request")
		        })
	    */

		@io.swagger.v3.oas.annotations.Operation(summary = "${rctrf.summary}", description="${rctrf.description}",

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
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						})
				}
		)



		public PretupsResponse<JsonNode> processRechargeRequest(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	            @Parameter(description = SwaggerAPIDescriptionI.C2S_RECHARGE, required = true)
	            @RequestBody C2SRechargeRequestVO requestVO,HttpServletResponse response1 ) throws Exception {
	        PretupsResponse<JsonNode> response;
	        ++requestIdChannel;
			String methodName= "processRechargeRequest";
	        String pin = requestVO.getData().getPin();
	        final String requestIDStr = String.valueOf(requestIdChannel);
			Boolean processOnRemoteInstance = true;
			response = new PretupsResponse<>();

			//process recharge on txn remote
				log.info("processRechargeRequest", "## START REMOTE REQUEST");
				String txnID="NA",message="NA",txnStatus="NA";
				try {

					OAuthenticationUtil.validateTokenApi(requestVO, headers, response1);
					String responseXML = sendRequestRemote(requestVO);

					HashMap responsemap = BTSLUtil.getStringToHash(responseXML, "&", "=");
					txnStatus = (String)responsemap.get("TXN_STATUS");
					message = ((String)responsemap.get("MESSAGE")).replace("+"," ");
					txnID = (String)responsemap.get("TXN_ID");

				}catch(Exception e){log.errorTrace(methodName,e);}


				JsonObject json = new JsonObject();
				String responseStr = null;
				json.addProperty("txnstatus", txnStatus);
				json.addProperty("message", message);
				json.addProperty("txnid", txnID);
				json.addProperty("receivertrfvalue", "0");
				json.addProperty("receiveraccessval", "0");
				final java.util.Date date = new java.util.Date();
				final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
				json.addProperty("date", sdf.format(date));

				responseStr = json.toString();
				log.debug("processRechargeRequest", " ## responseJson: " + responseStr);
				int status = (PretupsI.TXN_STATUS_SUCCESS.equals(txnStatus)) ? PretupsI.RESPONSE_SUCCESS : PretupsI.RESPONSE_FAIL;
				response.setDataObject(status, false, (JsonNode) PretupsRestUtil
						.convertJSONToObject(responseStr, new TypeReference<JsonNode>() {
						}));

				log.info("processRechargeRequest", "## END REMOTE REQUEST");


			//to process on this instance
			//response = processRequestChannel(requestVO, "rctrf".toUpperCase(),requestIDStr,headers,response1,pin);


			if(response!=null && response.getDataObject()!=null && response.getDataObject().get("txnstatus") != null){
					if(!response.getDataObject().get("txnstatus").textValue().equals("200"))
						response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
			else
			{
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
			}

	        return response;
	    }
	    
	    @PostMapping(value="/postpaid", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	   /* @Consumes(value = MediaType.APPLICATION_JSON)
	    @Produces(MediaType.APPLICATION_JSON)*/
	    /*@ApiOperation(value = "Post Paid Bill Payment ", response = PretupsResponse.class ,
	    		authorizations = {
	    	            @Authorization(value = "Authorization")
	    })
	    
	    @ApiResponses(value = {
		        @ApiResponse(code = 400, message = "Bad Request")
		        })
	    */

		@io.swagger.v3.oas.annotations.Operation(summary = "${postpaid.summary}", description="${postpaid.description}",

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
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						})
				}
		)



		public PretupsResponse<JsonNode> processPostPaidBillRequest(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	            @Parameter(description = SwaggerAPIDescriptionI.POST_PAID_BILL, required = true)
	            @RequestBody C2SRechargeRequestVO requestVO,HttpServletResponse response1) throws Exception {
	        PretupsResponse<JsonNode> response;
	        ++requestIdChannel;
	        final String requestIDStr = String.valueOf(requestIdChannel);
	        String pin = requestVO.getData().getPin();
	        response = processRequestChannel(requestVO, "postpaid".toUpperCase(),requestIDStr,headers,response1,pin);
	        if(response!=null && response.getDataObject()!=null && response.getDataObject().get("txnstatus") != null){
	        	if(!response.getDataObject().get("txnstatus").textValue().equals("200"))
	        		response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	        }
	        else
	        {
	        	response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
	        }
	        return response;
	    }
	    
	    @PostMapping(value = "/evd", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@ApiOperation(value = "Electronic Voucher Distribution ", response = PretupsResponse.class ,
	    		authorizations = {
	    	            @Authorization(value = "Authorization")
	    })
	    
	    @ApiResponses(value = {
		        @ApiResponse(code = 400, message = "Bad Request")
		        })
	    */

		@io.swagger.v3.oas.annotations.Operation(summary = "${evd.summary}", description="${evd.description}",

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
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						})
				}
		)



		public PretupsResponse<JsonNode> processEVDRequest(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	            @Parameter(description = SwaggerAPIDescriptionI.EVD, required = true)
	            @RequestBody C2SRechargeRequestVO requestVO,HttpServletResponse response1) throws Exception {
	        PretupsResponse<JsonNode> response;
	        ++requestIdChannel;
	        final String requestIDStr = String.valueOf(requestIdChannel);
	        String pin = requestVO.getData().getPin();
	        response = processRequestChannel(requestVO, "evd".toUpperCase(),requestIDStr,headers,response1,pin);
	        if(response!=null && response.getDataObject()!=null && response.getDataObject().get("txnstatus") != null){
	        	if(!response.getDataObject().get("txnstatus").textValue().equals("200"))
	        		response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	        }
	        else
	        {
	        	response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
	        }
	        return response;
	    }
	    

	    @PostMapping(value = "/mvd", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@ApiOperation(value = "Multiple Voucher Distribution ", response = PretupsResponse.class ,
	    		authorizations = {
	    	            @Authorization(value = "Authorization")
	    })
	    
	    @ApiResponses(value = {
		        @ApiResponse(code = 400, message = "Bad Request")
		        })
	    */

		@io.swagger.v3.oas.annotations.Operation(summary = "${mvd.summary}", description="${mvd.description}",

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
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						})
				}
		)



		public PretupsResponse<JsonNode> processMVDRequest(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	            @Parameter(description = SwaggerAPIDescriptionI.MVD, required = true)
	            @RequestBody MvdRequestVO requestVO,HttpServletResponse response1) throws Exception {
	        PretupsResponse<JsonNode> response;
	        ++requestIdChannel;
	        final String requestIDStr = String.valueOf(requestIdChannel);
	        String pin = requestVO.getData().getPin();
	        C2SRechargeRequestVO requestVO1 = new C2SRechargeRequestVO();
	        prepareMvdVO(requestVO1,requestVO);
	        response = processRequestChannel(requestVO1, "mvd".toUpperCase(),requestIDStr,headers,response1,pin);
	        if(response!=null && response.getDataObject()!=null && response.getDataObject().get("txnstatus") != null){
	        	if(!response.getDataObject().get("txnstatus").textValue().equals("200"))
	        	{
	        		response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	        		response.setStatusCode(HttpStatus.SC_BAD_REQUEST); 	
	        	}
	        }
	        else
	        {
	        	response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
	        }
	        return response;
	    }
	    @PostMapping(value = "/grc", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@ApiOperation(value = "Gift Recharge", response = PretupsResponse.class ,
	    		authorizations = {
	    	            @Authorization(value = "Authorization")
	    })
	    
	    @ApiResponses(value = {
		        @ApiResponse(code = 400, message = "Bad Request")
		        })
	    */

		@io.swagger.v3.oas.annotations.Operation(summary = "${grc.summary}", description="${grc.description}",

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
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						})
				}
		)



		public PretupsResponse<JsonNode> processGiftRechargeRequest(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	            @Parameter(description = SwaggerAPIDescriptionI.GIFT_RECHARGE, required = true)
	            @RequestBody GiftRechargeRequestVO requestVO,HttpServletResponse response1) throws Exception {
	        PretupsResponse<JsonNode> response;
	        ++requestIdChannel;
	        C2SRechargeRequestVO requestVO1 = new C2SRechargeRequestVO();
	        prepareC2SVoFromGiftVO(requestVO1,requestVO);
	        String pin = requestVO.getData().getPin();
	        final String requestIDStr = String.valueOf(requestIdChannel);
	        response = processRequestChannel(requestVO1, "grc".toUpperCase(),requestIDStr,headers,response1,pin);
	        if(response!=null && response.getDataObject()!=null && response.getDataObject().get("txnstatus") != null){
	        	if(!response.getDataObject().get("txnstatus").textValue().equals("200"))
	        		response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	        }
	        else
	        {
	        	response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
	        }
	        
	        return response;
	    }
	    
	    @PostMapping(value = "/c2sintrrc", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@ApiOperation(value = "Internet Recharge", response = PretupsResponse.class ,
	    		authorizations = {
	    	            @Authorization(value = "Authorization")
	    })
	    
	    @ApiResponses(value = {
		        @ApiResponse(code = 400, message = "Bad Request")
		        })
	    */

		@io.swagger.v3.oas.annotations.Operation(summary = "${c2sintrrc.summary}", description="${c2sintrrc.description}",

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
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						})
				}
		)


		public PretupsResponse<JsonNode> processInternetRechargeRequest(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	            @Parameter(description = SwaggerAPIDescriptionI.INTERNET_RECHARGE, required = true)
	            @RequestBody InternetRechargeRequestVO requestVO,HttpServletResponse response1) throws Exception {
	        PretupsResponse<JsonNode> response;
	        ++requestIdChannel;
	        C2SRechargeRequestVO requestVO1 = new C2SRechargeRequestVO();
	        prepareC2SVoFromInternetVO(requestVO1,requestVO);
	        String pin = requestVO.getData().getPin();
	        final String requestIDStr = String.valueOf(requestIdChannel);
	        response = processRequestChannel(requestVO1, "c2sintrrc".toUpperCase(),requestIDStr,headers,response1,pin);
	        if(response!=null && response.getDataObject()!=null && response.getDataObject().get("txnstatus") != null){
	        	if(!response.getDataObject().get("txnstatus").textValue().equals("200"))
	        		response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	        }
	        else
	        {
	        	response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
	        }
	        
	        return response;
	    }
	 
	    @PostMapping(value = "/fixlinerc", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@ApiOperation(value = "Fix Line Recharge", response = PretupsResponse.class ,
	    		authorizations = {
	    	            @Authorization(value = "Authorization")
	    })
	    
	    @ApiResponses(value = {
		        @ApiResponse(code = 400, message = "Bad Request")
		        })
	    */

		@io.swagger.v3.oas.annotations.Operation(summary = "${fixlinerc.summary}", description="${fixlinerc.description}",

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
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						})
				}
		)


		public PretupsResponse<JsonNode> processFixLineRechargeRequest(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	            @Parameter(description = SwaggerAPIDescriptionI.FIXLINE_RECHARGE, required = true)
	            @RequestBody InternetRechargeRequestVO requestVO,HttpServletResponse response1) throws Exception {
	        PretupsResponse<JsonNode> response;
	        ++requestIdChannel;
	        C2SRechargeRequestVO requestVO1 = new C2SRechargeRequestVO();
	        prepareC2SVoFromInternetVO(requestVO1,requestVO);
	        String pin = requestVO.getData().getPin();
	        final String requestIDStr = String.valueOf(requestIdChannel);
	        response = processRequestChannel(requestVO1, "fixlinerc".toUpperCase(),requestIDStr,headers, response1,pin);
	        if(response!=null && response.getDataObject()!=null && response.getDataObject().get("txnstatus") != null){
	        	if(!response.getDataObject().get("txnstatus").textValue().equals("200"))
	        		response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	        }
	        else
	        {
	        	response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
	        }
	        
	        return response;
	    }
	 private PretupsResponse<JsonNode> processRequestChannel(C2SRechargeRequestVO requestVO1, String serviceKeyword, String requestIdChannel ,MultiValueMap<String, String> headers,HttpServletResponse response1,String pin)  {
	        PretupsResponse<JsonNode> response = new PretupsResponse<>();
	        final String methodName = "processRequestChannel";
	        StringBuilder loggerValue= new StringBuilder(); 
	        StringBuilder eventhandler= new StringBuilder(); 
	        final RequestVO requestVO = new RequestVO();
	        String message = null;
	        Connection con = null;MComConnectionI mcomCon = null;
	        NetworkPrefixVO networkPrefixVO = null;
	        ChannelUserVO channelUserVO = null;
	        final Date currentDate = new Date();
	        SimProfileVO simProfileVO = null;
	        final long requestStartTime = System.currentTimeMillis();
	        long requestEndTime = 0;
	        GatewayParsersI gatewayParsersObj = null;
	        String networkID = null;
	        String externalInterfaceAllowed = null;
	        String filteredMSISDN= null;
	        String ERROR =null;
	        try {
	            String instanceCode = Constants.getProperty("INSTANCE_ID");
	            //requestVO.setReqContentType(httpServletRequest.getContentType());
	            requestVO.setModule(PretupsI.C2S_MODULE);
	            requestVO.setInstanceID(instanceCode);
	            requestVO.setCreatedOn(currentDate);
	            requestVO.setLocale(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
	            requestVO.setDecreaseLoadCounters(false);
	            requestVO.setRequestStartTime(requestStartTime);
	            requestVO.setServiceKeyword(serviceKeyword);
	            requestVO.setActionValue(PretupsI.CHANNEL_RECEIVER_ACTION);
	            mcomCon = new MComConnection();con=mcomCon.getConnection();
	            /*
				 * Authentication
				 * @throws BTSLBaseException
				 */
	 	        OAuthenticationUtil.validateTokenApi(requestVO1, headers,response1);
//	 	        String msisdn = requestVO1.getData().getMsisdn();
				
	 	        String loginId = requestVO1.getData().getLoginid();
	 	        ChannelUserVO senderVO = new UserDAO().loadAllUserDetailsByLoginID(con, loginId);
	 	        UserPhoneVO phoneVO = new UserDAO().loadUserPhoneVO(con, senderVO.getUserID());
	 	        senderVO.setUserPhoneVO(phoneVO);
	 	        
				final PrivateRchrgDAO prdao = new PrivateRchrgDAO();
				String prvtRcMsisdnPrefixList = (String) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.PRVT_RC_MSISDN_PREFIX_LIST);
				PrivateRchrgVO prvo = null;
				final String sidprefixes = prvtRcMsisdnPrefixList;
				final String[] sidprefix = sidprefixes.split(",");
				for (int i = 0; i < sidprefix.length; i++) {
					if (requestVO1.getData().getMsisdn2().startsWith(sidprefix[i].trim())) {
						prvo = prdao.loadUserDetailsBySID(con, requestVO1.getData().getMsisdn2());
						try {
							String checkPin =AESEncryptionUtil.aesDecryptor(pin, Constants.A_KEY);
							if(!checkPin.equals(requestVO1.getData().getPin())){
								throw new BTSLBaseException(PretupsErrorCodesI.INVALID_PIN1);
							}
							if (!BTSLUtil.isNullObject(prvo)) {
								requestVO1.getData().setMsisdn2(prvo.getMsisdn());
							} else {
								throw new BTSLBaseException(PretupsErrorCodesI.NO_MSISDN_EXISTS);
							}
						} catch (BTSLBaseException be) {
							if (be.isKey()
									&& ((be.getMessageKey().equals(PretupsErrorCodesI.NO_MSISDN_EXISTS)))) {
								OracleUtil.commit(con);
							}
							JsonObject json = new JsonObject();
							String responseStr = null;
							json.addProperty("txnstatus", "206");

							final java.util.Date date = new java.util.Date();
							final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
							json.addProperty("date", sdf.format(date));
							json.addProperty("message", PretupsRestUtil.getMessageString(be.getMessageKey(), null));

							responseStr = json.toString();
							response.setDataObject(PretupsI.RESPONSE_FAIL, false, (JsonNode) PretupsRestUtil
									.convertJSONToObject(responseStr, new TypeReference<JsonNode>() {
									}));

							return response;
						}
					}

				}
	 	        
	 	       UserPhoneVO senderPhoneVO = new UserPhoneVO();
	 	        if(senderVO.getUserType().equalsIgnoreCase(PretupsI.STAFF_USER_TYPE)) {
	 	        	settingStaffDetails(senderVO);
	 	        	if(senderVO.getUserPhoneVO() != null && PretupsI.NOT_AVAILABLE.equalsIgnoreCase(senderVO.getMsisdn())) {
	 	        		senderPhoneVO = new UserDAO().loadUserPhoneVO(con, senderVO.getUserID());//here parent id of staff will come
	 	        		senderVO.setUserPhoneVO(senderPhoneVO);
	 	        	}
	 	        }
	 	        
	 	        if(requestVO1.getData().getPin() == null) {
	 	        	requestVO1.getData().setPin(BTSLUtil.decryptText(senderPhoneVO.getSmsPin()));
	 	        	requestVO1.getData().setPassword(BTSLUtil.decryptText(senderVO.getPassword()));
	 	        	requestVO1.getData().setMsisdn(senderPhoneVO.getMsisdn());
	 	        }
	 	        
	 	       if (senderVO.getPinRequired().equals(PretupsI.YES)) {
					try {
						if(!BTSLUtil.isNullString(pin))
						ChannelUserBL.validatePIN(con, senderVO, pin);
						else{
							throw new BTSLBaseException(PretupsErrorCodesI.CHNL_ERROR_SNDR_BLANK_PIN);
						}
					} catch (BTSLBaseException be) {
						if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN))
								|| (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
							OracleUtil.commit(con);
						}
			             JsonObject json = new JsonObject();
			            String responseStr=null;
			                     json.addProperty("txnstatus", "206");
			                 
			                     final java.util.Date date = new java.util.Date();
			                     final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
			                 json.addProperty("date", sdf.format(date));
			                 json.addProperty("message",
			                         PretupsRestUtil.getMessageString(be.getMessageKey(), null));
			             
			             responseStr = json.toString();
			             response.setDataObject(PretupsI.RESPONSE_FAIL, false,
			                     (JsonNode) PretupsRestUtil.convertJSONToObject(responseStr, new TypeReference<JsonNode>() {
			                     }));
						
						return response;
					}
				}
	            ObjectMapper mapper = new ObjectMapper();
	            JsonNode request = mapper.valueToTree(requestVO1);;
	            requestVO.setRequestMessageOrigStr(request.toString());
	            requestVO.setLogin(requestVO1.getReqGatewayLoginId());
	            
	            
	            response = parseRequestfromJson(request, requestVO);
	            if (!response.getStatus()) {
	                return response;
	            }
	            
	            PretupsBL.validateRequestMessageGateway(requestVO);

	            gatewayParsersObj = (GatewayParsersI) PretupsBL.getGatewayHandlerObj(requestVO.getMessageGatewayVO()
	                    .getHandlerClass());

	            response = parseRequestForMessage(request, requestVO);
	            if (!response.getStatus()) {
	                return response;
	            }

	            ChannelGatewayRequestLog.inLog(requestVO);
	          
	            gatewayParsersObj.parseChannelRequestMessage(requestVO, con);

	            gatewayParsersObj.validateUserIdentification(requestVO);
	            filteredMSISDN = requestVO.getFilteredMSISDN();
	            if (!PretupsI.STATUS_ACTIVE.equals(requestVO.getMessageGatewayVO().getStatus())) {
	                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.MESSAGE_GATEWAY_NOT_ACTIVE);
	            }
	            if (!PretupsI.STATUS_ACTIVE.equals(requestVO.getMessageGatewayVO().getRequestGatewayVO().getStatus())) {
	                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.REQ_MESSAGE_GATEWAY_NOT_ACTIVE);
	            }

	            gatewayParsersObj.loadValidateNetworkDetails(requestVO);

	            networkPrefixVO = (NetworkPrefixVO) requestVO.getValueObject();

	            networkID = networkPrefixVO.getNetworkCode();
	            requestVO.setRequestNetworkCode(networkID);

	            String requestHandlerClass;

	            channelUserVO = gatewayParsersObj.loadValidateUserDetails(con, requestVO);

	            if (requestVO.getMessageGatewayVO().getAccessFrom() == null
	                    || requestVO.getMessageGatewayVO().getAccessFrom().equals(PretupsI.ACCESS_FROM_PHONE)) {
	                PretupsBL
	                        .unBarredUserAutomaic(con, channelUserVO.getUserPhoneVO().getMsisdn(),
	                                networkPrefixVO.getNetworkCode(), PretupsI.C2S_MODULE, PretupsI.USER_TYPE_SENDER,
	                                channelUserVO);
	            }
	            final BarredUserDAO barredUserDAO = new BarredUserDAO();
	            
	            String requesterMsisdn = requestVO.getRequestMSISDN();
				
				if (barredUserDAO.isExists(con, PretupsI.C2S_MODULE, networkPrefixVO.getNetworkCode(), requesterMsisdn,
						PretupsI.USER_TYPE_SENDER, null)) {
					throw new BTSLBaseException("C2CTrfInitiateController", "process",
							PretupsErrorCodesI.ERROR_USER_TRANSFER_CHNL_SENDER_BAR, 0,
							new String[] { requesterMsisdn }, null);
				}

	            channelUserVO.setModifiedOn(currentDate);
	            final UserPhoneVO userPhoneVO = channelUserVO.getUserPhoneVO();
	            userPhoneVO.setModifiedBy(channelUserVO.getUserID());
	            userPhoneVO.setModifiedOn(currentDate);
	            if (channelUserVO.isStaffUser() && PretupsI.NOT_AVAILABLE.equals(userPhoneVO.getSmsPin())) {
	                final ChannelUserVO parentVO = (ChannelUserVO) requestVO.getSenderVO();
	                parentVO.setModifiedOn(currentDate);
	                final UserPhoneVO parentPhoneVO = parentVO.getUserPhoneVO();
	                parentPhoneVO.setModifiedBy(channelUserVO.getUserID());
	                parentPhoneVO.setModifiedOn(currentDate);
	            }
	            
	            if (userPhoneVO.getLastAccessOn() == null) {
	                userPhoneVO.setAccessOn(true);
	            }
	            if (requestVO.getMessageGatewayVO().getAccessFrom().equals(PretupsI.ACCESS_FROM_PHONE)) {
	                userPhoneVO.setLastAccessOn(currentDate);
	            }
	            LogFactory.printLog(methodName, "Sender Locale in Request if any" + requestVO.getSenderLocale(), log);
	            if (requestVO.getSenderLocale() != null) {
	                requestVO.setLocale(requestVO.getSenderLocale());
	            } else {
	                requestVO.setSenderLocale(requestVO.getLocale());
	            }

	            if (!requestVO.isMessageAlreadyParsed()) {
	                PretupsBL.isPlainMessageAndAllowed(requestVO);

	                if (!requestVO.isPlainMessage()) {
	                    PretupsBL.getEncryptionKeyForUser(con, requestVO);
	                    simProfileVO = SimProfileCache.getSimProfileDetails(userPhoneVO.getSimProfileID());
	                    PretupsBL.parseBinaryMessage(requestVO, simProfileVO);
	                }
	            }
	            try {
					gatewayParsersObj.checkRequestUnderProcess(con, requestVO, PretupsI.C2S_MODULE, true, channelUserVO);
				} catch (BTSLBaseException e1) {
					log.errorTrace(methodName, e1);
					throw e1;
				} finally {
					mcomCon.finalCommit();
				}
	            if(mcomCon != null)
	            {
	            	mcomCon.close("RestReceiver#processRequestChannel");
	            	mcomCon=null;
	            }
	            MessageFormater.handleChannelMessageFormat(requestVO, channelUserVO);

	            populateLanguageSettings(requestVO, channelUserVO);
	            final ServiceKeywordCacheVO serviceKeywordCacheVO = ServiceKeywordCache.getServiceKeywordObj(requestVO);

	            if (serviceKeywordCacheVO == null) {
	            	eventhandler.setLength(0);
	            	eventhandler.append("Service keyword not found for the keyword=");
	            	eventhandler.append(requestVO.getRequestMessageArray()[0]);
	            	eventhandler.append(GATEWAYTYPE);
	            	eventhandler.append(requestVO.getRequestGatewayType());
	            	eventhandler.append(SERVICEPORT);
	            	eventhandler.append(requestVO.getServicePort());
	                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED,
	                        EventLevelI.INFO, PROCESSREQUEST, null, filteredMSISDN, "",eventhandler.toString());
	                throw new BTSLBaseException(RSTRECEIVER, methodName,
	                        PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD);
	            } else if (serviceKeywordCacheVO.getStatus().equals(PretupsI.SUSPEND)) {
	            	eventhandler.setLength(0);
	            	eventhandler.append("Service keyword suspended for the keyword=");
	            	eventhandler.append(requestVO.getRequestMessageArray()[0]);
	            	eventhandler.append(GATEWAYTYPE);
	            	eventhandler.append(requestVO.getRequestGatewayType());
	            	eventhandler.append(SERVICEPORT);
	            	eventhandler.append(requestVO.getServicePort());
	                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED,
	                        EventLevelI.INFO, PROCESSREQUEST, null, filteredMSISDN, "",eventhandler.toString());
	                throw new BTSLBaseException(RSTRECEIVER, methodName,
	                        PretupsErrorCodesI.C2S_ERROR_NOTFOUND_SERVICEKEYWORD_SUSPEND);
	            }
	            requestHandlerClass = serviceKeywordCacheVO.getRequestHandlerClass();
	            requestVO.setServiceType(serviceKeywordCacheVO.getServiceType());
	            requestVO.setType(serviceKeywordCacheVO.getType());
	            requestVO.setActualMessageFormat(serviceKeywordCacheVO.getMessageFormat());
	            requestVO.setUseInterfaceLanguage(serviceKeywordCacheVO.getUseInterfaceLanguage());

	            channelUserVO.setServiceTypes(requestVO.getServiceType());
	            response = validateServiceType(requestVO, serviceKeywordCacheVO, channelUserVO);
	            if (!response.getStatus()) {
	                return response;
	            }
	            externalInterfaceAllowed = serviceKeywordCacheVO.getExternalInterface();
	            requestVO.setGroupType(serviceKeywordCacheVO.getGroupType());

	            final ServiceKeywordControllerI controllerObj = (ServiceKeywordControllerI) PretupsBL
	                    .getServiceKeywordHandlerObj(requestHandlerClass);
	            //added 23-11-2021
//	            requestVO.setRequestGatewayCode(PretupsI.GATEWAY_TYPE_WEB);
//            	requestVO.setRequestGatewayType(PretupsI.GATEWAY_TYPE_WEB);
//            	requestVO.setSourceType(PretupsI.REQUEST_SOURCE_WEB);
            	//end
	            controllerObj.process(requestVO);


	        } catch (BTSLBaseException be) {
	            log.errorTrace(methodName, be);
	            requestVO.setSuccessTxn(false);
	            if (log.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append(BTSLBASEEXP);
	            	loggerValue.append(be.getMessage());
	                log.debug(methodName,  loggerValue);
	            }
	            if (!BTSLUtil.isNullString(requestVO.getSenderReturnMessage())) {
	                message = requestVO.getSenderReturnMessage();
	            }
	            if (be.isKey()) {
	            	ERROR=be.getMessageKey();
	                response.setMessageCode(be.getMessageKey());
	                response.setResponse(PretupsI.RESPONSE_SUCCESS, true,
	                        PretupsRestUtil.getMessageString(be.getMessageKey(), be.getArgs()));
	                requestVO.setMessageCode(be.getMessageKey());
	                requestVO.setMessageArguments(be.getArgs());
	            } else {
	                requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
	            }
	            
	            

	        } catch (Exception e) {
	            requestVO.setSuccessTxn(false);
	            loggerValue.setLength(0);
	            loggerValue.append("Exception e:");
	            loggerValue.append(e.getMessage());
	            log.error(methodName, loggerValue );
	            log.errorTrace(methodName, e);
	            if (BTSLUtil.isNullString(requestVO.getMessageCode())) {
	                requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
	            }
	            eventhandler.setLength(0);
	            eventhandler.append("Exception in ChannelReceiver:");
	            eventhandler.append(e.getMessage());
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                    PROCESSREQUEST, "", "", "",  eventhandler.toString());
	            
	            
	            
	            
	        } finally {

	            if (!BTSLUtil.isNullString(requestVO.getSenderReturnMessage())) {
	                message = requestVO.getSenderReturnMessage();
	            }
	            if(PretupsErrorCodesI.NO_EXPIRY_IN_PAYLOAD.equals(ERROR)||PretupsErrorCodesI.INVALID_TOKEN_FORMAT.equals(ERROR)||PretupsErrorCodesI.MAPP_INVALID_TOKEN.equals(ERROR)||PretupsErrorCodesI.MAPP_TOKEN_EXPIRED.equals(ERROR)||PretupsErrorCodesI.UNAUTHORIZED_REQUEST.equals(ERROR))
	            {
	            	 RestAPIStringParser.generateJsonResponse(requestVO);
	            	 prepareJsonResponse(requestVO);
	            	 response = requestVO.getJsonReponse();
	            }
	            else
	            {
	            	if (gatewayParsersObj == null) {
	            	
	                try {
	                    gatewayParsersObj = (GatewayParsersI) PretupsBL.getGatewayHandlerObj(requestVO
	                            .getMessageGatewayVO().getHandlerClass());
	                } catch (Exception e) {
	                    log.errorTrace(methodName, e);
	                }
	                if (log.isDebugEnabled()) {
	                	loggerValue.setLength(0);
	                	loggerValue.append("gatewayParsersObj=");
	                	loggerValue.append(gatewayParsersObj);
	                    log.debug(this,  loggerValue);
	                }
	            }

	            if (requestVO.getMessageGatewayVO() != null) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Gateway Time out=");
	            	loggerValue.append(requestVO.getMessageGatewayVO().getTimeoutValue());
	                LogFactory.printLog(methodName,loggerValue.toString(), log);
	            }

	            if (requestVO.getSenderLocale() != null) {
	                requestVO.setLocale(requestVO.getSenderLocale());
	            }

	            if (gatewayParsersObj != null) {
	                gatewayParsersObj.generateChannelResponseMessage(requestVO);
	            } else {
	                if (!BTSLUtil.isNullString(requestVO.getReqContentType())
	                        && PretupsI.JSON_CONTENT_TYPE.equals(requestVO.getReqContentType()) ) {
	                    prepareJsonResponse(requestVO);
	                } else {
	                    if (!BTSLUtil.isNullString(requestVO.getSenderReturnMessage())) {
	                        message = requestVO.getSenderReturnMessage();
	                    } else {
	                        message = BTSLUtil.getMessage(requestVO.getLocale(), requestVO.getMessageCode(),
	                                requestVO.getMessageArguments());
	                    }

	                    requestVO.setSenderReturnMessage(message);
	                }
	            }
	            response = requestVO.getJsonReponse();
	            try {
	                String reqruestGW = requestVO.getRequestGatewayCode();
	                final String altrnetGW = BTSLUtil.NullToString(Constants.getProperty("C2S_REC_MSG_REQD_BY_ALT_GW"));
	                if ((!BTSLUtil.isNullString(altrnetGW) && (altrnetGW.split(":")).length >= 2)
	                        && reqruestGW.equalsIgnoreCase(altrnetGW.split(":")[0])) {
	                    reqruestGW = (altrnetGW.split(":")[1]).trim();
	                    loggerValue.setLength(0);
	                    loggerValue.append("processRequest: Sender Message push through alternate GW");
	                    loggerValue.append(reqruestGW);
	                    loggerValue.append("Requested GW was:");
	                    loggerValue.append(requestVO.getRequestGatewayCode());
	                    LogFactory.printLog(methodName, loggerValue.toString(), log);
	                }
	                int messageLength = 0;
	                final String messLength = BTSLUtil.NullToString(Constants.getProperty("MSG_LENGTH_GW"));
	                if (!BTSLUtil.isNullString(messLength)) {
	                    messageLength = (new Integer(messLength)).intValue();
	                }

	                if (!(!BTSLUtil.isNullString(requestVO.getReqContentType()))
	                        && !PretupsI.GATEWAY_TYPE_USSD.equals(requestVO.getRequestGatewayType())) {
	                 

	                    message = requestVO.getSenderReturnMessage();
	                    String message1 = null;
	                    if ((messageLength > 0) && (message.length() > messageLength)) {
	                        message1 = BTSLUtil.getMessage(requestVO.getLocale(), PretupsErrorCodesI.REQUEST_IN_QUEUE_UB,
	                                requestVO.getMessageArguments());
	                        final PushMessage pushMessage1 = new PushMessage(requestVO.getFilteredMSISDN(), message1,
	                                requestVO.getRequestIDStr(), requestVO.getRequestGatewayCode(), requestVO.getLocale());
	                        pushMessage1.push();
	                        requestVO.setRequestGatewayCode(reqruestGW);
	                    }
	                    message = requestVO.getSenderReturnMessage();

	                } else {
	                    message = requestVO.getSenderReturnMessage();
	                }
	            } catch (Exception e) {
	                log.errorTrace(methodName, e);
	            }
	            if (log.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Locale=");
	            	loggerValue.append(requestVO.getLocale());
	            	loggerValue.append(" requestEndTime=");
	            	loggerValue.append(requestEndTime);
	            	loggerValue.append (REQSTART);
	            	loggerValue.append(requestStartTime);
	            	loggerValue.append(MESSAGECODE);
	            	loggerValue.append(requestVO.getMessageCode());
	            	loggerValue.append(ARGS);
	            	loggerValue.append(requestVO.getMessageArguments());
	                log.debug(methodName,  loggerValue );
	            }
	            if (requestVO.getMessageGatewayVO() == null
	                    || requestVO.getMessageGatewayVO().getResponseType()
	                            .equalsIgnoreCase(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_RESPONSE)
	                    || (requestEndTime - requestStartTime) / 1000 < requestVO.getMessageGatewayVO().getTimeoutValue()) {
	                requestVO.setMsgResponseType(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_RESPONSE);

	                if (requestVO.isSuccessTxn() && requestVO.isSenderMessageRequired()
	                        && (PretupsI.JSON_CONTENT_TYPE.equalsIgnoreCase(requestVO.getReqContentType())||("c2cvomstrfini").equalsIgnoreCase(requestVO.getServiceKeyword())
	                        		||("c2cvomstrf").equalsIgnoreCase(requestVO.getServiceKeyword())||("c2ctrfini").equalsIgnoreCase(requestVO.getServiceKeyword()))
	                        && !PretupsI.YES.equals(externalInterfaceAllowed)) {
	                    final String senderMessage = BTSLUtil.getMessage(requestVO.getLocale(), requestVO.getMessageCode(),
	                            requestVO.getMessageArguments());
	                    final PushMessage pushMessage = new PushMessage(requestVO.getMessageSentMsisdn(), senderMessage,
	                            requestVO.getRequestIDStr(), requestVO.getRequestGatewayCode(), requestVO.getLocale());

	                    if (!BTSLUtil.isNullString(senderMessage) && !"null".equalsIgnoreCase(senderMessage)) {
	                        pushMessage.push();
	                    }
	                }
	            } else {
	                requestVO.setMsgResponseType(PretupsI.MSG_GATEWAY_RESPONSE_TYPE_PUSH);
	                if (!PretupsI.KEYWORD_TYPE_ADMIN.equals(requestVO.getServiceType())
	                        && requestVO.isSenderMessageRequired()) {

	                    final PushMessage pushMessage = new PushMessage(requestVO.getMessageSentMsisdn(),
	                            requestVO.getSenderReturnMessage(), requestVO.getRequestIDStr(),
	                            requestVO.getRequestGatewayCode(), requestVO.getLocale());
	                    pushMessage.push();

	                }
	            }
	            }
	            ChannelGatewayRequestLog.outLog(requestVO);
	            LogFactory.printLog(methodName, "Exiting", log);
	            if(mcomCon != null){mcomCon.close("RestReceiver#processRequestChannel");mcomCon=null;}
	        }
	        
	        return response;
	    }


	 private void populateLanguageSettings(RequestVO prequestVO, ChannelUserVO channelUserVO) {
         final FixedInformationVO fixedInformationVO = (FixedInformationVO) prequestVO.getFixedInformationVO();
         if (prequestVO.getLocale() == null) {
             prequestVO.setLocale(new Locale((channelUserVO.getUserPhoneVO()).getPhoneLanguage(), (channelUserVO
                     .getUserPhoneVO()).getCountry()));
         }
         if (fixedInformationVO != null) {
             PretupsBL.getCurrentLocale(prequestVO, channelUserVO.getUserPhoneVO());
         }
     }
	 private PretupsResponse<JsonNode> validateServiceType(RequestVO prequestVO,
             ServiceKeywordCacheVO pserviceKeywordCacheVO, ChannelUserVO channelUserVO) throws BTSLBaseException {
         PretupsResponse<JsonNode> response = new PretupsResponse<>();
         final String methodName = "validateServiceType";
         final String serviceType = prequestVO.getServiceType();
         if (PretupsI.C2S_MODULE.equalsIgnoreCase(prequestVO.getModule())&&(PretupsI.YES.equals(pserviceKeywordCacheVO.getExternalInterface())
                 || PretupsI.AUTO_ASSIGN_SERVICES.equals(pserviceKeywordCacheVO.getExternalInterface()))) {
             final ListValueVO listValueVO = BTSLUtil.getOptionDesc(serviceType,
                     channelUserVO.getAssociatedServiceTypeList());
             if (listValueVO == null || BTSLUtil.isNullString(listValueVO.getLabel())) {
                 LogFactory.printError(methodName, " MSISDN=" + prequestVO.getFilteredMSISDN()
                         + " Service Type not found in allowed List", log);
                 response.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED);
                 response.setResponse(PretupsI.RESPONSE_FAIL, false,
                         PretupsRestUtil.getMessageString(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED));
                 return response;
             } else if (PretupsI.C2S_MODULE.equalsIgnoreCase(prequestVO.getModule())&&!PretupsI.YES.equals(listValueVO.getLabel())) {
                 LogFactory.printError(methodName, " MSISDN=" + prequestVO.getFilteredMSISDN()
                         + " Service Type is suspended in allowed List", log);
                 response.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_SUSPENDED);
                 response.setResponse(PretupsI.RESPONSE_FAIL, false,
                         PretupsRestUtil.getMessageString(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_SUSPENDED));
                 return response;
             }

         }
         if (PretupsI.OPT_MODULE.equalsIgnoreCase(prequestVO.getModule()) && PretupsI.YES.equals(pserviceKeywordCacheVO.getExternalInterface())) {
             final ListValueVO listValueVO = BTSLUtil.getOptionDesc(serviceType, channelUserVO.getAssociatedServiceTypeList());
             if (listValueVO == null || BTSLUtil.isNullString(listValueVO.getLabel())) {
                 log.error("validateServiceType", prequestVO.getRequestIDStr(), " MSISDN=" + prequestVO.getFilteredMSISDN() + " Service Type not found in allowed List");
                 response.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED);
                 response.setResponse(PretupsI.RESPONSE_FAIL, false,
                         PretupsRestUtil.getMessageString(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED));
                 return response;
             } else if (!PretupsI.YES.equals(listValueVO.getLabel())) {
                 log.error("validateServiceType", prequestVO.getRequestIDStr(),
                     " MSISDN=" + prequestVO.getFilteredMSISDN() + " Service Type is suspended in allowed List");
                 response.setMessageCode(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_NOTALLOWED);
                 response.setResponse(PretupsI.RESPONSE_FAIL, false,
                         PretupsRestUtil.getMessageString(PretupsErrorCodesI.CHNL_ERROR_SNDR_SRVCTYP_SUSPENDED));
                 return response;
             }// end if
         }
         response.setStatus(true);
         return response;
     }

	 protected void prepareJsonResponse(RequestVO prequestVO) {
         final String methodName = "prepareJsonResponse";
         final java.util.Date date = new java.util.Date();
         final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
         String responseStr;
         LogFactory.printLog(methodName, "Entered", log);
         try {
             PretupsResponse<JsonNode> response = new PretupsResponse<>();
             JsonObject json = new JsonObject();
             if (!BTSLUtil.isNullString(prequestVO.getMessageCode())) {
                 if (prequestVO.isSuccessTxn()) {
                     json.addProperty(TXNSTATUS, PretupsI.TXN_STATUS_SUCCESS);

                 } else {
                     String message = prequestVO.getMessageCode();
                     if (message.indexOf('_') != -1) {
                         message = message.substring(0, message.indexOf('_'));
                     }
                     json.addProperty(TXNSTATUS, message);
                 }

                 sdf.setLenient(false);
                 json.addProperty(DATE.toUpperCase(), sdf.format(date));
                 json.addProperty(MESSAGE,
                         PretupsRestUtil.getMessageString(prequestVO.getMessageCode(), prequestVO.getMessageArguments()));
             }
             responseStr = json.toString();
             response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,
                     (JsonNode) PretupsRestUtil.convertJSONToObject(responseStr, new TypeReference<JsonNode>() {
                     }));
             prequestVO.setJsonReponse(response);
             prequestVO.setSenderReturnMessage(responseStr);

         } catch (Exception e) {
             log.errorTrace(methodName, e);
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                     "ChannelReceiver[prepareJsonResponse]", prequestVO.getRequestIDStr(), "", "",
                     "Exception while generating XML response:" + e.getMessage());
         }
         if (log.isDebugEnabled()) {
             log.debug(methodName, prequestVO.getRequestIDStr(),
                     "Exiting with message=" + prequestVO.getSenderReturnMessage());
         }

     }
	 
	 public PretupsResponse<JsonNode> parseRequestForMessage(JsonNode request, RequestVO prequestVO)
             throws BTSLBaseException {
         final String methodName = "parseRequestForMessage";
         PretupsResponse<JsonNode> response = new PretupsResponse<>();
         String msisdn;
         String requestMessage;
         String loginID;
         JsonNode data;
         if (request != null) {
             data = request.get("data");
             requestMessage = data.toString();
         } else {
             response.setMessageCode(PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTMESSAGE);
             response.setResponse(PretupsI.RESPONSE_FAIL, false,
                     PretupsRestUtil.getMessageString(PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTMESSAGE));
             return response;
         }
         msisdn = getNodeValue(request, "msisdn");
         if ((BTSLUtil.isNullString(msisdn) || BTSLUtil.isNullString(requestMessage))
                 && PretupsI.JSON_CONTENT_TYPE.equals("application/json")) {
             prequestVO.setRequestMessage(requestMessage);
             prequestVO.setReqContentType("application/json");

         }
         if (!BTSLUtil.isNullString(msisdn)) {
             prequestVO.setRequestMSISDN(msisdn);
         }
         
         
         LogFactory.printLog(methodName, "requestMessage " + requestMessage, log);
         prequestVO.setRequestMessage(requestMessage);
         response.setStatus(true);
         return response;

     }
	 
	 private String getNodeValue(JsonNode node, String value) {
         if (node.get(value) != null) {
             return node.get(value).textValue();
         } else {
             return "";
         }
     }
	 public PretupsResponse<JsonNode> parseRequestfromJson(JsonNode request, RequestVO requestVO)
             throws BTSLBaseException {
         PretupsResponse<JsonNode> response = new PretupsResponse<>();
         final String methodName = "parseRequestfromJson";
         if (BTSLUtil.isNullString(requestVO.getReqContentType())) {
             requestVO.setReqContentType("CONTENT_TYPE");
         }
         String reqGatewayCode = getNodeValue(request, "reqGatewayCode");
         String reqGatewayType = getNodeValue(request, "reqGatewayType");
         LogFactory.printLog(methodName, " reqGatewayCode " + reqGatewayCode + "reqGatewayType " + reqGatewayType, log);

         if (BTSLUtil.isNullString(reqGatewayCode)) {
             response.setMessageCode(PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTINTID);
             response.setResponse(PretupsI.RESPONSE_FAIL, false,
                     PretupsRestUtil.getMessageString(PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTINTID));
             return response;
         } else {
             requestVO.setRequestGatewayCode(reqGatewayCode.trim());
         }
         if (BTSLUtil.isNullString(reqGatewayType)) {
             response.setMessageCode(PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTINTTYPE);
             response.setResponse(PretupsI.RESPONSE_FAIL, false,
                     PretupsRestUtil.getMessageString(PretupsErrorCodesI.C2S_ERROR_BLANK_REQUESTINTTYPE));
             return response;
         } else {
             requestVO.setRequestGatewayType(reqGatewayType.trim());
         }

         requestVO.setLogin(getNodeValue(request, "reqGatewayLoginId"));
         requestVO.setPassword(getNodeValue(request, "reqGatewayPassword"));
         requestVO.setServicePort(getNodeValue(request, "servicePort"));
         requestVO.setSourceType(getNodeValue(request, "sourceType"));

         response.setStatus(true);
         return response;

     }
	 
	 private void prepareC2SVoFromGiftVO( C2SRechargeRequestVO c2SRechargeRequestVO, GiftRechargeRequestVO giftRechargeRequestVO) throws BTSLBaseException {
	        if (log.isDebugEnabled()) {
	        	log.debug("prepareC2SVoFromGiftVO", "Entered TheForm: " + c2SRechargeRequestVO + " ChannelTransferVO: " + giftRechargeRequestVO );
	        }
	        c2SRechargeRequestVO.setData(new C2SRechargeDetails()); 
	        c2SRechargeRequestVO.getData().setAmount(giftRechargeRequestVO.getData().getAmount());
	        c2SRechargeRequestVO.getData().setDate(giftRechargeRequestVO.getData().getDate());
	        c2SRechargeRequestVO.getData().setExtnwcode(giftRechargeRequestVO.getData().getExtnwcode());
	        c2SRechargeRequestVO.getData().setExtrefnum(giftRechargeRequestVO.getData().getExtrefnum());
	        c2SRechargeRequestVO.getData().setGifterLang(giftRechargeRequestVO.getData().getGifterLang());
	        c2SRechargeRequestVO.getData().setGifterName(giftRechargeRequestVO.getData().getGifterName());
	        c2SRechargeRequestVO.getData().setGifterMsisdn(giftRechargeRequestVO.getData().getGifterMsisdn());
	        c2SRechargeRequestVO.getData().setLanguage1(giftRechargeRequestVO.getData().getLanguage1());
	        c2SRechargeRequestVO.getData().setLanguage2(giftRechargeRequestVO.getData().getLanguage2());
	        c2SRechargeRequestVO.getData().setSelector(giftRechargeRequestVO.getData().getSelector());
	        c2SRechargeRequestVO.getData().setMsisdn2(giftRechargeRequestVO.getData().getMsisdn2());
	        if (log.isDebugEnabled()) {
	        	log.debug("prepareC2SVoFromGiftVO", "Exited TheForm: " + c2SRechargeRequestVO + " ChannelTransferVO: " + giftRechargeRequestVO );
	        }
	    }
	 
	 private void prepareC2SVoFromInternetVO( C2SRechargeRequestVO c2SRechargeRequestVO, InternetRechargeRequestVO internetRechargeRequestVO) throws BTSLBaseException {
	        if (log.isDebugEnabled()) {
	        	log.debug("prepareC2SVoFromGiftVO", "Entered TheForm: " + c2SRechargeRequestVO + " ChannelTransferVO: " + internetRechargeRequestVO );
	        }
	        c2SRechargeRequestVO.setData(new C2SRechargeDetails()); 
	        c2SRechargeRequestVO.getData().setAmount(internetRechargeRequestVO.getData().getAmount());
	        c2SRechargeRequestVO.getData().setDate(internetRechargeRequestVO.getData().getDate());
	        c2SRechargeRequestVO.getData().setExtnwcode(internetRechargeRequestVO.getData().getExtnwcode());
	        c2SRechargeRequestVO.getData().setExtrefnum(internetRechargeRequestVO.getData().getExtrefnum());
	        c2SRechargeRequestVO.getData().setLanguage1(internetRechargeRequestVO.getData().getLanguage1());
	        c2SRechargeRequestVO.getData().setLanguage2(internetRechargeRequestVO.getData().getLanguage2());
	        c2SRechargeRequestVO.getData().setSelector(internetRechargeRequestVO.getData().getSelector());
	        c2SRechargeRequestVO.getData().setMsisdn2(internetRechargeRequestVO.getData().getMsisdn2());
	        c2SRechargeRequestVO.getData().setNotifMsisdn(internetRechargeRequestVO.getData().getNotifMsisdn());
	        if (log.isDebugEnabled()) {
	        	log.debug("prepareC2SVoFromGiftVO", "Exited TheForm: " + c2SRechargeRequestVO + " ChannelTransferVO: " + internetRechargeRequestVO );
	        }
	    }
	 
	 private void prepareMvdVO( C2SRechargeRequestVO c2SRechargeRequestVO, MvdRequestVO mvdRequestVO) throws BTSLBaseException {
	        if (log.isDebugEnabled()) {
	        	log.debug("prepareC2SVoFromGiftVO", "Entered TheForm: " + c2SRechargeRequestVO + " MvdRequestVO: " + mvdRequestVO );
	        }
	        c2SRechargeRequestVO.setData(new C2SRechargeDetails()); 
	        c2SRechargeRequestVO.getData().setAmount(mvdRequestVO.getData().getAmount());
	        c2SRechargeRequestVO.getData().setDate(mvdRequestVO.getData().getDate());
	        c2SRechargeRequestVO.getData().setLanguage1(mvdRequestVO.getData().getLanguage1());
	        c2SRechargeRequestVO.getData().setLanguage2(mvdRequestVO.getData().getLanguage2());
	        c2SRechargeRequestVO.getData().setSelector(mvdRequestVO.getData().getSelector());
	        c2SRechargeRequestVO.getData().setMsisdn2(mvdRequestVO.getData().getMsisdn2());
	        c2SRechargeRequestVO.getData().setQty(mvdRequestVO.getData().getQty());
	        c2SRechargeRequestVO.getData().setPin(mvdRequestVO.getData().getPin());
	        c2SRechargeRequestVO.getData().setExtnwcode(mvdRequestVO.getData().getExtnwcode());
	        c2SRechargeRequestVO.getData().setExtrefnum(mvdRequestVO.getData().getExtrefnum());
	        if (log.isDebugEnabled()) {
	        	log.debug("prepareMvdVO", "Exited TheForm: " + c2SRechargeRequestVO + " MvdRequestVO: " + mvdRequestVO );
	        }
	    }
	 
	 @GetMapping(value= "/getDenomination", produces = MediaType.APPLICATION_JSON)	
		@ResponseBody
		/*@ApiOperation(value = "Get Denominations For MVD",
		           response = MvdDenominationResponseVO.class,
		           authorizations = {
		               @Authorization(value = "Authorization")})
		@ApiResponses(value = {
		      @ApiResponse(code = 200, message = "OK", response = MvdDenominationResponseVO.class),
		      @ApiResponse(code = 400, message = "Bad Request" ),
		      @ApiResponse(code = 401, message = "Unauthorized"),
		      @ApiResponse(code = 404, message = "Not Found")
		      })
		*/

	 @io.swagger.v3.oas.annotations.Operation(summary = "${getDenomination.summary}", description="${getDenomination.description}",

			 responses = {
					 @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							 @io.swagger.v3.oas.annotations.media.Content(
									 mediaType = "application/json",
									 array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = MvdDenominationResponseVO.class))
							 )
					 }

					 ),


					 @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, description = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_DESC,  content = {
							 @io.swagger.v3.oas.annotations.media.Content(
									 mediaType = "application/json",
									 array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
							 )
					 }),
					 @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
							 @io.swagger.v3.oas.annotations.media.Content(
									 mediaType = "application/json",
									 array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
							 )
					 }),
					 @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
							 @io.swagger.v3.oas.annotations.media.Content(
									 mediaType = "application/json",
									 array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
							 )
					 }),
					 @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
							 @io.swagger.v3.oas.annotations.media.Content(
									 mediaType = "application/json",
									 array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
							 )
					 })
			 }
	 )



	 public MvdDenominationResponseVO getDenominationsMvd(
				@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				 HttpServletResponse responseSwag
						)throws Exception{
			

			final String methodName =  "getDenominations";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}
			
			Connection con = null;
		    MComConnectionI mcomCon = null;
		    MvdDenominationResponseVO response=null;
		    response = new MvdDenominationResponseVO();
			 String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		     String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
			
			 try {
				 
				
			    
			    OAuthUser oAuthUserData=new OAuthUser();
		        oAuthUserData.setData(new OAuthUserData());
		    
		        OAuthenticationUtil.validateTokenApi(oAuthUserData,headers,new BaseResponseMultiple());
		       
		        c2SServiceI.loadDenomination(response);
			 }catch (BTSLBaseException be) {
		      	 log.error(methodName, "Exception:e=" + be);
		         log.errorTrace(methodName, be);
		         if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
		         		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
		        	 String unauthorised=Integer.toString(HttpStatus.SC_UNAUTHORIZED) ;
		        	response.setStatus(unauthorised);
		         	responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
		         	
		         	 
		         }
		          else{
		          String badReq=Integer.toString(HttpStatus.SC_BAD_REQUEST) ;
		          response.setStatus(badReq);
		          responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		        
		          }
		         String resmsg ="";
		         if(be.getArgs()!=null) {
		        	 resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), be.getMessage(), be.getArgs());

		         }else {
		        	 resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), be.getMessage(), null);

		         }
		 	   response.setMessageCode(be.getMessage());
		 	   response.setMessage(resmsg);
		 	   
			}
		    catch (Exception e) {
		        log.error(methodName, "Exceptin:e=" + e);
		        log.errorTrace(methodName, e);
		        String fail=Integer.toString(PretupsI.RESPONSE_FAIL) ;
		        response.setStatus(fail);
				response.setMessageCode("error.general.processing");
				response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
		    } finally {
		    	try {
		        	if (mcomCon != null) {
						mcomCon.close("DownloadUserListController");
						mcomCon = null;
					}
		        } catch (Exception e) {
		        	log.errorTrace(methodName, e);
		        }
		    	
		        try {
		            if (con != null) {
		                con.close();
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


	 
	    @PostMapping(value = "/dvd", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@ApiOperation(value = "Digital Voucher Distribution ", response = DvdApiResponse.class ,
	    		authorizations = {
	    	            @Authorization(value = "Authorization")
	    })
	    
	    @ApiResponses(value = {
		        @ApiResponse(code = 400, message = "Bad Request")
		        })
	    */

		@io.swagger.v3.oas.annotations.Operation(summary = "${dvd.summary}", description="${dvd.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = DvdApiResponse.class))
								)
						}

						),


						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, description = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						})
				}
		)



		public DvdApiResponse processDVDRequest(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	            @Parameter(description = SwaggerAPIDescriptionI.DVD, required = true)
	            @RequestBody DvdSwaggRequestVO requestVO,HttpServletResponse responseSwag, HttpServletRequest httpServletRequest) throws Exception {
	    	DvdApiResponse response = null;
	        
	        ++requestIdChannel;
	        final String requestIDStr = String.valueOf(requestIdChannel);
	        //decrypting pin here and setting it in requestVO
	        requestVO.setPin(AESEncryptionUtil.aesDecryptor(requestVO.getPin(), Constants.A_KEY));
	        response = c2SServiceI.processRequestDVD(requestVO, requestIDStr, headers, responseSwag, httpServletRequest);
	        return response;
	    }
	    
	    @PostMapping(value= "/getReversalList", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)	
		@ResponseBody
		/*@ApiOperation(value = "Get recharge list For reversal",
		           response = ChannelTransferVO.class,
		           authorizations = {
		               @Authorization(value = "Authorization")})
		@ApiResponses(value = {
		      @ApiResponse(code = 200, message = "OK", response = MvdDenominationResponseVO.class),
		      @ApiResponse(code = 400, message = "Bad Request" ),
		      @ApiResponse(code = 401, message = "Unauthorized"),
		      @ApiResponse(code = 404, message = "Not Found")
		      })
		*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${getReversalList.summary}", description="${getReversalList.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = GetReversalListResponseVO.class))
								)
						}

						),


						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, description = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						})
				}
		)



		public GetReversalListResponseVO getReversalList(
				@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				@RequestBody GetReversalListRequestVO requestVO,
				 HttpServletResponse responseHttp
						)throws Exception{
			

			final String methodName =  "getReversalList";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}
			
			Connection con = null;
		    MComConnectionI mcomCon = null;
		    GetReversalListResponseVO response=null;
		    response = new GetReversalListResponseVO();
		    UserDAO userDao = new UserDAO();
		    List<ChannelTransferVO> reversalList = null;
		    UserVO userVO = null;
	      	
			String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		    String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
			
			try 
			{  
				mcomCon = new MComConnection();
	        	con=mcomCon.getConnection();
		        
	        	OAuthUser oAuthUserData=new OAuthUser();
		        oAuthUserData.setData(new OAuthUserData());
	  
		            
		         OAuthenticationUtil.validateTokenApi(oAuthUserData,headers,response);
		         
		         String loginId =  oAuthUserData.getData().getLoginid();
		         userVO = userDao.loadAllUserDetailsByLoginID(con, loginId);
		            
				 
		         String senderMsisdn = requestVO.getSenderMsisdn();
		         String receiverMsisdn = requestVO.getReceiverMsisdn();
		         String txnID = requestVO.getTxnID();
		         reversalList = c2SServiceI.getReversalList(con, userVO, senderMsisdn, receiverMsisdn, txnID);
		         
		         response.setResponseList(reversalList);
		         response.setStatus(Integer.toString(HttpStatus.SC_OK));
		         response.setMessage("SUCCESS");
		         response.setMessageCode(PretupsErrorCodesI.SUCCESS);
		         
			 }catch (BTSLBaseException be) {
		      	 log.error(methodName, "Exception:e=" + be);
		         log.errorTrace(methodName, be);
		         if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
		         		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
		        	 String unauthorised=Integer.toString(HttpStatus.SC_UNAUTHORIZED) ;
		        	response.setStatus(unauthorised);
		         	responseHttp.setStatus(HttpStatus.SC_UNAUTHORIZED);
		         	
		         	 
		         }
		          else{
		          String badReq=Integer.toString(HttpStatus.SC_BAD_REQUEST) ;
		          response.setStatus(badReq);
		          responseHttp.setStatus(HttpStatus.SC_BAD_REQUEST);
		        
		          }
		         String resmsg ="";
		         if(be.getArgs()!=null) {
		        	 resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), be.getMessage(), be.getArgs());

		         }else {
		        	 resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), be.getMessage(), null);

		         }
		 	   response.setMessageCode(be.getMessage());
		 	   response.setMessage(resmsg);
		 	   
			}
		    catch (Exception e) {
		        log.error(methodName, "Exceptin:e=" + e);
		        log.errorTrace(methodName, e);
		        String fail=Integer.toString(PretupsI.RESPONSE_FAIL) ;
		        response.setStatus(fail);
				response.setMessageCode("error.general.processing");
				response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
		    } finally {
		    	try {
		        	if (mcomCon != null) {
						mcomCon.close("C2SServicesController#"+methodName);
						mcomCon = null;
					}
		        } catch (Exception e) {
		        	log.errorTrace(methodName, e);
		        }
		    	
		        try {
		            if (con != null) {
		                con.close();
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

	    public static OperatorUtilI _operatorUtil = null;
	    @SuppressWarnings("unchecked")
	    @GetMapping(value="/userservicebal/{serviceName}", produces =MediaType.APPLICATION_JSON)
		@ResponseBody
		/*@ApiOperation(tags= "C2S Services", value = "User Services With Balance", response = GetUserServiceBalanceResponseVO.class ,
				notes = "Give input 'ALL' to get a list of balance for every service type",
		authorizations = {
		    	            @Authorization(value = "Authorization")
		    	            
		    })
	  
			@ApiResponses(value = { 
					@ApiResponse(code = 200, message = "OK", response = GetUserServiceBalanceResponseVO.class),
					@ApiResponse(code = 400, message = "Bad Request"),
					@ApiResponse(code = 401, message = "Unauthorized"),
					@ApiResponse(code = 404, message = "Not Found")
					})
	    */


		@io.swagger.v3.oas.annotations.Operation(summary = "${userservicebal.summary}", description="${userservicebal.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = GetUserServiceBalanceResponseVO.class))
								)
						}

						),


						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, description = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						})
				}
		)




		public GetUserServiceBalanceResponseVO processGetUserServiceBalanceRequest(
		    		@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
		            @Parameter(description = SwaggerAPIDescriptionI.SERVICE_TYPES, required = true)
		    		@PathVariable("serviceName") String serviceName, HttpServletResponse response1) throws Exception {
			    final String methodName = "processGetUserServiceBalanceRequest";
				if (log.isDebugEnabled()) {
					log.debug(methodName, "Entered ");
				}

				GetUserServiceBalanceResponseVO response = new GetUserServiceBalanceResponseVO();
		        response = c2SServiceI.processRequest(serviceName, headers, response1);
		        
		        return response;
		    }
	    
	    
	    @PostMapping(value = "/userwidget", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@ApiOperation(value = "Update,Add User Widget", response = UserWidgetResponse.class ,
	    		authorizations = {
	    	            @Authorization(value = "Authorization")
	    })
	    
	    @ApiResponses(value = {
		        @ApiResponse(code = 400, message = "Bad Request")
		        })*/


		@io.swagger.v3.oas.annotations.Operation(summary = "${userwidget.summary}", description="${userwidget.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserWidgetResponse.class))
								)
						}

						),


						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, description = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								)
						})
				}
		)




		public UserWidgetResponse processUserWidgetRequest(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	            @Parameter(description = SwaggerAPIDescriptionI.USER_WIDGET_RESPONSE, required = true)
	            @RequestBody UserWidgetRequestVO requestVO,HttpServletResponse responseSwag, HttpServletRequest httpServletRequest) throws Exception {
	    	UserWidgetResponse response = null;
	       
	        response = c2SServiceI.processUserWiget(requestVO, headers, responseSwag, httpServletRequest);
	        return response;
	    }
	

	    private void settingStaffDetails(ChannelUserVO channelUserVO) {

			Connection con = null;
			MComConnectionI mcomCon = null;
			try {
				
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				ChannelUserDAO channelUserDAO = new ChannelUserDAO();
				channelUserVO.setActiveUserID(channelUserVO.getUserID());
				UserDAO userDao = new UserDAO();
	            UserPhoneVO phoneVO = userDao.loadUserPhoneVO(con, channelUserVO.getUserID());
	            if (phoneVO != null && !BTSLUtil.isNullString(phoneVO.getMsisdn())) {
	                channelUserVO.setActiveUserMsisdn(phoneVO.getMsisdn());
	                channelUserVO.setActiveUserPin(phoneVO.getSmsPin());
	               }
	            ChannelUserVO staffUserVO = new ChannelUserVO();
	            UserPhoneVO staffphoneVO = new UserPhoneVO();
	            BeanUtils.copyProperties(staffUserVO, channelUserVO);
	            if (phoneVO != null && !BTSLUtil.isNullString(phoneVO.getMsisdn())) {
	                BeanUtils.copyProperties(staffphoneVO, phoneVO);
	                staffUserVO.setUserPhoneVO(staffphoneVO);
	            }
	            staffUserVO.setPinReset(channelUserVO.getPinReset());
	            channelUserVO.setStaffUserDetails(staffUserVO);
	            ChannelUserVO parentChannelUserVO = new UserDAO().loadUserDetailsFormUserID(con, channelUserVO.getParentID());
	            staffUserDetails(channelUserVO, parentChannelUserVO);
	            channelUserVO.setPrefixId(parentChannelUserVO.getPrefixId());
					
			}catch(Exception e) {
				
			}finally {
				if(mcomCon != null)
				{
					mcomCon.close("C2CTransferController#checkAndSetStaffVO");
					mcomCon=null;
				}
			}

			
		}
		
		protected void staffUserDetails(ChannelUserVO channelUserVO, ChannelUserVO parentChannelUserVO) {
	        channelUserVO.setUserID(channelUserVO.getParentID());
	        channelUserVO.setParentID(parentChannelUserVO.getParentID());
	        channelUserVO.setOwnerID(parentChannelUserVO.getOwnerID());
	        channelUserVO.setStatus(parentChannelUserVO.getStatus());
	        channelUserVO.setUserType(parentChannelUserVO.getUserType());
	        channelUserVO.setStaffUser(true);
	        channelUserVO.setMsisdn(parentChannelUserVO.getMsisdn());
	        channelUserVO.setPinRequired(parentChannelUserVO.getPinRequired());
	        channelUserVO.setSmsPin(parentChannelUserVO.getSmsPin());
	        channelUserVO.setParentLoginID(parentChannelUserVO.getLoginID());
	    }


		public String sendRequestRemote(C2SRechargeRequestVO RCRequestVO) throws BTSLBaseException, IOException {
			final String methodName = "sendRequestRemote";
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.ENTERED + " RCRequestVO " + RCRequestVO.toString() );
			}
			HttpURLConnection httpURLCon = null;
			BufferedReader in = null;
			boolean httpsEnabled = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.HTTPS_ENABLE))).booleanValue();

			String msisdnPrefix = PretupsBL.getMSISDNPrefix(RCRequestVO.getData().getMsisdn());
			final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);

			if (networkPrefixVO == null) {
				throw new BTSLBaseException("sendRequest", methodName, "Network is null");
			}
			final String networkCode = networkPrefixVO.getNetworkCode();
			//String instanceCode = Constants.getProperty("INSTANCE_ID");
			NetworkLoadVO instanceVO =(NetworkLoadVO) LoadControllerCache.getNetworkLoadHash().get(LoadControllerCache.getInstanceID() + "_" + networkCode);
			log.debug(methodName,"InstanceVO:" + instanceVO.toString());
			InstanceLoadVO instanceLoadVO = LoadControllerCache.getInstanceLoadForNetworkHash(instanceVO.getC2sInstanceID() + "_" + networkCode + "_" + PretupsI.REQUEST_SOURCE_TYPE_WEB);
			log.debug(methodName,"instanceLoadVO:" + instanceLoadVO.toString());

			String gatewayType = PretupsI.GATEWAY_TYPE_WEB, gatewayCode = PretupsI.GATEWAY_TYPE_WEB, gatewayLogin = "pretups", gatewayPassword = "", receiverString = "/pretups/C2SReceiver", servicePort = "190", sourceType = "XML";

			final MessageGatewayVO messageGatewayVO = MessageGatewayCache.getObject(PretupsI.GATEWAY_TYPE_WEB);
			if(messageGatewayVO != null && PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getStatus())){
				gatewayType = messageGatewayVO.getGatewayType();
				gatewayCode = messageGatewayVO.getGatewayCode();
				gatewayLogin = messageGatewayVO.getRequestGatewayVO().getLoginID();
			}
			receiverString = Optional.ofNullable(Constants.getProperty("CHANNEL_WEB_RECHARGE_SERVLET")).orElse("/pretups/C2SReceiver");
			gatewayPassword = Optional.ofNullable(Constants.getProperty("GATEWAY_PASSWORD")).orElse("pretups");


			HttpURLConnection con = null;
			String responseXML = null;
			String transStatus = null;
			String http = "http://";
			try {
				//final String requestXML = generateRCRequest(RCRequestVO);
				if(httpsEnabled){
					http = "https://";
				}
				final StringBuffer sbf = new StringBuffer(http);
				sbf.append(instanceLoadVO.getHostAddress()).append(":").append(instanceLoadVO.getHostPort());
				sbf.append(receiverString);
				sbf.append("?MSISDN=").append(RCRequestVO.getData().getMsisdn());
				sbf.append("&MESSAGE=").append(PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
				sbf.append("+").append(RCRequestVO.getData().getMsisdn2());
				sbf.append("+").append(RCRequestVO.getData().getAmount());
				sbf.append("+").append(RCRequestVO.getData().getSelector());
				sbf.append("+").append(RCRequestVO.getData().getLanguage1());
				sbf.append("+").append(RCRequestVO.getData().getLanguage2());
				sbf.append("+").append(URLEncoder.encode(RCRequestVO.getData().getPin()));
				sbf.append("&REQUEST_GATEWAY_CODE=").append(gatewayCode);
				sbf.append("&REQUEST_GATEWAY_TYPE=").append(gatewayType);
				sbf.append("&SERVICE_PORT=").append(servicePort);
				sbf.append("&LOGIN=").append(gatewayLogin);
				sbf.append("&PASSWORD=").append(gatewayPassword);
				sbf.append("&SOURCE_TYPE=").append(sourceType);
				sbf.append("&ACTIVE_USER_ID=").append(RCRequestVO.getData().getUserid());

				final String urlToSend = sbf.toString();
				log.debug(methodName,"## urlToSend: " + urlToSend);

				try {
					int connect_timeout = 20000, read_timeout = 20000;
					try {
						connect_timeout = Integer.parseInt(Constants.getProperty("SMS_SERVER_CONNECT_TIMEOUT"));
						read_timeout = Integer.parseInt(Constants.getProperty("SMS_SERVER_READ_TIMEOUT"));
					}catch (Exception e){
						log.error(methodName,e);
					}

					log.debug(methodName,"## connect_timeout: " + connect_timeout + "## read_timeout: " + read_timeout);
					URL url = null;
					url = new URL(urlToSend);
					httpURLCon = (HttpURLConnection) url.openConnection();
					httpURLCon.setDoInput(true);
					httpURLCon.setDoOutput(true);
					httpURLCon.setRequestMethod("GET");
					httpURLCon.setConnectTimeout(connect_timeout);
					httpURLCon.setReadTimeout(read_timeout);
					in = new BufferedReader(new InputStreamReader(httpURLCon.getInputStream()));
				} catch (Exception e) {
					// Increment the connection refuse counter if error occured
					// while openning the connection.
					log.errorTrace(methodName, e);
					throw new BTSLBaseException(this.getClass(), "sendRequest", PretupsErrorCodesI.REST_SCH_ERROR_CONNECTION);
				}
				String responseStr = null;
				responseXML = "";
				while ((responseStr = in.readLine()) != null) {
					responseXML = responseXML + responseStr;
				}
					log.debug(methodName, "## Exiting responseXML ::" + responseXML);
					/*final int index = responseXML.indexOf("<TXNSTATUS>");
					transStatus = responseXML.substring(index + "<TXNSTATUS>".length(), responseXML.indexOf("</TXNSTATUS>", index));*/
				HashMap responsemap = BTSLUtil.getStringToHash(responseXML, "&", "=");
				transStatus = (String) responsemap.get("TXN_STATUS");


			}catch (BTSLBaseException be) {
				log.error(methodName, "BTSLBaseException : " + be.getMessage());
				log.errorTrace(methodName, be);
				throw be;
			} catch (Exception e) {
				log.error(methodName, "Exception : " + e.getMessage());
				log.errorTrace(methodName, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
						methodName, "", "", "", "Exception:" + e.getMessage());
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION);
			}

			finally {
				if (con != null) {
					con.disconnect();
				}
				if(httpURLCon != null){
					httpURLCon.disconnect();
				}
				if(in != null){
					in.close();
				}
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting:transStatus::" + transStatus);
			}
			return responseXML;
		}
		public String generateRCRequest(C2SRechargeRequestVO RCRequestVO) throws BTSLBaseException {
			final String methodName = "generateRCRequest";
			String requestStr = null;

			try {
				if (log.isDebugEnabled()) {
					log.debug(methodName,PretupsI.ENTERED + " RCRequestVO " + RCRequestVO.toString() );
				}
				final Date date = new Date();
				final String currentDate = BTSLUtil.getDateStringFromDate(date, PretupsI.DATE_FORMAT);

				StringBuffer stringBuffer = null;
				stringBuffer = new StringBuffer(1028);
				stringBuffer.append("<?xml version=\"1.0\"?>");
				stringBuffer.append("<COMMAND>");
				stringBuffer.append("<TYPE>EXRCTRFREQ</TYPE>");
				stringBuffer.append("<DATE>"+currentDate+"</DATE>");
				stringBuffer.append("<EXTNWCODE>AK</EXTNWCODE>");
				stringBuffer.append("<MSISDN>"+RCRequestVO.getData().getMsisdn()+"</MSISDN>");
				stringBuffer.append("<PIN>"+RCRequestVO.getData().getPin()+"</PIN>");
				/*stringBuffer.append("<LOGINID>" + RCRequestVO.getData().getLoginid() + "</LOGINID>");
				stringBuffer.append("<PASSWORD>" + RCRequestVO.getData().getPassword() + "</PASSWORD>");*/
				stringBuffer.append("<LOGINID></LOGINID>");
				stringBuffer.append("<PASSWORD></PASSWORD>");
				stringBuffer.append("<EXTCODE></EXTCODE>");
				stringBuffer.append("<EXTREFNUM>13572468</EXTREFNUM>");
				stringBuffer.append("<MSISDN2>" + RCRequestVO.getData().getMsisdn2() + "</MSISDN2>");
				stringBuffer.append("<AMOUNT>" + RCRequestVO.getData().getAmount() + "</AMOUNT>");
				stringBuffer.append("<PRODUCTCODE></PRODUCTCODE>");
				stringBuffer.append("<LANGUAGE1>0</LANGUAGE1>");
				stringBuffer.append("<LANGUAGE2>0</LANGUAGE2>");
				stringBuffer.append("<SELECTOR>1</SELECTOR>");
				stringBuffer.append("</COMMAND>");
				requestStr = stringBuffer.toString();
				if (log.isDebugEnabled()) {
					log.debug(methodName, "Exiting requestStr::" + requestStr);
				}

			} catch (ParseException e) {
				log.error(methodName, "ParseException : " + e.getMessage());
				log.errorTrace(methodName, e);
			} catch (Exception e) {
				log.error(methodName, "Exception : " + e.getMessage());
				log.errorTrace(methodName, e);
			} finally {
				if (log.isDebugEnabled()) {
					log.debug(methodName, "Exiting requestStr:" + requestStr);
				}
			}
			return requestStr;
		}


}

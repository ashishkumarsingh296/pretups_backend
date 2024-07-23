package com.btsl.pretups.user.requesthandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


import jakarta.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.btsl.common.BTSLBaseException;
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
import com.btsl.pretups.channel.receiver.RestReceiver;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserDetailsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRestI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

@Path("")
@Tag(name = "${dummy.name}", description = "${dummy.desc}")//@Api(value="Channel User Details")
public class ChannelUserDetailsController implements ServiceKeywordControllerI {
	 private static Log _log = LogFactory.getLog(ChannelUserDetailsController.class.getName());
	 private static final String PROCESS = "ChannelUserDetailsController[process]";

	 	@Context
	 	private HttpServletRequest httpServletRequest;
	 	@POST
	 	@Path("/c2s-receiver/usrdetails")
	    @Consumes(value = MediaType.APPLICATION_JSON)
	    @Produces(MediaType.APPLICATION_JSON)
	 	//@ApiOperation(value = "Channel User Info ", response = PretupsResponse.class)

		@io.swagger.v3.oas.annotations.Operation(summary = "${usrdetails.summary}", description="${usrdetails.description}",

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



		public PretupsResponse<JsonNode> processCP2PUserRequest(@Parameter(description = SwaggerAPIDescriptionI.CHANNEL_USER_DETAILS)ChannelUserDetailsVO channelUserDetailsVO) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
	 		PretupsResponse<JsonNode> response;
	         RestReceiver restReceiver;
	         RestReceiver.updateRequestIdChannel();
	         final String requestIDStr = String.valueOf(RestReceiver.getRequestIdChannel());
	         restReceiver = new RestReceiver();
	         response = restReceiver.processCP2PRequestOperator(httpServletRequest,(JsonNode)(PretupsRestUtil.convertJSONToObject(PretupsRestUtil.convertObjectToJSONString(channelUserDetailsVO), new TypeReference<JsonNode>(){})), PretupsRestI.CHANNEL_USER_DETAILS,requestIDStr);
	         return response;
	     }	
	 
	@Override
	public void process(RequestVO p_requestVO) {
		    Connection con = null; MComConnectionI mcomCon = null;
		    final HashMap<String,String> requestMap = p_requestVO.getRequestMap();
		    final String methodName = "process";
		    LogFactory.printLog(methodName, "Entered requestVO=" + p_requestVO, _log);
	        ChannelUserDAO channelUserDAO = new ChannelUserDAO();
	        ChannelUserVO channelUserVO = null;
	
	        try {
	        	mcomCon = new MComConnection();
	        	con=mcomCon.getConnection();
	            String userMsisdn = (String) requestMap.get("MSISDN2");
	            // Loading channel user details 
	            if(!BTSLUtil.isValidMSISDN(userMsisdn)){
		               throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.MSISDN_INVALID_OR_BLANK);
	            }
	            	
	            channelUserVO = channelUserDAO.loadChannelUserDetails(con,userMsisdn);
	            if(channelUserVO == null){
	               throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_INVALID_DETAILS_NOT_FOUND);
	            }
	            ArrayList<ListValueVO> sourceTypeList = LookupsCache.loadLookupDropDown(PretupsI.USER_NAME_PREFIX_TYPE, true);
                if(sourceTypeList != null && sourceTypeList.size() > 0){
                      Iterator it = sourceTypeList.iterator();
                      while(it.hasNext()){
                    	  ListValueVO lookupsVO = (ListValueVO) it.next();
                            if(lookupsVO.getValue().equals(channelUserVO.getUserNamePrefix())){
                                  channelUserVO.setUserNamePrefix(lookupsVO.getLabel());
                                  break;
                            }
                            
                      }
                      
                }

			}  catch (SQLException be) {
				p_requestVO.setSuccessTxn(false);
				_log.error(methodName, "BTSLBaseException " + be.getMessage());
	            _log.errorTrace(methodName, be);
	            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
	            return;
			}catch (BTSLBaseException be) {
				p_requestVO.setSuccessTxn(false);
				_log.error(methodName, "BTSLBaseException " + be.getMessage());
	            _log.errorTrace(methodName, be);
	            if (be.isKey()) {
	            	p_requestVO.setMessageCode(be.getMessageKey());
	            	p_requestVO.setMessageArguments(be.getArgs());
	            } else {
	            	p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
	                return;
	            }
	        } catch (Exception e) {
	        	p_requestVO.setSuccessTxn(false);
	        	_log.error(methodName, "Exception " + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, PROCESS, "", "", "",
	                "Exception:" + e.getMessage());
	            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
	            return;
	        }
	        finally {
	        	requestMap.put("USERNAMEPREFIX", channelUserVO.getUserNamePrefix());
	            requestMap.put("FIRSTNAME", channelUserVO.getFirstName());
	            requestMap.put("LASTNAME", channelUserVO.getLastName());
	            requestMap.put("CATEGORYNAME", channelUserVO.getCategoryVO().getCategoryName());
	            requestMap.put("CATEGORYCODE", channelUserVO.getCategoryVO().getCategoryCode());
	            p_requestVO.setRequestMap(requestMap);
	            if(mcomCon != null){mcomCon.close("ChannelUserDetailsController#process");mcomCon=null;}
	            LogFactory.printLog(methodName, " Exited ", _log);
	        }
		
	}
	
	
}

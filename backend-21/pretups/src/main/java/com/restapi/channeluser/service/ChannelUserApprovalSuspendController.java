package com.restapi.channeluser.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;


import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;

//import ch.qos.logback.core.joran.action.Action;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${ChannelUserApprovalSuspendController.name}", description = "${ChannelUserApprovalSuspendController.desc}")//@Api(tags= "Channel Users",value="Channel Users")
@RestController
@RequestMapping(value = "/v1/channelUsers")
public class ChannelUserApprovalSuspendController {
	
	
	@Autowired
	private ChannelUserServicesI channelUserService;

	public static final Log LOG = LogFactory.getLog(ChannelUserApprvListController.class.getName());
	
	@SuppressWarnings("unchecked")
	@PostMapping(value= "/UserListByStatus", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(tags= "Channel Users", value = "Channel users in suspend request ", response = String.class,
	  notes = ("Api Info:") + ("\n") + (""
	  		+ " 1.  SearchType : MSISDN \r\n"
	  		+ "     MobileNumber,loggedUserNeworkCode,userStatus Required\r\n"
	  		+ "   \r\n"
	  		+ " 2. SearchType :LOGINID\r\n"
	  		+ "    loginID,loggedUserNeworkCode,userStatus Required\r\n"
	  		+ "   \r\n"
	  		+ " 3. SearchType : Advanced\r\n"
	  		+ "    category,domain,geography,loggedUserNeworkCode,userStatus Required") + ("\n")  
				,
				 authorizations = {
		    	            @Authorization(value = "Authorization")})
	        @ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = ApprovalUsersResponse.class),
	        @ApiResponse(code = 201, message = "Created"),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })	
	*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${UserListByStatus.summary}", description="${UserListByStatus.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApprovalUsersResponse.class))
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
	public ApprovalUsersResponse getSuspendReqOrDelReqChannelUsers(
			HttpServletResponse responseSwag,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = SwaggerAPIDescriptionI.CHANNEL_USER_APPRV_LIST, required = true)
			@RequestBody ChannelUserSearchReqVo requestVO)throws IOException, SQLException, BTSLBaseException
	{
		final String methodName =  "getSuspendRequestOrSuspendChannelUsers";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered ");
		}
		LogFactory.printLog(methodName, "Entered requestVO=" + requestVO, LOG);
		 
		 OAuthUser oAuthUser = null;
		 OAuthUserData oAuthUserData = null;
		 Connection con = null;MComConnectionI mcomCon = null;
		 ApprovalUsersResponse response = new ApprovalUsersResponse();
		 response.setService("UserListByStatus");
		 Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue
				        (PreferenceI.DEFAULT_LANGUAGE)), (String)
				        (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
		 try {
			    mcomCon = new MComConnection();
			    con=mcomCon.getConnection();
				OAuthenticationUtil.validateTokenApi(headers);
			    oAuthUser = new OAuthUser();
				oAuthUserData = new OAuthUserData();
			    oAuthUser.setData(oAuthUserData);
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
				 List<ChannelUserVO> userList= channelUserService.fetchChannelUsersByStatusForSRAndDelReq(con, requestVO);
			     if(userList.size()==0) {
			    	 response.setMessage("No User Found");
			     }
				 response.setUsersList(userList);
				 response.setStatus(String.valueOf(HttpServletResponse.SC_OK));
				
				 return response;
		 }catch (BTSLBaseException be) {
	        	response.setStatus(String.valueOf(HttpServletResponse.SC_BAD_REQUEST));
	            LOG.error(methodName, "BTSLBaseException " + be.getMessage());
	            try {
	                if (con != null) {
	                    con.rollback();
	                }
	            } catch (Exception e) {
	                LOG.errorTrace(methodName, e);
	            }
	            LOG.errorTrace(methodName, be);
	            response.setMessageCode(be.getMessageKey());
				response.setMessage(RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs()));
				responseSwag.setStatus(HttpServletResponse.SC_BAD_REQUEST);   
				return response;
	            
	        } catch (Exception e) {
	        	response.setStatus(String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
	        	responseSwag.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        	try {
	                if (con != null) {
	                    con.rollback();
	                }
	            } catch (Exception ee) {
	                LOG.errorTrace(methodName, ee);
	            }
	            LOG.error(methodName, "Exception " + e.getMessage());
	            LOG.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "",
	                "Exception:" + e.getMessage());
	            response.setMessage(e.getMessage());
	            return response;
	        } finally {
	            LogFactory.printLog(methodName, " Exited ", LOG);
	            if(mcomCon != null){mcomCon.close("ChannelUserApprovalSuspend#getSuspendReqOrDelReqChannelUsers");mcomCon=null;}
	        }
	}

	@PostMapping(value= "/actionOnUser", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(tags= "Channel Users", value = "Channel users in suspend request to suspend  ", response = ActionOnUserResVo.class,
	  notes = ("Api Info:") + ("\n") + ("") + ("\n")  
				,
				 authorizations = {
		    	            @Authorization(value = "Authorization")})
	        @ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = ActionOnUserResVo.class),
	        @ApiResponse(code = 201, message = "Created"),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
    */
	@io.swagger.v3.oas.annotations.Operation(summary = "${actionOnUser.summary}", description="${actionOnUser.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ActionOnUserResVo.class))
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
	public ActionOnUserResVo saveDeleteOrSuspend(HttpServletResponse responseSwag,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "Action On User", required = true)
			@RequestBody ActionOnUserReqVo requestVO) {
		final String methodName =  "saveDeleteOrSuspend";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered ");
		}
		
		 OAuthUser oAuthUser = null;
		 OAuthUserData oAuthUserData = null;
		 Connection con = null;MComConnectionI mcomCon = null;
		 boolean changeStatus=false;
		 ActionOnUserResVo response = new ActionOnUserResVo();
		 response.setService("SuspendUserService");
		 response.setChangeStatus(changeStatus);
		 Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue
				        (PreferenceI.DEFAULT_LANGUAGE)), (String)
				        (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
		
		 try {
			    mcomCon = new MComConnection();
			    con=mcomCon.getConnection();
			    oAuthUser = new OAuthUser();
				oAuthUserData = new OAuthUserData();
			    oAuthUser.setData(oAuthUserData);
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
				if(BTSLUtil.isNullorEmpty(requestVO.getRequestType())) {
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CAN_NOT_NULL, PretupsI.RESPONSE_FAIL, new String[] {"Request type"}, null);
				}
				if(BTSLUtil.isNullorEmpty(requestVO.getAction())){
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CAN_NOT_NULL, PretupsI.RESPONSE_FAIL, new String[] {"Action type"}, null);
				}
				if(BTSLUtil.isNullorEmpty(requestVO.getLoginId())){
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CAN_NOT_NULL, PretupsI.RESPONSE_FAIL, new String[] {"Login type"}, null);
				}
				if(BTSLUtil.isNullorEmpty(requestVO.getRemarks())) {
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CAN_NOT_NULL, PretupsI.RESPONSE_FAIL, new String[] {"Remarks"}, null);
				}
				if(!(requestVO.getAction().equalsIgnoreCase(PretupsI.USER_REJECTED)||
						requestVO.getAction().equalsIgnoreCase(PretupsI.USER_APPROVE))){
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PROPERTY_INVALID, PretupsI.RESPONSE_FAIL, new String[] {"Login type"}, null);
				}
				if(requestVO.getRequestType().equalsIgnoreCase("SUSPENDAPPROVAL")) {
	            	changeStatus=channelUserService.approvalOrRejectSuspendUser(con, requestVO, oAuthUserData);
	            }
				else if(requestVO.getRequestType().equalsIgnoreCase("DELETEAPPROVAL")) {
	            	changeStatus=channelUserService.approvalOrRejectDeleteUser(con, requestVO, oAuthUserData);
	            }else {
	            	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PROPERTY_INVALID, PretupsI.RESPONSE_FAIL, new String[] {"Request type"}, null);
	            }
				response.setChangeStatus(changeStatus);
				response.setStatus(String.valueOf(HttpServletResponse.SC_OK));
				response.setMessage("sucessful");
				response.setMessageCode("200");
		  }catch (BTSLBaseException be) {
	        	response.setStatus(String.valueOf(HttpServletResponse.SC_BAD_REQUEST));
	        	responseSwag.setStatus(HttpServletResponse.SC_BAD_REQUEST);   
	            LOG.error(methodName, "BTSLBaseException " + be.getMessage());
	            try {
	                if (con != null) {
	                    con.rollback();
	                }
	            } catch (Exception e) {
	                LOG.errorTrace(methodName, e);
	            }
	            LOG.errorTrace(methodName, be);
	            response.setMessageCode(be.getMessageKey());
				response.setMessage(RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs()));
	               return response;
	        } catch (Exception e) {
	        	response.setStatus(String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
	        	responseSwag.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);   
	        	try {
	                if (con != null) {
	                    con.rollback();
	                }
	            } catch (Exception ee) {
	                LOG.errorTrace(methodName, ee);
	            }
	            LOG.error(methodName, "Exception " + e.getMessage());
	            LOG.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "",
	                "Exception:" + e.getMessage());
	            response.setMessage(e.getMessage());
	            return response;
	        } finally {
	            LogFactory.printLog(methodName, " Exited ", LOG);
	            if(mcomCon != null){mcomCon.close("ChannelUserApprovalSuspend#saveDeleteOrSuspend");mcomCon=null;}
	        }
		   return response;
    }
	
	
	
	
	
	
	
	
	
	

}

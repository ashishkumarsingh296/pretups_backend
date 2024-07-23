package com.restapi.channeluser.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;


import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.beanutils.BeanUtils;
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
import com.btsl.pretups.channel.transfer.requesthandler.ViewChannelApprvListResp;
import com.btsl.pretups.channeluser.businesslogic.ApplistReqVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserApprovalVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${ChannelUserApprvListController.name}", description = "${ChannelUserApprvListController.desc}")//@Api(tags= "Channel Users",value="Channel Users")
@RestController
@RequestMapping(value = "/v1/channelUsers")
public class ChannelUserApprvListController {
	
	public static final Log LOG = LogFactory.getLog(ChannelUserApprvListController.class.getName());
	private UserDAO userDAO = new UserDAO();
	
	
	@SuppressWarnings("unchecked")
	@PostMapping(value= "/channelUserApprovalList", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(tags= "Channel Users", value = "Channel user approval list", response = ViewChannelApprvListResp.class,
	  notes = ("Api Info:") + ("\n") + ("1. roleType:'N' signifies System Role, 'Y' signifies Group Role") + ("\n")  
				,
				 authorizations = {
		    	            @Authorization(value = "Authorization")})
	        @ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = ViewChannelApprvListResp.class),
	        @ApiResponse(code = 201, message = "Created"),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${channelUserApprovalList.summary}", description="${channelUserApprovalList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ViewChannelApprvListResp.class))
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

	public ViewChannelApprvListResp getApprovalList(
			HttpServletResponse responseSwag, 
	@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	@Parameter(description = SwaggerAPIDescriptionI.CHANNEL_USER_APPRV_LIST, required = true)
	@RequestBody ApplistReqVO requestVO) throws IOException, SQLException, BTSLBaseException {
		final String methodName =  "getApprovalList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered ");
		}

        LogFactory.printLog(methodName, "Entered requestVO=" + requestVO, LOG);
        Connection con = null;MComConnectionI mcomCon = null;
        ChannelUserVO senderVO;
        
        userDAO = new UserDAO();
        
     
		OAuthUser oAuthUser = null;
		OAuthUserData oAuthUserData = null;
        Locale locale = null;
        ChannelApprvListService channelApprvListService = new ChannelApprvListService();
        ViewChannelApprvListResp response = new ViewChannelApprvListResp();
       try {
        	mcomCon = new MComConnection();con=mcomCon.getConnection();
        	/*
			 * Authentication
			 * @throws BTSLBaseException
			 */
			OAuthenticationUtil.validateTokenApi(headers);
			
			LOG.debug(methodName, "Gson Conversion");
					// Authentication
			oAuthUser = new OAuthUser();
			oAuthUserData = new OAuthUserData();
		    oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			senderVO= userDAO.loadAllUserDetailsByLoginID(con, oAuthUser.getData().getLoginid());
			ApplistReqVO applistReqVONew = new ApplistReqVO();
			BeanUtils.copyProperties(applistReqVONew, requestVO);
			List<UserApprovalVO> listApprList =channelApprvListService.execute(applistReqVONew, con);
			if(listApprList!=null && listApprList.isEmpty() ) {
				 throw new BTSLBaseException(ChannelApprvListService.class.getName(), methodName,
						PretupsErrorCodesI.NO_APPROVAL_LIST_RECORDS);
				
			}else {
			response.setListApprovalList(listApprList);
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(
				new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);
			}
			
			
			
			
			
				
        } catch (BTSLBaseException be) {
        	response.setStatus(String.valueOf(HttpServletResponse.SC_BAD_REQUEST));
        	if(locale ==null)
        	locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            LOG.error(methodName, "BTSLBaseException " + be.getMessage());
            LOG.errorTrace(methodName, be);
            response.setMessageCode(be.getMessageKey());
			response.setMessage(RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs()));
               return response;
            
        } catch (Exception e) {
        	response.setStatus(String.valueOf(HttpServletResponse.SC_BAD_REQUEST));
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

            if(mcomCon != null){mcomCon.close("AddChannelUser#process");mcomCon=null;}
            LogFactory.printLog(methodName, " Exited ", LOG);
        }
		return response;
	}   


}

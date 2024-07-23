package com.restapi.channeluser.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;


import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
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
import com.btsl.pretups.channeluser.businesslogic.ChannelUserApprovalReqVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

/*@Path("/v1/channelUsers")*/
@io.swagger.v3.oas.annotations.tags.Tag(name = "${ChannelUserApprovalAddorEdit.name}", description = "${ChannelUserApprovalAddorEdit.desc}")//@Api(tags= "Channel Users",value="Channel Users")
@RestController
@RequestMapping(value = "/v1/channelUsers")
public class ChannelUserApprovalAddorEdit {
	
public static final Log LOG = LogFactory.getLog(ChannelUserApprovalAddorEdit.class.getName());
//@Autowired
private AddChnlUserService addChnlUserService;

private HttpServletResponse responseSwag = null;

	@SuppressWarnings("unchecked")
	@PostMapping(value= "/channelUserApproval", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(tags= "Channel Users", value = "Add Channel User approval", response = BaseResponse.class,
	  notes = ("Api Info:") + ("\n") + ("1. roleType:'N' signifies System Role, 'Y' signifies Group Role") + ("\n") + 
				("2. roles:It will contain comma separated values for roles") + ("\n") + 
				 ("3. services:It will contain comma separated services code")+ ("\n") +
				 ("4. paymentType:It will contain comma separated payment type modes")+("\n") +
				 ("5. voucherTypes:It will contain comma separated voucher types")+("\n") +
				 ("6. tags(a):commissionProfileID,transferRuleType,transferProfile,control group,lmsProfileID, will be activated if SystemPreference for LMS_APPL=true,TRF_RULE_USER_LEVEL_ALLOW=true,USER_APPROVAL=0")+("\n"),
				 authorizations = {
		    	            @Authorization(value = "Authorization")})
	        @ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
	        @ApiResponse(code = 201, message = "Created"),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
	*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${channelUserApproval.summary}", description="${channelUserApproval.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BaseResponse.class))
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
	public BaseResponse ChannelUserApprovalAddOrEdit(
			HttpServletResponse responseSwag, 
	@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
	@Parameter(description = SwaggerAPIDescriptionI.ADD_CHANNEL_USER_APPRV, required = true)
	@RequestBody ChannelUserApprovalReqVO requestVO) throws IOException, SQLException, BTSLBaseException {
		final String methodName =  "addChannelUserApproval";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered ");
		}

        LogFactory.printLog(methodName, "Entered requestVO=" + requestVO, LOG);
        Connection con = null;MComConnectionI mcomCon = null;
		OAuthUser oAuthUser = null;
		OAuthUserData oAuthUserData = null;
        OperatorUtilI operatorUtili = null;
        AddChnlUserService addChnlUserService = new AddChnlUserService();
        Locale locale = null;
        ChannelUserVO senderVO = null;
        BaseResponse response = null;
        UserDAO userDAO = new UserDAO();
        ChannelUserWebDAO channelUserWebDAO  = new ChannelUserWebDAO(); 
        try {
        	
        	addChnlUserService= new AddChnlUserService();
        	mcomCon = new MComConnection();con=mcomCon.getConnection();
        	response = new BaseResponse();

        	/*
			 * Authentication
			 * @throws BTSLBaseException
			 */
			OAuthenticationUtil.validateTokenApi(headers);
			
			LOG.debug(methodName, "Gson Conversion");
			boolean validateuser = false;
			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			
			// Authentication
			oAuthUser = new OAuthUser();
			oAuthUserData = new OAuthUserData();
		    oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			this.responseSwag = responseSwag;
			senderVO= userDAO.loadAllUserDetailsByLoginID(con, oAuthUser.getData().getLoginid());
			if(senderVO.getCategoryCode().equals(PretupsI.OPERATOR_CATEGORY)) {
				ChannelUserVO chnluserVO =  userDAO.loadUserDetailsFormUserID(con, requestVO.getApproveUserID());
				if(String.valueOf(chnluserVO.getCategoryVO().getSequenceNumber()).equals(PretupsI.BUCKET_ONE)) {  // if distributor parent will ROOT,then take OwnerID
					ChannelUserVO channelOwnerUser =  userDAO.loadUserDetailsFormUserID(con, chnluserVO.getOwnerID());
					senderVO= userDAO.loadAllUserDetailsByLoginID(con, channelOwnerUser.getLoginID());	
				}else {
					senderVO= userDAO.loadAllUserDetailsByLoginID(con, chnluserVO.getParentLoginID());  //Dealer or agent or retailer
				}
			}
			ChannelUserVO channelUserVO = new ChannelUserVO ();  
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.APPROVER_CAN_EDIT))).booleanValue()) {
				response=	addChnlUserService.execute(requestVO, senderVO, con);
			}else {
				channelUserVO=  userDAO.loadUserDetailsByLoginId(con, requestVO.getData().getWebloginid());
			}
			
			
			
			
			

        } catch (BTSLBaseException be) {
        	response.setStatus(400);
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
        	response.setStatus(400);
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
            userDAO = null;
                  if(mcomCon != null){mcomCon.close("AddChannelUser#process");mcomCon=null;}
            LogFactory.printLog(methodName, " Exited ", LOG);
        }
		return response;
	}   
      

   

}

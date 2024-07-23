package com.restapi.channeluser.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LookupsDAO;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.user.requesthandler.ChannelSOSSettlementHandler;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${ChannelUserTransferController.name}", description = "${ˇChannelUserTransferController.desc}")//@Api(tags="Channel Users",value="Channel Users")
@RestController
@RequestMapping(value = "/v1/channelUsers")
public class ChannelUserTransferController {

	public static final Log log = LogFactory.getLog(ChannelUserTransferController.class.getName());
    public static final String  classname = "ChannelUserTransferController";
    
	@Autowired
	static OperatorUtilI operatorUtili = null;
	
    @Autowired
    ChannelUserTransferService channelUserTransferService;
    

	/**
	 * @author sarthak.saini
	 * @param headers
	 * @param requestVO
	 * @param response1
	 * @param httprequest
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/fetchUserTransferDetails",produces = MediaType.APPLICATION_JSON,consumes = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(tags="Channel Users", value = "Fetch Channel User Details",
	  
	  authorizations = {
	            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = ChannelUserTransferResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found")
			})
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${fetchUserTransferDetails.summary}", description="${fetchUserTransferDetails.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ChannelUserTransferResponseVO.class))
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

	public ChannelUserTransferResponseVO getChannelTransferUserDetails(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = SwaggerAPIDescriptionI.CHANNEL_USER_TRANSFER, required = true)
			@RequestBody ChannelUserTransferRequestVO requestVO,
			HttpServletResponse response1, HttpServletRequest httprequest
			)throws Exception {
		final String methodName = "getChannelTransferUserDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		ChannelUserTransferResponseVO response = new ChannelUserTransferResponseVO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		UserDAO userDao = new UserDAO();
		OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
		ChannelUserVO sessionUserVO  = new ChannelUserVO();
		ChannelUserVO channelUserVO = new ChannelUserVO();
		ChannelUserVO channelUserVO1 = new ChannelUserVO();
		Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
		if(!BTSLUtil.isNullString(requestVO.getMsisdn())) {
		 try {
			
				oAuthUser = new OAuthUser();
				oAuthUserData =new OAuthUserData();
				oAuthUser.setData(oAuthUserData);
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response1);
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				sessionUserVO = userDao.loadAllUserDetailsByLoginID(con, oAuthUserData.getLoginid());

				channelUserVO = userDao.loadUserDetailsByMsisdn(con, requestVO.getMsisdn());
				
				
				if(channelUserVO == null) {
					
					throw new BTSLBaseException(classname, methodName,
							PretupsErrorCodesI.EXT_USRADD_INVALID_MSISDN, 0, null);
				}
				if (sessionUserVO.getCategoryCode().equals(channelUserVO.getCategoryCode())) {
					throw new BTSLBaseException(classname, methodName,
							PretupsErrorCodesI.NO_TRANSFER, 0, null); 
				}
				String [] user = {channelUserVO.getUserName()};
				if(channelUserVO.getOwnerID().equalsIgnoreCase(sessionUserVO.getUserID()) || channelUserVO.getParentID().equalsIgnoreCase(sessionUserVO.getUserID())){
					String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USER_ALREADY_PRESENT, user);
					response.setMessageCode(PretupsErrorCodesI.USER_ALREADY_PRESENT);
					response.setMessage(msg);

					throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.USER_ALREADY_PRESENT, 0,user,null);  //add error code user already under same parent
				}
				response.setUserName(channelUserVO.getUserName());
				response.setDomain(channelUserVO.getDomainID());
				response.setCategory(channelUserVO.getCategoryCode());
				channelUserVO1 = userDao.loadUserDetailsFormUserID(con, channelUserVO.getParentID());
				if(channelUserVO1 == null){
					throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.TRANSFER_NOT_ALLOWED, 0,user,null);
				}
				response.setParentName(channelUserVO1.getUserName());
				response.setGeography(channelUserVO.getGeographicalCode());
				response.setStatus(HttpStatus.SC_OK);
				String resmsg = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE),
								(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
						PretupsErrorCodesI.SUCCESS, null);
				response.setMessage(resmsg);	
				response.setMessageCode(PretupsErrorCodesI.SUCCESS);
				response1.setStatus(HttpStatus.SC_OK);
			
			}
		 catch(BTSLBaseException be) {
				log.error(methodName, "Exceptin:e=" + be);
				log.errorTrace(methodName, be);
				if(response.getMessage()==null) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
				response.setMessageCode(be.getMessageKey());
				response.setMessage(msg);}

				if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
					response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
					response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				} else {
					response1.setStatus(HttpStatus.SC_BAD_REQUEST);
					response.setStatus(HttpStatus.SC_BAD_REQUEST);
				}
		 }
		 finally {

				if (mcomCon != null) {
					mcomCon.close(methodName);
					mcomCon = null;
				}
				if (log.isDebugEnabled()) {
					log.debug(methodName, "Exiting:=" + methodName);
				}

			}
		}
		else {
			//next sprint
		}
		return response;
	}
	
	@PostMapping(value = "/sendOTPUserTransfer", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Send OTP for User Transfer", response = BaseResponse.class, notes = ("Api Info:")
			+ ("\n") + ("1. Resend: 'N' if sending first time") + ("\n"),
			  authorizations = {
			            @Authorization(value = "Authorization")})
	
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${sendOTPUserTransfer.summary}", description="${sendOTPUserTransfer.description}",

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

	public BaseResponse sendOTPUserTranfer(
		     @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			 @Parameter(description =SwaggerAPIDescriptionI.OTP_FOR_USER_TRANFER, required = true)
			 @RequestBody ChannelUserTransferOtpRequestVO requestVO, HttpServletResponse response1,
			 HttpServletRequest httprequest) {
		final String methodName = "sentOTPUserTranfer";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		BaseResponse response = null;

		Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
		OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
		try {
			final List<String> modes=Arrays.asList("EMAIL","SMS");
			final List<String> reSends=Arrays.asList("N","Y");
			
			response = new BaseResponse();
			oAuthUser = new OAuthUser();
			oAuthUserData =new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response1);
		
			
			
			if (!modes.contains(requestVO.getMode())) {
				response.setMessageCode(PretupsErrorCodesI.INVALID_MODE);
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_MODE,null);
				response.setMessage(msg);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				return response;
			}
			if (!reSends.contains(requestVO.getReSend())) {
				response.setMessageCode(PretupsErrorCodesI.INVALID_RESEND);
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_RESEND,null);
				response.setMessage(msg);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				return response;
			}
			
			if (BTSLUtil.isNullString(requestVO.getReSend())) {
				response.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_BLANK,
						new String[] { "Resend Field" });
				response.setMessage(msg);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				return response;
			}
			if (BTSLUtil.isNullString(requestVO.getMode())) {
				response.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_BLANK,
						new String[] { "Mode" });
				response.setMessage(msg);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				return response;
			}
			if (BTSLUtil.isNullString(requestVO.getMsisdn())) {
				response.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
				String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_BLANK,
						new String[] { "msisdn" });
				response.setMessage(msg);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				return response;
			}
			
	            String utilClassName = (String) PreferenceCache
		      	    .getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
			try {
				operatorUtili = (OperatorUtilI) Class.forName(utilClassName).newInstance();
			} catch (Exception e) {
				log.errorTrace("static", e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED,
						EventLevelI.FATAL, classname + "[" + methodName + "]", "", "", "",
						"Exception while loading the class at the call:" + e.getMessage());
			}

			 channelUserTransferService.sendOtp(operatorUtili, response, response1,
					requestVO);

		} catch (BTSLBaseException be) {
			log.error(classname, "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(methodName, e);
			response.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			if (response.getMessage() == null) {
				String resmsg = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
						PretupsErrorCodesI.REQ_NOT_PROCESS, null);
				response.setMessage(resmsg);
			}

			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exit ");
		}
		return response;
	}
	
	/**
	 * @author sarthak.saini
	 * @param headers
	 * @param requestVO
	 * @param response1
	 * @param httprequest
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/confirmUserTransfer",produces = MediaType.APPLICATION_JSON,consumes = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(tags="Channel Users", value = "Channel User Transfer",
	  
	  authorizations = {
	            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found")
			})
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${confirmUserTransfer.summary}", description="${confirmUserTransfer.description}",

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

	public BaseResponse confirmUserTransfer(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			 @Parameter(description =SwaggerAPIDescriptionI.CONFIRM_USER_TRANFER, required = true)
			 @RequestBody ConfimChannelUserTransferRequestVO requestVO, HttpServletResponse response1,
			 HttpServletRequest httprequest
			 )throws Exception {
		final String methodName =  "confirmUserTransfer";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		Connection con = null;
        MComConnectionI mcomCon = null;
        BaseResponse response = null;
        LookupsDAO lookupsDAO = null;
        UserVO sessionUserVO = new UserVO();
        response = new BaseResponse();
        String messageArray[] = new String[1];
        UserDAO userDAO =new UserDAO();
		Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));

        try {
        	
        	OAuthUser oAuthUserData=new OAuthUser();
    		oAuthUserData.setData(new OAuthUserData());
    		OAuthenticationUtil.validateTokenApi(oAuthUserData,headers,response1);
    		mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			  if(BTSLUtil.isNullString(requestVO.getOtp())){
					response.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_BLANK,new String[]{"OTP"});
					response.setMessage(msg);							
					 response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		           		response.setStatus(HttpStatus.SC_BAD_REQUEST);
		           		return response;
			   }
      	  
      	channelUserTransferService.validateOTP(con,response, requestVO.getOtp(), requestVO.getMsisdn(), response1);
      	if(response.getStatus()==400) {
      		return response;
      	}
			ChannelUserVO channelUserVO = userDAO.loadUserDetailsByMsisdn(con, requestVO.getMsisdn());
    		
    		String loginId =  oAuthUserData.getData().getLoginid();
    		String msisdn =  oAuthUserData.getData().getMsisdn(); 
    		
			lookupsDAO = new LookupsDAO();
            
            if (!BTSLUtil.isNullString(channelUserVO.getNetworkID())) {
                NetworkVO networkVO = (NetworkVO) NetworkCache.getNetworkByExtNetworkCode(channelUserVO.getNetworkID());
                if(networkVO==null){
                 messageArray[0]= channelUserVO.getNetworkID();
               throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID, messageArray);
                }
            }
			else
			{
				 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_BLANK, 0,null,null);
			}
			String networkCode = channelUserVO.getNetworkID();
			
			boolean validateuser = false;
			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			String identifiertype= "loginId";
			String identifiervalue =loginId ;
			
			validateuser = pretupsRestUtil.validateUser(identifiertype, identifiervalue, networkCode, con);

			if(validateuser == false){
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_USER, 0,null,null);
			}
			UserVO userVO = new UserVO();
			 if(PretupsI.MSISDN.equalsIgnoreCase(identifiertype)){
	            	sessionUserVO = (UserVO) userDAO.loadUserDetailsByMsisdn(con,identifiervalue);
	            	if(sessionUserVO==null){
						throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.INVALID_USER, 0,null,null);
					}
					if(PretupsI.USER_STATUS_CANCELED.equalsIgnoreCase(sessionUserVO.getStatus())||PretupsI.USER_STATUS_DELETED.equalsIgnoreCase(sessionUserVO.getStatus()) ){
						throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.INVALID_USER, 0,null,null);
					}
	            }
	            else if(PretupsI.LOGINID.equalsIgnoreCase(identifiertype)){
	            	sessionUserVO = (UserVO) userDAO.loadAllUserDetailsByLoginID(con,identifiervalue);
	            	if(sessionUserVO==null){
	            		throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.INVALID_USER, 0,null,null);
					}
	            	if(PretupsI.USER_STATUS_CANCELED.equalsIgnoreCase(sessionUserVO.getStatus())||PretupsI.USER_STATUS_DELETED.equalsIgnoreCase(sessionUserVO.getStatus()) ){
						throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.INVALID_USER, 0,null,null);
					}
	            }
			 ArrayList lookupList = new ArrayList();
			
		         	userVO = (UserVO) userDAO.loadUserDetailsByMsisdn(con,requestVO.getMsisdn());
		         	if(userVO==null){
							throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.USER_NOT_FOUND_DELETE, 0,null,null);
						}
						if(PretupsI.USER_STATUS_CANCELED.equalsIgnoreCase(userVO.getStatus()) 
			         			||PretupsI.USER_STATUS_DELETE_REQUEST.equalsIgnoreCase(userVO.getStatus()) 
			         			||PretupsI.USER_STATUS_NEW.equalsIgnoreCase(userVO.getStatus())
			         			||PretupsI.USER_STATUS_SUSPEND_REQUEST.equalsIgnoreCase(userVO.getStatus())
			         			||PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST.equalsIgnoreCase(userVO.getStatus())
			         			||PretupsI.USER_STATUS_BARRED.equalsIgnoreCase(userVO.getStatus())
			         			||PretupsI.USER_STATUS_DELETED.equalsIgnoreCase(userVO.getStatus())
			         			||PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE.equalsIgnoreCase(userVO.getStatus())
			         			){
							lookupList=lookupsDAO.loadLookupsFromLookupCode(con, userVO.getStatus(),PretupsI.USER_STATUS_TYPE);
							   

							LookupsVO lookupsVO = (LookupsVO) lookupList.get(0);  
							String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USER_NOT_DELETED, new String[] {lookupsVO.getLookupName()});
							response.setMessageCode(PretupsErrorCodesI.USER_NOT_DELETED);
							response.setMessage(msg);
							throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.USER_NOT_DELETED,new String[] {lookupsVO.getLookupName()});		
							}
						C2STransferDAO c2STransferDAO = new C2STransferDAO();
						boolean flag = false;
						ArrayList allowedList = c2STransferDAO.loadC2SRulesListForChannelUserAssociation(con,networkCode);
						 ChannelTransferRuleVO channelTransferRuleVO = null;
						for(int i=0;i<allowedList.size();i++){
							channelTransferRuleVO = new ChannelTransferRuleVO();
							channelTransferRuleVO = (ChannelTransferRuleVO) allowedList.get(i);
							if(channelTransferRuleVO.getFromCategory().equalsIgnoreCase(sessionUserVO.getCategoryCode())&& channelTransferRuleVO.getToCategory().equalsIgnoreCase(userVO.getCategoryCode())){
								flag = true;
							}
						}
						if(flag==false){
							throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.INVALID_USER, 0, null);
						}
						String [] user = {channelUserVO.getUserName()};
						if(userVO.getOwnerID().equalsIgnoreCase(sessionUserVO.getUserID()) && userVO.getParentID().equalsIgnoreCase(sessionUserVO.getUserID())){
							String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USER_ALREADY_PRESENT, user);
							response.setMessageCode(PretupsErrorCodesI.USER_ALREADY_PRESENT);
							response.setMessage(msg);
							 throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.USER_ALREADY_PRESENT, 0,user,null);  //add error code user already under same parent
						}
						  /*
			             * Before Transfering user three checks will be perform
			             * a)Check whether the child user is active or not
			             * b)Check the balance of the transfer user
			             * c)Check for no O2C Transfer pending (closed and canceled
			             * Txn)
			             */
						
			            boolean isBalanceFlag = false;
			            boolean isO2CPendingFlag = false;
			            boolean isSOSPendingFlag = false;
			            boolean isLRPendingFlag = false;
			            final Date currentDate = new Date();
			            final boolean isChildFlag = userDAO.isChildUserActive(con, userVO.getUserID());

			            if (isChildFlag) {
			                throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.TRF_CHILD_USR_EXIST, 0,null,null);
			            }

			            else {
			            	// Checking SOS Pending transactions
			            	if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue()){
						        ChannelSOSSettlementHandler channelSOSSettlementHandler = new ChannelSOSSettlementHandler();
						        isSOSPendingFlag = channelSOSSettlementHandler.validateSOSPending(con, userVO.getUserID());
							}
			            }
			            if(isSOSPendingFlag){
			                throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.TRF_SOS_PENDING, 0,null,null);
			            }else {
			            	// Checking for pending LR transactions
			        		if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED))).booleanValue()){
			        			UserTransferCountsVO userTrfCntVO = new UserTransferCountsVO();
			        			UserTransferCountsDAO userTrfCntDAO = new UserTransferCountsDAO();
			        			userTrfCntVO = userTrfCntDAO.selectLastLRTxnID(userVO.getUserID(), con, false, null);
			        			if (userTrfCntVO!=null) 
			        				isLRPendingFlag = true;
			        		}
			            }
			            if(isLRPendingFlag){
			                throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.TRF_LR_PENDING, 0,null,null);
			            }else{ 
			            	// Checking O2C Pending transactions
			                final ChannelTransferDAO transferDAO = new ChannelTransferDAO();
			                isO2CPendingFlag = transferDAO.isPendingTransactionExist(con, userVO.getUserID());
			            }
			            boolean isRestrictedMsisdnFlag = false;
			            boolean isbatchFocPendingTxn = false;
			            if (isO2CPendingFlag) {
			                throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.TRF_O2C_PENDING, 0,null,null);
			            } else {
			                // Checking Batch FOC Pending transactions Ved -
			                // 07/08/06
			                final FOCBatchTransferDAO batchTransferDAO = new FOCBatchTransferDAO();
			                isbatchFocPendingTxn = batchTransferDAO.isPendingTransactionExist(con, userVO.getUserID());
			            }

			            if (isbatchFocPendingTxn) {
			                throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.TRF_FOC_PENDING, 0,null,null);
			            } else {
			                if (PretupsI.STATUS_ACTIVE.equals(userVO.getCategoryVO().getRestrictedMsisdns())) {
			                    final RestrictedSubscriberDAO restrictedSubscriberDAO = new RestrictedSubscriberDAO();
			                    isRestrictedMsisdnFlag = restrictedSubscriberDAO.isSubscriberExistByChannelUser(con, userVO.getUserID());
			                }
			            }
			            if (isRestrictedMsisdnFlag) {
			                throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.TRF_RESTRICTED_MSISDN, 0,null,null);
			            }
			            userVO.setUserID(userVO.getUserID());
		                isBalanceFlag = userDAO.isUserBalanceExist(con, userVO.getUserID());
		                userVO.setStatus(PretupsI.USER_STATUS_SUSPEND);

		              
		       response =  channelUserTransferService.confirmTransferUser(con,mcomCon, channelUserVO,userVO,sessionUserVO,requestVO);
		            
		            
		            
		                
        } catch(BTSLBaseException be) {
			log.error(methodName, "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			if(BTSLUtil.isNullString(response.getMessage())) {
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessage(msg);
			}
			response.setMessageCode(be.getMessageKey());
			
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
	 }
	 finally {

			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting:=" + methodName);
			}

		}
        return response;
	}
	
	
}



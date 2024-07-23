package com.restapi.user.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.MediaType;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.EmailSendToUser;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.UserPinMgmtVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserEventRemarksVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SqlParameterEncoder;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.web.user.businesslogic.UserWebDAO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${PinManagementController.name}", description = "${PinManagementController.desc}")//@Api(tags="User Services")
@RestController
@RequestMapping(value = "/v1/userServices")
public class PinManagementController {
	public static final Log log = LogFactory.getLog(ChannelUserServices.class.getName());
	public static final String classname = "PinManagementController";
	
	private static OperatorUtilI _operatorUtil = null;

    static {/*
      //  final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
           // _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    */}
	
	//@POST
		//@Path("/pinManagement")

		@PostMapping(value= "/pinManagement", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	    @ResponseBody
	    /*@Produces(MediaType.APPLICATION_JSON)*/
		/*@ApiOperation(tags="User Services", value = "Reset or send PIN for channel users", notes= SwaggerAPIDescriptionI.PIN_MANAGEMENT_DESC,
					response = BaseResponse.class,
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

		@io.swagger.v3.oas.annotations.Operation(summary = "${pinManagement.summary}", description="${pinManagement.description}",

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

		public BaseResponse userPinManagement(
				@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				@Parameter(description = SwaggerAPIDescriptionI.RESET_PIN, required = true)// allowableValues = "Y, N")
				@DefaultValue("") @RequestParam("resetPin") String resetPin,
				
				@Parameter(description="User Pin Management VO", required= true)
				@RequestBody UserPinMgmtVO userPinMgmtVO, HttpServletResponse response1 ) throws IOException, SQLException, BTSLBaseException
				
		{
			
			final String methodName = "userPinManagement";

			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}
			Connection con = null;
			MComConnectionI mcomCon = null;
			ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			String tempResetPin = "";
			BaseResponse response = null;
			int resetCount = 0;
			UserDAO userDAO = new UserDAO();
			int updateCount = 0;
			final String[] arr = new String[1];
		    BTSLMessages btslMessage1 = null;
		    String subject = null;
			UserEventRemarksVO remarksVO = null;
			ArrayList<UserEventRemarksVO> pinPswdRemarksList = null;
			ArrayList<String> arguments = null;
			OAuthUser oAuthUser= null;
			OAuthUserData oAuthUserData =null;
			
			String loggenInUser= null;
			try {
				oAuthUser = new OAuthUser();
				oAuthUserData =new OAuthUserData();
				
				oAuthUser.setData(oAuthUserData);
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response1);
				loggenInUser= oAuthUser.getData().getLoginid();
				loggenInUser =  SqlParameterEncoder.encodeParams(loggenInUser);
	        	resetPin =  SqlParameterEncoder.encodeParams(resetPin);

		        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		        try {
		            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
		        } catch (Exception e) {
		            log.errorTrace("static", e);
		            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[initialize]", "", "", "",
		                "Exception while loading the class at the call:" + e.getMessage());
		        }
		    
		        
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				response = new BaseResponse();
				
				/*
				 * Authentication
				 * @throws BTSLBaseException
				 */
				

				
				UserVO loggedInUserVO = null;
				loggedInUserVO = (UserVO) (userDAO.loadAllUserDetailsByLoginID(con, loggenInUser));

				ChannelUserVO childUser = new UserDAO().loadUserDetailsByMsisdn(con, userPinMgmtVO.getMsisdn());
				 
				if (childUser == null) {
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_USER, 0, null, null);
				}
				
				if(childUser.getStatus().equals(PretupsI.STATUS_NEW)) {
	            	throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.USER_APPROVAL, 0, null);
	            }


				if (loggedInUserVO.getUserID().equals(childUser.getUserID())) {
					response.setStatus(PretupsI.RESPONSE_FAIL);
					response.setMessage("Entered login ID is same as yours.");
					response.setMessageCode(PretupsErrorCodesI.FAILED);
	            	response1.setStatus(PretupsI.RESPONSE_FAIL);
	            	return response;
				}

				boolean isUserInHierachy = channelUserDAO.isUserInHierarchy(con, loggedInUserVO.getUserID(), "loginid",
						childUser.getLoginID());

				if (!isUserInHierachy) {
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_NOT_IN_HIERARCHY, 0, null, null);
				}
				childUser.setUserPhoneVO(userDAO.loadUserPhoneVO(con, childUser.getUserID()));
				Locale locale = null;
		        if (childUser.getUserPhoneVO().getLocale() == null) {
		            locale = new Locale(childUser.getUserPhoneVO().getPhoneLanguage(), childUser.getUserPhoneVO().getCountry());
		        } else {
		            locale = childUser.getUserPhoneVO().getLocale();
		        }
				if (BTSLUtil.isEmpty(userPinMgmtVO.getRemarks())) {
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.REMARKS_REQUIRED, 0, null, null);
				}
				if ("Y".equalsIgnoreCase(resetPin)) {
					childUser.setModifiedBy(loggedInUserVO.getUserID());
					childUser.setModifiedOn(new Date());
					// chnlUserPassPinMgmtForm.setResertPin(tempResertPin);
					final UserPhoneVO userPhoneVO = childUser.getUserPhoneVO();

					if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_RANDOM_PIN_GENERATE)).booleanValue()) {
						tempResetPin = _operatorUtil.generateRandomPin();
					} else {
						tempResetPin = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN));
					}
					childUser.setSmsPin(tempResetPin);
					resetCount = channelUserDAO.changePin(con, tempResetPin, childUser);
					if (resetCount > 0) {
						userPhoneVO.setModifiedBy(loggedInUserVO.getUserID());
						userPhoneVO.setModifiedOn(new Date());
						userPhoneVO.setInvalidPinCount(0);
						userPhoneVO.setPinReset("Y");
						updateCount = channelUserDAO.updateSmsPinCounter(con, userPhoneVO);
					}
					else if(userPhoneVO.getInvalidPinCount()<((Integer) PreferenceCache.getControlPreference(PreferenceI.C2S_MAX_PIN_BLOCK_COUNT_CODE, childUser.getNetworkID(), childUser.getCategoryCode())).intValue()){
						response.setStatus(PretupsI.RESPONSE_SUCCESS);
						response.setMessageCode(PretupsErrorCodesI.SUCCESS);
						response.setMessage("PIN of user is not blocked.");
					}
					if (resetCount > 0 && updateCount > 0) {

						int insertCount = 0;
						pinPswdRemarksList = new ArrayList<UserEventRemarksVO>();
						remarksVO = new UserEventRemarksVO();
						remarksVO.setCreatedBy(loggedInUserVO.getCreatedBy());
						remarksVO.setCreatedOn(new Date());
						remarksVO.setEventType(PretupsI.PIN_RESET);

						remarksVO.setMsisdn(userPinMgmtVO.getMsisdn());
						remarksVO.setRemarks(userPinMgmtVO.getRemarks());
						remarksVO.setUserID(childUser.getUserID());
						remarksVO.setUserType(childUser.getUserType());
						remarksVO.setModule(PretupsI.C2S_MODULE);
						pinPswdRemarksList.add(remarksVO);
						insertCount = new UserWebDAO().insertEventRemark(con, pinPswdRemarksList);
						if (insertCount <= 0) {
							// con.rollback();
							mcomCon.finalRollback();
							log.error("resetPin", "Error: while inserting into userEventRemarks Table");
							throw new BTSLBaseException(this, "save", "error.general.processing");
						}
							subject = "User PIN is reset successfully.";//BTSLUtil.getMessage(locale,"channeluser.unblockpin.msg.resetsuccess");
							arr[0] = tempResetPin;
				            btslMessage1 = new BTSLMessages(PretupsErrorCodesI.C2SSUBSCRIBER_RESETPIN_MSG, arr);
				            mcomCon.finalCommit();

					} else 
					{
						mcomCon.finalRollback();
		                throw new BTSLBaseException(this, "unblockPin", "channeluser.unblockpin.msg.resetunsuccess", "viewsubscriberdetail");
					}
					response.setStatus(PretupsI.RESPONSE_SUCCESS);
					response1.setStatus(PretupsI.RESPONSE_SUCCESS);
		            response.setMessageCode(PretupsErrorCodesI.SUCCESS);
		            response.setMessage("User PIN is reset successfully.");
				}
				else 
				{
		            arr[0] = BTSLUtil.decryptText(childUser.getUserPhoneVO().getSmsPin());
		            btslMessage1 = new BTSLMessages(PretupsErrorCodesI.C2SSUBSCRIBER_SENDPIN_MSG, arr);
		            // Email for pin & password
		            subject = "PIN is send successfully.";//BTSLUtil.getMessage(locale,"channeluser.unblockpin.msg.sendmsg.success");
		            if (arr[0] != null) 
		            {
		                	UserWebDAO userwebDAO = new UserWebDAO();
		                    int insertCount = 0;
		                    remarksVO = new UserEventRemarksVO();
		                    pinPswdRemarksList = new ArrayList<UserEventRemarksVO>();
		                    remarksVO.setCreatedBy(loggedInUserVO.getCreatedBy());
		                    remarksVO.setCreatedOn(new Date());
		                    remarksVO.setEventType(PretupsI.PIN_RESEND);
		                 
		                    remarksVO.setMsisdn(userPinMgmtVO.getMsisdn());
		                    remarksVO.setRemarks(userPinMgmtVO.getRemarks());
		                    remarksVO.setUserID(childUser.getUserID());
		                    remarksVO.setUserType(childUser.getUserType());
		                    remarksVO.setModule(PretupsI.C2S_MODULE);
		                    pinPswdRemarksList.add(remarksVO);
		                    insertCount = userwebDAO.insertEventRemark(con, pinPswdRemarksList);
		                    if (insertCount <= 0) {
		                        //con.rollback();
		                    	mcomCon.finalRollback();
		                        log.error("sendPin", "Error: while inserting into userEventRemarks Table");
		                        throw new BTSLBaseException(this, "save", "error.general.processing");
		                    }
		                    mcomCon.finalCommit();
		                } 
		            
		            response.setStatus(PretupsI.RESPONSE_SUCCESS);
					response1.setStatus(PretupsI.RESPONSE_SUCCESS);
		            response.setMessageCode(PretupsErrorCodesI.SUCCESS);
		            response.setMessage("User PIN is send successfully.");
		            }
				
				
		        // PushMessage pushMessage=new
		        // PushMessage(channelUserVO.getMsisdn(),btslMessage1,null,null,locale,channelUserVO.getNetworkID());
		        final PushMessage pushMessage = new PushMessage(childUser.getMsisdn(), btslMessage1, null, null, locale, childUser.getNetworkID(),
		            "SMS will be delivered shortly thanking you");
		        pushMessage.push();
		        // Email for pin & password
		        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(childUser.getEmail()) 
		        		&& !"SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
		            final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslMessage1, locale, childUser.getNetworkID(), "Email will be delivered shortly",
		            		childUser, loggedInUserVO);
		            emailSendToUser.sendMail();
		        }
				
			} catch (BTSLBaseException be) {
				log.error(methodName, "Exception:e=" + be);
				log.errorTrace(methodName, be);
				response.setStatus(400);
				String resmsg = RestAPIStringParser.getMessage(
						new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), be.getMessage(),
						BTSLUtil.isNullOrEmptyList(arguments) ? null : arguments.toArray(new String[arguments.size()]));
				response.setMessageCode(be.getMessage());
				response1.setStatus(PretupsI.RESPONSE_FAIL);
				response.setMessage(resmsg);
			} catch (Exception e) {
				log.error(methodName, "Exception:e=" + e);
				log.errorTrace(methodName, e);
				response.setStatus(400);
				response1.setStatus(PretupsI.RESPONSE_FAIL);

				response.setMessageCode(e.getMessage());
				response.setMessage(e.getMessage());
			} finally {
				try {
					if (mcomCon != null) {
						mcomCon.close("ChannelUserServices#" + methodName);
						mcomCon = null;
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
		
		
		
	}

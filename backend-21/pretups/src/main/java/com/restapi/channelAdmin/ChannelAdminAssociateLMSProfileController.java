package com.restapi.channelAdmin;

import java.sql.Connection;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Locale;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.BaseResponseRedoclyCommon;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.restapi.channelAdmin.requestVO.BulkUserUploadRequestVO;
import com.restapi.channelAdmin.responseVO.BulkUserUploadResponseVO;
import com.restapi.channelAdmin.service.ChannelAdminUserHierarchyService;
import com.restapi.networkadmin.messagemanagement.requestVO.MessageUploadRequestVO;
import com.restapi.networkadmin.messagemanagement.responseVO.MessagesBulkResponseVO;
import com.restapi.superadmin.networkmanagement.responseVO.NetworkListResponseVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author aarti.uniyal
 */

@Tag(name = "${ChannelAdminAssociateLMSProfileController.name}", description = "${ChannelAdminAssociateLMSProfileController.desc}")//@Api(tags ="Channel Admin", value="Channel Admin")//@Api(tags ="Channel Admin", value="Channel Admin")
@RestController	
@RequestMapping(value = "/v1/channeladmin")
public class ChannelAdminAssociateLMSProfileController {

	protected final Log _log = LogFactory.getLog(getClass().getName());
	protected final Log LOG = LogFactory.getLog(getClass().getName());

	public static final Log log = LogFactory.getLog(ChannelAdminAssociateLMSProfileController.class.getName());
	StringBuilder loggerValue= new StringBuilder(); 
	
	@Autowired
	private ChannelAdminAssociateLMSProfileService channelAdminAssociateLMSProfileService;
		
	/**
	 * @param headers
	 * @param response1
	 * @param httpServletRequest
	 * @return
	 * @throws Exception
	 */
	
	@GetMapping(value = "/loadProfileList",produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	@Operation(summary = "${networkManagmentController.getNetworkListByAdmin.name}", description = "${networkManagmentController.getNetworkListByAdmin.desc}",

			responses = {
					@ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = NetworkListResponseVO.class))) }

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
	
	public ProfileListResponseVO GetProfileList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest)throws Exception {
		
		final String methodName = "GetProfileList";
		if(log.isDebugEnabled()) {
			log.debug(methodName, "Entered : ");
		}
		
		ProfileListResponseVO response = new ProfileListResponseVO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE,SystemPreferences.DEFAULT_COUNTRY);
		String loginID = null;
		try {
			
			/*
			 * Authentication
			 * 
			 * @throws BTSLBaseException
			 */
			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			loginID = OAuthUserData.getData().getLoginid();
			response = channelAdminAssociateLMSProfileService.viewProfileList(con, loginID, response1);
			
		}catch(BTSLBaseException btslBaseException) {
			log.error("", "Exceptin:e=" + btslBaseException);
			log.errorTrace(methodName, btslBaseException);
			String msg = RestAPIStringParser.getMessage(locale, btslBaseException.getMessageKey(), null);
			response.setMessageCode(btslBaseException.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(btslBaseException.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		}catch(Exception exception) {
			log.error("", "Exceptin:e=" + exception);
			log.errorTrace(methodName, exception);
			String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NETWORK_LIST_WENT_WRONG, null);
			response.setMessageCode(PretupsErrorCodesI.NETWORK_LIST_WENT_WRONG);
			response.setMessage(msg);
		}
		finally {

			if (mcomCon != null) {
				mcomCon.close("GetProfileList");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting:=" + methodName);
			}

		}
		
		
		return response;
		
	}
		
	/**
     * @param headers
     * @param httpServletResponse
     * @param httpServletRequest
     * @return
     * @throws Exception
     */
	
    @GetMapping(value = "/loadDownloadFileAssocation", produces = MediaType.APPLICATION_JSON)
    @Operation(summary = "${MessageManagementController.Downloadmessagefile.name}", description = "${MessageManagementController.Downloadmessagefile.desc}",

            responses = {
                    @ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MessagesBulkResponseVO.class))) }

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

    public FileAssocationResponseVO downloadFileAssocation(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse httpServletResponse,
            HttpServletRequest httpServletRequest,
            @RequestParam("categoryCode") String categoryCode,
            @RequestParam("domainCode") String domainCode,
            @RequestParam("gradeCode") String gradeCode,
            @RequestParam("geographyCode")  String geographyCode
    ) throws Exception {
        final String METHOD_NAME = "downloadFileAssocation";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }
        FileAssocationResponseVO response = new FileAssocationResponseVO();

        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        String loginUserID = null;
        try {

            /*
             * Authentication
             *
             * @throws BTSLBaseException
             */
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, httpServletResponse);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            loginUserID = OAuthUserData.getData().getLoginid();
            response = channelAdminAssociateLMSProfileService.downloadFileAssocation(con,categoryCode,domainCode,gradeCode,geographyCode, locale, loginUserID, httpServletResponse);
            if (response != null) {
                response.setStatus((org.apache.http.HttpStatus.SC_OK));
                String resmsg = RestAPIStringParser.getMessage(locale,
                        PretupsErrorCodesI.MESSAGE_DOWNLOADED_SUCCESSFULLY, null);
                response.setMessage(resmsg);
                response.setMessageCode(PretupsErrorCodesI.MESSAGE_DOWNLOADED_SUCCESSFULLY);
            }
        } catch (BTSLBaseException be) {
            log.error(METHOD_NAME, "Exception=" + be);
            log.errorTrace(METHOD_NAME, be);
            String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
            response.setMessageCode(be.getMessageKey());
            response.setMessage(msg);

            if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
                httpServletResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
                response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            } else {
                httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error(METHOD_NAME, "Exception:e=" + e);
            log.errorTrace(METHOD_NAME, e);
            response.setStatus((HttpStatus.SC_BAD_REQUEST));
            String resmsg = RestAPIStringParser.getMessage(
                    new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                    PretupsErrorCodesI.MESSAGES_MESSAGESMANAGEMENT_DOWNLOAD_FAILED, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.MESSAGES_MESSAGESMANAGEMENT_DOWNLOAD_FAILED);
            httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
        } finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting" + METHOD_NAME);
            }
        }
        return response;
    }
    
    /**
     * @param headers
     * @param httpServletResponse
     * @param httpServletRequest
     * @param requestVO
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/uploadFileAssocation", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    /*@ApiOperation(value = "bulk update the messages from the uploaded file", response = MessageResponseVO.class, authorizations = {
            @Authorization(value = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = MessagesBulkResponseVO.class),
            @ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found") })*/

    @Operation(summary = "${MessageManagementController.UploadMessageFile.name}", description = "${MessageManagementController.UploadMessageFile.desc}",

            responses = {
                    @ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MessagesBulkResponseVO.class))) }

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

    public FileAssocationResponseVO uploadFileAssocation(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse httpServletResponse,
            HttpServletRequest httpServletRequest, @RequestBody FileAssociationUploadRequestVO requestVO) throws Exception {
    	final String METHOD_NAME = "uploadMessageFile";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }
        FileAssocationResponseVO response = new FileAssocationResponseVO();

        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        String loginUserID = null;
        try {

            /*
             * Authentication
             *
             * @throws BTSLBaseException
             */
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, httpServletResponse);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            loginUserID = OAuthUserData.getData().getLoginid();
            response = channelAdminAssociateLMSProfileService.uploadFileAssocation(con, locale, loginUserID, httpServletResponse, requestVO,requestVO.getSetId(),requestVO.getCategoryCode(),requestVO.getGeographyCode(),requestVO.getGradeCode());

        } catch (BTSLBaseException be) {
            log.error(METHOD_NAME, "Exception=" + be);
            log.errorTrace(METHOD_NAME, be);
            String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
            response.setMessageCode(be.getMessageKey());
            response.setMessage(msg);

            if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
                httpServletResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
                response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            } else {
                httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error(METHOD_NAME, "Exception:e=" + e);
            log.errorTrace(METHOD_NAME, e);
            response.setStatus((HttpStatus.SC_BAD_REQUEST));
            String resmsg = RestAPIStringParser.getMessage(
                    new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                    PretupsErrorCodesI.MESSAGES_MESSAGESMANAGEMENT_UPLOAD_FAILED, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.MESSAGES_MESSAGESMANAGEMENT_UPLOAD_FAILED);
            httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
        } finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting" + METHOD_NAME);
            }
        }
        return response;
    }

    /**
     * @param headers
     * @param httpServletRequest
     * @param httpServletResponse
     * @param requestVO
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/addAssociatePromotions", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${MessageManagementController.UploadMessageFile.name}", description = "${MessageManagementController.UploadMessageFile.desc}",

            responses = {
                    @ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MessagesBulkResponseVO.class))) }

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

    public FileAssocationResponseVO addAssociatePromotions(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse httpServletResponse,
            HttpServletRequest httpServletRequest, @RequestBody FileAssociationUploadRequestVO requestVO) throws Exception {
    	 final String METHOD_NAME = "addAssociatePromotions";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }
        FileAssocationResponseVO response = new FileAssocationResponseVO();

        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        String loginUserID = null;
        try {
            /*
             * Authentication
             *
             * @throws BTSLBaseException
             */
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, httpServletResponse);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            loginUserID = OAuthUserData.getData().getLoginid();
            response = channelAdminAssociateLMSProfileService.addAssociatePromotions(con, locale, loginUserID, httpServletResponse, requestVO,requestVO.getSetId(),requestVO.getDomainCode(),requestVO.getCategoryCode(),requestVO.getGeographyCode(),requestVO.getGradeCode());

        } catch (BTSLBaseException be) {
            log.error(METHOD_NAME, "Exception=" + be);
            log.errorTrace(METHOD_NAME, be);
            String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
            response.setMessageCode(be.getMessageKey());
            response.setMessage(msg);

            if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
                httpServletResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
                response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            } else {
                httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error(METHOD_NAME, "Exception:e=" + e);
            log.errorTrace(METHOD_NAME, e);
            response.setStatus((HttpStatus.SC_BAD_REQUEST));
            String resmsg = RestAPIStringParser.getMessage(
                    new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                    PretupsErrorCodesI.MESSAGES_MESSAGESMANAGEMENT_UPLOAD_FAILED, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.MESSAGES_MESSAGESMANAGEMENT_UPLOAD_FAILED);
            httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
        } finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting" + METHOD_NAME);
            }
        }
        return response;
    }

}

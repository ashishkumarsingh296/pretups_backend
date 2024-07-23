package com.restapi.networkadmin.messagemanagement;

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
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.networkadmin.messagemanagement.requestVO.MessageRequestVO;
import com.restapi.networkadmin.messagemanagement.requestVO.MessageUploadRequestVO;
import com.restapi.networkadmin.messagemanagement.responseVO.MessageResponseVO;
import com.restapi.networkadmin.messagemanagement.responseVO.MessagesBulkResponseVO;
import com.restapi.networkadmin.messagemanagement.service.MessageManagementServiceI;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Locale;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${MessageManagementController.name}", description = "${MessageManagementController.desc}")
// @Api(tags

@RestController
@RequestMapping(value = "/v1/networkadmin/messagemanagement")
public class MessageManagementController {
    public static final Log log = LogFactory.getLog(MessageManagementController.class.getName());
    public static final String classname = "MessageManagementController";

    @Autowired
    private MessageManagementServiceI managementServiceI;

    /**
     * @param headers
     * @param httpServletRequest
     * @param httpServletResponse
     * @param messageCode
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/message", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    /*@ApiOperation(value = "load the message Detail", response = MessageResponseVO.class, authorizations = {
            @Authorization(value = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = MessageResponseVO.class),
            @ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found") })*/
    @Operation(summary = "${MessageManagementController.LoadMessageDetails.name}", description = "${MessageManagementController.LoadMessageDetails.desc}",

            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MessageResponseVO.class))) }

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
    public MessageResponseVO loadMessageDetails(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse httpServletResponse,
            HttpServletRequest httpServletRequest,
            @Parameter(description = "messageCode") @RequestParam("messageCode") String messageCode
    ) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("loadMessageDetails", "Entered");
        }
        final String METHOD_NAME = "loadMessageDetails";
        MessageResponseVO response = new MessageResponseVO();

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
            response = managementServiceI.loadMessageDetails(con, locale, loginUserID, messageCode, httpServletResponse);
            if (response != null) {
                response.setStatus((org.apache.http.HttpStatus.SC_OK));
                String resmsg = RestAPIStringParser.getMessage(locale,
                        PretupsErrorCodesI.LOAD_MESSAGE_MANAGEMENT_LABEL_SUCCESS, null);
                response.setMessage(resmsg);
                response.setMessageCode(PretupsErrorCodesI.LOAD_MESSAGE_MANAGEMENT_LABEL_SUCCESS);
            }
        } catch (BTSLBaseException be) {
            log.error(METHOD_NAME, "Exception=" + be);
            log.errorTrace(METHOD_NAME, be);
            String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
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
                    PretupsErrorCodesI.LOAD_MESSAGE_MANAGEMENT_LABEL_FAILED, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.LOAD_MESSAGE_MANAGEMENT_LABEL_FAILED);
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
    @PostMapping(value = "/modifymessage", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    /*@ApiOperation(value = "modify the message", response = MessageResponseVO.class, authorizations = {
            @Authorization(value = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found") })*/
    @Operation(summary = "${MessageManagementController.ModifyMessageDetails.name}", description = "${MessageManagementController.ModifyMessageDetails.desc}",

            responses = {
                    @ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MessageResponseVO.class))) }

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
    public BaseResponse modifyMessageDetails(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse httpServletResponse,
            HttpServletRequest httpServletRequest, @RequestBody MessageRequestVO requestVO) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("loadMessageDetails", "Entered");
        }
        final String METHOD_NAME = "loadMessageDetails";
        BaseResponse response = new BaseResponse();

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
            response = managementServiceI.modifyMessageDetails(con, locale, loginUserID, httpServletResponse, requestVO);

        } catch (BTSLBaseException be) {
            log.error(METHOD_NAME, "Exception=" + be);
            log.errorTrace(METHOD_NAME, be);
            String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
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
                    PretupsErrorCodesI.MESSAGE_MANAGEMENT_MODIFY_FAILED, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.MESSAGE_MANAGEMENT_MODIFY_FAILED);
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
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/downloadmessagefile", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
   /* @ApiOperation(value = "download all the messages", response = MessageResponseVO.class, authorizations = {
            @Authorization(value = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = MessagesBulkResponseVO.class),
            @ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found") })*/
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

    public MessagesBulkResponseVO downloadmessagefile(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse httpServletResponse,
            HttpServletRequest httpServletRequest
    ) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("downloadMessageList", "Entered");
        }
        final String METHOD_NAME = "downloadMessageList";
        MessagesBulkResponseVO response = new MessagesBulkResponseVO();

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
            response = managementServiceI.downloadMessageFile(con, locale, loginUserID, httpServletResponse);
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
     * @param httpServletRequest
     * @param httpServletResponse
     * @param requestVO
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/uploadMessageFile", produces = MediaType.APPLICATION_JSON)
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

    public MessagesBulkResponseVO uploadMessageFile(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse httpServletResponse,
            HttpServletRequest httpServletRequest, @RequestBody MessageUploadRequestVO requestVO) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("uploadMessageFile", "Entered");
        }
        final String METHOD_NAME = "uploadMessageFile";
        MessagesBulkResponseVO response = new MessagesBulkResponseVO();

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
            response = managementServiceI.uploadMessages(con, locale, loginUserID, httpServletResponse, requestVO);

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

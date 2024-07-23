package com.restapi.superadmin.serviceclassmgmt;


import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
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
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.restapi.networkadmin.responseVO.ServiceClassListResponseVO;
import com.restapi.superadmin.serviceclassmgmt.requestVO.AddServiceClassRequestVO;
import com.restapi.superadmin.serviceclassmgmt.responseVO.AddServiceClassResponseVO;
import com.restapi.superadmin.serviceclassmgmt.service.AddServiceClassService;
import com.restapi.superadmin.serviceclassmgmt.service.DeleteServiceClassService;
import com.restapi.superadmin.serviceclassmgmt.service.ModifyServiceClassService;
import com.restapi.superadmin.serviceclassmgmt.service.ServiceClassListService;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Locale;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${ServiceClassManagementController.name}", description = "${ServiceClassManagementController.desc}")//@Api(tags = "Super Admin", value = "Super Admin")
@RestController
@RequestMapping("v1/superadmin/serviceclass")
public class ServiceClassManagementController {
    public static final Log log = LogFactory.getLog(ServiceClassManagementController.class.getName());
    public static final String classname = "ServiceClassManagementController";

    @Autowired
    ServiceClassListService serviceClassListService;

    @Autowired
    AddServiceClassService addServiceClassService;

    @Autowired
    ModifyServiceClassService modifyServiceClassService;

    @Autowired
    DeleteServiceClassService deleteServiceClassService;

    @GetMapping(value = "/getlist", produces = MediaType.APPLICATION_JSON)
    /*@ApiOperation(value = "Get list for service class", response = ServiceClassListResponseVO.class, authorizations = {
            @Authorization(value = "Authorization")})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = ServiceClassListResponseVO.class),
            @ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")})
    */

    @io.swagger.v3.oas.annotations.Operation(summary = "${getlist.summary}", description="${getlist.description}",

            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ServiceClassListResponseVO.class))
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

    public ServiceClassListResponseVO getServiceClassList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                                          HttpServletResponse responseSwag, @Parameter @RequestParam("id") String id) throws Exception {
        final String methodName = "getServiceClassList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        ServiceClassListResponseVO responseVO = new ServiceClassListResponseVO();
        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, responseSwag);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();

            responseVO = serviceClassListService.getServiceClassList(con, locale, id);
        } catch (BTSLBaseException be) {
            log.error("", "Exceptin:e=" + be);
            log.errorTrace(methodName, be);
            String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
            responseVO.setMessageCode(be.getMessageKey());
            responseVO.setMessage(msg);

            if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
                responseVO.setStatus(HttpStatus.SC_UNAUTHORIZED);
                responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
            } else {
                responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
                responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            responseVO.setStatus((HttpStatus.SC_BAD_REQUEST));
            String resmsg = RestAPIStringParser.getMessage(
                    new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                    e.getMessage(), null);
            responseVO.setMessage(resmsg);
            responseVO.setMessageCode(PretupsErrorCodesI.FAILED);
            responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
        } finally {

            if (mcomCon != null) {
                mcomCon.close(methodName);
                mcomCon = null;
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting:=" + methodName);
            }
            if (con != null)
                con.close();
        }
        return responseVO;
    }

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    /*@ApiOperation(value = "Add Service Class", response = BaseResponse.class, authorizations = {
            @Authorization(value = "Authorization")})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")})
    */

    @io.swagger.v3.oas.annotations.Operation(summary = "${add.summary}", description="${add.description}",

            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AddServiceClassResponseVO.class))
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

    public AddServiceClassResponseVO addServiceClass(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                                     HttpServletResponse responseSwag, @Parameter(description = SwaggerAPIDescriptionI.ADD_SERVICE_CLASS,  required = true) @RequestBody AddServiceClassRequestVO requestVO, HttpServletRequest httpServletRequest) throws Exception {
        final String methodName = "addServiceClass";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        AddServiceClassResponseVO responseVO = new AddServiceClassResponseVO();
        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, responseSwag);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            UserVO userVO = new UserDAO().loadUsersDetails(con, OAuthUserData.getData().getMsisdn());

            responseVO = addServiceClassService.add(con, locale, requestVO, userVO);
        } catch (BTSLBaseException be) {
            log.error("", "Exceptin:e=" + be);
            log.errorTrace(methodName, be);
            String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
            responseVO.setMessageCode(be.getMessageKey());
            responseVO.setMessage(msg);

            if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
                responseVO.setStatus(HttpStatus.SC_UNAUTHORIZED);
                responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
            } else {
                responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
                responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            responseVO.setStatus((HttpStatus.SC_BAD_REQUEST));
            String resmsg = RestAPIStringParser.getMessage(
                    new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                    e.getMessage(), null);
            responseVO.setMessage(resmsg);
            responseVO.setMessageCode(PretupsErrorCodesI.FAILED);
            responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
        } finally {

            if (mcomCon != null) {
                mcomCon.close(methodName);
                mcomCon = null;
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting:=" + methodName);
            }
            if (con != null)
                con.close();
        }
        return responseVO;
    }

    @PostMapping(value = "/modify", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    /*@ApiOperation(value = "Modify Service Class", response = BaseResponse.class, authorizations = {
            @Authorization(value = "Authorization")})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")})
    */
    @io.swagger.v3.oas.annotations.Operation(summary = "${modify.summary}", description="${modify.description}",

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


    public BaseResponse modifyServiceClass(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                           HttpServletResponse responseSwag, @Parameter(description = SwaggerAPIDescriptionI.MODIFY_SERVICE_CLASS,  required = true) @RequestBody AddServiceClassRequestVO requestVO, HttpServletRequest httpServletRequest) throws Exception {
        final String methodName = "modifyServiceClass";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        BaseResponse responseVO = new BaseResponse();
        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, responseSwag);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            UserVO userVO = new UserDAO().loadUsersDetails(con, OAuthUserData.getData().getMsisdn());

            responseVO = modifyServiceClassService.modify(con, locale, requestVO, userVO);
        } catch (BTSLBaseException be) {
            log.error("", "Exceptin:e=" + be);
            log.errorTrace(methodName, be);
            String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
            responseVO.setMessageCode(be.getMessageKey());
            responseVO.setMessage(msg);

            if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
                responseVO.setStatus(HttpStatus.SC_UNAUTHORIZED);
                responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
            } else {
                responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
                responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            responseVO.setStatus((HttpStatus.SC_BAD_REQUEST));
            String resmsg = RestAPIStringParser.getMessage(
                    new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                    e.getMessage(), null);
            responseVO.setMessage(resmsg);
            responseVO.setMessageCode(PretupsErrorCodesI.FAILED);
            responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
        } finally {

            if (mcomCon != null) {
                mcomCon.close(methodName);
                mcomCon = null;
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting:=" + methodName);
            }
            if (con != null)
                con.close();
        }
        return responseVO;
    }

    @GetMapping(value = "/delete", produces = MediaType.APPLICATION_JSON)
    /*@ApiOperation(value = "Delete service class", response = BaseResponse.class, authorizations = {
            @Authorization(value = "Authorization")})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")})
    */
    @io.swagger.v3.oas.annotations.Operation(summary = "${delete.summary}", description="${delete.description}",

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

    public BaseResponse deleteServiceClass(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                           HttpServletResponse responseSwag, @Parameter @RequestParam("id") String id, @Parameter @RequestParam("name") String name) throws Exception {
        final String methodName = "deleteServiceClass";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        BaseResponse responseVO = new BaseResponse();
        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, responseSwag);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            UserVO userVO = new UserDAO().loadUsersDetails(con, OAuthUserData.getData().getMsisdn());

            responseVO = deleteServiceClassService.delete(con, locale, id, userVO, name);
        } catch (BTSLBaseException be) {
            log.error("", "Exceptin:e=" + be);
            log.errorTrace(methodName, be);
            String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
            responseVO.setMessageCode(be.getMessageKey());
            responseVO.setMessage(msg);

            if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
                responseVO.setStatus(HttpStatus.SC_UNAUTHORIZED);
                responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
            } else {
                responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
                responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            responseVO.setStatus((HttpStatus.SC_BAD_REQUEST));
            String resmsg = RestAPIStringParser.getMessage(
                    new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                    e.getMessage(), null);
            responseVO.setMessage(resmsg);
            responseVO.setMessageCode(PretupsErrorCodesI.FAILED);
            responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
        } finally {

            if (mcomCon != null) {
                mcomCon.close(methodName);
                mcomCon = null;
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting:=" + methodName);
            }
            if (con != null)
                con.close();
        }
        return responseVO;
    }

}

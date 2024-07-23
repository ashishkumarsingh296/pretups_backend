package com.restapi.superadmin.interfacemanagement;


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
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.superadmin.interfacemanagement.requestVO.InterfaceDetailRequestVO;
import com.restapi.superadmin.interfacemanagement.requestVO.ModifyInterfaceDetailRequestVO;
import com.restapi.superadmin.interfacemanagement.responseVO.InterfaceDetailResponseVO;
import com.restapi.superadmin.interfacemanagement.responseVO.InterfaceTypeResponseVO;
import com.restapi.superadmin.interfacemanagement.responseVO.ModifyInterfaceDetailResponseVO;
import com.restapi.superadmin.interfacemanagement.service.InterfaceManagementServiceI;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Locale;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${InterfaceManagementController.name}", description = "${InterfaceManagementController.desc}")//@Api(tags="Super Admin")
@RestController
@RequestMapping(value = "/v1/superadmin/intrfcemgmt")
public class InterfaceManagementController extends HttpServlet {
    public static final Log log = LogFactory.getLog(InterfaceManagementController.class.getName());
    public static final String classname = "InterfaceManagementController";

    @Autowired
    InterfaceManagementServiceI interfaceManagementServiceI;

    @GetMapping(value= "/load", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    /*@ApiOperation(value = "populate the Details of Interface.",
            response = InterfaceDetailResponseVO.class,
            authorizations = {
                    @Authorization(value = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = InterfaceDetailResponseVO.class),
            @ApiResponse(code = 400, message = "Bad Request" ),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")
    })*/

    @io.swagger.v3.oas.annotations.Operation(summary = "${load.summary}", description="${load.description}",

            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = InterfaceDetailResponseVO.class))
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


    public InterfaceDetailResponseVO loadInterfaceDetails(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse responseSwag, @Parameter(description = "category", required = true) @RequestParam("category") String interfaceCategory) throws Exception{

        final String methodName = "loadInterfaceDetails";
        if(log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        InterfaceDetailResponseVO response = new InterfaceDetailResponseVO();

        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        String loginUserID = null;

        try {
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();

            /*
             * Authentication
             *
             * @throws BTSLBaseException
             */
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, responseSwag);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            loginUserID = OAuthUserData.getData().getLoginid();

            response = interfaceManagementServiceI.getInterfaceDetails(headers, responseSwag, con, loginUserID, locale, interfaceCategory);
        }
        catch (BTSLBaseException be) {
            log.error("", "Exceptin:e=" + be);
            log.errorTrace(methodName, be);
            String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
            ((BaseResponse) response).setMessageCode(be.getMessageKey());
            response.setMessage(msg);

            if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
                responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
                ((BaseResponse) response).setStatus(HttpStatus.SC_UNAUTHORIZED);
            } else {
                responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            String resmsg = RestAPIStringParser.getMessage(
                    new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                    PretupsErrorCodesI.ERROR_RESPONSE, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.ERROR_RESPONSE);
            responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
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
        return response;
    }


    @PostMapping(value= "/add", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    /*@ApiOperation(value = "Add Interface Details.",
            response = InterfaceDetailResponseVO.class,
            authorizations = {
                    @Authorization(value = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = InterfaceDetailResponseVO.class),
            @ApiResponse(code = 400, message = "Bad Request" ),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")
    })*/

    @io.swagger.v3.oas.annotations.Operation(summary = "${add.summary}", description="${add.description}",

            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = InterfaceDetailResponseVO.class))
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


    public InterfaceDetailResponseVO addInterfaceDetails(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, @Parameter(description = "category", required = true) @RequestParam("category") String interfaceCategory, @RequestBody InterfaceDetailRequestVO request) throws Exception {

        final String methodName = "addInterfaceDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        InterfaceDetailResponseVO response = null;

        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        String loginID = null;

        try {

            /*
             * Authentication
             *
             * @throws BTSLBaseException
             */

            response = new InterfaceDetailResponseVO();
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, httpServletResponse);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            loginID = OAuthUserData.getData().getLoginid();
            response = interfaceManagementServiceI.addInterfaceDetails(headers, con, loginID, locale, request, interfaceCategory);

        }catch (BTSLBaseException be) {
            log.error(methodName, "Exception:e=" + be);
            log.errorTrace(methodName, be);
            if (!BTSLUtil.isNullString(be.getMessage())) {
                String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
                response.setMessageCode(be.getMessage());
                response.setMessage(msg);
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
            }

        }
        catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus((HttpStatus.SC_BAD_REQUEST));
            String resmsg = RestAPIStringParser.getMessage(
                    new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                    PretupsErrorCodesI.ADD_INTERFACE_DETAIL_FAIL, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.ADD_INTERFACE_DETAIL_FAIL);
            httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
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


        return response;
    }

    @GetMapping(value= "/getdetailsbyid", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    /*@ApiOperation(value = "get the Details of Interface.",
            response = InterfaceDetailResponseVO.class,
            authorizations = {
                    @Authorization(value = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = InterfaceDetailResponseVO.class),
            @ApiResponse(code = 400, message = "Bad Request" ),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")
    })*/

    @io.swagger.v3.oas.annotations.Operation(summary = "${getdetailsbyid.summary}", description="${getdetailsbyid.description}",

            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ModifyInterfaceDetailResponseVO.class))
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


    public ModifyInterfaceDetailResponseVO getInterfaceDetailsById(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse responseSwag, @Parameter(description = "id", required = true) @RequestParam("id") String interfaceId) throws Exception{

        final String methodName = "getInterfaceDetailsById";
        if(log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        ModifyInterfaceDetailResponseVO response = new ModifyInterfaceDetailResponseVO();

        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        String loginUserID = null;

        try {
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();

            /*
             * Authentication
             *
             * @throws BTSLBaseException
             */
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, responseSwag);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            loginUserID = OAuthUserData.getData().getLoginid();

            response = interfaceManagementServiceI.getInterfaceDetailsByInterfaceId(headers, responseSwag, con, loginUserID, locale, interfaceId);
        }
        catch (BTSLBaseException be) {
            log.error("", "Exceptin:e=" + be);
            log.errorTrace(methodName, be);
            String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
            ((BaseResponse) response).setMessageCode(be.getMessageKey());
            response.setMessage(msg);

            if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
                responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
                ((BaseResponse) response).setStatus(HttpStatus.SC_UNAUTHORIZED);
            } else {
                responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            String resmsg = RestAPIStringParser.getMessage(
                    new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                    PretupsErrorCodesI.ERROR_RESPONSE, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.ERROR_RESPONSE);
            responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
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
        return response;
    }

    @PostMapping(value= "/modify", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    /*@ApiOperation(value = "modify Interface Details.",
            response = InterfaceDetailResponseVO.class,
            authorizations = {
                    @Authorization(value = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = InterfaceDetailResponseVO.class),
            @ApiResponse(code = 400, message = "Bad Request" ),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")
    })*/

    @io.swagger.v3.oas.annotations.Operation(summary = "${modify.summary}", description="${modify.description}",

            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = InterfaceDetailResponseVO.class))
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


    public InterfaceDetailResponseVO modifyInterfaceDetails(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, @RequestBody ModifyInterfaceDetailRequestVO request) throws Exception {

        final String methodName = "modifyInterfaceDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        InterfaceDetailResponseVO response = new InterfaceDetailResponseVO();

        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        String loginID = null;

        try {

            /*
             * Authentication
             *
             * @throws BTSLBaseException
             */

            response = new InterfaceDetailResponseVO();
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, httpServletResponse);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            loginID = OAuthUserData.getData().getLoginid();
            response = interfaceManagementServiceI.modifyInterfaceDetails(headers, con, loginID, locale, request);

        }catch (BTSLBaseException be) {
            log.error(methodName, "Exception:e=" + be);
            log.errorTrace(methodName, be);
            if (!BTSLUtil.isNullString(be.getMessage())) {
                String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
                response.setMessageCode(be.getMessage());
                response.setMessage(msg);
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
            }

        }
        catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus((HttpStatus.SC_BAD_REQUEST));
            String resmsg = RestAPIStringParser.getMessage(
                    new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                    PretupsErrorCodesI.ADD_INTERFACE_DETAIL_FAIL, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.ADD_INTERFACE_DETAIL_FAIL);
            httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
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
        return response;
    }

    @GetMapping(value = "/delete", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    /*@ApiOperation(value = "Delete Interface Details",
            response = BaseResponse.class,
            authorizations = {
                    @Authorization(value = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")
    })*/

    @io.swagger.v3.oas.annotations.Operation(summary = "${delete.summary}", description="${delete.description}",

            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = InterfaceDetailResponseVO.class))
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


    public InterfaceDetailResponseVO deleteInterfaceDetails(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest,@Parameter(description = "id", required = true) @RequestParam("id") String interfaceId) throws Exception {
        final String methodName = "getInterfaceDetailsById";
        if(log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        InterfaceDetailResponseVO response = new InterfaceDetailResponseVO();
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        String loginUserID = null;

        try {
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();

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

            response = interfaceManagementServiceI.deleteInterfaceDetails(headers, httpServletResponse, con, loginUserID, locale, interfaceId);
        }
        catch (BTSLBaseException be) {
            log.error("", "Exceptin:e=" + be);
            log.errorTrace(methodName, be);
            String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
            ((BaseResponse) response).setMessageCode(be.getMessageKey());
            response.setMessage(msg);

            if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
                httpServletResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
                ((BaseResponse) response).setStatus(HttpStatus.SC_UNAUTHORIZED);
            } else {
                httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            String resmsg = RestAPIStringParser.getMessage(
                    new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                    PretupsErrorCodesI.ERROR_RESPONSE, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.ERROR_RESPONSE);
            httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
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
        return response;
    }

    @GetMapping(value= "/loadinterfacetype", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    /*@ApiOperation(value = "populate the Details of Interface types.",
            response = InterfaceDetailResponseVO.class,
            authorizations = {
                    @Authorization(value = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = InterfaceDetailResponseVO.class),
            @ApiResponse(code = 400, message = "Bad Request" ),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")
    })*/
    @io.swagger.v3.oas.annotations.Operation(summary = "${loadinterfacetype.summary}", description="${loadinterfacetype.description}",

            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = InterfaceTypeResponseVO.class))
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


    public InterfaceTypeResponseVO loadInterfaceType(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse responseSwag, @Parameter(description = "category", required = true) @RequestParam("category") String interfaceCategory) throws Exception {

        final String methodName = "loadInterfaceType";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        InterfaceTypeResponseVO response = new InterfaceTypeResponseVO();

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
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, responseSwag);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            loginUserID = OAuthUserData.getData().getLoginid();

            response = interfaceManagementServiceI.loadInterfaceType(headers, responseSwag, con, loginUserID, locale, interfaceCategory);
        } catch (BTSLBaseException be) {
            log.error("", "Exceptin:e=" + be);
            log.errorTrace(methodName, be);
            String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
            ((BaseResponse) response).setMessageCode(be.getMessageKey());
            response.setMessage(msg);

            if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
                responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
                ((BaseResponse) response).setStatus(HttpStatus.SC_UNAUTHORIZED);
            } else {
                responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            String resmsg = RestAPIStringParser.getMessage(
                    new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                    PretupsErrorCodesI.ERROR_RESPONSE, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.ERROR_RESPONSE);
            responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
        } finally {
            if (mcomCon != null) {
                mcomCon.close(methodName);
                mcomCon = null;
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting:=" + methodName);
            }
        }
        return response;
    }
}

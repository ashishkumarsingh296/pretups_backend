package com.restapi.networkadmin.c2sreconciliation;


import com.btsl.common.BTSLBaseException;
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
import com.restapi.networkadmin.c2sreconciliation.requestVO.C2SRreconciliationActionRequestVO;
import com.restapi.networkadmin.c2sreconciliation.requestVO.C2SRreconciliationRequestVO;
import com.restapi.networkadmin.c2sreconciliation.responseVO.C2SReconciliationResponseListVO;
import com.restapi.networkadmin.c2sreconciliation.responseVO.C2SReconciliationTransferDetailsVO;
import com.restapi.networkadmin.c2sreconciliation.responseVO.C2SRreconciliationActionResponseVO;
import com.restapi.networkadmin.c2sreconciliation.service.C2SReconciliationService;
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

@io.swagger.v3.oas.annotations.tags.Tag(name = "${C2SReconciliationController.name}", description = "${C2SReconciliationController.desc}")//@Api(tags = "Network Admin")
@RestController
@RequestMapping(value = "/v1/networkadmin/c2sreconciliation")


public class C2SReconciliationController {

    @Autowired
    private C2SReconciliationService c2SReconciliationService;
    public final Log log = LogFactory.getLog(this.getClass().getName());

    @PostMapping(value = "/loadlist", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    /*@ApiOperation(value = "load C2S Reconciliation List", response = C2SReconciliationResponseListVO.class, authorizations = {
            @Authorization(value = "Authorization")})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = C2SReconciliationResponseListVO.class),
            @ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")})
*/

    @io.swagger.v3.oas.annotations.Operation(summary = "${loadlist.summary}", description="${loadlist.description}",

            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = C2SReconciliationResponseListVO.class))
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


    public C2SReconciliationResponseListVO loadC2SReconciliationList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                                                     HttpServletResponse response1, HttpServletRequest httpServletRequest,
                                                                     @RequestBody C2SRreconciliationRequestVO requestVO) throws Exception {
        final String methodName = "loadC2SReconciliationList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        C2SReconciliationResponseListVO response = new C2SReconciliationResponseListVO();

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
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = c2SReconciliationService.loadC2SReconciliationList(con, userVO, requestVO);
            response.setStatus((HttpStatus.SC_OK));
        } catch (BTSLBaseException be) {
            log.error("", "Exceptin:e=" + be);
            log.errorTrace(methodName, be);
            String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
            response.setMessageCode(be.getMessageKey());
            response.setMessage(msg);

            if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
                response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
                response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            } else {
                response1.setStatus(HttpStatus.SC_BAD_REQUEST);
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
            }
        }
        catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus((HttpStatus.SC_BAD_REQUEST));
            String resmsg = RestAPIStringParser.getMessage(
                    new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                    PretupsErrorCodesI.C2S_REC_WENT_WRONG, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.C2S_REC_WENT_WRONG);
            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
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

    @GetMapping(value = "/loadtransferdetails", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    /*@ApiOperation(value = "Fetch the reconciliation details based on transer ID",
            response = BaseResponse.class,
            authorizations = {
                    @Authorization(value = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")
    })
    */

    @io.swagger.v3.oas.annotations.Operation(summary = "${loadtransferdetails.summary}", description="${loadtransferdetails.description}",

            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = C2SReconciliationTransferDetailsVO.class))
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

    public C2SReconciliationTransferDetailsVO c2sreconciliationTransferDetails(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                                                               HttpServletResponse response1, HttpServletRequest httpServletRequest, @RequestParam("transferID") String transferID) throws Exception {

        final String methodName = "reconciliationTransferDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        C2SReconciliationTransferDetailsVO response = new C2SReconciliationTransferDetailsVO();

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
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = c2SReconciliationService.loadC2SreconciliationTransferDetails(con, transferID);
            response.setStatus((HttpStatus.SC_OK));
        } catch (BTSLBaseException be) {
            log.error("", "Exceptin:e=" + be);
            log.errorTrace(methodName, be);
            String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
            response.setMessageCode(be.getMessageKey());
            response.setMessage(msg);

            if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
                response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
                response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            } else {
                response1.setStatus(HttpStatus.SC_BAD_REQUEST);
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
            }
        }
        catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus((HttpStatus.SC_BAD_REQUEST));
            String resmsg = RestAPIStringParser.getMessage(
                    new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                    PretupsErrorCodesI.C2S_REC_WENT_WRONG, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.C2S_REC_WENT_WRONG);
            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
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

    @PostMapping(value = "/performreconciliationaction", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    /*@ApiOperation(value = "Success/Fail reconciliation transfer", response = C2SRreconciliationActionRequestVO.class, authorizations = {
            @Authorization(value = "Authorization")})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = C2SRreconciliationActionRequestVO.class),
            @ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")})
    */

    @io.swagger.v3.oas.annotations.Operation(summary = "${performreconciliationaction.summary}", description="${performreconciliationaction.description}",

            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = C2SRreconciliationActionResponseVO.class))
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

    public C2SRreconciliationActionResponseVO c2sreconciliationAction(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1,
                                                                      HttpServletRequest httpServletRequest, @RequestBody C2SRreconciliationActionRequestVO requestVO) throws Exception {

        final String methodName = "c2sreconciliationAction";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        C2SRreconciliationActionResponseVO response = new C2SRreconciliationActionResponseVO();

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
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = c2SReconciliationService.performreconciliationaction(con,userVO, requestVO);
            response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.C2S_REC_SUCCESSFULL, null));
            response.setMessageCode(PretupsErrorCodesI.C2S_REC_SUCCESSFULL);
            response.setStatus((HttpStatus.SC_OK));
        } catch (BTSLBaseException be) {
            log.error("", "Exceptin:e=" + be);
            log.errorTrace(methodName, be);
            String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
            response.setMessageCode(be.getMessageKey());
            response.setMessage(msg);

            if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
                response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
                response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            } else {
                response1.setStatus(HttpStatus.SC_BAD_REQUEST);
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
            }
        }
        catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus((HttpStatus.SC_BAD_REQUEST));
            String resmsg = RestAPIStringParser.getMessage(
                    new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                    PretupsErrorCodesI.C2S_REC_WENT_WRONG, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.C2S_REC_WENT_WRONG);
            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
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
}


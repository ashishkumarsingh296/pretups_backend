package com.restapi.networkadmin.networkStock;


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
import com.btsl.util.OAuthenticationUtil;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.apache.http.HttpStatus;
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

@io.swagger.v3.oas.annotations.tags.Tag(name = "${NetworkStockController.name}", description = "${NetworkStockController.desc}")//@Api(tags = "Network Admin", defaultValue = "Network Admin")
@RestController
@RequestMapping(value = "/v1/networkadmin/networkstock")
public class NetworkStockController {

    public static final Log log = LogFactory.getLog(NetworkStockController.class.getName());

    @Autowired
    NetworkStockServiceI networkStockServiceI;

    @GetMapping(value = "/displaylist", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    /*@ApiOperation(value = "View Current Stock", response = BaseResponse.class, authorizations = {@Authorization(value = "Authorization")})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = ViewCurrentStockResponseVO.class), @ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 404, message = "Not Found")})
    */

    @io.swagger.v3.oas.annotations.Operation(summary = "${displaylist.summary}", description = "${displaylist.description}",

            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ViewCurrentStockResponseVO.class))
                            )
                    }

                    ),


                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, description = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
                            )
                    }),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
                            )
                    }),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
                            )
                    }),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
                            )
                    })
            }
    )

    public ViewCurrentStockResponseVO getDisplayList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1, HttpServletRequest httpServletRequest) throws Exception {
        final String methodName = "getDisplayList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        ViewCurrentStockResponseVO response = new ViewCurrentStockResponseVO();
        String loginID = null;

        try {
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            loginID = OAuthUserData.getData().getLoginid();
            response = networkStockServiceI.getList(headers, response1, con, mcomCon, loginID);
        } catch (BTSLBaseException be) {
            log.error("", "Exceptin:e=" + be);
            log.errorTrace(methodName, be);
            String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
            ((BaseResponse) response).setMessageCode(be.getMessageKey());
            response.setMessage(msg);

            if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
                response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
                ((BaseResponse) response).setStatus(HttpStatus.SC_UNAUTHORIZED);
            } else {
                response1.setStatus(HttpStatus.SC_BAD_REQUEST);
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            String resmsg = RestAPIStringParser.getMessage(new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY), PretupsErrorCodesI.ERROR_RESPONSE, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.ERROR_RESPONSE);
            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
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

    @PostMapping(value = "/submitinitiatestock", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    /*@ApiOperation(value = "Confirm Stock Deduction", response = BaseResponse.class, authorizations = {@Authorization(value = "Authorization")})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = BaseResponse.class), @ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 404, message = "Not Found")})
    */

    @io.swagger.v3.oas.annotations.Operation(summary = "${submitinitiatestock.summary}", description = "${submitinitiatestock.description}",

            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BaseResponse.class))
                                    , examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(name = "The String example", value = "urgheiurgheirghieurg"),
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(name = "The Integer example", value = "311414")})
                    }

                    ),


                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, description = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
                                    //  ,examples = {
                                    //  @io.swagger.v3.oas.annotations.media.ExampleObject(name = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, value = "[ { \\\"status\\\": 200, \\\"messageCode\\\": \\\"string\\\", \\\"message\\\": \\\"Success\\\", \\\"errorMap\\\": { \\\"masterErrorList\\\": [ { \\\"errorCode\\\": \\\"string\\\", \\\"errorMsg\\\": \\\"string\\\" } ], \\\"rowErrorMsgLists\\\": [ { \\\"rowValue\\\": \\\"string\\\", \\\"rowName\\\": \\\"string\\\", \\\"masterErrorList\\\": [ { \\\"errorCode\\\": \\\"string\\\", \\\"errorMsg\\\": \\\"string\\\" } ], \\\"rowErrorMsgList\\\": [ {} ] } ] }, \\\"transactionId\\\": \\\"transactionId\\\" } ]" )
                                    // }

                                    , examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)}

                            )
                    }),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))

                                    , examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.BAD_REQUEST)}
                            )
                    }),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))

                                    , examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}
                            )
                    }),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))

                                    , examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)}
                            )
                    })
            }
    )

    public BaseResponse submitInitiateStock(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1, HttpServletRequest httpServletRequest, @Parameter(description = "categoryCode", required = true)// allowableValues = "NWADM")
    @RequestParam("categoryCode") String categoryType, @RequestBody NetworkInitiateStockDeductionRequestVO requestVO) throws Exception {
        final String methodName = "submitInitiateStock";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        BaseResponse response = new BaseResponse();
        String loginID = null;

        try {

            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            loginID = OAuthUserData.getData().getLoginid();
            response = networkStockServiceI.confirmStockAuthorise(con, mcomCon, categoryType, loginID, response1, headers,requestVO);

        } catch (BTSLBaseException be) {
            log.error("", "Exceptin:e=" + be);
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
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            String resmsg = RestAPIStringParser.getMessage(new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY), PretupsErrorCodesI.ERROR_RESPONSE, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.ERROR_RESPONSE);
            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
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

    @PostMapping(value = "/initiatestockdeduction", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    /*@ApiOperation(value = "Initiate Stock Deduction", response = BaseResponse.class, authorizations = {@Authorization(value = "Authorization")})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = BaseResponse.class), @ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 404, message = "Not Found")})
    */

    @io.swagger.v3.oas.annotations.Operation(summary = "${initiatestockdeduction.summary}", description="${initiatestockdeduction.description}",

            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = NetworkStockTxnVO1.class))
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

    public NetworkStockTxnVO1 initiateStockDeduction(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1, HttpServletRequest httpServletRequest, @Parameter(description = "categoryCode", required = true)// allowableValues = "NWADM")
    @RequestParam("categoryCode") String categoryType, @RequestBody NetworkInitiateStockDeductionRequestVO requestVO) throws Exception {
        final String methodName = "initiateStockDeduction";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        NetworkStockTxnVO1 response = new NetworkStockTxnVO1();
        String loginID = null;

        try {

            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            loginID = OAuthUserData.getData().getLoginid();
            response = networkStockServiceI.initiateStockDeduction(con, mcomCon, categoryType, loginID, response1, headers,requestVO);

        } catch (BTSLBaseException be) {
            log.error("", "Exceptin:e=" + be);
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
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            String resmsg = RestAPIStringParser.getMessage(new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY), PretupsErrorCodesI.ERROR_RESPONSE, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.ERROR_RESPONSE);
            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
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

    @GetMapping(value = "/initiatestockdeduction", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    /*@ApiOperation(value = "Initiate Network Stock Deduction List", response = NetworkStockInitiateDeductionResponseVO.class, authorizations = {@Authorization(value = "Authorization")})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = NetworkStockInitiateDeductionResponseVO.class), @ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 404, message = "Not Found")})
*/
    @io.swagger.v3.oas.annotations.Operation(summary = "${initiatestockdeduction.summary}", description="${initiatestockdeduction.description}",

            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = NetworkStockInitiateDeductionResponseVO.class))
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


    public NetworkStockInitiateDeductionResponseVO getTemplate(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, @Parameter(description = "categoryCode", required = true)// allowableValues = "NWADM")
    @RequestParam("categoryCode") String categoryType, @Parameter(description = "walletType", required = true)// allowableValues = "SAL,FOC,INC")
    @RequestParam("walletType") String walletType, HttpServletResponse response1) throws Exception {
        final String methodName = "getTemplate";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }

        NetworkStockInitiateDeductionResponseVO response = new NetworkStockInitiateDeductionResponseVO();
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        String loginID = null;

        try {

            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            loginID = OAuthUserData.getData().getLoginid();
            response = networkStockServiceI.getStockAuthorise(con, mcomCon, categoryType, loginID, response1, headers, walletType);

        } catch (BTSLBaseException be) {
            log.error("", "Exceptin:e=" + be);
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
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            String resmsg = RestAPIStringParser.getMessage(new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY), PretupsErrorCodesI.ERROR_RESPONSE, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.ERROR_RESPONSE);
            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
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

    @GetMapping(value = "/stockdeductiontransactionlist", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    /*@ApiOperation(value = "List of stock deduction transactions waiting for approval", response = ApprovalStockResponseVO.class, authorizations = {@Authorization(value = "Authorization")})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = ApprovalStockResponseVO.class), @ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 404, message = "Not Found")})
*/
    @io.swagger.v3.oas.annotations.Operation(summary = "${stockdeductiontransactionlist.summary}", description="${stockdeductiontransactionlist.description}",

            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApprovalStockResponseVO.class))
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

    public ApprovalStockResponseVO getStockDeductionTransationWaitingForApproval(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) throws Exception {

        final String methodName = "getStockDeductionTransationWaitingForApproval";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        String loginID = null;
        ApprovalStockResponseVO approvalStockResponseVO = new ApprovalStockResponseVO();
        try {

            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, httpServletResponse);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            loginID = OAuthUserData.getData().getLoginid();
            approvalStockResponseVO = networkStockServiceI.getStockDeductionTransactionList(con, loginID, httpServletResponse, headers);
        } catch (BTSLBaseException be) {
            log.error("", "Exceptin:e=" + be);
            log.errorTrace(methodName, be);
            String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
            approvalStockResponseVO.setMessageCode(be.getMessageKey());
            approvalStockResponseVO.setMessage(msg);

            if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
                httpServletResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
                approvalStockResponseVO.setStatus(HttpStatus.SC_UNAUTHORIZED);
            } else {
                httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
                approvalStockResponseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            String msg = RestAPIStringParser.getMessage(locale, e.getMessage(), null);
            approvalStockResponseVO.setMessageCode(msg);
            approvalStockResponseVO.setMessage(msg);
            approvalStockResponseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
            httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
        } finally {

            if (mcomCon != null) {
                mcomCon.close(methodName);
                mcomCon = null;
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting:=" + methodName);
            }

        }

        return approvalStockResponseVO;

    }

    @GetMapping(value = "/retrievestockdeductiontransactiondetails", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    /*@ApiOperation(value = "stock deduction transaction details waiting for approval", response = ApprovalStockDetailsResponseVO.class, authorizations = {@Authorization(value = "Authorization")})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = ApprovalStockDetailsResponseVO.class), @ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 404, message = "Not Found")})
*/
    @io.swagger.v3.oas.annotations.Operation(summary = "${retrievestockdeductiontransactiondetails.summary}", description="${retrievestockdeductiontransactiondetails.description}",

            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApprovalStockDetailsResponseVO.class))
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

    public ApprovalStockDetailsResponseVO getStockDeductionTransationDetail(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, @Parameter(description = "txnNo", required = true) @RequestParam("txnNo") String txnNo) throws Exception {

        final String methodName = "getStockDeductionTransationDetail";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        String loginID = null;
        ApprovalStockDetailsResponseVO response = null;
        try {

            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, httpServletResponse);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            loginID = OAuthUserData.getData().getLoginid();
            response = networkStockServiceI.getStockDeductionTransactionDetails(con, loginID, httpServletResponse, headers, txnNo);

        } catch (BTSLBaseException be) {
            log.error("", "Exceptin:e=" + be);
            log.errorTrace(methodName, be);
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
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            String msg = RestAPIStringParser.getMessage(locale, e.getMessage(), null);
            response.setMessageCode(msg);
            response.setMessage(msg);
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
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

    @PostMapping(value = "/approve", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    /*@ApiOperation(value = "Stock Dedcution Approval", response = BaseResponse.class, authorizations = {@Authorization(value = "Authorization")})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = BaseResponse.class), @ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 404, message = "Not Found")})
*/
    @io.swagger.v3.oas.annotations.Operation(summary = "${approve.summary}", description="${approve.description}",

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


    public BaseResponse confirmStockDeductionApproval(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response, HttpServletRequest httpServletRequest, @Parameter(description = "Stock Dedcution Approval",  required = true) @RequestBody ApproveStockDeductionRequestVO requestVO) throws Exception {

        final String methodName = "confirmStockDeductionApproval";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }

        BaseResponse responseVO = new BaseResponse();
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        String loginID = null;

        try {

            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            loginID = OAuthUserData.getData().getLoginid();
            responseVO = networkStockServiceI.approve(requestVO, response, headers,loginID);

        } catch (BTSLBaseException be) {
            log.error("", "Exceptin:e=" + be);
            log.errorTrace(methodName, be);
            String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
            responseVO.setMessageCode(be.getMessageKey());
            responseVO.setMessage(msg);

            if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
                response.setStatus(HttpStatus.SC_UNAUTHORIZED);
                responseVO.setStatus(HttpStatus.SC_UNAUTHORIZED);
            } else {
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            String msg = RestAPIStringParser.getMessage(locale, e.getMessage(), null);
            responseVO.setMessageCode(msg);
            responseVO.setMessage(msg);
            responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
        } finally {

            if (mcomCon != null) {
                mcomCon.close(methodName);
                mcomCon = null;
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting:=" + methodName);
            }

        }
        return responseVO;
    }

    @PostMapping(value = "/reject", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    /*@ApiOperation(value = "Stock Dedcution Rejection", response = BaseResponse.class, authorizations = {@Authorization(value = "Authorization")})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = BaseResponse.class), @ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 404, message = "Not Found")})
*/

    @io.swagger.v3.oas.annotations.Operation(summary = "${reject.summary}", description="${reject.description}",

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


    public BaseResponse rejectStockDeductionApproval(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response, HttpServletRequest httpServletRequest, @Parameter(description = "Stock Dedcution Rejection", required = true) @RequestBody RejectStockDeductionRequestVO requestVO) throws Exception {

        final String methodName = "rejectStockDeductionApproval";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }

        BaseResponse responseVO = new BaseResponse();
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        String loginID = null;

        try {

            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            loginID = OAuthUserData.getData().getLoginid();
            responseVO = networkStockServiceI.reject(requestVO, response, headers,loginID);

        } catch (BTSLBaseException be) {
            log.error("", "Exceptin:e=" + be);
            log.errorTrace(methodName, be);
            String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
            responseVO.setMessageCode(be.getMessageKey());
            responseVO.setMessage(msg);

            if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
                response.setStatus(HttpStatus.SC_UNAUTHORIZED);
                responseVO.setStatus(HttpStatus.SC_UNAUTHORIZED);
            } else {
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            String msg = RestAPIStringParser.getMessage(locale, e.getMessage(), null);
            responseVO.setMessageCode(msg);
            responseVO.setMessage(msg);
            responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
        } finally {

            if (mcomCon != null) {
                mcomCon.close(methodName);
                mcomCon = null;
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting:=" + methodName);
            }

        }
        return responseVO;
    }
}



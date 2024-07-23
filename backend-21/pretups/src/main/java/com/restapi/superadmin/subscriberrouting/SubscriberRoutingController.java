package com.restapi.superadmin.subscriberrouting;

import com.btsl.common.BaseResponse;
import com.btsl.common.BaseResponseRedoclyCommon;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.superadmin.subscriberrouting.requestVO.AddRequestVO;
import com.restapi.superadmin.subscriberrouting.requestVO.BulkAddRequestVO;
import com.restapi.superadmin.subscriberrouting.requestVO.BulkDeleteRequestVO;
import com.restapi.superadmin.subscriberrouting.requestVO.DeleteRequestVO;
import com.restapi.superadmin.subscriberrouting.responseVO.BulkResponseVO;
import com.restapi.superadmin.subscriberrouting.responseVO.InterfaceResponseVO;
import com.restapi.superadmin.subscriberrouting.service.SubscriberRoutingService;
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
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;

import java.sql.Connection;
import java.util.Locale;

import static com.btsl.common.BaseResponseRedoclyCommon.UNAUTH;
import static com.btsl.util.Constants.*;
import static com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC;

@Tag(name = "${SubscriberRoutingController.name}", description = "${SubscriberRoutingController.desc}")
@RestController
@RequestMapping(value = "/v1/superadmin/subscriberrouting")
@ControllerAdvice
public class SubscriberRoutingController {
    public final Log LOG = LogFactory.getLog(this.getClass().getName());
    final String CLASS_NAME = "SubscriberRoutingController";

    private final SubscriberRoutingService subscriberRoutingService;

    public SubscriberRoutingController(SubscriberRoutingService subscriberRoutingService) {
        this.subscriberRoutingService = subscriberRoutingService;
    }

    @GetMapping(value = "/interface", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${SubscriberRoutingController.loadInterface.name}", description = "${SubscriberRoutingController.loadInterface.desc}",
            responses = {
                    @ApiResponse(responseCode = API_SUCCESS_RESPONSE_CODE, description = API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = InterfaceResponseVO.class)))}),
                    @ApiResponse(responseCode = API_BAD_REQ_RESPONSE_CODE, description = API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)})}),
                    @ApiResponse(responseCode = API_UNAUTH_RESPONSE_CODE, description = API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = UNAUTH)})}),
                    @ApiResponse(responseCode = API_NOT_FOUND_RESPONSE_CODE, description = API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)})}),
                    @ApiResponse(responseCode = API_INTERNAL_ERROR_RESPONSE_CODE, description = API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})})
            })
    public InterfaceResponseVO loadInterface(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, @RequestParam("interfaceType") String interfaceType) throws Exception {
        final String METHOD_NAME = "loadInterface";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        InterfaceResponseVO response = new InterfaceResponseVO();

        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, httpServletResponse);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = subscriberRoutingService.loadInterface(con, mcomCon, locale, userVO, interfaceType, httpServletResponse);
        } finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }
        return response;
    }

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${SubscriberRoutingController.add.name}", description = "${SubscriberRoutingController.add.desc}",
            responses = {
                    @ApiResponse(responseCode = API_SUCCESS_RESPONSE_CODE, description = API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)))}),
                    @ApiResponse(responseCode = API_BAD_REQ_RESPONSE_CODE, description = API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)})}),
                    @ApiResponse(responseCode = API_UNAUTH_RESPONSE_CODE, description = API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = UNAUTH)})}),
                    @ApiResponse(responseCode = API_NOT_FOUND_RESPONSE_CODE, description = API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)})}),
                    @ApiResponse(responseCode = API_INTERNAL_ERROR_RESPONSE_CODE, description = API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})})
            })
    public BaseResponse addRoutingMSISDN(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, @RequestBody AddRequestVO request) throws Exception {
        final String METHOD_NAME = "addRoutingMSISDN";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        BaseResponse response = new BaseResponse();
        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, httpServletResponse);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = subscriberRoutingService.addSubscriberRouting(con, mcomCon, locale, userVO, request, httpServletResponse);
        } finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }
        return response;
    }

    @PostMapping(value = "/bulkadd", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${SubscriberRoutingController.bulkAddSubscriberRouting.name}", description = "${SubscriberRoutingController.bulkAddSubscriberRouting.desc}",
            responses = {
                    @ApiResponse(responseCode = API_SUCCESS_RESPONSE_CODE, description = API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BulkResponseVO.class)))}),
                    @ApiResponse(responseCode = API_BAD_REQ_RESPONSE_CODE, description = API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)})}),
                    @ApiResponse(responseCode = API_UNAUTH_RESPONSE_CODE, description = API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = UNAUTH)})}),
                    @ApiResponse(responseCode = API_NOT_FOUND_RESPONSE_CODE, description = API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)})}),
                    @ApiResponse(responseCode = API_INTERNAL_ERROR_RESPONSE_CODE, description = API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})})
            })
    public BulkResponseVO bulkAddSubscriberRouting(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, @RequestBody BulkAddRequestVO request) throws Exception {
        final String METHOD_NAME = "bulkAddSubscriberRouting";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        BulkResponseVO response = new BulkResponseVO();
        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, httpServletResponse);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = subscriberRoutingService.uploadAndProcessBulkAddSubscriberRouting(con, mcomCon, locale, userVO, request, httpServletResponse);
        } finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }
        return response;
    }

    @PostMapping(value = "/delete", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${SubscriberRoutingController.deleteSubscriberRouting.name}", description = "${SubscriberRoutingController.deleteSubscriberRouting.desc}",
            responses = {
                    @ApiResponse(responseCode = API_SUCCESS_RESPONSE_CODE, description = API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BulkResponseVO.class)))}),
                    @ApiResponse(responseCode = API_BAD_REQ_RESPONSE_CODE, description = API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)})}),
                    @ApiResponse(responseCode = API_UNAUTH_RESPONSE_CODE, description = API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = UNAUTH)})}),
                    @ApiResponse(responseCode = API_NOT_FOUND_RESPONSE_CODE, description = API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)})}),
                    @ApiResponse(responseCode = API_INTERNAL_ERROR_RESPONSE_CODE, description = API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})})
            })
    public BaseResponse deleteSubscriberRouting(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, @RequestBody DeleteRequestVO request) throws Exception {
        final String METHOD_NAME = "deleteSubscriberRouting";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        BaseResponse response = new BaseResponse();
        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, httpServletResponse);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = subscriberRoutingService.deleteSubscriberRouting(con, mcomCon, locale, userVO, request, httpServletResponse);
        } finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }
        return response;
    }

    @PostMapping(value = "/bulkdelete", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${SubscriberRoutingController.bulkDeleteSubscriberRouting.name}", description = "${SubscriberRoutingController.bulkDeleteSubscriberRouting.desc}",
            responses = {
                    @ApiResponse(responseCode = API_SUCCESS_RESPONSE_CODE, description = API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BulkResponseVO.class)))}),
                    @ApiResponse(responseCode = API_BAD_REQ_RESPONSE_CODE, description = API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)})}),
                    @ApiResponse(responseCode = API_UNAUTH_RESPONSE_CODE, description = API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = UNAUTH)})}),
                    @ApiResponse(responseCode = API_NOT_FOUND_RESPONSE_CODE, description = API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)})}),
                    @ApiResponse(responseCode = API_INTERNAL_ERROR_RESPONSE_CODE, description = API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})})
            })
    public BulkResponseVO bulkDeleteSubscriberRouting(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, @RequestBody BulkDeleteRequestVO request) throws Exception {
        final String METHOD_NAME = "bulkDeleteSubscriberRouting";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        BulkResponseVO response = new BulkResponseVO();
        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, httpServletResponse);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = subscriberRoutingService.uploadAndProcessBulkDeleteSubscriberRouting(con, mcomCon, locale, userVO, request, httpServletResponse);
        } finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }
        return response;
    }
}

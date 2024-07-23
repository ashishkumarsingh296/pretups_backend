package com.restapi.superadmin.STKServices;

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
import com.btsl.util.OAuthenticationUtil;
import com.restapi.superadmin.STKServices.requestVO.AddServiceRequestVO;
import com.restapi.superadmin.STKServices.requestVO.AssignServiceRequestVO;
import com.restapi.superadmin.STKServices.requestVO.ModifyServiceRequestVO;
import com.restapi.superadmin.STKServices.requestVO.PushWmlRequestVO;
import com.restapi.superadmin.STKServices.responseVO.*;
import com.restapi.superadmin.STKServices.service.STKServicesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.Locale;
import java.util.Objects;

import static com.btsl.common.BaseResponseRedoclyCommon.UNAUTH;
import static com.btsl.util.Constants.*;

@Tag(name = "${STKServicesController.name}", description = "${STKServicesController.desc}")
@RestController
@RequestMapping(value = "/v1/superadmin/stkservices")
@ControllerAdvice
public class STKServicesController {

    private final STKServicesService stkServicesService;
    public final Log LOG = LogFactory.getLog(this.getClass().getName());

    public STKServicesController(STKServicesService stkServicesService) {
        this.stkServicesService = stkServicesService;
    }

    @GetMapping(value = "/usertypeservicelist", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${STKServicesController.userTypeServiceList.name}", description = "${STKServicesController.userTypeServiceList.desc}",
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

    public UserTypeServiceListResponseVO userTypeServiceList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                    HttpServletResponse response1)
            throws Exception {

        final String METHOD_NAME = "userTypeServiceList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        UserTypeServiceListResponseVO response;

        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            response = stkServicesService.userTypeServiceList(con);
            response.setStatus((HttpStatus.SC_OK));
            response.setMessageCode(PretupsErrorCodesI.USER_TYPE_SERVICE_LIST_SUCCESS);
            response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USER_TYPE_SERVICE_LIST_SUCCESS, null));
        } finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
            }
        }
        return response;
    }

    @GetMapping(value = "/generatebytecode", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${STKServicesController.generateByteCode.name}", description = "${STKServicesController.generateByteCode.desc}",
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

    public GenerateByteCodeResponseVO generateByteCode(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                                       HttpServletResponse response1, @RequestParam("wmlCode") String wmlCode, @RequestParam(value = "description", required = false) String description, @RequestParam("serviceSetID") String serviceSetID)
            throws Exception {

        final String METHOD_NAME = "generateByteCode";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        GenerateByteCodeResponseVO response;

        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            response = stkServicesService.generateByteCode(con, wmlCode, description, serviceSetID);
            response.setStatus((HttpStatus.SC_OK));
            response.setMessageCode(PretupsErrorCodesI.BYTE_CODE_GEN_SUCCESS);
            response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.BYTE_CODE_GEN_SUCCESS, null));
        } finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
            }
        }

        return response;
    }

    @PostMapping(value = "/pushwml", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${STKServicesController.pushWml.name}", description = "${STKServicesController.pushWml.desc}",
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

    public BaseResponse pushWml(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                                       HttpServletResponse response1, @RequestBody PushWmlRequestVO request)
            throws Exception{

        final String METHOD_NAME = "pushWml";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
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
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            stkServicesService.pushWml(con, request, userVO);
            response.setStatus((HttpStatus.SC_OK));
            response.setMessageCode(PretupsErrorCodesI.PUSH_WML_SUCCESS);
            response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PUSH_WML_SUCCESS, null));
        } finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
            }
        }

        return response;
    }

    @PostMapping(value = "/addservice", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${STKServicesController.addService.name}", description = "${STKServicesController.addService.desc}",
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

    public BaseResponse addService(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                HttpServletResponse response1, @RequestBody AddServiceRequestVO request)
            throws Exception{

        final String METHOD_NAME = "addService";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
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
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            stkServicesService.addService(con, mcomCon, request, userVO);
            String[] args = Objects.equals(request.getStatus(), PretupsI.STK_SAVE_DRAFT) ?
                    new String[]{PretupsI.STK_SERVICE_DRAFT} :
                    new String[]{PretupsI.STK_SERVICE_ADDED};
            response.setStatus((HttpStatus.SC_OK));
            response.setMessageCode(PretupsErrorCodesI.ADD_SERVICE_SUCCESS);
            response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ADD_SERVICE_SUCCESS, args));
        } finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
            }
        }

        return response;
    }

    @GetMapping(value = "/categorysimprofilelist", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${STKServicesController.simProfileCategoryList.name}", description = "${STKServicesController.simProfileCategoryList.desc}",
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

    public SimProfileCategoryListResponseVO simProfileCategoryList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                                             HttpServletResponse response1)
            throws Exception {

        final String METHOD_NAME = "simProfileCategoryList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        SimProfileCategoryListResponseVO response;

        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            response = stkServicesService.simProfileCategoryList(con);
            response.setStatus((HttpStatus.SC_OK));
            response.setMessageCode(PretupsErrorCodesI.SIM_PROFILE_CAT_LIST_SUCCESS);
            response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SIM_PROFILE_CAT_LIST_SUCCESS, null));
        } finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
            }
        }
        return response;
    }

    @GetMapping(value = "/usersimserviceslist", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${STKServicesController.userSimServicesList.name}", description = "${STKServicesController.userSimServicesList.desc}",
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

    public UserSimServicesListResponseVO userSimServicesList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                                                   HttpServletResponse response1, @RequestParam("categoryCode") String categoryCode, @RequestParam("profileCode") String profileCode, @RequestParam("simProfileCode") String simProfileCode)
            throws Exception {

        final String METHOD_NAME = "userSimServicesList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        UserSimServicesListResponseVO response;

        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = stkServicesService.userSimServicesList(con, categoryCode, profileCode, simProfileCode, userVO.getNetworkID());
            response.setStatus((HttpStatus.SC_OK));
            response.setMessageCode(PretupsErrorCodesI.USER_SIM_SERVICES_LIST_SUCCESS);
            response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USER_SIM_SERVICES_LIST_SUCCESS, null));
        } finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
            }
        }
        return response;
    }

    @GetMapping(value = "/simserviceslist", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${STKServicesController.simServicesList.name}", description = "${STKServicesController.simServicesList.desc}",
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

    public SimServicesListResponseVO simServicesList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                                     HttpServletResponse response1, @RequestParam("categoryCode") String categoryCode, @RequestParam("serviceSetID") String serviceSetID, @RequestParam("searchString") String searchString)
            throws Exception {

        final String METHOD_NAME = "simServicesList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        SimServicesListResponseVO response;

        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = stkServicesService.simServicesList(con, categoryCode, serviceSetID, searchString, userVO.getNetworkID());
            response.setStatus((HttpStatus.SC_OK));
            String key = response.getSimServicesList().isEmpty() ? PretupsErrorCodesI.NO_RECORDS_FOUND : PretupsErrorCodesI.SIM_SERVICES_LIST_SUCCESS;
            response.setMessageCode(key);
            response.setMessage(RestAPIStringParser.getMessage(locale, key, null));
        } finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
            }
        }
        return response;
    }

    @GetMapping(value = "/calculateoffset", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${STKServicesController.calculateOffset.name}", description = "${STKServicesController.calculateOffset.desc}",
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

    public CalculateOffsetResponseVO calculateOffset(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                                     HttpServletResponse response1, @RequestParam ("categoryCode") String categoryCode, @RequestParam ("profileCode") String profileCode, @RequestParam ("simProfileCode") String simProfileCode, @RequestParam ("serviceID") String serviceID, @RequestParam ("byteCodeLength") String byteCodeLength, @RequestParam ("position") int position)
            throws Exception {

        final String METHOD_NAME = "calculateOffset";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        CalculateOffsetResponseVO response;

        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = stkServicesService.calculateOffset(con, categoryCode, profileCode, simProfileCode, serviceID, byteCodeLength, position, userVO);
            response.setStatus((HttpStatus.SC_OK));
            response.setMessageCode(PretupsErrorCodesI.CALCULATE_OFFSET_SUCCESS);
            response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CALCULATE_OFFSET_SUCCESS, null));
        } finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
            }
        }
        return response;
    }

    @PostMapping(value = "/assignservice", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${STKServicesController.assignService.name}", description = "${STKServicesController.assignService.desc}",
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

    public BaseResponse assignService(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                                     HttpServletResponse response1, @RequestBody AssignServiceRequestVO request)
            throws Exception {

        final String METHOD_NAME = "assignService";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
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
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            stkServicesService.assignService(con, mcomCon, request, userVO);
            response.setStatus((HttpStatus.SC_OK));
            response.setMessageCode(PretupsErrorCodesI.ASSIGN_SERVICE_SUCCESS);
            response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.ASSIGN_SERVICE_SUCCESS, null));
        } finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
            }
        }
        return response;
    }

    @GetMapping(value = "/loadservicedetails", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${STKServicesController.loadServiceDetails.name}", description = "${STKServicesController.loadServiceDetails.desc}",
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

    public ServiceDetailsResponseVO loadServiceDetails(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                                       HttpServletResponse response1, @RequestParam("serviceId") String serviceId, @RequestParam ("majorVersion") String majorVersion)
            throws Exception {
        final String METHOD_NAME = "loadServiceDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        ServiceDetailsResponseVO response;
        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = stkServicesService.loadSIMServiceDetails(con,serviceId,majorVersion);
            response.setStatus((HttpStatus.SC_OK));
            response.setMessageCode(PretupsErrorCodesI.LOAD_SERVICE_DETIALS_SUCCESS);
            response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LOAD_SERVICE_DETIALS_SUCCESS, null));
        } finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
            }
        }
        return response;

    }
    @PostMapping(value = "/updateservice", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${STKServicesController.updateService.name}", description = "${STKServicesController.updateService.desc}",
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

    public BaseResponse updateService(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                      HttpServletResponse response1, @RequestBody ModifyServiceRequestVO request)
            throws Exception{

        final String METHOD_NAME = "updateService";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
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
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            stkServicesService.updateService(con, mcomCon, request, userVO);
            response.setStatus((HttpStatus.SC_OK));
            response.setMessageCode(PretupsErrorCodesI.MODIFY_SERVICE_SUCCESS);
            response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MODIFY_SERVICE_SUCCESS, null));
        } finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, PretupsI.EXITED + METHOD_NAME);
            }
        }

        return response;
    }


}
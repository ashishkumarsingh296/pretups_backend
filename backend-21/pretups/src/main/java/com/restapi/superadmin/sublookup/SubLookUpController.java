package com.restapi.superadmin.sublookup;

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
import com.restapi.superadmin.sublookup.requestVO.DeleteSublookUpRequestVO;
import com.restapi.superadmin.sublookup.requestVO.ModifySubLookUpRequestVO;
import com.restapi.superadmin.sublookup.requestVO.SubLookUpRequestVO;
import com.restapi.superadmin.sublookup.responseVO.LookUpListResponseVO;
import com.restapi.superadmin.sublookup.responseVO.SubLookUpListResponseVO;
import com.restapi.superadmin.sublookup.responseVO.SubLookUpResponseVO;
import com.restapi.superadmin.sublookup.service.SubLookUpService;
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
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.util.Locale;

import static com.btsl.common.BaseResponseRedoclyCommon.UNAUTH;
import static com.btsl.util.Constants.*;

@Tag(name = "${SubLookUpController.name}", description = "${SubLookUpController.desc}")
@RestController
@RequestMapping(value = "/v1/superadmin/sublookup")
@ControllerAdvice

public class SubLookUpController {
    public final Log LOG = LogFactory.getLog(this.getClass().getName());
    final String CLASS_NAME = "SubLookUpController";

    private final SubLookUpService subLookUpService;

    public SubLookUpController(SubLookUpService subLookUpService) {
        this.subLookUpService = subLookUpService;
    }

    @GetMapping(value = "/lookuplist", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${SubLookUpController.loadLookUpList.name}", description = "${SubLookUpController.loadLookUpList.desc}",
            responses = {
                    @ApiResponse(responseCode = API_SUCCESS_RESPONSE_CODE, description = API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = LookUpListResponseVO.class)))}),
                    @ApiResponse(responseCode = API_BAD_REQ_RESPONSE_CODE, description = API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)})}),
                    @ApiResponse(responseCode = API_UNAUTH_RESPONSE_CODE, description = API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = UNAUTH)})}),
                    @ApiResponse(responseCode = API_NOT_FOUND_RESPONSE_CODE, description = API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)})}),
                    @ApiResponse(responseCode = API_INTERNAL_ERROR_RESPONSE_CODE, description = API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})})
            })

    public LookUpListResponseVO loadLookUpList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) throws Exception {
        final String METHOD_NAME = "loadLookUpList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        LookUpListResponseVO response = new LookUpListResponseVO();

        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, httpServletResponse);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = subLookUpService.loadLookUpList(con, locale);
            response.setStatus((HttpStatus.SC_OK));
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

    @GetMapping(value = "/sublookuplist", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${SubLookUpController.loadSubLookUpList.name}", description = "${SubLookUpController.loadSubLookUpList.desc}",
            responses = {
                    @ApiResponse(responseCode = API_SUCCESS_RESPONSE_CODE, description = API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SubLookUpListResponseVO.class)))}),
                    @ApiResponse(responseCode = API_BAD_REQ_RESPONSE_CODE, description = API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)})}),
                    @ApiResponse(responseCode = API_UNAUTH_RESPONSE_CODE, description = API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = UNAUTH)})}),
                    @ApiResponse(responseCode = API_NOT_FOUND_RESPONSE_CODE, description = API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)})}),
                    @ApiResponse(responseCode = API_INTERNAL_ERROR_RESPONSE_CODE, description = API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})})
            })
    public SubLookUpListResponseVO loadSubLookUpList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, @RequestParam("lookUpCode") String lookUpCode) throws Exception {
        final String METHOD_NAME = "loadSubLookUpList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        SubLookUpListResponseVO response = new SubLookUpListResponseVO();

        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, httpServletResponse);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = subLookUpService.loadSubLookUpList(con, locale, lookUpCode);
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
    @Operation(summary = "${SubLookUpController.addSublookUp.name}", description = "${SubLookUpController.addSublookUp.desc}",
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
    public BaseResponse addSubLookUp(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, @RequestBody SubLookUpRequestVO request) throws Exception {
        final String METHOD_NAME = "addSubLookUp";
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
            response = subLookUpService.addSubLookUp(con, mcomCon, locale, userVO, request);
            response.setStatus((HttpStatus.SC_OK));
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

    @GetMapping(value = "/sublookup", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${SubLookUpController.subLookUpDetails.name}", description = "${SubLookUpController.subLookUpDetails.desc}",
            responses = {
                    @ApiResponse(responseCode = API_SUCCESS_RESPONSE_CODE, description = API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SubLookUpResponseVO.class)))}),
                    @ApiResponse(responseCode = API_BAD_REQ_RESPONSE_CODE, description = API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)})}),
                    @ApiResponse(responseCode = API_UNAUTH_RESPONSE_CODE, description = API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = UNAUTH)})}),
                    @ApiResponse(responseCode = API_NOT_FOUND_RESPONSE_CODE, description = API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)})}),
                    @ApiResponse(responseCode = API_INTERNAL_ERROR_RESPONSE_CODE, description = API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {@ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})})
            })
    public SubLookUpResponseVO subLookUpDetails(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, @RequestParam("subLookUpCode") String subLookUpCode) throws Exception {
        final String METHOD_NAME = "subLookUpDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        SubLookUpResponseVO response = new SubLookUpResponseVO();

        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, httpServletResponse);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = subLookUpService.loadSubLookUpDetails(con, locale, subLookUpCode);
            response.setStatus((HttpStatus.SC_OK));
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

    @PostMapping(value = "/modify", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${SubLookUpController.modifySubLookUp.name}", description = "${SubLookUpController.modifySubLookUp.desc}",
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
    public BaseResponse modifySubLookUp(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, @RequestBody ModifySubLookUpRequestVO request) throws Exception {
        final String METHOD_NAME = "modifySubLookUp";
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
            response = subLookUpService.modifySubLookUp(con, mcomCon, locale, userVO, request);
            response.setStatus((HttpStatus.SC_OK));
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
    @Operation(summary = "${SubLookUpController.deleteSubLookUp.name}", description = "${SubLookUpController.deleteSubLookUp.desc}",
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
    public BaseResponse deleteSubLookUp(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, @RequestBody DeleteSublookUpRequestVO request) throws Exception {
        final String METHOD_NAME = "deleteSubLookUp";
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
            response = subLookUpService.deleteSubLookUp(con, mcomCon, locale, userVO, request.getSubLookUpCode());
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

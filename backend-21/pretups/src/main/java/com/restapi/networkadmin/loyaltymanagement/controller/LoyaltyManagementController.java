package com.restapi.networkadmin.loyaltymanagement.controller;

import com.btsl.common.BaseResponse;
import com.btsl.common.BaseResponseRedoclyCommon;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.networkadmin.loyaltymanagement.requestVO.*;
import com.restapi.networkadmin.loyaltymanagement.responseVO.*;
import com.restapi.networkadmin.loyaltymanagement.serviceI.LoyaltyManagementServiceI;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import java.sql.Connection;

@Tag(name = "${loyaltyManagementController.name}", description = "${loyaltyManagementController.desc}")
@RestController
@RequestMapping(value = "/v1/networkadmin/loyaltymanagement/")
public class LoyaltyManagementController {


    @Autowired
    private LoyaltyManagementServiceI service;
    public static final Log LOG = LogFactory.getLog(LoyaltyManagementController.class.getName());
    public static final String CLASS_NAME = "LoyaltyManagementController";

    @GetMapping(value = "/loadprfdetailsvrslist", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${loyaltyManagementController.loadProfileVersionsList.name}", description = "${loyaltyManagementController.loadprofileVersionsList.desc}",

            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProfileDetailsVersionsResponseVO.class)))}

                    ),

                    @ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class))

                                    , examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})})})
    public ProfileDetailsVersionsResponseVO loadProfileVersionsDetailsList(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse response1, HttpServletRequest httpServletRequest,
            @Parameter(name = "promotionType", required = true) @RequestParam("promotionType") String promotionType,
            @Parameter(name = "status", required = true) @RequestParam("status") String status,
            @Parameter(name = "applicableFrom", required = false) @RequestParam(name = "applicableFrom", required = false) String applicableFrom,
            @Parameter(name = "applicableTo", required = false) @RequestParam(name = "applicableTo", required = false) String applicableTo) throws Exception {

        final String METHOD_NAME = "loadProfileVersionsDetailsList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        ProfileDetailsVersionsResponseVO response = new ProfileDetailsVersionsResponseVO();
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
            service.loadProfileDetailsVersionsList(con, userVO, promotionType, status, applicableFrom, applicableTo, response);

        } finally {

            if (mcomCon != null) {
                mcomCon.close(CLASS_NAME + "#" + METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting forward=");
            }
        }
        return response;
    }

    @GetMapping(value = "/loadversionsList", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${loyaltyManagementController.loadversionsList.name}", description = "${loyaltyManagementController.loadversionsList.desc}",

            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = VersionsResponseVO.class)))}

                    ),

                    @ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class))

                                    , examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})})})

    public VersionsResponseVO loadVersionsList(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse response1, HttpServletRequest httpServletRequest,
            @Parameter(name = "setID", required = true) @RequestParam("setID") String setID,
            @Parameter(name = "validUpToDate", required = true) @RequestParam("validUpToDate") String validUpToDate
    ) throws Exception {

        final String METHOD_NAME = "loadVersionsList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        VersionsResponseVO response = new VersionsResponseVO();
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
            service.loadVersionsList(con, userVO, setID, validUpToDate, response);

        } finally {

            if (mcomCon != null) {
                mcomCon.close(CLASS_NAME + "#" + METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting forward=");
            }
        }
        return response;
    }


    @GetMapping(value = "/view", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${loyaltyManagementController.view.name}", description = "${loyaltyManagementController.view.desc}",

            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProfileDetailsVO.class)))}

                    ),

                    @ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class))

                                    , examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})})})

    public ProfileDetailsVO loadProfileDetails(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse response1, HttpServletRequest httpServletRequest,
            @Parameter(name = "setID", required = true) @RequestParam("setID") String setID,
            @Parameter(name = "version", required = true) @RequestParam("version") String version) throws Exception {

        final String METHOD_NAME = "loadProfileDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        ProfileDetailsVO response = new ProfileDetailsVO();
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
            service.loadProfileDetails(con, userVO, setID, version, response);

        } finally {

            if (mcomCon != null) {
                mcomCon.close(CLASS_NAME + "#" + METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting forward=");
            }
        }
        return response;
    }

    @GetMapping(value = "/loadmodulesandservices", produces = MediaType.APPLICATION_JSON)
    @ResponseBody

    @Operation(summary = "${loyaltyManagementController.loadModulesAndServices.name}", description = "${loyaltyManagementController.loadModulesAndServices.desc}",

            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ModulesAndServiceResponseVO.class)))}

                    ),

                    @ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class))

                                    , examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})})})


    public ModulesAndServiceResponseVO loadModulesAndServices(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse response1, HttpServletRequest httpServletRequest
    ) throws Exception {

        final String METHOD_NAME = "loadModulesAndServices";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        ModulesAndServiceResponseVO response = new ModulesAndServiceResponseVO();
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
            response = service.loadModulesAndServices(con, userVO, response);

        } finally {

            if (mcomCon != null) {
                mcomCon.close(CLASS_NAME + "#" + METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting forward=");
            }
        }
        return response;
    }

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${loyaltyManagementController.add.name}", description = "${loyaltyManagementController.add.desc}",

            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)))}

                    ),

                    @ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class))

                                    , examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})})})


    public BaseResponse addProfileDetails(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse response1, HttpServletRequest httpServletRequest,
            @RequestBody AddProfileDetailsRequestVO requestVO) throws Exception {

        final String METHOD_NAME = "addProfileDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        BaseResponse response = new BaseResponse();
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
            response = service.addProfileDetails(con, userVO, requestVO, response);

        } finally {

            if (mcomCon != null) {
                mcomCon.close(CLASS_NAME + "#" + METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting ");
            }
        }
        return response;
    }


    @PostMapping(value = "/delete", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${loyaltyManagementController.delete.name}", description = "${loyaltyManagementController.delete.desc}",

            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)))}

                    ),

                    @ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class))

                                    , examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})})})


    public BaseResponse deleteLmsProfile(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse response1, HttpServletRequest httpServletRequest,
            @RequestBody DeleteLmsProfileRequestVO requestVO) throws Exception {

        final String METHOD_NAME = "deleteLmsProfile";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        BaseResponse response = new BaseResponse();
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
            response = service.deleteLmsProfile(con, mcomCon, userVO, requestVO, response);

        } finally {

            if (mcomCon != null) {
                mcomCon.close(CLASS_NAME + "#" + METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting ");
            }
        }
        return response;
    }

    @PostMapping(value = "/modify", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${loyaltyManagementController.modify.name}", description = "${loyaltyManagementController.modify.desc}",

            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)))}

                    ),

                    @ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class))

                                    , examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})})})


    public BaseResponse modifyProfileDetails(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse response1, HttpServletRequest httpServletRequest,
            @RequestBody ModifyProfileDetailsRequestVO requestVO) throws Exception {

        final String METHOD_NAME = "modifyProfileDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        BaseResponse response = new BaseResponse();
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
            response = service.modifyProfileDetails(con, userVO, requestVO, response);

        } finally {

            if (mcomCon != null) {
                mcomCon.close(CLASS_NAME + "#" + METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting ");
            }
        }
        return response;
    }

    @PostMapping(value = "/suspend", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${loyaltyManagementController.suspend.name}", description = "${loyaltyManagementController.suspend.desc}",

            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)))}

                    ),

                    @ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class))

                                    , examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})})})


    public BaseResponse suspendProfileDetails(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse response1, HttpServletRequest httpServletRequest,
            @RequestBody SuspendRequestVO requestVO) throws Exception {

        final String METHOD_NAME = "suspendProfileDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        BaseResponse response = new BaseResponse();
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
            response = service.suspendProfileDetails(con, userVO, requestVO, response);

        } finally {

            if (mcomCon != null) {
                mcomCon.close(CLASS_NAME + "#" + METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting ");
            }
        }
        return response;
    }

    @PostMapping(value = "/approve", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${loyaltyManagementController.approve.name}", description = "${loyaltyManagementController.approve.desc}",

            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)))}

                    ),

                    @ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class))

                                    , examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})})})


    public BaseResponse approveProfileDetails(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse response1, HttpServletRequest httpServletRequest,
            @RequestBody ApproveProfileRequestVO requestVO) throws Exception {

        final String METHOD_NAME = "approveProfileDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        BaseResponse response = new BaseResponse();
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
            response = service.approveProfileDetails(con, userVO, requestVO, response);
        } finally {

            if (mcomCon != null) {
                mcomCon.close(CLASS_NAME + "#" + METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting ");
            }
        }
        return response;
    }

    @PostMapping(value = "/loadmsgs", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${loyaltyManagementController.loadmsgs.name}", description = "${loyaltyManagementController.loadmsgs.desc}",

            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProfileMessageDetailsVO.class)))}

                    ),

                    @ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class))

                                    , examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})})})


    public ProfileMessageDetailsVO getMessagesProfileDetails(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse response1, HttpServletRequest httpServletRequest,
            @RequestBody SuspendRequestVO requestVO) throws Exception {

        final String METHOD_NAME = "getMessagesProfileDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        ProfileMessageDetailsVO response = new ProfileMessageDetailsVO();
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
            response = service.getProfileMessageDetails(con, userVO, requestVO, response);
        } finally {

            if (mcomCon != null) {
                mcomCon.close(CLASS_NAME + "#" + METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting ");
            }
        }
        return response;
    }


    @PostMapping(value = "/updatemsgs", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${loyaltyManagementController.updatemsgs.name}", description = "${loyaltyManagementController.updatemsgs.desc}",

            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)))}

                    ),

                    @ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class))

                                    , examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})})})


    public BaseResponse updateMessagesProfileDetails(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse response1, HttpServletRequest httpServletRequest,
            @RequestBody UpdateMessageProfileRequestVO requestVO) throws Exception {

        final String METHOD_NAME = "updateMessagesProfileDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        BaseResponse response = new BaseResponse();
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
            response = service.updateMessageDetails(con, userVO, requestVO, response);
        } finally {

            if (mcomCon != null) {
                mcomCon.close(CLASS_NAME + "#" + METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting ");
            }
        }
        return response;
    }


    @PostMapping(value = "/resume", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${loyaltyManagementController.resume.name}", description = "${loyaltyManagementController.resume.desc}",

            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProfileMessageDetailsVO.class)))}

                    ),

                    @ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class))

                                    , examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})})})


    public BaseResponse resumeProfileDetails(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse response1, HttpServletRequest httpServletRequest,
            @RequestBody SuspendRequestVO requestVO) throws Exception {

        final String METHOD_NAME = "resumeProfileDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        BaseResponse response = new BaseResponse();
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
            response = service.resumePofileDetails(con, userVO, requestVO, response);
        } finally {

            if (mcomCon != null) {
                mcomCon.close(CLASS_NAME + "#" + METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting ");
            }
        }
        return response;
    }

    @GetMapping(value = "/loadaprvprflandvrsns", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${loyaltyManagementController.aprvprfl.name}", description = "${loyaltyManagementController.aprvprfl.desc}",

            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ApproveProfilesAndVersionsResponseVO.class)))}

                    ),

                    @ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class))

                                    , examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})})})


    public ApproveProfilesAndVersionsResponseVO loadApproveProfilesAndVersions(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse response1, HttpServletRequest httpServletRequest) throws Exception {

        final String METHOD_NAME = "loadApproveProfilesAndVersions";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        ApproveProfilesAndVersionsResponseVO response = new ApproveProfilesAndVersionsResponseVO();
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
            response = service.loadApprovePofilesAndVersionsDetails(con, userVO, response);
        } finally {

            if (mcomCon != null) {
                mcomCon.close(CLASS_NAME + "#" + METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting ");
            }
        }
        return response;
    }

    @PostMapping(value = "/loadaprvprfls", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${loyaltyManagementController.aprvprfls.name}", description = "${loyaltyManagementController.aprvprfls.desc}",

            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProfileDetailsVersionsResponseVO.class)))}

                    ),

                    @ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class))

                                    , examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})})})


    public ProfileDetailsVersionsResponseVO loadApproveProfilesDetails(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse response1, HttpServletRequest httpServletRequest,
            @RequestBody SuspendRequestVO requestVO) throws Exception {

        final String METHOD_NAME = "loadApproveProfilesDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        ProfileDetailsVersionsResponseVO response = new ProfileDetailsVersionsResponseVO();
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
            response = service.loadApprovePofilesDetails(con, userVO,requestVO, response);
        } finally {

            if (mcomCon != null) {
                mcomCon.close(CLASS_NAME + "#" + METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting ");
            }
        }
        return response;
    }
}

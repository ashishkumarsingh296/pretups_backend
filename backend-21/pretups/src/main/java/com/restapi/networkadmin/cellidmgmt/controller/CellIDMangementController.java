package com.restapi.networkadmin.cellidmgmt.controller;

import com.btsl.common.BaseResponse;
import com.btsl.common.BaseResponseRedoclyCommon;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.SchemaConstants;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.channelAdmin.restrictedlistmgmt.requestVO.UploadFileRequestVO;
import com.restapi.networkadmin.ICCIDIMSIkeymanagement.requestVO.MSISDNAndICCIDRequestVO;
import com.restapi.networkadmin.cellidmgmt.requestVO.AddCellIdMgmtRequestVO;
import com.restapi.networkadmin.cellidmgmt.requestVO.ModifyCellIdMgmtRequestVO;
import com.restapi.networkadmin.cellidmgmt.responseVO.CellGroupManagementListResponseVO;
import com.restapi.networkadmin.cellidmgmt.serviceI.CellIDMangementServiceI;
import com.restapi.user.service.FileDownloadResponse;
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
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.util.Locale;

@Tag(name = "${cellIdManagementController.name}", description = "${cellIdManagementController.desc}")
@RestController
@RequestMapping(value = "/v1/networkadmin/cellgrpmgmt/")
public class CellIDMangementController {

    public static final Log LOG = LogFactory.getLog(CellIDMangementController.class.getName());
    public static final String CLASS_NAME = "CellIDMangementController";

    @Autowired
    private CellIDMangementServiceI cellIDMangementServiceI;

    @GetMapping(value = "cellgrplist", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${cellIdManagementController.cellGroupManagementList.name}", description = "${cellIdManagementController.cellGroupManagementList.desc}",

            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(additionalProperties = Schema.AdditionalPropertiesValue.FALSE, implementation = CellGroupManagementListResponseVO.class)))}

                    ),

                    @ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class))

                                    , examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})}),
                    @ApiResponse(responseCode = Constants.API_SECURITY_RESPONSE_CODE, description = Constants.API_SECURITY_RESPONSE_CODE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_MULTI_REQUEST_RESPONSE_CODE, description = Constants.API_MULTI_REQUEST_RESPONSE_CODE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NA_RESPONSE_CODE, description = Constants.API_NA_RESPONSE_CODE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_DEFAULT_RESPONSE_CODE, description = Constants.API_DEFAULT_RESPONSE_CODE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )})
            })
    public CellGroupManagementListResponseVO cellGroupManagementList(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse response1, HttpServletRequest httpServletRequest) throws Exception {
        String methodName = "cellGroupManagementList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        CellGroupManagementListResponseVO response;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = cellIDMangementServiceI.getCellGroupList(con, userVO, locale);

        } finally {
            if (mcomCon != null) {
                mcomCon.close(CLASS_NAME + "#" + methodName);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting =");
            }
        }
        return response;
    }

    @PostMapping(value = "add", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${cellIdManagementController.cellGroupManagementAdd.name}", description = "${cellIdManagementController.cellGroupManagementAdd.desc}",

            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(additionalProperties = Schema.AdditionalPropertiesValue.FALSE, implementation = BaseResponse.class)))}

                    ),

                    @ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class))

                                    , examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})}), @ApiResponse(responseCode = Constants.API_SECURITY_RESPONSE_CODE, description = Constants.API_SECURITY_RESPONSE_CODE_DESC, content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                            @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                    )}),
                    @ApiResponse(responseCode = Constants.API_MULTI_REQUEST_RESPONSE_CODE, description = Constants.API_MULTI_REQUEST_RESPONSE_CODE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NA_RESPONSE_CODE, description = Constants.API_NA_RESPONSE_CODE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_DEFAULT_RESPONSE_CODE, description = Constants.API_DEFAULT_RESPONSE_CODE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )})
            })
    public BaseResponse addCellIdManagment(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                           HttpServletResponse response1, HttpServletRequest httpServletRequest,
                                           @RequestBody AddCellIdMgmtRequestVO requestVO) throws Exception {
        String methodName = "addCellIdManagment";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        BaseResponse response;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = cellIDMangementServiceI.addCellGroupId(con, userVO, locale, requestVO);

        } finally {
            if (mcomCon != null) {
                mcomCon.close(CLASS_NAME + "#" + methodName);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting =");
            }
        }
        return response;
    }


    @PostMapping(value = "modify", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${cellIdManagementController.cellGroupManagementModify.name}", description = "${cellIdManagementController.cellGroupManagementModify.desc}",

            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(additionalProperties = Schema.AdditionalPropertiesValue.FALSE, implementation = BaseResponse.class)))}

                    ),

                    @ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class))

                                    , examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})}), @ApiResponse(responseCode = Constants.API_SECURITY_RESPONSE_CODE, description = Constants.API_SECURITY_RESPONSE_CODE_DESC, content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                            @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                    )}),
                    @ApiResponse(responseCode = Constants.API_MULTI_REQUEST_RESPONSE_CODE, description = Constants.API_MULTI_REQUEST_RESPONSE_CODE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NA_RESPONSE_CODE, description = Constants.API_NA_RESPONSE_CODE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_DEFAULT_RESPONSE_CODE, description = Constants.API_DEFAULT_RESPONSE_CODE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )})
            })
    public BaseResponse modifyCellIdManagment(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                              HttpServletResponse response1, HttpServletRequest httpServletRequest,
                                              @RequestBody ModifyCellIdMgmtRequestVO requestVO) throws Throwable {
        String methodName = "modifyCellIdManagment";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        BaseResponse response;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = cellIDMangementServiceI.modifyCellGroupId(con, userVO, locale, requestVO);

        } finally {
            if (mcomCon != null) {
                mcomCon.close(CLASS_NAME + "#" + methodName);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting =");
            }
        }
        return response;
    }


    @GetMapping(value = "delete", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${cellIdManagementController.cellGroupManagementDelete.name}", description = "${cellIdManagementController.cellGroupManagementDelete.desc}",

            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(additionalProperties = Schema.AdditionalPropertiesValue.FALSE, implementation = BaseResponse.class)))}

                    ),

                    @ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class))

                                    , examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})}),
                    @ApiResponse(responseCode = Constants.API_SECURITY_RESPONSE_CODE, description = Constants.API_SECURITY_RESPONSE_CODE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_MULTI_REQUEST_RESPONSE_CODE, description = Constants.API_MULTI_REQUEST_RESPONSE_CODE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NA_RESPONSE_CODE, description = Constants.API_NA_RESPONSE_CODE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_DEFAULT_RESPONSE_CODE, description = Constants.API_DEFAULT_RESPONSE_CODE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )})
            })
    public BaseResponse deleteCellGroupId(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse response1, HttpServletRequest httpServletRequest,
            @RequestParam("groupId") @Pattern(regexp = SchemaConstants.STRING_INPUT_PATTERN, message = "Group Id is invalid") @Schema(pattern = SchemaConstants.STRING_INPUT_PATTERN, maxLength = SchemaConstants.STRING_MAX_SIZE) String groupId) throws Throwable {
        String methodName = "deleteCellGroupId";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        BaseResponse response;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = cellIDMangementServiceI.deleteCellGroupId(con, userVO, locale, groupId);

        } finally {
            if (mcomCon != null) {
                mcomCon.close(CLASS_NAME + "#" + methodName);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting =");
            }
        }
        return response;
    }


    @GetMapping(value = "associatetemplate", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${cellIdManagementController.cellGroupIdAssociateTemplate.name}", description = "${cellIdManagementController.cellGroupIdAssociateTemplate.desc}",

            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(additionalProperties = Schema.AdditionalPropertiesValue.FALSE, implementation = FileDownloadResponse.class)))}

                    ),

                    @ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class))

                                    , examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})}), @ApiResponse(responseCode = Constants.API_SECURITY_RESPONSE_CODE, description = Constants.API_SECURITY_RESPONSE_CODE_DESC, content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                            @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                    )}),
                    @ApiResponse(responseCode = Constants.API_MULTI_REQUEST_RESPONSE_CODE, description = Constants.API_MULTI_REQUEST_RESPONSE_CODE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NA_RESPONSE_CODE, description = Constants.API_NA_RESPONSE_CODE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_DEFAULT_RESPONSE_CODE, description = Constants.API_DEFAULT_RESPONSE_CODE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )})
            })
    public FileDownloadResponse cellIdAssociateTemplate(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse response1, HttpServletRequest httpServletRequest) throws Throwable {
        String methodName = "cellIdAssociateTemplate";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        FileDownloadResponse response;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = cellIDMangementServiceI.getCellIdAssociateTemplate(con, userVO, locale);

        } finally {
            if (mcomCon != null) {
                mcomCon.close(CLASS_NAME + "#" + methodName);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting =");
            }
        }
        return response;
    }

    @PostMapping(value = "associate", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${cellIdManagementController.cellGroupIdAssociate.name}", description = "${cellIdManagementController.cellGroupIdAssociate.desc}",

            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(additionalProperties = Schema.AdditionalPropertiesValue.FALSE, implementation = FileDownloadResponse.class)))}

                    ),

                    @ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = FileDownloadResponse.class))
                            )}),
                    @ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = FileDownloadResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = FileDownloadResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = FileDownloadResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})}), @ApiResponse(responseCode = Constants.API_SECURITY_RESPONSE_CODE, description = Constants.API_SECURITY_RESPONSE_CODE_DESC, content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                            @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                    )}),
                    @ApiResponse(responseCode = Constants.API_MULTI_REQUEST_RESPONSE_CODE, description = Constants.API_MULTI_REQUEST_RESPONSE_CODE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NA_RESPONSE_CODE, description = Constants.API_NA_RESPONSE_CODE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_DEFAULT_RESPONSE_CODE, description = Constants.API_DEFAULT_RESPONSE_CODE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )})
            })
    public FileDownloadResponse cellGroupIDAssociate(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                                     HttpServletResponse response1, HttpServletRequest httpServletRequest,
                                                     @RequestBody UploadFileRequestVO requestVO) throws Throwable {
        String methodName = "cellGroupIDAssociate";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        FileDownloadResponse response;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = cellIDMangementServiceI.associateCellGroupID(con, userVO, locale, requestVO);

        } finally {
            if (mcomCon != null) {
                mcomCon.close(CLASS_NAME + "#" + methodName);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting =");
            }
        }
        return response;
    }


    @GetMapping(value = "reassociatetemplate", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${cellIdManagementController.cellGroupIdReassociateTemplate.name}", description = "${cellIdManagementController.cellGroupIdReassociateTemplate.desc}",

            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(additionalProperties = Schema.AdditionalPropertiesValue.FALSE, implementation = FileDownloadResponse.class)))}

                    ),

                    @ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class))

                                    , examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.BAD_REQUEST)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})}), @ApiResponse(responseCode = Constants.API_SECURITY_RESPONSE_CODE, description = Constants.API_SECURITY_RESPONSE_CODE_DESC, content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                            @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                    )}),
                    @ApiResponse(responseCode = Constants.API_MULTI_REQUEST_RESPONSE_CODE, description = Constants.API_MULTI_REQUEST_RESPONSE_CODE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NA_RESPONSE_CODE, description = Constants.API_NA_RESPONSE_CODE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_DEFAULT_RESPONSE_CODE, description = Constants.API_DEFAULT_RESPONSE_CODE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )})
            })
    public FileDownloadResponse cellIdReassociateTemplate(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse response1, HttpServletRequest httpServletRequest) throws Throwable {
        String methodName = "cellIdReassociateTemplate";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        FileDownloadResponse response;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = cellIDMangementServiceI.getCellIdReassociateTemplate(con, userVO, locale);

        } finally {
            if (mcomCon != null) {
                mcomCon.close(CLASS_NAME + "#" + methodName);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting =");
            }
        }
        return response;
    }


    @PostMapping(value = "reassociate", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${cellIdManagementController.cellGroupIdReassociate.name}", description = "${cellIdManagementController.cellGroupIdReassociate.desc}",

            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(additionalProperties = Schema.AdditionalPropertiesValue.FALSE, implementation = FileDownloadResponse.class)))}

                    ),

                    @ApiResponse(responseCode = Constants.API_BAD_REQ_RESPONSE_CODE, description = Constants.API_BAD_REQ_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = FileDownloadResponse.class))
                            )}),
                    @ApiResponse(responseCode = Constants.API_UNAUTH_RESPONSE_CODE, description = Constants.API_UNAUTH_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = FileDownloadResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.UNAUTH)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NOT_FOUND_RESPONSE_CODE, description = Constants.API_NOT_FOUND_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = FileDownloadResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = Constants.API_INTERNAL_ERROR_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = FileDownloadResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)})}), @ApiResponse(responseCode = Constants.API_SECURITY_RESPONSE_CODE, description = Constants.API_SECURITY_RESPONSE_CODE_DESC, content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                            @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                    )}),
                    @ApiResponse(responseCode = Constants.API_MULTI_REQUEST_RESPONSE_CODE, description = Constants.API_MULTI_REQUEST_RESPONSE_CODE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_NA_RESPONSE_CODE, description = Constants.API_NA_RESPONSE_CODE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )}),
                    @ApiResponse(responseCode = Constants.API_DEFAULT_RESPONSE_CODE, description = Constants.API_DEFAULT_RESPONSE_CODE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(maxItems = SchemaConstants.ARRAY_MAX_SIZE, schema = @Schema(implementation = BaseResponse.class)), examples = {
                                    @ExampleObject(value = BaseResponseRedoclyCommon.NOT_FOUND)}

                            )})
            })
    public FileDownloadResponse cellGroupIDReassociate(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                                       HttpServletResponse response1, HttpServletRequest httpServletRequest,
                                                       @RequestBody UploadFileRequestVO requestVO) throws Throwable {
        String methodName = "cellGroupIDReassociate";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        FileDownloadResponse response;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = cellIDMangementServiceI.reassociateCellGroupID(con, userVO, locale, requestVO);

        } finally {
            if (mcomCon != null) {
                mcomCon.close(CLASS_NAME + "#" + methodName);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting =");
            }
        }
        return response;
    }
}

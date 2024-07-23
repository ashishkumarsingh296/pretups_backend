package com.restapi.networkadmin.geogrpahycellidmapping.controller;

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
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.networkadmin.ICCIDIMSIkeymanagement.responseVO.UploadMSISDNWithICCIDResponseVO;
import com.restapi.networkadmin.geogrpahycellidmapping.requestVO.GegraphicalCellIdFileRequestVO;
import com.restapi.networkadmin.geogrpahycellidmapping.responseVO.DownloadTemplateGeographyCellIdMappingRespVO;
import com.restapi.networkadmin.geogrpahycellidmapping.responseVO.UploadFileToAssociateCellIdResponseVO;
import com.restapi.networkadmin.geogrpahycellidmapping.service.GeographyCellIdMappingService;
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
import static com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC;

@Tag(name = "${GeographyCellIdMappingController.name}", description = "${GeographyCellIdMappingController.desc}")
@RestController
@RequestMapping(value = "/v1/networkadmin/geogrpahycellidmapping")
@ControllerAdvice
public class GeographyCellIdMappingController {

        private final GeographyCellIdMappingService  geographyCellIdMappingService;
        public final Log LOG = LogFactory.getLog(this.getClass().getName());
    public static final String CLASS_NAME = "GeographyCellIdMappingController";


    public GeographyCellIdMappingController(GeographyCellIdMappingService geographyCellIdMappingService) {
        this.geographyCellIdMappingService = geographyCellIdMappingService;
    }

    @GetMapping(value = "/downloadtemplatetogeographycellidmapping", produces = MediaType.APPLICATION_JSON)
        @ResponseBody
        @Operation(summary = "${GeographyCellIdMappingController.downloadTemplateToGeographyCellIdMapping.name}", description = "${GeographyCellIdMappingController.downloadTemplateToGeographyCellIdMapping.desc}",
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

        public DownloadTemplateGeographyCellIdMappingRespVO downloadTemplateToGeographyCellIdMapping(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                                                 HttpServletResponse response1)
                throws Exception {

            final String METHOD_NAME = "downloadTemplateToGeographyCellIdMapping";
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, PretupsI.ENTERED);
            }

            Connection con = null;
            MComConnectionI mcomCon = null;
            UserVO userVO = null;
            UserDAO userDAO = null;
            Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        DownloadTemplateGeographyCellIdMappingRespVO response;

            try {
                OAuthUser OAuthUserData = new OAuthUser();
                OAuthUserData.setData(new OAuthUserData());
                OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
                mcomCon = new MComConnection();
                con = mcomCon.getConnection();
                userDAO = new UserDAO();
                String loginID = OAuthUserData.getData().getLoginid();
                userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
                response = geographyCellIdMappingService.downloadTemplateToGeographyCellIdMapping(con,userVO, locale);
                response.setStatus((HttpStatus.SC_OK));
                response.setMessageCode(PretupsErrorCodesI.TEMPLATE_DOWNLOAD_SUCCESS);
                response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.TEMPLATE_DOWNLOAD_SUCCESS, null));
            } finally {
                if (mcomCon != null) {
                    mcomCon.close(METHOD_NAME);
                    mcomCon = null;
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug(METHOD_NAME, PretupsI.EXITED );
                }
            }
            return response;
        }

    @PostMapping(value = "/uploadfiletoassociatecellid", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${GeographyCellIdMappingController.uploadFileToAssociatecellId.name}", description = "${GeographyCellIdMappingController.uploadFileToAssociatecellId.desc}",

            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UploadMSISDNWithICCIDResponseVO.class))) }

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

    public UploadFileToAssociateCellIdResponseVO uploadMSISDNWithICCID(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse response1, HttpServletRequest httpServletRequest,
            @RequestBody GegraphicalCellIdFileRequestVO requestVO) throws Exception {

        final String METHOD_NAME = "uploadFileToAssociatecellId";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, PretupsI.ENTERED);
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        UploadFileToAssociateCellIdResponseVO response = new UploadFileToAssociateCellIdResponseVO();
        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = geographyCellIdMappingService.uploadFileToAssociatecellId(con, (MComConnection) mcomCon, userVO, requestVO, response,locale);

        }  finally {

            if (mcomCon != null) {
                mcomCon.close(CLASS_NAME + "#" + METHOD_NAME);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, PretupsI.EXITED);
            }
        }
        return response;
    }
}

package com.restapi.uploadICCIDKey;

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
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;


import com.restapi.networkadmin.ICCIDIMSIkeymanagement.requestVO.MSISDNWithICCIDFileRequestVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Locale;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${batchUploadICCIDKeyController.name}", description = "${batchUploadICCIDKeyController.desc}")
@RestController
@RequestMapping(value = "/v1/networkadmin/batchuploadiccidkey")
public class UploadICCIDKeyController {
    public static final Log log = LogFactory.getLog(UploadICCIDKeyController.class.getName());
    public static final String classname = "UploadICCIDKeyController";

    @Autowired
    private UploadICCIDKeyService uploadICCIDKeyServiceI;

    @PostMapping(value = "/processbatchkeyupload", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    //@ApiOperation(value = "Process Batch Upload ICCID Key", response = BaseResponse.class, authorizations = {@Authorization(value = "Authorization")})
    //@ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = BaseResponse.class), @ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 404, message = "Not Found")})

    @Operation(summary = "${batchUploadICCIDKeyController.processbatchkeyupload.name}", description = "${batchUploadICCIDKeyController.processbatchkeyupload.desc}",

            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BaseResponse.class))) }

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

    public BaseResponse process(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1, HttpServletRequest httpServletRequest, @Valid @RequestBody MSISDNWithICCIDFileRequestVO requestVO) throws Exception {

        final String METHOD_NAME = "process";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
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
            response = uploadICCIDKeyServiceI.processFile(headers, httpServletRequest, response1, con, mcomCon, locale, userVO, response, requestVO);

        } catch (BTSLBaseException e) {
            log.error(METHOD_NAME, "Exceptin:e=" + e);
            log.errorTrace(METHOD_NAME, e);
            String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), e.getArgs());
            response.setMessageCode(e.getMessageKey());
            response.setMessage(msg);

            if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
                response1.setStatus(org.apache.http.HttpStatus.SC_UNAUTHORIZED);
                response.setStatus(org.apache.http.HttpStatus.SC_UNAUTHORIZED);
            } else {
                response1.setStatus(org.apache.http.HttpStatus.SC_BAD_REQUEST);
                response.setStatus(org.apache.http.HttpStatus.SC_BAD_REQUEST);
            }

        } catch (Exception e) {
            log.error(METHOD_NAME, "Exceptin:e=" + e);
            log.errorTrace(METHOD_NAME, e);
            response1.setStatus(org.apache.http.HttpStatus.SC_BAD_REQUEST);
            response.setStatus(org.apache.http.HttpStatus.SC_BAD_REQUEST);
            String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UPLOAD_AND_PROCESS_FILE_FAIL, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.UPLOAD_AND_PROCESS_FILE_FAIL);
        } finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }
        return response;
    }
}

package com.restapi.networkadmin.bulkambiguoussettlement;

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
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.networkadmin.bulkambiguoussettlement.requestVO.BulkAmbiguousSettlementRequestVO;
import com.restapi.networkadmin.bulkambiguoussettlement.service.BulkAmbiguousSettlementServiceI;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Locale;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${BulkAmbiguousSettlementController.name}", description = "${BulkAmbiguousSettlementController.desc}")
// @Api(tags

@RestController
@RequestMapping(value = "/v1/networkadmin/reconciliation/bulkambiguoussettlement")
public class BulkAmbiguousSettlementController {
    public static final Log log = LogFactory.getLog(BulkAmbiguousSettlementController.class.getName());
    public static final String classname = "BulkAmbiguousSettlementController";

    @Autowired
    private BulkAmbiguousSettlementServiceI bulkAmbiguousSettlementServiceI;

    @PostMapping(value = "/upload", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    /*@ApiOperation(value = "upload text file for bulk ambiguous settlement", response = BaseResponse.class, authorizations = {
            @Authorization(value = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found")})*/

    @Operation(summary = "${dummy.summary}", description = "${dummy.description}",

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
    public BaseResponse uploadFile(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse httpServletResponse,
            HttpServletRequest httpServletRequest, @RequestBody BulkAmbiguousSettlementRequestVO requestVO) throws Exception {

        if (log.isDebugEnabled()) {
            log.debug("uploadFile", "Entered");
        }
        final String methodName = "uploadFile";
        BaseResponse response = new BaseResponse();

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
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, httpServletResponse);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            loginUserID = OAuthUserData.getData().getLoginid();
            response = bulkAmbiguousSettlementServiceI.uploadFile(con, locale, loginUserID, httpServletResponse, requestVO);

        } catch (BTSLBaseException be) {
            log.error(methodName, "Exception=" + be);
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
            response.setStatus((HttpStatus.SC_BAD_REQUEST));
            String resmsg = RestAPIStringParser.getMessage(
                    new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                    PretupsErrorCodesI.BULKAMBIGUOUSSETTLEMENT_MESSAGE, new String[] { "Failed" });
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.BULKAMBIGUOUSSETTLEMENT_MESSAGE);
            httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
        } finally {
            if (mcomCon != null) {
                mcomCon.close(methodName);
                mcomCon = null;
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting" + methodName);
            }
        }
        return response;
    }
}

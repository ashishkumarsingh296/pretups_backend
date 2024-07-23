package com.restapi.batchC2STransferRule;

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
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;


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
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Locale;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${batchC2STransferRuleController.name}", description = "${batchC2STransferRuleController.desc}")
@RestController
@RequestMapping(value = "/v1/networkadmin/batchc2stransferrule")
public class BatchC2STransferRuleController {
    public static final Log log = LogFactory.getLog(BatchC2STransferRuleController.class.getName());
    public static final String classname = "batchC2STransferRuleController";

    @Autowired
    private BatchC2STransferRuleServiceI batchC2STransferRuleServiceI;

    @GetMapping(value = "/downloadtemplate", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    //@ApiOperation(value = "This methods downloads the template for batch c2s transfer rule", response = BatchC2STransferRuleFileDownloadResponse.class, authorizations = {@Authorization(value = "Authorization")})
    //@ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = BatchC2STransferRuleFileDownloadResponse.class), @ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 404, message = "Not Found")})

    @Operation(summary = "${batchC2STransferRuleController.downloadtemplate.name}", description = "${batchC2STransferRuleController.downloadtemplate.desc}",

            responses = {
                    @ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BatchC2STransferRuleFileDownloadResponse.class))) }

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

    public BatchC2STransferRuleFileDownloadResponse downloadTemplate(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1, HttpServletRequest httpServletRequest) throws Exception {

        final String methodName = "downloadTemplate";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

        UserVO userVO = null;
        UserDAO userDAO = null;

        BatchC2STransferRuleFileDownloadResponse response = new BatchC2STransferRuleFileDownloadResponse();

        try {
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();

            OAuthUser oAuthUser = new OAuthUser();
            oAuthUser.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response1);

            userVO = new UserVO();
            userDAO = new UserDAO();
            userVO = userDAO.loadUsersDetails(con, oAuthUser.getData().getMsisdn());

            response = batchC2STransferRuleServiceI.downloadTemplateForbatch(headers, httpServletRequest, response1, con, mcomCon, locale, userVO, response);
        } catch (BTSLBaseException be) {
            log.error(methodName, "Exception:e=" + be);
            log.errorTrace(methodName, be);
            if (!BTSLUtil.isNullString(be.getMessage())) {
                String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
                response.setMessageCode(be.getMessage());
                response.setMessage(msg);
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                response1.setStatus(HttpStatus.SC_BAD_REQUEST);
            }

        } catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus((HttpStatus.SC_BAD_REQUEST));
            String resmsg = RestAPIStringParser.getMessage(new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY), PretupsErrorCodesI.TEMPLATE_DOWNLOAD_FAIL, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.TEMPLATE_DOWNLOAD_FAIL);
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

    @PostMapping(value = "/processbatchc2stransferrule", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    //@ApiOperation(value = "Process Batch C2S Transfer Rule", response = BatchC2STransferRuleResponseVO.class, authorizations = {@Authorization(value = "Authorization")})
    //@ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = BatchC2STransferRuleResponseVO.class), @ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 404, message = "Not Found")})

    @Operation(summary = "${batchC2STransferRuleController.processbatchc2stransferrule.name}", description = "${batchC2STransferRuleController.processbatchc2stransferrule.desc}",

            responses = {
                    @ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BatchC2STransferRuleResponseVO.class))) }

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

    public BatchC2STransferRuleResponseVO processBatchC2STransferRule(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1, HttpServletRequest httpServletRequest, @Valid @RequestBody BatchC2STransferRuleRequestVO requestVO) throws Exception {

        final String METHOD_NAME = "processBatchC2STransferRule";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        BatchC2STransferRuleResponseVO response = new BatchC2STransferRuleResponseVO();
        try {

            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = batchC2STransferRuleServiceI.processFile(headers, httpServletRequest, response1, con, mcomCon, locale, userVO, response, requestVO);

        } catch (BTSLBaseException e) {
            log.error(METHOD_NAME, "Exceptin:e=" + e);
            log.errorTrace(METHOD_NAME, e);
            String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
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
            String resmsg = RestAPIStringParser.getMessage(locale, PretupsI.BULK_USER_FILE_NOT_UPLOADED, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsI.BULK_USER_FILE_NOT_UPLOADED);
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

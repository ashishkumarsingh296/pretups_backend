package com.restapi.networkadmin.o2creconciliation;


import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.BaseResponseRedoclyCommon;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.networkadmin.o2creconciliation.requestVO.O2CReconciliationFailRequestVO;
import com.restapi.networkadmin.o2creconciliation.requestVO.O2CReconciliationOrderApproveRequestVO;
import com.restapi.networkadmin.o2creconciliation.responseVO.O2CReconciliationListResponseVO;
import com.restapi.networkadmin.o2creconciliation.responseVO.O2CReconciliationTxnDetailVO;
import com.restapi.networkadmin.o2creconciliation.service.O2CReconciliationService;
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

@Tag(name = "${O2CReconciliationController.name}", description = "${O2CReconciliationController.desc}")
@RestController
@RequestMapping(value = "/v1/networkadmin/o2creconciliation")
@ControllerAdvice

public class O2CReconciliationController {

    private final O2CReconciliationService o2cReconciliationService;
    public final Log LOG = LogFactory.getLog(this.getClass().getName());

    public O2CReconciliationController(O2CReconciliationService o2cReconciliationService) {
        this.o2cReconciliationService = o2cReconciliationService;
    }

    @GetMapping(value = "/o2creconciliationlist", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${O2CReconciliationController.o2cReconciliationList.name}", description = "${O2CReconciliationController.o2cReconciliationList.desc}",
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

    public O2CReconciliationListResponseVO o2cReconciliationList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                                                 HttpServletResponse response1, HttpServletRequest httpServletRequest, @RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate)
            throws Exception {

        final String METHOD_NAME = "o2cReconciliationList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        O2CReconciliationListResponseVO response = new O2CReconciliationListResponseVO();

        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = o2cReconciliationService.o2cReconciliationList(con, userVO, fromDate, toDate);
            response.setStatus((HttpStatus.SC_OK));
            response.setMessageCode(PretupsErrorCodesI.O2C_RECON_LIST_SUCCESS);
            response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_RECON_LIST_SUCCESS, null));
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

    @GetMapping(value = "/o2creconciliationtransactiondetail", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${O2CReconciliationController.o2cReconciliationTransactionDetail.name}", description = "${O2CReconciliationController.o2cReconciliationTransactionDetail.desc}",
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

    public O2CReconciliationTxnDetailVO o2cReconciliationTransactionDetail(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                                                           HttpServletResponse response1, HttpServletRequest httpServletRequest, @RequestParam("transferId") String transferId)
            throws Exception {

        final String METHOD_NAME = "o2cReconciliationTransactionDetail";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        O2CReconciliationTxnDetailVO response = new O2CReconciliationTxnDetailVO();

        try {
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = o2cReconciliationService.o2cReconciliationTransactionDetail(con, userVO, transferId);
            response.setStatus((HttpStatus.SC_OK));
            response.setMessageCode(PretupsErrorCodesI.O2C_RECON_TRANSACTION_DETAIL_SUCCESS);
            response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.O2C_RECON_TRANSACTION_DETAIL_SUCCESS, new String[]{transferId}));
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

    @PostMapping(value = "/o2creconciliationfail", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${O2CReconciliationController.o2CReconciliationFail.name}", description = "${O2CReconciliationController.o2CReconciliationFail.desc}",
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

    public BaseResponse o2CReconciliationFail(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1,
            HttpServletRequest httpServletRequest, @RequestBody O2CReconciliationFailRequestVO request)
            throws Exception {

        final String METHOD_NAME = "o2CReconciliationFail";
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
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = o2cReconciliationService.o2cReconciliationFail(mcomCon, con, userVO, request);
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

    @PostMapping(value = "/orderapproval", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${O2CReconciliationController.orderApproval.name}", description = "${O2CReconciliationController.orderApproval.desc}",
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
    public BaseResponse orderApproval(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                      HttpServletResponse response1, HttpServletRequest httpServletRequest, @RequestBody O2CReconciliationOrderApproveRequestVO request) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "orderApproval";
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
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userDAO = new UserDAO();
            String loginID = OAuthUserData.getData().getLoginid();
            userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
            response = o2cReconciliationService.o2cOrderApproval(con,mcomCon, locale, userVO, request, httpServletRequest, response1);
        }finally {
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

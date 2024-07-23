package com.restapi.networkadmin.updatesimtxnid.controller;

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
import com.restapi.channelAdmin.bulkUploadOperations.requestVO.BulkOperationRequestVO;
import com.restapi.networkadmin.loyaltymanagement.controller.LoyaltyManagementController;
import com.restapi.networkadmin.loyaltymanagement.responseVO.ProfileDetailsVersionsResponseVO;
import com.restapi.networkadmin.loyaltymanagement.serviceI.LoyaltyManagementServiceI;
import com.restapi.networkadmin.updatesimtxnid.requestVO.BulkSimTXNIdRequestVO;
import com.restapi.networkadmin.updatesimtxnid.requestVO.SimTxnIdRequestVO;
import com.restapi.networkadmin.updatesimtxnid.responseVO.BulkSimTXNIdResponseVO;
import com.restapi.networkadmin.updatesimtxnid.serviceI.SimTXNIDServiceI;
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

import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
@Tag(name = "${updateSIMTXNIdController.name}", description = "${updateSIMTXNIdController.desc}")
@RestController
@RequestMapping(value = "/v1/networkadmin/updatesimtxnid")
public class SimTXNIDController {


        @Autowired
        private SimTXNIDServiceI service;
        public static final Log LOG = LogFactory.getLog(SimTXNIDController.class.getName());
        public static final String CLASS_NAME = "SimTXNIDController";

        @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON)
        @ResponseBody
        @Operation(summary = "${updateSIMTXNIdController.update.name}", description = "${updateSIMTXNIdController.update.desc}",

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
        public BaseResponse updateSIMTXNId(
                @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                HttpServletResponse response1, HttpServletRequest httpServletRequest,
               @RequestBody SimTxnIdRequestVO simTxnIdRequestVO ) throws Exception {

            final String METHOD_NAME = "updateSIMTXNId";
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
                service.updateSIMTXNId(con, userVO, simTxnIdRequestVO.getMsisdn(), response);

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


    @PostMapping(value = "/updatebulk", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${updateSIMTXNIdController.updatebulk.name}", description = "${updateSIMTXNIdController.updatebulk.desc}",

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
    public BulkSimTXNIdResponseVO updatebulkSIMTXNIds(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse response1, HttpServletRequest httpServletRequest,
            @RequestBody BulkSimTXNIdRequestVO requestVO
    ) throws Exception {

        final String METHOD_NAME = "updatebulkSIMTXNIds";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        UserVO userVO = null;
        UserDAO userDAO = null;
        BulkSimTXNIdResponseVO response = new BulkSimTXNIdResponseVO();
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
            service.updateBulkSIMTXNIds(con, userVO, requestVO, response);

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

}

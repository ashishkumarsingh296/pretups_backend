package com.restapi.networkadmin.serviceproductinterfacemapping;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.BaseResponseRedoclyCommon;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.networkadmin.serviceproductinterfacemapping.requestVO.AddServiceInterfaceMappingRequestVO;
import com.restapi.networkadmin.serviceproductinterfacemapping.requestVO.DeleteServiceInterfaceMappingRequestVO;
import com.restapi.networkadmin.serviceproductinterfacemapping.requestVO.ModifyServiceInterfaceMappingRequestVO;
import com.restapi.networkadmin.serviceproductinterfacemapping.responseVO.*;
import com.restapi.networkadmin.serviceproductinterfacemapping.service.ServiceProductInterfaceMappingServiceI;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Locale;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.util.MultiValueMap;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${ServiceProductInterfaceMappingController.name}", description = "${ServiceProductInterfaceMappingController.desc}")
@RestController
@RequestMapping(value = "/v1/networkadmin/serviceproductinterfacemapping")
public class ServiceProductInterfaceMappingController {

    public static final Log log = LogFactory.getLog(ServiceProductInterfaceMappingController.class.getName());
    public static final String classname = "ServiceProductInterfaceMappingController";

    @Autowired
    private ServiceProductInterfaceMappingServiceI serviceProductInterfaceMappingServiceI;


    @GetMapping(value= "/getservicetypesandinterfacelist", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${ServiceProductInterfaceMappingController.getServiceTypesAndInterfaceList.name}", description = "${ServiceProductInterfaceMappingController.getServiceTypesAndInterfaceList.desc}",
            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ServiceTypesAndInterfaceListResponseVO.class))) }

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
    public ServiceTypesAndInterfaceListResponseVO getServiceTypesAndInterfaceList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                                                                  HttpServletResponse response1, HttpServletRequest httpServletRequest) throws Exception {

        final String methodName = "getInterfaceNetworkMappingPrefixList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

        UserVO userVO = null;
        UserDAO userDAO = null;

        ServiceTypesAndInterfaceListResponseVO response = new ServiceTypesAndInterfaceListResponseVO();

        try {
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();

            OAuthUser oAuthUser = new OAuthUser();
            oAuthUser.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response1);

            userVO = new UserVO();
            userDAO = new UserDAO();
            userVO  = userDAO.loadUsersDetails(con, oAuthUser.getData().getMsisdn());

            response = serviceProductInterfaceMappingServiceI.getServiceTypesAndInterfaceList(headers,httpServletRequest,response1,con,mcomCon,locale,userVO,response);
        }
        catch (BTSLBaseException be) {
            log.error(methodName, "Exception:e=" + be);
            log.errorTrace(methodName, be);
            if (!BTSLUtil.isNullString(be.getMessage())) {
                String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
                response.setMessageCode(be.getMessage());
                response.setMessage(msg);
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                response1.setStatus(HttpStatus.SC_BAD_REQUEST);
            }

        }
        catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus((HttpStatus.SC_BAD_REQUEST));
            String resmsg = RestAPIStringParser.getMessage(
                    new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                    PretupsErrorCodesI.LIST_NOT_FOUND, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.LIST_NOT_FOUND);
            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
        }

        finally {
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














    ///////////////////////////////////////////////////////
    @GetMapping(value= "/getserviceinterfacemappingforview", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${ServiceProductInterfaceMappingController.getServiceInterfaceMappingForView.name}", description = "${ServiceProductInterfaceMappingController.getServiceInterfaceMappingForView.desc}",
            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ServiceInterfaceMappingForViewResponseVO.class))) }

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
    public ServiceInterfaceMappingForViewResponseVO getServiceInterfaceMappingForView(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                                                                      HttpServletResponse response1, HttpServletRequest httpServletRequest,
                                                                                      @Parameter(description = "serviceType", required = true) @RequestParam("serviceType") String serviceType) throws Exception {

        final String methodName = "getServiceInterfaceMappingForView";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

        UserVO userVO = null;
        UserDAO userDAO = null;

        ServiceInterfaceMappingForViewResponseVO response = new ServiceInterfaceMappingForViewResponseVO();

        try {
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();

            OAuthUser oAuthUser = new OAuthUser();
            oAuthUser.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response1);

            userVO = new UserVO();
            userDAO = new UserDAO();
            userVO  = userDAO.loadUsersDetails(con, oAuthUser.getData().getMsisdn());

            response = serviceProductInterfaceMappingServiceI.getServiceInterfaceMappingForView(headers,httpServletRequest,response1,con,mcomCon,locale,userVO,response,serviceType);
        }
        catch (BTSLBaseException be) {
            log.error(methodName, "Exception:e=" + be);
            log.errorTrace(methodName, be);
            if (!BTSLUtil.isNullString(be.getMessage())) {
                String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
                response.setMessageCode(be.getMessage());
                response.setMessage(msg);
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                response1.setStatus(HttpStatus.SC_BAD_REQUEST);
            }

        }
        catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus((HttpStatus.SC_BAD_REQUEST));
            String resmsg = RestAPIStringParser.getMessage(
                    new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                    PretupsErrorCodesI.LIST_NOT_FOUND, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.LIST_NOT_FOUND);
            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
        }
        finally {
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





    //Add service product interface mapping api's starts//

    @GetMapping(value= "/getserviceinterfacemappingforadd", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${ServiceProductInterfaceMappingController.getServiceInterfaceMappingForAdd.name}", description = "${ServiceProductInterfaceMappingController.getServiceInterfaceMappingForAdd.desc}",
            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ServiceInterfaceMappingForAddResponseVO.class))) }

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
    public ServiceInterfaceMappingForAddResponseVO getServiceInterfaceMappingForAdd(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                                                                    HttpServletResponse response1, HttpServletRequest httpServletRequest,
                                                                                    @Parameter(description = "serviceType", required = true) @RequestParam("serviceType") String serviceType,
                                                                                    @Parameter(description = "interfaceType", required = true) @RequestParam("interfaceType") String interfaceType
    ) throws Exception {

        final String methodName = "getServiceInterfaceMappingForAdd";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

        UserVO userVO = null;
        UserDAO userDAO = null;

        ServiceInterfaceMappingForAddResponseVO response = new ServiceInterfaceMappingForAddResponseVO();

        try {
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();

            OAuthUser oAuthUser = new OAuthUser();
            oAuthUser.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response1);

            userVO = new UserVO();
            userDAO = new UserDAO();
            userVO  = userDAO.loadUsersDetails(con, oAuthUser.getData().getMsisdn());

            response = serviceProductInterfaceMappingServiceI.getServiceInterfaceMappingForAdd(headers,httpServletRequest,response1,con,mcomCon,locale,userVO,response,serviceType,interfaceType);
        }
        catch (BTSLBaseException be) {
            log.error(methodName, "Exception:e=" + be);
            log.errorTrace(methodName, be);
            if (!BTSLUtil.isNullString(be.getMessage())) {
                String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
                response.setMessageCode(be.getMessage());
                response.setMessage(msg);
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                response1.setStatus(HttpStatus.SC_BAD_REQUEST);
            }

        }
        catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus((HttpStatus.SC_BAD_REQUEST));
            String resmsg = RestAPIStringParser.getMessage(
                    new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                    PretupsErrorCodesI.LIST_NOT_FOUND, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.LIST_NOT_FOUND);
            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
        }
        finally {
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













    @PostMapping(value= "/addserviceinterfacemapping", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${ServiceProductInterfaceMappingController.addServiceInterfaceMapping.name}", description = "${ServiceProductInterfaceMappingController.addServiceInterfaceMapping.desc}",
            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AddServiceInterfaceMappingResponseVO.class))) }

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
    public AddServiceInterfaceMappingResponseVO addServiceInterfaceMapping(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                                                           HttpServletResponse response1, HttpServletRequest httpServletRequest,
                                                                           @Parameter(description = "Add service product interface mapping RequestVO",  required = true) @RequestBody AddServiceInterfaceMappingRequestVO requestVO
    ) throws Exception {

        final String methodName = "getServiceInterfaceMappingForAdd";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

        UserVO userVO = null;
        UserDAO userDAO = null;

        AddServiceInterfaceMappingResponseVO response = new AddServiceInterfaceMappingResponseVO();

        try{
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();

            OAuthUser oAuthUser = new OAuthUser();
            oAuthUser.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response1);

            userVO = new UserVO();
            userDAO = new UserDAO();
            userVO  = userDAO.loadUsersDetails(con, oAuthUser.getData().getMsisdn());

            response = serviceProductInterfaceMappingServiceI.addServiceInterfaceMapping(headers,httpServletRequest,response1,con,mcomCon,locale,userVO,response,requestVO);

        }
        catch (BTSLBaseException be) {
            log.error(methodName, "Exception:e=" + be);
            log.errorTrace(methodName, be);
            if (!BTSLUtil.isNullString(be.getMessage())) {
                String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
                response.setMessageCode(be.getMessage());
                response.setMessage(msg);
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                response1.setStatus(HttpStatus.SC_BAD_REQUEST);
            }

        }
        catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus((HttpStatus.SC_BAD_REQUEST));
            String resmsg = RestAPIStringParser.getMessage(
                    new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                    PretupsErrorCodesI.PRODUT_SERVICE_INTERFACE_MAPPING_ADDITION_FAIL, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.PRODUT_SERVICE_INTERFACE_MAPPING_ADDITION_FAIL);
            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
        }
        finally{
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
    //Add service product interface mapping api ends










    //modify api's starts
    @GetMapping(value= "/getserviceinterfacemappingformodify", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${ServiceProductInterfaceMappingController.getServiceInterfaceMappingForModify.name}", description = "${ServiceProductInterfaceMappingController.getServiceInterfaceMappingForModify.desc}",
            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ServiceInterfaceMappingForModifyResponseVO.class))) }

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
public ServiceInterfaceMappingForModifyResponseVO getServiceInterfaceMappingForModify(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                                                                    HttpServletResponse response1, HttpServletRequest httpServletRequest,
                                                                                    @Parameter(description = "serviceType", required = true) @RequestParam("serviceType") String serviceType
    ) throws Exception {

        final String methodName = "getserviceinterfacemappingformodify";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

        UserVO userVO = null;
        UserDAO userDAO = null;

        ServiceInterfaceMappingForModifyResponseVO response = new ServiceInterfaceMappingForModifyResponseVO();

        try {
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();

            OAuthUser oAuthUser = new OAuthUser();
            oAuthUser.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response1);

            userVO = new UserVO();
            userDAO = new UserDAO();
            userVO  = userDAO.loadUsersDetails(con, oAuthUser.getData().getMsisdn());

            response = serviceProductInterfaceMappingServiceI.getServiceInterfaceMappingForModify(headers,httpServletRequest,response1,con,mcomCon,locale,userVO,response,serviceType);
        }
        catch (BTSLBaseException be) {
            log.error(methodName, "Exception:e=" + be);
            log.errorTrace(methodName, be);
            if (!BTSLUtil.isNullString(be.getMessage())) {
                String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
                response.setMessageCode(be.getMessage());
                response.setMessage(msg);
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                response1.setStatus(HttpStatus.SC_BAD_REQUEST);
            }

        }
        catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus((HttpStatus.SC_BAD_REQUEST));
            String resmsg = RestAPIStringParser.getMessage(
                    new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                    PretupsErrorCodesI.LIST_NOT_FOUND, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.LIST_NOT_FOUND);
            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
        }
        finally {
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
    //modify api's ends





    @PostMapping(value= "/modifyserviceinterfacemapping", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${ServiceProductInterfaceMappingController.modifyServiceInterfaceMapping.name}", description = "${ServiceProductInterfaceMappingController.modifyServiceInterfaceMapping.desc}",
            responses = {
                    @ApiResponse(responseCode = Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ModifyServiceInterfaceMappingResponseVO.class))) }

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
    public ModifyServiceInterfaceMappingResponseVO modifyServiceInterfaceMapping(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                                                                 HttpServletResponse response1, HttpServletRequest httpServletRequest,
                                                                                 @Parameter(description = "Modify service product interface mapping RequestVO", name = "ModifyServiceInterfaceMappingRequestVO", required = true) @RequestBody ModifyServiceInterfaceMappingRequestVO requestVO
    ) throws Exception {

        final String methodName = "modifyServiceInterfaceMapping";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

        UserVO userVO = null;
        UserDAO userDAO = null;

        ModifyServiceInterfaceMappingResponseVO response = new ModifyServiceInterfaceMappingResponseVO();

        try{
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();

            OAuthUser oAuthUser = new OAuthUser();
            oAuthUser.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response1);

            userVO = new UserVO();
            userDAO = new UserDAO();
            userVO  = userDAO.loadUsersDetails(con, oAuthUser.getData().getMsisdn());

            response = serviceProductInterfaceMappingServiceI.modifyServiceInterfaceMapping(headers,httpServletRequest,response1,con,mcomCon,locale,userVO,response,requestVO);
        }
        catch (BTSLBaseException be) {
            log.error(methodName, "Exception:e=" + be);
            log.errorTrace(methodName, be);
            if (!BTSLUtil.isNullString(be.getMessage())) {
                String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
                response.setMessageCode(be.getMessage());
                response.setMessage(msg);
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                response1.setStatus(HttpStatus.SC_BAD_REQUEST);
            }

        }
        catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus((HttpStatus.SC_BAD_REQUEST));
            String resmsg = RestAPIStringParser.getMessage(
                    new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                    PretupsErrorCodesI.PRODUT_SERVICE_INTERFACE_MAPPING_MODIFICATION_FAIL, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.PRODUT_SERVICE_INTERFACE_MAPPING_MODIFICATION_FAIL);
            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
        }
        finally{
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














    @PostMapping(value= "/deleteserviceinterfacemapping", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    @Operation(summary = "${ServiceProductInterfaceMappingController.deleteServiceInterfaceMapping.name}", description = "${ServiceProductInterfaceMappingController.deleteServiceInterfaceMapping.desc}",
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

    public BaseResponse deleteServiceInterfaceMapping(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
                                                      HttpServletResponse response1, HttpServletRequest httpServletRequest,
                                                      @Parameter(description = "Delete service product interface mapping RequestVO", name = "DeleteServiceInterfaceMappingRequestVO", required = true) @RequestBody DeleteServiceInterfaceMappingRequestVO requestVO
    ) throws Exception {

        final String methodName = "deleteServiceInterfaceMapping";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

        UserVO userVO = null;
        UserDAO userDAO = null;

        BaseResponse response = new BaseResponse();

        try{
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();

            OAuthUser oAuthUser = new OAuthUser();
            oAuthUser.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response1);

            userVO = new UserVO();
            userDAO = new UserDAO();
            userVO  = userDAO.loadUsersDetails(con, oAuthUser.getData().getMsisdn());

            response = serviceProductInterfaceMappingServiceI.deleteServiceInterfaceMapping(headers,httpServletRequest,response1,con,mcomCon,locale,userVO,response,requestVO);
        }
        catch (BTSLBaseException be) {
            log.error(methodName, "Exception:e=" + be);
            log.errorTrace(methodName, be);
            if (!BTSLUtil.isNullString(be.getMessage())) {
                String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
                response.setMessageCode(be.getMessage());
                response.setMessage(msg);
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                response1.setStatus(HttpStatus.SC_BAD_REQUEST);
            }

        }
        catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus((HttpStatus.SC_BAD_REQUEST));
            String resmsg = RestAPIStringParser.getMessage(
                    new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                    PretupsErrorCodesI.PRODUT_SERVICE_INTERFACE_MAPPING_DELETION_FAIL, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.PRODUT_SERVICE_INTERFACE_MAPPING_DELETION_FAIL);
            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
        }
        finally{
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



}
